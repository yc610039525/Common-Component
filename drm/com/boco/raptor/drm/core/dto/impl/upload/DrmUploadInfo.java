/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DrmUploadInfo
/*    */   implements Serializable
/*    */ {
/* 21 */   private long totalSize = 0L;
/* 22 */   private long bytesRead = 0L;
/* 23 */   private long elapsedTime = 0L;
/* 24 */   private String status = "done";
/* 25 */   private int fileIndex = 0;
/*    */ 
/*    */   public DrmUploadInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public DrmUploadInfo(int fileIndex, long totalSize, long bytesRead, long elapsedTime, String status)
/*    */   {
/* 33 */     this.fileIndex = fileIndex;
/* 34 */     this.totalSize = totalSize;
/* 35 */     this.bytesRead = bytesRead;
/* 36 */     this.elapsedTime = elapsedTime;
/* 37 */     this.status = status;
/*    */   }
/*    */ 
/*    */   public String getStatus()
/*    */   {
/* 42 */     return this.status;
/*    */   }
/*    */ 
/*    */   public void setStatus(String status)
/*    */   {
/* 47 */     this.status = status;
/*    */   }
/*    */ 
/*    */   public long getTotalSize()
/*    */   {
/* 52 */     return this.totalSize;
/*    */   }
/*    */ 
/*    */   public void setTotalSize(long totalSize)
/*    */   {
/* 57 */     this.totalSize = totalSize;
/*    */   }
/*    */ 
/*    */   public long getBytesRead()
/*    */   {
/* 62 */     return this.bytesRead;
/*    */   }
/*    */ 
/*    */   public void setBytesRead(long bytesRead)
/*    */   {
/* 67 */     this.bytesRead = bytesRead;
/*    */   }
/*    */ 
/*    */   public long getElapsedTime()
/*    */   {
/* 72 */     return this.elapsedTime;
/*    */   }
/*    */ 
/*    */   public void setElapsedTime(long elapsedTime)
/*    */   {
/* 77 */     this.elapsedTime = elapsedTime;
/*    */   }
/*    */ 
/*    */   public boolean isInProgress()
/*    */   {
/* 82 */     return ("progress".equals(this.status)) || ("start".equals(this.status));
/*    */   }
/*    */ 
/*    */   public int getFileIndex()
/*    */   {
/* 87 */     return this.fileIndex;
/*    */   }
/*    */ 
/*    */   public void setFileIndex(int fileIndex)
/*    */   {
/* 92 */     this.fileIndex = fileIndex;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmUploadInfo
 * JD-Core Version:    0.6.0
 */