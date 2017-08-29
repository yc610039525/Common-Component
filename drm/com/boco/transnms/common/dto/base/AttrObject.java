/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.BufInputStream;
/*     */ import com.boco.common.util.io.BufOutputStream;
/*     */ import com.boco.common.util.io.FileHelper;
/*     */ import com.boco.common.util.sec.AESCryptoUtil;
/*     */ import java.beans.XMLDecoder;
/*     */ import java.beans.XMLEncoder;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.beanutils.BeanUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AttrObject
/*     */   implements IAttrObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  51 */   private final Map<String, Object> defalutAttrMap = new HashMap();
/*     */ 
/*  53 */   private final Map<String, Serializable> extendAttrMap = new AttrMap(this);
/*  54 */   private final Map<String, Serializable> coreAttrMap = new AttrMap(this);
/*  55 */   private boolean readable = false;
/*     */ 
/*     */   public AttrObject() {
/*  58 */     setClassName(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public AttrObject(String className) {
/*  62 */     setClassName(className);
/*     */   }
/*     */ 
/*     */   public String getClassName() {
/*  66 */     return (String)getCoreAttrMap().get("className");
/*     */   }
/*     */ 
/*     */   public void setClassName(String className) {
/*  70 */     getCoreAttrMap().put("className", className);
/*     */   }
/*     */ 
/*     */   public String[] getAttrNames() {
/*  74 */     String[] attrNames = new String[this.extendAttrMap.size()];
/*  75 */     this.extendAttrMap.keySet().toArray(attrNames);
/*  76 */     return attrNames;
/*     */   }
/*     */ 
/*     */   public List getAttrNameList() {
/*  80 */     List nameList = new ArrayList();
/*  81 */     String[] attrNames = new String[this.extendAttrMap.size()];
/*  82 */     this.extendAttrMap.keySet().toArray(attrNames);
/*  83 */     for (int i = 0; i < attrNames.length; i++) {
/*  84 */       nameList.add(attrNames[i]);
/*     */     }
/*  86 */     return nameList;
/*     */   }
/*     */ 
/*     */   public void clearDefaultValue() {
/*  90 */     Iterator i = this.defalutAttrMap.keySet().iterator();
/*  91 */     while (i.hasNext()) {
/*  92 */       String attrName = (String)i.next();
/*  93 */       this.extendAttrMap.remove(attrName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map getAllAttr() {
/*  97 */     return this.extendAttrMap;
/*     */   }
/*     */ 
/*     */   public void setDefaultAttrValue(String attrName, Object value) {
/* 101 */     this.defalutAttrMap.put(attrName, value);
/*     */   }
/*     */ 
/*     */   public void removeDefaultAttrValue(String attrName) {
/* 105 */     this.defalutAttrMap.remove(attrName);
/*     */   }
/*     */ 
/*     */   protected Map getCoreAttrMap() {
/* 109 */     return this.coreAttrMap;
/*     */   }
/*     */ 
/*     */   public Map getObjectToMap() {
/* 113 */     Map mp = new HashMap();
/* 114 */     mp.putAll(this.coreAttrMap);
/* 115 */     mp.putAll(this.extendAttrMap);
/* 116 */     return mp;
/*     */   }
/*     */ 
/*     */   public Object removeAttr(String attrName) {
/* 120 */     return this.extendAttrMap.remove(attrName);
/*     */   }
/*     */ 
/*     */   public boolean containsAttr(String attrName) {
/* 124 */     return this.extendAttrMap.containsKey(attrName);
/*     */   }
/*     */ 
/*     */   public <T> T getAttrValueT(String attrName) {
/* 128 */     return this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public Object getAttrValue(String attrName) {
/* 132 */     Object attrValue = this.extendAttrMap.get(attrName);
/*     */     try {
/* 134 */       String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 135 */       if ((secPasskey != null) && (attrValue != null) && (!attrValue.equals(""))) {
/* 136 */         String[] myValues = ((String)attrValue).split("-");
/* 137 */         if ((myValues.length == 2) && (myValues[1].indexOf("SK") == 0))
/* 138 */           attrValue = myValues[0] + "-" + AESCryptoUtil.decrypt(secPasskey, myValues[1].substring(2));
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 142 */       throw new UserException("属性[attrName=" + attrName + ", 解密失败]");
/*     */     }
/* 144 */     return attrValue;
/*     */   }
/*     */ 
/*     */   public String getSecValue(String attrName, String value)
/*     */   {
/* 150 */     String attrValue = value;
/*     */     try {
/* 152 */       String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 153 */       if ((secPasskey != null) && (attrValue != null) && (!attrValue.equals(""))) {
/* 154 */         String[] myValues = attrValue.split("-");
/* 155 */         if ((myValues.length == 2) && (myValues[1].indexOf("SK") != 0))
/* 156 */           attrValue = myValues[0] + "-" + "SK" + AESCryptoUtil.encrypt(secPasskey, myValues[1]);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 160 */       throw new UserException("属性[attrName=" + attrName + ", 加密失败]");
/*     */     }
/* 162 */     return attrValue;
/*     */   }
/*     */ 
/*     */   public String getValueFromSec(String attrName, String value)
/*     */   {
/* 167 */     String attrValue = value;
/*     */     try {
/* 169 */       String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 170 */       if ((secPasskey != null) && (attrValue != null) && (!attrValue.equals(""))) {
/* 171 */         String[] myValues = attrValue.split("-");
/* 172 */         if ((myValues.length == 2) && (myValues[1].indexOf("SK") == 0))
/* 173 */           attrValue = myValues[0] + "-" + AESCryptoUtil.decrypt(secPasskey, myValues[1]);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 177 */       throw new UserException("属性[attrName=" + attrName + ", 解密失败]");
/*     */     }
/* 179 */     return attrValue;
/*     */   }
/*     */ 
/*     */   public byte getAttrByte(String attrName) {
/* 183 */     byte defaultValue = 0;
/* 184 */     return getAttrByte(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public byte getAttrByte(String attrName, byte defaultValue) {
/* 188 */     byte value = defaultValue;
/* 189 */     Byte _value = (Byte)this.extendAttrMap.get(attrName);
/* 190 */     if (_value != null) {
/* 191 */       value = _value.byteValue();
/*     */     }
/* 193 */     return value;
/*     */   }
/*     */ 
/*     */   public short getAttrShort(String attrName) {
/* 197 */     short defaultValue = 0;
/* 198 */     return getAttrShort(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public short getAttrShort(String attrName, short defaultValue) {
/* 202 */     short value = defaultValue;
/* 203 */     Short _value = (Short)this.extendAttrMap.get(attrName);
/* 204 */     if (_value != null) {
/* 205 */       value = _value.shortValue();
/*     */     }
/* 207 */     return value;
/*     */   }
/*     */ 
/*     */   public Timestamp getAttrDateTime(String attrName) {
/* 211 */     return (Timestamp)this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public int getAttrInt(String attrName) {
/* 215 */     int defaultValue = 0;
/* 216 */     return getAttrInt(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public int getAttrInt(String attrName, int defaultValue) {
/* 220 */     int value = defaultValue;
/* 221 */     Integer _value = (Integer)this.extendAttrMap.get(attrName);
/* 222 */     if (_value != null) {
/* 223 */       value = _value.intValue();
/*     */     }
/* 225 */     return value;
/*     */   }
/*     */ 
/*     */   public long getAttrLong(String attrName) {
/* 229 */     long defaultValue = 0L;
/* 230 */     return getAttrLong(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public long getAttrLong(String attrName, long defaultValue) {
/* 234 */     long value = defaultValue;
/* 235 */     Long _value = (Long)this.extendAttrMap.get(attrName);
/* 236 */     if (_value != null) {
/* 237 */       value = _value.longValue();
/*     */     }
/* 239 */     return value;
/*     */   }
/*     */ 
/*     */   public float getAttrFloat(String attrName) {
/* 243 */     float defaultValue = 0.0F;
/* 244 */     return getAttrFloat(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public float getAttrFloat(String attrName, float defaultValue) {
/* 248 */     float value = defaultValue;
/* 249 */     Float _value = (Float)this.extendAttrMap.get(attrName);
/* 250 */     if (_value != null) {
/* 251 */       value = _value.floatValue();
/*     */     }
/* 253 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean getAttrBool(String attrName) {
/* 257 */     boolean defaultValue = false;
/* 258 */     return getAttrBool(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public boolean getAttrBool(String attrName, boolean defaultValue) {
/* 262 */     boolean value = defaultValue;
/* 263 */     Boolean _value = (Boolean)this.extendAttrMap.get(attrName);
/* 264 */     if (_value != null) {
/* 265 */       value = _value.booleanValue();
/*     */     }
/* 267 */     return value;
/*     */   }
/*     */ 
/*     */   public double getAttrDouble(String attrName) {
/* 271 */     double defaultValue = 0.0D;
/* 272 */     return getAttrDouble(attrName, defaultValue);
/*     */   }
/*     */ 
/*     */   public double getAttrDouble(String attrName, double defaultValue) {
/* 276 */     double value = defaultValue;
/* 277 */     Double _value = (Double)this.extendAttrMap.get(attrName);
/* 278 */     if (_value != null) {
/* 279 */       value = _value.doubleValue();
/*     */     }
/* 281 */     return value;
/*     */   }
/*     */ 
/*     */   public String getAttrString(String attrName) {
/* 285 */     String finalValue = (String)this.extendAttrMap.get(attrName);
/*     */     try {
/* 287 */       String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 288 */       if ((secPasskey != null) && (finalValue != null) && (!finalValue.equals(""))) {
/* 289 */         String[] myValues = finalValue.split("-");
/* 290 */         if ((myValues.length == 2) && (myValues[1].indexOf("SK") == 0))
/* 291 */           finalValue = myValues[0] + "-" + AESCryptoUtil.decrypt(secPasskey, myValues[1].substring(2));
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 295 */       throw new UserException("属性[attrName=" + attrName + ", 解密失败]");
/*     */     }
/* 297 */     return finalValue;
/*     */   }
/*     */ 
/*     */   public DboBlob getAttrBlob(String attrName) {
/* 301 */     return (DboBlob)this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public AttrObject getAttrObj(String attrName) {
/* 305 */     return (AttrObject)this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public Object getAttrArray(String attrName) {
/* 309 */     return this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public List<Serializable> getAttrList(String attrName) {
/* 313 */     return (List)this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public Date getAttrDate(String attrName) {
/* 317 */     return (Date)this.extendAttrMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, boolean value) {
/* 321 */     this.extendAttrMap.put(attrName, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrNull(String attrName) {
/* 325 */     this.extendAttrMap.put(attrName, null);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, byte value) {
/* 329 */     this.extendAttrMap.put(attrName, Byte.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, short value) {
/* 333 */     this.extendAttrMap.put(attrName, Short.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, int value) {
/* 337 */     this.extendAttrMap.put(attrName, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, long value) {
/* 341 */     this.extendAttrMap.put(attrName, Long.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, float value) {
/* 345 */     this.extendAttrMap.put(attrName, Float.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, double value) {
/* 349 */     this.extendAttrMap.put(attrName, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, String value) {
/* 353 */     String finalValue = value;
/*     */     try {
/* 355 */       String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 356 */       if ((secPasskey != null) && (value != null) && (!value.equals(""))) {
/* 357 */         String[] myValues = value.split("-");
/* 358 */         if ((myValues.length == 2) && (myValues[1].indexOf("SK") != 0))
/* 359 */           finalValue = myValues[0] + "-" + "SK" + AESCryptoUtil.encrypt(secPasskey, myValues[1]);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 363 */       throw new UserException("属性[attrName=" + attrName + ", 加密失败]");
/*     */     }
/* 365 */     this.extendAttrMap.put(attrName, finalValue);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, AttrObject value) {
/* 369 */     if (value != null) {
/* 370 */       checkAttrObject(attrName, value);
/*     */     }
/* 372 */     this.extendAttrMap.put(attrName, value);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, Object value) {
/* 376 */     Object finalObject = value;
/* 377 */     if (value != null) {
/* 378 */       checkAttrObject(attrName, (Serializable)value);
/*     */       try {
/* 380 */         String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrName);
/* 381 */         if ((secPasskey != null) && (value != null) && (!value.equals(""))) {
/* 382 */           String[] myValues = ((String)value).split("-");
/* 383 */           if ((myValues.length == 2) && (myValues[1].indexOf("SK") != 0))
/* 384 */             finalObject = myValues[0] + "-" + "SK" + AESCryptoUtil.encrypt(secPasskey, myValues[1]);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 388 */         throw new UserException("属性[attrName=" + attrName + ", 加密失败]");
/*     */       }
/*     */     }
/* 391 */     this.extendAttrMap.put(attrName, (Serializable)finalObject);
/*     */   }
/*     */ 
/*     */   public void setAttrValues(Map attrs) {
/* 395 */     if (attrs != null) {
/* 396 */       String[] attrNames = new String[attrs.size()];
/* 397 */       attrs.keySet().toArray(attrNames);
/* 398 */       for (int i = 0; i < attrNames.length; i++) {
/* 399 */         Serializable value = (Serializable)attrs.get(attrNames[i]);
/* 400 */         if (value != null) {
/* 401 */           checkAttrObject(attrNames[i], value);
/* 402 */           String secPasskey = SecAttrHelper.getPassKey(getClassName() + "." + attrNames[i]);
/* 403 */           if ((secPasskey != null) && (value != null) && (!value.equals(""))) {
/*     */             try {
/* 405 */               String[] myValues = ((String)value).split("-");
/* 406 */               if ((myValues.length == 2) && (myValues[1].indexOf("SK") != 0))
/* 407 */                 value = myValues[0] + "-" + "SK" + AESCryptoUtil.encrypt(secPasskey, myValues[1]);
/*     */             }
/*     */             catch (Exception e) {
/* 410 */               throw new UserException("属性[attrName=" + attrNames[i] + ", 加密失败]");
/*     */             }
/*     */           }
/* 413 */           this.extendAttrMap.put(attrNames[i], value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, ArrayList<Serializable> list) {
/* 420 */     if (list != null) {
/* 421 */       checkAttrList(attrName, list);
/*     */     }
/* 423 */     this.extendAttrMap.put(attrName, list);
/*     */   }
/*     */ 
/*     */   public void setAttrValue(String attrName, Date value) {
/* 427 */     this.extendAttrMap.put(attrName, value);
/*     */   }
/*     */ 
/*     */   private void checkAttrList(String attrName, List list) {
/* 431 */     for (int i = 0; i < list.size(); i++) {
/* 432 */       Serializable value = (Serializable)list.get(i);
/* 433 */       checkAttrObject(attrName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAttrArray(String attrName, Object array) {
/* 438 */     int arrayLen = Array.getLength(array);
/* 439 */     for (int i = 0; i < arrayLen; i++) {
/* 440 */       Serializable value = (Serializable)Array.get(array, i);
/* 441 */       checkAttrObject(attrName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAttrObject(String attrName, Serializable attrValue) {
/* 446 */     if (attrValue == null) return;
/*     */ 
/* 448 */     if ((attrValue.getClass() == Byte.class) || (attrValue.getClass() == Short.class) || (attrValue.getClass() == Integer.class) || (attrValue.getClass() == Long.class) || (attrValue.getClass() == Float.class) || (attrValue.getClass() == String.class) || (attrValue.getClass() == Boolean.class) || (attrValue.getClass() == Double.class) || ((attrValue instanceof Enum)) || ((attrValue instanceof Date)) || ((attrValue instanceof DboBlob)))
/*     */     {
/* 454 */       return;
/*     */     }
/*     */ 
/* 457 */     if ((attrValue instanceof IAttrObject)) {
/* 458 */       IAttrObject attrObj = (IAttrObject)attrValue;
/* 459 */       Map attrs = attrObj.getAllAttr();
/* 460 */       Object[] keys = attrs.keySet().toArray();
/* 461 */       for (int i = 0; i < keys.length; i++)
/* 462 */         checkAttrObject((String)keys[i], (Serializable)attrs.get(keys[i]));
/*     */     }
/* 464 */     else if (attrValue.getClass().isArray()) {
/* 465 */       checkAttrArray(attrName, attrValue);
/* 466 */     } else if ((attrValue instanceof ArrayList)) {
/* 467 */       checkAttrList(attrName, (ArrayList)attrValue);
/* 468 */     } else if (!(attrValue instanceof Serializable))
/*     */     {
/* 472 */       throw new UserException("无效的Attr类型[attrName=" + attrName + ", attrClassType=" + attrValue.getClass().getName() + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 478 */     return "";
/*     */   }
/*     */ 
/*     */   public void composeTo(AttrObject composed) {
/*     */     try {
/* 483 */       Object[] keys = getCoreAttrMap().keySet().toArray();
/* 484 */       for (int i = 0; i < keys.length; i++) {
/* 485 */         composed.getCoreAttrMap().put(keys[i], getCoreAttrMap().get(keys[i]));
/*     */       }
/*     */ 
/* 488 */       keys = getAllAttr().keySet().toArray();
/* 489 */       for (int i = 0; i < keys.length; i++) {
/* 490 */         composed.getAllAttr().put(keys[i], getAllAttr().get(keys[i]));
/*     */       }
/*     */ 
/* 493 */       keys = composed.getAllAttr().keySet().toArray();
/* 494 */       for (int i = 0; i < keys.length; i++)
/* 495 */         if (getAllAttr().get(keys[i]) == null)
/* 496 */           composed.removeAttr(keys[i].toString());
/*     */     }
/*     */     catch (Exception ex) {
/* 499 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void copyTo(AttrObject cloned) {
/*     */     try {
/* 505 */       cloned.getCoreAttrMap().putAll(getCoreAttrMap());
/* 506 */       cloned.setAttrValues(getAllAttr());
/*     */     } catch (Exception ex) {
/* 508 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map copyTo(Map cloned) {
/* 513 */     cloned.putAll(this.extendAttrMap);
/* 514 */     return cloned;
/*     */   }
/*     */   public Object clone() {
/*     */     AttrObject cloned;
/*     */     try {
/* 520 */       cloned = (AttrObject)getClass().newInstance();
/* 521 */       copyTo(cloned);
/*     */     } catch (Exception ex) {
/* 523 */       LogHome.getLog().error("", ex);
/* 524 */       cloned = new AttrObject();
/*     */     }
/* 526 */     return cloned;
/*     */   }
/*     */ 
/*     */   public AttrObject deepClone() {
/* 530 */     AttrObject cloned = null;
/*     */     try {
/* 532 */       BufOutputStream bufOut = new BufOutputStream();
/* 533 */       ObjectOutputStream out = new ObjectOutputStream(bufOut);
/* 534 */       out.writeObject(this);
/* 535 */       ObjectInputStream in = new ObjectInputStream(new BufInputStream(bufOut.getBuf()));
/* 536 */       cloned = (AttrObject)in.readObject();
/* 537 */       in.close();
/* 538 */       out.close();
/*     */     } catch (Exception ex) {
/* 540 */       LogHome.getLog().error("", ex);
/*     */     }
/* 542 */     return cloned;
/*     */   }
/*     */ 
/*     */   public void copyFromBean(Object bean) {
/*     */     try {
/* 547 */       BeanUtils.copyProperties(this, bean);
/*     */     } catch (Exception ex) {
/* 549 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToXml(String fileName) {
/*     */     try {
/* 555 */       FileOutputStream out = new FileOutputStream(fileName);
/* 556 */       XMLEncoder encode = new XMLEncoder(out);
/* 557 */       encode.writeObject(this);
/* 558 */       encode.close();
/*     */     } catch (Exception ex) {
/* 560 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static AttrObject readFromXml(String fileName) {
/* 565 */     AttrObject obj = null;
/*     */     try {
/* 567 */       InputStream in = FileHelper.getFileStream(fileName);
/* 568 */       XMLDecoder decode = new XMLDecoder(in);
/* 569 */       obj = (AttrObject)decode.readObject();
/* 570 */       decode.close();
/*     */     } catch (Exception ex) {
/* 572 */       LogHome.getLog().error("", ex);
/*     */     }
/* 574 */     return obj;
/*     */   }
/*     */ 
/*     */   public boolean isReadable() {
/* 578 */     return this.readable;
/*     */   }
/*     */ 
/*     */   public void setReadable(boolean readable) {
/* 582 */     this.readable = readable;
/*     */   }
/*     */   private class AttrMap<K, V> extends HashMap<K, V> {
/*     */     private AttrObject master;
/*     */ 
/* 588 */     public AttrMap(AttrObject master) { this.master = master;
/*     */     }
/*     */ 
/*     */     public V put(K key, V value)
/*     */     {
/* 593 */       Object result = null;
/* 594 */       if ((AttrObject.this.isReadable()) && (DataObjectManager.getInstance().hasListener()))
/* 595 */         synchronized (this.master) {
/* 596 */           AttrObject old = this.master.deepClone();
/* 597 */           DataObjectManager.getInstance().notifyChange(old, this.master);
/* 598 */           result = super.put(key, value);
/* 599 */           this.master.setReadable(false);
/*     */         }
/*     */       else {
/* 602 */         result = super.put(key, value);
/*     */       }
/* 604 */       return result;
/*     */     }
/*     */ 
/*     */     public V remove(Object key)
/*     */     {
/* 609 */       Object result = null;
/* 610 */       if ((AttrObject.this.isReadable()) && (DataObjectManager.getInstance().hasListener()))
/* 611 */         synchronized (this.master) {
/* 612 */           AttrObject old = this.master.deepClone();
/* 613 */           DataObjectManager.getInstance().notifyChange(old, this.master);
/* 614 */           result = super.remove(key);
/* 615 */           this.master.setReadable(false);
/*     */         }
/*     */       else {
/* 618 */         result = super.remove(key);
/*     */       }
/* 620 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AttrName
/*     */   {
/*     */     public static final String className = "className";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AttrObject
 * JD-Core Version:    0.6.0
 */