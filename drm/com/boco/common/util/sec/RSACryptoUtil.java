/*     */ package com.boco.common.util.sec;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.RSAPrivateKeySpec;
/*     */ import java.security.spec.RSAPublicKeySpec;
/*     */ import java.util.HashMap;
/*     */ import javax.crypto.Cipher;
/*     */ 
/*     */ public class RSACryptoUtil
/*     */ {
/*     */   public static HashMap<String, Object> getKeys()
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  23 */     HashMap map = new HashMap();
/*  24 */     KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/*  25 */     keyPairGen.initialize(1024);
/*  26 */     KeyPair keyPair = keyPairGen.generateKeyPair();
/*  27 */     RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
/*  28 */     RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
/*  29 */     map.put("public", publicKey);
/*  30 */     map.put("private", privateKey);
/*  31 */     return map;
/*     */   }
/*     */ 
/*     */   public static RSAPublicKey getPublicKey(String modulus, String exponent)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       BigInteger b1 = new BigInteger(modulus);
/*  47 */       BigInteger b2 = new BigInteger(exponent);
/*  48 */       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
/*  49 */       RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
/*  50 */       return (RSAPublicKey)keyFactory.generatePublic(keySpec);
/*     */     } catch (Exception e) {
/*  52 */       e.printStackTrace();
/*  53 */     }return null;
/*     */   }
/*     */ 
/*     */   public static RSAPrivateKey getPrivateKey(String modulus, String exponent)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       BigInteger b1 = new BigInteger(modulus);
/*  71 */       BigInteger b2 = new BigInteger(exponent);
/*  72 */       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
/*  73 */       RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
/*  74 */       return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
/*     */     } catch (Exception e) {
/*  76 */       e.printStackTrace();
/*  77 */     }return null;
/*     */   }
/*     */ 
/*     */   public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
/*     */     throws Exception
/*     */   {
/*  91 */     Cipher cipher = Cipher.getInstance("RSA");
/*  92 */     cipher.init(1, publicKey);
/*     */ 
/*  94 */     int key_len = publicKey.getModulus().bitLength() / 8;
/*     */ 
/*  96 */     String[] datas = splitString(data, key_len - 11);
/*  97 */     String mi = "";
/*     */ 
/*  99 */     for (String s : datas) {
/* 100 */       mi = mi + bcd2Str(cipher.doFinal(s.getBytes()));
/*     */     }
/* 102 */     return mi;
/*     */   }
/*     */ 
/*     */   public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
/*     */     throws Exception
/*     */   {
/* 115 */     Cipher cipher = Cipher.getInstance("RSA");
/* 116 */     cipher.init(2, privateKey);
/*     */ 
/* 118 */     int key_len = privateKey.getModulus().bitLength() / 8;
/* 119 */     byte[] bytes = data.getBytes();
/* 120 */     byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
/* 121 */     System.err.println(bcd.length);
/*     */ 
/* 123 */     String ming = "";
/* 124 */     byte[][] arrays = splitArray(bcd, key_len);
/* 125 */     for (byte[] arr : arrays) {
/* 126 */       ming = ming + new String(cipher.doFinal(arr));
/*     */     }
/* 128 */     return ming;
/*     */   }
/*     */ 
/*     */   public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len)
/*     */   {
/* 135 */     byte[] bcd = new byte[asc_len / 2];
/* 136 */     int j = 0;
/* 137 */     for (int i = 0; i < (asc_len + 1) / 2; i++) {
/* 138 */       bcd[i] = asc_to_bcd(ascii[(j++)]);
/* 139 */       bcd[i] = (byte)((j >= asc_len ? 0 : asc_to_bcd(ascii[(j++)])) + (bcd[i] << 4));
/*     */     }
/* 141 */     return bcd;
/*     */   }
/*     */ 
/*     */   public static byte asc_to_bcd(byte asc)
/*     */   {
/*     */     byte bcd;
/*     */     byte bcd;
/* 146 */     if ((asc >= 48) && (asc <= 57)) {
/* 147 */       bcd = (byte)(asc - 48);
/*     */     }
/*     */     else
/*     */     {
/*     */       byte bcd;
/* 148 */       if ((asc >= 65) && (asc <= 70)) {
/* 149 */         bcd = (byte)(asc - 65 + 10);
/*     */       }
/*     */       else
/*     */       {
/*     */         byte bcd;
/* 150 */         if ((asc >= 97) && (asc <= 102))
/* 151 */           bcd = (byte)(asc - 97 + 10);
/*     */         else
/* 153 */           bcd = (byte)(asc - 48); 
/*     */       }
/*     */     }
/* 154 */     return bcd;
/*     */   }
/*     */ 
/*     */   public static String bcd2Str(byte[] bytes)
/*     */   {
/* 160 */     char[] temp = new char[bytes.length * 2];
/*     */ 
/* 162 */     for (int i = 0; i < bytes.length; i++) {
/* 163 */       char val = (char)((bytes[i] & 0xF0) >> 4 & 0xF);
/* 164 */       temp[(i * 2)] = (char)(val > '\t' ? val + 'A' - 10 : val + '0');
/*     */ 
/* 166 */       val = (char)(bytes[i] & 0xF);
/* 167 */       temp[(i * 2 + 1)] = (char)(val > '\t' ? val + 'A' - 10 : val + '0');
/*     */     }
/* 169 */     return new String(temp);
/*     */   }
/*     */ 
/*     */   public static String[] splitString(String string, int len)
/*     */   {
/* 175 */     int x = string.length() / len;
/* 176 */     int y = string.length() % len;
/* 177 */     int z = 0;
/* 178 */     if (y != 0) {
/* 179 */       z = 1;
/*     */     }
/* 181 */     String[] strings = new String[x + z];
/* 182 */     String str = "";
/* 183 */     for (int i = 0; i < x + z; i++) {
/* 184 */       if ((i == x + z - 1) && (y != 0))
/* 185 */         str = string.substring(i * len, i * len + y);
/*     */       else {
/* 187 */         str = string.substring(i * len, i * len + len);
/*     */       }
/* 189 */       strings[i] = str;
/*     */     }
/* 191 */     return strings;
/*     */   }
/*     */ 
/*     */   public static byte[][] splitArray(byte[] data, int len)
/*     */   {
/* 197 */     int x = data.length / len;
/* 198 */     int y = data.length % len;
/* 199 */     int z = 0;
/* 200 */     if (y != 0) {
/* 201 */       z = 1;
/*     */     }
/* 203 */     byte[][] arrays = new byte[x + z][];
/*     */ 
/* 205 */     for (int i = 0; i < x + z; i++) {
/* 206 */       byte[] arr = new byte[len];
/* 207 */       if ((i == x + z - 1) && (y != 0))
/* 208 */         System.arraycopy(data, i * len, arr, 0, y);
/*     */       else {
/* 210 */         System.arraycopy(data, i * len, arr, 0, len);
/*     */       }
/* 212 */       arrays[i] = arr;
/*     */     }
/* 214 */     return arrays;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.sec.RSACryptoUtil
 * JD-Core Version:    0.6.0
 */