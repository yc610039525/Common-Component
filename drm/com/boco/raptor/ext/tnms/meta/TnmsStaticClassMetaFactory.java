/*    */ package com.boco.raptor.ext.tnms.meta;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*    */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*    */ import com.boco.raptor.drm.core.meta.IStaticClassMetaFactory;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class TnmsStaticClassMetaFactory
/*    */   implements IStaticClassMetaFactory
/*    */ {
/* 31 */   private Map<String, BMClassMeta> dboTemplates = new HashMap();
/*    */   private static final String DTO_CLASS_PREFIX = "com.boco.transnms.common.dto.";
/* 33 */   private Map<String, String> bmActionAdaptor = null;
/*    */ 
/*    */   public BMClassMeta getClassMeta(String bmClassId)
/*    */   {
/* 39 */     BMClassMeta classMeta = (BMClassMeta)this.dboTemplates.get(bmClassId);
/* 40 */     if (classMeta == null) {
/*    */       try {
/* 42 */         String className = FormatClassName(bmClassId);
/* 43 */         String classFullName = "com.boco.transnms.common.dto." + className;
/* 44 */         GenericDO dboTemplate = (GenericDO)Class.forName(classFullName).newInstance();
/* 45 */         dboTemplate.setBmClassId(bmClassId);
/* 46 */         classMeta = createClassMeta(dboTemplate);
/* 47 */         classMeta.setEntityClassName(classFullName);
/* 48 */         this.dboTemplates.put(bmClassId, classMeta);
/*    */       } catch (Exception ex) {
/* 50 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */     }
/* 53 */     return classMeta;
/*    */   }
/*    */ 
/*    */   private BMClassMeta createClassMeta(GenericDO dboTemplate) {
/* 57 */     String bmClassId = dboTemplate.getBmClassId();
/*    */ 
/* 59 */     BMClassMeta classMeta = new BMClassMeta();
/* 60 */     classMeta.setBmClassId(bmClassId);
/* 61 */     classMeta.setDbClassId(bmClassId);
/* 62 */     classMeta.setLabelCn(dboTemplate.getBmClassId());
/* 63 */     classMeta.setDynClass(false);
/*    */ 
/* 65 */     String[] allAttrNames = dboTemplate.getAllAttrNames();
/* 66 */     for (String attrId : allAttrNames) {
/* 67 */       if ("LABEL_CN".equals(attrId)) {
/* 68 */         classMeta.setLabelAttrId("LABEL_CN");
/*    */       }
/* 70 */       BMAttrMeta attrMeta = new BMAttrMeta(bmClassId, attrId, attrId);
/* 71 */       Class attrClassType = dboTemplate.getAttrType(attrId);
/* 72 */       attrMeta.setAttrClassType(attrClassType);
/* 73 */       classMeta.addAttrMeta(attrMeta);
/* 74 */       if (this.bmActionAdaptor != null) {
/* 75 */         String actionAdaptorClassName = (String)this.bmActionAdaptor.get(bmClassId);
/* 76 */         if (actionAdaptorClassName != null) {
/* 77 */           classMeta.setActionAdaptorClassName(actionAdaptorClassName);
/*    */         }
/*    */       }
/*    */     }
/* 81 */     return classMeta;
/*    */   }
/*    */ 
/*    */   public static String FormatClassName(String classId) {
/* 85 */     String[] _classId = classId.split("_");
/* 86 */     String className = "";
/* 87 */     for (int i = 0; i < _classId.length; i++) {
/* 88 */       className = className + _classId[i].substring(0, 1).toUpperCase() + _classId[i].substring(1, _classId[i].length()).toLowerCase();
/*    */     }
/*    */ 
/* 91 */     return className;
/*    */   }
/*    */ 
/*    */   public void setBmActionAdaptor(Map<String, String> bmActionAdaptor) {
/* 95 */     this.bmActionAdaptor = bmActionAdaptor;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.ext.tnms.meta.TnmsStaticClassMetaFactory
 * JD-Core Version:    0.6.0
 */