/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import com.boco.common.util.excel.ExcelHelper;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ExcelImpTemplateMeta extends TemplateMeta
/*     */ {
/*     */   private boolean isValid;
/*  35 */   private Map<String, Map<String, ExcelImpTemplateRelatedMeta>> excelImpRelateds = new HashMap();
/*     */ 
/*  37 */   private Map<String, Map<String, List>> excelRelatedValue = new HashMap();
/*     */ 
/*  39 */   private List<String> excelImpAttrList = new ArrayList();
/*     */ 
/*  41 */   private List<String> excelImpMoreRelatedAttrList = new ArrayList();
/*     */ 
/*  43 */   private String bmClassId = "";
/*     */ 
/*     */   public static boolean isNotEmpty(String str)
/*     */   {
/*  49 */     return (str != null) && (str.trim().length() > 0);
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, ExcelImpTemplateRelatedMeta>> getExcelImpRelateds()
/*     */   {
/*  56 */     return this.excelImpRelateds;
/*     */   }
/*     */ 
/*     */   public void setExcelImpRelateds(Map<String, Map<String, ExcelImpTemplateRelatedMeta>> aExcelImpRelateds) {
/*  60 */     this.excelImpRelateds = aExcelImpRelateds;
/*     */   }
/*     */ 
/*     */   public boolean getIsValid()
/*     */   {
/*  67 */     return this.isValid;
/*     */   }
/*     */ 
/*     */   public void setIsValid(boolean aIsValid) {
/*  71 */     this.isValid = aIsValid;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, List>> getExcelRelatedValue()
/*     */   {
/*  79 */     return this.excelRelatedValue;
/*     */   }
/*     */ 
/*     */   public void setExcelRelatedValue(Map<String, Map<String, List>> aExcelRelatedValue) {
/*  83 */     this.excelRelatedValue = aExcelRelatedValue;
/*     */   }
/*     */ 
/*     */   public List<String> getExcelImpAttrList()
/*     */   {
/*  90 */     return this.excelImpAttrList;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/*  94 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public void setExcelImpAttrList(List<String> aExcelImpAttrList) {
/*  98 */     this.excelImpAttrList = aExcelImpAttrList;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/* 102 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public String checkTemplate()
/*     */   {
/* 116 */     List attrList = super.getExtAttrMetaGroup().getGroupAttrIds();
/* 117 */     if (attrList.size() == 0) {
/* 118 */       return "未定义excel导入模板属性";
/*     */     }
/*     */ 
/* 122 */     String bmClassId = super.getExtAttrMetaGroup().getBmClassId();
/* 123 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 124 */     Iterator iter = classMeta.getUniqueAttrIds().keySet().iterator();
/* 125 */     boolean bFlag = false;
/* 126 */     while (iter.hasNext()) {
/* 127 */       Object key = iter.next();
/* 128 */       String[] uniqueAttrs = (String[])(String[])classMeta.getUniqueAttrIds().get(key);
/*     */ 
/* 130 */       int i = 0;
/* 131 */       for (i = 0; i < uniqueAttrs.length; i++) {
/* 132 */         String sAttrId = uniqueAttrs[i];
/* 133 */         if (!super.getExtAttrMetaGroup().getGroupAttrIds().contains(sAttrId))
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/* 138 */       if (i == uniqueAttrs.length)
/*     */       {
/* 140 */         bFlag = true;
/* 141 */         break;
/*     */       }
/*     */     }
/* 144 */     if (!bFlag) {
/* 145 */       return "模板属性列表中必须包含本类最少一种唯一性标识中的所有属性列";
/*     */     }
/*     */ 
/* 149 */     iter = classMeta.getAllAttrMetas().keySet().iterator();
/* 150 */     while (iter.hasNext()) {
/* 151 */       String sAttrId = (String)iter.next();
/* 152 */       if ((sAttrId.equals("")) || (sAttrId.equalsIgnoreCase("CUID")) || (sAttrId.equalsIgnoreCase("OBJECTID")) || (sAttrId.equalsIgnoreCase("ISDELETE")) || (sAttrId.equalsIgnoreCase("LAST_MODIFY_TIME")) || (sAttrId.equalsIgnoreCase("CREATE_TIME"))) {
/*     */         continue;
/*     */       }
/* 155 */       BMAttrMeta attrMeta = (BMAttrMeta)classMeta.getAllAttrMetas().get(sAttrId);
/* 156 */       String sDefaultValue = "";
/* 157 */       if (attrMeta.getDefaultValue() == null)
/* 158 */         sDefaultValue = "";
/*     */       else {
/* 160 */         sDefaultValue = attrMeta.getDefaultValue();
/*     */       }
/* 162 */       if ((attrMeta.getIsNotNull().booleanValue()) && (!isNotEmpty(sDefaultValue))) {
/* 163 */         int i = 0;
/* 164 */         for (i = 0; (i < attrList.size()) && 
/* 165 */           (!attrList.get(i).equals(sAttrId)); i++);
/* 168 */         if (i == attrList.size()) {
/* 169 */           return "属性'" + attrMeta.getLabelCn() + "'不允许为空且无默认值，必须包含此列";
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     Map relatedAttrIds = new HashMap();
/*     */ 
/* 178 */     relatedAttrIds = geClassRelatedAttrIdList();
/*     */ 
/* 184 */     iter = relatedAttrIds.keySet().iterator();
/* 185 */     while (iter.hasNext()) {
/* 186 */       String sAttrId = (String)iter.next();
/* 187 */       String sReturn = checkTemplateRelated(classMeta, sAttrId);
/* 188 */       if (isNotEmpty(sReturn)) {
/* 189 */         return sReturn;
/*     */       }
/*     */     }
/*     */ 
/* 193 */     return "";
/*     */   }
/*     */ 
/*     */   public void getAllUpperAttrs(BMClassMeta classMeta, List<String> attrIds, List<String> upperAtts)
/*     */   {
/* 202 */     for (int i = 0; i < attrIds.size(); i++) {
/* 203 */       BMAttrMeta attrMeta = (BMAttrMeta)classMeta.getAllAttrMetas().get(attrIds.get(i));
/* 204 */       upperAtts.add(attrIds.get(i));
/* 205 */       if (attrMeta.getUpperAttrIds().size() > 0)
/* 206 */         getAllUpperAttrs(classMeta, attrMeta.getUpperAttrIds(), upperAtts);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String checkTemplateRelated(BMClassMeta classMeta, String sAttrId)
/*     */   {
/* 219 */     String sRelatedClassId = getRelatedClassId(sAttrId);
/* 220 */     if (sRelatedClassId.equals("")) {
/* 221 */       return "关联类 '" + classMeta.getLabelCn() + "' 关联类有多个，未设置，无法确定";
/*     */     }
/* 223 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRelatedClassId);
/*     */ 
/* 226 */     String sRootAttrId = (String)classMeta.getAllRootAttrMap().get(sAttrId);
/* 227 */     if (sRootAttrId == null) sRootAttrId = sAttrId;
/* 228 */     String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, relatedClassMeta);
/* 229 */     if (uniqueIds == null) {
/* 230 */       return "关联类 '" + relatedClassMeta.getLabelCn() + "' 唯一性描述未指定";
/*     */     }
/*     */ 
/* 233 */     return "";
/*     */   }
/*     */ 
/*     */   public Map<String, String> geClassRelatedAttrIdList()
/*     */   {
/* 240 */     Map relatedAttrIds = new HashMap();
/*     */ 
/* 242 */     String bmClassId = super.getExtAttrMetaGroup().getBmClassId();
/* 243 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/*     */ 
/* 245 */     List attrIds = getExtAttrMetaGroup().getGroupAttrIds();
/* 246 */     for (int i = 0; i < attrIds.size(); i++) {
/* 247 */       BMAttrMeta attrMeta = (BMAttrMeta)classMeta.getAllAttrMetas().get(attrIds.get(i));
/* 248 */       if ((attrMeta.getRelatedAttrIds() == null) || 
/* 249 */         (attrMeta.getRelatedAttrIds().size() <= 0)) continue;
/* 250 */       relatedAttrIds.put((String)attrIds.get(i), (String)attrIds.get(i));
/*     */     }
/*     */ 
/* 256 */     Iterator iter = relatedAttrIds.keySet().iterator();
/* 257 */     List upperIds = new ArrayList();
/* 258 */     while (iter.hasNext()) {
/* 259 */       String sAttrId = (String)iter.next();
/* 260 */       BMAttrMeta attrMeta = (BMAttrMeta)classMeta.getAllAttrMetas().get(sAttrId);
/*     */ 
/* 262 */       if (attrMeta.getUpperAttrIds().size() > 0) {
/* 263 */         getAllUpperAttrs(classMeta, attrMeta.getUpperAttrIds(), upperIds);
/*     */       }
/*     */     }
/* 266 */     for (int i = 0; i < upperIds.size(); i++) {
/* 267 */       relatedAttrIds.remove(upperIds.get(i));
/*     */     }
/* 269 */     return relatedAttrIds;
/*     */   }
/*     */ 
/*     */   public String getRelatedClassId(String sAttrId)
/*     */   {
/* 278 */     String bmClassId = super.getExtAttrMetaGroup().getBmClassId();
/* 279 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 280 */     BMAttrMeta attrMeta = classMeta.getAttrMeta(sAttrId);
/*     */ 
/* 282 */     String sRootAttrId = (String)classMeta.getAllRootAttrMap().get(sAttrId);
/* 283 */     if (sRootAttrId == null) sRootAttrId = sAttrId;
/*     */ 
/* 285 */     Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 286 */     if (relatedAttrIds == null) {
/* 287 */       return "";
/*     */     }
/*     */ 
/* 290 */     String sRelatedClassId = "";
/*     */ 
/* 292 */     Map classRelatedMeta = null;
/* 293 */     if (relatedAttrIds.size() > 1)
/*     */     {
/* 295 */       Iterator iter = relatedAttrIds.keySet().iterator();
/* 296 */       while (iter.hasNext()) {
/* 297 */         String sClassId = (String)iter.next();
/* 298 */         String key = sRootAttrId + ExcelHelper.KEY_FLAG + sClassId;
/*     */ 
/* 300 */         classRelatedMeta = (Map)this.excelImpRelateds.get(key);
/* 301 */         if (classRelatedMeta != null) {
/* 302 */           sRelatedClassId = sClassId;
/* 303 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 308 */       Iterator iter = relatedAttrIds.keySet().iterator();
/* 309 */       if (iter.hasNext()) {
/* 310 */         sRelatedClassId = (String)iter.next();
/*     */       }
/*     */     }
/*     */ 
/* 314 */     return sRelatedClassId;
/*     */   }
/*     */ 
/*     */   public String[] getRelatedClassUniqueIds(String sRootAttrId, BMClassMeta classMeta)
/*     */   {
/* 323 */     String[] uniqueAttrIds = null;
/*     */ 
/* 325 */     String sClassId = classMeta.getBmClassId();
/* 326 */     String key = sRootAttrId + ExcelHelper.KEY_FLAG + sClassId;
/*     */ 
/* 328 */     if (classMeta.getUniqueAttrIds().size() > 1)
/*     */     {
/* 330 */       if (this.excelImpRelateds.get(key) != null) {
/* 331 */         ExcelImpTemplateRelatedMeta excelImpTemplateRelatedMeta = (ExcelImpTemplateRelatedMeta)((Map)this.excelImpRelateds.get(key)).get("unique");
/* 332 */         if (excelImpTemplateRelatedMeta != null)
/*     */         {
/* 334 */           uniqueAttrIds = (String[])(String[])classMeta.getUniqueAttrIds().get(excelImpTemplateRelatedMeta.getClassUniqueName());
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 339 */       Iterator uniqueIter = classMeta.getUniqueAttrIds().keySet().iterator();
/* 340 */       String uniqueId = (String)uniqueIter.next();
/* 341 */       uniqueAttrIds = (String[])(String[])classMeta.getUniqueAttrIds().get(uniqueId);
/*     */     }
/*     */ 
/* 344 */     return uniqueAttrIds;
/*     */   }
/*     */ 
/*     */   public void synAllImpAttrList()
/*     */   {
/* 353 */     List attrList = super.getExtAttrMetaGroup().getGroupAttrIds();
/* 354 */     this.excelImpAttrList = new ArrayList();
/* 355 */     for (int i = 0; i < attrList.size(); i++) {
/* 356 */       this.excelImpAttrList.add(attrList.get(i));
/*     */     }
/*     */ 
/* 359 */     String bmClassId = super.getExtAttrMetaGroup().getBmClassId();
/* 360 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/*     */ 
/* 362 */     for (int i = 0; i < attrList.size(); i++) {
/* 363 */       String sAttrId = (String)attrList.get(i);
/* 364 */       BMAttrMeta attrMeta = classMeta.getAttrMeta(sAttrId);
/* 365 */       if (attrMeta.getUpperAttrIds().size() > 0) {
/* 366 */         List upperAttrList = new ArrayList();
/* 367 */         attrMeta.getAllUpperAttrs(upperAttrList);
/* 368 */         for (int j = 0; j < upperAttrList.size(); j++)
/* 369 */           for (int k = 0; k < this.excelImpAttrList.size(); k++)
/* 370 */             if (((String)this.excelImpAttrList.get(k)).equals(upperAttrList.get(j))) {
/* 371 */               this.excelImpAttrList.set(k, "");
/* 372 */               break;
/*     */             }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setImpMoreRelatedAttrList(List<String> moreRelatedAttrList)
/*     */   {
/* 380 */     this.excelImpMoreRelatedAttrList = moreRelatedAttrList;
/*     */   }
/*     */   public List<String> getImpMoreRelatedAttrList() {
/* 383 */     return this.excelImpMoreRelatedAttrList;
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService()
/*     */   {
/* 388 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta
 * JD-Core Version:    0.6.0
 */