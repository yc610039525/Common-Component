/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlExecCmd extends AbstractSqlCmd
/*     */ {
/*     */   protected SqlExecCmd(DbContext dbContext)
/*     */   {
/*  31 */     super(dbContext);
/*     */   }
/*     */ 
/*     */   public int execSql(String sql) throws Exception {
/*  35 */     int result = -1;
/*     */     try {
/*  37 */       sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql);
/*  38 */       result = DbHelper.exceSql(getDbConn(), sql);
/*  39 */       LogHome.getLog().info("SQL=" + sql);
/*     */     } finally {
/*  41 */       clearCmd();
/*     */     }
/*  43 */     return result;
/*     */   }
/*     */ 
/*     */   public void updateWholeDbo(IDbModel dbo, String sqlCond) throws Exception
/*     */   {
/*  48 */     PreparedStatement pst = null;
/*     */     try {
/*  50 */       String sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), createUpdateSql(dbo, sqlCond));
/*  51 */       pst = getDbConn().prepareStatement(sql);
/*  52 */       setPrepareStatement(dbo, pst);
/*  53 */       LogHome.getLog().info("SQL=" + sql);
/*  54 */       pst.executeUpdate();
/*     */     } finally {
/*  56 */       DbHelper.closeStatement(pst);
/*  57 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertDbo(IDbModel dbo) throws Exception {
/*  62 */     PreparedStatement pst = null;
/*     */     try {
/*  64 */       String sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), createInsertSql(dbo));
/*  65 */       pst = getDbConn().prepareStatement(sql);
/*  66 */       setPrepareStatement(dbo, pst);
/*  67 */       LogHome.getLog().info("SQL=" + sql);
/*  68 */       pst.executeUpdate();
/*     */     } finally {
/*  70 */       DbHelper.closeStatement(pst);
/*  71 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertDbos(List dboList) throws Exception {
/*  76 */     PreparedStatement pst = null;
/*  77 */     Connection conn = getDbConn();
/*     */     try {
/*  79 */       conn.setAutoCommit(false);
/*     */ 
/*  81 */       for (int i = 0; i < dboList.size(); i++) {
/*  82 */         IDbModel dbo = (IDbModel)dboList.get(i);
/*  83 */         String sql = SqlHelper.prepareSql(super.getDbContext().getDbCharset(), createInsertSql(dbo));
/*  84 */         pst = conn.prepareStatement(sql);
/*  85 */         setPrepareStatement(dbo, pst);
/*  86 */         LogHome.getLog().info("SQL=" + sql);
/*  87 */         pst.executeUpdate();
/*     */       }
/*     */ 
/*  90 */       conn.commit();
/*     */     } catch (Exception ex) {
/*  92 */       DbHelper.rollback(conn);
/*  93 */       throw ex;
/*     */     } finally {
/*  95 */       DbHelper.closeStatement(pst);
/*  96 */       DbHelper.setAutoCommit(conn, true);
/*  97 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateBlob(String tableName, String blobFieldName, DbBlob blob, String sqlCond) throws Exception
/*     */   {
/*     */     try {
/* 104 */       if (blob.getBlobBytes().length > 10240) {
/* 105 */         blob.zipBytes();
/*     */       }
/* 107 */       DbHelper.updateBlob(getDbConn(), getDbType(), tableName, blobFieldName, blob.getBlobBytes(), sqlCond);
/*     */     }
/*     */     finally {
/* 110 */       clearCmd();
/*     */     }
/*     */   }
/*     */ 
/*     */   private String createUpdateSql(IDbModel dbo, String sqlCond) throws Exception {
/* 115 */     Field[] fields = dbo.getClass().getFields();
/* 116 */     StringBuffer sql = new StringBuffer("update " + dbo.getTableName() + " set ");
/* 117 */     int updateFieldCount = 0;
/* 118 */     for (int i = 0; i < fields.length; i++) {
/* 119 */       Object value = fields[i].get(dbo);
/* 120 */       if ((value == null) || (fields[i].getType() == DbBlob.class))
/*     */       {
/*     */         continue;
/*     */       }
/* 124 */       if (updateFieldCount > 0) {
/* 125 */         sql.append(", ");
/*     */       }
/* 127 */       sql.append(fields[i].getName() + "=? ");
/* 128 */       updateFieldCount++;
/*     */     }
/*     */ 
/* 131 */     return sql.toString() + sqlCond;
/*     */   }
/*     */ 
/*     */   private String createInsertSql(IDbModel dbo) throws Exception {
/* 135 */     Field[] fields = dbo.getClass().getFields();
/* 136 */     StringBuffer sql = new StringBuffer("insert into " + dbo.getTableName() + "(");
/*     */ 
/* 138 */     for (int i = 0; i < fields.length; i++) {
/* 139 */       sql.append(fields[i].getName());
/* 140 */       if (i < fields.length - 1) {
/* 141 */         sql.append(", ");
/*     */       }
/*     */     }
/* 144 */     sql.append(") values(");
/*     */ 
/* 146 */     for (int i = 0; i < fields.length; i++) {
/* 147 */       Object value = fields[i].get(dbo);
/* 148 */       if (fields[i].getType() == DbBlob.class)
/* 149 */         sql.append(DbHelper.getEmptyBlobStr(getDbContext().getDbType()));
/* 150 */       else if (value == null)
/* 151 */         sql.append("null");
/*     */       else {
/* 153 */         sql.append("?");
/*     */       }
/* 155 */       if (i < fields.length - 1) {
/* 156 */         sql.append(",");
/*     */       }
/*     */     }
/* 159 */     sql.append(")");
/*     */ 
/* 161 */     return sql.toString();
/*     */   }
/*     */ 
/*     */   private void setPrepareStatement(IDbModel dbo, PreparedStatement pst) throws Exception
/*     */   {
/* 166 */     Field[] fields = dbo.getClass().getFields();
/*     */ 
/* 168 */     int columnIndex = 0;
/* 169 */     for (int i = 0; i < fields.length; i++) {
/* 170 */       Field field = fields[i];
/* 171 */       Object value = field.get(dbo);
/* 172 */       Class fieldType = field.getType();
/* 173 */       if ((value == null) || (fieldType == DbBlob.class))
/*     */         continue;
/* 175 */       columnIndex++;
/* 176 */       if ((fieldType == Boolean.TYPE) || (fieldType == Boolean.class))
/* 177 */         pst.setBoolean(columnIndex, field.getBoolean(dbo));
/* 178 */       else if ((fieldType == Integer.TYPE) || (fieldType == Integer.class))
/* 179 */         pst.setInt(columnIndex, field.getInt(dbo));
/* 180 */       else if ((fieldType == Short.TYPE) || (fieldType == Short.class))
/* 181 */         pst.setShort(columnIndex, field.getShort(dbo));
/* 182 */       else if ((fieldType == Long.TYPE) || (fieldType == Long.class))
/* 183 */         pst.setLong(columnIndex, field.getLong(dbo));
/* 184 */       else if ((fieldType == Byte.TYPE) || (fieldType == Byte.class))
/* 185 */         pst.setByte(columnIndex, field.getByte(dbo));
/* 186 */       else if ((fieldType == Float.TYPE) || (fieldType == Float.class))
/* 187 */         pst.setFloat(columnIndex, field.getFloat(dbo));
/* 188 */       else if (fieldType == String.class)
/* 189 */         pst.setString(columnIndex, SqlHelper.prepareSql(super.getDbContext().getDbCharset(), (String)field.get(dbo)));
/* 190 */       else if (fieldType == Date.class)
/* 191 */         pst.setDate(columnIndex, (Date)field.get(dbo));
/* 192 */       else if (fieldType == Time.class)
/* 193 */         pst.setTime(columnIndex, (Time)field.get(dbo));
/* 194 */       else if (fieldType == Timestamp.class)
/* 195 */         pst.setTimestamp(columnIndex, (Timestamp)field.get(dbo));
/*     */       else
/* 197 */         throw new UserException("插入数据库对象不支持的数据类型：" + fieldType.getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.SqlExecCmd
 * JD-Core Version:    0.6.0
 */