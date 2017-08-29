/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.dto.impl.DrmQueryRow;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DboCollection extends AttrObjCollection<GenericDO>
/*     */   implements IQueryCollection, IDrmQueryResultSet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String dsName;
/*     */   private int countValue;
/*     */   private int fetchSize;
/*     */   private int offset;
/*     */   private boolean populate;
/*     */   private boolean entity;
/*     */ 
/*     */   public DboCollection()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DboCollection(String dsName, int offset, int fetchSize)
/*     */   {
/*  42 */     this.dsName = dsName;
/*  43 */     this.fetchSize = fetchSize;
/*  44 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public void addRow(DataObjectMap row) {
/*  48 */     super.getRows().add(row);
/*     */   }
/*     */ 
/*     */   public void addRow(IDrmQueryRow row) {
/*  52 */     Map dbos = row.getDboRow();
/*  53 */     DataObjectMap doMap = new DataObjectMap();
/*  54 */     for (String dbClassId : dbos.keySet()) {
/*  55 */       doMap.put(dbClassId, (GenericDO)dbos.get(dbClassId));
/*     */     }
/*  57 */     super.getRows().add(doMap);
/*     */   }
/*     */ 
/*     */   public void setDsName(String dsName)
/*     */   {
/*  62 */     this.dsName = dsName;
/*     */   }
/*     */ 
/*     */   public void setCountValue(int countValue) {
/*  66 */     this.countValue = countValue;
/*     */   }
/*     */ 
/*     */   public void setFetchSize(int fetchSize) {
/*  70 */     this.fetchSize = fetchSize;
/*     */   }
/*     */ 
/*     */   public void setOffset(int offset) {
/*  74 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public String getDsName() {
/*  78 */     return this.dsName;
/*     */   }
/*     */ 
/*     */   public int getCountValue() {
/*  82 */     return this.countValue;
/*     */   }
/*     */ 
/*     */   public int getFetchSize() {
/*  86 */     return this.fetchSize;
/*     */   }
/*     */ 
/*     */   public int getOffset() {
/*  90 */     return this.offset;
/*     */   }
/*     */   public int getPageSize() {
/*  93 */     int pageSize = 1;
/*  94 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/*  95 */       pageSize = this.countValue / this.fetchSize;
/*  96 */       if (this.countValue % this.fetchSize > 0) {
/*  97 */         pageSize++;
/*     */       }
/*     */     }
/* 100 */     return pageSize;
/*     */   }
/*     */ 
/*     */   public int getStartPageNo() {
/* 104 */     int startPageNo = 1;
/* 105 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/* 106 */       startPageNo = this.offset / this.fetchSize + 1;
/*     */     }
/* 108 */     return startPageNo;
/*     */   }
/*     */ 
/*     */   public GenericDO getQueryDbo(int rowNo, String className) {
/* 112 */     return (GenericDO)super.getAttrField(className, rowNo);
/*     */   }
/*     */ 
/*     */   public <T> T getQueryAttrValue(int rowNo, String className, String attrName) {
/* 116 */     return getQueryDbo(rowNo, className).getAttrValue(attrName);
/*     */   }
/*     */ 
/*     */   public List<IDrmQueryRow> getResultSet() {
/* 120 */     List resultRows = new ArrayList();
/* 121 */     for (int i = 0; i < size(); i++) {
/* 122 */       Map row = (Map)getRows().get(i);
/* 123 */       IDrmQueryRow resultRow = new DrmQueryRow();
/* 124 */       resultRow.setResultRow(row);
/* 125 */       resultRows.add(resultRow);
/*     */     }
/* 127 */     return resultRows;
/*     */   }
/*     */ 
/*     */   public String getQueryAttrString(int rowNo, String className, String attrName) {
/* 131 */     Object value = getQueryAttrValue(rowNo, className, attrName);
/* 132 */     String attrVal = "";
/* 133 */     if (value != null) {
/* 134 */       attrVal = value.toString();
/*     */     }
/* 136 */     return attrVal;
/*     */   }
/*     */ 
/*     */   public int getResultSetSize() {
/* 140 */     return size();
/*     */   }
/*     */ 
/*     */   public boolean isEntity() {
/* 144 */     return this.entity;
/*     */   }
/*     */ 
/*     */   public void setEntity(boolean entity) {
/* 148 */     this.entity = entity;
/*     */   }
/*     */ 
/*     */   public boolean isPopulate() {
/* 152 */     return this.populate;
/*     */   }
/*     */ 
/*     */   public void setPopulate(boolean populate) {
/* 156 */     this.populate = populate;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DboCollection
 * JD-Core Version:    0.6.0
 */