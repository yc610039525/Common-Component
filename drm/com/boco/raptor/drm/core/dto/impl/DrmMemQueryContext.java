/*    */ package com.boco.raptor.drm.core.dto.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*    */ import com.boco.transnms.common.dto.base.BoQueryContext;
/*    */ 
/*    */ public class DrmMemQueryContext extends BoQueryContext
/*    */   implements IDrmMemQueryContext
/*    */ {
/*  7 */   private String sessionId = "session";
/*  8 */   private String pageId = "page";
/*  9 */   private String requestId = "request";
/*    */   private boolean refresh;
/*    */   private int clearType;
/* 12 */   private int maxBufSize = 100;
/*    */ 
/*    */   public int getClearType()
/*    */   {
/* 18 */     return this.clearType;
/*    */   }
/*    */ 
/*    */   public String getPageId() {
/* 22 */     return this.pageId;
/*    */   }
/*    */ 
/*    */   public boolean isRefresh() {
/* 26 */     return this.refresh;
/*    */   }
/*    */ 
/*    */   public String getRequestId() {
/* 30 */     return this.requestId;
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 34 */     return this.sessionId;
/*    */   }
/*    */ 
/*    */   public int getMaxBufSize() {
/* 38 */     return this.maxBufSize;
/*    */   }
/*    */ 
/*    */   public void setClearType(int clearType) {
/* 42 */     this.clearType = clearType;
/*    */   }
/*    */ 
/*    */   public void setPageId(String pageId) {
/* 46 */     this.pageId = pageId;
/*    */   }
/*    */ 
/*    */   public void setRefresh(boolean refresh) {
/* 50 */     this.refresh = refresh;
/*    */   }
/*    */ 
/*    */   public void setRequestId(String requestId) {
/* 54 */     this.requestId = requestId;
/*    */   }
/*    */ 
/*    */   public void setSessionId(String sessionId) {
/* 58 */     this.sessionId = sessionId;
/*    */   }
/*    */ 
/*    */   public void setMaxBufSize(int maxBufSize) {
/* 62 */     this.maxBufSize = maxBufSize;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.DrmMemQueryContext
 * JD-Core Version:    0.6.0
 */