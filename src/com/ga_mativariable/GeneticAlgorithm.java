package com.ga_mativariable;

import java.util.ArrayList;  
import java.util.Date;
import java.util.List;  
  
    
public abstract class GeneticAlgorithm {  
    private List<Chromosome> population = new ArrayList<Chromosome>();  
    /**��Ⱥ����*/
    private int popSize = 40;
    /**Ⱦɫ����󳤶�*/
    private int geneSize;
    /**����������*/
    private int maxIterNum = 500; 
    /**�������ĸ���*/
    private double mutationRate = 0.001;
    /**�����첽��*/
    private int maxMutationNum = 3;  
    /**��ǰ�Ŵ����ڼ���*/  
    private int generation = 1;  
      
    private double bestScore;//��õ÷�  
    private double worstScore;//��÷�  
    private double totalScore;//�ܵ÷�  
    private double averageScore;//ƽ���÷�  
      
    private double x_1; //��¼��ʷ��Ⱥ����õ�Xֵ  
    private double x_2; //��¼��ʷ��Ⱥ����õ�Xֵ  
    private double y=Double.MAX_VALUE; //��¼��ʷ��Ⱥ����õ�Yֵ  
    private int geneI;//x y���ڴ���  
    
    private DynamicDataWindow ddWindow;
    private long tp;
    
    public GeneticAlgorithm(int geneSize) {  
        this.geneSize = geneSize;  
    }  
      
    public void caculte() { 
    	
        //1.��ʼ����Ⱥ  
        init(); 
        for(generation = 1; generation < maxIterNum; generation++) {  
        	//2.������Ⱥ��Ӧ��
        	caculteScore(); 
        	System.out.println("3>��֤��ֵ...");
            //4.��Ⱥ�Ŵ�  
            evolve();
          //5.����ͻ��  
            mutation();
            print();  
        }  
    }  
      
    /** 
     * @Description: ������ 
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
     * @Description: ��ʼ����Ⱥ 
     */  
    private void init() {  
    	System.out.println("1>���ɳ�ʼ��Ⱥ...");
//    	ddWindow.setVisible(true);
    	population = new ArrayList<Chromosome>();
        for (int i = 0; i < popSize; i++) {  
            Chromosome chro = new Chromosome(geneSize);  
            population.add(chro);  
        }  
    }  
      
    /** 
     * @Description:��Ⱥ�����Ŵ� 
     */  
    private void evolve() {  
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();  
        //������һ����Ⱥ  
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
        System.out.println("4.2>�����Ӵ���Ⱥ...");
        //����Ⱥ�滻����Ⱥ  
        population.clear();  
        population = childPopulation;  
    }  
      
    /** 
     * @return 
     * Email: tyhj_sf@163.com   
     * @Description: ���̶ķ�ѡ������Ŵ���һ����Ⱦɫ�� 
     */  
    private Chromosome getParentChromosome (){  
//    	System.out.println("4.1>ɸѡ������Ⱥһ��...");
    	while (true) {
    		double slice = Math.random() * totalScore;  
            double sum = 0;  
            for (Chromosome chro : population) {  
                sum += chro.getScore();  
//                System.out.println("���ԣ�sum="+sum+"  chro.getScore()="+chro.getScore());
                if (sum > slice && chro.getScore() <= averageScore) {  
                    return chro;  
                }  
            }
		}
    }  
      
    /** 
     * @Description: ������Ⱥ��Ӧ�� 
     */  
    private void caculteScore() {
    	System.out.println("2>������Ⱥ��Ӧ��...");
    	setChromosomeScore(population.get(0));  
    	bestScore=(double)population.get(0).getScore();
    	worstScore=(double)population.get(0).getScore();
//    	bestScore=Double.MAX_VALUE;
//    	worstScore=Double.MIN_VALUE;
        totalScore = 0;  
        for (Chromosome chro : population) {  
            setChromosomeScore(chro);  
            if (chro.getScore() < bestScore) { //������û���ֵ  
                bestScore = chro.getScore();  
                if (y > bestScore) {  
                    x_1 = (double)changeX(chro,0,23)/((1<<23)-1);  
                    x_2 = (double)changeX(chro,23,46)/((1<<23)-1);  
                    y = bestScore;  
                    geneI = generation;  
                }  
            }  
            if (chro.getScore() > worstScore) { //���������ֵ  
                worstScore = chro.getScore();  
            }  
            totalScore += chro.getScore();  
        }  
        averageScore = totalScore / popSize;  
        //��Ϊ�������⵼�µ�ƽ��ֵ�������ֵ����ƽ��ֵ���ó����ֵ  
        averageScore = averageScore > worstScore ? worstScore : averageScore;  
    }  
      
    /** 
     * ����ͻ�� 
     */  
    private void mutation() {  
    	System.out.println("5>����ͻ��...");
        for (Chromosome chro : population) {  
            if (Math.random() < mutationRate) { //��������ͻ��  
                int mutationNum = (int) (Math.random() * maxMutationNum);  
                chro.mutation(mutationNum);  
            }  
        }  
    }  
      
    /** 
     * @param chro 
     * @Description: ���㲢����Ⱦɫ����Ӧ�� 
     */  
    private void setChromosomeScore(Chromosome chro) {  
        if (chro == null) {  
            return;  
        }  
        
        int x1 = changeX(chro , 0 , 23); 
        int x2 = changeX(chro , 23 , 46);
        
        double a = 6*x1/((1<<23)-1);
        double b = 6*x2/((1<<23)-1);
//        double y = caculateY((x&56)>>3, x&7);//ע����ݾ��������ֵ����ȷ��������ָ�ֵ���˴���Ϊ�������Բ���Ҫ����  
        double y = caculateY(a,b);//ע����ݾ��������ֵ����ȷ��������ָ�ֵ���˴���Ϊ�������Բ���Ҫ����  
        chro.setScore(y);  
  
    }  
      
    /** 
     * @param chro 
     * @return 
     * @Description: ��������ת��Ϊ��Ӧ��X 
     */  
    public abstract int changeX(Chromosome chro , int a , int b);  
      
      
    /** 
     * @param x 
     * @return 
     * @Description: ����X����Yֵ Y=F(X) 
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
