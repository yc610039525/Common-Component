/*     */ package com.boco.raptor.bo.core;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.bo.ibo.core.IXrpcMsgServiceBO;
/*     */ import com.boco.raptor.common.message.GenericMessage;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.AbstractBO;
/*     */ import com.boco.transnms.server.bo.base.StateBO;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ @StateBO
/*     */ public class XrpcMsgServiceBO extends AbstractBO
/*     */   implements IXrpcMsgServiceBO
/*     */ {
/*     */   private static final int MAX_MSG_TIMEOUT_MSEC = 180000;
/*     */   private static final int WAIT_MSG_TIMEOUT_SEC = 60;
/*  43 */   private static final String CLOSE_SESSION_MSG_SOURCE = XrpcMsgServiceBO.class.getName();
/*     */ 
/*  45 */   private Map<String, SubscriberSession> sessionTable = new ConcurrentHashMap();
/*     */ 
/*     */   public void initBO()
/*     */     throws Exception
/*     */   {
/*  52 */     Thread thread = new SessionCheckThread(null);
/*  53 */     thread.start();
/*     */   }
/*     */ 
/*     */   public void addSession(IBoActionContext context, String sessionId) {
/*  57 */     synchronized (this.sessionTable) {
/*  58 */       if (!this.sessionTable.containsKey(sessionId))
/*  59 */         this.sessionTable.put(sessionId, new SubscriberSession(null));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void delSession(IBoActionContext context, String sessionId)
/*     */   {
/*  65 */     LogHome.getLog().info("关闭消息会话：sessionId=" + sessionId);
/*     */     try {
/*  67 */       SubscriberSession subSession = null;
/*  68 */       synchronized (this.sessionTable) {
/*  69 */         subSession = (SubscriberSession)this.sessionTable.remove(sessionId);
/*     */       }
/*  71 */       if (subSession != null) {
/*  72 */         subSession.getMsgQuery().clear();
/*  73 */         subSession.getMsgQuery().put(creatCloseMessage());
/*     */       }
/*     */     } catch (Exception ex) {
/*  76 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static IMessage creatCloseMessage()
/*     */   {
/*  82 */     GenericMessage closeMsg = new GenericMessage("");
/*  83 */     closeMsg.setSourceName(CLOSE_SESSION_MSG_SOURCE);
/*  84 */     return closeMsg;
/*     */   }
/*     */ 
/*     */   private static boolean isCloseMessage(IMessage msg) {
/*  88 */     return (msg.getSourceName() != null) && (CLOSE_SESSION_MSG_SOURCE.equals(msg.getSourceName()));
/*     */   }
/*     */ 
/*     */   public void addMessage(IBoActionContext context, List<String> sessionIds, IMessage msg) {
/*  92 */     for (String sessionId : sessionIds)
/*     */       try {
/*  94 */         SubscriberSession subSession = (SubscriberSession)this.sessionTable.get(sessionId);
/*  95 */         if (subSession != null) {
/*  96 */           BlockingQueue msgQuery = subSession.getMsgQuery();
/*  97 */           msgQuery.put(msg);
/*     */         }
/*     */       } catch (Throwable ex) {
/* 100 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public List<IMessage> getMessages(IBoActionContext context, String sessionId) throws UserException
/*     */   {
/* 106 */     List msgs = new ArrayList();
/*     */     try
/*     */     {
/* 109 */       SubscriberSession subSession = (SubscriberSession)this.sessionTable.get(sessionId);
/* 110 */       if (subSession == null) {
/* 111 */         throw new UserException(sessionId + "会话已经关闭 ！");
/*     */       }
/*     */ 
/* 114 */       subSession.refreshTime();
/* 115 */       BlockingQueue msgQuery = subSession.getMsgQuery();
/* 116 */       if (msgQuery.size() > 0) {
/* 117 */         for (int i = 0; i < msgQuery.size(); i++) {
/* 118 */           IMessage msg = (IMessage)msgQuery.poll(60L, TimeUnit.SECONDS);
/* 119 */           if ((msg != null) && (!isCloseMessage(msg)))
/* 120 */             msgs.add(msg);
/*     */         }
/*     */       }
/*     */       else {
/* 124 */         IMessage msg = (IMessage)msgQuery.poll(60L, TimeUnit.SECONDS);
/* 125 */         if ((msg != null) && (!isCloseMessage(msg)))
/* 126 */           msgs.add(msg);
/*     */       }
/*     */     }
/*     */     catch (Throwable ex) {
/* 130 */       LogHome.getLog().error("", ex);
/* 131 */       throw new UserException("获取消息事件失败 ！");
/*     */     }
/*     */ 
/* 134 */     return msgs;
/*     */   }
/*     */ 
/*     */   private IMessageServiceBO getMessageServiceBO() {
/* 138 */     return (IMessageServiceBO)super.getBO(IMessageServiceBO.class);
/*     */   }
/*     */ 
/*     */   private class SessionCheckThread extends Thread
/*     */   {
/*     */     private SessionCheckThread()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true)
/*     */         try
/*     */         {
/* 162 */           Thread.sleep(180000L);
/* 163 */           String[] sessionIds = null;
/* 164 */           synchronized (XrpcMsgServiceBO.this.sessionTable) {
/* 165 */             sessionIds = new String[XrpcMsgServiceBO.this.sessionTable.size()];
/* 166 */             XrpcMsgServiceBO.this.sessionTable.keySet().toArray(sessionIds);
/*     */           }
/* 168 */           String[] arr$ = sessionIds; int len$ = arr$.length; int i$ = 0; if (i$ >= len$) continue; String sessionId = arr$[i$];
/* 169 */           XrpcMsgServiceBO.SubscriberSession subSession = (XrpcMsgServiceBO.SubscriberSession)XrpcMsgServiceBO.this.sessionTable.get(sessionId);
/* 170 */           if ((subSession != null) && (XrpcMsgServiceBO.SubscriberSession.access$500(subSession) > 180000L)) {
/* 171 */             LogHome.getLog().warn("消息会话[sessionId=" + sessionId + "]: 超时清除会话 ！");
/* 172 */             XrpcMsgServiceBO.this.sessionTable.remove(sessionId);
/* 173 */             XrpcMsgServiceBO.SubscriberSession.access$200(subSession).clear();
/* 174 */             XrpcMsgServiceBO.this.getMessageServiceBO().delSession(new BoActionContext("MessageServiceBO"), sessionId);
/*     */           }
/* 168 */           i$++; continue;
/*     */ 
/* 179 */           continue;
/*     */         }
/*     */         catch (Throwable ex)
/*     */         {
/* 178 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SubscriberSession
/*     */   {
/* 142 */     private long refreshTime = System.currentTimeMillis();
/* 143 */     private BlockingQueue<IMessage> blockMsgQuery = new LinkedBlockingQueue();
/*     */ 
/*     */     private void refreshTime() {
/* 146 */       this.refreshTime = System.currentTimeMillis();
/*     */     }
/*     */ 
/*     */     private long getRefreshTimeInterval() {
/* 150 */       return System.currentTimeMillis() - this.refreshTime;
/*     */     }
/*     */ 
/*     */     private BlockingQueue<IMessage> getMsgQuery() {
/* 154 */       return this.blockMsgQuery;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.core.XrpcMsgServiceBO
 * JD-Core Version:    0.6.0
 */