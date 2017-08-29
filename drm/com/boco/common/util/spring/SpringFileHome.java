/*    */ package com.boco.common.util.spring;
/*    */ 
/*    */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*    */ 
/*    */ public class SpringFileHome<E>
/*    */ {
/*    */   private FileSystemXmlApplicationContext classAppContext;
/*    */ 
/*    */   public SpringFileHome(String[] beanXmlFileNames)
/*    */   {
/*  9 */     this.classAppContext = new FileSystemXmlApplicationContext(beanXmlFileNames);
/*    */   }
/*    */ 
/*    */   public E getBean(String beanName) {
/* 13 */     return this.classAppContext.getBean(beanName);
/*    */   }
/*    */ 
/*    */   public String[] getBeanNames() {
/* 17 */     return this.classAppContext.getBeanDefinitionNames();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.spring.SpringFileHome
 * JD-Core Version:    0.6.0
 */