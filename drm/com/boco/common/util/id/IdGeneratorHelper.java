/*    */ package com.boco.common.util.id;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class IdGeneratorHelper
/*    */ {
/* 26 */   private static int IP = -1;
/* 27 */   private static short counter = 0;
/* 28 */   private static final int JVM = (int)(System.currentTimeMillis() >>> 8);
/*    */ 
/*    */   public static int getJVM()
/*    */   {
/* 34 */     return JVM;
/*    */   }
/*    */ 
/*    */   public static short getCount() {
/* 38 */     synchronized (IdGeneratorHelper.class) {
/* 39 */       if (counter < 0) counter = 0;
/* 40 */       return counter++;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static int getIP() {
/* 45 */     if (IP == -1) {
/* 46 */       int ipAddr = 0;
/*    */       try {
/* 48 */         int result = 0;
/* 49 */         for (int i = 0; i < 4; i++) {
/* 50 */           result = (result << 8) - -128 + java.net.InetAddress.getLocalHost().getAddress()[i];
/*    */         }
/* 52 */         ipAddr = result;
/*    */       } catch (Exception e) {
/* 54 */         LogHome.getLog().error("", e);
/*    */       }
/* 56 */       IP = ipAddr;
/*    */     }
/* 58 */     return IP;
/*    */   }
/*    */ 
/*    */   public static short getHiTime() {
/* 62 */     return (short)(int)(System.currentTimeMillis() >>> 32);
/*    */   }
/*    */ 
/*    */   public static int getLoTime() {
/* 66 */     return (int)System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public static String format(int intval) {
/* 70 */     String formatted = Integer.toHexString(intval);
/* 71 */     StringBuffer buf = new StringBuffer("00000000");
/* 72 */     buf.replace(8 - formatted.length(), 8, formatted);
/* 73 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public static String format(short shortval) {
/* 77 */     String formatted = Integer.toHexString(shortval);
/* 78 */     StringBuffer buf = new StringBuffer("0000");
/* 79 */     buf.replace(4 - formatted.length(), 4, formatted);
/* 80 */     return buf.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.id.IdGeneratorHelper
 * JD-Core Version:    0.6.0
 */