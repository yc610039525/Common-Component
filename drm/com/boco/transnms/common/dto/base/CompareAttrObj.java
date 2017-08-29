/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class CompareAttrObj
/*    */   implements Comparable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private boolean isAscend;
/*    */   private IAttrObject dbo;
/*    */   private String sortAttrName;
/*    */ 
/*    */   public CompareAttrObj(boolean isAscend, IAttrObject dbo, String sortAttrName)
/*    */   {
/* 27 */     this.dbo = dbo;
/* 28 */     this.isAscend = isAscend;
/* 29 */     this.sortAttrName = sortAttrName;
/*    */   }
/*    */ 
/*    */   public IAttrObject getDO() {
/* 33 */     return this.dbo;
/*    */   }
/*    */ 
/*    */   public Object getSortAttr() {
/* 37 */     return this.dbo.getAttrValue(this.sortAttrName);
/*    */   }
/*    */ 
/*    */   public boolean isComparable() {
/* 41 */     Object sortAttr = getSortAttr();
/* 42 */     boolean isComparable = true;
/* 43 */     if (sortAttr != null) {
/* 44 */       isComparable = sortAttr instanceof Comparable;
/*    */     }
/* 46 */     return isComparable;
/*    */   }
/*    */ 
/*    */   public int compareTo(Object obj) {
/* 50 */     int compare = 0;
/* 51 */     Comparable source = (Comparable)getSortAttr();
/* 52 */     Comparable target = (Comparable)((CompareAttrObj)obj).getSortAttr();
/* 53 */     if ((source != null) && (target != null))
/* 54 */       compare = this.isAscend ? source.compareTo(target) : target.compareTo(source);
/* 55 */     else if (source != null)
/* 56 */       compare = this.isAscend ? 1 : -1;
/* 57 */     else if (target != null) {
/* 58 */       compare = this.isAscend ? -1 : 1;
/*    */     }
/* 60 */     return compare;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.CompareAttrObj
 * JD-Core Version:    0.6.0
 */