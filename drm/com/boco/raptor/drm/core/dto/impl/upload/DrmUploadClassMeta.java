/*     */ package com.boco.raptor.drm.core.dto.impl.upload;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DrmUploadClassMeta
/*     */   implements Serializable
/*     */ {
/*     */   private String bmClassId;
/*     */   private int excelRow;
/*  32 */   private Map<String, Object> excelAttrValueMap = new HashMap();
/*     */ 
/*  34 */   private Map<String, String> excelAttrLabelMap = new HashMap();
/*     */ 
/*  36 */   private Map<String, int[]> excelAttrAddrMap = new HashMap();
/*     */ 
/*  38 */   private Map<String, String> attrErrorDetailMap = new HashMap();
/*     */ 
/*  40 */   private String rowErrorInfo = "";
/*     */ 
/*  42 */   private String saveErrorInfo = "";
/*     */ 
/*  44 */   private String dataType = "";
/*     */ 
/*  46 */   private String importResultInfo = "";
/*     */ 
/*  49 */   List<String> allAttrList = new ArrayList();
/*     */ 
/*  52 */   List<String> allRelatedAttr = new ArrayList();
/*     */ 
/*  55 */   Map<String, String[]> uniqueAttrIds = new HashMap();
/*  56 */   Map<String, Map<String, String>> uniqueAttrFoundList = new HashMap();
/*     */ 
/*  59 */   Map<String, String> relatedNoClassAttrValueMap = new HashMap();
/*  60 */   Map<String, String> relatedNoClassAttrLabelMap = new HashMap();
/*     */ 
/*  63 */   String cuid = "";
/*     */ 
/*     */   public int getExcelRow()
/*     */   {
/*  76 */     return this.excelRow;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/*  80 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setExcelRow(int excelRow) {
/*  84 */     this.excelRow = excelRow;
/*     */   }
/*     */ 
/*     */   public void setRowErrorInfo(String rowErrorInfo)
/*     */   {
/*  89 */     this.rowErrorInfo = rowErrorInfo;
/*     */   }
/*     */ 
/*     */   public void setImportResultInfo(String importResultInfo) {
/*  93 */     this.importResultInfo = importResultInfo;
/*     */   }
/*     */ 
/*     */   public void setExcelAttrValueMap(Map excelAttrValueMap) {
/*  97 */     this.excelAttrValueMap = excelAttrValueMap;
/*     */   }
/*     */ 
/*     */   public void setExcelAttrAddrMap(Map<String, int[]> excelAttrAddrMap) {
/* 101 */     this.excelAttrAddrMap = excelAttrAddrMap;
/*     */   }
/*     */ 
/*     */   public void setDataType(String dataType) {
/* 105 */     this.dataType = dataType;
/*     */   }
/*     */ 
/*     */   public void setAttrErrorDetailMap(Map<String, String> attrErrorDetailMap)
/*     */   {
/* 110 */     this.attrErrorDetailMap = attrErrorDetailMap;
/*     */   }
/*     */ 
/*     */   public void setSaveErrorInfo(String saveErrorInfo)
/*     */   {
/* 115 */     this.saveErrorInfo = saveErrorInfo;
/*     */   }
/*     */ 
/*     */   public void setAllRelatedAttr(List<String> allRelatedAttr) {
/* 119 */     this.allRelatedAttr = allRelatedAttr;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 123 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public String getRowErrorInfo() {
/* 127 */     return this.rowErrorInfo;
/*     */   }
/*     */ 
/*     */   public String getImportResultInfo() {
/* 131 */     return this.importResultInfo;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getExcelAttrValueMap() {
/* 135 */     return this.excelAttrValueMap;
/*     */   }
/*     */ 
/*     */   public Map<String, int[]> getExcelAttrAddrMap() {
/* 139 */     return this.excelAttrAddrMap;
/*     */   }
/*     */ 
/*     */   public String getDataType() {
/* 143 */     return this.dataType;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getAttrErrorDetailMap() {
/* 147 */     return this.attrErrorDetailMap;
/*     */   }
/*     */ 
/*     */   public String getSaveErrorInfo() {
/* 151 */     return this.saveErrorInfo;
/*     */   }
/*     */ 
/*     */   public String getExcelAttrValue(Object sAttrId) {
/* 155 */     return (String)this.excelAttrValueMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public String getExcelAttrLabel(Object sAttrId) {
/* 159 */     return (String)this.excelAttrLabelMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public void setExcelAttrValue(String sAttrId, String sAttrValue) {
/* 163 */     this.excelAttrValueMap.put(sAttrId, sAttrValue);
/*     */ 
/* 165 */     if (this.excelAttrLabelMap.get(sAttrId) == null)
/*     */     {
/* 167 */       this.excelAttrLabelMap.put(sAttrId, sAttrValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int[] getExcelAttrAddr(String sAttrId) {
/* 172 */     return (int[])this.excelAttrAddrMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public void setExcelAttrAddr(String sAttrId, int colIndex, int rowIndex) {
/* 176 */     this.excelAttrAddrMap.put(sAttrId, new int[] { colIndex, rowIndex });
/*     */   }
/*     */ 
/*     */   public String getAttrErrorDetail(String sAttrId)
/*     */   {
/* 181 */     return (String)this.attrErrorDetailMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public void setAttrErrorDetail(String sAttrId, String sErrorInfo) {
/* 185 */     this.attrErrorDetailMap.put(sAttrId, sErrorInfo);
/*     */   }
/*     */ 
/*     */   public List<String> getAllAttrList() {
/* 189 */     return this.allAttrList;
/*     */   }
/*     */ 
/*     */   public List<String> getAllRelatedAttr() {
/* 193 */     return this.allRelatedAttr;
/*     */   }
/*     */ 
/*     */   public void addAttrToList(String sAttrId) {
/* 197 */     this.allAttrList.add(sAttrId);
/*     */   }
/*     */ 
/*     */   public void addRelatedAttrToList(String sAttrId) {
/* 201 */     this.allRelatedAttr.add(sAttrId);
/*     */   }
/*     */ 
/*     */   public Map<String, String[]> getUniqueAttrIds() {
/* 205 */     return this.uniqueAttrIds;
/*     */   }
/*     */ 
/*     */   public void setUniqueAttrIds(Map<String, String[]> uniqueAttrIds) {
/* 209 */     this.uniqueAttrIds = uniqueAttrIds;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getRelatedNoClassAttrValueMap()
/*     */   {
/* 217 */     return this.relatedNoClassAttrValueMap;
/*     */   }
/*     */ 
/*     */   public String getRelatedNoClassAttrValue(String sAttrId) {
/* 221 */     return (String)this.relatedNoClassAttrValueMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public void setRelatedNoClassAttrValueMap(Map<String, String> relatedNoClassAttrValueMap) {
/* 225 */     this.relatedNoClassAttrValueMap = relatedNoClassAttrValueMap;
/*     */   }
/*     */ 
/*     */   public void setRelatedNoClassAttrValue(String sAttrId, String sValue) {
/* 229 */     this.relatedNoClassAttrValueMap.put(sAttrId, sValue);
/* 230 */     if (this.relatedNoClassAttrValueMap.get(sAttrId) != null)
/* 231 */       this.relatedNoClassAttrLabelMap.put(sAttrId, sValue);
/*     */   }
/*     */ 
/*     */   public String getRelatedNoClassAttrLabel(String sAttrId)
/*     */   {
/* 236 */     return (String)this.relatedNoClassAttrLabelMap.get(sAttrId);
/*     */   }
/*     */ 
/*     */   public void setCuid(String cuid) {
/* 240 */     this.cuid = cuid;
/*     */   }
/*     */ 
/*     */   public String getCuid() {
/* 244 */     return this.cuid;
/*     */   }
/*     */ 
/*     */   public void setUniqueAttrFoundList(Map<String, Map<String, String>> uniqueAttrFoundList) {
/* 248 */     this.uniqueAttrFoundList = uniqueAttrFoundList;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, String>> getUniqueAttrFoundList() {
/* 252 */     return this.uniqueAttrFoundList;
/*     */   }
/*     */ 
/*     */   public static class DATA_TYPE
/*     */   {
/*  66 */     public static String DATA_TYPE_ERROR = "无效";
/*  67 */     public static String DATA_TYPE_ADD = "新增";
/*  68 */     public static String DATA_TYPE_MODIFY = "修改";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta
 * JD-Core Version:    0.6.0
 */