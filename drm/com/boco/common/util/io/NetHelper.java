/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.net.InetAddress;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class NetHelper
/*    */ {
/*    */   public static String getHostName()
/*    */   {
/* 13 */     String hostName = "";
/*    */     try {
/* 15 */       InetAddress address = InetAddress.getLocalHost();
/* 16 */       hostName = address.getHostName();
/*    */     } catch (Exception ex) {
/* 18 */       LogHome.getLog().error("", ex);
/*    */     }
/* 20 */     return hostName;
/*    */   }
/*    */ 
/*    */   public static String getHostIP() {
/* 24 */     String hostIP = "";
/*    */     try {
/* 26 */       InetAddress address = InetAddress.getLocalHost();
/* 27 */       hostIP = address.getHostAddress();
/*    */     } catch (Exception ex) {
/* 29 */       LogHome.getLog().error("", ex);
/*    */     }
/* 31 */     return hostIP;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.NetHelper
 * JD-Core Version:    0.6.0
 */