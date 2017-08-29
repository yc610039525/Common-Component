/*     */ package com.boco.raptor.drm.core.web.action;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.TimeFormatHelper;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportListener;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportResultListener;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportValidListener;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmMonitoredDiskFileItemFactory;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta.DATA_TYPE;
/*     */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadListener;
/*     */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.apache.commons.fileupload.FileItem;
/*     */ import org.apache.commons.fileupload.FileItemFactory;
/*     */ import org.apache.commons.fileupload.FileUploadException;
/*     */ import org.apache.commons.fileupload.servlet.ServletFileUpload;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ExcelImptemplateImportDataAction extends HttpServlet
/*     */ {
/*     */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  36 */     excelUploadAndDataImport(request, response);
/*     */   }
/*     */ 
/*     */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
/*     */   {
/*  41 */     doGet(request, response);
/*     */   }
/*     */ 
/*     */   public void excelUploadAndDataImport(HttpServletRequest request, HttpServletResponse response) {
/*  45 */     response.setContentType("text/html;charset=GBK");
/*  46 */     String sResult = ""; String resultFile = ""; String resultFileName = "";
/*     */ 
/*  48 */     String sClassId = request.getParameter("bmClassId");
/*  49 */     String doType = request.getParameter("doType");
/*     */ 
/*  51 */     long beginTime = System.currentTimeMillis();
/*  52 */     System.out.println("线程[" + Thread.currentThread().getName() + "]导入开始！开始时间：" + TimeFormatHelper.getFormatDate(new Date(beginTime), "yyyy-MM-dd HH:mm:ss:SSS"));
/*     */ 
/*  54 */     DrmUploadListener listener = new DrmUploadListener(request, 10L);
/*  55 */     FileItemFactory factory = new DrmMonitoredDiskFileItemFactory(listener);
/*  56 */     ServletFileUpload upload = new ServletFileUpload(factory);
/*  57 */     List errorlist = new ArrayList();
/*     */     try {
/*  59 */       List fileItems = upload.parseRequest(request);
/*  60 */       List a = new ArrayList();
/*  61 */       for (int i = 0; i < fileItems.size(); i++) {
/*  62 */         FileItem fileItem = (FileItem)fileItems.get(i);
/*  63 */         String fileName = fileItem.getName();
/*  64 */         System.out.println("filePath=======" + fileName);
/*  65 */         if ((fileName != null) && (fileName.trim().length() != 0)) {
/*  66 */           a.add(fileName);
/*     */         }
/*     */       }
/*     */ 
/*  70 */       if (a.size() == 0) {
/*  71 */         errorlist.add("导入文件不能为空!");
/*  72 */         request.getSession().setAttribute("errorlist", errorlist);
/*     */       } else {
/*  74 */         Iterator iterator = fileItems.iterator();
/*  75 */         while (iterator.hasNext()) {
/*  76 */           FileItem fileItem = (FileItem)iterator.next();
/*  77 */           String fileName = fileItem.getName();
/*  78 */           if ((fileName != null) && 
/*  79 */             (fileName.trim().length() != 0)) {
/*  80 */             if (fileName.indexOf("\\") != -1)
/*  81 */               fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
/*  82 */             else if (fileName.indexOf("/") != -1) {
/*  83 */               fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
/*     */             }
/*  85 */             fileName = fileName.substring(0, fileName.length() - 4) + "_" + System.currentTimeMillis() + "." + fileName.substring(fileName.length() - 3, fileName.length());
/*  86 */             System.out.println("开始导入:" + fileName);
/*     */ 
/*  88 */             IVMModelService vService = getVMModelService();
/*     */ 
/*  90 */             List allData = new ArrayList();
/*  91 */             IServiceActionContext actionContext = ServiceHelper.getUserSvActCxt(request);
/*  92 */             actionContext.setBmClassId(sClassId);
/*  93 */             DrmExcelImportValidListener validListener = new DrmExcelImportValidListener(request, 0L, 1);
/*  94 */             validListener.start();
/*     */ 
/*  96 */             Map returnMap = vService.validateExcelFile(actionContext, fileItem.get(), sClassId, doType);
/*  97 */             errorlist = (List)returnMap.get("errorlist");
/*  98 */             allData = (List)returnMap.get("alldata");
/*  99 */             validListener.done();
/* 100 */             if (errorlist.size() > 0) {
/* 101 */               response.getWriter().print("{success:false,message:'核查有错'}");
/* 102 */               request.getSession().setAttribute("errorlist", errorlist);
/* 103 */               return;
/*     */             }
/*     */ 
/* 106 */             String contextPath = request.getSession().getServletContext().getRealPath("/");
/* 107 */             String contextUrl = request.getContextPath();
/* 108 */             if (!doType.equals("check")) {
/* 109 */               String fileUrl = contextUrl + "//import//ImportResult_" + fileName;
/*     */ 
/* 111 */               DrmExcelImportListener importListener = new DrmExcelImportListener(request, 0L, 1);
/* 112 */               importListener.start();
/* 113 */               returnMap = vService.importExcelData(actionContext, doType, allData, fileUrl);
/* 114 */               sResult = (String)returnMap.get("resultinfo");
/* 115 */               allData = (List)returnMap.get("alldata");
/* 116 */               importListener.done();
/* 117 */               resultFileName = "ImportResult_" + fileName;
/*     */ 
/* 119 */               DrmExcelImportResultListener resultListener = new DrmExcelImportResultListener(request, 0L, 1);
/* 120 */               resultListener.start();
/* 121 */               resultFile = vService.createResultFile(actionContext, fileName, contextPath, contextUrl, doType, fileItem.get(), allData, errorlist);
/* 122 */               resultListener.done();
/*     */             } else {
/* 124 */               DrmExcelImportResultListener resultListener = new DrmExcelImportResultListener(request, 0L, 1);
/* 125 */               resultListener.start();
/*     */ 
/* 127 */               sResult = "核查未发现错误";
/* 128 */               for (int i = 0; i < allData.size(); i++) {
/* 129 */                 DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/* 130 */                 if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR)) {
/* 131 */                   sResult = "核查发现错误";
/* 132 */                   break;
/*     */                 }
/*     */               }
/* 135 */               resultFileName = "CheckResult_" + fileName;
/*     */ 
/* 137 */               resultFile = vService.createResultFile(actionContext, fileName, contextPath, contextUrl, doType, fileItem.get(), allData, errorlist);
/* 138 */               resultListener.done();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 144 */       long finishTime = System.currentTimeMillis();
/* 145 */       System.out.println("线程[" + Thread.currentThread().getName() + "]导入结束！结束时间：" + TimeFormatHelper.getFormatDate(new Date(finishTime), "yyyy-MM-dd HH:mm:ss:SSS"));
/* 146 */       System.out.println("线程[" + Thread.currentThread().getName() + "]导入花费时间：" + (finishTime - beginTime) / 1000.0D + "秒。");
/*     */ 
/* 148 */       response.getWriter().print("{success:true,message:'" + sResult + "',resultfile:'" + resultFile + "',filename:'" + resultFileName + "'}");
/*     */     } catch (FileUploadException e) {
/* 150 */       errorlist.add("发生异常：" + e.getMessage());
/* 151 */       LogHome.getLog().error("", e);
/* 152 */       request.getSession().setAttribute("errorlist", errorlist);
/*     */       try {
/* 154 */         response.getWriter().print("{success:false,message:'文件上传失败'}");
/*     */       } catch (Exception error) {
/*     */       }
/*     */     } catch (IOException e) {
/* 158 */       LogHome.getLog().error("", e);
/* 159 */       errorlist.add("发生异常：" + e.getMessage());
/* 160 */       request.getSession().setAttribute("errorlist", errorlist);
/*     */       try {
/* 162 */         response.getWriter().print("{success:false,message:'失败'}");
/*     */       } catch (Exception error) {
/*     */       }
/*     */     } catch (Exception e) {
/* 166 */       LogHome.getLog().error("", e);
/* 167 */       errorlist.add("发生异常：" + e.getMessage());
/* 168 */       request.getSession().setAttribute("errorlist", errorlist);
/*     */       try {
/* 170 */         response.getWriter().print("{success:false,message:'失败'}");
/*     */       } catch (Exception error) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static IVMModelService getVMModelService() {
/* 177 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.ExcelImptemplateImportDataAction
 * JD-Core Version:    0.6.0
 */