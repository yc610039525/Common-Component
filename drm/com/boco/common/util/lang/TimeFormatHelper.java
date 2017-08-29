/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class TimeFormatHelper
/*     */ {
/*     */   public static final String TIME_FORMAT_A = "yyyy-MM-dd HH:mm:ss";
/*     */   public static final String TIME_FORMAT_B = "yyyyMMddHHmmss";
/*     */   public static final String TIME_FORMAT_C = "yyyy-MM-dd HH:mm:ss:SSS";
/*     */   public static final String TIME_FORMAT_D = "yyyy-MM-dd HH:mm:ss.SSS";
/*     */   public static final String DATE_FORMAT = "yyyy-MM-dd";
/*     */   public static final String DATE_FORMAT_B = "yyMMdd";
/*     */   public static final String YEAR_FORMAT = "yyyy";
/*     */ 
/*     */   public static String getTimeFormat(String dateStr)
/*     */   {
/*  39 */     String timeFormat = "yyyy-MM-dd HH:mm:ss";
/*  40 */     if (dateStr != null) {
/*  41 */       String[] str1 = dateStr.split(":");
/*  42 */       String[] str2 = dateStr.split("-");
/*  43 */       boolean existDot = dateStr.contains(".");
/*  44 */       if ((str1.length == 3) && (str2.length == 3)) {
/*  45 */         if (!existDot)
/*  46 */           timeFormat = "yyyy-MM-dd HH:mm:ss";
/*     */         else {
/*  48 */           timeFormat = "yyyy-MM-dd HH:mm:ss.SSS";
/*     */         }
/*     */       }
/*  51 */       else if ((str1.length == 1) && (str2.length == 3)) {
/*  52 */         timeFormat = "yyyy-MM-dd";
/*     */       }
/*  54 */       else if (dateStr.length() == 14) {
/*  55 */         timeFormat = "yyyyMMddHHmmss";
/*     */       }
/*  57 */       else if (dateStr.length() == 6) {
/*  58 */         timeFormat = "yyMMdd";
/*     */       }
/*  60 */       else if (dateStr.length() == 4) {
/*  61 */         timeFormat = "yyyy";
/*     */       }
/*  63 */       else if (str1.length == 4) {
/*  64 */         timeFormat = "yyyy-MM-dd HH:mm:ss:SSS";
/*     */       }
/*     */     }
/*  67 */     System.out.println("timeFormat is:" + timeFormat);
/*  68 */     return timeFormat;
/*     */   }
/*     */ 
/*     */   public static Timestamp getFormatTimestamp(String dateStr) {
/*  72 */     String format = getTimeFormat(dateStr);
/*  73 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
/*  74 */     Date date = null;
/*     */     try {
/*  76 */       date = simpleDateFormat.parse(dateStr);
/*     */     } catch (Exception ex) {
/*  78 */       LogHome.getLog().error("日期格式转换错误", ex);
/*     */     }
/*  80 */     Timestamp timestamp = getFormatTimestamp(date, format);
/*  81 */     return timestamp;
/*     */   }
/*     */ 
/*     */   public static Timestamp getFormatTimestamp(Date date, String format) {
/*  85 */     Timestamp timestamp = null;
/*  86 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
/*  87 */     String resStr = simpleDateFormat.format(date);
/*  88 */     timestamp = Timestamp.valueOf(resStr);
/*  89 */     return timestamp;
/*     */   }
/*     */ 
/*     */   public static Timestamp getFormatTimestampNow() {
/*  93 */     return getFormatTimestamp(new Date(), "yyyy-MM-dd HH:mm:ss");
/*     */   }
/*     */ 
/*     */   public static String getFormatDate(Date date, String format) {
/*  97 */     String dateStr = null;
/*     */     try {
/*  99 */       if (date != null) {
/* 100 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
/* 101 */         dateStr = simpleDateFormat.format(date);
/*     */       }
/*     */     } catch (Exception ex) {
/* 104 */       LogHome.getLog().error("", ex);
/*     */     }
/* 106 */     return dateStr;
/*     */   }
/*     */ 
/*     */   public static Date convertDate(String dateStr, String format) {
/* 110 */     Date date = null;
/*     */     try {
/* 112 */       SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
/* 113 */       date = simpleDateFormat.parse(dateStr);
/*     */     } catch (Exception ex) {
/* 115 */       LogHome.getLog().error("", ex);
/*     */     }
/* 117 */     return date;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 121 */     String str = "1900-1-1 1:1:1";
/*     */     try {
/* 123 */       Timestamp timestamp = getFormatTimestamp(str);
/* 124 */       System.out.println(timestamp);
/*     */     } catch (Exception ex) {
/* 126 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.TimeFormatHelper
 * JD-Core Version:    0.6.0
 */