/*     */ package com.boco.raptor.drm.core.service.security.impl;
/*     */ 
/*     */ import com.boco.raptor.drm.core.service.security.IAuthentication;
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Authentication
/*     */   implements IAuthentication
/*     */ {
/*     */   private List actionNames;
/*     */   private List bmClassId;
/*     */   private Map positiveObject;
/*     */   private Map reverseObject;
/*     */   private Map<String, List> dimensionObject;
/*     */   private Map<String, Map<String, List>> dimensionActionNames;
/*     */   private IUserDetails userDetails;
/*     */ 
/*     */   public List getBmClassIds()
/*     */   {
/*  40 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public Map getPositiveObjects()
/*     */   {
/*  51 */     return this.positiveObject;
/*     */   }
/*     */ 
/*     */   public Map getReverseObjects()
/*     */   {
/*  62 */     return this.reverseObject;
/*     */   }
/*     */ 
/*     */   public IUserDetails getUserDetails()
/*     */   {
/*  73 */     return this.userDetails;
/*     */   }
/*     */ 
/*     */   public List getActionNames() {
/*  77 */     return this.actionNames;
/*     */   }
/*     */ 
/*     */   public Map<String, List> getDimensionObject() {
/*  81 */     return this.dimensionObject;
/*     */   }
/*     */ 
/*     */   public Map getDimensionActionNames() {
/*  85 */     return this.dimensionActionNames;
/*     */   }
/*     */ 
/*     */   public void setActionNames(List actionNames) {
/*  89 */     this.actionNames = actionNames;
/*     */   }
/*     */ 
/*     */   public void setBmClassIds(List bmClassId) {
/*  93 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setPositiveObjects(Map positiveObject) {
/*  97 */     this.positiveObject = positiveObject;
/*     */   }
/*     */ 
/*     */   public void setReverseObjects(Map reverseObject) {
/* 101 */     this.reverseObject = reverseObject;
/*     */   }
/*     */ 
/*     */   public void setUserDetails(IUserDetails userDetails) {
/* 105 */     this.userDetails = userDetails;
/*     */   }
/*     */ 
/*     */   public void setDimensionObject(Map<String, List> dimensionObject) {
/* 109 */     this.dimensionObject = dimensionObject;
/*     */   }
/*     */ 
/*     */   public void setDimensionActionNames(Map<String, Map<String, List>> dimensionActionNames) {
/* 113 */     this.dimensionActionNames = dimensionActionNames;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.impl.Authentication
 * JD-Core Version:    0.6.0
 */