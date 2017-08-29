/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class PeriodicTimer
/*     */ {
/*     */   public static final String PERIOD_UNIT_YEAR = "year";
/*     */   public static final String PERIOD_UNIT_MONTH = "month";
/*     */   public static final String PERIOD_UNIT_DAY = "day";
/*     */   public static final String PERIOD_UNIT_HOUR = "hour";
/*     */   public static final String PERIOD_UNIT_MINUTE = "min";
/*     */   public static final String PERIOD_UNIT_SECOND = "sec";
/*     */   public static final String START_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*     */   private int period;
/*     */   private String unit;
/*     */   private Date curTime;
/*     */   private Date startTime;
/*     */ 
/*     */   public int getPeriod()
/*     */   {
/*  44 */     return this.period;
/*     */   }
/*     */ 
/*     */   public String getUnit() {
/*  48 */     return this.unit;
/*     */   }
/*     */ 
/*     */   public Date getStartTime() {
/*  52 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public void setPeriod(int period) {
/*  56 */     this.period = period;
/*     */   }
/*     */ 
/*     */   public Date setStartTime(String startTimeStr) {
/*  60 */     return setStartTime(TimeFormatHelper.convertDate(startTimeStr, "yyyy-MM-dd HH:mm:ss"));
/*     */   }
/*     */ 
/*     */   public void setUnit(String unit) {
/*  64 */     this.unit = unit;
/*     */   }
/*     */ 
/*     */   public Date setStartTime(Date startTime) {
/*  68 */     this.startTime = startTime;
/*  69 */     this.curTime = startTime;
/*  70 */     if ((this.unit != null) && (this.period > 0)) {
/*  71 */       this.curTime = adjustStartTime(startTime, this.unit, this.period);
/*     */     }
/*  73 */     return this.curTime;
/*     */   }
/*     */ 
/*     */   public Date nextPeriodicTime() {
/*  77 */     this.curTime = nextPeriodicTime(this.curTime, this.unit, this.period);
/*  78 */     return this.curTime;
/*     */   }
/*     */ 
/*     */   private static Date nextPeriodicTime(Date startTime, String unit, int period)
/*     */   {
/*  83 */     Calendar calendar = Calendar.getInstance();
/*  84 */     calendar.setTimeInMillis(startTime.getTime());
/*     */     Date nextPeriodicTime;
/*     */     Date nextPeriodicTime;
/*  85 */     if ("year".equals(unit)) {
/*  86 */       calendar.add(1, period);
/*  87 */       nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */     }
/*     */     else
/*     */     {
/*     */       Date nextPeriodicTime;
/*  88 */       if ("month".equals(unit)) {
/*  89 */         calendar.add(2, period);
/*  90 */         nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */       }
/*     */       else
/*     */       {
/*     */         Date nextPeriodicTime;
/*  91 */         if ("day".equals(unit)) {
/*  92 */           calendar.add(6, period);
/*  93 */           nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */         }
/*     */         else
/*     */         {
/*     */           Date nextPeriodicTime;
/*  94 */           if ("hour".equals(unit)) {
/*  95 */             calendar.add(10, period);
/*  96 */             nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */           }
/*     */           else
/*     */           {
/*     */             Date nextPeriodicTime;
/*  97 */             if ("min".equals(unit)) {
/*  98 */               calendar.add(12, period);
/*  99 */               nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */             }
/*     */             else
/*     */             {
/*     */               Date nextPeriodicTime;
/* 100 */               if ("sec".equals(unit)) {
/* 101 */                 calendar.add(13, period);
/* 102 */                 nextPeriodicTime = new Date(calendar.getTimeInMillis());
/*     */               } else {
/* 104 */                 nextPeriodicTime = null;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 106 */     return nextPeriodicTime;
/*     */   }
/*     */ 
/*     */   private static Date adjustStartTime(Date startTime, String unit, int period) {
/* 110 */     Date adjustedTime = startTime;
/* 111 */     if (startTime.getTime() < System.currentTimeMillis()) {
/* 112 */       if (("year".equals(unit)) || ("month".equals(unit))) {
/*     */         do
/* 114 */           adjustedTime = nextPeriodicTime(adjustedTime, unit, period);
/* 115 */         while (adjustedTime.getTime() <= System.currentTimeMillis());
/*     */       }
/*     */       else
/*     */       {
/* 120 */         Date nextTime = nextPeriodicTime(startTime, unit, period);
/* 121 */         long periodicMillSec = nextTime.getTime() - startTime.getTime();
/* 122 */         long curMillSec = System.currentTimeMillis() - startTime.getTime();
/* 123 */         long interval = curMillSec / periodicMillSec;
/* 124 */         adjustedTime = new Date(startTime.getTime() + (interval + 1L) * periodicMillSec);
/*     */       }
/*     */     }
/* 127 */     return adjustedTime;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.PeriodicTimer
 * JD-Core Version:    0.6.0
 */