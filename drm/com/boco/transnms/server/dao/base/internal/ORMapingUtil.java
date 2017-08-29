/*     */ package com.boco.transnms.server.dao.base.internal;
/*     */ 
/*     */ import com.boco.common.util.db.DbConnManager;
/*     */ import com.boco.common.util.db.DbContext;
/*     */ import com.boco.common.util.db.DbHelper;
/*     */ import com.boco.common.util.db.DbType;
/*     */ import com.boco.common.util.db.SqlHelper;
/*     */ import com.boco.common.util.db.SqlParser;
/*     */ import com.boco.common.util.db.SqlQueryCmdHelper;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.cmcc.tm.middleware.exception.ServiceException;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ORMapingUtil
/*     */ {
/*     */   public static int getCount(Connection conn, String sqlStr)
/*     */   {
/*  36 */     int count = -1;
/*  37 */     Statement state = null;
/*  38 */     ResultSet rs = null;
/*     */     try {
/*  40 */       state = conn.createStatement();
/*  41 */       state.setFetchSize(Globals.getFETCHSIZE());
/*     */ 
/*  43 */       if (DbConnManager.getInstance().getDbType() == DbType.DB_TYPE_ORACLE) {
/*  44 */         sqlStr = sqlStr.replaceAll(" as ", " ");
/*     */       }
/*  46 */       long beginTime = System.currentTimeMillis();
/*     */ 
/*  48 */       rs = state.executeQuery(sqlStr);
/*  49 */       if (LogHome.getLog().isDebugEnabled()) {
/*  50 */         LogHome.getLog().debug("[conn]:" + conn + "\r\n[sql]:" + sqlStr + "\r\n[Query Cost]:" + (System.currentTimeMillis() - beginTime));
/*     */       }
/*     */ 
/*  53 */       rs.next();
/*  54 */       count = rs.getInt(1);
/*     */     }
/*     */     catch (Exception e) {
/*  57 */       LogHome.getLog().error("SQLException sql:" + sqlStr);
/*     */     } finally {
/*  59 */       if (rs != null) {
/*     */         try {
/*  61 */           rs.close();
/*     */         } catch (SQLException e) {
/*  63 */           LogHome.getLog().error("", e);
/*     */         }
/*     */       }
/*  66 */       if (state != null) {
/*     */         try {
/*  68 */           state.close();
/*     */         } catch (SQLException e) {
/*  70 */           LogHome.getLog().error("", e);
/*     */         }
/*     */       }
/*     */     }
/*  74 */     return count;
/*     */   }
/*     */ 
/*     */   public static GenericDO getResourceObjectById(long objectId, ResourceObjectType resourceObjectType, Connection con, DbContext dbContext) throws Exception
/*     */   {
/*  79 */     String className = ClassUtils.getInstance().getClassName(objectId);
/*  80 */     if ((className == null) || (className.trim().equals(""))) {
/*  81 */       throw new ServiceException("No such a class name for id:" + objectId);
/*     */     }
/*     */ 
/*  84 */     GenericDO obj = new GenericDO(className);
/*  85 */     GenericDO dboTemplate = obj.createInstanceByClassName();
/*     */ 
/*  87 */     Statement stat = null;
/*  88 */     ResultSet rs = null;
/*  89 */     long beginTime = 0L;
/*  90 */     long sqlTime = 0L;
/*  91 */     long buildTime = 0L;
/*     */     try {
/*  93 */       if (resourceObjectType == null) {
/*  94 */         resourceObjectType = ResourceObjectType.FULL_OMPOBJECT;
/*     */       }
/*  96 */       Set attrSet = ClassUtils.getInstance().getAttrByClassName(className, resourceObjectType);
/*     */ 
/*  99 */       String sql = getSelectSql(attrSet) + " FROM " + className + " WHERE OBJECTID=" + objectId + "";
/*     */ 
/* 101 */       stat = con.createStatement();
/* 102 */       stat.setFetchSize(Globals.getFETCHSIZE());
/*     */ 
/* 104 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/* 105 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 106 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, new GenericDO[] { dboTemplate });
/* 107 */       DboCollection rows = new DboCollection(dbContext.getDsName(), 0, Globals.getFETCHSIZE());
/* 108 */       beginTime = System.currentTimeMillis();
/* 109 */       rs = stat.executeQuery(sql);
/* 110 */       sqlTime = System.currentTimeMillis() - beginTime;
/* 111 */       SqlHelper.fillDataObjectMap(rs, dbContext, queryFieldList, dboMap, rows, sql, Globals.getFETCHSIZE());
/* 112 */       obj = (GenericDO)rows.getAttrField(dboTemplate.getClassName(), 0);
/* 113 */       if (LogHome.getLog().isDebugEnabled()) {
/* 114 */         buildTime = System.currentTimeMillis() - beginTime;
/* 115 */         LogHome.getLog().debug("[conn]:" + con + "\r\n[sql]:" + sql + "\r\n[sqlTime]:" + sqlTime + " [buildTime]:" + buildTime);
/*     */       }
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 120 */       System.out.println("SQLException objectid:" + objectId);
/* 121 */       throw new ServiceException(e);
/*     */     } finally {
/* 123 */       DbHelper.closeResultSet(rs);
/* 124 */       DbHelper.closeStatement(stat);
/*     */     }
/* 126 */     return obj;
/*     */   }
/*     */ 
/*     */   public static List getObjectIdBySQL(Connection conn, String sqlStr, int number)
/*     */   {
/* 139 */     List objectIdList = new ArrayList();
/* 140 */     Statement state = null;
/* 141 */     ResultSet rs = null;
/*     */     try {
/* 143 */       state = conn.createStatement();
/* 144 */       state.setFetchSize(Globals.getFETCHSIZE());
/*     */ 
/* 146 */       if (DbConnManager.getInstance().getDbType() == DbType.DB_TYPE_ORACLE) {
/* 147 */         sqlStr = sqlStr.replaceAll(" as ", " ");
/*     */       }
/* 149 */       long beginTime = System.currentTimeMillis();
/*     */ 
/* 151 */       rs = state.executeQuery(sqlStr);
/*     */ 
/* 153 */       long middleTime = System.currentTimeMillis() - beginTime;
/*     */ 
/* 155 */       while (rs.next()) {
/* 156 */         if (number == 1) {
/* 157 */           objectIdList.add(new Long(rs.getLong(1))); continue;
/*     */         }
/* 159 */         Long[] objectId = new Long[number];
/* 160 */         for (int i = 0; i < number; ) { objectId[i] = new Long(rs.getLong(i + 1));
/* 161 */           i++;
/*     */         }
/*     */ 
/* 164 */         objectIdList.add(objectId);
/*     */       }
/*     */ 
/* 168 */       if (LogHome.getLog().isDebugEnabled()) {
/* 169 */         LogHome.getLog().debug("[conn]:" + conn + "\r\n[sql]:" + sqlStr + "\r\n[Query Cost]:" + middleTime + "[Pick Cost]:" + (System.currentTimeMillis() - beginTime - middleTime));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 176 */       LogHome.getLog().error("", e);
/*     */     } finally {
/* 178 */       if (rs != null) {
/*     */         try {
/* 180 */           rs.close();
/*     */         } catch (SQLException e) {
/* 182 */           LogHome.getLog().error("", e);
/*     */         }
/*     */       }
/* 185 */       if (state != null) {
/*     */         try {
/* 187 */           state.close();
/*     */         } catch (SQLException e) {
/* 189 */           LogHome.getLog().error("", e);
/*     */         }
/*     */       }
/*     */     }
/* 193 */     return objectIdList;
/*     */   }
/*     */ 
/*     */   public static List<GenericDO> getResourceObjectsBySql(String fromSql, String className, ResourceObjectType resourceObjectType, Connection con, DbContext dbContext)
/*     */     throws Exception
/*     */   {
/* 200 */     List resultList = new ArrayList();
/* 201 */     Statement stat = null;
/* 202 */     ResultSet rs = null;
/* 203 */     if (resourceObjectType == null) {
/* 204 */       resourceObjectType = ResourceObjectType.FULL_OMPOBJECT;
/*     */     }
/* 206 */     GenericDO obj = new GenericDO(className);
/* 207 */     GenericDO dboTemplate = obj.createInstanceByClassName();
/* 208 */     String sql = "";
/* 209 */     long beginTime = 0L;
/* 210 */     long sqlTime = 0L;
/* 211 */     long buildTime = 0L;
/*     */     try {
/* 213 */       Set attrSet = ClassUtils.getInstance().getAttrByClassName(className, resourceObjectType);
/*     */ 
/* 215 */       sql = getSelectSql(attrSet) + fromSql;
/* 216 */       stat = con.createStatement();
/*     */ 
/* 220 */       String sqlQuery = SqlParser.parseSqlQuery(sql);
/* 221 */       String[] queryFieldList = SqlParser.parseQueryFields(sqlQuery);
/* 222 */       Map dboMap = SqlQueryCmdHelper.getDBOMap(sqlQuery, new GenericDO[] { dboTemplate });
/*     */ 
/* 224 */       DboCollection rows = new DboCollection(dbContext.getDsName(), 0, 0);
/*     */ 
/* 226 */       beginTime = System.currentTimeMillis();
/* 227 */       rs = stat.executeQuery(sql);
/* 228 */       sqlTime = System.currentTimeMillis() - beginTime;
/*     */ 
/* 232 */       SqlHelper.fillDataObjectMap(rs, dbContext, queryFieldList, dboMap, rows, sql);
/*     */ 
/* 235 */       for (int i = 0; (rows != null) && (i < rows.size()); i++) {
/* 236 */         GenericDO dto = (GenericDO)rows.getAttrField(dboTemplate.getClassName(), i);
/* 237 */         resultList.add(dto);
/*     */       }
/*     */ 
/* 240 */       if (LogHome.getLog().isDebugEnabled()) {
/* 241 */         int resultCount = rows.size();
/* 242 */         LogHome.getLog().debug("sql:" + sql);
/* 243 */         buildTime = System.currentTimeMillis() - beginTime;
/* 244 */         LogHome.getLog().debug("[conn]:" + con + "\r\n[sql]:" + sql + "\r\n[sqlTime]:" + sqlTime + " [buildTime]:" + buildTime + "\r\n[resultCount]:" + resultCount);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 250 */       LogHome.getLog().error("SQLException sql:" + sql);
/* 251 */       throw new ServiceException(e);
/*     */     }
/*     */     finally {
/* 254 */       if (rs != null)
/*     */         try {
/* 256 */           rs.close();
/*     */         }
/*     */         catch (Exception ex) {
/*     */         }
/* 260 */       if (stat != null)
/*     */         try {
/* 262 */           stat.close();
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/*     */         }
/*     */     }
/* 268 */     return resultList;
/*     */   }
/*     */ 
/*     */   public static int getCountOfClassBySql(String className, String wheresql, Connection conn)
/*     */     throws ServiceException
/*     */   {
/* 274 */     int count = 0;
/* 275 */     Statement st = null;
/* 276 */     ResultSet rs = null;
/* 277 */     String sql = "SELECT COUNT(OBJECTID) FROM " + className + " WHERE " + wheresql;
/*     */     try
/*     */     {
/* 280 */       if (LogHome.getLog().isDebugEnabled()) {
/* 281 */         String msg = "[conn]:" + conn + "\r\n[method]:getCountOfClassBySql [SQL]: " + sql;
/*     */ 
/* 283 */         LogHome.getLog().debug(msg);
/*     */       }
/* 285 */       st = conn.createStatement();
/* 286 */       st.setFetchSize(Globals.getFETCHSIZE());
/*     */ 
/* 288 */       rs = st.executeQuery(sql);
/* 289 */       if (rs.next())
/* 290 */         count = rs.getInt(1);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 294 */       LogHome.getLog().error("SQLException sql:" + sql);
/* 295 */       throw new ServiceException(e);
/*     */     } finally {
/* 297 */       if (rs != null)
/*     */         try {
/* 299 */           rs.close();
/*     */         }
/*     */         catch (Exception ex) {
/*     */         }
/* 303 */       if (st != null)
/*     */         try {
/* 305 */           st.close();
/*     */         }
/*     */         catch (Exception ex) {
/*     */         }
/*     */     }
/* 310 */     return count;
/*     */   }
/*     */ 
/*     */   private static String getSelectSql(Set attrSet) {
/* 314 */     StringBuffer sb = new StringBuffer();
/* 315 */     sb.append("SELECT ");
/* 316 */     for (Iterator it = attrSet.iterator(); it.hasNext(); ) {
/* 317 */       String name = (String)it.next();
/* 318 */       sb.append(name);
/* 319 */       if (it.hasNext()) {
/* 320 */         sb.append(",");
/*     */       }
/*     */     }
/* 323 */     sb.append(" ");
/* 324 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.ORMapingUtil
 * JD-Core Version:    0.6.0
 */