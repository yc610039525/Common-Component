/*    */ package com.boco.common.util.id;
/*    */ 
/*    */ public class IdHexGenerator
/*    */ {
/* 22 */   private static IdHexGenerator instance = new IdHexGenerator();
/*    */   public static final String PREFIX_SEPRATOR = "-";
/*    */   private static final String SEPRATOR = "";
/*    */ 
/*    */   public static IdHexGenerator getInstance()
/*    */   {
/* 27 */     return instance;
/*    */   }
/*    */ 
/*    */   public String generate() {
/* 31 */     String id = "";
/* 32 */     id = id + IdGeneratorHelper.format(IdGeneratorHelper.getIP()) + "";
/* 33 */     id = id + IdGeneratorHelper.format(IdGeneratorHelper.getJVM()) + "";
/* 34 */     id = id + IdGeneratorHelper.format(IdGeneratorHelper.getHiTime()) + "";
/* 35 */     id = id + IdGeneratorHelper.format(IdGeneratorHelper.getLoTime()) + "";
/* 36 */     id = id + IdGeneratorHelper.format(IdGeneratorHelper.getCount());
/* 37 */     return id;
/*    */   }
/*    */ 
/*    */   public String generate(String prefix) {
/* 41 */     return prefix + "-" + generate();
/*    */   }
/*    */ 
/*    */   public String compose(String className, String post) {
/* 45 */     return className + "-" + post;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.id.IdHexGenerator
 * JD-Core Version:    0.6.0
 */