/*    */ package com.boco.raptor.workflow.xpdl;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipInputStream;
/*    */ import org.obe.xpdl.model.pkg.XPDLPackage;
/*    */ import org.obe.xpdl.model.workflow.WorkflowProcess;
/*    */ import org.obe.xpdl.parser.XPDLParser;
/*    */ import org.obe.xpdl.parser.dom4j.Dom4JXPDLParser;
/*    */ 
/*    */ public abstract class AbstractXPDLParser
/*    */ {
/* 16 */   private static Map<String, Map<String, WorkflowProcess>> xpdlPackages = new HashMap();
/*    */ 
/*    */   public void load(InputStream xpdlStream)
/*    */     throws Exception
/*    */   {
/* 21 */     XPDLParser parser = new Dom4JXPDLParser();
/* 22 */     XPDLPackage xpdlpkg = parser.parse(xpdlStream);
/* 23 */     if (xpdlPackages.get(xpdlpkg.getPackageId()) == null) {
/* 24 */       WorkflowProcess[] processArray = xpdlpkg.getWorkflowProcess();
/* 25 */       Map wps = new HashMap();
/* 26 */       for (WorkflowProcess process : processArray) {
/* 27 */         if (wps.get(process.getId()) == null)
/* 28 */           wps.put(process.getId(), process);
/*    */         else {
/* 30 */           throw new Exception("在包" + xpdlpkg.getPackageId() + "下已经存在流程:" + process.getId());
/*    */         }
/*    */       }
/* 33 */       xpdlPackages.put(xpdlpkg.getPackageId(), wps);
/*    */     } else {
/* 35 */       Map wps = (Map)xpdlPackages.get(xpdlpkg.getPackageId());
/* 36 */       WorkflowProcess[] processArray = xpdlpkg.getWorkflowProcess();
/* 37 */       for (WorkflowProcess process : processArray)
/* 38 */         if (wps.get(process.getId()) == null)
/* 39 */           wps.put(process.getId(), process);
/*    */         else
/* 41 */           throw new Exception("在包" + xpdlpkg.getPackageId() + "下已经存在流程:" + process.getId());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void loadZip(InputStream zipStream)
/*    */     throws Exception
/*    */   {
/* 48 */     ZipInputStream zin = new ZipInputStream(zipStream);
/*    */     ZipEntry entry;
/* 50 */     while ((entry = zin.getNextEntry()) != null) {
/* 51 */       System.out.println("Extracting: " + entry.getName());
/* 52 */       byte[] buf = new byte[(int)entry.getSize()];
/* 53 */       zin.read(buf, 0, (int)entry.getSize());
/* 54 */       InputStream xpdlStream = new ByteArrayInputStream(buf);
/* 55 */       load(xpdlStream);
/*    */     }
/* 57 */     zin.close();
/*    */   }
/*    */ 
/*    */   public WorkflowProcess getWorkflowProcess(String packageId, String processId) {
/* 61 */     WorkflowProcess process = null;
/* 62 */     Map wps = (Map)xpdlPackages.get(packageId);
/* 63 */     if (wps != null) {
/* 64 */       process = (WorkflowProcess)wps.get(processId);
/*    */     }
/* 66 */     return process;
/*    */   }
/*    */ 
/*    */   public Map<String, Map<String, WorkflowProcess>> getAllXPDLPackage() {
/* 70 */     return xpdlPackages;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.AbstractXPDLParser
 * JD-Core Version:    0.6.0
 */