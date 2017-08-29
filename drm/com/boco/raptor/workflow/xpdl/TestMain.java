/*    */ package com.boco.raptor.workflow.xpdl;
/*    */ 
/*    */ import com.boco.raptor.workflow.xpdl.bonita.WorkflowBonitaFactory;
/*    */ import com.boco.raptor.workflow.xpdl.jawe.WorkflowJaweFactory;
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class TestMain
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 15 */     testJaweXPDL();
/*    */   }
/*    */ 
/*    */   public static void testBonitaXPDL() throws Exception {
/* 19 */     WorkflowBonitaFactory.getInstance().config("ProvinceTraphAttempRequestProcess.xpdl");
/* 20 */     WorkflowProcessRuntime workflowProcessRuntime = WorkflowBonitaFactory.getInstance().getWorkflowProcessRuntime("ProvinceTraphAttemp", "ProvinceTraphAttempRequestProcess");
/*    */ 
/* 22 */     String activityId = workflowProcessRuntime.createInstance(null);
/* 23 */     Map attributes = new HashMap();
/* 24 */     attributes.put("SHEET_ID", "测试用单号001");
/* 25 */     while (activityId != null) {
/* 26 */       System.out.println("活动状态ID:" + activityId);
/* 27 */       if (activityId.equals("Request"))
/*    */       {
/* 29 */         attributes.put("submit", Boolean.TRUE);
/*    */       }
/* 31 */       else if (activityId.equals("Approval"))
/*    */       {
/* 34 */         attributes.put("direction", "approvalmanager");
/*    */       }
/* 36 */       else if (activityId.equals("ApprovalManager"))
/*    */       {
/* 38 */         attributes.put("approvalManagerDirection", "autoexec");
/*    */       }
/* 40 */       else if (activityId.equals("reject"))
/*    */       {
/* 42 */         attributes.put("approvalManagerDirection", "autoexec");
/*    */       }
/* 44 */       else if (activityId.equals("AutoExec"))
/*    */       {
/* 46 */         attributes.put("completed", Boolean.TRUE);
/*    */       }
/*    */ 
/* 49 */       activityId = workflowProcessRuntime.runActivity(activityId, attributes);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void testJaweXPDL() throws Exception {
/* 54 */     WorkflowJaweFactory.getInstance().config("GMCC_ProvinceTraphAttemp.xpdl");
/* 55 */     WorkflowProcessRuntime workflowProcessRuntime = WorkflowJaweFactory.getInstance().getWorkflowProcessRuntime("GMCC_ProvinceTraphAttemp", "RequestProcess");
/*    */ 
/* 57 */     String activityId = workflowProcessRuntime.createInstance(null);
/* 58 */     while (activityId != null) {
/* 59 */       Map attributes = new HashMap();
/* 60 */       attributes.put("SHEET_ID", "测试用单号001");
/* 61 */       System.out.println("活动状态ID:" + activityId);
/* 62 */       if (activityId.equals("Request"))
/*    */       {
/* 64 */         attributes.put("rqeuestPort", Boolean.TRUE);
/* 65 */       } else if (activityId.equals("PortAdminApprove"))
/*    */       {
/* 67 */         attributes.put("submit", Boolean.TRUE);
/* 68 */       } else if (activityId.equals("PortUserConfig"))
/*    */       {
/* 70 */         attributes.put("submit", Boolean.TRUE);
/* 71 */       } else if (activityId.equals("ApprovalManager"))
/*    */       {
/* 73 */         attributes.put("approvalManagerDirection", "autoexec");
/*    */       }
/* 75 */       else if (activityId.equals("reject"))
/*    */       {
/* 77 */         attributes.put("approvalManagerDirection", "autoexec");
/*    */       }
/* 79 */       else if (activityId.equals("AutoExec"))
/*    */       {
/* 81 */         attributes.put("completed", Boolean.TRUE);
/*    */       }
/*    */ 
/* 84 */       activityId = workflowProcessRuntime.runActivity(activityId, attributes);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.TestMain
 * JD-Core Version:    0.6.0
 */