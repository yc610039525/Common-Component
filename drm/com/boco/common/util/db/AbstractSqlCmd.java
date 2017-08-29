/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.sql.Connection;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractSqlCmd
/*     */ {
/*     */   private DbContext dbContext;
/*  32 */   private boolean permitClearCmd = true;
/*     */ 
/*     */   public AbstractSqlCmd()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbstractSqlCmd(DbContext _dbContext)
/*     */   {
/*  46 */     this.dbContext = _dbContext;
/*     */   }
/*     */ 
/*     */   public DbContext getDbContext()
/*     */   {
/*  54 */     return this.dbContext;
/*     */   }
/*     */ 
/*     */   private boolean isPermitClearCmd()
/*     */   {
/*  62 */     return this.permitClearCmd;
/*     */   }
/*     */ 
/*     */   protected Connection getDbConn()
/*     */   {
/*  70 */     return this.dbContext.getDbConn();
/*     */   }
/*     */ 
/*     */   protected DbType getDbType()
/*     */   {
/*  78 */     return this.dbContext.getDbType();
/*     */   }
/*     */ 
/*     */   protected String getDbProductVersion()
/*     */   {
/*  86 */     return this.dbContext.getDbProductVersion();
/*     */   }
/*     */ 
/*     */   public void setPermitClearCmd(boolean _permitClearCmd)
/*     */   {
/*  94 */     this.permitClearCmd = _permitClearCmd;
/*     */   }
/*     */ 
/*     */   public void clearCmd()
/*     */   {
/* 101 */     if ((getDbConn() != null) && (isPermitClearCmd()))
/*     */       try {
/* 103 */         DbConnManager.getInstance().freeDbConn(getDbConn());
/* 104 */         this.dbContext.setDbConn(null);
/*     */       } catch (Exception ex) {
/* 106 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.AbstractSqlCmd
 * JD-Core Version:    0.6.0
 */