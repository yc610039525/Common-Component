/*     */ package com.boco.raptor.workflow.xpdl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.obe.xpdl.model.activity.Activity;
/*     */ import org.obe.xpdl.model.activity.AutomationMode;
/*     */ import org.obe.xpdl.model.activity.Implementation;
/*     */ import org.obe.xpdl.model.activity.Tool;
/*     */ import org.obe.xpdl.model.activity.ToolSet;
/*     */ import org.obe.xpdl.model.condition.Condition;
/*     */ import org.obe.xpdl.model.condition.ConditionType;
/*     */ import org.obe.xpdl.model.data.DataField;
/*     */ import org.obe.xpdl.model.data.DataType;
/*     */ import org.obe.xpdl.model.data.Type;
/*     */ import org.obe.xpdl.model.misc.ExtendedAttributes;
/*     */ import org.obe.xpdl.model.transition.Transition;
/*     */ import org.obe.xpdl.model.workflow.WorkflowProcess;
/*     */ 
/*     */ public abstract class AbstractWorkflowProcessRuntime
/*     */   implements WorkflowProcessRuntime
/*     */ {
/*     */   private String packageId;
/*     */   private String processId;
/*  21 */   protected Map<String, ActivityXPDL> activityRuntimes = new HashMap();
/*  22 */   private Map<String, Object> attributes = new HashMap();
/*     */ 
/*     */   public AbstractWorkflowProcessRuntime(WorkflowProcess process) {
/*     */     try {
/*  26 */       DataField[] dataFields = process.getDataField();
/*  27 */       for (DataField dataField : dataFields) {
/*  28 */         Object attributeValue = null;
/*  29 */         System.out.println("[" + dataField.getDataType().getType().value() + "]" + dataField.getId() + "=" + dataField.getInitialValue());
/*  30 */         switch (dataField.getDataType().getType().value()) {
/*     */         case 5:
/*  32 */           if ((dataField.getInitialValue() == null) || (dataField.getInitialValue().trim().length() == 0))
/*  33 */             attributeValue = Boolean.FALSE;
/*     */           else {
/*  35 */             attributeValue = Boolean.valueOf(dataField.getInitialValue());
/*     */           }
/*  37 */           break;
/*     */         case 12:
/*  39 */           attributeValue = dataField.getInitialValue();
/*  40 */           break;
/*     */         case 0:
/*  42 */           attributeValue = dataField.getInitialValue();
/*  43 */           break;
/*     */         case 1:
/*  45 */           if (dataField.getInitialValue() == null) break;
/*  46 */           attributeValue = Double.valueOf(dataField.getInitialValue()); break;
/*     */         case 2:
/*  50 */           if (dataField.getInitialValue() == null) break;
/*  51 */           attributeValue = Integer.valueOf(dataField.getInitialValue()); break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 6:
/*     */         case 7:
/*     */         case 8:
/*     */         case 9:
/*     */         case 10:
/*     */         case 11:
/*     */         default:
/*  55 */           System.out.println("不支持的DataField类型:" + dataField.getDataType().getType().value());
/*     */         }
/*  57 */         System.out.println("流程变量:" + dataField.getId() + "=" + dataField.getInitialValue());
/*  58 */         this.attributes.put(dataField.getId(), attributeValue);
/*     */       }
/*     */ 
/*  62 */       this.packageId = process.getPackageId();
/*  63 */       this.processId = process.getId();
/*  64 */       Activity[] activitys = process.getActivity();
/*  65 */       for (Activity activity : activitys) {
/*  66 */         ActivityXPDL activityRuntime = new ActivityXPDL();
/*  67 */         activityRuntime.setId(activity.getId());
/*  68 */         activityRuntime.setStartMode(activity.getStartMode().toString());
/*  69 */         ExtendedAttributes extendedAttributes = activity.getExtendedAttributes();
/*  70 */         String hook = null;
/*  71 */         if (extendedAttributes != null) {
/*  72 */           hook = extendedAttributes.get("hook");
/*     */         }
/*  74 */         if (hook == null)
/*     */         {
/*  76 */           Implementation imp = activity.getImplementation();
/*  77 */           if ((imp instanceof ToolSet)) {
/*  78 */             ToolSet toolset = (ToolSet)activity.getImplementation();
/*  79 */             hook = (String)toolset.getTool()[0].getExtendedAttributes().getMap().get("hook");
/*     */           }
/*     */         }
/*  82 */         if (hook != null) {
/*  83 */           activityRuntime.setHook(hook);
/*     */         }
/*  85 */         activityRuntime.initAttributes(this.attributes);
/*  86 */         System.out.println("活动:" + activity.getId() + "[hook=" + hook + "]");
/*  87 */         this.activityRuntimes.put(activity.getId(), activityRuntime);
/*     */       }
/*     */ 
/*  90 */       Transition[] transitions = process.getTransition();
/*  91 */       for (Transition transition : transitions) {
/*  92 */         ActivityXPDL activityRuntime = (ActivityXPDL)this.activityRuntimes.get(transition.getFrom());
/*  93 */         if (activityRuntime != null) {
/*  94 */           Condition condition = transition.getCondition();
/*  95 */           if (condition != null) {
/*  96 */             if (condition.getType() == ConditionType.CONDITION) {
/*  97 */               ActivityXPDL toActivityRuntime = (ActivityXPDL)this.activityRuntimes.get(transition.getTo());
/*  98 */               activityRuntime.addToActivity(toActivityRuntime, condition.getValue());
/*     */             } else {
/* 100 */               throw new Exception("流程" + process.getPackageId() + "." + process.getId() + "Transition.Condition:" + condition.getType() + "不支持");
/*     */             }
/*     */           } else {
/* 103 */             ActivityXPDL toActivityRuntime = (ActivityXPDL)this.activityRuntimes.get(transition.getTo());
/* 104 */             activityRuntime.addToActivity(toActivityRuntime, null);
/*     */           }
/* 106 */           if (condition != null)
/* 107 */             System.out.println("活动转换:" + transition.getFrom() + "->" + transition.getTo() + "[" + condition.getValue() + "]");
/*     */           else
/* 109 */             System.out.println("活动转换:" + transition.getFrom() + "->" + transition.getTo() + "[" + condition + "]");
/*     */         }
/*     */         else {
/* 112 */           throw new Exception("流程" + process.getPackageId() + "." + process.getId() + "定义错误,没有找到Activity:" + transition.getFrom());
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) {
/* 116 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getProcessId() {
/* 121 */     return this.packageId + "." + this.processId;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.AbstractWorkflowProcessRuntime
 * JD-Core Version:    0.6.0
 */