/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.DataInputStream;
/*    */ 
/*    */ public class BufInputStream extends DataInputStream
/*    */ {
/*    */   public BufInputStream(byte[] buf)
/*    */   {
/* 27 */     super(new BufByteArrayInputStream(buf));
/*    */   }
/*    */ 
/*    */   public BufInputStream(byte[] buf, int offset, int length) {
/* 31 */     super(new BufByteArrayInputStream(buf, offset, length));
/*    */   }
/*    */ 
/*    */   public byte[] getBuf() {
/* 35 */     return ((BufByteArrayInputStream)this.in).getBuf();
/*    */   }
/*    */ 
/*    */   public int getAvailableSize() {
/* 39 */     return ((BufByteArrayInputStream)this.in).available();
/*    */   }
/*    */ 
/*    */   public int skip(int n) {
/* 43 */     return (int)((BufByteArrayInputStream)this.in).skip(n);
/*    */   }
/*    */ 
/*    */   private static class BufByteArrayInputStream extends ByteArrayInputStream {
/*    */     public BufByteArrayInputStream(byte[] buf) {
/* 48 */       super();
/*    */     }
/*    */ 
/*    */     public BufByteArrayInputStream(byte[] buf, int offset, int length) {
/* 52 */       super(offset, length);
/*    */     }
/*    */ 
/*    */     public byte[] getBuf() {
/* 56 */       return this.buf;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.BufInputStream
 * JD-Core Version:    0.6.0
 */