/*    */ package com.boco.raptor.drm.core.plugin.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*    */ import com.boco.raptor.drm.core.plugin.IMultiAttrParser;
/*    */ 
/*    */ public class BasicMultiAttrParser
/*    */   implements IMultiAttrParser
/*    */ {
/*  7 */   private String splitChar = ",";
/*    */ 
/*    */   public String[] parseValues(BMAttrMeta attrMeta, String attrValue)
/*    */   {
/* 12 */     if (attrValue == null) return new String[0];
/* 13 */     String[] values = attrValue.split(this.splitChar);
/* 14 */     for (int i = 0; i < values.length; i++) {
/* 15 */       values[i] = values[i].trim();
/*    */     }
/* 17 */     return values;
/*    */   }
/*    */ 
/*    */   public String getSplitChar() {
/* 21 */     return this.splitChar;
/*    */   }
/*    */ 
/*    */   public void setSplitChar(String splitChar) {
/* 25 */     this.splitChar = splitChar;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.plugin.impl.BasicMultiAttrParser
 * JD-Core Version:    0.6.0
 */