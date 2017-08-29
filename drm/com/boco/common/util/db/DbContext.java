/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.transnms.common.cfg.EncryptManager;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DbContext
/*     */   implements Cloneable, Serializable
/*     */ {
/*  31 */   public static String defaultDsImplName = "org.apache.commons.dbcp.BasicDataSource";
/*     */   private String userName;
/*     */   private String password;
/*     */   private String dbUrl;
/*     */   private Connection dbConn;
/*     */   private String dbCharset;
/*     */   private Object userAddition;
/*     */   private String dsName;
/*  40 */   private int maxActive = 10;
/*  41 */   private int initialSize = 8;
/*  42 */   private int maxIdle = 8;
/*  43 */   private int minIdle = 8;
/*     */   private String dbProductVersion;
/*  45 */   private int serverId = -1;
/*  46 */   private int batchSize = 500;
/*  47 */   private String dsImplName = defaultDsImplName;
/*  48 */   private boolean encrypt = true;
/*     */   private Map dbExtPropperties;
/*     */ 
/*     */   public DbContext()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DbContext(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive)
/*     */   {
/*  56 */     this(dsName, dbUrl, userName, password, dbCharset, maxActive, true, -1);
/*     */   }
/*     */ 
/*     */   public DbContext(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt)
/*     */   {
/*  61 */     this(dsName, dbUrl, userName, password, dbCharset, maxActive, true, -1);
/*     */   }
/*     */ 
/*     */   public DbContext(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int serverId)
/*     */   {
/*  67 */     this(defaultDsImplName, dsName, dbUrl, userName, password, dbCharset, maxActive, encrypt, serverId);
/*     */   }
/*     */ 
/*     */   public DbContext(String implName, String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int serverId)
/*     */   {
/*  72 */     this.dsImplName = implName;
/*  73 */     this.dsName = dsName;
/*  74 */     this.userName = userName;
/*  75 */     String passwd = password == null ? "" : password;
/*  76 */     if (encrypt) {
/*  77 */       passwd = EncryptManager.getInstance().getDecryptPwd(passwd);
/*     */     }
/*  79 */     this.password = passwd;
/*  80 */     this.dbUrl = dbUrl;
/*  81 */     this.dbCharset = dbCharset;
/*  82 */     this.maxActive = maxActive;
/*  83 */     this.encrypt = encrypt;
/*  84 */     this.serverId = serverId;
/*     */   }
/*     */ 
/*     */   public DbContext(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int initialSize, int maxIdle, int minIdle, int serverId)
/*     */   {
/*  89 */     this(defaultDsImplName, dsName, dbUrl, userName, password, dbCharset, maxActive, encrypt, initialSize, maxIdle, minIdle, serverId, 500);
/*     */   }
/*     */ 
/*     */   public DbContext(String implName, String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int initialSize, int maxIdle, int minIdle, int serverId)
/*     */   {
/*  95 */     this(implName, dsName, dbUrl, userName, password, dbCharset, maxActive, encrypt, initialSize, maxIdle, minIdle, serverId, 500);
/*     */   }
/*     */ 
/*     */   public DbContext(String implName, String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int initialSize, int maxIdle, int minIdle, int serverId, int batchSize)
/*     */   {
/* 102 */     this.dsImplName = implName;
/* 103 */     this.dsName = dsName;
/* 104 */     this.userName = userName;
/* 105 */     String passwd = password == null ? "" : password;
/* 106 */     if (encrypt) {
/* 107 */       passwd = EncryptManager.getInstance().getDecryptPwd(passwd);
/*     */     }
/* 109 */     this.password = passwd;
/* 110 */     this.dbUrl = dbUrl;
/* 111 */     this.dbCharset = dbCharset;
/* 112 */     this.maxActive = maxActive;
/* 113 */     this.encrypt = encrypt;
/* 114 */     this.serverId = serverId;
/* 115 */     this.initialSize = initialSize;
/* 116 */     this.maxIdle = maxIdle;
/* 117 */     this.minIdle = minIdle;
/* 118 */     this.batchSize = batchSize;
/*     */   }
/*     */ 
/*     */   public DbContext(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive, boolean encrypt, int initialSize, int maxIdle, int minIdle)
/*     */   {
/* 124 */     this.dsName = dsName;
/* 125 */     this.userName = userName;
/* 126 */     String passwd = password == null ? "" : password;
/* 127 */     if (encrypt) {
/* 128 */       passwd = EncryptManager.getInstance().getDecryptPwd(passwd);
/*     */     }
/* 130 */     this.password = passwd;
/* 131 */     this.dbUrl = dbUrl;
/* 132 */     this.dbCharset = dbCharset;
/* 133 */     this.maxActive = maxActive;
/* 134 */     this.encrypt = encrypt;
/* 135 */     this.initialSize = initialSize;
/* 136 */     this.maxIdle = maxIdle;
/* 137 */     this.minIdle = minIdle;
/*     */   }
/*     */ 
/*     */   public String getDbCharset() {
/* 141 */     return this.dbCharset;
/*     */   }
/*     */ 
/*     */   public Connection getDbConn() {
/* 145 */     return this.dbConn;
/*     */   }
/*     */ 
/*     */   public DbType getDbType() throws UserException {
/* 149 */     return DbHelper.getDbType(this.dbUrl);
/*     */   }
/*     */ 
/*     */   public String getDbProductVersion() {
/* 153 */     return this.dbProductVersion;
/*     */   }
/*     */ 
/*     */   public String getDbUrl() {
/* 157 */     return this.dbUrl;
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 161 */     return this.password;
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/* 165 */     return this.userName;
/*     */   }
/*     */ 
/*     */   public Object getUserAddition() {
/* 169 */     return this.userAddition;
/*     */   }
/*     */ 
/*     */   public String getDsName() {
/* 173 */     return this.dsName;
/*     */   }
/*     */ 
/*     */   public int getMaxActive() {
/* 177 */     return this.maxActive;
/*     */   }
/*     */ 
/*     */   public void setUserAddition(Object userAddition) {
/* 181 */     this.userAddition = userAddition;
/*     */   }
/*     */ 
/*     */   public void setDbCharset(String dbCharset) {
/* 185 */     this.dbCharset = dbCharset;
/*     */   }
/*     */ 
/*     */   public void setDbConn(Connection dbConn) {
/* 189 */     this.dbConn = dbConn;
/*     */   }
/*     */ 
/*     */   public void setDbUrl(String dbUrl) {
/* 193 */     this.dbUrl = dbUrl;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password) {
/* 197 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public void setUserName(String userName) {
/* 201 */     this.userName = userName;
/*     */   }
/*     */ 
/*     */   public void setDsName(String dbPoolName) {
/* 205 */     this.dsName = dbPoolName;
/*     */   }
/*     */ 
/*     */   public void setMaxActive(int maxActive) {
/* 209 */     this.maxActive = maxActive;
/*     */   }
/*     */ 
/*     */   public void setDbProductVersion(String dbProductVersion) {
/* 213 */     this.dbProductVersion = dbProductVersion;
/*     */   }
/*     */ 
/*     */   public int getServerId() {
/* 217 */     return this.serverId;
/*     */   }
/*     */ 
/*     */   public void setServerId(int serverId) {
/* 221 */     this.serverId = serverId;
/*     */   }
/*     */ 
/*     */   public int getInitialSize() {
/* 225 */     return this.initialSize;
/*     */   }
/*     */ 
/*     */   public void setInitialSize(int initialSize) {
/* 229 */     this.initialSize = initialSize;
/*     */   }
/*     */ 
/*     */   public int getMaxIdle() {
/* 233 */     return this.maxIdle;
/*     */   }
/*     */ 
/*     */   public void setMaxIdle(int maxIdle) {
/* 237 */     this.maxIdle = maxIdle;
/*     */   }
/*     */ 
/*     */   public int getMinIdle() {
/* 241 */     return this.minIdle;
/*     */   }
/*     */ 
/*     */   public void setMinIdle(int minIdle) {
/* 245 */     this.minIdle = minIdle;
/*     */   }
/*     */ 
/*     */   public int getBatchSize() {
/* 249 */     return this.batchSize;
/*     */   }
/*     */ 
/*     */   public void setBatchSize(int batchSize) {
/* 253 */     this.batchSize = batchSize;
/*     */   }
/*     */ 
/*     */   public String getDsImplName() {
/* 257 */     return this.dsImplName;
/*     */   }
/*     */ 
/*     */   public void setDsImplName(String dsImplName) {
/* 261 */     this.dsImplName = dsImplName;
/*     */   }
/*     */ 
/*     */   public boolean isEncrypt() {
/* 265 */     return this.encrypt;
/*     */   }
/*     */ 
/*     */   public void setEncrypt(boolean encrypt) {
/* 269 */     this.encrypt = encrypt;
/* 270 */     if (encrypt)
/* 271 */       this.password = EncryptManager.getInstance().getDecryptPwd(this.password);
/*     */   }
/*     */ 
/*     */   public Map getDbExtPropperties()
/*     */   {
/* 276 */     return this.dbExtPropperties;
/*     */   }
/*     */ 
/*     */   public void setDbExtPropperties(Map dbExtPropperties) {
/* 280 */     this.dbExtPropperties = dbExtPropperties;
/*     */   }
/*     */ 
/*     */   protected DbContext clone() throws CloneNotSupportedException {
/* 284 */     DbContext dbContext = new DbContext(this.dsImplName, this.dsName, this.dbUrl, this.userName, this.password, this.dbCharset, this.maxActive, false, this.serverId, this.initialSize, this.maxIdle, this.minIdle, this.batchSize);
/*     */ 
/* 288 */     dbContext.setDsName(this.dsName);
/* 289 */     dbContext.setMaxActive(this.maxActive);
/* 290 */     dbContext.setUserAddition(this.userAddition);
/* 291 */     dbContext.setServerId(getServerId());
/* 292 */     dbContext.setInitialSize(this.initialSize);
/* 293 */     dbContext.setMaxIdle(this.maxIdle);
/* 294 */     dbContext.setMinIdle(this.minIdle);
/* 295 */     dbContext.setBatchSize(this.batchSize);
/* 296 */     dbContext.setDbExtPropperties(this.dbExtPropperties);
/* 297 */     return dbContext;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbContext
 * JD-Core Version:    0.6.0
 */