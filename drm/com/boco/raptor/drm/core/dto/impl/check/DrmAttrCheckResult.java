/*    */ package com.boco.raptor.drm.core.dto.impl.check;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.impl.DrmRelatedIdValue;
/*    */ 
/*    */ public class DrmAttrCheckResult extends DrmRelatedIdValue
/*    */ {
/*    */   private DrmCheckErrorTypeEnum errorTypeEnum;
/*    */   private DrmCheckTypeEnum checkTypeEnum;
/*    */ 
/*    */   public void setErrorType(String errorType)
/*    */   {
/* 31 */     this.errorTypeEnum = DrmCheckErrorTypeEnum.valueOf(errorType);
/*    */   }
/*    */ 
/*    */   public void _setErrorType(DrmCheckErrorTypeEnum errorTypeEnum) {
/* 35 */     this.errorTypeEnum = errorTypeEnum;
/*    */   }
/*    */ 
/*    */   public void setCheckType(String checkType) {
/* 39 */     this.checkTypeEnum = DrmCheckTypeEnum.valueOf(checkType);
/*    */   }
/*    */ 
/*    */   public void _setCheckType(DrmCheckTypeEnum checkTypeEnum) {
/* 43 */     this.checkTypeEnum = checkTypeEnum;
/*    */   }
/*    */ 
/*    */   public DrmCheckErrorTypeEnum _getErrorType() {
/* 47 */     return this.errorTypeEnum;
/*    */   }
/*    */ 
/*    */   public String getErrorType() {
/* 51 */     return this.errorTypeEnum != null ? this.errorTypeEnum.toString() : null;
/*    */   }
/*    */ 
/*    */   public String getCheckType() {
/* 55 */     return this.checkTypeEnum != null ? this.checkTypeEnum.toString() : null;
/*    */   }
/*    */ 
/*    */   public DrmCheckTypeEnum _getCheckType() {
/* 59 */     return this.checkTypeEnum;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckResult
 * JD-Core Version:    0.6.0
 */