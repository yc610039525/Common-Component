/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.plugin.IMultiAttrParser;
/*     */ import com.boco.raptor.drm.core.plugin.impl.BasicMultiAttrParser;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Date;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.builder.ToStringBuilder;
/*     */ import org.apache.commons.lang.builder.ToStringStyle;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BMAttrMeta
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String bmClassId;
/*     */   private String attrId;
/*     */   private String labelCn;
/*  65 */   private Integer attrDbType = Integer.valueOf(0);
/*     */   private String defaultValue;
/*     */   private String enumId;
/*  96 */   private Boolean isNotNull = Boolean.valueOf(false);
/*     */ 
/* 103 */   private Boolean isSystem = Boolean.valueOf(false);
/*     */ 
/* 109 */   private Boolean isReadOnly = Boolean.valueOf(false);
/*     */ 
/* 116 */   private Boolean isMultiValue = Boolean.valueOf(false);
/*     */ 
/* 122 */   private Boolean isBatchModify = Boolean.valueOf(false);
/*     */ 
/* 128 */   private Boolean isDimension = Boolean.valueOf(false);
/*     */   private Map<String, String> relatedAttrIds;
/* 142 */   private List<String> upperAttrIds = new ArrayList();
/*     */ 
/* 150 */   private Integer sourceType = Integer.valueOf(0);
/*     */   private String validRegExp;
/*     */   private String regExpExample;
/*     */   private String validCalcExp;
/*     */   private String xtype;
/* 182 */   private int strAttrMaxLen = 0;
/*     */   private List<String> allUpperAttrList;
/* 188 */   private String rootAttrId = "";
/*     */ 
/* 194 */   private Boolean hasChildAttrId = Boolean.valueOf(false);
/*     */   private String remark;
/*     */   private IMultiAttrParser multiParser;
/*     */ 
/*     */   public BMAttrMeta(String bmClassId, String attrId, String labelCn)
/*     */   {
/* 223 */     this.bmClassId = bmClassId;
/* 224 */     this.attrId = attrId;
/* 225 */     this.labelCn = labelCn;
/*     */   }
/*     */ 
/*     */   public BMAttrMeta() {
/*     */   }
/*     */ 
/*     */   public String getAttrId() {
/* 232 */     return this.attrId;
/*     */   }
/*     */ 
/*     */   public Integer getAttrDbType() {
/* 236 */     return this.attrDbType;
/*     */   }
/*     */ 
/*     */   public Class getAttrClassType() {
/* 240 */     Class attrClassType = null;
/* 241 */     if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.INT)
/* 242 */       attrClassType = Integer.TYPE;
/* 243 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.LONG)
/* 244 */       attrClassType = Long.TYPE;
/* 245 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.FLOAT)
/* 246 */       attrClassType = Float.TYPE;
/* 247 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.DOUBLE)
/* 248 */       attrClassType = Double.TYPE;
/* 249 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.STRING)
/* 250 */       attrClassType = String.class;
/* 251 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.BOOLEAN)
/* 252 */       attrClassType = Boolean.TYPE;
/* 253 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.DATE)
/* 254 */       attrClassType = Date.class;
/* 255 */     else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.TIME_STAMP)
/* 256 */       attrClassType = Timestamp.class;
/*     */     else {
/* 258 */       attrClassType = Object.class;
/*     */     }
/*     */ 
/* 261 */     return attrClassType;
/*     */   }
/*     */ 
/*     */   public String getDefaultValue() {
/* 265 */     return this.defaultValue;
/*     */   }
/*     */ 
/*     */   public String getEnumId() {
/* 269 */     return this.enumId;
/*     */   }
/*     */ 
/*     */   public Boolean getIsNotNull() {
/* 273 */     return this.isNotNull;
/*     */   }
/*     */ 
/*     */   public Boolean getIsReadOnly() {
/* 277 */     if (getSourceType().intValue() == 1) {
/* 278 */       return Boolean.valueOf(true);
/*     */     }
/* 280 */     return this.isReadOnly;
/*     */   }
/*     */ 
/*     */   public Boolean getIsRelation()
/*     */   {
/* 285 */     return Boolean.valueOf(this.relatedAttrIds != null);
/*     */   }
/*     */ 
/*     */   public Boolean getIsEnumAttr() {
/* 289 */     return Boolean.valueOf(this.enumId != null);
/*     */   }
/*     */ 
/*     */   public Boolean getIsSystem() {
/* 293 */     return this.isSystem;
/*     */   }
/*     */ 
/*     */   public String getLabelCn() {
/* 297 */     return this.labelCn != null ? this.labelCn : this.attrId;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getRelatedAttrIds() {
/* 301 */     return this.relatedAttrIds;
/*     */   }
/*     */ 
/*     */   public String getRelatedAttrId(String dbClassId) {
/* 305 */     return this.relatedAttrIds != null ? (String)this.relatedAttrIds.get(dbClassId) : null;
/*     */   }
/*     */ 
/*     */   public String getXtype() {
/* 309 */     String _xtype = this.xtype;
/* 310 */     if (_xtype == null) {
/* 311 */       if (getIsEnumAttr().booleanValue())
/* 312 */         _xtype = ATTR_XTYPE_FIELD.ENUM;
/* 313 */       else if (getIsRelation().booleanValue()) {
/* 314 */         _xtype = ATTR_XTYPE_FIELD.RELATION;
/*     */       }
/* 316 */       else if ((this.attrDbType.intValue() == ATTR_TYPE_ENUM.INT) || (this.attrDbType.intValue() == ATTR_TYPE_ENUM.LONG))
/*     */       {
/* 318 */         _xtype = ATTR_XTYPE_FIELD.INT;
/* 319 */       } else if ((this.attrDbType.intValue() == ATTR_TYPE_ENUM.FLOAT) || (this.attrDbType.intValue() == ATTR_TYPE_ENUM.DOUBLE))
/*     */       {
/* 321 */         _xtype = ATTR_XTYPE_FIELD.FLOAT;
/* 322 */       } else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.BOOLEAN)
/* 323 */         _xtype = ATTR_XTYPE_FIELD.BOOL;
/* 324 */       else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.DATE)
/* 325 */         _xtype = ATTR_XTYPE_FIELD.DATETIME;
/* 326 */       else if (this.attrDbType.intValue() == ATTR_TYPE_ENUM.TIME_STAMP)
/* 327 */         _xtype = ATTR_XTYPE_FIELD.DATETIME;
/* 328 */       else if ((getIsEnumAttr().booleanValue()) && (getIsMultiValue().booleanValue()))
/* 329 */         _xtype = ATTR_XTYPE_FIELD.MULTI_ENUM;
/*     */       else {
/* 331 */         _xtype = ATTR_XTYPE_FIELD.TEXT;
/*     */       }
/*     */     }
/*     */ 
/* 335 */     return _xtype;
/*     */   }
/*     */ 
/*     */   public Integer getSourceType() {
/* 339 */     return this.sourceType;
/*     */   }
/*     */ 
/*     */   public List<String> getUpperAttrIds() {
/* 343 */     return this.upperAttrIds;
/*     */   }
/*     */ 
/*     */   public String getValidCalcExp() {
/* 347 */     return (this.validCalcExp != null) && (this.validCalcExp.trim().length() == 0) ? null : this.validCalcExp;
/*     */   }
/*     */ 
/*     */   public String getValidRegExp() {
/* 351 */     return (this.validRegExp != null) && (this.validRegExp.trim().length() == 0) ? null : this.validRegExp;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 355 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public IMultiAttrParser getMultiParser() {
/* 359 */     IMultiAttrParser _multiParser = null;
/* 360 */     if (this.isMultiValue.booleanValue()) {
/* 361 */       _multiParser = this.multiParser != null ? this.multiParser : new BasicMultiAttrParser();
/*     */     }
/* 363 */     return _multiParser;
/*     */   }
/*     */ 
/*     */   public Boolean getIsMultiValue() {
/* 367 */     if (getAttrClassType() != String.class) {
/* 368 */       return Boolean.valueOf(false);
/*     */     }
/* 370 */     return this.isMultiValue;
/*     */   }
/*     */ 
/*     */   public Boolean getIsBatchModify() {
/* 374 */     return this.isBatchModify;
/*     */   }
/*     */ 
/*     */   public String getRegExpExample() {
/* 378 */     return this.regExpExample;
/*     */   }
/*     */ 
/*     */   public int getStrAttrMaxLen() {
/* 382 */     return this.strAttrMaxLen;
/*     */   }
/*     */ 
/*     */   public String getRemark() {
/* 386 */     return this.remark;
/*     */   }
/*     */ 
/*     */   public Boolean getHasChildAttrId() {
/* 390 */     return this.hasChildAttrId;
/*     */   }
/*     */ 
/*     */   public Boolean getIsDimension() {
/* 394 */     return this.isDimension;
/*     */   }
/*     */ 
/*     */   public void setAttrId(String attrId) {
/* 398 */     this.attrId = attrId;
/*     */   }
/*     */ 
/*     */   public void setAttrDbType(Integer attrDbType) {
/* 402 */     this.attrDbType = attrDbType;
/*     */   }
/*     */ 
/*     */   public void setAttrClassType(Class attrClass) {
/* 406 */     if (attrClass == Integer.TYPE)
/* 407 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.INT);
/* 408 */     else if (attrClass == Long.TYPE)
/* 409 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.LONG);
/* 410 */     else if (attrClass == Float.TYPE)
/* 411 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.FLOAT);
/* 412 */     else if (attrClass == Double.TYPE)
/* 413 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.DOUBLE);
/* 414 */     else if (attrClass == Boolean.TYPE)
/* 415 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.BOOLEAN);
/* 416 */     else if (attrClass == String.class)
/* 417 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.STRING);
/* 418 */     else if (attrClass == Date.class)
/* 419 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.DATE);
/* 420 */     else if (attrClass == Timestamp.class)
/* 421 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.TIME_STAMP);
/*     */     else
/* 423 */       this.attrDbType = Integer.valueOf(ATTR_TYPE_ENUM.UNKNOWN);
/*     */   }
/*     */ 
/*     */   public void setDefaultValue(String defaultValue)
/*     */   {
/* 428 */     this.defaultValue = defaultValue;
/*     */   }
/*     */ 
/*     */   public void setEnumId(String enumId) {
/* 432 */     this.enumId = enumId;
/*     */   }
/*     */ 
/*     */   public void setIsNotNull(Boolean isNotNull) {
/* 436 */     this.isNotNull = isNotNull;
/*     */   }
/*     */ 
/*     */   public void setIsReadOnly(Boolean isReadOnly) {
/* 440 */     this.isReadOnly = isReadOnly;
/*     */   }
/*     */ 
/*     */   public void setIsSystem(Boolean isSystem) {
/* 444 */     this.isSystem = isSystem;
/*     */   }
/*     */ 
/*     */   public void setLabelCn(String labelCn) {
/* 448 */     this.labelCn = labelCn;
/*     */   }
/*     */ 
/*     */   public void addRelatedAttrId(String dbClassId, String attrId) {
/* 452 */     if (this.relatedAttrIds == null) {
/* 453 */       this.relatedAttrIds = new HashMap();
/*     */     }
/* 455 */     this.relatedAttrIds.put(dbClassId, attrId);
/*     */   }
/*     */ 
/*     */   public void setXtype(String xtype) {
/* 459 */     this.xtype = xtype;
/*     */   }
/*     */ 
/*     */   public void setSourceType(Integer sourceType) {
/* 463 */     this.sourceType = sourceType;
/*     */   }
/*     */ 
/*     */   public void setUpperAttrIds(List<String> upperAttrIds) {
/* 467 */     Iterator it = upperAttrIds.iterator();
/* 468 */     while (it.hasNext()) {
/* 469 */       String _upperAttrId = (String)it.next();
/* 470 */       if (this.attrId.equals(_upperAttrId)) {
/* 471 */         LogHome.getLog().error("bmClassId=" + this.bmClassId + ", attrId=" + this.attrId + ", upperAttrId和本属性错误 ！");
/*     */ 
/* 473 */         it.remove();
/*     */       }
/*     */     }
/* 476 */     this.upperAttrIds = upperAttrIds;
/*     */   }
/*     */ 
/*     */   public void setValidCalcExp(String validCalcExp) {
/* 480 */     this.validCalcExp = validCalcExp;
/*     */   }
/*     */ 
/*     */   public void setValidRegExp(String validRegExp) {
/* 484 */     this.validRegExp = validRegExp;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 488 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setMultiParserClassName(String multiParserClassName) {
/*     */     try {
/* 493 */       this.multiParser = ((IMultiAttrParser)Class.forName(multiParserClassName).newInstance());
/*     */     } catch (Exception ex) {
/* 495 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setIsMultiValue(Boolean isMultiValue) {
/* 500 */     this.isMultiValue = isMultiValue;
/*     */   }
/*     */ 
/*     */   public void setIsBatchModify(Boolean isBatchModify) {
/* 504 */     this.isBatchModify = isBatchModify;
/*     */   }
/*     */ 
/*     */   public void setRegExpExample(String regExpExample) {
/* 508 */     this.regExpExample = regExpExample;
/*     */   }
/*     */ 
/*     */   public void setStrAttrMaxLen(int strAttrMaxLen) {
/* 512 */     this.strAttrMaxLen = strAttrMaxLen;
/*     */   }
/*     */ 
/*     */   public void setRemark(String remark) {
/* 516 */     this.remark = remark;
/*     */   }
/*     */ 
/*     */   public void setRelatedAttrIds(Map<String, String> relatedAttrIds) {
/* 520 */     this.relatedAttrIds = relatedAttrIds;
/*     */   }
/*     */ 
/*     */   public void setMultiParser(IMultiAttrParser multiParser) {
/* 524 */     this.multiParser = multiParser;
/*     */   }
/*     */ 
/*     */   public void setHasChildAttrId(Boolean hasChildAttrId) {
/* 528 */     this.hasChildAttrId = hasChildAttrId;
/*     */   }
/*     */ 
/*     */   public void setIsDimension(Boolean isDimension) {
/* 532 */     this.isDimension = isDimension;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 536 */     return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
/*     */   }
/*     */ 
/*     */   public String getDbTypeName() {
/* 540 */     int dbType = getAttrDbType().intValue();
/* 541 */     String sReturn = "";
/* 542 */     if (dbType == 1)
/* 543 */       sReturn = "字符串";
/* 544 */     else if (dbType == 2)
/* 545 */       sReturn = "整型";
/* 546 */     else if (dbType == 3)
/* 547 */       sReturn = "长整型";
/* 548 */     else if (dbType == 4)
/* 549 */       sReturn = "实数型";
/* 550 */     else if (dbType == 5)
/* 551 */       sReturn = "双精度";
/* 552 */     else if (dbType == 8)
/* 553 */       sReturn = "布尔型";
/* 554 */     else if (dbType == 9)
/* 555 */       sReturn = "日期型";
/* 556 */     else if (dbType == 10)
/* 557 */       sReturn = "时间型";
/* 558 */     else if (dbType == 2)
/* 559 */       sReturn = "整型";
/* 560 */     else if (dbType == 2)
/* 561 */       sReturn = "整型";
/* 562 */     else if (dbType == 2)
/* 563 */       sReturn = "整型";
/*     */     else {
/* 565 */       sReturn = "未知类型";
/*     */     }
/* 567 */     return sReturn;
/*     */   }
/*     */ 
/*     */   public void getAllUpperAttrs(List<String> upperIdList)
/*     */   {
/* 574 */     if ((getUpperAttrIds() == null) || (getUpperAttrIds().size() == 0)) {
/* 575 */       return;
/*     */     }
/*     */ 
/* 578 */     if ((this.allUpperAttrList == null) || (this.allUpperAttrList.size() == 0))
/* 579 */       for (int i = 0; i < getUpperAttrIds().size(); i++) {
/* 580 */         BMAttrMeta mat = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), getBmClassId(), (String)getUpperAttrIds().get(i));
/* 581 */         upperIdList.add(getUpperAttrIds().get(i));
/* 582 */         if (mat.getUpperAttrIds().size() > 0) {
/* 583 */           mat.getAllUpperAttrs(upperIdList);
/* 584 */           this.allUpperAttrList = upperIdList;
/*     */         }
/*     */       }
/*     */     else
/* 588 */       for (int i = 0; i < this.allUpperAttrList.size(); i++)
/* 589 */         upperIdList.add(this.allUpperAttrList.get(i));
/*     */   }
/*     */ 
/*     */   public String getRootAttr(List<String> allAttrList)
/*     */   {
/* 598 */     String sRootAttrId = this.attrId;
/* 599 */     boolean bFlag = true;
/*     */ 
/* 601 */     if ((this.rootAttrId == null) || (this.rootAttrId.equals("")));
/* 602 */     while (bFlag) {
/* 603 */       int i = 0;
/* 604 */       for (i = 0; i < allAttrList.size(); i++) {
/* 605 */         if (((String)allAttrList.get(i)).equals(this.attrId)) {
/*     */           continue;
/*     */         }
/* 608 */         BMAttrMeta mat = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), this.bmClassId, (String)allAttrList.get(i));
/* 609 */         if (mat == null) {
/*     */           continue;
/*     */         }
/* 612 */         List upperIdList = new ArrayList();
/* 613 */         mat.getAllUpperAttrs(upperIdList);
/* 614 */         if (upperIdList.contains(sRootAttrId)) {
/* 615 */           sRootAttrId = mat.getAttrId();
/* 616 */           break;
/*     */         }
/*     */       }
/* 619 */       if (i == allAttrList.size())
/*     */       {
/* 621 */         bFlag = false;
/*     */       }
/* 623 */       continue;
/*     */ 
/* 625 */       sRootAttrId = this.rootAttrId;
/*     */     }
/*     */ 
/* 628 */     return sRootAttrId;
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService()
/*     */   {
/* 640 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static class ATTR_XTYPE_FIELD
/*     */   {
/* 197 */     public static String TEXT = "text-field";
/* 198 */     public static String ENUM = "enum-field";
/* 199 */     public static String RELATION = "relation-field";
/* 200 */     public static String INT = "int-field";
/* 201 */     public static String FLOAT = "float-field";
/* 202 */     public static String BOOL = "bool-field";
/* 203 */     public static String DATE = "date-field";
/* 204 */     public static String TIME = "time-field";
/* 205 */     public static String DATETIME = "datetime-field";
/* 206 */     public static String MULTI_ENUM = "multi-enum";
/*     */   }
/*     */ 
/*     */   public static class ATTR_TYPE_ENUM
/*     */   {
/*  68 */     public static int UNKNOWN = 0;
/*  69 */     public static int STRING = 1;
/*  70 */     public static int INT = 2;
/*  71 */     public static int LONG = 3;
/*  72 */     public static int FLOAT = 4;
/*  73 */     public static int DOUBLE = 5;
/*  74 */     public static int BOOLEAN = 8;
/*  75 */     public static int DATE = 9;
/*  76 */     public static int TIME_STAMP = 10;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.BMAttrMeta
 * JD-Core Version:    0.6.0
 */