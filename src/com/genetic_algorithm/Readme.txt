关于遗传算法的详细原理以及具体的定义这里就不多介绍，想了解的可以自行百度，下面就简单介绍下自己对遗传算法的理解，本文对基因的编码采用二进制规则。

算法思想：
      遗传算法参照达尔文的进化论，认为物种都是向好的方向去发展（适者生存），因此可以认为到足够的代数之后，得到的最值可实际的最值很接近。

算法步骤：
1）随机产生一个种群；
2）计算种群的适应度、最好适应度、最差适应度、平均适应度等指标；
3）验证种群代数是否达到自己设置的阈值，如果达到结束计算，否则继续下一步计算；
4）采用转盘赌法选择可以产生下一代的父代，产生下一代种群（种群中个体数量不变）；
5）种群发生基因突变；
6）重复2、3、4、5步。

算法实现-基因部分
1、种群个体（这里认为是染色体），在个体中，我们为这个个体添加两个属性，个体的基因和基因对应的适应度（函数值）。
[java] view plain copy
public class Chromosome {  
    private boolean[] gene;//基因序列  
    private double score;//对应的函数得分  
}  

2、随机生成基因序列，基因的每一个位置是0还是1，这里采用完全随机的方式实现。
[java] view plain copy
public Chromosome(int size) {  
    if (size <= 0) {  
        return;  
    }  
    initGeneSize(size);  
    for (int i = 0; i < size; i++) {  
        gene[i] = Math.random() >= 0.5;  
    }  
}  
  
private void initGeneSize(int size) {  
    if (size <= 0) {  
        return;  
    }  
    gene = new boolean[size];  
}  

3、把基因转化为对应的值，比如101对应的数字是5，这里采用位运算来实现。
[java] view plain copy
public int getNum() {  
    if (gene == null) {  
        return 0;  
    }  
    int num = 0;  
    for (boolean bool : gene) {  
        num <<= 1;  
        if (bool) {  
            num += 1;  
        }  
    }  
    return num;  
}  

4、基因发生变异，对于变异的位置这里完全采取随机的方式实现，变异原则是由1变为0，0变为1。
[java] view plain copy
public void mutation(int num) {  
    //允许变异  
    int size = gene.length;  
    for (int i = 0; i < num; i++) {  
        //寻找变异位置  
        int at = ((int) (Math.random() * size)) % size;  
        //变异后的值  
        boolean bool = !gene[at];  
        gene[at] = bool;  
    }  
}  

5、克隆基因，用于产生下一代，这一步就是将已存在的基因copy一份。
[java] view plain copy
public static Chromosome clone(final Chromosome c) {  
    if (c == null || c.gene == null) {  
        return null;  
    }  
    Chromosome copy = new Chromosome();  
    copy.initGeneSize(c.gene.length);  
    for (int i = 0; i < c.gene.length; i++) {  
        copy.gene[i] = c.gene[i];  
    }  
    return copy;  
}  

6、父母双方产生下一代，这里两个个体产生两个个体子代，具体哪段基因差生交叉，完全随机。
[java] view plain copy
public static List<Chromosome> genetic(Chromosome p1, Chromosome p2) {  
    if (p1 == null || p2 == null) { //染色体有一个为空，不产生下一代  
        return null;  
    }  
    if (p1.gene == null || p2.gene == null) { //染色体有一个没有基因序列，不产生下一代  
        return null;  
    }  
    if (p1.gene.length != p2.gene.length) { //染色体基因序列长度不同，不产生下一代  
        return null;  
    }  
    Chromosome c1 = clone(p1);  
    Chromosome c2 = clone(p2);  
    //随机产生交叉互换位置  
    int size = c1.gene.length;  
    int a = ((int) (Math.random() * size)) % size;  
    int b = ((int) (Math.random() * size)) % size;  
    int min = a > b ? b : a;  
    int max = a > b ? a : b;  
    //对位置上的基因进行交叉互换  
    for (int i = min; i <= max; i++) {  
        boolean t = c1.gene[i];  
        c1.gene[i] = c2.gene[i];  
        c2.gene[i] = t;  
    }  
    List<Chromosome> list = new ArrayList<Chromosome>();  
    list.add(c1);  
    list.add(c2);  
    return list;  
}  


算法实现-遗传算法
1、对于遗传算法，我们需要有对应的种群以及我们需要设置的一些常量：种群数量、基因长度、基因突变个数、基因突变率等，具体参照如下代码：
[java] view plain copy
public abstract class GeneticAlgorithm {  
    private List<Chromosome> population = new ArrayList<Chromosome>();//种群  
    private int popSize = 100;//种群数量  
    private int geneSize;//基因最大长度  
    private int maxIterNum = 500;//最大迭代次数  
    private double mutationRate = 0.01;//基因变异的概率  
    private int maxMutationNum = 3;//最大变异步长  
      
    private int generation = 1;//当前遗传到第几代  
      
    private double bestScore;//最好得分  
    private double worstScore;//最坏得分  
    private double totalScore;//总得分  
    private double averageScore;//平均得分  
      
    private double x; //记录历史种群中最好的X值  
    private double y; //记录历史种群中最好的Y值  
    private int geneI;//x y所在代数  
}  

2、初始化种群，在遗传算法开始时，我们需要初始化一个原始种群，这就是原始的第一代。
[java] view plain copy
private void init() {  
    for (int i = 0; i < popSize; i++) {  
        population = new ArrayList<Chromosome>();  
        Chromosome chro = new Chromosome(geneSize);  
        population.add(chro);  
    }  
    caculteScore();  
}  

3、在初始种群存在后，我们需要计算种群的适应度以及最好适应度、最坏适应度和平均适应度等。
[java] view plain copy
private void caculteScore() {  
    setChromosomeScore(population.get(0));  
    bestScore = population.get(0).getScore();  
    worstScore = population.get(0).getScore();  
    totalScore = 0;  
    for (Chromosome chro : population) {  
        setChromosomeScore(chro);  
        if (chro.getScore() > bestScore) { //设置最好基因值  
            bestScore = chro.getScore();  
            if (y < bestScore) {  
                x = changeX(chro);  
                y = bestScore;  
                geneI = generation;  
            }  
        }  
        if (chro.getScore() < worstScore) { //设置最坏基因值  
            worstScore = chro.getScore();  
        }  
        totalScore += chro.getScore();  
    }  
    averageScore = totalScore / popSize;  
    //因为精度问题导致的平均值大于最好值，将平均值设置成最好值  
    averageScore = averageScore > bestScore ? bestScore : averageScore;  
}  

4、在计算个体适应度的时候，我们需要根据基因计算对应的Y值，这里我们设置两个抽象方法，具体实现由类的实现去实现。
[java] view plain copy
private void setChromosomeScore(Chromosome chro) {  
    if (chro == null) {  
        return;  
    }  
    double x = changeX(chro);  
    double y = caculateY(x);  
    chro.setScore(y);  
  
}  
  
/** 
 * @param chro 
 * @return 
 * @Author:lulei   
 * @Description: 将二进制转化为对应的X 
 */  
public abstract double changeX(Chromosome chro);  
  
  
/** 
 * @param x 
 * @return 
 * @Author:lulei   
 * @Description: 根据X计算Y值 Y=F(X) 
 */  
public abstract double caculateY(double x);  

5、在计算完种群适应度之后，我们需要使用转盘赌法选取可以产生下一代的个体，这里有个条件就是只有个人的适应度不小于平均适应度才会长生下一代（适者生存）。
[java] view plain copy
private Chromosome getParentChromosome (){  
    double slice = Math.random() * totalScore;  
    double sum = 0;  
    for (Chromosome chro : population) {  
        sum += chro.getScore();  
        //转到对应的位置并且适应度不小于平均适应度  
        if (sum > slice && chro.getScore() >= averageScore) {  
            return chro;  
        }  
    }  
    return null;  
}  

6、选择可以产生下一代的个体之后，就要交配产生下一代。
[java] view plain copy
private void evolve() {  
    List<Chromosome> childPopulation = new ArrayList<Chromosome>();  
    //生成下一代种群  
    while (childPopulation.size() < popSize) {  
        Chromosome p1 = getParentChromosome();  
        Chromosome p2 = getParentChromosome();  
        List<Chromosome> children = Chromosome.genetic(p1, p2);  
        if (children != null) {  
            for (Chromosome chro : children) {  
                childPopulation.add(chro);  
            }  
        }   
    }  
    //新种群替换旧种群  
    List<Chromosome> t = population;  
    population = childPopulation;  
    t.clear();  
    t = null;  
    //基因突变  
    mutation();  
    //计算新种群的适应度  
    caculteScore();  
}  

7、在产生下一代的过程中，可能会发生基因变异。
[java] view plain copy
private void mutation() {  
    for (Chromosome chro : population) {  
        if (Math.random() < mutationRate) { //发生基因突变  
            int mutationNum = (int) (Math.random() * maxMutationNum);  
            chro.mutation(mutationNum);  
        }  
    }  
}  

8、将上述步骤一代一代的重复执行。
[java] view plain copy
public void caculte() {  
    //初始化种群  
    generation = 1;  
    init();  
    while (generation < maxIterNum) {  
        //种群遗传  
        evolve();  
        print();  
        generation++;  
    }  
}  


编写实现类
      由于上述遗传算法的类是一个抽象类，因此我们需要针对特定的事例编写实现类，假设我们计算 Y=100-log(X)在[6,106]上的最值。

1、我们假设基因的长度为24（基因的长度由要求结果的有效长度确定），因此对应的二进制最大值为 1<< 24，我们做如下设置
[java] view plain copy
public class GeneticAlgorithmTest extends GeneticAlgorithm{  
      
    public static final int NUM = 1 << 24;  
  
    public GeneticAlgorithmTest() {  
        super(24);    
    }  
}  

2、对X值的抽象方法进行实现
[java] view plain copy
@Override  
public double changeX(Chromosome chro) {  
    // TODO Auto-generated method stub    
    return ((1.0 * chro.getNum() / NUM) * 100) + 6;  
}  

3、对Y的抽象方法进行实现
[java] view plain copy
@Override  
public double caculateY(double x) {  
    // TODO Auto-generated method stub    
    return 100 - Math.log(x);  
}  


运行结果
img

遗传算法思考
      自己看了很多遗传算法的介绍，上面提到的最优解都是最后一代的最值，自己就有一个疑问了，为什么我知道前面所有带中的最值，也就是程序中的X  Y值，为什么不能用X Y值做遗传算法最后的结果值呢？