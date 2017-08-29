/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboBlob;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import com.boco.transnms.server.bo.ibo.IObjectSecurityBO;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import com.boco.transnms.server.dao.base.internal.ClassUtils;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericObjectDAO extends AbstractObjectDAO
/*     */ {
/*     */   private static final String BEGIN_LOG = ", begin!";
/*     */   private static final String END_LOG = ", end!";
/*     */   private static final String EXP_TIME = ", dbtime=";
/*     */ 
/*     */   public GenericObjectDAO()
/*     */   {
/*  46 */     super("GenericObjectDAO");
/*  47 */     setImplDaoName(TnmsDrmCfg.getInstance().getImplDaoName());
/*     */   }
/*     */ 
/*     */   public GenericObjectDAO(String daoName) {
/*  51 */     super(daoName);
/*  52 */     setImplDaoName(TnmsDrmCfg.getInstance().getImplDaoName());
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo, boolean isClear)
/*     */     throws Exception
/*     */   {
/*  74 */     long startTime = System.currentTimeMillis();
/*     */ 
/*  76 */     if (super.isSupportObjectValid()) {
/*  77 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/*  78 */       if (sbo != null) {
/*  79 */         sbo.isObjectPermitEdit(actionContext, dbo);
/*     */       }
/*     */     }
/*  82 */     super.createObject(actionContext, dbo);
/*  83 */     DaoHelper.writeSystemLog(actionContext, "createObject", dbo, "");
/*  84 */     long expend = System.currentTimeMillis() - startTime;
/*  85 */     DaoHelper.printLog(actionContext, "createObject<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectNum(), ", dbtime=" + expend + ", end!");
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/*  89 */     createObject(actionContext, dbo, false);
/*     */   }
/*     */ 
/*     */   public void createObjects(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/*  93 */     long startTime = System.currentTimeMillis();
/*  94 */     String className = "";
/*  95 */     if (super.isSupportObjectValid()) {
/*  96 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/*  97 */       if (sbo != null) {
/*  98 */         sbo.isObjectsPermitEdit(actionContext, dbos);
/*     */       }
/*     */     }
/* 101 */     super.createObjects(actionContext, dbos);
/* 102 */     for (GenericDO dbo : dbos) {
/* 103 */       className = dbo.getClassName();
/* 104 */       if (dbo.getObjectNum() != 0L) {
/* 105 */         DaoHelper.writeSystemLog(actionContext, "createObject", dbo, "");
/*     */       }
/*     */     }
/* 108 */     long expend = System.currentTimeMillis() - startTime;
/* 109 */     DaoHelper.printLog(actionContext, "createObjects<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 113 */     long startTime = System.currentTimeMillis();
/* 114 */     if (super.isSupportObjectValid()) {
/* 115 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/* 116 */       if (sbo != null) {
/* 117 */         sbo.isObjectPermitEdit(actionContext, dbo);
/*     */       }
/*     */     }
/* 120 */     DaoHelper.setLogObjName(dbo);
/* 121 */     super.updateObject(actionContext, dbo);
/* 122 */     DaoHelper.writeSystemLog(actionContext, "updateObject", dbo, "");
/* 123 */     long expend = System.currentTimeMillis() - startTime;
/* 124 */     DaoHelper.printLog(actionContext, "updateObject<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectId(), ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs, boolean byCuidOrObjectId) throws Exception {
/* 128 */     long startTime = System.currentTimeMillis();
/* 129 */     String className = "";
/* 130 */     if (dbos.size() > 0) {
/* 131 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/* 133 */     if (super.isSupportObjectValid()) {
/* 134 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/* 135 */       if (sbo != null) {
/* 136 */         getSecurityObjectBO().isObjectsPermitEdit(actionContext, dbos);
/*     */       }
/*     */     }
/* 139 */     super.updateObjects(actionContext, dbos, attrs, byCuidOrObjectId);
/* 140 */     DaoHelper.writeSystemLog(actionContext, "updateObjects", dbos, "");
/* 141 */     long expend = System.currentTimeMillis() - startTime;
/* 142 */     DaoHelper.printLog(actionContext, "updateObjects<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs) throws Exception {
/* 146 */     long startTime = System.currentTimeMillis();
/* 147 */     String className = "";
/* 148 */     if (dbos.size() > 0) {
/* 149 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/* 151 */     if (super.isSupportObjectValid()) {
/* 152 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/* 153 */       if (sbo != null) {
/* 154 */         getSecurityObjectBO().isObjectsPermitEdit(actionContext, dbos);
/*     */       }
/*     */     }
/* 157 */     super.updateObjects(actionContext, dbos, attrs);
/* 158 */     DaoHelper.writeSystemLog(actionContext, "updateObjects", dbos, "");
/* 159 */     long expend = System.currentTimeMillis() - startTime;
/* 160 */     DaoHelper.printLog(actionContext, "updateObjects<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, String className, String sql, Map attrs) throws Exception {
/* 164 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 166 */     super.updateObjects(actionContext, className, sql, attrs);
/* 167 */     DaoHelper.writeSystemLog(actionContext, "updateObjects", new GenericDO(className), "用SQL='" + sql + "'更新多个对象");
/* 168 */     long expend = System.currentTimeMillis() - startTime;
/* 169 */     DaoHelper.printLog(actionContext, "updateObjects<" + className + ">, sql=" + sql, ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 173 */     long startTime = System.currentTimeMillis();
/* 174 */     if ((dbo.getCuid() == null) || (dbo.getCuid().trim().length() == 0)) {
/* 175 */       dbo = getObject(actionContext, dbo);
/*     */     }
/* 177 */     if (super.isSupportObjectValid()) {
/* 178 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/* 179 */       if (sbo != null) {
/* 180 */         sbo.isObjectPermitEdit(actionContext, dbo);
/*     */       }
/*     */     }
/*     */ 
/* 184 */     DaoHelper.setLogObjName(dbo);
/* 185 */     super.deleteObject(actionContext, dbo);
/* 186 */     DaoHelper.writeSystemLog(actionContext, "deleteObject", dbo, "");
/* 187 */     long expend = System.currentTimeMillis() - startTime;
/* 188 */     DaoHelper.printLog(actionContext, "deleteObject<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectId(), ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, List ids) throws Exception {
/* 192 */     if (ids.size() > 0) {
/* 193 */       Object obj = ids.get(0);
/* 194 */       String className = "";
/* 195 */       if ((obj instanceof Long))
/* 196 */         className = ClassUtils.getInstance().getClassName(((Long)obj).longValue());
/* 197 */       else if ((obj instanceof String)) {
/* 198 */         className = GenericDO.parseClassNameFromCuid((String)obj);
/*     */       }
/* 200 */       long startTime = System.currentTimeMillis();
/* 201 */       super.deleteObjects(actionContext, ids);
/* 202 */       DaoHelper.writeSystemLog(actionContext, "deleteObjects", ids, "");
/* 203 */       long expend = System.currentTimeMillis() - startTime;
/*     */ 
/* 205 */       DaoHelper.printLog(actionContext, "deleteObjects<" + className + ">" + ids.size() + "个", ", end!", expend);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/* 210 */     long startTime = System.currentTimeMillis();
/* 211 */     String className = "";
/* 212 */     if (dbos.size() > 0) {
/* 213 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/* 215 */     DataObjectList deletedbos = new DataObjectList();
/* 216 */     if (dbos.getCuids() == null) {
/* 217 */       GenericDO dbTemplate = new GenericDO(className);
/* 218 */       deletedbos = getObjects(actionContext, dbos.getIds(), dbTemplate.createInstanceByClassName());
/*     */     } else {
/* 220 */       deletedbos = dbos;
/*     */     }
/* 222 */     if (super.isSupportObjectValid()) {
/* 223 */       IObjectSecurityBO sbo = getSecurityObjectBO();
/* 224 */       if (sbo != null) {
/* 225 */         sbo.isObjectsPermitEdit(actionContext, deletedbos);
/*     */       }
/*     */     }
/* 228 */     deleteObjects(actionContext, dbos, false);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos, boolean byCuidOrObjectId)
/*     */     throws Exception
/*     */   {
/* 235 */     long startTime = System.currentTimeMillis();
/* 236 */     String className = "";
/* 237 */     if (dbos.size() > 0) {
/* 238 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/*     */ 
/* 254 */     super.deleteObjects(actionContext, dbos, byCuidOrObjectId);
/* 255 */     DaoHelper.writeSystemLog(actionContext, "deleteObjects", dbos, "");
/* 256 */     long expend = System.currentTimeMillis() - startTime;
/* 257 */     DaoHelper.printLog(actionContext, "deleteObjects<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, String className, String sql) throws Exception {
/* 261 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 263 */     super.deleteObjects(actionContext, className, sql);
/* 264 */     DaoHelper.writeSystemLog(actionContext, "deleteObjects", new GenericDO(className), "用SQL='" + sql + "'删除多个对象");
/* 265 */     long expend = System.currentTimeMillis() - startTime;
/* 266 */     DaoHelper.printLog(actionContext, "deleteObjects<" + className + ">" + ", sql=" + sql, ", end!", expend);
/*     */   }
/*     */ 
/*     */   public int execSql(String sql) throws Exception {
/* 270 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 272 */     int result = super.execSql(sql);
/* 273 */     long expend = System.currentTimeMillis() - startTime;
/* 274 */     DaoHelper.printLog(null, "execSql, sql=" + sql, ", end!", expend);
/* 275 */     return result;
/*     */   }
/*     */ 
/*     */   public void insertDbo(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 279 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 281 */     super.insertDbo(actionContext, dbo);
/* 282 */     DaoHelper.writeSystemLog(actionContext, "insertDbo", dbo, "创建对象");
/* 283 */     long expend = System.currentTimeMillis() - startTime;
/* 284 */     DaoHelper.printLog(null, "insertDbo<" + dbo.getClassName() + ">, cuid=" + dbo.getCuid(), ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void insertDbos(IBoActionContext actionContext, DataObjectList dbos) throws Exception {
/* 288 */     String className = "";
/* 289 */     if (dbos.size() > 0) {
/* 290 */       className = ((GenericDO)dbos.get(0)).getClassName();
/*     */     }
/* 292 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 294 */     super.insertDbos(actionContext, dbos);
/* 295 */     DaoHelper.writeSystemLog(actionContext, "insertDbos", dbos, "创建多个对象");
/* 296 */     long expend = System.currentTimeMillis() - startTime;
/* 297 */     DaoHelper.printLog(null, "insertDbos<" + className + ">" + dbos.size() + "个", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateDbo(IBoActionContext actionContext, GenericDO dbo, String sqlCond) throws Exception {
/* 301 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 303 */     super.updateDbo(actionContext, dbo, sqlCond);
/* 304 */     DaoHelper.writeSystemLog(actionContext, "updateDbo", dbo, "用SQL='" + sqlCond + "'更新多个对象");
/* 305 */     long expend = System.currentTimeMillis() - startTime;
/* 306 */     DaoHelper.printLog(null, "updateDbo<" + dbo.getClassName() + ">, sql=" + sqlCond, ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateDbo(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 310 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 312 */     super.updateDbo(actionContext, dbo);
/* 313 */     DaoHelper.writeSystemLog(actionContext, "updateDbo", dbo, "");
/* 314 */     long expend = System.currentTimeMillis() - startTime;
/* 315 */     DaoHelper.printLog(null, "updateDbo<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectNum(), ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void updateBlob(IBoActionContext actionContext, String tableName, String blobFieldName, DboBlob blob, String sqlCond) throws Exception
/*     */   {
/* 320 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 322 */     super.updateBlob(actionContext, tableName, blobFieldName, blob, sqlCond);
/* 323 */     DaoHelper.writeSystemLog(actionContext, "updateBlob", new GenericDO(tableName), "用SQL='" + sqlCond + "'更新BLOB");
/* 324 */     long expend = System.currentTimeMillis() - startTime;
/* 325 */     DaoHelper.printLog(null, "updateBlob<" + tableName + ">, blobSize=" + blob.getBlobBytes().length, ", end!", expend);
/*     */   }
/*     */ 
/*     */   public void deleteAll(IBoActionContext actionContext, String className) throws Exception {
/* 329 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 331 */     super.deleteAll(actionContext, className);
/* 332 */     DaoHelper.writeSystemLog(actionContext, "deleteAll", new GenericDO(className), "删除全部对象");
/* 333 */     long expend = System.currentTimeMillis() - startTime;
/* 334 */     DaoHelper.printLog(actionContext, "deleteAll<" + className + ">", ", end!", expend);
/*     */   }
/*     */ 
/*     */   public GenericDO getAttrObj(GenericDO dbo) throws Exception {
/* 338 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 340 */     GenericDO result = super.getAttrObj(dbo);
/* 341 */     long expend = System.currentTimeMillis() - startTime;
/* 342 */     DaoHelper.printLog(null, "getAttrObj<" + dbo.getClassName() + ">, objectId=" + dbo.getObjectNum(), ", end!", expend);
/* 343 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList getAttrObjs(long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 347 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 349 */     DataObjectList result = super.getAttrObjs(objectIds, dboTemplate);
/* 350 */     long expend = System.currentTimeMillis() - startTime;
/* 351 */     DaoHelper.printLog(null, "getAttrObjs<" + dboTemplate.getClassName() + ">" + result.size() + "个", ", end!", expend);
/* 352 */     return result;
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(GenericDO dboTemplate) throws Exception {
/* 356 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 358 */     GenericDO result = super.getObjByCuid(dboTemplate);
/* 359 */     long expend = System.currentTimeMillis() - startTime;
/* 360 */     DaoHelper.printLog(null, "getObjByCuid<" + dboTemplate.getClassName() + ">, cuid=" + dboTemplate.getCuid(), ", end!", expend);
/* 361 */     return result;
/*     */   }
/*     */ 
/*     */   public String getLabelCnByCuid(String cuid) throws Exception {
/* 365 */     long startTime = System.currentTimeMillis();
/* 366 */     String result = super.getLabelCnByCuid(cuid);
/* 367 */     long expend = System.currentTimeMillis() - startTime;
/* 368 */     DaoHelper.printLog(null, "getLabelCnByCuid<cuid=" + cuid, ", end!", expend);
/* 369 */     return result;
/*     */   }
/*     */ 
/*     */   public Map getLabelCnsByCuids(String[] cuids) throws Exception {
/* 373 */     long startTime = System.currentTimeMillis();
/* 374 */     Map result = super.getLabelCnsByCuids(cuids);
/* 375 */     long expend = System.currentTimeMillis() - startTime;
/* 376 */     DaoHelper.printLog(null, "getLabelCnsByCuids<cuids=" + cuids, ", end!", expend);
/* 377 */     return result;
/*     */   }
/*     */ 
/*     */   public GenericDO getSimpleObject(GenericDO dbo) throws Exception {
/* 381 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 383 */     GenericDO result = super.getSimpleObject(dbo);
/* 384 */     long expend = System.currentTimeMillis() - startTime;
/* 385 */     DaoHelper.printLog(null, "getSimpleObject<" + dbo.getClassName() + ">, objectNum=" + dbo.getObjectNum(), ", end!", expend);
/* 386 */     return result;
/*     */   }
/*     */   public GenericDO getObject(GenericDO dbo) throws Exception {
/* 389 */     return getObject(new BoActionContext(), dbo);
/*     */   }
/*     */   public GenericDO getObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 392 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 394 */     GenericDO result = super.getObject(actionContext, dbo);
/* 395 */     long expend = System.currentTimeMillis() - startTime;
/* 396 */     DaoHelper.printLog(null, "getObject<" + dbo.getClassName() + ">, objectNum=" + dbo.getObjectNum(), ", end!", expend);
/* 397 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjects(long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 401 */     return getObjects(new BoActionContext(), objectIds, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjects(IBoActionContext actionContext, long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 405 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 407 */     DataObjectList result = super.getObjects(actionContext, objectIds, dboTemplate);
/* 408 */     long expend = System.currentTimeMillis() - startTime;
/* 409 */     DaoHelper.printLog(null, "getObjects<" + dboTemplate.getClassName() + ">" + result.size() + "个", ", end!", expend, result.size());
/* 410 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList getAllObjByClass(GenericDO dboTemplate, int objGetType) throws Exception {
/* 414 */     long startTime = System.currentTimeMillis();
/* 415 */     DaoHelper.printLog(null, "getAllObjByClass[className=" + dboTemplate.getClassName() + "]", ", begin!");
/* 416 */     DataObjectList result = super.getAllObjByClass(dboTemplate, objGetType);
/* 417 */     long expend = System.currentTimeMillis() - startTime;
/* 418 */     DaoHelper.printLog(null, "getAllObjByClass<" + dboTemplate.getClassName() + ">" + result.size() + "个", ", end!", expend, result.size());
/* 419 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjectsBySql(String sql, GenericDO dboTemplate, int objGetType) throws Exception {
/* 423 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 425 */     DataObjectList result = super.getObjectsBySql(sql, dboTemplate, objGetType);
/* 426 */     long expend = System.currentTimeMillis() - startTime;
/* 427 */     DaoHelper.printLog(null, "getObjectsBySql<" + dboTemplate.getClassName() + ">" + result.size() + "个, sql=" + sql, ", end!", expend, result.size());
/* 428 */     return result;
/*     */   }
/*     */ 
/*     */   public int getCountOfClass(String className) throws Exception {
/* 432 */     long startTime = System.currentTimeMillis();
/* 433 */     DaoHelper.printLog(null, "getCountOfClass[className=" + className + "]", ", begin!");
/* 434 */     int result = super.getCountOfClass(className);
/* 435 */     long expend = System.currentTimeMillis() - startTime;
/* 436 */     DaoHelper.printLog(null, "getCountOfClass<" + className + ">", ", end!", expend);
/* 437 */     return result;
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(String sql, GenericDO[] dboTemplates)
/*     */     throws Exception
/*     */   {
/* 508 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 510 */     DboCollection result = super.selectDBOs(sql, dboTemplates);
/* 511 */     long expend = System.currentTimeMillis() - startTime;
/* 512 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 513 */     return result;
/*     */   }
/*     */ 
/*     */   public DataObjectList selectDBOs(String sql, Class[] colClassType) throws Exception {
/* 517 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 519 */     DataObjectList result = super.selectDBOs(sql, colClassType);
/* 520 */     long expend = System.currentTimeMillis() - startTime;
/* 521 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 522 */     return result;
/*     */   }
/*     */ 
/*     */   public DboCollection selectDBOs(IBoQueryContext queryContext, String sql, GenericDO[] dboTemplates) throws Exception {
/* 526 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 528 */     DboCollection result = super.selectDBOs(queryContext, sql, dboTemplates);
/* 529 */     long expend = System.currentTimeMillis() - startTime;
/* 530 */     DaoHelper.printLog(null, "selectDBOs<" + sql + ">" + result.size() + "个", ", end!", expend, result.size());
/* 531 */     return result;
/*     */   }
/*     */ 
/*     */   public int getCalculateValue(String sql) throws Exception {
/* 535 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 537 */     int result = super.getCalculateValue(sql);
/* 538 */     long expend = System.currentTimeMillis() - startTime;
/* 539 */     DaoHelper.printLog(null, "getCalculateValue<" + sql + ">", ", end!", expend);
/* 540 */     return result;
/*     */   }
/*     */ 
/*     */   public int[] getCalculateValues(String sql) throws Exception {
/* 544 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 546 */     int[] result = super.getCalculateValues(sql);
/* 547 */     long expend = System.currentTimeMillis() - startTime;
/* 548 */     DaoHelper.printLog(null, "getCalculateValues<" + sql + ">", ", end!", expend);
/* 549 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean hasObjects(String className) throws Exception {
/* 553 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 555 */     boolean result = super.hasObjects(className);
/* 556 */     long expend = System.currentTimeMillis() - startTime;
/* 557 */     DaoHelper.printLog(null, "hasObjects<" + className + ">", ", end!", expend);
/* 558 */     return result;
/*     */   }
/*     */ 
/*     */   public SqlQueryDaoCmd createSqlQueryDaoCmd() throws Exception {
/* 562 */     return super.createSqlQueryDaoCmd();
/*     */   }
/*     */ 
/*     */   public IObjectSecurityBO getSecurityObjectBO() {
/*     */     try {
/* 567 */       return (IObjectSecurityBO)BoHomeFactory.getInstance().getBO("IObjectSecurityBO");
/*     */     } catch (Throwable ex) {
/* 569 */       LogHome.getLog().info("安全拦截SecurityObjectBO没有实例化，如需要进行权限校验请在spring中配置此BO");
/* 570 */     }return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.GenericObjectDAO
 * JD-Core Version:    0.6.0
 */