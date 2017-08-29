/*    */ package com.boco.raptor.drm.core.web.action;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ 
/*    */ public class SqlFilter extends HttpServlet
/*    */   implements Filter
/*    */ {
/*    */   private FilterConfig filterConfig;
/*    */   private String charset;
/*    */ 
/*    */   public void init(FilterConfig filterConfig)
/*    */     throws ServletException
/*    */   {
/* 24 */     this.filterConfig = filterConfig;
/* 25 */     this.charset = filterConfig.getInitParameter("charset");
/*    */   }
/*    */ 
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
/*    */   {
/*    */     try {
/* 31 */       if ((this.charset != null) && (this.charset.trim().length() > 0)) {
/* 32 */         request.setCharacterEncoding(this.charset);
/*    */       }
/* 34 */       HttpServletRequest req = (HttpServletRequest)request;
/* 35 */       Map map = request.getParameterMap();
/* 36 */       Set keys = map.keySet();
/* 37 */       Iterator it = keys.iterator();
/* 38 */       while (it.hasNext()) {
/* 39 */         Object key = it.next();
/* 40 */         Object value = map.get(key);
/* 41 */         if ((value instanceof String[])) {
/* 42 */           String[] strs = (String[])(String[])value;
/* 43 */           if ((strs != null) && (strs.length > 0) && (strs[0] != null) && (strs[0].trim().length() > 0)) {
/* 44 */             strs[0] = StringUtils.replace(strs[0].toString(), "'", "''");
/*    */           }
/*    */         }
/*    */       }
/*    */ 
/* 49 */       filterChain.doFilter(request, response);
/*    */     } catch (ServletException sx) {
/* 51 */       this.filterConfig.getServletContext().log(sx.getMessage());
/*    */     } catch (IOException iox) {
/* 53 */       this.filterConfig.getServletContext().log(iox.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.SqlFilter
 * JD-Core Version:    0.6.0
 */