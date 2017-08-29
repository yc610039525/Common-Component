/*     */ package com.boco.raptor.common.message.amq;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.common.message.AbstractMsgSubscriber;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.IMessageFilter;
/*     */ import com.boco.raptor.common.message.IMessageListener;
/*     */ import com.boco.raptor.common.message.IMessageSubscriber;
/*     */ import com.boco.raptor.common.message.MsgChannelTypeEnum;
/*     */ import com.boco.raptor.common.message.MsgDestination;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MapMessage;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.StreamMessage;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicSession;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AmqMsgSubscriber extends AbstractMsgSubscriber
/*     */   implements IMessageSubscriber
/*     */ {
/*     */   private AmqTopicSubscriber subscriber;
/*     */ 
/*     */   public AmqMsgSubscriber()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AmqMsgSubscriber(String topicName)
/*     */   {
/*  57 */     super.setTopicName(topicName);
/*     */   }
/*     */ 
/*     */   public void createSession(IMessageFilter filter) {
/*     */     try {
/*  62 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  63 */       String sessionId = getMessageServiceBO().addSession(context, getMsgServieType(), getTopicName(), filter);
/*  64 */       createJmsSession(sessionId);
/*  65 */       super.setSessionId(sessionId);
/*     */     } catch (Exception ex) {
/*  67 */       closeSession();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createSession(String filterName, Serializable filterPara) {
/*     */     try {
/*  73 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  74 */       String sessionId = getMessageServiceBO().addSession(context, getMsgServieType(), getTopicName(), filterName, filterPara);
/*  75 */       createJmsSession(sessionId);
/*     */     } catch (Exception ex) {
/*  77 */       closeSession();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createJmsSession(String sessionId) {
/*  82 */     String selector = "sessionId like '%" + sessionId + "%'";
/*  83 */     this.subscriber = new AmqTopicSubscriber(getTopicName(), selector, true);
/*  84 */     super.setSessionId(sessionId);
/*     */   }
/*     */ 
/*     */   public void closeSession() {
/*  88 */     String sessionId = getSessionId();
/*  89 */     setSessionId(null);
/*  90 */     if (sessionId != null) {
/*  91 */       if (this.subscriber != null) {
/*  92 */         this.subscriber.close();
/*     */       }
/*     */ 
/*  95 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  96 */       getMessageServiceBO().delSession(context, sessionId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetFilterPara(Object filterPara) {
/* 101 */     IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */     try {
/* 103 */       getMessageServiceBO().modifyFilterPara(context, super.getSessionId(), filterPara);
/*     */     } catch (Exception ex) {
/* 105 */       LogHome.getLog().error(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MsgServiceTypeEnum getMsgServieType() {
/* 110 */     return MsgServiceTypeEnum.JMS;
/*     */   }
/*     */ 
/*     */   public void setSessionActive(boolean isActive) {
/* 114 */     IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */     try {
/* 116 */       getMessageServiceBO().modifySessionActive(context, super.getSessionId(), new Boolean(isActive));
/*     */     } catch (Exception ex) {
/* 118 */       LogHome.getLog().error(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static IMessageServiceBO getMessageServiceBO() {
/* 123 */     return (IMessageServiceBO)BoHomeFactory.getInstance().getBO(IMessageServiceBO.class);
/*     */   }
/*     */   private class AmqTopicSubscriber extends MsgDestination implements MessageListener {
/*     */     private MessageConsumer topicConsumer;
/*     */     private String durableName;
/*     */ 
/* 131 */     public AmqTopicSubscriber(String topicName, String selector) { this(topicName, "", false, "", false, 1, selector, true); }
/*     */ 
/*     */     public AmqTopicSubscriber(String topicName, String selector, boolean isInitTopic)
/*     */     {
/* 135 */       this(topicName, "", false, "", false, 1, selector, isInitTopic);
/*     */     }
/*     */ 
/*     */     protected AmqTopicSubscriber(String topicName, String clientId, boolean durable, String durableName, boolean transacted, int acknowledgementMode, String selector, boolean isInitTopic)
/*     */     {
/* 142 */       super(topicName, clientId, durable, transacted, acknowledgementMode);
/* 143 */       this.durableName = durableName;
/* 144 */       if (isInitTopic) {
/* 145 */         initTopicSubscriber(selector);
/* 146 */         LogHome.getLog().info("TopicName=" + topicName + " 初始化注册成功 ！");
/*     */       }
/*     */     }
/*     */ 
/*     */     public void initTopicSubscriber(String selector) {
/*     */       try {
/* 152 */         if (isDurable())
/* 153 */           this.topicConsumer = ((TopicSession)getSession()).createDurableSubscriber((Topic)getDestination(), this.durableName, selector, false);
/*     */         else {
/* 155 */           this.topicConsumer = ((TopicSession)getSession()).createConsumer(getDestination(), selector);
/*     */         }
/* 157 */         this.topicConsumer.setMessageListener(this);
/* 158 */         getConnection().start();
/*     */       } catch (Exception ex) {
/* 160 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected MessageConsumer getTopicSubscriber() {
/* 165 */       return this.topicConsumer;
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */       try {
/* 170 */         if (this.topicConsumer != null) {
/* 171 */           this.topicConsumer.close();
/*     */         }
/* 173 */         super.close();
/*     */       } catch (Exception ex) {
/* 175 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void onMessage(Message message) {
/* 180 */       if ((message instanceof BytesMessage))
/* 181 */         doByteMessage((BytesMessage)message);
/* 182 */       else if ((message instanceof MapMessage))
/* 183 */         doMapMessage((MapMessage)message);
/* 184 */       else if ((message instanceof ObjectMessage))
/* 185 */         doObjectMessage((ObjectMessage)message);
/* 186 */       else if ((message instanceof StreamMessage))
/* 187 */         doStreamMessage((StreamMessage)message);
/* 188 */       else if ((message instanceof TextMessage)) {
/* 189 */         doTextMessage((TextMessage)message);
/*     */       }
/*     */       try
/*     */       {
/* 193 */         commit();
/*     */       } catch (Exception ex) {
/* 195 */         LogHome.getLog().error("", ex);
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
/* 206 */       IMessage msg = parseMessage(message);
/* 207 */       if ((msg != null) && (AmqMsgSubscriber.this.getSessionId() != null))
/* 208 */         for (IMessageListener listener : AmqMsgSubscriber.this.getMsgListeners())
/*     */           try {
/* 210 */             List msgs = new ArrayList();
/* 211 */             msgs.add(msg);
/* 212 */             listener.notify(msgs);
/*     */           } catch (Throwable ex) {
/* 214 */             LogHome.getLog().error("", ex);
/*     */           }
/*     */     }
/*     */ 
/*     */     private IMessage parseMessage(ObjectMessage message)
/*     */     {
/* 221 */       IMessage msg = null;
/*     */       try {
/* 223 */         if ((message.getObject() instanceof IMessage))
/* 224 */           msg = (IMessage)message.getObject();
/*     */       }
/*     */       catch (JMSException ex) {
/* 227 */         LogHome.getLog().error("", ex);
/*     */       }
/* 229 */       return msg;
/*     */     }
/*     */ 
/*     */     protected void doStreamMessage(StreamMessage message) {
/*     */     }
/*     */ 
/*     */     protected void doTextMessage(TextMessage message) {
/* 236 */       for (IMessageListener listener : AmqMsgSubscriber.this.getMsgListeners())
/*     */         try {
/* 238 */           List msgs = new ArrayList();
/* 239 */           msgs.add(message);
/* 240 */           listener.notify(msgs);
/*     */         } catch (Throwable ex) {
/* 242 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */     }
/*     */ 
/*     */     public void onException(JMSException exp)
/*     */     {
/* 248 */       LogHome.getLog().error("JMS 接收消息异常： " + exp.getMessage());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.amq.AmqMsgSubscriber
 * JD-Core Version:    0.6.0
 */