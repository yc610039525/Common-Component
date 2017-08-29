/*     */ package com.boco.raptor.drm.core.service.dynres.impl;
/*     */ 
/*     */ import com.boco.common.util.db.TransactionFactory;
/*     */ import com.boco.common.util.db.UserTransaction;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.AbstractService;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.common.service.impl.ServiceActionContext;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmLabelValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.IDrmRelatedIdValue;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckCount;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmCheckAttrId;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*     */ import com.boco.raptor.drm.core.plugin.IActionAdaptor;
/*     */ import com.boco.raptor.drm.core.plugin.IMultiAttrParser;
/*     */ import com.boco.raptor.drm.core.plugin.IObjectValidator;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthenticationService;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.raptor.ext.tnms.adaptor.ServiceBoAdaptor;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DynResManageService<T extends IDrmDataObject> extends AbstractService
/*     */   implements IDynResManageService<T>
/*     */ {
/*     */   private IDynResManageServiceDAO serviceDAO;
/*     */   private MemoryQueryService memoryQueryService;
/*     */   private RelatedObjectService relatedObjectService;
/*     */   private DynCheckService checkService;
/*  68 */   private Map<String, CuidCache> requestCuidCache = new Hashtable();
/*  69 */   private boolean isLog = false;
/*     */ 
/*     */   public DynResManageService() {
/*  72 */     super("DynResManageService");
/*     */   }
/*     */ 
/*     */   public void setIsLog(boolean isLog) {
/*  76 */     this.isLog = isLog;
/*     */   }
/*     */ 
/*     */   public boolean getIsLog(IServiceActionContext actionContext) {
/*  80 */     return this.isLog;
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> addDynObjects(IServiceActionContext actionContext, List<T> dros, boolean isValidate) throws UserException {
/*  84 */     List results = new ArrayList();
/*  85 */     for (IDrmDataObject dro : dros) {
/*     */       try {
/*  87 */         IDrmDataObject result = addDynObject(actionContext, dro, isValidate);
/*  88 */         results.add(result);
/*     */       } catch (Exception ex) {
/*  90 */         LogHome.getLog().error("", ex);
/*  91 */         throw new UserException(ex.getMessage());
/*     */       }
/*     */     }
/*  94 */     return results;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject addDynObject(IServiceActionContext actionContext, IDrmDataObject dro, boolean isValidate) throws UserException {
/*     */     try {
/*  99 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 100 */       if (classMeta == null) {
/* 101 */         throw new UserException("业务模型没有配置，bmClassId=" + dro.getBmClassId());
/*     */       }
/* 103 */       if (classMeta.getActionAdaptor() != null) {
/* 104 */         classMeta.getActionAdaptor().addDynObject(actionContext, dro, isValidate, classMeta);
/*     */       } else {
/* 106 */         if (isValidate) {
/* 107 */           classMeta.getObjectValidator().checkAddObject(actionContext, dro, classMeta);
/*     */         }
/* 109 */         dro.setDbClassId(classMeta.getDbClassId());
/* 110 */         getAuthenticationService().isObjectValid(actionContext, dro);
/* 111 */         dro = this.serviceDAO.addDynObject(actionContext, dro);
/*     */       }
/*     */     } catch (Exception ex) {
/* 114 */       LogHome.getLog().error("", ex);
/* 115 */       throw new UserException(ex.getMessage());
/*     */     }
/* 117 */     return dro;
/*     */   }
/*     */ 
/*     */   public void modifyDynObject(IServiceActionContext actionContext, IDrmDataObject dro, boolean isValidate) throws UserException {
/*     */     try {
/* 122 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 123 */       if (classMeta == null) {
/* 124 */         throw new UserException("业务模型没有配置，bmClassId=" + dro.getBmClassId());
/*     */       }
/* 126 */       if (classMeta.getActionAdaptor() != null) {
/* 127 */         classMeta.getActionAdaptor().modifyDynObject(actionContext, dro, isValidate, classMeta);
/*     */       }
/*     */       else {
/* 130 */         IDrmDataObject fulldro = fillDrmDataObject(dro);
/* 131 */         getAuthenticationService().isObjectValid(actionContext, fulldro);
/* 132 */         if (isValidate) {
/* 133 */           classMeta.getObjectValidator().checkModifyObject(actionContext, fulldro, classMeta);
/*     */         }
/* 135 */         this.serviceDAO.modifyDynObject(actionContext, dro);
/*     */       }
/*     */     } catch (Exception ex) {
/* 138 */       LogHome.getLog().error("", ex);
/* 139 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyDynObject(IServiceActionContext actionContext, IDrmDataObject dro, List<T> maintenanceLogs, boolean isValidate) throws UserException {
/*     */     try {
/* 145 */       modifyDynObject(actionContext, dro, isValidate);
/* 146 */       if (maintenanceLogs.size() > 0)
/* 147 */         addDynObjects(actionContext, maintenanceLogs, false);
/*     */     }
/*     */     catch (Exception ex) {
/* 150 */       LogHome.getLog().error("", ex);
/* 151 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyDynObjects(IServiceActionContext actionContext, List<T> dros, Map<String, Object> modifyAttrs, boolean isValidate) throws UserException
/*     */   {
/*     */     try {
/* 158 */       if ((dros == null) || (dros.size() == 0)) return;
/*     */ 
/* 160 */       IDrmDataObject dro = (IDrmDataObject)dros.get(0);
/* 161 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 162 */       if (classMeta == null) {
/* 163 */         throw new UserException("业务模型没有配置，bmClassId=" + dro.getBmClassId());
/*     */       }
/*     */ 
/* 166 */       if (classMeta.getActionAdaptor() != null) {
/* 167 */         classMeta.getActionAdaptor().modifyDynObjects(actionContext, dros, modifyAttrs, isValidate, classMeta);
/*     */       } else {
/* 169 */         for (IDrmDataObject dbo : dros) {
/* 170 */           for (String attrId : modifyAttrs.keySet()) {
/* 171 */             dbo.setAttrValue(attrId, modifyAttrs.get(attrId));
/*     */           }
/* 173 */           if (isValidate) {
/* 174 */             classMeta.getObjectValidator().checkModifyObject(actionContext, dbo, classMeta);
/*     */           }
/* 176 */           getAuthenticationService().isObjectValid(actionContext, dbo);
/*     */         }
/* 178 */         this.serviceDAO.modifyDynObjects(actionContext, dros, modifyAttrs);
/*     */       }
/*     */     } catch (Exception ex) {
/* 181 */       LogHome.getLog().error("", ex);
/* 182 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyDynObjects(IServiceActionContext actionContext, List<T> dros, Map<String, Object> modifyAttrs, List<T> maintenanceLogs, boolean isValidate) throws UserException {
/*     */     try {
/* 188 */       modifyDynObjects(actionContext, dros, modifyAttrs, isValidate);
/* 189 */       if (maintenanceLogs.size() > 0)
/* 190 */         addDynObjects(actionContext, maintenanceLogs, false);
/*     */     }
/*     */     catch (Exception ex) {
/* 193 */       LogHome.getLog().error("", ex);
/* 194 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteDynObject(IServiceActionContext actionContext, IDrmDataObject dro, boolean isValidate) throws UserException
/*     */   {
/* 200 */     UserTransaction trx = TransactionFactory.getInstance().createTransaction();
/*     */     try {
/* 202 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 203 */       if (classMeta == null) {
/* 204 */         throw new UserException("业务模型没有配置，bmClassId=" + dro.getBmClassId());
/*     */       }
/* 206 */       trx.begin();
/* 207 */       if (classMeta.getActionAdaptor() != null) {
/* 208 */         classMeta.getActionAdaptor().deleteDynObject(actionContext, dro, isValidate, classMeta);
/*     */       }
/*     */       else {
/* 211 */         IDrmDataObject fulldro = fillDrmDataObject(dro);
/* 212 */         getAuthenticationService().isObjectValid(actionContext, fulldro);
/* 213 */         if (isValidate) {
/* 214 */           classMeta.getObjectValidator().checkDeleteObject(actionContext, fulldro, classMeta);
/*     */         }
/* 216 */         List cascadeObjs = this.relatedObjectService.getCascadeDeleteObjects(actionContext, dro);
/* 217 */         deleteDynObjects(actionContext, cascadeObjs, true);
/* 218 */         this.serviceDAO.deleteDynObject(actionContext, dro);
/*     */       }
/* 220 */       trx.commit();
/*     */     } catch (Exception ex) {
/* 222 */       trx.rollback();
/* 223 */       LogHome.getLog().error("", ex);
/* 224 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<String, String> deleteDynObjects(IServiceActionContext actionContext, List<T> dros, boolean isValidate) throws UserException {
/* 229 */     Map returnList = new LinkedHashMap();
/*     */     try {
/* 231 */       if ((dros == null) || (dros.size() == 0)) return returnList;
/*     */ 
/* 233 */       IDrmDataObject dro = (IDrmDataObject)dros.get(0);
/* 234 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 235 */       if (classMeta == null) {
/* 236 */         throw new UserException("业务模型没有配置，bmClassId=" + dro.getBmClassId());
/*     */       }
/* 238 */       if (classMeta.getActionAdaptor() != null)
/* 239 */         returnList = classMeta.getActionAdaptor().deleteDynObjects(actionContext, dros, isValidate, classMeta);
/*     */       else
/* 241 */         for (IDrmDataObject dbo : dros)
/*     */           try {
/* 243 */             deleteDynObject(actionContext, dbo, isValidate);
/*     */           } catch (Exception ex) {
/* 245 */             returnList.put(dbo.getCuid(), ex.getMessage());
/*     */           }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 250 */       LogHome.getLog().error("", ex);
/* 251 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */ 
/* 254 */     return returnList;
/*     */   }
/*     */ 
/*     */   public Map<String, String> deleteDynObjectsByCond(IServiceActionContext actionContext, String bmClassId, DrmSingleClassQuery query, boolean isValidate) throws UserException {
/* 258 */     Map returnList = new LinkedHashMap();
/*     */     try {
/* 260 */       classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 261 */       if (classMeta == null) {
/* 262 */         throw new UserException("业务模型没有配置，bmClassId=" + bmClassId);
/*     */       }
/*     */ 
/* 265 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 266 */       query.addQueryAttrId(classMeta.getCuidAttrId());
/* 267 */       query.addQueryAttrId(classMeta.getPkAttrId());
/*     */ 
/* 269 */       IDrmQueryResultSet rs = getDynObjBySql(actionContext, queryContext, query);
/* 270 */       List rows = rs.getResultSet();
/* 271 */       List idos = new ArrayList();
/*     */ 
/* 273 */       if (classMeta.getActionAdaptor() != null) {
/* 274 */         for (IDrmQueryRow row : rows) {
/* 275 */           IDrmDataObject dbobj = row.getResultDbo(classMeta.getDbClassId());
/* 276 */           idos.add(dbobj);
/*     */         }
/* 278 */         returnList = classMeta.getActionAdaptor().deleteDynObjects(actionContext, idos, isValidate, classMeta);
/*     */       } else {
/* 280 */         for (IDrmQueryRow row : rows) {
/* 281 */           IDrmDataObject dbobj = row.getResultDbo(classMeta.getDbClassId());
/*     */           try {
/* 283 */             deleteDynObject(actionContext, dbobj, isValidate);
/*     */           } catch (Exception ex) {
/* 285 */             returnList.put(dbobj.getCuid(), ex.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       BMClassMeta classMeta;
/* 290 */       LogHome.getLog().error("", ex);
/* 291 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */ 
/* 294 */     return returnList;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getDynObject(IServiceActionContext actionContext, IDrmQueryContext queryContext, IDrmDataObject simpleDbo) throws UserException
/*     */   {
/* 299 */     IDrmDataObject dbo = null;
/*     */     try {
/* 301 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, simpleDbo.getBmClassId());
/* 302 */       simpleDbo.setDbClassId(classMeta.getDbClassId());
/* 303 */       dbo = this.serviceDAO.getDynObject(actionContext, classMeta, simpleDbo);
/* 304 */       if ((dbo != null) && (queryContext.isPopulate())) {
/* 305 */         populateDboBool(classMeta, dbo, true);
/* 306 */         populateDboEnum(classMeta, dbo, null, true);
/* 307 */         populateDboRelation(actionContext, classMeta, dbo, true);
/*     */       }
/*     */     } catch (Exception ex) {
/* 310 */       LogHome.getLog().error("", ex);
/* 311 */       throw new UserException(ex.getMessage());
/*     */     }
/* 313 */     return dbo;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getObjectsBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, String sql, GenericDO[] dboTemplates) throws UserException
/*     */   {
/* 318 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 320 */       rs = this.serviceDAO.selectDBOs(ServiceBoAdaptor.drmQryCxt2boQryCxt(queryContext), sql, dboTemplates);
/*     */     } catch (Exception ex) {
/* 322 */       LogHome.getLog().error("", ex);
/* 323 */       throw new UserException(ex);
/*     */     }
/* 325 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getDynObjBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws UserException
/*     */   {
/* 330 */     return getDynObjBySql(actionContext, queryContext, query, null);
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getDynObjBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query, IDrmDataObject dboTemplate) throws UserException
/*     */   {
/* 335 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 337 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, query.getBmClassId());
/* 338 */       if (classMeta == null) {
/* 339 */         throw new UserException("bmClassId=" + query.getBmClassId() + "，无效！");
/*     */       }
/* 341 */       query.setClassMeta(classMeta);
/*     */ 
/* 344 */       if (dboTemplate == null)
/* 345 */         actionContext.setBmClassId(query.getBmClassId());
/*     */       else {
/* 347 */         actionContext.setBmClassId(dboTemplate.getBmClassId());
/*     */       }
/* 349 */       String whereSql = getAuthenticationService().getBmClassMetaAuthenticationSql(actionContext);
/* 350 */       if (whereSql != null) {
/* 351 */         query.setSqlFilter(whereSql);
/*     */       }
/* 353 */       if (dboTemplate == null)
/* 354 */         rs = this.serviceDAO.getDynObjBySql(actionContext, queryContext, query);
/*     */       else {
/* 356 */         rs = this.serviceDAO.getDynObjBySql(actionContext, queryContext, query, dboTemplate);
/*     */       }
/* 358 */       if ((rs != null) && (queryContext.isPopulate())) {
/* 359 */         IServiceActionContext context = new ServiceActionContext();
/* 360 */         populateResultSetBool(rs, classMeta, queryContext.isEntity());
/* 361 */         populateResultSetEnum(context, rs, classMeta, queryContext.isEntity());
/* 362 */         populateResultSetRelation(context, rs, classMeta, queryContext.isEntity());
/*     */       }
/* 364 */       if (rs != null) {
/* 365 */         rs.setPopulate(queryContext.isPopulate());
/* 366 */         rs.setEntity(queryContext.isEntity());
/*     */       } else {
/* 368 */         LogHome.getLog().error("IDrmQueryResultSet rs is null");
/*     */       }
/*     */     } catch (Exception ex) {
/* 371 */       LogHome.getLog().error("", ex);
/* 372 */       throw new UserException(ex.getMessage());
/*     */     }
/* 374 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getRelatedAttrValues(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query)
/*     */     throws UserException
/*     */   {
/* 380 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 382 */       List queryAttrIds = query.getQueryAttrIds();
/* 383 */       if (queryAttrIds.size() == 0) {
/* 384 */         throw new UserException("查询条件没有包含关联AttrId");
/*     */       }
/* 386 */       relatedAttrId = (String)queryAttrIds.get(0);
/*     */ 
/* 388 */       classMeta = getBMModelService().getClassMeta(actionContext, query.getBmClassId());
/* 389 */       String labelAttrId = classMeta.getLabelAttrId();
/* 390 */       if (labelAttrId != null) {
/* 391 */         queryContext.setOrderField(classMeta.getLabelAttrId());
/* 392 */         if (!query.getQueryAttrIds().contains(labelAttrId))
/* 393 */           query.addQueryAttrId(classMeta.getLabelAttrId());
/*     */       }
/*     */       else {
/* 396 */         List labelAttrIds = classMeta.getConstructLabelAttrIds();
/* 397 */         if ((labelAttrIds == null) || (labelAttrIds.size() == 0)) {
/* 398 */           throw new UserException("bmClassId=" + classMeta.getBmClassId() + "， 没有定义名称属性！");
/*     */         }
/*     */ 
/* 401 */         for (int i = 0; i < labelAttrIds.size(); i++) {
/* 402 */           String _labelAttrId = (String)labelAttrIds.get(i);
/* 403 */           query.addQueryAttrId(_labelAttrId);
/* 404 */           if (i == 0) {
/* 405 */             queryContext.setOrderField(_labelAttrId);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 410 */       queryContext.setPopulate(true);
/* 411 */       query.addQueryAttrId("OBJECTID");
/* 412 */       rs = getDynObjBySql(actionContext, queryContext, query);
/* 413 */       List rows = rs.getResultSet();
/* 414 */       for (IDrmQueryRow row : rows) {
/* 415 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 416 */         IDrmRelatedIdValue lv = DrmEntityFactory.getInstance().createRelatedIdValue(dbo.getDboId(), dbo.getBmClassId(), dbo.getDbClassId(), "CUID", dbo.getAttrValue(relatedAttrId), classMeta.getDisplayLabel(dbo));
/* 417 */         dbo.getAllAttr().clear();
/* 418 */         dbo.setDboId(null);
/* 419 */         dbo.setAttrValue(relatedAttrId, lv);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       String relatedAttrId;
/*     */       BMClassMeta classMeta;
/* 422 */       LogHome.getLog().error("", ex);
/* 423 */       throw new UserException(ex.getMessage());
/*     */     }
/* 425 */     return rs;
/*     */   }
/*     */ 
/*     */   private void populateResultSetBool(IDrmQueryResultSet rs, BMClassMeta classMeta, boolean isEntity) {
/* 429 */     if (isEntity) return;
/*     */ 
/* 431 */     List rows = rs.getResultSet();
/*     */ 
/* 433 */     for (IDrmQueryRow row : rows) {
/* 434 */       IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 435 */       populateDboBool(classMeta, dbo, isEntity);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateDboBool(BMClassMeta classMeta, IDrmDataObject dbo, boolean isEntity) {
/* 440 */     if ((classMeta == null) || (!classMeta.isDynClass())) {
/* 441 */       LogHome.getLog().error("bmClassId=" + dbo.getBmClassId() + ", 没有配置业务对象模型");
/* 442 */       return;
/*     */     }
/*     */ 
/* 445 */     String[] attrIds = dbo.getAllAttrId();
/* 446 */     for (String attrId : attrIds) {
/* 447 */       Object attrValue = dbo.getAttrValue(attrId);
/*     */ 
/* 449 */       if (attrValue.getClass() == Boolean.class)
/* 450 */         if (((Boolean)attrValue).booleanValue())
/* 451 */           dbo.setAttrValue(attrId, "是");
/*     */         else
/* 453 */           dbo.setAttrValue(attrId, "否");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateResultSetEnum(IServiceActionContext actionContext, IDrmQueryResultSet rs, BMClassMeta classMeta, boolean isEntity)
/*     */   {
/* 461 */     List rows = rs.getResultSet();
/* 462 */     Map enumMetaCacheMap = new HashMap();
/* 463 */     for (IDrmQueryRow row : rows) {
/* 464 */       IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 465 */       populateDboEnum(classMeta, dbo, enumMetaCacheMap, isEntity);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateDboEnum(BMClassMeta classMeta, IDrmDataObject dbo, Map<String, BMEnumMeta> enumMetaCacheMap, boolean isEntity)
/*     */   {
/* 471 */     IServiceActionContext actionContext = new ServiceActionContext();
/* 472 */     if ((classMeta == null) || (!classMeta.isDynClass())) {
/* 473 */       LogHome.getLog().error("bmClassId=" + dbo.getBmClassId() + ", 没有配置业务对象模型");
/* 474 */       return;
/*     */     }
/* 476 */     String[] attrIds = dbo.getAllAttrId();
/* 477 */     for (String attrId : attrIds) {
/* 478 */       Object enumValue = dbo.getAttrValue(attrId);
/* 479 */       if (enumValue == null)
/*     */         continue;
/* 481 */       BMAttrMeta attrMeta = classMeta.getAttrMeta(attrId);
/* 482 */       if (attrMeta == null) {
/* 483 */         LogHome.getLog().error("bmClassId=" + dbo.getBmClassId() + ", attrId=" + attrId + ", 没有配置业务对象属性模型");
/*     */       }
/*     */       else
/*     */       {
/* 488 */         String enumId = attrMeta.getEnumId();
/* 489 */         BMEnumMeta enumMeta = null;
/* 490 */         if (enumId != null) {
/* 491 */           if ((enumMetaCacheMap != null) && (enumMetaCacheMap.containsKey(attrId))) {
/* 492 */             enumMeta = (BMEnumMeta)enumMetaCacheMap.get(attrId);
/*     */           } else {
/* 494 */             enumMeta = getBMModelService().getEnumMeta(actionContext, enumId);
/* 495 */             if (enumMetaCacheMap != null) {
/* 496 */               enumMetaCacheMap.put(attrId, enumMeta);
/*     */             }
/*     */           }
/* 499 */           if (enumMeta == null) {
/* 500 */             LogHome.getLog().warn("enumId=" + enumId + "，没有配置业务模型 ！");
/*     */           }
/*     */           else {
/* 503 */             Object _enum = getEnumEntity(attrMeta, enumMeta, enumValue, isEntity);
/* 504 */             dbo.setAttrValue(attrId, _enum);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object getEnumEntity(BMAttrMeta attrMeta, BMEnumMeta enumMeta, Object attrValue, boolean isEntity) {
/* 510 */     Object result = null;
/* 511 */     if ((attrMeta.getIsMultiValue().booleanValue()) && (!isEntity)) {
/* 512 */       IMultiAttrParser parser = attrMeta.getMultiParser();
/* 513 */       if (parser != null) {
/* 514 */         String enumNames = "";
/* 515 */         String[] attrValues = parser.parseValues(attrMeta, (String)attrValue);
/* 516 */         for (int i = 0; (attrValues != null) && (i < attrValues.length); i++) {
/* 517 */           long enumValue = Long.parseLong(attrValues[i]);
/* 518 */           String enumName = enumMeta.getEnumName(Long.valueOf(enumValue));
/* 519 */           if (enumNames.length() > 0) {
/* 520 */             enumNames = enumNames + ",";
/*     */           }
/* 522 */           if ((enumName != null) && (enumName.length() > 0))
/* 523 */             enumNames = enumNames + enumName;
/*     */           else {
/* 525 */             enumNames = enumNames + enumValue;
/*     */           }
/*     */         }
/* 528 */         result = enumNames;
/*     */       }
/* 530 */     } else if ((attrMeta.getIsMultiValue().booleanValue()) && (isEntity)) {
/* 531 */       IMultiAttrParser parser = attrMeta.getMultiParser();
/* 532 */       if (parser != null) {
/* 533 */         String[] attrValues = parser.parseValues(attrMeta, (String)attrValue);
/* 534 */         List enumEntitys = new ArrayList();
/* 535 */         for (int i = 0; (attrValues != null) && (i < attrValues.length); i++) {
/* 536 */           long enumValue = Long.parseLong(attrValues[i]);
/* 537 */           String enumName = enumMeta.getEnumName(Long.valueOf(enumValue));
/* 538 */           IDrmEnumValue enumEntity = DrmEntityFactory.getInstance().createEnumValue(enumName, Long.valueOf(enumValue));
/* 539 */           enumEntitys.add(enumEntity);
/*     */         }
/* 541 */         result = enumEntitys;
/*     */       }
/* 543 */     } else if ((!attrMeta.getIsMultiValue().booleanValue()) && (!isEntity)) {
/* 544 */       String enumName = enumMeta.getEnumName(attrValue);
/* 545 */       if (enumName == null) {
/* 546 */         enumName = "" + attrValue;
/*     */       }
/* 548 */       result = enumName;
/* 549 */     } else if ((!attrMeta.getIsMultiValue().booleanValue()) && (isEntity)) {
/* 550 */       result = DrmEntityFactory.getInstance().createEnumValue(enumMeta.getEnumName(attrValue), attrValue);
/*     */     }
/* 552 */     return result;
/*     */   }
/*     */ 
/*     */   private void populateResultSetRelation(IServiceActionContext actionContext, IDrmQueryResultSet rs, BMClassMeta classMeta, boolean isEntity)
/*     */   {
/* 557 */     List rows = rs.getResultSet();
/* 558 */     this.requestCuidCache.put(actionContext.getRequestId(), new CuidCache(null));
/*     */     try {
/* 560 */       for (IDrmQueryRow row : rows) {
/* 561 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 562 */         populateDboRelation(actionContext, classMeta, dbo, isEntity);
/*     */       }
/*     */     } finally {
/* 565 */       this.requestCuidCache.remove(actionContext.getRequestId());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateDboRelation(IServiceActionContext actionContext, BMClassMeta classMeta, IDrmDataObject dbo, boolean isEntity)
/*     */   {
/* 571 */     actionContext.setUserId(null);
/* 572 */     if ((classMeta == null) || (!classMeta.isDynClass())) {
/* 573 */       LogHome.getLog().error("bmClassId=" + dbo.getBmClassId() + ", 没有配置业务对象模型");
/* 574 */       return;
/*     */     }
/*     */ 
/* 578 */     String[] attrIds = dbo.getAllAttrId();
/* 579 */     for (String attrId : attrIds) {
/* 580 */       Object attrValue = dbo.getAttrValue(attrId);
/* 581 */       if (attrValue == null) {
/*     */         continue;
/*     */       }
/* 584 */       BMAttrMeta attrMeta = classMeta.getAttrMeta(attrId);
/* 585 */       if (attrMeta == null) {
/* 586 */         LogHome.getLog().error("bmClassId=" + dbo.getBmClassId() + ", attrId=" + attrId + ", 没有配置业务对象属性模型");
/*     */       }
/*     */       else
/*     */       {
/* 591 */         if (attrMeta.getIsSystem().booleanValue())
/*     */           continue;
/* 593 */         if ((attrMeta.getIsRelation() == null) || (!attrMeta.getIsRelation().booleanValue())) continue;
/*     */         try {
/* 595 */           IDrmLabelValue relatedIdLabel = null;
/* 596 */           String relatedCuid = (String)attrValue;
/* 597 */           if (relatedCuid.trim().length() == 0) {
/*     */             continue;
/*     */           }
/* 600 */           Map cuidCache = (Map)this.requestCuidCache.get(actionContext.getRequestId());
/* 601 */           if ((cuidCache != null) && (cuidCache.containsKey(relatedCuid))) {
/* 602 */             relatedIdLabel = (IDrmLabelValue)cuidCache.get(attrValue);
/*     */           } else {
/* 604 */             relatedIdLabel = getRelatedIdLabelByAttrValue(actionContext, classMeta.getBmClassId(), attrMeta.getAttrId(), relatedCuid);
/* 605 */             if (cuidCache != null) {
/* 606 */               cuidCache.put(relatedCuid, relatedIdLabel);
/*     */             }
/*     */           }
/*     */ 
/* 610 */           if (isEntity) {
/* 611 */             dbo.setAttrValue(attrId, relatedIdLabel);
/*     */           }
/* 613 */           else if (relatedIdLabel != null)
/* 614 */             dbo.setAttrValue(attrId, relatedIdLabel.getLabel());
/*     */           else
/* 616 */             dbo.setAttrValue(attrId, "");
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/* 620 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue getRelatedIdLabelByClassValue(IServiceActionContext actionContext, String bmClassId, String attrId, String cuid)
/*     */     throws UserException
/*     */   {
/* 630 */     IDrmDataObject dbo = getRelatedObjectByClassValue(actionContext, bmClassId, attrId, cuid);
/* 631 */     if (dbo == null) return null;
/*     */ 
/* 633 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(actionContext, dbo.getBmClassId());
/* 634 */     IDrmDataObject displayDbo = relatedClassMeta.getDisplayLabelDbo(dbo);
/* 635 */     populateDboRelation(actionContext, relatedClassMeta, displayDbo, false);
/* 636 */     String label = relatedClassMeta.getDisplayLabel(displayDbo);
/* 637 */     return DrmEntityFactory.getInstance().createRelatedIdValue(dbo.getDboId(), dbo.getBmClassId(), dbo.getDbClassId(), "CUID", dbo.getCuid(), label);
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getRelatedObjectByClassValue(IServiceActionContext actionContext, String bmClassId, String attrId, String cuid)
/*     */     throws UserException
/*     */   {
/* 643 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 644 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 645 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 646 */     query.addQueryAttrId(attrId);
/* 647 */     query.setBmClassId(bmClassId);
/* 648 */     query.setClassMeta(classMeta);
/* 649 */     query.setSqlCond("CUID='" + cuid + "'");
/* 650 */     IDrmQueryResultSet dbos = getDynObjBySql(actionContext, queryContext, query);
/* 651 */     if ((dbos == null) || (dbos.getResultSet().size() == 0)) return null;
/* 652 */     IDrmDataObject dbo = ((IDrmQueryRow)dbos.getResultSet().get(0)).getResultDbo(classMeta.getDbClassId());
/* 653 */     return getRelatedObjectByAttrValue(actionContext, bmClassId, attrId, (String)dbo.getAttrValue(attrId));
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue getRelatedIdLabelByAttrValue(IServiceActionContext actionContext, String bmClassId, String attrId, String relatedCuid) throws UserException
/*     */   {
/* 658 */     IDrmDataObject dbo = getRelatedObjectByAttrValue(actionContext, bmClassId, attrId, relatedCuid);
/* 659 */     if (dbo == null) return null;
/*     */ 
/* 661 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(actionContext, dbo.getBmClassId());
/* 662 */     IDrmDataObject displayDbo = relatedClassMeta.getDisplayLabelDbo(dbo);
/* 663 */     populateDboRelation(actionContext, relatedClassMeta, displayDbo, false);
/* 664 */     String label = relatedClassMeta.getDisplayLabel(displayDbo);
/* 665 */     return DrmEntityFactory.getInstance().createRelatedIdValue(dbo.getDboId(), dbo.getBmClassId(), dbo.getDbClassId(), "CUID", dbo.getCuid(), label);
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getRelatedObjectByAttrValue(IServiceActionContext actionContext, String bmClassId, String attrId, String relatedCuid)
/*     */     throws UserException
/*     */   {
/* 671 */     if (relatedCuid == null) return null;
/*     */ 
/* 673 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 674 */     if (classMeta == null) {
/* 675 */       throw new UserException("未知的业务类：" + bmClassId);
/*     */     }
/*     */ 
/* 678 */     BMAttrMeta attrMeta = classMeta.getAttrMeta(attrId);
/* 679 */     String relatedBmClassId = null;
/* 680 */     String relatedDbClassId = parseDbClassId(relatedCuid);
/* 681 */     boolean isRealBmClassId = false;
/* 682 */     BMClassMeta relatedClassMeta = null;
/* 683 */     Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 684 */     List relatedBmClassIds = new ArrayList();
/*     */     int dbClassCount;
/* 686 */     if ((relatedAttrIds != null) && (relatedAttrIds.size() == 1)) {
/* 687 */       relatedBmClassId = (String)relatedAttrIds.keySet().iterator().next();
/* 688 */       isRealBmClassId = true;
/* 689 */     } else if ((relatedAttrIds != null) && (relatedAttrIds.size() > 1)) {
/* 690 */       if (relatedDbClassId == null) {
/* 691 */         throw new UserException("未能解析业务类ID：relatedCuid=" + relatedCuid);
/*     */       }
/*     */ 
/* 694 */       dbClassCount = 0;
/*     */ 
/* 696 */       for (String _relatedBmClassId : relatedAttrIds.keySet()) {
/* 697 */         BMClassMeta _relatedClassMeta = getBMModelService().getClassMeta(actionContext, _relatedBmClassId);
/* 698 */         if (_relatedClassMeta != null) {
/* 699 */           if (relatedDbClassId.equals(_relatedClassMeta.getDbClassId())) {
/* 700 */             if (dbClassCount == 0) {
/* 701 */               relatedBmClassId = _relatedClassMeta.getBmClassId();
/* 702 */               isRealBmClassId = true;
/* 703 */               dbClassCount++;
/*     */             } else {
/* 705 */               relatedBmClassId = null;
/* 706 */               relatedBmClassIds.add(_relatedClassMeta.getBmClassId());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 712 */     if (relatedBmClassId == null) {
/* 713 */       if (relatedDbClassId == null) {
/* 714 */         throw new UserException("未能解析业务类ID：relatedCuid=" + relatedCuid);
/*     */       }
/* 716 */       relatedBmClassId = relatedDbClassId;
/*     */     }
/*     */ 
/* 719 */     relatedClassMeta = getBMModelService().getClassMeta(actionContext, relatedBmClassId);
/* 720 */     if (relatedClassMeta == null) {
/* 721 */       throw new UserException("未知的业务关联类：" + relatedBmClassId);
/*     */     }
/* 723 */     relatedDbClassId = relatedClassMeta.getDbClassId();
/*     */ 
/* 725 */     IDrmDataObject relatedDbo = null;
/* 726 */     if (this.serviceDAO.isClassCached(relatedClassMeta.getDbClassId())) {
/* 727 */       IDrmDataObject simpleDbo = DrmEntityFactory.getInstance().createDataObject();
/* 728 */       simpleDbo.setCuid(relatedCuid);
/*     */       try {
/* 730 */         relatedDbo = this.serviceDAO.getDynObject(actionContext, relatedClassMeta, simpleDbo);
/*     */       } catch (Exception ex) {
/* 732 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     } else {
/* 735 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 736 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 737 */       query.setBmClassId(relatedBmClassId);
/* 738 */       query.setClassMeta(relatedClassMeta);
/* 739 */       query.setSqlCond("CUID='" + relatedCuid + "'");
/* 740 */       IDrmQueryResultSet dbos = getDynObjBySql(actionContext, queryContext, query);
/* 741 */       if ((dbos != null) && (dbos.getResultSet().size() > 0)) {
/* 742 */         relatedDbo = ((IDrmQueryRow)dbos.getResultSet().get(0)).getResultDbo(relatedDbClassId);
/*     */       }
/*     */     }
/*     */ 
/* 746 */     if (relatedDbo == null) return null;
/*     */ 
/* 748 */     if (isRealBmClassId)
/* 749 */       relatedDbo.setBmClassId(relatedBmClassId);
/*     */     else {
/* 751 */       for (String _relatedBmClassId : relatedBmClassIds) {
/* 752 */         BMClassMeta _relatedClassMeta = getBMModelService().getClassMeta(actionContext, _relatedBmClassId);
/* 753 */         if (_relatedClassMeta.isChildBmObject(relatedDbo)) {
/* 754 */           relatedDbo.setBmClassId(_relatedClassMeta.getBmClassId());
/*     */         }
/*     */       }
/*     */     }
/* 758 */     return relatedDbo;
/*     */   }
/*     */ 
/*     */   private static String parseDbClassId(String cuid) {
/* 762 */     String dbClassId = null;
/* 763 */     if ((cuid instanceof String)) {
/* 764 */       dbClassId = parseClassNameFromCuid(cuid);
/*     */     }
/* 766 */     if ((dbClassId != null) && (dbClassId.trim().length() == 0)) {
/* 767 */       dbClassId = null;
/*     */     }
/* 769 */     return dbClassId;
/*     */   }
/*     */ 
/*     */   private static String parseClassNameFromCuid(String cuid) {
/* 773 */     String className = null;
/* 774 */     if ((cuid != null) && (!cuid.equals(""))) {
/* 775 */       String[] cuids = cuid.split("-");
/* 776 */       if (cuids.length > 1) {
/* 777 */         className = cuids[0];
/*     */       }
/*     */     }
/* 780 */     return className;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getLabelValuesBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, String sql, GenericDO[] dboTemplates, String lvDbClassId, String labelAttrId, String valueAttrId)
/*     */     throws UserException
/*     */   {
/* 786 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 788 */       rs = this.serviceDAO.selectDBOs(ServiceBoAdaptor.drmQryCxt2boQryCxt(queryContext), sql, dboTemplates);
/* 789 */       List rows = rs.getResultSet();
/*     */ 
/* 791 */       for (IDrmQueryRow row : rows) {
/* 792 */         IDrmDataObject dbo = row.getResultDbo(lvDbClassId);
/* 793 */         IDrmRelatedIdValue lv = DrmEntityFactory.getInstance().createRelatedIdValue("", dbo.getDbClassId(), dbo.getDbClassId(), "CUID", dbo.getAttrValue(valueAttrId), (String)dbo.getAttrValue(labelAttrId));
/*     */ 
/* 795 */         dbo.getAllAttr().clear();
/* 796 */         dbo.setDboId(null);
/* 797 */         dbo.setAttrValue(labelAttrId, lv);
/*     */       }
/*     */     } catch (Exception ex) {
/* 800 */       LogHome.getLog().error("", ex);
/*     */     }
/* 802 */     return rs;
/*     */   }
/*     */ 
/*     */   public Map<String, Integer> getRelatedDeleteObjectCount(IServiceActionContext actionContext, IDrmDataObject deleteObj, boolean isCascade) throws UserException
/*     */   {
/* 807 */     return this.relatedObjectService.getRelatedDeleteObjectCount(actionContext, deleteObj, isCascade);
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getRelatedDeleteObjects(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, DrmSingleClassQuery query, IDrmDataObject deleteObj, int relatedCount, boolean isSelfChildNode) throws UserException
/*     */   {
/* 812 */     return this.relatedObjectService.getRelatedDeleteObjects(actionContext, queryContext, query, deleteObj, relatedCount, isSelfChildNode);
/*     */   }
/*     */ 
/*     */   public int getDynObjCountBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws UserException
/*     */   {
/* 817 */     int count = 0;
/*     */     try {
/* 819 */       BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, query.getBmClassId());
/* 820 */       query.setClassMeta(classMeta);
/*     */ 
/* 822 */       String whereSql = getAuthenticationService().getBmClassMetaAuthenticationSql(actionContext);
/* 823 */       if (whereSql != null) {
/* 824 */         query.setSqlFilter(whereSql);
/*     */       }
/* 826 */       count = this.serviceDAO.getDynObjCount(actionContext, query);
/*     */     } catch (Exception ex) {
/* 828 */       LogHome.getLog().error("", ex);
/* 829 */       throw new UserException(ex.getMessage());
/*     */     }
/* 831 */     return count;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<Integer, Integer>> getEnumCount(IServiceActionContext actionContext, String bmClassId, List<String> attrIds) throws UserException {
/* 835 */     Map enumAttrIdCount = new HashMap();
/*     */ 
/* 837 */     actionContext.setBmClassId(bmClassId);
/* 838 */     String whereSql = getAuthenticationService().getBmClassMetaAuthenticationSql(actionContext);
/* 839 */     for (int i = 0; i < attrIds.size(); i++) {
/* 840 */       Map enumCount = new HashMap();
/*     */       try {
/* 842 */         enumCount = this.serviceDAO.getEnumCount(actionContext, bmClassId, (String)attrIds.get(i), whereSql);
/*     */       } catch (Exception ex) {
/* 844 */         LogHome.getLog().error("", ex);
/*     */       }
/* 846 */       enumAttrIdCount.put(attrIds.get(i), enumCount);
/*     */     }
/* 848 */     return enumAttrIdCount;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getClassStatResultSet(IServiceActionContext actionContext, IDrmQueryContext queryContext, String bmClassId, List<String> attrIds) throws UserException {
/* 852 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 853 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 855 */       queryContext.setCountBeforQuery(false);
/*     */ 
/* 857 */       actionContext.setBmClassId(bmClassId);
/* 858 */       String userSql = getAuthenticationService().getBmClassMetaAuthenticationSql(actionContext);
/* 859 */       List rsCount = this.serviceDAO.getClassStatCount(actionContext, queryContext, bmClassId, attrIds, userSql);
/* 860 */       if (rsCount.size() == 0) return null;
/* 861 */       queryContext.setPopulate(true);
/* 862 */       queryContext.setEntity(true);
/* 863 */       queryContext.setCountBeforQuery(true);
/* 864 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 865 */       query.setBmClassId(bmClassId);
/* 866 */       query.setClassMeta(classMeta);
/* 867 */       String sql = " group by ";
/* 868 */       String sqlWhere = "";
/* 869 */       List drmQueryAttrConds = new ArrayList();
/* 870 */       for (String attrId : attrIds) {
/* 871 */         sql = sql + attrId + ",";
/* 872 */         sqlWhere = sqlWhere + attrId + " is not null and ";
/*     */       }
/* 874 */       query.setSqlCond(sqlWhere.substring(0, sqlWhere.length() - 5) + sql.substring(0, sql.length() - 1));
/* 875 */       query.setQueryAttrIds(attrIds);
/* 876 */       query.setQueryCondExps(drmQueryAttrConds);
/* 877 */       rs = getDynObjBySql(actionContext, queryContext, query);
/* 878 */       List rows = rs.getResultSet();
/* 879 */       for (int i = 0; i < rsCount.size(); i++) {
/* 880 */         IDrmQueryRow row = (IDrmQueryRow)rows.get(i);
/* 881 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 882 */         dbo.setAttrValue("count", rsCount.get(i));
/*     */       }
/*     */     } catch (Exception ex) {
/* 885 */       LogHome.getLog().error("", ex);
/*     */     }
/* 887 */     return rs;
/*     */   }
/*     */ 
/*     */   public void clearQueryResult(IServiceActionContext actionContext, IDrmMemQueryContext queryContext) {
/* 891 */     this.memoryQueryService.clearMemoryData(queryContext);
/*     */   }
/*     */ 
/*     */   public List<DrmAttrCheckCount> getCheckCounts(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, String bmClassId, List<DrmCheckAttrId> checkAttrIds) throws UserException
/*     */   {
/* 896 */     return this.checkService.getCheckCounts(actionContext, queryContext, bmClassId, checkAttrIds);
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getCheckResult(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, DrmAttrCheckCount checkCount) throws UserException
/*     */   {
/* 901 */     return this.checkService.getCheckResult(actionContext, queryContext, checkCount);
/*     */   }
/*     */ 
/*     */   public List<IDrmLabelValue> getDbEnums(IServiceActionContext actionContext, String dbClassId) {
/* 905 */     List enums = new ArrayList();
/*     */     try {
/* 907 */       String sql = "select KEY_NUM, KEY_VALUE from " + dbClassId;
/* 908 */       DataObjectList dbos = this.serviceDAO.selectDBOs(sql, new Class[] { Long.TYPE, String.class });
/* 909 */       for (GenericDO dbo : dbos) {
/* 910 */         long value = dbo.getAttrLong("1");
/* 911 */         String label = dbo.getAttrString("2");
/* 912 */         IDrmLabelValue _enum = DrmEntityFactory.getInstance().createLabelValue(label, Long.valueOf(value));
/* 913 */         enums.add(_enum);
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 917 */       LogHome.getLog().error("", ex);
/*     */     }
/* 919 */     return enums;
/*     */   }
/*     */ 
/*     */   public void setServiceDAO(IDynResManageServiceDAO dao) {
/* 923 */     this.serviceDAO = dao;
/*     */   }
/*     */ 
/*     */   public IDynResManageServiceDAO getServiceDAO() {
/* 927 */     return this.serviceDAO;
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService() {
/* 931 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static IVMModelService getVMModelService() {
/* 935 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ 
/*     */   public static IAuthenticationService getAuthenticationService() {
/* 939 */     return (IAuthenticationService)ServiceHomeFactory.getInstance().getService("AuthenticationService");
/*     */   }
/*     */ 
/*     */   public void setMemoryQueryService(MemoryQueryService memoryQueryService) {
/* 943 */     this.memoryQueryService = memoryQueryService;
/*     */   }
/*     */ 
/*     */   public void setRelatedObjectService(RelatedObjectService relatedObjectService) {
/* 947 */     this.relatedObjectService = relatedObjectService;
/*     */   }
/*     */ 
/*     */   private IDrmDataObject fillDrmDataObject(IDrmDataObject dro)
/*     */     throws Exception
/*     */   {
/* 953 */     IServiceActionContext actionContext = new ServiceActionContext();
/* 954 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, dro.getBmClassId());
/* 955 */     IDrmDataObject fulldbo = DrmEntityFactory.getInstance().createDataObject();
/* 956 */     fulldbo.setDbClassId(classMeta.getDbClassId());
/* 957 */     fulldbo.setCuid(dro.getCuid());
/* 958 */     fulldbo.setDboId(dro.getDboId());
/* 959 */     fulldbo = this.serviceDAO.getDynObject(actionContext, classMeta, fulldbo);
/* 960 */     fulldbo.getAllAttr().putAll(dro.getAllAttr());
/* 961 */     return fulldbo;
/*     */   }
/*     */ 
/*     */   public void setCheckService(DynCheckService checkService) {
/* 965 */     this.checkService = checkService;
/*     */   }
/*     */ 
/*     */   private static class CuidCache extends HashMap<String, IDrmLabelValue>
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.impl.DynResManageService
 * JD-Core Version:    0.6.0
 */