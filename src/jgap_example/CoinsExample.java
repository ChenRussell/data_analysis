package jgap_example;

import java.io.*;
import java.awt.image.*;
import org.jgap.*;
import org.jgap.impl.*;
import org.jgap.audit.*;

/**
 * Same logic as in MinimizingMakeChange except that we are using the new
 * audit capabilities provided by JGAP 2.2
 *
 * @author Klaus Meffert
 * @since 2.2
 */
public class CoinsExample {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.25 $";

  /**
   * The total number of times we'll let the population evolve.
   */
  private static final int MAX_ALLOWED_EVOLUTIONS = 80;

  /**
   * Executes the genetic algorithm to determine the minimum number of
   * coins necessary to make up the given target amount of change. The
   * solution will then be written to System.out.
   *
   * @param a_targetChangeAmount the target amount of change for which this
   * method is attempting to produce the minimum number of coins
   * @throws Exception
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   */
  public static void makeChangeForAmount(int a_targetChangeAmount)
      throws Exception {
    // Start with a DefaultConfiguration, which comes setup with the
    // most common settings.
    // -------------------------------------------------------------
    Configuration conf = new DefaultConfiguration();
    conf.setPreservFittestIndividual(true);//设置是否保存适应度最大的个体
    FitnessFunction myFunc =
        new CoinsExampleFitnessFunction(a_targetChangeAmount);
    conf.setFitnessFunction(myFunc);//设置适应度对象
    // Now we need to tell the Configuration object how we want our
    // Chromosomes to be setup. We do that by actually creating a
    // sample Chromosome and then setting it on the Configuration
    // object. As mentioned earlier, we want our Chromosomes to each
    // have four genes, one for each of the coin types. We want the
    // values (alleles) of those genes to be integers, which represent
    // how many coins of that type we have. We therefore use the
    // IntegerGene class to represent each of the genes. That class
    // also lets us specify a lower and upper bound, which we set
    // to sensible values for each coin type.
    // --------------------------------------------------------------
    Gene[] sampleGenes = new Gene[4];
    sampleGenes[0] = new IntegerGene(conf, 0, 3 * 10); // Quarters,注意并不指定对等基因值
    sampleGenes[1] = new IntegerGene(conf, 0, 2 * 10); // Dimes
    sampleGenes[2] = new IntegerGene(conf, 0, 1 * 10); // Nickels
    sampleGenes[3] = new IntegerGene(conf, 0, 4 * 10); // Pennies
    Chromosome sampleChromosome = new Chromosome(conf, sampleGenes);
    conf.setSampleChromosome(sampleChromosome);
    // Finally, we need to tell the Configuration object how many
    // Chromosomes we want in our population. The more Chromosomes,
    // the larger number of potential solutions (which is good for
    // finding the answer), but the longer it will take to evolve
    // the population (which could be seen as bad).
    // ------------------------------------------------------------
    conf.setPopulationSize(50);
    // Added here for demonstrating purposes is a permuting configuration.
    // It allows for evaluating which configuration could work best for
    // the given problem.
    // -------------------------------------------------------------------
    PermutingConfiguration pconf = new PermutingConfiguration(conf);
    pconf.addGeneticOperatorSlot(new CrossoverOperator(conf));//设置交叉算子
    pconf.addGeneticOperatorSlot(new MutationOperator(conf));//设置变异算子
    pconf.addNaturalSelectorSlot(new BestChromosomesSelector(conf));//设置选择器
    pconf.addNaturalSelectorSlot(new WeightedRouletteSelector(conf));//设置多个不同的选择器
    pconf.addRandomGeneratorSlot(new StockRandomGenerator());//设置随机生成器
    RandomGeneratorForTesting rn = new RandomGeneratorForTesting();
    rn.setNextDouble(0.7d);
    rn.setNextInt(2);
    pconf.addRandomGeneratorSlot(rn);//设置多个不同的随机生成器
    pconf.addRandomGeneratorSlot(new GaussianRandomGenerator());//设置多个不同的随机生成器
    pconf.addFitnessFunctionSlot(new CoinsExampleFitnessFunction(
        a_targetChangeAmount));//设置适应度对象
    Evaluator eval = new Evaluator(pconf);
    /**@todo class Evaluator:
     * input:
     *   + PermutingConfiguration
     *   + Number of evaluation runs pers config (to turn off randomness
     *     as much as possible)
     *   + output facility (data container)
     *   + optional: event subscribers
     * output:
     *   + averaged curve of fitness value thru all generations
     *   + best fitness value accomplished
     *   + average number of performance improvements for all generations
     */
    int permutation = 0;
    while (eval.hasNext()) {
      Genotype population = Genotype.randomInitialGenotype(eval.next());
      for (int run = 0; run < 10; run++) {
        for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
          population.evolve();//进行迭代进化操作
          //获取最大适应度值
          double fitness = population.getFittestChromosome().getFitnessValue();
          if (i % 3 == 0) {
            String s = String.valueOf(i);
            eval.setValue(permutation, run, fitness, "" + permutation, s);//保存当前进化信息
            eval.storeGenotype(permutation, run, population);//保存当前种群信息
          }
        }
      }
      // Display the best solution we found.
      // -----------------------------------
      IChromosome bestSolutionSoFar = population.getFittestChromosome();
      System.out.println("The best solution has a fitness value of " +
                         bestSolutionSoFar.getFitnessValue());
      System.out.println("It contained the following: ");
      System.out.println("\t" +
                         CoinsExampleFitnessFunction.
                         getNumberOfCoinsAtGene(
                             bestSolutionSoFar, 0) + " quarters.");
      System.out.println("\t" +
                         CoinsExampleFitnessFunction.
                         getNumberOfCoinsAtGene(
                             bestSolutionSoFar, 1) + " dimes.");
      System.out.println("\t" +
                         CoinsExampleFitnessFunction.
                         getNumberOfCoinsAtGene(
                             bestSolutionSoFar, 2) + " nickels.");
      System.out.println("\t" +
                         CoinsExampleFitnessFunction.
                         getNumberOfCoinsAtGene(
                             bestSolutionSoFar, 3) + " pennies.");
      System.out.println("For a total of " +
                         CoinsExampleFitnessFunction.amountOfChange(
                             bestSolutionSoFar) + " cents in " +
                         CoinsExampleFitnessFunction.
                         getTotalNumberOfCoins(
                             bestSolutionSoFar) + " coins.");
      permutation++;
    }
    // Create chart: fitness values average over all permutations.
    // -----------------------------------------------------------

    // Construct JFreeChart Dataset.
    // -----------------------------
  }

  public static void main(String[] args) {
    if (/*args.length != 1*/false) {
      System.out.println("Syntax: CoinsExample <amount>");
    }
    else {
      try {
        //int amount = Integer.parseInt(args[0]);
        int amount =100;
        
        if (amount < 1 ||
            amount >= CoinsExampleFitnessFunction.MAX_BOUND) {
          System.out.println("The <amount> argument must be between 1 and "
                             +
                             (CoinsExampleFitnessFunction.MAX_BOUND - 1)
                             + ".");
        }
        else {
          try {
            makeChangeForAmount(amount);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } catch (NumberFormatException e) {
        System.out.println(
            "The <amount> argument must be a valid integer value");
      }
    }
  }
}
