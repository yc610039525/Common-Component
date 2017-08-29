/*    */ package com.boco.transnms.common.dto.helper;
/*    */ 
/*    */ import com.boco.transnms.common.dto.base.AbstractDoHelper;
/*    */ import java.sql.Timestamp;
/*    */ 
/*    */ public class LoginLogHelper extends AbstractDoHelper
/*    */ {
/*  8 */   private static final LoginLogHelper instance = new LoginLogHelper();
/*    */ 
/*    */   private LoginLogHelper() {
/* 11 */     super(false);
/*    */   }
/*    */ 
/*    */   public static LoginLogHelper getInstance() {
/* 15 */     return instance;
/*    */   }
/*    */ 
/*    */   public static String getDboClassName() {
/* 19 */     return "LOGIN_LOG";
/*    */   }
/*    */ 
/*    */   protected void putAttrTypes() {
/* 23 */     putAttrType("LOGIN_TYPE", Long.TYPE);
/* 24 */     putAttrType("IP_ADDRESS", String.class);
/* 25 */     putAttrType("MACHINE_NAME", String.class);
/* 26 */     putAttrType("CUID", String.class);
/* 27 */     putAttrType("LOGIN_TIME", Timestamp.class);
/* 28 */     putAttrType("USER_NAME", String.class);
/* 29 */     putAttrType("LOGOUT_TIME", Timestamp.class);
/* 30 */     putAttrType("LOGIN_SYSTEM_NAME", String.class);
/*    */   }
/*    */ 
/*    */   protected void putAttrNames() {
/* 34 */     putAttrLabelCn("LOGIN_TYPE", "LOGIN_TYPE");
/* 35 */     putAttrLabelCn("IP_ADDRESS", "IP_ADDRESS");
/* 36 */     putAttrLabelCn("MACHINE_NAME", "MACHINE_NAME");
/* 37 */     putAttrLabelCn("CUID", "CUID");
/* 38 */     putAttrLabelCn("LOGIN_TIME", "LOGIN_TIME");
/* 39 */     putAttrLabelCn("USER_NAME", "USER_NAME");
/* 40 */     putAttrLabelCn("LOGOUT_TIME", "LOGOUT_TIME");
/* 41 */     putAttrLabelCn("LOGIN_SYSTEM_NAME", "LOGIN_SYSTEM_NAME");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.helper.LoginLogHelper
 * JD-Core Version:    0.6.0
 */