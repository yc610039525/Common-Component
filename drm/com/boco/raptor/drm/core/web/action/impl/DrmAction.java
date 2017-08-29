/*    */ package com.boco.raptor.drm.core.web.action.impl;
/*    */ 
/*    */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*    */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*    */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*    */ 
/*    */ public class DrmAction
/*    */ {
/*  8 */   public IBMModelService bmModelService = getBMModelService();
/*  9 */   public IDynResManageService dynResManageService = getDynResManageService();
/*    */ 
/*    */   private IBMModelService getBMModelService()
/*    */   {
/* 14 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*    */   }
/*    */ 
/*    */   private IDynResManageService getDynResManageService() {
/* 18 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.impl.DrmAction
 * JD-Core Version:    0.6.0
 */