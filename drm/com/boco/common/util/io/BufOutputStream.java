/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataOutputStream;
/*    */ 
/*    */ public class BufOutputStream extends DataOutputStream
/*    */   implements Cloneable
/*    */ {
/*    */   public BufOutputStream()
/*    */   {
/* 26 */     super(new ByteArrayOutputStream());
/*    */   }
/*    */ 
/*    */   public BufOutputStream(int size) {
/* 30 */     super(new ByteArrayOutputStream(size));
/*    */   }
/*    */ 
/*    */   public byte[] getBuf() {
/* 34 */     return ((ByteArrayOutputStream)this.out).toByteArray();
/*    */   }
/*    */ 
/*    */   public int getAvailableSize() {
/* 38 */     return ((ByteArrayOutputStream)this.out).size();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.BufOutputStream
 * JD-Core Version:    0.6.0
 */