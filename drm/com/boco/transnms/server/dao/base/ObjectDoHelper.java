/*    */ package com.boco.transnms.server.dao.base;
/*    */ 
/*    */ import com.boco.transnms.common.dto.base.DataObjectList;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import com.cmcc.tm.middleware.util.IResourceObject;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class ObjectDoHelper
/*    */ {
/*    */   public static void setFullObject(GenericDO genericDo, IResourceObject ompObject)
/*    */   {
/* 15 */     genericDo.setObjectNum(ompObject.getObjectID());
/* 16 */     genericDo.setClassName(ompObject.getClassName());
/* 17 */     genericDo.setObjectKey(ompObject.getKey());
/* 18 */     genericDo.setCreateTime(ompObject.getCreateTime());
/* 19 */     genericDo.setLastModifyTime(ompObject.getLastModifyTime());
/*    */   }
/*    */ 
/*    */   public static void setFullObject(GenericDO genericDo, GenericDO ompObject) {
/* 23 */     genericDo.setObjectNum(ompObject.getObjectNum());
/* 24 */     genericDo.setClassName(ompObject.getClassName());
/* 25 */     genericDo.setObjectKey(ompObject.getObjectKey());
/* 26 */     genericDo.setCreateTime(ompObject.getCreateTime());
/* 27 */     genericDo.setLastModifyTime(ompObject.getLastModifyTime());
/*    */   }
/*    */ 
/*    */   public static void setSimpleObject(GenericDO genericDo, IResourceObject ompObject) {
/* 31 */     genericDo.setObjectNum(ompObject.getObjectID());
/* 32 */     genericDo.setClassName(ompObject.getClassName());
/* 33 */     genericDo.setObjectKey(ompObject.getKey());
/*    */   }
/*    */ 
/*    */   public static void cloneDataObject(GenericDO genericDo, int objType, IResourceObject ompObject) {
/* 37 */     if (ompObject != null)
/* 38 */       if (objType == 0) {
/* 39 */         setFullObject(genericDo, ompObject);
/* 40 */         genericDo.setAttrValues(ompObject.getAttributes());
/* 41 */       } else if (objType == 1) {
/* 42 */         setSimpleObject(genericDo, ompObject);
/* 43 */         String[] fields = ompObject.getAttrNames();
/* 44 */         for (int i = 0; (fields != null) && (i < fields.length); i++)
/* 45 */           genericDo.setAttrValue(fields[i], ompObject.getAttrValue(fields[i]));
/*    */       }
/* 47 */       else if (objType == 2) {
/* 48 */         setSimpleObject(genericDo, ompObject);
/*    */       }
/*    */   }
/*    */ 
/*    */   public static void cloneDataObject(GenericDO genericDo, GenericDO ompObject)
/*    */   {
/* 54 */     setFullObject(genericDo, ompObject);
/* 55 */     genericDo.setAttrValues(ompObject.getAllAttr());
/*    */   }
/*    */ 
/*    */   public static DataObjectList cloneDataObjList(GenericDO dboTemplate, int objType, List<GenericDO> ompObjList) throws Exception {
/* 59 */     DataObjectList dboList = new DataObjectList();
/* 60 */     for (int i = 0; (ompObjList != null) && (i < ompObjList.size()); i++) {
/* 61 */       GenericDO ompObj = (GenericDO)ompObjList.get(i);
/* 62 */       GenericDO dbo = (GenericDO)dboTemplate.getClass().newInstance();
/* 63 */       dbo.setObjectLoadType(dboTemplate.getObjectLoadType());
/* 64 */       cloneDataObject(dbo, ompObj);
/* 65 */       dboList.add(dbo);
/*    */     }
/* 67 */     return dboList;
/*    */   }
/*    */ 
/*    */   public static DataObjectList cloneDataObjArray(GenericDO dboTemplate, int objType, Object[] ompObjs) throws Exception {
/* 71 */     DataObjectList dboList = new DataObjectList();
/* 72 */     for (int i = 0; i < ompObjs.length; i++) {
/* 73 */       IResourceObject ompObj = (IResourceObject)ompObjs[i];
/* 74 */       GenericDO dbo = (GenericDO)dboTemplate.getClass().newInstance();
/* 75 */       cloneDataObject(dbo, objType, ompObj);
/* 76 */       dboList.add(dbo);
/*    */     }
/* 78 */     return dboList;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.ObjectDoHelper
 * JD-Core Version:    0.6.0
 */