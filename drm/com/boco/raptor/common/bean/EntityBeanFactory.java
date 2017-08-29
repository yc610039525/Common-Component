/*    */ package com.boco.raptor.common.bean;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class EntityBeanFactory
/*    */ {
/* 27 */   private static final EntityBeanFactory instance = new EntityBeanFactory();
/* 28 */   private final Map<String, Class> beanTemplates = new HashMap();
/*    */ 
/*    */   public static EntityBeanFactory getInstance()
/*    */   {
/* 33 */     return instance;
/*    */   }
/*    */ 
/*    */   public <T> T createEntity(String entityName) throws Exception {
/* 37 */     Class entityClass = (Class)this.beanTemplates.get(entityName);
/* 38 */     if (entityClass == null) {
/* 39 */       throw new UserException("entityName: " + entityName + "，没有注册模板 ！");
/*    */     }
/* 41 */     return entityClass.newInstance();
/*    */   }
/*    */ 
/*    */   public void setEntityTemplates(Map<String, String> templateClassNames) throws Exception {
/* 45 */     Iterator it = templateClassNames.keySet().iterator();
/* 46 */     while (it.hasNext()) {
/* 47 */       String entityName = (String)it.next();
/* 48 */       String templateClassName = (String)templateClassNames.get(entityName);
/* 49 */       setEntityTemplate(entityName, templateClassName);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setEntityTemplate(String entityName, String templateClassName) throws Exception {
/* 54 */     Class entityClass = Class.forName(templateClassName);
/* 55 */     this.beanTemplates.put(entityName, entityClass);
/*    */   }
/*    */ 
/*    */   public boolean isEntityRegistered(String entityName) {
/* 59 */     return this.beanTemplates.containsKey(entityName);
/*    */   }
/*    */ 
/*    */   public Class getEntityTemplateClass(String entityName) {
/* 63 */     return (Class)this.beanTemplates.get(entityName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.bean.EntityBeanFactory
 * JD-Core Version:    0.6.0
 */