/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ public final class HttpProxyManager
/*     */ {
/*  26 */   private static HttpProxyManager instance = new HttpProxyManager();
/*     */   private String httpProxy;
/*     */   private boolean directProxy;
/*     */ 
/*     */   public static HttpProxyManager getInstance()
/*     */   {
/*  41 */     return instance;
/*     */   }
/*     */ 
/*     */   public String getHttpProxy()
/*     */   {
/*  49 */     return this.httpProxy;
/*     */   }
/*     */ 
/*     */   public void setHttpProxy(String _httpProxy)
/*     */   {
/*  57 */     this.httpProxy = _httpProxy;
/*     */   }
/*     */ 
/*     */   public boolean isHaveHttpProxy()
/*     */   {
/*  65 */     return (this.httpProxy != null) && (this.httpProxy.trim().length() > 0);
/*     */   }
/*     */ 
/*     */   public String getProxyXrpcUrl(String url)
/*     */   {
/*  76 */     if (!this.directProxy) {
/*  77 */       if (this.httpProxy != null) {
/*  78 */         String[] urls = url.split("/");
/*  79 */         String[] ipPort = urls[2].split(":");
/*  80 */         String port = ipPort[1];
/*  81 */         url = this.httpProxy + "/" + port;
/*     */       }
/*     */     }
/*  84 */     else url = null;
/*     */ 
/*  86 */     return url;
/*     */   }
/*     */ 
/*     */   public boolean isDirectProxy()
/*     */   {
/*  94 */     return this.directProxy;
/*     */   }
/*     */ 
/*     */   public void setDirectProxy(boolean _directProxy)
/*     */   {
/* 102 */     this.directProxy = _directProxy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.HttpProxyManager
 * JD-Core Version:    0.6.0
 */