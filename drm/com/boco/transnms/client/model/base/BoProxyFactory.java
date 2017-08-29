/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.bo.ibo.core.IBoManageService;
/*     */ import com.boco.raptor.cfg.StartUpEnv;
/*     */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*     */ import com.boco.transnms.server.bo.base.IBoHome;
/*     */ import com.boco.transnms.server.bo.base.IBusinessObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.context.support.ClassPathXmlApplicationContext;
/*     */ 
/*     */ public final class BoProxyFactory
/*     */ {
/*  42 */   private static BoProxyFactory instance = new BoProxyFactory();
/*     */ 
/*  45 */   private final Map<String, Map> boProxyManagerMap = new HashMap();
/*  46 */   private List<IBoProxyManager> proxyManagers = new ArrayList();
/*  47 */   private String defaultProxyManager = "TnmsXrpcProxy";
/*     */ 
/*     */   public BoProxyFactory loadSpringBeanFiles(String[] springXmlNames)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  71 */       ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(springXmlNames);
/*     */ 
/*  73 */       initServerBoProxy();
/*  74 */       initClientBoProxy();
/*     */     } catch (Exception ex) {
/*  76 */       LogHome.getLog().error("初始化业务代理工厂失败:", ex);
/*  77 */       System.exit(0);
/*     */     }
/*  79 */     return instance;
/*     */   }
/*     */ 
/*     */   public BoProxyFactory loadSpringBeanFiles(String[] springXmlNames, boolean isSetBoProxyMap)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  96 */       ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(springXmlNames);
/*     */ 
/*  98 */       initServerBoProxy();
/*     */     } catch (Exception ex) {
/* 100 */       ex.printStackTrace();
/*     */     }
/* 102 */     return instance;
/*     */   }
/*     */ 
/*     */   public static BoProxyFactory getInstance()
/*     */   {
/* 111 */     return instance;
/*     */   }
/*     */ 
/*     */   public void setXrpcProxyManagers(List<IBoProxyManager> _proxyManagers)
/*     */   {
/* 121 */     this.proxyManagers.addAll(_proxyManagers);
/* 122 */     setProxyManagers(this.proxyManagers);
/*     */   }
/*     */ 
/*     */   public void setHttpProxyManagers(List<IBoProxyManager> _proxyManagers) {
/* 126 */     this.proxyManagers.addAll(_proxyManagers);
/* 127 */     setProxyManagers(this.proxyManagers);
/*     */   }
/*     */ 
/*     */   public void setEjbProxyManagers(List<IBoProxyManager> ejbProxyManagers)
/*     */   {
/* 137 */     setProxyManagers(ejbProxyManagers);
/*     */   }
/*     */ 
/*     */   public void setWsProxyManagers(List<IBoProxyManager> wsProxyManagers)
/*     */   {
/* 147 */     setProxyManagers(wsProxyManagers);
/*     */   }
/*     */ 
/*     */   private void setProxyManagers(List<IBoProxyManager> _proxyManagers)
/*     */   {
/* 157 */     for (int i = 0; (_proxyManagers != null) && (i < _proxyManagers.size()); i++) {
/* 158 */       IBoProxyManager proxyManager = (IBoProxyManager)_proxyManagers.get(i);
/* 159 */       Map proxyMap = new HashMap();
/* 160 */       this.boProxyManagerMap.put(proxyManager.getBoProxyName(), proxyMap);
/* 161 */       proxyManager.prepareBoProxy();
/* 162 */       String[] boNames = proxyManager.getBoNames();
/* 163 */       for (int j = 0; j < boNames.length; j++) {
/* 164 */         IBoProxy boProxy = proxyManager.getBoProxy(boNames[j]);
/*     */ 
/* 168 */         proxyMap.put(boNames[j], boProxy);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private IBoProxy getBoProxy(String boName, String url) {
/* 173 */     IBoProxyManager proxyManager = null;
/* 174 */     for (int i = 0; (this.proxyManagers != null) && (i < this.proxyManagers.size()); i++) {
/* 175 */       proxyManager = (IBoProxyManager)this.proxyManagers.get(i);
/* 176 */       if (proxyManager.getBoProxyName().equals(this.defaultProxyManager)) {
/*     */         break;
/*     */       }
/*     */     }
/* 180 */     if (this.proxyManagers != null) {
/* 181 */       IBoProxy boProxy = proxyManager.getBoProxy(boName, url);
/* 182 */       return boProxy;
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public void initClientBoProxy()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 208 */       if (!StartUpEnv.isRunServer()) {
/* 209 */         Map boServerProxys = (Map)this.boProxyManagerMap.get(this.defaultProxyManager);
/*     */ 
/* 211 */         IBoManageService bo = (IBoManageService)BoHomeFactory.getInstance().getBO(IBoManageService.class);
/*     */ 
/* 213 */         Map importBoNames = ImportBoManager.getInstance().getImportBoNames();
/* 214 */         Map boProxyUrls = bo.getBoProxyUrls(importBoNames);
/* 215 */         Map boRemoteProxyUrls = bo.getBoRemoteProxyUrls(importBoNames);
/*     */ 
/* 217 */         Iterator it = boProxyUrls.keySet().iterator();
/* 218 */         while (it.hasNext()) {
/* 219 */           String _boName = (String)it.next();
/* 220 */           if (!_boName.equals("IBoManageService")) {
/* 221 */             String url = (String)boProxyUrls.get(_boName);
/* 222 */             if (HttpProxyManager.getInstance().isDirectProxy())
/* 223 */               url = (String)boRemoteProxyUrls.get(_boName);
/* 224 */             else if (HttpProxyManager.getInstance().isHaveHttpProxy()) {
/* 225 */               url = HttpProxyManager.getInstance().getProxyXrpcUrl(url);
/*     */             }
/*     */ 
/* 228 */             if (url != null)
/* 229 */               boServerProxys.put(_boName, getBoProxy(_boName, url));
/*     */             else
/* 231 */               LogHome.getLog().error("BO远程服务未配置URL[boName=" + _boName + "]，启用本地调用 ！");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 238 */       throw new Exception("初始化客户端业务对象代理失败，请检查import-bo.xml,并确认服务器正常提供Xrpc服务！");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initServerBoProxy()
/*     */   {
/* 246 */     if ((!StartUpEnv.isRunServer()) || (StartUpEnv.isAllInOneServer())) {
/* 247 */       return;
/*     */     }
/* 249 */     Map boServerProxys = (Map)this.boProxyManagerMap.get(this.defaultProxyManager);
/* 250 */     Map boProxyUrls = new HashMap();
/* 251 */     IBoHome boHome = BoHomeFactory.getInstance().getBoHome("TRANSNMS_CONTEXT");
/* 252 */     String[] boNames = boHome.getBoNames();
/* 253 */     for (String boName : boNames) {
/* 254 */       IBusinessObject bo = boHome.getBO(boName);
/* 255 */       if ((!bo.isRunOnLocalServer()) && (!bo.isStateLess())) {
/* 256 */         String url = BoServerUrlManager.getInstance().getBoServerUrl(bo.getServerName());
/* 257 */         boProxyUrls.put(boName, url);
/*     */       }
/*     */     }
/* 260 */     Iterator it = boProxyUrls.keySet().iterator();
/* 261 */     while (it.hasNext()) {
/* 262 */       String _boName = (String)it.next();
/* 263 */       if (!_boName.equals("IBoManageService")) {
/* 264 */         String url = (String)boProxyUrls.get(_boName);
/* 265 */         if (url != null)
/* 266 */           boServerProxys.put(_boName, getBoProxy(_boName, url));
/*     */         else
/* 268 */           LogHome.getLog().error("BO远程服务未配置URL[boName=" + _boName + "]，启用本地调用 ！");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public IBoProxy createBoProxy(String boName)
/*     */   {
/* 282 */     Map boProxyMap = (Map)this.boProxyManagerMap.get(this.defaultProxyManager);
/* 283 */     IBoProxy boProxy = null;
/* 284 */     if (boProxyMap != null) {
/* 285 */       boProxy = (IBoProxy)boProxyMap.get(boName);
/*     */     }
/* 287 */     if (boProxy == null)
/*     */     {
/* 290 */       boProxy = new SpringBoProxy(boName);
/*     */     }
/*     */ 
/* 293 */     return boProxy;
/*     */   }
/*     */ 
/*     */   public IBoProxy createBoProxyByName(String boProxyName, String boName)
/*     */   {
/* 306 */     Map proxyMap = (Map)this.boProxyManagerMap.get(boProxyName);
/* 307 */     IBoProxy boProxy = null;
/* 308 */     if (proxyMap != null) {
/* 309 */       boProxy = (IBoProxy)proxyMap.get(boName);
/*     */     }
/* 311 */     if (boProxy == null) {
/* 312 */       boProxy = new SpringBoProxy(boName);
/*     */     }
/* 314 */     return boProxy;
/*     */   }
/*     */ 
/*     */   public boolean isHaveRemoteBoProxy(String boName)
/*     */   {
/* 325 */     Iterator iterator = this.boProxyManagerMap.values().iterator();
/* 326 */     while (iterator.hasNext()) {
/* 327 */       Map boProxyMap = (Map)iterator.next();
/* 328 */       if (boProxyMap.containsKey(boName)) {
/* 329 */         return true;
/*     */       }
/*     */     }
/* 332 */     return ImportBoManager.getInstance().contains(boName);
/*     */   }
/*     */ 
/*     */   public String getDefaultProxyManager()
/*     */   {
/* 341 */     return this.defaultProxyManager;
/*     */   }
/*     */ 
/*     */   public void setDefaultProxyManager(String defaultProxyManager) {
/* 345 */     this.defaultProxyManager = defaultProxyManager;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.BoProxyFactory
 * JD-Core Version:    0.6.0
 */