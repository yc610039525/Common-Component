/*    */ package com.boco.common.util.debug;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class LogHome
/*    */ {
/*    */   public static Log getLog()
/*    */   {
/* 36 */     return LogFactory.getLog(LogHome.class);
/*    */   }
/*    */ 
/*    */   public static Log getLog(Class cls)
/*    */   {
/* 47 */     return LogFactory.getLog(cls);
/*    */   }
/*    */ 
/*    */   public static Log getLog(String name)
/*    */   {
/* 58 */     return LogFactory.getLog(name);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.debug.LogHome
 * JD-Core Version:    0.6.0
 */