/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ public abstract interface ISessionListener
/*    */ {
/*    */   public abstract void notify(String paramString1, String paramString2, SessionStatus paramSessionStatus);
/*    */ 
/*    */   public static enum SessionStatus
/*    */   {
/* 21 */     CREATE, CLOSE;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.ISessionListener
 * JD-Core Version:    0.6.0
 */