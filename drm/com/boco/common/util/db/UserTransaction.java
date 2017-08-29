/*    */ package com.boco.common.util.db;
/*    */ 
/*    */ public abstract interface UserTransaction
/*    */ {
/*    */   public abstract void begin()
/*    */     throws Exception;
/*    */ 
/*    */   public abstract void commit()
/*    */     throws Exception;
/*    */ 
/*    */   public abstract void rollback();
/*    */ 
/*    */   public abstract boolean isBeginTransc();
/*    */ 
/*    */   public static enum TRANSC_STATUS
/*    */   {
/* 28 */     BEGIN, COMMIT, ROLLBACK;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.UserTransaction
 * JD-Core Version:    0.6.0
 */