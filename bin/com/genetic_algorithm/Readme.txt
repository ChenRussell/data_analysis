�����Ŵ��㷨����ϸԭ���Լ�����Ķ�������Ͳ�����ܣ����˽�Ŀ������аٶȣ�����ͼ򵥽������Լ����Ŵ��㷨����⣬���ĶԻ���ı�����ö����ƹ���

�㷨˼�룺
      �Ŵ��㷨���մ���ĵĽ����ۣ���Ϊ���ֶ�����õķ���ȥ��չ���������棩����˿�����Ϊ���㹻�Ĵ���֮�󣬵õ�����ֵ��ʵ�ʵ���ֵ�ܽӽ���

�㷨���裺
1���������һ����Ⱥ��
2��������Ⱥ����Ӧ�ȡ������Ӧ�ȡ������Ӧ�ȡ�ƽ����Ӧ�ȵ�ָ�ꣻ
3����֤��Ⱥ�����Ƿ�ﵽ�Լ����õ���ֵ������ﵽ�������㣬���������һ�����㣻
4������ת�̶ķ�ѡ����Բ�����һ���ĸ�����������һ����Ⱥ����Ⱥ�и����������䣩��
5����Ⱥ��������ͻ�䣻
6���ظ�2��3��4��5����

�㷨ʵ��-���򲿷�
1����Ⱥ���壨������Ϊ��Ⱦɫ�壩���ڸ����У�����Ϊ�����������������ԣ�����Ļ���ͻ����Ӧ����Ӧ�ȣ�����ֵ����
[java] view plain copy
public class Chromosome {  
    private boolean[] gene;//��������  
    private double score;//��Ӧ�ĺ����÷�  
}  

2��������ɻ������У������ÿһ��λ����0����1�����������ȫ����ķ�ʽʵ�֡�
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

3���ѻ���ת��Ϊ��Ӧ��ֵ������101��Ӧ��������5���������λ������ʵ�֡�
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

4�����������죬���ڱ����λ��������ȫ��ȡ����ķ�ʽʵ�֣�����ԭ������1��Ϊ0��0��Ϊ1��
[java] view plain copy
public void mutation(int num) {  
    //�������  
    int size = gene.length;  
    for (int i = 0; i < num; i++) {  
        //Ѱ�ұ���λ��  
        int at = ((int) (Math.random() * size)) % size;  
        //������ֵ  
        boolean bool = !gene[at];  
        gene[at] = bool;  
    }  
}  

5����¡�������ڲ�����һ������һ�����ǽ��Ѵ��ڵĻ���copyһ�ݡ�
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

6����ĸ˫��������һ����������������������������Ӵ��������Ķλ���������棬��ȫ�����
[java] view plain copy
public static List<Chromosome> genetic(Chromosome p1, Chromosome p2) {  
    if (p1 == null || p2 == null) { //Ⱦɫ����һ��Ϊ�գ���������һ��  
        return null;  
    }  
    if (p1.gene == null || p2.gene == null) { //Ⱦɫ����һ��û�л������У���������һ��  
        return null;  
    }  
    if (p1.gene.length != p2.gene.length) { //Ⱦɫ��������г��Ȳ�ͬ����������һ��  
        return null;  
    }  
    Chromosome c1 = clone(p1);  
    Chromosome c2 = clone(p2);  
    //����������滥��λ��  
    int size = c1.gene.length;  
    int a = ((int) (Math.random() * size)) % size;  
    int b = ((int) (Math.random() * size)) % size;  
    int min = a > b ? b : a;  
    int max = a > b ? a : b;  
    //��λ���ϵĻ�����н��滥��  
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


�㷨ʵ��-�Ŵ��㷨
1�������Ŵ��㷨��������Ҫ�ж�Ӧ����Ⱥ�Լ�������Ҫ���õ�һЩ��������Ⱥ���������򳤶ȡ�����ͻ�����������ͻ���ʵȣ�����������´��룺
[java] view plain copy
public abstract class GeneticAlgorithm {  
    private List<Chromosome> population = new ArrayList<Chromosome>();//��Ⱥ  
    private int popSize = 100;//��Ⱥ����  
    private int geneSize;//������󳤶�  
    private int maxIterNum = 500;//����������  
    private double mutationRate = 0.01;//�������ĸ���  
    private int maxMutationNum = 3;//�����첽��  
      
    private int generation = 1;//��ǰ�Ŵ����ڼ���  
      
    private double bestScore;//��õ÷�  
    private double worstScore;//��÷�  
    private double totalScore;//�ܵ÷�  
    private double averageScore;//ƽ���÷�  
      
    private double x; //��¼��ʷ��Ⱥ����õ�Xֵ  
    private double y; //��¼��ʷ��Ⱥ����õ�Yֵ  
    private int geneI;//x y���ڴ���  
}  

2����ʼ����Ⱥ�����Ŵ��㷨��ʼʱ��������Ҫ��ʼ��һ��ԭʼ��Ⱥ�������ԭʼ�ĵ�һ����
[java] view plain copy
private void init() {  
    for (int i = 0; i < popSize; i++) {  
        population = new ArrayList<Chromosome>();  
        Chromosome chro = new Chromosome(geneSize);  
        population.add(chro);  
    }  
    caculteScore();  
}  

3���ڳ�ʼ��Ⱥ���ں�������Ҫ������Ⱥ����Ӧ���Լ������Ӧ�ȡ����Ӧ�Ⱥ�ƽ����Ӧ�ȵȡ�
[java] view plain copy
private void caculteScore() {  
    setChromosomeScore(population.get(0));  
    bestScore = population.get(0).getScore();  
    worstScore = population.get(0).getScore();  
    totalScore = 0;  
    for (Chromosome chro : population) {  
        setChromosomeScore(chro);  
        if (chro.getScore() > bestScore) { //������û���ֵ  
            bestScore = chro.getScore();  
            if (y < bestScore) {  
                x = changeX(chro);  
                y = bestScore;  
                geneI = generation;  
            }  
        }  
        if (chro.getScore() < worstScore) { //���������ֵ  
            worstScore = chro.getScore();  
        }  
        totalScore += chro.getScore();  
    }  
    averageScore = totalScore / popSize;  
    //��Ϊ�������⵼�µ�ƽ��ֵ�������ֵ����ƽ��ֵ���ó����ֵ  
    averageScore = averageScore > bestScore ? bestScore : averageScore;  
}  

4���ڼ��������Ӧ�ȵ�ʱ��������Ҫ���ݻ�������Ӧ��Yֵ���������������������󷽷�������ʵ�������ʵ��ȥʵ�֡�
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
 * @Description: ��������ת��Ϊ��Ӧ��X 
 */  
public abstract double changeX(Chromosome chro);  
  
  
/** 
 * @param x 
 * @return 
 * @Author:lulei   
 * @Description: ����X����Yֵ Y=F(X) 
 */  
public abstract double caculateY(double x);  

5���ڼ�������Ⱥ��Ӧ��֮��������Ҫʹ��ת�̶ķ�ѡȡ���Բ�����һ���ĸ��壬�����и���������ֻ�и��˵���Ӧ�Ȳ�С��ƽ����Ӧ�ȲŻ᳤����һ�����������棩��
[java] view plain copy
private Chromosome getParentChromosome (){  
    double slice = Math.random() * totalScore;  
    double sum = 0;  
    for (Chromosome chro : population) {  
        sum += chro.getScore();  
        //ת����Ӧ��λ�ò�����Ӧ�Ȳ�С��ƽ����Ӧ��  
        if (sum > slice && chro.getScore() >= averageScore) {  
            return chro;  
        }  
    }  
    return null;  
}  

6��ѡ����Բ�����һ���ĸ���֮�󣬾�Ҫ���������һ����
[java] view plain copy
private void evolve() {  
    List<Chromosome> childPopulation = new ArrayList<Chromosome>();  
    //������һ����Ⱥ  
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
    //����Ⱥ�滻����Ⱥ  
    List<Chromosome> t = population;  
    population = childPopulation;  
    t.clear();  
    t = null;  
    //����ͻ��  
    mutation();  
    //��������Ⱥ����Ӧ��  
    caculteScore();  
}  

7���ڲ�����һ���Ĺ����У����ܻᷢ��������졣
[java] view plain copy
private void mutation() {  
    for (Chromosome chro : population) {  
        if (Math.random() < mutationRate) { //��������ͻ��  
            int mutationNum = (int) (Math.random() * maxMutationNum);  
            chro.mutation(mutationNum);  
        }  
    }  
}  

8������������һ��һ�����ظ�ִ�С�
[java] view plain copy
public void caculte() {  
    //��ʼ����Ⱥ  
    generation = 1;  
    init();  
    while (generation < maxIterNum) {  
        //��Ⱥ�Ŵ�  
        evolve();  
        print();  
        generation++;  
    }  
}  


��дʵ����
      ���������Ŵ��㷨������һ�������࣬���������Ҫ����ض���������дʵ���࣬�������Ǽ��� Y=100-log(X)��[6,106]�ϵ���ֵ��

1�����Ǽ������ĳ���Ϊ24������ĳ�����Ҫ��������Ч����ȷ��������˶�Ӧ�Ķ��������ֵΪ 1<< 24����������������
[java] view plain copy
public class GeneticAlgorithmTest extends GeneticAlgorithm{  
      
    public static final int NUM = 1 << 24;  
  
    public GeneticAlgorithmTest() {  
        super(24);    
    }  
}  

2����Xֵ�ĳ��󷽷�����ʵ��
[java] view plain copy
@Override  
public double changeX(Chromosome chro) {  
    // TODO Auto-generated method stub    
    return ((1.0 * chro.getNum() / NUM) * 100) + 6;  
}  

3����Y�ĳ��󷽷�����ʵ��
[java] view plain copy
@Override  
public double caculateY(double x) {  
    // TODO Auto-generated method stub    
    return 100 - Math.log(x);  
}  


���н��
img

�Ŵ��㷨˼��
      �Լ����˺ܶ��Ŵ��㷨�Ľ��ܣ������ᵽ�����Žⶼ�����һ������ֵ���Լ�����һ�������ˣ�Ϊʲô��֪��ǰ�����д��е���ֵ��Ҳ���ǳ����е�X  Yֵ��Ϊʲô������X Yֵ���Ŵ��㷨���Ľ��ֵ�أ�