/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class DbRow extends HashMap
/*     */ {
/*     */   public boolean getBoolean(String fieldName)
/*     */   {
/*  28 */     return ((Boolean)get(fieldName)).booleanValue();
/*     */   }
/*     */ 
/*     */   public void putBoolean(String fieldName, boolean value) {
/*  32 */     put(fieldName, new Boolean(value));
/*     */   }
/*     */ 
/*     */   public int getInt(String fieldName) {
/*  36 */     return ((Integer)get(fieldName)).intValue();
/*     */   }
/*     */ 
/*     */   public void putInt(String fieldName, int value) {
/*  40 */     put(fieldName, new Integer(value));
/*     */   }
/*     */ 
/*     */   public short getShort(String fieldName) {
/*  44 */     return ((Short)get(fieldName)).shortValue();
/*     */   }
/*     */ 
/*     */   public void putShort(String fieldName, short value) {
/*  48 */     put(fieldName, new Short(value));
/*     */   }
/*     */ 
/*     */   public long getLong(String fieldName) {
/*  52 */     return ((Long)get(fieldName)).longValue();
/*     */   }
/*     */ 
/*     */   public void putLong(String fieldName, long value) {
/*  56 */     put(fieldName, new Long(value));
/*     */   }
/*     */ 
/*     */   public byte getByte(String fieldName) {
/*  60 */     return ((Byte)get(fieldName)).byteValue();
/*     */   }
/*     */ 
/*     */   public void putByte(String fieldName, byte value) {
/*  64 */     put(fieldName, new Byte(value));
/*     */   }
/*     */ 
/*     */   public float getFloat(String fieldName) {
/*  68 */     return ((Float)get(fieldName)).floatValue();
/*     */   }
/*     */ 
/*     */   public void putFloat(String fieldName, float value) {
/*  72 */     put(fieldName, new Float(value));
/*     */   }
/*     */ 
/*     */   public String getString(String fieldName) {
/*  76 */     return (String)get(fieldName);
/*     */   }
/*     */ 
/*     */   public Date getDate(String fieldName) {
/*  80 */     return (Date)get(fieldName);
/*     */   }
/*     */ 
/*     */   public Time getTime(String fieldName) {
/*  84 */     return (Time)get(fieldName);
/*     */   }
/*     */ 
/*     */   public Timestamp getTimestamp(String fieldName) {
/*  88 */     return (Timestamp)get(fieldName);
/*     */   }
/*     */ 
/*     */   public DbBlob getBlob(String fieldName) {
/*  92 */     return (DbBlob)get(fieldName);
/*     */   }
/*     */ 
/*     */   public IDbModel getDbo(String dboClassName) {
/*  96 */     return (IDbModel)get(dboClassName);
/*     */   }
/*     */ 
/*     */   public void putDbo(String dboClassName, IDbModel dbo) {
/* 100 */     put(dboClassName, dbo);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbRow
 * JD-Core Version:    0.6.0
 */