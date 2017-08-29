/*    */ package com.boco.common.util.spring;
/*    */ 
/*    */ import org.springframework.context.support.ClassPathXmlApplicationContext;
/*    */ 
/*    */ public class SpringBeanHome<E>
/*    */ {
/*    */   private ClassPathXmlApplicationContext classAppContext;
/*    */ 
/*    */   public SpringBeanHome(String[] beanXmlFileNames)
/*    */   {
/* 27 */     this.classAppContext = new ClassPathXmlApplicationContext(beanXmlFileNames);
/*    */   }
/*    */ 
/*    */   public E getBean(String beanName) {
/* 31 */     return this.classAppContext.getBean(beanName);
/*    */   }
/*    */ 
/*    */   public String[] getBeanNames() {
/* 35 */     return this.classAppContext.getBeanDefinitionNames();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.spring.SpringBeanHome
 * JD-Core Version:    0.6.0
 */