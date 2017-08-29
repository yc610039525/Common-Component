/*      */ package com.boco.raptor.drm.core.service.vm.impl;
/*      */ 
/*      */ import com.boco.common.util.debug.LogHome;
/*      */ import com.boco.common.util.excel.ExcelHelper;
/*      */ import com.boco.common.util.except.UserException;
/*      */ import com.boco.common.util.io.BufInputStream;
/*      */ import com.boco.common.util.lang.TimeFormatHelper;
/*      */ import com.boco.raptor.common.service.AbstractService;
/*      */ import com.boco.raptor.common.service.IServiceActionContext;
/*      */ import com.boco.raptor.common.service.ServiceHelper;
/*      */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*      */ import com.boco.raptor.common.service.impl.ServiceActionContext;
/*      */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*      */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*      */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*      */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*      */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta;
/*      */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta.DATA_TYPE;
/*      */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*      */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*      */ import com.boco.raptor.drm.core.meta.ClassAttrGroupMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateRelatedMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExtAttrMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
/*      */ import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup.GROUP_TYPE_ENUM;
/*      */ import com.boco.raptor.drm.core.meta.TemplateMeta;
/*      */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*      */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*      */ import com.boco.raptor.drm.core.service.security.IAuthenticationService;
/*      */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*      */ import com.boco.raptor.drm.core.service.vm.ExtAttrNameEnum.Property;
/*      */ import com.boco.raptor.drm.core.service.vm.ExtAttrNameEnum.QueryTable;
/*      */ import com.boco.raptor.drm.core.service.vm.ExtAttrNameEnum.QueryTemplate;
/*      */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*      */ import com.boco.raptor.drm.core.service.vm.IVMModelServiceDAO;
/*      */ import com.boco.raptor.drm.core.service.vm.TemplateExtEnum.QueryTemplate;
/*      */ import com.boco.transnms.common.dto.Drm;
/*      */ import com.boco.transnms.common.dto.DrmAttrGroup;
/*      */ import com.boco.transnms.common.dto.DrmClassAttrGroup;
/*      */ import com.boco.transnms.common.dto.DrmExlClassUnique;
/*      */ import com.boco.transnms.common.dto.DrmExlExpTemplate;
/*      */ import com.boco.transnms.common.dto.DrmExlImpTemplate;
/*      */ import com.boco.transnms.common.dto.DrmGroupAttr;
/*      */ import com.boco.transnms.common.dto.DrmQueryTemplate;
/*      */ import com.boco.transnms.common.dto.base.DataObjectList;
/*      */ import com.boco.transnms.common.dto.base.GenericDO;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import jxl.Workbook;
/*      */ import jxl.write.WritableWorkbook;
/*      */ import jxl.write.WriteException;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class VMModelService extends AbstractService
/*      */   implements IVMModelService
/*      */ {
/*      */   private IVMModelServiceDAO dao;
/*   85 */   private String defaultUserId = "SYS_USER-0";
/*   86 */   private Map<String, IDrmDataObject> userIsHideResNavs = new HashMap();
/*   87 */   private Map<String, Map<String, ExtAttrMetaGroup>> userQueryTableMetas = new HashMap();
/*   88 */   private Map<String, Map<String, ExtAttrMetaGroup>> userPropertyMetas = new HashMap();
/*   89 */   private Map<String, Map<String, List<TemplateMeta>>> userQueryTemplates = new HashMap();
/*   90 */   private Map<String, Map<String, List<ExcelImpTemplateMeta>>> userExcelImpTemplates = new HashMap();
/*      */ 
/*   92 */   private Map<String, Map<String, List<TemplateMeta>>> userExcelExpTemplates = new HashMap();
/*      */ 
/*   94 */   private Map<String, ExtAttrMetaGroup> attrGroups = new HashMap();
/*      */ 
/*   96 */   private List<ClassAttrGroupMeta> classAttrGroups = new ArrayList();
/*   97 */   private Map<String, Map<String, ClassAttrGroupMeta>> classGroupAttrs = new HashMap();
/*      */ 
/*      */   public VMModelService() {
/*  100 */     super("VMModelService");
/*      */   }
/*      */ 
/*      */   public void initService()
/*      */     throws Exception
/*      */   {
/*  107 */     delRedundanceGroupAttr();
/*      */ 
/*  110 */     initAttrGroup();
/*  111 */     initClassAttrGroup();
/*  112 */     initQueryTemplate();
/*  113 */     initExcelImpTemplateData();
/*  114 */     initExcelExpTemplateData();
/*  115 */     super.initService();
/*      */   }
/*      */ 
/*      */   public void delRedundanceGroupAttr()
/*      */   {
/*  120 */     String sSql = "";
/*  121 */     IServiceActionContext actionContext = ServiceHelper.createSvActCxt();
/*  122 */     IBMModelService bmService = getBMModelService();
/*      */     try {
/*  124 */       sSql = "select distinct bm_classid from drm_attr_group";
/*  125 */       Class[] ObjectClass = new Class[1];
/*  126 */       ObjectClass[0] = String.class;
/*  127 */       DataObjectList groupRs = this.dao.getObjectBySql(actionContext, sSql, ObjectClass);
/*  128 */       if (groupRs == null) {
/*  129 */         return;
/*      */       }
/*  131 */       for (GenericDO groupRow : groupRs) {
/*  132 */         String bmClassId = (String)groupRow.getAttrValue("1");
/*  133 */         BMClassMeta bmClassMeta = bmService.getClassMeta(actionContext, bmClassId);
/*  134 */         if (bmClassMeta == null)
/*      */         {
/*  136 */           sSql = "delete from drm_attr_group where bm_classid = '" + bmClassId + "'";
/*  137 */           this.dao.delOjbectBySql(actionContext, sSql);
/*      */ 
/*  139 */           sSql = "delete from drm_group_attr where bm_classid = '" + bmClassId + "'";
/*  140 */           this.dao.delOjbectBySql(actionContext, sSql);
/*      */         }
/*      */       }
/*      */ 
/*  144 */       sSql = "delete from DRM_EXL_EXP_TEMPLATE where related_attr_group_cuid not in (select cuid from drm_attr_group)";
/*  145 */       this.dao.delOjbectBySql(actionContext, sSql);
/*  146 */       sSql = "delete from DRM_EXL_IMP_TEMPLATE where related_attr_group_cuid not in (select cuid from drm_attr_group)";
/*  147 */       this.dao.delOjbectBySql(actionContext, sSql);
/*  148 */       sSql = "delete from DRM_EXL_CLASS_UNIQUE where RELATED_EXCEL_TEMPLATE_CUID not in (select cuid from DRM_EXL_IMP_TEMPLATE)";
/*  149 */       this.dao.delOjbectBySql(actionContext, sSql);
/*      */ 
/*  151 */       sSql = "select distinct bm_classid,attr_id from drm_group_attr";
/*  152 */       Class[] attrClass = new Class[2];
/*  153 */       attrClass[0] = String.class;
/*  154 */       attrClass[1] = String.class;
/*  155 */       DataObjectList groupAttrRs = this.dao.getObjectBySql(actionContext, sSql, attrClass);
/*  156 */       if (groupAttrRs == null) {
/*  157 */         return;
/*      */       }
/*  159 */       for (GenericDO groupAttrRow : groupAttrRs) {
/*  160 */         String bmClassId = (String)groupAttrRow.getAttrValue("1");
/*  161 */         String sAttrId = (String)groupAttrRow.getAttrValue("2");
/*  162 */         BMClassMeta bmClassMeta = bmService.getClassMeta(actionContext, bmClassId);
/*  163 */         BMAttrMeta mat = bmClassMeta.getAttrMeta(sAttrId);
/*  164 */         if (mat == null) {
/*  165 */           sSql = "delete from drm_group_attr where bm_classid = '" + bmClassId + "' and attr_id = '" + sAttrId + "'";
/*  166 */           this.dao.delOjbectBySql(actionContext, sSql);
/*      */         }
/*      */       }
/*      */     } catch (Exception ex) {
/*  170 */       LogHome.getLog().error("", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void initAttrGroup()
/*      */   {
/*  176 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  177 */     DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/*  178 */     drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/*  179 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond());
/*  180 */     DrmAttrGroup dbo = new DrmAttrGroup();
/*  181 */     IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmAttrGroupQuery, dbo);
/*  182 */     if (drmAttrGroupRs == null) {
/*  183 */       return;
/*      */     }
/*  185 */     List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/*      */ 
/*  187 */     for (IDrmQueryRow attrGroupRow : drmAttrGroupRows) {
/*  188 */       ExtAttrMetaGroup attrGroup = new ExtAttrMetaGroup();
/*  189 */       IDrmDataObject attrGroupDbobj = attrGroupRow.getResultDbo("DRM_ATTR_GROUP");
/*      */ 
/*  191 */       if (attrGroupDbobj.getAttrValue("GROUP_TYPE").toString().equals("7")) {
/*  192 */         this.userIsHideResNavs.put((String)attrGroupDbobj.getAttrValue("RELATED_USER_CUID"), attrGroupDbobj);
/*  193 */         continue;
/*      */       }
/*      */ 
/*  196 */       String drmAttrGroupCuid = (String)attrGroupDbobj.getAttrValue("CUID");
/*      */ 
/*  198 */       attrGroup.setBmClassId((String)attrGroupDbobj.getAttrValue("BM_CLASSID"));
/*  199 */       attrGroup.setDbClassId((String)attrGroupDbobj.getAttrValue("BM_CLASSID"));
/*  200 */       Long ll = (Long)attrGroupDbobj.getAttrValue("GROUP_TYPE");
/*  201 */       attrGroup.setGroupType(ll.intValue());
/*  202 */       attrGroup.setUserCuid((String)attrGroupDbobj.getAttrValue("RELATED_USER_CUID"));
/*  203 */       attrGroup.setGroupCuid(drmAttrGroupCuid);
/*      */ 
/*  205 */       List attrList = new ArrayList();
/*      */ 
/*  207 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/*  208 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/*  209 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/*  210 */       queryContext.setOrderField("SORT_NO");
/*  211 */       DrmGroupAttr dga = new DrmGroupAttr();
/*  212 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmGroupAttrQuery, dga);
/*  213 */       if (drmGroupAttrRs == null) {
/*  214 */         return;
/*      */       }
/*  216 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/*      */ 
/*  218 */       for (IDrmQueryRow groupAttrRow : drmGroupAttrRows) {
/*  219 */         DrmGroupAttr groupDbobj = (DrmGroupAttr)groupAttrRow.getResultDbo("DRM_GROUP_ATTR");
/*  220 */         ExtAttrMeta attrMeta = new ExtAttrMeta();
/*  221 */         String sAttrId = (String)groupDbobj.getAttrValue("ATTR_ID");
/*  222 */         String bmClassId = (String)groupDbobj.getAttrValue("BM_CLASSID");
/*  223 */         attrMeta.setAttrId(sAttrId);
/*  224 */         attrMeta.setBmClassId(bmClassId);
/*      */ 
/*  226 */         if (attrGroup.getGroupType() == 6) {
/*  227 */           attrMeta.setExtAttrMeta(ExtAttrNameEnum.QueryTemplate.ATTR_RELATION, groupDbobj.getExtVal1());
/*  228 */           attrMeta.setExtAttrMeta(ExtAttrNameEnum.QueryTemplate.ATTR_DEFAULT_VALUE, groupDbobj.getExtVal2());
/*  229 */         } else if ((attrGroup.getGroupType() == 1) || (attrGroup.getGroupType() == 2)) {
/*  230 */           attrMeta.setExtAttrMeta(ExtAttrNameEnum.QueryTable.COL_WIDTH, groupDbobj.getExtVal1());
/*      */         }
/*  232 */         attrList.add(attrMeta);
/*      */       }
/*      */ 
/*  235 */       attrGroup.setExtAttrMetas(attrList);
/*      */ 
/*  238 */       this.attrGroups.put(attrGroup.getGroupCuid(), attrGroup);
/*      */ 
/*  240 */       if ((attrGroup.getGroupType() == 1) && (attrGroup.getExtAttrMetas().size() > 0))
/*      */       {
/*  242 */         Map userQueryTable = (Map)this.userQueryTableMetas.get(attrGroup.getUserCuid());
/*  243 */         if (userQueryTable == null) {
/*  244 */           userQueryTable = new HashMap();
/*      */ 
/*  246 */           userQueryTable.put(attrGroup.getBmClassId(), attrGroup);
/*      */ 
/*  248 */           this.userQueryTableMetas.put(attrGroup.getUserCuid(), userQueryTable);
/*      */         } else {
/*  250 */           userQueryTable.put(attrGroup.getBmClassId(), attrGroup);
/*      */         }
/*  252 */       } else if ((attrGroup.getGroupType() == 2) && (attrGroup.getExtAttrMetas().size() > 0))
/*      */       {
/*  254 */         Map userDetailTable = (Map)this.userPropertyMetas.get(attrGroup.getUserCuid());
/*  255 */         if (userDetailTable == null) {
/*  256 */           userDetailTable = new HashMap();
/*      */ 
/*  258 */           userDetailTable.put(attrGroup.getBmClassId(), attrGroup);
/*      */ 
/*  260 */           this.userPropertyMetas.put(attrGroup.getUserCuid(), userDetailTable);
/*      */         } else {
/*  262 */           userDetailTable.put(attrGroup.getBmClassId(), attrGroup);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void initClassAttrGroup()
/*      */   {
/*  272 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  273 */     queryContext.setOrderField("SORT_NO");
/*  274 */     DrmSingleClassQuery drmClassAttrGroupQuery = new DrmSingleClassQuery();
/*  275 */     drmClassAttrGroupQuery.setBmClassId("DRM_CLASS_ATTR_GROUP");
/*  276 */     drmClassAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond());
/*  277 */     DrmClassAttrGroup dbo = new DrmClassAttrGroup();
/*  278 */     IDrmQueryResultSet drmClassAttrGroupRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmClassAttrGroupQuery, dbo);
/*      */ 
/*  280 */     if (drmClassAttrGroupRs != null) {
/*  281 */       List drmClassAttrGroupRows = drmClassAttrGroupRs.getResultSet();
/*      */ 
/*  283 */       for (IDrmQueryRow attrGroupRow : drmClassAttrGroupRows) {
/*  284 */         attrGroup = new ClassAttrGroupMeta();
/*  285 */         IDrmDataObject attrGroupDbobj = attrGroupRow.getResultDbo("DRM_CLASS_ATTR_GROUP");
/*      */ 
/*  287 */         drmAttrGroupCuid = (String)attrGroupDbobj.getAttrValue("CUID");
/*  288 */         attrGroup.setGroupCuid(drmAttrGroupCuid);
/*  289 */         attrGroup.setLabelCn((String)attrGroupDbobj.getAttrValue("LABEL_CN"));
/*  290 */         Long iSortNo = (Long)attrGroupDbobj.getAttrValue("SORT_NO");
/*  291 */         attrGroup.setSortNo(iSortNo.longValue());
/*  292 */         this.classAttrGroups.add(attrGroup);
/*      */ 
/*  295 */         if (drmAttrGroupCuid.equals("otherinfo"))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  300 */         DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/*  301 */         drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/*  302 */         drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/*  303 */         queryContext.setOrderField("SORT_NO");
/*  304 */         DrmGroupAttr dga = new DrmGroupAttr();
/*  305 */         IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmGroupAttrQuery, dga);
/*      */ 
/*  307 */         if (drmGroupAttrRs != null) {
/*  308 */           List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/*  309 */           for (IDrmQueryRow groupAttrRow : drmGroupAttrRows) {
/*  310 */             DrmGroupAttr groupDbobj = (DrmGroupAttr)groupAttrRow.getResultDbo("DRM_GROUP_ATTR");
/*      */ 
/*  312 */             String bmClassId = (String)groupDbobj.getAttrValue("BM_CLASSID");
/*      */ 
/*  314 */             ExtAttrMeta attrMeta = new ExtAttrMeta();
/*  315 */             attrMeta.setAttrId((String)groupDbobj.getAttrValue("ATTR_ID"));
/*  316 */             attrMeta.setBmClassId(bmClassId);
/*      */ 
/*  318 */             ClassAttrGroupMeta groupMeta = null;
/*  319 */             Map groupList = (Map)this.classGroupAttrs.get(bmClassId);
/*  320 */             if (groupList == null) {
/*  321 */               groupList = new HashMap();
/*      */ 
/*  323 */               groupMeta = new ClassAttrGroupMeta();
/*  324 */               groupMeta.setGroupCuid(drmAttrGroupCuid);
/*  325 */               groupMeta.setLabelCn(attrGroup.getLabelCn());
/*  326 */               groupMeta.setSortNo(attrGroup.getSortNo());
/*  327 */               groupMeta.addExtAttrMeta(attrMeta);
/*      */ 
/*  329 */               groupList.put(drmAttrGroupCuid, groupMeta);
/*  330 */               this.classGroupAttrs.put(bmClassId, groupList);
/*      */             } else {
/*  332 */               groupMeta = (ClassAttrGroupMeta)groupList.get(drmAttrGroupCuid);
/*  333 */               if (groupMeta == null) {
/*  334 */                 groupMeta = new ClassAttrGroupMeta();
/*  335 */                 groupMeta.setGroupCuid(drmAttrGroupCuid);
/*  336 */                 groupMeta.setLabelCn(attrGroup.getLabelCn());
/*  337 */                 groupMeta.setSortNo(attrGroup.getSortNo());
/*  338 */                 groupMeta.addExtAttrMeta(attrMeta);
/*      */ 
/*  340 */                 groupList.put(drmAttrGroupCuid, groupMeta);
/*      */               } else {
/*  342 */                 groupMeta.addExtAttrMeta(attrMeta);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     ClassAttrGroupMeta attrGroup;
/*      */     String drmAttrGroupCuid;
/*  350 */     if (this.classAttrGroups.size() == 0)
/*      */     {
/*  352 */       DrmClassAttrGroup baseInfoGroup = new DrmClassAttrGroup();
/*  353 */       baseInfoGroup.setBmClassId("DRM_CLASS_ATTR_GROUP");
/*  354 */       baseInfoGroup.setLabelCn("基本信息");
/*  355 */       baseInfoGroup.setSortNo(1L);
/*  356 */       baseInfoGroup.setCuid("baseinfo");
/*  357 */       getDynResManageService().addDynObject(ServiceHelper.createSvActCxt(), baseInfoGroup, false);
/*      */ 
/*  359 */       DrmClassAttrGroup otherInfoGroup = new DrmClassAttrGroup();
/*  360 */       otherInfoGroup.setBmClassId("DRM_CLASS_ATTR_GROUP");
/*  361 */       otherInfoGroup.setLabelCn("其他信息");
/*  362 */       otherInfoGroup.setSortNo(2L);
/*  363 */       otherInfoGroup.setCuid("otherinfo");
/*  364 */       getDynResManageService().addDynObject(ServiceHelper.createSvActCxt(), otherInfoGroup, false);
/*      */ 
/*  366 */       ClassAttrGroupMeta baseInfoGroupMeta = new ClassAttrGroupMeta();
/*  367 */       baseInfoGroupMeta.setGroupCuid("baseinfo");
/*  368 */       baseInfoGroupMeta.setLabelCn("基本信息");
/*  369 */       baseInfoGroupMeta.setSortNo(1L);
/*  370 */       this.classAttrGroups.add(baseInfoGroupMeta);
/*      */ 
/*  372 */       ClassAttrGroupMeta otherInfoGroupMeta = new ClassAttrGroupMeta();
/*  373 */       otherInfoGroupMeta.setGroupCuid("otherinfo");
/*  374 */       otherInfoGroupMeta.setLabelCn("其他信息");
/*  375 */       otherInfoGroupMeta.setSortNo(2L);
/*  376 */       this.classAttrGroups.add(otherInfoGroupMeta);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initExcelExpTemplateData()
/*      */   {
/*  382 */     List drmTemplateRows = getAllDynObjByClassName("DRM_EXL_EXP_TEMPLATE");
/*      */ 
/*  384 */     for (IDrmQueryRow templateRow : drmTemplateRows) {
/*  385 */       String sClassId = "";
/*  386 */       TemplateMeta template = new TemplateMeta();
/*      */ 
/*  388 */       IDrmDataObject templateDbobj = templateRow.getResultDbo("DRM_EXL_EXP_TEMPLATE");
/*      */ 
/*  390 */       String templateCuid = templateDbobj.getCuid();
/*  391 */       Long sortNo = (Long)templateDbobj.getAttrValue("SORT_NO");
/*  392 */       template.setSortNum(sortNo.longValue());
/*  393 */       template.setTemplateCuid(templateCuid);
/*  394 */       template.setTemplateLabelCn((String)templateDbobj.getAttrValue("LABEL_CN"));
/*      */ 
/*  397 */       String drmAttrGroupCuid = (String)templateDbobj.getAttrValue("RELATED_ATTR_GROUP_CUID");
/*  398 */       ExtAttrMetaGroup attrGroup = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/*  399 */       if (attrGroup == null) {
/*  400 */         attrGroup = new ExtAttrMetaGroup();
/*      */       }
/*      */ 
/*  403 */       template.setExtAttrMetaGroup(attrGroup);
/*  404 */       sClassId = attrGroup.getBmClassId();
/*      */ 
/*  407 */       Map userMap = (Map)this.userExcelExpTemplates.get((String)templateDbobj.getAttrValue("RELATED_USER_CUID"));
/*  408 */       List classMap = null;
/*  409 */       if (userMap == null) {
/*  410 */         userMap = new HashMap();
/*  411 */         classMap = new ArrayList();
/*  412 */         classMap.add(template);
/*  413 */         userMap.put(sClassId, classMap);
/*  414 */         this.userExcelExpTemplates.put((String)templateDbobj.getAttrValue("RELATED_USER_CUID"), userMap);
/*      */       } else {
/*  416 */         classMap = (List)userMap.get(sClassId);
/*  417 */         if (classMap == null) {
/*  418 */           classMap = new ArrayList();
/*  419 */           classMap.add(template);
/*  420 */           userMap.put(sClassId, classMap);
/*      */         } else {
/*  422 */           classMap.add(template);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private List<IDrmQueryRow> getAllDynObjByClassName(String className)
/*      */   {
/*  430 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/*  432 */     DrmSingleClassQuery drmTemplateQuery = new DrmSingleClassQuery();
/*  433 */     drmTemplateQuery.setBmClassId(className);
/*  434 */     drmTemplateQuery.addQueryCondExps(new DrmQueryAttrCond());
/*  435 */     IDrmQueryResultSet drmTemplateRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmTemplateQuery);
/*  436 */     return drmTemplateRs.getResultSet();
/*      */   }
/*      */ 
/*      */   public void initExcelImpTemplateData()
/*      */   {
/*  441 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/*  443 */     DrmSingleClassQuery drmTemplateQuery = new DrmSingleClassQuery();
/*  444 */     drmTemplateQuery.setBmClassId("DRM_EXL_IMP_TEMPLATE");
/*  445 */     drmTemplateQuery.addQueryCondExps(new DrmQueryAttrCond());
/*  446 */     DrmExlImpTemplate dd = new DrmExlImpTemplate();
/*  447 */     IDrmQueryResultSet drmTemplateRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmTemplateQuery, dd);
/*  448 */     if (drmTemplateRs == null) {
/*  449 */       return;
/*      */     }
/*      */ 
/*  452 */     List drmTemplateRows = drmTemplateRs.getResultSet();
/*      */ 
/*  454 */     for (IDrmQueryRow templateRow : drmTemplateRows) {
/*  455 */       String sClassId = "";
/*  456 */       ExcelImpTemplateMeta template = new ExcelImpTemplateMeta();
/*      */ 
/*  458 */       IDrmDataObject templateDbobj = templateRow.getResultDbo("DRM_EXL_IMP_TEMPLATE");
/*      */ 
/*  460 */       String templateCuid = templateDbobj.getCuid();
/*  461 */       Boolean isValid = (Boolean)templateDbobj.getAttrValue("IS_VALID");
/*  462 */       template.setIsValid(isValid.booleanValue());
/*  463 */       template.setTemplateCuid(templateCuid);
/*  464 */       template.setTemplateLabelCn((String)templateDbobj.getAttrValue("LABEL_CN"));
/*      */ 
/*  467 */       DrmSingleClassQuery drmTemplateUniqueQuery = new DrmSingleClassQuery();
/*  468 */       drmTemplateUniqueQuery.setBmClassId("DRM_EXL_CLASS_UNIQUE");
/*  469 */       drmTemplateUniqueQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_EXCEL_TEMPLATE_CUID", "=", templateCuid));
/*  470 */       DrmExlClassUnique ddd = new DrmExlClassUnique();
/*  471 */       IDrmQueryResultSet drmTemplateUniqueRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmTemplateUniqueQuery, ddd);
/*      */ 
/*  473 */       if (drmTemplateUniqueRs == null) {
/*  474 */         return;
/*      */       }
/*  476 */       List drmTemplateUniqueRows = drmTemplateUniqueRs.getResultSet();
/*  477 */       Map relatedList = new HashMap();
/*  478 */       for (IDrmQueryRow uniqueRow : drmTemplateUniqueRows) {
/*  479 */         IDrmDataObject uniqueDbobj = uniqueRow.getResultDbo("DRM_EXL_CLASS_UNIQUE");
/*  480 */         ExcelImpTemplateRelatedMeta relatedObj = new ExcelImpTemplateRelatedMeta();
/*  481 */         Map classRelatedMeta = new HashMap();
/*  482 */         String sRelatedOrUnique = (String)uniqueDbobj.getAttrValue("RELATED_OR_UNIQUE");
/*  483 */         relatedObj.setRelatedOrUnique(sRelatedOrUnique);
/*  484 */         relatedObj.setClassUniqueName((String)uniqueDbobj.getAttrValue("CLASS_UNIQUE_NAME"));
/*  485 */         relatedObj.setTemplateCuid(templateCuid);
/*  486 */         relatedObj.setTemplateRelatedBmClassId((String)uniqueDbobj.getAttrValue("RELATED_BM_CLASSID"));
/*  487 */         relatedObj.setTemplateRelatedCuid(uniqueDbobj.getCuid());
/*  488 */         relatedObj.setParentAttrId((String)uniqueDbobj.getAttrValue("PARENT_ATTRID"));
/*  489 */         relatedObj.setParentBmClassId((String)uniqueDbobj.getAttrValue("PARENT_BM_CLASSID"));
/*      */ 
/*  491 */         classRelatedMeta.put(sRelatedOrUnique, relatedObj);
/*  492 */         String key = relatedObj.getParentAttrId() + ExcelHelper.KEY_FLAG + relatedObj.getTemplateRelatedBmClassId();
/*  493 */         relatedList.put(key, classRelatedMeta);
/*      */       }
/*  495 */       template.setExcelImpRelateds(relatedList);
/*      */ 
/*  498 */       String drmAttrGroupCuid = (String)templateDbobj.getAttrValue("RELATED_ATTR_GROUP_CUID");
/*  499 */       ExtAttrMetaGroup attrGroup = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/*  500 */       if (attrGroup == null) {
/*  501 */         attrGroup = new ExtAttrMetaGroup();
/*      */       }
/*      */ 
/*  504 */       template.setExtAttrMetaGroup(attrGroup);
/*  505 */       sClassId = attrGroup.getBmClassId();
/*      */ 
/*  508 */       Map userMap = (Map)this.userExcelImpTemplates.get((String)templateDbobj.getAttrValue("RELATED_USER_CUID"));
/*  509 */       List classMap = null;
/*  510 */       if (userMap == null) {
/*  511 */         userMap = new HashMap();
/*  512 */         classMap = new ArrayList();
/*      */ 
/*  514 */         classMap.add(template);
/*  515 */         userMap.put(sClassId, classMap);
/*      */ 
/*  517 */         this.userExcelImpTemplates.put((String)templateDbobj.getAttrValue("RELATED_USER_CUID"), userMap);
/*      */       } else {
/*  519 */         classMap = (List)userMap.get(sClassId);
/*  520 */         if (classMap == null) {
/*  521 */           classMap = new ArrayList();
/*  522 */           classMap.add(template);
/*  523 */           userMap.put(sClassId, classMap);
/*      */         } else {
/*  525 */           classMap.add(template);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void initQueryTemplate()
/*      */   {
/*  536 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/*  539 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/*  540 */     query.setBmClassId("DRM_QUERY_TEMPLATE");
/*  541 */     DrmQueryTemplate dd = new DrmQueryTemplate();
/*  542 */     IDrmQueryResultSet rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, query, dd);
/*  543 */     if (rs == null) {
/*  544 */       return;
/*      */     }
/*  546 */     List rows = rs.getResultSet();
/*  547 */     for (int i = 0; i < rows.size(); i++) {
/*  548 */       IDrmQueryRow row = (IDrmQueryRow)rows.get(i);
/*  549 */       DrmQueryTemplate drmQueryTemplate = (DrmQueryTemplate)row.getResultDbo("DRM_QUERY_TEMPLATE");
/*  550 */       TemplateMeta queryTemplate = new TemplateMeta();
/*  551 */       queryTemplate.setTemplateCuid(drmQueryTemplate.getCuid());
/*  552 */       queryTemplate.setTemplateLabelCn(drmQueryTemplate.getLabelCn());
/*  553 */       queryTemplate.setSortNum(drmQueryTemplate.getSortNo());
/*  554 */       queryTemplate.addTemplateExtMeta(TemplateExtEnum.QueryTemplate.SQL_TEMPLATE, drmQueryTemplate.getSqlTemplate());
/*      */ 
/*  556 */       ExtAttrMetaGroup queryAttrMeta = (ExtAttrMetaGroup)this.attrGroups.get(drmQueryTemplate.getRelatedAttrGroupCuid());
/*  557 */       if (queryAttrMeta == null) {
/*  558 */         queryAttrMeta = new ExtAttrMetaGroup();
/*      */       }
/*      */ 
/*  561 */       queryTemplate.setExtAttrMetaGroup(queryAttrMeta);
/*  562 */       String sClassId = queryAttrMeta.getBmClassId();
/*      */ 
/*  564 */       Map userMap = (Map)this.userQueryTemplates.get(drmQueryTemplate.getRelatedUserCuid());
/*  565 */       List classMap = null;
/*  566 */       if (userMap == null) {
/*  567 */         userMap = new HashMap();
/*  568 */         classMap = new ArrayList();
/*      */ 
/*  570 */         classMap.add(queryTemplate);
/*  571 */         userMap.put(sClassId, classMap);
/*      */ 
/*  573 */         this.userQueryTemplates.put(drmQueryTemplate.getRelatedUserCuid(), userMap);
/*      */       } else {
/*  575 */         classMap = (List)userMap.get(sClassId);
/*  576 */         if (classMap == null) {
/*  577 */           classMap = new ArrayList();
/*  578 */           classMap.add(queryTemplate);
/*  579 */           userMap.put(sClassId, classMap);
/*      */         } else {
/*  581 */           classMap.add(queryTemplate);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDefaultUserId(IServiceActionContext actionContext, String defaultUserId)
/*      */   {
/*  589 */     this.defaultUserId = defaultUserId;
/*      */   }
/*      */ 
/*      */   public ExtAttrMetaGroup getQueryTableMeta(IServiceActionContext actionContext, String bmClassId) throws UserException {
/*  593 */     ExtAttrMetaGroup queryTableMeta = null;
/*  594 */     String userId = actionContext.getUserId();
/*  595 */     userId = this.userQueryTableMetas.containsKey(userId) ? userId : this.defaultUserId;
/*  596 */     Map userQueryTableMeta = (Map)this.userQueryTableMetas.get(userId);
/*  597 */     if (userQueryTableMeta != null) {
/*  598 */       queryTableMeta = (ExtAttrMetaGroup)userQueryTableMeta.get(bmClassId);
/*      */     }
/*      */ 
/*  601 */     if ((queryTableMeta == null) && (!userId.equals(this.defaultUserId))) {
/*  602 */       Map adminQueryTableMeta = (Map)this.userQueryTableMetas.get(this.defaultUserId);
/*  603 */       if (adminQueryTableMeta != null) {
/*  604 */         queryTableMeta = (ExtAttrMetaGroup)adminQueryTableMeta.get(bmClassId);
/*      */       }
/*      */     }
/*  607 */     if (queryTableMeta == null) {
/*  608 */       BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/*  609 */       queryTableMeta = new ExtAttrMetaGroup();
/*  610 */       queryTableMeta.setBmClassId(bmClassId);
/*  611 */       queryTableMeta.setDbClassId(bmClassId);
/*      */ 
/*  613 */       ExtAttrMeta extAttrMeta = new ExtAttrMeta(bmClassId, bmClassMeta.getCuidAttrId());
/*  614 */       queryTableMeta.addExtAttrMeta(extAttrMeta);
/*  615 */       extAttrMeta = new ExtAttrMeta(bmClassId, bmClassMeta.getPkAttrId());
/*  616 */       queryTableMeta.addExtAttrMeta(extAttrMeta);
/*      */ 
/*  618 */       List constructLabelAttrIds = bmClassMeta.getConstructLabelAttrIds();
/*  619 */       for (String attrId : constructLabelAttrIds) {
/*  620 */         ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, attrId);
/*  621 */         queryTableMeta.addExtAttrMeta(attrMeta);
/*      */       }
/*      */     }
/*  624 */     return queryTableMeta;
/*      */   }
/*      */ 
/*      */   public ExtAttrMetaGroup getPropertyMeta(IServiceActionContext actionContext, String bmClassId) throws UserException {
/*  628 */     ExtAttrMetaGroup propertyMeta = null;
/*  629 */     String userId = actionContext.getUserId();
/*  630 */     userId = this.userPropertyMetas.containsKey(userId) ? userId : this.defaultUserId;
/*  631 */     Map userProptertyMeta = (Map)this.userPropertyMetas.get(userId);
/*  632 */     if (userProptertyMeta != null) {
/*  633 */       propertyMeta = (ExtAttrMetaGroup)userProptertyMeta.get(bmClassId);
/*      */     }
/*      */ 
/*  636 */     if ((propertyMeta == null) && (!userId.equals(this.defaultUserId))) {
/*  637 */       Map adminProptertyMeta = (Map)this.userPropertyMetas.get(this.defaultUserId);
/*  638 */       if (adminProptertyMeta != null)
/*  639 */         propertyMeta = (ExtAttrMetaGroup)adminProptertyMeta.get(bmClassId);
/*      */     }
/*      */     BMClassMeta bmClassMeta;
/*  643 */     if (propertyMeta == null) {
/*  644 */       bmClassMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/*  645 */       propertyMeta = new ExtAttrMetaGroup();
/*  646 */       propertyMeta.setBmClassId(bmClassId);
/*  647 */       propertyMeta.setDbClassId(bmClassId);
/*      */ 
/*  652 */       for (String attrId : bmClassMeta.getAllAttrIds()) {
/*  653 */         BMAttrMeta bmAttrMeta = bmClassMeta.getAttrMeta(attrId);
/*  654 */         if ((bmAttrMeta != null) && (!bmAttrMeta.getIsSystem().booleanValue())) {
/*  655 */           ExtAttrMeta attrMeta = new ExtAttrMeta(bmClassId, attrId);
/*  656 */           propertyMeta.addExtAttrMeta(attrMeta);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  661 */     return propertyMeta;
/*      */   }
/*      */ 
/*      */   public List<TemplateMeta> getQueryTemplates(IServiceActionContext actionContext, String bmClassId) throws UserException {
/*  665 */     List queryTemplates = new ArrayList();
/*  666 */     String userId = actionContext.getUserId();
/*  667 */     userId = this.userQueryTemplates.containsKey(userId) ? userId : this.defaultUserId;
/*  668 */     Map classQueryTemplates = (Map)this.userQueryTemplates.get(userId);
/*  669 */     if (classQueryTemplates != null) {
/*  670 */       List templates = (List)classQueryTemplates.get(bmClassId);
/*  671 */       if (templates != null) {
/*  672 */         queryTemplates.addAll(templates);
/*      */       }
/*      */     }
/*  675 */     if (!userId.equals(this.defaultUserId)) {
/*  676 */       Map adminQueryTemplates = (Map)this.userQueryTemplates.get(this.defaultUserId);
/*  677 */       if (adminQueryTemplates != null) {
/*  678 */         List templates = (List)adminQueryTemplates.get(bmClassId);
/*  679 */         if (templates != null) {
/*  680 */           queryTemplates.addAll(templates);
/*      */         }
/*      */       }
/*      */     }
/*  684 */     Comparator comp = new Comparator() {
/*      */       public int compare(Object o1, Object o2) {
/*  686 */         TemplateMeta t1 = (TemplateMeta)o1;
/*  687 */         TemplateMeta t2 = (TemplateMeta)o2;
/*  688 */         if (t1.getSortNum() <= t2.getSortNum()) {
/*  689 */           return 0;
/*      */         }
/*  691 */         return 1;
/*      */       }
/*      */     };
/*  695 */     if (queryTemplates != null) {
/*  696 */       Collections.sort(queryTemplates, comp);
/*      */     }
/*  698 */     return queryTemplates;
/*      */   }
/*      */ 
/*      */   public void setPropertyMeta(IServiceActionContext actionContext, String bmClassId, ExtAttrMetaGroup propertyMeta) throws UserException
/*      */   {
/*  703 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  704 */     String userId = actionContext.getUserId();
/*  705 */     DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/*  706 */     drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/*  707 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", userId));
/*  708 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("GROUP_TYPE", "=", "2"));
/*  709 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "=", bmClassId));
/*  710 */     DrmAttrGroup dag = new DrmAttrGroup();
/*  711 */     IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmAttrGroupQuery, dag);
/*  712 */     List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/*      */ 
/*  714 */     String drmAttrGroupCuid = null;
/*  715 */     if (drmAttrGroupRows.size() > 0) {
/*  716 */       IDrmQueryRow drmAttrGroupRow = (IDrmQueryRow)drmAttrGroupRows.get(0);
/*  717 */       DrmAttrGroup drmAttrGroup = (DrmAttrGroup)drmAttrGroupRow.getResultDbo("DRM_ATTR_GROUP");
/*  718 */       drmAttrGroupCuid = drmAttrGroup.getCuid();
/*      */ 
/*  720 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/*  721 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/*  722 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/*  723 */       queryContext.setOrderField("SORT_NO");
/*  724 */       DrmGroupAttr dga = new DrmGroupAttr();
/*  725 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/*  726 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/*  727 */       List idos = new ArrayList();
/*  728 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/*  729 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/*  730 */         idos.add(dbobj);
/*      */       }
/*  732 */       if ((idos != null) && (idos.size() > 0))
/*  733 */         getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */     }
/*      */     else {
/*  736 */       DrmAttrGroup drmAttrGroup = new DrmAttrGroup();
/*  737 */       drmAttrGroup.setBmClassid(bmClassId);
/*  738 */       drmAttrGroup.setGroupType(2L);
/*  739 */       drmAttrGroup.setRelatedUserCuid(userId);
/*  740 */       getDynResManageService().addDynObject(actionContext, drmAttrGroup, false);
/*  741 */       drmAttrGroupCuid = drmAttrGroup.getCuid();
/*      */     }
/*      */ 
/*  744 */     propertyMeta.setGroupCuid(drmAttrGroupCuid);
/*      */ 
/*  746 */     List drmGroupAttrList = new ArrayList();
/*  747 */     List extAttrMetas = propertyMeta.getExtAttrMetas();
/*  748 */     int i = 1;
/*  749 */     for (ExtAttrMeta attrMeta : extAttrMetas) {
/*  750 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/*  751 */       drmGroupAttr.setAttrId(attrMeta.getAttrId());
/*  752 */       drmGroupAttr.setBmClassid(bmClassId);
/*  753 */       drmGroupAttr.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/*  754 */       drmGroupAttr.setSortNo(i);
/*      */ 
/*  756 */       if ((attrMeta.getExtMetas() != null) && (attrMeta.getExtMetas().get(ExtAttrNameEnum.Property.ATTR_ROW_NO) != null)) {
/*  757 */         drmGroupAttr.setExtVal1(attrMeta.getExtMetas().get(ExtAttrNameEnum.Property.ATTR_ROW_NO).toString());
/*      */       }
/*  759 */       if ((attrMeta.getExtMetas() != null) && (attrMeta.getExtMetas().get(ExtAttrNameEnum.Property.ATTR_COL_NO) != null)) {
/*  760 */         drmGroupAttr.setExtVal2(attrMeta.getExtMetas().get(ExtAttrNameEnum.Property.ATTR_COL_NO).toString());
/*      */       }
/*  762 */       drmGroupAttrList.add(drmGroupAttr);
/*  763 */       i++;
/*      */     }
/*      */ 
/*  766 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/*  768 */     Map userProptertyMeta = (Map)this.userPropertyMetas.get(userId);
/*  769 */     if (userProptertyMeta == null) {
/*  770 */       userProptertyMeta = new HashMap();
/*  771 */       this.userPropertyMetas.put(userId, userProptertyMeta);
/*      */     }
/*  773 */     userProptertyMeta.put(bmClassId, propertyMeta);
/*      */ 
/*  776 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/*  777 */     if (groupAttrs == null)
/*  778 */       this.attrGroups.put(drmAttrGroupCuid, propertyMeta);
/*      */     else
/*  780 */       groupAttrs = propertyMeta;
/*      */   }
/*      */ 
/*      */   public void setQueryTableMeta(IServiceActionContext actionContext, String bmClassId, ExtAttrMetaGroup queryTableMeta)
/*      */     throws UserException
/*      */   {
/*  786 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*  787 */     String userId = actionContext.getUserId();
/*      */ 
/*  789 */     DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/*  790 */     drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/*  791 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", userId));
/*  792 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("GROUP_TYPE", "=", "1"));
/*  793 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "=", bmClassId));
/*  794 */     DrmAttrGroup dag = new DrmAttrGroup();
/*  795 */     IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmAttrGroupQuery, dag);
/*  796 */     List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/*      */ 
/*  798 */     String drmAttrGroupCuid = null;
/*  799 */     if (drmAttrGroupRows.size() > 0) {
/*  800 */       IDrmQueryRow drmAttrGroupRow = (IDrmQueryRow)drmAttrGroupRows.get(0);
/*  801 */       DrmAttrGroup drmAttrGroup = (DrmAttrGroup)drmAttrGroupRow.getResultDbo("DRM_ATTR_GROUP");
/*  802 */       drmAttrGroupCuid = drmAttrGroup.getCuid();
/*      */ 
/*  804 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/*  805 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/*  806 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/*  807 */       queryContext.setOrderField("SORT_NO");
/*  808 */       DrmGroupAttr dga = new DrmGroupAttr();
/*  809 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/*  810 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/*  811 */       List idos = new ArrayList();
/*  812 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/*  813 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/*  814 */         idos.add(dbobj);
/*      */       }
/*  816 */       if ((idos != null) && (idos.size() > 0))
/*  817 */         getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */     }
/*      */     else {
/*  820 */       DrmAttrGroup drmAttrGroup = new DrmAttrGroup();
/*  821 */       drmAttrGroup.setBmClassid(bmClassId);
/*  822 */       drmAttrGroup.setGroupType(1L);
/*  823 */       drmAttrGroup.setRelatedUserCuid(userId);
/*  824 */       getDynResManageService().addDynObject(actionContext, drmAttrGroup, false);
/*  825 */       drmAttrGroupCuid = drmAttrGroup.getCuid();
/*      */     }
/*      */ 
/*  828 */     queryTableMeta.setGroupCuid(drmAttrGroupCuid);
/*      */ 
/*  830 */     List drmGroupAttrList = new ArrayList();
/*  831 */     List attrIds = queryTableMeta.getGroupAttrIds();
/*  832 */     for (int i = 0; i < attrIds.size(); i++) {
/*  833 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/*  834 */       drmGroupAttr.setAttrId((String)attrIds.get(i));
/*  835 */       drmGroupAttr.setBmClassid(bmClassId);
/*  836 */       drmGroupAttr.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/*  837 */       drmGroupAttr.setSortNo(i + 1);
/*  838 */       drmGroupAttrList.add(drmGroupAttr);
/*      */     }
/*  840 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/*  842 */     Map userQueryTableMeta = (Map)this.userQueryTableMetas.get(userId);
/*  843 */     if (userQueryTableMeta == null) {
/*  844 */       userQueryTableMeta = new HashMap();
/*  845 */       this.userQueryTableMetas.put(userId, userQueryTableMeta);
/*      */     }
/*  847 */     userQueryTableMeta.put(bmClassId, queryTableMeta);
/*      */ 
/*  850 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/*  851 */     if (groupAttrs == null)
/*  852 */       this.attrGroups.put(drmAttrGroupCuid, queryTableMeta);
/*      */     else
/*  854 */       groupAttrs = queryTableMeta;
/*      */   }
/*      */ 
/*      */   public void modifyQueryTableMeta(IServiceActionContext actionContext, String bmClassId, ExtAttrMetaGroup queryTableMeta)
/*      */     throws UserException
/*      */   {
/*  860 */     String userId = actionContext.getUserId();
/*  861 */     Map userQueryTableMeta = (Map)this.userQueryTableMetas.get(userId);
/*  862 */     if (userQueryTableMeta == null) {
/*  863 */       userQueryTableMeta = new HashMap();
/*  864 */       this.userQueryTableMetas.put(userId, userQueryTableMeta);
/*      */     }
/*  866 */     if (userQueryTableMeta.get(bmClassId) == null) {
/*  867 */       setQueryTableMeta(actionContext, bmClassId, queryTableMeta);
/*      */     }
/*  869 */     userQueryTableMeta.remove(bmClassId);
/*      */ 
/*  872 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/*  875 */     DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/*  876 */     drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/*  877 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_USER_CUID", "=", userId));
/*  878 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("GROUP_TYPE", "=", "1"));
/*  879 */     drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "=", bmClassId));
/*  880 */     DrmAttrGroup dag = new DrmAttrGroup();
/*  881 */     IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmAttrGroupQuery, dag);
/*  882 */     List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/*      */ 
/*  884 */     String drmAttrGroupCuid = null;
/*      */     List extAttrMetas;
/*  885 */     if (drmAttrGroupRows.size() > 0) {
/*  886 */       IDrmQueryRow drmAttrGroupRow = (IDrmQueryRow)drmAttrGroupRows.get(0);
/*  887 */       DrmAttrGroup drmAttrGroup = (DrmAttrGroup)drmAttrGroupRow.getResultDbo("DRM_ATTR_GROUP");
/*  888 */       drmAttrGroupCuid = drmAttrGroup.getCuid();
/*      */ 
/*  891 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/*  892 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/*  893 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/*  894 */       queryContext.setOrderField("SORT_NO");
/*  895 */       DrmGroupAttr dga = new DrmGroupAttr();
/*  896 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/*  897 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/*  898 */       extAttrMetas = queryTableMeta.getExtAttrMetas();
/*      */ 
/*  900 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/*  901 */         DrmGroupAttr drmGroupAttr = (DrmGroupAttr)row.getResultDbo("DRM_GROUP_ATTR");
/*  902 */         int i = 0;
/*  903 */         for (ExtAttrMeta attrMeta : extAttrMetas) {
/*  904 */           if (drmGroupAttr.getAttrId().equals(attrMeta.getAttrId())) {
/*  905 */             if (attrMeta.getExtMetas() != null) {
/*  906 */               drmGroupAttr.setExtVal1((String)attrMeta.getExtMetas().get(ExtAttrNameEnum.QueryTable.COL_WIDTH));
/*      */             }
/*  908 */             drmGroupAttr.setSortNo(i);
/*  909 */             break;
/*      */           }
/*  911 */           i++;
/*      */         }
/*  913 */         getDynResManageService().modifyDynObject(actionContext, drmGroupAttr, false);
/*      */       }
/*      */     }
/*  916 */     queryTableMeta.setGroupCuid(drmAttrGroupCuid);
/*  917 */     userQueryTableMeta.put(bmClassId, queryTableMeta);
/*      */ 
/*  920 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/*  921 */     if (groupAttrs == null)
/*  922 */       this.attrGroups.put(drmAttrGroupCuid, queryTableMeta);
/*      */     else
/*  924 */       groupAttrs = queryTableMeta;
/*      */   }
/*      */ 
/*      */   public void addQueryTemplate(IServiceActionContext actionContext, String bmClassId, TemplateMeta queryTemplate) throws UserException
/*      */   {
/*  929 */     String userId = actionContext.getUserId();
/*  930 */     Map classQueryTemplates = (Map)this.userQueryTemplates.get(userId);
/*      */ 
/*  932 */     if (classQueryTemplates == null) {
/*  933 */       classQueryTemplates = new HashMap();
/*  934 */       this.userQueryTemplates.put(userId, classQueryTemplates);
/*      */     }
/*      */ 
/*  937 */     List queryTemplates = (List)classQueryTemplates.get(bmClassId);
/*  938 */     if (queryTemplates == null) {
/*  939 */       queryTemplates = new ArrayList();
/*  940 */       classQueryTemplates.put(bmClassId, queryTemplates);
/*      */     }
/*      */ 
/*  943 */     DrmAttrGroup attrGroup = new DrmAttrGroup();
/*  944 */     attrGroup.setBmClassid(bmClassId);
/*  945 */     attrGroup.setGroupType(6L);
/*  946 */     attrGroup.setRelatedUserCuid(userId);
/*  947 */     getDynResManageService().addDynObject(actionContext, attrGroup, false);
/*  948 */     String attrGroupCuid = attrGroup.getCuid();
/*      */ 
/*  950 */     DrmQueryTemplate drmQueryTemplate = new DrmQueryTemplate();
/*  951 */     drmQueryTemplate.setLabelCn(queryTemplate.getTemplateLabelCn());
/*  952 */     drmQueryTemplate.setRelatedAttrGroupCuid(attrGroupCuid);
/*  953 */     drmQueryTemplate.setRelatedUserCuid(userId);
/*  954 */     String sqlTemplate = (String)queryTemplate.getTemplateExtMetas().get(TemplateExtEnum.QueryTemplate.SQL_TEMPLATE);
/*  955 */     drmQueryTemplate.setSqlTemplate(sqlTemplate);
/*  956 */     getDynResManageService().addDynObject(actionContext, drmQueryTemplate, false);
/*      */ 
/*  958 */     List drmGroupAttrList = new ArrayList();
/*  959 */     List attrIds = queryTemplate.getExtAttrMetaGroup().getGroupAttrIds();
/*  960 */     List attrMetas = queryTemplate.getExtAttrMetaGroup().getExtAttrMetas();
/*  961 */     for (int i = 0; i < attrIds.size(); i++) {
/*  962 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/*  963 */       drmGroupAttr.setAttrId((String)attrIds.get(i));
/*  964 */       drmGroupAttr.setBmClassid(bmClassId);
/*  965 */       drmGroupAttr.setRelatedAttrGroupCuid(attrGroupCuid);
/*  966 */       drmGroupAttr.setSortNo(i);
/*  967 */       ExtAttrMeta attrMeta = (ExtAttrMeta)attrMetas.get(i);
/*      */ 
/*  969 */       drmGroupAttr.setExtVal1((String)attrMeta.getExtMetas().get(ExtAttrNameEnum.QueryTemplate.ATTR_RELATION));
/*      */ 
/*  971 */       drmGroupAttr.setExtVal2((String)attrMeta.getExtMetas().get(ExtAttrNameEnum.QueryTemplate.ATTR_DEFAULT_VALUE));
/*  972 */       drmGroupAttrList.add(drmGroupAttr);
/*      */     }
/*  974 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*  975 */     queryTemplate.setTemplateCuid(drmQueryTemplate.getCuid());
/*  976 */     queryTemplate.getExtAttrMetaGroup().setBmClassId(bmClassId);
/*  977 */     queryTemplate.getExtAttrMetaGroup().setGroupCuid(attrGroupCuid);
/*  978 */     queryTemplate.getExtAttrMetaGroup().setGroupType(6);
/*  979 */     queryTemplate.getExtAttrMetaGroup().setUserCuid(userId);
/*  980 */     queryTemplates.add(queryTemplate);
/*      */ 
/*  983 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(attrGroupCuid);
/*  984 */     if (groupAttrs == null)
/*  985 */       this.attrGroups.put(attrGroupCuid, queryTemplate.getExtAttrMetaGroup());
/*      */     else
/*  987 */       groupAttrs = queryTemplate.getExtAttrMetaGroup();
/*      */   }
/*      */ 
/*      */   public void modifyQueryTemplatesSortNo(IServiceActionContext actionContext, String bmClassId, Map<String, TemplateMeta> templates)
/*      */     throws UserException
/*      */   {
/*  993 */     String userId = actionContext.getUserId();
/*  994 */     Map classQueryTemplates = (Map)this.userQueryTemplates.get(userId);
/*      */ 
/*  996 */     if (classQueryTemplates == null) {
/*  997 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1000 */     List queryTemplates = (List)classQueryTemplates.get(bmClassId);
/* 1001 */     if (queryTemplates == null) {
/* 1002 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1005 */     int index = -1;
/* 1006 */     String templateCuid = null;
/* 1007 */     for (int i = 0; (i < queryTemplates.size()) && 
/* 1008 */       (templates.size() != 0); i++)
/*      */     {
/* 1011 */       TemplateMeta _queryTemplate = (TemplateMeta)queryTemplates.get(i);
/* 1012 */       if (templates.get(_queryTemplate.getTemplateLabelCn()) != null) {
/* 1013 */         index = i;
/* 1014 */         templateCuid = _queryTemplate.getTemplateCuid();
/*      */ 
/* 1016 */         IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1018 */         DrmQueryTemplate drmQueryTemplate = new DrmQueryTemplate();
/* 1019 */         drmQueryTemplate.setCuid(templateCuid);
/* 1020 */         drmQueryTemplate = (DrmQueryTemplate)getDynResManageService().getDynObject(actionContext, queryContext, drmQueryTemplate);
/* 1021 */         if (drmQueryTemplate != null) {
/* 1022 */           drmQueryTemplate.setSortNo(((TemplateMeta)templates.get(_queryTemplate.getTemplateLabelCn())).getSortNum());
/* 1023 */           getDynResManageService().modifyDynObject(actionContext, drmQueryTemplate, false);
/* 1024 */           TemplateMeta oldQueryTemplate = (TemplateMeta)queryTemplates.get(index);
/* 1025 */           oldQueryTemplate.setSortNum(drmQueryTemplate.getSortNo());
/* 1026 */           queryTemplates.remove(index);
/* 1027 */           queryTemplates.add(index, oldQueryTemplate);
/*      */         }
/* 1029 */         templates.remove(templateCuid);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void modifyQueryTemplate(IServiceActionContext actionContext, String bmClassId, TemplateMeta queryTemplate) throws UserException {
/* 1035 */     String userId = actionContext.getUserId();
/* 1036 */     Map classQueryTemplates = (Map)this.userQueryTemplates.get(userId);
/*      */ 
/* 1038 */     if (classQueryTemplates == null) {
/* 1039 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1042 */     List queryTemplates = (List)classQueryTemplates.get(bmClassId);
/* 1043 */     if (queryTemplates == null) {
/* 1044 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1047 */     int index = -1;
/* 1048 */     String templateCuid = null;
/* 1049 */     for (int i = 0; i < queryTemplates.size(); i++) {
/* 1050 */       TemplateMeta _queryTemplate = (TemplateMeta)queryTemplates.get(i);
/* 1051 */       if (_queryTemplate.getTemplateCuid().equals(queryTemplate.getTemplateCuid())) {
/* 1052 */         index = i;
/* 1053 */         templateCuid = _queryTemplate.getTemplateCuid();
/* 1054 */         break;
/*      */       }
/*      */     }
/* 1057 */     if (index >= 0)
/*      */     {
/* 1059 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1062 */       DrmQueryTemplate drmQueryTemplate = new DrmQueryTemplate();
/* 1063 */       drmQueryTemplate.setCuid(templateCuid);
/* 1064 */       drmQueryTemplate = (DrmQueryTemplate)getDynResManageService().getDynObject(actionContext, queryContext, drmQueryTemplate);
/*      */ 
/* 1066 */       if (drmQueryTemplate != null) {
/* 1067 */         String attrGroupCuid = drmQueryTemplate.getRelatedAttrGroupCuid();
/*      */ 
/* 1069 */         drmQueryTemplate.setLabelCn(queryTemplate.getTemplateLabelCn());
/* 1070 */         String sqlTemplate = (String)queryTemplate.getTemplateExtMetas().get(TemplateExtEnum.QueryTemplate.SQL_TEMPLATE);
/* 1071 */         drmQueryTemplate.setSqlTemplate(sqlTemplate);
/* 1072 */         getDynResManageService().modifyDynObject(actionContext, drmQueryTemplate, false);
/*      */ 
/* 1075 */         DrmSingleClassQuery groupAttrQuery = new DrmSingleClassQuery();
/* 1076 */         groupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1077 */         groupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmQueryTemplate.getRelatedAttrGroupCuid()));
/* 1078 */         DrmGroupAttr dga = new DrmGroupAttr();
/* 1079 */         IDrmQueryResultSet groupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, groupAttrQuery, dga);
/* 1080 */         List groupAttrRows = groupAttrRs.getResultSet();
/* 1081 */         List idos = new ArrayList();
/* 1082 */         for (IDrmQueryRow groupAttrRow : groupAttrRows) {
/* 1083 */           IDrmDataObject dbobj = groupAttrRow.getResultDbo("DRM_GROUP_ATTR");
/* 1084 */           idos.add(dbobj);
/*      */         }
/* 1086 */         if ((idos != null) && (idos.size() > 0)) {
/* 1087 */           getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */         }
/*      */ 
/* 1091 */         List drmGroupAttrList = new ArrayList();
/* 1092 */         List attrIds = queryTemplate.getExtAttrMetaGroup().getGroupAttrIds();
/* 1093 */         List attrMetas = queryTemplate.getExtAttrMetaGroup().getExtAttrMetas();
/* 1094 */         for (int i = 0; i < attrIds.size(); i++) {
/* 1095 */           DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 1096 */           drmGroupAttr.setAttrId((String)attrIds.get(i));
/* 1097 */           drmGroupAttr.setBmClassid(bmClassId);
/* 1098 */           drmGroupAttr.setRelatedAttrGroupCuid(drmQueryTemplate.getRelatedAttrGroupCuid());
/* 1099 */           drmGroupAttr.setSortNo(i);
/* 1100 */           ExtAttrMeta attrMeta = (ExtAttrMeta)attrMetas.get(i);
/*      */ 
/* 1102 */           drmGroupAttr.setExtVal1((String)attrMeta.getExtMetas().get(ExtAttrNameEnum.QueryTemplate.ATTR_RELATION));
/*      */ 
/* 1104 */           drmGroupAttr.setExtVal2((String)attrMeta.getExtMetas().get(ExtAttrNameEnum.QueryTemplate.ATTR_DEFAULT_VALUE));
/* 1105 */           drmGroupAttrList.add(drmGroupAttr);
/*      */         }
/* 1107 */         getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/* 1109 */         queryTemplates.remove(index);
/* 1110 */         queryTemplates.add(index, queryTemplate);
/*      */ 
/* 1112 */         queryTemplate.getExtAttrMetaGroup().setBmClassId(bmClassId);
/* 1113 */         queryTemplate.getExtAttrMetaGroup().setGroupCuid(attrGroupCuid);
/* 1114 */         queryTemplate.getExtAttrMetaGroup().setGroupType(6);
/* 1115 */         queryTemplate.getExtAttrMetaGroup().setUserCuid(userId);
/*      */ 
/* 1117 */         ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(attrGroupCuid);
/* 1118 */         if (groupAttrs == null)
/* 1119 */           this.attrGroups.put(attrGroupCuid, queryTemplate.getExtAttrMetaGroup());
/*      */         else
/* 1121 */           groupAttrs = queryTemplate.getExtAttrMetaGroup();
/*      */       }
/*      */       else {
/* 1124 */         throw new UserException("");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteQueryTemplate(IServiceActionContext actionContext, String bmClassId, TemplateMeta queryTemplate) throws UserException
/*      */   {
/* 1131 */     String userId = actionContext.getUserId();
/* 1132 */     Map classQueryTemplates = (Map)this.userQueryTemplates.get(userId);
/*      */ 
/* 1134 */     if (classQueryTemplates == null) {
/* 1135 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1138 */     List queryTemplates = (List)classQueryTemplates.get(bmClassId);
/* 1139 */     if (queryTemplates == null) {
/* 1140 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1143 */     int index = -1;
/* 1144 */     for (int i = 0; i < queryTemplates.size(); i++) {
/* 1145 */       TemplateMeta _queryTemplate = (TemplateMeta)queryTemplates.get(i);
/* 1146 */       if (_queryTemplate.getTemplateCuid().equals(queryTemplate.getTemplateCuid())) {
/* 1147 */         index = i;
/* 1148 */         break;
/*      */       }
/*      */     }
/* 1151 */     if (index >= 0)
/*      */     {
/* 1153 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1155 */       DrmQueryTemplate drmQueryTemplate = new DrmQueryTemplate();
/* 1156 */       drmQueryTemplate.setCuid(queryTemplate.getTemplateCuid());
/* 1157 */       drmQueryTemplate = (DrmQueryTemplate)getDynResManageService().getDynObject(actionContext, queryContext, drmQueryTemplate);
/*      */ 
/* 1159 */       if (drmQueryTemplate != null)
/*      */       {
/* 1161 */         DrmSingleClassQuery groupAttrQuery = new DrmSingleClassQuery();
/* 1162 */         groupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1163 */         groupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmQueryTemplate.getRelatedAttrGroupCuid()));
/* 1164 */         DrmGroupAttr dga = new DrmGroupAttr();
/* 1165 */         IDrmQueryResultSet groupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, groupAttrQuery, dga);
/* 1166 */         List groupAttrRows = groupAttrRs.getResultSet();
/* 1167 */         List idos = new ArrayList();
/* 1168 */         for (IDrmQueryRow groupAttrRow : groupAttrRows) {
/* 1169 */           IDrmDataObject dbobj = groupAttrRow.getResultDbo("DRM_GROUP_ATTR");
/* 1170 */           idos.add(dbobj);
/*      */         }
/* 1172 */         if ((idos != null) && (idos.size() > 0)) {
/* 1173 */           getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */         }
/*      */ 
/* 1177 */         DrmAttrGroup drmAttrGroup = new DrmAttrGroup();
/* 1178 */         drmAttrGroup.setCuid(drmQueryTemplate.getRelatedAttrGroupCuid());
/* 1179 */         drmAttrGroup = (DrmAttrGroup)getDynResManageService().getDynObject(actionContext, queryContext, drmAttrGroup);
/* 1180 */         getDynResManageService().deleteDynObject(actionContext, drmAttrGroup, false);
/*      */ 
/* 1182 */         getDynResManageService().deleteDynObject(actionContext, drmQueryTemplate, false);
/* 1183 */         queryTemplates.remove(index);
/*      */ 
/* 1186 */         this.attrGroups.remove(drmQueryTemplate.getRelatedAttrGroupCuid());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setQueryTemplate(IServiceActionContext actionContext, String bmClassId, TemplateMeta queryTemplate) throws UserException
/*      */   {
/*      */   }
/*      */ 
/*      */   public List<ExcelImpTemplateMeta> getExcelImpTemplates(IServiceActionContext actionContext, String bmClassId) throws UserException
/*      */   {
/* 1197 */     List excelImpTemplates = null;
/*      */ 
/* 1201 */     IUserDetails admin = getAuthenticationService().getAdmin(new ServiceActionContext());
/* 1202 */     String userId = admin.getUserId();
/* 1203 */     Map classExcelImpTemplates = (Map)this.userExcelImpTemplates.get(userId);
/* 1204 */     if (classExcelImpTemplates != null) {
/* 1205 */       excelImpTemplates = (List)classExcelImpTemplates.get(bmClassId);
/*      */     }
/*      */ 
/* 1208 */     return excelImpTemplates;
/*      */   }
/*      */ 
/*      */   private static IAuthenticationService getAuthenticationService() {
/* 1212 */     return (IAuthenticationService)ServiceHomeFactory.getInstance().getService("AuthenticationService");
/*      */   }
/*      */ 
/*      */   public List<TemplateMeta> getExcelExpTemplates(IServiceActionContext actionContext, String bmClassId) throws UserException
/*      */   {
/* 1217 */     List excelExpTemplates = new ArrayList();
/*      */ 
/* 1219 */     String userId = actionContext.getUserId();
/* 1220 */     List userList = new ArrayList();
/* 1221 */     userList.add(userId);
/* 1222 */     IUserDetails admin = getAuthenticationService().getAdmin(new ServiceActionContext());
/* 1223 */     if ((admin != null) && (!admin.getUserId().equals(userId))) {
/* 1224 */       userList.add(admin.getUserId());
/*      */     }
/* 1226 */     for (int i = 0; i < userList.size(); i++) {
/* 1227 */       Map classExcelExpTemplates = (Map)this.userExcelExpTemplates.get(userList.get(i));
/* 1228 */       if (classExcelExpTemplates != null) {
/* 1229 */         List excelExpTemplatesTemp = (List)classExcelExpTemplates.get(bmClassId);
/* 1230 */         if (excelExpTemplatesTemp != null) {
/* 1231 */           excelExpTemplates.addAll(excelExpTemplatesTemp);
/*      */         }
/*      */       }
/*      */     }
/* 1235 */     return excelExpTemplates;
/*      */   }
/*      */ 
/*      */   public ExcelImpTemplateMeta getExcelImpTemplate(IServiceActionContext actionContext, String bmClassId, String cuid) throws UserException {
/* 1239 */     ExcelImpTemplateMeta excelImpTemplate = null;
/* 1240 */     List excelImpTemplates = getExcelImpTemplates(actionContext, bmClassId);
/* 1241 */     if (excelImpTemplates != null) {
/* 1242 */       for (int i = 0; i < excelImpTemplates.size(); i++) {
/* 1243 */         excelImpTemplate = (ExcelImpTemplateMeta)excelImpTemplates.get(i);
/* 1244 */         String sCuid = excelImpTemplate.getTemplateCuid();
/* 1245 */         if (sCuid.equals(cuid)) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1250 */     return excelImpTemplate;
/*      */   }
/*      */ 
/*      */   public List<BMAttrMeta> getExcelExpAttrList(IServiceActionContext actionContext, String bmClassId, String cuid) throws UserException {
/* 1254 */     List attrs = new ArrayList();
/* 1255 */     List expTemp = getExcelExpTemplates(actionContext, bmClassId);
/* 1256 */     if (expTemp != null) {
/* 1257 */       for (TemplateMeta tm : expTemp) {
/* 1258 */         if (tm.getTemplateCuid().equals(cuid)) {
/* 1259 */           List attrIds = tm.getExtAttrMetaGroup().getGroupAttrIds();
/* 1260 */           for (String attrId : attrIds) {
/* 1261 */             attrs.add(getBMModelService().getAttrMeta(actionContext, bmClassId, attrId));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1266 */     return attrs;
/*      */   }
/*      */ 
/*      */   public ExcelImpTemplateMeta addExcelImpTemplate(IServiceActionContext actionContext, String bmClassId, ExcelImpTemplateMeta template) throws UserException
/*      */   {
/* 1271 */     String userId = actionContext.getUserId();
/* 1272 */     Map classExcelImpTemplates = (Map)this.userExcelImpTemplates.get(userId);
/*      */ 
/* 1274 */     if (classExcelImpTemplates == null) {
/* 1275 */       classExcelImpTemplates = new HashMap();
/* 1276 */       this.userExcelImpTemplates.put(userId, classExcelImpTemplates);
/*      */     }
/*      */ 
/* 1279 */     List excelImpTemplates = (List)classExcelImpTemplates.get(bmClassId);
/* 1280 */     if (excelImpTemplates == null) {
/* 1281 */       excelImpTemplates = new ArrayList();
/* 1282 */       classExcelImpTemplates.put(bmClassId, excelImpTemplates);
/*      */     }
/*      */ 
/* 1287 */     List drmAttrGroupList = new ArrayList();
/* 1288 */     DrmAttrGroup drmAttrGroup = new DrmAttrGroup();
/* 1289 */     drmAttrGroup.setBmClassid(bmClassId);
/* 1290 */     drmAttrGroup.setGroupType(3L);
/* 1291 */     drmAttrGroup.setRelatedUserCuid(userId);
/* 1292 */     drmAttrGroupList.add(drmAttrGroup);
/* 1293 */     getDynResManageService().addDynObjects(actionContext, drmAttrGroupList, false);
/*      */ 
/* 1295 */     String drmAttrGroupCuid = drmAttrGroup.getCuid();
/* 1296 */     template.getExtAttrMetaGroup().setGroupCuid(drmAttrGroupCuid);
/*      */ 
/* 1299 */     List drmGroupAttrList = new ArrayList();
/* 1300 */     List attrIds = template.getExtAttrMetaGroup().getGroupAttrIds();
/* 1301 */     for (int i = 0; i < attrIds.size(); i++) {
/* 1302 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 1303 */       drmGroupAttr.setAttrId((String)attrIds.get(i));
/* 1304 */       drmGroupAttr.setBmClassid(bmClassId);
/* 1305 */       drmGroupAttr.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/* 1306 */       drmGroupAttr.setSortNo(i);
/* 1307 */       drmGroupAttrList.add(drmGroupAttr);
/*      */     }
/* 1309 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/* 1312 */     List impTemplateList = new ArrayList();
/* 1313 */     DrmExlImpTemplate drmExlImpTemplate = new DrmExlImpTemplate();
/* 1314 */     drmExlImpTemplate.setIsValid(template.getIsValid());
/* 1315 */     drmExlImpTemplate.setLabelCn(template.getTemplateLabelCn());
/* 1316 */     drmExlImpTemplate.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/* 1317 */     drmExlImpTemplate.setRelatedUserCuid(userId);
/* 1318 */     impTemplateList.add(drmExlImpTemplate);
/* 1319 */     getDynResManageService().addDynObjects(actionContext, impTemplateList, false);
/*      */ 
/* 1321 */     String drmExlImpTemplateCuid = drmExlImpTemplate.getCuid();
/*      */ 
/* 1323 */     template.setTemplateCuid(drmExlImpTemplateCuid);
/*      */ 
/* 1326 */     List drmExlClassUniqueList = new ArrayList();
/* 1327 */     Map excelRelatedList = template.getExcelImpRelateds();
/* 1328 */     Collection bufs = excelRelatedList.values();
/* 1329 */     Iterator iter = bufs.iterator();
/* 1330 */     while (iter.hasNext()) {
/* 1331 */       Map classRelatedUnique = (Map)iter.next();
/*      */ 
/* 1333 */       Collection classBufs = classRelatedUnique.values();
/* 1334 */       Iterator classIter = classBufs.iterator();
/* 1335 */       if (classIter.hasNext()) {
/* 1336 */         ExcelImpTemplateRelatedMeta templateRelatedMeta = (ExcelImpTemplateRelatedMeta)classIter.next();
/* 1337 */         DrmExlClassUnique drmClassUnique = new DrmExlClassUnique();
/* 1338 */         drmClassUnique.setClassUniqueName(templateRelatedMeta.getClassUniqueName());
/* 1339 */         drmClassUnique.setRelatedBmClassid(templateRelatedMeta.getTemplateRelatedBmClassId());
/* 1340 */         drmClassUnique.setRelatedExcelTemplateCuid(drmExlImpTemplateCuid);
/* 1341 */         drmClassUnique.setParentAttrid(templateRelatedMeta.getParentAttrId());
/* 1342 */         drmClassUnique.setParentBmClassid(templateRelatedMeta.getParentBmClassId());
/* 1343 */         drmClassUnique.setRelatedOrUnique(templateRelatedMeta.getRelatedOrUnique());
/*      */ 
/* 1345 */         drmExlClassUniqueList.add(drmClassUnique);
/*      */       }
/*      */     }
/* 1348 */     if (drmExlClassUniqueList.size() > 0) {
/* 1349 */       getDynResManageService().addDynObjects(actionContext, drmExlClassUniqueList, false);
/*      */     }
/*      */ 
/* 1352 */     excelImpTemplates.add(template);
/*      */ 
/* 1354 */     template.getExtAttrMetaGroup().setBmClassId(bmClassId);
/* 1355 */     template.getExtAttrMetaGroup().setGroupCuid(drmAttrGroupCuid);
/* 1356 */     template.getExtAttrMetaGroup().setGroupType(3);
/* 1357 */     template.getExtAttrMetaGroup().setUserCuid(userId);
/*      */ 
/* 1359 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/* 1360 */     if (groupAttrs == null)
/* 1361 */       this.attrGroups.put(drmAttrGroupCuid, template.getExtAttrMetaGroup());
/*      */     else {
/* 1363 */       groupAttrs = template.getExtAttrMetaGroup();
/*      */     }
/*      */ 
/* 1366 */     return template;
/*      */   }
/*      */ 
/*      */   public ExcelImpTemplateMeta addExcelExpTemplate(IServiceActionContext actionContext, String bmClassId, ExcelImpTemplateMeta template) throws UserException
/*      */   {
/* 1371 */     String userId = actionContext.getUserId();
/* 1372 */     Map classTemps = (Map)this.userExcelExpTemplates.get(userId);
/*      */ 
/* 1374 */     if (classTemps == null) {
/* 1375 */       classTemps = new HashMap();
/* 1376 */       this.userExcelExpTemplates.put(userId, classTemps);
/*      */     }
/*      */ 
/* 1379 */     List temps = (List)classTemps.get(bmClassId);
/* 1380 */     if (temps == null) {
/* 1381 */       temps = new ArrayList();
/* 1382 */       classTemps.put(bmClassId, temps);
/*      */     }
/*      */ 
/* 1387 */     List drmAttrGroupList = new ArrayList();
/* 1388 */     DrmAttrGroup drmAttrGroup = new DrmAttrGroup();
/* 1389 */     drmAttrGroup.setBmClassid(bmClassId);
/* 1390 */     drmAttrGroup.setGroupType(ExtAttrMetaGroup.GROUP_TYPE_ENUM.EXPORT);
/* 1391 */     drmAttrGroup.setRelatedUserCuid(userId);
/* 1392 */     drmAttrGroupList.add(drmAttrGroup);
/* 1393 */     getDynResManageService().addDynObjects(actionContext, drmAttrGroupList, false);
/*      */ 
/* 1395 */     String drmAttrGroupCuid = drmAttrGroup.getCuid();
/* 1396 */     template.getExtAttrMetaGroup().setGroupCuid(drmAttrGroupCuid);
/*      */ 
/* 1399 */     List drmGroupAttrList = new ArrayList();
/* 1400 */     List attrIds = template.getExtAttrMetaGroup().getGroupAttrIds();
/* 1401 */     for (int i = 0; i < attrIds.size(); i++) {
/* 1402 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 1403 */       drmGroupAttr.setAttrId((String)attrIds.get(i));
/* 1404 */       drmGroupAttr.setBmClassid(bmClassId);
/* 1405 */       drmGroupAttr.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/* 1406 */       drmGroupAttr.setSortNo(i);
/* 1407 */       drmGroupAttrList.add(drmGroupAttr);
/*      */     }
/* 1409 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/* 1412 */     List expTemplateList = new ArrayList();
/* 1413 */     DrmExlExpTemplate dbo = new DrmExlExpTemplate();
/* 1414 */     dbo.setLabelCn(template.getTemplateLabelCn());
/* 1415 */     dbo.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/* 1416 */     dbo.setRelatedUserCuid(userId);
/* 1417 */     expTemplateList.add(dbo);
/* 1418 */     getDynResManageService().addDynObjects(actionContext, expTemplateList, false);
/*      */ 
/* 1420 */     template.setTemplateCuid(dbo.getCuid());
/* 1421 */     temps.add(template);
/*      */ 
/* 1423 */     template.getExtAttrMetaGroup().setBmClassId(bmClassId);
/* 1424 */     template.getExtAttrMetaGroup().setGroupCuid(drmAttrGroupCuid);
/* 1425 */     template.getExtAttrMetaGroup().setGroupType(ExtAttrMetaGroup.GROUP_TYPE_ENUM.EXPORT);
/* 1426 */     template.getExtAttrMetaGroup().setUserCuid(userId);
/*      */ 
/* 1428 */     ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/* 1429 */     if (groupAttrs == null)
/* 1430 */       this.attrGroups.put(drmAttrGroupCuid, template.getExtAttrMetaGroup());
/*      */     else {
/* 1432 */       groupAttrs = template.getExtAttrMetaGroup();
/*      */     }
/*      */ 
/* 1435 */     return template;
/*      */   }
/*      */ 
/*      */   public void modifyExcelImpTemplate(IServiceActionContext actionContext, String bmClassId, ExcelImpTemplateMeta template) throws UserException
/*      */   {
/* 1440 */     String userId = actionContext.getUserId();
/* 1441 */     Map classExcelImpTemplates = (Map)this.userExcelImpTemplates.get(userId);
/*      */ 
/* 1443 */     if (classExcelImpTemplates == null) {
/* 1444 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1447 */     List excelImpTemplates = (List)classExcelImpTemplates.get(bmClassId);
/* 1448 */     if (excelImpTemplates == null) {
/* 1449 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1452 */     int index = -1;
/* 1453 */     for (int i = 0; i < excelImpTemplates.size(); i++) {
/* 1454 */       ExcelImpTemplateMeta excelImpTemplate = (ExcelImpTemplateMeta)excelImpTemplates.get(i);
/* 1455 */       if (excelImpTemplate.getTemplateCuid().equals(template.getTemplateCuid())) {
/* 1456 */         index = i;
/* 1457 */         break;
/*      */       }
/*      */     }
/* 1460 */     if (index >= 0)
/*      */     {
/* 1462 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1467 */       String drmAttrGroupCuid = template.getExtAttrMetaGroup().getGroupCuid();
/* 1468 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/* 1469 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1470 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/* 1471 */       queryContext.setOrderField("SORT_NO");
/* 1472 */       DrmGroupAttr dga = new DrmGroupAttr();
/* 1473 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/* 1474 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/* 1475 */       List iGroupAttrDos = new ArrayList();
/* 1476 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/* 1477 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/* 1478 */         iGroupAttrDos.add(dbobj);
/*      */       }
/* 1480 */       if ((iGroupAttrDos != null) && (iGroupAttrDos.size() > 0)) {
/* 1481 */         getDynResManageService().deleteDynObjects(actionContext, iGroupAttrDos, false);
/*      */       }
/*      */ 
/* 1484 */       List drmGroupAttrList = new ArrayList();
/* 1485 */       List attrIds = template.getExtAttrMetaGroup().getGroupAttrIds();
/* 1486 */       for (int i = 0; i < attrIds.size(); i++) {
/* 1487 */         DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 1488 */         drmGroupAttr.setAttrId((String)attrIds.get(i));
/* 1489 */         drmGroupAttr.setBmClassid(bmClassId);
/* 1490 */         drmGroupAttr.setRelatedAttrGroupCuid(drmAttrGroupCuid);
/* 1491 */         drmGroupAttr.setSortNo(i);
/* 1492 */         drmGroupAttrList.add(drmGroupAttr);
/*      */       }
/* 1494 */       getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/* 1499 */       queryContext.setOrderField(null);
/* 1500 */       String templateCuid = template.getTemplateCuid();
/* 1501 */       DrmSingleClassQuery drmTemplateUniqueQuery = new DrmSingleClassQuery();
/* 1502 */       drmTemplateUniqueQuery.setBmClassId("DRM_EXL_CLASS_UNIQUE");
/* 1503 */       drmTemplateUniqueQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_EXCEL_TEMPLATE_CUID", "=", templateCuid));
/* 1504 */       DrmExlClassUnique dd = new DrmExlClassUnique();
/* 1505 */       IDrmQueryResultSet drmTemplateUniqueRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmTemplateUniqueQuery, dd);
/* 1506 */       List drmTemplateUniqueRows = drmTemplateUniqueRs.getResultSet();
/* 1507 */       List iTemplateUniqueDos = new ArrayList();
/* 1508 */       for (IDrmQueryRow row : drmTemplateUniqueRows) {
/* 1509 */         IDrmDataObject dbobj = row.getResultDbo("DRM_EXL_CLASS_UNIQUE");
/* 1510 */         iTemplateUniqueDos.add(dbobj);
/*      */       }
/* 1512 */       if ((iTemplateUniqueDos != null) && (iTemplateUniqueDos.size() > 0)) {
/* 1513 */         getDynResManageService().deleteDynObjects(actionContext, iTemplateUniqueDos, false);
/*      */       }
/*      */ 
/* 1516 */       List drmExlClassUniqueList = new ArrayList();
/* 1517 */       Map excelRelatedList = template.getExcelImpRelateds();
/* 1518 */       Collection bufs = excelRelatedList.values();
/* 1519 */       Iterator iter = bufs.iterator();
/* 1520 */       while (iter.hasNext()) {
/* 1521 */         Map classRelatedUnique = (Map)iter.next();
/*      */ 
/* 1523 */         Collection classBufs = classRelatedUnique.values();
/* 1524 */         Iterator classIter = classBufs.iterator();
/* 1525 */         if (classIter.hasNext()) {
/* 1526 */           ExcelImpTemplateRelatedMeta templateRelatedMeta = (ExcelImpTemplateRelatedMeta)classIter.next();
/*      */ 
/* 1528 */           DrmExlClassUnique drmClassUnique = new DrmExlClassUnique();
/* 1529 */           drmClassUnique.setClassUniqueName(templateRelatedMeta.getClassUniqueName());
/* 1530 */           drmClassUnique.setRelatedBmClassid(templateRelatedMeta.getTemplateRelatedBmClassId());
/* 1531 */           drmClassUnique.setRelatedExcelTemplateCuid(templateCuid);
/* 1532 */           drmClassUnique.setParentAttrid(templateRelatedMeta.getParentAttrId());
/* 1533 */           drmClassUnique.setParentBmClassid(templateRelatedMeta.getParentBmClassId());
/* 1534 */           drmClassUnique.setRelatedOrUnique(templateRelatedMeta.getRelatedOrUnique());
/*      */ 
/* 1536 */           drmExlClassUniqueList.add(drmClassUnique);
/*      */         }
/*      */       }
/* 1539 */       if (drmExlClassUniqueList.size() > 0) {
/* 1540 */         getDynResManageService().addDynObjects(actionContext, drmExlClassUniqueList, false);
/*      */       }
/*      */ 
/* 1543 */       excelImpTemplates.remove(index);
/* 1544 */       excelImpTemplates.add(index, template);
/*      */ 
/* 1546 */       template.getExtAttrMetaGroup().setBmClassId(bmClassId);
/* 1547 */       template.getExtAttrMetaGroup().setGroupCuid(drmAttrGroupCuid);
/* 1548 */       template.getExtAttrMetaGroup().setGroupType(ExtAttrMetaGroup.GROUP_TYPE_ENUM.IMPORT);
/* 1549 */       template.getExtAttrMetaGroup().setUserCuid(userId);
/*      */ 
/* 1551 */       ExtAttrMetaGroup groupAttrs = (ExtAttrMetaGroup)this.attrGroups.get(drmAttrGroupCuid);
/* 1552 */       if (groupAttrs == null)
/* 1553 */         this.attrGroups.put(drmAttrGroupCuid, template.getExtAttrMetaGroup());
/*      */       else
/* 1555 */         groupAttrs = template.getExtAttrMetaGroup();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteExcelImpTemplate(IServiceActionContext actionContext, String bmClassId, ExcelImpTemplateMeta template)
/*      */     throws UserException
/*      */   {
/* 1562 */     String userId = actionContext.getUserId();
/* 1563 */     Map classExcelImpTemplates = (Map)this.userExcelImpTemplates.get(userId);
/*      */ 
/* 1565 */     if (classExcelImpTemplates == null) {
/* 1566 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1569 */     List excelImpTemplates = (List)classExcelImpTemplates.get(bmClassId);
/* 1570 */     if (excelImpTemplates == null) {
/* 1571 */       throw new UserException("");
/*      */     }
/*      */ 
/* 1574 */     int index = -1;
/* 1575 */     for (int i = 0; i < excelImpTemplates.size(); i++) {
/* 1576 */       ExcelImpTemplateMeta _excelTemplate = (ExcelImpTemplateMeta)excelImpTemplates.get(i);
/* 1577 */       if (_excelTemplate.getTemplateCuid().equals(template.getTemplateCuid())) {
/* 1578 */         index = i;
/* 1579 */         break;
/*      */       }
/*      */     }
/* 1582 */     if (index >= 0)
/*      */     {
/* 1584 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1587 */       String drmAttrGroupCuid = template.getExtAttrMetaGroup().getGroupCuid();
/* 1588 */       DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/* 1589 */       drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/* 1590 */       drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", drmAttrGroupCuid));
/* 1591 */       DrmAttrGroup dag = new DrmAttrGroup();
/* 1592 */       IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmAttrGroupQuery, dag);
/* 1593 */       List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/* 1594 */       List iAttrGroupDos = new ArrayList();
/* 1595 */       for (IDrmQueryRow row : drmAttrGroupRows) {
/* 1596 */         IDrmDataObject dbobj = row.getResultDbo("DRM_ATTR_GROUP");
/* 1597 */         iAttrGroupDos.add(dbobj);
/*      */       }
/* 1599 */       if ((iAttrGroupDos != null) && (iAttrGroupDos.size() > 0)) {
/* 1600 */         getDynResManageService().deleteDynObjects(actionContext, iAttrGroupDos, false);
/*      */       }
/*      */ 
/* 1604 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/* 1605 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1606 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/* 1607 */       DrmGroupAttr dga = new DrmGroupAttr();
/* 1608 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/* 1609 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/* 1610 */       List iGroupAttrDos = new ArrayList();
/* 1611 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/* 1612 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/* 1613 */         iGroupAttrDos.add(dbobj);
/*      */       }
/* 1615 */       if ((iGroupAttrDos != null) && (iGroupAttrDos.size() > 0)) {
/* 1616 */         getDynResManageService().deleteDynObjects(actionContext, iGroupAttrDos, false);
/*      */       }
/*      */ 
/* 1620 */       String templateCuid = template.getTemplateCuid();
/* 1621 */       DrmSingleClassQuery drmTemplateQuery = new DrmSingleClassQuery();
/* 1622 */       drmTemplateQuery.setBmClassId("DRM_EXL_IMP_TEMPLATE");
/* 1623 */       drmTemplateQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", templateCuid));
/* 1624 */       DrmExlImpTemplate dd = new DrmExlImpTemplate();
/* 1625 */       IDrmQueryResultSet drmTemplateRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmTemplateQuery, dd);
/* 1626 */       List drmTemplateRows = drmTemplateRs.getResultSet();
/* 1627 */       List iTemplateDos = new ArrayList();
/* 1628 */       for (IDrmQueryRow row : drmTemplateRows) {
/* 1629 */         IDrmDataObject dbobj = row.getResultDbo("DRM_EXL_IMP_TEMPLATE");
/* 1630 */         iTemplateDos.add(dbobj);
/*      */       }
/* 1632 */       if ((iTemplateDos != null) && (iTemplateDos.size() > 0)) {
/* 1633 */         getDynResManageService().deleteDynObjects(actionContext, iTemplateDos, false);
/*      */       }
/*      */ 
/* 1637 */       DrmSingleClassQuery drmTemplateUniqueQuery = new DrmSingleClassQuery();
/* 1638 */       drmTemplateUniqueQuery.setBmClassId("DRM_EXL_CLASS_UNIQUE");
/* 1639 */       drmTemplateUniqueQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_EXCEL_TEMPLATE_CUID", "=", templateCuid));
/* 1640 */       DrmExlClassUnique ddd = new DrmExlClassUnique();
/* 1641 */       IDrmQueryResultSet drmTemplateUniqueRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmTemplateUniqueQuery, ddd);
/* 1642 */       if (drmTemplateUniqueRs != null) {
/* 1643 */         List drmTemplateUniqueRows = drmTemplateUniqueRs.getResultSet();
/* 1644 */         List iTemplateUniqueDos = new ArrayList();
/* 1645 */         for (IDrmQueryRow row : drmTemplateUniqueRows) {
/* 1646 */           IDrmDataObject dbobj = row.getResultDbo("DRM_EXL_CLASS_UNIQUE");
/* 1647 */           iTemplateUniqueDos.add(dbobj);
/*      */         }
/* 1649 */         if ((iTemplateUniqueDos != null) && (iTemplateUniqueDos.size() > 0)) {
/* 1650 */           getDynResManageService().deleteDynObjects(actionContext, iTemplateUniqueDos, false);
/*      */         }
/*      */       }
/*      */ 
/* 1654 */       excelImpTemplates.remove(index);
/*      */ 
/* 1656 */       this.attrGroups.remove(drmAttrGroupCuid);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteExcelExpTemplate(IServiceActionContext actionContext, String bmClassId, String tempCuid) throws UserException {
/* 1661 */     String userId = actionContext.getUserId();
/* 1662 */     Map classTemplates = (Map)this.userExcelExpTemplates.get(userId);
/*      */ 
/* 1664 */     if (classTemplates == null) {
/* 1665 */       throw new UserException("用户[" + userId + "]导出模板信息不存在！");
/*      */     }
/*      */ 
/* 1668 */     List templates = (List)classTemplates.get(bmClassId);
/* 1669 */     if (templates == null) {
/* 1670 */       throw new UserException("用户[" + userId + "]的[" + bmClassId + "]导出模板信息不存在！");
/*      */     }
/*      */ 
/* 1673 */     int index = -1;
/* 1674 */     for (int i = 0; i < templates.size(); i++) {
/* 1675 */       TemplateMeta _excelTemplate = (TemplateMeta)templates.get(i);
/* 1676 */       if (_excelTemplate.getTemplateCuid().equals(tempCuid)) {
/* 1677 */         index = i;
/* 1678 */         break;
/*      */       }
/*      */     }
/* 1681 */     if (index >= 0)
/*      */     {
/* 1683 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 1684 */       DrmExlExpTemplate dbo = new DrmExlExpTemplate();
/* 1685 */       dbo.setCuid(tempCuid);
/* 1686 */       dbo = (DrmExlExpTemplate)getDynResManageService().getDynObject(actionContext, queryContext, dbo);
/*      */ 
/* 1688 */       String drmAttrGroupCuid = dbo.getRelatedAttrGroupCuid();
/* 1689 */       DrmSingleClassQuery drmAttrGroupQuery = new DrmSingleClassQuery();
/* 1690 */       drmAttrGroupQuery.setBmClassId("DRM_ATTR_GROUP");
/* 1691 */       drmAttrGroupQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", drmAttrGroupCuid));
/* 1692 */       DrmAttrGroup dag = new DrmAttrGroup();
/* 1693 */       IDrmQueryResultSet drmAttrGroupRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmAttrGroupQuery, dag);
/* 1694 */       List drmAttrGroupRows = drmAttrGroupRs.getResultSet();
/* 1695 */       List iAttrGroupDos = new ArrayList();
/* 1696 */       for (IDrmQueryRow row : drmAttrGroupRows) {
/* 1697 */         IDrmDataObject dbobj = row.getResultDbo("DRM_ATTR_GROUP");
/* 1698 */         iAttrGroupDos.add(dbobj);
/*      */       }
/* 1700 */       if ((iAttrGroupDos != null) && (iAttrGroupDos.size() > 0)) {
/* 1701 */         getDynResManageService().deleteDynObjects(actionContext, iAttrGroupDos, false);
/*      */       }
/*      */ 
/* 1705 */       DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/* 1706 */       drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1707 */       drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", drmAttrGroupCuid));
/* 1708 */       DrmGroupAttr dga = new DrmGroupAttr();
/* 1709 */       IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmGroupAttrQuery, dga);
/* 1710 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/* 1711 */       List iGroupAttrDos = new ArrayList();
/* 1712 */       for (IDrmQueryRow row : drmGroupAttrRows) {
/* 1713 */         IDrmDataObject dbobj = row.getResultDbo("DRM_GROUP_ATTR");
/* 1714 */         iGroupAttrDos.add(dbobj);
/*      */       }
/* 1716 */       if ((iGroupAttrDos != null) && (iGroupAttrDos.size() > 0)) {
/* 1717 */         getDynResManageService().deleteDynObjects(actionContext, iGroupAttrDos, false);
/*      */       }
/*      */ 
/* 1721 */       String templateCuid = tempCuid;
/* 1722 */       DrmSingleClassQuery drmTemplateQuery = new DrmSingleClassQuery();
/* 1723 */       drmTemplateQuery.setBmClassId("DRM_EXL_EXP_TEMPLATE");
/* 1724 */       drmTemplateQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", templateCuid));
/* 1725 */       DrmExlExpTemplate dd = new DrmExlExpTemplate();
/* 1726 */       IDrmQueryResultSet drmTemplateRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmTemplateQuery, dd);
/* 1727 */       List drmTemplateRows = drmTemplateRs.getResultSet();
/* 1728 */       List iTemplateDos = new ArrayList();
/* 1729 */       for (IDrmQueryRow row : drmTemplateRows) {
/* 1730 */         IDrmDataObject dbobj = row.getResultDbo("DRM_EXL_EXP_TEMPLATE");
/* 1731 */         iTemplateDos.add(dbobj);
/*      */       }
/* 1733 */       if ((iTemplateDos != null) && (iTemplateDos.size() > 0)) {
/* 1734 */         getDynResManageService().deleteDynObjects(actionContext, iTemplateDos, false);
/*      */       }
/* 1736 */       templates.remove(index);
/*      */ 
/* 1738 */       this.attrGroups.remove(drmAttrGroupCuid);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int saveExcelImportData(IServiceActionContext actionContext, String doType, List<DrmUploadClassMeta> allData, String sFileUrl)
/*      */     throws UserException
/*      */   {
/* 1751 */     int successCount = 0; int addCount = 0; int updateCount = 0;
/* 1752 */     String sClassId = ""; String sClassName = "";
/* 1753 */     DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(0);
/* 1754 */     sClassId = uploadClassMeta.getBmClassId();
/* 1755 */     BMClassMeta bmClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/* 1756 */     sClassName = bmClassMeta.getLabelCn();
/*      */ 
/* 1758 */     for (int i = 0; i < allData.size(); i++) {
/* 1759 */       uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/* 1760 */       if (!uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR)) {
/* 1761 */         if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ADD)) {
/* 1762 */           if ((doType.equals("add")) || (doType.equals("all")))
/*      */             try {
/* 1764 */               IDrmDataObject data = DrmEntityFactory.getInstance().createDataObject();
/* 1765 */               data.setBmClassId(sClassId);
/* 1766 */               data.setAttrValues(uploadClassMeta.getExcelAttrValueMap());
/* 1767 */               data = bmClassMeta.convertDrmDataObject(data, true);
/* 1768 */               if (getDynResManageService().addDynObject(actionContext, data, false) == null) {
/* 1769 */                 uploadClassMeta.setImportResultInfo("失败");
/* 1770 */                 uploadClassMeta.setSaveErrorInfo("插入数据库记录出错");
/*      */               } else {
/* 1772 */                 successCount++;
/* 1773 */                 uploadClassMeta.setImportResultInfo("成功");
/* 1774 */                 addCount++;
/*      */               }
/*      */             } catch (UserException ex) {
/* 1777 */               uploadClassMeta.setImportResultInfo("失败");
/* 1778 */               uploadClassMeta.setSaveErrorInfo("增加数据库记录出错：" + ex.getMessage());
/*      */             } catch (Exception ex) {
/* 1780 */               uploadClassMeta.setImportResultInfo("失败");
/* 1781 */               uploadClassMeta.setSaveErrorInfo("增加数据库记录出错：" + ex.getMessage());
/*      */             }
/*      */           else {
/* 1784 */             uploadClassMeta.setImportResultInfo("未导入");
/*      */           }
/*      */         }
/* 1787 */         else if ((doType.equals("modify")) || (doType.equals("all"))) {
/*      */           try {
/* 1789 */             IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1791 */             IDrmDataObject oldData = DrmEntityFactory.getInstance().createDataObject();
/* 1792 */             oldData.setBmClassId(sClassId);
/* 1793 */             String sCuid = uploadClassMeta.getCuid();
/* 1794 */             oldData.setCuid(sCuid);
/* 1795 */             oldData.setAttrValues(uploadClassMeta.getExcelAttrValueMap());
/* 1796 */             oldData = bmClassMeta.convertDrmDataObject(oldData, false);
/*      */ 
/* 1798 */             IDrmDataObject data = getDynResManageService().getDynObject(actionContext, queryContext, oldData);
/* 1799 */             oldData.setDboId(data.getDboId());
/*      */ 
/* 1801 */             getDynResManageService().modifyDynObject(actionContext, oldData, false);
/* 1802 */             successCount++;
/* 1803 */             uploadClassMeta.setImportResultInfo("成功");
/* 1804 */             updateCount++;
/*      */           } catch (UserException ex) {
/* 1806 */             uploadClassMeta.setImportResultInfo("失败");
/* 1807 */             uploadClassMeta.setSaveErrorInfo("修改数据库记录出错：" + ex.getMessage());
/*      */           } catch (Exception ex) {
/* 1809 */             uploadClassMeta.setImportResultInfo("失败");
/* 1810 */             uploadClassMeta.setSaveErrorInfo("修改数据库记录出错：" + ex.getMessage());
/*      */           }
/*      */         }
/*      */         else {
/* 1814 */           uploadClassMeta.setImportResultInfo("未导入");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1821 */     if (getDynResManageService().getIsLog(ServiceHelper.createSvActCxt())) {
/* 1822 */       Map logValues = new HashMap();
/* 1823 */       String sName = actionContext.getUserName();
/* 1824 */       if ((sName == null) || (sName.equals("")))
/* 1825 */         sName = "未知";
/* 1826 */       logValues.put("USER_NAME", sName);
/* 1827 */       logValues.put("CREATE_DATE", new Timestamp(System.currentTimeMillis()));
/* 1828 */       logValues.put("CLS_ID", sClassId);
/* 1829 */       logValues.put("CLS_NAME", sClassName);
/* 1830 */       logValues.put("ADD_NUM", Integer.valueOf(addCount));
/* 1831 */       logValues.put("MODIFY_NUM", Integer.valueOf(updateCount));
/* 1832 */       logValues.put("FILE_PATH", sFileUrl);
/*      */ 
/* 1834 */       IDrmDataObject logData = DrmEntityFactory.getInstance().createDataObject();
/* 1835 */       logData.setBmClassId("DATA_IMPORT_LOG");
/* 1836 */       logData.setAttrValues(logValues);
/*      */       try
/*      */       {
/* 1840 */         IServiceActionContext ctx = new ServiceActionContext();
/* 1841 */         getDynResManageService().addDynObject(ctx, logData, false);
/*      */       } catch (UserException ex) {
/* 1843 */         LogHome.getLog().info("保存EXCEL导入日志出错，原因：" + ex.getMessage());
/*      */       }
/*      */     }
/* 1846 */     return successCount;
/*      */   }
/*      */ 
/*      */   public void synAttrGroup(IServiceActionContext actionContext, List<Drm> deleteobjs, List<String> removeClassIds)
/*      */   {
/*      */     try
/*      */     {
/* 1856 */       if (removeClassIds != null) {
/* 1857 */         for (String removeBmClassId : removeClassIds) {
/* 1858 */           if (removeBmClassId.indexOf("\\.") < 0)
/*      */           {
/* 1860 */             Iterator iterator = this.attrGroups.keySet().iterator();
/* 1861 */             while (iterator.hasNext()) {
/* 1862 */               String sGroupCuid = (String)iterator.next();
/* 1863 */               ExtAttrMetaGroup attrGroup = (ExtAttrMetaGroup)this.attrGroups.get(sGroupCuid);
/* 1864 */               if (attrGroup.getBmClassId().equals(removeBmClassId)) {
/* 1865 */                 this.attrGroups.remove(sGroupCuid);
/*      */               }
/*      */             }
/* 1868 */             this.classGroupAttrs.remove(removeBmClassId);
/*      */           }
/*      */           else {
/* 1871 */             String[] _removeClsAttrId = removeBmClassId.split("\\.");
/* 1872 */             String bmClassId = _removeClsAttrId[0];
/* 1873 */             String attrId = _removeClsAttrId[1];
/*      */ 
/* 1875 */             Iterator iterator = this.attrGroups.keySet().iterator();
/* 1876 */             while (iterator.hasNext()) {
/* 1877 */               String sGroupCuid = (String)iterator.next();
/* 1878 */               ExtAttrMetaGroup attrGroup = (ExtAttrMetaGroup)this.attrGroups.get(sGroupCuid);
/* 1879 */               if (attrGroup.getBmClassId().equals(bmClassId)) {
/* 1880 */                 for (int i = 0; i < attrGroup.getExtAttrMetas().size(); i++) {
/* 1881 */                   if (((ExtAttrMeta)attrGroup.getExtAttrMetas().get(i)).getAttrId().equals(attrId)) {
/* 1882 */                     attrGroup.getExtAttrMetas().remove(i);
/* 1883 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 1889 */             Map groups = (Map)this.classGroupAttrs.get(bmClassId);
/* 1890 */             if (groups != null) {
/* 1891 */               Collection bufs = groups.values();
/* 1892 */               Iterator iter = bufs.iterator();
/* 1893 */               while (iter.hasNext()) {
/* 1894 */                 ClassAttrGroupMeta attrGroupMeta = (ClassAttrGroupMeta)iter.next();
/* 1895 */                 List attrList = attrGroupMeta.getExtAttrMetas();
/* 1896 */                 for (int i = 0; i < attrList.size(); i++) {
/* 1897 */                   if (((ExtAttrMeta)attrList.get(i)).getAttrId().equals(attrId)) {
/* 1898 */                     attrList.remove(i);
/* 1899 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1908 */       if (deleteobjs.size() > 0)
/* 1909 */         getDynResManageService().deleteDynObjects(ServiceHelper.createSvActCxt(), deleteobjs, false);
/*      */     }
/*      */     catch (Exception ex) {
/* 1912 */       LogHome.getLog().error("", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<ClassAttrGroupMeta> getClassAttrGroup(IServiceActionContext actionContext)
/*      */     throws UserException
/*      */   {
/* 1920 */     return this.classAttrGroups;
/*      */   }
/*      */ 
/*      */   public ClassAttrGroupMeta addClassAttrGroup(IServiceActionContext actionContext, String labelCn) throws UserException {
/* 1924 */     ClassAttrGroupMeta groupMeta = new ClassAttrGroupMeta();
/*      */ 
/* 1926 */     long iSortNo = 0L;
/* 1927 */     if (this.classAttrGroups.size() > 0) {
/* 1928 */       iSortNo = ((ClassAttrGroupMeta)this.classAttrGroups.get(this.classAttrGroups.size() - 1)).getSortNo();
/*      */     }
/* 1930 */     iSortNo += 1L;
/*      */ 
/* 1932 */     DrmClassAttrGroup attrGroup = new DrmClassAttrGroup();
/* 1933 */     attrGroup.setBmClassId("DRM_CLASS_ATTR_GROUP");
/* 1934 */     attrGroup.setLabelCn(labelCn);
/* 1935 */     attrGroup.setSortNo(iSortNo);
/* 1936 */     getDynResManageService().addDynObject(actionContext, attrGroup, false);
/*      */ 
/* 1938 */     groupMeta.setLabelCn(labelCn);
/* 1939 */     groupMeta.setSortNo(iSortNo);
/* 1940 */     groupMeta.setGroupCuid(attrGroup.getCuid());
/*      */ 
/* 1942 */     this.classAttrGroups.add(groupMeta);
/*      */ 
/* 1944 */     return groupMeta;
/*      */   }
/*      */ 
/*      */   public void modifyClassAttrGroup(IServiceActionContext actionContext, String groupCuid, String labelCn) throws UserException {
/* 1948 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 1949 */     DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 1950 */     drmQuery.setBmClassId("DRM_CLASS_ATTR_GROUP");
/* 1951 */     drmQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", groupCuid));
/* 1952 */     DrmClassAttrGroup dd = new DrmClassAttrGroup();
/* 1953 */     IDrmQueryResultSet drmRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmQuery, dd);
/* 1954 */     List drmGroupRows = drmRs.getResultSet();
/* 1955 */     if (drmGroupRows.size() > 0) {
/* 1956 */       IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupRows.get(0)).getResultDbo("DRM_CLASS_ATTR_GROUP");
/* 1957 */       dbobj.setAttrValue("LABEL_CN", labelCn);
/* 1958 */       getDynResManageService().modifyDynObject(actionContext, dbobj, false);
/*      */     }
/* 1960 */     for (int i = 0; i < this.classAttrGroups.size(); i++)
/* 1961 */       if (((ClassAttrGroupMeta)this.classAttrGroups.get(i)).getGroupCuid().equals(groupCuid)) {
/* 1962 */         ((ClassAttrGroupMeta)this.classAttrGroups.get(i)).setLabelCn(labelCn);
/* 1963 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void modifyClassAttrGroupSort(IServiceActionContext actionContext, List<String> groupCuidList) throws UserException
/*      */   {
/* 1969 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1971 */     this.classAttrGroups.clear();
/*      */ 
/* 1973 */     for (int i = 0; i < groupCuidList.size(); i++) {
/* 1974 */       DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 1975 */       drmQuery.setBmClassId("DRM_CLASS_ATTR_GROUP");
/* 1976 */       drmQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", (String)groupCuidList.get(i)));
/* 1977 */       DrmClassAttrGroup dd = new DrmClassAttrGroup();
/* 1978 */       IDrmQueryResultSet drmRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmQuery, dd);
/* 1979 */       List drmGroupRows = drmRs.getResultSet();
/* 1980 */       if (drmGroupRows.size() > 0) {
/* 1981 */         IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupRows.get(0)).getResultDbo("DRM_CLASS_ATTR_GROUP");
/* 1982 */         dbobj.setAttrValue("SORT_NO", Integer.valueOf(i));
/* 1983 */         getDynResManageService().modifyDynObject(actionContext, dbobj, false);
/*      */ 
/* 1986 */         ClassAttrGroupMeta attrGroup = new ClassAttrGroupMeta();
/* 1987 */         attrGroup.setGroupCuid((String)dbobj.getAttrValue("CUID"));
/* 1988 */         attrGroup.setLabelCn((String)dbobj.getAttrValue("LABEL_CN"));
/* 1989 */         attrGroup.setSortNo(Long.valueOf(i).longValue());
/* 1990 */         this.classAttrGroups.add(attrGroup);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String delClassAttrGroup(IServiceActionContext actionContext, String cuid) throws UserException {
/* 1996 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 1997 */     DrmSingleClassQuery drmGroupAttrQuery = new DrmSingleClassQuery();
/* 1998 */     drmGroupAttrQuery.setBmClassId("DRM_GROUP_ATTR");
/* 1999 */     drmGroupAttrQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", cuid));
/* 2000 */     DrmGroupAttr dga = new DrmGroupAttr();
/* 2001 */     IDrmQueryResultSet drmGroupAttrRs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmGroupAttrQuery, dga);
/* 2002 */     if (drmGroupAttrRs != null) {
/* 2003 */       List drmGroupAttrRows = drmGroupAttrRs.getResultSet();
/* 2004 */       List idos = new ArrayList();
/* 2005 */       if (drmGroupAttrRows != null) {
/* 2006 */         for (int i = 0; i < drmGroupAttrRows.size(); i++) {
/* 2007 */           IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupAttrRows.get(i)).getResultDbo("DRM_GROUP_ATTR");
/* 2008 */           dbobj.setAttrValue("RELATED_ATTR_GROUP_CUID", "otherinfo");
/* 2009 */           idos.add(dbobj);
/*      */         }
/* 2011 */         if (idos.size() > 0) {
/* 2012 */           Map modifyAttrs = new HashMap();
/* 2013 */           modifyAttrs.put("RELATED_ATTR_GROUP_CUID", "otherinfo");
/* 2014 */           getDynResManageService().modifyDynObjects(actionContext, idos, modifyAttrs, false);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2019 */     DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 2020 */     drmQuery.setBmClassId("DRM_CLASS_ATTR_GROUP");
/* 2021 */     drmQuery.addQueryCondExps(new DrmQueryAttrCond("CUID", "=", cuid));
/* 2022 */     DrmClassAttrGroup dd = new DrmClassAttrGroup();
/* 2023 */     IDrmQueryResultSet drmRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmQuery, dd);
/* 2024 */     List drmGroupRows = drmRs.getResultSet();
/* 2025 */     if (drmGroupRows.size() > 0) {
/* 2026 */       IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupRows.get(0)).getResultDbo("DRM_CLASS_ATTR_GROUP");
/* 2027 */       getDynResManageService().deleteDynObject(actionContext, dbobj, false);
/*      */     }
/* 2029 */     for (int i = 0; i < this.classAttrGroups.size(); i++) {
/* 2030 */       if (((ClassAttrGroupMeta)this.classAttrGroups.get(i)).getGroupCuid().equals(cuid)) {
/* 2031 */         this.classAttrGroups.remove(i);
/* 2032 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 2036 */     List delExtAttrMetas = null;
/* 2037 */     List otherExtAttrMetas = null;
/* 2038 */     Collection bufs = this.classGroupAttrs.values();
/* 2039 */     Iterator iter = bufs.iterator();
/* 2040 */     while (iter.hasNext()) {
/* 2041 */       Map bInfo = (Map)iter.next();
/* 2042 */       ClassAttrGroupMeta attrInfo = (ClassAttrGroupMeta)bInfo.get(cuid);
/* 2043 */       if (attrInfo != null) {
/* 2044 */         delExtAttrMetas = attrInfo.getExtAttrMetas();
/* 2045 */         bInfo.remove(cuid);
/*      */       }
/* 2047 */       ClassAttrGroupMeta otherInfo = (ClassAttrGroupMeta)bInfo.get("otherinfo");
/* 2048 */       if (otherInfo != null) {
/* 2049 */         otherExtAttrMetas = otherInfo.getExtAttrMetas();
/*      */       }
/*      */ 
/* 2052 */       if ((otherExtAttrMetas != null) && (delExtAttrMetas != null)) {
/* 2053 */         otherExtAttrMetas.addAll(delExtAttrMetas);
/*      */       }
/*      */     }
/*      */ 
/* 2057 */     return "";
/*      */   }
/*      */ 
/*      */   public ClassAttrGroupMeta getClassGroupAttr(IServiceActionContext actionContext, String bmClassId, String groupCuid) throws UserException {
/* 2061 */     ClassAttrGroupMeta groupMeta = null;
/*      */ 
/* 2063 */     Map groupList = (Map)this.classGroupAttrs.get(bmClassId);
/* 2064 */     if (groupList != null)
/*      */     {
/* 2066 */       if (groupCuid.equals("otherinfo")) {
/* 2067 */         groupMeta = new ClassAttrGroupMeta();
/*      */ 
/* 2070 */         Map attrMap = new HashMap();
/* 2071 */         Iterator iterator = groupList.keySet().iterator();
/* 2072 */         while (iterator.hasNext()) {
/* 2073 */           String sGroupId = (String)iterator.next();
/* 2074 */           if (sGroupId.equals("otherinfo")) {
/*      */             continue;
/*      */           }
/* 2077 */           ClassAttrGroupMeta attrGroupMeta = (ClassAttrGroupMeta)groupList.get(sGroupId);
/* 2078 */           List attrs = attrGroupMeta.getGroupAttrIds();
/* 2079 */           for (int i = 0; i < attrs.size(); i++) {
/* 2080 */             attrMap.put(attrs.get(i), sGroupId);
/*      */           }
/*      */         }
/*      */ 
/* 2084 */         BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 2085 */         Map attrList = classMeta.getAllAttrMetas();
/* 2086 */         Collection bufs = attrList.values();
/* 2087 */         Iterator iter = bufs.iterator();
/* 2088 */         while (iter.hasNext()) {
/* 2089 */           BMAttrMeta attrMeta = (BMAttrMeta)iter.next();
/* 2090 */           if (!attrMeta.getIsSystem().booleanValue()) {
/* 2091 */             String attrId = attrMeta.getAttrId();
/* 2092 */             if (attrMap.get(attrId) == null)
/*      */             {
/* 2095 */               groupMeta.setGroupCuid(groupCuid);
/* 2096 */               ExtAttrMeta extAttrMeta = new ExtAttrMeta();
/* 2097 */               extAttrMeta.setBmClassId(bmClassId);
/* 2098 */               extAttrMeta.setAttrId(attrId);
/* 2099 */               groupMeta.addExtAttrMeta(extAttrMeta);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2105 */         if (groupList == null) {
/* 2106 */           groupList = new HashMap();
/*      */         }
/* 2108 */         groupList.put(groupCuid, groupMeta);
/* 2109 */         this.classGroupAttrs.put(bmClassId, groupList);
/*      */       } else {
/* 2111 */         groupMeta = (ClassAttrGroupMeta)groupList.get(groupCuid);
/*      */       }
/*      */     } else {
/* 2114 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 2115 */       if ((groupMeta == null) || (groupMeta.getExtAttrMetas().size() == 0)) {
/* 2116 */         if (groupCuid.equals("baseinfo"))
/*      */         {
/* 2118 */           groupMeta = new ClassAttrGroupMeta();
/*      */ 
/* 2120 */           Map attrList = classMeta.getAllAttrMetas();
/* 2121 */           Collection bufs = attrList.values();
/* 2122 */           Iterator iter = bufs.iterator();
/* 2123 */           while (iter.hasNext()) {
/* 2124 */             BMAttrMeta attrMeta = (BMAttrMeta)iter.next();
/* 2125 */             if (!attrMeta.getIsSystem().booleanValue()) {
/* 2126 */               if (attrMeta.getIsNotNull().booleanValue()) {
/* 2127 */                 ExtAttrMeta extAttrMeta = new ExtAttrMeta();
/* 2128 */                 extAttrMeta.setBmClassId(bmClassId);
/* 2129 */                 extAttrMeta.setAttrId(attrMeta.getAttrId());
/* 2130 */                 groupMeta.addExtAttrMeta(extAttrMeta);
/*      */               } else {
/* 2132 */                 List labelAttrList = classMeta.getConstructLabelAttrIds();
/* 2133 */                 if (labelAttrList.contains(attrMeta.getAttrId())) {
/* 2134 */                   ExtAttrMeta extAttrMeta = new ExtAttrMeta();
/* 2135 */                   extAttrMeta.setBmClassId(bmClassId);
/* 2136 */                   extAttrMeta.setAttrId(attrMeta.getAttrId());
/* 2137 */                   groupMeta.addExtAttrMeta(extAttrMeta);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2144 */           if (groupList == null) {
/* 2145 */             groupList = new HashMap();
/*      */           }
/* 2147 */           groupList.put(groupCuid, groupMeta);
/* 2148 */           this.classGroupAttrs.put(bmClassId, groupList);
/* 2149 */         } else if (groupCuid.equals("otherinfo"))
/*      */         {
/* 2151 */           groupMeta = new ClassAttrGroupMeta();
/* 2152 */           Map selAttrList = new HashMap();
/*      */ 
/* 2154 */           Map groupMap = getClassGroupAttrMap(actionContext, bmClassId);
/* 2155 */           if (groupMap != null) {
/* 2156 */             Collection bufs = groupMap.values();
/* 2157 */             Iterator iter = bufs.iterator();
/* 2158 */             while (iter.hasNext()) {
/* 2159 */               ClassAttrGroupMeta mat = (ClassAttrGroupMeta)iter.next();
/* 2160 */               List groupAttrList = mat.getGroupAttrIds();
/* 2161 */               for (int i = 0; i < groupAttrList.size(); i++) {
/* 2162 */                 String attrId = (String)groupAttrList.get(i);
/* 2163 */                 selAttrList.put(attrId, attrId);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2168 */           Map attrList = classMeta.getAllAttrMetas();
/* 2169 */           Collection bufs = attrList.values();
/* 2170 */           Iterator iter = bufs.iterator();
/* 2171 */           while (iter.hasNext()) {
/* 2172 */             BMAttrMeta attrMeta = (BMAttrMeta)iter.next();
/* 2173 */             if ((!attrMeta.getIsSystem().booleanValue()) && 
/* 2174 */               (!attrMeta.getIsNotNull().booleanValue())) {
/* 2175 */               List labelAttrList = classMeta.getConstructLabelAttrIds();
/* 2176 */               if ((!labelAttrList.contains(attrMeta.getAttrId())) && 
/* 2177 */                 (!selAttrList.containsKey(attrMeta.getAttrId())))
/*      */               {
/* 2179 */                 ExtAttrMeta extAttrMeta = new ExtAttrMeta();
/* 2180 */                 extAttrMeta.setBmClassId(bmClassId);
/* 2181 */                 extAttrMeta.setAttrId(attrMeta.getAttrId());
/* 2182 */                 groupMeta.addExtAttrMeta(extAttrMeta);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2190 */           if (groupList == null) {
/* 2191 */             groupList = new HashMap();
/*      */           }
/* 2193 */           groupList.put(groupCuid, groupMeta);
/* 2194 */           this.classGroupAttrs.put(bmClassId, groupList);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2199 */     return groupMeta;
/*      */   }
/*      */ 
/*      */   public Map<String, ClassAttrGroupMeta> getClassGroupAttrMap(IServiceActionContext actionContext, String bmClassId) throws UserException {
/* 2203 */     Map groupMap = (Map)this.classGroupAttrs.get(bmClassId);
/*      */ 
/* 2205 */     return groupMap;
/*      */   }
/*      */ 
/*      */   public void saveClassGroupAttr(IServiceActionContext actionContext, String bmClassId, ClassAttrGroupMeta groupMeta) throws UserException {
/* 2209 */     String groupCuid = groupMeta.getGroupCuid();
/* 2210 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 2212 */     DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 2213 */     drmQuery.setBmClassId("DRM_GROUP_ATTR");
/* 2214 */     drmQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", groupCuid));
/* 2215 */     DrmGroupAttr dd = new DrmGroupAttr();
/* 2216 */     IDrmQueryResultSet drmRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmQuery, dd);
/* 2217 */     List drmGroupAttrRows = drmRs.getResultSet();
/* 2218 */     List idos = new ArrayList();
/* 2219 */     if (drmGroupAttrRows != null) {
/* 2220 */       for (int i = 0; i < drmGroupAttrRows.size(); i++) {
/* 2221 */         IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupAttrRows.get(i)).getResultDbo("DRM_GROUP_ATTR");
/* 2222 */         idos.add(dbobj);
/*      */       }
/* 2224 */       if (idos.size() > 0) {
/* 2225 */         getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */       }
/*      */     }
/*      */ 
/* 2229 */     List drmGroupAttrList = new ArrayList();
/* 2230 */     int i = 0;
/* 2231 */     for (ExtAttrMeta attrMeta : groupMeta.getExtAttrMetas()) {
/* 2232 */       DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 2233 */       drmGroupAttr.setAttrId(attrMeta.getAttrId());
/* 2234 */       drmGroupAttr.setBmClassid(bmClassId);
/* 2235 */       drmGroupAttr.setRelatedAttrGroupCuid(groupCuid);
/* 2236 */       drmGroupAttr.setSortNo(i);
/*      */ 
/* 2238 */       drmGroupAttrList.add(drmGroupAttr);
/* 2239 */       i++;
/*      */     }
/*      */ 
/* 2242 */     getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */ 
/* 2244 */     Map groupList = (Map)this.classGroupAttrs.get(bmClassId);
/* 2245 */     if (groupList == null) {
/* 2246 */       groupList = new HashMap();
/* 2247 */       this.classGroupAttrs.put(bmClassId, groupList);
/*      */     }
/* 2249 */     groupList.put(groupCuid, groupMeta);
/*      */   }
/*      */ 
/*      */   public void saveClassGroupAttrMap(IServiceActionContext actionContext, String bmClassId, List<ClassAttrGroupMeta> groupMap) throws UserException {
/* 2253 */     for (int k = 0; k < groupMap.size(); k++) {
/* 2254 */       ClassAttrGroupMeta groupMeta = (ClassAttrGroupMeta)groupMap.get(k);
/* 2255 */       String groupCuid = groupMeta.getGroupCuid();
/*      */ 
/* 2257 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 2259 */       DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 2260 */       drmQuery.setBmClassId("DRM_GROUP_ATTR");
/* 2261 */       drmQuery.addQueryCondExps(new DrmQueryAttrCond("RELATED_ATTR_GROUP_CUID", "=", groupCuid));
/* 2262 */       drmQuery.addQueryCondExps(new DrmQueryAttrCond("BM_CLASSID", "=", bmClassId));
/* 2263 */       DrmGroupAttr dd = new DrmGroupAttr();
/* 2264 */       IDrmQueryResultSet drmRs = getDynResManageService().getDynObjBySql(actionContext, queryContext, drmQuery, dd);
/* 2265 */       List drmGroupAttrRows = drmRs.getResultSet();
/* 2266 */       List idos = new ArrayList();
/* 2267 */       if (drmGroupAttrRows != null) {
/* 2268 */         for (int i = 0; i < drmGroupAttrRows.size(); i++) {
/* 2269 */           IDrmDataObject dbobj = ((IDrmQueryRow)drmGroupAttrRows.get(i)).getResultDbo("DRM_GROUP_ATTR");
/* 2270 */           idos.add(dbobj);
/*      */         }
/* 2272 */         if (idos.size() > 0) {
/* 2273 */           getDynResManageService().deleteDynObjects(actionContext, idos, false);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2278 */       if (groupCuid.equals("otherinfo"))
/*      */       {
/*      */         continue;
/*      */       }
/* 2282 */       List drmGroupAttrList = new ArrayList();
/* 2283 */       int i = 0;
/* 2284 */       for (ExtAttrMeta attrMeta : groupMeta.getExtAttrMetas()) {
/* 2285 */         DrmGroupAttr drmGroupAttr = new DrmGroupAttr();
/* 2286 */         drmGroupAttr.setAttrId(attrMeta.getAttrId());
/* 2287 */         drmGroupAttr.setBmClassid(bmClassId);
/* 2288 */         drmGroupAttr.setRelatedAttrGroupCuid(groupCuid);
/* 2289 */         drmGroupAttr.setSortNo(i);
/*      */ 
/* 2291 */         drmGroupAttrList.add(drmGroupAttr);
/* 2292 */         i++;
/*      */       }
/* 2294 */       if (drmGroupAttrList.size() > 0) {
/* 2295 */         getDynResManageService().addDynObjects(actionContext, drmGroupAttrList, false);
/*      */       }
/* 2297 */       Map groupList = (Map)this.classGroupAttrs.get(bmClassId);
/* 2298 */       if (groupList == null) {
/* 2299 */         groupList = new HashMap();
/* 2300 */         this.classGroupAttrs.put(bmClassId, groupList);
/*      */       }
/* 2302 */       groupList.put(groupCuid, groupMeta);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Map validateExcelFile(IServiceActionContext actionContext, byte[] bytes, String sClassId, String doType) throws UserException
/*      */   {
/* 2308 */     List allData = new ArrayList();
/* 2309 */     Map returnMap = new HashMap();
/* 2310 */     List errorlist = new ArrayList();
/* 2311 */     returnMap.put("errorlist", errorlist);
/* 2312 */     returnMap.put("alldata", allData);
/* 2313 */     Workbook rwb = null;
/*      */     try {
/* 2315 */       BufInputStream in = new BufInputStream(bytes);
/* 2316 */       rwb = Workbook.getWorkbook(in);
/* 2317 */       if (rwb == null) {
/* 2318 */         errorlist.add("数据文件格式错误！");
/* 2319 */         return returnMap;
/*      */       }
/*      */     } catch (Exception e) {
/* 2322 */       LogHome.getLog().error("", e);
/* 2323 */       errorlist.add("发生异常：" + e.getMessage());
/* 2324 */       return returnMap;
/*      */     }
/*      */     try
/*      */     {
/* 2328 */       errorlist = ExcelHelper.validateExcelFile(actionContext, rwb, allData, sClassId);
/* 2329 */       if (errorlist.size() > 0)
/* 2330 */         returnMap.put("errorlist", errorlist);
/*      */     } catch (Exception ex) {
/* 2332 */       LogHome.getLog().error("", ex);
/* 2333 */       errorlist.add("发生异常：" + ex.getMessage());
/* 2334 */       return returnMap;
/*      */     }
/*      */ 
/* 2337 */     return returnMap;
/*      */   }
/*      */ 
/*      */   public Map importExcelData(IServiceActionContext actionContext, String doType, List<DrmUploadClassMeta> allData, String sFileUrl) throws UserException {
/* 2341 */     Map returnlist = new HashMap();
/* 2342 */     String sReturn = "";
/* 2343 */     int iSuccessCount = 0;
/* 2344 */     int total = allData.size();
/*      */ 
/* 2346 */     List readOnlyAttr = new ArrayList();
/* 2347 */     BMClassMeta bmClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), actionContext.getBmClassId());
/* 2348 */     Map allMetaMap = bmClassMeta.getAllAttrMetas();
/* 2349 */     if ((allMetaMap != null) && (allMetaMap.size() > 0)) {
/* 2350 */       Iterator it = allMetaMap.keySet().iterator();
/* 2351 */       while (it.hasNext()) {
/* 2352 */         String key = (String)it.next();
/* 2353 */         BMAttrMeta attMeta = (BMAttrMeta)allMetaMap.get(key);
/* 2354 */         if ((attMeta != null) && (attMeta.getSourceType().intValue() == 1)) {
/* 2355 */           readOnlyAttr.add(key);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2361 */     for (int i = 0; i < allData.size(); i++) {
/* 2362 */       Map importData = new HashMap();
/* 2363 */       DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/* 2364 */       if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_MODIFY)) {
/* 2365 */         Map excelAttrValueMap = uploadClassMeta.getExcelAttrValueMap();
/* 2366 */         if (excelAttrValueMap != null) {
/* 2367 */           Iterator it1 = excelAttrValueMap.keySet().iterator();
/* 2368 */           while (it1.hasNext()) {
/* 2369 */             String key = (String)it1.next();
/* 2370 */             Object value = excelAttrValueMap.get(key);
/* 2371 */             if (!readOnlyAttr.contains(key)) {
/* 2372 */               importData.put(key, value);
/*      */             }
/*      */           }
/* 2375 */           uploadClassMeta.setExcelAttrValueMap(importData);
/*      */         }
/*      */       }
/*      */     }
/* 2379 */     iSuccessCount = saveExcelImportData(actionContext, doType, allData, sFileUrl);
/*      */ 
/* 2381 */     if (iSuccessCount == 0) {
/* 2382 */       sReturn = "全部不成功";
/*      */     }
/* 2384 */     else if (iSuccessCount == total)
/* 2385 */       sReturn = "全部成功（总共有" + String.valueOf(allData.size()) + "条记录，" + "成功导入" + String.valueOf(iSuccessCount) + "条记录）";
/*      */     else {
/* 2387 */       sReturn = "部分成功（总共有" + String.valueOf(allData.size()) + "条记录，" + "成功导入" + String.valueOf(iSuccessCount) + "条记录）";
/*      */     }
/*      */ 
/* 2391 */     returnlist.put("resultinfo", sReturn);
/* 2392 */     returnlist.put("alldata", allData);
/*      */ 
/* 2394 */     return returnlist;
/*      */   }
/*      */ 
/*      */   public String createResultFile(IServiceActionContext actionContext, String fileName, String contextPath, String contextUrl, String doType, byte[] bytes, List<DrmUploadClassMeta> allData, List errorlist) throws UserException
/*      */   {
/* 2399 */     Workbook rwb = null;
/*      */     try {
/* 2401 */       BufInputStream in = new BufInputStream(bytes);
/* 2402 */       rwb = Workbook.getWorkbook(in);
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/* 2406 */     return ExcelHelper.createResultFile(fileName, contextPath, contextUrl, doType, rwb, allData, errorlist);
/*      */   }
/*      */ 
/*      */   public String[] addTemplateHeaderMatrixClass(IServiceActionContext actionContext, String exportPath, String contextPath, String bmClassId, ExcelImpTemplateMeta excelImpTemplate) throws IOException, WriteException, Exception
/*      */   {
/* 2411 */     String[] returnFile = new String[2];
/* 2412 */     actionContext.setBmClassId(bmClassId);
/* 2413 */     BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, bmClassId);
/*      */ 
/* 2415 */     ExcelHelper.createDir(exportPath);
/* 2416 */     String fileName = bmClassId + "_" + TimeFormatHelper.getFormatDate(new Date(), "yyyyMMddHHmmss") + ".xls";
/* 2417 */     String _fileName = fileName;
/* 2418 */     String filePath = exportPath + File.separator + fileName;
/* 2419 */     String fileUrl = contextPath + "//import//" + _fileName;
/*      */ 
/* 2421 */     WritableWorkbook workbook = ExcelHelper.creatWritableWorkBook(filePath);
/*      */ 
/* 2423 */     ExcelHelper.addTemplateHeaderMatrixClass(actionContext, workbook, bmClassMeta, excelImpTemplate);
/*      */ 
/* 2425 */     ExcelHelper.closeWritableWorkbook(workbook);
/*      */ 
/* 2427 */     returnFile[0] = fileName;
/* 2428 */     returnFile[1] = fileUrl;
/*      */ 
/* 2430 */     return returnFile;
/*      */   }
/*      */ 
/*      */   public boolean isHideResNav(IServiceActionContext actionContext) throws UserException {
/* 2434 */     String userId = actionContext.getUserId();
/* 2435 */     IDrmDataObject ddo = (IDrmDataObject)this.userIsHideResNavs.get(userId);
/*      */ 
/* 2437 */     return (ddo != null) && (ddo.getAttrValue("REMARK") != null) && (ddo.getAttrValue("REMARK").equals("1"));
/*      */   }
/*      */ 
/*      */   public void setResNavVisible(IServiceActionContext actionContext, String visible)
/*      */     throws UserException
/*      */   {
/* 2443 */     String userId = actionContext.getUserId();
/* 2444 */     IDrmDataObject dro = (IDrmDataObject)this.userIsHideResNavs.get(userId);
/*      */     try {
/* 2446 */       if (dro != null) {
/* 2447 */         dro.setAttrValue("REMARK", visible);
/* 2448 */         getDynResManageService().modifyDynObject(actionContext, dro, false);
/*      */       } else {
/* 2450 */         dro = DrmEntityFactory.getInstance().createDataObject();
/* 2451 */         dro.setBmClassId("DRM_ATTR_GROUP");
/* 2452 */         dro.setAttrValue("BM_CLASSID", "DRM_ATTR_GROUP");
/* 2453 */         dro.setAttrValue("GROUP_TYPE", Integer.valueOf(7));
/* 2454 */         dro.setAttrValue("RELATED_USER_CUID", userId);
/* 2455 */         dro.setAttrValue("REMARK", visible);
/* 2456 */         dro = getDynResManageService().addDynObject(actionContext, dro, false);
/* 2457 */         this.userIsHideResNavs.put(userId, dro);
/*      */       }
/*      */     } catch (Exception ex) {
/* 2460 */       LogHome.getLog().error("", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setServiceDAO(IVMModelServiceDAO dao) {
/* 2465 */     this.dao = dao;
/*      */   }
/*      */ 
/*      */   private IDynResManageService getDynResManageService() {
/* 2469 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*      */   }
/*      */ 
/*      */   private IBMModelService getBMModelService() {
/* 2473 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*      */   }
/*      */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.vm.impl.VMModelService
 * JD-Core Version:    0.6.0
 */