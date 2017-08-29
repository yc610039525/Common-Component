/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.commons.dbcp.BasicDataSource;
/*     */ 
/*     */ public class DbDataSource
/*     */   implements DataSource
/*     */ {
/*     */   private DataSource ds;
/*     */ 
/*     */   public DbDataSource(String className)
/*     */   {
/*     */     try
/*     */     {
/*  14 */       this.ds = ((DataSource)Class.forName(className).newInstance());
/*     */     } catch (InstantiationException e) {
/*  16 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/*  18 */       e.printStackTrace();
/*     */     } catch (ClassNotFoundException e) {
/*  20 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public PrintWriter getLogWriter() throws SQLException {
/*  25 */     return this.ds.getLogWriter();
/*     */   }
/*     */ 
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/*  29 */     this.ds.setLogWriter(out);
/*     */   }
/*     */ 
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/*  33 */     this.ds.setLoginTimeout(seconds);
/*     */   }
/*     */ 
/*     */   public int getLoginTimeout() throws SQLException {
/*  37 */     return this.ds.getLoginTimeout();
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*  41 */     return this.ds.unwrap(iface);
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  45 */     return this.ds.isWrapperFor(iface);
/*     */   }
/*     */ 
/*     */   public Connection getConnection() throws SQLException {
/*  49 */     return this.ds.getConnection();
/*     */   }
/*     */ 
/*     */   public Connection getConnection(String username, String password) throws SQLException
/*     */   {
/*  54 */     return this.ds.getConnection(username, password);
/*     */   }
/*     */ 
/*     */   public void setDriverClassName(String driverClassName) {
/*  58 */     if ((this.ds instanceof BasicDataSource))
/*  59 */       ((BasicDataSource)this.ds).setDriverClassName(driverClassName);
/*     */   }
/*     */ 
/*     */   public void setValidationQuery(String validationQuery)
/*     */   {
/*  66 */     if ((this.ds instanceof BasicDataSource))
/*  67 */       ((BasicDataSource)this.ds).setValidationQuery(validationQuery);
/*     */   }
/*     */ 
/*     */   public void setUsername(String userName)
/*     */   {
/*  74 */     if ((this.ds instanceof BasicDataSource))
/*  75 */       ((BasicDataSource)this.ds).setUsername(userName);
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/*  82 */     if ((this.ds instanceof BasicDataSource))
/*  83 */       ((BasicDataSource)this.ds).setPassword(password);
/*     */   }
/*     */ 
/*     */   public void setUrl(String url)
/*     */   {
/*  90 */     if ((this.ds instanceof BasicDataSource))
/*  91 */       ((BasicDataSource)this.ds).setUrl(url);
/*     */   }
/*     */ 
/*     */   public void setMaxActive(int maxActive)
/*     */   {
/*  98 */     if ((this.ds instanceof BasicDataSource))
/*  99 */       ((BasicDataSource)this.ds).setMaxActive(maxActive);
/*     */   }
/*     */ 
/*     */   public int getMaxActive()
/*     */   {
/* 107 */     if ((this.ds instanceof BasicDataSource)) {
/* 108 */       return ((BasicDataSource)this.ds).getMaxActive();
/*     */     }
/*     */ 
/* 112 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getNumActive()
/*     */   {
/* 117 */     if ((this.ds instanceof BasicDataSource)) {
/* 118 */       return ((BasicDataSource)this.ds).getNumActive();
/*     */     }
/*     */ 
/* 122 */     return -1;
/*     */   }
/*     */ 
/*     */   public void setInitialSize(int initialSize)
/*     */   {
/* 127 */     if ((this.ds instanceof BasicDataSource))
/* 128 */       ((BasicDataSource)this.ds).setInitialSize(initialSize);
/*     */   }
/*     */ 
/*     */   public int getNumIdle()
/*     */   {
/* 136 */     if ((this.ds instanceof BasicDataSource)) {
/* 137 */       return ((BasicDataSource)this.ds).getNumIdle();
/*     */     }
/*     */ 
/* 141 */     return -1;
/*     */   }
/*     */ 
/*     */   public void setMaxIdle(int maxIdle)
/*     */   {
/* 146 */     if ((this.ds instanceof BasicDataSource))
/* 147 */       ((BasicDataSource)this.ds).setMaxIdle(maxIdle);
/*     */   }
/*     */ 
/*     */   public void setMaxWait(int maxWait)
/*     */   {
/* 155 */     if ((this.ds instanceof BasicDataSource))
/* 156 */       ((BasicDataSource)this.ds).setMaxWait(maxWait);
/*     */   }
/*     */ 
/*     */   public void addConnectionProperty(String key, String value)
/*     */   {
/* 164 */     if ((this.ds instanceof BasicDataSource))
/* 165 */       ((BasicDataSource)this.ds).addConnectionProperty(key, value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbDataSource
 * JD-Core Version:    0.6.0
 */