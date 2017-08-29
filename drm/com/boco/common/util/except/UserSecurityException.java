/*   */ package com.boco.common.util.except;
/*   */ 
/*   */ public class UserSecurityException extends UserException
/*   */ {
/*   */   public UserSecurityException()
/*   */   {
/* 5 */     super("对不起，您没有操作权限 ！", 10);
/*   */   }
/*   */   public UserSecurityException(String msg) {
/* 8 */     super(msg, 10);
/*   */   }
/*   */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.except.UserSecurityException
 * JD-Core Version:    0.6.0
 */