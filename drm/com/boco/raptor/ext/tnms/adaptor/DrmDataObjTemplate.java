/*    */ package com.boco.raptor.ext.tnms.adaptor;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.impl.DrmDataObject;
/*    */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*    */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class DrmDataObjTemplate extends GenericDO
/*    */ {
/*    */   private String bmClassId;
/* 32 */   private Map<String, Class> attrClassTypes = new HashMap();
/*    */ 
/* 34 */   public DrmDataObjTemplate(BMClassMeta classMeta) { this.bmClassId = classMeta.getBmClassId();
/* 35 */     super.setClassName(classMeta.getDbClassId());
/* 36 */     Map attrMetas = classMeta.getAllAttrMetas();
/* 37 */     Iterator it = attrMetas.values().iterator();
/* 38 */     while (it.hasNext()) {
/* 39 */       BMAttrMeta attrMeta = (BMAttrMeta)it.next();
/* 40 */       this.attrClassTypes.put(attrMeta.getAttrId(), attrMeta.getAttrClassType());
/*    */     }
/*    */   }
/*    */ 
/*    */   public GenericDO cloneDboClass()
/*    */   {
/* 46 */     DrmDataObject dbo = new DrmDataObject();
/* 47 */     dbo.setClassName(getClassName());
/* 48 */     dbo.setBmClassId(this.bmClassId);
/* 49 */     return dbo;
/*    */   }
/*    */ 
/*    */   public Class getAttrType(String attrName) {
/* 53 */     return (Class)this.attrClassTypes.get(attrName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.ext.tnms.adaptor.DrmDataObjTemplate
 * JD-Core Version:    0.6.0
 */