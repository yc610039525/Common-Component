/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SpringDaoHome extends SpringBeanHome
/*    */   implements IDaoHome
/*    */ {
/* 27 */   private List<String> daoNameList = new ArrayList();
/*    */ 
/*    */   public SpringDaoHome(String[] beanXmlFileNames) {
/* 30 */     super(beanXmlFileNames);
/* 31 */     initDaoNames();
/*    */   }
/*    */ 
/*    */   public IDataAccessObject getDAO(String boName) {
/* 35 */     return (IDataAccessObject)super.getBean(boName);
/*    */   }
/*    */ 
/*    */   public String[] getDaoNames() {
/* 39 */     String[] daoNames = new String[this.daoNameList.size()];
/* 40 */     return (String[])this.daoNameList.toArray(daoNames);
/*    */   }
/*    */ 
/*    */   private void initDaoNames() {
/* 44 */     String[] beanNames = super.getBeanNames();
/* 45 */     for (int i = 0; i < beanNames.length; i++)
/* 46 */       if ((super.getBean(beanNames[i]) instanceof IDataAccessObject))
/* 47 */         this.daoNameList.add(beanNames[i]);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.SpringDaoHome
 * JD-Core Version:    0.6.0
 */