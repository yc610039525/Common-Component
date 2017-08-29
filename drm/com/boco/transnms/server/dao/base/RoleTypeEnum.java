/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.transnms.server.dao.base.internal.ResObjectRoleInRelation;
/*    */ 
/*    */ public class RoleTypeEnum
/*    */ {
/*    */   public static final int FROM_LEFT = 1;
/*    */   public static final int TO_RIGHT = 2;
/*    */   public static final int ALL = 3;
/*    */ 
/*    */   public static ResObjectRoleInRelation fromGoatInt(int value)
/*    */   {
/* 49 */     ResObjectRoleInRelation role = null;
/* 50 */     switch (value) {
/*    */     case 3:
/* 52 */       role = ResObjectRoleInRelation.ALL;
/* 53 */       break;
/*    */     case 2:
/* 55 */       role = ResObjectRoleInRelation.TO_RIGHT;
/* 56 */       break;
/*    */     case 1:
/* 58 */       role = ResObjectRoleInRelation.FROM_LEFT;
/*    */     }
/*    */ 
/* 61 */     return role;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.RoleTypeEnum
 * JD-Core Version:    0.6.0
 */