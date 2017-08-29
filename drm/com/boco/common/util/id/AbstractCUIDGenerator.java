/*    */ package com.boco.common.util.id;
/*    */ 
/*    */ public abstract class AbstractCUIDGenerator
/*    */ {
/*    */   private static final int IP;
/*    */   private static short counter;
/*    */   private static final int JVM;
/*    */ 
/*    */   protected int getJVM()
/*    */   {
/* 53 */     return JVM;
/*    */   }
/*    */ 
/*    */   protected short getCount()
/*    */   {
/* 61 */     synchronized (AbstractCUIDGenerator.class) {
/* 62 */       if (counter < 0)
/* 63 */         counter = 0;
/* 64 */       return counter++;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected int getIP()
/*    */   {
/* 72 */     return IP;
/*    */   }
/*    */ 
/*    */   protected short getHiTime()
/*    */   {
/* 79 */     return (short)(int)(System.currentTimeMillis() >>> 32);
/*    */   }
/*    */ 
/*    */   protected int getLoTime() {
/* 83 */     return (int)System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     int ipadd;
/*    */     try
/*    */     {
/* 29 */       int result = 0;
/* 30 */       for (int i = 0; i < 4; i++) {
/* 31 */         result = (result << 8) - -128 + java.net.InetAddress.getLocalHost().getAddress()[i];
/*    */       }
/*    */ 
/* 34 */       ipadd = result;
/*    */     } catch (Exception e) {
/* 36 */       ipadd = 0;
/*    */     }
/* 38 */     IP = ipadd;
/*    */ 
/* 41 */     counter = 0;
/*    */ 
/* 43 */     JVM = (int)(System.currentTimeMillis() >>> 8);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.id.AbstractCUIDGenerator
 * JD-Core Version:    0.6.0
 */