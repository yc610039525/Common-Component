/*    */ package com.boco.transnms.common.dto.common;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.io.Serializable;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ServerChange
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ServerState appServer;
/*    */   private ServerState alarmServer;
/*    */ 
/*    */   public ServerChange(ServerState appServer, ServerState alarmServer)
/*    */   {
/* 24 */     this.appServer = appServer;
/* 25 */     this.alarmServer = alarmServer;
/*    */   }
/*    */ 
/*    */   public ServerChange() {
/*    */   }
/*    */ 
/*    */   public void setAppServer(ServerState appServer) {
/* 32 */     this.appServer = appServer;
/*    */   }
/*    */ 
/*    */   public void setAlarmServer(ServerState alarmServer) {
/* 36 */     this.alarmServer = alarmServer;
/*    */   }
/*    */ 
/*    */   public void printLog() {
/* 40 */     LogHome.getLog().info("==================服务器变更通知！==================");
/* 41 */     if (this.appServer != null) {
/* 42 */       LogHome.getLog().info("应用服务器：" + this.appServer.getServerType() + "," + this.appServer.getServerName() + "," + this.appServer.getServerXrpcUrl() + "," + this.appServer.getServerAddress());
/*    */     }
/* 44 */     if (this.alarmServer != null) {
/* 45 */       LogHome.getLog().info("告警服务器：" + this.alarmServer.getServerType() + "," + this.alarmServer.getServerName() + "," + this.alarmServer.getServerXrpcUrl() + "," + this.alarmServer.getServerAddress());
/*    */     }
/* 47 */     LogHome.getLog().info("==================服务器变更通知！==================");
/*    */   }
/*    */ 
/*    */   public ServerState getAlarmServer() {
/* 51 */     return this.alarmServer;
/*    */   }
/*    */ 
/*    */   public ServerState getAppServer() {
/* 55 */     return this.appServer;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.common.ServerChange
 * JD-Core Version:    0.6.0
 */