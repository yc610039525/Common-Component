/*    */ package com.boco.raptor.ext.tnms.plugin.adaptor;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.common.service.IServiceActionContext;
/*    */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*    */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*    */ import com.boco.raptor.drm.core.plugin.IActionAdaptor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class AbstractBoAdaptor
/*    */   implements IActionAdaptor
/*    */ {
/*    */   public List<IDrmDataObject> addDynObjects(IServiceActionContext actionContext, List<IDrmDataObject> dros, boolean isValidate, BMClassMeta classMeta)
/*    */     throws UserException
/*    */   {
/* 19 */     List result = new ArrayList();
/* 20 */     for (IDrmDataObject dro : dros) {
/*    */       try {
/* 22 */         IDrmDataObject fullDbo = addDynObject(actionContext, dro, isValidate, classMeta);
/* 23 */         result.add(fullDbo);
/*    */       } catch (Exception ex) {
/* 25 */         throw new UserException(ex.getMessage());
/*    */       }
/*    */     }
/* 28 */     return result;
/*    */   }
/*    */ 
/*    */   public Map<String, String> deleteDynObjects(IServiceActionContext actionContext, List<IDrmDataObject> dros, boolean isValidate, BMClassMeta classMeta) throws UserException {
/* 32 */     Map returnList = new LinkedHashMap();
/* 33 */     for (IDrmDataObject dro : dros) {
/*    */       try {
/* 35 */         deleteDynObject(actionContext, dro, isValidate, classMeta);
/*    */       } catch (Exception ex) {
/* 37 */         returnList.put(dro.getCuid(), ex.getMessage());
/*    */       }
/*    */     }
/*    */ 
/* 41 */     return returnList;
/*    */   }
/*    */ 
/*    */   public void modifyDynObjects(IServiceActionContext actionContext, List<IDrmDataObject> dros, Map<String, Object> modifyAttrs, boolean isValidate, BMClassMeta classMeta) throws UserException {
/* 45 */     for (IDrmDataObject dro : dros)
/*    */       try {
/* 47 */         modifyDynObject(actionContext, dro, isValidate, classMeta);
/*    */       } catch (Exception ex) {
/* 49 */         throw new UserException(ex.getMessage());
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.ext.tnms.plugin.adaptor.AbstractBoAdaptor
 * JD-Core Version:    0.6.0
 */