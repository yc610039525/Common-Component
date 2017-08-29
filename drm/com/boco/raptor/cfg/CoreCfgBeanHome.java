/*    */ package com.boco.raptor.cfg;
/*    */ 
/*    */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*    */ 
/*    */ public class CoreCfgBeanHome
/*    */ {
/*    */   private static CoreCfgBeanHome instance;
/*    */   FileSystemXmlApplicationContext context;
/*    */ 
/*    */   public static CoreCfgBeanHome getInstance()
/*    */   {
/* 29 */     return instance;
/*    */   }
/*    */ 
/*    */   public static void createInstance(String[] coreCfgXmlFiles) {
/* 33 */     if (instance == null) {
/* 34 */       CoreCfgBeanHome _instance = new CoreCfgBeanHome();
/* 35 */       _instance.context = new FileSystemXmlApplicationContext(coreCfgXmlFiles);
/* 36 */       instance = _instance;
/*    */     }
/*    */   }
/*    */ 
/*    */   public <T> T getBean(String beanName) {
/* 41 */     return this.context.getBean(beanName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.cfg.CoreCfgBeanHome
 * JD-Core Version:    0.6.0
 */