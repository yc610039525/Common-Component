/*    */ package com.boco.raptor.drm.core.web.action;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import org.apache.commons.fileupload.DiskFileUpload;
/*    */ import org.apache.commons.fileupload.FileItem;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ModelCompareUploadAction extends HttpServlet
/*    */ {
/*    */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*    */     throws ServletException, IOException
/*    */   {
/*    */     try
/*    */     {
/* 37 */       request.getSession().setAttribute(SessionNames.BMMODEL_FILE_BYTES, null);
/* 38 */       request.getSession().setAttribute(SessionNames.COMPARED_FILE_BYTES, null);
/* 39 */       DiskFileUpload diskFileUpload = new DiskFileUpload();
/* 40 */       diskFileUpload.setSizeMax(-1L);
/* 41 */       diskFileUpload.setSizeThreshold(4096);
/* 42 */       List fileItems = diskFileUpload.parseRequest(request);
/* 43 */       Iterator i = fileItems.iterator();
/* 44 */       boolean comparedFile = false;
/* 45 */       while (i.hasNext()) {
/* 46 */         FileItem fileItem = (FileItem)i.next();
/* 47 */         byte[] byteBussModel = fileItem.get();
/* 48 */         if (comparedFile) {
/* 49 */           request.getSession().setAttribute(SessionNames.COMPARED_FILE_BYTES, byteBussModel);
/*    */         } else {
/* 51 */           request.getSession().setAttribute(SessionNames.BMMODEL_FILE_BYTES, byteBussModel);
/* 52 */           comparedFile = true;
/*    */         }
/*    */       }
/*    */     } catch (Exception ex) {
/* 56 */       LogHome.getLog().error("", ex);
/* 57 */       request.setAttribute("errors", ex.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 62 */     doGet(request, response);
/*    */   }
/*    */ 
/*    */   public static class SessionNames
/*    */   {
/* 31 */     public static String BMMODEL_FILE_BYTES = "BNMODEL_FILE_BYTES";
/* 32 */     public static String COMPARED_FILE_BYTES = "COMPARED_FILE_BYTES";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ModelCompareUploadAction
 * JD-Core Version:    0.6.0
 */