/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public final class ImportBoManager
/*    */ {
/* 15 */   private static ImportBoManager instance = new ImportBoManager();
/*    */   private Map importBoNames;
/*    */ 
/*    */   public static ImportBoManager createInstance(String[] springXmlNames)
/*    */   {
/*    */     try
/*    */     {
/* 33 */       SpringBeanHome beanHome = new SpringBeanHome(springXmlNames);
/* 34 */       instance = (ImportBoManager)beanHome.getBean(ImportBoManager.class.getSimpleName());
/*    */     } catch (Exception e) {
/* 36 */       LogHome.getLog().error("", e);
/*    */     }
/* 38 */     return instance;
/*    */   }
/*    */ 
/*    */   public static ImportBoManager getInstance()
/*    */   {
/* 47 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setImportBoNames(Map _importBoNames)
/*    */   {
/* 57 */     this.importBoNames = _importBoNames;
/*    */   }
/*    */ 
/*    */   public Map getImportBoNames()
/*    */   {
/* 66 */     return this.importBoNames;
/*    */   }
/*    */ 
/*    */   public boolean contains(String boName)
/*    */   {
/* 77 */     return (this.importBoNames != null) && (this.importBoNames.containsKey(boName));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.ImportBoManager
 * JD-Core Version:    0.6.0
 */