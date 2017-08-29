/*    */ package com.boco.transnms.server.dao.base.internal;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ public class OIdAccount
/*    */ {
/*    */   private AtomicLong oid;
/*    */ 
/*    */   public OIdAccount(long init)
/*    */   {
/*  8 */     this.oid = new AtomicLong(init);
/*    */   }
/*    */ 
/*    */   public synchronized long getNext(int num) {
/* 12 */     return this.oid.addAndGet(num);
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.OIdAccount
 * JD-Core Version:    0.6.0
 */