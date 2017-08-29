/*      */ package com.boco.transnms.server.dao.base.internal;
/*      */ 
/*      */ import com.boco.common.util.db.DbConnManager;
/*      */ import com.boco.common.util.db.DbContext;
/*      */ import com.boco.common.util.db.DbHelper;
/*      */ import com.boco.common.util.db.SqlHelper;
/*      */ import com.boco.common.util.db.TransactionFactory;
/*      */ import com.boco.common.util.debug.LogHome;
/*      */ import com.boco.common.util.except.UserException;
/*      */ import com.boco.transnms.common.dto.base.DataObjectList;
/*      */ import com.boco.transnms.common.dto.base.GenericDO;
/*      */ import com.cmcc.tm.middleware.exception.ServiceException;
/*      */ import java.io.Serializable;
/*      */ import java.sql.Connection;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class OptObjectImpl
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -3077484770114455215L;
/*      */ 
/*      */   private static DbContext getDbContext(String dsName)
/*      */   {
/*   57 */     DbContext dbContext = null;
/*      */     try {
/*   59 */       dbContext = DbConnManager.getInstance().getDbContext(dsName);
/*      */     } catch (Exception e) {
/*   61 */       LogHome.getLog().error("获取DbContext出错！", e);
/*      */     }
/*   63 */     return dbContext;
/*      */   }
/*      */ 
/*      */   private static boolean isNotTransAction() {
/*   67 */     return TransactionFactory.getInstance().getTransaction() == null;
/*      */   }
/*      */ 
/*      */   private static Connection getConn(String dsName) {
/*   71 */     Connection conn = null;
/*      */     try {
/*   73 */       DbContext dbContext = TransactionFactory.getInstance().fetchTranscDbContext(dsName);
/*   74 */       if (dbContext == null)
/*   75 */         conn = DbConnManager.getInstance().fetchDbConn(dsName);
/*      */       else
/*   77 */         conn = dbContext.getDbConn();
/*      */     }
/*      */     catch (Exception e) {
/*   80 */       LogHome.getLog().error("[method]:getConn [message]:error get connection by DbConnManager.getInstance().fetchDbConn()");
/*   81 */       LogHome.getLog().error("", e);
/*   82 */       LogHome.getLog().error("DbConnManager.getInstance().fetchDbConn() return null！");
/*      */     }
/*   84 */     return conn;
/*      */   }
/*      */ 
/*      */   public static GenericDO createResourceObject(GenericDO res, Connection conn, String dsName)
/*      */     throws ServiceException
/*      */   {
/*  105 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  106 */       LogHome.getLog().debug("[method]:createResourceObject");
/*  107 */       LogHome.getLog().debug("[params]:");
/*  108 */       LogHome.getLog().debug("className=" + res.getClassName());
/*  109 */       Map resMap = res.getAllAttr();
/*  110 */       if (resMap == null) {
/*  111 */         LogHome.getLog().debug("resMap:null !");
/*      */       } else {
/*  113 */         LogHome.getLog().debug("resMap:{");
/*  114 */         Iterator itr = resMap.keySet().iterator();
/*  115 */         while (itr.hasNext()) {
/*  116 */           String attrName = (String)itr.next();
/*  117 */           LogHome.getLog().debug(attrName + "=" + resMap.get(attrName));
/*      */         }
/*  119 */         LogHome.getLog().debug("}");
/*      */       }
/*      */     }
/*  122 */     res = createOneResourceObject(res, conn, dsName);
/*      */ 
/*  124 */     return res;
/*      */   }
/*      */ 
/*      */   public static DataObjectList createResourceObjects(DataObjectList list, Connection conn, String dsName)
/*      */     throws ServiceException, SQLException
/*      */   {
/*  137 */     if (list.size() > 0) {
/*  138 */       Map clazzListMap = new HashMap();
/*  139 */       for (int i = 0; i < list.size(); i++) {
/*  140 */         GenericDO dbo = (GenericDO)list.get(i);
/*  141 */         String className = dbo.getClassName();
/*  142 */         DataObjectList dbos = (DataObjectList)clazzListMap.get(className);
/*  143 */         if (dbos == null) {
/*  144 */           dbos = new DataObjectList();
/*  145 */           clazzListMap.put(className, dbos);
/*      */         }
/*  147 */         dbos.add(dbo);
/*      */       }
/*  149 */       if (clazzListMap.keySet().size() > 1) {
/*  150 */         LogHome.getLog().warn("createResourceObjects一次创建多类对象：" + clazzListMap.keySet().toString());
/*      */       }
/*  152 */       String className = "";
/*      */       try {
/*  154 */         if (isNotTransAction()) {
/*  155 */           conn.setAutoCommit(false);
/*      */         }
/*  157 */         Iterator iterator = clazzListMap.keySet().iterator();
/*  158 */         while (iterator.hasNext()) {
/*  159 */           className = (String)iterator.next();
/*  160 */           DataObjectList dbos = (DataObjectList)clazzListMap.get(className);
/*  161 */           if (dbos.size() > 0)
/*  162 */             createResourceObjectsPrivate(dbos, conn, dsName);
/*      */         }
/*      */       }
/*      */       catch (Exception ex) {
/*  166 */         LogHome.getLog().error("批量入库出错：className=" + className, ex);
/*  167 */         throw new ServiceException(ex);
/*      */       } finally {
/*  169 */         if (isNotTransAction()) {
/*  170 */           conn.setAutoCommit(true);
/*      */         }
/*      */       }
/*      */     }
/*  174 */     return list;
/*      */   }
/*      */ 
/*      */   private static DataObjectList createResourceObjectsPrivate(DataObjectList list, Connection conn, String dsName) throws ServiceException, SQLException {
/*  178 */     if (list.size() > 0) {
/*  179 */       PreparedStatement ps = null;
/*  180 */       DbContext dbContext = getDbContext(dsName);
/*  181 */       String sql = "";
/*  182 */       int i = 0;
/*      */       try {
/*  184 */         GenericDO dboTemplate = (GenericDO)list.get(0);
/*  185 */         List modList = new ArrayList();
/*      */ 
/*  187 */         sql = SqlHelper.createInsertSql(dboTemplate, modList, getDbContext(dsName).getDbType(), true);
/*  188 */         String[] names = new String[modList.size()];
/*  189 */         for (i = 0; i < modList.size(); i++) {
/*  190 */           names[i] = ((String)modList.get(i));
/*      */         }
/*  192 */         ps = conn.prepareStatement(sql);
/*      */ 
/*  194 */         for (i = 0; i < list.size(); i++) {
/*  195 */           GenericDO dbo = (GenericDO)list.get(i);
/*  196 */           Map attributeMap = dbo.getAllAttr();
/*  197 */           if (attributeMap.size() > modList.size()) {
/*  198 */             LogHome.getLog().error("插入对象比模板属性多，属性未入库：" + dbo);
/*      */           }
/*  200 */           List values = new ArrayList();
/*  201 */           long objectID = dbo.getObjectNum();
/*  202 */           objectID = ClassUtils.getInstance().createObjID(dbo.getClassName(), dbContext.getServerId());
/*  203 */           dbo.setObjectNum(new Long(objectID).longValue());
/*  204 */           SqlHelper.setPrepareStatement(dbo, names, values, ps, dbContext.getDbCharset());
/*  205 */           ps.addBatch();
/*  206 */           if ((!isNotTransAction()) || 
/*  207 */             (i + 1 % dbContext.getBatchSize() != 0)) continue;
/*  208 */           ps.executeBatch();
/*  209 */           conn.commit();
/*  210 */           ps.clearBatch();
/*  211 */           if (conn == null) {
/*  212 */             conn = getConn(dsName);
/*  213 */             conn.setAutoCommit(false);
/*  214 */             ps = conn.prepareStatement(sql);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  219 */         ps.executeBatch();
/*  220 */         if (isNotTransAction())
/*  221 */           conn.commit();
/*      */       }
/*      */       catch (Exception e) {
/*  224 */         if (isNotTransAction()) {
/*  225 */           conn.rollback();
/*      */         }
/*  227 */         throw new ServiceException("[method]:createOneResourceObject [msg]:" + e.getMessage() + "; [sql-sy]:" + sql + " i=" + i);
/*      */       }
/*      */       finally {
/*  230 */         DbHelper.closeStatement(ps);
/*      */       }
/*      */     }
/*  233 */     return list;
/*      */   }
/*      */ 
/*      */   public static GenericDO createResourceObject(String className, Map attributeMap, Connection conn, String dsName)
/*      */     throws ServiceException
/*      */   {
/*  255 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  256 */       LogHome.getLog().debug("[method]:createResourceObject");
/*  257 */       LogHome.getLog().debug("[params]:");
/*  258 */       LogHome.getLog().debug("className=" + className);
/*  259 */       if (attributeMap == null) {
/*  260 */         LogHome.getLog().debug("resMap:null !");
/*      */       } else {
/*  262 */         LogHome.getLog().debug("resMap:{");
/*  263 */         Iterator itr = attributeMap.keySet().iterator();
/*  264 */         while (itr.hasNext()) {
/*  265 */           String attrName = (String)itr.next();
/*  266 */           LogHome.getLog().debug(attrName + "=" + attributeMap.get(attrName));
/*      */         }
/*  268 */         LogHome.getLog().debug("}");
/*      */       }
/*      */     }
/*  271 */     GenericDO template = new GenericDO(className);
/*  272 */     GenericDO dbo = template.createInstanceByClassName();
/*  273 */     dbo.setAttrValues(attributeMap);
/*      */ 
/*  275 */     GenericDO res = createOneResourceObject(dbo, conn, dsName);
/*      */ 
/*  277 */     return res;
/*      */   }
/*      */ 
/*      */   public static void deleteAllResObjectsOfClass(String className, boolean isForeverDelete, Connection conn)
/*      */     throws ServiceException
/*      */   {
/*  359 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  360 */       LogHome.getLog().debug("[method]:deleteAllResObjectsOfClass");
/*  361 */       LogHome.getLog().debug("[params]:");
/*  362 */       LogHome.getLog().debug("className=" + className);
/*  363 */       LogHome.getLog().debug("isForeverDelete=" + isForeverDelete);
/*      */     }
/*  365 */     if ((className == null) || ("".equalsIgnoreCase(className.trim()))) {
/*  366 */       String msg = "[method]:deleteAllResObjectsOfClass [className]:" + className + " [message]:className is null !";
/*      */ 
/*  368 */       LogHome.getLog().error(msg);
/*  369 */       throw new ServiceException(msg);
/*      */     }
/*  371 */     PreparedStatement ps = null;
/*      */     try {
/*  373 */       String sql = "";
/*      */ 
/*  375 */       sql = "DELETE FROM " + className;
/*  376 */       ps = conn.prepareStatement(sql);
/*  377 */       ps.execute();
/*      */     } catch (Exception e) {
/*  379 */       String msg = "[method]:deleteAllResObjectsOfClass [className]:" + className + " [message]:" + e.getMessage() + " [reason]:DBException";
/*      */ 
/*  382 */       LogHome.getLog().error(msg);
/*  383 */       LogHome.getLog().error("", e);
/*  384 */       throw new ServiceException(msg, e);
/*      */     } finally {
/*  386 */       if (ps != null)
/*      */         try {
/*  388 */           ps.close();
/*      */         } catch (SQLException e) {
/*  390 */           LogHome.getLog().error("", e);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void deleteResourceObject(long objectId, boolean isForeverDelete, boolean isAll, Connection conn)
/*      */     throws ServiceException
/*      */   {
/*  413 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  414 */       LogHome.getLog().debug("[method]:deleteResourceObject");
/*  415 */       LogHome.getLog().debug("[params]:");
/*  416 */       LogHome.getLog().debug("objectId=" + objectId);
/*  417 */       LogHome.getLog().debug("isForeverDelete=" + isForeverDelete);
/*      */     }
/*      */ 
/*  420 */     String className = ClassUtils.getInstance().getClassName(objectId);
/*  421 */     if ((className == null) || ("".equals(className.trim()))) {
/*  422 */       throw new ServiceException();
/*      */     }
/*  424 */     Statement st = null;
/*      */     try {
/*  426 */       int ret = 0;
/*      */ 
/*  428 */       st = conn.createStatement();
/*  429 */       String sql = "DELETE FROM " + className + " WHERE OBJECTID=" + objectId;
/*  430 */       ret = st.executeUpdate(sql);
/*  431 */       if (LogHome.getLog().isDebugEnabled()) {
/*  432 */         LogHome.getLog().debug("SQL: " + sql);
/*      */       }
/*  434 */       if (ret == 0) {
/*  435 */         String msg = "[method]:deleteResourceObject [className]:" + className + "[objectid]:" + objectId + " [message]:no recode effect!";
/*      */ 
/*  438 */         LogHome.getLog().error(msg);
/*  439 */         throw new ServiceException(msg);
/*      */       }
/*      */     } catch (SQLException se) {
/*  442 */       String msg = "[method]:deleteResourceObject [className] : " + className + " [objectid]:" + objectId + " [message]:" + se.getMessage() + "[errorCode] : " + se.getErrorCode() + "[getNextException] : " + se.getNextException() + ";";
/*      */ 
/*  445 */       LogHome.getLog().error(msg);
/*  446 */       LogHome.getLog().error("", se);
/*  447 */       throw new ServiceException(msg, se);
/*      */     } catch (Exception e) {
/*  449 */       String msg = "[method]:deleteResourceObject [className] : " + className + " [objectid]:" + objectId + " [message]:" + e.getMessage() + " [reason]:DBException";
/*      */ 
/*  452 */       LogHome.getLog().error(msg);
/*  453 */       LogHome.getLog().error("", e);
/*  454 */       throw new ServiceException(msg, e);
/*      */     } finally {
/*  456 */       if (st != null)
/*      */         try {
/*  458 */           st.close();
/*      */         } catch (SQLException e) {
/*  460 */           LogHome.getLog().error("", e);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void deleteResourceObjectsByObjectId(long[] objectIds, Connection conn, String dsName)
/*      */     throws ServiceException, SQLException
/*      */   {
/*  476 */     if ((objectIds != null) && (objectIds.length > 0)) {
/*  477 */       Map sqlMap = new HashMap();
/*  478 */       for (int i = 0; i < objectIds.length; i++)
/*      */       {
/*  480 */         String className = ClassUtils.getInstance().getClassName(objectIds[i]);
/*  481 */         if ((className == null) || ("".equalsIgnoreCase(className))) {
/*  482 */           String msg = "[method]:deleteResourceObjects [message]:className is null !";
/*  483 */           LogHome.getLog().error(msg);
/*  484 */           throw new ServiceException(msg);
/*      */         }
/*      */ 
/*  487 */         List sqlList = (List)sqlMap.get(className);
/*  488 */         if (sqlList == null) {
/*  489 */           sqlList = new ArrayList();
/*  490 */           sqlMap.put(className, sqlList);
/*      */         }
/*  492 */         sqlList.add(Long.valueOf(objectIds[i]));
/*      */       }
/*  494 */       PreparedStatement ps = null;
/*  495 */       String sql = "";
/*      */       try {
/*  497 */         Iterator iterator = sqlMap.keySet().iterator();
/*  498 */         while (iterator.hasNext()) {
/*  499 */           String tableName = iterator.next().toString();
/*  500 */           sql = "DELETE FROM " + tableName + " WHERE OBJECTID =?";
/*  501 */           if (isNotTransAction()) {
/*  502 */             conn.setAutoCommit(false);
/*      */           }
/*  504 */           ps = conn.prepareStatement(sql);
/*  505 */           List sqlList = (List)sqlMap.get(tableName);
/*  506 */           for (int i = 0; i < sqlList.size(); i++) {
/*  507 */             String objectId = sqlList.get(i).toString();
/*  508 */             ps.setLong(1, Long.parseLong("" + objectId));
/*  509 */             ps.addBatch();
/*  510 */             if ((!isNotTransAction()) || 
/*  511 */               (i + 1 % getDbContext(dsName).getBatchSize() != 0)) continue;
/*  512 */             ps.executeBatch();
/*  513 */             conn.commit();
/*  514 */             ps.clearBatch();
/*  515 */             if (conn == null) {
/*  516 */               conn = getConn(dsName);
/*  517 */               conn.setAutoCommit(false);
/*  518 */               ps = conn.prepareStatement(sql);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  523 */           ps.executeBatch();
/*  524 */           if (isNotTransAction())
/*  525 */             conn.commit();
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  530 */         if (isNotTransAction()) {
/*  531 */           conn.rollback();
/*      */         }
/*  533 */         LogHome.getLog().error("delete error:", e);
/*      */       } finally {
/*      */         try {
/*  536 */           if (isNotTransAction())
/*  537 */             conn.setAutoCommit(true);
/*      */         }
/*      */         catch (SQLException e) {
/*  540 */           LogHome.getLog().error("", e);
/*      */         }
/*  542 */         DbHelper.closeStatement(ps);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void deleteResourceObjectsByCuid(String[] cuids, Connection conn, String dsName)
/*      */     throws ServiceException, SQLException
/*      */   {
/*  555 */     if ((cuids != null) && (cuids.length > 0)) {
/*  556 */       Map sqlMap = new HashMap();
/*  557 */       for (int i = 0; i < cuids.length; i++)
/*      */       {
/*  559 */         String cuid = cuids[i];
/*  560 */         String className = GenericDO.parseClassNameFromCuid(cuid);
/*  561 */         if ((className == null) || ("".equalsIgnoreCase(className))) {
/*  562 */           String msg = "[method]:deleteResourceObjects [message]:className is null !";
/*  563 */           LogHome.getLog().error(msg);
/*  564 */           throw new ServiceException(msg);
/*      */         }
/*      */ 
/*  567 */         List sqlList = (List)sqlMap.get(className);
/*  568 */         if (sqlList == null) {
/*  569 */           sqlList = new ArrayList();
/*  570 */           sqlMap.put(className, sqlList);
/*      */         }
/*  572 */         sqlList.add(cuid);
/*      */       }
/*  574 */       PreparedStatement ps = null;
/*  575 */       String sql = "";
/*      */       try {
/*  577 */         Iterator iterator = sqlMap.keySet().iterator();
/*  578 */         while (iterator.hasNext()) {
/*  579 */           String tableName = iterator.next().toString();
/*  580 */           sql = "DELETE FROM " + tableName + " WHERE CUID =?";
/*  581 */           if (isNotTransAction()) {
/*  582 */             conn.setAutoCommit(false);
/*      */           }
/*  584 */           ps = conn.prepareStatement(sql);
/*  585 */           List sqlList = (List)sqlMap.get(tableName);
/*  586 */           for (int i = 0; i < sqlList.size(); i++) {
/*  587 */             String cuid = sqlList.get(i).toString();
/*  588 */             ps.setString(1, cuid);
/*  589 */             ps.addBatch();
/*  590 */             if ((!isNotTransAction()) || 
/*  591 */               (i + 1 % getDbContext(dsName).getBatchSize() != 0)) continue;
/*  592 */             ps.executeBatch();
/*  593 */             conn.commit();
/*  594 */             ps.clearBatch();
/*  595 */             if (conn == null) {
/*  596 */               conn = getConn(dsName);
/*  597 */               conn.setAutoCommit(false);
/*  598 */               ps = conn.prepareStatement(sql);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  603 */           ps.executeBatch();
/*  604 */           if (isNotTransAction())
/*  605 */             conn.commit();
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  610 */         if (isNotTransAction()) {
/*  611 */           conn.rollback();
/*      */         }
/*  613 */         LogHome.getLog().error("delete error:", e);
/*      */       } finally {
/*      */         try {
/*  616 */           if (isNotTransAction())
/*  617 */             conn.setAutoCommit(true);
/*      */         }
/*      */         catch (SQLException e) {
/*  620 */           LogHome.getLog().error("", e);
/*      */         }
/*  622 */         DbHelper.closeStatement(ps);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static List updateResourceObject(GenericDO dbo, Connection conn, String dsName)
/*      */     throws ServiceException
/*      */   {
/*  638 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  639 */       LogHome.getLog().debug("[method]:updateResourceObject");
/*  640 */       LogHome.getLog().debug("[params]:");
/*  641 */       LogHome.getLog().debug("className=" + dbo.getClassName());
/*  642 */       Map resMap = dbo.getAllAttr();
/*  643 */       if (resMap == null) {
/*  644 */         LogHome.getLog().debug("resMap:null !");
/*      */       } else {
/*  646 */         LogHome.getLog().debug("resMap:{");
/*  647 */         Iterator itr = resMap.keySet().iterator();
/*  648 */         while (itr.hasNext()) {
/*  649 */           String attrName = (String)itr.next();
/*  650 */           LogHome.getLog().debug(attrName + "=" + resMap.get(attrName));
/*      */         }
/*  652 */         LogHome.getLog().debug("}");
/*      */       }
/*      */     }
/*      */ 
/*  656 */     if (dbo == null) {
/*  657 */       String msg = "[method]:updateResourceObject [message]:resObject is null !";
/*  658 */       LogHome.getLog().error(msg);
/*  659 */       throw new ServiceException(msg);
/*      */     }
/*  661 */     DbContext dbContext = getDbContext(dsName);
/*      */ 
/*  663 */     GenericDO dbResObject = null;
/*      */     try {
/*  665 */       dbResObject = ORMapingUtil.getResourceObjectById(dbo.getObjectNum(), null, conn, dbContext);
/*      */     } catch (Exception e) {
/*  667 */       String msg = "[method]:updateResourceObject [className]: " + dbo.getClassName() + " [message]:" + e.getMessage() + " [reason]:ObjectNotFoundException";
/*      */ 
/*  669 */       LogHome.getLog().error(msg);
/*  670 */       LogHome.getLog().error("", e);
/*  671 */       throw new ServiceException(msg, e);
/*      */     }
/*      */ 
/*  674 */     List modList = new ArrayList();
/*  675 */     PreparedStatement ps = null;
/*      */     try {
/*  677 */       String sql = SqlHelper.createUpdateSql(dbo, dbResObject, modList);
/*  678 */       String psql = SqlHelper.prepareSql(dbContext.getDbCharset(), sql);
/*  679 */       ps = conn.prepareStatement(psql);
/*  680 */       SqlHelper.setPrepareStatement(dbo, ps, dbContext.getDbCharset());
/*  681 */       LogHome.getLog().info("SQL=" + sql);
/*  682 */       ps.executeUpdate();
/*      */     } catch (Exception ex) {
/*  684 */       LogHome.getLog().error("", ex);
/*      */     } finally {
/*  686 */       DbHelper.closeStatement(ps);
/*      */     }
/*  688 */     return modList;
/*      */   }
/*      */ 
/*      */   public static List updateResourceObjects(DataObjectList list, Map attrs, boolean byCuidOrObjectid, Connection conn, String dsName)
/*      */     throws ServiceException, SQLException
/*      */   {
/*  705 */     List modList = new ArrayList();
/*      */ 
/*  707 */     if (list.size() > 0) {
/*  708 */       GenericDO dto = (GenericDO)list.get(0);
/*  709 */       Map attrMap = SqlHelper.getUpdateDBAttrs(dto, attrs);
/*      */ 
/*  711 */       if (attrMap.size() > 0)
/*      */       {
/*  713 */         List l = SqlHelper.getUpdateNames(attrMap, byCuidOrObjectid);
/*  714 */         String[] names = (String[])(String[])l.get(0);
/*  715 */         String[] updateNames = (String[])(String[])l.get(1);
/*      */ 
/*  717 */         DbContext dbContext = getDbContext(dsName);
/*  718 */         PreparedStatement ps = null;
/*  719 */         String sql = "";
/*      */         try {
/*  721 */           if (isNotTransAction()) {
/*  722 */             conn.setAutoCommit(false);
/*      */           }
/*  724 */           sql = SqlHelper.createUpdateSql(dto, updateNames, modList, byCuidOrObjectid);
/*  725 */           String psql = SqlHelper.prepareSql(dbContext.getDbCharset(), sql);
/*  726 */           ps = conn.prepareStatement(psql);
/*  727 */           for (int i = 0; i < list.size(); i++) {
/*  728 */             GenericDO dbo = (GenericDO)list.get(i);
/*  729 */             if (dbo != null) {
/*  730 */               SqlHelper.setPrepareStatement(dbo, names, ps, dbContext.getDbCharset());
/*  731 */               ps.addBatch();
/*  732 */               if ((!isNotTransAction()) || 
/*  733 */                 (i + 1 % dbContext.getBatchSize() != 0)) continue;
/*  734 */               ps.executeBatch();
/*  735 */               conn.commit();
/*  736 */               ps.clearBatch();
/*  737 */               if (conn == null) {
/*  738 */                 conn = getConn(dsName);
/*  739 */                 conn.setAutoCommit(false);
/*  740 */                 ps = conn.prepareStatement(sql);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  746 */           ps.executeBatch();
/*  747 */           if (isNotTransAction())
/*  748 */             conn.commit();
/*      */         }
/*      */         catch (Exception e) {
/*  751 */           if (isNotTransAction()) {
/*  752 */             conn.rollback();
/*      */           }
/*  754 */           LogHome.getLog().error("erro sql:" + sql);
/*  755 */           LogHome.getLog().error("", ex);
/*      */         } finally {
/*      */           try {
/*  758 */             if (isNotTransAction())
/*  759 */               conn.setAutoCommit(true);
/*      */           }
/*      */           catch (SQLException e) {
/*  762 */             LogHome.getLog().error("", e);
/*      */           }
/*  764 */           DbHelper.closeStatement(ps);
/*      */         }
/*      */       }
/*      */     }
/*  768 */     return modList;
/*      */   }
/*      */ 
/*      */   public static void updateAttributeValue(GenericDO dbo, Map updateDBAttrs, boolean byCuidOrObjectid, Connection conn, String dsName)
/*      */     throws Exception
/*      */   {
/*  788 */     long lObjectID = dbo.getObjectNum();
/*  789 */     Map updateAttrs = SqlHelper.getUpdateDBAttrs(dbo, updateDBAttrs);
/*  790 */     if (updateAttrs.keySet().size() > 0)
/*      */     {
/*  793 */       String className = dbo.getClassName();
/*  794 */       if ((className == null) || ("".equals(className))) {
/*  795 */         String msg = "[method]:updateAttributeValue [objectid]:" + lObjectID + " [message]:className is null !";
/*  796 */         LogHome.getLog().error(msg);
/*  797 */         throw new ServiceException(msg);
/*      */       }
/*  799 */       DbContext dbContext = getDbContext(dsName);
/*  800 */       PreparedStatement ps = null;
/*      */       try {
/*  802 */         String lmt = "LAST_MODIFY_TIME";
/*      */ 
/*  804 */         List list = SqlHelper.getUpdateNames(updateAttrs, byCuidOrObjectid);
/*  805 */         String[] names = (String[])(String[])list.get(0);
/*  806 */         String[] updateNames = (String[])(String[])list.get(1);
/*      */ 
/*  808 */         String sql = SqlHelper.createUpdateSql(dbo, updateNames, null, byCuidOrObjectid);
/*  809 */         String psql = SqlHelper.prepareSql(dbContext.getDbCharset(), sql);
/*  810 */         ps = conn.prepareStatement(psql);
/*      */ 
/*  813 */         dbo.getAllAttr().putAll(updateAttrs);
/*  814 */         int columnIndex = SqlHelper.setPrepareStatement(dbo, names, ps, dbContext.getDbCharset());
/*      */ 
/*  816 */         LogHome.getLog().info("SQL=" + sql);
/*  817 */         ps.executeUpdate();
/*      */       } catch (Exception ex) {
/*  819 */         LogHome.getLog().error("", ex);
/*  820 */         throw new ServiceException(ex.getMessage());
/*      */       } finally {
/*  822 */         DbHelper.closeStatement(ps);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static GenericDO createOneResourceObject(GenericDO dbo, Connection conn, String dsName)
/*      */     throws ServiceException
/*      */   {
/*  842 */     if (dbo == null) {
/*  843 */       String msg = "[method]:createOneResourceObject [message]:res is null !";
/*  844 */       LogHome.getLog().error(msg);
/*  845 */       throw new ServiceException(msg);
/*      */     }
/*      */ 
/*  848 */     if (dbo.getClassName() == null) {
/*  849 */       String msg = "[method]:createOneResourceObject [message]:className is null !";
/*  850 */       LogHome.getLog().error(msg);
/*  851 */       throw new ServiceException(msg);
/*      */     }
/*      */ 
/*  856 */     Map attributeMap = dbo.getAllAttr();
/*      */ 
/*  858 */     if (attributeMap == null) {
/*  859 */       attributeMap = new HashMap();
/*      */     }
/*      */ 
/*  862 */     Date CREATE_TIME = dbo.getCreateTime();
/*      */ 
/*  864 */     if (CREATE_TIME == null) {
/*  865 */       CREATE_TIME = new Date();
/*      */     }
/*  867 */     attributeMap.put("CREATE_TIME", CREATE_TIME);
/*      */ 
/*  870 */     Date LAST_MODIFY_TIME = dbo.getLastModifyTime();
/*      */ 
/*  872 */     if (LAST_MODIFY_TIME == null) {
/*  873 */       LAST_MODIFY_TIME = new Date();
/*      */     }
/*  875 */     attributeMap.put("LAST_MODIFY_TIME", LAST_MODIFY_TIME);
/*  876 */     long GT_VERSION = dbo.getAttrLong("GT_VERSION", 0L);
/*  877 */     attributeMap.put("GT_VERSION", Long.valueOf(GT_VERSION));
/*  878 */     long objectID = dbo.getObjectNum();
/*      */ 
/*  881 */     objectID = ClassUtils.getInstance().createObjID(dbo.getClassName(), getDbContext(dsName).getServerId());
/*  882 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/*  883 */       LogHome.getLog().debug("[method]:createOneResourceObject [objectid]: " + objectID);
/*      */     }
/*      */ 
/*  886 */     attributeMap.put("OBJECTID", new Long(objectID));
/*  887 */     dbo.setObjectNum(new Long(objectID).longValue());
/*  888 */     dbo.setObjectKey("" + dbo.getObjectNum());
/*      */ 
/*  892 */     attributeMap.put("ISDELETE", new Boolean(false));
/*      */ 
/*  895 */     Iterator fieldsIte = attributeMap.keySet().iterator();
/*  896 */     String field = null;
/*  897 */     StringBuffer keys = new StringBuffer();
/*  898 */     StringBuffer values = new StringBuffer();
/*  899 */     while (fieldsIte.hasNext()) {
/*  900 */       field = (String)fieldsIte.next();
/*  901 */       Object value = attributeMap.get(field);
/*  902 */       if ((value == null) || (("" + value).equals(""))) {
/*      */         continue;
/*      */       }
/*  905 */       keys.append(field);
/*  906 */       values.append("?");
/*  907 */       if (fieldsIte.hasNext()) {
/*  908 */         keys.append(",");
/*  909 */         values.append(",");
/*      */       }
/*      */     }
/*  912 */     String keysql = keys.toString();
/*  913 */     String valuessql = values.toString();
/*  914 */     if (keysql.endsWith(",")) {
/*  915 */       keysql = keysql.substring(0, keysql.lastIndexOf(","));
/*  916 */       valuessql = valuessql.substring(0, valuessql.lastIndexOf(","));
/*      */     }
/*  918 */     String sql = "INSERT INTO " + dbo.getClassName() + " (" + keysql + ") VALUES (" + valuessql + ")";
/*  919 */     if (LogHome.getLog().isDebugEnabled()) {
/*  920 */       LogHome.getLog().debug("debugsql-sy:" + sql);
/*      */     }
/*  922 */     PreparedStatement ps = null;
/*  923 */     String msg = "";
/*      */     try {
/*  925 */       ps = conn.prepareStatement(sql);
/*  926 */       Iterator itr = attributeMap.keySet().iterator();
/*  927 */       int columnIndex = 1;
/*  928 */       while (itr.hasNext()) {
/*  929 */         field = (String)itr.next();
/*  930 */         msg = "[field]:" + field;
/*  931 */         Object value = attributeMap.get(field);
/*  932 */         if ((value == null) || (("" + value).equals(""))) {
/*      */           continue;
/*      */         }
/*  935 */         if ("OBJECTID".equalsIgnoreCase(field)) {
/*  936 */           ps.setLong(columnIndex, Long.parseLong("" + value));
/*  937 */         } else if ("GT_VERSION".equalsIgnoreCase(field)) {
/*  938 */           ps.setInt(columnIndex, Integer.parseInt("" + value));
/*  939 */         } else if ("CREATE_TIME".equalsIgnoreCase(field)) {
/*  940 */           ps.setTimestamp(columnIndex, getTimestamp(value));
/*  941 */         } else if ("LAST_MODIFY_TIME".equalsIgnoreCase(field)) {
/*  942 */           ps.setTimestamp(columnIndex, getTimestamp(value));
/*  943 */         } else if ("ISDELETE".equalsIgnoreCase(field)) {
/*  944 */           ps.setBoolean(columnIndex, ((Boolean)value).booleanValue());
/*      */         } else {
/*  946 */           Class fieldType = dbo.getAttrType(field);
/*  947 */           if ((fieldType == Boolean.TYPE) || (fieldType == Boolean.class))
/*  948 */             ps.setBoolean(columnIndex, ((Boolean)value).booleanValue());
/*  949 */           else if ((fieldType == Long.TYPE) || (fieldType == Long.class))
/*  950 */             ps.setLong(columnIndex, Long.parseLong("" + value));
/*  951 */           else if ((fieldType == Double.TYPE) || (fieldType == Float.class))
/*  952 */             ps.setDouble(columnIndex, Double.parseDouble("" + value));
/*  953 */           else if (fieldType == String.class)
/*  954 */             ps.setString(columnIndex, SqlHelper.prepareSql(getDbContext(dsName).getDbCharset(), (String)value));
/*  955 */           else if (fieldType == Timestamp.class)
/*  956 */             ps.setTimestamp(columnIndex, (Timestamp)value);
/*      */           else {
/*  958 */             throw new UserException("插入数据库对象不支持的数据类型：" + fieldType.getName());
/*      */           }
/*      */         }
/*  961 */         columnIndex++;
/*      */       }
/*  963 */       ps.executeUpdate();
/*      */     } catch (SQLException se) {
/*  965 */       String msg1 = "[method]:createOneResourceObject [className] : " + dbo.getClassName() + " [objectid]:" + objectID + " [message]:" + se.getMessage() + "[errorCode] : " + se.getErrorCode() + "[getNextException] : " + se.getNextException() + ";";
/*      */ 
/*  968 */       LogHome.getLog().error(msg1);
/*  969 */       LogHome.getLog().error("", se);
/*  970 */       throw new ServiceException(se);
/*      */     } catch (Exception e) {
/*  972 */       throw new ServiceException("[method]:createOneResourceObject " + msg + " [msg]:" + e.getMessage() + "; [sql-sy]:" + sql);
/*      */     }
/*      */     finally {
/*  975 */       if (ps != null) {
/*      */         try {
/*  977 */           ps.close();
/*      */         } catch (SQLException e) {
/*  979 */           LogHome.getLog().error("", e);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  984 */     dbo.setCreateTime(CREATE_TIME);
/*  985 */     dbo.setLastModifyTime(LAST_MODIFY_TIME);
/*  986 */     return dbo;
/*      */   }
/*      */ 
/*      */   private static Timestamp getTimestamp(Object value) {
/*  990 */     if ((value == null) || (("" + value).equals(""))) {
/*  991 */       return null;
/*      */     }
/*  993 */     Date date = (Date)value;
/*  994 */     return new Timestamp(date.getTime());
/*      */   }
/*      */ 
/*      */   private static String getDBDate() {
/*  998 */     String ret = "";
/*      */     try {
/* 1000 */       ret = SqlHelper.getDateNow(DbConnManager.getInstance().getDbType());
/*      */     } catch (Exception e) {
/* 1002 */       LogHome.getLog().error("", e);
/*      */     }
/* 1004 */     return ret;
/*      */   }
/*      */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.OptObjectImpl
 * JD-Core Version:    0.6.0
 */