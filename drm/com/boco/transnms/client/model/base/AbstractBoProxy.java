/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractBoProxy
/*     */   implements IBoProxy
/*     */ {
/*     */   private String boName;
/*     */ 
/*     */   public void setBoName(String _boName)
/*     */   {
/*  45 */     this.boName = _boName;
/*     */   }
/*     */ 
/*     */   public String getBoName()
/*     */   {
/*  54 */     return this.boName;
/*     */   }
/*     */ 
/*     */   public Object exec(IBoCommand cmd)
/*     */     throws Exception
/*     */   {
/*  67 */     Object boProxy = getBoProxy();
/*  68 */     String methodName = cmd.getCmdContext().getMethodName();
/*  69 */     Method method = null;
/*  70 */     Class[] ivkParams = parseParaTypes(cmd);
/*     */     try {
/*  72 */       method = boProxy.getClass().getMethod(cmd.getCmdContext().getMethodName(), parseParaTypes(cmd));
/*     */     } catch (Exception ex) {
/*  74 */       LogHome.getLog().error("调用方法不存在：" + cmd.toString() + ",parms=" + ivkParams);
/*  75 */       Method[] methods = boProxy.getClass().getMethods();
/*  76 */       for (int i = 0; i < methods.length; i++) {
/*  77 */         Method m = methods[i];
/*  78 */         if (methodName.equals(m.getName())) {
/*  79 */           Class[] cls = m.getParameterTypes();
/*  80 */           LogHome.getLog().error("调用方法不存在，存在同名方法，但参数不匹配：" + cmd.toString() + ",parms=" + cls);
/*     */         }
/*     */       }
/*  83 */       throw ex;
/*     */     }
/*  85 */     return method.invoke(boProxy, cmd.getParas());
/*     */   }
/*     */ 
/*     */   protected Class[] parseParaTypes(IBoCommand cmd)
/*     */   {
/*  96 */     Class[] paraClasses = cmd.getCmdContext().getParaClassTypes();
/*  97 */     if (paraClasses == null) {
/*  98 */       paraClasses = new Class[cmd.getParas().length];
/*  99 */       for (int i = 0; i < cmd.getParas().length; i++) {
/* 100 */         if (cmd.getParas()[i] != null) {
/* 101 */           paraClasses[i] = cmd.getParas()[i].getClass();
/*     */         } else {
/* 103 */           String loc = cmd.getCmdContext().getBoName() + ", methodName=" + cmd.getCmdContext().getMethodName();
/*     */ 
/* 105 */           String info = "解析参数错误：boName=" + loc + "，第" + i + "个参数为null ！";
/* 106 */           throw new UserException(info);
/*     */         }
/*     */       }
/*     */     }
/* 110 */     return paraClasses;
/*     */   }
/*     */ 
/*     */   public String getIdentifier()
/*     */   {
/* 119 */     return "";
/*     */   }
/*     */ 
/*     */   protected abstract Object getBoProxy()
/*     */     throws Exception;
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.AbstractBoProxy
 * JD-Core Version:    0.6.0
 */