/*     */ package com.boco.transnms.server.dao.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.transnms.common.dto.base.DataObjectList;
/*     */ import com.boco.transnms.common.dto.base.DboCollection;
/*     */ import com.boco.transnms.common.dto.base.GenericDO;
/*     */ import com.boco.transnms.common.dto.base.IBoActionContext;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class GenericDAO extends AbstractDAO
/*     */ {
/*     */   public GenericDAO()
/*     */   {
/*  34 */     super("GenericDAO");
/*     */   }
/*     */ 
/*     */   public GenericDAO(String daoName) {
/*  38 */     super(daoName);
/*     */   }
/*     */ 
/*     */   public void getDboCuidObj(GenericDO dbo, String[] cuidNames)
/*     */   {
/*  47 */     for (int i = 0; i < cuidNames.length; i++)
/*     */       try {
/*  49 */         Object cuidObj = dbo.getAttrValue(cuidNames[i]);
/*  50 */         if ((cuidObj instanceof GenericDO)) {
/*     */           continue;
/*     */         }
/*  53 */         if ((cuidObj instanceof DataObjectList)) {
/*     */           continue;
/*     */         }
/*  56 */         String cuid = (String)cuidObj;
/*  57 */         if ((cuid != null) && (cuid.trim().equals(""))) {
/*     */           continue;
/*     */         }
/*  60 */         String[] cuids = cuid.split(",");
/*  61 */         if (cuids.length > 1) {
/*  62 */           dbo.setAttrValue(cuidNames[i], getObjsByCuid(cuids));
/*     */         }
/*     */         else {
/*  65 */           GenericDO cuidDbo = new GenericDO();
/*  66 */           cuidDbo.setCuid(cuid);
/*  67 */           cuidDbo = super.getObjByCuid(cuidDbo);
/*  68 */           dbo.setAttrValue(cuidNames[i], cuidDbo);
/*     */         }
/*     */       } catch (Exception ex) {
/*  71 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void getDboCuidObjs(DataObjectList dbos, String[] cuidNames)
/*     */   {
/*  82 */     for (int i = 0; i < dbos.size(); i++)
/*  83 */       getDboCuidObj((GenericDO)dbos.get(i), cuidNames);
/*     */   }
/*     */ 
/*     */   public void getDboCuidObjs(DboCollection dbos, String className, String[] cuidNames)
/*     */   {
/*  93 */     for (int i = 0; i < dbos.size(); i++) {
/*  94 */       GenericDO dbo = (GenericDO)dbos.getAttrField(className, i);
/*  95 */       getDboCuidObj(dbo, cuidNames);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getDboCuidLabelCn(GenericDO dbo, String[] cuidNames, String[] labelCnNames)
/*     */   {
/* 106 */     assert (cuidNames.length == labelCnNames.length);
/*     */ 
/* 108 */     for (int i = 0; i < cuidNames.length; i++)
/*     */       try {
/* 110 */         Object cuidObj = dbo.getAttrValue(cuidNames[i]);
/* 111 */         if ((cuidObj instanceof GenericDO)) {
/*     */           continue;
/*     */         }
/* 114 */         String cuid = (String)cuidObj;
/* 115 */         if ((cuid == null) || (cuid.trim().equals(""))) {
/* 116 */           dbo.setAttrNull(labelCnNames[i]);
/*     */         }
/*     */         else {
/* 119 */           GenericDO cuidDbo = new GenericDO();
/* 120 */           cuidDbo.setCuid(cuid);
/* 121 */           if (!cuidDbo.isSetClassName()) {
/* 122 */             LogHome.getLog().error("Dbo[className=" + dbo.getClassName() + ", cuidName=" + cuidNames[i] + ", cuid=" + cuid + "，是非法的CUID !");
/*     */           }
/*     */           else
/*     */           {
/* 126 */             cuidDbo = super.getObjByCuid(cuidDbo);
/* 127 */             if ((cuidDbo != null) && (dbo != null))
/* 128 */               dbo.setAttrValue(labelCnNames[i], cuidDbo.getAttrString("LABEL_CN"));
/*     */             else
/* 130 */               LogHome.getLog().error("Dbo[className=" + dbo.getClassName() + ", cuidName=" + cuidNames[i] + ", dbo=" + dbo + "或cuidDbo=" + cuidDbo + "为空 !");
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {
/* 135 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void getDboCuidLabelCns(DataObjectList dbos, String[] cuidNames, String[] labelNames)
/*     */   {
/* 147 */     for (int i = 0; i < dbos.size(); i++)
/* 148 */       getDboCuidLabelCn((GenericDO)dbos.get(i), cuidNames, labelNames);
/*     */   }
/*     */ 
/*     */   public void getDboCuidLabelCns(DboCollection dbos, String className, String[] cuidNames, String[] labelNames)
/*     */   {
/* 159 */     for (int i = 0; i < dbos.size(); i++) {
/* 160 */       GenericDO dbo = (GenericDO)dbos.getAttrField(className, i);
/* 161 */       getDboCuidLabelCn(dbo, cuidNames, labelNames);
/*     */     }
/*     */   }
/*     */ 
/*     */   public DataObjectList getObjsByCuid(String[] cuids)
/*     */   {
/* 171 */     DataObjectList objList = new DataObjectList();
/* 172 */     for (int i = 0; i < cuids.length; i++) {
/*     */       try {
/* 174 */         if ((cuids[i] != null) && (cuids[i].trim().equals(""))) {
/*     */           continue;
/*     */         }
/* 177 */         GenericDO cuidDbo = new GenericDO();
/* 178 */         cuidDbo.setCuid(cuids[i]);
/* 179 */         cuidDbo = super.getObjByCuid(cuidDbo);
/* 180 */         objList.add(cuidDbo);
/*     */       } catch (Exception ex) {
/* 182 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/* 185 */     return objList;
/*     */   }
/*     */ 
/*     */   public DataObjectList getSimpleObjsByCuid(String[] cuids)
/*     */   {
/* 194 */     DataObjectList objList = new DataObjectList();
/* 195 */     for (int i = 0; i < cuids.length; i++) {
/*     */       try {
/* 197 */         if ((cuids[i] != null) && (cuids[i].trim().equals(""))) {
/*     */           continue;
/*     */         }
/* 200 */         GenericDO cuidDbo = new GenericDO();
/* 201 */         cuidDbo.setCuid(cuids[i]);
/* 202 */         cuidDbo = super.getSimpleObjByCuid(cuidDbo);
/* 203 */         objList.add(cuidDbo);
/*     */       } catch (Exception ex) {
/* 205 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/* 208 */     return objList;
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(String cuid)
/*     */   {
/* 217 */     GenericDO resDbo = null;
/*     */     try {
/* 219 */       GenericDO cuidDbo = new GenericDO();
/* 220 */       cuidDbo.setCuid(cuid);
/*     */       try
/*     */       {
/* 223 */         cuidDbo.createInstanceByClassName();
/*     */       } catch (Exception ex) {
/* 225 */         LogHome.getLog().error("getObjByCuid对象CUID非法：" + cuid);
/* 226 */         return null;
/*     */       }
/* 228 */       resDbo = getObjByCuid(cuidDbo);
/* 229 */       if ((resDbo instanceof GenericDO)) {
/* 230 */         GenericDO cloneDbo = null;
/* 231 */         cloneDbo = resDbo.createInstanceByClassName();
/* 232 */         resDbo.copyTo(cloneDbo);
/* 233 */         resDbo = cloneDbo;
/*     */       }
/*     */     } catch (Exception ex) {
/* 236 */       LogHome.getLog().error("", ex);
/*     */     }
/* 238 */     return resDbo;
/*     */   }
/*     */ 
/*     */   public GenericDO getObjByCuid(IBoActionContext actionContext, String cuid)
/*     */   {
/* 247 */     GenericDO resDbo = null;
/*     */     try {
/* 249 */       GenericDO cuidDbo = new GenericDO();
/* 250 */       cuidDbo.setCuid(cuid);
/*     */       try
/*     */       {
/* 253 */         cuidDbo.createInstanceByClassName();
/*     */       } catch (Exception ex) {
/* 255 */         LogHome.getLog().error("getObjByCuid对象CUID非法：" + cuid);
/* 256 */         return null;
/*     */       }
/* 258 */       resDbo = getObjByCuid(actionContext, cuidDbo);
/* 259 */       if ((resDbo instanceof GenericDO)) {
/* 260 */         GenericDO cloneDbo = null;
/* 261 */         cloneDbo = resDbo.createInstanceByClassName();
/* 262 */         resDbo.copyTo(cloneDbo);
/* 263 */         resDbo = cloneDbo;
/*     */       }
/*     */     } catch (Exception ex) {
/* 266 */       LogHome.getLog().error("", ex);
/*     */     }
/* 268 */     return resDbo;
/*     */   }
/*     */ 
/*     */   public String getLabelCnByCuid(String cuid) throws Exception {
/* 272 */     String value = null;
/*     */     try {
/* 274 */       value = super.getLabelCnByCuid(cuid);
/*     */     } catch (Exception ex) {
/* 276 */       LogHome.getLog().error(ex);
/*     */     }
/*     */ 
/* 279 */     if ((value == null) || (value.trim().equals(""))) {
/* 280 */       GenericDO dbo = getObjByCuid(cuid);
/* 281 */       value = dbo.getAttrString("LABEL_CN");
/*     */     }
/* 283 */     return value;
/*     */   }
/*     */ 
/*     */   public Map getLabelCnsByCuids(String[] cuids) throws Exception {
/* 287 */     Map map = super.getLabelCnsByCuids(cuids);
/* 288 */     for (int i = 0; i < cuids.length; i++) {
/* 289 */       String cuid = cuids[i];
/* 290 */       String value = (String)map.get(cuid);
/* 291 */       if ((value == null) || (value.trim().equals(""))) {
/* 292 */         GenericDO dbo = getObjByCuid(cuid);
/* 293 */         value = dbo.getAttrString("LABEL_CN");
/* 294 */         map.put(cuid, value);
/*     */       }
/*     */     }
/* 297 */     return map;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.GenericDAO
 * JD-Core Version:    0.6.0
 */