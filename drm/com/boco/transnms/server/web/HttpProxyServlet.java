/*     */ package com.boco.transnms.server.web;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.NetHelper;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.client.model.base.BoCmdFactory;
/*     */ import com.boco.transnms.client.model.base.CmdStatInfo;
/*     */ import com.boco.transnms.client.model.base.ExportBoManager;
/*     */ import com.boco.transnms.client.model.base.GenericBoCmd;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
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
/*     */ 
/*     */ public class HttpProxyServlet extends HttpServlet
/*     */ {
/*  36 */   public static int XRPC_MAX_THREAD_NUM = 1000;
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  47 */     response.setContentType("text/html; charset=gbk");
/*  48 */     PrintWriter pw = response.getWriter();
/*  49 */     pw.println("<html><br>" + NetHelper.getHostName() + "(" + NetHelper.getHostIP() + ")上[" + System.getProperty("servername") + "]服务器提供的 Xml-RPC (最大线程数=" + XRPC_MAX_THREAD_NUM + ") 服务正常运行！<br><br>");
/*  50 */     pw.println("本服务器发布的服务：<br>");
/*  51 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  52 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  53 */     pw.println("<tr><th>服务名</th></tr>");
/*  54 */     String[] names = { "" };
/*  55 */     Set set = ExportBoManager.getInstance().getExportBoNames();
/*  56 */     if (set.size() > 0) {
/*  57 */       if (set.contains("*")) {
/*  58 */         names = BoHomeFactory.getInstance().getAllBoNames();
/*     */       } else {
/*  60 */         names = new String[set.size()];
/*  61 */         Object[] objs = set.toArray();
/*  62 */         for (int i = 0; i < objs.length; i++) {
/*  63 */           String obj = objs[i].toString();
/*  64 */           names[i] = obj;
/*     */         }
/*     */       }
/*     */     }
/*  68 */     for (int i = 0; i < names.length; i++) {
/*  69 */       String boName = names[i];
/*  70 */       pw.println("<tr bgcolor='#b0ffb0'>");
/*  71 */       pw.println("<td>" + boName + "</td>");
/*  72 */       pw.println("</tr>");
/*     */     }
/*  74 */     pw.println("</table>");
/*     */ 
/*  76 */     pw.println("<br>本服务器各服务接口访问统计：<br>");
/*  77 */     pw.println("<table border=2 cellspacing=2 cellpadding=2>");
/*  78 */     pw.println("<tr><th>服务.方法</th><th>调用次数</th><th>最大耗时(ms)</th><th>最大耗时发生时间</th><th>最小耗时(ms)</th><th>最小耗时发生时间</th></tr>");
/*  79 */     Map statInfo = BoCmdFactory.getInstance().getBoInvokeStatInfo();
/*  80 */     Iterator iterator = statInfo.values().iterator();
/*  81 */     while (iterator.hasNext()) {
/*  82 */       CmdStatInfo cmdStatInfo = (CmdStatInfo)iterator.next();
/*     */ 
/*  84 */       pw.println("<tr bgcolor='#b0ffb0'>");
/*  85 */       pw.println("<td>" + cmdStatInfo.getActionName() + "</td><td>" + cmdStatInfo.getCount() + "</td><td>" + cmdStatInfo.getMaxActionTime() + "</td><td>" + cmdStatInfo.getMaxActionDate() + "</td><td>" + cmdStatInfo.getMinActionTime() + "</td><td>" + cmdStatInfo.getMinActionDate() + "</td>");
/*     */ 
/*  88 */       pw.println("</tr>");
/*     */     }
/*     */ 
/*  91 */     pw.println("</table>");
/*  92 */     pw.println("</html>");
/*  93 */     pw.flush();
/*  94 */     pw.close();
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 107 */     process(request, response);
/*     */   }
/*     */ 
/*     */   protected void process(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 115 */     int fileSize = request.getContentLength();
/* 116 */     byte[] byteArr = new byte[Integer.parseInt(Long.toString(fileSize))];
/*     */     try {
/* 118 */       BufferedInputStream in = new BufferedInputStream(request.getInputStream());
/*     */ 
/* 120 */       in.read(byteArr);
/* 121 */       String cmd = new String(byteArr);
/* 122 */       LogHome.getLog().info("-----------------" + cmd);
/*     */     } catch (Exception ex) {
/* 124 */       ex.printStackTrace();
/*     */     }
/* 126 */     byte[] result = execBoCommand(byteArr, true, true);
/*     */ 
/* 130 */     response.setContentType("text/xml");
/* 131 */     response.setContentLength(result.length);
/* 132 */     OutputStream out = response.getOutputStream();
/* 133 */     out.write(result);
/* 134 */     out.flush();
/* 135 */     out.close();
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras)
/*     */   {
/* 144 */     return execBoCommand(paras, true, false);
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras, boolean isCompressed)
/*     */   {
/* 154 */     return execBoCommand(paras, isCompressed, true);
/*     */   }
/*     */ 
/*     */   public byte[] execBoCommand(byte[] paras, boolean isCompressed, boolean isNewVersion)
/*     */   {
/* 169 */     long startTime = System.currentTimeMillis();
/* 170 */     long zipTime = -1L;
/*     */ 
/* 172 */     byte[] out = null;
/* 173 */     GenericBoCmd cmd = null;
/* 174 */     String actionName = "";
/* 175 */     String cmdInfo = "none cmd";
/* 176 */     if (BoHomeFactory.getInstance().isIsInitBoHome()) {
/* 177 */       String threadId = ThreadHelper.getCurrentThreadId();
/*     */       try {
/* 179 */         if (isCompressed)
/* 180 */           cmd = GenericBoCmd.fromZipBytes(paras);
/*     */         else {
/* 182 */           cmd = GenericBoCmd.fromBytes(paras);
/*     */         }
/*     */ 
/* 185 */         if (!isNewVersion) {
/* 186 */           cmd.setCompressed(isCompressed);
/*     */         }
/* 188 */         IBoActionContext context = cmd.getActionContext();
/* 189 */         cmdInfo = cmd.toString();
/* 190 */         if (context != null) {
/* 191 */           actionName = context.getActionName();
/* 192 */           if (ActionHelper.isPrintActionLog(actionName)) {
/* 193 */             LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", clientIp=" + cmd.getHostIP() + ", plen=" + paras.length + ", zip=" + cmd.isCompressed() + ", 开始");
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 199 */           LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", plen=" + paras.length + ", zip=" + cmd.isCompressed() + ", 开始");
/*     */         }
/*     */ 
/* 202 */         BoCmdFactory.getInstance().execBoCmd(cmd);
/*     */       }
/*     */       catch (Exception zipStartTime)
/*     */       {
/*     */         long zipStartTime;
/* 204 */         if (ex.getMessage().contains("Not in GZIP format")) {
/* 205 */           LogHome.getLog().error("非压缩格式命令调用！");
/*     */         }
/*     */ 
/* 208 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */       finally
/*     */       {
/*     */         long zipStartTime;
/* 211 */         long zipStartTime = System.currentTimeMillis();
/* 212 */         out = doCmdResult(cmd);
/* 213 */         zipTime = System.currentTimeMillis() - zipStartTime;
/*     */       }
/*     */ 
/* 216 */       if (cmd != null) {
/* 217 */         if (ActionHelper.isPrintActionLog(actionName)) {
/* 218 */           int length = -1;
/* 219 */           if (out != null) {
/* 220 */             length = out.length;
/*     */           }
/* 222 */           LogHome.getLog().info("XRPC命令调用：" + cmdInfo + ", clientIp=" + cmd.getHostIP() + ", time=" + (System.currentTimeMillis() - startTime) + ", zipTime=" + cmd.getZipTime() + ", unZipTime=" + cmd.getUnZipTime() + ", olen=" + length + ", zip=" + cmd.isCompressed() + ", 结束");
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 229 */         LogHome.getLog().error("命令参数解析错误: XRPC命令参数有客户端对象或JAR文件不匹配!");
/*     */       }
/*     */     } else {
/* 232 */       LogHome.getLog().error("服务器BO还没有加载完成!");
/*     */     }
/* 234 */     return out;
/*     */   }
/*     */ 
/*     */   private byte[] doCmdResult(GenericBoCmd cmd)
/*     */   {
/* 244 */     byte[] out = new byte[0];
/*     */     try {
/* 246 */       if (cmd != null) {
/* 247 */         out = cmd.getResultBytes();
/*     */       } else {
/* 249 */         cmd = (GenericBoCmd)BoCmdFactory.getInstance().createBoCmd("Serializable error", new Object[0]);
/* 250 */         cmd.setException(new UserException("命令参数解析错误: XRPC命令参数有客户端对象或JAR文件不匹配!"));
/* 251 */         out = cmd.getResultBytes();
/*     */       }
/*     */     } catch (Exception ex) {
/* 254 */       LogHome.getLog().error("", ex);
/* 255 */       cmd.setException(new UserException(ex));
/*     */     }
/* 257 */     return out;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.web.HttpProxyServlet
 * JD-Core Version:    0.6.0
 */