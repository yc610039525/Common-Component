/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.io.BufOutputStream;
/*     */ import com.boco.common.util.lang.CharsetHelper;
/*     */ import java.io.InputStream;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import oracle.sql.BLOB;
/*     */ 
/*     */ public class ResultsetWrapper
/*     */ {
/*     */   private final ResultSet rs;
/*     */   private final DbContext dbContext;
/*     */ 
/*     */   public ResultsetWrapper(ResultSet rs, DbContext dbContext)
/*     */   {
/*  35 */     this.dbContext = dbContext;
/*  36 */     this.rs = rs;
/*     */   }
/*     */ 
/*     */   public String getString(int columnIndex) throws Exception {
/*  40 */     String s = this.rs.getString(columnIndex);
/*  41 */     if (s == null)
/*  42 */       s = "";
/*     */     else {
/*  44 */       s = CharsetHelper.decode(this.dbContext.getDbCharset(), s);
/*     */     }
/*  46 */     return s;
/*     */   }
/*     */ 
/*     */   public DbBlob getBlob(int columnIndex) throws Exception {
/*  50 */     byte[] blobBytes = null;
/*  51 */     DbType dbType = this.dbContext.getDbType();
/*     */ 
/*  53 */     if (dbType == DbType.DB_TYPE_ORACLE) {
/*  54 */       BLOB blob = (BLOB)this.rs.getBlob(columnIndex);
/*  55 */       if (blob == null) {
/*  56 */         return null;
/*     */       }
/*  58 */       blobBytes = blob.getBytes(1L, (int)blob.length());
/*     */     }
/*  60 */     else if ((dbType == DbType.DB_TYPE_SYBASE) || (dbType == DbType.DB_TYPE_HSQL) || (dbType == DbType.DB_TYPE_INFORMIX) || (dbType == DbType.DB_TYPE_MYSQL))
/*     */     {
/*  62 */       InputStream in = this.rs.getBinaryStream(columnIndex);
/*  63 */       BufOutputStream outBuf = new BufOutputStream();
/*  64 */       while ((in != null) && (in.available() > 0)) {
/*  65 */         byte[] buf = new byte[1024];
/*  66 */         int len = in.read(buf);
/*  67 */         outBuf.write(buf, 0, len);
/*     */       }
/*  69 */       blobBytes = outBuf.getBuf();
/*  70 */       outBuf.close();
/*     */     }
/*     */ 
/*  73 */     DbBlob blob = new DbBlob(blobBytes);
/*  74 */     if (blob.isZipBytes()) {
/*  75 */       blob.unzipBytes();
/*     */     }
/*  77 */     return blob;
/*     */   }
/*     */ 
/*     */   public byte getByte(int columnIndex) throws SQLException {
/*  81 */     byte fieldVal = 0;
/*  82 */     if (this.rs.getObject(columnIndex) != null) {
/*  83 */       fieldVal = this.rs.getByte(columnIndex);
/*     */     }
/*  85 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public double getDouble(int columnIndex) throws SQLException {
/*  89 */     double fieldVal = 0.0D;
/*  90 */     if (this.rs.getObject(columnIndex) != null) {
/*  91 */       fieldVal = this.rs.getDouble(columnIndex);
/*     */     }
/*  93 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public float getFloat(int columnIndex) throws SQLException {
/*  97 */     float fieldVal = 0.0F;
/*  98 */     if (this.rs.getObject(columnIndex) != null) {
/*  99 */       fieldVal = this.rs.getFloat(columnIndex);
/*     */     }
/* 101 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public int getInt(int columnIndex) throws SQLException {
/* 105 */     int fieldVal = 0;
/* 106 */     if (this.rs.getObject(columnIndex) != null) {
/* 107 */       fieldVal = this.rs.getInt(columnIndex);
/*     */     }
/* 109 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public long getLong(int columnIndex) throws SQLException {
/* 113 */     long fieldVal = 0L;
/* 114 */     if (this.rs.getObject(columnIndex) != null) {
/* 115 */       fieldVal = this.rs.getLong(columnIndex);
/*     */     }
/* 117 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public short getShort(int columnIndex) throws SQLException {
/* 121 */     short fieldVal = 0;
/* 122 */     if (this.rs.getObject(columnIndex) != null) {
/* 123 */       fieldVal = this.rs.getShort(columnIndex);
/*     */     }
/* 125 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(int columnIndex) throws SQLException {
/* 129 */     boolean fieldVal = false;
/* 130 */     if (this.rs.getObject(columnIndex) != null) {
/* 131 */       fieldVal = this.rs.getBoolean(columnIndex);
/*     */     }
/* 133 */     return fieldVal;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(int columnIndex) throws SQLException {
/* 137 */     return this.rs.getBytes(columnIndex);
/*     */   }
/*     */ 
/*     */   public Object getObject(int columnIndex) throws SQLException
/*     */   {
/* 142 */     return this.rs.getObject(columnIndex);
/*     */   }
/*     */ 
/*     */   public Date getDate(int columnIndex) throws SQLException {
/* 146 */     return this.rs.getDate(columnIndex);
/*     */   }
/*     */ 
/*     */   public Time getTime(int columnIndex) throws SQLException {
/* 150 */     return this.rs.getTime(columnIndex);
/*     */   }
/*     */ 
/*     */   public Timestamp getTimestamp(int columnIndex) throws SQLException {
/* 154 */     return this.rs.getTimestamp(columnIndex);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.ResultsetWrapper
 * JD-Core Version:    0.6.0
 */