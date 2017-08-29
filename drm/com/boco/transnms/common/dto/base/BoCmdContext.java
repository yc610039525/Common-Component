/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class BoCmdContext extends AttrObject
/*    */   implements Cloneable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Class[] paraClassTypes;
/* 32 */   private boolean compressed = true;
/*    */ 
/*    */   public BoCmdContext() {
/*    */   }
/*    */ 
/*    */   public BoCmdContext(String boName, String methodName) {
/* 38 */     setBoName(boName);
/* 39 */     setMethodName(methodName);
/*    */   }
/*    */ 
/*    */   public BoCmdContext(String boName, String methodName, String userId) {
/* 43 */     setBoName(boName);
/* 44 */     setMethodName(methodName);
/*    */   }
/*    */ 
/*    */   public void setBoName(String boName) {
/* 48 */     super.setAttrValue("boName", boName);
/*    */   }
/*    */ 
/*    */   public void setMethodName(String methodName) {
/* 52 */     super.setAttrValue("methodName", methodName);
/*    */   }
/*    */ 
/*    */   public void setBoProxyName(String boProxyName) {
/* 56 */     super.setAttrValue("boProxyName", boProxyName);
/*    */   }
/*    */   public String getBoName() {
/* 59 */     return super.getAttrString("boName");
/*    */   }
/*    */ 
/*    */   public String getMethodName() {
/* 63 */     return super.getAttrString("methodName");
/*    */   }
/*    */ 
/*    */   public String getBoProxyName() {
/* 67 */     return super.getAttrString("boProxyName");
/*    */   }
/*    */ 
/*    */   public Class[] getParaClassTypes() {
/* 71 */     return this.paraClassTypes;
/*    */   }
/*    */ 
/*    */   public void setParaClassTypes(Class[] paraClassTypes) {
/* 75 */     this.paraClassTypes = paraClassTypes;
/*    */   }
/*    */ 
/*    */   public boolean isCompressed() {
/* 79 */     return this.compressed;
/*    */   }
/*    */ 
/*    */   public void setCompressed(boolean compressed) {
/* 83 */     this.compressed = compressed;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 87 */     String str = "BoCmdContext[";
/* 88 */     str = str + "boName=" + getBoName();
/* 89 */     str = str + ", methodName=" + getMethodName();
/* 90 */     str = str + "]";
/* 91 */     return str;
/*    */   }
/*    */ 
/*    */   private static class AttrName
/*    */   {
/*    */     private static final String boName = "boName";
/*    */     private static final String methodName = "methodName";
/*    */     private static final String boProxyName = "boProxyName";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.BoCmdContext
 * JD-Core Version:    0.6.0
 */