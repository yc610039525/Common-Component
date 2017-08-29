/*    */ package com.boco.common.util.db;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class PrimaryKeyProxy
/*    */ {
/*    */   public static final String DEFAULT_PRIMARY_KEY_NAME = "ID";
/* 28 */   private final Map primaryKeyTable = new HashMap();
/* 29 */   private static final PrimaryKeyProxy instance = new PrimaryKeyProxy();
/*    */ 
/* 68 */   private int startObjId = -1;
/* 69 */   private int nextObjId = -1;
/*    */   private static final int BASE_NEXT_ID = 1000;
/*    */   private static final int ID_RAISE_STEP = 100;
/*    */   private static final String OBJECT_ID_TABLE_NAME = "WM_OBJECT_ID";
/*    */   private static final String OBJECT_ID_FIELD_NAME = "NEXT_ID";
/*    */ 
/*    */   public static PrimaryKeyProxy getInstance()
/*    */   {
/* 35 */     return instance;
/*    */   }
/*    */ 
/*    */   public synchronized int getPrimaryKey(String dsName, String tableName) {
/* 39 */     return getPrimaryKey(dsName, tableName, "ID");
/*    */   }
/*    */ 
/*    */   public synchronized int getPrimaryKey(String dsName, String tableName, String primaryKeyName) {
/* 43 */     int primaryKey = 0;
/* 44 */     String _dsName = dsName == null ? "" : dsName;
/* 45 */     String key = dsName + "-" + tableName;
/*    */ 
/* 47 */     if (this.primaryKeyTable.containsKey(key))
/* 48 */       primaryKey = ((Integer)this.primaryKeyTable.get(key)).intValue() + 1;
/*    */     else {
/* 50 */       primaryKey = getPrimaryKeyFromDb(dsName, tableName, primaryKeyName);
/*    */     }
/*    */ 
/* 53 */     this.primaryKeyTable.put(key, new Integer(primaryKey));
/* 54 */     return primaryKey;
/*    */   }
/*    */ 
/*    */   public synchronized int getPrimaryKeyFromDb(String dsName, String tableName, String primaryKeyName) {
/* 58 */     int primaryKey = 0;
/*    */     try {
/* 60 */       SqlQueryCmd sqlQueryCmd = DbCmdFactory.getInstance().createSqlQueryCmd(dsName);
/* 61 */       primaryKey = sqlQueryCmd.getCalculateValue("select max(" + primaryKeyName + ") from " + tableName);
/*    */     } catch (Exception ex) {
/* 63 */       LogHome.getLog().error("", ex);
/*    */     }
/* 65 */     return primaryKey + 1;
/*    */   }
/*    */ 
/*    */   public synchronized int getNextObjId(String dsName)
/*    */   {
/* 76 */     String _dsName = dsName == null ? "" : dsName;
/* 77 */     if ((this.startObjId < 0) || (this.nextObjId - this.startObjId >= 100)) {
/* 78 */       getBaseNextObjId(_dsName);
/* 79 */       this.nextObjId = (this.startObjId + 1);
/*    */     } else {
/* 81 */       this.nextObjId += 1;
/*    */     }
/* 83 */     return this.nextObjId;
/*    */   }
/*    */ 
/*    */   private void getBaseNextObjId(String dsName) {
/*    */     try {
/* 88 */       int maxObjId = -1;
/*    */       try {
/* 90 */         SqlQueryCmd sqlQueryCmd = DbCmdFactory.getInstance().createSqlQueryCmd(dsName);
/* 91 */         maxObjId = sqlQueryCmd.getCalculateValue("select max(NEXT_ID) from WM_OBJECT_ID");
/*    */       } catch (Exception ex) {
/* 93 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */ 
/* 96 */       if (maxObjId <= 0) {
/* 97 */         this.startObjId = 1000;
/* 98 */         SqlExecCmd sqlExecCmd = DbCmdFactory.getInstance().createSqlExecCmd(dsName);
/* 99 */         sqlExecCmd.execSql("insert into WM_OBJECT_ID values(" + this.startObjId + ")");
/*    */       } else {
/* 101 */         this.startObjId = (maxObjId + 100);
/* 102 */         SqlExecCmd sqlExecCmd = DbCmdFactory.getInstance().createSqlExecCmd(dsName);
/* 103 */         sqlExecCmd.execSql("update WM_OBJECT_ID set NEXT_ID=" + this.startObjId);
/*    */       }
/*    */     } catch (Exception ex) {
/* 106 */       LogHome.getLog().error("", ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.PrimaryKeyProxy
 * JD-Core Version:    0.6.0
 */