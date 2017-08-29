/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import java.sql.Timestamp;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public abstract class AbstractDoHelper
/*    */ {
/* 24 */   private Map<String, Class> dtoAttrTypes = new HashMap();
/* 25 */   private Map<String, String> dtoAttrLabelCns = new HashMap();
/*    */ 
/*    */   public AbstractDoHelper() {
/* 28 */     this(true);
/*    */   }
/*    */ 
/*    */   public AbstractDoHelper(boolean isObjDto) {
/* 32 */     if (isObjDto) {
/* 33 */       putAttrType("OBJECTID", Long.TYPE);
/* 34 */       putAttrType("CREATE_TIME", Timestamp.class);
/* 35 */       putAttrType("LAST_MODIFY_TIME", Timestamp.class);
/*    */     }
/* 37 */     putAttrTypes();
/* 38 */     putAttrNames();
/*    */   }
/*    */ 
/*    */   public Class getAttrType(String attrName) {
/* 42 */     return (Class)this.dtoAttrTypes.get(attrName);
/*    */   }
/*    */ 
/*    */   public String[] getAllAttrNames() {
/* 46 */     String[] attrNames = new String[this.dtoAttrTypes.size()];
/* 47 */     this.dtoAttrTypes.keySet().toArray(attrNames);
/* 48 */     return attrNames;
/*    */   }
/*    */ 
/*    */   public void putAttrType(String attrName, Class attrType) {
/* 52 */     this.dtoAttrTypes.put(attrName, attrType);
/*    */   }
/*    */ 
/*    */   public void removeAttrType(String attrName) {
/* 56 */     this.dtoAttrTypes.remove(attrName);
/*    */   }
/*    */ 
/*    */   public void putAttrLabelCn(String attrName, String attrLabelCn) {
/* 60 */     this.dtoAttrLabelCns.put(attrName, attrLabelCn);
/*    */   }
/*    */ 
/*    */   public String getAttrLabelCn(String attrName) {
/* 64 */     return (String)this.dtoAttrLabelCns.get(attrName);
/*    */   }
/*    */ 
/*    */   public String[] getAllUserAttrNames() {
/* 68 */     String[] attrNames = new String[this.dtoAttrLabelCns.size()];
/* 69 */     this.dtoAttrLabelCns.keySet().toArray(attrNames);
/* 70 */     return attrNames;
/*    */   }
/*    */ 
/*    */   protected abstract void putAttrTypes();
/*    */ 
/*    */   protected void putAttrNames()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AbstractDoHelper
 * JD-Core Version:    0.6.0
 */