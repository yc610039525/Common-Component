/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.common.util.lang.StringHelper;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import com.boco.transnms.server.dao.base.DaoHelper;
/*     */ 
/*     */ public class BoActionContext extends BoCmdContext
/*     */   implements IBoActionContext
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public BoActionContext()
/*     */   {
/*  47 */     this(true);
/*     */   }
/*     */ 
/*     */   public BoActionContext(boolean actionChecked) {
/*  51 */     setActionChecked(actionChecked);
/*  52 */     setHostIP(DaoHelper.HOST_IP);
/*  53 */     setHostName(DaoHelper.HOST_NAME);
/*     */   }
/*     */ 
/*     */   public BoActionContext(String userId) {
/*  57 */     setUserId(userId);
/*  58 */     setActionChecked(true);
/*     */   }
/*     */ 
/*     */   public BoActionContext(BoCmdContext boCmdContext) {
/*  62 */     this(boCmdContext.getBoName(), boCmdContext.getMethodName());
/*     */   }
/*     */ 
/*     */   public BoActionContext(String boName, String methodName) {
/*  66 */     super(boName, methodName, "");
/*  67 */     setActionChecked(true);
/*     */   }
/*     */ 
/*     */   public void setUserId(String userId) {
/*  71 */     super.setAttrValue("userId", userId);
/*     */   }
/*     */ 
/*     */   public String getUserId() {
/*  75 */     String userId = super.getAttrString("userId");
/*  76 */     return StringHelper.nullToEmpty(userId);
/*     */   }
/*     */ 
/*     */   public void setDsName(String dsName) {
/*  80 */     super.setAttrValue("dsName", dsName);
/*     */   }
/*     */ 
/*     */   public String getDsName() {
/*  84 */     return super.getAttrString("dsName");
/*     */   }
/*     */ 
/*     */   public String getActionName() {
/*  88 */     String actionName = ActionHelper.getActionName(super.getBoName(), super.getMethodName());
/*  89 */     return StringHelper.nullToEmpty(actionName);
/*     */   }
/*     */ 
/*     */   public void setActionName(String actionName) {
/*  93 */     String boName = ActionHelper.getBoName(actionName);
/*  94 */     String methodName = ActionHelper.getMethodName(actionName);
/*  95 */     super.setBoName(boName);
/*  96 */     super.setMethodName(methodName);
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/* 100 */     String userName = super.getAttrString("userName");
/* 101 */     return StringHelper.nullToEmpty(userName);
/*     */   }
/*     */   public String getLoginSystemName() {
/* 104 */     String loginSystemName = super.getAttrString("loginSystemName");
/* 105 */     return StringHelper.nullToEmpty(loginSystemName);
/*     */   }
/*     */ 
/*     */   public void setUserName(String userName) {
/* 109 */     super.setAttrValue("userName", userName);
/*     */   }
/*     */   public void setLoginSystemName(String loginSystemName) {
/* 112 */     super.setAttrValue("loginSystemName", loginSystemName);
/*     */   }
/*     */   public String getHostIP() {
/* 115 */     String hostIP = super.getAttrString("hostIP");
/* 116 */     return StringHelper.nullToEmpty(hostIP);
/*     */   }
/*     */ 
/*     */   public String getHostName() {
/* 120 */     String hostName = super.getAttrString("hostName");
/* 121 */     return StringHelper.nullToEmpty(hostName);
/*     */   }
/*     */ 
/*     */   public void setHostIP(String hostIP) {
/* 125 */     super.setAttrValue("hostIP", hostIP);
/*     */   }
/*     */ 
/*     */   public void setHostName(String hostName) {
/* 129 */     super.setAttrValue("hostName", hostName);
/*     */   }
/*     */ 
/*     */   public String getClientId() {
/* 133 */     return super.getAttrString("clientId");
/*     */   }
/*     */ 
/*     */   public boolean isActionChecked() {
/* 137 */     return super.getAttrBool("actionChecked");
/*     */   }
/*     */ 
/*     */   public void setClientId(String clientId) {
/* 141 */     super.setAttrValue("clientId", clientId);
/*     */   }
/*     */ 
/*     */   public void setActionChecked(boolean checked) {
/* 145 */     super.setAttrValue("actionChecked", checked);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 149 */     String str = "BoActionContext[";
/* 150 */     str = str + "boName=" + getBoName();
/* 151 */     str = str + ", methodName=" + getMethodName();
/* 152 */     str = str + ", hostName=" + getHostName();
/* 153 */     str = str + ", hostIP=" + getHostIP();
/* 154 */     str = str + ", userName=" + getUserName();
/* 155 */     str = str + ", compressed=" + isCompressed();
/* 156 */     str = str + "]";
/* 157 */     return str;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 161 */     String userId = getUserId();
/* 162 */     return userId != null ? userId.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 166 */     return (obj != null) && (hashCode() == obj.hashCode());
/*     */   }
/*     */ 
/*     */   private static class AttrName
/*     */   {
/*     */     private static final String dsName = "dsName";
/*     */     private static final String hostIP = "hostIP";
/*     */     private static final String hostName = "hostName";
/*     */     private static final String userId = "userId";
/*     */     private static final String userName = "userName";
/*     */     private static final String actionChecked = "actionChecked";
/*     */     private static final String clientId = "clientId";
/*     */     private static final String loginSystemName = "loginSystemName";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.BoActionContext
 * JD-Core Version:    0.6.0
 */