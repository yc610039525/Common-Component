/*     */ package com.boco.raptor.bo.core;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.id.IdHexGenerator;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.IMessageFilter;
/*     */ import com.boco.raptor.common.message.IMessagePublisher;
/*     */ import com.boco.raptor.common.message.ISessionListener;
/*     */ import com.boco.raptor.common.message.ISessionListener.SessionStatus;
/*     */ import com.boco.raptor.common.message.MessageSession;
/*     */ import com.boco.raptor.common.message.MsgFilterFactory;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.AbstractBO;
/*     */ import com.boco.transnms.server.bo.base.StateLessBO;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.Semaphore;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ @StateLessBO(serverName="COMMON", initByAllServer=true)
/*     */ public class MessageServiceBO extends AbstractBO
/*     */   implements IMessageServiceBO
/*     */ {
/*  52 */   private final Map<String, IMessagePublisher> msgPublishers = new ConcurrentHashMap();
/*  53 */   private final Map<String, MessageSession> msgSessions = new ConcurrentHashMap();
/*  54 */   private final Map<String, TopicPublisher> topicPublishers = new ConcurrentHashMap();
/*  55 */   private final List<ISessionListener> sessionListeners = new Vector();
/*  56 */   private boolean isServiceActive = true;
/*     */ 
/*  58 */   private int checkMsgServiceNum = 100;
/*  59 */   private int checkMsgServiceTime = 120;
/*  60 */   private int maxMsgHandleTime = 50;
/*  61 */   private Map<String, Integer> maxTopicThreadTable = new HashMap();
/*     */ 
/*  63 */   private long poolTotalRecvEleCount = 0L;
/*  64 */   private long serviceTotalHandledEleCount = 0L;
/*  65 */   private long serviceLastHandleEleCount = 0L;
/*  66 */   private long poolSysStartTime = System.currentTimeMillis();
/*  67 */   private long poolLastCheckTime = System.currentTimeMillis();
/*     */ 
/*     */   public MessageServiceBO() {
/*  70 */     super("MessageServiceBO");
/*     */   }
/*     */ 
/*     */   public MessageServiceBO(int checkMsgServiceNum, int checkMsgServiceTime, int maxMsgHandleTime) {
/*  74 */     super("MessageServiceBO");
/*  75 */     this.checkMsgServiceNum = checkMsgServiceNum;
/*  76 */     this.checkMsgServiceTime = checkMsgServiceTime;
/*  77 */     this.maxMsgHandleTime = maxMsgHandleTime;
/*     */   }
/*     */ 
/*     */   public void initBO() throws Exception {
/*  81 */     this.checkMsgServiceNum = TnmsDrmCfg.getInstance().getCheckMsgServiceNum();
/*  82 */     this.checkMsgServiceTime = TnmsDrmCfg.getInstance().getCheckMsgServiceTime();
/*  83 */     this.maxMsgHandleTime = TnmsDrmCfg.getInstance().getMaxMsgHandleTime();
/*  84 */     Map msgServiceTable = TnmsDrmCfg.getInstance().getMsgServiceTable();
/*  85 */     if (msgServiceTable != null) {
/*  86 */       Iterator it = msgServiceTable.keySet().iterator();
/*  87 */       while (it.hasNext()) {
/*  88 */         String topicName = (String)it.next();
/*  89 */         this.maxTopicThreadTable.put(topicName, Integer.valueOf(Integer.parseInt((String)msgServiceTable.get(topicName))));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String addSession(IBoActionContext context, MsgServiceTypeEnum msgServiceType, String topicName, IMessageFilter filter) throws UserException
/*     */   {
/*  96 */     if (!this.isServiceActive) {
/*  97 */       throw new UserException("消息服务没有激活 ！");
/*     */     }
/*     */ 
/* 100 */     if ((topicName == null) || (topicName.trim().length() == 0)) {
/* 101 */       throw new UserException("注册TopicName是必须的 ！");
/*     */     }
/*     */ 
/* 104 */     String sessionId = createSessionId(msgServiceType);
/*     */     try {
/* 106 */       IMessagePublisher publisher = getPublisher(msgServiceType, topicName);
/* 107 */       if (publisher == null) {
/* 108 */         throw new UserException("消息会话参数错误");
/*     */       }
/*     */ 
/* 111 */       if (filter != null) {
/* 112 */         filter.setSessionId(sessionId);
/*     */       }
/*     */ 
/* 115 */       MessageSession msgSession = new MessageSession(sessionId, topicName, publisher, filter);
/* 116 */       this.msgSessions.put(sessionId, msgSession);
/* 117 */       notifySessionListener(msgSession, ISessionListener.SessionStatus.CREATE);
/*     */ 
/* 119 */       if (this.topicPublishers.get(topicName) == null) {
/* 120 */         int maxTopicThread = 1;
/* 121 */         if (this.maxTopicThreadTable.containsKey(topicName)) {
/* 122 */           maxTopicThread = ((Integer)this.maxTopicThreadTable.get(topicName)).intValue();
/*     */         }
/* 124 */         TopicPublisher thread = new TopicPublisher(topicName, maxTopicThread, null);
/* 125 */         this.topicPublishers.put(topicName, thread);
/*     */       }
/*     */     } catch (Exception ex) {
/* 128 */       LogHome.getLog().error("", ex);
/* 129 */       throw new UserException(ex.getMessage());
/*     */     }
/* 131 */     return sessionId;
/*     */   }
/*     */ 
/*     */   public String addSession(IBoActionContext context, MsgServiceTypeEnum msgServiceType, String topicName, String filterTemplateName, Object filterPara) throws UserException
/*     */   {
/* 136 */     if (!this.isServiceActive) {
/* 137 */       throw new UserException("消息服务没有激活 ！");
/*     */     }
/*     */ 
/* 140 */     IMessageFilter filter = null;
/* 141 */     if ((filterTemplateName != null) && (filterTemplateName.trim().length() > 0)) {
/* 142 */       filter = MsgFilterFactory.getInstance().createMsgFilter(filterTemplateName, filterPara);
/*     */     }
/*     */ 
/* 145 */     return addSession(context, msgServiceType, topicName, filter);
/*     */   }
/*     */ 
/*     */   public void delSession(IBoActionContext context, String sessionId) {
/* 149 */     MessageSession session = (MessageSession)this.msgSessions.remove(sessionId);
/* 150 */     if (session != null)
/* 151 */       notifySessionListener(session, ISessionListener.SessionStatus.CLOSE);
/*     */   }
/*     */ 
/*     */   public void modifyFilterPara(IBoActionContext context, String sessionId, Object filterPara)
/*     */   {
/* 156 */     if (this.isServiceActive) {
/* 157 */       MessageSession msgSession = (MessageSession)this.msgSessions.get(sessionId);
/* 158 */       if ((msgSession != null) && (msgSession.getMsgFilter() != null))
/* 159 */         msgSession.getMsgFilter().setFilterPara(filterPara);
/*     */     }
/*     */   }
/*     */ 
/*     */   public IMessageFilter getMsgFilter(IBoActionContext context, String sessionId)
/*     */   {
/* 165 */     IMessageFilter msgFilter = null;
/* 166 */     if (this.isServiceActive) {
/* 167 */       MessageSession msgSession = (MessageSession)this.msgSessions.get(sessionId);
/* 168 */       if ((msgSession != null) && (msgSession.getMsgFilter() != null)) {
/* 169 */         msgFilter = msgSession.getMsgFilter();
/*     */       }
/*     */     }
/* 172 */     return msgFilter;
/*     */   }
/*     */ 
/*     */   public void addMessage(IBoActionContext context, IMessage msg) {
/* 176 */     if ((msg.getTopicName() == null) || (msg.getTopicName().trim().length() == 0)) {
/* 177 */       throw new UserException("消息topicName不能为空 ！");
/*     */     }
/*     */ 
/* 180 */     TopicPublisher topicPublisher = (TopicPublisher)this.topicPublishers.get(msg.getTopicName());
/* 181 */     if (topicPublisher != null)
/* 182 */       topicPublisher.publishTopicMessage(msg);
/*     */   }
/*     */ 
/*     */   public synchronized void checkMesageService(boolean isMantory)
/*     */   {
/* 187 */     long serviceHandledCount = this.serviceTotalHandledEleCount - this.serviceLastHandleEleCount;
/* 188 */     if (serviceHandledCount <= 0L) return;
/*     */ 
/* 190 */     if ((isMantory) || ((this.checkMsgServiceNum > 0) && (serviceHandledCount >= this.checkMsgServiceNum)) || ((this.checkMsgServiceTime > 0) && (System.currentTimeMillis() - this.poolLastCheckTime >= this.checkMsgServiceTime * 1000)))
/*     */     {
/* 192 */       long sysTimeGap = System.currentTimeMillis() - this.poolLastCheckTime;
/* 193 */       LogHome.getLog().warn("消息服务统计：总系统时间(s)＝" + (System.currentTimeMillis() - this.poolSysStartTime) / 1000L + ", 系统时间(s)=" + sysTimeGap / 1000L + ", 接收消息总数=" + this.poolTotalRecvEleCount + ", 已处理消息总数=" + this.serviceTotalHandledEleCount + ", 本次处理消息数=" + serviceHandledCount + ", 未处理消息总数=" + (this.poolTotalRecvEleCount - this.serviceTotalHandledEleCount) + "，消息平均系统时间(ms)=" + sysTimeGap / serviceHandledCount + "   --------------------------------");
/*     */ 
/* 198 */       this.serviceLastHandleEleCount = this.serviceTotalHandledEleCount;
/* 199 */       Iterator it = this.topicPublishers.values().iterator();
/* 200 */       while (it.hasNext()) {
/* 201 */         ((TopicPublisher)it.next()).checkPublisher();
/*     */       }
/* 203 */       this.poolLastCheckTime = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void publishMessage(FilteredMessage filteredMsg) {
/* 208 */     IMessage msg = filteredMsg.message;
/* 209 */     Map publishSessions = filteredMsg.publishSessions;
/* 210 */     if ((this.isServiceActive) && (this.msgSessions.size() > 0)) {
/* 211 */       Iterator it = publishSessions.keySet().iterator();
/* 212 */       while (it.hasNext()) {
/* 213 */         IMessagePublisher publisher = (IMessagePublisher)it.next();
/* 214 */         List sessionIds = (List)publishSessions.get(publisher);
/*     */         try {
/* 216 */           publisher.publishMessage(sessionIds, msg);
/*     */         } catch (Throwable ex) {
/* 218 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Map<IMessagePublisher, List<String>> getMsgPublishSessions(IMessage msg) {
/* 225 */     Map publishSessions = new ConcurrentHashMap();
/* 226 */     if ((msg.getTargetId() == null) || (msg.getTargetId().length() == 0)) {
/* 227 */       Iterator it = this.msgSessions.keySet().iterator();
/* 228 */       while (it.hasNext()) {
/* 229 */         MessageSession msgSession = (MessageSession)this.msgSessions.get(it.next());
/* 230 */         if ((msgSession.getTopicName().equals(msg.getTopicName())) && (msgSession.isActive())) {
/* 231 */           IMessageFilter filter = msgSession.getMsgFilter();
/* 232 */           if (filter != null) {
/* 233 */             if (filter.isMsgPublish(msg))
/* 234 */               putSessionIds(publishSessions, msgSession.getPublisher(), msgSession.getSessionId());
/*     */           }
/*     */           else
/* 237 */             putSessionIds(publishSessions, msgSession.getPublisher(), msgSession.getSessionId());
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 242 */       MessageSession msgSession = (MessageSession)this.msgSessions.get(msg.getTargetId());
/* 243 */       if ((msgSession != null) && (msgSession.isActive())) {
/* 244 */         putSessionIds(publishSessions, msgSession.getPublisher(), msg.getTargetId());
/*     */       }
/*     */     }
/* 247 */     return publishSessions;
/*     */   }
/*     */ 
/*     */   private void putSessionIds(Map<IMessagePublisher, List<String>> publishSessions, IMessagePublisher publisher, String sessionId)
/*     */   {
/* 252 */     List sessionIds = (List)publishSessions.get(publisher);
/* 253 */     if (sessionIds == null) {
/* 254 */       sessionIds = new ArrayList();
/*     */     }
/* 256 */     sessionIds.add(sessionId);
/* 257 */     publishSessions.put(publisher, sessionIds);
/*     */   }
/*     */ 
/*     */   public void modifyMsgPublishers(List<IMessagePublisher> publishers) {
/* 261 */     if (this.isServiceActive)
/* 262 */       for (IMessagePublisher publish : publishers) {
/* 263 */         String key = getPublisherKey(publish.getMsgServiceType(), publish.getTopicName());
/* 264 */         this.msgPublishers.put(key, publish);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void modifyActive(IBoActionContext context, Boolean isActive)
/*     */   {
/* 270 */     this.isServiceActive = isActive.booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean isActive(IBoActionContext context) {
/* 274 */     return this.isServiceActive;
/*     */   }
/*     */ 
/*     */   private IMessagePublisher getPublisher(MsgServiceTypeEnum serviceType, String topicName) {
/* 278 */     String publisherKey = getPublisherKey(serviceType, topicName);
/* 279 */     IMessagePublisher publisher = (IMessagePublisher)this.msgPublishers.get(publisherKey);
/* 280 */     if (publisher == null) {
/* 281 */       publisherKey = getPublisherKey(serviceType, "topic/*");
/* 282 */       publisher = (IMessagePublisher)this.msgPublishers.get(publisherKey);
/*     */     }
/* 284 */     return publisher;
/*     */   }
/*     */ 
/*     */   private static String getPublisherKey(MsgServiceTypeEnum serviceType, String topicName) {
/* 288 */     return serviceType.name() + "-" + topicName;
/*     */   }
/*     */ 
/*     */   private static boolean isTopicKey(String key, String topicName) {
/* 292 */     String[] keys = key.split("-");
/* 293 */     boolean isTopic = false;
/* 294 */     if (topicName.equals(keys[1])) {
/* 295 */       isTopic = true;
/*     */     }
/* 297 */     return isTopic;
/*     */   }
/*     */ 
/*     */   private String createSessionId(MsgServiceTypeEnum serviceType) {
/* 301 */     return IdHexGenerator.getInstance().generate(serviceType + "MsgSession");
/*     */   }
/*     */ 
/*     */   public void modifyTopicActive(IBoActionContext context, String topicName, Boolean isActive) {
/* 305 */     Iterator it = this.msgPublishers.keySet().iterator();
/* 306 */     while (it.hasNext()) {
/* 307 */       String key = (String)it.next();
/* 308 */       if (isTopicKey(key, topicName)) {
/* 309 */         IMessagePublisher publisher = (IMessagePublisher)this.msgPublishers.get(key);
/* 310 */         if (publisher != null)
/* 311 */           publisher.setActive(isActive.booleanValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isTopicActive(IBoActionContext context, String topicName)
/*     */   {
/* 318 */     boolean isActive = true;
/* 319 */     Iterator it = this.msgPublishers.keySet().iterator();
/* 320 */     while (it.hasNext()) {
/* 321 */       String key = (String)it.next();
/* 322 */       if (isTopicKey(key, topicName)) {
/* 323 */         IMessagePublisher publisher = (IMessagePublisher)this.msgPublishers.get(key);
/* 324 */         if (publisher == null) break;
/* 325 */         isActive = publisher.isActive(); break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 330 */     return isActive;
/*     */   }
/*     */ 
/*     */   public void modifySessionActive(IBoActionContext context, String sessionId, Boolean isActive) {
/* 334 */     MessageSession msgSession = (MessageSession)this.msgSessions.get(sessionId);
/* 335 */     if (msgSession != null)
/* 336 */       msgSession.setActive(isActive.booleanValue());
/*     */   }
/*     */ 
/*     */   public void addSessionListener(ISessionListener sessionListener)
/*     */   {
/* 341 */     if ((sessionListener != null) && (!this.sessionListeners.contains(sessionListener)))
/* 342 */       this.sessionListeners.add(sessionListener);
/*     */   }
/*     */ 
/*     */   public void delSessionListener(ISessionListener sessionListener)
/*     */   {
/* 347 */     if (sessionListener != null)
/* 348 */       this.sessionListeners.remove(sessionListener);
/*     */   }
/*     */ 
/*     */   private void notifySessionListener(MessageSession session, ISessionListener.SessionStatus status)
/*     */   {
/* 353 */     for (int i = 0; i < this.sessionListeners.size(); i++) {
/* 354 */       ISessionListener listener = (ISessionListener)this.sessionListeners.get(i);
/* 355 */       listener.notify(session.getTopicName(), session.getSessionId(), status);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FilteredMessage
/*     */   {
/*     */     private IMessage message;
/*     */     private Map<IMessagePublisher, List<String>> publishSessions;
/*     */ 
/*     */     public FilteredMessage(IMessage message, Map<IMessagePublisher, List<String>> publishSessions)
/*     */     {
/* 493 */       this.message = message;
/* 494 */       this.publishSessions = publishSessions;
/*     */     }
/*     */ 
/*     */     public boolean isFinished() {
/* 498 */       return this.publishSessions != null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TopicPublisher
/*     */   {
/*     */     private final String topicName;
/* 361 */     private final BlockingQueue<IMessage> topicMsgQueue = new LinkedBlockingQueue();
/* 362 */     private final MsgDispatchThread msgDispatchThread = new MsgDispatchThread();
/*     */     private MsgFilterThreadPool msgFilterThreadPool;
/*     */     private final int maxTopicThread;
/* 366 */     private long recvTotalEleCount = 0L;
/* 367 */     private long handledTotalEleCount = 0L;
/* 368 */     private long handledEleCount = 0L;
/*     */ 
/* 370 */     private long msgHandleCpuTime = 0L;
/*     */ 
/*     */     private TopicPublisher(String topicName, int maxMsgThread) {
/* 373 */       this.topicName = topicName;
/* 374 */       this.maxTopicThread = maxMsgThread;
/* 375 */       this.msgDispatchThread.start();
/* 376 */       if (maxMsgThread > 1) {
/* 377 */         this.msgFilterThreadPool = new MsgFilterThreadPool(maxMsgThread, null);
/*     */       }
/* 379 */       LogHome.getLog().info("消息服务：topicName=" + topicName + ", maxMsgThread=" + maxMsgThread + ", 队列启动 !");
/*     */     }
/*     */ 
/*     */     private void checkPublisher() {
/* 383 */       if (this.handledEleCount > 0L) {
/* 384 */         LogHome.getLog().warn("消息服务统计：topicName=" + this.topicName + ", 接收的消息总数=" + this.recvTotalEleCount + ", 已处理消息总数" + this.handledTotalEleCount + ", 未处理消息数=" + this.topicMsgQueue.size() + ", 本次处理消息数=" + this.handledEleCount + ", 平均CPU时间=" + this.msgHandleCpuTime / this.handledEleCount);
/*     */ 
/* 388 */         this.handledEleCount = 0L;
/* 389 */         this.msgHandleCpuTime = 0L;
/*     */       }
/*     */     }
/*     */ 
/*     */     private void publishTopicMessage(IMessage msg) {
/*     */       try {
/* 395 */         this.recvTotalEleCount += 1L;
/* 396 */         this.topicMsgQueue.put(msg);
/*     */       } catch (Exception ex) {
/* 398 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void filterMessage(MessageServiceBO.FilteredMessage filterMsg) {
/* 403 */       long startTime = System.currentTimeMillis();
/*     */ 
/* 405 */       MessageServiceBO.FilteredMessage.access$402(filterMsg, MessageServiceBO.this.getMsgPublishSessions(filterMsg.message));
/*     */ 
/* 407 */       long cpuTime = System.currentTimeMillis() - startTime;
/* 408 */       if (cpuTime > MessageServiceBO.this.maxMsgHandleTime) {
/* 409 */         LogHome.getLog().warn("消息处理大于设定门限: topicName=" + this.topicName + ", 处理时间(ms)=" + cpuTime + ", 开始处理时间=" + TimeFormatHelper.getFormatDate(new Date(startTime), "yyyy-MM-dd HH:mm:ss:SSS") + ", 问题消息：" + filterMsg.message);
/*     */       }
/*     */ 
/* 413 */       this.msgHandleCpuTime += cpuTime;
/* 414 */       MessageServiceBO.access$808(MessageServiceBO.this);
/* 415 */       this.handledTotalEleCount += 1L;
/* 416 */       this.handledEleCount += 1L;
/* 417 */       MessageServiceBO.this.checkMesageService(false);
/*     */     }
/*     */     private class MsgFilterThreadPool {
/* 444 */       private final List<MessageServiceBO.FilteredMessage> filterMsgList = new ArrayList();
/*     */       private final Semaphore filterMsgSema;
/*     */ 
/*     */       private MsgFilterThreadPool(int maxMsgThread) {
/* 448 */         this.filterMsgSema = new Semaphore(maxMsgThread);
/*     */       }
/*     */ 
/*     */       private void putFilterMessage(IMessage msg) {
/*     */         try {
/* 453 */           this.filterMsgSema.acquire();
/* 454 */           MessageServiceBO.FilteredMessage filterMsg = new MessageServiceBO.FilteredMessage(msg, null);
/* 455 */           synchronized (this.filterMsgList) {
/* 456 */             this.filterMsgList.add(filterMsg);
/*     */           }
/* 458 */           Thread filterThread = new Thread(filterMsg) {
/*     */             public void run() {
/* 460 */               MessageServiceBO.TopicPublisher.this.filterMessage(this.val$filterMsg);
/* 461 */               MessageServiceBO.TopicPublisher.MsgFilterThreadPool.this.notifyMsgFiltered();
/*     */             }
/*     */           };
/* 464 */           filterThread.start();
/*     */         } catch (Exception ex) {
/* 466 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */ 
/*     */       private void notifyMsgFiltered() {
/* 471 */         this.filterMsgSema.release();
/* 472 */         publishFilterMsg();
/*     */       }
/*     */ 
/*     */       private void publishFilterMsg() {
/* 476 */         synchronized (this.filterMsgList) {
/* 477 */           for (int i = 0; (i < this.filterMsgList.size()) && 
/* 478 */             (((MessageServiceBO.FilteredMessage)this.filterMsgList.get(0)).isFinished()); i++)
/*     */           {
/* 479 */             MessageServiceBO.this.publishMessage((MessageServiceBO.FilteredMessage)this.filterMsgList.remove(0));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private class MsgDispatchThread extends Thread
/*     */     {
/*     */       public MsgDispatchThread()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         while (true)
/*     */           try
/*     */           {
/* 427 */             IMessage msg = (IMessage)MessageServiceBO.TopicPublisher.this.topicMsgQueue.take();
/* 428 */             if (msg != null) {
/* 429 */               if (MessageServiceBO.TopicPublisher.this.maxTopicThread > 1) {
/* 430 */                 MessageServiceBO.TopicPublisher.this.msgFilterThreadPool.putFilterMessage(msg);
/*     */               } else {
/* 432 */                 MessageServiceBO.FilteredMessage filterMsg = new MessageServiceBO.FilteredMessage(msg, null);
/* 433 */                 MessageServiceBO.TopicPublisher.this.filterMessage(filterMsg);
/* 434 */                 MessageServiceBO.this.publishMessage(filterMsg);
/*     */               }
/*     */ 
/* 438 */               continue;
/*     */             }
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 437 */             LogHome.getLog().info("", ex);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.core.MessageServiceBO
 * JD-Core Version:    0.6.0
 */