package jgap_example.ga_demo;

import java.util.List;

public class CondExpress {

	private double value;
	
	private int flag;
	// List<Integer,Double> valueList;

	List<CondGene> valueList;

	public List<CondGene> getValueList() {
		return valueList;
	}

	public void setValueList(List<CondGene> valueList) {
		this.valueList = valueList;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
