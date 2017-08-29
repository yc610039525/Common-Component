/*     */ package com.boco.transnms.server.bo.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.bo.base.rpc._BoRpcInterceptor;
/*     */ import com.boco.transnms.client.model.base.BoCmdFactory;
/*     */ import com.boco.transnms.client.model.base.BoProxyFactory;
/*     */ import com.boco.transnms.client.model.base.IBoProxy;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BoHomeFactory
/*     */ {
/*  40 */   private static BoHomeFactory instance = new BoHomeFactory();
/*  41 */   private final Map<String, IBoHome> boHomeMap = new HashMap();
/*  42 */   private final Map<String, String> contextMap = new HashMap();
/*  43 */   private final Map<String, Class[]> boClassMap = new Hashtable();
/*  44 */   private boolean isInitBoHome = true;
/*     */ 
/*     */   public static BoHomeFactory getInstance()
/*     */   {
/*  58 */     return instance;
/*     */   }
/*     */ 
/*     */   public boolean isIsInitBoHome()
/*     */   {
/*  67 */     return this.isInitBoHome;
/*     */   }
/*     */ 
/*     */   public IBoHome createBoHome(String contextName, List<IBusinessObject> boList)
/*     */   {
/* 141 */     IBoHome boHome = new DynamicBoHome(boList);
/* 142 */     for (int i = 0; i < boList.size(); i++) {
/* 143 */       this.contextMap.put(((IBusinessObject)boList.get(i)).getBoName(), contextName);
/*     */     }
/* 145 */     this.boHomeMap.put(contextName, boHome);
/*     */ 
/* 147 */     if (this.isInitBoHome) {
/* 148 */       initBoHome(contextName);
/*     */     }
/* 150 */     return boHome;
/*     */   }
/*     */ 
/*     */   public IBoHome createBeanBoHome(String contextName, String[] boXmlFiles)
/*     */     throws Exception
/*     */   {
/* 165 */     IBoHome boHome = new SpringBeanBoHome(boXmlFiles);
/* 166 */     String[] boNames = boHome.getBoNames();
/* 167 */     for (int i = 0; i < boNames.length; i++) {
/* 168 */       this.contextMap.put(boNames[i], contextName);
/*     */     }
/* 170 */     this.boHomeMap.put(contextName, boHome);
/* 171 */     if (this.isInitBoHome) {
/* 172 */       initBoHome(contextName);
/*     */     }
/* 174 */     return boHome;
/*     */   }
/*     */ 
/*     */   public IBoHome createBoHome(String contextName, String[] boXmlFiles)
/*     */     throws Exception
/*     */   {
/* 189 */     IBoHome boHome = new SpringBoHome(boXmlFiles);
/* 190 */     String[] boNames = boHome.getBoNames();
/* 191 */     for (int i = 0; i < boNames.length; i++) {
/* 192 */       this.contextMap.put(boNames[i], contextName);
/*     */     }
/* 194 */     this.boHomeMap.put(contextName, boHome);
/* 195 */     if (this.isInitBoHome) {
/* 196 */       initBoHome(contextName);
/*     */     }
/* 198 */     return boHome;
/*     */   }
/*     */ 
/*     */   public void initBoHome(String contextName)
/*     */   {
/* 208 */     IBoHome boHome = (IBoHome)this.boHomeMap.get(contextName);
/* 209 */     if (boHome == null) {
/* 210 */       throw new UserException("没有注册 contextName=" + contextName);
/*     */     }
/* 212 */     String[] boNames = boHome.getBoNames();
/* 213 */     for (int i = 0; i < boNames.length; i++) {
/* 214 */       IBusinessObject bo = null;
/*     */       try {
/* 216 */         bo = getLocalBO(boNames[i]);
/* 217 */         if (bo == null) {
/*     */           continue;
/*     */         }
/* 220 */         if ((bo instanceof AbstractBO)) {
/* 221 */           AbstractBO abstractBO = (AbstractBO)bo;
/*     */ 
/* 223 */           LogHome.getLog().info("初始化业务对象[" + boNames[i] + "] 开始");
/* 224 */           bo.initBO();
/* 225 */           LogHome.getLog().info("初始化业务对象[" + boNames[i] + "] 结束");
/*     */         }
/*     */       }
/*     */       catch (Throwable ex) {
/* 229 */         LogHome.getLog().error("初始化业务对象[" + boNames[i] + "] 异常！");
/* 230 */         LogHome.getLog().error("", ex);
/* 231 */         LogHome.getLog().info("初始化业务对象[" + boNames[i] + "] 结束");
/*     */       }
/*     */     }
/* 234 */     this.isInitBoHome = true;
/*     */   }
/*     */ 
/*     */   public IBoHome getBoHome(String contextName)
/*     */   {
/* 245 */     return (IBoHome)this.boHomeMap.get(contextName);
/*     */   }
/*     */ 
/*     */   public IBusinessObject getBO(String boName)
/*     */     throws UserException
/*     */   {
/* 258 */     IBusinessObject bo = getLocalBO(boName);
/* 259 */     if (bo == null) {
/* 260 */       bo = getRemoteBO(boName);
/*     */     }
/* 262 */     return bo;
/*     */   }
/*     */ 
/*     */   public <T extends IBusinessObject> T getBO(Class<T> ibo)
/*     */     throws UserException
/*     */   {
/* 277 */     IBusinessObject bo = getLocalBO(ibo.getSimpleName());
/* 278 */     if (bo == null) {
/* 279 */       bo = getRemoteBO(ibo);
/*     */     }
/* 281 */     return bo;
/*     */   }
/*     */ 
/*     */   private IBusinessObject getLocalBO(String boName)
/*     */     throws UserException
/*     */   {
/* 294 */     IBusinessObject bo = null;
/* 295 */     String contextName = (String)this.contextMap.get(boName);
/* 296 */     IBoHome boHome = getBoHome(contextName);
/* 297 */     if (boHome != null) {
/* 298 */       bo = boHome.getBO(boName);
/*     */     }
/* 300 */     if ((bo != null) && (bo.isCallByRemote())) {
/* 301 */       bo = null;
/*     */     }
/* 303 */     return bo;
/*     */   }
/*     */ 
/*     */   private <T extends IBusinessObject> T getRemoteBO(Class<T> ibo)
/*     */     throws UserException
/*     */   {
/* 318 */     IBusinessObject bo = null;
/* 319 */     String boName = ibo.getSimpleName();
/*     */     try {
/* 321 */       Class[] iboClassTypes = (Class[])this.boClassMap.get(boName);
/* 322 */       if (iboClassTypes == null)
/*     */       {
/* 324 */         if (!BoProxyFactory.getInstance().isHaveRemoteBoProxy(boName)) {
/* 325 */           throw new UserException("没有配置BO[" + boName + "]的远程代理，也没有配置为本地BO，无法调用, 请检查bo-*.xml和import-bo.xml,并确认服务器正常提供Xrpc服务！");
/*     */         }
/* 327 */         iboClassTypes = new Class[1];
/* 328 */         iboClassTypes[0] = ibo;
/* 329 */         this.boClassMap.put(boName, iboClassTypes);
/*     */       }
/* 331 */       _BoRpcInterceptor boInterceptor = new _BoRpcInterceptor(boName);
/* 332 */       bo = (IBusinessObject)Proxy.newProxyInstance(BoActionContext.class.getClassLoader(), iboClassTypes, boInterceptor);
/*     */     } catch (Exception ex) {
/* 334 */       LogHome.getLog().debug("远程加载业务对象:" + boName + "，失败 !");
/* 335 */       throw new UserException(ex.getMessage());
/*     */     }
/* 337 */     return bo;
/*     */   }
/*     */ 
/*     */   private IBusinessObject getRemoteBO(String boName)
/*     */     throws UserException
/*     */   {
/* 350 */     IBusinessObject bo = null;
/*     */     try {
/* 352 */       boolean firstInvoke = false;
/* 353 */       Class[] iboClassTypes = (Class[])this.boClassMap.get(boName);
/* 354 */       if (iboClassTypes == null) {
/* 355 */         firstInvoke = true;
/*     */ 
/* 360 */         if (!BoProxyFactory.getInstance().isHaveRemoteBoProxy(boName)) {
/* 361 */           throw new UserException("没有配置BO[" + boName + "]的远程代理，也没有配置为本地BO，无法调用, 请检查bo-*.xml和import-bo.xml,并确认服务器正常提供Xrpc服务 ！");
/*     */         }
/*     */ 
/* 364 */         BoActionContext context = new BoActionContext();
/*     */ 
/* 366 */         IBoProxy boProxy = BoProxyFactory.getInstance().createBoProxy(boName);
/* 367 */         String boClassName = (String)BoCmdFactory.getInstance().execBoCmd(boProxy, "IBoManageService.getBoClassName", new Object[] { context, boName });
/* 368 */         if (boClassName != null) {
/* 369 */           String[] iboClassNames = boClassName.split(",");
/* 370 */           iboClassTypes = new Class[iboClassNames.length];
/* 371 */           for (int i = 0; i < iboClassNames.length; i++) {
/* 372 */             iboClassTypes[i] = Class.forName(iboClassNames[i]);
/*     */           }
/* 374 */           this.boClassMap.put(boName, iboClassTypes);
/*     */         }
/*     */       }
/* 377 */       if (iboClassTypes != null) {
/* 378 */         _BoRpcInterceptor boInterceptor = new _BoRpcInterceptor(boName);
/* 379 */         bo = (IBusinessObject)Proxy.newProxyInstance(BoActionContext.class.getClassLoader(), iboClassTypes, boInterceptor);
/*     */       }
/*     */ 
/* 382 */       if ((firstInvoke) && (bo != null))
/* 383 */         LogHome.getLog().info("获取业务对象实现:" + boName + "=" + bo);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 387 */       LogHome.getLog().error("远程加载业务对象:" + boName + "，失败 !");
/* 388 */       throw new UserException(ex.getMessage());
/*     */     }
/* 390 */     return bo;
/*     */   }
/*     */ 
/*     */   public String getBoClassName(String boName)
/*     */     throws UserException
/*     */   {
/* 403 */     String boClassName = null;
/* 404 */     String contextName = (String)this.contextMap.get(boName);
/* 405 */     IBoHome boHome = getBoHome(contextName);
/* 406 */     if (boHome != null)
/* 407 */       boClassName = boHome.getBoClassName(boName);
/*     */     else {
/* 409 */       throw new UserException("没有加载业务对象，boName=" + boName);
/*     */     }
/* 411 */     return boClassName;
/*     */   }
/*     */ 
/*     */   public void setIsInitBoHome(boolean _isInitBoHome)
/*     */   {
/* 421 */     this.isInitBoHome = _isInitBoHome;
/*     */   }
/*     */ 
/*     */   public String[] getAllBoNames()
/*     */   {
/* 430 */     String[] boNames = new String[this.contextMap.size()];
/* 431 */     this.contextMap.keySet().toArray(boNames);
/* 432 */     return boNames;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.BoHomeFactory
 * JD-Core Version:    0.6.0
 */