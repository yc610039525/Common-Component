/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class GenericEnumDO<E> extends GenericDO
/*     */   implements Comparable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  32 */   private final Map<String, Class> attrTypeMap = new HashMap();
/*     */ 
/*     */   public GenericEnumDO() {
/*  35 */     initAttrTypes();
/*     */   }
/*     */ 
/*     */   public GenericEnumDO(String className) {
/*  39 */     super(className);
/*  40 */     initAttrTypes();
/*     */   }
/*     */ 
/*     */   public GenericEnumDO(String className, E value) {
/*  44 */     super.setAttrValue("KEY_NUM", value);
/*     */   }
/*     */ 
/*     */   public String getEnumName() {
/*  48 */     return super.getAttrString("KEY_VALUE");
/*     */   }
/*     */ 
/*     */   public void setEnumName(String enumName) {
/*  52 */     super.setAttrValue("KEY_VALUE", enumName);
/*     */   }
/*     */ 
/*     */   public E getEnumValue() {
/*  56 */     return super.getAttrValue("KEY_NUM");
/*     */   }
/*     */ 
/*     */   public void setEnumValue(E value) {
/*  60 */     super.setAttrValue("KEY_NUM", value);
/*     */   }
/*     */ 
/*     */   public Class getAttrType(String attrName) {
/*  64 */     return (Class)this.attrTypeMap.get(attrName);
/*     */   }
/*     */ 
/*     */   public String[] getAllAttrNames() {
/*  68 */     String[] attrNames = new String[this.attrTypeMap.size()];
/*  69 */     this.attrTypeMap.keySet().toArray(attrNames);
/*  70 */     return attrNames;
/*     */   }
/*     */ 
/*     */   public void setAttrType(String name, Class attrType) {
/*  74 */     this.attrTypeMap.put(name, attrType);
/*     */   }
/*     */ 
/*     */   protected void initAttrTypes() {
/*  78 */     setAttrType("KEY_VALUE", String.class);
/*  79 */     setAttrType("KEY_NUM", Long.TYPE);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/*  83 */     return (obj != null) && (getEnumValue().equals(((GenericEnumDO)obj).getEnumValue()));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  87 */     int hash = 0;
/*  88 */     if (getEnumValue() != null) {
/*  89 */       hash = getEnumValue().hashCode();
/*     */     }
/*  91 */     return hash;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object compared) {
/*  95 */     int res = 0;
/*  96 */     GenericEnumDO _compared = (GenericEnumDO)compared;
/*  97 */     if ((getEnumValue() instanceof Comparable)) {
/*  98 */       Comparable source = (Comparable)getEnumValue();
/*  99 */       Comparable target = (Comparable)_compared.getEnumValue();
/* 100 */       if ((source != null) && (target != null)) {
/* 101 */         res = source.compareTo(target);
/*     */       }
/*     */     }
/* 104 */     return res;
/*     */   }
/*     */ 
/*     */   public static class AttrName
/*     */   {
/*     */     public static final String enumName = "KEY_VALUE";
/*     */     public static final String enumValue = "KEY_NUM";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.GenericEnumDO
 * JD-Core Version:    0.6.0
 */