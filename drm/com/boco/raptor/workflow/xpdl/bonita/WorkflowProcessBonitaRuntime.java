/*    */ package com.boco.raptor.workflow.xpdl.bonita;
/*    */ 
/*    */ import com.boco.raptor.workflow.xpdl.AbstractWorkflowProcessRuntime;
/*    */ import com.boco.raptor.workflow.xpdl.ActivityXPDL;
/*    */ import com.boco.raptor.workflow.xpdl.Hook;
/*    */ import java.util.Map;
/*    */ import org.obe.xpdl.model.workflow.WorkflowProcess;
/*    */ 
/*    */ public class WorkflowProcessBonitaRuntime extends AbstractWorkflowProcessRuntime
/*    */ {
/*    */   public WorkflowProcessBonitaRuntime(WorkflowProcess process)
/*    */   {
/* 13 */     super(process);
/*    */   }
/*    */ 
/*    */   public String runActivity(String activityId, Map attributes) throws Exception {
/* 17 */     ActivityXPDL activityRuntime = (ActivityXPDL)this.activityRuntimes.get(activityId);
/* 18 */     String nextActivityId = activityRuntime.getToActivityId(attributes);
/* 19 */     if (activityRuntime.getHook() != null) {
/* 20 */       Class hookClass = Class.forName(activityRuntime.getHook());
/* 21 */       Hook hook = (Hook)hookClass.newInstance();
/* 22 */       if ((hook instanceof Hook))
/* 23 */         hook.execute(activityRuntime, attributes);
/*    */       else {
/* 25 */         throw new Exception("Class:" + activityRuntime.getHook() + "没有实现接口com.boco.raptor.workflow.xpdl.bonita.Hook");
/*    */       }
/*    */     }
/* 28 */     return nextActivityId;
/*    */   }
/*    */ 
/*    */   public String createInstance(Map attributes) {
/* 32 */     ActivityXPDL activityRuntime = (ActivityXPDL)this.activityRuntimes.get(XPDLBonitaParser.StartActivityId);
/* 33 */     return activityRuntime.getToActivityId(attributes);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.bonita.WorkflowProcessBonitaRuntime
 * JD-Core Version:    0.6.0
 */