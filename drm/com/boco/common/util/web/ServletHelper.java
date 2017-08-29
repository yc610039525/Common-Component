/*    */ package com.boco.common.util.web;
/*    */ 
/*    */ import javax.servlet.http.Cookie;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ public class ServletHelper
/*    */ {
/*    */   public static Cookie getCookie(HttpServletRequest request, String cookieName)
/*    */   {
/* 30 */     Cookie cookie = null;
/*    */ 
/* 32 */     Cookie[] cookies = request.getCookies();
/* 33 */     if (cookies != null) {
/* 34 */       for (int i = 0; i < cookies.length; i++) {
/* 35 */         if (cookieName.equals(cookies[i].getName())) {
/* 36 */           cookie = cookies[i];
/* 37 */           break;
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 42 */     return cookie;
/*    */   }
/*    */ 
/*    */   public static String getRequestUri(HttpServletRequest request) {
/* 46 */     StringBuffer uri = new StringBuffer("");
/* 47 */     return uri.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.web.ServletHelper
 * JD-Core Version:    0.6.0
 */