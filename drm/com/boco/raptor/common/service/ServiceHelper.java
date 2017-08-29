/*    */ package com.boco.raptor.common.service;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.raptor.common.bean.EntityBeanFactory;
/*    */ import com.boco.raptor.common.service.impl.ServiceActionContext;
/*    */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*    */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*    */ import com.boco.raptor.drm.core.web.security.SecurityModel;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ServiceHelper
/*    */ {
/* 30 */   private static final String SV_ACT_CXT_ENTITY_NAME = IServiceActionContext.class.getSimpleName();
/*    */   private static final String DEFAULT_USER_ID = "SYS_USER-0";
/*    */ 
/*    */   public static IServiceActionContext createSvActCxt()
/*    */   {
/* 37 */     IServiceActionContext context = null;
/* 38 */     if (EntityBeanFactory.getInstance().isEntityRegistered(SV_ACT_CXT_ENTITY_NAME)) {
/*    */       try {
/* 40 */         context = (IServiceActionContext)EntityBeanFactory.getInstance().createEntity(SV_ACT_CXT_ENTITY_NAME);
/* 41 */         context.setUserId(SecurityModel.getInstance().getAdmin().getUserId());
/*    */       } catch (Exception ex) {
/* 43 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */     }
/* 46 */     return context == null ? new ServiceActionContext() : context;
/*    */   }
/*    */ 
/*    */   public static IServiceActionContext getUserSvActCxt(HttpServletRequest request) {
/* 50 */     IServiceActionContext context = null;
/* 51 */     if (EntityBeanFactory.getInstance().isEntityRegistered(SV_ACT_CXT_ENTITY_NAME)) {
/*    */       try {
/* 53 */         context = (IServiceActionContext)EntityBeanFactory.getInstance().createEntity(SV_ACT_CXT_ENTITY_NAME);
/*    */       } catch (Exception ex) {
/* 55 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */     }
/* 58 */     context = context == null ? new ServiceActionContext() : context;
/* 59 */     Object userId = request.getSession().getAttribute("SESSION_USER_ID");
/* 60 */     if (userId != null)
/*    */     {
/* 62 */       IAuthentication auth = SecurityModel.getInstance().getAuthentication(userId.toString());
/* 63 */       context.setUserId(auth.getUserDetails().getUserId());
/* 64 */       context.setUserName(auth.getUserDetails().getTruename());
/*    */     } else {
/* 66 */       context.setUserId(SecurityModel.getInstance().getAdmin().getUserId());
/* 67 */       context.setUserName(SecurityModel.getInstance().getAdmin().getTruename());
/*    */     }
/* 69 */     return context;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.ServiceHelper
 * JD-Core Version:    0.6.0
 */