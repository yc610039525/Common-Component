/*    */ package com.boco.transnms.common.cfg;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.util.HashMap;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Map;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.InitialContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class InitialContextFactory
/*    */ {
/* 31 */   private static InitialContextFactory instance = new InitialContextFactory();
/* 32 */   private final Map<String, Context> contextMap = new HashMap();
/*    */ 
/*    */   public static InitialContextFactory getInstance()
/*    */   {
/* 38 */     return instance;
/*    */   }
/*    */ 
/*    */   public Context createContext(String contextName, Hashtable contextProps) {
/* 42 */     Context context = (Context)this.contextMap.get(contextName);
/*    */     try {
/* 44 */       if (context == null) {
/* 45 */         context = contextProps == null ? new InitialContext() : new InitialContext(contextProps);
/* 46 */         if ((!context.getEnvironment().containsKey("java.naming.factory.initial")) && (System.getProperty("java.naming.factory.initial") != null))
/*    */         {
/* 48 */           context.addToEnvironment("java.naming.factory.initial", System.getProperty("java.naming.factory.initial"));
/*    */         }
/*    */ 
/* 51 */         if ((!context.getEnvironment().containsKey("java.naming.provider.url")) && (System.getProperty("java.naming.provider.url") != null))
/*    */         {
/* 53 */           context.addToEnvironment("java.naming.provider.url", System.getProperty("java.naming.provider.url"));
/*    */         }
/*    */ 
/* 56 */         this.contextMap.put(contextName, context);
/*    */       }
/*    */     } catch (Exception ex) {
/* 59 */       LogHome.getLog().error("InitialContextFactory 初始化错误，可能是不需要该服务.");
/*    */     }
/* 61 */     return context;
/*    */   }
/*    */ 
/*    */   public void setTnmsServerContext(Map contextProps) {
/* 65 */     Hashtable _contextProps = new Hashtable(contextProps);
/* 66 */     createContext("TRANSNMS_CONTEXT", _contextProps);
/*    */   }
/*    */ 
/*    */   public Context getContext(String contextName) {
/* 70 */     return (Context)this.contextMap.get(contextName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.cfg.InitialContextFactory
 * JD-Core Version:    0.6.0
 */