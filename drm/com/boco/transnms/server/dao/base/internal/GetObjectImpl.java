/*     */ package com.boco.transnms.server.dao.base.internal;
/*     */ 
/*     */ import com.boco.common.util.db.DbContext;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.cmcc.tm.middleware.exception.ServiceException;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GetObjectImpl
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4741259400759584286L;
/*  22 */   private DbContext dbContext = null;
/*     */ 
/*  24 */   public GetObjectImpl(DbContext dbContext) { this.dbContext = dbContext;
/*     */   }
/*     */ 
/*     */   public Object[] getAttributeValues(long[] objList, String attributeName, Connection conn)
/*     */     throws Exception
/*     */   {
/*  42 */     if (objList == null) {
/*  43 */       LogHome.getLog().error("[method]:getAttributeValues [message]:objList is null !");
/*  44 */       return null;
/*     */     }
/*  46 */     if (attributeName == null) {
/*  47 */       LogHome.getLog().error("[method]:getAttributeValues [message]:attributeName is null !");
/*  48 */       return null;
/*     */     }
/*  50 */     if (objList.length == 0) {
/*  51 */       LogHome.getLog().error("[method]:getAttributeValues [message]:objList length is 0!");
/*  52 */       return null;
/*     */     }
/*  54 */     if ("".equalsIgnoreCase(attributeName.trim())) {
/*  55 */       LogHome.getLog().error("[method]:getAttributeValues [message]:attributeName is ''!");
/*  56 */       return null;
/*     */     }
/*     */ 
/*  59 */     Object[] objAttriArray = null;
/*  60 */     String className = null;
/*     */ 
/*  62 */     objAttriArray = new Object[objList.length];
/*     */ 
/*  64 */     for (int i = 0; i < objList.length; i++)
/*     */     {
/*  66 */       className = ClassUtils.getInstance().getClassName(objList[i]);
/*     */ 
/*  68 */       if ((className == null) || (className.trim().equals(""))) {
/*  69 */         throw new ServiceException();
/*     */       }
/*     */ 
/*  72 */       GenericDO res = ORMapingUtil.getResourceObjectById(objList[i], null, conn, this.dbContext);
/*     */ 
/*  74 */       if (LogHome.getLog().isDebugEnabled()) {
/*  75 */         LogHome.getLog().debug("(" + i + "/" + objList.length + ") is DONE!");
/*     */       }
/*  77 */       if (res == null) {
/*  78 */         throw new ServiceException();
/*     */       }
/*  80 */       objAttriArray[i] = res.getAttrValue(attributeName);
/*     */     }
/*     */ 
/*  83 */     return objAttriArray;
/*     */   }
/*     */ 
/*     */   public int getCountOfResClass(String className, Connection conn)
/*     */     throws ServiceException
/*     */   {
/*  98 */     if (className == null) {
/*  99 */       LogHome.getLog().error("[method]:getCountOfResClass [message]:className is null !");
/* 100 */       return 0;
/*     */     }
/* 102 */     if ("".equalsIgnoreCase(className.trim())) {
/* 103 */       LogHome.getLog().error("[method]:getCountOfResClass [message]:className is '' !");
/* 104 */       return 0;
/*     */     }
/*     */ 
/* 107 */     int count = 0;
/*     */ 
/* 109 */     count = ORMapingUtil.getCountOfClassBySql(className, "1=1", conn);
/*     */ 
/* 112 */     return count;
/*     */   }
/*     */ 
/*     */   public GenericDO getResourceObject(long objID, ResourceObjectType resType, String schemaName, Connection conn)
/*     */     throws Exception
/*     */   {
/* 130 */     if (resType == null) {
/* 131 */       LogHome.getLog().error("[method]:getResourceObject [message]:resType is null !");
/* 132 */       return null;
/*     */     }
/* 134 */     GenericDO rtnResObj = null;
/*     */ 
/* 136 */     rtnResObj = ORMapingUtil.getResourceObjectById(objID, resType, conn, this.dbContext);
/*     */ 
/* 138 */     return rtnResObj;
/*     */   }
/*     */ 
/*     */   public List getResourceObjects(long[] objID, ResourceObjectType resType, Connection conn) throws Exception
/*     */   {
/* 143 */     if (objID == null) {
/* 144 */       LogHome.getLog().error("[method]:getResourceObjects [message]:objIDs is null !");
/* 145 */       return null;
/*     */     }
/* 147 */     if (objID.length == 0) {
/* 148 */       LogHome.getLog().error("[method]:getResourceObjects [message]:objIDs length is 0 !");
/* 149 */       return null;
/*     */     }
/* 151 */     if (resType == null) {
/* 152 */       LogHome.getLog().error("[method]:getResourceObjects [message]:resType is null !");
/* 153 */       return null;
/*     */     }
/*     */ 
/* 156 */     List retList = new ArrayList();
/*     */ 
/* 158 */     Map sqlMap = new HashMap();
/* 159 */     for (int i = 0; i < objID.length; i++)
/*     */     {
/* 161 */       String className = ClassUtils.getInstance().getClassName(objID[i]);
/*     */ 
/* 163 */       if ((className == null) || ("".equalsIgnoreCase(className))) {
/* 164 */         throw new ServiceException();
/*     */       }
/*     */ 
/* 167 */       if (sqlMap.containsKey(className))
/*     */       {
/* 169 */         List sqlList = (List)sqlMap.get(className);
/*     */ 
/* 171 */         StringBuffer clzSql = (StringBuffer)sqlList.get(sqlList.size() - 1);
/*     */ 
/* 174 */         if (clzSql.length() >= 13000) {
/* 175 */           StringBuffer newclzSql = new StringBuffer("FROM " + className + " WHERE OBJECTID IN (" + objID[i]);
/*     */ 
/* 177 */           sqlList.add(newclzSql);
/*     */         } else {
/* 179 */           clzSql.append("," + objID[i]);
/*     */         }
/* 181 */         sqlMap.put(className, sqlList);
/*     */       } else {
/* 183 */         StringBuffer clzSql = new StringBuffer("FROM " + className + " WHERE OBJECTID IN (" + objID[i]);
/*     */ 
/* 185 */         List sqlList = new ArrayList();
/* 186 */         sqlList.add(clzSql);
/* 187 */         sqlMap.put(className, sqlList);
/*     */       }
/*     */     }
/*     */ 
/* 191 */     Iterator resIter = sqlMap.keySet().iterator();
/*     */ 
/* 193 */     while (resIter.hasNext()) {
/* 194 */       String className = (String)resIter.next();
/* 195 */       List sqlList = (List)sqlMap.get(className);
/* 196 */       for (int j = 0; j < sqlList.size(); j++) {
/* 197 */         StringBuffer clzSql = (StringBuffer)sqlList.get(j);
/* 198 */         clzSql.append(" )");
/* 199 */         List qList = ORMapingUtil.getResourceObjectsBySql(clzSql.toString(), className, resType, conn, this.dbContext);
/*     */ 
/* 201 */         retList.addAll(qList);
/*     */       }
/*     */     }
/* 204 */     if (objID.length != retList.size()) {
/* 205 */       throw new ServiceException("object is not exist!");
/*     */     }
/*     */ 
/* 208 */     return retList;
/*     */   }
/*     */ 
/*     */   public List getResObjectsByClass(String className, boolean getChildAllObj, ResourceObjectType resType, Connection conn)
/*     */     throws Exception
/*     */   {
/* 227 */     if (className == null) {
/* 228 */       LogHome.getLog().error("[method]:getResObjectsByClass [message]:className is null !");
/* 229 */       return null;
/*     */     }
/* 231 */     if (resType == null) {
/* 232 */       LogHome.getLog().error("[method]:getResObjectsByClass [message]:resType is null !");
/* 233 */       return null;
/*     */     }
/* 235 */     if ("".equalsIgnoreCase(className.trim())) {
/* 236 */       LogHome.getLog().error("[method]:getResObjectsByClass [message]:className is '' !");
/* 237 */       return null;
/*     */     }
/*     */ 
/* 240 */     List retList = new ArrayList();
/*     */ 
/* 242 */     String sql = " FROM " + className + "";
/*     */ 
/* 244 */     List faList = ORMapingUtil.getResourceObjectsBySql(sql, className, resType, conn, this.dbContext);
/*     */ 
/* 246 */     if (faList != null) {
/* 247 */       retList.addAll(faList);
/*     */     }
/* 249 */     return retList;
/*     */   }
/*     */ 
/*     */   public List<GenericDO> getResObjectsBySql(String className, String whereSql, ResourceObjectType resType, Connection conn) throws Exception
/*     */   {
/* 254 */     if (Globals.isIFDEBUGAPIINVOKE()) {
/* 255 */       LogHome.getLog().debug("[conn]:" + conn + "\r\n[method]:getResObjectsBySql");
/* 256 */       LogHome.getLog().debug("[params]:");
/* 257 */       LogHome.getLog().debug("className=" + className);
/* 258 */       LogHome.getLog().debug("resType=" + resType);
/* 259 */       LogHome.getLog().debug("sql={" + whereSql + "}");
/*     */     }
/*     */ 
/* 262 */     if (resType == null) {
/* 263 */       LogHome.getLog().error("[conn]:" + conn + "\r\n[method]:getResObjectsBySql [message]:resType is null !");
/* 264 */       return null;
/*     */     }
/*     */ 
/* 267 */     List rtnResObjList = null;
/*     */ 
/* 269 */     if ((className == null) || (whereSql == null)) {
/* 270 */       return null;
/*     */     }
/* 272 */     if (className.trim().equals("")) {
/* 273 */       return null;
/*     */     }
/*     */ 
/* 276 */     StringBuffer sqlsb = new StringBuffer();
/*     */ 
/* 278 */     if (whereSql.trim().equals(""))
/* 279 */       sqlsb.append(" FROM " + className);
/*     */     else {
/* 281 */       sqlsb.append(" FROM " + className + " WHERE ").append(whereSql);
/*     */     }
/*     */ 
/* 284 */     rtnResObjList = ORMapingUtil.getResourceObjectsBySql(sqlsb.toString(), className, resType, conn, this.dbContext);
/*     */ 
/* 287 */     return rtnResObjList;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.GetObjectImpl
 * JD-Core Version:    0.6.0
 */