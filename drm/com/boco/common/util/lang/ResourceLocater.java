/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.URL;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ public class ResourceLocater
/*    */ {
/*    */   public static String[] getResource(String fileName)
/*    */   {
/* 30 */     ResourceLocater _instance = new ResourceLocater();
/* 31 */     String[] resources = null;
/*    */     try {
/* 33 */       ArrayList res = new ArrayList();
/* 34 */       Enumeration systemResources = ResourceLocater.class.getClassLoader().getResources(fileName);
/*    */ 
/* 36 */       while (systemResources.hasMoreElements()) {
/* 37 */         URL nextElement = (URL)systemResources.nextElement();
/* 38 */         System.out.println(nextElement);
/* 39 */         res.add(nextElement.toString());
/*    */       }
/* 41 */       resources = new String[res.size()];
/* 42 */       for (int i = 0; i < res.size(); i++)
/* 43 */         resources[i] = ((String)res.get(i));
/*    */     }
/*    */     catch (Exception e) {
/* 46 */       e.printStackTrace();
/*    */     }
/* 48 */     return resources;
/*    */   }
/*    */ 
/*    */   public static URL[] getResourceURL(String fileName) {
/* 52 */     ResourceLocater _instance = new ResourceLocater();
/* 53 */     URL[] resources = null;
/*    */     try {
/* 55 */       ArrayList res = new ArrayList();
/* 56 */       Enumeration systemResources = ResourceLocater.class.getClassLoader().getResources(fileName);
/*    */ 
/* 58 */       while (systemResources.hasMoreElements()) {
/* 59 */         URL nextElement = (URL)systemResources.nextElement();
/* 60 */         System.out.println(nextElement);
/* 61 */         res.add(nextElement);
/*    */       }
/* 63 */       resources = new URL[res.size()];
/* 64 */       for (int i = 0; i < res.size(); i++)
/* 65 */         resources[i] = ((URL)res.get(i));
/*    */     }
/*    */     catch (Exception e) {
/* 68 */       e.printStackTrace();
/*    */     }
/* 70 */     return resources;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try {
/* 76 */       boXmlFiles = getResource("amrtubo.xml");
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */       String[] boXmlFiles;
/* 80 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.ResourceLocater
 * JD-Core Version:    0.6.0
 */