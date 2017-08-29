/*     */ package com.boco.common.util.db;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AbstractUserTransaction
/*     */   implements UserTransaction
/*     */ {
/*  14 */   private List<ITranscListener> transcListeners = new Vector();
/*     */   private int transcCount;
/*     */   private boolean isBegined;
/*     */   private boolean isCommited;
/*     */   private boolean isRollbacked;
/*     */   private boolean isBeginTransc;
/*     */ 
/*     */   protected void addTranscCount()
/*     */   {
/*  31 */     this.transcCount += 1;
/*     */   }
/*     */ 
/*     */   protected void subTranscCount()
/*     */   {
/*  38 */     if (this.transcCount > 0)
/*  39 */       this.transcCount -= 1;
/*     */   }
/*     */ 
/*     */   protected boolean isExecAction()
/*     */   {
/*  48 */     return this.transcCount <= 0;
/*     */   }
/*     */ 
/*     */   protected void clearCount()
/*     */   {
/*  55 */     this.transcCount = 0;
/*     */   }
/*     */ 
/*     */   public void addTranscListener(ITranscListener listener)
/*     */   {
/*  63 */     this.transcListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void clearTranscListener()
/*     */   {
/*  70 */     this.transcListeners.clear();
/*     */   }
/*     */ 
/*     */   public void setBeginTransc()
/*     */   {
/*  77 */     this.isBeginTransc = true;
/*     */   }
/*     */ 
/*     */   public boolean isBeginTransc()
/*     */   {
/*  85 */     return this.isBeginTransc;
/*     */   }
/*     */ 
/*     */   private void notifyTranscBegin() {
/*  89 */     if (!this.isBegined) {
/*  90 */       this.isBegined = true;
/*  91 */       for (ITranscListener listener : this.transcListeners)
/*  92 */         listener.doTranscBegin(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyTranscCommit()
/*     */   {
/*  98 */     if (!this.isCommited) {
/*  99 */       this.isCommited = true;
/* 100 */       for (ITranscListener listener : this.transcListeners)
/* 101 */         listener.doTranscCommit(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyTranscRollback()
/*     */   {
/* 107 */     if (!this.isRollbacked) {
/* 108 */       this.isRollbacked = true;
/* 109 */       for (ITranscListener listener : this.transcListeners)
/* 110 */         listener.doTranscRollback(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void notifyListener(UserTransaction.TRANSC_STATUS status)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       switch (1.$SwitchMap$com$boco$common$util$db$UserTransaction$TRANSC_STATUS[status.ordinal()]) {
/*     */       case 1:
/* 123 */         notifyTranscBegin();
/* 124 */         break;
/*     */       case 2:
/* 126 */         notifyTranscCommit();
/* 127 */         break;
/*     */       case 3:
/* 129 */         notifyTranscRollback();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Throwable ex)
/*     */     {
/* 135 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.AbstractUserTransaction
 * JD-Core Version:    0.6.0
 */