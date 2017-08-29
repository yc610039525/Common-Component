/*    */ package com.boco.webmaster.base;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ 
/*    */ public class CharsetFilter extends HttpServlet
/*    */   implements Filter
/*    */ {
/*    */   private FilterConfig filterConfig;
/*    */   private String charset;
/*    */ 
/*    */   public void init(FilterConfig filterConfig)
/*    */     throws ServletException
/*    */   {
/* 18 */     this.filterConfig = filterConfig;
/* 19 */     this.charset = filterConfig.getInitParameter("charset");
/*    */   }
/*    */ 
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
/*    */   {
/*    */     try {
/* 25 */       if ((this.charset != null) && (this.charset.trim().length() > 0)) {
/* 26 */         request.setCharacterEncoding(this.charset);
/*    */       }
/* 28 */       filterChain.doFilter(request, response);
/*    */     } catch (ServletException sx) {
/* 30 */       this.filterConfig.getServletContext().log(sx.getMessage());
/*    */     } catch (IOException iox) {
/* 32 */       this.filterConfig.getServletContext().log(iox.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.webmaster.base.CharsetFilter
 * JD-Core Version:    0.6.0
 */