package com.boco.flow.traph.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.attemp.PathDesignConstants;
import com.boco.attemp.bo.BaseServiceBO;
import com.boco.attemp.bo.IMaintainBO;
import com.boco.attemp.bo.IPathDesignBO;
import com.boco.attemp.bo.IServiceBO;
import com.boco.attemp.pojo.AttempLog;
import com.boco.attemp.pojo.IService;
import com.boco.attemp.pojo.PathData;
import com.boco.attemp.pojo.PathNode;
import com.boco.attemp.pojo.PathPoint;
import com.boco.attemp.pojo.Service;
import com.boco.attemp.pojo.ServiceDesignPath;
import com.boco.attemp.pojo.ServicePath;
import com.boco.attemp.pojo.ServiceRel;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.core.utils.lang.StringHelper;
import com.boco.flow.bo.ProcessBO;
import com.boco.flow.common.SheetConstants;
import com.boco.flow.common.bo.SendMessageBO;
import com.boco.flow.model.RoleVO;
import com.boco.flow.model.TaskInst;
import com.boco.flow.order.bo.FacedeOrderMaintainBO;
import com.boco.flow.order.pojo.InfoDesignType;
import com.boco.flow.traph.TraphConstants;
import com.boco.flow.traph.excel.bo.AttempTraphRDataHandler;
import com.boco.flow.traph.pojo.AttempTraphService;
import com.boco.flow.traph.pojo.ITraphExtend;
import com.boco.flow.traph.pojo.TraphName;
import com.boco.flow.traph.pro.bo.TraphDispatchProBO;
import com.boco.maintain.device.bo.Property;
import com.boco.maintain.traph.bo.AttempTraphRoutePathBO;
import com.boco.maintain.traph.bo.ResTraphBO;
import com.boco.maintain.traph.pojo.JkInfo;
import com.boco.transnms.common.dto.AttempPtnPath;
import com.boco.transnms.common.dto.AttempTraph;
import com.boco.transnms.common.dto.Traph;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.dao.base.CachedDtoMessage;
import com.boco.ws.client.traph.TnmsCircuitClient;
import com.boco.ws.server.nmemos.bo.AttempXToEomsServiceBO;

@SuppressWarnings({"unchecked", "static-access"})
public class TraphMaintainBO extends BaseServiceBO implements IMaintainBO,IServiceBO {
	
	private Map<String,Integer> attempTraphLock = new ConcurrentHashMap<String,Integer>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String sqlMap = "TraphMaintain";
	
	private TraphDispatchProBO TraphDispatchProBO;

	public void setTraphDispatchProBO(TraphDispatchProBO traphDispatchProBO) {
		TraphDispatchProBO = traphDispatchProBO;
	}
	
	protected SendMessageBO SendMessageBO;
	
	public void setSendMessageBO(SendMessageBO sendMessageBO) {
		SendMessageBO = sendMessageBO;
	}
	protected ProcessBO ProcessBO;
	
	public void setProcessBO(ProcessBO processBO) {
		ProcessBO = processBO;
	}
	private ITraphNameBO TraphNameBO;
	
	public void setTraphNameBO(ITraphNameBO traphNameBO) {
		TraphNameBO = traphNameBO;
	}
	
	private FacedeOrderMaintainBO FacedeOrderMaintainBO;
	
	public void setFacedeOrderMaintainBO(FacedeOrderMaintainBO facedeOrderMaintainBO) {
		FacedeOrderMaintainBO = facedeOrderMaintainBO;
	}

	private ResTraphBO ResTraphBO;

	public void setResTraphBO(ResTraphBO resTraphBO) {
		ResTraphBO = resTraphBO;
	}
	private TnmsCircuitClient TnmsCircuitClient;

	public void setTnmsCircuitClient(TnmsCircuitClient tnmsCircuitClient) {
		TnmsCircuitClient = tnmsCircuitClient;
	}

	public Map<String, Integer> getAttempTraphLock() {
		return attempTraphLock;
	}

	public String getServiceTableName() {
		return "ATTEMP_TRAPH";
	}
	
	/**
	 * 获取待设计电路的基础信息，包括版本、终端点、名称等信息
	 * @param attempTraphCuidList
	 * @return
	 */
	public List<Service> getServiceInfo (List<String> attempTraphCuidList){
		List<Service> serviceList = new ArrayList<Service>();
		//根据调度电路ID，查询调度电路的数据
		List<Map<String, Object>> list = this.findAttempTraph(attempTraphCuidList);
		for(Map<String,Object> map:list){
			//初始化电路对象
			Service service = new Service();
			//设置类型为调度电路
			service.setBmClassId("ATTEMP_TRAPH");
			service.setCuid(IbatisDAOHelper.getStringValue(map, "CUID"));//电路ID
			service.setLabelCn(IbatisDAOHelper.getStringValue(map, "LABEL_CN"));//电路名称
			service.setVersion(IbatisDAOHelper.getIntValue(map, "GT_VERSION"));//设计版本号
			String apointType = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_A");//A端终端点类型
			String aSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_ZD_SITE_CUID");//A端终端点CUID
			String apointName = IbatisDAOHelper.getStringValue(map, "A_ZD_NAME");//A端终端点名称
			String zpointType = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_Z");//Z端终端点类型
			String zSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_ZD_SITE_CUID");//Z端终端点CUID
			String zpointName = IbatisDAOHelper.getStringValue(map, "Z_ZD_NAME");//Z端终端点名称
			String aRoomCuid=IbatisDAOHelper.getStringValue(map, "RELATED_A_END_ROOM_CUID");
			String zRoomCuid=IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_ROOM_CUID");
			String aNeCuid=IbatisDAOHelper.getStringValue(map, "RELATED_NE_A_CUID");
			String zNeCuid=IbatisDAOHelper.getStringValue(map, "RELATED_NE_Z_CUID");
			String apointCuid=aSiteCuid;
			String zpointCuid=zSiteCuid;
			if(StringUtils.isNotEmpty(apointType)&&apointType.equals("ROOM")){
				apointCuid=aRoomCuid;
			}else if(StringUtils.isNotEmpty(apointType)&&apointType.equals("TRANS_ELEMENT")){
				apointCuid=aNeCuid;
			}
			if(StringUtils.isNotEmpty(zpointType)&&zpointType.equals("ROOM")){
				zpointCuid=zRoomCuid;
			}else if(StringUtils.isNotEmpty(zpointType)&&zpointType.equals("TRANS_ELEMENT")){
				zpointCuid=zNeCuid;
			}
			//封装成路由点对象
			PathPoint apoint = new PathPoint(apointType, apointCuid, apointName);
			PathPoint zpoint = new PathPoint(zpointType, zpointCuid, zpointName);
			service.setApoint(apoint);
			service.setZpoint(zpoint);
			//存储电路数据
			service.setAttr(map);
			//存储电路对象
			serviceList.add(service);
		}
		return serviceList;
	}

	/**
	 * 通过调度电路ID，获取调度电路转接点
	 */
	public List<PathPoint> getIndiPoints(List<String> serviceCuidList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("attempTraphCuidList", serviceCuidList);
		List<PathPoint> pointList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphIndiPoints",params);
		return pointList;
	}

	/**
	 * 根据调度电路ID，获取电路路由对象的简略信息
	 */
	public Map<String,ServicePath> getRoutePath(List<String> serviceCuidList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("attempTraphCuidList", serviceCuidList);
		//通过调度电路ID，获取全部调度路由数据
		List<Map<String,Object>> routePathList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRoutePath",params);
		Map<String,ServicePath> serMap = new HashMap<String,ServicePath>();
		
		//循环调度路由数据，按{KEY:电路ID，VALUE:电路路由对象}结构进行封装
		for(Map<String,Object> map:routePathList){
			String traphCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_CUID");//电路ID
			String traphRouteCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_ROUTE_CUID");//路由ID
			//封装为{KEY:电路ID，VALUE:电路路由对象}
			ServicePath service = serMap.get(traphCuid);
			if(service==null){
				service = new ServicePath();
				serMap.put(traphCuid, service);
				service.setCuid(traphCuid);//电路ID
				service.setBmClassId("ATTEMP_TRAPH");//类型为调度电路
				service.setLabelCn(IbatisDAOHelper.getStringValue(map, "TRAPH_NAME"));//电路名称
				service.setRouteCuid(traphRouteCuid);//路由ID
				String apointType = IbatisDAOHelper.getStringValue(map, "TRAPH_A_POINT_TYPE");//电路A端终端点类型
				String apointCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_A_POINT_CUID");//电路A端终端点CUID
				String apointName = IbatisDAOHelper.getStringValue(map, "TRAPH_A_POINT_NAME");//电路A端终端点名称
				String zpointType = IbatisDAOHelper.getStringValue(map, "TRAPH_Z_POINT_TYPE");//电路Z端终端点类型
				String zpointCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_Z_POINT_CUID");//电路Z端终端点CUID
				String zpointName = IbatisDAOHelper.getStringValue(map, "TRAPH_Z_POINT_NAME");//电路Z端终端点名称
				//封装成电路路由点对象
				String azdSite = IbatisDAOHelper.getStringValue(map, "RELATED_A_ZD_SITE_SITE_CUID");
				String zzdSite = IbatisDAOHelper.getStringValue(map, "RELATED_Z_ZD_SITE_SITE_CUID");
				
				PathPoint apoint = new PathPoint(apointType, azdSite, apointName);
				PathPoint zpoint = new PathPoint(zpointType, zzdSite, zpointName);
				service.setApoint(apoint);
				service.setZpoint(zpoint);
				service.setAttr(map);
			}
			
			String routePathCuid = IbatisDAOHelper.getStringValue(map, "CUID");//路由与通道关联表的CUID
			String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");//通道ID
			String pathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");//通道类型
			//如果电路路由ID或者路由与通道关联表的ID为空，则跳过
			// TODO 请注释写这句代码的原因或场景
			if(StringUtils.isBlank(traphRouteCuid) || StringUtils.isBlank(routePathCuid))continue;
			
			//封装成{KEY:路由与通道关联表CUID，VALUE:通道对象}
			PathData pathData = service.getRoutePathMap().get(routePathCuid);
			if(pathData==null){
				pathData = new PathData();
				pathData.setServiceCuid(traphCuid);//电路ID
				pathData.setRouteCuid(traphRouteCuid);//路由ID
				pathData.setRoutePathCuid(routePathCuid);//路由与通道关联表ID
				pathData.setBmClassId("ATTTRAPH_ROUTE_TO_PATH");
				pathData.setPathCuid(pathCuid);//通道ID
				pathData.setPathType(pathType);//通道类型
				pathData.setIndexNo(IbatisDAOHelper.getIntValue(map, "INDEX_PATH_ROUTE"));//通道顺序 TODO 请确认注释是否正确，并写出此字段的详细用法
				pathData.addData(map);//存储路由信息
				service.addPathData(pathData);//存储通道对象
			}
			String multiRoutePathCuid = IbatisDAOHelper.getStringValue(map, "MULTI_CUID");//MSTP通道表CUID
			int rowNum = IbatisDAOHelper.getIntValue(map, "ROW_NUM");
			String multiPathCuid = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_CUID");//MSTP通道ID
			String multiPathType = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_TYPE");//MSTP通道类型
			//如果MSTP通道ID不为空，才封装MSTP通道对象，
			//因为查询出的路由数据结构为普通通道和MSTP通道数据平行，所以这里通过MSTP通道ID来判断是否为MSTP通道
			if(!StringUtils.isBlank(multiPathCuid)){
				PathData multPath = new PathData();
				multPath.setServiceCuid(traphCuid);//电路ID
				multPath.setRouteCuid(traphRouteCuid);//电路路由ID
				multPath.setRoutePathCuid(multiRoutePathCuid);//MSTP通道表CUID
				multPath.setBmClassId("ATTEMP_MULTI_PATH");
				multPath.setPathCuid(multiPathCuid);//MSTP通道ID
				multPath.setPathType(multiPathType);//MSTP通道类型
				multPath.setIndexNo(IbatisDAOHelper.getIntValue(map, "COL_NUM"));//TODO 请加上注释
				multPath.setRowNum(rowNum);//TODO 请加上注释
				multPath.setCode(multiPathType);//TODO 请加上注释
				multPath.addData(map);
				pathData.addMultPathList(multPath);//存储MSTP通道，数据存储在普通通道对象的MSTP通道集合里
			}
		}
		return serMap;
	}
	
	/**
	 *  根据调度电路ID，获取电路路由对象的详细信息,
	 *  	放入转接点的数据,放入path的详细数据
	 */
	public Map<String, ServicePath> getRoutePathDetail(List<String> serviceCuidList) {
		//根据调度电路ID，获取电路路由对象的简略信息
		Map<String,ServicePath> serviceMap = this.getRoutePath(serviceCuidList);
		//根据调度电路ID，获取电路转接点数据
		List<PathPoint> pointAllList = this.getIndiPoints(serviceCuidList);
		//把路由信息和转接点信息存储到一起
		List<Map<String,Object>> pathMapList = ServicePath.converServicePathPoint(serviceMap, pointAllList);
		
		//SDH通道ID集合
		List<String> selTransPathCuidList = new ArrayList<String>();
		//文本段ID集合
		List<String> selTextPathCuidList = new ArrayList<String>();
		//自建段ID集合
		List<String> selSelfPathCuidList = new ArrayList<String>();
		//PTN通道ID集合
		List<String> selPtnPathCuidList = new ArrayList<String>();
		//msap通道ID集合
		List<String> selMsapPathCuidList = new ArrayList<String>();
		
		//循环通道数据，获取各通道ID
		for(Map<String, Object> map:pathMapList){
			String pathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");//通道类型
			String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");//通道ID
			if("TRANS_PATH".equals(pathType)){//SDH通道
				selTransPathCuidList.add(pathCuid);
			}else if("ATTEMP_TEXT_PATH".equals(pathType)){//调度文本段
				selTextPathCuidList.add(pathCuid);
			}else if("ATTEMP_SELF_BUILT_PATH".equals(pathType)){//调度自建段
				selSelfPathCuidList.add(pathCuid);
			}else if("ATTEMP_PTN_PATH".equals(pathType)){//调度PTN通道
				selPtnPathCuidList.add(pathCuid);
			}else if("ATTEMP_MSAP_PATH".equals(pathType)){
				selMsapPathCuidList.add(pathCuid);
			}
		}
		
		//SDH通道数据集合
		List<Map<String,Object>> selTransPathList = new ArrayList<Map<String,Object>>();
		//文本段数据集合
		List<Map<String,Object>> selTextPathList = new ArrayList<Map<String,Object>>();
		//自建段数据集合
		List<Map<String,Object>> selSelfPathList = new ArrayList<Map<String,Object>>();
		//PTN通道数据集合
		List<Map<String,Object>> selPtnPathList = new ArrayList<Map<String,Object>>();
		//MSAP通道数据集合
		List<Map<String,Object>> selMsapPathList = new ArrayList<Map<String,Object>>();
		//如果SDH通道ID不为空，通过SDH通道ID，查询SDH通道数据
		if(!selTransPathCuidList.isEmpty()){
			selTransPathList = this.findAttempTransPath(selTransPathCuidList);
		}
		//如果文本段ID不为空，通过文本段ID，查询文本段数据
		if(!selTextPathCuidList.isEmpty()){
			selTextPathList = this.findAttempTextPath(selTextPathCuidList);
		}
		//如果自建段ID不为空，通过自建段ID，查询自建段数据
		if(!selSelfPathCuidList.isEmpty()){
			selSelfPathList = this.findAttempSelfPath(selSelfPathCuidList);
		}
		//如果PTN通道ID不为空，通过PTN通道ID，查询PTN通道数据
		if(!selPtnPathCuidList.isEmpty()){
			selPtnPathList = this.findAttempPtnPath(selPtnPathCuidList);
		}
		//如果MSAP通道ID不为空，通过MSAP通道ID，查询MSAP通道数据
		if(!selMsapPathCuidList.isEmpty()){
			selMsapPathList = this.findAttempMsapPath(selMsapPathCuidList);
		}
		//封装为{KEY:通道ID，VALUE:通道数据}
		Map<String,Map<String,Object>> pathMap = new HashMap<String, Map<String,Object>>();
		//循环SDH通道数据
		for(Map<String,Object> map : selTransPathList){
			pathMap.put(IbatisDAOHelper.getStringValue(map, "PATH_CUID"), map);
		}
		//循环文本段数据
		for(Map<String,Object> map : selTextPathList){
			pathMap.put(IbatisDAOHelper.getStringValue(map, "PATH_CUID"), map);
		}
		//循环自建段数据
		for(Map<String,Object> map : selSelfPathList){
			pathMap.put(IbatisDAOHelper.getStringValue(map, "PATH_CUID"), map);
		}
		//循环PTN通道数据
		for(Map<String,Object> map : selPtnPathList){
			pathMap.put(IbatisDAOHelper.getStringValue(map, "PATH_CUID"), map);
		}
		//循环MSAP通道数据
		for(Map<String,Object> map : selMsapPathList){
			pathMap.put(IbatisDAOHelper.getStringValue(map, "PATH_CUID"), map);
		}
		//循环电路对象
		for(ServicePath service:serviceMap.values()){
			//循环通道对象
			for(PathData pathData:service.getPathList()){
				if(pathData.getMultPathList().isEmpty()){//区分普通通道和MSTP通道的处理方式
					Map<String,Object> data = pathMap.get(pathData.getPathCuid());
					// TODO 请注释为什么加上这句代码的原因
					if(data==null) continue;
					if("TRANS_PATH".equals(pathData.getPathType())){//判断是否为SDH通道，否时，路由描述无需判断方向
						boolean isEqualA = true;//用于判断通道方向为A-Z，还是Z-A
						String atype = pathData.getApoint().getType();//通道A端点类型
						String acuid = pathData.getApoint().getValue();//通道A端点CUID
						//如果通道A端点CUID不为空，才与通道数据里的A端点判断
						// TODO 如果通道端点CUID为空，是否应该直接跳过，或者报错？
						if(StringUtils.isNotBlank(acuid)){
							if(PathPoint.TYPE_TRANS_ELEMENT.equals(atype)){//通道A端点类型为网元
								if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_NE_CUID"))){
									isEqualA = false;
								}
							}else if(PathPoint.TYPE_ROOM.equals(atype)){//通道A端点类型为机房
								if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_ROOM_CUID"))){
									isEqualA = false;
								}
							}else{//其他类型，默认为站点 TODO 是否应该写成else if？可变相校验类型与存储的CUID是否一致
								if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_SITE_CUID"))){
									isEqualA = false;
								}
							}
						}
						
						if(isEqualA){//如果为TRUE，表示通道方向为A-Z，否则为Z-A
							String aRouteDescription = IbatisDAOHelper.getStringValue(data,"A_ROUTE_DESCIPTION");
							if(StringUtils.isBlank(aRouteDescription)) {
								aRouteDescription = IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION");
							}
							
							data.put("ROUTE_DESCRIPTION", aRouteDescription);
						}else {
							String zRouteDescription = IbatisDAOHelper.getStringValue(data,"Z_ROUTE_DESCIPTION");
							if(StringUtils.isBlank(zRouteDescription)) {
								zRouteDescription = IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION");
							}
							
							data.put("ROUTE_DESCRIPTION", zRouteDescription);
							
							//颠倒全部路由数据
							Map<String,Object> attempData = new HashMap<String, Object>();
							attempData.putAll(data);
							for(String key:attempData.keySet()){
								if(key.startsWith("A_")){
									data.put(key.replace("A_", "Z_"), attempData.get(key));
								}else if(key.startsWith("Z_")){
									data.put(key.replace("Z_", "A_"), attempData.get(key));
								}
							}
						}
						pathData.addData(data);
					}else {
						data.put("ROUTE_DESCRIPTION", IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION"));
						pathData.addData(data);
					}
				}else {
					//循环MSTP通道
					for(PathData multiPath:pathData.getMultPathList()){
						Map<String,Object> data = pathMap.get(multiPath.getPathCuid());
						// TODO 请注释为什么加上这句代码的原因
						if(data==null) continue;
						if("TRANS_PATH".equals(multiPath.getPathType())){//判断是否为SDH通道，否时，路由描述无需判断方向
							boolean isEqualA = true;//用于判断通道方向为A-Z，还是Z-A
							String atype = multiPath.getApoint().getType();//通道A端点类型
							String acuid = multiPath.getApoint().getValue();//通道A端点CUID
							//如果通道A端点CUID不为空，才与通道数据里的A端点判断
							// TODO 如果通道端点CUID为空，是否应该直接跳过，或者报错？
							if(StringUtils.isNotBlank(acuid)){
								if(PathPoint.TYPE_TRANS_ELEMENT.equals(atype)){//通道A端点类型为网元
									if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_NE_CUID"))){
										isEqualA = false;
									}
								}else if(PathPoint.TYPE_ROOM.equals(atype)){//通道A端点类型为机房
									if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_ROOM_CUID"))){
										isEqualA = false;
									}
								}else{//其他类型，默认为站点 TODO 是否应该写成else if？可变相校验类型与存储的CUID是否一致
									if(!acuid.equals(IbatisDAOHelper.getStringValue(data, "A_SITE_CUID"))){
										isEqualA = false;
									}
								}
							}
							if(isEqualA){
								String aRouteDescription = IbatisDAOHelper.getStringValue(data,"A_ROUTE_DESCIPTION");
								if(StringUtils.isBlank(aRouteDescription)) {
									aRouteDescription = IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION");
								}
								
								data.put("ROUTE_DESCRIPTION", aRouteDescription);
							}else {
								String zRouteDescription = IbatisDAOHelper.getStringValue(data,"Z_ROUTE_DESCIPTION");
								if(StringUtils.isBlank(zRouteDescription)) {
									zRouteDescription = IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION");
								}
								
								data.put("ROUTE_DESCRIPTION", zRouteDescription);
								
								//颠倒全部路由数据
								Map<String,Object> attempData = new HashMap<String, Object>();
								attempData.putAll(data);
								for(String key:attempData.keySet()){
									if(key.startsWith("A_")){
										data.put(key.replace("A_", "Z_"), attempData.get(key));
									}else if(key.startsWith("Z_")){
										data.put(key.replace("Z_", "A_"), attempData.get(key));
									}
								}
							}
							multiPath.addData(data);
						}else {
							data.put("ROUTE_DESCRIPTION", IbatisDAOHelper.getStringValue(data,"ROUTE_DESCIPTION"));
							multiPath.addData(data);
						}
					}
				}
			}
		}
		
		return serviceMap;
	}
	
	/**
	 * 查询调度SDH路由
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTransPath(List<String> cuidList) {
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		
		return this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectTraphPathsByCuids", pm);
	}
	
	/**
	 * 查询调度文本路由
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTextPath(List<String> cuidList) {
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		
		return this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectAttempTextPathsByCuids", pm);
	}
	
	/**
	 * 查询调度自建段路由
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempSelfPath(List<String> cuidList) {
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		
		return this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectAttempSelfPathsByCuids", pm);
	}
	
	/**
	 * 查询调度PTN路由
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempPtnPath(List<String> cuidList) {
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		
		return this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectAttempPtnPathsByCuids", pm);
	}
	
	public List<Map<String, Object>> findAttempMsapPath(List<String> cuidList) {
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		
		return this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectAttempMsapPathsByCuids", pm);
	}
	
	
	/**
	 * 根据业务明细创建新增电路
	 * @param ac
	 * @param devInfoMap
	 * @param taskMap
	 * @param task
	 * @return
	 */
	public List<String> createAddAttempTraph(ServiceActionContext ac, Map<TraphName, List<AttempTraphService>> devInfoMap, 
			Map<String, TaskInst> taskMap, TaskInst defaultTask) {
		if(devInfoMap == null || devInfoMap.isEmpty()) throw new RuntimeException("业务明细为空！");
		
		Map<ITraphExtendBO,List<ITraphExtend>> extendMap = new HashMap<ITraphExtendBO, List<ITraphExtend>>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		List<String> attempTraphCuidList = new ArrayList<String>();
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		
		for(TraphName traphName : devInfoMap.keySet()) {
			List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
			
			List<AttempTraphService> devInfoList = devInfoMap.get(traphName);
			List<TraphName> nameList = TraphNameBO.getTraphNames(traphName.getaStationId(), traphName.getzStationId(), traphName.getRate(), devInfoList.size());
			
			if(nameList.size() != devInfoList.size()) throw new RuntimeException("生成的电路编号与电路数量不一致！");
			
			
			List<String> changeNameList = this.TraphNameBO.validateTraphLabelCnsUniqu(nameList);
			if(changeNameList != null && !changeNameList.isEmpty()) {
				String errorInfo = StringUtils.join(changeNameList, "\n");
				errorInfo += "\n请修改名称后再提交！";				
				throw new RuntimeException(errorInfo); 
			}
			
			
			for(int i=0; i<devInfoList.size(); i++) {
				TraphName name = nameList.get(i);
				AttempTraphService attempTraph = devInfoList.get(i);
				
				//防止数据地址同享
				Map<String, Object> infoMap = new HashMap<String, Object>();
				infoMap.putAll(attempTraph.getDataMap());
				
				String extIds = IbatisDAOHelper.getStringValue(infoMap, "EXT_IDS");
				String[] exts = StringUtils.split(extIds, ",");
				String ext = "0";
				if (exts != null && exts.length > 0) {
					ext = exts[0];
				}
				logger.info("-----extIds------:"+extIds);
				String serviceName = TraphNameBO.getServiceName(extIds);
				logger.info("-----afterextIds------:"+extIds);
				String attempTraphName = name.getLabelCn() + serviceName;
				String isLte = IbatisDAOHelper.getStringValue(infoMap, "LTE");
				if(StringUtils.isNotEmpty(isLte)&&isLte.equals("0")){
					attempTraphName = attempTraphName+"/P";
				}
				
				String devInfoCuid = IbatisDAOHelper.getStringValue(infoMap, "CUID");
				String attempTraphCuid = StringUtils.replace(devInfoCuid, "T_ACT_ORDER_DETAIL", "ATTEMP_TRAPH");
				String traphDesignUser = IbatisDAOHelper.getStringValue(infoMap, "TRAPH_DESIGN_USER");
				TaskInst task = defaultTask;
				if(defaultTask == null) {
					if(taskMap == null) throw new RuntimeException("当前电路无法和任务建立关联！");
					task = taskMap.get(traphDesignUser);
				}
				if(task == null) {
					throw new RuntimeException("当前电路无法和任务建立关联！");
				} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
					throw new RuntimeException("当前任务已被其它人处理完毕！");
				}
				
				String relatedASiteCuid = IbatisDAOHelper.getStringValue(infoMap, "A_POINT_CUID");
				String relatedZSiteCuid = IbatisDAOHelper.getStringValue(infoMap, "Z_POINT_CUID");
				
				String aPointCuid = attempTraph.getAPointCuid();
				String zPointCuid = attempTraph.getZPointCuid();
				String aPointType = attempTraph.getAPointType();
				String zPointType = attempTraph.getZPointType();
				Map<String,String> stationCuidMap = getAZEndStaction(aPointCuid,zPointCuid);
				
				List<String> cuidList = task2AttempTraphCuidMap.get(task);
				if(cuidList == null) {
					cuidList = new ArrayList<String>();
					task2AttempTraphCuidMap.put(task, cuidList);
				}
				cuidList.add(attempTraphCuid);
				
				String bandWidth = IbatisDAOHelper.getStringValue(infoMap, "BAND_WIDTH");
				
				String relatedDistrictACuid = IbatisDAOHelper.getStringValue(infoMap, "RELATED_DISTRICT_A_CUID");
				String relatedDistrictZCuid = IbatisDAOHelper.getStringValue(infoMap, "RELATED_DISTRICT_Z_CUID");
				
				String vlanId = IbatisDAOHelper.getStringValue(infoMap, "VLANID");
				Integer sortNo = IbatisDAOHelper.getIntValue(infoMap, "SORT_NO");
				
				String endSwitchRoomA = IbatisDAOHelper.getStringValue(infoMap, "END_SWITCH_ROOM_A");
				String endSwitchRoomZ = IbatisDAOHelper.getStringValue(infoMap, "END_SWITCH_ROOM_Z");
				
				attempTraph.setCuid(attempTraphCuid);
				attempTraph.setLabelCn(attempTraphName);
				attempTraph.setRate(name.getRate());
				attempTraph.setNo(name.getNo());
				attempTraph.addData("RELATED_OUT_TRAPH_CUID", IbatisDAOHelper.getStringValue(infoMap, "OUTSIDE_KEY_ID"));
				attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_NEW);
				attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
				attempTraph.addData("REQUEST_DATE", task.getSheetInst().getDueTime());
				attempTraph.addData("BANDWIDTH", bandWidth);
				attempTraph.addData("IS_WHOLE_ROUTE", 0);
				attempTraph.addData("EXT", ext);
				attempTraph.addData("RELATED_A_DISTRICT_CUID", relatedDistrictACuid);
				attempTraph.addData("RELATED_Z_DISTRICT_CUID", relatedDistrictZCuid);
				attempTraph.addData("RELATED_A_ZD_SITE_CUID", relatedASiteCuid);
				attempTraph.addData("RELATED_Z_ZD_SITE_CUID", relatedZSiteCuid);
				attempTraph.addData("ZD_SITE_TYPE_A", aPointType);
				attempTraph.addData("ZD_SITE_TYPE_Z", zPointType);
				attempTraph.addData("RELATED_A_SITE_CUID", relatedASiteCuid);
				attempTraph.addData("RELATED_Z_SITE_CUID", relatedZSiteCuid);
				attempTraph.addData("RELATED_A_END_SITE_CUID", relatedASiteCuid);
				attempTraph.addData("RELATED_Z_END_SITE_CUID", relatedZSiteCuid);
				attempTraph.addData("RELATED_A_END_STATION_CUID", (String)stationCuidMap.get("aSite"));
				attempTraph.addData("RELATED_Z_END_STATION_CUID", (String)stationCuidMap.get("zSite"));
				attempTraph.addData("END_STATION_TYPE_A", (String)stationCuidMap.get("type"));
				attempTraph.addData("END_STATION_TYPE_Z", (String)stationCuidMap.get("type"));
				attempTraph.addData("END_VLANID_A", vlanId);
				attempTraph.addData("END_VLANID_Z", vlanId);
				attempTraph.addData("JHROOM_A", endSwitchRoomA);
				attempTraph.addData("JHROOM_Z", endSwitchRoomZ);
				attempTraph.addData("SORT_NO", sortNo);
				attempTraph.addData("LONG_DISTANCE_TRAPH_NAME", IbatisDAOHelper.getStringValue(infoMap, "MAIN_TRAPH_NAME"));
				if(ext.equals("101")){
					attempTraph.setExtIds(extIds);
				}
				attempTraphList.add(attempTraph);
				if(attempTraph.getExtend()!=null) {
					ITraphExtend traphExtend = attempTraph.getExtend();
					traphExtend.setRelatedServiceCuid(attempTraphCuid);
					String infoType = traphExtend.getInfoType();
					ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
					if(extendBO==null)throw new RuntimeException("无法根据扩展数据类型获取电路扩展信息维护BO，请检查代码AttempTraphService");
					List<ITraphExtend> extendList = extendMap.get(extendBO);
					if(extendList==null){
						extendList = new ArrayList<ITraphExtend>();
						extendMap.put(extendBO, extendList);
					}
					extendList.add(traphExtend);
				}
				
				Map<String, Object> taskServiceMap = new HashMap<String, Object>();
				taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
				taskServiceMap.put("RELATED_ORDER_DETAIL_CUID", devInfoCuid);
				taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
				taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
				taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
				taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
				taskServiceMap.put("SORT_NO", sortNo);
				
				taskServiceList.add(taskServiceMap);
			}
			
			attempTraphCuidList.addAll(this.createAttempTraph(attempTraphList, false));
		}
		
		this.createTaskService(taskServiceList);
		
		for(ITraphExtendBO bo:extendMap.keySet()){
			bo.createExtendInfo(extendMap.get(bo));
		}
		
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				if(this.isSetDefaultRole(t)) {
					this.setDefaultRole(t,cuidList);
				}
			}
		}
		
		return attempTraphCuidList;
	}
	
	/**
	 * 根据电路编号创建新增电路
	 * @param ac
	 * @param noList
	 * @param traph
	 * @param task
	 * @return
	 */
	public List<String> createAddAttempTraph(ServiceActionContext ac, List<Integer> noList, AttempTraphService traph, TaskInst task) {
		if(traph == null) throw new RuntimeException("电路信息为空！");
		
		Map<String, Object> traphInfoMap = traph.getDataMap();
		if(traphInfoMap == null || traphInfoMap.isEmpty()) throw new RuntimeException("电路信息为空！");
		
		if(task == null) {
			throw new RuntimeException("当前电路无法和任务建立关联！");
		} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
			throw new RuntimeException("当前任务已被其它人处理完毕！");
		}
		
		String aPoint = traph.getAPointCuid();
		String zPoint = traph.getZPointCuid();
		
		if(StringUtils.isBlank(aPoint) || StringUtils.isBlank(zPoint)) {
			throw new RuntimeException("起始点、终止点不能为空！");
		}
		
		Integer traphRate = traph.getRate();
		
		if(noList == null || noList.isEmpty()) throw new RuntimeException("电路编号不能为空！");
		
		List<TraphName> tempNameList = new ArrayList<TraphName>();
		for(Integer no : noList) {
			TraphName name = new TraphName(aPoint, zPoint, traphRate);
			name.setNo(no);
			
			tempNameList.add(name);
		}
		
		String labelCnSuffix = IbatisDAOHelper.getStringValue(traphInfoMap, "LABEL_CN_SUFFIX");
		String extIds = IbatisDAOHelper.getStringValue(traphInfoMap, "EXT_IDS");
		String[] exts = StringUtils.split(extIds, ",");
		String ext = "0";
		if (exts != null && exts.length > 0) {
			ext = exts[0];
		}
		List<TraphName> nameList = TraphNameBO.getTraphNames(aPoint, zPoint, traphRate, extIds, labelCnSuffix, noList);
		if(nameList.size() != noList.size()) throw new RuntimeException("生成的电路编号与电路数量不一致！");
		if(SysProperty.getInstance().getValue("districtName").trim().equals("内蒙")||SysProperty.getInstance().getValue("districtName").trim().equals("青海")){
			String notes = IbatisDAOHelper.getStringValue(traphInfoMap, "NOTES");
			if (StringUtils.isNotEmpty(notes)){
				for (TraphName traphName : nameList){
					traphName.setLabelCn(traphName.getLabelCn()+"("+notes+")");
				}
			} 
		}
		if(SysProperty.getInstance().getValue("districtName").trim().equals("重庆")){
			Integer useType = IbatisDAOHelper.getIntValue(traphInfoMap, "USE_TYPE");
			if (useType == 2){
				for (TraphName traphName : nameList){
					traphName.setLabelCn(traphName.getLabelCn()+"/NP");
				}
			} 
		}
		List<String> changeNameList = this.TraphNameBO.validateTraphLabelCnsUniqu(nameList);
		if(changeNameList != null && !changeNameList.isEmpty()) {
			String errorInfo = StringUtils.join(changeNameList, "\n");
			errorInfo += "\n请修改名称后再提交！";
			
			throw new RuntimeException(errorInfo);
		}
		
		Integer maxSortNo = TraphDispatchProBO.selectMaxSortNoByTask(task.getCuid()) + 1;
		
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		Map<ITraphExtendBO,List<ITraphExtend>> extendMap = new HashMap<ITraphExtendBO, List<ITraphExtend>>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < nameList.size(); i++) {
			TraphName name = nameList.get(i);
			
			Integer sortNo = maxSortNo++;
			String attempTraphCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_TRAPH");
			
			AttempTraphService attempTraph = traph.copy();
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn(name.getLabelCn());
			attempTraph.setRate(name.getRate());
			attempTraph.setNo(name.getNo());
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_NEW);
			attempTraph.addData("IS_WHOLE_ROUTE", 0);
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("EXT", ext);
			
			Map<String, Object> dataMap = attempTraph.getDataMap();
			attempTraph.addData("RELATED_A_ZD_SITE_CUID", dataMap.get("RELATED_A_SITE_CUID"));
			attempTraph.addData("RELATED_Z_ZD_SITE_CUID", dataMap.get("RELATED_Z_SITE_CUID"));
			attempTraph.addData("EXT_IDS", dataMap.get("EXT_IDS"));
			
			attempTraphList.add(attempTraph);
			
			if(attempTraph.getExtend()!=null) {
				ITraphExtend traphExtend = attempTraph.getExtend();
				traphExtend.setRelatedServiceCuid(attempTraphCuid);
				String infoType = traphExtend.getInfoType();
				ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
				if(extendBO==null)throw new RuntimeException("无法根据扩展数据类型获取电路扩展信息维护BO，请检查代码AttempTraphService");
				List<ITraphExtend> extendList = extendMap.get(extendBO);
				if(extendList==null){
					extendList = new ArrayList<ITraphExtend>();
					extendMap.put(extendBO, extendList);
				}
				extendList.add(traphExtend);
			}
			
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			
			taskServiceList.add(taskServiceMap);
		}
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, false);
		this.createTaskService(taskServiceList);
		
		for(ITraphExtendBO bo:extendMap.keySet()){
			bo.createExtendInfo(extendMap.get(bo));
		}
		
		if(this.isSetDefaultRole(task)) {
			this.setDefaultRole(task, attempTraphCuidList);
		}
		//设置电路默认施工角色
		setDefaultConstructRole(attempTraphCuidList);
		return attempTraphCuidList;
	}
	
	/**
	 * 根据电路编号创建新增电路
	 * @param ac
	 * @param noList
	 * @param traph
	 * @param task
	 * @return
	 */
	public List<String> createNewAddAttempTraph(ServiceActionContext ac, List<Map> traphNameList, AttempTraphService traph, TaskInst task) {
		if(traph == null) throw new RuntimeException("电路信息为空！");
		
		Map<String, Object> traphInfoMap = traph.getDataMap();
		if(traphInfoMap == null || traphInfoMap.isEmpty()) throw new RuntimeException("电路信息为空！");
		
		if(task == null) {
			throw new RuntimeException("当前电路无法和任务建立关联！");
		} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
			throw new RuntimeException("当前任务已被其它人处理完毕！");
		}
		
		String aPoint = traph.getAPointCuid();
		String zPoint = traph.getZPointCuid();
		
		if(StringUtils.isBlank(aPoint) || StringUtils.isBlank(zPoint)) {
			throw new RuntimeException("起始点、终止点不能为空！");
		}
		
		Integer traphRate = traph.getRate();
		
		String extIds = IbatisDAOHelper.getStringValue(traphInfoMap, "EXT_IDS");
		String[] exts = StringUtils.split(extIds, ",");
		String ext = "0";
		if (exts != null && exts.length > 0) {
			ext = exts[0];
		}

		Integer maxSortNo = TraphDispatchProBO.selectMaxSortNoByTask(task.getCuid()) + 1;
		
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		Map<ITraphExtendBO,List<ITraphExtend>> extendMap = new HashMap<ITraphExtendBO, List<ITraphExtend>>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < traphNameList.size(); i++) {
			Map name = traphNameList.get(i);
			
			Integer sortNo = maxSortNo++;
			String attempTraphCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_TRAPH");
			
			AttempTraphService attempTraph = traph.copy();
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn((String)name.get("label_cn"));
			attempTraph.setRate(traphRate);
			attempTraph.setNo(Integer.parseInt(name.get("no").toString()));
			attempTraph.setAlias((String)name.get("alias"));
			attempTraph.setMainTraphName((String)name.get("main_traph_name"));
			attempTraph.setNotes((String)name.get("notes"));
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_NEW);
			attempTraph.addData("IS_WHOLE_ROUTE", 0);
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("EXT", ext);
			
			Map<String, Object> dataMap = attempTraph.getDataMap();
			attempTraph.addData("RELATED_A_ZD_SITE_CUID", dataMap.get("RELATED_A_SITE_CUID"));
			attempTraph.addData("RELATED_Z_ZD_SITE_CUID", dataMap.get("RELATED_Z_SITE_CUID"));
			attempTraph.addData("EXT_IDS", dataMap.get("EXT_IDS"));
			
			attempTraphList.add(attempTraph);
			
			if(attempTraph.getExtend()!=null) {
				ITraphExtend traphExtend = attempTraph.getExtend();
				traphExtend.setRelatedServiceCuid(attempTraphCuid);
				String infoType = traphExtend.getInfoType();
				ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
				if(extendBO==null)throw new RuntimeException("无法根据扩展数据类型获取电路扩展信息维护BO，请检查代码AttempTraphService");
				List<ITraphExtend> extendList = extendMap.get(extendBO);
				if(extendList==null){
					extendList = new ArrayList<ITraphExtend>();
					extendMap.put(extendBO, extendList);
				}
				extendList.add(traphExtend);
			}
			
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			
			taskServiceList.add(taskServiceMap);
		}
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, false);
		this.createTaskService(taskServiceList);
		
		for(ITraphExtendBO bo:extendMap.keySet()){
			bo.createExtendInfo(extendMap.get(bo));
		}
		
		if(this.isSetDefaultRole(task)) {
			this.setDefaultRole(task, attempTraphCuidList);
		}
		//设置电路默认施工角色
		setDefaultConstructRole(attempTraphCuidList);
		return attempTraphCuidList;
	}
	
	/**
	 * 根据业务明细创建调整电路
	 * @param ac
	 * @param devInfoList
	 * @param taskMap
	 * @param task
	 * @return
	 */
	public List<String> createAdjustAttempTraph(ServiceActionContext ac, List<AttempTraphService> devInfoList, 
			Map<String, TaskInst> taskMap, TaskInst defaultTask) {
		if(devInfoList == null || devInfoList.isEmpty()) throw new RuntimeException("调整后电路为空！");
		
		List<String> relatedTraphCuidList = new ArrayList<String>();
		for(AttempTraphService attempTraph : devInfoList) {
			String relatedTraphCuid = attempTraph.getRelatedTraphCuid();
			if(StringUtils.isNotBlank(relatedTraphCuid)) {
				relatedTraphCuidList.add(relatedTraphCuid);
			}
		}
		
		if(devInfoList.size() != relatedTraphCuidList.size()) {
			throw new RuntimeException("调整后电路与调整前电路数量不一致！");
		}
		
		//施工验证电路可调度
		Map<String, String> validateTraphCuidMap = this.findValidateTraphCuidMap(relatedTraphCuidList);
		List<Map<String, Object>> traphList = ResTraphBO.findTraph(relatedTraphCuidList);
		if(traphList.isEmpty()) throw new RuntimeException("无法查询出调整前电路！");
		
		List<Record> attempTraphPkList = new ArrayList<Record>();
		List<Record> attempTraphParamList = new ArrayList<Record>();
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		Map<ITraphExtendBO,List<ITraphExtend>> extendMap = new HashMap<ITraphExtendBO, List<ITraphExtend>>();
		Map<TraphName, List<AttempTraphService>> changeTraphNameMap = new HashMap<TraphName, List<AttempTraphService>>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		
		Map<String, Map<String, Object>> traphCuid2TraphMap = IbatisDAOHelper.parseList2Map(traphList, "CUID");
		
		for(AttempTraphService attempTraph : devInfoList) {
			String relatedTraphCuid = attempTraph.getRelatedTraphCuid();
			Map<String, Object> traphInfoMap = traphCuid2TraphMap.get(relatedTraphCuid);
			//防止数据地址同享
			Map<String, Object> devInfoMap = new HashMap<String, Object>();
			devInfoMap.putAll(attempTraph.getDataMap());
			
			String traphDesignUser = IbatisDAOHelper.getStringValue(devInfoMap, "TRAPH_DESIGN_USER");
			TaskInst task = defaultTask;
			if(defaultTask == null) {
				if(taskMap == null) throw new RuntimeException("当前电路无法和任务建立关联！");
				task = taskMap.get(traphDesignUser);
			}
			if(task == null) {
				throw new RuntimeException("当前电路无法和任务建立关联！");
			} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
				throw new RuntimeException("当前任务已被其它人处理完毕！");
			}
			
			String traphCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "CUID");
			String attempTraphCuid = StringUtils.replace(traphCuid, "TRAPH", "ATTEMP_TRAPH");
			
			List<String> cuidList = task2AttempTraphCuidMap.get(task);
			if(cuidList == null) {
				cuidList = new ArrayList<String>();
				task2AttempTraphCuidMap.put(task, cuidList);
			}
			cuidList.add(attempTraphCuid);
			
			String devInfoCuid = IbatisDAOHelper.getStringValue(devInfoMap, "CUID");
			Integer sortNo = IbatisDAOHelper.getIntValue(devInfoMap, "SORT_NO");
			
			//全部电路都创建关系，避免直接从施工验证环节转移的电路关系丢失
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_ORDER_DETAIL_CUID", devInfoCuid);
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			
			taskServiceList.add(taskServiceMap);
			
			//为了防止以前的错误数据（替换后的调度电路ID与存量电路ID，但是调度电路所属工单已经归档）
			//而导致之后插入调度电路数据时，CUID重复BUG
			//修改成不用准备调整的调度电路ID判断已经在调度的调度电路，而是用准备调整的存量电路ID判断已经在调度的调整前电路
			//更新调度电路数据时，也使用已经在调度的调度电路ID，而不是准备调整的调度电路ID
			if(validateTraphCuidMap.keySet().contains(traphCuid)) {
				// 修改在施工验证环节的调度电路的数据
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", validateTraphCuidMap.get(traphCuid));
				Record param = new Record("ATTEMP_TRAPH");
				param.addColValue("RELATED_SHEET_ID", task.getSheetInst().getCuid());
				param.addColValue("RELATED_TRAPH_CUID", traphCuid);
				param.addColValue("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_ADJUCT);
				//TODO 综资的电路ID是否会变？
				param.addColValue("RELATED_OUT_TRAPH_CUID", IbatisDAOHelper.getStringValue(devInfoMap, "OUTSIDE_KEY_ID"));
				attempTraphPkList.add(pk);
				attempTraphParamList.add(param);
				
				taskServiceMap.put("RELATED_SERVICE_CUID", validateTraphCuidMap.get(traphCuid));
				
				continue;
			}
			
			String labelCn = IbatisDAOHelper.getStringValue(traphInfoMap, "LABEL_CN");
			Integer no = IbatisDAOHelper.getIntValue(traphInfoMap, "NO");
			Integer traphRate = IbatisDAOHelper.getIntValue(traphInfoMap, "TRAPH_RATE");
			
			String bandWidth = IbatisDAOHelper.getStringValue(devInfoMap, "BAND_WIDTH");
			String endSwitchRoomA = IbatisDAOHelper.getStringValue(devInfoMap, "END_SWITCH_ROOM_A");
			String endSwitchRoomZ = IbatisDAOHelper.getStringValue(devInfoMap, "END_SWITCH_ROOM_Z");
			String relatedDistrictACuid = IbatisDAOHelper.getStringValue(devInfoMap, "RELATED_DISTRICT_A_CUID");
			String relatedDistrictZCuid = IbatisDAOHelper.getStringValue(devInfoMap, "RELATED_DISTRICT_Z_CUID");
			String relatedASiteCuid = IbatisDAOHelper.getStringValue(devInfoMap, "A_POINT_CUID");
			String relatedZSiteCuid = IbatisDAOHelper.getStringValue(devInfoMap, "Z_POINT_CUID");
			

			String oldRelatedASiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_A_SITE_CUID");
			String oldRelatedZSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_Z_SITE_CUID");
			
			
			String newRelatedAEndSiteCuid = attempTraph.getAPointCuid();
			String newRelatedZEndSiteCuid = attempTraph.getZPointCuid();
			String aPointType = attempTraph.getAPointType();
			String zPointType = attempTraph.getZPointType();
			Integer newTraphRate = attempTraph.getRate();
			
			
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn(labelCn);
			attempTraph.setRate(traphRate);
			attempTraph.setNo(no);
			attempTraph.addData(traphInfoMap);
			attempTraph.addData(devInfoMap);
			attempTraph.addData("RELATED_OUT_TRAPH_CUID", IbatisDAOHelper.getStringValue(devInfoMap, "OUTSIDE_KEY_ID"));
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("BANDWIDTH", bandWidth);
			attempTraph.addData("IS_WHOLE_ROUTE", 0);
			attempTraph.addData("RELATED_TRAPH_CUID", traphCuid);
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_ADJUCT);
			attempTraph.addData("RELATED_A_DISTRICT_CUID", relatedDistrictACuid);
			attempTraph.addData("RELATED_Z_DISTRICT_CUID", relatedDistrictZCuid);
			attempTraph.addData("RELATED_A_ZD_SITE_CUID", relatedASiteCuid);
			attempTraph.addData("RELATED_Z_ZD_SITE_CUID", relatedZSiteCuid);
			attempTraph.addData("ZD_SITE_TYPE_A", "SITE");
			attempTraph.addData("ZD_SITE_TYPE_Z", "SITE");
			attempTraph.addData("RELATED_A_SITE_CUID", oldRelatedASiteCuid);
			attempTraph.addData("RELATED_Z_SITE_CUID", oldRelatedZSiteCuid);
			attempTraph.addData("RELATED_A_END_SITE_CUID", relatedASiteCuid);
			attempTraph.addData("RELATED_Z_END_SITE_CUID", relatedZSiteCuid);
			attempTraph.addData("RELATED_A_END_STATION_CUID", newRelatedAEndSiteCuid);
			attempTraph.addData("RELATED_Z_END_STATION_CUID", newRelatedZEndSiteCuid);
			attempTraph.addData("END_STATION_TYPE_A", aPointType);
			attempTraph.addData("END_STATION_TYPE_Z", zPointType);
			attempTraph.addData("JHROOM_A", endSwitchRoomA);
			attempTraph.addData("JHROOM_Z", endSwitchRoomZ);
			attempTraph.addData("LABEL_CN", traphInfoMap);
			String extids = IbatisDAOHelper.getStringValue(devInfoMap, "EXT_IDS");
			if(StringUtils.isBlank(extids)){
				extids = IbatisDAOHelper.getStringValue(traphInfoMap, "EXT_IDS");
			}
			attempTraph.addData("EXT_IDS", extids);
			attempTraphList.add(attempTraph);
			if(attempTraph.getExtend()!=null) {
				ITraphExtend traphExtend = attempTraph.getExtend();
				traphExtend.setRelatedServiceCuid(attempTraphCuid);
				String infoType = traphExtend.getInfoType();
				ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
				if(extendBO==null)throw new RuntimeException("无法根据扩展数据类型获取电路扩展信息维护BO，请检查代码AttempTraphService");
				List<ITraphExtend> extendList = extendMap.get(extendBO);
				if(extendList==null){
					extendList = new ArrayList<ITraphExtend>();
					extendMap.put(extendBO, extendList);
				}
				extendList.add(traphExtend);
			}
			String oldRelatedAEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_A_END_STATION_CUID");
			String oldRelatedZEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_Z_END_STATION_CUID");
			Integer oldTraphRate = IbatisDAOHelper.getIntValue(traphInfoMap, "TRAPH_RATE");
			TraphName oldTraphName = new TraphName(oldRelatedAEndSiteCuid, oldRelatedZEndSiteCuid, oldTraphRate);
			//如果new的AZ站点为空就不生成电路
			if(StringUtils.isNotBlank(newRelatedAEndSiteCuid)&&StringUtils.isNotBlank(newRelatedZEndSiteCuid)&&newTraphRate!=0){
				TraphName newTraphName = new TraphName(newRelatedAEndSiteCuid, newRelatedZEndSiteCuid, newTraphRate);
			
				if(!newTraphName.equals(oldTraphName)) {
					List<AttempTraphService> list = changeTraphNameMap.get(newTraphName);
					if(list == null) {
						list = new ArrayList<AttempTraphService>();
						changeTraphNameMap.put(newTraphName, list);
					}
					list.add(attempTraph);
				}
			}
		}
		
		if(changeTraphNameMap != null && !changeTraphNameMap.isEmpty()) {
			for(TraphName traphName : changeTraphNameMap.keySet()) {
				List<AttempTraphService> traphInfoList = changeTraphNameMap.get(traphName);
				List<TraphName> traphNameList = TraphNameBO.getTraphNames(traphName.getaStationId(), traphName.getzStationId(), traphName.getRate(), traphInfoList.size());
				
				if(traphNameList.size() != traphInfoList.size()) {
					throw new RuntimeException("生成的电路编号与电路数量不一致！");
				}
				
				for(int i=0; i<traphInfoList.size(); i++) {
					TraphName name = traphNameList.get(i);
					AttempTraphService t = traphInfoList.get(i);
					
					String extIds = IbatisDAOHelper.getStringValue(t.getDataMap(), "EXT_IDS");
					String serviceName = TraphNameBO.getServiceName(extIds);
					
					String labelCn = name.getLabelCn();
					t.setNo(name.getNo());
					t.setRate(name.getRate());
					t.addData("RELATED_A_END_STATION_CUID", name.getaStationId());
					t.addData("RELATED_Z_END_STATION_CUID", name.getzStationId());
					t.addData("END_STATION_TYPE_A", name.getaStationType());
					t.addData("END_STATION_TYPE_Z", name.getzStationType());
				}
			}
		}
		
		if(attempTraphParamList != null && !attempTraphParamList.isEmpty()) {
			logger.info("修改在施工验证环节的调度电路的数据");
			this.IbatisResDAO.updateDynamicTableBatch(attempTraphParamList, attempTraphPkList);
			logger.info("删除在施工验证环节的调度电路与任务的关系");
			this.deleteTaskService(new ArrayList<String>(validateTraphCuidMap.values()));
			logger.info("删除在施工验证环节的调度电路与任务扩展的关系");
			this.deleteTaskServiceLink(new ArrayList<String>(validateTraphCuidMap.values()));
		}
		
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, true);
		this.createTaskService(taskServiceList);
		
		for(ITraphExtendBO bo:extendMap.keySet()){
			bo.createExtendInfo(extendMap.get(bo));
		}
		
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				if(this.isSetDefaultRole(t)) {
					//设置默认施工角色
					this.setDefaultRole(t, cuidList);
				}
			}
		}
		
		return attempTraphCuidList;
	}
	
	/**
	 * 创建调整电路
	 * @param ac
	 * @param relatedTraphCuidList
	 * @param task
	 * @return
	 */
	public List<String> createAdjustAttempTraph(ServiceActionContext ac, List<String> relatedTraphCuidList, TaskInst task) {
		if(relatedTraphCuidList == null || relatedTraphCuidList.isEmpty()) {
			throw new RuntimeException("调整前电路为空！");
		}
		
		if(task == null) {
			throw new RuntimeException("当前电路无法和任务建立关联！");
		} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
			throw new RuntimeException("当前任务已被其它人处理完毕！");
		}
		
		//施工验证电路可调度
		Map<String, String> validateTraphCuidMap = this.findValidateTraphCuidMap(relatedTraphCuidList);
		
		List<Map<String, Object>> traphList = ResTraphBO.findTraph(relatedTraphCuidList);
		if(traphList == null || traphList.isEmpty()) throw new RuntimeException("无法查询出调整前电路！");
		
		List<Record> attempTraphPkList = new ArrayList<Record>();
		List<Record> attempTraphParamList = new ArrayList<Record>();
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		List<String> addedTraphCuidList = new ArrayList<String>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		
		Integer maxSortNo = TraphDispatchProBO.selectMaxSortNoByTask(task.getCuid()) + 1;
		
		for(int i=0; i<traphList.size(); i++) {
			Map<String, Object> traphInfoMap = traphList.get(i);
			
			String traphCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "CUID");
			String attempTraphCuid = StringUtils.replace(traphCuid, "TRAPH", "ATTEMP_TRAPH");
			Integer sortNo = maxSortNo++;
			
			//全部电路都创建关系，避免直接从施工验证环节转移的电路关系丢失
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			
			taskServiceList.add(taskServiceMap);
			
			//为了防止以前的错误数据（替换后的调度电路ID与存量电路ID，但是调度电路所属工单已经归档）
			//而导致之后插入调度电路数据时，CUID重复BUG
			//修改成不用准备调整的调度电路ID判断已经在调度的调度电路，而是用准备调整的存量电路ID判断已经在调度的调整前电路
			//更新调度电路数据时，也使用已经在调度的调度电路ID，而不是准备调整的调度电路ID
			if(validateTraphCuidMap.keySet().contains(traphCuid)) {
				// 修改在施工验证环节的调度电路的数据
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", validateTraphCuidMap.get(traphCuid));
				Record param = new Record("ATTEMP_TRAPH");
				param.addColValue("RELATED_SHEET_ID", task.getSheetInst().getCuid());
				param.addColValue("RELATED_TRAPH_CUID", traphCuid);
				param.addColValue("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_ADJUCT);
				
				attempTraphPkList.add(pk);
				attempTraphParamList.add(param);
				
				continue;
			}
			
			addedTraphCuidList.add(traphCuid);
			
			String relatedAEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_A_END_STATION_CUID");
			String relatedZEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_Z_END_STATION_CUID");
			Integer traphRate = IbatisDAOHelper.getIntValue(traphInfoMap, "TRAPH_RATE");
			String labelCn = IbatisDAOHelper.getStringValue(traphInfoMap, "LABEL_CN");
			Integer no = IbatisDAOHelper.getIntValue(traphInfoMap, "NO");
			
			AttempTraphService attempTraph = new AttempTraphService(relatedAEndSiteCuid, relatedZEndSiteCuid, traphRate);
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn(labelCn);
			attempTraph.setRate(traphRate);
			attempTraph.setNo(no);
			attempTraph.addData(traphInfoMap);
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("IS_WHOLE_ROUTE", 1);
			attempTraph.addData("RELATED_TRAPH_CUID", traphCuid);
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_ADJUCT);
			
			attempTraphList.add(attempTraph);
		}
		
		if(attempTraphParamList != null && !attempTraphParamList.isEmpty()) {
			logger.info("修改在施工验证环节的调度电路的数据");
			this.IbatisResDAO.updateDynamicTableBatch(attempTraphParamList, attempTraphPkList);
			logger.info("删除在施工验证环节的调度电路与任务的关系");
			this.deleteTaskService(new ArrayList<String>(validateTraphCuidMap.values()));
			logger.info("删除在施工验证环节的调度电路与任务扩展的关系");
			this.deleteTaskServiceLink(new ArrayList<String>(validateTraphCuidMap.values()));
		}
		
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, true);
		this.createTaskService(taskServiceList);
		
		//复制存量电路业务扩展信息到调度电路中
		Map<String,List<ServiceRel>> relMap = this.findServiceRels(addedTraphCuidList);
		for(String infoType:relMap.keySet()){
			ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
			if(extendBO!=null){
				List<ServiceRel> relList = relMap.get(infoType);
				List<String> infoCuidList = new ArrayList<String>();
				for(ServiceRel rel:relList){
					infoCuidList.add(rel.getInfoCuid());
				}
				extendBO.copyExtendInfoToAttempByInfo(infoCuidList);
			}
		}
		//复制存量电路专网业务信息到调度电路中
		this.copyTraphGroupToAttemp(addedTraphCuidList);
		//复制存量电路上端站信息到调度电路中
		if(SysProperty.getInstance().getValue("districtName").trim().equals("黑龙江")){
//			this.copyTraphUpPortToAttemp(addedTraphCuidList);
		}
		
		if(this.isSetDefaultRole(task)) {
			//设置默认施工角色
			this.setDefaultRole(task, attempTraphCuidList);
		}
		
		return attempTraphCuidList;
	}
	//复制存量电路专网业务信息到调度电路中
	private void copyTraphGroupToAttemp(List<String> addedTraphCuidList) {
		Map mp = new HashMap();
		mp.put("traphCuidList", addedTraphCuidList);
		List<Map<String,Object>> traphGroupList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ResTraph.findTraphGroupRelList",mp);
		if (traphGroupList != null && traphGroupList.size()>0) {
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertToAttempTraphGroupRelation", mp);
		}
	}
	/**
	 * 复制存量电路上端站信息到调度电路中
	 * @param addedTraphCuidList
	 * @return
	 */
/*	private void copyTraphUpPortToAttemp(List<String> addedTraphCuidList) {
		Map mp = new HashMap();
		mp.put("traphCuidList", addedTraphCuidList);
		List<Map<String,Object>> traphUpPortList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findTraphUpPortRelList",mp);
		if (traphUpPortList != null && traphUpPortList.size()>0) {
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertToAttempTraphUpPortRelation", mp);
		}
	}*/

	/**
	 * 根据业务明细创建停闭电路
	 * @param ac
	 * @param devInfoList
	 * @param taskMap
	 * @param task
	 * @return
	 */
	public List<String> createCloseAttempTraph(ServiceActionContext ac, List<AttempTraphService> devInfoList, 
			Map<String, TaskInst> taskMap, TaskInst defaultTask) {
		if(devInfoList == null || devInfoList.isEmpty()) throw new RuntimeException("调整后电路为空！");
		
		List<String> relatedTraphCuidList = new ArrayList<String>();
		for(AttempTraphService attempTraph : devInfoList) {
			String relatedTraphCuid = attempTraph.getRelatedTraphCuid();
			if(StringUtils.isNotBlank(relatedTraphCuid)) {
				relatedTraphCuidList.add(relatedTraphCuid);
			}
		}
		
		//施工验证电路可调度
		Map<String, String> validateTraphCuidMap = this.findValidateTraphCuidMap(relatedTraphCuidList);
		
		List<Map<String, Object>> traphList = ResTraphBO.findTraph(relatedTraphCuidList);
		if(traphList.isEmpty()) throw new RuntimeException("无法查询出调整前电路！");
		
		List<Record> attempTraphPkList = new ArrayList<Record>();
		List<Record> attempTraphParamList = new ArrayList<Record>();
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		Map<TaskInst, List<String>> task2AttempTraphCuidMap = new HashMap<TaskInst, List<String>>();
		
		Map<String, Map<String, Object>> traphCuid2TraphMap = IbatisDAOHelper.parseList2Map(traphList, "CUID");
		
		for(AttempTraphService tempAttempTraph : devInfoList) {
			String relatedTraphCuid = tempAttempTraph.getRelatedTraphCuid();
			Map<String, Object> traphInfoMap = traphCuid2TraphMap.get(relatedTraphCuid);
			
			//防止数据地址共享
			Map<String, Object> devInfoMap = new HashMap<String, Object>();
			devInfoMap.putAll(tempAttempTraph.getDataMap());
			
			String traphDesignUser = IbatisDAOHelper.getStringValue(devInfoMap, "TRAPH_DESIGN_USER");
			TaskInst task = defaultTask;
			if(defaultTask == null) {
				if(taskMap == null) throw new RuntimeException("当前电路无法和任务建立关联！");
				task = taskMap.get(traphDesignUser);
			}
			if(task == null) {
				throw new RuntimeException("当前电路无法和任务建立关联！");
			} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
				throw new RuntimeException("当前任务已被其它人处理完毕！");
			}
			
			String traphCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "CUID");
			String attempTraphCuid = StringUtils.replace(traphCuid, "TRAPH", "ATTEMP_TRAPH");
			
			List<String> cuidList = task2AttempTraphCuidMap.get(task);
			if(cuidList == null) {
				cuidList = new ArrayList<String>();
				task2AttempTraphCuidMap.put(task, cuidList);
			}
			cuidList.add(attempTraphCuid);
			
			String devInfoCuid = IbatisDAOHelper.getStringValue(devInfoMap, "CUID");
			Integer sortNo = IbatisDAOHelper.getIntValue(devInfoMap, "SORT_NO");
			
			//全部电路都创建关系，避免直接从施工验证环节转移的电路关系丢失
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_ORDER_DETAIL_CUID", devInfoCuid);
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			taskServiceList.add(taskServiceMap);
			
			//为了防止以前的错误数据（替换后的调度电路ID与存量电路ID，但是调度电路所属工单已经归档）
			//而导致之后插入调度电路数据时，CUID重复BUG
			//修改成不用准备停闭的调度电路ID判断已经在调度的调度电路，而是用准备停闭的存量电路ID判断已经在调度的调整前电路
			//更新调度电路数据时，也使用已经在调度的调度电路ID，而不是准备停闭的调度电路ID
			if(validateTraphCuidMap.keySet().contains(traphCuid)) {
				// 修改在施工验证环节的调度电路的数据
				logger.info("-----validateTraphCuidMap----");
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", validateTraphCuidMap.get(traphCuid));
				Record param = new Record("ATTEMP_TRAPH");
				param.addColValue("RELATED_SHEET_ID", task.getRelatedSheetCuid());
				param.addColValue("RELATED_TRAPH_CUID", traphCuid);
				param.addColValue("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_CLOSE);
				param.addColValue("IS_WHOLE_ROUTE", 1);
				param.addColValue("RELATED_OUT_TRAPH_CUID", IbatisDAOHelper.getStringValue(devInfoMap, "OUTSIDE_KEY_ID"));
				attempTraphPkList.add(pk);
				attempTraphParamList.add(param);
				
				taskServiceMap.put("RELATED_SERVICE_CUID", validateTraphCuidMap.get(traphCuid));

				continue;
			}
			
			String relatedAEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_A_END_STATION_CUID");
			String relatedZEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_Z_END_STATION_CUID");
			Integer traphRate = IbatisDAOHelper.getIntValue(traphInfoMap, "TRAPH_RATE");
			String labelCn = IbatisDAOHelper.getStringValue(traphInfoMap, "LABEL_CN");
			Integer no = IbatisDAOHelper.getIntValue(traphInfoMap, "NO");
			
			String bandWidth = IbatisDAOHelper.getStringValue(devInfoMap, "BAND_WIDTH");
			String endSwitchRoomA = IbatisDAOHelper.getStringValue(devInfoMap, "END_SWITCH_ROOM_A");
			String endSwitchRoomZ = IbatisDAOHelper.getStringValue(devInfoMap, "END_SWITCH_ROOM_Z");
			String relatedDistrictACuid = IbatisDAOHelper.getStringValue(devInfoMap, "RELATED_DISTRICT_A_CUID");
			String relatedDistrictZCuid = IbatisDAOHelper.getStringValue(devInfoMap, "RELATED_DISTRICT_Z_CUID");
			String relatedASiteCuid = IbatisDAOHelper.getStringValue(devInfoMap, "A_POINT_CUID");
			String relatedZSiteCuid = IbatisDAOHelper.getStringValue(devInfoMap, "Z_POINT_CUID");
			
			AttempTraphService attempTraph = new AttempTraphService(relatedAEndSiteCuid, relatedZEndSiteCuid, traphRate);
			String aPointCuid = attempTraph.getAPointCuid();
			String zPointCuid = attempTraph.getZPointCuid();
			String aPointType = attempTraph.getAPointType();
			String zPointType = attempTraph.getZPointType();
			
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn(labelCn);
			attempTraph.setRate(traphRate);
			attempTraph.setNo(no);
			attempTraph.addData(traphInfoMap);
			attempTraph.addData(devInfoMap);
			attempTraph.addData("RELATED_OUT_TRAPH_CUID", IbatisDAOHelper.getStringValue(devInfoMap, "OUTSIDE_KEY_ID"));
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("BANDWIDTH", bandWidth);
			attempTraph.addData("IS_WHOLE_ROUTE", 1);
			attempTraph.addData("RELATED_TRAPH_CUID", traphCuid);
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_CLOSE);
			attempTraph.addData("RELATED_A_DISTRICT_CUID", relatedDistrictACuid);
			attempTraph.addData("RELATED_Z_DISTRICT_CUID", relatedDistrictZCuid);
			attempTraph.addData("RELATED_A_ZD_SITE_CUID", relatedASiteCuid);
			attempTraph.addData("RELATED_Z_ZD_SITE_CUID", relatedZSiteCuid);
			attempTraph.addData("ZD_SITE_TYPE_A", "SITE");
			attempTraph.addData("ZD_SITE_TYPE_Z", "SITE");
			attempTraph.addData("RELATED_A_SITE_CUID", relatedASiteCuid);
			attempTraph.addData("RELATED_Z_SITE_CUID", relatedZSiteCuid);
			attempTraph.addData("RELATED_A_END_SITE_CUID", relatedASiteCuid);
			attempTraph.addData("RELATED_Z_END_SITE_CUID", relatedZSiteCuid);
			attempTraph.addData("RELATED_A_END_STATION_CUID", aPointCuid);
			attempTraph.addData("RELATED_Z_END_STATION_CUID", zPointCuid);
			attempTraph.addData("END_STATION_TYPE_A", aPointType);
			attempTraph.addData("END_STATION_TYPE_Z", zPointType);
			attempTraph.addData("JHROOM_A", endSwitchRoomA);
			attempTraph.addData("JHROOM_Z", endSwitchRoomZ);
			String extids = IbatisDAOHelper.getStringValue(devInfoMap, "EXT_IDS");
			if(StringUtils.isBlank(extids)){
				extids = IbatisDAOHelper.getStringValue(traphInfoMap, "EXT_IDS");
			}
			attempTraph.addData("EXT_IDS", extids);
			attempTraphList.add(attempTraph);
		}
		
		if(attempTraphParamList != null && !attempTraphParamList.isEmpty()) {
			logger.info("修改在施工验证环节的调度电路的数据");
			this.IbatisResDAO.updateDynamicTableBatch(attempTraphParamList, attempTraphPkList);
			logger.info("删除在施工验证环节的调度电路与任务的关系");
			this.deleteTaskService(new ArrayList<String>(validateTraphCuidMap.values()));
			logger.info("删除在施工验证环节的调度电路与任务扩展的关系");
			this.deleteTaskServiceLink(new ArrayList<String>(validateTraphCuidMap.values()));
		}
		
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, false);
		this.createTaskService(taskServiceList);
		
		if(task2AttempTraphCuidMap != null && !task2AttempTraphCuidMap.isEmpty()) {
			for(TaskInst t : task2AttempTraphCuidMap.keySet()) {
				List<String> cuidList = task2AttempTraphCuidMap.get(t);
				if(this.isSetDefaultRole(t)) {
					//设置默认施工角色
					this.setDefaultRole(t, cuidList);
				}
			}
		}
		
		return attempTraphCuidList;
	}
	
	/**
	 * 创建停闭电路
	 * @param ac
	 * @param relatedTraphCuidList
	 * @param task
	 * @return
	 */
	public List<String> createCloseAttempTraph(ServiceActionContext ac, List<String> relatedTraphCuidList, TaskInst task) {
		if(relatedTraphCuidList == null || relatedTraphCuidList.isEmpty()) {
			throw new RuntimeException("调整前电路为空！");
		}
		
		if(task == null) {
			throw new RuntimeException("当前电路无法和任务建立关联！");
		} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
			throw new RuntimeException("当前任务已被其它人处理完毕！");
		}
		
		//施工验证电路可调度
		Map<String, String> validateTraphCuidMap = this.findValidateTraphCuidMap(relatedTraphCuidList);
		
		List<Map<String, Object>> traphList = ResTraphBO.findTraph(relatedTraphCuidList);
		if(traphList.isEmpty()) throw new RuntimeException("无法查询出调整前电路！");
		
		List<Record> attempTraphPkList = new ArrayList<Record>();
		List<Record> attempTraphParamList = new ArrayList<Record>();
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		
		Integer maxSortNo = TraphDispatchProBO.selectMaxSortNoByTask(task.getCuid()) + 1;
		
		for(int i=0; i<traphList.size(); i++) {
			Map<String, Object> traphInfoMap = traphList.get(i);
			
			String traphCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "CUID");
			String attempTraphCuid = StringUtils.replace(traphCuid, "TRAPH", "ATTEMP_TRAPH");
			Integer sortNo = maxSortNo++;
			
			//全部电路都创建关系，避免直接从施工验证环节转移的电路关系丢失
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_ORDER_CUID", task.getRelatedOrderCuid());
			taskServiceMap.put("RELATED_SHEET_CUID", task.getRelatedSheetCuid());
			taskServiceMap.put("RELATED_TASK_CUID", task.getCuid());
			taskServiceMap.put("RELATED_SERVICE_CUID", attempTraphCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "ATTEMP_TRAPH");
			taskServiceMap.put("SORT_NO", sortNo);
			
			taskServiceList.add(taskServiceMap);
			
			//为了防止以前的错误数据（替换后的调度电路ID与存量电路ID，但是调度电路所属工单已经归档）
			//而导致之后插入调度电路数据时，CUID重复BUG
			//修改成不用准备停闭的调度电路ID判断已经在调度的调度电路，而是用准备停闭的存量电路ID判断已经在调度的调整前电路
			//更新调度电路数据时，也使用已经在调度的调度电路ID，而不是准备停闭的调度电路ID
			if(validateTraphCuidMap.keySet().contains(traphCuid)) {
				// 修改在施工验证环节的调度电路的数据
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", validateTraphCuidMap.get(traphCuid));
				Record param = new Record("ATTEMP_TRAPH");
				param.addColValue("RELATED_SHEET_ID", task.getSheetInst().getCuid());
				param.addColValue("RELATED_TRAPH_CUID", traphCuid);
				param.addColValue("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_CLOSE);
				param.addColValue("IS_WHOLE_ROUTE", 1);

				attempTraphPkList.add(pk);
				attempTraphParamList.add(param);

				continue;
			}
			
			String relatedAEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_A_END_STATION_CUID");
			String relatedZEndSiteCuid = IbatisDAOHelper.getStringValue(traphInfoMap, "RELATED_Z_END_STATION_CUID");
			Integer traphRate = IbatisDAOHelper.getIntValue(traphInfoMap, "TRAPH_RATE");
			String labelCn = IbatisDAOHelper.getStringValue(traphInfoMap, "LABEL_CN");
			Integer no = IbatisDAOHelper.getIntValue(traphInfoMap, "NO");
			
			AttempTraphService attempTraph = new AttempTraphService(relatedAEndSiteCuid, relatedZEndSiteCuid, traphRate);
			attempTraph.setCuid(attempTraphCuid);
			attempTraph.setLabelCn(labelCn);
			attempTraph.setRate(traphRate);
			attempTraph.setNo(no);
			attempTraph.addData(traphInfoMap);
			attempTraph.addData("SORT_NO", sortNo);
			attempTraph.addData("IS_WHOLE_ROUTE", 1);
			attempTraph.addData("RELATED_TRAPH_CUID", traphCuid);
			attempTraph.addData("RELATED_SHEET_ID", task.getRelatedSheetCuid());
			attempTraph.addData("SCHEDULE_TYPE", SheetConstants.SCHEDULE_TYPE_CLOSE);
			
			attempTraphList.add(attempTraph);
		}
		
		if(attempTraphParamList != null && !attempTraphParamList.isEmpty()) {
			logger.info("修改在施工验证环节的调度电路的数据");
			this.IbatisResDAO.updateDynamicTableBatch(attempTraphParamList, attempTraphPkList);
			logger.info("删除在施工验证环节的调度电路与任务的关系");
			this.deleteTaskService(new ArrayList<String>(validateTraphCuidMap.values()));
			logger.info("删除在施工验证环节的调度电路与任务扩展的关系");
			this.deleteTaskServiceLink(new ArrayList<String>(validateTraphCuidMap.values()));
		}
		
		List<String> attempTraphCuidList = this.createAttempTraph(attempTraphList, false);
		this.createTaskService(taskServiceList);
		
		if(this.isSetDefaultRole(task)) {
			//设置默认施工角色
			this.setDefaultRole(task, attempTraphCuidList);
		}
		
		return attempTraphCuidList;
	}
	
	/**
	 * 综资回写调度电路
	 * @param ac
	 * @param taskId
	 * @param attempTraphCuidList
	 */
	public void writeIrmsService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		try {
			//TODO 如果集群情况下，需要使用数据库锁或者统一缓存锁;
			this.setLock(serviceCuidList);
			// 综资回写校验时隙占用
			Map<String, Object> resultMap = this.validateRepeatCtp(serviceCuidList);
			if(resultMap != null && !resultMap.isEmpty()) {
				Integer success = IbatisDAOHelper.getIntValue(resultMap, "success");
				String msg = IbatisDAOHelper.getStringValue(resultMap, "msg");
				if(success == 0) {
					throw new RuntimeException(msg);
				}
			}
			
			List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
			if(attempTraphList!=null&&attempTraphList.size()>0){
				TaskInst task = ProcessBO.getTaskInstByTaskId(taskId);
				List<String> outIdList = new ArrayList<String>();
				List<String> errorTraphNameList = new ArrayList<String>();
				for(Map<String, Object> map : attempTraphList) {
					String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
					String relatedSheetId = IbatisDAOHelper.getStringValue(map, "RELATED_SHEET_ID");
					if(!task.getRelatedSheetCuid().equals(relatedSheetId)) {
						errorTraphNameList.add(labelCn);
					}
				}
				
				if(!errorTraphNameList.isEmpty()) {
					String errorInfo = "工单ID【"+task.getRelatedSheetCuid()+"】与电路\n";
					errorInfo += StringUtils.join(errorTraphNameList, ",\n")+"\n记录的工单ID不一致！";
					
					throw new RuntimeException(errorInfo);
				}
				
				AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_WRITE);
				attempLog.setRelatedTaskCuid(taskId);
				this.writeServiceLog(attempLog);
				//标记业务明细的状态为完成
				FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
				this.writeAttempTraph(ac, serviceCuidList);
				
				// 修改与任务的关系状态为已回写
				ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"综资回写");
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}finally{
			//解锁
			this.clearLock(serviceCuidList);
		}
	}
	
	/**
	 * 回写调度电路，并修改与任务的关系状态
	 * @param ac
	 * @param taskId
	 * @param taskServiceCuidList
	 */
	public void writeTnmsService(ServiceActionContext ac, String taskId, List<String> taskServiceCuidList) {
		if(taskServiceCuidList == null || taskServiceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		List<Map<String, Object>> attempTraphList = this.findServiceByTaskService(taskServiceCuidList);
		if(attempTraphList != null && !attempTraphList.isEmpty()) {
			TaskInst task = ProcessBO.getTaskInstByTaskId(taskId);
			
			List<String> errorTraphNameList = new ArrayList<String>();
			List<String> attempTraphCuidList = new ArrayList<String>();
			
			for(Map<String, Object> map : attempTraphList) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				if(StringUtils.isNotBlank(cuid)) attempTraphCuidList.add(cuid);
				
				String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				String relatedSheetId = IbatisDAOHelper.getStringValue(map, "RELATED_SHEET_ID");
				if(!task.getRelatedSheetCuid().equals(relatedSheetId)) {
					errorTraphNameList.add(labelCn);
				}
			}
			
			if(!errorTraphNameList.isEmpty()) {
				String errorInfo = "工单ID【"+task.getRelatedSheetCuid()+"】与电路\n";
				errorInfo += StringUtils.join(errorTraphNameList, ",\n")+"\n记录的工单ID不一致！";
				
				throw new RuntimeException(errorInfo);
			}
			try {
				//TODO 如果集群情况下，需要使用数据库锁或者统一缓存锁;
				this.setLock(attempTraphCuidList);
				// 记录变更日志
				AttempLog attempLog = new AttempLog(attempTraphCuidList, AttempLog.LOG_ACTION_CODE_TNMS_WRITE);
				attempLog.setRelatedTaskCuid(taskId);
				this.writeServiceLog(attempLog);
				//标记业务明细的状态为完成
				FacedeOrderMaintainBO.updateOrderDetailStateEndByService(attempTraphCuidList, getServiceTableName());
				
				this.writeAttempTraph(ac, attempTraphCuidList);
				// 修改与任务的关系状态为已回写
				ProcessBO.updateTaskServiceStateByTask2Service(taskServiceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"传输回写");
				//调用施工结果推送接口
				TnmsCircuitClient.updateCircuitConstructResult(ac, task, attempTraphCuidList, false);
				//内蒙接口施工完成推送
				if(SysProperty.getInstance().getValue("districtName").trim().equals("内蒙")||SysProperty.getInstance().getValue("districtName").trim().equals("内蒙古"))
				{
				  AttempXToEomsServiceBO axt=(AttempXToEomsServiceBO)SpringContextUtil.getBean("AttempXToEomsServiceWs");
				  axt.workFinshNotice2Emos(task);
				}
			} catch (Exception e) {
				e.printStackTrace();
				
				String msg = "回写电路异常：异常原因："+e.getMessage();
				logger.debug(msg);
				
				throw new RuntimeException(msg);
			}finally{
				//解锁
				this.clearLock(attempTraphCuidList);
			}
		}
	}
	
	/**
	 * 回写调度电路，并修改与任务的关系状态
	 * @param ac
	 * @param taskId
	 * @param taskServiceCuidList
	 */
	public void writeSysService(ServiceActionContext ac, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		try {
			//TODO 如果集群情况下，需要使用数据库锁或者统一缓存锁;
			this.setLock(serviceCuidList);
			// 记录变更日志
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_WRITE);
			attempLog.setRelatedTaskCuid(ac.getUserName()+"按照电路回写");
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			this.writeAttempTraph(ac, serviceCuidList);
			// 修改与任务的关系状态为已释放
			ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_SUCCESS, ac.getUserName()+"按照电路回写");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}finally{
			//解锁
			this.clearLock(serviceCuidList);
		}
	}
	
	public void setLock(List<String> serviceCuidList){
		for(String cuid:serviceCuidList){
			if(cuid!=null){
				if(attempTraphLock.containsKey(cuid)){
					String msg = "当前电路["+cuid+"]正在回写中，请稍后再试！";
					logger.error(msg);
					throw new RuntimeException(msg);
				}
				attempTraphLock.put(cuid, 0);
			}
		}
	}
	
	public void clearLock(List<String> serviceCuidList){
		for(String cuid:serviceCuidList){
			attempTraphLock.remove(cuid);
		}
	}
	
	/**
	 * 回写调度电路，需自行写日志
	 * @param ac
	 * @param attempTraphCuidList
	 */
	private void writeAttempTraph(ServiceActionContext ac, List<String> attempTraphCuidList) {
		if(attempTraphCuidList == null || attempTraphCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		
		try {
			Map<String, Object> pm = new HashMap<String, Object>();
			List<Map<String, Object>> attempTraphList = this.findAttempTraph(attempTraphCuidList);
			if(!attempTraphList.isEmpty()) {
				pm.clear();
				pm.put("cuidList", attempTraphCuidList);
				List<Map<String, Object>> validateAttempTraphNameList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".validateAttempTraphName", pm);
				
				List<String> insertAttempTraphCuidList = new ArrayList<String>();
				List<String> traphCuidList = new ArrayList<String>();
				List<String> outIdList = new ArrayList<String>();
				
				for(Map<String, Object> map : attempTraphList) {
					String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
					Integer scheduleType = IbatisDAOHelper.getIntValue(map, "SCHEDULE_TYPE");
					String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
					String outId = IbatisDAOHelper.getStringValue(map, "RELATED_OUT_TRAPH_CUID");
					if(!SheetConstants.SCHEDULE_TYPE_CLOSE.equals(scheduleType)) {
						insertAttempTraphCuidList.add(cuid);
					}
					if(StringUtils.isNotBlank(outId)) outIdList.add(outId);
					if(StringUtils.isNotBlank(relatedTraphCuid)) traphCuidList.add(relatedTraphCuid);
				}
				
				if(validateAttempTraphNameList != null && !validateAttempTraphNameList.isEmpty()) {
					List<String> errorInfoList = new ArrayList<String>(); 
					
					for(Map<String, Object> map : validateAttempTraphNameList) {
						String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
						String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
						
						if(!traphCuidList.contains(relatedTraphCuid)) {
							errorInfoList.add(labelCn);
						}
					}
					
					if(errorInfoList != null && !errorInfoList.isEmpty()) {
						String errorInfo = StringUtils.join(errorInfoList, "\n");
						errorInfo += "电路名称与存量的重复！";
						
						throw new RuntimeException(errorInfo);
					}
				}
				
				if(!traphCuidList.isEmpty()) {
					// 删除存量电路
					ResTraphBO.deleteTraph(ac, traphCuidList);
				}
				if(!outIdList.isEmpty()){
					//删除接口数据
					FacedeOrderMaintainBO.deleteIfaceDetail(ac, outIdList);
				}
				
				if(insertAttempTraphCuidList != null && !insertAttempTraphCuidList.isEmpty()) {
					// 插入非停闭的调度电路
					this.insertAttempTraph(ac, insertAttempTraphCuidList);
				}
				
				// 删除调度电路的关系
				this.releaseAttempTraphRelation(ac, attempTraphCuidList);
				
				// 删除调度电路
				pm.clear();
				pm.put("cuidList", attempTraphCuidList);
				List<Map<String,Object>> attempTraphExtend = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphExtend", pm);
				logger.info("删除调度电路");
				if (attempTraphExtend.size()>0){
					this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraphExtend", pm);
				}
				
				this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraph", pm);
				//解锁
				this.clearLock(attempTraphCuidList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			String msg = "回写电路异常：异常原因："+e.getMessage();
			logger.debug(msg);
			
			throw new RuntimeException(msg);
		} finally {
			
		}
	}
	
	/**
	 * 传输内部删除电路，并释放电路的关系
	 * @param ac
	 * @param task
	 * @param attempTraphCuidList
	 */
	public void deleteAttempTraph(ServiceActionContext ac, TaskInst task, List<String> attempTraphCuidList) {
		this.deleteAttempTraph(ac, task.getSheetInst().getCuid(), attempTraphCuidList);
	}
	
	/**
	 * 综资释放电路，记录变更日志
	 * @param ac
	 * @param attempTraphCuidList
	 */
	public void releaseIrmsService(ServiceActionContext ac, String taskId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
		TaskInst task = ProcessBO.getTaskInstByTaskId(taskId);
		
		List<String> errorTraphNameList = new ArrayList<String>();
		for(Map<String, Object> map : attempTraphList) {
			String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
			String relatedSheetId = IbatisDAOHelper.getStringValue(map, "RELATED_SHEET_ID");
			if(!task.getRelatedSheetCuid().equals(relatedSheetId)) {
				errorTraphNameList.add(labelCn);
			}
		}
		
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_RELEASE);
		attempLog.setRelatedTaskCuid(taskId);
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		
		this.releaseAttempTraph(ac, serviceCuidList);
		
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(taskId,serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"综资回写");
	}
	
	/**
	 * 工单作废，释放资源
	 * @param ac
	 * @param sheetId
	 * @param serviceCuidList
	 */
	public void deleteSheetService(ServiceActionContext ac, String sheetId, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		if(StringUtils.isBlank(sheetId)) {
			throw new RuntimeException("无回写工单ID！");
		}
		
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_IRMS_RELEASE);
		attempLog.setRelatedTaskCuid(ac.getUserName()+"作废工单");
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		
		this.releaseAttempTraph(ac, serviceCuidList);
		
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"作废工单");
	}
	
	/**
	 * 传输驳回
	 * @param ac
	 * @param taskId
	 * @param attempTraphCuidList
	 */
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
		this.releaseAttempTraph(ac, serviceCuidList);
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(taskId, serviceCuidList,SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"传输驳回，删除资源");
	}

	/**
	 * 传输释放电路
	 * 改变业务明细状态
	 * 改变任务状态
	 * 记录变更日志
	 * @param ac
	 * @param taskServiceCuidList
	 */
	public void releaseTnmsService(ServiceActionContext ac, String taskId, List<String> taskServiceCuidList) {
		if(taskServiceCuidList == null || taskServiceCuidList.isEmpty()) {
			throw new RuntimeException("无回写任务资源关系ID！");
		}
		if(StringUtils.isBlank(taskId)) {
			throw new RuntimeException("无回写任务ID！");
		}
		List<Map<String, Object>> attempTraphList = this.findServiceByTaskService(taskServiceCuidList);
		if(attempTraphList != null && !attempTraphList.isEmpty()) {
			TaskInst task = ProcessBO.getTaskInstByTaskId(taskId);
			
			List<String> errorTraphNameList = new ArrayList<String>();
			List<String> serviceCuidList = new ArrayList<String>();
			
			for(Map<String, Object> map : attempTraphList) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				if(StringUtils.isNotBlank(cuid)) serviceCuidList.add(cuid);
				
				String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
				String relatedSheetId = IbatisDAOHelper.getStringValue(map, "RELATED_SHEET_ID");
				if(!task.getRelatedSheetCuid().equals(relatedSheetId)) {
					errorTraphNameList.add(labelCn);
				}
			}
			
			if(!errorTraphNameList.isEmpty()) {
				String errorInfo = "工单ID【"+task.getRelatedSheetCuid()+"】与电路\n";
				errorInfo += StringUtils.join(errorTraphNameList, ",\n")+"\n记录的工单ID不一致！";
				
				throw new RuntimeException(errorInfo);
			}
			
			// 记录变更日志
			AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_RELEASE);
			attempLog.setRelatedTaskCuid(taskId);
			this.writeServiceLog(attempLog);
			//标记业务明细的状态为完成
			FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
			
			this.releaseAttempTraph(ac, serviceCuidList);
			
			// 修改与任务的关系状态为已释放
			ProcessBO.updateTaskServiceStateByTask2Service(taskServiceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"传输释放");
		}
	}
	
	/**
	 * 传输系统管理员释放电路，记录变更日志
	 * @param ac
	 * @param attempTraphCuidList
	 */
	public void releaseSysService(ServiceActionContext ac, List<String> serviceCuidList) {
		if(serviceCuidList == null || serviceCuidList.isEmpty()) {
			throw new RuntimeException("无回写电路ID！");
		}
		// 记录变更日志
		AttempLog attempLog = new AttempLog(serviceCuidList, AttempLog.LOG_ACTION_CODE_TNMS_RELEASE);
		attempLog.setRelatedTaskCuid(ac.getUserName()+"按照电路释放");
		this.writeServiceLog(attempLog);
		//标记业务明细的状态为完成
		FacedeOrderMaintainBO.updateOrderDetailStateEndByService(serviceCuidList, getServiceTableName());
		this.releaseAttempTraph(ac, serviceCuidList);
		// 修改与任务的关系状态为已释放
		ProcessBO.updateTaskServiceState(serviceCuidList, SheetConstants.SERVICE_STATE_WRITE_RELEASE, ac.getUserName()+"按照电路释放");
	}
	
	/**
	 * 传输电路删除
	 * 清空业务明细的电路关联信息
	 * @param ac
	 * @param sheetId
	 * @param serviceCuidList
	 */
	private void deleteAttempTraph(ServiceActionContext ac, String sheetId, List<String> serviceCuidList){
		if(serviceCuidList == null || serviceCuidList.isEmpty()) throw new RuntimeException("需要删除的资源为空！");
//		this.setLock(serviceCuidList);
		Map<String, Object> pm = new HashMap<String, Object>();
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
		if(attempTraphList.isEmpty()) throw new RuntimeException("无法查询出调度电路！");
		
		List<String> traphCuidList = new ArrayList<String>();
		for(Map<String, Object> map : attempTraphList) {
			String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
			
			if(StringUtils.isNotBlank(relatedTraphCuid)) {
				traphCuidList.add(relatedTraphCuid);
			}
		}
		
		if(traphCuidList != null && !traphCuidList.isEmpty()) {
			ResTraphBO.updateServiceSchduleState(traphCuidList, SheetConstants.SCHEDULE_STATE_END);
		}
		
		//根据工单ID和调度电路ID，清除屏蔽告警表
//		this.deleteAlarmTask(sheetId, attempTraphCuidList);
		
		// 释放调度电路的关系
		this.releaseAttempTraphRelation(ac, serviceCuidList);
		
		// 删除电路与任务的关系
		this.deleteTaskService(serviceCuidList);
		
		// 删除资源的任务链
		this.deleteTaskServiceLink(serviceCuidList);
		
		// 清空业务明细冗余存的资源ID
		logger.info("置空业务明细中调度电路ID");
		FacedeOrderMaintainBO.clearOrderDetailByService(ac, serviceCuidList, this.getServiceTableName());
		
		pm.clear();
		pm.put("cuidList", serviceCuidList);
		logger.info("删除调度电路");
		List<Map<String,Object>> attempTraphExtList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphExtend", pm);
		logger.info("删除调度电路");
		if (attempTraphExtList.size()>0){
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraphExtend", pm);
		}
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraph", pm);
//		this.clearLock(serviceCuidList);
	}
	
	/**
	 * 释放电路
	 * 释放电路的关系
	 * 释放接口数据
	 * 不写日志，需自行写日志
	 * 删除电路
	 * @param ac
	 * @param attempTraphCuidList
	 */
	private void releaseAttempTraph(ServiceActionContext ac, List<String> attempTraphCuidList) {
//		this.setLock(attempTraphCuidList);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempTraphCuidList);
		pm = Property.getIsModelOne(pm);
		List<Map<String, Object>> attempTraphList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		if(!attempTraphList.isEmpty()) {
			List<String> traphCuidList = new ArrayList<String>();
			List<String> outIdList = new ArrayList<String>();
			for(Map<String, Object> map : attempTraphList) {
				String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
				String outId = IbatisDAOHelper.getStringValue(map, "RELATED_OUT_TRAPH_CUID");
				if(StringUtils.isNotBlank(relatedTraphCuid)) {
					traphCuidList.add(relatedTraphCuid);
				}
				if(StringUtils.isNotBlank(outId)) {
					outIdList.add(outId);
				}
			}
			
			if(!traphCuidList.isEmpty()) {
				pm.clear();
				pm.put("cuidList", traphCuidList);
				pm.put("state", SheetConstants.SCHEDULE_STATE_END);
				logger.info("设置存量电路为可用");
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setTraphState", pm);
			}
			if(!outIdList.isEmpty()){
				//删除接口明细数据
				FacedeOrderMaintainBO.deleteIfaceDetail(ac, outIdList);
			}
			// 释放调度电路的关系
			this.releaseAttempTraphRelation(ac, attempTraphCuidList);
			
			pm.clear();
			pm.put("cuidList", attempTraphCuidList);
			List<Map<String,Object>> attempTraphExtList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphExtend", pm);
			logger.info("删除调度电路");
			if (attempTraphExtList.size()>0){
				this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraphExtend", pm);
			}
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraph", pm);
//			this.clearLock(attempTraphCuidList);
		}
	}
	
	/**
	 * 释放调度电路的通道关系
	 * @param ac
	 * @param attempTraphCuidList
	 */
	public void releaseAttempTraphRelation(ServiceActionContext ac, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		//查询通道
		List<Map<String, Object>> attempPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRoutePath", pm);
		
		List<String> attempTransPathCuidList = new ArrayList<String>();
		List<String> attempPtnPathCuidList = new ArrayList<String>();
		List<String> attempMsapPathCuidList = new ArrayList<String>();
		List<String> attempTextPathCuidList = new ArrayList<String>();
		List<String> attempSelfBuiltPathCuidList = new ArrayList<String>();
		List<String> attempTraphRouteCuidList = new ArrayList<String>();
		List<String> attempRoutePathCuidList = new ArrayList<String>();
		List<String> attempMultiPathCuidList = new ArrayList<String>();
		
		if(attempPathList != null && !attempPathList.isEmpty()) {
			for(Map<String, Object> map : attempPathList) {
				String attempRoutePathCuid = IbatisDAOHelper.getStringValue(map, "CUID");
				String attempTraphRouteCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_ROUTE_CUID");
				String attempMultiCuid = IbatisDAOHelper.getStringValue(map, "MULTI_CUID");
				String attempPathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
				String attempPathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");
				String attempMultiPathCuid = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_CUID");
				String attempMultiPathType = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_TYPE");
				
				if("TRANS_PATH".equals(attempPathType) || "TRANS_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempTransPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempTransPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_PTN_PATH".equals(attempPathType) || "ATTEMP_PTN_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempPtnPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempPtnPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_TEXT_PATH".equals(attempPathType) || "ATTEMP_TEXT_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempTextPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempTextPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_SELF_BUILT_PATH".equals(attempPathType) || "ATTEMP_SELF_BUILT_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempSelfBuiltPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempSelfBuiltPathCuidList.add(attempMultiPathCuid);
					}
				}else if("MSAP_PATH".equals(attempPathType) || "MSAP_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempMsapPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempMsapPathCuidList.add(attempMultiPathCuid);
					}
				}
				
				if(StringUtils.isNotBlank(attempRoutePathCuid)) attempRoutePathCuidList.add(attempRoutePathCuid);
				if(StringUtils.isNotBlank(attempTraphRouteCuid)) attempTraphRouteCuidList.add(attempTraphRouteCuid);
				if(StringUtils.isNotBlank(attempMultiCuid)) attempMultiPathCuidList.add(attempMultiCuid);
			}
		}
		
		if(attempTransPathCuidList != null && !attempTransPathCuidList.isEmpty()) {
			// 释放与SDH通道的关系
			this.releaseAttempTraphTransRelation(ac, attempTransPathCuidList, attempTraphCuidList);
		}
		
		if(attempPtnPathCuidList != null && !attempPtnPathCuidList.isEmpty()) {
			// 释放与PTN通道的关系
			this.releaseAttempTraphPtnRelation(ac, attempPtnPathCuidList, attempTraphCuidList);
		}
		
		if(attempMsapPathCuidList != null && !attempMsapPathCuidList.isEmpty()) {
			// 释放与PTN通道的关系
			this.releaseAttempTraphMsapRelation(ac, attempMsapPathCuidList, attempTraphCuidList);
		}
		
		//释放与电路扩展表信息
		Map<String,List<ServiceRel>> relMap = this.findServiceRels(attempTraphCuidList);
		for(String infoType:relMap.keySet()){
			ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
			if(extendBO!=null){
				List<ServiceRel> relList = relMap.get(infoType);
				List<String> infoCuidList = new ArrayList<String>();
				for(ServiceRel rel:relList){
					infoCuidList.add(rel.getInfoCuid());
				}
				extendBO.deleteExtendInfoByInfo(infoCuidList);
			}
		}
		//释放与电路相关联的专网业务信息
		this.releaseTraphGroupRelation(ac, attempTraphCuidList);
		
		if(attempTextPathCuidList != null && !attempTextPathCuidList.isEmpty()) {
			// 释放与文本段的关系
			this.releaseAttempTraphTextRelation(ac, attempTextPathCuidList);
		}
		
		if(attempSelfBuiltPathCuidList != null && !attempSelfBuiltPathCuidList.isEmpty()) {
			// 释放与自建段的关系
			this.releaseAttempTraphSelfBuiltRelation(ac, attempSelfBuiltPathCuidList);
		}
		
		// 释放与逻辑口的关系
		this.releaseAttempLogicPortRelation(ac, attempTraphCuidList);
		
		List<String> delRouteCuidList = new ArrayList<String>();
		delRouteCuidList.addAll(attempTraphRouteCuidList);
		delRouteCuidList.addAll(attempRoutePathCuidList);
		// 删除调度电路路由点(转接站)
		this.releaseAttempIndiPointsRelation(ac, delRouteCuidList);
		
		// 删除调度电路MULTI通道
		this.releaseAttempMultiPathRelation(ac, attempMultiPathCuidList);
		
		// 删除调度电路的路由与通道的关系
		this.releaseAttempRouteToPathRelation(ac, attempRoutePathCuidList);
		
		// 删除调度电路的路由
		this.releaseAttempRouteRelation(ac, attempTraphRouteCuidList);
		//删除关联的T_ATTEMP_DGN_SEG数据
		this.deleteAttempDgnSeg(ac, attempTraphCuidList);
		
		//删除黑龙江上端站信息
		if(SysProperty.getInstance().getValue("districtName").trim().equals("黑龙江")){
//			pm.clear();
//			pm.put("cuidList", attempTraphCuidList);
//			logger.info("删除T_ATTEMP_TRAPH_UP_PORT数据");
//			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempUpPort", pm);
		}
	}
	
	//释放与电路相关联的专网业务信息
	public void releaseTraphGroupRelation(ServiceActionContext ac,List<String> attempTraphCuidList) {
		Map mp = new HashMap();
		mp.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String,Object>> traphGroupList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".findAttempTraphGroupRelList",mp);
		if (traphGroupList != null && traphGroupList.size()>0){
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap + ".deleteAttempTraphGroupRelList",mp);
		}
	}

	/**
	 * 根据调度电路ID，释放电路与SDH通道的关系
	 * @param ac
	 * @param attempTransPathCuidList
	 * @param attempTraphCuidList
	 */
	public void releaseAttempTraphTransRelation(ServiceActionContext ac, List<String> attempTransPathCuidList, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		
		List<String> tempDelTransPathCuidList = new ArrayList<String>();
		List<String> tempTransPathCuidList = new ArrayList<String>();
		Set<String> tempTransPathCuidSet = new HashSet<String>();
		
		if(attempTransPathCuidList != null && !attempTransPathCuidList.isEmpty()) {
			List<Map<String, List<String>>> attempTransPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempTransPathCuidList, 1000);
			
			pm.clear();
			pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
			List<Map<String, Object>> transPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransPath", pm);
			if(transPathList != null && !transPathList.isEmpty()) {
				for(Map<String, Object> map : transPathList) {
					String relatedPathCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PATH_CUID");
					Integer createType = IbatisDAOHelper.getIntValue(map, "CREATE_TYPE");
					
					tempTransPathCuidSet.add(relatedPathCuid);
					
					if(createType == 2) {
						tempDelTransPathCuidList.add(relatedPathCuid);
					}
				}
			}
			
			tempTransPathCuidList.addAll(tempTransPathCuidSet);
			this.removeRepeatPath(tempTransPathCuidList, attempTraphCuidList, transPathList);
		}
		
		if(tempTransPathCuidList != null && !tempTransPathCuidList.isEmpty()) {
			List<String> delTransPathCuidList = new ArrayList<String>();
			for(String tempDelTransCuid : tempDelTransPathCuidList) {
				if(tempTransPathCuidList.contains(tempDelTransCuid)) {
					delTransPathCuidList.add(tempDelTransCuid);
				}
			}
			
			this.setTransPathState(tempTransPathCuidList, SheetConstants.SCHEDULE_STATE_END);
			
			List<Map<String, List<String>>> attempTransPathCuidGroup = IbatisDAOHelper.pareseGroupList(tempTransPathCuidList, 1000);
			
			List<Map<String, Object>> attempTransCtpList = this.findAttempTransCtpByAttempTrans(attempTransPathCuidGroup);
			List<Map<String, Object>> attempTransPtpList = this.findAttempTransPtpByAttempTrans(attempTransPathCuidGroup);
			
			List<String> attempTransCtpCuidList = new ArrayList<String>();
			Set<String> attempTransCtpCuidSet = new HashSet<String>();
			
			if(attempTransCtpList != null && !attempTransCtpList.isEmpty()) {
				for(Map<String, Object> map : attempTransCtpList) {
					String aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					
					if(StringUtils.isNotBlank(aCtpCuid)) attempTransCtpCuidSet.add(aCtpCuid);
					if(StringUtils.isNotBlank(zCtpCuid)) attempTransCtpCuidSet.add(zCtpCuid);
				}
				
				attempTransCtpCuidList.addAll(attempTransCtpCuidSet);
				if(attempTransCtpCuidList != null && !attempTransCtpCuidList.isEmpty()) {
					List<Map<String, List<String>>> tempTransCtpCuidGroup = IbatisDAOHelper.pareseGroupList(attempTransCtpCuidList, 1000);
					
					pm.clear();
					pm.put("ctpCuidGroup", tempTransCtpCuidGroup);
					List<Map<String, Object>> tempRepeatTransCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatTransCtp", pm);
					this.removeRepeatPoint(attempTransCtpCuidList, tempTransPathCuidList, tempRepeatTransCtpList);
				}
			}
			
			List<String> attempTransPtpCuidList = new ArrayList<String>();
			Set<String> attempTransPtpCuidSet = new HashSet<String>();
			
			if(attempTransPtpList != null && !attempTransPtpList.isEmpty()) {
				for(Map<String, Object> map : attempTransPtpList) {
					String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					
					if(StringUtils.isNotBlank(aPtpCuid)) attempTransPtpCuidSet.add(aPtpCuid);
					if(StringUtils.isNotBlank(zPtpCuid)) attempTransPtpCuidSet.add(zPtpCuid);
				}
				
				attempTransPtpCuidList.addAll(attempTransPtpCuidSet);
				if(attempTransPtpCuidList != null && !attempTransPtpCuidList.isEmpty()) {
					List<Map<String, List<String>>> tempTransPtpCuidGroup = IbatisDAOHelper.pareseGroupList(attempTransPtpCuidList, 1000);
					
					pm.clear();
					pm.put("ptpCuidGroup", tempTransPtpCuidGroup);
					List<Map<String, Object>> tempRepeatTransPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatTransPtp", pm);
					this.removeRepeatPoint(attempTransPtpCuidList, tempTransPathCuidList, tempRepeatTransPtpList);
				}
			}
			
			if(attempTransCtpCuidList != null && !attempTransCtpCuidList.isEmpty()) {
				// 设置时隙状态为空闲
				logger.info("设置时隙状态为空闲");
				this.setCtpState(attempTransCtpCuidList, 1);
			}
			if(attempTransPtpCuidList != null && !attempTransPtpCuidList.isEmpty()) {
				// 设置端口状态为空闲
				logger.info("设置端口状态为空闲");
				this.setPtpState(attempTransPtpCuidList, 1, 3);
			}
			
			pm.clear();
			pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
			logger.info("删除通道路由段");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempPathRouteSeg", pm);
			logger.info("删除调度SDH子通道");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTransSubPath", pm);
			logger.info("删除调度时隙与SDH通道的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempCtp2TransPath", pm);
			
			if(delTransPathCuidList != null && !delTransPathCuidList.isEmpty()) {
				List<Map<String, List<String>>> delAttempTransPathCuidGroup = IbatisDAOHelper.pareseGroupList(delTransPathCuidList, 1000);
				
				pm.clear();
				pm.put("cuidGroup", delAttempTransPathCuidGroup);
				logger.info("删除调度SDH通道");
				this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTransPath", pm);
			}
		}
		
		pm.clear();
		if(attempTraphCuidList!=null && !attempTraphCuidList.isEmpty()){
			pm.put("attempTraphCuidList", attempTraphCuidList);
			logger.info("删除调度电路与SDH通道的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTransPath2Traph", pm);
		}
	}
	
	/**
	 * 根据调度路由CUID，删除ptnPath中的伪线和隧道关联关系
	 * @param attempPtnPathCuidList
	 */
	public void delVirtualLineInPtnPath(List<String> attempPtnPathCuidList) {
		for(int i=0;i<attempPtnPathCuidList.size();i++){
			String attempPtnPathCuid=attempPtnPathCuidList.get(i);
			//Map<String, Object> attempPtnPathCuidMap = new HashMap<String, Object>();
			//attempPtnPathCuidMap.put("attempPtnPathCuid", attempPtnPathCuid);                                     
			String queryVirtualLine="SELECT CUID,RELATED_VIRTUAL_LINE_CUID FROM ATTEMP_PTN_PATH WHERE CUID='"+attempPtnPathCuid+"'";
			List<Map<String, Object>> virtualLineCuidList=this.IbatisResDAO.querySql(queryVirtualLine);
			String virtualLine=null;
			//得到这条调度电路关联的伪线
			if(virtualLineCuidList!=null&&virtualLineCuidList.size()>0){
				virtualLine=(String) virtualLineCuidList.get(0).get("RETLTED_VIRTULA_LINE_CUID");
			}
			if(virtualLine!=null){
				String queryPtnPathCuid="SELECT CUID,RELATED_VIRTUAL_LINE_CUID FROM ATTEMP_PTN_PATH WHERE RELATED_VIRTUAL_LINE_CUID='"+virtualLine+"'";
				List<Map<String, Object>> ptnPathCuidList=this.IbatisResDAO.querySql(queryPtnPathCuid);
				//如果在ATTEMP_PTN_PAT表中，这条伪线还有其他调度电路关联，则delVirtualLineFlag为true
				boolean delVirtualLineFlag=false;
				for(int j=0;j<ptnPathCuidList.size();j++){
					Map<String, Object> ptnPathCuidMap = new HashMap<String, Object>();
					ptnPathCuidMap=ptnPathCuidList.get(j);
					String ptnPathCuid=(String) ptnPathCuidMap.get("CUID");
					if(ptnPathCuid.equals("attempPtnPathCuid")){
						delVirtualLineFlag=true;
					}
				}
				//在PTN_PATH表中，这条伪线还有其他调度电路关联，则delVirtualLineFlag=true
				String queryPtnPathCuid1="SELECT COUNT(*) AS PTNPATHNO FROM PTN_PATH WHERE RELATED_VIRTUAL_LINE_CUID='"+virtualLine+"'";
				List<Map<String, Object>> ptnPathCuidList1=this.IbatisResDAO.querySql(queryPtnPathCuid1);
				int ptnPathNo=Integer.valueOf(ptnPathCuidList1.get(0).get("PTNPATHNO").toString());
				if(ptnPathNo>0){
					delVirtualLineFlag=true;
				}
				//在ATTEMP_PTN_PAT,PTN_PATH表中，这条伪线没有其他调度电路关联，则删除这条伪线
				if(delVirtualLineFlag==false){
					//删除伪线表中这条伪线
					String delVirtualLine="DELETE FROM PTN_VIRTUAL_LINE WHERE CUID='"+virtualLine+"'";
					this.IbatisResDAO.deleteSql(delVirtualLine);
					//删除伪线隧道关联关系 TUNNEL_TO_VIRTUAL_LINE
					String delTunnelToVirtualLine="DELETE FROM  TUNNEL_TO_VIRTUAL_LINE WHERE RELATED_VIRTUAL_LINE_CUID = '"+virtualLine+"'";
					this.IbatisResDAO.deleteSql(delTunnelToVirtualLine);
				}
			}		
		}
	}
	 /* 根据调度电路ID，释放电路与PTN通道的关系
	 * @param ac
	 * @param attempPtnPathCuidList
	 * @param attempTraphCuidList
	 */
	public void releaseAttempTraphPtnRelation(ServiceActionContext ac, List<String> attempPtnPathCuidList, List<String> attempTraphCuidList) {
		if(attempPtnPathCuidList != null && !attempPtnPathCuidList.isEmpty()) {
			List<Map<String, List<String>>> attempPtnPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempPtnPathCuidList, 1000);
			this.delVirtualLineInPtnPath(attempPtnPathCuidList);
			Map<String, Object> pm = new HashMap<String, Object>();
			List<Map<String, Object>> attempPtnCtpList = this.findAttempPtnCtpByAttempPtn(attempPtnPathCuidGroup);
			List<Map<String, Object>> attempPtnPtpList = this.findAttempPtnPtpByAttempPtn(attempPtnPathCuidGroup);
			
			List<String> attempPtnCtpCuidList = new ArrayList<String>();
			Set<String> attempPtnCtpCuidSet = new HashSet<String>();
			
			if(attempPtnCtpList != null && !attempPtnCtpList.isEmpty()) {
				for(Map<String, Object> map : attempPtnCtpList) {
					String aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					
					if(StringUtils.isNotBlank(aCtpCuid)) attempPtnCtpCuidSet.add(aCtpCuid);
					if(StringUtils.isNotBlank(zCtpCuid)) attempPtnCtpCuidSet.add(zCtpCuid);
				}
				
				attempPtnCtpCuidList.addAll(attempPtnCtpCuidSet);
				if(attempPtnCtpCuidList != null && !attempPtnCtpCuidList.isEmpty()) {
					List<Map<String, List<String>>> tempPtnCtpCuidGroup = IbatisDAOHelper.pareseGroupList(attempPtnCtpCuidList, 1000);
					
					pm.clear();
					pm.put("ctpCuidGroup", tempPtnCtpCuidGroup);
					List<Map<String, Object>> tempRepeatPtnCtpList1 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnCtp", pm);
					List<Map<String, Object>> tempRepeatPtnCtpList2 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnCtp2", pm);
					
					List<Map<String, Object>> tempRepeatPtnCtpList = new ArrayList<Map<String, Object>>();
					tempRepeatPtnCtpList.addAll(tempRepeatPtnCtpList1);
					tempRepeatPtnCtpList.addAll(tempRepeatPtnCtpList2);
					
					this.removeRepeatPoint(attempPtnCtpCuidList, attempPtnPathCuidList, tempRepeatPtnCtpList);
				}
			}
			
			List<String> attempPtnPtpCuidList = new ArrayList<String>();
			Set<String> attempPtnPtpCuidSet = new HashSet<String>();
			
			if(attempPtnPtpList != null && !attempPtnPtpList.isEmpty()) {
				for(Map<String, Object> map : attempPtnPtpList) {
					String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					
					if(StringUtils.isNotBlank(aPtpCuid)) attempPtnPtpCuidSet.add(aPtpCuid);
					if(StringUtils.isNotBlank(zPtpCuid)) attempPtnPtpCuidSet.add(zPtpCuid);
				}
				
				attempPtnPtpCuidList.addAll(attempPtnPtpCuidSet);
				if(attempPtnPtpCuidList != null && !attempPtnPtpCuidList.isEmpty()) {
					List<Map<String, List<String>>> tempPtnPtpCuidGroup = IbatisDAOHelper.pareseGroupList(attempPtnPtpCuidList, 1000);
					
					pm.clear();
					pm.put("ptpCuidGroup", tempPtnPtpCuidGroup);
					List<Map<String, Object>> tempRepeatPtnPtpList1 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnPtp", pm);
					List<Map<String, Object>> tempRepeatPtnPtpList2 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnPtp2", pm);
					
					List<Map<String, Object>> tempRepeatPtnPtpList = new ArrayList<Map<String, Object>>();
					tempRepeatPtnPtpList.addAll(tempRepeatPtnPtpList1);
					tempRepeatPtnPtpList.addAll(tempRepeatPtnPtpList2);
					
					this.removeRepeatPoint(attempPtnPtpCuidList, attempPtnPathCuidList, tempRepeatPtnPtpList);
				}
			}
			
			if(attempPtnCtpCuidList != null && !attempPtnCtpCuidList.isEmpty()) {
				// 设置时隙状态为空闲
				logger.info("设置时隙状态为空闲");
				this.setCtpState(attempPtnCtpCuidList, 1);
			}
			if(attempPtnPtpCuidList != null && !attempPtnPtpCuidList.isEmpty()) {
				// 设置端口状态为空闲
				logger.info("设置端口状态为空闲");
//				this.setPtpState(attempPtnPtpCuidList, 1,3);
				this.setPtpState(attempPtnPtpCuidList, 1,2);
			}
			
			pm.clear();
			pm.put("attempPtnPathCuidGroup", attempPtnPathCuidGroup);
			
			
			logger.info("删除调度PTN通道和ip的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempPtnPath2Ip", pm);
			logger.info("删除调度PTN通道和静态路由的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempPtnPath2StaticRoute", pm);
			logger.info("删除调度PTN通道");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempPtnPath", pm);
			
		}
	}
	
	 /* 根据调度电路ID，释放电路与PTN通道的关系
		 * @param ac
		 * @param attempMsapPathCuidList
		 * @param attempTraphCuidList
		 */
		public void releaseAttempTraphMsapRelation(ServiceActionContext ac, List<String> attempMsapPathCuidList, List<String> attempTraphCuidList) {
			if(attempMsapPathCuidList != null && !attempMsapPathCuidList.isEmpty()) {
				List<Map<String, List<String>>> attempMsapPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempMsapPathCuidList, 1000);
				Map<String, Object> pm = new HashMap<String, Object>();
				pm.put("attempMsapPathCuidGroup", attempMsapPathCuidGroup);
				List<Map<String, Object>> attempMsapPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempMsapPtp", pm);
				
				List<String> attempMsapPtpCuidList = new ArrayList<String>();
				Set<String> attempMsapPtpCuidSet = new HashSet<String>();
				
				if(attempMsapPtpList != null && !attempMsapPtpList.isEmpty()) {
					for(Map<String, Object> map : attempMsapPtpList) {
						String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
						String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
						
						if(StringUtils.isNotBlank(aPtpCuid)) attempMsapPtpCuidSet.add(aPtpCuid);
						if(StringUtils.isNotBlank(zPtpCuid)) attempMsapPtpCuidSet.add(zPtpCuid);
					}
					
					attempMsapPtpCuidList.addAll(attempMsapPtpCuidSet);
					if(attempMsapPtpCuidList != null && !attempMsapPtpCuidList.isEmpty()) {
						List<Map<String, List<String>>> tempMsapPtpCuidGroup = IbatisDAOHelper.pareseGroupList(attempMsapPtpCuidList, 1000);
						
						pm.clear();
						pm.put("ptpCuidGroup", tempMsapPtpCuidGroup);
						List<Map<String, Object>> tempRepeatMsapPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatMsapPtp", pm);
						
						this.removeRepeatPoint(attempMsapPtpCuidList, attempMsapPathCuidList, tempRepeatMsapPtpList);
					}
				}
				
				if(attempMsapPtpCuidList != null && !attempMsapPtpCuidList.isEmpty()) {
					// 设置端口状态为空闲
					logger.info("设置端口状态为空闲");
					this.setPtpState(attempMsapPtpCuidList, 1,2);
				}
				
				pm.clear();
				pm.put("attempMsapPathCuidGroup", attempMsapPathCuidGroup);
				
				logger.info("删除调度PTN通道");
				this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempMsapPath", pm);
				
			}
		}
	
	/**
	 * 根据调度电路ID，释放电路与文本段的关系
	 * @param ac
	 * @param attempTextPathCuidList
	 */
	public void releaseAttempTraphTextRelation(ServiceActionContext ac, List<String> attempTextPathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempTextPathCuidList);
		logger.info("删除调度文本段");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTextPath", pm);
	}
	
	/**
	 * 根据调度电路ID，释放电路与自建段的关系
	 * @param ac
	 * @param attempSelfBuiltPathCuidList
	 */
	public void releaseAttempTraphSelfBuiltRelation(ServiceActionContext ac, List<String> attempSelfBuiltPathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempSelfBuiltPathCuidList);
		logger.info("删除调度自建段");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempSelfBuiltPath", pm);
	}
	
	/**
	 * 根据调度电路ID，释放电路的转接点
	 * @param traphRouteCuidList
	 */
	public void releaseAttempIndiPointsRelation(ServiceActionContext ac, List<String> traphRouteCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphRouteCuidList", IbatisDAOHelper.skipEmptyForList(traphRouteCuidList));
		logger.info("删除调度电路的转接点");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttTraphToIndiPoints", pm);
	}
	/**
	 * 根据调度电路ID，释放电路的MSTP通道
	 * @param multiPathCuidList
	 */
	public void releaseAttempMultiPathRelation(ServiceActionContext ac, List<String> multiPathCuidList) {
		if(!multiPathCuidList.isEmpty()){
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put("cuidList", IbatisDAOHelper.skipEmptyForList(multiPathCuidList));
			logger.info("删除调度电路的MSTP通道");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempMultiPath", pm);
		}
	}
	
	/**
	 * 根据调度电路ID，释放通道与路由的关系
	 * @param routePathCuidList
	 */
	public void releaseAttempRouteToPathRelation(ServiceActionContext ac, List<String> routePathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", IbatisDAOHelper.skipEmptyForList(routePathCuidList));
		logger.info("删除调度通道与路由的关系");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttTraphRouteToPath", pm);
	}
	
	/**
	 * 根据调度电路ID，释放路由
	 * @param traphRouteCuidList
	 */
	public void releaseAttempRouteRelation(ServiceActionContext ac, List<String> traphRouteCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", IbatisDAOHelper.skipEmptyForList(traphRouteCuidList));
		logger.info("删除调度路由");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraphRoute", pm);
	}
	/**
	 * 根据调度电路ID,删除T_ATTEMP_DGN_SEG数据
	 * @param traphRouteCuidList
	 */
	public void deleteAttempDgnSeg(ServiceActionContext ac, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", IbatisDAOHelper.skipEmptyForList(attempTraphCuidList));
		logger.info("删除T_ATTEMP_DGN_SEG数据");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempDgnSeg", pm);
	}
	/**
	 * 根据调度电路ID，释放电路与逻辑口的关系
	 * @param attempTraphCuidList
	 */
	public void releaseAttempLogicPortRelation(ServiceActionContext ac, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", IbatisDAOHelper.skipEmptyForList(attempTraphCuidList));
		logger.info("删除调度电路与逻辑口的关系");
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setLogicPortAttempTraphEmpty", pm);
	}
	/**
	 * 设置通道的状态
	 * @param transPathCuidList
	 * @param state
	 */
	public void setTransPathState(List<String> transPathCuidList,int state){
		List<Map<String, List<String>>> transPathCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(transPathCuidList), 1000);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidGroup", transPathCuidGroup);
		pm.put("state", state);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setTransPathState", pm);
	}
	/**
	 * 设置端口的状态
	 * @param ptpCuidList
	 * @param state
	 */
	public void setPtpState(List<String> ptpCuidList,int state,Integer oldState){
		List<Map<String, List<String>>> ptpCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(ptpCuidList), 1000);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("ptpCuidGroup", ptpCuidGroup);
		pm.put("state", state);
		pm.put("oldState", oldState);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setPtpState", pm);
	}
	/**
	 * 设置时隙的状态
	 * @param ctpCuidList
	 * @param state
	 */
	public void setCtpState(List<String> ctpCuidList,int state){
		List<Map<String, List<String>>> ctpCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(ctpCuidList), 1000);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("ctpCuidGroup", ctpCuidGroup);
		pm.put("state", state);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setCtpState", pm);
	}
	/**
	 * 设置预增端口的状态
	 * @param ptpCuidList
	 * @param state
	 */
	public void setPtpNaState(List<String> ptpCuidList,int state, Integer oldState){
		List<Map<String, List<String>>> ptpCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(ptpCuidList), 1000);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("ptpCuidGroup", ptpCuidGroup);
		pm.put("state", state);
		pm.put("oldState", oldState);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setPtpNaState", pm);
	}
	/**
	 * 设置预增时隙的状态
	 * @param ctpCuidList
	 * @param state
	 */
	public void setCtpNaState(List<String> ctpCuidList,int state){
		List<Map<String, List<String>>> ctpCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(ctpCuidList), 1000);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("ctpCuidGroup", ctpCuidGroup);
		pm.put("state", state);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setCtpNaState", pm);
	}
	/**
	 * 根据业务明细创建资源
	 */
	public List<String> createServiceDetailByOrderInfo(ServiceActionContext ac, Integer attempType, InfoDesignType res, 
			Map<String, TaskInst> taskMap, TaskInst task) {
		Map<TraphName, List<AttempTraphService>> devInfoMap = new HashMap<TraphName, List<AttempTraphService>>();
		List<AttempTraphService> attempTraphList = new ArrayList<AttempTraphService>();
		List<String> relatedDetailCuidList = new ArrayList<String>();
		
		// 按设计类型转换对象，新增电路按局向分组，获取调度电路ID校验是否生成调度电路
		for(IService service : res.getResList()) {
			if(SheetConstants.DESIGN_TYPE_TRAPH.equals(service.getDesignType())) {
				AttempTraphService attempTraph = (AttempTraphService) service;
				attempTraphList.add(attempTraph);
				
				String relatedDetailCuid = IbatisDAOHelper.getStringValue(service.getDataMap(), "RELATED_DETAIL_CUID");
				if(StringUtils.isNotBlank(relatedDetailCuid)) {
					relatedDetailCuidList.add(relatedDetailCuid);
				}
				
				if(attempType == SheetConstants.SCHEDULE_TYPE_NEW) {
					if(StringUtils.isNotBlank(attempTraph.getAPointCuid())&&StringUtils.isNotBlank(attempTraph.getZPointCuid())){
						Map<String,String> data=this.getAZEndStaction(attempTraph.getAPointCuid(), attempTraph.getZPointCuid());
						String aSite=attempTraph.getAPointCuid();
						String zSite=attempTraph.getZPointCuid();
						if(data!=null&&data.containsKey("aSite")&&StringUtils.isNotEmpty(data.get("aSite"))){
							aSite=data.get("aSite");
						}
						if(data!=null&&data.containsKey("zSite")&&StringUtils.isNotEmpty(data.get("zSite"))){
							zSite=data.get("zSite");
						}
						TraphName name = new TraphName(aSite, zSite, attempTraph.getRate());
						List<AttempTraphService> list = devInfoMap.get(name);
						if(list == null) {
							list = new ArrayList<AttempTraphService>();
							devInfoMap.put(name, list);
						}
						list.add(attempTraph);
					}
				}
			}
		}
		
		// 校验是否生成调度电路
		if(relatedDetailCuidList != null && !relatedDetailCuidList.isEmpty()) {
			List<Map<String, Object>> existsAttempTraphList = this.findAttempTraph(relatedDetailCuidList);
			if(existsAttempTraphList != null && !existsAttempTraphList.isEmpty()) throw new RuntimeException("业务明细已经生成调度电路！");
		}
		
		List<String> resIds = new ArrayList<String>();
		if (attempType == SheetConstants.SCHEDULE_TYPE_NEW) {
			if(!devInfoMap.isEmpty()){
			    resIds.addAll(this.createAddAttempTraph(ac, devInfoMap, taskMap, task));
			}
		}else if(attempType == SheetConstants.SCHEDULE_TYPE_ADJUCT){
			resIds.addAll(this.createAdjustAttempTraph(ac, attempTraphList, taskMap, task));
		}else if(attempType == SheetConstants.SCHEDULE_TYPE_CLOSE){
			resIds.addAll(this.createCloseAttempTraph(ac, attempTraphList, taskMap, task));
		}
		//广西本地延伸段路由设计
		String serviceCuid = resIds.get(0);
		Map pm = new HashMap();
		pm.put("serviceCuid", serviceCuid);
		List<Map<String,Object>> endStationTypeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getEndStationType", pm);
		Map<String,Object> endStationTypeMap = endStationTypeList.get(0);
		String endStationType = IbatisDAOHelper.getStringValue(endStationTypeMap, "END_STATION_TYPE_A");
		String fromCode = IbatisDAOHelper.getStringValue(endStationTypeMap, "FROMCODE");
		String extOrderCuid = IbatisDAOHelper.getStringValue(endStationTypeMap, "CUID");
		if(Property.IRMS.equalsIgnoreCase(fromCode) && endStationType.equals("DISTRICT")
				&& SysProperty.getInstance().getValue("districtName").equals("广西") && StringUtils.isNotEmpty(extOrderCuid)){
			createTraphRouteDesign(ac, devInfoMap, taskMap, task);
		}
		return resIds;
	}
	public void createTraphRouteDesign(ServiceActionContext ac, Map<TraphName, List<AttempTraphService>> devInfoMap, 
			 Map<String, TaskInst> taskMap, TaskInst defaultTask){
		for(TraphName traphName : devInfoMap.keySet()) {
			List<AttempTraphService> devInfoList = devInfoMap.get(traphName);
			for(int i=0; i<devInfoList.size(); i++) {
				AttempTraphService attempTraph = devInfoList.get(i);
				Map<String, Object> infoMap = new HashMap<String, Object>();
				infoMap.putAll(attempTraph.getDataMap());
				String traphDesignUser = IbatisDAOHelper.getStringValue(infoMap, "TRAPH_DESIGN_USER");
				TaskInst task = defaultTask;
				if(defaultTask == null) {
					if(taskMap == null) throw new RuntimeException("当前电路无法和任务建立关联！");
					task = taskMap.get(traphDesignUser);
				}
				if(task == null) {
					throw new RuntimeException("当前电路无法和任务建立关联！");
				} else if(!ProcessBO.TASK_RUN.equals(task.getState())) {
					throw new RuntimeException("当前任务已被其它人处理完毕！");
				}
				String taskId = task.getCuid();
				
				Map zjMapInfo=new HashMap();
				List<PathPoint> points=new ArrayList<PathPoint>();
				Map<String,Object> pm = new HashMap<String,Object>();
				
				String extOrderDetailCuid = IbatisDAOHelper.getStringValue(infoMap, "CUID");
				pm.put("detailCuid", extOrderDetailCuid);
				List<Map<String,String>> zjList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPointsByDetailCuid", pm);
				Map<String,String> zjMap = zjList.get(0);
				List<String> serviceCuidList = new ArrayList<String>();
				serviceCuidList.add(IbatisDAOHelper.getStringValue(zjMap, "RELATED_DETAIL_CUID"));
				
				PathPoint pathPoint1=new PathPoint();
				PathPoint pathPoint2=new PathPoint();
				PathPoint pathPoint3=new PathPoint();
				PathPoint pathPoint4=new PathPoint();
				
				pathPoint1.setText(zjMap.get("ORIG_NE_A"));    //A端点
				pathPoint1.setType(zjMap.get("TYPE"));
				pathPoint1.setValue(zjMap.get("ORIG_NE_A_CUID"));
				points.add(pathPoint1);
				if(!zjMapInfo.containsKey(zjMap.get("ORIG_NE_A"))){
					zjMapInfo.put(zjMap.get("ORIG_NE_A"), zjMap.get("ORIG_NE_A_CUID"));
				}
				pathPoint2.setText(zjMap.get("DEST_NE_A"));        //转接点(站点)
				pathPoint2.setType(zjMap.get("ZJ_TYPE"));
				pathPoint2.setValue(zjMap.get("DEST_NE_A_CUID"));  
				points.add(pathPoint2);
				if(!zjMapInfo.containsKey(zjMap.get("DEST_NE_A"))){
					zjMapInfo.put(zjMap.get("DEST_NE_A"), zjMap.get("DEST_NE_A_CUID"));
				}
				pathPoint3.setText(zjMap.get("ORIG_NE_Z"));         //转接点(站点)
				pathPoint3.setType(zjMap.get("ZJ_TYPE"));
				pathPoint3.setValue(zjMap.get("ORIG_NE_Z_CUID"));
				points.add(pathPoint3);
				if(!zjMapInfo.containsKey(zjMap.get("ORIG_NE_Z"))){
					zjMapInfo.put(zjMap.get("ORIG_NE_Z"), zjMap.get("ORIG_NE_Z_CUID"));
				}
				pathPoint4.setText(zjMap.get("DEST_NE_Z"));    //Z端点
				pathPoint4.setType(zjMap.get("TYPE"));
				pathPoint4.setValue(zjMap.get("DEST_NE_Z_CUID"));
				points.add(pathPoint4);
				if(!zjMapInfo.containsKey(zjMap.get("DEST_NE_Z"))){
					zjMapInfo.put(zjMap.get("DEST_NE_Z"), zjMap.get("DEST_NE_Z_CUID"));
				}
				logger.info("得到路由点："+points.size());
				//生成电路段
				List<Map<String,Object>> routeSegInfoList = getTraphRouteDsn(extOrderDetailCuid);
				AttempTraphRDataHandler bo = (AttempTraphRDataHandler)SpringContextUtil.getBean("AttempTraphRDataHandler");
				PathNode pathNodeOld=bo.getPathDesign(taskId,serviceCuidList);
				IPathDesignBO pathDesignBO = (IPathDesignBO) SpringContextUtil.getBean("TraphDesignBO");
				pathDesignBO.savePathDesignTree(taskId, pathNodeOld, points);
				PathNode pathNodeNew=bo.getPathDesign(taskId,serviceCuidList);
				for(int k=0;k<pathNodeNew.getChildren().size();k++){
					PathNode childPathNode=pathNodeNew.getChildren().get(k);
					Map<String,Object> routeInfoMap=new HashMap<String,Object>();
					String routeAName = "";
					String routeZName = "";
					for(int p=0;p<routeSegInfoList.size();p++){
						Map<String,Object> routeSegInfoMap=routeSegInfoList.get(p);
						routeAName=IbatisDAOHelper.getStringValue(routeSegInfoMap,"ROUTE_POINT_A_NAME");
						routeZName=IbatisDAOHelper.getStringValue(routeSegInfoMap,"ROUTE_POINT_Z_NAME");
						if((routeAName+"-"+routeZName).equals(childPathNode.getName())||(routeZName+"-"+routeAName).equals(childPathNode.getName())){
							routeInfoMap=routeSegInfoList.get(p);
							break;
						}
					}
					logger.info("保存路由段"+childPathNode.getName()+"设计"+"，路由详细信息为:"+routeInfoMap.toString());
					String[] routePoints = new String[2];
					if(childPathNode.getName().indexOf(routeAName)>=0 && childPathNode.getName().indexOf(routeZName)>=0){
						routePoints[0] = routeAName;
						routePoints[1] = routeZName;
					}
					//保存设计电路	
					Map segMap =new HashMap();
					//构造segData
					List<Map> segData=new ArrayList<Map>();
					Map serviceMap=new HashMap();
					serviceMap.put("segCuid", childPathNode.getCuid());
					serviceMap.put("serviceCuid", serviceCuidList.get(0));
					segData.add(serviceMap);
					segMap.put("A_POINT", zjMapInfo.get(routePoints[0]));
					segMap.put("Z_POINT", zjMapInfo.get(routePoints[1]));
					segMap.put("segData", segData);
					bo.saveDgnSegDetail(routeInfoMap,segMap);
				}
				bo.submitPathDesign(taskId,serviceCuidList);
			}
		}
	}
	
	public List<Map<String,Object>> getTraphRouteDsn(String extOrderDetailCuid){
		List<Map<String,Object>> routeSegInfoList = new ArrayList<Map<String,Object>>();
		Map<String,Object> pm = new HashMap<String,Object>();
		pm.put("detailCuid", extOrderDetailCuid);
		List<Map<String,Object>> routSeglist = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTrahRouteByDetailCuid", pm);
		Map<String,Object> routeMap1 = new HashMap<String, Object>();
		Map<String,Object> map = routSeglist.get(0);
		String codeA = IbatisDAOHelper.getStringValue(map, "A_AP_MODE");
		if(codeA.equals("2")){
			codeA = "port";
			routeMap1.put("CODE", codeA);
			routeMap1.put("ROUTE_POINT_A_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_NE_A"));
			routeMap1.put("ROUTE_POINT_Z_NAME",IbatisDAOHelper.getStringValue(map, "DEST_NE_A"));
			routeMap1.put("RELATED_A_CTP",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A_CUID"));
			routeMap1.put("RELATED_A_CTP_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A"));
			routeMap1.put("RELATED_A_PTP",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A_CUID"));
			routeMap1.put("RELATED_A_PTP_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A"));
			routeMap1.put("RELATED_Z_CTP",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A_CUID"));
			routeMap1.put("RELATED_Z_CTP_NAME",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A"));
			routeMap1.put("RELATED_Z_PTP",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A_CUID"));
			routeMap1.put("RELATED_Z_PTP_NAME",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A"));
			routeMap1.put("EXT_IDS",IbatisDAOHelper.getStringValue(map, "EXT_IDS"));
		}else if(codeA.equals("1")){
			codeA = "ptn";
			routeMap1.put("CODE", codeA);
			routeMap1.put("ROUTE_POINT_A_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_NE_A"));
			routeMap1.put("ROUTE_POINT_Z_NAME",IbatisDAOHelper.getStringValue(map, "DEST_NE_A"));
			routeMap1.put("RELATED_A_CTP_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A_CUID"));
			routeMap1.put("RELATED_A_CTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A"));
			routeMap1.put("RELATED_A_PTP_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A_CUID"));
			routeMap1.put("RELATED_A_PTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A"));
			routeMap1.put("RELATED_Z_CTP_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A_CUID"));
			routeMap1.put("RELATED_Z_CTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A"));
			routeMap1.put("RELATED_Z_PTP_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A_CUID"));
			routeMap1.put("RELATED_Z_PTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A"));
			
			routeMap1.put("RELATED_A_CTP2_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A2_CUID"));
			routeMap1.put("RELATED_A_CTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_A2"));
			routeMap1.put("RELATED_A_PTP2_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A2_CUID"));
			routeMap1.put("RELATED_A_PTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_A2"));
			routeMap1.put("RELATED_Z_CTP2_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A2_CUID"));
			routeMap1.put("RELATED_Z_CTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_A2"));
			routeMap1.put("RELATED_Z_PTP2_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A2_CUID"));
			routeMap1.put("RELATED_Z_PTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_A2"));
			
			routeMap1.put("CIR_BAND",IbatisDAOHelper.getStringValue(map, "CIR"));
			routeMap1.put("PIR_BAND",IbatisDAOHelper.getStringValue(map, "PIR"));
			routeMap1.put("QOS_BAND",IbatisDAOHelper.getStringValue(map, "QOS"));
			routeMap1.put("VLANID",IbatisDAOHelper.getStringValue(map, "VLANID"));
			routeMap1.put("SERVICE_PRIORITY","");
		}
		
		Map<String,Object> routeMap2 = new HashMap<String, Object>();
		String destNeA = IbatisDAOHelper.getStringValue(map, "DEST_NE_A");
		String destNeZ = IbatisDAOHelper.getStringValue(map, "ORIG_NE_Z");
		String text = destNeA +"《》"+ destNeZ;
		routeMap2.put("CODE", "text");
		routeMap2.put("ROUTE_POINT_A_NAME",destNeA);
		routeMap2.put("ROUTE_POINT_Z_NAME",destNeZ);
		routeMap2.put("TEXT",text);
		
		Map<String,Object> routeMap3 = new HashMap<String, Object>();
		String codeZ = IbatisDAOHelper.getStringValue(map, "Z_AP_MODE");
		if(codeZ.equals("2")){
			codeZ = "port";
			routeMap3.put("CODE", codeZ);
			routeMap3.put("ROUTE_POINT_A_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_NE_Z"));
			routeMap3.put("ROUTE_POINT_Z_NAME",IbatisDAOHelper.getStringValue(map, "DEST_NE_Z"));
			routeMap3.put("RELATED_A_CTP",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z_CUID"));
			routeMap3.put("RELATED_A_CTP_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z"));
			routeMap3.put("RELATED_A_PTP",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z_CUID"));
			routeMap3.put("RELATED_A_PTP_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z"));
			routeMap3.put("RELATED_Z_CTP",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z_CUID"));
			routeMap3.put("RELATED_Z_CTP_NAME",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z"));
			routeMap3.put("RELATED_Z_PTP",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z_CUID"));
			routeMap3.put("RELATED_Z_PTP_NAME",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z"));
			routeMap3.put("EXT_IDS",IbatisDAOHelper.getStringValue(map, "EXT_IDS"));
		}else if(codeZ.equals("1")){
			codeZ = "ptn";
			routeMap3.put("CODE", codeZ);
			routeMap3.put("ROUTE_POINT_A_NAME",IbatisDAOHelper.getStringValue(map, "ORIG_NE_Z"));
			routeMap3.put("ROUTE_POINT_Z_NAME",IbatisDAOHelper.getStringValue(map, "DEST_NE_Z"));
			routeMap3.put("RELATED_A_CTP_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z_CUID"));
			routeMap3.put("RELATED_A_CTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z"));
			routeMap3.put("RELATED_A_PTP_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z_CUID"));
			routeMap3.put("RELATED_A_PTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z"));
			routeMap3.put("RELATED_Z_CTP_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z_CUID"));
			routeMap3.put("RELATED_Z_CTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z"));
			routeMap3.put("RELATED_Z_PTP_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z_CUID"));
			routeMap3.put("RELATED_Z_PTP_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z"));
			
			routeMap3.put("RELATED_A_CTP2_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z2_CUID"));
			routeMap3.put("RELATED_A_CTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_CTP_Z2"));
			routeMap3.put("RELATED_A_PTP2_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z2_CUID"));
			routeMap3.put("RELATED_A_PTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "ORIG_PTP_Z2"));
			routeMap3.put("RELATED_Z_CTP2_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z2_CUID"));
			routeMap3.put("RELATED_Z_CTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_CTP_Z2"));
			routeMap3.put("RELATED_Z_PTP2_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z2_CUID"));
			routeMap3.put("RELATED_Z_PTP2_NAME_PTN",IbatisDAOHelper.getStringValue(map, "DEST_PTP_Z2"));
			
			routeMap3.put("CIR_BAND",IbatisDAOHelper.getStringValue(map, "CIR"));
			routeMap3.put("PIR_BAND",IbatisDAOHelper.getStringValue(map, "PIR"));
			routeMap3.put("QOS_BAND",IbatisDAOHelper.getStringValue(map, "QOS"));
			routeMap3.put("VLANID",IbatisDAOHelper.getStringValue(map, "VLANID"));
			routeMap3.put("SERVICE_PRIORITY","");
		}
		routeSegInfoList.add(routeMap1);
		routeSegInfoList.add(routeMap2);
		routeSegInfoList.add(routeMap3);
		return routeSegInfoList;
	}
	
	/**
	 * 通过批量调度电路ID，获取调度电路
	 * @param serviceCuidList
	 * @return
	 */
	public List<IService> findService(List<String> serviceCuidList) {
		List<IService> serviceList = new ArrayList<IService>();
		
		if(serviceCuidList != null && !serviceCuidList.isEmpty()) {
			List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
			/*Map<String, ITraphExtend> serviceInfoMap = new HashMap<String, ITraphExtend>();
			Map<String,List<ServiceRel>> relMap = this.findServiceRels(serviceCuidList);
			for(String infoType:relMap.keySet()){
				ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
				if(extendBO!=null){
					List<ServiceRel> relList = relMap.get(infoType);
					List<String> infoCuidList = new ArrayList<String>();
					for(ServiceRel rel:relList){
						infoCuidList.add(rel.getInfoCuid());
					}
					Map<String,ITraphExtend>  extendInfoList = extendBO.getExtendInfoByInfo(infoCuidList);
					serviceInfoMap.putAll(extendInfoList);
				}
			}*/
			
			if(attempTraphList != null && !attempTraphList.isEmpty()) {
				for(Map<String, Object> map : attempTraphList) {
					String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
					String labelCn = IbatisDAOHelper.getStringValue(map, "LABEL_CN");
					Integer no = IbatisDAOHelper.getIntValue(map, "NO");
					String extIds = IbatisDAOHelper.getStringValue(map, "EXT_IDS");
					String extTypes = IbatisDAOHelper.getStringValue(map, "EXT_TYPE");
					String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
					String relatedUserCuid = IbatisDAOHelper.getStringValue(map, "RELATED_USER_CUID");
					String aPointCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID");
					String zPointCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID");
					String aPointType = IbatisDAOHelper.getStringValue(map, "END_STATION_TYPE_A");
					String zPointType = IbatisDAOHelper.getStringValue(map, "END_STATION_TYPE_Z");
					Integer rate = IbatisDAOHelper.getIntValue(map, "TRAPH_RATE");
					AttempTraphService service = new AttempTraphService(aPointCuid, zPointCuid, rate);
					service.setAPointType(aPointType);
					service.setZPointType(zPointType);
					service.setCuid(cuid);
					service.setLabelCn(labelCn);
					service.setNo(no);
					service.setExtIds(extIds);
					service.setExtTypes(extTypes);
					service.setRelatedTraphCuid(relatedTraphCuid);
					service.setRelatedUserCuid(relatedUserCuid);
					service.setDataMap(map);
//					service.setExtend(serviceInfoMap.get(cuid));
					serviceList.add(service);
				}
			}
		}
		
		return serviceList;
	}
	
	/**
	 * 通过批量调度电路ID，获取调度电路数据
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraph(List<String> cuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", IbatisDAOHelper.skipEmptyForList(cuidList));
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		
		return list;
	}
	/**
	 * 根据申请单ID，获取所有调度电路数据
	 * @param cuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraph(String orderId){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("orderId", orderId);
		pm = Property.getIsModelOne(pm);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		return list;
	}
	/**
	 * 通过调度电路ID，获取调度电路数据，会对枚举对象转换
	 * @param cuid
	 * @return
	 */
	public Map<String, Object> getAttempTraph(String cuid) {
		Map<String, Object> traphMap = new HashMap<String, Object>();
		traphMap.put("CUID", cuid);
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", cuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryIrmsTitle", pm);
		//add by luoshuyun 判断是否是综资过来的单子
		boolean isIrmsSheet = false;
		if(!list.isEmpty()){
			String irmstitle = IbatisDAOHelper.getStringValue(list.get(0), "IRMSTITLE");
			if(irmstitle!=null && !"".equals(irmstitle)){
				isIrmsSheet = false;
			}
		}
		Map<String, Object> map = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".queryAttempTraph", pm);
		if(map != null && !map.isEmpty()) {
			for(String key : map.keySet()) {
				if (key.startsWith("N_")) {
					String attr = StringUtils.substring(key, 2);
					String text = IbatisDAOHelper.getStringValue(map, key);
					String value = IbatisDAOHelper.getStringValue(map, attr);
					Map<String, Object> relMap = new HashMap<String, Object>();
					relMap.put("value", value);
					relMap.put("text", text);
					
					traphMap.put(attr, relMap);
				} else if (!traphMap.containsKey(key)) {
					traphMap.put(key, IbatisDAOHelper.getStringValue(map, key));
					if(isIrmsSheet){//流程过来的单据默认承载方式为空，需要给个默认值，默认值为1
						String extValue = IbatisDAOHelper.getStringValue(map, "EXT_TYPE");
						if(key.equals("EXT_TYPE") && extValue!=null && !"".equals(extValue)){
							extValue = "1";
							traphMap.put("EXT_TYPE", extValue);
						}
					}
				}
				
			}
		}
		
		return traphMap;
	}
	
	/**
	 * 通过任务ID，获取调度电路数据
	 * @param taskId
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraphByTaskId(String taskId) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("taskId", taskId);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		
		return list;
	}
	
	/**
	 * 通过订单ID，获取调度电路数据
	 * @param taskId
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraphByOrderId(String orderId) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("orderId", orderId);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		
		return list;
	}
	
	/**
	 * 通过工单ID，获取调度电路数据
	 * @param taskId
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraphBySheetId(String sheetId) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("sheetId", sheetId);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		
		return list;
	}
	
	/**
	 * 通过任务与资源的关系ID，获取调度电路数据
	 * @param taskServiceCuidList
	 * @return
	 */
	public List<Map<String, Object>> findServiceByTaskService(List<String> taskServiceCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("taskServiceCuidList", IbatisDAOHelper.skipEmptyForList(taskServiceCuidList));
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph", pm);
		
		return list;
	}
	
	
	/**
	 * 根据任务ID和电路ID查询电路日志
	 * @param taskId
	 * @param attempTraphCuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTraphLogByParam(String taskId, List<String> attempTraphCuidList) {
		if(StringUtils.isBlank(taskId)) throw new RuntimeException("任务ID不允许为空！");
		/*if(attempTraphCuidList == null || attempTraphCuidList.isEmpty()) {
			throw new RuntimeException("电路ID不允许为空！");
		}*/
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("taskId", taskId);
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphLog", pm);
		
		return list;
	}

	/**
	 * 根据电路的CUID查询调度的PTN段
	 * @param taskServiceCuidList
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> findAttempPtnbyTcuid(List<String> taskServiceCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", IbatisDAOHelper.skipEmptyForList(taskServiceCuidList));
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findAttempPtnbyTcuid", pm);
		return list;
	}
	
	/**
	 * 校验PTN电路A/Z端口组合的端口是否已经使用了
	 * @param attempTraphCuidList	
	 * @return
	 */
	public Map<String, Object> validateRepeatPtnAzGroupPort(List<String> attempTraphCuidList)
	{
		int success = 1;//成功
		String msg = "校验通过";//无错误消息
		int errorCount = 0;//端口组合的个数
		String errorMsg = "";//详细的错误信息，只有当repeatPtpCount>0时才有
		TraphDesignBO traphDesignBO = (TraphDesignBO) SpringContextUtil.getBean("TraphDesignBO");
		List<Map<String, Object>> attempPtns = findAttempPtnbyTcuid(attempTraphCuidList);
		StringBuilder checkPtpMsg = new StringBuilder();
		int aptnSize = attempPtns.size();
		for(int j=0; j<aptnSize; j++) 
		{
			Map<String, Object> attempPtn = attempPtns.get(j);
			String attempCuid = IbatisDAOHelper.getStringValue(attempPtn, AttempPtnPath.AttrName.relatedRouteCuid);
			logger.info("第"+(j+1)+"条电路"+attempCuid+"检查开始");
			String aPointCuid = IbatisDAOHelper.getStringValue(attempPtn, AttempPtnPath.AttrName.relatedAPtpCuid);
			String zPointCuid = IbatisDAOHelper.getStringValue(attempPtn, AttempPtnPath.AttrName.relatedZPtpCuid);
			String aPointName = IbatisDAOHelper.getStringValue(attempPtn,"A_PTP_NAME");
			String zPointName = IbatisDAOHelper.getStringValue(attempPtn,"Z_PTP_NAME");
			BigDecimal pathType = (BigDecimal)attempPtn.get("PATH_TYPE");
			String vlanId = IbatisDAOHelper.getStringValue(attempPtn,"VLANID");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aPointCuid", aPointCuid);
			map.put("zPointCuid", zPointCuid);
			map.put("pathType", pathType);
			map.put("vlanId", vlanId);
			List<Map<String, Object>> list = traphDesignBO.getPtnTraphByPtp(map);
			
			
			//获取当前调度电路以及资源电路信息
			String selfCuid = "";
			AttempTraphRoutePathBO attempTraphBo = (AttempTraphRoutePathBO) SpringContextUtil.getBean("AttempTraphRoutePathBO");
			Map<String, Object> attempTraph = attempTraphBo.getServiceInfo(attempCuid);
			String attempTraphCuid = IbatisDAOHelper.getStringValue(attempTraph, AttempTraph.AttrName.cuid);
			if(StringUtils.isNotEmpty(attempTraphCuid)) 
			{
				selfCuid = selfCuid.concat(attempTraphCuid);
			}
			String traphCuid = IbatisDAOHelper.getStringValue(attempTraph, AttempTraph.AttrName.relatedTraphCuid);
			if(StringUtils.isNotEmpty(traphCuid)) 
			{
				selfCuid = selfCuid.concat(","+traphCuid);
			}
			
			for(int i=0; i<list.size(); i++) 
			{
				Map<String, Object> ptnTraph = list.get(i);
				String ptnTcuid = IbatisDAOHelper.getStringValue(ptnTraph, Traph.AttrName.cuid);
				String ptnTLabelCn = IbatisDAOHelper.getStringValue(ptnTraph, Traph.AttrName.labelCn);
				if(selfCuid.indexOf(ptnTcuid)>=0) //排除自身的电路
				{
					logger.info("PTN端口查找到自身电路|"+ptnTLabelCn);
				} else 
				{
					errorCount++;
					String tempMsg = "端口>"+aPointName+"←→"+zPointName+"被电路【"+ptnTLabelCn+"】使用\r\n";
					logger.info(tempMsg);
					if(-1==checkPtpMsg.indexOf(tempMsg)) //如果不存在此消息
					{
						checkPtpMsg.append(tempMsg);
					}
					continue;
				}
			}
			
			if(checkPtpMsg.length()>0) 
			{
				errorMsg = checkPtpMsg.toString();
			} else 
			{
				logger.info("PTN电路可以使用AZ端口组合["+aPointName+","+zPointName+"]");
			}
			logger.info("第"+(j+1)+"条电路"+attempCuid+"检查结束");
		}
		logger.info("A-Z端口组合errorMsg等于"+errorMsg);
		logger.info("PTN段A/Z端口正反向不重复判断结束");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(errorCount>0) //如果存在错误信息
		{
			success = 0;
			msg = "校验未通过，A→Z端口已经被多条电路使用";
		}
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		resultMap.put("errorCount", errorCount);
		resultMap.put("errorMsg", errorMsg);
		return resultMap;
	}
	
	/**
	 * 校验PTN电路2M端口复用
	 * @param attempTraphCuidList	
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> validateRepeatPtn2MPort(List<String> attempTraphCuidList)
	{
		int success = 1;//成功
		String msg = "校验通过";//无错误消息
		int repeatPtpCount = 0;//端口复用的个数
		StringBuilder errorMsg = new StringBuilder();//详细的错误信息，只有当repeatPtpCount>0时才有
		List<Map> repeatList = new ArrayList<Map>();
		logger.info("PTN电路2M端口复用判断开始");
		Map<String, Object> queryParam = new HashMap<String, Object>();
		List<Map<String, Object>> used2MPortPtnTraphs=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> used2MPortPtnTraphsTemp=new ArrayList<Map<String, Object>>();
		List<String> attempTraphCuidListTemp=new ArrayList<String>();
		
		for(int i=0;i<attempTraphCuidList.size();i++){
			
			if((i!=0&&i%100==0)||i==attempTraphCuidList.size()-1){
				attempTraphCuidListTemp.add(attempTraphCuidList.get(i));
				queryParam.put("attempTraphCuidList", attempTraphCuidListTemp);
				used2MPortPtnTraphsTemp= this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findUsed2MPortPtnTraphs", queryParam);
				if(used2MPortPtnTraphsTemp!=null&&used2MPortPtnTraphsTemp.size()>0){
					used2MPortPtnTraphs.addAll(used2MPortPtnTraphsTemp);
				}
				used2MPortPtnTraphsTemp=new ArrayList<Map<String, Object>>();
				attempTraphCuidListTemp=new ArrayList<String>();
			}else{
				attempTraphCuidListTemp.add(attempTraphCuidList.get(i));
			}
		}
//		SQL实现逻辑1.根据传入的调度电路名称ATTEMP_TRAPH获取到A、Z端口
//				 2.根据A、Z端口，且端口的速率为2M，查询ATTEMP_PTN_PATH表以及PTN_PATH
//				 3.判断PTN_PATH表中的RELATED_ROUTE_CUID等于ATTEMP_TRAPH的RELATED_TRAPH_CUID,是为了排除调整以及停闭的PTN电路
		logger.info("查找已经使用了2M端口的PTN电路开始");
		if(null!=used2MPortPtnTraphs) //判断使用了2M端口的PTN电路是否为空
		{
			int count = used2MPortPtnTraphs.size();
			logger.info("统计2M端口复用情况开始");
			Map<String, List<String>> usedPtn2MPort = new HashMap<String, List<String>>();//已经使用了2M端口的PTN电路,key为端口名称。value为电路名称
			for(int i=0; i<count; i++) 
			{
				Map<String, Object> value = used2MPortPtnTraphs.get(i);
				String ptpName = IbatisDAOHelper.getStringValue(value, "PTP_NAME");
				String traphName = IbatisDAOHelper.getStringValue(value, "TRAPH_NAME");
				if(usedPtn2MPort.containsKey(ptpName)) //根据端口名称分组
				{
					List<String> ls = usedPtn2MPort.get(ptpName);
					ls.add(traphName);
				}else
				{
					List<String> ls = new ArrayList<String>();
					ls.add(traphName);
					usedPtn2MPort.put(ptpName, ls);
				}
			}
			logger.info("获取所有使用了2M端口的电路usedPtn2MPort结束");
			Set<Entry<String, List<String>>> es = usedPtn2MPort.entrySet();
			for(Entry<String, List<String>> e:es) 
			{
				String ptpName = e.getKey();
				List<String> traphLs = e.getValue();
				int size = traphLs.size();
				Map<String, Object> map = null;
				if(size>1)//如果有两条或以上电路都使用了，此端口不能再次被使用
				{
					repeatPtpCount++;
					errorMsg.append("2M端口>"+ptpName+"被电路【");	
					for(int i=0;i<size;i++) 
					{
						map = new HashMap<String, Object>();
						String traphName = traphLs.get(i);
						map.put("PTP_NAME", ptpName);
						map.put("TRAPH_NAME", traphName);
						repeatList.add(map);
						errorMsg.append(traphName+",");
					}
					errorMsg.append("】同时使用\r\n");
					logger.info("端口["+ptpName+"]被"+size+"条电路使用");
				}
			}
			logger.info("计算被2M端口占用的电路结束");
			if(repeatPtpCount>0)//如果有端口被多条电路使用 
			{
				success = 0;
				msg = "校验未通过，2M端口占用数量【"+repeatPtpCount+"】";
			}
			logger.info("统计2M端口复用情况结束");
		}
		//logger.info("2M端口复用errorMsg等于"+errorMsg);
		logger.info("查找已经使用了2M端口的PTN电路结束");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		//resultMap.put("errorCount", repeatPtpCount);
		resultMap.put("repeatPtpCount", repeatPtpCount);
		resultMap.put("repeatList", repeatList);
		resultMap.put("errorMsg", errorMsg.toString());
		return resultMap;
	}
	
	/**
	 * 校验时隙是否冲突
	 * 
	 * @param attempTraphCuidList
	 * @return {key:success, key:msg, key:repeatCtpCount, key:repeatCtp2TraphMap}
	 */
	public Map<String, Object> validateRepeatCtp(List<String> attempTraphCuidList) {
		Map<String, List<String>> repeatCtp2TraphMap = new HashMap<String, List<String>>();
		//冲突时隙数
		int repeatCtpCount = 0;
		//校验是否通过，默认通过
		int success = 1;
		String msg = "校验通过";
		
		Map<String, Object> pm = new HashMap<String, Object>();
		//查询调度电路
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(attempTraphCuidList);
		
		//电路速率集合
		Map<String, String> traphRateMap = new HashMap<String, String>();
		//过滤停闭电路的调度电路
		List<String> newAttempCuidList = new ArrayList<String>();
		
		List<String> closeList = new ArrayList<String>();
		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
		
 		if(attempTraphList != null && attempTraphList.size() > 0) {
			for(Map<String, Object> map : attempTraphList) {
				Integer schedultType = IbatisDAOHelper.getIntValue(map, "SCHEDULE_TYPE");
				if(SheetConstants.SCHEDULE_TYPE_CLOSE  == schedultType ){
					String endPortA =  IbatisDAOHelper.getStringValue(map, "END_PORT_A");
					String endPortZ =  IbatisDAOHelper.getStringValue(map, "END_PORT_Z");
					closeList.add(endPortA);
					closeList.add(endPortZ);
				} else {
					newList.add(map);					
				}
			}
		}
 		//河南SDH基站开通使用调前资源【利旧】功能
 		for(Map<String, Object> map : newList){
 			String endPortA =  IbatisDAOHelper.getStringValue(map, "END_PORT_A");
			String endPortZ =  IbatisDAOHelper.getStringValue(map, "END_PORT_Z");
			String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
			String traphRate = IbatisDAOHelper.getStringValue(map, "TRAPH_RATE");
			if(!closeList.contains(endPortA) && !closeList.contains(endPortZ)){
				newAttempCuidList.add(cuid);
				traphRateMap.put(cuid, traphRate);
			}
 		}
		if(newAttempCuidList != null && !newAttempCuidList.isEmpty()) {
			pm.clear();
			pm.put("attempTraphCuidList", newAttempCuidList);
			//查询调度电路的时隙和关联的存量电路
			List<Map<String, Object>> dataList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransCtp", pm);
			
			if(dataList != null && dataList.size() > 0) {
				//调整前电路CUID集合
				List<String> traphCuidList = new ArrayList<String>();
				//时隙速率集合
				Map<String, String> ctpRateMap = new HashMap<String, String>();
				//需要校验的时隙CUID集合
				List<String> ctpCuidList = new ArrayList<String>();
				Set<String> ctpCuidSet = new HashSet<String>();
				
				for(Map<String, Object> map : dataList) {
					String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
					String aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String actpRate = IbatisDAOHelper.getStringValue(map, "A_POINT_RATE");
					String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					String zCtpRate = IbatisDAOHelper.getStringValue(map, "Z_POINT_RATE");
					
					ctpRateMap.put(aCtpCuid, actpRate);
					ctpRateMap.put(zCtpCuid, zCtpRate);
					if(StringUtils.isNotEmpty(aCtpCuid)){
						ctpCuidSet.add(aCtpCuid);
					}
					if(StringUtils.isNotEmpty(zCtpCuid)){
						ctpCuidSet.add(zCtpCuid);
					}
					if(StringUtils.isNotBlank(relatedTraphCuid)) {
						traphCuidList.add(relatedTraphCuid);
					}
				}
				ctpCuidList.addAll(ctpCuidSet);
				
                //避免时隙数大于1000，导致ORACLE查询条件过多报错
				List<Map<String, Object>> repeatSdhList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> repeatPtnList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> repeatPtnList2 = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> logicPortList = new ArrayList<Map<String, Object>>();
				List<String> ctpCuidListTemp=new ArrayList<String>();
				if(ctpCuidList.size()>0){
					pm.clear();
					for (int i =0;i<ctpCuidList.size();i++){
						if((i!=0&&i%500==0)||i==ctpCuidList.size()-1){
							ctpCuidListTemp.add(ctpCuidList.get(i));
							List<Map<String, List<String>>> ctpCuidGroupTemp = IbatisDAOHelper.pareseGroupList(ctpCuidListTemp, 1000);
							logger.info("----------------ctpCuidListTemp.size():"+ctpCuidListTemp.size());
							pm.put("ctpCuidGroup", ctpCuidGroupTemp);
							//查询占用冲突时隙的SDH电路
							List<Map<String, Object>> repeatSdhListTemp = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatTransCtp", pm);
							List<Map<String, Object>> repeatPtnListTemp = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnCtp", pm);
							List<Map<String, Object>> repeatPtnListTemp2 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRepeatPtnCtp2", pm);
							repeatSdhList.addAll(repeatSdhListTemp);
							repeatPtnList.addAll(repeatPtnListTemp);
							repeatPtnList2.addAll(repeatPtnListTemp2);
							ctpCuidListTemp = new ArrayList<String>(); 
						}else{
							ctpCuidListTemp.add(ctpCuidList.get(i));
						}
					}
					List<Map<String, List<String>>> ctpCuidGroup = IbatisDAOHelper.pareseGroupList(ctpCuidList, 1000);
					logger.info("----------------ctpCuidList.size():"+ctpCuidList.size());
					pm.clear();
					pm.put("ctpCuidGroup", ctpCuidGroup);
					// 查询时隙打散表
					logicPortList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryLogicPort", pm);
				}
								
				//冲突时隙与冲突电路集合
				Map<String, List<String>> ctp2TraphMap = new HashMap<String, List<String>>();
				
				if(repeatSdhList != null && !repeatSdhList.isEmpty()) {
					for(Map<String, Object> map : repeatSdhList) {
						String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID");
						String ctpCuid = IbatisDAOHelper.getStringValue(map, "POINT_CUID");
						
						List<String> list = ctp2TraphMap.get(ctpCuid);
						if(list == null) {
							list = new ArrayList<String>();
							ctp2TraphMap.put(ctpCuid, list);
						}
						if(StringUtils.isNotBlank(relatedTraphCuid)) list.add(relatedTraphCuid);
					}
				}
				
				if(repeatPtnList != null && !repeatPtnList.isEmpty()) {
					for(Map<String, Object> map : repeatPtnList) {
						String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID");
						String ctpCuid = IbatisDAOHelper.getStringValue(map, "POINT_CUID");
						
						List<String> list = ctp2TraphMap.get(ctpCuid);
						if(list == null) {
							list = new ArrayList<String>();
							ctp2TraphMap.put(ctpCuid, list);
						}
						if(StringUtils.isNotBlank(relatedTraphCuid)) list.add(relatedTraphCuid);
					}
				}
				
				if(repeatPtnList2 != null && !repeatPtnList2.isEmpty()) {
					for(Map<String, Object> map : repeatPtnList2) {
						String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID");
						String ctpCuid = IbatisDAOHelper.getStringValue(map, "POINT_CUID");
						
						List<String> list = ctp2TraphMap.get(ctpCuid);
						if(list == null) {
							list = new ArrayList<String>();
							ctp2TraphMap.put(ctpCuid, list);
						}
						if(StringUtils.isNotBlank(relatedTraphCuid)) list.add(relatedTraphCuid);
					}
				}
				//注：由于在路由设计提交按钮处，对打散时隙是否被调度或存量电路占用进行了判断，所以此处屏蔽
				if(logicPortList != null && !logicPortList.isEmpty()) {
					for(Map<String, Object> map : logicPortList) {
						String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
						String ctpCuid = IbatisDAOHelper.getStringValue(map, "CTP_CUID");
						String logicRate = IbatisDAOHelper.getStringValue(map, "LOGIC_RATE");
						String relatedAttempTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_ATTEMP_TRAPH_CUID");
						String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
						
						if(ctp2TraphMap.containsKey(ctpCuid)) {
							ctp2TraphMap.remove(ctpCuid);
						}
						
						List<String> list = ctp2TraphMap.get(cuid);
						if(list == null) {
							list = new ArrayList<String>();
							ctp2TraphMap.put(cuid, list);
						}
						if(StringUtils.isNotBlank(relatedAttempTraphCuid)) list.add(relatedAttempTraphCuid);
						if(StringUtils.isNotBlank(relatedTraphCuid)) list.add(relatedTraphCuid);
						
						ctpRateMap.put(cuid, logicRate);
					}
				}
				
				if(ctp2TraphMap != null && !ctp2TraphMap.isEmpty()) {
					//过滤调整前电路
					if(traphCuidList != null && !traphCuidList.isEmpty()) {
						for(String ctpCuid : ctp2TraphMap.keySet()) {
							List<String> tempTraphList = ctp2TraphMap.get(ctpCuid);
							tempTraphList.removeAll(traphCuidList);
						}
					}
					
					for(String ctpCuid : ctp2TraphMap.keySet()) {
						String ctpRate = ctpRateMap.get(ctpCuid);
						List<String> tempTraphCuidList = ctp2TraphMap.get(ctpCuid);
						
						if("1".equals(ctpRate)) {
							//2M时隙，判断占用电路是否大于1
							if(tempTraphCuidList.size() > 1) {
								repeatCtpCount++;
								
								repeatCtp2TraphMap.put(ctpCuid, tempTraphCuidList);
							}
						} else if("9".equals(ctpRate)) {
							//155M时隙，判断2M占用电路是否大于63，155M占用电路是否大于1
							Integer totalRate = 0;
							if(tempTraphCuidList != null && !tempTraphCuidList.isEmpty()) {
								boolean repeatFlag = false;
								
								for(String tempTraphCuid : tempTraphCuidList) {
									Integer rateType = IbatisDAOHelper.getIntValue(traphRateMap, tempTraphCuid);
									if(rateType == 1) {
										totalRate += 2;
									} else if(rateType == 9) {
										totalRate += 155;
									} else {
										
									}
								}
								
								if(totalRate > 155) {
									repeatFlag = true;
									
									repeatCtp2TraphMap.put(ctpCuid, tempTraphCuidList);
								}
								
								if(repeatFlag) {
									repeatCtpCount++;
								}
							}
						}
					}
				}
				
				if(repeatCtpCount > 0) {
					success = 0;
					msg = "校验未通过，占用时隙数量【"+repeatCtpCount+"】";
				}
			}
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		resultMap.put("repeatCtpCount", repeatCtpCount);
		resultMap.put("repeatCtp2TraphMap", repeatCtp2TraphMap);
		
		return resultMap;
	}
	
	/**
	 * 获取BS_LTE路由信息
	 * @param request
	 * @param traphList
	 */
	public List<Map> getAttempRoute(List<Map> traphList){
		List<Map> routeList = new ArrayList<Map>();
		List<Map> traphRouteList = new ArrayList<Map>();
		Map<String,Object> nmlteMap = new HashMap<String,Object>();
		for(Map<String,Object> map : traphList){
			String extIds = IbatisDAOHelper.getStringValue(map, "EXT_IDS");
			if(StringUtils.isNotEmpty(extIds) && extIds.indexOf("102")>-1){
				String traphCuid = IbatisDAOHelper.getStringValue(map, "CUID");
				Map pm = new HashMap();
				pm.put("cuid", traphCuid);
				routeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getAttempPtnPathInfo", pm);
			}else{
				nmlteMap = map;
			}
		}
		Map<String,Object> routeMap = routeList.get(0);
		traphRouteList.add(routeMap);
		traphRouteList.add(nmlteMap);
		return traphRouteList;
	}
	/**
	 * NM_LTE路由复制
	 * @param request
	 * @param traphList
	 */
	public void copyBslteByCuid(Map<String,Object> addMesMap, Map routeMap, Map nmlteMap, String taskCuid){
		List<Map<String,Object>> routeList = new ArrayList<Map<String,Object>>();
		List<Record> recordList = new ArrayList<Record>();
		List<Record> paramRecordList = new ArrayList<Record>();
		List<Record> pkRecordList = new ArrayList<Record>();
		String serviceCuid = IbatisDAOHelper.getStringValue(nmlteMap, "CUID");
		Map map = new HashMap();
		map.put("cuid", serviceCuid);
		List<Map<String,Object>> routeCuidsList =  this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getNmlteDesignedRouteCuids", map);
		List<String> portList = new ArrayList<String>();
		if(StringUtils.isNotEmpty((String)routeMap.get("L2L3_PORT_CUID"))){
			portList.add((String)routeMap.get("L2L3_PORT_CUID"));
		}
		if(StringUtils.isNotEmpty((String)routeMap.get("RELATED_A_PTP_CUID"))){
			portList.add((String)routeMap.get("RELATED_A_PTP_CUID"));
		}
		if(StringUtils.isNotEmpty((String)routeMap.get("RELATED_Z_PTP_CUID"))){
			portList.add((String)routeMap.get("RELATED_Z_PTP_CUID"));
		}
		String vlanId = IbatisDAOHelper.getStringValue(addMesMap, "VLANID");
		if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
			map.clear();
			map.put("vlanId", vlanId);
			map.put("portList", portList);
			List<String> ptnPathList =  this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPtnPathExistVlanid", map);
			if(ptnPathList !=null && ptnPathList.size()>0)throw new RuntimeException("VLANID已使用，请重新填写一个未使用的VLANID！");
		}
		String routeDescription = IbatisDAOHelper.getStringValue(routeMap, "LABEL_CN");
		routeDescription = StringUtils.substring(routeDescription, 0, routeDescription.indexOf("VLanID"));
		routeDescription = routeDescription +"VLanID="+ vlanId;
		Date now = new Date();
		Record attempTraphRecordPk = new Record("ATTEMP_TRAPH");
		Record pathRecord = new Record("ATTEMP_PTN_PATH");
		Record routeRecord = new Record("ATTEMP_TRAPH_ROUTE");
		Record routePathRecord = new Record("ATTTRAPH_ROUTE_TO_PATH");
		Record attempTraphRecord = new Record("ATTEMP_TRAPH");
		Record ptnIpRecord = new Record("T_ATTEMP_PTN_PATH_TO_IP");
		attempTraphRecordPk.addColValue("CUID", serviceCuid);
		//判断NM_LTE电路是否已设计
		if(routeCuidsList!=null && routeCuidsList.size()>0){
			throw new RuntimeException("业务类型为NM_LTE的电路存在已设计的相关数据！");
		}/*else{
			String ipAddr = IbatisDAOHelper.getStringValue(addMesMap, "IP_ADDR");
			String netAddr = IbatisDAOHelper.getStringValue(addMesMap, "NET_ADDR");
			List<String> ipList = new ArrayList<String>();
			ipList.add(ipAddr);
			if(StringUtils.isNotEmpty(netAddr)&&!ipList.contains(netAddr)){
				ipList.add(netAddr);
			}
			List<Map<String,Object>> ipMapList = new ArrayList<Map<String,Object>>();
			if(ipList!=null && ipList.size()>0){
				Map mp = new HashMap();
				mp.put("ipList", ipList);
				if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
					mp.put("useTypevalue", "2");
				}
				ipMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphDesign.getBusinessIpCuid", mp);
			}
			Map<String,Map<String,Object>> ipMap = new HashMap<String,Map<String,Object>>();
			if(ipMapList!=null&&ipMapList.size()>0){
				ipMap = IbatisDAOHelper.parseList2Map(ipMapList, "ADDR");
			}*/
			String pathCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_PTN_PATH");
			pathRecord.addColValue("CUID", pathCuid);
			pathRecord.addColValue("RELATED_ROUTE_CUID", serviceCuid);
			pathRecord.addColSqlValue("OBJECTID", pathRecord.getObjectIdSql());
			pathRecord.addColValue("ISDELETE", 0);
			pathRecord.addColValue("GT_VERSION", 0);
			pathRecord.addColValue("IS_DISPACH_OPTICAL", 0);
			pathRecord.addColValue("OBJECT_TYPE_CODE", 1001);
			pathRecord.addColValue("CREATE_TIME", now);
			
			String routeCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_TRAPH_ROUTE");
			routeRecord.addColValue("CUID", routeCuid);
			routeRecord.addColValue("RELATED_SERVICE_CUID", serviceCuid);
			routeRecord.addColSqlValue("OBJECTID", routeRecord.getObjectIdSql());
			routeRecord.addColValue("GT_VERSION", 0);
			routeRecord.addColValue("ROUTE_INDEX", 0);
			routeRecord.addColValue("WORK_STATE", 1);
			routeRecord.addColValue("ISDELETE", 0);
			routeRecord.addColValue("OBJECT_TYPE_CODE", 9002);
			routeRecord.addColValue("CREATE_TIME", now);
			
			routePathRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("ATTTRAPH_ROUTE_TO_PATH"));
			routePathRecord.addColSqlValue("OBJECTID", routePathRecord.getObjectIdSql());
			routePathRecord.addColValue("GT_VERSION", 0);
			routePathRecord.addColValue("TRAPH_ROUTE_CUID", routeCuid);
			routePathRecord.addColValue("PATH_CUID", pathCuid);
			routePathRecord.addColValue("ISDELETE", 0);
			routePathRecord.addColValue("CREATE_TIME", now);
			
			ptnIpRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
			ptnIpRecord.addColValue("RELATED_PTN_PATH_CUID", pathCuid);
			ptnIpRecord.addColValue("TYPE", "BUSINESS");
			String ipAddr = IbatisDAOHelper.getStringValue(addMesMap, "IP_ADDR");
//			if(ipMap.get(ipAddr)==null){
//				throw new RuntimeException("IP"+ipAddr+"或不可用或不存在于数据库，请检查后重新操作！");
//			}
//			Map<String,Object> newIpMap = ipMap.get(ipAddr);
//			ptnIpRecord.addColValue("RELATED_NUMBER_IP_CUID", (String)newIpMap.get("CUID"));
			ptnIpRecord.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '" + ipAddr + "'");
			recordList.add(ptnIpRecord);
			
			String netAddr = IbatisDAOHelper.getStringValue(addMesMap, "NET_ADDR");
			if(StringUtils.isNotEmpty(netAddr)){
//				if(ipMap.get(netAddr)==null){
//					throw new RuntimeException("IP"+netAddr+"或不可用或不存在于数据库，请检查后重新操作！");
//				}
//				Map<String,Object> newNetIpMap = ipMap.get(netAddr);
				Record ptnNetRecord = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				ptnNetRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
				ptnNetRecord.addColValue("RELATED_PTN_PATH_CUID", pathCuid);
				ptnNetRecord.addColValue("TYPE", "NETCONFIG");
//				ptnNetRecord.addColValue("RELATED_NUMBER_IP_CUID", (String)newNetIpMap.get("CUID"));
				ptnNetRecord.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '" + netAddr + "'");
				recordList.add(ptnNetRecord);
//			}
		}
		//更新ATTEMP_PTN_PATH表的信息或插入ATTEMP_PTN_PATH表的部分信息
		pathRecord.addColValue("LABEL_CN", routeDescription);
		pathRecord.addColValue("ROUTE_DESCIPTION", routeDescription);
		pathRecord.addColValue("PATH_TYPE", 2);
		pathRecord.addColValue("ORIG_POINT_CUID", IbatisDAOHelper.getStringValue(routeMap, "ORIG_POINT_CUID"));
		pathRecord.addColValue("DEST_POINT_CUID", IbatisDAOHelper.getStringValue(routeMap, "DEST_POINT_CUID"));
		pathRecord.addColValue("RELATED_A_NE_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_A_NE_CUID"));
		pathRecord.addColValue("RELATED_A_NE_CUID2", IbatisDAOHelper.getStringValue(routeMap, "RELATED_A_NE_CUID2"));
		pathRecord.addColValue("RELATED_A_PTP_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_A_PTP_CUID"));
		pathRecord.addColValue("RELATED_A_PTP_CUID2", IbatisDAOHelper.getStringValue(routeMap, "RELATED_A_PTP_CUID2"));
		pathRecord.addColValue("RELATED_Z_NE_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_NE_CUID"));
		pathRecord.addColValue("RELATED_Z_NE_CUID2", IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_NE_CUID2"));
		pathRecord.addColValue("RELATED_Z_PTP_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_PTP_CUID"));
		pathRecord.addColValue("RELATED_Z_PTP_CUID2", IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_PTP_CUID2"));
		pathRecord.addColValue("L2L3_PORT_CUID", IbatisDAOHelper.getStringValue(routeMap, "L2L3_PORT_CUID"));
		pathRecord.addColValue("L2L3_PORT_NAME", IbatisDAOHelper.getStringValue(routeMap, "L2L3_PORT_NAME"));
		pathRecord.addColValue("L2L3_PORT_CUID2", IbatisDAOHelper.getStringValue(routeMap, "L2L3_PORT_CUID2"));
		pathRecord.addColValue("L2L3_PORT_NAME2", IbatisDAOHelper.getStringValue(routeMap, "L2L3_PORT_NAME2"));
		pathRecord.addColValue("VLANID", IbatisDAOHelper.getStringValue(addMesMap, "VLANID"));
		pathRecord.addColValue("CIR_BAND", IbatisDAOHelper.getStringValue(addMesMap, "CIR_BAND"));
		pathRecord.addColValue("PIR_BAND", IbatisDAOHelper.getStringValue(addMesMap, "PIR_BAND"));
		pathRecord.addColValue("SERVICE_PRIORITY", IbatisDAOHelper.getStringValue(addMesMap, "QOS"));
		pathRecord.addColValue("LTE_CODE", IbatisDAOHelper.getStringValue(addMesMap, "LTE_CODE"));
		pathRecord.addColValue("BSVLANID", IbatisDAOHelper.getStringValue(addMesMap, "BASE_VLANID"));
		pathRecord.addColValue("LAST_MODIFY_TIME", now);

		recordList.add(pathRecord);
		//更新ATTEMP_TRAPH_ROUTE表的信息或插入ATTEMP_TRAPH_ROUTE表的部分信息
		routeRecord.addColValue("ROUTE_DESCRIPTION", routeDescription);
		routeRecord.addColValue("LAST_MODIFY_TIME", now);
		recordList.add(routeRecord);
		//更新ATTTRAPH_ROUTE_TO_PATH表的信息或插入ATTTRAPH_ROUTE_TO_PATH表的部分信息
		routePathRecord.addColValue("PATH_TYPE", "ATTEMP_PTN_PATH");
		routePathRecord.addColValue("INDEX_PATH_ROUTE", IbatisDAOHelper.getStringValue(routeMap, "INDEX_PATH_ROUTE"));
		routePathRecord.addColValue("AVLANID", IbatisDAOHelper.getStringValue(routeMap, "AVLANID"));
		routePathRecord.addColValue("ZVLANID", IbatisDAOHelper.getStringValue(routeMap, "ZVLANID"));
		routePathRecord.addColValue("WORK_MODE", IbatisDAOHelper.getStringValue(routeMap, "WORK_MODE"));
		routePathRecord.addColValue("MSTP_EXTS", IbatisDAOHelper.getStringValue(routeMap, "MSTP_EXTS"));
		routePathRecord.addColValue("A_MAC", IbatisDAOHelper.getStringValue(routeMap, "A_MAC"));
		routePathRecord.addColValue("A_MP", IbatisDAOHelper.getStringValue(routeMap, "A_MP"));
		routePathRecord.addColValue("Z_MAC", IbatisDAOHelper.getStringValue(routeMap, "Z_MAC"));
		routePathRecord.addColValue("Z_MP", IbatisDAOHelper.getStringValue(routeMap, "Z_MP"));
		routePathRecord.addColValue("LAST_MODIFY_TIME", now);
		recordList.add(routePathRecord);
		//更新ATTEMP_TRAPH表的信息或插入ATTEMP_TRAPH表的部分信息
		String aPortCuid = IbatisDAOHelper.getStringValue(routeMap, "L2L3_PORT_CUID");
		String zPortCuid = IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_PTP_CUID");
		attempTraphRecord.addColValue("RELATED_NE_A_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_A_NE_CUID"));
		attempTraphRecord.addColValue("RELATED_NE_Z_CUID", IbatisDAOHelper.getStringValue(routeMap, "RELATED_Z_NE_CUID"));
		attempTraphRecord.addColValue("RELATED_A_PORT_CUID", aPortCuid);
		attempTraphRecord.addColValue("RELATED_Z_PORT_CUID", zPortCuid);
		attempTraphRecord.addColSqlValue("END_PORT_A", "SELECT LABEL_CN FROM PTP WHERE CUID ='"+aPortCuid+"'");
		attempTraphRecord.addColSqlValue("END_PORT_Z", "SELECT LABEL_CN FROM PTP WHERE CUID ='"+zPortCuid+"'");
		attempTraphRecord.addColValue("PATHINFO", routeDescription);
		attempTraphRecord.addColValue("DESIGN_INFO", routeDescription);
		attempTraphRecord.addColValue("EXT_TYPE", "7");
//		attempTraphRecord.addColValue("EXT_TYPE", "5");
//		attempTraphRecord.addColValue("IS_WHOLE_ROUTE", 1);
		if(attempTraphRecordPk.getValues().size()>0){
			paramRecordList.add(attempTraphRecordPk);
			pkRecordList.add(attempTraphRecord);
		}
		//更新T_TASK_TO_SERVICE表的信息或插入T_TASK_TO_SERVICE表的部分信息
		map.clear();
		map.put("serviceCuid", serviceCuid);
		map.put("taskCuid", taskCuid);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateStateByCuids", map);
		
		if(recordList != null && recordList.size()>0){
			this.IbatisResDAO.insertDynamicTableBatch(recordList);
		}
		if(pkRecordList != null && pkRecordList.size()>0){
			this.IbatisResDAO.updateDynamicTableBatch(pkRecordList,paramRecordList);
		}
	}
	
	/**
	 * 同步电路业务站点
	 * @param serviceCuidList
	 */
	public void synTraphSrvSite(List<String> serviceCuidList) {
		if (serviceCuidList == null || serviceCuidList.size() == 0) {
			throw new RuntimeException("未指定任何电路！");
		}
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
		
		List<Record> paramList = new ArrayList<Record>();
		List<Record> pkList = new ArrayList<Record>();
		
		if(attempTraphList != null && !attempTraphList.isEmpty()) {
			for(Map<String, Object> map : attempTraphList) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				String relatedASiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_SITE_CUID");
				String relatedZSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_SITE_CUID");
				String relatedAZdSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_ZD_SITE_CUID");
				String relatedZZdSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_ZD_SITE_CUID");
				String zdSiteTypeA = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_A");
				String zdSiteTypeZ = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_Z");
				
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", cuid);
				pkList.add(pk);
				
				Record param = new Record("ATTEMP_TRAPH");
				param.addColValue("CUID", cuid);
				if(StringUtils.isBlank(relatedASiteCuid) || !relatedASiteCuid.equals(relatedAZdSiteCuid)) {
					if("SITE".equals(zdSiteTypeA)) {
						param.addColValue("RELATED_A_SITE_CUID", relatedAZdSiteCuid);
					} else if("ROOM".equals(zdSiteTypeA)) {
						param.addColSqlValue("RELATED_A_SITE_CUID", "SELECT R.RELATED_SPACE_CUID FROM ROOM R WHERE R.CUID='"+relatedAZdSiteCuid+"'");
					} else if("TRANS_ELEMENT".equals(zdSiteTypeA)) {
						param.addColSqlValue("RELATED_A_SITE_CUID", "SELECT E.RELATED_SITE_CUID FROM TRANS_ELEMENT E WHERE E.CUID='"+relatedAZdSiteCuid+"'");
					}
				}
				
				if(StringUtils.isBlank(relatedZSiteCuid) || !relatedZSiteCuid.equals(relatedZZdSiteCuid)) {
					if("SITE".equals(zdSiteTypeZ)) {
						param.addColValue("RELATED_Z_SITE_CUID", relatedZZdSiteCuid);
					} else if("ROOM".equals(zdSiteTypeZ)) {
						param.addColSqlValue("RELATED_Z_SITE_CUID", "SELECT R.RELATED_SPACE_CUID FROM ROOM R WHERE R.CUID='"+relatedZZdSiteCuid+"'");
					} else if("TRANS_ELEMENT".equals(zdSiteTypeZ)) {
						param.addColSqlValue("RELATED_Z_SITE_CUID", "SELECT E.RELATED_SITE_CUID FROM TRANS_ELEMENT E WHERE E.CUID='"+relatedZZdSiteCuid+"'");
					}
				}
				paramList.add(param);
			}
			
			if(paramList != null && !paramList.isEmpty()) {
				this.IbatisResDAO.updateDynamicTableBatch(paramList, pkList);
			}
		}
	}
	
	/**
	 * 电路局向颠倒（仅支持新增，且未进行路由设计的电路）
	 * @param serviceCuidList
	 */
	public void triggerTraphAZ(List<String> serviceCuidList) {
		if (serviceCuidList == null || serviceCuidList.size() == 0) {
			throw new RuntimeException("未指定任何电路！");
		}
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
		Map<String, Integer> pathNumMap = this.getPathNum(serviceCuidList);

		List<Record> updatePkList = new ArrayList<Record>();
		List<Record> updateList = new ArrayList<Record>();
		for (Map<String, Object> attempTraph : attempTraphList) {
			String cuid = IbatisDAOHelper.getStringValue(attempTraph, "CUID");
			Integer pathCount = pathNumMap.get(cuid);
			if (pathCount != null && pathCount > 0) {
				throw new RuntimeException("只允许颠倒未包含路由的电路！");
			}
			Record rpk = new Record("ATTEMP_TRAPH");
			rpk.addColValue("CUID", cuid);
			updatePkList.add(rpk);
			Record r = new Record("ATTEMP_TRAPH");
			updateList.add(r);
			// 局向信息颠倒
			for (String key : attempTraph.keySet()) {
				if (key.endsWith("_A")) {
					String otherKey = key.substring(0, key.length() - 2) + "_Z";
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				} else if (key.endsWith("_Z")) {
					String otherKey = key.substring(0, key.length() - 2) + "_A";
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				} else if (key.endsWith("_ACUID")) {
					String otherKey = key.substring(0, key.length() - 6) + "_ZCUID";
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				} else if (key.endsWith("_ZCUID")) {
					String otherKey = key.substring(0, key.length() - 6) + "_ACUID";
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				} else if (key.indexOf("_A_") != -1) {
					String otherKey = key.replace("_A_", "_Z_");
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				} else if (key.indexOf("_Z_") != -1) {
					String otherKey = key.replace("_Z_", "_A_");
					String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
					r.addColValue(key, otherVal);
				}
			}
			for (String key : attempTraph.keySet()) {
				if (key.equals("ACCESS_POINT_A")) {
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(attempTraph, key))){
						String otherKey = key.substring(0, key.length() - 2) + "_Z";
						String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
						r.addColValue("JHROOM_A", otherVal);
						r.addColValue("JHROOM_Z", IbatisDAOHelper.getStringValue(attempTraph, key));
					}
				}
				if (key.equals("ACCESS_POINT_Z")) {
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(attempTraph, key))){
						String otherKey = key.substring(0, key.length() - 2) + "_A";
						String otherVal = IbatisDAOHelper.getStringValue(attempTraph, otherKey);
						r.addColValue("JHROOM_Z", otherVal);
						r.addColValue("JHROOM_A", IbatisDAOHelper.getStringValue(attempTraph, key));
					}
				}
			}
			// 电路名称更新
			String labelCn = IbatisDAOHelper.getStringValue(attempTraph, "LABEL_CN");
			String endStationA = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_END_STATION");
			String endStationZ = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_END_STATION");
			String traphRateName = IbatisDAOHelper.getStringValue(attempTraph, "N_TRAPH_RATE");
			String no = IbatisDAOHelper.getStringValue(attempTraph, "NO");
			if (StringUtils.isNumeric(no)) {
				no = StringHelper.changeIntToFixedLthStr(Integer.parseInt(no), 4);
			}
			String extIds = IbatisDAOHelper.getStringValue(attempTraph, "EXT_IDS");
			if (StringUtils.isNotBlank(extIds)) {
				extIds = TraphNameBO.getServiceName(extIds);
			} else {
				extIds = "";
			}
			StringBuffer suffixBf = new StringBuffer();
			for (String suffix : TraphConstants.suffixList) {
				if (labelCn.indexOf(suffix) != -1) {
					suffixBf.append(suffix);
				}
			}
			if (StringUtils.isBlank(endStationA) || StringUtils.isBlank(endStationZ)
					|| StringUtils.isBlank(traphRateName) || StringUtils.isBlank(no)) {
				throw new RuntimeException("电路终端站点、速率或者序号无法解析，无法完成颠倒！");
			}
			String newLabelCn = endStationZ + "-" + endStationA + traphRateName + no + extIds + suffixBf.toString();
			r.addColValue("LABEL_CN", newLabelCn);
			
			r.remove("RELATED_A_END_STATION");
			r.remove("RELATED_Z_END_STATION");
			r.remove("RELATED_A_SITE");
			r.remove("RELATED_Z_SITE");
			r.remove("RELATED_A_DISTRICT_SITE");
			r.remove("RELATED_Z_DISTRICT_SITE");
			r.remove("RELATED_A_ZD_SITE");
			r.remove("RELATED_Z_ZD_SITE");
			r.remove("RELATED_A_ZD_SITE_SITE_CUID");
			r.remove("RELATED_Z_ZD_SITE_SITE_CUID");
			r.remove("A_END_STATION_ABBREVIATION");
			r.remove("RELATED_Z_END_LEVEL");
			r.remove("Z_END_STATION_ABBREVIATION");
			r.remove("RELATED_A_END_LEVEL");
			r.remove("CUSTOM_A_NAME");
			r.remove("CUSTOM_Z_NAME");
			r.remove("CUSTOM_PHONE_A");
			r.remove("CUSTOM_PHONE_Z");
			r.remove("ACCESS_POINT_A");
			r.remove("ACCESS_POINT_Z");
		}
		IbatisResDAO.updateDynamicTableBatch(updateList, updatePkList);
	}
	
	/**
	 * 更新电路局向
	 * @param attempTraphList
	 */
	public void changeAttempTraphAZ(List<AttempTraphService> attempTraphList) {
		List<String> serviceCuidList = new ArrayList<String>();
		Map<String, AttempTraphService> newAttTraphMap = new HashMap<String, AttempTraphService>();
		
		for (AttempTraphService attempTraph : attempTraphList) {
			String cuid = attempTraph.getCuid();
			serviceCuidList.add(cuid);
			newAttTraphMap.put(cuid, attempTraph);
		}
		
		Map<String, AttempTraphService> oldAttTraphMap = new HashMap<String, AttempTraphService>();
		List<IService> serviceList = this.findService(serviceCuidList);
		//电路名称按局向、速率分组
		Map<TraphName, List<TraphName>> traphNameMap = new HashMap<TraphName, List<TraphName>>();
//		List<String> delRouteServiceCuidList = new ArrayList<String>();
//		boolean hasChangeAZflag = false;
		for (IService service : serviceList) {
			AttempTraphService oldAttTraph = (AttempTraphService)service;
			String cuid = oldAttTraph.getCuid();
			oldAttTraphMap.put(cuid, oldAttTraph);
			AttempTraphService newAttTraph = newAttTraphMap.get(cuid);
			
			boolean isNameChange = false;
			String ap = null, zp = null;
			int rate = 0;
			if(StringUtils.isNotBlank(newAttTraph.getAPointCuid()) &&
					!newAttTraph.getAPointCuid().equals(oldAttTraph.getAPointCuid())) {
				isNameChange = true;
//				hasChangeAZflag = true;
				ap = newAttTraph.getAPointCuid();
			}else {
				ap = oldAttTraph.getAPointCuid();
			}
			if(StringUtils.isNotBlank(newAttTraph.getZPointCuid()) &&
					!newAttTraph.getZPointCuid().equals(oldAttTraph.getZPointCuid())) {
				isNameChange = true;
//				hasChangeAZflag = true;
				zp = newAttTraph.getZPointCuid();
			}else {
				zp = oldAttTraph.getZPointCuid();
			}
			if(newAttTraph.getRate() != 0 && oldAttTraph.getRate() != newAttTraph.getRate()) {
				isNameChange = true;
				rate = newAttTraph.getRate();
			}else {
				rate = oldAttTraph.getRate();
			}
			if(isNameChange) {
				TraphName name = new TraphName(ap, zp, rate);
				name.setCuid(cuid);
				List<TraphName> nameList = traphNameMap.get(name);
				if(nameList == null) {
					nameList = new ArrayList<TraphName>();
					traphNameMap.put(name, nameList);
				}
				nameList.add(name);
			}
//			if(hasChangeAZflag){
//				delRouteServiceCuidList.add(cuid);
//			}
		}
		//批量生成新的电路名称
		Map<String, Record> updateNameRdsMap = new HashMap<String, Record>();
		for(TraphName nkey : traphNameMap.keySet()) {
			List<TraphName> updateNameList = traphNameMap.get(nkey);
			List<TraphName> newNameList = TraphNameBO.getTraphNames(nkey.getaStationId(), nkey.getzStationId(), nkey.getRate(), updateNameList.size());
			for(int i = 0; i < newNameList.size(); i++) {
				TraphName name = updateNameList.get(i);
				TraphName nName = newNameList.get(i);
				AttempTraphService newAttTraph = newAttTraphMap.get(name.getCuid());
				String labelCn = nName.getLabelCn();
				if (StringUtils.isNotBlank(newAttTraph.getExtIds())) {
			          labelCn = labelCn + this.TraphNameBO.getServiceName(newAttTraph.getExtIds());
		        } else {
			          AttempTraphService oldAttTraph = (AttempTraphService)oldAttTraphMap.get(nkey.getCuid());
			          labelCn = labelCn + this.TraphNameBO.getServiceName(oldAttTraph.getExtIds());
		        }
				if(StringUtils.isNotBlank(newAttTraph.getSuffixLabel())) {
					String suffixLabel = newAttTraph.getSuffixLabel();
					if(!suffixLabel.startsWith("/")) {
						suffixLabel = "/"+suffixLabel;
					}
					labelCn += suffixLabel;
				}
				Record rd = new Record("ATTEMP_TRAPH");
				rd.addColValue("LABEL_CN", labelCn);
				rd.addColValue("NO", nName.getNo());
				rd.addColValue("TRAPH_RATE", nName.getRate());
				rd.addColValue("RELATED_A_END_STATION_CUID", nName.getaStationId());
				rd.addColValue("RELATED_Z_END_STATION_CUID", nName.getzStationId());
				if(nName.getaStationType().equals("SITE")) {
					rd.addColValue("RELATED_A_END_SITE_CUID", nName.getaStationId());
					rd.addColValue("END_STATION_TYPE_A", "SITE");
					rd.addColValue("ZD_SITE_TYPE_A", "SITE");
				}else if(nName.getaStationType().equals("DISTRICT")) {
					rd.addColValue("RELATED_A_END_SITE_CUID", null);
					rd.addColValue("END_STATION_TYPE_A", "DISTRICT");
				}
				if(nName.getzStationType().equals("SITE")) {
					rd.addColValue("RELATED_Z_END_SITE_CUID", nName.getzStationId());
					rd.addColValue("END_STATION_TYPE_Z", "SITE");
					rd.addColValue("ZD_SITE_TYPE_Z", "SITE");
				}else if(nName.getzStationType().equals("DISTRICT")) {
					rd.addColValue("RELATED_Z_END_SITE_CUID", null);
					rd.addColValue("END_STATION_TYPE_Z", "DISTRICT");
				}
				updateNameRdsMap.put(name.getCuid(), rd);
			}
		}
		
		List<Record> updatePks = new ArrayList<Record>();
		List<Record> updateRds = new ArrayList<Record>();
		for (IService service : serviceList) {
			AttempTraphService oldAttTraph = (AttempTraphService)service;
			String cuid = oldAttTraph.getCuid();
			AttempTraphService newAttTraph = newAttTraphMap.get(cuid);
			
			boolean needUpdate = false;
			Record rd = updateNameRdsMap.get(cuid);
			if(rd == null) {
				rd = new Record("ATTEMP_TRAPH");
			}else {
				needUpdate = true;
			}
			Map<String, Object> newDataMap = newAttTraph.getDataMap();
			//key包括：ACCESS_POINT_A,ACCESS_POINT_Z,END_SWITCH_DEV_A,END_SWITCH_DEV_Z,RELATED_A_SITE_CUID,RELATED_Z_SITE_CUID
			for(String key : newDataMap.keySet()) {
				Object obj = newDataMap.get(key);
				if(obj == null) {
					continue;
				}
				if(obj instanceof String) {
					String value = obj.toString();
					if(StringUtils.isBlank(value)) {
						continue;
					}
					if("ACCESS_POINT_A".equals(key)) {
						if(value.toString().startsWith("ACCESSPOINT")){
							rd.addColSqlValue("JHROOM_A", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+value+"'");//A端业务资源点
							rd.addColValue("JHROOM_ACUID", value);
						}else{
							rd.addColValue("JHROOM_A",value);
							rd.addColSqlValue("JHROOM_ACUID", "SELECT MAX(CUID) FROM ACCESSPOINT WHERE LABEL_CN='"+value+"'");
						}
						needUpdate = true;
					}else if("ACCESS_POINT_Z".equals(key)) {
						if(value.toString().startsWith("ACCESSPOINT")){
							rd.addColSqlValue("JHROOM_Z", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+value+"'");//Z端业务资源点
							rd.addColValue("JHROOM_ZCUID", value);
						}else{
							rd.addColValue("JHROOM_Z",value);
							rd.addColSqlValue("JHROOM_ZCUID", "SELECT MAX(CUID) FROM ACCESSPOINT WHERE LABEL_CN='"+value+"'");
						}
						needUpdate = true;
					}else if("END_SWITCH_DEV_A".equals(key)) {
						if(value.toString().startsWith("SWITCH_ELEMENT")){
							rd.addColSqlValue("END_SWITCH_DEV_A", "SELECT LABEL_CN FROM SWITCH_ELEMENT WHERE CUID='"+value+"'");//A端业务网元
							rd.addColValue("RELATED_A_END_SWITCHDEV_CUID", value);
						}else if(value.toString().startsWith("T_LOGIC")){
							rd.addColSqlValue("END_SWITCH_DEV_A", "SELECT LABEL_CN FROM T_LOGIC_DEVICE WHERE CUID='"+value+"'");//A端业务网元
							rd.addColValue("RELATED_A_END_SWITCHDEV_CUID", value);
						}else if(value.toString().startsWith("TRANS_ELEMENT")){
							rd.addColSqlValue("END_SWITCH_DEV_A", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID='"+value+"'");//A端业务网元
							rd.addColValue("RELATED_A_END_SWITCHDEV_CUID", value);
						}else {
							rd.addColValue("END_SWITCH_DEV_A", value);
						}
						needUpdate = true;
					}else if( "END_SWITCH_DEV_Z".equals(key)){
						if(value.toString().startsWith("SWITCH_ELEMENT")){
							rd.addColSqlValue("END_SWITCH_DEV_Z", "SELECT LABEL_CN FROM SWITCH_ELEMENT WHERE CUID='"+value+"'");//Z端业务网元
							rd.addColValue("RELATED_Z_END_SWITCHDEV_CUID", value);
						}else if(value.toString().startsWith("T_LOGIC")){
							rd.addColSqlValue("END_SWITCH_DEV_Z", "SELECT LABEL_CN FROM T_LOGIC_DEVICE WHERE CUID='"+value+"'");//Z端业务网元
							rd.addColValue("RELATED_Z_END_SWITCHDEV_CUID", value);
						}else if(value.toString().startsWith("TRANS_ELEMENT")){
							rd.addColSqlValue("END_SWITCH_DEV_Z", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID='"+value+"'");//A端业务网元
							rd.addColValue("RELATED_Z_END_SWITCHDEV_CUID", value);
						}else{
							rd.addColValue("END_SWITCH_DEV_Z", value);
						}
						needUpdate = true;
					}else if("END_SWITCHDEV_PORT_A".equals(key)) {
						if(value.toString().startsWith("SWITCH_PORT")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_A", "SELECT LABEL_CN FROM SWITCH_PORT WHERE CUID='"+value+"'");//A端业务端口
							rd.addColValue("RELATED_A_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("T_LOGIC_PORT")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_A", "SELECT LABEL_CN FROM T_LOGIC_PORT WHERE CUID='"+value+"'");//A端业务端口
							rd.addColValue("RELATED_A_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("PTP")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_A", "SELECT LABEL_CN FROM PTP WHERE CUID='"+value+"'");//A端传输端口
							rd.addColValue("RELATED_A_END_SWITCH_PORT_CUID",value);
						}else{
							rd.addColValue("END_SWITCHDEV_PORT_A",value);
						}
						needUpdate = true;
					}else if("END_SWITCHDEV_PORT_Z".equals(key)) {
						if(value.toString().startsWith("SWITCH_PORT")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_Z", "SELECT LABEL_CN FROM SWITCH_PORT WHERE CUID='"+value+"'");//Z端业务端口
							rd.addColValue("RELATED_Z_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("T_LOGIC_PORT")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_Z", "SELECT LABEL_CN FROM T_LOGIC_PORT WHERE CUID='"+value+"'");//Z端业务端口
							rd.addColValue("RELATED_Z_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("PTP")){
							rd.addColSqlValue("END_SWITCHDEV_PORT_Z", "SELECT LABEL_CN FROM PTP WHERE CUID='"+value+"'");//A端传输端口
							rd.addColValue("RELATED_Z_END_SWITCH_PORT_CUID",value);
						}else{
							rd.addColValue("END_SWITCHDEV_PORT_Z",value);
						}
						needUpdate = true;
					}else if("RELATED_A_SITE_CUID".equals(key)) {
						rd.addColValue(key, value); 
						rd.addColValue("RELATED_A_ZD_SITE_CUID", value);
						rd.addColSqlValue("RELATED_A_DISTRICT_CUID", "SELECT SUBSTR(RELATED_SPACE_CUID, 0, 26) FROM SITE WHERE CUID = '" + value + "'");
						rd.addColValue("RELATED_A_ZD_SITE_CUID", value); 
						needUpdate = true;
					}else if("RELATED_Z_SITE_CUID".equals(key)) {
						rd.addColValue(key, value); 
						rd.addColValue("RELATED_Z_ZD_SITE_CUID", value);
						rd.addColSqlValue("RELATED_Z_DISTRICT_CUID", "SELECT SUBSTR(RELATED_SPACE_CUID, 0, 26) FROM SITE WHERE CUID = '" + value + "'");
						rd.addColValue("RELATED_Z_ZD_SITE_CUID", value); 
						needUpdate = true;
					}
				}
			}
			//更新业务类型
			if(StringUtils.isNotBlank(newAttTraph.getExtIds()) && !newAttTraph.getExtIds().equals(oldAttTraph.getExtIds())) {
				needUpdate = true;
				//TODO 暂时不支持同步电路名称
				Record nRd = updateNameRdsMap.get(cuid);
				if(nRd == null) {
					List<TraphName> nNameList = TraphNameBO.getTraphNames(oldAttTraph.getAPointCuid(), oldAttTraph.getZPointCuid(), oldAttTraph.getRate(), 1);
					TraphName nName = nNameList.get(0);
					String labelCn = nName.getaStation() + "-" + nName.getzStation() + nName.getRateName() + StringHelper.changeIntToFixedLthStr(oldAttTraph.getNo(), 4);
		            labelCn = labelCn + this.TraphNameBO.getServiceName(newAttTraph.getExtIds());
		            rd.addColValue("LABEL_CN", labelCn);
				}
				rd.addColValue("EXT_IDS", ","+newAttTraph.getExtIds()+",");
			}
			//更新承载方式
			if(StringUtils.isNotBlank(newAttTraph.getExtTypes()) &&
					!newAttTraph.getExtTypes().equals(oldAttTraph.getExtTypes())) {
				needUpdate = true;
				rd.addColValue("EXT_TYPE", newAttTraph.getExtTypes());
			}
			
			if(StringUtils.isNotBlank(newAttTraph.getSuffixLabel())) {
				String labelCn = (String)rd.getValueMap().get("LABEL_CN");
				if(StringUtils.isBlank(labelCn)) {
					labelCn = oldAttTraph.getLabelCn();
				}
				String oldSuffix = "", newSuffix = newAttTraph.getSuffixLabel();
				for(String suffix : TraphConstants.suffixList) {
					if(labelCn.indexOf(suffix) != -1) {
						oldSuffix += suffix;
						labelCn = StringUtils.replace(labelCn, suffix, "");
					}
				}
				if(!oldSuffix.equals(newSuffix)) {
					if(!newSuffix.startsWith("/")) {
						newSuffix = "/"+newSuffix;
					}
					labelCn += newSuffix;
				}else {
					labelCn += oldSuffix;
				}
				rd.addColValue("LABEL_CN", labelCn);
				needUpdate = true;
			}
			/*if(delRouteServiceCuidList.contains(cuid)){
				rd.addColValue("PATHINFO", "");
				rd.addColValue("DESIGN_INFO", "");
				rd.addColValue("RELATED_A_PORT_CUID", "");
				rd.addColValue("RELATED_Z_PORT_CUID", "");
				rd.addColValue("RELATED_NE_A_CUID", "");
				rd.addColValue("RELATED_NE_Z_CUID", "");
				rd.addColValue("END_PORT_A", "");
				rd.addColValue("END_PORT_Z", "");
				rd.addColValue("ZJSITES", "");
				rd.addColValue("IS_WHOLE_ROUTE", 0);
				needUpdate = true;
				
				this.deleteDgnSegInfo(cuid);
		    	//删除路由设计表
		    	ServiceActionContext ac = null;
		    	List<String> TraphCuidList = new ArrayList<String>();
		    	TraphCuidList.add(cuid);
		    	this.releaseAttempTraphRelation(ac, TraphCuidList);
			}*/
			if(needUpdate) {
				Record pk = new Record("ATTEMP_TRAPH");
				pk.addColValue("CUID", cuid);
				updatePks.add(pk);
				updateRds.add(rd);
			}
		}
		this.IbatisResDAO.updateDynamicTableBatch(updateRds, updatePks);
	}
	
	/**
	 * 批量修改电路信息
	 * 
	 * @param list
	 */
	public void updateAttempTraphBatch(List<AttempTraphService> list) {
		if(list != null && !list.isEmpty()) {
			List<Record> rList = new ArrayList<Record>();
			List<Record> rPkList = new ArrayList<Record>();
			List<Record> extRecordList = new ArrayList<Record>();
			List<Record> extPkRecordList = new ArrayList<Record>();
			List<String> labelCnList = new ArrayList<String>();
			List<String> repeatList = new ArrayList<String>();
			List<TraphName> traphNameList = new ArrayList<TraphName>();
			
			List<String> modifyNameTrphCuids = new ArrayList<String>();
			for(AttempTraphService attempTraph : list) {
				Record r = new Record("ATTEMP_TRAPH");
				Record record = null;
				
				String cuid = attempTraph.getCuid();
				String labelCn = attempTraph.getLabelCn();
				if (StringUtils.isNotBlank(labelCn)) {
					if (labelCnList.contains(labelCn)) {
						repeatList.add("电路名称【" + labelCn + "】重复");
					} else {
						Integer traphRate = attempTraph.getRate();
						Integer no = attempTraph.getNo();
						String aEndStationCuid = attempTraph.getAPointCuid();
						String zEndStationCuid = attempTraph.getZPointCuid();
						
						if (StringUtils.isBlank(aEndStationCuid)) {
							throw new RuntimeException("电路A端通达地点不能为空！");
						}
						if (StringUtils.isBlank(zEndStationCuid)) {
							throw new RuntimeException("电路Z端通达地点不能为空！");
						}
						if (traphRate == null || traphRate == 0) {
							throw new RuntimeException("电路速率不能为空！");
						}
						
						traphNameList.add(new TraphName(cuid, labelCn, aEndStationCuid, zEndStationCuid, traphRate, no));
						labelCnList.add(labelCn);
						
						modifyNameTrphCuids.add(cuid);
						r.addColValue("LABEL_CN", labelCn);
						r.addColValue("TRAPH_RATE", traphRate);
						r.addColValue("NO", no);
						r.addColValue("RELATED_A_END_STATION_CUID", aEndStationCuid);
						
						String endStationTypeA = "SITE";
						if(aEndStationCuid.startsWith("DISTRICT")) {
							endStationTypeA = "DISTRICT";
						} else if(aEndStationCuid.startsWith("ACCESSPOINT")) {
							endStationTypeA = "ACCESSPOINT";
						} else if(aEndStationCuid.startsWith("SITE_RESOURCE")) {
							endStationTypeA = "ACCESSPOINT";
						} else if(aEndStationCuid.startsWith("ROOM")) {
							endStationTypeA = "ROOM";
						} else if(aEndStationCuid.startsWith("TRANS_ELEMENT")) {
							endStationTypeA = "TRANS_ELEMENT";
						}else if(aEndStationCuid.startsWith("SWITCH_ELEMENT")) {
							endStationTypeA = "SWITCHELEMENT";
						}else if(aEndStationCuid.startsWith("T_LOGIC")) {
							endStationTypeA = "T_LOGIC";
						}
						r.addColValue("END_STATION_TYPE_A", endStationTypeA);
						
						r.addColValue("RELATED_Z_END_STATION_CUID", zEndStationCuid);
						
						String endStationTypeZ = "SITE";
						if(zEndStationCuid.startsWith("DISTRICT")) {
							endStationTypeZ = "DISTRICT";
						} else if(zEndStationCuid.startsWith("ACCESSPOINT")) {
							endStationTypeZ = "ACCESSPOINT";
						} else if(zEndStationCuid.startsWith("SITE_RESOURCE")) {
							endStationTypeZ = "ACCESSPOINT";
						} else if(zEndStationCuid.startsWith("ROOM")) {
							endStationTypeZ = "ROOM";
						} else if(zEndStationCuid.startsWith("TRANS_ELEMENT")) {
							endStationTypeZ = "TRANS_ELEMENT";
						}else if(zEndStationCuid.startsWith("SWITCH_ELEMENT")) {
							endStationTypeZ = "SWITCHELEMENT";
						}else if(zEndStationCuid.startsWith("T_LOGIC")) {
							endStationTypeZ = "T_LOGIC";
						}
						r.addColValue("END_STATION_TYPE_Z", endStationTypeZ);
					}
				}
				
				Map<String, Object> dataMap = attempTraph.getDataMap();
				if(dataMap != null && !dataMap.isEmpty()) {
					dataMap.remove("EXT_IDS_NAME");
					dataMap.remove("SUFFIX");
					List<String> switchDdfPortList = new ArrayList<String>();
					List<String> switchDevList = new ArrayList<String>();
					List<String> switchPortList = new ArrayList<String>();
					List<String> switchRoomList = new ArrayList<String>();
                    for (Object key : dataMap.keySet()) {
						if(key.toString().equals("END_SWITCH_DF_PORT_A")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchDdfPortList.contains(dataMap.get(key))){
								switchDdfPortList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("END_SWITCH_DF_PORT_Z")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchDdfPortList.contains(dataMap.get(key))){
								switchDdfPortList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("END_SWITCH_DEV_A")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchDevList.contains(dataMap.get(key))){
								switchDevList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("END_SWITCH_DEV_Z")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchDevList.contains(dataMap.get(key))){
								switchDevList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("JHROOM_A")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchRoomList.contains(dataMap.get(key))){
								switchRoomList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("JHROOM_Z")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchRoomList.contains(dataMap.get(key))){
								switchRoomList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("END_SWITCHDEV_PORT_A")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchPortList.contains(dataMap.get(key))){
								switchPortList.add(dataMap.get(key).toString());
							}
						}else if(key.toString().equals("END_SWITCHDEV_PORT_Z")&&StringUtils.isNotEmpty((String)dataMap.get(key))){
							if(!switchPortList.contains(dataMap.get(key))){
								switchPortList.add(dataMap.get(key).toString());
							}
						}
					}
                    
                    Map mp = new HashMap();
                    List<Map<String,Object>> switchDdfPortMapList = new ArrayList<Map<String,Object>>();
                    List<Map<String,Object>> switchDevMapList = new ArrayList<Map<String,Object>>();
                    List<Map<String,Object>> switchRoomMapList = new ArrayList<Map<String,Object>>();
                    List<Map<String,Object>> switchPortMapList = new ArrayList<Map<String,Object>>();
                    Map<String,Map<String,Object>> switchDdfPortMapListMap = new HashMap<String,Map<String,Object>>();
                    Map<String,Map<String,Object>> switchDevMapListMap = new HashMap<String,Map<String,Object>>();
                    Map<String,Map<String,Object>> switchRoomMapListMap = new HashMap<String,Map<String,Object>>();
                    Map<String,Map<String,Object>> switchPortMapListMap = new HashMap<String,Map<String,Object>>();
                    if(switchDdfPortList!=null && switchDdfPortList.size()>0){
                    	mp.put("switchDdfPortList", switchDdfPortList);
                    	switchDdfPortMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".quarySwitchDdfPort", mp);
                    }
                    if(switchDdfPortMapList!=null && switchDdfPortMapList.size()>0){
                    	switchDdfPortMapListMap = IbatisDAOHelper.parseList2Map(switchDdfPortMapList, "LABEL_CN");
                    }
                    if(switchDevList!=null && switchDevList.size()>0){
                    	mp.put("switchDevList", switchDevList);
                    	switchDevMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".quarySwitchDev", mp);
                    }
                    if(switchDevMapList!=null && switchDevMapList.size()>0){
                    	switchDevMapListMap = IbatisDAOHelper.parseList2Map(switchDevMapList, "LABEL_CN");
                    }
                    if(switchRoomList!=null && switchRoomList.size()>0){
                    	mp.put("switchRoomList", switchRoomList);
                    	switchRoomMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".quarySwitchRoom", mp);
                    }
                    if(switchRoomMapList!=null && switchRoomMapList.size()>0){
                    	switchRoomMapListMap = IbatisDAOHelper.parseList2Map(switchRoomMapList, "LABEL_CN");
                    }
                    if(switchPortList!=null && switchPortList.size()>0){
                    	mp.put("switchPortList", switchPortList);
                    	switchPortMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".quarySwitchPort", mp);
                    }
                    if(switchPortMapList!=null && switchPortMapList.size()>0){
                    	switchPortMapListMap = IbatisDAOHelper.parseList2Map(switchPortMapList, "LABEL_CN");
                    }
                    
					for (Object key : dataMap.keySet()) {
						if(key.toString().equals("END_SWITCH_DF_PORT_A")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchDdfPortMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_DF_PORT_A_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_DF_PORT_A_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_DF_PORT_A_CUID", "");
							}
						}else if(key.toString().equals("END_SWITCH_DF_PORT_Z")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchDdfPortMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_DF_PORT_Z_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_DF_PORT_Z_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_DF_PORT_Z_CUID", "");
							}
						}else if(key.toString().equals("END_SWITCH_DEV_A")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchDevMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_A_END_SWITCHDEV_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_A_END_SWITCHDEV_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_A_END_SWITCHDEV_CUID", "");
							}
						}else if(key.toString().equals("END_SWITCH_DEV_Z")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchDevMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_Z_END_SWITCHDEV_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_Z_END_SWITCHDEV_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_Z_END_SWITCHDEV_CUID", "");
							}
						}else if(key.toString().equals("JHROOM_A")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchRoomMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("JHROOM_ACUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("JHROOM_ACUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("JHROOM_ACUID", "");
							}
						}else if(key.toString().equals("JHROOM_Z")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchRoomMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("JHROOM_ZCUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("JHROOM_ZCUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("JHROOM_ZCUID", "");
							}
						}else if(key.toString().equals("END_SWITCHDEV_PORT_A")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchPortMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_A_END_SWITCH_PORT_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_A_END_SWITCH_PORT_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_A_END_SWITCH_PORT_CUID", "");
							}
						}else if(key.toString().equals("END_SWITCHDEV_PORT_Z")){
							r.addColValue(key.toString(), dataMap.get(key));
							if(StringUtils.isNotEmpty((String)dataMap.get(key))){
								Map newMap = switchPortMapListMap.get((String)dataMap.get(key));
								if(getFlag()){
									if(newMap==null){
										throw new RuntimeException("数据不存在！");
									}else{
										r.addColValue("RELATED_Z_END_SWITCH_PORT_CUID", newMap.get("CUID"));
									}
								}else{
									if(newMap!=null){
										r.addColValue("RELATED_Z_END_SWITCH_PORT_CUID", newMap.get("CUID"));
									}
								}
							}else{
								r.addColValue("RELATED_Z_END_SWITCH_PORT_CUID", "");
							}
						}
					}
					dataMap.remove("END_SWITCH_DF_PORT_A");
					dataMap.remove("END_SWITCH_DF_PORT_Z");
					dataMap.remove("END_SWITCH_DEV_A");
					dataMap.remove("END_SWITCH_DEV_Z");
					dataMap.remove("JHROOM_A");
					dataMap.remove("JHROOM_Z");
					dataMap.remove("END_SWITCHDEV_PORT_A");
					dataMap.remove("END_SWITCHDEV_PORT_Z");
					
					for (Object key : dataMap.keySet()) {
						r.addColValue(key.toString(), dataMap.get(key));
					}
				}
				rList.add(r);
				
				Record rpk = new Record("ATTEMP_TRAPH");
				rpk.addColValue("CUID", cuid);
				rPkList.add(rpk);
				if(record!=null){
					extRecordList.add(record);
					Record extPk = new Record("T_EXT_ATTEMP_TRAPH");
					extPk.addColValue("CUID", cuid);
					extPkRecordList.add(extPk);
				}
				
			}
			//先把电路名称改成临时的
			this.updateTraphLabelCnsTemp(modifyNameTrphCuids);
			// 校验电路名称是否可用
			List<String> checkResult = TraphNameBO.validateTraphLabelCnsUniqu(traphNameList);
			if (!checkResult.isEmpty() || !repeatList.isEmpty()) {
				String errorInfo = "";
				
				List<String> errorInfoList = new ArrayList<String>();
				if (checkResult != null && !checkResult.isEmpty()) {
					errorInfoList.addAll(checkResult);
				}
				if (repeatList != null && !repeatList.isEmpty()) {
					errorInfoList.addAll(repeatList);
				}
				
				errorInfo = StringUtils.join(errorInfoList, "\n");
				errorInfo += "\n请修改名称后再提交！";
				
				throw new RuntimeException(errorInfo);
			}
			
			this.IbatisResDAO.updateDynamicTableBatch(rList, rPkList);
			if(extRecordList != null && extRecordList.size()>0){
				this.IbatisResDAO.updateDynamicTableBatch(extRecordList, extPkRecordList);
			}
		}
	}
	
	private void updateTraphLabelCnsTemp(List<String> traphCuids){
		Map pm = new HashMap();
		pm.put("cuids", traphCuids);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateTraphLabelCn2Temp", pm);
	}
	
	/**
	 * 修改电路信息
	 * @param data
	 */
	public void updateAttempTraph(AttempTraphService attempTraph) {
		if(attempTraph != null) {
			String cuid = attempTraph.getCuid();
			String labelCn = attempTraph.getLabelCn();
			Integer traphRate = attempTraph.getRate();
			Integer no = attempTraph.getNo();
			String aEndStationCuid = attempTraph.getAPointCuid();
			String zEndStationCuid = attempTraph.getZPointCuid();
			String extids = attempTraph.getExtIds();
			
			// 字段校验
			if (StringUtils.isBlank(aEndStationCuid)) {
				throw new RuntimeException("电路A端通达地点不能为空！");
			}
			if (StringUtils.isBlank(zEndStationCuid)) {
				throw new RuntimeException("电路Z端通达地点不能为空！");
			}
			if (no == null) {
				throw new RuntimeException("电路编号不能为空！");
			}
			if (traphRate == null || traphRate == 0) {
				throw new RuntimeException("电路速率不能为空！");
			}
			if (StringUtils.isBlank(labelCn)) {
				throw new RuntimeException("电路名称不能为空！");
			}
		    if (StringUtils.isBlank(extids)) {
		          throw new RuntimeException("电路业务类型不能为空！");
		    }
		    
//		    boolean hasChangeAZflag = this.updateRouteAndDegInfo(attempTraph);
			List<TraphName> traphNameList = new ArrayList<TraphName>();
			TraphName traphName = new TraphName(cuid, labelCn, aEndStationCuid, zEndStationCuid, traphRate, no, extids);
			traphNameList.add(traphName);
			List<String> resultList = TraphNameBO.validateTraphLabelCnsUniqu(traphNameList);
			if (resultList != null && !resultList.isEmpty()) {
				String errorInfo = StringUtils.join(resultList, "\n");
				errorInfo += "\n请修改名称后再提交！";
				
				throw new RuntimeException(errorInfo);
			}
			
			Record pk = new Record("ATTEMP_TRAPH");
			pk.addColValue("CUID", cuid);
			
			Record pkExtend = new Record("T_EXT_ATTEMP_TRAPH");
			Record recordExtend = new Record("T_EXT_ATTEMP_TRAPH");
			pkExtend.addColValue("CUID", cuid);
			
			Record record = new Record("ATTEMP_TRAPH");
			record.addColValue("LABEL_CN", labelCn);
			record.addColValue("TRAPH_RATE", traphRate);
			record.addColValue("NO", no);
			record.addColValue("RELATED_A_END_STATION_CUID", aEndStationCuid);

			String endStationTypeA = "SITE";
			if(aEndStationCuid.startsWith("DISTRICT")) {
				endStationTypeA = "DISTRICT";
			} else if(aEndStationCuid.startsWith("ROOM")) {
				endStationTypeA = "ROOM";
			} else if(aEndStationCuid.startsWith("ACCESSPOINT")) {
				endStationTypeA = "ACCESSPOINT";
			} else if(aEndStationCuid.startsWith("SITE_RESOURCE")) {
				endStationTypeA = "ACCESSPOINT";
			} else if(aEndStationCuid.startsWith("TRANS_ELEMENT")) {
				endStationTypeA = "TRANS_ELEMENT";
			}else if(aEndStationCuid.startsWith("SWITCH_ELEMENT")) {
				endStationTypeA = "SWITCHELEMENT";
			}else if(aEndStationCuid.startsWith("T_LOGIC")) {
				endStationTypeA = "T_LOGIC";
			}
			record.addColValue("END_STATION_TYPE_A", endStationTypeA);
			
			record.addColValue("RELATED_Z_END_STATION_CUID", zEndStationCuid);
			
			String endStationTypeZ = "SITE";
			if(zEndStationCuid.startsWith("DISTRICT")) {
				endStationTypeZ = "DISTRICT";
			} else if(zEndStationCuid.startsWith("ACCESSPOINT")) {
				endStationTypeZ = "ACCESSPOINT";
			} else if(zEndStationCuid.startsWith("SITE_RESOURCE")) {
				endStationTypeZ = "ACCESSPOINT";
			} else if(zEndStationCuid.startsWith("ROOM")) {
				endStationTypeZ = "ROOM";
			} else if(zEndStationCuid.startsWith("TRANS_ELEMENT")) {
				endStationTypeZ = "TRANS_ELEMENT";
			}else if(zEndStationCuid.startsWith("SWITCH_ELEMENT")) {
				endStationTypeZ = "SWITCHELEMENT";
			}else if(zEndStationCuid.startsWith("T_LOGIC")) {
				endStationTypeZ = "T_LOGIC";
			}
			record.addColValue("END_STATION_TYPE_Z", endStationTypeZ);
			
			Map<String, Object> dataMap = attempTraph.getDataMap();
			dataMap.put("EXT_IDS", ","+dataMap.get("EXT_IDS")+",");
			if(dataMap != null && !dataMap.isEmpty()) {
				String extIds = IbatisDAOHelper.getStringValue(dataMap, "EXT_IDS");
				String[] exts = StringUtils.split(extIds, ",");
				String ext = "0";
				if (exts != null && exts.length > 0) {
					ext = exts[0];
				}
				dataMap.put("EXT", ext);
				dataMap.put("LAST_MODIFY_TIME", new Date());
				
				for (String key : dataMap.keySet()) {
					Object obj = dataMap.get(key);
					if(obj == null) {
						continue;
					}
					String value="";
					if(obj instanceof String) {
					    value = obj.toString();
						if(StringUtils.isBlank(value)) {
							continue;
						}
					}
					if("END_SWITCH_DEV_A".equals(key)) {
						if (value.toString().startsWith("SWITCH_ELEMENT")){
							record.addColSqlValue("END_SWITCH_DEV_A", "SELECT LABEL_CN FROM SWITCH_ELEMENT WHERE CUID='"+value+"'");//A端业务网元
							record.addColValue("RELATED_A_END_SWITCHDEV_CUID", value);
						}else if (value.toString().startsWith("T_LOGIC")){
							record.addColSqlValue("END_SWITCH_DEV_A", "SELECT LABEL_CN FROM T_LOGIC_DEVICE WHERE CUID='"+value+"'");//A端业务网元
							record.addColValue("RELATED_A_END_SWITCHDEV_CUID", value);
						}else{
							record.addColValue("END_SWITCH_DEV_A", value);
							record.addColSqlValue("RELATED_A_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM SWITCH_ELEMENT WHERE LABEL_CN='"+value+"'");
						}
					}else if( "END_SWITCH_DEV_Z".equals(key)){
						if (value.toString().startsWith("SWITCH_ELEMENT")){
							record.addColSqlValue("END_SWITCH_DEV_Z", "SELECT LABEL_CN FROM SWITCH_ELEMENT WHERE CUID='"+value+"'");//z端业务网元
							record.addColValue("RELATED_Z_END_SWITCHDEV_CUID", value);
						}else if (value.toString().startsWith("T_LOGIC")){
							record.addColSqlValue("END_SWITCH_DEV_Z", "SELECT LABEL_CN FROM T_LOGIC_DEVICE WHERE CUID='"+value+"'");//z端业务网元
							record.addColValue("RELATED_Z_END_SWITCHDEV_CUID", value);
						}else{
							record.addColValue("END_SWITCH_DEV_Z", value);
							record.addColSqlValue("RELATED_Z_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM SWITCH_ELEMENT WHERE LABEL_CN='"+value+"'");
						}
					}else if("END_SWITCHDEV_PORT_A".equals(key)) {
						if(value.toString().startsWith("SWITCH_PORT")){
							record.addColSqlValue("END_SWITCHDEV_PORT_A", "SELECT LABEL_CN FROM SWITCH_PORT WHERE CUID='"+value+"'");//A端业务端口
							record.addColValue("RELATED_A_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("T_LOGIC_PORT")){
							record.addColSqlValue("END_SWITCHDEV_PORT_A", "SELECT LABEL_CN FROM T_LOGIC_PORT WHERE CUID='"+value+"'");//A端业务端口
							record.addColValue("RELATED_A_END_SWITCH_PORT_CUID",value);
						}else{
							record.addColValue("END_SWITCHDEV_PORT_A",value);
							record.addColSqlValue("RELATED_A_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM SWITCH_PORT WHERE LABEL_CN='"+value+"'");//A端业务端口
						}
					}else if("END_SWITCHDEV_PORT_Z".equals(key)) {
						if(value.toString().startsWith("SWITCH_PORT")){
							record.addColSqlValue("END_SWITCHDEV_PORT_Z", "SELECT LABEL_CN FROM SWITCH_PORT WHERE CUID='"+value+"'");//A端业务端口
							record.addColValue("RELATED_Z_END_SWITCH_PORT_CUID",value);
						}else if(value.toString().startsWith("T_LOGIC_PORT")){
							record.addColSqlValue("END_SWITCHDEV_PORT_Z", "SELECT LABEL_CN FROM T_LOGIC_PORT WHERE CUID='"+value+"'");//A端业务端口
							record.addColValue("RELATED_Z_END_SWITCH_PORT_CUID",value);
						}else{
							record.addColValue("END_SWITCHDEV_PORT_Z",value);
							record.addColSqlValue("RELATED_Z_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM SWITCH_PORT WHERE LABEL_CN='"+value+"'");//A端业务端口
						}
					}else if("JHROOM_A".equals(key)) {
						record.addColValue("JHROOM_ACUID",value);
						record.addColSqlValue("JHROOM_A", "SELECT LABEL_CN FROM ROOM WHERE CUID='"+value+"'");//A端业务机房
					}else if("JHROOM_Z".equals(key)) {
						record.addColValue("JHROOM_ZCUID",value);
						record.addColSqlValue("JHROOM_Z", "SELECT LABEL_CN FROM ROOM WHERE CUID='"+value+"'");//Z端业务机房
					}else if("ACCESS_POINT_A".equals(key)) {
						if(value.toString().startsWith("ACCESSPOINT")){
							record.addColSqlValue("JHROOM_A", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+value+"'");//A端业务资源点
							record.addColValue("JHROOM_ACUID", value);
						}else{
							record.addColValue("JHROOM_A",value);
							record.addColSqlValue("JHROOM_ACUID", "SELECT MAX(CUID) FROM ACCESSPOINT WHERE LABEL_CN='"+value+"'");
						}
					}else if("ACCESS_POINT_Z".equals(key)) {
						if(value.toString().startsWith("ACCESSPOINT")){
							record.addColSqlValue("JHROOM_Z", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+value+"'");//Z端业务资源点
							record.addColValue("JHROOM_ZCUID", value);
						}else{
							record.addColValue("JHROOM_Z",value);
							record.addColSqlValue("JHROOM_ZCUID", "SELECT MAX(CUID) FROM ACCESSPOINT WHERE LABEL_CN='"+value+"'");
						}
					}else if("CUSTOM_A_NAME".equals(key)) {
						recordExtend.addColValue("CUSTOM_A_NAME", value);
					}else if("CUSTOM_Z_NAME".equals(key)) {
						recordExtend.addColValue("CUSTOM_Z_NAME", value);
					}else if("CUSTOM_PHONE_A".equals(key)) {
						recordExtend.addColValue("CUSTOM_PHONE_A", value);
					}else if("CUSTOM_PHONE_Z".equals(key)) {
						recordExtend.addColValue("CUSTOM_PHONE_Z", value);
					}else if("RELATED_A_ZD_SITE_CUID".equals(key)){
						if(value.toString().startsWith("ACCESSPOINT")||
								value.toString().startsWith("SITE_RESOURCE")){
							record.addColValue("ZD_SITE_TYPE_A", "ACCESSPOINT");
							record.addColValue("RELATED_A_ZD_SITE_CUID", value);
						}else if(value.toString().startsWith("SITE")){
							record.addColValue("ZD_SITE_TYPE_A", "SITE");
							record.addColValue("RELATED_A_ZD_SITE_CUID", value);
						}
					}else if("RELATED_Z_ZD_SITE_CUID".equals(key)){
						if(value.toString().startsWith("ACCESSPOINT")||
								value.toString().startsWith("SITE_RESOURCE")){
							record.addColValue("ZD_SITE_TYPE_Z", "ACCESSPOINT");
							record.addColValue("RELATED_Z_ZD_SITE_CUID", value);
						}else if(value.toString().startsWith("SITE")){
						record.addColValue("ZD_SITE_TYPE_Z", "SITE");
						record.addColValue("RELATED_Z_ZD_SITE_CUID", value);
						}
					}else if("RELATED_TRAPHGROUP_CUID".equals(key)){
						
					}else if("RELATED_USER_CUID".equals(key)){
						if(value.toString().startsWith("VP")){
							record.addColValue("RELATED_USER_CUID", value);
							record.addColSqlValue("CUSTOMER_TRAPH_NAME", "SELECT LABEL_CN FROM VP WHERE CUID = '"+value.toString()+"'");
							record.addColSqlValue("USER_NAME", "SELECT LABEL_CN FROM VP WHERE CUID = '"+value.toString()+"'");
						}else{
							record.addColSqlValue("RELATED_USER_CUID", "SELECT CUID FROM VP WHERE LABEL_CN = '"+value.toString()+"'");
							record.addColValue("CUSTOMER_TRAPH_NAME", value);
							record.addColValue("USER_NAME", value);
						}
					}else{
						record.addColValue(key, dataMap.get(key));
					}
				}
				
				record.remove("OBJECTID");
				record.remove("TASK_ID");
			}
			
			/*if(hasChangeAZflag){
				record.addColValue("PATHINFO", "");
				record.addColValue("DESIGN_INFO", "");
				record.addColValue("RELATED_A_PORT_CUID", "");
				record.addColValue("RELATED_Z_PORT_CUID", "");
				record.addColValue("RELATED_NE_A_CUID", "");
				record.addColValue("RELATED_NE_Z_CUID", "");
				record.addColValue("END_PORT_A", "");
				record.addColValue("END_PORT_Z", "");
				record.addColValue("ZJSITES", "");
				record.addColValue("IS_WHOLE_ROUTE", 0);
			}*/
			
			this.IbatisResDAO.updateDynamicTable(record, pk);
			if (recordExtend.getValues().size()>0){
				this.IbatisResDAO.updateDynamicTable(recordExtend, pkExtend);
			}
			List<String> attempTraphCuidList = new ArrayList<String>();
			attempTraphCuidList.add(cuid);
			Map mp = new HashMap();
			mp.put("attempTraphCuidList", attempTraphCuidList);
			List<Map<String,Object>> attempTraphGroupList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".findAttempTraphGroupRelList",mp);
			String attempTraphGroupCuid = IbatisDAOHelper.getStringValue(dataMap, "RELATED_TRAPHGROUP_CUID");
			Record rd = new Record("T_ATTEMP_SERVICE_GROUP_ROUTE");
			rd.addColValue("RELATED_SERVICE_CUID", cuid);
			if (attempTraphGroupList != null && attempTraphGroupList.size()>0){
				if(StringUtils.isNotBlank(attempTraphGroupCuid)){
					Record rdPk = new Record("T_ATTEMP_SERVICE_GROUP_ROUTE");
					if (attempTraphGroupCuid.startsWith("T_SERVICE_GROUP-")){
						rdPk.addColValue("RELATED_GROUP_CUID", attempTraphGroupCuid);
					}else{
						rdPk.addColSqlValue("RELATED_GROUP_CUID", "SELECT CUID FROM T_SERVICE_GROUP WHERE LABEL_CN = '"+attempTraphGroupCuid+"'");
					}
					
					this.IbatisResDAO.updateDynamicTable(rdPk, rd);
				}else{
					this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTraphGroupRelList", mp);
				}
			}else{
				if(StringUtils.isNotBlank(attempTraphGroupCuid)){
					rd.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_SERVICE_GROUP_ROUTE"));
					rd.addColValue("RELATED_GROUP_CUID", attempTraphGroupCuid);
					rd.addColValue("TYPES", 1);
					this.IbatisResDAO.insertDynamicTable(rd);
				}
			}
		}
	}
	
	/**
	 * 设置默认施工角色，根据是否是联通调度，调用不同的设置默认角色方法
	 * @author: zhangliang
	 * @param task
	 * @param attempTraphCuidList
	 */
	private void setDefaultRole(TaskInst task, List<String> attempTraphCuidList){
		if(SheetConstants.SHEET_CODE_UNICOMTRAPH.equals(task.getSheetInst().getSheetCfg().getSheetCode())){
			//设置联通调度默认主调、辅调
			this.setUDefaultConstructRole(attempTraphCuidList);
		}else{
			//设置默认施工角色
			this.setDefaultConstructRole(attempTraphCuidList);
		}
	}
	/**
	 * 设置service的所属工单id
	 * @param attempTraphCuidList
	 * @param sheetId
	 */
	public void setServicesSheetId(List<String> attempTraphCuidList,String sheetId){
		Map pm = new HashMap();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		pm.put("sheetId", sheetId);
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateServicesSheetId", pm);
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
		//LTE 施工判断
		List<Map<String, Object>> lteList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getLtebyattempTraphCuidList", pm);
		String isLte ="0";
		boolean isHenan = false;
		if(SysProperty.getInstance().getValue("district").trim().equals("DISTRICT-00001-00008")){
			isHenan = true;
		}
		for (Map<String, Object> m : lteList) {
			isLte =  StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "EXT_IDS"));
		}
		String[] exts = StringUtils.split(isLte, ",");
		String ext = "0";
		if (exts != null && exts.length > 0) {
			for(int i = 0;i<exts.length;i++){
				if(exts[i].equals("101")){
					ext="101";
				}else if(exts[i].equals("102")){
					ext="101";
				}else if(exts[i].equals("103")){
					ext="101";
				}
			}
		}
	
		Map<String, List<RoleVO>> roleMap = new HashMap<String, List<RoleVO>>();
		for (Map<String, Object> m : list) {
			String aSiteCuid=IbatisDAOHelper.getStringValue(m, "RELATED_A_SITE_CUID");
			String zSiteCuid=IbatisDAOHelper.getStringValue(m, "RELATED_Z_SITE_CUID");
			String tdInfoId = IbatisDAOHelper.getStringValue(m, "RELATED_TASK2SERVICE_CUID");
			String endSwitchDevZ = IbatisDAOHelper.getStringValue(m, "END_SWITCH_DEV_Z");
			String endSwitchDevA = IbatisDAOHelper.getStringValue(m, "END_SWITCH_DEV_A");
			String aDistrictOfSite = IbatisDAOHelper.getStringValue(m, "DISTRICT_A_SITE_CUID");
			String zDistrictOfSite = IbatisDAOHelper.getStringValue(m, "DISTRICT_Z_SITE_CUID");
			String aRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_A_ROLE_CUID"));
			String zRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_Z_ROLE_CUID"));
			if(aSiteCuid==null||aSiteCuid.equals("")||zSiteCuid==null||zSiteCuid.equals("")){
				continue;
			}
			
			//验证施工角色是否正确,若不正确，则重新设置
			boolean isWrongConstructRole = true;
			if(aDistrictOfSite!=null&&aDistrictOfSite.trim().length()>0){
				String aDistrictS = aDistrictOfSite.trim().length() > 26?aDistrictOfSite.substring(0,26):aDistrictOfSite;
				if(!aRoleId.contains(aDistrictS)){
					pm.put("distritCuid", aDistrictS);
					List<Map<String, Object>> aList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getDefaultConstructRole", pm);
					Map<String,Object> aMap = aList.get(0);
					aRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(aMap, "ROLE_CUID"));
				}
			}
			if(zDistrictOfSite!=null&&zDistrictOfSite.trim().length()>0){
				String zDistrictS = zDistrictOfSite.trim().length() > 26?zDistrictOfSite.substring(0,26):zDistrictOfSite;
				if(!zRoleId.contains(zDistrictS)){
					pm.put("distritCuid", zDistrictS);
					List<Map<String, Object>> zList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getDefaultConstructRole", pm);
					Map<String,Object> zMap = zList.get(0);
					zRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(zMap, "ROLE_CUID"));
				}
			}
			
			if (!existsRoleInfoLinkCuidList.contains(tdInfoId)) {
				this.IbatisResDAO.deleteSql(String.format(delSql, tdInfoId));
				

				if ((ext.equals("101") || ext.equals("102")) && !zRoleId.equals("")){
					String roleId = zRoleId;
					if(isHenan){
						if(endSwitchDevA!=null &&endSwitchDevA.trim().length()>0 && endSwitchDevA.contains("NODEB")){
							roleId = aRoleId;
						}
					}
					List<RoleVO> roleVOList = roleMap.get(tdInfoId);
					if(roleVOList == null) {
						roleVOList = new ArrayList<RoleVO>();
						roleMap.put(tdInfoId, roleVOList);
					}
					RoleVO roleVO = new RoleVO();
					roleVO.setType(RoleVO.TYPE_ROLE);
					roleVO.setValue(roleId);
					roleVOList.add(roleVO);
				} else {
				// 如果电路两端角色不同，则为二干调单，默认施工角色为两地市,RoleId长度30为电路二干施工，34为光路二干施工
				if (!aRoleId.equals(zRoleId)&&(aRoleId.length()==36||aRoleId.length()==34)&&(zRoleId.length()==36||zRoleId.length()==34)){
					logger.info("进入默认分配二干施工");
					List<RoleVO> roleVOList = roleMap.get(tdInfoId);
					if(roleVOList == null) {
						roleVOList = new ArrayList<RoleVO>();
						roleMap.put(tdInfoId, roleVOList);
					}
					RoleVO roleAVO = new RoleVO();
					roleAVO.setType(RoleVO.TYPE_ROLE);
					roleAVO.setValue(aRoleId);
					roleVOList.add(roleAVO);
					RoleVO roleZVO = new RoleVO();
					roleZVO.setType(RoleVO.TYPE_ROLE);
					roleZVO.setValue(zRoleId);
					roleVOList.add(roleZVO);
					continue;
				}
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
		}

		TraphDispatchProBO.setTaskRoles(roleMap, "constructTask");
	}
	
	private void setUDefaultConstructRole(List<String> attempTraphCuidList){
		String delSql = "DELETE FROM T_TASK_TO_SERVICE_LINK WHERE RELATED_TASK2SERVICE_CUID = '%s' AND LINK_TYPE IN('mainDispatch','assortDispatch')";
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".queryUDefaultConstructRole", pm);
		List<String> existsRoleInfoLinkCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".validateExistsRoleInfoLink", pm);
		
		Map<String, List<RoleVO>> aroleMap = new HashMap<String, List<RoleVO>>();
		Map<String, List<RoleVO>> zroleMap = new HashMap<String, List<RoleVO>>();
		for (Map<String, Object> m : list) {
			String tdInfoId = IbatisDAOHelper.getStringValue(m, "RELATED_TASK2SERVICE_CUID");
			if (!existsRoleInfoLinkCuidList.contains(tdInfoId)) {
				String aRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_A_ROLE_CUID"));
				String zRoleId = StringUtils.trimToEmpty(IbatisDAOHelper.getStringValue(m, "RELATED_Z_ROLE_CUID"));
				this.IbatisResDAO.deleteSql(String.format(delSql, tdInfoId));
				if (StringUtils.isNotEmpty(aRoleId)) {
					List<RoleVO> aroleVOList = aroleMap.get(tdInfoId);
					if(aroleVOList == null) {
						aroleVOList = new ArrayList<RoleVO>();
						aroleMap.put(tdInfoId, aroleVOList);
					}
					RoleVO roleVO = new RoleVO();
					roleVO.setType(RoleVO.TYPE_ROLE);
					roleVO.setValue(aRoleId);
					aroleVOList.add(roleVO);
				}
				
				if (StringUtils.isNotEmpty(zRoleId)) {
					List<RoleVO> zroleVOList = zroleMap.get(tdInfoId);
					if(zroleVOList == null) {
						zroleVOList = new ArrayList<RoleVO>();
						zroleMap.put(tdInfoId, zroleVOList);
					}
					RoleVO roleVO = new RoleVO();
					roleVO.setType(RoleVO.TYPE_ROLE);
					roleVO.setValue(zRoleId);
					zroleVOList.add(roleVO);
				}
				
			}
		}

		TraphDispatchProBO.setTaskRoles(aroleMap, "mainDispatch");
		TraphDispatchProBO.setTaskRoles(zroleMap, "assortDispatch");
	}
	
	/**
	 * 写入电路日志表
	 * @param attempLog
	 */
	protected void writeServiceLog(AttempLog attempLog) {
		if(attempLog.isWriteLog()) {
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphLog", attempLog);
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphLogExtend", attempLog);
			logger.info("写入【"+attempLog.getActionCodeName()+"】的日志");
		}
	}
	
	/**
	 * 根据调度电路ID，查询调度和调整前的端口，存入屏蔽告警表
	 * @param sheet
	 * @param attempTraphCuidList
	 * @return
	 */
	public String writeAlarmTask(TaskInst task, List<String> attempTraphCuidList) {
		List<String> traphCuidList = new ArrayList<String>();
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(attempTraphCuidList);
		for(Map<String, Object> map : attempTraphList) {
			String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
			if(StringUtils.isNotBlank(relatedTraphCuid)) traphCuidList.add(relatedTraphCuid);
		}
		
		List<Map<String, Object>> transPtpList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> ptnPtpList = new ArrayList<Map<String,Object>>();
		if(!traphCuidList.isEmpty()) {
			transPtpList = ResTraphBO.findTransPtpByTraph(traphCuidList);
			ptnPtpList = ResTraphBO.findPtnPtpByTraph(traphCuidList);
		}
		
		List<Map<String, Object>> attempTransPtpList = this.findAttempTransPtpByAttempTraph(attempTraphCuidList);
		List<Map<String, Object>> attempPtnPtpList = this.findAttempPtnPtpByAttempTraph(attempTraphCuidList);
		
		List<String> neNameList = new ArrayList<String>();
		for(Map<String, Object> map : attempTransPtpList) {
			String aNeName = IbatisDAOHelper.getStringValue(map, "RELATED_A_NE");
			String zNeName = IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE");
			
			if(StringUtils.isNotBlank(aNeName)) neNameList.add(aNeName);
			if(StringUtils.isNotBlank(zNeName)) neNameList.add(zNeName);
		}
		
		for(Map<String, Object> map : attempPtnPtpList) {
			String aNeName = IbatisDAOHelper.getStringValue(map, "RELATED_A_NE");
			String zNeName = IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE");
			
			if(StringUtils.isNotBlank(aNeName)) neNameList.add(aNeName);
			if(StringUtils.isNotBlank(zNeName)) neNameList.add(zNeName);
		}
		
		for(Map<String, Object> map : transPtpList) {
			String aNeName = IbatisDAOHelper.getStringValue(map, "RELATED_A_NE");
			String zNeName = IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE");
			
			if(StringUtils.isNotBlank(aNeName)) neNameList.add(aNeName);
			if(StringUtils.isNotBlank(zNeName)) neNameList.add(zNeName);
		}
		
		for(Map<String, Object> map : ptnPtpList) {
			String aNeName = IbatisDAOHelper.getStringValue(map, "RELATED_A_NE");
			String zNeName = IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE");
			
			if(StringUtils.isNotBlank(aNeName)) neNameList.add(aNeName);
			if(StringUtils.isNotBlank(zNeName)) neNameList.add(zNeName);
		}
		
		String neInfo = StringUtils.join(neNameList, "，");
		neInfo = StringUtils.substring(neInfo, 0, 300) + ".....";
		
		String alarmTaskCuid = CUIDHexGenerator.getInstance().generate("PROJECT_NE_TASK");
		String sheetId = task.getRelatedSheetCuid();
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("sheetId", sheetId);
		Map<String, Object> alarmTaskMap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".queryAlarmTask", pm);
		if(alarmTaskMap != null && !alarmTaskMap.isEmpty()) {
			alarmTaskCuid = IbatisDAOHelper.getStringValue(alarmTaskMap, "CUID");
			//删除task下的所有资源数据
			pm.clear();
			pm.put("alarmTaskCuid", alarmTaskCuid);
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAlarmTaskPtp", pm);
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAlarmTaskRelalarm", pm);
		} else {
			//创建任务
			Date now = new Date();
			Record r = new Record("PROJECT_NE_TASK");
			r.addColSqlValue("OBJECTID", r.getObjectIdSql());
			r.addColValue("CREATEUSER", task.getCreator());//创建人
			r.addColValue("TASK_BEGIN_TIME", task.getCreateTime());//任务开始时间
			r.addColValue("TASK_END_TIME", task.getDueTime());//任务结束时间
			r.addColValue("TASK_LASTMODIFY_TIME", now);
			r.addColValue("NE_INFO", neInfo);//网元信息
			r.addColValue("LABEL_CN", task.getSheetInst().getLabelCn().replaceAll("，", ",")+"-"+task.getSheetInst().getSheetNo());//任务名称
			r.addColValue("CUID", alarmTaskCuid);
			r.addColValue("CREATE_TIME", now);
			r.addColValue("LAST_MODIFY_TIME", now);
			r.addColValue("IS_FREQUENCY", 0);//是否周期任务
			//r.addColValue("CIRCLE_START", now);//周期开始时间
			//r.addColValue("CIRCLE_END", sheet.getDueTime());//周期结束时间
			r.addColValue("PROJECT_TYPE", 1);//任务类型
			r.addColValue("RELATED_SHEET_CUID", sheetId);//所属工单
			r.addColValue("CYCLE_TYPE", 1);//周期类型
			
			this.IbatisResDAO.insertDynamicTable(r);
		}
		
		pm.clear();
		pm.put("alarmTaskCuid", alarmTaskCuid);
		pm.put("attempTraphCuidList", attempTraphCuidList);
		
		if(attempTransPtpList != null && !attempTransPtpList.isEmpty()) {
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAlarmTaskPtpTrans", pm);
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAlarmTaskRelalarmTrans", pm);
		}
		
		if(attempPtnPtpList != null && !attempPtnPtpList.isEmpty()) {
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAlarmTaskPtpPtn", pm);
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAlarmTaskRelalarmPtn", pm);
		}
		
		return alarmTaskCuid;
	}
	
	/**
	 * 根据工单ID，清除屏蔽告警表
	 * @param task
	 * @param attempTraphCuidList
	 * @param isAllDel
	 */
	public void deleteAlarmTask(TaskInst task) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("sheetId", task.getRelatedSheetCuid());
		Map<String, Object> alarmTaskMap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".queryAlarmTask", pm);
		if(alarmTaskMap != null && !alarmTaskMap.isEmpty()) {
			String alarmTaskCuid = IbatisDAOHelper.getStringValue(alarmTaskMap, "CUID");
			Date now = new Date();
			Record record = new Record("PROJECT_NE_TASK");
			record.addColValue("CUID", alarmTaskCuid);
			record.addColValue("TASK_END_TIME", task.getEndTime());//任务结束时间
			record.addColValue("TASK_LASTMODIFY_TIME", now);
//			record.addColValue("CIRCLE_START", now);
//			record.addColValue("CIRCLE_END", now);
			Record recordPk = new Record("PROJECT_NE_TASK");
			recordPk.addColValue("CUID", alarmTaskCuid);
			this.IbatisResDAO.updateDynamicTable(record, recordPk);
		}
	}
	
	/**
	 * 根据调度电路ID，查询SDH通道的时隙
	 * @param attempTraphCuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTransCtpByAttempTraph(List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> attempTransCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransCtp", pm);
		
		return attempTransCtpList;
	}
	
	/**
	 * 根据调度SDH通道ID，查询SDH通道的时隙
	 * @param attempTransPathCuidGroup
	 * @return
	 */
	public List<Map<String, Object>> findAttempTransCtpByAttempTrans(List<Map<String, List<String>>> attempTransPathCuidGroup) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
		List<Map<String, Object>> attempTransCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransCtp", pm);
		
		return attempTransCtpList;
	}
	
	/**
	 * 根据调度电路ID，查询SDH通道的端口
	 * @param attempTraphCuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempTransPtpByAttempTraph(List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> attempTransPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransPtp", pm);
		
		return attempTransPtpList;
	}
	
	/**
	 * 根据调度SDH通道ID，查询SDH通道的端口
	 * @param attempTransPathCuidGroup
	 * @return
	 */
	public List<Map<String, Object>> findAttempTransPtpByAttempTrans(List<Map<String, List<String>>> attempTransPathCuidGroup) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
		List<Map<String, Object>> attempTransPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransPtp", pm);
		
		return attempTransPtpList;
	}
	
	/**
	 * 根据调度电路ID，查询PTN通道的时隙
	 * @param attempTraphCuidList
	 * @return
	 */
	public List<Map<String, Object>> findAttempPtnCtpByAttempTraph(List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> attempPtnCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnCtp", pm);
		
		return attempPtnCtpList;
	}
	
	/**
	 * 根据调度PTN通道ID，查询PTN通道的时隙
	 * @param attempPtnPathCuidGroup
	 * @return
	 */
	private List<Map<String, Object>> findAttempPtnCtpByAttempPtn(List<Map<String, List<String>>> attempPtnPathCuidGroup) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempPtnPathCuidGroup", attempPtnPathCuidGroup);
		List<Map<String, Object>> attempPtnCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnCtp", pm);
		
		return attempPtnCtpList;
	}
	
	/**
	 * 根据调度电路ID，查询PTN通道的端口
	 * @param attempTraphCuidList
	 * @return
	 */
	private List<Map<String, Object>> findAttempPtnPtpByAttempTraph(List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> attempPtnPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnPtp", pm);
		
		return attempPtnPtpList;
	}
	
	/**
	 * 根据调度PTN通道ID，查询PTN通道的端口
	 * @param attempPtnPathCuidGroup
	 * @return
	 */
	private List<Map<String, Object>> findAttempPtnPtpByAttempPtn(List<Map<String, List<String>>> attempPtnPathCuidGroup) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempPtnPathCuidGroup", attempPtnPathCuidGroup);
		List<Map<String, Object>> attempPtnPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnPtp", pm);
		
		return attempPtnPtpList;
	}

	/**
	 * 创建调度电路
	 * @param attempTraphList
	 * @param isCopyRoute
	 * @return
	 */
	private List<String> createAttempTraph(List<AttempTraphService> attempTraphList, boolean isCopyRoute) {
		List<String> attempTraphCuidList = new ArrayList<String>();
		List<String> traphCuidList = new ArrayList<String>();
		
		if(attempTraphList != null && !attempTraphList.isEmpty()) {
			Date now = new Date();
			this.formartData(attempTraphList);
			List<Record> recordList = new ArrayList<Record>();
			
			for(AttempTraphService service : attempTraphList) {
				Map<String, Object> map = service.getDataMap();
				
				String cuid = service.getCuid();
				attempTraphCuidList.add(cuid);
				
				Date createTime = (Date) map.get("CREATE_TIME");
				if(createTime == null) {
					createTime = now;
				}
				
				String zdSiteTypeA = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_A");
				String zdSiteCuidA = IbatisDAOHelper.getStringValue(map, "RELATED_A_ZD_SITE_CUID");
				if(StringUtils.isBlank(zdSiteTypeA)) {
					zdSiteTypeA = "SITE";
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_NE_A_CUID"))){
						zdSiteTypeA = "TRANS_ELEMENT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_ROOM_CUID"))){
						zdSiteTypeA = "ROOM";
					}else if(StringUtils.isNotEmpty(zdSiteCuidA)&&zdSiteCuidA.indexOf("ACCESSPOINT")>=0){
						zdSiteTypeA = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(zdSiteCuidA)&&zdSiteCuidA.indexOf("SITE_RESOURCE")>=0){   //吉林资源点cuid有两种形式的数据
						zdSiteTypeA = "ACCESSPOINT";
					}
				}
				String zdSiteTypeZ = IbatisDAOHelper.getStringValue(map, "ZD_SITE_TYPE_Z");
				String zdSiteCuidZ = IbatisDAOHelper.getStringValue(map, "RELATED_Z_ZD_SITE_CUID");
				if(StringUtils.isBlank(zdSiteTypeZ)) {
					zdSiteTypeZ = "SITE";
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_NE_Z_CUID"))){
						zdSiteTypeZ = "TRANS_ELEMENT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_ROOM_CUID"))){
						zdSiteTypeZ = "ROOM";
					}else if(StringUtils.isNotEmpty(zdSiteCuidZ)&&zdSiteCuidZ.indexOf("ACCESSPOINT")>=0){
						zdSiteTypeZ = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(zdSiteCuidZ)&&zdSiteCuidZ.indexOf("SITE_RESOURCE")>=0){
						zdSiteTypeZ = "ACCESSPOINT";
					}
				}
				//若AZ端为跨地市站点，AZ端不判定为区域
				if(SysProperty.getInstance().getValue("districtName").trim().equals("重庆")){
					if ((StringUtils.isNotEmpty(zdSiteTypeA)) && (zdSiteTypeA.equals("DISTRICT"))) {
				          zdSiteTypeA = "SITE";
				    }
					if ((StringUtils.isNotEmpty(zdSiteTypeZ)) && (zdSiteTypeZ.equals("DISTRICT"))) {
						zdSiteTypeZ = "SITE";
				    }
				}
				
				String endStationTypeA = IbatisDAOHelper.getStringValue(map, "END_STATION_TYPE_A");
				
				if(StringUtils.isBlank(endStationTypeA)) {
					endStationTypeA = "SITE";
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID").indexOf("DISTRICT")>=0) {
						endStationTypeA = "DISTRICT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID").indexOf("ACCESSPOINT")>=0) {
						endStationTypeA = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID").indexOf("SITE_RESOURCE")>=0) {
						endStationTypeA = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID").indexOf("ROOM")>=0) {
						endStationTypeA = "ROOM";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_A_END_STATION_CUID").indexOf("SWITCH_ELEMENT")>=0) {
						endStationTypeA = "SWITCHELEMENT";
					}
				}
				
				String endStationTypeZ = IbatisDAOHelper.getStringValue(map, "END_STATION_TYPE_Z");
		        if (StringUtils.isBlank(endStationTypeZ)) {
		        	endStationTypeZ = "SITE";
					if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID").indexOf("DISTRICT")>=0) {
						endStationTypeZ = "DISTRICT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID").indexOf("ACCESSPOINT")>=0) {
						endStationTypeZ = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID").indexOf("SITE_RESOURCE")>=0) {
						endStationTypeZ = "ACCESSPOINT";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID").indexOf("ROOM")>=0) {
						endStationTypeZ = "ROOM";
					}else if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID"))&&IbatisDAOHelper.getStringValue(map, "RELATED_Z_END_STATION_CUID").indexOf("SWITCH_ELEMENT")>=0) {
						endStationTypeZ = "SWITCHELEMENT";
					}
				}
				
				String relatedASiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_SITE_CUID");
				String relatedZSiteCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_SITE_CUID");
				
				//因为带宽有可能是2.5，所以类型需要是字符串而不是整型
				String bandWidth = IbatisDAOHelper.getStringValue(map, "BANDWIDTH");
				if(StringUtils.isBlank(bandWidth)) {
					bandWidth = "2";
				}
				/*if(bandWidth.indexOf("M") != -1) {
					bandWidth = StringUtils.replace(bandWidth, "M", "");
				}*/
				
				Integer isWholeRoute = IbatisDAOHelper.getIntValue(map, "IS_WHOLE_ROUTE");
				Integer scheduleType = IbatisDAOHelper.getIntValue(map, "SCHEDULE_TYPE");
				//停闭，默认电路路由完整
				if(SheetConstants.SCHEDULE_TYPE_CLOSE == scheduleType) {
					isWholeRoute = 1;
				}
				
				Integer ownership = IbatisDAOHelper.getIntValue(map, "OWNERSHIP");
				if(ownership == 0) ownership = 1;
				
				Integer isPriorityMonitorsTraph = IbatisDAOHelper.getIntValue(map, "IS_PRIORITY_MONITORS_TRAPH");
				
				Integer useType = IbatisDAOHelper.getIntValue(map, "USE_TYPE");
				if(useType == 0) useType = 1;
				
				Integer serviceLevel = IbatisDAOHelper.getIntValue(map, "SERVICE_LEVEL");
				if(serviceLevel == 0) serviceLevel = 1;
				
				Integer traphLevel = IbatisDAOHelper.getIntValue(map, "TRAPH_LEVEL");
				if(traphLevel == null || traphLevel == 0) traphLevel = 7;
				
				Integer traphType = IbatisDAOHelper.getIntValue(map, "TRAPH_TYPE");
				if(traphType == null || traphType == 0) traphType = 1;
				
				String relatedUser = "";
				ITraphExtend traphExtend = service.getExtend();
				if(traphExtend != null) {
					JkInfo jkInfo = null;
					if(traphExtend instanceof JkInfo) {
						jkInfo = (JkInfo) traphExtend;
					}
					
					if(jkInfo != null) {
						relatedUser = jkInfo.getClientName();
					}
				}
				if(StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(map,"RELATED_TRAPH_CUID"))){
					traphCuidList.add(IbatisDAOHelper.getStringValue(map,"RELATED_TRAPH_CUID"));
				}
				Record r = new Record("ATTEMP_TRAPH");
				r.addColValue("CUID", cuid);
				r.addColSqlValue("OBJECTID", r.getObjectIdSql());
				r.addColValue("SCHEDULE_TYPE", map.get("SCHEDULE_TYPE"));//调度类型：1、新增 2、停闭 3、调整
				r.addColValue("RELATED_SHEET_ID", map.get("RELATED_SHEET_ID"));//工单ID
				r.addColValue("RELATED_OUT_TRAPH_CUID", map.get("RELATED_OUT_TRAPH_CUID"));//外部接口电路ID
				r.addColValue("IS_WHOLE_ROUTE", isWholeRoute);//电路路由是否完整
				r.addColValue("RELATED_TRAPH_CUID", map.get("RELATED_TRAPH_CUID"));//调整前电路ID
				r.addColValue("RELATED_SHEET_ID", map.get("RELATED_SHEET_ID"));//关联工单ID
				r.addColValue("REQUEST_DATE", map.get("REQUEST_DATE"));//要求完成时间
				r.addColValue("CREATE_TIME", createTime);//创建时间
				r.addColValue("LAST_MODIFY_TIME", now);//修改时间
				if(SheetConstants.SCHEDULE_TYPE_NEW == scheduleType) {
				    if(StringUtils.isNotEmpty(relatedASiteCuid)&&StringUtils.isNotEmpty(relatedZSiteCuid)){
					   r.addColValue("LABEL_CN", service.getLabelCn());//名称
				    }else{
					   r.addColValue("LABEL_CN", "");
				    }
			    }else{
				   r.addColValue("LABEL_CN", service.getLabelCn());
			    }
				
				r.addColValue("NO", service.getNo());//编号
				r.addColValue("TRAPH_RATE", service.getRate());//速率
				r.addColValue("BANDWIDTH", bandWidth);//带宽
				r.addColValue("EXT", map.get("EXT"));//业务类型
				
				//判断业务类型数据是不是以逗号开头和结尾，如果不是则添加
				String extIds = (String)map.get("EXT_IDS");
				if(!extIds.startsWith(",")){
					extIds = "," + extIds;
				}
				if(!extIds.endsWith(",")){
					extIds = extIds + ",";
				}
				//给宗资派来的lte电路加上承载方式
				boolean islt=false;
				String[] exts = StringUtils.split(extIds, ",");
				if (exts != null && exts.length > 0) {
					for(int i = 0;i<exts.length;i++){
						if(exts[i].equals("101")||exts[i].equals("102")||exts[i].equals("103")){
							 islt=true;
						}
					}
				}
				if(islt&&(map.get("EXT_TYPE")==null||map.get("EXT_TYPE").equals(""))){
					if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
						r.addColValue("EXT_TYPE","5");
					}else{
						r.addColValue("EXT_TYPE","7");
					}
				}else{
					r.addColValue("EXT_TYPE", StringUtils.isNotEmpty((String)map.get("EXT_TYPE"))?map.get("EXT_TYPE"):"1");//承载方式
				}
				
				String isJK = IbatisDAOHelper.getStringValue(map, "IS_JIKE");
				if(SysProperty.getInstance().getValue("districtName").trim().equals("陕西") && "1".equals(isJK)){
					r.addColValue("ALIAS", map.get("BIZ_SECURITY_LV"));//集客电路使用服务保障等级(接口传递)作为别名
					r.addColValue("REMARK", map.get("SERVICE_LV"));//集客电路使用客户等级(接口传递)作为备注
					if (StringUtils.isNotEmpty((String)map.get("CUSTOM_NAME"))){
						Map mp = new HashMap();
						mp.put("name", (String)map.get("CUSTOM_NAME"));
						List<String> vpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getVPInfo", mp);
						if (vpList == null || vpList.size()<=0){
							throw new RuntimeException("客户"+(String)map.get("CUSTOM_NAME")+"在调度系统中不存在，请到客户管理处添加该用户后再操作！" );
						}
					}
					r.addColValue("CUSTOMER_TRAPH_NAME", map.get("CUSTOM_NAME"));//此字段存储用户(接口传递)
					r.addColValue("EXT_IDS", ",52,");//业务类型
					r.addColValue("USE_TYPE", 2);//使用类型默认为出租
					r.addColValue("IS_PRIORITY_MONITORS_TRAPH", 1);//是否重点监控默认为是
				}else{
					r.addColValue("ALIAS", map.get("ALIAS")==null?service.getAlias():map.get("ALIAS"));//别名
					r.addColValue("REMARK", map.get("REMARK"));//电路备注
					r.addColValue("EXT_IDS", extIds);//业务类型
					r.addColValue("USE_TYPE", useType);//使用类型
					r.addColValue("IS_PRIORITY_MONITORS_TRAPH", isPriorityMonitorsTraph);
				}
				r.addColValue("ZJSITES", map.get("ZJSITES"));//转接站
				r.addColValue("TRAPH_LEVEL", traphLevel);//电路级别
				r.addColValue("RELATED_A_SITE_CUID", relatedASiteCuid);//A端业务站点ID
				r.addColValue("RELATED_Z_SITE_CUID", relatedZSiteCuid);//Z端业务站点ID
				r.addColValue("RELATED_A_ZD_SITE_CUID", map.get("RELATED_A_ZD_SITE_CUID"));//A端终端ID
				r.addColValue("RELATED_Z_ZD_SITE_CUID", map.get("RELATED_Z_ZD_SITE_CUID"));//Z端终端ID
				r.addColValue("ZD_SITE_TYPE_A", zdSiteTypeA);//A端终端类型
				r.addColValue("ZD_SITE_TYPE_Z", zdSiteTypeZ);//Z端终端类型
				r.addColValue("RELATED_A_END_STATION_CUID", map.get("RELATED_A_END_STATION_CUID"));//A端通达ID
				r.addColValue("RELATED_Z_END_STATION_CUID", map.get("RELATED_Z_END_STATION_CUID"));//Z端通达ID
				r.addColValue("END_STATION_TYPE_A", endStationTypeA);//A端通达类型
				r.addColValue("END_STATION_TYPE_Z", endStationTypeZ);//Z端通达类型
				r.addColValue("RELATED_A_END_SITE_CUID", map.get("RELATED_A_END_STATION_CUID"));//A端通达站点ID
				r.addColValue("RELATED_Z_END_SITE_CUID", map.get("RELATED_Z_END_STATION_CUID"));//A端通达站点ID
				r.addColValue("RELATED_A_DEVROOM_CUID", map.get("RELATED_A_DEVROOM_CUID"));//A端机房ID
				r.addColValue("RELATED_Z_DEVROOM_CUID", map.get("RELATED_Z_DEVROOM_CUID"));//Z端机房ID
				r.addColValue("RELATED_NE_A_CUID", map.get("RELATED_NE_A_CUID"));//A端传输网元ID
				r.addColValue("RELATED_NE_Z_CUID", map.get("RELATED_NE_Z_CUID"));//Z端传输网元ID
				r.addColValue("END_PORT_A", map.get("END_PORT_A"));//A端传输端口
				r.addColValue("END_PORT_Z", map.get("END_PORT_Z"));//Z端传输端口
				r.addColValue("END_DF_PORT_A", map.get("END_DF_PORT_A"));//A端传输端子
				r.addColValue("END_DF_PORT_Z", map.get("END_DF_PORT_Z"));//Z端传输端子
				r.addColValue("RELATED_A_PORT_CUID", map.get("RELATED_A_PORT_CUID"));//A端端口
				r.addColValue("RELATED_Z_PORT_CUID", map.get("RELATED_Z_PORT_CUID"));//Z端端口

				r.addColValue("RELATED_Z_ROOM_CUID", map.get("RELATED_Z_ROOM_CUID"));
				r.addColValue("RELATED_A_ROOM_CUID", map.get("RELATED_A_ROOM_CUID"));
				r.addColValue("RELATED_Z_END_ROOM_CUID", map.get("RELATED_Z_END_ROOM_CUID"));
				r.addColValue("RELATED_A_END_ROOM_CUID", map.get("RELATED_A_END_ROOM_CUID"));
				if(getFlag()){
					if (map.get("END_SWITCH_DEV_A")!=null){
						r.addColValue("END_SWITCH_DEV_A", map.get("END_SWITCH_DEV_A"));
						r.addColSqlValue("RELATED_A_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM T_LOGIC_DEVICE WHERE LABEL_CN='"+map.get("END_SWITCH_DEV_A")+"'");
					}
					if (map.get("END_SWITCH_DEV_Z")!=null){
						r.addColValue("END_SWITCH_DEV_Z", map.get("END_SWITCH_DEV_Z"));
					    r.addColSqlValue("RELATED_Z_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM T_LOGIC_DEVICE WHERE LABEL_CN='"+map.get("END_SWITCH_DEV_Z")+"'");
					}
					if (map.get("END_SWITCHDEV_PORT_A")!=null){
						r.addColValue("END_SWITCHDEV_PORT_A",map.get("END_SWITCHDEV_PORT_A"));
						r.addColSqlValue("RELATED_A_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM T_LOGIC_PORT WHERE LABEL_CN='"+map.get("END_SWITCHDEV_PORT_A")+"'");
					}
					if (map.get("END_SWITCHDEV_PORT_Z")!=null){
						r.addColValue("END_SWITCHDEV_PORT_Z",map.get("END_SWITCHDEV_PORT_Z"));
						r.addColSqlValue("RELATED_Z_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM T_LOGIC_PORT WHERE LABEL_CN='"+map.get("END_SWITCHDEV_PORT_Z")+"'");
					}
				}else{
					if (map.get("END_SWITCH_DEV_A")!=null){
						r.addColValue("END_SWITCH_DEV_A", map.get("END_SWITCH_DEV_A"));
						r.addColSqlValue("RELATED_A_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM SWITCH_ELEMENT WHERE LABEL_CN='"+map.get("END_SWITCH_DEV_A")+"'");
					}
					if (map.get("END_SWITCH_DEV_Z")!=null){
						r.addColValue("END_SWITCH_DEV_Z", map.get("END_SWITCH_DEV_Z"));
					    r.addColSqlValue("RELATED_Z_END_SWITCHDEV_CUID", "SELECT MAX(CUID) FROM SWITCH_ELEMENT WHERE LABEL_CN='"+map.get("END_SWITCH_DEV_Z")+"'");
					}
					if (map.get("END_SWITCHDEV_PORT_A")!=null){
						r.addColValue("END_SWITCHDEV_PORT_A",map.get("END_SWITCHDEV_PORT_A"));
						r.addColSqlValue("RELATED_A_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM SWITCH_PORT WHERE LABEL_CN='"+map.get("END_SWITCHDEV_PORT_A")+"'");
					}
					if (map.get("END_SWITCHDEV_PORT_Z")!=null){
						r.addColValue("END_SWITCHDEV_PORT_Z",map.get("END_SWITCHDEV_PORT_Z"));
						r.addColSqlValue("RELATED_Z_END_SWITCH_PORT_CUID", "SELECT MAX(CUID) FROM SWITCH_PORT WHERE LABEL_CN='"+map.get("END_SWITCHDEV_PORT_Z")+"'");
					}
				}
				
				r.addColValue("END_SWITCH_DF_PORT_A", map.get("END_SWITCH_DF_PORT_A"));//A端端子
				r.addColValue("END_SWITCH_DF_PORT_Z", map.get("END_SWITCH_DF_PORT_Z"));//Z端端子
				if (map.get("JHROOM_A")!=null&&!map.get("JHROOM_A").equals("")){
					if (map.get("JHROOM_A").toString().startsWith("ROOM")){
						r.addColValue("JHROOM_ACUID", map.get("JHROOM_A"));//A端业务机房ID
						r.addColSqlValue("JHROOM_A", "SELECT LABEL_CN FROM ROOM WHERE CUID='"+map.get("JHROOM_A")+"'");
					}else{
						r.addColValue("JHROOM_A", map.get("JHROOM_A"));//A端业务机房
						r.addColSqlValue("JHROOM_ACUID", "SELECT MAX(CUID) FROM ROOM WHERE LABEL_CN='"+map.get("JHROOM_A")+"'");
					}
				}else if (map.get("ACCESS_POINT_A")!=null&&!map.get("ACCESS_POINT_A").equals("")){
					if(map.get("ACCESS_POINT_A").toString().startsWith("ACCESSPOINT")){
						r.addColValue("JHROOM_ACUID", map.get("ACCESS_POINT_A"));//A端业务资源点ID
						r.addColSqlValue("JHROOM_A", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+map.get("ACCESS_POINT_A")+"'");
					}else{
						r.addColValue("JHROOM_A", map.get("ACCESS_POINT_A"));//A端业务资源点
						r.addColSqlValue("JHROOM_ACUID", "SELECT CUID FROM ACCESSPOINT WHERE LABEL_CN='"+map.get("ACCESS_POINT_A")+"'");
					}
				}
				if (map.get("JHROOM_Z")!=null&&!map.get("JHROOM_Z").equals("")){
					if (map.get("JHROOM_Z").toString().startsWith("ROOM")){
						r.addColValue("JHROOM_ZCUID", map.get("JHROOM_Z"));//Z端业务机房ID
						r.addColSqlValue("JHROOM_Z", "SELECT LABEL_CN FROM ROOM WHERE CUID='"+map.get("JHROOM_Z")+"'");
					}else{

						r.addColValue("JHROOM_Z", map.get("JHROOM_Z"));//Z端业务机房
						r.addColSqlValue("JHROOM_ZCUID", "SELECT MAX(CUID) FROM ROOM WHERE LABEL_CN='"+map.get("JHROOM_Z")+"'");

					}
				}else if (map.get("ACCESS_POINT_Z")!=null&&!map.get("ACCESS_POINT_Z").equals("")){
					if(map.get("ACCESS_POINT_Z").toString().startsWith("ACCESSPOINT")){
						r.addColValue("JHROOM_ZCUID", map.get("ACCESS_POINT_Z"));//Z端业务资源点ID
						r.addColSqlValue("JHROOM_Z", "SELECT LABEL_CN FROM ACCESSPOINT WHERE CUID='"+map.get("ACCESS_POINT_Z")+"'");
					}else{
						r.addColValue("JHROOM_Z", map.get("ACCESS_POINT_Z"));//Z端业务资源点
						r.addColSqlValue("JHROOM_ZCUID", "SELECT CUID FROM ACCESSPOINT WHERE LABEL_CN='"+map.get("ACCESS_POINT_Z")+"'");
					}
				}
				
				r.addColValue("ZJDF_A", map.get("ZJDF_A"));//A端转接配线架
				r.addColValue("ZJDF_Z", map.get("ZJDF_Z"));//Z端转接配线架
				r.addColValue("SDXC1_A", map.get("SDXC1_A"));//A端SDXC1
				r.addColValue("SDXC1_Z", map.get("SDXC1_Z"));//Z端SDXC1
				r.addColValue("SDXC2_A", map.get("SDXC2_A"));//A端SDXC2
				r.addColValue("SDXC2_Z", map.get("SDXC2_Z"));//Z端SDXC2
				r.addColValue("EXTPATH1_A", map.get("EXTPATH1_A"));//A端延伸路由段
				r.addColValue("EXTPATH1_Z", map.get("EXTPATH1_Z"));//Z端延伸路由段
				
				r.addColValue("JHSITE_A", map.get("JHSITE_A"));//A端交换站点
				r.addColValue("JHSITE_Z", map.get("JHSITE_Z"));//Z端交换站点
				r.addColValue("END_VLANID_A", map.get("END_VLANID_A"));//A端VLAN ID
				r.addColValue("END_VLANID_Z", map.get("END_VLANID_Z"));//Z端VLAN ID
				r.addColValue("ADDRESS_A", map.get("ADDRESS_A"));//A端详细地址
				r.addColValue("ADDRESS_Z", map.get("ADDRESS_Z"));//Z端详细地址
				r.addColValue("IMPORT_LEVEL", map.get("IMPORT_LEVEL"));//重要性级别s
				r.addColValue("NOTES", map.get("NOTES")==null?service.getNotes():map.get("NOTES"));//电路名称备注
				r.addColValue("RELATED_USER_CUID", map.get("RELATED_USER_CUID"));
				r.addColValue("LONG_DISTANCE_TRAPH_NAME", map.get("LONG_DISTANCE_TRAPH_NAME")==null?service.getMainTraphName():map.get("LONG_DISTANCE_TRAPH_NAME"));
				r.addColValue("SERVICE_LEVEL", serviceLevel);//服务级别
				r.addColValue("OWNERSHIP", ownership);//产权
				r.addColValue("TRAPH_TYPE", traphType);//电路类型，1 传输电路
				r.addColValue("PATHINFO", map.get("PATHINFO"));//路由描述
				r.addColValue("DESIGN_INFO", map.get("DESIGN_INFO"));//设计路由描述
				if (StringUtils.isNotEmpty(relatedUser)){
					r.addColSqlValue("RELATED_USER_CUID", "SELECT MAX(CUID) FROM VP WHERE LABEL_CN='"+relatedUser+"'");//客户CUID
					r.addColValue("USER_NAME", relatedUser);//客户CUID
				}
				
				//A端区域ID
				String relatedADistrictCuid = IbatisDAOHelper.getStringValue(map, "RELATED_A_DISTRICT_CUID");
				if(StringUtils.isNotBlank(relatedADistrictCuid)) {
					r.addColValue("RELATED_A_DISTRICT_CUID", relatedADistrictCuid);
				} else {
					r.addColSqlValue("RELATED_A_DISTRICT_CUID", 
							"SELECT SUBSTR(S.RELATED_SPACE_CUID, 0, 26) AS RELATED_SPACE_CUID FROM SITE S WHERE S.CUID = '"+relatedASiteCuid+"'"+
							"UNION ALL SELECT SUBSTR(A.DISTRICT_CUID, 0, 26) AS RELATED_SPACE_CUID FROM ACCESSPOINT A WHERE A.CUID = '"+relatedASiteCuid+"'");//A端区域ID
				}
				
				//Z端区域ID
				String relatedZDistrictCuid = IbatisDAOHelper.getStringValue(map, "RELATED_Z_DISTRICT_CUID");
				if(StringUtils.isNotBlank(relatedZDistrictCuid)) {
					r.addColValue("RELATED_Z_DISTRICT_CUID", relatedZDistrictCuid);
				} else {
					r.addColSqlValue("RELATED_Z_DISTRICT_CUID", 
							"SELECT SUBSTR(S.RELATED_SPACE_CUID, 0, 26) AS RELATED_SPACE_CUID FROM SITE S WHERE S.CUID = '"+relatedZSiteCuid+"'"+
							"UNION ALL SELECT SUBSTR(A.DISTRICT_CUID, 0, 26) AS RELATED_SPACE_CUID FROM ACCESSPOINT A WHERE A.CUID = '"+relatedZSiteCuid+"'");//Z端区域ID
				}
				
				//默认值
				r.addColValue("CHECK_FLAG", 1);//核查状态
				r.addColValue("SCHEDULE_STATE", 2);//调度状态
				r.addColValue("PROTECT_MODE", 1);//保护方式
				r.addColValue("IS_IMPORTANT_PROTECT", 1);//重保状态
				r.addColValue("IS_DECLARED", 0);//是否上报
				r.addColValue("IS_TEST_TRAPH", 0);//是否测试电路
				r.addColValue("ISDELETE", 0);//是否删除
				r.addColValue("INPUT_TYPE", 0);
				r.addColValue("IMPORT_TYPE", 0);
				
				recordList.add(r);
				
				Record record = new Record("T_EXT_ATTEMP_TRAPH");
				record.addColValue("CUID", cuid);
				record.addColValue("CUSTOM_A_NAME", IbatisDAOHelper.getStringValue(map, "CUSTOM_A_NAME"));
				record.addColValue("CUSTOM_Z_NAME", IbatisDAOHelper.getStringValue(map, "CUSTOM_Z_NAME"));
				record.addColValue("CUSTOM_PHONE_A", IbatisDAOHelper.getStringValue(map, "CUSTOM_PHONE_A"));
				record.addColValue("CUSTOM_PHONE_Z", IbatisDAOHelper.getStringValue(map, "CUSTOM_PHONE_Z"));
				record.addColValue("RELATED_PTP_A_CUID", IbatisDAOHelper.getStringValue(map, "A_END_PTP"));
				record.addColValue("RELATED_CTP_A_CUID", IbatisDAOHelper.getStringValue(map, "A_END_CTP"));
				record.addColValue("RELATED_PTP_Z_CUID", IbatisDAOHelper.getStringValue(map, "Z_END_PTP"));
				record.addColValue("RELATED_CTP_Z_CUID", IbatisDAOHelper.getStringValue(map, "Z_END_CTP"));
				record.addColValue("IS_ACT", IbatisDAOHelper.getStringValue(map, "IS_ACT"));
				record.addColValue("PTN_ERRORINFO", IbatisDAOHelper.getStringValue(map, "PTN_ERRORINFO"));
				recordList.add(record);
			}
			
			logger.info("创建调度中电路");
			this.IbatisResDAO.insertDynamicTableBatch(recordList);
		}
		
		if(isCopyRoute) {
			this.copyTraphRouteInfo(attempTraphCuidList);
		}
		ResTraphBO.updateServiceSchduleState(traphCuidList,SheetConstants.SCHEDULE_STATE_RUN);
		return attempTraphCuidList;
	}
	
	/**
	 * 根据调度中电路的CUID集合复制存量相关的路由信息
	 * @param attempTraphCuidList
	 */
	private void copyTraphRouteInfo(List<String> attempTraphCuidList) {
		List<String> traphRoute2PathCuidList = new ArrayList<String>();
		List<String> traph2IndiPointsCuidList = new ArrayList<String>();
		List<String> multiPathCuidList = new ArrayList<String>();
		List<String> textPathCuidList = new ArrayList<String>();
		List<String> shelfBuildPathCuidList = new ArrayList<String>();
		List<String> ptnPathCuidList = new ArrayList<String>();
		List<String> msapPathCuidList = new ArrayList<String>();
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		//查询TRANS_PATH_TO_TRAPH的CUID
		List<String> transPath2TraphCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTransPath2Traph", pm);
		//查询TRAPH_ROUTE的CUID
		List<String> traphRouteCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTraphRoute", pm);
		
		if(traphRouteCuidList != null && !traphRouteCuidList.isEmpty()) {
			pm.clear();
			pm.put("traphRouteCuidList", traphRouteCuidList);
			//查询TRAPH_ROUTE_TO_PATH的CUID,PATH_TYPE,PATH_CUID
			List<Map<String, Object>> traphRouteToPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTraphRoute2Path", pm);
			if(traphRouteToPathList != null && traphRouteToPathList.size() > 0) {
				for(Map<String, Object> map : traphRouteToPathList) {
					String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
					String pathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");
					String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
					
					traphRoute2PathCuidList.add(cuid);
					
					if("TEXT_PATH".equals(pathType)) {
						textPathCuidList.add(pathCuid);
					} else if("SELF_BUILT_PATH".equals(pathType)) {
						shelfBuildPathCuidList.add(pathCuid);
					} else if("PTN_PATH".equals(pathType)) {
						ptnPathCuidList.add(pathCuid);
					}else if("MSAP_PATH".equals(pathType)) {
						msapPathCuidList.add(pathCuid);
					}
				}
			}
			
			//查询TRAPH_TO_INDI_POINTS的CUID
			traph2IndiPointsCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTraph2IndiPoints", pm);
		}
		
		if(traphRoute2PathCuidList != null && !traphRoute2PathCuidList.isEmpty()) {
			pm.clear();
			pm.put("traphRouteToPathCuidList", traphRoute2PathCuidList);
			//查询MULTI_PATH的CUID,PATH_TYPE,PATH_CUID
			List<Map<String, Object>> multiPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryMultiPath", pm);
			if(multiPathList != null && multiPathList.size() > 0) {
				for(Map<String, Object> map : multiPathList) {
					String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
					String pathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");
					String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
					
					multiPathCuidList.add(cuid);
					
					if("TEXT_PATH".equals(pathType)) {
						textPathCuidList.add(pathCuid);
					} else if("SELF_BUILT_PATH".equals(pathType)) {
						shelfBuildPathCuidList.add(pathCuid);
					} else if("PTN_PATH".equals(pathType)) {
						ptnPathCuidList.add(pathCuid);
					}else if("MSAP_PATH".equals(pathType)) {
						msapPathCuidList.add(pathCuid);
					}
				}
			}
		}
		
		if(!transPath2TraphCuidList.isEmpty()) {
			pm.clear();
			List<Map<String, List<String>>> transCuidGroup = IbatisDAOHelper.pareseGroupList(IbatisDAOHelper.skipEmptyForList(transPath2TraphCuidList), 1000);
			pm.put("transCuidGroup", transCuidGroup);
			logger.info("复制电路与电路通道的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyTransPath2Traph", pm);
		}
		
		if(!traphRouteCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", traphRouteCuidList);
			logger.info("复制电路路由");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyTraphRoute", pm);
		}
		
		if(!traphRoute2PathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", traphRoute2PathCuidList);
			
			logger.info("复制电路路由与通道");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyTraphRouteToPath", pm);
		}
		
		if(!multiPathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", multiPathCuidList);
			
			logger.info("复制MSTP电路通道");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyMultiPath", pm);
		}
		
		if(!textPathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", textPathCuidList);
			
			logger.info("复制文本段");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyTextPath", pm);
		}
		
		if(!shelfBuildPathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", shelfBuildPathCuidList);
			
			logger.info("复制自建段");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyShelfBuildPath", pm);
		}
		
		if(!ptnPathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", ptnPathCuidList);
			
			logger.info("复制PTN通道");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyPtnPath", pm);
			logger.info("复制PTN通道和ip的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyPtnPath2Ip", pm);
			logger.info("复制PTN通道和staticRoute的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyPtnPath2StaticRoute", pm);
		}
		
		if(!msapPathCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", msapPathCuidList);
			
			logger.info("复制MSAP通道");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyMsapPath", pm);
		}
		
		if(!traph2IndiPointsCuidList.isEmpty()) {
			pm.clear();
			pm.put("cuidList", traph2IndiPointsCuidList);
			
			logger.info("复制电路路由和转接点的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".copyTraph2IndiPoints", pm);
		}
	}
	
	/**
	 * 绑定资源与任务的关系
	 * @param taskServiceList
	 * @return
	 */
	private List<String> createTaskService(List<Map<String, Object>> taskServiceList) {
		List<String> taskServiceCuidList = new ArrayList<String>();
		List<String> taskCuidList = new ArrayList<String>();
		if(taskServiceList != null && !taskServiceList.isEmpty()) {
			List<Record> recordList = new ArrayList<Record>();
			List<Record> updateList = new ArrayList<Record>();
			List<Record> pkList = new ArrayList<Record>();
			for(Map<String, Object> map : taskServiceList) {
				String cuid = CUIDHexGenerator.getInstance().generate("T_TASK_TO_SERVICE");
				taskServiceCuidList.add(cuid);
				String taskId = (String)map.get("RELATED_TASK_CUID");
				if(StringUtils.isEmpty(taskId))throw new RuntimeException("任务ID不允许为空！");
				taskCuidList.add(taskId);
				Record r = new Record("T_TASK_TO_SERVICE");
				r.addColValue("CUID", cuid);
				r.addColValue("RELATED_ORDER_CUID", map.get("RELATED_ORDER_CUID"));
				r.addColValue("RELATED_SHEET_CUID", map.get("RELATED_SHEET_CUID"));
				r.addColValue("RELATED_TASK_CUID", taskId);
				r.addColValue("RELATED_SERVICE_CUID", map.get("RELATED_SERVICE_CUID"));
				r.addColValue("RELATED_SERVICE_TYPE", map.get("RELATED_SERVICE_TYPE"));
				r.addColValue("STATE", map.get("STATE"));
				r.addColValue("SORT_NO", map.get("SORT_NO"));
				recordList.add(r);
				
				String orderDetailCuid = IbatisDAOHelper.getStringValue(map, "RELATED_ORDER_DETAIL_CUID");
				if(StringUtils.isNotBlank(orderDetailCuid)){
					Record pk = new Record("T_ACT_ORDER_DETAIL");
					pk.addColValue("CUID", orderDetailCuid);
					pkList.add(pk);
					Record update = new Record("T_ACT_ORDER_DETAIL");
					update.addColValue("RELATED_DETAIL_CUID", map.get("RELATED_SERVICE_CUID"));
					update.addColValue("RELATED_SHEET_CUID", map.get("RELATED_SHEET_CUID"));
					updateList.add(update);
				}
			}
			
			logger.info("绑定资源与任务的关系");
			this.IbatisResDAO.insertDynamicTableBatch(recordList);
			if(!pkList.isEmpty()){
				this.IbatisResDAO.updateDynamicTableBatch(updateList, pkList);
			}
			//更新任务资源服务数量
			this.ProcessBO.updateTaskServiceNum(taskCuidList);
		}
		
		return taskServiceCuidList;
	}
	
	
	/**
	 * 校验电路是否已在调度中
	 * @param traphCuidList
	 * 
	 * return {key:存量电路ID, value:调度电路ID}
	 */
	private Map<String, String> findValidateTraphCuidMap(List<String> traphCuidList) {
		if(traphCuidList == null || traphCuidList.isEmpty()) throw new RuntimeException("没有调整前电路信息！");
		
		Map<String, String> validatTraphCuidMap = new HashMap<String, String>();
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", traphCuidList);
		List<Map<String, Object>> existsAttempTraphList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".validateTraphState", pm);
		if(existsAttempTraphList != null && !existsAttempTraphList.isEmpty()) {
			List<String> errorInfoList = new ArrayList<String>();
			for(Map<String, Object> map : existsAttempTraphList) {
				String traphCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_CUID");
				String traphName = IbatisDAOHelper.getStringValue(map, "TRAPH_NAME");
				String attempTraphCuid = IbatisDAOHelper.getStringValue(map, "ATTEMP_TRAPH_CUID");
				String scheduleState = IbatisDAOHelper.getStringValue(map, "SCHEDULE_STATE");
				if("2".equals(scheduleState)) {
					errorInfoList.add(traphName);
				} else if("3".equals(scheduleState)) {
					validatTraphCuidMap.put(traphCuid, attempTraphCuid);
				}
			}
			if(errorInfoList != null && !errorInfoList.isEmpty()) {
				String errorInfo = StringUtils.join(errorInfoList, "\n");
				errorInfo += "\n电路已在调度中！";
				
				throw new RuntimeException(errorInfo);
			}
		}
		
		return validatTraphCuidMap;
	}
	
	/**
	 * 根据流程定义判断是否设置默认施工角色
	 * @param task
	 * @return
	 */
	private boolean isSetDefaultRole(TaskInst task) {
		boolean isSetDefaultRole = true;
		
		if(task != null && task.getSheetInst() != null) {
			if(SheetConstants.SHEET_CODE_TRAPHSHORT.equals(task.getSheetInst().getSheetCfg().getSheetCode())) {
				isSetDefaultRole = false;
			}
		}
		
		return isSetDefaultRole;
	}
	
	/**
	 * 根据资源ID，删除资源与任务的关系
	 * @param serviceCuidList
	 */
	public void deleteTaskService(List<String> serviceCuidList) {
		List<TaskInst> taskList = ProcessBO.findRunTaskServiceByService(serviceCuidList);
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("serviceCuidList", serviceCuidList);
		logger.info("根据资源ID，删除资源与任务的关系");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTaskService", pm);
		//汇总任务下的服务数量
		List<String> taskCuidList = new ArrayList<String>();
		for(TaskInst task:taskList){
			taskCuidList.add(task.getCuid());
		}
		ProcessBO.updateTaskServiceNum(taskCuidList);
	}
	
	/**
	 * 根据资源ID，删除资源的任务链
	 * @param serviceCuidList
	 */
	private void deleteTaskServiceLink(List<String> serviceCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("serviceCuidList", serviceCuidList);
		
		logger.info("根据资源ID，删除资源与任务的关系");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTaskServiceLink", pm);
	}
	
	/**
	 * 清空被其他资源所占用的通道
	 * @param pathCuidList
	 * @param attempTraphCuidList
	 * @param searchList{key:RELATED_PATH_CUID, key:RELATED_SERVICE_CUID}
	 */
	private void removeRepeatPath(List<String> pathCuidList, List<String> attempTraphCuidList, List<Map<String, Object>> searchList) {
		if(pathCuidList != null && !pathCuidList.isEmpty()) {
			List<String> tempRepeatPathCuidList = new ArrayList<String>();
			Map<String, List<String>> tempPathMap = new HashMap<String, List<String>>();
			
			if(searchList != null && !searchList.isEmpty()) {
				for(Map<String, Object> map : searchList) {
					String relatedPathCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PATH_CUID");
					String relatedServiceCuid = IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID");
					
					List<String> list = tempPathMap.get(relatedPathCuid);
					if(list == null) {
						list = new ArrayList<String>();
						tempPathMap.put(relatedPathCuid, list);
					}
					list.add(relatedServiceCuid);
				}
				
				if(tempPathMap != null && !tempPathMap.isEmpty()) {
					for(String pathCuid : tempPathMap.keySet()) {
						List<String> list = tempPathMap.get(pathCuid);
						list.removeAll(attempTraphCuidList);
					}
					
					for(String pathCuid : tempPathMap.keySet()) {
						List<String> list = tempPathMap.get(pathCuid);
						if(list.size() > 0) {
							tempRepeatPathCuidList.add(pathCuid);
						}
					}
				}
				
				pathCuidList.removeAll(tempRepeatPathCuidList);
			}
		}
	}
	
	/**
	 * 清空被其他资源所占用的端点
	 * @param pointList
	 * @param attempPathCuidList
	 * @param searchList {key:RELATED_PATH_CUID, key:POINT_CUID}
	 */
	public void removeRepeatPoint(List<String> pointList, List<String> attempPathCuidList, List<Map<String, Object>> searchList) {
		if(pointList != null && !pointList.isEmpty()) {
			List<String> tempRepeatPointCuidList = new ArrayList<String>();
			Map<String, List<String>> tempPointMap = new HashMap<String, List<String>>();
			
			if(searchList != null && !searchList.isEmpty()) {
				for(Map<String, Object> map : searchList) {
					String relatedPathCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PATH_CUID");
					String pointCuid = IbatisDAOHelper.getStringValue(map, "POINT_CUID");
					
					List<String> list = tempPointMap.get(pointCuid);
					if(list == null) {
						list = new ArrayList<String>();
						tempPointMap.put(pointCuid, list);
					}
					list.add(relatedPathCuid);
				}
				
				if(tempPointMap != null && !tempPointMap.isEmpty()) {
					for(String pointCuid : tempPointMap.keySet()) {
						List<String> list = tempPointMap.get(pointCuid);
						list.removeAll(attempPathCuidList);
					}
					
					for(String pointCuid : tempPointMap.keySet()) {
						List<String> list = tempPointMap.get(pointCuid);
						if(list.size() > 0) {
							tempRepeatPointCuidList.add(pointCuid);
						}
					}
				}
				
				pointList.removeAll(tempRepeatPointCuidList);
			}
		}
	}
	
	/**
	 * 根据调度电路ID，把调度电路存入存量中
	 * @param ac
	 * @param attempTraphCuidList
	 */
	private void insertAttempTraph(ServiceActionContext ac, List<String> attempTraphCuidList) {
		// 把调度电路的关系存入存量中
		this.insertAttempTraphRelation(ac, attempTraphCuidList);
		
		// 把调度电路存入存量中
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempTraphCuidList);
		
		List<Map<String,Object>> attempTraphExtend = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphExtend", pm);
		
		logger.info("把调度电路存入存量中");
		if (attempTraphExtend.size()>0){
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphExtend", pm);
		}
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraph", pm);
		this.sendMsg(attempTraphCuidList);
	}
	private void sendMsg(List<String> traphCuidList){
		//获得电路对象
		Map pm = new HashMap();
		List<String> cuids=new ArrayList<String>();
		for(String cuid:traphCuidList){
			cuids.add(cuid.replace("ATTEMP_TRAPH-", "TRAPH-"));
		}
		pm.put("traphCuidList", cuids);
		List<Map<String, Object>> traphList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ResTraph.getAllColTraph", pm);
		/*DataObjectList  dbos=new DataObjectList();
		for(Map<String,Object> map:traphList){
			GenericDO dbo=new GenericDO();
			dbo.setAttrValues(map);
			dbos.add(dbo);
		}
		SendMessageBO.sendMessageToMq(CachedDtoMessage.DTO_MSG_TYPE.CREATE, "T_CM_OBJ_CHG", dbos);*/
		 Object dbos = new ArrayList();
		    for (Map map : traphList) {
		      GenericDO dbo = new GenericDO();
		      dbo.setAttrValues(map);
		      ((List)dbos).add(dbo);
		    }
		    this.SendMessageBO.sendMessageToMq(CachedDtoMessage.DTO_MSG_TYPE.CREATE, "T_CM_OBJ_CHG", (List)dbos);
	}
	/**
	 * 根据调度电路ID，把调度电路的关系存入存量中
	 * @param ac
	 * @param attempTraphCuidList
	 */
	private void insertAttempTraphRelation(ServiceActionContext ac, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		//查询通道
		List<Map<String, Object>> attempPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRoutePath", pm);
		
		List<String> attempTransPathCuidList = new ArrayList<String>();
		List<String> attempPtnPathCuidList = new ArrayList<String>();
		List<String> attempMsapPathCuidList = new ArrayList<String>();
		List<String> attempTextPathCuidList = new ArrayList<String>();
		List<String> attempSelfBuiltPathCuidList = new ArrayList<String>();
		List<String> attempTraphRouteCuidList = new ArrayList<String>();
		List<String> attempRoutePathCuidList = new ArrayList<String>();
		List<String> attempMultiPathCuidList = new ArrayList<String>();
		
		if(attempPathList != null && !attempPathList.isEmpty()) {
			for(Map<String, Object> map : attempPathList) {
				String attempRoutePathCuid = IbatisDAOHelper.getStringValue(map, "CUID");
				String attempTraphRouteCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_ROUTE_CUID");
				String attempMultiCuid = IbatisDAOHelper.getStringValue(map, "MULTI_CUID");
				String attempPathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
				String attempPathType = IbatisDAOHelper.getStringValue(map, "PATH_TYPE");
				String attempMultiPathCuid = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_CUID");
				String attempMultiPathType = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_TYPE");
				
				
				if("TRANS_PATH".equals(attempPathType) || "TRANS_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempTransPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempTransPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_PTN_PATH".equals(attempPathType) || "ATTEMP_PTN_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempPtnPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempPtnPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_TEXT_PATH".equals(attempPathType) || "ATTEMP_TEXT_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempTextPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempTextPathCuidList.add(attempMultiPathCuid);
					}
				} else if("ATTEMP_SELF_BUILT_PATH".equals(attempPathType) || "ATTEMP_SELF_BUILT_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempSelfBuiltPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempSelfBuiltPathCuidList.add(attempMultiPathCuid);
					}
				}else if("MSAP_PATH".equals(attempPathType) || "MSAP_PATH".equals(attempMultiPathType)) {
					if(StringUtils.isNotBlank(attempPathCuid)) {
						attempMsapPathCuidList.add(attempPathCuid);
					}
					if(StringUtils.isNotBlank(attempMultiPathCuid)) {
						attempMsapPathCuidList.add(attempMultiPathCuid);
					}
				}
				
				if(StringUtils.isNotBlank(attempRoutePathCuid)) attempRoutePathCuidList.add(attempRoutePathCuid);
				if(StringUtils.isNotBlank(attempTraphRouteCuid)) attempTraphRouteCuidList.add(attempTraphRouteCuid);
				if(StringUtils.isNotBlank(attempMultiCuid)) attempMultiPathCuidList.add(attempMultiCuid);
			}
		}
		
		if(attempTransPathCuidList != null && !attempTransPathCuidList.isEmpty()) {
			// 插入与SDH通道的关系
			this.insertAttempTraphTransRelation(ac, attempTransPathCuidList,attempTraphCuidList);
		}
		
		if(attempPtnPathCuidList != null && !attempPtnPathCuidList.isEmpty()) {
			// 插入与PTN通道的关系
			this.insertAttempTraphPtnRelation(ac, attempPtnPathCuidList);
		}
		
		if(attempMsapPathCuidList != null && !attempMsapPathCuidList.isEmpty()) {
			// 插入与MSAP通道的关系
			this.insertAttempTraphMsapRelation(ac, attempMsapPathCuidList);
		}
		
		//复制调度电路业务扩展信息到存量电路中
		Map<String,List<ServiceRel>> relMap = this.findServiceRels(attempTraphCuidList);
		for(String infoType:relMap.keySet()){
			ITraphExtendBO extendBO = this.extendBOMap.get(infoType);
			if(extendBO!=null){
				List<ServiceRel> relList = relMap.get(infoType);
				List<String> infoCuidList = new ArrayList<String>();
				for(ServiceRel rel:relList){
					infoCuidList.add(rel.getInfoCuid());
				}
				extendBO.copyExtendInfoToResByInfo(infoCuidList);
			}
		}
		//复制调度电路专网业务信息到存量电路中
		this.insertAttempTraphGroupRelation(ac, attempTraphCuidList);
		if(attempTextPathCuidList != null && !attempTextPathCuidList.isEmpty()) {
			// 插入与文本段的关系
			this.insertAttempTraphTextRelation(ac, attempTextPathCuidList);
		}
		
		if(attempSelfBuiltPathCuidList != null && !attempSelfBuiltPathCuidList.isEmpty()) {
			// 插入与自建段的关系
			this.insertAttempTraphSelfBuiltRelation(ac, attempSelfBuiltPathCuidList);
		}
		
		// 插入与逻辑口的关系
		this.insertAttempLogicPortRelation(ac, attempTraphCuidList);
		
		List<String> insertRouteCuidList = new ArrayList<String>();
		insertRouteCuidList.addAll(attempTraphRouteCuidList);
		insertRouteCuidList.addAll(attempRoutePathCuidList);
		// 把调度电路路由点(转接站)存入存量中
		pm.clear();
		pm.put("attempTraphRouteCuidList", insertRouteCuidList);
		
		logger.info("把调度电路路由点(转接站)存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphToIndiPoints", pm);
		
		// 把调度电路MULTI通道存入存量中
		pm.clear();
		pm.put("cuidList", attempMultiPathCuidList);
		
		logger.info("把调度电路MULTI通道存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempMultiPath", pm);
		
		// 把调度电路的路由与通道的关系存入存量中
		pm.clear();
		pm.put("cuidList", attempRoutePathCuidList);
		
		logger.info("把调度电路的路由与通道的关系存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttTraphRouteToPath", pm);
		
		// 把调度电路的路由存入存量中
		pm.clear();
		pm.put("cuidList", attempTraphRouteCuidList);
		
		logger.info("把调度电路的路由存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphRoute", pm);
		
		//把黑龙江上端站信息存入存量中
		if(SysProperty.getInstance().getValue("districtName").trim().equals("黑龙江")){
			/*pm.clear();
			pm.put("cuidList", attempTraphCuidList);
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphUpPort", pm);
			logger.info("把调度电路的上端站信息存入存量中");*/
			}
	}
	//把调度电路的路由与通道的关系存入存量中
//	private void insertAttTraphRouteToPath(Map<String, Object> pm) {
//		List<Map<String,Object>> attTraphRouteInfoList = this.IbatisResDAO.getSqlMapClientTemplate()
//		                                        .queryForList(sqlMap+".queryAttTraphRouteToPath", pm);
//		List<Record> recordList = new ArrayList<Record>();
//		if (attTraphRouteInfoList!= null && attTraphRouteInfoList.size()>0){
//			for (Map<String,Object> map : attTraphRouteInfoList){
//				Record record = new Record("TRAPH_ROUTE_TO_PATH");
//				record.addColValue("OBJECTID", map.get("OBJECTID"));
//				record.addColValue("PATH_TYPE", IbatisDAOHelper.getStringValue(map, "PATH_TYPE"));
//				record.addColValue("INDEX_PATH_ROUTE", map.get("INDEX_PATH_ROUTE"));
//				record.addColValue("PATH_CUID", IbatisDAOHelper.getStringValue(map, "PATH_CUID"));
//				record.addColValue("TRAPH_ROUTE_CUID", IbatisDAOHelper.getStringValue(map, "TRAPH_ROUTE_CUID"));
//				record.addColValue("AVLANID", IbatisDAOHelper.getStringValue(map, "AVLANID"));
//				record.addColValue("ZVLANID", IbatisDAOHelper.getStringValue(map, "ZVLANID"));
//				record.addColValue("WORK_MODE", map.get("WORK_MODE"));
//				record.addColValue("MSTP_EXTS", map.get("MSTP_EXTS"));
//				record.addColValue("A_MAC", IbatisDAOHelper.getStringValue(map, "A_MAC"));
//				record.addColValue("A_MP", IbatisDAOHelper.getStringValue(map, "A_MP"));
//				record.addColValue("Z_MAC", IbatisDAOHelper.getStringValue(map, "Z_MAC"));
//				record.addColValue("Z_MP", IbatisDAOHelper.getStringValue(map, "Z_MP"));
//				record.addColValue("LABEL_CN", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
//				record.addColValue("CUID", IbatisDAOHelper.getStringValue(map, "CUID"));
//				record.addColValue("ISDELETE", map.get("ISDELETE"));
//				if (map.get("CREATE_TIME") != null){
//					String str = map.get("CREATE_TIME").toString();
//					Date createTime = TimeFormatHelper.convertDate(str);
//					record.addColValue("CREATE_TIME", createTime);
//				}
//				record.addColValue("LAST_MODIFY_TIME", new Date());
//				
//				recordList.add(record);
//			}
//		}
//		this.IbatisResDAO.insertDynamicTableBatch(recordList);
//	}

	//复制调度电路专网业务信息到存量电路中
	private void insertAttempTraphGroupRelation(ServiceActionContext ac,List<String> attempTraphCuidList) {
		Map mp = new HashMap();
		mp.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String,Object>> attempTraphGroupList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".findAttempTraphGroupRelList",mp);
		if (attempTraphGroupList != null && attempTraphGroupList.size()>0){
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTraphGroupRelation", mp);
		}
	}

	/**
	 * 根据调度电路ID，往存量插入电路与SDH通道的关系
	 * @param ac
	 * @param attempTransPathCuidList
	 */
	/*private void insertAttempTraphTransRelation(ServiceActionContext ac, List<String> attempTransPathCuidList,List<String> attempTraphCuidList) {
		if(attempTransPathCuidList != null && !attempTransPathCuidList.isEmpty()) {
			List<Map<String, List<String>>> attempTransPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempTransPathCuidList, 1000);

			Map<String, Object> pm = new HashMap<String, Object>();
			List<String> failTransPathCuidList = new ArrayList<String>();
			Map<String,List<Map<String, Object>>> stateMap = new HashMap<String, List<Map<String,Object>>>();
			Map<String,List<Map<String, Object>>> ptnPathCuidMap = new HashMap<String, List<Map<String,Object>>>();
			pm.put("attempTraphCuidList", attempTraphCuidList);
			List<Map<String,Object>> stateList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryStateByCuidList",pm);
			List<Map<String, Object>> attempPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRoutePath", pm);
			if (stateList != null && stateList.size()>0){
				for (Map<String,Object> m : stateList){
					List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
					String attempTraphCuid = IbatisDAOHelper.getStringValue(m, "CUID");
					if (stateMap.containsKey(attempTraphCuid)){
						list = stateMap.get(attempTraphCuid);
						list.add(m);
					}else {
						list.add(m);
						stateMap.put(attempTraphCuid, list);
					}
				}
			}
			if(attempPathList != null && attempPathList.size()>0){
				for (Map<String,Object> m : attempPathList){
					List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
					String attempTraphCuid = IbatisDAOHelper.getStringValue(m, "TRAPH_CUID");
					String pathType = IbatisDAOHelper.getStringValue(m, "PATH_TYPE");
					if(pathType.equals("TRANS_PATH")){
						if (ptnPathCuidMap.containsKey(attempTraphCuid)){
							list = ptnPathCuidMap.get(attempTraphCuid);
							list.add(m);
						}else {
							list.add(m);
							ptnPathCuidMap.put(attempTraphCuid, list);
						}
					}
				}
			}

			if(stateList != null && stateList.size() > 0 ){
				for (Map<String,Object> m : stateList){
					String attempTraphCuid = IbatisDAOHelper.getStringValue(m, "CUID");
					String state = IbatisDAOHelper.getStringValue(m, "STATE");
					if(state.equals(SheetConstants.SERVICE_STATE_REPLY_FAIL)){
						List<Map<String,Object>>  ptnPathCuidList = ptnPathCuidMap.get(attempTraphCuid);
						if(ptnPathCuidList != null && ptnPathCuidList.size() > 0){
							for(Map<String,Object> mp : ptnPathCuidList){
								String failTransPathCuid = IbatisDAOHelper.getStringValue(mp, "PATH_CUID");
								attempTransPathCuidList.remove(failTransPathCuid);
								failTransPathCuidList.add(failTransPathCuid);
							}
						}
					}
				}
			}
			if(attempTransPathCuidList != null && attempTransPathCuidList.size() > 0){
				this.setTransPathState(attempTransPathCuidList, SheetConstants.SCHEDULE_STATE_RUN);
			}
			if(failTransPathCuidList != null && failTransPathCuidList.size() > 0){
				//设置施工失败的通道状态为空闲
				this.setTransPathState(failTransPathCuidList, SheetConstants.SCHEDULE_STATE_END);
			}
			
			pm.clear();
			pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
			List<Map<String, Object>> attempTransCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransCtp", pm);
			List<Map<String, Object>> attempTransPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransPtp", pm);
			
			List<String> attempTransCtpCuidList = new ArrayList<String>();
			List<String> failCtpList = new ArrayList<String>();
			
			if(attempTransCtpList != null && !attempTransCtpList.isEmpty()) {
				for(Map<String, Object> map : attempTransCtpList) {
					String aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					logger.info("-----stateMap-----");
					if(stateMap != null && !stateMap.isEmpty()){
						List<Map<String,Object>> list = stateMap.get(IbatisDAOHelper.getStringValue(map, "CUID"));
						logger.info("-----stateMap1-----");
						if(list != null && list.size()>0){
							for ( int i = 0;i<list.size();i++){
								logger.info("-----stateMap2-----");
								String state = (String) list.get(i).get("STATE");
								if (state.equals(SheetConstants.SERVICE_STATE_REPLY_FAIL)){
									if(StringUtils.isNotBlank(aCtpCuid)) failCtpList.add(aCtpCuid);
									if(StringUtils.isNotBlank(zCtpCuid)) failCtpList.add(zCtpCuid);
								}else {
									if(StringUtils.isNotBlank(aCtpCuid)) attempTransCtpCuidList.add(aCtpCuid);
									if(StringUtils.isNotBlank(zCtpCuid)) attempTransCtpCuidList.add(zCtpCuid);
								}
							}
						}
					}else{   //电路修改单
						if(StringUtils.isNotBlank(aCtpCuid)) attempTransCtpCuidList.add(aCtpCuid);
						if(StringUtils.isNotBlank(zCtpCuid)) attempTransCtpCuidList.add(zCtpCuid);
					}
				}
			}
			
			List<String> attempTransPtpCuidList = new ArrayList<String>();
			List<String> failPtpList = new ArrayList<String>();
			if(attempTransPtpList != null && !attempTransPtpList.isEmpty()) {
				for(Map<String, Object> map : attempTransPtpList) {
					String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					
					if(stateMap != null && !stateMap.isEmpty()){
						List<Map<String,Object>> list = stateMap.get(IbatisDAOHelper.getStringValue(map, "CUID"));
						if(list != null && list.size()>0){
							for ( int i = 0;i<list.size();i++){
								String state = (String) list.get(i).get("STATE");
								if (state.equals(SheetConstants.SERVICE_STATE_REPLY_FAIL)){
									if(StringUtils.isNotBlank(aPtpCuid)) failPtpList.add(aPtpCuid);
									if(StringUtils.isNotBlank(zPtpCuid)) failPtpList.add(zPtpCuid);
								}else{
									if(StringUtils.isNotBlank(aPtpCuid)) attempTransPtpCuidList.add(aPtpCuid);
									if(StringUtils.isNotBlank(zPtpCuid)) attempTransPtpCuidList.add(zPtpCuid);
								}
							}
						}
					}else{
						if(StringUtils.isNotBlank(aPtpCuid)) attempTransPtpCuidList.add(aPtpCuid);
						if(StringUtils.isNotBlank(zPtpCuid)) attempTransPtpCuidList.add(zPtpCuid);
					}
				}
			}
			
			if(attempTransPtpCuidList != null && !attempTransPtpCuidList.isEmpty()) {
				// 设置端口状态为占用
				logger.info("设置端口状态为占用");
				this.setPtpState(attempTransPtpCuidList, 2,3);
			}
			if(failPtpList != null && !failPtpList.isEmpty()) {
				logger.info("施工失败，设置端口状态为空闲");
				this.setPtpState(failPtpList, 1,3);
			}
			if(attempTransCtpCuidList != null && !attempTransCtpCuidList.isEmpty()) {
				// 设置时隙状态为占用
				logger.info("设置时隙状态为占用");
				this.setCtpState(attempTransCtpCuidList, 2);
			}
			if(failCtpList != null && !failCtpList.isEmpty()) {
				logger.info("施工失败，设置时隙状态为空闲");
				this.setCtpState(failCtpList, 1);
			}
			
			pm.clear();
			pm.put("attempTraphCuidList", attempTraphCuidList);
			logger.info("把调度电路与SDH通道的关系存入存量中");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTransPath2Traph", pm);
		}
	}*/
	
	private void insertAttempTraphTransRelation(ServiceActionContext ac, List<String> attempTransPathCuidList, List<String> attempTraphCuidList)
	  {
	    if ((attempTransPathCuidList != null) && (!attempTransPathCuidList.isEmpty())) {
	      List attempTransPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempTransPathCuidList, 1000);

	      setTransPathState(attempTransPathCuidList, SheetConstants.SCHEDULE_STATE_RUN.intValue());

	      Map pm = new HashMap();
	      pm.put("attempTransPathCuidGroup", attempTransPathCuidGroup);
	      List<Map<String, Object>> attempTransCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransCtp", pm);
	      List<Map<String, Object>> attempTransPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTransPtp", pm);

	      List attempTransCtpCuidList = new ArrayList();
	      String aCtpCuid;
	      if ((attempTransCtpList != null) && (!attempTransCtpList.isEmpty())) {
	        for (Map map : attempTransCtpList) {
	          aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
	          String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");

	          if (StringUtils.isNotBlank(aCtpCuid)) attempTransCtpCuidList.add(aCtpCuid);
	          if (!StringUtils.isNotBlank(zCtpCuid)) continue; attempTransCtpCuidList.add(zCtpCuid);
	        }
	      }

	      List attempTransPtpCuidList = new ArrayList();

	      if ((attempTransPtpList != null) && (!attempTransPtpList.isEmpty())) {
	        for (Map map : attempTransPtpList) {
	          String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
	          String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");

	          if (StringUtils.isNotBlank(aPtpCuid)) attempTransPtpCuidList.add(aPtpCuid);
	          if (!StringUtils.isNotBlank(zPtpCuid)) continue; attempTransPtpCuidList.add(zPtpCuid);
	        }
	      }

	      if ((attempTransCtpCuidList != null) && (!attempTransCtpCuidList.isEmpty()))
	      {
	        this.logger.info("设置时隙状态为占用");
	        setCtpState(attempTransCtpCuidList, 2);
	      }
	      if ((attempTransPtpCuidList != null) && (!attempTransPtpCuidList.isEmpty()))
	      {
	        this.logger.info("设置端口状态为占用");
	        setPtpState(attempTransPtpCuidList, 2, Integer.valueOf(3));
	      }

	      pm.clear();
	      pm.put("attempTraphCuidList", attempTraphCuidList);
	      this.logger.info("把调度电路与SDH通道的关系存入存量中");
	      this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTransPath2Traph", pm);
	    }
	  }
	
	/**
	 * 根据调度电路ID，往存量插入电路与PTN通道的关系
	 * @param ac
	 * @param attempPtnPathCuidList
	 */
	private void insertAttempTraphPtnRelation(ServiceActionContext ac, List<String> attempPtnPathCuidList) {
		if(attempPtnPathCuidList != null && !attempPtnPathCuidList.isEmpty()) {
			List<Map<String, List<String>>> attempPtnPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempPtnPathCuidList, 1000);
			
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.clear();
			pm.put("attempPtnPathCuidGroup", attempPtnPathCuidGroup);
			List<Map<String, Object>> attempPtnCtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnCtp", pm);
			List<Map<String, Object>> attempPtnPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnPtp", pm);
			List<Map<String, Object>> PtnPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryPtnPtp", pm);
			
			/*Map<String,List<Map<String, Object>>> stateMap = new HashMap<String, List<Map<String,Object>>>();
			pm.clear();
 			pm.put("cuidList", attempPtnPathCuidList);
 			List<Map<String,Object>> attempTraphCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphCuid",pm);
 			List<String> cuidList = new ArrayList<String>();
 			if(attempTraphCuidList != null && attempTraphCuidList.size()>0){
 				for(Map m : attempTraphCuidList){
 					String cuid = IbatisDAOHelper.getStringValue(m, "RELATED_ROUTE_CUID");
 					cuidList.add(cuid);
 				}
 			}
 			pm.clear();
 			pm.put("attempTraphCuidList", cuidList);
			List<Map<String,Object>> stateList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryStateByCuidList",pm);
			if (stateList != null && stateList.size()>0){
				for (Map<String,Object> m : stateList){
					List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
					String attempTraphCuid = IbatisDAOHelper.getStringValue(m, "CUID");
					if (stateMap.containsKey(attempTraphCuid)){
						list = stateMap.get(attempTraphCuid);
						list.add(m);
					}else {
						list.add(m);
						stateMap.put(attempTraphCuid, list);
					}
				}
			}*/

			List<String> attempPtnCtpCuidList = new ArrayList<String>();
//			List<String> failCtpList = new ArrayList<String>();
			if(attempPtnCtpList != null && !attempPtnCtpList.isEmpty()) {
				for(Map<String, Object> map : attempPtnCtpList) {
					String aCtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zCtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					if (StringUtils.isNotBlank(aCtpCuid)) attempPtnCtpCuidList.add(aCtpCuid);
			        if (!StringUtils.isNotBlank(zCtpCuid)) continue; attempPtnCtpCuidList.add(zCtpCuid);

//					if(stateMap != null && !stateMap.isEmpty()){
//						List<Map<String,Object>> list = stateMap.get(IbatisDAOHelper.getStringValue(map, "CUID"));
//						if(list != null && list.size()>0){
//							for ( int i = 0;i<list.size();i++){
//								String state = (String) list.get(i).get("STATE");
//								if (state.equals(SheetConstants.SERVICE_STATE_REPLY_FAIL)){
//									if(StringUtils.isNotBlank(aCtpCuid)) failCtpList.add(aCtpCuid);
//									if(StringUtils.isNotBlank(zCtpCuid)) failCtpList.add(zCtpCuid);
//								}else {
//									if(StringUtils.isNotBlank(aCtpCuid)) attempPtnCtpCuidList.add(aCtpCuid);
//									if(StringUtils.isNotBlank(zCtpCuid)) attempPtnCtpCuidList.add(zCtpCuid);
//								}
//							}
//						}
//					}else{  //电路修改单
//						if(StringUtils.isNotBlank(aCtpCuid)) attempPtnCtpCuidList.add(aCtpCuid);
//						if(StringUtils.isNotBlank(zCtpCuid)) attempPtnCtpCuidList.add(zCtpCuid);
//					}
				}
			}
			
			List<String> attempPtnPtpCuidList = new ArrayList<String>();
//			List<String> failPtpList = new ArrayList<String>();
			if(attempPtnPtpList != null && !attempPtnPtpList.isEmpty()) {
				for(Map<String, Object> map : attempPtnPtpList) {
					String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					String portCuid = IbatisDAOHelper.getStringValue(map, "L2L3_PORT_CUID");
					if (StringUtils.isNotBlank(aPtpCuid)) attempPtnPtpCuidList.add(aPtpCuid);
		            if (StringUtils.isNotBlank(zPtpCuid)) attempPtnPtpCuidList.add(zPtpCuid);
		            if (!StringUtils.isNotBlank(portCuid)) continue; attempPtnPtpCuidList.add(portCuid);

					/*if(stateMap != null && !stateMap.isEmpty()){
						List<Map<String,Object>> list = stateMap.get(IbatisDAOHelper.getStringValue(map, "CUID"));
						if(list != null && list.size()>0){
							for ( int i = 0;i<list.size();i++){
								String state = (String) list.get(i).get("STATE");
								if (state.equals(SheetConstants.SERVICE_STATE_REPLY_FAIL)){
									if(StringUtils.isNotBlank(aPtpCuid)) failPtpList.add(aPtpCuid);
									if(StringUtils.isNotBlank(zPtpCuid)) failPtpList.add(zPtpCuid);
									if(StringUtils.isNotBlank(portCuid)) failPtpList.add(portCuid);
								}else {
									if(StringUtils.isNotBlank(aPtpCuid)) attempPtnPtpCuidList.add(aPtpCuid);
									if(StringUtils.isNotBlank(zPtpCuid)) attempPtnPtpCuidList.add(zPtpCuid);
									if(StringUtils.isNotBlank(portCuid)) attempPtnPtpCuidList.add(portCuid);
								}
							}
						}
					}else{   //电路修改单
						if(StringUtils.isNotBlank(aPtpCuid)) attempPtnPtpCuidList.add(aPtpCuid);
						if(StringUtils.isNotBlank(zPtpCuid)) attempPtnPtpCuidList.add(zPtpCuid);
						if(StringUtils.isNotBlank(portCuid)) attempPtnPtpCuidList.add(portCuid);
					}*/
				}
			}
			List<String> ptnPtpCuidList = new ArrayList<String>();
			if(ptnPtpCuidList != null && !ptnPtpCuidList.isEmpty()) {
				for(Map<String, Object> map : attempPtnPtpList) {
					String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
					String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
					String portCuid = IbatisDAOHelper.getStringValue(map, "L2L3_PORT_CUID");
					
					if(StringUtils.isNotBlank(aPtpCuid)) ptnPtpCuidList.add(aPtpCuid);
					if(StringUtils.isNotBlank(zPtpCuid)) ptnPtpCuidList.add(zPtpCuid);
					if(StringUtils.isNotBlank(portCuid)) attempPtnPtpCuidList.add(portCuid);
				}
			}
			if(attempPtnCtpCuidList != null && !attempPtnCtpCuidList.isEmpty()) {
				// 设置时隙状态为占用
				logger.info("设置时隙状态为占用");
				this.setCtpState(attempPtnCtpCuidList, 2);
			}
			/*if(failCtpList != null && !failCtpList.isEmpty()) {
				logger.info("施工失败，设置时隙状态为空闲");
				this.setCtpState(failCtpList, 1);
			}*/
			if(ptnPtpCuidList != null && !ptnPtpCuidList.isEmpty()) {
				// 释放调整前电路端口，设置端口状态为空闲
				logger.info("释放调整前电路端口，设置端口状态为空闲");
				this.setPtpState(ptnPtpCuidList, 1,2);
			}
			if(attempPtnPtpCuidList != null && !attempPtnPtpCuidList.isEmpty()) {
				// 设置端口状态为占用
				logger.info("设置端口状态为占用");
				this.setPtpState(attempPtnPtpCuidList, 2,3);
			}
			/*if(failPtpList != null && !failPtpList.isEmpty()) {
				logger.info("施工失败，设置端口状态为空闲");
				this.setPtpState(failPtpList, 1,3);
			}*/

			pm.clear();
			pm.put("attempPtnPathCuidGroup", attempPtnPathCuidGroup);
			pm.put("type", "NETCONFIG");

			logger.info("修改IP状态");
			this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateIpWithTraph", pm);
			pm.put("type", "BUSINESS");
			
			this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateIpWithTraph", pm);
			
			logger.info("把调度PTN通道存入存量中");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempPtnPath", pm);
			logger.info("把调度PTN通道和IP的关系存入存量中");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempPtnPath2Ip", pm);
			logger.info("把调度PTN通道和路由的关系存入存量中");
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempPtnPath2StaticRoute", pm);
			
		}
	}
	 
	 /**
		 * 根据调度电路ID，往存量插入电路与MSAP通道的关系
		 * @param ac
		 * @param attempMsapPathCuidList
		 */
		private void insertAttempTraphMsapRelation(ServiceActionContext ac, List<String> attempMsapPathCuidList) {
			if(attempMsapPathCuidList != null && !attempMsapPathCuidList.isEmpty()) {
				List<Map<String, List<String>>> attempMsapPathCuidGroup = IbatisDAOHelper.pareseGroupList(attempMsapPathCuidList, 1000);
				
				Map<String, Object> pm = new HashMap<String, Object>();
				pm.clear();
				pm.put("attempMsapPathCuidGroup", attempMsapPathCuidGroup);
				List<Map<String, Object>> attempMsapPtpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempMsapPtp", pm);
				
				List<String> attempMsapPtpCuidList = new ArrayList<String>();
				
				if(attempMsapPtpList != null && !attempMsapPtpList.isEmpty()) {
					for(Map<String, Object> map : attempMsapPtpList) {
						String aPtpCuid = IbatisDAOHelper.getStringValue(map, "A_POINT_CUID");
						String zPtpCuid = IbatisDAOHelper.getStringValue(map, "Z_POINT_CUID");
						
						if(StringUtils.isNotBlank(aPtpCuid)) attempMsapPtpCuidList.add(aPtpCuid);
						if(StringUtils.isNotBlank(zPtpCuid)) attempMsapPtpCuidList.add(zPtpCuid);
					}
				}
				
				if(attempMsapPtpCuidList != null && !attempMsapPtpCuidList.isEmpty()) {
					// 设置端口状态为占用
					logger.info("设置端口状态为占用");
					this.setPtpState(attempMsapPtpCuidList, 2,3);
				}

				pm.clear();
				pm.put("attempMsapPathCuidGroup", attempMsapPathCuidGroup);
				
				logger.info("把调度MSAP通道存入存量中");
				this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempMsapPath", pm);
			}
		}
	
//	private void insertAttempPtnPath(Map<String, Object> pm)
//	  {
//	    List<Map<String, Object>> attempPtnPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempPtnPath", pm);
//	    List recordList = new ArrayList();
//	    for (Map map : attempPtnPathList) {
//	      Record record = new Record("PTN_PATH");
//	      record.addColValue("OBJECTID", IbatisDAOHelper.getStringValue(map, "OBJECTID"));
//	      record.addColValue("GT_VERSION", IbatisDAOHelper.getStringValue(map, "GT_VERSION"));
//	      record.addColValue("RELATED_A_NE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_A_NE_CUID"));
//	      record.addColValue("RELATED_A_NE_CUID2", IbatisDAOHelper.getStringValue(map, "RELATED_A_NE_CUID2"));
//	      record.addColValue("RELATED_A_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_A_PTP_CUID"));
//	      record.addColValue("RELATED_A_PTP_CUID2", IbatisDAOHelper.getStringValue(map, "RELATED_A_PTP_CUID2"));
//	      record.addColValue("RELATED_VIRTUAL_LINE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_VIRTUAL_LINE_CUID"));
//	      record.addColValue("RELATED_Z_NE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE_CUID"));
//	      record.addColValue("RELATED_Z_NE_CUID2", IbatisDAOHelper.getStringValue(map, "RELATED_Z_NE_CUID2"));
//	      record.addColValue("RELATED_Z_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_Z_PTP_CUID"));
//	      record.addColValue("RELATED_Z_PTP_CUID2", IbatisDAOHelper.getStringValue(map, "RELATED_Z_PTP_CUID2"));
//	      record.addColValue("VLANID", IbatisDAOHelper.getStringValue(map, "VLANID"));
//	      record.addColValue("DEST_SITE_TYPE", IbatisDAOHelper.getStringValue(map, "DEST_SITE_TYPE"));
//	      record.addColValue("ORIG_SITE_TYPE", IbatisDAOHelper.getStringValue(map, "ORIG_SITE_TYPE"));
//	      record.addColValue("IS_DISPACH_OPTICAL", IbatisDAOHelper.getStringValue(map, "IS_DISPACH_OPTICAL"));
//	      record.addColValue("ROUTE_INDEX", IbatisDAOHelper.getStringValue(map, "ROUTE_INDEX"));
//	      record.addColValue("LABEL_CN", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
//	      record.addColValue("ROUTE_DESCIPTION", IbatisDAOHelper.getStringValue(map, "ROUTE_DESCIPTION"));
//	      record.addColValue("CUSTOM_ROUTE_DESCIPTION", IbatisDAOHelper.getStringValue(map, "CUSTOM_ROUTE_DESCIPTION"));
//	      record.addColValue("DEST_POINT_CUID", IbatisDAOHelper.getStringValue(map, "DEST_POINT_CUID"));
//	      record.addColValue("ORIG_POINT_CUID", IbatisDAOHelper.getStringValue(map, "ORIG_POINT_CUID"));
//	      record.addColValue("RELATED_ROUTE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_ROUTE_CUID"));
//	      record.addColValue("OBJECT_TYPE_CODE", IbatisDAOHelper.getStringValue(map, "OBJECT_TYPE_CODE"));
//	      record.addColValue("CUID", IbatisDAOHelper.getStringValue(map, "CUID"));
//	      record.addColValue("ISDELETE", IbatisDAOHelper.getStringValue(map, "ISDELETE"));
//	      String createTime = (String)map.get("CREATE_TIME");
//	      Date createTimeDate = new Date();
//	      if (createTime != null) {
//	        createTimeDate = TimeFormatHelper.convertDate(createTime);
//	      }
//	      record.addColValue("CREATE_TIME", createTimeDate);
//	      record.addColValue("LAST_MODIFY_TIME", new Date());
//	      record.addColValue("ORIG_CTP_CUID", IbatisDAOHelper.getStringValue(map, "ORIG_CTP_CUID"));
//	      record.addColValue("DEST_CTP_CUID", IbatisDAOHelper.getStringValue(map, "DEST_CTP_CUID"));
//	      record.addColValue("L2L3_PORT_CUID", IbatisDAOHelper.getStringValue(map, "L2L3_PORT_CUID"));
//	      record.addColValue("L2L3_PORT_NAME", IbatisDAOHelper.getStringValue(map, "L2L3_PORT_NAME"));
//	      record.addColValue("L2L3_PORT_CUID2", IbatisDAOHelper.getStringValue(map, "L2L3_PORT_CUID2"));
//	      record.addColValue("L2L3_PORT_NAME2", IbatisDAOHelper.getStringValue(map, "L2L3_PORT_NAME2"));
//	      record.addColValue("SERVICE_PRIORITY", IbatisDAOHelper.getStringValue(map, "SERVICE_PRIORITY"));
//	      record.addColValue("QOS_BAND", IbatisDAOHelper.getStringValue(map, "QOS_BAND"));
//	      record.addColValue("CIR_BAND", IbatisDAOHelper.getStringValue(map, "CIR_BAND"));
//	      record.addColValue("PIR_BAND", IbatisDAOHelper.getStringValue(map, "PIR_BAND"));
//	      record.addColValue("PATH_TYPE", IbatisDAOHelper.getStringValue(map, "PATH_TYPE"));
//	      record.addColValue("REMARK", IbatisDAOHelper.getStringValue(map, "REMARK"));
//	      record.addColValue("BSVLANID", IbatisDAOHelper.getStringValue(map, "BSVLANID"));
//	      recordList.add(record);
//	    }
//	    this.IbatisResDAO.insertDynamicTableBatch(recordList);
//	  }
	/**
	 * 根据调度电路ID，往存量插入电路与文本段的关系
	 * @param ac
	 * @param attempTextPathCuidList
	 */
	private void insertAttempTraphTextRelation(ServiceActionContext ac, List<String> attempTextPathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempTextPathCuidList);
//		List<Map<String,Object>> attempTextPathInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getAttempTextPathInfo", pm);
//		List<Record> recordList = new ArrayList<Record>();
//		if (attempTextPathInfoList!=null&&attempTextPathInfoList.size()>0){
//			for(Map<String,Object> attempTextPathInfo : attempTextPathInfoList){
//				Record record = new Record("TEXT_PATH");
//				record.addColValue("OBJECTID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "OBJECTID"));
//				record.addColValue("OBJECT_TYPE_CODE", 12006);
//				record.addColValue("IN_OUT", 1);
//				record.addColValue("IS_PRO", 0);
//				record.addColValue("PTP_A_NAME", IbatisDAOHelper.getStringValue(attempTextPathInfo, "PTP_A_NAME"));
//				record.addColValue("PTP_Z_NAME", IbatisDAOHelper.getStringValue(attempTextPathInfo, "PTP_Z_NAME"));
//				record.addColValue("ROOM_A_NAME", IbatisDAOHelper.getStringValue(attempTextPathInfo, "ROOM_A_NAME"));
//				record.addColValue("ROOM_Z_NAME", IbatisDAOHelper.getStringValue(attempTextPathInfo, "ROOM_Z_NAME"));
//				record.addColValue("RELATED_A_PTP_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_A_PTP_CUID"));
//				record.addColValue("RELATED_Z_PTP_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_Z_PTP_CUID"));
//				record.addColValue("RELATED_A_ROOM_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_A_ROOM_CUID"));
//				record.addColValue("RELATED_Z_ROOM_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_Z_ROOM_CUID"));
//				record.addColValue("DEST_SITE_TYPE", IbatisDAOHelper.getStringValue(attempTextPathInfo, "DEST_SITE_TYPE"));
//				record.addColValue("ORIG_SITE_TYPE", IbatisDAOHelper.getStringValue(attempTextPathInfo, "ORIG_SITE_TYPE"));
//				record.addColValue("LABEL_CN", IbatisDAOHelper.getStringValue(attempTextPathInfo, "LABEL_CN"));
//				record.addColValue("ROUTE_DESCIPTION", IbatisDAOHelper.getStringValue(attempTextPathInfo, "ROUTE_DESCIPTION"));
//				record.addColValue("DEST_POINT_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "DEST_POINT_CUID"));
//				record.addColValue("ORIG_POINT_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "ORIG_POINT_CUID"));
//				record.addColValue("RELATED_ROUTE_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_ROUTE_CUID"));
//				record.addColValue("CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "CUID"));
//				record.addColValue("ISDELETE", 0);
//				String createTime = IbatisDAOHelper.getStringValue(attempTextPathInfo, "CREATE_TIME");
//				Date newCreateTime = new Date();
//				if (StringUtils.isNotEmpty(createTime)){
//					newCreateTime = TimeFormatHelper.convertDate(createTime);
//				}
//				record.addColValue("CREATE_TIME", newCreateTime);
//				record.addColValue("LAST_MODIFY_TIME", new Date());
//				record.addColValue("RELATED_A_NE_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_A_NE_CUID"));
//				record.addColValue("RELATED_Z_NE_CUID", IbatisDAOHelper.getStringValue(attempTextPathInfo, "RELATED_Z_NE_CUID"));
//				record.addColValue("IS_DISPACH_OPTICAL", 0);
//				recordList.add(record);
//			}
//		}
		logger.info("把调度文本段存入存量中");
//		this.IbatisResDAO.insertDynamicTableBatch(recordList);
		this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertAttempTextPath", pm);
	}
	
	/**
	 * 根据调度电路ID，往存量插入电路与自建段的关系
	 * @param ac
	 * @param attempSelfBuiltPathCuidList
	 */
	private void insertAttempTraphSelfBuiltRelation(ServiceActionContext ac, List<String> attempSelfBuiltPathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempSelfBuiltPathCuidList);
		
		logger.info("把调度自建段存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".insertAttempSelfBuiltPath", pm);
	}
	
	/**
	 * 根据调度电路ID，往存量插入电路与逻辑口的关系
	 * @param ac
	 * @param attempTraphCuidList
	 */
	private void insertAttempLogicPortRelation(ServiceActionContext ac, List<String> attempTraphCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		
		logger.info("把调度电路与逻辑口存入存量中");
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setLogicPortTraph", pm);
	}
	
	/**
	 * 获取路由数量
	 * @param attempTraphCuidList
	 * @return {key:attempTraphCuid}
	 */
	private Map<String, Integer> getPathNum(List<String> attempTraphCuidList) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("attempTraphCuidList", attempTraphCuidList);
		List<Map<String, Object>> attempPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRoutePath", pm);
		if(attempPathList != null && !attempPathList.isEmpty()) {
			for(Map<String, Object> map : attempPathList) {
				String attempTraphCuid = IbatisDAOHelper.getStringValue(map, "TRAPH_CUID");
				String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
				String multiPathCuid = IbatisDAOHelper.getStringValue(map, "MULTI_PATH_CUID");
				
				if(StringUtils.isBlank(pathCuid) && StringUtils.isBlank(multiPathCuid)) {
					continue;
				}
				
				Integer num = resultMap.get(attempTraphCuid);
				if(num == null) {
					num = 0;
					resultMap.put(attempTraphCuid, num);
				}
				num++;
			}
		}
		
		return resultMap;
	}
	/**
	 * 根据调度电路ID，释放电路与自建段的关系
	 * @param ac
	 * @param attempSelfBuiltPathCuidList
	 */
	public void releasePathRouteSeg(ServiceActionContext ac, List<String> attempSelfBuiltPathCuidList) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuidList", attempSelfBuiltPathCuidList);
		logger.info("删除调度自建段");
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempSelfBuiltPath", pm);
	}
	
	private void removeListNull(List<String> list){
		List<String> delList = new ArrayList<String>();
		delList.add(null);
		list.removeAll(delList);
	}
	
	/**
	 * 根据纬线ID,释放纬线已经纬线和隧道的关系
	 * @param ac
	 * @param attempPtnPathCuidList
	 */
	public void releaseAttempTraphPtnVirtualLine(ServiceActionContext ac, List<String> delVirtualLineList, List<String> ptnPathCuidList) {
		//清空list中的null
		if(ptnPathCuidList != null) {
			//ptnPathCuidList.remove(null);
			removeListNull(ptnPathCuidList);
		}
		if(delVirtualLineList != null) {
			removeListNull(delVirtualLineList);
			//delVirtualLineList.remove(null);
		}
		List<Map<String,Object>> ptnServices = this.findPtnService(ptnPathCuidList, null, delVirtualLineList);
		if(!ptnServices.isEmpty()) {
			List<String> serviceCuidList = new ArrayList<String>();
			for(Map<String,Object> map : ptnServices) {
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				long state = IbatisDAOHelper.getLongValue(map, "STATE");
				int pathCount = IbatisDAOHelper.getIntValue(map, "PATH_COUNT");
				int attempPathCount = IbatisDAOHelper.getIntValue(map, "ATTEMP_PATH_COUNT");
				String vlCuid = IbatisDAOHelper.getStringValue(map, "VIRTUALLINE_CUID");
				if(state!=1L && pathCount==0 && attempPathCount==0) {//状态不是空闲且没有关联其他电路的业务置为空闲状态
					serviceCuidList.add(cuid);
				}
				delVirtualLineList.remove(vlCuid);//关联业务的伪线不做删除操作（主要针对采集数据，全部采用ATTEMP_PTN_VIRTUAL_LINE数据不用移除伪线）
			}
			if(!serviceCuidList.isEmpty()) {
				this.setPtnServiceState(serviceCuidList, "1");//业务设置为空闲
			}
		}
		if(delVirtualLineList!=null&&delVirtualLineList.size()>0){
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put("cuidList", delVirtualLineList);
			logger.info("删除纬线");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempVirtualLine", pm);
			logger.info("删除纬线与隧道的关系");
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteAttempTunnelToVirtualLine", pm);
		}
	}
	/**
	 * 刷新电路名称
	 * @param serviceCuidList
	 */
	public void refreshAttempTraphName(List<String> serviceCuidList) {
		if (serviceCuidList == null || serviceCuidList.size() == 0) {
			throw new RuntimeException("未指定任何电路！");
		}
		List<Map<String, Object>> attempTraphList = this.findAttempTraph(serviceCuidList);
		updateAttempTraphDistrictCuid(attempTraphList);		
		for (Map<String, Object> attempTraph : attempTraphList) {			
			String cuid = IbatisDAOHelper.getStringValue(attempTraph, "CUID");
			Record rpk = new Record("ATTEMP_TRAPH");
			rpk.addColValue("CUID", cuid);
			Record r = new Record("ATTEMP_TRAPH");
			String labelCn = IbatisDAOHelper.getStringValue(attempTraph, "LABEL_CN");
			/*
			String aPoint =	IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_ZD_SITE_CUID");
			String zPoint = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_ZD_SITE_CUID");
		    */
			
			String aPoint = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_END_STATION_CUID");
		    String zPoint = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_END_STATION_CUID");

			
			/* if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
		    	aPoint = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_ZD_SITE_CUID");
		    	zPoint = IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_ZD_SITE_CUID");
		    }*/
			Integer traphRate = IbatisDAOHelper.getIntValue(attempTraph, "TRAPH_RATE");
			String extIds = IbatisDAOHelper.getStringValue(attempTraph, "EXT_IDS");				
			String aDistrictCuid= IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_DISTRICT_CUID");
			String zDistrictCuid= IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_DISTRICT_CUID");
			
			if (StringUtils.isNotEmpty(aDistrictCuid)&&StringUtils.isNotEmpty(zDistrictCuid)){
				if (aDistrictCuid.length() > 26) {
					aDistrictCuid = aDistrictCuid.substring(0, 26);
				}
				if (zDistrictCuid.length() > 26) {
					zDistrictCuid = zDistrictCuid.substring(0, 26);
				}
				//若AZ地市不同，则是二干电路
				if (!aDistrictCuid.equals(zDistrictCuid)) {
					aPoint = aDistrictCuid;
					zPoint = zDistrictCuid;
				}
			}
			
			List<String> skipAttempTraphIds = new ArrayList<String>();
			skipAttempTraphIds.add(cuid);
			List<TraphName> traphName = TraphNameBO.getTraphNames(aPoint,zPoint,traphRate,1,skipAttempTraphIds);
			TraphName name = traphName.get(0);
			String[] exts = StringUtils.split(extIds, ",");
			String ext = "0";
			if (exts != null && exts.length > 0) {
				ext = exts[0];
			}
			StringBuffer suffixBf = new StringBuffer();
			for (String suffix : TraphConstants.suffixList) {
				if (labelCn.indexOf(suffix) != -1) {
					suffixBf.append(suffix);
				}
			}
			String serviceName = this.TraphNameBO.getServiceName(extIds);
			String newLabelCn = name.getLabelCn() + serviceName + suffixBf.toString();
			String notes = IbatisDAOHelper.getStringValue(attempTraph, "NOTES");
			if (SysProperty.getInstance().getValue("districtName").trim().equals("内蒙")
					||SysProperty.getInstance().getValue("districtName").trim().equals("青海")){
				if (StringUtils.isNotEmpty(notes)){
					newLabelCn = newLabelCn + "(" + notes + ")";
				}
			}
			if(SysProperty.getInstance().getValue("districtName").trim().equals("重庆")){
				Integer useType = IbatisDAOHelper.getIntValue(attempTraph, "USE_TYPE");
				if (useType == 2){
					newLabelCn = newLabelCn + "/NP";
				} 
			}
			r.addColValue("LABEL_CN", newLabelCn);
			r.addColValue("NO",name.getNo());
			IbatisResDAO.updateDynamicTable(r, rpk);
		}
	}
	/**
	 * 若站点更新后,需要刷调度中电路对应的A、Z端站点区域,所以无论是否更新,都检查一下,保持站点对应的区域正确
	 * @param serviceCuidList
	 */
	private void updateAttempTraphDistrictCuid(List<Map<String, Object>> attempTraphList){		
		List<Record> pkList=new ArrayList<Record>();
		List<Record> paraList=new ArrayList<Record>();
		List<String> SiteCuidList=new ArrayList<String>();
		List<String> attempCuidList=new ArrayList<String>();
		Map<String,Map<String,Object>> mapSite2Space=new HashMap<String,Map<String,Object>>();
		for (Map<String, Object> attempTraph : attempTraphList) {
			SiteCuidList.add(IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_ZD_SITE_CUID")); 
			SiteCuidList.add(IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_ZD_SITE_CUID")); 
			attempCuidList.add(IbatisDAOHelper.getStringValue(attempTraph, "CUID"));
		}
		String attempCuidStr="";
		for(String str:attempCuidList){
			attempCuidStr+="'"+str+"',";
		}
		Map mp = new HashMap();
		mp.put("cuidList", SiteCuidList);
		List<Map<String, Object>> listSite = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getAZEndStaction", mp);
		if(listSite.size()>0){
			mapSite2Space = IbatisDAOHelper.parseList2Map(listSite, "CUID");
		}
		for (Map<String, Object> attempTraph : attempTraphList) {
			 String cuid=IbatisDAOHelper.getStringValue(attempTraph, "CUID");
			 String siteACuid=IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_ZD_SITE_CUID");
			 String spaceACuid=IbatisDAOHelper.getStringValue(attempTraph, "RELATED_A_DISTRICT_CUID");
			 String siteZCuid=IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_ZD_SITE_CUID");
			 String spaceZCuid=IbatisDAOHelper.getStringValue(attempTraph, "RELATED_Z_DISTRICT_CUID");
			 String MapASpace=IbatisDAOHelper.getStringValue(mapSite2Space.get(siteACuid), "DISTRICT_CUID");
			 String MapZSpace=IbatisDAOHelper.getStringValue(mapSite2Space.get(siteZCuid), "DISTRICT_CUID");
			 if(StringUtils.isEmpty(spaceACuid)||!spaceACuid.equals(MapASpace)){
				 Record rec=new Record("ATTEMP_TRAPH");
				 rec.addColValue("RELATED_A_DISTRICT_CUID", MapASpace);
				 paraList.add(rec);
				 attempTraph.put("RELATED_A_DISTRICT_CUID", MapASpace);
				 Record pkRec=new Record("ATTEMP_TRAPH");
				 pkRec.addColValue("CUID", cuid);
				 pkList.add(pkRec);
			 }
			 if(StringUtils.isEmpty(spaceZCuid)||!spaceZCuid.equals(MapZSpace)){
				 Record rec=new Record("ATTEMP_TRAPH");
				 rec.addColValue("RELATED_Z_DISTRICT_CUID", MapZSpace);
				 paraList.add(rec);
				 attempTraph.put("RELATED_Z_DISTRICT_CUID", MapZSpace);
				 Record pkRec=new Record("ATTEMP_TRAPH");
				 pkRec.addColValue("CUID", cuid);
				 pkList.add(pkRec);
			 }
			 
		}			
		if(pkList.size()>0){
		    this.IbatisResDAO.updateDynamicTableBatch(paraList, pkList);
		}
		String delSql="delete from T_TASK_TO_SERVICE_LINK where related_service_cuid in("+attempCuidStr.substring(0,attempCuidStr.length()-1)+")";
		this.IbatisResDAO.deleteSql(delSql);
		this.setDefaultConstructRole(attempCuidList);
		
	}
	public List<Map<String, Object>> getActSheetByTaskId(String taskId) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", taskId);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getActSheetByTaskId", pm);
		
		return list;
	}
	public List<Map<String, Object>> getSiteByCuid(String origOrdestsiteCuid) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", origOrdestsiteCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSiteByCuid", pm);
		
		return list;
	}
	public List<Map<String, Object>> getRoomByCuid(String origOrdestsiteCuid) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", origOrdestsiteCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getRoomByCuid", pm);
		
		return list;
	}
	public List<Map<String, Object>> getTransElementByCuid(String origOrdestsiteCuid) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", origOrdestsiteCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTransElementByCuid", pm);
		
		return list;
	}
	public List<Map<String, Object>> getDistrictByCuid(String origOrdestsiteCuid) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", origOrdestsiteCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getDistrictByCuid", pm);
		
		return list;
	}
	public List<Map<String, Object>> getSwitchElementByCuid(String origOrdestsiteCuid) {
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", origOrdestsiteCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSwitchElementByCuid", pm);
		
		return list;
	}
	public String getRequestDate(String cuid){
		String requestDate = "";
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("cuid", cuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getRequestDate", pm);
		for(Map<String, Object>  exists : list) {
			requestDate = IbatisDAOHelper.getStringValue(exists, "DUE_TIME");//工单编号
		}
		return requestDate;
	}
	
	public  List<Map<String,Object>> getTraphExtValue(List<String> extList){
		Map<String,Object> pm=new HashMap<String,Object>();
		pm.put("extList",extList );
		List<Map<String,Object>> list=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTraphExtValue", pm);
		return list;
	}
	
	public List<Map<String, Object>> findTraphByRelatedTraphCuid (List<String> relatedTraphCuidList){
		Map<String,Object> pm=new HashMap<String,Object>();
		pm.put("relatedTraphCuidList",relatedTraphCuidList );
	    List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findTraphByRelatedTraphCuid", pm);
	    return list;
	}
	/**
	 * 查询伪线相关业务（只查询以太网业务）
	 * @param ptnPathCuidList 排除的调前电路路由段
	 * @param attempPtnPathCuidList 排除的调后电路路由段
	 * @param virtualLineCuids
	 * @return
	 */
	public List<Map<String,Object>> findPtnService(List<String> ptnPathCuidList, List<String> attempPtnPathCuidList, List<String> virtualLineCuids) {
		Map<String, Object> pm = new HashMap<String, Object>();
		List<Map<String,Object>> ptnServices = new ArrayList<Map<String,Object>>();
		if(ptnPathCuidList!=null && ptnPathCuidList.contains(null)) {
			//ptnPathCuidList.remove(null);
			removeListNull(ptnPathCuidList);
		}
		if(attempPtnPathCuidList!=null && attempPtnPathCuidList.contains(null)) {
			//attempPtnPathCuidList.remove(null);
			removeListNull(attempPtnPathCuidList);
		}
		if(virtualLineCuids!=null && virtualLineCuids.contains(null)) {
			//virtualLineCuids.remove(null);
			removeListNull(virtualLineCuids);
		}
		if(virtualLineCuids!=null && !virtualLineCuids.isEmpty()){
			pm.put("ptnPathCuidList", ptnPathCuidList);
			pm.put("attempPtnPathCuidList", attempPtnPathCuidList);
			pm.put("virtualLineCuids", virtualLineCuids);
			ptnServices = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPtnEth", pm);
		}
		return ptnServices;
	}
	/**
	 * 设置PTN业务的状态
	 * @param serviceCuidList
	 * @param state 1：空闲，2：占用，3：预占
	 */
	public void setPtnServiceState(List<String> serviceCuidList, String state) {
		//更新业务状态
		if(serviceCuidList.size() > 0) {
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put("serviceCuidList", serviceCuidList);
			pm.put("state", state);//设置业务状态
			this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".setPtnEthState", pm);
		}
	}
	public Map<String,String> getAZEndStaction(String aSite,String zSite){
		Map<String,String> map=new HashMap<String,String>();
		Map<String, Object> pm = new HashMap<String, Object>();
		List list=new ArrayList();
		if(StringUtils.isNotEmpty(aSite)&&aSite.indexOf("DISTRICT")>=0){
			map.put("atype", "DISTRICT");
			map.put("ztype", "DISTRICT");
			map.put("aSite", aSite);
			map.put("zSite", zSite);
		}else{
			if(StringUtils.isNotEmpty(aSite)){
				list.add(aSite);
			}
			if(StringUtils.isNotEmpty(zSite)){
				list.add(zSite);
			}
			if(list.size()>0){
				pm.put("cuidList", list);
				List<Map<String, Object>> dataList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getAZEndStaction", pm);
				String aSiteCuid="";
				String zSiteCuid="";
				String aDistrictCuid="";
				String zDistrictCuid="";
				String subADistrictCuid="";
				String subZDistrictCuid="";
				for(int i=0;i<dataList.size();i++){
					Map data=dataList.get(i);
					String cuid=IbatisDAOHelper.getStringValue(data, "CUID");
					if(cuid.equals(aSite) && cuid.contains("ROOM")){
						aSiteCuid=IbatisDAOHelper.getStringValue(data, "CUID");
						aDistrictCuid=IbatisDAOHelper.getStringValue(data, "DISTRICT_CUID");
					}else if(cuid.equals(aSite)){
						aSiteCuid=IbatisDAOHelper.getStringValue(data, "SITE_CUID");
						aDistrictCuid=IbatisDAOHelper.getStringValue(data, "DISTRICT_CUID");
					}
					if(cuid.equals(zSite) && cuid.contains("ROOM")){
						zSiteCuid=IbatisDAOHelper.getStringValue(data, "CUID");
						zDistrictCuid=IbatisDAOHelper.getStringValue(data, "DISTRICT_CUID");
					}else if(cuid.equals(zSite)){
						zSiteCuid=IbatisDAOHelper.getStringValue(data, "SITE_CUID");
						zDistrictCuid=IbatisDAOHelper.getStringValue(data, "DISTRICT_CUID");
					}
				}
				if(StringUtils.isNotEmpty(aDistrictCuid)&&StringUtils.isNotEmpty(zDistrictCuid)){
					if(aDistrictCuid.length()>26){
						subADistrictCuid = aDistrictCuid.substring(0,26);
					}else{
						subADistrictCuid = aDistrictCuid;
					}
					if (zDistrictCuid.length()>26){
						subZDistrictCuid = zDistrictCuid.substring(0,26);
					}else{
						subZDistrictCuid = zDistrictCuid;
					}
					if(subADistrictCuid.equals(subZDistrictCuid)){
//						if(aSiteCuid!=null && zSiteCuid!=null){
						if (StringUtils.isEmpty(aSiteCuid)) {
				              throw new RuntimeException("A端终端点未归属！");
				            }
			            if (StringUtils.isEmpty(zSiteCuid)) {
			              throw new RuntimeException("Z端终端点未归属！");
			            }
						if(aSiteCuid.contains("ROOM")){
							map.put("atype", "ROOM");
							map.put("aSite", aSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
						}
						if(zSiteCuid.contains("ROOM")){
							map.put("ztype", "ROOM");
							map.put("zSite", zSiteCuid);
							map.put("zDistrictCuid", zDistrictCuid);
						}
						if(aSiteCuid.contains("SITE_RESOURCE")){
							map.put("atype", "ACCESSPOINT");
							map.put("aSite", aSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
						}else if(aSiteCuid.contains("SITE")){
							map.put("atype", "SITE");
							map.put("aSite", aSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
						}
						if(zSiteCuid.contains("SITE_RESOURCE")){        //吉林资源点表数据可能是SITE_RESOURCE-XXXX，也可能是ACCESSPOINT-XXXX
							map.put("ztype", "ACCESSPOINT");
							map.put("zSite", zSiteCuid);
							map.put("zDistrictCuid", zDistrictCuid);
						}else if (zSiteCuid.contains("SITE")){
							map.put("ztype", "SITE");
							map.put("zSite", zSiteCuid);
							map.put("zDistrictCuid", zDistrictCuid);
					    }
					    if(aSiteCuid.contains("ACCESSPOINT")){
							map.put("atype", "ACCESSPOINT");
							map.put("aSite", aSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
						}
					    if (zSiteCuid.contains("ACCESSPOINT")){
							map.put("ztype", "ACCESSPOINT");
							map.put("zSite", zSiteCuid);
							map.put("zDistrictCuid", zDistrictCuid);
					    }
						if(!aSiteCuid.contains("ROOM")&&!zSiteCuid.contains("ROOM")&&!aSiteCuid.contains("SITE")&&!zSiteCuid.contains("SITE")
								&&!aSiteCuid.contains("ACCESSPOINT")&&!zSiteCuid.contains("ACCESSPOINT")){
							map.put("atype", "SITE");
							map.put("ztype", "SITE");
							map.put("aSite", aSiteCuid);
							map.put("zSite", zSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
							map.put("zDistrictCuid", zDistrictCuid);
						}
//						}
					}else{
						if (SysProperty.getInstance().getValue("districtName").trim().equals("重庆")){
							map.put("atype", "SITE");
							map.put("ztype", "SITE");
							map.put("aSite", aSiteCuid);
							map.put("zSite", zSiteCuid);
							map.put("aDistrictCuid", aDistrictCuid);
							map.put("zDistrictCuid", zDistrictCuid);
						}else{
							map.put("atype", "DISTRICT");
							map.put("ztype", "DISTRICT");
							map.put("aSite", subADistrictCuid);
							map.put("zSite", subZDistrictCuid);
							map.put("aDistrictCuid", aDistrictCuid);
							map.put("zDistrictCuid", zDistrictCuid);
						}
					}
				}
			}
		}
		return map;
	}
	
	public void formartDataForRecordList(List<Record> recordList){
		List<Map<String, String>> roomMapList=new ArrayList<Map<String,String>>();
		List<Map<String, String>> neMapList=new ArrayList<Map<String,String>>();
		List<String> roomList=new ArrayList<String>();
		List<String> neList=new ArrayList<String>();
		for(int i=0;i<recordList.size();i++){
			Record traph=recordList.get(i);
			String aCuid=(String) traph.getColValue("RELATED_A_SITE_CUID");
			String zCuid=(String) traph.getColValue("RELATED_Z_SITE_CUID");
			if(aCuid.indexOf("ROOM")>=0){
				roomList.add(aCuid);
			}else if(aCuid.indexOf("TRANS_ELEMENT")>=0){
				neList.add(aCuid);
			}
			if(zCuid.indexOf("ROOM")>=0){
				roomList.add(zCuid);
			}else if(zCuid.indexOf("TRANS_ELEMENT")>=0){
				neList.add(zCuid);
			}
		}
		if(neList.size()>0){
			Map pm=new HashMap();
			pm.put("cuidList", neList);
			neMapList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getNeLocation", pm);
		}
		if(roomList.size()>0){
			Map pm=new HashMap();
			pm.put("cuidList", roomList);
			pm = Property.getIsModelOne(pm);
			roomMapList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getRoomLocation", pm);
		}
		for(int i=0;i<recordList.size();i++){
			Record traph=recordList.get(i);
			String aCuid=(String) traph.getColValue("RELATED_A_SITE_CUID");
			String zCuid=(String) traph.getColValue("RELATED_Z_SITE_CUID");
			if(aCuid.indexOf("ROOM")>=0){
				String site="";
				for(int j=0;j<roomMapList.size();j++){
					Map<String,String> roomMap=roomMapList.get(j);
					if(roomMap.get("CUID").equals(aCuid)){
						site=roomMap.get("SITE_CUID");
						break;
					}
				}
				traph.addColValue("RELATED_A_SITE_CUID", site);
				traph.addColValue("RELATED_A_ROOM_CUID", aCuid);
				traph.addColValue("RELATED_A_END_ROOM_CUID", aCuid);
				traph.addColValue("RELATED_A_END_SITE_CUID", site);
				traph.addColValue("RELATED_A_ZD_SITE_CUID", site);
			}else if(aCuid.indexOf("TRANS_ELEMENT")>=0){
				String site="";
				String room="";
				for(int j=0;j<neMapList.size();j++){
					Map<String,String> neMap=neMapList.get(j);
					if(neMap.get("CUID").equals(aCuid)){
						site=neMap.get("SITE_CUID");
						room=neMap.get("ROOM_CUID");
						break;
					}
				}
				traph.addColValue("RELATED_A_SITE_CUID", site);
				traph.addColValue("RELATED_A_ROOM_CUID", room);
				traph.addColValue("RELATED_A_END_ROOM_CUID", room);
				traph.addColValue("RELATED_A_END_SITE_CUID", site);
				traph.addColValue("RELATED_A_ZD_SITE_CUID", site);
				traph.addColValue("RELATED_NE_A_CUID", aCuid);
			}
			
			
			if(zCuid.indexOf("ROOM")>=0){
				String site="";
				for(int j=0;j<roomMapList.size();j++){
					Map<String,String> roomMap=roomMapList.get(j);
					if(roomMap.get("CUID").equals(zCuid)){
						site=roomMap.get("SITE_CUID");
						break;
					}
				}
				traph.addColValue("RELATED_Z_SITE_CUID", site);
				traph.addColValue("RELATED_Z_ROOM_CUID", zCuid);
				traph.addColValue("RELATED_Z_END_ROOM_CUID", zCuid);
				traph.addColValue("RELATED_Z_END_SITE_CUID", site);
				traph.addColValue("RELATED_Z_ZD_SITE_CUID", site);
			}else if(zCuid.indexOf("TRANS_ELEMENT")>=0){
				String site="";
				String room="";
				for(int j=0;j<neMapList.size();j++){
					Map<String,String> neMap=neMapList.get(j);
					if(neMap.get("CUID").equals(zCuid)){
						site=neMap.get("SITE_CUID");
						room=neMap.get("ROOM_CUID");
						break;
					}
				}
				traph.addColValue("RELATED_Z_SITE_CUID", site);
				traph.addColValue("RELATED_Z_ROOM_CUID", room);
				traph.addColValue("RELATED_Z_END_ROOM_CUID", room);
				traph.addColValue("RELATED_Z_END_SITE_CUID", site);
				traph.addColValue("RELATED_Z_ZD_SITE_CUID", site);
				traph.addColValue("RELATED_NE_Z_CUID", zCuid);
			}
		}
	}
	
	private void formartData(List<AttempTraphService> attempTraphList){
		List<Map<String, String>> roomMapList=new ArrayList<Map<String,String>>();
		List<Map<String, String>> neMapList=new ArrayList<Map<String,String>>();
		List<String> roomList=new ArrayList<String>();
		List<String> neList=new ArrayList<String>();
		for(int i=0;i<attempTraphList.size();i++){
			AttempTraphService traph=attempTraphList.get(i);
			String aCuid=IbatisDAOHelper.getStringValue(traph.getDataMap(), "RELATED_A_SITE_CUID");
			String zCuid=IbatisDAOHelper.getStringValue(traph.getDataMap(), "RELATED_Z_SITE_CUID");
			if(StringUtils.isNotEmpty(aCuid)&&StringUtils.isNotEmpty(zCuid)){
				if(aCuid.indexOf("ROOM")>=0){
					roomList.add(aCuid);
				}else if(aCuid.indexOf("TRANS_ELEMENT")>=0){
					neList.add(aCuid);
				}
				if(zCuid.indexOf("ROOM")>=0){
					roomList.add(zCuid);
				}else if(zCuid.indexOf("TRANS_ELEMENT")>=0){
					neList.add(zCuid);
				}
			}
		}
		if(neList.size()>0){
			Map pm=new HashMap();
			pm.put("cuidList", neList);
			neMapList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getNeLocation", pm);
		}
		if(roomList.size()>0){
			Map pm=new HashMap();
			pm.put("cuidList", roomList);
			pm = Property.getIsModelOne(pm);
			roomMapList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getRoomLocation", pm);
		}
		for(int i=0;i<attempTraphList.size();i++){
			AttempTraphService traph=attempTraphList.get(i);
			String aCuid=IbatisDAOHelper.getStringValue(traph.getDataMap(), "RELATED_A_SITE_CUID");
			String zCuid=IbatisDAOHelper.getStringValue(traph.getDataMap(), "RELATED_Z_SITE_CUID");
			if(StringUtils.isNotEmpty(aCuid)&&StringUtils.isNotEmpty(zCuid)){
				if(aCuid.indexOf("ROOM")>=0){
					String site="";
					for(int j=0;j<roomMapList.size();j++){
						Map<String,String> roomMap=roomMapList.get(j);
						if(roomMap.get("CUID").equals(aCuid)){
							site=roomMap.get("SITE_CUID");
							break;
						}
					}
					traph.addData("RELATED_A_SITE_CUID", site);
					traph.addData("RELATED_A_ROOM_CUID", aCuid);
					traph.addData("RELATED_A_END_ROOM_CUID", aCuid);
					traph.addData("RELATED_A_END_SITE_CUID", site);
					traph.addData("RELATED_A_ZD_SITE_CUID", site);
				}else if(aCuid.indexOf("TRANS_ELEMENT")>=0){
					String site="";
					String room="";
					for(int j=0;j<neMapList.size();j++){
						Map<String,String> neMap=neMapList.get(j);
						if(neMap.get("CUID").equals(aCuid)){
							site=neMap.get("SITE_CUID");
							room=neMap.get("ROOM_CUID");
							break;
						}
					}
					traph.addData("RELATED_A_SITE_CUID", site);
					traph.addData("RELATED_A_ROOM_CUID", room);
					traph.addData("RELATED_A_END_ROOM_CUID", room);
					traph.addData("RELATED_A_END_SITE_CUID", site);
					traph.addData("RELATED_A_ZD_SITE_CUID", site);
					traph.addData("RELATED_NE_A_CUID", aCuid);
				}
				
				
				if(zCuid.indexOf("ROOM")>=0){
					String site="";
					for(int j=0;j<roomMapList.size();j++){
						Map<String,String> roomMap=roomMapList.get(j);
						if(roomMap.get("CUID").equals(zCuid)){
							site=roomMap.get("SITE_CUID");
							break;
						}
					}
					traph.addData("RELATED_Z_SITE_CUID", site);
					traph.addData("RELATED_Z_ROOM_CUID", zCuid);
					traph.addData("RELATED_Z_END_ROOM_CUID", zCuid);
					traph.addData("RELATED_Z_END_SITE_CUID", site);
					traph.addData("RELATED_Z_ZD_SITE_CUID", site);
				}else if(zCuid.indexOf("TRANS_ELEMENT")>=0){
					String site="";
					String room="";
					for(int j=0;j<neMapList.size();j++){
						Map<String,String> neMap=neMapList.get(j);
						if(neMap.get("CUID").equals(zCuid)){
							site=neMap.get("SITE_CUID");
							room=neMap.get("ROOM_CUID");
							break;
						}
					}
					traph.addData("RELATED_Z_SITE_CUID", site);
					traph.addData("RELATED_Z_ROOM_CUID", room);
					traph.addData("RELATED_Z_END_ROOM_CUID", room);
					traph.addData("RELATED_Z_END_SITE_CUID", site);
					traph.addData("RELATED_Z_ZD_SITE_CUID", site);
					traph.addData("RELATED_NE_Z_CUID", zCuid);
				}
			}
		}
	}
	
	//add by luoshuyun
	public List<Map> getTraphByLabelCn(String traphName) {
		Map<String,Object> pm=new HashMap<String,Object>();
		pm.put("labelCn", traphName);
		List<Map> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getTraphByLabelCn", pm);
		return list;		
	}
	
	public void changeTraphNamebyEndStation(String attempcuid,Record record,String asite,String zsite,String oldLbael,String pathType,List<Integer> existsNo){
		//检查AZ终端点是否发生变化
		Map<String, Object> attempTraph =getAttempTraph(attempcuid);
		String endStationCuid = (String)attempTraph.get("RELATED_A_END_STATION_CUID");
		logger.info("-------------endStationCuid:"+endStationCuid);
		String extIds = (String) attempTraph.get("EXT_IDS");
		Map<String,String> map = this.getAZEndStaction(asite,zsite);
		HashMap hmap = (HashMap) attempTraph.get("TRAPH_RATE");
		Integer rate = Integer.parseInt( hmap.get("value").toString());
		if (StringUtils.isNotEmpty(endStationCuid)&&endStationCuid.indexOf("SWITCH_ELEMENT")>=0){
			asite = endStationCuid;
			zsite = (String)attempTraph.get("RELATED_Z_END_STATION_CUID");
			logger.info("-------------zsite:"+zsite);
			record.addColValue("END_STATION_TYPE_A","SWITCHELEMENT");
			record.addColValue("END_STATION_TYPE_Z","SWITCHELEMENT");
		}else{
			asite=map.get("aSite");
			zsite=map.get("zSite");
		}
		
		List<String> cuidList=new ArrayList<String>();
		cuidList.add(attempcuid);
		TraphName name =TraphNameBO.getTraphNamesWithOutValidate(asite, zsite, rate,Integer.valueOf(1), extIds, cuidList,oldLbael,existsNo);
		int no =  name.getNo();
		String traphname = name.getLabelCn();
		if(StringUtils.isNotEmpty(pathType)&&pathType.equalsIgnoreCase("ATTEMP_PTN_PATH")){
			traphname = traphname +"/P";
		}
		Map pm=new HashMap();
		if(asite==null||zsite==null||traphname==null){
			throw new RuntimeException("ATTEMP明细为空！");
		}
		if("DISTRICT".equals(map.get("atype"))){
			record.addColValue("RELATED_A_DISTRICT_CUID",asite);
			record.addColValue("RELATED_Z_DISTRICT_CUID",zsite);
			if (StringUtils.isNotEmpty(endStationCuid)&&endStationCuid.indexOf("SWITCH_ELEMENT")>=0){
			}else{
				record.addColValue("END_STATION_TYPE_A",map.get("atype"));
				record.addColValue("END_STATION_TYPE_Z",map.get("ztype"));
			}
		}
		record.addColValue("LABEL_CN", traphname);
		if (StringUtils.isNotEmpty(endStationCuid)&&endStationCuid.indexOf("SWITCH_ELEMENT")>=0){
		}else{
			record.addColValue("RELATED_A_END_STATION_CUID",asite);
			record.addColValue("RELATED_Z_END_STATION_CUID",zsite);
		}
		
		record.addColValue("NO",no);
	}
    //保存电路AZ端实际落地点
	public void saveAZEndPtpInfo(ServiceDesignPath service) {
		String attempTraphCuid = service.getCuid();
		Record pk = new Record("T_EXT_ATTEMP_TRAPH");
		pk.addColValue("CUID", attempTraphCuid);
		Record record = new Record("T_EXT_ATTEMP_TRAPH");
		List<PathData> pathDataList = service.getNewPathList();
		boolean flag = false;
		for (PathData pathData :  pathDataList){
			if (pathData.getPathType() != null && !pathData.getPathType().equals("ATTEMP_TEXT_PATH")){
				flag = true;
			}
		}
		if (flag) {
			for (PathData pathData :  pathDataList){
				if (pathData != null && (!"ATTEMP_TEXT_PATH".equals(pathData.getPathType()))){
					Map pathDataMap = pathData.getData();
					if (StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(pathDataMap, "A_PTP_CUID"))){
						record.addColValue("RELATED_PTP_A_CUID", IbatisDAOHelper.getStringValue(pathDataMap, "A_PTP_CUID"));
					}
					if (StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(pathDataMap, "A_CTP_CUID"))){
						record.addColValue("RELATED_CTP_A_CUID", IbatisDAOHelper.getStringValue(pathDataMap, "A_CTP_CUID"));
					}
					break;
				}
			}
			for (int i = pathDataList.size()-1;i>=0;i--){
				if (pathDataList.get(i)!= null &&(!"ATTEMP_TEXT_PATH".equals(pathDataList.get(i).getPathType()))){
					Map pathDataMap = pathDataList.get(i).getData();
					if (StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(pathDataMap, "Z_PTP_CUID"))){
						record.addColValue("RELATED_PTP_Z_CUID", IbatisDAOHelper.getStringValue(pathDataMap, "Z_PTP_CUID"));
					}
					if (StringUtils.isNotEmpty(IbatisDAOHelper.getStringValue(pathDataMap, "A_CTP_CUID"))){
						record.addColValue("RELATED_CTP_Z_CUID", IbatisDAOHelper.getStringValue(pathDataMap, "Z_CTP_CUID"));
					}
					break;
				}
			}
			if (record.getValueMap() != null && record.getValueMap().size()>0){
				this.IbatisResDAO.updateDynamicTable(record, pk);
			}
			
		}
	}
	
	//根据路由设计方式自动计算电路承载方式
	public void updateExtType(List<PathData> newPathList,
			Record traphUpdateRecord) {
		String extType = "";
		List<String> extTypeList = new ArrayList<String>();
		if (newPathList != null && newPathList.size()>0){
			for (PathData pathData :  newPathList){
				if(pathData.getMultPathList()!= null && pathData.getMultPathList().size()>0){
					extType = "2";
				}else if (StringUtils.isNotBlank(pathData.getCode())&&pathData.getCode().equals(PathDesignConstants.MODE_CODE_PORT)
						){//端口
					if(pathData.getDataString("A_MAC")!= null || pathData.getDataString("A_MP")!= null ||
							   pathData.getDataString("Z_MAC")!= null || pathData.getDataString("Z_MP")!= null){
						extType = "2";//MSTP
					}else{
						extType = "1";//SDH
					}
				}else if (StringUtils.isNotBlank(pathData.getCode())&&(pathData.getCode().equals(PathDesignConstants.MODE_CODE_CHAN) //通道
						|| pathData.getCode().equals(PathDesignConstants.MODE_CODE_OTN))){//波分
					List<String> transElementList = new ArrayList<String>();
					if (StringUtils.isNotEmpty(pathData.getDataString("A_NE_CUID"))){
						transElementList.add(pathData.getDataString("A_NE_CUID"));
					}
					if (StringUtils.isNotEmpty(pathData.getDataString("Z_NE_CUID"))){
					    transElementList.add(pathData.getDataString("Z_NE_CUID"));	
					}
					List<String> transElementTypeList = new ArrayList<String>();
					Map pm = new HashMap();
					if(pathData.getDataString("RELATED_TRANS_PATH_CUID")!= null){
						pm.put("pathCuid", pathData.getDataString("RELATED_TRANS_PATH_CUID"));
						List<String> pathTypeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryPathTypeBypathCuid",pm);
						if (pathTypeList.get(0).equals("1")){
							extType = "1";
						}else if (pathTypeList.get(0).equals("3")){
							extType = "2";
						}else if (pathTypeList.get(0).equals("2")){
							pm.clear();
							pm.put("transElementList", transElementList);
							transElementTypeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".querytransElementTypeByCuid",pm);
						}
					}else{
						pm.put("transElementList", transElementList);
						transElementTypeList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".querytransElementTypeByCuid",pm);
					}
					if (transElementTypeList != null && transElementTypeList.size()>0){
						boolean flag = false;//WDM
						for (String transElementType : transElementTypeList){
							if (transElementType.equals("11")){
								flag = true;//OTN
								break;
							}
						}
						if (flag){
							extType = "4";
						}else {
							extType = "3";
						}
					}
				}else if (StringUtils.isNotBlank(pathData.getCode())&&pathData.getCode().equals(PathDesignConstants.MODE_CODE_PTN)){//ptn
					extType = "5";//ptn
				}else if (StringUtils.isNotBlank(pathData.getCode())&&pathData.getCode().equals(PathDesignConstants.MODE_CODE_PTN_LTE3)){//ptn_lte3
					extType = "7";//PTN-L3
				}else if (StringUtils.isNotBlank(pathData.getCode())&&pathData.getCode().equals(PathDesignConstants.MODE_CODE_TEXT)){//文本
					extType = "6";//文本
				}
				if (!extTypeList.contains(extType)){
					extTypeList.add(extType);
				}
			}
			for (int i = 0;i<extTypeList.size();i++){
				if (i==0){
					extType = extTypeList.get(i);
				}else{
					extType += ","+ extTypeList.get(i);
				}
			}
			traphUpdateRecord.addColValue("EXT_TYPE", extType);
		}
	}
	
	public void saveUpSiteInfo(List<Map> upSiteInfoList){
//		public void saveUpSiteInfo(List<Map> upSiteInfoList,String userDistrictId){
		if(upSiteInfoList != null && upSiteInfoList.size()>0){
			List<String> traphIds = new ArrayList<String>();
			for (Map map : upSiteInfoList) {
		        String traphId = (String)map.get("RELATED_SERVICE_CUID");
		        if ((StringUtils.isNotEmpty(traphId)) && (!traphIds.contains(traphId))) {
		          traphIds.add(traphId);
		        }
			}
			if(traphIds != null && traphIds.size()>0){
				/*if(traphIds.size()>1){
					throw new RuntimeException("只能选择一条电路设计保存");
				}
				//任务名称
				String taskLabelCn = null;
				Map map = new HashMap();
				map.put("serviceCuid",traphIds.get(0));
				List<Map<String,Object>> taskList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphDesign"+".getTaskByServiceCuid", map);
				if(taskList != null && taskList.size() > 0){
					taskLabelCn = IbatisDAOHelper.getStringValue(taskList.get(0), "LABEL_CN");
				}
				//多级设计环节判断AZ能否修改上端站信息
				map.clear();
				map.put("cuid",traphIds.get(0));
				List<Map<String,Object>> traphInfos = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraph",map);
				Map<String,Object> traphInfo=traphInfos.get(0);
				String aSiteDistrict=IbatisDAOHelper.getStringValue(traphInfo, "RELATED_A_DISTRICT_SITE").substring(0,26);
				String zSiteDistrict=IbatisDAOHelper.getStringValue(traphInfo, "RELATED_Z_DISTRICT_SITE").substring(0,26);
				
				
				map.clear();
				map.put("serviceCuid", traphIds.get(0));
				List<Map<String,Object>> existUpSiteInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryExistUpSiteInfoList",map);
				
				List<Map<String, Object>> oldUpsiteInfo = this.getRelatedTpByTraph(traphIds.get(0));				
				
				
				if(existUpSiteInfoList!=null && existUpSiteInfoList.size()>0){
					List<String> existUpSiteInfoCuidList = new ArrayList<String>();
					for(Map<String,Object> existUpSiteInfoMap : existUpSiteInfoList){
						if(!existUpSiteInfoCuidList.contains((String)existUpSiteInfoMap.get("CUID"))){
							existUpSiteInfoCuidList.add((String)existUpSiteInfoMap.get("CUID"));
						}
					}
					//先删除原有存在信息
					map.put("existUpSiteInfoCuidList", existUpSiteInfoCuidList);
			    	this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteExistUpSiteInfoList",map);
			    }
				*/
		        Map mp = new HashMap();
		        mp.put("traphIds", traphIds);
		        List existUpSiteInfoCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphMaintain.queryExistUpSiteInfoList", mp);
		        List attempTraphInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphMaintain.queryAttempTraphInfoList", mp);
		        Map attempTraphInfoListMap = new HashMap();
		        if ((attempTraphInfoList != null) && (attempTraphInfoList.size() > 0)) {
		          attempTraphInfoListMap = IbatisDAOHelper.parseList2Map(attempTraphInfoList, "CUID");
		        }
		        if ((existUpSiteInfoCuidList != null) && (existUpSiteInfoCuidList.size() > 0)) {
		          mp.put("existUpSiteInfoCuidList", existUpSiteInfoCuidList);
		          this.IbatisResDAO.getSqlMapClientTemplate().delete("TraphMaintain.deleteExistUpSiteInfoList", mp);
		        }
				
				//upSiteInfoList  回传 //oldUpsiteInfo 返回前台上端站信息
		        /*if("多级设计".equals(taskLabelCn)){
					if(upSiteInfoList.size()!=oldUpsiteInfo.size()){
						throw new RuntimeException("返回上端站信息异常！");
					}
					//用户不可操作A端上端站信息  
					if(!StringUtils.equals(userDistrictId, aSiteDistrict)){
							for(int i=0;i<upSiteInfoList.size();i++){
								Map<String,Object> newupSiteInfo=upSiteInfoList.get(i);
								String azMode=IbatisDAOHelper.getStringValue(newupSiteInfo, "AZMODE");
								if(StringUtils.equals("A", azMode)){
									String newUpPtp1=IbatisDAOHelper.getStringValue(newupSiteInfo, "RELATED_UP_CUID");
									String newUpPtp2=IbatisDAOHelper.getStringValue(newupSiteInfo, "RELATED_UP_CUID2");
									Map<String,Object> oldupSiteInfo=oldUpsiteInfo.get(i);
									String oldUpPtp1=IbatisDAOHelper.getStringValue(oldupSiteInfo, "RELATED_UP_PTP_CUID");
									String oldUpPtp2=IbatisDAOHelper.getStringValue(oldupSiteInfo, "RELATED_UP_PTP_CUID2");
									if(!StringUtils.equals(newUpPtp1,oldUpPtp1)||!StringUtils.equals(newUpPtp2,oldUpPtp2)){
										throw new RuntimeException("A端的上端站端口，只能由A地市或二干用户进行修改!");
								}
							}
						}
					}
					//用户不可操作Z端上端站信息
					if(!StringUtils.equals(userDistrictId, zSiteDistrict)){
						for(int i=0;i<upSiteInfoList.size();i++){
							Map<String,Object> newupSiteInfo=upSiteInfoList.get(i);
							String azMode=IbatisDAOHelper.getStringValue(newupSiteInfo, "AZMODE");
							if(StringUtils.equals("Z", azMode)){
								String newUpPtp1=IbatisDAOHelper.getStringValue(newupSiteInfo, "RELATED_UP_CUID");
								String newUpPtp2=IbatisDAOHelper.getStringValue(newupSiteInfo, "RELATED_UP_CUID2");
								Map<String,Object> oldupSiteInfo=oldUpsiteInfo.get(i);
								String oldUpPtp1=IbatisDAOHelper.getStringValue(oldupSiteInfo, "RELATED_UP_PTP_CUID");
								String oldUpPtp2=IbatisDAOHelper.getStringValue(oldupSiteInfo, "RELATED_UP_PTP_CUID2");
								if(!StringUtils.equals(newUpPtp1,oldUpPtp1)||!StringUtils.equals(newUpPtp2,oldUpPtp2)){
									throw new RuntimeException("Z端的上端站端口，只能由Z地市或二干用户进行修改!");
								}
							}
						}
					}
				}
				List<Map> needSaveInfoList = new ArrayList<Map>();
				for(Map upSiteInfoMap : upSiteInfoList){
					String upPtpCuid = (String)upSiteInfoMap.get("RELATED_UP_CUID");
					String relatedTpCuid = (String)upSiteInfoMap.get("RELATED_TP_CUID");
					boolean flag = true;
					for(Map newMap : needSaveInfoList){
						String newUpPtpCuid = (String)newMap.get("RELATED_UP_CUID");
						String newRelatedTpCuid = (String)newMap.get("RELATED_TP_CUID");
						if(StringUtils.isNotEmpty(newUpPtpCuid)&&StringUtils.isNotEmpty(newRelatedTpCuid)){
							if(newUpPtpCuid.equals(upPtpCuid)&&newRelatedTpCuid.equals(relatedTpCuid)){
								flag = false;
								break;
							}
						}else{
							flag = false;
							break;
						}
					}
					if(flag){
						needSaveInfoList.add(upSiteInfoMap);
					}
				}*/
				List<Record> recordList = new ArrayList<Record>();
				/*for(Map upSiteInfoMap : needSaveInfoList){
					String traphId = (String)upSiteInfoMap.get("RELATED_SERVICE_CUID");
					String upPtpCuid = (String)upSiteInfoMap.get("RELATED_UP_CUID");
					String upPtpCuid2 = (String)upSiteInfoMap.get("RELATED_UP_CUID2");
					String relatedTpCuid = (String)upSiteInfoMap.get("RELATED_TP_CUID");//不保存备用传输网元ID
					String relatedTpCuid2 = (String)upSiteInfoMap.get("RELATED_TP_CUID2");//不保存备用传输网元ID
					if(StringUtils.isNotEmpty(traphId)&&StringUtils.isNotEmpty(upPtpCuid)&&StringUtils.isNotEmpty(relatedTpCuid)){
						Record record = new Record("T_ATTEMP_TRAPH_UP_PORT");
						record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_TRAPH_UP_PORT"));
						record.addColValue("RELATED_UP_PTP_CUID", upPtpCuid);
						record.addColSqlValue("RELATED_UP_DEVICE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+upPtpCuid+"'");
						record.addColValue("RELATED_TRAPH_CUID", traphId);
						record.addColValue("RELATED_TP_CUID", relatedTpCuid);
						record.addColValue("GROUP_NO", "0");//主用
						recordList.add(record);
						//根据是否传入备用设备信息判断是否插入备用信息
						if(StringUtils.isNotEmpty(relatedTpCuid2)){
							Record recordbak = new Record("T_ATTEMP_TRAPH_UP_PORT");
							recordbak.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_TRAPH_UP_PORT"));
							recordbak.addColValue("RELATED_UP_PTP_CUID", upPtpCuid2);
							recordbak.addColSqlValue("RELATED_UP_DEVICE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+upPtpCuid2+"'");
							recordbak.addColValue("RELATED_TRAPH_CUID", traphId);
							recordbak.addColValue("RELATED_TP_CUID", relatedTpCuid2);
							recordbak.addColValue("GROUP_NO", "1");
							recordList.add(recordbak);
						}
					}
				}
				if(recordList!=null&&recordList.size()>0){
					List insertInfo = this.IbatisResDAO.insertDynamicTableBatch(recordList);
					logger.info("插入数据："+insertInfo.size()+"条");
					
				}*/
				for (Map upSiteInfoMap : upSiteInfoList) {
			          String traphId = (String)upSiteInfoMap.get("RELATED_SERVICE_CUID");
			          if (StringUtils.isNotEmpty(traphId)) {
			            String origPtpCuid = (String)upSiteInfoMap.get("A_SIDE_CUID");
			            String destPtpCuid = (String)upSiteInfoMap.get("Z_SIDE_CUID");
			            Map attempTraphInfoMap = (Map)attempTraphInfoListMap.get(traphId);
			            if (attempTraphInfoMap != null) {
			              String aNeCuid = (String)attempTraphInfoMap.get("RELATED_NE_A_CUID");
			              String zNeCuid = (String)attempTraphInfoMap.get("RELATED_NE_Z_CUID");
			              if ((!StringUtils.isNotEmpty(aNeCuid)) || (!StringUtils.isNotEmpty(zNeCuid)) || 
			                (!StringUtils.isNotEmpty(origPtpCuid)) || (!StringUtils.isNotEmpty(destPtpCuid))) continue;
			              Record aRecord = new Record("T_ATTEMP_TRAPH_UP_PORT");
			              aRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_TRAPH_UP_PORT"));
			              aRecord.addColValue("RELATED_UP_PTP_CUID", origPtpCuid);
			              aRecord.addColSqlValue("RELATED_UP_DEVICE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '" + origPtpCuid + "'");
			              aRecord.addColValue("RELATED_TRAPH_CUID", traphId);
			              aRecord.addColValue("RELATED_TP_CUID", aNeCuid);
			              recordList.add(aRecord);

			              Record zRecord = new Record("T_ATTEMP_TRAPH_UP_PORT");
			              zRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_TRAPH_UP_PORT"));
			              zRecord.addColValue("RELATED_UP_PTP_CUID", destPtpCuid);
			              zRecord.addColSqlValue("RELATED_UP_DEVICE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '" + destPtpCuid + "'");
			              zRecord.addColValue("RELATED_TRAPH_CUID", traphId);
			              zRecord.addColValue("RELATED_TP_CUID", zNeCuid);
			              recordList.add(zRecord);
			            }
			          }
			        }

			        if ((recordList != null) && (recordList.size() > 0))
			          this.IbatisResDAO.insertDynamicTableBatch(recordList);
			      }
			}
//		}
	}
	//获取电路AZ端取需要设置上端站设备及端口的设备
	public List<Map<String,Object>> getRelatedTpByTraph(List<String> serviceCuidList) {
//		public List<Map<String,Object>> getRelatedTpByTraph(String serviceCuid) {
		List<Map<String,Object>> tpMapList = new ArrayList<Map<String,Object>>();
		if ((serviceCuidList != null) && (serviceCuidList.size() > 0)) {
		      Map mp = new HashMap();
		      mp.put("serviceCuidList", serviceCuidList);
		      List localList1 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphMaintain.queryAttempTraphRouteInfoList", mp);
		    }
		/*if(StringUtils.isNotEmpty(serviceCuid)){
			Map mp = new HashMap();
			mp.put("serviceCuid", serviceCuid);
			//获取电路路由信息
			List<Map<String,Object>> attempTraphRouteList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRouteList",mp);
			if(attempTraphRouteList!=null && attempTraphRouteList.size()>0){
		    	List<String> pathCuidList = new ArrayList<String>();
		    	int index = 0;
		    	//循环路由段，找出最后一段路由段序号
		    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
		    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
		        	int indexPathRoute = Integer.parseInt(indexNo.toString());
		        	if(indexPathRoute>index){
		        		index = indexPathRoute;
		        	}
		        }
		    	//判断第一段路由设计方式是否正确
		    	String aSiteCuid = "";
		    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
		    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
		        	int indexPathRoute = Integer.parseInt(indexNo.toString());
		    		if(indexPathRoute==0){
		    			String pathType = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_TYPE"))?(String)attempTraphRouteMap.get("PATH_TYPE"):(String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
		    			logger.info("-------------------pathType:"+pathType);
				    	if(pathType.trim().equals("TRANS_PATH")||pathType.trim().equals("ATTEMP_PTN_PATH")||pathType.trim().equals("MSAP_PATH")){
				    		logger.info("-------------------pathType:"+pathType);
				    		String pathCuid = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_CUID"))?
				    				(String)attempTraphRouteMap.get("PATH_CUID"):
				    					(String)attempTraphRouteMap.get("MULTI_PATH_CUID");
					    	if(!pathCuidList.contains(pathCuid)){
					    		pathCuidList.add(pathCuid);
					    	} 
				    	}else{
			    			throw new RuntimeException("所选电路第一段路由设计方式为不能进行上端站配置的方式");
				    	}
				    	aSiteCuid = (String)attempTraphRouteMap.get("RELATED_A_SITE_CUID");
		    		}
		    	}
		    	List<Map<String,Object>> existUpSiteInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryExistUpSiteInfoList",mp);
		    	Map<String,List<Map<String,Object>>> existUpSiteInfoListMap = new HashMap<String,List<Map<String,Object>>>();
		    	if(existUpSiteInfoList!=null && existUpSiteInfoList.size()>0){
		    		for(Map<String,Object> existUpSiteInfoMap : existUpSiteInfoList){
		    			String relatedTpCuid = IbatisDAOHelper.getStringValue(existUpSiteInfoMap, "RELATED_TP_CUID");
		    			String  groupNo=IbatisDAOHelper.getStringValue(existUpSiteInfoMap, "GROUP_NO");
		    			List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
		    			
						 if(existUpSiteInfoListMap.containsKey(relatedTpCuid)){
							 list=existUpSiteInfoListMap.get(relatedTpCuid);
							 list.add(existUpSiteInfoMap); 
						 }else{
							 list.add(existUpSiteInfoMap);
							 existUpSiteInfoListMap.put(relatedTpCuid, list); 
						 }
		    		}
		    	}
		    	if(pathCuidList!=null && pathCuidList.size()>0){
		    		tpMapList = this.getTpMapList(aSiteCuid,pathCuidList,tpMapList,existUpSiteInfoListMap);
		    	}
		    	
		    	//判断最后一段路由设计方式是否正确
		    	String zSiteCuid = "";
		    	pathCuidList = new ArrayList<String>();
		    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
		    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
		        	int indexPathRoute = Integer.parseInt(indexNo.toString());
		    		if(indexPathRoute==index){
		    			String pathType = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_TYPE"))?(String)attempTraphRouteMap.get("PATH_TYPE"):(String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
				    	if(pathType.trim().equals("TRANS_PATH")||pathType.trim().equals("ATTEMP_PTN_PATH")||pathType.trim().equals("MSAP_PATH")){
				    		
				    		String pathCuid = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_CUID"))?(String)attempTraphRouteMap.get("PATH_CUID"):(String)attempTraphRouteMap.get("MULTI_PATH_CUID");
					    	if(!pathCuidList.contains(pathCuid)){
					    		pathCuidList.add(pathCuid);
					    	} 
				    	}else{
				    		if(index>0){
				    			throw new RuntimeException("所选电路最后一段路由设计方式为不能进行上端站配置的方式");
				    		}
				    	}
				    	zSiteCuid = (String)attempTraphRouteMap.get("RELATED_Z_SITE_CUID");
		    		}
		    	}
		    	if(pathCuidList!=null && pathCuidList.size()>0){
		    		tpMapList = this.getTpMapList(zSiteCuid,pathCuidList,tpMapList,existUpSiteInfoListMap);
		    	}
		    }
		}*/
		return tpMapList;
	}
	/*private List<Map<String, Object>> getTpMapList(String siteCuid,List<String> pathCuidList, 
			List<Map<String, Object>> tpMapList,Map<String,List<Map<String,Object>>> existUpSiteInfoListMap) {
		Map mp = new HashMap();
		mp.put("pathCuidList", pathCuidList);
		//TODO 获取路由详细设计信息
		List<Map<String,Object>> attempTraphRouteInfoList = 
			this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRouteInfoList",mp);
		
		if(attempTraphRouteInfoList!=null&&attempTraphRouteInfoList.size()>0){
			for(Map<String,Object> attempTraphRouteInfo : attempTraphRouteInfoList){
				String origSiteCuid = (String)attempTraphRouteInfo.get("ORIG_SITE_CUID");
    			String destSiteCuid = (String)attempTraphRouteInfo.get("DEST_SITE_CUID");
    			String origSite = (String)attempTraphRouteInfo.get("ORIG_SITE");
    			String destSite = (String)attempTraphRouteInfo.get("DEST_SITE");
    			String origEquCuid = (String)attempTraphRouteInfo.get("ORIG_EQU_CUID");
    			String origEquCuid2 = (String)attempTraphRouteInfo.get("ORIG_EQU_CUID2");
    			String destEquCuid = (String)attempTraphRouteInfo.get("DEST_EQU_CUID");
    			String destEquCuid2 = (String)attempTraphRouteInfo.get("DEST_EQU_CUID2");
    			String origPtp = (String)attempTraphRouteInfo.get("ORIG_PTP");
    			String origPtp2 = (String)attempTraphRouteInfo.get("ORIG_PTP2");
    			String destPtp = (String)attempTraphRouteInfo.get("DEST_PTP");
    			String destPtp2 = (String)attempTraphRouteInfo.get("DEST_PTP2");
    			String upPtpCuid = "";
    			String upPtpName = "";
    			String upPtpCuid2 = "";
    			String upPtpName2 = "";
    			String pathType=(String)attempTraphRouteInfo.get("PATH_TYPE");
    			String pathCuid=(String)attempTraphRouteInfo.get("CUID");
    			Map<String,Object> aTpMap = new HashMap<String,Object>();
    			if(siteCuid.equals(origSiteCuid)){//A端
    				boolean flag = true;
    				if(tpMapList != null&&tpMapList.size()>0){
    					for(Map<String,Object> oldTpMap : tpMapList){
    						String oldOrigSite = (String)oldTpMap.get("SITE");
    						String oldOrigEquCuid = (String)oldTpMap.get("EQU_CUID");
    						if(oldOrigSite.equals(origSite)&&oldOrigEquCuid.equals(origEquCuid)){//起点上端站站点设备信息未发生改变
    							flag = false;
    						}
    					}
    				}
    				
    				if(flag){
    					//拓展备用  
    					List<Map<String,Object>> existUpSiteInfoMapList = existUpSiteInfoListMap.get(origEquCuid);//主用设备ID
    					if(existUpSiteInfoMapList!=null && existUpSiteInfoMapList.size()>0){
    						upPtpCuid = (String)existUpSiteInfoMapList.get(0).get("RELATED_UP_PTP_CUID");
    						upPtpName = (String)existUpSiteInfoMapList.get(0).get("RELATED_UP_PTP_NAME");
    					}
    					if(existUpSiteInfoMapList!=null && existUpSiteInfoMapList.size()>1){
							existUpSiteInfoMapList.remove(0);//移除
	    				}
    					if(StringUtils.isNotEmpty(origEquCuid2)){
	    					List<Map<String,Object>> existUpSiteInfoMapListbak = existUpSiteInfoListMap.get(origEquCuid2);//设计主用设备ID
	    					if(existUpSiteInfoMapListbak!=null && existUpSiteInfoMapListbak.size()>0){
	    						upPtpCuid2 = (String)existUpSiteInfoMapListbak.get(0).get("RELATED_UP_PTP_CUID");
	    						upPtpName2 = (String)existUpSiteInfoMapListbak.get(0).get("RELATED_UP_PTP_NAME");
	    					}
	    					if(existUpSiteInfoMapListbak!=null && existUpSiteInfoMapListbak.size()>0){
	    						existUpSiteInfoMapListbak.remove(0);//移除
		    				}
    					}
    					
    					
    					
    					aTpMap.put("SITE", origSite);
    					aTpMap.put("SITECUID", siteCuid);
    					aTpMap.put("PATH_TYPE", pathType);
    					aTpMap.put("PATH_CUID", pathCuid);
	    				aTpMap.put("PTP", origPtp);
	    				aTpMap.put("EQU_CUID", origEquCuid);
	    				aTpMap.put("RELATED_UP_PTP_CUID", upPtpCuid);
	    				aTpMap.put("RELATED_UP_PTP_NAME", upPtpName);
	    				//路由段备用端口名称 设备名称	 上端站端口、名称    				
	    				aTpMap.put("PTP2", origPtp2);//ORIG_PTP
	    				aTpMap.put("EQU_CUID2", origEquCuid2);//ORIG_EQU_CUID
	    				aTpMap.put("RELATED_UP_PTP_CUID2", upPtpCuid2);
	    				aTpMap.put("RELATED_UP_PTP_NAME2", upPtpName2);
	    				aTpMap.put("AZMode", "A");
	    				tpMapList.add(aTpMap);
    				}
    			}else if(siteCuid.equals(destSiteCuid)){
    				boolean flag = true;
    				if(tpMapList != null&&tpMapList.size()>0){
    					for(Map<String,Object> oldTpMap : tpMapList){
    						String oldOrigSite = (String)oldTpMap.get("SITE");
    						String oldOrigEquCuid = (String)oldTpMap.get("EQU_CUID");
    						if(oldOrigSite.equals(destSite)&&oldOrigEquCuid.equals(destEquCuid)){
    							flag = false;
    						}
    					}
    				}
    				if(flag){
    					List<Map<String,Object>> existUpSiteInfoMapList = existUpSiteInfoListMap.get(destEquCuid);
    					if(existUpSiteInfoMapList!=null && existUpSiteInfoMapList.size()>0){
    						upPtpCuid = (String)existUpSiteInfoMapList.get(0).get("RELATED_UP_PTP_CUID");
    						upPtpName = (String)existUpSiteInfoMapList.get(0).get("RELATED_UP_PTP_NAME");
    						if(existUpSiteInfoMapList!=null && existUpSiteInfoMapList.size()>0){
        						existUpSiteInfoMapList.remove(0);
    	    				}
    					}
    					if(StringUtils.isNotEmpty(destEquCuid2)){
	    					List<Map<String,Object>> existUpSiteInfoMapListbak = existUpSiteInfoListMap.get(destEquCuid2);//主用设备ID
	    					if(existUpSiteInfoMapListbak!=null && existUpSiteInfoMapListbak.size()>0){
	    						upPtpCuid2 = (String)existUpSiteInfoMapListbak.get(0).get("RELATED_UP_PTP_CUID");
	    						upPtpName2 = (String)existUpSiteInfoMapListbak.get(0).get("RELATED_UP_PTP_NAME");
	    					}
	    					if(existUpSiteInfoMapListbak!=null && existUpSiteInfoMapListbak.size()>0){
	    						existUpSiteInfoMapListbak.remove(0);//移除
		    				}
    					}
    					
    					aTpMap.put("SITE", destSite);
    					aTpMap.put("SITECUID", siteCuid);
    					aTpMap.put("PATH_TYPE", pathType);
    					aTpMap.put("PATH_CUID", pathCuid);
	    				aTpMap.put("PTP", destPtp);//RELATED_PTP_NAME
	    				aTpMap.put("EQU_CUID", destEquCuid);
	    				aTpMap.put("RELATED_UP_PTP_CUID", upPtpCuid);
	    				aTpMap.put("RELATED_UP_PTP_NAME", upPtpName);
	    				
	    				aTpMap.put("PTP2", destPtp2);//ORIG_PTP
	    				aTpMap.put("EQU_CUID2", destEquCuid2);//ORIG_EQU_CUID
	    				aTpMap.put("RELATED_UP_PTP_CUID2", upPtpCuid2);
	    				aTpMap.put("RELATED_UP_PTP_NAME2", upPtpName2);
	    				aTpMap.put("AZMode", "Z");
	    				
	    				tpMapList.add(aTpMap);
    				}
    			}
			}
		}
		return tpMapList;
	}*/
	//获取电路综资要求路由段设计方式
	/*public Map<String,Map> getModeMap(List<String> needCheckTraphCuidList) {
		Map<String,Map> newMap = new HashMap<String,Map>();
		Map mp = new HashMap();
		mp.put("traphCuidList", needCheckTraphCuidList);
		List<Map<String,Object>> modeMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryModeMapList",mp);
		if(modeMapList != null && modeMapList.size()>0){
			for (Map<String,Object> modeMap : modeMapList){
				Map map = new HashMap();
				String traphCuid = IbatisDAOHelper.getStringValue(modeMap, "RELATED_DETAIL_CUID");
				String aApMode = IbatisDAOHelper.getStringValue(modeMap, "A_AP_MODE");
				String aSubApMode = IbatisDAOHelper.getStringValue(modeMap, "A_SUB_AP_MODE");
				String zApMode = IbatisDAOHelper.getStringValue(modeMap, "Z_AP_MODE");
				String zSubApMode = IbatisDAOHelper.getStringValue(modeMap, "Z_SUB_AP_MODE");
				String aMode = "未知";
				if(StringUtils.isNotEmpty(aApMode)&&StringUtils.isNotEmpty(aSubApMode)){
					aMode = getModeMap(aApMode,aSubApMode);
				}
				logger.info("-------------------aMode:"+aMode);
				String zMode = "未知";
				if(StringUtils.isNotEmpty(zApMode)&&StringUtils.isNotEmpty(zSubApMode)){
					zMode = getModeMap(zApMode,zSubApMode);
				}
				logger.info("-------------------zMode:"+zMode);
				map.put("aMode", aMode);
				map.put("zMode", zMode);
				newMap.put(traphCuid, map);
			}
		}
		return newMap;
	}
	
	public String getModeMap(String apMode,String subApmode) {
		String mode = "未知";
		if(apMode.equals("1")){//ptn
			if(subApmode.equals("1")||subApmode.equals("4")||subApmode.equals("5")||subApmode.equals("6")||subApmode.equals("7")||subApmode.equals("8")){
				mode = "PTN";
			}else if(subApmode.equals("2")||subApmode.equals("3")){
				mode = "MSAP";
			}
		}else if(apMode.equals("2")){//sdh
			if(subApmode.equals("1")||subApmode.equals("4")||subApmode.equals("5")||subApmode.equals("6")||subApmode.equals("7")||subApmode.equals("8")){
				mode = "SDH";
			}else if(subApmode.equals("2")||subApmode.equals("3")){
				mode = "MSAP";
			}
		}
		return mode;
	}*/
	//校验电路第一段与最后一段路由设计方式是否和综资派发要求方式一致
	/*public void validateTraphRoute(String serviceCuid,String aMode,String zMode,String str) {
		if(StringUtils.isNotBlank(serviceCuid)){
			Map mp = new HashMap();
			mp.put("serviceCuid", serviceCuid);
			//A端上端站必填校验
			if("A".equals(str) || "AZ".equals(str)){
				boolean flagA = false;
				List<Map<String,Object>> existUpSiteInfoAlist = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ZJPointsGrid"+".getUpSiteInfoA",mp);
				if(existUpSiteInfoAlist!=null&&existUpSiteInfoAlist.size()>0){
					for(Map<String,Object> existUpSiteInfoAMap : existUpSiteInfoAlist){
						String aPortName = IbatisDAOHelper.getStringValue(existUpSiteInfoAMap, "A_PORT_NAME");
						if(StringUtils.isEmpty(aPortName)){
							flagA = true;
							break;
						}
					}
				}
				if(flagA||existUpSiteInfoAlist==null||existUpSiteInfoAlist.size()==0){
					throw new RuntimeException("A端上端站不能为空!");
				}
			}
			//Z端上端站必填校验
			if("Z".equals(str) || "AZ".equals(str)){
				boolean flagZ = false;
				List<Map<String,Object>> existUpSiteInfoZlist = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ZJPointsGrid"+".getUpSiteInfoZ",mp);
				if(existUpSiteInfoZlist!=null&&existUpSiteInfoZlist.size()>0){
					for(Map<String,Object> existUpSiteInfoZMap : existUpSiteInfoZlist){
						String zPortName = IbatisDAOHelper.getStringValue(existUpSiteInfoZMap, "Z_PORT_NAME");
						if(StringUtils.isEmpty(zPortName)){
							flagZ = true;
							break;
						}
					}
				}
				if(flagZ||existUpSiteInfoZlist==null||existUpSiteInfoZlist.size()==0){
					throw new RuntimeException("Z端上端站不能为空!");
				}
			}
			
			//校验电路第一段与最后一段路由设计方式是否和综资派发要求方式一致
			List<Map<String,Object>> attempTraphRouteList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRouteList",mp);
			
			if(attempTraphRouteList!=null && attempTraphRouteList.size()>1){
		    	int index = 0;
		    	//循环路由段，找出最后一段路由段序号
		    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
		    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
		        	int indexPathRoute = Integer.parseInt(indexNo.toString());
		        	if(indexPathRoute>index){
		        		index = indexPathRoute;
		        	}
		        }
		    	//判断第一段路由设计方式是否正确
		    	logger.info("---------------A端组网方式验证(多段)--------------");
		    	if("A".equals(str) || "AZ".equals(str)){
			    	if(!aMode.equals("未知")){
			    		for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
			    			Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
				        	int indexPathRoute = Integer.parseInt(indexNo.toString());
				    		if(indexPathRoute==0){
				    			String pathType = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_TYPE"))?(String)attempTraphRouteMap.get("PATH_TYPE"):(String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
				    			logger.info("-------------------第一段路由的配置方式:"+pathType);
				    			logger.info("-------------------第一段综资传入配置方式:"+aMode);
				    			if(aMode.equals("SDH")&&!pathType.equals("TRANS_PATH")){
						    		throw new RuntimeException("所选电路第一段路由的配置方式应为SDH");
						    	}
					    		if(aMode.equals("PTN")&&!pathType.equals("ATTEMP_PTN_PATH")){
						    		throw new RuntimeException("所选电路第一段路由的配置方式应为PTN");
						    	}
					    		if(aMode.equals("MSAP")&&!pathType.equals("MSAP_PATH")){
						    		throw new RuntimeException("所选电路第一段路由的配置方式应为MSAP");
						    	}
				    		}
				    	}
			    	}
			    }
			    	//判断最后一段路由设计方式是否正确
		    	if("Z".equals(str) || "AZ".equals(str)){
			    	if(!zMode.equals("未知")){
			    		for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
			    			Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
				        	int indexPathRoute = Integer.parseInt(indexNo.toString());
				    		if(indexPathRoute==index){
				    			String pathType = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_TYPE"))?(String)attempTraphRouteMap.get("PATH_TYPE"):(String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
				    			logger.info("-------------------最后一段路由的配置方式:"+pathType);
				    			logger.info("-------------------最后综资传入配置方式:"+zMode);
				    			if(index>0||(index==0&&aMode.equals("未知"))){
				    				logger.info("---------------Z端组网方式验证(多段)--------------");
						    		if(zMode.equals("SDH")&&!pathType.equals("TRANS_PATH")){
							    		throw new RuntimeException("所选电路最后一段路由的配置方式应为SDH");
							    	}
						    		if(zMode.equals("PTN")&&!pathType.equals("ATTEMP_PTN_PATH")){
							    		throw new RuntimeException("所选电路最后一段路由的配置方式应为PTN");
							    	}
						    		if(zMode.equals("MSAP")&&!pathType.equals("MSAP_PATH")){
							    		throw new RuntimeException("所选电路最后一段路由的配置方式应为MSAP");
							    	}
						    	}
				    		}
				    	}
			    	}
		    	}
		    }else if(attempTraphRouteList!=null && attempTraphRouteList.size()==1){
		    			    	
		    	//判断第一段路由设计方式是否正确
		    	Map<String,Object> attempTraphRouteMap= attempTraphRouteList.get(0);
    			String pathType = StringUtils.isNotBlank((String)attempTraphRouteMap.get("PATH_TYPE"))?(String)attempTraphRouteMap.get("PATH_TYPE"):(String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
		    	if(!aMode.equals("未知")&&!zMode.equals("未知")){		    				
			    			logger.info("-------------------第一段路由的配置方式:"+pathType);			    			
			    			if(aMode.equals("PTN")&&zMode.equals("PTN")&&!pathType.equals("ATTEMP_PTN_PATH")){
					    	    throw new RuntimeException("所选电路第一段路由的配置方式应为PTN");					    	    
					    	}
			    			if(aMode.equals("SDH")&&zMode.equals("SDH")&&!pathType.equals("TRANS_PATH")){
					    	    throw new RuntimeException("所选电路第一段路由的配置方式应为SDH");
					    	}
			    			if((aMode.equals("PTN")&&zMode.equals("SDH"))||(aMode.equals("SDH")&&zMode.equals("PTN"))){
					    	    throw new RuntimeException("因电路AZ端组网方式不同，请为电路配置两段路由!");
					    	}
			    			if(((aMode.equals("MSAP")&&(zMode.equals("SDH")||zMode.equals("PTN")||zMode.equals("MSAP")))
			    					||(zMode.equals("MSAP")&&(aMode.equals("SDH")||aMode.equals("PTN"))))&&!pathType.equals("MSAP_PATH")){			    				
			    				throw new RuntimeException("所选电路第一段路由的配置方式应为MSAP");
			    			}			    		
			    }
		    	if((aMode.equals("未知")&&!zMode.equals("未知"))||(!aMode.equals("未知")&&zMode.equals("未知"))){
		    		if((aMode.equals("PTN")||zMode.equals("PTN"))&&!pathType.equals("ATTEMP_PTN_PATH")){
		    			throw new RuntimeException("所选电路第一段路由的配置方式应为PTN");	
		    		}
		    		if((aMode.equals("SDH")||zMode.equals("SDH"))&&!pathType.equals("TRANS_PATH")){
		    			throw new RuntimeException("所选电路第一段路由的配置方式应为SDH");
		    		}
		    		if((aMode.equals("MSAP")||zMode.equals("MSAP"))&&!pathType.equals("MSAP_PATH")){
		    			throw new RuntimeException("所选电路第一段路由的配置方式应为MSAP");
		    		}
		    	}
		    }
		}
	}*/
	/*public void validateUpSiteData(String traphCuid){
		Map mp = new HashMap();
		List traphCuidList=new ArrayList();
		traphCuidList.add(traphCuid);
		mp.put("traphCuidList", traphCuidList);
		//获取订单详情的路由保护方式
		List<Map<String,Object>> modeMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryModeMapList",mp);
		if(modeMapList.size()>0){
			Map<String,Object> orderDetail=modeMapList.get(0);
			//A端上端站信息
			List<Map<String,Object>> existUpSiteInfoAlist = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ZJPointsGrid"+".getUpSiteInfoA",mp);
			List<Map<String,Object>> aMainUpSiteInfolist=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> aBakUpSiteInfolist=new ArrayList<Map<String,Object>>();
			for(Map<String,Object> existUpSiteInfo : existUpSiteInfoAlist){
				String groupNo=IbatisDAOHelper.getStringValue(existUpSiteInfo, "GROUP_NO");
				//主用
				if("0".equals(groupNo)){
					aMainUpSiteInfolist.add(existUpSiteInfo);
				}else if("1".equals(groupNo)){
					aBakUpSiteInfolist.add(existUpSiteInfo);
				}
			
			}
			//Z端上端站信息
			List<Map<String,Object>> existUpSiteInfoZlist = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("ZJPointsGrid"+".getUpSiteInfoZ",mp);
			List<Map<String,Object>> zMainUpSiteInfolist=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> zBakUpSiteInfolist=new ArrayList<Map<String,Object>>();
			for(Map<String,Object> existUpSiteInfo :existUpSiteInfoZlist){
				String groupNo=IbatisDAOHelper.getStringValue(existUpSiteInfo, "GROUP_NO");
				//主用
				if("0".equals(groupNo)){
					zMainUpSiteInfolist.add(existUpSiteInfo);
				}else if("1".equals(groupNo)){
					zBakUpSiteInfolist.add(existUpSiteInfo);
				}
			
			}
			int aMode=IbatisDAOHelper.getIntValue(orderDetail, "A_ROUTE_MODE");
			int zMode=IbatisDAOHelper.getIntValue(orderDetail, "Z_ROUTE_MODE");
			//单节点单路由
			//aMode=0  传入A端路由  保护方式为空   
			if(aMode==1){
				if(aBakUpSiteInfolist.size()>0){
					throw new RuntimeException("该电路为单节点单路由，无需配置备用上端站！");
				}
			}else if(aMode==2){
				if(aBakUpSiteInfolist.size()<=0){
					throw new RuntimeException("该电路为单节点双路由，必须填写备用上端站！");
				}
				for(Map<String,Object> aMainUpsiteInfo : aMainUpSiteInfolist){
					boolean isSameNe=false;
					String aNeName=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_NE_NAME");
					for(Map<String,Object> aBakUpsiteInfo : aBakUpSiteInfolist){
						String aNeNamebak=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_NE_NAME");
						if(aNeName.equals(aNeNamebak)){
							isSameNe=true;							
						}
					}
					if(!isSameNe){
						throw new RuntimeException("该电路为单节点双路由，主备用上端站设备必须为同一设备!");	
					}
					boolean isSamePort=false;
					String aPortName=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_PORT_NAME");
					for(Map<String,Object> aBakUpsiteInfo : aBakUpSiteInfolist){
						String aPortNamebak=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_PORT_NAME");
						if(aPortName.equals(aPortNamebak)){
							isSamePort=true;							
						}
					}
					if(isSamePort){
						throw new RuntimeException("该电路为单节点双路由，主备用上端站端口不能相同！");	
					}
				}
				
			}else if(aMode==3){
				if(aBakUpSiteInfolist.size()<=0){
					throw new RuntimeException("该电路为双节点双路由，必须填写备用上端站！");
				}
				for(Map<String,Object> aMainUpsiteInfo : aMainUpSiteInfolist){
					boolean isSameNe=false;
					String aNeName=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_NE_NAME");
					for(Map<String,Object> aBakUpsiteInfo : aBakUpSiteInfolist){
						String aNeNamebak=IbatisDAOHelper.getStringValue(aBakUpsiteInfo, "A_NE_NAME");
						if(aNeName.equals(aNeNamebak)){
							isSameNe=true;							
						}
					}
					if(isSameNe){
						throw new RuntimeException("该电路为双节点双路由，主备用上端站设备必须为不同设备!");	
					}
					boolean isSamePort=false;
					String aPortName=IbatisDAOHelper.getStringValue(aMainUpsiteInfo, "A_PORT_NAME");
					for(Map<String,Object> aBakUpsiteInfo : aBakUpSiteInfolist){
						String aPortNamebak=IbatisDAOHelper.getStringValue(aBakUpsiteInfo, "A_PORT_NAME");
						if(aPortName.equals(aPortNamebak)){
							isSamePort=true;							
						}
					}
					if(isSamePort){
						throw new RuntimeException("该电路为双节点双路由，主备用上端站端口不能相同！");	
					}
				}
				
				if(zMode==1){
					if(zBakUpSiteInfolist.size()>0){
						throw new RuntimeException("该电路为单节点单路由，无需配置备用上端站！");
					}
				}else if(zMode==2){
					if(zBakUpSiteInfolist.size()<=0){
						throw new RuntimeException("该电路为单节点双路由，必须填写备用上端站！");
					}
					for(Map<String,Object> zMainUpsiteInfo : zMainUpSiteInfolist){
						boolean isSameNe=false;
						String zNeName=IbatisDAOHelper.getStringValue(zMainUpsiteInfo, "A_NE_NAME");
						for(Map<String,Object> zBakUpsiteInfo : zBakUpSiteInfolist){
							String zNeNamebak=IbatisDAOHelper.getStringValue(zBakUpsiteInfo, "A_NE_NAME");
							if(zNeName.equals(zNeNamebak)){
								isSameNe=true;							
							}
						}
						if(!isSameNe){
							throw new RuntimeException("该电路为单节点双路由，主备用上端站设备必须为同一设备!");	
						}
						boolean isSamePort=false;
						String zPortName=IbatisDAOHelper.getStringValue(zMainUpsiteInfo, "A_PORT_NAME");
						for(Map<String,Object> zBakUpsiteInfo : zBakUpSiteInfolist){
							String zPortNamebak=IbatisDAOHelper.getStringValue(zBakUpsiteInfo, "A_PORT_NAME");
							if(zPortName.equals(zPortNamebak)){
								isSamePort=true;							
							}
						}
						if(isSamePort){
							throw new RuntimeException("该电路为单节点双路由，主备用上端站端口不能相同！");	
						}
					}
				}else if(zMode==3){
					if(zBakUpSiteInfolist.size()<=0){
						throw new RuntimeException("该电路为双节点双路由，必须填写备用上端站！");
					}
					for(Map<String,Object> zMainUpsiteInfo : zMainUpSiteInfolist){
						boolean isSameNe=false;
						String zNeName=IbatisDAOHelper.getStringValue(zMainUpsiteInfo, "A_NE_NAME");
						for(Map<String,Object> zBakUpsiteInfo : zBakUpSiteInfolist){
							String zNeNamebak=IbatisDAOHelper.getStringValue(zBakUpsiteInfo, "A_NE_NAME");
							if(zNeName.equals(zNeNamebak)){
								isSameNe=true;							
							}
						}
						if(isSameNe){
							throw new RuntimeException("该电路为双节点双路由，主备用上端站设备必须为不同设备!");	
						}
						boolean isSamePort=false;
						String zPortName=IbatisDAOHelper.getStringValue(zMainUpsiteInfo, "A_PORT_NAME");
						for(Map<String,Object> zBakUpsiteInfo : zBakUpSiteInfolist){
							String zPortNamebak=IbatisDAOHelper.getStringValue(zBakUpsiteInfo, "A_PORT_NAME");
							if(zPortName.equals(zPortNamebak)){
								isSamePort=true;							
							}
						}
						if(isSamePort){
							throw new RuntimeException("该电路为双节点双路由，主备用上端站端口不能相同！");	
						}
					
				}		
			}
		}
	  }
	}*/
	// 自动计算上端站端口
	/*public void CountRelatedUpByTraph(String serviceCuid) {
		Map mp = new HashMap();
		mp.put("serviceCuid", serviceCuid);
		//获取电路路由信息
		List<Map<String,Object>> attempTraphRouteList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRouteList",mp);
		List<Map> upSiteInfoList = new ArrayList<Map>();
		if(attempTraphRouteList!=null && attempTraphRouteList.size()>0){
	    	int index = 0;
	    	//循环路由段，找出最后一段路由段序号
	    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
	    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
	        	int indexPathRoute = Integer.parseInt(indexNo.toString());
	        	if(indexPathRoute>index){
	        		index = indexPathRoute;
	        	}
	        }
	    	//计算第一段路由上端设备及端口
	    	upSiteInfoList = this.CountRelatedUpInfo(attempTraphRouteList,0,"A",upSiteInfoList,serviceCuid);
	    	//计算最后一段路由上端设备及端口
	    	upSiteInfoList = this.CountRelatedUpInfo(attempTraphRouteList,index,"Z",upSiteInfoList,serviceCuid);
		}
		if(upSiteInfoList!= null && upSiteInfoList.size()>0){
			this.saveUpSiteInfo(upSiteInfoList,null);
		}
	}*/
	/*private List<Map> CountRelatedUpInfo(List<Map<String, Object>> attempTraphRouteList, int index, String str,
			List<Map> upSiteInfoList,String serviceCuid) {
		List<String> pathCuidList = new ArrayList<String>();//ptn路由段
		List<String> ptnPathCuidList = new ArrayList<String>();//ptn路由段
		List<String> msapPathCuidList = new ArrayList<String>();//msap路由段
		List<String> transPathCuidList = new ArrayList<String>();//通道
		String siteCuid = "";
    	for(Map<String,Object> attempTraphRouteMap : attempTraphRouteList){
    		Object indexNo = (Object)attempTraphRouteMap.get("INDEX_PATH_ROUTE");
        	int indexPathRoute = Integer.parseInt(indexNo.toString());
    		if(indexPathRoute==index){
    			String pathType = (String)attempTraphRouteMap.get("PATH_TYPE");
    			if(StringUtils.isNotBlank(pathType)){
    				if(pathType.equals("ATTEMP_PTN_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("PATH_CUID");
    					if(!ptnPathCuidList.contains(pathCuid)){
    						ptnPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}else if(pathType.equals("MSAP_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("PATH_CUID");
    					if(!msapPathCuidList.contains(pathCuid)){
    						msapPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}else if(pathType.equals("TRANS_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("PATH_CUID");
    					if(!transPathCuidList.contains(pathCuid)){
    						transPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}
    			}else{
    				pathType = (String)attempTraphRouteMap.get("MULTI_PATH_TYPE");
    				if(pathType.equals("ATTEMP_PTN_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("MULTI_PATH_CUID");
    					if(!ptnPathCuidList.contains(pathCuid)){
    						ptnPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}else if(pathType.equals("MSAP_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("MULTI_PATH_CUID");
    					if(!msapPathCuidList.contains(pathCuid)){
    						msapPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}else if(pathType.equals("TRANS_PATH")){
    					String pathCuid = (String)attempTraphRouteMap.get("MULTI_PATH_CUID");
    					if(!transPathCuidList.contains(pathCuid)){
    						transPathCuidList.add(pathCuid);
    					}
    					if(!pathCuidList.contains(pathCuid)){
    						pathCuidList.add(pathCuid);
    					}
    				}
    			}
    			if(str.equals("A")){
    				siteCuid = (String)attempTraphRouteMap.get("RELATED_A_SITE_CUID");
    			}else if(str.equals("Z")){
    				siteCuid = (String)attempTraphRouteMap.get("RELATED_Z_SITE_CUID");
    			}
    			Map mp = new HashMap();
    			mp.put("pathCuidList", pathCuidList);
    			List<Map<String,Object>> attempTraphRouteInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryAttempTraphRouteInfoList",mp);
    			if(attempTraphRouteInfoList!=null &&attempTraphRouteInfoList.size()>0){
    				Map<String,Map<String,Object>> attempTraphRouteInfoListMap = IbatisDAOHelper.parseList2Map(attempTraphRouteInfoList, "CUID");
    				if(ptnPathCuidList != null && ptnPathCuidList.size()>0){
    					//ptn设计
			    		for(String ptnPathCuid : ptnPathCuidList){
			    			Map<String,Object> attempTraphRouteInfoMap = attempTraphRouteInfoListMap.get(ptnPathCuid);
			    			if(attempTraphRouteInfoMap != null){
			    				String origSite = (String)attempTraphRouteInfoMap.get("ORIG_SITE_CUID");
			    				String destSite = (String)attempTraphRouteInfoMap.get("DEST_SITE_CUID");
			    				String origEqu = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID");
			    				String destEqu = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID");
			    				
			    				String origEqu2 = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID2");
			    				String destEqu2 = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID2");
			    				
			    				String origPtp = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID");
			    				String destPtp = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID");
			    				
			    				String origPtp2 = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID2");
			    				String destPtp2 = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID2");
			    				
			    				
			    				//<!-- 根据两端端口查询隧道以及标签交换信息-->
			    				mp.clear();
			    				mp.put("origPtp", origPtp);
			    				mp.put("destPtp", destPtp);
			    				List<Map<String,Object>> tunnelInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTurnnelInfoList",mp);
			    				Map upSiteInfoMap = new HashMap();
			    				if(tunnelInfoList!=null&& tunnelInfoList.size()>1){
			    			    	//隧道关联端口查询标签交换的下一跳端口记为A端上端站端口
			    			    	Map<String,Object> tunnelInfoMap = tunnelInfoList.get(1);
			    			    	String tunnelPtpA = (String)tunnelInfoMap.get("ORIG_PTP_CUID");
			    			    	String upPtp = (String)tunnelInfoMap.get("ORIG_UP_CUID");
			    			    	if(siteCuid.equals(origSite)&&origPtp.equals(tunnelPtpA)){
			    			    		upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID", upPtp);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID", origEqu);
			    			    		upSiteInfoList.add(upSiteInfoMap);
			    			    	}else if(siteCuid.equals(destSite)&&destPtp.equals(tunnelPtpA)){
			    			    		upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID", upPtp);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID", destEqu);
			    			    		upSiteInfoList.add(upSiteInfoMap);
			    			    	}
			    			    }
			    			    if(StringUtils.isNotEmpty(origPtp2)&&StringUtils.isNotEmpty(destPtp2)){
			    			    	mp.clear();
			    			    	mp.put("origPtp", origPtp2);
			    			    	mp.put("destPtp", destPtp2);
			    			    	List<Map<String,Object>> tunnelInfoListbak = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTurnnelInfoList",mp);
			    			    	if(tunnelInfoListbak!=null&& tunnelInfoListbak.size()>1){
				    			    	//隧道关联端口查询标签交换的下一跳端口记为A端上端站端口
			    			    		Map<String,Object> tunnelInfoMap = tunnelInfoListbak.get(1);
				    			    	String tunnelPtpA = (String)tunnelInfoMap.get("ORIG_PTP_CUID");
				    			    	String upPtpbak = (String)tunnelInfoMap.get("ORIG_UP_CUID");
				    			    	if(siteCuid.equals(origSite)&&origPtp2.equals(tunnelPtpA)){
				    			    		upSiteInfoMap.put("RELATED_UP_CUID2", upPtpbak);
				    			    		upSiteInfoMap.put("RELATED_TP_CUID2", destEqu2);
				    			    		upSiteInfoList.add(upSiteInfoMap);
				    			    	}else if(siteCuid.equals(destSite)&&destPtp.equals(tunnelPtpA)){
				    			    		upSiteInfoMap.put("RELATED_UP_CUID2", upPtpbak);
				    			    		upSiteInfoMap.put("RELATED_TP_CUID2", destEqu2);
				    			    		upSiteInfoList.add(upSiteInfoMap);
				    			    	}
				    			    }
			    			    }
			    			}
			    		}
			    	}
    				//当电路路由为MSAP时，电路A端的上端站为MSAP通道的对端设备即Z端设备，A端上端站端口即为MSAP通道Z端端口
    				if(msapPathCuidList != null && msapPathCuidList.size()>0){
    					for(String ptnPathCuid : msapPathCuidList){
    						Map upSiteInfoMap = new HashMap();
			    			Map<String,Object> attempTraphRouteInfoMap = attempTraphRouteInfoListMap.get(ptnPathCuid);
			    			if(attempTraphRouteInfoMap != null){
			    				String origSite = (String)attempTraphRouteInfoMap.get("ORIG_SITE_CUID");
			    				String destSite = (String)attempTraphRouteInfoMap.get("DEST_SITE_CUID");
			    				String origEqu = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID");
			    				String destEqu = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID");
			    				
			    				String origEqu2 = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID2");
			    				String destEqu2 = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID2");
			    				
			    				String origPtp = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID");
			    				String destPtp = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID");
			    				
			    				String origPtp2 = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID2");
			    				String destPtp2 = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID2");
			    				
			    				if(siteCuid.equals(origSite)){
			    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
		    			    		upSiteInfoMap.put("RELATED_UP_CUID", destPtp);
		    			    		upSiteInfoMap.put("RELATED_TP_CUID", origEqu);
		    			    		upSiteInfoList.add(upSiteInfoMap);
			    				}else if(siteCuid.equals(destSite)){
			    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
		    			    		upSiteInfoMap.put("RELATED_UP_CUID", origPtp);
		    			    		upSiteInfoMap.put("RELATED_TP_CUID", destEqu);
		    			    		upSiteInfoList.add(upSiteInfoMap);
			    				}
			    				//目前MSAP未含有双路由
			    				if(StringUtils.isNotEmpty(origEqu2)&&StringUtils.isNotEmpty(destEqu2)
			    						&&StringUtils.isNotEmpty(origPtp2)&&StringUtils.isNotEmpty(destPtp2)){
			    					if(siteCuid.equals(origSite)){
//				    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID2", destPtp2);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID2", origEqu2);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}else if(siteCuid.equals(destSite)){
//				    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID2", origPtp2);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID2", destEqu2);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}
			    					
			    				}
			    				
			    			}
    					}
    				}
    				//找其所在端口的拓扑对端，对端设备即为A端上端站设备
    				if(transPathCuidList != null && transPathCuidList.size()>0){
                        for(String ptnPathCuid : transPathCuidList){
                        	Map upSiteInfoMap = new HashMap();
			    			Map<String,Object> attempTraphRouteInfoMap = attempTraphRouteInfoListMap.get(ptnPathCuid);
			    			if(attempTraphRouteInfoMap != null){
			    				String origSite = (String)attempTraphRouteInfoMap.get("ORIG_SITE_CUID");
			    				String destSite = (String)attempTraphRouteInfoMap.get("DEST_SITE_CUID");
			    				String origEqu = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID");
			    				String destEqu = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID");
			    				
			    				String origEqu2 = (String)attempTraphRouteInfoMap.get("ORIG_EQU_CUID2");
			    				String destEqu2 = (String)attempTraphRouteInfoMap.get("DEST_EQU_CUID2");
			    				
			    				String origPtp = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID");
			    				String destPtp = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID");
			    				
			    				String origPtp2 = (String)attempTraphRouteInfoMap.get("ORIG_PTP_CUID2");
			    				String destPtp2 = (String)attempTraphRouteInfoMap.get("DEST_PTP_CUID2");
			    				
			    				
			    				mp.clear();
			    				List<String> ptpCuidList = new ArrayList<String>();
			    				ptpCuidList.add(origPtp);
			    				ptpCuidList.add(destPtp);
			    				mp.put("ptpCuidList", ptpCuidList);
			    				List<Map<String,Object>> topoInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTopoInfoList",mp);
			    			    if(topoInfoList!=null && topoInfoList.size()>0){
			    			    	String upPtpCuid = "";
			    			    	if(siteCuid.equals(origSite)){
			    			    		for(Map<String,Object> topoInfo : topoInfoList){
			    			    			if(origPtp.equals((String)topoInfo.get("TP_CUID"))){
			    			    				upPtpCuid = (String)topoInfo.get("RELATED_UP_CUID");
			    			    				break;
			    			    			}
			    			    		}
				    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID", upPtpCuid);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID", origEqu);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}else if(siteCuid.equals(destSite)){
				    					for(Map<String,Object> topoInfo : topoInfoList){
			    			    			if(destPtp.equals((String)topoInfo.get("TP_CUID"))){
			    			    				upPtpCuid = (String)topoInfo.get("RELATED_UP_CUID");
			    			    				break;
			    			    			}
			    			    		}
				    					upSiteInfoMap.put("RELATED_SERVICE_CUID", serviceCuid);
			    			    		upSiteInfoMap.put("RELATED_UP_CUID", upPtpCuid);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID", destEqu);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}
			    			    }
			    			    if(StringUtils.isNotEmpty(origPtp2)&&StringUtils.isNotEmpty(destPtp2)){
			    			    mp.clear();
			    				List<String> ptpCuidListbak = new ArrayList<String>();
			    				ptpCuidListbak.add(origPtp2);
			    				ptpCuidListbak.add(destPtp2);
			    				mp.put("ptpCuidList", ptpCuidListbak);
			    				List<Map<String,Object>> topoInfoListbak = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTopoInfoList",mp);
			    			    if(topoInfoListbak!=null && topoInfoListbak.size()>0){
			    			    	String upPtpCuid = "";
			    			    	if(siteCuid.equals(origSite)){
			    			    		for(Map<String,Object> topoInfo : topoInfoListbak){
			    			    			if(origPtp2.equals((String)topoInfo.get("TP_CUID"))){
			    			    				upPtpCuid = (String)topoInfo.get("RELATED_UP_CUID");
			    			    				break;
			    			    			}
			    			    		}
			    			    		upSiteInfoMap.put("RELATED_UP_CUID2", upPtpCuid);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID2", origEqu2);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}else if(siteCuid.equals(destSite)){
				    					for(Map<String,Object> topoInfo : topoInfoList){
			    			    			if(destPtp2.equals((String)topoInfo.get("TP_CUID"))){
			    			    				upPtpCuid = (String)topoInfo.get("RELATED_UP_CUID");
			    			    				break;
			    			    			}
			    			    		}
			    			    		upSiteInfoMap.put("RELATED_UP_CUID2", upPtpCuid);
			    			    		upSiteInfoMap.put("RELATED_TP_CUID2", destEqu2);
			    			    		upSiteInfoList.add(upSiteInfoMap);
				    				}
			    			    }
			    			}
			    			}
    					}
    				}
    			}
    		}
    	}
		return upSiteInfoList;
	}
*/
	//河南-LTE电路调度优化—根据Z端站点CUID获取LABEL_CN
	public Map<String, Object> getZdistrictName(String zDistrictCuid) {
		Map zDistrictNameMap = new HashMap();
		zDistrictNameMap.put("zDistrictCuid", zDistrictCuid);
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("zDistrictCuid", zDistrictCuid);
		List<Map<String, Object>> map = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryZdistrictName", pm);
		return map.get(0);
	}
	
	public List<Map<String, String>> getTraphRouteInfo(List<String> serviceCuidList) {
    	List<Map<String, String>> traphRouteInfo = new ArrayList<Map<String, String>>();
    	Map mp = new HashMap();
    	mp.put("serviceCuidList", serviceCuidList);
    	List<Map<String, Object>> traphRouteInfoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTraphRouteInfo", mp);
		if(traphRouteInfoList != null && traphRouteInfoList.size()>0){
			for(Map<String, Object> map : traphRouteInfoList){
				
				String traphCuid = StringUtils.isEmpty((String)map.get("CUID"))?"":(String)map.get("CUID");
				String traphName = StringUtils.isEmpty((String)map.get("LABEL_CN"))?"":(String)map.get("LABEL_CN");
				String origCtpCuid = StringUtils.isEmpty((String)map.get("ORIG_CTP_CUID"))?"":(String)map.get("ORIG_CTP_CUID");
				String origCtpName = StringUtils.isEmpty((String)map.get("ORIG_CTP_NAME"))?"":(String)map.get("ORIG_CTP_NAME");
				String origCtpCuid2 = StringUtils.isEmpty((String)map.get("ORIG_CTP_CUID2"))?"":(String)map.get("ORIG_CTP_CUID2");
				String origCtpName2 = StringUtils.isEmpty((String)map.get("ORIG_CTP_NAME2"))?"":(String)map.get("ORIG_CTP_NAME2");
				String destCtpCuid = StringUtils.isEmpty((String)map.get("DEST_CTP_CUID"))?"":(String)map.get("DEST_CTP_CUID");
				String destCtpName = StringUtils.isEmpty((String)map.get("DEST_CTP_NAME"))?"":(String)map.get("DEST_CTP_NAME");
				String destCtpCuid2 = StringUtils.isEmpty((String)map.get("DEST_CTP_CUID2"))?"":(String)map.get("DEST_CTP_CUID2");
				String destCtpName2 = StringUtils.isEmpty((String)map.get("DEST_CTP_NAME2"))?"":(String)map.get("DEST_CTP_NAME2");
				String aPtpCuid = StringUtils.isEmpty((String)map.get("RELATED_A_PTP_CUID"))?"":(String)map.get("RELATED_A_PTP_CUID");
				String aPtpName = StringUtils.isEmpty((String)map.get("RELATED_A_PTP_NAME"))?"":(String)map.get("RELATED_A_PTP_NAME");
				String aPtpCuid2 = StringUtils.isEmpty((String)map.get("RELATED_A_PTP_CUID2"))?"":(String)map.get("RELATED_A_PTP_CUID2");
				String aPtpName2 = StringUtils.isEmpty((String)map.get("RELATED_A_PTP_NAME2"))?"":(String)map.get("RELATED_A_PTP_NAME2");
				String zPtpCuid = StringUtils.isEmpty((String)map.get("RELATED_Z_PTP_CUID"))?"":(String)map.get("RELATED_Z_PTP_CUID");
				String zPtpName = StringUtils.isEmpty((String)map.get("RELATED_Z_PTP_NAME"))?"":(String)map.get("RELATED_Z_PTP_NAME");
				String zPtpCuid2 = StringUtils.isEmpty((String)map.get("RELATED_Z_PTP_CUID2"))?"":(String)map.get("RELATED_Z_PTP_CUID2");
				String zPtpName2 = StringUtils.isEmpty((String)map.get("RELATED_Z_PTP_NAME2"))?"":(String)map.get("RELATED_Z_PTP_NAME2");
				String aSideName = StringUtils.isEmpty((String)map.get("A_SIDE_NAME"))?"":(String)map.get("A_SIDE_NAME");
				String zSideName = StringUtils.isEmpty((String)map.get("Z_SIDE_NAME"))?"":(String)map.get("Z_SIDE_NAME");
				String l2l3PortCuid = StringUtils.isEmpty((String)map.get("L2L3_PORT_CUID"))?"":(String)map.get("L2L3_PORT_CUID");
				String l2l3PortName = StringUtils.isEmpty((String)map.get("L2L3_PORT_NAME"))?"":(String)map.get("L2L3_PORT_NAME");
				String l2l3PortCuid2 = StringUtils.isEmpty((String)map.get("L2L3_PORT_CUID2"))?"":(String)map.get("L2L3_PORT_CUID2");
				String l2l3PortName2 = StringUtils.isEmpty((String)map.get("L2L3_PORT_NAME2"))?"":(String)map.get("L2L3_PORT_NAME2");

				String origPointCuid = StringUtils.isEmpty(origCtpCuid)?l2l3PortCuid:origCtpCuid;  
				String origPointName = StringUtils.isEmpty(origCtpName)?l2l3PortName:aPtpName+origCtpName;
				String origPointCuid2 = StringUtils.isEmpty(origCtpCuid2)?l2l3PortCuid2:origCtpCuid2;
				String origPointName2 = StringUtils.isEmpty(origCtpName2)?l2l3PortName2:aPtpName2+origCtpName2;
				String destPointCuid = StringUtils.isEmpty(destCtpCuid)?zPtpCuid:destCtpCuid;
				String destPointName = StringUtils.isEmpty(destCtpName)?zPtpName:zPtpName+destCtpName;
				String destPointCuid2 = StringUtils.isEmpty(destCtpCuid2)?zPtpCuid2:destCtpCuid2;
				String destPointName2 = StringUtils.isEmpty(destCtpName2)?zPtpName2:zPtpName2+destCtpName2;
				
				String busIp = (String)map.get("BUSINESS_IP");
				String netCfgIp = (String)map.get("NETCONFIG_IP");
				String netWorkIpName = (String)map.get("NETWORK_IP_NAME");
				String netWorkcIpName = (String)map.get("NETWORKC_IP_NAME");
				String qos = (String)map.get("QOS_BAND");
				String cir = (String)map.get("CIR_BAND");
				String pir = (String)map.get("PIR_BAND");
				String servicePriority = (String)map.get("SERVICE_PRIORITY");
				String lteCode = (String)map.get("LTE_CODE");
				String vlan = (String)map.get("VLANID");
				String bsVlan = (String)map.get("BSVLANID");
//				String districtCuidZ = (String)map.get("RELATED_Z_DISTRICT_CUID");
				Map<String, String> newMap = new HashMap<String, String>();
				newMap.put("RELATED_SERVICE_CUID", traphCuid);
				newMap.put("RELATED_SERVICE_NAME", traphName);
				newMap.put("A_SIDE_CUID", origPointCuid);
				newMap.put("A_SIDE_FULL_NAME", origPointName);
				newMap.put("A_SIDE_NAME", aSideName);
				newMap.put("A_SIDE_CUID2", origPointCuid2);
				newMap.put("A_SIDE_FULL_NAME2", origPointName2);
				newMap.put("A_SIDE_NAME2", origCtpName2);
				newMap.put("Z_SIDE_CUID", destPointCuid);
				newMap.put("Z_SIDE_FULL_NAME", destPointName);
				newMap.put("Z_SIDE_NAME", zSideName);
				newMap.put("Z_SIDE_CUID2", destPointCuid2);
				newMap.put("Z_SIDE_FULL_NAME2", destPointName2);
				newMap.put("A_SIDE_CUID3", aPtpCuid);
				newMap.put("A_SIDE_NAME3", aPtpName);
				newMap.put("A_SIDE_CUID4", aPtpCuid2);
				newMap.put("A_SIDE_NAME4", aPtpName2);
				newMap.put("BUSINESS_IP_NAME", busIp);
				newMap.put("NETCONFIG_IP_NAME", netCfgIp);
				newMap.put("QOS_BAND", qos);
				newMap.put("CIR_BAND", cir);
				newMap.put("PIR_BAND", pir);
				newMap.put("SERVICE_PRIORITY", servicePriority);
				newMap.put("LTE_CODE", lteCode);
				newMap.put("VLANID", vlan);
				newMap.put("BSVLANID", bsVlan);
//				newMap.put("RELATED_Z_DISTRICT_CUID", districtCuidZ);
				
				newMap.put("A_SIDE_FULL_NAME3", origPointName);
				newMap.put("A_SIDE_FULL_NAME4", origPointName2);
				newMap.put("NETWORK_IP_NAME", netWorkIpName);
				newMap.put("NETWORKC_IP_NAME", netWorkcIpName);
				
				traphRouteInfo.add(newMap);
			}
		}
    	return traphRouteInfo;
	}

	private Boolean getFlag(){
		Boolean flag = false;
		if(SysProperty.getInstance().getValue("districtName").trim().equals("湖北")||
		SysProperty.getInstance().getValue("districtName").trim().equals("广西")){
			flag = true;
		}
		return flag;
	}

	/*public void saveLteTraphRouteInfo(ServiceActionContext ac, List<Map> traphRouteInfoList, String taskId) {
		if(traphRouteInfoList != null && traphRouteInfoList.size()>0){
			Date now = new Date();
			List<String> serviceCuidList = new ArrayList<String>();
			List<Record> traphUpdateRecordList = new ArrayList<Record>();
			List<Record> traphRecordPkList = new ArrayList<Record>();
			List<Record> extAttTraphRecordList= new ArrayList<Record>();
			List<Record> extAttTraphRecordPkList= new ArrayList<Record>();
			List<Record> attPtnPathRecordList= new ArrayList<Record>();
			List<Record> attTraphRouteRecordList = new ArrayList<Record>();
			List<Record> attRoutePathRecordList = new ArrayList<Record>();
			List<Record> rList = new ArrayList<Record>();
			for(Map map : traphRouteInfoList){
				String traphId = (String)map.get("RELATED_SERVICE_CUID");
				String traphName = (String)map.get("RELATED_SERVICE_NAME");
				String aSideCuid = (String)map.get("A_SIDE_CUID");    //A端主用端口
				String aSideName = (String)map.get("A_SIDE_NAME");
				String aSideFullName = (String)map.get("A_SIDE_FULL_NAME");
				String aSide = (String)map.get("A_SIDE");
				
				String aSideCuid2 = (String)map.get("A_SIDE_CUID2");  //A端备用端口
				String aSideName2 = (String)map.get("A_SIDE_NAME2");
				String aSideFullName2 = (String)map.get("A_SIDE_FULL_NAME2");
				
				String aSideCuid3 = (String)map.get("A_SIDE_CUID3");  //虚端口
				String aSideName3 = (String)map.get("A_SIDE_NAME3");
				String aSideFullName3 = (String)map.get("A_SIDE_FULL_NAME3");
				
				String aSideCuid4 = (String)map.get("A_SIDE_CUID4");  //备用虚端口
				String aSideName4 = (String)map.get("A_SIDE_NAME4");
				String aSideFullName4 = (String)map.get("A_SIDE_FULL_NAME4");
				
				String zSideCuid = (String)map.get("Z_SIDE_CUID");    //Z端主用端口
				String zSideName = (String)map.get("Z_SIDE_NAME");
				String zSideFullName = (String)map.get("Z_SIDE_FULL_NAME");
				String zSide = (String)map.get("Z_SIDE");
				
				String zSideCuid2 = (String)map.get("Z_SIDE_CUID2");   //Z端备用端口
				String zSideName2= (String)map.get("Z_SIDE_NAME2");
				String zSideFullName2 = (String)map.get("Z_SIDE_FULL_NAME2");
				
				String pirBand = (String)map.get("PIR_BAND");
				String lteCode = (String)map.get("LTE_CODE");
				String cirBand = (String)map.get("CIR_BAND");
				String qosBand = (String)map.get("QOS_BAND");
				String vlanId = (String)map.get("VLANID");
				String netWorkVlanId = (String)map.get("NETWORK_VLANID");
				String bsVlanId = (String)map.get("BSVLANID");
				String servicePriorty = (String)map.get("SERVICE_PRIORITY");
				
				String netConfigIpName = (String)map.get("NETCONFIG_IP_NAME");
				String businessIpName = (String)map.get("BUSINESS_IP_NAME");
				String networkIpName = (String)map.get("NETWORK_IP_NAME"); 
				String netWorkCIpName = (String)map.get("NETWORKC_IP_NAME");
				String labelCn = "";
				if(StringUtils.isNotEmpty(aSideCuid)){
					labelCn = aSide+"("+aSideFullName+")" +"("+zSideFullName+")"+zSide+"/VLanID="+vlanId;
				}else{
					labelCn = aSide+"("+aSideFullName3+")" +"("+zSideFullName+")"+zSide+"/VLanID="+vlanId;
				}
				
				//更新已关联了该电路IP的状态
				TraphDesignBO bo = (TraphDesignBO) SpringContextUtil.getBean("TraphDesignBO");
				bo.updateLogicIpType(traphId);
				//判断IP在数据库中是否存在，并更新将使用Ip的状态
				List<String> ipNameList = new ArrayList<String>();
				if(StringUtils.isNotEmpty(businessIpName)){
					ipNameList.add(businessIpName);
				}
				if(StringUtils.isNotEmpty(networkIpName)){
					ipNameList.add(networkIpName);
				}
				if(StringUtils.isNotEmpty(netConfigIpName)){
					ipNameList.add(netConfigIpName);
				}
				if(StringUtils.isNotEmpty(netWorkCIpName)){
					ipNameList.add(netWorkCIpName);
				}
				Map param = new HashMap();
				param.put("ipList", ipNameList);
				List<Map<String, Object>> ipCuidsList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList("TraphDesign"+".getBusinessIpCuid",param);
				Map<String, Map<String, Object>> ipNamesListMap = IbatisDAOHelper.parseList2Map(ipCuidsList, "ADDR");
				for(String ipName : ipNameList){
					if(ipNamesListMap.get(ipName) == null){
						throw new RuntimeException("数据库中无此IP("+ipName+")地址");
					}
				}
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateLogicIpType",param);

				Map cuidMap = new HashMap();
				cuidMap.put("cuid", traphId);

				//校验VLANID在数据库中是否存在
				String districtCuid = (String)map.get("RELATED_Z_DISTRICT_CUID");
				param.clear();
				param.put("relatedZDistrictCuid", districtCuid);
				List<Map<String, Object>> vlanidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanNoList",param);
				boolean bflag = true;
				boolean nflag = true;
				if(vlanidList != null && vlanidList.size()>0){
					for(Map<String, Object> vlanidMap : vlanidList){
						String bussinessVlan = IbatisDAOHelper.getStringValue(vlanidMap, "RELATED_RES_CUID");
						String networkVlan = IbatisDAOHelper.getStringValue(vlanidMap, "RELATED_RES_NAME");
						if(StringUtils.isNotEmpty(bussinessVlan)&&StringUtils.isNotEmpty(networkVlan)){
							String[] vlan = StringUtils.split(bussinessVlan, "-");
							int vlanMin = Integer.parseInt(vlan[0]);
							int vlanMax = Integer.parseInt(vlan[1]);
							String[] netVlan = StringUtils.split(networkVlan, "-");
							int netVlanMin = Integer.parseInt(netVlan[0]);
							int netVlanMax = Integer.parseInt(netVlan[1]);
							if(vlanMin <= Integer.parseInt(vlanId) && vlanMax >= Integer.parseInt(vlanId)){
								bflag = false;
							}
							if(StringUtils.isNotEmpty(netWorkVlanId)&&vlanMin <= Integer.parseInt(vlanId) && vlanMax >= Integer.parseInt(vlanId)
									&&netVlanMin <= Integer.parseInt(netWorkVlanId) && netVlanMax >= Integer.parseInt(netWorkVlanId)){
								nflag = false;
							}
						}
					}
				}
				if(StringUtils.isNotEmpty(netWorkVlanId)&&nflag){
					throw new RuntimeException("网管VLANID("+netWorkVlanId+")不存在!");
				}
				if(bflag){
					throw new RuntimeException("VLANID("+vlanId+")不存在");
				}
				//VLAN在同一L2/L3设备下不唯一性校验
				if(StringUtils.isNotEmpty(aSideCuid3)){
					this.checkVlan(aSideCuid3,null,vlanId,null,traphId);//校验虚端口VLANID
					this.checkVlan(aSideCuid3,null,null,netWorkVlanId, traphId);//校验虚端口网管VLANID
				}
				if(StringUtils.isNotEmpty(aSideCuid4)){
					this.checkVlan(null,aSideCuid4,vlanId,null,traphId);//校验备用虚端口VLANID
					this.checkVlan(null,aSideCuid4,null,netWorkVlanId,traphId);//校验备用虚端口网管VLANID
				}
				
				//添加T_EXT_ATTEMP_TRAPH表中端口或时隙的信息
				Record pk = new Record("T_EXT_ATTEMP_TRAPH");
				Record record = new Record("T_EXT_ATTEMP_TRAPH");
				if (StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("PTP")>-1){
					record.addColValue("RELATED_PTP_A_CUID", aSideCuid);
				}
				if (StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("CTP")>-1){
					record.addColValue("RELATED_CTP_A_CUID", aSideCuid);
				}
				if (StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("PTP")>-1){
					record.addColValue("RELATED_PTP_Z_CUID", zSideCuid);
				}
				if (StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("CTP")>-1){
					record.addColValue("RELATED_CTP_Z_CUID", zSideCuid);
				}
				pk.addColValue("CUID", traphId);
				extAttTraphRecordList.add(record);
				extAttTraphRecordPkList.add(pk);
				
				//更新ATTEMP_TRAPH表中的信息
				Record traphUpdateRecord = new Record("ATTEMP_TRAPH");
				Record traphRecordPk = new Record("ATTEMP_TRAPH");
				traphRecordPk.addColValue("CUID", traphId);
				if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("CTP")>-1){
					traphUpdateRecord.addColSqlValue("RELATED_A_PORT_CUID", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "'");
					traphUpdateRecord.addColSqlValue("END_PORT_A", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "')");
					traphUpdateRecord.addColSqlValue("RELATED_NE_A_CUID", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "'");
					traphUpdateRecord.addColSqlValue("RELATED_A_ROOM_CUID", "SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aSideCuid + "'");
				}else if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("PTP")>-1){
					traphUpdateRecord.addColValue("RELATED_A_PORT_CUID", aSideCuid);
					traphUpdateRecord.addColValue("END_PORT_A", aSideFullName);
					traphUpdateRecord.addColSqlValue("RELATED_NE_A_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aSideCuid + "'");
					traphUpdateRecord.addColSqlValue("RELATED_A_ROOM_CUID", "SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aSideCuid + "'");
					
				}
				if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("CTP")>-1){
					traphUpdateRecord.addColSqlValue("RELATED_Z_PORT_CUID", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zSideCuid + "'");
					traphUpdateRecord.addColSqlValue("END_PORT_Z", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zSideCuid + "')");
					traphUpdateRecord.addColSqlValue("RELATED_NE_Z_CUID", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zSideCuid + "'");
					traphUpdateRecord.addColSqlValue("RELATED_Z_ROOM_CUID", "SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zSideCuid + "'");
				}else if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("PTP")>-1){
					traphUpdateRecord.addColValue("RELATED_Z_PORT_CUID", zSideCuid);
					traphUpdateRecord.addColValue("END_PORT_Z", zSideFullName);
					traphUpdateRecord.addColSqlValue("RELATED_NE_Z_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zSideCuid + "'");
					traphUpdateRecord.addColSqlValue("RELATED_Z_ROOM_CUID", "SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zSideCuid + "'");
				}
				if(!traphName.endsWith("/P")){
					traphName = traphName +"/P";
				}
				traphUpdateRecord.addColValue("LABEL_CN", traphName);
				traphUpdateRecord.addColValue("PATHINFO", labelCn);
				traphUpdateRecord.addColValue("EXT_TYPE", "7");
				traphUpdateRecord.addColValue("DESIGN_INFO", labelCn);
				traphUpdateRecord.addColValue("IS_WHOLE_ROUTE", 1);
				traphUpdateRecord.addColValue("LAST_MODIFY_TIME", now);
				traphUpdateRecordList.add(traphUpdateRecord);
				traphRecordPkList.add(traphRecordPk);
				
				//删除已存在的路由信息
				List cuidList = new ArrayList();
				cuidList.add(traphId);
				this.releaseAttempTraphRelation(ac,cuidList);

				//更新端口状态
				List<String> ptpCuidList = new ArrayList<String>();
				List<String> ctpCuidList = new ArrayList<String>();
				if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("CTP")>-1){
					ctpCuidList.add(aSideCuid);
				}else if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("PTP")>-1){
					ptpCuidList.add(aSideCuid);
				}
				if(StringUtils.isNotEmpty(aSideCuid2) && aSideCuid2.indexOf("CTP")>-1){
					ctpCuidList.add(aSideCuid2);
				}else if(StringUtils.isNotEmpty(aSideCuid2) && aSideCuid2.indexOf("PTP")>-1){
					ptpCuidList.add(aSideCuid2);
				}
				if(StringUtils.isNotEmpty(aSideCuid3) && aSideCuid3.indexOf("CTP")>-1){
					ctpCuidList.add(aSideCuid3);
				}else if(StringUtils.isNotEmpty(aSideCuid3) && aSideCuid3.indexOf("PTP")>-1){
					ptpCuidList.add(aSideCuid3);
				}
				if(StringUtils.isNotEmpty(aSideCuid4) && aSideCuid4.indexOf("CTP")>-1){
					ctpCuidList.add(aSideCuid4);
				}else if(StringUtils.isNotEmpty(aSideCuid4) && aSideCuid4.indexOf("PTP")>-1){
					ptpCuidList.add(aSideCuid4);
				}
				if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("CTP")>-1){
					ctpCuidList.add(zSideCuid);
				}else if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("PTP")>-1){
					ptpCuidList.add(zSideCuid);
				}
				if(StringUtils.isNotEmpty(zSideCuid2) && zSideCuid2.indexOf("CTP")>-1){
					ctpCuidList.add(aSideCuid2);
				}else if(StringUtils.isNotEmpty(zSideCuid2) && zSideCuid2.indexOf("PTP")>-1){
					ptpCuidList.add(zSideCuid2);
				}
				if(!ptpCuidList.isEmpty()){
					logger.info("<!------------------更新端口的状态是预占用------------------>");
					this.setPtpState(ptpCuidList, 3, 1);
					//变更预调度端口状态
					this.setPtpNaState(ptpCuidList, 3, 1);
				}
				if(!ctpCuidList.isEmpty()){
					logger.info("<!------------------更新时隙的状态是预占用------------------>");
					this.setCtpState(ctpCuidList, 3);
					//变更预调度时隙状态
					this.setCtpNaState(ctpCuidList, 3);
				}
				
				//向数据库中插入端口等信息
				Record attPtnPathRecord = new Record("ATTEMP_PTN_PATH");
				Record attTraphRouteRecord = new Record("ATTEMP_TRAPH_ROUTE");
				Record attRoutePathRecord = new Record("ATTTRAPH_ROUTE_TO_PATH");
				int ctpCount = 1;
				String attPtnPathCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_PTN_PATH");
				if(StringUtils.isNotEmpty(traphId)){
					attPtnPathRecord.addColSqlValue("OBJECTID", attPtnPathRecord.getObjectIdSql());
					attPtnPathRecord.addColValue("CUID", attPtnPathCuid);
					attPtnPathRecord.addColValue("LABEL_CN", labelCn);
					attPtnPathRecord.addColValue("RELATED_ROUTE_CUID", traphId);
					attPtnPathRecord.addColValue("ROUTE_DESCIPTION", labelCn);
					attPtnPathRecord.addColValue("VLANID",vlanId );
					attPtnPathRecord.addColValue("QOS_BAND", qosBand);
					attPtnPathRecord.addColValue("CIR_BAND", cirBand);
					attPtnPathRecord.addColValue("PIR_BAND", pirBand);
					attPtnPathRecord.addColValue("BSVLANID", bsVlanId);
					attPtnPathRecord.addColValue("LTE_CODE", lteCode);
					attPtnPathRecord.addColValue("SERVICE_PRIORITY", servicePriorty);
					//处理A、Z端数据，A端端口与虚端口存储位置互换
					if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("CTP")>-1){
						attPtnPathRecord.addColValue("ORIG_CTP_CUID", aSideCuid);
						attPtnPathRecord.addColValue("ORIG_CTP_NAME", aSideFullName);
						attPtnPathRecord.addColSqlValue("RELATED_A_NE_CUID", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "'");
						attPtnPathRecord.addColSqlValue("L2L3_PORT_CUID", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "'");
						attPtnPathRecord.addColSqlValue("L2L3_PORT_NAME", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid + "')");
					}else if(StringUtils.isNotEmpty(aSideCuid) && aSideCuid.indexOf("PTP")>-1){
						ctpCount = this.IbatisResDAO.calculate("SELECT COUNT(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aSideCuid + "'");
						if(ctpCount == 1) {
							attPtnPathRecord.addColSqlValue("ORIG_CTP_CUID", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aSideCuid + "'");
							attPtnPathRecord.addColSqlValue("ORIG_CTP_NAME", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aSideCuid + "'");
						}else if(ctpCount != 0){
							throw new RuntimeException("您所选择的端口:"+aSideFullName+"，时隙数量不唯一！");
						}
						attPtnPathRecord.addColValue("L2L3_PORT_CUID", aSideCuid);
						attPtnPathRecord.addColValue("L2L3_PORT_NAME", aSideFullName);
						attPtnPathRecord.addColSqlValue("RELATED_A_NE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aSideCuid + "'");
					}
					logger.info("A端数据处理完毕!");
					logger.info("开始处理Z端数据!");
					if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("CTP")>-1){
						attPtnPathRecord.addColValue("DEST_CTP_CUID", zSideCuid);
						attPtnPathRecord.addColValue("DEST_CTP_NAME", zSideFullName);
						attPtnPathRecord.addColSqlValue("RELATED_Z_NE_CUID", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zSideCuid + "'");
						attPtnPathRecord.addColSqlValue("RELATED_Z_PTP_CUID", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zSideCuid + "'");
					}else if(StringUtils.isNotEmpty(zSideCuid) && zSideCuid.indexOf("PTP")>-1){
						ctpCount = this.IbatisResDAO.calculate("SELECT COUNT(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zSideCuid + "'");
						if(ctpCount == 1) {
							attPtnPathRecord.addColSqlValue("DEST_CTP_CUID", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zSideCuid + "'");
							attPtnPathRecord.addColSqlValue("DEST_CTP_NAME", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zSideCuid + "'");
						}else if(ctpCount != 0){
							throw new RuntimeException("您所选择的端口:"+zSideFullName+"，时隙数量不唯一！");
						}
						attPtnPathRecord.addColValue("RELATED_Z_PTP_CUID", zSideCuid);
						attPtnPathRecord.addColSqlValue("RELATED_Z_NE_CUID", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zSideCuid + "'");
					}
					logger.info("Z端数据处理完毕!");
					logger.info("开始处理A端备用数据!");
					if(StringUtils.isNotEmpty(aSideCuid2) && aSideCuid2.indexOf("CTP")>-1){
						attPtnPathRecord.addColValue("ORIG_CTP_CUID2", aSideCuid2);
						attPtnPathRecord.addColValue("ORIG_CTP_NAME2", aSideFullName2);
						attPtnPathRecord.addColSqlValue("RELATED_A_NE_CUID2", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aSideCuid2 + "'");
						attPtnPathRecord.addColSqlValue("L2L3_PORT_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid2 + "'");
						attPtnPathRecord.addColSqlValue("L2L3_PORT_NAME2", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aSideCuid2 + "')");
					}else if(StringUtils.isNotEmpty(aSideCuid2) && aSideCuid2.indexOf("PTP")>-1){
						ctpCount = this.IbatisResDAO.calculate("SELECT COUNT(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aSideCuid2 + "'");
						if(ctpCount == 1) {
							attPtnPathRecord.addColSqlValue("ORIG_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aSideCuid2 + "'");
							attPtnPathRecord.addColSqlValue("ORIG_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aSideCuid2 + "'");
						}else if(ctpCount != 0){
							throw new RuntimeException("您所选择的端口:"+aSideFullName2+"，时隙数量不唯一！");
						}
						attPtnPathRecord.addColValue("L2L3_PORT_CUID2", aSideCuid2);
						attPtnPathRecord.addColValue("L2L3_PORT_NAME2", aSideFullName2);
						attPtnPathRecord.addColSqlValue("RELATED_A_NE_CUID2", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aSideCuid2 + "'");
					}
					logger.info("A端备用数据处理完毕!");
					logger.info("开始处理Z端备用数据!");
					if(StringUtils.isNotEmpty(zSideCuid2) && zSideCuid2.indexOf("CTP")>-1){
						attPtnPathRecord.addColValue("DEST_CTP_CUID2", zSideCuid2);
						attPtnPathRecord.addColValue("DEST_CTP_NAME2", zSideFullName2);
						attPtnPathRecord.addColSqlValue("RELATED_Z_NE_CUID2", "SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zSideCuid2 + "'");
						attPtnPathRecord.addColSqlValue("RELATED_Z_PTP_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zSideCuid2 + "'");
					}else if(StringUtils.isNotEmpty(zSideCuid2) && zSideCuid2.indexOf("PTP")>-1){
						ctpCount = this.IbatisResDAO.calculate("SELECT COUNT(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zSideCuid2 + "'");
						if(ctpCount == 1) {
							attPtnPathRecord.addColSqlValue("DEST_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zSideCuid2 + "'");
							attPtnPathRecord.addColSqlValue("DEST_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zSideCuid2 + "'");
						}else if(ctpCount != 0){
							throw new RuntimeException("您所选择的端口:"+zSideFullName2+"，时隙数量不唯一！");
						}
						attPtnPathRecord.addColValue("RELATED_Z_PTP_CUID2", zSideCuid2);
						attPtnPathRecord.addColSqlValue("RELATED_Z_NE_CUID2", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zSideCuid2 + "'");
					}
					logger.info("Z端备用数据处理完毕!");
					attPtnPathRecord.addColValue("RELATED_A_PTP_CUID", aSideCuid3);
					attPtnPathRecord.addColValue("RELATED_A_PTP_CUID2", aSideCuid4);
					
					attPtnPathRecord.addColValue("PATH_TYPE", 2);
					attPtnPathRecord.addColValue("ISDELETE", "0");
					attPtnPathRecord.addColSqlValue("ORIG_POINT_CUID", "SELECT RELATED_A_ZD_SITE_CUID FROM ATTEMP_TRAPH WHERE CUID = '"+traphId+"'");
					attPtnPathRecord.addColSqlValue("DEST_POINT_CUID", "SELECT RELATED_Z_ZD_SITE_CUID FROM ATTEMP_TRAPH WHERE CUID = '"+traphId+"'");
					attPtnPathRecord.addColSqlValue("DEST_SITE_TYPE", "SELECT ZD_SITE_TYPE_Z FROM ATTEMP_TRAPH WHERE CUID = '"+traphId+"'");
					attPtnPathRecord.addColSqlValue("ORIG_SITE_TYPE", "SELECT ZD_SITE_TYPE_A FROM ATTEMP_TRAPH WHERE CUID = '"+traphId+"'");
					attPtnPathRecord.addColValue("CREATE_TIME", now);
					attPtnPathRecord.addColValue("LAST_MODIFY_TIME", now);
					
					//ATTEMP_TRAPH_ROUTE数据处理
					String attTraphRouteCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_TRAPH_ROUTE");
					attTraphRouteRecord.addColValue("CUID", attTraphRouteCuid);
					attTraphRouteRecord.addColSqlValue("OBJECTID", attTraphRouteRecord.getObjectIdSql());
					attTraphRouteRecord.addColValue("GT_VERSION", "0");
					attTraphRouteRecord.addColValue("OBJECT_TYPE_CODE", "9002");
					attTraphRouteRecord.addColValue("ROUTE_INDEX", "0");
					attTraphRouteRecord.addColValue("WORK_STATE", "1");
					attTraphRouteRecord.addColValue("RELATED_SERVICE_CUID", traphId);
					attTraphRouteRecord.addColValue("ROUTE_DESCRIPTION", labelCn);
					attTraphRouteRecord.addColValue("ISDELETE", "0");
					attTraphRouteRecord.addColValue("CREATE_TIME", now);
					attTraphRouteRecord.addColValue("LAST_MODIFY_TIME", now);

					//ATTTRAPH_ROUTE_TO_PATH数据处理
					attRoutePathRecord.addColValue("CUID", CUIDHexGenerator.getInstance().generate("ATTTRAPH_ROUTE_TO_PATH"));
					attRoutePathRecord.addColSqlValue("OBJECTID", attRoutePathRecord.getObjectIdSql());
					attRoutePathRecord.addColValue("GT_VERSION", "0");
					attRoutePathRecord.addColValue("PATH_TYPE", "ATTEMP_PTN_PATH");
					attRoutePathRecord.addColValue("INDEX_PATH_ROUTE", "0");
					attRoutePathRecord.addColValue("PATH_CUID", attPtnPathCuid);
					attRoutePathRecord.addColValue("TRAPH_ROUTE_CUID", attTraphRouteCuid);
					attRoutePathRecord.addColValue("ISDELETE", "0");
					attRoutePathRecord.addColValue("CREATE_TIME", now);
					attRoutePathRecord.addColValue("LAST_MODIFY_TIME", now);

				}
				attTraphRouteRecordList.add(attTraphRouteRecord);
				attPtnPathRecordList.add(attPtnPathRecord);
				attRoutePathRecordList.add(attRoutePathRecord);
				
				//向t_attemp_ptn_path_to_ip中插入IP和VLANID信息
				Record rBusinessIpName = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				Record rNetConfigIpName = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				Record rNetworkIpName = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				Record rNetWorkCIpName = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				Record rVlanIdName = new Record("T_ATTEMP_PTN_PATH_TO_IP");
				Record rNetWorkVlanIdName = new Record("T_ATTEMP_PTN_PATH_TO_IP");

				//添加业务IP
				if (!StringUtils.isEmpty(businessIpName)){
					rBusinessIpName.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '"+businessIpName+"' AND ROWNUM=1");
					rBusinessIpName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rBusinessIpName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rBusinessIpName.addColValue("TYPE", "BUSINESS");
					rList.add(rBusinessIpName);
				}
				
				//添加网关地址
				if(!StringUtils.isEmpty(netConfigIpName)){
					rNetConfigIpName.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '"+netConfigIpName+"' AND ROWNUM=1");
					rNetConfigIpName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rNetConfigIpName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rNetConfigIpName.addColValue("TYPE", "NETCONFIG");
					rList.add(rNetConfigIpName);
				}
				
				//添加网管IP NETWORK_IP_NAME 
				if (!StringUtils.isEmpty(networkIpName)){
					rNetworkIpName.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '"+networkIpName+"' AND ROWNUM=1");
					rNetworkIpName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rNetworkIpName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rNetworkIpName.addColValue("TYPE", "NM_IP");
					rList.add(rNetworkIpName);

				}
				
				//添加网管网关地址    NM_NETCONFIG
				if(!StringUtils.isEmpty(netWorkCIpName)){
					rNetWorkCIpName.addColSqlValue("RELATED_NUMBER_IP_CUID", "SELECT CUID FROM T_LOGIC_NUMBER_IP WHERE ADDR = '"+netWorkCIpName+"' AND ROWNUM=1");
					rNetWorkCIpName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rNetWorkCIpName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rNetWorkCIpName.addColValue("TYPE", "NM_NETCONFIG");
					rList.add(rNetWorkCIpName);
				}
				
				//添加VLANID   VLAN
				if(!StringUtils.isEmpty(vlanId)){
					rVlanIdName.addColValue("RELATED_NUMBER_IP_CUID", vlanId);
					rVlanIdName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rVlanIdName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rVlanIdName.addColValue("TYPE", "VLAN");
					rList.add(rVlanIdName);
				}
				
				//添加网管VLANID  NM_VLANID
				if(!StringUtils.isEmpty(netWorkVlanId)){
					rNetWorkVlanIdName.addColValue("RELATED_NUMBER_IP_CUID", netWorkVlanId);
					rNetWorkVlanIdName.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_PTN_PATH_TO_IP"));
					rNetWorkVlanIdName.addColValue("RELATED_PTN_PATH_CUID", attPtnPathCuid);
					rNetWorkVlanIdName.addColValue("TYPE", "NM_VLANID");
					rList.add(rNetWorkVlanIdName);
				}
				serviceCuidList.add(traphId);
			}
			if(!traphUpdateRecordList.isEmpty() && traphUpdateRecordList.size()>0){
				this.IbatisResDAO.updateDynamicTableBatch(traphUpdateRecordList, traphRecordPkList);
			}
			if(!extAttTraphRecordList.isEmpty() && extAttTraphRecordList.size()>0){
				this.IbatisResDAO.updateDynamicTableBatch(extAttTraphRecordList, extAttTraphRecordPkList);
			}
			if(!attPtnPathRecordList.isEmpty() && attPtnPathRecordList.size()>0){
				this.IbatisResDAO.insertDynamicTableBatch(attPtnPathRecordList);
			}
			if(!attTraphRouteRecordList.isEmpty() && attTraphRouteRecordList.size()>0){
				this.IbatisResDAO.insertDynamicTableBatch(attTraphRouteRecordList);
			}
			if(!attRoutePathRecordList.isEmpty() && attRoutePathRecordList.size()>0){
				this.IbatisResDAO.insertDynamicTableBatch(attRoutePathRecordList);
			}
			if(!rList.isEmpty() && rList.size()>0){
				this.IbatisResDAO.insertDynamicTableBatch(rList);
			}
			//修改taskService的状态
			ProcessBO.updateTaskServiceState(taskId, serviceCuidList, SheetConstants.SERVICE_STATE_DESIGN_SUCCESS, "");
		}
	}
	*/
	/**
	 * VLAN在同一L2/L3设备下不唯一性校验
	 * @param ptpCuid1
	 * @param ptpCuid2
	 * @param vlanId
	 * @param nmVlanId
	 * @param traphId
	 */
	/*public void checkVlan(String ptpCuid1, String ptpCuid2 , String vlanId,String nmVlanId,String traphId){
		Map<String,Object> m = new HashMap<String, Object>();
		m.put("traphId", traphId);
		m.put("vlanId", vlanId);
		m.put("nmVlanId",nmVlanId);
		if(StringUtils.isNotEmpty(ptpCuid1)){
			m.put("ptpCuid1", ptpCuid1);
			List<Map<String,Object>> vlanList1 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanList1", m);
			if(vlanList1 != null && vlanList1.size()>0){
				for (Map<String,Object> mp : vlanList1){
					String vlan = IbatisDAOHelper.getStringValue(mp, "RELATED_NUMBER_IP_CUID");
					String neName = IbatisDAOHelper.getStringValue(mp, "LABEL_CN");
					if(StringUtils.isNotEmpty(vlanId) && vlan.equals(vlanId)){
						throw new RuntimeException("VLANID在"+neName+"网元下不唯一");
					}
					if (StringUtils.isNotEmpty(nmVlanId) && vlan.equals(nmVlanId)){
						throw new RuntimeException("网管VLANID在"+neName+"网元下不唯一");
					}
				}
			}
		}
		if(StringUtils.isNotEmpty(ptpCuid2)){
			m.put("ptpCuid2", ptpCuid2);
			List<Map<String,Object>> vlanList2 = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanList2", m);
			if(vlanList2 != null && vlanList2.size()>0){
				for (Map<String,Object> mp : vlanList2){
					String vlan = IbatisDAOHelper.getStringValue(mp, "RELATED_NUMBER_IP_CUID");
					String neName = IbatisDAOHelper.getStringValue(mp, "LABEL_CN");
					if(StringUtils.isNotEmpty(vlanId) && vlan.equals(vlanId)){
						throw new RuntimeException("VLANID在"+neName+"网元下不唯一");
					}
					if (StringUtils.isNotEmpty(nmVlanId) && vlan.equals(nmVlanId)){
						throw new RuntimeException("网管VLANID在"+neName+"网元下不唯一");
					}
				}
			}
		}
	}
	
	public void saveFirstCheckInfo(Map firstcheckInfoMap,String relatedOrderCuid){
	    Map<String, Object> pm = new HashMap<String, Object>();
	    pm.put("relatedOrderCuid", relatedOrderCuid);
	    List<Map> cuid=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getcuidByrelated", pm);;
	    if(cuid.isEmpty()){
		    if(firstcheckInfoMap != null && !firstcheckInfoMap.isEmpty()){
			Record record = new Record("T_EXT_ACT_ORDER");
			record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_EXT_ACT_ORDER"));
			record.addColValue("RELATED_ORDER_CUID", relatedOrderCuid);
			record.addColValue("ACCESS_DOWN_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "ACCESS_DOWN_STATION"));
			record.addColValue("ACCESS_UP_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "ACCESS_UP_STATION"));
			record.addColValue("DESIGN_ACCESS_DOWN_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_ACCESS_DOWN_STATION"));
			record.addColValue("DESIGN_ACCESS_UP_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_ACCESS_UP_STATION"));
			record.addColValue("DESIGN_IS_RING", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_IS_RING"));
			record.addColValue("GREEN_PROCESS_NO", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "GREEN_PROCESS_NO"));
			record.addColValue("IS_ACCORD_WITH_DESIGN", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_ACCORD_WITH_DESIGN"));
			record.addColValue("IS_GREEN_PROCESS", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_GREEN_PROCESS"));
			record.addColValue("IS_RING", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_RING"));
			record.addColValue("IS_USE_PHYSICAL_CARD", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_USE_PHYSICAL_CARD"));
			record.addColValue("REMARK", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "REMARK"));
			record.addColValue("STATE", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "STATE"));
			this.IbatisResDAO.insertDynamicTable(record);
		    }
	    }else{
		    Record param = new Record("T_EXT_ACT_ORDER");
		    Record pk = new Record("T_EXT_ACT_ORDER");
		    pk.addColValue("RELATED_ORDER_CUID", relatedOrderCuid);
		    param.addColValue("ACCESS_DOWN_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "ACCESS_DOWN_STATION"));
		    param.addColValue("ACCESS_UP_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "ACCESS_UP_STATION"));
		    param.addColValue("DESIGN_ACCESS_DOWN_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_ACCESS_DOWN_STATION"));
		    param.addColValue("DESIGN_ACCESS_UP_STATION", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_ACCESS_UP_STATION"));
		    param.addColValue("DESIGN_IS_RING", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "DESIGN_IS_RING"));
		    param.addColValue("GREEN_PROCESS_NO", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "GREEN_PROCESS_NO"));
		    param.addColValue("IS_ACCORD_WITH_DESIGN", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_ACCORD_WITH_DESIGN"));
		    param.addColValue("IS_GREEN_PROCESS", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_GREEN_PROCESS"));
		    param.addColValue("IS_RING", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_RING"));
		    param.addColValue("IS_USE_PHYSICAL_CARD", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "IS_USE_PHYSICAL_CARD"));
		    param.addColValue("REMARK", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "REMARK"));
		    param.addColValue("STATE", IbatisDAOHelper.getStringValue(firstcheckInfoMap, "STATE"));
    	    this.IbatisResDAO.updateDynamicTable(param, pk);
	    }
    }
	
	public List<Map<String,Object>> ifRole(String designUser){
		Map<String,Object> mp = new HashMap<String, Object>();
		mp.put("role", designUser);
		List<Map<String,Object>> list =  this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".validetionRole",mp);
		return list;
	}
	
	public Map<String, List<Integer>> findVlanNoListExist(int startNum, int num, String vlan, String state, String relatedZDistrictCuid){
		Map<String, Object> am = new HashMap<String, Object>();
		am.put("relatedZDistrictCuid", relatedZDistrictCuid);
		List<Map<String, Object>> vlanNoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanNoList", am);
		List<Integer> vlanList = new ArrayList<Integer>();
		List<Integer> vlanFreeList = new ArrayList<Integer>();
		List<Integer> vlanUsedList = new ArrayList<Integer>();
		Map<String, List<Integer>> noMap = new HashMap<String, List<Integer>>();
		if(vlanNoList != null && vlanNoList.size()>0){
			for(Map<String, Object> map : vlanNoList){
				//业务VLAN
				String vlanBS = IbatisDAOHelper.getStringTrueValue(map,"RELATED_RES_CUID");//业务VLAN
				String startVlanBe = vlanBS.substring(0, vlanBS.lastIndexOf("-"));//业务起始VLAN
				String endVlanBe = vlanBS.substring(vlanBS.indexOf("-")+1,vlanBS.length());//业务终止VLAN
				int startVlanBeing = Integer.parseInt(startVlanBe);
				int endVlanBeing = Integer.parseInt(endVlanBe);
				for(int i=startVlanBeing;i<=endVlanBeing;i++){
					vlanList.add(i);
				}
			}
			//查询已占用vlan
			List<Integer> vlanUsedTempList = new ArrayList<Integer>();
			List<Map<String, Object>> vlanNoUsedList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanNoListUsed", am);
			if(vlanNoUsedList != null && vlanNoUsedList.size()>0){
				for(Map<String, Object> vlanNoUsedMap : vlanNoUsedList){
					int usedVlan = Integer.parseInt(IbatisDAOHelper.getStringValue(vlanNoUsedMap, "RELATED_NUMBER_IP_CUID"));
					if(!vlanUsedTempList.contains(usedVlan)){
						vlanUsedTempList.add(usedVlan);
					}
				}
			}
			//查询状态为空闲的vlan
			if(StringUtils.isNotEmpty(state)&&state.equals("1")){
				for(int no : vlanUsedTempList){
					if(vlanList.contains(no)){
						vlanList.remove(no);    //剩余未占用vlan
					}
				}
				vlanFreeList = findVlanList(startNum, num, vlan, vlanList);
				noMap.put("USED_VLAN", null);
				noMap.put("FREE_VLAN", vlanFreeList);
			}
			//查询状态为占用的vlan
			if(StringUtils.isNotEmpty(state)&&state.equals("2")){
				vlanUsedList = findVlanList(startNum, num, vlan, vlanUsedTempList);
				noMap.put("USED_VLAN", vlanUsedList);
				noMap.put("FREE_VLAN", null);
			}
			//查询状态为空的vlan
			if(StringUtils.isEmpty(state)){
				List<Integer> vList = findVlanList(startNum, num, vlan, vlanList);
				for(int no : vlanUsedTempList){
					if(vList.contains(no)){
						vlanUsedList.add(no);   //占用vlan
					}
				}
				vList.removeAll(vlanUsedList);   //剩余未占用vlan
				noMap.put("USED_VLAN", vlanUsedList);
				noMap.put("FREE_VLAN", vList);
			}
		}
		return noMap;
	}*/
	/**
	 * 过滤出显示到页面的Vlan
	 * @param startNum
	 * @param num
	 * @param vlan
	 * @param vlanList
	 */
/*	public List<Integer> findVlanList(int startNum, int num, String vlan, List<Integer> vlanList){
		List<Integer> noList = new ArrayList<Integer>();
		List<Integer> list = new ArrayList<Integer>();
		Collections.sort(vlanList);
		int a=0;
		int b=0;
		int i=0;
		int m=0;
		for(i=0;i<vlanList.size();i++){
			if(vlanList.get(i)==startNum){
				a = i;
				break;
			}else if(vlanList.get(i)>startNum){
				a = i;
				break;
			}else if(vlanList.get(vlanList.size()-1)<startNum){
				throw new RuntimeException("不存在大于"+startNum+"的VLAN！");
			}
		}
		
		//如果VLAN不为空就根据条件获取集合元素
		if(vlan != null){
			for(b=i;b<vlanList.size();b++){
				int n = vlanList.get(b).toString().indexOf(vlan);
				if(n != -1){
					list.add(vlanList.get(b));
				}
			}
			if(list.size()<num){
				for(m=0;m<list.size();m++){
					noList.add(list.get(m));
				}
			}else{
				for(m=0;m<num;m++){
					noList.add(list.get(m));
				}
			}
		}else{
			if((vlanList.size()-a)<num){
				for(a=i;a<vlanList.size();a++){
					noList.add(vlanList.get(a));
				}
			}else{
				for(a=i;a<i+num;a++){
					noList.add(vlanList.get(a));
				}
			}
		}
		return noList;
	}
	
	public List<Integer> findVlanNM(int vlanID, String relatedZDistrictCuid){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("relatedZDistrictCuid", relatedZDistrictCuid);
		List<Map<String, Object>> vlanNoList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryVlanNoList", pm);
		List<Integer> vlanList = new ArrayList<Integer>();
		for(Map<String, Object> map : vlanNoList){
			//业务VLAN
			String vlanBS = IbatisDAOHelper.getStringTrueValue(map,"RELATED_RES_CUID");//业务VLAN
			String startVlanBe = vlanBS.substring(0, vlanBS.lastIndexOf("-"));//业务起始VLAN
			String endVlanBe = vlanBS.substring(vlanBS.indexOf("-")+1,vlanBS.length());//业务终止VLAN
			int startVlanBeing = Integer.parseInt(startVlanBe);//业务起始VLAN
			int endVlanBeing = Integer.parseInt(endVlanBe);//业务终止VLAN
			//网管VLAN
			String vlanNM = IbatisDAOHelper.getStringTrueValue(map,"RELATED_RES_NAME");//网管VLAN
			String startVlanBeNM = vlanNM.substring(0, vlanNM.lastIndexOf("-"));//网管起始VLAN
			int startVlanBeingNM = Integer.parseInt(startVlanBeNM);//网管起始VLAN
			if(vlanID >= startVlanBeing && vlanID <= endVlanBeing){
				int difValuue = startVlanBeingNM - startVlanBeing;//差值
				int vlanIdNM = vlanID + difValuue;//与业务vlanID相对应的网管vlanIdNM
				vlanList.add(vlanIdNM);
				break;
			}
		}
		return vlanList;
	}
	
	public List<Map<String,Object>> getOrderTraphInfo(String orderId){
		Map pm = new HashMap();
		pm.put("orderId", orderId);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getOrderTraphInfo", pm);
		return list;
	}
	*//**
	 * 查询该工单下所有电路，再根据电路获取所有的活动任务
	 * @param relatedSheetCuid
	 *//*
	public List<TaskInst> findServiceCuidsBySheetId(String relatedSheetCuid){
		Map mp = new HashMap();
		mp.put("sheetCuid", relatedSheetCuid);
		List<String> serviceIdList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSheetToService", mp);
		List<TaskInst> runTaskList = ProcessBO.findRunTaskInstByService(serviceIdList);
		return runTaskList;
	}
	public List<Map<String,Object>> findTComBtsCircuit(String label_cn){
		Map<String,Object> mp = new HashMap<String, Object>();
		mp.put("label_cn", label_cn);
		List<Map<String,Object>> list =  this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".querytComBtsCircuitByAlias1",mp);
		return list; 
	}
	*//**
	 * 根据电路CUID删除草稿表信息
	 *//*
	public void deleteDgnSegInfo(String traphCuid){
		Map<String,Object> pm = new HashMap<String, Object>();
		pm.put("traphCuid", traphCuid);
		IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteDgnSegDetailRes", pm);
    	IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteDgnSegDetail", pm);
    	IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteDgnSeg", pm);
    	IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".serviceState", pm);
	}
	*//**
	 * 根据电路CUID删除路由相关信息
	 * @param traphCuid
	 *//*
   private boolean updateRouteAndDegInfo(AttempTraphService attempTraph){
	   boolean hasChangeAZflag = false;
	   Map<String,Object> dataMap=attempTraph.getDataMap();
	    String aSiteCuid=IbatisDAOHelper.getStringValue(dataMap, "RELATED_A_SITE_CUID");
	    String zSiteCuid=IbatisDAOHelper.getStringValue(dataMap, "RELATED_Z_SITE_CUID");
	    String traphCuid=attempTraph.getCuid();
	    List<String> serviceCuidList=new ArrayList<String>();
	    serviceCuidList.add(traphCuid);
	    List<IService> serviceList = this.findService(serviceCuidList);
	    Map<String,Object> olddataMap=new HashMap<String, Object>();
	    if(!serviceList.isEmpty()){
	    	IService iSer=serviceList.get(0);
	    	olddataMap=iSer.getDataMap();
	    	String oldAsiteCuid=IbatisDAOHelper.getStringValue(olddataMap, "RELATED_A_SITE_CUID");
		    String oldZsiteCuid=IbatisDAOHelper.getStringValue(olddataMap, "RELATED_Z_SITE_CUID");
		    Map<String,Object> pm=new HashMap<String, Object>();
			if ((aSiteCuid != null && !aSiteCuid.equals(oldAsiteCuid))
					|| (zSiteCuid!=null && !zSiteCuid.equals(oldZsiteCuid))) {
				hasChangeAZflag = true;
				//删除草稿表
		    	this.deleteDgnSegInfo(traphCuid);
		    	//删除路由设计表
		    	ServiceActionContext ac = null;
		    	this.releaseAttempTraphRelation(ac, serviceCuidList);
		    }
	    }
	    return hasChangeAZflag;
   }

	public List<Map<String,Object>> getKeyValue(String param){
		Map pm = new HashMap();
		pm.put("rateValue", param);
		List<Map<String,Object>> rateList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".findKeyValue", pm);
		return rateList;
	}*/
	/*PORT_REPLACE_INFO,PORT_REPLACE_TO_TRAPH插入数据*/
	public void createRelpace(List<Map<String,String>>  portList,Map<String,String> taskMap){
		List<Record> portReplaceInfoList = new ArrayList<Record>();
		List<Record> portRelpaceToTraphList = new ArrayList<Record>();
		List<Map<String, Object>> taskServiceList = new ArrayList<Map<String, Object>>();
		int sortNo = 0;
		for(Map<String,String> map : portList){
			/*向PORT_REPLACE_INFO插入数据*/
			String BeforeCuid = (String)map.get("BEFORE_SIDE_CUID");
			String AfterCuid = (String)map.get("AFTER_SIDE_CUID");
			String BeforeType = (String)map.get("BEFORE_SIDE_BM_CLASS_ID");
			String AfterType = (String)map.get("AFTER_SIDE_BM_CLASS_ID");
			Record r = new Record("PORT_REPLACE_INFO");
			String portReplaceInfoCuid = CUIDHexGenerator.getInstance().generate("PORT_REPLACE_INFO");
			r.addColValue("CUID",portReplaceInfoCuid);
			if(BeforeType.equalsIgnoreCase("CTP")){ 
				HashMap mp = new HashMap();
				mp.put("cuid", BeforeCuid);
				r.addColSqlValue("RELATED_BEFORE_PTP_CUID","SELECT RELATED_PTP_CUID FROM CTP WHERE CUID ='"+ BeforeCuid + "'");
				r.addColValue("RELATED_BEFORE_CTP_CUID",BeforeCuid);
			}else{
				r.addColValue("RELATED_BEFORE_PTP_CUID",BeforeCuid);
			}
			if(AfterType.equalsIgnoreCase("CTP")){
				HashMap mp = new HashMap();
				mp.put("cuid", AfterCuid);
				r.addColSqlValue("RELATED_AFTER_PTP_CUID","SELECT RELATED_PTP_CUID FROM CTP WHERE CUID ='"+ AfterCuid + "'");
				r.addColValue("RELATED_AFTER_CTP_CUID",AfterCuid);
			}else{
				r.addColValue("RELATED_AFTER_PTP_CUID",AfterCuid);
			}
			r.addColValue("OPERRATE_TYPE","0");
			sortNo++;
			portReplaceInfoList.add(r);
			
			/*查询存量电路获取需要替换的电路*/
			HashMap map1 = new HashMap();
			List<Map<String,Object>> replaceToPathList = new ArrayList<Map<String,Object>>();
			if(BeforeType.equalsIgnoreCase("CTP")){
				//CTPCUID 改成ctpCuid
				map1.put("ctpCuid",BeforeCuid);
				replaceToPathList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectReplaceToPathbyCtp",map1);
			}else{
				map1.put("ptpCuid", BeforeCuid);
				replaceToPathList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectReplaceToPathbyPtp",map1);
			}
			/*attemp和traph的数据去重，以attemp的为准*/
			portRelpaceToTraphList.addAll(this.insertPortReplaceToTraph(replaceToPathList, portReplaceInfoCuid));
			/*生成t_task_to_service数据*/
			//电路
			Map<String, Object> taskServiceMap = new HashMap<String, Object>();
			taskServiceMap.put("RELATED_SHEET_CUID", IbatisDAOHelper.getStringTrueValue(taskMap, "RELATED_SHEET_CUID"));
			taskServiceMap.put("RELATED_TASK_CUID",IbatisDAOHelper.getStringTrueValue(taskMap, "RELATED_TASK_CUID"));
			taskServiceMap.put("RELATED_SERVICE_CUID", portReplaceInfoCuid);
			taskServiceMap.put("RELATED_SERVICE_TYPE", "POR_REPLACE");
			taskServiceMap.put("SORT_NO", sortNo);
			taskServiceList.add(taskServiceMap);
		}
		this.IbatisResDAO.insertDynamicTableBatch(portReplaceInfoList);
		this.IbatisResDAO.insertDynamicTableBatch(portRelpaceToTraphList);
		this.createTaskService(taskServiceList);  //任务关联资源数据处理
	}
	/*删除端口替换后台逻辑*/
	public void deletePortRelpace(List<String> cuidList){
		/*删除PORT_REPLACE_INFO*/
		HashMap map = new HashMap();
		map.put("cuidList",cuidList);
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePortReplaceInfo",map);
		/*删除PORT_REPLACE_TO_TRAPH*/
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePortReplaceToTraph",map);
		/*删除t_task_to_service*/
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTaskToService",map);
	}
	public Map<String,Object> searchPageParams(String cuid){
		Map<String,Object> result = new HashMap<String,Object>();
		/*查询PORT_REPLACE_INFO参数*/
		HashMap map = new HashMap();
		map.put("cuid",cuid);
		List<Map<String,Object>> resultList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".searchPortReplaceInfo",map);
		if(StringUtils.isEmpty((String)resultList.get(0).get("RELATED_BEFORE_CTP_CUID"))){
			result.put("BEFORE_SIDE_BM_CLASS_ID","PTP");
			result.put("BEFORE_SIDE_CUID",resultList.get(0).get("RELATED_BEFORE_PTP_CUID"));
		}else{
			result.put("BEFORE_SIDE_BM_CLASS_ID","CTP");
			result.put("BEFORE_SIDE_CUID",resultList.get(0).get("RELATED_BEFORE_CTP_CUID"));
		}
		if(StringUtils.isEmpty((String)resultList.get(0).get("RELATED_AFTER_CTP_CUID"))){
			result.put("AFTER_SIDE_BM_CLASS_ID","PTP");
			result.put("AFTER_SIDE_CUID",resultList.get(0).get("RELATED_AFTER_PTP_CUID"));
		}else{
			result.put("AFTER_SIDE_BM_CLASS_ID", "CTP");
			result.put("AFTER_SIDE_CUID",resultList.get(0).get("RELATED_AFTER_CTP_CUID"));
		}
		return result;
	}
	/*修改端口替换后台逻辑*/
	public void modifyTraphRelpace(List<Map<String,String>>  portList,Map<String,String> taskMap){
		/*调用删除后台逻辑*/
		List<String> cuidList = new ArrayList<String>();
		for(Map<String,String> map : portList){
			String cuid = (String)map.get("RELATED_SERVICE_CUID");
			cuidList.add(cuid);
		}
		this.deletePortRelpace(cuidList);
		/*调用新增端口替换后台逻辑*/
		this.createRelpace(portList, taskMap);
	}
	/*刷新按钮后台逻辑*/
	public void refreshPortRelpace(List<String> cuidList){
		List<Record> portRelpaceToTraphList = new ArrayList<Record>();
		HashMap map = new HashMap();
		map.put("cuidList",cuidList);
		/*删除PORT_REPLACE_TO_TRAPH*/
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePortReplaceToTraph",map);
		for(String cuid : cuidList){
			HashMap map1 = new HashMap();
			List<Map<String,Object>> replaceToPathList = new ArrayList<Map<String,Object>>();
			/*查询PORT_REPLACE_INFO参数*/
			Map<String,Object> refreshPortRelpaceParams =  this.searchPageParams(cuid);
			if(((String) refreshPortRelpaceParams.get("BEFORE_SIDE_BM_CLASS_ID")).equalsIgnoreCase("CTP")){
				//CTPCUID 改成ctpCuid
				map1.put("ctpCuid",((String) refreshPortRelpaceParams.get("BEFORE_SIDE_CUID")));
				replaceToPathList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectReplaceToPathbyCtp",map1);
			}else{
				map1.put("ptpCuid", ((String) refreshPortRelpaceParams.get("BEFORE_SIDE_CUID")));
				replaceToPathList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectReplaceToPathbyPtp",map1);
			}
			/*attemp和traph的数据去重，以attemp的为准*/
			portRelpaceToTraphList.addAll(this.insertPortReplaceToTraph(replaceToPathList, cuid));
		}
		this.IbatisResDAO.insertDynamicTableBatch(portRelpaceToTraphList);
	}
	/*作废逻辑*/
	public void deletePortRelpaceFirst(String taskId){
		List<Map<String,Object>> replaceToPathMapList = new ArrayList<Map<String,Object>>();
		List<String> replaceToPathList = new ArrayList<String>();
		replaceToPathMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPortReplaceInfoList",taskId);
		for(Map<String,Object> map : replaceToPathMapList){
			replaceToPathList.add((String)map.get("RELATED_SERVICE_CUID"));
		}
		HashMap map = new HashMap();
		map.put("cuidList",replaceToPathList);
		/*删除PORT_REPLACE_INFO*/
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePortReplaceInfo",map);
		/*删除PORT_REPLACE_TO_TRAPH*/
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePortReplaceToTraph",map);
	}
	/*端口替换-根据工单Id获取任务ID*/
	public String searchTaskId(String sheetId){
		return (String) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".searchTaskIdbySheetId", sheetId);
	}
	/*去重,并往PORT_REPLACE_TO_TRAPH插入数据封装record*/
	public List<Record> insertPortReplaceToTraph(List<Map<String,Object>> replaceToPathList,String cuid){
		List<Record> portRelpaceToTraphList = new ArrayList<Record>();
		Map<String,List<Map<String,Object>>> replaceToPathMapList = new HashMap<String,List<Map<String,Object>>>();
		for(Map<String,Object> map : replaceToPathList){
			List<Map<String,Object>> replaceToPathValueList = new ArrayList<Map<String,Object>>();
			if(((List<Map<String,Object>>)replaceToPathMapList.get("TRAPH_NAME"))==null){
				replaceToPathValueList.add(map);
				replaceToPathMapList.put((String)map.get("TRAPH_NAME"),replaceToPathValueList);
			}else{
				replaceToPathValueList = replaceToPathMapList.get("TRAPH_NAME");
				replaceToPathValueList.add(map);
			}
		}
		List<Map<String,Object>> replaceToPathTrueList = new ArrayList<Map<String,Object>>();
		for (String name : replaceToPathMapList.keySet()){
			if(replaceToPathMapList.get(name).size()==1){
				replaceToPathTrueList.addAll(replaceToPathMapList.get(name));
			}else{
				for(Map<String,Object> replaceToPathMap: replaceToPathMapList.get(name)){
					if((((String)replaceToPathMap.get("RELATED_SERVICE_CUID"))).indexOf("ATTEMP")>-1){
						replaceToPathTrueList.add(replaceToPathMap);
					}
				}
			}
		}
		for(Map<String,Object> replaceToPathTrue : replaceToPathTrueList){
			/*查询替换前某段路由两端的端口时隙信息*/
			Record r1 = new Record("PORT_REPLACE_TO_TRAPH");
			String portReplaceToPathCuid = CUIDHexGenerator.getInstance().generate("PORT_REPLACE_TO_TRAPH");
			r1.addColValue("CUID",portReplaceToPathCuid);
			r1.addColValue("RELATED_PORT_REPLACE_CUID",cuid);
			r1.addColValue("RELATED_SERVICE_CUID",(String)replaceToPathTrue.get("RELATED_SERVICE_CUID"));
			r1.addColValue("A_PTP_NAME",(String)replaceToPathTrue.get("A_PTP_NAME"));
			r1.addColValue("Z_PTP_NAME",(String)replaceToPathTrue.get("Z_PTP_NAME"));
			r1.addColValue("A_CTP_NAME",(String)replaceToPathTrue.get("A_CTP_NAME"));
			r1.addColValue("Z_CTP_NAME",(String)replaceToPathTrue.get("Z_CTP_NAME"));
			r1.addColValue("TRAPH_NAME",(String)replaceToPathTrue.get("TRAPH_NAME"));
			portRelpaceToTraphList.add(r1);
		}
		return portRelpaceToTraphList;
	}
}
