/*    */ package com.boco.transnms.common.cfg;
/*    */ 
/*    */ import com.boco.ldap.ApmPwdReader;
/*    */ 
/*    */ public class TnmsApmPwdReader extends ApmPwdReader
/*    */ {
/*    */   public boolean connectLdap(String[] ldapServers)
/*    */   {
/*  8 */     String ldaps = "";
/*  9 */     for (int i = 0; i < ldapServers.length; i++) {
/* 10 */       String temp = ldapServers[i];
/* 11 */       ldaps = ldaps + temp + ";";
/*    */     }
/* 13 */     ldaps = ldaps + ldaps;
/* 14 */     super.setParameter("ldap_servers", ldaps);
/* 15 */     return super.connectLdap();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.cfg.TnmsApmPwdReader
 * JD-Core Version:    0.6.0
 */