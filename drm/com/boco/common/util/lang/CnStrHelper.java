/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class CnStrHelper
/*     */ {
/*  22 */   private static char[] chartable = { '啊', 33453, '擦', '搭', 34558, '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座' };
/*     */ 
/*  28 */   private static char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
/*     */ 
/*  33 */   private static int[] table = new int[27];
/*     */ 
/*     */   public static char Char2Alpha(char ch)
/*     */   {
/*  46 */     if ((ch >= 'a') && (ch <= 'z'))
/*  47 */       return (char)(ch - 'a' + 65);
/*  48 */     if ((ch >= 'A') && (ch <= 'Z')) {
/*  49 */       return ch;
/*     */     }
/*  51 */     int gb = gbValue(ch);
/*  52 */     if (gb < table[0]) {
/*  53 */       return ch;
/*     */     }
/*     */ 
/*  56 */     for (int i = 0; (i < 26) && 
/*  57 */       (!match(i, gb)); i++);
/*  61 */     if (i >= 26) {
/*  62 */       return ch;
/*     */     }
/*  64 */     return alphatable[i];
/*     */   }
/*     */ 
/*     */   public static String getAlpha(String SourceStr, int k)
/*     */   {
/*  72 */     String Result = "";
/*  73 */     int StrLength = SourceStr.length();
/*     */ 
/*  75 */     int kk = 0;
/*  76 */     if (StrLength > k)
/*  77 */       kk = k;
/*     */     else
/*  79 */       kk = StrLength;
/*     */     try {
/*  81 */       for (int i = 0; i < kk; i++)
/*  82 */         Result = Result + Char2Alpha(SourceStr.charAt(i));
/*     */     }
/*     */     catch (Exception e) {
/*  85 */       Result = "";
/*  86 */       LogHome.getLog().error("", e);
/*     */     }
/*     */ 
/*  89 */     return Result;
/*     */   }
/*     */ 
/*     */   public static boolean match(int i, int gb) {
/*  93 */     if (gb < table[i]) {
/*  94 */       return false;
/*     */     }
/*  96 */     int j = i + 1;
/*     */ 
/*  99 */     while ((j < 26) && (table[j] == table[i])) {
/* 100 */       j++;
/*     */     }
/* 102 */     if (j == 26) {
/* 103 */       return gb <= table[j];
/*     */     }
/* 105 */     return gb < table[j];
/*     */   }
/*     */ 
/*     */   public static int gbValue(char ch)
/*     */   {
/* 110 */     String str = new String();
/* 111 */     str = str + ch;
/*     */     try {
/* 113 */       byte[] bytes = str.getBytes("GB2312");
/* 114 */       if (bytes.length < 2)
/* 115 */         return 0;
/* 116 */       return (bytes[0] << 8 & 0xFF00) + (bytes[1] & 0xFF);
/*     */     } catch (Exception e) {
/* 118 */       LogHome.getLog().error("", e);
/* 119 */     }return 0;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     for (int i = 0; i < 27; i++)
/*  37 */       table[i] = gbValue(chartable[i]);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.CnStrHelper
 * JD-Core Version:    0.6.0
 */