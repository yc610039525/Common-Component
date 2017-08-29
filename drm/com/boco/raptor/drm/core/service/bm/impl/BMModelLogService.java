/*     */ package com.boco.raptor.drm.core.service.bm.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*     */ import com.boco.raptor.drm.core.meta.ICompareMetaResult;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.transnms.common.dto.DrmModelLogDetail;
/*     */ import com.boco.transnms.common.dto.DrmModelLogIndex;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BMModelLogService
/*     */ {
/*     */   public DrmModelLogIndex getDbLastModelLogIndex()
/*     */     throws UserException
/*     */   {
/*  73 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  74 */     queryContext.setOrderField("CHANGE_TIME");
/*  75 */     queryContext.setFetchSize(1);
/*  76 */     queryContext.setCountBeforQuery(false);
/*  77 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  78 */     query.setBmClassId("DRM_MODEL_LOG_INDEX");
/*  79 */     DrmModelLogIndex dbo = new DrmModelLogIndex();
/*  80 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, query, dbo);
/*  81 */     DrmModelLogIndex modelLogIndex = null;
/*  82 */     if (rs.getResultSet().size() > 0) {
/*  83 */       modelLogIndex = (DrmModelLogIndex)((IDrmQueryRow)rs.getResultSet().get(0)).getResultDbo("DRM_MODEL_LOG_INDEX");
/*     */     }
/*  85 */     return modelLogIndex;
/*     */   }
/*     */ 
/*     */   public DrmModelLogIndex getFileModelLogIndex(String changeName, String remark, File gmFile, File bmFile) {
/*  89 */     DrmModelLogIndex modelLogIndex = new DrmModelLogIndex();
/*     */ 
/*  91 */     modelLogIndex.setChangeName(changeName + "<" + TimeFormatHelper.getFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss") + ">");
/*  92 */     modelLogIndex.setRemark(remark);
/*  93 */     modelLogIndex.setChangeTime(new Timestamp(System.currentTimeMillis()));
/*  94 */     modelLogIndex.setGoatFileSize(gmFile.length());
/*  95 */     modelLogIndex.setBmFileSize(bmFile.length());
/*  96 */     modelLogIndex.setGoatFileTime(new Timestamp(gmFile.lastModified()));
/*  97 */     modelLogIndex.setBmFileTime(new Timestamp(bmFile.lastModified()));
/*     */ 
/*  99 */     return modelLogIndex;
/*     */   }
/*     */ 
/*     */   public DrmModelLogIndex createFileModelLogIndex(String changeName, String remark, File gmFile, File bmFile) {
/* 103 */     DrmModelLogIndex modelLogIndex = getFileModelLogIndex(changeName, remark, gmFile, bmFile);
/* 104 */     getDynResManageService().addDynObject(ServiceHelper.createSvActCxt(), modelLogIndex, false);
/* 105 */     return modelLogIndex;
/*     */   }
/*     */ 
/*     */   public DrmModelLogIndex getModelLogDetail(ICompareMetaResult compResult) {
/* 109 */     DrmModelLogIndex modelLogIndex = new DrmModelLogIndex();
/* 110 */     modelLogIndex.setAttrValue("CLASS_LOG_DETAIL", getClassModelLogDetails(compResult));
/* 111 */     modelLogIndex.setAttrValue("ATTR_LOG_DETAIL", getAttrModelLogDetails(compResult));
/* 112 */     modelLogIndex.setAttrValue("ENUM_LOG_DETAIL", getEnumModelLogDetails(compResult));
/* 113 */     return modelLogIndex;
/*     */   }
/*     */ 
/*     */   public void createModelLogDetail(DrmModelLogIndex modelLogIndex, ICompareMetaResult compResult) {
/* 117 */     List modelDetails = new ArrayList();
/* 118 */     modelDetails.addAll(getClassModelLogDetails(compResult));
/* 119 */     modelDetails.addAll(getAttrModelLogDetails(compResult));
/* 120 */     modelDetails.addAll(getEnumModelLogDetails(compResult));
/*     */ 
/* 122 */     for (IDrmDataObject _md : modelDetails) {
/* 123 */       DrmModelLogDetail md = (DrmModelLogDetail)_md;
/*     */ 
/* 125 */       String remark = getValueSubString(md.getRemark(), 1200);
/* 126 */       String firstName = getValueSubString(md.getFirstItemName(), 64);
/* 127 */       String secondName = getValueSubString(md.getSecondItemName(), 64);
/* 128 */       md.setFirstItemName(firstName);
/* 129 */       md.setSecondItemName(secondName);
/* 130 */       md.setRemark(remark);
/*     */ 
/* 135 */       md.setRelatedLogIndexCuid(modelLogIndex.getCuid());
/*     */     }
/* 137 */     getDynResManageService().addDynObjects(ServiceHelper.createSvActCxt(), modelDetails, false);
/*     */   }
/*     */ 
/*     */   private List<DrmModelLogDetail> getClassModelLogDetails(ICompareMetaResult compResult) {
/* 141 */     List modelDetails = new ArrayList();
/* 142 */     List addClassMetas = compResult.getAddedClassMetas();
/* 143 */     for (BMClassMeta addClassMeta : addClassMetas) {
/* 144 */       DrmModelLogDetail md = new DrmModelLogDetail();
/* 145 */       md.setChangeType(1L);
/* 146 */       md.setItemType(1L);
/* 147 */       md.setFirstItemName(addClassMeta.getLabelCn() + "(" + addClassMeta.getBmClassId() + ")");
/* 148 */       modelDetails.add(md);
/*     */     }
/*     */ 
/* 151 */     List removeClassIds = compResult.getRemovedClassIds();
/* 152 */     for (String removeBmClassId : removeClassIds) {
/* 153 */       String removeBmClassLabelCn = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), removeBmClassId).getLabelCn();
/* 154 */       DrmModelLogDetail md = new DrmModelLogDetail();
/* 155 */       md.setChangeType(2L);
/* 156 */       md.setItemType(1L);
/* 157 */       md.setFirstItemName(removeBmClassLabelCn + "(" + removeBmClassId + ")");
/* 158 */       modelDetails.add(md);
/*     */     }
/*     */ 
/* 161 */     List updateClassIds = compResult.getUpdatedClassIds();
/* 162 */     for (String updateBmClassId : updateClassIds) {
/* 163 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), updateBmClassId);
/* 164 */       Map bmClassEles = compResult.getUpdatedClassEles(updateBmClassId);
/*     */ 
/* 166 */       Iterator it = bmClassEles.keySet().iterator();
/* 167 */       while (it.hasNext()) {
/* 168 */         String classPropName = (String)it.next();
/* 169 */         Object classPropValue = bmClassEles.get(classPropName);
/*     */ 
/* 171 */         DrmModelLogDetail md = new DrmModelLogDetail();
/* 172 */         md.setChangeType(3L);
/* 173 */         md.setItemType(1L);
/* 174 */         md.setFirstItemName(classMeta.getLabelCn() + "(" + updateBmClassId + ")");
/*     */ 
/* 177 */         md.setSecondItemName(classPropName);
/* 178 */         md.setRemark("oldValue=" + getPropertyValue(classMeta, classPropName) + ", newValue=" + propValue2Str(classPropValue));
/*     */ 
/* 184 */         modelDetails.add(md);
/*     */       }
/*     */     }
/*     */ 
/* 188 */     return modelDetails;
/*     */   }
/*     */ 
/*     */   private List<DrmModelLogDetail> getAttrModelLogDetails(ICompareMetaResult compResult)
/*     */   {
/* 193 */     List modelDetails = new ArrayList();
/* 194 */     List addAttrMetas = compResult.getAddedAttrMetas();
/* 195 */     for (BMAttrMeta addAttrMeta : addAttrMetas) {
/* 196 */       DrmModelLogDetail md = new DrmModelLogDetail();
/* 197 */       md.setChangeType(1L);
/* 198 */       md.setItemType(2L);
/* 199 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), addAttrMeta.getBmClassId());
/* 200 */       md.setFirstItemName(classMeta.getLabelCn() + "(" + addAttrMeta.getBmClassId() + ")");
/* 201 */       md.setSecondItemName(addAttrMeta.getLabelCn() + "(" + addAttrMeta.getAttrId() + ")");
/* 202 */       modelDetails.add(md);
/*     */     }
/*     */ 
/* 205 */     List removeClsAttrIds = compResult.getRemovedAttrIds();
/* 206 */     for (String removeClsAttrId : removeClsAttrIds) {
/* 207 */       String[] _removeClsAttrId = removeClsAttrId.split("\\.");
/* 208 */       String bmClassId = _removeClsAttrId[0];
/* 209 */       String attrId = _removeClsAttrId[1];
/*     */ 
/* 211 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 212 */       BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), bmClassId, attrId);
/*     */ 
/* 214 */       DrmModelLogDetail md = new DrmModelLogDetail();
/* 215 */       md.setChangeType(2L);
/* 216 */       md.setItemType(2L);
/* 217 */       md.setFirstItemName(classMeta.getLabelCn() + "(" + classMeta.getBmClassId() + ")");
/* 218 */       md.setSecondItemName(attrMeta.getLabelCn() + "(" + attrMeta.getAttrId() + ")");
/* 219 */       modelDetails.add(md);
/*     */     }
/*     */ 
/* 222 */     List updateClsAttrIds = compResult.getUpdatedAttrIds();
/* 223 */     for (String updateClsAttrId : updateClsAttrIds) {
/* 224 */       String[] _classAttrId = updateClsAttrId.split("\\.");
/* 225 */       String bmClassId = _classAttrId[0];
/* 226 */       String attrId = _classAttrId[1];
/*     */ 
/* 228 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 229 */       BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), bmClassId, attrId);
/*     */ 
/* 231 */       Map updateAttrEles = compResult.getUpdatedAttrEles(bmClassId, attrId);
/* 232 */       Iterator it = updateAttrEles.keySet().iterator();
/* 233 */       while (it.hasNext()) {
/* 234 */         String attrPropName = (String)it.next();
/* 235 */         Object attrPropValue = updateAttrEles.get(attrPropName);
/*     */ 
/* 237 */         DrmModelLogDetail md = new DrmModelLogDetail();
/* 238 */         md.setChangeType(3L);
/* 239 */         md.setItemType(2L);
/* 240 */         md.setFirstItemName(classMeta.getLabelCn() + "(" + classMeta.getBmClassId() + ")");
/* 241 */         if (attrMeta != null)
/* 242 */           md.setSecondItemName(attrMeta.getLabelCn() + "(" + attrMeta.getAttrId() + ")");
/*     */         else {
/* 244 */           md.setSecondItemName("(" + attrId + ")");
/*     */         }
/* 246 */         md.setThirdItemName(attrPropName);
/*     */ 
/* 248 */         md.setRemark("oldValue=" + getPropertyValue(attrMeta, attrPropName) + ", " + "newValue=" + propValue2Str(attrPropValue));
/*     */ 
/* 252 */         modelDetails.add(md);
/*     */       }
/*     */     }
/*     */ 
/* 256 */     return modelDetails;
/*     */   }
/*     */ 
/*     */   private String getValueSubString(String Value, int getStringLength)
/*     */   {
/* 261 */     int getValueLength = 0;
/* 262 */     if ((Value == null) || (Value.length() == 0)) {
/* 263 */       return null;
/*     */     }
/* 265 */     if (Value.getBytes().length > getStringLength)
/* 266 */       getValueLength = getStringLength / 2;
/*     */     else {
/* 268 */       getValueLength = Value.length();
/*     */     }
/* 270 */     return Value.substring(0, getValueLength);
/*     */   }
/*     */ 
/*     */   private List<DrmModelLogDetail> getEnumModelLogDetails(ICompareMetaResult compResult) {
/* 274 */     List modelDetails = new ArrayList();
/* 275 */     List addedEnumMetas = compResult.getAddedEnums();
/* 276 */     setEnumModelLog(addedEnumMetas, 1, modelDetails);
/*     */ 
/* 278 */     List removedEnumMetas = compResult.getRemovedEnums();
/* 279 */     setEnumModelLog(removedEnumMetas, 2, modelDetails);
/*     */ 
/* 281 */     List changeEnumMetas = compResult.getUpdatedEnums();
/* 282 */     setEnumModelLog(changeEnumMetas, 3, modelDetails);
/*     */ 
/* 284 */     return modelDetails;
/*     */   }
/*     */ 
/*     */   private void setEnumModelLog(List<BMEnumMeta> changeEnumMetas, int changeType, List<DrmModelLogDetail> modelDetails)
/*     */   {
/* 289 */     for (Iterator i$ = changeEnumMetas.iterator(); i$.hasNext(); ) { changeEnumMeta = (BMEnumMeta)i$.next();
/* 290 */       List enums = changeEnumMeta.getEnums();
/* 291 */       for (IDrmEnumValue _enum : enums) {
/* 292 */         DrmModelLogDetail md = new DrmModelLogDetail();
/* 293 */         md.setChangeType(changeType);
/* 294 */         md.setItemType(3L);
/* 295 */         md.setFirstItemName(changeEnumMeta.getEnumLabelCn() + "(" + changeEnumMeta.getEnumId() + ")");
/* 296 */         md.setSecondItemName(_enum.getEnumName());
/* 297 */         md.setRemark("value=" + _enum.getEnumValue());
/* 298 */         modelDetails.add(md);
/*     */       } }
/*     */     BMEnumMeta changeEnumMeta;
/*     */   }
/*     */ 
/*     */   private static String getPropertyValue(Object bean, String name) {
/* 304 */     if ((bean == null) || (name == null) || (name.length() == 0)) return "";
/* 305 */     String methodName = "";
/* 306 */     if (name.equals("labelAttrId"))
/* 307 */       methodName = "_get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
/*     */     else {
/* 309 */       methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
/*     */     }
/* 311 */     String value = "";
/*     */     try {
/* 313 */       Object result = bean.getClass().getMethod(methodName, new Class[0]).invoke(bean, new Object[0]);
/* 314 */       value = propValue2Str(result);
/*     */     } catch (Exception ex) {
/* 316 */       LogHome.getLog().error("", ex);
/*     */     }
/* 318 */     return value;
/*     */   }
/*     */ 
/*     */   private static String propValue2Str(Object attrValue) {
/* 322 */     String value = "";
/* 323 */     if (attrValue == null) {
/* 324 */       value = "null";
/* 325 */     } else if ((attrValue instanceof List)) {
/* 326 */       value = value + "[";
/* 327 */       for (Iterator i$ = ((List)attrValue).iterator(); i$.hasNext(); ) { Object l = i$.next();
/* 328 */         value = value + l + ", ";
/*     */       }
/* 330 */       value = value + "]";
/* 331 */     } else if ((attrValue instanceof Map)) {
/* 332 */       value = value + "[";
/* 333 */       Map _result = (Map)attrValue;
/* 334 */       for (Iterator i$ = _result.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 335 */         value = value + key + "=" + propValue2Str(_result.get(key)) + ",";
/*     */       }
/* 337 */       value = value + "]";
/* 338 */     } else if (attrValue.getClass().isArray()) {
/* 339 */       value = value + "[";
/* 340 */       for (int i = 0; i < Array.getLength(attrValue); i++) {
/* 341 */         value = value + propValue2Str(Array.get(attrValue, i)) + ",";
/*     */       }
/* 343 */       value = value + "]";
/*     */     } else {
/* 345 */       value = value + attrValue;
/*     */     }
/* 347 */     return value;
/*     */   }
/*     */ 
/*     */   private static IDynResManageService getDynResManageService() {
/* 351 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService() {
/* 355 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static class LogItemTypeAttrName
/*     */   {
/*     */     public static final String CLASS_LOG_DETAIL = "CLASS_LOG_DETAIL";
/*     */     public static final String ATTR_LOG_DETAIL = "ATTR_LOG_DETAIL";
/*     */     public static final String ENUM_LOG_DETAIL = "ENUM_LOG_DETAIL";
/*     */   }
/*     */ 
/*     */   public static class LogItemType
/*     */   {
/*     */     public static final int CLASS = 1;
/*     */     public static final int ATTR = 2;
/*     */     public static final int ENUM = 3;
/*     */   }
/*     */ 
/*     */   public static class LogChangeType
/*     */   {
/*     */     public static final int ADD = 1;
/*     */     public static final int REMOVE = 2;
/*     */     public static final int UPDATE = 3;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.bm.impl.BMModelLogService
 * JD-Core Version:    0.6.0
 */