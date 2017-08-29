/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ExtAttrMetaGroup
/*     */   implements Serializable
/*     */ {
/*     */   private String bmClassId;
/*     */   private String dbClassId;
/*     */   private String groupCuid;
/*     */   private String userCuid;
/*     */   private int groupType;
/*  40 */   private List<ExtAttrMeta> extAttrMetas = new ArrayList();
/*     */ 
/*     */   public List<String> getGroupAttrIds()
/*     */   {
/*  46 */     List groupAttrIds = new ArrayList();
/*  47 */     for (ExtAttrMeta attrMeta : this.extAttrMetas) {
/*  48 */       groupAttrIds.add(attrMeta.getAttrId());
/*     */     }
/*  50 */     return groupAttrIds;
/*     */   }
/*     */ 
/*     */   public List<ExtAttrMeta> getExtAttrMetas() {
/*  54 */     return this.extAttrMetas;
/*     */   }
/*     */ 
/*     */   public String getGroupCuid() {
/*  58 */     return this.groupCuid;
/*     */   }
/*     */ 
/*     */   public String getDbClassId() {
/*  62 */     return this.dbClassId;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/*  66 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public int getGroupType() {
/*  70 */     return this.groupType;
/*     */   }
/*     */ 
/*     */   public String getUserCuid() {
/*  74 */     return this.userCuid;
/*     */   }
/*     */ 
/*     */   public void setExtAttrMetas(List<ExtAttrMeta> extAttrMetas)
/*     */   {
/*  79 */     this.extAttrMetas = extAttrMetas;
/*     */   }
/*     */ 
/*     */   public void setGroupCuid(String groupCuid) {
/*  83 */     this.groupCuid = groupCuid;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/*  87 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setDbClassId(String dbClassId) {
/*  91 */     this.dbClassId = dbClassId;
/*     */   }
/*     */ 
/*     */   public void setUserCuid(String userCuid) {
/*  95 */     this.userCuid = userCuid;
/*     */   }
/*     */ 
/*     */   public void setGroupType(int groupType) {
/*  99 */     this.groupType = groupType;
/*     */   }
/*     */ 
/*     */   public void setGroupType(Integer groupType) {
/* 103 */     this.groupType = groupType.intValue();
/*     */   }
/*     */ 
/*     */   public void addExtAttrMeta(ExtAttrMeta extAttrMeta) {
/* 107 */     this.extAttrMetas.add(extAttrMeta);
/*     */   }
/*     */ 
/*     */   public static class GROUP_TYPE_ENUM
/*     */   {
/*  32 */     public static int QUERY_LIST = 1;
/*  33 */     public static int PROPERTY = 2;
/*  34 */     public static int IMPORT = 3;
/*  35 */     public static int EXPORT = 4;
/*  36 */     public static int DATA_CHECK = 5;
/*  37 */     public static int QUERY_TEMPLATE = 6;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ExtAttrMetaGroup
 * JD-Core Version:    0.6.0
 */