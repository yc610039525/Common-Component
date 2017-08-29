/*    */ package com.boco.raptor.drm.ext.service.vm;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.common.service.IServiceActionContext;
/*    */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
/*    */ import com.boco.raptor.drm.core.meta.TemplateMeta;
/*    */ import com.boco.raptor.drm.core.service.vm.IVMModelServiceDAO;
/*    */ import com.boco.transnms.common.dto.base.DataObjectList;
/*    */ import com.boco.transnms.server.dao.base.AbstractDAO;
/*    */ import java.util.List;
/*    */ 
/*    */ public class VMModelServiceDAO extends AbstractDAO
/*    */   implements IVMModelServiceDAO
/*    */ {
/*    */   public List getAllAttrGroup(IServiceActionContext actionContext)
/*    */     throws UserException
/*    */   {
/* 35 */     return null;
/*    */   }
/*    */ 
/*    */   public List getAllQueryTemplate(IServiceActionContext actionContext) throws UserException {
/* 39 */     return null;
/*    */   }
/*    */ 
/*    */   public void createAttrGroup(IServiceActionContext actionContext, ExtAttrMetaGroup attrGroup) throws UserException {
/*    */   }
/*    */ 
/*    */   public void deleteAttrGroup(IServiceActionContext actionContext, ExtAttrMetaGroup attrGroup) throws UserException {
/*    */   }
/*    */ 
/*    */   public void createQueryTemplate(IServiceActionContext actionContext, TemplateMeta queryTemplate) {
/*    */   }
/*    */ 
/*    */   public void deleteQueryTemplate(IServiceActionContext actionContext, TemplateMeta queryTemplate) throws UserException {
/*    */   }
/*    */ 
/*    */   public void modifyQueryTemplate(IServiceActionContext actionContext, TemplateMeta queryTemplate) {
/*    */   }
/*    */ 
/*    */   public DataObjectList getObjectBySql(IServiceActionContext actionContext, String sSql, Class[] objectClass) throws Exception {
/* 58 */     DataObjectList rs = super.selectDBOs(sSql, objectClass);
/* 59 */     return rs;
/*    */   }
/*    */ 
/*    */   public void delOjbectBySql(IServiceActionContext actionContext, String sSql) throws Exception {
/* 63 */     super.execSql(sSql);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.ext.service.vm.VMModelServiceDAO
 * JD-Core Version:    0.6.0
 */