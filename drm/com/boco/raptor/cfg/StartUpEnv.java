/*    */ package com.boco.raptor.cfg;
/*    */ 
/*    */ import com.boco.transnms.server.common.cfg.TnmsServerName.ServerName;
/*    */ 
/*    */ public class StartUpEnv
/*    */ {
/*    */   public static String getServerName()
/*    */   {
/* 10 */     String serverName = System.getProperty("servername");
/* 11 */     return serverName;
/*    */   }
/*    */ 
/*    */   public static boolean isRunServer() {
/* 15 */     String serverName = System.getProperty("servername");
/*    */     try {
/* 17 */       return TnmsServerName.ServerName.valueOf(serverName) != null;
/*    */     } catch (Exception ex) {
/*    */     }
/* 20 */     return false;
/*    */   }
/*    */ 
/*    */   public static boolean isAllInOneServer() {
/* 24 */     String serverName = System.getProperty("servername");
/* 25 */     if (serverName == null) return false;
/* 26 */     return "ALL".equals(serverName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.cfg.StartUpEnv
 * JD-Core Version:    0.6.0
 */