/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.server.common.cfg.TnmsServerName;
/*     */ import com.boco.transnms.server.dao.base.CachedDtoMessage;
/*     */ import com.boco.transnms.server.dao.base.DaoHelper;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.MessageProducer;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.TextMessage;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*     */ 
/*     */ public class MsgBusManager
/*     */ {
/*  36 */   private static MsgBusManager instance = new MsgBusManager();
/*     */ 
/*  38 */   private Map<String, MsgDestination> producerDestinations = new ConcurrentHashMap();
/*     */ 
/*  40 */   private Map<String, MsgDestination> consumerDestinations = new ConcurrentHashMap();
/*  41 */   private Map<ISimpleMsgListener, Set<MsgDestination>> consumers = new ConcurrentHashMap();
/*  42 */   private Map<MessageListener, Set<MsgDestination>> jmsConsumers = new ConcurrentHashMap();
/*  43 */   private Map<ISimpleMsgListener, String> listeners = new ConcurrentHashMap();
/*  44 */   private Map<MessageListener, String> jmsListeners = new ConcurrentHashMap();
/*  45 */   private Map<JMSConnectionFactory, Set<String>> destinations = new ConcurrentHashMap();
/*     */   private CheckMQQConnectThread checkThread;
/*     */ 
/*     */   private MsgBusManager()
/*     */   {
/*  53 */     this.checkThread = new CheckMQQConnectThread("MQ队列/主题连接检测线程");
/*  54 */     this.checkThread.start();
/*     */ 
/*  56 */     CheckMQConnectThread checkConnThread = new CheckMQConnectThread("MQ主备切换检测线程");
/*  57 */     checkConnThread.start();
/*     */   }
/*     */ 
/*     */   public static MsgBusManager getInstance()
/*     */   {
/*  65 */     return instance;
/*     */   }
/*     */ 
/*     */   public static void init(String beanXmlFileName)
/*     */     throws Exception
/*     */   {
/*  75 */     init(new String[] { beanXmlFileName });
/*     */   }
/*     */ 
/*     */   public static void init(String[] beanXmlFileNames)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  87 */       new FileSystemXmlApplicationContext(beanXmlFileNames);
/*     */     } catch (Exception ex) {
/*  89 */       LogHome.getLog().error("消息管理器加载失败", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addProducerDestination(String destinationName)
/*     */     throws Exception
/*     */   {
/*  99 */     JMSConnectionFactory factory = JMSConnectionManager.getInstance().getJMSConnectionFactory(destinationName);
/* 100 */     if (factory != null) {
/* 101 */       if (!factory.isConn()) {
/* 102 */         LogHome.getLog().warn(new StringBuilder().append("和MQ队列管理器处理断开状态，创建与队列回话失败").append(destinationName).append(",").append(factory.getJmsContext().getUrl()).toString());
/* 103 */         return;
/*     */       }
/* 105 */       JMSContext context = factory.getJmsContext();
/* 106 */       LogHome.getLog().info(new StringBuilder().append("添加目标队列连接=").append(destinationName).append(",").append(factory.isMainMQ()).append(",").append(context.getContextName()).toString());
/* 107 */       if (!this.producerDestinations.containsKey(destinationName)) {
/* 108 */         MsgDestination destination = new MsgDestination(context.getChannelTypeUpperCase(), context.getContextName(), destinationName, context.getUser(), context.getPassword(), "", false, false, 1);
/*     */ 
/* 112 */         if (destination.isConnect()) {
/* 113 */           Set destinationNames = (Set)this.destinations.get(factory);
/* 114 */           if (destinationNames == null) {
/* 115 */             this.destinations.put(factory, new HashSet());
/*     */           }
/* 117 */           ((Set)this.destinations.get(factory)).add(destinationName);
/* 118 */           this.producerDestinations.put(destinationName, destination);
/* 119 */           LogHome.getLog().warn(new StringBuilder().append("消息Destination添加成功，destinatioName=").append(destinationName).append("上下文信息").append(context.toString()).toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 124 */       LogHome.getLog().error(new StringBuilder().append("消息Destination没有配置，destinatioName=").append(destinationName).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private List<MsgDestination> addConsumerDestinationOnly(String destinationName)
/*     */     throws Exception
/*     */   {
/* 147 */     List destinations = new ArrayList();
/* 148 */     List jmsConnectionFactorys = JMSConnectionManager.getInstance().getJMSConnectionFactorys(destinationName);
/* 149 */     if (jmsConnectionFactorys.size() == 0) {
/* 150 */       LogHome.getLog().error(new StringBuilder().append("消息Destination没有配置，destinatioName=").append(destinationName).toString());
/* 151 */       return destinations;
/*     */     }
/* 153 */     for (JMSConnectionFactory jmsConnectionFactory : jmsConnectionFactorys) {
/* 154 */       if ((jmsConnectionFactory != null) && (jmsConnectionFactory.isConn()))
/*     */       {
/* 158 */         MsgDestination destination = getDestinationByFactory(jmsConnectionFactory, destinationName);
/* 159 */         if (destination != null) {
/* 160 */           jmsConnectionFactory.getDestinations().add(destinationName);
/* 161 */           destinations.add(destination);
/*     */         }
/*     */       }
/*     */     }
/* 165 */     return destinations;
/*     */   }
/*     */ 
/*     */   private MsgDestination getDestinationByFactory(JMSConnectionFactory factory, String destinationName) {
/* 169 */     JMSContext context = factory.getJmsContext();
/* 170 */     MsgDestination destination = new MsgDestination(factory, context.getChannelTypeUpperCase(), context.getContextName(), destinationName, context.getUser(), context.getPassword(), "", false, false, 1);
/*     */ 
/* 174 */     if (destination.isConnect()) {
/* 175 */       Set destinationNames = (Set)this.destinations.get(factory);
/* 176 */       if (destinationNames == null) {
/* 177 */         this.destinations.put(factory, new HashSet());
/*     */       }
/* 179 */       ((Set)this.destinations.get(factory)).add(destinationName);
/* 180 */       this.consumerDestinations.put(destination.getDestinationId(), destination);
/* 181 */       LogHome.getLog().warn(new StringBuilder().append("消息Destination添加成功，destinatioName=").append(destinationName).append(",contextName=").append(context).toString());
/* 182 */       return destination;
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public MsgDestination getProducerMsgDestination(String topicName)
/*     */   {
/* 193 */     return (MsgDestination)this.producerDestinations.get(topicName);
/*     */   }
/*     */ 
/*     */   public boolean sendMessage(IMessage msg, String sourceName)
/*     */   {
/* 203 */     return sendMsg(msg, sourceName);
/*     */   }
/*     */ 
/*     */   public void sendMessage(IMessage msg)
/*     */   {
/* 211 */     sendMsg(msg, null);
/*     */   }
/*     */ 
/*     */   private boolean sendMsg(IMessage msg, String _sourceName)
/*     */   {
/* 225 */     boolean issend = true;
/*     */     try {
/* 227 */       String topicName = msg.getTopicName();
/* 228 */       MsgDestination msgDestination = getRealProducerMsgDestination(topicName);
/* 229 */       if ((msgDestination != null) && (msgDestination.getSession() != null) && (msgDestination.isConnect())) {
/* 230 */         String sourceName = getSourceName(_sourceName, msg.getSourceName());
/* 231 */         String targetId = msg.getTargetId();
/* 232 */         ObjectMessage objectMessage = createObjectMessage(msgDestination, topicName, sourceName, targetId, msg);
/* 233 */         MessageProducer producer = getMessageProducer(msgDestination);
/* 234 */         producer.send(objectMessage);
/* 235 */         msgDestination.commit();
/*     */       } else {
/* 237 */         String msgbody = getMsgContent(msg);
/* 238 */         issend = false;
/* 239 */         String src = DaoHelper.getCallClassName();
/* 240 */         LogHome.getLog().warn(new StringBuilder().append("消息发送失败，topicName=").append(topicName).append(", sourceName=").append(msg.getSourceName()).append(", src=").append(src).append(", msgBody=").append(msgbody).toString());
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 244 */       issend = false;
/* 245 */       LogHome.getLog().error(new StringBuilder().append("发送消息失败，topicName=").append(msg.getTopicName()).append(", errMsg=").append(ex.getMessage()).toString());
/*     */     }
/* 247 */     return issend;
/*     */   }
/*     */ 
/*     */   public boolean sendTextMessage(String msg, String topicName)
/*     */   {
/* 252 */     boolean issend = true;
/*     */     try {
/* 254 */       MsgDestination msgDestination = getRealProducerMsgDestination(topicName);
/* 255 */       if ((msgDestination != null) && (msgDestination.getSession() != null) && (msgDestination.isConnect())) {
/* 256 */         String sourceName = DaoHelper.getCallClassName();
/* 257 */         TextMessage textMessage = createTextMessage(msgDestination, sourceName, msg);
/* 258 */         MessageProducer producer = getMessageProducer(msgDestination);
/* 259 */         producer.send(textMessage);
/* 260 */         msgDestination.commit();
/*     */       } else {
/* 262 */         issend = false;
/* 263 */         String src = DaoHelper.getCallClassName();
/* 264 */         LogHome.getLog().warn(new StringBuilder().append("消息发送失败，topicName=").append(topicName).append(", src=").append(src).append(", msgBody=").append(msg).toString());
/*     */       }
/*     */     } catch (Exception ex) {
/* 267 */       issend = false;
/* 268 */       LogHome.getLog().error(new StringBuilder().append("发送消息失败，topicName=").append(topicName).toString(), ex);
/*     */     }
/* 270 */     return issend;
/*     */   }
/*     */ 
/*     */   private MsgDestination getRealProducerMsgDestination(String topicName) throws Exception {
/* 274 */     MsgDestination msgDestination = getProducerMsgDestination(topicName);
/* 275 */     if (msgDestination == null) {
/* 276 */       addProducerDestination(topicName);
/* 277 */       msgDestination = getProducerMsgDestination(topicName);
/*     */     }
/* 279 */     return msgDestination;
/*     */   }
/*     */ 
/*     */   private MessageProducer getMessageProducer(MsgDestination msgDestination) throws Exception {
/* 283 */     MessageProducer producer = getProducer(msgDestination);
/* 284 */     producer.setDeliveryMode(1);
/* 285 */     return producer;
/*     */   }
/*     */ 
/*     */   private TextMessage createTextMessage(MsgDestination msgDestination, String sourceName, String msg) throws Exception {
/* 289 */     TextMessage textMessage = msgDestination.getSession().createTextMessage();
/* 290 */     textMessage.clearBody();
/*     */ 
/* 292 */     textMessage.setText(msg);
/* 293 */     return textMessage;
/*     */   }
/*     */ 
/*     */   private ObjectMessage createObjectMessage(MsgDestination msgDestination, String topicName, String sourceName, String targetId, IMessage msg) throws Exception
/*     */   {
/* 298 */     ObjectMessage objectMessage = msgDestination.getSession().createObjectMessage();
/* 299 */     objectMessage.clearBody();
/* 300 */     objectMessage.setJMSType(topicName);
/* 301 */     objectMessage.setObjectProperty("sourceName", sourceName);
/* 302 */     objectMessage.setObjectProperty("targetId", targetId);
/* 303 */     objectMessage.setObject(msg);
/* 304 */     return objectMessage;
/*     */   }
/*     */ 
/*     */   private String getSourceName(String _sourceName, String msgSourceName) {
/* 308 */     String sourceName = _sourceName;
/* 309 */     if ((_sourceName == null) && (
/* 310 */       (msgSourceName == null) || (msgSourceName.trim().length() == 0))) {
/* 311 */       sourceName = DaoHelper.getCallClassName();
/*     */     }
/*     */ 
/* 314 */     return sourceName;
/*     */   }
/*     */ 
/*     */   private String getMsgContent(IMessage msg) {
/* 318 */     String msgbody = "";
/* 319 */     if ((msg instanceof CachedDtoMessage))
/* 320 */       msgbody = ((CachedDtoMessage)msg).getMsgDtos().toString();
/*     */     else {
/* 322 */       msgbody = msg.toString();
/*     */     }
/* 324 */     return msgbody;
/*     */   }
/*     */ 
/*     */   public void addMsgListener(String destinationName, String selector, ISimpleMsgListener msgListener)
/*     */     throws Exception
/*     */   {
/* 335 */     if (this.listeners.containsKey(msgListener)) {
/* 336 */       return;
/*     */     }
/* 338 */     this.listeners.put(msgListener, new StringBuilder().append(destinationName).append(",").append(selector).toString());
/* 339 */     List destinations = addConsumerDestinationOnly(destinationName);
/* 340 */     LogHome.getLog().warn(new StringBuilder().append("添加消息监听").append(destinationName).toString());
/* 341 */     for (MsgDestination destination : destinations)
/* 342 */       if (destination != null) {
/* 343 */         destination.setSelector(selector);
/* 344 */         addMsgListener(destination, destinationName, selector, msgListener);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void addMsgListener(MsgDestination destination, String destinationName, String selector, ISimpleMsgListener msgListener)
/*     */     throws Exception
/*     */   {
/* 358 */     if (destination == null) {
/* 359 */       LogHome.getLog().error(new StringBuilder().append("消息主题添加失败，destinationName=").append(destinationName).toString());
/* 360 */       return;
/*     */     }
/*     */ 
/* 363 */     MessageConsumer topicConsumer = destination.getSession().createConsumer(destination.getDestination(), selector);
/* 364 */     if (topicConsumer != null) {
/* 365 */       topicConsumer.setMessageListener(new MessageHandler(msgListener, null));
/* 366 */       destination.setConsumer(topicConsumer);
/* 367 */       if (this.consumers.get(msgListener) == null) {
/* 368 */         this.consumers.put(msgListener, new HashSet());
/*     */       }
/* 370 */       ((Set)this.consumers.get(msgListener)).add(destination);
/* 371 */       LogHome.getLog().info(new StringBuilder().append("添加消息监听：").append(destinationName).append(",").append(msgListener).toString());
/*     */     } else {
/* 373 */       LogHome.getLog().error(new StringBuilder().append("消息接收者添加监听失败，topicName=").append(destinationName).append("， selector=").append(selector).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addMsgListener(MsgDestination destination, String destinationName, String selector, MessageListener msgListener) throws Exception {
/* 378 */     if (destination == null) {
/* 379 */       LogHome.getLog().error(new StringBuilder().append("消息主题添加失败，destinationName=").append(destinationName).toString());
/* 380 */       return;
/*     */     }
/* 382 */     MessageConsumer topicConsumer = destination.getSession().createConsumer(destination.getDestination(), selector);
/* 383 */     if (topicConsumer != null) {
/* 384 */       topicConsumer.setMessageListener(msgListener);
/* 385 */       destination.setConsumer(topicConsumer);
/* 386 */       if (this.jmsConsumers.get(msgListener) == null) {
/* 387 */         this.jmsConsumers.put(msgListener, new HashSet());
/*     */       }
/* 389 */       ((Set)this.jmsConsumers.get(msgListener)).add(destination);
/* 390 */       LogHome.getLog().info(new StringBuilder().append("添加消息监听：").append(destinationName).append(",").append(msgListener).append(",").append(destination.destinationId).append(",").append(destination).toString());
/*     */     } else {
/* 392 */       LogHome.getLog().error(new StringBuilder().append("消息接收者添加监听失败，topicName=").append(destinationName).append("， selector=").append(selector).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addMsgListener(String topicName, String selector, MessageListener msgListener)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 405 */       this.jmsListeners.put(msgListener, new StringBuilder().append(topicName).append(",").append(selector).toString());
/* 406 */       List destinations = addConsumerDestinationOnly(topicName);
/* 407 */       if ((destinations.size() == 1) || (destinations.size() == 2)) {
/* 408 */         LogHome.getLog().warn(new StringBuilder().append("添加消息监听").append(topicName).toString());
/*     */       } else {
/* 410 */         StringBuilder sb = new StringBuilder();
/* 411 */         for (MsgDestination msgDestination : destinations) {
/* 412 */           sb.append(msgDestination.getDestinationName()).append("====");
/*     */         }
/* 414 */         LogHome.getLog().error(new StringBuilder().append("错误监听数=").append(sb.toString()).toString());
/*     */       }
/* 416 */       for (MsgDestination destination : destinations)
/* 417 */         if (destination != null) {
/* 418 */           destination.setSelector(selector);
/* 419 */           addMsgListener(destination, topicName, selector, msgListener);
/*     */         }
/*     */     }
/*     */     catch (Exception ex) {
/* 423 */       LogHome.getLog().error("addMsgListenerError", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeMsgListener(ISimpleMsgListener msgListener)
/*     */     throws Exception
/*     */   {
/* 433 */     Set destinations = (Set)this.consumers.get(msgListener);
/* 434 */     for (MsgDestination destination : destinations) {
/* 435 */       if (destination != null) {
/* 436 */         destination.close();
/*     */       }
/*     */     }
/* 439 */     this.consumers.remove(msgListener);
/*     */   }
/*     */ 
/*     */   public void removeMsgListener(MessageListener msgListener) throws Exception {
/* 443 */     Set destinations = (Set)this.jmsConsumers.get(msgListener);
/* 444 */     for (MsgDestination destination : destinations) {
/* 445 */       if (destination != null) {
/* 446 */         destination.close();
/*     */       }
/*     */     }
/* 449 */     this.jmsConsumers.remove(msgListener);
/*     */   }
/*     */ 
/*     */   protected void removeAllTopic()
/*     */   {
/*     */     try
/*     */     {
/* 457 */       for (Iterator it = this.producerDestinations.values().iterator(); it.hasNext(); ) {
/* 458 */         MsgDestination topic = (MsgDestination)it.next();
/* 459 */         if (topic != null) {
/* 460 */           topic.close();
/*     */         }
/*     */       }
/* 463 */       for (Iterator it = this.consumerDestinations.values().iterator(); it.hasNext(); ) {
/* 464 */         MsgDestination topic = (MsgDestination)it.next();
/* 465 */         if (topic != null) {
/* 466 */           topic.close();
/*     */         }
/*     */       }
/* 469 */       this.producerDestinations.clear();
/* 470 */       this.consumerDestinations.clear();
/*     */     } catch (Exception ex) {
/* 472 */       LogHome.getLog().error(new StringBuilder().append("移除所有Topic失败").append(ex).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeAllTopic(Set<String> destinations) {
/*     */     try {
/* 478 */       for (Iterator it = this.producerDestinations.values().iterator(); it.hasNext(); ) {
/* 479 */         MsgDestination topic = (MsgDestination)it.next();
/* 480 */         if ((topic != null) && (destinations.contains(topic.destinationName))) {
/* 481 */           topic.close();
/*     */         }
/*     */       }
/*     */ 
/* 485 */       for (String destination : destinations)
/* 486 */         this.producerDestinations.remove(destination);
/*     */     }
/*     */     catch (Exception ex) {
/* 489 */       LogHome.getLog().error(new StringBuilder().append("移除指定Topic失败").append(destinations).toString(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 497 */     removeAllTopic();
/*     */   }
/*     */ 
/*     */   private MessageProducer getProducer(MsgDestination msgDestination)
/*     */     throws Exception
/*     */   {
/* 507 */     MessageProducer producer = msgDestination.getProducer();
/* 508 */     if (producer == null) {
/* 509 */       producer = msgDestination.getSession().createProducer(msgDestination.getDestination());
/* 510 */       msgDestination.setProducer(producer);
/*     */     }
/* 512 */     return producer;
/*     */   }
/*     */ 
/*     */   public String getClientCacheMsgDestName()
/*     */   {
/* 520 */     String destName = "T_CM_CLIENT_CACHE_SYNC";
/* 521 */     if (TnmsServerName.isAlarmServer()) {
/* 522 */       destName = "T_AM_CLIENT_CACHE_SYNC";
/*     */     }
/* 524 */     return destName;
/*     */   }
/*     */ 
/*     */   public String getCacheAdminMsgDestName()
/*     */   {
/* 532 */     String destName = "T_CM_CACHE_ADMIN";
/* 533 */     if (TnmsServerName.isAlarmServer()) {
/* 534 */       destName = "T_AM_CACHE_ADMIN";
/*     */     }
/* 536 */     return destName;
/*     */   }
/*     */ 
/*     */   public String getServerCacheSyncMsgDestName()
/*     */   {
/* 544 */     String destName = "T_CM_SVR_CACHE_SYNC";
/* 545 */     if (TnmsServerName.isAlarmServer()) {
/* 546 */       destName = "T_AM_SVR_CACHE_SYNC";
/*     */     }
/* 548 */     return destName;
/*     */   }
/*     */ 
/*     */   public String getObjectChangedMsgDestName()
/*     */   {
/* 556 */     String destName = "T_CM_OBJ_CHG";
/* 557 */     if (TnmsServerName.isAlarmServer()) {
/* 558 */       destName = "T_AM_OBJ_CHG";
/*     */     }
/* 560 */     return destName;
/*     */   }
/*     */ 
/*     */   private ISimpleMsgListener getListenerById(String destinationId)
/*     */   {
/* 571 */     Iterator iterator = this.consumers.keySet().iterator();
/*     */     ISimpleMsgListener l;
/* 572 */     while (iterator.hasNext()) {
/* 573 */       l = (ISimpleMsgListener)iterator.next();
/* 574 */       Set dests = (Set)this.consumers.get(l);
/* 575 */       for (MsgDestination dest : dests) {
/* 576 */         LogHome.getLog().info(new StringBuilder().append("getListener=").append(destinationId).append(",").append(dest.getDestinationName()).append(",").append(dest.contextName).toString());
/* 577 */         if ((dest != null) && (dest.getDestinationId().equals(destinationId))) {
/* 578 */           return l;
/*     */         }
/*     */       }
/*     */     }
/* 582 */     return null;
/*     */   }
/*     */ 
/*     */   private MessageListener getJmsListenerById(String destinationId) {
/* 586 */     Iterator iterator = this.jmsConsumers.keySet().iterator();
/*     */     MessageListener l;
/* 587 */     while (iterator.hasNext()) {
/* 588 */       l = (MessageListener)iterator.next();
/* 589 */       Set dests = (Set)this.jmsConsumers.get(l);
/* 590 */       for (MsgDestination dest : dests) {
/* 591 */         if ((dest != null) && (dest.getDestinationId().equals(destinationId))) {
/* 592 */           return l;
/*     */         }
/*     */       }
/*     */     }
/* 596 */     return null;
/*     */   }
/*     */ 
/*     */   private class MessageHandler
/*     */     implements MessageListener
/*     */   {
/*     */     private final ISimpleMsgListener msgListener;
/*     */ 
/*     */     private MessageHandler(ISimpleMsgListener msgListener)
/*     */     {
/* 770 */       this.msgListener = msgListener;
/*     */     }
/*     */     public void onMessage(Message message) {
/*     */       try {
/* 774 */         if (!(message instanceof ObjectMessage))
/* 775 */           return;
/* 776 */         IMessage msg = parseMessage((ObjectMessage)message);
/* 777 */         this.msgListener.notify(msg);
/*     */       } catch (Exception ex) {
/* 779 */         LogHome.getLog().error("JMS 接收消息异常： " + ex.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     private IMessage parseMessage(ObjectMessage message)
/*     */     {
/* 789 */       IMessage msg = null;
/*     */       try {
/* 791 */         Serializable object = message.getObject();
/* 792 */         if ((object instanceof IMessage)) {
/* 793 */           msg = (IMessage)object;
/*     */         } else {
/* 795 */           LogHome.getLog().debug("JMS 接收消息异常，接受消息类型错误： " + object.getClass().getName());
/* 796 */           LogHome.getLog().debug("类型错误消息对象： " + object);
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {
/*     */       }
/* 801 */       return msg;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CheckMQConnectThread extends Thread
/*     */   {
/*     */     public CheckMQConnectThread(String threadName)
/*     */     {
/* 662 */       super();
/*     */     }
/*     */ 
/*     */     public void run() {
/* 666 */       if (!JMSConnectionManager.getInstance().isUseMasterBackupMode()) {
/* 667 */         LogHome.getLog().warn("没有配置为双机热备，中断主备切换检测线程");
/* 668 */         return;
/*     */       }
/*     */       while (true) {
/*     */         try {
/* 672 */           Map factorys = JMSConnectionManager.getInstance().getAllMainConnectionFactorys();
/* 673 */           Map backFactorys = JMSConnectionManager.getInstance().getAllBackConnectionFactorys();
/* 674 */           Set contextNames = factorys.keySet();
/* 675 */           Iterator i$ = contextNames.iterator(); if (!i$.hasNext()) continue; String contextName = (String)i$.next();
/* 676 */           boolean usedMode = JMSConnectionManager.getInstance().getUsedMQConnMode(contextName).booleanValue();
/* 677 */           JMSConnectionFactory factory = (JMSConnectionFactory)factorys.get(contextName);
/* 678 */           JMSConnectionFactory backFactory = (JMSConnectionFactory)backFactorys.get(contextName);
/* 679 */           if ((factory.isConn()) || (!usedMode) || (!backFactory.isConn()))
/*     */             continue;
/* 681 */           LogHome.getLog().warn("当前主用连接中断，备用处理连接状态，切换到备用MQ" + backFactory.getJmsContext());
/* 682 */           JMSConnectionManager.getInstance().setUsedMQConnMode(contextName, false);
/* 683 */           switchMq((Set)MsgBusManager.this.destinations.get(factory));
/* 684 */           doSwitch(factory.getJmsContext().getQueueManager(), factory.getJmsContext().getUrl(), backFactory.getJmsContext().getUrl()); continue;
/*     */ 
/* 686 */           if ((usedMode) || (!factory.isConn()))
/*     */             continue;
/* 688 */           LogHome.getLog().warn("当前使用备用MQ，主用处理连接状态，切换到主用MQ" + factory.getJmsContext());
/* 689 */           JMSConnectionManager.getInstance().setUsedMQConnMode(contextName, true);
/* 690 */           switchMq((Set)MsgBusManager.this.destinations.get(backFactory));
/* 691 */           doSwitch(backFactory.getJmsContext().getQueueManager(), backFactory.getJmsContext().getUrl(), factory.getJmsContext().getUrl());
/*     */ 
/* 694 */           continue;
/*     */         } catch (Exception ex) {
/* 696 */           LogHome.getLog().error("MQ主备切换异常", ex);
/*     */         }
/*     */         try {
/* 699 */           Thread.sleep(10000L);
/*     */         } catch (InterruptedException e) {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void resetSimpleMsgListener(Set<String> destinationNames) {
/* 706 */       Set keys = MsgBusManager.this.listeners.keySet();
/* 707 */       for (ISimpleMsgListener messageListener : keys) {
/* 708 */         String[] destinaInfo = ((String)MsgBusManager.this.listeners.get(messageListener)).split(",");
/* 709 */         String destinationName = destinaInfo[0];
/* 710 */         String selector = destinaInfo.length == 2 ? destinaInfo[1] : "";
/*     */         try {
/* 712 */           if (destinationNames.contains(destinationName))
/* 713 */             MsgBusManager.this.addMsgListener(destinationName, selector, messageListener);
/*     */         }
/*     */         catch (Exception ex) {
/* 716 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void resetMessageListener(Set<String> destinationNames) {
/* 722 */       Set keys = MsgBusManager.this.jmsListeners.keySet();
/* 723 */       for (MessageListener messageListener : keys) {
/* 724 */         String[] destinaInfo = ((String)MsgBusManager.this.jmsListeners.get(messageListener)).split(",");
/* 725 */         String destinationName = destinaInfo[0];
/* 726 */         String selector = destinaInfo.length == 2 ? destinaInfo[1] : "";
/*     */         try {
/* 728 */           if (destinationNames.contains(destinationName))
/* 729 */             MsgBusManager.this.addMsgListener(destinationName, selector, messageListener);
/*     */         }
/*     */         catch (Exception ex) {
/* 732 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void switchMq(Set<String> destinations) {
/* 738 */       if ((destinations != null) && (destinations.size() > 0)) {
/* 739 */         MsgBusManager.this.removeAllTopic(destinations);
/* 740 */         LogHome.getLog().warn("MQ主备切换," + MsgBusManager.this.listeners + "," + "," + MsgBusManager.this.jmsListeners + "," + MsgBusManager.this.listeners.size() + "," + MsgBusManager.this.jmsListeners.size());
/*     */ 
/* 742 */         resetSimpleMsgListener(destinations);
/* 743 */         resetMessageListener(destinations);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void doSwitch(String qmName, String switchFromIp, String switchToIp) {
/*     */       try {
/* 749 */         LogHome.getLog().warn("MQ主备切换，记录切换日志：队列管理器名称=" + qmName + ",切换源IP=" + switchFromIp + ",切换目标IP=" + switchToIp);
/*     */ 
/* 751 */         IMQSwitchHandler handler = MQSwitchManager.getInstance().getSwitchHandler();
/* 752 */         if (handler != null)
/* 753 */           handler.doSwitch(qmName, switchFromIp, switchToIp);
/*     */       }
/*     */       catch (Exception ex) {
/* 756 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CheckMQQConnectThread extends Thread
/*     */   {
/*     */     public CheckMQQConnectThread(String threadName)
/*     */     {
/* 605 */       super();
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true)
/*     */         try {
/* 612 */           Iterator iterator = MsgBusManager.this.producerDestinations.values().iterator();
/* 613 */           if (iterator.hasNext()) {
/* 614 */             MsgDestination dest = (MsgDestination)iterator.next();
/* 615 */             if (!dest.isConnect()) {
/* 616 */               if (LogHome.getLog().isDebugEnabled()) {
/* 617 */                 LogHome.getLog().debug("MsgDestination[" + dest.getDestinationName() + "]断连，尝试重新连接");
/*     */               }
/* 619 */               dest.setProducer(null);
/* 620 */               dest.reconnect();
/*     */             }
/* 622 */             continue;
/*     */           }
/* 624 */           iterator = MsgBusManager.this.consumerDestinations.values().iterator();
/* 625 */           if (iterator.hasNext()) {
/* 626 */             MsgDestination dest = (MsgDestination)iterator.next();
/* 627 */             if (!dest.isConnect()) {
/* 628 */               if (LogHome.getLog().isDebugEnabled()) {
/* 629 */                 LogHome.getLog().debug("MsgDestination[" + dest.getDestinationName() + "]断连，尝试重新连接");
/*     */               }
/* 631 */               dest.reconnect();
/* 632 */               if (dest.isConnect()) {
/* 633 */                 LogHome.getLog().info("MQ队列/主题断连后连接，重新注册监听" + dest.getDestinationName() + "," + dest.destinationId);
/* 634 */                 ISimpleMsgListener l = MsgBusManager.this.getListenerById(dest.getDestinationId());
/* 635 */                 if (l != null) {
/* 636 */                   MsgBusManager.this.addMsgListener(dest, dest.getDestinationName(), dest.getSelector(), l);
/*     */                 }
/*     */ 
/* 639 */                 MessageListener jmsListener = MsgBusManager.this.getJmsListenerById(dest.getDestinationId());
/* 640 */                 if (jmsListener != null) {
/* 641 */                   MsgBusManager.this.addMsgListener(dest, dest.getDestinationName(), dest.getSelector(), jmsListener);
/*     */                 }
/*     */               }
/*     */             }
/* 645 */             continue;
/*     */           }
/*     */         } catch (Exception ex) {
/* 648 */           LogHome.getLog().error("尝试重新连接MQ出错：", ex);
/*     */         } finally {
/*     */           try {
/* 651 */             sleep(5000L);
/*     */           } catch (Exception ex) {
/* 653 */             LogHome.getLog().error(ex);
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MsgBusManager
 * JD-Core Version:    0.6.0
 */