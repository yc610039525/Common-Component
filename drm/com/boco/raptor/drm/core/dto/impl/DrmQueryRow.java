/*    */ package com.boco.raptor.drm.core.dto.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*    */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DrmQueryRow
/*    */   implements IDrmQueryRow
/*    */ {
/* 28 */   private Map<String, IDrmDataObject> resultRow = new HashMap();
/*    */ 
/*    */   public Map<String, IDrmDataObject> getDboRow()
/*    */   {
/* 34 */     return this.resultRow;
/*    */   }
/*    */ 
/*    */   public IDrmDataObject getResultDbo(String dbClassId) {
/* 38 */     return (IDrmDataObject)this.resultRow.get(dbClassId);
/*    */   }
/*    */ 
/*    */   public <T> T getResAttrValue(String dbClassId, String attrId) {
/* 42 */     IDrmDataObject dbo = getResultDbo(dbClassId);
/* 43 */     if (dbo != null) {
/* 44 */       return dbo.getAttrValue(attrId);
/*    */     }
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   public void setResultRow(Map<String, IDrmDataObject> resultRow) {
/* 50 */     this.resultRow = resultRow;
/*    */   }
/*    */ 
/*    */   public void addResultDbo(IDrmDataObject dbo) {
/* 54 */     this.resultRow.put(dbo.getDbClassId(), dbo);
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getAttrRow() {
/* 58 */     Map attrRow = new HashMap();
/* 59 */     Iterator it = this.resultRow.keySet().iterator();
/* 60 */     while (it.hasNext()) {
/* 61 */       String bmClassId = (String)it.next();
/* 62 */       IDrmDataObject dbo = (IDrmDataObject)this.resultRow.get(bmClassId);
/* 63 */       String[] attrIds = dbo.getAllAttrId();
/* 64 */       for (String attrId : attrIds) {
/* 65 */         attrRow.put(bmClassId + "." + attrId, dbo.getAttrValue(attrId));
/*    */       }
/* 67 */       if (dbo.getDboId().longValue() > 0L) {
/* 68 */         attrRow.put(bmClassId + ".OBJECTID", dbo.getDboId());
/*    */       }
/*    */     }
/* 71 */     return attrRow;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.DrmQueryRow
 * JD-Core Version:    0.6.0
 */