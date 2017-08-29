/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ 
/*    */ public final class ModelFactory
/*    */ {
/*    */   private static ModelFactory instance;
/*    */   private SpringBeanHome modelHome;
/*    */ 
/*    */   public static ModelFactory getInstance()
/*    */   {
/* 41 */     return instance;
/*    */   }
/*    */ 
/*    */   public static ModelFactory createInstance(String[] springXmlNames)
/*    */   {
/* 50 */     ModelFactory _instance = new ModelFactory();
/* 51 */     _instance.modelHome = new SpringBeanHome(springXmlNames);
/* 52 */     instance = _instance;
/* 53 */     return instance;
/*    */   }
/*    */ 
/*    */   public IModel getModel(String modelName)
/*    */   {
/* 62 */     return (IModel)this.modelHome.getBean(modelName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.ModelFactory
 * JD-Core Version:    0.6.0
 */