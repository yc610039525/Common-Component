/*     */ package com.boco.transnms.server.web;
/*     */ 
/*     */ import com.boco.common.util.db.TransactionFactory;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.NetHelper;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.client.model.base.BoCmdFactory;
/*     */ import com.boco.transnms.client.model.base.CmdStatInfo;
/*     */ import com.boco.transnms.client.model.base.ExportBoManager;
/*     */ import com.boco.transnms.client.model.base.GenericBoCmd;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.xmlrpc.XmlRpcServer;
/*     */ 
/*     */ public class XrpcProxyServlet extends HttpServlet
/*     */ {
/*  33 */   private XmlRpcServer xmlrpc = new XmlRpcServer();
/*  34 */   public static int XRPC_MAX_THREAD_NUM = 1000;
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/*  40 */     this.xmlrpc.addHandler("$default", this);
/*  41 */     this.xmlrpc.setMaxThreads(XRPC_MAX_THREAD_NUM);
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*  45 */     response.setContentType("text/html; charset=gbk");
/*  46 */     PrintWriter pw = response.getWriter();
/*  47 */     pw.println("<html><br>" + NetHelper.getHostName() + "(" + NetHelper.getHostIP() + ")上[" + System.getProperty("servername") + "]服务器提供的 Xml-RPC (最大线程数=" + XRPC_MAX_THREAD_NUM + ") 服务正常运行！<br><br>");
/*  48 */     pw.println("本服务器发布的服务：<br>");
/*  49 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  50 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  51 */     pw.println("<tr><th>服务名</th></tr>");
/*  52 */     String[] names = { "" };
/*  53 */     Set set = ExportBoManager.getInstance().getExportBoNames();
/*  54 */     if (set.size() > 0) {
/*  55 */       if (set.contains("*")) {
/*  56 */         names = BoHomeFactory.getInstance().getAllBoNames();
/*     */       } else {
/*  58 */         names = new String[set.size()];
/*  59 */         Object[] objs = set.toArray();
/*  60 */         for (int i = 0; i < objs.length; i++) {
/*  61 */           String obj = objs[i].toString();
/*  62 */           names[i] = obj;
/*     */         }
/*     */       }
/*     */     }
/*  66 */     for (int i = 0; i < names.length; i++) {
/*  67 */       String boName = names[i];
/*  68 */       pw.println("<tr bgcolor='#b0ffb0'>");
/*  69 */       pw.println("<td>" + boName + "</td>");
/*  70 */       pw.println("</tr>");
/*     */     }
/*  72 */     pw.println("</table>");
/*     */ 
/*  74 */     pw.println("<br>本服务器各服务接口访问统计：<br>");
/*  75 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  76 */     pw.println("<tr><th>服务.方法</th><th>调用次数</th><th>最大耗时(ms)</th><th>最大耗时发生时间</th><th>最小耗时(ms)</th><th>最小耗时发生时间</th></tr>");
/*  77 */     Map statInfo = BoCmdFactory.getInstance().getBoInvokeStatInfo();
/*  78 */     Iterator iterator = statInfo.values().iterator();
/*  79 */     while (iterator.hasNext()) {
/*  80 */       CmdStatInfo cmdStatInfo = (CmdStatInfo)iterator.next();
/*     */ 
/*  82 */       pw.println("<tr bgcolor='#b0ffb0'>");
/*  83 */       pw.println("<td>" + cmdStatInfo.getActionName() + "</td><td>" + cmdStatInfo.getCount() + "</td><td>" + cmdStatInfo.getMaxActionTime() + "</td><td>" + cmdStatInfo.getMaxActionDate() + "</td><td>" + cmdStatInfo.getMinActionTime() + "</td><td>" + cmdStatInfo.getMinActionDate() + "</td>");
/*     */ 
/*  86 */       pw.println("</tr>");
/*     */     }
/*     */ 
/*  89 */     pw.println("</table>");
/*  90 */     pw.println("</html>");
/*  91 */     pw.flush();
/*  92 */     pw.close();
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 106 */     String type = request.getQueryString();
/* 107 */     if ((type != null) && (type.equals("type=http")))
/* 108 */       processHttpReq(request, response);
/*     */     else
/* 110 */       process(request, response);
/*     */   }
/*     */ 
/*     */   protected void processHttpReq(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 116 */     int fileSize = request.getContentLength();
/* 117 */     byte[] byteArr = new byte[Integer.parseInt(Long.toString(fileSize))];
/*     */     try {
/* 119 */       BufferedInputStream in = new BufferedInputStream(request.getInputStream());
/* 120 */       in.read(byteArr);
/* 121 */       cmd = new String(byteArr);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       String cmd;
/* 124 */       ex.printStackTrace();
/*     */     }
/* 126 */     byte[] result = execBoCommand(byteArr, true, true);
/* 127 */     response.setContentType("text/xml");
/* 128 */     response.setContentLength(result.length);
/* 129 */     OutputStream out = response.getOutputStream();
/* 130 */     out.write(result);
/* 131 */     out.flush();
/* 132 */     out.close();
/*     */   }
/*     */ 
/*     */   protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 136 */     byte[] result = this.xmlrpc.execute(request.getInputStream());
/* 137 */     response.setContentType("text/xml");
/* 138 */     response.setContentLength(result.length);
/* 139 */     OutputStream out = response.getOutputStream();
/* 140 */     out.write(result);
/* 141 */     out.flush();
/* 142 */     out.close();
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras)
/*     */   {
/* 151 */     return execBoCommand(paras, true, false);
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras, boolean isCompressed)
/*     */   {
/* 161 */     return execBoCommand(paras, isCompressed, true);
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras, boolean isCompressed, boolean isNewVersion)
/*     */   {
/* 176 */     long startTime = System.currentTimeMillis();
/* 177 */     long zipTime = -1L;
/*     */ 
/* 179 */     byte[] out = null;
/* 180 */     GenericBoCmd cmd = null;
/* 181 */     String actionName = "";
/* 182 */     String cmdInfo = "none cmd";
/* 183 */     if (BoHomeFactory.getInstance().isIsInitBoHome()) {
/* 184 */       String threadId = ThreadHelper.getCurrentThreadId();
/*     */       try {
/* 186 */         if (isCompressed)
/* 187 */           cmd = GenericBoCmd.fromZipBytes(paras);
/*     */         else {
/* 189 */           cmd = GenericBoCmd.fromBytes(paras);
/*     */         }
/*     */ 
/* 192 */         if (!isNewVersion) {
/* 193 */           cmd.setCompressed(isCompressed);
/*     */         }
/* 195 */         IBoActionContext context = cmd.getActionContext();
/* 196 */         cmdInfo = cmd.toString();
/* 197 */         if (context != null) {
/* 198 */           actionName = context.getActionName();
/* 199 */           if (ActionHelper.isPrintActionLog(actionName)) {
/* 200 */             LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", clientIp=" + cmd.getHostIP() + ", plen=" + paras.length + ", zip=" + cmd.isCompressed() + ", 开始");
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 206 */           BoCmdContext cmdContext = cmd.getCmdContext();
/* 207 */           if (cmdContext != null) {
/* 208 */             actionName = ActionHelper.getActionName(cmdContext.getBoName(), cmdContext.getMethodName());
/*     */           }
/* 210 */           if (ActionHelper.isPrintActionLog(actionName)) {
/* 211 */             LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", plen=" + paras.length + ", zip=" + cmd.isCompressed() + ", 开始");
/*     */           }
/*     */         }
/*     */ 
/* 215 */         BoCmdFactory.getInstance().execBoCmd(cmd);
/*     */       }
/*     */       catch (Exception zipStartTime)
/*     */       {
/*     */         long zipStartTime;
/*     */         boolean isTransaction;
/* 217 */         if (ex.getMessage().contains("Not in GZIP format")) {
/* 218 */           LogHome.getLog().error("非压缩格式命令调用！");
/*     */         }
/*     */ 
/* 221 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */       finally
/*     */       {
/*     */         long zipStartTime;
/*     */         boolean isTransaction;
/* 224 */         long zipStartTime = System.currentTimeMillis();
/* 225 */         out = doCmdResult(cmd);
/* 226 */         zipTime = System.currentTimeMillis() - zipStartTime;
/* 227 */         boolean isTransaction = TransactionFactory.getInstance().getTransaction() != null;
/* 228 */         if (isTransaction) {
/* 229 */           LogHome.getLog().error("XRPC命令调用：" + cmdInfo + ", 事务未正常提交！");
/*     */         }
/*     */       }
/*     */ 
/* 233 */       if (cmd != null) {
/* 234 */         if (ActionHelper.isPrintActionLog(actionName)) {
/* 235 */           int length = -1;
/* 236 */           if (out != null) {
/* 237 */             length = out.length;
/*     */           }
/* 239 */           LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", clientIp=" + cmd.getHostIP() + ", time=" + (System.currentTimeMillis() - startTime) + ", zipTime=" + cmd.getZipTime() + ", unZipTime=" + cmd.getUnZipTime() + ", olen=" + length + ", zip=" + cmd.isCompressed() + ", 结束");
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 246 */         LogHome.getLog().error("命令参数解析错误: XRPC命令参数有客户端对象或JAR文件不匹配!");
/*     */       }
/*     */     } else {
/* 249 */       LogHome.getLog().error("服务器BO还没有加载完成!");
/*     */     }
/* 251 */     return out;
/*     */   }
/*     */ 
/*     */   private byte[] doCmdResult(GenericBoCmd cmd)
/*     */   {
/* 261 */     byte[] out = new byte[0];
/*     */     try {
/* 263 */       if (cmd != null) {
/* 264 */         out = cmd.getResultBytes();
/*     */       } else {
/* 266 */         cmd = (GenericBoCmd)BoCmdFactory.getInstance().createBoCmd("Serializable error", new Object[0]);
/* 267 */         cmd.setException(new UserException("命令参数解析错误: XRPC命令参数有客户端对象或JAR文件不匹配!"));
/* 268 */         out = cmd.getResultBytes();
/*     */       }
/*     */     } catch (Exception ex) {
/* 271 */       LogHome.getLog().error("", ex);
/* 272 */       cmd.setException(new UserException(ex));
/*     */     }
/* 274 */     return out;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.web.XrpcProxyServlet
 * JD-Core Version:    0.6.0
 */