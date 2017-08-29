/*     */ package com.boco.raptor.drm.core.dto;
/*     */ 
/*     */ import com.boco.common.util.db.DbType;
/*     */ import com.boco.common.util.db.SqlHelper;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Date;
/*     */ import java.sql.Timestamp;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DrmQueryAttrCond
/*     */   implements Serializable
/*     */ {
/*     */   private String bmClassId;
/*     */   private String attrId;
/*  31 */   private String relation = "=";
/*     */   private String value;
/*     */   private String prefix;
/*     */ 
/*     */   public DrmQueryAttrCond()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DrmQueryAttrCond(String attrId, String relation, String value)
/*     */   {
/*  39 */     this.attrId = attrId;
/*  40 */     this.relation = relation;
/*  41 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public String getAttrId() {
/*  45 */     return this.attrId;
/*     */   }
/*     */ 
/*     */   public String getRelation() {
/*  49 */     return this.relation;
/*     */   }
/*     */ 
/*     */   private String getDbValue(DbType dbType, Class attrClassType) {
/*  53 */     String dbValue = this.value;
/*  54 */     if (dbValue.trim().toUpperCase().equals("NULL"))
/*  55 */       return dbValue;
/*     */     try
/*     */     {
/*  58 */       if (Timestamp.class == attrClassType)
/*  59 */         dbValue = SqlHelper.getTimestamp(dbType, new Timestamp(Long.parseLong(dbValue)));
/*  60 */       else if (Date.class == attrClassType)
/*  61 */         dbValue = SqlHelper.getDate(dbType, new Date(Long.parseLong(dbValue)));
/*  62 */       else if ("like".equalsIgnoreCase(this.relation.trim())) {
/*  63 */         if (this.value.indexOf(",") == -1) {
/*  64 */           dbValue = "'%" + this.value + "%'";
/*     */         } else {
/*  66 */           dbValue = "";
/*  67 */           String[] strArr = this.value.split(",");
/*  68 */           if (strArr != null) {
/*  69 */             for (int i = 0; i < strArr.length; i++) {
/*  70 */               String strValue = strArr[i];
/*  71 */               if (strArr[i].lastIndexOf("'") != -1) {
/*  72 */                 strValue = strArr[i].substring(strArr[i].indexOf("'") + 1, strArr[i].lastIndexOf("'"));
/*     */               }
/*  74 */               dbValue = dbValue + " '%" + strValue + "%' or " + getAttrId() + " like ";
/*     */             }
/*  76 */             dbValue = dbValue.substring(0, dbValue.lastIndexOf("or " + getAttrId() + " like"));
/*     */           }
/*     */         }
/*  79 */       } else if ("in".equals(this.relation.trim()))
/*  80 */         dbValue = "(" + dbValue + ")";
/*  81 */       else if (String.class == attrClassType)
/*  82 */         dbValue = "'" + dbValue + "'";
/*     */     }
/*     */     catch (Exception ex) {
/*  85 */       LogHome.getLog().error("", ex);
/*     */     }
/*  87 */     return dbValue;
/*     */   }
/*     */ 
/*     */   public String getDbQueryExpr(DbType dbType, Class attrClassType)
/*     */   {
/*  92 */     String dbQueryExpr = "";
/*  93 */     String _prefix = this.prefix + ".";
/*  94 */     if (this.value != null) {
/*  95 */       dbQueryExpr = _prefix + getAttrId() + " " + getRelation() + " " + getDbValue(dbType, attrClassType);
/*     */     }
/*  97 */     return dbQueryExpr;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/* 101 */     return this.value;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 105 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public String getPrefix() {
/* 109 */     return this.prefix;
/*     */   }
/*     */ 
/*     */   public void setAttrId(String attrId) {
/* 113 */     this.attrId = attrId;
/*     */   }
/*     */ 
/*     */   public void setRelation(String relation) {
/* 117 */     this.relation = relation;
/*     */   }
/*     */ 
/*     */   public void setValue(String value) {
/* 121 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 125 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setPrefix(String prefix) {
/* 129 */     this.prefix = prefix;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.DrmQueryAttrCond
 * JD-Core Version:    0.6.0
 */