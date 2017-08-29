/*     */ package com.boco.transnms.common.dto.base;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.io.FileHelper;
/*     */ import java.beans.XMLDecoder;
/*     */ import java.beans.XMLEncoder;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DataObjectList extends AttrObjList<GenericDO>
/*     */   implements IQueryCollection
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  34 */   private String eleClassName = null;
/*     */   private int countValue;
/*     */   private int fetchSize;
/*     */   private int offset;
/*     */   private String showAttr;
/*     */ 
/*     */   public DataObjectList()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DataObjectList(int offset, int fetchSize)
/*     */   {
/*  43 */     this.fetchSize = fetchSize;
/*  44 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public void setCountValue(int countValue) {
/*  48 */     this.countValue = countValue;
/*     */   }
/*     */ 
/*     */   public void setFetchSize(int fetchSize) {
/*  52 */     this.fetchSize = fetchSize;
/*     */   }
/*     */ 
/*     */   public void setOffset(int offset) {
/*  56 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   public void setShowAttr(String showAttr) {
/*  60 */     this.showAttr = showAttr;
/*     */   }
/*     */ 
/*     */   public int getCountValue() {
/*  64 */     return this.countValue;
/*     */   }
/*     */ 
/*     */   public int getFetchSize() {
/*  68 */     return this.fetchSize;
/*     */   }
/*     */ 
/*     */   public int getOffset() {
/*  72 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public String getShowAttr() {
/*  76 */     return this.showAttr;
/*     */   }
/*     */ 
/*     */   public int getPageSize() {
/*  80 */     int pageSize = 1;
/*  81 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/*  82 */       pageSize = this.countValue / this.fetchSize;
/*  83 */       if (this.countValue % this.fetchSize > 0) {
/*  84 */         pageSize++;
/*     */       }
/*     */     }
/*  87 */     return pageSize;
/*     */   }
/*     */ 
/*     */   public int getStartPageNo() {
/*  91 */     int startPageNo = 1;
/*  92 */     if ((this.countValue > 0) && (this.fetchSize > 0)) {
/*  93 */       startPageNo = this.offset / this.fetchSize + 1;
/*     */     }
/*  95 */     return startPageNo;
/*     */   }
/*     */ 
/*     */   public DataObjectList(String eleClassName) {
/*  99 */     this.eleClassName = eleClassName;
/*     */   }
/*     */ 
/*     */   public String getElementClassName() {
/* 103 */     if ((this.eleClassName == null) && (size() > 0)) {
/* 104 */       GenericDO dbo = (GenericDO)get(0);
/* 105 */       this.eleClassName = dbo.getClassName();
/*     */     }
/* 107 */     return this.eleClassName;
/*     */   }
/*     */ 
/*     */   public DataObjectList(GenericDO[] dbos) {
/* 111 */     for (int i = 0; i < dbos.length; i++)
/* 112 */       super.add(dbos[i]);
/*     */   }
/*     */ 
/*     */   public long[] getIds()
/*     */   {
/* 117 */     long[] ids = new long[size()];
/* 118 */     for (int i = 0; i < ids.length; i++) {
/* 119 */       ids[i] = ((GenericDO)get(i)).getObjectNum();
/*     */     }
/* 121 */     return ids;
/*     */   }
/*     */ 
/*     */   public List<Long> getIdList() {
/* 125 */     List objectIds = new ArrayList();
/* 126 */     for (int i = 0; i < size(); i++) {
/* 127 */       objectIds.add(Long.valueOf(((GenericDO)get(i)).getObjectNum()));
/*     */     }
/* 129 */     return objectIds;
/*     */   }
/*     */ 
/*     */   public String[] getCuids() {
/* 133 */     String[] cuids = new String[size()];
/* 134 */     getCuidList().toArray(cuids);
/* 135 */     return cuids;
/*     */   }
/*     */ 
/*     */   public List<String> getCuidList() {
/* 139 */     List cuids = new ArrayList();
/* 140 */     for (int i = 0; i < size(); i++) {
/* 141 */       cuids.add(((GenericDO)get(i)).getCuid());
/*     */     }
/* 143 */     return cuids;
/*     */   }
/*     */ 
/*     */   public List<GenericDO> getObjectByCuid(String cuid) {
/* 147 */     return getObjectByAttr("CUID", cuid);
/*     */   }
/*     */ 
/*     */   public List<GenericDO> getObjectByAttr(String attrName, Object value) {
/* 151 */     List dbos = new ArrayList();
/* 152 */     for (int i = 0; i < size(); i++) {
/* 153 */       GenericDO dbo = (GenericDO)get(i);
/* 154 */       Object attrValue = dbo.getAttrValue(attrName);
/* 155 */       if ((value == null) || (attrValue == null)) {
/* 156 */         if (attrValue == value)
/* 157 */           dbos.add(dbo);
/*     */       }
/* 159 */       else if (attrValue.equals(value)) {
/* 160 */         dbos.add(dbo);
/*     */       }
/*     */     }
/* 163 */     return dbos;
/*     */   }
/*     */ 
/*     */   public void removeObjectByCuid(String cuid) {
/* 167 */     removeObjectByAttr("CUID", cuid);
/*     */   }
/*     */ 
/*     */   public void removeObjectByAttr(String attrName, Object value) {
/* 171 */     for (int i = size() - 1; i >= 0; i--) {
/* 172 */       GenericDO dbo = (GenericDO)get(i);
/* 173 */       Object attrValue = dbo.getAttrValue(attrName);
/* 174 */       if ((value == null) || (attrValue == null)) {
/* 175 */         if (attrValue == value)
/* 176 */           remove(i);
/*     */       }
/* 178 */       else if (attrValue.equals(value))
/* 179 */         remove(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToXml(String fileName)
/*     */   {
/*     */     try {
/* 186 */       FileOutputStream out = new FileOutputStream(fileName);
/* 187 */       XMLEncoder encode = new XMLEncoder(out);
/* 188 */       for (int i = 0; i < size(); i++) {
/* 189 */         LogHome.getLog().info(get(i));
/* 190 */         encode.writeObject(get(i));
/*     */       }
/* 192 */       encode.close();
/*     */     } catch (Exception ex) {
/* 194 */       LogHome.getLog().error("", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readFromXml(String fileName) {
/* 199 */     XMLDecoder decode = null;
/*     */     try {
/* 201 */       InputStream in = FileHelper.getFileStream(fileName);
/* 202 */       decode = new XMLDecoder(in);
/*     */       while (true) {
/* 204 */         GenericDO dbo = (GenericDO)decode.readObject();
/* 205 */         if (dbo == null) break;
/* 206 */         add(dbo);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 214 */       if (decode != null)
/* 215 */         decode.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readFromStream(InputStream in)
/*     */   {
/* 222 */     XMLDecoder decode = null;
/*     */     try {
/* 224 */       decode = new XMLDecoder(in);
/*     */       while (true) {
/* 226 */         GenericDO dbo = (GenericDO)decode.readObject();
/* 227 */         if (dbo == null) break;
/* 228 */         add(dbo);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 236 */       if (decode != null)
/* 237 */         decode.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public GenericDO getQueryDbo(int rowNo, String className)
/*     */   {
/* 243 */     GenericDO dbo = (GenericDO)get(rowNo);
/* 244 */     if (!className.equals(dbo.getClassName())) {
/* 245 */       dbo = null;
/*     */     }
/* 247 */     return dbo;
/*     */   }
/*     */ 
/*     */   public Object getQueryAttrValue(int rowNo, String className, String attrName) {
/* 251 */     return getQueryDbo(rowNo, className).getAttrValue(attrName);
/*     */   }
/*     */ 
/*     */   public String getQueryAttrString(int rowNo, String className, String attrName) {
/* 255 */     Object value = getQueryAttrValue(rowNo, className, attrName);
/* 256 */     String attrVal = "";
/* 257 */     if (value != null) {
/* 258 */       attrVal = value.toString();
/*     */     }
/* 260 */     return attrVal;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 264 */     StringBuilder str = new StringBuilder();
/* 265 */     if (this.showAttr == null) {
/* 266 */       for (int i = 0; i < size(); i++) {
/* 267 */         str.append(new StringBuilder().append(((GenericDO)get(i)).toString()).append("\n").toString());
/*     */       }
/* 269 */       return str.toString();
/*     */     }
/* 271 */     for (int i = 0; i < size(); i++) {
/* 272 */       str.append(",");
/* 273 */       str.append(((GenericDO)get(i)).getAttrValue(this.showAttr));
/*     */     }
/* 275 */     if (str.length() > 0) {
/* 276 */       return str.toString().substring(1);
/*     */     }
/* 278 */     return "";
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.DataObjectList
 * JD-Core Version:    0.6.0
 */