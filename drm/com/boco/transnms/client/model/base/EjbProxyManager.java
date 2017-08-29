/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class EjbProxyManager
/*    */   implements IBoProxyManager
/*    */ {
/* 26 */   private Map<String, EjbBoProxy> ejbBoProxyMap = new HashMap();
/* 27 */   private Properties ejbJndiProps = new Properties();
/*    */   private String ejbContextName;
/*    */   private String ejbType;
/*    */   private String boProxyName;
/*    */ 
/*    */   private Properties getEjbJndiProps()
/*    */   {
/* 36 */     return this.ejbJndiProps;
/*    */   }
/*    */ 
/*    */   public String getEjbContextName() {
/* 40 */     return this.ejbContextName;
/*    */   }
/*    */ 
/*    */   public String getEjbType() {
/* 44 */     return this.ejbType;
/*    */   }
/*    */ 
/*    */   public void setEjbJndiProps(Properties ejbJndiProps) {
/* 48 */     this.ejbJndiProps = ejbJndiProps;
/*    */   }
/*    */ 
/*    */   public void setEjbContextName(String ejbContextName) {
/* 52 */     this.ejbContextName = ejbContextName;
/*    */   }
/*    */ 
/*    */   public void setEjbType(String ejbType) {
/* 56 */     this.ejbType = ejbType;
/*    */   }
/*    */ 
/*    */   public String getEjbJndiName(String boName) {
/* 60 */     return getEjbJndiProps().getProperty(boName);
/*    */   }
/*    */ 
/*    */   public void prepareBoProxy() {
/* 64 */     String[] boNames = getBoNames();
/* 65 */     for (int i = 0; i < boNames.length; i++) {
/* 66 */       String boName = boNames[i];
/* 67 */       EjbBoProxy ejbBoProxy = new EjbBoProxy(boName, getEjbContextName(), getEjbJndiName(boName), BoProxyType.valueOf(getEjbType()));
/*    */ 
/* 69 */       this.ejbBoProxyMap.put(boName, ejbBoProxy);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String[] getBoNames() {
/* 74 */     String[] boNames = new String[this.ejbJndiProps.size()];
/* 75 */     this.ejbJndiProps.keySet().toArray(boNames);
/* 76 */     return boNames;
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName) {
/* 80 */     return (EjbBoProxy)this.ejbBoProxyMap.get(boName);
/*    */   }
/*    */ 
/*    */   public IBoProxy getBoProxy(String boName, String url) {
/* 84 */     return (EjbBoProxy)this.ejbBoProxyMap.get(boName);
/*    */   }
/*    */ 
/*    */   public void setBoProxyName(String boProxyName) {
/* 88 */     this.boProxyName = boProxyName;
/*    */   }
/*    */ 
/*    */   public String getBoProxyName() {
/* 92 */     return this.boProxyName;
/*    */   }
/*    */ 
/*    */   public boolean isLocalBoProxy() {
/* 96 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.EjbProxyManager
 * JD-Core Version:    0.6.0
 */