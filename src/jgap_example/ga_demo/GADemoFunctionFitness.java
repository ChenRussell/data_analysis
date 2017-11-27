package jgap_example.ga_demo;

import java.util.List;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class GADemoFunctionFitness extends FitnessFunction {

//	private double value;
//	private int flag;
//	private List<Double> gaList;
//	
//	public GADemoFunctionFitness(double value, int flag, List<Double> gaList) {
//		// TODO Auto-generated constructor stub
//		this.value = value;
//		this.flag = flag;
//		this.gaList = gaList;
//	}
	@Override
	protected double evaluate(IChromosome a_subject) {
		// TODO Auto-generated method stub

		double fitness = 0;
		for (int i = 0; i < a_subject.getGenes().length; i++) {
			fitness += Math.abs((Double) a_subject.getGene(i).getAllele() - 1);
		}

		// return (1000-fitness);
		return Integer.MAX_VALUE / 2 - fitness;
	}

	public static double getValueAtGene(IChromosome a_potentialSolution, int a_position) {
		Double value = (Double) a_potentialSolution.getGene(a_position).getAllele();
		return value.doubleValue();
	}
}