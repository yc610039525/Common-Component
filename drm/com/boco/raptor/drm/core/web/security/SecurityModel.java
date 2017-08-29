/*     */ package com.boco.raptor.drm.core.web.security;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.common.service.impl.ServiceActionContext;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*     */ import com.boco.raptor.drm.core.service.security.IAuthenticationService;
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
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
/*     */ public class SecurityModel
/*     */ {
/*  31 */   private static SecurityModel instance = new SecurityModel();
/*     */   private IUserDetails admin;
/*  33 */   private Map<String, IAuthentication> authenticationMap = new HashMap();
/*     */ 
/*     */   public SecurityModel() {
/*  36 */     this.admin = getAuthenticationService().getAdmin(new ServiceActionContext());
/*     */   }
/*     */ 
/*     */   public static SecurityModel getInstance() {
/*  40 */     return instance;
/*     */   }
/*     */ 
/*     */   public IUserDetails getAdmin() {
/*  44 */     return this.admin;
/*     */   }
/*     */ 
/*     */   public IAuthentication getAuthentication(String userId) {
/*  48 */     IAuthentication auth = (IAuthentication)this.authenticationMap.get(userId);
/*  49 */     if (auth == null) {
/*  50 */       auth = getAuthenticationService().getAuthentication(new ServiceActionContext(), userId);
/*  51 */       this.authenticationMap.put(userId, auth);
/*     */     }
/*  53 */     return auth;
/*     */   }
/*     */ 
/*     */   public void initAuthentication(String userId) {
/*  57 */     IAuthentication auth = getAuthenticationService().getAuthentication(new ServiceActionContext(), userId);
/*  58 */     this.authenticationMap.put(userId, auth);
/*  59 */     LogHome.getLog().info("初始化用户[" + auth.getUserDetails().getTruename() + "]的安全认证信息");
/*     */   }
/*     */ 
/*     */   public IAuthentication getCurrentUserAuthentication(HttpServletRequest request)
/*     */   {
/*  66 */     IAuthentication auth = null;
/*  67 */     Object userId = request.getSession().getAttribute("SESSION_USER_ID");
/*  68 */     if ((userId != null) && (userId.toString().trim().length() > 0)) {
/*  69 */       auth = getAuthentication(userId.toString());
/*     */     }
/*  71 */     return auth;
/*     */   }
/*     */ 
/*     */   private static IAuthenticationService getAuthenticationService() {
/*  75 */     return (IAuthenticationService)ServiceHomeFactory.getInstance().getService("AuthenticationService");
/*     */   }
/*     */ 
/*     */   public Map getOnlineUses(HttpServletRequest request)
/*     */   {
/*  83 */     return this.authenticationMap;
/*     */   }
/*     */ 
/*     */   public void deleteLoginOutUserFormMap(HttpServletRequest request)
/*     */   {
/*  91 */     Object userId = request.getSession().getAttribute("SESSION_USER_ID");
/*  92 */     LogHome.getLog().info("退出的用户为[" + userId + "]！");
/*  93 */     if ((userId != null) && (userId.toString().trim().length() > 0) && 
/*  94 */       (this.authenticationMap != null) && (this.authenticationMap.containsKey(userId.toString()))) {
/*  95 */       LogHome.getLog().info("正在删除退出用户[" + userId.toString() + "]！");
/*  96 */       this.authenticationMap.remove(userId.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public List getUserDistricts(HttpServletRequest request)
/*     */   {
/* 108 */     List roleDisList = new ArrayList();
/* 109 */     IAuthentication auth = getCurrentUserAuthentication(request);
/* 110 */     if (auth != null) {
/* 111 */       Map dimensionActionNames = auth.getDimensionActionNames();
/* 112 */       if ((dimensionActionNames != null) && (dimensionActionNames.size() > 0)) {
/* 113 */         Map actionNames = (Map)dimensionActionNames.get("DISTRICT");
/* 114 */         if ((actionNames != null) && (actionNames.size() > 0)) {
/* 115 */           Iterator it = actionNames.keySet().iterator();
/* 116 */           while (it.hasNext()) {
/* 117 */             String districtCuid = (String)it.next();
/* 118 */             List actionList = (List)actionNames.get(districtCuid);
/* 119 */             if ((actionList != null) && (actionList.size() > 0)) {
/* 120 */               roleDisList.add(districtCuid);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 126 */     return roleDisList;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.security.SecurityModel
 * JD-Core Version:    0.6.0
 */