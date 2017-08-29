/*     */ package com.boco.transnms.server.common.cfg;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TnmsDrmCfg
/*     */ {
/*   8 */   private static TnmsDrmCfg instance = new TnmsDrmCfg();
/*     */ 
/*  10 */   private long maxDbTime = 1000L;
/*     */ 
/*  12 */   private long maxBoInvokeTime = 2000L;
/*     */ 
/*  14 */   private long maxQueryResultSize = 2000L;
/*  15 */   private boolean writeSystemLog = false;
/*     */ 
/*  18 */   private static String implDaoName = "GenericObjectDAO";
/*     */   private String dbModelFilePath;
/*     */   private Map<String, String> msgServiceTable;
/*  21 */   private int checkMsgServiceNum = 100;
/*  22 */   private int checkMsgServiceTime = 120;
/*  23 */   private int maxMsgHandleTime = 50;
/*     */ 
/*     */   public static TnmsDrmCfg getInstance()
/*     */   {
/*  28 */     return instance;
/*     */   }
/*     */ 
/*     */   public void setMaxDbTime(long _maxDbTime) {
/*  32 */     this.maxDbTime = _maxDbTime;
/*     */   }
/*     */ 
/*     */   public void setMaxQueryResultSize(long _maxQueryResultSize) {
/*  36 */     this.maxQueryResultSize = _maxQueryResultSize;
/*     */   }
/*     */ 
/*     */   public void setMaxBoInvokeTime(long _maxBoInvokeTime) {
/*  40 */     this.maxBoInvokeTime = _maxBoInvokeTime;
/*     */   }
/*     */ 
/*     */   public void setWriteSystemLog(boolean _writeSystemLog) {
/*  44 */     this.writeSystemLog = _writeSystemLog;
/*     */   }
/*     */ 
/*     */   public void setImplDaoName(String _implDaoName)
/*     */   {
/*  52 */     implDaoName = _implDaoName;
/*     */   }
/*     */ 
/*     */   public void setDbModelFilePath(String _dbModelFilePath) {
/*  56 */     this.dbModelFilePath = _dbModelFilePath;
/*     */   }
/*     */ 
/*     */   public long getMaxDbTime() {
/*  60 */     return this.maxDbTime;
/*     */   }
/*     */ 
/*     */   public long getMaxQueryResultSize() {
/*  64 */     return this.maxQueryResultSize;
/*     */   }
/*     */ 
/*     */   public long getMaxBoInvokeTime() {
/*  68 */     return this.maxBoInvokeTime;
/*     */   }
/*     */ 
/*     */   public boolean isWriteSystemLog() {
/*  72 */     return this.writeSystemLog;
/*     */   }
/*     */ 
/*     */   public String getImplDaoName()
/*     */   {
/*  81 */     return implDaoName;
/*     */   }
/*     */ 
/*     */   public String getDbModelFilePath() {
/*  85 */     return this.dbModelFilePath;
/*     */   }
/*     */ 
/*     */   public int getCheckMsgServiceNum() {
/*  89 */     return this.checkMsgServiceNum;
/*     */   }
/*     */ 
/*     */   public int getCheckMsgServiceTime() {
/*  93 */     return this.checkMsgServiceTime;
/*     */   }
/*     */ 
/*     */   public int getMaxMsgHandleTime() {
/*  97 */     return this.maxMsgHandleTime;
/*     */   }
/*     */ 
/*     */   public Map getMsgServiceTable() {
/* 101 */     return this.msgServiceTable;
/*     */   }
/*     */ 
/*     */   public void setCheckMsgServiceNum(int _checkMsgServiceNum) {
/* 105 */     this.checkMsgServiceNum = _checkMsgServiceNum;
/*     */   }
/*     */ 
/*     */   public void setCheckMsgServiceTime(int _checkMsgServiceTime) {
/* 109 */     this.checkMsgServiceTime = _checkMsgServiceTime;
/*     */   }
/*     */ 
/*     */   public void setMaxMsgHandleTime(int _maxMsgHandleTime) {
/* 113 */     this.maxMsgHandleTime = _maxMsgHandleTime;
/*     */   }
/*     */ 
/*     */   public void setMsgServiceTable(Map msgServiceTable) {
/* 117 */     this.msgServiceTable = msgServiceTable;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.common.cfg.TnmsDrmCfg
 * JD-Core Version:    0.6.0
 */