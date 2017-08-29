/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.db.DbConnManager;
/*     */ import com.boco.common.util.db.DbContext;
/*     */ import com.boco.common.util.db.DbType;
/*     */ import com.boco.common.util.db.SqlHelper;
/*     */ import com.boco.common.util.db.TransactionFactory;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import com.boco.transnms.server.dao.base.internal.GetObjectImpl;
/*     */ import com.boco.transnms.server.dao.base.internal.OptObjectImpl;
/*     */ import com.boco.transnms.server.dao.base.internal.ResourceObjectType;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import sun.reflect.Reflection;
/*     */ 
/*     */ public abstract class AbstractObjectDAO extends AbstractDAO
/*     */ {
/*     */   public AbstractObjectDAO(String daoName)
/*     */   {
/*  50 */     super(daoName);
/*     */   }
/*     */ 
/*     */   public boolean isClassCached(String dbClassId)
/*     */   {
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   private String getDsName(IBoActionContext actionContext) {
/*  62 */     String dsName = "GLOBAL_DS_NAME";
/*  63 */     if ((actionContext != null) && (actionContext.getDsName() != null) && (actionContext.getDsName().trim().length() > 0))
/*     */     {
/*  65 */       dsName = actionContext.getDsName();
/*     */     }
/*  67 */     return dsName;
/*     */   }
/*     */ 
/*     */   private DbContext getDbContext(String dsName) {
/*  71 */     DbContext dbContext = null;
/*     */     try {
/*  73 */       dbContext = DbConnManager.getInstance().getDbContext(dsName);
/*     */     } catch (Exception e) {
/*  75 */       LogHome.getLog().error("获取DbContext出错！");
/*     */     }
/*  77 */     return dbContext;
/*     */   }
/*     */ 
/*     */   private Connection getConn(String dsName) throws Exception {
/*  81 */     Connection conn = null;
/*     */     try {
/*  83 */       DbContext dbContext = TransactionFactory.getInstance().fetchTranscDbContext(dsName);
/*  84 */       if (dbContext == null)
/*  85 */         conn = DbConnManager.getInstance().fetchDbConn(dsName);
/*     */       else
/*  87 */         conn = dbContext.getDbConn();
/*     */     }
/*     */     catch (Exception e) {
/*  90 */       LogHome.getLog().error("[method]:getConn [message]:error get connection by DbConnManager.getInstance().fetchDbConn()");
/*  91 */       LogHome.getLog().error(e);
/*  92 */       throw new Exception("DbConnManager.getInstance().fetchDbConn() return null！");
/*     */     }
/*  94 */     return conn;
/*     */   }
/*     */ 
/*     */   private void closeConn(Connection cconn) {
/*  98 */     if (!isTransaction())
/*  99 */       DbConnManager.getInstance().freeDbConn(cconn);
/*     */   }
/*     */ 
/*     */   private boolean isTransaction()
/*     */   {
/* 104 */     return TransactionFactory.getInstance().getTransaction() != null;
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/*     */     try {
/* 109 */       setCuid(dbo);
/* 110 */       Map clearAttrs = dbo.clearUnknowAttrs();
/* 111 */       Connection conn = null;
/* 112 */       GenericDO resObj = null;
/*     */       try {
/* 114 */         String dsName = getDsName(actionContext);
/* 115 */         conn = getConn(dsName);
/*     */ 
/* 117 */         resObj = OptObjectImpl.createResourceObject(dbo.getClassName(), dbo.copyTo(new HashMap()), conn, dsName);
/*     */       } catch (Exception e) {
/* 119 */         e.printStackTrace();
/* 120 */         throw new UserException(e);
/*     */       } finally {
/* 122 */         closeConn(conn);
/*     */       }
/* 124 */       ObjectDoHelper.setFullObject(dbo, resObj);
/* 125 */       dbo.setAttrValues(clearAttrs);
/*     */     } catch (Exception ex) {
/* 127 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createObject(IBoActionContext actionContext, GenericDO dbo, boolean isClear) throws Exception {
/*     */     try {
/* 133 */       setCuid(dbo);
/* 134 */       Map clearAttrs = dbo.clearUnknowAttrs();
/* 135 */       Connection conn = null;
/* 136 */       GenericDO resObj = null;
/*     */       try {
/* 138 */         String dsName = getDsName(actionContext);
/* 139 */         conn = getConn(dsName);
/*     */ 
/* 141 */         resObj = OptObjectImpl.createResourceObject(dbo.getClassName(), dbo.copyTo(new HashMap()), conn, dsName);
/*     */       } catch (Exception e) {
/* 143 */         e.printStackTrace();
/* 144 */         throw new UserException(e);
/*     */       } finally {
/* 146 */         closeConn(conn);
/*     */       }
/* 148 */       ObjectDoHelper.setFullObject(dbo, resObj);
/* 149 */       if (!isClear)
/* 150 */         dbo.setAttrValues(clearAttrs);
/*     */     }
/*     */     catch (Exception ex) {
/* 153 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createObjects(IBoActionContext actionContext, DataObjectList dbos)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 164 */       Connection conn = null;
/* 165 */       GenericDO resObj = null;
/*     */       try {
/* 167 */         Map dboMap = new HashMap();
/* 168 */         for (int i = 0; i < dbos.size(); i++) {
/* 169 */           GenericDO dbo = (GenericDO)dbos.get(i);
/* 170 */           setCuid(dbo);
/* 171 */           Map clearAttrs = dbo.clearUnknowAttrs();
/* 172 */           dboMap.put(dbo.getCuid(), clearAttrs);
/*     */         }
/* 174 */         String dsName = getDsName(actionContext);
/* 175 */         conn = getConn(dsName);
/*     */ 
/* 177 */         OptObjectImpl.createResourceObjects(dbos, conn, dsName);
/* 178 */         for (int i = 0; i < dbos.size(); i++) {
/* 179 */           GenericDO dbo = (GenericDO)dbos.get(i);
/*     */ 
/* 181 */           Map clearAttrs = (Map)dboMap.get(dbo.getCuid());
/* 182 */           dbo.setAttrValues(clearAttrs);
/*     */         }
/*     */       } catch (Exception e) {
/* 185 */         e.printStackTrace();
/* 186 */         throw new UserException(e);
/*     */       } finally {
/* 188 */         closeConn(conn);
/*     */       }
/*     */     } catch (Exception ex) {
/* 191 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 196 */     Connection conn = null;
/*     */     try {
/* 198 */       String dsName = getDsName(actionContext);
/* 199 */       conn = getConn(dsName);
/*     */ 
/* 201 */       OptObjectImpl.updateAttributeValue(dbo, null, false, conn, dsName);
/*     */     } catch (Exception ex) {
/* 203 */       ex.printStackTrace();
/* 204 */       throw new UserException(ex);
/*     */     } finally {
/* 206 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs, boolean byCuidOrObjectId)
/*     */     throws Exception
/*     */   {
/* 214 */     Connection conn = null;
/*     */     try {
/* 216 */       String dsName = getDsName(actionContext);
/* 217 */       conn = getConn(dsName);
/*     */ 
/* 219 */       OptObjectImpl.updateResourceObjects(dbos, attrs, byCuidOrObjectId, conn, dsName);
/*     */     } catch (Exception ex) {
/* 221 */       ex.printStackTrace();
/* 222 */       throw new UserException(ex);
/*     */     } finally {
/* 224 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, DataObjectList dbos, Map attrs)
/*     */     throws Exception
/*     */   {
/* 232 */     Connection conn = null;
/*     */     try {
/* 234 */       String dsName = getDsName(actionContext);
/* 235 */       conn = getConn(dsName);
/*     */ 
/* 237 */       for (int i = 0; (dbos != null) && (i < dbos.size()); i++) {
/* 238 */         GenericDO dbo = (GenericDO)dbos.get(i);
/* 239 */         if (attrs != null) {
/* 240 */           dbo.getAllAttr().putAll(attrs);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 245 */       if (conn != null) {
/* 246 */         OptObjectImpl.updateResourceObjects(dbos, attrs, false, conn, dsName);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 252 */       ex.printStackTrace();
/* 253 */       throw new UserException(ex);
/*     */     } finally {
/* 255 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateObjects(IBoActionContext actionContext, String className, String sql, Map attrs) throws Exception {
/* 260 */     GenericDO dboTemplate = new GenericDO(className);
/* 261 */     DataObjectList dbos = getObjectsBySql(actionContext, sql, dboTemplate.createInstanceByClassName(), 2);
/* 262 */     for (GenericDO dbo : dbos) {
/* 263 */       dbo.setClassName(className);
/*     */     }
/* 265 */     if (dbos.size() > 0) {
/* 266 */       Connection conn = null;
/*     */       try {
/* 268 */         String dsName = getDsName(actionContext);
/* 269 */         conn = getConn(dsName);
/*     */ 
/* 271 */         for (int i = 0; (dbos != null) && (i < dbos.size()); i++) {
/* 272 */           GenericDO dbo = (GenericDO)dbos.get(i);
/* 273 */           if (attrs != null) {
/* 274 */             dbo.getAllAttr().putAll(attrs);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 279 */         OptObjectImpl.updateResourceObjects(dbos, attrs, false, conn, dsName);
/*     */       } catch (Exception ex) {
/* 281 */         ex.printStackTrace();
/* 282 */         throw new UserException(ex);
/*     */       } finally {
/* 284 */         closeConn(conn);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 290 */     Connection conn = null;
/*     */     try {
/* 292 */       String dsName = getDsName(actionContext);
/* 293 */       conn = getConn(dsName);
/*     */ 
/* 295 */       OptObjectImpl.deleteResourceObject(dbo.getObjectNum(), true, true, conn);
/*     */     } catch (Exception ex) {
/* 297 */       ex.printStackTrace();
/* 298 */       throw new UserException(ex);
/*     */     } finally {
/* 300 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, List ids) throws Exception {
/* 305 */     ArrayList objectids = new ArrayList();
/* 306 */     ArrayList cuids = new ArrayList();
/* 307 */     for (int i = 0; i < ids.size(); i++) {
/* 308 */       Object obj = ids.get(i);
/* 309 */       if ((obj instanceof String))
/* 310 */         cuids.add(obj);
/* 311 */       else if ((obj instanceof Long))
/* 312 */         objectids.add(obj);
/*     */       else {
/* 314 */         LogHome.getLog().error("deleteObjects删除对象的唯一标识类型未知:" + obj);
/*     */       }
/*     */     }
/* 317 */     Connection conn = null;
/*     */     try {
/* 319 */       String dsName = getDsName(actionContext);
/* 320 */       conn = getConn(dsName);
/* 321 */       if (objectids.size() > 0) {
/* 322 */         long[] oids = new long[objectids.size()];
/* 323 */         for (int i = 0; i < objectids.size(); i++) {
/* 324 */           oids[i] = ((Long)objectids.get(i)).longValue();
/*     */         }
/* 326 */         OptObjectImpl.deleteResourceObjectsByObjectId(oids, conn, dsName);
/*     */       }
/* 328 */       if (cuids.size() > 0) {
/* 329 */         String[] objcuids = new String[cuids.size()];
/* 330 */         for (int i = 0; i < cuids.size(); i++) {
/* 331 */           objcuids[i] = ((String)cuids.get(i));
/*     */         }
/* 333 */         OptObjectImpl.deleteResourceObjectsByCuid(objcuids, conn, dsName);
/*     */       }
/*     */     } catch (Exception ex) {
/* 336 */       ex.printStackTrace();
/* 337 */       throw new UserException(ex);
/*     */     } finally {
/* 339 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, DataObjectList dbos, boolean byCuidOrObjectId)
/*     */     throws Exception
/*     */   {
/* 348 */     Connection conn = null;
/*     */     try {
/* 350 */       String dsName = getDsName(actionContext);
/* 351 */       conn = getConn(dsName);
/*     */ 
/* 353 */       if (dbos.size() > 0) {
/* 354 */         if (!byCuidOrObjectId)
/* 355 */           OptObjectImpl.deleteResourceObjectsByObjectId(dbos.getIds(), conn, dsName);
/*     */         else
/* 357 */           OptObjectImpl.deleteResourceObjectsByCuid(dbos.getCuids(), conn, dsName);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 362 */       ex.printStackTrace();
/* 363 */       throw new UserException(ex);
/*     */     } finally {
/* 365 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteObjects(IBoActionContext actionContext, String className, String sql) throws Exception {
/* 370 */     GenericDO dboTemplate = new GenericDO(className);
/* 371 */     DataObjectList doList = getObjectsBySql(actionContext, sql, dboTemplate.createInstanceByClassName(), 2);
/* 372 */     if (doList.size() > 0)
/* 373 */       deleteObjects(actionContext, doList);
/*     */   }
/*     */ 
/*     */   public void deleteAll(IBoActionContext actionContext, String className) throws Exception
/*     */   {
/* 378 */     Connection conn = null;
/*     */     try {
/* 380 */       String dsName = getDsName(actionContext);
/* 381 */       conn = getConn(dsName);
/*     */ 
/* 383 */       OptObjectImpl.deleteAllResObjectsOfClass(className, true, conn);
/*     */     } catch (Exception ex) {
/* 385 */       ex.printStackTrace();
/* 386 */       throw new UserException(ex);
/*     */     } finally {
/* 388 */       closeConn(conn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public GenericDO getAttrObj(GenericDO dbo) throws Exception {
/* 393 */     DataObjectList objList = getAttrObjs(new long[] { dbo.getObjectNum() }, dbo);
/* 394 */     if (objList.size() > 0) {
/* 395 */       dbo = (GenericDO)objList.get(0);
/*     */     }
/* 397 */     return dbo;
/*     */   }
/*     */ 
/*     */   public DataObjectList getAttrObjs(long[] objectIds, GenericDO dboTemplate) throws Exception {
/* 401 */     DataObjectList dboList = new DataObjectList();
/* 402 */     if (objectIds.length == 0) return dboList;
/* 403 */     Connection conn = null;
/*     */     try {
/* 405 */       String dsName = getDsName(null);
/* 406 */       conn = getConn(dsName);
/* 407 */       String[] attrNames = dboTemplate.getAttrNames();
/* 408 */       for (int i = 0; i < attrNames.length; i++)
/*     */         try {
/* 410 */           GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 411 */           Object[] resObjs = impl.getAttributeValues(objectIds, attrNames[i], conn);
/* 412 */           for (int j = 0; j < resObjs.length; j++) {
/* 413 */             GenericDO dbo = null;
/* 414 */             if (i == 0) {
/* 415 */               dbo = (GenericDO)dboTemplate.clone();
/* 416 */               dboList.add(dbo);
/*     */             } else {
/* 418 */               dbo = (GenericDO)dboList.get(j);
/*     */             }
/* 420 */             dbo.setAttrValue(attrNames[i], resObjs[j]);
/*     */           }
/*     */         } catch (Exception e) {
/* 423 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */     catch (Exception ex) {
/* 427 */       throw new UserException(ex);
/*     */     } finally {
/* 429 */       closeConn(conn);
/*     */     }
/* 431 */     return dboList;
/*     */   }
/*     */ 
/*     */   public GenericDO getSimpleObject(GenericDO dbo) throws Exception {
/* 435 */     Connection conn = null;
/*     */     try {
/* 437 */       String dsName = getDsName(null);
/* 438 */       conn = getConn(dsName);
/* 439 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 440 */       GenericDO resObj = impl.getResourceObject(dbo.getObjectNum(), new ResourceObjectType(1), null, conn);
/* 441 */       if (resObj != null) {
/* 442 */         dbo.setObjectLoadType(2);
/* 443 */         dbo.setAttrValues(resObj.getAllAttr());
/*     */       }
/*     */       else {
/* 446 */         dbo = null;
/*     */       }
/*     */     } catch (Exception ex) {
/* 449 */       ex.printStackTrace();
/* 450 */       throw new UserException(ex);
/*     */     } finally {
/* 452 */       closeConn(conn);
/*     */     }
/* 454 */     return dbo;
/*     */   }
/*     */ 
/*     */   public GenericDO getObject(IBoActionContext actionContext, GenericDO dbo) throws Exception {
/* 458 */     Connection conn = null;
/*     */     try {
/* 460 */       String dsName = getDsName(actionContext);
/* 461 */       conn = getConn(dsName);
/* 462 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 463 */       GenericDO resObj = impl.getResourceObject(dbo.getObjectNum(), new ResourceObjectType(0), null, conn);
/* 464 */       if (resObj != null) {
/* 465 */         dbo.setObjectLoadType(3);
/* 466 */         dbo.setAttrValues(resObj.getAllAttr());
/*     */       }
/*     */       else {
/* 469 */         dbo = null;
/*     */       }
/*     */     } catch (Exception ex) {
/* 472 */       ex.printStackTrace();
/* 473 */       throw new UserException(ex);
/*     */     } finally {
/* 475 */       closeConn(conn);
/*     */     }
/* 477 */     return dbo;
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(GenericDO dboTemplate) throws Exception {
/* 481 */     return getObjByCuid(null, dboTemplate);
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(IBoActionContext actionContext, GenericDO dboTemplate) throws Exception {
/* 485 */     GenericDO dbo = null;
/* 486 */     DataObjectList doList = getObjectsBySql(actionContext, "CUID='" + dboTemplate.getCuid() + "'", dboTemplate, 0);
/* 487 */     if (doList.size() > 0) {
/* 488 */       dbo = (GenericDO)doList.get(0);
/*     */     }
/* 490 */     return dbo;
/*     */   }
/*     */ 
/*     */   public String getLabelCnByCuid(IBoActionContext actionContext, String cuid) throws Exception {
/* 494 */     Class[] colClassType = { String.class };
/* 495 */     String className = GenericDO.parseClassNameFromCuid(cuid);
/* 496 */     GenericDO dbo = null;
/* 497 */     String labelCn = null;
/* 498 */     String sql = "SELECT LABEL_CN FROM " + className + " WHERE CUID='" + cuid + "'";
/* 499 */     DataObjectList list = super.selectDBOs(sql, colClassType);
/* 500 */     if (list.size() > 0) {
/* 501 */       dbo = (GenericDO)list.get(0);
/* 502 */       labelCn = dbo.getAttrString("1");
/*     */     }
/* 504 */     return labelCn;
/*     */   }
/*     */ 
/*     */   public Map getLabelCnsByCuids(String[] cuids) throws Exception {
/* 508 */     Map res = new HashMap();
/* 509 */     if (cuids.length > 0) {
/* 510 */       Class[] colClassType = { String.class, String.class };
/* 511 */       String className = GenericDO.parseClassNameFromCuid(cuids[0]);
/* 512 */       String sql = "SELECT CUID,LABEL_CN FROM " + className + " WHERE CUID in ('";
/* 513 */       for (int i = 0; i < cuids.length; i++) {
/* 514 */         sql = sql + "'" + cuids[i] + "','";
/*     */       }
/* 516 */       sql = sql.substring(0, sql.length() - 2) + ")";
/* 517 */       DataObjectList list = super.selectDBOs(sql, colClassType);
/* 518 */       for (GenericDO dbo : list) {
/* 519 */         String qcuid = dbo.getAttrString("1");
/* 520 */         String labelCn = dbo.getAttrString("2");
/* 521 */         res.put(qcuid, labelCn);
/*     */       }
/*     */     }
/* 524 */     return res;
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjsByCuids(List<String> cuids, GenericDO dboTemplate) throws Exception {
/* 528 */     return getObjsByCuids(new BoActionContext(), cuids, dboTemplate);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjsByCuids(IBoActionContext actionContext, List<String> cuids, GenericDO dboTemplate) throws Exception {
/* 532 */     DataObjectList dbos = new DataObjectList();
/* 533 */     for (String cuid : cuids) {
/*     */       try {
/* 535 */         dboTemplate.setCuid(cuid);
/* 536 */         GenericDO dtoTemplate = dboTemplate;
/* 537 */         String clazzName = GenericDO.parseClassNameFromCuid(cuid);
/* 538 */         if ((clazzName == null) || (!clazzName.equals(dboTemplate.getClassName()))) {
/* 539 */           GenericDO dbo = new GenericDO();
/* 540 */           dbo.setClassName(clazzName);
/* 541 */           dbo.setCuid(cuid);
/* 542 */           dtoTemplate = dbo.createInstanceByClassName();
/* 543 */           dtoTemplate.setCuid(cuid);
/* 544 */           LogHome.getLog().warn("dbo模块类型与cuid不匹配，dboTemplateClassName=" + dboTemplate.getClassName() + ", cuid=" + cuid);
/*     */         }
/* 546 */         GenericDO dbo = getObjByCuid(actionContext, dtoTemplate);
/* 547 */         if (dbo != null)
/* 548 */           dbos.add(dbo);
/*     */       } catch (Exception ex) {
/* 550 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/* 553 */     return dbos;
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjByAttrs(IBoQueryContext context, GenericDO dboTemplate) throws Exception {
/* 557 */     dboTemplate.clearDefaultValue();
/* 558 */     String sql = "";
/* 559 */     Map attrs = dboTemplate.getAllAttr();
/* 560 */     if (attrs.size() == 0) {
/* 561 */       throw new UserException("没有指定条件属性 ！");
/*     */     }
/*     */ 
/* 564 */     Iterator it = attrs.keySet().iterator();
/* 565 */     while (it.hasNext()) {
/* 566 */       String attrName = (String)it.next();
/* 567 */       Object attrValue = dboTemplate.getAttrValue(attrName);
/* 568 */       if (attrValue == null)
/*     */         continue;
/* 570 */       if (sql.length() > 0) sql = sql + " and ";
/* 571 */       Class attrType = dboTemplate.getAttrType(attrName);
/* 572 */       if ((attrType == Boolean.class) || (attrType == Boolean.TYPE)) {
/* 573 */         int val = ((Boolean)attrValue).booleanValue() ? 1 : 0;
/* 574 */         sql = sql + attrName + "=" + val;
/* 575 */       } else if ((attrType == Long.class) || (attrType == Long.TYPE)) {
/* 576 */         sql = sql + attrName + "=" + attrValue;
/* 577 */       } else if ((attrType == Double.class) || (attrType == Double.TYPE)) {
/* 578 */         sql = sql + attrName + "=" + attrValue;
/* 579 */       } else if (attrType == String.class) {
/* 580 */         sql = sql + attrName + "='" + attrValue + "'";
/* 581 */       } else if (attrType == Timestamp.class) {
/* 582 */         if (context == null) {
/* 583 */           throw new UserException("不支持此数据类型" + attrType + " ！");
/*     */         }
/* 585 */         DbType dbType = DbConnManager.getInstance().getDbType(context.getDsName());
/* 586 */         String sqlVal = SqlHelper.getTimestamp(dbType, (Timestamp)attrValue);
/* 587 */         sql = sql + attrName + "=" + sqlVal;
/*     */       }
/*     */       else {
/* 590 */         throw new UserException("不支持此数据类型" + attrType + " ！");
/*     */       }
/*     */     }
/* 593 */     return getObjectsBySql(context, sql, dboTemplate, 0);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjByAttrValues(IBoQueryContext context, GenericDO dboTemplate, String attrName, List<String> values) throws Exception {
/* 597 */     String sql = "";
/* 598 */     if (attrName == null) {
/* 599 */       throw new UserException("没有指定属性名 ！");
/*     */     }
/*     */ 
/* 602 */     if ((values == null) || (values.size() == 0)) {
/* 603 */       throw new UserException("没有指定属性条件 ！");
/*     */     }
/* 605 */     sql = sql + attrName + " in( ";
/* 606 */     for (int i = 0; i < values.size(); i++) {
/* 607 */       if (i == 0)
/* 608 */         sql = sql + "'" + (String)values.get(i) + "'";
/*     */       else {
/* 610 */         sql = sql + " , '" + (String)values.get(i) + "'";
/*     */       }
/*     */     }
/* 613 */     sql = sql + ") ";
/* 614 */     return getObjectsBySql(context, sql, dboTemplate, 0);
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjects(IBoActionContext context, long[] objectIds, GenericDO dboTemplate)
/*     */     throws Exception
/*     */   {
/* 622 */     DataObjectList dboList = new DataObjectList();
/* 623 */     if (objectIds.length == 0) return dboList;
/*     */ 
/* 625 */     Connection conn = null;
/*     */     try {
/* 627 */       String dsName = getDsName(context);
/* 628 */       conn = getConn(dsName);
/* 629 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 630 */       List resObjList = impl.getResourceObjects(objectIds, new ResourceObjectType(0), conn);
/* 631 */       dboTemplate.setObjectLoadType(3);
/* 632 */       dboList = ObjectDoHelper.cloneDataObjList(dboTemplate, 0, resObjList);
/*     */     } catch (Exception ex) {
/* 634 */       ex.printStackTrace();
/* 635 */       throw new UserException(ex);
/*     */     } finally {
/* 637 */       closeConn(conn);
/*     */     }
/* 639 */     return dboList;
/*     */   }
/*     */ 
/*     */   public DataObjectList getAllObjByClass(GenericDO dboTemplate, int objGetType) throws Exception {
/* 643 */     DataObjectList dboList = new DataObjectList();
/* 644 */     Connection conn = null;
/*     */     try {
/* 646 */       String dsName = getDsName(null);
/* 647 */       conn = getConn(dsName);
/* 648 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 649 */       List resObjList = impl.getResObjectsByClass(dboTemplate.getClassName(), false, new ResourceObjectType(objGetType), conn);
/* 650 */       dboTemplate.setObjectLoadType(objGetType);
/* 651 */       dboList = ObjectDoHelper.cloneDataObjList(dboTemplate, objGetType, resObjList);
/*     */     } catch (Exception ex) {
/* 653 */       ex.printStackTrace();
/* 654 */       throw new UserException(ex);
/*     */     } finally {
/* 656 */       closeConn(conn);
/*     */     }
/* 658 */     return dboList;
/*     */   }
/*     */   public DataObjectList getObjectsBySql(IBoActionContext actionContext, String sql, GenericDO dboTemplate, int objGetType) throws Exception {
/* 661 */     DataObjectList dboList = null;
/*     */ 
/* 663 */     dboList = getObjectsByResSql(actionContext, sql, dboTemplate, objGetType);
/*     */ 
/* 667 */     return dboList;
/*     */   }
/*     */   public DataObjectList getObjectsBySql(String sql, GenericDO dboTemplate, int objGetType) throws Exception {
/* 670 */     DataObjectList dboList = null;
/*     */ 
/* 672 */     dboList = getObjectsByResSql(null, sql, dboTemplate, objGetType);
/*     */ 
/* 676 */     return dboList;
/*     */   }
/*     */ 
/*     */   private DataObjectList getObjectsByResSql(IBoActionContext actionContext, String sql, GenericDO dboTemplate, int objGetType)
/*     */     throws Exception
/*     */   {
/* 718 */     long startTime = System.currentTimeMillis();
/* 719 */     DataObjectList dboList = new DataObjectList();
/* 720 */     Connection conn = null;
/*     */     try {
/* 722 */       String dsName = getDsName(actionContext);
/* 723 */       conn = getConn(dsName);
/* 724 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 725 */       if ((sql != null) && (sql.trim().length() > 0)) {
/* 726 */         List resObjList = impl.getResObjectsBySql(dboTemplate.getClassName(), sql, new ResourceObjectType(objGetType), conn);
/* 727 */         dboTemplate.setObjectLoadType(objGetType);
/* 728 */         dboList = ObjectDoHelper.cloneDataObjList(dboTemplate, objGetType, resObjList);
/*     */       } else {
/* 730 */         Class clientClass = Reflection.getCallerClass(8);
/* 731 */         String clientClassName = clientClass != null ? clientClass.getName() : "";
/* 732 */         LogHome.getLog().warn("Sql条件为空，callerClassName=" + clientClassName);
/* 733 */         List resObjList = impl.getResObjectsByClass(dboTemplate.getClassName(), false, new ResourceObjectType(objGetType), conn);
/* 734 */         dboTemplate.setObjectLoadType(objGetType);
/* 735 */         dboList = ObjectDoHelper.cloneDataObjList(dboTemplate, objGetType, resObjList);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */       long expendTime;
/*     */       Class clientClass;
/*     */       String clientClassName;
/* 739 */       if ((!getDsName(actionContext).equals("cacheDB")) || (dboTemplate.getClassName() == null) || (!dboTemplate.getClassName().equals("USER_HAVE_OBJECT")))
/*     */       {
/* 742 */         LogHome.getLog().error("className=" + dboTemplate.getClassName() + ",sql=" + sql, ex);
/*     */       }
/* 744 */       throw new UserException(ex);
/*     */     } finally {
/* 746 */       closeConn(conn);
/* 747 */       long expendTime = System.currentTimeMillis() - startTime;
/* 748 */       if ((expendTime > TnmsDrmCfg.getInstance().getMaxDbTime()) || (dboList.size() > TnmsDrmCfg.getInstance().getMaxQueryResultSize()))
/*     */       {
/* 750 */         Class clientClass = Reflection.getCallerClass(8);
/* 751 */         String clientClassName = clientClass != null ? clientClass.getName() : "";
/*     */ 
/* 753 */         LogHome.getLog().warn("DbCall<threadId=" + ThreadHelper.getCurrentThreadId() + ", dbTime=" + expendTime + ", resultSize=" + dboList.size() + "> getObjectsByResSql<className=" + dboTemplate.getClassName() + ", callerClassName=" + clientClassName + ", sql=" + sql);
/*     */       }
/*     */     }
/*     */ 
/* 757 */     return dboList;
/*     */   }
/*     */ 
/*     */   private DataObjectList getObjectsByCmdSql(String sql, GenericDO dboTemplate) throws Exception
/*     */   {
/* 762 */     DataObjectList dboList = new DataObjectList();
/* 763 */     String querySql = "select * from " + dboTemplate.getTableName();
/* 764 */     if ((sql != null) && (sql.trim().length() > 0)) {
/* 765 */       querySql = querySql + " where " + sql;
/*     */     } else {
/* 767 */       Class clientClass = Reflection.getCallerClass(8);
/* 768 */       String clientClassName = clientClass != null ? clientClass.getName() : "";
/* 769 */       LogHome.getLog().warn("Sql条件为空，callerClassName=" + clientClassName);
/*     */     }
/* 771 */     DboCollection dbos = super.selectDBOs(querySql, new GenericDO[] { dboTemplate });
/*     */ 
/* 773 */     for (int i = 0; (dbos != null) && (i < dbos.size()); i++) {
/* 774 */       GenericDO dbo = (GenericDO)dbos.getAttrField(dboTemplate.getClassName(), i);
/* 775 */       dboList.add(dbo);
/*     */     }
/*     */ 
/* 783 */     return dboList;
/*     */   }
/*     */ 
/*     */   public int getCountOfClass(String className) throws Exception {
/* 787 */     int count = 0;
/* 788 */     Connection conn = null;
/*     */     try {
/* 790 */       String dsName = getDsName(null);
/* 791 */       conn = getConn(dsName);
/* 792 */       GetObjectImpl impl = new GetObjectImpl(getDbContext(dsName));
/* 793 */       count = impl.getCountOfResClass(className, conn);
/*     */     } catch (Exception e) {
/* 795 */       e.printStackTrace();
/* 796 */       throw new UserException(e);
/*     */     } finally {
/* 798 */       closeConn(conn);
/*     */     }
/* 800 */     return count;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.AbstractObjectDAO
 * JD-Core Version:    0.6.0
 */