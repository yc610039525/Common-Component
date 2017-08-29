/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class AttrAddition
/*    */   implements Serializable
/*    */ {
/*    */   private String attrName;
/*    */   private String attrAddition;
/*    */   private Serializable attrValue;
/*    */   private AttrAdditionType attrAdditionType;
/*    */ 
/*    */   public AttrAddition(String attrName, Object attrValue, String attrAddition)
/*    */   {
/* 36 */     this(attrName, attrValue, attrAddition, AttrAdditionType.NORMAL);
/*    */   }
/*    */ 
/*    */   public AttrAddition(String attrName, Object attrValue, String attrAddition, AttrAdditionType attrAdditionType)
/*    */   {
/* 41 */     this.attrName = attrName;
/* 42 */     this.attrAddition = attrAddition;
/* 43 */     this.attrValue = ((Serializable)attrValue);
/* 44 */     this.attrAdditionType = attrAdditionType;
/*    */   }
/*    */ 
/*    */   public void setAttrName(String attrName) {
/* 48 */     this.attrName = attrName;
/*    */   }
/*    */ 
/*    */   public void setAttrAddition(String attrAddition) {
/* 52 */     this.attrAddition = attrAddition;
/*    */   }
/*    */ 
/*    */   public void setAttrValue(Object attrValue) {
/* 56 */     this.attrValue = ((Serializable)attrValue);
/*    */   }
/*    */ 
/*    */   public String getAttrName() {
/* 60 */     return this.attrName;
/*    */   }
/*    */ 
/*    */   public String getAttrAddition() {
/* 64 */     return this.attrAddition;
/*    */   }
/*    */ 
/*    */   public Object getAttrValue() {
/* 68 */     return this.attrValue;
/*    */   }
/*    */ 
/*    */   public void setAdditionType(AttrAdditionType additionType) {
/* 72 */     this.attrAdditionType = additionType;
/*    */   }
/*    */ 
/*    */   public AttrAdditionType getAdditionType() {
/* 76 */     return this.attrAdditionType;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 80 */     String str = "";
/* 81 */     if (this.attrValue != null) {
/* 82 */       str = this.attrValue.toString();
/*    */     }
/* 84 */     return str;
/*    */   }
/*    */ 
/*    */   public static enum AttrAdditionType
/*    */   {
/* 25 */     ICON, 
/* 26 */     URL, 
/* 27 */     NORMAL;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AttrAddition
 * JD-Core Version:    0.6.0
 */