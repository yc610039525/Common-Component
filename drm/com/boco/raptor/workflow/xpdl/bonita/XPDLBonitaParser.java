/*    */ package com.boco.raptor.workflow.xpdl.bonita;
/*    */ 
/*    */ import com.boco.raptor.workflow.xpdl.AbstractXPDLParser;
/*    */ 
/*    */ public class XPDLBonitaParser extends AbstractXPDLParser
/*    */ {
/*  6 */   public static String StartActivityId = "BonitaInit";
/*  7 */   public static String EndActivityId = "BonitaEnd";
/*  8 */   private static XPDLBonitaParser instance = new XPDLBonitaParser();
/*    */ 
/*    */   public static XPDLBonitaParser getInstance()
/*    */   {
/* 14 */     return instance;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.bonita.XPDLBonitaParser
 * JD-Core Version:    0.6.0
 */