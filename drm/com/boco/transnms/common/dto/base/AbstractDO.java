/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import com.boco.common.util.db.IDbModel;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class AbstractDO extends AttrObject
/*    */   implements Cloneable, IDbModel
/*    */ {
/*    */   public void setObjectId(String objectId)
/*    */   {
/* 33 */     if ((objectId != null) && (objectId.trim().length() > 0))
/* 34 */       getCoreAttrMap().put("OBJECTID", new Long(objectId.trim()));
/*    */   }
/*    */ 
/*    */   public String getObjectId()
/*    */   {
/* 39 */     Long value = convObjectId();
/* 40 */     if (value != null) {
/* 41 */       return value.toString();
/*    */     }
/* 43 */     return "0";
/*    */   }
/*    */ 
/*    */   public void setObjectNum(long objectId)
/*    */   {
/* 48 */     if (objectId > 0L)
/* 49 */       getCoreAttrMap().put("OBJECTID", new Long(objectId));
/*    */   }
/*    */ 
/*    */   public long getObjectNum()
/*    */   {
/* 54 */     Long value = convObjectId();
/* 55 */     if (value != null) {
/* 56 */       return value.longValue();
/*    */     }
/* 58 */     return 0L;
/*    */   }
/*    */ 
/*    */   private Long convObjectId()
/*    */   {
/* 63 */     Object value = getCoreAttrMap().get("OBJECTID");
/* 64 */     Long objectId = null;
/* 65 */     if ((value != null) && ((value instanceof String))) {
/* 66 */       objectId = new Long((String)value);
/* 67 */       getCoreAttrMap().put("OBJECTID", objectId);
/*    */     } else {
/* 69 */       objectId = (Long)getCoreAttrMap().get("OBJECTID");
/*    */     }
/* 71 */     return objectId;
/*    */   }
/*    */ 
/*    */   public String getTableName() {
/* 75 */     return getClassName();
/*    */   }
/*    */ 
/*    */   public static class AttrName
/*    */   {
/*    */     public static final String objectId = "OBJECTID";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.AbstractDO
 * JD-Core Version:    0.6.0
 */