/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmLabelValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExtAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.vm.ExtAttrNameEnum.Property;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.transnms.common.dto.DrmGroupAttr;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ public class DynPropertyAction
/*     */ {
/*     */   public static final String CUID = "CUID";
/*     */   public static final String SYS_USER = "SYS_USER";
/*  56 */   private IBMModelService bmModelService = getBMModelService();
/*  57 */   private IVMModelService vmModelService = getVMModelService();
/*  58 */   private IDynResManageService dynResManageService = getDynResManageService();
/*     */ 
/*     */   public IDrmDataObject addDynObject(HttpServletRequest request, IDrmDataObject dro) throws UserException
/*     */   {
/*     */     try
/*     */     {
/*  64 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  65 */       actionContext.setBmClassId(dro.getBmClassId());
/*  66 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, dro.getBmClassId());
/*  67 */       dro = bmClassMeta.convertDrmDataObject(dro, true);
/*  68 */       dro = this.dynResManageService.addDynObject(actionContext, dro, true);
/*  69 */       IDrmLabelValue lv = this.dynResManageService.getRelatedIdLabelByAttrValue(actionContext, dro.getBmClassId(), "CUID", dro.getCuid());
/*  70 */       dro.setAttrValue("DISPLAY_LABEL", lv.getLabel());
/*  71 */       return dro; } catch (Exception ex) {
/*     */     }
/*  73 */     throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public IDrmDataObject modifyDynObject(HttpServletRequest request, IDrmDataObject dro, Map<String, String> attrLabels) throws UserException
/*     */   {
/*     */     try {
/*  79 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  80 */       actionContext.setBmClassId(dro.getBmClassId());
/*  81 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, dro.getBmClassId());
/*  82 */       dro = bmClassMeta.convertDrmDataObject(dro, false);
/*  83 */       dro.setDboId(dro.getDboId());
/*  84 */       String cuid = dro.getCuid();
/*  85 */       if (this.dynResManageService.getIsLog(ServiceHelper.createSvActCxt())) {
/*  86 */         IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  87 */         queryContext.setPopulate(true);
/*  88 */         queryContext.setEntity(true);
/*  89 */         DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  90 */         query.setBmClassId(dro.getBmClassId());
/*  91 */         query.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", dro.getCuid()));
/*  92 */         IDrmQueryResultSet rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query);
/*  93 */         List rows = rs.getResultSet();
/*     */ 
/*  95 */         Map modifyAttrs = dro.getAllAttr();
/*  96 */         modifyAttrs.remove(bmClassMeta.getCuidAttrId());
/*  97 */         List maintenanceLogs = createMaintenanceLog(request, dro.getBmClassId(), rows, modifyAttrs, attrLabels);
/*  98 */         this.dynResManageService.modifyDynObject(actionContext, dro, maintenanceLogs, true);
/*     */       } else {
/* 100 */         this.dynResManageService.modifyDynObject(actionContext, dro, true);
/*     */       }
/* 102 */       IDrmLabelValue lv = this.dynResManageService.getRelatedIdLabelByAttrValue(actionContext, dro.getBmClassId(), "CUID", cuid);
/* 103 */       dro.setAttrValue("DISPLAY_LABEL", lv.getLabel());
/* 104 */       return dro; } catch (Exception ex) {
/*     */     }
/* 106 */     throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public void modifyDynObjects(HttpServletRequest request, String bmClassId, List<IDrmDataObject> dros, Map<String, String> attrLabels) throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 113 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 114 */       actionContext.setBmClassId(bmClassId);
/* 115 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 116 */       StringBuffer cuids = new StringBuffer();
/* 117 */       for (int i = 0; i < dros.size(); i++) {
/* 118 */         IDrmDataObject drmDataObject = (IDrmDataObject)dros.get(i);
/* 119 */         cuids.append("'" + drmDataObject.getCuid() + "',");
/*     */       }
/* 121 */       Map modifyAttrs = ((IDrmDataObject)dros.get(0)).getAllAttr();
/* 122 */       modifyAttrs.remove(bmClassMeta.getCuidAttrId());
/* 123 */       for (int i = 0; i < dros.size(); i++) {
/* 124 */         IDrmDataObject drmDataObject = (IDrmDataObject)dros.get(i);
/* 125 */         drmDataObject = bmClassMeta.convertDrmDataObject(drmDataObject, false);
/*     */       }
/*     */ 
/* 128 */       if (this.dynResManageService.getIsLog(ServiceHelper.createSvActCxt())) {
/* 129 */         IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 130 */         queryContext.setPopulate(true);
/* 131 */         queryContext.setEntity(true);
/* 132 */         DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 133 */         query.setBmClassId(bmClassId);
/*     */ 
/* 135 */         query.addQueryCondExps(new DrmQueryAttrCond("CUID", "in", cuids.substring(0, cuids.length() - 1).toString()));
/* 136 */         IDrmQueryResultSet rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query);
/* 137 */         List rows = rs.getResultSet();
/* 138 */         List maintenanceLogs = createMaintenanceLog(request, bmClassId, rows, modifyAttrs, attrLabels);
/* 139 */         this.dynResManageService.modifyDynObjects(actionContext, dros, modifyAttrs, maintenanceLogs, true);
/*     */       } else {
/* 141 */         this.dynResManageService.modifyDynObjects(actionContext, dros, modifyAttrs, true);
/*     */       }
/*     */     } catch (Exception ex) {
/* 144 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyDynObjectsByCond(HttpServletRequest request, String bmClassId, DrmSingleClassQuery query, Map<String, String> attrValues, Map<String, String> attrLabels) throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 152 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 153 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 154 */       actionContext.setBmClassId(query.getBmClassId());
/* 155 */       BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/* 156 */       List attrIds = new ArrayList();
/* 157 */       query.setQueryAttrIds(attrIds);
/* 158 */       IDrmQueryResultSet rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query);
/* 159 */       List rows = rs.getResultSet();
/* 160 */       List idos = new ArrayList();
/* 161 */       for (IDrmQueryRow row : rows) {
/* 162 */         IDrmDataObject newData = DrmEntityFactory.getInstance().createDataObject();
/* 163 */         IDrmDataObject dbobj = row.getResultDbo(bmClassMeta.getDbClassId());
/* 164 */         attrValues.put(bmClassMeta.getCuidAttrId(), dbobj.getCuid());
/* 165 */         newData.setBmClassId(bmClassId);
/* 166 */         newData.setDboId(dbobj.getDboId());
/* 167 */         Iterator iterator = attrValues.keySet().iterator();
/* 168 */         while (iterator.hasNext()) {
/* 169 */           String sAttrId = (String)iterator.next();
/* 170 */           newData.setAttrValue(sAttrId, attrValues.get(sAttrId));
/*     */         }
/* 172 */         newData = bmClassMeta.convertDrmDataObject(newData, false);
/* 173 */         idos.add(newData);
/*     */       }
/* 175 */       Map modifyAttrs = ((IDrmDataObject)idos.get(0)).getAllAttr();
/* 176 */       modifyAttrs.remove(bmClassMeta.getCuidAttrId());
/* 177 */       if (this.dynResManageService.getIsLog(ServiceHelper.createSvActCxt())) {
/* 178 */         queryContext.setPopulate(true);
/* 179 */         queryContext.setEntity(true);
/* 180 */         rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query);
/* 181 */         List maintenanceLogs = createMaintenanceLog(request, bmClassId, rs.getResultSet(), modifyAttrs, attrLabels);
/* 182 */         this.dynResManageService.modifyDynObjects(actionContext, idos, modifyAttrs, maintenanceLogs, true);
/*     */       } else {
/* 184 */         this.dynResManageService.modifyDynObjects(actionContext, idos, modifyAttrs, true);
/*     */       }
/*     */     } catch (Exception ex) {
/* 187 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> createMaintenanceLog(HttpServletRequest request, String bmClassId, List<IDrmQueryRow> rows, Map<String, Object> modifyAttrs, Map<String, String> attrLabels)
/*     */   {
/* 194 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 195 */     actionContext.setBmClassId(bmClassId);
/* 196 */     BMClassMeta bmClassMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/*     */ 
/* 198 */     IDrmDataObject sysUser = getSysUser(request);
/*     */ 
/* 200 */     String relatedDistrictCuid = "";
/* 201 */     if (bmClassMeta.getAllAttrIds().contains("RELATED_DISTRICT_CUID"))
/* 202 */       relatedDistrictCuid = "RELATED_DISTRICT_CUID";
/* 203 */     else if (bmClassMeta.getAllAttrIds().contains("RELATED_SPACE_CUID")) {
/* 204 */       relatedDistrictCuid = "RELATED_SPACE_CUID";
/*     */     }
/*     */ 
/* 207 */     List maintenanceLogs = new ArrayList();
/* 208 */     for (IDrmQueryRow row : rows) {
/* 209 */       IDrmDataObject dbobj = row.getResultDbo(bmClassMeta.getDbClassId());
/* 210 */       IDrmDataObject maintenanceLog = DrmEntityFactory.getInstance().createDataObject();
/* 211 */       maintenanceLog.setBmClassId("IRMS_MAINTENANCE_LOG");
/*     */ 
/* 213 */       StringBuffer description = new StringBuffer();
/* 214 */       Iterator it = modifyAttrs.keySet().iterator();
/* 215 */       Map allAttrMetas = bmClassMeta.getAllAttrMetas();
/* 216 */       while (it.hasNext()) {
/* 217 */         String attrId = (String)it.next();
/* 218 */         BMAttrMeta attrMeta = (BMAttrMeta)allAttrMetas.get(attrId);
/* 219 */         Object attrValue = dbobj.getAttrValue(attrId);
/* 220 */         StringBuffer origLabel = new StringBuffer();
/* 221 */         if ((attrValue != null) && (!"".equals(attrValue.toString()))) {
/* 222 */           if (attrMeta.getIsRelation().booleanValue()) {
/* 223 */             origLabel.append(((IDrmLabelValue)attrValue).getLabel());
/* 224 */           } else if ((attrMeta.getIsEnumAttr().booleanValue()) && (attrMeta.getIsMultiValue().booleanValue())) {
/* 225 */             List enumEntitys = (List)attrValue;
/* 226 */             for (int i = 0; i < enumEntitys.size(); i++) {
/* 227 */               origLabel.append(((IDrmEnumValue)enumEntitys.get(i)).getEnumName() + ",");
/*     */             }
/* 229 */             origLabel.delete(origLabel.length() - 1, origLabel.length());
/* 230 */           } else if ((attrMeta.getIsEnumAttr().booleanValue()) && (!attrMeta.getIsMultiValue().booleanValue())) {
/* 231 */             origLabel.append(((IDrmEnumValue)attrValue).getEnumName());
/* 232 */           } else if (attrValue.getClass() == Boolean.class) {
/* 233 */             if (((Boolean)attrValue).booleanValue())
/* 234 */               origLabel.append("是");
/*     */             else
/* 236 */               origLabel.append("否");
/*     */           }
/*     */           else {
/* 239 */             origLabel.append(attrValue.toString());
/*     */           }
/*     */         }
/*     */ 
/* 243 */         String newLabel = (String)attrLabels.get(attrId);
/* 244 */         if ((attrMeta.getAttrClassType() == java.sql.Date.class) || (attrMeta.getAttrClassType() == Timestamp.class)) {
/* 245 */           if (!"".equals(origLabel.toString())) {
/* 246 */             java.util.Date origDate = TimeFormatHelper.convertDate(origLabel.toString(), "yyyy-MM-dd HH:mm:ss");
/* 247 */             origLabel = origLabel.replace(0, origLabel.length(), TimeFormatHelper.getFormatDate(origDate, "yyyy-MM-dd HH:mm:ss"));
/*     */           }
/* 249 */           if (!"".equals(newLabel)) {
/* 250 */             java.util.Date newDate = TimeFormatHelper.convertDate(newLabel, "yyyy-MM-dd HH:mm:ss");
/* 251 */             newLabel = TimeFormatHelper.getFormatDate(newDate, "yyyy-MM-dd HH:mm:ss");
/*     */           }
/*     */         }
/* 254 */         if (!origLabel.toString().equals(attrLabels.get(attrId))) {
/* 255 */           description.append(attrMeta.getLabelCn() + "[" + origLabel.toString() + "|" + newLabel + "]@");
/*     */         }
/*     */       }
/* 258 */       if (description.length() == 0)
/*     */         continue;
/* 260 */       StringBuffer label = new StringBuffer();
/* 261 */       List labelAttrIds = bmClassMeta.getConstructLabelAttrIds();
/* 262 */       for (int i = 0; i < labelAttrIds.size(); i++) {
/* 263 */         String attrId = (String)labelAttrIds.get(i);
/* 264 */         if (modifyAttrs.keySet().contains(attrId))
/* 265 */           label.append(modifyAttrs.get(attrId));
/*     */         else {
/* 267 */           label.append(dbobj.getAttrValue(attrId));
/*     */         }
/* 269 */         if (i != labelAttrIds.size() - 1) {
/* 270 */           label.append(bmClassMeta.getLabelJoinChar());
/*     */         }
/*     */       }
/* 273 */       maintenanceLog.setAttrValue("OBJECT_NAME", label.toString());
/* 274 */       description.insert(0, label + ":");
/* 275 */       maintenanceLog.setAttrValue("DESCIPTION", description.substring(0, description.length() - 1).toString());
/*     */ 
/* 277 */       maintenanceLog.setAttrValue("CREATE_DATE", new java.util.Date());
/* 278 */       maintenanceLog.setAttrValue("USER_NAME", sysUser.getAttrValue("TRUE_NAME"));
/* 279 */       maintenanceLog.setAttrValue("RELATED_SYSUSER_CUID", actionContext.getUserId());
/* 280 */       maintenanceLog.setAttrValue("RELATED_DEPARTMENT_CUID", sysUser.getAttrValue("RELATED_ORGANIZATION_CUID"));
/* 281 */       maintenanceLog.setAttrValue("IP_ADDRESS", request.getRemoteAddr());
/* 282 */       maintenanceLog.setAttrValue("MACHINE_NAME", request.getRemoteHost());
/*     */ 
/* 284 */       maintenanceLog.setAttrValue("CLS_ID", bmClassId);
/* 285 */       maintenanceLog.setAttrValue("CLS_NAME", bmClassMeta.getLabelCn());
/* 286 */       if ((!"".equals(relatedDistrictCuid)) && (modifyAttrs.keySet().contains(relatedDistrictCuid)))
/* 287 */         maintenanceLog.setAttrValue("RELATED_DISTRICT_CUID", modifyAttrs.get(relatedDistrictCuid));
/* 288 */       else if ((!"".equals(relatedDistrictCuid)) && (dbobj.getAttrValue(relatedDistrictCuid) != null) && ((dbobj.getAttrValue(relatedDistrictCuid) instanceof IDrmLabelValue)))
/*     */       {
/* 290 */         maintenanceLog.setAttrValue("RELATED_DISTRICT_CUID", ((IDrmLabelValue)dbobj.getAttrValue(relatedDistrictCuid)).getValue().toString());
/*     */       }
/* 292 */       maintenanceLogs.add(maintenanceLog);
/*     */     }
/* 294 */     return maintenanceLogs;
/*     */   }
/*     */ 
/*     */   public void setPropertyMeta(HttpServletRequest request, String bmClassId, DrmGroupAttr[] drmGroupAttrs) {
/*     */     try {
/* 299 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 300 */       actionContext.setBmClassId(bmClassId);
/* 301 */       ExtAttrMetaGroup propertyMeta = new ExtAttrMetaGroup();
/* 302 */       propertyMeta.setBmClassId(bmClassId);
/* 303 */       propertyMeta.setDbClassId(bmClassId);
/*     */ 
/* 305 */       ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, "CUID");
/* 306 */       propertyMeta.addExtAttrMeta(attrMeta);
/*     */ 
/* 308 */       for (int i = 0; i < drmGroupAttrs.length; i++) {
/* 309 */         attrMeta = new ExtAttrMeta(bmClassId, drmGroupAttrs[i].getAttrId());
/* 310 */         attrMeta.setExtAttrMeta(ExtAttrNameEnum.Property.ATTR_ROW_NO, drmGroupAttrs[i].getExtVal1());
/* 311 */         attrMeta.setExtAttrMeta(ExtAttrNameEnum.Property.ATTR_COL_NO, drmGroupAttrs[i].getExtVal2());
/* 312 */         propertyMeta.addExtAttrMeta(attrMeta);
/*     */       }
/* 314 */       this.vmModelService.setPropertyMeta(actionContext, bmClassId, propertyMeta);
/*     */     } catch (Exception ex) {
/* 316 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addModifyLogInfoData(HttpServletRequest request, List<IDrmDataObject> idos)
/*     */   {
/* 322 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 323 */     IDrmDataObject sysUser = getSysUser(request);
/* 324 */     List maintenanceLogs = new ArrayList();
/*     */ 
/* 326 */     for (int i = 0; i < idos.size(); i++) {
/* 327 */       IDrmDataObject maintenanceLog = (IDrmDataObject)idos.get(i);
/* 328 */       if (maintenanceLog != null) {
/* 329 */         maintenanceLog.setAttrValue("CREATE_DATE", new java.util.Date());
/* 330 */         maintenanceLog.setAttrValue("USER_NAME", sysUser.getAttrValue("TRUE_NAME"));
/* 331 */         maintenanceLog.setAttrValue("RELATED_SYSUSER_CUID", actionContext.getUserId());
/* 332 */         maintenanceLog.setAttrValue("RELATED_DEPARTMENT_CUID", sysUser.getAttrValue("RELATED_ORGANIZATION_CUID"));
/* 333 */         maintenanceLog.setAttrValue("IP_ADDRESS", request.getRemoteAddr());
/* 334 */         maintenanceLog.setAttrValue("MACHINE_NAME", request.getRemoteHost());
/* 335 */         maintenanceLogs.add(maintenanceLog);
/*     */       }
/*     */     }
/* 337 */     if (maintenanceLogs.size() > 0)
/* 338 */       this.dynResManageService.addDynObjects(actionContext, maintenanceLogs, false);
/*     */   }
/*     */ 
/*     */   private IDrmDataObject getSysUser(HttpServletRequest request)
/*     */   {
/* 343 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 344 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 345 */     queryContext.setPopulate(true);
/* 346 */     IDrmDataObject ddo = DrmEntityFactory.getInstance().createDataObject();
/* 347 */     ddo.setBmClassId("SYS_USER");
/* 348 */     ddo.setCuid(actionContext.getUserId());
/* 349 */     ddo = this.dynResManageService.getDynObject(actionContext, queryContext, ddo);
/* 350 */     return ddo;
/*     */   }
/*     */ 
/*     */   private IDynResManageService getDynResManageService() {
/* 354 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 358 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 362 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DynPropertyAction
 * JD-Core Version:    0.6.0
 */