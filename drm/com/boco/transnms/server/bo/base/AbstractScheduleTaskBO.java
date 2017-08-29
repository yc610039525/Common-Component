/*    */ package com.boco.transnms.server.bo.base;
/*    */ 
/*    */ import com.boco.common.util.lang.PeriodicTimer;
/*    */ import java.util.Date;
/*    */ import javax.management.Notification;
/*    */ 
/*    */ public abstract class AbstractScheduleTaskBO extends AbstractBO
/*    */   implements IScheduleTask
/*    */ {
/*    */   private Integer taskId;
/*    */   private Date scheduleTime;
/* 30 */   private String taskType = "TransNmsScheduleTask";
/*    */   private Object taskUserData;
/* 32 */   private String taskMessage = "";
/*    */ 
/* 34 */   private final PeriodicTimer periodicTimer = new PeriodicTimer();
/*    */ 
/*    */   public AbstractScheduleTaskBO() {
/*    */   }
/*    */ 
/*    */   public AbstractScheduleTaskBO(String boName) {
/* 40 */     super(boName);
/*    */   }
/*    */ 
/*    */   public String getTaskType() {
/* 44 */     return this.taskType;
/*    */   }
/*    */ 
/*    */   public Integer getTaskId() {
/* 48 */     return this.taskId;
/*    */   }
/*    */ 
/*    */   public Date getScheduleTime() {
/* 52 */     return this.scheduleTime;
/*    */   }
/*    */ 
/*    */   public String getTaskMessage() {
/* 56 */     return this.taskMessage;
/*    */   }
/*    */ 
/*    */   public Object getTaskUserData() {
/* 60 */     return this.taskUserData;
/*    */   }
/*    */ 
/*    */   public Date nextScheduleTime() {
/* 64 */     this.scheduleTime = this.periodicTimer.nextPeriodicTime();
/* 65 */     return this.scheduleTime;
/*    */   }
/*    */ 
/*    */   public void setTaskId(Integer taskId) {
/* 69 */     this.taskId = taskId;
/*    */   }
/*    */ 
/*    */   public void setTaskType(String type) {
/* 73 */     this.taskType = type;
/*    */   }
/*    */ 
/*    */   public void setScheduleTime(Date scheduleTime) {
/* 77 */     this.scheduleTime = scheduleTime;
/*    */   }
/*    */ 
/*    */   public void setTaskMessage(String message) {
/* 81 */     this.taskMessage = message;
/*    */   }
/*    */ 
/*    */   public void setTaskUserData(Object userData) {
/* 85 */     this.taskUserData = userData;
/*    */   }
/*    */ 
/*    */   public void setStartTime(String startTimeStr) {
/* 89 */     Date startTime = this.periodicTimer.setStartTime(startTimeStr);
/* 90 */     setScheduleTime(startTime);
/*    */   }
/*    */ 
/*    */   public void setPeriod(int period) {
/* 94 */     this.periodicTimer.setPeriod(period);
/*    */   }
/*    */ 
/*    */   public void setUnit(String unit) {
/* 98 */     this.periodicTimer.setUnit(unit);
/*    */   }
/*    */ 
/*    */   protected abstract void doScheduleTask(Notification paramNotification, Object paramObject)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.AbstractScheduleTaskBO
 * JD-Core Version:    0.6.0
 */