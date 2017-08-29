/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.db.AbstractSqlQueryCmd;
/*     */ import com.boco.common.util.db.DbContext;
/*     */ import com.boco.common.util.db.DbHelper;
/*     */ import com.boco.common.util.db.DbType;
/*     */ import com.boco.common.util.db.ResultsetWrapper;
/*     */ import com.boco.common.util.db.SqlHelper;
/*     */ import com.boco.common.util.db.SqlParser;
/*     */ import com.boco.common.util.db.SqlQueryCmdHelper;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlQueryDaoCmd extends AbstractSqlQueryCmd
/*     */ {
/*     */   private Statement statement;
/*     */   private ResultSet resultset;
/*     */ 
/*     */   protected SqlQueryDaoCmd(DbContext dbContext)
/*     */   {
/*  47 */     super(dbContext);
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(String sql, GenericDO[] dboTemplates) throws Exception {
/*  51 */     return selectDBOs(null, sql, 0, 0, false, dboTemplates);
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(IBoQueryContext queryContext, String sql, int offset, int fetchSize, boolean isCountBeforQuery, GenericDO[] dboTemplates) throws Exception
/*     */   {
/*  56 */     Statement st = null;
/*  57 */     ResultSet rs = null;
/*  58 */     DboCollection rows = null;
/*     */ 
/*  60 */     long beginTime = System.currentTimeMillis(); long startTime = System.currentTimeMillis();
/*  61 */     long countTime = 0L; long resultTime = 0L; long sqlExecTime = 0L; long offsetTime = 0L; long totalTime = 0L;
/*     */     try {
/*  63 */       LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL=" + sql);
/*  64 */       String psql = SqlQueryDaoCmdHelper.prepareSqlStar(sql, dboTemplates);
/*  65 */       rows = new DboCollection(super.getDbContext().getDsName(), offset, fetchSize);
/*  66 */       if (isCountBeforQuery) {
/*  67 */         int count = getCountBeforQuery(psql);
/*  68 */         countTime = System.currentTimeMillis() - beginTime;
/*  69 */         if (count == 0) {
/*  70 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' Count is 0");
/*  71 */           DboCollection localDboCollection1 = rows;
/*     */           long clearTime;
/*     */           return localDboCollection1;
/*     */         }
/*  72 */         if (count > 2000) {
/*  73 */           LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' Count is :" + count);
/*     */         }
/*  75 */         rows.setCountValue(count);
/*     */       }
/*  77 */       if ((queryContext == null) || (!queryContext.isQueryCountOnly())) {
/*  78 */         String sqlQuery = SqlParser.parseSqlQuery(psql);
/*  79 */         String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/*  80 */         Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/*     */ 
/*  82 */         Connection conn = getDbConn();
/*  83 */         if (fetchSize > 0) {
/*  84 */           st = conn.createStatement(1004, 1007);
/*  85 */           st.setFetchSize(fetchSize);
/*     */         } else {
/*  87 */           st = conn.createStatement();
/*     */         }
/*     */ 
/*  91 */         beginTime = System.currentTimeMillis();
/*  92 */         DbType dbType = super.getDbContext().getDbType();
/*  93 */         String osql = null;
/*  94 */         if ((fetchSize > 0) && ((dbType == DbType.DB_TYPE_ORACLE) || (dbType == DbType.DB_TYPE_INFORMIX))) {
/*  95 */           osql = SqlHelper.getOptimizeSql(super.getDbContext().getDbType(), psql, offset, fetchSize);
/*     */         }
/*  97 */         if ((osql != null) && (!osql.equals(psql)))
/*     */         {
/*  99 */           rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), osql));
/* 100 */           sqlExecTime = System.currentTimeMillis() - beginTime;
/* 101 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ",OptimizeSql SQL=" + sql);
/*     */         } else {
/* 103 */           rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), psql));
/* 104 */           sqlExecTime = System.currentTimeMillis() - beginTime;
/* 105 */           beginTime = System.currentTimeMillis();
/* 106 */           if (offset != 0) {
/* 107 */             if (!rs.absolute(offset)) {
/* 108 */               LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/* 109 */               DboCollection localDboCollection2 = rows;
/*     */               long clearTime;
/*     */               return localDboCollection2;
/*     */             }
/* 111 */             offsetTime = System.currentTimeMillis() - beginTime;
/*     */           }
/*     */         }
/*     */ 
/* 115 */         beginTime = System.currentTimeMillis();
/* 116 */         SqlHelper.fillDataObjectMap(rs, super.getDbContext(), queryFieldList, dboMap, rows, sql, fetchSize);
/* 117 */         resultTime = System.currentTimeMillis() - beginTime;
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       long clearTime;
/* 120 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 121 */       throw ex;
/*     */     } finally {
/* 123 */       beginTime = System.currentTimeMillis();
/* 124 */       DbHelper.closeResultSet(rs);
/* 125 */       DbHelper.closeStatement(st);
/* 126 */       clearCmd();
/* 127 */       totalTime = System.currentTimeMillis() - startTime;
/* 128 */       long clearTime = System.currentTimeMillis() - beginTime;
/* 129 */       if (rows != null) {
/* 130 */         if ((totalTime > TnmsDrmCfg.getInstance().getMaxDbTime()) || (rows.size() > TnmsDrmCfg.getInstance().getMaxQueryResultSize()))
/*     */         {
/* 132 */           LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ",  countTime=" + countTime + ", sqlExecTime=" + sqlExecTime + ", offsetTime=" + offsetTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */         else
/*     */         {
/* 137 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ",  countTime=" + countTime + ", sqlExecTime=" + sqlExecTime + ", offsetTime=" + offsetTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 145 */     return rows;
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOsnoLog(String sql, Class[] colClassType) throws Exception {
/* 149 */     Statement st = null;
/* 150 */     ResultSet rs = null;
/* 151 */     DataObjectList rows = new DataObjectList();
/*     */     try {
/* 153 */       st = getDbConn().createStatement();
/* 154 */       rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 155 */       ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/* 156 */       for (int k = 0; rs.next(); k++) {
/* 157 */         GenericDO row = new GenericDO();
/* 158 */         for (int i = 0; i < colClassType.length; i++) {
/* 159 */           SqlHelper.setDBOField(rsw, i + 1, colClassType[i], row);
/*     */         }
/* 161 */         rows.add(row);
/*     */       }
/*     */     } catch (Exception ex) {
/* 164 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 165 */       throw ex;
/*     */     } finally {
/* 167 */       DbHelper.closeResultSet(rs);
/* 168 */       DbHelper.closeStatement(st);
/* 169 */       clearCmd();
/*     */     }
/* 171 */     return rows;
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(String sql, Class[] colClassType) throws Exception {
/* 175 */     Statement st = null;
/* 176 */     ResultSet rs = null;
/* 177 */     DataObjectList rows = new DataObjectList();
/* 178 */     long beginTime = System.currentTimeMillis(); long startTime = System.currentTimeMillis();
/* 179 */     long resultTime = 0L; long sqlExecTime = 0L; long totalTime = 0L;
/*     */     try {
/* 181 */       beginTime = System.currentTimeMillis();
/* 182 */       LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL=" + sql);
/* 183 */       st = getDbConn().createStatement();
/* 184 */       rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 185 */       sqlExecTime = System.currentTimeMillis() - beginTime;
/* 186 */       beginTime = System.currentTimeMillis();
/* 187 */       ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/* 188 */       for (int k = 0; rs.next(); k++) {
/* 189 */         GenericDO row = new GenericDO();
/* 190 */         for (int i = 0; i < colClassType.length; i++) {
/* 191 */           SqlHelper.setDBOField(rsw, i + 1, colClassType[i], row);
/*     */         }
/* 193 */         rows.add(row);
/*     */       }
/* 195 */       resultTime = System.currentTimeMillis() - beginTime;
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       long clearTime;
/* 197 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 198 */       throw ex;
/*     */     } finally {
/* 200 */       beginTime = System.currentTimeMillis();
/* 201 */       DbHelper.closeResultSet(rs);
/* 202 */       DbHelper.closeStatement(st);
/* 203 */       clearCmd();
/* 204 */       long clearTime = System.currentTimeMillis() - beginTime;
/* 205 */       totalTime = System.currentTimeMillis() - startTime;
/* 206 */       if (rows != null) {
/* 207 */         if ((totalTime > TnmsDrmCfg.getInstance().getMaxDbTime()) || (rows.size() > TnmsDrmCfg.getInstance().getMaxQueryResultSize()))
/*     */         {
/* 209 */           LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ", sqlExecTime=" + sqlExecTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */         else
/*     */         {
/* 214 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ", sqlExecTime=" + sqlExecTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 221 */     return rows;
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(IBoQueryContext queryContext, String sql, int offset, int fetchSize, boolean isCountBeforQuery, Class[] colClassType) throws Exception
/*     */   {
/* 226 */     Statement st = null;
/* 227 */     ResultSet rs = null;
/* 228 */     DataObjectList rows = new DataObjectList(offset, fetchSize);
/* 229 */     long beginTime = System.currentTimeMillis(); long startTime = System.currentTimeMillis();
/* 230 */     long countTime = 0L; long resultTime = 0L; long sqlExecTime = 0L; long offsetTime = 0L; long totalTime = 0L;
/*     */     try
/*     */     {
/*     */       int count;
/* 232 */       if (isCountBeforQuery) {
/* 233 */         count = getCountBeforQuery(sql);
/* 234 */         countTime = System.currentTimeMillis() - beginTime;
/* 235 */         if (count == 0) {
/* 236 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' Count is 0");
/* 237 */           DataObjectList localDataObjectList1 = rows;
/*     */           long clearTime;
/*     */           return localDataObjectList1;
/*     */         }
/* 239 */         rows.setCountValue(count);
/*     */       }
/* 241 */       if ((queryContext == null) || (!queryContext.isQueryCountOnly())) {
/* 242 */         if (fetchSize > 0) {
/* 243 */           st = getDbConn().createStatement(1004, 1007);
/* 244 */           st.setFetchSize(fetchSize);
/*     */         } else {
/* 246 */           st = getDbConn().createStatement();
/*     */         }
/* 248 */         beginTime = System.currentTimeMillis();
/* 249 */         LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL=" + sql);
/* 250 */         rs = st.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), sql));
/* 251 */         sqlExecTime = System.currentTimeMillis() - beginTime;
/* 252 */         beginTime = System.currentTimeMillis();
/* 253 */         if (offset != 0) {
/* 254 */           if (!rs.absolute(offset)) {
/* 255 */             LogHome.getLog().error("游标偏移位置错误，offset=" + offset);
/* 256 */             count = rows;
/*     */             long clearTime;
/*     */             return count;
/*     */           }
/* 258 */           offsetTime = System.currentTimeMillis() - beginTime;
/*     */         }
/* 260 */         beginTime = System.currentTimeMillis();
/* 261 */         ResultsetWrapper rsw = new ResultsetWrapper(rs, getDbContext());
/* 262 */         for (int k = 0; (rs.next()) && (
/* 263 */           (fetchSize <= 0) || (k < fetchSize)); k++)
/*     */         {
/* 266 */           GenericDO row = new GenericDO();
/* 267 */           for (int i = 0; i < colClassType.length; i++) {
/* 268 */             SqlHelper.setDBOField(rsw, i + 1, colClassType[i], row);
/*     */           }
/* 270 */           rows.add(row);
/*     */         }
/* 272 */         resultTime = System.currentTimeMillis() - beginTime;
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       long clearTime;
/* 275 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 276 */       throw ex;
/*     */     } finally {
/* 278 */       beginTime = System.currentTimeMillis();
/* 279 */       DbHelper.closeResultSet(rs);
/* 280 */       DbHelper.closeStatement(st);
/* 281 */       clearCmd();
/* 282 */       long clearTime = System.currentTimeMillis() - beginTime;
/* 283 */       totalTime = System.currentTimeMillis() - startTime;
/* 284 */       if (rows != null) {
/* 285 */         if ((totalTime > TnmsDrmCfg.getInstance().getMaxDbTime()) || (rows.size() > TnmsDrmCfg.getInstance().getMaxQueryResultSize()))
/*     */         {
/* 287 */           LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ",  countTime=" + countTime + ", sqlExecTime=" + sqlExecTime + ", offsetTime=" + offsetTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */         else
/*     */         {
/* 292 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + totalTime + ",  countTime=" + countTime + ", sqlExecTime=" + sqlExecTime + ", offsetTime=" + offsetTime + ", resultTime=" + resultTime + ", clearTime=" + clearTime + ", rowSize=" + rows.size() + ", sql=" + sql);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 299 */     return rows;
/*     */   }
/*     */ 
/*     */   public DboCollection getFirsetDBOs(String sql, int fetchSize, boolean isCountBeforQuery, GenericDO[] dboTemplates) throws Exception
/*     */   {
/* 304 */     int offset = 0;
/* 305 */     DboCollection rows = null;
/* 306 */     setPermitClearCmd(false);
/*     */     try {
/* 308 */       String psql = SqlQueryDaoCmdHelper.prepareSqlStar(sql, dboTemplates);
/* 309 */       rows = new DboCollection(super.getDbContext().getDsName(), offset, fetchSize);
/* 310 */       if (isCountBeforQuery) {
/* 311 */         int count = getCountBeforQuery(psql);
/* 312 */         if (count == 0) {
/* 313 */           LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' Count is 0");
/* 314 */           return rows;
/* 315 */         }if (count > 2000) {
/* 316 */           LogHome.getLog().warn("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' Count is :" + count);
/*     */         }
/* 318 */         rows.setCountValue(count);
/*     */       }
/*     */ 
/* 321 */       String sqlQuery = SqlParser.parseSqlQuery(psql);
/* 322 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 323 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/*     */ 
/* 325 */       LogHome.getLog().info("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL=" + sql);
/* 326 */       if (fetchSize > 0) {
/* 327 */         this.statement = getDbConn().createStatement(1004, 1007);
/* 328 */         this.statement.setFetchSize(fetchSize);
/*     */       } else {
/* 330 */         this.statement = getDbConn().createStatement();
/*     */       }
/*     */ 
/* 333 */       this.resultset = this.statement.executeQuery(SqlHelper.prepareSql(super.getDbContext().getDbCharset(), psql));
/* 334 */       SqlHelper.fillDataObjectMap(this.resultset, super.getDbContext(), queryFieldList, dboMap, rows, sql, fetchSize);
/*     */     } catch (Exception ex) {
/* 336 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 337 */       throw ex;
/*     */     }
/* 339 */     return rows;
/*     */   }
/*     */ 
/*     */   public DboCollection getNextDbos(String sql, int offset, int fetchSize, GenericDO[] dboTemplates) throws Exception
/*     */   {
/* 344 */     if ((this.statement == null) || (this.resultset == null)) {
/* 345 */       throw new UserException("请执行selectFirstDbos后调用本方法 !");
/*     */     }
/*     */ 
/* 348 */     DboCollection rows = null;
/*     */     try
/*     */     {
/* 351 */       String psql = SqlQueryDaoCmdHelper.prepareSqlStar(sql, dboTemplates);
/* 352 */       rows = new DboCollection(super.getDbContext().getDsName(), offset, fetchSize);
/*     */ 
/* 355 */       String sqlQuery = SqlParser.parseSqlQuery(psql);
/* 356 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 357 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, dboTemplates);
/*     */ 
/* 359 */       if ((offset != 0) && 
/* 360 */         (!this.resultset.absolute(offset))) {
/* 361 */         LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", 游标偏移位置错误，offset=" + offset);
/* 362 */         return rows;
/*     */       }
/*     */ 
/* 366 */       SqlHelper.fillDataObjectMap(this.resultset, super.getDbContext(), queryFieldList, dboMap, rows, sql, fetchSize);
/*     */     } catch (Exception ex) {
/* 368 */       LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", SQL='" + sql + "' 执行错误!");
/* 369 */       throw ex;
/*     */     }
/*     */ 
/* 372 */     return rows;
/*     */   }
/*     */ 
/*     */   public void closeResultDbos() {
/* 376 */     DbHelper.closeResultSet(this.resultset);
/* 377 */     DbHelper.closeStatement(this.statement);
/* 378 */     super.setPermitClearCmd(true);
/* 379 */     clearCmd();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.SqlQueryDaoCmd
 * JD-Core Version:    0.6.0
 */