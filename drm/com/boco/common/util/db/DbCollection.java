/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class DbCollection extends ArrayList
/*     */ {
/*     */   private int countValue;
/*     */   private int fetchSize;
/*     */   private int offset;
/*     */   private String dsName;
/*     */ 
/*     */   public DbCollection()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DbCollection(String dsName, int offset, int fetchSize)
/*     */   {
/*  34 */     this.offset = offset;
/*  35 */     this.fetchSize = fetchSize;
/*  36 */     this.dsName = dsName;
/*     */   }
/*     */ 
/*     */   public int getCountValue() {
/*  40 */     return this.countValue;
/*     */   }
/*     */ 
/*     */   public int getFetchSize() {
/*  44 */     return this.fetchSize;
/*     */   }
/*     */ 
/*     */   public int getOffset() {
/*  48 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public String getDsName() {
/*  52 */     return this.dsName;
/*     */   }
/*     */ 
/*     */   public void setCountValue(int countValue) {
/*  56 */     this.countValue = countValue;
/*     */   }
/*     */ 
/*     */   protected void setFetchSize(int fetchSize) {
/*  60 */     this.fetchSize = fetchSize;
/*     */   }
/*     */ 
/*     */   protected void setOffset(int offset) {
/*  64 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public void setDsName(String dsName) {
/*  68 */     this.dsName = dsName;
/*     */   }
/*     */ 
/*     */   public int getRowSize() {
/*  72 */     return size();
/*     */   }
/*     */ 
/*     */   public int getPageSize() {
/*  76 */     int pageSize = 1;
/*  77 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/*  78 */       pageSize = this.countValue / this.fetchSize;
/*  79 */       if (this.countValue % this.fetchSize > 0) {
/*  80 */         pageSize++;
/*     */       }
/*     */     }
/*  83 */     return pageSize;
/*     */   }
/*     */ 
/*     */   public int getStartPageNo() {
/*  87 */     int startPageNo = 1;
/*  88 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/*  89 */       startPageNo = this.offset / this.fetchSize + 1;
/*     */     }
/*  91 */     return startPageNo;
/*     */   }
/*     */ 
/*     */   public DbRow getQueryRow(int rowIndex) {
/*  95 */     return (DbRow)get(rowIndex);
/*     */   }
/*     */ 
/*     */   public DbRow removeQueryRow(int rowIndex) {
/*  99 */     return (DbRow)remove(rowIndex);
/*     */   }
/*     */ 
/*     */   public IDbModel getQueryDbo(String dboClassName, int rowIndex) throws Exception {
/* 103 */     return getQueryRow(rowIndex).getDbo(dboClassName);
/*     */   }
/*     */ 
/*     */   public Object getQueryDboField(String dboClassName, int rowIndex, String dboFieldName) throws Exception
/*     */   {
/* 108 */     IDbModel dbo = getQueryDbo(dboClassName, rowIndex);
/* 109 */     Field field = dbo.getClass().getField(dboFieldName);
/* 110 */     Object value = field.get(dbo);
/* 111 */     return value;
/*     */   }
/*     */ 
/*     */   public String getQueryDboFieldS(String dboClassName, int rowIndex, String dboFieldName) throws Exception
/*     */   {
/* 116 */     Object value = getQueryDboField(dboClassName, rowIndex, dboFieldName);
/* 117 */     return value == null ? "" : value.toString();
/*     */   }
/*     */ 
/*     */   public Object getQuerySqlField(int rowIndex, int columnIndex) throws Exception {
/* 121 */     return getQueryRow(rowIndex).get(Integer.toString(columnIndex));
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbCollection
 * JD-Core Version:    0.6.0
 */