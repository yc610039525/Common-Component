/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.CharsetHelper;
/*     */ import com.boco.common.util.lang.ThreadHelper;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.transnms.common.dto.base.DataObjectMap;
/*     */ import com.boco.transnms.common.dto.base.DboBlob;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SqlHelper
/*     */ {
/*     */   private static final String SO_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*     */   private static final String SO_DATE_FORMAT = "yyyy-MM-dd";
/*     */   private static final String ORACLEDATE_PATTERN = "yyyy-MM-dd HH24:mi:ss";
/*     */ 
/*     */   public static String prepareSql(String dbCharset, String sql)
/*     */   {
/*  55 */     String pareparedSql = CharsetHelper.encode(dbCharset, sql);
/*  56 */     return pareparedSql;
/*     */   }
/*     */ 
/*     */   public static String getOptimizeSql(DbType dbType, String sql, int offset, int fetchSize) {
/*  60 */     String psql = sql;
/*     */     try {
/*  62 */       if (dbType == DbType.DB_TYPE_ORACLE) {
/*  63 */         String tsql = sql.toUpperCase();
/*  64 */         int indexOf = tsql.indexOf(" FROM ");
/*  65 */         int lastIndexOf = tsql.lastIndexOf(" FROM ");
/*  66 */         int indexOfWhere = tsql.indexOf("WHERE");
/*  67 */         boolean mutiTable = true;
/*  68 */         if (indexOfWhere >= 0) {
/*  69 */           String spsql = tsql.substring(indexOf + 6, indexOfWhere);
/*  70 */           mutiTable = (spsql.contains(",")) || (spsql.contains(" JOIN "));
/*     */         }
/*     */ 
/*  73 */         if ((indexOf == lastIndexOf) && (!mutiTable)) {
/*  74 */           String preSql = sql.substring(0, indexOf);
/*  75 */           String lastSql = sql.substring(indexOf + 6);
/*  76 */           psql = preSql + " ,ROWNUM RN FROM " + lastSql;
/*  77 */           psql = "SELECT /*+ FIRST_ROWS */ * FROM ( SELECT a.*, ROWNUM ROWNO  FROM (" + psql + ") A WHERE ROWNUM <=" + (offset + fetchSize) + " ) WHERE ROWNO > " + offset;
/*     */         }
/*     */ 
/*     */       }
/*  83 */       else if (dbType == DbType.DB_TYPE_INFORMIX) {
/*  84 */         String tsql = sql.toUpperCase();
/*  85 */         String[] ss = tsql.split("SELECT");
/*  86 */         int pos = tsql.indexOf("SELECT");
/*     */ 
/*  88 */         if (ss.length == 2) {
/*  89 */           psql = sql.substring(0, pos) + "SELECT SKIP " + offset + " FIRST " + (offset + fetchSize) + " " + sql.substring(pos + 6, sql.length());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*  95 */       ex.printStackTrace();
/*  96 */       psql = sql;
/*     */     }
/*  98 */     return psql;
/*     */   }
/*     */ 
/*     */   public static String getDate(DbType dbType, java.sql.Date date) throws Exception {
/* 102 */     String sqlDate = "";
/*     */ 
/* 104 */     if (dbType == DbType.DB_TYPE_ORACLE) {
/* 105 */       String dateStr = TimeFormatHelper.getFormatDate(date, "yyyy-MM-dd");
/* 106 */       sqlDate = "to_date('" + dateStr + "','YYYY-MM-DD')";
/* 107 */     } else if (dbType == DbType.DB_TYPE_INFORMIX)
/*     */     {
/* 109 */       String dateStr = TimeFormatHelper.getFormatDate(date, "yyyy-MM-dd");
/* 110 */       sqlDate = "to_date('" + dateStr + "','%Y-%m-%d')";
/*     */     } else {
/* 112 */       sqlDate = "'" + TimeFormatHelper.getFormatDate(date, "yyyy-MM-dd") + "'";
/*     */     }
/*     */ 
/* 115 */     return sqlDate;
/*     */   }
/*     */ 
/*     */   public static String getDateNow(DbType dbType) throws Exception {
/* 119 */     java.util.Date date = new java.util.Date();
/* 120 */     return getDate(dbType, new java.sql.Date(date.getTime()));
/*     */   }
/*     */ 
/*     */   public static String getTimestamp(DbType dbType, String dateStr) throws Exception {
/* 124 */     Timestamp timestamp = TimeFormatHelper.getFormatTimestamp(dateStr);
/* 125 */     return getTimestamp(dbType, timestamp);
/*     */   }
/*     */ 
/*     */   private static Timestamp getTimestamp(Object value) {
/* 129 */     if ((value == null) || (("" + value).equals(""))) {
/* 130 */       return null;
/*     */     }
/* 132 */     java.util.Date date = (java.util.Date)value;
/* 133 */     return new Timestamp(date.getTime());
/*     */   }
/*     */ 
/*     */   public static String getTimestamp(DbType dbType, Timestamp timestamp) throws Exception {
/* 137 */     String sqlTimestamp = "";
/*     */ 
/* 139 */     if (dbType == DbType.DB_TYPE_ORACLE) {
/* 140 */       String timestampStr = TimeFormatHelper.getFormatDate(timestamp, "yyyy-MM-dd HH:mm:ss");
/* 141 */       sqlTimestamp = "to_date('" + timestampStr + "','YYYY-MM-DD HH24:MI:SS')";
/* 142 */     } else if (dbType == DbType.DB_TYPE_INFORMIX)
/*     */     {
/* 144 */       String timestampStr = TimeFormatHelper.getFormatDate(timestamp, "yyyy-MM-dd HH:mm:ss");
/* 145 */       sqlTimestamp = "to_date('" + timestampStr + "','%Y-%m-%d  %H:%M:%S')";
/*     */     } else {
/* 147 */       sqlTimestamp = "'" + TimeFormatHelper.getFormatDate(timestamp, "yyyy-MM-dd HH:mm:ss") + "'";
/*     */     }
/*     */ 
/* 150 */     return sqlTimestamp;
/*     */   }
/*     */ 
/*     */   public static String getTimestampNow(DbType dbType) throws Exception {
/* 154 */     java.util.Date date = new java.util.Date();
/* 155 */     return getTimestamp(dbType, new Timestamp(date.getTime()));
/*     */   }
/*     */ 
/*     */   public static String string2strSqlDate(String value) {
/* 159 */     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 160 */     formatter.setLenient(false);
/* 161 */     String rtn = null;
/*     */     try {
/* 163 */       java.util.Date dateValue = formatter.parse(value);
/* 164 */       if (dateValue != null) {
/* 165 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
/* 166 */         rtn = simpleDateFormat.format(dateValue);
/*     */         try {
/* 168 */           if (DbConnManager.getInstance().getDbType(null) == DbType.DB_TYPE_ORACLE) {
/* 169 */             rtn = "TO_DATE('" + rtn + "','" + "yyyy-MM-dd HH24:mi:ss" + "')";
/* 170 */           } else if (DbConnManager.getInstance().getDbType(null) == DbType.DB_TYPE_INFORMIX) {
/* 171 */             rtn = "'" + rtn + "'";
/* 172 */           } else if (DbConnManager.getInstance().getDbType(null) == DbType.DB_TYPE_SYBASE) {
/* 173 */             value = value.replaceAll("-", "/");
/* 174 */             rtn = "CONVERT(datetime,'" + value + "',111)";
/* 175 */           } else if (DbConnManager.getInstance().getDbType(null) == DbType.DB_TYPE_MYSQL) {
/* 176 */             rtn = "'" + rtn + "'";
/*     */           }
/*     */         } catch (Exception e) {
/* 179 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     } catch (ParseException e) {
/* 183 */       e.printStackTrace();
/*     */     }
/* 185 */     return rtn;
/*     */   }
/*     */   public static String createUpdateSql(GenericDO dbo, String sqlCond) throws Exception {
/* 188 */     return createUpdateSql(dbo, null, sqlCond, false);
/*     */   }
/*     */ 
/*     */   public static String createUpdateSql(GenericDO dbo, Map updateAttrs, String sqlCond, boolean withLastModifyTime)
/*     */     throws Exception
/*     */   {
/* 200 */     String[] attrNames = dbo.getAllAttrNames();
/*     */ 
/* 203 */     StringBuffer sql = new StringBuffer("update " + dbo.getTableName() + " set ");
/* 204 */     int updateFieldCount = 0;
/* 205 */     for (int i = 0; i < attrNames.length; i++) {
/* 206 */       String attrName = attrNames[i];
/*     */ 
/* 214 */       if (dbo.getAttrType(attrNames[i]) == DboBlob.class) {
/*     */         continue;
/*     */       }
/* 217 */       if ((updateAttrs != null) && (!updateAttrs.containsKey(attrNames[i])))
/*     */       {
/*     */         continue;
/*     */       }
/* 221 */       if (updateFieldCount > 0) {
/* 222 */         sql.append(", ");
/*     */       }
/* 224 */       sql.append(attrNames[i] + "=? ");
/* 225 */       updateFieldCount++;
/*     */     }
/* 227 */     if (withLastModifyTime) {
/* 228 */       sql.append(",LAST_MODIFY_TIME=? ");
/*     */     }
/* 230 */     return sql.toString() + sqlCond;
/*     */   }
/*     */ 
/*     */   public static String createUpdateSql(GenericDO dbo, GenericDO dbResObject, List list)
/*     */     throws Exception
/*     */   {
/* 242 */     Map dbMap = dbResObject.getAllAttr();
/* 243 */     return createUpdateSql(dbo, dbMap, list, false);
/*     */   }
/*     */ 
/*     */   public static String createUpdateSql(GenericDO dbo, Map dbMap, List list, boolean byCuid)
/*     */     throws Exception
/*     */   {
/* 255 */     String[] attrNames = dbo.getAllAttrNames();
/* 256 */     List modList = new ArrayList();
/* 257 */     Map resMap = dbo.getAllAttr();
/* 258 */     Iterator resIter = resMap.keySet().iterator();
/*     */ 
/* 260 */     boolean isModify = false;
/* 261 */     StringBuffer setSql = new StringBuffer();
/*     */ 
/* 263 */     while (resIter.hasNext()) {
/* 264 */       String fieldName = (String)resIter.next();
/* 265 */       String resValue = "" + resMap.get(fieldName);
/* 266 */       String dbValue = null;
/* 267 */       Iterator dbIter = dbMap.keySet().iterator();
/*     */ 
/* 269 */       while (dbIter.hasNext()) {
/* 270 */         String dbfName = (String)dbIter.next();
/* 271 */         if (fieldName.equals(dbfName))
/*     */         {
/* 275 */           if ((!fieldName.equalsIgnoreCase("CREATE_TIME")) && (!fieldName.equalsIgnoreCase("LAST_MODIFY_TIME")))
/*     */           {
/* 277 */             dbValue = "" + dbMap.get(dbfName);
/*     */ 
/* 280 */             if (((dbValue == null) || (dbValue.equals(resValue))) && ((resValue == null) || (resValue.equals(dbValue))))
/*     */               break;
/* 282 */             if (isModify) {
/* 283 */               setSql.append(" , ");
/*     */             }
/* 285 */             setSql.append(fieldName);
/* 286 */             setSql.append("=?");
/* 287 */             isModify = true;
/* 288 */             modList.add(fieldName); break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 295 */     String sql = "";
/*     */ 
/* 297 */     if (isModify) {
/* 298 */       setSql.append(",LAST_MODIFY_TIME=? ");
/* 299 */       sql = "UPDATE " + dbo.getTableName() + " SET " + setSql.toString() + " WHERE ";
/* 300 */       if (!byCuid)
/* 301 */         sql = sql + " OBJECTID=? " + dbo.getObjectNum();
/*     */       else {
/* 303 */         sql = sql + " CUID=? " + dbo.getCuid();
/*     */       }
/*     */     }
/* 306 */     if (list != null) {
/* 307 */       list.clear();
/* 308 */       list.addAll(modList);
/*     */     }
/* 310 */     return sql;
/*     */   }
/*     */ 
/*     */   public static String createUpdateSql(GenericDO dbo, String[] names, List list, boolean byCuid)
/*     */     throws Exception
/*     */   {
/* 324 */     String[] attrNames = dbo.getAllAttrNames();
/*     */ 
/* 326 */     List modList = new ArrayList();
/*     */ 
/* 335 */     boolean isModify = false;
/* 336 */     StringBuffer setSql = new StringBuffer();
/*     */ 
/* 338 */     for (int i = 0; i < names.length; i++) {
/* 339 */       String fieldName = names[i];
/* 340 */       if (isModify) {
/* 341 */         setSql.append(" , ");
/*     */       }
/* 343 */       setSql.append(fieldName);
/* 344 */       setSql.append("=?");
/* 345 */       isModify = true;
/* 346 */       modList.add(fieldName);
/*     */     }
/*     */ 
/* 378 */     String sql = "";
/*     */ 
/* 380 */     if (isModify) {
/* 381 */       setSql.append(",LAST_MODIFY_TIME=? ");
/* 382 */       sql = "UPDATE " + dbo.getTableName() + " SET " + setSql.toString() + " WHERE ";
/* 383 */       if (!byCuid)
/* 384 */         sql = sql + " OBJECTID=? ";
/*     */       else {
/* 386 */         sql = sql + " CUID=? ";
/*     */       }
/*     */     }
/* 389 */     if (list != null) {
/* 390 */       list.clear();
/* 391 */       list.addAll(modList);
/*     */     }
/* 393 */     return sql;
/*     */   }
/*     */ 
/*     */   public static String createUpdateSql(GenericDO dbo, Map updateAttrs, boolean withLastModifyTime)
/*     */     throws Exception
/*     */   {
/* 401 */     String sqlCond = "where OBJECTID=" + dbo.getObjectNum();
/* 402 */     return createUpdateSql(dbo, updateAttrs, sqlCond, withLastModifyTime);
/*     */   }
/*     */ 
/*     */   public static String createInsertSql(GenericDO dbo, DbType dbType)
/*     */     throws Exception
/*     */   {
/* 412 */     String[] names = dbo.getAllAttrNames();
/* 413 */     StringBuffer sql = new StringBuffer("insert into " + dbo.getTableName() + "(");
/*     */ 
/* 415 */     boolean isFirst = true;
/* 416 */     for (int i = 0; i < names.length; i++) {
/* 417 */       if (!isFirst) {
/* 418 */         sql.append(", ");
/*     */       }
/* 420 */       sql.append(names[i]);
/* 421 */       isFirst = false;
/*     */     }
/* 423 */     sql.append(") values(");
/*     */ 
/* 425 */     isFirst = true;
/* 426 */     for (int i = 0; i < names.length; i++) {
/* 427 */       if (!isFirst) {
/* 428 */         sql.append(",");
/*     */       }
/*     */ 
/* 431 */       if (dbo.getAttrType(names[i]) == DboBlob.class)
/* 432 */         sql.append(DbHelper.getEmptyBlobStr(dbType));
/*     */       else {
/* 434 */         sql.append("?");
/*     */       }
/* 436 */       isFirst = false;
/*     */     }
/* 438 */     sql.append(")");
/* 439 */     return sql.toString();
/*     */   }
/*     */ 
/*     */   public static String createInsertSql(GenericDO dbo, List names, DbType dbType, boolean withObjectId) throws Exception {
/* 443 */     String sql = "";
/* 444 */     if ((dbo != null) && (dbo.getClassName() != null)) {
/* 445 */       String[] attrNames = dbo.getAllAttrNames();
/* 446 */       Map attributeMap = new HashMap();
/* 447 */       for (int i = 0; i < attrNames.length; i++) {
/* 448 */         attributeMap.put(attrNames[i], "");
/* 449 */         names.add(attrNames[i]);
/*     */       }
/*     */ 
/* 456 */       if (withObjectId) {
/* 457 */         java.util.Date CREATE_TIME = dbo.getCreateTime();
/* 458 */         if (CREATE_TIME == null) {
/* 459 */           CREATE_TIME = new java.util.Date();
/* 460 */           dbo.setCreateTime(CREATE_TIME);
/*     */         }
/* 462 */         if (!attributeMap.containsKey("CREATE_TIME")) {
/* 463 */           names.add("CREATE_TIME");
/*     */         }
/* 465 */         attributeMap.put("CREATE_TIME", CREATE_TIME);
/* 466 */         java.util.Date LAST_MODIFY_TIME = dbo.getLastModifyTime();
/* 467 */         if (LAST_MODIFY_TIME == null) {
/* 468 */           LAST_MODIFY_TIME = new java.util.Date();
/* 469 */           dbo.setCreateTime(LAST_MODIFY_TIME);
/*     */         }
/* 471 */         if (!attributeMap.containsKey("LAST_MODIFY_TIME")) {
/* 472 */           names.add("LAST_MODIFY_TIME");
/*     */         }
/* 474 */         attributeMap.put("LAST_MODIFY_TIME", LAST_MODIFY_TIME);
/* 475 */         dbo.setLastModifyTime(LAST_MODIFY_TIME);
/*     */ 
/* 478 */         if (!attributeMap.containsKey("OBJECTID")) {
/* 479 */           names.add("OBJECTID");
/*     */         }
/* 481 */         long objectID = dbo.getObjectNum();
/* 482 */         attributeMap.put("OBJECTID", new Long(objectID));
/* 483 */         if (!attributeMap.containsKey("ISDELETE")) {
/* 484 */           names.add("ISDELETE");
/*     */         }
/* 486 */         attributeMap.put("ISDELETE", new Boolean(false));
/*     */       }
/* 488 */       String field = null;
/* 489 */       StringBuffer keys = new StringBuffer();
/* 490 */       StringBuffer values = new StringBuffer();
/* 491 */       for (int i = 0; i < names.size(); i++) {
/* 492 */         field = (String)names.get(i);
/* 493 */         Object value = attributeMap.get(field);
/*     */ 
/* 497 */         keys.append(field);
/* 498 */         if (dbo.getAttrType(field) == DboBlob.class)
/* 499 */           values.append(DbHelper.getEmptyBlobStr(dbType));
/*     */         else {
/* 501 */           values.append("?");
/*     */         }
/* 503 */         if (i < names.size() - 1) {
/* 504 */           keys.append(",");
/* 505 */           values.append(",");
/*     */         }
/*     */       }
/* 508 */       String keysql = keys.toString();
/* 509 */       String valuessql = values.toString();
/* 510 */       if (keysql.endsWith(",")) {
/* 511 */         keysql = keysql.substring(0, keysql.lastIndexOf(","));
/* 512 */         valuessql = valuessql.substring(0, valuessql.lastIndexOf(","));
/*     */       }
/* 514 */       sql = "INSERT INTO " + dbo.getClassName() + " (" + keysql + ") VALUES (" + valuessql + ")";
/*     */     }
/* 516 */     return sql;
/*     */   }
/*     */ 
/*     */   public static void setPrepareStatement(GenericDO dbo, List modList, PreparedStatement pst, String dbCharset) throws Exception {
/* 520 */     String[] attrNames = new String[modList.size()];
/* 521 */     for (int i = 0; i < modList.size(); i++) {
/* 522 */       attrNames[i] = modList.get(i).toString();
/*     */     }
/* 524 */     setPrepareStatement(dbo, attrNames, pst, dbCharset);
/*     */   }
/*     */ 
/*     */   public static void setPrepareStatement(GenericDO dbo, PreparedStatement pst, String dbCharset) throws Exception {
/* 528 */     String[] attrNames = dbo.getAllAttrNames();
/*     */ 
/* 532 */     setPrepareStatement(dbo, attrNames, pst, dbCharset);
/*     */   }
/*     */ 
/*     */   public static int setPrepareStatement(GenericDO dbo, String[] names, PreparedStatement pst, String dbCharset) throws Exception
/*     */   {
/* 537 */     return setPrepareStatement(dbo, names, null, pst, dbCharset);
/*     */   }
/*     */ 
/*     */   public static int setPrepareStatement(GenericDO dbo, String[] names, List values, PreparedStatement pst, String dbCharset) throws Exception
/*     */   {
/* 542 */     int columnIndex = 0;
/* 543 */     String name = "";
/* 544 */     Object value = null;
/* 545 */     Class fieldType = null;
/* 546 */     int i = 0;
/*     */     try {
/* 548 */       for (i = 0; i < names.length; i++) {
/* 549 */         name = names[i];
/* 550 */         value = dbo.getAttrValue(name);
/* 551 */         fieldType = dbo.getAttrType(name);
/* 552 */         if ("OBJECTID".equals(name)) {
/* 553 */           if (dbo.getObjectNum() <= 0L) continue;
/* 554 */           value = new Long(dbo.getObjectNum());
/*     */ 
/* 558 */           fieldType = Long.TYPE;
/* 559 */         } else if ("ISDELETE".equals(name)) {
/* 560 */           fieldType = Boolean.class;
/* 561 */         } else if ("GT_VERSION".equals(name)) {
/* 562 */           fieldType = Long.class;
/* 563 */         } else if ("LAST_MODIFY_TIME".equals(name)) {
/* 564 */           java.util.Date LAST_MODIFY_TIME = new java.util.Date();
/* 565 */           value = getTimestamp(LAST_MODIFY_TIME);
/*     */ 
/* 567 */           fieldType = Timestamp.class;
/* 568 */         } else if ("CREATE_TIME".equals(name)) {
/* 569 */           java.util.Date CREATE_TIME = new java.util.Date();
/* 570 */           value = getTimestamp(CREATE_TIME);
/* 571 */           fieldType = Timestamp.class;
/*     */         }
/*     */ 
/* 574 */         if (fieldType == DboBlob.class)
/*     */           continue;
/* 576 */         columnIndex++;
/* 577 */         if (values != null) {
/* 578 */           values.add(value);
/*     */         }
/* 580 */         if ((fieldType == Boolean.TYPE) || (fieldType == Boolean.class)) {
/* 581 */           if (value == null)
/* 582 */             pst.setNull(columnIndex, -7);
/*     */           else
/* 584 */             pst.setBoolean(columnIndex, ((Boolean)value).booleanValue());
/*     */         }
/* 586 */         else if ((fieldType == Long.TYPE) || (fieldType == Long.class)) {
/* 587 */           if (value == null)
/* 588 */             pst.setNull(columnIndex, -5);
/*     */           else
/* 590 */             pst.setLong(columnIndex, Long.parseLong("" + value));
/*     */         }
/* 592 */         else if ((fieldType == Double.TYPE) || (fieldType == Double.class) || (fieldType == Float.TYPE) || (fieldType == Float.class))
/*     */         {
/* 594 */           if (value == null)
/* 595 */             pst.setNull(columnIndex, 8);
/*     */           else
/* 597 */             pst.setDouble(columnIndex, Double.parseDouble("" + value));
/*     */         }
/* 599 */         else if (fieldType == String.class) {
/* 600 */           if (value == null)
/* 601 */             pst.setNull(columnIndex, 12);
/*     */           else
/* 603 */             pst.setString(columnIndex, prepareSql(dbCharset, (String)value));
/*     */         }
/* 605 */         else if (fieldType == Timestamp.class) {
/* 606 */           if (value == null) {
/* 607 */             pst.setNull(columnIndex, 93);
/*     */           }
/* 609 */           else if ((value instanceof Timestamp)) {
/* 610 */             pst.setTimestamp(columnIndex, (Timestamp)value);
/*     */           } else {
/* 612 */             Timestamp t = getTimestamp(value);
/* 613 */             pst.setTimestamp(columnIndex, t);
/*     */           }
/*     */         }
/*     */         else {
/* 617 */           String type = fieldType != null ? fieldType.getName() : "null";
/* 618 */           throw new UserException("插入数据库对象不支持的数据类型：" + type);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 623 */       String err = "pst设置属性出错：atrrNo=" + i + ",attrName=" + name + ",attrType=" + fieldType + ", value=" + value;
/* 624 */       LogHome.getLog().error(err);
/* 625 */       throw new UserException(err + " " + ex);
/*     */     }
/* 627 */     return columnIndex;
/*     */   }
/*     */ 
/*     */   public static void fillDataObjectMap(ResultSet rs, DbContext dbContext, String[] queryFieldList, Map dboMap, DboCollection rows, String sql)
/*     */     throws Exception
/*     */   {
/* 641 */     fillDataObjectMap(rs, dbContext, queryFieldList, dboMap, rows, sql, 0);
/*     */   }
/*     */ 
/*     */   public static void fillDataObjectMap(ResultSet rs, DbContext dbContext, String[] queryFieldList, Map dboMap, DboCollection rows, String sql, int fetchSize) throws Exception
/*     */   {
/* 646 */     ResultsetWrapper rsw = new ResultsetWrapper(rs, dbContext);
/*     */ 
/* 648 */     int k = 0;
/* 649 */     while ((rs.next()) && (
/* 650 */       (fetchSize <= 0) || (k < fetchSize)))
/*     */     {
/* 653 */       if ((k > 0) && (k % 2000 == 0)) {
/* 654 */         LogHome.getLog().warn("ResultSet Size:" + k + " SQL=" + sql);
/*     */       }
/* 656 */       DataObjectMap row = new DataObjectMap();
/* 657 */       for (int i = 0; i < queryFieldList.length; i++) {
/* 658 */         String queryFieldName = queryFieldList[i];
/* 659 */         String queryFieldPrefix = SqlParser.parseQueryFieldPrefix(queryFieldName);
/* 660 */         String dbFieldName = SqlParser.parseDBFieldName(queryFieldName);
/* 661 */         GenericDO dboTemplate = null;
/* 662 */         dboTemplate = (GenericDO)dboMap.get(queryFieldPrefix);
/* 663 */         GenericDO dbo = (GenericDO)row.get(dboTemplate.getBmClassId());
/* 664 */         if (dbo == null) {
/* 665 */           dbo = dboTemplate.cloneDboClass();
/* 666 */           dbo.setClassName(dboTemplate.getClassName());
/* 667 */           row.put(dbo.getClassName(), dbo);
/*     */         }
/* 669 */         setDBOField(rsw, i + 1, dbFieldName, dbo, dboTemplate);
/*     */       }
/* 671 */       rows.addRow(row);
/*     */ 
/* 648 */       k++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setDBOField(ResultsetWrapper rsw, int columnIIndex, Class colClassType, GenericDO dbo)
/*     */     throws Exception
/*     */   {
/* 676 */     String colIndexName = "" + columnIIndex;
/* 677 */     if ((colClassType == Long.TYPE) || (colClassType == Long.class)) {
/* 678 */       dbo.setAttrValue(colIndexName, rsw.getLong(columnIIndex));
/* 679 */     } else if ((colClassType == Integer.TYPE) || (colClassType == Integer.class)) {
/* 680 */       dbo.setAttrValue(colIndexName, rsw.getInt(columnIIndex));
/* 681 */     } else if (colClassType == String.class) {
/* 682 */       dbo.setAttrValue(colIndexName, rsw.getString(columnIIndex));
/* 683 */     } else if ((colClassType == Double.TYPE) || (colClassType == Double.class)) {
/* 684 */       if (rsw.getObject(columnIIndex) == null)
/* 685 */         dbo.setAttrValue(colIndexName, rsw.getObject(columnIIndex));
/*     */       else
/* 687 */         dbo.setAttrValue(colIndexName, rsw.getDouble(columnIIndex));
/*     */     }
/* 689 */     else if ((colClassType == Float.TYPE) || (colClassType == Float.class)) {
/* 690 */       dbo.setAttrValue(colIndexName, rsw.getFloat(columnIIndex));
/* 691 */     } else if ((colClassType == Boolean.TYPE) || (colClassType == Boolean.class)) {
/* 692 */       dbo.setAttrValue(colIndexName, rsw.getBoolean(columnIIndex));
/* 693 */     } else if (colClassType == Timestamp.class) {
/* 694 */       Timestamp time = rsw.getTimestamp(columnIIndex);
/* 695 */       if (time != null)
/* 696 */         dbo.setAttrValue(colIndexName, new Timestamp(time.getTime()));
/*     */       else
/* 698 */         dbo.setAttrNull(colIndexName);
/*     */     }
/*     */     else {
/* 701 */       dbo.setAttrValue(colIndexName, rsw.getObject(columnIIndex));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setDBOField(ResultsetWrapper rsw, int columnIIndex, String dbFieldName, GenericDO dbo, GenericDO dboTemplate)
/*     */     throws Exception
/*     */   {
/* 708 */     if ("OBJECTID".equals(dbFieldName)) {
/* 709 */       dbo.setObjectNum(rsw.getLong(columnIIndex));
/* 710 */     } else if ("CREATE_TIME".equals(dbFieldName)) {
/* 711 */       Timestamp time = rsw.getTimestamp(columnIIndex);
/* 712 */       if (time != null)
/* 713 */         dbo.setCreateTime(new java.util.Date(time.getTime()));
/*     */     }
/* 715 */     else if ("LAST_MODIFY_TIME".equals(dbFieldName)) {
/* 716 */       Timestamp time = rsw.getTimestamp(columnIIndex);
/* 717 */       if (time != null)
/* 718 */         dbo.setLastModifyTime(new java.util.Date(time.getTime()));
/*     */     }
/*     */     else {
/* 721 */       Class attrType = dboTemplate.getAttrType(dbFieldName);
/* 722 */       if ((attrType == Long.TYPE) || (attrType == Long.class)) {
/* 723 */         if (rsw.getObject(columnIIndex) != null)
/*     */         {
/* 725 */           dbo.setAttrValue(dbFieldName, rsw.getLong(columnIIndex));
/*     */         }
/* 727 */       } else if ((attrType == Boolean.TYPE) || (attrType == Boolean.class)) {
/* 728 */         dbo.setAttrValue(dbFieldName, rsw.getBoolean(columnIIndex));
/* 729 */       } else if ((attrType == Double.TYPE) || (attrType == Double.class)) {
/* 730 */         if (rsw.getObject(columnIIndex) == null)
/* 731 */           dbo.setAttrValue(dbFieldName, rsw.getObject(columnIIndex));
/*     */         else
/* 733 */           dbo.setAttrValue(dbFieldName, rsw.getDouble(columnIIndex));
/*     */       }
/* 735 */       else if (attrType == String.class) {
/* 736 */         dbo.setAttrValue(dbFieldName, rsw.getString(columnIIndex));
/* 737 */       } else if (attrType == Timestamp.class) {
/* 738 */         Timestamp time = rsw.getTimestamp(columnIIndex);
/* 739 */         if (time != null) {
/* 740 */           dbo.setAttrValue(dbFieldName, new Timestamp(time.getTime()));
/*     */         }
/*     */       }
/* 743 */       else if (attrType == DboBlob.class) {
/* 744 */         DbBlob blob = rsw.getBlob(columnIIndex);
/* 745 */         if (blob != null) {
/* 746 */           dbo.setAttrValue(dbFieldName, new DboBlob(blob.getBlobBytes()));
/*     */         }
/*     */       }
/* 749 */       else if ((attrType != null) && (!dbFieldName.equals("ISDELETE"))) {
/* 750 */         LogHome.getLog().error("DbCall threadId=" + ThreadHelper.getCurrentThreadId() + ", 未知字段类型：[attrName=" + dbFieldName + ", attrClassType=" + attrType + "]");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String[] getUpdateAttrs(GenericDO dbo, Map updateAttrs)
/*     */   {
/* 764 */     String[] attrNames = dbo.getAllAttrNames();
/* 765 */     List modList = new ArrayList();
/* 766 */     for (int i = 0; i < attrNames.length; i++) {
/* 767 */       String attrName = attrNames[i];
/*     */ 
/* 769 */       if ((attrName.equalsIgnoreCase("CREATE_TIME")) || (attrName.equalsIgnoreCase("LAST_MODIFY_TIME"))) {
/*     */         continue;
/*     */       }
/* 772 */       if (dbo.getAttrType(attrNames[i]) == DboBlob.class) {
/*     */         continue;
/*     */       }
/* 775 */       if ((updateAttrs != null) && (!updateAttrs.containsKey(attrNames[i]))) {
/*     */         continue;
/*     */       }
/* 778 */       modList.add(attrNames[i]);
/*     */     }
/* 780 */     String[] names = new String[modList.size()];
/* 781 */     for (int i = 0; i < modList.size(); i++) {
/* 782 */       names[i] = modList.get(i).toString();
/*     */     }
/* 784 */     return names;
/*     */   }
/*     */ 
/*     */   public static Map getUpdateDBAttrs(GenericDO dbo, Map updateAttrs) {
/* 788 */     Map attrs = updateAttrs;
/* 789 */     Map objMapAttr_Value = dbo.getAllAttr();
/* 790 */     if (updateAttrs == null)
/*     */     {
/* 792 */       updateAttrs = new HashMap();
/* 793 */       String[] allNames = dbo.getAllAttrNames();
/* 794 */       for (int i = 0; i < allNames.length; i++) {
/* 795 */         String attrName = allNames[i];
/* 796 */         if (objMapAttr_Value.containsKey(attrName)) {
/* 797 */           updateAttrs.put(attrName, objMapAttr_Value.get(attrName));
/*     */         }
/*     */       }
/* 800 */       attrs = updateAttrs;
/*     */     }
/* 802 */     return attrs;
/*     */   }
/*     */ 
/*     */   public static List getUpdateNames(Map updateAttrs, boolean byCuidOrObjectid) {
/* 806 */     List result = new ArrayList();
/* 807 */     String lmt = "LAST_MODIFY_TIME";
/* 808 */     String[] updateNames = new String[updateAttrs.size()];
/* 809 */     String[] names = new String[updateAttrs.size() + 2];
/*     */ 
/* 811 */     if (updateAttrs.containsKey(lmt)) {
/* 812 */       updateNames = new String[updateAttrs.size() - 1];
/* 813 */       names = new String[updateAttrs.size() + 1];
/*     */     }
/* 815 */     Iterator iterator = updateAttrs.keySet().iterator();
/* 816 */     int j = 0;
/* 817 */     while (iterator.hasNext()) {
/* 818 */       String attr = iterator.next().toString();
/* 819 */       if (attr.equals(lmt))
/*     */         continue;
/* 821 */       names[j] = attr;
/* 822 */       updateNames[j] = names[j];
/* 823 */       j++;
/*     */     }
/* 825 */     if (!updateAttrs.containsKey(lmt)) {
/* 826 */       names[(names.length - 2)] = "LAST_MODIFY_TIME";
/*     */     }
/*     */ 
/* 829 */     if (byCuidOrObjectid)
/* 830 */       names[(names.length - 1)] = "CUID";
/*     */     else {
/* 832 */       names[(names.length - 1)] = "OBJECTID";
/*     */     }
/* 834 */     result.add(names);
/* 835 */     result.add(updateNames);
/* 836 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.SqlHelper
 * JD-Core Version:    0.6.0
 */