/*    */ package com.boco.raptor.workflow.xpdl.jawe;
/*    */ 
/*    */ import com.boco.raptor.workflow.xpdl.AbstractXPDLParser;
/*    */ 
/*    */ public class XPDLJaweParser extends AbstractXPDLParser
/*    */ {
/*  6 */   public static String StartActivityId = "StartOfWorkflow";
/*  7 */   public static String EndActivityId = "EndOfWorkflow";
/*  8 */   private static XPDLJaweParser instance = new XPDLJaweParser();
/*    */ 
/*    */   public static XPDLJaweParser getInstance()
/*    */   {
/* 14 */     return instance;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.jawe.XPDLJaweParser
 * JD-Core Version:    0.6.0
 */