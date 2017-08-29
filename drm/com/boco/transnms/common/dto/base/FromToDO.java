/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class FromToDO extends GenericDO
/*    */ {
/*    */   public static final String CLASS_NAME = "FromToDO";
/*    */ 
/*    */   public FromToDO()
/*    */   {
/* 12 */     super("FromToDO");
/*    */   }
/*    */ 
/*    */   public void setFromObject(GenericDO fromObj) {
/* 16 */     super.setAttrValue("FROM_OBJECT", fromObj);
/*    */   }
/*    */ 
/*    */   public void setToObject(GenericDO toObj) {
/* 20 */     super.setAttrValue("TO_OBJECT", toObj);
/*    */   }
/*    */ 
/*    */   public GenericDO getFromObject() {
/* 24 */     return (GenericDO)super.getAttrValue("FROM_OBJECT");
/*    */   }
/*    */ 
/*    */   public GenericDO getToObject() {
/* 28 */     return (GenericDO)super.getAttrValue("TO_OBJECT");
/*    */   }
/*    */ 
/*    */   public static class AttrName
/*    */   {
/*    */     public static final String fromObject = "FROM_OBJECT";
/*    */     public static final String toObject = "TO_OBJECT";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.FromToDO
 * JD-Core Version:    0.6.0
 */