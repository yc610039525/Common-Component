/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class XrpcProxyManager
/*    */   implements IBoProxyManager
/*    */ {
/*    */   private String boProxyName;
/*    */   private String xrpcContextName;
/* 26 */   private List boNames = new ArrayList();
/* 27 */   private boolean localBoProxy = false;
/*    */ 
/*    */   public void setXrpcContextName(String xrpcContextName)
/*    */   {
/* 32 */     this.xrpcContextName = xrpcContextName;
/*    */   }
/*    */ 
/*    */   public void setXrpcBoNames(List boNames) {
/* 36 */     this.boNames = boNames;
/*    */   }
/*    */ 
/*    */   public void setLocalBoProxy(boolean isLocalBoProxy) {
/* 40 */     this.localBoProxy = isLocalBoProxy;
/*    */   }
/*    */ 
/*    */   public String getXrpcContextName() {
/* 44 */     return this.xrpcContextName;
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
/*    */   public List getXrpcBoNames() {
/* 56 */     return this.boNames;
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName) {
/* 60 */     IBoProxy proxy = null;
/* 61 */     String url = XrpcUrlManager.getInstance().getXrpcUrl(this.xrpcContextName);
/* 62 */     if (url != null)
/*    */     {
/* 64 */       proxy = new XrpcBoProxy(url);
/*    */     }
/*    */ 
/* 67 */     return proxy;
/*    */   }
/*    */   public IBoProxy getBoProxy(String boName, String url) {
/* 70 */     IBoProxy proxy = null;
/* 71 */     if (url != null)
/*    */     {
/* 73 */       proxy = new XrpcBoProxy(url);
/*    */     }
/*    */ 
/* 76 */     return proxy;
/*    */   }
/*    */ 
/*    */   public void prepareBoProxy() {
/*    */   }
/*    */ 
/*    */   public void setBoProxyName(String boProxyName) {
/* 83 */     this.boProxyName = boProxyName;
/*    */   }
/*    */ 
/*    */   public String getBoProxyName() {
/* 87 */     return this.boProxyName;
/*    */   }
/*    */ 
/*    */   public boolean isLocalBoProxy() {
/* 91 */     return this.localBoProxy;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.XrpcProxyManager
 * JD-Core Version:    0.6.0
 */