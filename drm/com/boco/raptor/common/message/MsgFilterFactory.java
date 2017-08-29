/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class MsgFilterFactory
/*    */ {
/* 25 */   private static MsgFilterFactory instance = new MsgFilterFactory();
/* 26 */   private Map<String, IMessageFilter> filterTemplateTable = new ConcurrentHashMap();
/*    */ 
/*    */   public static MsgFilterFactory getInstance()
/*    */   {
/* 32 */     return instance;
/*    */   }
/*    */ 
/*    */   public void setMsgFilterTemplates(List<IMessageFilter> filterTemplates) {
/* 36 */     for (IMessageFilter filterTemplate : filterTemplates)
/* 37 */       this.filterTemplateTable.put(filterTemplate.getFilterName(), filterTemplate);
/*    */   }
/*    */ 
/*    */   public IMessageFilter createMsgFilter(String filterTemplateName, Object filterPara)
/*    */   {
/* 42 */     IMessageFilter filter = null;
/* 43 */     IMessageFilter filterTemplate = (IMessageFilter)this.filterTemplateTable.get(filterTemplateName);
/* 44 */     if (filterTemplate != null) {
/* 45 */       filter = filterTemplate.cloneFilter();
/* 46 */       filter.setFilterPara(filterPara);
/*    */     }
/* 48 */     return filter;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MsgFilterFactory
 * JD-Core Version:    0.6.0
 */