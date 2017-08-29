/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.raptor.common.message.GenericMessage;
/*    */ import com.boco.transnms.common.dto.base.DataObjectList;
/*    */ 
/*    */ public class CachedDtoMessage extends GenericMessage
/*    */ {
/*    */   private final DTO_MSG_TYPE dtoMsgType;
/*    */   private DataObjectList msgDtos;
/*    */ 
/*    */   public CachedDtoMessage(String topicName, DTO_MSG_TYPE dtoMsgType)
/*    */   {
/* 12 */     super(topicName);
/* 13 */     this.dtoMsgType = dtoMsgType;
/*    */   }
/*    */ 
/*    */   public DTO_MSG_TYPE getDtoMsgType() {
/* 17 */     return this.dtoMsgType;
/*    */   }
/*    */ 
/*    */   public String getCacheClassName() {
/* 21 */     String className = "";
/* 22 */     if (this.msgDtos.size() > 0) {
/* 23 */       className = this.msgDtos.getElementClassName();
/*    */     }
/* 25 */     return className;
/*    */   }
/*    */ 
/*    */   public void setMsgDtos(DataObjectList msgDtos) {
/* 29 */     this.msgDtos = msgDtos;
/*    */   }
/*    */ 
/*    */   public DataObjectList getMsgDtos() {
/* 33 */     return this.msgDtos;
/*    */   }
/*    */ 
/*    */   public static enum DTO_MSG_TYPE
/*    */   {
/*  7 */     CREATE, UPDATE, DELETE;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.CachedDtoMessage
 * JD-Core Version:    0.6.0
 */