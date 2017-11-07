package com.boco.flow.order.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.attemp.pojo.IAttempService;
import com.boco.attemp.pojo.IName;
import com.boco.attemp.pojo.IService;

public class InfoDesignType {
	
	private String designType;
	
	public InfoDesignType(String designType){
		this.designType = designType;
	}
	
	private Map<IName,List<IService>> resNameMap = new HashMap<IName, List<IService>>();
	
	/**
	 * 调前资源的ID
	 */
	private List<String> relatedResInfo = new ArrayList<String>();
	
//	private List<String> resIdList = new ArrayList<String>();
	
	private List<IService> resList = new ArrayList<IService>();
	
	private List<IAttempService> attSrvList = new ArrayList<IAttempService>();
	
	/**
	 * 业务明细的ID
	 */
	private List<String> detailIdList = new ArrayList<String>();
	
	private Map<String,List<Map<String,Object>>> userPlanMap = new HashMap<String, List<Map<String,Object>>>();
	
	private Map<String,List<Map<String,Object>>> districtPlanMap = new HashMap<String, List<Map<String,Object>>>();
	
	public Map<String, List<Map<String, Object>>> getUserPlanMap() {
		return userPlanMap;
	}
	
	public Map<String, List<Map<String, Object>>> getDistrictPlanMap() {
		return districtPlanMap;
	}

	public String getDesignType() {
		return designType;
	}
	
	public Map<IName, List<IService>> getResNameMap() {
		return resNameMap;
	}
	public List<String> getRelatedResInfo() {
		return relatedResInfo;
	}
	public List<IService> getResList() {
		return resList;
	}
	public List<IAttempService> getAttSrvList() {
		return attSrvList;
	}
	public List<String> getDetailIdList() {
		return detailIdList;
	}
	
//	public List<String> getResIdList() {
//		return resIdList;
//	}
	
}
