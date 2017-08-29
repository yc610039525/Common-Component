/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.common.util.db.DbConnManager;
/*    */ import com.boco.common.util.db.DbContext;
/*    */ import com.boco.common.util.db.TransactionFactory;
/*    */ import com.boco.common.util.except.UserException;
/*    */ 
/*    */ public class SqlDaoCmdFactory
/*    */ {
/* 25 */   private static final SqlDaoCmdFactory instance = new SqlDaoCmdFactory();
/*    */ 
/*    */   public static SqlDaoCmdFactory getInstance()
/*    */   {
/* 30 */     return instance;
/*    */   }
/*    */ 
/*    */   public SqlQueryDaoCmd createSqlQueryCmd() throws Exception {
/* 34 */     return createSqlQueryCmd(DbConnManager.getInstance().getDefaultDsName());
/*    */   }
/*    */ 
/*    */   public SqlQueryDaoCmd createSqlQueryCmd(String dsName) throws Exception {
/* 38 */     if ((dsName == null) || (dsName.trim().length() == 0)) {
/* 39 */       dsName = DbConnManager.getInstance().getDefaultDsName();
/*    */     }
/*    */ 
/* 42 */     boolean isTranscSqlCmd = true;
/* 43 */     DbContext dbContext = TransactionFactory.getInstance().fetchTranscDbContext(dsName);
/* 44 */     if (dbContext == null) {
/* 45 */       isTranscSqlCmd = false;
/* 46 */       dbContext = DbConnManager.getInstance().fetchDbContext(dsName);
/* 47 */       if (dbContext == null) {
/* 48 */         throw new UserException("无效的的DSName=" + dsName);
/*    */       }
/*    */     }
/* 51 */     SqlQueryDaoCmd sqlQueryCmd = null;
/* 52 */     sqlQueryCmd = new SqlQueryDaoCmd(dbContext);
/* 53 */     sqlQueryCmd.setPermitClearCmd(!isTranscSqlCmd);
/* 54 */     return sqlQueryCmd;
/*    */   }
/*    */ 
/*    */   public SqlExecDaoCmd createSqlExecCmd() throws Exception {
/* 58 */     return createSqlExecCmd(DbConnManager.getInstance().getDefaultDsName());
/*    */   }
/*    */ 
/*    */   public SqlExecDaoCmd createSqlExecCmd(String dsName) throws Exception {
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
/*    */ 
/* 76 */     SqlExecDaoCmd sqlExecCmd = new SqlExecDaoCmd(dbContext);
/* 77 */     sqlExecCmd.setPermitClearCmd(!isTranscSqlCmd);
/* 78 */     return sqlExecCmd;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.SqlDaoCmdFactory
 * JD-Core Version:    0.6.0
 */