/*     */ package com.boco.raptor.drm.core.web.startup;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*     */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import java.io.FileOutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ExportModelXls
/*     */ {
/*  19 */   private Map<String, BMClassMeta> allClassMeta = new HashMap();
/*     */   private String[] bmClassIds;
/*     */ 
/*     */   protected void initDrm()
/*     */     throws Exception
/*     */   {
/*  26 */     DrmStartupServlet servlet = new DrmStartupServlet();
/*  27 */     servlet.init();
/*  28 */     BMClassMeta[] classMetas = getBMModelService().getAllClassMeta(ServiceHelper.createSvActCxt());
/*  29 */     this.bmClassIds = new String[classMetas.length];
/*  30 */     for (int i = 0; i < classMetas.length; i++) {
/*  31 */       BMClassMeta classMeta = classMetas[i];
/*  32 */       this.allClassMeta.put(classMeta.getBmClassId(), classMeta);
/*  33 */       this.bmClassIds[i] = classMeta.getBmClassId();
/*     */     }
/*  35 */     Arrays.sort(this.bmClassIds);
/*     */   }
/*     */ 
/*     */   public void writeClassXls() {
/*     */     try {
/*  40 */       FileOutputStream out = new FileOutputStream("类元数据.csv");
/*  41 */       String title = "表名,中文名称,唯一性属性,名称属性\n";
/*  42 */       out.write(title.getBytes("GBK"));
/*  43 */       for (int i = 0; i < this.bmClassIds.length; i++) {
/*  44 */         BMClassMeta classMeta = (BMClassMeta)this.allClassMeta.get(this.bmClassIds[i]);
/*  45 */         String row = classMeta.getBmClassId() + "," + classMeta.getLabelCn() + ",";
/*  46 */         Map uniqueAttrIds = classMeta.getUniqueAttrIds();
/*  47 */         if (uniqueAttrIds != null) {
/*  48 */           String _unique = "";
/*  49 */           for (String key : uniqueAttrIds.keySet()) {
/*  50 */             String[] values = (String[])uniqueAttrIds.get(key);
/*  51 */             _unique = _unique + "[name=" + key + ";uniqueId=";
/*  52 */             for (int j = 0; j < values.length; j++) {
/*  53 */               if (j > 0) {
/*  54 */                 _unique = _unique + ";";
/*     */               }
/*  56 */               _unique = _unique + values[j];
/*     */             }
/*  58 */             _unique = _unique + "]";
/*     */           }
/*  60 */           row = row + _unique + ",";
/*     */         }
/*  62 */         List labelAttrIds = classMeta.getConstructLabelAttrIds();
/*  63 */         for (int j = 0; j < labelAttrIds.size(); j++) {
/*  64 */           String labelAttrId = (String)labelAttrIds.get(j);
/*  65 */           if (j > 0) {
/*  66 */             row = row + ";";
/*     */           }
/*  68 */           row = row + labelAttrId;
/*     */         }
/*  70 */         row = row + "\n";
/*  71 */         LogHome.getLog().info(row);
/*  72 */         out.write(row.getBytes("GBK"));
/*     */       }
/*  74 */       out.close();
/*     */     } catch (Exception ex) {
/*  76 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeRelatedAttrMetaXls() {
/*     */     try {
/*  82 */       FileOutputStream out = new FileOutputStream("类关联元数据.csv");
/*  83 */       String title = "表名,中文名称,属性ID,上级属性,关联类\n";
/*  84 */       out.write(title.getBytes("GBK"));
/*  85 */       for (int i = 0; i < this.bmClassIds.length; i++) {
/*  86 */         BMClassMeta classMeta = (BMClassMeta)this.allClassMeta.get(this.bmClassIds[i]);
/*  87 */         String classInfo = classMeta.getBmClassId() + "," + classMeta.getLabelCn() + ",";
/*  88 */         List allAttrIds = classMeta.getAllAttrIds();
/*  89 */         String[] _allAttrIds = new String[allAttrIds.size()];
/*  90 */         for (int k = 0; k < allAttrIds.size(); k++) {
/*  91 */           _allAttrIds[k] = ((String)allAttrIds.get(k));
/*     */         }
/*  93 */         Arrays.sort(_allAttrIds);
/*  94 */         for (int k = 0; k < _allAttrIds.length; k++) {
/*  95 */           String prefix = classInfo;
/*  96 */           String attrId = _allAttrIds[k];
/*  97 */           prefix = prefix + attrId + ",";
/*  98 */           BMAttrMeta attrMeta = classMeta.getAttrMeta(attrId);
/*  99 */           if (attrMeta == null) {
/* 100 */             LogHome.getLog().error("--- 没有相应的AttrMeta, bmClassId=" + classMeta.getBmClassId() + ", attrId=" + attrId);
/*     */           }
/*     */           else
/*     */           {
/* 104 */             String upper = "";
/* 105 */             List upperAttrIds = attrMeta.getUpperAttrIds();
/* 106 */             for (int j = 0; (upperAttrIds != null) && (j < upperAttrIds.size()); j++) {
/* 107 */               if (j > 0) {
/* 108 */                 upper = upper + ";";
/*     */               }
/* 110 */               upper = upper + (String)upperAttrIds.get(j);
/*     */             }
/* 112 */             prefix = prefix + upper + ",";
/*     */ 
/* 114 */             Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 115 */             if ((relatedAttrIds != null) && (relatedAttrIds.size() > 0)) {
/* 116 */               Object[] relatedClassIds = relatedAttrIds.keySet().toArray();
/* 117 */               for (int m = 0; m < relatedClassIds.length; m++) {
/* 118 */                 String row = prefix;
/* 119 */                 row = row + relatedClassIds[m];
/* 120 */                 row = row + "\n";
/* 121 */                 LogHome.getLog().info(row);
/* 122 */                 out.write(row.getBytes("GBK"));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 127 */       out.close();
/*     */     } catch (Exception ex) {
/* 129 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeAttrMetaXls() {
/*     */     try {
/* 135 */       FileOutputStream out = new FileOutputStream("类属性元数据.csv");
/* 136 */       String title = "表名,中文名称,属性ID,属性名称,可空,系统,枚举名称,默认值\n";
/* 137 */       out.write(title.getBytes("GBK"));
/* 138 */       for (int i = 0; i < this.bmClassIds.length; i++) {
/* 139 */         BMClassMeta classMeta = (BMClassMeta)this.allClassMeta.get(this.bmClassIds[i]);
/* 140 */         String classInfo = classMeta.getBmClassId() + "," + classMeta.getLabelCn() + ",";
/* 141 */         List allAttrIds = classMeta.getAllAttrIds();
/* 142 */         String[] _allAttrIds = new String[allAttrIds.size()];
/* 143 */         for (int k = 0; k < allAttrIds.size(); k++) {
/* 144 */           _allAttrIds[k] = ((String)allAttrIds.get(k));
/*     */         }
/* 146 */         Arrays.sort(_allAttrIds);
/* 147 */         for (int k = 0; k < _allAttrIds.length; k++) {
/* 148 */           String row = classInfo;
/* 149 */           String attrId = _allAttrIds[k];
/* 150 */           row = row + attrId + ",";
/* 151 */           BMAttrMeta attrMeta = classMeta.getAttrMeta(attrId);
/* 152 */           if (attrMeta == null) {
/* 153 */             LogHome.getLog().error("--- 没有相应的AttrMeta, bmClassId=" + classMeta.getBmClassId() + ", attrId=" + attrId);
/*     */           }
/*     */           else {
/* 156 */             row = row + attrMeta.getLabelCn() + ",";
/* 157 */             if (attrMeta.getIsNotNull().booleanValue())
/* 158 */               row = row + "是,";
/*     */             else {
/* 160 */               row = row + "否,";
/*     */             }
/*     */ 
/* 163 */             if (attrMeta.getIsSystem().booleanValue())
/* 164 */               row = row + "是,";
/*     */             else {
/* 166 */               row = row + "否,";
/*     */             }
/*     */ 
/* 169 */             String enumId = attrMeta.getEnumId();
/* 170 */             if (enumId != null)
/* 171 */               row = row + enumId + ",";
/*     */             else {
/* 173 */               row = row + " ,";
/*     */             }
/*     */ 
/* 176 */             if (attrMeta.getDefaultValue() != null) {
/* 177 */               row = row + attrMeta.getDefaultValue();
/*     */             }
/* 179 */             row = row + "\n";
/* 180 */             LogHome.getLog().info(row);
/* 181 */             out.write(row.getBytes("GBK"));
/*     */           }
/*     */         }
/*     */       }
/* 184 */       out.close();
/*     */     } catch (Exception ex) {
/* 186 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeEnumXls() {
/*     */     try {
/* 192 */       FileOutputStream out = new FileOutputStream("枚举.csv");
/* 193 */       String title = "枚举类ID,枚举值,枚举名称\n";
/* 194 */       out.write(title.getBytes("GBK"));
/* 195 */       BMEnumMeta[] enumMetas = getBMModelService().getAllEnumMetas(ServiceHelper.createSvActCxt());
/* 196 */       for (int i = 0; i < enumMetas.length; i++) {
/* 197 */         BMEnumMeta enumMeta = enumMetas[i];
/* 198 */         List enums = enumMeta.getEnums();
/* 199 */         String row = "";
/* 200 */         for (int k = 0; k < enums.size(); k++) {
/* 201 */           IDrmEnumValue _enum = (IDrmEnumValue)enums.get(k);
/* 202 */           row = enumMeta.getEnumId() + ",";
/* 203 */           row = row + _enum.getEnumValue() + "," + _enum.getEnumName();
/* 204 */           row = row + "\n";
/* 205 */           LogHome.getLog().info(row);
/* 206 */           out.write(row.getBytes("GBK"));
/*     */         }
/*     */       }
/* 209 */       out.close();
/*     */     } catch (Exception ex) {
/* 211 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService() {
/* 216 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try {
/* 222 */       ExportModelXls export = new ExportModelXls();
/* 223 */       export.initDrm();
/* 224 */       export.writeClassXls();
/* 225 */       export.writeAttrMetaXls();
/* 226 */       export.writeRelatedAttrMetaXls();
/* 227 */       export.writeEnumXls();
/*     */     } catch (Exception ex) {
/* 229 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.startup.ExportModelXls
 * JD-Core Version:    0.6.0
 */