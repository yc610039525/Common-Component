/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.io.ObjZipBufOutputStream;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class MessageBatchSender
/*    */ {
/* 13 */   private String senderName = "MessageBatchSender";
/*    */ 
/* 15 */   private Timer timer = null;
/*    */ 
/* 17 */   private ArrayList msgs = null;
/*    */ 
/* 19 */   private String destName = "Q_AM_AMRTU2AM";
/*    */ 
/* 21 */   private int batchCount = 10;
/*    */ 
/* 23 */   private int batchTime = 1000;
/*    */ 
/* 25 */   public static String lock = "LOCK";
/*    */ 
/*    */   public MessageBatchSender(String senderName, String destName, int batchCount, int batchTime)
/*    */   {
/* 29 */     this.senderName = senderName;
/* 30 */     this.destName = destName;
/*    */ 
/* 32 */     if (batchCount > 0) {
/* 33 */       this.batchCount = batchCount;
/*    */     }
/*    */ 
/* 36 */     if (batchTime > 500) {
/* 37 */       this.batchTime = batchTime;
/*    */     }
/* 39 */     init();
/*    */   }
/*    */ 
/*    */   private void init()
/*    */   {
/* 46 */     this.msgs = new ArrayList();
/* 47 */     this.timer = new Timer(this.senderName);
/* 48 */     TimerTask task = new TimerTask()
/*    */     {
/*    */       public void run() {
/*    */         try {
/* 52 */           synchronized (MessageBatchSender.lock) {
/* 53 */             if (MessageBatchSender.this.msgs.size() > 0)
/* 54 */               MessageBatchSender.this.sendMessage();
/*    */           }
/*    */         }
/*    */         catch (Exception ex) {
/* 58 */           LogHome.getLog().error("发送器(" + MessageBatchSender.this.senderName + ")定时批量发送消息失败：" + ex);
/*    */         }
/*    */       }
/*    */     };
/* 64 */     this.timer.schedule(task, 10L, this.batchTime);
/*    */   }
/*    */ 
/*    */   public void sendMessage(GenericDO dbo)
/*    */   {
/*    */     try
/*    */     {
/* 76 */       synchronized (lock) {
/* 77 */         this.msgs.add(dbo);
/* 78 */         if (this.msgs.size() >= this.batchCount)
/* 79 */           sendMessage();
/*    */       }
/*    */     }
/*    */     catch (Exception ex) {
/* 83 */       LogHome.getLog().error("发送器(" + this.senderName + ")发送消息失败：" + ex);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void sendMessage()
/*    */   {
/*    */     try
/*    */     {
/* 92 */       ArrayList list = null;
/*    */ 
/* 94 */       synchronized (lock) {
/* 95 */         list = this.msgs;
/* 96 */         if (LogHome.getLog().isDebugEnabled()) {
/* 97 */           LogHome.getLog().debug("发送器(" + this.senderName + ")批量发送消息开始，消息个数(" + list.size() + ")");
/*    */         }
/*    */ 
/* 101 */         this.msgs = new ArrayList();
/*    */       }
/* 103 */       if (list.size() > 0) {
/* 104 */         GenericMessage msg = null;
/* 105 */         ObjZipBufOutputStream outputStream = new ObjZipBufOutputStream();
/* 106 */         outputStream.writeObject(list);
/* 107 */         outputStream.close();
/* 108 */         byte[] byteArray = outputStream.getBuf();
/* 109 */         msg = new GenericMessage(this.destName, byteArray);
/* 110 */         msg.setSourceName(this.senderName);
/* 111 */         MsgBusManager.getInstance().sendMessage(msg);
/*    */       }
/* 113 */       else if (LogHome.getLog().isDebugEnabled()) {
/* 114 */         LogHome.getLog().debug("发送器(" + this.senderName + ")批量发送消息个数为0");
/*    */       }
/*    */ 
/* 117 */       if (LogHome.getLog().isDebugEnabled()) {
/* 118 */         LogHome.getLog().debug("发送器(" + this.senderName + ")批量发送消息完成，消息个数(" + list.size() + ")");
/*    */       }
/*    */     }
/*    */     catch (Exception ex)
/*    */     {
/* 123 */       LogHome.getLog().error("发送器(" + this.senderName + ")发送消息失败：" + ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MessageBatchSender
 * JD-Core Version:    0.6.0
 */