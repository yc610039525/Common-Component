/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.commons.fileupload.disk.DiskFileItem;
/*    */ 
/*    */ public class DrmMonitoredDiskFileItem extends DiskFileItem
/*    */ {
/* 28 */   private DrmMonitoredOutputStream mos = null;
/*    */   private DrmOutputStreamListener listener;
/*    */ 
/*    */   public DrmMonitoredDiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, DrmOutputStreamListener listener)
/*    */   {
/* 33 */     super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
/* 34 */     this.listener = listener;
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream() throws IOException
/*    */   {
/* 39 */     if (this.mos == null)
/*    */     {
/* 41 */       this.mos = new DrmMonitoredOutputStream(super.getOutputStream(), this.listener);
/*    */     }
/* 43 */     return this.mos;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmMonitoredDiskFileItem
 * JD-Core Version:    0.6.0
 */