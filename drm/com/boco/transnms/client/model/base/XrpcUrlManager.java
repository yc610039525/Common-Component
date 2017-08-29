/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class XrpcUrlManager
/*    */ {
/* 24 */   private static XrpcUrlManager instance = new XrpcUrlManager();
/* 25 */   private Map xrpcUrlMap = new HashMap();
/* 26 */   private Map appletXrpcUrlMap = null;
/*    */ 
/*    */   public static XrpcUrlManager getInstance()
/*    */   {
/* 32 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setXrpcUrls(Map xrpcUrlMap) {
/* 36 */     this.xrpcUrlMap = xrpcUrlMap;
/*    */   }
/*    */ 
/*    */   public void setXrpcUrl(String contextName, String url) {
/* 40 */     this.xrpcUrlMap.put(contextName, url);
/*    */   }
/*    */ 
/*    */   public String getXrpcUrl(String contextName) {
/* 44 */     return (String)this.xrpcUrlMap.get(contextName);
/*    */   }
/*    */ 
/*    */   public void setAppletXrpcUrls(Map appletXrpcUrlMap) {
/* 48 */     this.appletXrpcUrlMap = appletXrpcUrlMap;
/*    */   }
/*    */ 
/*    */   public void setAppletXrpcUrl(String contextName, String url) {
/* 52 */     this.appletXrpcUrlMap.put(contextName, url);
/*    */   }
/*    */ 
/*    */   public String getAppletXrpcUrl(String contextName) {
/* 56 */     if (this.appletXrpcUrlMap != null) {
/* 57 */       return (String)this.appletXrpcUrlMap.get(contextName);
/*    */     }
/* 59 */     return (String)this.xrpcUrlMap.get(contextName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.XrpcUrlManager
 * JD-Core Version:    0.6.0
 */