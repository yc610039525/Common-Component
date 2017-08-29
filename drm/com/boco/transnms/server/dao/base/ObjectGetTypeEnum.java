/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.transnms.server.dao.base.internal.ResourceObjectType;
/*    */ 
/*    */ public class ObjectGetTypeEnum
/*    */ {
/*    */   public static final int MINI = 2;
/*    */   public static final int SIMPLE = 1;
/*    */   public static final int FULL = 0;
/*    */ 
/*    */   public static ResourceObjectType fromGoatInt(int value)
/*    */   {
/* 48 */     ResourceObjectType objType = null;
/* 49 */     switch (value) {
/*    */     case 2:
/* 51 */       objType = ResourceObjectType.MINI_OMPOBJECT;
/* 52 */       break;
/*    */     case 1:
/* 54 */       objType = ResourceObjectType.SIMPLE_OMPOBJECT;
/* 55 */       break;
/*    */     case 0:
/* 57 */       objType = ResourceObjectType.FULL_OMPOBJECT;
/*    */     }
/*    */ 
/* 60 */     return objType;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.ObjectGetTypeEnum
 * JD-Core Version:    0.6.0
 */