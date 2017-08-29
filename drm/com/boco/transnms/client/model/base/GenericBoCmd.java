/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.common.util.io.NetHelper;
/*     */ import com.boco.common.util.io.ObjBufInputStream;
/*     */ import com.boco.common.util.io.ObjBufOutputStream;
/*     */ import com.boco.common.util.io.ObjZipBufInputStream;
/*     */ import com.boco.common.util.io.ObjZipBufOutputStream;
/*     */ import com.boco.transnms.common.bussiness.helper.ActionHelper;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*     */ import java.io.IOException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.SocketException;
/*     */ import org.apache.commons.httpclient.HttpException;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericBoCmd
/*     */   implements IBoCommand
/*     */ {
/*  41 */   private static final String HOST_IP = NetHelper.getHostIP();
/*  42 */   private static final String HOST_NAME = NetHelper.getHostName();
/*     */   private static final long serialVersionUID = 1L;
/*  48 */   private Object[] paras = new Object[0];
/*     */   private BoCmdContext boContext;
/*     */   private Object result;
/*     */   private UserException exception;
/*     */   private IBoProxy boProxy;
/*  53 */   private String hostIP = HOST_IP;
/*  54 */   private String hostName = HOST_NAME;
/*     */   private String cmdTarget;
/*     */   private String invokeHashCode;
/*     */   private int size;
/*  58 */   private boolean compressed = true;
/*  59 */   private long zipTime = -1L;
/*  60 */   private long unZiptime = -1L;
/*     */ 
/*     */   protected GenericBoCmd(IBoProxy _boProxy, BoCmdContext _boCmdContext, Object[] _paras)
/*     */   {
/*  73 */     this.boContext = _boCmdContext;
/*  74 */     this.boProxy = _boProxy;
/*  75 */     this.paras = _paras;
/*  76 */     prepareParas();
/*     */   }
/*     */ 
/*     */   private void prepareParas()
/*     */   {
/*  83 */     IBoActionContext actionContext = getActionContext();
/*  84 */     if (actionContext != null) {
/*  85 */       if (actionContext.getHostIP() == null) {
/*  86 */         actionContext.setHostIP(this.hostIP);
/*     */       }
/*     */ 
/*  89 */       if (actionContext.getUserId() == null) {
/*  90 */         actionContext.setHostName(this.hostName);
/*     */       }
/*  92 */       this.compressed = actionContext.isCompressed();
/*  93 */       String actionName = actionContext.getActionName();
/*  94 */       if ((this.boContext != null) && ((actionName == null) || (actionName.trim().equals(""))))
/*  95 */         actionContext.setActionName(this.boContext.getBoName() + "." + this.boContext.getMethodName());
/*     */     }
/*  97 */     else if (this.boContext != null) {
/*  98 */       this.compressed = this.boContext.isCompressed();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object[] getParas()
/*     */   {
/* 108 */     return this.paras;
/*     */   }
/*     */ 
/*     */   public Object getResult()
/*     */   {
/* 117 */     return this.result;
/*     */   }
/*     */ 
/*     */   public UserException getException()
/*     */   {
/* 126 */     return this.exception;
/*     */   }
/*     */ 
/*     */   public BoCmdContext getCmdContext()
/*     */   {
/* 135 */     return this.boContext;
/*     */   }
/*     */ 
/*     */   public Object exec()
/*     */     throws UserException
/*     */   {
/* 146 */     long startTime = System.currentTimeMillis();
/*     */ 
/* 148 */     String info = "clientIp = " + this.hostIP + ", boName=" + this.boContext.getBoName() + ", methodName=" + this.boContext.getMethodName();
/*     */ 
/* 150 */     if (ActionHelper.isPrintActionLog(this.boContext.getBoName(), this.boContext.getMethodName())) {
/* 151 */       LogHome.getLog().info("--- 命令调用[" + info + ", zip=" + isCompressed() + "]开始");
/*     */     }
/*     */     try
/*     */     {
/* 155 */       if ((((this.boProxy instanceof XrpcBoProxy)) || ((this.boProxy instanceof HttpBoProxy))) && 
/* 156 */         (BoCacheCmdProxy.getInstance().exec(this))) {
/* 157 */         this.result = getResult();
/* 158 */         Object localObject1 = this.result;
/*     */         long expendTime;
/*     */         return localObject1;
/*     */       }
/* 161 */       this.result = this.boProxy.exec(this);
/* 162 */       if (((this.boProxy instanceof XrpcBoProxy)) || ((this.boProxy instanceof HttpBoProxy)))
/* 163 */         BoCacheCmdProxy.getInstance().setCacheResult(this, this.result);
/*     */     }
/*     */     catch (Throwable ex)
/*     */     {
/*     */       long expendTime;
/* 166 */       LogHome.getLog().warn("--- 命令调用[" + info + ", 调用失败 ,地址=" + this.boProxy.getIdentifier());
/* 167 */       this.exception = doException(ex);
/* 168 */       throw this.exception;
/*     */     } finally {
/* 170 */       if (ActionHelper.isPrintActionLog(this.boContext.getBoName(), this.boContext.getMethodName())) {
/* 171 */         long expendTime = System.currentTimeMillis() - startTime;
/* 172 */         if (expendTime > TnmsDrmCfg.getInstance().getMaxBoInvokeTime())
/* 173 */           LogHome.getLog().warn("--- 命令调用[" + info + ", time=" + expendTime + ", zipTime=" + getZipTime() + ", unZipTime=" + getUnZipTime() + ", zip=" + isCompressed() + "]结束");
/*     */         else {
/* 175 */           LogHome.getLog().info("--- 命令调用[" + info + ", time=" + expendTime + ", zipTime=" + getZipTime() + ", unZipTime=" + getUnZipTime() + ", zip=" + isCompressed() + "]结束");
/*     */         }
/*     */       }
/*     */     }
/* 179 */     return this.result;
/*     */   }
/*     */ 
/*     */   private static UserException doException(Throwable ex)
/*     */   {
/* 228 */     LogHome.getLog().debug("", ex);
/* 229 */     if (((ex instanceof UserException)) && (ex.getCause() == null)) {
/* 230 */       return (UserException)ex;
/*     */     }
/*     */ 
/* 233 */     UserException exp = null;
/* 234 */     if ((ex.getCause() != null) && ((ex.getCause() instanceof UserException))) {
/* 235 */       exp = (UserException)ex.getCause();
/* 236 */       if ((exp.getCause() != null) && ((exp.getCause() instanceof UserException)))
/* 237 */         exp = (UserException)exp.getCause();
/*     */     }
/* 239 */     else if (((ex instanceof ConnectException)) || ((ex instanceof SocketException)) || ((ex instanceof HttpException)))
/*     */     {
/* 241 */       exp = new UserException("无法连接服务器，请稍候再试！");
/* 242 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */     else {
/* 245 */       exp = new UserException(ex);
/*     */     }
/*     */ 
/* 248 */     return exp;
/*     */   }
/*     */ 
/*     */   public void setException(UserException _exception)
/*     */   {
/* 258 */     this.exception = _exception;
/*     */   }
/*     */ 
/*     */   public void setResult(Object _result)
/*     */   {
/* 268 */     this.result = _result;
/*     */   }
/*     */ 
/*     */   private void clearInvoke()
/*     */   {
/* 275 */     this.paras = null;
/* 276 */     this.boProxy = null;
/*     */   }
/*     */ 
/*     */   public byte[] getZipBytes()
/*     */     throws IOException
/*     */   {
/* 291 */     long startTime = System.currentTimeMillis();
/* 292 */     IBoProxy proxy = this.boProxy;
/* 293 */     this.boProxy = null;
/*     */ 
/* 295 */     ObjZipBufOutputStream out = null;
/* 296 */     out = new ObjZipBufOutputStream();
/* 297 */     out.writeObject(this);
/* 298 */     out.close();
/*     */ 
/* 300 */     this.boProxy = proxy;
/* 301 */     byte[] b = out.getBuf();
/*     */ 
/* 309 */     this.zipTime = (System.currentTimeMillis() - startTime);
/* 310 */     return b;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() throws IOException {
/* 314 */     IBoProxy proxy = this.boProxy;
/* 315 */     this.boProxy = null;
/*     */ 
/* 317 */     ObjBufOutputStream out = null;
/* 318 */     out = new ObjBufOutputStream();
/* 319 */     out.writeObject(this);
/* 320 */     out.close();
/*     */ 
/* 322 */     this.boProxy = proxy;
/* 323 */     return out.getBuf();
/*     */   }
/*     */ 
/*     */   public byte[] getResultBytes()
/*     */     throws IOException
/*     */   {
/* 334 */     clearInvoke();
/* 335 */     byte[] b = null;
/* 336 */     if (isCompressed())
/* 337 */       b = getZipBytes();
/*     */     else {
/* 339 */       b = getBytes();
/*     */     }
/* 341 */     return b;
/*     */   }
/*     */ 
/*     */   public void setCmdTarget(String target)
/*     */   {
/* 351 */     this.cmdTarget = target;
/*     */   }
/*     */ 
/*     */   public void setInvokeHashCode(String _invokeHashCode)
/*     */   {
/* 361 */     this.invokeHashCode = _invokeHashCode;
/*     */   }
/*     */ 
/*     */   public void setHostIP(String _hostIP)
/*     */   {
/* 371 */     this.hostIP = _hostIP;
/*     */   }
/*     */ 
/*     */   public void setHostName(String _hostName)
/*     */   {
/* 381 */     this.hostName = _hostName;
/*     */   }
/*     */ 
/*     */   public String getCmdTarget()
/*     */   {
/* 390 */     return this.cmdTarget;
/*     */   }
/*     */ 
/*     */   public String getInvokeHashCode()
/*     */   {
/* 399 */     return this.invokeHashCode;
/*     */   }
/*     */ 
/*     */   public String getHostIP()
/*     */   {
/* 408 */     return this.hostIP;
/*     */   }
/*     */ 
/*     */   public String getHostName()
/*     */   {
/* 417 */     return this.hostName;
/*     */   }
/*     */ 
/*     */   public IBoActionContext getActionContext()
/*     */   {
/* 426 */     IBoActionContext context = null;
/* 427 */     Object[] _paras = getParas();
/* 428 */     if ((_paras != null) && (_paras.length > 0) && 
/* 429 */       ((_paras[0] instanceof IBoActionContext))) {
/* 430 */       context = (IBoActionContext)_paras[0];
/*     */     }
/*     */ 
/* 433 */     return context;
/*     */   }
/*     */ 
/*     */   public static GenericBoCmd fromZipBytes(byte[] bytes)
/*     */     throws Exception
/*     */   {
/* 448 */     long startTime = System.currentTimeMillis();
/* 449 */     ObjZipBufInputStream in = new ObjZipBufInputStream(bytes);
/* 450 */     GenericBoCmd boCmd = (GenericBoCmd)in.readObject();
/* 451 */     in.close();
/*     */ 
/* 453 */     long unzipTime = System.currentTimeMillis() - startTime;
/* 454 */     boCmd.setUnZipTime(unzipTime);
/* 455 */     return boCmd;
/*     */   }
/*     */ 
/*     */   public static GenericBoCmd fromBytes(byte[] bytes) throws Exception {
/* 459 */     ObjBufInputStream in = new ObjBufInputStream(bytes);
/* 460 */     GenericBoCmd boCmd = (GenericBoCmd)in.readObject();
/* 461 */     in.close();
/* 462 */     return boCmd;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 471 */     String str = "";
/* 472 */     IBoActionContext actionContext = getActionContext();
/* 473 */     if (actionContext != null)
/* 474 */       str = actionContext.toString();
/*     */     else {
/* 476 */       str = getCmdContext().toString();
/*     */     }
/* 478 */     return str;
/*     */   }
/*     */ 
/*     */   public int getCmdSize()
/*     */   {
/* 487 */     return this.size;
/*     */   }
/*     */ 
/*     */   public void setCmdSize(int _size)
/*     */   {
/* 497 */     this.size = _size;
/*     */   }
/*     */ 
/*     */   public boolean isCompressed() {
/* 501 */     return this.compressed;
/*     */   }
/*     */ 
/*     */   public void setCompressed(boolean compressed) {
/* 505 */     this.compressed = compressed;
/*     */   }
/*     */ 
/*     */   public long getZipTime() {
/* 509 */     return this.zipTime;
/*     */   }
/*     */ 
/*     */   public void setZipTime(long _zipTime) {
/* 513 */     this.zipTime = _zipTime;
/*     */   }
/*     */ 
/*     */   public long getUnZipTime() {
/* 517 */     return this.unZiptime;
/*     */   }
/*     */ 
/*     */   public void setUnZipTime(long _UnZipTime) {
/* 521 */     this.unZiptime = _UnZipTime;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.GenericBoCmd
 * JD-Core Version:    0.6.0
 */