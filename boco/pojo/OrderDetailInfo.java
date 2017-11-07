package com.boco.flow.order.pojo;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.flow.common.pojo.IOrder;
import com.boco.flow.common.pojo.IOrderDetail;

public class OrderDetailInfo implements IOrderDetail {

	private String cuid;
	
	private IOrder order;
	
	private int attempType;
	
	private String designType;
	
	private String relatedSheetCuid;
	
	private int sortNo = 1;
	
	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	private String relatedServiceCuid;
	
	private String aPointType;
	
	private String aPointCuid;
	
	private String aPointName;
	
	private String zPointType;
	
	private String zPointCuid;
	
	private String zPointName;
	
	private Map<String, Object> data;
	
	public OrderDetailInfo(IOrder order, int attempType, String designType) {
		if(order == null || StringUtils.isBlank(order.getOrderId())) {
			throw new RuntimeException("缺少参数order！");
		}
		if(attempType != InfoAttempType.ATTEMP_TYPE_ADD && attempType != InfoAttempType.ATTEMP_TYPE_DEL && attempType != InfoAttempType.ATTEMP_TYPE_ADJ) {
			throw new RuntimeException("未定义的“工单类型”枚举！");
		}
		if(StringUtils.isBlank(designType)) {
			designType = "T";
		}
		this.order = order;
		this.attempType = attempType;
		this.designType = designType;
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public IOrder getOrder() {
		return order;
	}

	public void setOrder(IOrder order) {
		this.order = order;
	}

	public int getAttempType() {
		return attempType;
	}

	public void setAttempType(int attempType) {
		this.attempType = attempType;
	}

	public String getDesignType() {
		return designType;
	}

	public void setDesignType(String designType) {
		this.designType = designType;
	}

	public String getRelatedSheetCuid() {
		return relatedSheetCuid;
	}

	public void setRelatedSheetCuid(String relatedSheetCuid) {
		this.relatedSheetCuid = relatedSheetCuid;
	}

	public String getRelatedServiceCuid() {
		return relatedServiceCuid;
	}

	public void setRelatedServiceCuid(String relatedServiceCuid) {
		this.relatedServiceCuid = relatedServiceCuid;
	}

	public String getaPointType() {
		return aPointType;
	}

	public void setaPointType(String aPointType) {
		this.aPointType = aPointType;
	}

	public String getaPointCuid() {
		return aPointCuid;
	}

	public void setaPointCuid(String aPointCuid) {
		this.aPointCuid = aPointCuid;
	}

	public String getaPointName() {
		return aPointName;
	}

	public void setaPointName(String aPointName) {
		this.aPointName = aPointName;
	}

	public String getzPointType() {
		return zPointType;
	}

	public void setzPointType(String zPointType) {
		this.zPointType = zPointType;
	}

	public String getzPointCuid() {
		return zPointCuid;
	}

	public void setzPointCuid(String zPointCuid) {
		this.zPointCuid = zPointCuid;
	}

	public String getzPointName() {
		return zPointName;
	}

	public void setzPointName(String zPointName) {
		this.zPointName = zPointName;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
