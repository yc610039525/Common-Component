/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.common.util.db.DbContext;
/*    */ import com.boco.transnms.common.dto.base.DataObjectList;
/*    */ import com.boco.transnms.common.dto.base.DboCollection;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*    */ 
/*    */ public class ExSqlQueryDaoCmd extends SqlQueryDaoCmd
/*    */ {
/*    */   protected ExSqlQueryDaoCmd(DbContext dbContext)
/*    */   {
/* 11 */     super(dbContext);
/*    */   }
/*    */ 
/*    */   public DboCollection selectDBOs(IBoQueryContext queryContext, String sql, int offset, int fetchSize, boolean isCountBeforQuery, GenericDO[] dboTemplates) throws Exception
/*    */   {
/* 16 */     String esql = sql;
/* 17 */     if ((queryContext == null) || (!queryContext.isQueryDeleted())) {
/* 18 */       esql = SqlQueryDaoCmdHelper.appendDeleteFlag(sql);
/*    */     }
/* 20 */     return super.selectDBOs(queryContext, esql, offset, fetchSize, isCountBeforQuery, dboTemplates);
/*    */   }
/*    */ 
/*    */   public DataObjectList selectDBOs(String sql, Class[] colClassType) throws Exception {
/* 24 */     return super.selectDBOs(SqlQueryDaoCmdHelper.appendDeleteFlag(sql), colClassType);
/*    */   }
/*    */ 
/*    */   public DataObjectList selectDBOs(IBoQueryContext queryContext, String sql, int offset, int fetchSize, boolean isCountBeforQuery, Class[] colClassType) throws Exception
/*    */   {
/* 29 */     String esql = sql;
/* 30 */     if ((queryContext == null) || (!queryContext.isQueryDeleted())) {
/* 31 */       esql = SqlQueryDaoCmdHelper.appendDeleteFlag(sql);
/*    */     }
/* 33 */     return super.selectDBOs(queryContext, esql, offset, fetchSize, isCountBeforQuery, colClassType);
/*    */   }
/*    */ 
/*    */   public DboCollection getFirsetDBOs(String sql, int fetchSize, boolean isCountBeforQuery, GenericDO[] dboTemplates) throws Exception
/*    */   {
/* 38 */     return super.getFirsetDBOs(SqlQueryDaoCmdHelper.appendDeleteFlag(sql), fetchSize, isCountBeforQuery, dboTemplates);
/*    */   }
/*    */ 
/*    */   public DboCollection getNextDbos(String sql, int offset, int fetchSize, GenericDO[] dboTemplates) throws Exception
/*    */   {
/* 43 */     return super.getNextDbos(SqlQueryDaoCmdHelper.appendDeleteFlag(sql), offset, fetchSize, dboTemplates);
/*    */   }
/*    */ 
/*    */   public int getCalculateValue(String sql) throws Exception {
/* 47 */     return getCalculateValue(SqlQueryDaoCmdHelper.appendDeleteFlag(sql), true);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.ExSqlQueryDaoCmd
 * JD-Core Version:    0.6.0
 */