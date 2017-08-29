/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.db.AbstractSqlCmd;
/*     */ import com.boco.common.util.db.DbContext;
/*     */ import com.boco.common.util.db.DbHelper;
/*     */ import com.boco.common.util.db.SqlHelper;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboBlob;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlExecDaoCmd extends AbstractSqlCmd
/*     */ {
/*     */   protected SqlExecDaoCmd(DbContext dbContext)
/*     */   {
/*  36 */     super(dbContext);
/*     */   }
/*     */ 
/*     */   public int execSql(String sql) throws Exception {
/*  40 */     int result = -1;
/*     */     try {
/*  42 */       sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*  43 */       result = DbHelper.exceSql(getDbConn(), sql);
/*  44 */       LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + " SQL=" + sql);
/*     */     } finally {
/*  46 */       clearCmd();
/*     */     }
/*  48 */     return result;
/*     */   }
/*     */ 
/*     */   public int execSqlnoLog(String sql) throws Exception {
/*  52 */     int result = -1;
/*     */     try {
/*  54 */       sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*  55 */       result = DbHelper.exceSql(getDbConn(), sql, false);
/*     */     } finally {
/*  57 */       clearCmd();
/*     */     }
/*  59 */     return result;
/*     */   }
/*     */ 
/*     */   public void insertDbo(GenericDO dbo) throws Exception {
/*  63 */     PreparedStatement pst = null;
/*     */     try {
/*  65 */       String sql = SqlHelper.createInsertSql(dbo, super.getDbContext().getDbType());
/*  66 */       String psql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*  67 */       pst = getDbConn().prepareStatement(psql);
/*  68 */       SqlHelper.setPrepareStatement(dbo, pst, super.getDbContext().getDbCharset());
/*  69 */       LogHome.getLog().info("className=" + dbo.getClassName() + ",cuid=" + dbo.getCuid());
/*  70 */       LogHome.getLog().debug("SQL=" + sql);
/*  71 */       pst.executeUpdate();
/*     */     } finally {
/*  73 */       DbHelper.closeStatement(pst);
/*  74 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertDbos(DataObjectList dboList) throws Exception {
/*  79 */     PreparedStatement pst = null;
/*  80 */     Connection conn = getDbConn();
/*     */     try {
/*  82 */       conn.setAutoCommit(false);
/*     */ 
/*  84 */       for (int i = 0; i < dboList.size(); i++) {
/*     */         try {
/*  86 */           GenericDO dbo = (GenericDO)dboList.get(i);
/*  87 */           String sql = SqlHelper.createInsertSql(dbo, super.getDbContext().getDbType());
/*  88 */           String psql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*  89 */           pst = conn.prepareStatement(psql);
/*  90 */           SqlHelper.setPrepareStatement(dbo, pst, super.getDbContext().getDbCharset());
/*  91 */           LogHome.getLog().info("SQL=" + sql);
/*  92 */           pst.executeUpdate();
/*     */         } catch (Exception ex) {
/*  94 */           LogHome.getLog().error("", ex);
/*     */         } finally {
/*  96 */           DbHelper.closeStatement(pst);
/*     */         }
/*     */       }
/*  99 */       conn.commit();
/*     */     } catch (Exception ex) {
/* 101 */       DbHelper.rollback(conn);
/* 102 */       throw ex;
/*     */     } finally {
/* 104 */       DbHelper.closeStatement(pst);
/* 105 */       DbHelper.setAutoCommit(conn, true);
/* 106 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateDbo(GenericDO dbo, String sqlCond) throws Exception
/*     */   {
/* 112 */     PreparedStatement pst = null;
/*     */     try {
/* 114 */       String sql = SqlHelper.createUpdateSql(dbo, sqlCond);
/* 115 */       String psql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*     */ 
/* 117 */       pst = getDbConn().prepareStatement(psql);
/* 118 */       SqlHelper.setPrepareStatement(dbo, pst, super.getDbContext().getDbCharset());
/* 119 */       LogHome.getLog().info("SQL=" + sql);
/* 120 */       pst.executeUpdate();
/*     */     } finally {
/* 122 */       DbHelper.closeStatement(pst);
/* 123 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateDbo(GenericDO dbo) throws Exception {
/* 128 */     updateDbo(dbo, "where OBJECTID=" + dbo.getObjectNum());
/*     */   }
/*     */ 
/*     */   public void updateDbo(long[] objIds, GenericDO dbo) throws Exception {
/* 132 */     String sqlCond = "";
/* 133 */     for (int i = 0; i < objIds.length; i++) {
/* 134 */       if (i == 0) {
/* 135 */         sqlCond = "where OBJECTID in (";
/*     */       }
/*     */ 
/* 138 */       sqlCond = sqlCond + objIds[i];
/*     */ 
/* 140 */       if (i == objIds.length - 1)
/* 141 */         sqlCond = sqlCond + ")";
/*     */       else {
/* 143 */         sqlCond = sqlCond + ", ";
/*     */       }
/*     */     }
/*     */ 
/* 147 */     updateDbo(dbo, sqlCond);
/*     */   }
/*     */ 
/*     */   public void updateBlob(String tableName, String blobFieldName, DboBlob blob, String sqlCond) throws Exception
/*     */   {
/*     */     try {
/* 153 */       if (blob.getBlobBytes().length > 10240) {
/* 154 */         blob.zipBytes();
/*     */       }
/* 156 */       DbHelper.updateBlob(getDbConn(), getDbType(), tableName, blobFieldName, blob.getBlobBytes(), sqlCond);
/*     */     }
/*     */     finally {
/* 159 */       clearCmd();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.SqlExecDaoCmd
 * JD-Core Version:    0.6.0
 */