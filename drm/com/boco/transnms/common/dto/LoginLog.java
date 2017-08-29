/*     */ package com.boco.transnms.common.dto;
/*     */ 
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.helper.LoginLogHelper;
/*     */ import java.sql.Timestamp;
/*     */ 
/*     */ public class LoginLog extends GenericDO
/*     */ {
/*     */   public static final String CLASS_NAME = "LOGIN_LOG";
/*     */   private static final long serialVersionUID = 1009L;
/*     */ 
/*     */   public LoginLog()
/*     */   {
/*  41 */     setClassName("LOGIN_LOG");
/*  42 */     initDefaultValue();
/*     */   }
/*     */ 
/*     */   public LoginLog(String cuid) {
/*  46 */     setClassName("LOGIN_LOG");
/*  47 */     setCuid(cuid);
/*  48 */     initDefaultValue();
/*     */   }
/*     */ 
/*     */   private void initDefaultValue()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLoginType(long varLoginType) {
/*  56 */     setAttrValue("LOGIN_TYPE", varLoginType);
/*  57 */     removeDefaultAttrValue("LOGIN_TYPE");
/*     */   }
/*     */ 
/*     */   public long getLoginType() {
/*  61 */     return getAttrLong("LOGIN_TYPE", 0L);
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
/*     */   public void setMachineName(String varMachineName) {
/*  74 */     setAttrValue("MACHINE_NAME", varMachineName);
/*  75 */     removeDefaultAttrValue("MACHINE_NAME");
/*     */   }
/*     */ 
/*     */   public String getMachineName() {
/*  79 */     return getAttrString("MACHINE_NAME");
/*     */   }
/*     */ 
/*     */   public void setLoginTime(Timestamp varLoginTime) {
/*  83 */     setAttrValue("LOGIN_TIME", varLoginTime);
/*  84 */     removeDefaultAttrValue("LOGIN_TIME");
/*     */   }
/*     */ 
/*     */   public Timestamp getLoginTime() {
/*  88 */     return getAttrDateTime("LOGIN_TIME");
/*     */   }
/*     */ 
/*     */   public void setUserName(String varUserName) {
/*  92 */     setAttrValue("USER_NAME", varUserName);
/*  93 */     removeDefaultAttrValue("USER_NAME");
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/*  97 */     return getAttrString("USER_NAME");
/*     */   }
/*     */ 
/*     */   public void setLogoutTime(Timestamp varLogoutTime) {
/* 101 */     setAttrValue("LOGOUT_TIME", varLogoutTime);
/* 102 */     removeDefaultAttrValue("LOGOUT_TIME");
/*     */   }
/*     */ 
/*     */   public Timestamp getLogoutTime() {
/* 106 */     return getAttrDateTime("LOGOUT_TIME");
/*     */   }
/*     */ 
/*     */   public void setLoginSystemName(String varLoginSystemName) {
/* 110 */     setAttrValue("LOGIN_SYSTEM_NAME", varLoginSystemName);
/* 111 */     removeDefaultAttrValue("LOGIN_SYSTEM_NAME");
/*     */   }
/*     */ 
/*     */   public String getLoginSystemName() {
/* 115 */     return getAttrString("LOGIN_SYSTEM_NAME");
/*     */   }
/*     */   public Class getAttrType(String attrName) {
/* 118 */     return LoginLogHelper.getInstance().getAttrType(attrName);
/*     */   }
/*     */ 
/*     */   public String[] getAllAttrNames() {
/* 122 */     return LoginLogHelper.getInstance().getAllAttrNames();
/*     */   }
/*     */ 
/*     */   public String[] getAllUserAttrNames() {
/* 126 */     return LoginLogHelper.getInstance().getAllUserAttrNames();
/*     */   }
/*     */ 
/*     */   public String getAttrLabelCn(String attrName) {
/* 130 */     return LoginLogHelper.getInstance().getAttrLabelCn(attrName);
/*     */   }
/*     */ 
/*     */   public static class AttrName
/*     */   {
/*     */     public static final String loginType = "LOGIN_TYPE";
/*     */     public static final String ipAddress = "IP_ADDRESS";
/*     */     public static final String machineName = "MACHINE_NAME";
/*     */     public static final String cuid = "CUID";
/*     */     public static final String loginTime = "LOGIN_TIME";
/*     */     public static final String userName = "USER_NAME";
/*     */     public static final String logoutTime = "LOGOUT_TIME";
/*     */     public static final String loginSystemName = "LOGIN_SYSTEM_NAME";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.LoginLog
 * JD-Core Version:    0.6.0
 */