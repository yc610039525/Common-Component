/*     */ package com.boco.transnms.server.web;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.client.model.base.BoCmdFactory;
/*     */ import com.boco.transnms.client.model.base.GenericBoCmd;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.xmlrpc.XmlRpcServer;
/*     */ 
/*     */ public class XrpcBoProxyServlet extends HttpServlet
/*     */ {
/*     */   private static long alarmEventCount;
/*  27 */   private static long alarmStartTime = System.currentTimeMillis();
/*     */   private static long alarmHandleTimeSum;
/*     */   private static long systemTimeSum;
/*  30 */   private XmlRpcServer xmlrpc = new XmlRpcServer();
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/*  43 */     this.xmlrpc.addHandler("$default", this);
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest req, HttpServletResponse rep)
/*     */     throws ServletException, IOException
/*     */   {
/*  59 */     byte[] result = this.xmlrpc.execute(req.getInputStream());
/*  60 */     rep.setContentType("text/xml");
/*  61 */     rep.setContentLength(result.length);
/*  62 */     OutputStream out = rep.getOutputStream();
/*  63 */     out.write(result);
/*  64 */     out.flush();
/*  65 */     out.close();
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest req, HttpServletResponse rep)
/*     */     throws ServletException, IOException
/*     */   {
/*  81 */     doGet(req, rep);
/*     */   }
/*  92 */   public byte[] execBoCommand(byte[] paras) { long startTime = System.currentTimeMillis();
/*     */ 
/*  94 */     GenericBoCmd cmd = null;
/*     */     byte[] out;
/*     */     try { cmd = GenericBoCmd.fromZipBytes(paras);
/*  97 */       String info = "XRPC调用命令：" + cmd + ", threadId=" + ThreadHelper.getCurrentThreadId() + ", 开始";
/*  98 */       LogHome.getLog().info(info);
/*  99 */       BoCmdFactory.getInstance().execBoCmd(cmd);
/*     */     } catch (Exception ex) {
/* 101 */       LogHome.getLog().error("", ex);
/*     */     } finally {
/* 103 */       out = doCmdResult(cmd);
/*     */     }
/*     */ 
/* 106 */     if (cmd != null) {
/* 107 */       String info = "XRPC调用命令：" + cmd + ", threadId=" + ThreadHelper.getCurrentThreadId() + ", 时间花费=" + (System.currentTimeMillis() - startTime) + ", 完成";
/*     */ 
/* 109 */       LogHome.getLog().info(info);
/*     */     } else {
/* 111 */       LogHome.getLog().error("命令参数解析错误: XRPC命令参数有客户端对象或JAR文件不匹配!");
/*     */     }
/* 113 */     return out;
/*     */   }
/*     */ 
/*     */   private byte[] doCmdResult(GenericBoCmd cmd)
/*     */   {
/* 124 */     byte[] out = new byte[0];
/*     */     try {
/* 126 */       if (cmd != null)
/* 127 */         out = cmd.getResultBytes();
/*     */     }
/*     */     catch (Exception ex) {
/* 130 */       LogHome.getLog().error("", ex);
/* 131 */       cmd.setException(new UserException(ex));
/*     */     }
/* 133 */     return out;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.web.XrpcBoProxyServlet
 * JD-Core Version:    0.6.0
 */