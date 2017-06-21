package com.boco.flow.traph.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.omg.CORBA.UserException;

import com.boco.attemp.PathDesignConstants;
import com.boco.attemp.bo.AbstractPathDesignBO;
import com.boco.attemp.bo.IServiceBO;
import com.boco.attemp.bo.ITraphPathBO;
import com.boco.attemp.pojo.PathData;
import com.boco.attemp.pojo.PathDesignMode;
import com.boco.attemp.pojo.PathDesignParam;
import com.boco.attemp.pojo.PathPoint;
import com.boco.attemp.pojo.ServiceDesignPath;
import com.boco.attemp.pojo.ServicePath;
import com.boco.common.util.debug.LogHome;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.core.utils.lang.CollectionHelper;
import com.boco.flow.traph.pojo.CtpRelation;
import com.boco.flow.traph.pojo.PathRouteSeg;
import com.boco.flow.traph.pojo.TRUsedWave;
import com.boco.flow.traph.pojo.TraphPathData;
import com.boco.maintain.util.MultiPathsMerge;
import com.boco.transnms.common.dto.PtnVirtualLine;
import com.boco.transnms.common.dto.TunnelToVirtualLine;
/**
 *  吉林
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class TraphDesignBO extends AbstractPathDesignBO  {
	
	private Logger logger = Logger.getLogger("TraphDesignBO");
	
	private static final String sqlMap = "TraphDesign";
	
	private TraphMaintainBO TraphMaintainBO;
	
	public void setTraphMaintainBO(TraphMaintainBO traphMaintainBO) {
		TraphMaintainBO = traphMaintainBO;
	}
	public IServiceBO getServiceBO() {
		return TraphMaintainBO;
	}
	private ITraphPathBO TraphPathBO;
	public void setTraphPathBO(ITraphPathBO traphPathBO) {
		TraphPathBO = traphPathBO;
	}
    private  static final HashMap<String,Integer> rateInfoMap = new HashMap<String, Integer>();
    static{
		rateInfoMap.put("40G-10G",17);
		rateInfoMap.put("40G-10GE",17);
		rateInfoMap.put("40G-155M",35);
		rateInfoMap.put("40G-100M",35);
		rateInfoMap.put("40G-622M",35);
		rateInfoMap.put("40G-FE",35);
		rateInfoMap.put("40G-GE",35);
		rateInfoMap.put("40G-1.25G",35);
		rateInfoMap.put("40G-2.5G",16);
		rateInfoMap.put("40G-FC",16);
		rateInfoMap.put("10G-10G",17);
		rateInfoMap.put("10G-10GE",17);
		rateInfoMap.put("10G-155M",35);
		rateInfoMap.put("10G-100M",35);
		rateInfoMap.put("10G-622M",35);
		rateInfoMap.put("10G-FE",35);
		rateInfoMap.put("10G-GE",35);
		rateInfoMap.put("10G-1.25G",35);
		rateInfoMap.put("10G-2.5G",16);
		rateInfoMap.put("10G-FC",16);
		rateInfoMap.put("2.5G-155M",35);
		rateInfoMap.put("2.5G-100M",35);
		rateInfoMap.put("2.5G-622M",35);
		rateInfoMap.put("2.5G-FE",35);
		rateInfoMap.put("2.5G-GE",35);
		rateInfoMap.put("2.5G-1.25G",35);
		rateInfoMap.put("2.5G-FC",35);
		rateInfoMap.put("2.5G-2.5G",16);
	}
	
    /**
     * 根据A/Z端口获取PTN电路
     * @param map	
     */
    public List<Map<String, Object>> getPtnTraphByPtp(Map<String, Object> map){
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	if (map.get("pathType").toString().equals("2")){
    		//lte电路用AZ端口及VLANID验证
    		list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getLteTraphByPtp",map);
    	}else{
    		list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPtnTraphByPtp",map);
    	}
    	
    	return list;
    }
    
	/**
	 * 检查电路设计版本
	 */
	protected boolean checkServiceVersion(PathDesignParam param) {
		boolean isPass = true;
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("serviceList", param.getServiceList());
		try{
			Integer updateNum = this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".checkServiceVersion", pm);
			if(updateNum < param.getServiceList().size()){
				isPass = false;
			}
		}catch(Exception e){
			this.logger.info("修改电路设计版本时出错！");
		}
		return isPass;
	}
	/**
	 * 检查电路设计完整性
	 */
	protected boolean checkServiceIntegrity(PathDesignParam pathDesignParam) {
		boolean isPass = true;
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("serviceList", pathDesignParam.getServiceList());
		pm.put("relatedTaskCuid", pathDesignParam.getTaskId());
		pm.put("way", pathDesignParam.getParamString("way"));
		Map rp1 = pathDesignParam.getParamMap("routePoint1");
		Map rp2 = pathDesignParam.getParamMap("routePoint2");
		pm.put("routePoint1", IbatisDAOHelper.getStringValue(rp1, "CUID"));
		pm.put("routePoint2", IbatisDAOHelper.getStringValue(rp2, "CUID"));
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".checkServiceIntegrity", pm);
		for (Map<String, Object> m : list) {
			Integer isFinish = IbatisDAOHelper.getIntValue(m, "IS_FINISH");
			Integer routeCount = IbatisDAOHelper.getIntValue(m, "ROUTECOUNT");
			if (routeCount < 1 || isFinish != 1) {
				isPass = false;
				break;
			}
		}
		return isPass;
	}
	/**
	 * 保存路由设计信息
	 */
	public void saveDgnSegDetail(List<PathDesignMode> modeList) {
		//循环保存
		int ctpCount = 1;
		List<Record> segDetailList = new ArrayList<Record>();
		List<Record> resList = new ArrayList<Record>();
		for(PathDesignMode pdm:modeList){
			String code = pdm.getCode();
			if(PathDesignConstants.MODE_CODE_PORT.equals(code)){
				String aPointCuid = pdm.getDataString("A_POINT_CUID");
				String zPointCuid = pdm.getDataString("Z_POINT_CUID");
				String aPointName = pdm.getDataString("A_POINT_NAME");
				String zPointName = pdm.getDataString("Z_POINT_NAME");
				String aBmClassId = StringUtils.isEmpty(pdm.getDataString("A_POINT_NAME"))?"":pdm.getDataString("A_BM_CLASS_ID");
				String zBmClassId = StringUtils.isEmpty(pdm.getDataString("Z_POINT_NAME"))?"":pdm.getDataString("Z_BM_CLASS_ID");
				if(StringUtils.isBlank(aPointCuid) || StringUtils.isBlank(zPointCuid)){
					throw new RuntimeException("端口或者时隙数据未设置！");
				}

				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("SELF_DEFINE_DESCRIPTION", pdm.getDataString("SELF_DEFINE_DESCRIPTION"));
				
				if("CTP".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("CTP_NA".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP_NA");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP_NA C,TRANS_ELEMENT_NA E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP_NA WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT_NA WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP_NA WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP_NA WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP_NA WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP_NA WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						throw new RuntimeException("您所选择的端口:"+aPointName+"，没有时隙！");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP_NA".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP_NA WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						throw new RuntimeException("您所选择的端口:"+aPointName+"，没有时隙！");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP_NA");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP_NA WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP_NA WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP_NA P,TRANS_ELEMENT_NA E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP_NA WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT_NA WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP_NA WHERE CUID = '"+ aPointCuid + "')");
				}
				
				if("CTP".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("CTP_NA".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP_NA");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP_NA C,TRANS_ELEMENT_NA E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP_NA WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT_NA WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP_NA WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP_NA WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP_NA WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP_NA WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						throw new RuntimeException("您所选择的端口:"+zPointName+"，没有时隙！");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP_NA".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP_NA WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						throw new RuntimeException("您所选择的端口:"+zPointName+"，没有时隙！");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP_NA");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP_NA WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP_NA WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP_NA P,TRANS_ELEMENT_NA E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP_NA WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT_NA WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP_NA WHERE CUID = '"+ zPointCuid + "')");
					record.addColValue("IS_PREATTEMP_Z",1);
				}
				record.addColSqlValue(
						"RATE",
						"SELECT MIN(RATE) FROM(SELECT PORT_RATE AS RATE FROM PTP WHERE CUID IN( '"+
								aPointCuid+ "','"+ zPointCuid+ "') " +
								"UNION ALL " +
								"SELECT PORT_RATE AS RATE FROM PTP_NA WHERE CUID IN( '"+
								aPointCuid+ "','"+ zPointCuid+ "') " +
								"UNION ALL " +
								"SELECT CTP_RATE AS RATE FROM CTP WHERE CUID IN ('"+ 
								aPointCuid + "','"+ zPointCuid + "') " +
								"UNION ALL " +
								"SELECT CTP_RATE AS RATE FROM CTP_NA WHERE CUID IN ('"+ 
								aPointCuid + "','"+ zPointCuid + "') " +
								"UNION ALL " +
								" SELECT P.LOGIC_RATE AS RATE  FROM TRANS_LOGIC_CTP P  " +
									"WHERE P.CTP_CUID = '"+aPointCuid+"'   AND P.CTP_NAME = '"+aPointName+"' " +
								"UNION ALL  " +
								" SELECT P.LOGIC_RATE AS RATE FROM TRANS_LOGIC_CTP P   " +
									"WHERE P.CTP_CUID = '"+zPointCuid+"'   AND P.CTP_NAME = '"+zPointName+"')");
				segDetailList.add(record);
			}else if(PathDesignConstants.MODE_CODE_PTN.equals(code)){
				String aPointCuid = pdm.getDataString("A_POINT_CUID");
				String zPointCuid = pdm.getDataString("Z_POINT_CUID");
				String aPointName = pdm.getDataString("A_POINT_NAME");
				String zPointName = pdm.getDataString("Z_POINT_NAME");
				String aBmClassId = pdm.getDataString("A_BM_CLASS_ID");
				String zBmClassId = StringUtils.isEmpty(pdm.getDataString("Z_POINT_NAME"))?"":pdm.getDataString("Z_BM_CLASS_ID");
				String aPointCuid2 = pdm.getDataString("A_POINT_CUID2");
				String zPointCuid2 = pdm.getDataString("Z_POINT_CUID2");
				String aPointName2 = pdm.getDataString("A_POINT_NAME2");
				String zPointName2 = pdm.getDataString("Z_POINT_NAME2");
				String aBmClassId2 = pdm.getDataString("A_BM_CLASS_ID2");
				String zBmClassId2 = pdm.getDataString("Z_BM_CLASS_ID2");
				String extIds = (String)pdm.getService().getAttr().get("EXT_IDS");
				if(StringUtils.isBlank(aPointCuid)){
					throw new RuntimeException("A端口或者时隙数据未设置！");
				}
				if(StringUtils.isBlank(extIds) || (extIds.indexOf(",24,")<0&&extIds.indexOf(",25,")<0&&extIds.indexOf(",26,")<0)){
					if(StringUtils.isBlank(zPointCuid)){
						throw new RuntimeException("Z端口或者时隙数据未设置！");
					}
				}

				if(SysProperty.getInstance().getValue("districtName").equals("河南")){
					//验证Vlan是否被使用
					String vlanId = pdm.getDataString("VLANID");
					//String traphCuid = pdm.getDataString("PTN_SERVICE_CUID");
					String traphCuid = pdm.getService().getCuid();
					logger.info("--------------aPointCuid="+aPointCuid+",zPointCuid="+zPointCuid+",traphCuid="+traphCuid+",vlanId="+vlanId);
					if(vlanId!=null && !vlanId.equals("")){
						TraphPtnVlanNoBO traphPtnVlanNoBO = (TraphPtnVlanNoBO)SpringContextUtil.getBean("TraphPtnVlanNoBO");
						List<String> skipCuids = new ArrayList<String>();
						if(traphCuid!=null && !traphCuid.equals("")){
							skipCuids.add(traphCuid);
						}
						List<Map<String, Object>> vlanList = traphPtnVlanNoBO.validatePtnVlanIdUniqu(aPointCuid, zPointCuid, vlanId, skipCuids);
						if(vlanList!=null && !vlanList.isEmpty()){
							throw new RuntimeException("该VLANID"+vlanId+"编号已经被使用过！");
						}
					}
				}
				
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("CIR_BAND", pdm.getDataString("CIR_BAND"));
				record.addColValue("QOS_BAND", pdm.getDataString("QOS_BAND"));
				record.addColValue("PIR_BAND", pdm.getDataString("PIR_BAND"));
				record.addColValue("VLANID", pdm.getDataString("VLANID"));
				record.addColValue("BSVLANID", pdm.getDataString("BSVLANID"));
				record.addColValue("SELF_DEFINE_DESCRIPTION", pdm.getDataString("SELF_DEFINE_DESCRIPTION"));
				record.addColValue("TURNNEL_CUID", pdm.getDataString("TURNNEL_CUID"));
				record.addColValue("TURNNEL_NAME", pdm.getDataString("TURNNEL_NAME"));
				record.addColValue("RELATED_VIRTUAL_LINE_CUID", pdm.getDataString("RELATED_VIRTUAL_LINE_CUID"));
				record.addColValue("RELATED_VIRTUAL_LINE_NAME", pdm.getDataString("RELATED_VIRTUAL_LINE_NAME"));
				record.addColValue("PTN_SERVICE_CUID", pdm.getDataString("PTN_SERVICE_CUID"));
				record.addColValue("PTN_SERVICE_NAME", pdm.getDataString("PTN_SERVICE_NAME"));
				record.addColValue("A_CARD_NAME", pdm.getDataString("BACKUP_TURNNEL_BAND"));//备用隧道名称复用Z端机盘名称-VOSTRO
				record.addColValue("A_CARD_CUID", pdm.getDataString("BACKUP_TURNNEL_CUID"));//备用隧道CUID复用Z端机盘CUID-VOSTRO
				logger.info("开始处理A端数据!");
				if("CTP".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("A_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "')");
				}
				logger.info("A端数据处理完毕!");
				logger.info("开始处理Z端数据!");
				if("CTP".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("Z_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "')");
				}
				logger.info("Z端数据处理完毕!");
				
				logger.info("开始处理 A端 2 数据!");
				if("CTP".equalsIgnoreCase(aBmClassId2)){
					record.addColValue("A_BM_CLASS_ID2", "CTP");
					record.addColValue("A_CTP_CUID2", aPointCuid2);
					record.addColValue("A_CTP_NAME2", aPointName2);
					record.addColSqlValue("A_NE_CUID2","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "')");
					record.addColSqlValue("A_PTP_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_PTP_NAME2", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId2)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid2 + "'");
					if(ctpCount==0){
						record.addColValue("A_BM_CLASS_ID2", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID2", "CTP");
						record.addColSqlValue("A_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid2 + "'");
						record.addColSqlValue("A_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid2 + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName2+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID2", aPointCuid2);
					record.addColValue("A_PTP_NAME2", aPointName2);
					record.addColSqlValue("A_NE_CUID2","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid2 + "')");
				}
				logger.info("A端 2 数据处理完毕!");
				
				logger.info("开始处理 Z端 2 数据!");
				if("CTP".equalsIgnoreCase(zBmClassId2)){
					record.addColValue("Z_BM_CLASS_ID2", "CTP");
					record.addColValue("Z_CTP_CUID2", zPointCuid2);
					record.addColValue("Z_CTP_NAME2", zPointName2);
					record.addColSqlValue("Z_NE_CUID2","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "')");
					record.addColSqlValue("Z_PTP_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_PTP_NAME2", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId2)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid2 + "'");
					if(ctpCount==0){
						record.addColValue("Z_BM_CLASS_ID2", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID2", "CTP");
						record.addColSqlValue("Z_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid2 + "'");
						record.addColSqlValue("Z_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid2 + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName2+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID2", zPointCuid2);
					record.addColValue("Z_PTP_NAME2", zPointName2);
					record.addColSqlValue("Z_NE_CUID2", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid2 + "')");
				}
				logger.info("Z端 2 数据处理完毕!");
				segDetailList.add(record);
				//TODO 增加 ptnpath 和 staticroute 的关系，ptnpath 和 ip的关系（草稿表）绑定
				
			}else if (PathDesignConstants.MODE_CODE_MSAP.equals(code)){
				String aPointCuid = pdm.getDataString("A_POINT_CUID");
				String zPointCuid = pdm.getDataString("Z_POINT_CUID");
				String aPointName = pdm.getDataString("A_POINT_NAME");
				String zPointName = pdm.getDataString("Z_POINT_NAME");
				String aBmClassId = StringUtils.isEmpty(pdm.getDataString("A_POINT_NAME"))?"":pdm.getDataString("A_BM_CLASS_ID");
				String zBmClassId = StringUtils.isEmpty(pdm.getDataString("Z_POINT_NAME"))?"":pdm.getDataString("Z_BM_CLASS_ID");
				if(StringUtils.isBlank(aPointCuid) || StringUtils.isBlank(zPointCuid)){
					throw new RuntimeException("端口或者时隙数据未设置！");
				}
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("SELF_DEFINE_DESCRIPTION", pdm.getDataString("SELF_DEFINE_DESCRIPTION"));
				logger.info("开始处理A端数据!");
				if("CTP".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("A_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "')");
				}
				logger.info("A端数据处理完毕!");
				logger.info("开始处理Z端数据!");
				if("CTP".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("Z_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "')");
				}
				logger.info("Z端数据处理完毕!");
				segDetailList.add(record);
				
			}else if(PathDesignConstants.MODE_CODE_PTN_LTE3.equals(code)){
				String aPointCuid = pdm.getDataString("A_POINT_CUID");
				String aPointName = pdm.getDataString("A_POINT_NAME");
				String aBmClassId = pdm.getDataString("A_BM_CLASS_ID");
				
				String zPointCuid = pdm.getDataString("Z_POINT_CUID");
				String zPointName = pdm.getDataString("Z_POINT_NAME");
				String zBmClassId = StringUtils.isEmpty(pdm.getDataString("Z_BM_CLASS_ID"))?"":pdm.getDataString("Z_BM_CLASS_ID");
				
				String aPointCuid2 = pdm.getDataString("A_POINT_CUID2");
				String aPointName2 = pdm.getDataString("A_POINT_NAME2");
				String aBmClassId2 = pdm.getDataString("A_BM_CLASS_ID2");
				
				String zPointCuid2 = pdm.getDataString("Z_POINT_CUID2");
				String zPointName2 = pdm.getDataString("Z_POINT_NAME2");
				String zBmClassId2 = pdm.getDataString("Z_BM_CLASS_ID2");
				//主用虚端口
				String aPointCuid3 = pdm.getDataString("A_SIDE_CUID3");
				String aPointName3 = pdm.getDataString("A_SIDE_NAME3");
				String aBmClassId3 = pdm.getDataString("A_BM_CLASS_ID3");
				//备用虚端口
				String aPointCuid4 = pdm.getDataString("A_SIDE_CUID4");
				String aPointName4 = pdm.getDataString("A_SIDE_NAME4");
				String aBmClassId4 = pdm.getDataString("A_BM_CLASS_ID4");
				
				String extIds = (String)pdm.getService().getAttr().get("EXT_IDS");
				if(StringUtils.isBlank(aPointCuid3)){
					throw new RuntimeException("虚端口或者时隙数据未设置！");
				}
				/*if (StringUtils.isBlank(aPointCuid)) {
		            throw new RuntimeException("A端口或者时隙数据未设置！");
		        }*/
				if(StringUtils.isBlank(extIds) || (extIds.indexOf(",24,")<0&&extIds.indexOf(",25,")<0&&extIds.indexOf(",26,")<0)){
					if(StringUtils.isBlank(zPointCuid)){
						throw new RuntimeException("Z端口或者时隙数据未设置！");
					}
				}
				
				String businessIpCuid = ""; 
				String netconfigIpCuid = ""; 
				String businessIpName = pdm.getDataString("BUSINESS_IP_NAME"); 
				String netconfigIpName = pdm.getDataString("NETCONFIG_IP_NAME"); 
				if(StringUtils.isBlank(businessIpName)){
					throw new RuntimeException("业务IP不能为空！");
				}
				Map pm = new HashMap();
				pm.put("ADDR", businessIpName);
				String useTypevalue = "";
                if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
                	if(extIds.indexOf(",101,")>=0){//非共址
    					useTypevalue = "1";
    				}else{
    					useTypevalue = "2";
    				}
                	pm.put("useTypevalue", useTypevalue);
				}
				List<Map<String, Object>> businessIpCuids =this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getBusinessIpCuid",pm);
				String msg = "";
				if(StringUtils.isEmpty(useTypevalue)){
					msg = "数据库";
				}else if(useTypevalue.equals("1")){
					msg = "共址ip";
				}else if(useTypevalue.equals("2")){
					msg = "非共址ip";
				}
				if (businessIpCuids.isEmpty()) {
					throw new RuntimeException(msg +"中无此ip("+businessIpName+")地址");
				}else{
					String numType = IbatisDAOHelper.getStringValue(businessIpCuids.get(0), "NUM_TYPE");
					if (StringUtils.isNotEmpty(numType) && !numType.equals('4')&&!numType.equals('5')){
						String cuid = IbatisDAOHelper.getStringValue(businessIpCuids.get(0), "CUID");
						businessIpCuid = cuid;
					}else{
						throw new RuntimeException("该ip("+businessIpName+")地址不可用");
					}
				}
				
				if (StringUtils.isNotBlank(netconfigIpName)){
					Map pm1 = new HashMap();
					pm1.put("ADDR", netconfigIpName);
					if(SysProperty.getInstance().getValue("districtName").trim().equals("河南")){
	                	pm1.put("useTypevalue", useTypevalue);
					}
					List<Map<String, Object>> netconfigIpCuids = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getBusinessIpCuid",pm1);
					if (netconfigIpCuids.isEmpty()) {
						throw new RuntimeException(msg +"中无此ip("+netconfigIpName+")地址");
					}else{
						String numType = IbatisDAOHelper.getStringValue(netconfigIpCuids.get(0), "NUM_TYPE");
						if (!numType.equals('4')&&!numType.equals('5')){
							String cuid = IbatisDAOHelper.getStringValue(netconfigIpCuids.get(0), "CUID");
							netconfigIpCuid = cuid;
						}else{
							throw new RuntimeException("该ip("+netconfigIpName+")地址不可用");
						}
					}
				}
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				String detailCuid = CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", detailCuid);
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				
				record.addColValue("CIR_BAND", pdm.getDataString("CIR_BAND"));
				record.addColValue("LTE_CODE", pdm.getDataString("LTE_CODE"));
				record.addColValue("PIR_BAND", pdm.getDataString("PIR_BAND"));
				record.addColValue("VLANID", pdm.getDataString("VLANID"));
				record.addColValue("BSVLANID", pdm.getDataString("BSVLANID"));
				record.addColValue("SERVICE_PRIORITY", pdm.getDataString("SERVICE_PRIORITY"));
				record.addColValue("SELF_DEFINE_DESCRIPTION", pdm.getDataString("SELF_DEFINE_DESCRIPTION"));
				
				if("CTP".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("A_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "')");
				}
				logger.info("A端数据处理完毕!");
				logger.info("开始处理Z端数据!");
				if("CTP".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						record.addColValue("Z_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "')");
				}
				logger.info("Z端数据处理完毕!");
				
				logger.info("开始处理 A端 2 数据!");
				if("CTP".equalsIgnoreCase(aBmClassId2)){
					record.addColValue("A_BM_CLASS_ID2", "CTP");
					record.addColValue("A_CTP_CUID2", aPointCuid2);
					record.addColValue("A_CTP_NAME2", aPointName2);
					record.addColSqlValue("A_NE_CUID2","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "')");
					record.addColSqlValue("A_PTP_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_PTP_NAME2", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid2 + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId2)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid2 + "'");
					if(ctpCount==0){
						record.addColValue("A_BM_CLASS_ID2", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID2", "CTP");
						record.addColSqlValue("A_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid2 + "'");
						record.addColSqlValue("A_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid2 + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName2+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID2", aPointCuid2);
					record.addColValue("A_PTP_NAME2", aPointName2);
					record.addColSqlValue("A_NE_CUID2","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid2 + "'");
					record.addColSqlValue("A_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid2 + "')");
				}
				logger.info("A端 2 数据处理完毕!");
				
				logger.info("开始处理 Z端 2 数据!");
				if("CTP".equalsIgnoreCase(zBmClassId2)){
					record.addColValue("Z_BM_CLASS_ID2", "CTP");
					record.addColValue("Z_CTP_CUID2", zPointCuid2);
					record.addColValue("Z_CTP_NAME2", zPointName2);
					record.addColSqlValue("Z_NE_CUID2","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "')");
					record.addColSqlValue("Z_PTP_CUID2", "SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_PTP_NAME2", "SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid2 + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId2)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid2 + "'");
					if(ctpCount==0){
						record.addColValue("Z_BM_CLASS_ID2", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID2", "CTP");
						record.addColSqlValue("Z_CTP_CUID2", "SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid2 + "'");
						record.addColSqlValue("Z_CTP_NAME2", "SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid2 + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName2+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID2", zPointCuid2);
					record.addColValue("Z_PTP_NAME2", zPointName2);
					record.addColSqlValue("Z_NE_CUID2", "SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid2 + "'");
					record.addColSqlValue("Z_NE_NAME2", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid2 + "')");
				}
				//虚端口
				record.addColValue("Z_CARD_CUID", aPointCuid3);
				record.addColValue("Z_CARD_NAME", aPointName3);
				//备用虚端口
				record.addColValue("A_CARD_CUID", aPointCuid4);
				record.addColValue("A_CARD_NAME", aPointName4);
				segDetailList.add(record);
				
				Record rdMIp = new Record("T_ATTEMP_DGN_SEG_DETAIL_TO_RES");
				rdMIp.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL_TO_RES"));
				rdMIp.addColValue("TYPE", "BUSINESS");
				rdMIp.addColValue("RELATED_SEG_DETAIL_CUID", detailCuid);
				rdMIp.addColValue("RELATED_RES_CUID", businessIpCuid);
				rdMIp.addColValue("RELATED_RES_NAME", businessIpName);
				rdMIp.addColValue("RELATED_RES_TYPE", "T_LOGIC_NUMBER_IP");
				resList.add(rdMIp);
				if(StringUtils.isNotBlank(netconfigIpName)){
					Record rdCIp = new Record("T_ATTEMP_DGN_SEG_DETAIL_TO_RES");
					rdCIp.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL_TO_RES"));
					rdCIp.addColValue("TYPE", "NETCONFIG");
					rdCIp.addColValue("RELATED_SEG_DETAIL_CUID", detailCuid);
					rdCIp.addColValue("RELATED_RES_CUID", netconfigIpCuid);
					rdCIp.addColValue("RELATED_RES_NAME", netconfigIpName);
					rdCIp.addColValue("RELATED_RES_TYPE", "T_LOGIC_NUMBER_IP");
					resList.add(rdCIp);
				}
				
			}else if(PathDesignConstants.MODE_CODE_CHAN.equals(code)){
				String transPathCuid = pdm.getDataString("RELATED_TRANS_PATH_CUID");
				String labelCn = pdm.getDataString("LABEL_CN");
				if(StringUtils.isBlank(transPathCuid)){
					throw new RuntimeException("通道数据未设置！");
				}
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("RELATED_TRANS_PATH_CUID", transPathCuid);
				record.addColValue("LABEL_CN", labelCn);
				record.addColValue("ROUTE_DESCRIPTION",pdm.getDataString("ROUTE_DESCRIPTION"));
				
				Map<String, Object> pm = new HashMap<String, Object>();
				pm.put("transPathCuid", transPathCuid);
				pm.put("dgnSegCuid", pdm.getRelatedDgnSegCuid());
				List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".checkIsOpposite", pm);
				if(list!=null &&list.size()>0){
					record.addColSqlValue("A_NE_CUID", "SELECT T.ORIG_EQU_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_NE_NAME", "SELECT E.LABEL_CN FROM TRANS_PATH T,TRANS_ELEMENT E WHERE E.CUID = T.ORIG_EQU_CUID AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_ROOM_CUID", "SELECT T.ORIG_ROOM_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_PTP_CUID", "SELECT T.RELATED_A_END_PTP FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_PTP_NAME", "SELECT P.LABEL_CN FROM TRANS_PATH T,PTP P WHERE P.CUID = T.RELATED_A_END_PTP AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_CTP_CUID",	"SELECT T.ORIG_POINT_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_CTP_NAME",	"SELECT P.LABEL_CN FROM TRANS_PATH T,CTP P WHERE P.CUID = T.ORIG_POINT_CUID AND T.CUID = '"+transPathCuid+"'");
					
					record.addColSqlValue("Z_NE_CUID", "SELECT T.DEST_EQU_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_NE_NAME", "SELECT E.LABEL_CN FROM TRANS_PATH T,TRANS_ELEMENT E WHERE E.CUID = T.DEST_EQU_CUID AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_ROOM_CUID", "SELECT T.DEST_ROOM_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_PTP_CUID", "SELECT T.RELATED_Z_END_PTP FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_PTP_NAME", "SELECT P.LABEL_CN FROM TRANS_PATH T,PTP P WHERE P.CUID = T.RELATED_Z_END_PTP AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_CTP_CUID",	"SELECT T.DEST_POINT_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_CTP_NAME",	"SELECT P.LABEL_CN FROM TRANS_PATH T,CTP P WHERE P.CUID = T.DEST_POINT_CUID AND T.CUID = '"+transPathCuid+"'");
				}else{
					record.addColSqlValue("Z_NE_CUID", "SELECT T.ORIG_EQU_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_NE_NAME", "SELECT E.LABEL_CN FROM TRANS_PATH T,TRANS_ELEMENT E WHERE E.CUID = T.ORIG_EQU_CUID AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_ROOM_CUID", "SELECT T.ORIG_ROOM_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_PTP_CUID", "SELECT T.RELATED_A_END_PTP FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_PTP_NAME", "SELECT P.LABEL_CN FROM TRANS_PATH T,PTP P WHERE P.CUID = T.RELATED_A_END_PTP AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_CTP_CUID",	"SELECT T.ORIG_POINT_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("Z_CTP_NAME",	"SELECT P.LABEL_CN FROM TRANS_PATH T,CTP P WHERE P.CUID = T.ORIG_POINT_CUID AND T.CUID = '"+transPathCuid+"'");
					
					record.addColSqlValue("A_NE_CUID", "SELECT T.DEST_EQU_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_NE_NAME", "SELECT E.LABEL_CN FROM TRANS_PATH T,TRANS_ELEMENT E WHERE E.CUID = T.DEST_EQU_CUID AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_ROOM_CUID", "SELECT T.DEST_ROOM_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_PTP_CUID", "SELECT T.RELATED_Z_END_PTP FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_PTP_NAME", "SELECT P.LABEL_CN FROM TRANS_PATH T,PTP P WHERE P.CUID = T.RELATED_Z_END_PTP AND T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_CTP_CUID",	"SELECT T.DEST_POINT_CUID FROM TRANS_PATH T WHERE T.CUID = '"+transPathCuid+"'");
					record.addColSqlValue("A_CTP_NAME",	"SELECT P.LABEL_CN FROM TRANS_PATH T,CTP P WHERE P.CUID = T.DEST_POINT_CUID AND T.CUID = '"+transPathCuid+"'");
				}
				
				segDetailList.add(record);
			}else if(PathDesignConstants.MODE_CODE_TEXT.equals(code)){
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("LABEL_CN", pdm.getDataString("LABEL_CN"));
				record.addColValue("ROUTE_DESCRIPTION", pdm.getDataString("LABEL_CN"));
				segDetailList.add(record);
			}else if(PathDesignConstants.MODE_CODE_OTN.equals(code)){
				String aPointCuid = pdm.getDataString("A_POINT_CUID");
				String zPointCuid = pdm.getDataString("Z_POINT_CUID");
				String aPointName = pdm.getDataString("A_POINT_NAME");
				String zPointName = pdm.getDataString("Z_POINT_NAME");
				String aBmClassId = pdm.getDataString("A_BM_CLASS_ID");
				String zBmClassId = StringUtils.isEmpty(pdm.getDataString("Z_BM_CLASS_ID"))?"":pdm.getDataString("Z_BM_CLASS_ID");
				String extIds = (String)pdm.getService().getAttr().get("EXT_IDS");// pdm.getDataString("EXT_IDS");
				if(StringUtils.isBlank(aPointCuid)){
					throw new RuntimeException("A端口或者时隙数据未设置！");
				}
				if(StringUtils.isBlank(extIds) || (extIds.indexOf(",24,")<0&&extIds.indexOf(",25,")<0&&extIds.indexOf(",26,")<0)){
					if(StringUtils.isBlank(zPointCuid)){
						throw new RuntimeException("Z端口或者时隙数据未设置！");
					}
				}
				//分支1
				String aPointCuid2 = pdm.getDataString("A_SIDE_CUID2");
				String zPointCuid2 = pdm.getDataString("Z_SIDE_CUID2");
				String aPointName2 = pdm.getDataString("A_SIDE_NAME2");
				String zPointName2 = pdm.getDataString("Z_SIDE_NAME2");
				String aBmClassId2 = StringUtils.isEmpty(pdm.getDataString("A_SIDE_BM_CLASS_ID2"))?"":pdm.getDataString("A_SIDE_BM_CLASS_ID2");
				String zBmClassId2 = StringUtils.isEmpty(pdm.getDataString("Z_SIDE_BM_CLASS_ID2"))?"":pdm.getDataString("Z_SIDE_BM_CLASS_ID2");
				String ctpName1 = pdm.getDataString("CTP_NAME1");
				String transSysCuid1 = pdm.getDataString("TRANS_SYS_CUID1");
				//分支2
				String aPointCuid3 = pdm.getDataString("A_SIDE_CUID3");
				String zPointCuid3 = pdm.getDataString("Z_SIDE_CUID3");
				String aPointName3 = pdm.getDataString("A_SIDE_NAME3");
				String zPointName3 = pdm.getDataString("Z_SIDE_NAME3");
				String aBmClassId3 = StringUtils.isEmpty(pdm.getDataString("A_SIDE_BM_CLASS_ID3"))?"":pdm.getDataString("A_SIDE_BM_CLASS_ID3");
				String zBmClassId3 = StringUtils.isEmpty(pdm.getDataString("Z_SIDE_BM_CLASS_ID3"))?"":pdm.getDataString("Z_SIDE_BM_CLASS_ID3");
				String ctpName2 = pdm.getDataString("CTP_NAME2");
				String transSysCuid2 = pdm.getDataString("TRANS_SYS_CUID2");
				
				Record record = new Record("T_ATTEMP_DGN_SEG_DETAIL");
				record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_ATTEMP_DGN_SEG_DETAIL"));
				record.addColValue("RELATED_DGN_SEG_CUID", pdm.getRelatedDgnSegCuid());
				record.addColValue("LABEL_CN", pdm.getDataString("LABEL_CN"));
				record.addColValue("RELATED_SYSTEM_CUID", pdm.getDataString("RELATED_SYSTEM_CUID"));
				
				record.addColValue("SELF_DEFINE_DESCRIPTION", pdm.getDataString("SELF_DEFINE_DESCRIPTION"));
				if("CTP".equalsIgnoreCase(aBmClassId)){
					record.addColValue("A_BM_CLASS_ID", "CTP");
					record.addColValue("A_CTP_CUID", aPointCuid);
					record.addColValue("A_CTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
					record.addColSqlValue("A_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ aPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(aBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ aPointCuid + "'");
					if(ctpCount==0){
						//throw new RuntimeException("您所选择的端口:"+aPointName+"，没有时隙！");
						record.addColValue("A_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("A_BM_CLASS_ID", "CTP");
						record.addColSqlValue("A_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
						record.addColSqlValue("A_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + aPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+aPointName+"，时隙数量不唯一！");
					}
					record.addColValue("A_PTP_CUID", aPointCuid);
					record.addColValue("A_PTP_NAME", aPointName);
					record.addColSqlValue("A_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "'");
					record.addColSqlValue("A_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ aPointCuid + "')");
				}
				
				if("CTP".equalsIgnoreCase(zBmClassId)){
					record.addColValue("Z_BM_CLASS_ID", "CTP");
					record.addColValue("Z_CTP_CUID", zPointCuid);
					record.addColValue("Z_CTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM CTP C,TRANS_ELEMENT E  WHERE C.RELATED_NE_CUID = E.CUID AND C.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
					record.addColSqlValue("Z_PTP_CUID",	"SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_PTP_NAME",	"SELECT LABEL_CN FROM PTP WHERE CUID = (SELECT RELATED_PTP_CUID FROM CTP WHERE CUID = '"+ zPointCuid + "')");
				}else if("PTP".equalsIgnoreCase(zBmClassId)){
					ctpCount = this.IbatisResDAO.calculate("SELECT count(*) FROM CTP WHERE RELATED_PTP_CUID = '"+ zPointCuid + "'");
					if(ctpCount==0){
						//throw new RuntimeException("您所选择的端口:"+zPointName+"，没有时隙！");
						record.addColValue("Z_BM_CLASS_ID", "PTP");
					}else if (ctpCount == 1) {
						record.addColValue("Z_BM_CLASS_ID", "CTP");
						record.addColSqlValue("Z_CTP_CUID",	"SELECT CUID FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
						record.addColSqlValue("Z_CTP_NAME",	"SELECT LABEL_CN FROM CTP WHERE RELATED_PTP_CUID = '" + zPointCuid + "'");
					}else {
						throw new RuntimeException("您所选择的端口:"+zPointName+"，时隙数量不唯一！");
					}
					record.addColValue("Z_PTP_CUID", zPointCuid);
					record.addColValue("Z_PTP_NAME", zPointName);
					record.addColSqlValue("Z_ROOM_CUID","SELECT E.RELATED_ROOM_CUID FROM PTP P,TRANS_ELEMENT E  WHERE P.RELATED_NE_CUID = E.CUID AND P.CUID ='"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_CUID","SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "'");
					record.addColSqlValue("Z_NE_NAME", "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID = (SELECT RELATED_NE_CUID FROM PTP WHERE CUID = '"+ zPointCuid + "')");
				}
				
				record.addColSqlValue(
						"RATE",
						"SELECT MIN(RATE) FROM(SELECT PORT_RATE AS RATE FROM PTP WHERE CUID IN( '"+
								aPointCuid+ "','"+ zPointCuid+ "') " + " )");
				//分支1端口
				if (StringUtils.isNotBlank(aPointCuid2)&&StringUtils.isNotBlank(zPointCuid2)){
					record.addColValue("A_NE_CUID2", aPointCuid2);
					record.addColValue("Z_NE_CUID2", zPointCuid2);
					if("CTP".equalsIgnoreCase(aBmClassId2)){
						record.addColSqlValue("A_NE_NAME2",
								"SELECT P.LABEL_CN||'.'||C.LABEL_CN FROM PTP P,CTP C " +
								"WHERE C.RELATED_PTP_CUID = P.CUID AND C.CUID = '"
								+ aPointCuid2 +"'");
					}else if("PTP".equalsIgnoreCase(aBmClassId2)){
					    record.addColValue("A_NE_NAME2", aPointName2);
					}
                    if("CTP".equalsIgnoreCase(zBmClassId2)){
                    	record.addColSqlValue("Z_NE_NAME2",
                    			"SELECT P.LABEL_CN||'.'||C.LABEL_CN FROM PTP P,CTP C " +
                    			"WHERE C.RELATED_PTP_CUID = P.CUID AND C.CUID = '"
								+ zPointCuid2 +"'");
					}else if("PTP".equalsIgnoreCase(zBmClassId2)){
						record.addColValue("Z_NE_NAME2", zPointName2);
					}
				    
				}
				if (StringUtils.isNotBlank(ctpName1)){
                	record.addColValue("A_CTP_NAME2", ctpName1);
    				record.addColValue("A_CTP_CUID2", transSysCuid1);
				}
			
				//分支2端口
				if (StringUtils.isNotBlank(aPointCuid3) && StringUtils.isNotBlank(zPointCuid3)){
					record.addColValue("A_PTP_CUID2", aPointCuid3);
					record.addColValue("Z_PTP_CUID2", zPointCuid3);
					if("CTP".equalsIgnoreCase(aBmClassId3)){
						record.addColSqlValue("A_PTP_NAME2",
                    			"SELECT P.LABEL_CN||'.'||C.LABEL_CN FROM PTP P,CTP C " +
                    			"WHERE C.RELATED_PTP_CUID = P.CUID AND C.CUID = '"
								+ aPointCuid3 +"'");
					}else if("PTP".equalsIgnoreCase(aBmClassId3)){
						record.addColValue("A_PTP_NAME2", aPointName3);
					}
                    if("CTP".equalsIgnoreCase(zBmClassId3)){
                    	record.addColSqlValue("Z_PTP_NAME2",
                    			"SELECT P.LABEL_CN||'.'||C.LABEL_CN FROM PTP P,CTP C " +
                    			"WHERE C.RELATED_PTP_CUID = P.CUID AND C.CUID = '"
								+ zPointCuid3 +"'");
					}else if("PTP".equalsIgnoreCase(zBmClassId3)){
						record.addColValue("Z_PTP_NAME2", zPointName3);
					}
					
					
					
					
				}
				if (StringUtils.isNotBlank(ctpName2)){
					record.addColValue("Z_CTP_NAME2", ctpName2);
					record.addColValue("Z_CTP_CUID2", transSysCuid2);
				}
				segDetailList.add(record);
			}
		}
		this.IbatisResDAO.insertDynamicTableBatch(segDetailList);
		if(!resList.isEmpty()){
			this.IbatisResDAO.insertDynamicTableBatch(resList);
		}
	}
	
	/**
	 * 如果是保存逻辑调用，需要把seg 的code 跟新成当前的code
	 * 重置的调用要把code 更新成null
	 */
	protected void deleteDgnSegDetail(List<PathDesignMode> modeList) {
		// 冯海超 删除路由设计草稿明细(通过relatedDgnSegCuid+serviceList或者仅serviceList删除)
		//取出 segCuid，删除SegDetail，更新对应的seg 的code字段
		List<String> segCuidList = new ArrayList<String>();
		List<String> serviceCuidList = new ArrayList<String>();
		for(PathDesignMode pdm :modeList){
			if(StringUtils.isNotBlank(pdm.getRelatedDgnSegCuid()))segCuidList.add(pdm.getRelatedDgnSegCuid());
			if(pdm.getService()!=null) serviceCuidList.add(pdm.getService().getCuid());
		}
		Map param = new HashMap();
		param.put("segCuidList", segCuidList);
		param.put("serviceCuidList", serviceCuidList);
//		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetailRess", param);
//		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetails", param);
		
		List<String> segDetailCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSegDetailsBySegCuid", param);
		Map map=new HashMap();
		map.put("segDetailCuidList",segDetailCuidList);
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetailRessNew", map);
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetailsNew", map);
	}
	
	protected void deleteDgnSegDetail(PathDesignParam pathDesignParam) {
		Map param = new HashMap();
		param.put("serviceCuidList", pathDesignParam.getServiceCuidList());
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetailRess", param);
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteSegDetails", param);
	}
	
	private boolean isEqualAZ(List<Map<String,Object>> transPathList){
		boolean isEqual = false;
		for(int i=0,j=i+1,size=transPathList.size();i<j&&j<size;i++,j++){
			Map<String,Object> mapi = transPathList.get(i);
			Map<String,Object> mapj = transPathList.get(j);
			if(getValueByLabelCn(mapi,"A_SITE_NAME")!=null && getValueByLabelCn(mapj,"A_SITE_NAME")!=null
			&& getValueByLabelCn(mapi,"Z_SITE_NAME")!=null && getValueByLabelCn(mapj,"Z_SITE_NAME")!=null){
				if(getValueByLabelCn(mapi,"A_SITE_NAME").trim().equals(getValueByLabelCn(mapj,"A_SITE_NAME").trim())
				&& getValueByLabelCn(mapi,"Z_SITE_NAME").trim().equals(getValueByLabelCn(mapj,"Z_SITE_NAME").trim())){
					isEqual = true;
				}else{
					isEqual = false;
				}
			}
			if(getValueByLabelCn(mapi,"A_PTP_NAME")!=null && getValueByLabelCn(mapj,"A_PTP_NAME")!=null
			&& getValueByLabelCn(mapi,"Z_PTP_NAME")!=null && getValueByLabelCn(mapj,"Z_PTP_NAME")!=null){
				if(getValueByLabelCn(mapi,"A_PTP_NAME").trim().equals(getValueByLabelCn(mapj,"A_PTP_NAME").trim())
				&& getValueByLabelCn(mapi,"Z_PTP_NAME").trim().equals(getValueByLabelCn(mapj,"Z_PTP_NAME").trim())){
					isEqual = true;
				}else{
					isEqual = false;
				}
			}
			if(getValueByLabelCn(mapi,"A_POINT_NAME")!=null && getValueByLabelCn(mapj,"A_POINT_NAME")!=null
			&& getValueByLabelCn(mapi,"Z_POINT_NAME")!=null && getValueByLabelCn(mapj,"Z_POINT_NAME")!=null){
				if(getValueByLabelCn(mapi,"A_POINT_NAME").trim().equals(getValueByLabelCn(mapj,"A_POINT_NAME").trim())
				&& getValueByLabelCn(mapi,"Z_POINT_NAME").trim().equals(getValueByLabelCn(mapj,"Z_POINT_NAME").trim())){
					isEqual = true;
				}else{
					isEqual = false;
				}
			}
			String aCtpNamei = "";
			String zCtpNamei = "";
			String aCtpNamej = "";
			String zCtpNamej = "";
			if(getValueByLabelCn(mapi,"ORIG_CTP_NAME")!=null && getValueByLabelCn(mapi,"DEST_CTP_NAME")!=null){
				if(getValueByLabelCn(mapi,"ORIG_CTP_NAME").trim().length()>0 && getValueByLabelCn(mapi,"DEST_CTP_NAME").trim().length()>0){
					aCtpNamei = getValueByLabelCn(mapi,"ORIG_CTP_NAME").split("/")[0];
					zCtpNamei = getValueByLabelCn(mapi,"DEST_CTP_NAME").split("/")[0];
				}
			}
			if(getValueByLabelCn(mapj,"ORIG_CTP_NAME")!=null && getValueByLabelCn(mapj,"DEST_CTP_NAME")!=null){
				if(getValueByLabelCn(mapj,"ORIG_CTP_NAME").trim().length()>0 && getValueByLabelCn(mapj,"DEST_CTP_NAME").trim().length()>0){
					aCtpNamej = getValueByLabelCn(mapi,"ORIG_CTP_NAME").split("/")[0];
					zCtpNamej = getValueByLabelCn(mapi,"DEST_CTP_NAME").split("/")[0];
				}
			}
			if(aCtpNamei.trim().length()>0 && aCtpNamej.trim().length()>0  
			&&	zCtpNamei.trim().length()>0 && zCtpNamej.trim().length() > 0
			&& aCtpNamei.equals(aCtpNamej) && zCtpNamei.equals(zCtpNamej)){
				isEqual = true;
			}else{
				isEqual = false;
			}
			if(!isEqual){
				break;
			}
		}
		return isEqual;
	}
	
	//add by luoshuyun
	private String handlerRouteDesc(List<String> pathCuidList) {
		String routeDesc = "";
		if(pathCuidList!=null && !pathCuidList.isEmpty()){
			Map pm = new HashMap();
			pm.put("PATH_TYPE", "TRANS_PATH");
			pm.put("pathCuidList", pathCuidList);
			List<Map<String,Object>> transPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectTransPathByPathCuids", pm);
			Map<String,Map<Integer,String>> pathA = new HashMap<String,Map<Integer,String>>();
			Map<String,Map<Integer,String>> pathZ = new HashMap<String, Map<Integer,String>>();
			Map<Integer,String> midPathAMap = new HashMap<Integer,String>();
			Map<Integer,String> midPathZMap = new HashMap<Integer,String>();
			List routeDescList = new ArrayList();
			if(transPathList!=null && !transPathList.isEmpty()){
				if(transPathList.size()==1){
					Map<String,Object> map = transPathList.get(0);
					routeDesc = getValueByLabelCn(map,"ROUTE_DESCIPTION").trim();
				}else{
					boolean isEqual = isEqualAZ(transPathList);
					logger.info("------------------------isEqual="+isEqual);
					for(int i=0,size=transPathList.size();i<size;i++){
						Map<String,Object> map = transPathList.get(i);
						if(isEqual){
							String aPtp = getValueByLabelCn(map,"A_PTP_NAME").trim();
							if(!pathA.containsKey(aPtp)){
								pathA.put(aPtp, new HashMap<Integer,String>());	
							}
							String actp = StringUtils.isEmpty(getValueByLabelCn(map,"ORIG_CTP_NAME").trim())?getValueByLabelCn(map,"A_POINT_NAME").trim():getValueByLabelCn(map,"ORIG_CTP_NAME").trim();
							if(actp.indexOf("M")!=-1){
								String actpStr = actp.substring(actp.lastIndexOf("M")+1, actp.length());
								String actp155 = actp.contains("/")?actp.substring(0,actp.lastIndexOf("/")):"";
								String actp2M = actp.contains("/")?actp.substring(actp.lastIndexOf("/")+1,actp.length()):actp;
								if(StringUtils.isNumeric(actpStr)){
									Integer actpNo = Integer.valueOf(actpStr);
									pathA.get(aPtp).put(actpNo, actp155);
									midPathAMap.put(actpNo, actp2M);
								}
							} 
							
							String zPtp = getValueByLabelCn(map,"Z_PTP_NAME").trim();
							if(!pathZ.containsKey(zPtp)){
								pathZ.put(zPtp, new HashMap<Integer,String>());
							}
							String zctp = StringUtils.isEmpty(getValueByLabelCn(map,"DEST_CTP_NAME").trim())?getValueByLabelCn(map,"Z_POINT_NAME").trim():getValueByLabelCn(map,"DEST_CTP_NAME").trim();
							if(zctp.indexOf("M")!=-1){
								String zctpStr = zctp.substring(zctp.lastIndexOf("M")+1, zctp.length());
								String zctp155 = zctp.contains("/")?zctp.substring(0,zctp.lastIndexOf("/")):"";
								String zctp2M = zctp.contains("/")?zctp.substring(zctp.lastIndexOf("/")+1,zctp.length()):zctp;
								if(StringUtils.isNumeric(zctpStr)){
									Integer zctpNo = Integer.valueOf(zctpStr);
									pathZ.get(zPtp).put(zctpNo, zctp155);
									midPathZMap.put(zctpNo, zctp2M);
								}
							}
						}else{
							if(getValueByLabelCn(map,"ROUTE_DESCIPTION")!=null && getValueByLabelCn(map,"ROUTE_DESCIPTION").trim().length()>0){
								routeDescList.add(getValueByLabelCn(map,"ROUTE_DESCIPTION").trim());
							}else{
								String aSite = getValueByLabelCn(map,"A_SITE_NAME").trim();
								String zSite = getValueByLabelCn(map,"Z_SITE_NAME").trim();
								String aPtp = getValueByLabelCn(map,"A_PTP_NAME").trim();
								String zPtp = getValueByLabelCn(map,"Z_PTP_NAME").trim();
								String actp = StringUtils.isEmpty(getValueByLabelCn(map,"ORIG_CTP_NAME").trim())?getValueByLabelCn(map,"A_POINT_NAME").trim():getValueByLabelCn(map,"ORIG_CTP_NAME").trim();
								String zctp = StringUtils.isEmpty(getValueByLabelCn(map,"DEST_CTP_NAME").trim())?getValueByLabelCn(map,"Z_POINT_NAME").trim():getValueByLabelCn(map,"DEST_CTP_NAME").trim();
								if(actp.length()>0 && zctp.length()>0){
									String routeDescStr = aSite+"("+aPtp+"/"+actp+")("+zPtp+"/"+zctp+")"+zSite;
									logger.info("--------------------routeDescStr="+routeDescStr);
									routeDescList.add(routeDescStr);
								}else{
									String routeDescStr = aSite+"("+aPtp+")("+zPtp+")"+zSite;
									logger.info("--------------------routeDescStr="+routeDescStr);
									routeDescList.add(routeDescStr);
								}
							}
						}
					}
					if(isEqual){
						String pathDescA = "";
						String pathDescZ = "";
						String midPathA = "";
						String midPathZ = "";
						if(!pathA.isEmpty()){ 
							String pathDescStrA = generatePathDesc(pathA);
							if(pathDescStrA.trim().length()>0){
								pathDescA = "("+pathDescStrA+")";
							}
						}
						if(!pathZ.isEmpty()){
							String pathDescStrZ = generatePathDesc(pathZ);
							if(pathDescStrZ.trim().length()>0){
								pathDescZ = "("+pathDescStrZ+")";
							}
						}
						if(!midPathAMap.isEmpty()){
							String midPathStrA = generatePathDescMindle(midPathAMap);
							if(midPathStrA.trim().length()>0){
								midPathA = "【"+midPathStrA+"】";
							}
						}
						if(!midPathZMap.isEmpty()){
							String midPathStrZ = generatePathDescMindle(midPathZMap);
							if(midPathStrZ.trim().length()>0){
								midPathZ = "【"+midPathStrZ+"】";
							}
						}
						Map<String,Object> map = transPathList.get(0);
						String aSite = getValueByLabelCn(map,"A_SITE_NAME").trim();
						String zSite = getValueByLabelCn(map,"Z_SITE_NAME").trim();
						logger.info("-------------------pathDescA="+pathDescA);
						logger.info("-------------------pathDescZ="+pathDescZ);
						logger.info("-------------------midPathA="+midPathA);
						logger.info("-------------------midPathZ="+midPathZ);
						routeDesc = (aSite+pathDescA+midPathA+pathDescZ+midPathZ+zSite);
					}else{
						routeDesc = StringUtils.join(routeDescList, "~");
					}
				}
			}
		}
		return routeDesc;
	}
	
	public String getValueByLabelCn(Map<String,Object> map,String labelCn){
		String value = "";
		if(map!=null&&!map.isEmpty()){
			value = IbatisDAOHelper.getStringValue(map, labelCn);
		}
		return value;
	}
	/**
	 * 提交
	 */
	protected void submitDgnSeg(PathDesignParam pathDesignParam){
		List<String> serviceCuidList = pathDesignParam.getServiceCuidList();
		Map<String, Object> param = pathDesignParam.getParamForQuery();
		List<Map<String,Object>> segAllList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectSegDetailForSubmit", param);
		List<Map<String,Object>> segResList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectSegDetailResForSubmit", param);
		logger.info("<!------------------查询草稿表数据完毕------------------>");
		Map<String,ServicePath> serviceMap = this.getServiceBO().getRoutePathDetail(serviceCuidList);
		logger.info("<!------------------取调度中电路数据完毕------------------>");
		this.submitDataProcess(serviceMap, segAllList,segResList);
	}
	/**
	 * 数据处理
	 * @param serviceCuidList
	 * @param param
	 */
	public void submitDataProcess(Map<String,ServicePath> serviceMap,List<Map<String,Object>> segAllList,List<Map<String,Object>> segResList) {
		List<String> serviceCuidList = new ArrayList<String>();
		//List<Map<String, Object>> attempTraphList = new ArrayList<Map<String, Object>>();
		Map<String,ServiceDesignPath> serviceDesignMap = new HashMap<String, ServiceDesignPath>();
		if(segResList == null) {
			segResList = new ArrayList<Map<String,Object>>();
		}
		logger.info("<!------------------整合草稿表的数据开始------------------>");
		for(Map<String,Object> segMap:segAllList){
			Iterator<Map<String,Object>> resListIter = segResList.iterator();
			String serviceCuid = IbatisDAOHelper.getStringValue(segMap, "RELATED_SERVICE_CUID");
			ServiceDesignPath service =  serviceDesignMap.get(serviceCuid);
			if(service==null){
				serviceCuidList.add(serviceCuid);
				//attempTraphList = TraphMaintainBO.findAttempTraph(serviceCuidList);
				service = new ServiceDesignPath(serviceMap.get(serviceCuid));
				service.setWay(IbatisDAOHelper.getStringValue(segMap, "WAY"));
				service.setTraphLevel(IbatisDAOHelper.getStringValue(segMap, "TRAPH_LEVEL"));
				if(service.isHasError()&&
					!(PathDesignConstants.WAY_SINGLE.equals(service.getWay())||
						PathDesignConstants.WAY_A_Z.equals(service.getWay()))){
					throw new RuntimeException("电路："+service.getLabelCn()+"，历史路由数据错误，请按照 【A-Z方案或者单条】进行路由设计！");
				}
				service.setRoutePoint1(IbatisDAOHelper.getStringValue(segMap, "ROUTE_POINT1"));
				service.setRoutePoint2(IbatisDAOHelper.getStringValue(segMap, "ROUTE_POINT2"));
				serviceDesignMap.put(serviceCuid, service);
			}
			
			String segCuid = IbatisDAOHelper.getStringValue(segMap, "CUID");
			String perentSegCuid = IbatisDAOHelper.getStringValue(segMap, "PARENT_DGN_SEG_CUID");
			int parentIndexNo = IbatisDAOHelper.getIntValue(segMap, "PARENT_INDEX_NO");
			PathPoint apoint = new PathPoint(IbatisDAOHelper.getStringValue(segMap, "A_POINT_AFT_TYPE"), 
											IbatisDAOHelper.getStringValue(segMap, "A_POINT_AFT_CUID"), 
											IbatisDAOHelper.getStringValue(segMap, "A_POINT_AFT_NAME"));
			PathPoint zpoint = new PathPoint(IbatisDAOHelper.getStringValue(segMap, "Z_POINT_AFT_TYPE"), 
											IbatisDAOHelper.getStringValue(segMap, "Z_POINT_AFT_CUID"), 
											IbatisDAOHelper.getStringValue(segMap, "Z_POINT_AFT_NAME"));
			PathData seg =  service.getSeg(segCuid);
			logger.info("----------------seg:"+seg+"---------------");
			logger.info("----------------seg:"+IbatisDAOHelper.getStringValue(segMap, "CODE")+"---------------");
			if(seg == null){
				seg = new PathData();
				seg.setRoutePathCuid(segCuid);
				seg.setServiceCuid(serviceCuid);
				seg.setApoint(apoint);
				seg.setZpoint(zpoint);
				seg.setSeg(true);
				seg.setCode(IbatisDAOHelper.getStringValue(segMap, "CODE"));
				seg.addData(segMap);
			}
			if(StringUtils.isBlank(perentSegCuid)){
				service.addSeg(seg);
			}else{
				seg.setRowNum(parentIndexNo);
				PathData pseg = service.getSeg(perentSegCuid);
				pseg.addMultPathList(seg);
			}
			String detailCuid = IbatisDAOHelper.getStringValue(segMap, "DETAIL_CUID");
			if(StringUtils.isNotEmpty(detailCuid)){
				while(resListIter.hasNext()){
					Map<String,Object> resMap = resListIter.next();
					if(detailCuid.equals(IbatisDAOHelper.getStringValue(resMap, "RELATED_SEG_DETAIL_CUID"))){
						seg.addResData(resMap);
						resListIter.remove();
					}
				}
			}
		}
		logger.info("<!------------------整合草稿表的数据完毕------------------>");
		logger.info("<!------------------根据路由设计方案和转接点重新生成新的PathData List 开始----------------->");
		// 判断插入的位置并记录(后续更新INDEX_PATH_ROUTE要用,根据新插入的数量)
		// 记录所有的待删除的pathData
		//routeCuid 用来删除转接点表
		List<String> routeCuidList = new ArrayList<String>();
		List<PathData> delPathList = new ArrayList<PathData>();
		for(ServiceDesignPath service:serviceDesignMap.values()){
			routeCuidList.add(service.getRouteCuid());
			List<PathData> segList =  service.getSegList();
			List<PathData> pathList = service.getPathList();
			List<PathPoint> pointList = service.getPoints();
			//计算需要删除的起点和终点
			Map<String,Integer>  seRes= this.getStartEnd(service, pointList);
			int start = seRes.get("start");
			int end = seRes.get("end");
			List<PathData> newPathList = service.getNewPathList();
			if (!pathList.isEmpty()) {
				for (int i = 0; i < pathList.size(); i++) {
					PathData pathData = pathList.get(i);
					if (!service.isHasError() && (i < start || i >= end )) {
						newPathList.add(pathData);
					} else {// 记录需要删除的pathData
						delPathList.add(pathData);
					}
				}
			}
			if (newPathList.size() == 0 || start == -1) {
				newPathList.addAll(segList);
			} else {
				newPathList.addAll(start, segList);
			}
		}
		logger.info("<!------------------根据路由设计方案和转接点重新生成新的PathData List 完毕------------------>");
		//记录所有待添加的数据(所有的关系，record，)
		List<Record> traphUpdateRecordList = new ArrayList<Record>();
		List<Record> traphRecordPkList = new ArrayList<Record>();
		
		List<Record> routeAddRecordList = new ArrayList<Record>();
		List<Record> routeUpdateRecordList = new ArrayList<Record>();
		List<Record> routeRecordPkList = new ArrayList<Record>();
		
		List<Record> routePathAddRecordList = new ArrayList<Record>();
		List<Record> routePathUpdateRecordList = new ArrayList<Record>();
		List<Record> routePathRecordPkList = new ArrayList<Record>();
		
		List<Record> pathDetailAddReocrdList = new ArrayList<Record>();
		
		List<Record> pointRecordList = new ArrayList<Record>(); 
		
		List<Map<String,String>> ctpAttempTraphList = new ArrayList<Map<String,String>>();
		List<String> ptpCuidList = new ArrayList<String>();
		List<String> ctpCuidList = new ArrayList<String>();
		Set<String> transPathCuidSet = new HashSet<String>();
		List<String> attTraphCuidList = new ArrayList<String>();
		Date now = new Date();
		logger.info("<!------------------开始循环完整的电路信息（处理路由描述，关系信息，创建各种通道）------------------>");
		List<Integer> existsNo=new ArrayList<Integer>();
		for(ServiceDesignPath service:serviceDesignMap.values()){
			logger.info("<!------------------开始处理电路："+service.getLabelCn()+" 的数据------------------>");
			//记录 startPathData,endPathData 为了更新电路的A,Z 端传输信息（）
			PathData startPathData = new PathData();
			PathData endPathData = new PathData();
			
			List<String> routeDescList = new ArrayList<String>();
			List<String> pointDesc = new ArrayList<String>();
			List<String> pathCuidList = new ArrayList<String>();
			
			
			Record traphUpdateRecord = new Record("ATTEMP_TRAPH");
			Record traphRecordPk = new Record("ATTEMP_TRAPH");
			traphRecordPk.addColValue("CUID", service.getCuid());
			attTraphCuidList.add(service.getCuid());
			String routeCuid = service.getRouteCuid();
			Record routeRecord = new Record("ATTEMP_TRAPH_ROUTE");
			boolean isNeedCreateRoute = false;
			if(StringUtils.isBlank(routeCuid)){
				routeCuid = CUIDHexGenerator.getInstance().generate("ATTEMP_TRAPH_ROUTE");
				isNeedCreateRoute = true;
				service.setRouteCuid(routeCuid);
			}
			
			//循环所有的路由
			List<CtpRelation> serviceCtpRelationList = new ArrayList<CtpRelation>();
			//如果有mstp路由就所有路由段都是mstp
			boolean isMstp=false;
			for(int i=0;i<service.getNewPathList().size();i++){
				PathData pathData = service.getNewPathList().get(i);
				if(pathData.getMultPathList()!=null&&pathData.getMultPathList().size()>0){
					isMstp=true;
					break;
				}
			}
			if(isMstp){
				for(int i=0;i<service.getNewPathList().size();i++){
					PathData pathData = service.getNewPathList().get(i);
					if(pathData.getMultPathList()==null||pathData.getMultPathList().size()==0){
						PathData pathDataNew=new PathData();
						pathDataNew.setRouteCuid(pathData.getRouteCuid());
						pathDataNew.setServiceCuid(pathData.getServiceCuid());
						pathDataNew.setRoutePathCuid(pathData.getRoutePathCuid());
						pathDataNew.setApoint(pathData.getApoint());
						pathDataNew.setZpoint(pathData.getZpoint());
						pathDataNew.setIndexNo(pathData.getIndexNo());
						pathDataNew.setRowNum(pathData.getRowNum());
						pathDataNew.setSeg(pathData.isSeg());
						pathDataNew.addData(pathData.getData());
						pathDataNew.setResDatas(pathData.getResDatas());
						pathDataNew.addMultPathList(pathData);
						service.getNewPathList().set(i, pathDataNew);
					}
				}
			}
//			Map<String, Map<String, Object>> segAllListGroup = new HashMap<String, Map<String, Object>>();
//			//if(SysProperty.getInstance().getValue("districtName").trim().equals("湖北")){
//			for(Map<String, Object> obj : segAllList) {
//				String key = IbatisDAOHelper.getStringValue(obj, "INDEX_NO");
//				if(segAllListGroup.get(key) == null&&obj.get("PARENT_DGN_SEG_CUID")==null) {
//					segAllListGroup.put(key, obj);
//				}
//			}
			//}
			for(int i=0;i<service.getNewPathList().size();i++){
				PathData pathData = service.getNewPathList().get(i);
				if (StringUtils.isNotBlank(pathData.getCode())){
					logger.info("-------------pathData..getCode()"+pathData.getCode());
					logger.info("-------------pathData.isSeg()"+pathData.isSeg());
				}
				pathData.setIndexNo(i);
				pathData.setRouteCuid(routeCuid);
				if(!pathData.getMultPathList().isEmpty()){
					service.setTraphType("3");
				}
				logger.info("<!----开始处理电路："+service.getLabelCn()+"，路由【"+pathData.getIndexNo()+"】的数据------------------>");
				TraphPathData traphPathData = TraphPathBO.pathDataProcess(pathData, now);
				routePathAddRecordList.addAll(traphPathData.getRoutePathAddRecordList());
				routePathUpdateRecordList.addAll(traphPathData.getRoutePathUpdateRecordList());
				routePathRecordPkList.addAll(traphPathData.getRoutePathRecordPkList());
				pointRecordList.addAll(traphPathData.getPointRecordList());
				routeDescList.addAll(traphPathData.getRouteDescAddList());
//				routeDesc.add(traphPathData.getRouteDescription());
				pathCuidList.addAll(traphPathData.getPathCuidAddList());
				serviceCtpRelationList.addAll(traphPathData.getCtpRelationList());
				pathDetailAddReocrdList.addAll(traphPathData.getPathDetailAddReocrdList());
				if(StringUtils.isNotBlank(pathData.getDataString("A_PTP_CUID"))){
					if(startPathData.getData().isEmpty()) startPathData.addData(pathData.getData());
					endPathData.addData(pathData.getData());
				}
				if(i!=service.getNewPathList().size()-1){
					pointDesc.add(pathData.getZpoint().getText());
					Record indi = new Record("ATTTRAPH_TO_INDI_POINTS");
					indi.addColValue("CUID", CUIDHexGenerator.getInstance().generate("ATTTRAPH_TO_INDI_POINTS"));
					indi.addColSqlValue("OBJECTID", indi.getObjectIdSql());
					indi.addColValue("INDI_INDEX", i);
					indi.addColValue("LABEL_CN", pathData.getZpoint().getText());
					indi.addColValue("INDIRECT_POINT_TYPE", pathData.getZpoint().getType());
					indi.addColValue("TRAPH_ROUTE_CUID", routeCuid);
					indi.addColValue("INDIRECT_POINT_CUID", pathData.getZpoint().getValue());
					indi.addColValue("CREATE_TIME", new Date());
					pointRecordList.add(indi);
				}
				if(i==0){
					startPathData.setApoint(pathData.getApoint());
					startPathData.setZpoint(pathData.getZpoint());
//						String A_MAC=IbatisDAOHelper.getStringValue(segAllListGroup.get(String.valueOf(i)), "A_MAC");
//						String A_MAC_NAME=IbatisDAOHelper.getStringValue(segAllListGroup.get(String.valueOf(i)), "A_MAC_NAME");
//						if(StringUtils.isNotEmpty(A_MAC)){
//							startPathData.getData().put("A_PTP_CUID", A_MAC);
//							startPathData.getData().put("A_PTP_NAME", A_MAC_NAME);
//						}
				}
				if(i==service.getNewPathList().size()-1){
					endPathData.setApoint(pathData.getApoint());
					endPathData.setZpoint(pathData.getZpoint());
//						String Z_MAC=IbatisDAOHelper.getStringValue(segAllListGroup.get(String.valueOf(i)), "Z_MAC");
//						String Z_MAC_NAME=IbatisDAOHelper.getStringValue(segAllListGroup.get(String.valueOf(i)), "Z_MAC_NAME");
//						if(StringUtils.isNotEmpty(Z_MAC)){
//							endPathData.getData().put("Z_PTP_CUID", Z_MAC);
//							endPathData.getData().put("Z_PTP_NAME", Z_MAC_NAME);
//						}
				}
				logger.info("<!------------------电路："+service.getLabelCn()+"，路由【"+pathData.getIndexNo()+"】处理完毕----------------->");
			}
			
			//该电路所有路由循环完毕
			for(CtpRelation cr: serviceCtpRelationList){
				if(StringUtils.isNotBlank(cr.getCtpName())){
					Map<String,String> ctpAttempTraph = new HashMap<String, String>();
					ctpAttempTraph.put("PTP_CUID", cr.getPtpCuid());
					ctpAttempTraph.put("CTP_NAME", cr.getCtpName());
					ctpAttempTraph.put("RELATED_ATTEMP_TRAPH_CUID", cr.getServiceCuid());
					ctpAttempTraphList.add(ctpAttempTraph);
				}
				ptpCuidList.add(cr.getPtpCuid());
				if(StringUtils.isNotBlank(cr.getCtpCuid())) ctpCuidList.add(cr.getCtpCuid());
				if("TRANS_PATH".equals(cr.getPathType())){
					if (StringUtils.isNotEmpty(cr.getPathCuid())){
						transPathCuidSet.add(cr.getPathCuid());
					}
					if("2".equals(service.getCheckFlag()))service.setCheckFlag("1");
				}
			}
			
			traphUpdateRecord.addColValue("ZD_SITE_TYPE_A", startPathData.getApoint().getType());
			traphUpdateRecord.addColValue("RELATED_A_ZD_SITE_CUID", startPathData.getApoint().getValue());
			traphUpdateRecord.addColValue("RELATED_A_END_SITE_CUID",startPathData.getApoint().getValue());
			traphUpdateRecord.addColValue("RELATED_A_SITE_CUID", startPathData.getApoint().getValue());
			/**/
			traphUpdateRecord.addColValue("RELATED_A_PORT_CUID", startPathData.getDataString("A_PTP_CUID"));
			traphUpdateRecord.addColValue("RELATED_A_PORT_CUID2", startPathData.getDataString("A_PTP_CUID2"));
			traphUpdateRecord.addColValue("END_PORT_A", startPathData.getDataString("A_PTP_NAME"));
			traphUpdateRecord.addColValue("RELATED_NE_A_CUID", startPathData.getDataString("A_NE_CUID"));
			traphUpdateRecord.addColValue("RELATED_A_ROOM_CUID", startPathData.getDataString("A_ROOM_CUID"));
			
			traphUpdateRecord.addColValue("ZD_SITE_TYPE_Z", endPathData.getZpoint().getType());
			traphUpdateRecord.addColValue("RELATED_Z_ZD_SITE_CUID", endPathData.getZpoint().getValue());
			traphUpdateRecord.addColValue("RELATED_Z_END_SITE_CUID",endPathData.getZpoint().getValue());
			traphUpdateRecord.addColValue("RELATED_Z_SITE_CUID", endPathData.getZpoint().getValue());
			traphUpdateRecord.addColValue("RELATED_Z_PORT_CUID", endPathData.getDataString("Z_PTP_CUID"));
			/**/
			traphUpdateRecord.addColValue("RELATED_Z_PORT_CUID2", endPathData.getDataString("Z_PTP_CUID2"));
			traphUpdateRecord.addColValue("END_PORT_Z", endPathData.getDataString("Z_PTP_NAME"));
			traphUpdateRecord.addColValue("RELATED_NE_Z_CUID", endPathData.getDataString("Z_NE_CUID"));
			traphUpdateRecord.addColValue("RELATED_Z_ROOM_CUID", endPathData.getDataString("Z_ROOM_CUID"));
			//将传输侧的配线架跟新到attemp_traph的对应字段
			if(StringUtils.isNotBlank(startPathData.getDataString("A_PTP_CUID"))){
				traphUpdateRecord.addColSqlValue("END_DF_PORT_A", getSqlLabelCn(startPathData.getDataString("A_PTP_CUID")));
				traphUpdateRecord.addColSqlValue("RELATED_DF_PORT_A_CUID", getSqlCuid(startPathData.getDataString("A_PTP_CUID")));
			}
			if(StringUtils.isNotBlank(endPathData.getDataString("Z_PTP_CUID"))){
				traphUpdateRecord.addColSqlValue("END_DF_PORT_Z", getSqlLabelCn(endPathData.getDataString("Z_PTP_CUID")));
				traphUpdateRecord.addColSqlValue("RELATED_DF_PORT_Z_CUID", getSqlCuid(endPathData.getDataString("Z_PTP_CUID")));
			}
			//add by luoshuyun
			String routedesc = handlerRouteDesc(pathCuidList);
			if(routedesc ==null|| routedesc.trim().equals("")){
//				String routeDescMain="";
//				String routeDescBak="";
//				if(CollectionHelper.isNotEmpty(routeDescList)){
//					int i=1;
//					for(String routeDesc : routeDescList){
//						String[] routeDescs=routeDesc.split("@");
//						routeDescMain=routeDescMain+routeDescs[0]+",";
//						if(routeDescs.length>1&&StringUtils.isNotEmpty(routeDescs[1])){
//							routeDescBak=routeDescBak+routeDescs[1]+","; 
//						}
//						i++;
//					}
//					routedesc ="主用："+	routeDescMain.substring(0,routeDescMain.length()-1)+
//					((StringUtils.isEmpty(routeDescBak))?"":("<br/>"+"备用："+
//							routeDescBak.substring(0,routeDescBak.length()-1)));		
//				}
				routedesc = StringUtils.join(routeDescList, ",");
			}
			traphUpdateRecord.addColValue("ZJSITES", StringUtils.join(pointDesc, ","));
			logger.info("--------routedesc="+routedesc);
			traphUpdateRecord.addColValue("PATHINFO", routedesc);
			traphUpdateRecord.addColValue("DESIGN_INFO", routedesc);
			long checkFlag = this.getTraphCheckFlag(new ArrayList<String>(transPathCuidSet));//根据通道核查状态更新电路核查状态
			traphUpdateRecord.addColValue("CHECK_FLAG", checkFlag);
			
			traphUpdateRecord.addColValue("IS_WHOLE_ROUTE", 1);
			traphUpdateRecord.addColValue("LAST_MODIFY_TIME", now);
			String attemppathtype = "NONE_TYPE";
			if(!service.getNewPathList().isEmpty()){
				logger.info("----------------1");
				attemppathtype = service.getNewPathList().get(0).getPathType();
				TraphMaintainBO.saveAZEndPtpInfo(service);
				TraphMaintainBO.updateExtType(service.getNewPathList(),traphUpdateRecord);
			}
			
			if(service.getApoint()!=null&&service.getZpoint()!=null&&service.getApoint().getValue()!=null&&service.getZpoint().getValue()!=null
					&&(service.getApoint().getValue().equals(startPathData.getApoint().getValue())&&service.getZpoint().getValue().equals(endPathData.getZpoint().getValue()))){
				String traphname =service.getLabelCn();
				if(service.getTraphLevel().equals("7")||service.getTraphLevel().equals("8")||service.getTraphLevel().equals("9")){
					if(StringUtils.isNotEmpty(attemppathtype)&&attemppathtype.equalsIgnoreCase("ATTEMP_PTN_PATH")){
						
						if(!traphname.endsWith("/P")){
							traphname = traphname +"/P";
						}
					}
					traphUpdateRecord.addColValue("LABEL_CN", traphname);
				}
				
 			}else{
				logger.info("----------------service.getCuid() :"+service.getCuid());
				logger.info("----------------existsNo :"+existsNo);
				if(service.getTraphLevel().equals("7")||service.getTraphLevel().equals("8")||service.getTraphLevel().equals("9")){
					TraphMaintainBO.changeTraphNamebyEndStation(service.getCuid(), traphUpdateRecord,startPathData.getApoint().getValue(),endPathData.getZpoint().getValue(),service.getLabelCn(),attemppathtype,existsNo);
					existsNo.add(Integer.parseInt(traphUpdateRecord.getColValue("NO").toString()));
				}
				
			}
			
			traphUpdateRecord.addColValue("ALIAS", service.get("ALIAS"));
			traphUpdateRecord.addColValue("BANDWIDTH", service.get("BANDWIDTH"));
			traphUpdateRecord.addColValue("JHROOM_A", service.get("JHROOM_A"));
			traphUpdateRecord.addColValue("JHROOM_Z", service.get("JHROOM_Z"));
			traphUpdateRecord.addColValue("JHROOM_ACUID", service.get("JHROOM_ACUID"));
			traphUpdateRecord.addColValue("JHROOM_ZCUID", service.get("JHROOM_ZCUID"));
			traphUpdateRecord.addColValue("END_SWITCH_DEV_A",service.get("END_SWITCH_DEV_A"));
			traphUpdateRecord.addColValue("END_SWITCH_DEV_Z",service.get("END_SWITCH_DEV_Z"));
			traphUpdateRecord.addColValue("END_SWITCHDEV_PORT_A",service.get("END_SWITCHDEV_PORT_A"));
			traphUpdateRecord.addColValue("END_SWITCHDEV_PORT_Z",service.get("END_SWITCHDEV_PORT_Z"));
			traphUpdateRecord.addColValue("END_SWITCH_DF_PORT_A",service.get("END_SWITCH_DF_PORT_A"));
			traphUpdateRecord.addColValue("END_SWITCH_DF_PORT_Z",service.get("END_SWITCH_DF_PORT_Z"));
			
			traphUpdateRecordList.add(traphUpdateRecord);
			traphRecordPkList.add(traphRecordPk);
			routeRecord.addColValue("ROUTE_DESCRIPTION", routedesc);
			if(isNeedCreateRoute){
				routeRecord.addColValue("CUID", routeCuid);
				routeRecord.addColSqlValue("OBJECTID", routeRecord.getObjectIdSql());
				routeRecord.addColValue("GT_VERSION", 0);
				routeRecord.addColValue("ROUTE_INDEX", 0);
				routeRecord.addColValue("WORK_STATE", 1);
				routeRecord.addColValue("RELATED_SERVICE_CUID", service.getCuid());
				routeRecord.addColValue("ISDELETE", 0);
				routeRecord.addColValue("CREATE_TIME", now);
				routeRecord.addColValue("LAST_MODIFY_TIME", now);
				routeAddRecordList.add(routeRecord);
			}else {
				routeUpdateRecordList.add(routeRecord);
				Record routeRecordPk = new Record("ATTEMP_TRAPH_ROUTE");
				routeRecordPk.addColValue("CUID", routeCuid);
				routeRecordPkList.add(routeRecordPk);
			}
			logger.info("<!------------------电路："+service.getLabelCn()+"，处理完毕------------------>");
		}
		//调前PTN_PATH
		List<String> ptnPathCuidList = new ArrayList<String>();
		if(attTraphCuidList!=null && attTraphCuidList.size()>0) {
			Map pm = new HashMap();
			pm.put("attempTraphCuidList", attTraphCuidList);
			ptnPathCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPtnPathByAttempTraphCuid", pm);
		}
		logger.info("<!------------------所有电路处理完毕------------------>");
		
		//记录所有待删除的参数
		//routePathCuid 用来删除转接点表，multiPath表，routepath表
		//各种pathCuid用来删除通道，释放关系
		//端口和时隙的cuid 用来修改占用状态
		List<String> routePathCuidList = new ArrayList<String>();
		List<String> multiPathCuidList = new ArrayList<String>();
		
		List<String> delTransPathList = new ArrayList<String>(); 
		List<String> delTextList = new ArrayList<String>();
		List<String> delSelfList = new ArrayList<String>();
		List<String> delPtnList = new ArrayList<String>();
		logger.info("<!------------------处理待删除的数据------------------>");
		for(PathData delPath:delPathList){
			routePathCuidList.add(delPath.getRoutePathCuid());
			if("TRANS_PATH".equals(delPath.getPathType())){
				if(!transPathCuidSet.contains(delPath.getPathCuid())){
					delTransPathList.add(delPath.getPathCuid());
				} 
			}else if("ATTEMP_TEXT_PATH".equals(delPath.getPathType())){
				delTextList.add(delPath.getPathCuid());
			}else if("ATTEMP_SELF_BUILT_PATH".equals(delPath.getPathType())){
				delSelfList.add(delPath.getPathCuid());
			}else if("ATTEMP_PTN_PATH".equals(delPath.getPathType())){
				delPtnList.add(delPath.getPathCuid());
			}
			for(PathData delMultiPath:delPath.getMultPathList()){
				multiPathCuidList.add(delMultiPath.getRoutePathCuid());
				if("TRANS_PATH".equals(delMultiPath.getPathType())){
					if(!transPathCuidSet.contains(delMultiPath.getPathCuid())){
						delTransPathList.add(delMultiPath.getPathCuid());
					} 
				}else if("ATTEMP_TEXT_PATH".equals(delMultiPath.getPathType())){
					delTextList.add(delMultiPath.getPathCuid());
				}else if("ATTEMP_SELF_BUILT_PATH".equals(delMultiPath.getPathType())){
					delSelfList.add(delMultiPath.getPathCuid());
				}else if("ATTEMP_PTN_PATH".equals(delMultiPath.getPathType())){
					delPtnList.add(delMultiPath.getPathCuid());
				}
			}
		}
		logger.info("<!------------------待删除的数据处理完毕------------------>");
		
		logger.info("<!------------------释放TraphTrans------------------>");
		TraphMaintainBO.releaseAttempTraphTransRelation(null, delTransPathList, serviceCuidList);
		if(!delPtnList.isEmpty()&& delPtnList.size()>0){
			logger.info("<!------------------释放Ptn通道------------------>");
			List ptnVirtualLineCuidList = null;
			Map param = new HashMap();
			param.put("ptnPathCuidList", delPtnList);
			ptnVirtualLineCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPtnVirtualLineByPathCuid", param);
			TraphMaintainBO.releaseAttempTraphPtnRelation(null,delPtnList,serviceCuidList);
			logger.info("<!------------------释放隧道纬线------------------>");
			TraphMaintainBO.releaseAttempTraphPtnVirtualLine(null, ptnVirtualLineCuidList, delPtnList);
		}
		if(delTextList!=null && !delTextList.isEmpty()){
			logger.info("<!------------------释放文本通道------------------>");
			TraphMaintainBO.releaseAttempTraphTextRelation(null, delTextList);
		}
		if(delSelfList!=null && !delSelfList.isEmpty()){
			logger.info("<!------------------释放自建段通道------------------>");
			TraphMaintainBO.releaseAttempTraphSelfBuiltRelation(null, delSelfList);
		}
		if(serviceCuidList!=null && !serviceCuidList.isEmpty()){
			logger.info("<!------------------释放调度电路与逻辑口的关系------------------>");
			TraphMaintainBO.releaseAttempLogicPortRelation(null,serviceCuidList);
		}
		
		
		
		//根据参数删除关系数据
		//routeCuidList 删除转接点
		//routePathCuidList 删除转接点，删除atttraph_route_to_path
		//multiPathCuidList 删除attempMultiPath
		List<String> delPointPm = new ArrayList<String>();
		delPointPm.addAll(routeCuidList);
		delPointPm.addAll(routePathCuidList);
		if(delPointPm!=null && !delPointPm.isEmpty()){
			logger.info("<!------------------释放转接点------------------>");
			TraphMaintainBO.releaseAttempIndiPointsRelation(null, delPointPm);
		}
		if(routePathCuidList!=null && !routePathCuidList.isEmpty()){
			logger.info("<!------------------释放AttempRouteToPath------------------>");
			TraphMaintainBO.releaseAttempRouteToPathRelation(null, routePathCuidList);
		}
		if(multiPathCuidList!=null && !multiPathCuidList.isEmpty()){
			logger.info("<!------------------释放AttempMultiPath------------------>");
			TraphMaintainBO.releaseAttempMultiPathRelation(null, multiPathCuidList);
		}
		
		
		if(traphUpdateRecordList!=null && !traphUpdateRecordList.isEmpty()){
			logger.info("<!------------------更新traph表的路由描述字段------------------>");
			TraphMaintainBO.formartDataForRecordList(traphUpdateRecordList);
			this.IbatisResDAO.updateDynamicTableBatch(traphUpdateRecordList, traphRecordPkList);
		}
		
		if(routeAddRecordList!=null && !routeAddRecordList.isEmpty()){
			logger.info("<!------------------添加traphRoute数据------------------>");
			this.IbatisResDAO.insertDynamicTableBatch(routeAddRecordList);
		}
		if(routeUpdateRecordList!=null && !routeUpdateRecordList.isEmpty()){
			logger.info("<!------------------更新traphRoute的路由描述------------------>");
			
			this.IbatisResDAO.updateDynamicTableBatch(routeUpdateRecordList, routeRecordPkList);
		}
		
		if(routePathAddRecordList!=null && !routePathAddRecordList.isEmpty()){
			logger.info("<!------------------添加routePath数据------------------>");
			this.IbatisResDAO.insertDynamicTableBatch(routePathAddRecordList);
		}
		
		if(routePathUpdateRecordList!=null && !routePathUpdateRecordList.isEmpty()){
			logger.info("<!------------------更新routePath的排序字段------------------>");
			this.IbatisResDAO.updateDynamicTableBatch(routePathUpdateRecordList, routePathRecordPkList);
		}
		
		if(!pointRecordList.isEmpty()){
			logger.info("<!------------------添加转接点数据------------------>");
			this.IbatisResDAO.insertDynamicTableBatch(pointRecordList);
		}
		if(!transPathCuidSet.isEmpty()){
			logger.info("<!------------------修改通道的状态为预占用------------------>");
			TraphMaintainBO.setTransPathState( new ArrayList<String>(transPathCuidSet),3);
		}
		List<String> attempPtnPathCuidList = new ArrayList<String>();
		List<String> vlCuidList = new ArrayList<String>();
		if(!pathDetailAddReocrdList.isEmpty()){
			logger.info("<!------------------添加通道详情------------------>");
			for(Record record : pathDetailAddReocrdList) {
				if("ATTEMP_PTN_PATH".equals(record.getTableName())) {
					String cuid = (String)record.getColValue("CUID");
					String vlCuid = (String)record.getColValue("RELATED_VIRTUAL_LINE_CUID");
					vlCuidList.add(vlCuid);
					attempPtnPathCuidList.add(cuid);
				}
			}
			this.IbatisResDAO.insertDynamicTableBatch(pathDetailAddReocrdList);
			if(!vlCuidList.isEmpty()){
				logger.info("<!------------------更新PTN业务状态------------------>");
				List<Map<String,Object>> ptnServices = TraphMaintainBO.findPtnService(ptnPathCuidList, attempPtnPathCuidList, vlCuidList);
				if(ptnServices!=null && !ptnServices.isEmpty()){
					List<String> ptnServiceCuidList = new ArrayList<String>();
					for(Map<String,Object> map : ptnServices) {
						String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
						String userLabel = IbatisDAOHelper.getStringValue(map, "USER_LABEL");
						int pathCount = IbatisDAOHelper.getIntValue(map, "PATH_COUNT");
						int attempPathCount = IbatisDAOHelper.getIntValue(map, "ATTEMP_PATH_COUNT");
						if(pathCount>0 || attempPathCount>0) {
							throw new RuntimeException("业务：" + userLabel + "已被其他电路占用！");
						}
						long state = IbatisDAOHelper.getLongValue(map, "STATE");
						if(state == 1L) {//空闲的业务设置为预占状态
							ptnServiceCuidList.add(cuid);
						}
					}
					//变更PTN业务状态为预占
					if(ptnServiceCuidList!=null && !ptnServiceCuidList.isEmpty()){
						TraphMaintainBO.setPtnServiceState(ptnServiceCuidList, "3");
					}
				}
				
			}
		}
		if(!ptpCuidList.isEmpty()){
			logger.info("<!------------------更新端口的状态是预占用------------------>");
			TraphMaintainBO.setPtpState(ptpCuidList, 3, 1);
			//变更预调度端口状态
			TraphMaintainBO.setPtpNaState(ptpCuidList, 3, 1);
		}
		if(!ctpCuidList.isEmpty()){
			logger.info("<!------------------更新时隙的状态是预占用------------------>");
			TraphMaintainBO.setCtpState(ctpCuidList, 3);
			//变更预调度时隙状态
			TraphMaintainBO.setCtpNaState(ctpCuidList, 3);
		}
		if(!ctpAttempTraphList.isEmpty()){
			logger.info("<!------------------更新打散后的时隙的attemp_traph 字段------------------>");
			Map pm = new HashMap();
			List<List<Map<String, String>>> groupLists = CollectionHelper.groupList(ctpAttempTraphList, 50); 
			for(List<Map<String, String>> groupList :groupLists){
				String msg = "";
				String relatedAttempTraphCuid = (String)groupList.get(0).get("RELATED_ATTEMP_TRAPH_CUID");
				String relatedTraphCuid = relatedAttempTraphCuid.replace("ATTEMP_TRAPH", "TRAPH");   //调整电路的情况，需要找该调度电路对应存量电路
				pm.put("ctpAttempTraphList", groupList);
				//判断打散后的时隙是否被占用
				List<Map<String, Object>> tempTraphCuidList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getLogicPortAttempTraphCuid", pm);
				if(tempTraphCuidList!=null && tempTraphCuidList.size()>0){
					for(Map<String, Object> tempTraphCuidMap : tempTraphCuidList){
						String attempTraphCuid = IbatisDAOHelper.getStringValue(tempTraphCuidMap, "RELATED_ATTEMP_TRAPH_CUID");
						String traphCuid = IbatisDAOHelper.getStringValue(tempTraphCuidMap, "RELATED_TRAPH_CUID");
						String ptpName = IbatisDAOHelper.getStringValue(tempTraphCuidMap, "PTP_NAME");
						String ctpName = IbatisDAOHelper.getStringValue(tempTraphCuidMap, "CTP_NAME");
						String portName = ptpName+"."+ctpName;
						if(StringUtils.isNotEmpty(attempTraphCuid)&&!attempTraphCuid.equals(relatedAttempTraphCuid)){
							logger.info("attempTraphCuid:"+attempTraphCuid+",relatedAttempTraphCuid:"+relatedAttempTraphCuid);
							msg += "("+portName+")已被其他调度电路占用；\r\n";
						}
						//（调整）判断是否被当前电路对应的存量电路占用，若是，则不提示
						if(StringUtils.isNotEmpty(traphCuid)&&!traphCuid.equals(relatedTraphCuid)){
							logger.info("traphCuid:"+traphCuid+",relatedTraphCuid:"+relatedTraphCuid);
							msg += "("+portName+")已被存量电路占用；\r\n";
						}
					}
				}
				if(StringUtils.isNotEmpty(msg)){
					throw new RuntimeException(msg);
				}
				//更新打散后的时隙的attemp_traph 字段
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateLogicPortAttempTraphCuid", pm);
			}
		}
		for (String serviceCuid : serviceCuidList){
			Map attempCuidMap = new HashMap();
			attempCuidMap.put("serviceCuid", serviceCuid);
			List<Map<String,Object>> routeDesciptionList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryRouteDesciption", attempCuidMap);
			
			logger.info("----------routeDesciptionList:"+routeDesciptionList);
			if (routeDesciptionList != null && routeDesciptionList.size()>0){
				List<String> pointsList = new ArrayList<String>();
				Map<String,List<Map<String,Object>>> routeDesciptionMap=new HashMap<String, List<Map<String,Object>>>();
				for (Map<String,Object> map : routeDesciptionList){
					String indexPathRoute = IbatisDAOHelper.getStringValue(map, "INDEX_PATH_ROUTE");
					String newOrigSite = (String)map.get("ORIG_SITE_CUID");
					String newDestSite= (String)map.get("DEST_SITE_CUID");
					String siteName=newOrigSite+"+"+newDestSite+"+"+indexPathRoute;
					if (!pointsList.contains(newOrigSite)){
						pointsList.add(newOrigSite);
					}
					if (!pointsList.contains(newDestSite)){
						pointsList.add(newDestSite);
					}
					
					List<Map<String,Object>> newList = new ArrayList<Map<String,Object>>();
					if(!routeDesciptionMap.containsKey(siteName)){
						newList.add(map);
						routeDesciptionMap.put(siteName, newList);
					}else{
						newList=routeDesciptionMap.get(siteName);
						newList.add(map);
						routeDesciptionMap.put(siteName, newList);
					}
				}
				Map<String,Object> pm = new HashMap<String,Object>();
				pm.put("pointsList", pointsList);
				List<Map<String,Object>> siteList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getSiteByCuids", pm);
				Map<String,Map<String,Object>> sitesList = IbatisDAOHelper.parseList2Map(siteList, "CUID");
				Iterator it=routeDesciptionMap.keySet().iterator();
				Map<Integer,String> descMap = new HashMap<Integer,String>();
				while(it.hasNext()){
					String key=it.next().toString();
					List<Map<String,Object>> list=routeDesciptionMap.get(key);
					 
					String tmp = "";
					String pathType = (String)list.get(0).get("PATH_TYPE");
					Object index = (Object)list.get(0).get("INDEX_PATH_ROUTE");
					int indexNo = Integer.parseInt(index.toString());
					if(pathType.equals("TRANS_PATH")){
						ArrayList arrayList = new ArrayList();
						for(Map map : list){
				    	  String arr[][] = new String[1][3];
				    	  arr[0][0] = (String)map.get("CUID");
				          arr[0][1] = (String)map.get("ROUTE_DESCIPTION");
				          arr[0][2] = "0";
				          arrayList.add(arr);
				        }
						tmp = MultiPathsMerge.mergeRoutes(arrayList, true);
						String[] siteName = key.split("\\+");
			            String aMac = "";
			            String zMac = "";
			            String aMp = "";
			            String zMp = "";
			            if (list.get(0).get("A_MAC")!=null){
			            	aMac = "("+(String)list.get(0).get("A_MAC")+")";
			            }
			            if (list.get(0).get("Z_MAC")!=null){
			            	zMac = "("+(String)list.get(0).get("Z_MAC")+")";
			            }
			            if (list.get(0).get("A_MP")!=null){
			            	aMp = "("+(String)list.get(0).get("A_MP")+")";
			            }
			            if (list.get(0).get("Z_MP")!=null){
			            	zMp = "("+(String)list.get(0).get("Z_MP")+")";
			            }
			            tmp = (String)sitesList.get(siteName[0]).get("LABEL_CN")+aMac+aMp 
			                       +tmp+ zMp+zMac+(String)sitesList.get(siteName[1]).get("LABEL_CN");
						logger.info("----------tmp:"+tmp);
					}else{//PTN
						for(int i =0; i<list.size(); i++){
							if(i==0){
								tmp = (String)list.get(i).get("ROUTE_DESCIPTION");
							}else{
								tmp += "," + (String)list.get(i).get("ROUTE_DESCIPTION");
							}
						}
						logger.info("----------tmp:"+tmp);
					}
				    logger.info("----------tmp:"+tmp);
		            descMap.put(indexNo, tmp);
				}
				
				String desc = "";
//				String descMain="";
//				String descBak="";
//				for(int m = 0;m<descMap.size();m++){
//					String descTemp=descMap.get(m);
//					String[] descTemps=descTemp.split("$$");
//					for(int n=0;n<descTemps.length;n++){
//						//主备关系
//						String[] descTempMB = descTemps[n].split("@");
//						descMain=descMain+descTempMB[0]+",";
//						if(descTempMB.length>1&&StringUtils.isNotEmpty(descTempMB[1])){
//						descBak=descBak+descTempMB[1]+",";
//						}
//					}
//				}
//				desc="主用："+descMain.substring(0, descMain.length()-1)+
//				((StringUtils.isEmpty(descBak))?"":("<br/>"+"备用："+descBak.substring(0, descBak.length()-1)));
				for (int index = 0; index < descMap.size(); index++) {
			          if (index == 0){
			        	  desc = (String)descMap.get(Integer.valueOf(index));
			            }else {
			            desc = desc + "," + (String)descMap.get(Integer.valueOf(index));
			          }
			        }
				logger.info("----------desc:"+desc);
				attempCuidMap.put("desc", desc);
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateAttempTraphInfo", attempCuidMap);
			}
			
			//自动计算上端站端口与设备
			if(SysProperty.getInstance().getValue("districtName").trim().equals("黑龙江")){
				String taskLabelCn = null;
				Map map = new HashMap();
				map.put("serviceCuid", serviceCuid);
				List<Map<String,Object>> taskList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getTaskByServiceCuid", map);
				if(taskList != null && taskList.size() > 0){
					taskLabelCn = IbatisDAOHelper.getStringValue(taskList.get(0), "LABEL_CN");
				}
				if(!"多级设计".equals(taskLabelCn)){
					TraphMaintainBO.CountRelatedUpByTraph(serviceCuid);
				}
			}
		}
	}
	/**
	 * 无草稿信息时加载Ip和Vlan
	 */
//	public List<Map<String,Object>> getOuterIpAndVlan(String traphcuid){
//		List<Map<String,Object>> ipVlanList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(this.sqlMap+".getOuterIpAndVlanByCuid", traphcuid);
//		return ipVlanList;
//	}
	/**
	 * 查询草稿表和存量的数据，补充到list里面
	 * @param list
	 * @return
	 */
	public List<PathDesignMode> findDgnSegDetails(List<PathDesignMode> list) {
		//重组参数，为了查询草稿表数据和调度中的表的数据
		List<String> segCuidList = new ArrayList<String>();
		List<String> traphCuidList = new ArrayList<String>();
		PathPoint apoint = null,zpoint = null;
		for(PathDesignMode pdm :list){
			segCuidList.add(pdm.getRelatedDgnSegCuid());
			traphCuidList.add(pdm.getService().getCuid());
			if(apoint == null) apoint = pdm.getApoint();
			if(zpoint == null) zpoint = pdm.getZpoint();
		}
		//查询草稿表数据
		Map param = new HashMap();
		param.put("segCuidList", segCuidList);
		List<Map> segDetailList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectSegDetailForPage", param);
		List<Map> segDetailResList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectSegDetailResForPage", param);
		Map<String,Map<String,Object>> detailResMap = new HashMap<String, Map<String,Object>>();
		for(Map resMap :segDetailResList){
			String segDetailCuid = IbatisDAOHelper.getStringValue(resMap, "RELATED_SEG_DETAIL_CUID");
			Map<String, Object> resData = detailResMap.get(segDetailCuid);
			if(resData==null){
				resData = new HashMap<String, Object>();
				detailResMap.put(segDetailCuid, resData);
			}
			String resType = IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_TYPE");
			String type = IbatisDAOHelper.getStringValue(resMap, "TYPE");
			if("T_LOGIC_NUMBER_IP".equals(resType)){
				if("BUSINESS".equals(type)){
					resData.put("BUSINESS_IP_CUID", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_CUID"));
					resData.put("BUSINESS_IP_NAME", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_NAME"));
				}else if("NETCONFIG".equals(type)){
					resData.put("NETCONFIG_IP_CUID", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_CUID"));
					resData.put("NETCONFIG_IP_NAME", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_NAME"));
				}
			}else if("T_STATIC_ROUTE".equals(resType)){
				if("MAIN".equals(type)){
					resData.put("MAIN_ROUTE_CUID", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_CUID"));
					resData.put("MAIN_ROUTE_NAME", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_NAME"));
				}else if("BACK".equals(type)){
					resData.put("BACK_ROUTE_CUID", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_CUID"));
					resData.put("BACK_ROUTE_NAME", IbatisDAOHelper.getStringValue(resMap, "RELATED_RES_NAME"));
				}
			}
		}
		
		for(Map segDetail :segDetailList){
			String segDegtailCuid = IbatisDAOHelper.getStringValue(segDetail, "CUID");
			if(StringUtils.isNotBlank(segDegtailCuid)){
				String traphCuid = IbatisDAOHelper.getStringValue(segDetail, "RELATED_SERVICE_CUID");
				traphCuidList.remove(traphCuid);
			}
		}
		List<Map> attempList = new ArrayList<Map>();
		if(!traphCuidList.isEmpty()){
			Map pm = new HashMap();
			pm.put("traphCuidList", traphCuidList);
			String aType =apoint.getType();
			String aValue = apoint.getValue();
			String zType = zpoint.getType();
			String zValue = zpoint.getValue();
			if(StringUtils.isNotEmpty(aType)&&StringUtils.isNotEmpty(zType)
						&&StringUtils.isNotEmpty(aValue)&&StringUtils.isNotEmpty(zValue)){
				if(PathPoint.TYPE_SITE.equalsIgnoreCase(aType)){
					pm.put("ORIG_SITE_CUID", aValue);
				}else if(PathPoint.TYPE_ACCESSPOINT.equalsIgnoreCase(aType)){
					pm.put("ORIG_SITE_CUID", aValue);
				}else if(PathPoint.TYPE_ROOM.equalsIgnoreCase(aType)){
					pm.put("ORIG_ROOM_CUID", aValue);
				}else if(PathPoint.TYPE_TRANS_ELEMENT.equalsIgnoreCase(aType)){
					pm.put("ORIG_EQU_CUID", aValue);
				}
				if(PathPoint.TYPE_SITE.equalsIgnoreCase(zType)){
					pm.put("DEST_SITE_CUID", zValue);
				}else if(PathPoint.TYPE_ACCESSPOINT.equalsIgnoreCase(zType)){
					pm.put("DEST_SITE_CUID", zValue);
				}else if(PathPoint.TYPE_ROOM.equalsIgnoreCase(zType)){
					pm.put("DEST_ROOM_CUID", zValue);
				}else if(PathPoint.TYPE_TRANS_ELEMENT.equalsIgnoreCase(zType)){
					pm.put("DEST_EQU_CUID", zValue);
				}
				pm.put("ORIG_POINT_CUID", aValue);
				pm.put("DEST_POINT_CUID", zValue);
				
				List<Map> traphPathsList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".selectTraphPathsForPage", pm);
				attempList.addAll(traphPathsList) ;
				List<String> otnCuidList = new ArrayList<String>();
				for (Map map : traphPathsList) {
					String code = IbatisDAOHelper.getStringValue(map, "CODE");
					if (PathDesignConstants.MODE_CODE_OTN.equals(code)) {
						otnCuidList.add(IbatisDAOHelper.getStringValue(map, "RELATED_TRANS_PATH_CUID"));
					}
				}
				Map<String,Map<String,Object>> routeSegMap = new HashMap<String, Map<String,Object>>();
				if(!otnCuidList.isEmpty()){
					pm.put("traphPathsList", otnCuidList);
					List<Map> routeList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectOtnPathRouteSegForPage", pm);
					for (Map map : routeList) {
						String otnCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PATH_CUID");
						Map routeData = routeSegMap.get(otnCuid);
						if (routeData == null) {
							routeData = new HashMap();
							routeSegMap.put(otnCuid,routeData);
						}
						routeData.put("A_NE_CUID2", IbatisDAOHelper.getStringValue(map, "A_NE_CUID2"));
						routeData.put("A_PTP_CUID2", IbatisDAOHelper.getStringValue(map, "A_PTP_CUID2"));
						routeData.put("Z_NE_CUID2", IbatisDAOHelper.getStringValue(map, "Z_NE_CUID2"));
						routeData.put("Z_PTP_CUID2", IbatisDAOHelper.getStringValue(map, "Z_PTP_CUID2"));
						routeData.put("A_CTP_NAME2", IbatisDAOHelper.getStringValue(map, "A_CTP_NAME2"));
						routeData.put("A_NE_NAME2", IbatisDAOHelper.getStringValue(map, "A_NE_NAME2"));
						routeData.put("Z_NE_NAME2", IbatisDAOHelper.getStringValue(map, "Z_NE_NAME2"));
						routeData.put("Z_CTP_NAME2", IbatisDAOHelper.getStringValue(map, "Z_CTP_NAME2"));
						routeData.put("A_PTP_NAME2", IbatisDAOHelper.getStringValue(map, "A_PTP_NAME2"));
						routeData.put("Z_PTP_NAME2", IbatisDAOHelper.getStringValue(map, "Z_PTP_NAME2"));
					}
					for(Map map:traphPathsList){
						String otnCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRANS_PATH_CUID");
						if(routeSegMap.containsKey(otnCuid)){
							map.putAll(routeSegMap.get(otnCuid));
						}
					}
				}
				attempList.addAll(IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".selectTextPathsForPage", pm)) ;
				attempList.addAll(IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".selectSelfPathsForPage", pm)) ;
				attempList.addAll(IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".selectMsapPathsForPage", pm)) ;
				
				List<Map> ptnList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".selectPtnPathsForPage", pm);
				attempList.addAll(ptnList);
				List<String> lte3PtnCuidList = new ArrayList<String>();
				for(Map map:ptnList){
					String code = IbatisDAOHelper.getStringValue(map, "CODE");
					if(PathDesignConstants.MODE_CODE_PTN_LTE3.equals(code)){
						lte3PtnCuidList.add(IbatisDAOHelper.getStringValue(map, "PATH_CUID"));
					}
				}
				Map<String,Map<String,Object>> attempResMap = new HashMap<String, Map<String,Object>>();
				if(!lte3PtnCuidList.isEmpty()){
					pm.put("ptnCuidList", lte3PtnCuidList);
					List<Map> ipList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPtn2IPResForPage", pm);
					List<Map> routeList = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectPtn2SRouteResForPage", pm);
					for(Map map:ipList){
						String ptnCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PTN_PATH_CUID");
						Map resData = attempResMap.get(ptnCuid);
						if(resData == null){
							resData = new HashMap();
							attempResMap.put(ptnCuid, resData);
						}
						String type = IbatisDAOHelper.getStringValue(map, "TYPE");
						if("BUSINESS".equals(type)){
							resData.put("BUSINESS_IP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_NUMBER_IP_CUID"));
							resData.put("BUSINESS_IP_NAME", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
						}else if("NETCONFIG".equals(type)){
							resData.put("NETCONFIG_IP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_NUMBER_IP_CUID"));
							resData.put("NETCONFIG_IP_NAME", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
						}
					}
					for(Map map:routeList){
						String ptnCuid = IbatisDAOHelper.getStringValue(map, "RELATED_PTN_PATH_CUID");
						Map resData = attempResMap.get(ptnCuid);
						if(resData == null){
							resData = new HashMap();
							attempResMap.put(ptnCuid, resData);
						}
						String type = IbatisDAOHelper.getStringValue(map, "TYPE");
						if("MAIN".equals(type)){
							resData.put("MAIN_ROUTE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_STATIC_ROUTE_CUID"));
							resData.put("MAIN_ROUTE_NAME", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
						}else if("BACK".equals(type)){
							resData.put("BACK_ROUTE_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_STATIC_ROUTE_CUID"));
							resData.put("BACK_ROUTE_NAME", IbatisDAOHelper.getStringValue(map, "LABEL_CN"));
						}
					}
					for(Map map:ptnList){
						String pathCuid = IbatisDAOHelper.getStringValue(map, "PATH_CUID");
						if(attempResMap.containsKey(pathCuid)){
							map.putAll(attempResMap.get(pathCuid));
						}
					}
				}
			}
		}
		boolean isExistSegDetail = false;
		for(PathDesignMode pdm :list){
			isExistSegDetail = false;
			String hasparentcuid="0";
			String segCuid =  pdm.getRelatedDgnSegCuid();
			for(Map segDetail : segDetailList){
				String relatedSegCuid = IbatisDAOHelper.getStringValue(segDetail, "RELATED_DGN_SEG_CUID");
				String segDegtailCuid = IbatisDAOHelper.getStringValue(segDetail, "CUID");
				String parentcuid = IbatisDAOHelper.getStringValue(segDetail, "PARENT_DGN_SEG_CUID");
				if(segCuid.equals(relatedSegCuid)){
					//补充右侧显示的通用字段
					pdm.setCode(IbatisDAOHelper.getStringValue(segDetail, "CODE"));
					pdm.getService().set("TRAPH_RATE",IbatisDAOHelper.getStringValue(segDetail, "TRAPH_RATE"));
					pdm.getService().set("BANDWIDTH",IbatisDAOHelper.getStringValue(segDetail, "BANDWIDTH"));
					if(StringUtils.isNotEmpty(parentcuid)){
						//1表示是MSTP路由设计
						 hasparentcuid = "1";
					}
					pdm.getService().set("PARENT_DGN_SEG_CUID",hasparentcuid);
					if(StringUtils.isNotBlank(segDegtailCuid)){
						isExistSegDetail = true;
						pdm.setData(segDetail);
						if(detailResMap.containsKey(segDegtailCuid)){
							pdm.getData().putAll(detailResMap.get(segDegtailCuid)); 
						}
					}
					break;
				}
			}
			String traphCuid = pdm.getService().getCuid();
			if(!isExistSegDetail){
				for(Map attempData : attempList){
					//每次取到数据，会把该字段更新进去,标记为已经被谁使用过，循环的时候排除掉
					String usedSegCuid = IbatisDAOHelper.getStringValue(attempData, "RELATED_DGN_SEG_CUID");
					if(StringUtils.isBlank(usedSegCuid)){
						String attempTraphCuid = IbatisDAOHelper.getStringValue(attempData, "RELATED_SERVICE_CUID");
						if(traphCuid.equals(attempTraphCuid)){
							attempData.put("RELATED_DGN_SEG_CUID", segCuid);
							pdm.setCode(IbatisDAOHelper.getStringValue(attempData, "CODE"));
							pdm.setData(attempData);
							break;
						}
					}
				}
			}
		}
		return list;
	}
	/**
	 * 端口恢复(ptn,port)
	 * @param param
	 *   code(ptn/port)
	 *   traphCuidList
	 *   siteCuid
	 * @return
	 */
	public List<Map> portReset(Map param) {
		String code = IbatisDAOHelper.getStringValue(param, "code");
		Map point = (Map)param.get("point");
		String value = IbatisDAOHelper.getStringValue(point, "value");
		List<Map> list = new ArrayList<Map>();
		List<String> traphCuidList = (List<String>)param.get("traphCuidList");
		param.put("serviceCuidList",traphCuidList);
		if("port".equals(code)){
			String type = IbatisDAOHelper.getStringValue(point, "type");
			if(PathPoint.TYPE_SITE.equalsIgnoreCase(type)){
				param.put("siteCuid", value);
			}else if(PathPoint.TYPE_ROOM.equalsIgnoreCase(type)){
				param.put("roomCuid", value);
			}else if(PathPoint.TYPE_TRANS_ELEMENT.equalsIgnoreCase(type)){
				param.put("neCuid", value);
			}
			list = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".portReset", param);
		}else if("ptn".equals(code)){
			param.put("pointCuid", value);
			list = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".ptnPortReset", param);
		}
		param.put("zdSiteCuid", value);
		List<Map> listByTraph = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".portResetByTraph", param);
		List<Map> attempList = new ArrayList<Map>();
		for (Map map : list) {
			Map dataMap = new HashMap();
			String ptpName = IbatisDAOHelper.getStringValue(map,"PTP_NAME");
			String ctpName = IbatisDAOHelper.getStringValue(map,"NAME");
			if(StringUtils.isBlank(ctpName)){
				ctpName = ptpName;
				dataMap.put("CUID", IbatisDAOHelper.getStringValue(map,"PTP_CUID"));
				dataMap.put("NAME", ctpName);
				dataMap.put("FULL_NAME", ctpName);
				dataMap.put("BM_CLASS_ID", "PTP");
				dataMap.put("RATE", IbatisDAOHelper.getStringValue(map,"PTP_RATE"));
			}else {
				dataMap.put("CUID", IbatisDAOHelper.getStringValue(map,"VALUE"));
				dataMap.put("NAME", ctpName);
				dataMap.put("FULL_NAME", ptpName+"."+ctpName);
				dataMap.put("BM_CLASS_ID", "CTP");
				dataMap.put("RATE", IbatisDAOHelper.getStringValue(map,"CTP_RATE"));
			}
			dataMap.put("RELATED_SERVICE_CUID", IbatisDAOHelper.getStringValue(map,"RELATED_SERVICE_CUID"));
			if(StringUtils.isNotBlank(IbatisDAOHelper.getStringValue(map,"VALUE"))) {
				attempList.add(dataMap);
			}
		}
		for (Map map : listByTraph) {
			Map dataMap = new HashMap();
			String ctpName = IbatisDAOHelper.getStringValue(map,"NAME");
			String ptpName = IbatisDAOHelper.getStringValue(map, "PTP_NAME");
			dataMap.put("CUID", IbatisDAOHelper.getStringValue(map,"VALUE"));
			dataMap.put("NAME",ctpName);
			dataMap.put("FULL_NAME",ptpName+"."+ctpName);
			dataMap.put("BM_CLASS_ID", "CTP");
			dataMap.put("RATE", IbatisDAOHelper.getStringValue(map,"CTP_RATE"));
			dataMap.put("RELATED_SERVICE_CUID", IbatisDAOHelper.getStringValue(map,"RELATED_SERVICE_CUID"));
			if(StringUtils.isNotBlank(ctpName)){
				attempList.add(dataMap);
			}
		}
		//排序
		List<Map> resultList = new ArrayList<Map>();
		for(String traphCuid:traphCuidList){
			Iterator<Map> iter = attempList.iterator();
			while(iter.hasNext()){
				Map map = iter.next();
				if(traphCuid.equals(IbatisDAOHelper.getStringValue(map, "RELATED_SERVICE_CUID"))){
					resultList.add(map);
					iter.remove();
				}
			}
		}
		return resultList;
	}
	/**
	 * 修改路由属性
	 * 修改子节点的数量(1，oldNum = newNum 不变；2，oldNum > newNum ,减少；3，oldNum < newNum，增加)
	 * 	  (查询逻辑要把这些数据补充进去)
	 * @param param
	 */
	public void modifyRouteAttributes(Map param) {
		//电路类型为“传输电路”时，路由数量必须为0；当电路类型为“MSTP电路”时，路由数量不允许为0
		int oldNum = IbatisDAOHelper.getIntValue(param, "OLD_NUM");
		int newNum = IbatisDAOHelper.getIntValue(param, "NEW_NUM");
		if(SysProperty.getInstance().getValue("districtName").trim().equals("广西")){
			String traphType = IbatisDAOHelper.getStringValue(param, "TRAPH_TYPE");
			if(traphType.equals("3")&& newNum == 0){
				throw new RuntimeException("该电路的电路类型为“MSTP电路”，路由数量不允许为0");
			}
			if(!traphType.equals("3")&& newNum != 0){
				throw new RuntimeException("该电路的电路类型为“传输电路”，路由数量必须为0");
			}
		}
		
		String segCuid = IbatisDAOHelper.getStringValue(param, "RELATED_DGN_SEG_CUID");
		if(newNum>300){
			throw new RuntimeException("节点总数量不能大于300！");
		}
		Record record = new Record("T_ATTEMP_DGN_SEG");
		record.addColValue("AVLANID", IbatisDAOHelper.getStringValue(param, "AVLANID"));
		record.addColValue("ZVLANID", IbatisDAOHelper.getStringValue(param, "ZVLANID"));
		record.addColValue("WORK_MODE", IbatisDAOHelper.getStringValue(param, "WORK_MODE"));
		record.addColValue("MSTP_EXTS", IbatisDAOHelper.getStringValue(param, "MSTP_EXTS"));
		record.addColValue("A_MAC", IbatisDAOHelper.getStringValue(param, "A_MAC"));
		record.addColValue("A_MP", IbatisDAOHelper.getStringValue(param, "A_MP"));
		record.addColValue("Z_MAC", IbatisDAOHelper.getStringValue(param, "Z_MAC"));
		record.addColValue("Z_MP", IbatisDAOHelper.getStringValue(param, "Z_MP"));
		
		Record pkRecord = new Record("T_ATTEMP_DGN_SEG");
		pkRecord.addColValue("CUID", segCuid);
		Map pm = new HashMap();
		this.IbatisResDAO.updateDynamicTable(record, pkRecord);
		int num = oldNum - newNum;
		if(num>0){//要删除多余的,如果旧的有5个子节点， newNum 是3，则删除PARENT_INDEX_NO >2 的
			pm.put("segCuid", segCuid);
			pm.put("newNum", newNum-1);
			List<Map<String, Object>> dgnSegList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectDgnSegChild", pm);
			List<PathDesignMode> modeList = new ArrayList<PathDesignMode>();
			for(Map<String, Object> dgnSeg : dgnSegList) {
				PathDesignMode mode = new PathDesignMode();
				mode.setRelatedDgnSegCuid(IbatisDAOHelper.getStringValue(dgnSeg, "CUID"));
				modeList.add(mode);
			}
			this.deleteDgnSegDetail(modeList);
			this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteDgnSegChild", pm);
		}else if(num<0){//要增加绝对值 num
			List<Integer> parIndexNoList = new ArrayList<Integer>();
			for(int i=0;i<Math.abs(num);i++){
				parIndexNoList.add(i);
			}
			pm.put("segCuid", segCuid);
			pm.put("intList", parIndexNoList);
			this.IbatisResDAO.getSqlMapClientTemplate().insert(sqlMap+".insertDgnSegChild", pm);
		}
		//更新父节点路由设计的状态segCuids
		if(num!=0){
			List<String> segCuids = new ArrayList<String>();
			segCuids.add(segCuid);
			pm.put("segCuids",segCuids);
			this.IbatisResDAO.getSqlMapClientTemplate().update("AbstractPathDesign.updateDgnSegIsFinish", pm);
		}
	}
	public Map<String, List> getDdfInfo(Map param) {
		//翻译时隙
		List<Map> adata = (List<Map>) param.get("adata");
		List<Map> zdata = (List<Map>) param.get("zdata");
		
		List<String> ptpNameList = new ArrayList<String>();
		List<String> ctpCuidList = new ArrayList<String>();
		for(Map map:adata){
			if("CTP".equals(IbatisDAOHelper.getStringValue(map, "BM_CLASS_ID"))){
				ctpCuidList.add(IbatisDAOHelper.getStringValue(map, "CUID"));
			}else if("PTP".equals(IbatisDAOHelper.getStringValue(map, "BM_CLASS_ID"))){
				ptpNameList.add(IbatisDAOHelper.getStringValue(map, "NAME"));
				map.put("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(map, "CUID"));
				map.put("RELATED_PTP_NAME", IbatisDAOHelper.getStringValue(map, "NAME"));
			}
		}
		for(Map map:zdata){
			if("CTP".equals(IbatisDAOHelper.getStringValue(map, "BM_CLASS_ID"))){
				ctpCuidList.add(IbatisDAOHelper.getStringValue(map, "CUID"));
			}else if("PTP".equals(IbatisDAOHelper.getStringValue(map, "BM_CLASS_ID"))){
				ptpNameList.add(IbatisDAOHelper.getStringValue(map, "NAME"));
				map.put("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(map, "CUID"));
				map.put("RELATED_PTP_NAME", IbatisDAOHelper.getStringValue(map, "NAME"));
			}
		}
		Map pm = new HashMap();
		if(!ctpCuidList.isEmpty()){
			pm.put("ctpCuidList", ctpCuidList);
			List<Map> ptpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPtpByCtpCuidList", pm);
			for(Map map:ptpList){
				//时隙的CUID
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				ptpNameList.add(IbatisDAOHelper.getStringValue(map,"RELATED_PTP_NAME"));
				for(Map m:adata){
					if(cuid.equals(IbatisDAOHelper.getStringValue(m, "CUID"))){
						m.put("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_CUID"));
						m.put("RELATED_PTP_NAME", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME"));
					}
				}
				for(Map m:zdata){
					if(cuid.equals(IbatisDAOHelper.getStringValue(m, "CUID"))){
						m.put("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_CUID"));
						m.put("RELATED_PTP_NAME", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME"));
					}
				}
			}
		}
		pm.clear();
		pm.put("ptpNameList", ptpNameList);
		List<Map<String, Object>> dfPort = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPointInfoByPtp", pm) ;
		List<Map<String, Object>> ptpDfPort = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPointInfoByPtpName", pm) ;
		List<String> addfs = new ArrayList<String>();
		List<String> zddfs = new ArrayList<String>();
		for(Map map:adata){
			String ptpName = IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME");
			String ddfInfo = ""; //有值没有值，都要加进去
			for(Map m:dfPort){
				if(ptpName.equals(IbatisDAOHelper.getStringValue(m, "LABEL_CN"))){
					ddfInfo = IbatisDAOHelper.getStringValue(m, "DFPORT");
					break;
				}
			}
			if(StringUtils.isBlank(ddfInfo)){
				for(Map m:ptpDfPort){
					if(ptpName.equals(IbatisDAOHelper.getStringValue(m, "LABEL_CN"))){
						ddfInfo = IbatisDAOHelper.getStringValue(m, "DFPORT");
						break;
					}
				}
			}
			addfs.add(ddfInfo);
		}
		for(Map map:zdata){
			String ptpName = IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME");
			String ddfInfo = "";//有值没有值，都要加进去
			for(Map m:dfPort){
				if(ptpName.equals(IbatisDAOHelper.getStringValue(m, "LABEL_CN"))){
					ddfInfo = IbatisDAOHelper.getStringValue(m, "DFPORT");
					break;
				}
			}
			if(StringUtils.isBlank(ddfInfo)){
				for(Map m:ptpDfPort){
					if(ptpName.equals(IbatisDAOHelper.getStringValue(m, "LABEL_CN"))){
						ddfInfo = IbatisDAOHelper.getStringValue(m, "DFPORT");
						break;
					}
				}
			}
			zddfs.add(ddfInfo);
		}
		Map<String,List> result = new HashMap<String, List>();
		result.put("adata", adata);
		result.put("zdata", zdata);
		result.put("addfs", addfs);
		result.put("zddfs", zddfs);
		return result;
	}
	/**
	 * 适应新组件的还原ddf信息
	 * @param datas
	 * @return
	 */
	public List<Map> findDdfInfo(List<Map> datas){
		List<String> ptpNameList = new ArrayList<String>();
		List<String> ctpCuidList = new ArrayList<String>();
		for(Map map:datas){
			String aSideBmClassId = IbatisDAOHelper.getStringValue(map, "A_SIDE_BM_CLASS_ID");
			String aSideCuid = IbatisDAOHelper.getStringValue(map, "A_SIDE_CUID");
			String aSideName = IbatisDAOHelper.getStringValue(map, "A_SIDE_NAME");
			if("PTP".equals(aSideBmClassId)){
				map.put("A_PTP_CUID", aSideCuid);
				map.put("A_PTP_NAME", aSideName);
				ptpNameList.add(aSideName);
			}else{
				ctpCuidList.add(aSideCuid);
			}
			String zSideBmClassId = IbatisDAOHelper.getStringValue(map, "Z_SIDE_BM_CLASS_ID");
			String zSideCuid = IbatisDAOHelper.getStringValue(map, "Z_SIDE_CUID");
			String zSideName = IbatisDAOHelper.getStringValue(map, "Z_SIDE_NAME");
			if("PTP".equals(zSideBmClassId)){
				map.put("Z_PTP_CUID", zSideCuid);
				map.put("Z_PTP_NAME", zSideName);
				ptpNameList.add(zSideName);
			}else{
				ctpCuidList.add(zSideCuid);
			}
		}
		Map pm = new HashMap();
		if(!ctpCuidList.isEmpty()){
			pm.put("ctpCuidList", ctpCuidList);
			List<Map> ptpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPtpByCtpCuidList", pm);
			for(Map map:ptpList){
				//时隙的CUID
				String cuid = IbatisDAOHelper.getStringValue(map, "CUID");
				ptpNameList.add(IbatisDAOHelper.getStringValue(map,"RELATED_PTP_NAME"));
				for(Map m:datas){
					if(cuid.equals(IbatisDAOHelper.getStringValue(m, "A_SIDE_CUID"))){
						m.put("A_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_CUID"));
						m.put("A_PTP_NAME", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME"));
					}
					if(cuid.equals(IbatisDAOHelper.getStringValue(m, "Z_SIDE_CUID"))){
						m.put("Z_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_CUID"));
						m.put("Z_PTP_NAME", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME"));
					}
				}
			}
		}
		pm.clear();
		pm.put("ptpNameList", ptpNameList);
		List<Map<String, Object>> ptpDdfPort = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPointInfoByPtpName", pm) ;
		Map<String,String> ptpDdfMap = new HashMap<String, String>();
		for(Map m:ptpDdfPort){
			String ptp = IbatisDAOHelper.getStringValue(m, "LABEL_CN");
			String ddf = IbatisDAOHelper.getStringValue(m, "DFPORT");
			if(!ptpDdfMap.containsKey(ptp)&&StringUtils.isNotBlank(ddf)){
				ptpDdfMap.put(ptp, ddf);
			}
		}
		ptpNameList.removeAll(ptpDdfMap.keySet());
		if(!ptpNameList.isEmpty()){
			List<Map<String, Object>> ddfPort = IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPointInfoByPtp", pm) ;
			for(Map m:ddfPort){
				String ptp = IbatisDAOHelper.getStringValue(m, "LABEL_CN");
				String ddf = IbatisDAOHelper.getStringValue(m, "DFPORT");
				if(!ptpDdfMap.containsKey(ptp)&&StringUtils.isNotBlank(ddf)){
					ptpDdfMap.put(ptp, ddf);
				}
			}
		}
		for(Map map:datas){
			String aptp = IbatisDAOHelper.getStringValue(map, "A_PTP_NAME");
			map.put("A_DDF", ptpDdfMap.containsKey(aptp)?ptpDdfMap.get(aptp):"");
			String zptp = IbatisDAOHelper.getStringValue(map, "Z_PTP_NAME");
			map.put("Z_DDF", ptpDdfMap.containsKey(zptp)?ptpDdfMap.get(zptp):"");
		}
		return datas;
	}
	
	/**
	 * 更新ddf信息
	 * @param param
	 */
	public void updateDdfInfo(List<Map> param) {
		//删除
		List<String> ptpNameList = new ArrayList<String>();  
		List<Record> list = new ArrayList<Record>();
		for(Map map:param){
			String ptpName = IbatisDAOHelper.getStringValue(map, "RELATED_PTP_NAME");
			ptpNameList.add(ptpName);
			String ddf = IbatisDAOHelper.getStringValue(map, "DDF_INFO");
			if(StringUtils.isBlank(ddf)) continue;
			Record ddfR = new Record("T_PHY_PTP2DDF");
			ddfR.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_PHY_PTP2DDF"));
			ddfR.addColValue("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(map, "RELATED_PTP_CUID"));
			ddfR.addColValue("RELATED_PTP_NAME", ptpName);
			ddfR.addColValue("DDF_INFO",ddf);
			list.add(ddfR);
		}
		Map pm = new HashMap();
		pm.put("ptpNameList", ptpNameList);
		this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deletePort2Ddf", pm);
		this.IbatisResDAO.insertDynamicTableBatch(list);
	}
	
	/**
	 * 创建逻辑口
	 * @param logicCuid
	 * @param logicType(
	 * 	 C 表示 ctp
	 *   P 表示 ptp
	 * )
	 */
	public void createLogicPort(String logicCuid, String logicType) {
		List<Record> recordList = new ArrayList<Record>();
		
		Map pm = new HashMap();
		if("P".equalsIgnoreCase(logicType)){
			pm.put("ptpCuid", logicCuid);
			List<Map<String,Object>> ctps = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectCtpsByPtpCuid", pm);
			if(ctps.size()!=1){
				throw new RuntimeException("您所选择的端口时隙数量不唯一！");
			}
			if(9!=IbatisDAOHelper.getIntValue(ctps.get(0), "CTP_RATE")){
				throw new RuntimeException("速率不是155M！");
			}
			logicType = "C";
			logicCuid = IbatisDAOHelper.getStringValue(ctps.get(0), "CUID");
		}
		pm.clear();
		if("C".equalsIgnoreCase(logicType)) {
			String logicNamePre = "";
			int count = 0;
			int start = 0;
			int logicRate = 0;
			String ptpCuid = "";
			String ptpName = "";
			
			pm.put("ctpCuid", logicCuid);
			
			Map<String, Object> ctpMap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".queryCtp", pm);
			if(ctpMap == null || ctpMap.isEmpty()) {
				throw new RuntimeException("该ID【"+logicCuid+"】无法查询出时隙！");
			}
			
			String attempPathKey = "ATTEMP_PATH";
			String pathKey = "PATH";
			String attempTraphKey = "ATTEMP_TRAPH";
			String traphKey = "TRAPH";
			Map<String, Map<String, Set<String>>> ctp2PathMap = new HashMap<String, Map<String, Set<String>>>();
			Map<String, Map<String, String>> ctp2TraphMap = new HashMap<String, Map<String,String>>();
			
			pm.clear();
			pm.put("ctpCuid", logicCuid);
			List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTraphPathByCtp", pm);
			if(list != null && list.size() > 0) {
				for(Map<String, Object> map : list) {
					String relatedTraphCuid = StringUtils.stripToEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID"));
					String relatedPathCuid = StringUtils.stripToEmpty(IbatisDAOHelper.getStringValue(map, "RELATED_PATH_CUID"));
					String ctpCuid = IbatisDAOHelper.getStringValue(map, "CTP_CUID");
					
					Map<String, Set<String>> pathMap = ctp2PathMap.get(ctpCuid);
					if(pathMap == null) {
						pathMap = new HashMap<String, Set<String>>();
						ctp2PathMap.put(ctpCuid, pathMap);
					}
					
					Set<String> attempPathCuidSet = pathMap.get(attempPathKey);
					if(attempPathCuidSet == null) {
						attempPathCuidSet = new HashSet<String>();
						pathMap.put(attempPathKey, attempPathCuidSet);
					}
					
					Set<String> pathCuidSet = pathMap.get(pathKey);
					if(pathCuidSet == null) {
						pathCuidSet = new HashSet<String>();
						pathMap.put(pathKey, pathCuidSet);
					}
					
					if(relatedPathCuid.startsWith(attempPathKey)) {
						attempPathCuidSet.add(relatedPathCuid);
					} else if(relatedPathCuid.startsWith(pathKey)) {
						pathCuidSet.add(relatedPathCuid);
					}
					
					Map<String, String> traphMap = ctp2TraphMap.get(ctpCuid);
					if(traphMap == null) {
						traphMap = new HashMap<String, String>();
						ctp2TraphMap.put(ctpCuid, traphMap);
					}
					
					if(relatedTraphCuid.startsWith(attempTraphKey)) {
						traphMap.put(attempTraphKey, relatedTraphCuid);
					} else if(relatedTraphCuid.startsWith(traphKey)) {
						traphMap.put(traphKey, relatedTraphCuid);
					}
				}
			}
			
			String ctpCuid = IbatisDAOHelper.getStringValue(ctpMap, "CUID");
			String ctpRate = IbatisDAOHelper.getStringValue(ctpMap, "CTP_RATE");
			//判断时隙速率是否既不是155M(9)，又不是1.25G(15)
			if(!"9".equals(ctpRate) && !"15".equals(ctpRate)) {
				throw new RuntimeException("该时隙非155M和1.25G，不允许打散！");
			}
			
			Integer logicPortCount = IbatisDAOHelper.getIntValue(ctpMap, "LOGIC_PORT_COUNT");
			//判断该时隙是否已打散
			if(logicPortCount > 0) {
				throw new RuntimeException("该时隙已打散，不允许重复打散！");
			}
			
			String OldCtpName = IbatisDAOHelper.getStringValue(ctpMap, "LABEL_CN");
			ptpCuid = IbatisDAOHelper.getStringValue(ctpMap, "PTP_CUID");
			ptpName = IbatisDAOHelper.getStringValue(ctpMap, "PTP_NAME");
			
			//155M打散成63个2M，1.25G打散成8个155M
			if("9".equals(ctpRate)) {
				logicNamePre = OldCtpName + "/2M";
				count = 63;
				start = 0;
				logicRate = 1;
				
				Map<String, String> attAndTraphMap = ctp2TraphMap.get(ctpCuid);
				Map<String, Set<String>> pathMap = ctp2PathMap.get(ctpCuid);
				if(pathMap != null && pathMap.size() > 0) {
					String attempTraphCuid = attAndTraphMap.get(attempTraphKey);
					String traphCuid = attAndTraphMap.get(traphKey);
					
					Set<String> attempPathCuidSet = pathMap.get(attempPathKey);
					List<String> attempPathCuidList = new ArrayList<String>();
					attempPathCuidList.addAll(attempPathCuidSet);
					Set<String> pathCuidSet = pathMap.get(pathKey);
					List<String> pathCuidList = new ArrayList<String>();
					pathCuidList.addAll(pathCuidSet);
					
					int attempPathCount = attempPathCuidList.size();
					int pathCount = pathCuidList.size();
					
					if(attempPathCount > 0) {
						count = count - attempPathCount;
						
						for(int a=0; a<attempPathCount; a++) {
							String cuid = CUIDHexGenerator.getInstance().generate("TRANS_LOGIC_CTP");
							int sortNo = a+1;
							start++;
							String logicName = logicNamePre + sortNo;
							
							Record record = new Record("TRANS_LOGIC_CTP");
							record.addColValue("CUID", cuid);
							record.addColValue("LOGIC_CUID", logicCuid);
							record.addColValue("CTP_CUID", logicCuid);
							record.addColValue("CTP_NAME", logicName);
							record.addColValue("LOGIC_RATE", logicRate);
							record.addColValue("LOGIC_TYPE", logicType);
							record.addColValue("PTP_CUID", ptpCuid);
							record.addColValue("PTP_NAME", ptpName);
							record.addColValue("RELATED_ATTEMP_TRAPH_CUID", attempTraphCuid);
							record.addColValue("RELATED_TRAPH_CUID", traphCuid);
							record.addColValue("SORT_NO", sortNo);
							record.addColValue("GROUP_NO", sortNo);
							
							recordList.add(record);
						}
					}
					
					if(pathCount > 0) {
						count = count - pathCount;
						
						for(int a=0; a<pathCount; a++) {
							String cuid = CUIDHexGenerator.getInstance().generate("TRANS_LOGIC_CTP");
							int sortNo = a+1;
							start++;
							String logicName = logicNamePre + sortNo;
							
							Record record = new Record("TRANS_LOGIC_CTP");
							record.addColValue("CUID", cuid);
							record.addColValue("LOGIC_CUID", logicCuid);
							record.addColValue("CTP_CUID", logicCuid);
							record.addColValue("CTP_NAME", logicName);
							record.addColValue("LOGIC_RATE", logicRate);
							record.addColValue("LOGIC_TYPE", logicType);
							record.addColValue("PTP_CUID", ptpCuid);
							record.addColValue("PTP_NAME", ptpName);
							record.addColValue("RELATED_ATTEMP_TRAPH_CUID", attempTraphCuid);
							record.addColValue("RELATED_TRAPH_CUID", traphCuid);
							record.addColValue("SORT_NO", sortNo);
							record.addColValue("GROUP_NO", sortNo);
							
							recordList.add(record);
						}
					}
				}
				
			} else if("15".equals(ctpRate)) {
				logicNamePre = "155M";
				count = 8;
				start = 0;
				logicRate = 9;
			}
			
			for(int i=start; i<count; i++) {
				String cuid = CUIDHexGenerator.getInstance().generate("TRANS_LOGIC_CTP");
				int sortNo = i+1;
				String logicName = logicNamePre + sortNo;
				
				Record record = new Record("TRANS_LOGIC_CTP");
				record.addColValue("CUID", cuid);
				record.addColValue("LOGIC_CUID", logicCuid);
				record.addColValue("CTP_CUID", logicCuid);
				record.addColValue("CTP_NAME", logicName);
				record.addColValue("LOGIC_TYPE", logicType);
				record.addColValue("LOGIC_RATE", logicRate);
				record.addColValue("PTP_CUID", ptpCuid);
				record.addColValue("PTP_NAME", ptpName);
				record.addColValue("SORT_NO", sortNo);
				record.addColValue("GROUP_NO", sortNo);
				
				recordList.add(record);
			}
		} else if("G".equalsIgnoreCase(logicType)) {
		}
		
		this.IbatisResDAO.insertDynamicTableBatch(recordList);
	}
	
	/**
	 * 删除逻辑口
	 * @param logicCuid
	 */
	public void deleteLogicPort(String logicCuid) {
		Map pm = new HashMap();
		pm.put("logicCuid", logicCuid);
		
		List<Map<String, Object>> logicPortList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryLogicPort", pm);
		if(logicPortList == null || logicPortList.isEmpty()) {
			throw new RuntimeException("该时隙或高转通道未打散，无法撤销！");
		}
		
		boolean isExistsTraph = false;
		for(Map<String, Object> map : logicPortList) {
			String relatedAttempTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_ATTEMP_TRAPH_CUID");
			String relatedTraphCuid = IbatisDAOHelper.getStringValue(map, "RELATED_TRAPH_CUID");
			
			if(StringUtils.isNotBlank(relatedAttempTraphCuid) || StringUtils.isNotBlank(relatedTraphCuid)) {
				isExistsTraph = true;
				break;
			}
		}
		
		if(isExistsTraph) {
			throw new RuntimeException("该时隙或高转通道被电路占用，请先释放！");
		}
		
		String delSql = "DELETE TRANS_LOGIC_CTP WHERE LOGIC_CUID='%s'";
		this.IbatisResDAO.deleteSql(String.format(delSql, logicCuid));
	}
	/**
	 * 高转打散
	 * @param gzCuid
	 */
	public void splitGzTransPath(String gzCuid) {
		if(StringUtils.isBlank(gzCuid)) throw new RuntimeException("高转通道CUID不能为空");
		Map pm = new HashMap();
		pm.put("cuid", gzCuid);
		Map<String,Object> gzmap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getGzTransPath",pm);
		int pathNum = IbatisDAOHelper.getIntValue(gzmap, "PATHNUM");
		if(pathNum>0){
			throw new RuntimeException("该高转通道已打散,请勿重复打散！");
		}
		String origCtpRate = IbatisDAOHelper.getStringValue(gzmap, "ORIG_CTP_RATE");
		String destCtpRate = IbatisDAOHelper.getStringValue(gzmap, "DEST_CTP_RATE");
		if(StringUtils.isBlank(origCtpRate)||StringUtils.isBlank(destCtpRate)){
			throw new RuntimeException("A端时隙或者Z端时隙不能为空！");
		}
		if(!origCtpRate.equals(destCtpRate)){
			throw new RuntimeException("A端时隙与Z端时隙不相等！");
		}
		if(!"9".equals(origCtpRate)&&!"15".equals(origCtpRate)){
			throw new RuntimeException("时隙不是155M或者1.25G！");
		}
		String preName = null;
		int count = 0;
		int logicRate = 0;
		if("9".equals(origCtpRate)){
			preName = "/2M";
			count = 63;
			logicRate = 1;
		}else if("15".equals(origCtpRate)){
			preName = "/155M";
			count = 8;
			logicRate = 1;
		}
		List<Record> transPathList = new ArrayList<Record>();
		List<Record> transSubPathList = new ArrayList<Record>();
		List<Record> ctp2TransPathList = new ArrayList<Record>();
		String oorigCtpName = IbatisDAOHelper.getStringValue(gzmap, "ORIG_CTP_NAME");
		String odestCtpName = IbatisDAOHelper.getStringValue(gzmap, "DEST_CTP_NAME");
		String origPtpName = IbatisDAOHelper.getStringValue(gzmap, "ORIG_PTP_NAME");
		String destPtpName = IbatisDAOHelper.getStringValue(gzmap, "DEST_PTP_NAME");
		String origSiteName = IbatisDAOHelper.getStringValue(gzmap, "ORIG_SITE_NAME");
		String destSiteName = IbatisDAOHelper.getStringValue(gzmap, "DEST_SITE_NAME");
		for(int i=1;i<=count;i++){
			Record transPathRecord = new Record("TRANS_PATH");
			Record transSubPathRecord = new Record("TRANS_SUB_PATH");
			Record ctp2TransPathRecord1 = new Record("CTP_TO_TRANSPATH");
			Record ctp2TransPathRecord2 = new Record("CTP_TO_TRANSPATH");
			
			String norigCtpName = oorigCtpName+preName+i;
			String ndestCtpName = odestCtpName+preName+i;
			String descriptionA = origSiteName+"("+origPtpName+"/"+norigCtpName+")("+destPtpName+"/"+ndestCtpName+")"+destSiteName;
			String descriptionZ = destSiteName+"("+destPtpName+"/"+ndestCtpName+")("+origPtpName+"/"+norigCtpName+")"+origSiteName;
			
			String transPathCuid = CUIDHexGenerator.getInstance().generate("TRANS_PATH");
			Date now = new Date();
			transPathRecord.addColValue("CUID", transPathCuid);
			transPathRecord.addColSqlValue("OBJECTID", transPathRecord.getObjectIdSql());
			transPathRecord.addColValue("RELATED_GZ_TRANS_PATH_CUID", gzCuid);
			transPathRecord.addColValue("LABEL_CN", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION_A", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION_Z", descriptionZ);
			transPathRecord.addColValue("RELATED_A_DISTRICT_CUID",IbatisDAOHelper.getStringValue(gzmap, "RELATED_A_DISTRICT_CUID"));
			transPathRecord.addColValue("RELATED_Z_DISTRICT_CUID",IbatisDAOHelper.getStringValue(gzmap, "RELATED_Z_DISTRICT_CUID"));
			transPathRecord.addColValue("ORIG_ROOM_CUID",IbatisDAOHelper.getStringValue(gzmap, "ORIG_ROOM_CUID"));
			transPathRecord.addColValue("DEST_ROOM_CUID",IbatisDAOHelper.getStringValue(gzmap, "DEST_ROOM_CUID"));
			transPathRecord.addColValue("ORIG_CARD_CUID",IbatisDAOHelper.getStringValue(gzmap, "ORIG_CARD_CUID"));
			transPathRecord.addColValue("DEST_CARD_CUID",IbatisDAOHelper.getStringValue(gzmap, "DEST_CARD_CUID"));
			transPathRecord.addColValue("ORIG_SITE_CUID",IbatisDAOHelper.getStringValue(gzmap, "ORIG_SITE_CUID"));
			transPathRecord.addColValue("DEST_SITE_CUID",IbatisDAOHelper.getStringValue(gzmap, "DEST_SITE_CUID"));
			transPathRecord.addColValue("ORIG_EQU_CUID",IbatisDAOHelper.getStringValue(gzmap, "ORIG_EQU_CUID"));
			transPathRecord.addColValue("DEST_EQU_CUID",IbatisDAOHelper.getStringValue(gzmap, "DEST_EQU_CUID"));
			transPathRecord.addColValue("RELATED_A_END_PTP", IbatisDAOHelper.getStringValue(gzmap, "ORIG_PTP_CUID"));
			transPathRecord.addColValue("RELATED_Z_END_PTP", IbatisDAOHelper.getStringValue(gzmap, "DEST_PTP_CUID"));
			transPathRecord.addColValue("ORIG_POINT_CUID", IbatisDAOHelper.getStringValue(gzmap, "ORIG_CTP_CUID"));
			transPathRecord.addColValue("DEST_POINT_CUID", IbatisDAOHelper.getStringValue(gzmap, "DEST_CTP_CUID"));
			
			transPathRecord.addColValue("ORIG_CTP_NAME", norigCtpName);
			transPathRecord.addColValue("DEST_CTP_NAME", ndestCtpName);
			
			transPathRecord.addColValue("PATH_STATE", 1);// 空闲
			transPathRecord.addColValue("CREATE_TYPE", 1);// 预配
			transPathRecord.addColValue("IS_EQUAL_Z", 1);// Z端口速率是否与通道速率相等
			transPathRecord.addColValue("IS_EQUAL_A", 1);// A端口速率是否与通道速率相等
			transPathRecord.addColValue("PATH_REST_VOL", 0);// 空闲速率
			transPathRecord.addColValue("CREATE_TIME", now);
			transPathRecord.addColValue("CH_RATE", logicRate);
			
			// 创建子通道
			transSubPathRecord.getValues().putAll(transPathRecord.getValues());
			String subPathCuid = CUIDHexGenerator.getInstance().generate("TRANS_SUB_PATH");
			transSubPathRecord.addColValue("CUID", subPathCuid);
			transSubPathRecord.addColSqlValue("OBJECTID", transSubPathRecord.getObjectIdSql());
			transSubPathRecord.addColValue("RELATED_TRANSPATH_CUID", transPathCuid);
			transSubPathRecord.remove("RELATED_GZ_TRANS_PATH_CUID");
			transSubPathRecord.remove("PATH_STATE");
			transSubPathRecord.remove("CREATE_TYPE");
			transSubPathRecord.remove("PATH_REST_VOL");
			// 创建时隙与通道的关系 orig
			ctp2TransPathRecord1.addColValue("CUID", CUIDHexGenerator.getInstance().generate("CTP_TO_TRANSPATH"));
			ctp2TransPathRecord1.addColSqlValue("OBJECTID", ctp2TransPathRecord1.getObjectIdSql());
			ctp2TransPathRecord1.addColValue("RELATED_CTP_CUID", IbatisDAOHelper.getStringValue(gzmap, "ORIG_CTP_CUID"));
			ctp2TransPathRecord1.addColValue("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(gzmap, "ORIG_PTP_CUID"));
			ctp2TransPathRecord1.addColValue("RELATED_CARD_CUID",IbatisDAOHelper.getStringValue(gzmap, "ORIG_CARD_CUID"));
			ctp2TransPathRecord1.addColValue("RELATED_NE_CUID", IbatisDAOHelper.getStringValue(gzmap, "ORIG_EQU_CUID"));
			ctp2TransPathRecord1.addColValue("RELATED_SITE_CUID", IbatisDAOHelper.getStringValue(gzmap, "ORIG_SITE_CUID"));
			ctp2TransPathRecord1.addColValue("RELATED_TRANSPATH_CUID", transPathCuid);
			ctp2TransPathRecord1.addColValue("RELATED_SUB_PATH_CUID", subPathCuid);
			ctp2TransPathRecord1.addColValue("CREATE_TIME", now);
			// 创建时隙与通道的关系 dest
			ctp2TransPathRecord2.addColValue("CUID", CUIDHexGenerator.getInstance().generate("CTP_TO_TRANSPATH"));
			ctp2TransPathRecord2.addColSqlValue("OBJECTID", ctp2TransPathRecord2.getObjectIdSql());
			ctp2TransPathRecord2.addColValue("RELATED_CTP_CUID", IbatisDAOHelper.getStringValue(gzmap, "DEST_CTP_CUID"));
			ctp2TransPathRecord2.addColValue("RELATED_PTP_CUID", IbatisDAOHelper.getStringValue(gzmap, "DEST_PTP_CUID"));
			ctp2TransPathRecord2.addColValue("RELATED_CARD_CUID",IbatisDAOHelper.getStringValue(gzmap, "DEST_CARD_CUID"));
			ctp2TransPathRecord2.addColValue("RELATED_NE_CUID", IbatisDAOHelper.getStringValue(gzmap, "DEST_EQU_CUID"));
			ctp2TransPathRecord2.addColValue("RELATED_SITE_CUID", IbatisDAOHelper.getStringValue(gzmap, "DEST_SITE_CUID"));
			ctp2TransPathRecord2.addColValue("RELATED_TRANSPATH_CUID", transPathCuid);
			ctp2TransPathRecord2.addColValue("RELATED_SUB_PATH_CUID", subPathCuid);
			ctp2TransPathRecord2.addColValue("CREATE_TIME", now);
			
			
			transPathList.add(transPathRecord);
			transSubPathList.add(transSubPathRecord);
			ctp2TransPathList.add(ctp2TransPathRecord1);
			ctp2TransPathList.add(ctp2TransPathRecord2);
		}
		this.IbatisResDAO.insertDynamicTableBatch(transPathList);
		this.IbatisResDAO.insertDynamicTableBatch(transSubPathList);
		this.IbatisResDAO.insertDynamicTableBatch(ctp2TransPathList);
	}
	/**
	 * 撤销打散
	 * @param gzCuid
	 */
	public void repealSplitGzTransPath(String gzCuid) {
		if(StringUtils.isBlank(gzCuid)) throw new RuntimeException("高转通道CUID不能为空");
		Map pm = new HashMap();
		pm.put("cuid", gzCuid);
		Map<String,Object> gzmap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getGzTransPath",pm);
		int traphNum = IbatisDAOHelper.getIntValue(gzmap, "TRAPHNUM");
		if(traphNum>0){
			throw new RuntimeException("该高转通道下存在业务,请勿撤销！");
		}
		//删除ctp2transPath
		Integer num = this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteCtp2TransPathByGzCuid", pm);
		logger.info("删除ctp2transPath："+num);
		//删除transSubPath
		num = this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTransSubPathByGzCuid", pm);
		logger.info("删除transSubPath："+num);
		//删除transPath
		num = this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTransPathByGzCuid", pm);
		logger.info("删除transPath："+num);
	}
    
	/**
	 * 高阶打散
	 * @param cuid
	 */
	@SuppressWarnings("deprecation")
	public void splitTransPath(String cuid) {
		if(StringUtils.isBlank(cuid)) throw new RuntimeException("通道CUID不能为空");
		Map pm = new HashMap();
		pm.put("cuid", cuid);
		Map<String,Object> gjmap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getTransPath",pm);
		int pathNum = IbatisDAOHelper.getIntValue(gjmap, "PATHNUM");
		if(pathNum>0){
			throw new RuntimeException("该通道已打散,请勿重复打散！");
		}
		String origCtpRate = IbatisDAOHelper.getStringValue(gjmap, "ORIG_CTP_RATE");
		String destCtpRate = IbatisDAOHelper.getStringValue(gjmap, "DEST_CTP_RATE");
		if(StringUtils.isBlank(origCtpRate)||StringUtils.isBlank(destCtpRate)){
			throw new RuntimeException("A端时隙或者Z端时隙不能为空！");
		}
		if(!origCtpRate.equals(destCtpRate)){
			throw new RuntimeException("A端时隙与Z端时隙不相等！");
		}
//		"15".equals(origCtpRate)) 622M的暂且不处理
		if(!"9".equals(origCtpRate)){
			throw new RuntimeException("时隙不是155M！");
		}
		String preName = null;
		int count = 0;
		int logicRate = 0;
		if("9".equals(origCtpRate)){
			preName = "/2M";
			count = 63;
			logicRate = 1;
		}
//		else if("15".equals(origCtpRate)){
//			preName = "/155M";
//			count = 8;
//			logicRate = 1;
//		}
		List<Record> transPathList = new ArrayList<Record>();
		List<Record> transSubPathList = new ArrayList<Record>();
		String oorigCtpName = IbatisDAOHelper.getStringValue(gjmap, "ORIG_CTP_NAME");
		String odestCtpName = IbatisDAOHelper.getStringValue(gjmap, "DEST_CTP_NAME");
		String origPtpName = IbatisDAOHelper.getStringValue(gjmap, "ORIG_PTP_NAME");
		String destPtpName = IbatisDAOHelper.getStringValue(gjmap, "DEST_PTP_NAME");
		String origSiteName = IbatisDAOHelper.getStringValue(gjmap, "ORIG_SITE_NAME");
		String destSiteName = IbatisDAOHelper.getStringValue(gjmap, "DEST_SITE_NAME");
		String pathName = IbatisDAOHelper.getStringValue(gjmap, "ROUTE_DESCIPTION");
		//tran_path的名称要是没有《》，这里会报错
		CharSequence transSysName="";
		if((pathName.indexOf("《")!=-1)&&(pathName.indexOf("》")!=-1)){
			transSysName=pathName.subSequence(pathName.indexOf("《"), pathName.indexOf("》"));
		}
		//TODO 虚拟打散时隙
		for(int i=1;i<=count;i++){
			Record transPathRecord = new Record("TRANS_PATH");
			Record transSubPathRecord = new Record("TRANS_SUB_PATH");
			
			String norigCtpName = oorigCtpName+preName+i;
			String ndestCtpName = odestCtpName+preName+i;
			String descriptionA = origSiteName+"("+origPtpName+"/"+norigCtpName+")"+transSysName+preName+i+"》("+destPtpName+"/"+ndestCtpName+")"+destSiteName;
			
			String descriptionZ = destSiteName+"("+destPtpName+"/"+ndestCtpName+")"+transSysName+preName+i+"》("+origPtpName+"/"+norigCtpName+")"+origSiteName;
			
			String transPathCuid = CUIDHexGenerator.getInstance().generate("TRANS_PATH");
			Date now = new Date();
			transPathRecord.addColValue("CUID", transPathCuid);
			transPathRecord.addColSqlValue("OBJECTID", transPathRecord.getObjectIdSql());
			transPathRecord.addColValue("RELATED_GZ_TRANS_PATH_CUID", cuid);
			transPathRecord.addColValue("LABEL_CN", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION_A", descriptionA);
			transPathRecord.addColValue("ROUTE_DESCIPTION_Z", descriptionZ);
			transPathRecord.addColValue("RELATED_A_DISTRICT_CUID",IbatisDAOHelper.getStringValue(gjmap, "RELATED_A_DISTRICT_CUID"));
			transPathRecord.addColValue("RELATED_Z_DISTRICT_CUID",IbatisDAOHelper.getStringValue(gjmap, "RELATED_Z_DISTRICT_CUID"));
			transPathRecord.addColValue("ORIG_ROOM_CUID",IbatisDAOHelper.getStringValue(gjmap, "ORIG_ROOM_CUID"));
			transPathRecord.addColValue("DEST_ROOM_CUID",IbatisDAOHelper.getStringValue(gjmap, "DEST_ROOM_CUID"));
			transPathRecord.addColValue("ORIG_CARD_CUID",IbatisDAOHelper.getStringValue(gjmap, "ORIG_CARD_CUID"));
			transPathRecord.addColValue("DEST_CARD_CUID",IbatisDAOHelper.getStringValue(gjmap, "DEST_CARD_CUID"));
			transPathRecord.addColValue("ORIG_SITE_CUID",IbatisDAOHelper.getStringValue(gjmap, "ORIG_SITE_CUID"));
			transPathRecord.addColValue("DEST_SITE_CUID",IbatisDAOHelper.getStringValue(gjmap, "DEST_SITE_CUID"));
			transPathRecord.addColValue("ORIG_EQU_CUID",IbatisDAOHelper.getStringValue(gjmap, "ORIG_EQU_CUID"));
			transPathRecord.addColValue("DEST_EQU_CUID",IbatisDAOHelper.getStringValue(gjmap, "DEST_EQU_CUID"));
			transPathRecord.addColValue("RELATED_A_END_PTP", IbatisDAOHelper.getStringValue(gjmap, "ORIG_PTP_CUID"));
			transPathRecord.addColValue("RELATED_Z_END_PTP", IbatisDAOHelper.getStringValue(gjmap, "DEST_PTP_CUID"));
			transPathRecord.addColValue("ORIG_POINT_CUID", IbatisDAOHelper.getStringValue(gjmap, "ORIG_CTP_CUID"));
			transPathRecord.addColValue("DEST_POINT_CUID", IbatisDAOHelper.getStringValue(gjmap, "DEST_CTP_CUID"));
			
			transPathRecord.addColValue("ORIG_CTP_NAME", norigCtpName);
			transPathRecord.addColValue("DEST_CTP_NAME", ndestCtpName);
			
			transPathRecord.addColValue("PATH_STATE", 1);// 空闲
			transPathRecord.addColValue("CREATE_TYPE", 1);// 预配
			transPathRecord.addColValue("IS_EQUAL_Z", 1);// Z端口速率是否与通道速率相等
			transPathRecord.addColValue("IS_EQUAL_A", 1);// A端口速率是否与通道速率相等
			transPathRecord.addColValue("PATH_REST_VOL", 0);// 空闲速率
			transPathRecord.addColValue("CREATE_TIME", now);
			transPathRecord.addColValue("CH_RATE", logicRate);
			
			// 创建子通道
			transSubPathRecord.getValues().putAll(transPathRecord.getValues());
			String subPathCuid = CUIDHexGenerator.getInstance().generate("TRANS_SUB_PATH");
			transSubPathRecord.addColValue("CUID", subPathCuid);
			transSubPathRecord.addColSqlValue("OBJECTID", transSubPathRecord.getObjectIdSql());
			transSubPathRecord.addColValue("RELATED_TRANSPATH_CUID", transPathCuid);
			transSubPathRecord.remove("RELATED_GZ_TRANS_PATH_CUID");
			transSubPathRecord.remove("PATH_STATE");
			transSubPathRecord.remove("CREATE_TYPE");
			transSubPathRecord.remove("PATH_REST_VOL");
			transPathList.add(transPathRecord);
			transSubPathList.add(transSubPathRecord);
		}
		this.IbatisResDAO.insertDynamicTableBatch(transPathList);
		this.IbatisResDAO.insertDynamicTableBatch(transSubPathList);
	}
	/**
	 * 撤销打散
	 * @param cuid
	 */
	@SuppressWarnings("deprecation")
	public void repealSplitTransPath(String cuid) {
		if(StringUtils.isBlank(cuid)) throw new RuntimeException("高阶通道CUID不能为空");
		Map pm = new HashMap();
		pm.put("cuid", cuid);
		Map<String,Object> gjmap = (Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getTransPath",pm);
		int traphNum = IbatisDAOHelper.getIntValue(gjmap, "TRAPHNUM");
		if(traphNum>0){
			throw new RuntimeException("该高阶通道下存在业务,请勿撤销！");
		}
		//删除transSubPath
		Integer num = this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTransSubPathByGzCuid", pm);
		logger.info("删除transSubPath："+num);
		//删除transPath
		num = this.IbatisResDAO.getSqlMapClientTemplate().delete(sqlMap+".deleteTransPathByGzCuid", pm);
		logger.info("删除transPath："+num);
	}
	/**
	 * 查询传输系统
	 * @param gzCuid
	 */
	public List<Map> getTransSystem(Map param) {
		String aPointCuid=(String) param.get("apointCuid");
		String zPointCuid=(String) param.get("zpointCuid");
		Map pm = new HashMap();
		List<Map> result = new ArrayList<Map>();
		if(StringUtils.isNotBlank(aPointCuid)&&StringUtils.isNotBlank(zPointCuid)){
			pm.put("aPointCuid", aPointCuid);
			List<Map<String,Object>> atransSystems=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTransSystem", pm);
			pm.clear();
			pm.put("zPointCuid", zPointCuid);
			List<Map<String,Object>> ztransSystems=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".queryTransSystem", pm);
			List<Map<String,Object>> transSystems = new ArrayList<Map<String,Object>>();
			for (Map<String,Object> amap : atransSystems){
				String aCuid = (String)amap.get("CUID");
				for(Map<String,Object> zmap : ztransSystems){
					String zCuid = (String)zmap.get("CUID");
					if (aCuid.equals(zCuid)){
						transSystems.add(amap);
					}
				}
			}
			if(transSystems == null || transSystems.isEmpty()) {
				throw new RuntimeException("没有可选传输系统！");
				
			}	
			for(Map<String,Object> map:transSystems){
				result.add(map);
			}			
		}
		return result;
	}
	
	/**
	 * 获取WDM传输系统的空闲波或通道时隙.
	 * @param systemCuid
	 * @param pathRate     准备承载的光波道的速率
	 * @param rateToSearch 目标光波道的速率
	 * @param pathType
	 * @return
	 * @throws Exception 
	 * @throws UserException
	 */
	public List<Object> getFreeSystemWave(String sysASiteCuid,String sysZSiteCuid,String sysCuid,Integer pathRate) throws Exception{
		Map pm=new HashMap();
		pm.put("sysCuid", sysCuid);
		Map<String,Object> wdmSystem=(Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".queryTransSystem",pm);
		Integer waveRate=0;
		if(!wdmSystem.isEmpty()){
			 waveRate=IbatisDAOHelper.getIntValue(wdmSystem, "WAVE_RATE");
	    }
		Integer rateTosearch=getRateToSearch(waveRate,pathRate);//17;
		if(rateTosearch==null){
			throw new RuntimeException("分析波道异常！");
		}
		HashMap<String,ArrayList<String>> ctpTosearch = getWdmCtpToSearch(wdmSystem, rateTosearch);
		
		List<TRUsedWave> usedList=new ArrayList<TRUsedWave>();
		Map params=new HashMap();
		params.put("sysCuid", sysCuid);
		List<Map> usedMap=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getFreeSystemWave",pm);
		if(usedMap.size()>0&&usedMap!=null){
			for(Map map:usedMap){
				Integer traphRate=IbatisDAOHelper.getIntValue(map,"TRAPH_RATE");
				String timegapInfo=IbatisDAOHelper.getStringValue(map, "TIMEGAP_INFO");
				if (timegapInfo.indexOf("》")>-1){
					timegapInfo=timegapInfo.substring(timegapInfo.indexOf("λ"), timegapInfo.indexOf("》"));
				}
				TRUsedWave trUse = new TRUsedWave();
				trUse.setWaveInfo(traphRate,timegapInfo, waveRate,getSqlLableCN(traphRate));
				usedList.add(trUse);
			}
		}
		
	  for (TRUsedWave trUse:usedList){
		ArrayList<String> ctpWillFree = ctpTosearch.get(trUse.wavehead);
		//说明波下有时隙被使用
		//检测使用的时隙是否与待分析的时隙等速率
		if (trUse.traphRate != rateTosearch){
			if (trUse.traphRate== 16){//2.5G
				//存在电路是2.5G的，但待分析电路是GE的，则同一10G下，不允许混用
				if (rateTosearch ==  35){//GE
					int keyPlace = trUse.usedWaveCtpInfo.indexOf("2.5G");
					String upTimegap  = trUse.usedWaveCtpInfo.substring(0,keyPlace);
					for ( int k =0;k<ctpWillFree.size();k++){
						String waveWillSearch = ctpWillFree.get(k);
						if (waveWillSearch.indexOf(upTimegap)>-1){
							//该时隙不能混合调度，设置时隙为占用
							ctpWillFree.remove(k);
							k--;
						}
					}
				}
			}
			if (trUse.traphRate== 35){//GE
				//存在电路是GE的，但待分析电路是2.5G的，则同一10G下，不允许混用
				if (rateTosearch ==  16){//2.5G
					int keyPlace = trUse.usedWaveCtpInfo.indexOf("GE");
					String upTimegap  = trUse.usedWaveCtpInfo.substring(0,keyPlace);
					for ( int k =0;k<ctpWillFree.size();k++){
						String waveWillSearch = ctpWillFree.get(k);
						if (waveWillSearch.indexOf(upTimegap)>-1){
							//该时隙不能混合调度，设置时隙为占用
							ctpWillFree.remove(k);
							k--;
						}
					}
				}
			}

		}
		for ( int k =0;k<ctpWillFree.size();k++){
			String waveWillSearch = ctpWillFree.get(k);
			if (waveWillSearch.indexOf(trUse.usedWaveCtpInfo)>-1){
				//该时隙已经占用
				ctpWillFree.remove(k);
				k--;
			}
		}
	}
		
		return buildSegList(wdmSystem,sysASiteCuid,sysZSiteCuid,ctpTosearch);
	}
	public String getSqlLabelCn(String value){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT MAX(LABEL_CN) FROM ( ");
		sb.append(" SELECT DP.LABEL_CN         ");
		sb.append("   FROM PTP P, DDFPORT DP, JUMP_PAIR JP ");
		sb.append("  WHERE DP.CUID = JP.DEST_POINT_CUID    ");
		sb.append("    AND JP.ORIG_POINT_CUID = P.CUID     ");
		sb.append(" AND  P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID = '"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT DP.LABEL_CN         ");
		sb.append("   FROM PTP P, DDFPORT DP, JUMP_PAIR JP ");
		sb.append("  WHERE DP.CUID = JP.ORIG_POINT_CUID    ");
		sb.append("    AND JP.DEST_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT OP.LABEL_CN        ");
		sb.append("   FROM PTP P, ODFPORT OP, JUMP_FIBER JF");
		sb.append("  WHERE OP.CUID = JF.DEST_POINT_CUID    ");
		sb.append("    AND JF.ORIG_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT OP.LABEL_CN        ");
		sb.append("   FROM PTP P, ODFPORT OP, JUMP_FIBER JF");
		sb.append("  WHERE OP.CUID = JF.ORIG_POINT_CUID    ");
		sb.append("    AND JF.DEST_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"')");
		return sb.toString();
	}
	public String getSqlCuid(String value){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT MAX(CUID) FROM ( ");
		sb.append(" SELECT DP.CUID         ");
		sb.append("   FROM PTP P, DDFPORT DP, JUMP_PAIR JP ");
		sb.append("  WHERE DP.CUID = JP.DEST_POINT_CUID    ");
		sb.append("    AND JP.ORIG_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID = '"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT DP.CUID         ");
		sb.append("   FROM PTP P, DDFPORT DP, JUMP_PAIR JP ");
		sb.append("  WHERE DP.CUID = JP.ORIG_POINT_CUID    ");
		sb.append("    AND JP.DEST_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT OP.CUID        ");
		sb.append("   FROM PTP P, ODFPORT OP, JUMP_FIBER JF");
		sb.append("  WHERE OP.CUID = JF.DEST_POINT_CUID    ");
		sb.append("    AND JF.ORIG_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"'");
		sb.append(" UNION ALL                              ");
		sb.append(" SELECT OP.CUID        ");
		sb.append("   FROM PTP P, ODFPORT OP, JUMP_FIBER JF");
		sb.append("  WHERE OP.CUID = JF.ORIG_POINT_CUID    ");
		sb.append("    AND JF.DEST_POINT_CUID = P.CUID     ");
		sb.append(" AND P.IS_CONN_STATE=1");
		sb.append("    AND P.CUID ='"+value+"')");
		return sb.toString();
	}
	private Integer getRateToSearch(Integer waveRate,Integer pathRate){
		
		String pathRateName = getSqlLableCN(pathRate);
		String waveRateKey=getSqlLableCN(waveRate);
		if (pathRateName.indexOf("FC")>-1){
			pathRateName = "FC";
		}
		return rateInfoMap.get(waveRateKey+"-" + pathRateName);
	}
	
	public String getSqlLableCN(Integer value){
		String sql="SELECT KEY_VALUE FROM RATE WHERE KEY_NUM= "+value;
		List<Map> resultList=this.IbatisResDAO.querySql(sql);
		String lableCn="";
		if(resultList!=null &&resultList.size()> 0){
			for(Map map:resultList){
				lableCn=IbatisDAOHelper.getStringValue(map, "KEY_VALUE");
			}
		}
		return  lableCn;
	}
	
	public HashMap<String ,ArrayList<String>> getWdmCtpToSearch(Map<String,Object> wdmSystem,Integer rateTosearch){
		Integer waveRate=IbatisDAOHelper.getIntValue(wdmSystem, "WAVE_RATE");
		int waveNum=IbatisDAOHelper.getIntValue(wdmSystem, "WAVE_NUM");
		HashMap<String,ArrayList<String>> allWaveMap = new HashMap<String,ArrayList<String>>();
		if (waveRate == rateTosearch){
			//全波
			for (int i =1;i<waveNum+1;i++){
				ArrayList<String> waveList = new ArrayList<String>();
				waveList.add("λ"+i);
				allWaveMap.put("λ"+i,waveList);
			}
		}else {
			//
			for (int i =1;i<waveNum+1;i++){
				ArrayList<String> waveList = new ArrayList<String>();
				if (waveRate == 19){
					if (rateTosearch==17){
						for ( int j=1;j<5;j++){
							waveList.add("λ"+i+"/10G"+j);
						}
					}else if (rateTosearch==16){
						for ( int j=1;j<5;j++){
							waveList.add("λ"+i +"/10G"+j+"/2.5G1");
							waveList.add("λ"+i +"/10G"+j+"/2.5G2");
							waveList.add("λ"+i +"/10G"+j+"/2.5G3");
							waveList.add("λ"+i +"/10G"+j+"/2.5G4");
						}
					}else  if (rateTosearch==35){
						for ( int j=1;j<5;j++){
							waveList.add("λ"+i +"/10G"+j+"/GE1");
							waveList.add("λ"+i +"/10G"+j+"/GE2");
							waveList.add("λ"+i +"/10G"+j+"/GE3");
							waveList.add("λ"+i +"/10G"+j+"/GE4");
							waveList.add("λ"+i +"/10G"+j+"/GE5");
							waveList.add("λ"+i +"/10G"+j+"/GE6");
							waveList.add("λ"+i +"/10G"+j+"/GE7");
							waveList.add("λ"+i +"/10G"+j+"/GE8");
						}
					}
				}else if (waveRate == 17){
					if (rateTosearch==16){
						for ( int j=1;j<5;j++){
							waveList.add("λ"+i +"/2.5G"+j);
						}
					}else  if (rateTosearch==35){
						for ( int j=1;j<9;j++){
							waveList.add("λ"+i +"/GE" +j);
						}
					}
					
				}
				allWaveMap.put("λ"+i, waveList);
			}
		}
		return allWaveMap;	
	}
	
	private List<Object> buildSegList(Map<String,Object> system,String startSite,String endSite,
			HashMap<String,ArrayList<String>> freeSag) throws Exception {
		List<Object> segList=new ArrayList<Object>();
		String sysCuid=IbatisDAOHelper.getStringValue(system, "CUID");
		String sysLabelCn=IbatisDAOHelper.getStringValue(system, "LABEL_CN");
		int waveNum=IbatisDAOHelper.getIntValue(system, "WAVE_NUM");
		List<Map> endingPtps = getSegEndingPtp(sysCuid,startSite,endSite);
		if (endingPtps.size()!= 2){
			throw new Exception("不能获取关键端口。");
		}
		Map<String,Object> startptp = endingPtps.get(0);
		Map<String,Object> endptp = endingPtps.get(1);
		String startNeRelSiteCuid=getRelatedSiteCuid(IbatisDAOHelper.getStringValue(startptp, "RELATED_NE_CUID"));
		String endNeRelSiteCuid=getRelatedSiteCuid(IbatisDAOHelper.getStringValue(endptp, "RELATED_NE_CUID"));
		for (int i = 1; i < waveNum + 1; i++) {
			ArrayList<String> freesegnames = freeSag.get("λ" + i);
			if (freesegnames != null && freesegnames.size() > 0) {
				List<Object> subsegList=new ArrayList<Object>();
				PathRouteSeg topSeg = new PathRouteSeg();
				topSeg.setOrigPointCuid(IbatisDAOHelper.getStringValue(startptp,"CUID"));
				topSeg.setDestPointCuid(IbatisDAOHelper.getStringValue(endptp,"CUID"));
				topSeg.setRelatedSystemCuid(sysCuid);
				topSeg.setCuid(CUIDHexGenerator.getInstance().generate("PATH_ROUTE_SEG"));
				topSeg.setDirection(1);

				topSeg.setLabelCn("《" + sysLabelCn + "/"
						+ "λ" + i + "》");
				topSeg.setReverseLabel("《" + sysLabelCn + "/"
						+ "λ" + i + "》");
				topSeg.setOrigPtpCuid(IbatisDAOHelper.getStringValue(startptp,"CUID"));
				topSeg.setOrigSiteCuid(startNeRelSiteCuid);
				topSeg.setDestPtpCuid(IbatisDAOHelper.getStringValue(endptp,"CUID"));
				topSeg.setDestSiteCuid(endNeRelSiteCuid);
				topSeg.setTimegapInfo("λ" + i);

				for (String segCtpName : freesegnames) {
					PathRouteSeg tempSeg = new PathRouteSeg();
					tempSeg.setOrigPointCuid(IbatisDAOHelper.getStringValue(startptp,"CUID"));

					tempSeg.setDestPointCuid(IbatisDAOHelper.getStringValue(endptp,"CUID"));

					tempSeg.setRelatedSystemCuid(sysCuid);
					tempSeg.setCuid(CUIDHexGenerator.getInstance().generate("PATH_ROUTE_SEG"));
					tempSeg.setDirection(1);

					tempSeg.setLabelCn("《" + sysLabelCn + "/"
							+ segCtpName + "》");
					tempSeg.setReverseLabel("《" + sysLabelCn + "/"
							+ segCtpName + "》");
					tempSeg.setOrigPtpCuid(IbatisDAOHelper.getStringValue(startptp,"CUID"));
					tempSeg.setOrigSiteCuid(startNeRelSiteCuid);
					tempSeg.setDestPtpCuid(IbatisDAOHelper.getStringValue(endptp,"CUID"));
					tempSeg.setDestSiteCuid(endNeRelSiteCuid);
					tempSeg.setTimegapInfo(segCtpName);
					subsegList.add(subsegList.size(), tempSeg);
				}
				topSeg.setArrValue(subsegList);
                segList.add(segList.size(), topSeg);
			}
		}
		
		return segList;
	}
	private List<Map> getSegEndingPtp(String sysCuid,String startSite,String endSite) throws Exception {
		List<Map> segList= new ArrayList<Map>();
		Map pm=new HashMap();
		pm.put("sysCuid", sysCuid);
		List<Map> topoLinks=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".selectNebyTopo",pm);
		//List<TopoLink> allTopos = new ArrayList<TopoLink>();
		if(topoLinks.size()>0 && topoLinks !=null){
			int firstSiteplace = -1;
			int lastSiteplace = -1;
			int i=0;
			for(Map topolink:topoLinks){
				String relOrigNeCuid=IbatisDAOHelper.getStringValue(topolink, "ORIG_NE_CUID");
				String relDestNeCuid=IbatisDAOHelper.getStringValue(topolink, "DEST_NE_CUID");
				String neRelatedSiteCuid=getRelatedSiteCuid(relDestNeCuid);
				if (neRelatedSiteCuid.equals(startSite) ) {
					firstSiteplace = i;
				}
				if (neRelatedSiteCuid.equals(endSite)) {
					lastSiteplace = i;//1
				}
				neRelatedSiteCuid=getRelatedSiteCuid(relOrigNeCuid);
				if (neRelatedSiteCuid.equals(startSite)) {
					firstSiteplace = i;//0
				}
				if (neRelatedSiteCuid.equals(endSite)) {
					lastSiteplace = i;//
				}
				i++;
			}
			
			if (firstSiteplace == -1 || lastSiteplace == -1) {
				logger.info("网元不可达！");
				throw new Exception("网元不可达！");		
			}
			if (firstSiteplace == lastSiteplace){
				String origNeCuid = (String) topoLinks.get(firstSiteplace).get("ORIG_NE_CUID");
				String neRelatedSiteCuid = getRelatedSiteCuid(origNeCuid);
				if (neRelatedSiteCuid.equals(startSite)){
				   Map<String,Object> startPtp=getPtp((String) topoLinks.get(firstSiteplace).get("ORIG_POINT_CUID"));
				   segList.add(startPtp);
				   Map<String,Object> endPtp=getPtp((String) topoLinks.get(firstSiteplace).get("DEST_POINT_CUID"));
				   segList.add(endPtp);
				}else{
				   Map<String,Object> startPtp=getPtp((String) topoLinks.get(firstSiteplace).get("DEST_POINT_CUID"));
				   segList.add(startPtp);
				   Map<String,Object> endPtp=getPtp((String) topoLinks.get(firstSiteplace).get("ORIG_POINT_CUID"));
				   segList.add(endPtp);
				}

			}else if (firstSiteplace > lastSiteplace) {
				// 局站反向
				 Map<String,Object> startPtp=getPtp((String) topoLinks.get(firstSiteplace - 1).get("ORIG_POINT_CUID"));
				 segList.add(startPtp);
				 Map<String,Object> endPtp=getPtp((String) topoLinks.get(lastSiteplace).get("DEST_POINT_CUID"));
				 segList.add(endPtp);
			} else {
				 Map<String,Object> startPtp=getPtp((String) topoLinks.get(firstSiteplace).get("ORIG_POINT_CUID"));
				 segList.add(startPtp);
				 Map<String,Object> endPtp=getPtp((String) topoLinks.get(lastSiteplace - 1).get("DEST_POINT_CUID"));
				 segList.add(endPtp);
			}
		}
		return segList;
	}
	
	public String getRelatedSiteCuid(String cuid){
		Map pm=new HashMap();
		pm.put("cuid",cuid);
		Map<String,Object> ne=(Map<String, Object>)this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getNeRelatedSiteCuid",pm);
		return IbatisDAOHelper.getStringValue(ne, "RELATED_SITE_CUID");
	}
	
	public Map<String,Object> getPtp(String cuid){
		Map pm=new HashMap();
		pm.put("cuid", cuid);
		Map<String,Object> ptpMap=(Map<String, Object>) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap+".getPtp",pm);
		return ptpMap;
	}
	
	//联通业务查询光波道信息
	public List<Map> getFreeTimegapInfo(Map param) {
		String sysASiteCuid=IbatisDAOHelper.getStringValue(param,"sysAPointCuid");
    	String sysZSiteCuid=IbatisDAOHelper.getStringValue(param,"sysZPointCuid");
    	String sysCuid=IbatisDAOHelper.getStringValue(param,"sysCuid");
    	Integer traphRate=IbatisDAOHelper.getIntValue(param,"traphRate"); 
    	Map pm=new HashMap();
		pm.put("sysCuid", sysCuid);
    	List<Map> pathSegList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getPathSeg",pm);
    	if(pathSegList == null || pathSegList.isEmpty()) {
			throw new RuntimeException("没有空闲波道！");
			
		}
    	return pathSegList;
	}
	
	public HashMap getTunnelByNeCuid(HttpServletRequest request,String aSideCuid, String zSideCuid) throws Exception {
		String queryATransElement ="";
		if (aSideCuid.startsWith("PTP-")){
			queryATransElement="SELECT RELATED_NE_CUID FROM PTP WHERE CUID='"+aSideCuid+"'";
		}else if (aSideCuid.startsWith("CTP-")){
			queryATransElement="SELECT P.RELATED_NE_CUID FROM PTP P, CTP T WHERE T.RELATED_PTP_CUID = P.CUID AND T.CUID='"+aSideCuid+"'";
		}
		
		List<Map<String, Object>> aSiteCuidList = this.IbatisResDAO.querySql(queryATransElement);
		String origNeCuid=(String) aSiteCuidList.get(0).get("RELATED_NE_CUID");
		
		String queryZTransElement = "";
		if (zSideCuid.startsWith("PTP-")){
			queryZTransElement="SELECT RELATED_NE_CUID FROM PTP WHERE CUID='"+zSideCuid+"'";
		}else if (zSideCuid.startsWith("CTP-")){
			queryZTransElement="SELECT P.RELATED_NE_CUID FROM PTP P, CTP T WHERE T.RELATED_PTP_CUID = P.CUID AND T.CUID='"+zSideCuid+"'";
		}
		List<Map<String, Object>> zSiteCuidList = this.IbatisResDAO.querySql(queryZTransElement);
		String destNeCuid=(String) zSiteCuidList.get(0).get("RELATED_NE_CUID");
		
		HashMap map = new HashMap();
		if (origNeCuid != null && origNeCuid.trim().length() > 0&& destNeCuid != null && destNeCuid.trim().length() > 0) {
			
			List<Map<String, Object>> list = this.selectTurnnelByLsp(origNeCuid, destNeCuid);
			//TODO	通过LSP保护组查询备隧道	VOSTRO
			//前台展示列的长度	1.主隧道CUID	2.主隧道全局编码名称	3.伪线名称	 4.备隧道CUID  5.备隧道全局编码名称    6.容量预警
			int columnSize = 6;
			if(list!=null&&list.size()>0){
				String[][] strs = new String[list.size()][columnSize];
				for(int i=0;i<list.size();i++){
					Map mapContent=new HashMap();
					mapContent=list.get(i);
					String wholeTurnnelName = IbatisDAOHelper.getStringValue(mapContent, "WHOLE_TURNNEL_NAME");//原主隧道名称
					String turnnelCuid = IbatisDAOHelper.getStringValue(mapContent, "TURNNEL_CUID");//原主隧道CUID
					String lspTurnnelName = IbatisDAOHelper.getStringValue(mapContent, "LSP_TURNNEL_NAME");//LSP保护组中主隧道名称
					String lspTurnnelCuid = IbatisDAOHelper.getStringValue(mapContent, "LSP_TURNNEL_CUID");//LSP保护组中主隧道CUID
					String lspBackupTurnnelName = IbatisDAOHelper.getStringValue(mapContent, "LSP_BACKUP_TURNNEL_NAME");//LSP保护组中备用隧道名称
					String lspBackupTurnnelCuid = IbatisDAOHelper.getStringValue(mapContent, "LSP_BACKUP_TURNNEL_CUID");//LSP保护组中备用隧道CUID
					if(StringUtils.isNotEmpty(lspTurnnelName)) //如果LSP保护组中主用隧道名称不为空
					{
						logger.info("LSP保护组中主用隧道名称不为空");
						strs[i][0]= lspTurnnelCuid;
						strs[i][1]= lspTurnnelName;
						strs[i][3]= lspBackupTurnnelCuid;
						strs[i][4]= lspBackupTurnnelName;
					}else
					{
						logger.info("LSP保护组中主用隧道名称为空");
						strs[i][0]= turnnelCuid;
						strs[i][1]= wholeTurnnelName;
					}
					strs[i][2]="";//暂不查询伪线名称
				}
				map.put("tunnel", strs);
			}
			map.put("listSize", list.size());
	    }
		return map;
	}
	
	/**
	 * 创建伪线：根据隧道信息创建伪线cuid，根据路由信息创建伪线两端数据。
	 * @throws Exception 
	 */
	public void createVirtualLine(PathData pathData) throws Exception {
		//获得隧道信息
		//select TURNNEL_CUID,TURNNEL_NAME from T_ATTEMP_DGN_SEG_DETAIL where CUID='T_ATTEMP_DGN_SEG-8a9f823e3f18cab1013f18cab1030000' 
		String relatedDgnSegCuid=pathData.getDataString("DETAIL_CUID");
		System.out.println("T_ATTEMP_DGN_SEG_DETAIL id in TraphDesignBO ----"+relatedDgnSegCuid);
		String queryTurnnelCuid="SELECT TURNNEL_CUID,TURNNEL_NAME FROM T_ATTEMP_DGN_SEG_DETAIL WHERE CUID='"+relatedDgnSegCuid+"'";
		List<Map<String, Object>> TurnnelCuidList = this.IbatisResDAO.querySql(queryTurnnelCuid);	
		
		ArrayList<Map<String, Object>> turnnelList=new ArrayList<Map<String, Object>>();
		
		String tunnelCuid=TurnnelCuidList.get(0).get("TURNNEL_CUID").toString();
		
		Map<String, Object> ptnTurnnelMap=new HashMap();
		String queryPtnTurnnel="SELECT * FROM PTN_TURNNEL WHERE CUID='"+tunnelCuid+"'";
		List<Map<String, Object>> ptnTurnnelList = this.IbatisResDAO.querySql(queryPtnTurnnel);	
		for(int k=0;k<ptnTurnnelList.size();k++){
			ptnTurnnelMap=ptnTurnnelList.get(k);
			String wholeArr[]=null;
			wholeArr=new String[]{String.valueOf(ptnTurnnelMap.get("WHOLE_TURNNEL_NO")),ptnTurnnelMap.get("WHOLE_TURNNEL_NAME").toString(),String.valueOf(this.getMaxLineByTurnnel(ptnTurnnelMap.get("CUID").toString()))};

			PtnVirtualLine virtualLine = new PtnVirtualLine();
			//创建伪线
			//----参照此处写创建伪线的方法，dealPtnVirtualLine为创建伪线的cuid方法
			virtualLine = dealPtnVirtualLine(virtualLine,wholeArr);
			//virtualLine.clearUnknowAttrs();
			
			//getTraphAndAttDAO().createObject(new BoActionContext(),virtualLine);
			//代替上面一行代码，根据隧道的一些信息创建伪线
			//ptn_path表的RELATED_A_NE_CUID字段
			String aElementCuid=pathData.getDataString("A_NE_CUID");
			//ptn_path表的RELATED_Z_NE_CUID字段
			String zElementCuid=pathData.getDataString("Z_NE_CUID");
			
			Map<String, Object> startNeMap=new HashMap();
			String queryStartNe="SELECT * FROM TRANS_ELEMENT WHERE CUID ='"+aElementCuid+"'";
			List<Map<String, Object>> startNeList = this.IbatisResDAO.querySql(queryStartNe);
			startNeMap=startNeList.get(0);
			
			Map<String, Object> endNeMap=new HashMap();
			String queryEndNe="SELECT * FROM TRANS_ELEMENT WHERE CUID ='"+zElementCuid+"'";
			List<Map<String, Object>> endNeList = this.IbatisResDAO.querySql(queryEndNe);
			endNeMap=endNeList.get(0);
			
			//virtualLine.setAttrValue("PTN_VIRTUAL_LINE" + "ORIG_NE_CUID", endNeMap.get("LABEL_CN"));
			virtualLine.setOrigNeCuid(aElementCuid);
			virtualLine.setOrigPtpCuid(pathData.getDataString("A_PTP_CUID"));
					
			//virtualLine.setAttrValue("PTN_VIRTUAL_LINE" + "DEST_NE_CUID", pathData.getDataString("A_NE_NAME"));
			virtualLine.setDestNeCuid(zElementCuid);
			virtualLine.setDestPtpCuid(pathData.getDataString("Z_PTP_CUID"));
			
			virtualLine.setRelatedEmsCuid(endNeMap.get("RELATED_EMS_CUID").toString());
			virtualLine.setLayerRate("1");
			virtualLine.setActiveFlag(2L);
			virtualLine.setDirectionality(2L);
			virtualLine.setFdn(startNeMap.get("FDN") +"<-->" + endNeMap.get("FDN"));
			virtualLine.setUserLabel(startNeMap.get("LABEL_CN") +"-" + endNeMap.get("LABEL_CN"));
			virtualLine.setNatineEmsName(startNeMap.get("LABEL_CN") +"-" + endNeMap.get("LABEL_CN"));
			virtualLine.setDestTpFdn(endNeMap.get("FDN").toString());
			virtualLine.setOrigTpFdn(startNeMap.get("FDN").toString());
			
//			Map virtualLineMap=new HashMap();
//			virtualLineMap.put("ORIG_NE_CUID", virtualLine.getOrigNeCuid());
//			virtualLineMap.put("ORIG_PTP_CUID", virtualLine.getOrigPtpCuid());
//			virtualLineMap.put("DEST_NE_CUID", virtualLine.getDestNeCuid());
//			virtualLineMap.put("DEST_PTP_CUID", virtualLine.getDestPtpCuid());
//			virtualLineMap.put("RELATED_EMS_CUID", virtualLine.getRelatedEmsCuid());
//			virtualLineMap.put("LAYER_RATE", virtualLine.getLayerRate());
//			virtualLineMap.put("ACTIVE_FLAG", virtualLine.getActiveFlag());
//			virtualLineMap.put("DIRECTIONALITY", virtualLine.getDirectionality());
//			virtualLineMap.put("FDN", virtualLine.getFdn());
//			virtualLineMap.put("USER_LABEL", virtualLine.getUserLabel());
//			virtualLineMap.put("NATINE_EMS_NAME", virtualLine.getNatineEmsName());
//			virtualLineMap.put("DEST_TP_FDN", virtualLine.getDestTpFdn());
//			virtualLineMap.put("ORIG_TP_FDN", virtualLine.getOrigTpFdn());
//			this.IbatisResDAO.insertDynamicTable("PTN_VIRTUAL_LINE", virtualLineMap);
			
			Record virtualLineRecod = new Record("PTN_VIRTUAL_LINE");
			virtualLineRecod.addColSqlValue("OBJECTID", virtualLineRecod.getObjectIdSql());
			virtualLineRecod.addColValue("CUID", CUIDHexGenerator.getInstance().generate("PTN_VIRTUAL_LINE"));
			//virtualLineRecod.addColValue("CUID", CUIDHexGenerator.getInstance().generate());
			virtualLineRecod.addColValue("ORIG_NE_CUID", virtualLine.getOrigNeCuid());
			virtualLineRecod.addColValue("ORIG_PTP_CUID", virtualLine.getOrigPtpCuid());
			virtualLineRecod.addColValue("DEST_NE_CUID", virtualLine.getDestNeCuid());
			virtualLineRecod.addColValue("DEST_PTP_CUID", virtualLine.getDestPtpCuid());
			virtualLineRecod.addColValue("RELATED_EMS_CUID", virtualLine.getRelatedEmsCuid());
			virtualLineRecod.addColValue("LAYER_RATE", virtualLine.getLayerRate());
			virtualLineRecod.addColValue("ACTIVE_FLAG", virtualLine.getActiveFlag());
			virtualLineRecod.addColValue("DIRECTIONALITY", virtualLine.getDirectionality());
			virtualLineRecod.addColValue("FDN", virtualLine.getFdn());
			virtualLineRecod.addColValue("USER_LABEL", virtualLine.getUserLabel());
			virtualLineRecod.addColValue("NATINE_EMS_NAME", virtualLine.getNatineEmsName());
			virtualLineRecod.addColValue("DEST_TP_FDN", virtualLine.getDestTpFdn());
			virtualLineRecod.addColValue("ORIG_TP_FDN", virtualLine.getOrigTpFdn());
			this.IbatisResDAO.insertDynamicTable(virtualLineRecod);
			
			//把此伪线保存进T_ATTEMP_DGN_SEG_DETAIL表
//			String sql="UPDATE PTN_PATH SET RELATED_VIRTUAL_LINE_CUID='"+virtualLineRecod.getCuid()+"' WHERE CUID='"++"'";
//			this.IbatisResDAO.updateSql(sql);
			
			//伪线表中没有这些字段，所以没有从webattemp工程中的代码迁移过来
			//ArrayList<PtnIpCrossconnect> routeList = getPtnInCrossconnectByTurnnelCuid(tun.getCuid());
			//tun.setAttrValue("PTN_IP_CROSSCONNECT",routeList);
			//ArrayList<PtnTurnnel> list=new ArrayList<PtnTurnnel>();
			//list.add(tun);
			//vline.setAttrValue("MAIN_TRUNNEL",list);
//			List<PtnNeToService> replatedTpList = new ArrayList<PtnNeToService>();
//			try{
//				String vTpFdn = virtualLine.getOrigTpFdn();
//				String vPtpFdn = this.getPortFdnByStr(vTpFdn);
//				if (this.isNotEmpty(vPtpFdn)){
//					Map<String, Object> realptpMap =this.getPtpByFdn(vPtpFdn);
//					if(realptpMap!=null){
//						PtnNeToService newRele = new PtnNeToService();
//						newRele.setRelatedCardCuid(realptpMap.get("RELATED_CARD_CUID").toString());
//						newRele.setRelatedNeCuid(realptpMap.get("RELATED_NE_CUID").toString());
//						newRele.setRelatedPtpCuid(realptpMap.get("CUID").toString());
//						newRele.setRelatedEmsCuid(realptpMap.get("RELATED_EMS_CUID").toString());
//						newRele.setTpFdn(vTpFdn);
//						newRele.setRelatedVirtuallineFdn(virtualLine.getFdn());
//						replatedTpList.add(newRele);
//					}
//				}
//				vTpFdn = virtualLine.getDestTpFdn();
//				vPtpFdn = this.getPortFdnByStr(vTpFdn);
//				if (this.isNotEmpty(vPtpFdn)){
//					Map<String, Object> realptpMap =this.getPtpByFdn(vPtpFdn);
//					if (realptpMap != null){
//						PtnNeToService newRele = new PtnNeToService();
//						newRele.setRelatedCardCuid(realptpMap.get("RELATED_CARD_CUID").toString());
//						newRele.setRelatedNeCuid(realptpMap.get("RELATED_NE_CUID").toString());
//						newRele.setRelatedPtpCuid(realptpMap.get("CUID").toString());
//						newRele.setRelatedEmsCuid(realptpMap.get("RELATED_EMS_CUID").toString());
//						newRele.setTpFdn(vTpFdn);
//						newRele.setRelatedVirtuallineFdn(virtualLine.getFdn());
//						replatedTpList.add(newRele);
//					}
//				}
//				for (int i = 0; i < replatedTpList.size(); i++) {
//					//设备与伪线的关系单独记录，不和与隧道的关系进行混合记忆.
//					PtnNeToService devPort = (PtnNeToService) replatedTpList.get(i).deepClone();
//					//PtnNeToService的所有属性
////					public static final String relatedEmsCuid = "RELATED_EMS_CUID";
////			        public static final String relatedNeCuid = "RELATED_NE_CUID";
////					public static final String relatedCardCuid = "RELATED_CARD_CUID";
////					public static final String relatedPtpCuid = "RELATED_PTP_CUID";
////					public static final String relatedTunnelFdn = "RELATED_TUNNEL_FDN";
////					public static final String relatedVirtuallineFdn = "RELATED_VIRTUALLINE_FDN";
////					public static final String relatedServiceFdn = "RELATED_SERVICE_FDN";
////					public static final String tpFdn = "TP_FDN";
////					public static final String relatedTraphCuid = "RELATED_TRAPH_CUID";
////					public static final String objectTypeCode = "OBJECT_TYPE_CODE";
////					public static final String cuid = "CUID";
//					Map devPortMap=new HashMap();
//				    //devPort.setRelatedTunnelFdn("");
//					devPort.setRelatedVirtuallineFdn(virtualLine.getFdn());
//					devPortMap.put("devPortMap", devPort.getObjectId());
//					devPortMap.put("RELATED_EMS_CUID", devPort.getRelatedEmsCuid());
//					devPortMap.put("RELATED_NE_CUID", devPort.getRelatedNeCuid());
//					devPortMap.put("RELATED_CARD_CUID", devPort.getRelatedCardCuid());
//					devPortMap.put("RELATED_PTP_CUID", devPort.getRelatedPtpCuid());
//					devPortMap.put("RELATED_TUNNEL_FDN", devPort.getRelatedTunnelFdn());
//					devPortMap.put("RELATED_VIRTUALLINE_FDN", virtualLine.getFdn());
//					devPortMap.put("RELATED_SERVICE_FDN", devPort.getRelatedServiceFdn());
//					devPortMap.put("TP_FDN", devPort.getTpFdn());
//					devPortMap.put("RELATED_TRAPH_CUID", devPort.getRelatedTraphCuid());
//					devPortMap.put("OBJECT_TYPE_CODE", devPort.getObjectTypeCode());
//					devPortMap.put("CUID", devPort.getCuid());
//					this.IbatisResDAO.insertDynamicTable("PTN_NE_TO_SERVICE", devPortMap);
//					
////					String relEmsCuid=devPortMap.get("RELATED_EMS_CUID").toString();
////					String relNeCuid=devPortMap.get("RELATED_NE_CUID").toString();
////					String relCardCuid=devPortMap.get("RELATED_CARD_CUID").toString();
////					String relPtpCuid=devPortMap.get("RELATED_PTP_CUID").toString();
////					String relTurnnelCuid=devPortMap.get("RELATED_TUNNEL_FDN").toString();
////					String relVirtualLineFdn=virtualLine.getFdn();
////					String relServiceFdn=devPortMap.get("RELATED_SERVICE_FDN").toString();
////					String teFdn=devPortMap.get("TP_FDN").toString();
////					String relTraphCuid=devPortMap.get("RELATED_TRAPH_CUID").toString();
////					String objTypeCode=devPortMap.get("OBJECT_TYPE_CODE").toString();
////					String cuid=devPortMap.get("CUID").toString();
////					String insertPtnNeToService="INSERT INTO PTN_NE_TO_SERVICE (RELATED_EMS_CUID,RELATED_NE_CUID,RELATED_CARD_CUID,RELATED_PTP_CUID,RELATED_TUNNEL_FDN,RELATED_VIRTUALLINE_FDN,RELATED_SERVICE_FDN,TP_FDN,RELATED_TRAPH_CUID,OBJECT_TYPE_CODE,CUID)"+
////					"VALUES ("+relEmsCuid+","+relNeCuid+","+relCardCuid+","+relPtpCuid+","+relTurnnelCuid+","+relVirtualLineFdn+","+relServiceFdn+","+teFdn+","+relTraphCuid+","+objTypeCode+","+cuid+")";
	//  
//					
//				}
//			}catch(Exception e){
//				LogHome.getLog().error("记录伪线查询数据出错",e);
//			}
			
			//创建隧道伪线关联关系
			String virtualLineCuid = "";
			virtualLineCuid = virtualLineRecod.getColValue("CUID").toString();
			String QosBand = (String) pathData.getDataString("QOS_BAND");
			String CirBand = (String) pathData.getDataString("CIR_BAND");
			String PirBand = (String) pathData.getDataString("PIR_BAND");
			
			TunnelToVirtualLine tnelToLine = new TunnelToVirtualLine();
			tnelToLine.setRelatedTunnelCuid(ptnTurnnelMap.get("CUID").toString());
			tnelToLine.setRelatedVirtualLineCuid(virtualLineCuid);
			tnelToLine.setQosBand(QosBand);
			tnelToLine.setCirBand(CirBand);
			tnelToLine.setPirBand(PirBand);
			tnelToLine.setIsBackup(false);
			tnelToLine.setOrderNum(0);
			
			//PtnTurnnel的所有属性
//			public static final String relatedTunnelCuid = "RELATED_TUNNEL_CUID";
//			public static final String relatedVirtualLineCuid = "RELATED_VIRTUAL_LINE_CUID";
//			public static final String orderNum = "ORDER_NUM";
//			public static final String isBackup = "IS_BACKUP";
//			public static final String qosBand = "QOS_BAND";
//			public static final String cirBand = "CIR_BAND";
//			public static final String pirBand = "PIR_BAND";
//			public static final String isMulitVir = "IS_MULIT_VIR";
//			public static final String virOrderNum = "VIR_ORDER_NUM";
//			public static final String virType = "VIR_TYPE";
//			public static final String vlan = "VLAN";
//			public static final String address = "ADDRESS";
//			public static final String qos = "QOS";
//			public static final String cir = "CIR";
//		    public static final String pir = "PIR";
//			public static final String pwProtectGroup = "PW_PROTECT_GROUP";
//			public static final String relatedPwCuid = "RELATED_PW_CUID";
//			public static final String labelCn = "LABEL_CN";
//			public static final String cuid = "CUID";	
			
//			Map tnelToLineMap=new HashMap();
//			tnelToLineMap.put("OBJECTID", tnelToLine.getObjectId());
//			tnelToLineMap.put("RELATED_TUNNEL_CUID", tnelToLine.getRelatedTunnelCuid());
//			tnelToLineMap.put("RELATED_VIRTUAL_LINE_CUID", tnelToLine.getRelatedVirtualLineCuid());
//			tnelToLineMap.put("QOS_BAND", tnelToLine.getQosBand());
//			tnelToLineMap.put("CIR_BAND", tnelToLine.getCirBand());
//			tnelToLineMap.put("PIR_BAND", tnelToLine.getPirBand());
//			tnelToLineMap.put("IS_BACKUP", tnelToLine.getIsBackup());
//			tnelToLineMap.put("ORDER_NUM", tnelToLine.getOrderNum());					
//			this.IbatisResDAO.insertDynamicTable("TUNNEL_TO_VIRTUAL_LINE", tnelToLineMap);
				
			Record tnelToLineRecod = new Record("TUNNEL_TO_VIRTUAL_LINE");
			tnelToLineRecod.addColSqlValue("OBJECTID", virtualLineRecod.getObjectIdSql());
			tnelToLineRecod.addColValue("CUID", CUIDHexGenerator.getInstance().generate("TUNNEL_TO_VIRTUAL_LINE"));
			tnelToLineRecod.addColValue("RELATED_TUNNEL_CUID", tnelToLine.getRelatedTunnelCuid());
			tnelToLineRecod.addColValue("RELATED_VIRTUAL_LINE_CUID", tnelToLine.getRelatedVirtualLineCuid());
			tnelToLineRecod.addColValue("QOS_BAND", tnelToLine.getQosBand());
			tnelToLineRecod.addColValue("CIR_BAND", tnelToLine.getCirBand());
			tnelToLineRecod.addColValue("PIR_BAND", tnelToLine.getPirBand());
			tnelToLineRecod.addColValue("IS_BACKUP", tnelToLine.getIsBackup());
			tnelToLineRecod.addColValue("ORDER_NUM", tnelToLine.getOrderNum());			
			this.IbatisResDAO.insertDynamicTable(tnelToLineRecod);
		}
	}
	
	public int getMaxLineByTurnnel(String cuid) throws Exception{
    	String sql = "SELECT COUNT(CUID) AS NUM FROM TUNNEL_TO_VIRTUAL_LINE WHERE RELATED_TUNNEL_CUID ='"+cuid+"'";
    	List<Map<String, Object>> maxLineList = this.IbatisResDAO.querySql(sql);
    	int num=0;
    	num=Integer.valueOf(maxLineList.get(0).get("NUM").toString());
    	return num;
    }
	
	private PtnVirtualLine dealPtnVirtualLine(PtnVirtualLine virtualLine,String[] turnnelArr) throws Exception{
		String name = getLineName(virtualLine.getOrigNeCuid(),virtualLine.getDestNeCuid());
		virtualLine.setLabelCn(name);
		
		String wholeArr[] = getLineWholeNum();
		if (turnnelArr != null) {
			try{
				virtualLine.setWholeLineName(turnnelArr[1].split("-")[0].replace("TMP", "TMC")+"-"+Long.parseLong(turnnelArr[2])+1);
				virtualLine.setWholeLineNo(Long.parseLong(turnnelArr[2])+1);
			}catch(Exception ex){
				LogHome.getLog().error("伪线编号出错:"+ex.getMessage());
	            throw new Exception(ex);
			}
		}
		return virtualLine;
		
	}
	
	private String getLineName(String sNeCuid,String zNeCuid) throws Exception{
		String sql ="SELECT COUNT(CUID) AS NUM FROM PTN_VIRTUAL_LINE WHERE ORIG_NE_CUID ='" + sNeCuid + "' AND DEST_NE_CUID ='" + zNeCuid + "' OR ORIG_NE_CUID ='" + zNeCuid + "' AND DEST_NE_CUID ='" + sNeCuid + "'";
		List<Map<String, Object>> maxLineList = this.IbatisResDAO.querySql(sql);
    	int num=0;
    	num=Integer.valueOf(maxLineList.get(0).get("NUM").toString());

		String sNeName = getElementNameByCuid(sNeCuid);
		String zNeName = getElementNameByCuid(zNeCuid);
		String orderNum = (num+1)+"";
		String name = "TMC-" + sNeName + "——" + zNeName + "-" + orderNum ;
		return name;
	}
	
	private String getElementNameByCuid(String cuid) throws Exception{
		String neName = "";
		String queryLabelCN = "SELECT LABEL_CN FROM TRANS_ELEMENT WHERE CUID ='" + cuid + "'";
		List<Map<String, Object>> labelCNList = this.IbatisResDAO.querySql(queryLabelCN);
    	String labelCN=null;
    	if(labelCNList.size()>0){
    		labelCN=(String) labelCNList.get(0).get("LABEL_CN");
    	}
		return labelCN;
	}
	
	private String[] getLineWholeNum() throws Exception{
    	String arr[] = new String[2];
    	String queryNum = "SELECT MAX(WHOLE_LINE_NO) AS NUM FROM PTN_VIRTUAL_LINE";
    	List<Map<String, Object>> wholeLineNoList = this.IbatisResDAO.querySql(queryNum);
    	int num=0;
    	num=Integer.valueOf(wholeLineNoList.get(0).get("NUM").toString());

    	int no = num + 1;
    	arr[0] = no + "";
    	
    	String zeroStr = "";
    	for(int i=arr[0].length();i<8;i++){
    		zeroStr = zeroStr + "0";
    	}
    	arr[1] = "TMC"+zeroStr+arr[0];
    	return arr;
    }
	
	public static String getPortFdnByStr(String souFdn){
	    if (souFdn == null) {
	        return null;
	    }
	    int ptpPlace = souFdn.indexOf("PTP=");
	    int ftpPlace = souFdn.indexOf("FTP=");
	    if ((ptpPlace < 0) && (ftpPlace < 0)) {
	        return null;
	    }
	    String neFdn = getNeFdnByStr(souFdn);
	    String tmpFdn = souFdn.substring(neFdn.length());
	    String[] fdnBuf = tmpFdn.split(":");
	    StringBuffer portFdnBuffer = new StringBuffer();
	    if (fdnBuf.length >= 2) {
	        portFdnBuffer.append(neFdn);
	        portFdnBuffer.append(":");
	        portFdnBuffer.append(fdnBuf[1]);
	    }else{
	        portFdnBuffer.setLength(0);
	    }
	    return portFdnBuffer.toString();
    }
	
	public static String getNeFdnByStr(String souFdn){
		if (souFdn == null) {
			return null;
		}
		int nePlace = souFdn.indexOf("ManagedElement=");
		String[] fdnBuf = souFdn.split(":");
		if (nePlace < 0) {
			return null;
		}
		String neFdn = new StringBuilder().append(fdnBuf[0]).append(":").append(fdnBuf[1]).toString();
		try {
			if (fdnBuf.length > 2) {
				if (fdnBuf[2].indexOf("--") > 0) {
					neFdn = new StringBuilder().append(neFdn).append(":").append(fdnBuf[2]).toString();
				} else if (new Long(fdnBuf[2]).longValue() > -1L) {
					neFdn = new StringBuilder().append(neFdn).append(":").append(fdnBuf[2]).toString();
				}
			}
		}catch (Exception e){
		}
		return neFdn;
	}
	public  boolean isNotEmpty(String str) {
		return ((str != null) && (str.trim().length() > 0));
	}
	protected Map<String, Object> getPtpByFdn(String portFdn){
		String queryPtp = "SELECT * FROM PTP WHERE FDN='" + portFdn + "'";
		List<Map<String, Object>> ptpList = this.IbatisResDAO.querySql(queryPtp);
		Map<String, Object> ptpMap=new HashMap();
		if(ptpList.size()>0){
			ptpMap=ptpList.get(0);
		} else {
			ptpMap=null;
		}
		return ptpMap;
	}
	protected Map<String,Object> getTunnelVirtualLine(String tunnelCuid,String virtualLineCuid){
		Map pm=new HashMap();
		pm.put("tunnelCuid", tunnelCuid);
		pm.put("virtualLineCuid", virtualLineCuid);
		List<Map<String,Object>> VirtualLineList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTunnelVirtualLine", pm);
		Map<String, Object> virtualLineMap=new HashMap();
		if(!VirtualLineList.isEmpty()&&VirtualLineList.size()>0){
			virtualLineMap=VirtualLineList.get(0);
		} else {
			virtualLineMap=null;
		}
		return virtualLineMap;
	}

	public void updateIpNumType(List<Map<String, Object>> relatedNumberIps){
		Map pm = new HashMap();
		if (relatedNumberIps.size()==1) {
			pm.put("relatedNumberIp", IbatisDAOHelper.getStringValue(relatedNumberIps.get(0), "RELATED_NUMBER_IP_CUID"));
			pm.put("count", 1);
		}else if (relatedNumberIps.size()==2){
			String cuid1 = IbatisDAOHelper.getStringValue(relatedNumberIps.get(0), "RELATED_NUMBER_IP_CUID");
			String cuid2 = IbatisDAOHelper.getStringValue(relatedNumberIps.get(1), "RELATED_NUMBER_IP_CUID");
			if (!cuid1.equals(cuid2)) {
				pm.put("relatedNumberIp",cuid1);
				pm.put("count", 1);
				Map pm1 = new HashMap();
				pm1.put("relatedNumberIp",cuid2);
				pm1.put("count", 1);
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateIpNumType",pm1);
			} else {
				pm.put("relatedNumberIp",cuid1);
				pm.put("count", 2); 
			}
		}
		this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateIpNumType",pm);
	}
	
	public void updateLogicIpType(String traphId){
		String sql = "SELECT T.RELATED_NUMBER_IP_CUID FROM T_ATTEMP_PTN_PATH_TO_IP T,ATTEMP_PTN_PATH S " +
		         "WHERE T.RELATED_PTN_PATH_CUID = S.CUID AND S.RELATED_ROUTE_CUID = '" + traphId +"'";
		List<Map<String, Object>> relatedNumberIps = this.IbatisResDAO.querySql(sql);
		List<Map> pmList = new ArrayList<Map>();
		this.getUpdateNumType(relatedNumberIps, pmList);
		if(pmList != null && pmList.size()>0){
			for(Map pm : pmList){
				this.IbatisResDAO.getSqlMapClientTemplate().update(sqlMap+".updateIpNumType",pm);
			}
		}
	}
	
	public List<Map> getUpdateNumType(List<Map<String, Object>> relatedNumberIps, List<Map> pmList){
		if(relatedNumberIps != null && relatedNumberIps.size()>0){
			int count=0;
			String numberIpCuid = IbatisDAOHelper.getStringValue(relatedNumberIps.get(0), "RELATED_NUMBER_IP_CUID");
			//存放临时数据
			List<Map<String, Object>> tempCuidsList = new ArrayList<Map<String, Object>>();
			for(int i = 0; i < relatedNumberIps.size(); i++){
				if(numberIpCuid.equals(IbatisDAOHelper.getStringValue(relatedNumberIps.get(i), "RELATED_NUMBER_IP_CUID"))){
					count++;
				}else{
					tempCuidsList.add(relatedNumberIps.get(i));
				}
			}
			Map pm = new HashMap();
			pm.put("relatedNumberIp", numberIpCuid);
			pm.put("count", count);
			pmList.add(pm);
			if(tempCuidsList != null && tempCuidsList.size()>0){
				getUpdateNumType(tempCuidsList, pmList);
			}
		}
		return pmList;
	}
	
	public List getMacNe(HttpServletRequest request, String attempDgnSegCuid) {
		Map pm = new HashMap();
		pm.put("attempDgnSegCuid", attempDgnSegCuid);
		List<Map<String, Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getMacNeCuid",pm);
		return list;
	}
	private long getTraphCheckFlag(List<String> cuidList) {
		long checkflag = 1L;
		Map pm = new HashMap();
		pm.put("cuidList", cuidList);
		List<Map<String,Object>> transPathList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTransPathsByCuids", pm);
		if(transPathList.size() > 0){
	        int flag1 = 0;
	        int flag2 = 0;
	        int flag3 = 0;
	        for (int i = 0; i < transPathList.size(); i++) {
	        	long pathmakeFlag = IbatisDAOHelper.getLongValue(transPathList.get(i), "PATHMAKE_FLAG");
	        	//对通道核查状态进行处理 1、待核查 2、成功 3、失败
	        	switch ((int) pathmakeFlag) {
	        		case 1:
	        			flag1++;
	        			continue;
	        		case 2:
	        			flag2++;
	        			continue;
	        		case 3:
	        			flag3++;
	        			continue;
	        	}
	        }
	        //电路对应通道中包含核查失败的通道时，电路的核查标志为“失败”；
	        //电路对应通道中不包含核查失败的通道但包含有待核查通道时，电路的核查标志为“待核查”
	        //电路对应通道全部核查成功时，电路的核查标志为“成功”；
	        if (flag3 > 0) {
	        	checkflag = 3;
	        } else if (flag1 > 0) {
	        	checkflag = 1;
	        } else if (flag2 > 0) {
	        	checkflag = 2;
	        }
		}
		return checkflag;
	}
   

   public void isMultDesigned(String aSiteCuid,String zSiteCuid,List<String> relatedDistrictCuidList,String taskDefId){
	    List<String> siteList =new ArrayList<String>();
	    if(aSiteCuid != null){
	    	siteList.add(aSiteCuid);
	    }
	    if(zSiteCuid != null){
	    	 siteList.add(zSiteCuid);
	    }
	    logger.info("<!-----------------aSiteCuid------------------->:"+aSiteCuid);
	    logger.info("<!-----------------zSiteCuid------------------->:"+zSiteCuid);
	    Map pm=new HashMap();
	    pm.put("siteList", siteList);
	    List<Map<String,Object>> districtList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getDistrictBySite", pm);
	    if(districtList.size()==2){
        	String aDistrictCuid=IbatisDAOHelper.getStringValue(districtList.get(0), "RELATED_SPACE_CUID");
        	String zDistrictCuid=IbatisDAOHelper.getStringValue(districtList.get(1), "RELATED_SPACE_CUID");
        	if (taskDefId.equalsIgnoreCase("multDesignTask")){
        		if(aDistrictCuid.length()<26 || zDistrictCuid.length()<26){
            		if(!aDistrictCuid.substring(0, aDistrictCuid.length()).equals(zDistrictCuid.substring(0, zDistrictCuid.length()))){
            			throw new RuntimeException("二干设计人员设计的路由不允许地市人员进行修改");
            		}
            	}else if(!aDistrictCuid.substring(0, 26).equals(zDistrictCuid.substring(0, 26))){
            		throw new RuntimeException("二干设计人员设计的路由不允许地市人员进行修改");
            	}else {
            		boolean aflag = false;
            		boolean zflag = false;
            		for (String relatedDistrictCuid : relatedDistrictCuidList){
            			if (aDistrictCuid.startsWith(relatedDistrictCuid)){
            				aflag = true;
            			}
            			if (zDistrictCuid.startsWith(relatedDistrictCuid)){
            				zflag = true;
            			}
            		}
            		if(!aflag || !zflag){
                		throw new RuntimeException("非本地市端路由，不允许修改！");
                	}
            	}
        	}else{
        		boolean aflag = false;
        		boolean zflag = false;
        		for (String relatedDistrictCuid : relatedDistrictCuidList){
        			if (aDistrictCuid.startsWith(relatedDistrictCuid)){
        				aflag = true;
        			}
        			if (zDistrictCuid.startsWith(relatedDistrictCuid)){
        				zflag = true;
        			}
        		}
        		if(!aflag || !zflag){
            		throw new RuntimeException("非本地市端路由，不允许修改！");
            	}
        	}
        	
        }
   }
   public String  getVlanCuid(String cuid){
	   String sql = "SELECT * FROM TUNNEL_TO_VIRTUAL_LINE WHERE RELATED_TUNNEL_CUID='"+cuid+"'";
		List<Map<String, Object>> vlanList = this.IbatisResDAO.querySql(sql);	
		Map<String, Object>vlanmap=vlanList.get(0);
		String a = (String) vlanmap.get("RELATED_VIRTUAL_LINE_CUID");
		return a;
   }
   
   /**
	 * mstp端口调度路由描述拼写
	 * @param paths
	 * @return
	 */
   private String generatePathDescMindle(Map<Integer,String> ctps) {
	   StringBuffer result = new StringBuffer();
	   logger.info("------------------------ctps.size="+ctps.size());
		List<Map<String,Integer>> minMaxList = this.groupNumbers(ctps.keySet());
		logger.info("------------------------minMaxList.size="+minMaxList.size());
		//根据分组处理时隙
		for(int i =0,size =minMaxList.size();i<size;i++){
			Map<String,Integer> map = minMaxList.get(i);
			int min = map.get("min");
			int max = map.get("max");
			int count = max-min;
			if(count==0){
				result.append(",").append(ctps.get(min));
			}else if(count==1){
				result.append(",").append(ctps.get(min)).append("~").append(ctps.get(max));
			}else {
				result.append(ctps.get(min)).append("~").append(max);
			}
		}
		return result.toString();
   }
	private String generatePathDesc(Map<String, Map<Integer,String>> paths) {
		StringBuffer result = new StringBuffer();
		int i=0;
		for(String ptp:paths.keySet()){
			if(StringUtils.isBlank(ptp)){
				continue;
			}
			if(i==0) 
				result.append(ptp);
			else 
				result.append(",").append(ptp);
			Map<Integer,String> ctps = paths.get(ptp);
			List<Map<String,Integer>> minMaxList = this.groupNumbers(ctps.keySet());
			//根据分组处理时隙
			if(ctps!=null&&!ctps.isEmpty()){
				Map<String,Integer> map = minMaxList.get(0);
				int min = map.get("min");
				result.append("/").append(ctps.get(min));
			}
			i++;
		}
		return result.toString();
	}
	/**
	 * 根据传入的数字进行相邻的数字分组
	 * @param numberSet
	 * @return
	 */
	private List<Map<String,Integer>> groupNumbers(Set<Integer> numberSet){
		//排序
		List<Integer> numbers = new ArrayList<Integer>();
		numbers.addAll(numberSet);
		Collections.sort(numbers);
		//分组
		List<Map<String,Integer>> minMaxList = new ArrayList<Map<String,Integer>>();
		int linkSize = 1;
		Map<String,Integer> linkMap = null;
		for(int a=0 ; a < numbers.size() ; a++){
			Integer ctpNo = numbers.get(a);
			if(a==0){
				linkMap = new HashMap<String, Integer>();
				linkMap.put("min", ctpNo);
				linkMap.put("max", ctpNo);
				if(numbers.size() == 1) {
					minMaxList.add(linkMap);
				}
			}else{
				Integer prevCtpNo = numbers.get(a-1);
				if(prevCtpNo == (ctpNo-1)){
					linkSize ++;
				}else{
					linkSize = 1;
				}
				if(linkSize>1){
					linkMap.put("max", ctpNo);
					if(a==numbers.size()-1){
						minMaxList.add(linkMap);
					}
				}else{
					linkMap.put("max", prevCtpNo);
					minMaxList.add(linkMap);
					linkMap = new HashMap<String, Integer>();
					linkMap.put("min", ctpNo);
					if(a==numbers.size()-1){
						linkMap.put("max", ctpNo);
						minMaxList.add(linkMap);
					}
				}
			}
		}
		return minMaxList;
	}
	
	/**
	 * 根据LSP保护组查询主备隧道(LSP模型定义后，提交脚本以及更新xml中的SQL配置)
	 * @param origNeCuid	源网元CUID
	 * @param destNeCuid	宿网元CUID
	 * @return	
	 */
    @SuppressWarnings("deprecation")
	public List<Map<String, Object>> selectTurnnelByLsp(String origNeCuid,String destNeCuid) 
    {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("origNeCuid", origNeCuid);
		map.put("destNeCuid", destNeCuid);
		List<Map<String, Object>> list = this.IbatisResDAO
				.getSqlMapClientTemplate().queryForList(
						sqlMap + ".selectTurnnelByLsp", map);
    	return list;
    }
    public List<Map<String, Object>>  getTraphNameList (String ptpCuid){

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ptpCuid", ptpCuid);
		List<Map<String, Object>> list = this.IbatisResDAO
		.getSqlMapClientTemplate().queryForList(
				sqlMap + ".getTraphNameList", map);
    	return list;
    }
    
    public List<Map<String, Object>>  getUsed155MTraph (String ptpCuid){

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ptpCuid", ptpCuid);
		List<Map<String, Object>> list = this.IbatisResDAO
		.getSqlMapClientTemplate().queryForList(
				sqlMap + ".getUsed155MTraph", map);
    	return list;
    }
	
    public boolean validateIpUsed(Map<String, String> ptnLte){
		String ipValue = IbatisDAOHelper.getStringValue(ptnLte, "BUSINESS_IP_NAME");
		logger.info("==============ipValue="+ipValue);
		if(!StringUtils.isEmpty(ipValue)){
			Map pm = new HashMap();
			pm.put("ipValue", String.valueOf(ipValue));
			List<Map<String, Object>> existsIpList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".validateIpState", pm);
			if(existsIpList ==null || existsIpList.isEmpty()){
				return true;
			}
		}
		return false;
	}
    
    public List<Map<String,Object>> getTransElementByAll(Map map){
		List<Map<String,Object>> transElementList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getTransElementByAll", map);
		return transElementList;
	}
    
    public List<Map<String,Object>> getSignalTypeByPtpOrCtp(Map map){
    	List<Map<String,Object>> list = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap+".getSignalTypeByPtpOrCtp", map);
		return list;
    }
}
