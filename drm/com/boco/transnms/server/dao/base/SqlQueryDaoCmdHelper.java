/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.db.IDbModel;
/*     */ import com.boco.common.util.db.SqlParser;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SqlQueryDaoCmdHelper
/*     */ {
/*     */   public static String prepareSqlStar(String sql, GenericDO[] dboTemplates)
/*     */   {
/*  33 */     Map starFieldTable = new HashMap();
/*  34 */     String dealedSql = sql;
/*  35 */     if (sql.indexOf(" * ") > 0) {
/*  36 */       starFieldTable.put(" * ", dboTemplates[0]);
/*  37 */       dealedSql = replaceSqlStar(sql, starFieldTable);
/*  38 */     } else if (sql.indexOf(".*") > 0) {
/*  39 */       String[] queryTables = SqlParser.parseSqlQueryTables(sql);
/*  40 */       for (int i = 0; i < queryTables.length; i++) {
/*  41 */         String[] spiltTable = queryTables[i].trim().split(" ");
/*  42 */         if (spiltTable.length < 2)
/*     */         {
/*     */           continue;
/*     */         }
/*  46 */         String tablePrefix = spiltTable[1].trim();
/*  47 */         if (sql.indexOf(tablePrefix + ".*") < 0)
/*     */         {
/*     */           continue;
/*     */         }
/*  51 */         String tableName = spiltTable[0].trim();
/*  52 */         IDbModel dbo = null;
/*  53 */         for (int j = 0; j < dboTemplates.length; j++) {
/*  54 */           if (dboTemplates[j].getTableName().equals(tableName)) {
/*  55 */             dbo = dboTemplates[j];
/*     */           }
/*     */         }
/*  58 */         if (dbo != null) {
/*  59 */           starFieldTable.put(tablePrefix + ".*", dbo);
/*     */         }
/*     */       }
/*  62 */       dealedSql = replaceSqlStar(sql, starFieldTable);
/*     */     }
/*  64 */     return dealedSql;
/*     */   }
/*     */ 
/*     */   public static String replaceSqlStar(String sql, Map starFieldTable) throws SecurityException {
/*  68 */     StringBuffer dealedSql = new StringBuffer(sql);
/*     */ 
/*  70 */     Object[] starFields = starFieldTable.keySet().toArray();
/*  71 */     for (int i = 0; i < starFieldTable.size(); i++) {
/*  72 */       String starField = (String)starFields[i];
/*  73 */       String fieldPrefix = SqlParser.parseQueryFieldPrefix(starField);
/*  74 */       fieldPrefix = fieldPrefix.length() > 0 ? fieldPrefix + "." : "";
/*  75 */       GenericDO dbo = (GenericDO)starFieldTable.get(starField);
/*     */ 
/*  77 */       String[] attrNames = dbo.getAllAttrNames();
/*  78 */       StringBuffer dboFieldsSql = new StringBuffer(" ");
/*  79 */       for (int j = 0; j < attrNames.length; j++) {
/*  80 */         dboFieldsSql.append(fieldPrefix + attrNames[j]);
/*  81 */         if (j < attrNames.length - 1) {
/*  82 */           dboFieldsSql.append(", ");
/*     */         }
/*     */       }
/*  85 */       dboFieldsSql.append(" ");
/*  86 */       while (dealedSql.toString().indexOf(starField) > 0) {
/*  87 */         int starIndex = dealedSql.toString().indexOf(starField);
/*  88 */         dealedSql.replace(starIndex, starIndex + starField.length(), dboFieldsSql.toString());
/*     */       }
/*     */     }
/*     */ 
/*  92 */     return dealedSql.toString();
/*     */   }
/*     */ 
/*     */   public static String appendDeleteFlag(String sql)
/*     */   {
/* 116 */     String esql = sql;
/*     */ 
/* 147 */     return esql;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.SqlQueryDaoCmdHelper
 * JD-Core Version:    0.6.0
 */