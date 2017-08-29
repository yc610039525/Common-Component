/*    */ package com.boco.common.util.j2ee;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.NamingException;
/*    */ 
/*    */ public class ContextHelper
/*    */ {
/*    */   public static Context createContext(Hashtable contextProps)
/*    */     throws NamingException
/*    */   {
/* 11 */     Context context = contextProps == null ? new InitialContext() : new InitialContext(contextProps);
/* 12 */     if ((!context.getEnvironment().containsKey("java.naming.factory.initial")) && (System.getProperty("java.naming.factory.initial") != null))
/*    */     {
/* 14 */       context.addToEnvironment("java.naming.factory.initial", System.getProperty("java.naming.factory.initial"));
/*    */     }
/*    */ 
/* 17 */     if ((!context.getEnvironment().containsKey("java.naming.provider.url")) && (System.getProperty("java.naming.provider.url") != null))
/*    */     {
/* 19 */       context.addToEnvironment("java.naming.provider.url", System.getProperty("java.naming.provider.url"));
/*    */     }
/*    */ 
/* 22 */     return context;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.j2ee.ContextHelper
 * JD-Core Version:    0.6.0
 */