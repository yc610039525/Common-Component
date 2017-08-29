/*    */ package com.boco.raptor.drm.core.meta;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class TemplateMeta
/*    */   implements Serializable
/*    */ {
/*    */   private String templateCuid;
/*    */   private String templateLabelCn;
/*    */   private long sortNum;
/*    */   private ExtAttrMetaGroup extAttrMetaGroup;
/* 29 */   private Map<String, Object> templateExtMetas = new HashMap();
/*    */ 
/*    */   public ExtAttrMetaGroup getExtAttrMetaGroup()
/*    */   {
/* 35 */     return this.extAttrMetaGroup;
/*    */   }
/*    */ 
/*    */   public String getTemplateCuid() {
/* 39 */     return this.templateCuid;
/*    */   }
/*    */ 
/*    */   public String getTemplateLabelCn() {
/* 43 */     return this.templateLabelCn;
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getTemplateExtMetas() {
/* 47 */     return this.templateExtMetas;
/*    */   }
/*    */ 
/*    */   public long getSortNum() {
/* 51 */     return this.sortNum;
/*    */   }
/*    */ 
/*    */   public void setExtAttrMetaGroup(ExtAttrMetaGroup extAttrMetaGroup) {
/* 55 */     this.extAttrMetaGroup = extAttrMetaGroup;
/*    */   }
/*    */ 
/*    */   public void setTemplateCuid(String templateCuid) {
/* 59 */     this.templateCuid = templateCuid;
/*    */   }
/*    */ 
/*    */   public void setTemplateLabelCn(String templateLabelCn) {
/* 63 */     this.templateLabelCn = templateLabelCn;
/*    */   }
/*    */ 
/*    */   public void setTemplateExtMetas(Map templateExtMetas) {
/* 67 */     this.templateExtMetas = templateExtMetas;
/*    */   }
/*    */ 
/*    */   public void setSortNum(long sortNum) {
/* 71 */     this.sortNum = sortNum;
/*    */   }
/*    */ 
/*    */   public void addTemplateExtMeta(String extName, String extValue) {
/* 75 */     this.templateExtMetas.put(extName, extValue);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.TemplateMeta
 * JD-Core Version:    0.6.0
 */