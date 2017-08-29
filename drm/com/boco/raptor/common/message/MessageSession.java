/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ public class MessageSession
/*    */ {
/*    */   private final IMessageFilter msgFilter;
/*    */   private final IMessagePublisher publisher;
/*    */   private final String sessionId;
/*    */   private final String topicName;
/* 25 */   private boolean active = true;
/*    */ 
/*    */   public MessageSession(String sessionId, String topicName, IMessagePublisher publisher, IMessageFilter msgFilter) {
/* 28 */     this.sessionId = sessionId;
/* 29 */     this.publisher = publisher;
/* 30 */     this.msgFilter = msgFilter;
/* 31 */     this.topicName = topicName;
/*    */   }
/*    */ 
/*    */   public IMessageFilter getMsgFilter() {
/* 35 */     return this.msgFilter;
/*    */   }
/*    */ 
/*    */   public IMessagePublisher getPublisher() {
/* 39 */     return this.publisher;
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 43 */     return this.sessionId;
/*    */   }
/*    */ 
/*    */   public String getTopicName() {
/* 47 */     return this.topicName;
/*    */   }
/*    */ 
/*    */   public void setActive(boolean active) {
/* 51 */     this.active = active;
/*    */   }
/*    */ 
/*    */   public boolean isActive() {
/* 55 */     return this.active;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MessageSession
 * JD-Core Version:    0.6.0
 */