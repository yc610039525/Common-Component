/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.security.Key;
/*     */ import java.security.SecureRandom;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.SecretKeyFactory;
/*     */ import javax.crypto.spec.DESKeySpec;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SecurityHelper
/*     */ {
/*  32 */   private static SecurityHelper instance = new SecurityHelper();
/*     */ 
/*  34 */   private String KEY = "BOCO_TNMS_2008";
/*  35 */   private String CIPHER_TYPE = "DES";
/*  36 */   private String BYTE_TYPE = "GBK";
/*  37 */   private Key key = null;
/*  38 */   private Cipher EnCipher = null;
/*  39 */   private Cipher DeCipher = null;
/*     */ 
/*  41 */   private int DEFAULT_AVAILABLE_TIME = 3;
/*  42 */   private int DEFAULT_REPEAT_COUNT = 8;
/*  43 */   private boolean IS_CONSTRAINT_PASSWORD = true;
/*     */ 
/*     */   public static SecurityHelper getInstance()
/*     */   {
/*  47 */     return instance;
/*     */   }
/*     */ 
/*     */   public boolean isIS_CONSTRAINT_PASSWORD() {
/*  51 */     return this.IS_CONSTRAINT_PASSWORD;
/*     */   }
/*     */ 
/*     */   public int getDEFAULT_REPEAT_COUNT() {
/*  55 */     return this.DEFAULT_REPEAT_COUNT;
/*     */   }
/*     */ 
/*     */   public int getDEFAULT_AVAILABLE_TIME() {
/*  59 */     return this.DEFAULT_AVAILABLE_TIME;
/*     */   }
/*     */ 
/*     */   public String getKEY() {
/*  63 */     return this.KEY;
/*     */   }
/*     */ 
/*     */   public void setDEFAULT_AVAILABLE_TIME(int DEFAULT_AVAILABLE_TIME) {
/*  67 */     this.DEFAULT_AVAILABLE_TIME = DEFAULT_AVAILABLE_TIME;
/*     */   }
/*     */ 
/*     */   public void setDEFAULT_REPEAT_COUNT(int DEFAULT_REPEAT_COUNT) {
/*  71 */     this.DEFAULT_REPEAT_COUNT = DEFAULT_REPEAT_COUNT;
/*     */   }
/*     */ 
/*     */   public void setIS_CONSTRAINT_PASSWORD(boolean IS_CONSTRAINT_PASSWORD) {
/*  75 */     this.IS_CONSTRAINT_PASSWORD = IS_CONSTRAINT_PASSWORD;
/*     */   }
/*     */ 
/*     */   public void setKEY(String KEY) {
/*  79 */     this.KEY = KEY;
/*  80 */     init();
/*     */   }
/*     */ 
/*     */   public SecurityHelper() {
/*  84 */     init();
/*     */   }
/*     */ 
/*     */   public void init() {
/*     */     try {
/*  89 */       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(this.CIPHER_TYPE);
/*  90 */       byte[] keyData = this.KEY.getBytes();
/*  91 */       DESKeySpec dks = new DESKeySpec(keyData);
/*  92 */       SecureRandom sr = new SecureRandom();
/*     */ 
/*  94 */       this.key = keyFactory.generateSecret(dks);
/*     */ 
/*  96 */       this.EnCipher = Cipher.getInstance(this.CIPHER_TYPE);
/*  97 */       this.DeCipher = Cipher.getInstance(this.CIPHER_TYPE);
/*  98 */       this.EnCipher.init(1, this.key, sr);
/*  99 */       this.DeCipher.init(2, this.key, sr);
/*     */     } catch (Exception ex) {
/* 101 */       LogHome.getLog().error("密钥生成失败:", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getEncrypt(String str)
/*     */   {
/* 111 */     String encrypted = null;
/* 112 */     if (str != null) {
/*     */       try {
/* 114 */         encrypted = byte2hex(getEncrypt(str.getBytes()));
/*     */       }
/*     */       catch (Exception e) {
/* 117 */         LogHome.getLog().error("加密失败:", e);
/*     */       }
/*     */     }
/* 120 */     return encrypted;
/*     */   }
/*     */ 
/*     */   private byte[] getEncrypt(byte[] str)
/*     */   {
/* 130 */     byte[] encrypted = null;
/*     */     try {
/* 132 */       encrypted = this.EnCipher.doFinal(str);
/*     */     } catch (Exception e) {
/* 134 */       LogHome.getLog().error("加密失败:", e);
/*     */     }
/* 136 */     return encrypted;
/*     */   }
/*     */ 
/*     */   public String getDecrypt(String str)
/*     */   {
/* 145 */     String dncrypted = null;
/* 146 */     if (str != null) {
/*     */       try {
/* 148 */         dncrypted = new String(getDecrypt(hex2byte(str.getBytes())));
/*     */       } catch (Exception e) {
/* 150 */         LogHome.getLog().error("解密失败:", e);
/*     */       }
/*     */     }
/* 153 */     return dncrypted;
/*     */   }
/*     */ 
/*     */   private byte[] getDecrypt(byte[] b)
/*     */   {
/* 162 */     byte[] dncrypted = null;
/*     */     try {
/* 164 */       dncrypted = this.DeCipher.doFinal(b);
/*     */     } catch (Exception e) {
/* 166 */       LogHome.getLog().error("解密失败:", e);
/*     */     }
/* 168 */     return dncrypted;
/*     */   }
/*     */ 
/*     */   private String byte2hex(byte[] b)
/*     */     throws Exception
/*     */   {
/* 177 */     String hs = "";
/* 178 */     String stmp = "";
/* 179 */     for (int n = 0; n < b.length; n++) {
/* 180 */       stmp = Integer.toHexString(b[n] & 0xFF);
/* 181 */       if (stmp.length() == 1)
/* 182 */         hs = hs + "0" + stmp;
/*     */       else
/* 184 */         hs = hs + stmp;
/*     */     }
/* 186 */     return hs.toUpperCase();
/*     */   }
/*     */ 
/*     */   private byte[] hex2byte(byte[] b) throws Exception {
/* 190 */     if (b.length % 2 != 0) {
/* 191 */       throw new IllegalArgumentException("长度不是偶数");
/*     */     }
/* 193 */     byte[] b2 = new byte[b.length / 2];
/* 194 */     for (int n = 0; n < b.length; n += 2) {
/* 195 */       String item = new String(b, n, 2);
/* 196 */       b2[(n / 2)] = (byte)Integer.parseInt(item, 16);
/*     */     }
/* 198 */     return b2;
/*     */   }
/*     */ 
/*     */   public static boolean isBefor(Timestamp statrTime, Timestamp endTime, int tpye, int count)
/*     */   {
/* 211 */     Calendar start = Calendar.getInstance();
/* 212 */     start.setTime(statrTime);
/* 213 */     Calendar end = Calendar.getInstance();
/* 214 */     end.setTime(endTime);
/* 215 */     start.add(tpye, count);
/* 216 */     return start.before(end);
/*     */   }
/*     */ 
/*     */   public static boolean isBefor(Timestamp statrTime, Timestamp endTime, int monthCount)
/*     */   {
/* 227 */     return isBefor(statrTime, endTime, 2, monthCount);
/*     */   }
/*     */ 
/*     */   public static boolean isBefor(Timestamp statrTime, int monthCount)
/*     */   {
/* 238 */     return isBefor(statrTime, new Timestamp(System.currentTimeMillis()), 2, monthCount);
/*     */   }
/*     */ 
/*     */   public static boolean isNum(String str)
/*     */   {
/* 247 */     boolean isNum = false;
/* 248 */     if ((str != null) && (str.length() > 0)) {
/* 249 */       isNum = str.matches("^([0-9])+$");
/*     */     }
/* 251 */     return isNum;
/*     */   }
/*     */ 
/*     */   public static boolean isContainNum(String str)
/*     */   {
/* 260 */     boolean isContainNum = false;
/* 261 */     if ((str != null) && (str.length() > 0)) {
/* 262 */       isContainNum = str.matches(".*?[0-9].*?");
/*     */     }
/* 264 */     return isContainNum;
/*     */   }
/*     */ 
/*     */   public static boolean isLetter(String str)
/*     */   {
/* 274 */     boolean isLetter = false;
/* 275 */     if ((str != null) && (str.length() > 0)) {
/* 276 */       isLetter = str.matches("^([A-Za-z])+$");
/*     */     }
/* 278 */     return isLetter;
/*     */   }
/*     */ 
/*     */   public static boolean isContainLetter_A(String str)
/*     */   {
/* 287 */     boolean isContainLetter = false;
/* 288 */     if ((str != null) && (str.length() > 0)) {
/* 289 */       isContainLetter = str.matches(".*?[A-Z].*?");
/*     */     }
/* 291 */     return isContainLetter;
/*     */   }
/*     */ 
/*     */   public static boolean isContainLetter_a(String str)
/*     */   {
/* 300 */     boolean isContainLetter = false;
/* 301 */     if ((str != null) && (str.length() > 0)) {
/* 302 */       isContainLetter = str.matches(".*?[a-z].*?");
/*     */     }
/* 304 */     return isContainLetter;
/*     */   }
/*     */ 
/*     */   public static boolean isContainCharacter(String str)
/*     */   {
/* 314 */     boolean isContainCharacter = false;
/* 315 */     if ((str != null) && (str.length() > 0)) {
/* 316 */       isContainCharacter = str.matches(".*?(?:[^A-Za-z0-9]).*?");
/*     */     }
/* 318 */     return isContainCharacter;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.SecurityHelper
 * JD-Core Version:    0.6.0
 */