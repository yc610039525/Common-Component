/*    */ package com.boco.common.util.io;
/*    */ 
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.ImageIcon;
/*    */ 
/*    */ public class ResourceLoader
/*    */ {
/*    */   public static Icon loadImageIcon(String iconPath)
/*    */   {
/* 11 */     return new ImageIcon(ResourceLoader.class.getClassLoader().getResource(iconPath));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.io.ResourceLoader
 * JD-Core Version:    0.6.0
 */