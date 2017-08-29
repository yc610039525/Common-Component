/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.Service;
/*     */ import javax.xml.rpc.ServiceFactory;
/*     */ 
/*     */ public class WsBoProxy extends AbstractBoProxy
/*     */ {
/*     */   private String serviceName;
/*     */   private String serviceNameSpace;
/*     */   private String wsClassName;
/*     */   private String wsContextName;
/*     */   private String wsUri;
/*     */ 
/*     */   public WsBoProxy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WsBoProxy(String boName, String _wsContextName, String wsUrl, String _serviceName, String _serviceNameSpace, String _wsClassName)
/*     */   {
/*  66 */     super.setBoName(boName);
/*  67 */     setWsContextName(_wsContextName);
/*  68 */     setWsUri(wsUrl);
/*  69 */     setServiceName(_serviceName);
/*  70 */     setServiceNameSpace(_serviceNameSpace);
/*  71 */     setWsClassName(_wsClassName);
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/*  81 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   public String getServiceNameSpace()
/*     */   {
/*  90 */     return this.serviceNameSpace;
/*     */   }
/*     */ 
/*     */   public String getWsClassName()
/*     */   {
/*  99 */     return this.wsClassName;
/*     */   }
/*     */ 
/*     */   public String getWsContextName()
/*     */   {
/* 108 */     return this.wsContextName;
/*     */   }
/*     */ 
/*     */   public String getWsUri()
/*     */   {
/* 117 */     return this.wsUri;
/*     */   }
/*     */ 
/*     */   public void setWsUri(String _wsUri)
/*     */   {
/* 127 */     this.wsUri = _wsUri;
/*     */   }
/*     */ 
/*     */   public void setWsContextName(String _wsContextName)
/*     */   {
/* 137 */     this.wsContextName = _wsContextName;
/*     */   }
/*     */ 
/*     */   public void setWsClassName(String _wsClassName)
/*     */   {
/* 147 */     this.wsClassName = _wsClassName;
/*     */   }
/*     */ 
/*     */   public void setServiceName(String _serviceName)
/*     */   {
/* 157 */     this.serviceName = _serviceName;
/*     */   }
/*     */ 
/*     */   public void setServiceNameSpace(String _serviceNameSpace)
/*     */   {
/* 167 */     this.serviceNameSpace = _serviceNameSpace;
/*     */   }
/*     */ 
/*     */   public void setBoName(String boName)
/*     */   {
/* 177 */     super.setBoName(boName);
/* 178 */     if (getWsUri() == null) {
/* 179 */       setWsUri(boName);
/*     */     }
/* 181 */     if (getServiceName() == null)
/* 182 */       setServiceName(boName);
/*     */   }
/*     */ 
/*     */   public Object exec(IBoCommand cmd)
/*     */     throws Exception
/*     */   {
/* 196 */     Object boProxy = null;
/* 197 */     if (cmd.getCmdTarget() != null)
/* 198 */       boProxy = getBoProxy(cmd.getCmdTarget());
/*     */     else {
/* 200 */       boProxy = getBoProxy();
/*     */     }
/* 202 */     Method method = boProxy.getClass().getMethod(cmd.getCmdContext().getMethodName(), super.parseParaTypes(cmd));
/*     */ 
/* 204 */     return method.invoke(boProxy, cmd.getParas());
/*     */   }
/*     */ 
/*     */   protected Object getBoProxy()
/*     */     throws Exception
/*     */   {
/* 215 */     String urlPrefix = WsUrlManager.getInstance().getWsUrl(getWsContextName());
/* 216 */     return getBoProxy(urlPrefix);
/*     */   }
/*     */ 
/*     */   private Object getBoProxy(String urlPrefix)
/*     */     throws Exception
/*     */   {
/* 229 */     URL url = new URL(urlPrefix + "/" + getWsUri() + "?wsdl");
/* 230 */     QName qname = new QName(getServiceNameSpace(), getServiceName());
/* 231 */     ServiceFactory factory = ServiceFactory.newInstance();
/* 232 */     Service service = factory.createService(url, qname);
/* 233 */     return service.getPort(Class.forName(getWsClassName()));
/*     */   }
/*     */ 
/*     */   public BoProxyType getBoProxyType()
/*     */   {
/* 242 */     return BoProxyType.WEBSERVICE_TYPE;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.WsBoProxy
 * JD-Core Version:    0.6.0
 */