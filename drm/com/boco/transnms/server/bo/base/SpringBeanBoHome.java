/*    */ package com.boco.transnms.server.bo.base;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SpringBeanBoHome extends SpringBeanHome
/*    */   implements IBoHome
/*    */ {
/* 30 */   private List<String> boNameList = new ArrayList();
/* 31 */   private Map<String, String> boClassNames = new HashMap();
/*    */ 
/*    */   public SpringBeanBoHome(String[] beanXmlFileNames) {
/* 34 */     super(beanXmlFileNames);
/* 35 */     initBoNames();
/*    */   }
/*    */ 
/*    */   public IBusinessObject getBO(String boName) {
/* 39 */     return (IBusinessObject)super.getBean(boName);
/*    */   }
/*    */ 
/*    */   public String[] getBoNames() {
/* 43 */     String[] boNames = new String[this.boClassNames.size()];
/* 44 */     this.boNameList.toArray(boNames);
/* 45 */     return boNames;
/*    */   }
/*    */ 
/*    */   private void initBoNames() {
/* 49 */     String[] beanNames = super.getBeanNames();
/* 50 */     for (int i = 0; i < beanNames.length; i++)
/* 51 */       if ((super.getBean(beanNames[i]) instanceof IBusinessObject)) {
/* 52 */         this.boNameList.add(beanNames[i]);
/* 53 */         IBusinessObject bo = getBO(beanNames[i]);
/* 54 */         Class boClass = bo.getClass();
/* 55 */         String iboClassNames = "";
/* 56 */         Class[] iboClasses = boClass.getInterfaces();
/* 57 */         for (int k = 0; k < iboClasses.length; k++) {
/* 58 */           if (k > 0) {
/* 59 */             iboClassNames = iboClassNames + ",";
/*    */           }
/* 61 */           iboClassNames = iboClassNames + iboClasses[k].getName();
/*    */         }
/* 63 */         this.boClassNames.put(beanNames[i], iboClassNames);
/*    */       }
/*    */   }
/*    */ 
/*    */   public String getBoClassName(String boName) throws UserException
/*    */   {
/* 69 */     if (this.boClassNames.size() == 0) {
/* 70 */       throw new UserException("BoHome还未初始化 ！");
/*    */     }
/* 72 */     String boClassName = (String)this.boClassNames.get(boName);
/* 73 */     if (boClassName == null) {
/* 74 */       throw new UserException("BoHome没有加载这个BO, boName=" + boName);
/*    */     }
/* 76 */     return boClassName;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.SpringBeanBoHome
 * JD-Core Version:    0.6.0
 */