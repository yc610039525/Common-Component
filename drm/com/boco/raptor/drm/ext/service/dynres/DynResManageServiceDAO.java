/*     */ package com.boco.raptor.drm.ext.service.dynres;
/*     */ 
/*     */ import com.boco.common.util.db.DbConnManager;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO;
/*     */ import com.boco.raptor.ext.tnms.adaptor.DrmDataObjTemplate;
/*     */ import com.boco.raptor.ext.tnms.adaptor.ServiceBoAdaptor;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.server.dao.base.AbstractDAO;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DynResManageServiceDAO<T extends IDrmDataObject> extends AbstractDAO
/*     */   implements IDynResManageServiceDAO<T>
/*     */ {
/*     */   public IDrmDataObject addDynObject(IServiceActionContext actionContext, IDrmDataObject dro)
/*     */     throws Exception
/*     */   {
/*  48 */     super.createObject(ServiceBoAdaptor.svCxt2boCxt(actionContext), ServiceBoAdaptor.drmDO2genericDO(dro));
/*  49 */     return dro;
/*     */   }
/*     */ 
/*     */   public void deleteDynObject(IServiceActionContext actionContext, IDrmDataObject dro) throws Exception {
/*  53 */     super.deleteObject(ServiceBoAdaptor.svCxt2boCxt(actionContext), (GenericDO)dro);
/*     */   }
/*     */ 
/*     */   public void deleteDynObjects(IServiceActionContext actionContext, List<T> dros) throws Exception {
/*  57 */     DataObjectList dbos = new DataObjectList();
/*  58 */     for (IDrmDataObject dro : dros) {
/*  59 */       dbos.add(ServiceBoAdaptor.drmDO2genericDO(dro));
/*     */     }
/*  61 */     super.deleteObjects(ServiceBoAdaptor.svCxt2boCxt(actionContext), dbos);
/*     */   }
/*     */ 
/*     */   public void modifyDynObject(IServiceActionContext actionContext, IDrmDataObject dro) throws Exception {
/*  65 */     super.updateObject(ServiceBoAdaptor.svCxt2boCxt(actionContext), ServiceBoAdaptor.drmDO2genericDO(dro));
/*     */   }
/*     */ 
/*     */   public void modifyDynObjects(IServiceActionContext actionContext, List<T> dros, Map modifyAttrs) throws Exception {
/*  69 */     DataObjectList dbos = new DataObjectList();
/*  70 */     for (IDrmDataObject dro : dros) {
/*  71 */       dbos.add(ServiceBoAdaptor.drmDO2genericDO(dro));
/*     */     }
/*  73 */     super.updateObjects(ServiceBoAdaptor.svCxt2boCxt(actionContext), dbos, modifyAttrs);
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getDynObjBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query, IDrmDataObject dboTemplate) throws Exception
/*     */   {
/*  78 */     String sql = query.getQuerySql(DbConnManager.getInstance().getDbType(actionContext.getDataSourceName()));
/*  79 */     String orderField = queryContext.getOrderField();
/*  80 */     if (orderField != null) {
/*  81 */       String sort = queryContext.isOrderDesc() ? " DESC " : " ASC ";
/*  82 */       sql = sql + " order by " + orderField + sort;
/*     */     }
/*  84 */     return super.selectDBOs(ServiceBoAdaptor.drmQryCxt2boQryCxt(queryContext), sql, new GenericDO[] { ServiceBoAdaptor.drmDO2genericDO(dboTemplate) });
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getDynObjBySql(IServiceActionContext actionContext, IDrmQueryContext queryContext, DrmSingleClassQuery query) throws Exception
/*     */   {
/*  89 */     BMClassMeta classMeta = query.getClassMeta();
/*  90 */     GenericDO dboTemplate = null;
/*  91 */     if (classMeta.isDynClass())
/*  92 */       dboTemplate = new DrmDataObjTemplate(classMeta);
/*     */     else {
/*  94 */       dboTemplate = (GenericDO)Class.forName(classMeta.getEntityClassName()).newInstance();
/*     */     }
/*  96 */     return getDynObjBySql(actionContext, queryContext, query, dboTemplate);
/*     */   }
/*     */ 
/*     */   public IDrmDataObject getDynObject(IServiceActionContext actionContext, BMClassMeta classMeta, IDrmDataObject simpleDro) throws Exception {
/* 100 */     GenericDO fullDbo = null;
/* 101 */     simpleDro.setBmClassId(classMeta.getBmClassId());
/* 102 */     simpleDro.setDbClassId(classMeta.getDbClassId());
/* 103 */     if (simpleDro.getDboId().longValue() != 0L)
/* 104 */       fullDbo = super.getObject(ServiceBoAdaptor.drmDO2genericDO(simpleDro));
/* 105 */     else if (simpleDro.getCuid() != null)
/* 106 */       fullDbo = super.getObjByCuid(ServiceBoAdaptor.drmDO2genericDO(simpleDro));
/*     */     else {
/* 108 */       throw new UserException("主键ID和CUID都为空 ！");
/*     */     }
/*     */ 
/* 111 */     if (fullDbo != null) {
/* 112 */       return ServiceBoAdaptor.genericDO2drmDO(fullDbo);
/*     */     }
/* 114 */     return fullDbo;
/*     */   }
/*     */ 
/*     */   public int getDynObjCount(IServiceActionContext actionContext, String sql) throws Exception {
/* 118 */     return super.getCalculateValue(sql);
/*     */   }
/*     */ 
/*     */   public int getDynObjCount(IServiceActionContext actionContext, DrmSingleClassQuery query) throws Exception
/*     */   {
/* 123 */     String sql = "select count(*) from " + query.getClassMeta().getDbClassId();
/* 124 */     String sqlCond = query.getQuerySqlCond(DbConnManager.getInstance().getDbType(actionContext.getDataSourceName()));
/* 125 */     if ((sqlCond != null) && (sqlCond.trim().length() > 0)) {
/* 126 */       sql = sql + " where " + sqlCond;
/*     */     }
/* 128 */     return getDynObjCount(actionContext, sql);
/*     */   }
/*     */ 
/*     */   public Map<Integer, Integer> getEnumCount(IServiceActionContext actionContext, String bmClassId, String attrId, String whereSql) throws Exception {
/* 132 */     String dbClassId = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId).getDbClassId();
/* 133 */     String sqlCond = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId).getBmDivideSqlCond();
/* 134 */     String sql = "select " + attrId + ",count(*) from " + dbClassId;
/* 135 */     if (sqlCond.length() > 0) {
/* 136 */       sql = sql + " where " + sqlCond;
/* 137 */       sql = sql + " and " + attrId + " IS NOT NULL ";
/*     */     } else {
/* 139 */       sql = sql + " where " + attrId + " IS NOT NULL ";
/*     */     }
/* 141 */     if ((whereSql != null) && (whereSql.trim().length() > 0))
/* 142 */       sql = sql + " and " + whereSql + " group by " + attrId;
/*     */     else {
/* 144 */       sql = sql + " group by " + attrId;
/*     */     }
/* 146 */     Map enumNameCount = new HashMap();
/* 147 */     Class[] enumCountClass = new Class[2];
/* 148 */     enumCountClass[0] = Integer.TYPE;
/* 149 */     enumCountClass[1] = Integer.TYPE;
/* 150 */     DataObjectList rs = super.selectDBOs(sql, enumCountClass);
/* 151 */     if (rs != null) {
/* 152 */       for (GenericDO attr : rs) {
/* 153 */         int enumValue = ((Integer)attr.getAttrValue("1")).intValue();
/*     */ 
/* 155 */         int count = ((Integer)attr.getAttrValue("2")).intValue();
/* 156 */         enumNameCount.put(Integer.valueOf(enumValue), Integer.valueOf(count));
/*     */       }
/*     */     }
/* 159 */     return enumNameCount;
/*     */   }
/*     */ 
/*     */   public List<Integer> getClassStatCount(IServiceActionContext actionContext, IDrmQueryContext queryContext, String bmClassId, List<String> attrIds, String userSql) throws Exception {
/* 163 */     String dbClassId = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId).getDbClassId();
/* 164 */     String sqlCond = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId).getBmDivideSqlCond();
/* 165 */     String condition = " ";
/* 166 */     String sql = "select ";
/* 167 */     for (String attrId : attrIds) {
/* 168 */       condition = condition + attrId + ",";
/*     */     }
/* 170 */     sql = sql + "count(*) from " + dbClassId;
/* 171 */     if (sqlCond.length() > 0)
/* 172 */       sql = sql + " where " + sqlCond + " and ";
/*     */     else {
/* 174 */       sql = sql + " where ";
/*     */     }
/* 176 */     for (int i = 0; i < attrIds.size() - 1; i++) {
/* 177 */       sql = sql + (String)attrIds.get(i) + " is not null and ";
/*     */     }
/* 179 */     sql = sql + (String)attrIds.get(attrIds.size() - 1) + " is not null";
/* 180 */     if ((userSql != null) && (userSql.trim().length() > 0)) {
/* 181 */       sql = sql + " and " + userSql;
/*     */     }
/* 183 */     sql = sql + " group by " + condition.substring(0, condition.length() - 1);
/* 184 */     sql = sql + " order by " + condition.substring(0, condition.length() - 1);
/* 185 */     Class[] classStatCountClass = new Class[1];
/* 186 */     classStatCountClass[0] = Integer.TYPE;
/* 187 */     DataObjectList dbo = super.selectDBOs(ServiceBoAdaptor.drmQryCxt2boQryCxt(queryContext), sql, classStatCountClass);
/* 188 */     List attrValueCount = new ArrayList();
/* 189 */     if (dbo != null) {
/* 190 */       for (GenericDO attr : dbo) {
/* 191 */         int count = ((Integer)attr.getAttrValue("1")).intValue();
/* 192 */         attrValueCount.add(Integer.valueOf(count));
/*     */       }
/*     */     }
/* 195 */     return attrValueCount;
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 199 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.ext.service.dynres.DynResManageServiceDAO
 * JD-Core Version:    0.6.0
 */