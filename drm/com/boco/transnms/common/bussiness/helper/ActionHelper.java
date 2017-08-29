/*     */ package com.boco.transnms.common.bussiness.helper;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.transnms.client.model.base.GenericBoCmd;
/*     */ import com.boco.transnms.client.model.base.IBoCommand;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ActionHelper
/*     */ {
/*     */   public static final String QUERY_METHOD_PREFIX = "get";
/*     */   public static final String ADD_METHOD_PREFIX = "add";
/*     */   public static final String MODIFY_METHOD_PREFIX = "modify";
/*     */   public static final String DELETE_METHOD_PREFIX = "del";
/*     */   public static final String IS_METHOD_PREFIX = "is";
/*     */   public static final String EDIT_METHOD_PREFIX = "edit";
/*     */ 
/*     */   public static String getActionName(String boName, String methodName)
/*     */   {
/*  42 */     String actionName = "";
/*  43 */     if ((boName != null) && (methodName != null)) {
/*  44 */       actionName = boName + "." + methodName;
/*     */     }
/*  46 */     return actionName;
/*     */   }
/*     */ 
/*     */   public static BoCmdContext getBoCmdContext(String boProxyName, String actionName) {
/*  50 */     BoCmdContext context = getBoCmdContext(actionName);
/*  51 */     context.setBoProxyName(boProxyName);
/*  52 */     return context;
/*     */   }
/*     */ 
/*     */   public static BoCmdContext getBoCmdContext(String actionName) {
/*  56 */     if (actionName == null) return new BoCmdContext();
/*  57 */     actionName = actionName.replace('.', '-');
/*  58 */     String[] actions = actionName.split("-");
/*  59 */     if (actions.length < 2) {
/*  60 */       LogHome.getLog().info("actionName=" + actionName + "格式无效 ！");
/*  61 */       throw new UserException("actionName=" + actionName + "格式无效 ！");
/*     */     }
/*  63 */     BoCmdContext context = new BoCmdContext(actions[0], actions[1]);
/*     */ 
/*  65 */     return context;
/*     */   }
/*     */ 
/*     */   public static String getBoName(String actionName) {
/*  69 */     BoCmdContext context = getBoCmdContext(actionName);
/*  70 */     return context.getBoName();
/*     */   }
/*     */ 
/*     */   public static String getMethodName(String actionName) {
/*  74 */     BoCmdContext context = getBoCmdContext(actionName);
/*  75 */     return context.getMethodName();
/*     */   }
/*     */ 
/*     */   public static List<String> decomposeActionName(String actionName) {
/*  79 */     List actionNames = new ArrayList();
/*  80 */     if ((actionName == null) || (actionName.trim().length() == 0)) return actionNames;
/*     */ 
/*  82 */     String geneActionName = getBoName(actionName) + ".";
/*  83 */     String methodName = getMethodName(actionName);
/*  84 */     if (methodName.indexOf("edit") == 0) {
/*  85 */       actionNames.add(geneActionName + "get");
/*  86 */       actionNames.add(geneActionName + "add");
/*  87 */       actionNames.add(geneActionName + "modify");
/*  88 */       actionNames.add(geneActionName + "del");
/*     */     } else {
/*  90 */       actionNames.add(actionName);
/*     */     }
/*  92 */     return actionNames;
/*     */   }
/*     */ 
/*     */   public static String getGenericActionName(String actionName) {
/*  96 */     String geneActionName = getBoName(actionName) + ".";
/*  97 */     String methodName = getMethodName(actionName);
/*  98 */     if (methodName.indexOf("get") == 0) {
/*  99 */       geneActionName = geneActionName + "get";
/* 100 */     } else if (methodName.indexOf("is") == 0) {
/* 101 */       geneActionName = geneActionName + "get";
/* 102 */     } else if (methodName.indexOf("add") == 0) {
/* 103 */       geneActionName = geneActionName + "add";
/* 104 */     } else if (methodName.indexOf("modify") == 0) {
/* 105 */       geneActionName = geneActionName + "modify";
/* 106 */     } else if (methodName.indexOf("del") == 0) {
/* 107 */       geneActionName = geneActionName + "del";
/*     */     } else {
/* 109 */       LogHome.getLog().warn("命名不符合规范: actionName=" + actionName);
/* 110 */       geneActionName = geneActionName + "get";
/*     */     }
/* 112 */     return geneActionName;
/*     */   }
/*     */ 
/*     */   public static IBoActionContext getCmdActionContext(IBoCommand cmd) {
/* 116 */     IBoActionContext context = null;
/* 117 */     Object[] paras = cmd.getParas();
/* 118 */     if ((paras != null) && (paras.length > 0) && 
/* 119 */       ((paras[0] instanceof IBoActionContext))) {
/* 120 */       context = (IBoActionContext)paras[0];
/*     */     }
/*     */ 
/* 123 */     return context;
/*     */   }
/*     */   public static boolean isPrintActionLog(GenericBoCmd cmd) {
/* 126 */     boolean isprint = true;
/*     */     try {
/* 128 */       if ((cmd != null) && (cmd.getActionContext() != null)) {
/* 129 */         String actionName = cmd.getActionContext().getActionName();
/* 130 */         isprint = isPrintActionLog(actionName);
/*     */       }
/*     */     } catch (Exception ex) {
/*     */     }
/* 134 */     return isprint;
/*     */   }
/*     */ 
/*     */   public static boolean isPrintActionLog(String boName, String methodName) {
/* 138 */     String actionName = getActionName(boName, methodName);
/* 139 */     return isPrintActionLog(actionName);
/*     */   }
/*     */ 
/*     */   public static boolean isPrintActionLog(String actionName) {
/* 143 */     boolean isPrintActionLog = true;
/* 144 */     if ((actionName != null) && ((actionName.equals("XrpcMsgServiceBO.getMessages")) || (actionName.equals("CommonBO.getPing")) || (actionName.equals("SystemManageBO.modifyDevInfo")) || (actionName.equals("SystemManageBO.getServerState")) || (actionName.equals("SystemManageBO.getAllDevStates")) || (actionName.equals("IXrpcMsgServiceBO.getMessages")) || (actionName.equals("ICommonBO.getPing")) || (actionName.equals("ISystemManageBO.modifyDevInfo")) || (actionName.equals("ISystemManageBO.getServerState")) || (actionName.equals("ISystemManageBO.getAllDevStates")) || (actionName.equals("ISystemDevManageBO.getServerAddress"))))
/*     */     {
/* 155 */       isPrintActionLog = false;
/*     */     }
/* 157 */     return isPrintActionLog;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.bussiness.helper.ActionHelper
 * JD-Core Version:    0.6.0
 */