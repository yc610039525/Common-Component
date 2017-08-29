/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import com.boco.transnms.server.bo.ibo.IObjectSecurityBO;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ExObjectDAO extends GenericObjectDAO
/*     */ {
/*     */   private static final String BEGIN_LOG = ", begin!";
/*     */   private static final String END_LOG = ", end!";
/*     */   private static final String EXP_TIME = ", dbtime=";
/*  36 */   private boolean isDelete = false;
/*     */ 
/*     */   public ExObjectDAO() {
/*  39 */     super("ExObjectDAO");
/*  40 */     setImplDaoName(TnmsDrmCfg.getInstance().getImplDaoName());
/*     */   }
/*     */ 
/*     */   public ExObjectDAO(String daoName) {
/*  44 */     super(daoName);
/*  45 */     setImplDaoName(TnmsDrmCfg.getInstance().getImplDaoName());
/*     */   }
/*     */ 
/*     */   public void deleteObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/*  49 */     long startTime = System.currentTimeMillis();
/*  50 */     if ((dbo.getCuid() == null) || (dbo.getCuid().trim().length() == 0)) {
/*  51 */       dbo = getObject(dbo);
/*     */     }
/*     */ 
/*  54 */     IObjectSecurityBO sbo = getSecurityObjectBO();
/*  55 */     if (sbo != null) {
/*  56 */       sbo.isObjectPermitEdit(actionContext, dbo);
/*     */     }
/*  58 */     DaoHelper.setLogObjName(dbo);
/*     */ 
/*  72 */     DaoHelper.writeSystemLog(actionContext, "deleteObject", dbo, "");
/*  73 */     long expend = System.currentTimeMillis() - startTime;
/*  74 */     DaoHelper.printLog(actionContext, "deleteObject<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectId(), ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/*  78 */     long startTime = System.currentTimeMillis();
/*  79 */     String className = "";
/*  80 */     if (dbos.size() > 0) {
/*  81 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/*  83 */     DataObjectList deletedbos = new DataObjectList();
/*  84 */     if (dbos.getCuids() == null)
/*  85 */       deletedbos = getObjects(dbos.getIds(), new GenericDO());
/*     */     else {
/*  87 */       deletedbos = dbos;
/*     */     }
/*     */ 
/*  90 */     IObjectSecurityBO sbo = getSecurityObjectBO();
/*  91 */     if (sbo != null) {
/*  92 */       sbo.isObjectsPermitEdit(actionContext, deletedbos);
/*     */     }
/*     */ 
/* 108 */     DaoHelper.writeSystemLog(actionContext, "deleteObjects", dbos, "");
/* 109 */     long expend = System.currentTimeMillis() - startTime;
/* 110 */     DaoHelper.printLog(actionContext, "deleteObjects<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, String className, String sql) throws Exception {
/* 114 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 116 */     super.deleteObjects(actionContext, className, sql);
/* 117 */     DaoHelper.writeSystemLog(actionContext, "deleteObjects", new GenericDO(className), "用SQL='" + sql + "'删除多个对象");
/* 118 */     long expend = System.currentTimeMillis() - startTime;
/* 119 */     DaoHelper.printLog(actionContext, "deleteObjects<" + className + ">" + ", sql=" + sql, ", end!", expend);
/*     */   }
/*     */ 
/*     */   public int execSql(String sql) throws Exception {
/* 123 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 125 */     int result = super.execSql(sql);
/* 126 */     long expend = System.currentTimeMillis() - startTime;
/* 127 */     DaoHelper.printLog(null, "execSql, sql=" + sql, ", end!", expend);
/* 128 */     return result;
/*     */   }
/*     */ 
/*     */   public void deleteAll(IBoActionContext actionContext, String className) throws Exception {
/* 132 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 146 */     DaoHelper.writeSystemLog(actionContext, "deleteAll", new GenericDO(className), "删除全部对象");
/* 147 */     long expend = System.currentTimeMillis() - startTime;
/* 148 */     DaoHelper.printLog(actionContext, "deleteAll<" + className + ">", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjectsBySql(String sql, GenericDO dboTemplate, int objGetType)
/*     */     throws Exception
/*     */   {
/* 161 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 163 */     DataObjectList result = super.getObjectsBySql(appendDeleteFlag(sql), dboTemplate, objGetType);
/* 164 */     long expend = System.currentTimeMillis() - startTime;
/* 165 */     DaoHelper.printLog(null, "getObjectsBySql<" + dboTemplate.getClassName() + ">" + result.size() + "个, sql=" + sql, ", end!", expend, result.size());
/* 166 */     return result;
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(String sql, GenericDO[] dboTemplates) throws Exception
/*     */   {
/* 171 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 173 */     DboCollection result = super.selectDBOs(sql, dboTemplates);
/* 174 */     long expend = System.currentTimeMillis() - startTime;
/* 175 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(String sql, Class[] colClassType) throws Exception {
/* 180 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 182 */     DataObjectList result = super.selectDBOs(sql, colClassType);
/* 183 */     long expend = System.currentTimeMillis() - startTime;
/* 184 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 185 */     return result;
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(IBoQueryContext queryContext, String sql, GenericDO[] dboTemplates) throws Exception {
/* 189 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 191 */     DboCollection result = super.selectDBOs(queryContext, sql, dboTemplates);
/* 192 */     long expend = System.currentTimeMillis() - startTime;
/* 193 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 194 */     return result;
/*     */   }
/*     */ 
/*     */   public int getCalculateValue(String sql) throws Exception {
/* 198 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 200 */     int result = super.getCalculateValue(appendDeleteFlag(sql));
/* 201 */     long expend = System.currentTimeMillis() - startTime;
/* 202 */     DaoHelper.printLog(null, "getCalculateValue<" + sql + ">", ", end!", expend);
/* 203 */     return result;
/*     */   }
/*     */ 
/*     */   public int[] getCalculateValues(String sql) throws Exception {
/* 207 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 209 */     int[] result = super.getCalculateValues(appendDeleteFlag(sql));
/* 210 */     long expend = System.currentTimeMillis() - startTime;
/* 211 */     DaoHelper.printLog(null, "getCalculateValues<" + sql + ">", ", end!", expend);
/* 212 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean hasObjects(String className) throws Exception {
/* 216 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 218 */     boolean result = super.hasObjects(className);
/* 219 */     long expend = System.currentTimeMillis() - startTime;
/* 220 */     DaoHelper.printLog(null, "hasObjects<" + className + ">", ", end!", expend);
/* 221 */     return result;
/*     */   }
/*     */ 
/*     */   public SqlQueryDaoCmd createSqlQueryDaoCmd() throws Exception {
/* 225 */     return super.createSqlQueryDaoCmd();
/*     */   }
/*     */ 
/*     */   public IObjectSecurityBO getSecurityObjectBO() {
/*     */     try {
/* 230 */       return (IObjectSecurityBO)BoHomeFactory.getInstance().getBO("IObjectSecurityBO");
/*     */     } catch (Throwable ex) {
/* 232 */       LogHome.getLog().info("安全拦截SecurityObjectBO没有实例化，如需要进行权限校验请在spring中配置此BO");
/* 233 */     }return null;
/*     */   }
/*     */ 
/*     */   private String appendDeleteFlag(String sql)
/*     */   {
/* 238 */     if (!this.isDelete) {
/* 239 */       sql = " ISDELETE=0 AND " + sql;
/*     */     }
/* 241 */     return sql;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.ExObjectDAO
 * JD-Core Version:    0.6.0
 */