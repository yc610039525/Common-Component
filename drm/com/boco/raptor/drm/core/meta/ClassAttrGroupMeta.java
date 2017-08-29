/*    */ package com.boco.raptor.drm.core.meta;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ClassAttrGroupMeta
/*    */   implements Serializable
/*    */ {
/*    */   private String groupCuid;
/*    */   private String labelCn;
/*    */   private long sortNo;
/* 29 */   private List<ExtAttrMeta> extAttrMetas = new ArrayList();
/*    */ 
/*    */   public List<String> getGroupAttrIds()
/*    */   {
/* 35 */     List groupAttrIds = new ArrayList();
/* 36 */     for (ExtAttrMeta attrMeta : this.extAttrMetas) {
/* 37 */       groupAttrIds.add(attrMeta.getAttrId());
/*    */     }
/* 39 */     return groupAttrIds;
/*    */   }
/*    */ 
/*    */   public List<ExtAttrMeta> getExtAttrMetas() {
/* 43 */     return this.extAttrMetas;
/*    */   }
/*    */ 
/*    */   public String getGroupCuid() {
/* 47 */     return this.groupCuid;
/*    */   }
/*    */ 
/*    */   public String getLabelCn() {
/* 51 */     return this.labelCn;
/*    */   }
/*    */ 
/*    */   public long getSortNo() {
/* 55 */     return this.sortNo;
/*    */   }
/*    */ 
/*    */   public void setExtAttrMetas(List<ExtAttrMeta> extAttrMetas) {
/* 59 */     this.extAttrMetas = extAttrMetas;
/*    */   }
/*    */ 
/*    */   public void setGroupCuid(String groupCuid) {
/* 63 */     this.groupCuid = groupCuid;
/*    */   }
/*    */ 
/*    */   public void setLabelCn(String labelCn) {
/* 67 */     this.labelCn = labelCn;
/*    */   }
/*    */ 
/*    */   public void setSortNo(long sortNo) {
/* 71 */     this.sortNo = sortNo;
/*    */   }
/*    */ 
/*    */   public void addExtAttrMeta(ExtAttrMeta extAttrMeta) {
/* 75 */     this.extAttrMetas.add(extAttrMeta);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ClassAttrGroupMeta
 * JD-Core Version:    0.6.0
 */