/*    */ package com.boco.common.util.lang;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class AbstractQueueThreadHandler<E>
/*    */   implements IQueueThreadHandler<E>
/*    */ {
/*    */   public void handle(String threadPoolName, List<E> elements)
/*    */   {
/* 10 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 11 */       handle(threadPoolName, element); }
/*    */   }
/*    */ 
/*    */   public boolean isSyncWait(E element1, E element2)
/*    */   {
/* 16 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isSyncWait(E element, List<E> handlingElements) {
/* 20 */     return false;
/*    */   }
/*    */ 
/*    */   public Object getElementKey(E element) {
/* 24 */     return null;
/*    */   }
/*    */ 
/*    */   public void notifyQueueLock()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void notifyQueueUnlock()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.AbstractQueueThreadHandler
 * JD-Core Version:    0.6.0
 */