/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class DrmMonitoredOutputStream extends OutputStream
/*    */ {
/*    */   private OutputStream target;
/*    */   private DrmOutputStreamListener listener;
/*    */ 
/*    */   public DrmMonitoredOutputStream(OutputStream target, DrmOutputStreamListener listener)
/*    */   {
/* 30 */     this.target = target;
/* 31 */     this.listener = listener;
/* 32 */     this.listener.start();
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len) throws IOException
/*    */   {
/* 37 */     this.target.write(b, off, len);
/* 38 */     this.listener.bytesRead(len - off);
/*    */   }
/*    */ 
/*    */   public void write(byte[] b) throws IOException
/*    */   {
/* 43 */     this.target.write(b);
/* 44 */     this.listener.bytesRead(b.length);
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException
/*    */   {
/* 49 */     this.target.write(b);
/* 50 */     this.listener.bytesRead(1);
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 55 */     this.target.close();
/* 56 */     this.listener.done();
/*    */   }
/*    */ 
/*    */   public void flush() throws IOException
/*    */   {
/* 61 */     this.target.flush();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmMonitoredOutputStream
 * JD-Core Version:    0.6.0
 */