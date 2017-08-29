/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmImportProcessInfo;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmImportResultProcessInfo;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmImportValidProcessInfo;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadInfo;
/*     */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import jxl.write.WriteException;
/*     */ 
/*     */ public class ExcelImpTemplateDownloadAction
/*     */ {
/*     */   public String[] getExcelTemplate(HttpServletRequest request, String bmClassId, ExcelImpTemplateMeta excelImpTemplate)
/*     */     throws IOException, WriteException, Exception
/*     */   {
/*  49 */     String[] returnFile = new String[2];
/*     */ 
/*  51 */     IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*     */ 
/*  54 */     String exportPath = request.getSession().getServletContext().getRealPath("/") + "import";
/*  55 */     String contextPath = request.getContextPath();
/*     */ 
/*  57 */     returnFile = getVMModelService().addTemplateHeaderMatrixClass(actionContext, exportPath, contextPath, bmClassId, excelImpTemplate);
/*     */ 
/*  59 */     return returnFile;
/*     */   }
/*     */ 
/*     */   public DrmUploadInfo getUploadInfo(HttpServletRequest req)
/*     */   {
/*  66 */     if (req.getSession().getAttribute("uploadInfo") != null) {
/*  67 */       return (DrmUploadInfo)req.getSession().getAttribute("uploadInfo");
/*     */     }
/*  69 */     return new DrmUploadInfo();
/*     */   }
/*     */ 
/*     */   public DrmImportValidProcessInfo getExcelImportValidInfo(HttpServletRequest req)
/*     */   {
/*  76 */     if (req.getSession().getAttribute("importValidProcessInfo") != null) {
/*  77 */       return (DrmImportValidProcessInfo)req.getSession().getAttribute("importValidProcessInfo");
/*     */     }
/*  79 */     return new DrmImportValidProcessInfo();
/*     */   }
/*     */ 
/*     */   public DrmImportProcessInfo getExcelImportInfo(HttpServletRequest req)
/*     */   {
/*  86 */     if (req.getSession().getAttribute("importProcessInfo") != null) {
/*  87 */       return (DrmImportProcessInfo)req.getSession().getAttribute("importProcessInfo");
/*     */     }
/*  89 */     return new DrmImportProcessInfo();
/*     */   }
/*     */ 
/*     */   public List<String> getExcelImportErrorList(HttpServletRequest req)
/*     */   {
/*  96 */     if (req.getSession().getAttribute("errorlist") != null) {
/*  97 */       return (List)req.getSession().getAttribute("errorlist");
/*     */     }
/*  99 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public DrmImportResultProcessInfo getExcelImportResultFileInfo(HttpServletRequest req)
/*     */   {
/* 106 */     if (req.getSession().getAttribute("importSaveResultFileInfo") != null) {
/* 107 */       return (DrmImportResultProcessInfo)req.getSession().getAttribute("importSaveResultFileInfo");
/*     */     }
/* 109 */     return new DrmImportResultProcessInfo();
/*     */   }
/*     */ 
/*     */   private IBMModelService getBMModelService()
/*     */   {
/* 114 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ 
/*     */   private IVMModelService getVMModelService() {
/* 118 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ExcelImpTemplateDownloadAction
 * JD-Core Version:    0.6.0
 */