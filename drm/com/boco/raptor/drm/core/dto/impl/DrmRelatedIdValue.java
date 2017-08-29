/*    */ package com.boco.raptor.drm.core.dto.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.IDrmRelatedIdValue;
/*    */ 
/*    */ public class DrmRelatedIdValue extends DrmLabelValue
/*    */   implements IDrmRelatedIdValue
/*    */ {
/*    */   private String dbClassId;
/*    */   private String bmClassId;
/*    */   private String attrId;
/*    */ 
/*    */   public String getAttrId()
/*    */   {
/* 29 */     return this.attrId;
/*    */   }
/*    */ 
/*    */   public String getDbClassId() {
/* 33 */     return this.dbClassId;
/*    */   }
/*    */ 
/*    */   public void setAttrId(String attrId) {
/* 37 */     this.attrId = attrId;
/*    */   }
/*    */ 
/*    */   public void setDbClassId(String dbClassId) {
/* 41 */     this.dbClassId = dbClassId;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 45 */     return "关联ID名称[label=" + super.getLabel() + ", value=" + super.getValue() + ", dbClassId=" + this.dbClassId + ", attrId=" + this.attrId + "]";
/*    */   }
/*    */ 
/*    */   public String getBmClassId()
/*    */   {
/* 50 */     return this.bmClassId;
/*    */   }
/*    */ 
/*    */   public void setBmClassId(String bmClassId) {
/* 54 */     this.bmClassId = bmClassId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.DrmRelatedIdValue
 * JD-Core Version:    0.6.0
 */