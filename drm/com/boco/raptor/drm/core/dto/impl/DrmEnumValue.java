/*    */ package com.boco.raptor.drm.core.dto.impl;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*    */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*    */ import org.apache.commons.lang.builder.ToStringBuilder;
/*    */ import org.apache.commons.lang.builder.ToStringStyle;
/*    */ 
/*    */ public class DrmEnumValue<T>
/*    */   implements IDrmEnumValue<T>
/*    */ {
/*    */   private String enumName;
/*    */   private T enumValue;
/*    */   private IDrmDataObject dbo;
/*    */ 
/*    */   public DrmEnumValue()
/*    */   {
/*    */   }
/*    */ 
/*    */   public DrmEnumValue(String enumName, T enumValue)
/*    */   {
/* 36 */     this.enumName = enumName;
/* 37 */     this.enumValue = enumValue;
/*    */   }
/*    */ 
/*    */   public String getEnumName() {
/* 41 */     return this.enumName;
/*    */   }
/*    */ 
/*    */   public T getEnumValue() {
/* 45 */     return this.enumValue;
/*    */   }
/*    */ 
/*    */   public void setEnumValue(T enumValue) {
/* 49 */     if (enumValue == null) {
/* 50 */       throw new UserException("枚举值为空");
/*    */     }
/* 52 */     this.enumValue = enumValue;
/*    */   }
/*    */ 
/*    */   public void setEnumName(String enumName) {
/* 56 */     this.enumName = enumName;
/*    */   }
/*    */ 
/*    */   public boolean isEnumValueEqual(Object _enumValue) {
/* 60 */     if (_enumValue == null) return false;
/* 61 */     String evalue = this.enumValue.toString();
/* 62 */     String _evalue = _enumValue.toString();
/* 63 */     return _evalue.equals(evalue);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 67 */     return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
/*    */   }
/*    */ 
/*    */   public IDrmDataObject getEnumDrmDataObject() {
/* 71 */     return this.dbo;
/*    */   }
/*    */ 
/*    */   public void setEnumDrmDataObject(IDrmDataObject dbo) {
/* 75 */     this.dbo = dbo;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.DrmEnumValue
 * JD-Core Version:    0.6.0
 */