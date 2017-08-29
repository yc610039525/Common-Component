/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ImportBoInterceptor
/*    */ {
/*    */   public void doCommand(IBoCommand cmd)
/*    */     throws UserException
/*    */   {
/* 19 */     BoCmdContext context = cmd.getCmdContext();
/* 20 */     String boName = context.getBoName();
/* 21 */     String methodName = context.getMethodName();
/* 22 */     String hostIp = cmd.getHostIP();
/* 23 */     String userName = cmd.getHostName();
/* 24 */     boolean isImportBo = ImportBoManager.getInstance().contains(boName);
/* 25 */     if (!isImportBo) {
/* 26 */       LogHome.getLog().debug("客户端没有导入服务：" + boName + "." + methodName + "，请修改import-bo.xml导入需要的服务！");
/* 27 */       throw new UserException("客户端没有导入" + boName + "服务！");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.ImportBoInterceptor
 * JD-Core Version:    0.6.0
 */