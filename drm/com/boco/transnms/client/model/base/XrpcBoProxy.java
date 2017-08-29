/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ import org.apache.xmlrpc.CommonsXmlRpcTransportFactory;
/*     */ import org.apache.xmlrpc.XmlRpcClient;
/*     */ import org.apache.xmlrpc.XmlRpcException;
/*     */ 
/*     */ public class XrpcBoProxy extends AbstractBoProxy
/*     */ {
/*     */   private URL url;
/*  29 */   private static int _TIMEOUT = 18000000;
/*  30 */   private static int _CONNTIMEOUT = 3600000;
/*  31 */   private int timeout = _TIMEOUT;
/*  32 */   private int conntimeout = _CONNTIMEOUT;
/*     */   private String urlAddr;
/*     */ 
/*     */   public XrpcBoProxy(String urlAddr)
/*     */   {
/*  35 */     this.urlAddr = urlAddr;
/*     */   }
/*     */ 
/*     */   public Object exec(IBoCommand cmd) throws Exception {
/*  39 */     if (this.url == null) this.url = new URL(this.urlAddr);
/*  40 */     CommonsXmlRpcTransportFactory transportFactory = new CommonsXmlRpcTransportFactory(this.url);
/*  41 */     transportFactory.setConnectionTimeout(this.conntimeout);
/*  42 */     transportFactory.setTimeout(this.timeout);
/*  43 */     XmlRpcClient client = cmd.getCmdTarget() == null ? new XmlRpcClient(this.url, transportFactory) : new XmlRpcClient(new URL(cmd.getCmdTarget()), transportFactory);
/*  44 */     byte[] bytes = null;
/*  45 */     if (cmd.isCompressed())
/*  46 */       bytes = cmd.getZipBytes();
/*     */     else {
/*  48 */       bytes = cmd.getBytes();
/*     */     }
/*  50 */     Vector paras = new Vector();
/*  51 */     paras.add(bytes);
/*  52 */     if (!cmd.isCompressed()) {
/*  53 */       paras.add(Boolean.valueOf(cmd.isCompressed()));
/*     */     }
/*  55 */     byte[] result = (byte[])(byte[])client.execute("execBoCommand", paras);
/*  56 */     GenericBoCmd resBoCmd = null;
/*  57 */     if (cmd.isCompressed())
/*  58 */       resBoCmd = GenericBoCmd.fromZipBytes(result);
/*     */     else {
/*  60 */       resBoCmd = GenericBoCmd.fromBytes(result);
/*     */     }
/*  62 */     cmd.setCmdSize(result.length);
/*  63 */     cmd.setUnZipTime(resBoCmd.getUnZipTime());
/*  64 */     if (resBoCmd.getException() != null)
/*     */     {
/*  66 */       throw resBoCmd.getException();
/*     */     }
/*  68 */     return resBoCmd.getResult();
/*     */   }
/*     */ 
/*     */   protected Object getBoProxy() throws Exception {
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   public BoProxyType getBoProxyType() {
/*  76 */     return BoProxyType.XRPC_TYPE;
/*     */   }
/*     */ 
/*     */   public String getIdentifier() {
/*  80 */     return this.urlAddr;
/*     */   }
/*     */ 
/*     */   public void setTimeout(int _timeout)
/*     */   {
/*  87 */     this.timeout = _timeout;
/*     */   }
/*     */ 
/*     */   public int getTimeout()
/*     */   {
/*  95 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setConnectionTimeout(int _conntimeout)
/*     */   {
/* 102 */     this.conntimeout = _conntimeout;
/*     */   }
/*     */ 
/*     */   public int getConnectionTimeout()
/*     */   {
/* 110 */     return this.conntimeout;
/*     */   }
/*     */ 
/*     */   public static void main(String[] arg) throws Exception {
/* 114 */     String uurl = "http://localhost:4000/tnmsdrm/xrpcproxyservlet";
/*     */ 
/* 116 */     CommonsXmlRpcTransportFactory transportFactory = new CommonsXmlRpcTransportFactory(new URL(uurl));
/* 117 */     transportFactory.setConnectionTimeout(_CONNTIMEOUT);
/* 118 */     transportFactory.setTimeout(_TIMEOUT);
/* 119 */     XmlRpcClient client = new XmlRpcClient(new URL(uurl), transportFactory);
/*     */     try {
/* 121 */       Vector paras = new Vector();
/* 122 */       String pr = "dddddd";
/* 123 */       paras.add(pr.getBytes());
/* 124 */       paras.add(Boolean.valueOf(true));
/* 125 */       byte[] result = (byte[])(byte[])client.execute("execBoCommand", paras);
/* 126 */       GenericBoCmd resBoCmd = GenericBoCmd.fromZipBytes(result);
/* 127 */       System.out.println("=============================================================================");
/*     */     } catch (XmlRpcException ex) {
/* 129 */       String msg = ex.getMessage();
/*     */ 
/* 131 */       ex.printStackTrace();
/*     */     } catch (Exception ex) {
/* 133 */       ex.printStackTrace();
/*     */     }
/*     */     try {
/* 136 */       IBoCommand syscmd = BoCmdFactory.getInstance().createBoCmd("", new Object[] { "" });
/* 137 */       XrpcBoProxy boProxy = new XrpcBoProxy(uurl);
/* 138 */       boProxy.exec(syscmd);
/*     */     } catch (Exception e) {
/* 140 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.XrpcBoProxy
 * JD-Core Version:    0.6.0
 */