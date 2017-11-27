package com.ga_mativariable;

import java.util.ArrayList;  
import java.util.Date;
import java.util.List;  
  
    
public abstract class GeneticAlgorithm {  
    private List<Chromosome> population = new ArrayList<Chromosome>();  
    /**种群数量*/
    private int popSize = 40;
    /**染色体最大长度*/
    private int geneSize;
    /**最大迭代次数*/
    private int maxIterNum = 500; 
    /**基因变异的概率*/
    private double mutationRate = 0.001;
    /**最大变异步长*/
    private int maxMutationNum = 3;  
    /**当前遗传到第几代*/  
    private int generation = 1;  
      
    private double bestScore;//最好得分  
    private double worstScore;//最坏得分  
    private double totalScore;//总得分  
    private double averageScore;//平均得分  
      
    private double x_1; //记录历史种群中最好的X值  
    private double x_2; //记录历史种群中最好的X值  
    private double y=Double.MAX_VALUE; //记录历史种群中最好的Y值  
    private int geneI;//x y所在代数  
    
    private DynamicDataWindow ddWindow;
    private long tp;
    
    public GeneticAlgorithm(int geneSize) {  
        this.geneSize = geneSize;  
    }  
      
    public void caculte() { 
    	
        //1.初始化种群  
        init(); 
        for(generation = 1; generation < maxIterNum; generation++) {  
        	//2.计算种群适应度
        	caculteScore(); 
        	System.out.println("3>验证阈值...");
            //4.种群遗传  
            evolve();
          //5.基因突变  
            mutation();
            print();  
        }  
    }  
      
    /** 
     * @Description: 输出结果 
     */  
    private void print() {  
        System.out.println("--------------------------------");  
        System.out.println("the generation is:" + generation);  
        System.out.println("the best y is:" + bestScore);  
        System.out.println("the worst fitness is:" + worstScore);  
        System.out.println("the average fitness is:" + averageScore);  
        System.out.println("the total fitness is:" + totalScore);  
        System.out.println("geneI:" + geneI + "\tx_1:" + x_1+"\tx_2:"+ x_2 + "\ty:" + (y));
     	
//        long millis=System.currentTimeMillis();
//        if (millis-tp>300) {
//        	tp=millis;
////        	ddWindow.setyMaxMin(y-10);
//     		ddWindow.addData(millis, y);
//		}
//       
//		try {
//			Thread.sleep(10L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        			
    }  
      
      
    /** 
     * @Description: 初始化种群 
     */  
    private void init() {  
    	System.out.println("1>生成初始种群...");
//    	ddWindow.setVisible(true);
    	population = new ArrayList<Chromosome>();
        for (int i = 0; i < popSize; i++) {  
            Chromosome chro = new Chromosome(geneSize);  
            population.add(chro);  
        }  
    }  
      
    /** 
     * @Description:种群进行遗传 
     */  
    private void evolve() {  
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();  
        //生成下一代种群  
        while (childPopulation.size() < popSize) {  
        	System.out.println("fuck you");
            Chromosome parent1 = getParentChromosome();  
            Chromosome parent2 = getParentChromosome();  
            List<Chromosome> children = Chromosome.genetic(parent1, parent2);  
            if (children != null) {  
                for (Chromosome chro : children) {  
                    childPopulation.add(chro);  
                }  
            }   
        }  
        System.out.println("4.2>产生子代种群...");
        //新种群替换旧种群  
        population.clear();  
        population = childPopulation;  
    }  
      
    /** 
     * @return 
     * Email: tyhj_sf@163.com   
     * @Description: 轮盘赌法选择可以遗传下一代的染色体 
     */  
    private Chromosome getParentChromosome (){  
//    	System.out.println("4.1>筛选父代种群一次...");
    	while (true) {
    		double slice = Math.random() * totalScore;  
            double sum = 0;  
            for (Chromosome chro : population) {  
                sum += chro.getScore();  
//                System.out.println("测试：sum="+sum+"  chro.getScore()="+chro.getScore());
                if (sum > slice && chro.getScore() <= averageScore) {  
                    return chro;  
                }  
            }
		}
    }  
      
    /** 
     * @Description: 计算种群适应度 
     */  
    private void caculteScore() {
    	System.out.println("2>计算种群适应度...");
    	setChromosomeScore(population.get(0));  
    	bestScore=(double)population.get(0).getScore();
    	worstScore=(double)population.get(0).getScore();
//    	bestScore=Double.MAX_VALUE;
//    	worstScore=Double.MIN_VALUE;
        totalScore = 0;  
        for (Chromosome chro : population) {  
            setChromosomeScore(chro);  
            if (chro.getScore() < bestScore) { //设置最好基因值  
                bestScore = chro.getScore();  
                if (y > bestScore) {  
                    x_1 = (double)changeX(chro,0,23)/((1<<23)-1);  
                    x_2 = (double)changeX(chro,23,46)/((1<<23)-1);  
                    y = bestScore;  
                    geneI = generation;  
                }  
            }  
            if (chro.getScore() > worstScore) { //设置最坏基因值  
                worstScore = chro.getScore();  
            }  
            totalScore += chro.getScore();  
        }  
        averageScore = totalScore / popSize;  
        //因为精度问题导致的平均值大于最好值，将平均值设置成最好值  
        averageScore = averageScore > worstScore ? worstScore : averageScore;  
    }  
      
    /** 
     * 基因突变 
     */  
    private void mutation() {  
    	System.out.println("5>基因突变...");
        for (Chromosome chro : population) {  
            if (Math.random() < mutationRate) { //发生基因突变  
                int mutationNum = (int) (Math.random() * maxMutationNum);  
                chro.mutation(mutationNum);  
            }  
        }  
    }  
      
    /** 
     * @param chro 
     * @Description: 计算并设置染色体适应度 
     */  
    private void setChromosomeScore(Chromosome chro) {  
        if (chro == null) {  
            return;  
        }  
        
        int x1 = changeX(chro , 0 , 23); 
        int x2 = changeX(chro , 23 , 46);
        
        double a = 6*x1/((1<<23)-1);
        double b = 6*x2/((1<<23)-1);
//        double y = caculateY((x&56)>>3, x&7);//注意根据具体情况对值补偿确保不会出现负值。此处不为负，所以不需要补偿  
        double y = caculateY(a,b);//注意根据具体情况对值补偿确保不会出现负值。此处不为负，所以不需要补偿  
        chro.setScore(y);  
  
    }  
      
    /** 
     * @param chro 
     * @return 
     * @Description: 将二进制转化为对应的X 
     */  
    public abstract int changeX(Chromosome chro , int a , int b);  
      
      
    /** 
     * @param x 
     * @return 
     * @Description: 根据X计算Y值 Y=F(X) 
     */  
    public abstract double caculateY(double x1, double x2);  
  
    public void setPopulation(List<Chromosome> population) {  
        this.population = population;  
    }  
  
    public void setPopSize(int popSize) {  
        this.popSize = popSize;  
    }  
  
    public void setGeneSize(int geneSize) {  
        this.geneSize = geneSize;  
    }  
  
    public void setMaxIterNum(int maxIterNum) {  
        this.maxIterNum = maxIterNum;  
    }  
  
    public void setMutationRate(double mutationRate) {  
        this.mutationRate = mutationRate;  
    }  
  
    public void setMaxMutationNum(int maxMutationNum) {  
        this.maxMutationNum = maxMutationNum;  
    }  
  
    public double getBestScore() {  
        return bestScore;  
    }  
  
    public double getWorstScore() {  
        return worstScore;  
    }  
  
    public double getTotalScore() {  
        return totalScore;  
    }  
  
    public double getAverageScore() {  
        return averageScore;  
    }  
  
    public double getX_1() {  
        return x_1;  
    }  
  
    public double getY() {  
        return y;  
    }  
    
    public DynamicDataWindow getDdWindow() {
		return ddWindow;
	}
    
    public void setDdWindow(DynamicDataWindow ddWindow) {
		this.ddWindow = ddWindow;
	}
}  
