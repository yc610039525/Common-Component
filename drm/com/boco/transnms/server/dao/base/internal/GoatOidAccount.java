/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class GoatOidAccount
/*    */ {
/*    */   private static GoatOidAccount account;
/*  9 */   Map<Integer, OIdAccount> map = new ConcurrentHashMap();
/*    */ 
/*    */   public static synchronized GoatOidAccount getGoatOidAccount()
/*    */   {
/* 15 */     if (account == null) {
/* 16 */       account = new GoatOidAccount();
/*    */     }
/* 18 */     return account;
/*    */   }
/*    */ 
/*    */   public void setInit(int classId, long initValue) {
/* 22 */     this.map.put(Integer.valueOf(classId), new OIdAccount(initValue));
/*    */   }
/*    */ 
/*    */   public long getNextId(int classId) {
/* 26 */     return ((OIdAccount)this.map.get(Integer.valueOf(classId))).getNext(1);
/*    */   }
/*    */ 
/*    */   public long getNextId(int serverId, int classId) {
/* 30 */     return ((OIdAccount)this.map.get(Integer.valueOf(classId))).getNext(100) / 100L * 100L + serverId;
/*    */   }
/*    */ 
/*    */   public boolean isInitComplate(int classId) {
/* 34 */     return this.map.get(Integer.valueOf(classId)) != null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.GoatOidAccount
 * JD-Core Version:    0.6.0
 */