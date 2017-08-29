/*    */ package com.boco.raptor.drm.core.dto.impl.check;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DrmCheckAttrId
/*    */   implements Serializable
/*    */ {
/*    */   private String checkAttrId;
/*    */   private DrmCheckTypeEnum checkTypeEnum;
/*    */ 
/*    */   public String getCheckType()
/*    */   {
/* 31 */     return this.checkTypeEnum != null ? this.checkTypeEnum.toString() : null;
/*    */   }
/*    */ 
/*    */   public DrmCheckTypeEnum _getCheckType() {
/* 35 */     return this.checkTypeEnum;
/*    */   }
/*    */ 
/*    */   public String getCheckAttrId() {
/* 39 */     return this.checkAttrId;
/*    */   }
/*    */ 
/*    */   public void setCheckAttrId(String checkAttrId) {
/* 43 */     this.checkAttrId = checkAttrId;
/*    */   }
/*    */ 
/*    */   public void setCheckType(String checkType) {
/* 47 */     this.checkTypeEnum = DrmCheckTypeEnum.valueOf(checkType);
/*    */   }
/*    */ 
/*    */   public void _setCheckType(DrmCheckTypeEnum checkTypeEnum) {
/* 51 */     this.checkTypeEnum = checkTypeEnum;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.check.DrmCheckAttrId
 * JD-Core Version:    0.6.0
 */