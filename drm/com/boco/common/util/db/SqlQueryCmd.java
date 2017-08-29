/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlQueryCmd extends AbstractSqlQueryCmd
/*     */ {
/*     */   private Statement statement;
/*     */   private ResultSet resultset;
/*     */ 
/*     */   protected SqlQueryCmd(DbContext dbContext)
/*     */   {
/*  35 */     super(dbContext);
/*     */   }
/*     */ 
/*     */   public boolean selectIntoDBO(String sql, IDbModel[] dbos) throws Exception {
/*  39 */     Statement st = null;
/*  40 */     ResultSet rs = null;
/*  41 */     boolean isFetchData = false;
/*     */     try
/*     */     {
/*  44 */       sql = SqlQueryCmdHelper.prepareSqlStar(sql, dbos);
/*  45 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/*  46 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/*  47 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dbos);
/*  48 */       LogHome.getLog().info("SQL=" + sql);
/*  49 */       st = getDbConn().createStatement();
/*  50 */       st.setFetchSize(1);
/*  51 */       rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/*  52 */       ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/*  53 */       if (rs.next()) {
/*  54 */         isFetchData = true;
/*  55 */         for (int i = 0; i < queryFieldList.length; i++) {
/*  56 */           String queryFieldName = queryFieldList[i];
/*  57 */           String queryFieldPrefix = SqlParser.parseQueryFieldPrefix(queryFieldName);
/*  58 */           String dbFieldName = SqlParser.parseDBFieldName(queryFieldName);
/*  59 */           IDbModel dbo = (IDbModel)dboMap.get(queryFieldPrefix);
/*     */ 
/*  61 */           setDBOField(rsw, i + 1, dbFieldName, dbo);
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) {
/*  65 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/*  66 */       throw ex;
/*     */     } finally {
/*  68 */       DbHelper.closeResultSet(rs);
/*  69 */       DbHelper.closeStatement(st);
/*  70 */       clearCmd();
/*     */     }
/*     */ 
/*  73 */     return isFetchData;
/*     */   }
/*     */ 
/*     */   public DbCollection selectDBOs(String sql, IDbModel[] dboTemplates) throws Exception {
/*  77 */     return selectDBOs(sql, 0, 0, false, dboTemplates);
/*     */   }
/*     */ 
/*     */   public DbCollection selectDBOs(String sql, int offset, int fetchSize, boolean isCountBeforQuery, IDbModel[] dboTemplates) throws Exception {
/*  81 */     Statement st = null;
/*  82 */     ResultSet rs = null;
/*  83 */     DbCollection rows = null;
/*     */     try
/*     */     {
/*  86 */       sql = SqlQueryCmdHelper.prepareSqlStar(sql, dboTemplates);
/*  87 */       rows = new DbCollection(super.getDbContext().getDsName(), offset, fetchSize);
/*  88 */       if (isCountBeforQuery) {
/*  89 */         int count = getCountBeforQuery(sql);
/*  90 */         if (count == 0) {
/*  91 */           LogHome.getLog().info("SQL='" + sql + "' Count is 0");
/*  92 */           DbCollection localDbCollection1 = rows;
/*     */           return localDbCollection1;
/*     */         }
/*  94 */         rows.setCountValue(count);
/*     */       }
/*     */ 
/*  97 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/*  98 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/*  99 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/*     */ 
/* 101 */       LogHome.getLog().info("SQL=" + sql);
/* 102 */       if (fetchSize > 0) {
/* 103 */         st = getDbConn().createStatement(1004, 1007);
/* 104 */         st.setFetchSize(fetchSize);
/*     */       } else {
/* 106 */         st = getDbConn().createStatement();
/*     */       }
/*     */ 
/* 109 */       rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 110 */       if ((offset != 0) && 
/* 111 */         (!rs.absolute(offset))) {
/* 112 */         LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/* 113 */         DbCollection localDbCollection2 = rows;
/*     */         return localDbCollection2;
/*     */       }
/* 117 */       ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/* 118 */       for (int k = 0; (rs.next()) && (
/* 119 */         (fetchSize <= 0) || (k < fetchSize)); k++)
/*     */       {
/* 123 */         DbRow row = new DbRow();
/* 124 */         for (int i = 0; i < queryFieldList.length; i++) {
/* 125 */           String queryFieldName = queryFieldList[i];
/* 126 */           String queryFieldPrefix = SqlParser.parseQueryFieldPrefix(queryFieldName);
/* 127 */           String dbFieldName = SqlParser.parseDBFieldName(queryFieldName);
/* 128 */           IDbModel dboTemplate = (IDbModel)dboMap.get(queryFieldPrefix);
/* 129 */           IDbModel dbo = (IDbModel)row.get(dboTemplate.getClass().getName());
/* 130 */           if (dbo == null) {
/* 131 */             dbo = (IDbModel)dboTemplate.getClass().newInstance();
/* 132 */             row.putDbo(dbo.getClass().getName(), dbo);
/*     */           }
/*     */ 
/* 135 */           setDBOField(rsw, i + 1, dbFieldName, dbo);
/*     */         }
/*     */ 
/* 138 */         rows.add(row);
/*     */       }
/*     */     } catch (Exception ex) {
/* 141 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 142 */       throw ex;
/*     */     } finally {
/* 144 */       DbHelper.closeResultSet(rs);
/* 145 */       DbHelper.closeStatement(st);
/* 146 */       clearCmd();
/*     */     }
/*     */ 
/* 149 */     return rows;
/*     */   }
/*     */ 
/*     */   public DbCollection getFirstDbos(String sql, int fetchSize, boolean isCountBeforQuery, IDbModel[] dboTemplates) throws Exception {
/* 153 */     DbCollection rows = null;
/* 154 */     int offset = 0;
/* 155 */     setPermitClearCmd(false);
/*     */     try {
/* 157 */       sql = SqlQueryCmdHelper.prepareSqlStar(sql, dboTemplates);
/* 158 */       rows = new DbCollection(super.getDbContext().getDsName(), offset, fetchSize);
/* 159 */       if (isCountBeforQuery) {
/* 160 */         int count = getCountBeforQuery(sql);
/* 161 */         if (count == 0) {
/* 162 */           LogHome.getLog().info("SQL='" + sql + "' Count is 0");
/* 163 */           return rows;
/*     */         }
/* 165 */         rows.setCountValue(count);
/*     */       }
/*     */ 
/* 168 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/* 169 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 170 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/*     */ 
/* 172 */       LogHome.getLog().info("SQL=" + sql);
/* 173 */       if (fetchSize > 0) {
/* 174 */         this.statement = getDbConn().createStatement(1004, 1007);
/* 175 */         this.statement.setFetchSize(fetchSize);
/*     */       } else {
/* 177 */         this.statement = getDbConn().createStatement();
/*     */       }
/*     */ 
/* 180 */       this.resultset = this.statement.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 181 */       if ((offset != 0) && 
/* 182 */         (!this.resultset.absolute(offset))) {
/* 183 */         LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/* 184 */         return rows;
/*     */       }
/*     */ 
/* 188 */       ResultsetWrapper rsw = new ResultsetWrapper(this.resultset, getDbContext());
/* 189 */       for (int k = 0; (this.resultset.next()) && (
/* 190 */         (fetchSize <= 0) || (k < fetchSize)); k++)
/*     */       {
/* 194 */         DbRow row = new DbRow();
/* 195 */         for (int i = 0; i < queryFieldList.length; i++) {
/* 196 */           String queryFieldName = queryFieldList[i];
/* 197 */           String queryFieldPrefix = SqlParser.parseQueryFieldPrefix(queryFieldName);
/* 198 */           String dbFieldName = SqlParser.parseDBFieldName(queryFieldName);
/* 199 */           IDbModel dboTemplate = (IDbModel)dboMap.get(queryFieldPrefix);
/* 200 */           IDbModel dbo = (IDbModel)row.get(dboTemplate.getClass().getName());
/* 201 */           if (dbo == null) {
/* 202 */             dbo = (IDbModel)dboTemplate.getClass().newInstance();
/* 203 */             row.putDbo(dbo.getClass().getName(), dbo);
/*     */           }
/*     */ 
/* 206 */           setDBOField(rsw, i + 1, dbFieldName, dbo);
/*     */         }
/*     */ 
/* 209 */         rows.add(row);
/*     */       }
/*     */     } catch (Exception ex) {
/* 212 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 213 */       throw ex;
/*     */     }
/*     */ 
/* 216 */     return rows;
/*     */   }
/*     */ 
/*     */   public DbCollection getNextDbos(String sql, int offset, int fetchSize, IDbModel[] dboTemplates) throws Exception {
/* 220 */     if ((this.statement == null) || (this.resultset == null)) {
/* 221 */       throw new UserException("请执行selectFirstDbos后调用本方法 !");
/*     */     }
/* 223 */     DbCollection rows = null;
/*     */     try
/*     */     {
/* 226 */       sql = SqlQueryCmdHelper.prepareSqlStar(sql, dboTemplates);
/* 227 */       rows = new DbCollection(super.getDbContext().getDsName(), offset, fetchSize);
/*     */ 
/* 229 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/* 230 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 231 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/* 232 */       if ((offset != 0) && 
/* 233 */         (!this.resultset.absolute(offset))) {
/* 234 */         LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/* 235 */         return rows;
/*     */       }
/*     */ 
/* 239 */       ResultsetWrapper rsw = new ResultsetWrapper(this.resultset, getDbContext());
/* 240 */       for (int k = 0; (this.resultset.next()) && (
/* 241 */         (fetchSize <= 0) || (k < fetchSize)); k++)
/*     */       {
/* 245 */         DbRow row = new DbRow();
/* 246 */         for (int i = 0; i < queryFieldList.length; i++) {
/* 247 */           String queryFieldName = queryFieldList[i];
/* 248 */           String queryFieldPrefix = SqlParser.parseQueryFieldPrefix(queryFieldName);
/* 249 */           String dbFieldName = SqlParser.parseDBFieldName(queryFieldName);
/* 250 */           IDbModel dboTemplate = (IDbModel)dboMap.get(queryFieldPrefix);
/* 251 */           IDbModel dbo = (IDbModel)row.get(dboTemplate.getClass().getName());
/* 252 */           if (dbo == null) {
/* 253 */             dbo = (IDbModel)dboTemplate.getClass().newInstance();
/* 254 */             row.putDbo(dbo.getClass().getName(), dbo);
/*     */           }
/*     */ 
/* 257 */           setDBOField(rsw, i + 1, dbFieldName, dbo);
/*     */         }
/*     */ 
/* 260 */         rows.add(row);
/*     */       }
/*     */     } catch (Exception ex) {
/* 263 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 264 */       throw ex;
/*     */     }
/*     */ 
/* 267 */     return rows;
/*     */   }
/*     */ 
/*     */   public void closeResultDbos() {
/* 271 */     DbHelper.closeResultSet(this.resultset);
/* 272 */     DbHelper.closeStatement(this.statement);
/* 273 */     clearCmd();
/*     */   }
/*     */ 
/*     */   private static void setDBOField(ResultsetWrapper rsw, int columnIIndex, String dbFieldName, IDbModel dbo) throws Exception
/*     */   {
/* 278 */     Field field = dbo.getClass().getField(dbFieldName);
/* 279 */     if ((field.getType() == Byte.TYPE) || (field.getType() == Byte.class)) {
/* 280 */       field.set(dbo, new Byte(rsw.getByte(columnIIndex)));
/* 281 */     } else if ((field.getType() == Short.TYPE) || (field.getType() == Short.class)) {
/* 282 */       field.set(dbo, new Short(rsw.getShort(columnIIndex)));
/* 283 */     } else if ((field.getType() == Integer.TYPE) || (field.getType() == Integer.class)) {
/* 284 */       field.set(dbo, new Integer(rsw.getInt(columnIIndex)));
/* 285 */     } else if ((field.getType() == Long.TYPE) || (field.getType() == Long.class)) {
/* 286 */       field.set(dbo, new Long(rsw.getLong(columnIIndex)));
/* 287 */     } else if ((field.getType() == Float.TYPE) || (field.getType() == Float.class)) {
/* 288 */       field.set(dbo, new Float(rsw.getFloat(columnIIndex)));
/* 289 */     } else if ((field.getType() == Boolean.TYPE) || (field.getType() == Boolean.class)) {
/* 290 */       field.set(dbo, new Boolean(rsw.getBoolean(columnIIndex)));
/* 291 */     } else if (field.getType() == String.class) {
/* 292 */       field.set(dbo, rsw.getString(columnIIndex));
/* 293 */     } else if (field.getType() == Date.class) {
/* 294 */       field.set(dbo, rsw.getDate(columnIIndex));
/* 295 */     } else if (field.getType() == Time.class) {
/* 296 */       field.set(dbo, rsw.getTime(columnIIndex));
/* 297 */     } else if (field.getType() == Timestamp.class) {
/* 298 */       field.set(dbo, rsw.getTimestamp(columnIIndex));
/* 299 */     } else if (field.getType() == DbBlob.class) {
/* 300 */       DbBlob dbBlob = rsw.getBlob(columnIIndex);
/* 301 */       field.set(dbo, dbBlob);
/*     */     } else {
/* 303 */       throw new Exception("" + field.getType().getName() + ", 没有处理的数据库类型 ！");
/*     */     }
/*     */   }
/*     */ 
/*     */   public DbCollection selectCollections(String sql, Class[] fieldClasses)
/*     */     throws Exception
/*     */   {
/* 310 */     return selectCollections(sql, 0, 0, false, fieldClasses);
/*     */   }
/*     */ 
/*     */   public DbCollection selectCollections(String sql, int offset, int fetchSize, boolean isCountBeforQuery, Class[] fieldClasses) throws Exception
/*     */   {
/* 315 */     DbCollection rows = null;
/* 316 */     Statement st = null;
/* 317 */     ResultSet rs = null;
/*     */     try
/*     */     {
/* 320 */       rows = new DbCollection(super.getDbContext().getDsName(), offset, fetchSize);
/* 321 */       if (isCountBeforQuery) {
/* 322 */         int count = getCountBeforQuery(sql);
/* 323 */         if (count == 0) {
/* 324 */           LogHome.getLog().info("SQL='" + sql + "' Count is 0");
/* 325 */           DbCollection localDbCollection1 = rows;
/*     */           return localDbCollection1;
/*     */         }
/* 327 */         rows.setCountValue(count);
/*     */       }
/*     */ 
/* 330 */       String[] queryFields = SqlParser.parseQueryFields(sql);
/*     */       int i;
/* 331 */       if ((fieldClasses == null) || (fieldClasses.length == 0)) {
/* 332 */         fieldClasses = new Class[queryFields.length];
/* 333 */         for (i = 0; i < queryFields.length; i++)
/* 334 */           fieldClasses[i] = String.class;
/*     */       }
/* 336 */       else if (fieldClasses.length != queryFields.length) {
/* 337 */         throw new UserException("查询字段和类型数组长度不一致 ！");
/*     */       }
/*     */ 
/* 340 */       if (fetchSize > 0) {
/* 341 */         st = getDbConn().createStatement(1004, 1007);
/* 342 */         st.setFetchSize(fetchSize);
/*     */       } else {
/* 344 */         st = getDbConn().createStatement();
/*     */       }
/*     */ 
/* 347 */       LogHome.getLog().info("SQL=" + sql);
/* 348 */       rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 349 */       if (offset != 0) {
/* 350 */         if (!rs.absolute(offset)) {
/* 351 */           LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/*     */         }
/* 353 */         DbCollection localDbCollection2 = rows;
/*     */         return localDbCollection2;
/*     */       }
/* 356 */       ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/* 357 */       for (int k = 0; (rs.next()) && (
/* 358 */         (fetchSize <= 0) || (k < fetchSize)); k++)
/*     */       {
/* 362 */         DbRow row = new DbRow();
/* 363 */         for (int i = 0; i < queryFields.length; i++) {
/* 364 */           setCollectionField(rsw, i + 1, row, fieldClasses[i]);
/*     */         }
/* 366 */         rows.add(row);
/*     */       }
/*     */     } catch (Exception ex) {
/* 369 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 370 */       throw ex;
/*     */     } finally {
/* 372 */       DbHelper.closeResultSet(rs);
/* 373 */       DbHelper.closeStatement(st);
/* 374 */       clearCmd();
/*     */     }
/*     */ 
/* 377 */     return rows;
/*     */   }
/*     */ 
/*     */   private static void setCollectionField(ResultsetWrapper rsw, int columnIIndex, DbRow row, Class fieldClass) throws Exception
/*     */   {
/* 382 */     String queryFieldName = Integer.toString(columnIIndex);
/* 383 */     if ((fieldClass == Byte.TYPE) || (fieldClass == Byte.class))
/* 384 */       row.putByte(queryFieldName, rsw.getByte(columnIIndex));
/* 385 */     else if ((fieldClass == Short.TYPE) || (fieldClass == Short.class))
/* 386 */       row.putShort(queryFieldName, rsw.getShort(columnIIndex));
/* 387 */     else if ((fieldClass == Integer.TYPE) || (fieldClass == Integer.class))
/* 388 */       row.putInt(queryFieldName, rsw.getInt(columnIIndex));
/* 389 */     else if ((fieldClass == Long.TYPE) || (fieldClass == Long.class))
/* 390 */       row.putLong(queryFieldName, rsw.getLong(columnIIndex));
/* 391 */     else if ((fieldClass == Float.TYPE) || (fieldClass == Float.class))
/* 392 */       row.putFloat(queryFieldName, rsw.getFloat(columnIIndex));
/* 393 */     else if ((fieldClass == Boolean.TYPE) || (fieldClass == Short.class))
/* 394 */       row.putBoolean(queryFieldName, rsw.getBoolean(columnIIndex));
/* 395 */     else if (fieldClass == String.class)
/* 396 */       row.put(queryFieldName, rsw.getString(columnIIndex));
/* 397 */     else if (fieldClass == Date.class)
/* 398 */       row.put(queryFieldName, rsw.getDate(columnIIndex));
/* 399 */     else if (fieldClass == Time.class)
/* 400 */       row.put(queryFieldName, rsw.getTime(columnIIndex));
/* 401 */     else if (fieldClass == Timestamp.class)
/* 402 */       row.put(queryFieldName, rsw.getTimestamp(columnIIndex));
/* 403 */     else if (fieldClass == DbBlob.class)
/* 404 */       row.put(queryFieldName, rsw.getBlob(columnIIndex));
/*     */     else
/* 406 */       throw new Exception("" + fieldClass.getName() + ", 没有处理的数据库类型 ！");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.SqlQueryCmd
 * JD-Core Version:    0.6.0
 */