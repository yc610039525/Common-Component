/*     */ package com.boco.transnms.server.bo.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.server.common.cfg.TnmsServerName;
/*     */ import com.boco.transnms.server.dao.base.DaoHomeFactory;
/*     */ import com.boco.transnms.server.dao.base.IDataAccessObject;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractBO
/*     */   implements IBusinessObject
/*     */ {
/*     */   private static String localServerName;
/*     */   private String boName;
/*     */   private boolean loadDemon;
/*  29 */   private boolean stateLess = true;
/*  30 */   private String serverName = "";
/*     */   private boolean initByAllServer;
/*  32 */   private String initServerNames = "";
/*     */   private boolean stateBoInited;
/*     */ 
/*     */   public AbstractBO()
/*     */   {
/*  38 */     this.boName = getClass().getSimpleName();
/*     */     try {
/*  40 */       if (localServerName == null) {
/*  41 */         localServerName = TnmsServerName.getLocalServerNameStr();
/*  42 */         if (localServerName == null) {
/*  43 */           LogHome.getLog().error("全服务器方式运行，没有配置本地服务器名称: servername=" + this.serverName);
/*  44 */           localServerName = null;
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) {
/*  48 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public AbstractBO(String _boName)
/*     */   {
/*  56 */     this.boName = _boName;
/*     */   }
/*     */ 
/*     */   public String getBoName()
/*     */   {
/*  63 */     return this.boName;
/*     */   }
/*     */ 
/*     */   public void initBO()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected IBusinessObject getBO(String _boName)
/*     */   {
/*  78 */     return BoHomeFactory.getInstance().getBO(_boName);
/*     */   }
/*     */ 
/*     */   protected IDataAccessObject getDAO(String daoName)
/*     */   {
/*  87 */     return DaoHomeFactory.getInstance().getDAO(daoName);
/*     */   }
/*     */ 
/*     */   protected IBusinessObject getBO(Class clazz)
/*     */   {
/*  95 */     return BoHomeFactory.getInstance().getBO(clazz.getSimpleName());
/*     */   }
/*     */ 
/*     */   protected IDataAccessObject getDAO(Class clazz)
/*     */   {
/* 103 */     return DaoHomeFactory.getInstance().getDAO(clazz.getSimpleName());
/*     */   }
/*     */ 
/*     */   public boolean isLoadDemon()
/*     */   {
/* 110 */     return this.loadDemon;
/*     */   }
/*     */ 
/*     */   public boolean isInitByAllServer()
/*     */   {
/* 117 */     return this.initByAllServer;
/*     */   }
/*     */ 
/*     */   public boolean isInitByThisServer()
/*     */   {
/* 125 */     boolean isInit = false;
/* 126 */     if (isRunOnLocalServer())
/* 127 */       isInit = true;
/* 128 */     else if ((isStateLess()) && (isInitByAllServer()))
/* 129 */       isInit = true;
/* 130 */     else if (this.stateBoInited) {
/* 131 */       isInit = true;
/*     */     }
/* 133 */     return isInit;
/*     */   }
/*     */ 
/*     */   public boolean isCallByRemote()
/*     */   {
/* 140 */     return (!isStateLess()) && (!this.stateBoInited) && (!isRunOnLocalServer());
/*     */   }
/*     */ 
/*     */   public String getServerName()
/*     */   {
/* 147 */     return this.serverName;
/*     */   }
/*     */ 
/*     */   public boolean isStateLess()
/*     */   {
/* 154 */     return this.stateLess;
/*     */   }
/*     */ 
/*     */   public void setLoadDemon(boolean _loadDemon)
/*     */   {
/* 161 */     this.loadDemon = _loadDemon;
/*     */   }
/*     */ 
/*     */   public void setBoName(String _boName)
/*     */   {
/* 169 */     this.boName = _boName;
/*     */   }
/*     */ 
/*     */   public void setInitByAllServer(boolean _initByAllServer)
/*     */   {
/* 176 */     this.initByAllServer = _initByAllServer;
/*     */   }
/*     */ 
/*     */   public void setServerName(String _serverName)
/*     */   {
/* 183 */     this.serverName = _serverName;
/*     */   }
/*     */ 
/*     */   public void setStateLess(boolean _stateLess)
/*     */   {
/* 190 */     this.stateLess = _stateLess;
/*     */   }
/*     */ 
/*     */   public void setInitServerNames(String _initServerNames)
/*     */   {
/* 197 */     if (_initServerNames.indexOf("#" + localServerName + "#") >= 0)
/* 198 */       this.stateBoInited = true;
/*     */   }
/*     */ 
/*     */   public boolean isRunOnLocalServer()
/*     */   {
/* 206 */     if ((localServerName == null) || (localServerName.trim().length() == 0) || ("ALL".equals(localServerName))) {
/* 207 */       return true;
/*     */     }
/* 209 */     return localServerName.equals(this.serverName);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.AbstractBO
 * JD-Core Version:    0.6.0
 */