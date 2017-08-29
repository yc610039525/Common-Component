/*    */ package com.boco.transnms.server.bo.base;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.util.Hashtable;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DynamicBoHome
/*    */   implements IBoHome
/*    */ {
/* 10 */   private Map<String, IBusinessObject> boTable = new Hashtable();
/*    */ 
/* 12 */   public DynamicBoHome(List<IBusinessObject> boList) { for (IBusinessObject bo : boList)
/* 13 */       this.boTable.put(bo.getBoName(), bo);
/*    */   }
/*    */ 
/*    */   public IBusinessObject getBO(String boName)
/*    */   {
/* 18 */     return (IBusinessObject)this.boTable.get(boName);
/*    */   }
/*    */ 
/*    */   public String[] getBoNames() {
/* 22 */     String[] boNames = new String[this.boTable.size()];
/* 23 */     this.boTable.keySet().toArray(boNames);
/* 24 */     return boNames;
/*    */   }
/*    */ 
/*    */   public String getBoClassName(String boName) throws UserException {
/* 28 */     if (this.boTable.size() == 0) {
/* 29 */       throw new UserException("BoHome还未初始化 ！");
/*    */     }
/*    */ 
/* 32 */     String boClassName = null;
/* 33 */     IBusinessObject bo = (IBusinessObject)this.boTable.get(boName);
/* 34 */     if (bo != null)
/* 35 */       boClassName = bo.getClass().getName();
/*    */     else {
/* 37 */       throw new UserException("BoHome没有加载这个BO, boName=" + boName);
/*    */     }
/* 39 */     return boClassName;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.DynamicBoHome
 * JD-Core Version:    0.6.0
 */