package com.boco.flow.order.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.flow.common.pojo.IOrder;
import com.boco.flow.order.OrderConstants;

/**
 * 订单信息
 * @author LeeZw
 *
 */
public class OrderInfo implements IOrder {
	/**
	 * 订单编码
	 */
	private String orderCode;
	/**
	 * 订单ID
	 */
	private String orderId;
	/**
	 * 订单标题
	 */
	private String title;
	/**
	 * 来源系统
	 */
	private String formCode;
	/**
	 * 所属地市
	 */
	private String relatedDistrictCuid;
	/**
	 * 工单类型：1. 开通；2. 停闭；3. 调整
	 */
	private int sheetType = 1;
	/**
	 * 网络类型：1. 一干；2. 二干；3. 本地
	 */
	private int netType = 3;
	
	private Date applyDate;
	
	private Date finishDate;
	
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public OrderInfo(String orderId, String title, String relatedDistrictCuid, int netType, int sheetType) {
		if(StringUtils.isBlank(orderId)) {
			throw new RuntimeException("缺少参数orderId！");
		}
		if(StringUtils.isBlank(title)) {
			throw new RuntimeException("缺少参数title！");
		}
		if(StringUtils.isBlank(relatedDistrictCuid)) {
			throw new RuntimeException("缺少参数relatedDistrictCuid！");
		}
		if (netType != OrderConstants.NET_TYPE_LV1 && netType != OrderConstants.NET_TYPE_LV2 && netType != OrderConstants.NET_TYPE_LV3) {
			throw new RuntimeException("未定义的“网络类型”枚举！");
		}
		if (sheetType != OrderConstants.ORDER_TYPE_ADD && sheetType != OrderConstants.ORDER_TYPE_DEL && sheetType != OrderConstants.ORDER_TYPE_ADJ) {
			throw new RuntimeException("未定义的“工单类型”枚举！");
		}
		Date now = new Date();
		this.orderId = orderId;
		this.title = title;
		this.relatedDistrictCuid = relatedDistrictCuid;
		this.formCode = "tnms";
		this.netType = netType;
		this.sheetType = sheetType;
		this.applyDate = now;
		this.finishDate = new Date(now.getTime() + (long) 7 * 24 * 60 * 60 * 1000);
	}
	
	public OrderInfo(String orderCode, String orderId, String title, String relatedDistrictCuid, int netType, int sheetType) {
		if(StringUtils.isBlank(orderId)) {
			throw new RuntimeException("缺少参数orderId！");
		}
		if(StringUtils.isBlank(title)) {
			throw new RuntimeException("缺少参数title！");
		}
		if(StringUtils.isBlank(relatedDistrictCuid)) {
			throw new RuntimeException("缺少参数relatedDistrictCuid！");
		}
		if (netType != OrderConstants.NET_TYPE_LV1 && netType != OrderConstants.NET_TYPE_LV2 && netType != OrderConstants.NET_TYPE_LV3) {
			throw new RuntimeException("未定义的“网络类型”枚举！");
		}
		if (sheetType != OrderConstants.ORDER_TYPE_ADD && 
				sheetType != OrderConstants.ORDER_TYPE_DEL && 
				sheetType != OrderConstants.ORDER_TYPE_ADJ && 
				sheetType != OrderConstants.ORDER_TYPE_RECOVER &&
				sheetType != OrderConstants.ORDER_TYPE_STOP) {
			throw new RuntimeException("未定义的“工单类型”枚举！");
		}
		Date now = new Date();
		this.orderCode = orderCode;
		this.orderId = orderId;
		this.title = title;
		this.relatedDistrictCuid = relatedDistrictCuid;
		this.formCode = "tnms";
		this.netType = netType;
		this.sheetType = sheetType;
		this.applyDate = now;
		this.finishDate = new Date(now.getTime() + (long) 7 * 24 * 60 * 60 * 1000);
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public String getRelatedDistrictCuid() {
		return relatedDistrictCuid;
	}

	public void setRelatedDistrictCuid(String relatedDistrictCuid) {
		this.relatedDistrictCuid = relatedDistrictCuid;
	}

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}

	public int getSheetType() {
		return sheetType;
	}

	public void setSheetType(int sheetType) {
		this.sheetType = sheetType;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
