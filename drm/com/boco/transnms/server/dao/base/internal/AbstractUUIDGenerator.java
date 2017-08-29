/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ public abstract class AbstractUUIDGenerator
/*    */ {
/*    */   private static final int IP;
/*    */   private static short counter;
/*    */   private static final int JVM;
/*    */ 
/*    */   protected int getJVM()
/*    */   {
/* 45 */     return JVM;
/*    */   }
/*    */ 
/*    */   protected short getCount()
/*    */   {
/* 53 */     synchronized (AbstractUUIDGenerator.class) {
/* 54 */       if (counter < 0) {
/* 55 */         counter = 0;
/*    */       }
/* 57 */       return counter++;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected int getIP()
/*    */   {
/* 65 */     return IP;
/*    */   }
/*    */ 
/*    */   protected short getHiTime()
/*    */   {
/* 72 */     return (short)(int)(System.currentTimeMillis() >>> 32);
/*    */   }
/*    */ 
/*    */   protected int getLoTime() {
/* 76 */     return (int)System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     int ipadd;
/*    */     try
/*    */     {
/* 21 */       int result = 0;
/* 22 */       for (int i = 0; i < 4; i++) {
/* 23 */         result = (result << 8) - -128 + java.net.InetAddress.getLocalHost().getAddress()[i];
/*    */       }
/*    */ 
/* 26 */       ipadd = result;
/*    */     } catch (Exception e) {
/* 28 */       ipadd = 0;
/*    */     }
/* 30 */     IP = ipadd;
/*    */ 
/* 33 */     counter = 0;
/*    */ 
/* 35 */     JVM = (int)(System.currentTimeMillis() >>> 8);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.AbstractUUIDGenerator
 * JD-Core Version:    0.6.0
 */