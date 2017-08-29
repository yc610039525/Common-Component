/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public final class BoCmdFactory
/*     */ {
/*  35 */   private static BoCmdFactory instance = new BoCmdFactory();
/*  36 */   private final List<ICmdInterceptor> preInterceptors = new ArrayList();
/*  37 */   private final List<ICmdInterceptor> postInterceptors = new ArrayList();
/*  38 */   private Map<String, CmdStatInfo> boInvokeStatInfo = new ConcurrentHashMap();
/*  39 */   private long invokeCount = 0L;
/*     */ 
/*     */   public static BoCmdFactory getInstance()
/*     */   {
/*  52 */     return instance;
/*     */   }
/*     */ 
/*     */   public IBoCommand createBoCmd(String actionName, Object[] paras)
/*     */   {
/*  62 */     BoCmdContext context = ActionHelper.getBoCmdContext(actionName);
/*  63 */     return createBoCmd(context, paras);
/*     */   }
/*     */ 
/*     */   public IBoCommand createBoCmd(BoCmdContext boCmdContext, Object[] paras)
/*     */   {
/*  73 */     IBoProxy boProxy = null;
/*  74 */     String boProxyName = boCmdContext.getBoProxyName();
/*  75 */     if (boProxyName != null)
/*  76 */       boProxy = BoProxyFactory.getInstance().createBoProxyByName(boProxyName, boCmdContext.getBoName());
/*     */     else {
/*  78 */       boProxy = BoProxyFactory.getInstance().createBoProxy(boCmdContext.getBoName());
/*     */     }
/*  80 */     IBoCommand cmd = new GenericBoCmd(boProxy, boCmdContext, paras);
/*  81 */     cmd = new GenericBoCmdWrapper(this.preInterceptors, this.postInterceptors, cmd);
/*  82 */     return cmd;
/*     */   }
/*     */ 
/*     */   public IBoCommand createBoCmd(IBoProxy boProxy, BoCmdContext boCmdContext, Object[] paras)
/*     */   {
/*  93 */     IBoCommand cmd = new GenericBoCmd(boProxy, boCmdContext, paras);
/*  94 */     cmd = new GenericBoCmdWrapper(this.preInterceptors, this.postInterceptors, cmd);
/*  95 */     return cmd;
/*     */   }
/*     */ 
/*     */   public IBoCommand createXrpcBoCmd(String xrpcUrl, BoCmdContext boCmdContext, Object[] paras)
/*     */   {
/* 105 */     IBoProxy boProxy = new XrpcBoProxy(xrpcUrl);
/* 106 */     IBoCommand cmd = new GenericBoCmd(boProxy, boCmdContext, paras);
/* 107 */     cmd = new GenericBoCmdWrapper(this.preInterceptors, this.postInterceptors, cmd);
/* 108 */     return cmd;
/*     */   }
/*     */ 
/*     */   public IBoCommand createHttpBoCmd(String httpUrl, BoCmdContext boCmdContext, Object[] paras)
/*     */   {
/* 119 */     IBoProxy boProxy = new HttpBoProxy(httpUrl);
/* 120 */     IBoCommand cmd = new GenericBoCmd(boProxy, boCmdContext, paras);
/* 121 */     cmd = new GenericBoCmdWrapper(this.preInterceptors, this.postInterceptors, cmd);
/* 122 */     return cmd;
/*     */   }
/*     */ 
/*     */   public void execBoCmd(IBoCommand boCmd)
/*     */   {
/* 130 */     long startTime = System.currentTimeMillis();
/* 131 */     BoCmdContext context = boCmd.getCmdContext();
/* 132 */     IBoCommand invokeCmd = createBoCmd(context, boCmd.getParas());
/* 133 */     invokeCmd.setHostIP(((GenericBoCmd)boCmd).getHostIP());
/* 134 */     invokeCmd.setHostName(((GenericBoCmd)boCmd).getHostName());
/* 135 */     invokeCmd.setCompressed(((GenericBoCmd)boCmd).isCompressed());
/* 136 */     invokeCmd.setUnZipTime(((GenericBoCmd)boCmd).getUnZipTime());
/*     */     try {
/* 138 */       invokeCmd.exec();
/*     */     }
/*     */     catch (Throwable ex)
/*     */     {
/*     */       String actionName;
/*     */       CmdStatInfo csi;
/*     */       long aTime;
/*     */       String info;
/*     */       Iterator iterator;
/*     */       CmdStatInfo cmdStatInfo;
/* 140 */       if (LogHome.getLog().isDebugEnabled())
/* 141 */         LogHome.getLog().debug(ex);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/*     */         String actionName;
/*     */         CmdStatInfo csi;
/*     */         long aTime;
/*     */         String info;
/*     */         Iterator iterator;
/*     */         CmdStatInfo cmdStatInfo;
/* 145 */         boCmd.setException(invokeCmd.getException());
/* 146 */         boCmd.setResult(invokeCmd.getResult());
/* 147 */         boCmd.setZipTime(invokeCmd.getZipTime());
/* 148 */         String actionName = context.getBoName() + "." + context.getMethodName();
/*     */ 
/* 150 */         CmdStatInfo csi = (CmdStatInfo)this.boInvokeStatInfo.get(actionName);
/* 151 */         if (csi == null) {
/* 152 */           csi = new CmdStatInfo();
/* 153 */           csi.setActionName(actionName);
/* 154 */           this.boInvokeStatInfo.put(csi.getActionName(), csi);
/*     */         }
/* 156 */         long aTime = System.currentTimeMillis() - startTime;
/* 157 */         csi.addStatInfo(actionName, aTime);
/* 158 */         this.invokeCount += 1L;
/* 159 */         if (this.invokeCount % 1000L == 0L) {
/* 160 */           String info = "";
/* 161 */           Iterator iterator = this.boInvokeStatInfo.values().iterator();
/* 162 */           while (iterator.hasNext()) {
/* 163 */             CmdStatInfo cmdStatInfo = (CmdStatInfo)iterator.next();
/* 164 */             info = info + cmdStatInfo.toString() + "\r\n";
/*     */           }
/* 166 */           LogHome.getLog().info("BoCommandStatInfo:\r\n" + info);
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {
/* 170 */         LogHome.getLog().error(ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object execBoCmd(BoCmdContext boCmdContext, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 183 */     IBoCommand boCmd = createBoCmd(boCmdContext, paras);
/* 184 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public Object execBoCmd(String actionName, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 195 */     BoCmdContext context = ActionHelper.getBoCmdContext(actionName);
/* 196 */     IBoCommand boCmd = createBoCmd(context, paras);
/* 197 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public Object execBoCmd(IBoProxy boProxy, String actionName, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 208 */     BoCmdContext context = ActionHelper.getBoCmdContext(actionName);
/* 209 */     IBoCommand boCmd = createBoCmd(boProxy, context, paras);
/* 210 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public Object execBoCommand(String boProxyName, String actionName, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 221 */     BoCmdContext context = ActionHelper.getBoCmdContext(boProxyName, actionName);
/* 222 */     IBoCommand boCmd = createBoCmd(context, paras);
/* 223 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public Object execXrpcBoCmd(String xrpcUrl, String actionName, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 234 */     BoCmdContext context = ActionHelper.getBoCmdContext(actionName);
/* 235 */     IBoCommand boCmd = createXrpcBoCmd(xrpcUrl, context, paras);
/* 236 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public Object exeHttpBoCmd(String httpUrl, String actionName, Object[] paras)
/*     */     throws Exception
/*     */   {
/* 248 */     BoCmdContext context = ActionHelper.getBoCmdContext(actionName);
/* 249 */     IBoCommand boCmd = createHttpBoCmd(httpUrl, context, paras);
/* 250 */     return boCmd.exec();
/*     */   }
/*     */ 
/*     */   public void setPreInterceptors(List<ICmdInterceptor> _preInterceptors)
/*     */   {
/* 258 */     this.preInterceptors.addAll(_preInterceptors);
/*     */   }
/*     */ 
/*     */   public void addPreInterceptor(ICmdInterceptor interceptor)
/*     */   {
/* 266 */     this.preInterceptors.add(interceptor);
/*     */   }
/*     */ 
/*     */   public void removePreInterceptor(ICmdInterceptor _interceptor)
/*     */   {
/* 274 */     this.preInterceptors.remove(_interceptor);
/*     */   }
/*     */ 
/*     */   public void setPostInterceptor(ICmdInterceptor interceptor)
/*     */   {
/* 282 */     this.postInterceptors.add(interceptor);
/*     */   }
/*     */ 
/*     */   public void removePostInterceptor(ICmdInterceptor interceptor)
/*     */   {
/* 290 */     this.postInterceptors.remove(interceptor);
/*     */   }
/*     */ 
/*     */   public Map<String, CmdStatInfo> getBoInvokeStatInfo() {
/* 294 */     return this.boInvokeStatInfo;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.BoCmdFactory
 * JD-Core Version:    0.6.0
 */