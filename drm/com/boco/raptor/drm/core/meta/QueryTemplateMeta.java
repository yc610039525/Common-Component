/*    */ package com.boco.raptor.drm.core.meta;
/*    */ 
/*    */ import com.boco.raptor.drm.core.service.vm.TemplateExtEnum.QueryTemplate;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class QueryTemplateMeta extends TemplateMeta
/*    */ {
/*    */   public String getSqlCondTemplate()
/*    */   {
/* 10 */     return (String)super.getTemplateExtMetas().get(TemplateExtEnum.QueryTemplate.SQL_TEMPLATE);
/*    */   }
/*    */ 
/*    */   public void setSqlCondTemplate(String sqlCondTemplate) {
/* 14 */     super.getTemplateExtMetas().put(TemplateExtEnum.QueryTemplate.SQL_TEMPLATE, sqlCondTemplate);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.QueryTemplateMeta
 * JD-Core Version:    0.6.0
 */