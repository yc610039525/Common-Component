/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmRelatedIdValue;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExtAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
/*     */ import com.boco.raptor.drm.core.meta.QueryTemplateMeta;
/*     */ import com.boco.raptor.drm.core.meta.TemplateMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.vm.ExtAttrNameEnum.QueryTable;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.transnms.common.dto.DrmAttrGroup;
/*     */ import com.boco.transnms.common.dto.DrmGroupAttr;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DynQueryGridAction
/*     */ {
/*     */   public static final String ATTR_RELATION = "RELATION";
/*     */   public static final String ATTR_DEFAULT_VALUE = "DEFAULT_VALUE";
/*  61 */   private IBMModelService bmModelService = getBMModelService();
/*  62 */   private IVMModelService vmModelService = getVMModelService();
/*  63 */   private IDynResManageService dynResManageService = getDynResManageService();
/*     */ 
/*     */   public IDrmQueryRow getDrmAttrGroup(HttpServletRequest request, String bmClassId)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/*  70 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  71 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*     */ 
/*  73 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  74 */       query.setBmClassId("DRM_ATTR_GROUP");
/*  75 */       query.addQueryAttrId("CUID");
/*  76 */       query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", actionContext.getUserId()));
/*  77 */       query.addQueryCondExps(new DrmQueryAttrCond("GROUP_TYPE", "=", "1"));
/*  78 */       query.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "=", bmClassId));
/*  79 */       DrmAttrGroup dbo = new DrmAttrGroup();
/*  80 */       IDrmQueryResultSet rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query, dbo);
/*  81 */       List rows = rs.getResultSet();
/*     */ 
/*  83 */       if (rows.size() > 0) {
/*  84 */         return (IDrmQueryRow)rows.get(0);
/*     */       }
/*  86 */       return null;
/*     */     }
/*     */     catch (UserException ex) {
/*  89 */       LogHome.getLog().error("", ex);
/*  90 */     }throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> getDrmGroupAttrs(HttpServletRequest request, String relatedAttrGroupCuid) throws UserException
/*     */   {
/*     */     try
/*     */     {
/*  97 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  98 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*     */ 
/* 100 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 101 */       query.setBmClassId("DRM_GROUP_ATTR");
/* 102 */       query.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", relatedAttrGroupCuid));
/* 103 */       queryContext.setOrderField("SORT_NO");
/* 104 */       DrmGroupAttr dbo = new DrmGroupAttr();
/* 105 */       IDrmQueryResultSet rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query, dbo);
/* 106 */       List rows = rs.getResultSet();
/* 107 */       List idos = new ArrayList();
/* 108 */       for (IDrmQueryRow row : rows) {
/* 109 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/* 110 */         idos.add(dbobj);
/*     */       }
/* 112 */       return idos;
/*     */     } catch (UserException ex) {
/* 114 */       LogHome.getLog().error("", ex);
/* 115 */     }throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public String[] getDrmGroupAttrIds(HttpServletRequest request, String bmClassId) throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 122 */       IDrmQueryRow drmAttrGroupRow = getDrmAttrGroup(request, bmClassId);
/* 123 */       if (drmAttrGroupRow != null) {
/* 124 */         IDrmDataObject dbo = drmAttrGroupRow.getResultDbo("DRM_ATTR_GROUP");
/* 125 */         String drmAttrGroupCuid = (String)dbo.getAttrValueT("CUID");
/* 126 */         List drmGroupAttrs = getDrmGroupAttrs(request, drmAttrGroupCuid);
/* 127 */         int i = 0;
/* 128 */         if (drmGroupAttrs.size() > 0) {
/* 129 */           String[] attrIds = new String[drmGroupAttrs.size()];
/* 130 */           for (IDrmDataObject drmGroupAttr : drmGroupAttrs) {
/* 131 */             String attrId = (String)drmGroupAttr.getAttrValueT("ATTR_ID");
/* 132 */             attrIds[(i++)] = attrId;
/*     */           }
/* 134 */           return attrIds;
/*     */         }
/*     */       }
/* 137 */       return null;
/*     */     } catch (UserException ex) {
/* 139 */       LogHome.getLog().error("", ex);
/* 140 */     }throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public void updateDrmGroupAttrs(HttpServletRequest request, String[] attrIds, String bmClassId)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 154 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 156 */       ExtAttrMetaGroup queryTableMeta = new ExtAttrMetaGroup();
/* 157 */       queryTableMeta.setBmClassId(bmClassId);
/* 158 */       queryTableMeta.setDbClassId(bmClassId);
/*     */ 
/* 160 */       for (int i = 0; i < attrIds.length; i++) {
/* 161 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, attrIds[i]);
/* 162 */         queryTableMeta.addExtAttrMeta(attrMeta);
/*     */       }
/* 164 */       this.vmModelService.setQueryTableMeta(actionContext, bmClassId, queryTableMeta);
/*     */     } catch (UserException ex) {
/* 166 */       LogHome.getLog().error("", ex);
/* 167 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<BMAttrMeta> getRelationQueryAttrs(HttpServletRequest request, String bmClassId, String cuid)
/*     */   {
/* 181 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 182 */     List attrMetas = new ArrayList();
/* 183 */     List attrMetaList = this.vmModelService.getPropertyMeta(actionContext, bmClassId).getExtAttrMetas();
/* 184 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 185 */     queryContext.setPopulate(true);
/* 186 */     IDrmDataObject ddo = DrmEntityFactory.getInstance().createDataObject();
/* 187 */     ddo.setBmClassId(bmClassId);
/* 188 */     ddo.setCuid(cuid);
/* 189 */     ddo = this.dynResManageService.getDynObject(actionContext, queryContext, ddo);
/* 190 */     BMClassMeta classMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 191 */     Map allAttrMetas = classMeta.getAllAttrMetas();
/* 192 */     for (int i = 0; i < attrMetaList.size(); i++) {
/* 193 */       String attrId = ((ExtAttrMeta)attrMetaList.get(i)).getAttrId();
/* 194 */       BMAttrMeta attrMeta = (BMAttrMeta)allAttrMetas.get(attrId);
/* 195 */       if (attrMeta.getIsSystem().booleanValue()) {
/*     */         continue;
/*     */       }
/* 198 */       Object attrValue = ddo.getAttrValueT(attrId);
/* 199 */       if (attrValue == null) {
/*     */         continue;
/*     */       }
/* 202 */       if ((attrMeta.getIsEnumAttr().booleanValue()) && (attrMeta.getIsMultiValue().booleanValue())) {
/* 203 */         String en = "";
/* 204 */         for (int k = 0; k < ((List)attrValue).size(); k++) {
/* 205 */           if (k != 0) {
/* 206 */             en = en + ",";
/*     */           }
/* 208 */           DrmEnumValue ev = (DrmEnumValue)((List)attrValue).get(k);
/* 209 */           en = en + ev.getEnumName();
/*     */         }
/* 211 */         attrMeta.setDefaultValue(en);
/* 212 */       } else if ((attrValue instanceof DrmEnumValue)) {
/* 213 */         DrmEnumValue drmEnumValue = (DrmEnumValue)attrValue;
/* 214 */         String enumName = drmEnumValue.getEnumName();
/* 215 */         attrMeta.setDefaultValue(enumName);
/* 216 */       } else if ((attrValue instanceof DrmRelatedIdValue)) {
/* 217 */         DrmRelatedIdValue relatedIdValue = (DrmRelatedIdValue)attrValue;
/* 218 */         String relatedValue = relatedIdValue.getLabel();
/* 219 */         attrMeta.setDefaultValue(relatedValue);
/*     */       } else {
/* 221 */         Class attrDbType = attrMeta.getAttrClassType();
/* 222 */         if (attrDbType == Object.class) {
/* 223 */           System.out.println(attrValue);
/*     */         }
/* 225 */         if ((attrDbType == Timestamp.class) || (attrDbType == java.sql.Date.class)) {
/* 226 */           String strAttrValue = String.valueOf(attrValue);
/* 227 */           if (strAttrValue.indexOf("/") > 0)
/* 228 */             strAttrValue = strAttrValue.replaceAll("/", "-");
/*     */           try
/*     */           {
/* 231 */             java.util.Date d = TimeFormatHelper.convertDate(strAttrValue, "yyyy-MM-dd HH:mm:ss");
/* 232 */             strAttrValue = TimeFormatHelper.getFormatDate(d, "yyyy-MM-dd HH:mm:ss");
/*     */           } catch (Exception ex) {
/* 234 */             attrMeta.setDefaultValue(attrValue.toString());
/*     */           }
/* 236 */           attrMeta.setDefaultValue(strAttrValue);
/*     */         }
/*     */         else {
/* 239 */           attrMeta.setDefaultValue(attrValue.toString());
/*     */         }
/*     */       }
/* 242 */       attrMetas.add(attrMeta);
/*     */     }
/* 244 */     return attrMetas;
/*     */   }
/*     */ 
/*     */   public void addQueryTemplate(HttpServletRequest request, String bmClassId, String templateName, DrmSingleClassQuery drmSingleClassQuery) throws UserException {
/*     */     try {
/* 249 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 251 */       QueryTemplateMeta queryTemplate = new QueryTemplateMeta();
/* 252 */       queryTemplate.setTemplateLabelCn(templateName);
/* 253 */       queryTemplate.setSqlCondTemplate(drmSingleClassQuery.getSqlCondTemplate());
/*     */ 
/* 255 */       ExtAttrMetaGroup queryAttrMeta = new ExtAttrMetaGroup();
/* 256 */       queryAttrMeta.setBmClassId(bmClassId);
/* 257 */       queryAttrMeta.setDbClassId(bmClassId);
/*     */ 
/* 259 */       List drmQueryAttrConds = drmSingleClassQuery.getQueryCondExps();
/* 260 */       for (DrmQueryAttrCond drmQueryAttrCond : drmQueryAttrConds) {
/* 261 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, drmQueryAttrCond.getAttrId());
/* 262 */         queryAttrMeta.addExtAttrMeta(attrMeta);
/* 263 */         attrMeta.setExtAttrMeta("RELATION", drmQueryAttrCond.getRelation());
/* 264 */         attrMeta.setExtAttrMeta("DEFAULT_VALUE", drmQueryAttrCond.getValue());
/*     */       }
/* 266 */       queryTemplate.setExtAttrMetaGroup(queryAttrMeta);
/* 267 */       this.vmModelService.addQueryTemplate(actionContext, bmClassId, queryTemplate);
/*     */     } catch (UserException ex) {
/* 269 */       LogHome.getLog().error("", ex);
/* 270 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteQueryTemplate(HttpServletRequest request, String bmClassId, String templateCuid) throws UserException
/*     */   {
/*     */     try {
/* 277 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 278 */       actionContext.setBmClassId(bmClassId);
/* 279 */       QueryTemplateMeta queryTemplate = new QueryTemplateMeta();
/* 280 */       queryTemplate.setTemplateCuid(templateCuid);
/* 281 */       this.vmModelService.deleteQueryTemplate(actionContext, bmClassId, queryTemplate);
/*     */     } catch (UserException ex) {
/* 283 */       LogHome.getLog().error("", ex);
/* 284 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveQueryTemplateSort(HttpServletRequest request, String bmClassId, TemplateMeta[] templates) throws UserException
/*     */   {
/*     */     try {
/* 291 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 292 */       Map queryTemplates = new HashMap();
/* 293 */       for (int i = 0; i < templates.length; i++) {
/* 294 */         queryTemplates.put(templates[i].getTemplateLabelCn(), templates[i]);
/*     */       }
/* 296 */       this.vmModelService.modifyQueryTemplatesSortNo(actionContext, bmClassId, queryTemplates);
/*     */     } catch (UserException ex) {
/* 298 */       LogHome.getLog().error("", ex);
/* 299 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyQueryTemplate(HttpServletRequest request, String bmClassId, String templateCuid, String templateName, DrmSingleClassQuery drmSingleClassQuery) throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 307 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 309 */       QueryTemplateMeta queryTemplate = new QueryTemplateMeta();
/* 310 */       queryTemplate.setTemplateCuid(templateCuid);
/* 311 */       queryTemplate.setTemplateLabelCn(templateName);
/* 312 */       queryTemplate.setSqlCondTemplate(drmSingleClassQuery.getSqlCondTemplate());
/*     */ 
/* 314 */       ExtAttrMetaGroup queryAttrMeta = new ExtAttrMetaGroup();
/* 315 */       queryAttrMeta.setBmClassId(bmClassId);
/* 316 */       queryAttrMeta.setDbClassId(bmClassId);
/*     */ 
/* 318 */       List drmQueryAttrConds = drmSingleClassQuery.getQueryCondExps();
/* 319 */       for (DrmQueryAttrCond drmQueryAttrCond : drmQueryAttrConds) {
/* 320 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, drmQueryAttrCond.getAttrId());
/* 321 */         queryAttrMeta.addExtAttrMeta(attrMeta);
/* 322 */         attrMeta.setExtAttrMeta("RELATION", drmQueryAttrCond.getRelation());
/* 323 */         attrMeta.setExtAttrMeta("DEFAULT_VALUE", drmQueryAttrCond.getValue());
/*     */       }
/* 325 */       queryTemplate.setExtAttrMetaGroup(queryAttrMeta);
/* 326 */       this.vmModelService.modifyQueryTemplate(actionContext, bmClassId, queryTemplate);
/*     */     } catch (UserException ex) {
/* 328 */       LogHome.getLog().error("", ex);
/* 329 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyQueryTableColWidth(HttpServletRequest request, String bmClassId, String[] attrIds, String[] width) throws UserException
/*     */   {
/*     */     try {
/* 336 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 338 */       ExtAttrMetaGroup queryTableMeta = new ExtAttrMetaGroup();
/* 339 */       queryTableMeta.setBmClassId(bmClassId);
/* 340 */       queryTableMeta.setDbClassId(bmClassId);
/*     */ 
/* 342 */       for (int i = 0; i < attrIds.length; i++) {
/* 343 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, attrIds[i]);
/* 344 */         attrMeta.setExtAttrMeta(ExtAttrNameEnum.QueryTable.COL_WIDTH, width[i]);
/* 345 */         queryTableMeta.addExtAttrMeta(attrMeta);
/*     */       }
/*     */ 
/* 348 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 349 */       ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, bmClassMeta.getCuidAttrId());
/* 350 */       queryTableMeta.addExtAttrMeta(attrMeta);
/*     */ 
/* 352 */       this.vmModelService.modifyQueryTableMeta(actionContext, bmClassId, queryTableMeta);
/*     */     } catch (UserException ex) {
/* 354 */       LogHome.getLog().error("", ex);
/* 355 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyQueryTableColMove(HttpServletRequest request, String bmClassId, String[] attrIds) throws UserException
/*     */   {
/*     */     try {
/* 362 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 364 */       ExtAttrMetaGroup queryTableMeta = new ExtAttrMetaGroup();
/* 365 */       queryTableMeta.setBmClassId(bmClassId);
/* 366 */       queryTableMeta.setDbClassId(bmClassId);
/*     */ 
/* 368 */       for (int i = 0; i < attrIds.length; i++) {
/* 369 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, attrIds[i]);
/* 370 */         queryTableMeta.addExtAttrMeta(attrMeta);
/*     */       }
/*     */ 
/* 373 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 374 */       ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, bmClassMeta.getCuidAttrId());
/* 375 */       queryTableMeta.addExtAttrMeta(attrMeta);
/*     */ 
/* 377 */       this.vmModelService.modifyQueryTableMeta(actionContext, bmClassId, queryTableMeta);
/*     */     } catch (UserException ex) {
/* 379 */       LogHome.getLog().error("", ex);
/* 380 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<String, String> deleteDynObjects(HttpServletRequest request, String bmClassId, Long[] dboIds, String[] cuids) throws UserException
/*     */   {
/* 386 */     if (dboIds.length != cuids.length) {
/* 387 */       throw new UserException("参数错误，objectId和cuid参数必须都完整!");
/*     */     }
/* 389 */     Map returnList = new LinkedHashMap();
/*     */     try {
/* 391 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 392 */       actionContext.setBmClassId(bmClassId);
/* 393 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 394 */       List idos = new ArrayList();
/*     */ 
/* 408 */       for (int i = 0; i < dboIds.length; i++) {
/* 409 */         long objectId = dboIds[i].longValue();
/* 410 */         String cuid = cuids[i];
/* 411 */         IDrmDataObject dbobj = DrmEntityFactory.getInstance().createDataObject();
/* 412 */         dbobj.setBmClassId(bmClassId);
/* 413 */         dbobj.setDbClassId(bmClassMeta.getDbClassId());
/* 414 */         dbobj.setDboId(Long.valueOf(objectId));
/* 415 */         dbobj.setCuid(cuid);
/* 416 */         idos.add(dbobj);
/*     */       }
/* 418 */       if ((idos != null) && (idos.size() > 0))
/* 419 */         returnList = this.dynResManageService.deleteDynObjects(actionContext, idos, true);
/*     */     }
/*     */     catch (UserException ex) {
/* 422 */       LogHome.getLog().error("", ex);
/* 423 */       throw new UserException(ex.getMessage());
/*     */     }
/* 425 */     return returnList;
/*     */   }
/*     */ 
/*     */   public Map<String, String> deleteDynObjectsByCond(HttpServletRequest request, String bmClassId, DrmSingleClassQuery query) throws UserException {
/* 429 */     Map returnList = new LinkedHashMap();
/*     */     try {
/* 431 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 432 */       actionContext.setBmClassId(bmClassId);
/* 433 */       returnList = this.dynResManageService.deleteDynObjectsByCond(actionContext, bmClassId, query, true);
/*     */     } catch (UserException ex) {
/* 435 */       LogHome.getLog().error("", ex);
/* 436 */       throw new UserException(ex.getMessage());
/*     */     }
/* 438 */     return returnList;
/*     */   }
/*     */ 
/*     */   private IDynResManageService getDynResManageService() {
/* 442 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 446 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 450 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DynQueryGridAction
 * JD-Core Version:    0.6.0
 */