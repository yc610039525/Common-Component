package com.boco.flow.optical.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.attemp.bo.IMaintainBO;
import com.boco.attemp.bo.IServiceBO;
import com.boco.attemp.pojo.AttempLog;
import com.boco.attemp.pojo.AttempLogActionParam;
import com.boco.attemp.pojo.IService;
import com.boco.attemp.pojo.PathPoint;
import com.boco.attemp.pojo.Service;
import com.boco.attemp.pojo.ServicePath;
import com.boco.attemp.pojo.ServiceRel;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.core.utils.lang.CollectionHelper;
import com.boco.flow.bo.ProcessBO;
import com.boco.flow.common.SheetConstants;
import com.boco.flow.common.pojo.IOrder;
import com.boco.flow.model.Log;
import com.boco.flow.model.RoleVO;
import com.boco.flow.model.SheetInst;
import com.boco.flow.model.TaskInst;
import com.boco.flow.model.TaskService;
import com.boco.flow.optical.pojo.AttempOpticalWayService;
import com.boco.flow.optical.pojo.OpticalWayName;
import com.boco.flow.order.bo.FacedeOrderMaintainBO;
import com.boco.flow.order.pojo.InfoDesignType;
import com.boco.ws.client.optical.IrmsOpticalWayClient;
import com.boco.ws.client.optical.OdnOpticalWayClient;
import com.boco.ws.client.optical.RMRouteDesignClient;
import com.boco.ws.client.traph.TnmsCircuitClient;

@SuppressWarnings({"unchecked", "static-access"})
public class OptMaintainBO  implements IMaintainBO,IServiceBO{
	
	private Logger logger = LoggerFactory.getLogger(OptMaintainBO.class);

	private static final String sqlMap = "OptMaintain";
	
	public IbatisDAO IbatisResDAO;
	public ProcessBO ProcessBO;
	public void setIbatisResDAO(IbatisDAO ibatisResDAO) {
		IbatisResDAO = ibatisResDAO;
	}
	public void setProcessBO(ProcessBO processBO) {
		ProcessBO = processBO;
	}
	private OptDispatchBO OptDispatchBO;
	
	private FiberOptDealBO FiberOptDealBO;
	
	public void setOptDispatchBO(OptDispatchBO optDispatchBO) {
		OptDispatchBO = optDispatchBO;
	}
	private OpticalWayNameBO OpticalWayNameBO;
	
	public void setOpticalWayNameBO(OpticalWayNameBO opticalWayNameBO) {
		OpticalWayNameBO = opticalWayNameBO;
	}
	
	private FacedeOrderMaintainBO FacedeOrderMaintainBO;
	
	public void setFacedeOrderMaintainBO(FacedeOrderMaintainBO facedeOrderMaintainBO) {
		FacedeOrderMaintainBO = facedeOrderMaintainBO;
	}
	
	private TnmsCircuitClient TnmsCircuitClient;
	public void setTnmsCircuitClient(TnmsCircuitClient tnmsCircuitClient) {
		TnmsCircuitClient = tnmsCircuitClient;
	}
	
	private OptDesignBO OptDesignBO;
	
	public void setOptDesignBO(OptDesignBO optDesignBO) {
		OptDesignBO = optDesignBO;
	}
	
	public void setFiberOptDealBO(FiberOptDealBO fiberOptDealBO) {
		FiberOptDealBO = fiberOptDealBO;
	}
	private IrmsOpticalWayClient IrmsOpticalWayClient;
	public void setIrmsOpticalWayClient(IrmsOpticalWayClient irmsOpticalWayClient) {
		IrmsOpticalWayClient = irmsOpticalWayClient;
	}
	
	private OdnOpticalWayClient OdnOpticalWayClient;
	public void setOdnOpticalWayClient(OdnOpticalWayClient odnOpticalWayClient) {
		OdnOpticalWayClient = odnOpticalWayClient;
	}
	
	private RMRouteDesignClient RMRouteDesignClient;
	public void setRMRouteDesignClient(RMRouteDesignClient rmRouteDesignClient) {
		RMRouteDesignClient = rmRouteDesignClient;
	}
	/**
	 * 批量创建调度中光路信息（开通）
	 * @param ac
	 * @param taskCuid
	 * @param noList
	 * @param attempOpticalWay
	 */
	public List<String> createAttempOpticalWayAddRecords(ServiceActionContext ac, String taskCuid, List<Integer> noList, AttempOpticalWayService attempOpticalWay) {
		logger.info("==========批量创建调度中光路信息（开通）========createAttempOpticalWayAddRecords");
		if(attempOpticalWay == null) {
			throw new RuntimeException("光路基本信息不能为空！");
		}
		TaskInst task = ProcessBO.getTaskInstByTaskId(taskCuid);
		if(task==null||!ProcessBO.TASK_RUN.equals(task.getState()))throw new RuntimeException("当前任务已被其它人处理完毕！");
		SheetInst sheet = task.getSheetInst();
		
		String siteCuidA = attempOpticalWay.getAPointCuid();
		String siteCuidZ = attempOpticalWay.getZPointCuid();
		if(StringUtils.isBlank(siteCuidA) || StringUtils.isBlank(siteCuidZ)) {
			throw new RuntimeException("起始点、终止点不能为空！");
		}else if(noList == null || noList.size() == 0) {
			throw new RuntimeException("光路编号不能为空！");
		}
		//校验这些关路名称是否可用
		List<OpticalWayName> optWayNameList = new ArrayList<OpticalWayName>();
		for (Integer no : noList) {
			OpticalWayName name = new OpticalWayName(siteCuidA, siteCuidZ);
			name.setNo(no);
			optWayNameList.add(name);
		}
		List<String> checkResult = OpticalWayNameBO.validateOpticalWayLabelCnsUniqu(optWayNameList);
		if(!checkResult.isEmpty()){
			StringBuffer sb = new StringBuffer();
			if (checkResult.size() > 0) {
				for (String result : checkResult) {
					sb.append(result + "\n");
				}
			}
			sb.append("请修改名称后再提交！");
			throw new RuntimeException(sb.toString());
		}
		Map<String, Object> dataMap = attempOpticalWay.getDataMap();
		String  nePortCuidA=(String)dataMap.get("NE_PORT_CUID_A");
		String  nePortCuidZ=(String)dataMap.get("NE_PORT_CUID_Z");
		long routeType = Long.parseLong((String)dataMap.get("ROUTE_TYPE"));
		List<OpticalWayName> nameList = OpticalWayNameBO.createOpticalWayNames(siteCuidA, siteCuidZ, noList, routeType);
		Integer maxSortNo = OptDispatchBO.selectMaxSortNoByTask(taskCuid);
		if(maxSortNo==null){
			maxSortNo=0;
		}else{
			maxSortNo = maxSortNo+1;
		}
		List<AttempOpticalWayService> infoList = new ArrayList<AttempOpticalWayService>();
		for(int i = 0; i < nameList.size(); i++) {
			AttempOpticalWayService o = attempOpticalWay.copyOpticalWay();
			o.addData("SORT_NO", maxSortNo++);
			infoList.add(o);
		}
		return this.createAddAttempOpticalWayRecords(nameList, infoList, null,task);
	}
	
	private List<String> createAddAttempOpticalWayRecords(List<OpticalWayName> nameList,List<AttempOpticalWayService> infoList, Map<String,TaskInst> taskMap, TaskInst task){
		Date now = new Date();
		List<Record> optWayRds = new ArrayList<Record>();
		List<Record> task2ServiceRds = new ArrayList<Record>();
		List<String> optWayCuidList = new ArrayList<String>();
		List<Record> updateList = new ArrayList<Record>();
		List<Record> pkList = new ArrayList<Record>();
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		for(int i = 0; i < infoList.size(); i++) {
			OpticalWayName optWayName = nameList.get(i);
			AttempOpticalWayService optWay = infoList.get(i);
			Map<String, Object> info = optWay.getDataMap();
			String designer = IbatisDAOHelper.getStringValue(info, "TRAPH_DESIGN_USER");
			
			if(task==null){
				if(taskMap==null)new RuntimeException("当前资源无法和任务建立关联！");
				task = taskMap.get(designer);
			}
			if(task==null)throw new RuntimeException("当前光路无法和任务建立关联！");
			
			String devInfoCuid = IbatisDAOHelper.getStringValue(info, "CUID");//业务明细CUID，调度批量添加电路无此ID；
			String outKeyId = IbatisDAOHelper.getStringValue(info, "OUTSIDE_KEY_ID");//综资接口唯一ID；
			int sortNO = IbatisDAOHelper.getIntValue(info, "SORT_NO");
			
			String optWayCuid = "";
			if(StringUtils.isEmpty(devInfoCuid)){
				optWayCuid=CUIDHexGenerator.getInstance().generate("ATTEMP_OPTICAL_WAY");
			}else{
				optWayCuid=StringUtils.replace(devInfoCuid,"T_ACT_ORDER_DETAIL", "ATTEMP_OPTICAL_WAY");
			}
			
			optWayCuidList.add(optWayCuid);
			
			List<String> cuidList = task2AttempTraphCuidMap.get(task);
			if(cuidList == null) {
				cuidList = new ArrayList<String>();
				task2AttempTraphCuidMap.put(task, cuidList);
			}
			cuidList.add(optWayCuid);
			String OPTICAL_NUM = IbatisDAOHelper.getStringValue(info, "OPTICAL_NUM");
			if(StringUtils.isEmpty(OPTICAL_NUM)){
				OPTICAL_NUM = "2";
			}
			String OPTICAL_LEVEL = IbatisDAOHelper.getStringValue(info, "OPTICAL_LEVEL");
			if(StringUtils.isEmpty(OPTICAL_LEVEL)){
				OPTICAL_LEVEL = "7";
			}
			Record rd = new Record("ATTEMP_OPTICAL_WAY");
			rd.addColSqlValue("OBJECTID", rd.getObjectIdSql());
			rd.addColValue("CUID", optWayCuid);
			//综资接口的外部光路ID
			rd.addColValue("OTHER_BUSSINESS_CUID", outKeyId);
			//info  获取EXT_IDS 并翻译
			String labelCn=optWayName.getLabelCn();
			if(SysProperty.getInstance().getValue("districtName").trim().equals("吉林")){
				String extTypeValue = IbatisDAOHelper.getStringValue(info, "EXT_IDS");
				if(StringUtils.isNotEmpty(extTypeValue)){
					String sql = "SELECT V.NAME  FROM T_SYS_ENUM_TYPE T, T_SYS_ENUM_VALUE V  " +
					"WHERE T.CODE = 'OPTICAL_EXT_TYPE' " +
					  "AND V.RELATED_ENUM_TYPE_CUID = T.CUID  " +
					  "AND V.VALUE='"+extTypeValue+"'";
					List<Map<String,Object>> extTypeNames = IbatisResDAO.querySql(sql);
					if(CollectionHelper.isNotEmpty(extTypeNames)){
						Map<String,Object> extTypeMap=extTypeNames.get(0);
						String extTypeCn = IbatisDAOHelper.getStringValue(extTypeMap, "NAME");
						if(StringUtils.isNotEmpty(extTypeCn)){
							labelCn = labelCn+"/"+extTypeCn;
						}
					}
				}
			}
			rd.addColValue("LABEL_CN", labelCn);
			rd.addColValue("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			rd.addColValue("SITE_CUID_A", optWayName.getaStationId());
			rd.addColValue("SITE_CUID_Z", optWayName.getzStationId());
			rd.addColValue("NO", optWayName.getNo());
			rd.addColValue("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_NEW);
			rd.addColValue("SCHEDULE_STATE", SheetConstants.SCHEDULE_STATE_RUN);
			logger.info("-------------IbatisDAOHelper.getStringValue(info, 'APPLY_TYPE')="+IbatisDAOHelper.getStringValue(info, "APPLY_TYPE"));
			
			if (StringUtils.isEmpty(IbatisDAOHelper.getStringValue(info, "APPLY_TYPE"))){
				rd.addColValue("APPLY_TYPE", "0");
			}else{
				rd.addColValue("APPLY_TYPE",IbatisDAOHelper.getStringValue(info, "APPLY_TYPE"));
			}
			
			
			rd.addColValue("OPTICAL_LEVEL", OPTICAL_LEVEL);
			if (StringUtils.isEmpty(IbatisDAOHelper.getStringValue(info, "ROUTE_TYPE"))){
				rd.addColValue("ROUTE_TYPE", "1");
			}else{
				rd.addColValue("ROUTE_TYPE", IbatisDAOHelper.getStringValue(info, "ROUTE_TYPE"));
			}
			
			rd.addColValue("OPTICAL_NUM", OPTICAL_NUM);
			rd.addColValue("RELATED_SYSTEM_CUID", IbatisDAOHelper.getStringValue(info, "RELATED_SYSTEM_CUID"));
			if(SysProperty.getInstance().getValue("districtName").trim().equals("吉林")){
				String swhDevCuid_A=IbatisDAOHelper.getStringValue(info, "RELATED_A_SWITCH_DEV");
				if(StringUtils.isEmpty(swhDevCuid_A)){
					swhDevCuid_A=IbatisDAOHelper.getStringValue(info, "END_SWITCH_DEV_A");
				}
				String swhDevCuid_Z=IbatisDAOHelper.getStringValue(info, "RELATED_Z_SWITCH_DEV");
				if(StringUtils.isEmpty(swhDevCuid_Z)){
					swhDevCuid_Z=IbatisDAOHelper.getStringValue(info, "END_SWITCH_DEV_Z");
				}
				String swhPortCuid_A=IbatisDAOHelper.getStringValue(info, "RELATED_A_SWITCH_PORT");
				if(StringUtils.isEmpty(swhPortCuid_A)){
					swhPortCuid_A=IbatisDAOHelper.getStringValue(info, "END_SWITCHDEV_PORT_A");
				}
				String swhPortCuid_Z=IbatisDAOHelper.getStringValue(info, "RELATED_A_SWITCH_PORT");
				if(StringUtils.isEmpty(swhPortCuid_Z)){
					swhPortCuid_Z=IbatisDAOHelper.getStringValue(info, "END_SWITCHDEV_PORT_Z");
				}
	
				if(StringUtils.isNotEmpty(swhDevCuid_A)){
					if(swhDevCuid_A.startsWith("SWITCH_ELEMENT")){
						rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM SWITCH_ELEMENT R WHERE R.CUID='" + swhDevCuid_A + "'");	
					}else if(swhDevCuid_A.startsWith("TRANS_ELEMENT")){
						rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM TRANS_ELEMENT R WHERE R.CUID='" + swhDevCuid_A + "'");
					}else{
						rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM T_LOGIC_DEVICE R WHERE R.CUID='" + swhDevCuid_A + "'");
					}
				}
				if(StringUtils.isNotEmpty(swhDevCuid_Z)){
					if(swhDevCuid_Z.startsWith("SWITCH_ELEMENT")){
						rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM SWITCH_ELEMENT R WHERE R.CUID='" + swhDevCuid_Z + "'");	
					}else if(swhDevCuid_Z.startsWith("TRANS_ELEMENT")){
						rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM TRANS_ELEMENT R WHERE R.CUID='" + swhDevCuid_Z + "'");
					}else{
						rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM T_LOGIC_DEVICE R WHERE R.CUID='" + swhDevCuid_Z + "'");
					}
				}
				
				if(StringUtils.isNotEmpty(swhPortCuid_A)){
					if(swhPortCuid_A.startsWith("SWITCH_PORT")){
						rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM SWITCH_PORT R WHERE R.CUID='" + swhPortCuid_A + "'");	
					}else if(swhPortCuid_A.startsWith("PTP")){
						rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM PTP R WHERE R.CUID='" + swhPortCuid_A + "'");
					}else if(swhPortCuid_A.startsWith("T_LOGIC_PORT")){//T_LOGIC_PORT
						rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM T_LOGIC_PORT R WHERE R.CUID='" + swhPortCuid_A + "'");
					}else{
						rd.addColValue("RELATED_A_SWITCH_PORT", swhPortCuid_A);
					}
				}
				
				if(StringUtils.isNotEmpty(swhPortCuid_Z)){
					if(swhPortCuid_Z.startsWith("SWITCH_PORT")){
						rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM SWITCH_PORT R WHERE R.CUID='" + swhPortCuid_Z + "'");	
					}else if(swhPortCuid_Z.startsWith("T_LOGIC_PORT")){
						rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM PTP R WHERE R.CUID='" + swhPortCuid_Z + "'");
					}else if(swhPortCuid_Z.startsWith("PTP")){
						rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM T_LOGIC_PORT R WHERE R.CUID='" + swhPortCuid_Z + "'");
					}else{
						rd.addColValue("RELATED_Z_SWITCH_PORT", swhPortCuid_Z);
					}
				}
				String businessName = IbatisDAOHelper.getStringValue(info, "BUSINESS_NAME");
				if(StringUtils.isNotEmpty(businessName)){
					rd.addColValue("BUSINESS_NAME", IbatisDAOHelper.getStringValue(info, "BUSINESS_NAME"));
				}
				String ext_ids = IbatisDAOHelper.getStringValue(info, "EXT_IDS");
				//处理',1,2,3,' 情况对应字段为Number类型===》插入数据库会失败 
				if(StringUtils.isNotEmpty(ext_ids)){
					String regEx="[0-9]";  
				    Pattern p = Pattern.compile(regEx);     
				    Matcher m  = p.matcher(ext_ids);
				    String ext_type = null;
				    while(m.find()){
				    	 ext_type = m.group(); 
				    	 break;
				     }
				    if(StringUtils.isNotEmpty(ext_type)){
				    	rd.addColValue("EXT_TYPE", ext_type);
				    }
				}else{
			        rd.addColValue("EXT_TYPE", Integer.valueOf(1));
				}
			  
			}
			
			rd.addColValue("NE_PORT_CUID_Z",IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_Z"));
			if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_Z"))){
				OptDesignBO.createAttempOpticalWayToPort(optWayCuid,IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_Z"),"2","1");
			}
			rd.addColValue("NE_PORT_CUID_A",IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_A"));
			if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_A"))){
				OptDesignBO.createAttempOpticalWayToPort(optWayCuid,IbatisDAOHelper.getStringValue(info, "NE_PORT_CUID_A"),"1","1");
			}
			rd.addColValue("IS_WHOLE_JUMP", 0);
			rd.addColValue("IS_WHOLE_ROUTE", 0);
			rd.addColValue("OPTICAL_WAY_STATE", 1);
			rd.addColValue("OPTICAL_WAY_TYPE", 1);
			rd.addColValue("OBJECT_TYPE_CODE", 1);
			rd.addColValue("CREATE_TIME", now);
			rd.addColValue("LAST_MODIFY_TIME", now);
			/******添加创建人******/
			if(designer!=null){

				rd.addColValue("CREATOR",designer);
			}
			else{
				//取出光路创建人
				rd.addColValue("CREATOR", task.getCreator());
			}
			optWayRds.add(rd);
			Record t2sRd = this.makeTaskServiceRecord(task, devInfoCuid, optWayCuid, SheetConstants.SERVICE_STATE_DESIGN_UNDO,sortNO);
			task2ServiceRds.add(t2sRd);
			if(StringUtils.isNotEmpty(devInfoCuid)){
				Record pk = new Record("T_ACT_ORDER_DETAIL");
				pk.addColValue("CUID", devInfoCuid);
				pkList.add(pk);
				
				Record update = new Record("T_ACT_ORDER_DETAIL");
				update.addColValue("RELATED_DETAIL_CUID", optWayCuid);
				update.addColValue("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
				updateList.add(update);
			}
		}

		IbatisResDAO.insertDynamicTableBatch(optWayRds);
		IbatisResDAO.insertDynamicTableBatch(task2ServiceRds);
		
		this.IbatisResDAO.updateDynamicTableBatch(updateList, pkList);
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				this.setDefaultRole(t,cuidList);
			}
		}
		return optWayCuidList;
	}
	
	private Record makeTaskServiceRecord(TaskInst task,String infoCuid,String resId,String state,int sortNo){
		Record t2sRd = new Record("T_TASK_TO_SERVICE");
		t2sRd.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_TASK_TO_SERVICE"));
		t2sRd.addColValue("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
		t2sRd.addColValue("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
		t2sRd.addColValue("RELATED_TASK_CUID", task.getCuid());
		t2sRd.addColValue("RELATED_SERVICE_CUID", resId);
		t2sRd.addColValue("RELATED_SERVICE_TYPE", "ATTEMP_OPTICAL_WAY");
		t2sRd.addColValue("STATE", state);
		t2sRd.addColValue("SORT_NO", sortNo);
		return t2sRd;
	}
	private List<String> validateAndCopyRelatedRes(List<String> optWayCuidList,String sheeId,boolean copyRoute){
		if(optWayCuidList == null || optWayCuidList.size() == 0) {
			throw new RuntimeException("没有调整前光路信息！");
		}
		logger.debug("校验光路是否可以调度");
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", optWayCuidList);
		
		List<Map<String, Object>> existsAttempList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".validateOpticalWayState", pm);
		StringBuffer errorMsg = new StringBuffer();
		for(Map<String, Object> exists : existsAttempList) {
			String labelCn = IbatisDAOHelper.getStringValue(exists, "LABEL_CN");
			errorMsg.append("”"+labelCn+"“；");
		}
		if(errorMsg.length() > 0) {
			throw new RuntimeException("光路："+errorMsg.toString()+"已在调度中！");
		}
		Date now = new Date();
		
		List<Map<String, Object>> opticalRouteList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findOptRoute", pm);
		logger.debug("查询光路路由，结果："+opticalRouteList.size()+"条");
		List<String> routeCuidList = new ArrayList<String>();
		for(Map<String, Object> opticalRoute : opticalRouteList) {
			routeCuidList.add(IbatisDAOHelper.getStringValue(opticalRoute, "CUID"));
		}
		pm.put("routeCuidList", routeCuidList);
		pm.put("scheduleState", SheetConstants.SCHEDULE_STATE_RUN);
		pm.put("scheduleType", copyRoute?SheetConstants.SCHEDULE_TYPE_ADJUCT:SheetConstants.SCHEDULE_TYPE_CLOSE);
		pm.put("isWholeJump", 0);
		pm.put("isWholeRoute", 1);
		pm.put("createTime", now);
		pm.put("lastModifyTime", now);
		pm.put("relatedSheetId", sheeId);
		IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOpticalWayScheduleState", pm);
		logger.debug("修改光路调度状态为”调度中“");
		List<String> attempOptCuidList = this.copyAttOptPathFromOptWay(pm, optWayCuidList, true);
		return attempOptCuidList;
	}
	/**
	 * 批量创建调度中光路信息（调整）
	 * @param ac
	 * @param taskCuid
	 * @param optWayCuidList
	 * @return
	 */
	public List<String> createAttempOpticalWayAdjustRecords(ServiceActionContext ac, String taskCuid, List<String> optWayCuidList) {
		TaskInst task = ProcessBO.getTaskInstByTaskId(taskCuid);
		if(task==null||!ProcessBO.TASK_RUN.equals(task.getState()))throw new RuntimeException("当前任务已被其它人处理完毕！");
		List<String> attempOptCuidList=validateAndCopyRelatedRes(optWayCuidList,task.getRelatedSheetCuid(),true);
		Integer maxSortNo = OptDispatchBO.selectMaxSortNoByTask(taskCuid);
		if(maxSortNo==null){
			maxSortNo=0;
		}else{
			maxSortNo = maxSortNo+1;
		}
		List<Record> task2ServiceRds = new ArrayList<Record>();
		for(String optWayCuid : attempOptCuidList) {
			Record t2sRd = this.makeTaskServiceRecord(task, null, optWayCuid, SheetConstants.SERVICE_STATE_DESIGN_UNDO, maxSortNo++);
			task2ServiceRds.add(t2sRd);
		}
		IbatisResDAO.insertDynamicTableBatch(task2ServiceRds);
		logger.debug("创建光路与任务关联："+task2ServiceRds.size()+"条");
		return attempOptCuidList;
	}
	
	/**
	 * 根据业务明细创建调整光路
	 * @param order
	 * @param adjustList
	 * @param oldRes
	 * @param taskMap
	 * @param task
	 * @return
	 */
	public List<String> createAttempOpticalWayAdjustRecords(ServiceActionContext ac, List<AttempOpticalWayService> adjustList, Map<String, TaskInst> taskMap, TaskInst task) {
		if(adjustList == null || adjustList.isEmpty()) throw new RuntimeException("调整后光路数据为空！");
		
		List<String> optCuidList = new ArrayList<String>();
		for(AttempOpticalWayService opticalWay : adjustList) {
			String relatedServiceCuid = opticalWay.getRelatedServiceCuid();
			if(StringUtils.isNotBlank(relatedServiceCuid)) {
				optCuidList.add(relatedServiceCuid);
			}
		}
		
		List<String> attempOptCuidList = this.validateAndCopyRelatedRes(optCuidList, null, true);
		List<Record> records = new ArrayList<Record>();
		List<Record> pks = new ArrayList<Record>();
		List<Record> task2ServiceRds = new ArrayList<Record>();
		List<Record> updateList = new ArrayList<Record>();
		List<Record> pkList = new ArrayList<Record>();
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		
		int sortNo = 0;
		for(AttempOpticalWayService opticalWay : adjustList){
			Map<String, Object> newMap = opticalWay.getDataMap();
			String devInfoCuid = IbatisDAOHelper.getStringValue(newMap, "CUID");
			String relatedResCuid = IbatisDAOHelper.getStringValue(newMap, "RELATED_TRAPH_CUID");
			String atempCuid = StringUtils.replace(relatedResCuid, "OPTICAL_WAY", "ATTEMP_OPTICAL_WAY");
			String newApointCuid = IbatisDAOHelper.getStringValue(newMap, "A_POINT_CUID");
			String newZpointCuid = IbatisDAOHelper.getStringValue(newMap, "Z_POINT_CUID");
			String designer = IbatisDAOHelper.getStringValue(newMap, "TRAPH_DESIGN_USER");
			
			if(task == null) {
				if(taskMap==null)new RuntimeException("当前资源无法和任务建立关联！");
				task = taskMap.get(designer);
			}
			if(task == null) throw new RuntimeException("业务明细设计人与流程定义设计人不同！");
			
			Record value = new Record("ATTEMP_OPTICAL_WAY");
			value.addColValue("SITE_CUID_A", newApointCuid);
			value.addColValue("SITE_CUID_Z", newZpointCuid);
			value.addColValue("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			Record pk = new Record("ATTEMP_OPTICAL_WAY");
			pk.addColValue("CUID", atempCuid);
			records.add(value);
			pks.add(pk);
			Record t2sRd = this.makeTaskServiceRecord(task, devInfoCuid, atempCuid, SheetConstants.SERVICE_STATE_DESIGN_UNDO, sortNo++);
			task2ServiceRds.add(t2sRd);
			
			List<String> cuidList = task2AttempTraphCuidMap.get(task);
			if(cuidList == null) {
				cuidList = new ArrayList<String>();
				task2AttempTraphCuidMap.put(task, cuidList);
			}
			cuidList.add(atempCuid);
			
			
			Record ipk = new Record("T_ACT_ORDER_DETAIL");
			ipk.addColValue("CUID", devInfoCuid);
			pkList.add(ipk);
			
			Record update = new Record("T_ACT_ORDER_DETAIL");
			update.addColValue("RELATED_DETAIL_CUID", atempCuid);
			update.addColValue("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			updateList.add(update);
		}
		this.IbatisResDAO.updateDynamicTableBatch(records, pks);
		IbatisResDAO.insertDynamicTableBatch(task2ServiceRds);
		this.IbatisResDAO.updateDynamicTableBatch(updateList, pkList);
		logger.debug("创建光路与任务关联："+task2ServiceRds.size()+"条");
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				this.setDefaultRole(t,cuidList);
			}
		}
		return attempOptCuidList;
	}
	
	/**
	 * 批量创建调度中光路信息（停闭）
	 * @param ac
	 * @param taskCuid
	 * @param optWayCuidList
	 * @return
	 */
	public List<String> createAttempOpticalWayCloseRecords(ServiceActionContext ac, String taskCuid, List<String> optWayCuidList) {
		TaskInst task = ProcessBO.getTaskInstByTaskId(taskCuid);
		if(task==null||!ProcessBO.TASK_RUN.equals(task.getState()))throw new RuntimeException("当前任务已被其它人处理完毕！");
		List<String> attempOptCuidList=validateAndCopyRelatedRes(optWayCuidList,task.getRelatedSheetCuid(),false);
		Integer maxSortNo = OptDispatchBO.selectMaxSortNoByTask(taskCuid);
		if(maxSortNo==null){
			maxSortNo=0;
		}else{
			maxSortNo = maxSortNo+1;
		}
		List<Record> task2ServiceRds = new ArrayList<Record>();
		for(String optWayCuid : attempOptCuidList) {
			Record t2sRd = this.makeTaskServiceRecord(task, null, optWayCuid, SheetConstants.SERVICE_STATE_DESIGN_SUCCESS, maxSortNo++);
			task2ServiceRds.add(t2sRd);
		}
		IbatisResDAO.insertDynamicTableBatch(task2ServiceRds);
		logger.debug("创建光路与任务关联："+task2ServiceRds.size()+"条");
		return optWayCuidList;
	}
	
	/**
	 * 根据业务明细创建停闭光路
	 * @param order
	 * @param adjustList
	 * @param oldRes
	 * @param taskMap
	 * @param task
	 * @return
	 */
	public List<String> createAttempOpticalWayCloseRecords(ServiceActionContext ac, List<AttempOpticalWayService> closeList, Map<String, TaskInst> taskMap, TaskInst task) {
		if(closeList == null || closeList.isEmpty()) throw new RuntimeException("调整后光路为空！");
		
		List<String> optCuidList = new ArrayList<String>();
		for(AttempOpticalWayService opticalWay : closeList) {
			String relatedServiceCuid = opticalWay.getRelatedServiceCuid();
			if(StringUtils.isNotBlank(relatedServiceCuid)) {
				optCuidList.add(relatedServiceCuid);
			}
		}
		
		List<String> attempOptCuidList = this.validateAndCopyRelatedRes(optCuidList, null, false);
		List<Record> records = new ArrayList<Record>();
		List<Record> pks = new ArrayList<Record>();
		List<Record> task2ServiceRds = new ArrayList<Record>();
		
		List<Record> updateList = new ArrayList<Record>();
		List<Record> pkList = new ArrayList<Record>();
		
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		
		int sortNo = 0;
		for(AttempOpticalWayService opticalWay : closeList){
			Map<String, Object> newMap = opticalWay.getDataMap();
			
			String devInfoCuid = IbatisDAOHelper.getStringValue(newMap, "CUID");
			String relatedResCuid = IbatisDAOHelper.getStringValue(newMap, "RELATED_TRAPH_CUID");
			String atempCuid = StringUtils.replace(relatedResCuid, "OPTICAL_WAY", "ATTEMP_OPTICAL_WAY");
			String designer = IbatisDAOHelper.getStringValue(newMap, "TRAPH_DESIGN_USER");
			
			if(task == null) {
				if(taskMap==null)new RuntimeException("当前资源无法和任务建立关联！");
				task = taskMap.get(designer);
			}
			if(task == null) throw new RuntimeException("业务明细设计人与流程定义设计人不同！");
			
			Record value = new Record("ATTEMP_OPTICAL_WAY");
			value.addColValue("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			Record pk = new Record("ATTEMP_OPTICAL_WAY");
			pk.addColValue("CUID", atempCuid);
			records.add(value);
			pks.add(pk);
			
			List<String> cuidList = task2AttempTraphCuidMap.get(task);
			if(cuidList == null) {
				cuidList = new ArrayList<String>();
				task2AttempTraphCuidMap.put(task, cuidList);
			}
			cuidList.add(atempCuid);
			
			Record t2sRd = this.makeTaskServiceRecord(task, devInfoCuid, atempCuid, SheetConstants.SERVICE_STATE_DESIGN_SUCCESS, sortNo++);
			task2ServiceRds.add(t2sRd);
			
			Record ipk = new Record("T_ACT_ORDER_DETAIL");
			ipk.addColValue("CUID", devInfoCuid);
			pkList.add(ipk);
			
			Record update = new Record("T_ACT_ORDER_DETAIL");
			update.addColValue("RELATED_DETAIL_CUID", atempCuid);
			update.addColValue("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			updateList.add(update);
		}
		this.IbatisResDAO.updateDynamicTableBatch(records, pks);
		IbatisResDAO.insertDynamicTableBatch(task2ServiceRds);
		this.IbatisResDAO.updateDynamicTableBatch(updateList, pkList);
		
		logger.debug("创建光路与任务关联："+task2ServiceRds.size()+"条");
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				this.setDefaultRole(t,cuidList);
			}
		}
		return attempOptCuidList;
	}
	
	/**
	 * 复制光路数据
	 * @param pm
	 * @param optWayCuidList
	 * @param isCopyRoute
	 */
	private List<String> copyAttOptPathFromOptWay(Map<String, Object> pm, List<String> optWayCuidList, boolean isCopyRoute) {
		if(isCopyRoute == true) {
			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptRoute2Path", pm);
			logger.debug("创建调度中光路路由与光纤关系");
			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptIndiPoints", pm);
			logger.debug("复制调度中光路路由与转接点关系");
//			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOpt", pm);
//			logger.debug("复制调度中光路光纤");
			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptRoute", pm);
			logger.debug("复制调度中光路路由");
			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptWay2Port", pm);
			logger.debug("复制调度中光路与端口关系");
//			IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptWayRoute", pm);
//			logger.debug("复制调度中光路路由信息");
		}
		IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyAttOptWay", pm);
		List<String> attempOptWayList = new ArrayList<String>();
		for(String cuid:optWayCuidList){
			attempOptWayList.add(StringUtils.replace(cuid, "OPTICAL_WAY", "ATTEMP_OPTICAL_WAY"));
		}
		logger.debug("复制调度中光路");
		return attempOptWayList;
	}
	
	
	
	/**
	 * 保存调度中光路信息
	 * @param ac
	 * @param attempOpticalWay
	 */
	public void saveAttempOpticalWay(ServiceActionContext ac, Map<String, Object> attempOpticalWay) {
		String cuid = IbatisDAOHelper.getStringValue(attempOpticalWay, "CUID");
		String labelCn = IbatisDAOHelper.getStringValue(attempOpticalWay, "LABEL_CN");
		Record rd = new Record("ATTEMP_OPTICAL_WAY");
		rd.addColValue("LABEL_CN", labelCn);
		if(StringUtils.isBlank(cuid)) {
			IbatisResDAO.insertDynamicTable(rd);
		}else {
			Record pk = new Record("ATTEMP_OPTICAL_WAY");
			pk.addColValue("CUID", cuid);
			IbatisResDAO.updateDynamicTable(rd, pk);
		}
	}
	
	
	/**
	 * 根据cuid查询光路信息
	 * @param cuid
	 * @return
	 */
	public Map<String,Object> getAttempOpticalWayByCuid(String cuid){
		Map<String,Object> map = (Map<String,Object>)this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(this.sqlMap+".getAttempOpticalWayByCuid", cuid);
		logger.info("根据cuid:"+cuid+";查询光路信息:"+map);
		return map;
	}
	public void updateAttempOpticalWay(Map<String,Object> mp){
	  Integer opticalNum=Integer.parseInt(mp.get("OPTICAL_NUM").toString());
	  String cuid = (String) mp.get("CUID");
	  Map<String, Object> oldAttempOpticalWay=this.getAttempOpticalWayByCuid(cuid);
	  if(oldAttempOpticalWay!=null){
		  Integer oldNum=Integer.parseInt(oldAttempOpticalWay.get("OPTICAL_NUM").toString());
		  Record rd = new Record("ATTEMP_OPTICAL_WAY");
		  Record pk = new Record("ATTEMP_OPTICAL_WAY");
		  pk.addColValue("CUID", cuid);
		  if(opticalNum!=oldNum){//新旧纤芯数不相等
			  rd.addColValue("PATHROUTE", null);
		  }
		  String labelCn =IbatisDAOHelper.getStringValue(mp, "LABEL_CN");
		  if(StringUtils.isNotEmpty(labelCn)){
			  rd.addColValue("LABEL_CN",labelCn);
		  }
		  String no =IbatisDAOHelper.getStringValue(mp, "NO");
		  if(StringUtils.isNotEmpty(no)){
			  rd.addColValue("NO", no);
		  }
		  String alias =IbatisDAOHelper.getStringValue(mp, "ALIAS");
		  if(StringUtils.isNotEmpty(alias)){
			  rd.addColValue("ALIAS", alias);
		  }
		  String opticalNm =IbatisDAOHelper.getStringValue(mp, "OPTICAL_NUM");
		  if(StringUtils.isNotEmpty(opticalNm)){
			  rd.addColValue("OPTICAL_NUM", opticalNm);
		  }
		  String extType =IbatisDAOHelper.getStringValue(mp, "EXT_TYPE");
		  if(StringUtils.isNotEmpty(extType)){
			  rd.addColValue("EXT_TYPE", extType);
		  }
		  String applyType=IbatisDAOHelper.getStringValue(mp, "APPLY_TYPE");
		  if(StringUtils.isNotEmpty(applyType)){
			  rd.addColValue("APPLY_TYPE", applyType);
		  }
		  String swhDevCuid_A=IbatisDAOHelper.getStringValue(mp, "RELATED_A_SWITCH_DEV");
		  if(StringUtils.isNotEmpty(swhDevCuid_A)){
			  if(swhDevCuid_A.startsWith("SWITCH_ELEMENT")){
					rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM SWITCH_ELEMENT R WHERE R.CUID='" + swhDevCuid_A + "'");	
				}else if(swhDevCuid_A.startsWith("TRANS_ELEMENT")){
					rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM TRANS_ELEMENT R WHERE R.CUID='" + swhDevCuid_A + "'");
				}else{
					rd.addColSqlValue("RELATED_A_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM T_LOGIC_DEVICE R WHERE R.CUID='" + swhDevCuid_A + "'");
				}
		  }
		  String swhDevCuid_Z=IbatisDAOHelper.getStringValue(mp, "RELATED_Z_SWITCH_DEV");
		  if(StringUtils.isNotEmpty(swhDevCuid_Z)){
			  if(swhDevCuid_Z.startsWith("SWITCH_ELEMENT")){
					rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM SWITCH_ELEMENT R WHERE R.CUID='" + swhDevCuid_Z + "'");	
				}else if(swhDevCuid_Z.startsWith("TRANS_ELEMENT")){
					rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM TRANS_ELEMENT R WHERE R.CUID='" + swhDevCuid_Z + "'");
				}else{
					rd.addColSqlValue("RELATED_Z_SWITCH_DEV", "SELECT MAX(LABEL_CN) FROM T_LOGIC_DEVICE R WHERE R.CUID='" + swhDevCuid_Z + "'");
				} 
		  }
		  String swhPortCuid_A=IbatisDAOHelper.getStringValue(mp, "RELATED_A_SWITCH_PORT");
		  if(StringUtils.isNotEmpty(swhPortCuid_A)){
			  if(swhPortCuid_A.startsWith("SWITCH_PORT")){
					rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM SWITCH_PORT R WHERE R.CUID='" + swhPortCuid_A + "'");	
				}else if(swhPortCuid_A.startsWith("PTP")){
					rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM PTP R WHERE R.CUID='" + swhPortCuid_A + "'");
				}else if(swhPortCuid_A.startsWith("T_LOGIC_PORT")){//T_LOGIC_PORT
					rd.addColSqlValue("RELATED_A_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM T_LOGIC_PORT R WHERE R.CUID='" + swhPortCuid_A + "'");
				}else{
					rd.addColValue("RELATED_A_SWITCH_PORT", swhPortCuid_A);
				}
			}
		  String swhPortCuid_Z=IbatisDAOHelper.getStringValue(mp, "RELATED_Z_SWITCH_PORT");
		  if(StringUtils.isNotEmpty(swhPortCuid_Z)){
			  if(swhPortCuid_Z.startsWith("SWITCH_PORT")){
					rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM SWITCH_PORT R WHERE R.CUID='" + swhPortCuid_Z + "'");	
				}else if(swhPortCuid_Z.startsWith("T_LOGIC_PORT")){
					rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM T_LOGIC_PORT R WHERE R.CUID='" + swhPortCuid_Z + "'");
				}else if(swhPortCuid_Z.startsWith("PTP")){
					rd.addColSqlValue("RELATED_Z_SWITCH_PORT", "SELECT MAX(LABEL_CN) FROM  PTP R WHERE R.CUID='" + swhPortCuid_Z + "'");
				}else{
					rd.addColValue("RELATED_Z_SWITCH_PORT", swhPortCuid_Z);
				}
			}
		  if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(mp, "NE_PORT_CUID_A"))){
			  rd.addColValue("NE_PORT_CUID_A", IbatisDAOHelper.getStringValue(mp, "NE_PORT_CUID_A"));
		  }
		  if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(mp, "NE_PORT_CUID_Z"))){
			  rd.addColValue("NE_PORT_CUID_Z", IbatisDAOHelper.getStringValue(mp, "NE_PORT_CUID_Z"));
		  }
		  if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(mp, "OPTICAL_LEVEL"))){
			  rd.addColValue("OPTICAL_LEVEL", IbatisDAOHelper.getStringValue(mp, "OPTICAL_LEVEL"));  
		  }
		  if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(mp, "ROUTE_TYPE"))){
			  rd.addColValue("ROUTE_TYPE", IbatisDAOHelper.getStringValue(mp, "ROUTE_TYPE"));
		  }
		  if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(mp, "BUSINESS_NAME"))){
			  rd.addColValue("BUSINESS_NAME", IbatisDAOHelper.getStringValue(mp, "BUSINESS_NAME"));
		  }
		  IbatisResDAO.updateDynamicTable(rd, pk);
	  }
	}
	
	
	/**
	 * 根据订单明细生成调单明细
	 * 
	 * @param order
	 * @param devInfoMap
	 * @param taskList
	 */
	public List<String> createAddAttempOpticalWay(ServiceActionContext ac, Map<OpticalWayName, List<AttempOpticalWayService>> devInfoMap, Map<String, TaskInst> taskMap, TaskInst task) {
		Date now = new Date();
		List<String> optWayCuidList = new ArrayList<String>();
		for (OpticalWayName key : devInfoMap.keySet()) {
			List<AttempOpticalWayService> list = devInfoMap.get(key);
			if(StringUtils.isEmpty(key.getaStationId())||StringUtils.isEmpty(key.getzStationId())){
				continue;
			}
			List<OpticalWayName> nameList = OpticalWayNameBO.getOpticalWayNames(key.getaStationId(), key.getzStationId(), 0,list.size(),null);
			if (nameList.size() != list.size())
				throw new RuntimeException("生成的光路编号与光路数量不一致！");
			optWayCuidList.addAll(this.createAddAttempOpticalWayRecords(nameList, list, taskMap, task));
		}
		return optWayCuidList;
	}
	
	public List<String> createServiceDetailByOrderInfo(ServiceActionContext ac, Integer attempType, InfoDesignType res, 
			Map<String, TaskInst> taskMap, TaskInst task) {
		Map<OpticalWayName, List<AttempOpticalWayService>> devInfoMap = new HashMap<OpticalWayName, List<AttempOpticalWayService>>();
		List<AttempOpticalWayService> attempOpticalWayList = new ArrayList<AttempOpticalWayService>();
		
		for(IService service : res.getResList()) {
			if(service instanceof AttempOpticalWayService) {
				AttempOpticalWayService attempOpticalWay = (AttempOpticalWayService) service;
				attempOpticalWayList.add(attempOpticalWay);
				if(StringUtils.isNotBlank(attempOpticalWay.getAPointCuid())&&StringUtils.isNotBlank(attempOpticalWay.getZPointCuid())){
					OpticalWayName name = new OpticalWayName(attempOpticalWay.getAPointCuid(), attempOpticalWay.getZPointCuid());
					List<AttempOpticalWayService> list = devInfoMap.get(name);
					if(list == null) {
						list = new ArrayList<AttempOpticalWayService>();
						devInfoMap.put(name, list);
					}
					list.add(attempOpticalWay);
				}
			}
		}
		
		/*List<String> attempOpticalWayCuidList = res.getServiceIdList();
		if(attempOpticalWayCuidList != null && !attempOpticalWayCuidList.isEmpty()) {
			Map pm = new HashMap();
			pm.put("cuidList", attempOpticalWayCuidList);
			List attempList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".findAttempOpticalWay",pm);
			if(!attempList.isEmpty())throw new RuntimeException("业务明细已经生成调度光路！");
		}*/
		
		List<String> resIds = new ArrayList<String>();
		if (attempType == SheetConstants.SCHEDULE_TYPE_NEW) {
			resIds.addAll(this.createAddAttempOpticalWay(ac, devInfoMap, taskMap, task));
		}else if(attempType == SheetConstants.SCHEDULE_TYPE_ADJUCT){
			resIds.addAll(this.createAttempOpticalWayAdjustRecords(ac, attempOpticalWayList, taskMap, task));
		}else if(attempType == SheetConstants.SCHEDULE_TYPE_CLOSE){
			resIds.addAll(this.createAttempOpticalWayCloseRecords(ac, attempOpticalWayList, taskMap, task));
		}
		return resIds;
	}
	
	/**
	 * 通过任务与资源的关系ID，获取调度光路数据
	 * @param taskServiceCuidList
	 * @return
	 */
	public List<Map<String, Object>> findServiceByTaskService(List<String> taskServiceCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("taskServiceCuidList", taskServiceCuidList);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttempOpticalWay", pm);
		return list;
	}
    /**
	 * 通过批量调度光路ID，获取调度光路数据
	 * @param cuidList
	 * @return
	 */
	public List<Map<String,Object>> findAttempOpticalWay(List<String> cuidList){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", cuidList);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttempOpticalWay", pm);
		return list;
	}
	public void irmsWriteAttempOptical(ServiceActionContext ac,String taskId){
		AttempLogActionParam attempLogAction = new AttempLogActionParam();
		attempLogAction.setRelatedTaskCuid(taskId);
		attempLogAction.setAttempOpticalWayList(new ArrayList());
		attempLogAction.setActionCode("TNMS_WRITE");
		attempLogAction.setRemark("传输回写");
		Map<String,Object> mp = this.validateIsAbleToArchive(new ArrayList(), taskId);
		Map jumpMap = (Map)mp.get("jumpMap");
//		this.writeServiceLog(ac, attempLogAction, jumpMap);
	}
	
	private void writeAttempOptical(ServiceActionContext ac,List<String> serviceCuidList,Map jumpMap){
		logger.info("回写光路=======================writeAttempOptical========================");
		Map pm = new HashMap();
		pm.put("attempOpticalCuidList", serviceCuidList);
		//查询光路信息
		List<Map<String, Object>> attempOpticalList = this.findAttempOpticalWay(serviceCuidList);
		List<String> outIdList = new ArrayList<String>();
		for(Map<String,Object> map : attempOpticalList){
			String outId = IbatisDAOHelper.getStringValue(map, "OTHER_BUSSINESS_CUID");
			if(StringUtils.isNotBlank(outId)){
				outIdList.add(outId);
			}
		}
		//删除光路接口数据
		if(!outIdList.isEmpty()){
			FacedeOrderMaintainBO.deleteIfaceDetail(ac, outIdList);
		}
		if(SysProperty.getInstance().getValue("districtName").trim().equals("黑龙江")){
			List<String> newAttempOpticalWayCuidList = new ArrayList<String>();
			for(Map<String,Object> map : attempOpticalList){
				Object obj = map.get("ROUTE_DESIGNER");
				if(obj != null && obj.toString().equals("1")){
					newAttempOpticalWayCuidList.add((String)map.get("CUID"));
				}
			}
			logger.info("=============newAttempOpticalWayCuidList的长度："+newAttempOpticalWayCuidList.size());
			if(newAttempOpticalWayCuidList!=null && newAttempOpticalWayCuidList.size()>0){
				try {
					logger.info("=============调用管线回写接口开始=============");
					RMRouteDesignClient.writeOpticalResult(ac, newAttempOpticalWayCuidList);
					logger.info("=============调用管线回写接口结束=============");
				} catch (Exception e) {
					 logger.info("方法writeOpticalResult发错，"+e);
					 throw new RuntimeException("调用管线接口出错,"+e.getMessage());
				} 
			}
		}
		
		//根据attempOpticalCuidList删除原有数据，删除前修改状态
		//找到所有要释放的资源CUID
		List<Map<String, Object>> resList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".selectOpticalCuid", pm);
		//找到所有要创建的资源
		List<Map<String, Object>> attList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".selectAttempOpticalCuid", pm);
		
		//要释放状态的opticalList
		List<String> opticalList =new ArrayList<String>();
		//要释放状态的端口
		List<String> portList =new ArrayList<String>();
		//要释放的ODF状态
	    List<String> odfReleaseList = new ArrayList<String>();
	    //释放光路cuidList
	    List<String> releaseOpticalList = new ArrayList<String>();
	    
		//要占用状态的
		List<String> opticalUseList =new ArrayList<String>();
		//要占用状态的端口
		List<String> portUseList =new ArrayList<String>();
		//要占用的ODF状态端口
	    List<String> odfUseList = new ArrayList<String>();
		
		//回写的调度光路cuidList
		Set<String> writeOpticalSet = new HashSet<String>();
		
		List<String> writeOpticalWayList =  new ArrayList<String>();
		
		for(Map<String, Object> map:resList){
			if(map!=null){
				String pathCuid=IbatisDAOHelper.getStringValue(map, "PATH_CUID");  //OPTICAL_WAY
				String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
				if(StringUtils.isNotBlank(pathCuid) && !releaseOpticalList.contains(pathCuid)){
					releaseOpticalList.add(pathCuid);
				}
			    String aFPort=IbatisDAOHelper.getStringValue(map, "AF_PORT_CUID");
				String aSPort=IbatisDAOHelper.getStringValue(map, "AS_PORT_CUID");
				String zFPort=IbatisDAOHelper.getStringValue(map, "ZF_PORT_CUID");
				String zSPort=IbatisDAOHelper.getStringValue(map, "ZS_PORT_CUID");
				String origPointCuid = IbatisDAOHelper.getStringValue(map,"ORIG_POINT_CUID");
				String destPointCuid = IbatisDAOHelper.getStringValue(map, "DEST_POINT_CUID");
				
				if(StringUtils.isNotEmpty(opticalCuid) && !opticalList.contains(opticalCuid)){
					opticalList.add(opticalCuid);
				}
				if(StringUtils.isNotEmpty(aFPort)){
					if(portList.indexOf(aFPort)==-1){
						portList.add(aFPort);
					}
				}
				if(StringUtils.isNotEmpty(aSPort)){
					if(portList.indexOf(aSPort)==-1){
						portList.add(aSPort);
					}
				}
				if(StringUtils.isNotEmpty(zFPort)){
					if(portList.indexOf(zFPort)==-1){
						portList.add(zFPort);
					}
				}
				if(StringUtils.isNotEmpty(zSPort)){
					if(portList.indexOf(zSPort)==-1){
						portList.add(zSPort);
					}
				}
			   if (StringUtils.isNotEmpty(origPointCuid)){
					odfReleaseList.add(origPointCuid);
			       }
		       if (StringUtils.isNotEmpty(destPointCuid)){
		    	    odfReleaseList.add(destPointCuid);
		       }
			}
		}
		List<Record> rdList = new ArrayList<Record>();
		List<Record> rdPkList = new ArrayList<Record>();
		Map optMp = new HashMap();
		for(Map<String, Object> map:attList){
			if(map != null){
				String attempOptCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_PATH_CUID");
				String pathCuid=IbatisDAOHelper.getStringValue(map, "PATH_CUID");
				String aFPort=IbatisDAOHelper.getStringValue(map, "AF_PORT_CUID");
				String aSPort=IbatisDAOHelper.getStringValue(map, "AS_PORT_CUID");
				String zFPort=IbatisDAOHelper.getStringValue(map, "ZF_PORT_CUID");
				String zSPort=IbatisDAOHelper.getStringValue(map, "ZS_PORT_CUID");
				Integer scheduleType = IbatisDAOHelper.getIntValue(map, "SCHEDULE_TYPE");
				String attempPathCuid = IbatisDAOHelper.getStringValue(map, "ATTEMP_PATH_CUID");
				String origPointCuid = IbatisDAOHelper.getStringValue(map,"ORIG_POINT_CUID");
				String destPointCuid = IbatisDAOHelper.getStringValue(map, "DEST_POINT_CUID");
				if(StringUtils.isNotBlank(attempPathCuid) && !writeOpticalWayList.contains(attempOptCuid)){
					writeOpticalWayList.add(attempOptCuid);
				}
				if(!SheetConstants.SCHEDULE_TYPE_CLOSE.equals(scheduleType)) {
					if(StringUtils.isNotBlank(attempPathCuid)){
						writeOpticalSet.add(attempPathCuid);

					}
					if(StringUtils.isNotBlank(attempPathCuid)&&attempPathCuid.indexOf("ATTEMP_OPTICAL")>=0&&StringUtils.isNotBlank(pathCuid)
							&&pathCuid.indexOf("OPTICAL")==0){
						Record r = new Record("OPTICAL");
						if(StringUtils.isNotBlank(origPointCuid)){
							r.addColValue("ORIG_POINT_CUID", origPointCuid);
						}
						if(StringUtils.isNotBlank(destPointCuid)){
							r.addColValue("DEST_POINT_CUID", destPointCuid);
						}
						if((r.getColValue("ORIG_POINT_CUID")!=null)||(r.getColValue("DEST_POINT_CUID")!=null)){
							rdList.add(r);
						}
						Record pk = new Record("OPTICAL");
						pk.addColValue("CUID", map.get("PATH_CUID").toString());
						rdPkList.add(pk);
					}
					if(StringUtils.isNotEmpty(pathCuid)){
						optMp.clear();
						optMp.put("attempOptCuid", attempOptCuid);
						optMp.put("pathCuid", pathCuid);
						//更新optical的related_optical_way_cuid字段
						IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateOpticaWayCuid", optMp);
						opticalUseList.add(pathCuid);
						if(opticalList.indexOf(pathCuid)>=0){
							opticalList.remove(pathCuid);
						}
					}
					if(StringUtils.isNotEmpty(aFPort)){
						portUseList.add(aFPort);
						if(portList.indexOf(aFPort)>=0){
							portList.remove(aFPort);
						}
					}
					if(StringUtils.isNotEmpty(aSPort)){
						portUseList.add(aSPort);
						if(portList.indexOf(aSPort)>=0){
							portList.remove(aSPort);
						}
					}
					if(StringUtils.isNotEmpty(zFPort)){
						portUseList.add(zFPort);
						if(portList.indexOf(zFPort)>=0){
							portList.remove(zFPort);
						}
					}
					if(StringUtils.isNotEmpty(zSPort)){
						portUseList.add(zSPort);
						if(portList.indexOf(zSPort)>=0){
							portList.remove(zSPort);
						}
					}

			       if (StringUtils.isNotEmpty(origPointCuid)){
			    	   odfUseList.add(origPointCuid);
			       }
			       if (StringUtils.isNotEmpty(destPointCuid)){
			    	  odfUseList.add(destPointCuid);
			       }
				}
			}
		}
		//释放optical和fiber的状态
		if(opticalList.size()>0){
			pm.put("opticalList", opticalList);
			pm.put("state", 1);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateOpticaStatusByOpt", pm);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateFiberStatus", pm);
		}
		//释放端口状态
		if(portList.size()>0){
			pm.put("portList", portList);
			pm.put("state", 1);
			pm.put("isConnected", "");
	        pm.put("isConn",0);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updatePtpStatus", pm);
	       // IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateptpConnStatus", pm);
		}
		
		//占用optical和fiber的状态
		if(opticalUseList.size()>0){
			pm.put("opticalList", opticalUseList);
			pm.put("state", 2);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateOpticaStatus", pm);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateFiberStatus", pm);
		}
		//占用端口状态
		if(portUseList.size()>0){
			pm.put("portList", portUseList);
			pm.put("state", 2);
			pm.put("isConnected", 1);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updatePtpStatus", pm);
	        IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateptpConnStatus", pm);
		}
		
		  //占用ODF端口状态 
	    if (odfUseList.size() > 0) {
	        pm.put("portList", odfUseList);
	        pm.put("state", 2);
	        pm.put("isConnected", 1);
	        pm.put("isConn",1);
	        this.IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateodfportStatus", pm);
	        this.IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateptpConnStatus", pm);
	      }
	    //释放ODF端口状态
	    if (odfReleaseList.size() > 0) {
    	 	pm.put("portList", odfReleaseList);
	        pm.put("state", 1);//业务占用状态 
	        pm.put("isConnected", 0);//连接状态
	        pm.put("isConn",0);//物理关联
	        this.IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateodfportStatus", pm);
	        this.IbatisResDAO.getSqlMapClientTemplate().update("OptMaintain.updateptpConnStatus", pm);
	    }
		
		if(!releaseOpticalList.isEmpty()){
			//删除原有光路及其关联表
			pm.clear();
			pm.put("releaseOpticalList", releaseOpticalList);
			OptDesignBO.dealWriteJumpFibersByOptCuids(releaseOpticalList,false);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOpticalRouteToIniPoints", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOpticalRouteToPath", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOpticalRoute", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOpticalToPort", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOpticalWay", pm);
			FiberOptDealBO.deleteOpticalToOpticalWay(releaseOpticalList);
			OptDesignBO.deletePathInfos(releaseOpticalList);
		}
		if(!writeOpticalSet.isEmpty()){
			pm.clear();
			pm.put("attempOpticalCuidList", writeOpticalSet.toArray());
			//将光纤表进行创建
			//IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertTextOpticalMaintain", pm);
//			if(!rdList.isEmpty()){
//				IbatisResDAO.updateDynamicTableBatch(rdList, rdPkList);
//			}
			//以上注释掉了， 文本光纤在施工的时候必须替换为实体光纤
			List<String> writeOpticalSetList = new ArrayList<String>();
			for (int i =0 ; i< writeOpticalSet.toArray().length;i++){
				String str = writeOpticalSet.toArray()[i].toString();
				writeOpticalSetList.add(str);
			}
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalWay", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalToPort", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalRoute", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalRouteToPath", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalRouteToIniPoints", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertOpticalToOpticalWay", pm);
			IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertPathinfo", pm);
			OptDesignBO.dealWriteJumpFibersByOptCuids(writeOpticalWayList,true);
			OptDesignBO.updateDfportToTraph(writeOpticalSetList);
			//将attemp_traph_jump_fiber的数据同步到jump_fiber中
//			addJumpFiber(writeOpticalSet);
			//创建跳纤
			createJumpFiber(jumpMap);
		}
		//清空调度中的信息
		if(!writeOpticalWayList.isEmpty()){
			pm.clear();
			pm.put("attempOpticalCuidList", writeOpticalWayList);
			//删除原有光路及其关联表
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOptical", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOpticalRouteToIniPoints", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOpticalRouteToPath", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOpticalRoute", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOpticalToPort", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteAttOpticalWay", pm);
			IbatisResDAO.getSqlMapClientTemplate().delete(this.sqlMap + ".deleteOdfPortToTraph", pm);
			FiberOptDealBO.deleteOpticalToOpticalWay(writeOpticalWayList);
			OptDesignBO.deletePathInfos(writeOpticalWayList);
		}
	}
	private void createJumpFiber(Map jumpMap){
		if(jumpMap!=null&&jumpMap.size()>0){
			Iterator it = jumpMap.keySet().iterator();
			while(it.hasNext()){
				String odfCuid = (String) it.next();
				if(StringUtils.isNotEmpty(odfCuid)&&jumpMap.get(odfCuid)!=null){
					String ptpCuid=(String)jumpMap.get(odfCuid);
					if(StringUtils.isNotEmpty(ptpCuid)){
						if(ptpCuid.indexOf("PTP-")<0){
							String temp=ptpCuid;
							ptpCuid=odfCuid;
							odfCuid=temp;
						}
						String textCuid = CUIDHexGenerator.getInstance().generate("JUMP_FIBER");
						Record textRecord = new Record("JUMP_FIBER");
						textRecord.addColValue("CUID", textCuid);
						textRecord.addColSqlValue("OBJECTID", textRecord.getObjectIdSql());
						textRecord.addColValue("GT_VERSION", 0);
						textRecord.addColValue("OBJECT_TYPE_CODE", 12006);
						textRecord.addColValue("RELATED_SEG_CUID", "");
						textRecord.addColValue("IS_FIXED", 0);
						textRecord.addColValue("RELATED_SYSTEM_CUID","");
						textRecord.addColSqlValue("RELATED_SITE_CUID","SELECT RELATED_SITE_CUID FROM TRANS_ELEMENT WHERE CUID IN( SELECT RELATED_NE_CUID FROM PTP WHERE CUID='"+ptpCuid+"')");
						textRecord.addColValue("DEST_POINT_CUID",odfCuid);
						textRecord.addColValue("ORIG_POINT_CUID",ptpCuid);
						textRecord.addColSqlValue("ORIG_EQP_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID ='"+ptpCuid+"'");
						String sql="";
						String table="";
						if(odfCuid.indexOf("ODFPORT")==0){
							textRecord.addColSqlValue("DEST_EQP_CUID","SELECT RELATED_DEVICE_CUID FROM ODFPORT WHERE CUID='"+odfCuid+"'");
							sql="SELECT LABEL_CN FROM ODFPORT WHERE CUID='"+odfCuid+"'";
							table="ODFPORT";
						}else if(odfCuid.indexOf("FCABPORT")==0){
							textRecord.addColSqlValue("DEST_EQP_CUID","SELECT RELATED_DEVICE_CUID FROM FCABPORT WHERE CUID='"+odfCuid+"'");
							sql="SELECT LABEL_CN FROM FCABPORT WHERE CUID='"+odfCuid+"'";
							table="FCABPORT";
						}else if(odfCuid.indexOf("FIBER_DP_PORT")==0){
							textRecord.addColSqlValue("DEST_EQP_CUID","SELECT RELATED_DEVICE_CUID FROM FIBER_DP_PORT WHERE CUID='"+odfCuid+"'");
							sql="SELECT LABEL_CN FROM FIBER_DP_PORT WHERE CUID='"+odfCuid+"'";
							table="FIBER_DP_PORT";
						}else if(odfCuid.indexOf("FIBER_JOINT_POINT")==0){
							textRecord.addColSqlValue("DEST_EQP_CUID","SELECT RELATED_DEVICE_CUID FROM FIBER_JOINT_POINT WHERE CUID='"+odfCuid+"'");
							sql="SELECT LABEL_CN FROM FIBER_JOINT_POINT WHERE CUID='"+odfCuid+"'";
							table="FIBER_JOINT_POINT";
						}
						
						textRecord.addColValue("OWNERSHIP",1);
						textRecord.addColValue("PURPOSE",1);
						textRecord.addColValue("MAINT_MODE",1);
						textRecord.addColSqlValue("LABEL_CN","SELECT (SELECT LABEL_CN FROM PTP WHERE CUID='"+ptpCuid+"')||'=>'||("+sql+") FROM DUAL");
						textRecord.addColValue("DIRECTION",2);
						textRecord.addColValue("CREATE_TIME", new Date());
						textRecord.addColValue("LAST_MODIFY_TIME", new Date());
						this.IbatisResDAO.insertDynamicTable(textRecord);
						this.IbatisResDAO.updateSql("UPDATE PTP SET IS_CONN_STATE=1 WHERE CUID='"+ptpCuid+"'");
						this.IbatisResDAO.updateSql("UPDATE "+table+" SET IS_CONNECTED=1 WHERE CUID='"+odfCuid+"'");
					}
				}
			}
		}
	}
	
	/**
	 * 释放调度中光路信息
	 * @param ac
	 * @param attempLogAction
	 */
	public void releaseAttempOpticalWay(ServiceActionContext ac, List<String> serviceCuidList){
		if(serviceCuidList != null && serviceCuidList.size() > 0) {
			Map pm = new HashMap();
			pm.put("cuidList", serviceCuidList);
			List<Map<String, Object>> attempOpticalWayList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttempOpticalWay", pm);
			List<String> relatedServiceList = new ArrayList<String>();
			List<String> outIdList = new ArrayList<String>();
			for(Map<String, Object> attempOpticalWay : attempOpticalWayList) {
				String relatedServiceCuid = IbatisDAOHelper.getStringValue(attempOpticalWay, "RELATED_SERVICE_CUID");
				if(StringUtils.isNotBlank(relatedServiceCuid)) {
					relatedServiceList.add(relatedServiceCuid);
				}
				String outId = IbatisDAOHelper.getStringValue(attempOpticalWay, "OTHER_BUSSINESS_CUID");
				if(StringUtils.isNotBlank(outId)) {
					outIdList.add(outId);
				}
			}
			logger.debug("关联存量光路"+relatedServiceList.size()+"条");
			if(relatedServiceList.size() > 0) {
				Map<String, Object> pm2 = new HashMap<String, Object>();
				pm2.put("cuidList", relatedServiceList);
				pm2.put("scheduleState", SheetConstants.SCHEDULE_STATE_END);
				IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOpticalWayScheduleState", pm2);
				logger.debug("修改存量光路状态为可调度");
			}
			if(outIdList.isEmpty()){
				//删除接口明细数据
				FacedeOrderMaintainBO.deleteIfaceDetail(ac, outIdList);
			}
			
			this.clearService(serviceCuidList);
			//恢复调整前OPTICAL信息
			for(Map<String, Object> attempOpticalWay : attempOpticalWayList) {
				String relatedServiceCuid = IbatisDAOHelper.getStringValue(attempOpticalWay, "RELATED_SERVICE_CUID");
				if(StringUtils.isNotBlank(relatedServiceCuid)) {
					logger.debug("恢复调整前OPTICAL信息");
					Map<String, Object>  mp= new HashMap<String, Object>();
					mp.put("cuid", relatedServiceCuid);
					IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOldOptical", mp);
				}
			}
			
		}else {
			logger.warn("删除调度中光路，但输入的光路CUID集合长度小于1");
		}
	}
	
	/**
	 * 释放调度中光路信息
	 * @param ac
	 * @param attempLogAction
	 */
	public void deleteAttempOpticalWay(ServiceActionContext ac, List<String> serviceCuidList){
		if(serviceCuidList != null && serviceCuidList.size() > 0) {
			logger.debug("清空定单明细关联的资源ID");
			FacedeOrderMaintainBO.clearOrderDetailByService(ac, serviceCuidList, this.getServiceTableName());
			
			Map pm = new HashMap();
			pm.put("cuidList", serviceCuidList);
			List<Map<String, Object>> attempOpticalWayList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttempOpticalWay", pm);
			List<String> relatedServiceList = new ArrayList<String>();
			for(Map<String, Object> attempOpticalWay : attempOpticalWayList) {
				String relatedServiceCuid = IbatisDAOHelper.getStringValue(attempOpticalWay, "RELATED_SERVICE_CUID");
				if(StringUtils.isNotBlank(relatedServiceCuid)) {
					relatedServiceList.add(relatedServiceCuid);

				}
			}
			logger.debug("关联存量光路"+relatedServiceList.size()+"条");
			if(relatedServiceList.size() > 0) {
				Map<String, Object> pm2 = new HashMap<String, Object>();
				pm2.put("cuidList", relatedServiceList);
				pm2.put("scheduleState", SheetConstants.SCHEDULE_STATE_END);
				IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOpticalWayScheduleState", pm2);
				logger.debug("修改存量光路状态为可调度");
			}
			
			this.clearService(serviceCuidList);
			//恢复调整前OPTICAL信息
			for(Map<String, Object> attempOpticalWay : attempOpticalWayList) {
				String relatedServiceCuid = IbatisDAOHelper.getStringValue(attempOpticalWay, "RELATED_SERVICE_CUID");
				if(StringUtils.isNotBlank(relatedServiceCuid)) {
					logger.debug("恢复调整前OPTICAL信息");
					Map<String, Object>  mp= new HashMap<String, Object>();
					mp.put("cuid", relatedServiceCuid);
					IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOldOptical", mp);
				}
			}
			
			
			IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptWay2Task", pm);
			logger.debug("删除调度中光路路由与任务关系");
			IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptWay2TaskLink", pm);
			
			

			
		}else {
			logger.warn("删除调度中光路，但输入的光路CUID集合长度小于1");
		}
	}
	
	private void clearService(List<String> serviceCuidList){
		Map pm = new HashMap();
		pm.put("cuidList", serviceCuidList);
//		List<Map<String, Object>> attempOpticalRouteList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttOptRoute", pm);
//		logger.debug("查询调度中光路路由，结果："+attempOpticalRouteList.size()+"条");
//		List<String> routeCuidList = new ArrayList<String>();
//		for(Map<String, Object> attempOpticalRoute : attempOpticalRouteList) {
//			routeCuidList.add(IbatisDAOHelper.getStringValue(attempOpticalRoute, "CUID"));
//		}
//		//找到所有要释放的资源
//		pm.put("attempOpticalCuidList", serviceCuidList);
		List<Map<String, Object>> resList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".selectAttempOpticalCuid", pm);
		//找到所有要占用的资源
		List<Map<String, Object>> attList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".selectOpticalCuid", pm);
		//要占用状态的
		List<String> opticalUseList =new ArrayList<String>();
		//要占用状态的端口
		List<String> portUseList =new ArrayList<String>();
		//要释放状态的
		List<String> opticalList = new ArrayList<String>();
		//要释放状态的端口
		List<String> portList = new ArrayList<String>();
		for(Map<String,Object> map : resList){
			String opticalCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
			String AF_PORT_CUID = IbatisDAOHelper.getStringValue(map, "AF_PORT_CUID");
			String AS_PORT_CUID = IbatisDAOHelper.getStringValue(map, "AS_PORT_CUID");
			String ZF_PORT_CUID = IbatisDAOHelper.getStringValue(map, "ZF_PORT_CUID");
			String ZS_PORT_CUID = IbatisDAOHelper.getStringValue(map, "ZS_PORT_CUID");
			if(StringUtils.isNotBlank(opticalCuid)){
				opticalList.add(opticalCuid);
			}
			if(StringUtils.isNotBlank(AF_PORT_CUID)){
				portList.add(AF_PORT_CUID);
			}
			if(StringUtils.isNotBlank(AS_PORT_CUID)){
				portList.add(AS_PORT_CUID);
			}
			if(StringUtils.isNotBlank(ZF_PORT_CUID)){
				portList.add(ZF_PORT_CUID);
			}
			if(StringUtils.isNotBlank(ZS_PORT_CUID)){
				portList.add(ZS_PORT_CUID);
			}
		}
		for(Map<String,Object> map : attList){
			String opticalCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
			String AF_PORT_CUID = IbatisDAOHelper.getStringValue(map, "AF_PORT_CUID");
			String AS_PORT_CUID = IbatisDAOHelper.getStringValue(map, "AS_PORT_CUID");
			String ZF_PORT_CUID = IbatisDAOHelper.getStringValue(map, "ZF_PORT_CUID");
			String ZS_PORT_CUID = IbatisDAOHelper.getStringValue(map, "ZS_PORT_CUID");
			if(StringUtils.isNotBlank(opticalCuid)){
				opticalUseList.add(opticalCuid);
			}
			if(StringUtils.isNotBlank(AF_PORT_CUID)){
				portUseList.add(AF_PORT_CUID);
			}
			if(StringUtils.isNotBlank(AS_PORT_CUID)){
				portUseList.add(AS_PORT_CUID);
			}
			if(StringUtils.isNotBlank(ZF_PORT_CUID)){
				portUseList.add(ZF_PORT_CUID);
			}
			if(StringUtils.isNotBlank(ZS_PORT_CUID)){
				portUseList.add(ZS_PORT_CUID);
			}
		}
		//释放optical和fiber的状态
		if(opticalList.size()>0){
			pm.put("opticalList", opticalList);
			pm.put("state", 1);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateOpticaStatus", pm);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateFiberStatus", pm);
		}
		//释放端口状态
		if(portList.size()>0){
			pm.put("portList", portList);
			pm.put("state", 1);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updatePtpStatus", pm);
		}
		//占用optical和fiber的状态
		if(opticalUseList.size()>0){
			pm.put("opticalList", opticalUseList);
			pm.put("state", 2);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateOpticaStatus", pm);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updateFiberStatus", pm);
		}
		//占用端口状态
		if(portUseList.size()>0){
			pm.put("portList", portUseList);
			pm.put("state", 2);
			IbatisResDAO.getSqlMapClientTemplate().update(this.sqlMap + ".updatePtpStatus", pm);
		}
		OptDesignBO.deleteAttempRouteInfos(serviceCuidList);
//		pm.put("routeCuidList", routeCuidList);
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptRoute2Path", pm);
//		logger.debug("删除调度中光路路由与光纤关系");
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptIndiPoints", pm);
//		logger.debug("删除调度中光路路由与转接点关系");
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOpt", pm);
//		logger.debug("删除调度中光路光纤");
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptRoute", pm);
//		logger.debug("删除调度中光路路由");
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptWay2Port", pm);
//		logger.debug("删除调度中光路与端口关系");
		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptWay", pm);
		logger.debug("删除调度中光路");

//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptDgnTextPath",pm);
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptDgnPath",pm);
//		logger.debug("删除调度中的草稿");

		//删除attemp_traph_jump_fiber中数据
//		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttJumpFiber", pm);
//		logger.debug("删除调度中跳纤信息");
		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptDgnTextPath",pm);
		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttOptDgnPath",pm);
		logger.debug("删除调度中的草稿");

	}
	
	/**
	 * 写入光路日志
	 * 
	 * @param attempLogAction
	 * @throws Exception 
	 */
	public void writeServiceLog(AttempLog action) {
		try{
			List<String> attempOptCuidList = action.getServiceCuidList();
			Map pm = new HashMap();
			pm.put("taskId", action.getRelatedTaskCuid());
			pm.put("attempOptCuidList", attempOptCuidList);
			pm.put("REMARK", action.getActionCodeName());
			pm.put("ACTION_CODE", action.getActionCode());
			this.IbatisResDAO.getSqlMapClientTemplate().insert(this.sqlMap + ".insertTActOpticalWayLog", pm);
			logger.info("---------------attempOptCuidList :"+attempOptCuidList);
			List<Record> pathInfoList = new ArrayList<Record>();
			List<Map<String,Object>> pfList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".getPathinfoList", pm);
			if(pfList !=null && pfList.size()>0){
				for(String optCuid:attempOptCuidList){
					for(Map map:pfList){
						byte[] contnt = (byte[])map.get("CONTENT");
						String attOptCuid = (String)map.get("RELATED_SERVICE_CUID");
						if(optCuid.equals(attOptCuid)){
							Record pathInfo = new Record("PATHINFO");
							pathInfo.addColValue("CUID",CUIDHexGenerator.getInstance().generate("PATHINFO"));
							pathInfo.addColSqlValue("RELATED_SERVICE_CUID", "SELECT CUID FROM T_ATTEMP_OPTICAL_WAY_LOG WHERE RELATED_TASK_CUID = '"
									+action.getRelatedTaskCuid()+"' AND (RELATED_ATTEMP_SERVICE_CUID = '"
									+optCuid+"' AND (STATUS = '调整后' OR STATUS = '新增' OR STATUS = '停闭')) OR (RELATED_SERVICE_CUID = '"+optCuid
									+"' AND STATUS = '调整前')");
							pathInfo.addColValue("CONTENT", contnt);
							pathInfo.addColValue("SERVICE_TYPE", 2L);
							pathInfoList.add(pathInfo);
						}
					}
				}
				IbatisResDAO.insertDynamicTableBatch(pathInfoList);
			}
		}catch(Exception e){
			logger.info("方法writeServiceLog报错，原因="+e);
			throw new RuntimeException("调用综资接口出错,"+e.getMessage());
		}
	}
	
	public void writeLog(ServiceActionContext ac,String action,String taskId, String remark){
		if(StringUtils.isNotEmpty(taskId)){
			Log log = new Log();
			log.setAction(action);
			log.setRelatedTaskCuid(taskId);
			TaskInst taskInst = ProcessBO.getTaskInstByTaskId(taskId);
			log.setRelatedSheetCuid(taskInst.getRelatedSheetCuid());
			log.setRemark(remark);
			ProcessBO.log(ac, log);
		}
	}
	public Map validateIsAbleToArchive(List attempOpticalWayCuidList){
		Map param = new HashMap();
		param.put("attempOpticalWayCuidList",attempOpticalWayCuidList);
		//调前电路cuid的List
		List<Map<String,Object>> attempList=getValidateParam(param);
		List<Map<String,Object>> reList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateOpticalData", param);
		//验证光纤两端是否上架 
		List<String> notHasOnOdfOpticalList=new ArrayList<String>();
//		List<String> notHasOnOdfOpticalList=validateAttempOpticalHasOnOdf(attempList);
		//验证光纤是否被重复占用 
		List<String> opticalUsedByOtherList=validateOpticalHasUseByOtherWay(attempList,reList);
		//验证odf端子是否多次上架
		List<String> odfUsedByOtherList=new ArrayList<String>();
//		List<String> odfUsedByOtherList=validateOdfPortUseByOther(attempList);
		//验证两端上架的端子是否做过跳纤，端口是否做过跳纤 如果都没有则需要回写的时候添加 如果做过了需要对端子
		 //验证端口是否存在
		List<String> PortNotExistList=new ArrayList<String>();
//	    List PortNotExistList= validatePortExistList(attempList);
		//先验证是否端子端口没有跳纤
		Map<String,String> jumpMap=new HashMap<String,String>();
		List<String> odfCannotFindPtpList=validateOdfAndPtpList(attempList,jumpMap);
		//验证端口是否被重复使用
		List<String> portUseByOtherList=validataPtpUseByOther(attempList);
		
		//验证端口是否重复创建跳纤
		Map<String,List<String>> portJumpFiberMap=portHasFiber(jumpMap);
		List<String> portJumpFiberCuid=portJumpFiberMap.get("cuid");
		List<String> portJumpFiberName=portJumpFiberMap.get("name");
		
		Map result = new HashMap();
		result.put("notHasOnOdfOpticalList", notHasOnOdfOpticalList);
		result.put("opticalUsedByOtherList", opticalUsedByOtherList);
		result.put("odfUsedByOtherList", odfUsedByOtherList);
		result.put("odfCannotFindPtpList", odfCannotFindPtpList);
		result.put("portUseByOtherList", portUseByOtherList);
		result.put("jumpMap", jumpMap);
		result.put("PortNotExistList", PortNotExistList);
		result.put("portJumpFiberCuid", portJumpFiberCuid);
		result.put("portJumpFiberName", portJumpFiberName);
		return result;
	}
	
	private Map<String,List<String>> portHasFiber(Map<String,String> jumpMap){
		Iterator it = jumpMap.keySet().iterator();
		List<String> list=new ArrayList<String>();
		Map<String,List<String>> map=new HashMap<String,List<String>>();
		List<String> nameList=new ArrayList<String>();
		List<String> cuidList=new ArrayList<String>();
		map.put("name", nameList);
		map.put("cuid", cuidList);
		while(it.hasNext()){
			String odfCuid = (String) it.next();
			String ptpCuid=jumpMap.get(odfCuid);
			if(!list.contains(odfCuid)){
				list.add(ptpCuid);
			}
		}
		if(list.size()>0){
			Map param = new HashMap();
			param.put("usePortList",list);
			List<Map<String,Object>> reList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".selectJumpFiberByPort", param);
			for(int i=0;i<reList.size();i++){
				Map data=reList.get(i);
				String labelCn=IbatisDAOHelper.getStringValue(data, "LABEL_CN");
				String cuid=IbatisDAOHelper.getStringValue(data, "CUID");
				String origPort=IbatisDAOHelper.getStringValue(data, "ORIG_POINT_CUID");
				String destPort=IbatisDAOHelper.getStringValue(data, "DEST_POINT_CUID");
				if(origPort!=null&&origPort.indexOf("ODF")>=0){
					if(jumpMap.containsKey(origPort)){
						if(!destPort.equals(jumpMap.get(origPort))){
							nameList.add(labelCn);
							cuidList.add(cuid);
						}
					}else{
						nameList.add(labelCn);
						cuidList.add(cuid);
					}
				}else if(destPort!=null&&destPort.indexOf("ODF")>=0){
					if(jumpMap.containsKey(destPort)){
						if(!origPort.equals(jumpMap.get(destPort))){
							nameList.add(labelCn);
							cuidList.add(cuid);
						}
					}else{
						nameList.add(labelCn);
						cuidList.add(cuid);
					}
				}
			}
		}
		return map;
	}
	/**
	 * 校验电路是否能够归档
	 * @param taskId
	 */
	public Map validateIsAbleToArchive(List attempOpticalWayCuidList,String taskId){
		Map param = new HashMap();
		param.put("attempOpticalWayCuidList",attempOpticalWayCuidList);
		param.put("taskId", taskId);
		//调前电路cuid的List
		List<Map<String,Object>> attempList=getValidateParam(param);
		List<Map<String,Object>> reList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateOpticalData", param);
		//验证光纤两端是否上架 
		List notHasOnOdfOpticalList=new ArrayList();
//		List<String> notHasOnOdfOpticalList=validateAttempOpticalHasOnOdf(attempList);
		//验证光纤是否被重复占用 
		List<String> opticalUsedByOtherList=validateOpticalHasUseByOtherWay(attempList,reList);
		//验证odf端子是否多次上架
		List odfUsedByOtherList=new ArrayList();
//		List<String> odfUsedByOtherList=validateOdfPortUseByOther(attempList);
		//验证两端上架的端子是否做过跳纤，端口是否做过跳纤 如果都没有则需要回写的时候添加 如果做过了需要对端子
		 //验证端口是否存在
		List PortNotExistList=new ArrayList();
//	    List PortNotExistList= validatePortExistList(attempList);
		//先验证是否端子端口没有跳纤
		Map<String,String> jumpMap=new HashMap<String,String>();
		List<String> odfCannotFindPtpList=validateOdfAndPtpList(attempList,jumpMap);
		//验证端口是否被重复使用
		List<String> portUseByOtherList=validataPtpUseByOther(attempList);
		
		Map result = new HashMap();
		result.put("notHasOnOdfOpticalList", notHasOnOdfOpticalList);
		result.put("opticalUsedByOtherList", opticalUsedByOtherList);
		result.put("odfUsedByOtherList", odfUsedByOtherList);
		result.put("odfCannotFindPtpList", odfCannotFindPtpList);
		result.put("portUseByOtherList", portUseByOtherList);
		result.put("jumpMap", jumpMap);
		result.put("PortNotExistList", PortNotExistList);
		return result;
	}
	 private List<String> validatePortExistList(List<Map<String, Object>> attempList){
		 List<String> resutlList = new ArrayList<String>();
		 List<String> attempOpticalWayCuidList=new ArrayList<String>();		  
		 for(int i=(attempList.size()-1);i>=0;i--){
		    Map map= attempList.get(i);
		    String opticalWayType=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_EXT_TYPE");
//		    String opticalNum = IbatisDAOHelper.getStringValue(map, "OPTICAL_NUM");
		    //仅业务类型为传输系统的才做效验
		    if((opticalWayType!=null)&&(opticalWayType.equals("2"))){
		    //只有光纤数量为2芯时才做校验
//		    if((opticalNum!=null)&&(opticalNum.equals("2"))){
			   String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			   attempOpticalWayCuidList.add(opticalWayCuid);
		    }
		  }
		  List<String> leftList=new ArrayList<String>();
		  List<String> bothList=new ArrayList<String>();
		  List<String> rightList=new ArrayList<String>();
		  Map param = new HashMap();
		  param.put("attempOpticalWayCuidList", attempOpticalWayCuidList);
		  //验证光路起止路由段是否为文本段
		  List<Map<String, Object>> routeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("OptMaintain" + ".selectRouteSeg", param);
		  for(String cuid:attempOpticalWayCuidList){
			  List<Boolean> tempList=new ArrayList<Boolean>();
			  boolean left=false;
			  boolean right=false;
			  for(int i=0;i<routeList.size();i++){
				  Map map=routeList.get(i);
				  if(IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID").equals(cuid)){
					  int flag=IbatisDAOHelper.getIntValue(map, "IS_TEXT");
					  if(flag==0){
						  tempList.add(true);
					  }else{
						  tempList.add(false);
					  }
				  }
			  }
			  if(tempList.size()>0){
				  if(tempList.get(0)){
					  left=true;
				  }
				  if(tempList.get(tempList.size()-1)){
					  right=true;
				  }
				  if(left&&right){
					  bothList.add(cuid);
				  }else if(left){
					  leftList.add(cuid);
				  }else if(right){
					  rightList.add(cuid);
				  }
			  }
		  }
		  
		  Map<String,List> portAZtype=new HashMap<String,List>();
		  List<Map<String, Object>> reList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("OptMaintain" + ".validatePortExist", param);
		  for(String cuid:leftList){
			  boolean flag=true;
			  String name="";
			  for (Map<String, Object> map :reList){
				  String attempOpticalWayName=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				  String portAz=IbatisDAOHelper.getStringValue(map, "PORTAZTYPE");
				  String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_CUID");
				  if(opticalWayCuid.equals(cuid)){
					  name=attempOpticalWayName;
					  if(StringUtils.isNotEmpty(portAz)&&portAz.equals("1")){
						  flag=false;
					  }
				  }
			  }
			  if(flag){
				  String str="调度光路["+name+"]的A侧端口缺失\\n";
				    if(!resutlList.contains(str)){
						resutlList.add(str);
				    }
			  }
		  }
		  for(String cuid:rightList){
			  boolean flag=true;
			  String name="";
			  for (Map<String, Object> map :reList){
				  String attempOpticalWayName=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				  String portAz=IbatisDAOHelper.getStringValue(map, "PORTAZTYPE");
				  String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_CUID");
				  if(opticalWayCuid.equals(cuid)){
					  name=attempOpticalWayName;
					  if(StringUtils.isNotEmpty(portAz)&&portAz.equals("2")){
						  flag=false;
					  }
				  }
			  }
			  if(flag){
				  String str="调度光路["+name+"]的A侧端口缺失\\n";
				    if(!resutlList.contains(str)){
						resutlList.add(str);
				    }
			  }
		  }
		  for(String cuid:bothList){
			  boolean flagA=true;
			  boolean flagZ=true;
			  String name="";
			  for (Map<String, Object> map :reList){
				  String attempOpticalWayName=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				  String portAz=IbatisDAOHelper.getStringValue(map, "PORTAZTYPE");
				  String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_CUID");
				  String siteCuidA = IbatisDAOHelper.getStringValue(map, "SITE_CUID_A");
				  String siteCuidZ = IbatisDAOHelper.getStringValue(map, "SITE_CUID_Z");
				  if(opticalWayCuid.equals(cuid)){
					  name=attempOpticalWayName;
					  if((StringUtils.isNotEmpty(portAz)&&portAz.equals("1")) ||!siteCuidA.contains("SITE")){
						  flagA=false;
					  }
					  if((StringUtils.isNotEmpty(portAz)&&portAz.equals("2")) || !siteCuidZ.contains("SITE")){
						  flagZ=false;
					  }
				  }
			  }
			  if(flagA&&flagZ){
				  String str="调度光路["+name+"]的两侧端口缺失\\n";
				    if(!resutlList.contains(str)){
						resutlList.add(str);
				    }
			  }
			  if(flagA){
				  String str="调度光路["+name+"]的A侧端口缺失\\n";
				    if(!resutlList.contains(str)){
						resutlList.add(str);
				    }
			  }
			  if(flagZ){
				  String str="调度光路["+name+"]的Z侧端口缺失\\n";
				    if(!resutlList.contains(str)){
						resutlList.add(str);
				    }
			  }
		  }
		  return resutlList;
	  }
	private List<String> validatePtpAndOdfPortCanLine(List<Map<String,Object>> attempList){
		List<String> resutlList = new ArrayList<String>();
		List<String> portList=new ArrayList<String>();
		Map<String,String> useMap=new HashMap<String,String>();
		List<Map<String,Object>> useList=new ArrayList<Map<String,Object>>();
		Map<String,Map<String,String>> opticalMap=new HashMap<String,Map<String,String>>();
		for(Map<String,Object> map:attempList){
			String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			int azType=IbatisDAOHelper.getIntValue(map, "PORT_AZ_TYPE");
			String portCuid=IbatisDAOHelper.getStringValue(map, "PORT_CUID");
			int index=IbatisDAOHelper.getIntValue(map, "INDEX_PORT");
			String origOdfPortCuid=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destOdfPortCuid=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			String az="";
			if(azType==1){
				az="A";
			}else{
				az="Z";
			}
			String in="";
			if(index==0){
				in="F";
			}else{
				in="S";
			}
			if(StringUtils.isNotEmpty(portCuid)&&StringUtils.isNotEmpty(opticalWayCuid)){
				if(opticalMap.containsKey(opticalWayCuid)){
					Map<String,String> dataMap=opticalMap.get(opticalWayCuid);
					dataMap.put(az+in, portCuid);
				}else{
					Map<String,String> dataMap=new HashMap<String,String>();
					dataMap.put(az+in, portCuid);
					opticalMap.put(opticalWayCuid, dataMap);
				}
			}
		}
		int maxSeg=0;
		for(Map<String,Object> map:attempList){
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(seg>maxSeg){
				maxSeg=seg;
			}
		}
		for(Map<String,Object> map:attempList){
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_CUID");
			String origCuid=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destCuid=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(StringUtils.isNotEmpty(opticalCuid)&&(seg==0||seg==maxSeg)){
				if(maxSeg==0){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(origCuid)){
							dataMap.put("aOdfPortCuid", origCuid);
						}
						if(StringUtils.isNotEmpty(destCuid)){
							dataMap.put("zOdfPortCuid", destCuid);
						}
						useList.add(dataMap);
					}
				}else if(seg==0){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(origCuid)){
							dataMap.put("aOdfPortCuid", origCuid);
						}
						useList.add(dataMap);
					}
				}else if(seg==maxSeg){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(destCuid)){
							dataMap.put("zOdfPortCuid", destCuid);
						}
						useList.add(dataMap);
					}
				}
			}
		}
		
		
		return resutlList;
	}
	private List<Map<String,Object>> getValidateParam(Map param){
		List<String> opticalWayCuidList=new ArrayList<String>();
		List<Map<String,Object>> attempList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateAttempOpticalData", param);
		//停闭的单子不再验证   
		//光路应用类型为 pon光路时不再验证
		List<Map<String,Object>> returnList=new ArrayList<Map<String,Object>> ();
		for(Map<String,Object> map:attempList){
			String scheduleType=IbatisDAOHelper.getStringValue(map, "SCHEDULE_TYPE");
			String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_CUID");
//			String applyType = IbatisDAOHelper.getStringValue(map, "APPLY_TYPE");
			if(StringUtils.isNotEmpty(scheduleType)&&(scheduleType.equals("2")||scheduleType.equals("3"))&&StringUtils.isNotEmpty(opticalWayCuid)){//停闭调整
				if(!opticalWayCuidList.contains(opticalWayCuid)){
					opticalWayCuidList.add(opticalWayCuid);
				}
			}
			if((StringUtils.isNotEmpty(scheduleType)&&!scheduleType.equals("2"))/* && !("1".equals(applyType))*/){
				returnList.add(map);
			}
		}
		param.put("opticalWayCuidList",opticalWayCuidList);
		return returnList;
	}
	/**
	 * 校验光路是否能够归档
	 * @param taskId
	 */
	public Map validateIsAbleToSubmit(List attempOpticalWayCuidList){
		Map param = new HashMap();
		Map result = new HashMap();
		List<String> notHasOnOdfOpticalList = new ArrayList<String>();
		List<String> opticalUsedByOtherList = new ArrayList<String>();
		List<String> odfUsedByOtherList = new ArrayList<String>();
		List<String> odfCannotFindPtpList = new ArrayList<String>();
		List<String> portUseByOtherList = new ArrayList<String>();
		List PortNotExistList = new ArrayList();
		List<String> isWholeRouteList = new ArrayList<String>();
		if (StringUtils.isEmpty(SysProperty.getInstance().getValue("isodnCfg"))||!SysProperty.getInstance().getValue("isodnCfg").trim().equals("true")){
			param.put("attempOpticalWayCuidList",attempOpticalWayCuidList);
			//调前电路cuid的List
			List<Map<String,Object>> attempList=getValidateParam(param);
			List<Map<String,Object>> reList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateOpticalData", param);
			//验证端口是否被重复使用
			 portUseByOtherList=validataPtpUseByOther(attempList);
			//验证光纤两端是否上架 
			notHasOnOdfOpticalList=validateAttempOpticalHasOnOdf(attempList);
			//验证光纤是否被重复占用 
			opticalUsedByOtherList=validateOpticalHasUseByOtherWay(attempList,reList);
			//验证odf端子是否多次上架
			odfUsedByOtherList=validateOdfPortUseByOther(attempList);
			 //验证端口是否存在
		    PortNotExistList= validatePortExistList(attempList);
			//验证端口是否能够找到能够找到上架的odf端子信息 算法
			odfCannotFindPtpList=validateOdfCannotFindPtpList(attempList);				
			//验证光路由路由完整 
			isWholeRouteList=validateisWholeRoute(param);
		}
		result.put("notHasOnOdfOpticalList", notHasOnOdfOpticalList);
		result.put("opticalUsedByOtherList", opticalUsedByOtherList);
		result.put("odfUsedByOtherList", odfUsedByOtherList);
		result.put("odfCannotFindPtpList", odfCannotFindPtpList);
		result.put("portUseByOtherList", portUseByOtherList);
		result.put("PortNotExistList", PortNotExistList);
		result.put("isWholeRouteList", isWholeRouteList);
		return result;
	}
	private List<String> validataPtpUseByOther(List<Map<String,Object>> attempList){
		List<String> resutlList = new ArrayList<String>();
		Map<String,String> attempOpticalWayMap=new HashMap<String,String>();
		Map<String,String> portToAttempOpticalWayMap=new HashMap<String,String>();
		List<String> usePortList=new ArrayList<String>();
		for(Map<String,Object> map:attempList){
			String attempOpticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			String attempOpticalWayName=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_NAME");
			String portCuid=IbatisDAOHelper.getStringValue(map, "PORT_CUID");
			if(StringUtils.isNotEmpty(attempOpticalWayCuid)&&StringUtils.isNotEmpty(attempOpticalWayName)){
				attempOpticalWayMap.put(attempOpticalWayCuid, attempOpticalWayName);
				if(StringUtils.isNotEmpty(portCuid)){
					if(portToAttempOpticalWayMap.containsKey(portCuid)){
						if(!attempOpticalWayCuid.equals(portToAttempOpticalWayMap.get(portCuid))){
							String str="调度光路["+attempOpticalWayName+"]的端口被光路["+attempOpticalWayMap.get(portToAttempOpticalWayMap.get(portCuid))+"]使用";
							if(!resutlList.contains(str))
							resutlList.add(str);
						}
					}else{
						portToAttempOpticalWayMap.put(portCuid, attempOpticalWayCuid);
					}
				}
			}
		}
		//查找端口使用情况
		Map param=new HashMap();
		param.put("usePortList", usePortList);
		List<Map<String, Object>> portWithOpticalList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".validatePtpWithOptical", param);
		for(Map<String, Object> map:portWithOpticalList){
			String portCuid=IbatisDAOHelper.getStringValue(map, "PORT_CUID");
			String attempOpticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			String wayName=IbatisDAOHelper.getStringValue(map, "WAY_NAME");
			String portName=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
			if(StringUtils.isNotEmpty(portCuid)){
				if(portToAttempOpticalWayMap.containsKey(portCuid)){
					String oldWayCuid=portToAttempOpticalWayMap.get(portCuid);
					if(StringUtils.isNotEmpty(oldWayCuid)){
						if(!oldWayCuid.equals(attempOpticalWayCuid)){
							resutlList.add("调度光路["+attempOpticalWayMap.get(oldWayCuid)+"]的端口"+portName+"被光路["+wayName+"]使用");
						}
					}
				}
			}
		}
		//端口只能在在
		return resutlList;
	}
	//验证端口是否能够找到能够找到上架的odf端子信息 算法
	private List<String> validateOdfCannotFindPtpList(List<Map<String,Object>> attempList){
		List<String> resutlList = new ArrayList<String>();
		List<Map<String,Object>> useList=new ArrayList<Map<String,Object>>();
		Map<String,String> useMap=new HashMap<String,String>();
		//保存光路的起止端口
		Map<String,Map<String,String>> opticalWayPortMap=new HashMap<String,Map<String,String>>();
		//保存光纤和光路关联关系
		Map<String,String> opticalToOpticalWayMap=new HashMap<String,String>();
		//保存光路的名称
		Map<String,String> opticalWayMap=new HashMap<String,String>();
		//保存光纤的名称
		Map<String,String> opticalMap=new HashMap<String,String>();
		for(int i=(attempList.size()-1);i>=0;i--){
		    Map map= attempList.get(i);
		    String opticalWayType=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_EXT_TYPE");
//		    String opticalNum=IbatisDAOHelper.getStringValue(map, "OPTICAL_NUM");
		    /*仅业务类型为传输系统的才做效验*/
		    if((opticalWayType!=null)&&(!opticalWayType.equals("2"))){
		    /*仅光纤数量为2芯的才做效验*/
//		    if((opticalNum!=null)&&(!opticalNum.equals("2"))){
		    	attempList.remove(i);
		    }
		  }
		
		for(Map<String,Object> map:attempList){
			String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			int azType=IbatisDAOHelper.getIntValue(map, "PORT_AZ_TYPE");
			String portCuid=IbatisDAOHelper.getStringValue(map, "PORT_CUID");
			int index=IbatisDAOHelper.getIntValue(map, "INDEX_PORT");
			String attempOpticalCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_CUID");
			String opticalWayName=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_NAME");
			String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
			opticalToOpticalWayMap.put(attempOpticalCuid, opticalWayCuid);
			opticalToOpticalWayMap.put(attempOpticalCuid, opticalWayCuid);
			opticalWayMap.put(opticalWayCuid, opticalName);
			opticalMap.put(attempOpticalCuid, opticalName);
			String az="";
			if(azType==1){
				az="A";
			}else{
				az="Z";
			}
			String in="";
			if(index==0){
				in="F";
			}else{
				in="S";
			}
			if(StringUtils.isNotEmpty(portCuid)&&StringUtils.isNotEmpty(opticalWayCuid)){
				if(opticalWayPortMap.containsKey(opticalWayCuid)){
					Map<String,String> dataMap=opticalWayPortMap.get(opticalWayCuid);
					dataMap.put(az+in, portCuid);
				}else{
					Map<String,String> dataMap=new HashMap<String,String>();
					dataMap.put(az+in, portCuid);
					opticalWayPortMap.put(opticalWayCuid, dataMap);
				}
			}
		}
		
		//只计算开始和结束段的端子端口数据
		int maxSeg=0;
		for(Map<String,Object> map:attempList){
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(seg>maxSeg){
				maxSeg=seg;
			}
		}
		for(Map<String,Object> map:attempList){
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_CUID");
			String origCuid=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destCuid=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			String opCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(StringUtils.isNotEmpty(opCuid)){
				if(StringUtils.isNotEmpty(opticalCuid)&&(seg==0||seg==maxSeg)){
					if(maxSeg==0){
						if(!useMap.containsKey(opticalCuid)){
							useMap.put(opticalCuid, opticalCuid);
							Map<String,Object> dataMap=new HashMap<String,Object>();
							dataMap.put("value", opticalCuid);
							if(StringUtils.isNotEmpty(origCuid)){
								dataMap.put("aOdfPortCuid", origCuid);
							}
							if(StringUtils.isNotEmpty(destCuid)){
								dataMap.put("zOdfPortCuid", destCuid);
							}
							useList.add(dataMap);
						}
					}else if(seg==0){
						if(!useMap.containsKey(opticalCuid)){
							useMap.put(opticalCuid, opticalCuid);
							Map<String,Object> dataMap=new HashMap<String,Object>();
							dataMap.put("value", opticalCuid);
							if(StringUtils.isNotEmpty(origCuid)){
								dataMap.put("aOdfPortCuid", origCuid);
							}
							useList.add(dataMap);
						}
					}else if(seg==maxSeg){
						if(!useMap.containsKey(opticalCuid)){
							useMap.put(opticalCuid, opticalCuid);
							Map<String,Object> dataMap=new HashMap<String,Object>();
							dataMap.put("value", opticalCuid);
							if(StringUtils.isNotEmpty(destCuid)){
								dataMap.put("zOdfPortCuid", destCuid);
							}
							useList.add(dataMap);
						}
					}
				}
			}
		}
		if(useList.size()>0){
			List<Map<String,String>> rList=OptDesignBO.getPtpByPort(useList);
			Iterator it = useMap.keySet().iterator();
			while(it.hasNext()){
				String opticalCuid = (String) it.next();
				if(StringUtils.isNotEmpty(opticalCuid)){
					if(rList!=null&&rList.size()>0){
						for(Map<String,String> map:rList){
							String cuid=IbatisDAOHelper.getStringValue(map, "opticalCuid");
							if(StringUtils.isNotEmpty(cuid)){
								if(cuid.equals(opticalCuid)){
									//得到光路的端口
									if(opticalToOpticalWayMap.containsKey(cuid)){
										String opticalWayCuid=opticalToOpticalWayMap.get(cuid);
										if(opticalWayPortMap.containsKey(opticalWayCuid)){
											Map<String,String> datamap=opticalWayPortMap.get(opticalWayCuid);
											String afPotyCuid=IbatisDAOHelper.getStringValue(datamap, "AF");
											String asPotyCuid=IbatisDAOHelper.getStringValue(datamap, "AS");
											String zfPotyCuid=IbatisDAOHelper.getStringValue(datamap, "ZF");
											String zsPotyCuid=IbatisDAOHelper.getStringValue(datamap, "ZS");
											String aCuid=IbatisDAOHelper.getStringValue(map, "origPtp");
											String zCuid=IbatisDAOHelper.getStringValue(map, "destPtp");
											boolean aFlag=true;
											boolean zFlag=true;
											for(Map<String,Object> dataMap:useList){
												if(dataMap.containsValue(cuid)){
													if(!dataMap.containsKey("zOdfPortCuid")){
														zFlag=false;
													}
													if(!dataMap.containsKey("aOdfPortCuid")){
														aFlag=false;
													}
												}
											}
											if(aFlag){
												if(StringUtils.isEmpty(aCuid)){
													resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]A端上架端子与光路起端端口没有跳纤\\n");
												}else{
													if(!aCuid.equals(afPotyCuid)&&!aCuid.equals(asPotyCuid)){
														resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]A端上架端子与光路起端端口没有跳纤\\n");
													}
												}
											}
											if(zFlag){
												if(StringUtils.isEmpty(zCuid)){
													resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]Z端上架端子与光路起端端口没有跳纤\\n");
												}else{
			
													if(!zCuid.equals(zfPotyCuid)&&!zCuid.equals(zsPotyCuid)){
														resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]Z端上架端子与光路起端端口没有跳纤\\n");
													}
												}
											}
										}
									}
								}
							}
						}
					}else{
						String opticalWayCuid=opticalToOpticalWayMap.get(opticalCuid);
						if(StringUtils.isNotEmpty(opticalWayCuid)){
							resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(opticalCuid)+"]AZ端上架端子与光路起端端口没有跳纤\\n");
						}
					}
				}
			}
		}
		return resutlList;
	}
	//验证光纤是否都已经上架
	private List<String> validateAttempOpticalHasOnOdf(List<Map<String,Object>> attempList){
		List<String> resutlList = new ArrayList<String>();
		for(Map<String,Object> map:attempList){
		    String opticalWayType=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_EXT_TYPE");
		    if((opticalWayType!=null)&&(!opticalWayType.equals("2"))){
			          continue;
		    }
			String scheduleType=IbatisDAOHelper.getStringValue(map, "SCHEDULE_TYPE");
			String opticalWayName=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_NAME");
			String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
			String origOdfpointCuid=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destOdfpointCuid=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			if(scheduleType!=null&&opticalWayName!=null&&StringUtils.isNotEmpty(opticalCuid)){
				if(scheduleType.equals("1")||scheduleType.equals("3")){
					if(origOdfpointCuid==null||origOdfpointCuid.trim().length()==0){
						if(!resutlList.contains("光路:"+opticalWayName+"的光纤:"+opticalName+"未上架\\n")){
							resutlList.add("光路:"+opticalWayName+"的光纤:"+opticalName+"未上架\\n");
						}
					}
					if(destOdfpointCuid==null||destOdfpointCuid.trim().length()==0){
						if(!resutlList.contains("光路:"+opticalWayName+"的光纤:"+opticalName+"未上架\\n")){
							resutlList.add("光路:"+opticalWayName+"的光纤:"+opticalName+"未上架\\n");
						}
					}
				}
			}
		}
		return resutlList;
	}
	//验证光纤是否被重复使用
	private List<String> validateOpticalHasUseByOtherWay(List<Map<String,Object>> attempList,List<Map<String,Object>> reList){
		List<String> resutlList = new ArrayList<String>();
		List<String> useOpticalList =new ArrayList<String>();
		Map<String,String> oMap=new HashMap<String,String>();
		for(Map<String,Object> map:attempList){
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
			oMap.put(opticalCuid, opticalName);
		}
		//占用的opticalCuid
		for(Map<String,Object> map:attempList){
			String scheduleType=IbatisDAOHelper.getStringValue(map, "SCHEDULE_TYPE");
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			if(StringUtils.isNotEmpty(scheduleType)&&StringUtils.isNotEmpty(opticalCuid)){
				if(scheduleType.equals("1")||scheduleType.equals("2")){//占用
					if(!useOpticalList.contains(opticalCuid)){
						useOpticalList.add(opticalCuid);
					}
				}
			}
		}
		Map param=new HashMap();
		param.put("opticalCuidList",useOpticalList);
		List<Map<String,Object>> useList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateOpticalUse", param);
		//释放的opticalCuid
		Map<String,String> deleteMap=new HashMap<String,String>();
		for(Map<String,Object> map:reList){
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			String labelCn=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
			if(StringUtils.isNotEmpty(opticalCuid)){
				deleteMap.put(opticalCuid, "光路:路由设计不完整"+labelCn);
			}
		}
		Map<String,Integer> reNoMap=new HashMap<String,Integer>();
		Map<String,List<String>> reMap=new HashMap<String,List<String>>();
		for(Map<String,Object> map:useList){
			String type=IbatisDAOHelper.getStringValue(map, "TYPE");
			String labelCn=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "CUID");
			if(StringUtils.isNotEmpty(opticalCuid)){
				if(reMap.containsKey(opticalCuid)){
					List list=reMap.get(opticalCuid);
					list.add(type+":"+labelCn);
					int no=reNoMap.get(opticalCuid);
					reNoMap.put(opticalCuid, no+1);
				}else{
					reNoMap.put(opticalCuid, 1);
					List<String> list=new ArrayList<String>();
					list.add(type+":"+labelCn);
					reMap.put(opticalCuid, list);
				}
			}
		}
		Iterator it = reMap.keySet().iterator();
		while(it.hasNext()){
			String opticalCuid = (String) it.next();
			int count=reNoMap.get(opticalCuid);
			if(count>1){
				if(deleteMap.containsKey(opticalCuid)){
					List<String> list=reMap.get(opticalCuid);
					List<String> newList=new ArrayList<String>();
					for(String str:list){
						if(!str.equals(deleteMap.get(opticalCuid))){
							newList.add(str);
						}
					}
					if(newList.size()>1){
						String route="";
						for(String str:newList){
							route=route+" "+str;
						}
						resutlList.add("光纤["+oMap.get(opticalCuid)+"]被多条光路占用  ["+route+"]\\n");
					}
				}else{
					List<String> list=reMap.get(opticalCuid);
					String route="";
					for(String str:list){
						route=route+" "+str;
					}
					resutlList.add("光纤["+oMap.get(opticalCuid)+"]被多条光路占用  ["+route+"]\\n");
				}
			}
		}
		return resutlList;
	}
	//验证路由完整
	private List<String> validateisWholeRoute(Map param){
		List<Map<String,Object>> attempList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getAttempOpticalWay", param);
		List<Map<String,Object>> attempJumpFiberNum=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getAttempJumpFiberList", param);
		List<String> resutlList = new ArrayList<String>();
		for(Map<String,Object> map:attempList){
			String isWholeRoute=IbatisDAOHelper.getStringValue(map, "IS_WHOLE_ROUTE");
			String labelCn=IbatisDAOHelper.getStringValue(map, "LABEL_CN");
			String siteCuidA=IbatisDAOHelper.getStringValue(map, "SITE_CUID_A");
			String siteCuidZ=IbatisDAOHelper.getStringValue(map, "SITE_CUID_Z");
			String ROUTE_TYPE=IbatisDAOHelper.getStringValue(map, "ROUTE_TYPE");
			String EXT_TYPE=IbatisDAOHelper.getStringValue(map, "EXT_TYPE");
			String OPTICAL_TYPE=IbatisDAOHelper.getStringValue(map, "APPLY_TYPE");
			String SCHEDULE_TYPE = IbatisDAOHelper.getStringValue(map, "SCHEDULE_TYPE");
			String OPTICAL_NUM = IbatisDAOHelper.getStringValue(map, "OPTICAL_NUM");
			//针对吉林，若为文本设计，则不校验跳纤
			boolean flag = false; 
			if(SysProperty.getInstance().getValue("districtName").trim().equals("吉林")){
				String attempOptWaycuid=IbatisDAOHelper.getStringValue(map, "CUID");
				flag = noCheckTextOptRoute(attempOptWaycuid);
			}
			
			if(StringUtils.isNotEmpty(isWholeRoute) && "0".endsWith(isWholeRoute)){
				resutlList.add("光路【"+labelCn+"】路由设计不完整  \\n");
			}
			// if ((siteCuidA.contains("ACCESSPOINT")) || (siteCuidZ.contains("ACCESSPOINT")) || (EXT_TYPE.equals("1")) || (EXT_TYPE.equals("3")) || (OPTICAL_TYPE.equals("1"))) {
			if((siteCuidA.contains("ROOM")&&siteCuidA.equals(siteCuidZ)&&ROUTE_TYPE.equals("2")) || siteCuidA.contains("ACCESSPOINT") || siteCuidZ.contains("ACCESSPOINT") || EXT_TYPE.equals("3")||EXT_TYPE.equals("1")|| OPTICAL_TYPE.equals("1")||flag){
				continue;
		    }else{
		       if(attempJumpFiberNum.size()!=0){
		    	  for(Map<String,Object> map2:attempJumpFiberNum){
		    		  if (map.get("CUID").equals(map2.get("OPTICAL_CUID")) && !SCHEDULE_TYPE.equals("2")){
		    			  String num=map2.get("NUM").toString();
						    if((OPTICAL_NUM.equals("2") && Integer.parseInt(num)<4) 
						    		|| (OPTICAL_NUM.equals("1") && Integer.parseInt(num)<2)){
							   resutlList.add("光路【"+labelCn+"】路由设计不完整--跳纤不完整  \\n");
							}
		    		  }
				  }  
		       }else if (!SCHEDULE_TYPE.equals("2")){
		    	   resutlList.add("光路【"+labelCn+"】路由设计不完整--跳纤不完整  \\n");
		       }		       
		    }		
		}
		return resutlList;
	}
	//验证odf端子是否多次上架
	private List<String> validateOdfPortUseByOther(List<Map<String,Object>> attempList){
		List<String> resutlList = new ArrayList<String>();
		Map<String,String> attempOpticalWayNameMap=new HashMap<String,String>();
		Map<String,String> oticalNameMap=new HashMap<String,String>();
		Map<String,String> odfToOpticalMap=new HashMap<String,String>();
		Map<String,String> opticalToAttempOpticalWayMap=new HashMap<String,String>();
		List<String> useOdfList =new ArrayList<String>();
		//占用的opticalCuid
		for(Map<String,Object> map:attempList){
			String origPoint=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destPoint=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			String opticalWayName=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_NAME");
			String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			String attempOpticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			if(StringUtils.isNotEmpty(opticalCuid)){
				if(StringUtils.isNotEmpty(origPoint)){
					if(odfToOpticalMap.containsKey(origPoint)){
						String oldCuid=odfToOpticalMap.get(origPoint);
						if(!oldCuid.equals(opticalCuid)){
							String str="调度光路:["+opticalWayName+"]的光纤["+opticalName+"]的A端端子被调度光路["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]使用了";
							if(!resutlList.contains(str)){
								resutlList.add(str);
							}
						}
					}else{
						odfToOpticalMap.put(origPoint, opticalCuid);
						if(!useOdfList.contains(origPoint)){
							useOdfList.add(origPoint);
						}
					}
				}
				if(StringUtils.isNotEmpty(destPoint)){
					if(odfToOpticalMap.containsKey(destPoint)){
						String oldCuid=odfToOpticalMap.get(destPoint);
						if(!oldCuid.equals(opticalCuid)){
							String str="调度光路:["+opticalWayName+"]的光纤["+opticalName+"]的Z端端子被调度光路["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]使用了";
							if(!resutlList.contains(str)){
								resutlList.add(str);
							}
						}
					}else{
						odfToOpticalMap.put(destPoint, opticalCuid);
						if(!useOdfList.contains(destPoint)){
							useOdfList.add(destPoint);
						}
					}
				}
				oticalNameMap.put(opticalCuid, opticalName);
				opticalToAttempOpticalWayMap.put(opticalCuid, opticalWayName);
			}
		}
		if(resutlList.size()==0){
			Map param=new HashMap();
			param.put("odfCuidList",useOdfList);
			List<Map<String,Object>> useList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".validateOdfData", param);
			//端子在资料中只能出现一次 只判断上边的所有端子
			for(Map<String,Object> map:useList){
				String origCuid=IbatisDAOHelper.getStringValue(map, "ORIG_POINT_CUID");
				String destCuid=IbatisDAOHelper.getStringValue(map, "DEST_POINT_CUID");
				String wayName=IbatisDAOHelper.getStringValue(map, "WAY_NAME");
				String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
				String cuid=IbatisDAOHelper.getStringValue(map, "CUID");
					if(StringUtils.isNotEmpty(origCuid)){
						if(odfToOpticalMap.containsKey(origCuid)){
							String oldCuid=odfToOpticalMap.get(origCuid);
							if(!cuid.equals(oldCuid)){
								String str="";
								if(StringUtils.isNotEmpty(wayName)){
									str="调度光路:["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]的端子被光路["+wayName+"]的光纤["+opticalName+"]使用了";
								}else{
									str="调度光路:["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]的端子被光纤["+opticalName+"]使用了";
								}
								if(!resutlList.contains(str)){
									resutlList.add(str);
								}
							}
						}
					}
					if(StringUtils.isNotEmpty(destCuid)){
						if(odfToOpticalMap.containsKey(destCuid)){
							String oldCuid=odfToOpticalMap.get(destCuid);
							if(!cuid.equals(oldCuid)){
								String str="";
								if(StringUtils.isNotEmpty(wayName)){
									str="调度光路:["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]的端子被光路["+wayName+"]的光纤["+opticalName+"]使用了";
								}else{
									str="调度光路:["+opticalToAttempOpticalWayMap.get(oldCuid)+"]的光纤["+oticalNameMap.get(oldCuid)+"]的端子被光纤["+opticalName+"]使用了";
								}
								if(!resutlList.contains(str)){
									resutlList.add(str);
								}
							}
						}
					}
			}
		}
		return resutlList;
	}
	
	
	
	//验证端口是否能够找到能够找到上架的odf端子信息 算法
	private List<String> validateOdfAndPtpList(List<Map<String,Object>> attempList,Map<String,String> jumpMap){
		for(int i=(attempList.size()-1);i>0;i--){
	    	Map map= attempList.get(i);
	    	String extType=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_EXT_TYPE");
	    	if(extType.equals("3")){
	    		attempList.remove(i);
	    	}
	    }
		List<String> resutlList = new ArrayList<String>();
		List<Map<String,Object>> useList=new ArrayList<Map<String,Object>>();
		List<String> jumpFiberList=new ArrayList<String>();
		List<String> newJumpFiberList=new ArrayList<String>();
		Map<String,String> useMap=new HashMap<String,String>();
		//保存光路的起止端口
		Map<String,Map<String,String>> opticalWayPortMap=new HashMap<String,Map<String,String>>();
		//保存光纤和光路关联关系
		Map<String,String> opticalToOpticalWayMap=new HashMap<String,String>();
		//保存光路的名称
		Map<String,String> opticalWayMap=new HashMap<String,String>();
		//保存光纤的名称
		Map<String,String> opticalMap=new HashMap<String,String>();
		for(Map<String,Object> map:attempList){
			String opticalWayCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_WAY_CUID");
			int azType=IbatisDAOHelper.getIntValue(map, "PORT_AZ_TYPE");
			String portCuid=IbatisDAOHelper.getStringValue(map, "PORT_CUID");
			int index=IbatisDAOHelper.getIntValue(map, "INDEX_PORT");
			String attempOpticalCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_CUID");
			String opticalWayName=IbatisDAOHelper.getStringValue(map, "OPTICAL_WAY_NAME");
			String opticalName=IbatisDAOHelper.getStringValue(map, "OPTICAL_NAME");
			opticalToOpticalWayMap.put(attempOpticalCuid, opticalWayCuid);
			opticalWayMap.put(opticalWayCuid, opticalName);
			opticalMap.put(attempOpticalCuid, opticalName);
			String az="";
			if(azType==1){
				az="A";
			}else{
				az="Z";
			}
			String in="";
			if(index==0){
				in="F";
			}else{
				in="S";
			}
			if(StringUtils.isNotEmpty(portCuid)&&StringUtils.isNotEmpty(opticalWayCuid)){
				if(opticalWayPortMap.containsKey(opticalWayCuid)){
					Map<String,String> dataMap=opticalWayPortMap.get(opticalWayCuid);
					dataMap.put(az+in, portCuid);
				}else{
					Map<String,String> dataMap=new HashMap<String,String>();
					dataMap.put(az+in, portCuid);
					opticalWayPortMap.put(opticalWayCuid, dataMap);
				}
			}
		}
		
		//只计算开始和结束段的端子端口数据
		int maxSeg=0;
		for(Map<String,Object> map:attempList){
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(seg>maxSeg){
				maxSeg=seg;
			}
		}
		for(Map<String,Object> map:attempList){
			String opticalCuid=IbatisDAOHelper.getStringValue(map, "ATTEMP_OPTICAL_CUID");
			String origCuid=IbatisDAOHelper.getStringValue(map, "ORIG_ODFPOINT_CUID");
			String destCuid=IbatisDAOHelper.getStringValue(map, "DEST_ODFPOINT_CUID");
			String opCuid=IbatisDAOHelper.getStringValue(map, "OPTICAL_CUID");
			int seg=IbatisDAOHelper.getIntValue(map, "INDEX_ROUTE_SEG");
			if(StringUtils.isNotEmpty(opticalCuid)&&(seg==0||seg==maxSeg)){
				if(maxSeg==0){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(origCuid)){
							dataMap.put("aOdfPortCuid", origCuid);
							if(!jumpFiberList.contains(origCuid)){
								jumpFiberList.add(origCuid);
							}
						}
						if(StringUtils.isNotEmpty(destCuid)){
							dataMap.put("zOdfPortCuid", destCuid);
							if(!jumpFiberList.contains(destCuid)){
								jumpFiberList.add(destCuid);
							}
						}
						useList.add(dataMap);
					}
				}else if(seg==0){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(origCuid)){
							dataMap.put("aOdfPortCuid", origCuid);
							if(!jumpFiberList.contains(origCuid)){
								jumpFiberList.add(origCuid);
							}
						}
						useList.add(dataMap);
					}
				}else if(seg==maxSeg){
					if(!useMap.containsKey(opticalCuid)){
						useMap.put(opticalCuid, opticalCuid);
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("value", opticalCuid);
						if(StringUtils.isNotEmpty(destCuid)){
							dataMap.put("zOdfPortCuid", destCuid);
							if(!jumpFiberList.contains(destCuid)){
								jumpFiberList.add(destCuid);
							}
						}
						useList.add(dataMap);
					}
				}
			}
		}
		if(jumpFiberList.size()>0){
			Map pm=new HashMap();
			pm.put("usePortList", jumpFiberList);
			List<Map<String, Object>> resList = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap + ".selectJumpFiberByPort", pm);
			
			for(Map<String, Object> map:resList){
				String destCuid=IbatisDAOHelper.getStringValue(map, "DEST_POINT_CUID");
				String origCuid=IbatisDAOHelper.getStringValue(map, "ORIG_POINT_CUID");
				if(StringUtils.isNotEmpty(destCuid)){
					pm.put(destCuid, destCuid);
				}
				if(StringUtils.isNotEmpty(origCuid)){
					pm.put(origCuid, origCuid);
				}
			}
			for(String str:jumpFiberList){
				if(pm.containsKey(str)){
					newJumpFiberList.add(str);
				}else{
					for(Map<String,Object> map:useList){
						String value=IbatisDAOHelper.getStringValue(map,"value");
						String zOdfPortCuid=IbatisDAOHelper.getStringValue(map,"zOdfPortCuid");
						String aOdfPortCuid=IbatisDAOHelper.getStringValue(map,"aOdfPortCuid");
						if(StringUtils.isNotEmpty(value)){
							if(opticalToOpticalWayMap.containsKey(value)){
								String wayValue=opticalToOpticalWayMap.get(value);
								if(StringUtils.isNotEmpty(wayValue)){
									Map<String,String> portMap=opticalWayPortMap.get(wayValue);//端口
									if(portMap!=null&&portMap.size()>0){
										String aPtp=IbatisDAOHelper.getStringValue(portMap,"AF");
										String zPtp=IbatisDAOHelper.getStringValue(portMap,"ZF");
										if(StringUtils.isNotEmpty(aOdfPortCuid)&&StringUtils.isNotEmpty(aPtp)){
											jumpMap.put(aOdfPortCuid, aPtp);
										}
										if(StringUtils.isNotEmpty(zOdfPortCuid)&&StringUtils.isNotEmpty(zPtp)){
											jumpMap.put(zOdfPortCuid, zPtp);
										}
									}
								}
							}
						}
					}
				}
			}
			List<Map<String,String>> rList=OptDesignBO.getPtpByPort(useList);
			Iterator it = useMap.keySet().iterator();
			while(it.hasNext()){
				String opticalCuid = (String) it.next();
				if(StringUtils.isNotEmpty(opticalCuid)){
					if(rList!=null&&rList.size()>0){
						for(Map<String,String> map:rList){
							String cuid=IbatisDAOHelper.getStringValue(map, "opticalCuid");
							if(StringUtils.isNotEmpty(cuid)){
								if(cuid.equals(opticalCuid)){
									//得到光路的端口
									if(opticalToOpticalWayMap.containsKey(cuid)){
										String opticalWayCuid=opticalToOpticalWayMap.get(cuid);
										if(opticalWayPortMap.containsKey(opticalWayCuid)){
											Map<String,String> datamap=opticalWayPortMap.get(opticalWayCuid);
											String afPotyCuid=IbatisDAOHelper.getStringValue(datamap, "AF");
											String asPotyCuid=IbatisDAOHelper.getStringValue(datamap, "AS");
											String zfPotyCuid=IbatisDAOHelper.getStringValue(datamap, "ZF");
											String zsPotyCuid=IbatisDAOHelper.getStringValue(datamap, "ZS");
											String aCuid=IbatisDAOHelper.getStringValue(map, "origPtp");
											String zCuid=IbatisDAOHelper.getStringValue(map, "destPtp");
											boolean aFlag=true;
											boolean zFlag=true;
											for(Map<String,Object> dataMap:useList){
												if(dataMap.containsValue(cuid)){
													if(!dataMap.containsKey("zOdfPortCuid")){
														zFlag=false;
													}else{
														String str=IbatisDAOHelper.getStringValue(dataMap,"zOdfPortCuid");
														if(str!=null){
															if(newJumpFiberList.contains(str)){
																zFlag=false;
															}
														}
													}
													if(!dataMap.containsKey("aOdfPortCuid")){
														aFlag=false;
													}else{
														String str=IbatisDAOHelper.getStringValue(dataMap,"aOdfPortCuid");
														if(str!=null){
															if(newJumpFiberList.contains(str)){
																aFlag=false;
															}
														}
													}
												}
											}
											if(aFlag){
												if(StringUtils.isEmpty(aCuid)){
													resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]A端上架端子与光路起端端口没有跳纤\\n");
												}else{
													if(!aCuid.equals(afPotyCuid)&&!aCuid.equals(asPotyCuid)){
														resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]A端上架端子与光路起端端口没有跳纤\\n");
													}
												}
											}
											if(zFlag){
												if(StringUtils.isEmpty(zCuid)){
													resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]Z端上架端子与光路起端端口没有跳纤\\n");
												}else{
			
													if(!zCuid.equals(zfPotyCuid)&&!zCuid.equals(zsPotyCuid)){
														resutlList.add("光路["+opticalWayMap.get(opticalWayCuid)+"] 的光纤["+opticalMap.get(cuid)+"]Z端上架端子与光路起端端口没有跳纤\\n");
													}
												}
											}
										}
									}
								}
							}
						}
					}else{
					}
				}
			}
		}
		return resutlList;
	}

	public String getServiceTableName() {
		return "ATTEMP_OPTICAL_WAY";
	}
	public void deleteSheetService(ServiceActionContext ac, String sheetId, List<String> serviceCuidList) {
		if(!serviceCuidList.isEmpty()){
			// 记录变更日志
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_RELEASE);
			attempLog.setRelatedTaskCuid(ac.getUserName()+"作废工单");
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			
			this.releaseAttempOpticalWay(ac, serviceCuidList);
			
			// 修改与任务的关系状态为已释放
			ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"作废工单");
		}
	}
	public void rejectService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_REJECT);
		attempLog.setRelatedTaskCuid(taskId);
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateDelByService(serviceCuidList, getServiceTableName());
		this.releaseAttempOpticalWay(ac, serviceCuidList);
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(taskId, serviceCuidList,SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"传输驳回，删除资源");
		
	}
	public void releaseIrmsService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_RELEASE);
		attempLog.setRelatedTaskCuid(taskId);
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		
		this.releaseAttempOpticalWay(ac, serviceCuidList);
		
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"综资回写");
		
	}
	public void releaseSysService(ServiceActionContext ac, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写光路ID！");
		}
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_RELEASE);
		attempLog.setRelatedTaskCuid(ac.getUserName()+"按照光路释放");
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		this.releaseAttempOpticalWay(ac, serviceCuidList);
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"按照光路释放");
	}
	public void releaseTnmsService(ServiceActionContext ac, String taskId, List<String> taskServiceCuidList) {
		if(taskServiceCuidList == null || taskServiceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		List<Map<String, Object>> serviceList = this.findServiceByTaskService(taskServiceCuidList);
		if(serviceList != null && !serviceList.isEmpty()) {
			List<String> serviceCuidList = new ArrayList<String>();
			
			for(Map<String, Object> map : serviceList) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				if(StringUtils.isNotBlank(cuid)) serviceCuidList.add(cuid);
			}
			
			// 记录变更日志
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_RELEASE);
			attempLog.setRelatedTaskCuid(taskId);
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			
			this.releaseAttempOpticalWay(ac, serviceCuidList);
			
			// 修改与任务的关系状态为已释放
			ProcessBO.updateTaskServiceStateByTask2Service(taskServiceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"传输释放");
		}
	}
	
	public void releaseTnmsServiceByService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_RELEASE);
		attempLog.setRelatedTaskCuid(taskId);
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		
		this.releaseAttempOpticalWay(ac, serviceCuidList);
		
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"传输释放");
	}
	
	public void writeIrmsService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if (!SysProperty.getInstance().getValue("district").equals("DISTRICT-00001-00008")) {
			if(serviceCuidList == null || serviceCuidList.isEmpty()) {
				throw new RuntimeException("无回写电路ID！");
			}
			if(StringUtils.isBlank(taskId)) {
				throw new RuntimeException("无回写任务ID！");
			}
			
			// 综资回写校验时隙占用
			Map<String, Object> resultMap = this.validateIsAbleToArchive(serviceCuidList);
			Map jumpMap = (Map)resultMap.get("jumpMap");
//			if(resultMap != null && !resultMap.isEmpty()) {
//				Integer success = IbatisDAOHelper.getIntValue(resultMap, "success");
//				String msg = IbatisDAOHelper.getStringValue(resultMap, "msg");
//				if(success == 0) {
//					throw new RuntimeException(msg);
//				}
//			}
			
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_WRITE);
			attempLog.setRelatedTaskCuid(taskId);
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			this.writeAttempOptical(ac, serviceCuidList,jumpMap);
			
			// 修改与任务的关系状态为已回写
			ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"综资回写");
		}
	}
	public void writeSysService(ServiceActionContext ac, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		Map<String, Object> resultMap = this.validateIsAbleToArchive(serviceCuidList);
		Map jumpMap = (Map)resultMap.get("jumpMap");
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_WRITE);
		attempLog.setRelatedTaskCuid(ac.getUserName()+"按照电路回写");
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		this.writeAttempOptical(ac, serviceCuidList,jumpMap);
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"按照电路回写");
		
	}
	public void writeTnmsService(ServiceActionContext ac, String taskId, List<String> taskServiceCuidList) {
		if(taskServiceCuidList == null || taskServiceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		
		List<Map<String, Object>> serviceList = this.findServiceByTaskService(taskServiceCuidList);
		if(serviceList != null && !serviceList.isEmpty()) {
			List<String> serviceCuidList = new ArrayList<String>();
			
			for(Map<String, Object> map : serviceList) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				if(StringUtils.isNotBlank(cuid)) serviceCuidList.add(cuid);
			}
			Map<String, Object> resultMap = this.validateIsAbleToArchive(serviceCuidList);
			Map jumpMap = (Map)resultMap.get("jumpMap");
			// 记录变更日志
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_WRITE);
			attempLog.setRelatedTaskCuid(taskId);
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			
			this.writeAttempOptical(ac, serviceCuidList,jumpMap);
			
			// 修改与任务的关系状态为已回写
			ProcessBO.updateTaskServiceStateByTask2Service(taskServiceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"传输回写");
		}
	}
	
	public void writeTnmsServiceByService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		Map<String, Object> resultMap = this.validateIsAbleToArchive(serviceCuidList);
		Map jumpMap = (Map)resultMap.get("jumpMap");
		
		Map<String,Object> params=new HashMap<String, Object>();
		List<String> serviceCuids=new ArrayList<String>();
		if(SysProperty.getInstance().getValue("district").equals("DISTRICT-00001-00005")){
			for(String serviceCuid : serviceCuidList){
				if(serviceCuid.startsWith("ATTEMP_OPTICAL_WAY")){
					serviceCuids.add(serviceCuid.replaceAll("ATTEMP_OPTICAL_WAY", "OPTICAL_WAY"));
				}else{
					serviceCuids.add(serviceCuid);
				}
			}
		}
		params.put("cuids",serviceCuids);
		List<Map<String,String>> closelistOpts = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".queryForOptWayByPks", params);
		
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_WRITE);
		attempLog.setRelatedTaskCuid(taskId);
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		//回写光路
		this.writeAttempOptical(ac, serviceCuidList,jumpMap);
		
		if(SysProperty.getInstance().getValue("district").equals("DISTRICT-00001-00005")){
			List<Map<String,String>> listOpts = IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".queryForOptWayByPks", params);
			listOpts.addAll(closelistOpts);
			OptGraphBO optGraphBO = (OptGraphBO)SpringContextUtil.getBean("OptGraphBO");
			optGraphBO.synOpticalFiber(taskId,listOpts);
		}
		// 修改与任务的关系状态为已回写
		ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"传输回写");
		
		TaskInst task = ProcessBO.getTaskInstByTaskId(taskId);
		//add by luoshuyun 通知综资 先回写在推送施工信息 start
		if(StringUtils.isNotBlank(task.getRelatedOrderCuid())) {
			IOrder order = FacedeOrderMaintainBO.getOrderById(task.getRelatedOrderCuid());
			Map<String, Object> orderMap = order.getData();
			String fromcode = IbatisDAOHelper.getStringValue(orderMap,"FROMCODE");
			if(!"tnms".equalsIgnoreCase(fromcode)){
				try {
					//当单子中有多条资源时，最后一条回写时，要推送所有施工信息
					List<TaskService> taskList =  ProcessBO.findTaskServices(task.getCuid());
					boolean writeEndFlag = true;
					for(TaskService taskservice : taskList){
						if(!taskservice.getState().equals(SheetConstants.SERVICE_STATE_WRITE_SUCCESS)){
							writeEndFlag = false;
						}
					}
					if(writeEndFlag){
						logger.info("==========回写后，推送施工信息开始 start==========");
						IrmsOpticalWayClient.archiveAttempOpticalWay(ac, task, IbatisDAOHelper.getStringValue(orderMap,"IRMSTITLE"),false);
						logger.info("==========回写后，推送施工信息结束 end==========");
					}
				} catch (Exception e) {
					 logger.info("方法writeTnmsServiceByService发错，"+e);
					 throw new RuntimeException("调用综资接口出错,"+e.getMessage());
				}
			}
		}
	}
	public void deleteService(ServiceActionContext ac, String sheetId,
			List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		
	}
	public List<IService> findService(List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<PathPoint> getIndiPoints(List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public Map<String, ServicePath> getRoutePath(List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public Map<String, ServicePath> getRoutePathDetail(
			List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<Service> getServiceInfo(List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public Map<String, List<ServiceRel>> findServiceRels(
			List<String> serviceCuidList) {
		// TODO Auto-generated method stub
		return null;
	}
	public Map<String, Object> getFromCode(String sheetCuid){
		Map<String, Object> value=null;
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("sheetCuid", sheetCuid);
		List<Map<String, Object>> fromCodeList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getFromCode", pm);
		if(fromCodeList!=null&&fromCodeList.size()>0){
			value=fromCodeList.get(0);
		}
		return value;
	}
	public void deleteJumpFiber(List<String> list){
		String condition="";
		for(int i=0;i<list.size();i++){
			if(i==0){
				condition="'"+list.get(i)+"'";
			}else{
				condition=condition+",'"+list.get(i)+"'";
			}
		}
		if(condition.length()>0){
			String sql="UPDATE PTP SET IS_CONN_STATE=0 WHERE CUID IN(SELECT ORIG_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+") UNION SELECT DEST_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+"))";
			IbatisResDAO.updateSql(sql);
			sql="UPDATE ODFPORT SET IS_CONNECTED=0 WHERE CUID IN(SELECT ORIG_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+") UNION SELECT DEST_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+"))";
			IbatisResDAO.updateSql(sql);
			sql="UPDATE FCABPORT SET IS_CONNECTED=0 WHERE CUID IN(SELECT ORIG_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+") UNION SELECT DEST_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+"))";
			IbatisResDAO.updateSql(sql);
			sql="UPDATE FIBER_DP_PORT SET IS_CONNECTED=0 WHERE CUID IN(SELECT ORIG_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+") UNION SELECT DEST_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+"))";
			IbatisResDAO.updateSql(sql);
			sql="UPDATE FIBER_JOINT_POINT SET IS_CONNECTED=0 WHERE CUID IN(SELECT ORIG_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+") UNION SELECT DEST_POINT_CUID FROM JUMP_FIBER WHERE CUID IN("+condition+"))";
			IbatisResDAO.updateSql(sql);
			sql="DELETE FROM JUMP_FIBER WHERE CUID IN("+condition+")";
			IbatisResDAO.deleteSql(sql);
		}
	}
	/**
	 * 回单中是否有未被替换文本光纤的的光路
	 * @param taskId
	 * @return
	 */
	public  String isExitTextRoute(String taskId){
		String hasTextRouteOptName = "";
		Map mp = new HashMap();
		mp.put("taskId", taskId);
		List<Map<String,Object>> textOpt = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTextOpticalRoute",mp);
		if(textOpt!=null && textOpt.size()>0){
			for(int i=0;i<textOpt.size();i++){
				Map map = textOpt.get(i);
				if(hasTextRouteOptName.indexOf((String)map.get("OPTICAL_WAY_NAME"))==-1){
				    hasTextRouteOptName +="\n【"+(String)map.get("OPTICAL_WAY_NAME")+"】";
				}
			}
		}
		return hasTextRouteOptName;
	}
	
	/**
	 * 设置默认施工角色，根据是否是联通调度，调用不同的设置默认角色方法
	 * @author: zhangliang
	 * @param task
	 * @param attempTraphCuidList
	 */
	private void setDefaultRole(TaskInst task, List<String> attempTraphCuidList){
			//设置默认施工角色
		this.setDefaultConstructRole(attempTraphCuidList);
	}
	
	/**
	 * 设置默认施工角色
	 * @param attempTraphCuidList
	 */
	public void setDefaultConstructRole(List<String> attempTraphCuidList) {
		String delSql = "DELETE FROM T_TASK_TO_SERVICE_LINK WHERE RELATED_TASK2SERVICE_CUID = '%s' AND LINK_TYPE ='constructTask'";
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".queryDefaultConstructRole", pm);
		List<String> existsRoleInfoLinkCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".validateExistsRoleInfoLink", pm);
		
		Map<String, List<RoleVO>> roleMap = new HashMap<String, List<RoleVO>>();
		for (Map<String, Object> m : list) {
			String tdInfoId = IbatisDAOHelper.getStringValue(m, "RELATED_TASK2SERVICE_CUID");
			if (!existsRoleInfoLinkCuidList.contains(tdInfoId)) {
				String aRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_A_ROLE_CUID"));
				String zRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_Z_ROLE_CUID"));
				this.IbatisResDAO.deleteSql(String.format(delSql, tdInfoId));
				
				// 如果电路两端角色不同，则默认不生成
				if (!aRoleId.equals(zRoleId))
					continue;
				// 如果找不 到默认施工角色，则不生成
				if (!aRoleId.equals("")) {
					List<RoleVO> roleVOList = roleMap.get(tdInfoId);
					if(roleVOList == null) {
						roleVOList = new ArrayList<RoleVO>();
						roleMap.put(tdInfoId, roleVOList);
					}
					RoleVO roleVO = new RoleVO();
					roleVO.setType(RoleVO.TYPE_ROLE);
					roleVO.setValue(aRoleId);
					roleVOList.add(roleVO);
				}
			}
		}
		OptDispatchBO.setTaskRoles(roleMap, "constructTask");
	}
	 
	
	public  List<Map<String, Object>> findOpticalWay(List optWayCuidList){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", optWayCuidList);
		List<Map<String, Object>> OpticalWayList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findEOpticalWay", pm);
       return OpticalWayList;
	}
	public String sendODN(ServiceActionContext ac, String taskId,List<String> opticalCuidList) {
		String msg = "";
		Object [] res = null;
		try{
			logger.info("==========开始派单 start==========");
			res = OdnOpticalWayClient.workOrder(ac,taskId,opticalCuidList);
			logger.info("==========派单结束 end==========");
		}catch (Exception e) {
			throw new RuntimeException("调用ODN接口出错,"+e.getMessage());
		}
		if (res==null||res.length==0){
			throw new RuntimeException("调用ODN接口出错，请联系ODN网管人员！");
		}else{
			Map<String,String> result = null;
			try {
				logger.info("==========解析返回结果==========");
				result = OdnOpticalWayClient.resultAnalysis(res);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("解析返回结果出错");
			}
			if(result != null){
				String success = result.get("success");
				if("true".equals(success)){
					msg = "派单成功！";
					updateState(taskId,opticalCuidList,"已派单");
				}else{
					msg = "派单失败："+ result.get("msg");
				}
			}
		}
		return msg;
	}
	public void updateState(String taskId, List<String> opticalCuidList,
			String state) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("taskId", taskId);
		pm.put("opticalCuidList", opticalCuidList);
		pm.put("state", state);
		IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateOptState", pm);
		
	}
	public String cancleODN(ServiceActionContext ac, String taskId,
			String retrieveReasonCode, String retrieveReasonDesc,List<String> opticalCuidList) {
		String msg = "";
		Object [] res = null;
		try{
			logger.info("==========开始撤单 start==========");
			res = OdnOpticalWayClient.retrieveOrder(ac,taskId,retrieveReasonCode,retrieveReasonDesc);
			logger.info("==========撤单结束 end==========");
		}catch (Exception e) {
			throw new RuntimeException("调用ODN接口出错,"+e.getMessage());
		}
		if (res==null||res.length==0){
			throw new RuntimeException("调用ODN接口出错，请联系ODN网管人员！");
		}else{
			Map<String,String> result = null;
			try {
				logger.info("==========解析返回结果==========");
				result = OdnOpticalWayClient.resultAnalysis(res);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("解析返回结果出错");
			}
			if(result != null){
				String success = result.get("success");
				if("true".equals(success)){
					msg = "撤单成功！";
					updateState(taskId,opticalCuidList,"撤单成功");
				}else{
					msg = "撤单失败："+ result.get("msg");
				}
			}
		}
		return msg;
	}
	
	/**
	 * 推送管线
	 * @param ac
	 * @param attempOpticalWayCuidList 所选光路cuid
	 * @return
	 */
	public String resourceConfig(ServiceActionContext ac,String taskId,List<String> addOpticalWayCuidList) {
		String msg = "";
		String res = null;
		try{
			logger.info("==========开始派单 start==========");
			res = RMRouteDesignClient.sendLine(ac,addOpticalWayCuidList);
			logger.info("==========派单结束 end==========");
		}catch (Exception e) {
			throw new RuntimeException("调用管线工单派发接口出错,"+e.getMessage());
		}
		if (StringUtils.isEmpty(res)){
			throw new RuntimeException("调用管线工单派发接口返回结果为空，请联系管线人员！");
		}else{
			Map<String,String> result = null;
			try {
				logger.info("==========解析返回结果==========");
				result = RMRouteDesignClient.getResults(res);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("解析返回结果出错");
			}
			if(result != null){
				String success = result.get("success");
				if("1".equals(success)){
					msg = "派单成功！";
					updateState(taskId,addOpticalWayCuidList,"已派单");
					List<Record> recordList = new ArrayList<Record>();
					List<Record> pkRecordList = new ArrayList<Record>();
					//修改光路表光纤是否管线推送标示
					for(String addOpticalWayCuid : addOpticalWayCuidList){
						Record record = new Record("ATTEMP_OPTICAL_WAY");
						record.addColValue("CUID", addOpticalWayCuid);
						Record pkRecord = new Record("ATTEMP_OPTICAL_WAY");
						pkRecord.addColValue("ROUTE_DESIGNER", 1);
						recordList.add(record);
						pkRecordList.add(pkRecord);
					}
					if(recordList!=null&&recordList.size()>0){
						this.IbatisResDAO.updateDynamicTableBatch(pkRecordList, recordList);
					}
				}else{
					msg = "派单失败："+ result.get("msg");
				}
			}
		}
		return msg;
	}
	/**
	 * 光路停闭接口
	 * @param ac
	 * @param closeOpticalCuidList 所选光路cuid
	 * @return
	 */
	public String releaseOpticalWaySheet(ServiceActionContext ac,String taskId, List<String> closeOpticalCuidList) {
		String msg = "";
		String res = null;
		try{
			logger.info("==========开始派发停闭单 start==========");
			res = RMRouteDesignClient.releaseOpticalWaySheet(ac,closeOpticalCuidList);
			logger.info("==========派发停闭单结束 end==========");
		}catch (Exception e) {
			throw new RuntimeException("调用管线光路停闭接口出错,"+e.getMessage());
		}
		if (StringUtils.isEmpty(res)){
			throw new RuntimeException("调用管线光路停闭接口返回结果为空，请联系管线人员！");
		}else{
			Map<String,String> result = null;
			try {
				logger.info("==========解析返回结果==========");
				result = RMRouteDesignClient.getResults(res);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("解析返回结果出错");
			}
			if(result != null){
				String success = result.get("success");
				if("1".equals(success)){
					msg = "派发停闭单成功！";
					updateState(taskId,closeOpticalCuidList,"已派单");
					List<Record> recordList = new ArrayList<Record>();
					List<Record> pkRecordList = new ArrayList<Record>();
					//修改光路表光纤是否管线推送标示
					for(String closeOpticalWayCuid : closeOpticalCuidList){
						Record record = new Record("ATTEMP_OPTICAL_WAY");
						record.addColValue("CUID", closeOpticalWayCuid);
						Record pkRecord = new Record("ATTEMP_OPTICAL_WAY");
						pkRecord.addColValue("ROUTE_DESIGNER", 1);
						recordList.add(record);
						pkRecordList.add(pkRecord);
					}
					if(recordList!=null&&recordList.size()>0){
						this.IbatisResDAO.updateDynamicTableBatch(pkRecordList, recordList);
					}
				}else{
					msg = "派单停闭单失败："+ result.get("msg");
				}
			}
		}
		return msg;
	}
	/**
	 * 撤销光纤
	 * @param ac
	 * @param opticalWayCuidList 所选光路cuid
	 * @return
	 */
	public String cancleOptical(ServiceActionContext ac, String taskId,List<String> opticalWayCuidList) {
		String msg = "";
		String res = null;
		try{
			logger.info("==========开始派单 start==========");
			res = RMRouteDesignClient.cancleOptical(ac,opticalWayCuidList);
			logger.info("==========派单结束 end==========");
		}catch (Exception e) {
			throw new RuntimeException("调用管线工单派发接口出错,"+e.getMessage());
		}
		if (StringUtils.isEmpty(res)){
			throw new RuntimeException("调用管线工单派发接口返回结果为空，请联系管线人员！");
		}else{
			Map<String,String> result = null;
			try {
				logger.info("==========解析返回结果==========");
				result = RMRouteDesignClient.getResults(res);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("解析返回结果出错");
			}
			if(result != null){
				String success = result.get("success");
				if("1".equals(success)){
					msg = "撤销光纤成功！";
					updateState(taskId,opticalWayCuidList,"已撤销");
					List<Record> recordList = new ArrayList<Record>();
					List<Record> pkRecordList = new ArrayList<Record>();
					//修改光路表光纤是否管线推送标示
					for(String addOpticalWayCuid : opticalWayCuidList){
						Record record = new Record("ATTEMP_OPTICAL_WAY");
						record.addColValue("CUID", addOpticalWayCuid);
						Record pkRecord = new Record("ATTEMP_OPTICAL_WAY");
						pkRecord.addColValue("ROUTE_DESIGNER", 0);
						recordList.add(record);
						pkRecordList.add(pkRecord);
					}
					if(recordList!=null&&recordList.size()>0){
						this.IbatisResDAO.updateDynamicTableBatch(pkRecordList, recordList);
					}
				}else{
					msg = "派单失败："+ result.get("msg");
				}
			}
		}
		return msg;
	}
	
	/**
	 * 查询该工单下所有光路，再根据光路获取所有的活动任务
	 * @param relatedSheetCuid
	 */
	public List<TaskInst> findServiceCuidsBySheetId(String relatedSheetCuid){
		Map mp = new HashMap();
		mp.put("sheetCuid", relatedSheetCuid);
		List<String> serviceIdList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSheetToAttOpticalWay", mp);
		List<TaskInst> runTaskList = ProcessBO.findRunTaskInstByService(serviceIdList);
		return runTaskList;
	}
	
	public boolean noCheckTextOptRoute(String attempOptWayCuid){
		Map mp = new HashMap();
		mp.put("attempOptWayCuid", attempOptWayCuid);
		boolean flag = true;
		List<Map<String,Object>> textOptRouteList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getIsTextOptRoute", mp);
		for(Map<String,Object> textOptRouteMap : textOptRouteList){
			String isText = IbatisDAOHelper.getStringValue(textOptRouteMap, "IS_TEXT");
			if("0".equals(isText)){    //只要其中一芯是用光纤设计就校验
				flag = false;
			}
		}
		return flag;
	}
}