/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractDbModel
/*     */   implements IDbModel
/*     */ {
/*     */   private String tableName;
/*     */ 
/*     */   public AbstractDbModel()
/*     */   {
/*  39 */     String className = getClass().getName();
/*  40 */     int index = className.lastIndexOf(".");
/*  41 */     this.tableName = className.substring(index + 1, className.length());
/*     */   }
/*     */ 
/*     */   public AbstractDbModel(String _tableName)
/*     */   {
/*  49 */     this.tableName = _tableName;
/*     */   }
/*     */ 
/*     */   public String getTableName()
/*     */   {
/*  57 */     return this.tableName;
/*     */   }
/*     */ 
/*     */   public void setTableName(String _tableName)
/*     */   {
/*  65 */     this.tableName = _tableName;
/*     */   }
/*     */ 
/*     */   public void copyDbo(AbstractDbModel dbo)
/*     */     throws Exception
/*     */   {
/*  74 */     Field[] fields = dbo.getClass().getFields();
/*  75 */     Map fieldMap = new HashMap();
/*  76 */     for (int i = 0; i < fields.length; i++) {
/*  77 */       fieldMap.put(fields[i].getName(), fields[i]);
/*     */     }
/*     */ 
/*  80 */     fields = getClass().getFields();
/*  81 */     for (int i = 0; i < fields.length; i++)
/*  82 */       if (fieldMap.containsKey(fields[i].getName())) {
/*  83 */         Field field = (Field)fieldMap.get(fields[i].getName());
/*  84 */         if (field.getType() == fields[i].getType())
/*  85 */           fields[i].set(this, field.get(dbo));
/*     */       }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  96 */     String className = getClass().getName();
/*  97 */     int index = className.lastIndexOf(".");
/*  98 */     className = className.substring(index + 1, className.length());
/*     */ 
/* 100 */     StringBuffer str = new StringBuffer(className + "[");
/* 101 */     Field[] fields = getClass().getFields();
/* 102 */     for (int i = 0; i < fields.length; i++) {
/* 103 */       Field field = fields[i];
/*     */       try {
/* 105 */         Object value = field.get(this);
/* 106 */         if (value != null)
/* 107 */           str.append(field.getName() + "=" + value + ", ");
/*     */       }
/*     */       catch (Exception ex) {
/* 110 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/* 113 */     str.append("]");
/*     */ 
/* 115 */     return str.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.AbstractDbModel
 * JD-Core Version:    0.6.0
 */