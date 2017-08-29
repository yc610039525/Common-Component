/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.transnms.common.dto.DrmModelLogDetail;
/*     */ import com.boco.transnms.common.dto.DrmModelLogIndex;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DynModleManageAction
/*     */ {
/*  43 */   private IBMModelService bmModelService = getBMModelService();
/*  44 */   private IDynResManageService dynResManageService = getDynResManageService();
/*     */ 
/*     */   public void clearSessionModelFile(HttpServletRequest request)
/*     */   {
/*  50 */     request.getSession().setAttribute(ModelUploadAction.SessionNames.MODEL_FILE_BYTES, null);
/*  51 */     request.getSession().setAttribute(ModelCompareUploadAction.SessionNames.BMMODEL_FILE_BYTES, null);
/*  52 */     request.getSession().setAttribute(ModelCompareUploadAction.SessionNames.COMPARED_FILE_BYTES, null);
/*     */   }
/*     */ 
/*     */   public String modifyModelLogIndex(HttpServletRequest request, String changeModelName, String remark, boolean comparedUpload) throws UserException {
/*  56 */     String errMsg = null;
/*     */     try {
/*  58 */       if (comparedUpload) {
/*  59 */         byte[] bmFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelCompareUploadAction.SessionNames.BMMODEL_FILE_BYTES);
/*  60 */         byte[] comparedFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelCompareUploadAction.SessionNames.COMPARED_FILE_BYTES);
/*  61 */         this.bmModelService.modifyModelMeta(ServiceHelper.createSvActCxt(), changeModelName, remark, bmFileBytes, comparedFileBytes);
/*     */       } else {
/*  63 */         byte[] bmFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelUploadAction.SessionNames.MODEL_FILE_BYTES);
/*  64 */         this.bmModelService.modifyModelMeta(ServiceHelper.createSvActCxt(), changeModelName, remark, bmFileBytes);
/*     */       }
/*     */     } catch (Exception ex) {
/*  67 */       errMsg = ex.getMessage();
/*  68 */       LogHome.getLog().error("", ex);
/*     */     }
/*  70 */     return errMsg;
/*     */   }
/*     */ 
/*     */   public Map getDrmModelLogIDetailData(HttpServletRequest request, boolean comparedUpdate) throws UserException {
/*  74 */     Map map = new HashMap();
/*  75 */     DrmModelLogIndex modelLogIndex = null;
/*  76 */     if (comparedUpdate) {
/*  77 */       byte[] bmFileBytes = bmFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelCompareUploadAction.SessionNames.BMMODEL_FILE_BYTES);
/*  78 */       byte[] comparedFileBytes = comparedFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelCompareUploadAction.SessionNames.COMPARED_FILE_BYTES);
/*  79 */       if ((bmFileBytes == null) || (comparedFileBytes == null)) {
/*  80 */         throw new UserException("上载模型文件为空，请等待 ！");
/*     */       }
/*  82 */       modelLogIndex = this.bmModelService.getModelCompareLog(ServiceHelper.createSvActCxt(), bmFileBytes, comparedFileBytes);
/*     */     } else {
/*  84 */       byte[] bmFileBytes = bmFileBytes = (byte[])(byte[])request.getSession().getAttribute(ModelUploadAction.SessionNames.MODEL_FILE_BYTES);
/*  85 */       if (bmFileBytes == null) {
/*  86 */         throw new UserException("上载模型文件为空，请等待 ！");
/*     */       }
/*  88 */       modelLogIndex = this.bmModelService.getModelCompareLog(ServiceHelper.createSvActCxt(), bmFileBytes);
/*     */     }
/*  90 */     String errorLog = (String)modelLogIndex.getAttrValueT("ERROR");
/*  91 */     map.put("ERROR", errorLog);
/*  92 */     List classLog = (List)modelLogIndex.getAttrValueT("CLASS_LOG_DETAIL");
/*  93 */     List classLogData = getModelLogDetailData(classLog);
/*  94 */     map.put("CLASS_LOG_DETAIL", classLogData);
/*  95 */     List attrLog = (List)modelLogIndex.getAttrValueT("ATTR_LOG_DETAIL");
/*  96 */     List attrLogData = getModelLogDetailData(attrLog);
/*  97 */     map.put("ATTR_LOG_DETAIL", attrLogData);
/*  98 */     List enumLog = (List)modelLogIndex.getAttrValueT("ENUM_LOG_DETAIL");
/*  99 */     List enumLogData = getModelLogDetailData(enumLog);
/* 100 */     map.put("ENUM_LOG_DETAIL", enumLogData);
/* 101 */     return map;
/*     */   }
/*     */ 
/*     */   private List getModelLogDetailData(List<DrmModelLogDetail> list)
/*     */   {
/* 109 */     if ((list == null) || (list.size() == 0)) {
/* 110 */       return null;
/*     */     }
/* 112 */     List newList = new ArrayList();
/* 113 */     for (int i = 0; i < list.size(); i++) {
/* 114 */       DrmModelLogDetail mflil = (DrmModelLogDetail)list.get(i);
/* 115 */       String firstItemName = mflil.getFirstItemName();
/* 116 */       String secondItemName = mflil.getSecondItemName();
/* 117 */       String thirdItemName = mflil.getThirdItemName();
/* 118 */       Long change_Type = Long.valueOf(mflil.getChangeType());
/* 119 */       String remark = mflil.getRemark();
/* 120 */       String changeType = judgeChangType(change_Type);
/* 121 */       String[] str = { firstItemName, secondItemName, thirdItemName, changeType, remark };
/* 122 */       newList.add(str);
/*     */     }
/* 124 */     return newList;
/*     */   }
/*     */ 
/*     */   private String judgeChangType(Long change_Type)
/*     */   {
/*     */     String changeType;
/*     */     String changeType;
/* 133 */     if (change_Type.longValue() == 1L) {
/* 134 */       changeType = "增加";
/*     */     }
/*     */     else
/*     */     {
/*     */       String changeType;
/* 135 */       if (change_Type.longValue() == 2L)
/* 136 */         changeType = "删除";
/*     */       else
/* 138 */         changeType = "修改";
/*     */     }
/* 140 */     return changeType;
/*     */   }
/*     */ 
/*     */   public List getDrmModelLogDetailValue(HttpServletRequest request, String cuid, String itemType)
/*     */     throws UserException
/*     */   {
/* 152 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 153 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 154 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 155 */     query.setBmClassId("DRM_MODEL_LOG_DETAIL");
/* 156 */     query.addQueryAttrId("FIRST_ITEM_NAME");
/* 157 */     query.addQueryAttrId("SECOND_ITEM_NAME");
/* 158 */     query.addQueryAttrId("THIRD_ITEM_NAME");
/* 159 */     query.addQueryAttrId("CHANGE_TYPE");
/* 160 */     query.addQueryAttrId("REMARK");
/* 161 */     query.addQueryCondExps(new DrmQueryAttrCond("RELATED_LOG_INDEX_CUID", "=", cuid));
/* 162 */     query.addQueryCondExps(new DrmQueryAttrCond("ITEM_TYPE", "=", itemType));
/* 163 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 165 */       DrmModelLogDetail dd = new DrmModelLogDetail();
/* 166 */       rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query, dd);
/*     */     } catch (Exception ex) {
/* 168 */       LogHome.getLog().error("", ex);
/* 169 */       throw new UserException(ex.getMessage());
/*     */     }
/* 171 */     List rows = rs.getResultSet();
/* 172 */     int i = 0;
/* 173 */     List list = new ArrayList();
/* 174 */     for (IDrmQueryRow row : rows) {
/* 175 */       IDrmDataObject dbo = row.getResultDbo("DRM_MODEL_LOG_DETAIL");
/* 176 */       String firstItemName = (String)dbo.getAttrValueT("FIRST_ITEM_NAME");
/* 177 */       String secondItemName = (String)dbo.getAttrValueT("SECOND_ITEM_NAME");
/* 178 */       String thirdItemName = (String)dbo.getAttrValueT("THIRD_ITEM_NAME");
/* 179 */       Long change_Type = (Long)dbo.getAttrValueT("CHANGE_TYPE");
/* 180 */       String remark = (String)dbo.getAttrValueT("REMARK");
/* 181 */       String changeType = judgeChangType(change_Type);
/* 182 */       String[] str = { firstItemName, secondItemName, thirdItemName, changeType, remark };
/* 183 */       list.add(str);
/*     */     }
/* 185 */     return list;
/*     */   }
/*     */ 
/*     */   public List getDrmModelLogIndexValue(HttpServletRequest request, String beginTime, String endTime)
/*     */     throws UserException
/*     */   {
/* 194 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/* 195 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 196 */     DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 197 */     query.setBmClassId("DRM_MODEL_LOG_INDEX");
/* 198 */     query.addQueryAttrId("CUID");
/* 199 */     query.addQueryAttrId("CHANGE_NAME");
/* 200 */     query.addQueryAttrId("CHANGE_TIME");
/* 201 */     query.addQueryAttrId("REMARK");
/*     */ 
/* 214 */     if (beginTime.length() > 0) {
/* 215 */       query.addQueryCondExps(new DrmQueryAttrCond("CHANGE_TIME", ">=", beginTime));
/*     */     }
/* 217 */     if (endTime.length() > 0) {
/* 218 */       query.addQueryCondExps(new DrmQueryAttrCond("CHANGE_TIME", "<=", endTime));
/*     */     }
/*     */ 
/* 221 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 223 */       DrmModelLogIndex dd = new DrmModelLogIndex();
/* 224 */       rs = this.dynResManageService.getDynObjBySql(actionContext, queryContext, query, dd);
/*     */     } catch (Exception ex) {
/* 226 */       LogHome.getLog().error("", ex);
/* 227 */       throw new UserException(ex.getMessage());
/*     */     }
/* 229 */     List rows = rs.getResultSet();
/* 230 */     int i = 0;
/* 231 */     List list = new ArrayList();
/* 232 */     for (IDrmQueryRow row : rows) {
/* 233 */       IDrmDataObject dbo = row.getResultDbo("DRM_MODEL_LOG_INDEX");
/* 234 */       String cuid = (String)dbo.getAttrValueT("CUID");
/* 235 */       String changeName = (String)dbo.getAttrValueT("CHANGE_NAME");
/* 236 */       Timestamp change_Time = (Timestamp)dbo.getAttrValueT("CHANGE_TIME");
/* 237 */       String remark = (String)dbo.getAttrValueT("REMARK");
/* 238 */       String changeTime = change_Time.toString();
/* 239 */       String[] str = { cuid, changeName + "_" + changeTime, remark };
/* 240 */       list.add(str);
/*     */     }
/* 242 */     return list;
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService() {
/* 246 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   public static IDynResManageService getDynResManageService() {
/* 250 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DynModleManageAction
 * JD-Core Version:    0.6.0
 */