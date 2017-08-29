/*    */ package com.boco.transnms.server.web;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Properties;
/*    */ import javax.servlet.ServletConfig;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ 
/*    */ public class Log4jInit extends HttpServlet
/*    */ {
/*    */   public void init(ServletConfig config)
/*    */     throws ServletException
/*    */   {
/* 19 */     String prefix = config.getServletContext().getRealPath("/");
/* 20 */     String file = config.getInitParameter("log4j");
/* 21 */     String filePath = prefix + file;
/* 22 */     Properties props = new Properties();
/*    */     try {
/* 24 */       FileInputStream istream = new FileInputStream(filePath);
/* 25 */       props.load(istream);
/* 26 */       istream.close();
/* 27 */       String logFile = prefix + props.getProperty("log4j.appender.file.File");
/*    */ 
/* 30 */       props.setProperty("log4j.appender.file.File", logFile);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.web.Log4jInit
 * JD-Core Version:    0.6.0
 */