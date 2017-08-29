/*    */ package com.boco.common.util.web;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class LabelValueBean
/*    */   implements Serializable
/*    */ {
/*    */   private String value;
/*    */   private String label;
/*    */   private Object addition;
/*    */ 
/*    */   public LabelValueBean(String label, String value)
/*    */   {
/* 31 */     this.label = label;
/* 32 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public LabelValueBean(String label, String value, Object addition) {
/* 36 */     this.label = label;
/* 37 */     this.value = value;
/* 38 */     this.addition = addition;
/*    */   }
/*    */ 
/*    */   public String getLabel() {
/* 42 */     return this.label;
/*    */   }
/*    */ 
/*    */   public void setLabel(String label) {
/* 46 */     this.label = label;
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 50 */     return this.value;
/*    */   }
/*    */ 
/*    */   public Object getAddition() {
/* 54 */     return this.addition;
/*    */   }
/*    */ 
/*    */   public void setValue(String value) {
/* 58 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public void setAddition(Object addition) {
/* 62 */     this.addition = addition;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 66 */     StringBuffer sb = new StringBuffer("LabelValueBean[");
/* 67 */     sb.append("label=" + this.label);
/* 68 */     sb.append(", value=" + this.value);
/* 69 */     sb.append(", addition=" + this.addition);
/* 70 */     sb.append("]");
/* 71 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.web.LabelValueBean
 * JD-Core Version:    0.6.0
 */