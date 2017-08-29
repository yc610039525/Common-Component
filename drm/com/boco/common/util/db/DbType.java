/*    */ package com.boco.common.util.db;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ 
/*    */ public class DbType
/*    */ {
/*    */   private int value;
/*    */   public static final int _DB_TYPE_ORACLE = 1;
/*    */   public static final int _DB_TYPE_SYBASE = 2;
/*    */   public static final int _DB_TYPE_HSQL = 4;
/*    */   public static final int _DB_TYPE_INFORMIX = 5;
/*    */   public static final int _DB_TYPE_FIREBIRD = 6;
/*    */   public static final int _DB_TYPE_MYSQL = 7;
/*    */   public static final int _DB_TYPE_SQLSERVER = 8;
/* 34 */   public static final DbType DB_TYPE_ORACLE = new DbType(1);
/* 35 */   public static final DbType DB_TYPE_SYBASE = new DbType(2);
/* 36 */   public static final DbType DB_TYPE_HSQL = new DbType(4);
/* 37 */   public static final DbType DB_TYPE_INFORMIX = new DbType(5);
/* 38 */   public static final DbType DB_TYPE_FIREBIRD = new DbType(6);
/* 39 */   public static final DbType DB_TYPE_MYSQL = new DbType(7);
/* 40 */   public static final DbType DB_TYPE_SQLSERVER = new DbType(8);
/*    */ 
/*    */   protected DbType(int value) {
/* 43 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public int value() {
/* 47 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static DbType from_int(int value) {
/* 51 */     switch (value) {
/*    */     case 1:
/* 53 */       return DB_TYPE_ORACLE;
/*    */     case 2:
/* 55 */       return DB_TYPE_SYBASE;
/*    */     case 4:
/* 57 */       return DB_TYPE_HSQL;
/*    */     case 5:
/* 59 */       return DB_TYPE_INFORMIX;
/*    */     case 6:
/* 61 */       return DB_TYPE_FIREBIRD;
/*    */     case 7:
/* 63 */       return DB_TYPE_MYSQL;
/*    */     case 8:
/* 65 */       return DB_TYPE_SQLSERVER;
/*    */     case 3:
/* 67 */     }throw new UserException("非法的数据库类型：" + value);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.DbType
 * JD-Core Version:    0.6.0
 */