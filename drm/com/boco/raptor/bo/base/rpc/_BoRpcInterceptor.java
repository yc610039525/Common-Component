/*    */ package com.boco.raptor.bo.base.rpc;
/*    */ 
/*    */ import com.boco.transnms.client.model.base.BoCmdFactory;
/*    */ import com.boco.transnms.common.dto.base.BoCmdContext;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class _BoRpcInterceptor
/*    */   implements InvocationHandler
/*    */ {
/*    */   private String boName;
/*    */ 
/*    */   public _BoRpcInterceptor()
/*    */   {
/*    */   }
/*    */ 
/*    */   public _BoRpcInterceptor(String _boName)
/*    */   {
/* 43 */     this.boName = _boName;
/*    */   }
/*    */ 
/*    */   public final Object invoke(Object proxy, Method method, Object[] args)
/*    */     throws Throwable
/*    */   {
/* 54 */     BoCmdContext context = new BoCmdContext();
/* 55 */     context.setBoName(this.boName);
/* 56 */     context.setMethodName(method.getName());
/* 57 */     context.setParaClassTypes(method.getParameterTypes());
/* 58 */     return BoCmdFactory.getInstance().execBoCmd(context, args);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.base.rpc._BoRpcInterceptor
 * JD-Core Version:    0.6.0
 */