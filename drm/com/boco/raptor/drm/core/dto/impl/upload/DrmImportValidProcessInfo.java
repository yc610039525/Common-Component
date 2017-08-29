/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DrmImportValidProcessInfo
/*    */   implements Serializable
/*    */ {
/* 22 */   private int totalSize = 0;
/* 23 */   private int completeSize = 0;
/* 24 */   private String state = "start";
/* 25 */   private long elapsedTime = 0L;
/*    */ 
/*    */   public DrmImportValidProcessInfo() {
/*    */   }
/*    */ 
/*    */   public DrmImportValidProcessInfo(int totalSize, int completeSize, String state, long elapsedTime) {
/* 31 */     this.totalSize = totalSize;
/* 32 */     this.completeSize = completeSize;
/* 33 */     this.state = state;
/* 34 */     this.elapsedTime = elapsedTime;
/*    */   }
/*    */ 
/*    */   public void setTotalSize(int totalSize) {
/* 38 */     this.totalSize = totalSize;
/*    */   }
/*    */ 
/*    */   public void setCompleteSize(int completeSize) {
/* 42 */     this.completeSize = completeSize;
/*    */   }
/*    */ 
/*    */   public void setState(String state) {
/* 46 */     this.state = state;
/*    */   }
/*    */ 
/*    */   public void setElapsedTime(long elapsedTime) {
/* 50 */     this.elapsedTime = elapsedTime;
/*    */   }
/*    */ 
/*    */   public int getTotalSize() {
/* 54 */     return this.totalSize;
/*    */   }
/*    */ 
/*    */   public int getCompleteSize() {
/* 58 */     return this.completeSize;
/*    */   }
/*    */ 
/*    */   public String getState() {
/* 62 */     return this.state;
/*    */   }
/*    */ 
/*    */   public long getElapsedTime() {
/* 66 */     return this.elapsedTime;
/*    */   }
/*    */ 
/*    */   public boolean isInProgress() {
/* 70 */     return ("progress".equals(this.state)) || ("start".equals(this.state));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmImportValidProcessInfo
 * JD-Core Version:    0.6.0
 */