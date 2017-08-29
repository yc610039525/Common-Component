/*    */ package com.boco.common.util.sec;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.KeyGenerator;
/*    */ import javax.crypto.SecretKey;
/*    */ import javax.crypto.spec.SecretKeySpec;
/*    */ 
/*    */ public class AESCryptoUtil
/*    */ {
/*    */   private static final String HEX = "0123456789ABCDEF";
/*    */ 
/*    */   public static String encrypt(String seed, String cleartext)
/*    */     throws Exception
/*    */   {
/* 13 */     byte[] rawKey = getRawKey(seed.getBytes());
/* 14 */     byte[] result = encrypt(rawKey, cleartext.getBytes());
/* 15 */     return toHex(result);
/*    */   }
/*    */ 
/*    */   public static String decrypt(String seed, String encrypted) throws Exception
/*    */   {
/* 20 */     byte[] rawKey = getRawKey(seed.getBytes());
/* 21 */     byte[] enc = toByte(encrypted);
/* 22 */     byte[] result = decrypt(rawKey, enc);
/* 23 */     return new String(result);
/*    */   }
/*    */ 
/*    */   private static byte[] getRawKey(byte[] seed) throws Exception {
/* 27 */     KeyGenerator kgen = KeyGenerator.getInstance("AES");
/* 28 */     SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
/* 29 */     sr.setSeed(seed);
/* 30 */     kgen.init(128, sr);
/* 31 */     SecretKey skey = kgen.generateKey();
/* 32 */     byte[] raw = skey.getEncoded();
/* 33 */     return raw;
/*    */   }
/*    */ 
/*    */   private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
/* 37 */     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
/* 38 */     Cipher cipher = Cipher.getInstance("AES");
/* 39 */     cipher.init(1, skeySpec);
/* 40 */     byte[] encrypted = cipher.doFinal(clear);
/* 41 */     return encrypted;
/*    */   }
/*    */ 
/*    */   private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception
/*    */   {
/* 46 */     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
/* 47 */     Cipher cipher = Cipher.getInstance("AES");
/* 48 */     cipher.init(2, skeySpec);
/* 49 */     byte[] decrypted = cipher.doFinal(encrypted);
/* 50 */     return decrypted;
/*    */   }
/*    */ 
/*    */   public static String toHex(String txt) {
/* 54 */     return toHex(txt.getBytes());
/*    */   }
/*    */ 
/*    */   public static String fromHex(String hex) {
/* 58 */     return new String(toByte(hex));
/*    */   }
/*    */ 
/*    */   public static byte[] toByte(String hexString) {
/* 62 */     int len = hexString.length() / 2;
/* 63 */     byte[] result = new byte[len];
/* 64 */     for (int i = 0; i < len; i++) {
/* 65 */       result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
/*    */     }
/* 67 */     return result;
/*    */   }
/*    */ 
/*    */   public static String toHex(byte[] buf) {
/* 71 */     if (buf == null)
/* 72 */       return "";
/* 73 */     StringBuffer result = new StringBuffer(2 * buf.length);
/* 74 */     for (int i = 0; i < buf.length; i++) {
/* 75 */       appendHex(result, buf[i]);
/*    */     }
/* 77 */     return result.toString();
/*    */   }
/*    */ 
/*    */   private static void appendHex(StringBuffer sb, byte b)
/*    */   {
/* 83 */     sb.append("0123456789ABCDEF".charAt(b >> 4 & 0xF)).append("0123456789ABCDEF".charAt(b & 0xF));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.sec.AESCryptoUtil
 * JD-Core Version:    0.6.0
 */