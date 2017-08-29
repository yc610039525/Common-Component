/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ import org.apache.xmlrpc.CommonsXmlRpcTransportFactory;
/*     */ import org.apache.xmlrpc.XmlRpcClient;
/*     */ import org.apache.xmlrpc.XmlRpcException;
/*     */ 
/*     */ public class HttpBoProxy extends AbstractBoProxy
/*     */ {
/*     */   private URL url;
/*  37 */   private static int _TIMEOUT = 18000000;
/*  38 */   private static int _CONNTIMEOUT = 3600000;
/*  39 */   private int timeout = _TIMEOUT;
/*  40 */   private int conntimeout = _CONNTIMEOUT;
/*     */   private String urlAddr;
/*     */ 
/*     */   public HttpBoProxy(String urlAddr)
/*     */   {
/*  43 */     this.urlAddr = urlAddr;
/*     */   }
/*     */ 
/*     */   public Object exec(IBoCommand cmd) throws Exception {
/*  47 */     if (this.url == null) this.url = new URL(this.urlAddr + "?type=http");
/*  48 */     HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
/*     */ 
/*  55 */     connection.setDoOutput(true);
/*     */ 
/*  57 */     connection.setDoInput(true);
/*     */ 
/*  59 */     connection.setRequestMethod("POST");
/*     */ 
/*  62 */     connection.setUseCaches(false);
/*  63 */     connection.setConnectTimeout(this.timeout);
/*  64 */     connection.setReadTimeout(this.timeout);
/*     */ 
/*  75 */     connection.setInstanceFollowRedirects(true);
/*     */ 
/*  88 */     connection.connect();
/*  89 */     DataOutputStream out = new DataOutputStream(connection.getOutputStream());
/*     */ 
/*  91 */     byte[] bytes = null;
/*  92 */     if (cmd.isCompressed())
/*  93 */       bytes = cmd.getZipBytes();
/*     */     else {
/*  95 */       bytes = cmd.getBytes();
/*     */     }
/*  97 */     out.write(bytes);
/*  98 */     out.flush();
/*  99 */     out.close();
/*     */ 
/* 101 */     int fileSize = connection.getContentLength();
/* 102 */     byte[] result = new byte[Integer.parseInt(Long.toString(fileSize))];
/*     */     try {
/* 104 */       BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
/*     */ 
/* 106 */       in.read(result);
/* 107 */       in.close();
/*     */     } catch (Exception ex) {
/* 109 */       ex.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */ 
/* 117 */     connection.disconnect();
/*     */ 
/* 125 */     GenericBoCmd resBoCmd = null;
/* 126 */     if (cmd.isCompressed())
/* 127 */       resBoCmd = GenericBoCmd.fromZipBytes(result);
/*     */     else {
/* 129 */       resBoCmd = GenericBoCmd.fromBytes(result);
/*     */     }
/* 131 */     cmd.setCmdSize(result.length);
/* 132 */     cmd.setUnZipTime(resBoCmd.getUnZipTime());
/* 133 */     if (resBoCmd.getException() != null)
/*     */     {
/* 135 */       throw resBoCmd.getException();
/*     */     }
/* 137 */     return resBoCmd.getResult();
/*     */   }
/*     */ 
/*     */   protected Object getBoProxy() throws Exception {
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public BoProxyType getBoProxyType() {
/* 145 */     return BoProxyType.XRPC_TYPE;
/*     */   }
/*     */ 
/*     */   public String getIdentifier() {
/* 149 */     return this.urlAddr;
/*     */   }
/*     */ 
/*     */   public void setTimeout(int _timeout)
/*     */   {
/* 156 */     this.timeout = _timeout;
/*     */   }
/*     */ 
/*     */   public int getTimeout()
/*     */   {
/* 164 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setConnectionTimeout(int _conntimeout)
/*     */   {
/* 171 */     this.conntimeout = _conntimeout;
/*     */   }
/*     */ 
/*     */   public int getConnectionTimeout()
/*     */   {
/* 179 */     return this.conntimeout;
/*     */   }
/*     */ 
/*     */   public static void main(String[] arg) throws Exception {
/* 183 */     String uurl = "http://localhost:4000/tnmsdrm/xrpcproxyservlet";
/*     */ 
/* 185 */     CommonsXmlRpcTransportFactory transportFactory = new CommonsXmlRpcTransportFactory(new URL(uurl));
/* 186 */     transportFactory.setConnectionTimeout(_CONNTIMEOUT);
/* 187 */     transportFactory.setTimeout(_TIMEOUT);
/* 188 */     XmlRpcClient client = new XmlRpcClient(new URL(uurl), transportFactory);
/*     */     try {
/* 190 */       Vector paras = new Vector();
/* 191 */       String pr = "dddddd";
/* 192 */       paras.add(pr.getBytes());
/* 193 */       paras.add(Boolean.valueOf(true));
/* 194 */       byte[] result = (byte[])(byte[])client.execute("execBoCommand", paras);
/* 195 */       GenericBoCmd resBoCmd = GenericBoCmd.fromZipBytes(result);
/* 196 */       System.out.println("=============================================================================");
/*     */     } catch (XmlRpcException ex) {
/* 198 */       String msg = ex.getMessage();
/*     */ 
/* 200 */       ex.printStackTrace();
/*     */     } catch (Exception ex) {
/* 202 */       ex.printStackTrace();
/*     */     }
/*     */     try {
/* 205 */       IBoCommand syscmd = BoCmdFactory.getInstance().createBoCmd("", new Object[] { "" });
/* 206 */       HttpBoProxy boProxy = new HttpBoProxy(uurl);
/* 207 */       boProxy.exec(syscmd);
/*     */     } catch (Exception e) {
/* 209 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.HttpBoProxy
 * JD-Core Version:    0.6.0
 */