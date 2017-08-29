/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class AttrObjCollection<E extends IAttrObject>
/*    */   implements Serializable
/*    */ {
/* 26 */   private List<AttrObjMap<E>> rows = new ArrayList();
/*    */ 
/*    */   public E getAttrField(String className, int rowNum)
/*    */   {
/* 32 */     AttrObjMap row = (AttrObjMap)this.rows.get(rowNum);
/* 33 */     IAttrObject attr = (IAttrObject)row.get(className);
/* 34 */     return attr;
/*    */   }
/*    */ 
/*    */   public AttrObjMap<E> getAttrRow(int rowNum) {
/* 38 */     return (AttrObjMap)this.rows.get(rowNum);
/*    */   }
/*    */ 
/*    */   public void insertAttrField(int rowNum, E field) {
/* 42 */     AttrObjMap row = null;
/* 43 */     if (rowNum < this.rows.size()) {
/* 44 */       row = (AttrObjMap)this.rows.get(rowNum);
/*    */     } else {
/* 46 */       row = new AttrObjMap();
/* 47 */       this.rows.add(row);
/*    */     }
/* 49 */     row.put(field.getClassName(), field);
/*    */   }
/*    */ 
/*    */   public int size() {
/* 53 */     return this.rows.size();
/*    */   }
/*    */ 
/*    */   protected List getRows() {
/* 57 */     return this.rows;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AttrObjCollection
 * JD-Core Version:    0.6.0
 */