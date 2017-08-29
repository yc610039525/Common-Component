/*    */ package com.boco.transnms.common.bussiness.helper;
/*    */ 
/*    */ import java.beans.XMLDecoder;
/*    */ import java.beans.XMLEncoder;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ public class XmlHelper
/*    */ {
/*    */   public static final String SAVED_XML_VERSION = "1-1-1";
/*    */ 
/*    */   public static byte[] writeByXML(List list)
/*    */     throws IOException
/*    */   {
/* 28 */     byte[] res = null;
/*    */     try {
/* 30 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 31 */       XMLEncoder encode = new XMLEncoder(out);
/* 32 */       encode.writeObject(list);
/* 33 */       encode.close();
/* 34 */       res = out.toByteArray();
/*    */     } catch (Exception ex) {
/* 36 */       ex.printStackTrace();
/*    */     }
/* 38 */     return res;
/*    */   }
/*    */ 
/*    */   public static Object readByXML(byte[] bytes) throws Exception {
/*    */     try {
/* 43 */       ByteArrayInputStream in = new ByteArrayInputStream(bytes);
/* 44 */       XMLDecoder decode = new XMLDecoder(in);
/* 45 */       Object obj = decode.readObject();
/* 46 */       decode.close();
/* 47 */       return obj;
/*    */     } catch (ArrayIndexOutOfBoundsException e) {
/*    */     }
/*    */     catch (Exception ex) {
/* 51 */       ex.printStackTrace();
/*    */     }
/* 53 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.bussiness.helper.XmlHelper
 * JD-Core Version:    0.6.0
 */