/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import java.sql.Connection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class UserTransactionImpl extends AbstractUserTransaction
/*     */   implements UserTransaction
/*     */ {
/*  30 */   private final Map dbConnTable = new HashMap();
/*  31 */   private boolean isCompleted = false;
/*     */ 
/*     */   public void begin()
/*     */     throws Exception
/*     */   {
/*  37 */     TransactionFactory.getInstance().beginTransaction(this);
/*  38 */     super.setBeginTransc();
/*  39 */     super.addTranscCount();
/*  40 */     super.notifyListener(UserTransaction.TRANSC_STATUS.BEGIN);
/*     */   }
/*     */ 
/*     */   public void commit() throws Exception {
/*  44 */     if (!this.isCompleted) {
/*  45 */       super.subTranscCount();
/*     */       try {
/*  47 */         if (super.isExecAction()) {
/*  48 */           Object[] keys = this.dbConnTable.keySet().toArray();
/*  49 */           for (int i = 0; i < keys.length; i++) {
/*  50 */             Connection conn = (Connection)this.dbConnTable.get(keys[i]);
/*  51 */             conn.commit();
/*     */           }
/*  53 */           notifyListener(UserTransaction.TRANSC_STATUS.COMMIT);
/*     */         }
/*     */       } finally {
/*  56 */         if (super.isExecAction())
/*  57 */           clearTransc();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rollback()
/*     */   {
/*  64 */     if (!this.isCompleted) {
/*  65 */       Object[] keys = this.dbConnTable.keySet().toArray();
/*  66 */       for (int i = 0; i < keys.length; i++) {
/*  67 */         Connection conn = (Connection)this.dbConnTable.get(keys[i]);
/*     */         try {
/*  69 */           conn.rollback();
/*     */         } catch (Throwable ex) {
/*  71 */           LogHome.getLog().error("数据库操作回滚失败：", ex);
/*     */         }
/*     */       }
/*  74 */       notifyListener(UserTransaction.TRANSC_STATUS.ROLLBACK);
/*  75 */       clearTransc();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void clearTransc() {
/*  80 */     this.isCompleted = true;
/*  81 */     Object[] keys = this.dbConnTable.keySet().toArray();
/*  82 */     for (int i = 0; i < keys.length; i++) {
/*  83 */       Connection conn = (Connection)this.dbConnTable.remove(keys[i]);
/*     */       try {
/*  85 */         conn.setAutoCommit(true);
/*  86 */         DbConnManager.getInstance().freeDbConn(conn);
/*     */       } catch (Throwable ex) {
/*  88 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*  91 */     super.clearCount();
/*  92 */     TransactionFactory.getInstance().clearTransaction();
/*     */   }
/*     */ 
/*     */   protected DbContext fetchTranscDbContext(String dsName) throws Exception {
/*  96 */     DbContext dbContext = null;
/*  97 */     if ((super.isBeginTransc()) && (!this.isCompleted)) {
/*  98 */       Connection conn = (Connection)this.dbConnTable.get(dsName);
/*  99 */       if (conn == null) {
/* 100 */         dbContext = DbConnManager.getInstance().fetchDbContext(dsName);
/* 101 */         if (dbContext == null) {
/* 102 */           throw new UserException("无效的的DSName=" + dsName);
/*     */         }
/* 104 */         dbContext.getDbConn().setAutoCommit(false);
/* 105 */         this.dbConnTable.put(dsName, dbContext.getDbConn());
/*     */       } else {
/* 107 */         dbContext = DbConnManager.getInstance().getDbContext(dsName, conn);
/*     */       }
/*     */     }
/* 110 */     return dbContext;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.UserTransactionImpl
 * JD-Core Version:    0.6.0
 */