/*    */ package com.boco.raptor.common.message;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.io.ObjZipBufInputStream;
/*    */ import com.boco.transnms.common.dto.base.GenericDO;
/*    */ import java.util.ArrayList;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class MsgUtils
/*    */ {
/*    */   public static ArrayList resolveMessage(IMessage msg)
/*    */   {
/* 11 */     ArrayList list = new ArrayList();
/*    */     try {
/* 13 */       Object object = msg.getDataObject();
/*    */ 
/* 15 */       if ((object instanceof GenericDO)) {
/* 16 */         list.add(object);
/*    */       } else {
/* 18 */         byte[] dataObject = (byte[])(byte[])object;
/* 19 */         ObjZipBufInputStream in = new ObjZipBufInputStream(dataObject);
/* 20 */         Object obj = in.readObject();
/* 21 */         if ((obj instanceof ArrayList)) {
/* 22 */           list = (ArrayList)obj;
/*    */         }
/* 24 */         in.close();
/*    */       }
/*    */     } catch (Exception e) {
/* 27 */       LogHome.getLog().error("解析消息失败", e);
/*    */     }
/* 29 */     return list;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.MsgUtils
 * JD-Core Version:    0.6.0
 */