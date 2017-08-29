/*     */ package com.boco.raptor.drm.core.plugin.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.RelatedClassTreeNode;
/*     */ import com.boco.raptor.drm.core.plugin.IObjectValidator;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DrmDynObjValidator
/*     */   implements IObjectValidator
/*     */ {
/*     */   public void checkAddObject(IServiceActionContext actionContext, IDrmDataObject dbo, BMClassMeta classMeta)
/*     */     throws UserException
/*     */   {
/*  45 */     Map uniqueNameIds = classMeta.getUniqueAttrIds();
/*  46 */     if ((uniqueNameIds == null) || (uniqueNameIds.size() == 0)) return;
/*     */ 
/*  48 */     int validUniqueCount = 0;
/*  49 */     for (String uniqueName : uniqueNameIds.keySet()) {
/*  50 */       String[] uniqueAttrIds = (String[])uniqueNameIds.get(uniqueName);
/*  51 */       Object[] uniqueAttrValues = new Object[uniqueAttrIds.length];
/*  52 */       boolean isHasUniqueValue = true;
/*  53 */       for (int i = 0; i < uniqueAttrValues.length; i++) {
/*  54 */         uniqueAttrValues[i] = dbo.getAttrValue(uniqueAttrIds[i]);
/*  55 */         if (uniqueAttrValues[i] == null) {
/*  56 */           isHasUniqueValue = false;
/*  57 */           LogHome.getLog().info(uniqueAttrIds[i] + "=" + uniqueAttrValues[i]);
/*  58 */           break;
/*     */         }
/*     */       }
/*     */ 
/*  62 */       if (isHasUniqueValue) {
/*  63 */         boolean isValidUnique = isUniqueAttrIdExist(actionContext, classMeta, uniqueAttrIds, uniqueAttrValues, "");
/*  64 */         if (!isValidUnique) {
/*  65 */           String expMsg = getUniqueIdExpMsg(classMeta, uniqueName, uniqueAttrIds);
/*  66 */           throw new UserException("唯一性约束不满足：" + expMsg);
/*     */         }
/*  68 */         validUniqueCount++;
/*     */       }
/*     */     }
/*     */ 
/*  72 */     if (validUniqueCount == 0)
/*  73 */       throw new UserException("唯一性的属性都没有赋值");
/*     */   }
/*     */ 
/*     */   public void checkModifyObject(IServiceActionContext actionContext, IDrmDataObject dbo, BMClassMeta classMeta) throws UserException
/*     */   {
/*  78 */     Map uniqueNameIds = classMeta.getUniqueAttrIds();
/*  79 */     Map uniqueNameValues = new HashMap();
/*  80 */     if ((uniqueNameIds == null) || (uniqueNameIds.size() == 0)) return;
/*     */ 
/*  82 */     for (String uniqueName : uniqueNameIds.keySet()) {
/*  83 */       String[] uniqueAttrIds = (String[])uniqueNameIds.get(uniqueName);
/*  84 */       Object[] uniqueAttrValues = new Object[uniqueAttrIds.length];
/*  85 */       boolean isValidUnique = false;
/*  86 */       for (int i = 0; i < uniqueAttrValues.length; i++) {
/*  87 */         uniqueAttrValues[i] = dbo.getAttrValue(uniqueAttrIds[i]);
/*  88 */         if (uniqueAttrValues[i] != null) {
/*  89 */           isValidUnique = true;
/*     */         }
/*     */       }
/*  92 */       if (isValidUnique) {
/*  93 */         uniqueNameValues.put(uniqueName, uniqueAttrValues);
/*     */       }
/*     */     }
/*     */ 
/*  97 */     if (uniqueNameValues.size() == 0) return;
/*     */ 
/*  99 */     int uniqueIdCount = 0;
/* 100 */     IDrmDataObject existDbo = null;
/* 101 */     for (String uniqueName : uniqueNameValues.keySet()) {
/* 102 */       String[] uniqueAttrIds = (String[])uniqueNameIds.get(uniqueName);
/* 103 */       Object[] uniqueAttrValues = (Object[])uniqueNameValues.get(uniqueName);
/* 104 */       boolean isValidUnique = true;
/* 105 */       for (int i = 0; i < uniqueAttrValues.length; i++) {
/* 106 */         Object uniqueAttrValue = uniqueAttrValues[i];
/* 107 */         if (uniqueAttrValue == null) {
/* 108 */           existDbo = existDbo == null ? getModifyExistDbo(actionContext, dbo) : existDbo;
/* 109 */           if ((existDbo == null) || (existDbo.getAttrValue(uniqueAttrIds[i]) == null)) {
/* 110 */             isValidUnique = false;
/* 111 */             break;
/*     */           }
/* 113 */           uniqueAttrValues[i] = existDbo.getAttrValue(uniqueAttrIds[i]);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 118 */       if (isValidUnique) {
/* 119 */         if (!isUniqueAttrIdExist(actionContext, classMeta, uniqueAttrIds, uniqueAttrValues, dbo.getCuid())) {
/* 120 */           String expMsg = getUniqueIdExpMsg(classMeta, uniqueName, uniqueAttrIds);
/* 121 */           throw new UserException(expMsg);
/*     */         }
/* 123 */         uniqueIdCount++;
/*     */       }
/*     */     }
/*     */ 
/* 127 */     if (uniqueIdCount == 0)
/* 128 */       throw new UserException("修改数据的有效性都不完整，请修改!");
/*     */   }
/*     */ 
/*     */   public void checkDeleteObject(IServiceActionContext actionContext, IDrmDataObject dbo, BMClassMeta classMeta) throws UserException
/*     */   {
/* 133 */     LogHome.getLog().info("删除对象前进行约束检查");
/* 134 */     Map relatedObjCount = getDynRMService().getRelatedDeleteObjectCount(actionContext, dbo, false);
/* 135 */     if (relatedObjCount != null) {
/* 136 */       Iterator i = relatedObjCount.keySet().iterator();
/* 137 */       while (i.hasNext()) {
/* 138 */         String bmClassKey = (String)i.next();
/* 139 */         String bmClassId = RelatedClassTreeNode.getBmClassId(bmClassKey);
/* 140 */         Long count = Long.valueOf(((Integer)relatedObjCount.get(bmClassKey)).intValue());
/* 141 */         if (count.longValue() > 0L) {
/* 142 */           String expMsg = "";
/* 143 */           BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 144 */           String labelCn = relatedClassMeta != null ? relatedClassMeta.getLabelCn() : bmClassId;
/* 145 */           expMsg = expMsg.length() > 0 ? expMsg + ", " : expMsg;
/* 146 */           expMsg = expMsg + labelCn + " " + count + "个";
/* 147 */           expMsg = "删除失败，有约束对象：[" + expMsg + "]";
/* 148 */           throw new UserException(expMsg);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private IDrmDataObject getModifyExistDbo(IServiceActionContext actionContext, IDrmDataObject dbo)
/*     */   {
/* 156 */     IDrmDataObject fullDbo = null;
/*     */     try {
/* 158 */       IDrmDataObject cloneDbo = DrmEntityFactory.getInstance().createDataObject();
/* 159 */       cloneDbo.setBmClassId(dbo.getBmClassId());
/* 160 */       cloneDbo.setDbClassId(dbo.getDbClassId());
/* 161 */       cloneDbo.setCuid(dbo.getCuid());
/* 162 */       cloneDbo.setDboId(cloneDbo.getDboId());
/* 163 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 164 */       fullDbo = getDynRMService().getDynObject(actionContext, queryContext, cloneDbo);
/*     */     } catch (Exception ex) {
/* 166 */       LogHome.getLog().error("", ex);
/*     */     }
/* 168 */     return fullDbo;
/*     */   }
/*     */ 
/*     */   private boolean isUniqueAttrIdExist(IServiceActionContext actionContext, BMClassMeta classMeta, String[] uniqueAttrIds, Object[] uniqueAttrValues, String cuid) {
/* 172 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 174 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 175 */       query.setBmClassId(classMeta.getBmClassId());
/* 176 */       query.setClassMeta(classMeta);
/* 177 */       query.addQueryAttrId("CUID");
/* 178 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 179 */         query.addQueryCondExps(new DrmQueryAttrCond(uniqueAttrIds[i], "=", uniqueAttrValues[i].toString()));
/*     */       }
/* 181 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 182 */       rs = getDynRMService().getDynObjBySql(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/* 184 */       LogHome.getLog().error("", ex);
/*     */     }
/* 186 */     if ((rs == null) || (rs.getResultSet().size() == 0)) return true;
/* 187 */     if ((rs.getResultSet().size() == 1) && (!"".equals(cuid))) {
/* 188 */       IDrmDataObject ddo = ((IDrmQueryRow)rs.getResultSet().get(0)).getResultDbo(classMeta.getDbClassId());
/* 189 */       if (ddo.getCuid().equals(cuid)) return true;
/*     */     }
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   private String getUniqueIdExpMsg(BMClassMeta classMeta, String uniqueName, String[] uniqueAttrIds) {
/* 195 */     String expMsg = uniqueName + "违反唯一性[";
/* 196 */     for (String uniqueAttrId : uniqueAttrIds) {
/* 197 */       expMsg = expMsg + classMeta.getAttrMeta(uniqueAttrId).getLabelCn() + ",";
/*     */     }
/* 199 */     expMsg = expMsg + "]";
/* 200 */     return expMsg;
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService()
/*     */   {
/* 205 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private static IDynResManageService getDynRMService() {
/* 209 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.plugin.impl.DrmDynObjValidator
 * JD-Core Version:    0.6.0
 */