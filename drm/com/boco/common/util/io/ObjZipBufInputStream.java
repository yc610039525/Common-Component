/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.util.zip.GZIPInputStream;
/*    */ 
/*    */ public class ObjZipBufInputStream extends ObjectInputStream
/*    */ {
/*    */   public ObjZipBufInputStream(byte[] buf)
/*    */     throws IOException
/*    */   {
/* 11 */     super(new GZIPInputStream(new ByteArrayInputStream(buf)));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.ObjZipBufInputStream
 * JD-Core Version:    0.6.0
 */