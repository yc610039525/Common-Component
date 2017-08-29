/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.id.CUIDHexGenerator;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboBlob;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractDAO
/*     */   implements IDataAccessObject
/*     */ {
/*     */   private static String implDaoName;
/*     */   private String daoName;
/*     */   private boolean supportObjectValid;
/*     */ 
/*     */   public AbstractDAO(String daoName)
/*     */   {
/*  37 */     this.daoName = daoName;
/*  38 */     LogHome.getLog().info("加载数据访问对象[" + daoName + "]");
/*     */   }
/*     */ 
/*     */   public AbstractDAO() {
/*  42 */     this.daoName = getClass().getSimpleName();
/*  43 */     LogHome.getLog().info("加载数据访问对象[" + this.daoName + "]");
/*     */   }
/*     */ 
/*     */   public String getDaoName() {
/*  47 */     return this.daoName;
/*     */   }
/*     */ 
/*     */   public static String getImplDaoName() {
/*  51 */     return implDaoName;
/*     */   }
/*     */ 
/*     */   protected static void setImplDaoName(String _implDaoName) {
/*  55 */     implDaoName = _implDaoName;
/*  56 */     LogHome.getLog().info("DAO使用的ImplDaoName=" + implDaoName);
/*     */   }
/*     */ 
/*     */   public boolean isSupportObjectValid() {
/*  60 */     return this.supportObjectValid;
/*     */   }
/*     */ 
/*     */   protected void setSupportObjectValid(boolean supportObjectValid) {
/*  64 */     this.supportObjectValid = supportObjectValid;
/*  65 */     LogHome.getLog().info("DAO对象鉴权 supportObjectValid=" + supportObjectValid);
/*     */   }
/*     */ 
/*     */   public IDataAccessObject getDAO(String daoName) {
/*  69 */     return DaoHomeFactory.getInstance().getDAO(daoName);
/*     */   }
/*     */ 
/*     */   protected SqlQueryDaoCmd createSqlQueryDaoCmd() throws Exception {
/*  73 */     return SqlDaoCmdFactory.getInstance().createSqlQueryCmd();
/*     */   }
/*     */ 
/*     */   protected SqlQueryDaoCmd createSqlQueryDaoCmd(String dsName) throws Exception {
/*  77 */     return SqlDaoCmdFactory.getInstance().createSqlQueryCmd(dsName);
/*     */   }
/*     */ 
/*     */   protected SqlExecDaoCmd createSqlExecDaoCmd() throws Exception {
/*  81 */     return SqlDaoCmdFactory.getInstance().createSqlExecCmd();
/*     */   }
/*     */ 
/*     */   protected SqlExecDaoCmd createSqlExecDaoCmd(String dsName) throws Exception {
/*  85 */     return SqlDaoCmdFactory.getInstance().createSqlExecCmd(dsName);
/*     */   }
/*     */ 
/*     */   protected IDataAccessObject getImplDAO() {
/*  89 */     return getDAO(implDaoName);
/*     */   }
/*     */ 
/*     */   public void reInitCache(GenericDO dbo) throws Exception {
/*  93 */     getDAO(implDaoName).reInitCache(dbo);
/*     */   }
/*     */ 
/*     */   public void clearCache(GenericDO dbo) throws Exception {
/*  97 */     getDAO(implDaoName).clearCache(dbo);
/*     */   }
/*     */ 
/*     */   public static IDataAccessObject getSystemDAO() {
/* 101 */     return DaoHomeFactory.getInstance().getDAO(implDaoName);
/*     */   }
/*     */ 
/*     */   public boolean isClassCached(String dbClassId) {
/* 105 */     return getImplDAO().isClassCached(dbClassId);
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 109 */     getImplDAO().createObject(actionContext, dbo);
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo, boolean isClear) throws Exception {
/* 113 */     getImplDAO().createObject(actionContext, dbo, isClear);
/*     */   }
/*     */ 
/*     */   public void createObjects(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/* 117 */     getImplDAO().createObjects(actionContext, dbos);
/*     */   }
/*     */ 
/*     */   public void updateObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 121 */     getImplDAO().updateObject(actionContext, dbo);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs, boolean byCuidOrObjectId) throws Exception {
/* 125 */     getImplDAO().updateObjects(actionContext, dbos, attrs, byCuidOrObjectId);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs) throws Exception {
/* 129 */     getImplDAO().updateObjects(actionContext, dbos, attrs);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, String className, String sql, Map attrs) throws Exception {
/* 133 */     getImplDAO().updateObjects(actionContext, className, sql, attrs);
/*     */   }
/*     */ 
/*     */   public void deleteObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 137 */     getImplDAO().deleteObject(actionContext, dbo);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, List ids) throws Exception {
/* 141 */     getImplDAO().deleteObjects(actionContext, ids);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/* 145 */     getImplDAO().deleteObjects(actionContext, dbos);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos, boolean byCuidOrObjectId) throws Exception {
/* 149 */     getImplDAO().deleteObjects(actionContext, dbos, byCuidOrObjectId);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, String className, String sql) throws Exception {
/* 153 */     getImplDAO().deleteObjects(actionContext, className, sql);
/*     */   }
/*     */ 
/*     */   public void deleteAll(IBoActionContext actionContext, String className) throws Exception {
/* 157 */     getImplDAO().deleteAll(actionContext, className);
/*     */   }
/*     */ 
/*     */   public GenericDO getAttrObj(GenericDO dbo) throws Exception {
/* 161 */     return getImplDAO().getAttrObj(dbo);
/*     */   }
/*     */ 
/*     */   public DataObjectList getAttrObjs(long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 165 */     return getImplDAO().getAttrObjs(objectIds, dboTemplate);
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(GenericDO dboTemplate) throws Exception {
/* 169 */     return getImplDAO().getObjByCuid(dboTemplate);
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(IBoActionContext actionContext, GenericDO dboTemplate) throws Exception {
/* 173 */     return getImplDAO().getObjByCuid(actionContext, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjsByCuids(List<String> cuids, GenericDO dboTemplate) throws Exception {
/* 177 */     return getImplDAO().getObjsByCuids(cuids, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjsByCuids(IBoActionContext actionContext, List<String> cuids, GenericDO dboTemplate) throws Exception {
/* 181 */     return getImplDAO().getObjsByCuids(actionContext, cuids, dboTemplate);
/*     */   }
/*     */ 
/*     */   protected GenericDO getSimpleObjByCuid(GenericDO dboTemplate) throws Exception {
/* 185 */     GenericDO dbo = null;
/* 186 */     DataObjectList doList = getObjectsBySql("CUID='" + dboTemplate.getCuid() + "'", dboTemplate, 1);
/*     */ 
/* 188 */     if (doList.size() > 0) {
/* 189 */       dbo = (GenericDO)doList.get(0);
/*     */     }
/* 191 */     return dbo;
/*     */   }
/*     */ 
/*     */   public GenericDO getSimpleObject(GenericDO dbo) throws Exception {
/* 195 */     return getImplDAO().getSimpleObject(dbo);
/*     */   }
/*     */ 
/*     */   public GenericDO getObject(GenericDO dbo) throws Exception {
/* 199 */     return getImplDAO().getObject(dbo);
/*     */   }
/*     */   public GenericDO getObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 202 */     return getImplDAO().getObject(actionContext, dbo);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjects(long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 206 */     return getImplDAO().getObjects(objectIds, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjects(IBoActionContext context, long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 210 */     return getImplDAO().getObjects(context, objectIds, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjByAttrs(IBoQueryContext context, GenericDO dboTemplate) throws Exception {
/* 214 */     return getImplDAO().getObjByAttrs(context, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjByAttrValues(IBoQueryContext context, GenericDO dboTemplate, String attrName, List<String> values) throws Exception {
/* 218 */     return getImplDAO().getObjByAttrValues(context, dboTemplate, attrName, values);
/*     */   }
/*     */ 
/*     */   public DataObjectList getAllObjByClass(GenericDO dboTemplate, int objGetType) throws Exception {
/* 222 */     return getImplDAO().getAllObjByClass(dboTemplate, objGetType);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjectsBySql(String sql, GenericDO dboTemplate, int objGetType) throws Exception {
/* 226 */     return getImplDAO().getObjectsBySql(sql, dboTemplate, objGetType);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjectsBySql(IBoActionContext actionContext, String sql, GenericDO dboTemplate, int objGetType) throws Exception {
/* 230 */     return getImplDAO().getObjectsBySql(actionContext, sql, dboTemplate, objGetType);
/*     */   }
/*     */ 
/*     */   public int getCountOfClass(String className) throws Exception {
/* 234 */     return getImplDAO().getCountOfClass(className);
/*     */   }
/*     */ 
/*     */   public int getCalculateValue(String sql)
/*     */     throws Exception
/*     */   {
/* 269 */     return createSqlQueryDaoCmd().getCalculateValue(sql);
/*     */   }
/*     */ 
/*     */   public int getCalculateValue(IBoActionContext actionContext, String sql) throws Exception {
/* 273 */     return createSqlQueryDaoCmd(getDsName(actionContext)).getCalculateValue(sql);
/*     */   }
/*     */ 
/*     */   public int[] getCalculateValues(String sql) throws Exception {
/* 277 */     return createSqlQueryDaoCmd().getCalculateValues(sql);
/*     */   }
/*     */ 
/*     */   public int[] getCalculateValues(IBoActionContext actionContext, String sql) throws Exception {
/* 281 */     return createSqlQueryDaoCmd(getDsName(actionContext)).getCalculateValues(sql);
/*     */   }
/*     */ 
/*     */   public boolean hasObjects(String className) throws Exception {
/* 285 */     String sql = "select count(*) from " + className;
/* 286 */     return getCalculateValue(sql) > 0;
/*     */   }
/*     */ 
/*     */   public boolean hasObjects(IBoActionContext actionContext, String className) throws Exception {
/* 290 */     String sql = "select count(*) from " + className;
/* 291 */     return getCalculateValue(actionContext, sql) > 0;
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(String sql, GenericDO[] dboTemplates) throws Exception {
/* 295 */     return createSqlQueryDaoCmd().selectDBOs(sql, dboTemplates);
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(IBoQueryContext queryContext, String sql, GenericDO[] dboTemplates) throws Exception
/*     */   {
/* 300 */     return createSqlQueryDaoCmd(getDsName(queryContext)).selectDBOs(queryContext, sql, queryContext.getOffset(), queryContext.getFetchSize(), queryContext.isCountBeforQuery(), dboTemplates);
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(String sql, Class[] colClassType) throws Exception
/*     */   {
/* 305 */     return createSqlQueryDaoCmd().selectDBOs(sql, colClassType);
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(IBoQueryContext queryContext, String sql, Class[] colClassType) throws Exception
/*     */   {
/* 310 */     return createSqlQueryDaoCmd(getDsName(queryContext)).selectDBOs(queryContext, sql, queryContext.getOffset(), queryContext.getFetchSize(), queryContext.isCountBeforQuery(), colClassType);
/*     */   }
/*     */ 
/*     */   public int execSql(String sql) throws Exception
/*     */   {
/* 315 */     return createSqlExecDaoCmd().execSql(sql);
/*     */   }
/*     */ 
/*     */   public int execSql(IBoActionContext actionContext, String sql) throws Exception {
/* 319 */     return createSqlExecDaoCmd(getDsName(actionContext)).execSql(sql);
/*     */   }
/*     */ 
/*     */   public void insertDbo(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 323 */     createSqlExecDaoCmd(getDsName(actionContext)).insertDbo(dbo);
/*     */   }
/*     */ 
/*     */   public void insertDbos(IBoActionContext actionContext, DataObjectList dboList) throws Exception {
/* 327 */     createSqlExecDaoCmd(getDsName(actionContext)).insertDbos(dboList);
/*     */   }
/*     */ 
/*     */   public void updateDbo(IBoActionContext actionContext, GenericDO dbo, String sqlCond) throws Exception {
/* 331 */     createSqlExecDaoCmd(getDsName(actionContext)).updateDbo(dbo, sqlCond);
/*     */   }
/*     */ 
/*     */   public void updateDbo(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 335 */     createSqlExecDaoCmd(getDsName(actionContext)).updateDbo(dbo);
/*     */   }
/*     */ 
/*     */   public void updateBlob(IBoActionContext actionContext, String tableName, String blobFieldName, DboBlob blob, String sqlCond) throws Exception
/*     */   {
/* 340 */     createSqlExecDaoCmd(getDsName(actionContext)).updateBlob(tableName, blobFieldName, blob, sqlCond);
/*     */   }
/*     */ 
/*     */   public String getLabelCnByCuid(String cuid) throws Exception {
/* 344 */     return getImplDAO().getLabelCnByCuid(cuid);
/*     */   }
/*     */ 
/*     */   public Map getLabelCnsByCuids(String[] cuids) throws Exception {
/* 348 */     return getImplDAO().getLabelCnsByCuids(cuids);
/*     */   }
/*     */ 
/*     */   protected void setCuid(GenericDO dbo) {
/* 352 */     if (dbo.getCuid() == null) {
/* 353 */       String cuid = CUIDHexGenerator.getInstance().generate(dbo.getClassName());
/* 354 */       dbo.setCuid(cuid);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getDsName(IBoActionContext actionContext) {
/* 359 */     String dsName = "GLOBAL_DS_NAME";
/* 360 */     if ((actionContext != null) && (actionContext.getDsName() != null) && (actionContext.getDsName().trim().length() > 0))
/*     */     {
/* 362 */       dsName = actionContext.getDsName();
/*     */     }
/* 364 */     return dsName;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.AbstractDAO
 * JD-Core Version:    0.6.0
 */