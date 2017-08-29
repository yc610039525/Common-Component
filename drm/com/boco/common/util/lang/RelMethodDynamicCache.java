/*     */ package com.boco.common.util.lang;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class RelMethodDynamicCache
/*     */ {
/*  28 */   private static final RelMethodDynamicCache instance = new RelMethodDynamicCache();
/*     */   private Map relationDirect;
/*     */   private Map sqlString;
/*     */   private Map relationClassByLink;
/*     */   private Map bindClassName;
/*     */   private Map relationGroup;
/*     */ 
/*     */   public static RelMethodDynamicCache getInstance()
/*     */   {
/*  41 */     return instance;
/*     */   }
/*     */ 
/*     */   public List getRelationDirect(String schemaName, String objectName, int relationType, int relationRole)
/*     */   {
/*  47 */     List result = null;
/*  48 */     RelDataInfo relDataInfo = new RelDataInfo(schemaName, objectName, relationType, relationRole);
/*  49 */     if (this.relationDirect.containsKey(relDataInfo))
/*     */     {
/*  51 */       if (LogHome.getLog().isDebugEnabled()) {
/*  52 */         LogHome.getLog().debug("hit cache by getRelationDirect");
/*     */       }
/*  54 */       result = (List)this.relationDirect.get(relDataInfo);
/*  55 */       if (result != null) {
/*  56 */         return result;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  64 */     return result;
/*     */   }
/*     */   public String[] getSqlString(String schemaName, String relatingName, String relatedName, String bindClassName, int relationType) {
/*  67 */     List result = null;
/*  68 */     RelDataInfo relDataInfo = new RelDataInfo(schemaName, relatingName, relatedName, bindClassName, relationType);
/*  69 */     if (this.sqlString.containsKey(relDataInfo))
/*     */     {
/*  71 */       if (LogHome.getLog().isDebugEnabled()) {
/*  72 */         LogHome.getLog().debug("hit cache by getSqlString");
/*     */       }
/*  74 */       result = (List)this.sqlString.get(relDataInfo);
/*  75 */       if (result != null) {
/*  76 */         String[] str = new String[result.size()];
/*  77 */         result.toArray(str);
/*  78 */         return str;
/*     */       }
/*     */     }
/*     */ 
/*  82 */     String[] strArray = null;
/*     */ 
/*  88 */     return strArray;
/*     */   }
/*     */   public List getRelateClassByLinkClass(String schemaName, int relationType, String bindClassName) {
/*  91 */     List result = null;
/*  92 */     RelDataInfo relDataInfo = new RelDataInfo(schemaName, bindClassName, relationType);
/*  93 */     if (this.relationClassByLink.containsKey(relDataInfo))
/*     */     {
/*  95 */       if (LogHome.getLog().isDebugEnabled()) {
/*  96 */         LogHome.getLog().debug("hit cache by getRelateClassByLinkClass");
/*     */       }
/*  98 */       result = (List)this.relationClassByLink.get(relDataInfo);
/*  99 */       if (result != null) {
/* 100 */         return result;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 108 */     return result;
/*     */   }
/*     */ 
/*     */   public String[] getBindClassName(String schemaName, String relatingName, String relatedName, int relationType) {
/* 112 */     List result = null;
/* 113 */     RelDataInfo relDataInfo = new RelDataInfo(schemaName, relatingName, relatedName, relationType);
/* 114 */     if (this.bindClassName.containsKey(relDataInfo))
/*     */     {
/* 116 */       if (LogHome.getLog().isDebugEnabled()) {
/* 117 */         LogHome.getLog().debug("hit cache by getBindClassName");
/*     */       }
/* 119 */       result = (List)this.bindClassName.get(relDataInfo);
/* 120 */       if (result != null) {
/* 121 */         String[] str = new String[result.size()];
/* 122 */         result.toArray(str);
/* 123 */         return str;
/*     */       }
/*     */     }
/*     */ 
/* 127 */     String[] strArray = null;
/*     */ 
/* 133 */     return strArray;
/*     */   }
/*     */ 
/*     */   public String[] getBindClassName(String schemaName, String objectName, int relationType)
/*     */   {
/* 157 */     List result = null;
/* 158 */     RelDataInfo relDataInfo = new RelDataInfo(schemaName, objectName, relationType);
/* 159 */     if (this.bindClassName.containsKey(relDataInfo))
/*     */     {
/* 161 */       if (LogHome.getLog().isDebugEnabled()) {
/* 162 */         LogHome.getLog().debug("hit cache by getBindClassName");
/*     */       }
/* 164 */       result = (List)this.bindClassName.get(relDataInfo);
/* 165 */       if (result != null) {
/* 166 */         String[] str = new String[result.size()];
/* 167 */         result.toArray(str);
/* 168 */         return str;
/*     */       }
/*     */     }
/*     */ 
/* 172 */     String[] strArray = null;
/*     */ 
/* 178 */     return strArray;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     instance.relationDirect = new HashMap();
/*  30 */     instance.sqlString = new HashMap();
/*  31 */     instance.relationClassByLink = new HashMap();
/*  32 */     instance.bindClassName = new HashMap();
/*  33 */     instance.relationGroup = new HashMap();
/*     */   }
/*     */ 
/*     */   public final class RelDataInfo
/*     */   {
/*     */     private String schemaName;
/*     */     private String objectName;
/*     */     private String relatingName;
/*     */     private String relatedName;
/*     */     private String bindClassName;
/*     */     private int relationType;
/*     */     private int relationRole;
/*     */ 
/*     */     public RelDataInfo(String schemaName, String relatingName, String relatedName, int relationType)
/*     */     {
/* 191 */       this.schemaName = schemaName;
/* 192 */       this.relatingName = relatingName;
/* 193 */       this.relatedName = relatedName;
/* 194 */       this.relationType = relationType;
/*     */     }
/*     */ 
/*     */     public RelDataInfo(String schemaName, String objectName, int relationType, int relationRole) {
/* 198 */       this.schemaName = schemaName;
/* 199 */       this.objectName = objectName;
/* 200 */       this.relationType = relationType;
/* 201 */       this.relationRole = relationRole;
/*     */     }
/*     */ 
/*     */     public RelDataInfo(String schemaName, String relatingName, String relatedName, String bindClassName, int relationType) {
/* 205 */       this.schemaName = schemaName;
/* 206 */       this.relatingName = relatingName;
/* 207 */       this.relatedName = relatedName;
/* 208 */       this.bindClassName = bindClassName;
/* 209 */       this.relationType = relationType;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 213 */       int PRIME = 31;
/* 214 */       int result = 1;
/* 215 */       result = 31 * result + (this.bindClassName == null ? 0 : this.bindClassName.hashCode());
/* 216 */       result = 31 * result + (this.objectName == null ? 0 : this.objectName.hashCode());
/* 217 */       result = 31 * result + (this.relatedName == null ? 0 : this.relatedName.hashCode());
/* 218 */       result = 31 * result + (this.relatingName == null ? 0 : this.relatingName.hashCode());
/* 219 */       result = 31 * result + this.relationRole;
/* 220 */       result = 31 * result + this.relationType;
/* 221 */       result = 31 * result + (this.schemaName == null ? 0 : this.schemaName.hashCode());
/* 222 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 226 */       if (this == obj)
/* 227 */         return true;
/* 228 */       if (obj == null)
/* 229 */         return false;
/* 230 */       if (getClass() != obj.getClass())
/* 231 */         return false;
/* 232 */       RelDataInfo other = (RelDataInfo)obj;
/* 233 */       if (this.bindClassName == null) {
/* 234 */         if (other.bindClassName != null)
/* 235 */           return false;
/* 236 */       } else if (!this.bindClassName.equals(other.bindClassName))
/* 237 */         return false;
/* 238 */       if (this.objectName == null) {
/* 239 */         if (other.objectName != null)
/* 240 */           return false;
/* 241 */       } else if (!this.objectName.equals(other.objectName))
/* 242 */         return false;
/* 243 */       if (this.relatedName == null) {
/* 244 */         if (other.relatedName != null)
/* 245 */           return false;
/* 246 */       } else if (!this.relatedName.equals(other.relatedName))
/* 247 */         return false;
/* 248 */       if (this.relatingName == null) {
/* 249 */         if (other.relatingName != null)
/* 250 */           return false;
/* 251 */       } else if (!this.relatingName.equals(other.relatingName))
/* 252 */         return false;
/* 253 */       if (this.relationRole != other.relationRole)
/* 254 */         return false;
/* 255 */       if (this.relationType != other.relationType)
/* 256 */         return false;
/* 257 */       if (this.schemaName == null) {
/* 258 */         if (other.schemaName != null)
/* 259 */           return false;
/* 260 */       } else if (!this.schemaName.equals(other.schemaName))
/* 261 */         return false;
/* 262 */       return true;
/*     */     }
/*     */ 
/*     */     public RelDataInfo(String schemaName, String bindClassName, int relationType) {
/* 266 */       this.schemaName = schemaName;
/* 267 */       this.bindClassName = bindClassName;
/* 268 */       this.relationType = relationType;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.RelMethodDynamicCache
 * JD-Core Version:    0.6.0
 */