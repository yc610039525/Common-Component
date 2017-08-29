/*    */ package com.boco.raptor.bo.core;
/*    */ 
/*    */ import com.boco.raptor.bo.ibo.core.IMsgBusServiceBO;
/*    */ import com.boco.raptor.common.message.IMessage;
/*    */ import com.boco.raptor.common.message.ISimpleMsgListener;
/*    */ import com.boco.raptor.common.message.MsgBusManager;
/*    */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*    */ import com.boco.transnms.server.bo.base.AbstractBO;
/*    */ import com.boco.transnms.server.bo.base.StateLessBO;
/*    */ 
/*    */ @StateLessBO(serverName="COMMON")
/*    */ public class MsgBusServiceBO extends AbstractBO
/*    */   implements IMsgBusServiceBO
/*    */ {
/*    */   public void initBO()
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sendMessage(IBoActionContext context, IMessage msg)
/*    */   {
/* 34 */     MsgBusManager.getInstance().sendMessage(msg);
/*    */   }
/*    */ 
/*    */   public void addMsgListener(IBoActionContext context, String topicName, String selector, ISimpleMsgListener msgListener) throws Exception
/*    */   {
/* 39 */     MsgBusManager.getInstance().addMsgListener(topicName, selector, msgListener);
/*    */   }
/*    */ 
/*    */   public void removeMsgListener(IBoActionContext context, ISimpleMsgListener msgListener) throws Exception {
/* 43 */     MsgBusManager.getInstance().removeMsgListener(msgListener);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.core.MsgBusServiceBO
 * JD-Core Version:    0.6.0
 */