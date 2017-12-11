package jgap_example.ga_demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.dbcp.BasicDataSource;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.UnsupportedRepresentationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.xml.XMLManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;

public class GAUtils2 {

	private static JdbcTemplate jdbcTemplate;

	private static final String driverClassName = "com.mysql.jdbc.Driver";
	private static final String url = "jdbc:mysql://localhost:3306/jxgl?useUnicode=true&characterEncoding=UTF-8";
	private static final String dbUser = "root";
	private static final String dbPassword = "123456";

	private static double value; // 存储不需要调整的其余项的和的两项的差
	private static List<Double> gaList; // 存储需要调整的项的和
	private static List<CondGene> gaValueList; // 存储需要调整的项的和,新增的

	private static Map<String, Double> gaMap = new HashMap<String, Double>(); // 存储遗传算法调整后的权值,乘以原始系数得到调整后的系数
	private static Map<String, Double> gaMap_adjust; // 存储调整后的系数值
	private static int flag; // gaList第一项的个数

	private static List<Map<String, Object>> list3; // 存储科室一对应的完整信息，用来重新计算求和
	private static List<Map<String, Object>> list4;

	private static int gennum; // 基因个数

	private static Map<String, Integer> idmap = new HashMap<String, Integer>(); // 存储id和基因位置的映射
	private static Map<String, Double> weight_map = new HashMap<String, Double>(); // 存储id和基因位置的映射

	private static List<CondExpress> condlist = new ArrayList<CondExpress>(); // 存储多个约束条件对应的数据

	private static int counter = 0; // 基因个数计数器

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
		List<Map<String, Object>> list = GAUtils2.query(ks1); // 查询某个科室对应的检查项目的信息，group by dlmc
		List<Map<String, Object>> list2 = GAUtils2.query(ks2);

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
		List<Map<String, Object>> list1 = GAUtils2.query(ks1); // 查询某个科室对应的检查项目的信息，group by dlmc
		List<Map<String, Object>> list2 = GAUtils2.query(ks2);

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

		Map<String, Double> map1 = new HashMap<String, Double>(); // 存储重新计算出的和
		Map<String, Double> map2 = new HashMap<String, Double>();
		Map<String, Double> map1_unique = new HashMap<String, Double>(); // 存储两个科室对应的非重复的dlmc
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
			// 计算两个科室中不重复出现的项目所对应的和
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
		System.out.println("sum1,sum2: " + sum1 + "," + sum2);
		double sum3 = 0;
		double sum4 = 0;
		List<Double> list_unique = new ArrayList<Double>(); // 存储需要调整系数的项的和
		Map<String, Double> map_unique = new LinkedHashMap<String, Double>(); // 存储需要调整系数的项的和,必须保证按顺序存放
		Set<Entry<String, Double>> entrySet3 = map1_unique.entrySet();
		Set<Entry<String, Double>> entrySet4 = map2_unique.entrySet();
		for (Entry<String, Double> entry : entrySet3) {
			sum3 += entry.getValue();
			list_unique.add(entry.getValue());
			map_unique.put(entry.getKey(), entry.getValue());
		}
		flag = list_unique.size();
		for (Entry<String, Double> entry : entrySet4) {
			sum4 += entry.getValue();
			list_unique.add(entry.getValue());
			map_unique.put(entry.getKey(), entry.getValue());
		}
		System.out.println("sum3,sum4: " + sum3 + "," + sum4);
		System.out.println("list_unique: " + list_unique);
		System.out.println("map_unique: " + map_unique);

		value = sum2 - sum1;
		gaList = list_unique;
		gaMap = map_unique;
	}

	public static void method_v3(String ks1, String ks2) {
		// 初始化gaValueList
		gaList = new ArrayList<Double>();
		gaValueList = new ArrayList<CondGene>();

		List<Map<String, Object>> list1 = GAUtils2.query(ks1); // 查询某个科室对应的检查项目的信息，group by dlmc
		List<Map<String, Object>> list2 = GAUtils2.query(ks2);

		List<String> dlmcList1 = new ArrayList<String>(); // 存储某个科室对应的所有检查项目,无重复的
		List<String> dlmcList2 = new ArrayList<String>();

		double sum_ks1 = 0, sum_ks2 = 0;
		for (Map<String, Object> map : list1) {
			dlmcList1.add((String) map.get("dlmc"));
			sum_ks1 += (Double) map.get("sum");
		}
		for (Map<String, Object> map : list2) {
			dlmcList2.add((String) map.get("dlmc"));
			sum_ks2 += (Double) map.get("sum");
		}
		System.out.println("sum_ks1: " + sum_ks1 + "\t sum_ks2: " + sum_ks2);
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

		// 遍历unique_dlmc,为idmap赋值
		for (String string : unique_dlmc) {
			if (!idmap.containsKey(string)) {
				idmap.put(string, counter);
				counter++; // 计数器加1
				weight_map.put(string, 0.0);	// 用来存储遗传算法计算出来的值
			}
		}
		System.out.println("idmap: " + idmap);
		System.out.println("idmap.size() : " + idmap.size());
		System.out.println("counter: " + counter);

		CondExpress condExpress = new CondExpress();
		/**
		 * 获取不重复的科室对应的dlmc所对应的和
		 */
		double com_sum_ks1 = 0, com_sum_ks2 = 0; // 定义共同的dlmc的和
		for (Map<String, Object> map : list1) {
			boolean tag = true;
			for (String dlmc : unique_dlmc) {
				if (((String) map.get("dlmc")).equals(dlmc)) {
					tag = false;

					CondGene condGene = new CondGene();
					condGene.setId(dlmc);
					condGene.setValue((Double) map.get("sum"));
					gaValueList.add(condGene);

					gaList.add((Double) map.get("sum"));
				}
			}
			if (tag) {
				com_sum_ks1 += (Double) map.get("sum");
			}
		}
		flag = gaList.size();
		// condExpress.flag = gaList.size();
		condExpress.setFlag(gaValueList.size());

		for (Map<String, Object> map : list2) {
			boolean tag = true;
			for (String dlmc : unique_dlmc) {
				if (((String) map.get("dlmc")).equals(dlmc)) {
					tag = false;
					gaList.add((Double) map.get("sum"));

					CondGene condGene = new CondGene();
					condGene.setId(dlmc);
					condGene.setValue((Double) map.get("sum"));
					gaValueList.add(condGene);
				}
			}
			// 共同的dlmc
			if (tag) {
				com_sum_ks2 += (Double) map.get("sum");
			}
		}
		System.out.println("com_Sum_ks1: " + com_sum_ks1 + "\tcom_sum_ks2: " + com_sum_ks2);
		value = com_sum_ks2 - com_sum_ks1;
		condExpress.setValue(value);

		condExpress.setValueList(gaValueList);
		condlist.add(condExpress);
	}

	public static boolean geneticAlgorithem(int gennum, Map<String, Integer> idmap, List<CondExpress> condlist)
			throws Exception {
		Configuration conf = new DefaultConfiguration();
		conf.setPreservFittestIndividual(true);
		FitnessFunction myfunction = new GADemoFunctionFitness1(gennum, idmap, condlist);
		conf.setFitnessFunction(myfunction);

		/**
		 * 动态设置基因个数
		 */
		Gene[] sampleGene = new Gene[idmap.size()];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new DoubleGene(conf, 0.5, 3); // 定义基因的取值范围，即系数的取值范围
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
		// bestSolutionSoFar.setFitnessValueDirectly(-1);

		if (v1 > 1000) {
			System.out.println("It contains the following: ");
			// for (int i = 0; i < sampleGene.length; i++) {
			// double gene_value = GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar,
			// i);
			// System.out.println(gene_value);
			// }
			
//			Set<Entry<String, Double>> entrySet = gaMap.entrySet();
//			int count = 0;
//			for (Entry<String, Double> entry : entrySet) {
//				double gene_value = GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, count);
//				gaMap.put(entry.getKey(), gene_value);
//				count++;
//				System.out.println(gene_value);
//			}
			
			// double sum1 = 0;
			// double sum2 = 0;
			// for (int i = 0; i < sampleGene.length - flag; i++) {
			// sum1 += GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, i) *
			// gaList.get(i);
			// }
			// for (int i = flag; i < sampleGene.length; i++) {
			// sum2 += GADemoFunctionFitness2.getValueAtGene(bestSolutionSoFar, i) *
			// gaList.get(i);
			// }
			// System.out.println(sum1 - sum2 + "\t" + value);

			/**
			 * 遍历 基因
			 */
			for (int i = 0; i < condlist.size(); i++) {

				double sum_1 = 0;
				double sum_2 = 0;

				for (int j = 0; j < condlist.get(i).getValueList().size() - condlist.get(i).getFlag(); j++) {
					sum_1 += (Double) bestSolutionSoFar
							.getGene(idmap.get(condlist.get(i).getValueList().get(j).getId())).getAllele()
							* condlist.get(i).getValueList().get(j).getValue();
				}

				for (int k = condlist.get(i).getFlag(); k < condlist.get(i).getValueList().size(); k++) {
					sum_2 += (Double) bestSolutionSoFar
							.getGene(idmap.get(condlist.get(i).getValueList().get(k).getId())).getAllele()
							* condlist.get(i).getValueList().get(k).getValue();
				}
				
				double xx = sum_1 - sum_2;
				System.out.println("xx: " + xx + "\t value: " + condlist.get(i).getValue());
			}
			
			/**
			 * 对 weight_map 赋值
			 */
			Set<Entry<String,Integer>> entrySet = idmap.entrySet();
			for (Entry<String, Integer> entry : entrySet) {
				weight_map.put(entry.getKey(), (Double)bestSolutionSoFar.getGene(entry.getValue()).getAllele());
			}
			System.out.println("weight_map: "+weight_map);
			System.out.println("weight_map.size(): "+weight_map.size());

			return true;
		} else {
			System.out.println("无法满足约束条件，请改变基因的取值范围！");
			return false;
		}
	}

	public static void adjustWeight() {
		// 调整系数
		Set<Entry<String, Double>> entrySet = gaMap.entrySet();
		for (Map<String, Object> map : list3) {
			for (Entry<String, Double> entry : entrySet) {
				if (entry.getKey().equals(map.get("dlmc"))) {
					map.put("xs", (Double) map.get("xs") * entry.getValue());
				}
			}
		}
		for (Map<String, Object> map : list4) {
			for (Entry<String, Double> entry : entrySet) {
				if (entry.getKey().equals(map.get("dlmc"))) {
					map.put("xs", (Double) map.get("xs") * entry.getValue());
				}
			}
		}
		System.out.println("系数已经调整完成！");
	}

	public static void caculateSum() {
		Map<String, Double> map1 = new HashMap<String, Double>(); // 存储重新计算出的和
		Map<String, Double> map2 = new HashMap<String, Double>();

		// 对同个dlmc执行求和操作
		for (Map<String, Object> m : list3) {
			if (map1.containsKey(m.get("dlmc"))) {
				map1.put((String) m.get("dlmc"), map1.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map1.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		for (Map<String, Object> m : list4) {
			// 计算两个科室中不重复出现的项目所对应的和
			if (map2.containsKey(m.get("dlmc"))) {
				map2.put((String) m.get("dlmc"), map2.get(m.get("dlmc")) + (Double) m.get("xs") * (Double) m.get("sl"));
			} else {
				map2.put((String) m.get("dlmc"), (Double) m.get("xs") * (Double) m.get("sl"));
			}
		}
		double sum1 = 0, sum2 = 0;
		Set<Entry<String, Double>> entrySet = map1.entrySet();
		for (Entry<String, Double> entry : entrySet) {
			sum1 += entry.getValue();
		}
		Set<Entry<String, Double>> entrySet2 = map2.entrySet();
		for (Entry<String, Double> entry : entrySet2) {
			sum2 += entry.getValue();
		}
		System.out.println("sum1: " + sum1 + "\t" + "sum2: " + sum2);
	}

	public static void realAdjustWeight() {
		// 查找项目对应的系数
		String sql_query_dlmc = "select id as id, name as name,defaultvalue as value from jxgl.zbmx";
		List<Map<String, Object>> list_dlmc_info = jdbcTemplate.queryForList(sql_query_dlmc);
		System.out.println("项目系数信息: ");
		System.out.println(list_dlmc_info);
		gaMap_adjust = gaMap;
		Set<Entry<String, Double>> entrySet = gaMap_adjust.entrySet();

		for (Entry<String, Double> entry : entrySet) {
			for (Map<String, Object> map : list_dlmc_info) {
				if (entry.getKey().equals(map.get("name"))) {
					entry.setValue(entry.getValue() * (Double) map.get("value"));
					break; // 调整完后跳出内循环
				}
			}
		}
		System.out.println(gaMap_adjust); // 最后要存储的值
	}

	public static void savaData() {
		Map<String, Double> data = gaMap_adjust;
		Set<Entry<String, Double>> entrySet = data.entrySet();
		String zbid = UUID.randomUUID().toString().replace("-", "");
		// 指标id,新产生一个,或者从数据库中查一个已存在的
		int len = zbid.length();
		String sql = "insert into temp_zb values(?,?,?,?,?,?)";
		for (Entry<String, Double> entry : entrySet) {
			String id = UUID.randomUUID().toString().replace("-", "");
			String dlmc = entry.getKey();
			Double value = entry.getValue();
			String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
			String updateTime = createTime;
			// List<Object> list = new ArrayList<Object>();
			// list.add(id);
			// list.add(zbid);
			// list.add(dlmc);
			// list.add(value);
			// list.add(createTime);
			// list.add(updateTime);
			// 参数列表不能使用List
			/**
			 * Object ... 表示参数的个数是不确定的
			 */
			// jdbcTemplate.update(sql, new Object[]
			// {id,zbid,dlmc,value,createTime,updateTime});
			jdbcTemplate.update(sql, id, zbid, dlmc, value, createTime, updateTime);
			// jdbcTemplate.update("insert into temp_zb
			// values('11','22','33',4.4,'22','33')");
		}
	}

	public static void main(String[] args) throws Exception {
		jdbcTemplate = getJdbcTemplate();

//		 String ks[][] = { { "产科护理站", "介入科护理站" }, { "产科护理站", "妇科护理站" } };
		String ks[][] = { { "产科护理站", "介入科护理站" }, { "眼科一护理站", "妇科护理站" } };
		for (int i = 0; i < 2; i++) {
			String ks1 = "产科护理站";
			ks1 = ks[i][0];
			// String ks1 = "眼科三护理站";
			// String ks1 = "妇科护理站";
			String ks2 = "介入科护理站";
			ks2 = ks[i][1];
			// String ks2 = "眼科一护理站";

			/**
			 * 查询非聚合的数据，重新求和
			 */
			String sql = "select dlmc,xs,sl from jxgl.ksxlfmx where hszmc=? and sl>0";
			/**
			 * 求list3,list4的代码要提出去
			 */
			// list3 = jdbcTemplate.queryForList(sql, ks1); // 查询某个科室对应的检查项目的详细信息，没有group by
			// list4 = jdbcTemplate.queryForList(sql, ks2);

			// list3.forEach(System.out::print); jdk1.8 打印List
			// newMethod(ks1, ks2);
			method_v3(ks1, ks2);

			System.out.println("value: " + value);
			System.out.println("gaList: " + gaList);
			System.out.println("gaList.size(): " + gaList.size());
			System.out.println("flag: " + flag);
			System.out.println("*******************************\n");

		}
		boolean boo = geneticAlgorithem(idmap.size(), idmap, condlist);
		// System.out.println("gaMap: " + gaMap); // 最后用来更新系数的数据
		// System.out.println("gaMap.size(): " + gaMap.size()); // 最后用来更新系数的数据
		// if (boo) {
		// // 调整系数，并重新求和
		// // adjustWeight();
		// // caculateSum();
		// realAdjustWeight();
		// savaData();
		// }
	}
}
