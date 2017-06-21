package com.boco.flow.common.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.message.CachedDtoMessage;
import com.boco.core.message.MsgBusManager;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.flow.model.SheetInst;
import com.boco.flow.model.TaskInst;
import com.boco.flow.pojo.SeqLink;
import com.boco.maintain.device.bo.Property;
import com.boco.sys.sms.bo.SmsManagerBO;
import com.boco.transnms.common.dto.base.GenericDO;
/**
广西
*/
@SuppressWarnings("unchecked")
public class SendMessageBO {
	private Logger logger = LoggerFactory.getLogger("IFACE_LOG");

	private static final String sqlMap = "SendMessage";

	protected  IbatisDAO IbatisResDAO;
	
	private SmsManagerBO smsManagerBO;
	
	public SmsManagerBO getSmsManagerBO() {
		return smsManagerBO;
	}
	public void setSmsManagerBO(SmsManagerBO smsManagerBO) {
		this.smsManagerBO = smsManagerBO;
	}
	public void setIbatisResDAO(IbatisDAO ibatisResDAO) {
		IbatisResDAO = ibatisResDAO;
	}
	public IbatisDAO getIbtisResDAO(){
		return IbatisResDAO;
	}
	public void sendMessageToMq(CachedDtoMessage.DTO_MSG_TYPE type,String topic,List<GenericDO> list){
		CachedDtoMessage cachedDtoMsg = new CachedDtoMessage(topic, type);
        cachedDtoMsg.setMsgDtos(list);
        cachedDtoMsg.setSourceName("TNMS");
		MsgBusManager.getInstance().sendMessage(cachedDtoMsg);
	}
	public void sendMessage(SheetInst sheetInst, TaskInst oldTask,List<TaskInst> newTaskList) {
		if ("true".equals(SysProperty.getInstance().getValue("sendMessage"))) {
			Map<String, Object> pm = new HashMap<String, Object>();
			List<Map<String, Object>> userMapList =new ArrayList<Map<String, Object>>();
			List<String> roleIdList = new ArrayList<String>();
			String sheetState="";
			String taskCfgCode="";
			String sheetCode = sheetInst.getSheetCfg().getSheetCode();
			for (TaskInst task : newTaskList) {
				SeqLink seq=task.getSeqLink();
				sheetState=task.getLabelCn();
				taskCfgCode = task.getTaskCfg().getTaskCode();
				pm.put("relatedTaskId", task.getCuid());
				if(sheetState.equals("电路调度流程")){
					sheetState="调单设计";
					sheetCode = "TRAPH";
				}else if(sheetState.equals("光路调度流程")){
					sheetState="调单设计";
					sheetCode = "OPTICAL_TRAPH";
				}
				pm.put("relatedDistrictCuid", sheetInst.getRelatedDistrictCuid());
				pm.put("relatedRel", sheetCode+'.'+taskCfgCode);
				
				if(StringUtils.isNotEmpty(task.getAssignee())){
					List<String>userList=new ArrayList<String>();
					if(oldTask==null||!task.getAssignee().equals(oldTask.getAssignee())){
					    userList.add(task.getAssignee());      
					}
					if(userList.size()!=0){
						pm.put("userId", task.getAssignee());
					    userMapList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getSmsinfoUserByName", pm);
					    pm.put("userNames", userList);    
					    roleIdList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getRoleIdByName", pm);
					    userMapList = smsManagerBO.sendFilter(sheetInst,userMapList, roleIdList);
					    insertSendMessage(sheetInst,sheetState,userMapList);
				    }
				}else if(seq.getUserList().size()!=0){
					List<String>userList=new ArrayList<String>();
					for(String user:seq.getUserList()){
					    if(oldTask==null||!user.equals(oldTask.getAssignee())){
							  userList.add(user);      
						}  
					}
			       if(userList.size()!=0){
					  pm.put("userNames", userList);
					  userMapList=this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getSmsinfoUserByUserName", pm);
					  roleIdList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getRoleIdByName", pm);
					  userMapList = smsManagerBO.sendFilter(sheetInst,userMapList, roleIdList);
					  insertSendMessage(sheetInst,sheetState,userMapList);
				    } 
				}else if(seq.getRoleList().size()!=0&&seq.getUserList().size()==0){
					if ("施工验证".equals(sheetState)&&SysProperty.getInstance().getValue("districtName").trim().equals("河南")) {//河南需求：施工验证阶段，当选择为部门时，短信只发给调单设计人，而不是部门下拥有施工验证权限的人。
						String cuid = task.getSheetInst().getCuid();
						pm.put("sheetCuid", cuid);
						userMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getSmsinfoUserBySheetCuid",pm);
						insertSendMessage(sheetInst, sheetState, userMapList);

					} else {
						roleIdList = new ArrayList<String>();
						for (String seqKey : seq.getRoleList()) {
							if(!roleIdList.contains(seqKey)){
								roleIdList.add(seqKey);
							}
						}
						pm.put("roleIdList", roleIdList);
						userMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getSmsinfoUserByRoleId",pm);
						userMapList = smsManagerBO.sendFilter(sheetInst,userMapList, roleIdList);
						insertSendMessage(sheetInst, sheetState,userMapList);
						
					}
				}
			}
		}
	}
	/**
	 * 用于综资申请单在传输侧生成订单是短信提醒
	 * @param sheetInst
	 */
	public void sendMessage(SheetInst sheetInst) {
		Map<String, Object> pm = new HashMap<String, Object>();
		List<Map<String, Object>> userMapList = new ArrayList<Map<String, Object>>();
		if ("true".equals(SysProperty.getInstance().getValue("sendMessage"))) {
			String sheetState ="方案制作审批";
			pm.put("userId", sheetInst.getCreator());
			userMapList = this.IbatisResDAO.getSqlMapClientTemplate().queryForList(sqlMap + ".getSmsinfoUserByName", pm);
			if(userMapList != null && userMapList.size()>0){
				insertSendMessage(sheetInst,sheetState,userMapList);
			}
		}
	}
	
	
	public void insertSendMessage(SheetInst sheetInst,String sheetState,List<Map<String, Object>> userMapList) {
		Date now = new Date();
		Map<String, Object> map = new HashMap<String, Object>();				
		map.put("relatedSheetId", sheetInst.getCuid());
		String rootSheetNo=(String) this.IbatisResDAO.getSqlMapClientTemplate().queryForObject(sqlMap + ".getSheetNo", map);
		List<Record> parentRecords = new ArrayList<Record>();
		StringBuffer msg = new StringBuffer();
		Map<String, Object> modelMap = new HashMap<String, Object>(); 
		modelMap = Property.getIsModelOne(modelMap);
		//如果是综资申请的订单并且任务状态为审批（综资单子到达传输）
		if(StringUtils.isNotEmpty(sheetInst.getRelatedOrderCuid())&&sheetInst.getRelatedOrderCuid().startsWith("IRMS") && "方案制作审批".equals(sheetState)){
			msg.append(rootSheetNo + sheetInst.getLabelCn() + "已到达亿阳综合资源传输调度，请及时处理！");
		} else {
//			if(null==modelMap.get("model")) 
//			{
				msg.append("您有待处理的调度工单， 调度单号："+ rootSheetNo + " 工单标题：" + sheetInst.getLabelCn()+ " 状态："+sheetState);
//			}else 
//			{
//				//如果是模式1
//				msg.append("您好，【"+ rootSheetNo + "】+【" + sheetInst.getLabelCn()+ "】工单已派发至您处，烦请尽快登录综合资源传输调度平台处理，谢谢！");
//			}
		}
		for (Map<String, Object> userMap : userMapList) {
			String mobilePhone = IbatisDAOHelper.getStringValue(userMap,
					"MOBILE_PHONE");
			if (StringUtils.isNotBlank(mobilePhone)) {
				Record record = new Record("SMSINFO");
				record.addColSqlValue("OBJECTID", record.getObjectIdSql());
				record.addColValue("CUID", CUIDHexGenerator.getInstance()
						.generate(record.getTableName()));
				record.addColValue("MSG", msg.toString());
				record.addColValue("MOBILEUSER", IbatisDAOHelper
						.getStringValue(userMap, "TRUE_NAME"));
				record.addColValue("MOBILECODE", mobilePhone);
				record.addColValue("MSGSTATE", 1);
				record.addColValue("MSG_TYPE", 2);
				record.addColValue("ISDELETE", 0);
				record.addColValue("CREATE_TIME", now);
				record.addColValue("LAST_MODIFY_TIME", now);
				record.addColValue("INTIME", now);
				parentRecords.add(record);
			}
		}
		this.IbatisResDAO.insertDynamicTableBatch(parentRecords);
	}
}