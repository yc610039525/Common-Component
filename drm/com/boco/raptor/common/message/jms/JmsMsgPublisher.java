/*     */ package com.boco.raptor.common.message.jms;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.IMessagePublisher;
/*     */ import com.boco.raptor.common.message.MsgPubSubFactory;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import java.util.List;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.TopicPublisher;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.Context;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JmsMsgPublisher
/*     */   implements IMessagePublisher
/*     */ {
/*     */   private JmsTopicPublisher jmsPublisher;
/*     */   private final String topicName;
/*  36 */   private boolean active = true;
/*     */ 
/*     */   public JmsMsgPublisher(String topicName) {
/*  39 */     this.topicName = topicName;
/*     */   }
/*     */ 
/*     */   public void initPublisher() throws Exception {
/*  43 */     this.jmsPublisher = new JmsTopicPublisher(MsgPubSubFactory.getInstance().getJmsInitialContext(), "ConnectionFactory", this.topicName);
/*     */   }
/*     */ 
/*     */   public MsgServiceTypeEnum getMsgServiceType()
/*     */   {
/*  48 */     return MsgServiceTypeEnum.JMS;
/*     */   }
/*     */ 
/*     */   public String getTopicName() {
/*  52 */     return this.topicName;
/*     */   }
/*     */ 
/*     */   public boolean isActive() {
/*  56 */     return this.active;
/*     */   }
/*     */ 
/*     */   public void setActive(boolean active) {
/*  60 */     this.active = active;
/*     */   }
/*     */ 
/*     */   public void publishMessage(List<String> sessionIds, IMessage msg) throws Exception {
/*  64 */     if (isActive()) {
/*  65 */       String _sessionIds = "";
/*  66 */       for (int i = 0; i < sessionIds.size(); i++) {
/*  67 */         _sessionIds = _sessionIds + (String)sessionIds.get(i);
/*  68 */         _sessionIds = _sessionIds + ",";
/*     */       }
/*  70 */       this.jmsPublisher.publishObject(_sessionIds, msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class JmsTopicPublisher extends AbstractJmsTopic {
/*     */     private TopicPublisher topicPublisher;
/*     */ 
/*     */     public JmsTopicPublisher(Context context, String topicConnectionFactoryName, String topicName) {
/*  78 */       this(context, topicConnectionFactoryName, topicName, "", false, false, 1);
/*     */     }
/*     */ 
/*     */     protected JmsTopicPublisher(Context context, String topicConnectionFactoryName, String topicName, String clientId, boolean durable, boolean transacted, int acknowledgementMode)
/*     */     {
/*  84 */       super(topicConnectionFactoryName, topicName, clientId, durable, transacted, acknowledgementMode);
/*  85 */       initTopicPublisher();
/*     */     }
/*     */ 
/*     */     private void initTopicPublisher() {
/*     */       try {
/*  90 */         this.topicPublisher = ((TopicSession)getSession()).createPublisher(getTopic());
/*     */       } catch (Exception ex) {
/*  92 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected TopicPublisher getTopicPublisher() {
/*  97 */       return this.topicPublisher;
/*     */     }
/*     */ 
/*     */     public void publishText(String message) throws Exception {
/* 101 */       TextMessage textMessage = ((TopicSession)getSession()).createTextMessage();
/* 102 */       textMessage.clearBody();
/* 103 */       textMessage.setText(message);
/* 104 */       getTopicPublisher().publish(textMessage);
/* 105 */       commit();
/*     */     }
/*     */ 
/*     */     public void publishObject(String sessionIds, IMessage message) throws Exception {
/* 109 */       ObjectMessage objectMessage = ((TopicSession)getSession()).createObjectMessage();
/* 110 */       objectMessage.clearBody();
/* 111 */       objectMessage.setJMSType(message.getTopicName());
/* 112 */       objectMessage.setObjectProperty("sessionId", sessionIds);
/* 113 */       objectMessage.setObject(message);
/* 114 */       getTopicPublisher().publish(objectMessage);
/* 115 */       commit();
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */       try {
/* 120 */         if (this.topicPublisher != null) {
/* 121 */           this.topicPublisher.close();
/*     */         }
/* 123 */         super.close();
/*     */       } catch (Exception ex) {
/* 125 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.jms.JmsMsgPublisher
 * JD-Core Version:    0.6.0
 */