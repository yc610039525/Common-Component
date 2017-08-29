/*     */ package com.boco.transnms.server.bo.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.transnms.common.dto.base.BoActionContext;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.server.bo.ibo.IGenericBO;
/*     */ import com.boco.transnms.server.dao.base.AbstractDAO;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericBO extends AbstractBO
/*     */   implements IGenericBO
/*     */ {
/*     */   public GenericDO getObject(BoActionContext actionContext, long objectId)
/*     */     throws UserException
/*     */   {
/*  43 */     GenericDO dbo = new GenericDO();
/*  44 */     dbo.setObjectNum(objectId);
/*     */     try {
/*  46 */       getGenericDAO().getObject(dbo);
/*     */     } catch (Exception ex) {
/*  48 */       LogHome.getLog().error("", ex);
/*  49 */       throw new UserException(ex);
/*     */     }
/*  51 */     return dbo;
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjectsBySql(BoActionContext actionContext, String sql, GenericDO dboTemplate)
/*     */     throws UserException
/*     */   {
/*  59 */     DataObjectList dbos = null;
/*     */     try {
/*  61 */       dbos = getGenericDAO().getObjectsBySql(sql, dboTemplate, 0);
/*     */     } catch (Exception ex) {
/*  63 */       LogHome.getLog().error("", ex);
/*  64 */       throw new UserException(ex);
/*     */     }
/*  66 */     return dbos;
/*     */   }
/*     */ 
/*     */   public DboCollection getObjectsBySql(BoActionContext actionContext, String sql, GenericDO[] dboTemplates)
/*     */     throws UserException
/*     */   {
/*  74 */     DboCollection dbos = null;
/*     */     try {
/*  76 */       dbos = getGenericDAO().selectDBOs(sql, dboTemplates);
/*     */     } catch (Exception ex) {
/*  78 */       LogHome.getLog().error("", ex);
/*  79 */       throw new UserException(ex);
/*     */     }
/*  81 */     return dbos;
/*     */   }
/*     */ 
/*     */   public void deleteObject(BoActionContext actionContext, Long objectId)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/*  90 */       GenericDO dbo = new GenericDO();
/*  91 */       dbo.setObjectNum(objectId.longValue());
/*  92 */       getGenericDAO().deleteObject(actionContext, dbo);
/*     */     } catch (Exception ex) {
/*  94 */       LogHome.getLog().error("", ex);
/*  95 */       throw new UserException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateObject(BoActionContext actionContext, GenericDO dbo)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 105 */       getGenericDAO().updateObject(actionContext, dbo);
/*     */     } catch (Exception ex) {
/* 107 */       LogHome.getLog().error("", ex);
/* 108 */       throw new UserException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createObject(BoActionContext actionContext, GenericDO dbo)
/*     */     throws UserException
/*     */   {
/*     */     try
/*     */     {
/* 118 */       getGenericDAO().createObject(actionContext, dbo);
/*     */     } catch (Exception ex) {
/* 120 */       LogHome.getLog().error("", ex);
/* 121 */       throw new UserException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected AbstractDAO getGenericDAO() {
/* 126 */     return (AbstractDAO)super.getDAO("GenericDAO");
/*     */   }
/*     */ 
/*     */   public Object getData(BoActionContext actionContext, Map map, List list, String str, Long num, long no)
/*     */   {
/* 134 */     return "getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567getDataResult1234567";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.GenericBO
 * JD-Core Version:    0.6.0
 */