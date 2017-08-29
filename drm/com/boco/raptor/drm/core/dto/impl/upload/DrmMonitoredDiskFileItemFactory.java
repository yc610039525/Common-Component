/*    */ package com.boco.raptor.drm.core.dto.impl.upload;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.apache.commons.fileupload.FileItem;
/*    */ import org.apache.commons.fileupload.disk.DiskFileItemFactory;
/*    */ 
/*    */ public class DrmMonitoredDiskFileItemFactory extends DiskFileItemFactory
/*    */ {
/* 28 */   private DrmOutputStreamListener listener = null;
/*    */ 
/*    */   public DrmMonitoredDiskFileItemFactory(DrmOutputStreamListener listener)
/*    */   {
/* 33 */     this.listener = listener;
/*    */   }
/*    */ 
/*    */   public DrmMonitoredDiskFileItemFactory(int sizeThreshold, File repository, DrmOutputStreamListener listener)
/*    */   {
/* 38 */     super(sizeThreshold, repository);
/* 39 */     this.listener = listener;
/*    */   }
/*    */ 
/*    */   public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName)
/*    */   {
/* 44 */     return new DrmMonitoredDiskFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository(), this.listener);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmMonitoredDiskFileItemFactory
 * JD-Core Version:    0.6.0
 */