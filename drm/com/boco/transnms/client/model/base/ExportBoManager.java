/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.spring.SpringBeanHome;
/*    */ import java.util.Set;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public final class ExportBoManager
/*    */ {
/* 14 */   private static ExportBoManager instance = new ExportBoManager();
/*    */   private Set exportBoNames;
/*    */ 
/*    */   public static ExportBoManager createInstance(String[] springXmlNames)
/*    */   {
/*    */     try
/*    */     {
/* 30 */       SpringBeanHome beanHome = new SpringBeanHome(springXmlNames);
/* 31 */       instance = (ExportBoManager)beanHome.getBean(ExportBoManager.class.getSimpleName());
/*    */     } catch (Exception e) {
/* 33 */       LogHome.getLog().error("", e);
/*    */     }
/* 35 */     return instance;
/*    */   }
/*    */ 
/*    */   public static ExportBoManager getInstance()
/*    */   {
/* 43 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setExportBoNames(Set _exportBoNames)
/*    */   {
/* 51 */     this.exportBoNames = _exportBoNames;
/*    */   }
/*    */ 
/*    */   public Set getExportBoNames()
/*    */   {
/* 59 */     return this.exportBoNames;
/*    */   }
/*    */ 
/*    */   public boolean contains(String boName)
/*    */   {
/* 68 */     return (this.exportBoNames != null) && ((this.exportBoNames.contains(boName)) || (this.exportBoNames.contains("*")));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.ExportBoManager
 * JD-Core Version:    0.6.0
 */