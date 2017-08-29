/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutputStream;
/*    */ 
/*    */ public class ObjBufOutputStream
/*    */ {
/*    */   private ObjectOutputStream objOutStream;
/* 11 */   private ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream();
/*    */ 
/* 13 */   public ObjBufOutputStream() throws IOException { this.objOutStream = new ObjectOutputStream(this.bufOutStream); }
/*    */ 
/*    */   public void writeObject(Object value) throws IOException
/*    */   {
/* 17 */     this.objOutStream.writeObject(value);
/*    */   }
/*    */ 
/*    */   public void write(byte[] bytes) throws IOException {
/* 21 */     this.objOutStream.write(bytes);
/*    */   }
/*    */ 
/*    */   public byte[] getBuf() {
/* 25 */     return this.bufOutStream.toByteArray();
/*    */   }
/*    */ 
/*    */   public void close() {
/*    */     try {
/* 30 */       this.objOutStream.close();
/*    */     }
/*    */     catch (IOException ex)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.ObjBufOutputStream
 * JD-Core Version:    0.6.0
 */