/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ 
/*    */ public class DrmExcelImportListener
/*    */ {
/*    */   private HttpServletRequest request;
/* 24 */   private long delay = 0L;
/* 25 */   private long startTime = 0L;
/* 26 */   private int total = 0;
/* 27 */   private int complete = 0;
/*    */ 
/*    */   public DrmExcelImportListener(HttpServletRequest request, long debugDelay, int total) {
/* 30 */     this.request = request;
/* 31 */     this.delay = debugDelay;
/* 32 */     this.total = total;
/* 33 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public void start() {
/* 37 */     updateUploadInfo("start");
/*    */   }
/*    */ 
/*    */   public void update(int count) {
/* 41 */     this.complete += count;
/* 42 */     updateUploadInfo("progress");
/*    */     try {
/* 44 */       Thread.sleep(this.delay);
/*    */     } catch (InterruptedException e) {
/* 46 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message) {
/* 51 */     updateUploadInfo("error");
/*    */   }
/*    */ 
/*    */   public void done() {
/* 55 */     updateUploadInfo("done");
/*    */   }
/*    */ 
/*    */   private void updateUploadInfo(String state)
/*    */   {
/* 64 */     long delta = (System.currentTimeMillis() - this.startTime) / 1000L;
/* 65 */     this.request.getSession().setAttribute("importProcessInfo", new DrmImportProcessInfo(this.total, this.complete, state, delta));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportListener
 * JD-Core Version:    0.6.0
 */