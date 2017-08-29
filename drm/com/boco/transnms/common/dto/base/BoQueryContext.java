/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class BoQueryContext extends BoActionContext
/*     */   implements IBoQueryContext, IDrmQueryContext
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public BoQueryContext()
/*     */   {
/*     */   }
/*     */ 
/*     */   public BoQueryContext(boolean queryDeleted)
/*     */   {
/*  43 */     setQueryDeleted(queryDeleted);
/*     */   }
/*     */ 
/*     */   public BoQueryContext(int offset, int fetchSize, boolean countBeforQuery) {
/*  47 */     this(offset, fetchSize, countBeforQuery, null, false);
/*     */   }
/*     */ 
/*     */   public BoQueryContext(int offset, int fetchSize, boolean countBeforQuery, String orderField, boolean orderDesc)
/*     */   {
/*  52 */     setOffset(offset);
/*  53 */     setFetchSize(fetchSize);
/*  54 */     setCountBeforQuery(countBeforQuery);
/*  55 */     setOrderField(orderField);
/*  56 */     setOrderDesc(orderDesc);
/*     */   }
/*     */ 
/*     */   public void setOffset(int offset) {
/*  60 */     super.setAttrValue("offset", offset);
/*     */   }
/*     */ 
/*     */   public void setFetchSize(int fetchSize) {
/*  64 */     super.setAttrValue("fetchSize", fetchSize);
/*     */   }
/*     */ 
/*     */   public void setOrderField(String orderField) {
/*  68 */     super.setAttrValue("orderField", orderField);
/*     */   }
/*     */ 
/*     */   public void setCountBeforQuery(boolean countBeforQuery) {
/*  72 */     super.setAttrValue("countBeforQuery", countBeforQuery);
/*     */   }
/*     */ 
/*     */   public void setQueryCountOnly(boolean queryCountOnly) {
/*  76 */     super.setAttrValue("queryCountOnly", queryCountOnly);
/*     */   }
/*     */ 
/*     */   public void setOrderDesc(boolean orderDesc) {
/*  80 */     super.setAttrValue("orderDesc", orderDesc);
/*     */   }
/*     */ 
/*     */   public void setQueryDeleted(boolean queryDeleted) {
/*  84 */     super.setAttrValue("queryDeleted", queryDeleted);
/*     */   }
/*     */   public int getOffset() {
/*  87 */     return super.getAttrInt("offset", 0);
/*     */   }
/*     */ 
/*     */   public int getFetchSize() {
/*  91 */     return super.getAttrInt("fetchSize", 0);
/*     */   }
/*     */ 
/*     */   public String getOrderField() {
/*  95 */     return super.getAttrString("orderField");
/*     */   }
/*     */ 
/*     */   public boolean isCountBeforQuery() {
/*  99 */     return super.getAttrBool("countBeforQuery", false);
/*     */   }
/*     */ 
/*     */   public boolean isQueryCountOnly() {
/* 103 */     return super.getAttrBool("queryCountOnly", false);
/*     */   }
/*     */ 
/*     */   public boolean isOrderDesc() {
/* 107 */     return super.getAttrBool("orderDesc", false);
/*     */   }
/*     */ 
/*     */   public boolean isByPage() {
/* 111 */     return getFetchSize() > 0;
/*     */   }
/*     */ 
/*     */   public boolean isQueryDeleted() {
/* 115 */     return super.getAttrBool("queryDeleted", false);
/*     */   }
/*     */ 
/*     */   public boolean isPopulate()
/*     */   {
/* 120 */     return super.getAttrBool("populate", false);
/*     */   }
/*     */ 
/*     */   public void setPopulate(boolean isPopulate) {
/* 124 */     super.setAttrValue("populate", isPopulate);
/*     */   }
/*     */ 
/*     */   public boolean isFillDisplayLabel() {
/* 128 */     return super.getAttrBool("fillDisplayLabel", false);
/*     */   }
/*     */ 
/*     */   public void setFillDisplayLabel(boolean displayLabel) {
/* 132 */     super.setAttrValue("fillDisplayLabel", displayLabel);
/*     */   }
/*     */ 
/*     */   public boolean isEntity() {
/* 136 */     return super.getAttrBool("entity", false);
/*     */   }
/*     */ 
/*     */   public void setEntity(boolean isEntity) {
/* 140 */     super.setAttrValue("entity", isEntity);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 144 */     String hashcode = "";
/* 145 */     hashcode = hashcode + getUserId() + "-";
/* 146 */     hashcode = hashcode + getOrderField() + "-";
/* 147 */     hashcode = hashcode + getFetchSize() + "-";
/* 148 */     hashcode = hashcode + getOffset() + "-";
/* 149 */     hashcode = hashcode + isEntity() + "-";
/* 150 */     hashcode = hashcode + isPopulate() + "-";
/* 151 */     hashcode = hashcode + isCountBeforQuery() + "-";
/* 152 */     hashcode = hashcode + isEntity() + "-";
/* 153 */     hashcode = hashcode + isQueryDeleted() + "-";
/* 154 */     return hashcode.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 158 */     return (obj != null) && (hashCode() == obj.hashCode());
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 162 */     BoQueryContext query1 = new BoQueryContext(0, 20, true, "", true);
/* 163 */     int code1 = query1.hashCode();
/* 164 */     BoQueryContext query2 = new BoQueryContext(0, 20, true, "", true);
/* 165 */     int code2 = query2.hashCode();
/* 166 */     HashMap map = new HashMap();
/* 167 */     map.put(query1, "");
/* 168 */     map.put(query2, "");
/* 169 */     if (code1 == code2)
/* 170 */       System.out.println("一致的");
/*     */   }
/*     */ 
/*     */   private static class AttrName
/*     */   {
/*     */     private static final String offset = "offset";
/*     */     private static final String fetchSize = "fetchSize";
/*     */     private static final String orderField = "orderField";
/*     */     private static final String countBeforQuery = "countBeforQuery";
/*     */     private static final String orderDesc = "orderDesc";
/*     */     private static final String populate = "populate";
/*     */     private static final String entity = "entity";
/*     */     private static final String fillDisplayLabel = "fillDisplayLabel";
/*     */     private static final String queryDeleted = "queryDeleted";
/*     */     private static final String queryCountOnly = "queryCountOnly";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.BoQueryContext
 * JD-Core Version:    0.6.0
 */