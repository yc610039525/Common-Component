/*    */ package com.boco.transnms.server.common.cfg;
/*    */ 
/*    */ public class SystemEnv
/*    */ {
/*    */   public static final String TNMS_SERVER_HOME = "TNMS_SERVER_HOME";
/*    */   public static final String TNMS_CLIENT_HOME = "TNMS_CLIENT_HOME";
/*    */ 
/*    */   public static String getPathEnv(String envName)
/*    */   {
/* 29 */     String env = System.getenv(envName);
/* 30 */     if (env != null) {
/* 31 */       env = env.replace('\\', '/');
/*    */     }
/* 33 */     return env;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.common.cfg.SystemEnv
 * JD-Core Version:    0.6.0
 */