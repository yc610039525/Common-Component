/*    */ package com.boco.common.util.db;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class TransactionFactory
/*    */ {
/* 24 */   private static final TransactionFactory instance = new TransactionFactory();
/* 25 */   private static ThreadLocal transcThreadLocal = new ThreadLocal();
/*    */ 
/*    */   public static TransactionFactory getInstance()
/*    */   {
/* 31 */     return instance;
/*    */   }
/*    */ 
/*    */   public UserTransaction createTransaction() {
/* 35 */     UserTransaction transc = null;
/*    */     try {
/* 37 */       transc = (UserTransaction)transcThreadLocal.get();
/*    */     }
/*    */     catch (Exception ex) {
/* 40 */       LogHome.getLog().error("", ex);
/*    */     }
/* 42 */     if (transc == null) {
/* 43 */       transc = new UserTransactionImpl();
/*    */     }
/* 45 */     return transc;
/*    */   }
/*    */ 
/*    */   protected void beginTransaction(UserTransaction transc) {
/* 49 */     transcThreadLocal.set(transc);
/*    */   }
/*    */ 
/*    */   public UserTransaction getTransaction() {
/* 53 */     return (UserTransaction)transcThreadLocal.get();
/*    */   }
/*    */ 
/*    */   public DbContext fetchTranscDbContext(String dsName) throws Exception {
/* 57 */     UserTransactionImpl transc = (UserTransactionImpl)transcThreadLocal.get();
/* 58 */     DbContext dbContext = null;
/* 59 */     if (transc != null) {
/* 60 */       dbContext = transc.fetchTranscDbContext(dsName);
/*    */     }
/* 62 */     return dbContext;
/*    */   }
/*    */ 
/*    */   protected void clearTransaction() {
/* 66 */     transcThreadLocal.set(null);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.TransactionFactory
 * JD-Core Version:    0.6.0
 */