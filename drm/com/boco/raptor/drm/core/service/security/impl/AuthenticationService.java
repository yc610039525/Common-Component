/*     */ package com.boco.raptor.drm.core.service.security.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserSecurityException;
/*     */ import com.boco.raptor.common.service.AbstractService;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.common.service.impl.ServiceActionContext;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthenticationService;
/*     */ import com.boco.raptor.drm.core.service.security.IObjectValidService;
/*     */ import com.boco.raptor.drm.core.service.security.ISecurityService;
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AuthenticationService extends AbstractService
/*     */   implements IAuthenticationService
/*     */ {
/*  47 */   private IUserDetails admin = new UserDetails("SYS_USER-0", "admin", "管理员", "boco", true);
/*  48 */   private boolean debug = false;
/*     */   private IObjectValidService objectValidService;
/*     */   private ISecurityService securityService;
/*  52 */   private Map<String, IAuthentication> authenticationMap = new HashMap();
/*     */ 
/*  54 */   public AuthenticationService() { super("AuthenticationService"); }
/*     */ 
/*     */   public void addAuthentication(IServiceActionContext actionContext, IAuthentication authentication)
/*     */   {
/*  58 */     this.authenticationMap.put(authentication.getUserDetails().getUserId(), authentication);
/*     */   }
/*     */ 
/*     */   public void removeAuthentication(IServiceActionContext actionContext, IAuthentication authentication) {
/*  62 */     this.authenticationMap.remove(authentication.getUserDetails().getUserId());
/*     */   }
/*     */ 
/*     */   public void removeAuthentication(IServiceActionContext actionContext, String userId) {
/*  66 */     this.authenticationMap.remove(userId);
/*     */   }
/*     */ 
/*     */   public IAuthentication getAuthentication(IServiceActionContext actionContext, String userId) {
/*  70 */     return (IAuthentication)this.authenticationMap.get(userId);
/*     */   }
/*     */ 
/*     */   public void isActionValid(String userId, String actionName)
/*     */   {
/*  77 */     AuthDebug("开始方法调用权限认证,用户：" + userId + ",方法：" + actionName);
/*  78 */     IAuthentication auth = getAuthentication(new ServiceActionContext(), userId);
/*  79 */     if ((auth != null) && (!auth.getUserDetails().getIsAdmin()))
/*     */     {
/*  81 */       actionName = ActionHelper.getGenericActionName(actionName);
/*     */ 
/*  83 */       List acceptActionNames = auth.getActionNames();
/*  84 */       if (this.debug) {
/*  85 */         String acs = "";
/*  86 */         for (int i = 0; i < acceptActionNames.size(); i++) {
/*  87 */           if (i == 0)
/*  88 */             acs = acceptActionNames.get(i).toString();
/*     */           else {
/*  90 */             acs = acs + "," + acceptActionNames.get(i).toString();
/*     */           }
/*     */         }
/*  93 */         AuthDebug("用户可操作的方法：" + acs);
/*     */       }
/*  95 */       if (!acceptActionNames.contains(actionName)) {
/*  96 */         LogHome.getLog().info("权限认证：用户[" + userId + "]没有调用]" + actionName + "]方法的权限");
/*  97 */         throw new UserSecurityException();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void isObjectValid(IServiceActionContext serviceActionContext, IDrmDataObject dbo)
/*     */     throws Exception
/*     */   {
/* 110 */     AuthDebug("维度权限以及对象权限验证,用户：" + serviceActionContext.getUserId() + ",业务类：" + dbo.getBmClassId());
/* 111 */     IAuthentication auth = getAuthentication(new ServiceActionContext(), serviceActionContext.getUserId());
/* 112 */     if ((auth != null) && (!auth.getUserDetails().getIsAdmin()))
/* 113 */       if (this.objectValidService != null)
/*     */       {
/* 115 */         this.objectValidService.isObjectValid(serviceActionContext, dbo);
/*     */       }
/*     */       else
/*     */       {
/* 119 */         Map positiveObjects = auth.getPositiveObjects();
/* 120 */         Map reverseObjects = auth.getReverseObjects();
/* 121 */         if (positiveObjects != null)
/*     */         {
/* 123 */           if (this.debug) {
/* 124 */             String[] cuids = new String[positiveObjects.size()];
/* 125 */             positiveObjects.keySet().toArray(cuids);
/* 126 */             for (int i = 0; i < cuids.length; i++) {
/* 127 */               AuthDebug("用户具有的正向对象：" + cuids[i]);
/*     */             }
/*     */           }
/* 130 */           if (positiveObjects.get(dbo.getCuid()) != null) {
/* 131 */             LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]正向权限，认证成功！");
/* 132 */             return;
/*     */           }
/* 134 */           LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]正向权限，认证失败！");
/*     */         }
/* 136 */         else if (reverseObjects != null)
/*     */         {
/* 138 */           if (this.debug) {
/* 139 */             String[] cuids = new String[reverseObjects.size()];
/* 140 */             reverseObjects.keySet().toArray(cuids);
/* 141 */             for (int i = 0; i < cuids.length; i++) {
/* 142 */               AuthDebug("用户具有的反向对象：" + cuids[i]);
/*     */             }
/*     */           }
/* 145 */           if (reverseObjects.get(dbo.getCuid()) != null) {
/* 146 */             LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]反向权限，认证失败！");
/*     */           } else {
/* 148 */             LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]反向权限，认证成功！");
/* 149 */             return;
/*     */           }
/*     */         } else {
/* 152 */           LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象权限没有定义,认证成功！");
/*     */         }
/*     */ 
/* 155 */         IServiceActionContext actionContext = new ServiceActionContext();
/*     */ 
/* 157 */         if (auth.getDimensionObject() != null) {
/* 158 */           String[] dimensionBmClassIds = new String[auth.getDimensionObject().keySet().toArray().length];
/* 159 */           auth.getDimensionObject().keySet().toArray(dimensionBmClassIds);
/* 160 */           if (this.debug) {
/* 161 */             String dimension = "";
/* 162 */             for (int i = 0; i < dimensionBmClassIds.length; i++) {
/* 163 */               if (i == 0)
/* 164 */                 dimension = dimensionBmClassIds[i];
/*     */               else {
/* 166 */                 dimension = dimension + "," + dimensionBmClassIds[i];
/*     */               }
/*     */             }
/* 169 */             AuthDebug("用户具有的对象维度：" + dimension);
/*     */           }
/* 171 */           if (dimensionBmClassIds.length > 0) {
/* 172 */             BMClassMeta bmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), dbo.getBmClassId());
/* 173 */             if (bmClassMeta != null) {
/* 174 */               for (int i = 0; i < dimensionBmClassIds.length; i++) {
/* 175 */                 String dimensionBmClassId = dimensionBmClassIds[i];
/* 176 */                 List dimensionCuidList = new ArrayList();
/* 177 */                 AuthDebug("对业务类[" + bmClassMeta.getBmClassId() + "]进行对象维度[" + dimensionBmClassId + "]的认证");
/* 178 */                 dimensionCuidList = getDimensionCuid(bmClassMeta, dimensionBmClassId, dbo);
/* 179 */                 if (dimensionCuidList.size() > 0) {
/* 180 */                   List cuids = (List)auth.getDimensionObject().get(dimensionBmClassId);
/* 181 */                   int m = 0; if (m < dimensionCuidList.size()) {
/* 182 */                     String dimensionCuid = dimensionCuidList.get(m).toString();
/* 183 */                     AuthDebug("在对象[" + dbo.getCuid() + "]中找到关联的[" + dimensionBmClassId + "]维度[" + dimensionCuid + "]");
/* 184 */                     if (!cuids.contains(dimensionCuid)) {
/* 185 */                       BMClassMeta dimensionBmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), dimensionBmClassId);
/*     */ 
/* 188 */                       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 189 */                       IDrmDataObject dro = DrmEntityFactory.getInstance().createDataObject();
/* 190 */                       dro.setBmClassId(dimensionBmClassMeta.getBmClassId());
/* 191 */                       dro.setCuid(dimensionCuid);
/* 192 */                       dro = getDynResManageService().getDynObject(actionContext, queryContext, dro);
/* 193 */                       Object labelCn = null;
/* 194 */                       if (dro != null) {
/* 195 */                         labelCn = dro.getAttrValue(dimensionBmClassMeta.getLabelAttrId());
/*     */                       }
/* 197 */                       if (labelCn != null) {
/* 198 */                         throw new UserSecurityException("对不起，您没有" + dimensionBmClassMeta.getLabelCn() + "维度[" + labelCn.toString() + "]的权限!");
/*     */                       }
/*     */ 
/* 201 */                       throw new UserSecurityException("对不起，您没有" + dimensionBmClassMeta.getLabelCn() + "维度的权限!");
/*     */                     }
/*     */ 
/* 204 */                     LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]对象维度权限，认证成功！");
/*     */ 
/* 206 */                     return;
/*     */                   }
/*     */                 }
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 217 */         if (auth.getDimensionActionNames() != null) {
/* 218 */           String[] dimensionBmClassIds = new String[auth.getDimensionActionNames().keySet().toArray().length];
/* 219 */           auth.getDimensionActionNames().keySet().toArray(dimensionBmClassIds);
/* 220 */           if (this.debug) {
/* 221 */             String dimension = "";
/* 222 */             for (int i = 0; i < dimensionBmClassIds.length; i++) {
/* 223 */               if (i == 0)
/* 224 */                 dimension = dimensionBmClassIds[i];
/*     */               else {
/* 226 */                 dimension = dimension + "," + dimensionBmClassIds[i];
/*     */               }
/*     */             }
/* 229 */             AuthDebug("用户具有的方法维度：" + dimension);
/*     */           }
/*     */ 
/* 232 */           if (dimensionBmClassIds.length > 0)
/*     */           {
/* 234 */             BMClassMeta bmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), dbo.getBmClassId());
/* 235 */             if (bmClassMeta != null)
/* 236 */               for (int i = 0; i < dimensionBmClassIds.length; i++)
/*     */               {
/* 238 */                 List dimensionCuidList = new ArrayList();
/* 239 */                 String dimensionBmClassId = dimensionBmClassIds[i];
/* 240 */                 AuthDebug("对业务类[" + bmClassMeta.getBmClassId() + "]进行方法维度[" + dimensionBmClassId + "]的认证");
/* 241 */                 dimensionCuidList = getDimensionCuid(bmClassMeta, dimensionBmClassId, dbo);
/* 242 */                 if (dimensionCuidList.size() <= 0) {
/*     */                   continue;
/*     */                 }
/* 245 */                 Map cuidActionNames = (Map)auth.getDimensionActionNames().get(dimensionBmClassId);
/* 246 */                 if (cuidActionNames != null) {
/* 247 */                   int m = 0; if (m < dimensionCuidList.size()) {
/* 248 */                     String dimensionCuid = dimensionCuidList.get(m).toString();
/* 249 */                     AuthDebug("在对象[" + dbo.getCuid() + "]中找到关联的[" + dimensionBmClassId + "]维度[" + dimensionCuid + "]");
/* 250 */                     List actionNames = (List)cuidActionNames.get(dimensionCuid);
/* 251 */                     String actionName = ActionHelper.getGenericActionName(serviceActionContext.getActionId());
/* 252 */                     if ((actionNames == null) || (!actionNames.contains(actionName))) {
/* 253 */                       BMClassMeta dimensionBmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), dimensionBmClassId);
/*     */ 
/* 255 */                       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 256 */                       IDrmDataObject dro = DrmEntityFactory.getInstance().createDataObject();
/* 257 */                       dro.setBmClassId(dimensionBmClassMeta.getBmClassId());
/* 258 */                       dro.setCuid(dimensionCuid);
/* 259 */                       dro = getDynResManageService().getDynObject(actionContext, queryContext, dro);
/* 260 */                       Object labelCn = null;
/* 261 */                       if (dro != null) {
/* 262 */                         labelCn = dro.getAttrValue(dimensionBmClassMeta.getLabelAttrId());
/*     */                       }
/* 264 */                       if (labelCn != null) {
/* 265 */                         throw new UserSecurityException("对不起，您没有" + dimensionBmClassMeta.getLabelCn() + "维度[" + labelCn.toString() + "]的权限!");
/*     */                       }
/*     */ 
/* 268 */                       throw new UserSecurityException("对不起，您没有" + dimensionBmClassMeta.getLabelCn() + "维度的权限!");
/*     */                     }
/*     */ 
/* 271 */                     LogHome.getLog().info("权限认证：用户[" + serviceActionContext.getUserId() + "]对象[" + dbo.getBmClassId() + ":" + dbo.getCuid() + "]方法维度权限，认证成功！");
/*     */ 
/* 273 */                     return;
/*     */                   }
/*     */                 }
/*     */               }
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private List<String> getDimensionCuid(BMClassMeta bmClassMeta, String dimensionBmClassId, IDrmDataObject dbo)
/*     */   {
/* 290 */     IServiceActionContext actionContext = new ServiceActionContext();
/* 291 */     List dimensionCuidList = new ArrayList();
/* 292 */     List attrMetas = bmClassMeta.getRelatedClassAttrMeta(dimensionBmClassId);
/* 293 */     if ((attrMetas != null) && (attrMetas.size() > 0))
/*     */     {
/* 295 */       LogHome.getLog().info("权限认证：对象[" + dbo.getBmClassId() + "]直接关联[" + dimensionBmClassId + "]维度认证 === === ===");
/*     */ 
/* 297 */       Map attrIdmap = new HashMap();
/* 298 */       for (int k = 0; k < attrMetas.size(); k++) {
/* 299 */         BMAttrMeta attrMeta = (BMAttrMeta)attrMetas.get(k);
/*     */ 
/* 301 */         if (attrMeta.getIsDimension().booleanValue()) {
/* 302 */           String attrId = attrMeta.getAttrId();
/* 303 */           String dimensionCuid = (String)dbo.getAttrValue(attrId);
/* 304 */           if ((dimensionCuid != null) && (dimensionCuid.trim().length() > 0)) {
/* 305 */             attrIdmap.put(attrId, dimensionCuid);
/* 306 */             if ((attrMeta.getUpperAttrIds() != null) && (attrMeta.getUpperAttrIds().size() > 0)) {
/* 307 */               for (int i = 0; i < attrMeta.getUpperAttrIds().size(); i++) {
/* 308 */                 String upperAttrId = (String)attrMeta.getUpperAttrIds().get(i);
/* 309 */                 attrIdmap.remove(upperAttrId);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 315 */       Iterator it = attrIdmap.keySet().iterator();
/* 316 */       while (it.hasNext()) {
/* 317 */         String attrId = (String)it.next();
/* 318 */         String dimensionCuid = (String)attrIdmap.get(attrId);
/* 319 */         dimensionCuidList.add(dimensionCuid);
/*     */       }
/*     */     }
/*     */     else {
/* 323 */       LogHome.getLog().info("权限认证：对象[" + dbo.getBmClassId() + "]间接关联[" + dimensionBmClassId + "]维度认证 >>> >>> >>>");
/* 324 */       Map allAttrMetas = bmClassMeta.getAllAttrMetas();
/* 325 */       if (allAttrMetas != null) {
/* 326 */         Iterator i = allAttrMetas.keySet().iterator();
/* 327 */         while (i.hasNext()) {
/* 328 */           String attrId = (String)i.next();
/* 329 */           BMAttrMeta attrMeta = (BMAttrMeta)allAttrMetas.get(attrId);
/* 330 */           Object attrValue = dbo.getAttrValue(attrId);
/*     */ 
/* 332 */           if ((attrMeta.getIsDimension().booleanValue()) && (attrValue != null)) {
/* 333 */             Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 334 */             if (relatedAttrIds != null) {
/* 335 */               Iterator k = relatedAttrIds.keySet().iterator();
/* 336 */               while (k.hasNext()) {
/* 337 */                 String relatedBmClassId = (String)k.next();
/* 338 */                 String relatedAttrId = (String)relatedAttrIds.get(relatedBmClassId);
/*     */ 
/* 340 */                 IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 341 */                 DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 342 */                 query.setBmClassId(relatedBmClassId);
/* 343 */                 query.addQueryCondExps(new DrmQueryAttrCond(relatedAttrId, "=", attrValue.toString()));
/* 344 */                 IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(actionContext, queryContext, query);
/* 345 */                 List rows = rs.getResultSet();
/* 346 */                 if (rows.size() > 0) {
/* 347 */                   IDrmDataObject relateddro = ((IDrmQueryRow)rows.get(0)).getResultDbo(relatedBmClassId);
/* 348 */                   if (relateddro != null) {
/* 349 */                     BMClassMeta relatedBmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), relatedBmClassId);
/* 350 */                     dimensionCuidList = getDimensionCuid(relatedBmClassMeta, dimensionBmClassId, relateddro);
/* 351 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 360 */     return dimensionCuidList;
/*     */   }
/*     */ 
/*     */   public String getBmClassMetaAuthenticationSql(IServiceActionContext serviceActionContext)
/*     */   {
/* 367 */     String whereSql = null;
/* 368 */     if ((serviceActionContext == null) || (serviceActionContext.getUserId() == null) || (serviceActionContext.getUserId().trim().length() == 0)) {
/* 369 */       return whereSql;
/*     */     }
/* 371 */     IAuthentication auth = getAuthentication(new ServiceActionContext(), serviceActionContext.getUserId());
/* 372 */     if ((auth != null) && (!auth.getUserDetails().getIsAdmin()))
/*     */     {
/* 374 */       String queryBmClassId = serviceActionContext.getBmClassId();
/* 375 */       if ((queryBmClassId != null) && (queryBmClassId.trim().length() > 0))
/*     */       {
/* 378 */         BMClassMeta bmClassMeta = getBMModelService().getClassMeta(new ServiceActionContext(), queryBmClassId);
/*     */ 
/* 380 */         Map relatedAttrIdAndCuids = new HashMap();
/* 381 */         if (bmClassMeta != null)
/*     */         {
/* 383 */           if (auth.getDimensionActionNames() != null) {
/* 384 */             String[] dimensionActionNameBmClassIds = new String[auth.getDimensionActionNames().keySet().toArray().length];
/* 385 */             auth.getDimensionActionNames().keySet().toArray(dimensionActionNameBmClassIds);
/* 386 */             for (int i = 0; i < dimensionActionNameBmClassIds.length; i++) {
/* 387 */               List attrMetas = bmClassMeta.getRelatedClassAttrMeta(dimensionActionNameBmClassIds[i]);
/* 388 */               if (attrMetas != null) {
/* 389 */                 Map cuidActionNames = (Map)auth.getDimensionActionNames().get(dimensionActionNameBmClassIds[i]);
/* 390 */                 if (cuidActionNames != null) {
/* 391 */                   List cuidsList = new ArrayList();
/* 392 */                   Iterator iter = cuidActionNames.keySet().iterator();
/* 393 */                   while (iter.hasNext()) {
/* 394 */                     String cuid = (String)iter.next();
/* 395 */                     List actionNames = (List)cuidActionNames.get(cuid);
/* 396 */                     if (((actionNames != null) && (actionNames.size() > 0)) || (queryBmClassId.equals("DISTRICT"))) {
/* 397 */                       cuidsList.add(cuid);
/*     */                     }
/*     */                   }
/* 400 */                   String[] cuids = new String[cuidsList.size()];
/* 401 */                   cuidsList.toArray(cuids);
/* 402 */                   for (int k = 0; k < attrMetas.size(); k++)
/*     */                   {
/* 404 */                     if (((attrMetas.size() != 1) && (((BMAttrMeta)attrMetas.get(k)).getHasChildAttrId().booleanValue())) || 
/* 405 */                       (!((BMAttrMeta)attrMetas.get(k)).getIsDimension().booleanValue())) continue;
/* 406 */                     relatedAttrIdAndCuids.put(dimensionActionNameBmClassIds[i] + "." + ((BMAttrMeta)attrMetas.get(k)).getAttrId(), cuids);
/*     */                   }
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 416 */           if (auth.getDimensionObject() != null) {
/* 417 */             String[] dimensionObjectBmClassIds = new String[auth.getDimensionObject().keySet().toArray().length];
/* 418 */             auth.getDimensionObject().keySet().toArray(dimensionObjectBmClassIds);
/* 419 */             for (int i = 0; i < dimensionObjectBmClassIds.length; i++) {
/* 420 */               List attrMetas = bmClassMeta.getRelatedClassAttrMeta(dimensionObjectBmClassIds[i]);
/* 421 */               if (attrMetas != null) {
/* 422 */                 List cuidList = (List)auth.getDimensionObject().get(dimensionObjectBmClassIds[i]);
/* 423 */                 if (cuidList != null) {
/* 424 */                   String[] cuids = new String[cuidList.size()];
/* 425 */                   cuidList.toArray(cuids);
/* 426 */                   for (int k = 0; k < attrMetas.size(); k++)
/*     */                   {
/* 428 */                     if (((attrMetas.size() != 1) && (((BMAttrMeta)attrMetas.get(k)).getHasChildAttrId().booleanValue())) || 
/* 429 */                       (!((BMAttrMeta)attrMetas.get(k)).getIsDimension().booleanValue())) continue;
/* 430 */                     relatedAttrIdAndCuids.put(dimensionObjectBmClassIds[i] + "." + ((BMAttrMeta)attrMetas.get(k)).getAttrId(), cuids);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 440 */         if (relatedAttrIdAndCuids.size() > 0) {
/* 441 */           Iterator it = relatedAttrIdAndCuids.keySet().iterator();
/* 442 */           while (it.hasNext()) {
/* 443 */             String key = (String)it.next();
/* 444 */             String bmClassId = key.split("\\.")[0];
/* 445 */             String attrId = key.split("\\.")[1];
/* 446 */             String[] cuids = (String[])relatedAttrIdAndCuids.get(key);
/* 447 */             String cuidSql = "";
/* 448 */             for (int i = 0; i < cuids.length; i++) {
/* 449 */               if (i == 0)
/* 450 */                 cuidSql = "'" + cuids[i] + "'";
/*     */               else {
/* 452 */                 cuidSql = cuidSql + ",'" + cuids[i] + "'";
/*     */               }
/*     */             }
/* 455 */             if (whereSql == null) {
/* 456 */               if (cuids.length == 1)
/* 457 */                 whereSql = "(" + attrId + " = " + cuidSql + " or " + attrId + " is null or " + attrId + "=''";
/*     */               else {
/* 459 */                 whereSql = "(" + attrId + " in (" + cuidSql + ")" + " or " + attrId + " is null or " + attrId + "=''";
/*     */               }
/*     */             }
/* 462 */             else if (cuids.length == 1)
/* 463 */               whereSql = whereSql + " and (" + attrId + " = " + cuidSql + " or " + attrId + " is null or " + attrId + "=''";
/*     */             else {
/* 465 */               whereSql = whereSql + " and (" + attrId + " in (" + cuidSql + ")" + " or " + attrId + " is null or " + attrId + "=''";
/*     */             }
/*     */ 
/* 469 */             if (queryBmClassId.equals(bmClassId)) {
/* 470 */               if (cuids.length == 1)
/* 471 */                 whereSql = " CUID = " + cuidSql;
/*     */               else
/* 473 */                 whereSql = " CUID in (" + cuidSql + ")";
/*     */             }
/*     */             else {
/* 476 */               whereSql = whereSql + ")";
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 481 */         if (auth.getDimensionObject().keySet().contains(queryBmClassId)) {
/* 482 */           List cuids = (List)auth.getDimensionObject().get(queryBmClassId);
/* 483 */           if (cuids != null) {
/* 484 */             String cuidSql = null;
/* 485 */             String attrId = "CUID";
/* 486 */             for (int i = 0; i < cuids.size(); i++) {
/* 487 */               if (i == 0)
/* 488 */                 cuidSql = "'" + (String)cuids.get(i) + "'";
/*     */               else {
/* 490 */                 cuidSql = cuidSql + ",'" + (String)cuids.get(i) + "'";
/*     */               }
/*     */             }
/* 493 */             if (cuids.size() == 1)
/* 494 */               whereSql = whereSql + " and (" + attrId + " = " + cuidSql + " or " + attrId + " is null or " + attrId + "='')";
/*     */             else
/* 496 */               whereSql = whereSql + " and (" + attrId + " in (" + cuidSql + ")" + " or " + attrId + " is null or " + attrId + "='')";
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 501 */         LogHome.getLog().info("IServiceActionContext中没有设置bmClassId，则不构造认证过滤sql语句");
/*     */       }
/* 503 */       LogHome.getLog().info("权限认证：查询[" + queryBmClassId + "]条件过滤:" + whereSql);
/*     */     }
/* 505 */     return whereSql;
/*     */   }
/*     */ 
/*     */   private void AuthDebug(String msg) {
/* 509 */     if (this.debug)
/* 510 */       LogHome.getLog().info("认证调试信息：" + msg);
/*     */   }
/*     */ 
/*     */   public IUserDetails getAdmin(IServiceActionContext actionContext)
/*     */   {
/* 515 */     return this.admin;
/*     */   }
/*     */ 
/*     */   public void setAdmin(IUserDetails admin) {
/* 519 */     this.admin = admin;
/* 520 */     IAuthentication auth = new Authentication();
/* 521 */     auth.setUserDetails(this.admin);
/* 522 */     this.authenticationMap.put(this.admin.getUserId(), auth);
/*     */   }
/*     */ 
/*     */   public void setObjectValidService(IObjectValidService objectValidService) {
/* 526 */     this.objectValidService = objectValidService;
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean debug) {
/* 530 */     this.debug = debug;
/*     */   }
/*     */ 
/*     */   public void setSecurityService(ISecurityService securityService) {
/* 534 */     this.securityService = securityService;
/*     */   }
/*     */ 
/*     */   private static IBMModelService getBMModelService() {
/* 538 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private static IDynResManageService getDynResManageService() {
/* 542 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   public ISecurityService getSecurityService() {
/* 546 */     return this.securityService;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.impl.AuthenticationService
 * JD-Core Version:    0.6.0
 */