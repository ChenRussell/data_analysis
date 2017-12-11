package jgap_example.ga_demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class GADemoFunctionFitness1 extends FitnessFunction {

	private double value;
	private int flag;
	private List<Double> gaList;
	private int gennum;
	private Map<String, Integer> idmap;
	private List<CondExpress> condlist;

	public static final int MAX_BOUND = 4000;

	public GADemoFunctionFitness1(int gennum, Map<String, Integer> idmap, List<CondExpress> condlist) {
		// TODO Auto-generated constructor stub
		this.gennum = gennum;
		this.idmap = idmap;
		this.condlist = condlist;
	}

	/**
	 * 没有目标函数时，适应度只是一个值，越大代表这个染色体越好，该值无实际意义，根据需要变更适应度的值
	 * 有目标函数时，如果求最小值，需要将其变换成最大值的形式，如 Integer.MAX_VALUE/2 - fitness;
	 */
	@Override
	protected double evaluate(IChromosome a_subject) {
		// TODO Auto-generated method stub

		double fitness = 0;
		// 对约束条件遍历
		for (int i = 0; i < condlist.size(); i++) {

			double sum1 = 0;
			double sum2 = 0;

			for (int j = 0; j < condlist.get(i).getValueList().size() - condlist.get(i).getFlag(); j++) {
				sum1 += (Double) a_subject.getGene(idmap.get(condlist.get(i).getValueList().get(j).getId())).getAllele()
						* condlist.get(i).getValueList().get(j).getValue();
			}

			for (int k = condlist.get(i).getFlag(); k < condlist.get(i).getValueList().size(); k++) {
				sum2 += (Double) a_subject.getGene(idmap.get(condlist.get(i).getValueList().get(k).getId())).getAllele()
						* condlist.get(i).getValueList().get(k).getValue();
			}

			double xx = sum1 - sum2;
			fitness += changeDifferenceBonus(MAX_BOUND / 2, xx - condlist.get(i).getValue());

			// double fitness = 0;
			// for (int i = 0; i < a_subject.getGenes().length; i++) {
			// fitness += Math.abs((Double) a_subject.getGene(i).getAllele() - 1);
			// }

			// return (1000-fitness);

		}
		return Math.max(1.0d, fitness);
		// double sum1 = 0;
		// double sum2 = 0;
		// double fitness = 0;
		// for (int i = 0; i < a_subject.size() - flag; i++) {
		// sum1 += (Double) a_subject.getGene(i).getAllele() * gaList.get(i);
		// }
		//
		// for (int i = flag; i < a_subject.size(); i++) {
		// sum2 += (Double) a_subject.getGene(i).getAllele() * gaList.get(i);
		// }
		//
		// double xx = sum1 - sum2;
		// fitness += changeDifferenceBonus(MAX_BOUND / 2, xx - value);
		//
		// // double fitness = 0;
		// // for (int i = 0; i < a_subject.getGenes().length; i++) {
		// // fitness += Math.abs((Double) a_subject.getGene(i).getAllele() - 1);
		// // }
		//
		// // return (1000-fitness);
		// return Math.max(1.0d, fitness);
	}

	public double changeDifferenceBonus(double a_maxFitness, double a_changeDifference) {
		if (a_changeDifference < 0) {
			return 0.0d;
		} else {
			// we arbitrarily work with half of the maximum fitness as basis for non-
			// optimal solutions (concerning change difference)
			if (a_changeDifference * a_changeDifference >= a_maxFitness / 2) {
				/**
				 * 差别太大与不满足条件等价!!! -------修改为返回100的适应度,而不是0,与其区分
				 */
//				System.out.println("a_changeDifference * a_changeDifference: "+a_changeDifference * a_changeDifference);
				return 100.0d;
			} else {
				return a_maxFitness / 2 - a_changeDifference * a_changeDifference; // 关键代码，当满足大于条件时，越接近，适应度越大
			}
		}
	}

	public static double getValueAtGene(IChromosome a_potentialSolution, int a_position) {
		Double value = (Double) a_potentialSolution.getGene(a_position).getAllele();
		return value.doubleValue();
	}
}