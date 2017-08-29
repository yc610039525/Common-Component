/*    */ package com.boco.transnms.common.dto.helper;
/*    */ 
/*    */ import com.boco.transnms.common.dto.base.AbstractDoHelper;
/*    */ import java.sql.Timestamp;
/*    */ 
/*    */ public class SystemLogHelper extends AbstractDoHelper
/*    */ {
/*  8 */   private static final SystemLogHelper instance = new SystemLogHelper();
/*    */ 
/*    */   private SystemLogHelper() {
/* 11 */     super(false);
/*    */   }
/*    */ 
/*    */   public static SystemLogHelper getInstance() {
/* 15 */     return instance;
/*    */   }
/*    */ 
/*    */   public static String getDboClassName() {
/* 19 */     return "SYSTEM_LOG";
/*    */   }
/*    */ 
/*    */   protected void putAttrTypes() {
/* 23 */     putAttrType("LOG_CLASS_NAME", String.class);
/* 24 */     putAttrType("IP_ADDRESS", String.class);
/* 25 */     putAttrType("OBJECT_NAME", String.class);
/* 26 */     putAttrType("MACHINE_NAME", String.class);
/* 27 */     putAttrType("CREATETIME", Timestamp.class);
/* 28 */     putAttrType("DESCIPTION", String.class);
/* 29 */     putAttrType("CUID", String.class);
/* 30 */     putAttrType("OPERATE_TYPE", String.class);
/* 31 */     putAttrType("USER_NAME", String.class);
/*    */   }
/*    */ 
/*    */   protected void putAttrNames() {
/* 35 */     putAttrLabelCn("LOG_CLASS_NAME", "LOG_CLASS_NAME");
/* 36 */     putAttrLabelCn("IP_ADDRESS", "IP_ADDRESS");
/* 37 */     putAttrLabelCn("OBJECT_NAME", "OBJECT_NAME");
/* 38 */     putAttrLabelCn("MACHINE_NAME", "MACHINE_NAME");
/* 39 */     putAttrLabelCn("CREATETIME", "CREATETIME");
/* 40 */     putAttrLabelCn("DESCIPTION", "DESCIPTION");
/* 41 */     putAttrLabelCn("CUID", "CUID");
/* 42 */     putAttrLabelCn("OPERATE_TYPE", "OPERATE_TYPE");
/* 43 */     putAttrLabelCn("USER_NAME", "USER_NAME");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.helper.SystemLogHelper
 * JD-Core Version:    0.6.0
 */