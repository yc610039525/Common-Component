/*    */ package com.boco.transnms.common.cfg;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.lang.SecurityHelper;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class EncryptManager
/*    */ {
/* 14 */   private String encryptKey = "";
/* 15 */   private String encryptType = EncryptType.NONE.toString();
/* 16 */   private List ldapServers = new ArrayList();
/* 17 */   private boolean isLdapServerConnected = false;
/* 18 */   private TnmsApmPwdReader apmPwdReader = null;
/* 19 */   private static EncryptManager instance = new EncryptManager();
/*    */ 
/*    */   public static EncryptManager getInstance() {
/* 22 */     return instance;
/*    */   }
/*    */ 
/*    */   public List getLdapServers()
/*    */   {
/* 29 */     return this.ldapServers;
/*    */   }
/*    */ 
/*    */   public void setLdapServers(List ldapServers) {
/* 33 */     this.ldapServers = ldapServers;
/*    */   }
/*    */ 
/*    */   public String getEncryptType() {
/* 37 */     return this.encryptType;
/*    */   }
/*    */ 
/*    */   public void setEncryptType(String encryptType) {
/* 41 */     this.encryptType = encryptType;
/*    */ 
/* 43 */     if ((encryptType != null) && (encryptType.trim().length() > 0) && (encryptType.equals(EncryptType.LDAP_SERVER.toString())))
/*    */     {
/* 45 */       this.apmPwdReader = new TnmsApmPwdReader();
/* 46 */       String[] params = new String[this.ldapServers.size()];
/* 47 */       this.ldapServers.toArray(params);
/* 48 */       this.isLdapServerConnected = this.apmPwdReader.connectLdap(params);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getDecryptPwd(String password) {
/* 53 */     String passwd = password;
/* 54 */     if ((this.encryptType != null) && (this.encryptType.trim().length() > 0)) {
/* 55 */       String type = this.encryptType.trim();
/* 56 */       if (type.equals(EncryptType.LDAP_SERVER.toString())) {
/* 57 */         if ((this.apmPwdReader != null) && (this.isLdapServerConnected)) {
/* 58 */           passwd = this.apmPwdReader.readApmPwd1(password);
/* 59 */           String passwd2 = "";
/*    */           try {
/* 61 */             String[] a = password.split("/");
/* 62 */             if (a.length == 4)
/* 63 */               passwd2 = this.apmPwdReader.readApmEncryptPwd1(password);
/*    */           }
/*    */           catch (Exception e) {
/* 66 */             passwd2 = "";
/* 67 */             LogHome.getLog().error("执行 passwd2 = apmPwdReader.readApmEncryptPwd1(password) 异常: " + e.getMessage());
/*    */           }
/* 69 */           if ((passwd != null) && (passwd.equals(passwd2)))
/* 70 */             passwd = "";
/*    */         }
/*    */         else {
/* 73 */           LogHome.getLog().error("LDAP Server没有连接，无法获取密码！");
/*    */         }
/* 75 */       } else if (type.equals(EncryptType.LOCAL_ENCRYPT.toString())) {
/* 76 */         passwd = SecurityHelper.getInstance().getDecrypt(password);
/*    */       }
/*    */     }
/* 79 */     return passwd;
/*    */   }
/*    */ 
/*    */   public String getEncryptKey() {
/* 83 */     return this.encryptKey;
/*    */   }
/*    */ 
/*    */   public void setEncryptKey(String encryptKey) {
/* 87 */     if ((encryptKey != null) && (encryptKey.trim().length() > 0)) {
/* 88 */       String proEncryptKey = "";
/*    */       try {
/* 90 */         proEncryptKey = SecurityHelper.getInstance().getDecrypt(encryptKey.trim());
/*    */       } catch (Exception e) {
/* 92 */         LogHome.getLog().error("对配置文件中的密文密钥进行解密时发生异常！" + e.getMessage());
/*    */       }
/* 94 */       if ((proEncryptKey != null) && (proEncryptKey.trim().length() > 0)) {
/* 95 */         SecurityHelper.getInstance().setKEY(proEncryptKey.trim());
/*    */       }
/* 97 */       this.encryptKey = encryptKey.trim();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static enum EncryptType
/*    */   {
/* 11 */     NONE, LOCAL_ENCRYPT, LDAP_SERVER;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.cfg.EncryptManager
 * JD-Core Version:    0.6.0
 */