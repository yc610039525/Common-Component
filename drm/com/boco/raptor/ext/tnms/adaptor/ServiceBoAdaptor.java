/*    */ package com.boco.raptor.ext.tnms.adaptor;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.raptor.common.service.IServiceActionContext;
/*    */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*    */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*    */ import com.boco.transnms.common.dto.base.IBoQueryContext;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ServiceBoAdaptor
/*    */ {
/*    */   public static IBoActionContext svCxt2boCxt(IServiceActionContext svActCxt)
/*    */   {
/* 33 */     IBoActionContext boActCxt = null;
/* 34 */     if ((svActCxt instanceof IBoActionContext))
/* 35 */       boActCxt = (IBoActionContext)svActCxt;
/*    */     else {
/* 37 */       throw new UserException("数据类型错误 !");
/*    */     }
/* 39 */     return boActCxt;
/*    */   }
/*    */ 
/*    */   public static IBoQueryContext drmQryCxt2boQryCxt(IDrmQueryContext drmQryCxt) {
/* 43 */     IBoQueryContext boQryCxt = null;
/* 44 */     if ((drmQryCxt instanceof IBoQueryContext))
/* 45 */       boQryCxt = (IBoQueryContext)drmQryCxt;
/*    */     else {
/* 47 */       throw new UserException("数据类型错误 !");
/*    */     }
/* 49 */     return boQryCxt;
/*    */   }
/*    */ 
/*    */   public static GenericDO drmDO2genericDO(IDrmDataObject dro) {
/* 53 */     GenericDO dbo = null;
/* 54 */     if ((dro instanceof GenericDO))
/* 55 */       dbo = (GenericDO)dro;
/*    */     else {
/* 57 */       throw new UserException("数据类型错误 !");
/*    */     }
/* 59 */     return dbo;
/*    */   }
/*    */ 
/*    */   public static <T> T drmDO2genericDO(IDrmDataObject dro, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*    */   {
/* 64 */     GenericDO dbo = (GenericDO)Class.forName(className).newInstance();
/* 65 */     dbo.setBmClassId(dro.getBmClassId());
/* 66 */     dbo.setDbClassId(dro.getDbClassId());
/* 67 */     dbo.setObjectNum(dro.getDboId().longValue());
/* 68 */     dbo.setAttrValues(dro.getAllAttr());
/* 69 */     return dbo;
/*    */   }
/*    */ 
/*    */   public static void copyDrmDO2genericDO(IDrmDataObject dro, GenericDO dbo) throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*    */   {
/* 74 */     dbo.setObjectNum(dro.getDboId().longValue());
/* 75 */     dbo.setCuid(dro.getCuid());
/* 76 */     dbo.setAttrValues(dro.getAllAttr());
/*    */   }
/*    */ 
/*    */   public static void updateDrmDO2genericDO(IDrmDataObject dro, GenericDO dbo) throws ClassNotFoundException, IllegalAccessException, InstantiationException
/*    */   {
/* 81 */     for (String attrId : dro.getAllAttr().keySet()) {
/* 82 */       Object attrValue = dro.getAllAttr().get(attrId);
/* 83 */       dbo.setAttrValue(attrId, attrValue);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static IDrmDataObject genericDO2drmDO(GenericDO dbo) {
/* 88 */     IDrmDataObject dro = null;
/* 89 */     if ((dbo instanceof IDrmDataObject))
/* 90 */       dro = dbo;
/*    */     else {
/* 92 */       throw new UserException("数据类型错误 !");
/*    */     }
/* 94 */     return dro;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.ext.tnms.adaptor.ServiceBoAdaptor
 * JD-Core Version:    0.6.0
 */