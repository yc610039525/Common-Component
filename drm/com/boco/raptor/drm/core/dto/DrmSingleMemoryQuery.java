/*    */ package com.boco.raptor.drm.core.dto;
/*    */ 
/*    */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class DrmSingleMemoryQuery
/*    */   implements Serializable
/*    */ {
/*    */   public static final String CLEAR_REQUEST = "CLEAR_REQUEST";
/*    */   public static final String CLEAR_SESSION = "CLEAR_SESSION";
/*    */   private String sessionId;
/*    */   private String requestId;
/*    */   private String bmClassId;
/*    */   private String actionId;
/* 35 */   private boolean refresh = false;
/*    */   private BMClassMeta classMeta;
/* 39 */   private Map<String, Object> queryPara = new HashMap();
/*    */ 
/*    */   public String getActionId()
/*    */   {
/* 45 */     return this.actionId;
/*    */   }
/*    */ 
/*    */   public String getBmClassId() {
/* 49 */     return this.bmClassId;
/*    */   }
/*    */ 
/*    */   public BMClassMeta getClassMeta() {
/* 53 */     return this.classMeta;
/*    */   }
/*    */ 
/*    */   public Map getQueryPara() {
/* 57 */     return this.queryPara;
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 61 */     return this.sessionId;
/*    */   }
/*    */ 
/*    */   public boolean isRefresh()
/*    */   {
/* 66 */     return this.refresh;
/*    */   }
/*    */ 
/*    */   public String getRequestId() {
/* 70 */     return this.requestId;
/*    */   }
/*    */ 
/*    */   public void setQueryPara(Map queryPara) {
/* 74 */     this.queryPara = queryPara;
/*    */   }
/*    */ 
/*    */   public void setClassMeta(BMClassMeta classMeta) {
/* 78 */     this.classMeta = classMeta;
/*    */   }
/*    */ 
/*    */   public void setBmClassId(String bmClassId) {
/* 82 */     this.bmClassId = bmClassId;
/*    */   }
/*    */ 
/*    */   public void setActionId(String actionId) {
/* 86 */     this.actionId = actionId;
/*    */   }
/*    */ 
/*    */   public void setSessionId(String sessionId) {
/* 90 */     this.sessionId = sessionId;
/*    */   }
/*    */ 
/*    */   public void setRefresh(boolean refresh)
/*    */   {
/* 95 */     this.refresh = refresh;
/*    */   }
/*    */ 
/*    */   public void setRequestId(String requestId) {
/* 99 */     this.requestId = requestId;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.DrmSingleMemoryQuery
 * JD-Core Version:    0.6.0
 */