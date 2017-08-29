/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class AttrObjList<E extends IAttrObject> extends ArrayList<E>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public AttrObjList()
/*    */   {
/*    */   }
/*    */ 
/*    */   public AttrObjList(E[] attrObjs)
/*    */   {
/* 29 */     for (int i = 0; i < attrObjs.length; i++)
/* 30 */       super.add(attrObjs[i]);
/*    */   }
/*    */ 
/*    */   public void sort(String sortAttrName, boolean isAscend)
/*    */   {
/* 35 */     AttrObjHelper.sort(this, sortAttrName, isAscend);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AttrObjList
 * JD-Core Version:    0.6.0
 */