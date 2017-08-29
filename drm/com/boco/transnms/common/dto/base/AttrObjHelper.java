/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ public class AttrObjHelper
/*    */ {
/*    */   public static void sort(List list, String sortAttrName, boolean isAscend)
/*    */   {
/* 28 */     if (list.size() == 0) return;
/*    */ 
/* 30 */     CompareAttrObj[] compareDOs = new CompareAttrObj[list.size()];
/* 31 */     for (int i = 0; i < compareDOs.length; i++) {
/* 32 */       CompareAttrObj compareDO = new CompareAttrObj(isAscend, (IAttrObject)list.get(i), sortAttrName);
/* 33 */       if (!compareDO.isComparable()) break;
/* 34 */       compareDOs[i] = compareDO;
/*    */     }
/*    */ 
/* 37 */     if ((compareDOs.length > 0) && (compareDOs[0] != null)) {
/* 38 */       Arrays.sort(compareDOs);
/* 39 */       list.clear();
/* 40 */       for (int i = 0; i < compareDOs.length; i++)
/* 41 */         list.add(compareDOs[i].getDO());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AttrObjHelper
 * JD-Core Version:    0.6.0
 */