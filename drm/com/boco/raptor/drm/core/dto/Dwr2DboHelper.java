/*    */ package com.boco.raptor.drm.core.dto;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.sql.Timestamp;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class Dwr2DboHelper
/*    */ {
/*    */   public static Map<String, Object> dwr2dboMap(GenericDO dboTemplate, Map<String, Object> dwrAttrValues)
/*    */     throws UserException
/*    */   {
/* 16 */     Map dboAttrValues = new HashMap();
/* 17 */     Iterator it = dwrAttrValues.keySet().iterator();
/* 18 */     if (dboTemplate.getClass() == GenericDO.class) {
/* 19 */       return dwrAttrValues;
/*    */     }
/*    */ 
/* 22 */     while (it.hasNext()) {
/* 23 */       String attrId = (String)it.next();
/* 24 */       Class attrType = dboTemplate.getAttrType(attrId);
/* 25 */       Object dwrValue = dwrAttrValues.get(attrId);
/* 26 */       if ((attrType == null) || (dwrValue == null)) {
/* 27 */         dboAttrValues.put(attrId, dwrValue);
/* 28 */       } else if ((dwrValue.getClass() == String.class) && (attrType != String.class) && (((String)dwrValue).trim().length() == 0))
/*    */       {
/* 30 */         dboAttrValues.put(attrId, null);
/*    */       } else {
/* 32 */         Object dboValue = dwrValue;
/*    */         try {
/* 34 */           if (attrType == Double.TYPE) {
/* 35 */             if (dwrValue.getClass() == String.class)
/* 36 */               dboValue = new Double((String)dwrValue);
/*    */           }
/* 38 */           else if (attrType == Long.TYPE) {
/* 39 */             if (dwrValue.getClass() == String.class)
/* 40 */               dboValue = new Long((String)dwrValue);
/*    */           }
/* 42 */           else if (attrType == Timestamp.class) {
/* 43 */             if (dwrValue.getClass() == String.class) {
/* 44 */               Long time = new Long((String)dwrValue);
/* 45 */               dboValue = new Timestamp(time.longValue());
/*    */             }
/* 47 */           } else if ((attrType == Boolean.TYPE) && 
/* 48 */             (dwrValue.getClass() == String.class)) {
/* 49 */             String _dwrValue = (String)dwrValue;
/* 50 */             if ((_dwrValue.trim().equalsIgnoreCase("true")) || (_dwrValue.trim().equalsIgnoreCase("false")))
/* 51 */               dboValue = Boolean.valueOf(_dwrValue.trim().equalsIgnoreCase("true"));
/* 52 */             else if ((_dwrValue.indexOf("0") >= 0) || (_dwrValue.indexOf("1") >= 0))
/* 53 */               dboValue = Boolean.valueOf(!"0".equals(dwrValue));
/*    */           }
/*    */         }
/*    */         catch (Exception ex)
/*    */         {
/* 58 */           throw new UserException("attrId=" + attrId + ", attrValue=" + dwrValue + ", 数据值转化错误 ！");
/*    */         }
/* 60 */         dboAttrValues.put(attrId, dboValue);
/*    */       }
/*    */     }
/* 63 */     return dboAttrValues;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.Dwr2DboHelper
 * JD-Core Version:    0.6.0
 */