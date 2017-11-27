/**
 * ���������Ŵ��㷨������һ�������࣬���������Ҫ����ض���������дʵ���࣬�������Ǽ��� Y=100-log(X)��[6,106]�ϵ���ֵ��

1�����Ǽ������ĳ���Ϊ24������ĳ�����Ҫ��������Ч����ȷ��������˶�Ӧ�Ķ��������ֵΪ 1<< 24����������������
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
    	
    	// f(x)=xsin(10pi*x)+2  x��[-1,2]
//        return ((1.0 * chro.getNum() / NUM)*3) - 1;  
    	
    	//f(x) = x + 10*sin(5*x) + 7*cos(4*x)  x��[0,9]
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
