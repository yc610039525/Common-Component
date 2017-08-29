/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class DataObjectManager
/*    */ {
/*  9 */   private static final DataObjectManager instance = new DataObjectManager();
/* 10 */   private List<IDataObjectListener> listeners = new ArrayList();
/*    */ 
/*    */   public static DataObjectManager getInstance()
/*    */   {
/* 15 */     return instance;
/*    */   }
/*    */ 
/*    */   public void addListener(IDataObjectListener listener) {
/* 19 */     if (!this.listeners.contains(listener))
/* 20 */       this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void removeListener(IDataObjectListener listener)
/*    */   {
/* 25 */     this.listeners.remove(listener);
/*    */   }
/*    */ 
/*    */   public void notifyChange(AttrObject oldDbo, AttrObject newDbo) {
/* 29 */     for (IDataObjectListener listener : this.listeners)
/*    */       try {
/* 31 */         listener.notifyChange(oldDbo, newDbo);
/*    */       } catch (Throwable ex) {
/* 33 */         LogHome.getLog().error("", ex);
/*    */       }
/*    */   }
/*    */ 
/*    */   public boolean hasListener()
/*    */   {
/* 39 */     return this.listeners.size() > 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DataObjectManager
 * JD-Core Version:    0.6.0
 */