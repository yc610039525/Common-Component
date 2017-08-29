/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.plugin.IActionAdaptor;
/*     */ import com.boco.raptor.drm.core.plugin.IObjectValidator;
/*     */ import com.boco.raptor.drm.core.plugin.impl.DrmDynObjValidator;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import groovy.lang.GroovyShell;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.lang.builder.ToStringBuilder;
/*     */ import org.apache.commons.lang.builder.ToStringStyle;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BMClassMeta
/*     */   implements Serializable, Comparable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String DIV_VALUE_SPLIT = "-";
/*     */   private String dbClassId;
/*     */   private String bmClassId;
/*     */   private String labelCn;
/*     */   private String alias;
/*  79 */   private Integer bussinessType = Integer.valueOf(0);
/*     */ 
/*  86 */   private Boolean isSlave = Boolean.valueOf(false);
/*     */ 
/*  93 */   private String labelAttrId = null;
/*     */ 
/* 100 */   private String labelJoinChar = "-";
/*     */ 
/* 106 */   private Boolean isDimension = Boolean.valueOf(false);
/*     */ 
/* 113 */   private List<String> constructLabelAttrIds = new ArrayList();
/*     */ 
/* 120 */   private List<String> parentClassAttrIds = new ArrayList();
/*     */ 
/* 127 */   private String pkAttrId = "OBJECTID";
/*     */ 
/* 133 */   private String cuidAttrId = "CUID";
/*     */ 
/* 139 */   private String objSelectXType = "drm-complex-select";
/*     */ 
/* 147 */   private Map<String, String[]> uniqueAttrIds = new HashMap();
/*     */   private Map<String, String> statAttrIds;
/*     */   private List<String> relatedActionUrls;
/* 168 */   private Map<String, BMAttrMeta> attrMetas = new HashMap();
/*     */ 
/* 174 */   private RelatedClassTreeNode relatedClassNode = null;
/*     */   private String bmDivideCond;
/*     */   private String remark;
/* 192 */   private IObjectValidator objectValidator = new DrmDynObjValidator();
/*     */ 
/* 198 */   private Boolean isShowAddBtn = Boolean.valueOf(true);
/*     */ 
/* 204 */   private Boolean isShowEditBtn = Boolean.valueOf(true);
/*     */ 
/* 210 */   private Boolean isShowDeleteBtn = Boolean.valueOf(true);
/*     */ 
/* 216 */   private transient IActionAdaptor actionAdaptor = null;
/*     */ 
/* 222 */   private String entityClassName = "com.boco.raptor.drm.core.dto.impl.DrmDataObject";
/*     */ 
/* 228 */   private boolean dynClass = true;
/*     */ 
/* 233 */   private Map<String, String> allRootAttrMap = new HashMap();
/*     */ 
/* 240 */   private Map<String, List<BMAttrMeta>> relatedClassAttrIds = new HashMap();
/*     */ 
/*     */   public BMClassMeta() {
/*     */   }
/*     */ 
/*     */   public BMClassMeta(String dbClassId, String bmClassId, String labelCn) {
/* 246 */     this.dbClassId = dbClassId;
/* 247 */     this.bmClassId = bmClassId;
/* 248 */     this.labelCn = labelCn;
/*     */   }
/*     */ 
/*     */   public void init() {
/* 252 */     initBmClassMetaCache();
/* 253 */     initDivClassMeta();
/*     */   }
/*     */ 
/*     */   private void initDivClassMeta()
/*     */   {
/*     */     Map divAttrValues;
/* 257 */     if (isChildBmClass()) {
/* 258 */       divAttrValues = getBmDivideAttrValues();
/* 259 */       for (String attrId : divAttrValues.keySet()) {
/* 260 */         String divAttrValue = (String)divAttrValues.get(attrId);
/* 261 */         String[] _divAttrValue = divAttrValue.split("-");
/* 262 */         BMAttrMeta attrMeta = getAttrMeta(attrId);
/* 263 */         if (_divAttrValue.length > 2) {
/* 264 */           attrMeta.setIsSystem(Boolean.valueOf(false));
/* 265 */           attrMeta.setIsReadOnly(Boolean.valueOf(false));
/* 266 */           attrMeta.setDefaultValue("");
/*     */         } else {
/* 268 */           attrMeta.setIsSystem(Boolean.valueOf(true));
/* 269 */           attrMeta.setIsReadOnly(Boolean.valueOf(true));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<String, BMAttrMeta> getAllAttrMetas() {
/* 276 */     return this.attrMetas;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 280 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public Integer getBussinessType() {
/* 284 */     return this.bussinessType;
/*     */   }
/*     */ 
/*     */   public String getDbClassId() {
/* 288 */     return this.dbClassId;
/*     */   }
/*     */ 
/*     */   public String getLabelCn() {
/* 292 */     return this.labelCn != null ? this.labelCn : this.bmClassId;
/*     */   }
/*     */ 
/*     */   public String getAlias() {
/* 296 */     return this.alias;
/*     */   }
/*     */ 
/*     */   public String getDbTableName() {
/* 300 */     return this.dbClassId;
/*     */   }
/*     */ 
/*     */   public Map<String, String[]> getUniqueAttrIds() {
/* 304 */     return this.uniqueAttrIds;
/*     */   }
/*     */ 
/*     */   public String getLabelJoinChar() {
/* 308 */     return this.labelJoinChar;
/*     */   }
/*     */ 
/*     */   public IObjectValidator getObjectValidator() {
/* 312 */     return this.objectValidator;
/*     */   }
/*     */ 
/*     */   public List<String> getConstructLabelAttrIds() {
/* 316 */     if ((this.constructLabelAttrIds.size() == 0) && (this.labelAttrId != null)) {
/* 317 */       this.constructLabelAttrIds.add(this.labelAttrId);
/*     */     }
/* 319 */     return this.constructLabelAttrIds;
/*     */   }
/*     */ 
/*     */   public String getLabelAttrId() {
/* 323 */     String _labelAttrId = this.labelAttrId;
/* 324 */     if ((this.constructLabelAttrIds != null) && (this.constructLabelAttrIds.size() > 1))
/* 325 */       _labelAttrId = null;
/* 326 */     else if ((this.constructLabelAttrIds != null) && (this.constructLabelAttrIds.size() == 1)) {
/* 327 */       _labelAttrId = (String)this.constructLabelAttrIds.get(0);
/*     */     }
/* 329 */     return _labelAttrId;
/*     */   }
/*     */ 
/*     */   public String _getLabelAttrId() {
/* 333 */     String _labelAttrId = this.labelAttrId;
/* 334 */     if ((this.constructLabelAttrIds != null) && (this.constructLabelAttrIds.size() > 1)) {
/* 335 */       for (int i = 0; i < this.constructLabelAttrIds.size(); i++) {
/* 336 */         if (i == 0)
/* 337 */           _labelAttrId = (String)this.constructLabelAttrIds.get(i);
/*     */         else {
/* 339 */           _labelAttrId = _labelAttrId + this.labelJoinChar + (String)this.constructLabelAttrIds.get(i);
/*     */         }
/*     */       }
/*     */     }
/* 343 */     else if ((this.constructLabelAttrIds != null) && (this.constructLabelAttrIds.size() == 1)) {
/* 344 */       _labelAttrId = (String)this.constructLabelAttrIds.get(0);
/*     */     }
/* 346 */     return _labelAttrId;
/*     */   }
/*     */ 
/*     */   public String getPkAttrId() {
/* 350 */     return this.pkAttrId;
/*     */   }
/*     */ 
/*     */   public String getCuidAttrId() {
/* 354 */     return this.cuidAttrId;
/*     */   }
/*     */ 
/*     */   public String getObjSelectXType() {
/* 358 */     return this.objSelectXType;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getStatAttrIds() {
/* 362 */     return this.statAttrIds;
/*     */   }
/*     */ 
/*     */   public List<String> getRelatedActionUrls() {
/* 366 */     return this.relatedActionUrls;
/*     */   }
/*     */ 
/*     */   public boolean isDynClass() {
/* 370 */     return this.dynClass;
/*     */   }
/*     */ 
/*     */   public String getEntityClassName() {
/* 374 */     return this.entityClassName;
/*     */   }
/*     */ 
/*     */   public Boolean getIsSlave() {
/* 378 */     return this.isSlave;
/*     */   }
/*     */ 
/*     */   public RelatedClassTreeNode getRelatedClassNode() {
/* 382 */     return this.relatedClassNode;
/*     */   }
/*     */ 
/*     */   public List getParentClassAttrIds() {
/* 386 */     return this.parentClassAttrIds;
/*     */   }
/*     */ 
/*     */   public String getBmDivideCond() {
/* 390 */     return this.bmDivideCond;
/*     */   }
/*     */ 
/*     */   public String getRemark() {
/* 394 */     return this.remark;
/*     */   }
/*     */ 
/*     */   public IActionAdaptor getActionAdaptor() {
/* 398 */     return this.actionAdaptor;
/*     */   }
/*     */ 
/*     */   public String getBmDivideSqlCond() {
/* 402 */     String bmDivideSqlCond = this.bmDivideCond;
/* 403 */     if (this.bmDivideCond != null) {
/* 404 */       for (String attrId : this.attrMetas.keySet()) {
/* 405 */         bmDivideSqlCond = bmDivideSqlCond.replace("$" + attrId, attrId);
/*     */       }
/*     */     }
/* 408 */     return bmDivideSqlCond == null ? "" : bmDivideSqlCond;
/*     */   }
/*     */ 
/*     */   public boolean getIsRelatedConstraint() {
/* 412 */     return this.relatedClassNode != null;
/*     */   }
/*     */ 
/*     */   public BMAttrMeta getAttrMeta(String attrId) {
/* 416 */     return (BMAttrMeta)this.attrMetas.get(attrId);
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 420 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setDbClassId(String dbClassId) {
/* 424 */     this.dbClassId = dbClassId;
/*     */   }
/*     */ 
/*     */   public void setLabelCn(String labelCn) {
/* 428 */     this.labelCn = labelCn;
/*     */   }
/*     */ 
/*     */   public void setAlias(String alias) {
/* 432 */     this.alias = alias;
/*     */   }
/*     */ 
/*     */   public void setBussinessType(Integer bussinessType) {
/* 436 */     this.bussinessType = bussinessType;
/*     */   }
/*     */ 
/*     */   public void setLabelJoinChar(String labelJoinChar) {
/* 440 */     this.labelJoinChar = labelJoinChar;
/*     */   }
/*     */ 
/*     */   public void setObjectValidatorClassName(String validatorClassName) {
/*     */     try {
/* 445 */       if (validatorClassName != null)
/* 446 */         this.objectValidator = ((IObjectValidator)Class.forName(validatorClassName).newInstance());
/*     */     }
/*     */     catch (Exception ex) {
/* 449 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setConstructLabelAttrIds(List constructLabelAttrIds) {
/* 454 */     this.constructLabelAttrIds = constructLabelAttrIds;
/*     */   }
/*     */ 
/*     */   public void setLabelAttrId(String labelAttrId) {
/* 458 */     this.labelAttrId = labelAttrId;
/*     */   }
/*     */ 
/*     */   public void setPkAttrId(String pkAttrId) {
/* 462 */     this.pkAttrId = pkAttrId;
/*     */   }
/*     */ 
/*     */   public void setCuidAttrId(String cuidAttrId) {
/* 466 */     this.cuidAttrId = cuidAttrId;
/*     */   }
/*     */ 
/*     */   public void setObjSelectXType(String objSelectXType) {
/* 470 */     this.objSelectXType = objSelectXType;
/*     */   }
/*     */ 
/*     */   public void setStatAttrIds(Map<String, String> statAttrIds) {
/* 474 */     this.statAttrIds = statAttrIds;
/*     */   }
/*     */ 
/*     */   public void setRelatedActionUrls(List<String> relatedActionUrls) {
/* 478 */     this.relatedActionUrls = relatedActionUrls;
/*     */   }
/*     */ 
/*     */   public void setDynClass(boolean dynClass) {
/* 482 */     this.dynClass = dynClass;
/*     */   }
/*     */ 
/*     */   public void setEntityClassName(String entityClassName) {
/* 486 */     this.entityClassName = entityClassName;
/*     */   }
/*     */ 
/*     */   public void setIsSlave(Boolean isSlave) {
/* 490 */     this.isSlave = isSlave;
/*     */   }
/*     */ 
/*     */   public void setRelatedClassNode(RelatedClassTreeNode relatedClassNode) {
/* 494 */     this.relatedClassNode = relatedClassNode;
/*     */   }
/*     */ 
/*     */   public void setParentClassAttrIds(List parentClassAttrIds) {
/* 498 */     this.parentClassAttrIds = parentClassAttrIds;
/*     */   }
/*     */ 
/*     */   public void setBmDivideCond(String bmDivideCond) {
/* 502 */     this.bmDivideCond = bmDivideCond;
/*     */   }
/*     */ 
/*     */   public void setRemark(String remark) {
/* 506 */     this.remark = remark;
/*     */   }
/*     */ 
/*     */   public void setUniqueAttrIds(Map<String, String[]> uniqueAttrIds) {
/* 510 */     this.uniqueAttrIds = uniqueAttrIds;
/*     */   }
/*     */ 
/*     */   public void setActionAdaptor(IActionAdaptor actionAdaptor) {
/* 514 */     this.actionAdaptor = actionAdaptor;
/*     */   }
/*     */ 
/*     */   public void setObjectValidator(IObjectValidator objectValidator) {
/* 518 */     this.objectValidator = objectValidator;
/*     */   }
/*     */ 
/*     */   public void setIsDimension(Boolean isDimension) {
/* 522 */     this.isDimension = isDimension;
/*     */   }
/*     */ 
/*     */   public void setIsShowAddBtn(Boolean isShowAddBtn) {
/* 526 */     this.isShowAddBtn = isShowAddBtn;
/*     */   }
/*     */ 
/*     */   public void setIsShowDeleteBtn(Boolean isShowDeleteBtn) {
/* 530 */     this.isShowDeleteBtn = isShowDeleteBtn;
/*     */   }
/*     */ 
/*     */   public void setIsShowEditBtn(Boolean isShowEditBtn) {
/* 534 */     this.isShowEditBtn = isShowEditBtn;
/*     */   }
/*     */ 
/*     */   public void setActionAdaptorClassName(String actionAdaptorClassName) {
/*     */     try {
/* 539 */       if (actionAdaptorClassName != null)
/* 540 */         this.actionAdaptor = ((IActionAdaptor)Class.forName(actionAdaptorClassName).newInstance());
/*     */     }
/*     */     catch (Exception ex) {
/* 543 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addAttrMeta(BMAttrMeta attrMeta) {
/* 548 */     this.attrMetas.put(attrMeta.getAttrId(), attrMeta);
/*     */   }
/*     */ 
/*     */   public void addUniqueAttrIds(String uniqueName, String[] attrIds) {
/* 552 */     this.uniqueAttrIds.put(uniqueName, attrIds);
/*     */   }
/*     */ 
/*     */   public void addLabelAttrId(String attrId) {
/* 556 */     this.constructLabelAttrIds.add(attrId);
/*     */   }
/*     */ 
/*     */   public List<String> getAllAttrIds() {
/* 560 */     List enumNotNullAttrIds = new ArrayList();
/* 561 */     List relationNotNullAttrIds = new ArrayList();
/* 562 */     List enumNullAttrIds = new ArrayList();
/* 563 */     List relationNullAttrIds = new ArrayList();
/* 564 */     List systemAttrIds = new ArrayList();
/* 565 */     List miscAttrIds = new ArrayList();
/*     */ 
/* 567 */     for (BMAttrMeta attrMeta : this.attrMetas.values()) {
/* 568 */       if (this.constructLabelAttrIds.contains(attrMeta.getAttrId()))
/*     */         continue;
/* 570 */       if ((attrMeta.getIsRelation().booleanValue()) && (attrMeta.getIsNotNull().booleanValue()))
/* 571 */         relationNotNullAttrIds.add(attrMeta.getAttrId());
/* 572 */       else if ((attrMeta.getIsEnumAttr().booleanValue()) && (attrMeta.getIsNotNull().booleanValue()))
/* 573 */         enumNotNullAttrIds.add(attrMeta.getAttrId());
/* 574 */       else if ((attrMeta.getIsRelation().booleanValue()) && (!attrMeta.getIsNotNull().booleanValue()))
/* 575 */         relationNullAttrIds.add(attrMeta.getAttrId());
/* 576 */       else if ((attrMeta.getIsEnumAttr().booleanValue()) && (!attrMeta.getIsNotNull().booleanValue()))
/* 577 */         enumNullAttrIds.add(attrMeta.getAttrId());
/* 578 */       else if (attrMeta.getIsSystem().booleanValue())
/* 579 */         systemAttrIds.add(attrMeta.getAttrId());
/*     */       else {
/* 581 */         miscAttrIds.add(attrMeta.getAttrId());
/*     */       }
/*     */     }
/*     */ 
/* 585 */     List sortAttrIds = new ArrayList();
/* 586 */     sortAttrIds.addAll(this.constructLabelAttrIds);
/* 587 */     sortAttrIds.addAll(relationNotNullAttrIds);
/* 588 */     sortAttrIds.addAll(enumNotNullAttrIds);
/* 589 */     sortAttrIds.addAll(relationNullAttrIds);
/* 590 */     sortAttrIds.addAll(enumNullAttrIds);
/* 591 */     sortAttrIds.addAll(miscAttrIds);
/* 592 */     sortAttrIds.addAll(systemAttrIds);
/* 593 */     return sortAttrIds;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getSpaceAttrs() {
/* 597 */     Map spaceAttrIds = new HashMap();
/* 598 */     Iterator it = this.attrMetas.values().iterator();
/* 599 */     while (it.hasNext()) {
/* 600 */       BMAttrMeta attrMeta = (BMAttrMeta)it.next();
/* 601 */       if (attrMeta.getIsRelation().booleanValue()) {
/* 602 */         Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 603 */         Iterator i = relatedAttrIds.keySet().iterator();
/* 604 */         while (i.hasNext()) {
/* 605 */           String relatedBmClassId = (String)i.next();
/* 606 */           String relatedAttrId = (String)relatedAttrIds.get(relatedBmClassId);
/* 607 */           if (("DISTRICT".equals(relatedBmClassId)) && ("CUID".equals(relatedAttrId)))
/* 608 */             spaceAttrIds.put("DISTRICT", attrMeta.getAttrId());
/* 609 */           else if (("SITE".equals(relatedBmClassId)) && ("CUID".equals(relatedAttrId)))
/* 610 */             spaceAttrIds.put("SITE", attrMeta.getAttrId());
/* 611 */           else if (("ROOM".equals(relatedBmClassId)) && ("CUID".equals(relatedAttrId))) {
/* 612 */             spaceAttrIds.put("ROOM", attrMeta.getAttrId());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 617 */     return spaceAttrIds;
/*     */   }
/*     */ 
/*     */   public List<BMAttrMeta> getRelatedClassAttrMeta(String relatedBmClassId)
/*     */   {
/* 624 */     return (List)this.relatedClassAttrIds.get(relatedBmClassId);
/*     */   }
/*     */ 
/*     */   public String getDisplayLabel(IDrmDataObject dbo) {
/* 628 */     String label = "";
/* 629 */     List labelAttrIds = getConstructLabelAttrIds();
/* 630 */     if ((dbo != null) && (labelAttrIds.size() > 0)) {
/* 631 */       for (int i = 0; i < labelAttrIds.size(); i++) {
/* 632 */         String val = dbo.getAttrValue((String)labelAttrIds.get(i)) + "";
/* 633 */         if (label.length() > 0) {
/* 634 */           label = label + getLabelJoinChar();
/*     */         }
/* 636 */         if (val != null) {
/* 637 */           label = label + val;
/*     */         }
/*     */       }
/*     */     }
/* 641 */     return label;
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getDisplayLabelDbo(IDrmDataObject dbo) {
/* 645 */     List constructAttrIds = getConstructLabelAttrIds();
/* 646 */     IDrmDataObject _dbo = DrmEntityFactory.getInstance().createDataObject();
/* 647 */     _dbo.setDboId(dbo.getDboId());
/* 648 */     _dbo.setBmClassId(dbo.getBmClassId());
/* 649 */     _dbo.setCuid(dbo.getCuid());
/* 650 */     _dbo.setDbClassId(dbo.getDbClassId());
/* 651 */     for (String constructAttrId : constructAttrIds) {
/* 652 */       _dbo.setAttrValue(constructAttrId, dbo.getAttrValue(constructAttrId));
/*     */     }
/* 654 */     return _dbo;
/*     */   }
/*     */ 
/*     */   public void convertGenericDO(GenericDO dbo, IDrmDataObject drmDataObject, boolean defaultValue) {
/* 658 */     IDrmDataObject dro = convertDrmDataObject(drmDataObject, defaultValue);
/* 659 */     dbo.setAttrValues(dro.getAllAttr());
/* 660 */     if (dro.getAllAttr().get("OBJECTID") != null)
/* 661 */       dbo.setObjectId(dro.getAllAttr().get("OBJECTID").toString());
/*     */   }
/*     */ 
/*     */   public IDrmDataObject convertDrmDataObject(IDrmDataObject drmDataObject, boolean defaultValue)
/*     */   {
/* 666 */     Map attrMetas = getAllAttrMetas();
/* 667 */     Map HaveNotAttrMetas = new HashMap(attrMetas);
/* 668 */     Map allAttrMap = drmDataObject.getAllAttr();
/* 669 */     Iterator i = allAttrMap.keySet().iterator();
/* 670 */     while (i.hasNext()) {
/* 671 */       String attrId = (String)i.next();
/* 672 */       HaveNotAttrMetas.remove(attrId);
/* 673 */       String value = (String)allAttrMap.get(attrId);
/* 674 */       BMAttrMeta attrMeta = (BMAttrMeta)attrMetas.get(attrId);
/* 675 */       Class cls = attrMeta.getAttrClassType();
/* 676 */       if ("".equals(value)) {
/* 677 */         allAttrMap.put(attrId, null);
/*     */       }
/* 679 */       else if (cls == Integer.TYPE) {
/* 680 */         allAttrMap.put(attrId, Integer.valueOf(Integer.valueOf(value).intValue()));
/* 681 */       } else if (cls == Long.TYPE) {
/* 682 */         allAttrMap.put(attrId, Long.valueOf(Long.valueOf(value).longValue()));
/* 683 */       } else if (cls == Float.TYPE) {
/* 684 */         allAttrMap.put(attrId, Float.valueOf(Float.valueOf(value).floatValue()));
/* 685 */       } else if (cls == Double.TYPE) {
/* 686 */         allAttrMap.put(attrId, Double.valueOf(Double.valueOf(value).doubleValue()));
/* 687 */       } else if (cls == Boolean.TYPE) {
/* 688 */         Boolean attrValue = Boolean.FALSE;
/* 689 */         if ((value != null) && ((value.trim().equals("true")) || (value.trim().equals("false")))) {
/* 690 */           attrValue = Boolean.valueOf(value);
/*     */         }
/* 692 */         allAttrMap.put(attrId, attrValue);
/* 693 */       } else if (cls == java.sql.Date.class) {
/* 694 */         allAttrMap.put(attrId, new java.util.Date(Long.parseLong(value)));
/* 695 */       } else if (cls == Timestamp.class) {
/* 696 */         allAttrMap.put(attrId, new Timestamp(Long.parseLong(value)));
/*     */       } else {
/* 698 */         allAttrMap.put(attrId, value);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 704 */     if (defaultValue) {
/* 705 */       i = HaveNotAttrMetas.keySet().iterator();
/*     */ 
/* 707 */       while (i.hasNext()) {
/* 708 */         String attrId = (String)i.next();
/* 709 */         String value = (String)allAttrMap.get(attrId);
/* 710 */         if ((value == null) || (value.trim().length() == 0)) {
/* 711 */           BMAttrMeta attrMeta = (BMAttrMeta)attrMetas.get(attrId);
/* 712 */           if ((attrMeta.getDefaultValue() != null) && (attrMeta.getDefaultValue().trim().length() > 0)) {
/* 713 */             value = attrMeta.getDefaultValue();
/* 714 */             Class cls = attrMeta.getAttrClassType();
/* 715 */             if ("".equals(value)) {
/* 716 */               allAttrMap.put(attrId, null);
/*     */             }
/* 718 */             else if (cls == Integer.TYPE) {
/* 719 */               allAttrMap.put(attrId, Integer.valueOf(Integer.valueOf(value).intValue()));
/* 720 */             } else if (cls == Long.TYPE) {
/* 721 */               allAttrMap.put(attrId, Long.valueOf(Long.valueOf(value).longValue()));
/* 722 */             } else if (cls == Float.TYPE) {
/* 723 */               allAttrMap.put(attrId, Float.valueOf(Float.valueOf(value).floatValue()));
/* 724 */             } else if (cls == Double.TYPE) {
/* 725 */               allAttrMap.put(attrId, Double.valueOf(Double.valueOf(value).doubleValue()));
/* 726 */             } else if (cls == Boolean.TYPE) {
/* 727 */               Boolean attrValue = Boolean.FALSE;
/* 728 */               if ((value != null) && ((value.trim().equals("true")) || (value.trim().equals("false")))) {
/* 729 */                 attrValue = Boolean.valueOf(value);
/*     */               }
/* 731 */               allAttrMap.put(attrId, attrValue);
/* 732 */             } else if (cls == java.sql.Date.class) {
/*     */               try {
/* 734 */                 java.util.Date defaultdate = TimeFormatHelper.convertDate(value, "yyyy-MM-dd");
/* 735 */                 allAttrMap.put(attrId, defaultdate);
/*     */               } catch (Exception ex) {
/* 737 */                 LogHome.getLog().error("bmClassId=" + this.bmClassId + ", attrId=" + attrId + "] 默认值=" + value + ", 设置错误！");
/*     */               }
/* 739 */             } else if (cls == Timestamp.class) {
/*     */               try {
/* 741 */                 java.util.Date defaultdate = TimeFormatHelper.convertDate(value, "yyyy-MM-dd HH:mm:ss");
/* 742 */                 allAttrMap.put(attrId, new Timestamp(defaultdate.getTime()));
/*     */               } catch (Exception ex) {
/* 744 */                 LogHome.getLog().error("bmClassId=" + this.bmClassId + ", attrId=" + attrId + "] 默认值=" + value + ", 设置错误！");
/*     */               }
/*     */             } else {
/* 747 */               allAttrMap.put(attrId, value);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 754 */     return drmDataObject;
/*     */   }
/*     */ 
/*     */   public List<String> getSystemAttrIds() {
/* 758 */     List systemAttrIds = new ArrayList();
/* 759 */     for (BMAttrMeta attrMeta : this.attrMetas.values()) {
/* 760 */       if (attrMeta.getIsSystem().booleanValue()) {
/* 761 */         systemAttrIds.add(attrMeta.getAttrId());
/*     */       }
/*     */     }
/* 764 */     return systemAttrIds;
/*     */   }
/*     */ 
/*     */   public List<String> getBmDivideAttrIds() {
/* 768 */     List divideAttrIds = new ArrayList();
/* 769 */     if (this.bmDivideCond != null) {
/* 770 */       for (String attrId : this.attrMetas.keySet()) {
/* 771 */         if (this.bmDivideCond.indexOf("$" + attrId) >= 0) {
/* 772 */           divideAttrIds.add(attrId);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 777 */     return divideAttrIds;
/*     */   }
/*     */ 
/*     */   private String parseBmDivAttrId(String divExpr) {
/* 781 */     String divAttrId = null;
/* 782 */     for (String _divAttrId : getBmDivideAttrIds()) {
/* 783 */       if (divExpr.indexOf(_divAttrId) >= 0) {
/* 784 */         divAttrId = _divAttrId;
/* 785 */         break;
/*     */       }
/*     */     }
/* 788 */     return divAttrId;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getBmDivideAttrValues() {
/* 792 */     Map bmDivAttrValues = new HashMap();
/* 793 */     if (this.bmDivideCond != null) {
/* 794 */       String _bmDivideCond = this.bmDivideCond.toUpperCase();
/* 795 */       String[] divExprs = _bmDivideCond.split(" AND ");
/* 796 */       for (String divExpr : divExprs) {
/*     */         try {
/* 798 */           String divAttrValue = "";
/* 799 */           String divAttrId = parseBmDivAttrId(divExpr);
/* 800 */           String _divExpr = divExpr.toUpperCase();
/* 801 */           if (_divExpr.indexOf(" IN") > 0) {
/* 802 */             int start = divExpr.indexOf("(");
/* 803 */             int end = divExpr.indexOf(")");
/* 804 */             String sAttrValue = divExpr.substring(start + 1, end);
/* 805 */             String[] sAttrValues = sAttrValue.split(",");
/* 806 */             divAttrValue = "-";
/* 807 */             for (int i = 0; i < sAttrValues.length; i++)
/* 808 */               divAttrValue = divAttrValue + sAttrValues[i].trim() + "-";
/*     */           }
/* 810 */           else if (divExpr.indexOf("=") > 0) {
/* 811 */             String[] sAttrValues = divExpr.split("=");
/* 812 */             divAttrValue = "-" + sAttrValues[1].trim() + "-";
/* 813 */             bmDivAttrValues.put(divAttrId, divAttrValue);
/*     */           }
/* 815 */           bmDivAttrValues.put(divAttrId, divAttrValue);
/*     */         } catch (Exception ex) {
/* 817 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       }
/*     */     }
/* 821 */     return bmDivAttrValues;
/*     */   }
/*     */ 
/*     */   public boolean isChildBmClass() {
/* 825 */     return this.bmDivideCond != null;
/*     */   }
/*     */ 
/*     */   public boolean isDbEntityClass() {
/* 829 */     return this.bmClassId.equals(this.dbClassId);
/*     */   }
/*     */ 
/*     */   public boolean isChildBmObject(IDrmDataObject relatedDbo) {
/* 833 */     String divideCond = getBmDivideCond();
/* 834 */     if (divideCond == null) {
/* 835 */       return false;
/*     */     }
/*     */ 
/* 838 */     boolean isValidExpr = true;
/* 839 */     for (String divideAttrId : getBmDivideAttrIds()) {
/* 840 */       Object attrValue = relatedDbo.getAttrValue(divideAttrId);
/* 841 */       if (attrValue == null) {
/* 842 */         isValidExpr = false;
/* 843 */         break;
/*     */       }
/* 845 */       divideCond = divideCond.replace("$" + divideAttrId, attrValue.toString());
/*     */     }
/*     */ 
/* 848 */     if (!isValidExpr) {
/* 849 */       return false;
/*     */     }
/*     */ 
/* 852 */     divideCond = divideCond.toUpperCase();
/* 853 */     divideCond = divideCond.replace("AND", "&&");
/* 854 */     return isDividedBM(divideCond).booleanValue();
/*     */   }
/*     */ 
/*     */   private static Boolean isDividedBM(String divideCond) {
/* 858 */     Boolean isEqual = Boolean.valueOf(false);
/*     */     try {
/* 860 */       GroovyShell shell = new GroovyShell();
/* 861 */       isEqual = (Boolean)shell.evaluate(divideCond);
/*     */     } catch (Exception ex) {
/* 863 */       LogHome.getLog().error("", ex);
/*     */     }
/* 865 */     return isEqual;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 869 */     return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
/*     */   }
/*     */ 
/*     */   public int compareTo(Object obj) {
/* 873 */     int compare = 0;
/* 874 */     boolean isAscend = true;
/* 875 */     Comparable source = this.labelCn;
/* 876 */     Comparable target = ((BMClassMeta)obj).getLabelCn();
/* 877 */     if ((source != null) && (target != null))
/* 878 */       compare = isAscend ? source.compareTo(target) : target.compareTo(source);
/* 879 */     else if (source != null)
/* 880 */       compare = isAscend ? 1 : -1;
/* 881 */     else if (target != null) {
/* 882 */       compare = isAscend ? -1 : 1;
/*     */     }
/* 884 */     return compare;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getAllRootAttrMap()
/*     */   {
/* 889 */     if (this.allRootAttrMap.size() == 0)
/*     */     {
/* 891 */       Map upperAttrMap = new HashMap();
/* 892 */       List upperAttrList = new ArrayList();
/*     */ 
/* 895 */       List allAttrList = getAllAttrIds();
/* 896 */       for (int i = 0; i < allAttrList.size(); i++) {
/* 897 */         String attrId = (String)allAttrList.get(i);
/* 898 */         BMAttrMeta attrMeta = getAttrMeta(attrId);
/* 899 */         if ((attrMeta != null) && (attrMeta.getUpperAttrIds() != null) && (attrMeta.getUpperAttrIds().size() > 0)) {
/* 900 */           if (!upperAttrMap.containsKey(attrId)) {
/* 901 */             upperAttrMap.put(attrId, attrId);
/* 902 */             upperAttrList.add(attrId);
/*     */           }
/* 904 */           for (int k = 0; k < attrMeta.getUpperAttrIds().size(); k++) {
/* 905 */             String upperAttrId = (String)attrMeta.getUpperAttrIds().get(k);
/* 906 */             if (!upperAttrMap.containsKey(upperAttrId)) {
/* 907 */               upperAttrMap.put(upperAttrId, upperAttrId);
/* 908 */               upperAttrList.add(upperAttrId);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 914 */       Iterator iterator = upperAttrMap.keySet().iterator();
/* 915 */       while (iterator.hasNext()) {
/* 916 */         String attrId = (String)iterator.next();
/* 917 */         BMAttrMeta attrMeta = getAttrMeta(attrId);
/* 918 */         String sRootAttrId = attrMeta.getRootAttr(upperAttrList);
/* 919 */         this.allRootAttrMap.put(attrId, sRootAttrId);
/*     */       }
/*     */     }
/*     */ 
/* 923 */     return this.allRootAttrMap;
/*     */   }
/*     */ 
/*     */   public Boolean getIsDimension() {
/* 927 */     return this.isDimension;
/*     */   }
/*     */ 
/*     */   public Boolean getIsShowEditBtn() {
/* 931 */     return this.isShowEditBtn;
/*     */   }
/*     */ 
/*     */   public Boolean getIsShowDeleteBtn() {
/* 935 */     return this.isShowDeleteBtn;
/*     */   }
/*     */ 
/*     */   public Boolean getIsShowAddBtn() {
/* 939 */     return this.isShowAddBtn;
/*     */   }
/*     */ 
/*     */   public void initBmClassMetaCache()
/*     */   {
/* 947 */     this.relatedClassAttrIds.clear();
/* 948 */     Iterator i = this.attrMetas.keySet().iterator();
/* 949 */     while (i.hasNext()) {
/* 950 */       String attrId = (String)i.next();
/* 951 */       BMAttrMeta attrMeta = (BMAttrMeta)this.attrMetas.get(attrId);
/* 952 */       if ((attrMeta.getRelatedAttrIds() != null) && (attrMeta.getRelatedAttrIds().size() > 0)) {
/* 953 */         String[] bmClassIds = new String[attrMeta.getRelatedAttrIds().size()];
/* 954 */         attrMeta.getRelatedAttrIds().keySet().toArray(bmClassIds);
/* 955 */         for (int k = 0; k < bmClassIds.length; k++) {
/* 956 */           String bmClassId = bmClassIds[k];
/* 957 */           if ("CUID".equals(attrMeta.getRelatedAttrIds().get(bmClassId))) {
/* 958 */             List attrMetas = (List)this.relatedClassAttrIds.get(bmClassId);
/* 959 */             if (attrMetas == null) {
/* 960 */               attrMetas = new ArrayList();
/* 961 */               attrMetas.add(attrMeta);
/* 962 */               this.relatedClassAttrIds.put(bmClassId, attrMetas);
/*     */             } else {
/* 964 */               attrMetas.add(attrMeta);
/*     */             }
/* 966 */             LogHome.getLog().info("初始化" + this.bmClassId + "，关联" + attrMeta.getAttrId() + "->" + bmClassId);
/*     */ 
/* 968 */             if ((attrMeta.getUpperAttrIds() != null) && (attrMeta.getUpperAttrIds().size() > 0)) {
/* 969 */               for (int m = 0; m < attrMeta.getUpperAttrIds().size(); m++) {
/* 970 */                 String upperAttrId = (String)attrMeta.getUpperAttrIds().get(m);
/* 971 */                 ((BMAttrMeta)this.attrMetas.get(upperAttrId)).setHasChildAttrId(Boolean.TRUE);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 978 */     LogHome.getLog().info("初始化" + this.bmClassId + "对象内部缓存！");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.BMClassMeta
 * JD-Core Version:    0.6.0
 */