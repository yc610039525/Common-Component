/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ 
/*    */ public class DrmExcelImportValidListener
/*    */ {
/*    */   private HttpServletRequest request;
/* 24 */   private long delay = 0L;
/* 25 */   private long startTime = 0L;
/* 26 */   private int total = 0;
/* 27 */   private int complete = 0;
/*    */ 
/*    */   public DrmExcelImportValidListener(HttpServletRequest request, long debugDelay, int total) {
/* 30 */     this.request = request;
/* 31 */     this.delay = debugDelay;
/* 32 */     this.total = total;
/* 33 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public DrmExcelImportValidListener(HttpServletRequest request, long debugDelay) {
/* 37 */     this.request = request;
/* 38 */     this.delay = debugDelay;
/* 39 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public void setTotal(int total) {
/* 43 */     this.total = total;
/*    */   }
/*    */ 
/*    */   public void start() {
/* 47 */     updateUploadInfo("start");
/*    */   }
/*    */ 
/*    */   public void update(int count) {
/* 51 */     this.complete += count;
/* 52 */     updateUploadInfo("progress");
/*    */     try {
/* 54 */       Thread.sleep(this.delay);
/*    */     } catch (InterruptedException e) {
/* 56 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message) {
/* 61 */     updateUploadInfo("error");
/*    */   }
/*    */ 
/*    */   public void done() {
/* 65 */     updateUploadInfo("done");
/*    */   }
/*    */ 
/*    */   private void updateUploadInfo(String state)
/*    */   {
/* 74 */     long delta = (System.currentTimeMillis() - this.startTime) / 1000L;
/* 75 */     this.request.getSession().setAttribute("importValidProcessInfo", new DrmImportValidProcessInfo(this.total, this.complete, state, delta));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportValidListener
 * JD-Core Version:    0.6.0
 */