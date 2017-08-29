/*    */ package com.boco.raptor.drm.core.dto;
/*    */ 
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ModifyResult<T>
/*    */ {
/* 11 */   private int modifyTotalCount = 0;
/* 12 */   private String errorMsg = "";
/*    */ 
/* 14 */   private List<T> failedIds = new ArrayList();
/* 15 */   private List<Map> succeedDbos = new ArrayList();
/* 16 */   private Map options = new HashMap();
/*    */ 
/*    */   public ModifyResult() {
/*    */   }
/*    */ 
/*    */   public ModifyResult(int modifyTotalCount) {
/* 22 */     this.modifyTotalCount = modifyTotalCount;
/*    */   }
/*    */ 
/*    */   public List<T> getFailedIds() {
/* 26 */     return this.failedIds;
/*    */   }
/*    */ 
/*    */   public Map getOptions() {
/* 30 */     return this.options;
/*    */   }
/*    */ 
/*    */   public List<Map> getSucceedDbos()
/*    */   {
/* 35 */     return this.succeedDbos;
/*    */   }
/*    */ 
/*    */   public int getModifyTotalCount() {
/* 39 */     return this.modifyTotalCount;
/*    */   }
/*    */ 
/*    */   public String getErrorMsg() {
/* 43 */     return this.errorMsg;
/*    */   }
/*    */ 
/*    */   public boolean succeed() {
/* 47 */     return this.failedIds.size() == 0;
/*    */   }
/*    */ 
/*    */   public void addSucceedDbo(GenericDO dbo) {
/* 51 */     this.succeedDbos.add(dbo.getAllAttr());
/*    */   }
/*    */ 
/*    */   public void merge(ModifyResult<T> merged) {
/* 55 */     this.failedIds.addAll(merged.getFailedIds());
/* 56 */     this.options.putAll(merged.getOptions());
/* 57 */     this.succeedDbos.addAll(merged.getSucceedDbos());
/*    */   }
/*    */ 
/*    */   public void setModifyTotalCount(int modifyTotalCount) {
/* 61 */     this.modifyTotalCount = modifyTotalCount;
/*    */   }
/*    */ 
/*    */   public void setErrorMsg(String errorMsg) {
/* 65 */     this.errorMsg = errorMsg;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.ModifyResult
 * JD-Core Version:    0.6.0
 */