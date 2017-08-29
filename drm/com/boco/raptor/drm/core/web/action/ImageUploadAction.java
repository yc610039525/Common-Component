/*    */ package com.boco.raptor.drm.core.web.action;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import org.apache.commons.fileupload.DiskFileUpload;
/*    */ import org.apache.commons.fileupload.FileItem;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ImageUploadAction extends HttpServlet
/*    */ {
/*    */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*    */     throws ServletException, IOException
/*    */   {
/*    */     try
/*    */     {
/* 22 */       String loadpath = request.getSession().getServletContext().getRealPath("/") + request.getParameter("path");
/* 23 */       String fileName = request.getParameter("name");
/* 24 */       DiskFileUpload fu = new DiskFileUpload();
/* 25 */       fu.setSizeMax(10485760L);
/* 26 */       fu.setSizeThreshold(4096);
/*    */ 
/* 29 */       List fileItems = fu.parseRequest(request);
/* 30 */       Iterator iter = fileItems.iterator();
/* 31 */       while (iter.hasNext()) {
/* 32 */         FileItem item = (FileItem)iter.next();
/* 33 */         if (!item.isFormField()) {
/* 34 */           String name = item.getName();
/* 35 */           name = name.substring(name.lastIndexOf("\\") + 1);
/* 36 */           long size = item.getSize();
/* 37 */           if (((name == null) || (name.equals(""))) && (size == 0L)) {
/*    */             continue;
/*    */           }
/* 40 */           File dirFile = new File(loadpath);
/* 41 */           if (dirFile.listFiles() == null) dirFile.mkdirs();
/* 42 */           File fNew = new File(loadpath, fileName);
/* 43 */           item.write(fNew);
/*    */         }
/*    */       }
/*    */     } catch (Exception ex) {
/* 47 */       LogHome.getLog().error("", ex);
/* 48 */       request.setAttribute("errors", ex.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
/*    */   {
/* 54 */     doGet(request, response);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ImageUploadAction
 * JD-Core Version:    0.6.0
 */