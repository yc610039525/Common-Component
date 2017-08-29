/*    */ package com.boco.transnms.server.bo.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.except.UserException;
/*    */ import com.boco.transnms.common.dto.base.BoActionContext;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import com.boco.transnms.server.bo.ibo.IGenericExtBO;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class GenericExtBO extends GenericBO
/*    */   implements IGenericExtBO
/*    */ {
/*    */   public GenericDO getExtObject(BoActionContext actionContext, long objectId)
/*    */     throws UserException
/*    */   {
/* 41 */     LogHome.getLog().info("调用getExtObject!!");
/* 42 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.GenericExtBO
 * JD-Core Version:    0.6.0
 */