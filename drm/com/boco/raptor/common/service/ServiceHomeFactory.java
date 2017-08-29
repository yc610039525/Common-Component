/*    */ package com.boco.raptor.common.service;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ServiceHomeFactory
/*    */ {
/* 28 */   private static final ServiceHomeFactory instance = new ServiceHomeFactory();
/* 29 */   private final Map<String, IServiceHome> serviceHomes = new HashMap();
/* 30 */   private final Map<String, IService> services = new HashMap();
/*    */ 
/*    */   public static ServiceHomeFactory getInstance()
/*    */   {
/* 35 */     return instance;
/*    */   }
/*    */ 
/*    */   public <T> T getService(String serviceId) {
/* 39 */     return this.services.get(serviceId);
/*    */   }
/*    */ 
/*    */   public void setServiceHomes(List<IServiceHome> serviceHomes) throws Exception {
/* 43 */     for (IServiceHome serviceHome : serviceHomes)
/* 44 */       addServiceHome(serviceHome);
/*    */   }
/*    */ 
/*    */   public void initServiceHome()
/*    */   {
/* 49 */     for (IServiceHome serviceHome : this.serviceHomes.values())
/*    */       try {
/* 51 */         serviceHome.initServices();
/*    */       } catch (Exception ex) {
/* 53 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */   }
/*    */ 
/*    */   public void addServiceHome(IServiceHome serviceHome) throws UserException
/*    */   {
/* 59 */     checkServiceHome(serviceHome);
/* 60 */     this.serviceHomes.put(serviceHome.getServiceHomeId(), serviceHome);
/* 61 */     String[] serviceIds = serviceHome.getServiceIds();
/* 62 */     for (String serviceId : serviceIds) {
/* 63 */       if ((serviceId == null) || (serviceId.trim().length() == 0)) {
/* 64 */         throw new UserException("serviceId: " + serviceId + ", 无效 !");
/*    */       }
/* 66 */       if (this.services.containsKey(serviceId)) {
/* 67 */         LogHome.getLog().warn("serviceId: " + serviceId + "， 加载重复 ！");
/*    */       }
/* 69 */       this.services.put(serviceId, serviceHome.getService(serviceId));
/*    */     }
/*    */   }
/*    */ 
/*    */   public void deleteServiceHome(String serviceHomeId) {
/* 74 */     IServiceHome serviceHome = (IServiceHome)this.serviceHomes.remove(serviceHomeId);
/* 75 */     if (serviceHome != null) {
/* 76 */       String[] serviceIds = serviceHome.getServiceIds();
/* 77 */       for (String serviceId : serviceIds)
/* 78 */         this.services.remove(serviceId);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void checkServiceHome(IServiceHome serviceHome) throws UserException
/*    */   {
/* 84 */     String serviceHomeId = serviceHome.getServiceHomeId();
/* 85 */     if ((serviceHomeId == null) || (serviceHomeId.trim().length() == 0)) {
/* 86 */       throw new UserException("ServiceHomeId: " + serviceHomeId + ", 无效 !");
/*    */     }
/*    */ 
/* 89 */     if (this.serviceHomes.containsKey(serviceHomeId))
/* 90 */       throw new UserException("ServiceHomeId: " + serviceHomeId + ", 已经加载 !");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.ServiceHomeFactory
 * JD-Core Version:    0.6.0
 */