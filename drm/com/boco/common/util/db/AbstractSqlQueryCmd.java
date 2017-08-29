/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractSqlQueryCmd extends AbstractSqlCmd
/*     */ {
/*     */   protected AbstractSqlQueryCmd(DbContext dbContext)
/*     */   {
/*  20 */     super(dbContext);
/*     */   }
/*     */ 
/*     */   public int getCalculateValue(String sql)
/*     */     throws Exception
/*     */   {
/*  30 */     return getCalculateValue(sql, true);
/*     */   }
/*     */ 
/*     */   public int[] getCalculateValues(String sql)
/*     */     throws Exception
/*     */   {
/*  40 */     return getCalculateValues(sql, true);
/*     */   }
/*     */ 
/*     */   protected int[] getCalculateValues(String sql, boolean isClearCmd)
/*     */     throws Exception
/*     */   {
/*  51 */     Statement st = null;
/*  52 */     ResultSet rs = null;
/*  53 */     ArrayList countList = new ArrayList();
/*     */     try
/*     */     {
/*  56 */       st = getDbConn().createStatement();
/*  57 */       LogHome.getLog().info("SQL=" + sql);
/*  58 */       rs = st.executeQuery(SqlHelper.prepareSql(getDbContext().getDbCharset(), sql));
/*  59 */       while (rs.next())
/*  60 */         countList.add(Integer.valueOf(rs.getInt(1)));
/*     */     }
/*     */     catch (Exception ex) {
/*  63 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/*  64 */       throw ex;
/*     */     } finally {
/*  66 */       DbHelper.closeResultSet(rs);
/*  67 */       DbHelper.closeStatement(st);
/*  68 */       if (isClearCmd) {
/*  69 */         clearCmd();
/*     */       }
/*     */     }
/*     */ 
/*  73 */     int[] counts = new int[countList.size()];
/*  74 */     for (int i = 0; i < countList.size(); i++) {
/*  75 */       counts[i] = ((Integer)countList.get(i)).intValue();
/*     */     }
/*  77 */     return counts;
/*     */   }
/*     */ 
/*     */   protected int getCalculateValue(String sql, boolean isClearCmd)
/*     */     throws Exception
/*     */   {
/*  88 */     Statement st = null;
/*  89 */     ResultSet rs = null;
/*  90 */     int count = 0;
/*     */     try
/*     */     {
/*  93 */       st = getDbConn().createStatement();
/*  94 */       LogHome.getLog().info("SQL=" + sql);
/*  95 */       rs = st.executeQuery(SqlHelper.prepareSql(getDbContext().getDbCharset(), sql));
/*  96 */       if (rs.next())
/*  97 */         count = rs.getInt(1);
/*     */     }
/*     */     catch (Exception ex) {
/* 100 */       LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 101 */       throw ex;
/*     */     } finally {
/* 103 */       DbHelper.closeResultSet(rs);
/* 104 */       DbHelper.closeStatement(st);
/* 105 */       if (isClearCmd) {
/* 106 */         clearCmd();
/*     */       }
/*     */     }
/*     */ 
/* 110 */     return count;
/*     */   }
/*     */ 
/*     */   protected int getCountBeforQuery(String sql)
/*     */     throws Exception
/*     */   {
/* 120 */     int orderIndex = SqlParser.getSqlKeywordIndex(sql, "order");
/* 121 */     if (orderIndex >= 0) {
/* 122 */       sql = sql.substring(0, orderIndex);
/*     */     }
/*     */ 
/* 125 */     String countSql = SqlParser.parseCountSqlFromQuery(super.getDbType(), super.getDbProductVersion(), sql);
/* 126 */     int count = 0;
/* 127 */     if (countSql != null) {
/*     */       try {
/* 129 */         count = getCalculateValue(countSql, false);
/*     */       } catch (Exception ex) {
/* 131 */         LogHome.getLog().error("COUNT SQL 执行错误", ex);
/* 132 */         countSql = null;
/*     */       }
/*     */     }
/* 135 */     if (countSql == null) {
/* 136 */       Statement st = null;
/* 137 */       ResultSet rs = null;
/*     */       try {
/* 139 */         st = getDbConn().createStatement(1004, 1007);
/* 140 */         LogHome.getLog().info("SQL=" + sql);
/* 141 */         rs = st.executeQuery(SqlHelper.prepareSql(getDbContext().getDbCharset(), sql));
/* 142 */         if (rs.last())
/* 143 */           count = rs.getRow();
/*     */       }
/*     */       catch (Exception ex) {
/* 146 */         LogHome.getLog().error("SQL='" + sql + "' 执行错误!");
/* 147 */         throw ex;
/*     */       } finally {
/* 149 */         DbHelper.closeResultSet(rs);
/* 150 */         DbHelper.closeStatement(st);
/*     */       }
/*     */     }
/*     */ 
/* 154 */     return count;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.AbstractSqlQueryCmd
 * JD-Core Version:    0.6.0
 */