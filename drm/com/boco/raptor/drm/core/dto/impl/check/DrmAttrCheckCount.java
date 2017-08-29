/*    */ package com.boco.raptor.drm.core.dto.impl.check;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DrmAttrCheckCount
/*    */   implements Serializable
/*    */ {
/*    */   private String bmClassId;
/*    */   private String attrId;
/*    */   private int count;
/*    */   private DrmCheckTypeEnum checkTypeEnum;
/*    */   private DrmCheckErrorTypeEnum errorTypeEnum;
/*    */ 
/*    */   public void setErrorType(String errorType)
/*    */   {
/* 33 */     this.errorTypeEnum = DrmCheckErrorTypeEnum.valueOf(errorType);
/*    */   }
/*    */ 
/*    */   public void _setErrorType(DrmCheckErrorTypeEnum errorTypeEnum) {
/* 37 */     this.errorTypeEnum = errorTypeEnum;
/*    */   }
/*    */ 
/*    */   public void setCheckType(String checkType) {
/* 41 */     this.checkTypeEnum = DrmCheckTypeEnum.valueOf(checkType);
/*    */   }
/*    */ 
/*    */   public void _setCheckType(DrmCheckTypeEnum checkTypeEnum) {
/* 45 */     this.checkTypeEnum = checkTypeEnum;
/*    */   }
/*    */ 
/*    */   public DrmCheckErrorTypeEnum _getErrorType() {
/* 49 */     return this.errorTypeEnum;
/*    */   }
/*    */ 
/*    */   public String getErrorType() {
/* 53 */     return this.errorTypeEnum != null ? this.errorTypeEnum.toString() : null;
/*    */   }
/*    */ 
/*    */   public String getCheckType() {
/* 57 */     return this.checkTypeEnum != null ? this.checkTypeEnum.toString() : null;
/*    */   }
/*    */ 
/*    */   public DrmCheckTypeEnum _getCheckType() {
/* 61 */     return this.checkTypeEnum;
/*    */   }
/*    */ 
/*    */   public String getAttrId() {
/* 65 */     return this.attrId;
/*    */   }
/*    */ 
/*    */   public int getCount() {
/* 69 */     return this.count;
/*    */   }
/*    */ 
/*    */   public String getBmClassId() {
/* 73 */     return this.bmClassId;
/*    */   }
/*    */ 
/*    */   public void setAttrId(String attrId) {
/* 77 */     this.attrId = attrId;
/*    */   }
/*    */ 
/*    */   public void setCount(int count) {
/* 81 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public void setBmClassId(String bmClassId) {
/* 85 */     this.bmClassId = bmClassId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckCount
 * JD-Core Version:    0.6.0
 */