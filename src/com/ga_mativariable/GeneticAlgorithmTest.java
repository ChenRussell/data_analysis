package com.ga_mativariable;

public class GeneticAlgorithmTest extends GeneticAlgorithm{  
    /***/
	public static final int GENE_LENGTH = 46;
	/**基因对应的数值上限，由基因 的位数决定*/
    public static final int NUM = 1 << GENE_LENGTH;  
    
    public GeneticAlgorithmTest() {  
        super(GENE_LENGTH);    
    }  
      
    @Override  
    public int changeX(Chromosome chro,int a ,int b) {  
        // TODO Auto-generated method stub    
        return chro.getNum(a ,b); 
    }  
  
    @Override  
    public double caculateY(double  x, double y) {  
        // TODO Auto-generated method stub 
//    	return x1*x1+x2*x2;  
    	return 3 - Math.sin(2 * x) * Math.sin(2 * x) - Math.sin(2 * y) * Math.sin(2 * y);
    	
    }  
  
    public static void main(String[] args) {  
        GeneticAlgorithmTest test = new GeneticAlgorithmTest(); 
//        test.setDdWindow(new DynamicDataWindow("遗传算法最优化求解过程"));
        test.caculte();  
    }  
}  
