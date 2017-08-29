/*     */ package com.boco.raptor.common.message.amq;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.IMessagePublisher;
/*     */ import com.boco.raptor.common.message.MsgChannelTypeEnum;
/*     */ import com.boco.raptor.common.message.MsgDestination;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import java.util.List;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MessageProducer;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.TopicSession;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AmqMsgPublisher
/*     */   implements IMessagePublisher
/*     */ {
/*     */   private AmqTopicPublisher jmsPublisher;
/*     */   private final String topicName;
/*  38 */   private boolean active = true;
/*     */ 
/*     */   public AmqMsgPublisher(String topicName) {
/*  41 */     this.topicName = topicName;
/*     */   }
/*     */ 
/*     */   public void initPublisher() throws Exception {
/*  45 */     this.jmsPublisher = new AmqTopicPublisher(this.topicName);
/*     */   }
/*     */ 
/*     */   public MsgServiceTypeEnum getMsgServiceType() {
/*  49 */     return MsgServiceTypeEnum.JMS;
/*     */   }
/*     */ 
/*     */   public String getTopicName() {
/*  53 */     return this.topicName;
/*     */   }
/*     */ 
/*     */   public boolean isActive() {
/*  57 */     return this.active;
/*     */   }
/*     */ 
/*     */   public void setActive(boolean active) {
/*  61 */     this.active = active;
/*     */   }
/*     */ 
/*     */   public void publishMessage(List<String> sessionIds, IMessage msg) throws Exception {
/*  65 */     if (isActive()) {
/*  66 */       String _sessionIds = "";
/*  67 */       for (int i = 0; i < sessionIds.size(); i++) {
/*  68 */         _sessionIds = _sessionIds + (String)sessionIds.get(i);
/*  69 */         _sessionIds = _sessionIds + ",";
/*     */       }
/*  71 */       this.jmsPublisher.publishObject(_sessionIds, msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void publishText(String message) throws Exception {
/*  76 */     this.jmsPublisher.publishText(message);
/*     */   }
/*     */   private static class AmqTopicPublisher extends MsgDestination {
/*     */     private MessageProducer topicPublisher;
/*     */ 
/*     */     public AmqTopicPublisher(String topicName) {
/*  83 */       this(topicName, "", false, false, 1);
/*     */     }
/*     */ 
/*     */     protected AmqTopicPublisher(String topicName, String clientId, boolean durable, boolean transacted, int acknowledgementMode)
/*     */     {
/*  89 */       super(topicName, clientId, durable, transacted, acknowledgementMode);
/*  90 */       initTopicPublisher();
/*     */     }
/*     */ 
/*     */     private void initTopicPublisher() {
/*     */       try {
/*  95 */         this.topicPublisher = ((TopicSession)getSession()).createProducer(getDestination());
/*     */       } catch (Exception ex) {
/*  97 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected MessageProducer getTopicPublisher() {
/* 102 */       return this.topicPublisher;
/*     */     }
/*     */ 
/*     */     public void publishText(String message) throws Exception {
/* 106 */       TextMessage textMessage = ((TopicSession)getSession()).createTextMessage();
/* 107 */       textMessage.clearBody();
/* 108 */       textMessage.setText(message);
/* 109 */       getTopicPublisher().send(textMessage);
/* 110 */       commit();
/*     */     }
/*     */ 
/*     */     public void publishObject(String sessionIds, IMessage message) throws Exception {
/* 114 */       ObjectMessage objectMessage = ((TopicSession)getSession()).createObjectMessage();
/* 115 */       objectMessage.clearBody();
/* 116 */       objectMessage.setJMSType(message.getTopicName());
/* 117 */       objectMessage.setObjectProperty("sessionId", sessionIds);
/* 118 */       objectMessage.setObject(message);
/* 119 */       getTopicPublisher().send(objectMessage);
/* 120 */       commit();
/*     */     }
/*     */ 
/*     */     public void onException(JMSException exp) {
/* 124 */       LogHome.getLog().error("JMS 接收消息异常： " + exp.getMessage());
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */       try {
/* 129 */         if (this.topicPublisher != null) {
/* 130 */           this.topicPublisher.close();
/*     */         }
/* 132 */         super.close();
/*     */       } catch (Exception ex) {
/* 134 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.amq.AmqMsgPublisher
 * JD-Core Version:    0.6.0
 */