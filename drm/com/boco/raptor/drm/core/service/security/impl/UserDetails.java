/*     */ package com.boco.raptor.drm.core.service.security.impl;
/*     */ 
/*     */ import com.boco.raptor.drm.core.service.security.IUserDetails;
/*     */ 
/*     */ public class UserDetails
/*     */   implements IUserDetails
/*     */ {
/*     */   private String userId;
/*     */   private String truename;
/*     */   private String password;
/*     */   private String clientIp;
/*     */   private String loginName;
/*     */   private String clientHostName;
/*  24 */   private boolean isAdmin = false;
/*     */   private String relatedDistrict;
/*     */ 
/*     */   public UserDetails(String userId, String loginName, String truename, String password, boolean isAdmin)
/*     */   {
/*  27 */     this.userId = userId;
/*  28 */     this.truename = truename;
/*  29 */     this.password = password;
/*  30 */     this.isAdmin = isAdmin;
/*  31 */     this.loginName = loginName;
/*     */   }
/*     */ 
/*     */   public UserDetails(String userId, String loginName, String truename, String password, boolean isAdmin, String clientIp, String relatedDistrict) {
/*  35 */     this.userId = userId;
/*  36 */     this.truename = truename;
/*  37 */     this.password = password;
/*  38 */     this.isAdmin = isAdmin;
/*  39 */     this.loginName = loginName;
/*  40 */     this.clientIp = clientIp;
/*  41 */     this.relatedDistrict = relatedDistrict;
/*     */   }
/*     */ 
/*     */   public String getClientHostName()
/*     */   {
/*  53 */     return this.clientHostName;
/*     */   }
/*     */ 
/*     */   public String getClientIp()
/*     */   {
/*  64 */     return this.clientIp;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/*  75 */     return this.password;
/*     */   }
/*     */ 
/*     */   public String getTruename()
/*     */   {
/*  86 */     return this.truename;
/*     */   }
/*     */ 
/*     */   public String getUserId()
/*     */   {
/*  97 */     return this.userId;
/*     */   }
/*     */ 
/*     */   public void setUserId(String userId) {
/* 101 */     this.userId = userId;
/*     */   }
/*     */ 
/*     */   public void setTruename(String truename) {
/* 105 */     this.truename = truename;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password) {
/* 109 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public void setClientIp(String clientIp) {
/* 113 */     this.clientIp = clientIp;
/*     */   }
/*     */ 
/*     */   public void setClientHostName(String clientHostName) {
/* 117 */     this.clientHostName = clientHostName;
/*     */   }
/*     */ 
/*     */   public boolean getIsAdmin() {
/* 121 */     return this.isAdmin;
/*     */   }
/*     */ 
/*     */   public void setIsAdmin(boolean isAdmin) {
/* 125 */     this.isAdmin = isAdmin;
/*     */   }
/*     */ 
/*     */   public String getLoginName() {
/* 129 */     return this.loginName;
/*     */   }
/*     */ 
/*     */   public void setLoginName(String loginName) {
/* 133 */     this.loginName = loginName;
/*     */   }
/*     */ 
/*     */   public String getRelatedDistrict() {
/* 137 */     return this.relatedDistrict;
/*     */   }
/*     */ 
/*     */   public void setRelatedDistrict(String relatedDistrict) {
/* 141 */     this.relatedDistrict = relatedDistrict;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.impl.UserDetails
 * JD-Core Version:    0.6.0
 */