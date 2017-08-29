/*     */ package com.boco.raptor.common.service.impl;
/*     */ 
/*     */ import com.boco.common.util.id.CUIDHexGenerator;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class ServiceActionContext extends BoActionContext
/*     */   implements IServiceActionContext
/*     */ {
/*  34 */   private boolean isLog = false;
/*     */ 
/*     */   public boolean getIsLog()
/*     */   {
/*  40 */     return this.isLog;
/*     */   }
/*     */ 
/*     */   public String getActionId() {
/*  44 */     return super.getActionName();
/*     */   }
/*     */ 
/*     */   public String getServiceId() {
/*  48 */     return super.getAttrString("serviceId");
/*     */   }
/*     */ 
/*     */   public void setIsLog(boolean isLog) {
/*  52 */     this.isLog = isLog;
/*     */   }
/*     */ 
/*     */   public void setActionId(String actionId) {
/*  56 */     super.setActionName(actionId);
/*     */   }
/*     */ 
/*     */   public void setServiceId(String serviceId) {
/*  60 */     super.setAttrValue("serviceId", serviceId);
/*     */   }
/*     */ 
/*     */   public void addExtCxtAttr(String attrName, Serializable attrValue) {
/*  64 */     super.setAttrValue(attrName, attrValue);
/*     */   }
/*     */ 
/*     */   public void removeExtCxtAttr(String attrName) {
/*  68 */     super.removeAttr(attrName);
/*     */   }
/*     */ 
/*     */   public <T> T getExtCxtAttr(String attrName) {
/*  72 */     return super.getAttrValue(attrName);
/*     */   }
/*     */ 
/*     */   public String getDataSourceName() {
/*  76 */     return super.getAttrString("dataSouceName");
/*     */   }
/*     */ 
/*     */   public void setDataSourceName(String dsName) {
/*  80 */     super.setAttrValue("dataSouceName", dsName);
/*     */   }
/*     */ 
/*     */   public String getRequestId() {
/*  84 */     String requestId = (String)super.getAttrValueT("requestId");
/*  85 */     if (requestId == null) {
/*  86 */       requestId = CUIDHexGenerator.getInstance().generate("RequestId");
/*  87 */       setRequestId(requestId);
/*     */     }
/*  89 */     return requestId;
/*     */   }
/*     */ 
/*     */   public void setRequestId(String requestId) {
/*  93 */     super.setAttrValue("requestId", requestId);
/*     */   }
/*     */ 
/*     */   public String getProxyName() {
/*  97 */     return super.getAttrString("proxyName");
/*     */   }
/*     */ 
/*     */   public void setProxyName(String proxyName) {
/* 101 */     super.setAttrValue("proxyName", proxyName);
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 105 */     return super.getAttrString("bmclassId");
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 109 */     super.setAttrValue("bmclassId", bmClassId);
/*     */   }
/*     */ 
/*     */   private static class AttrName
/*     */   {
/*     */     private static final String serviceId = "serviceId";
/*     */     private static final String dataSouceName = "dataSouceName";
/*     */     private static final String requestId = "requestId";
/*     */     private static final String proxyName = "proxyName";
/*     */     private static final String bmClassId = "bmclassId";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.impl.ServiceActionContext
 * JD-Core Version:    0.6.0
 */