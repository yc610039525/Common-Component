/*     */ package com.boco.raptor.drm.core.service.dynres.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckCount;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckResult;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmCheckAttrId;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmCheckErrorTypeEnum;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmCheckTypeEnum;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DynCheckService
/*     */ {
/*     */   private MemoryQueryService memoryQueryService;
/*     */   private IDynResManageServiceDAO serviceDAO;
/*     */ 
/*     */   public IDrmQueryResultSet getCheckResult(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, DrmAttrCheckCount checkCount)
/*     */     throws UserException
/*     */   {
/*  57 */     IDrmQueryResultSet rs = DrmEntityFactory.getInstance().createResultSet();
/*  58 */     rs.setCountValue(checkCount.getCount());
/*     */ 
/*  60 */     List checkResults = (List)this.memoryQueryService.getMemoryData(queryContext);
/*  61 */     if (checkResults == null) return null;
/*     */ 
/*  63 */     List thisCheckResults = new ArrayList();
/*  64 */     for (DrmAttrCheckResult checkResult : checkResults) {
/*  65 */       if ((checkResult._getCheckType() == checkCount._getCheckType()) && (checkResult._getErrorType() == checkCount._getErrorType()) && (checkResult.getAttrId().equals(checkCount.getAttrId())))
/*     */       {
/*  68 */         thisCheckResults.add(checkResult);
/*     */       }
/*     */     }
/*     */ 
/*  72 */     int i = queryContext.getOffset();
/*  73 */     for (; (i < thisCheckResults.size()) && (i < queryContext.getOffset() + queryContext.getFetchSize()); i++) {
/*  74 */       DrmAttrCheckResult checkResult = (DrmAttrCheckResult)thisCheckResults.get(i);
/*  75 */       IDrmQueryRow row = DrmEntityFactory.getInstance().createQueryRow();
/*  76 */       IDrmDataObject dbo = DrmEntityFactory.getInstance().createDataObject();
/*  77 */       dbo.setBmClassId(checkCount.getBmClassId());
/*  78 */       dbo.setAttrValue("CUID", checkResult);
/*  79 */       row.addResultDbo(dbo);
/*  80 */       rs.addRow(row);
/*     */     }
/*  82 */     return rs;
/*     */   }
/*     */ 
/*     */   public List<DrmAttrCheckCount> getCheckCounts(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, String bmClassId, List<DrmCheckAttrId> checkAttrIds) throws UserException
/*     */   {
/*  87 */     List checkCountList = new ArrayList();
/*  88 */     List checkResults = new ArrayList();
/*  89 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/*  90 */     if (classMeta == null) {
/*  91 */       throw new UserException("未配置业务模型：bmClassId=" + bmClassId);
/*     */     }
/*     */ 
/*  94 */     List uniqueCheck = getCheckAttrIds(checkAttrIds, DrmCheckTypeEnum.UNIQUE_ID);
/*  95 */     if (uniqueCheck.size() > 0) {
/*  96 */       for (String uniqueName : classMeta.getUniqueAttrIds().keySet()) {
/*  97 */         List _checkResults = checkUniqueAttr(actionContext, classMeta, uniqueName);
/*  98 */         if (_checkResults.size() > 0) {
/*  99 */           checkResults.addAll(_checkResults);
/* 100 */           checkCountList.add(getCheckCountFromResult(bmClassId, uniqueName, _checkResults, DrmCheckTypeEnum.UNIQUE_ID, DrmCheckErrorTypeEnum.UNIQUE_REPEAT));
/*     */         }
/*     */       }
/*     */ 
/* 104 */       List _checkResults = checkUniqueAttr(actionContext, classMeta);
/* 105 */       if (_checkResults.size() > 0) {
/* 106 */         checkResults.addAll(_checkResults);
/* 107 */         checkCountList.add(getCheckCountFromResult(bmClassId, "唯一性", _checkResults, DrmCheckTypeEnum.UNIQUE_ID, DrmCheckErrorTypeEnum.UNIQUE_UNCOMPLETED));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 112 */     List _checkAttrIds = getCheckAttrIds(checkAttrIds, DrmCheckTypeEnum.ENUM);
/* 113 */     for (String checkAttrId : _checkAttrIds) {
/* 114 */       List _checkResults = checkEnumAttr(actionContext, classMeta, classMeta.getAttrMeta(checkAttrId));
/* 115 */       if ((_checkResults != null) && (_checkResults.size() > 0)) {
/* 116 */         checkResults.addAll(_checkResults);
/* 117 */         checkCountList.add(getCheckCountFromResult(bmClassId, checkAttrId, _checkResults, DrmCheckTypeEnum.ENUM, DrmCheckErrorTypeEnum.INVALID_ENUM));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 122 */     _checkAttrIds = getCheckAttrIds(checkAttrIds, DrmCheckTypeEnum.RELATION);
/* 123 */     for (String checkAttrId : _checkAttrIds) {
/* 124 */       List _checkResults = checkRelationAttr(actionContext, classMeta, classMeta.getAttrMeta(checkAttrId));
/* 125 */       if (_checkResults.size() > 0) {
/* 126 */         checkResults.addAll(_checkResults);
/* 127 */         checkCountList.add(getCheckCountFromResult(bmClassId, checkAttrId, _checkResults, DrmCheckTypeEnum.RELATION, DrmCheckErrorTypeEnum.RELATION_UN_EXIST));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 132 */     _checkAttrIds = getCheckAttrIds(checkAttrIds, DrmCheckTypeEnum.VAL_EXPR);
/* 133 */     for (String checkAttrId : _checkAttrIds) {
/* 134 */       List _checkResults = checkCalcExprAttr(actionContext, classMeta, classMeta.getAttrMeta(checkAttrId));
/*     */ 
/* 136 */       if (_checkResults.size() > 0) {
/* 137 */         checkResults.addAll(_checkResults);
/* 138 */         checkCountList.add(getCheckCountFromResult(bmClassId, checkAttrId, _checkResults, DrmCheckTypeEnum.VAL_EXPR, DrmCheckErrorTypeEnum.INVALID_CACL_EXP));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 143 */     _checkAttrIds = getCheckAttrIds(checkAttrIds, DrmCheckTypeEnum.NOT_NULL);
/* 144 */     for (String checkAttrId : _checkAttrIds) {
/* 145 */       List _checkResults = checkNotNullAttr(actionContext, classMeta, classMeta.getAttrMeta(checkAttrId));
/*     */ 
/* 147 */       if (_checkResults.size() > 0) {
/* 148 */         checkResults.addAll(_checkResults);
/* 149 */         checkCountList.add(getCheckCountFromResult(bmClassId, checkAttrId, _checkResults, DrmCheckTypeEnum.NOT_NULL, DrmCheckErrorTypeEnum.IS_NULL));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     this.memoryQueryService.setMemoryData(queryContext, checkResults);
/* 155 */     return checkCountList;
/*     */   }
/*     */ 
/*     */   private DrmAttrCheckCount getCheckCountFromResult(String bmClassId, String checkAttrId, List<DrmAttrCheckResult> _checkResults, DrmCheckTypeEnum checkType, DrmCheckErrorTypeEnum errorType)
/*     */   {
/* 160 */     DrmAttrCheckCount checkCount = new DrmAttrCheckCount();
/* 161 */     checkCount.setAttrId(checkAttrId);
/* 162 */     checkCount._setCheckType(checkType);
/* 163 */     checkCount._setErrorType(errorType);
/* 164 */     checkCount.setCount(_checkResults.size());
/* 165 */     checkCount.setBmClassId(bmClassId);
/* 166 */     return checkCount;
/*     */   }
/*     */ 
/*     */   private List<String> getCheckAttrIds(List<DrmCheckAttrId> checkAttrIds, DrmCheckTypeEnum checkType) {
/* 170 */     List attrIds = new ArrayList();
/* 171 */     for (DrmCheckAttrId checkAttrId : checkAttrIds) {
/* 172 */       if (checkAttrId._getCheckType() == checkType) {
/* 173 */         attrIds.add(checkAttrId.getCheckAttrId());
/*     */       }
/*     */     }
/* 176 */     return attrIds;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkEnumAttr(IServiceActionContext actionContext, BMClassMeta classMeta, BMAttrMeta attrMeta) {
/* 180 */     List checkResults = new ArrayList();
/* 181 */     BMEnumMeta enumMeta = getBMModelService().getEnumMeta(actionContext, attrMeta.getEnumId());
/* 182 */     if (enumMeta == null) {
/* 183 */       LogHome.getLog().error("枚举，enumId=" + attrMeta.getEnumId() + "， 未配置业务枚举 ！");
/* 184 */       return checkResults;
/*     */     }
/* 186 */     String sqlCond = "";
/* 187 */     List enums = enumMeta.getEnums();
/* 188 */     for (int i = 0; (enums != null) && (i < enums.size()); i++) {
/* 189 */       if (i == 0) {
/* 190 */         if (sqlCond.length() > 0) sqlCond = sqlCond + " AND ";
/* 191 */         sqlCond = sqlCond + attrMeta.getAttrId() + " NOT IN ( ";
/* 192 */       } else if (i > 0) {
/* 193 */         sqlCond = sqlCond + ", ";
/*     */       }
/* 195 */       sqlCond = sqlCond + ((IDrmEnumValue)enums.get(i)).getEnumValue();
/* 196 */       if (i == enums.size() - 1) {
/* 197 */         sqlCond = sqlCond + " ) ";
/*     */       }
/*     */     }
/* 200 */     IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 201 */     if (rs != null) {
/* 202 */       for (IDrmQueryRow row : rs.getResultSet()) {
/* 203 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 204 */         DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, attrMeta.getAttrId(), DrmCheckTypeEnum.ENUM, DrmCheckErrorTypeEnum.INVALID_ENUM);
/*     */ 
/* 206 */         checkResults.add(checkResult);
/*     */       }
/*     */     }
/* 209 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private IDrmQueryResultSet getCheckDbResult(IServiceActionContext actionContext, BMClassMeta classMeta, String sqlCond) {
/* 213 */     IDrmQueryResultSet result = null;
/*     */     try {
/* 215 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 216 */       query.setClassMeta(classMeta);
/* 217 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 218 */       queryContext.setPopulate(true);
/* 219 */       query.setSqlCond(sqlCond);
/* 220 */       query.setQueryAttrIds(getCheckQueryAttrIds(classMeta));
/* 221 */       result = getDynRMService().getDynObjBySql(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/*     */     }
/* 224 */     return result;
/*     */   }
/*     */ 
/*     */   private DrmAttrCheckResult checkResultDbo(IDrmDataObject dbo, BMClassMeta classMeta, String checkAttrId, DrmCheckTypeEnum checkType, DrmCheckErrorTypeEnum errorType)
/*     */   {
/* 229 */     DrmAttrCheckResult checkResult = new DrmAttrCheckResult();
/* 230 */     String label = classMeta.getDisplayLabel(dbo);
/* 231 */     checkResult.setAttrId(checkAttrId);
/* 232 */     checkResult.setLabel(label);
/* 233 */     checkResult.setValue(dbo.getCuid());
/* 234 */     checkResult._setErrorType(errorType);
/* 235 */     checkResult._setCheckType(checkType);
/* 236 */     return checkResult;
/*     */   }
/*     */ 
/*     */   private List<String> getCheckQueryAttrIds(BMClassMeta classMeta) {
/* 240 */     List queryAttrIds = new ArrayList();
/* 241 */     queryAttrIds.add(classMeta.getCuidAttrId());
/* 242 */     queryAttrIds.add(classMeta.getPkAttrId());
/* 243 */     List labelAttrIds = classMeta.getConstructLabelAttrIds();
/* 244 */     queryAttrIds.addAll(labelAttrIds);
/* 245 */     return queryAttrIds;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkUniqueAttr(IServiceActionContext actionContext, BMClassMeta classMeta) {
/* 249 */     List checkResults = new ArrayList();
/* 250 */     String sqlCond = " 1=1 ";
/* 251 */     for (String[] uniqueAttrIds : classMeta.getUniqueAttrIds().values()) {
/* 252 */       String sqlUnique = "(";
/* 253 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 254 */         if (i > 0) sqlUnique = sqlUnique + " OR ";
/* 255 */         sqlUnique = sqlUnique + uniqueAttrIds[i] + " IS NULL ";
/*     */       }
/* 257 */       sqlUnique = sqlUnique + ")";
/* 258 */       sqlCond = sqlCond + " AND " + sqlUnique;
/*     */     }
/* 260 */     IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 261 */     if (rs != null) {
/* 262 */       for (IDrmQueryRow row : rs.getResultSet()) {
/* 263 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 264 */         DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, "唯一性", DrmCheckTypeEnum.UNIQUE_ID, DrmCheckErrorTypeEnum.UNIQUE_UNCOMPLETED);
/*     */ 
/* 266 */         checkResults.add(checkResult);
/*     */       }
/*     */     }
/* 269 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkUniqueAttr(IServiceActionContext actionContext, BMClassMeta classMeta, String uniqueName) {
/* 273 */     List checkResults = new ArrayList();
/* 274 */     String[] uniqueAttrIds = (String[])classMeta.getUniqueAttrIds().get(uniqueName);
/* 275 */     List nonUniqueObjs = getNonUniqueObjects(classMeta, uniqueName);
/* 276 */     for (IDrmDataObject nonUniqueObj : nonUniqueObjs) {
/* 277 */       String sqlCond = classMeta.getBmDivideSqlCond();
/* 278 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 279 */         Object attrValue = nonUniqueObj.getAttrValue(uniqueAttrIds[i]);
/* 280 */         if (attrValue != null) {
/* 281 */           if (sqlCond.length() > 0) sqlCond = sqlCond + " AND ";
/* 282 */           if (attrValue.getClass() == String.class)
/* 283 */             sqlCond = sqlCond + uniqueAttrIds[i] + "='" + attrValue + "'";
/*     */           else {
/* 285 */             sqlCond = sqlCond + uniqueAttrIds[i] + "=" + attrValue;
/*     */           }
/*     */         }
/*     */       }
/* 289 */       IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 290 */       if (rs != null) {
/* 291 */         for (IDrmQueryRow row : rs.getResultSet()) {
/* 292 */           IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 293 */           DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, uniqueName, DrmCheckTypeEnum.UNIQUE_ID, DrmCheckErrorTypeEnum.UNIQUE_REPEAT);
/*     */ 
/* 295 */           checkResults.add(checkResult);
/*     */         }
/*     */       }
/*     */     }
/* 299 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private List<IDrmDataObject> getNonUniqueObjects(BMClassMeta classMeta, String uniqueName) {
/* 303 */     List invalidDbos = new ArrayList();
/*     */     try {
/* 305 */       String[] uniqueAttrIds = (String[])classMeta.getUniqueAttrIds().get(uniqueName);
/* 306 */       String sql = "SELECT ";
/* 307 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 308 */         if (i > 0) sql = sql + ", ";
/* 309 */         sql = sql + uniqueAttrIds[i];
/*     */       }
/* 311 */       sql = sql + " FROM " + classMeta.getDbClassId() + " WHERE ";
/* 312 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 313 */         if (i > 0) sql = sql + " AND ";
/* 314 */         sql = sql + uniqueAttrIds[i] + " IS NOT NULL ";
/*     */       }
/* 316 */       if (classMeta.getBmDivideSqlCond().length() > 0) {
/* 317 */         sql = sql + " AND " + classMeta.getBmDivideSqlCond();
/*     */       }
/* 319 */       sql = sql + " GROUP BY ";
/* 320 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 321 */         if (i > 0) sql = sql + ", ";
/* 322 */         sql = sql + uniqueAttrIds[i];
/*     */       }
/* 324 */       sql = sql + " HAVING count(*) > 1";
/*     */ 
/* 326 */       Class[] classTypes = new Class[uniqueAttrIds.length];
/* 327 */       for (int i = 0; i < uniqueAttrIds.length; i++) {
/* 328 */         classTypes[i] = classMeta.getAttrMeta(uniqueAttrIds[i]).getAttrClassType();
/*     */       }
/* 330 */       List rs = this.serviceDAO.selectDBOs(sql, classTypes);
/* 331 */       for (int i = 0; i < rs.size(); i++) {
/* 332 */         IDrmDataObject invalidDbo = DrmEntityFactory.getInstance().createDataObject();
/* 333 */         invalidDbo.setBmClassId(classMeta.getBmClassId());
/* 334 */         for (int k = 1; k < uniqueAttrIds.length + 1; k++) {
/* 335 */           IDrmDataObject row = (IDrmDataObject)rs.get(i);
/* 336 */           Object attrValue = row.getAttrValue("" + k);
/* 337 */           invalidDbo.setAttrValue(uniqueAttrIds[(k - 1)], attrValue);
/*     */         }
/* 339 */         invalidDbos.add(invalidDbo);
/*     */       }
/*     */     } catch (Exception ex) {
/* 342 */       LogHome.getLog().error("", ex);
/*     */     }
/* 344 */     return invalidDbos;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkRelationAttr(IServiceActionContext actionContext, BMClassMeta classMeta, BMAttrMeta attrMeta) {
/* 348 */     List checkResults = new ArrayList();
/*     */ 
/* 350 */     Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 351 */     String relatedBmClassId = (String)relatedAttrIds.keySet().toArray()[0];
/* 352 */     String relatedAttrId = (String)relatedAttrIds.values().toArray()[0];
/* 353 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(actionContext, relatedBmClassId);
/*     */ 
/* 355 */     String sqlCond = attrMeta.getAttrId() + " NOT IN " + "( SELECT " + relatedAttrId + " FROM " + relatedClassMeta.getDbClassId() + ")";
/*     */ 
/* 358 */     IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 359 */     if (rs != null) {
/* 360 */       for (IDrmQueryRow row : rs.getResultSet()) {
/* 361 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 362 */         DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, attrMeta.getAttrId(), DrmCheckTypeEnum.RELATION, DrmCheckErrorTypeEnum.RELATION_UN_EXIST);
/*     */ 
/* 364 */         checkResults.add(checkResult);
/*     */       }
/*     */     }
/* 367 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkCalcExprAttr(IServiceActionContext actionContext, BMClassMeta classMeta, BMAttrMeta attrMeta) {
/* 371 */     List checkResults = new ArrayList();
/*     */ 
/* 373 */     String calcExpr = attrMeta.getValidCalcExp();
/* 374 */     Map allAttrMetas = classMeta.getAllAttrMetas();
/* 375 */     for (String attrId : allAttrMetas.keySet()) {
/* 376 */       calcExpr = calcExpr.replace("$[" + attrId + "]", attrId);
/*     */     }
/* 378 */     String sqlCond = " NOT (" + calcExpr + ")";
/*     */ 
/* 380 */     IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 381 */     if (rs != null) {
/* 382 */       for (IDrmQueryRow row : rs.getResultSet()) {
/* 383 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 384 */         DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, attrMeta.getAttrId(), DrmCheckTypeEnum.VAL_EXPR, DrmCheckErrorTypeEnum.INVALID_CACL_EXP);
/*     */ 
/* 386 */         checkResults.add(checkResult);
/*     */       }
/*     */     }
/* 389 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private List<DrmAttrCheckResult> checkNotNullAttr(IServiceActionContext actionContext, BMClassMeta classMeta, BMAttrMeta attrMeta) {
/* 393 */     List checkResults = new ArrayList();
/*     */ 
/* 395 */     String sqlCond = attrMeta.getAttrId() + " IS NULL";
/*     */ 
/* 397 */     IDrmQueryResultSet rs = getCheckDbResult(actionContext, classMeta, sqlCond);
/* 398 */     if (rs != null) {
/* 399 */       for (IDrmQueryRow row : rs.getResultSet()) {
/* 400 */         IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 401 */         DrmAttrCheckResult checkResult = checkResultDbo(dbo, classMeta, attrMeta.getAttrId(), DrmCheckTypeEnum.NOT_NULL, DrmCheckErrorTypeEnum.IS_NULL);
/*     */ 
/* 403 */         checkResults.add(checkResult);
/*     */       }
/*     */     }
/* 406 */     return checkResults;
/*     */   }
/*     */ 
/*     */   private static IDynResManageService getDynRMService() {
/* 410 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService() {
/* 414 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public void setMemoryQueryService(MemoryQueryService memoryQueryService) {
/* 418 */     this.memoryQueryService = memoryQueryService;
/*     */   }
/*     */ 
/*     */   public void setServiceDAO(IDynResManageServiceDAO serviceDAO) {
/* 422 */     this.serviceDAO = serviceDAO;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.impl.DynCheckService
 * JD-Core Version:    0.6.0
 */