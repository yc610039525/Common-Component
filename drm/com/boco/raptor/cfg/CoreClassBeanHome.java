/*    */ package com.boco.raptor.cfg;
/*    */ 
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ 
/*    */ public class CoreClassBeanHome
/*    */ {
/*    */   private static CoreClassBeanHome instance;
/*    */   private SpringBeanHome beanHome;
/*    */ 
/*    */   public static CoreClassBeanHome getInstance()
/*    */   {
/* 30 */     return instance;
/*    */   }
/*    */ 
/*    */   public static CoreClassBeanHome createInstance(String[] coreBeanXmlFileNames) throws Exception {
/* 34 */     if (instance == null) {
/* 35 */       CoreClassBeanHome _instance = new CoreClassBeanHome();
/* 36 */       _instance.beanHome = new SpringBeanHome(coreBeanXmlFileNames);
/* 37 */       instance = _instance;
/*    */     }
/* 39 */     return instance;
/*    */   }
/*    */ 
/*    */   public <T> T getCoreBean(String beanName) {
/* 43 */     return this.beanHome.getBean(beanName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.cfg.CoreClassBeanHome
 * JD-Core Version:    0.6.0
 */