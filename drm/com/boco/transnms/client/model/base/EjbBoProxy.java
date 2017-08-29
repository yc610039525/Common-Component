/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ public final class EjbBoProxy extends AbstractBoProxy
/*    */ {
/*    */   private BoProxyType ejbProxyType;
/*    */   private String ejbContextName;
/*    */   private String ejbJndiName;
/*    */ 
/*    */   public EjbBoProxy()
/*    */   {
/*    */   }
/*    */ 
/*    */   public EjbBoProxy(String boName, String _ejbContextName, String _ejbJndiName, BoProxyType _boProxyType)
/*    */   {
/* 43 */     super.setBoName(boName);
/* 44 */     setEjbContextName(_ejbContextName);
/* 45 */     setEjbJndiName(_ejbJndiName);
/* 46 */     setEjbProxyType(_boProxyType);
/*    */   }
/*    */ 
/*    */   public boolean isLocalEjb()
/*    */   {
/* 54 */     return getBoProxyType() == BoProxyType.EJB_LOCAL_TYPE;
/*    */   }
/*    */ 
/*    */   protected Object getBoProxy()
/*    */     throws Exception
/*    */   {
/* 68 */     return null;
/*    */   }
/*    */ 
/*    */   public void setEjbProxyType(BoProxyType ejbProxyType) {
/* 72 */     this.ejbProxyType = ejbProxyType;
/*    */   }
/*    */ 
/*    */   public void setEjbContextName(String ejbContextName) {
/* 76 */     this.ejbContextName = ejbContextName;
/*    */   }
/*    */ 
/*    */   public void setEjbJndiName(String ejbJndiName) {
/* 80 */     this.ejbJndiName = ejbJndiName;
/*    */   }
/*    */ 
/*    */   public BoProxyType getBoProxyType() {
/* 84 */     return this.ejbProxyType;
/*    */   }
/*    */ 
/*    */   public String getEjbContextName() {
/* 88 */     return this.ejbContextName;
/*    */   }
/*    */ 
/*    */   public String getEjbJndiName() {
/* 92 */     return this.ejbJndiName;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.EjbBoProxy
 * JD-Core Version:    0.6.0
 */