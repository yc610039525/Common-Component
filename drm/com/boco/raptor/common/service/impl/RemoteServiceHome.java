/*     */ package com.boco.raptor.common.service.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.AbstractServiceHome;
/*     */ import com.boco.raptor.common.service.IService;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.xmlrpc.CommonsXmlRpcTransportFactory;
/*     */ import org.apache.xmlrpc.XmlRpcClient;
/*     */ 
/*     */ public class RemoteServiceHome extends AbstractServiceHome
/*     */ {
/*     */   private static final String DEFAULT_PROXY_NAME = "DEFAULT_PROXY";
/*     */   private static final String XRPC_PREFIX = "DRM";
/*  42 */   private Map<String, URL> urls = new HashMap();
/*  43 */   private static int timeout = 7200000;
/*  44 */   private static int conntimeout = 3600000;
/*     */ 
/*     */   public RemoteServiceHome(String serviceHomeId) {
/*  47 */     super(serviceHomeId);
/*     */   }
/*     */ 
/*     */   public IService getService(String serviceId) {
/*  51 */     IService service = (IService)this.services.get(serviceId);
/*  52 */     if (service != null) {
/*  53 */       Class serviceClass = service.getClass();
/*  54 */       RemoteInterceptor interceptor = new RemoteInterceptor(serviceId);
/*  55 */       service = (IService)Proxy.newProxyInstance(serviceClass.getClassLoader(), serviceClass.getInterfaces(), interceptor);
/*     */     }
/*     */ 
/*  58 */     return service;
/*     */   }
/*     */ 
/*     */   public boolean isLocalServiceHome() {
/*  62 */     return false;
/*     */   }
/*     */ 
/*     */   public void setProxyUrls(Map<String, String> _urls)
/*     */   {
/* 124 */     for (String proxyName : _urls.keySet())
/*     */       try {
/* 126 */         this.urls.put(proxyName, new URL((String)_urls.get(proxyName)));
/*     */       } catch (Exception ex) {
/* 128 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public class RemoteInterceptor
/*     */     implements InvocationHandler
/*     */   {
/*     */     private String serviceId;
/*     */ 
/*     */     public RemoteInterceptor(String serviceId)
/*     */     {
/*  69 */       this.serviceId = serviceId;
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  73 */       if (RemoteServiceHome.this.urls.size() == 0) {
/*  74 */         throw new UserException("没有配置有效的服务URL !");
/*     */       }
/*     */ 
/*  77 */       if (args.length == 0) {
/*  78 */         throw new UserException("服务调用参数必须有 !");
/*     */       }
/*     */ 
/*  81 */       if (args[0] == null) {
/*  82 */         throw new UserException("服务调用参数ActionContext为空 !");
/*     */       }
/*     */ 
/*  85 */       if (!(args[0] instanceof IServiceActionContext)) {
/*  86 */         throw new UserException("服务调用参数第一个参数不是ActionContext !");
/*     */       }
/*     */ 
/*  89 */       IServiceActionContext actionContext = (IServiceActionContext)args[0];
/*  90 */       actionContext.setServiceId(this.serviceId);
/*  91 */       actionContext.setActionId(this.serviceId + "." + method.getName());
/*  92 */       ServiceInvokeEntity entity = new ServiceInvokeEntity(actionContext, method.getParameterTypes(), args);
/*  93 */       URL url = null;
/*  94 */       String proxyName = "DEFAULT_PROXY";
/*  95 */       if (actionContext.getProxyName() != null) {
/*  96 */         proxyName = actionContext.getProxyName();
/*     */       }
/*  98 */       url = (URL)RemoteServiceHome.this.urls.get(proxyName);
/*  99 */       if (url == null) {
/* 100 */         throw new UserException("服务调用代理名称错误, proxyName=" + proxyName);
/*     */       }
/* 102 */       return exec(url, entity);
/*     */     }
/*     */ 
/*     */     public Object exec(URL url, ServiceInvokeEntity entity) throws Throwable {
/* 106 */       CommonsXmlRpcTransportFactory transportFactory = new CommonsXmlRpcTransportFactory(url);
/* 107 */       transportFactory.setConnectionTimeout(RemoteServiceHome.conntimeout);
/* 108 */       transportFactory.setTimeout(RemoteServiceHome.timeout);
/* 109 */       XmlRpcClient client = new XmlRpcClient(url);
/* 110 */       byte[] bytes = entity.toBytes();
/* 111 */       Vector paras = new Vector();
/* 112 */       paras.add(bytes);
/* 113 */       byte[] result = (byte[])(byte[])client.execute("DRM.invoke", paras);
/* 114 */       ServiceInvokeEntity resEntity = ServiceInvokeEntity.fromBytes(result);
/* 115 */       if (resEntity.getException() != null) {
/* 116 */         throw resEntity.getException();
/*     */       }
/* 118 */       return resEntity.getResult();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.impl.RemoteServiceHome
 * JD-Core Version:    0.6.0
 */