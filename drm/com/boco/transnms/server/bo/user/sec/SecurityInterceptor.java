/*    */ package com.boco.transnms.server.bo.user.sec;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.transnms.client.model.base.IBoCommand;
/*    */ import com.boco.transnms.client.model.base.ICmdInterceptor;
/*    */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*    */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*    */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*    */ import com.boco.transnms.server.bo.ibo.IBasicSecurityBO;
/*    */ 
/*    */ public class SecurityInterceptor
/*    */   implements ICmdInterceptor
/*    */ {
/*    */   public void doCommand(IBoCommand cmd)
/*    */     throws UserException
/*    */   {
/* 17 */     IBoActionContext context = ActionHelper.getCmdActionContext(cmd);
/* 18 */     if ((context != null) && (!context.isActionChecked())) {
/* 19 */       IBasicSecurityBO securityBO = (IBasicSecurityBO)BoHomeFactory.getInstance().getBO("ISecurityBO");
/* 20 */       securityBO.isActionValid(context.getUserId(), context.getActionName());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.user.sec.SecurityInterceptor
 * JD-Core Version:    0.6.0
 */