/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.j2ee.ContextHelper;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.common.message.amq.AmqMsgPublisher;
/*     */ import com.boco.raptor.common.message.amq.AmqMsgSubscriber;
/*     */ import com.boco.raptor.common.message.jms.JmsMsgPublisher;
/*     */ import com.boco.raptor.common.message.jms.JmsMsgSubscriber;
/*     */ import com.boco.raptor.common.message.xrpc.XrpcMsgPublisher;
/*     */ import com.boco.raptor.common.message.xrpc.XrpcMsgSubscriber;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class MsgPubSubFactory
/*     */ {
/*  42 */   private static MsgPubSubFactory instance = new MsgPubSubFactory();
/*     */   private Context jmsContext;
/*     */   private List<IMessagePublisher> publishers;
/*     */ 
/*     */   public static MsgPubSubFactory getInstance()
/*     */   {
/*  50 */     return instance;
/*     */   }
/*     */ 
/*     */   public IMessageSubscriber createMsgSubscriber(MsgServiceTypeEnum serviceType, String topicName) {
/*  54 */     IMessageSubscriber sub = null;
/*  55 */     if (serviceType == MsgServiceTypeEnum.JMS)
/*  56 */       sub = new JmsMsgSubscriber(topicName);
/*  57 */     else if (serviceType == MsgServiceTypeEnum.XRPC)
/*  58 */       sub = new XrpcMsgSubscriber(topicName);
/*  59 */     else if (serviceType == MsgServiceTypeEnum.ACTIVE_MQ) {
/*  60 */       sub = new AmqMsgSubscriber(topicName);
/*     */     }
/*  62 */     return sub;
/*     */   }
/*     */ 
/*     */   public IMessagePublisher createMsgPublisher(MsgServiceTypeEnum serviceType, String topicName) throws Exception {
/*  66 */     IMessagePublisher publisher = null;
/*  67 */     if (serviceType == MsgServiceTypeEnum.JMS)
/*  68 */       publisher = new JmsMsgPublisher(topicName);
/*  69 */     else if (serviceType == MsgServiceTypeEnum.XRPC)
/*  70 */       publisher = new XrpcMsgPublisher();
/*  71 */     else if (serviceType == MsgServiceTypeEnum.ACTIVE_MQ)
/*  72 */       publisher = new AmqMsgPublisher(topicName);
/*     */     else {
/*  74 */       LogHome.getLog().error("消息类型不正确：" + serviceType);
/*     */     }
/*  76 */     if (publisher != null) {
/*  77 */       publisher.initPublisher();
/*     */     }
/*     */ 
/*  80 */     List publishers = new ArrayList();
/*  81 */     publishers.add(publisher);
/*  82 */     getMessageServiceBO().modifyMsgPublishers(publishers);
/*     */ 
/*  84 */     return publisher;
/*     */   }
/*     */ 
/*     */   public void setMsgPublishers(List<IMessagePublisher> publishers) {
/*  88 */     for (IMessagePublisher publisher : publishers) {
/*     */       try {
/*  90 */         publisher.initPublisher();
/*     */       } catch (Exception ex) {
/*  92 */         publishers.remove(publisher);
/*  93 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*  96 */     getMessageServiceBO().modifyMsgPublishers(publishers);
/*     */   }
/*     */ 
/*     */   public Context getJmsInitialContext() {
/* 100 */     return this.jmsContext;
/*     */   }
/*     */ 
/*     */   public void setJmsContext(Map contextProps) throws NamingException {
/* 104 */     Hashtable _contextProps = new Hashtable();
/* 105 */     Iterator it = contextProps.keySet().iterator();
/* 106 */     while (it.hasNext()) {
/* 107 */       Object key = it.next();
/* 108 */       _contextProps.put(key, contextProps.get(key));
/*     */     }
/* 110 */     if (_contextProps.size() > 0)
/* 111 */       this.jmsContext = ContextHelper.createContext(_contextProps);
/*     */     else
/* 113 */       this.jmsContext = new InitialContext();
/*     */   }
/*     */ 
/*     */   private IMessageServiceBO getMessageServiceBO()
/*     */   {
/* 118 */     return (IMessageServiceBO)BoHomeFactory.getInstance().getBO(IMessageServiceBO.class);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MsgPubSubFactory
 * JD-Core Version:    0.6.0
 */