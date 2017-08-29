/*    */ package com.boco.common.util.xml;
/*    */ 
/*    */ import com.boco.common.util.io.BufInputStream;
/*    */ import java.io.File;
/*    */ import java.io.InputStream;
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Element;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
/*    */ 
/*    */ public class JDomHelper
/*    */ {
/*    */   public static Element getRootElement(InputStream in)
/*    */     throws Exception
/*    */   {
/* 39 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 40 */     DocumentBuilder db = dbf.newDocumentBuilder();
/* 41 */     Document doc = db.parse(in);
/* 42 */     return doc.getDocumentElement();
/*    */   }
/*    */ 
/*    */   public static Element getRootElement(String xmlFilePath) throws Exception {
/* 46 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 47 */     DocumentBuilder db = dbf.newDocumentBuilder();
/* 48 */     Document doc = db.parse(new File(xmlFilePath));
/* 49 */     return doc.getDocumentElement();
/*    */   }
/*    */ 
/*    */   public static Element getRootElement(byte[] xmlBytes) throws Exception {
/* 53 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 54 */     DocumentBuilder db = dbf.newDocumentBuilder();
/* 55 */     Document doc = db.parse(new BufInputStream(xmlBytes));
/* 56 */     return doc.getDocumentElement();
/*    */   }
/*    */ 
/*    */   public static Node getTagValueNode(Element parentEle, String tagName) {
/* 60 */     return parentEle.getElementsByTagName(tagName).item(0).getFirstChild();
/*    */   }
/*    */ 
/*    */   public static String getTagNodeValue(Element parentEle, String tagName) {
/* 64 */     String val = "";
/*    */     try {
/* 66 */       val = parentEle.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
/*    */     }
/*    */     catch (Exception ex) {
/*    */     }
/* 70 */     return val;
/*    */   }
/*    */ 
/*    */   public static String getNodeValue(Node node) {
/* 74 */     String value = "";
/*    */     try {
/* 76 */       value = node.getNodeValue();
/*    */     } catch (Exception ex) {
/*    */     }
/* 79 */     return value;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.xml.JDomHelper
 * JD-Core Version:    0.6.0
 */