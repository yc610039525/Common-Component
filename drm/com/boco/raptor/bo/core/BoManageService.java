/*    */ package com.boco.raptor.bo.core;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.bo.ibo.core.IBoManageService;
/*    */ import com.boco.transnms.client.model.base.BoServerUrlManager;
/*    */ import com.boco.transnms.common.dto.base.BoActionContext;
/*    */ import com.boco.transnms.server.bo.base.AbstractBO;
/*    */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*    */ import com.boco.transnms.server.bo.base.StateLessBO;
/*    */ import com.boco.transnms.server.bo.base.StateLessBoField;
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ @StateLessBO
/*    */ public class BoManageService extends AbstractBO
/*    */   implements IBoManageService
/*    */ {
/*    */ 
/*    */   @StateLessBoField
/* 37 */   private static boolean firstInvoked = false;
/*    */ 
/*    */   public String getBoClassName(BoActionContext context, String boName)
/*    */     throws UserException
/*    */   {
/* 43 */     return BoHomeFactory.getInstance().getBoClassName(boName);
/*    */   }
/*    */ 
/*    */   public String getBoName() {
/* 47 */     return "IBoManageService";
/*    */   }
/*    */ 
/*    */   public Map<String, String> getBoProxyUrls(Map boNames)
/*    */   {
/* 54 */     Map boProxyUrls = new HashMap();
/*    */ 
/* 59 */     if (boNames != null) {
/* 60 */       Iterator iterator = boNames.keySet().iterator();
/* 61 */       while (iterator.hasNext()) {
/* 62 */         String boName = (String)iterator.next();
/* 63 */         String serverName = (String)boNames.get(boName);
/* 64 */         String url = BoServerUrlManager.getInstance().getBoServerUrl(serverName);
/*    */ 
/* 66 */         boProxyUrls.put(boName, url);
/* 67 */         if ("ISecurityBO".equals(boName)) {
/* 68 */           System.out.println();
/*    */         }
/* 70 */         if ((url == null) && (!firstInvoked))
/* 71 */           LogHome.getLog().error("BO远程服务未配置URL[boName=" + boName + "]");
/*    */       }
/*    */     }
/*    */     else {
/* 75 */       LogHome.getLog().error("BoManageService.getBoProxyUrls传入boNames为空");
/*    */     }
/* 77 */     firstInvoked = true;
/* 78 */     return boProxyUrls;
/*    */   }
/*    */ 
/*    */   public Map<String, String> getBoRemoteProxyUrls(Map boNames) {
/* 82 */     Map boProxyUrls = new HashMap();
/*    */ 
/* 87 */     Iterator iterator = boNames.keySet().iterator();
/* 88 */     while (iterator.hasNext()) {
/* 89 */       String boName = (String)iterator.next();
/* 90 */       String serverName = (String)boNames.get(boName);
/* 91 */       String proxyUrl = BoServerUrlManager.getInstance().getBoServerProxyUrl(serverName);
/*    */ 
/* 93 */       boProxyUrls.put(boName, proxyUrl);
/* 94 */       if ((proxyUrl == null) && (!firstInvoked)) {
/* 95 */         LogHome.getLog().error("BO远程服务未配置URL[boName=" + boName + "]");
/*    */       }
/*    */     }
/* 98 */     firstInvoked = true;
/* 99 */     return boProxyUrls;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.core.BoManageService
 * JD-Core Version:    0.6.0
 */