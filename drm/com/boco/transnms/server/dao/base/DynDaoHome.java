/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DynDaoHome
/*    */   implements IDaoHome
/*    */ {
/*  7 */   private final Map<String, IDataAccessObject> daoHome = new HashMap();
/*    */ 
/*    */   public void addDAO(IDataAccessObject dao)
/*    */   {
/* 12 */     this.daoHome.put(dao.getDaoName(), dao);
/*    */   }
/*    */ 
/*    */   public IDataAccessObject getDAO(String daoName) {
/* 16 */     return (IDataAccessObject)this.daoHome.get(daoName);
/*    */   }
/*    */ 
/*    */   public String[] getDaoNames() {
/* 20 */     String[] daoNames = new String[this.daoHome.size()];
/* 21 */     this.daoHome.keySet().toArray(daoNames);
/* 22 */     return daoNames;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.DynDaoHome
 * JD-Core Version:    0.6.0
 */