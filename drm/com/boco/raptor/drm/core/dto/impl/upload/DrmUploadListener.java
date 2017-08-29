/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ 
/*    */ public class DrmUploadListener
/*    */   implements DrmOutputStreamListener
/*    */ {
/*    */   private HttpServletRequest request;
/* 25 */   private long delay = 0L;
/* 26 */   private long startTime = 0L;
/* 27 */   private int totalToRead = 0;
/* 28 */   private int totalBytesRead = 0;
/* 29 */   private int totalFiles = -1;
/*    */ 
/*    */   public DrmUploadListener(HttpServletRequest request, long debugDelay)
/*    */   {
/* 33 */     this.request = request;
/* 34 */     this.delay = debugDelay;
/* 35 */     this.totalToRead = request.getContentLength();
/* 36 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public void start()
/*    */   {
/* 41 */     this.totalFiles += 1;
/* 42 */     updateUploadInfo("start");
/*    */   }
/*    */ 
/*    */   public void bytesRead(int bytesRead)
/*    */   {
/* 47 */     this.totalBytesRead += bytesRead;
/* 48 */     updateUploadInfo("progress");
/*    */     try
/*    */     {
/* 52 */       Thread.sleep(this.delay);
/*    */     }
/*    */     catch (InterruptedException e)
/*    */     {
/* 56 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 62 */     updateUploadInfo("error");
/*    */   }
/*    */ 
/*    */   public void done()
/*    */   {
/* 67 */     updateUploadInfo("done");
/*    */   }
/*    */ 
/*    */   private long getDelta()
/*    */   {
/* 72 */     return (System.currentTimeMillis() - this.startTime) / 1000L;
/*    */   }
/*    */ 
/*    */   private void updateUploadInfo(String status)
/*    */   {
/* 77 */     long delta = (System.currentTimeMillis() - this.startTime) / 1000L;
/* 78 */     this.request.getSession().setAttribute("uploadInfo", new DrmUploadInfo(this.totalFiles, this.totalToRead, this.totalBytesRead, delta, status));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmUploadListener
 * JD-Core Version:    0.6.0
 */