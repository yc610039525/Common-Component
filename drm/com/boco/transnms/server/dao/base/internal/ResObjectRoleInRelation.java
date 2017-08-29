/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import com.cmcc.tm.middleware.util.IResObjectRoleInRelation;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ResObjectRoleInRelation
/*    */   implements Serializable, IResObjectRoleInRelation
/*    */ {
/*    */   private static final long serialVersionUID = 2917027538908854355L;
/* 26 */   public static int FROM = 1;
/* 27 */   public static int TO = 2;
/* 28 */   public static int ALL_FROM_TO = 3;
/*    */ 
/* 38 */   public static ResObjectRoleInRelation FROM_LEFT = new ResObjectRoleInRelation(1);
/* 39 */   public static ResObjectRoleInRelation TO_RIGHT = new ResObjectRoleInRelation(2);
/* 40 */   public static ResObjectRoleInRelation ALL = new ResObjectRoleInRelation(3);
/*    */   private int A;
/*    */ 
/*    */   public ResObjectRoleInRelation(int i)
/*    */   {
/* 30 */     this.A = 0;
/* 31 */     this.A = i;
/*    */   }
/*    */ 
/*    */   public int getType() {
/* 35 */     return this.A;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.ResObjectRoleInRelation
 * JD-Core Version:    0.6.0
 */