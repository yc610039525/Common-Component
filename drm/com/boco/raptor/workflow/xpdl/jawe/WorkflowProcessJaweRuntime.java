/*    */ package com.boco.raptor.workflow.xpdl.jawe;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.raptor.workflow.xpdl.AbstractWorkflowProcessRuntime;
/*    */ import com.boco.raptor.workflow.xpdl.ActivityXPDL;
/*    */ import com.boco.raptor.workflow.xpdl.Hook;
/*    */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.obe.xpdl.model.workflow.WorkflowProcess;
/*    */ 
/*    */ public class WorkflowProcessJaweRuntime extends AbstractWorkflowProcessRuntime
/*    */ {
/* 14 */   String StartActivityId = null;
/* 15 */   String EndOfWorkflow = null;
/*    */ 
/* 17 */   public WorkflowProcessJaweRuntime(WorkflowProcess process) throws Exception { super(process);
/* 18 */     this.StartActivityId = process.getExtendedAttributes().get(XPDLJaweParser.StartActivityId).split(";")[1];
/* 19 */     this.EndOfWorkflow = process.getExtendedAttributes().get(XPDLJaweParser.EndActivityId).split(";")[1]; }
/*    */ 
/*    */   public String runActivity(String activityId, Map attributes)
/*    */     throws Exception
/*    */   {
/* 24 */     ActivityXPDL activityRuntime = (ActivityXPDL)this.activityRuntimes.get(activityId);
/* 25 */     String nextActivityId = activityRuntime.getToActivityId(attributes);
/* 26 */     if (activityRuntime.getHook() != null) {
/* 27 */       LogHome.getLog().info("hook:" + activityRuntime.getHook());
/* 28 */       Hook hook = (Hook)BoHomeFactory.getInstance().getBO(activityRuntime.getHook());
/*    */ 
/* 31 */       if ((hook instanceof Hook))
/* 32 */         hook.execute(activityRuntime, attributes);
/*    */       else {
/* 34 */         throw new Exception("Class:" + activityRuntime.getHook() + "没有实现接口com.boco.raptor.workflow.xpdl.bonita.Hook");
/*    */       }
/*    */     }
/* 37 */     return nextActivityId;
/*    */   }
/*    */ 
/*    */   public String createInstance(Map attributes) throws Exception {
/* 41 */     ActivityXPDL activityRuntime = (ActivityXPDL)this.activityRuntimes.get(this.StartActivityId);
/* 42 */     if (activityRuntime.getHook() != null) {
/* 43 */       Class hookClass = Class.forName(activityRuntime.getHook());
/* 44 */       Hook hook = (Hook)hookClass.newInstance();
/* 45 */       if ((hook instanceof Hook))
/* 46 */         hook.execute(activityRuntime, attributes);
/*    */       else {
/* 48 */         throw new Exception("Class:" + activityRuntime.getHook() + "没有实现接口com.boco.raptor.workflow.xpdl.bonita.Hook");
/*    */       }
/*    */     }
/* 51 */     return activityRuntime.getToActivityId(attributes);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.jawe.WorkflowProcessJaweRuntime
 * JD-Core Version:    0.6.0
 */