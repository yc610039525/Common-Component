/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ 
/*    */ public class ObjBufInputStream extends ObjectInputStream
/*    */ {
/*    */   public ObjBufInputStream(byte[] buf)
/*    */     throws IOException
/*    */   {
/* 11 */     super(new ByteArrayInputStream(buf));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.ObjBufInputStream
 * JD-Core Version:    0.6.0
 */