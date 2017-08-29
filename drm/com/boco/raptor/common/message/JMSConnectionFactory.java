/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.ibm.mq.jms.MQConnectionFactory;
/*     */ import com.ibm.mq.jms.MQQueueConnectionFactory;
/*     */ import com.ibm.mq.jms.MQTopicConnectionFactory;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.QueueConnectionFactory;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import org.apache.activemq.ActiveMQConnection;
/*     */ import org.apache.activemq.ActiveMQConnectionFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JMSConnectionFactory
/*     */ {
/*  24 */   private JMSContext jmsContext = null;
/*  25 */   private boolean mainMQ = true;
/*  26 */   private boolean isConn = false;
/*  27 */   private ConnectionFactory connFactory = null;
/*  28 */   private Set<String> destinationNames = new HashSet();
/*     */ 
/*     */   public JMSConnectionFactory(JMSContext jmsContext, boolean isMainMQ) throws Exception {
/*  31 */     this.jmsContext = jmsContext;
/*  32 */     this.mainMQ = isMainMQ;
/*  33 */     this.connFactory = createConnectionFactory();
/*  34 */     this.isConn = isMqActive();
/*  35 */     String checkThreadName = "MQ管理器连接检测线程-" + jmsContext.getUrl() + ":" + jmsContext.getQueueManager() + ":" + jmsContext.getChannelTypeUpperCase();
/*     */ 
/*  37 */     CheckMQConnectThread checkConnThread = new CheckMQConnectThread(checkThreadName);
/*  38 */     checkConnThread.start();
/*     */   }
/*     */ 
/*     */   private ConnectionFactory createConnectionFactory() throws Exception {
/*  42 */     ConnectionFactory connectionFactory = null;
/*  43 */     String msgServiceType = this.jmsContext.getMsgServiceTypeUpperCase();
/*  44 */     if (MsgServiceTypeEnum.IBM_MQ.toString().equals(msgServiceType))
/*  45 */       connectionFactory = createIBMMQConnectFactory();
/*  46 */     else if (MsgServiceTypeEnum.ACTIVE_MQ.toString().equals(msgServiceType))
/*  47 */       connectionFactory = createActiveMQConnectFactory();
/*     */     else {
/*  49 */       throw new UserException("不支持的消息服务类型");
/*     */     }
/*  51 */     return connectionFactory;
/*     */   }
/*     */ 
/*     */   private ConnectionFactory createIBMMQConnectFactory() throws Exception {
/*  55 */     if (this.jmsContext.getChannelTypeUpperCase().equals(MsgChannelTypeEnum.TOPIC.toString()))
/*  56 */       return createIBMMQTopicConnectionFactory();
/*  57 */     if (this.jmsContext.getChannelTypeUpperCase().equals(MsgChannelTypeEnum.QUEUE.toString())) {
/*  58 */       return createIBMMQQueueConnectionFactory();
/*     */     }
/*  60 */     return createIBMMQConnectionFactory();
/*     */   }
/*     */ 
/*     */   private ConnectionFactory createIBMMQConnectionFactory() throws Exception
/*     */   {
/*  65 */     ConnectionFactory connectionFactory = new MQConnectionFactory();
/*  66 */     ((MQConnectionFactory)connectionFactory).setTransportType(1);
/*  67 */     ((MQConnectionFactory)connectionFactory).setQueueManager(this.jmsContext.getQueueManager());
/*  68 */     ((MQConnectionFactory)connectionFactory).setHostName(this.jmsContext.getUrl());
/*  69 */     ((MQConnectionFactory)connectionFactory).setPort(this.jmsContext.getPort());
/*  70 */     ((MQConnectionFactory)connectionFactory).setChannel(this.jmsContext.getChannel());
/*  71 */     ((MQConnectionFactory)connectionFactory).setCCSID(this.jmsContext.getCcsid());
/*  72 */     return connectionFactory;
/*     */   }
/*     */ 
/*     */   private QueueConnectionFactory createIBMMQQueueConnectionFactory() throws JMSException {
/*  76 */     QueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
/*  77 */     ((MQQueueConnectionFactory)connectionFactory).setTransportType(1);
/*  78 */     ((MQQueueConnectionFactory)connectionFactory).setQueueManager(this.jmsContext.getQueueManager());
/*  79 */     ((MQQueueConnectionFactory)connectionFactory).setHostName(this.jmsContext.getUrl());
/*  80 */     ((MQQueueConnectionFactory)connectionFactory).setPort(this.jmsContext.getPort());
/*  81 */     ((MQQueueConnectionFactory)connectionFactory).setChannel(this.jmsContext.getChannel());
/*  82 */     ((MQQueueConnectionFactory)connectionFactory).setCCSID(this.jmsContext.getCcsid());
/*  83 */     return connectionFactory;
/*     */   }
/*     */ 
/*     */   private TopicConnectionFactory createIBMMQTopicConnectionFactory() throws JMSException {
/*  87 */     TopicConnectionFactory connectionFactory = new MQTopicConnectionFactory();
/*  88 */     ((MQTopicConnectionFactory)connectionFactory).setTransportType(1);
/*  89 */     ((MQTopicConnectionFactory)connectionFactory).setQueueManager(this.jmsContext.getQueueManager());
/*  90 */     ((MQTopicConnectionFactory)connectionFactory).setHostName(this.jmsContext.getUrl());
/*  91 */     ((MQTopicConnectionFactory)connectionFactory).setPort(this.jmsContext.getPort());
/*  92 */     ((MQTopicConnectionFactory)connectionFactory).setChannel(this.jmsContext.getChannel());
/*  93 */     ((MQTopicConnectionFactory)connectionFactory).setCCSID(this.jmsContext.getCcsid());
/*  94 */     return connectionFactory;
/*     */   }
/*     */ 
/*     */   private ConnectionFactory createActiveMQConnectFactory() {
/*  98 */     String user = this.jmsContext.getUser();
/*  99 */     String password = this.jmsContext.getPassword();
/* 100 */     String url = this.jmsContext.getUrl();
/* 101 */     int port = this.jmsContext.getPort();
/* 102 */     if ((user == null) || ("".equals(user))) {
/* 103 */       user = ActiveMQConnection.DEFAULT_USER;
/*     */     }
/* 105 */     if ((password == null) || ("".equals(password))) {
/* 106 */       password = ActiveMQConnection.DEFAULT_PASSWORD;
/*     */     }
/* 108 */     if ((url == null) || ("".equals(url)) || (port == 0))
/* 109 */       url = "failover://tcp://localhost:61616";
/*     */     else {
/* 111 */       url = "tcp://" + url + ":" + port + "?wireFormat.maxInactivityDuration=0&jms.useAsyncSend=true&jms.producerWindowSize=1024000";
/*     */     }
/*     */ 
/* 115 */     ConnectionFactory connectionFactory = null;
/* 116 */     connectionFactory = new ActiveMQConnectionFactory(user, password, url);
/* 117 */     return connectionFactory;
/*     */   }
/*     */ 
/*     */   private boolean isMqActive() {
/*     */     try {
/* 122 */       Connection conn = this.connFactory.createConnection(this.jmsContext.getUser(), this.jmsContext.getPassword());
/* 123 */       if (conn != null) {
/* 124 */         conn.close();
/* 125 */         return true;
/*     */       }
/*     */     } catch (JMSException ex) {
/* 128 */       if (!ex.getErrorCode().equals("JMSWMQ0018"))
/*     */       {
/* 130 */         LogHome.getLog().error("检测MQ是否可以连接失败:", ex);
/*     */       }
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public JMSContext getJmsContext() {
/* 137 */     return this.jmsContext;
/*     */   }
/*     */ 
/*     */   public boolean isMainMQ() {
/* 141 */     return this.mainMQ;
/*     */   }
/*     */ 
/*     */   public boolean isConn() {
/* 145 */     return this.isConn;
/*     */   }
/*     */ 
/*     */   public ConnectionFactory getConnFactory() {
/* 149 */     return this.connFactory;
/*     */   }
/*     */ 
/*     */   public void addDestination(String destinationName) {
/* 153 */     synchronized (destinationName) {
/* 154 */       this.destinationNames.add(destinationName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<String> getDestinations() {
/* 159 */     return this.destinationNames;
/*     */   }
/*     */ 
/*     */   public void clearDestination() {
/* 163 */     synchronized (this.destinationNames) {
/* 164 */       this.destinationNames.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CheckMQConnectThread extends Thread {
/*     */     public CheckMQConnectThread(String threadName) {
/* 170 */       super();
/*     */     }
/*     */ 
/*     */     public void run() {
/* 174 */       int connCount = 0;
/*     */       while (true) {
/* 176 */         if (JMSConnectionFactory.this.isMqActive()) {
/* 177 */           connCount = 0;
/* 178 */           JMSConnectionFactory.access$102(JMSConnectionFactory.this, true);
/*     */         } else {
/* 180 */           connCount++;
/* 181 */           if (connCount == 3) {
/* 182 */             JMSConnectionFactory.access$102(JMSConnectionFactory.this, false);
/*     */ 
/* 185 */             LogHome.getLog().error("MQ队列管理器无法连接，确认是否启动：" + JMSConnectionFactory.this.jmsContext.getUrl() + ":" + JMSConnectionFactory.this.jmsContext.getQueueManager());
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 190 */           Thread.sleep(5000L);
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.JMSConnectionFactory
 * JD-Core Version:    0.6.0
 */