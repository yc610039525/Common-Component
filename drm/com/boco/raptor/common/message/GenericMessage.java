/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class GenericMessage
/*     */   implements IMessage
/*     */ {
/*     */   private String topicName;
/*  30 */   private long msgTimestamp = System.currentTimeMillis();
/*     */   private Serializable dataObject;
/*     */   private String sourceName;
/*     */   private String targetId;
/*     */   private Serializable attachData;
/*  40 */   private static int callClassLevelBase = 3;
/*     */ 
/*     */   public GenericMessage(String topicName)
/*     */   {
/*  47 */     this(topicName, "", null);
/*     */   }
/*     */ 
/*     */   public GenericMessage(String topicName, Serializable dataObject)
/*     */   {
/*  56 */     this(topicName, "", dataObject);
/*     */   }
/*     */ 
/*     */   public GenericMessage(String topicName, String sessionId, Serializable dataObject)
/*     */   {
/*  66 */     this.topicName = topicName;
/*  67 */     this.dataObject = dataObject;
/*  68 */     this.targetId = sessionId;
/*     */   }
/*     */ 
/*     */   public String getTopicName() {
/*  72 */     return this.topicName;
/*     */   }
/*     */ 
/*     */   public String getSourceName() {
/*  76 */     return this.sourceName;
/*     */   }
/*     */ 
/*     */   public String getTargetId() {
/*  80 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public long getTimestamp() {
/*  84 */     return this.msgTimestamp;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(long timestamp) {
/*  88 */     this.msgTimestamp = timestamp;
/*     */   }
/*     */ 
/*     */   public <T extends Serializable> T getDataObject() {
/*  92 */     return this.dataObject;
/*     */   }
/*     */ 
/*     */   public void setDataObject(Serializable dataObject) {
/*  96 */     this.dataObject = dataObject;
/*  97 */     if ((this.dataObject instanceof GenericDO)) {
/*  98 */       GenericDO dbo = (GenericDO)this.dataObject;
/*  99 */       this.dataObject = dbo.deepClone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSourceName(String sourceName) {
/* 104 */     this.sourceName = sourceName;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String sessionId) {
/* 108 */     this.targetId = sessionId;
/*     */   }
/*     */ 
/*     */   public <T extends Serializable> T getAttachData() {
/* 112 */     return this.attachData;
/*     */   }
/*     */ 
/*     */   public void setAttachData(Serializable attachData) {
/* 116 */     this.attachData = attachData;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 121 */     String msg = "";
/* 122 */     String time = TimeFormatHelper.getFormatDate(new Date(this.msgTimestamp), "yyyy-MM-dd HH:mm:ss");
/* 123 */     msg = msg + ": [time=" + time + ", ";
/* 124 */     msg = msg + " topicName=" + this.topicName + ", ";
/*     */ 
/* 126 */     if (getSourceName() != null) {
/* 127 */       msg = msg + "sourceName=" + getSourceName() + ", ";
/*     */     }
/*     */ 
/* 130 */     if (getTargetId() != null) {
/* 131 */       msg = msg + "targetId=" + getTargetId() + ", ";
/*     */     }
/*     */ 
/* 134 */     msg = msg + getDataObject() + "]";
/* 135 */     return msg;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.GenericMessage
 * JD-Core Version:    0.6.0
 */