/*    */ package com.boco.common.util.id;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class CUIDHexGenerator extends AbstractCUIDGenerator
/*    */ {
/* 24 */   private static CUIDHexGenerator instance = new CUIDHexGenerator();
/*    */   public static final String CUID_PREFIX_SEPRATOR = "-";
/* 27 */   private String sep = "";
/*    */ 
/*    */   protected String format(int intval) {
/* 30 */     String formatted = Integer.toHexString(intval);
/* 31 */     StringBuffer buf = new StringBuffer("00000000");
/* 32 */     buf.replace(8 - formatted.length(), 8, formatted);
/* 33 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   protected String format(short shortval) {
/* 37 */     String formatted = Integer.toHexString(shortval);
/* 38 */     StringBuffer buf = new StringBuffer("0000");
/* 39 */     buf.replace(4 - formatted.length(), 4, formatted);
/* 40 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public String generate() {
/* 44 */     return 36 + format(getIP()) + this.sep + format(getJVM()) + this.sep + format(getHiTime()) + this.sep + format(getLoTime()) + this.sep + format(getCount());
/*    */   }
/*    */ 
/*    */   public String generate(String prefix)
/*    */   {
/* 54 */     return prefix + "-" + generate();
/*    */   }
/*    */ 
/*    */   public static CUIDHexGenerator getInstance() {
/* 58 */     return instance;
/*    */   }
/*    */ 
/*    */   public static String compose(String className, String post) {
/* 62 */     return className + "-" + post;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 66 */     System.out.println(getInstance().generate());
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.id.CUIDHexGenerator
 * JD-Core Version:    0.6.0
 */