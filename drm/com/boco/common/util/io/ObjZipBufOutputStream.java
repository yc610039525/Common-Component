/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.util.zip.GZIPOutputStream;
/*    */ 
/*    */ public class ObjZipBufOutputStream
/*    */ {
/*    */   private ObjectOutputStream objOutStream;
/* 10 */   private BufArrayOutputStream bufOutStream = new BufArrayOutputStream(null);
/*    */ 
/* 12 */   public ObjZipBufOutputStream() throws IOException { this.objOutStream = new ObjectOutputStream(this.bufOutStream); }
/*    */ 
/*    */   public void writeObject(Object value) throws IOException
/*    */   {
/* 16 */     this.objOutStream.writeObject(value);
/*    */   }
/*    */ 
/*    */   public void write(byte[] bytes) throws IOException {
/* 20 */     this.objOutStream.write(bytes);
/*    */   }
/*    */ 
/*    */   public byte[] getBuf() {
/* 24 */     return this.bufOutStream.getBuf();
/*    */   }
/*    */ 
/*    */   public void close() {
/*    */     try {
/* 29 */       this.objOutStream.close();
/*    */     } catch (IOException ex) {
/*    */     }
/*    */   }
/*    */ 
/*    */   private static class BufArrayOutputStream extends GZIPOutputStream {
/*    */     private BufArrayOutputStream() throws IOException {
/* 36 */       super();
/*    */     }
/*    */ 
/*    */     private byte[] getBuf() {
/* 40 */       return ((ByteArrayOutputStream)this.out).toByteArray();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.ObjZipBufOutputStream
 * JD-Core Version:    0.6.0
 */