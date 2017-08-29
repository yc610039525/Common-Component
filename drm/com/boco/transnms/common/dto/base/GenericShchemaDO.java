/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class GenericShchemaDO extends GenericDO
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 29 */   private static final String[] attrNames = { "TOOBJID", "FROMOBJID", "LINKOBJID" };
/*    */ 
/*    */   public GenericShchemaDO(String className)
/*    */   {
/* 33 */     super(className);
/*    */   }
/*    */ 
/*    */   public Class getAttrType(String attrName) {
/* 37 */     return Long.TYPE;
/*    */   }
/*    */ 
/*    */   public String[] getAllAttrNames() {
/* 41 */     return attrNames;
/*    */   }
/*    */ 
/*    */   public void setToObjId(long toObjId) {
/* 45 */     super.setAttrValue("TOOBJID", toObjId);
/*    */   }
/*    */ 
/*    */   public void setFromObjId(long fromObjId) {
/* 49 */     super.setAttrValue("FROMOBJID", fromObjId);
/*    */   }
/*    */ 
/*    */   public void setLinkObjId(long linkObjId) {
/* 53 */     super.setAttrValue("LINKOBJID", linkObjId);
/*    */   }
/*    */ 
/*    */   public long getToObjId() {
/* 57 */     return super.getAttrLong("TOOBJID");
/*    */   }
/*    */ 
/*    */   public long getFromObjId() {
/* 61 */     return super.getAttrLong("FROMOBJID");
/*    */   }
/*    */ 
/*    */   public long getLinkObjId() {
/* 65 */     return super.getAttrLong("LINKOBJID");
/*    */   }
/*    */ 
/*    */   public static class AttrName
/*    */   {
/*    */     public static final String toObjId = "TOOBJID";
/*    */     public static final String fromObjId = "FROMOBJID";
/*    */     public static final String linkObjId = "LINKOBJID";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.GenericShchemaDO
 * JD-Core Version:    0.6.0
 */