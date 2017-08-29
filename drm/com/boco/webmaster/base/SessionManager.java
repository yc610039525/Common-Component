/*    */ package com.boco.webmaster.base;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ 
/*    */ public class SessionManager
/*    */ {
/*    */   private static final String SESSION_MANAGER_TABLE = "sessionManagerTable";
/* 20 */   private static SessionManager instance = new SessionManager();
/*    */ 
/*    */   public static SessionManager getInstance()
/*    */   {
/* 25 */     return instance;
/*    */   }
/*    */ 
/*    */   public void saveQueryAttr(HttpServletRequest request, String attrName, Object value) {
/* 29 */     saveAttribute(request, "QUERY", attrName, value);
/*    */   }
/*    */ 
/*    */   public void savePermanentAttr(HttpServletRequest request, String attrName, Object value) {
/* 33 */     saveAttribute(request, "PERMANENT", attrName, value);
/*    */   }
/*    */ 
/*    */   public void saveAttribute(HttpServletRequest request, String sessionAttrType, String attrName, Object value)
/*    */   {
/* 38 */     Map sessionAttrTable = getSessionAttrTable(request, sessionAttrType);
/* 39 */     if ((attrName != null) && (attrName.trim().length() > 0)) {
/* 40 */       sessionAttrTable.put(attrName, "");
/* 41 */       request.getSession().setAttribute(attrName, value);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void removeQueryAttrs(HttpServletRequest request) {
/* 46 */     removeAttributes(request, "QUERY");
/*    */   }
/*    */ 
/*    */   public void removeAttributes(HttpServletRequest request, String sessionAttrType) {
/* 50 */     Map sessionAttrTable = getSessionAttrTable(request, sessionAttrType);
/* 51 */     Object[] attrNames = sessionAttrTable.keySet().toArray();
/* 52 */     for (int i = 0; i < attrNames.length; i++) {
/* 53 */       request.getSession().removeAttribute((String)attrNames[i]);
/*    */     }
/* 55 */     sessionAttrTable.clear();
/*    */   }
/*    */ 
/*    */   private static Map getSessionAttrTable(HttpServletRequest request, String sessionAttrType) {
/* 59 */     HttpSession session = request.getSession();
/* 60 */     Map sessionManagerTable = (Map)session.getAttribute("sessionManagerTable");
/* 61 */     if (sessionManagerTable == null) {
/* 62 */       sessionManagerTable = new Hashtable();
/* 63 */       session.setAttribute("sessionManagerTable", sessionManagerTable);
/*    */     }
/*    */ 
/* 66 */     Map sessionAttrTable = (Map)sessionManagerTable.get(sessionAttrType);
/* 67 */     if (sessionAttrTable == null) {
/* 68 */       sessionAttrTable = new Hashtable();
/* 69 */       sessionManagerTable.put(sessionAttrType, sessionAttrTable);
/*    */     }
/* 71 */     return sessionAttrTable;
/*    */   }
/*    */ 
/*    */   public static void clearSession(HttpServletRequest request) {
/* 75 */     HttpSession session = request.getSession();
/* 76 */     Enumeration e = session.getAttributeNames();
/* 77 */     List attrNames = new ArrayList();
/* 78 */     while (e.hasMoreElements()) {
/* 79 */       attrNames.add(e.nextElement());
/*    */     }
/*    */ 
/* 82 */     for (int i = 0; i < attrNames.size(); i++)
/* 83 */       session.removeAttribute((String)attrNames.get(i));
/*    */   }
/*    */ 
/*    */   public static class SessionAttrType
/*    */   {
/*    */     public static final String PAMANENT = "PERMANENT";
/*    */     public static final String QUERY = "QUERY";
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.webmaster.base.SessionManager
 * JD-Core Version:    0.6.0
 */