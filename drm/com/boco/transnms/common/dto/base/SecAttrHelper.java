/*    */ package com.boco.transnms.common.dto.base;
/*    */ 
/*    */ import com.boco.common.util.lang.SystemEnv;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class SecAttrHelper
/*    */ {
/* 15 */   private static HashMap<String, String> secAttrAndPass = null;
/*    */   public static final String SECKey = "SK";
/*    */ 
/*    */   public static String getPassKey(String tabAndAttrName)
/*    */   {
/* 59 */     if (secAttrAndPass != null) {
/* 60 */       return (String)secAttrAndPass.get(tabAndAttrName);
/*    */     }
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 20 */     String tnmsResIs = SystemEnv.getPathEnv("TNMS_RESOURCE");
/* 21 */     if ((tnmsResIs != null) && (tnmsResIs.equals("OTHER"))) {
/* 22 */       secAttrAndPass = new HashMap();
/* 23 */       secAttrAndPass.put("CTP_TO_TRANSPATH.RELATED_TRANSPATH_CUID", "trpath.CUID@boco.com.cn");
/* 24 */       secAttrAndPass.put("TRANSPATH_TO_TRAPH.RELATED_TRANSPATH_CUID", "trpath.CUID@boco.com.cn");
/* 25 */       secAttrAndPass.put("PATH_ROUTE_POINT.RELATED_PATH_CUID", "trpath.CUID@boco.com.cn");
/*    */ 
/* 27 */       secAttrAndPass.put("CTP_TO_TRANSPATH.RELATED_SUB_PATH_CUID", "trsubpath.CUID@boco.com.cn");
/* 28 */       secAttrAndPass.put("PATH_ROUTE_POINT.RELATED_SUB_PATH_CUID", "trsubpath.CUID@boco.com.cn");
/*    */ 
/* 30 */       secAttrAndPass.put("CROSSCONNECT.ORIG_POINT_CUID", "trans.CTP@boco.com.cn");
/* 31 */       secAttrAndPass.put("CROSSCONNECT.DEST_POINT_CUID", "trans.CTP@boco.com.cn");
/* 32 */       secAttrAndPass.put("CTP_TO_TRANSPATH.RELATED_CTP_CUID", "trans.CTP@boco.com.cn");
/* 33 */       secAttrAndPass.put("TRANS_PATH.ORIG_POINT_CUID", "trans.CTP@boco.com.cn");
/* 34 */       secAttrAndPass.put("TRANS_PATH.DEST_POINT_CUID", "trans.CTP@boco.com.cn");
/* 35 */       secAttrAndPass.put("TRANS_SUB_PATH.ORIG_POINT_CUID", "trans.CTP@boco.com.cn");
/* 36 */       secAttrAndPass.put("TRANS_SUB_PATH.DEST_POINT_CUID", "trans.CTP@boco.com.cn");
/* 37 */       secAttrAndPass.put("PATH_ROUTE_POINT.ORIG_POINT_CUID", "trans.CTP@boco.com.cn");
/* 38 */       secAttrAndPass.put("PATH_ROUTE_POINT.DEST_POINT_CUID", "trans.CTP@boco.com.cn");
/*    */ 
/* 40 */       secAttrAndPass.put("PTN_IP_CROSSCONNECT.ORIG_CARD_CUID", "trans.card@boco.com.cn");
/* 41 */       secAttrAndPass.put("PTN_IP_CROSSCONNECT.DEST_CARD_CUID", "trans.card@boco.com.cn");
/*    */ 
/* 43 */       secAttrAndPass.put("TRAPH_ROUTE.RELATED_SERVICE_CUID", "trans.TR@boco.com.cn");
/*    */ 
/* 45 */       secAttrAndPass.put("PATH_ROUTE_POINT.ORIG_PTP_CUID", "trans.PTP@boco.com.cn");
/* 46 */       secAttrAndPass.put("PATH_ROUTE_POINT.DEST_PTP_CUID", "trans.PTP@boco.com.cn");
/* 47 */       secAttrAndPass.put("PTN_IP_CROSSCONNECT.DEST_PTP_CUID", "trans.PTP@boco.com.cn");
/* 48 */       secAttrAndPass.put("PTN_IP_CROSSCONNECT.ORIG_PTP_CUID", "trans.PTP@boco.com.cn");
/* 49 */       secAttrAndPass.put("CTP_TO_TRANSPATH.RELATED_PTP_CUID", "trans.PTP@boco.com.cn");
/*    */ 
/* 51 */       secAttrAndPass.put("CTP.RELATED_PTP_CUID", "trans.PTP@boco.com.cn");
/* 52 */       secAttrAndPass.put("CROSSCONNECT.ORIG_PTP_CUID", "trans.PTP@boco.com.cn");
/* 53 */       secAttrAndPass.put("CROSSCONNECT.DEST_PTP_CUID", "trans.PTP@boco.com.cn");
/* 54 */       secAttrAndPass.put("OPTICAL_ROUTE.RELATED_SERVICE_CUID", "trans.OR@boco.com.cn");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.SecAttrHelper
 * JD-Core Version:    0.6.0
 */