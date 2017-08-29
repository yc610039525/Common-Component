/*    */ package com.boco.common.util.db;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ 
/*    */ public class DbCmdFactory
/*    */ {
/* 24 */   private static DbCmdFactory instance = new DbCmdFactory();
/*    */ 
/*    */   public static DbCmdFactory getInstance()
/*    */   {
/* 30 */     return instance;
/*    */   }
/*    */ 
/*    */   public SqlQueryCmd createSqlQueryCmd() throws Exception {
/* 34 */     return createSqlQueryCmd(DbConnManager.getInstance().getDefaultDsName());
/*    */   }
/*    */ 
/*    */   public SqlExecCmd createSqlExecCmd() throws Exception {
/* 38 */     return createSqlExecCmd(DbConnManager.getInstance().getDefaultDsName());
/*    */   }
/*    */ 
/*    */   public SqlQueryCmd createSqlQueryCmd(String dsName) throws Exception {
/* 42 */     if ((dsName == null) || (dsName.trim().length() == 0)) {
/* 43 */       dsName = DbConnManager.getInstance().getDefaultDsName();
/*    */     }
/*    */ 
/* 46 */     boolean isTranscSqlCmd = true;
/* 47 */     DbContext dbContext = TransactionFactory.getInstance().fetchTranscDbContext(dsName);
/* 48 */     if (dbContext == null) {
/* 49 */       isTranscSqlCmd = false;
/* 50 */       dbContext = DbConnManager.getInstance().fetchDbContext(dsName);
/* 51 */       if (dbContext == null) {
/* 52 */         throw new UserException("无效的的DSName=" + dsName);
/*    */       }
/*    */     }
/*    */ 
/* 56 */     SqlQueryCmd sqlQueryCmd = new SqlQueryCmd(dbContext);
/* 57 */     sqlQueryCmd.setPermitClearCmd(!isTranscSqlCmd);
/* 58 */     return sqlQueryCmd;
/*    */   }
/*    */ 
/*    */   public SqlExecCmd createSqlExecCmd(String dsName) throws Exception {
/* 62 */     if ((dsName == null) || (dsName.trim().length() == 0)) {
/* 63 */       dsName = DbConnManager.getInstance().getDefaultDsName();
/*    */     }
/*    */ 
/* 66 */     boolean isTranscSqlCmd = true;
/* 67 */     DbContext dbContext = TransactionFactory.getInstance().fetchTranscDbContext(dsName);
/* 68 */     if (dbContext == null) {
/* 69 */       isTranscSqlCmd = false;
/* 70 */       dbContext = DbConnManager.getInstance().fetchDbContext(dsName);
/* 71 */       if (dbContext == null) {
/* 72 */         throw new UserException("无效的的DSName=" + dsName);
/*    */       }
/*    */     }
/* 75 */     SqlExecCmd sqlExecCmd = new SqlExecCmd(dbContext);
/* 76 */     sqlExecCmd.setPermitClearCmd(!isTranscSqlCmd);
/* 77 */     return sqlExecCmd;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbCmdFactory
 * JD-Core Version:    0.6.0
 */