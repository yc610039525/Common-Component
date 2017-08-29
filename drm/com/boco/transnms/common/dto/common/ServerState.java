/*     */ package com.boco.transnms.common.dto.common;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class ServerState
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String serverName;
/*     */   private String serverType;
/*     */   private String serverAddress;
/*     */   private String serverXrpcUrl;
/*     */   private String proxyServerXrpcUrl;
/*  25 */   private boolean isActivityServer = false;
/*     */   private boolean supportAlarmBalance;
/*     */   private String heartServerAddress;
/*     */ 
/*     */   public ServerState(String serverName, String serverType, String serverAddress, String serverXrpcUrl, String proxyServerXrpcUrl)
/*     */   {
/*  30 */     this.serverName = serverName;
/*  31 */     this.serverType = serverType;
/*  32 */     this.serverAddress = serverAddress;
/*  33 */     this.serverXrpcUrl = serverXrpcUrl;
/*  34 */     this.proxyServerXrpcUrl = proxyServerXrpcUrl;
/*     */   }
/*     */ 
/*     */   public String toMessage() {
/*  38 */     return this.serverName + "," + this.serverType + "," + this.serverXrpcUrl + "," + this.serverAddress.toString() + "," + this.supportAlarmBalance + "," + this.isActivityServer;
/*     */   }
/*     */ 
/*     */   public String getServerAddress()
/*     */   {
/*  44 */     return this.serverAddress;
/*     */   }
/*     */ 
/*     */   public String getServerIp() {
/*  48 */     String ip = "";
/*  49 */     if (this.serverAddress != null) {
/*  50 */       ip = this.serverAddress.split(":")[0];
/*     */     }
/*  52 */     return ip;
/*     */   }
/*     */ 
/*     */   public String getServerXrpcAddress() {
/*  56 */     String serverXrpcAddress = "";
/*     */     try {
/*  58 */       if (this.serverXrpcUrl != null) {
/*  59 */         String substr = this.serverXrpcUrl.split("://")[1];
/*  60 */         if (substr != null)
/*  61 */           serverXrpcAddress = substr.split("/")[0];
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/*  65 */       ex.printStackTrace();
/*     */     }
/*  67 */     return serverXrpcAddress;
/*     */   }
/*     */ 
/*     */   public String getServerName() {
/*  71 */     return this.serverName;
/*     */   }
/*     */ 
/*     */   public String getServerType() {
/*  75 */     return this.serverType;
/*     */   }
/*     */ 
/*     */   public String getServerXrpcUrl() {
/*  79 */     return this.serverXrpcUrl;
/*     */   }
/*     */ 
/*     */   public String getProxyServerXrpcUrl() {
/*  83 */     return this.proxyServerXrpcUrl;
/*     */   }
/*     */ 
/*     */   public boolean isSupportAlarmBalance() {
/*  87 */     return this.supportAlarmBalance;
/*     */   }
/*     */ 
/*     */   public String getHeartServerAddress() {
/*  91 */     return this.heartServerAddress;
/*     */   }
/*     */ 
/*     */   public boolean isActivityServer() {
/*  95 */     return this.isActivityServer;
/*     */   }
/*     */ 
/*     */   public void setServerAddress(String serverAddress) {
/*  99 */     this.serverAddress = serverAddress;
/*     */   }
/*     */ 
/*     */   public void setServerName(String serverName) {
/* 103 */     this.serverName = serverName;
/*     */   }
/*     */ 
/*     */   public void setServerType(String serverType) {
/* 107 */     this.serverType = serverType;
/*     */   }
/*     */ 
/*     */   public void setServerXrpcUrl(String serverXrpcUrl) {
/* 111 */     this.serverXrpcUrl = serverXrpcUrl;
/*     */   }
/*     */ 
/*     */   public void setProxyServerXrpcUrl(String proxyServerXrpcUrl) {
/* 115 */     this.proxyServerXrpcUrl = proxyServerXrpcUrl;
/*     */   }
/*     */ 
/*     */   public void setSupportAlarmBalance(boolean supportAlarmBalance) {
/* 119 */     this.supportAlarmBalance = supportAlarmBalance;
/*     */   }
/*     */ 
/*     */   public void setHeartServerAddress(String heartServerAddress) {
/* 123 */     this.heartServerAddress = heartServerAddress;
/*     */   }
/*     */   public void setActivityServer(boolean isActivityServer) {
/* 126 */     this.isActivityServer = isActivityServer;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.common.ServerState
 * JD-Core Version:    0.6.0
 */