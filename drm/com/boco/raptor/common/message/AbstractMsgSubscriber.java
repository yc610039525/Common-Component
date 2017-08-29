/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public abstract class AbstractMsgSubscriber
/*    */   implements IMessageSubscriber
/*    */ {
/* 24 */   private final List<IMessageListener> listeners = new Vector();
/*    */   private String topicName;
/*    */   private String sessionId;
/*    */ 
/*    */   public void addMsgListener(IMessageListener msgListener)
/*    */   {
/* 32 */     this.listeners.add(msgListener);
/*    */   }
/*    */ 
/*    */   public void removeMsgListener(IMessageListener msgListener) {
/* 36 */     this.listeners.remove(msgListener);
/*    */   }
/*    */ 
/*    */   protected List<IMessageListener> getMsgListeners() {
/* 40 */     return this.listeners;
/*    */   }
/*    */ 
/*    */   public String getTopicName() {
/* 44 */     return this.topicName;
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 48 */     return this.sessionId;
/*    */   }
/*    */ 
/*    */   public void setTopicName(String topicName) {
/* 52 */     this.topicName = topicName;
/*    */   }
/*    */ 
/*    */   protected void setSessionId(String sessionId) {
/* 56 */     this.sessionId = sessionId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.AbstractMsgSubscriber
 * JD-Core Version:    0.6.0
 */