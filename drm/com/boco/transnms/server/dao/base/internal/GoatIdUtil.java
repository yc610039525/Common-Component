/*     */ package com.boco.transnms.server.dao.base.internal;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class GoatIdUtil
/*     */ {
/*     */   public static void int2byte(byte[] source, int num)
/*     */   {
/*  17 */     for (int i = 0; i < source.length; i++)
/*  18 */       source[i] = (byte)(num >> 8 * i & 0xFF);
/*     */   }
/*     */ 
/*     */   public static void long2byte(byte[] source, long num)
/*     */   {
/*  28 */     for (int i = 0; i < 8; i++)
/*  29 */       source[i] = (byte)(int)(num >> 8 * i & 0xFF);
/*     */   }
/*     */ 
/*     */   public static int byte2int(byte[] buf, boolean asc)
/*     */   {
/*  40 */     int r = 0;
/*  41 */     if (asc)
/*  42 */       for (int i = buf.length - 1; i >= 0; i--) {
/*  43 */         r <<= 8;
/*  44 */         r |= buf[i] & 0xFF;
/*     */       }
/*     */     else {
/*  47 */       for (int i = 0; i < buf.length; i++) {
/*  48 */         r <<= 8;
/*  49 */         r |= buf[i] & 0xFF;
/*     */       }
/*     */     }
/*  52 */     return r;
/*     */   }
/*     */ 
/*     */   public static long byte2long(byte[] buf, boolean asc)
/*     */   {
/*  62 */     long r = 0L;
/*  63 */     if (asc)
/*  64 */       for (int i = buf.length - 1; i >= 0; i--) {
/*  65 */         r <<= 8;
/*  66 */         r |= buf[i] & 0xFF;
/*     */       }
/*     */     else {
/*  69 */       for (int i = 0; i < buf.length; i++) {
/*  70 */         r <<= 8;
/*  71 */         r |= buf[i] & 0xFF;
/*     */       }
/*     */     }
/*  74 */     return r;
/*     */   }
/*     */ 
/*     */   public static long getNewIdByByte(byte[] cid, byte[] oid)
/*     */   {
/*  84 */     byte[] newid = new byte[8];
/*  85 */     for (int i = 0; i < 5; i++) {
/*  86 */       newid[i] = oid[i];
/*     */     }
/*  88 */     for (int i = 0; i < 3; i++) {
/*  89 */       newid[(5 + i)] = cid[i];
/*     */     }
/*  91 */     return byte2long(newid, true);
/*     */   }
/*     */ 
/*     */   public static long getNewIdByByte(byte[] cid, byte[] sid, byte[] oid) {
/*  95 */     byte[] newid = new byte[8];
/*  96 */     for (int i = 0; i < 4; i++) {
/*  97 */       newid[i] = oid[i];
/*     */     }
/*  99 */     newid[4] = sid[0];
/*     */ 
/* 101 */     for (int i = 0; i < 3; i++) {
/* 102 */       newid[(5 + i)] = cid[i];
/*     */     }
/* 104 */     return byte2long(newid, true);
/*     */   }
/*     */ 
/*     */   public static long getNewId(int clsId, long objId)
/*     */   {
/* 114 */     byte[] cidByte = new byte[4];
/* 115 */     byte[] oidByte = new byte[8];
/* 116 */     int2byte(cidByte, clsId);
/* 117 */     long2byte(oidByte, objId);
/* 118 */     return getNewIdByByte(cidByte, oidByte);
/*     */   }
/*     */ 
/*     */   public static long getNewId(int serverId, int clsId, long objId) {
/* 122 */     byte[] cidByte = new byte[4];
/* 123 */     byte[] sidByte = new byte[1];
/* 124 */     byte[] oidByte = new byte[8];
/* 125 */     int2byte(cidByte, clsId);
/* 126 */     int2byte(sidByte, serverId);
/* 127 */     long2byte(oidByte, objId);
/* 128 */     return getNewIdByByte(cidByte, sidByte, oidByte);
/*     */   }
/*     */ 
/*     */   public static int getClassIdByOid(long objId)
/*     */   {
/* 137 */     byte[] cidByte = new byte[4];
/* 138 */     byte[] oidByte = new byte[8];
/* 139 */     long2byte(oidByte, objId);
/* 140 */     cidByte[0] = oidByte[5];
/* 141 */     cidByte[1] = oidByte[6];
/* 142 */     cidByte[2] = oidByte[7];
/* 143 */     cidByte[3] = 0;
/* 144 */     return byte2int(cidByte, true);
/*     */   }
/*     */ 
/*     */   public static int getServerIdByOid(long objId)
/*     */   {
/* 153 */     byte[] sidByte = new byte[1];
/* 154 */     byte[] oidByte = new byte[8];
/* 155 */     long2byte(oidByte, objId);
/* 156 */     sidByte[0] = oidByte[4];
/* 157 */     return byte2int(sidByte, true);
/*     */   }
/*     */ 
/*     */   public static int getObjectIdByOid(long objId)
/*     */   {
/* 167 */     byte[] oidByte = new byte[8];
/* 168 */     long2byte(oidByte, objId);
/* 169 */     oidByte[5] = 0;
/* 170 */     oidByte[6] = 0;
/* 171 */     oidByte[7] = 0;
/* 172 */     return byte2int(oidByte, true);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 176 */     long id = 68165428674001L;
/* 177 */     GoatOidAccount.getGoatOidAccount().setInit(30, id);
/* 178 */     System.out.println(GoatOidAccount.getGoatOidAccount().getNextId(8, 30));
/* 179 */     System.out.println(getClassIdByOid(id));
/* 180 */     System.out.println(getServerIdByOid(id));
/* 181 */     System.out.println(getObjectIdByOid(id));
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.internal.GoatIdUtil
 * JD-Core Version:    0.6.0
 */