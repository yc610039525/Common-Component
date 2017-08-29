/*     */ package com.boco.transnms.server.dao.base.internal;
/*     */ 
/*     */ public class Globals
/*     */ {
/*   5 */   private static String MODEL_FILE = "F:\\Server\\arenaServer\\cfg\\goat\\goat_model.xml";
/*     */ 
/*   9 */   private static boolean USEFORCERELATION = true;
/*     */ 
/*  11 */   private static boolean IFDEBUGAPIINVOKE = true;
/*     */ 
/*  13 */   private static boolean IFUSEOMP = false;
/*     */ 
/*  15 */   private static int USCOUNT = -1;
/*     */ 
/*  17 */   private static int FETCHSIZE = 800;
/*     */ 
/*  19 */   private static int SERVERID = -128;
/*     */ 
/*     */   public static void setSERVERID(int serverId) {
/*  22 */     SERVERID = serverId;
/*     */   }
/*     */ 
/*     */   public static int getSERVERID() {
/*  26 */     return SERVERID;
/*     */   }
/*     */ 
/*     */   public static int getFETCHSIZE() {
/*  30 */     return FETCHSIZE;
/*     */   }
/*     */ 
/*     */   public static void setDBInfo(String dsName, String dbUrl, String userName, String password, String dbCharset, int maxActive)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void setUpdateStatisticsCount(int count)
/*     */   {
/*  77 */     USCOUNT = count;
/*     */   }
/*     */ 
/*     */   public static int getUpdateStatisticsCount() {
/*  81 */     return USCOUNT;
/*     */   }
/*     */ 
/*     */   public static boolean isIFDEBUGAPIINVOKE() {
/*  85 */     return IFDEBUGAPIINVOKE;
/*     */   }
/*     */ 
/*     */   public static String getMODEL_FILE() {
/*  89 */     return MODEL_FILE;
/*     */   }
/*     */ 
/*     */   public static boolean isUSEFORCERELATION()
/*     */   {
/*  97 */     return USEFORCERELATION;
/*     */   }
/*     */ 
/*     */   public static boolean isIFUSEOMP() {
/* 101 */     return IFUSEOMP;
/*     */   }
/*     */ 
/*     */   public static void setFETCHSIZE(int fetchsize) {
/* 105 */     FETCHSIZE = fetchsize;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.Globals
 * JD-Core Version:    0.6.0
 */