/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.BufInputStream;
/*     */ import com.informix.jdbc.IfxDriver;
/*     */ import com.sybase.jdbc3.jdbc.SybDriver;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Properties;
/*     */ import oracle.jdbc.driver.OracleDriver;
/*     */ import oracle.sql.BLOB;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.hsqldb.jdbcDriver;
/*     */ 
/*     */ public class DbHelper
/*     */ {
/*     */   public static String getDbDriverClassName(String dbUrl)
/*     */     throws UserException
/*     */   {
/*  40 */     String dbDriverClassName = null;
/*  41 */     DbType dbType = getDbType(dbUrl);
/*  42 */     if (dbType == DbType.DB_TYPE_SYBASE)
/*  43 */       dbDriverClassName = "com.sybase.jdbc3.jdbc.SybDriver";
/*  44 */     else if (dbType == DbType.DB_TYPE_ORACLE)
/*  45 */       dbDriverClassName = "oracle.jdbc.driver.OracleDriver";
/*  46 */     else if (dbType == DbType.DB_TYPE_HSQL)
/*  47 */       dbDriverClassName = "org.hsqldb.jdbcDriver";
/*  48 */     else if (dbType == DbType.DB_TYPE_INFORMIX)
/*  49 */       dbDriverClassName = "com.informix.jdbc.IfxDriver";
/*  50 */     else if (dbType == DbType.DB_TYPE_FIREBIRD)
/*  51 */       dbDriverClassName = "org.firebirdsql.jdbc.FBDriver";
/*  52 */     else if (dbType == DbType.DB_TYPE_MYSQL)
/*  53 */       dbDriverClassName = "com.mysql.jdbc.Driver";
/*  54 */     else if (dbType == DbType.DB_TYPE_SQLSERVER) {
/*  55 */       dbDriverClassName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
/*     */     }
/*     */ 
/*  58 */     return dbDriverClassName;
/*     */   }
/*     */ 
/*     */   public static String getValidationQuery(String dbUrl) throws UserException {
/*  62 */     String validationQuery = null;
/*  63 */     DbType dbType = getDbType(dbUrl);
/*  64 */     if (dbType == DbType.DB_TYPE_SYBASE)
/*  65 */       validationQuery = "select 1 from sysusers";
/*  66 */     else if (dbType == DbType.DB_TYPE_ORACLE)
/*  67 */       validationQuery = "SELECT 1 FROM DUAL";
/*  68 */     else if (dbType == DbType.DB_TYPE_INFORMIX) {
/*  69 */       validationQuery = "select site from systables where tabname =' GL_COLLATE'";
/*     */     }
/*  71 */     return validationQuery;
/*     */   }
/*     */ 
/*     */   public static String getLockTableSql(int dbType, String tableName) {
/*  75 */     String lockTableSql = null;
/*  76 */     if (dbType == 2)
/*  77 */       lockTableSql = "lock table " + tableName + " in exclusive mode nowait";
/*  78 */     else if (dbType == 1)
/*  79 */       lockTableSql = "lock table " + tableName + " in exclusive mode nowait";
/*  80 */     else if (dbType == 5) {
/*  81 */       lockTableSql = "lock table " + tableName + " in exclusive mode";
/*     */     }
/*  83 */     return lockTableSql;
/*     */   }
/*     */ 
/*     */   public static DbType getDbType(String dbUrl) throws UserException {
/*  87 */     DbType dbType = null;
/*  88 */     if (dbUrl.indexOf("jdbc:oracle") >= 0)
/*  89 */       dbType = DbType.DB_TYPE_ORACLE;
/*  90 */     else if (dbUrl.indexOf("jdbc:sybase") >= 0)
/*  91 */       dbType = DbType.DB_TYPE_SYBASE;
/*  92 */     else if (dbUrl.indexOf("jdbc:hsqldb") >= 0)
/*  93 */       dbType = DbType.DB_TYPE_HSQL;
/*  94 */     else if (dbUrl.indexOf("jdbc:informix") >= 0)
/*  95 */       dbType = DbType.DB_TYPE_INFORMIX;
/*  96 */     else if (dbUrl.indexOf("jdbc:firebirdsql") >= 0)
/*  97 */       dbType = DbType.DB_TYPE_FIREBIRD;
/*  98 */     else if (dbUrl.indexOf("jdbc:mysql") >= 0)
/*  99 */       dbType = DbType.DB_TYPE_MYSQL;
/* 100 */     else if (dbUrl.indexOf("jdbc:microsoft") >= 0)
/* 101 */       dbType = DbType.DB_TYPE_SQLSERVER;
/*     */     else {
/* 103 */       throw new UserException("未知的数据库类型：dbUrl=" + dbUrl);
/*     */     }
/* 105 */     return dbType;
/*     */   }
/*     */ 
/*     */   public static Connection makeDBConn(String dbUrl, String userName, String password) throws Exception {
/* 109 */     regDBDriverByUrl(dbUrl);
/* 110 */     Properties prop = new Properties();
/* 111 */     prop.put("user", userName);
/* 112 */     prop.put("password", password);
/* 113 */     Connection conn = DriverManager.getConnection(dbUrl, prop);
/* 114 */     LogHome.getLog().info("建立数据库连接[url=" + dbUrl + ", userName=" + userName + "]");
/* 115 */     return conn;
/*     */   }
/*     */ 
/*     */   private static int regDBDriverByUrl(String dbUrl) throws Exception {
/* 119 */     int dbType = -1;
/* 120 */     if (getDbType(dbUrl) == DbType.DB_TYPE_ORACLE) {
/* 121 */       DriverManager.registerDriver(new OracleDriver());
/* 122 */       dbType = 1;
/* 123 */     } else if (getDbType(dbUrl) == DbType.DB_TYPE_SYBASE) {
/* 124 */       DriverManager.registerDriver(new SybDriver());
/* 125 */       dbType = 2;
/* 126 */     } else if (getDbType(dbUrl) == DbType.DB_TYPE_HSQL) {
/* 127 */       DriverManager.registerDriver(new jdbcDriver());
/* 128 */       dbType = 4;
/* 129 */     } else if (getDbType(dbUrl) == DbType.DB_TYPE_INFORMIX) {
/* 130 */       DriverManager.registerDriver(new IfxDriver());
/* 131 */       dbType = 5;
/* 132 */     } else if (getDbType(dbUrl) == DbType.DB_TYPE_FIREBIRD) {
/* 133 */       DriverManager.registerDriver((Driver)Class.forName("org.firebirdsql.jdbc.FBDriver").newInstance());
/* 134 */       dbType = 6;
/* 135 */     } else if (getDbType(dbUrl) == DbType.DB_TYPE_SQLSERVER) {
/* 136 */       DriverManager.registerDriver((Driver)Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance());
/* 137 */       dbType = 8;
/*     */     } else {
/* 139 */       throw new UserException("不支持的数据库类型：dbUrl=" + dbUrl);
/*     */     }
/*     */ 
/* 142 */     return dbType;
/*     */   }
/*     */ 
/*     */   public static int exceSql(Connection conn, String sql) throws Exception
/*     */   {
/* 147 */     int result = -1;
/* 148 */     PreparedStatement pst = conn.prepareStatement(sql);
/*     */     try {
/* 150 */       result = pst.executeUpdate();
/*     */     } catch (Exception ex) {
/* 152 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 153 */       throw ex;
/*     */     } finally {
/* 155 */       closeStatement(pst);
/*     */     }
/* 157 */     return result;
/*     */   }
/*     */ 
/*     */   public static int exceSql(Connection conn, String sql, boolean showLog) throws Exception {
/* 161 */     int result = -1;
/* 162 */     PreparedStatement pst = conn.prepareStatement(sql);
/*     */     try {
/* 164 */       result = pst.executeUpdate();
/*     */     } catch (Exception ex) {
/* 166 */       if (showLog) {
/* 167 */         LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/*     */       }
/* 169 */       throw ex;
/*     */     } finally {
/* 171 */       closeStatement(pst);
/*     */     }
/* 173 */     return result;
/*     */   }
/*     */ 
/*     */   public static void closeStatement(Statement st) {
/*     */     try {
/* 178 */       if (st != null)
/* 179 */         st.close();
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void closeResultSet(ResultSet rs) {
/*     */     try {
/* 188 */       if (rs != null)
/* 189 */         rs.close();
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getEmptyBlobStr(DbType dbType) {
/* 197 */     String emptyBlobStr = "null";
/* 198 */     if (dbType == DbType.DB_TYPE_ORACLE) {
/* 199 */       emptyBlobStr = " empty_blob() ";
/*     */     }
/* 201 */     return emptyBlobStr;
/*     */   }
/*     */ 
/*     */   private static void updateOracleBlob(Connection conn, String tableName, String blobFieldName, byte[] blobBytes, String sqlCond)
/*     */     throws Exception
/*     */   {
/* 216 */     PreparedStatement pst = null;
/* 217 */     ResultSet rs = null;
/* 218 */     String sql = null;
/*     */     try {
/* 220 */       conn.setAutoCommit(false);
/* 221 */       sql = "update " + tableName + " set " + blobFieldName + " = empty_blob() " + " where " + sqlCond;
/*     */ 
/* 223 */       PreparedStatement pstB = null;
/*     */       try {
/* 225 */         pstB = conn.prepareStatement(sql);
/* 226 */         LogHome.getLog().info("SQL=" + sql);
/* 227 */         pstB.executeUpdate();
/* 228 */         conn.commit();
/*     */       } finally {
/* 230 */         closeStatement(pstB);
/*     */       }
/*     */ 
/* 233 */       sql = "select " + blobFieldName + " from " + tableName + " where " + sqlCond + " for update";
/*     */ 
/* 235 */       pst = conn.prepareStatement(sql);
/* 236 */       pst.setQueryTimeout(0);
/* 237 */       rs = pst.executeQuery();
/* 238 */       pst.setQueryTimeout(0);
/* 239 */       if (rs.next()) {
/* 240 */         BLOB blob = (BLOB)rs.getBlob(1);
/*     */ 
/* 242 */         blob.putBytes(1L, blobBytes);
/* 243 */         sql = "update " + tableName + " set " + blobFieldName + "=?" + " where " + sqlCond;
/*     */ 
/* 245 */         pstB = null;
/*     */         try {
/* 247 */           pstB = conn.prepareStatement(sql);
/* 248 */           pstB.setBlob(1, blob);
/* 249 */           LogHome.getLog().info("SQL=" + sql);
/* 250 */           pstB.executeUpdate();
/* 251 */           conn.commit();
/*     */         } finally {
/* 253 */           closeStatement(pstB);
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) {
/* 257 */       rollback(conn);
/* 258 */       throw ex;
/*     */     } finally {
/* 260 */       setAutoCommit(conn, true);
/* 261 */       closeResultSet(rs);
/* 262 */       closeStatement(pst);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void clearSybaseBlob(Connection conn, String tableName, String blobFieldName, byte[] blobBytes, String sqlCond) throws Exception
/*     */   {
/* 268 */     PreparedStatement pst = null;
/* 269 */     ResultSet rs = null;
/* 270 */     String sql = null;
/*     */     try {
/* 272 */       sql = "update " + tableName + " set " + blobFieldName + "=null where " + sqlCond;
/*     */ 
/* 274 */       pst = conn.prepareStatement(sql);
/* 275 */       LogHome.getLog().info("SQL=" + sql);
/* 276 */       pst.executeUpdate();
/*     */     } finally {
/* 278 */       closeStatement(pst);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void updateStandardBlob(Connection conn, String tableName, String blobFieldName, byte[] blobBytes, String sqlCond) throws Exception
/*     */   {
/* 284 */     PreparedStatement pst = null;
/* 285 */     ResultSet rs = null;
/* 286 */     String sql = null;
/*     */     try {
/* 288 */       sql = "update " + tableName + " set " + blobFieldName + "=? where " + sqlCond;
/*     */ 
/* 290 */       pst = conn.prepareStatement(sql);
/* 291 */       BufInputStream blobIn = new BufInputStream(blobBytes);
/* 292 */       pst.setBinaryStream(1, blobIn, blobBytes.length);
/* 293 */       LogHome.getLog().info("SQL=" + sql);
/* 294 */       pst.executeUpdate();
/*     */     } finally {
/* 296 */       closeStatement(pst);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void updateBlob(Connection conn, DbType dbType, String tableName, String blobFieldName, byte[] blobBytes, String sqlCond)
/*     */     throws Exception
/*     */   {
/* 303 */     if (dbType == DbType.DB_TYPE_ORACLE)
/* 304 */       updateOracleBlob(conn, tableName, blobFieldName, blobBytes, sqlCond);
/* 305 */     else if ((dbType == DbType.DB_TYPE_SYBASE) && (blobBytes.length == 0))
/* 306 */       clearSybaseBlob(conn, tableName, blobFieldName, blobBytes, sqlCond);
/* 307 */     else if ((dbType == DbType.DB_TYPE_SYBASE) || (dbType == DbType.DB_TYPE_HSQL) || (dbType == DbType.DB_TYPE_INFORMIX) || (dbType == DbType.DB_TYPE_MYSQL))
/*     */     {
/* 309 */       updateStandardBlob(conn, tableName, blobFieldName, blobBytes, sqlCond);
/*     */     }
/* 311 */     else throw new Exception("未知类型的数据库 !"); 
/*     */   }
/*     */ 
/*     */   public static void setAutoCommit(Connection conn, boolean isAutoCommit)
/*     */   {
/*     */     try
/*     */     {
/* 317 */       if (conn != null)
/* 318 */         conn.setAutoCommit(isAutoCommit);
/*     */     }
/*     */     catch (Exception ex) {
/* 321 */       LogHome.getLog().info("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void rollback(Connection conn) {
/*     */     try {
/* 327 */       if (conn != null)
/* 328 */         conn.rollback();
/*     */     }
/*     */     catch (SQLException ex) {
/* 331 */       LogHome.getLog().info("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void showConnectionInfo(Connection conn) {
/*     */     try {
/* 337 */       DatabaseMetaData dbMeta = conn.getMetaData();
/* 338 */       LogHome.getLog().info("jdbcDriverName=" + dbMeta.getDriverName() + ", isSupportBatchUpdates=" + dbMeta.supportsBatchUpdates() + ", isSupportsScrollable=" + dbMeta.supportsResultSetType(1004) + ", maxConnections=" + dbMeta.getMaxConnections() + ", maxStatementLength=" + dbMeta.getMaxStatementLength() + ", transactionIsolation=" + conn.getTransactionIsolation());
/*     */     }
/*     */     catch (SQLException ex)
/*     */     {
/* 345 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbHelper
 * JD-Core Version:    0.6.0
 */