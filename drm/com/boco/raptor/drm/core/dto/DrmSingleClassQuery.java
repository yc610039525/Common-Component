/*     */ package com.boco.raptor.drm.core.dto;
/*     */ 
/*     */ import com.boco.common.util.db.DbType;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DrmSingleClassQuery
/*     */   implements Serializable
/*     */ {
/*     */   public static final String QUERY_ATTR_COND_TEMPLATE_POSTFIX = "_EXP";
/*     */   private String bmClassId;
/*     */   private String sqlCond;
/*     */   private String sqlCondTemplate;
/*     */   private String sqlFilter;
/*     */   private List<String> queryAttrIds;
/*     */   private List<DrmQueryAttrCond> queryCondExps;
/*     */   private BMClassMeta classMeta;
/*     */ 
/*     */   public String getBmClassId()
/*     */   {
/*  44 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public List<String> getQueryAttrIds() {
/*  48 */     return this.queryAttrIds;
/*     */   }
/*     */ 
/*     */   public List<DrmQueryAttrCond> getQueryCondExps() {
/*  52 */     return this.queryCondExps;
/*     */   }
/*     */ 
/*     */   public String getSqlCondTemplate() {
/*  56 */     return this.sqlCondTemplate;
/*     */   }
/*     */ 
/*     */   public BMClassMeta getClassMeta() {
/*  60 */     return this.classMeta;
/*     */   }
/*     */ 
/*     */   public String getSqlCond() {
/*  64 */     return this.sqlCond;
/*     */   }
/*     */ 
/*     */   public String getSqlFilter() {
/*  68 */     return this.sqlFilter;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/*  72 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void addQueryAttrId(String attrId) {
/*  76 */     if (this.queryAttrIds == null) {
/*  77 */       this.queryAttrIds = new ArrayList();
/*     */     }
/*  79 */     this.queryAttrIds.add(attrId);
/*     */   }
/*     */ 
/*     */   public void addQueryAttrIds(List<String> attrIds) {
/*  83 */     for (String attrId : attrIds)
/*  84 */       addQueryAttrId(attrId);
/*     */   }
/*     */ 
/*     */   public void setQueryAttrIds(List<String> queryAttrIds)
/*     */   {
/*  89 */     this.queryAttrIds = queryAttrIds;
/*     */   }
/*     */ 
/*     */   public void addQueryCondExps(DrmQueryAttrCond queryCondExp) {
/*  93 */     if (this.queryCondExps == null) {
/*  94 */       this.queryCondExps = new ArrayList();
/*     */     }
/*  96 */     this.queryCondExps.add(queryCondExp);
/*     */   }
/*     */ 
/*     */   public void setQueryCondExps(List<DrmQueryAttrCond> queryCondExps) {
/* 100 */     this.queryCondExps = queryCondExps;
/*     */   }
/*     */ 
/*     */   public void setSqlCondTemplate(String sqlCondTemplate) {
/* 104 */     this.sqlCondTemplate = sqlCondTemplate;
/*     */   }
/*     */ 
/*     */   public void setClassMeta(BMClassMeta classMeta) {
/* 108 */     this.classMeta = classMeta;
/* 109 */     if (classMeta != null)
/* 110 */       this.bmClassId = classMeta.getBmClassId();
/*     */   }
/*     */ 
/*     */   public void setSqlCond(String sqlCond)
/*     */   {
/* 115 */     this.sqlCond = sqlCond;
/*     */   }
/*     */ 
/*     */   public void setSqlFilter(String sqlFilter) {
/* 119 */     this.sqlFilter = sqlFilter;
/*     */   }
/*     */ 
/*     */   public String getQuerySql(DbType dbType) {
/* 123 */     String queryFields = getQueryFields();
/* 124 */     String _sqlCond = getQuerySqlCond(dbType);
/* 125 */     String sql = "select " + queryFields + " from " + this.classMeta.getDbTableName();
/* 126 */     if ((_sqlCond != null) && (_sqlCond.trim().length() > 0)) {
/* 127 */       if (_sqlCond.toLowerCase().trim().indexOf("group ") != 0)
/* 128 */         sql = sql + " where " + _sqlCond;
/*     */       else {
/* 130 */         sql = sql + " " + _sqlCond;
/*     */       }
/*     */     }
/* 133 */     return sql;
/*     */   }
/*     */ 
/*     */   public String getQuerySqlCond(DbType dbType) {
/* 137 */     String _sqlCond = "";
/* 138 */     if ((this.classMeta.getBmDivideCond() != null) && (this.classMeta.getBmDivideCond().trim().length() > 0)) {
/* 139 */       _sqlCond = this.classMeta.getBmDivideSqlCond();
/*     */     }
/*     */ 
/* 142 */     if ((this.sqlCond != null) && (this.sqlCond.trim().length() > 0)) {
/* 143 */       _sqlCond = _sqlCond.length() > 0 ? _sqlCond + " and " + this.sqlCond : this.sqlCond;
/*     */     }
/* 145 */     else if (this.queryCondExps != null) {
/* 146 */       if ((this.sqlCondTemplate != null) && (this.sqlCondTemplate.trim().length() > 0)) {
/* 147 */         String _sqlCondTemplate = this.sqlCondTemplate;
/* 148 */         for (int i = 0; i < this.queryCondExps.size(); i++) {
/* 149 */           DrmQueryAttrCond queryAttrCond = (DrmQueryAttrCond)this.queryCondExps.get(i);
/* 150 */           _sqlCondTemplate = getTemplateQueryExpr(dbType, queryAttrCond, _sqlCondTemplate, i);
/*     */         }
/* 152 */         if (_sqlCond.length() > 0)
/* 153 */           _sqlCond = _sqlCond + " and " + _sqlCondTemplate;
/*     */         else
/* 155 */           _sqlCond = _sqlCond + _sqlCondTemplate;
/*     */       }
/*     */       else {
/* 158 */         for (DrmQueryAttrCond queryAttrCond : this.queryCondExps) {
/* 159 */           if (queryAttrCond.getValue() != null) {
/* 160 */             BMAttrMeta attrMeta = this.classMeta.getAttrMeta(queryAttrCond.getAttrId());
/* 161 */             if (attrMeta == null) {
/* 162 */               LogHome.getLog().error("未配置业务模型的属性, bmClassId=" + this.classMeta.getBmClassId() + ", attrId=" + queryAttrCond.getAttrId());
/* 163 */               continue;
/*     */             }
/* 165 */             Class attrClassType = attrMeta.getAttrClassType();
/* 166 */             String expr = queryAttrCond.getDbQueryExpr(dbType, attrClassType);
/* 167 */             if (_sqlCond.length() > 0) {
/* 168 */               _sqlCond = _sqlCond + " and ";
/*     */             }
/* 170 */             _sqlCond = _sqlCond + expr;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 176 */     if ((_sqlCond != null) && (_sqlCond.trim().length() > 0)) {
/* 177 */       if ((this.sqlFilter != null) && (this.sqlFilter.trim().length() > 0)) {
/* 178 */         if (_sqlCond.toLowerCase().indexOf("group ") != -1) {
/* 179 */           String groupSql = _sqlCond.substring(_sqlCond.lastIndexOf("group "));
/* 180 */           _sqlCond = _sqlCond.substring(0, _sqlCond.indexOf(groupSql));
/* 181 */           _sqlCond = _sqlCond + " and " + this.sqlFilter + " " + groupSql;
/*     */         } else {
/* 183 */           _sqlCond = _sqlCond + " and " + this.sqlFilter;
/*     */         }
/*     */       }
/*     */     }
/* 187 */     else if ((this.sqlFilter != null) && (this.sqlFilter.trim().length() > 0)) {
/* 188 */       _sqlCond = this.sqlFilter;
/*     */     }
/*     */ 
/* 191 */     return _sqlCond;
/*     */   }
/*     */ 
/*     */   private String getQueryFields() {
/* 195 */     String queryFields = "";
/*     */ 
/* 197 */     if ((this.queryAttrIds != null) && (this.queryAttrIds.size() > 0))
/*     */     {
/* 199 */       for (int i = 0; i < this.queryAttrIds.size(); i++) {
/* 200 */         queryFields = queryFields + (String)this.queryAttrIds.get(i);
/* 201 */         if (i < this.queryAttrIds.size() - 1)
/* 202 */           queryFields = queryFields + ", ";
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 207 */       Map allAttrMetas = this.classMeta.getAllAttrMetas();
/* 208 */       String[] attrIds = new String[allAttrMetas.size()];
/* 209 */       allAttrMetas.keySet().toArray(attrIds);
/* 210 */       for (int i = 0; i < attrIds.length; i++) {
/* 211 */         queryFields = queryFields + attrIds[i];
/* 212 */         if (i < attrIds.length - 1) {
/* 213 */           queryFields = queryFields + ", ";
/*     */         }
/*     */       }
/*     */     }
/* 217 */     return queryFields;
/*     */   }
/*     */ 
/*     */   private String getTemplateQueryExpr(DbType dbType, DrmQueryAttrCond queryAttrCond, String sqlTemplate, int attrCondNum) {
/* 221 */     String expr = "1=1";
/* 222 */     if (queryAttrCond.getValue() != null) {
/* 223 */       Class attrClassType = this.classMeta.getAttrMeta(queryAttrCond.getAttrId()).getAttrClassType();
/* 224 */       expr = queryAttrCond.getDbQueryExpr(dbType, attrClassType);
/*     */     }
/* 226 */     return sqlTemplate.replace("$" + queryAttrCond.getAttrId() + "_EXP" + attrCondNum, expr);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.DrmSingleClassQuery
 * JD-Core Version:    0.6.0
 */