/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*     */ import com.boco.raptor.drm.core.meta.ClassAttrGroupMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
/*     */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
/*     */ import com.boco.raptor.drm.core.meta.TemplateMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*     */ import com.boco.raptor.drm.core.service.security.impl.UserDetails;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.raptor.drm.core.web.security.SecurityModel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class VMModelAction
/*     */ {
/*  47 */   private IBMModelService bmModelService = null;
/*  48 */   private IVMModelService vmModelService = null;
/*     */ 
/*     */   public void printClassMeta(String bmClassId)
/*     */   {
/*  54 */     if (this.bmModelService == null) {
/*  55 */       this.bmModelService = getBMModelService();
/*     */     }
/*  57 */     BMClassMeta classMeta = this.bmModelService.getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/*  58 */     LogHome.getLog().info(classMeta);
/*     */   }
/*     */ 
/*     */   public void printEnumMeta(String enumId) {
/*  62 */     if (this.bmModelService == null) {
/*  63 */       this.bmModelService = getBMModelService();
/*     */     }
/*  65 */     BMEnumMeta enumMeta = this.bmModelService.getEnumMeta(ServiceHelper.createSvActCxt(), enumId);
/*  66 */     LogHome.getLog().info(enumMeta);
/*     */   }
/*     */ 
/*     */   public BMClassMeta getClassMeta(HttpServletRequest request, String bmClassId)
/*     */   {
/*  71 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  72 */     if (this.bmModelService == null) {
/*  73 */       this.bmModelService = getBMModelService();
/*     */     }
/*  75 */     BMClassMeta classMeta = this.bmModelService.getClassMeta(actionContext, bmClassId);
/*  76 */     return classMeta;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getStatAttrLabel(HttpServletRequest request, String bmClassId) {
/*  80 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  81 */     if (this.bmModelService == null) {
/*  82 */       this.bmModelService = getBMModelService();
/*     */     }
/*  84 */     List statClassMetas = this.bmModelService.getStatClassMeta(actionContext, bmClassId);
/*  85 */     Map statAttrLabel = new HashMap();
/*  86 */     for (int i = 0; i < statClassMetas.size(); i++) {
/*  87 */       BMClassMeta classMeta = (BMClassMeta)statClassMetas.get(i);
/*  88 */       statAttrLabel.put(classMeta.getBmClassId(), classMeta.getLabelCn());
/*     */     }
/*  90 */     return statAttrLabel;
/*     */   }
/*     */ 
/*     */   public List<BMAttrMeta> getBmClassMetaRelatedClassAttrMeta(HttpServletRequest request, String bmClassId, String relatedBmClassId)
/*     */   {
/*  95 */     BMClassMeta classMeta = getClassMeta(request, bmClassId);
/*  96 */     return classMeta.getRelatedClassAttrMeta(relatedBmClassId);
/*     */   }
/*     */ 
/*     */   public List<String> getAllAttrIds(HttpServletRequest request, String bmClassId) {
/* 100 */     BMClassMeta classMeta = getClassMeta(request, bmClassId);
/* 101 */     return classMeta.getAllAttrIds();
/*     */   }
/*     */ 
/*     */   public BMEnumMeta getEnumMeta(String enumId) {
/* 105 */     if (this.bmModelService == null) {
/* 106 */       this.bmModelService = getBMModelService();
/*     */     }
/* 108 */     BMEnumMeta enumMeta = this.bmModelService.getEnumMeta(ServiceHelper.createSvActCxt(), enumId);
/* 109 */     return enumMeta;
/*     */   }
/*     */ 
/*     */   public BMEnumMeta getRelationEnumMeta(Map _relatedAttrIds) {
/* 113 */     if (this.bmModelService == null) {
/* 114 */       this.bmModelService = getBMModelService();
/*     */     }
/* 116 */     BMEnumMeta enumMeta = this.bmModelService.getRelationEnumMeta(ServiceHelper.createSvActCxt(), _relatedAttrIds);
/* 117 */     return enumMeta;
/*     */   }
/*     */ 
/*     */   public ExtAttrMetaGroup getPropertyMeta(HttpServletRequest request, String bmClassId) {
/* 121 */     if (this.vmModelService == null) {
/* 122 */       this.vmModelService = getVMModelService();
/*     */     }
/* 124 */     ExtAttrMetaGroup propertyMeta = this.vmModelService.getPropertyMeta(ServiceHelper.getUserSvActCxt(request), bmClassId);
/* 125 */     return propertyMeta;
/*     */   }
/*     */ 
/*     */   public ExtAttrMetaGroup getQueryTableMeta(HttpServletRequest request, String bmClassId) {
/* 129 */     if (this.vmModelService == null) {
/* 130 */       this.vmModelService = getVMModelService();
/*     */     }
/* 132 */     ExtAttrMetaGroup queryTableMeta = this.vmModelService.getQueryTableMeta(ServiceHelper.getUserSvActCxt(request), bmClassId);
/*     */ 
/* 134 */     return queryTableMeta;
/*     */   }
/*     */ 
/*     */   public List<TemplateMeta> getQueryTemplates(HttpServletRequest request, String bmClassId) {
/* 138 */     if (this.vmModelService == null) {
/* 139 */       this.vmModelService = getVMModelService();
/*     */     }
/* 141 */     List queryTemplates = this.vmModelService.getQueryTemplates(ServiceHelper.getUserSvActCxt(request), bmClassId);
/*     */ 
/* 143 */     return queryTemplates;
/*     */   }
/*     */ 
/*     */   public void setPropertyMeta(HttpServletRequest request, String bmClassId, ExtAttrMetaGroup propertyMeta)
/*     */   {
/* 148 */     if (this.vmModelService == null) {
/* 149 */       this.vmModelService = getVMModelService();
/*     */     }
/* 151 */     this.vmModelService.setPropertyMeta(ServiceHelper.getUserSvActCxt(request), bmClassId, propertyMeta);
/*     */   }
/*     */ 
/*     */   public void setQueryTableMeta(HttpServletRequest request, String bmClassId, ExtAttrMetaGroup queryTableMeta) {
/* 155 */     if (this.vmModelService == null) {
/* 156 */       this.vmModelService = getVMModelService();
/*     */     }
/* 158 */     this.vmModelService.setQueryTableMeta(ServiceHelper.getUserSvActCxt(request), bmClassId, queryTableMeta);
/*     */   }
/*     */ 
/*     */   public void addQueryTemplate(String userId, TemplateMeta queryTemplate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void modifyQueryTemplate(String userId, TemplateMeta queryTemplate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void deleteQueryTemplate(String userId, TemplateMeta queryTemplate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public List<ExcelImpTemplateMeta> getExcelImpTemplates(HttpServletRequest request, String bmClassId)
/*     */   {
/* 175 */     if (this.vmModelService == null) {
/* 176 */       this.vmModelService = getVMModelService();
/*     */     }
/* 178 */     List excelImpTemplates = this.vmModelService.getExcelImpTemplates(ServiceHelper.getUserSvActCxt(request), bmClassId);
/*     */ 
/* 180 */     return excelImpTemplates;
/*     */   }
/*     */ 
/*     */   public List<TemplateMeta> getExcelExpTemplates(HttpServletRequest request, String bmClassId)
/*     */   {
/* 187 */     if (this.vmModelService == null) {
/* 188 */       this.vmModelService = getVMModelService();
/*     */     }
/* 190 */     List excelExpTemplates = this.vmModelService.getExcelExpTemplates(ServiceHelper.getUserSvActCxt(request), bmClassId);
/* 191 */     return excelExpTemplates;
/*     */   }
/*     */ 
/*     */   public ExcelImpTemplateMeta getExcelImpTemplate(HttpServletRequest request, String bmClassId, String cuid)
/*     */   {
/* 198 */     if (this.vmModelService == null) {
/* 199 */       this.vmModelService = getVMModelService();
/*     */     }
/* 201 */     ExcelImpTemplateMeta excelImpTemplate = this.vmModelService.getExcelImpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, cuid);
/*     */ 
/* 203 */     return excelImpTemplate;
/*     */   }
/*     */ 
/*     */   public List<BMAttrMeta> getExcelExpAttrList(HttpServletRequest request, String bmClassId, String cuid) {
/* 207 */     if (this.vmModelService == null) {
/* 208 */       this.vmModelService = getVMModelService();
/*     */     }
/* 210 */     List attrLis = this.vmModelService.getExcelExpAttrList(ServiceHelper.getUserSvActCxt(request), bmClassId, cuid);
/* 211 */     return attrLis;
/*     */   }
/*     */ 
/*     */   public ExcelImpTemplateMeta addExcelImpTemplate(HttpServletRequest request, String bmClassId, ExcelImpTemplateMeta excelImpTemplateMeta)
/*     */   {
/* 218 */     if (this.vmModelService == null) {
/* 219 */       this.vmModelService = getVMModelService();
/*     */     }
/* 221 */     ExcelImpTemplateMeta template = this.vmModelService.addExcelImpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, excelImpTemplateMeta);
/* 222 */     return template;
/*     */   }
/*     */ 
/*     */   public ExcelImpTemplateMeta addExcelExpTemplate(HttpServletRequest request, String bmClassId, ExcelImpTemplateMeta excelImpTemplateMeta)
/*     */   {
/* 229 */     if (this.vmModelService == null) {
/* 230 */       this.vmModelService = getVMModelService();
/*     */     }
/* 232 */     ExcelImpTemplateMeta template = this.vmModelService.addExcelExpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, excelImpTemplateMeta);
/* 233 */     return template;
/*     */   }
/*     */ 
/*     */   public void modifyExcelImpTemplate(HttpServletRequest request, String bmClassId, ExcelImpTemplateMeta excelImpTemplateMeta)
/*     */   {
/* 240 */     if (this.vmModelService == null) {
/* 241 */       this.vmModelService = getVMModelService();
/*     */     }
/* 243 */     this.vmModelService.modifyExcelImpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, excelImpTemplateMeta);
/*     */   }
/*     */ 
/*     */   public void delExcelImpTemplate(HttpServletRequest request, String bmClassId, ExcelImpTemplateMeta excelImpTemplateMeta)
/*     */   {
/* 250 */     if (this.vmModelService == null) {
/* 251 */       this.vmModelService = getVMModelService();
/*     */     }
/* 253 */     this.vmModelService.deleteExcelImpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, excelImpTemplateMeta);
/*     */   }
/*     */ 
/*     */   public void clearExcelImpTemplateSession(HttpServletRequest request) {
/* 257 */     request.getSession().removeAttribute("uploadInfo");
/* 258 */     request.getSession().removeAttribute("importValidProcessInfo");
/* 259 */     request.getSession().removeAttribute("importSaveResultFileInfo");
/* 260 */     request.getSession().removeAttribute("importProcessInfo");
/*     */   }
/*     */ 
/*     */   public void delExcelExpTemplate(HttpServletRequest request, String bmClassId, String tempCuid)
/*     */   {
/* 267 */     if (this.vmModelService == null) {
/* 268 */       this.vmModelService = getVMModelService();
/*     */     }
/* 270 */     this.vmModelService.deleteExcelExpTemplate(ServiceHelper.getUserSvActCxt(request), bmClassId, tempCuid);
/*     */   }
/*     */ 
/*     */   public List<ClassAttrGroupMeta> getClassAttrGroup()
/*     */   {
/* 278 */     if (this.vmModelService == null) {
/* 279 */       this.vmModelService = getVMModelService();
/*     */     }
/* 281 */     List groupList = this.vmModelService.getClassAttrGroup(ServiceHelper.createSvActCxt());
/*     */ 
/* 283 */     return groupList;
/*     */   }
/*     */ 
/*     */   public ClassAttrGroupMeta addClassAttrGroup(HttpServletRequest request, String labelCn) {
/* 287 */     if (this.vmModelService == null) {
/* 288 */       this.vmModelService = getVMModelService();
/*     */     }
/* 290 */     ClassAttrGroupMeta groupMeta = this.vmModelService.addClassAttrGroup(ServiceHelper.getUserSvActCxt(request), labelCn);
/*     */ 
/* 292 */     return groupMeta;
/*     */   }
/*     */ 
/*     */   public void modifyClassAttrGroup(HttpServletRequest request, String groupCuid, String labelCn) {
/* 296 */     if (this.vmModelService == null) {
/* 297 */       this.vmModelService = getVMModelService();
/*     */     }
/* 299 */     this.vmModelService.modifyClassAttrGroup(ServiceHelper.getUserSvActCxt(request), groupCuid, labelCn);
/*     */   }
/*     */ 
/*     */   public String delClassAttrGroup(HttpServletRequest request, String cuid) {
/* 303 */     if (this.vmModelService == null) {
/* 304 */       this.vmModelService = getVMModelService();
/*     */     }
/* 306 */     return this.vmModelService.delClassAttrGroup(ServiceHelper.getUserSvActCxt(request), cuid);
/*     */   }
/*     */ 
/*     */   public void modifyClassAttrGroupSort(HttpServletRequest request, List<String> groupCuidList) {
/* 310 */     if (this.vmModelService == null) {
/* 311 */       this.vmModelService = getVMModelService();
/*     */     }
/* 313 */     this.vmModelService.modifyClassAttrGroupSort(ServiceHelper.getUserSvActCxt(request), groupCuidList);
/*     */   }
/*     */ 
/*     */   public ClassAttrGroupMeta getClassGroupAttr(String bmClassId, String groupCuid) {
/* 317 */     if (this.vmModelService == null) {
/* 318 */       this.vmModelService = getVMModelService();
/*     */     }
/* 320 */     ClassAttrGroupMeta groupMeta = this.vmModelService.getClassGroupAttr(ServiceHelper.createSvActCxt(), bmClassId, groupCuid);
/*     */ 
/* 322 */     return groupMeta;
/*     */   }
/*     */ 
/*     */   public Map<String, ClassAttrGroupMeta> getClassGroupAttrMap(String bmClassId) {
/* 326 */     if (this.vmModelService == null) {
/* 327 */       this.vmModelService = getVMModelService();
/*     */     }
/* 329 */     Map groupMap = this.vmModelService.getClassGroupAttrMap(ServiceHelper.createSvActCxt(), bmClassId);
/*     */ 
/* 331 */     return groupMap;
/*     */   }
/*     */ 
/*     */   public void saveClassGroupAttr(HttpServletRequest request, String bmClassId, ClassAttrGroupMeta groupMeta) {
/* 335 */     if (this.vmModelService == null) {
/* 336 */       this.vmModelService = getVMModelService();
/*     */     }
/* 338 */     this.vmModelService.saveClassGroupAttr(ServiceHelper.getUserSvActCxt(request), bmClassId, groupMeta);
/*     */   }
/*     */ 
/*     */   public void saveClassGroupAttrMap(HttpServletRequest request, String bmClassId, List<ClassAttrGroupMeta> groupMap) {
/* 342 */     if (this.vmModelService == null) {
/* 343 */       this.vmModelService = getVMModelService();
/*     */     }
/* 345 */     this.vmModelService.saveClassGroupAttrMap(ServiceHelper.getUserSvActCxt(request), bmClassId, groupMap);
/*     */   }
/*     */ 
/*     */   public String getLoginUserName(HttpServletRequest request) {
/* 349 */     SecurityModel sModel = SecurityModel.getInstance();
/* 350 */     IAuthentication authen = sModel.getCurrentUserAuthentication(request);
/* 351 */     if (authen == null) {
/* 352 */       return "";
/*     */     }
/* 354 */     return authen.getUserDetails().getTruename();
/*     */   }
/*     */ 
/*     */   public Map getCurrentUserDetail(HttpServletRequest request)
/*     */   {
/* 359 */     SecurityModel sModel = SecurityModel.getInstance();
/* 360 */     IAuthentication authen = sModel.getCurrentUserAuthentication(request);
/* 361 */     if (authen == null) {
/* 362 */       return null;
/*     */     }
/* 364 */     Map userDetailMap = new HashMap();
/* 365 */     UserDetails us = (UserDetails)authen.getUserDetails();
/* 366 */     if (us != null) {
/* 367 */       userDetailMap.put("userId", us.getUserId());
/* 368 */       userDetailMap.put("password", us.getPassword());
/* 369 */       userDetailMap.put("loginName", us.getLoginName());
/*     */     }
/* 371 */     return userDetailMap;
/*     */   }
/*     */ 
/*     */   public String getLoginUserIdAndPassword(HttpServletRequest request)
/*     */   {
/* 376 */     String loginParm = "";
/* 377 */     SecurityModel sModel = SecurityModel.getInstance();
/* 378 */     IAuthentication authen = sModel.getCurrentUserAuthentication(request);
/* 379 */     if (authen == null)
/* 380 */       loginParm = "userName=&password=";
/*     */     else {
/* 382 */       loginParm = "userName=" + authen.getUserDetails().getLoginName() + "&password=" + authen.getUserDetails().getPassword();
/*     */     }
/* 384 */     return loginParm;
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 388 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 392 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ 
/*     */   public String getOnLineUserCount(HttpServletRequest request)
/*     */   {
/* 401 */     String userCount = "0";
/* 402 */     Map authenticationMap = SecurityModel.getInstance().getOnlineUses(request);
/* 403 */     if (authenticationMap != null) {
/* 404 */       userCount = authenticationMap.size() + "";
/*     */     }
/* 406 */     return userCount;
/*     */   }
/*     */ 
/*     */   public void deleteLoginOutUserFormMap(HttpServletRequest request)
/*     */   {
/* 415 */     SecurityModel.getInstance().deleteLoginOutUserFormMap(request);
/*     */   }
/*     */ 
/*     */   public List getAllOnlineUserDetail(HttpServletRequest request)
/*     */   {
/* 425 */     List userDetailList = new ArrayList();
/* 426 */     Map authenticationMap = SecurityModel.getInstance().getOnlineUses(request);
/* 427 */     if (authenticationMap != null) {
/* 428 */       Iterator it = authenticationMap.keySet().iterator();
/* 429 */       while (it.hasNext()) {
/* 430 */         String userId = (String)it.next();
/* 431 */         LogHome.getLog().info("在线用户ID[" + userId + "]！");
/* 432 */         IAuthentication auth = (IAuthentication)authenticationMap.get(userId);
/* 433 */         LogHome.getLog().info("在线用户ID[" + userId + "]的认证类[" + auth + "]！");
/* 434 */         if (auth != null) {
/* 435 */           IUserDetails userDetail = auth.getUserDetails();
/* 436 */           LogHome.getLog().info("在线用户ID[" + userId + "]的详细信息[" + userDetail + "]！");
/* 437 */           if (userDetail != null) {
/* 438 */             LogHome.getLog().info("正在获取在线用户[" + userDetail.getUserId() + "," + userDetail.getLoginName() + "]的详细信息！");
/* 439 */             String[] str = { userDetail.getRelatedDistrict(), userDetail.getLoginName() + "(" + userDetail.getTruename() + ")", userDetail.getClientIp() };
/*     */ 
/* 441 */             userDetailList.add(str);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 446 */     return userDetailList;
/*     */   }
/*     */ 
/*     */   public boolean getCurrentUserIsAdmin(HttpServletRequest request)
/*     */   {
/* 455 */     boolean flag = false;
/* 456 */     SecurityModel sModel = SecurityModel.getInstance();
/* 457 */     IAuthentication authen = sModel.getCurrentUserAuthentication(request);
/* 458 */     if (authen != null) {
/* 459 */       UserDetails us = (UserDetails)authen.getUserDetails();
/* 460 */       IUserDetails admin = sModel.getAdmin();
/* 461 */       if ((us.getUserId() != null) && (us.getUserId().equals(admin.getUserId()))) {
/* 462 */         flag = true;
/*     */       }
/*     */     }
/* 465 */     return flag;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.VMModelAction
 * JD-Core Version:    0.6.0
 */