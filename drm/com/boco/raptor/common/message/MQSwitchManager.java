/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ public class MQSwitchManager
/*    */ {
/*    */   private IMQSwitchHandler switchHandler;
/*  6 */   private static MQSwitchManager instance = new MQSwitchManager();
/*    */ 
/*    */   public static MQSwitchManager getInstance()
/*    */   {
/* 12 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setSwitchHandler(IMQSwitchHandler handler) {
/* 16 */     this.switchHandler = handler;
/*    */   }
/*    */ 
/*    */   public IMQSwitchHandler getSwitchHandler()
/*    */   {
/* 21 */     return this.switchHandler;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MQSwitchManager
 * JD-Core Version:    0.6.0
 */