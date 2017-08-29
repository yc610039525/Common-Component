/*    */ package com.boco.raptor.common.message.xrpc;
/*    */ 
/*    */ import com.boco.raptor.bo.ibo.core.IXrpcMsgServiceBO;
/*    */ import com.boco.raptor.common.message.IMessage;
/*    */ import com.boco.raptor.common.message.IMessagePublisher;
/*    */ import com.boco.raptor.common.message.MsgServiceTypeEnum;
/*    */ import com.boco.transnms.common.dto.base.BoActionContext;
/*    */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*    */ import java.util.List;
/*    */ 
/*    */ public class XrpcMsgPublisher
/*    */   implements IMessagePublisher
/*    */ {
/* 14 */   private String topicName = "topic/*";
/* 15 */   private boolean active = true;
/*    */ 
/*    */   public XrpcMsgPublisher() {
/*    */   }
/*    */   public XrpcMsgPublisher(String topicName) {
/* 20 */     this.topicName = topicName;
/*    */   }
/*    */ 
/*    */   public void initPublisher() throws Exception {
/*    */   }
/*    */ 
/*    */   public MsgServiceTypeEnum getMsgServiceType() {
/* 27 */     return MsgServiceTypeEnum.XRPC;
/*    */   }
/*    */ 
/*    */   public void publishMessage(List<String> sessionIds, IMessage msg) throws Exception {
/* 31 */     if (isActive())
/* 32 */       getXrpcMsgManagerBO().addMessage(new BoActionContext("XrpcMsgServiceBO"), sessionIds, msg);
/*    */   }
/*    */ 
/*    */   public String getTopicName()
/*    */   {
/* 37 */     return this.topicName;
/*    */   }
/*    */ 
/*    */   public boolean isActive() {
/* 41 */     return this.active;
/*    */   }
/*    */ 
/*    */   private IXrpcMsgServiceBO getXrpcMsgManagerBO() {
/* 45 */     return (IXrpcMsgServiceBO)BoHomeFactory.getInstance().getBO(IXrpcMsgServiceBO.class);
/*    */   }
/*    */ 
/*    */   public void setActive(boolean active) {
/* 49 */     this.active = active;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.xrpc.XrpcMsgPublisher
 * JD-Core Version:    0.6.0
 */