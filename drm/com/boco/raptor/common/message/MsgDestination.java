/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.id.CUIDHexGenerator;
/*     */ import com.ibm.mq.jms.MQQueue;
/*     */ import com.ibm.mq.jms.MQTopic;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.ExceptionListener;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageProducer;
/*     */ import javax.jms.Session;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class MsgDestination
/*     */   implements ExceptionListener, IMsgDestination
/*     */ {
/*     */   public static final String MSG_DEST_TYPE_SEPARATOR = ".";
/*     */   protected String destinationId;
/*     */   protected String selector;
/*     */   protected Connection connection;
/*     */   protected Session session;
/*     */   protected Destination destination;
/*     */   protected String destinationName;
/*     */   protected String contextName;
/*     */   protected boolean transacted;
/*     */   protected int acknowledgementMode;
/*     */   protected String clientId;
/*     */   protected boolean durable;
/*     */   private String channeltype;
/*     */   protected String userName;
/*     */   protected String password;
/*     */   protected MessageProducer producer;
/*     */   protected MessageConsumer consumer;
/*  53 */   private boolean connect = false;
/*  54 */   private JMSConnectionFactory factory = null;
/*     */ 
/*     */   public MsgDestination(String channeltype, String destinationName, String clientId, boolean durable, boolean transacted, int acknowledgementMode) {
/*  57 */     this(channeltype, "", destinationName, "", "", clientId, durable, transacted, acknowledgementMode);
/*     */   }
/*     */ 
/*     */   public MsgDestination(String channeltype, String contextName, String destinationName, String userName, String password, String clientId, boolean durable, boolean transacted, int acknowledgementMode) {
/*  61 */     this(null, channeltype, contextName, destinationName, userName, password, clientId, durable, transacted, acknowledgementMode);
/*     */   }
/*     */ 
/*     */   public MsgDestination(JMSConnectionFactory factory, String channeltype, String contextName, String destinationName, String userName, String password, String clientId, boolean durable, boolean transacted, int acknowledgementMode)
/*     */   {
/*     */     try {
/*  67 */       this.factory = factory;
/*  68 */       this.channeltype = channeltype;
/*  69 */       this.contextName = contextName;
/*  70 */       this.destinationName = destinationName;
/*  71 */       this.userName = userName;
/*  72 */       this.password = password;
/*  73 */       this.clientId = clientId;
/*  74 */       this.durable = durable;
/*  75 */       this.transacted = transacted;
/*  76 */       this.acknowledgementMode = acknowledgementMode;
/*  77 */       this.destinationId = CUIDHexGenerator.getInstance().generate("destinationName");
/*  78 */       initDestination();
/*     */     } catch (Exception ex) {
/*  80 */       LogHome.getLog().error("增加消息定义失败：DestinationName=" + destinationName + ", message=" + ex.getMessage(), ex);
/*  81 */       throw new UserException("初始化消息失败！");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Session getSession() {
/*  86 */     return this.session;
/*     */   }
/*     */ 
/*     */   public Destination getDestination() {
/*  90 */     return this.destination;
/*     */   }
/*     */ 
/*     */   public String getDestinationId() {
/*  94 */     return this.destinationId;
/*     */   }
/*     */ 
/*     */   public void setDestinationId(String id) {
/*  98 */     this.destinationId = id;
/*     */   }
/*     */ 
/*     */   public Connection getConnection() {
/* 102 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public String getDestinationName() {
/* 106 */     return this.destinationName;
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 111 */       if (this.consumer != null) {
/* 112 */         this.consumer.close();
/*     */       }
/* 114 */       if (this.producer != null) {
/* 115 */         this.producer.close();
/*     */       }
/* 117 */       if (this.session != null) {
/* 118 */         this.session.close();
/*     */       }
/* 120 */       if (this.connection != null)
/* 121 */         this.connection.close();
/*     */     }
/*     */     catch (Exception ex) {
/* 124 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void commit() throws Exception {
/* 129 */     if (isTransacted())
/* 130 */       getSession().commit();
/*     */   }
/*     */ 
/*     */   public boolean isTransacted()
/*     */   {
/* 135 */     return this.transacted;
/*     */   }
/*     */ 
/*     */   public int getAcknowledgementMode() {
/* 139 */     return this.acknowledgementMode;
/*     */   }
/*     */ 
/*     */   public boolean isDurable() {
/* 143 */     return this.durable;
/*     */   }
/*     */ 
/*     */   public void setConsumer(MessageConsumer consumer) {
/* 147 */     this.consumer = consumer;
/*     */   }
/*     */ 
/*     */   public MessageProducer getProducer() {
/* 151 */     return this.producer;
/*     */   }
/*     */ 
/*     */   public void setProducer(MessageProducer _producer) {
/* 155 */     this.producer = _producer;
/*     */   }
/*     */ 
/*     */   public void onException(JMSException exp) {
/* 159 */     LogHome.getLog().error("JMS 接受消息异常： " + exp.getMessage(), exp);
/* 160 */     if ((exp.getErrorCode() != null) && (exp.getErrorCode().equals("JMSWMQ1107"))) {
/* 161 */       this.connect = false;
/* 162 */       LogHome.getLog().error("IBM MQ [" + this.destinationName + "]断开服务器连接！");
/* 163 */     } else if ((exp.getMessage() != null) && (exp.getMessage().equals("java.io.EOFException"))) {
/* 164 */       this.connect = false;
/* 165 */       LogHome.getLog().error("ActiveMQ [" + this.destinationName + "]断开服务器连接！");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reconnect() {
/* 170 */     int reConnectNum = 0;
/* 171 */     while (!this.connect) {
/*     */       try {
/* 173 */         reConnectNum++;
/* 174 */         close();
/* 175 */         initDestination();
/*     */       } catch (Exception ex) {
/* 177 */         LogHome.getLog().error("重连消息服务器连接失败：" + ex);
/*     */       }
/*     */       try {
/* 180 */         if (!this.connect)
/* 181 */           Thread.sleep(5000L);
/*     */       }
/*     */       catch (InterruptedException e) {
/* 184 */         e.printStackTrace();
/*     */       }
/* 186 */       if (reConnectNum == 3)
/* 187 */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initDestination() throws Exception
/*     */   {
/* 193 */     if (this.factory == null) {
/* 194 */       this.factory = JMSConnectionManager.getInstance().getJMSConnectionFactory(this.destinationName);
/*     */     }
/* 196 */     if ((this.factory == null) || (!this.factory.isConn())) {
/* 197 */       LogHome.getLog().warn("与MQ队列管理器连接失败，无法建立目标队列连接" + this.contextName + "--" + this.destinationName);
/* 198 */       return;
/*     */     }
/* 200 */     ConnectionFactory connectionFactory = this.factory.getConnFactory();
/* 201 */     this.connection = connectionFactory.createConnection(this.userName, this.password);
/* 202 */     this.connection.start();
/* 203 */     if ((this.durable) && (this.clientId != null) && (this.clientId.length() > 0) && (!"null".equals(this.clientId))) {
/* 204 */       this.connection.setClientID(this.clientId);
/*     */     }
/* 206 */     this.connection.setExceptionListener(this);
/* 207 */     this.session = this.connection.createSession(this.transacted, this.acknowledgementMode);
/* 208 */     if (MsgChannelTypeEnum.QUEUE.toString().equals(this.channeltype)) {
/* 209 */       this.destination = this.session.createQueue(this.destinationName);
/* 210 */       if ((this.destination instanceof MQQueue)) {
/* 211 */         ((MQQueue)this.destination).setPersistence(1);
/*     */ 
/* 215 */         ((MQQueue)this.destination).setPutAsyncAllowed(1);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 222 */       this.destination = this.session.createTopic(this.destinationName);
/* 223 */       if ((this.destination instanceof MQTopic)) {
/* 224 */         ((MQTopic)this.destination).setPersistence(1);
/* 225 */         ((MQTopic)this.destination).setPutAsyncAllowed(1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 231 */     this.connect = true;
/*     */   }
/*     */ 
/*     */   public boolean isConnect() {
/* 235 */     return this.connect;
/*     */   }
/*     */ 
/*     */   public String getSelector() {
/* 239 */     return this.selector;
/*     */   }
/*     */ 
/*     */   public void setSelector(String selector) {
/* 243 */     this.selector = selector;
/*     */   }
/*     */ 
/*     */   public static enum MsgDestType
/*     */   {
/*  32 */     MS, 
/*  33 */     MR;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MsgDestination
 * JD-Core Version:    0.6.0
 */