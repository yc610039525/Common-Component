/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import com.cmcc.tm.middleware.util.IResourceObjectType;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ResourceObjectType
/*    */   implements Serializable, IResourceObjectType
/*    */ {
/*    */   private static final long serialVersionUID = 7940366160798785297L;
/* 35 */   public static ResourceObjectType FULL_OMPOBJECT = new ResourceObjectType(0);
/*    */ 
/* 37 */   public static ResourceObjectType SIMPLE_OMPOBJECT = new ResourceObjectType(1);
/*    */ 
/* 40 */   public static ResourceObjectType MINI_OMPOBJECT = new ResourceObjectType(2);
/*    */   private int A;
/*    */ 
/*    */   public ResourceObjectType(int i)
/*    */   {
/* 19 */     this.A = 0;
/* 20 */     this.A = i;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 24 */     return super.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj) {
/* 28 */     if ((obj instanceof ResourceObjectType)) {
/* 29 */       ResourceObjectType ompobjectgettype = (ResourceObjectType)obj;
/* 30 */       return ompobjectgettype.A == this.A;
/*    */     }
/* 32 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.ResourceObjectType
 * JD-Core Version:    0.6.0
 */