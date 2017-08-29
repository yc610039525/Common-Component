/*     */ package com.boco.transnms.server.dao.base.internal;
/*     */ 
/*     */ import com.boco.common.util.db.DbConnManager;
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.ResourceLocater;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.cmcc.tm.middleware.exception.ServiceException;
/*     */ import java.net.URL;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ClassUtils
/*     */ {
/*  33 */   private static HashMap dtoNames = new HashMap();
/*  34 */   private static HashMap dtoId2Names = new HashMap();
/*  35 */   private static HashMap dtoName2Ids = new HashMap();
/*  36 */   private static HashMap dtoName2Class = new HashMap();
/*     */   private static ClassUtils instance;
/*  40 */   private static UUIDHexGenerator uuidHexGenerator = new UUIDHexGenerator();
/*     */ 
/*     */   private ClassUtils() {
/*  43 */     URL[] dtoNameFiles = ResourceLocater.getResourceURL("com/boco/transnms/common/dto/base/dtoNames.properties");
/*  44 */     URL[] dtoName2IdFiles = ResourceLocater.getResourceURL("com/boco/transnms/common/dto/base/dtoName2Id.properties");
/*  45 */     URL[] dtoId2NameFiles = ResourceLocater.getResourceURL("com/boco/transnms/common/dto/base/dtoId2Name.properties");
/*  46 */     Properties properties = new Properties();
/*  47 */     for (int i = 0; i < dtoNameFiles.length; i++) {
/*  48 */       URL dtoNameFile = dtoNameFiles[i];
/*     */       try {
/*  50 */         properties.load(dtoNameFile.openStream());
/*     */       } catch (Exception e) {
/*  52 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  55 */     dtoNames.putAll(properties);
/*  56 */     properties = new Properties();
/*  57 */     for (int i = 0; i < dtoId2NameFiles.length; i++) {
/*  58 */       URL dtoId2NameFile = dtoId2NameFiles[i];
/*     */       try {
/*  60 */         properties.load(dtoId2NameFile.openStream());
/*     */       } catch (Exception e) {
/*  62 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  65 */     dtoId2Names.putAll(properties);
/*  66 */     properties = new Properties();
/*  67 */     for (int i = 0; i < dtoName2IdFiles.length; i++) {
/*  68 */       URL dtoName2IdFile = dtoName2IdFiles[i];
/*     */       try {
/*  70 */         properties.load(dtoName2IdFile.openStream());
/*     */       } catch (Exception e) {
/*  72 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  75 */     dtoName2Ids.putAll(properties);
/*  76 */     Iterator iterator = dtoNames.keySet().iterator();
/*  77 */     while (iterator.hasNext()) {
/*  78 */       String className = (String)iterator.next();
/*  79 */       GenericDO dbo = new GenericDO();
/*  80 */       dbo.setClassName(className);
/*  81 */       Class clazz = dbo.createInstanceByClassName().getClass();
/*  82 */       dtoName2Class.put(className, clazz);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ClassUtils getInstance()
/*     */   {
/*  88 */     if (instance == null) {
/*  89 */       instance = new ClassUtils();
/*     */     }
/*  91 */     return instance;
/*     */   }
/*     */ 
/*     */   public int getClassIdByObjID(long objID)
/*     */   {
/* 102 */     return GoatIdUtil.getClassIdByOid(objID);
/*     */   }
/*     */ 
/*     */   public String getClassName(long objID)
/*     */   {
/* 112 */     int classID = getClassIdByObjID(objID);
/* 113 */     return getClassName(classID);
/*     */   }
/*     */ 
/*     */   public String getTableLabelCn(String dbClassName) {
/* 117 */     return dtoNames.get(dbClassName).toString();
/*     */   }
/*     */ 
/*     */   public String getClassName(int classID)
/*     */   {
/* 127 */     return (String)dtoId2Names.get(classID + "");
/*     */   }
/*     */ 
/*     */   public boolean isClassNameExist(String className)
/*     */   {
/* 137 */     return dtoName2Ids.containsKey(className);
/*     */   }
/*     */ 
/*     */   public long createObjID(String className, int serverId)
/*     */     throws ServiceException
/*     */   {
/* 148 */     if (!isClassNameExist(className)) {
/* 149 */       throw new ServiceException("class not found!");
/*     */     }
/* 151 */     int sId = serverId > 0 ? serverId : Globals.getSERVERID();
/*     */ 
/* 155 */     if (sId > 0) {
/* 156 */       return GoatOidGenerator.getNewId(sId, getClassID(className));
/*     */     }
/* 158 */     return GoatOidGenerator.getNewId(getClassID(className));
/*     */   }
/*     */ 
/*     */   public int getClassID(String className)
/*     */   {
/* 170 */     String ids = (String)dtoName2Ids.get(className);
/* 171 */     Long id = Long.valueOf(Long.parseLong(ids));
/* 172 */     return id.intValue();
/*     */   }
/*     */ 
/*     */   public long getObjIDByObjKey(String className, String objKeyVals)
/*     */     throws Exception
/*     */   {
/* 183 */     throw new Exception("not support");
/*     */   }
/*     */ 
/*     */   public Object getObjByObjKey(String className, String objKeyVals)
/*     */     throws Exception
/*     */   {
/* 195 */     throw new Exception("not support");
/*     */   }
/*     */ 
/*     */   public long[] getObjByObjKey(String className, String[] objKeyVals)
/*     */     throws Exception
/*     */   {
/* 208 */     long[] rtn = new long[objKeyVals.length];
/* 209 */     if (!isClassNameExist(className)) {
/* 210 */       return rtn;
/*     */     }
/* 212 */     for (int i = 0; i < objKeyVals.length; i++) {
/*     */       try {
/* 214 */         rtn[i] = getObjIDByObjKey(className, objKeyVals[i]);
/*     */       } catch (Exception e) {
/* 216 */         e.printStackTrace();
/* 217 */         throw new Exception("getObjIDByObjKey");
/*     */       }
/*     */     }
/* 220 */     return rtn;
/*     */   }
/*     */ 
/*     */   public String[] getAllClassNames()
/*     */   {
/* 229 */     String[] cns = null;
/* 230 */     return cns;
/*     */   }
/*     */ 
/*     */   public String[] getAllEntityClassNames()
/*     */   {
/* 238 */     List classNames = new ArrayList();
/* 239 */     String[] cns = null;
/* 240 */     Iterator iterator = dtoName2Ids.keySet().iterator();
/* 241 */     while (iterator.hasNext()) {
/* 242 */       String className = (String)iterator.next();
/* 243 */       classNames.add(className);
/*     */     }
/* 245 */     cns = new String[classNames.size()];
/* 246 */     classNames.toArray(cns);
/* 247 */     return cns;
/*     */   }
/*     */ 
/*     */   public String[] getClassNameByGrpName(String groupName)
/*     */   {
/* 257 */     String[] cns = null;
/* 258 */     return cns;
/*     */   }
/*     */ 
/*     */   public String[] getEntityClassNameByGrpName(String groupName)
/*     */   {
/* 268 */     String[] cns = null;
/* 269 */     return cns;
/*     */   }
/*     */ 
/*     */   public String[] getAllGroups()
/*     */   {
/* 278 */     String[] gns = null;
/* 279 */     return gns;
/*     */   }
/*     */ 
/*     */   public int[] getClassIDByGrpName(String groupName)
/*     */   {
/* 289 */     int[] cis = null;
/* 290 */     return cis;
/*     */   }
/*     */ 
/*     */   public Class getValueType(int valueType) {
/* 294 */     switch (valueType) {
/*     */     case 1:
/* 296 */       return String.class;
/*     */     case 2:
/* 298 */       return Double.class;
/*     */     case 4:
/* 300 */       return Integer.class;
/*     */     case 20:
/* 302 */       return Boolean.class;
/*     */     }
/* 304 */     return String.class;
/*     */   }
/*     */ 
/*     */   public Set getAttrByClassName(String className, ResourceObjectType resourceObjectType)
/*     */   {
/* 310 */     GenericDO dbo = new GenericDO(className);
/* 311 */     dbo = dbo.createInstanceByClassName();
/* 312 */     Set tmpSet = new LinkedHashSet();
/* 313 */     String[] attrNames = dbo.getAllAttrNames();
/* 314 */     for (int i = 0; i < attrNames.length; i++) {
/* 315 */       tmpSet.add(attrNames[i]);
/*     */     }
/* 317 */     tmpSet.add("OBJECTID");
/*     */ 
/* 320 */     tmpSet.add("CREATE_TIME");
/* 321 */     tmpSet.add("LAST_MODIFY_TIME");
/* 322 */     return tmpSet;
/*     */   }
/*     */ 
/*     */   public Set getAttrByClassName(GenericDO dbo) {
/* 326 */     String[] attrs = dbo.getAllAttrNames();
/* 327 */     Set tmpSet = new LinkedHashSet();
/* 328 */     for (int i = 0; i < attrs.length; i++) {
/* 329 */       tmpSet.add(attrs[i]);
/*     */     }
/* 331 */     return tmpSet;
/*     */   }
/*     */ 
/*     */   public Set getAttrByClassName(String className)
/*     */   {
/* 336 */     getAttrByClassName(className, null);
/* 337 */     return null;
/*     */   }
/*     */ 
/*     */   public void initOid() {
/* 341 */     initOid(null, null);
/*     */   }
/*     */ 
/*     */   public void initOid(String dsName) {
/* 345 */     initOid(dsName, null);
/*     */   }
/*     */ 
/*     */   public void initOid(Map tables) {
/* 349 */     initOid(null, tables);
/*     */   }
/*     */ 
/*     */   public void initOid(String dsName, Map tables) {
/* 353 */     if (dsName != null)
/* 354 */       LogHome.getLog().info("begin to init objectid... at " + dsName);
/*     */     else {
/* 356 */       LogHome.getLog().info("begin to init objectid... at Default DB");
/*     */     }
/* 358 */     long start = System.currentTimeMillis();
/* 359 */     String[] classNames = getAllEntityClassNames();
/* 360 */     Connection conn = null;
/* 361 */     Statement st = null;
/* 362 */     ResultSet rs = null;
/*     */     try {
/* 364 */       conn = DbConnManager.getInstance().fetchDbConn(dsName);
/* 365 */       st = conn.createStatement();
/* 366 */       for (String className : classNames) {
/* 367 */         if ((tables != null) && (!tables.containsKey(className)))
/*     */           continue;
/*     */         try {
/* 370 */           long oid = 0L;
/* 371 */           oid = getMaxObjectIdFromDB(conn, st, rs, className);
/* 372 */           if (GoatOidAccount.getGoatOidAccount().isInitComplate(getClassID(className))) {
/* 373 */             long objectId = GoatOidAccount.getGoatOidAccount().getNextId(getClassID(className));
/* 374 */             oid = Math.max(oid, objectId);
/*     */           }
/* 376 */           GoatOidAccount.getGoatOidAccount().setInit(getClassID(className), oid);
/*     */         } catch (Exception ex) {
/* 378 */           ex.printStackTrace();
/*     */         }
/*     */       }
/* 381 */       LogHome.getLog().info("init objectid success,cost: " + (System.currentTimeMillis() - start) + " ms");
/*     */     } catch (Exception e) {
/* 383 */       LogHome.getLog().error("init objectid fail...");
/* 384 */       e.printStackTrace();
/*     */     } finally {
/* 386 */       if (rs != null) {
/*     */         try {
/* 388 */           rs.close();
/*     */         } catch (SQLException e) {
/* 390 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 393 */       if (st != null) {
/*     */         try {
/* 395 */           st.close();
/*     */         } catch (SQLException e) {
/* 397 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 400 */       if (conn != null)
/*     */         try {
/* 402 */           conn.close();
/*     */         } catch (SQLException e) {
/* 404 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private long getMaxObjectIdFromDB(Connection conn, Statement st, ResultSet rs, String className)
/*     */   {
/* 411 */     long startTime = System.currentTimeMillis();
/* 412 */     if (LogHome.getLog().isDebugEnabled()) {
/* 413 */       LogHome.getLog().debug("query classname:" + className + " start");
/*     */     }
/* 415 */     long oid = 0L;
/*     */     try {
/* 417 */       rs = st.executeQuery("select max(OBJECTID) from " + className.toUpperCase());
/* 418 */       if (rs.next())
/* 419 */         oid = rs.getLong(1);
/*     */     }
/*     */     catch (Exception ex) {
/* 422 */       LogHome.getLog().debug("获取类初始ID失败：className=" + className.toUpperCase());
/*     */ 
/* 424 */       oid = -1L;
/*     */     }
/* 426 */     String status = " is max";
/* 427 */     if (oid == 0L) {
/* 428 */       oid = GoatIdUtil.getNewId(getClassID(className), 0L);
/* 429 */       status = " is new";
/* 430 */     } else if (getClassID(className) != getClassIdByObjID(oid)) {
/* 431 */       oid = GoatIdUtil.getNewId(getClassID(className), uuidHexGenerator.generateLong());
/* 432 */       status = " max is wrong,init by uuid";
/*     */     }
/* 434 */     long t = System.currentTimeMillis() - startTime;
/* 435 */     if (t > 1000L) {
/* 436 */       LogHome.getLog().warn("数据表 [" + className + "]可能丢失ObjectId主键, 获取最大ObjectId耗时" + t + "ms");
/*     */     }
/* 438 */     if (LogHome.getLog().isDebugEnabled()) {
/* 439 */       LogHome.getLog().debug("classname:" + className + " objectid:" + oid + " " + status + ", time=" + t);
/*     */     }
/* 441 */     return oid;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 465 */     new ClassUtils();
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.ClassUtils
 * JD-Core Version:    0.6.0
 */