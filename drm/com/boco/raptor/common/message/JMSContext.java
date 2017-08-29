/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ public class JMSContext
/*     */ {
/*  15 */   private String contextName = "";
/*     */   private String msgServiceType;
/*     */   private String url;
/*     */   private int port;
/*     */   private String queueManager;
/*     */   private String channel;
/*     */   private int ccsid;
/*     */   private String userName;
/*     */   private String password;
/*     */   private String channelType;
/*  25 */   private String parameters = "";
/*  26 */   private int realizationType = 0;
/*     */ 
/*  28 */   private int timeToLive = 0;
/*     */ 
/*     */   public JMSContext(String contextName, String msgServiceType, String channelType, String url, int port, String userName, String password, String parameters)
/*     */   {
/*  32 */     this.contextName = contextName;
/*  33 */     this.msgServiceType = msgServiceType;
/*  34 */     this.channelType = channelType;
/*  35 */     this.url = url;
/*  36 */     this.port = port;
/*  37 */     this.userName = userName;
/*  38 */     this.password = password;
/*  39 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public JMSContext(String contextName, String msgServiceType, String channelType, String url, int port, String userName, String password, String queueManager, String channel, int ccsid, String parameters)
/*     */   {
/*  45 */     this.contextName = contextName;
/*  46 */     this.msgServiceType = msgServiceType;
/*  47 */     this.channelType = channelType;
/*  48 */     this.url = url;
/*  49 */     this.port = port;
/*  50 */     this.userName = userName;
/*  51 */     this.password = password;
/*  52 */     this.queueManager = queueManager;
/*  53 */     this.channel = channel;
/*  54 */     this.ccsid = ccsid;
/*  55 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public String getContextName() {
/*  59 */     return this.contextName;
/*     */   }
/*     */ 
/*     */   public void setContextName(String contextName) {
/*  63 */     this.contextName = contextName;
/*     */   }
/*     */ 
/*     */   public String getChannelType() {
/*  67 */     return this.channelType;
/*     */   }
/*     */ 
/*     */   public String getChannelTypeUpperCase() {
/*  71 */     return this.channelType.toUpperCase();
/*     */   }
/*     */ 
/*     */   public void setChannelType(String channelType) {
/*  75 */     this.channelType = channelType;
/*     */   }
/*     */ 
/*     */   public String getParameters() {
/*  79 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(String parameters) {
/*  83 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public String getUrl() {
/*  87 */     return this.url;
/*     */   }
/*     */ 
/*     */   public void setUrl(String url) {
/*  91 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public int getPort() {
/*  95 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setPort(int port) {
/*  99 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public String getQueueManager() {
/* 103 */     return this.queueManager;
/*     */   }
/*     */ 
/*     */   public void setQueueManager(String queueManager) {
/* 107 */     this.queueManager = queueManager;
/*     */   }
/*     */ 
/*     */   public String getChannel() {
/* 111 */     return this.channel;
/*     */   }
/*     */ 
/*     */   public void setChannel(String channel) {
/* 115 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   public int getCcsid() {
/* 119 */     return this.ccsid;
/*     */   }
/*     */ 
/*     */   public void setCcsid(int ccsid) {
/* 123 */     this.ccsid = ccsid;
/*     */   }
/*     */ 
/*     */   public String getUser() {
/* 127 */     return this.userName;
/*     */   }
/*     */ 
/*     */   public void setUser(String user) {
/* 131 */     this.userName = user;
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 135 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password) {
/* 139 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public String getMsgServiceType() {
/* 143 */     return this.msgServiceType;
/*     */   }
/*     */ 
/*     */   public String getMsgServiceTypeUpperCase() {
/* 147 */     return this.msgServiceType.toUpperCase();
/*     */   }
/*     */ 
/*     */   public void setMsgServiceType(String mqType) {
/* 151 */     this.msgServiceType = mqType;
/*     */   }
/*     */ 
/*     */   public int getRealizationType() {
/* 155 */     return this.realizationType;
/*     */   }
/*     */ 
/*     */   public void setRealizationType(int realizationType) {
/* 159 */     this.realizationType = realizationType;
/*     */   }
/*     */ 
/*     */   public int getTimeToLive() {
/* 163 */     return this.timeToLive;
/*     */   }
/*     */ 
/*     */   public void setTimeToLive(int timeToLive) {
/* 167 */     this.timeToLive = timeToLive;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 171 */     StringBuffer sb = new StringBuffer("JMSContext Information:\n");
/* 172 */     sb.append("\t [contextName=").append(this.contextName).append("\t [mqType=").append(this.msgServiceType).append("],\n").append("\t [url=").append(this.url).append("],\n").append("\t [port=").append(this.port).append("],\n").append("\t [queueManager=").append(this.queueManager).append("],\n").append("\t [channelType=").append(this.channelType).append("],\n").append("\t [channel=").append(this.channel).append("],\n").append("\t [ccsid=").append(this.ccsid).append("],\n").append("\t [realizationType=").append(this.realizationType).append("],\n").append("\t [timeToLive=").append(this.timeToLive).append("]\n");
/*     */ 
/* 182 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.JMSContext
 * JD-Core Version:    0.6.0
 */