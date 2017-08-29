/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class DummyDO extends GenericDO
/*    */ {
/* 21 */   private static String CLASSNAME = "DummyDO";
/* 22 */   private static String NAME = "LABEL_CN";
/*    */ 
/*    */   public DummyDO() {
/* 25 */     super(CLASSNAME);
/*    */   }
/*    */ 
/*    */   public DummyDO(GenericDO dbo) {
/* 29 */     super(CLASSNAME);
/* 30 */     copy(dbo);
/*    */   }
/*    */ 
/*    */   public void copy(GenericDO dbo) {
/* 34 */     dbo.copyTo(this);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 38 */     return (String)getAttrValue(NAME);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DummyDO
 * JD-Core Version:    0.6.0
 */