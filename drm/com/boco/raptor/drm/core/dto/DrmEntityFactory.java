/*     */ package com.boco.raptor.drm.core.dto;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.bean.EntityBeanFactory;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmRelatedIdValue;
/*     */ import com.boco.transnms.common.dto.base.BoQueryContext;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DrmEntityFactory
/*     */ {
/*  31 */   private static final DrmEntityFactory instance = new DrmEntityFactory();
/*     */ 
/*     */   public static DrmEntityFactory getInstance()
/*     */   {
/*  37 */     return instance;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject createDataObject() {
/*  41 */     return (IDrmDataObject)createEntity(IDrmDataObject.class.getSimpleName(), GenericDO.class);
/*     */   }
/*     */ 
/*     */   public IDrmQueryContext createQueryContext() {
/*  45 */     return (IDrmQueryContext)createEntity(IDrmQueryContext.class.getSimpleName(), BoQueryContext.class);
/*     */   }
/*     */ 
/*     */   public IDrmMemQueryContext createMemQueryContext() {
/*  49 */     return (IDrmMemQueryContext)createEntity(IDrmMemQueryContext.class.getSimpleName(), DrmMemQueryContext.class);
/*     */   }
/*     */ 
/*     */   public IDrmEnumValue<Long> createEnumValue() {
/*  53 */     return (IDrmEnumValue)createEntity(IDrmEnumValue.class.getSimpleName(), DrmEnumValue.class);
/*     */   }
/*     */ 
/*     */   public IDrmEnumValue createEnumValue(String enumName, Object enumValue) {
/*  57 */     IDrmEnumValue enumEntity = (IDrmEnumValue)createEntity(IDrmEnumValue.class.getSimpleName(), DrmEnumValue.class);
/*  58 */     enumEntity.setEnumName(enumName);
/*  59 */     enumEntity.setEnumValue(enumValue);
/*  60 */     return enumEntity;
/*     */   }
/*     */ 
/*     */   public IDrmEnumValue createEnumValue(String enumName, Object enumValue, IDrmDataObject dbo) {
/*  64 */     IDrmEnumValue enumEntity = (IDrmEnumValue)createEntity(IDrmEnumValue.class.getSimpleName(), DrmEnumValue.class);
/*  65 */     enumEntity.setEnumName(enumName);
/*  66 */     enumEntity.setEnumValue(enumValue);
/*  67 */     enumEntity.setEnumDrmDataObject(dbo);
/*  68 */     return enumEntity;
/*     */   }
/*     */ 
/*     */   public IDrmRelatedIdValue createRelatedIdValue(Object dboId, String bmClassId, String dbClassId, String attrId, Object value, String label) {
/*  72 */     IDrmRelatedIdValue idValue = (IDrmRelatedIdValue)createEntity(IDrmRelatedIdValue.class.getSimpleName(), DrmRelatedIdValue.class);
/*  73 */     idValue.setDbClassId(dbClassId);
/*  74 */     idValue.setBmClassId(bmClassId);
/*  75 */     idValue.setDboId(dboId);
/*  76 */     idValue.setValue(value);
/*  77 */     idValue.setLabel(label);
/*  78 */     idValue.setAttrId(attrId);
/*  79 */     return idValue;
/*     */   }
/*     */ 
/*     */   public IDrmRelatedIdValue createRelatedIdValue() {
/*  83 */     return (IDrmRelatedIdValue)createEntity(IDrmRelatedIdValue.class.getSimpleName(), DrmRelatedIdValue.class);
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue createLabelValue(String label, Object value) {
/*  87 */     return createLabelValue(null, label, value);
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue createLabelValue(Object dboId, String label, Object value) {
/*  91 */     IDrmLabelValue labelValue = (IDrmLabelValue)createEntity(IDrmLabelValue.class.getSimpleName(), DrmRelatedIdValue.class);
/*  92 */     labelValue.setDboId(dboId);
/*  93 */     labelValue.setLabel(label);
/*  94 */     labelValue.setValue(value);
/*  95 */     return labelValue;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet createResultSet() {
/*  99 */     return (IDrmQueryResultSet)createEntity(IDrmQueryResultSet.class.getSimpleName(), DboCollection.class);
/*     */   }
/*     */ 
/*     */   public IDrmQueryRow createQueryRow() {
/* 103 */     return (IDrmQueryRow)createEntity(IDrmQueryRow.class.getSimpleName(), DrmQueryRow.class);
/*     */   }
/*     */ 
/*     */   private <T> T createEntity(String entityName, Class defaultClass) {
/* 107 */     Object entity = null;
/* 108 */     if (EntityBeanFactory.getInstance().isEntityRegistered(entityName))
/*     */       try {
/* 110 */         entity = EntityBeanFactory.getInstance().createEntity(entityName);
/*     */       } catch (Exception ex) {
/* 112 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     try
/*     */     {
/* 116 */       entity = entity == null ? defaultClass.newInstance() : entity;
/*     */     }
/*     */     catch (Exception ex) {
/*     */     }
/* 120 */     return entity;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.DrmEntityFactory
 * JD-Core Version:    0.6.0
 */