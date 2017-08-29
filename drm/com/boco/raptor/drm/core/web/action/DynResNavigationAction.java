/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.CnStrHelper;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmLabelValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import com.boco.raptor.drm.core.web.security.SecurityModel;
/*     */ import com.boco.transnms.common.dto.DrmResNav;
/*     */ import com.boco.transnms.common.dto.DrmResNavNode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ public class DynResNavigationAction
/*     */ {
/*     */   public DrmResNav addNavigation(HttpServletRequest request, DrmResNav drn)
/*     */     throws UserException
/*     */   {
/*  59 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  60 */     drn.setRelatedUserCuid(actionContext.getUserId());
/*  61 */     drn = (DrmResNav)getDynResManageService().addDynObject(actionContext, drn, false);
/*  62 */     return drn;
/*     */   }
/*     */ 
/*     */   public void deleteNavigation(HttpServletRequest request, String objectId)
/*     */     throws UserException
/*     */   {
/*  69 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  70 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  71 */     DrmResNav drn = new DrmResNav();
/*  72 */     drn.setObjectId(objectId);
/*  73 */     drn = (DrmResNav)getDynResManageService().getDynObject(actionContext, queryContext, drn);
/*  74 */     DrmResNavNode[] drnns = getNavigationAllChildNode(request, drn.getCuid());
/*  75 */     for (int i = 0; i < drnns.length; i++) {
/*  76 */       getDynResManageService().deleteDynObject(actionContext, drnns[i], false);
/*     */     }
/*  78 */     getDynResManageService().deleteDynObject(actionContext, drn, false);
/*     */   }
/*     */ 
/*     */   public void modifyNavigation(HttpServletRequest request, DrmResNav drn)
/*     */     throws UserException
/*     */   {
/*  85 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  86 */     drn.setRelatedUserCuid(actionContext.getUserId());
/*  87 */     getDynResManageService().modifyDynObject(actionContext, drn, false);
/*     */   }
/*     */ 
/*     */   public DrmResNav[] getSystemNavigation(HttpServletRequest request, String navType)
/*     */     throws UserException
/*     */   {
/*  95 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  96 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  97 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  98 */     query.setBmClassId("DRM_RES_NAV");
/*  99 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", SecurityModel.getInstance().getAdmin().getUserId()));
/* 100 */     query.addQueryCondExps(new DrmQueryAttrCond("NAV_TYPE", "=", navType));
/* 101 */     queryContext.setOrderField("LABEL_CN");
/* 102 */     DrmResNav drnn = new DrmResNav();
/* 103 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, drnn);
/* 104 */     DrmResNav[] rlv = new DrmResNav[0];
/*     */     int i;
/* 105 */     if (rs.getResultSet().size() > 0) {
/* 106 */       rlv = new DrmResNav[rs.getResultSet().size()];
/* 107 */       List rows = rs.getResultSet();
/* 108 */       i = 0;
/* 109 */       for (IDrmQueryRow row : rows) {
/* 110 */         DrmResNav dbo = (DrmResNav)row.getResultDbo("DRM_RES_NAV");
/* 111 */         rlv[(i++)] = dbo;
/*     */       }
/*     */     }
/* 114 */     return rlv;
/*     */   }
/*     */ 
/*     */   public DrmResNav[] getUserNavigation(HttpServletRequest request, String navType)
/*     */     throws UserException
/*     */   {
/* 122 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 123 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 124 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 125 */     query.setBmClassId("DRM_RES_NAV");
/* 126 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", actionContext.getUserId()));
/* 127 */     query.addQueryCondExps(new DrmQueryAttrCond("NAV_TYPE", "=", navType));
/* 128 */     queryContext.setOrderField("REMARK");
/* 129 */     DrmResNav drnn = new DrmResNav();
/* 130 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, drnn);
/* 131 */     DrmResNav[] rlv = new DrmResNav[0];
/*     */     int i;
/* 132 */     if (rs.getResultSet().size() > 0) {
/* 133 */       rlv = new DrmResNav[rs.getResultSet().size()];
/* 134 */       List rows = rs.getResultSet();
/* 135 */       i = 0;
/* 136 */       for (IDrmQueryRow row : rows) {
/* 137 */         DrmResNav dbo = (DrmResNav)row.getResultDbo("DRM_RES_NAV");
/* 138 */         rlv[(i++)] = dbo;
/*     */       }
/*     */     }
/* 141 */     return rlv;
/*     */   }
/*     */ 
/*     */   public DrmResNav[] getAllNavigation(HttpServletRequest request, String navType, boolean haveSystem)
/*     */     throws UserException
/*     */   {
/* 149 */     DrmResNav[] systemrlv = new DrmResNav[0];
/* 150 */     DrmResNav[] userrlv = new DrmResNav[0];
/* 151 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 152 */     if ((!actionContext.getUserId().equals(SecurityModel.getInstance().getAdmin().getUserId())) && (haveSystem))
/*     */     {
/* 154 */       systemrlv = getSystemNavigation(request, navType);
/*     */     }
/*     */ 
/* 157 */     userrlv = getUserNavigation(request, navType);
/* 158 */     DrmResNav[] rlv = new DrmResNav[0];
/*     */ 
/* 169 */     rlv = new DrmResNav[systemrlv.length + userrlv.length];
/* 170 */     System.arraycopy(systemrlv, 0, rlv, 0, systemrlv.length);
/* 171 */     System.arraycopy(userrlv, 0, rlv, systemrlv.length, userrlv.length);
/*     */ 
/* 173 */     return rlv;
/*     */   }
/*     */ 
/*     */   public DrmResNavNode[] getNavigationAllChildNode(HttpServletRequest request, String resNavCuid)
/*     */   {
/* 185 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 186 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 187 */     queryContext.setOrderField("NODE_LEVEL,SORT_NUM,LABEL_CN");
/* 188 */     queryContext.setOrderDesc(false);
/* 189 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 190 */     query.setBmClassId("DRM_RES_NAV_NODE");
/* 191 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_RES_NAV_CUID", "=", resNavCuid));
/* 192 */     DrmResNavNode drnn = new DrmResNavNode();
/* 193 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, drnn);
/* 194 */     DrmResNavNode[] dlvs = new DrmResNavNode[rs.getResultSet().size()];
/* 195 */     List rows = rs.getResultSet();
/* 196 */     List userHaveBmClassIds = new ArrayList();
/* 197 */     List userActionNames = new ArrayList();
/* 198 */     IAuthentication auth = SecurityModel.getInstance().getAuthentication(actionContext.getUserId());
/*     */     int i;
/* 199 */     if (auth != null)
/*     */     {
/*     */       int i;
/* 200 */       if ((auth.getUserDetails() != null) && (auth.getUserDetails().getIsAdmin()))
/*     */       {
/* 202 */         i = 0;
/* 203 */         for (IDrmQueryRow row : rows) {
/* 204 */           DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 205 */           dbo.setAttrValue("IS_AUTH", true);
/* 206 */           dlvs[(i++)] = dbo;
/*     */         }
/*     */       }
/*     */       else {
/* 210 */         userHaveBmClassIds.addAll(auth.getBmClassIds());
/* 211 */         userActionNames.addAll(auth.getActionNames());
/* 212 */         i = 0;
/* 213 */         for (IDrmQueryRow row : rows) {
/* 214 */           DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 215 */           dbo.setAttrValue("IS_AUTH", false);
/* 216 */           if (dbo.getNodeType() == 1L)
/*     */           {
/* 218 */             if (userHaveBmClassIds.contains(dbo.getBmClassid()))
/* 219 */               dbo.setAttrValue("IS_AUTH", true);
/*     */           }
/* 221 */           else if (dbo.getNodeType() == 3L)
/*     */           {
/* 223 */             dbo.setAttrValue("IS_AUTH", false);
/* 224 */             String[] nodeActionNames = dbo.getActionNames().split(",");
/* 225 */             for (int k = 0; k < nodeActionNames.length; k++)
/* 226 */               if (userActionNames.contains(nodeActionNames[k])) {
/* 227 */                 dbo.setAttrValue("IS_AUTH", true);
/* 228 */                 break;
/*     */               }
/*     */           }
/*     */           else {
/* 232 */             dbo.setAttrValue("IS_AUTH", true);
/*     */           }
/* 234 */           dlvs[(i++)] = dbo;
/*     */         }
/*     */       }
/*     */     }
/* 238 */     return dlvs;
/*     */   }
/*     */ 
/*     */   public DrmResNavNode[] getSearchNavChildNode(HttpServletRequest request, String resNavCuid, String matchStr)
/*     */   {
/* 251 */     matchStr = matchStr.toUpperCase();
/* 252 */     boolean cnMatch = true;
/* 253 */     if (matchStr.equals(CnStrHelper.getAlpha(matchStr, matchStr.length()))) {
/* 254 */       cnMatch = false;
/*     */     }
/*     */ 
/* 257 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 258 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 259 */     queryContext.setOrderField("NODE_LEVEL,SORT_NUM");
/* 260 */     queryContext.setOrderDesc(false);
/* 261 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 262 */     query.setBmClassId("DRM_RES_NAV_NODE");
/* 263 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_RES_NAV_CUID", "=", resNavCuid));
/*     */ 
/* 265 */     DrmResNavNode drnn = new DrmResNavNode();
/* 266 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, drnn);
/* 267 */     List rows = rs.getResultSet();
/* 268 */     List upperCuids = new ArrayList();
/* 269 */     for (int i = rows.size() - 1; i >= 0; i--) {
/* 270 */       DrmResNavNode dbo = (DrmResNavNode)((IDrmQueryRow)rows.get(i)).getResultDbo("DRM_RES_NAV_NODE");
/* 271 */       String labelCn = dbo.getLabelCn();
/* 272 */       if (dbo.getNodeType() != 2L) {
/* 273 */         if (((cnMatch) && (labelCn.indexOf(matchStr) == -1)) || ((!cnMatch) && (CnStrHelper.getAlpha(labelCn, labelCn.length()).indexOf(matchStr) == -1)))
/* 274 */           rows.remove(i);
/*     */         else {
/* 276 */           upperCuids.add(dbo.getRelatedUpperNodeCuid());
/*     */         }
/*     */       }
/* 279 */       else if (!upperCuids.contains(dbo.getCuid()))
/* 280 */         rows.remove(i);
/*     */       else {
/* 282 */         upperCuids.add(dbo.getRelatedUpperNodeCuid());
/*     */       }
/*     */     }
/*     */ 
/* 286 */     DrmResNavNode[] dlvs = new DrmResNavNode[rows.size()];
/* 287 */     List userHaveBmClassIds = new ArrayList();
/* 288 */     List userActionNames = new ArrayList();
/* 289 */     IAuthentication auth = SecurityModel.getInstance().getAuthentication(actionContext.getUserId());
/*     */     int i;
/* 290 */     if (auth != null)
/*     */     {
/*     */       int i;
/* 291 */       if ((auth.getUserDetails() != null) && (auth.getUserDetails().getIsAdmin()))
/*     */       {
/* 293 */         i = 0;
/* 294 */         for (IDrmQueryRow row : rows) {
/* 295 */           DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 296 */           dbo.setAttrValue("IS_AUTH", true);
/* 297 */           dlvs[(i++)] = dbo;
/*     */         }
/*     */       }
/*     */       else {
/* 301 */         userHaveBmClassIds.addAll(auth.getBmClassIds());
/* 302 */         userActionNames.addAll(auth.getActionNames());
/* 303 */         i = 0;
/* 304 */         for (IDrmQueryRow row : rows) {
/* 305 */           DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 306 */           dbo.setAttrValue("IS_AUTH", false);
/* 307 */           if (dbo.getNodeType() == 1L)
/*     */           {
/* 309 */             if (userHaveBmClassIds.contains(dbo.getBmClassid()))
/* 310 */               dbo.setAttrValue("IS_AUTH", true);
/*     */           }
/* 312 */           else if (dbo.getNodeType() == 3L)
/*     */           {
/* 314 */             dbo.setAttrValue("IS_AUTH", false);
/* 315 */             String[] nodeActionNames = dbo.getActionNames().split(",");
/* 316 */             for (int k = 0; k < nodeActionNames.length; k++)
/* 317 */               if (userActionNames.contains(nodeActionNames[k])) {
/* 318 */                 dbo.setAttrValue("IS_AUTH", true);
/* 319 */                 break;
/*     */               }
/*     */           }
/*     */           else {
/* 323 */             dbo.setAttrValue("IS_AUTH", true);
/*     */           }
/* 325 */           dlvs[(i++)] = dbo;
/*     */         }
/*     */       }
/*     */     }
/* 329 */     return dlvs;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject addNavigationNode(HttpServletRequest request, DrmResNavNode drnn)
/*     */     throws UserException
/*     */   {
/* 337 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 338 */     drnn = (DrmResNavNode)getDynResManageService().addDynObject(actionContext, drnn, false);
/* 339 */     return drnn;
/*     */   }
/*     */ 
/*     */   public void delNavigationNode(HttpServletRequest request, String drmNavNodeObjectId)
/*     */     throws UserException
/*     */   {
/* 346 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 347 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*     */ 
/* 349 */     DrmResNavNode drnn = new DrmResNavNode();
/* 350 */     drnn.setObjectId(drmNavNodeObjectId);
/* 351 */     drnn = (DrmResNavNode)getDynResManageService().getDynObject(actionContext, queryContext, drnn);
/* 352 */     if (drnn != null)
/*     */     {
/* 354 */       List childNodedbos = getAllChildNodeByNode(request, drnn);
/* 355 */       getDynResManageService().deleteDynObjects(actionContext, childNodedbos, false);
/*     */ 
/* 358 */       DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 359 */       query = new DrmSingleClassQuery();
/* 360 */       query.setBmClassId("DRM_RES_NAV_NODE");
/* 361 */       query.addQueryCondExps(new DrmQueryAttrCond("RELATED_UPPER_NODE_CUID", "=", drnn.getRelatedUpperNodeCuid()));
/* 362 */       query.addQueryCondExps(new DrmQueryAttrCond("SORT_NUM", ">", String.valueOf(drnn.getSortNum())));
/* 363 */       DrmResNavNode dd = new DrmResNavNode();
/* 364 */       IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 365 */       List modifySortNumdbos = new ArrayList();
/* 366 */       List rows = rs.getResultSet();
/* 367 */       for (IDrmQueryRow row : rows) {
/* 368 */         DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 369 */         dbo.setSortNum(dbo.getSortNum() - 1L);
/* 370 */         modifySortNumdbos.add(dbo);
/*     */       }
/*     */ 
/* 374 */       for (int i = 0; i < modifySortNumdbos.size(); i++) {
/* 375 */         DrmResNavNode dbo = (DrmResNavNode)modifySortNumdbos.get(i);
/* 376 */         getDynResManageService().modifyDynObject(actionContext, dbo, false);
/*     */       }
/*     */ 
/* 379 */       getDynResManageService().deleteDynObject(actionContext, drnn, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyNavigationNodeName(HttpServletRequest request, String drmNavNodeObjectId, String nodeName)
/*     */     throws UserException
/*     */   {
/* 387 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 388 */     DrmResNavNode drnn = new DrmResNavNode();
/* 389 */     drnn.setObjectId(drmNavNodeObjectId);
/* 390 */     drnn.setLabelCn(nodeName);
/* 391 */     getDynResManageService().modifyDynObject(actionContext, drnn, false);
/*     */   }
/*     */ 
/*     */   private List getAllChildNodeByNode(HttpServletRequest request, DrmResNavNode node)
/*     */   {
/* 396 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 397 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 398 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 399 */     query.setBmClassId("DRM_RES_NAV_NODE");
/* 400 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_UPPER_NODE_CUID", "=", node.getCuid()));
/* 401 */     DrmResNavNode dd = new DrmResNavNode();
/* 402 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 403 */     List childNodedbos = new ArrayList();
/* 404 */     List rows = rs.getResultSet();
/* 405 */     for (IDrmQueryRow row : rows) {
/* 406 */       DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 407 */       dbo.setNodeLevel(node.getNodeLevel() + 1L);
/* 408 */       childNodedbos.add(dbo);
/* 409 */       List childList = getAllChildNodeByNode(request, dbo);
/* 410 */       childNodedbos.addAll(childList);
/*     */     }
/* 412 */     return childNodedbos;
/*     */   }
/*     */ 
/*     */   public void modifyNavigationNodeOrder(HttpServletRequest request, DrmResNavNode drnn, Map bortherNodesSortNum) throws UserException {
/* 416 */     modifyNavigationNode(request, drnn);
/* 417 */     if (bortherNodesSortNum != null) {
/* 418 */       List modifySortNumdbos = new ArrayList();
/* 419 */       Iterator it = bortherNodesSortNum.keySet().iterator();
/* 420 */       while (it.hasNext()) {
/* 421 */         String objectId = (String)it.next();
/* 422 */         String sortNum = (String)bortherNodesSortNum.get(objectId);
/* 423 */         DrmResNavNode dbo = new DrmResNavNode();
/* 424 */         dbo.setObjectId(objectId);
/* 425 */         dbo.setSortNum(Long.valueOf(sortNum).longValue());
/* 426 */         modifySortNumdbos.add(dbo);
/*     */       }
/* 428 */       if (modifySortNumdbos.size() > 0) {
/* 429 */         IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/* 431 */         for (int i = 0; i < modifySortNumdbos.size(); i++) {
/* 432 */           DrmResNavNode dbo = (DrmResNavNode)modifySortNumdbos.get(i);
/* 433 */           getDynResManageService().modifyDynObject(actionContext, dbo, false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyNavigationNode(HttpServletRequest request, DrmResNavNode drnn)
/*     */     throws UserException
/*     */   {
/* 443 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 444 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*     */ 
/* 446 */     DrmResNavNode olddrnn = new DrmResNavNode();
/* 447 */     olddrnn.setObjectId(drnn.getObjectId());
/* 448 */     olddrnn = (DrmResNavNode)getDynResManageService().getDynObject(actionContext, queryContext, olddrnn);
/* 449 */     if (olddrnn.getNodeLevel() != drnn.getNodeLevel())
/*     */     {
/* 451 */       drnn.setCuid(olddrnn.getCuid());
/* 452 */       List allChildDrmResNodeList = getAllChildNodeByNode(request, drnn);
/* 453 */       for (int i = 0; i < allChildDrmResNodeList.size(); i++) {
/* 454 */         DrmResNavNode dn = (DrmResNavNode)allChildDrmResNodeList.get(i);
/* 455 */         getDynResManageService().modifyDynObject(actionContext, dn, false);
/*     */       }
/*     */     }
/* 458 */     getDynResManageService().modifyDynObject(actionContext, drnn, false);
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue[] getAllClassMetaByNavigation(HttpServletRequest request)
/*     */     throws UserException
/*     */   {
/* 465 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 466 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 467 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/*     */ 
/* 469 */     query.setBmClassId("DRM_RES_NAV");
/* 470 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", actionContext.getUserId()));
/* 471 */     query.addQueryCondExps(new DrmQueryAttrCond("NAV_TYPE", "=", "1"));
/* 472 */     DrmResNav dd = new DrmResNav();
/* 473 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, dd);
/* 474 */     String drmResNavCuids = "";
/* 475 */     List rows = rs.getResultSet();
/* 476 */     for (IDrmQueryRow row : rows) {
/* 477 */       DrmResNav dbo = (DrmResNav)row.getResultDbo("DRM_RES_NAV");
/* 478 */       if (drmResNavCuids.trim().length() == 0)
/* 479 */         drmResNavCuids = "'" + dbo.getCuid() + "'";
/*     */       else {
/* 481 */         drmResNavCuids = drmResNavCuids + ",'" + dbo.getCuid() + "'";
/*     */       }
/*     */     }
/*     */ 
/* 485 */     List bmClassMetaList = new ArrayList();
/* 486 */     if ((drmResNavCuids != null) && (drmResNavCuids.trim().length() > 0)) {
/* 487 */       query = new DrmSingleClassQuery();
/* 488 */       query.setBmClassId("DRM_RES_NAV_NODE");
/* 489 */       query.addQueryCondExps(new DrmQueryAttrCond("RELATED_RES_NAV_CUID", "in", drmResNavCuids));
/* 490 */       query.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "is not", "null"));
/* 491 */       DrmResNavNode ddd = new DrmResNavNode();
/* 492 */       rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query, ddd);
/* 493 */       rows = rs.getResultSet();
/* 494 */       Map bmClassIdLabelCnMap = new HashMap();
/* 495 */       for (IDrmQueryRow row : rows) {
/* 496 */         DrmResNavNode dbo = (DrmResNavNode)row.getResultDbo("DRM_RES_NAV_NODE");
/* 497 */         bmClassIdLabelCnMap.put(dbo.getBmClassid(), dbo.getLabelCn());
/*     */       }
/*     */ 
/* 500 */       BMClassMeta[] bmClassMetas = getBMModelService().getAllClassMeta(ServiceHelper.getUserSvActCxt(request));
/* 501 */       for (int i = 0; i < bmClassMetas.length; i++) {
/* 502 */         BMClassMeta classMeta = bmClassMetas[i];
/* 503 */         if ((bmClassIdLabelCnMap.get(classMeta.getBmClassId()) != null) || 
/* 504 */           (classMeta.getIsSlave().booleanValue())) continue;
/* 505 */         String value = classMeta.getBmClassId();
/* 506 */         String label = classMeta.getLabelCn();
/* 507 */         IDrmLabelValue bmClassLabelValue = DrmEntityFactory.getInstance().createLabelValue(label, value);
/* 508 */         Map extmap = new HashMap();
/* 509 */         extmap.put("dbEntityClass", Boolean.valueOf(classMeta.isDbEntityClass()));
/* 510 */         bmClassLabelValue.setExtMapValue(extmap);
/* 511 */         bmClassMetaList.add(bmClassLabelValue);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 516 */     return (IDrmLabelValue[])(IDrmLabelValue[])bmClassMetaList.toArray(new IDrmLabelValue[0]);
/*     */   }
/*     */ 
/*     */   public IDrmLabelValue[] getAllClassMetaLabelValue(HttpServletRequest request)
/*     */   {
/* 523 */     BMClassMeta[] bmClassMetas = getBMModelService().getAllClassMeta(ServiceHelper.getUserSvActCxt(request));
/* 524 */     List bmClassMetaList = new ArrayList();
/*     */ 
/* 526 */     for (int i = 0; i < bmClassMetas.length; i++) {
/* 527 */       BMClassMeta classMeta = bmClassMetas[i];
/* 528 */       if (!classMeta.getIsSlave().booleanValue()) {
/* 529 */         String value = classMeta.getBmClassId();
/* 530 */         String label = classMeta.getLabelCn();
/* 531 */         IDrmLabelValue bmClassLabelValue = DrmEntityFactory.getInstance().createLabelValue(label, value);
/* 532 */         Map extmap = new HashMap();
/* 533 */         extmap.put("dbEntityClass", Boolean.valueOf(classMeta.isDbEntityClass()));
/* 534 */         bmClassLabelValue.setExtMapValue(extmap);
/* 535 */         bmClassMetaList.add(bmClassLabelValue);
/*     */       }
/*     */     }
/* 538 */     return (IDrmLabelValue[])(IDrmLabelValue[])bmClassMetaList.toArray(new IDrmLabelValue[0]);
/*     */   }
/*     */ 
/*     */   public boolean isHideNavigation(HttpServletRequest request) {
/* 542 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 543 */     return getVMModelService().isHideResNav(actionContext);
/*     */   }
/*     */ 
/*     */   public void setResNavVisible(HttpServletRequest request, String visible) {
/* 547 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 548 */     getVMModelService().setResNavVisible(actionContext, visible);
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 552 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static IDynResManageService getDynResManageService() {
/* 556 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 560 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DynResNavigationAction
 * JD-Core Version:    0.6.0
 */