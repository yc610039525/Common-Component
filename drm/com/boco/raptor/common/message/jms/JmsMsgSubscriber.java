/*     */ package com.boco.raptor.common.message.jms;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.common.message.AbstractMsgSubscriber;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.IMessageFilter;
/*     */ import com.boco.raptor.common.message.IMessageListener;
/*     */ import com.boco.raptor.common.message.IMessageSubscriber;
/*     */ import com.boco.raptor.common.message.MsgPubSubFactory;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MapMessage;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.StreamMessage;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.jms.TopicSubscriber;
/*     */ import javax.naming.Context;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JmsMsgSubscriber extends AbstractMsgSubscriber
/*     */   implements IMessageSubscriber
/*     */ {
/*     */   private JmsTopicSubscriber subscriber;
/*     */ 
/*     */   public JmsMsgSubscriber()
/*     */   {
/*     */   }
/*     */ 
/*     */   public JmsMsgSubscriber(String topicName)
/*     */   {
/*  55 */     super.setTopicName(topicName);
/*     */   }
/*     */ 
/*     */   public void createSession(IMessageFilter filter) {
/*     */     try {
/*  60 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  61 */       String sessionId = getMessageServiceBO().addSession(context, getMsgServieType(), getTopicName(), filter);
/*  62 */       createJmsSession(sessionId);
/*  63 */       super.setSessionId(sessionId);
/*     */     } catch (Exception ex) {
/*  65 */       closeSession();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createSession(String filterName, Serializable filterPara) {
/*     */     try {
/*  71 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  72 */       String sessionId = getMessageServiceBO().addSession(context, getMsgServieType(), getTopicName(), filterName, filterPara);
/*  73 */       createJmsSession(sessionId);
/*     */     } catch (Exception ex) {
/*  75 */       closeSession();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createJmsSession(String sessionId) {
/*  80 */     String selector = "sessionId like '%" + sessionId + "%'";
/*  81 */     this.subscriber = new JmsTopicSubscriber(MsgPubSubFactory.getInstance().getJmsInitialContext(), "ConnectionFactory", getTopicName(), selector, true);
/*     */ 
/*  83 */     super.setSessionId(sessionId);
/*     */   }
/*     */ 
/*     */   public void closeSession() {
/*  87 */     String sessionId = getSessionId();
/*  88 */     setSessionId(null);
/*  89 */     if (sessionId != null) {
/*  90 */       if (this.subscriber != null) {
/*  91 */         this.subscriber.close();
/*     */       }
/*     */ 
/*  94 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  95 */       getMessageServiceBO().delSession(context, sessionId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetFilterPara(Object filterPara) {
/* 100 */     IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */     try {
/* 102 */       getMessageServiceBO().modifyFilterPara(context, super.getSessionId(), filterPara);
/*     */     } catch (Exception ex) {
/* 104 */       LogHome.getLog().error(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MsgServiceTypeEnum getMsgServieType() {
/* 109 */     return MsgServiceTypeEnum.JMS;
/*     */   }
/*     */ 
/*     */   public void setSessionActive(boolean isActive) {
/* 113 */     IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */     try {
/* 115 */       getMessageServiceBO().modifySessionActive(context, super.getSessionId(), new Boolean(isActive));
/*     */     } catch (Exception ex) {
/* 117 */       LogHome.getLog().error(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static IMessageServiceBO getMessageServiceBO() {
/* 122 */     return (IMessageServiceBO)BoHomeFactory.getInstance().getBO(IMessageServiceBO.class);
/*     */   }
/*     */   private class JmsTopicSubscriber extends AbstractJmsTopic implements MessageListener {
/*     */     private TopicSubscriber topicSubscriber;
/*     */     private String durableName;
/*     */ 
/* 130 */     public JmsTopicSubscriber(Context context, String topicConnectionFactoryName, String topicName, String selector) { this(context, topicConnectionFactoryName, topicName, "", false, "", false, 1, selector, true); }
/*     */ 
/*     */     public JmsTopicSubscriber(Context context, String topicConnectionFactoryName, String topicName, String selector, boolean isInitTopic)
/*     */     {
/* 134 */       this(context, topicConnectionFactoryName, topicName, "", false, "", false, 1, selector, isInitTopic);
/*     */     }
/*     */ 
/*     */     protected JmsTopicSubscriber(Context context, String topicConnectionFactoryName, String topicName, String clientId, boolean durable, String durableName, boolean transacted, int acknowledgementMode, String selector, boolean isInitTopic)
/*     */     {
/* 141 */       super(topicConnectionFactoryName, topicName, clientId, durable, transacted, acknowledgementMode);
/* 142 */       this.durableName = durableName;
/* 143 */       if ((context != null) && (isInitTopic)) {
/* 144 */         initTopicSubscriber(selector);
/* 145 */         LogHome.getLog().info("TopicName=" + topicName + " 初始化注册成功 ！");
/*     */       }
/*     */     }
/*     */ 
/*     */     public void initTopicSubscriber(String selector) {
/*     */       try {
/* 151 */         if (isDurable())
/* 152 */           this.topicSubscriber = ((TopicSession)getSession()).createDurableSubscriber(getTopic(), this.durableName, selector, false);
/*     */         else {
/* 154 */           this.topicSubscriber = ((TopicSession)getSession()).createSubscriber(getTopic(), selector, false);
/*     */         }
/* 156 */         this.topicSubscriber.setMessageListener(this);
/* 157 */         getTopicConnection().start();
/*     */       } catch (Exception ex) {
/* 159 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected TopicSubscriber getTopicSubscriber() {
/* 164 */       return this.topicSubscriber;
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */       try {
/* 169 */         if (this.topicSubscriber != null) {
/* 170 */           this.topicSubscriber.close();
/*     */         }
/* 172 */         super.close();
/*     */       } catch (Exception ex) {
/* 174 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void onMessage(Message message) {
/* 179 */       if ((message instanceof BytesMessage))
/* 180 */         doByteMessage((BytesMessage)message);
/* 181 */       else if ((message instanceof MapMessage))
/* 182 */         doMapMessage((MapMessage)message);
/* 183 */       else if ((message instanceof ObjectMessage))
/* 184 */         doObjectMessage((ObjectMessage)message);
/* 185 */       else if ((message instanceof StreamMessage))
/* 186 */         doStreamMessage((StreamMessage)message);
/* 187 */       else if ((message instanceof TextMessage)) {
/* 188 */         doTextMessage((TextMessage)message);
/*     */       }
/*     */       try
/*     */       {
/* 192 */         commit();
/*     */       } catch (Exception ex) {
/* 194 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void doByteMessage(BytesMessage message) {
/*     */     }
/*     */ 
/*     */     protected void doMapMessage(MapMessage message) {
/*     */     }
/*     */ 
/*     */     protected void doObjectMessage(ObjectMessage message) {
/* 205 */       IMessage msg = parseMessage(message);
/* 206 */       if ((msg != null) && (JmsMsgSubscriber.this.getSessionId() != null))
/* 207 */         for (IMessageListener listener : JmsMsgSubscriber.this.getMsgListeners())
/*     */           try {
/* 209 */             List msgs = new ArrayList();
/* 210 */             msgs.add(msg);
/* 211 */             listener.notify(msgs);
/*     */           } catch (Throwable ex) {
/* 213 */             LogHome.getLog().error("", ex);
/*     */           }
/*     */     }
/*     */ 
/*     */     private IMessage parseMessage(ObjectMessage message)
/*     */     {
/* 220 */       IMessage msg = null;
/*     */       try {
/* 222 */         if ((message.getObject() instanceof IMessage))
/* 223 */           msg = (IMessage)message.getObject();
/*     */       }
/*     */       catch (JMSException ex) {
/* 226 */         LogHome.getLog().error("", ex);
/*     */       }
/* 228 */       return msg;
/*     */     }
/*     */ 
/*     */     protected void doStreamMessage(StreamMessage message)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void doTextMessage(TextMessage message)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.jms.JmsMsgSubscriber
 * JD-Core Version:    0.6.0
 */