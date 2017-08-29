/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DaoHomeFactory
/*     */ {
/*  28 */   private static DaoHomeFactory instance = new DaoHomeFactory();
/*  29 */   private final Map<String, IDaoHome> daoHomeMap = new HashMap();
/*  30 */   private final Map<String, String> contextMap = new HashMap();
/*     */ 
/*     */   public static DaoHomeFactory getInstance()
/*     */   {
/*  40 */     return instance;
/*     */   }
/*     */ 
/*     */   public IDaoHome createDaoHome(String contextName, String[] daoXmlFiles)
/*     */     throws Exception
/*     */   {
/*  51 */     IDaoHome daoHome = new SpringDaoHome(daoXmlFiles);
/*  52 */     addDaoHome(contextName, daoHome);
/*  53 */     return daoHome;
/*     */   }
/*     */ 
/*     */   public void addDaoHome(String contextName, IDaoHome daoHome)
/*     */   {
/*  61 */     this.daoHomeMap.put(contextName, daoHome);
/*  62 */     String[] daoNames = daoHome.getDaoNames();
/*  63 */     for (int i = 0; i < daoNames.length; i++)
/*  64 */       this.contextMap.put(daoNames[i], contextName);
/*     */   }
/*     */ 
/*     */   public IDaoHome getDaoHome(String contextName)
/*     */   {
/*  74 */     IDaoHome daoHome = null;
/*  75 */     if (this.daoHomeMap != null) {
/*  76 */       daoHome = (IDaoHome)this.daoHomeMap.get(contextName);
/*  77 */       if (daoHome == null)
/*  78 */         LogHome.getLog().error("获取daoHome异常，没有找到daoHome,请检查是否初始化加载dao-*.xml,contextName=" + contextName);
/*     */     }
/*     */     else {
/*  81 */       LogHome.getLog().error("获取daoHome异常，DaoHomeFactory.daoHomeMap为空,contextName=" + contextName);
/*     */     }
/*  83 */     return daoHome;
/*     */   }
/*     */ 
/*     */   public IDataAccessObject getDAO(String daoName)
/*     */   {
/*  92 */     IDataAccessObject dao = null;
/*  93 */     if (this.contextMap != null) {
/*  94 */       String contextName = (String)this.contextMap.get(daoName);
/*  95 */       dao = getDaoHome(contextName).getDAO(daoName);
/*  96 */       if (dao == null)
/*  97 */         LogHome.getLog().error("获取DAO异常，没有找到DAO,请检查dao-*.xml是否配该DAO,contextName=" + contextName + ",daoName=" + daoName);
/*     */     }
/*     */     else
/*     */     {
/* 101 */       LogHome.getLog().error("获取DAO异常，DaoHomeFactory.contextMap为空,daoName=" + daoName);
/*     */     }
/* 103 */     return dao;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.DaoHomeFactory
 * JD-Core Version:    0.6.0
 */