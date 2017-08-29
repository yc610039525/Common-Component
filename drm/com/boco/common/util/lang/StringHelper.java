/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class StringHelper
/*     */ {
/*     */   public static String[] trims(String[] strs)
/*     */   {
/*  27 */     String[] _strs = new String[strs.length];
/*  28 */     for (int i = 0; i < strs.length; i++) {
/*  29 */       _strs[i] = strs[i].trim();
/*     */     }
/*  31 */     return _strs;
/*     */   }
/*     */ 
/*     */   public static String nullToEmpty(String str) {
/*  35 */     return str == null ? "" : str;
/*     */   }
/*     */ 
/*     */   public static boolean isInteger(String str) {
/*     */     try {
/*  40 */       Integer.parseInt(str);
/*  41 */       return true; } catch (NumberFormatException ex) {
/*     */     }
/*  43 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isDouble(String str)
/*     */   {
/*     */     try {
/*  49 */       Double.parseDouble(str);
/*  50 */       return true; } catch (NumberFormatException ex) {
/*     */     }
/*  52 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isLong(String str)
/*     */   {
/*     */     try {
/*  58 */       Long.parseLong(str);
/*  59 */       return true; } catch (NumberFormatException ex) {
/*     */     }
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public static String StrReplace(String rStr, String rFix, String rRep)
/*     */   {
/*  72 */     int l = 0;
/*  73 */     int fl = rFix.length();
/*  74 */     String gRtnStr = rStr;
/*  75 */     String rtn = "";
/*  76 */     if (rStr != null) {
/*     */       while (true) {
/*  78 */         l = rStr.indexOf(rFix, l);
/*  79 */         if (l == -1) {
/*     */           break;
/*     */         }
/*  82 */         gRtnStr = rStr.substring(0, l) + rRep + rStr.substring(l + fl);
/*  83 */         l += rRep.length();
/*  84 */         rStr = gRtnStr;
/*     */       }
/*  86 */       rtn = gRtnStr.substring(0, gRtnStr.length());
/*     */     }
/*  88 */     return rtn;
/*     */   }
/*     */ 
/*     */   public static boolean isEqual(String str1, String str2) {
/*  92 */     if ((str1 == null) && (str2 == null)) {
/*  93 */       return true;
/*     */     }
/*  95 */     return str1 != null ? str1.equals(str2) : false;
/*     */   }
/*     */ 
/*     */   public static String getStrByLength(String strParameter, int limitLength)
/*     */   {
/* 105 */     String return_str = strParameter;
/* 106 */     int temp_int = 0;
/* 107 */     int cut_int = 0;
/* 108 */     byte[] b = strParameter.getBytes();
/*     */ 
/* 110 */     for (int i = 0; i < b.length; i++) {
/* 111 */       if (b[i] >= 0) {
/* 112 */         temp_int += 1;
/*     */       } else {
/* 114 */         temp_int += 2;
/* 115 */         i++;
/*     */       }
/* 117 */       cut_int++;
/*     */ 
/* 119 */       if (temp_int >= limitLength) {
/* 120 */         if ((temp_int % 2 != 0) && (b[(temp_int - 1)] < 0)) {
/* 121 */           cut_int--;
/*     */         }
/* 123 */         return_str = return_str.substring(0, cut_int);
/* 124 */         break;
/*     */       }
/*     */     }
/* 127 */     return return_str;
/*     */   }
/*     */ 
/*     */   public static String[] getSplit(String str, String delim)
/*     */   {
/* 132 */     String ss = str.trim();
/* 133 */     StringTokenizer st = new StringTokenizer(ss, delim);
/* 134 */     String[] SS = new String[st.countTokens()];
/*     */ 
/* 136 */     for (int i = 0; i < SS.length; i++) {
/* 137 */       SS[i] = st.nextToken();
/*     */     }
/* 139 */     return SS;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String s) {
/* 143 */     return (s == null) || (s.length() == 0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.StringHelper
 * JD-Core Version:    0.6.0
 */