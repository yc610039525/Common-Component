/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ public class CompareHelper
/*    */ {
/* 21 */   private static String __ENCODE__ = "GBK";
/* 22 */   private static String __SERVER_ENCODE__ = "GB2312";
/*    */ 
/*    */   public static int compare(String s1, String s2)
/*    */   {
/* 28 */     String m_s1 = null; String m_s2 = null;
/*    */     try
/*    */     {
/* 31 */       m_s1 = new String(s1.getBytes(__SERVER_ENCODE__), __ENCODE__);
/* 32 */       m_s2 = new String(s2.getBytes(__SERVER_ENCODE__), __ENCODE__);
/*    */     } catch (Exception ex) {
/* 34 */       return s1.compareTo(s2);
/*    */     }
/* 36 */     int res = chineseCompareTo(m_s1, m_s2);
/*    */ 
/* 38 */     return res;
/*    */   }
/*    */ 
/*    */   public static int getCharCode(String s)
/*    */   {
/* 43 */     if ((s == null) || (s.equals(""))) return -1;
/* 44 */     byte[] b = s.getBytes();
/* 45 */     int value = 0;
/*    */ 
/* 47 */     for (int i = 0; (i < b.length) && (i <= 2); i++) {
/* 48 */       value = value * 100 + b[i];
/*    */     }
/* 50 */     return value;
/*    */   }
/*    */ 
/*    */   public static int chineseCompareTo(String s1, String s2)
/*    */   {
/* 55 */     int len1 = s1.length();
/* 56 */     int len2 = s2.length();
/*    */ 
/* 58 */     int n = Math.min(len1, len2);
/* 59 */     for (int i = 0; i < n; i++) {
/* 60 */       int s1_code = getCharCode(s1.charAt(i) + "");
/* 61 */       int s2_code = getCharCode(s2.charAt(i) + "");
/* 62 */       if (s1_code != s2_code) return s1_code - s2_code;
/*    */     }
/* 64 */     return len1 - len2;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.CompareHelper
 * JD-Core Version:    0.6.0
 */