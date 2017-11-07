package com.boco.flow.order.pojo;

import java.util.HashMap;
import java.util.Map;

public class InfoAttempType {
	public static int ATTEMP_TYPE_ADD = 1;
	public static int ATTEMP_TYPE_ADJ = 3;
	public static int ATTEMP_TYPE_DEL = 2;
	
	public InfoAttempType(int attempType){
		this.attempType = attempType;
	}
	private int attempType;
	private Map<String,InfoDesignType> designTypeMap = new HashMap<String, InfoDesignType>();

	public int getAttempType() {
		return attempType;
	}
	
	public void setAttempType(int attempType) {
		this.attempType = attempType;
	}


	public Map<String, InfoDesignType> getDesignTypeMap() {
		return designTypeMap;
	}

	public void setDesignTypeMap(Map<String, InfoDesignType> designTypeMap) {
		this.designTypeMap = designTypeMap;
	}
	
}
