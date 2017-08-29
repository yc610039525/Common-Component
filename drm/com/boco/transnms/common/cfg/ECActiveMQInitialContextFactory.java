/*    */ package com.boco.transnms.common.cfg;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.NamingException;
/*    */ import org.apache.activemq.jndi.ActiveMQInitialContextFactory;
/*    */ 
/*    */ public class ECActiveMQInitialContextFactory extends ActiveMQInitialContextFactory
/*    */ {
/*    */   public Context getInitialContext(Hashtable environment)
/*    */     throws NamingException
/*    */   {
/* 21 */     Map data = new ConcurrentHashMap();
/*    */ 
/* 36 */     return createContext(environment, data);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.cfg.ECActiveMQInitialContextFactory
 * JD-Core Version:    0.6.0
 */