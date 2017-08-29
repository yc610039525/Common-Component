/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SqlQueryCmdHelper
/*     */ {
/*     */   public static Map getDBOMap(String sqlQuery, IDbModel[] dboTemplates)
/*     */     throws Exception
/*     */   {
/*  15 */     Map dboMap = new HashMap();
/*  16 */     String[] queryTableList = SqlParser.parseQueryTables(sqlQuery);
/*  17 */     Map queryTablePostfixMap = new HashMap();
/*  18 */     for (int i = 0; i < queryTableList.length; i++) {
/*  19 */       String queryTableName = queryTableList[i].trim();
/*  20 */       int spaceIndex = queryTableName.indexOf(" ");
/*  21 */       String queryTablePostfix = "";
/*  22 */       String dbTableName = queryTableName;
/*  23 */       if (spaceIndex > 0) {
/*  24 */         queryTablePostfix = queryTableName.substring(spaceIndex + 1, queryTableName.length());
/*     */ 
/*  26 */         dbTableName = queryTableName.substring(0, spaceIndex).trim();
/*     */       }
/*  28 */       queryTablePostfixMap.put(dbTableName, queryTablePostfix);
/*     */     }
/*     */ 
/*  31 */     for (int i = 0; i < dboTemplates.length; i++) {
/*  32 */       String queryTablePostfix = (String)queryTablePostfixMap.get(dboTemplates[i].getTableName());
/*     */ 
/*  34 */       if (dboTemplates[i].getClass() == GenericDO.class) {
/*  35 */         dboMap.put(dboTemplates[i].getTableName(), dboTemplates[i]);
/*     */       } else {
/*  37 */         if (queryTablePostfix == null) {
/*  38 */           throw new Exception("DBO 对象'" + dboTemplates[i].getTableName() + "'， 无有效数据库表映射");
/*     */         }
/*  40 */         dboMap.put(queryTablePostfix, dboTemplates[i]);
/*     */       }
/*     */     }
/*     */ 
/*  44 */     return dboMap;
/*     */   }
/*     */ 
/*     */   public static String prepareSqlStar(String sql, IDbModel[] dboTemplates) {
/*  48 */     Map starFieldTable = new HashMap();
/*  49 */     String dealedSql = sql;
/*     */ 
/*  51 */     if (sql.indexOf(" * ") > 0) {
/*  52 */       starFieldTable.put("*", dboTemplates[0]);
/*  53 */       dealedSql = replaceSqlStar(sql, starFieldTable);
/*  54 */     } else if (sql.indexOf(".*") > 0) {
/*  55 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/*  56 */       String[] queryTables = SqlParser.parseQueryTables(sqlQuery);
/*  57 */       for (int i = 0; i < queryTables.length; i++) {
/*  58 */         String[] spiltTable = queryTables[i].trim().split(" ");
/*  59 */         if (spiltTable.length < 2)
/*     */         {
/*     */           continue;
/*     */         }
/*  63 */         String tablePrefix = spiltTable[1].trim();
/*  64 */         if (sql.indexOf(tablePrefix + ".*") < 0)
/*     */         {
/*     */           continue;
/*     */         }
/*  68 */         String tableName = spiltTable[0].trim();
/*  69 */         IDbModel dbo = null;
/*  70 */         for (int j = 0; j < dboTemplates.length; j++) {
/*  71 */           if (dboTemplates[j].getTableName().equals(tableName)) {
/*  72 */             dbo = dboTemplates[j];
/*     */           }
/*     */         }
/*     */ 
/*  76 */         if (dbo != null) {
/*  77 */           starFieldTable.put(tablePrefix + ".*", dbo);
/*     */         }
/*     */       }
/*     */ 
/*  81 */       dealedSql = replaceSqlStar(sql, starFieldTable);
/*     */     }
/*     */ 
/*  84 */     return dealedSql;
/*     */   }
/*     */ 
/*     */   public static String replaceSqlStar(String sql, Map starFieldTable) throws SecurityException
/*     */   {
/*  89 */     StringBuffer dealedSql = new StringBuffer(sql);
/*     */ 
/*  91 */     Object[] starFields = starFieldTable.keySet().toArray();
/*  92 */     for (int i = 0; i < starFieldTable.size(); i++) {
/*  93 */       String starField = (String)starFields[i];
/*  94 */       String fieldPrefix = SqlParser.parseQueryFieldPrefix(starField);
/*  95 */       fieldPrefix = fieldPrefix.length() > 0 ? fieldPrefix + "." : "";
/*  96 */       IDbModel dbo = (IDbModel)starFieldTable.get(starField);
/*     */ 
/*  98 */       Field[] fields = dbo.getClass().getFields();
/*  99 */       StringBuffer dboFieldsSql = new StringBuffer(" ");
/* 100 */       for (int j = 0; j < fields.length; j++) {
/* 101 */         dboFieldsSql.append(fieldPrefix + fields[j].getName());
/* 102 */         if (j < fields.length - 1) {
/* 103 */           dboFieldsSql.append(", ");
/*     */         }
/*     */       }
/* 106 */       dboFieldsSql.append(" ");
/*     */ 
/* 108 */       int starIndex = dealedSql.toString().indexOf(starField);
/* 109 */       dealedSql.replace(starIndex, starIndex + starField.length(), dboFieldsSql.toString());
/*     */     }
/*     */ 
/* 113 */     return dealedSql.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.SqlQueryCmdHelper
 * JD-Core Version:    0.6.0
 */