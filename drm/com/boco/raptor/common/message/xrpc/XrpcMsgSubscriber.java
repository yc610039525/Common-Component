/*     */ package com.boco.raptor.common.message.xrpc;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.bo.ibo.core.IMessageServiceBO;
/*     */ import com.boco.raptor.bo.ibo.core.IXrpcMsgServiceBO;
/*     */ import com.boco.raptor.common.message.AbstractMsgSubscriber;
/*     */ import com.boco.raptor.common.message.GenericMessage;
/*     */ import com.boco.raptor.common.message.IMessageFilter;
/*     */ import com.boco.raptor.common.message.IMessageListener;
/*     */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import java.io.OptionalDataException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class XrpcMsgSubscriber extends AbstractMsgSubscriber
/*     */ {
/*     */   private static final int RECONNECT_TIMEOUT = 5000;
/*     */   private static final int CLOSE_TIMES = 60;
/*  26 */   public boolean isSessionClose = true;
/*     */   public static final String SESSION_CLOSE_MESSAGE = "SESSION_CLOSE_MESSAGE";
/*     */ 
/*     */   public XrpcMsgSubscriber()
/*     */   {
/*     */   }
/*     */ 
/*     */   public XrpcMsgSubscriber(String topicName)
/*     */   {
/*  33 */     setTopicName(topicName);
/*     */   }
/*     */ 
/*     */   public MsgServiceTypeEnum getMsgServieType() {
/*  37 */     return MsgServiceTypeEnum.XRPC;
/*     */   }
/*     */ 
/*     */   public void createSession(IMessageFilter filter) {
/*  41 */     if (this.isSessionClose)
/*     */       try {
/*  43 */         BoActionContext context = new BoActionContext("MessageServiceBO");
/*  44 */         String sessionId = getMessageServiceBO().addSession(context, MsgServiceTypeEnum.XRPC, getTopicName(), filter);
/*  45 */         super.setSessionId(sessionId);
/*  46 */         createXrpcSession();
/*     */       } catch (Exception ex) {
/*  48 */         LogHome.getLog().error("", ex);
/*  49 */         throw new UserException(ex.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   public void createSession(String filterTemplateName, Serializable filterPara)
/*     */   {
/*     */     try {
/*  56 */       if (this.isSessionClose) {
/*  57 */         IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  58 */         String sessionId = getMessageServiceBO().addSession(context, MsgServiceTypeEnum.XRPC, getTopicName(), filterTemplateName, filterPara);
/*  59 */         super.setSessionId(sessionId);
/*  60 */         createXrpcSession();
/*     */       }
/*     */     } catch (Exception ex) {
/*  63 */       LogHome.getLog().error("", ex);
/*  64 */       throw new UserException(ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createXrpcSession() {
/*     */     try {
/*  70 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*  71 */       getXrpcMsgServiceBO().addSession(context, super.getSessionId());
/*     */     } catch (Exception ex) {
/*  73 */       LogHome.getLog().error(ex);
/*     */     }
/*     */ 
/*  76 */     this.isSessionClose = false;
/*  77 */     Thread msgThread = new Thread()
/*     */     {
/*     */       public void run() {
/*  80 */         XrpcMsgSubscriber.this.handleMessages();
/*     */       }
/*     */     };
/*  83 */     msgThread.start();
/*     */   }
/*     */ 
/*     */   public void closeSession() {
/*     */     try {
/*  88 */       if (getSessionId() != null) {
/*  89 */         this.isSessionClose = true;
/*  90 */         IBoActionContext context = new BoActionContext("XrpcMsgServiceBO");
/*  91 */         getXrpcMsgServiceBO().delSession(context, super.getSessionId());
/*  92 */         context = new BoActionContext("MessageServiceBO");
/*  93 */         getMessageServiceBO().delSession(context, super.getSessionId());
/*     */       }
/*     */     } catch (Throwable ex) {
/*  96 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetFilterPara(Object filterPara) {
/* 101 */     if (!this.isSessionClose) {
/* 102 */       IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */       try {
/* 104 */         getMessageServiceBO().modifyFilterPara(context, super.getSessionId(), filterPara);
/*     */       } catch (Exception ex) {
/* 106 */         LogHome.getLog().error(ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleMessages() {
/* 112 */     int closeCount = 0;
/* 113 */     while (!this.isSessionClose)
/*     */       try {
/* 115 */         IBoActionContext context = new BoActionContext("XrpcMsgServiceBO");
/* 116 */         msgs = getXrpcMsgServiceBO().getMessages(context, super.getSessionId());
/* 117 */         closeCount = 0;
/* 118 */         if ((msgs == null) || (msgs.size() != 0))
/* 119 */           for (IMessageListener listener : super.getMsgListeners())
/*     */             try {
/* 121 */               listener.notify(msgs);
/*     */             } catch (Throwable ex) {
/* 123 */               LogHome.getLog().error("", ex);
/*     */             }
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/*     */         List msgs;
/* 127 */         if (((ex instanceof UserException)) && 
/* 128 */           ((ex.getCause() instanceof OptionalDataException))) {
/* 129 */           LogHome.getLog().error("", ex);
/* 130 */           continue;
/*     */         }
/*     */ 
/* 133 */         LogHome.getLog().error("", ex);
/*     */         List msgs;
/* 134 */         if (closeCount > 60) {
/* 135 */           this.isSessionClose = true;
/* 136 */           LogHome.getLog().warn("Subscriber连接超时，SessionId=" + getSessionId() + ", 会话关闭 ！");
/* 137 */           msgs = new ArrayList(1);
/* 138 */           GenericMessage msg = new GenericMessage("topic/AlarmTopic");
/* 139 */           msg.setSourceName("SESSION_CLOSE_MESSAGE");
/* 140 */           msgs.add(msg);
/* 141 */           for (IMessageListener listener : super.getMsgListeners())
/*     */             try {
/* 143 */               listener.notify(msgs);
/*     */             } catch (Throwable ex1) {
/* 145 */               LogHome.getLog().error("", ex1);
/*     */             }
/*     */         }
/*     */         else {
/* 149 */           closeCount++;
/*     */           try {
/* 151 */             Thread.sleep(5000L);
/*     */           } catch (Exception ex1) {
/* 153 */             LogHome.getLog().error("", ex1);
/*     */           }
/*     */         }
/* 156 */         LogHome.getLog().error("客户端获取告警失败，进行第" + closeCount + "重连");
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setSessionActive(boolean isActive)
/*     */   {
/* 162 */     IBoActionContext context = new BoActionContext("MessageServiceBO");
/*     */     try {
/* 164 */       getMessageServiceBO().modifySessionActive(context, super.getSessionId(), new Boolean(isActive));
/*     */     } catch (Exception ex) {
/* 166 */       LogHome.getLog().error(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static IMessageServiceBO getMessageServiceBO()
/*     */   {
/* 172 */     return (IMessageServiceBO)BoHomeFactory.getInstance().getBO(IMessageServiceBO.class);
/*     */   }
/*     */ 
/*     */   public static IXrpcMsgServiceBO getXrpcMsgServiceBO() {
/* 176 */     return (IXrpcMsgServiceBO)BoHomeFactory.getInstance().getBO(IXrpcMsgServiceBO.class);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.xrpc.XrpcMsgSubscriber
 * JD-Core Version:    0.6.0
 */