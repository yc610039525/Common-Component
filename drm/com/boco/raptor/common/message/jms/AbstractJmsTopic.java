/*     */ package com.boco.raptor.common.message.jms;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.Context;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AbstractJmsTopic
/*     */ {
/*     */   private TopicConnection topicConnection;
/*     */   private TopicSession topicSession;
/*     */   private Topic topic;
/*     */   private Context context;
/*     */   private boolean transacted;
/*     */   private int acknowledgementMode;
/*     */   private String topicConnectionFactoryName;
/*     */   private String topicName;
/*     */   private String clientId;
/*     */   private boolean durable;
/*     */ 
/*     */   protected AbstractJmsTopic(Context context, String topicConnectionFactoryName, String topicName, String clientId, boolean durable, boolean transacted, int acknowledgementMode)
/*     */   {
/*     */     try
/*     */     {
/*  48 */       this.context = context;
/*  49 */       if (this.context != null) {
/*  50 */         this.topicConnectionFactoryName = topicConnectionFactoryName;
/*  51 */         this.topicName = topicName;
/*  52 */         this.clientId = clientId;
/*  53 */         this.durable = durable;
/*  54 */         this.transacted = transacted;
/*  55 */         this.acknowledgementMode = acknowledgementMode;
/*  56 */         initTopic();
/*     */       } else {
/*  58 */         LogHome.getLog().error("TopicName=" + topicName + ", Context 为空不能注册 ！");
/*     */       }
/*     */     } catch (Exception ex) {
/*  61 */       LogHome.getLog().error("TopicName=" + topicName + ", " + ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initTopic() throws Exception {
/*  66 */     TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)this.context.lookup(this.topicConnectionFactoryName);
/*  67 */     this.topicConnection = topicConnectionFactory.createTopicConnection();
/*  68 */     this.topicConnection.start();
/*  69 */     if (this.durable) {
/*  70 */       this.topicConnection.setClientID(this.clientId);
/*     */     }
/*  72 */     this.topicSession = this.topicConnection.createTopicSession(this.transacted, this.acknowledgementMode);
/*  73 */     this.topic = ((Topic)this.context.lookup(this.topicName));
/*     */   }
/*     */ 
/*     */   protected Topic getTopic() {
/*  77 */     return this.topic;
/*     */   }
/*     */ 
/*     */   public Session getSession() {
/*  81 */     return this.topicSession;
/*     */   }
/*     */ 
/*     */   protected int getAcknowledgementMode()
/*     */   {
/*  89 */     return this.acknowledgementMode;
/*     */   }
/*     */ 
/*     */   protected boolean isTransacted() {
/*  93 */     return this.transacted;
/*     */   }
/*     */ 
/*     */   protected boolean isDurable() {
/*  97 */     return this.durable;
/*     */   }
/*     */ 
/*     */   protected void commit() throws Exception {
/* 101 */     if (isTransacted())
/* 102 */       getSession().commit();
/*     */   }
/*     */ 
/*     */   protected TopicConnection getTopicConnection()
/*     */   {
/* 107 */     return this.topicConnection;
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 112 */       if (this.topicSession != null) {
/* 113 */         this.topicSession.close();
/*     */       }
/* 115 */       if (this.topicConnection != null)
/* 116 */         this.topicConnection.close();
/*     */     }
/*     */     catch (Exception ex) {
/* 119 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getTopicName() {
/* 124 */     return this.topicName;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.jms.AbstractJmsTopic
 * JD-Core Version:    0.6.0
 */