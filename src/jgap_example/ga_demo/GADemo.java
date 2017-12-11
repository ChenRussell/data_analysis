package jgap_example.ga_demo;

import java.io.File;
import java.io.FileNotFoundException;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.UnsupportedRepresentationException;
import org.jgap.data.DataTreeBuilder;
import org.jgap.data.IDataCreators;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.xml.XMLDocumentBuilder;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;

public class GADemo {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new DefaultConfiguration();
		conf.setPreservFittestIndividual(true);
//		FitnessFunction myfunction = new GADemoFunctionFitness1();
//		conf.setFitnessFunction(myfunction);
//		Gene[] sampleGene = new Gene[2];
//		// sampleGene[0]=new IntegerGene(conf,0,9999999);
//		// sampleGene[1]=new IntegerGene(conf,0,9999999);
//		sampleGene[0] = new DoubleGene(conf, 0, 9);
//		sampleGene[1] = new DoubleGene(conf, 0, 9);
		
		/**
		 * 动态设置基因个数
		 */
		Gene[] sampleGene = new Gene[Integer.parseInt(args[0])];
		for (int i = 0; i < sampleGene.length; i++) {
			sampleGene[i] = new DoubleGene(conf,0.5,2);
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
//		System.out.println(GADemoFunctionFitness.getValueAtGene(bestSolutionSoFar, 0));
//		System.out.println(GADemoFunctionFitness.getValueAtGene(bestSolutionSoFar, 1));
		/**
		 * 循环打印基因值
		 */
		for (int i = 0; i < sampleGene.length; i++) {
//			System.out.println(GADemoFunctionFitness.getValueAtGene(bestSolutionSoFar, i));
		}
		// System.out.println(bestSolutionSoFar.getFitnessValue()-1000);
		System.out.println(Integer.MAX_VALUE/2 - bestSolutionSoFar.getFitnessValue());
		// System.out.println(bestSolutionSoFar.getFitnessValueDirectly());
		// DataTreeBuilder builder = DataTreeBuilder.getInstance();
		// IDataCreators doc2=builder.representGenotypeAsDocument(popution);
		// XMLDocumentBuilder docbuilder = new XMLDocumentBuilder();
		// Document xmlDoc = (Document) docbuilder.buildDocument(doc2);
		// XMLManager.writeFile(xmlDoc, new File("GADEMO.xml"));
		//

	}

}
