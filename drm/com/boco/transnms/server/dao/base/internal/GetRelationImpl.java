/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class GetRelationImpl
/*    */ {
/*    */   private static final String CS_SCHEMA_NAME = "GT_REL_";
/*    */ 
/*    */   private void getAttrStrByObject(StringBuffer queryString, Set attrSet, String source)
/*    */   {
/* 16 */     queryString.append("select distinct ");
/* 17 */     for (Iterator it = attrSet.iterator(); it.hasNext(); ) {
/* 18 */       String name = (String)it.next();
/* 19 */       queryString.append(source + "." + name);
/* 20 */       if (it.hasNext())
/* 21 */         queryString.append(",");
/*    */     }
/*    */   }
/*    */ 
/*    */   private String getSchemaClassName(String name)
/*    */   {
/* 27 */     return "GT_REL_" + name;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.GetRelationImpl
 * JD-Core Version:    0.6.0
 */