/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.beans.XMLEncoder;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class XMLWriteUtils
/*    */ {
/*    */   public static void writeObject(String fileName, Map propertyMap)
/*    */   {
/*    */     try
/*    */     {
/* 38 */       File file = new File(fileName);
/* 39 */       XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
/* 40 */       encoder.writeObject(propertyMap);
/* 41 */       encoder.close();
/*    */     } catch (Exception ex) {
/* 43 */       throw new UserException(ex.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.XMLWriteUtils
 * JD-Core Version:    0.6.0
 */