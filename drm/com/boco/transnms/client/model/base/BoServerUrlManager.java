/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public final class BoServerUrlManager
/*     */ {
/*  14 */   private static BoServerUrlManager instance = new BoServerUrlManager();
/*  15 */   private Map<String, String> serverUrls = new HashMap();
/*  16 */   private Map<String, String> serverProxyUrls = new HashMap();
/*     */   private Map boServices;
/*     */ 
/*     */   public static BoServerUrlManager getInstance()
/*     */   {
/*  32 */     return instance;
/*     */   }
/*     */ 
/*     */   public void setServerUrls(Map<String, String> _serverUrls)
/*     */   {
/*  40 */     this.serverUrls = _serverUrls;
/*     */   }
/*     */ 
/*     */   public void setServerProxyUrls(Map<String, String> _serverUrls)
/*     */   {
/*  48 */     this.serverProxyUrls = _serverUrls;
/*     */   }
/*     */ 
/*     */   public String getBoServerUrl(String serverName)
/*     */   {
/*  57 */     String serverUrl = "";
/*  58 */     if (this.serverUrls != null) {
/*  59 */       serverUrl = (String)this.serverUrls.get(serverName);
/*  60 */       if (serverUrl == null)
/*  61 */         LogHome.getLog().error("服务器[" + serverName + "]没有配置地址，或未加载地址配置！");
/*     */     }
/*     */     else {
/*  64 */       LogHome.getLog().error("服务器[" + serverName + "]没有配置地址，或未加载地址配置！");
/*     */     }
/*  66 */     return serverUrl;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getBoServerUrls()
/*     */   {
/*  74 */     return this.serverUrls;
/*     */   }
/*     */ 
/*     */   public String getBoServerProxyUrl(String serverName)
/*     */   {
/*  83 */     String serverUrl = "";
/*  84 */     if (this.serverProxyUrls != null) {
/*  85 */       serverUrl = (String)this.serverProxyUrls.get(serverName);
/*  86 */       if (serverUrl == null)
/*  87 */         LogHome.getLog().error("服务器[" + serverName + "]没有配置代理地址，或未加载地址配置！");
/*     */     }
/*     */     else {
/*  90 */       LogHome.getLog().error("服务器[" + serverName + "]没有配置代理地址，或未加载地址配置！");
/*     */     }
/*  92 */     return serverUrl;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getBoServerProxyUrls()
/*     */   {
/* 100 */     return this.serverProxyUrls;
/*     */   }
/*     */ 
/*     */   public Map getBoServices()
/*     */   {
/* 108 */     return this.boServices;
/*     */   }
/*     */ 
/*     */   public void setBoServices(Map _boServices)
/*     */   {
/* 116 */     this.boServices = _boServices;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.BoServerUrlManager
 * JD-Core Version:    0.6.0
 */