/*     */ package com.boco.transnms.client.model.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericBoCmdWrapper
/*     */   implements IBoCommand
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final List<ICmdInterceptor> preInterceptors;
/*     */   private final List<ICmdInterceptor> postInterceptors;
/*     */   private IBoCommand command;
/*     */   private UserException exception;
/*     */ 
/*     */   public GenericBoCmdWrapper(List<ICmdInterceptor> _preInterceptors, List<ICmdInterceptor> _postInterceptors, IBoCommand _command)
/*     */   {
/*  54 */     this.preInterceptors = _preInterceptors;
/*  55 */     this.postInterceptors = _postInterceptors;
/*  56 */     this.command = _command;
/*     */   }
/*     */ 
/*     */   public Object exec()
/*     */     throws UserException
/*     */   {
/*  67 */     long startTime = System.currentTimeMillis();
/*     */     try {
/*  69 */       callInterceptor(this.preInterceptors, this);
/*     */     } catch (Throwable ex) {
/*  71 */       this.exception = doException(ex);
/*  72 */       this.command.setException(this.exception);
/*  73 */       throw this.exception;
/*     */     }
/*  75 */     Object result = null;
/*     */     try {
/*  77 */       result = this.command.exec();
/*     */     } finally {
/*  79 */       callInterceptor(this.postInterceptors, this);
/*     */     }
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   private static UserException doException(Throwable ex)
/*     */   {
/*  92 */     LogHome.getLog().error("", ex);
/*  93 */     if (((ex instanceof UserException)) && (ex.getCause() == null)) {
/*  94 */       return (UserException)ex;
/*     */     }
/*     */ 
/*  97 */     UserException exp = null;
/*  98 */     if ((ex.getCause() != null) && ((ex.getCause() instanceof UserException))) {
/*  99 */       exp = (UserException)ex.getCause();
/* 100 */       if ((exp.getCause() != null) && ((exp.getCause() instanceof UserException)))
/* 101 */         exp = (UserException)exp.getCause();
/*     */     }
/*     */     else {
/* 104 */       exp = new UserException("系统异常：[" + ex.getMessage() + "]");
/*     */     }
/*     */ 
/* 107 */     return exp;
/*     */   }
/*     */ 
/*     */   private static void callInterceptor(List<ICmdInterceptor> interceptors, IBoCommand cmd)
/*     */     throws UserException
/*     */   {
/* 121 */     for (int i = 0; i < interceptors.size(); i++) {
/* 122 */       ICmdInterceptor interceptor = (ICmdInterceptor)interceptors.get(i);
/* 123 */       interceptor.doCommand(cmd);
/*     */     }
/*     */   }
/*     */ 
/*     */   public BoCmdContext getCmdContext()
/*     */   {
/* 133 */     return this.command.getCmdContext();
/*     */   }
/*     */ 
/*     */   public Object[] getParas()
/*     */   {
/* 142 */     return this.command.getParas();
/*     */   }
/*     */ 
/*     */   public Object getResult()
/*     */   {
/* 151 */     return this.command.getResult();
/*     */   }
/*     */ 
/*     */   public byte[] getZipBytes()
/*     */     throws IOException
/*     */   {
/* 162 */     return this.command.getZipBytes();
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() throws IOException {
/* 166 */     return this.command.getBytes();
/*     */   }
/*     */ 
/*     */   public UserException getException()
/*     */   {
/* 175 */     return this.command.getException();
/*     */   }
/*     */ 
/*     */   public void setResult(Object result)
/*     */   {
/* 185 */     this.command.setResult(result);
/*     */   }
/*     */ 
/*     */   public void setException(UserException exp)
/*     */   {
/* 195 */     this.command.setException(exp);
/*     */   }
/*     */ 
/*     */   public void setCmdTarget(String target)
/*     */   {
/* 205 */     this.command.setCmdTarget(target);
/*     */   }
/*     */ 
/*     */   public String getCmdTarget()
/*     */   {
/* 214 */     return this.command.getCmdTarget();
/*     */   }
/*     */ 
/*     */   public String getHostIP()
/*     */   {
/* 223 */     return this.command.getHostIP();
/*     */   }
/*     */ 
/*     */   public String getHostName()
/*     */   {
/* 232 */     return this.command.getHostName();
/*     */   }
/*     */ 
/*     */   public void setHostIP(String hostIP)
/*     */   {
/* 242 */     this.command.setHostIP(hostIP);
/*     */   }
/*     */ 
/*     */   public void setHostName(String hostName)
/*     */   {
/* 252 */     this.command.setHostName(hostName);
/*     */   }
/*     */ 
/*     */   public int getCmdSize()
/*     */   {
/* 262 */     return this.command.getCmdSize();
/*     */   }
/*     */ 
/*     */   public void setCmdSize(int size)
/*     */   {
/* 272 */     this.command.setCmdSize(size);
/*     */   }
/*     */ 
/*     */   public boolean isCompressed() {
/* 276 */     return this.command.isCompressed();
/*     */   }
/*     */ 
/*     */   public void setCompressed(boolean compress) {
/* 280 */     this.command.setCompressed(compress);
/*     */   }
/*     */ 
/*     */   public long getZipTime() {
/* 284 */     return this.command.getZipTime();
/*     */   }
/*     */ 
/*     */   public void setZipTime(long _zipTime) {
/* 288 */     this.command.setZipTime(_zipTime);
/*     */   }
/*     */ 
/*     */   public long getUnZipTime() {
/* 292 */     return this.command.getUnZipTime();
/*     */   }
/*     */ 
/*     */   public void setUnZipTime(long _UnZipTime) {
/* 296 */     this.command.setUnZipTime(_UnZipTime);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.GenericBoCmdWrapper
 * JD-Core Version:    0.6.0
 */