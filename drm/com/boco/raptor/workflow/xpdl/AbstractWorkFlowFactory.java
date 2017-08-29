/*    */ package com.boco.raptor.workflow.xpdl;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class AbstractWorkFlowFactory
/*    */ {
/*  7 */   protected static Map<String, WorkflowProcessRuntime> workflowProcessRuntimeMap = new HashMap();
/*    */ 
/*    */   public void config(String xpdlFileName)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public WorkflowProcessRuntime getWorkflowProcessRuntime(String packageId, String processId) throws Exception
/*    */   {
/* 16 */     WorkflowProcessRuntime workflowProcessRuntime = (WorkflowProcessRuntime)workflowProcessRuntimeMap.get(packageId + "." + processId);
/* 17 */     if (workflowProcessRuntime != null) {
/* 18 */       return workflowProcessRuntime;
/*    */     }
/* 20 */     throw new Exception("没有找到流程定义:[" + packageId + "." + processId + "]");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.AbstractWorkFlowFactory
 * JD-Core Version:    0.6.0
 */