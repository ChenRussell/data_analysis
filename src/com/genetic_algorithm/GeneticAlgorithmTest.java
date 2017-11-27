/**
 * 由于上述遗传算法的类是一个抽象类，因此我们需要针对特定的事例编写实现类，假设我们计算 Y=100-log(X)在[6,106]上的最值。

1、我们假设基因的长度为24（基因的长度由要求结果的有效长度确定），因此对应的二进制最大值为 1<< 24，我们做如下设置
 */
package com.genetic_algorithm;

public class GeneticAlgorithmTest extends GeneticAlgorithm{  
    
    public static final int NUM = 1 << 24;  
  
    public GeneticAlgorithmTest() {  
        super(24);    
    }  
      
    @Override  
    public double changeX(Chromosome chro) {  
        // TODO Auto-generated method stub    
//    	System.out.println("********x value: "+((1.0 * chro.getNum() / NUM) * 100) + 6);
//        return ((1.0 * chro.getNum() / NUM) * 100) + 6;
    	
    	// f(x)=xsin(10pi*x)+2  x在[-1,2]
//        return ((1.0 * chro.getNum() / NUM)*3) - 1;  
    	
    	//f(x) = x + 10*sin(5*x) + 7*cos(4*x)  x在[0,9]
        return ((1.0 * chro.getNum() / NUM)*9);  
    }  
  
    @Override  
    public double caculateY(double x) {  
        // TODO Auto-generated method stub    
//        return 100 - Math.log(x);  
//        return Math.sin(10*x*Math.PI)*x+2;  
    	return x + 10*Math.sin(5*x) + 7*Math.cos(4*x);
    }  
  
    public static void main(String[] args) {  
        GeneticAlgorithmTest test = new GeneticAlgorithmTest();  
        test.caculte();  
    }  
}  
