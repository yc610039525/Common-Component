/*     */ package com.boco.transnms.common.dto;
/*     */ 
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.helper.SystemLogHelper;
/*     */ import java.sql.Timestamp;
/*     */ 
/*     */ public class SystemLog extends GenericDO
/*     */ {
/*     */   public static final String CLASS_NAME = "SYSTEM_LOG";
/*     */   private static final long serialVersionUID = 1047L;
/*     */ 
/*     */   public SystemLog()
/*     */   {
/*  41 */     setClassName("SYSTEM_LOG");
/*  42 */     initDefaultValue();
/*     */   }
/*     */ 
/*     */   public SystemLog(String cuid) {
/*  46 */     setClassName("SYSTEM_LOG");
/*  47 */     setCuid(cuid);
/*  48 */     initDefaultValue();
/*     */   }
/*     */ 
/*     */   private void initDefaultValue()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLogClassName(String varLogClassName) {
/*  56 */     setAttrValue("LOG_CLASS_NAME", varLogClassName);
/*  57 */     removeDefaultAttrValue("LOG_CLASS_NAME");
/*     */   }
/*     */ 
/*     */   public String getLogClassName() {
/*  61 */     return getAttrString("LOG_CLASS_NAME");
/*     */   }
/*     */ 
/*     */   public void setIpAddress(String varIpAddress) {
/*  65 */     setAttrValue("IP_ADDRESS", varIpAddress);
/*  66 */     removeDefaultAttrValue("IP_ADDRESS");
/*     */   }
/*     */ 
/*     */   public String getIpAddress() {
/*  70 */     return getAttrString("IP_ADDRESS");
/*     */   }
/*     */ 
/*     */   public void setObjectName(String varObjectName) {
/*  74 */     setAttrValue("OBJECT_NAME", varObjectName);
/*  75 */     removeDefaultAttrValue("OBJECT_NAME");
/*     */   }
/*     */ 
/*     */   public String getObjectName() {
/*  79 */     return getAttrString("OBJECT_NAME");
/*     */   }
/*     */ 
/*     */   public void setMachineName(String varMachineName) {
/*  83 */     setAttrValue("MACHINE_NAME", varMachineName);
/*  84 */     removeDefaultAttrValue("MACHINE_NAME");
/*     */   }
/*     */ 
/*     */   public String getMachineName() {
/*  88 */     return getAttrString("MACHINE_NAME");
/*     */   }
/*     */ 
/*     */   public void setCreatetime(Timestamp varCreatetime) {
/*  92 */     setAttrValue("CREATETIME", varCreatetime);
/*  93 */     removeDefaultAttrValue("CREATETIME");
/*     */   }
/*     */ 
/*     */   public Timestamp getCreatetime() {
/*  97 */     return getAttrDateTime("CREATETIME");
/*     */   }
/*     */ 
/*     */   public void setDesciption(String varDesciption) {
/* 101 */     setAttrValue("DESCIPTION", varDesciption);
/* 102 */     removeDefaultAttrValue("DESCIPTION");
/*     */   }
/*     */ 
/*     */   public String getDesciption() {
/* 106 */     return getAttrString("DESCIPTION");
/*     */   }
/*     */ 
/*     */   public void setOperateType(String varOperateType) {
/* 110 */     setAttrValue("OPERATE_TYPE", varOperateType);
/* 111 */     removeDefaultAttrValue("OPERATE_TYPE");
/*     */   }
/*     */ 
/*     */   public String getOperateType() {
/* 115 */     return getAttrString("OPERATE_TYPE");
/*     */   }
/*     */ 
/*     */   public void setUserName(String varUserName) {
/* 119 */     setAttrValue("USER_NAME", varUserName);
/* 120 */     removeDefaultAttrValue("USER_NAME");
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/* 124 */     return getAttrString("USER_NAME");
/*     */   }
/*     */   public Class getAttrType(String attrName) {
/* 127 */     return SystemLogHelper.getInstance().getAttrType(attrName);
/*     */   }
/*     */ 
/*     */   public String[] getAllAttrNames() {
/* 131 */     return SystemLogHelper.getInstance().getAllAttrNames();
/*     */   }
/*     */ 
/*     */   public String[] getAllUserAttrNames() {
/* 135 */     return SystemLogHelper.getInstance().getAllUserAttrNames();
/*     */   }
/*     */ 
/*     */   public String getAttrLabelCn(String attrName) {
/* 139 */     return SystemLogHelper.getInstance().getAttrLabelCn(attrName);
/*     */   }
/*     */ 
/*     */   public static class AttrName
/*     */   {
/*     */     public static final String logClassName = "LOG_CLASS_NAME";
/*     */     public static final String ipAddress = "IP_ADDRESS";
/*     */     public static final String objectName = "OBJECT_NAME";
/*     */     public static final String machineName = "MACHINE_NAME";
/*     */     public static final String createtime = "CREATETIME";
/*     */     public static final String desciption = "DESCIPTION";
/*     */     public static final String cuid = "CUID";
/*     */     public static final String operateType = "OPERATE_TYPE";
/*     */     public static final String userName = "USER_NAME";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.SystemLog
 * JD-Core Version:    0.6.0
 */