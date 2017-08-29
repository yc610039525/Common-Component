/*     */ package com.boco.raptor.drm.core.service.dynres.impl;
/*     */ 
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MemoryQueryService
/*     */ {
/*  29 */   private SessionDataStore sessionDataStore = new SessionDataStore(null);
/*     */ 
/*     */   public IDrmQueryResultSet getResultSet(IDrmMemQueryContext queryContext)
/*     */   {
/*  35 */     IDrmQueryResultSet result = null;
/*  36 */     IDrmQueryResultSet memoryData = (IDrmQueryResultSet)getMemoryData(queryContext);
/*  37 */     if (result == null) return null;
/*     */ 
/*  39 */     if (queryContext.getFetchSize() == 0) {
/*  40 */       result = memoryData;
/*     */     } else {
/*  42 */       IDrmQueryResultSet _result = memoryData;
/*  43 */       result = DrmEntityFactory.getInstance().createResultSet();
/*  44 */       _result.setCountValue(result.getCountValue());
/*  45 */       for (int i = queryContext.getOffset(); i < _result.getResultSet().size(); i++) {
/*  46 */         IDrmQueryRow row = (IDrmQueryRow)_result.getResultSet().get(i);
/*  47 */         result.getResultSet().add(row);
/*     */       }
/*     */     }
/*  50 */     return result;
/*     */   }
/*     */ 
/*     */   public void setResultSet(IDrmMemQueryContext queryContext, IDrmQueryResultSet result) {
/*  54 */     RequestDataStore rstore = createRequestStore(queryContext);
/*  55 */     for (int i = 0; (i < queryContext.getMaxBufSize()) && (i < result.getResultSet().size()); i++) {
/*  56 */       result.getResultSet().remove(i);
/*     */     }
/*  58 */     rstore.put(queryContext.getRequestId(), new DataStore(result, null));
/*     */   }
/*     */ 
/*     */   public void setMemoryData(IDrmMemQueryContext queryContext, Object data) {
/*  62 */     RequestDataStore rstore = createRequestStore(queryContext);
/*  63 */     if (rstore != null)
/*  64 */       rstore.put(queryContext.getRequestId(), new DataStore(data, null));
/*     */   }
/*     */ 
/*     */   public <T> T getMemoryData(IDrmMemQueryContext queryContext)
/*     */   {
/*  69 */     if ((queryContext.getSessionId() == null) || (queryContext.getPageId() == null) || (queryContext.getRequestId() == null))
/*     */     {
/*  71 */       return null;
/*     */     }
/*     */ 
/*  74 */     PageDataStore pstore = (PageDataStore)this.sessionDataStore.get(queryContext.getSessionId());
/*  75 */     if (pstore == null) return null;
/*  76 */     RequestDataStore rstore = (RequestDataStore)pstore.get(queryContext.getPageId());
/*  77 */     if (rstore == null) return null;
/*  78 */     DataStore dataStore = (DataStore)rstore.get(queryContext.getRequestId());
/*  79 */     if (dataStore == null) return null;
/*  80 */     return dataStore.data;
/*     */   }
/*     */ 
/*     */   private RequestDataStore createRequestStore(IDrmMemQueryContext queryContext) {
/*  84 */     if ((queryContext.getSessionId() == null) || (queryContext.getPageId() == null) || (queryContext.getRequestId() == null))
/*     */     {
/*  86 */       return null;
/*     */     }
/*     */ 
/*  89 */     PageDataStore pstore = (PageDataStore)this.sessionDataStore.get(queryContext.getSessionId());
/*  90 */     if (pstore == null) {
/*  91 */       pstore = new PageDataStore(null);
/*  92 */       this.sessionDataStore.put(queryContext.getSessionId(), pstore);
/*     */     }
/*  94 */     RequestDataStore rstore = (RequestDataStore)pstore.get(queryContext.getPageId());
/*  95 */     if (rstore == null) {
/*  96 */       rstore = new RequestDataStore(null);
/*  97 */       pstore.put(queryContext.getPageId(), rstore);
/*     */     }
/*  99 */     return rstore;
/*     */   }
/*     */ 
/*     */   public void clearMemoryData(IDrmMemQueryContext queryContext)
/*     */   {
/* 104 */     int clearType = queryContext.getClearType();
/*     */     PageDataStore pstore;
/* 105 */     switch (clearType) {
/*     */     case 3:
/* 107 */       this.sessionDataStore.remove(queryContext.getSessionId());
/* 108 */       break;
/*     */     case 2:
/* 110 */       pstore = (PageDataStore)this.sessionDataStore.get(queryContext.getSessionId());
/* 111 */       if (pstore == null) break;
/* 112 */       pstore.remove(queryContext.getPageId()); break;
/*     */     case 1:
/* 116 */       pstore = (PageDataStore)this.sessionDataStore.get(queryContext.getSessionId());
/* 117 */       if (pstore == null) break;
/* 118 */       RequestDataStore rstore = (RequestDataStore)pstore.get(queryContext.getPageId());
/* 119 */       if (rstore == null) break;
/* 120 */       pstore.remove(queryContext.getRequestId());
/*     */     }
/*     */   }
/*     */   private static class DataStore<T> {
/* 137 */     long refreshTime = System.currentTimeMillis();
/*     */     T data;
/*     */ 
/*     */     private DataStore(T resultSet) {
/* 140 */       this.data = resultSet;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class RequestDataStore extends HashMap<String, MemoryQueryService.DataStore>
/*     */   {
/*     */   }
/*     */ 
/*     */   private static class PageDataStore extends HashMap<String, MemoryQueryService.RequestDataStore>
/*     */   {
/*     */   }
/*     */ 
/*     */   private static class SessionDataStore extends HashMap<String, MemoryQueryService.PageDataStore>
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.impl.MemoryQueryService
 * JD-Core Version:    0.6.0
 */