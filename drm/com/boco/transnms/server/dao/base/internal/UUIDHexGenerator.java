/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.Serializable;
/*    */ import java.util.zip.CRC32;
/*    */ 
/*    */ public class UUIDHexGenerator extends AbstractUUIDGenerator
/*    */ {
/* 17 */   private String sep = "";
/* 18 */   CRC32 crc32 = new CRC32();
/*    */ 
/*    */   protected String format(int intval) {
/* 21 */     String formatted = Integer.toHexString(intval);
/* 22 */     StringBuffer buf = new StringBuffer("00000000");
/* 23 */     buf.replace(8 - formatted.length(), 8, formatted);
/* 24 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   protected String format(short shortval) {
/* 28 */     String formatted = Integer.toHexString(shortval);
/* 29 */     StringBuffer buf = new StringBuffer("0000");
/* 30 */     buf.replace(4 - formatted.length(), 4, formatted);
/* 31 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public Serializable generate() {
/* 35 */     return 36 + format(getIP()) + this.sep + format(getJVM()) + this.sep + format(getHiTime()) + this.sep + format(getLoTime()) + this.sep + format(getCount());
/*    */   }
/*    */ 
/*    */   public long generateLong()
/*    */   {
/* 45 */     this.crc32.reset();
/* 46 */     this.crc32.update(((String)generate()).getBytes());
/* 47 */     return this.crc32.getValue();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception
/*    */   {
/* 52 */     UUIDHexGenerator u = new UUIDHexGenerator();
/* 53 */     long max = 0L;
/* 54 */     long cur = 0L;
/* 55 */     for (int i = 0; i < 10115; i++)
/*    */     {
/* 58 */       cur = u.generateLong();
/* 59 */       System.out.println(cur);
/* 60 */       if (cur > max) {
/* 61 */         max = cur;
/*    */       }
/*    */     }
/*    */ 
/* 65 */     System.out.println("-----:1099511627775");
/* 66 */     System.out.println("-----:145742206464");
/* 67 */     System.out.println("-----:" + max);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.UUIDHexGenerator
 * JD-Core Version:    0.6.0
 */