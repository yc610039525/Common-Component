/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.lang.builder.ToStringBuilder;
/*     */ import org.apache.commons.lang.builder.ToStringStyle;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BMEnumMeta<T>
/*     */   implements Serializable
/*     */ {
/*     */   private String enumId;
/*     */   private String enumLabelCn;
/*  34 */   private List<IDrmEnumValue> enums = new ArrayList();
/*  35 */   private int defaultEnumIndex = 0;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public BMEnumMeta()
/*     */   {
/*     */   }
/*     */ 
/*     */   public BMEnumMeta(String enumId, String enumLabelCn)
/*     */   {
/*  42 */     this.enumId = enumId;
/*  43 */     this.enumLabelCn = enumLabelCn;
/*     */   }
/*     */ 
/*     */   public String getEnumId() {
/*  47 */     return this.enumId;
/*     */   }
/*     */ 
/*     */   public List<IDrmEnumValue> getEnums() {
/*  51 */     return this.enums;
/*     */   }
/*     */ 
/*     */   public String getEnumLabelCn() {
/*  55 */     return this.enumLabelCn;
/*     */   }
/*     */ 
/*     */   public int getDefaultEnumIndex() {
/*  59 */     return this.defaultEnumIndex;
/*     */   }
/*     */ 
/*     */   public void setEnumId(String enumId) {
/*  63 */     this.enumId = enumId;
/*     */   }
/*     */ 
/*     */   public void setEnums(List<IDrmEnumValue> enums) {
/*  67 */     this.enums = enums;
/*     */   }
/*     */ 
/*     */   public void setEnumLabelCn(String enumLabelCn) {
/*  71 */     this.enumLabelCn = enumLabelCn;
/*     */   }
/*     */ 
/*     */   public void setDefaultEnumIndex(int defaultEnumIndex) {
/*  75 */     this.defaultEnumIndex = defaultEnumIndex;
/*     */   }
/*     */ 
/*     */   public void addEnum(IDrmEnumValue drmEnum) {
/*  79 */     this.enums.add(drmEnum);
/*     */   }
/*     */ 
/*     */   public T getEnumValue(String enumName) {
/*  83 */     Object enumValue = null;
/*  84 */     if ((enumName == null) || (enumName.trim().length() == 0)) {
/*  85 */       throw new UserException("无效的枚举名称");
/*     */     }
/*     */ 
/*  88 */     for (IDrmEnumValue drmEnum : this.enums) {
/*  89 */       if (enumName.equals(drmEnum.getEnumName())) {
/*  90 */         enumValue = drmEnum.getEnumValue();
/*  91 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  95 */     if (enumValue == null) {
/*  96 */       throw new UserException("enumId=" + this.enumId + ",enumName=" + enumName + "，无效枚举 ！");
/*     */     }
/*  98 */     return enumValue;
/*     */   }
/*     */ 
/*     */   public String getEnumName(T enumValue) {
/* 102 */     String enumName = null;
/* 103 */     if (enumValue == null) {
/* 104 */       throw new UserException("无效的枚举值");
/*     */     }
/*     */ 
/* 107 */     for (IDrmEnumValue drmEnum : this.enums) {
/* 108 */       if (drmEnum.isEnumValueEqual(enumValue)) {
/* 109 */         enumName = drmEnum.getEnumName();
/* 110 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 114 */     if (enumName == null) {
/* 115 */       enumName = "未知枚举";
/* 116 */       LogHome.getLog().error("enumId=" + this.enumId + ",enumValue=" + enumValue + "，无效枚举 ！");
/*     */     }
/* 118 */     return enumName;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 122 */     return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.BMEnumMeta
 * JD-Core Version:    0.6.0
 */