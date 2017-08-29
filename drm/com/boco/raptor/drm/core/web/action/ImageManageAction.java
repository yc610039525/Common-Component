/*    */ package com.boco.raptor.drm.core.web.action;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*    */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ImageManageAction
/*    */ {
/*    */   public List<IDrmDataObject> getImagesByDir(HttpServletRequest request, String dirPath)
/*    */     throws UserException
/*    */   {
/*    */     try
/*    */     {
/* 21 */       File filesDir = new File(request.getSession().getServletContext().getRealPath("/") + dirPath);
/* 22 */       List imageList = new ArrayList();
/* 23 */       if (filesDir.listFiles() == null) return imageList;
/* 24 */       File[] list = filesDir.listFiles();
/* 25 */       for (int i = 0; i < list.length; i++) {
/* 26 */         if (list[i].isFile()) {
/* 27 */           String fileName = list[i].getName();
/* 28 */           fileName = fileName.toLowerCase();
/* 29 */           if ((fileName.indexOf(".gif") <= 0) && (fileName.indexOf(".jpg") <= 0) && (fileName.indexOf(".png") <= 0) && (fileName.indexOf(".bmp") <= 0))
/*    */             continue;
/* 31 */           IDrmDataObject ido = DrmEntityFactory.getInstance().createDataObject();
/* 32 */           ido.setAttrValue("name", list[i].getName());
/* 33 */           String path = request.getContextPath() + dirPath + "/" + list[i].getName();
/* 34 */           ido.setAttrValue("path", path);
/* 35 */           imageList.add(ido);
/*    */         }
/*    */       }
/*    */ 
/* 39 */       return imageList;
/*    */     } catch (Exception ex) {
/* 41 */       LogHome.getLog().error("", ex);
/* 42 */     }throw new UserException(ex.getMessage());
/*    */   }
/*    */ 
/*    */   public void deleteImage(HttpServletRequest request, String filepath) throws IOException
/*    */   {
/* 47 */     File f = new File(request.getSession().getServletContext().getRealPath("/") + filepath);
/* 48 */     if ((f.exists()) && (f.isFile())) {
/* 49 */       LogHome.getLog().info(request.getSession().getServletContext().getRealPath("/") + filepath);
/* 50 */       f.delete();
/*    */     } else {
/* 52 */       LogHome.getLog().info("文件找不到");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ImageManageAction
 * JD-Core Version:    0.6.0
 */