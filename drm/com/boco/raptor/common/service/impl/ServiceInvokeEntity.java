/*     */ package com.boco.raptor.common.service.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.ObjZipBufInputStream;
/*     */ import com.boco.common.util.io.ObjZipBufOutputStream;
/*     */ import com.boco.raptor.common.service.IService;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ServiceInvokeEntity
/*     */   implements Serializable
/*     */ {
/*     */   private IServiceActionContext actionContext;
/*     */   private Class[] paraClassTypes;
/*     */   private Object[] args;
/*  37 */   private Object result = null;
/*  38 */   private Throwable exception = null;
/*     */ 
/*     */   public ServiceInvokeEntity() {
/*     */   }
/*     */ 
/*     */   public ServiceInvokeEntity(IServiceActionContext actionContext, Class[] paraClassTypes, Object[] args) {
/*  44 */     this.actionContext = actionContext;
/*  45 */     this.paraClassTypes = paraClassTypes;
/*  46 */     this.args = args;
/*     */   }
/*     */ 
/*     */   public IServiceActionContext getActionContext() {
/*  50 */     return this.actionContext;
/*     */   }
/*     */ 
/*     */   public Class[] getParaClassTypes() {
/*  54 */     return this.paraClassTypes;
/*     */   }
/*     */ 
/*     */   public Object[] getArgs() {
/*  58 */     return this.args;
/*     */   }
/*     */ 
/*     */   public Throwable getException() {
/*  62 */     return this.exception;
/*     */   }
/*     */ 
/*     */   public Object getResult() {
/*  66 */     return this.result;
/*     */   }
/*     */ 
/*     */   public void setArgs(Object[] args) {
/*  70 */     this.args = args;
/*     */   }
/*     */ 
/*     */   public void setParaClassTypes(Class[] paraClassTypes) {
/*  74 */     this.paraClassTypes = paraClassTypes;
/*     */   }
/*     */ 
/*     */   public void setActionContext(IServiceActionContext actionContext) {
/*  78 */     this.actionContext = actionContext;
/*     */   }
/*     */ 
/*     */   public void setException(Throwable exception) {
/*  82 */     this.exception = exception;
/*     */   }
/*     */ 
/*     */   public void setResult(Object result) {
/*  86 */     this.result = result;
/*     */   }
/*     */ 
/*     */   public static ServiceInvokeEntity fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
/*  90 */     ServiceInvokeEntity entity = null;
/*  91 */     ObjZipBufInputStream in = new ObjZipBufInputStream(bytes);
/*  92 */     entity = (ServiceInvokeEntity)in.readObject();
/*  93 */     in.close();
/*  94 */     return entity;
/*     */   }
/*     */ 
/*     */   public byte[] toBytes() throws IOException {
/*  98 */     ObjZipBufOutputStream out = new ObjZipBufOutputStream();
/*  99 */     out = new ObjZipBufOutputStream();
/* 100 */     out.writeObject(this);
/* 101 */     out.close();
/* 102 */     return out.getBuf();
/*     */   }
/*     */ 
/*     */   public void invoke() {
/* 106 */     if (this.actionContext == null) {
/* 107 */       LogHome.getLog().error("调用的服务上下文为空");
/* 108 */       this.exception = new UserException("调用的服务上下文为空");
/* 109 */       return;
/*     */     }
/*     */ 
/* 112 */     IService service = (IService)ServiceHomeFactory.getInstance().getService(this.actionContext.getServiceId());
/* 113 */     if (service == null) {
/* 114 */       LogHome.getLog().error("未配置的服务，serviceId=" + this.actionContext.getServiceId());
/* 115 */       this.exception = new UserException("未配置的服务，serviceId=" + this.actionContext.getServiceId());
/* 116 */       return;
/*     */     }
/*     */ 
/* 119 */     Method method = null;
/*     */     try {
/* 121 */       String methodName = this.actionContext.getActionId();
/* 122 */       if (methodName.indexOf(".") >= 0) {
/* 123 */         methodName = methodName.substring(methodName.indexOf(".") + 1);
/*     */       }
/* 125 */       method = service.getClass().getMethod(methodName, this.paraClassTypes);
/*     */     } catch (Exception ex) {
/* 127 */       LogHome.getLog().error("错误的服务接口方法，serviceId=" + this.actionContext.getServiceId() + ", actionId=" + this.actionContext.getActionId());
/*     */ 
/* 129 */       this.exception = new UserException("错误的服务接口方法，serviceId=" + this.actionContext.getServiceId() + ", actionId=" + this.actionContext.getActionId());
/*     */ 
/* 131 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 135 */       this.result = method.invoke(service, this.args);
/*     */     } catch (InvocationTargetException ex) {
/* 137 */       this.exception = ex.getTargetException();
/*     */     } catch (Throwable ex) {
/* 139 */       LogHome.getLog().error("", ex);
/* 140 */       this.exception = ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearInvokeParas() {
/* 145 */     this.paraClassTypes = null;
/* 146 */     this.args = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.impl.ServiceInvokeEntity
 * JD-Core Version:    0.6.0
 */