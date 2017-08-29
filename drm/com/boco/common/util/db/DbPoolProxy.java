/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import java.sql.Connection;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DbPoolProxy
/*     */ {
/*     */   private static final int MAX_WAIT_TIME = 600000;
/*     */   private final String dsName;
/*     */   private DbDataSource dataSource;
/*     */ 
/*     */   protected DbPoolProxy(DbContext dbContext)
/*     */     throws Exception
/*     */   {
/*  38 */     this(dbContext, dbContext.getDbExtPropperties());
/*     */   }
/*     */ 
/*     */   protected DbPoolProxy(DbContext dbContext, Map dbProps) throws Exception {
/*  42 */     this.dsName = dbContext.getDsName();
/*     */     try {
/*  44 */       this.dataSource = createDataSource(dbContext, 600000, dbProps);
/*  45 */       checkDbConn();
/*     */     } catch (Exception ex) {
/*  47 */       LogHome.getLog().error("数据源配置错误：dsName=" + this.dsName + ", dbUrl=" + dbContext.getDbUrl() + ", userName=" + dbContext.getUserName() + ", password=" + dbContext.getPassword());
/*     */ 
/*  49 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkDbConn() throws Exception {
/*  54 */     Connection conn = getConnection();
/*  55 */     DbHelper.showConnectionInfo(conn);
/*  56 */     conn.close();
/*     */   }
/*     */ 
/*     */   public void checkDbpool() {
/*  60 */     LogHome.getLog().warn("检查数据库连接池：dsName=" + this.dsName + ", maxActive=" + this.dataSource.getMaxActive() + ", numActive=" + this.dataSource.getNumActive() + ", numIdle=" + this.dataSource.getNumIdle());
/*     */   }
/*     */ 
/*     */   private DbDataSource createDataSource(DbContext dbContext, int maxWaitTime, Map dbProps)
/*     */     throws Exception
/*     */   {
/*  66 */     String dsImplName = dbContext.getDsImplName() != null ? dbContext.getDsImplName() : DbContext.defaultDsImplName;
/*  67 */     DbDataSource basicDataSource = new DbDataSource(dsImplName);
/*  68 */     basicDataSource.setDriverClassName(DbHelper.getDbDriverClassName(dbContext.getDbUrl()));
/*  69 */     basicDataSource.setValidationQuery(DbHelper.getValidationQuery(dbContext.getDbUrl()));
/*  70 */     basicDataSource.setUsername(dbContext.getUserName());
/*  71 */     basicDataSource.setPassword(dbContext.getPassword());
/*  72 */     basicDataSource.setUrl(dbContext.getDbUrl());
/*  73 */     if (dbContext.getMaxActive() > 0)
/*  74 */       basicDataSource.setMaxActive(dbContext.getMaxActive());
/*     */     else {
/*  76 */       basicDataSource.setMaxActive(1);
/*     */     }
/*  78 */     if (dbContext.getInitialSize() > 0) {
/*  79 */       basicDataSource.setInitialSize(dbContext.getInitialSize());
/*     */     }
/*  81 */     if (dbContext.getMaxIdle() > 0) {
/*  82 */       basicDataSource.setMaxIdle(dbContext.getMaxIdle());
/*     */     }
/*  84 */     if (dbContext.getMinIdle() > 0) {
/*  85 */       basicDataSource.setMaxIdle(dbContext.getMinIdle());
/*     */     }
/*     */ 
/*  88 */     if ((dbProps != null) && (dbProps.size() > 0)) {
/*  89 */       String[] keys = new String[dbProps.size()];
/*  90 */       dbProps.keySet().toArray(keys);
/*  91 */       for (int i = 0; i < keys.length; i++) {
/*  92 */         String prop = (String)dbProps.get(keys[i]);
/*  93 */         basicDataSource.addConnectionProperty(keys[i], prop);
/*     */       }
/*     */     }
/*     */ 
/*  97 */     basicDataSource.setMaxWait(maxWaitTime);
/*     */ 
/*  99 */     return basicDataSource;
/*     */   }
/*     */ 
/*     */   public String getDsName() {
/* 103 */     return this.dsName;
/*     */   }
/*     */ 
/*     */   public Connection getConnection() throws Exception {
/* 107 */     long startTime = System.currentTimeMillis();
/* 108 */     Connection conn = this.dataSource.getConnection();
/* 109 */     long expendTime = System.currentTimeMillis() - startTime;
/* 110 */     if (conn == null) {
/* 111 */       LogHome.getLog().warn("Out of DBConnection threadId=" + ThreadHelper.getCurrentThreadId() + ", createConnTime=" + expendTime + ", numActive=" + this.dataSource.getNumActive() + ", numIdle=" + this.dataSource.getNumIdle());
/*     */     }
/* 113 */     else if (expendTime > 500L) {
/* 114 */       boolean isTransaction = TransactionFactory.getInstance().getTransaction() != null;
/* 115 */       LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", createConnTime=" + expendTime + ", numActive=" + this.dataSource.getNumActive() + ", numIdle=" + this.dataSource.getNumIdle() + ", isTransaction=" + isTransaction);
/*     */     }
/*     */ 
/* 118 */     return conn;
/*     */   }
/*     */ 
/*     */   public DataSource getBasicDataSource() {
/* 122 */     return this.dataSource;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbPoolProxy
 * JD-Core Version:    0.6.0
 */