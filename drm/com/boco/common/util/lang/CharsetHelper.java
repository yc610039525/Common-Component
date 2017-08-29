/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Set;
/*    */ import java.util.SortedMap;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class CharsetHelper
/*    */ {
/*    */   public static String encode(String charset, String str)
/*    */   {
/* 31 */     if ((str == null) || (str.trim().length() == 0) || (charset == null) || (charset.trim().length() == 0))
/*    */     {
/* 33 */       return str;
/*    */     }
/*    */ 
/* 36 */     String encode = str;
/*    */     try {
/* 38 */       byte[] bytes = str.getBytes();
/* 39 */       encode = new String(bytes, charset);
/*    */     } catch (Exception ex) {
/* 41 */       LogHome.getLog().error("", ex);
/*    */     }
/*    */ 
/* 44 */     return encode;
/*    */   }
/*    */ 
/*    */   public static String decode(String charset, String str) {
/* 48 */     if ((str == null) || (str.trim().length() == 0) || (charset == null) || (charset.trim().length() == 0))
/*    */     {
/* 50 */       return str;
/*    */     }
/*    */ 
/* 53 */     String decode = str;
/*    */     try {
/* 55 */       byte[] bytes = str.getBytes(charset);
/* 56 */       decode = new String(bytes);
/*    */     } catch (Exception ex) {
/* 58 */       LogHome.getLog().error("", ex);
/*    */     }
/*    */ 
/* 61 */     return decode;
/*    */   }
/*    */ 
/*    */   public static String getUnicodeStr(String local) {
/* 65 */     char[] localChars = local.toCharArray();
/* 66 */     StringBuffer unicode = new StringBuffer();
/* 67 */     for (int i = 0; i < localChars.length; i++) {
/* 68 */       int shortLocal = localChars[i];
/* 69 */       String uniStr = Integer.toHexString(shortLocal).toLowerCase();
/* 70 */       for (int j = uniStr.length(); j < 4; j++) {
/* 71 */         uniStr = "0" + uniStr;
/*    */       }
/* 73 */       unicode.append("\\u" + uniStr);
/*    */     }
/* 75 */     return unicode.toString();
/*    */   }
/*    */ 
/*    */   public static String[] getSupportCharsetNames() {
/* 79 */     SortedMap charsets = Charset.availableCharsets();
/* 80 */     String[] charsetNames = new String[charsets.size()];
/* 81 */     charsets.keySet().toArray(charsetNames);
/* 82 */     return charsetNames;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.CharsetHelper
 * JD-Core Version:    0.6.0
 */