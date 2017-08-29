/*    */ package com.boco.transnms.client.model.base;
/*    */ 
/*    */ import com.boco.transnms.server.bo.base.BoHomeFactory;
/*    */ 
/*    */ public class SpringBoProxy extends AbstractBoProxy
/*    */ {
/*    */   public SpringBoProxy(String boName)
/*    */   {
/* 37 */     setBoName(boName);
/*    */   }
/*    */ 
/*    */   protected Object getBoProxy()
/*    */     throws Exception
/*    */   {
/* 48 */     return BoHomeFactory.getInstance().getBO(super.getBoName());
/*    */   }
/*    */ 
/*    */   public BoProxyType getBoProxyType()
/*    */   {
/* 57 */     return BoProxyType.SPRING_TYPE;
/*    */   }
/*    */ 
/*    */   public String getIdentifier()
/*    */   {
/* 66 */     return "local";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.SpringBoProxy
 * JD-Core Version:    0.6.0
 */