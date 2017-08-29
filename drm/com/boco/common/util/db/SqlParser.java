/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlParser
/*     */ {
/*     */   public static int getSqlKeywordIndex(String sql, String keyword)
/*     */   {
/*  31 */     sql = sql.toUpperCase();
/*  32 */     int index = sql.indexOf(keyword.toUpperCase());
/*  33 */     if (index >= 0) {
/*  34 */       if (index + keyword.length() >= sql.length()) {
/*  35 */         index = -1;
/*  36 */       } else if ((sql.charAt(index + keyword.length()) != ' ') || ((index > 0) && (sql.charAt(index - 1) != ' ')))
/*     */       {
/*  41 */         int subindex = getSqlKeywordIndex(sql.substring(index + keyword.length(), sql.length()), keyword);
/*     */ 
/*  43 */         if (subindex != -1)
/*  44 */           index += subindex + keyword.length();
/*     */         else {
/*  46 */           index = subindex;
/*     */         }
/*     */       }
/*     */     }
/*  50 */     return index;
/*     */   }
/*     */ 
/*     */   public static String parseSqlQuery(String sql) {
/*  54 */     int condIndex = sql.length();
/*     */ 
/*  56 */     int whereIndex = getSqlKeywordIndex(sql, "where");
/*  57 */     int groupIndex = getSqlKeywordIndex(sql, "group");
/*  58 */     int orderIndex = getSqlKeywordIndex(sql, "order");
/*     */ 
/*  60 */     if (whereIndex > 0)
/*  61 */       condIndex = whereIndex;
/*  62 */     else if (groupIndex > 0)
/*  63 */       condIndex = groupIndex;
/*  64 */     else if (orderIndex > 0) {
/*  65 */       condIndex = orderIndex;
/*     */     }
/*     */ 
/*  68 */     return sql.substring(0, condIndex);
/*     */   }
/*     */ 
/*     */   public static String[] parseQueryFields(String sql) throws Exception {
/*  72 */     String sqlQuery = parseSqlQuery(sql);
/*     */ 
/*  74 */     int fromIndex = getSqlKeywordIndex(sqlQuery, "from");
/*  75 */     int distinctIndex = getSqlKeywordIndex(sqlQuery, "distinct");
/*  76 */     int distinctLength = "distinct".length();
/*     */ 
/*  78 */     String queryFields = "";
/*  79 */     if (distinctIndex > 0)
/*  80 */       queryFields = sqlQuery.substring(distinctIndex + distinctLength, fromIndex);
/*     */     else {
/*  82 */       queryFields = sqlQuery.substring("select".length(), fromIndex);
/*     */     }
/*     */ 
/*  85 */     String[] queryFieldList = queryFields.split(",");
/*  86 */     if (queryFieldList.length == 0) {
/*  87 */       throw new Exception("SQL='" + sqlQuery + "', 无有效查询字段");
/*     */     }
/*     */ 
/*  90 */     for (int i = 0; i < queryFieldList.length; i++) {
/*  91 */       queryFieldList[i] = queryFieldList[i].trim();
/*     */     }
/*     */ 
/*  94 */     return queryFieldList;
/*     */   }
/*     */ 
/*     */   public static String parseQueryFieldPrefix(String queryFieldName) {
/*  98 */     String queryFieldPrefix = "";
/*  99 */     queryFieldName = queryFieldName.trim();
/* 100 */     int prefixIndex = queryFieldName.indexOf(".");
/* 101 */     if (prefixIndex > 0) {
/* 102 */       queryFieldPrefix = queryFieldName.substring(0, prefixIndex);
/*     */     }
/* 104 */     return queryFieldPrefix;
/*     */   }
/*     */ 
/*     */   public static String parseDBFieldName(String queryFieldName)
/*     */   {
/* 109 */     String dbFieldName = queryFieldName.trim();
/* 110 */     int prefixIndex = queryFieldName.trim().indexOf(".");
/* 111 */     if (prefixIndex > 0) {
/* 112 */       dbFieldName = dbFieldName.substring(prefixIndex + 1, dbFieldName.length());
/*     */     }
/* 114 */     return dbFieldName;
/*     */   }
/*     */ 
/*     */   public static boolean isIfxSupportComplexCount(String dbProductVersion) {
/* 118 */     boolean isIfxSupportComplexCount = false;
/* 119 */     String smajorVersion = dbProductVersion.split("\\.")[0];
/* 120 */     String sminorVersion = dbProductVersion.split("\\.")[1];
/* 121 */     int majorVersion = Integer.parseInt(smajorVersion);
/* 122 */     int minorVersion = Integer.parseInt(sminorVersion);
/* 123 */     if ((majorVersion > 11) || ((majorVersion == 11) && (minorVersion >= 50))) {
/* 124 */       isIfxSupportComplexCount = true;
/*     */     }
/* 126 */     return isIfxSupportComplexCount;
/*     */   }
/*     */ 
/*     */   public static boolean isSupportComplexCount(DbType dbType, String dbProductVersion)
/*     */   {
/* 131 */     boolean isSupportComplexCount = (dbType == DbType.DB_TYPE_ORACLE) || (dbType == DbType.DB_TYPE_SYBASE) || ((dbType == DbType.DB_TYPE_INFORMIX) && (dbProductVersion != null) && (isIfxSupportComplexCount(dbProductVersion)));
/*     */ 
/* 133 */     return isSupportComplexCount;
/*     */   }
/*     */ 
/*     */   public static String parseCountSqlFromQuery(DbType dbType, String dbProductVersion, String sql) throws Exception {
/* 137 */     String countSql = null;
/* 138 */     if (isSupportComplexCount(dbType, dbProductVersion))
/* 139 */       countSql = "select count(*) from (" + sql + ") XX";
/* 140 */     else if (dbType == DbType.DB_TYPE_INFORMIX) {
/* 141 */       countSql = getInformixCountSQL(sql);
/*     */     }
/* 143 */     return countSql;
/*     */   }
/*     */ 
/*     */   private static String getInformixCountSQL(String sql) throws Exception {
/*     */     try {
/* 148 */       if (sql == null) {
/* 149 */         return null;
/*     */       }
/* 151 */       String sqlLow = sql.toLowerCase();
/* 152 */       if ((sqlLow.toLowerCase().indexOf(" distinct ") >= 0) || (sqlLow.indexOf(" union ") >= 0)) {
/* 153 */         return null;
/*     */       }
/* 155 */       int fromIndex = getSqlKeywordIndex(sql, "from");
/* 156 */       return "select count(*) " + sql.substring(fromIndex);
/*     */     } catch (Exception ex) {
/* 158 */       LogHome.getLog().error("", ex);
/* 159 */     }return null;
/*     */   }
/*     */ 
/*     */   public static String[] parseQueryTables(String sqlQuery) {
/* 163 */     int fromIndex = getSqlKeywordIndex(sqlQuery, "from");
/* 164 */     String queryTableSql = sqlQuery.substring(fromIndex + "from".length(), sqlQuery.length());
/* 165 */     List queryTableList = new ArrayList();
/* 166 */     String[] queryTableTokens = queryTableSql.split(",");
/* 167 */     for (int i = 0; i < queryTableTokens.length; i++) {
/* 168 */       parseQueryTableName(queryTableList, queryTableTokens[i]);
/*     */     }
/* 170 */     String[] queryTables = new String[queryTableList.size()];
/* 171 */     queryTableList.toArray(queryTables);
/* 172 */     return queryTables;
/*     */   }
/*     */ 
/*     */   public static String[] parseSqlQueryTables(String sql) {
/* 176 */     int unionIndex = 0;
/* 177 */     List allQueryTableList = new ArrayList();
/* 178 */     String restSql = sql;
/*     */     do {
/* 180 */       unionIndex = getSqlKeywordIndex(restSql, "union");
/* 181 */       String partSql = "";
/* 182 */       if (unionIndex > 0) {
/* 183 */         partSql = restSql.substring(0, unionIndex);
/* 184 */         int length = restSql.length();
/* 185 */         String rSql = restSql.substring(unionIndex + "union".length(), length);
/* 186 */         restSql = rSql;
/*     */       } else {
/* 188 */         partSql = restSql;
/*     */       }
/* 190 */       String sqlQuery = parseSqlQuery(partSql);
/* 191 */       String[] queryTables = parseQueryTables(sqlQuery);
/* 192 */       for (int i = 0; i < queryTables.length; i++)
/* 193 */         allQueryTableList.add(queryTables[i]);
/*     */     }
/* 195 */     while (unionIndex > 0);
/* 196 */     String[] queryTables = new String[allQueryTableList.size()];
/* 197 */     allQueryTableList.toArray(queryTables);
/* 198 */     return queryTables;
/*     */   }
/*     */ 
/*     */   private static void parseQueryTableName(List queryTableList, String sqlTable) {
/* 202 */     if (getSqlKeywordIndex(sqlTable, "JOIN") > 0) {
/* 203 */       String[] words = sqlTable.trim().split(" ");
/* 204 */       String table = "";
/* 205 */       boolean isJoinCond = false;
/* 206 */       for (int i = 0; i < words.length; i++) {
/* 207 */         String word = words[i].trim();
/* 208 */         if (word.length() == 0)
/*     */           continue;
/* 210 */         if ((word.equalsIgnoreCase("JOIN")) || (word.equalsIgnoreCase("LEFT")) || (word.equalsIgnoreCase("RIGHT")) || (word.equalsIgnoreCase("OUTER")) || (word.equalsIgnoreCase("INNER")))
/*     */         {
/* 213 */           if ((!isJoinCond) && (table.trim().length() > 0)) {
/* 214 */             queryTableList.add(table.trim());
/*     */           }
/* 216 */           table = "";
/* 217 */           isJoinCond = false;
/* 218 */         } else if (word.equalsIgnoreCase("ON")) {
/* 219 */           if ((!isJoinCond) && (table.trim().length() > 0)) {
/* 220 */             queryTableList.add(table.trim());
/*     */           }
/* 222 */           table = "";
/* 223 */           isJoinCond = true;
/*     */         }
/* 225 */         else if (!isJoinCond) {
/* 226 */           table = table + word;
/* 227 */           table = table + " ";
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 232 */       queryTableList.add(sqlTable);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.SqlParser
 * JD-Core Version:    0.6.0
 */