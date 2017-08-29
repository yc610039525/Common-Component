/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ public class DummyDOHelper
/*    */ {
/*    */   public static void convAllDOToDummy(DataObjectList list)
/*    */   {
/* 23 */     for (int i = 0; i < list.size(); i++)
/* 24 */       list.set(i, new DummyDO((GenericDO)list.get(i)));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DummyDOHelper
 * JD-Core Version:    0.6.0
 */