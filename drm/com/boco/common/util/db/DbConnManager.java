/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DbConnManager
/*     */ {
/*  38 */   private String defaultDsName = "GLOBAL_DS_NAME";
/*     */ 
/*  40 */   private String defaultDsImplName = "org.apache.commons.dbcp.BasicDataSource";
/*     */ 
/*  42 */   private final Map<String, DbContext> dbContextTable = new HashMap();
/*  43 */   private final Map<String, DbPoolProxy> dbPoolTable = new HashMap();
/*  44 */   private int serverId = -1;
/*     */ 
/*  46 */   private static DbConnManager instance = new DbConnManager();
/*     */ 
/*     */   public static DbConnManager getInstance()
/*     */   {
/*  52 */     return instance;
/*     */   }
/*     */ 
/*     */   public String getDefaultDsName() {
/*  56 */     return this.defaultDsName;
/*     */   }
/*     */ 
/*     */   public void setDbConnPools(List<DbContext> dbContexts) throws Exception {
/*  60 */     for (int i = 0; i < dbContexts.size(); i++)
/*  61 */       setDbConnPool((DbContext)dbContexts.get(i));
/*     */   }
/*     */ 
/*     */   private void setDbConnPool(DbContext dbContext)
/*     */     throws Exception
/*     */   {
/*  83 */     String dsName = dbContext.getDsName();
/*  84 */     if (this.dbContextTable.containsKey(dsName)) {
/*  85 */       LogHome.getLog().info("数据源＝" + dsName + "， 已经加载 ！");
/*     */     }
/*  87 */     DbPoolProxy dbPool = new DbPoolProxy(dbContext);
/*  88 */     this.dbPoolTable.put(dsName, dbPool);
/*  89 */     int sId = this.serverId > 0 ? this.serverId : this.serverId;
/*  90 */     DbContext _dbContext = dbContext.clone();
/*  91 */     this.dbContextTable.put(dsName, _dbContext);
/*  92 */     LogHome.getLog().info("数据源[dsName=" + dsName + ", dbUrl=" + dbContext.getDbUrl() + "], 加载成功 ！");
/*     */   }
/*     */ 
/*     */   public void checkDbpool()
/*     */   {
/*     */     try
/*     */     {
/* 110 */       Iterator iterator = this.dbPoolTable.values().iterator();
/* 111 */       while (iterator.hasNext()) {
/* 112 */         DbPoolProxy dbPool = (DbPoolProxy)iterator.next();
/* 113 */         dbPool.checkDbpool();
/*     */       }
/*     */     } catch (Exception ex) {
/* 116 */       LogHome.getLog().error("数据库连接池检查失败！" + ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Connection fetchDbConn() throws Exception {
/* 121 */     return fetchDbConn(getDefaultDsName());
/*     */   }
/*     */ 
/*     */   private String getDSName(String dataSourceName) {
/* 125 */     String dsName = dataSourceName;
/* 126 */     if ((dataSourceName == null) || (dataSourceName.trim().length() == 0)) {
/* 127 */       dsName = getDefaultDsName();
/*     */     }
/* 129 */     return dsName;
/*     */   }
/*     */ 
/*     */   private Connection getConn(String dsName) throws Exception {
/* 133 */     Connection conn = null;
/* 134 */     DbPoolProxy dbPoolProxy = (DbPoolProxy)this.dbPoolTable.get(dsName);
/* 135 */     if (dbPoolProxy != null)
/*     */       try {
/* 137 */         conn = dbPoolProxy.getConnection();
/* 138 */         if (conn == null)
/* 139 */           throw new UserException("dsName=" + dsName + " ，获取连接失败!");
/*     */       }
/*     */       catch (Exception ex) {
/* 142 */         throw new UserException("dsName=" + dsName + " ，获取连接失败：" + ex);
/*     */       }
/*     */     else {
/* 145 */       throw new UserException("dsName=" + dsName + " ，没有配置数据源 ！");
/*     */     }
/* 147 */     return conn;
/*     */   }
/*     */ 
/*     */   private void prepareConn(String dsName, Connection conn) throws Exception {
/* 151 */     if (conn != null) {
/* 152 */       if (!conn.getAutoCommit()) {
/* 153 */         conn.setAutoCommit(true);
/*     */       }
/*     */ 
/* 156 */       DbContext dbContextTemplate = (DbContext)this.dbContextTable.get(dsName);
/* 157 */       DbContext dbContext = dbContextTemplate.clone();
/* 158 */       if (((dbContext.getDbType() == DbType.DB_TYPE_INFORMIX) || (dbContext.getDbType() == DbType.DB_TYPE_SYBASE)) && (conn.getTransactionIsolation() != 1))
/*     */       {
/* 160 */         conn.setTransactionIsolation(1);
/*     */       }
/* 162 */       if (dbContext.getDbType() == DbType.DB_TYPE_INFORMIX) {
/*     */         try {
/* 164 */           int rsDR = conn.createStatement().executeUpdate("set isolation to dirty read");
/* 165 */           int rsLW = conn.createStatement().executeUpdate("set lock mode to wait 180");
/* 166 */           LogHome.getLog().debug("set isolation to dirty read:" + rsDR + "; set lock mode to wait 180:" + rsLW);
/*     */         }
/*     */         catch (Exception ex) {
/* 169 */           LogHome.getLog().error("set isolation to dirty read and lock mode error:" + ex);
/*     */         }
/*     */       }
/* 172 */       dbContext.setDbConn(conn);
/* 173 */       String dbProductVersion = conn.getMetaData().getDatabaseProductVersion();
/* 174 */       dbContext.setDbProductVersion(dbProductVersion);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Connection fetchDbConn(String dataSourceName) throws Exception
/*     */   {
/* 180 */     String dsName = getDSName(dataSourceName);
/* 181 */     Connection conn = getConn(dsName);
/* 182 */     prepareConn(dsName, conn);
/* 183 */     return conn;
/*     */   }
/*     */ 
/*     */   public void freeDbConn(Connection conn) {
/*     */     try {
/* 188 */       if (conn != null)
/* 189 */         conn.close();
/*     */     }
/*     */     catch (SQLException ex) {
/* 192 */       LogHome.getLog().error("关闭数据库连接异常：", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public DbContext fetchDbContext(String dsName) throws Exception {
/* 197 */     return getDbContext(dsName, fetchDbConn(dsName));
/*     */   }
/*     */ 
/*     */   public DbContext getDbContext() {
/* 201 */     return getDbContext("GLOBAL_DS_NAME");
/*     */   }
/*     */ 
/*     */   public DbContext getDbContext(String dsName) {
/* 205 */     return (DbContext)this.dbContextTable.get(dsName);
/*     */   }
/*     */ 
/*     */   public DbContext getDbContext(String dsName, Connection conn) {
/* 209 */     DbContext dbContext = null;
/*     */     try {
/* 211 */       dbContext = ((DbContext)this.dbContextTable.get(dsName)).clone();
/* 212 */       dbContext.setDbConn(conn);
/* 213 */       String dbProductVersion = conn.getMetaData().getDatabaseProductVersion();
/* 214 */       dbContext.setDbProductVersion(dbProductVersion);
/*     */     }
/*     */     catch (Exception ex) {
/* 217 */       LogHome.getLog().error("", ex);
/*     */     }
/* 219 */     return dbContext;
/*     */   }
/*     */ 
/*     */   public void setDefaultDsName(String defaultDsName) {
/* 223 */     this.defaultDsName = defaultDsName;
/*     */   }
/*     */ 
/*     */   public DbType getDbType(String dsName) {
/* 227 */     if ((dsName == null) || (dsName.trim().length() == 0)) {
/* 228 */       dsName = getDefaultDsName();
/*     */     }
/*     */ 
/* 231 */     DbContext dbContext = (DbContext)this.dbContextTable.get(dsName);
/* 232 */     return dbContext.getDbType();
/*     */   }
/*     */ 
/*     */   public DbType getDbType() {
/* 236 */     return getDbType(this.defaultDsName);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbConnManager
 * JD-Core Version:    0.6.0
 */