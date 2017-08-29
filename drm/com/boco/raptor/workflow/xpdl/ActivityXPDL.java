/*     */ package com.boco.raptor.workflow.xpdl;
/*     */ 
/*     */ import bsh.Interpreter;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.obe.xpdl.model.activity.AutomationMode;
/*     */ 
/*     */ public class ActivityXPDL
/*     */ {
/*     */   private String id;
/*     */   private String startMode;
/*     */   private String hook;
/*  15 */   private Map<String, Object> processAttributes = new HashMap();
/*  16 */   private Map<String, ToActivity> toActivitys = new HashMap();
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*  21 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public void setStartMode(String startMode) {
/*  25 */     this.startMode = startMode;
/*     */   }
/*     */ 
/*     */   public void setHook(String hook) {
/*  29 */     this.hook = hook;
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  33 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getStartMode() {
/*  37 */     return this.startMode;
/*     */   }
/*     */ 
/*     */   public String getHook() {
/*  41 */     return this.hook;
/*     */   }
/*     */ 
/*     */   public void addToActivity(ActivityXPDL toActivityRuntime, String condition) {
/*  45 */     this.toActivitys.put(toActivityRuntime.getId(), new ToActivity(toActivityRuntime, condition));
/*     */   }
/*     */ 
/*     */   public void addAttributes(String attributeId, Object attributeValue) {
/*  49 */     this.processAttributes.put(attributeId, attributeValue);
/*     */   }
/*     */ 
/*     */   public void initAttributes(Map<String, Object> attributes) {
/*  53 */     this.processAttributes.clear();
/*  54 */     this.processAttributes.putAll(attributes);
/*     */   }
/*     */ 
/*     */   public String getToActivityId(Map _attributes) {
/*  58 */     Map attributes = new HashMap();
/*  59 */     if (_attributes != null) {
/*  60 */       attributes.putAll(this.processAttributes);
/*  61 */       attributes.putAll(_attributes);
/*     */     } else {
/*  63 */       attributes.putAll(this.processAttributes);
/*     */     }
/*  65 */     Iterator i = this.toActivitys.keySet().iterator();
/*  66 */     while (i.hasNext()) {
/*  67 */       String toActivityId = (String)i.next();
/*  68 */       ToActivity toActivity = (ToActivity)this.toActivitys.get(toActivityId);
/*  69 */       if ((toActivity.condition != null) && (toActivity.condition.trim().length() > 0))
/*     */       {
/*     */         try {
/*  72 */           Interpreter bsh = new Interpreter();
/*  73 */           if (attributes != null) {
/*  74 */             Iterator attributesi = attributes.keySet().iterator();
/*  75 */             while (attributesi.hasNext()) {
/*  76 */               String attributeId = attributesi.next().toString();
/*  77 */               Object attributeValue = attributes.get(attributeId);
/*  78 */               bsh.set(attributeId, attributeValue);
/*     */             }
/*     */           }
/*  81 */           Boolean exp = (Boolean)bsh.eval(toActivity.condition);
/*  82 */           if (exp.booleanValue()) {
/*  83 */             ActivityXPDL activityRuntime = toActivity.activityRuntime;
/*  84 */             if (activityRuntime.getStartMode().toString().equals(AutomationMode.AUTOMATIC.toString()))
/*     */             {
/*  86 */               return activityRuntime.getToActivityId(attributes);
/*     */             }
/*     */ 
/*  89 */             return toActivity.activityRuntime.getId();
/*     */           }
/*     */         }
/*     */         catch (Exception ex) {
/*  93 */           System.out.println(toActivityId + ":condition[" + toActivity.condition + "]");
/*  94 */           ex.printStackTrace();
/*     */         }
/*     */       }
/*     */       else {
/*  98 */         ActivityXPDL activityRuntime = toActivity.activityRuntime;
/*  99 */         if (activityRuntime.getStartMode().toString().equals(AutomationMode.AUTOMATIC.toString()))
/*     */         {
/* 101 */           return activityRuntime.getToActivityId(null);
/*     */         }
/*     */ 
/* 104 */         return toActivity.activityRuntime.getId();
/*     */       }
/*     */     }
/*     */ 
/* 108 */     return null;
/*     */   }
/*     */   class ToActivity { private ActivityXPDL activityRuntime;
/*     */     private String condition;
/*     */ 
/* 115 */     ToActivity(ActivityXPDL activityRuntime, String condition) { this.activityRuntime = activityRuntime;
/* 116 */       this.condition = condition;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.ActivityXPDL
 * JD-Core Version:    0.6.0
 */