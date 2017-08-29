/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class WsProxyManager
/*    */   implements IBoProxyManager
/*    */ {
/* 25 */   private Map<String, WsBoProxy> wsBoProxyMap = new HashMap();
/*    */   private String wsContextName;
/*    */   private String serviceNameSpace;
/*    */   private String boProxyName;
/*    */ 
/*    */   public void setWsCmdContextMap(Map wsCmdContextMap)
/*    */   {
/* 34 */     this.wsBoProxyMap = wsCmdContextMap;
/*    */   }
/*    */ 
/*    */   public void setWsContextName(String wsContextName) {
/* 38 */     this.wsContextName = wsContextName;
/*    */   }
/*    */ 
/*    */   public void setServiceNameSpace(String serviceNameSpace) {
/* 42 */     this.serviceNameSpace = serviceNameSpace;
/*    */   }
/*    */ 
/*    */   public void setWsBoProxyMap(Map wsBoProxyMap) {
/* 46 */     this.wsBoProxyMap = wsBoProxyMap;
/*    */   }
/*    */ 
/*    */   public void prepareBoProxy() {
/* 50 */     String[] boNames = getBoNames();
/* 51 */     for (int i = 0; i < boNames.length; i++) {
/* 52 */       WsBoProxy wsProxy = (WsBoProxy)this.wsBoProxyMap.get(boNames[i]);
/* 53 */       wsProxy.setBoName(boNames[i]);
/* 54 */       if (wsProxy.getWsContextName() == null) {
/* 55 */         wsProxy.setWsContextName(this.wsContextName);
/*    */       }
/* 57 */       if (wsProxy.getServiceNameSpace() == null)
/* 58 */         wsProxy.setServiceNameSpace(this.serviceNameSpace);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String[] getBoNames()
/*    */   {
/* 64 */     String[] boNames = new String[this.wsBoProxyMap.size()];
/* 65 */     this.wsBoProxyMap.keySet().toArray(boNames);
/* 66 */     return boNames;
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName) {
/* 70 */     return (IBoProxy)this.wsBoProxyMap.get(boName);
/*    */   }
/*    */   public IBoProxy getBoProxy(String boName, String url) {
/* 73 */     return (IBoProxy)this.wsBoProxyMap.get(boName);
/*    */   }
/*    */ 
/*    */   public Map getWsBoProxyMap() {
/* 77 */     return this.wsBoProxyMap;
/*    */   }
/*    */ 
/*    */   public void setBoProxyName(String boProxyName) {
/* 81 */     this.boProxyName = boProxyName;
/*    */   }
/*    */ 
/*    */   public String getBoProxyName() {
/* 85 */     return this.boProxyName;
/*    */   }
/*    */ 
/*    */   public boolean isLocalBoProxy() {
/* 89 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.WsProxyManager
 * JD-Core Version:    0.6.0
 */