/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class WsUrlManager
/*    */ {
/* 25 */   private static WsUrlManager instance = new WsUrlManager();
/* 26 */   private Map wsUrlMap = new HashMap();
/*    */ 
/*    */   public static WsUrlManager getInstance()
/*    */   {
/* 32 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setWsUrls(Map wsUrlMap) {
/* 36 */     this.wsUrlMap = wsUrlMap;
/*    */   }
/*    */ 
/*    */   public void setWsUrl(String contextName, String url) {
/* 40 */     this.wsUrlMap.put(contextName, url);
/*    */   }
/*    */ 
/*    */   public String getWsUrl(String contextName) {
/* 44 */     return (String)this.wsUrlMap.get(contextName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.WsUrlManager
 * JD-Core Version:    0.6.0
 */