/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.raptor.cfg.StartUpEnv;
/*     */ import com.boco.raptor.common.message.GenericMessage;
/*     */ import com.boco.raptor.common.message.IMessage;
/*     */ import com.boco.raptor.common.message.ISimpleMsgListener;
/*     */ import com.boco.raptor.common.message.JMSConnectionManager;
/*     */ import com.boco.raptor.common.message.MsgBusManager;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import com.boco.transnms.common.dto.base.BoQueryContext;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public final class BoCacheCmdProxy
/*     */ {
/*  46 */   private static BoCacheCmdProxy instance = new BoCacheCmdProxy();
/*  47 */   private final Map<String, Map<String, Object>> cmdResults = new Hashtable();
/*     */   private boolean isInitSucceed;
/*  49 */   private boolean isInitInvoked = false;
/*  50 */   private String syncTopicName = "T_CM_BO_CACHE_SYNC";
/*  51 */   private Map classTypeMap = new HashMap();
/*  52 */   private Map<String, List<String>> dto2actionNames = new HashMap();
/*     */ 
/*     */   private BoCacheCmdProxy()
/*     */   {
/*  68 */     if (!this.isInitInvoked) {
/*  69 */       this.isInitInvoked = true;
/*  70 */       String localServerName = StartUpEnv.getServerName();
/*  71 */       if ((localServerName != null) && (!"ALL".equals(localServerName)))
/*     */         try {
/*  73 */           String contextName = JMSConnectionManager.getInstance().getJMSContextName(this.syncTopicName);
/*     */ 
/*  75 */           if ((contextName != null) && (contextName.trim().length() > 0)) {
/*  76 */             MsgBusManager.getInstance().addMsgListener(this.syncTopicName, "", new ChangeListener(null));
/*     */ 
/*  79 */             this.isInitSucceed = true;
/*  80 */             LogHome.getLog().info("BO调用调用前置缓存：---------  初始化成功------------ !");
/*     */           } else {
/*  82 */             LogHome.getLog().info("客户端不加载前置缓存");
/*     */           }
/*     */         } catch (Exception ex) {
/*  85 */           LogHome.getLog().info("BO调用调用前置缓存：消息初始化失败");
/*  86 */           LogHome.getLog().error("", ex);
/*     */         }
/*     */       else {
/*  89 */         LogHome.getLog().info("BO调用调用前置缓存：全服务器模式或客户端运行，不加载 ！");
/*     */       }
/*     */ 
/*  92 */       this.classTypeMap.put(String.class, String.class);
/*  93 */       this.classTypeMap.put(Integer.TYPE, Integer.TYPE);
/*  94 */       this.classTypeMap.put(Integer.class, Integer.class);
/*  95 */       this.classTypeMap.put(Long.TYPE, Long.TYPE);
/*  96 */       this.classTypeMap.put(Long.class, Long.class);
/*  97 */       this.classTypeMap.put(Boolean.TYPE, Boolean.TYPE);
/*  98 */       this.classTypeMap.put(Boolean.class, Boolean.class);
/*  99 */       this.classTypeMap.put(BoActionContext.class, BoActionContext.class);
/* 100 */       this.classTypeMap.put(BoQueryContext.class, BoQueryContext.class);
/* 101 */       this.classTypeMap.put(HashMap.class, HashMap.class);
/* 102 */       this.classTypeMap.put(Hashtable.class, Hashtable.class);
/* 103 */       this.classTypeMap.put(Float.TYPE, Float.TYPE);
/* 104 */       this.classTypeMap.put(Float.class, Float.class);
/* 105 */       this.classTypeMap.put(Double.TYPE, Double.TYPE);
/* 106 */       this.classTypeMap.put(Double.class, Double.class);
/* 107 */       this.classTypeMap.put(Timestamp.class, Timestamp.class);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static BoCacheCmdProxy getInstance()
/*     */   {
/* 117 */     return instance;
/*     */   }
/*     */ 
/*     */   public boolean exec(GenericBoCmd cmd)
/*     */   {
/* 128 */     if (!this.isInitSucceed) {
/* 129 */       return false;
/*     */     }
/*     */ 
/* 132 */     String boName = cmd.getCmdContext().getBoName();
/* 133 */     String methodName = cmd.getCmdContext().getMethodName();
/* 134 */     String actionName = ActionHelper.getActionName(boName, methodName);
/* 135 */     Map cacheResult = (Map)this.cmdResults.get(actionName);
/* 136 */     if (cacheResult == null) {
/* 137 */       return false;
/*     */     }
/* 139 */     return isCmdResultCached(cacheResult, cmd);
/*     */   }
/*     */ 
/*     */   public void setSyncTopicName(String topicName)
/*     */   {
/* 149 */     this.syncTopicName = topicName;
/*     */   }
/*     */ 
/*     */   public void setCacheResult(GenericBoCmd cmd, Object result)
/*     */   {
/* 161 */     if ((this.isInitSucceed) && (cmd.getInvokeHashCode() != null) && (result != null)) {
/* 162 */       String boName = cmd.getCmdContext().getBoName();
/* 163 */       String methodName = cmd.getCmdContext().getMethodName();
/* 164 */       String actionName = ActionHelper.getActionName(boName, methodName);
/* 165 */       Map cacheResult = (Map)this.cmdResults.get(actionName);
/* 166 */       if (cacheResult != null)
/* 167 */         cacheResult.put(cmd.getInvokeHashCode(), result);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isCmdResultCached(Map<String, Object> cacheResult, GenericBoCmd cmd)
/*     */   {
/* 182 */     String key = getKey(cmd);
/* 183 */     if (key != null) {
/* 184 */       Object result = cacheResult.get(key);
/* 185 */       if (result != null) {
/* 186 */         cmd.setResult(result);
/* 187 */         return true;
/*     */       }
/*     */     }
/* 190 */     return false;
/*     */   }
/*     */ 
/*     */   private String getKey(GenericBoCmd cmd)
/*     */   {
/* 201 */     Object[] paras = cmd.getParas();
/* 202 */     String key = "";
/*     */ 
/* 204 */     for (int i = 0; i < paras.length; i++) {
/* 205 */       if (paras[i] == null) {
/* 206 */         key = key + "NULL-";
/*     */       } else {
/* 208 */         Class paraClass = paras[i].getClass();
/* 209 */         if (this.classTypeMap.containsKey(paraClass)) {
/* 210 */           String info = "boName=" + cmd.getCmdContext().getBoName() + ", methodName=" + cmd.getCmdContext().getMethodName();
/*     */ 
/* 212 */           LogHome.getLog().warn("不支持缓存BO访问参数：" + info + ", para[" + i + "]=" + paraClass);
/* 213 */           return null;
/*     */         }
/* 215 */         key = key + paras[i].hashCode() + "-";
/*     */       }
/* 217 */       cmd.setInvokeHashCode(key);
/*     */     }
/* 219 */     return key;
/*     */   }
/*     */ 
/*     */   public void setCacheActionNames(Map<String, String> actionNames)
/*     */   {
/* 229 */     for (String actionName : actionNames.keySet()) {
/* 230 */       if (!this.cmdResults.containsKey(actionName)) {
/* 231 */         this.cmdResults.put(actionName, new Hashtable());
/*     */       }
/* 233 */       String _dtoActionNames = (String)actionNames.get(actionName);
/* 234 */       String[] dtoActionNames = _dtoActionNames.split(",");
/* 235 */       for (String dtoActionName : dtoActionNames) {
/* 236 */         List _actionNames = (List)this.dto2actionNames.get(dtoActionName);
/* 237 */         if (_actionNames == null) {
/* 238 */           _actionNames = new ArrayList();
/* 239 */           this.dto2actionNames.put(dtoActionName, _actionNames);
/*     */         }
/* 241 */         _actionNames.add(actionName);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void notifyDtoChange(String dboClassName, DaoActionEnum daoActionEnum)
/*     */   {
/* 255 */     if (this.isInitSucceed) {
/* 256 */       String dboActionName = dboClassName + "." + daoActionEnum;
/* 257 */       List actionNames = (List)this.dto2actionNames.get(dboActionName);
/* 258 */       if (actionNames != null) {
/* 259 */         IMessage msg = new GenericMessage(this.syncTopicName);
/* 260 */         msg.setTargetId(dboActionName);
/* 261 */         MsgBusManager.getInstance().sendMessage(msg);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ChangeListener
/*     */     implements ISimpleMsgListener
/*     */   {
/*     */     private ChangeListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void notify(IMessage _msg)
/*     */     {
/*     */       try
/*     */       {
/* 282 */         GenericMessage msg = (GenericMessage)_msg;
/* 283 */         if (msg != null) {
/* 284 */           String dboActionName = msg.getTargetId();
/* 285 */           List actionNames = (List)BoCacheCmdProxy.this.dto2actionNames.get(dboActionName);
/* 286 */           for (String actionName : actionNames) {
/* 287 */             Map cacheResult = (Map)BoCacheCmdProxy.this.cmdResults.get(actionName);
/* 288 */             if (cacheResult != null)
/* 289 */               cacheResult.clear();
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {
/* 294 */         LogHome.getLog().warn("消息处理异常:" + ex.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum DaoActionEnum
/*     */   {
/*  61 */     ADD, MODIFY, DELETE;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.BoCacheCmdProxy
 * JD-Core Version:    0.6.0
 */