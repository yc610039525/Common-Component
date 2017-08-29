/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class HttpDirectProxyManager
/*    */   implements IBoProxyManager
/*    */ {
/*    */   private String boProxyName;
/*    */   private String contextName;
/* 26 */   private List boNames = new ArrayList();
/* 27 */   private boolean localBoProxy = false;
/*    */ 
/*    */   public void setContextName(String contextName)
/*    */   {
/* 32 */     this.contextName = contextName;
/*    */   }
/*    */ 
/*    */   public void setHttpBoNames(List boNames) {
/* 36 */     this.boNames = boNames;
/*    */   }
/*    */ 
/*    */   public void setLocalBoProxy(boolean isLocalBoProxy) {
/* 40 */     this.localBoProxy = isLocalBoProxy;
/*    */   }
/*    */ 
/*    */   public String getContextName() {
/* 44 */     return this.contextName;
/*    */   }
/*    */ 
/*    */   public String[] getBoNames() {
/* 48 */     List allBoNames = new ArrayList();
/* 49 */     allBoNames.addAll(this.boNames);
/* 50 */     String[] _boNames = new String[allBoNames.size()];
/* 51 */     allBoNames.toArray(_boNames);
/* 52 */     return _boNames;
/*    */   }
/*    */ 
/*    */   public List getHttpBoNames() {
/* 56 */     return this.boNames;
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName) {
/* 60 */     IBoProxy proxy = null;
/* 61 */     String url = XrpcUrlManager.getInstance().getXrpcUrl(this.contextName);
/* 62 */     if (url != null)
/*    */     {
/* 64 */       proxy = new HttpBoProxy(url);
/*    */     }
/*    */ 
/* 67 */     return proxy;
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName, String url) {
/* 71 */     IBoProxy proxy = null;
/* 72 */     if (url != null)
/*    */     {
/* 74 */       proxy = new HttpBoProxy(url);
/*    */     }
/*    */ 
/* 77 */     return proxy;
/*    */   }
/*    */ 
/*    */   public void prepareBoProxy() {
/*    */   }
/*    */ 
/*    */   public void setBoProxyName(String boProxyName) {
/* 84 */     this.boProxyName = boProxyName;
/*    */   }
/*    */ 
/*    */   public String getBoProxyName() {
/* 88 */     return this.boProxyName;
/*    */   }
/*    */ 
/*    */   public boolean isLocalBoProxy() {
/* 92 */     return this.localBoProxy;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.HttpDirectProxyManager
 * JD-Core Version:    0.6.0
 */