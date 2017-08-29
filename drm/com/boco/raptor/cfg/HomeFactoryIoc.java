/*    */ package com.boco.raptor.cfg;
/*    */ 
/*    */ import com.boco.raptor.common.misc.RaptorSystem;
/*    */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*    */ 
/*    */ public class HomeFactoryIoc
/*    */ {
/*    */   private static HomeFactoryIoc instance;
/*    */ 
/*    */   public static void createInstance(String iocXmlFile)
/*    */   {
/* 30 */     if (instance == null) {
/* 31 */       iocXmlFile = RaptorSystem.getRaptorHomePath() + iocXmlFile;
/* 32 */       new FileSystemXmlApplicationContext(new String[] { iocXmlFile });
/* 33 */       instance = new HomeFactoryIoc();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.cfg.HomeFactoryIoc
 * JD-Core Version:    0.6.0
 */