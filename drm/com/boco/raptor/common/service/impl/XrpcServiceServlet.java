/*    */ package com.boco.raptor.common.service.impl;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.lang.ThreadHelper;
/*    */ import com.boco.raptor.common.service.IServiceActionContext;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.xmlrpc.XmlRpcServer;
/*    */ 
/*    */ public class XrpcServiceServlet extends HttpServlet
/*    */ {
/* 35 */   private XmlRpcServer xmlrpc = new XmlRpcServer();
/*    */ 
/*    */   public void init()
/*    */     throws ServletException
/*    */   {
/* 40 */     this.xmlrpc.addHandler("DRM", this);
/*    */   }
/*    */ 
/*    */   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 44 */     byte[] result = this.xmlrpc.execute(request.getInputStream());
/* 45 */     response.setContentType("text/xml");
/* 46 */     response.setContentLength(result.length);
/* 47 */     OutputStream out = response.getOutputStream();
/* 48 */     out.write(result);
/* 49 */     out.flush();
/* 50 */     out.close();
/*    */   }
/*    */ 
/*    */   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 54 */     doGet(req, resp);
/*    */   }
/*    */ 
/*    */   public byte[] invoke(byte[] invokeBytes) throws IOException {
/* 58 */     ServiceInvokeEntity invokeEntity = null;
/* 59 */     IServiceActionContext actionContext = null;
/* 60 */     long startTime = System.currentTimeMillis();
/* 61 */     String threadId = ThreadHelper.getCurrentThreadId();
/*    */     try {
/* 63 */       invokeEntity = ServiceInvokeEntity.fromBytes(invokeBytes);
/* 64 */       actionContext = invokeEntity.getActionContext();
/* 65 */       if (actionContext != null) {
/* 66 */         LogHome.getLog().info("actionId=" + actionContext.getActionId() + ", userId=" + actionContext.getUserId() + ", threadId=" + threadId + ", 开始调用服务 !");
/*    */       }
/*    */ 
/* 70 */       invokeEntity.invoke();
/* 71 */       invokeEntity.clearInvokeParas();
/*    */     } catch (Exception ex) {
/* 73 */       invokeEntity = new ServiceInvokeEntity();
/* 74 */       invokeEntity.setException(ex);
/*    */     }
/* 76 */     if (actionContext != null) {
/* 77 */       LogHome.getLog().info("actionId=" + actionContext.getActionId() + ", userId=" + actionContext.getUserId() + ", threadId=" + threadId + ", time=" + (System.currentTimeMillis() - startTime) + ", 结束服务调用 !");
/*    */     }
/*    */ 
/* 82 */     return invokeEntity.toBytes();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.impl.XrpcServiceServlet
 * JD-Core Version:    0.6.0
 */