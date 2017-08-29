/*    */ package com.boco.raptor.common.service;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public abstract class AbstractService
/*    */   implements IService
/*    */ {
/*    */   private String serviceId;
/*    */   private boolean enable;
/*    */ 
/*    */   public AbstractService(String serviceId)
/*    */   {
/* 28 */     this.serviceId = serviceId;
/*    */   }
/*    */ 
/*    */   public String getServiceId() {
/* 32 */     return this.serviceId;
/*    */   }
/*    */ 
/*    */   public void initService() throws Exception {
/* 36 */     LogHome.getLog().info("服务[" + this.serviceId + "] 正常加载;");
/*    */   }
/*    */ 
/*    */   public boolean isEnable() {
/* 40 */     return this.enable;
/*    */   }
/*    */ 
/*    */   public void setIsEnable(boolean enable) {
/* 44 */     this.enable = enable;
/*    */   }
/*    */ 
/*    */   public void setServiceId(String serviceId) {
/* 48 */     this.serviceId = serviceId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.AbstractService
 * JD-Core Version:    0.6.0
 */