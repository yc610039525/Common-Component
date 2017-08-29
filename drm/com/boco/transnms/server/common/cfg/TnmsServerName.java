/*     */ package com.boco.transnms.server.common.cfg;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class TnmsServerName
/*     */ {
/*  14 */   public static int WEBMASTER_SERVER_ID = 50;
/*  15 */   public static int WEBATTEMP_SERVER_ID = 51;
/*  16 */   public static int WEBATTEMPX_SERVER_ID = 52;
/*  17 */   public static int WEBOPTATTEMP_SERVER_ID = 53;
/*  18 */   public static int WEBPERFORMANCE_SERVER_ID = 54;
/*  19 */   public static int WEBREPORT_SERVER_ID = 55;
/*  20 */   public static int WEBWORKFLOW_SERVER_ID = 56;
/*  21 */   public static int TRANSNTP_SERVER_ID = 57;
/*  22 */   public static int WEB_AN_ID = 58;
/*     */   private static ServerName localServerName;
/*  26 */   private static String sLocalServerFullName = "ALL-0";
/*  27 */   private static String sLocalServerName = "ALL";
/*     */   public static final String SERVER_NAME_KEY = "servername";
/*     */ 
/*     */   public static void setLocalServerFullName(String serverFullName)
/*     */   {
/*     */     try
/*     */     {
/*  35 */       sLocalServerFullName = serverFullName;
/*  36 */       if (serverFullName.contains("-"))
/*  37 */         sLocalServerName = sLocalServerFullName.split("-")[0];
/*     */       else
/*  39 */         sLocalServerName = sLocalServerFullName;
/*     */       try
/*     */       {
/*  42 */         localServerName = ServerName.valueOf(sLocalServerName);
/*     */       } catch (Exception ex) {
/*  44 */         LogHome.getLog().error("服务器名配置错误：" + serverFullName + ",正确的格式为:[ServerName]-[ServerId],如：CM-1");
/*     */       }
/*  46 */       LogHome.getLog().info("系统配置的完整服务器名：" + sLocalServerFullName + " 服务器类型名称：" + localServerName);
/*     */     } catch (Exception ex) {
/*  48 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static ServerName getLocalServerName() {
/*  53 */     if (localServerName == null) {
/*  54 */       if (sLocalServerName == null) {
/*  55 */         LogHome.getLog().warn("ALL-IN-ONE服务器启动: -Dservername=ALL，多服务器启动：-Dservername=XXX");
/*  56 */         sLocalServerName = "ALL";
/*     */       }
/*  58 */       localServerName = ServerName.valueOf(sLocalServerName);
/*     */     }
/*  60 */     return localServerName;
/*     */   }
/*     */ 
/*     */   public static String getServerFullName()
/*     */   {
/*  69 */     String serverName = getLocalServerFullName();
/*  70 */     String sName = System.getProperty("servername");
/*  71 */     if (((sLocalServerFullName == null) || (sLocalServerFullName.contains("ALL"))) && 
/*  72 */       (sName != null)) {
/*  73 */       serverName = sName;
/*     */     }
/*     */ 
/*  76 */     return serverName;
/*     */   }
/*     */ 
/*     */   public static String getLocalServerFullName() {
/*  80 */     if (sLocalServerFullName == null) {
/*  81 */       LogHome.getLog().warn("ALL-IN-ONE服务器启动: -Dservername=ALL，多服务器启动：-Dservername=XXX");
/*  82 */       sLocalServerFullName = "ALL";
/*     */     }
/*  84 */     return sLocalServerFullName;
/*     */   }
/*     */ 
/*     */   public static String getLocalServerNameStr() {
/*  88 */     return sLocalServerName != null ? sLocalServerName : "";
/*     */   }
/*     */ 
/*     */   public static boolean isLocalServer(String _serverName) {
/*  92 */     if (getLocalServerFullName().equals(ServerName.ALL.toString())) return true;
/*     */     try
/*     */     {
/*  95 */       return _serverName.equals(getLocalServerFullName()); } catch (Exception e) {
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isLocalServer(ServerName _serverName)
/*     */   {
/* 103 */     if (getLocalServerFullName().equals(ServerName.ALL.toString())) return true;
/* 104 */     return _serverName.toString().equals(getLocalServerFullName());
/*     */   }
/*     */ 
/*     */   public static boolean isAllInOneServer() {
/* 108 */     return getLocalServerFullName().equals(ServerName.ALL.toString());
/*     */   }
/*     */ 
/*     */   public static boolean isRunServerMode()
/*     */   {
/* 117 */     return localServerName != null;
/*     */   }
/*     */ 
/*     */   public static boolean isAlarmServer() {
/* 121 */     boolean isAlarmServer = false;
/* 122 */     String serverName = getLocalServerNameStr();
/* 123 */     if ((serverName.equals(ServerName.AM.toString())) || (serverName.equals(ServerName.AMRTU.toString())) || (serverName.equals(ServerName.FAULT.toString())) || (serverName.equals(ServerName.ROOTALARM.toString())) || (serverName.equals(ServerName.ALARMNTP.toString())) || (serverName.equals(ServerName.PM.toString())) || (serverName.equals(ServerName.UIP.toString())) || (serverName.equals(ServerName.AMDTS.toString())) || (serverName.equals(ServerName.ALARMALALYSE_AN.toString())) || (serverName.contains(ServerName.AM_AN.toString())) || (serverName.equals(ServerName.PM_AN.toString())) || (serverName.equals(WebServerName.WebPerformance.toString())))
/*     */     {
/* 137 */       isAlarmServer = true;
/*     */     }
/* 139 */     return isAlarmServer;
/*     */   }
/*     */ 
/*     */   public static int getLocalServerId()
/*     */   {
/* 151 */     int serverId = 0;
/* 152 */     if (isRunServerMode()) {
/* 153 */       ServerName _serverName = getLocalServerName();
/* 154 */       if (_serverName == ServerName.CM)
/* 155 */         serverId = 1;
/* 156 */       else if (_serverName == ServerName.CMRTU)
/* 157 */         serverId = 7;
/* 158 */       else if (_serverName == ServerName.ATTEMP)
/* 159 */         serverId = 2;
/* 160 */       else if (_serverName == ServerName.NETWORKADJUST)
/* 161 */         serverId = 3;
/* 162 */       else if (_serverName == ServerName.IMPORT)
/* 163 */         serverId = 4;
/* 164 */       else if (_serverName == ServerName.PATHCHECK)
/* 165 */         serverId = 8;
/* 166 */       else if (_serverName == ServerName.DM)
/* 167 */         serverId = 5;
/* 168 */       else if (_serverName == ServerName.OPTICALCHECK)
/* 169 */         serverId = 9;
/* 170 */       else if (_serverName == ServerName.AM)
/* 171 */         serverId = 6;
/* 172 */       else if (_serverName == ServerName.AMRTU)
/* 173 */         serverId = 11;
/* 174 */       else if (_serverName == ServerName.FAULT)
/* 175 */         serverId = 7;
/* 176 */       else if (_serverName == ServerName.ROOTALARM)
/* 177 */         serverId = 8;
/* 178 */       else if (_serverName == ServerName.ALARMNTP)
/* 179 */         serverId = 9;
/* 180 */       else if (_serverName == ServerName.PM)
/* 181 */         serverId = 10;
/* 182 */       else if (_serverName == ServerName.COLLECTCM)
/* 183 */         serverId = 11;
/* 184 */       else if (_serverName == ServerName.EVENTCM)
/* 185 */         serverId = 51;
/* 186 */       else if (_serverName == ServerName.UIP)
/* 187 */         serverId = 71;
/* 188 */       else if (_serverName == ServerName.NTP)
/* 189 */         serverId = 72;
/* 190 */       else if (_serverName == ServerName.AMDTS)
/* 191 */         serverId = 73;
/* 192 */       else if (_serverName == ServerName.CMDTS)
/* 193 */         serverId = 74;
/* 194 */       else if (_serverName == ServerName.SCHEDULEDTS)
/* 195 */         serverId = 75;
/* 196 */       else if (_serverName == ServerName.RESUIP)
/* 197 */         serverId = 76;
/* 198 */       else if (_serverName == ServerName.PCU)
/* 199 */         serverId = 61;
/* 200 */       else if (_serverName == ServerName.ALARMALALYSE_AN)
/* 201 */         serverId = 50;
/* 202 */       else if (_serverName.toString().contains(ServerName.AM_AN.toString()))
/* 203 */         serverId = 11;
/* 204 */       else if (_serverName == ServerName.CMRTU_AN)
/* 205 */         serverId = 61;
/* 206 */       else if (_serverName == ServerName.DC_AN)
/* 207 */         serverId = 91;
/* 208 */       else if (_serverName == ServerName.BTEST_AN)
/* 209 */         serverId = 92;
/* 210 */       else if (_serverName == ServerName.PM_AN)
/* 211 */         serverId = 93;
/* 212 */       else if (_serverName == ServerName.AREARES)
/* 213 */         serverId = 94;
/* 214 */       else if (_serverName == ServerName.COLLECTODN)
/* 215 */         serverId = 95;
/*     */     }
/*     */     else {
/* 218 */       String _serverName = getLocalServerNameStr();
/* 219 */       if (_serverName.equals(WebServerName.WebMaster.toString()))
/* 220 */         serverId = WEBMASTER_SERVER_ID;
/* 221 */       else if (_serverName.equals(WebServerName.WebAttemp.toString()))
/* 222 */         serverId = WEBATTEMP_SERVER_ID;
/* 223 */       else if (_serverName.equals(WebServerName.WebAttempX.toString()))
/* 224 */         serverId = WEBATTEMPX_SERVER_ID;
/* 225 */       else if (_serverName.equals(WebServerName.WebPerformance.toString()))
/* 226 */         serverId = WEBPERFORMANCE_SERVER_ID;
/* 227 */       else if (_serverName.equals(WebServerName.WebReport.toString()))
/* 228 */         serverId = WEBREPORT_SERVER_ID;
/* 229 */       else if (_serverName.equals(WebServerName.WebWorkFlow.toString()))
/* 230 */         serverId = WEBWORKFLOW_SERVER_ID;
/* 231 */       else if (_serverName.equals(WebServerName.WebAn.toString())) {
/* 232 */         serverId = WEB_AN_ID;
/*     */       }
/*     */     }
/* 235 */     return serverId;
/*     */   }
/*     */ 
/*     */   public static enum WebServerName
/*     */   {
/*   9 */     WebMaster, WebAttemp, WebAttempX, WebReport, Portal, WebPerformance, 
/*  10 */     WebWorkFlow, WebAttempOpt, PonMaster, NetAdjust, WebAn;
/*     */   }
/*     */ 
/*     */   public static enum ServerName
/*     */   {
/*   6 */     ALL, CM, CMRTU, ATTEMP, NETWORKADJUST, PATHCHECK, IMPORT, DM, 
/*   7 */     OPTICALCHECK, AM, AMRTU, FAULT, ROOTALARM, ALARMNTP, PM, UIP, NTP, AMDTS, CMDTS, SCHEDULEDTS, COLLECTODN, 
/*   8 */     RESUIP, RES, ALARM, COLLECTCM, EVENTCM, PCU, ALARMALALYSE_AN, AM_AN, CMRTU_AN, DC_AN, BTEST_AN, PM_AN, AREARES;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.common.cfg.TnmsServerName
 * JD-Core Version:    0.6.0
 */