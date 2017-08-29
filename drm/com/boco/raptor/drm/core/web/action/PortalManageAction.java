/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
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
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.transnms.common.dto.DrmCallBoard;
/*     */ import com.boco.transnms.common.dto.DrmPortlet;
/*     */ import com.boco.transnms.common.dto.DrmPortletLayout;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class PortalManageAction
/*     */ {
/*     */   public DrmPortlet addPortlet(HttpServletRequest request, DrmPortlet portlet)
/*     */     throws UserException
/*     */   {
/*  55 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  56 */     portlet = (DrmPortlet)getDynResManageService().addDynObject(actionContext, portlet, false);
/*  57 */     return portlet;
/*     */   }
/*     */ 
/*     */   public void deletePortlet(HttpServletRequest request, String objectid)
/*     */     throws UserException
/*     */   {
/*  64 */     LogHome.getLog().info("删除一个Portlet[" + objectid + "]");
/*  65 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  66 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*     */ 
/*  68 */     DrmPortlet portlet = new DrmPortlet();
/*  69 */     portlet.setObjectId(objectid);
/*  70 */     portlet = (DrmPortlet)getDynResManageService().getDynObject(actionContext, queryContext, portlet);
/*     */ 
/*  72 */     queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  73 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  74 */     query.setBmClassId("DRM_PORTLET_LAYOUT");
/*  75 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_PORTLET_CUID", "=", portlet.getCuid()));
/*  76 */     DrmPortletLayout dd = new DrmPortletLayout();
/*  77 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/*  78 */     List dpls = new ArrayList();
/*  79 */     List rows = rs.getResultSet();
/*  80 */     for (IDrmQueryRow row : rows) {
/*  81 */       DrmPortletLayout dbo = (DrmPortletLayout)row.getResultDbo("DRM_PORTLET_LAYOUT");
/*  82 */       dpls.add(dbo);
/*     */     }
/*  84 */     getDynResManageService().deleteDynObjects(actionContext, dpls, false);
/*     */ 
/*  86 */     getDynResManageService().deleteDynObject(actionContext, portlet, false);
/*     */   }
/*     */ 
/*     */   public void modifyPortlet(HttpServletRequest request, DrmPortlet portlet)
/*     */     throws UserException
/*     */   {
/*  93 */     LogHome.getLog().info("修改一个Portlet[" + portlet + "]");
/*  94 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  95 */     getDynResManageService().modifyDynObject(actionContext, portlet, false);
/*     */   }
/*     */ 
/*     */   public DrmPortlet getPortletByCuid(HttpServletRequest request, String cuid)
/*     */     throws UserException
/*     */   {
/* 102 */     LogHome.getLog().info("获取一个Portlet[" + cuid + "]");
/* 103 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 104 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 105 */     DrmPortlet portlet = new DrmPortlet();
/* 106 */     portlet.setCuid(cuid);
/* 107 */     return (DrmPortlet)getDynResManageService().getDynObject(actionContext, queryContext, portlet);
/*     */   }
/*     */ 
/*     */   public DrmPortlet[] getAllPortlet(HttpServletRequest request)
/*     */     throws UserException
/*     */   {
/* 114 */     LogHome.getLog().info("获取所有的Portlet");
/* 115 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 116 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 117 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 118 */     query.setBmClassId("DRM_PORTLET");
/* 119 */     DrmPortlet dd = new DrmPortlet();
/* 120 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 121 */     DrmPortlet[] portlets = new DrmPortlet[rs.getResultSet().size()];
/* 122 */     List rows = rs.getResultSet();
/* 123 */     int i = 0;
/* 124 */     for (IDrmQueryRow row : rows) {
/* 125 */       DrmPortlet dbo = (DrmPortlet)row.getResultDbo("DRM_PORTLET");
/* 126 */       portlets[(i++)] = dbo;
/*     */     }
/* 128 */     return portlets;
/*     */   }
/*     */ 
/*     */   public DrmPortletLayout[] getUserPortlet(HttpServletRequest request)
/*     */     throws UserException
/*     */   {
/* 135 */     LogHome.getLog().info("获取当前用户的Portlet");
/* 136 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 137 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 138 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 139 */     query.setBmClassId("DRM_PORTLET_LAYOUT");
/* 140 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", actionContext.getUserId()));
/* 141 */     queryContext.setOrderField("COLUMN_NO,ROW_NO");
/* 142 */     queryContext.setOrderDesc(false);
/* 143 */     DrmPortletLayout dd = new DrmPortletLayout();
/* 144 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 145 */     DrmPortletLayout[] portletLayouts = new DrmPortletLayout[rs.getResultSet().size()];
/* 146 */     List rows = rs.getResultSet();
/* 147 */     int i = 0;
/* 148 */     for (IDrmQueryRow row : rows) {
/* 149 */       DrmPortletLayout portletLayout = (DrmPortletLayout)row.getResultDbo("DRM_PORTLET_LAYOUT");
/* 150 */       if ((portletLayout.getRelatedPortletCuid() != null) && (portletLayout.getRelatedPortletCuid().trim().length() > 0)) {
/* 151 */         DrmPortlet portlet = getPortletByCuid(request, portletLayout.getRelatedPortletCuid());
/* 152 */         portletLayout.setAttrValue("DRM_PORTLET", portlet);
/*     */       }
/* 154 */       portletLayouts[(i++)] = portletLayout;
/*     */     }
/* 156 */     return portletLayouts;
/*     */   }
/*     */ 
/*     */   public void SaveUserPortalPostion(HttpServletRequest request, DrmPortletLayout[] dpls)
/*     */     throws UserException
/*     */   {
/* 163 */     LogHome.getLog().info("修改保存用户的门户内容位置");
/*     */ 
/* 165 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 166 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 167 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 168 */     query.setBmClassId("DRM_PORTLET_LAYOUT");
/* 169 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", actionContext.getUserId()));
/* 170 */     DrmPortletLayout dd = new DrmPortletLayout();
/* 171 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 172 */     List delDrmPortletLayouts = new ArrayList();
/* 173 */     List rows = rs.getResultSet();
/* 174 */     for (IDrmQueryRow row : rows) {
/* 175 */       DrmPortletLayout portletLayout = (DrmPortletLayout)row.getResultDbo("DRM_PORTLET_LAYOUT");
/* 176 */       delDrmPortletLayouts.add(portletLayout);
/*     */     }
/* 178 */     getDynResManageService().deleteDynObjects(actionContext, delDrmPortletLayouts, false);
/*     */ 
/* 180 */     List addDrmPortletLayouts = new ArrayList();
/* 181 */     if (dpls != null) {
/* 182 */       for (int i = 0; i < dpls.length; i++) {
/* 183 */         DrmPortletLayout drp = dpls[i];
/* 184 */         drp.setRelatedUserCuid(actionContext.getUserId());
/* 185 */         addDrmPortletLayouts.add(drp);
/*     */       }
/*     */     }
/* 188 */     getDynResManageService().addDynObjects(actionContext, addDrmPortletLayouts, false);
/*     */   }
/*     */ 
/*     */   public void modifyUserPortletLayoutHeight(HttpServletRequest request, String cuid, long height)
/*     */     throws UserException
/*     */   {
/* 195 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 196 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 197 */     DrmPortletLayout drmPortletLayout = new DrmPortletLayout();
/* 198 */     drmPortletLayout.setCuid(cuid);
/* 199 */     drmPortletLayout = (DrmPortletLayout)getDynResManageService().getDynObject(actionContext, queryContext, drmPortletLayout);
/* 200 */     drmPortletLayout.setHeight(height);
/* 201 */     getDynResManageService().modifyDynObject(actionContext, drmPortletLayout, false);
/*     */   }
/*     */ 
/*     */   public void deleteUserPortlet(HttpServletRequest request, String cuid) throws UserException {
/* 205 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 206 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 207 */     DrmPortletLayout drmPortletLayout = new DrmPortletLayout();
/* 208 */     drmPortletLayout.setCuid(cuid);
/* 209 */     drmPortletLayout = (DrmPortletLayout)getDynResManageService().getDynObject(actionContext, queryContext, drmPortletLayout);
/* 210 */     getDynResManageService().deleteDynObject(actionContext, drmPortletLayout, false);
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> getCallBoards(HttpServletRequest request, String currentTime) throws UserException {
/*     */     try {
/* 215 */       IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 216 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 217 */       BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, "DRM_CALL_BOARD");
/*     */ 
/* 219 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 220 */       query.setBmClassId("DRM_CALL_BOARD");
/* 221 */       query.addQueryCondExps(new DrmQueryAttrCond("ENDDATETIME", ">=", currentTime));
/* 222 */       query.addQueryCondExps(new DrmQueryAttrCond("BEGINDATETIME", "<=", currentTime));
/* 223 */       queryContext.setOrderField("BEGINDATETIME");
/* 224 */       queryContext.setOrderDesc(true);
/* 225 */       DrmCallBoard callBoard = new DrmCallBoard();
/* 226 */       IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, callBoard);
/* 227 */       List rows = rs.getResultSet();
/*     */ 
/* 229 */       List idos = new ArrayList();
/* 230 */       for (IDrmQueryRow row : rows) {
/* 231 */         IDrmDataObject dbobj = row.getResultDbo(bmClassMeta.getDbClassId());
/* 232 */         idos.add(dbobj);
/*     */       }
/* 234 */       return idos;
/*     */     } catch (UserException ex) {
/* 236 */       LogHome.getLog().error("", ex);
/* 237 */     }throw new UserException(ex.getMessage());
/*     */   }
/*     */ 
/*     */   public static IDynResManageService getDynResManageService()
/*     */   {
/* 243 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 247 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.PortalManageAction
 * JD-Core Version:    0.6.0
 */