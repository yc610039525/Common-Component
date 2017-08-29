/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import com.boco.common.util.except.UserException;
/*    */ import java.beans.XMLDecoder;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class XMLReadUtils
/*    */ {
/*    */   public static Map readObject(File file)
/*    */   {
/* 31 */     XMLDecoder d = null;
/*    */     try {
/* 33 */       d = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
/* 34 */       Object result = d.readObject();
/* 35 */       d.close();
/* 36 */       if ((result instanceof Map))
/* 37 */         return (Map)result;
/*    */     }
/*    */     catch (FileNotFoundException e) {
/* 40 */       throw new UserException(e.getMessage());
/*    */     }
/* 42 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.XMLReadUtils
 * JD-Core Version:    0.6.0
 */