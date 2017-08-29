/*    */ package com.boco.raptor.drm.core.meta;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ExcelImpTemplateRelatedMeta
/*    */   implements Serializable
/*    */ {
/*    */   private String templateRelatedCuid;
/*    */   private String templateCuid;
/*    */   private String templateRelatedBmClassId;
/*    */   private String classUniqueName;
/*    */   private String parentBmClassId;
/*    */   private String parentAttrId;
/*    */   private String relatedOrUnique;
/*    */ 
/*    */   public String getTemplateRelatedCuid()
/*    */   {
/* 37 */     return this.templateRelatedCuid;
/*    */   }
/*    */ 
/*    */   public String getTemplateRelatedBmClassId() {
/* 41 */     return this.templateRelatedBmClassId;
/*    */   }
/*    */ 
/*    */   public String getTemplateCuid() {
/* 45 */     return this.templateCuid;
/*    */   }
/*    */ 
/*    */   public void setClassUniqueName(String classUniqueName) {
/* 49 */     this.classUniqueName = classUniqueName;
/*    */   }
/*    */ 
/*    */   public void setTemplateRelatedCuid(String templateRelatedCuid) {
/* 53 */     this.templateRelatedCuid = templateRelatedCuid;
/*    */   }
/*    */ 
/*    */   public void setTemplateRelatedBmClassId(String templateRelatedBmClassId) {
/* 57 */     this.templateRelatedBmClassId = templateRelatedBmClassId;
/*    */   }
/*    */ 
/*    */   public void setTemplateCuid(String templateCuid) {
/* 61 */     this.templateCuid = templateCuid;
/*    */   }
/*    */ 
/*    */   public void setRelatedOrUnique(String relatedOrUnique) {
/* 65 */     this.relatedOrUnique = relatedOrUnique;
/*    */   }
/*    */ 
/*    */   public void setParentBmClassId(String parentBmClassId) {
/* 69 */     this.parentBmClassId = parentBmClassId;
/*    */   }
/*    */ 
/*    */   public void setParentAttrId(String parentAttrId) {
/* 73 */     this.parentAttrId = parentAttrId;
/*    */   }
/*    */ 
/*    */   public String getClassUniqueName() {
/* 77 */     return this.classUniqueName;
/*    */   }
/*    */ 
/*    */   public String getRelatedOrUnique() {
/* 81 */     return this.relatedOrUnique;
/*    */   }
/*    */ 
/*    */   public String getParentBmClassId() {
/* 85 */     return this.parentBmClassId;
/*    */   }
/*    */ 
/*    */   public String getParentAttrId() {
/* 89 */     return this.parentAttrId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ExcelImpTemplateRelatedMeta
 * JD-Core Version:    0.6.0
 */