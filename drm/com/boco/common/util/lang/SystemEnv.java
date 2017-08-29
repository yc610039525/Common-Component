/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ public class SystemEnv
/*    */ {
/*    */   public static String getPathEnv(String envName)
/*    */   {
/* 26 */     String env = System.getenv(envName);
/* 27 */     if (env != null) {
/* 28 */       env = env.replace('\\', '/');
/*    */     }
/* 30 */     return env;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.SystemEnv
 * JD-Core Version:    0.6.0
 */