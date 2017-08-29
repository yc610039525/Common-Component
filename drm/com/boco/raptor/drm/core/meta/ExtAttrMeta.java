/*    */ package com.boco.raptor.drm.core.meta;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ExtAttrMeta
/*    */   implements Serializable
/*    */ {
/*    */   private String bmClassId;
/*    */   private String attrId;
/*    */   private Map extMetas;
/*    */ 
/*    */   public ExtAttrMeta()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ExtAttrMeta(String bmClassId, String attrId)
/*    */   {
/* 33 */     this.bmClassId = bmClassId;
/* 34 */     this.attrId = attrId;
/*    */   }
/*    */ 
/*    */   public Map getExtMetas() {
/* 38 */     return this.extMetas;
/*    */   }
/*    */ 
/*    */   public String getAttrId() {
/* 42 */     return this.attrId;
/*    */   }
/*    */ 
/*    */   public String getBmClassId() {
/* 46 */     return this.bmClassId;
/*    */   }
/*    */ 
/*    */   public void setAttrId(String attrId) {
/* 50 */     this.attrId = attrId;
/*    */   }
/*    */ 
/*    */   public void setBmClassId(String bmClassId) {
/* 54 */     this.bmClassId = bmClassId;
/*    */   }
/*    */ 
/*    */   public void setExtAttrMeta(String metaName, Object value) {
/* 58 */     if (this.extMetas == null) {
/* 59 */       this.extMetas = new HashMap();
/*    */     }
/* 61 */     this.extMetas.put(metaName, value);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ExtAttrMeta
 * JD-Core Version:    0.6.0
 */