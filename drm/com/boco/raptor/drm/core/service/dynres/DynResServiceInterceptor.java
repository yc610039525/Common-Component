/*    */ package com.boco.raptor.drm.core.service.dynres;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.common.service.IService;
/*    */ import com.boco.raptor.common.service.IServiceActionContext;
/*    */ import com.boco.raptor.common.service.IServiceInterceptor;
/*    */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*    */ import com.boco.raptor.drm.core.plugin.IDynBmClassInterceptor;
/*    */ import com.boco.raptor.drm.core.service.security.IAuthenticationService;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class DynResServiceInterceptor
/*    */   implements IServiceInterceptor, Cloneable
/*    */ {
/*    */   private IDynResManageService service;
/* 20 */   private final List<String> serviceIds = new ArrayList();
/*    */   private Map<String, IDynBmClassInterceptor> beforeInterceptors;
/*    */   private Map<String, IDynBmClassInterceptor> afterInterceptors;
/*    */ 
/*    */   public DynResServiceInterceptor()
/*    */   {
/* 25 */     this.serviceIds.add("DynResManageService");
/*    */   }
/*    */ 
/*    */   public Object invoke(Object proxy, Method method, Object[] args) throws UserException {
/*    */     try {
/* 30 */       IServiceActionContext actionContext = null;
/* 31 */       if ((args != null) && (args.length > 0))
/*    */       {
/* 33 */         actionContext = (IServiceActionContext)args[0];
/* 34 */         if (actionContext != null) {
/* 35 */           actionContext.setActionId(actionContext.getBmClassId() + "." + method.getName());
/* 36 */           actionContext.setServiceId(this.service.getServiceId());
/* 37 */           LogHome.getLog().info("服务调用：" + actionContext.toString());
/*    */ 
/* 39 */           if ((actionContext.getUserId() != null) && (actionContext.getUserId().trim().length() > 0) && (actionContext.getBmClassId() != null) && (actionContext.getBmClassId().trim().length() > 0) && (actionContext.getBmClassId().indexOf("DRM_") != 0))
/*    */           {
/* 42 */             IAuthenticationService authService = (IAuthenticationService)ServiceHomeFactory.getInstance().getService("AuthenticationService");
/* 43 */             if (authService != null) {
/* 44 */               authService.isActionValid(actionContext.getUserId(), actionContext.getActionId());
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/* 49 */       return method.invoke(this.service, args);
/*    */     } catch (InvocationTargetException ex) {
/* 51 */       throw new UserException(ex.getTargetException().getMessage()); } catch (Throwable ex) {
/*    */     }
/* 53 */     throw new UserException(ex.getMessage());
/*    */   }
/*    */ 
/*    */   public List<String> getInterceptServiceIds()
/*    */   {
/* 58 */     return this.serviceIds;
/*    */   }
/*    */ 
/*    */   public void setSerivce(IService service) {
/* 62 */     this.service = ((IDynResManageService)service);
/*    */   }
/*    */ 
/*    */   public IServiceInterceptor clone() {
/* 66 */     return this;
/*    */   }
/*    */ 
/*    */   public void setBeforeInterceptors(Map<String, IDynBmClassInterceptor> interceptors) {
/* 70 */     this.beforeInterceptors = interceptors;
/*    */   }
/*    */ 
/*    */   public void setAfterInterceptors(Map<String, IDynBmClassInterceptor> interceptors) {
/* 74 */     this.afterInterceptors = interceptors;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.DynResServiceInterceptor
 * JD-Core Version:    0.6.0
 */