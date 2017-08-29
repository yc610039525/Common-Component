/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class CmdStatInfo
/*     */ {
/*     */   private String actionName;
/*     */   private long count;
/*  31 */   private long maxActionTime = -1L;
/*  32 */   private long minActionTime = -1L;
/*     */   private Date maxActionDate;
/*     */   private Date minActionDate;
/*     */ 
/*     */   public void addStatInfo(String _actionName, long _actionTime)
/*     */   {
/*  37 */     this.count += 1L;
/*  38 */     this.actionName = _actionName;
/*  39 */     if ((this.maxActionTime == -1L) || (_actionTime > this.maxActionTime)) {
/*  40 */       this.maxActionTime = _actionTime;
/*  41 */       this.maxActionDate = TimeFormatHelper.getFormatTimestamp(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
/*     */     }
/*  43 */     if ((this.minActionTime == -1L) || (_actionTime < this.minActionTime)) {
/*  44 */       this.minActionTime = _actionTime;
/*  45 */       this.minActionDate = TimeFormatHelper.getFormatTimestamp(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getActionName() {
/*  50 */     return this.actionName;
/*     */   }
/*     */ 
/*     */   public void setActionName(String actionName) {
/*  54 */     this.actionName = actionName;
/*     */   }
/*     */ 
/*     */   public long getCount() {
/*  58 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void setCount(long count) {
/*  62 */     this.count = count;
/*     */   }
/*     */ 
/*     */   public long getMaxActionTime() {
/*  66 */     return this.maxActionTime;
/*     */   }
/*     */ 
/*     */   public void setMaxActionTime(long maxActionTime) {
/*  70 */     this.maxActionTime = maxActionTime;
/*     */   }
/*     */ 
/*     */   public long getMinActionTime() {
/*  74 */     return this.minActionTime;
/*     */   }
/*     */ 
/*     */   public void setMinActionTime(long minActionTime) {
/*  78 */     this.minActionTime = minActionTime;
/*     */   }
/*     */ 
/*     */   public Date getMaxActionDate() {
/*  82 */     return this.maxActionDate;
/*     */   }
/*     */ 
/*     */   public void setMaxActionDate(Date maxActionDate) {
/*  86 */     this.maxActionDate = maxActionDate;
/*     */   }
/*     */ 
/*     */   public Date getMinActionDate() {
/*  90 */     return this.minActionDate;
/*     */   }
/*     */ 
/*     */   public void setMinActionDate(Date minActionDate) {
/*  94 */     this.minActionDate = minActionDate;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  98 */     String str = "CmdStatInfo[";
/*  99 */     str = str + "actionName=" + getActionName();
/* 100 */     str = str + ", count=" + getCount();
/* 101 */     str = str + ", maxActionTime=" + getMaxActionTime();
/* 102 */     str = str + ", maxActionDate=" + getMaxActionDate();
/* 103 */     str = str + ", minActionTime=" + getMinActionTime();
/* 104 */     str = str + ", minActionDate=" + getMinActionDate();
/* 105 */     str = str + "]";
/* 106 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.CmdStatInfo
 * JD-Core Version:    0.6.0
 */