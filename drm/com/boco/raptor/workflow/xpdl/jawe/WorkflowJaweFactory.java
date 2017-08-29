/*    */ package com.boco.raptor.workflow.xpdl.jawe;
/*    */ 
/*    */ import com.boco.common.util.io.FileHelper;
/*    */ import com.boco.raptor.workflow.xpdl.AbstractWorkFlowFactory;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.obe.xpdl.model.misc.RedefinableHeader;
/*    */ import org.obe.xpdl.model.workflow.WorkflowProcess;
/*    */ 
/*    */ public class WorkflowJaweFactory extends AbstractWorkFlowFactory
/*    */ {
/* 13 */   private static WorkflowJaweFactory instance = new WorkflowJaweFactory();
/*    */ 
/*    */   public static WorkflowJaweFactory getInstance()
/*    */   {
/* 18 */     return instance;
/*    */   }
/*    */ 
/*    */   public void config(String xpdlFileName) throws Exception {
/* 22 */     XPDLJaweParser parser = XPDLJaweParser.getInstance();
/* 23 */     InputStream ins = FileHelper.getFileStream(xpdlFileName);
/* 24 */     if (ins != null) {
/* 25 */       parser.load(ins);
/*    */     }
/* 27 */     Map xpdlPackages = parser.getAllXPDLPackage();
/* 28 */     Iterator ipackage = xpdlPackages.keySet().iterator();
/* 29 */     while (ipackage.hasNext()) {
/* 30 */       String packageId = ipackage.next().toString();
/* 31 */       System.out.println("流程包:" + packageId);
/* 32 */       Map wfps = (Map)xpdlPackages.get(packageId);
/* 33 */       Iterator iprocess = wfps.keySet().iterator();
/* 34 */       while (iprocess.hasNext()) {
/* 35 */         String processId = iprocess.next().toString();
/* 36 */         WorkflowProcess process = (WorkflowProcess)wfps.get(processId);
/* 37 */         System.out.println("WorkflowProcessId:" + processId);
/* 38 */         System.out.println("WorkflowProcessVersion:" + process.getRedefinableHeader().getVersion());
/* 39 */         WorkflowProcessJaweRuntime wfpRuntime = new WorkflowProcessJaweRuntime(process);
/* 40 */         workflowProcessRuntimeMap.put(wfpRuntime.getProcessId(), wfpRuntime);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.jawe.WorkflowJaweFactory
 * JD-Core Version:    0.6.0
 */