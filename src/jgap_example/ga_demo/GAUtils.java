package jgap_example.ga_demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.UnsupportedRepresentationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.xml.XMLManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;

import jgap_example.MinimizingMakeChangeFitnessFunction;

public class GAUtils {

	private static JdbcTemplate jdbcTemplate;

	private static final String driverClassName = "com.mysql.jdbc.Driver";
	private static final String url = "jdbc:mysql://localhost:3306/jxgl?useUnicode=true&characterEncoding=UTF-8";
	private static final String dbUser = "root";
	private static final String dbPassword = "123456";

	private static double value; // 存储不需要调整的其余项的和的两项的差
	private static List<Double> gaList; // 存储需要调整的项的和
	private static int flag; // gaList第一项的个数

	public static JdbcTemplate getJdbcTemplate() {
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPassword);

		// 想要获取jdbcTemplate对象，只需要获取一个dataSource
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		return jdbcTemplate;
	}

	public static List<Map<String, Object>> query(String str) {

		String sql = "select dlmc,xs,sum(xs*sl) as sum from jxgl.ksxlfmx where hszmc=? and sl>0 group by dlmc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, str);
		return list;
	}

	public static void previousMethod() {
		jdbcTemplate = getJdbcTemplate();

		String ks1 = "妇科护理站";
		String ks2 = "介入科护理站";
		List<Map<String, Object>> list = GAUtils.query(ks1); // 查询某个科室对应的检查项目的信息，group by dlmc
		List<Map<String, Object>> list2 = GAUtils.query(ks2);

		List<String> dlmcList1 = new ArrayList<String>(); // 存储某个科室对应的所有检查项目,无重复的
		List<String> dlmcList2 = new ArrayList<String>();
		for (Map<String, Object> map : list) {
			dlmcList1.add((String) map.get("dlmc"));
		}
		for (Map<String, Object> map : list2) {
			dlmcList2.add((String) map.get("dlmc"));
		}
		System.out.println(dlmcList1);
		System.out.println(dlmcList2);

		// 找要改变的系数
		// int index1 = (int) (Math.random()*dlmcList1.size());
		// int index2 = (int) (Math.random()*dlmcList2.size());
		// System.out.println(dlmcList1.get(index1));
		// System.out.println(dlmcList1.get(index2));
		// System.out.println(dlmcList2.contains(dlmcList1.get(index1)));
		// System.out.println(dlmcList2.contains(dlmcList1.get(index2)));

		String sql = "select dlmc,xs,sl from jxgl.ksxlfmx where hszmc=? and sl>0";
		List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql, ks1); // 查询某个科室对应的检查项目的详细信息，没有group by
		List<Map<String, Object>> list4 = jdbcTemplate.queryForList(sql, ks2);

		String dlmc1 = "";
		String dlmc2 = "";
		// 从两个list中选择两个不同的检查项目
		while (true) {
			int index1 = (int) (Math.random() * dlmcList1.size());
			int index2 = (int) (Math.random() * dlmcList2.size());
			dlmc1 = dlmcList1.get(index1);
			dlmc2 = dlmcList2.get(index2);
			// double sum1 = 0;
			// double sum2 = 0;
			// for (Map<String, Object> map : list) {
			// if (((String) map.get("dlmc")).equals(dlmc1)) {
			// sum1 = (Double) map.get("sum");
			// }
			// }
			// for (Map<String, Object> map : list2) {
			// if (((String) map.get("dlmc")).equals(dlmc2)) {
			// sum2 = (Double) map.get("sum");
			// }
			// }
			// 不同的项目
			if (!(dlmc1.equals(dlmc2))) {
				break;
			}
		}
		System.out.println("dlmc1,dlmc2: " + dlmc1 + " " + dlmc2);
		Map<String, Double> map = new HashMap<String, Double>(); // 存储重新计算出的和
		Map<String, Double> map2 = new HashMap<String, Double>();
		double sum_dlmc1 = 0.0;
		double sum_dlmc2 = 0.0;
		// 对同个dlmc执行求和操作
		for (Map<String, Object> m : list3) {
			// 改变其中某个的系数
			if (((String) (m.get("dlmc"))).equals(dlmc1)) {
				// m.replace("xs", (Double)m.get("xs")*11);
				sum_dlmc1 += (Double) m.get("xs") * (Double) m.get("sl");
			} else if (map.containsKey(m.get("dlmc"))) {
				map.put((String) m.get("dlmc"), map.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		for (Map<String, Object> m : list4) {
			// 改变其中某个的系数
			if (((String) (m.get("dlmc"))).equals(dlmc2)) {
				// m.replace("xs", (Double)m.get("xs")*11);
				sum_dlmc2 += (Double) m.get("xs") * (Double) m.get("sl");
			} else if (map2.containsKey(m.get("dlmc"))) {
				map2.put((String) m.get("dlmc"), map2.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map2.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		System.out.println("dlmc_sum:  " + sum_dlmc1 + " , " + sum_dlmc2);
		System.out.println(map);
		System.out.println(map2);
		System.out.println("" + map.size());
		System.out.println(map2.size());
		double sum = 0;
		double sum2 = 0;
		Set<Entry<String, Double>> entrySet = map.entrySet();
		Set<Entry<String, Double>> entrySet2 = map2.entrySet();
		for (Entry<String, Double> entry : entrySet) {
			sum += entry.getValue();
		}
		for (Entry<String, Double> entry : entrySet2) {
			sum2 += entry.getValue();
		}
		System.out.println((double) Math.round(sum * 100) / 100);
		System.out.println((double) Math.round(sum2 * 100) / 100);

		// ga(sum_dlmc1, sum_dlmc2, sum2 - sum);
		// w = x - y
		// double w = (sum2-sum)/(sum_dlmc1-sum_dlmc2);
		// System.out.println(w);
	}

	public static void newMethod(String ks1, String ks2) {
		List<Map<String, Object>> list1 = GAUtils.query(ks1); // 查询某个科室对应的检查项目的信息，group by dlmc
		List<Map<String, Object>> list2 = GAUtils.query(ks2);

		List<String> dlmcList1 = new ArrayList<String>(); // 存储某个科室对应的所有检查项目,无重复的
		List<String> dlmcList2 = new ArrayList<String>();
		for (Map<String, Object> map : list1) {
			dlmcList1.add((String) map.get("dlmc"));
		}
		for (Map<String, Object> map : list2) {
			dlmcList2.add((String) map.get("dlmc"));
		}
		System.out.println(dlmcList1);
		System.out.println(dlmcList1.size());
		System.out.println(dlmcList2);
		System.out.println(dlmcList2.size());
		List<String> unique_dlmc = new ArrayList<String>();
		for (String dlmc : dlmcList1) {
			if (!dlmcList2.contains(dlmc)) {
				unique_dlmc.add(dlmc);
			}
		}
		for (String dlmc : dlmcList2) {
			if (!dlmcList1.contains(dlmc)) {
				unique_dlmc.add(dlmc);
			}
		}
		System.out.println(unique_dlmc);
		System.out.println("unique_dlmc.size(): " + unique_dlmc.size());

		/**
		 * 查询非聚合的数据，重新求和
		 */
		String sql = "select dlmc,xs,sl from jxgl.ksxlfmx where hszmc=? and sl>0";
		List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql, ks1); // 查询某个科室对应的检查项目的详细信息，没有group by
		List<Map<String, Object>> list4 = jdbcTemplate.queryForList(sql, ks2);

		Map<String, Double> map1 = new HashMap<String, Double>(); // 存储重新计算出的和
		Map<String, Double> map2 = new HashMap<String, Double>();
		Map<String, Double> map1_unique = new HashMap<String, Double>(); // 存储非重复的dlmc
		Map<String, Double> map2_unique = new HashMap<String, Double>();

		// 对同个dlmc执行求和操作
		for (Map<String, Object> m : list3) {
			// 把两个科室不重复的dlmc的求和放到一个单独的map中
			if (unique_dlmc.contains(m.get("dlmc"))) {
				if (map1_unique.containsKey(m.get("dlmc"))) {
					map1_unique.put((String) m.get("dlmc"),
							map1_unique.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
				} else {
					map1_unique.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
				}
			} else if (map1.containsKey(m.get("dlmc"))) {
				map1.put((String) m.get("dlmc"), map1.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map1.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		for (Map<String, Object> m : list4) {
			// 改变其中某个的系数
			if (unique_dlmc.contains(m.get("dlmc"))) {
				if (map2_unique.containsKey(m.get("dlmc"))) {
					map2_unique.put((String) m.get("dlmc"),
							map2_unique.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
				} else {
					map2_unique.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
				}
			} else if (map2.containsKey(m.get("dlmc"))) {
				map2.put((String) m.get("dlmc"), map2.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map2.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		System.out.println("map1.size(): " + map1.size());
		System.out.println("map1_unique.size(): " + map1_unique.size());
		System.out.println("map2.size(): " + map2.size());
		System.out.println("map2_unique.size(): " + map2_unique.size());
		System.out.println("**********map1_unique：********");
		System.out.println(map1_unique);
		System.out.println("**********map2_unique：********");
		System.out.println(map2_unique);

		double sum1 = 0;
		double sum2 = 0;
		Set<Entry<String, Double>> entrySet = map1.entrySet();
		Set<Entry<String, Double>> entrySet2 = map2.entrySet();
		for (Entry<String, Double> entry : entrySet) {
			sum1 += entry.getValue();
		}
		for (Entry<String, Double> entry : entrySet2) {
			sum2 += entry.getValue();
		}
		System.out.println("sum1,sum2: "+sum1 + "," + sum2);
		double sum3 = 0;
		double sum4 = 0;
		List<Double> list_unique = new ArrayList<Double>(); // 存储需要调整系数的项的和
		Set<Entry<String, Double>> entrySet3 = map1_unique.entrySet();
		Set<Entry<String, Double>> entrySet4 = map2_unique.entrySet();
		for (Entry<String, Double> entry : entrySet3) {
			sum3 += entry.getValue();
			list_unique.add(entry.getValue());
		}
		flag = list_unique.size();
		for (Entry<String, Double> entry : entrySet4) {
			sum4 += entry.getValue();
			list_unique.add(entry.getValue());
		}
		System.out.println("sum3,sum4: "+sum3 + "," + sum4);
		System.out.println("list_unique: "+list_unique);

		value = sum2 - sum1;
		gaList = list_unique;
	}

	public static void geneticAlgorithem(double value, int flag, List<Double> gaList) throws Exception {
		Configuration conf = new DefaultConfiguration();
		conf.setPreservFittestIndividual(true);
		FitnessFunction myfunction = new GADemoFunctionFitness2(value, flag, gaList);
		conf.setFitnessFunction(myfunction);

		/**
		 * 动态设置基因个数
		 */
		Gene[] sampleGene = new Gene[gaList.size()];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new DoubleGene(conf, 0.5, 5);	//定义基因的取值范围，即系数的取值范围
		}

		IChromosome samplechromosome = new Chromosome(conf, sampleGene);
		conf.setSampleChromosome(samplechromosome);
		conf.setPopulationSize(80);
		Genotype popution;
		try {
			Document doc = XMLManager.readFile(new File("GADemo.xml"));
			popution = XMLManager.getGenotypeFromDocument(conf, doc);
		} catch (UnsupportedRepresentationException uex) {
			popution = Genotype.randomInitialGenotype(conf);
		} catch (FileNotFoundException e) {
			// 不包含文件就通过这种方式初始化种群
			popution = Genotype.randomInitialGenotype(conf);
		}
		long starttime = System.currentTimeMillis();
		for (int i = 0; i < 50; i++) {
			popution.evolve();
		}
		long endtime = System.currentTimeMillis();
		System.out.println("the total evolve time:" + (endtime - starttime));
		IChromosome bestSolutionSoFar = popution.getFittestChromosome();

		// System.out.println(Integer.MAX_VALUE/2 -
		// bestSolutionSoFar.getFitnessValue());
		double v1 = bestSolutionSoFar.getFitnessValue();
		System.out.println("The best solution has a fitness value of " + bestSolutionSoFar.getFitnessValue());
//		bestSolutionSoFar.setFitnessValueDirectly(-1);
		
		if(v1!=1) {
			System.out.println("It contains the following: ");
			for (int i = 0; i < sampleGene.length; i++) {
				System.out.println(GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, i));
			}
			double sum1 = 0;
			double sum2 = 0;
			for (int i = 0; i < sampleGene.length - flag; i++) {
				sum1 += GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, i) * gaList.get(i);
			}
			for (int i = flag; i < sampleGene.length; i++) {
				sum2 += GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, i) * gaList.get(i);
			}
			System.out.println(sum1-sum2+"\t"+value);
		}
		else {
			System.out.println("无法满足约束条件，请改变基因的取值范围！");
		}
	}

	public static void main(String[] args) throws Exception {
		jdbcTemplate = getJdbcTemplate();
//		String ks1 = "产科护理站";
		String ks1 = "眼科三护理站";
//		String ks1 = "妇科护理站";
//		String ks2 = "介入科护理站";
		String ks2 = "眼科一护理站";
		newMethod(ks1, ks2);
		System.out.println("value: "+value);
		System.out.println("gaList: "+gaList);
		System.out.println("gaList.size(): "+gaList.size());
		System.out.println("flag: "+flag);
		System.out.println("*******************************\n");
		geneticAlgorithem(value, flag, gaList);
	}
}
