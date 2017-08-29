/*     */ package com.boco.raptor.common.service;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.misc.RaptorSystem;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*     */ 
/*     */ public abstract class AbstractServiceHome
/*     */   implements IServiceHome
/*     */ {
/*     */   private String serviceHomeId;
/*  34 */   private List<String> serviceIdList = new ArrayList();
/*  35 */   protected final Map<String, IService> services = new HashMap();
/*  36 */   private Map<String, IServiceInterceptor> interceptorTemplates = new HashMap();
/*     */ 
/*     */   public AbstractServiceHome(String serviceHomeId) {
/*  39 */     this.serviceHomeId = serviceHomeId;
/*     */   }
/*     */ 
/*     */   public void initServices() throws Exception {
/*  43 */     if (isLocalServiceHome())
/*  44 */       for (String serviceId : this.serviceIdList)
/*  45 */         getService(serviceId).initService();
/*     */   }
/*     */ 
/*     */   public IService getService(String serviceId)
/*     */   {
/*  51 */     IService service = (IService)this.services.get(serviceId);
/*  52 */     IServiceInterceptor interceptorTemplate = (IServiceInterceptor)this.interceptorTemplates.get(serviceId);
/*  53 */     if ((service != null) && (interceptorTemplate != null)) {
/*  54 */       IServiceInterceptor interceptor = interceptorTemplate.clone();
/*  55 */       interceptor.setSerivce(service);
/*  56 */       Class serviceClass = service.getClass();
/*  57 */       service = (IService)Proxy.newProxyInstance(serviceClass.getClassLoader(), serviceClass.getInterfaces(), interceptor);
/*     */     }
/*     */ 
/*  60 */     return service;
/*     */   }
/*     */ 
/*     */   public void addService(IService service) throws Exception {
/*  64 */     checkService(service);
/*  65 */     this.serviceIdList.add(service.getServiceId());
/*  66 */     this.services.put(service.getServiceId(), service);
/*     */   }
/*     */ 
/*     */   public void deleteService(String serviceId) {
/*  70 */     this.services.remove(serviceId);
/*     */   }
/*     */ 
/*     */   public String[] getServiceIds() {
/*  74 */     String[] serviceIds = new String[this.services.size()];
/*  75 */     this.serviceIdList.toArray(serviceIds);
/*  76 */     return serviceIds;
/*     */   }
/*     */ 
/*     */   public String getServiceHomeId() {
/*  80 */     return this.serviceHomeId;
/*     */   }
/*     */ 
/*     */   public void setServiceHomeId(String serviceHomeId) {
/*  84 */     this.serviceHomeId = serviceHomeId;
/*     */   }
/*     */ 
/*     */   public void setServiceInterceptor(IServiceInterceptor interceptor) throws UserException {
/*  88 */     List serviceIds = interceptor.getInterceptServiceIds();
/*  89 */     for (String serviceId : serviceIds) {
/*  90 */       if (this.interceptorTemplates.containsKey(serviceId)) {
/*  91 */         LogHome.getLog().warn("serviceId：" + serviceId + "，服务已经配置了拦截器!");
/*     */       }
/*  93 */       this.interceptorTemplates.put(serviceId, interceptor);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setServiceInterceptors(List<IServiceInterceptor> interceptors) {
/*  98 */     for (IServiceInterceptor interceptor : interceptors)
/*  99 */       setServiceInterceptor(interceptor);
/*     */   }
/*     */ 
/*     */   private void checkService(IService service) throws UserException
/*     */   {
/* 104 */     String serviceId = service.getServiceId();
/* 105 */     if ((serviceId == null) || (serviceId.trim().length() == 0)) {
/* 106 */       throw new UserException("serviceId: " + serviceId + ", 无效 !");
/*     */     }
/*     */ 
/* 109 */     if (this.services.containsKey(serviceId))
/* 110 */       throw new UserException("serviceId: " + serviceId + ", 已经加载 !");
/*     */   }
/*     */ 
/*     */   public void setServiceFiles(String _serviceFilePaths) throws Exception
/*     */   {
/* 115 */     String[] serviceFilePaths = _serviceFilePaths.split(",");
/* 116 */     for (int i = 0; i < serviceFilePaths.length; i++) {
/* 117 */       String raptorHome = RaptorSystem.getRaptorHomePath();
/* 118 */       serviceFilePaths[i] = (raptorHome + serviceFilePaths[i].trim());
/*     */     }
/* 120 */     FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(serviceFilePaths);
/* 121 */     String[] serviceIds = context.getBeanDefinitionNames();
/* 122 */     for (String serviceId : serviceIds) {
/* 123 */       Object _service = context.getBean(serviceId);
/* 124 */       if ((_service instanceof IService))
/* 125 */         addService((IService)_service);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.AbstractServiceHome
 * JD-Core Version:    0.6.0
 */