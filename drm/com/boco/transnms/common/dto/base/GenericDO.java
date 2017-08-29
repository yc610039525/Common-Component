/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.id.CUIDHexGenerator;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericDO extends AbstractDO
/*     */   implements IDrmDataObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private transient DataObjectList dynAttrModel;
/*     */ 
/*     */   public GenericDO()
/*     */   {
/*  56 */     setObjectLoadType(0);
/*     */   }
/*     */ 
/*     */   public GenericDO(String className) {
/*  60 */     setClassName(className);
/*  61 */     setObjectLoadType(0);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, Object value) {
/*  65 */     if ((value != null) && (value.getClass() == String.class)) {
/*  66 */       Class attrClass = getAttrType(attrName);
/*  67 */       if ((attrClass != null) && (attrClass == Long.TYPE))
/*  68 */         super.setAttrValue(attrName, new Long((String)value));
/*  69 */       else if ((attrClass != null) && (attrClass == Boolean.TYPE))
/*  70 */         super.setAttrValue(attrName, new Boolean((String)value));
/*  71 */       else if ((attrClass != null) && (attrClass == Double.TYPE))
/*  72 */         super.setAttrValue(attrName, new Double((String)value));
/*     */       else
/*  74 */         super.setAttrValue(attrName, value);
/*     */     }
/*     */     else {
/*  77 */       super.setAttrValue(attrName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObjectKey(String objectKey) {
/*  82 */     getCoreAttrMap().put("OBJECT_KEY", objectKey);
/*     */   }
/*     */ 
/*     */   public void setCreateTime(Date createTime) {
/*  86 */     getCoreAttrMap().put("CREATE_TIME", createTime);
/*     */   }
/*     */ 
/*     */   public void setLastModifyTime(Date lastModifyTime) {
/*  90 */     getCoreAttrMap().put("LAST_MODIFY_TIME", lastModifyTime);
/*     */   }
/*     */ 
/*     */   public String getObjectKey() {
/*  94 */     return (String)getCoreAttrMap().get("OBJECT_KEY");
/*     */   }
/*     */ 
/*     */   public Date getCreateTime() {
/*  98 */     return (Date)getCoreAttrMap().get("CREATE_TIME");
/*     */   }
/*     */ 
/*     */   public Date getLastModifyTime() {
/* 102 */     return (Date)getCoreAttrMap().get("LAST_MODIFY_TIME");
/*     */   }
/*     */ 
/*     */   public void setCuid(String cuid) {
/* 106 */     if (!isSetClassName()) {
/* 107 */       String className = parseClassNameFromCuid(cuid);
/* 108 */       if (className != null) {
/* 109 */         setClassName(className);
/*     */       }
/*     */     }
/* 112 */     super.setAttrValue("CUID", cuid);
/*     */   }
/*     */ 
/*     */   public void setCuid() {
/* 116 */     String cuid = CUIDHexGenerator.getInstance().generate(getClassName());
/* 117 */     setCuid(cuid);
/*     */   }
/*     */ 
/*     */   public static String parseClassNameFromCuid(String cuid) {
/* 121 */     String className = null;
/* 122 */     if ((cuid != null) && (!cuid.equals(""))) {
/* 123 */       String[] cuids = cuid.split("-");
/* 124 */       if (cuids.length > 1) {
/* 125 */         className = cuids[0];
/*     */       }
/*     */     }
/* 128 */     return className;
/*     */   }
/*     */ 
/*     */   public String getCuid() {
/* 132 */     return super.getAttrString("CUID");
/*     */   }
/*     */ 
/*     */   public void setObjectLoadType(int objectLoadType) {
/* 136 */     getCoreAttrMap().put("OBJECT_LOAD_TYPE", Integer.valueOf(objectLoadType));
/*     */   }
/*     */ 
/*     */   public int getObjectLoadType() {
/* 140 */     return ((Integer)getCoreAttrMap().get("OBJECT_LOAD_TYPE")).intValue();
/*     */   }
/*     */ 
/*     */   public void setGtVersion(long gtVersion) {
/* 144 */     getCoreAttrMap().put("GT_VERSION", Long.valueOf(gtVersion));
/*     */   }
/*     */ 
/*     */   public long getGtVersion() {
/* 148 */     long value = 0L;
/* 149 */     Long _value = (Long)getCoreAttrMap().get("GT_VERSION");
/* 150 */     if (_value != null) {
/* 151 */       value = _value.longValue();
/*     */     }
/* 153 */     return value;
/*     */   }
/*     */ 
/*     */   public Class getAttrType(String attrName) {
/* 157 */     Class attrType = null;
/* 158 */     if (super.containsAttr(attrName)) {
/* 159 */       Object attrValue = super.getAttrValue(attrName);
/* 160 */       attrType = attrValue.getClass();
/*     */     }
/* 162 */     return attrType;
/*     */   }
/*     */ 
/*     */   public String[] getAllAttrNames() {
/* 166 */     String[] attrNames = new String[super.getAllAttr().size()];
/* 167 */     super.getAllAttr().keySet().toArray(attrNames);
/* 168 */     return attrNames;
/*     */   }
/*     */ 
/*     */   public String[] getAllUserAttrNames() {
/* 172 */     return getAllAttrNames();
/*     */   }
/*     */ 
/*     */   public void convAllObjAttrToCuid() {
/* 176 */     Object[] allAttrNames = getAllAttr().keySet().toArray();
/* 177 */     for (int i = 0; i < allAttrNames.length; i++) {
/* 178 */       String attrName = (String)allAttrNames[i];
/* 179 */       Object value = super.getAttrValue(attrName);
/* 180 */       if (((value instanceof GenericDO)) && (attrName.indexOf("CUID") >= 0)) {
/* 181 */         GenericDO dbo = (GenericDO)value;
/* 182 */         super.setAttrValue(attrName, dbo.getCuid());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void convObjAttrToCuid(String[] attrNames) {
/* 188 */     for (int i = 0; i < attrNames.length; i++) {
/* 189 */       String attrName = attrNames[i];
/* 190 */       Object value = super.getAttrValue(attrName);
/* 191 */       if ((value instanceof GenericDO)) {
/* 192 */         GenericDO dbo = (GenericDO)value;
/* 193 */         super.setAttrValue(attrName, dbo.getCuid());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<String, Object> clearUnknowAttrs()
/*     */   {
/* 200 */     Map clearAttrs = new HashMap();
/* 201 */     if (getClass() != GenericDO.class) {
/* 202 */       String[] knownAttrNames = getAllAttrNames();
/* 203 */       ArrayList knownAttrNameList = new ArrayList();
/* 204 */       for (int j = 0; j < knownAttrNames.length; j++) {
/* 205 */         knownAttrNameList.add(knownAttrNames[j]);
/*     */       }
/* 207 */       Object[] allAttrNames = getAllAttr().keySet().toArray();
/* 208 */       for (int i = 0; i < allAttrNames.length; i++) {
/* 209 */         String attrName = (String)allAttrNames[i];
/* 210 */         if ((!knownAttrNameList.contains(attrName)) && ((attrName == null) || (!attrName.equals("GT_VERSION")))) {
/* 211 */           Object value = getAllAttr().remove(attrName);
/* 212 */           clearAttrs.put(attrName, value);
/*     */         }
/*     */       }
/*     */     }
/* 216 */     return clearAttrs;
/*     */   }
/*     */ 
/*     */   public boolean isSetClassName() {
/* 220 */     boolean isSetClassName = true;
/* 221 */     if ((getClassName() == null) || (getClassName().trim().length() == 0) || (GenericDO.class.getSimpleName().equals(getClassName())))
/*     */     {
/* 223 */       isSetClassName = false;
/*     */     }
/* 225 */     return isSetClassName;
/*     */   }
/*     */ 
/*     */   public GenericDO createEmptyDO() {
/* 229 */     GenericDO emptyDO = null;
/*     */     try {
/* 231 */       if (getClass() == GenericDO.class)
/* 232 */         emptyDO = new GenericDO();
/*     */       else
/* 234 */         emptyDO = (GenericDO)getClass().newInstance();
/*     */     }
/*     */     catch (Exception ex) {
/* 237 */       LogHome.getLog().error("", ex);
/*     */     }
/* 239 */     return emptyDO;
/*     */   }
/*     */ 
/*     */   public GenericDO createInstanceByClassName() {
/* 243 */     GenericDO newInstance = null;
/* 244 */     if (isSetClassName())
/*     */       try {
/* 246 */         String[] splits = getClassName().split("_");
/* 247 */         String className = "";
/* 248 */         for (int i = 0; i < splits.length; i++) {
/* 249 */           String split = splits[i];
/* 250 */           className = className + split.substring(0, 1).toUpperCase() + split.substring(1, split.length()).toLowerCase();
/*     */         }
/* 252 */         className = "com.boco.transnms.common.dto." + className;
/* 253 */         newInstance = (GenericDO)Class.forName(className).newInstance();
/*     */       } catch (Exception ex) {
/* 255 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     else {
/* 258 */       newInstance = new GenericDO(getClassName());
/*     */     }
/* 260 */     return newInstance;
/*     */   }
/*     */ 
/*     */   public GenericDO cloneByClassName() {
/* 264 */     GenericDO cloned = createInstanceByClassName();
/* 265 */     super.copyTo(cloned);
/* 266 */     return cloned;
/*     */   }
/*     */ 
/*     */   public GenericDO cloneDboClass() {
/* 270 */     GenericDO cloned = null;
/*     */     try {
/* 272 */       if (getClass() == GenericDO.class) {
/* 273 */         cloned = new GenericDO();
/* 274 */         cloned.setClassName(getClassName());
/*     */       } else {
/* 276 */         cloned = (GenericDO)getClass().newInstance();
/*     */       }
/*     */     } catch (Exception ex) {
/* 279 */       LogHome.getLog().error("", ex);
/*     */     }
/* 281 */     return cloned;
/*     */   }
/*     */ 
/*     */   public void setDynAttrModel(DataObjectList models) {
/* 285 */     this.dynAttrModel = models;
/*     */   }
/*     */ 
/*     */   public DataObjectList getDynAttrModel() {
/* 289 */     return this.dynAttrModel;
/*     */   }
/*     */ 
/*     */   public GenericDO cloneGenericDO() {
/* 293 */     GenericDO cloned = new GenericDO(getClassName());
/* 294 */     super.copyTo(cloned);
/* 295 */     return cloned;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 299 */     StringBuffer str = new StringBuffer();
/* 300 */     str.append(getClassName() + "[");
/*     */ 
/* 302 */     if (getCoreAttrMap().containsKey("OBJECTID")) {
/* 303 */       str.append("objectId=" + getObjectNum());
/*     */     }
/*     */ 
/* 307 */     if (getObjectKey() != null) {
/* 308 */       str.append(", objectKey=" + getObjectKey());
/*     */     }
/*     */ 
/* 311 */     if (getCreateTime() != null) {
/* 312 */       str.append(", createTime=" + TimeFormatHelper.getFormatDate(getCreateTime(), "yyyy-MM-dd HH:mm:ss") + ", ");
/*     */     }
/*     */ 
/* 316 */     if (getLastModifyTime() != null) {
/* 317 */       str.append(", lastModifyTime=" + TimeFormatHelper.getFormatDate(getLastModifyTime(), "yyyy-MM-dd HH:mm:ss") + ", ");
/*     */     }
/*     */ 
/* 321 */     String[] attrNames = super.getAttrNames();
/* 322 */     for (int i = 0; i < attrNames.length; i++) {
/* 323 */       Object value = getAttrValue(attrNames[i]);
/* 324 */       if (value != null) {
/* 325 */         String objClassName = value.getClass().getSimpleName();
/* 326 */         str.append(", " + attrNames[i] + "<" + objClassName + ">=" + value);
/*     */       }
/*     */     }
/* 329 */     str.append("]");
/* 330 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public String getBmClassId()
/*     */   {
/* 335 */     String bmClassId = (String)super.getCoreAttrMap().get("BM_CLASS_ID");
/* 336 */     return bmClassId == null ? getDbClassId() : bmClassId;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 340 */     super.getCoreAttrMap().put("BM_CLASS_ID", bmClassId);
/*     */   }
/*     */ 
/*     */   public String getDbClassId() {
/* 344 */     return super.getClassName();
/*     */   }
/*     */ 
/*     */   public void setDbClassId(String dbClassId) {
/* 348 */     super.setClassName(dbClassId);
/*     */   }
/*     */ 
/*     */   public Long getDboId() {
/* 352 */     return Long.valueOf(super.getObjectNum());
/*     */   }
/*     */ 
/*     */   public void setDboId(Long dboId) {
/* 356 */     if (dboId == null)
/* 357 */       getCoreAttrMap().put("OBJECTID", new Long(0L));
/*     */     else
/* 359 */       super.setObjectNum(dboId.longValue());
/*     */   }
/*     */ 
/*     */   public String[] getAllAttrId()
/*     */   {
/* 364 */     return super.getAttrNames();
/*     */   }
/*     */ 
/*     */   public void setDisplayLabel(String value) {
/* 368 */     super.setAttrValue("DRM_DISPLAY_LABEL", value);
/*     */   }
/*     */ 
/*     */   public String getDisplayLabel() {
/* 372 */     return super.getAttrString("DRM_DISPLAY_LABEL");
/*     */   }
/*     */ 
/*     */   public static class AttrName
/*     */   {
/*     */     public static final String cuid = "CUID";
/*     */     public static final String objectKey = "OBJECT_KEY";
/*     */     public static final String createTime = "CREATE_TIME";
/*     */     public static final String lastModifyTime = "LAST_MODIFY_TIME";
/*     */     public static final String objectLoadType = "OBJECT_LOAD_TYPE";
/*     */     public static final String labelCn = "LABEL_CN";
/*     */     public static final String dynAttrModel = "DYN_ATTR_MODEL";
/*     */     public static final String bmClassId = "BM_CLASS_ID";
/*     */     public static final String displayLabel = "DRM_DISPLAY_LABEL";
/*     */     public static final String gtVersion = "GT_VERSION";
/*     */   }
/*     */ 
/*     */   public static class ObjectLoadType
/*     */   {
/*     */     public static final int UNKNOWN = 0;
/*     */     public static final int MINI = 1;
/*     */     public static final int SIMPLE = 2;
/*     */     public static final int FULL = 3;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.GenericDO
 * JD-Core Version:    0.6.0
 */