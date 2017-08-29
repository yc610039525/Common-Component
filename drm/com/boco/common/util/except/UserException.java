/*    */ package com.boco.common.util.except;
/*    */ 
/*    */ public class UserException extends RuntimeException
/*    */ {
/*    */   private int errorCode;
/*    */ 
/*    */   public UserException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UserException(String message)
/*    */   {
/* 31 */     super(message);
/*    */   }
/*    */ 
/*    */   public UserException(String message, int errorCode) {
/* 35 */     super(message);
/* 36 */     this.errorCode = errorCode;
/*    */   }
/*    */ 
/*    */   public UserException(String message, Throwable cause) {
/* 40 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public UserException(Throwable cause) {
/* 44 */     super(cause);
/*    */   }
/*    */ 
/*    */   public void setErrorCode(int errorCode) {
/* 48 */     this.errorCode = errorCode;
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 52 */     return this.errorCode;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.except.UserException
 * JD-Core Version:    0.6.0
 */