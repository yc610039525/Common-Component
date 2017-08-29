/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.excel.ExcelHelper;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.Dwr2DboHelper;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmLabelValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmRelatedIdValue;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckCount;
/*     */ import com.boco.raptor.drm.core.dto.impl.check.DrmCheckAttrId;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportValidListener;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.ClassStatTreeNode;
/*     */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.raptor.drm.core.web.security.SecurityModel;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.io.File;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import jxl.write.WritableSheet;
/*     */ import jxl.write.WritableWorkbook;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DynResManageAction
/*     */ {
/*     */   public IDrmDataObject addDynObject(HttpServletRequest request, IDrmDataObject dro)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/*  73 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  74 */       actionContext.setBmClassId(dro.getBmClassId());
/*  75 */       dro = getDynRMService().addDynObject(actionContext, dro, true);
/*     */     } catch (UserException ex) {
/*  77 */       LogHome.getLog().error("", ex);
/*  78 */       throw new UserException(ex.getMessage());
/*     */     }
/*  80 */     return dro;
/*     */   }
/*     */ 
/*     */   public void modifyDynObject(HttpServletRequest request, IDrmDataObject dro) throws UserException {
/*     */     try {
/*  85 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  86 */       actionContext.setBmClassId(dro.getBmClassId());
/*  87 */       getDynRMService().modifyDynObject(actionContext, dro, true);
/*     */     } catch (Exception ex) {
/*  89 */       LogHome.getLog().error("", ex);
/*  90 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyDynObjects(HttpServletRequest request, List<IDrmDataObject> dros, Map modifyAttrs) throws UserException {
/*     */     try {
/*  96 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  97 */       if ((dros != null) && (dros.size() > 0)) {
/*  98 */         actionContext.setBmClassId(((IDrmDataObject)dros.get(0)).getBmClassId());
/*     */       }
/* 100 */       getDynRMService().modifyDynObjects(ServiceHelper.getUserSvActCxt(request), dros, modifyAttrs, true);
/*     */     } catch (Exception ex) {
/* 102 */       LogHome.getLog().error("", ex);
/* 103 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteDynObjects(HttpServletRequest request, List<IDrmDataObject> dros) throws UserException {
/*     */     try {
/* 109 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 110 */       if ((dros != null) && (dros.size() > 0)) {
/* 111 */         actionContext.setBmClassId(((IDrmDataObject)dros.get(0)).getBmClassId());
/*     */       }
/* 113 */       getDynRMService().deleteDynObjects(ServiceHelper.getUserSvActCxt(request), dros, true);
/*     */     } catch (Exception ex) {
/* 115 */       LogHome.getLog().error("", ex);
/* 116 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getDynObjBySql(HttpServletRequest request, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws UserException {
/* 121 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 123 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 124 */       actionContext.setBmClassId(query.getBmClassId());
/* 125 */       rs = getDynRMService().getDynObjBySql(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/* 127 */       LogHome.getLog().error("", ex);
/* 128 */       throw new UserException(ex.getMessage());
/*     */     }
/* 130 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getUserDistrictTree(HttpServletRequest request, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws UserException {
/* 134 */     IDrmQueryResultSet rs = getDynObjBySql(request, queryContext, query);
/* 135 */     List dbos = rs.getResultSet();
/* 136 */     VMModelAction vma = new VMModelAction();
/* 137 */     boolean isAdmin = vma.getCurrentUserIsAdmin(request);
/* 138 */     if (!isAdmin) {
/* 139 */       List userDis = userDistrictList(request);
/* 140 */       if ((userDis != null) && (userDis.size() > 0) && 
/* 141 */         (dbos != null) && (dbos.size() > 0)) {
/* 142 */         for (int i = 0; i < dbos.size(); i++) {
/* 143 */           IDrmDataObject dbo = ((IDrmQueryRow)dbos.get(i)).getResultDbo(query.getBmClassId());
/* 144 */           if (userDis.contains(dbo.getCuid()))
/* 145 */             dbo.setAttrValue("isRole", Boolean.valueOf(true));
/*     */           else {
/* 147 */             dbo.setAttrValue("isRole", Boolean.valueOf(false));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 153 */     return rs;
/*     */   }
/*     */ 
/*     */   private List userDistrictList(HttpServletRequest request) {
/* 157 */     List userList = SecurityModel.getInstance().getUserDistricts(request);
/* 158 */     return userList;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getDynObjectBySql(HttpServletRequest request, IDrmQueryContext queryContext, DrmSingleClassQuery query)
/*     */     throws UserException
/*     */   {
/* 166 */     IDrmQueryResultSet rs = null;
/* 167 */     IDrmDataObject robj = null;
/* 168 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */     try {
/* 170 */       actionContext.setBmClassId(query.getBmClassId());
/* 171 */       rs = getDynRMService().getDynObjBySql(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/* 173 */       LogHome.getLog().error("", ex);
/* 174 */       throw new UserException(ex.getMessage());
/*     */     }
/* 176 */     if ((rs != null) && (rs.getResultSet().size() > 0)) {
/* 177 */       String dbClassId = "";
/* 178 */       if (query.getClassMeta() == null) {
/* 179 */         BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, query.getBmClassId());
/* 180 */         if (bmClassMeta != null)
/* 181 */           dbClassId = bmClassMeta.getDbClassId();
/*     */         else
/* 183 */           throw new UserException("根据[" + query.getBmClassId() + "]找不到业务对象描述！");
/*     */       }
/*     */       else {
/* 186 */         dbClassId = query.getClassMeta().getDbClassId();
/*     */       }
/*     */ 
/* 189 */       robj = ((IDrmQueryRow)rs.getResultSet().get(0)).getResultDbo(dbClassId);
/*     */     }
/* 191 */     return robj;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getDynObject(HttpServletRequest request, IDrmQueryContext queryContext, IDrmDataObject simpleDro) throws UserException {
/* 195 */     IDrmDataObject fullDro = null;
/*     */     try {
/* 197 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 198 */       actionContext.setBmClassId(simpleDro.getBmClassId());
/* 199 */       fullDro = getDynRMService().getDynObject(actionContext, queryContext, simpleDro);
/*     */     } catch (Exception ex) {
/* 201 */       LogHome.getLog().error("", ex);
/* 202 */       throw new UserException(ex.getMessage());
/*     */     }
/* 204 */     return fullDro;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getRelatedAttrValues(HttpServletRequest request, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws UserException {
/* 208 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 210 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 211 */       actionContext.setBmClassId(query.getBmClassId());
/* 212 */       rs = getDynRMService().getRelatedAttrValues(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/* 214 */       LogHome.getLog().error("", ex);
/* 215 */       throw new UserException(ex.getMessage());
/*     */     }
/* 217 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getLabelValuesBySql(HttpServletRequest request, IDrmQueryContext queryContext, Map<String, String> queryCond) throws UserException {
/* 221 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 223 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 224 */       String sql = (String)queryCond.get("sql");
/* 225 */       String dbClassIds = (String)queryCond.get("dbClassIds");
/* 226 */       String lvDbClassId = (String)queryCond.get("lvDbClassId");
/* 227 */       String labelAttrId = (String)queryCond.get("labelAttrId");
/* 228 */       String valueAttrId = (String)queryCond.get("valueAttrId");
/* 229 */       actionContext.setBmClassId(lvDbClassId);
/* 230 */       String[] _dbClassIds = dbClassIds.split(",");
/* 231 */       GenericDO[] dboTemplates = new GenericDO[_dbClassIds.length];
/* 232 */       for (int i = 0; i < _dbClassIds.length; i++) {
/* 233 */         GenericDO dboTemplate = new GenericDO(_dbClassIds[i]);
/* 234 */         dboTemplates[i] = dboTemplate.createInstanceByClassName();
/*     */       }
/* 236 */       rs = getDynRMService().getLabelValuesBySql(actionContext, queryContext, sql, dboTemplates, lvDbClassId, labelAttrId, valueAttrId);
/*     */     }
/*     */     catch (Exception ex) {
/* 239 */       LogHome.getLog().error("", ex);
/* 240 */       throw new UserException(ex.getMessage());
/*     */     }
/* 242 */     return rs;
/*     */   }
/*     */ 
/*     */   public Map<String, Integer> getRelatedDeleteObjectCount(HttpServletRequest request, IDrmDataObject deleteObj, boolean isCascade) throws UserException {
/* 246 */     Map rs = null;
/*     */     try {
/* 248 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 249 */       actionContext.setBmClassId(deleteObj.getBmClassId());
/* 250 */       rs = getDynRMService().getRelatedDeleteObjectCount(actionContext, deleteObj, isCascade);
/*     */     } catch (Exception ex) {
/* 252 */       LogHome.getLog().error("", ex);
/* 253 */       throw new UserException(ex.getMessage());
/*     */     }
/* 255 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getObjectBySql(HttpServletRequest request, IDrmQueryContext queryContext, Map<String, String> queryCond) throws UserException
/*     */   {
/* 260 */     IDrmDataObject dbo = null;
/*     */     try {
/* 262 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 263 */       String sql = (String)queryCond.get("sql");
/* 264 */       String dbClassId = (String)queryCond.get("dbClassId");
/* 265 */       GenericDO dboTemplate = new GenericDO(dbClassId);
/* 266 */       dboTemplate = dboTemplate.createInstanceByClassName();
/*     */ 
/* 268 */       IDrmQueryResultSet rs = getDynRMService().getObjectsBySql(actionContext, queryContext, sql, new GenericDO[] { dboTemplate });
/* 269 */       if (rs.getResultSet().size() > 0)
/* 270 */         dbo = ((IDrmQueryRow)rs.getResultSet().get(0)).getResultDbo(dbClassId);
/*     */     }
/*     */     catch (Exception ex) {
/* 273 */       LogHome.getLog().error("", ex);
/* 274 */       throw new UserException(ex.getMessage());
/*     */     }
/* 276 */     return dbo;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getObjectsBySql(HttpServletRequest request, IDrmQueryContext queryContext, Map<String, String> queryCond) throws UserException
/*     */   {
/* 281 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 283 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 284 */       String sql = (String)queryCond.get("sql");
/* 285 */       String dbClassIds = (String)queryCond.get("dbClassIds");
/* 286 */       String[] _dbClassIds = dbClassIds.split(",");
/* 287 */       GenericDO[] dboTemplates = new GenericDO[_dbClassIds.length];
/* 288 */       for (int i = 0; i < _dbClassIds.length; i++) {
/* 289 */         GenericDO dboTemplate = new GenericDO(_dbClassIds[i]);
/* 290 */         dboTemplates[i] = dboTemplate.createInstanceByClassName();
/*     */       }
/*     */ 
/* 293 */       rs = getDynRMService().getObjectsBySql(actionContext, queryContext, sql, dboTemplates);
/*     */     } catch (Exception ex) {
/* 295 */       LogHome.getLog().error("", ex);
/* 296 */       throw new UserException(ex.getMessage());
/*     */     }
/* 298 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getRelatedDeleteObjects(HttpServletRequest request, IDrmMemQueryContext queryContext, DrmSingleClassQuery query, IDrmDataObject deleteObj, int relatedCount, boolean isSelfChildNode)
/*     */     throws UserException
/*     */   {
/* 304 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 306 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 307 */       actionContext.setBmClassId(query.getBmClassId());
/* 308 */       queryContext.setSessionId(request.getRequestedSessionId());
/* 309 */       rs = getDynRMService().getRelatedDeleteObjects(actionContext, queryContext, query, deleteObj, relatedCount, isSelfChildNode);
/*     */     }
/*     */     catch (Exception ex) {
/* 312 */       LogHome.getLog().error("", ex);
/* 313 */       throw new UserException(ex.getMessage());
/*     */     }
/* 315 */     return rs;
/*     */   }
/*     */ 
/*     */   public void clearQueryResult(HttpServletRequest request, IDrmMemQueryContext queryContext) {
/*     */     try {
/* 320 */       queryContext.setSessionId(request.getRequestedSessionId());
/* 321 */       getDynRMService().clearQueryResult(ServiceHelper.getUserSvActCxt(request), queryContext);
/*     */     } catch (Exception ex) {
/* 323 */       LogHome.getLog().error("", ex);
/* 324 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<DrmAttrCheckCount> getCheckCounts(HttpServletRequest request, IDrmMemQueryContext queryContext, String bmClassId, List<DrmCheckAttrId> checkAttrIds) throws UserException
/*     */   {
/* 330 */     List rs = null;
/*     */     try {
/* 332 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 333 */       actionContext.setBmClassId(bmClassId);
/* 334 */       queryContext.setSessionId(request.getRequestedSessionId());
/* 335 */       rs = getDynRMService().getCheckCounts(actionContext, queryContext, bmClassId, checkAttrIds);
/*     */     } catch (Exception ex) {
/* 337 */       LogHome.getLog().error("", ex);
/* 338 */       throw new UserException(ex.getMessage());
/*     */     }
/* 340 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getCheckResult(HttpServletRequest request, IDrmMemQueryContext queryContext, DrmAttrCheckCount checkCount) throws UserException
/*     */   {
/* 345 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 347 */       queryContext.setSessionId(request.getRequestedSessionId());
/* 348 */       rs = getDynRMService().getCheckResult(ServiceHelper.getUserSvActCxt(request), queryContext, checkCount);
/*     */     } catch (Exception ex) {
/* 350 */       LogHome.getLog().error("", ex);
/* 351 */       throw new UserException(ex.getMessage());
/*     */     }
/* 353 */     return rs;
/*     */   }
/*     */ 
/*     */   public Map<String, Integer> getStatCount(HttpServletRequest request, String cuid, Map<String, String> bmClassIds) throws UserException {
/* 357 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 358 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 359 */     Map bmClassIdStatCount = new HashMap();
/* 360 */     Set keyset = bmClassIds.keySet();
/* 361 */     Iterator ite = keyset.iterator();
/* 362 */     while (ite.hasNext()) { DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 364 */       String bmClassId = (String)ite.next();
/* 365 */       String attrId = (String)bmClassIds.get(bmClassId);
/* 366 */       query.setBmClassId(bmClassId);
/* 367 */       query.addQueryCondExps(new DrmQueryAttrCond(attrId, "=", cuid));
/*     */       int statCount;
/*     */       try { statCount = getDynResManageService().getDynObjCountBySql(actionContext, queryContext, query);
/*     */       } catch (Exception ex) {
/* 372 */         LogHome.getLog().error("", ex);
/* 373 */         throw new UserException(ex.getMessage());
/*     */       }
/* 375 */       bmClassIdStatCount.put(bmClassId, Integer.valueOf(statCount));
/*     */     }
/* 377 */     return bmClassIdStatCount;
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue getRelatedIdLabelByClassValue(HttpServletRequest request, String bmClassId, String attrId, String cuid) throws UserException {
/* 381 */     IDrmLabelValue rs = null;
/*     */     try {
/* 383 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 384 */       actionContext.setBmClassId(bmClassId);
/* 385 */       rs = getDynRMService().getRelatedIdLabelByClassValue(actionContext, bmClassId, attrId, cuid);
/*     */     } catch (Exception ex) {
/* 387 */       LogHome.getLog().error("", ex);
/* 388 */       throw new UserException(ex.getMessage());
/*     */     }
/* 390 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getRelatedObjectByClassValue(HttpServletRequest request, String bmClassId, String attrId, String cuid) throws UserException
/*     */   {
/* 395 */     IDrmDataObject rs = null;
/*     */     try {
/* 397 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 398 */       actionContext.setBmClassId(bmClassId);
/* 399 */       rs = getDynRMService().getRelatedObjectByClassValue(actionContext, bmClassId, attrId, cuid);
/*     */     } catch (Exception ex) {
/* 401 */       LogHome.getLog().error("", ex);
/* 402 */       throw new UserException(ex.getMessage());
/*     */     }
/* 404 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue getRelatedIdLabelByAttrValue(HttpServletRequest request, String bmClassId, String attrId, String relatedCuid) throws UserException {
/* 408 */     IDrmLabelValue rs = null;
/*     */     try {
/* 410 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 411 */       actionContext.setBmClassId(bmClassId);
/* 412 */       rs = getDynRMService().getRelatedIdLabelByAttrValue(actionContext, bmClassId, attrId, relatedCuid);
/*     */     } catch (Exception ex) {
/* 414 */       LogHome.getLog().error("", ex);
/* 415 */       throw new UserException(ex.getMessage());
/*     */     }
/* 417 */     return rs;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getRelatedObjectByAttrValue(HttpServletRequest request, String bmClassId, String attrId, String relatedCuid) throws UserException
/*     */   {
/* 422 */     IDrmDataObject rs = null;
/*     */     try {
/* 424 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 425 */       actionContext.setBmClassId(bmClassId);
/* 426 */       rs = getDynRMService().getRelatedObjectByAttrValue(actionContext, bmClassId, attrId, relatedCuid);
/*     */     } catch (Exception ex) {
/* 428 */       LogHome.getLog().error("", ex);
/* 429 */       throw new UserException(ex.getMessage());
/*     */     }
/* 431 */     return rs;
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> getRelatedObjectByAttrValues(HttpServletRequest request, String bmClassId, String attrId, String[] relatedCuid) throws UserException {
/* 435 */     List dbos = new ArrayList();
/*     */     try {
/* 437 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 438 */       actionContext.setBmClassId(bmClassId);
/* 439 */       for (int i = 0; i < relatedCuid.length; i++) {
/* 440 */         IDrmDataObject dbo = getDynRMService().getRelatedObjectByAttrValue(actionContext, bmClassId, attrId, relatedCuid[i]);
/* 441 */         dbos.add(dbo);
/*     */       }
/*     */     } catch (Exception ex) {
/* 444 */       LogHome.getLog().error("", ex);
/* 445 */       throw new UserException(ex.getMessage());
/*     */     }
/* 447 */     return dbos;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<Integer, Integer>> getEnumCount(HttpServletRequest request, String bmClassId, List<String> attrIds)
/*     */     throws UserException
/*     */   {
/* 453 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 454 */     actionContext.setBmClassId(bmClassId);
/* 455 */     Map enumAttrIdCount = new HashMap();
/*     */     try {
/* 457 */       enumAttrIdCount = getDynResManageService().getEnumCount(actionContext, bmClassId, attrIds);
/*     */     } catch (Exception ex) {
/* 459 */       LogHome.getLog().error("", ex);
/* 460 */       throw new UserException(ex.getMessage());
/*     */     }
/* 462 */     return enumAttrIdCount;
/*     */   }
/*     */ 
/*     */   private Map<String, List<ClassStatTreeNode>> getClassStatNodeList(HttpServletRequest request, IDrmQueryResultSet rs, IDrmQueryContext queryContext, List<String> attrIds, String bmClassId) {
/* 466 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 467 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 468 */     Map classStatAttrIdNodeListMap = new HashMap();
/* 469 */     List rows = rs.getResultSet();
/* 470 */     for (String attrId : attrIds) {
/* 471 */       List classStatNodeList = new ArrayList();
/* 472 */       BMAttrMeta attrMeta = getBMModelService().getAttrMeta(actionContext, bmClassId, attrId);
/* 473 */       if (attrMeta.getIsSystem().booleanValue()) {
/*     */         continue;
/*     */       }
/* 476 */       for (IDrmQueryRow row : rows) {
/* 477 */         IDrmDataObject ddo = row.getResultDbo(classMeta.getDbClassId());
/* 478 */         Object attrValue = ddo.getAttrValueT(attrId);
/* 479 */         ClassStatTreeNode classStatNode = new ClassStatTreeNode();
/* 480 */         if ((attrValue == null) || ("".equals(attrValue))) {
/* 481 */           classStatNode.setAttrValue("");
/* 482 */           classStatNode.setAttrName("");
/*     */         }
/* 484 */         else if (attrMeta.getIsEnumAttr().booleanValue()) {
/* 485 */           if (attrMeta.getIsMultiValue().booleanValue()) {
/* 486 */             String en = "";
/* 487 */             String ev = "";
/* 488 */             for (int k = 0; k < ((List)attrValue).size(); k++) {
/* 489 */               if (k != 0) {
/* 490 */                 en = en + ",";
/* 491 */                 ev = ev + ",";
/*     */               }
/* 493 */               DrmEnumValue dev = (DrmEnumValue)((List)attrValue).get(k);
/* 494 */               en = en + dev.getEnumName();
/* 495 */               ev = ev + dev.getEnumValue();
/*     */             }
/* 497 */             classStatNode.setAttrValue(ev);
/* 498 */             classStatNode.setAttrName(en);
/*     */           } else {
/* 500 */             DrmEnumValue drmEnumValue = (DrmEnumValue)attrValue;
/* 501 */             classStatNode.setAttrValue(drmEnumValue.getEnumValue());
/* 502 */             classStatNode.setAttrName(drmEnumValue.getEnumName());
/*     */           }
/* 504 */         } else if (attrMeta.getIsRelation().booleanValue()) {
/* 505 */           if ((attrValue instanceof DrmRelatedIdValue)) {
/* 506 */             DrmRelatedIdValue relatedIdValue = (DrmRelatedIdValue)attrValue;
/* 507 */             classStatNode.setAttrValue(relatedIdValue.getValue());
/* 508 */             classStatNode.setAttrName(relatedIdValue.getLabel());
/*     */           } else {
/* 510 */             classStatNode.setAttrValue(attrValue);
/* 511 */             classStatNode.setAttrName(attrValue.toString());
/*     */           }
/*     */         } else {
/* 514 */           Class attrDbType = attrMeta.getAttrClassType();
/* 515 */           if ((attrDbType == Timestamp.class) || (attrDbType == java.sql.Date.class)) {
/* 516 */             String strAttrValue = String.valueOf(attrValue);
/* 517 */             if (strAttrValue.indexOf("/") > 0)
/* 518 */               strAttrValue = strAttrValue.replaceAll("/", "-");
/*     */             try
/*     */             {
/* 521 */               java.util.Date d = TimeFormatHelper.convertDate(strAttrValue, "yyyy-MM-dd HH:mm:ss");
/* 522 */               strAttrValue = TimeFormatHelper.getFormatDate(d, "yyyy-MM-dd HH:mm:ss");
/*     */             } catch (Exception ex) {
/* 524 */               attrMeta.setDefaultValue(attrValue.toString());
/*     */             }
/* 526 */             classStatNode.setAttrValue(attrValue);
/* 527 */             classStatNode.setAttrName(strAttrValue);
/* 528 */           } else if (attrDbType == Boolean.TYPE) {
/* 529 */             if ("true".equals(attrValue.toString())) {
/* 530 */               classStatNode.setAttrValue(Integer.valueOf(1));
/* 531 */               classStatNode.setAttrName("是");
/*     */             } else {
/* 533 */               classStatNode.setAttrValue(Integer.valueOf(0));
/* 534 */               classStatNode.setAttrName("否");
/*     */             }
/*     */           }
/*     */           else {
/* 538 */             classStatNode.setAttrValue(attrValue);
/* 539 */             classStatNode.setAttrName(attrValue.toString());
/*     */           }
/*     */         }
/*     */ 
/* 543 */         classStatNodeList.add(classStatNode);
/*     */       }
/* 545 */       classStatAttrIdNodeListMap.put(attrId, classStatNodeList);
/*     */     }
/* 547 */     List classStatCountNodeList = new ArrayList();
/* 548 */     for (IDrmQueryRow row : rows) {
/* 549 */       IDrmDataObject ddo = row.getResultDbo(classMeta.getDbClassId());
/* 550 */       Object attrValue = ddo.getAttrValueT("count");
/* 551 */       ClassStatTreeNode classStatNode = new ClassStatTreeNode();
/* 552 */       classStatNode.setAttrValue(attrValue);
/* 553 */       classStatCountNodeList.add(classStatNode);
/*     */     }
/* 555 */     classStatAttrIdNodeListMap.put("count", classStatCountNodeList);
/* 556 */     return classStatAttrIdNodeListMap;
/*     */   }
/*     */ 
/*     */   private IDrmQueryResultSet getClassStatResultSet(HttpServletRequest request, IDrmQueryContext queryContext, List<String> attrIds, String bmClassId) throws UserException {
/* 560 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 561 */     actionContext.setBmClassId(bmClassId);
/* 562 */     IDrmQueryResultSet classStatResultSet = null;
/*     */     try {
/* 564 */       classStatResultSet = getDynResManageService().getClassStatResultSet(actionContext, queryContext, bmClassId, attrIds);
/*     */     } catch (Exception ex) {
/* 566 */       LogHome.getLog().error("", ex);
/* 567 */       throw new UserException(ex.getMessage());
/*     */     }
/* 569 */     return classStatResultSet;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getClassStatTree(HttpServletRequest request, IDrmQueryContext queryContext, List<String> attrIds, String bmClassId)
/*     */   {
/* 574 */     IDrmQueryResultSet rs = getClassStatResultSet(request, queryContext, attrIds, bmClassId);
/* 575 */     if (rs == null) return null;
/* 576 */     Map classStatNodeListMap = getClassStatNodeList(request, rs, queryContext, attrIds, bmClassId);
/* 577 */     ClassStatTreeNode rootNode = new ClassStatTreeNode();
/* 578 */     rootNode.setIsRootNode(true);
/* 579 */     List nodeList = new ArrayList();
/* 580 */     for (String attrId : attrIds) {
/* 581 */       List classStatNodeList = (List)classStatNodeListMap.get(attrId);
/* 582 */       ClassStatTreeNode classStatNode = (ClassStatTreeNode)classStatNodeList.get(0);
/* 583 */       classStatNode.setAttrId(attrId);
/* 584 */       nodeList.add(classStatNode);
/*     */     }
/* 586 */     rootNode.addChildNode((ClassStatTreeNode)nodeList.get(0));
/* 587 */     for (int i = 1; i < nodeList.size(); i++) {
/* 588 */       ((ClassStatTreeNode)nodeList.get(i - 1)).addChildNode((ClassStatTreeNode)nodeList.get(i));
/*     */     }
/* 590 */     ClassStatTreeNode firstCountNode = (ClassStatTreeNode)((List)classStatNodeListMap.get("count")).get(0);
/* 591 */     firstCountNode.setIsLeafNode(true);
/* 592 */     ((ClassStatTreeNode)nodeList.get(nodeList.size() - 1)).addChildNode(firstCountNode);
/* 593 */     int flag = 0; int notFirstChildFlag = 1;
/* 594 */     for (int row = 1; row < ((List)classStatNodeListMap.get("count")).size(); row++) {
/* 595 */       for (int col = 0; col < attrIds.size(); col++) {
/* 596 */         List classStatNodeList = (List)classStatNodeListMap.get(attrIds.get(col));
/* 597 */         ClassStatTreeNode classStatNode = (ClassStatTreeNode)classStatNodeList.get(row);
/* 598 */         Object attrValue = classStatNode.getAttrValue();
/* 599 */         if (((attrValue == null) && ("".equals(String.valueOf(((ClassStatTreeNode)nodeList.get(col)).getAttrValue()))) && (flag == 0)) || ((String.valueOf(attrValue).equals(String.valueOf(((ClassStatTreeNode)nodeList.get(col)).getAttrValue()))) && (flag == 0))) {
/* 600 */           ((ClassStatTreeNode)nodeList.get(col)).addColspan();
/*     */         } else {
/* 602 */           classStatNode.setAttrId((String)attrIds.get(col));
/* 603 */           if (notFirstChildFlag == 1) {
/* 604 */             classStatNode.setNotFirstChildNode(true);
/*     */           }
/* 606 */           notFirstChildFlag = 0;
/* 607 */           if (col == 0)
/* 608 */             rootNode.addChildNode(classStatNode);
/*     */           else {
/* 610 */             ((ClassStatTreeNode)nodeList.get(col - 1)).addChildNode(classStatNode);
/*     */           }
/* 612 */           nodeList.set(col, classStatNode);
/* 613 */           flag = 1;
/*     */         }
/*     */       }
/*     */ 
/* 617 */       ClassStatTreeNode figureNode = (ClassStatTreeNode)((List)classStatNodeListMap.get("count")).get(row);
/* 618 */       figureNode.setIsLeafNode(true);
/* 619 */       ((ClassStatTreeNode)nodeList.get(nodeList.size() - 1)).addChildNode(figureNode);
/* 620 */       flag = 0;
/* 621 */       notFirstChildFlag = 1;
/*     */     }
/* 623 */     Map classStatCountNodeMap = new HashMap();
/* 624 */     classStatCountNodeMap.put("totalCount", Integer.valueOf(rs.getCountValue()));
/* 625 */     classStatCountNodeMap.put("count", Integer.valueOf(((List)classStatNodeListMap.get("count")).size()));
/* 626 */     classStatCountNodeMap.put("rootNode", rootNode);
/* 627 */     return classStatCountNodeMap;
/*     */   }
/*     */ 
/*     */   public String[] getImportExcel(HttpServletRequest request, String bmClassId, String impTempCuid, List<String> attrIds, DrmSingleClassQuery query) throws Exception {
/* 631 */     DrmExcelImportValidListener listener = new DrmExcelImportValidListener(request, 0L);
/* 632 */     listener.start();
/*     */ 
/* 634 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 635 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 636 */     BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/* 637 */     query.setQueryAttrIds(attrIds);
/* 638 */     queryContext.setPopulate(true);
/* 639 */     queryContext.setEntity(true);
/* 640 */     long bt = System.currentTimeMillis();
/* 641 */     IDrmQueryResultSet rs = getDynObjBySql(request, queryContext, query);
/* 642 */     LogHome.getLog().info("根据导入模板[查询]数据总计：" + (System.currentTimeMillis() - bt) + "毫秒");
/*     */ 
/* 644 */     String exportPath = request.getSession().getServletContext().getRealPath("/") + "export";
/* 645 */     ExcelHelper.createDir(exportPath);
/* 646 */     String fileName = bmClassId + "_" + TimeFormatHelper.getFormatDate(new java.util.Date(), "yyyyMMddHHmmss") + ".xls";
/* 647 */     String filePath = exportPath + File.separator + fileName;
/*     */ 
/* 649 */     String _fileName = fileName;
/* 650 */     String fileURL = request.getContextPath() + "//" + "export" + "//" + _fileName;
/*     */ 
/* 652 */     WritableWorkbook workbook = ExcelHelper.creatWritableWorkBook(filePath);
/*     */ 
/* 654 */     ExcelImpTemplateMeta tempMeta = getVMModelService().getExcelImpTemplate(actionContext, bmClassId, impTempCuid);
/* 655 */     tempMeta.synAllImpAttrList();
/* 656 */     ExcelHelper.addTemplateHeaderMatrixClass(actionContext, workbook, bmClassMeta, tempMeta);
/* 657 */     if (rs != null) {
/* 658 */       int size = rs.getResultSet().size();
/* 659 */       listener.setTotal(size * 2);
/*     */     }
/* 661 */     bt = System.currentTimeMillis();
/* 662 */     List attrList = tempMeta.getExcelImpAttrList();
/* 663 */     if (rs != null) {
/* 664 */       repairRelationDbos(request, rs, bmClassMeta, tempMeta, listener);
/* 665 */       LogHome.getLog().info("根据导入模板[修复]数据总计：" + (System.currentTimeMillis() - bt) + "毫秒");
/* 666 */       bt = System.currentTimeMillis();
/* 667 */       ExcelHelper.addExcelData(workbook.getSheet(0), rs, bmClassMeta, attrList, listener);
/* 668 */       LogHome.getLog().info("根据导入模板[导出]数据总计：" + (System.currentTimeMillis() - bt) + "毫秒");
/*     */     }
/* 670 */     ExcelHelper.closeWritableWorkbook(workbook);
/* 671 */     listener.done();
/* 672 */     return new String[] { fileName, fileURL };
/*     */   }
/*     */ 
/*     */   private void repairRelationDbos(HttpServletRequest request, IDrmQueryResultSet rs, BMClassMeta bm, ExcelImpTemplateMeta tempMeta, DrmExcelImportValidListener listener) {
/* 676 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 677 */     List relationAttrs = new ArrayList();
/*     */ 
/* 682 */     Map allAttrs = bm.getAllAttrMetas();
/* 683 */     for (Map.Entry entry : allAttrs.entrySet()) {
/* 684 */       BMAttrMeta attrMeta = (BMAttrMeta)entry.getValue();
/* 685 */       if (attrMeta.getIsRelation().booleanValue()) {
/* 686 */         relationAttrs.add(attrMeta);
/*     */       }
/*     */     }
/* 689 */     List dbos = rs.getResultSet();
/* 690 */     for (IDrmQueryRow row : dbos) {
/* 691 */       IDrmDataObject dbo = row.getResultDbo(bm.getDbClassId());
/* 692 */       Map map = new HashMap();
/* 693 */       List useAttr = new ArrayList();
/* 694 */       for (BMAttrMeta attrMeta : relationAttrs) {
/* 695 */         Object o = dbo.getAttrValue(attrMeta.getAttrId());
/* 696 */         if (o != null) {
/* 697 */           if ((o instanceof IDrmLabelValue)) {
/* 698 */             IDrmLabelValue lv = (IDrmLabelValue)o;
/* 699 */             String cuid = lv.getValue() == null ? "" : String.valueOf(lv.getValue());
/* 700 */             Map r = attrMeta.getRelatedAttrIds();
/* 701 */             boolean isExist = false;
/* 702 */             for (Map.Entry entry : r.entrySet()) {
/* 703 */               if (map.containsKey(entry.getKey())) {
/* 704 */                 isExist = true;
/* 705 */                 break;
/*     */               }
/*     */             }
/* 708 */             if (!isExist) {
/* 709 */               ExcelHelper.getRelatedClassValueList(attrMeta.getAttrId(), attrMeta, cuid, tempMeta.getExcelImpRelateds(), map);
/* 710 */               useAttr.add(attrMeta);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 743 */       for (Map.Entry entry : map.entrySet()) {
/* 744 */         String key = (String)entry.getKey();
/* 745 */         if ((key != null) && (key.trim().length() != 0) && (key.indexOf(ExcelHelper.KEY_FLAG) >= 0)) {
/* 746 */           String orgAttrId = key.split(ExcelHelper.KEY_FLAG)[0];
/* 747 */           classId = key.split(ExcelHelper.KEY_FLAG)[1];
/* 748 */           BMClassMeta _bm = getBMModelService().getClassMeta(actionContext, classId);
/* 749 */           Map m = (Map)entry.getValue();
/* 750 */           if (m != null) {
/* 751 */             Object o = dbo.getAttrValue(orgAttrId);
/* 752 */             if (o == null)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 760 */             if (m.size() > 1) {
/* 761 */               m.remove(_bm.getCuidAttrId());
/*     */             }
/* 763 */             sRootAttrId = (String)bm.getAllRootAttrMap().get(orgAttrId);
/* 764 */             if (sRootAttrId == null) sRootAttrId = orgAttrId;
/* 765 */             for (Map.Entry e : m.entrySet()) {
/* 766 */               String attrId = (String)e.getKey();
/* 767 */               dbo.setAttrValue(ExcelHelper.RELATED_COL_FLAG + sRootAttrId + ExcelHelper.KEY_FLAG + classId + "-" + attrId, e.getValue());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       String classId;
/*     */       String sRootAttrId;
/* 770 */       listener.update(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getExportExcel(HttpServletRequest request, String bmClassId, List<String> attrIds, DrmSingleClassQuery query) throws Exception {
/* 775 */     DrmExcelImportValidListener listener = new DrmExcelImportValidListener(request, 0L);
/* 776 */     listener.start();
/*     */ 
/* 778 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 779 */     BMClassMeta bmClassMeta = getBMModelService().getClassMeta(ServiceHelper.getUserSvActCxt(request), bmClassId);
/* 780 */     String exportPath = request.getSession().getServletContext().getRealPath("/") + "export";
/* 781 */     ExcelHelper.createDir(exportPath);
/* 782 */     String fileName = bmClassId + "_" + TimeFormatHelper.getFormatDate(new java.util.Date(), "yyyyMMddHHmmss") + ".xls";
/* 783 */     String filePath = exportPath + File.separator + fileName;
/*     */ 
/* 785 */     String _fileName = fileName;
/* 786 */     String fileURL = request.getContextPath() + "//" + "export" + "//" + _fileName;
/*     */ 
/* 788 */     WritableWorkbook workbook = ExcelHelper.creatWritableWorkBook(filePath);
/*     */ 
/* 790 */     WritableSheet sheet = workbook.createSheet(bmClassMeta.getBmClassId(), 0);
/* 791 */     ExcelHelper.addTemplateHeaderMatrixClass(sheet, bmClassMeta, attrIds);
/*     */ 
/* 793 */     query.setQueryAttrIds(attrIds);
/* 794 */     queryContext.setPopulate(true);
/* 795 */     queryContext.setEntity(true);
/* 796 */     IDrmQueryResultSet rs = getDynObjBySql(request, queryContext, query);
/* 797 */     if (rs != null) {
/* 798 */       int size = rs.getResultSet().size();
/* 799 */       listener.setTotal(size * 2);
/* 800 */       ExcelHelper.addExcelData(workbook.getSheet(0), rs, bmClassMeta, attrIds, listener);
/*     */     }
/* 802 */     ExcelHelper.closeWritableWorkbook(workbook);
/* 803 */     listener.done();
/* 804 */     return new String[] { fileName, fileURL };
/*     */   }
/*     */ 
/*     */   public List<IDrmLabelValue> getDbEnums(HttpServletRequest request, String dbClassId) {
/* 808 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 809 */     return getDynResManageService().getDbEnums(actionContext, dbClassId);
/*     */   }
/*     */ 
/*     */   public Map getObjectById(HttpServletRequest request, String dbClassId, String id, boolean isCuid) throws UserException {
/* 813 */     GenericDO dbo = new GenericDO(dbClassId);
/*     */     try {
/* 815 */       dbo = dbo.createInstanceByClassName();
/* 816 */       if (isCuid) {
/* 817 */         dbo.setCuid(id);
/* 818 */         dbo = getDynRMService().getServiceDAO().getObjByCuid(dbo);
/*     */       } else {
/* 820 */         dbo.setObjectId(id);
/* 821 */         dbo = getDynRMService().getServiceDAO().getObject(dbo);
/*     */       }
/*     */     } catch (Exception ex) {
/* 824 */       LogHome.getLog().error("", ex);
/* 825 */       dbo = null;
/*     */     }
/* 827 */     return dbo == null ? null : dbo.getAllAttr();
/*     */   }
/*     */ 
/*     */   public Map addDbObject(HttpServletRequest request, String dbClassId, Map attrValues) {
/* 831 */     GenericDO dbo = new GenericDO(dbClassId);
/*     */     try {
/* 833 */       dbo = dbo.createInstanceByClassName();
/* 834 */       attrValues = Dwr2DboHelper.dwr2dboMap(dbo, attrValues);
/* 835 */       dbo.setAttrValues(attrValues);
/* 836 */       getDynRMService().getServiceDAO().createObject(new BoActionContext(), dbo);
/*     */     } catch (Exception ex) {
/* 838 */       LogHome.getLog().error("", ex);
/* 839 */       dbo = null;
/*     */     }
/* 841 */     return dbo == null ? null : dbo.getAllAttr();
/*     */   }
/*     */ 
/*     */   public Map modifyDbObject(HttpServletRequest request, String dbClassId, String cuid, Map attrValues)
/*     */   {
/* 846 */     GenericDO dbo = new GenericDO(dbClassId);
/*     */     try {
/* 848 */       dbo = dbo.createInstanceByClassName();
/* 849 */       attrValues = Dwr2DboHelper.dwr2dboMap(dbo, attrValues);
/* 850 */       dbo.setCuid(cuid);
/* 851 */       dbo = getDynRMService().getServiceDAO().getObjByCuid(dbo);
/* 852 */       dbo.setAttrValues(attrValues);
/* 853 */       getDynRMService().getServiceDAO().updateObject(new BoActionContext(), dbo);
/*     */     } catch (Exception ex) {
/* 855 */       LogHome.getLog().error("", ex);
/* 856 */       dbo = null;
/*     */     }
/* 858 */     return dbo == null ? null : dbo.getAllAttr();
/*     */   }
/*     */ 
/*     */   public static IDynResManageService getDynResManageService() {
/* 862 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IDynResManageService getDynRMService() {
/* 866 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 870 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 874 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DynResManageAction
 * JD-Core Version:    0.6.0
 */