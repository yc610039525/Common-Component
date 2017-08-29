/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class BoInterceptor
/*    */   implements ICmdInterceptor
/*    */ {
/*    */   public void doCommand(IBoCommand cmd)
/*    */     throws UserException
/*    */   {
/* 24 */     BoCmdContext context = cmd.getCmdContext();
/* 25 */     String boName = context.getBoName();
/* 26 */     String methodName = context.getMethodName();
/* 27 */     String hostIp = cmd.getHostIP();
/* 28 */     String userName = cmd.getHostName();
/* 29 */     boolean isExportBo = ExportBoManager.getInstance().contains(boName);
/* 30 */     if (!isExportBo) {
/* 31 */       LogHome.getLog().debug("来自" + hostIp + "用户" + userName + "的BO调用" + boName + "." + methodName + "非本服务器发布的服务！");
/*    */ 
/* 34 */       throw new UserException("该服务器未发布" + boName + "服务！");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.BoInterceptor
 * JD-Core Version:    0.6.0
 */