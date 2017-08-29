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
/*    */ public class ModelUploadAction extends HttpServlet
/*    */ {
/*    */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*    */     throws ServletException, IOException
/*    */   {
/*    */     try
/*    */     {
/* 24 */       request.getSession().setAttribute(SessionNames.MODEL_FILE_BYTES, null);
/* 25 */       DiskFileUpload diskFileUpload = new DiskFileUpload();
/* 26 */       diskFileUpload.setSizeMax(-1L);
/* 27 */       diskFileUpload.setSizeThreshold(4096);
/* 28 */       List fileItems = diskFileUpload.parseRequest(request);
/* 29 */       Iterator i = fileItems.iterator();
/* 30 */       FileItem fileItem = (FileItem)i.next();
/* 31 */       byte[] byteBussModel = fileItem.get();
/* 32 */       request.getSession().setAttribute(SessionNames.MODEL_FILE_BYTES, byteBussModel);
/*    */     } catch (Exception ex) {
/* 34 */       LogHome.getLog().error("", ex);
/* 35 */       request.setAttribute("errors", ex.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 40 */     doGet(request, response);
/*    */   }
/*    */ 
/*    */   public static class SessionNames
/*    */   {
/* 19 */     public static String MODEL_FILE_BYTES = "MODEL_FILE_BYTES";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ModelUploadAction
 * JD-Core Version:    0.6.0
 */