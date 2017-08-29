/*    */ package com.boco.raptor.drm.core.dto.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.IDrmLabelValue;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class DrmLabelValue
/*    */   implements IDrmLabelValue
/*    */ {
/*    */   private Object dboId;
/*    */   private Object value;
/*    */   private String label;
/*    */   private Map valueMap;
/*    */ 
/*    */   public Object getValue()
/*    */   {
/* 34 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String getLabel() {
/* 38 */     return this.label;
/*    */   }
/*    */ 
/*    */   public Object getDboId() {
/* 42 */     return this.dboId;
/*    */   }
/*    */ 
/*    */   public Map getExtMapValue() {
/* 46 */     return this.valueMap;
/*    */   }
/*    */ 
/*    */   public void setValue(Object value) {
/* 50 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public void setLabel(String label) {
/* 54 */     this.label = label;
/*    */   }
/*    */ 
/*    */   public void setDboId(Object dboId) {
/* 58 */     this.dboId = dboId;
/*    */   }
/*    */ 
/*    */   public void setExtMapValue(Map valueMap) {
/* 62 */     this.valueMap = valueMap;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 66 */     return "LabelValue[label=" + this.label + ", value=" + this.value + ", valueMap=" + this.valueMap + "]";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.DrmLabelValue
 * JD-Core Version:    0.6.0
 */