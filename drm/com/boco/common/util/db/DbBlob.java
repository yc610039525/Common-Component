/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.io.BufOutputStream;
/*     */ import com.boco.common.util.io.ObjZipBufInputStream;
/*     */ import com.boco.common.util.io.ObjZipBufOutputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DbBlob
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private byte[] blobBytes;
/*  38 */   private static final byte[] ZIP_FLAG = { 17, 34, 51, 68 };
/*     */ 
/*     */   public DbBlob() {
/*     */   }
/*     */ 
/*     */   public DbBlob(byte[] blobBytes) {
/*  44 */     this.blobBytes = blobBytes;
/*     */   }
/*     */ 
/*     */   public byte[] getBlobBytes() {
/*  48 */     return this.blobBytes;
/*     */   }
/*     */ 
/*     */   public void setBlobBytes(byte[] blobBytes) {
/*  52 */     this.blobBytes = blobBytes;
/*     */   }
/*     */ 
/*     */   public void zipBytes() {
/*  56 */     if (this.blobBytes == null) return;
/*     */     try
/*     */     {
/*  59 */       ObjZipBufOutputStream out = null;
/*  60 */       out = new ObjZipBufOutputStream();
/*  61 */       out.write(this.blobBytes);
/*  62 */       out.close();
/*  63 */       byte[] zipBytes = out.getBuf();
/*  64 */       this.blobBytes = new byte[zipBytes.length + ZIP_FLAG.length];
/*  65 */       System.arraycopy(zipBytes, 0, this.blobBytes, 0, zipBytes.length);
/*  66 */       System.arraycopy(ZIP_FLAG, 0, this.blobBytes, zipBytes.length, ZIP_FLAG.length);
/*     */     } catch (Exception ex) {
/*  68 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unzipBytes() {
/*  73 */     if (this.blobBytes == null) return;
/*     */ 
/*  75 */     BufOutputStream buf = new BufOutputStream();
/*     */     try {
/*  77 */       byte[] unwrapBytes = new byte[this.blobBytes.length - ZIP_FLAG.length];
/*  78 */       System.arraycopy(this.blobBytes, 0, unwrapBytes, 0, this.blobBytes.length - ZIP_FLAG.length);
/*  79 */       ObjZipBufInputStream in = new ObjZipBufInputStream(unwrapBytes);
/*  80 */       int readLen = 0;
/*     */       while (true) {
/*  82 */         byte[] readBytes = new byte[10240];
/*  83 */         readLen = in.read(readBytes);
/*  84 */         if (readLen < 0) break;
/*  85 */         buf.write(readBytes, 0, readLen);
/*     */       }
/*     */ 
/*  90 */       in.close();
/*  91 */       buf.close();
/*  92 */       this.blobBytes = buf.getBuf();
/*     */     } catch (Exception ex) {
/*  94 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isZipBytes() {
/*  99 */     boolean isZip = false;
/* 100 */     if ((this.blobBytes != null) && (this.blobBytes.length >= ZIP_FLAG.length)) {
/* 101 */       isZip = true;
/* 102 */       for (int i = 1; i <= ZIP_FLAG.length; i++) {
/* 103 */         if (ZIP_FLAG[(ZIP_FLAG.length - i)] != this.blobBytes[(this.blobBytes.length - i)]) {
/* 104 */           isZip = false;
/* 105 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 109 */     return isZip;
/*     */   }
/*     */ 
/*     */   public void printBlobBytes() {
/* 113 */     String strBlob = "";
/* 114 */     for (int i = 0; i < this.blobBytes.length; i++) {
/* 115 */       strBlob = strBlob + this.blobBytes[i] + ", ";
/*     */     }
/* 117 */     System.out.println(strBlob);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 122 */     BufOutputStream b = new BufOutputStream();
/*     */     try {
/* 124 */       FileInputStream f = new FileInputStream("c:/JDBC.doc");
/*     */       while (true) {
/* 126 */         byte[] buf = new byte[10240];
/* 127 */         int readlen = f.read(buf);
/* 128 */         if (readlen < 0) break;
/* 129 */         b.write(buf, 0, readlen);
/*     */       }
/*     */ 
/* 134 */       b.close();
/* 135 */       f.close();
/*     */     } catch (Exception ex) {
/* 137 */       ex.printStackTrace();
/*     */     }
/* 139 */     System.out.println("old len=" + b.getBuf().length);
/* 140 */     DbBlob blob = new DbBlob(b.getBuf());
/* 141 */     LogHome.getLog().info("begin zip");
/* 142 */     blob.zipBytes();
/* 143 */     LogHome.getLog().info("end zip");
/*     */     try {
/* 145 */       FileOutputStream out = new FileOutputStream("d:/jdbc.gzip");
/* 146 */       out.write(blob.getBlobBytes());
/* 147 */       out.close();
/* 148 */       if (blob.isZipBytes()) {
/* 149 */         LogHome.getLog().info("begin unzip");
/* 150 */         blob.unzipBytes();
/* 151 */         LogHome.getLog().info("end unzip");
/* 152 */         out = new FileOutputStream("d:/jdbc.doc");
/* 153 */         out.write(blob.getBlobBytes());
/* 154 */         out.close();
/*     */       }
/*     */     } catch (Exception ex1) {
/* 157 */       ex1.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbBlob
 * JD-Core Version:    0.6.0
 */