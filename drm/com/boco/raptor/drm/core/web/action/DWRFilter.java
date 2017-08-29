/*    */ package com.boco.raptor.drm.core.web.action;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ import org.directwebremoting.AjaxFilter;
/*    */ import org.directwebremoting.AjaxFilterChain;
/*    */ 
/*    */ public class DWRFilter
/*    */   implements AjaxFilter
/*    */ {
/*    */   public Object doFilter(Object object, Method method, Object[] arr, AjaxFilterChain chain)
/*    */     throws Exception
/*    */   {
/* 18 */     System.out.println("拦截目标方法：" + method.getName());
/* 19 */     if ((arr != null) && (arr.length > 0))
/*    */     {
/* 21 */       for (int i = 1; i < arr.length; i++) {
/* 22 */         Object dbo = arr[i];
/* 23 */         if (dbo != null) {
/* 24 */           if (dbo.getClass().equals(String.class)) {
/* 25 */             String timeStr = dbo.toString();
/* 26 */             if ((timeStr != null) && (!timeStr.equals("")) && (!timeStr.equals("null")))
/* 27 */               arr[i] = StringUtils.replace(arr[i].toString(), "'", "''");
/*    */           }
/* 29 */           else if (dbo.getClass().equals(HashMap.class)) {
/* 30 */             Set set = ((HashMap)dbo).keySet();
/* 31 */             Iterator it = set.iterator();
/* 32 */             while (it.hasNext()) {
/* 33 */               String key = (String)it.next();
/* 34 */               String value = (String)((HashMap)dbo).get(key);
/* 35 */               if ((value != null) && (!value.equals("")) && (!value.equals("null")) && 
/* 36 */                 (value.indexOf("'") >= 0)) {
/* 37 */                 value = StringUtils.replace(value.trim(), "'", "''");
/* 38 */                 ((HashMap)dbo).put(key, value);
/*    */               }
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 46 */     Object obj = chain.doFilter(object, method, arr);
/* 47 */     System.out.println("目标方法" + method.getName() + "执行结束");
/* 48 */     return obj;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.action.DWRFilter
 * JD-Core Version:    0.6.0
 */