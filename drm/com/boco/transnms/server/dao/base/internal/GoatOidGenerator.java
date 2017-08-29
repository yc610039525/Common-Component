/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ public class GoatOidGenerator
/*    */ {
/*  5 */   private static long offset = 0L;
/*    */ 
/*  7 */   private static long sysCurrentTimeMillis = System.currentTimeMillis();
/*    */ 
/* 28 */   private static UUIDHexGenerator uuidHexGenerator = new UUIDHexGenerator();
/*    */ 
/*    */   private static synchronized long createOID()
/*    */   {
/* 10 */     if (offset < 9223372036854775807L) {
/* 11 */       offset += 1L;
/*    */     } else {
/* 13 */       long currentTimeMillisMth = System.currentTimeMillis();
/* 14 */       if (currentTimeMillisMth > sysCurrentTimeMillis)
/* 15 */         sysCurrentTimeMillis = currentTimeMillisMth;
/*    */       else {
/* 17 */         sysCurrentTimeMillis += 9223372036854775807L;
/*    */       }
/* 19 */       offset = 0L;
/*    */     }
/* 21 */     return sysCurrentTimeMillis + offset;
/*    */   }
/*    */ 
/*    */   public static synchronized long getNewId(int clsId)
/*    */   {
/* 32 */     return GoatOidAccount.getGoatOidAccount().getNextId(clsId);
/*    */   }
/*    */ 
/*    */   public static synchronized long getNewId(int serverId, int clsId)
/*    */   {
/* 37 */     return GoatOidAccount.getGoatOidAccount().getNextId(serverId, clsId);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.GoatOidGenerator
 * JD-Core Version:    0.6.0
 */