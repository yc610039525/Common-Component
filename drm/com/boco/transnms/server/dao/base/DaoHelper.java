/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.NetHelper;
/*     */ import com.boco.common.util.lang.StringHelper;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.raptor.common.message.MsgBusManager;
/*     */ import com.boco.transnms.common.dto.SystemLog;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.FromToDO;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import com.boco.transnms.server.common.cfg.TnmsServerName;
/*     */ import com.boco.transnms.server.dao.base.internal.ClassUtils;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DaoHelper
/*     */ {
/*  27 */   public static String HOST_IP = NetHelper.getHostIP();
/*  28 */   public static String HOST_NAME = NetHelper.getHostName();
/*  29 */   private static DaoHelper instance = null;
/*     */ 
/*     */   public static synchronized DaoHelper getInstance()
/*     */   {
/*  34 */     if (instance == null) {
/*  35 */       instance = new DaoHelper();
/*     */     }
/*  37 */     return instance;
/*     */   }
/*     */ 
/*     */   public static boolean isNotEmpty(String str) {
/*  41 */     return (str != null) && (str.trim().length() > 0);
/*     */   }
/*     */ 
/*     */   public static void exportClassData(GenericDO dboTemplate) {
/*     */     try {
/*  46 */       DataObjectList list = getGenericDAO().getAllObjByClass(dboTemplate, 0);
/*  47 */       list.writeToXml(dboTemplate.getClassName() + ".xml");
/*     */     } catch (Exception ex) {
/*  49 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void importClassData(GenericDO dboTemplate) {
/*     */     try {
/*  55 */       DataObjectList list = new DataObjectList();
/*  56 */       list.readFromXml(dboTemplate.getClassName() + ".xml");
/*  57 */       getGenericDAO().deleteAll(null, dboTemplate.getClassName());
/*  58 */       getGenericDAO().createObjects(null, list);
/*     */     } catch (Exception ex) {
/*  60 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void importAndExportData(GenericDO dboTemplate) {
/*     */     try {
/*  66 */       String sql = "select * from " + dboTemplate.getClassName();
/*  67 */       DboCollection dbos = getGenericDAO().selectDBOs(sql, new GenericDO[] { dboTemplate });
/*  68 */       getGenericDAO().deleteAll(null, dboTemplate.getClassName());
/*  69 */       for (int i = 0; i < dbos.size(); i++) {
/*  70 */         GenericDO dbo = (GenericDO)dbos.getAttrField(dboTemplate.getClassName(), i);
/*  71 */         LogHome.getLog().info(dbo);
/*  72 */         getGenericDAO().createObject(null, dbo);
/*     */       }
/*     */     } catch (Exception ex) {
/*  75 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static IDataAccessObject getGenericDAO() {
/*  80 */     return DaoHomeFactory.getInstance().getDAO("GenericDAO");
/*     */   }
/*     */ 
/*     */   public static DataObjectList filterRelateObjs(GenericDO dboTemplate, DataObjectList dbos) {
/*  84 */     DataObjectList filteredDbos = dbos;
/*  85 */     if ((dbos != null) && (dbos.size() > 0) && (dboTemplate.isSetClassName())) {
/*  86 */       filteredDbos = new DataObjectList();
/*  87 */       for (int i = 0; i < dbos.size(); i++) {
/*  88 */         if (dboTemplate.getClassName().equals(((GenericDO)dbos.get(i)).getClassName())) {
/*  89 */           filteredDbos.add(dbos.get(i));
/*     */         }
/*     */       }
/*     */     }
/*  93 */     return filteredDbos;
/*     */   }
/*     */ 
/*     */   public static DataObjectList filterRelateLinkObjs(GenericDO fromDboTemplate, GenericDO toDboTemplate, DataObjectList dbos) {
/*  97 */     DataObjectList filteredDbos = dbos;
/*  98 */     if ((dbos != null) && (dbos.size() > 0)) {
/*  99 */       filteredDbos = new DataObjectList();
/* 100 */       for (int i = 0; i < dbos.size(); i++) {
/* 101 */         FromToDO fromTo = (FromToDO)dbos.get(i);
/* 102 */         GenericDO from = fromTo.getFromObject();
/* 103 */         GenericDO to = fromTo.getToObject();
/* 104 */         if (((fromDboTemplate.isSetClassName()) && (!from.getClassName().equals(fromDboTemplate.getClassName()))) || ((toDboTemplate.isSetClassName()) && (!to.getClassName().equals(toDboTemplate.getClassName()))))
/*     */         {
/*     */           continue;
/*     */         }
/* 108 */         filteredDbos.add(fromTo);
/*     */       }
/*     */     }
/*     */ 
/* 112 */     return filteredDbos;
/*     */   }
/*     */ 
/*     */   public static void setLogObjName(GenericDO dbo) {
/* 116 */     if (!TnmsDrmCfg.getInstance().isWriteSystemLog()) {
/* 117 */       return;
/*     */     }
/* 119 */     String objName = "";
/*     */     try {
/* 121 */       if ((dbo.getObjectNum() != 0L) && (dbo.getAttrString("LABEL_CN") == null)) {
/* 122 */         IDataAccessObject dao = DaoHomeFactory.getInstance().getDAO("GenericObjectDAO");
/* 123 */         GenericDO simpleDbo = new GenericDO(dbo.getClassName());
/* 124 */         simpleDbo.setObjectNum(dbo.getObjectNum());
/* 125 */         simpleDbo = dao.getSimpleObject(simpleDbo);
/* 126 */         if (simpleDbo.getAttrString("LABEL_CN") != null) {
/* 127 */           objName = simpleDbo.getAttrString("LABEL_CN");
/* 128 */           dbo.setAttrValue("LABEL_CN", objName);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeSystemLog(IBoActionContext actionContext, String methodName, Object operObj, String desc) {
/* 137 */     if (!TnmsDrmCfg.getInstance().isWriteSystemLog())
/* 138 */       return;
/*     */     try
/*     */     {
/* 141 */       if ((actionContext != null) && (actionContext.getUserName() != null) && (actionContext.getUserName().length() > 0)) {
/* 142 */         SystemLog log = new SystemLog();
/* 143 */         log.setCuid();
/* 144 */         log.setOperateType(getOperTypeByMethod(methodName));
/* 145 */         log.setDesciption(desc);
/* 146 */         log.setCreateTime(new Timestamp(System.currentTimeMillis()));
/* 147 */         if ((operObj instanceof GenericDO)) {
/* 148 */           GenericDO dbo = (GenericDO)operObj;
/* 149 */           if (("SYSTEM_LOG".equals(dbo.getClassName())) || ("LOGIN_LOG".equals(dbo.getClassName()))) {
/* 150 */             return;
/*     */           }
/* 152 */           String classId = StringHelper.nullToEmpty(dbo.getClassName());
/* 153 */           if (classId.equals("DrmDataObject"))
/* 154 */             classId = dbo.getBmClassId();
/* 155 */           log.setLogClassName(getTableNameCn(classId));
/* 156 */           if (dbo.getAttrString("LABEL_CN") != null)
/* 157 */             log.setObjectName(StringHelper.nullToEmpty(dbo.getAttrString("LABEL_CN")));
/*     */           else
/* 159 */             log.setObjectName("未知对象");
/*     */         }
/* 161 */         else if ((operObj instanceof DataObjectList)) {
/* 162 */           DataObjectList dbos = (DataObjectList)operObj;
/* 163 */           String objNames = "";
/* 164 */           for (int i = 0; i < dbos.size(); i++) {
/* 165 */             GenericDO dbo = (GenericDO)dbos.get(i);
/* 166 */             if (i == 0) {
/* 167 */               log.setLogClassName(getTableNameCn(StringHelper.nullToEmpty(dbo.getClassName())));
/*     */             }
/* 169 */             if (dbo.getAttrString("LABEL_CN") != null) {
/* 170 */               String objectName = StringHelper.nullToEmpty(dbo.getAttrString("LABEL_CN"));
/* 171 */               if (objNames.toCharArray().length + objectName.toCharArray().length < 252) {
/* 172 */                 objNames = objNames + StringHelper.nullToEmpty(dbo.getAttrString("LABEL_CN"));
/*     */               } else {
/* 174 */                 objNames = objNames + "...";
/* 175 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 179 */           if (objNames.trim().length() == 0)
/* 180 */             log.setObjectName("对象列表");
/*     */           else
/* 182 */             log.setObjectName(objNames);
/*     */         }
/*     */         else {
/* 185 */           log.setLogClassName("未知类");
/* 186 */           log.setObjectName("未知或批量对象");
/*     */         }
/*     */ 
/* 189 */         if ((actionContext.getHostName() == null) || (actionContext.getHostName().trim().length() == 0))
/* 190 */           log.setMachineName("服务器");
/*     */         else {
/* 192 */           log.setMachineName(actionContext.getHostName());
/*     */         }
/*     */ 
/* 195 */         if ((actionContext.getHostIP() == null) || (actionContext.getHostIP().trim().length() == 0))
/* 196 */           log.setIpAddress(HOST_IP);
/*     */         else {
/* 198 */           log.setIpAddress(actionContext.getHostIP());
/*     */         }
/* 200 */         log.setUserName(actionContext.getUserName());
/* 201 */         SqlExecDaoCmd sqlCmd = SqlDaoCmdFactory.getInstance().createSqlExecCmd();
/* 202 */         sqlCmd.insertDbo(log);
/*     */       }
/*     */     } catch (Exception ex) {
/* 205 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getOperTypeByMethod(String daoMethodName) {
/* 210 */     String operType = "系统";
/* 211 */     if ((daoMethodName.indexOf("insert") >= 0) || (daoMethodName.indexOf("create") >= 0))
/* 212 */       operType = "添加";
/* 213 */     else if (daoMethodName.indexOf("update") >= 0)
/* 214 */       operType = "修改";
/* 215 */     else if (daoMethodName.indexOf("delete") >= 0) {
/* 216 */       operType = "删除";
/*     */     }
/* 218 */     return operType;
/*     */   }
/*     */ 
/*     */   public static String getDispLog(IBoActionContext actionContext, String action, String desc) {
/* 222 */     String dispLog = "DbCall <action=" + action;
/* 223 */     if (actionContext != null) {
/* 224 */       dispLog = dispLog + ", actionContext=" + actionContext.toString();
/*     */     }
/* 226 */     dispLog = dispLog + ">";
/* 227 */     dispLog = dispLog + desc;
/* 228 */     return dispLog;
/*     */   }
/*     */ 
/*     */   public static void printLog(IBoActionContext actionContext, String action, String desc) {
/* 232 */     String dispLog = "DbCall<threadId=" + ThreadHelper.getCurrentThreadId() + "> ";
/* 233 */     dispLog = dispLog + action + ", ";
/* 234 */     dispLog = dispLog + desc;
/* 235 */     LogHome.getLog().info(dispLog);
/*     */   }
/*     */ 
/*     */   public static void printLog(IBoActionContext actionContext, String action, String desc, long expendTime) {
/* 239 */     printLog(actionContext, action, desc, expendTime, 0L);
/*     */   }
/*     */ 
/*     */   public static void printLog(IBoActionContext actionContext, String action, String desc, long expendTime, long expendSize) {
/* 243 */     String threadId = ThreadHelper.getCurrentThreadId();
/* 244 */     String dispLog = "DbCall<threadId=" + threadId + ", ";
/* 245 */     dispLog = dispLog + "dbTime=" + expendTime + "> ";
/* 246 */     dispLog = dispLog + action;
/* 247 */     dispLog = dispLog + desc;
/*     */ 
/* 249 */     String logName = null;
/* 250 */     if ((threadId != null) && (threadId.indexOf("OBJECT_EVENT") >= 0)) {
/* 251 */       logName = "CMCHANGE";
/*     */     }
/*     */ 
/* 254 */     if ((expendTime > TnmsDrmCfg.getInstance().getMaxDbTime()) || (expendSize > TnmsDrmCfg.getInstance().getMaxQueryResultSize()))
/*     */     {
/* 256 */       if (logName != null)
/* 257 */         LogHome.getLog(logName).warn(dispLog);
/*     */       else {
/* 259 */         LogHome.getLog().warn(dispLog);
/*     */       }
/*     */     }
/* 262 */     else if (logName != null)
/* 263 */       LogHome.getLog(logName).info(dispLog);
/*     */     else
/* 265 */       LogHome.getLog().info(dispLog);
/*     */   }
/*     */ 
/*     */   public void notifyClientCacheChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, DataObjectList dtos)
/*     */   {
/* 271 */     String destName = MsgBusManager.getInstance().getClientCacheMsgDestName();
/* 272 */     notifyCacheDataChanges(dtoMsgType, destName, dtos);
/*     */   }
/*     */ 
/*     */   public void notifyClientCacheChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, GenericDO dto) {
/* 276 */     String destName = MsgBusManager.getInstance().getClientCacheMsgDestName();
/* 277 */     notifyCacheDataChange(dtoMsgType, destName, dto);
/*     */   }
/*     */ 
/*     */   public void notifyServerCacheChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, DataObjectList dtos)
/*     */   {
/* 285 */     String destName = MsgBusManager.getInstance().getServerCacheSyncMsgDestName();
/* 286 */     notifyCacheDataChanges(dtoMsgType, destName, dtos);
/*     */   }
/*     */ 
/*     */   public void notifyServerCacheChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, GenericDO dto)
/*     */   {
/* 294 */     String destName = MsgBusManager.getInstance().getServerCacheSyncMsgDestName();
/* 295 */     notifyCacheDataChange(dtoMsgType, destName, dto);
/*     */   }
/*     */ 
/*     */   public void notifySpecObjectChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, DataObjectList dtos)
/*     */   {
/* 311 */     String destName = MsgBusManager.getInstance().getObjectChangedMsgDestName();
/* 312 */     notifySpecDataChanges(dtoMsgType, destName, dtos);
/*     */   }
/*     */ 
/*     */   public void notifySpecObjectChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, GenericDO dto)
/*     */   {
/* 320 */     String destName = MsgBusManager.getInstance().getObjectChangedMsgDestName();
/* 321 */     notifySpecDataChange(dtoMsgType, destName, dto);
/*     */   }
/*     */ 
/*     */   public void notifyObjectChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, DataObjectList dtos) {
/* 325 */     String destName = MsgBusManager.getInstance().getObjectChangedMsgDestName();
/* 326 */     notifyCacheDataChanges(dtoMsgType, destName, dtos);
/*     */   }
/*     */ 
/*     */   public void notifyObjectChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, GenericDO dto) {
/* 330 */     String destName = MsgBusManager.getInstance().getObjectChangedMsgDestName();
/* 331 */     notifyCacheDataChange(dtoMsgType, destName, dto);
/*     */   }
/*     */ 
/*     */   private void notifyCacheDataChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, String topicName, DataObjectList dtos) {
/* 335 */     CachedDtoMessage cachedDtoMsg = new CachedDtoMessage(topicName, dtoMsgType);
/* 336 */     cachedDtoMsg.setMsgDtos(dtos);
/* 337 */     cachedDtoMsg.setSourceName(getCallClassName());
/* 338 */     MsgBusManager.getInstance().sendMessage(cachedDtoMsg);
/*     */   }
/*     */ 
/*     */   private void notifySpecDataChanges(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, String topicName, DataObjectList dtos)
/*     */   {
/* 343 */     CachedDtoMessage cachedDtoMsg = new CachedDtoMessage(topicName, dtoMsgType);
/* 344 */     cachedDtoMsg.setMsgDtos(dtos);
/* 345 */     cachedDtoMsg.setSourceName(getCallClassName());
/* 346 */     cachedDtoMsg.setTargetId("OBJECT_CHANGE_NOTIFY");
/* 347 */     MsgBusManager.getInstance().sendMessage(cachedDtoMsg);
/*     */   }
/*     */ 
/*     */   private void notifySpecDataChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, String topicName, GenericDO dto) {
/* 351 */     DataObjectList msgDtos = new DataObjectList();
/* 352 */     msgDtos.add(dto);
/* 353 */     notifySpecDataChanges(dtoMsgType, topicName, msgDtos);
/*     */   }
/*     */ 
/*     */   private void notifyCacheDataChange(CachedDtoMessage.DTO_MSG_TYPE dtoMsgType, String topicName, GenericDO dto) {
/* 357 */     DataObjectList msgDtos = new DataObjectList();
/* 358 */     msgDtos.add(dto);
/* 359 */     notifyCacheDataChanges(dtoMsgType, topicName, msgDtos);
/*     */   }
/*     */ 
/*     */   public static String getCallClassName() {
/* 363 */     String clientClassName = TnmsServerName.getServerFullName();
/*     */     try
/*     */     {
/* 379 */       clientClassName = HOST_IP + "/" + clientClassName;
/*     */     }
/*     */     catch (Throwable ex)
/*     */     {
/* 396 */       LogHome.getLog().error("", ex);
/*     */     }
/* 398 */     return clientClassName;
/*     */   }
/*     */ 
/*     */   public static boolean checkObjAttr(GenericDO dbo) {
/* 402 */     boolean isExistObjAttr = false;
/* 403 */     Object[] allAttrNames = dbo.getAllAttr().keySet().toArray();
/* 404 */     for (int i = 0; i < allAttrNames.length; i++) {
/* 405 */       String attrName = (String)allAttrNames[i];
/* 406 */       Object value = dbo.getAttrValue(attrName);
/* 407 */       if (((value instanceof GenericDO)) && (attrName.indexOf("CUID") >= 0)) {
/* 408 */         GenericDO v = (GenericDO)value;
/* 409 */         isExistObjAttr = true;
/* 410 */         LogHome.getLog().error("错误的对象属性：CUID=" + dbo.getCuid() + ", attrName=" + attrName + ", value=" + v);
/*     */       }
/*     */     }
/* 413 */     return isExistObjAttr;
/*     */   }
/*     */ 
/*     */   public static ArrayList getStrToArrayList(String str)
/*     */     throws Exception
/*     */   {
/* 423 */     ArrayList list = new ArrayList();
/* 424 */     if ((str != null) && (str.trim().length() > 0)) {
/* 425 */       String[] strs = str.split(",");
/* 426 */       for (int i = 0; i < strs.length; i++) {
/* 427 */         if (!list.contains(strs[i])) {
/* 428 */           list.add(strs[i]);
/*     */         }
/*     */       }
/*     */     }
/* 432 */     return list;
/*     */   }
/*     */ 
/*     */   public static String getArrayListToStr(List list)
/*     */     throws Exception
/*     */   {
/* 442 */     StringBuffer str = new StringBuffer();
/* 443 */     if ((list != null) && (list.size() > 0)) {
/* 444 */       for (int i = 0; i < list.size(); i++) {
/* 445 */         String s = (String)list.get(i);
/* 446 */         if ((s != null) && (s.trim().length() > 0)) {
/* 447 */           if (str.length() == 0)
/* 448 */             str.append(s);
/*     */           else {
/* 450 */             str.append(",").append(s);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 455 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public static String getSubStringByFlag(String str, boolean isContain, String[] compareStrs)
/*     */     throws Exception
/*     */   {
/* 468 */     ArrayList list = new ArrayList();
/* 469 */     if ((str != null) && (str.trim().length() > 0) && (compareStrs != null) && (compareStrs.length > 0)) {
/* 470 */       String[] strs = str.split(",");
/*     */ 
/* 472 */       if (isContain) {
/* 473 */         for (int i = 0; i < strs.length; i++) {
/* 474 */           for (int j = 0; j < compareStrs.length; j++) {
/* 475 */             if (strs[i].indexOf(compareStrs[j]) > -1) {
/* 476 */               list.add(strs[i]);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 482 */         for (int i = 0; i < strs.length; i++) {
/* 483 */           boolean flag = false;
/* 484 */           for (int j = 0; j < compareStrs.length; j++) {
/* 485 */             if (strs[i].indexOf(compareStrs[j]) > -1) {
/* 486 */               flag = true;
/*     */             }
/*     */           }
/*     */ 
/* 490 */           if (!flag) {
/* 491 */             list.add(strs[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 497 */     return getArrayListToStr(list);
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getCuidGroupByClassName(String cuids)
/*     */     throws Exception
/*     */   {
/* 509 */     Map cuidGroup = new HashMap();
/* 510 */     ArrayList list = getStrToArrayList(cuids);
/* 511 */     for (int i = 0; i < list.size(); i++) {
/* 512 */       String cuid = (String)list.get(i);
/* 513 */       String className = GenericDO.parseClassNameFromCuid(cuid);
/* 514 */       List oneGroupList = (List)cuidGroup.get(className);
/* 515 */       if (oneGroupList == null) {
/* 516 */         oneGroupList = new ArrayList();
/* 517 */         oneGroupList.add(cuid);
/* 518 */         cuidGroup.put(className, oneGroupList);
/*     */       } else {
/* 520 */         oneGroupList.add(cuid);
/* 521 */         cuidGroup.put(className, oneGroupList);
/*     */       }
/*     */     }
/* 524 */     return cuidGroup;
/*     */   }
/*     */ 
/*     */   public static String getStringAttrVaules(DataObjectList dbos, String[] attrNames)
/*     */     throws UserException
/*     */   {
/* 536 */     StringBuffer attrs = new StringBuffer();
/* 537 */     if ((dbos != null) && (attrNames != null)) {
/* 538 */       List attrValueList = new ArrayList();
/* 539 */       for (int i = 0; i < dbos.size(); i++) {
/* 540 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 541 */         for (int j = 0; j < attrNames.length; j++) {
/* 542 */           String neCuid = dbo.getAttrString(attrNames[j]);
/* 543 */           if ((neCuid == null) || (neCuid.trim().length() <= 0) || 
/* 544 */             (attrValueList.contains(neCuid))) continue;
/* 545 */           attrValueList.add(neCuid);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 551 */       for (int i = 0; i < attrValueList.size(); i++) {
/* 552 */         if (i == 0)
/* 553 */           attrs.append((String)attrValueList.get(i));
/*     */         else {
/* 555 */           attrs.append(",").append((String)attrValueList.get(i));
/*     */         }
/*     */       }
/*     */     }
/* 559 */     return attrs.toString();
/*     */   }
/*     */ 
/*     */   public static String getStringAttrVaules(DboCollection dbos, String[] classNameAndAttrName)
/*     */     throws UserException
/*     */   {
/* 570 */     StringBuffer attrCuids = new StringBuffer();
/* 571 */     if ((dbos != null) && (dbos.size() > 0) && (classNameAndAttrName != null) && (classNameAndAttrName.length > 0)) {
/* 572 */       List attrList = new ArrayList();
/* 573 */       for (int i = 0; i < classNameAndAttrName.length; i++) {
/* 574 */         if ((classNameAndAttrName[i].indexOf("@") > 0) && (classNameAndAttrName[i].indexOf("@") < classNameAndAttrName[i].length())) {
/* 575 */           attrList.add(classNameAndAttrName[i].replace("@", ",").split(","));
/*     */         }
/*     */       }
/*     */ 
/* 579 */       List attrValueList = new ArrayList();
/* 580 */       if (attrList.size() > 0) {
/* 581 */         for (int i = 0; i < dbos.size(); i++) {
/* 582 */           for (int j = 0; j < attrList.size(); j++) {
/* 583 */             String[] one_class_attrs = (String[])attrList.get(j);
/* 584 */             GenericDO dbo = dbos.getQueryDbo(i, one_class_attrs[0]);
/* 585 */             if (dbo != null) {
/* 586 */               for (int k = 1; k < one_class_attrs.length; k++) {
/* 587 */                 String attrValue = dbo.getAttrString(one_class_attrs[k]);
/* 588 */                 if ((attrValue == null) || (attrValue.trim().length() <= 0) || 
/* 589 */                   (attrValueList.contains(attrValue))) continue;
/* 590 */                 attrValueList.add(attrValue);
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 599 */       for (int i = 0; i < attrValueList.size(); i++) {
/* 600 */         if (i == 0)
/* 601 */           attrCuids.append((String)attrValueList.get(i));
/*     */         else {
/* 603 */           attrCuids.append(",").append((String)attrValueList.get(i));
/*     */         }
/*     */       }
/*     */     }
/* 607 */     return attrCuids.toString();
/*     */   }
/*     */ 
/*     */   public String[][] getStringArrayByDataObjectList(DataObjectList dbos, String[] attrName)
/*     */   {
/* 619 */     String[][] strArray = (String[][])null;
/* 620 */     if ((dbos != null) && (attrName != null)) {
/* 621 */       strArray = new String[dbos.size()][attrName.length];
/* 622 */       for (int i = 0; i < dbos.size(); i++) {
/* 623 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 624 */         for (int j = 0; j < attrName.length; j++) {
/* 625 */           strArray[i][j] = "";
/* 626 */           if ((!dbo.getAllAttr().containsKey(attrName[j])) || 
/* 627 */             (dbo.getAllAttr().get(attrName[j]) == null)) continue;
/* 628 */           String v = String.valueOf(dbo.getAllAttr().get(attrName[j]));
/* 629 */           strArray[i][j] = (isNotEmpty(v) ? v : "");
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 635 */     return strArray;
/*     */   }
/*     */ 
/*     */   public List<String> getFeachSql(String ids)
/*     */     throws Exception
/*     */   {
/* 647 */     List list = new ArrayList();
/* 648 */     if (isNotEmpty(ids)) {
/* 649 */       List temp = getStrToArrayList(ids);
/* 650 */       list = getFeachSql(temp);
/*     */     }
/* 652 */     return list;
/*     */   }
/*     */ 
/*     */   public List<String> getFeachSql(List<String> ids)
/*     */     throws Exception
/*     */   {
/* 664 */     List list = new ArrayList();
/* 665 */     if ((ids != null) && (ids.size() > 0)) {
/* 666 */       int feachCount = 100;
/* 667 */       int queryCount = (ids.size() + feachCount - 1) / feachCount;
/* 668 */       for (int i = 0; i < queryCount; i++) {
/* 669 */         int orig = i * feachCount;
/* 670 */         int dest = (i + 1) * feachCount > ids.size() ? ids.size() : (i + 1) * feachCount - 1;
/* 671 */         List feach = ids.subList(orig, dest);
/* 672 */         String cuids = getArrayListToStr(feach);
/* 673 */         list.add(cuids);
/*     */       }
/*     */     }
/* 676 */     return list;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getGenericDOMap(DataObjectList dbos, String keyAttrName, String valueAttrName)
/*     */     throws Exception
/*     */   {
/* 688 */     Map genericDOMap = new HashMap();
/* 689 */     if (dbos != null) {
/* 690 */       for (int i = 0; i < dbos.size(); i++) {
/* 691 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 692 */         String key = dbo.getAttrString(keyAttrName);
/* 693 */         String value = dbo.getAttrString(valueAttrName);
/* 694 */         if ((key != null) && (value != null)) {
/* 695 */           genericDOMap.put(key, value);
/*     */         }
/*     */       }
/*     */     }
/* 699 */     return genericDOMap;
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getGenericDOGroupMap(DataObjectList dbos, String keyAttrNames, String valueAttrNames, boolean isIncludeSameValue)
/*     */     throws Exception
/*     */   {
/* 713 */     Map genericDOMap = new HashMap();
/* 714 */     String[] keyAttrName = keyAttrNames.split(",");
/* 715 */     String[] valueAttrName = valueAttrNames.split(",");
/* 716 */     if (dbos != null) {
/* 717 */       for (int i = 0; i < dbos.size(); i++) {
/* 718 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 719 */         for (int j = 0; j < keyAttrName.length; j++) {
/* 720 */           String key = dbo.getAttrString(keyAttrName[j]);
/* 721 */           for (int k = 0; k < valueAttrName.length; k++) {
/* 722 */             String value = dbo.getAttrString(valueAttrName[k]);
/* 723 */             if ((key == null) || (value == null)) {
/*     */               continue;
/*     */             }
/* 726 */             if (genericDOMap.containsKey(key)) {
/* 727 */               List temp = (List)genericDOMap.get(key);
/* 728 */               if (temp.contains(value)) {
/* 729 */                 if (!isIncludeSameValue) {
/* 730 */                   temp.add(value);
/* 731 */                   genericDOMap.put(key, temp);
/*     */                 }
/*     */               } else {
/* 734 */                 temp.add(value);
/* 735 */                 genericDOMap.put(key, temp);
/*     */               }
/*     */             } else {
/* 738 */               List temp = new ArrayList();
/* 739 */               temp.add(value);
/* 740 */               genericDOMap.put(key, temp);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 747 */     return genericDOMap;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getGenericDOFilterMap(DataObjectList dbos, String keyAttrNames, String valueAttrName, boolean isContain, String keyCompareStrs)
/*     */     throws Exception
/*     */   {
/* 761 */     Map genericDOMap = new HashMap();
/* 762 */     boolean compareFlag = false;
/* 763 */     if (dbos != null) {
/* 764 */       String[] keyAttrName = keyAttrNames.split(",");
/* 765 */       if (keyCompareStrs == null) {
/* 766 */         keyCompareStrs = "";
/*     */       }
/* 768 */       String[] compareStr = keyCompareStrs.split(",");
/* 769 */       for (int i = 0; i < dbos.size(); i++) {
/* 770 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 771 */         String value = dbo.getAttrString(valueAttrName);
/* 772 */         if (value == null) {
/*     */           continue;
/*     */         }
/* 775 */         for (int j = 0; j < keyAttrName.length; j++) {
/* 776 */           String key = dbo.getAttrString(keyAttrName[j]);
/* 777 */           if (key == null) {
/*     */             continue;
/*     */           }
/* 780 */           if (compareStr.length > 0) {
/* 781 */             if (isContain) {
/* 782 */               for (int k = 0; k < compareStr.length; k++)
/* 783 */                 if (key.contains(compareStr[k])) {
/* 784 */                   genericDOMap.put(key, value);
/* 785 */                   break;
/*     */                 }
/*     */             }
/*     */             else {
/* 789 */               compareFlag = false;
/* 790 */               for (int k = 0; k < compareStr.length; k++) {
/* 791 */                 if (key.contains(compareStr[k])) {
/* 792 */                   compareFlag = true;
/* 793 */                   break;
/*     */                 }
/*     */               }
/* 796 */               if (!compareFlag)
/* 797 */                 genericDOMap.put(key, value);
/*     */             }
/*     */           }
/*     */           else {
/* 801 */             genericDOMap.put(key, value);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 806 */     return genericDOMap;
/*     */   }
/*     */ 
/*     */   public static boolean isSameStr(String a, String b) {
/* 810 */     if ((a == null) && (b == null))
/* 811 */       return true;
/* 812 */     if ((a == null) && (b != null))
/* 813 */       return false;
/* 814 */     if ((a != null) && (b == null))
/* 815 */       return false;
/* 816 */     if ((a != null) && (b != null)) {
/* 817 */       return a.equals(b);
/*     */     }
/* 819 */     return false;
/*     */   }
/*     */ 
/*     */   private static String getTableNameCn(String tableNameEn)
/*     */   {
/* 824 */     String tableLabelCn = ClassUtils.getInstance().getTableLabelCn(tableNameEn);
/* 825 */     String name = tableLabelCn != null ? tableLabelCn : tableNameEn;
/* 826 */     return name;
/*     */   }
/*     */ 
/*     */   public static String getRelatedCuid(Object obj) {
/* 830 */     if ((obj instanceof GenericDO))
/* 831 */       return ((GenericDO)obj).getCuid();
/* 832 */     if ((obj instanceof String))
/* 833 */       return (String)obj;
/* 834 */     if (obj != null) {
/* 835 */       return obj.toString();
/*     */     }
/* 837 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getJavaClassNameByDbClassName(String className)
/*     */   {
/* 842 */     String[] splits = className.split("_");
/* 843 */     String dbClassName = "";
/* 844 */     for (int i = 0; i < splits.length; i++) {
/* 845 */       String split = splits[i];
/* 846 */       dbClassName = dbClassName + split.substring(0, 1).toUpperCase() + split.substring(1, split.length()).toLowerCase();
/*     */     }
/* 848 */     dbClassName = "com.boco.transnms.common.dto." + dbClassName;
/* 849 */     return dbClassName;
/*     */   }
/*     */ 
/*     */   public static void copyArray(Object[] src, Object[] dest) {
/* 853 */     if ((src != null) && (dest != null) && (dest.length >= src.length))
/* 854 */       for (int i = 0; i < dest.length; i++)
/* 855 */         dest[i] = src[i];
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.DaoHelper
 * JD-Core Version:    0.6.0
 */