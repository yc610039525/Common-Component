/*    */ package com.boco.raptor.common.misc;
/*    */ 
/*    */ public class RaptorSystem
/*    */ {
/*    */   private static final String RAPTOR_HOME = "TNMS_SERVER_HOME";
/*    */   private static String raptorHome;
/*    */ 
/*    */   public static void setRaptorHome(String _raptorHome)
/*    */   {
/* 28 */     raptorHome = _raptorHome;
/*    */   }
/*    */ 
/*    */   public static String getRaptorHomePath() {
/* 32 */     return raptorHome != null ? raptorHome : getPathEnv("TNMS_SERVER_HOME");
/*    */   }
/*    */ 
/*    */   private static String getPathEnv(String envName) {
/* 36 */     String env = System.getenv(envName);
/* 37 */     return preparePath(env);
/*    */   }
/*    */ 
/*    */   private static String preparePath(String env) {
/* 41 */     if (env != null) {
/* 42 */       env = env.replace('\\', '/');
/* 43 */       char last = env.charAt(env.length() - 1);
/* 44 */       if (last != '/') {
/* 45 */         env = env + "/";
/*    */       }
/*    */     }
/* 48 */     return env;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.misc.RaptorSystem
 * JD-Core Version:    0.6.0
 */