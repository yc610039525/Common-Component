/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import com.boco.common.util.db.DbBlob;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DboBlob
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String CLASS_NAME = "DboBlob";
/*    */   private DbBlob dbBlob;
/*    */ 
/*    */   public DboBlob()
/*    */   {
/* 31 */     this.dbBlob = new DbBlob();
/*    */   }
/*    */ 
/*    */   public DboBlob(byte[] blobBytes) {
/* 35 */     this.dbBlob = new DbBlob(blobBytes);
/*    */   }
/*    */ 
/*    */   public byte[] getBlobBytes() {
/* 39 */     return this.dbBlob.getBlobBytes();
/*    */   }
/*    */ 
/*    */   public void setBlobBytes(byte[] blobBytes) {
/* 43 */     this.dbBlob.setBlobBytes(blobBytes);
/*    */   }
/*    */ 
/*    */   public void zipBytes() {
/* 47 */     this.dbBlob.zipBytes();
/*    */   }
/*    */ 
/*    */   public void unzipBytes() {
/* 51 */     this.dbBlob.unzipBytes();
/*    */   }
/*    */ 
/*    */   public boolean isZipBytes() {
/* 55 */     return this.dbBlob.isZipBytes();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DboBlob
 * JD-Core Version:    0.6.0
 */