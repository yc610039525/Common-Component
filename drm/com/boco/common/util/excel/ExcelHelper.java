/*      */ package com.boco.common.util.excel;
/*      */ 
/*      */ import com.boco.common.util.debug.LogHome;
/*      */ import com.boco.common.util.except.UserException;
/*      */ import com.boco.common.util.io.FileHelper;
/*      */ import com.boco.common.util.lang.StringHelper;
/*      */ import com.boco.common.util.lang.TimeFormatHelper;
/*      */ import com.boco.raptor.common.service.IServiceActionContext;
/*      */ import com.boco.raptor.common.service.ServiceHelper;
/*      */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*      */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*      */ import com.boco.raptor.drm.core.dto.DrmQueryAttrCond;
/*      */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*      */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*      */ import com.boco.raptor.drm.core.dto.IDrmEnumValue;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*      */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*      */ import com.boco.raptor.drm.core.dto.impl.DrmEnumValue;
/*      */ import com.boco.raptor.drm.core.dto.impl.DrmLabelValue;
/*      */ import com.boco.raptor.drm.core.dto.impl.upload.DrmExcelImportValidListener;
/*      */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta;
/*      */ import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta.DATA_TYPE;
/*      */ import com.boco.raptor.drm.core.meta.BMAttrMeta;
/*      */ import com.boco.raptor.drm.core.meta.BMAttrMeta.ATTR_TYPE_ENUM;
/*      */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*      */ import com.boco.raptor.drm.core.meta.BMEnumMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
/*      */ import com.boco.raptor.drm.core.meta.ExcelImpTemplateRelatedMeta;
/*      */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*      */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*      */ import com.boco.raptor.drm.core.service.vm.IVMModelService;
/*      */ import groovy.lang.GroovyShell;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import jxl.Cell;
/*      */ import jxl.CellType;
/*      */ import jxl.CellView;
/*      */ import jxl.NumberCell;
/*      */ import jxl.Sheet;
/*      */ import jxl.SheetSettings;
/*      */ import jxl.Workbook;
/*      */ import jxl.format.Alignment;
/*      */ import jxl.format.Border;
/*      */ import jxl.format.BorderLineStyle;
/*      */ import jxl.format.Colour;
/*      */ import jxl.format.RGB;
/*      */ import jxl.format.UnderlineStyle;
/*      */ import jxl.write.DateFormat;
/*      */ import jxl.write.DateTime;
/*      */ import jxl.write.Label;
/*      */ import jxl.write.Number;
/*      */ import jxl.write.NumberFormats;
/*      */ import jxl.write.WritableCell;
/*      */ import jxl.write.WritableCellFeatures;
/*      */ import jxl.write.WritableCellFormat;
/*      */ import jxl.write.WritableFont;
/*      */ import jxl.write.WritableSheet;
/*      */ import jxl.write.WritableWorkbook;
/*      */ import jxl.write.WriteException;
/*      */ import jxl.write.biff.RowsExceededException;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class ExcelHelper
/*      */ {
/*      */   private static final String EXCEL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*      */   private static final String EXCEL_DATE_FORMAT_E = "m/d/yyyy hh:mm AM/PM";
/*   95 */   public static int DATA_BEGIN_LINE_IN_EXCEL = 5;
/*   96 */   public static int UNIQUE_LINE_IN_EXCEL = 0;
/*   97 */   public static int RELATED_VALUE_LINE_IN_EXCEL = 1;
/*   98 */   public static int RELATED_CLASS_LINE_IN_EXCEL = 2;
/*   99 */   public static int ATTR_LINE_IN_EXCEL = 3;
/*  100 */   public static int LABEL_LINE_IN_EXCEL = 4;
/*  101 */   public static String RELATED_COL_FLAG = "000000-";
/*  102 */   public static String KEY_FLAG = "@@";
/*      */   public static final String EXPORT_PATH = "export";
/*      */   public static final int MAX_EXCEL_LINE = 60000;
/*      */   public static final String ENUM_SEPARATOR = ",";
/*      */ 
/*      */   public static WritableWorkbook creatWritableWorkBook(String fileName)
/*      */     throws IOException
/*      */   {
/*  125 */     return Workbook.createWorkbook(new File(fileName));
/*      */   }
/*      */ 
/*      */   public static String addTemplateHeaderMatrixClass(IServiceActionContext actionContext, WritableWorkbook workbook, BMClassMeta mc, ExcelImpTemplateMeta excelImpTemplate)
/*      */     throws IOException, WriteException
/*      */   {
/*  137 */     WritableSheet sheet = workbook.createSheet(mc.getBmClassId(), 0);
/*  138 */     for (int i = 0; i < DATA_BEGIN_LINE_IN_EXCEL - 1; i++) {
/*  139 */       sheet.setRowView(i, 1);
/*      */     }
/*  141 */     sheet.getSettings().setVerticalFreeze(2);
/*  142 */     int colIndex = 0;
/*      */ 
/*  145 */     Map relatedClassValueMap = new LinkedHashMap();
/*      */ 
/*  147 */     Map relateUniqueList = excelImpTemplate.getExcelImpRelateds();
/*      */ 
/*  149 */     Map newRelatedAttrMap = new LinkedHashMap();
/*  150 */     List allAttrList = new ArrayList();
/*  151 */     Map allAttrMap = new HashMap();
/*  152 */     List attrListTemp = excelImpTemplate.getExcelImpAttrList();
/*  153 */     List attrList = new ArrayList();
/*  154 */     Map impAttrMap = new HashMap();
/*  155 */     for (int i = 0; i < attrListTemp.size(); i++) {
/*  156 */       String sAttrId = (String)attrListTemp.get(i);
/*  157 */       if (sAttrId.equals(""))
/*      */         continue;
/*  159 */       attrList.add(sAttrId);
/*      */ 
/*  161 */       BMAttrMeta attrMeta = mc.getAttrMeta(sAttrId);
/*  162 */       if (!attrMeta.getIsRelation().booleanValue())
/*      */         continue;
/*  164 */       if (attrMeta.getIsNotNull().booleanValue())
/*      */         continue;
/*  166 */       List upperIds = new LinkedList();
/*  167 */       attrMeta.getAllUpperAttrs(upperIds);
/*  168 */       for (int j = 0; j < upperIds.size(); j++) {
/*  169 */         BMAttrMeta upAttrMeta = mc.getAttrMeta((String)upperIds.get(j));
/*  170 */         attrList.add(upperIds.get(j));
/*  171 */         if (upAttrMeta.getIsNotNull().booleanValue())
/*      */           break;
/*      */       }
/*      */     }
/*  175 */     for (int i = 0; i < attrList.size(); i++) {
/*  176 */       String sAttrId = (String)attrList.get(i);
/*  177 */       if (sAttrId.equals(""))
/*      */         continue;
/*  179 */       impAttrMap.put(sAttrId, sAttrId);
/*      */ 
/*  181 */       BMAttrMeta attrMeta = mc.getAttrMeta(sAttrId);
/*  182 */       if (attrMeta.getIsRelation().booleanValue()) {
/*  183 */         String sRootAttrId = (String)mc.getAllRootAttrMap().get(sAttrId);
/*  184 */         if (sRootAttrId == null) sRootAttrId = sAttrId;
/*  185 */         String sReturn = getRelatedClassValueList(sRootAttrId, attrMeta, "", excelImpTemplate.getExcelImpRelateds(), relatedClassValueMap);
/*  186 */         if (!sReturn.equals("")) return sReturn; 
/*      */       }
/*      */     }
/*  188 */     if (relatedClassValueMap.size() > 0)
/*      */     {
/*  190 */       Iterator iterator = relatedClassValueMap.keySet().iterator();
/*  191 */       while (iterator.hasNext()) {
/*  192 */         String sValue = (String)iterator.next();
/*  193 */         int iPos = sValue.indexOf(KEY_FLAG);
/*  194 */         String sRootAttrId = sValue.substring(0, iPos);
/*  195 */         String sClassId = sValue.substring(iPos + 2, sValue.length());
/*      */ 
/*  197 */         actionContext.setBmClassId(sClassId);
/*  198 */         BMClassMeta bmClassMeta = getBMModelService().getClassMeta(actionContext, sClassId);
/*      */ 
/*  200 */         String key = RELATED_COL_FLAG + sValue;
/*  201 */         newRelatedAttrMap.put(key, sValue);
/*  202 */         attrList.add(key);
/*      */ 
/*  204 */         String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, bmClassMeta, relateUniqueList);
/*  205 */         for (int i = 0; i < uniqueIds.length; i++) {
/*  206 */           BMAttrMeta attrMeta = bmClassMeta.getAttrMeta(uniqueIds[i]);
/*  207 */           if (attrMeta.getIsRelation().booleanValue())
/*      */             continue;
/*  209 */           attrList.add(RELATED_COL_FLAG + sValue + "-" + attrMeta.getAttrId());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  219 */     Map relatedMap = new HashMap();
/*  220 */     Map relatedMapTemp = new HashMap();
/*  221 */     Iterator iterator = excelImpTemplate.getExcelRelatedValue().keySet().iterator();
/*  222 */     while (iterator.hasNext()) {
/*  223 */       String key = (String)iterator.next();
/*  224 */       int iPos = key.indexOf(KEY_FLAG);
/*  225 */       String sRootAttrId = key.substring(0, iPos);
/*  226 */       int iPos1 = key.indexOf(KEY_FLAG, iPos + 1);
/*  227 */       String sClassId = key.substring(iPos + 2, iPos1);
/*  228 */       String attrId = key.substring(iPos1 + 2, key.length());
/*      */ 
/*  230 */       BMClassMeta bmClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/*  231 */       Iterator itr = ((Map)excelImpTemplate.getExcelRelatedValue().get(key)).keySet().iterator();
/*      */ 
/*  233 */       while (itr.hasNext()) {
/*  234 */         String sAttrId = (String)itr.next();
/*  235 */         List sValue = (List)((Map)excelImpTemplate.getExcelRelatedValue().get(key)).get(sAttrId);
/*  236 */         BMAttrMeta attrMeta = bmClassMeta.getAttrMeta(sAttrId);
/*      */ 
/*  239 */         Map classValueMap = new HashMap();
/*  240 */         String sReturn = getRelatedClassValueList(sRootAttrId, attrMeta, (String)sValue.get(0), excelImpTemplate.getExcelImpRelateds(), classValueMap);
/*  241 */         if (sReturn.equals("")) {
/*  242 */           Iterator iter = classValueMap.keySet().iterator();
/*  243 */           while (iter.hasNext()) {
/*  244 */             String sKey = (String)iter.next();
/*  245 */             iPos = sKey.indexOf(KEY_FLAG);
/*  246 */             String sCId = sKey.substring(iPos + 2, sKey.length());
/*      */ 
/*  248 */             BMClassMeta rClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sCId);
/*  249 */             String sTemp = (String)((Map)classValueMap.get(sKey)).get(rClassMeta.getCuidAttrId());
/*  250 */             relatedMap.put(sKey + KEY_FLAG + attrId, sTemp);
/*  251 */             relatedMapTemp.put(sKey, sTemp);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  258 */     Map delRelated = new HashMap();
/*      */ 
/*  261 */     for (int i = 0; i < attrList.size(); i++) {
/*  262 */       String sAttrId = (String)attrList.get(i);
/*  263 */       if (sAttrId.equals("")) {
/*      */         continue;
/*      */       }
/*  266 */       if (sAttrId.indexOf(RELATED_COL_FLAG) == 0)
/*      */         continue;
/*  268 */       BMAttrMeta attrMeta = mc.getAttrMeta(sAttrId);
/*      */ 
/*  270 */       Collection bufs = mc.getUniqueAttrIds().values();
/*  271 */       Iterator iter = bufs.iterator();
/*  272 */       if (iter.hasNext()) {
/*  273 */         String[] uniqueIds = (String[])(String[])iter.next();
/*  274 */         for (int j = 0; j < uniqueIds.length; j++) {
/*  275 */           if (uniqueIds[j].equals(sAttrId)) {
/*  276 */             attrMeta.setIsNotNull(java.lang.Boolean.valueOf(true));
/*  277 */             break;
/*      */           }
/*      */         }
/*      */       }
/*  281 */       allAttrList.add(sAttrId);
/*  282 */       allAttrMap.put(sAttrId, sAttrId);
/*  283 */       setHeaderColumnMatrixAttribute(sheet, colIndex, attrMeta);
/*  284 */       colIndex++;
/*      */ 
/*  286 */       if (attrMeta.getIsRelation().booleanValue()) {
/*  287 */         String sRootAttr = (String)mc.getAllRootAttrMap().get(sAttrId);
/*  288 */         if (sRootAttr == null) sRootAttr = sAttrId;
/*      */ 
/*  290 */         Map notNullMap = new HashMap();
/*  291 */         List upperIds = new LinkedList();
/*  292 */         attrMeta.getAllUpperAttrs(upperIds);
/*  293 */         for (int j = 0; j < upperIds.size(); j++) {
/*  294 */           BMAttrMeta upAttrMeta = mc.getAttrMeta((String)upperIds.get(j));
/*  295 */           if (upAttrMeta.getIsNotNull().booleanValue()) {
/*  296 */             String sClassId = getRelatedClassId(sRootAttr, upAttrMeta, excelImpTemplate.getExcelImpRelateds());
/*  297 */             notNullMap.put(sClassId, sClassId);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  302 */         Iterator ite = newRelatedAttrMap.keySet().iterator();
/*  303 */         while (ite.hasNext()) {
/*  304 */           String sId = (String)ite.next();
/*  305 */           String key = (String)newRelatedAttrMap.get(sId);
/*  306 */           int iPos = key.indexOf(KEY_FLAG);
/*  307 */           String sRootAttrId = key.substring(0, iPos);
/*  308 */           String sClassId = key.substring(iPos + 2, key.length());
/*      */ 
/*  310 */           if ((!sRootAttrId.equals(sRootAttr)) || 
/*  313 */             (relatedMapTemp.get(key) != null))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/*  328 */           BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/*      */ 
/*  331 */           String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, classMeta, relateUniqueList);
/*  332 */           for (int j = 0; j < uniqueIds.length; j++) {
/*  333 */             BMAttrMeta mat = classMeta.getAttrMeta(uniqueIds[j]);
/*  334 */             if (mat.getIsRelation().booleanValue())
/*      */               continue;
/*  336 */             BMAttrMeta attr = new BMAttrMeta();
/*  337 */             attr.setAttrId(RELATED_COL_FLAG + key + "-" + mat.getAttrId());
/*      */ 
/*  339 */             attr.setLabelCn(attrMeta.getLabelCn() + "-" + classMeta.getLabelCn() + "-" + mat.getLabelCn());
/*  340 */             attr.setBmClassId(key);
/*  341 */             boolean bIsNotNull = attrMeta.getIsNotNull().booleanValue();
/*  342 */             if ((!bIsNotNull) && 
/*  343 */               (notNullMap.get(sClassId) != null)) {
/*  344 */               bIsNotNull = true;
/*      */             }
/*      */ 
/*  347 */             attr.setIsNotNull(java.lang.Boolean.valueOf(bIsNotNull));
/*  348 */             attr.setAttrDbType(mat.getAttrDbType());
/*  349 */             String sTemp = RELATED_COL_FLAG + key + "-" + mat.getAttrId();
/*  350 */             if (allAttrMap.get(sTemp) == null) {
/*  351 */               allAttrList.add(sTemp);
/*  352 */               allAttrMap.put(sTemp, sTemp);
/*  353 */               setHeaderColumnMatrixAttribute(sheet, colIndex, attr);
/*  354 */               colIndex++;
/*      */             }
/*      */           }
/*      */ 
/*  358 */           delRelated.put(sId, sId);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  365 */     Iterator iter = newRelatedAttrMap.keySet().iterator();
/*  366 */     while (iter.hasNext()) {
/*  367 */       String sAttrId = (String)iter.next();
/*  368 */       String key = (String)newRelatedAttrMap.get(sAttrId);
/*  369 */       int iPos = key.indexOf(KEY_FLAG);
/*  370 */       String sRootAttrId = key.substring(0, iPos);
/*  371 */       String sClassId = key.substring(iPos + 2, key.length());
/*      */ 
/*  374 */       if ((relatedMapTemp.get(key) != null) || 
/*  377 */         (delRelated.get(sAttrId) != null))
/*      */         continue;
/*  379 */       BMAttrMeta rootMat = mc.getAttrMeta(sRootAttrId);
/*  380 */       if (impAttrMap.get(sRootAttrId) == null) {
/*  381 */         List upperIds = new LinkedList();
/*  382 */         rootMat.getAllUpperAttrs(upperIds);
/*  383 */         for (int j = 0; j < upperIds.size(); j++) {
/*  384 */           if (impAttrMap.get(upperIds.get(j)) != null) {
/*  385 */             rootMat = mc.getAttrMeta((String)upperIds.get(j));
/*      */           }
/*      */         }
/*  388 */         if (rootMat == null) {
/*  389 */           rootMat = mc.getAttrMeta(sRootAttrId);
/*      */         }
/*      */       }
/*      */ 
/*  393 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/*      */ 
/*  396 */       String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, classMeta, relateUniqueList);
/*  397 */       for (int i = 0; i < uniqueIds.length; i++) {
/*  398 */         BMAttrMeta mat = classMeta.getAttrMeta(uniqueIds[i]);
/*  399 */         if (mat.getIsRelation().booleanValue())
/*      */           continue;
/*  401 */         BMAttrMeta attrMeta = new BMAttrMeta();
/*  402 */         attrMeta.setAttrId(RELATED_COL_FLAG + key + "-" + mat.getAttrId());
/*  403 */         attrMeta.setLabelCn(rootMat.getLabelCn() + "-" + classMeta.getLabelCn() + "-" + mat.getLabelCn());
/*  404 */         attrMeta.setBmClassId(key);
/*  405 */         attrMeta.setIsNotNull(java.lang.Boolean.valueOf(false));
/*  406 */         attrMeta.setAttrDbType(mat.getAttrDbType());
/*  407 */         String sTemp = RELATED_COL_FLAG + key + "-" + mat.getAttrId();
/*  408 */         if (allAttrMap.get(sTemp) == null) {
/*  409 */           allAttrList.add(sTemp);
/*  410 */           allAttrMap.put(sTemp, sTemp);
/*  411 */           setHeaderColumnMatrixAttribute(sheet, colIndex, attrMeta);
/*  412 */           colIndex++;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  418 */     iterator = relatedMap.keySet().iterator();
/*  419 */     while (iterator.hasNext()) {
/*  420 */       String key = (String)iterator.next();
/*  421 */       int iPos = key.indexOf(KEY_FLAG);
/*  422 */       String sRootAttrId = key.substring(0, iPos);
/*  423 */       int iPos1 = key.indexOf(KEY_FLAG, iPos + 1);
/*  424 */       String sClassId = key.substring(iPos + 2, iPos1);
/*  425 */       String matId = key.substring(iPos1 + 2, key.length());
/*      */ 
/*  427 */       BMAttrMeta rootMat = mc.getAttrMeta(sRootAttrId);
/*      */ 
/*  429 */       BMClassMeta bmClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/*      */ 
/*  431 */       String sValue = (String)relatedMap.get(key);
/*  432 */       BMAttrMeta attrMeta = new BMAttrMeta();
/*      */ 
/*  434 */       String sAttrId = RELATED_COL_FLAG + key;
/*  435 */       String sLabelCn = rootMat.getLabelCn() + "-" + bmClassMeta.getLabelCn();
/*  436 */       allAttrList.add(sAttrId);
/*      */ 
/*  443 */       List upperIdList = new ArrayList();
/*  444 */       upperIdList.add(sRootAttrId);
/*  445 */       getAllUpperAttrs(rootMat, upperIdList);
/*  446 */       if (upperIdList.size() > 0) {
/*  447 */         for (int i = 0; i < upperIdList.size(); i++) {
/*  448 */           String attrId = (String)upperIdList.get(i);
/*  449 */           BMAttrMeta attrMat = mc.getAttrMeta(attrId);
/*      */           String sRelatedClassId;
/*      */           WritableCellFormat cellFormat;
/*      */           Iterator itr;
/*  450 */           if (attrMat.getIsRelation().booleanValue()) {
/*  451 */             String sRelatedClassId = getRelatedClassId(sRootAttrId, attrMat, excelImpTemplate.getExcelImpRelateds());
/*  452 */             if ((sRelatedClassId.equals(sClassId)) && (attrId.equals(matId))) {
/*  453 */               sAttrId = attrId;
/*  454 */               sLabelCn = attrMat.getLabelCn();
/*  455 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       } else {
/*  460 */         sRelatedClassId = getRelatedClassId(sRootAttrId, rootMat, excelImpTemplate.getExcelImpRelateds());
/*  461 */         if (sRelatedClassId.equals(sClassId)) {
/*  462 */           sAttrId = sRootAttrId;
/*  463 */           sLabelCn = rootMat.getLabelCn();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  468 */       attrMeta.setAttrId(sAttrId);
/*  469 */       attrMeta.setBmClassId(key);
/*  470 */       attrMeta.setLabelCn(sLabelCn);
/*  471 */       attrMeta.setIsNotNull(java.lang.Boolean.valueOf(true));
/*      */ 
/*  473 */       setHeaderColumnRelatedAttr(sheet, colIndex, attrMeta, sValue);
/*  474 */       colIndex++;
/*      */     }
/*      */ 
/*  478 */     excelImpTemplate.setExcelImpAttrList(allAttrList);
/*      */ 
/*  481 */     colIndex = 0;
/*  482 */     if (excelImpTemplate.getExcelImpRelateds() != null) {
/*  483 */       cellFormat = new WritableCellFormat();
/*  484 */       itr = excelImpTemplate.getExcelImpRelateds().keySet().iterator();
/*  485 */       while (itr.hasNext()) {
/*  486 */         String key = (String)itr.next();
/*  487 */         int iPos = key.indexOf(KEY_FLAG);
/*  488 */         String sRootAttrId = key.substring(0, iPos);
/*  489 */         String sClassId = key.substring(iPos + 2, key.length());
/*      */ 
/*  491 */         Label l = new Label(colIndex, UNIQUE_LINE_IN_EXCEL, sClassId, cellFormat);
/*  492 */         sheet.addCell(l);
/*  493 */         colIndex++;
/*      */ 
/*  495 */         Map classRelatedUnique = (Map)excelImpTemplate.getExcelImpRelateds().get(key);
/*      */ 
/*  497 */         ExcelImpTemplateRelatedMeta relatedMeta = (ExcelImpTemplateRelatedMeta)classRelatedUnique.get("unique");
/*  498 */         if (relatedMeta != null) {
/*  499 */           String sUniqueName = relatedMeta.getClassUniqueName();
/*  500 */           l = new Label(colIndex, UNIQUE_LINE_IN_EXCEL, sUniqueName, cellFormat);
/*  501 */           sheet.addCell(l);
/*      */         }
/*  503 */         colIndex++;
/*      */ 
/*  505 */         relatedMeta = (ExcelImpTemplateRelatedMeta)classRelatedUnique.get("related");
/*  506 */         if (relatedMeta != null) {
/*  507 */           String sParentClassId = relatedMeta.getParentBmClassId();
/*  508 */           l = new Label(colIndex, UNIQUE_LINE_IN_EXCEL, sParentClassId, cellFormat);
/*  509 */           sheet.addCell(l);
/*      */         }
/*  511 */         colIndex++;
/*      */ 
/*  513 */         l = new Label(colIndex, UNIQUE_LINE_IN_EXCEL, sRootAttrId, cellFormat);
/*  514 */         sheet.addCell(l);
/*  515 */         colIndex++;
/*      */       }
/*      */     }
/*  518 */     return "";
/*      */   }
/*      */ 
/*      */   private static void getAllUpperAttrs(BMAttrMeta attrMeta, List<String> upperIdList)
/*      */   {
/*  527 */     if ((attrMeta.getUpperAttrIds() == null) || (attrMeta.getUpperAttrIds().size() == 0)) return;
/*      */ 
/*  529 */     for (int i = 0; i < attrMeta.getUpperAttrIds().size(); i++) {
/*  530 */       BMAttrMeta mat = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), attrMeta.getBmClassId(), (String)attrMeta.getUpperAttrIds().get(i));
/*  531 */       upperIdList.add(attrMeta.getUpperAttrIds().get(i));
/*  532 */       if (mat.getUpperAttrIds().size() > 0)
/*  533 */         getAllUpperAttrs(mat, upperIdList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addTemplateHeaderMatrixClass(WritableSheet sheet, BMClassMeta bm, List<String> attrIds)
/*      */     throws IOException, WriteException
/*      */   {
/*  541 */     for (int i = 0; i < DATA_BEGIN_LINE_IN_EXCEL - 1; i++) {
/*  542 */       sheet.setRowView(i, 1);
/*      */     }
/*  544 */     sheet.getSettings().setVerticalFreeze(2);
/*  545 */     int colIndex = 0;
/*  546 */     for (int i = 0; i < attrIds.size(); i++) {
/*  547 */       String sAttrId = (String)attrIds.get(i);
/*  548 */       if (sAttrId.equals("")) {
/*      */         continue;
/*      */       }
/*  551 */       BMAttrMeta attrMeta = bm.getAttrMeta(sAttrId);
/*  552 */       setExportTemplateHeader(sheet, colIndex, attrMeta);
/*  553 */       colIndex++;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setExportTemplateHeader(WritableSheet sheet, int colIndex, BMAttrMeta mat) throws WriteException {
/*  558 */     boolean isNotNull = mat.getIsNotNull().booleanValue();
/*      */ 
/*  561 */     WritableCellFormat cellFormat = null;
/*  562 */     if (isNotNull) {
/*  563 */       WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
/*  564 */       cellFormat = new WritableCellFormat(font);
/*      */     } else {
/*  566 */       cellFormat = new WritableCellFormat();
/*      */     }
/*  568 */     cellFormat.setWrap(true);
/*  569 */     cellFormat.setBackground(Colour.LIGHT_GREEN);
/*  570 */     cellFormat.setAlignment(Alignment.CENTRE);
/*  571 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/*  573 */     String sAttrId = mat.getAttrId();
/*  574 */     String sLabel = mat.getLabelCn();
/*  575 */     Label l = new Label(colIndex, ATTR_LINE_IN_EXCEL, sAttrId, cellFormat);
/*  576 */     sheet.addCell(l);
/*  577 */     if (sAttrId.indexOf(RELATED_COL_FLAG) == 0) {
/*  578 */       l = new Label(colIndex, RELATED_CLASS_LINE_IN_EXCEL, mat.getBmClassId(), cellFormat);
/*  579 */       sheet.addCell(l);
/*      */     }
/*  581 */     l = new Label(colIndex, LABEL_LINE_IN_EXCEL, sLabel, cellFormat);
/*  582 */     sheet.addCell(l);
/*  583 */     sheet.setColumnView(colIndex, 30);
/*      */   }
/*      */ 
/*      */   private static void setHeaderColumnMatrixAttribute(WritableSheet sheet, int colIndex, BMAttrMeta mat)
/*      */     throws WriteException
/*      */   {
/*  593 */     boolean isNotNull = mat.getIsNotNull().booleanValue();
/*      */ 
/*  596 */     WritableCellFormat cellFormat = null;
/*  597 */     if (isNotNull) {
/*  598 */       WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
/*      */ 
/*  604 */       cellFormat = new WritableCellFormat(font);
/*      */     } else {
/*  606 */       cellFormat = new WritableCellFormat();
/*      */     }
/*  608 */     cellFormat.setWrap(true);
/*  609 */     cellFormat.setBackground(Colour.LIGHT_GREEN);
/*  610 */     cellFormat.setAlignment(Alignment.CENTRE);
/*  611 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/*  613 */     String sAttrId = mat.getAttrId();
/*  614 */     String sLabel = mat.getLabelCn();
/*  615 */     Label l = new Label(colIndex, ATTR_LINE_IN_EXCEL, sAttrId, cellFormat);
/*  616 */     sheet.addCell(l);
/*  617 */     if (sAttrId.indexOf(RELATED_COL_FLAG) == 0) {
/*  618 */       l = new Label(colIndex, RELATED_CLASS_LINE_IN_EXCEL, mat.getBmClassId(), cellFormat);
/*  619 */       sheet.addCell(l);
/*      */     }
/*  621 */     l = new Label(colIndex, LABEL_LINE_IN_EXCEL, sLabel, cellFormat);
/*  622 */     WritableCellFeatures cellFeatures = new WritableCellFeatures();
/*      */ 
/*  624 */     String sComment = "";
/*  625 */     int iHeight = 2;
/*  626 */     if (mat.getIsEnumAttr().booleanValue()) {
/*  627 */       String sEnumId = mat.getEnumId();
/*  628 */       BMEnumMeta enumMeta = getBMModelService().getEnumMeta(ServiceHelper.createSvActCxt(), sEnumId);
/*  629 */       if (enumMeta != null) {
/*  630 */         sComment = "枚举类型。取值范围：\n";
/*  631 */         for (int i = 0; i < enumMeta.getEnums().size(); i++) {
/*  632 */           iHeight++;
/*  633 */           IDrmEnumValue data = (IDrmEnumValue)enumMeta.getEnums().get(i);
/*  634 */           sComment = sComment + data.getEnumName() + ";\n";
/*      */         }
/*      */       }
/*  637 */     } else if (mat.getIsRelation().booleanValue()) {
/*  638 */       sComment = "关联属性。数据类型：字符串";
/*      */     }
/*  640 */     else if ((mat.getAttrDbType().intValue() == 9) || (mat.getAttrDbType().intValue() == 10)) {
/*  641 */       iHeight++;
/*  642 */       sComment = "数据类型：" + mat.getDbTypeName() + "\n（格式为yyyy-mm-dd hh:mm:ss）";
/*      */     } else {
/*  644 */       sComment = "数据类型：" + mat.getDbTypeName();
/*      */     }
/*      */ 
/*  648 */     cellFeatures.setComment(sComment, 2.0D, iHeight);
/*  649 */     l.setCellFeatures(cellFeatures);
/*  650 */     sheet.addCell(l);
/*      */ 
/*  652 */     if (mat.getIsRelation().booleanValue())
/*      */     {
/*  654 */       CellView cellView = new CellView();
/*  655 */       cellView.setHidden(true);
/*  656 */       sheet.setColumnView(colIndex, cellView);
/*      */     } else {
/*  658 */       sheet.setColumnView(colIndex, 30);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setHeaderColumnRelatedAttr(WritableSheet sheet, int colIndex, BMAttrMeta mat, String sValue)
/*      */     throws WriteException
/*      */   {
/*  665 */     WritableCellFormat cellFormat = null;
/*  666 */     cellFormat = new WritableCellFormat();
/*  667 */     cellFormat.setWrap(true);
/*  668 */     cellFormat.setBackground(Colour.LIGHT_GREEN);
/*  669 */     cellFormat.setAlignment(Alignment.CENTRE);
/*  670 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/*  672 */     String sLabel = mat.getLabelCn();
/*  673 */     Label l = new Label(colIndex, RELATED_VALUE_LINE_IN_EXCEL, sValue, cellFormat);
/*  674 */     sheet.addCell(l);
/*  675 */     l = new Label(colIndex, RELATED_CLASS_LINE_IN_EXCEL, mat.getBmClassId(), cellFormat);
/*  676 */     sheet.addCell(l);
/*  677 */     l = new Label(colIndex, ATTR_LINE_IN_EXCEL, mat.getAttrId(), cellFormat);
/*  678 */     sheet.addCell(l);
/*  679 */     l = new Label(colIndex, LABEL_LINE_IN_EXCEL, sLabel, cellFormat);
/*  680 */     sheet.addCell(l);
/*      */ 
/*  682 */     CellView cellView = new CellView();
/*  683 */     cellView.setHidden(true);
/*  684 */     sheet.setColumnView(colIndex, cellView);
/*      */   }
/*      */ 
/*      */   public static void addExcelData(WritableSheet sheet, IDrmQueryResultSet rs, BMClassMeta bm, List<String> attrIds, DrmExcelImportValidListener listener) throws Exception
/*      */   {
/*  689 */     List dbos = rs.getResultSet();
/*  690 */     boolean isPop = rs.isPopulate();
/*  691 */     boolean isEntiry = rs.isEntity();
/*  692 */     for (int i = 0; i < dbos.size(); i++) {
/*  693 */       IDrmDataObject dbo = ((IDrmQueryRow)dbos.get(i)).getResultDbo(bm.getDbClassId());
/*  694 */       int j = 0;
/*  695 */       for (String attrId : attrIds) {
/*  696 */         BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), bm.getBmClassId(), attrId);
/*  697 */         String value = "";
/*  698 */         if (attrMeta == null) {
/*  699 */           if (attrId.indexOf(RELATED_COL_FLAG) >= 0) {
/*  700 */             Object _value = dbo.getAttrValue(attrId);
/*  701 */             if ((_value == null) || (_value.toString().trim().length() == 0)) {
/*  702 */               _value = "";
/*      */             }
/*  704 */             value = String.valueOf(_value);
/*  705 */             insertSimpleCellData(sheet, i, j, value);
/*      */           }
/*  707 */           j++;
/*  708 */           continue;
/*      */         }
/*      */ 
/*  711 */         if ((isPop) && (isEntiry)) {
/*  712 */           if (attrMeta.getIsRelation().booleanValue()) {
/*  713 */             Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  714 */             if ((_value == null) || (_value.toString().trim().length() == 0)) {
/*  715 */               value = "";
/*      */             }
/*  717 */             else if ((_value instanceof DrmLabelValue)) {
/*  718 */               DrmLabelValue v = (DrmLabelValue)_value;
/*  719 */               value = String.valueOf(v.getLabel());
/*      */             } else {
/*  721 */               value = _value.toString();
/*      */             }
/*      */           }
/*  724 */           else if (attrMeta.getIsEnumAttr().booleanValue()) {
/*  725 */             Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  726 */             if ((_value == null) || (_value.toString().trim().length() == 0)) {
/*  727 */               value = "";
/*      */             }
/*  729 */             else if ((_value instanceof ArrayList)) {
/*  730 */               ArrayList l = (ArrayList)_value;
/*  731 */               for (DrmEnumValue e : l) {
/*  732 */                 value = value + String.valueOf(e.getEnumName()) + ",";
/*      */               }
/*  734 */               if (value.indexOf(",") >= 0)
/*  735 */                 value = value.substring(0, value.lastIndexOf(","));
/*      */             }
/*      */             else {
/*  738 */               DrmEnumValue v = (DrmEnumValue)_value;
/*  739 */               value = String.valueOf(v.getEnumName());
/*      */             }
/*      */           }
/*  742 */           else if (attrMeta.getAttrDbType().equals(Integer.valueOf(BMAttrMeta.ATTR_TYPE_ENUM.BOOLEAN))) {
/*  743 */             Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  744 */             if (_value == null) {
/*  745 */               _value = "";
/*      */             }
/*  747 */             value = String.valueOf(_value);
/*  748 */             if (value.equals("true"))
/*  749 */               value = "是";
/*  750 */             else if (value.equals("false"))
/*  751 */               value = "否";
/*      */           }
/*  753 */           else if (attrMeta.getAttrDbType().equals(Integer.valueOf(BMAttrMeta.ATTR_TYPE_ENUM.TIME_STAMP))) {
/*  754 */             Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  755 */             if (_value == null) {
/*  756 */               value = "";
/*      */             } else {
/*  758 */               value = String.valueOf(_value);
/*  759 */               java.util.Date d = TimeFormatHelper.convertDate(value.replaceAll("/", "-"), "yyyy-MM-dd HH:mm:ss");
/*  760 */               value = TimeFormatHelper.getFormatDate(d, "yyyy-MM-dd HH:mm:ss");
/*      */             }
/*      */           } else {
/*  763 */             Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  764 */             if ((_value == null) || (_value.toString().trim().length() == 0)) {
/*  765 */               _value = "";
/*      */             }
/*  767 */             value = String.valueOf(_value);
/*      */           }
/*      */         } else {
/*  770 */           Object _value = dbo.getAttrValue(attrMeta.getAttrId());
/*  771 */           if (_value == null) {
/*  772 */             _value = "";
/*      */           }
/*  774 */           value = String.valueOf(_value);
/*      */         }
/*  776 */         insertCellData(sheet, i, j, attrMeta, value);
/*  777 */         j++;
/*      */       }
/*  779 */       listener.update(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void insertSimpleCellData(WritableSheet sheet, int rowIndex, int colIndex, String value) throws WriteException, RowsExceededException {
/*  784 */     String rgb = "255,255,255";
/*  785 */     WritableCellFormat cellFormat = new WritableCellFormat();
/*  786 */     WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
/*      */ 
/*  792 */     cellFormat.setFont(font);
/*  793 */     cellFormat.setAlignment(Alignment.LEFT);
/*  794 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*  795 */     cellFormat.setBackground(getColour(Integer.parseInt(rgb.split(",")[0]), Integer.parseInt(rgb.split(",")[1]), Integer.parseInt(rgb.split(",")[2])));
/*  796 */     Label label = new Label(colIndex, rowIndex + DATA_BEGIN_LINE_IN_EXCEL, value, cellFormat);
/*  797 */     sheet.addCell(label);
/*      */   }
/*      */ 
/*      */   private static void insertCellData(WritableSheet sheet, int rowIndex, int colIndex, BMAttrMeta attrMeta, String value) throws Exception {
/*  801 */     DateFormat df = new DateFormat("yyyy-mm-dd hh:mm:ss");
/*  802 */     String collectionRgb = "255,255,255";
/*  803 */     String manualRgb = "255,255,255";
/*  804 */     if ((attrMeta.getAttrDbType().equals(Integer.valueOf(BMAttrMeta.ATTR_TYPE_ENUM.DATE))) && (value.trim().length() > 0)) {
/*  805 */       WritableCellFormat wcfDF = new WritableCellFormat(df);
/*  806 */       WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
/*      */ 
/*  813 */       wcfDF.setFont(font);
/*  814 */       wcfDF.setAlignment(Alignment.LEFT);
/*  815 */       wcfDF.setBorder(Border.ALL, BorderLineStyle.THIN);
/*  816 */       java.util.Date d = null;
/*  817 */       if (value.contains("."))
/*  818 */         d = TimeFormatHelper.convertDate(value.substring(0, value.lastIndexOf(".")), "yyyy-MM-dd");
/*      */       else {
/*  820 */         d = TimeFormatHelper.convertDate(value, "yyyy-MM-dd");
/*      */       }
/*  822 */       DateTime labelDate = null;
/*  823 */       labelDate = new DateTime(colIndex, rowIndex + DATA_BEGIN_LINE_IN_EXCEL, d, wcfDF);
/*  824 */       sheet.addCell(labelDate);
/*      */     }
/*  826 */     else if ((attrMeta.getAttrDbType().equals(Integer.valueOf(BMAttrMeta.ATTR_TYPE_ENUM.TIME_STAMP))) && (value.trim().length() > 0)) {
/*  827 */       DateFormat dateFormat = new DateFormat("yyyy-MM-dd HH:mm:ss");
/*  828 */       WritableCellFormat wcfDF = new WritableCellFormat(dateFormat);
/*  829 */       WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
/*      */ 
/*  836 */       wcfDF.setFont(font);
/*  837 */       wcfDF.setAlignment(Alignment.LEFT);
/*  838 */       wcfDF.setBorder(Border.ALL, BorderLineStyle.THIN);
/*  839 */       java.util.Date d = null;
/*  840 */       if (value.contains("."))
/*  841 */         d = TimeFormatHelper.convertDate(value.substring(0, value.lastIndexOf(".")), "yyyy-MM-dd HH:mm:ss");
/*      */       else {
/*  843 */         d = TimeFormatHelper.convertDate(value, "yyyy-MM-dd HH:mm:ss");
/*      */       }
/*  845 */       DateTime labelDate = null;
/*  846 */       labelDate = new DateTime(colIndex, rowIndex + DATA_BEGIN_LINE_IN_EXCEL, d, wcfDF);
/*  847 */       sheet.addCell(labelDate);
/*      */     } else {
/*  849 */       WritableCellFormat cellFormat = new WritableCellFormat();
/*  850 */       if ((attrMeta.getAttrDbType().equals(Integer.valueOf(BMAttrMeta.ATTR_TYPE_ENUM.FLOAT))) && (value.trim().length() > 0) && (value.indexOf(".") == 0)) {
/*  851 */         value = "0" + value;
/*      */       }
/*  853 */       if (value == null)
/*  854 */         value = "";
/*  855 */       setCellFormatByMatrixAttribute(cellFormat, attrMeta, manualRgb, collectionRgb);
/*  856 */       Label label = new Label(colIndex, rowIndex + DATA_BEGIN_LINE_IN_EXCEL, value, cellFormat);
/*  857 */       sheet.addCell(label);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setCellFormatByMatrixAttribute(WritableCellFormat cellFormat, BMAttrMeta attrMeta, String manualColour, String collectionColour) throws Exception {
/*  862 */     WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
/*      */ 
/*  868 */     cellFormat.setFont(font);
/*  869 */     cellFormat.setAlignment(Alignment.LEFT);
/*  870 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/*  872 */     if (attrMeta.getSourceType().intValue() == 1)
/*  873 */       cellFormat.setBackground(getColour(Integer.parseInt(collectionColour.split(",")[0]), Integer.parseInt(collectionColour.split(",")[1]), Integer.parseInt(collectionColour.split(",")[2])));
/*  874 */     else if (attrMeta.getSourceType().intValue() != 1)
/*  875 */       cellFormat.setBackground(getColour(Integer.parseInt(manualColour.split(",")[0]), Integer.parseInt(manualColour.split(",")[1]), Integer.parseInt(manualColour.split(",")[2])));
/*      */   }
/*      */ 
/*      */   public static Colour getColour(int r, int g, int b)
/*      */   {
/*  880 */     Colour re = Colour.DEFAULT_BACKGROUND;
/*      */ 
/*  882 */     for (Colour colour : Colour.getAllColours()) {
/*  883 */       if ((colour.getDefaultRGB().getBlue() == b) && (colour.getDefaultRGB().getGreen() == g) && (colour.getDefaultRGB().getRed() == r)) {
/*  884 */         re = colour;
/*  885 */         break;
/*      */       }
/*      */     }
/*  888 */     return re;
/*      */   }
/*      */ 
/*      */   public static List<String> validateExcelFile(IServiceActionContext actionContext, Workbook rwb, List<DrmUploadClassMeta> allData, String sClassId)
/*      */     throws Exception
/*      */   {
/*  901 */     List errorList = new ArrayList();
/*      */ 
/*  903 */     actionContext.setBmClassId(sClassId);
/*  904 */     BMClassMeta classMeta = getBMModelService().getClassMeta(actionContext, sClassId);
/*  905 */     if (classMeta == null) {
/*  906 */       errorList.add("系统未找到类ID为['" + sClassId + "']对应业务类！请检查Excel文件。");
/*  907 */       return errorList;
/*      */     }
/*      */     try
/*      */     {
/*  911 */       Sheet sheet = rwb.getSheet(0);
/*      */ 
/*  913 */       String bmClassId = sheet.getName();
/*  914 */       if (!bmClassId.equals(sClassId)) {
/*  915 */         errorList.add("Excel文件不是业务类 [" + classMeta.getLabelCn() + "] 的数据，不能导入！");
/*  916 */         return errorList;
/*      */       }
/*      */ 
/*  919 */       if (sheet.getRows() < DATA_BEGIN_LINE_IN_EXCEL) {
/*  920 */         errorList.add("文件格式错误！请检查Excel文件。");
/*  921 */         return errorList;
/*      */       }
/*  923 */       Cell[] cells = sheet.getRow(ATTR_LINE_IN_EXCEL);
/*  924 */       if (cells.length == 0) {
/*  925 */         errorList.add("文件格式错误！请检查Excel文件。");
/*  926 */         return errorList;
/*      */       }
/*      */ 
/*  929 */       int rowCount = sheet.getRows();
/*  930 */       if (rowCount <= DATA_BEGIN_LINE_IN_EXCEL) {
/*  931 */         errorList.add("文件中没有数据！");
/*  932 */         return errorList;
/*      */       }
/*      */ 
/*  935 */       int iTotal = (rowCount - DATA_BEGIN_LINE_IN_EXCEL) * 3;
/*  936 */       Double dValue = Double.valueOf(iTotal * 0.25D);
/*  937 */       int iValue = dValue.intValue();
/*  938 */       iTotal += iValue;
/*      */ 
/*  943 */       Cell[] relatedUniqueCells = sheet.getRow(UNIQUE_LINE_IN_EXCEL);
/*  944 */       Map relateUniqueList = new HashMap();
/*  945 */       for (Cell classCell : relatedUniqueCells) {
/*  946 */         int col = classCell.getColumn();
/*  947 */         if (col % 4 == 0) {
/*  948 */           Cell uniqueCell = sheet.getCell(col + 1, UNIQUE_LINE_IN_EXCEL);
/*  949 */           Cell parentClassCell = sheet.getCell(col + 2, UNIQUE_LINE_IN_EXCEL);
/*  950 */           Cell parentAttrCell = sheet.getCell(col + 3, UNIQUE_LINE_IN_EXCEL);
/*  951 */           if ((classCell.getContents() != null) && (!classCell.getContents().trim().equals(""))) {
/*  952 */             Map classRelatedMeta = new HashMap();
/*  953 */             if ((uniqueCell.getContents() != null) && (!uniqueCell.getContents().trim().equals(""))) {
/*  954 */               ExcelImpTemplateRelatedMeta relatedMeta = new ExcelImpTemplateRelatedMeta();
/*  955 */               relatedMeta.setClassUniqueName(uniqueCell.getContents().trim());
/*  956 */               relatedMeta.setTemplateRelatedBmClassId(classCell.getContents().trim());
/*  957 */               relatedMeta.setRelatedOrUnique("unique");
/*  958 */               classRelatedMeta.put("unique", relatedMeta);
/*      */             }
/*  960 */             if ((parentClassCell.getContents() != null) && (!parentClassCell.getContents().trim().equals(""))) {
/*  961 */               ExcelImpTemplateRelatedMeta relatedMeta = new ExcelImpTemplateRelatedMeta();
/*  962 */               relatedMeta.setParentBmClassId(parentClassCell.getContents().trim());
/*  963 */               relatedMeta.setParentAttrId(parentAttrCell.getContents().trim());
/*  964 */               relatedMeta.setTemplateRelatedBmClassId(classCell.getContents().trim());
/*  965 */               relatedMeta.setRelatedOrUnique("related");
/*  966 */               classRelatedMeta.put("related", relatedMeta);
/*      */             }
/*  968 */             String key = parentAttrCell.getContents().trim() + KEY_FLAG + classCell.getContents().trim();
/*  969 */             relateUniqueList.put(key, classRelatedMeta);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  975 */       Map matOneValueList = new HashMap();
/*      */ 
/*  977 */       Map relatedMap = new HashMap();
/*      */ 
/*  979 */       Map uniqueAttrMap = new HashMap();
/*      */ 
/*  981 */       Map uniqueAttrFoundList = new HashMap();
/*      */ 
/*  984 */       Iterator iterator = classMeta.getUniqueAttrIds().keySet().iterator();
/*  985 */       while (iterator.hasNext()) {
/*  986 */         String uniqueName = (String)iterator.next();
/*  987 */         String[] uniqueAttrs = (String[])classMeta.getUniqueAttrIds().get(uniqueName);
/*  988 */         Map uniqueAttrList = new HashMap();
/*  989 */         for (int i = 0; i < uniqueAttrs.length; i++) {
/*  990 */           uniqueAttrList.put(uniqueAttrs[i], uniqueAttrs[i]);
/*      */         }
/*  992 */         uniqueAttrMap.put(uniqueName, uniqueAttrList);
/*      */       }
/*      */ 
/*  995 */       for (Cell cell : cells) {
/*  996 */         if (cell.getContents() == null)
/*      */           continue;
/*  998 */         String sAttrId = cell.getContents().trim();
/*  999 */         String sAttrLabel = sheet.getCell(cell.getColumn(), LABEL_LINE_IN_EXCEL).getContents().trim();
/*      */ 
/* 1001 */         if ((sAttrId.equals("")) || (sAttrId.equalsIgnoreCase("OBJECTID")) || (sAttrId.equalsIgnoreCase("ISDELETE")) || (sAttrId.equalsIgnoreCase("LAST_MODIFY_TIME")) || (sAttrId.equalsIgnoreCase("CREATE_TIME")))
/*      */         {
/*      */           continue;
/*      */         }
/* 1005 */         BMAttrMeta attrMeta = null;
/*      */ 
/* 1007 */         int col = cell.getColumn();
/* 1008 */         Cell labelCell = sheet.getCell(col, LABEL_LINE_IN_EXCEL);
/* 1009 */         if (labelCell.isHidden())
/*      */         {
/* 1011 */           String key = sheet.getCell(col, RELATED_CLASS_LINE_IN_EXCEL).getContents();
/* 1012 */           String sRelatedAttrValue = sheet.getCell(col, RELATED_VALUE_LINE_IN_EXCEL).getContents();
/* 1013 */           if ((sRelatedAttrValue != null) && (!sRelatedAttrValue.trim().equals("")))
/*      */           {
/* 1015 */             relatedMap.put(key, sRelatedAttrValue);
/* 1016 */             attrMeta = classMeta.getAttrMeta(sAttrId);
/*      */ 
/* 1018 */             if (attrMeta == null) continue;
/*      */           }
/*      */         }
/*      */         else {
/* 1022 */           if (sAttrId.indexOf(RELATED_COL_FLAG) == 0)
/*      */             continue;
/* 1024 */           attrMeta = classMeta.getAttrMeta(sAttrId);
/* 1025 */           if (attrMeta == null) {
/* 1026 */             errorList.add("属性[" + sAttrLabel + "]在业务类[" + classMeta.getLabelCn() + "]中未发现！请检查Excel文件。");
/* 1027 */             continue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1032 */         if (sAttrId.indexOf(RELATED_COL_FLAG) == 0)
/*      */           continue;
/* 1034 */         if (attrMeta == null) {
/* 1035 */           attrMeta = classMeta.getAttrMeta(sAttrId);
/*      */         }
/*      */ 
/* 1038 */         iterator = uniqueAttrMap.keySet().iterator();
/* 1039 */         while (iterator.hasNext()) {
/* 1040 */           String uniqueName = (String)iterator.next();
/* 1041 */           Map uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1042 */           if (uniqueAttrList.get(sAttrId) != null) {
/* 1043 */             Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1044 */             if (foundAttrList == null)
/* 1045 */               foundAttrList = new HashMap();
/* 1046 */             foundAttrList.put(sAttrId, sAttrId);
/*      */ 
/* 1049 */             if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1050 */               List upperIds = new ArrayList();
/* 1051 */               attrMeta.getAllUpperAttrs(upperIds);
/* 1052 */               for (int j = 0; j < upperIds.size(); j++) {
/* 1053 */                 if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1054 */                   foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */                 }
/*      */               }
/*      */             }
/* 1058 */             uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1059 */             break;
/*      */           }
/*      */ 
/* 1062 */           if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1063 */             Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1064 */             if (foundAttrList == null) {
/* 1065 */               foundAttrList = new HashMap();
/*      */             }
/* 1067 */             List upperIds = new ArrayList();
/* 1068 */             attrMeta.getAllUpperAttrs(upperIds);
/* 1069 */             for (int j = 0; j < upperIds.size(); j++) {
/* 1070 */               if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1071 */                 foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */               }
/*      */             }
/* 1074 */             uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1075 */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1082 */       iterator = uniqueAttrFoundList.keySet().iterator();
/* 1083 */       boolean bFlag = false;
/* 1084 */       while (iterator.hasNext()) {
/* 1085 */         String uniqueName = (String)iterator.next();
/* 1086 */         Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1087 */         if (foundAttrList == null) {
/* 1088 */           foundAttrList = new HashMap();
/*      */         }
/* 1090 */         Map uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1091 */         if (foundAttrList.size() == uniqueAttrList.size()) {
/* 1092 */           bFlag = true;
/* 1093 */           break;
/*      */         }
/*      */       }
/* 1096 */       if (!bFlag) {
/* 1097 */         errorList.add("Excel文件中必须包含业务类对象中唯一性标识所有属性");
/*      */       }
/* 1099 */       Collection bufs = classMeta.getAllAttrMetas().values();
/* 1100 */       Iterator iter = bufs.iterator();
/* 1101 */       while (iter.hasNext()) {
/* 1102 */         BMAttrMeta mat = (BMAttrMeta)iter.next();
/*      */ 
/* 1104 */         if (mat.getIsSystem().booleanValue()) {
/* 1105 */           if ((!mat.getAttrId().equals("")) && (!mat.getAttrId().equalsIgnoreCase("CUID")) && (!mat.getAttrId().equalsIgnoreCase("OBJECTID")) && (!mat.getAttrId().equalsIgnoreCase("ISDELETE")) && (!mat.getAttrId().equalsIgnoreCase("LAST_MODIFY_TIME")) && (!mat.getAttrId().equalsIgnoreCase("CREATE_TIME")))
/*      */           {
/* 1108 */             if ((mat.getDefaultValue() == null) || (mat.getDefaultValue().trim().length() == 0)) {
/* 1109 */               if (mat.getIsNotNull().booleanValue())
/* 1110 */                 errorList.add("属性[" + mat.getLabelCn() + "]是系统属性，但是没有默认值！");
/*      */             }
/*      */             else {
/* 1113 */               matOneValueList.put(mat.getAttrId(), mat.getDefaultValue());
/*      */             }
/*      */           }
/*      */         }
/* 1117 */         else if ((mat.getDefaultValue() != null) && (mat.getDefaultValue().trim().length() != 0)) {
/* 1118 */           matOneValueList.put(mat.getAttrId(), mat.getDefaultValue());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1126 */       if (errorList.size() > 0) return errorList;
/*      */ 
/* 1129 */       checkExcelFileData(sheet, allData, matOneValueList, relateUniqueList, relatedMap, uniqueAttrMap);
/*      */ 
/* 1132 */       checkRelatedAndUnique(allData, matOneValueList, relateUniqueList, relatedMap);
/*      */ 
/* 1135 */       checkExcelDataBaseData(allData);
/*      */     } catch (Exception ex) {
/* 1137 */       ex.printStackTrace();
/* 1138 */       errorList.add("异常错误：" + ex.getMessage());
/* 1139 */       return errorList;
/*      */     }
/* 1141 */     return errorList;
/*      */   }
/*      */ 
/*      */   private static void checkExcelFileData(Sheet sheet, List<DrmUploadClassMeta> allData, Map<String, String> matOneValueList, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, String> relatedMap, Map<String, Map<String, String>> uniqueAttrMap)
/*      */   {
/* 1156 */     int lineCount = sheet.getRows();
/* 1157 */     int columnCount = sheet.getColumns();
/*      */ 
/* 1159 */     String bmClassId = sheet.getName();
/* 1160 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/*      */ 
/* 1162 */     for (int rowIndex = DATA_BEGIN_LINE_IN_EXCEL; rowIndex < lineCount; rowIndex++) {
/* 1163 */       DrmUploadClassMeta uploadClassMeta = new DrmUploadClassMeta();
/* 1164 */       uploadClassMeta.setBmClassId(bmClassId);
/*      */ 
/* 1166 */       Map uniqueAttrFoundList = uploadClassMeta.getUniqueAttrFoundList();
/*      */ 
/* 1168 */       uploadClassMeta.setExcelRow(rowIndex + 1);
/*      */ 
/* 1170 */       String sValue = "";
/*      */ 
/* 1172 */       for (int colIndex = 0; colIndex < columnCount; colIndex++) {
/* 1173 */         Cell cellAttr = sheet.getCell(colIndex, ATTR_LINE_IN_EXCEL);
/* 1174 */         Cell cellLabel = sheet.getCell(colIndex, LABEL_LINE_IN_EXCEL);
/* 1175 */         Cell cellData = sheet.getCell(colIndex, rowIndex);
/* 1176 */         String contents = cellData.getContents().trim();
/*      */ 
/* 1178 */         String sAttrId = cellAttr.getContents().trim();
/* 1179 */         Cell cellRelatedValue = sheet.getCell(colIndex, RELATED_VALUE_LINE_IN_EXCEL);
/*      */ 
/* 1182 */         if (sAttrId.indexOf(RELATED_COL_FLAG) == 0)
/*      */         {
/* 1184 */           if (cellLabel.isHidden())
/*      */             continue;
/* 1186 */           uploadClassMeta.setRelatedNoClassAttrValue(sAttrId, contents);
/*      */         }
/*      */         else
/*      */         {
/* 1191 */           BMAttrMeta attrMeta = classMeta.getAttrMeta(sAttrId);
/* 1192 */           if (attrMeta == null)
/*      */             continue;
/* 1194 */           if (cellLabel.isHidden())
/*      */           {
/* 1196 */             if ((cellRelatedValue.getContents() != null) && (!cellRelatedValue.getContents().trim().equals("")))
/*      */             {
/* 1198 */               uploadClassMeta.addRelatedAttrToList(sAttrId);
/* 1199 */               continue;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1204 */           if (cellData.getType().equals(CellType.NUMBER)) {
/* 1205 */             int dbtype = attrMeta.getAttrDbType().intValue();
/* 1206 */             if ((dbtype == 4) || (dbtype == 5))
/*      */             {
/* 1208 */               NumberCell nc = (NumberCell)cellData;
/* 1209 */               contents = Double.valueOf(nc.getValue()).toString();
/*      */             }
/*      */           }
/*      */ 
/* 1213 */           uploadClassMeta.setExcelAttrAddr(sAttrId, colIndex, rowIndex);
/* 1214 */           uploadClassMeta.setExcelAttrValue(sAttrId, contents);
/* 1215 */           uploadClassMeta.addAttrToList(sAttrId);
/*      */ 
/* 1217 */           if (attrMeta.getIsRelation().booleanValue())
/*      */           {
/* 1219 */             uploadClassMeta.addRelatedAttrToList(sAttrId);
/*      */           }
/*      */           else {
/* 1222 */             if ((contents == null) || (contents.trim().length() == 0))
/*      */             {
/* 1224 */               sValue = (String)matOneValueList.get(sAttrId);
/* 1225 */               if (sValue != null) {
/* 1226 */                 uploadClassMeta.setExcelAttrValue(sAttrId, sValue);
/* 1227 */                 continue;
/*      */               }
/*      */             }
/*      */ 
/* 1231 */             checkAttrMeta(uploadClassMeta, attrMeta.getAttrId());
/*      */ 
/* 1235 */             if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR))
/*      */               continue;
/* 1237 */             if ((!attrMeta.getIsEnumAttr().booleanValue()) && (!attrMeta.getIsRelation().booleanValue())) {
/* 1238 */               int iMaxLen = attrMeta.getStrAttrMaxLen();
/* 1239 */               if ((iMaxLen <= 0) || 
/* 1240 */                 (contents.getBytes().length <= iMaxLen)) continue;
/* 1241 */               uploadClassMeta.setAttrErrorDetail(sAttrId, "属性[" + attrMeta.getLabelCn() + "]超过最大长度[" + iMaxLen + "]");
/* 1242 */               uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1252 */       for (int colIndex = 0; colIndex < columnCount; colIndex++) {
/* 1253 */         Cell cellAttr = sheet.getCell(colIndex, ATTR_LINE_IN_EXCEL);
/* 1254 */         String sAttrId = cellAttr.getContents().trim();
/*      */ 
/* 1256 */         if (sAttrId.indexOf(RELATED_COL_FLAG) == 0)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 1261 */         BMAttrMeta attrMeta = classMeta.getAttrMeta(sAttrId);
/* 1262 */         if (attrMeta != null) {
/* 1263 */           String sAttrValue = uploadClassMeta.getExcelAttrValue(sAttrId);
/*      */ 
/* 1265 */           if ((sAttrValue != null) && (!sAttrValue.equals("")))
/*      */           {
/* 1267 */             String validRegExp = attrMeta.getValidRegExp();
/* 1268 */             if (validRegExp != null) {
/* 1269 */               Pattern regex = Pattern.compile(validRegExp);
/* 1270 */               Matcher matcher = regex.matcher(sAttrValue);
/* 1271 */               boolean rs = matcher.find();
/* 1272 */               if (!rs) {
/* 1273 */                 uploadClassMeta.setAttrErrorDetail(sAttrId, "属性[" + attrMeta.getLabelCn() + "]值不符合设置的正则表达式'" + validRegExp + "'");
/* 1274 */                 uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1279 */             String validCalcExp = attrMeta.getValidCalcExp();
/* 1280 */             String sExp = validCalcExp;
/* 1281 */             if (validCalcExp != null) {
/* 1282 */               List attrList = uploadClassMeta.getAllAttrList();
/* 1283 */               int k = 0;
/* 1284 */               for (k = 0; k < attrList.size(); k++) {
/* 1285 */                 String attrId = (String)attrList.get(k);
/* 1286 */                 String sTemp = uploadClassMeta.getExcelAttrValue(attrId);
/* 1287 */                 if (validCalcExp.indexOf("$[" + attrId + "]") >= 0) {
/* 1288 */                   if ((sTemp == null) || (sTemp.equals("")))
/*      */                     break;
/* 1290 */                   validCalcExp = validCalcExp.replace("$[" + attrId + "]", sTemp);
/*      */ 
/* 1292 */                   BMAttrMeta mat = classMeta.getAttrMeta(attrId);
/* 1293 */                   sExp = sExp.replace("$[" + attrId + "]", mat.getLabelCn());
/*      */                 }
/*      */               }
/* 1296 */               if (validCalcExp.indexOf("$[") < 0)
/*      */               {
/* 1298 */                 java.lang.Boolean isEqual = java.lang.Boolean.valueOf(false);
/*      */                 try {
/* 1300 */                   GroovyShell shell = new GroovyShell();
/* 1301 */                   isEqual = (java.lang.Boolean)shell.evaluate(validCalcExp);
/*      */                 } catch (Exception ex) {
/* 1303 */                   LogHome.getLog().error("", ex);
/*      */                 }
/* 1305 */                 if (!isEqual.booleanValue()) {
/* 1306 */                   uploadClassMeta.setAttrErrorDetail(sAttrId, "属性[" + attrMeta.getLabelCn() + "]值不符合设置的计算表达式'" + sExp + "'");
/* 1307 */                   uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1314 */           if ((sAttrValue != null) && (!sAttrValue.equals("")))
/*      */           {
/* 1316 */             Iterator iterator = uniqueAttrMap.keySet().iterator();
/* 1317 */             while (iterator.hasNext()) {
/* 1318 */               String uniqueName = (String)iterator.next();
/* 1319 */               Map uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1320 */               if (uniqueAttrList.get(sAttrId) != null) {
/* 1321 */                 Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1322 */                 if (foundAttrList == null)
/* 1323 */                   foundAttrList = new HashMap();
/* 1324 */                 foundAttrList.put(sAttrId, sAttrId);
/*      */ 
/* 1327 */                 if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1328 */                   List upperIds = new ArrayList();
/* 1329 */                   attrMeta.getAllUpperAttrs(upperIds);
/* 1330 */                   for (int j = 0; j < upperIds.size(); j++) {
/* 1331 */                     if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1332 */                       foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */                     }
/*      */                   }
/*      */                 }
/* 1336 */                 uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1337 */                 break;
/*      */               }
/*      */ 
/* 1340 */               if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1341 */                 Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1342 */                 if (foundAttrList == null) {
/* 1343 */                   foundAttrList = new HashMap();
/*      */                 }
/* 1345 */                 List upperIds = new ArrayList();
/* 1346 */                 attrMeta.getAllUpperAttrs(upperIds);
/* 1347 */                 for (int j = 0; j < upperIds.size(); j++) {
/* 1348 */                   if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1349 */                     foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */                   }
/*      */                 }
/* 1352 */                 uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1353 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/* 1358 */             if (!attrMeta.getIsRelation().booleanValue())
/*      */               continue;
/* 1360 */             String sRootAttrId = (String)classMeta.getAllRootAttrMap().get(sAttrId);
/* 1361 */             if (sRootAttrId == null) sRootAttrId = sAttrId;
/* 1362 */             String sRelatedClassId = getRelatedClassId(sRootAttrId, attrMeta, relateUniqueList);
/* 1363 */             String sLabel = getUniqueOneValueKey(sRootAttrId, sAttrId, uploadClassMeta, sRelatedClassId, relateUniqueList, relatedMap);
/* 1364 */             if (!sLabel.equals(""))
/*      */             {
/* 1366 */               Iterator iterator = uniqueAttrMap.keySet().iterator();
/* 1367 */               while (iterator.hasNext()) {
/* 1368 */                 String uniqueName = (String)iterator.next();
/* 1369 */                 Map uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1370 */                 if (uniqueAttrList.get(sAttrId) != null) {
/* 1371 */                   Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1372 */                   if (foundAttrList == null)
/* 1373 */                     foundAttrList = new HashMap();
/* 1374 */                   foundAttrList.put(sAttrId, sAttrId);
/*      */ 
/* 1377 */                   if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1378 */                     List upperIds = new ArrayList();
/* 1379 */                     attrMeta.getAllUpperAttrs(upperIds);
/* 1380 */                     for (int j = 0; j < upperIds.size(); j++) {
/* 1381 */                       if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1382 */                         foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */                       }
/*      */                     }
/*      */                   }
/* 1386 */                   uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1387 */                   break;
/*      */                 }
/*      */ 
/* 1390 */                 if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1391 */                   Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1392 */                   if (foundAttrList == null) {
/* 1393 */                     foundAttrList = new HashMap();
/*      */                   }
/* 1395 */                   List upperIds = new ArrayList();
/* 1396 */                   attrMeta.getAllUpperAttrs(upperIds);
/* 1397 */                   for (int j = 0; j < upperIds.size(); j++) {
/* 1398 */                     if (uniqueAttrList.get(upperIds.get(j)) != null) {
/* 1399 */                       foundAttrList.put(upperIds.get(j), upperIds.get(j));
/*      */                     }
/*      */                   }
/* 1402 */                   uniqueAttrFoundList.put(uniqueName, foundAttrList);
/* 1403 */                   break;
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/* 1409 */             else if (attrMeta.getUpperAttrIds().size() > 0) {
/* 1410 */               List upperIds = new ArrayList();
/* 1411 */               attrMeta.getAllUpperAttrs(upperIds);
/* 1412 */               boolean bTemp = false;
/* 1413 */               for (int j = 0; j < upperIds.size(); j++) {
/* 1414 */                 sAttrId = (String)upperIds.get(j);
/* 1415 */                 BMAttrMeta upAttrMeta = classMeta.getAttrMeta(sAttrId);
/* 1416 */                 sRootAttrId = (String)classMeta.getAllRootAttrMap().get(sAttrId);
/* 1417 */                 if (sRootAttrId == null) sRootAttrId = sAttrId;
/* 1418 */                 sRelatedClassId = getRelatedClassId(sRootAttrId, upAttrMeta, relateUniqueList);
/* 1419 */                 sLabel = getUniqueOneValueKey(sRootAttrId, sAttrId, uploadClassMeta, sRelatedClassId, relateUniqueList, relatedMap);
/* 1420 */                 Iterator iterator = uniqueAttrMap.keySet().iterator();
/* 1421 */                 while (iterator.hasNext()) {
/* 1422 */                   String uniqueName = (String)iterator.next();
/* 1423 */                   Map uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1424 */                   if (uniqueAttrList.get(sAttrId) != null) {
/* 1425 */                     Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1426 */                     if (foundAttrList == null)
/* 1427 */                       foundAttrList = new HashMap();
/* 1428 */                     foundAttrList.put(sAttrId, sAttrId);
/*      */ 
/* 1431 */                     uniqueAttrFoundList.put(uniqueName, foundAttrList);
/*      */ 
/* 1433 */                     bTemp = true;
/* 1434 */                     break;
/*      */                   }
/*      */                 }
/* 1437 */                 if (bTemp)
/*      */                 {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1446 */       Iterator iterator = uniqueAttrFoundList.keySet().iterator();
/* 1447 */       boolean bFlag = false;
/*      */ 
/* 1449 */       Map uniqueAttrList = null;
/* 1450 */       Map uniqueAttrIds = new HashMap();
/* 1451 */       while (iterator.hasNext()) {
/* 1452 */         String uniqueName = (String)iterator.next();
/* 1453 */         Map foundAttrList = (Map)uniqueAttrFoundList.get(uniqueName);
/* 1454 */         if (foundAttrList == null) {
/* 1455 */           foundAttrList = new HashMap();
/*      */         }
/* 1457 */         uniqueAttrList = (Map)uniqueAttrMap.get(uniqueName);
/* 1458 */         if (foundAttrList.size() == uniqueAttrList.size()) {
/* 1459 */           Iterator iter = uniqueAttrList.keySet().iterator();
/* 1460 */           String[] attrArray = new String[uniqueAttrList.size()];
/* 1461 */           int i = 0;
/* 1462 */           while (iter.hasNext()) {
/* 1463 */             String attrId = (String)iter.next();
/* 1464 */             attrArray[i] = attrId;
/* 1465 */             i++;
/*      */           }
/* 1467 */           uniqueAttrIds.put(uniqueName, attrArray);
/* 1468 */           uploadClassMeta.setUniqueAttrIds(uniqueAttrIds);
/* 1469 */           bFlag = true;
/*      */         }
/*      */       }
/* 1472 */       if (!bFlag) {
/* 1473 */         uploadClassMeta.setRowErrorInfo("唯一性描述属性值不能为空");
/* 1474 */         uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/*      */       }
/*      */ 
/* 1477 */       allData.add(uploadClassMeta);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void checkExcelDataBaseData(List<DrmUploadClassMeta> allData)
/*      */   {
/* 1489 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/*      */ 
/* 1491 */     for (int i = 0; i < allData.size(); i++) {
/* 1492 */       DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/*      */ 
/* 1494 */       if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR))
/*      */       {
/*      */         continue;
/*      */       }
/* 1498 */       IDrmQueryResultSet rs = null;
/* 1499 */       boolean bFound = false;
/* 1500 */       Collection uniqueBufs = uploadClassMeta.getUniqueAttrIds().values();
/* 1501 */       Iterator uniqueIter = uniqueBufs.iterator();
/* 1502 */       while (uniqueIter.hasNext()) {
/* 1503 */         DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 1504 */         drmQuery.setBmClassId(uploadClassMeta.getBmClassId());
/* 1505 */         String[] uniqueAttrIds = (String[])(String[])uniqueIter.next();
/*      */ 
/* 1507 */         drmQuery.addQueryAttrId("CUID");
/* 1508 */         for (int j = 0; j < uniqueAttrIds.length; j++) {
/* 1509 */           String sAttrId = uniqueAttrIds[j];
/* 1510 */           String sValue = uploadClassMeta.getExcelAttrValue(sAttrId);
/*      */ 
/* 1512 */           DrmQueryAttrCond drmCond = new DrmQueryAttrCond(sAttrId, "=", sValue);
/* 1513 */           drmQuery.addQueryCondExps(drmCond);
/*      */         }
/*      */ 
/* 1516 */         rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmQuery);
/* 1517 */         if ((rs != null) && (rs.getResultSet().size() != 0)) {
/* 1518 */           bFound = true;
/* 1519 */           break;
/*      */         }
/*      */       }
/*      */ 
/* 1523 */       if (!bFound)
/*      */       {
/* 1525 */         uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ADD);
/*      */       } else {
/* 1527 */         List dataList = rs.getResultSet();
/* 1528 */         IDrmQueryRow data = (IDrmQueryRow)dataList.get(0);
/* 1529 */         BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), uploadClassMeta.getBmClassId());
/* 1530 */         String sCuid = (String)data.getResAttrValue(classMeta.getDbClassId(), classMeta.getCuidAttrId());
/*      */ 
/* 1532 */         uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_MODIFY);
/* 1533 */         uploadClassMeta.setCuid(sCuid);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void checkAttrMeta(DrmUploadClassMeta uploadClassMeta, String sAttrId)
/*      */   {
/* 1544 */     String sReturn = "";
/*      */ 
/* 1546 */     BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), uploadClassMeta.getBmClassId(), sAttrId);
/*      */ 
/* 1549 */     sReturn = checkIsNotNull(uploadClassMeta, sAttrId);
/* 1550 */     if (!sReturn.equals("")) {
/* 1551 */       uploadClassMeta.setAttrErrorDetail(sAttrId, sReturn);
/* 1552 */       uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1553 */       return;
/*      */     }
/*      */ 
/* 1556 */     if (!attrMeta.getIsRelation().booleanValue())
/*      */     {
/* 1558 */       if (!attrMeta.getIsEnumAttr().booleanValue()) {
/* 1559 */         sReturn = checkNoEnumType(uploadClassMeta, sAttrId);
/* 1560 */         if (!sReturn.equals("")) {
/* 1561 */           uploadClassMeta.setAttrErrorDetail(sAttrId, sReturn);
/* 1562 */           uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1563 */           return;
/*      */         }
/*      */       } else {
/* 1566 */         sReturn = checkEnumType(uploadClassMeta, sAttrId);
/* 1567 */         if (!sReturn.equals("")) {
/* 1568 */           uploadClassMeta.setAttrErrorDetail(sAttrId, sReturn);
/* 1569 */           uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1570 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String checkIsNotNull(DrmUploadClassMeta uploadClassMeta, String sAttrId)
/*      */   {
/* 1584 */     BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), uploadClassMeta.getBmClassId(), sAttrId);
/* 1585 */     String contents = uploadClassMeta.getExcelAttrValue(sAttrId);
/* 1586 */     if (((contents == null) || (contents.trim().length() == 0)) && 
/* 1587 */       (attrMeta.getIsNotNull().booleanValue())) {
/* 1588 */       return "属性[" + attrMeta.getLabelCn() + "]不能为空";
/*      */     }
/*      */ 
/* 1592 */     return "";
/*      */   }
/*      */ 
/*      */   private static String checkNoEnumType(DrmUploadClassMeta uploadClassMeta, String sAttrId)
/*      */   {
/* 1602 */     BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), uploadClassMeta.getBmClassId(), sAttrId);
/* 1603 */     String contents = uploadClassMeta.getExcelAttrValue(sAttrId);
/*      */ 
/* 1605 */     if ((contents != null) && (contents.equals(""))) {
/* 1606 */       return "";
/*      */     }
/* 1608 */     boolean flag = true;
/* 1609 */     if (!attrMeta.getIsEnumAttr().booleanValue()) {
/* 1610 */       Class dbType = attrMeta.getAttrClassType();
/* 1611 */       if (dbType == java.lang.Boolean.TYPE) {
/* 1612 */         if ((contents != null) && ((contents.equals("是")) || (contents.equals("否")))) {
/* 1613 */           if (contents.equals("是"))
/* 1614 */             uploadClassMeta.setExcelAttrValue(sAttrId, "true");
/*      */           else {
/* 1616 */             uploadClassMeta.setExcelAttrValue(sAttrId, "false");
/*      */           }
/* 1618 */           flag = true;
/*      */         } else {
/* 1620 */           flag = false;
/*      */         }
/* 1622 */       } else if ((dbType == Long.TYPE) || (dbType == Integer.TYPE)) {
/*      */         try {
/* 1624 */           Double dValue = Double.valueOf(contents);
/* 1625 */           Long lValue = Long.valueOf(dValue.longValue());
/* 1626 */           if (lValue.longValue() != dValue.doubleValue()) {
/* 1627 */             flag = false;
/*      */           } else {
/* 1629 */             contents = lValue.toString();
/* 1630 */             uploadClassMeta.setExcelAttrValue(sAttrId, contents);
/* 1631 */             flag = true;
/*      */           }
/*      */         } catch (NumberFormatException ex) {
/* 1634 */           flag = false;
/*      */         }
/*      */       }
/* 1637 */       else if ((dbType == Timestamp.class) || (dbType == java.sql.Date.class))
/*      */       {
/*      */         try {
/* 1640 */           SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy hh:mm", Locale.US);
/* 1641 */           java.util.Date d = sdf.parse(contents);
/* 1642 */           Timestamp sqlDate = new Timestamp(d.getTime());
/* 1643 */           long time = sqlDate.getTime();
/* 1644 */           contents = Long.valueOf(time).toString();
/*      */ 
/* 1646 */           uploadClassMeta.setExcelAttrValue(sAttrId, contents);
/*      */         } catch (Exception ex) {
/* 1648 */           flag = false;
/*      */         }
/* 1650 */         if (!flag)
/*      */           try {
/* 1652 */             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
/* 1653 */             java.util.Date d = sdf.parse(contents);
/* 1654 */             Timestamp sqlDate = new Timestamp(d.getTime());
/* 1655 */             long time = sqlDate.getTime();
/* 1656 */             contents = Long.valueOf(time).toString();
/*      */ 
/* 1658 */             uploadClassMeta.setExcelAttrValue(sAttrId, contents);
/* 1659 */             flag = true;
/*      */           } catch (Exception ex) {
/*      */           }
/*      */       }
/* 1663 */       else if (dbType == Float.TYPE) {
/* 1664 */         flag = StringHelper.isDouble(contents);
/*      */       }
/*      */ 
/* 1667 */       if (!flag) {
/* 1668 */         return "属性[" + attrMeta.getLabelCn() + "]数据类型错误";
/*      */       }
/*      */     }
/*      */ 
/* 1672 */     return "";
/*      */   }
/*      */ 
/*      */   private static String checkEnumType(DrmUploadClassMeta uploadClassMeta, String sAttrId)
/*      */   {
/* 1682 */     BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), uploadClassMeta.getBmClassId(), sAttrId);
/* 1683 */     String contents = uploadClassMeta.getExcelAttrValue(sAttrId);
/* 1684 */     if (attrMeta.getIsEnumAttr().booleanValue()) {
/* 1685 */       if ((contents == null) || (contents.trim().equals("")))
/*      */       {
/* 1687 */         return "";
/*      */       }
/*      */ 
/* 1690 */       String sAttrValue = "";
/* 1691 */       String sEnumId = attrMeta.getEnumId();
/* 1692 */       BMEnumMeta enumMeta = getBMModelService().getEnumMeta(ServiceHelper.createSvActCxt(), sEnumId);
/* 1693 */       if (attrMeta.getIsMultiValue().booleanValue()) {
/* 1694 */         int iPos = contents.indexOf(",");
/*      */ 
/* 1696 */         while (iPos >= 0) {
/* 1697 */           String sValue = contents.substring(0, iPos);
/*      */           try {
/* 1699 */             Long value = (Long)enumMeta.getEnumValue(sValue);
/* 1700 */             String sEnumValue = value.toString();
/* 1701 */             if (sAttrValue.equals(""))
/* 1702 */               sAttrValue = sEnumValue;
/*      */             else
/* 1704 */               sAttrValue = sAttrValue + "," + sEnumValue;
/*      */           }
/*      */           catch (UserException ex) {
/* 1707 */             return "属性[" + attrMeta.getLabelCn() + "]枚举值转换出错" + ex.getMessage();
/*      */           }
/* 1709 */           contents = contents.substring(iPos + 1, contents.length());
/* 1710 */           iPos = contents.indexOf(",");
/*      */         }
/*      */         try
/*      */         {
/* 1714 */           Long value = (Long)enumMeta.getEnumValue(contents);
/* 1715 */           String sEnumValue = value.toString();
/* 1716 */           if (sAttrValue.equals(""))
/* 1717 */             sAttrValue = sEnumValue;
/*      */           else {
/* 1719 */             sAttrValue = sAttrValue + "," + sEnumValue;
/*      */           }
/* 1721 */           uploadClassMeta.setExcelAttrValue(sAttrId, sAttrValue);
/*      */         } catch (UserException ex) {
/* 1723 */           return "属性[" + attrMeta.getLabelCn() + "]枚举值转换出错" + ex.getMessage();
/*      */         }
/*      */       } else {
/*      */         try {
/* 1727 */           Long value = (Long)enumMeta.getEnumValue(contents);
/* 1728 */           String sEnumValue = value.toString();
/* 1729 */           uploadClassMeta.setExcelAttrValue(sAttrId, sEnumValue);
/*      */         } catch (UserException ex) {
/* 1731 */           return "属性[" + attrMeta.getLabelCn() + "]枚举值转换出错" + ex.getMessage();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1736 */     return "";
/*      */   }
/*      */ 
/*      */   private static void checkRelatedAndUnique(List<DrmUploadClassMeta> allData, Map<String, String> matOneValueList, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, String> relatedMap)
/*      */   {
/* 1750 */     Map uniqueValue = new HashMap();
/*      */ 
/* 1752 */     for (int i = 0; i < allData.size(); i++) {
/* 1753 */       DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/* 1754 */       String sClassId = uploadClassMeta.getBmClassId();
/* 1755 */       BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sClassId);
/*      */ 
/* 1757 */       for (int j = 0; j < uploadClassMeta.getAllRelatedAttr().size(); j++) {
/* 1758 */         String sAttrId = (String)uploadClassMeta.getAllRelatedAttr().get(j);
/* 1759 */         BMAttrMeta attrMeta = getBMModelService().getAttrMeta(ServiceHelper.createSvActCxt(), sClassId, sAttrId);
/*      */ 
/* 1761 */         String sRootAttrId = (String)classMeta.getAllRootAttrMap().get(sAttrId);
/* 1762 */         if (sRootAttrId == null) sRootAttrId = sAttrId;
/*      */ 
/* 1764 */         String sRelatedClassId = getRelatedClassId(sRootAttrId, attrMeta, relateUniqueList);
/* 1765 */         String key = sRootAttrId + KEY_FLAG + sRelatedClassId;
/* 1766 */         BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRelatedClassId);
/* 1767 */         String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, relatedClassMeta, relateUniqueList);
/* 1768 */         boolean bFlag = false;
/* 1769 */         for (int k = 0; k < uniqueIds.length; k++) {
/* 1770 */           String sLabel = "";
/* 1771 */           BMAttrMeta rMat = relatedClassMeta.getAttrMeta(uniqueIds[k]);
/* 1772 */           if (!rMat.getIsRelation().booleanValue()) {
/* 1773 */             sLabel = uploadClassMeta.getRelatedNoClassAttrLabel(RELATED_COL_FLAG + key + "-" + uniqueIds[k]);
/* 1774 */             if ((sLabel != null) && (!sLabel.equals(""))) {
/* 1775 */               bFlag = true;
/* 1776 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1781 */         String sClassCuid = checkAndConvRelated(sRootAttrId, sAttrId, uploadClassMeta, sRelatedClassId, matOneValueList, relateUniqueList, relatedMap);
/* 1782 */         if (sClassCuid.equals("")) {
/* 1783 */           if (attrMeta.getIsNotNull().booleanValue()) {
/* 1784 */             if (!sRelatedClassId.equals(sClassId))
/*      */             {
/* 1786 */               uploadClassMeta.setAttrErrorDetail(sAttrId, "转换属性[" + attrMeta.getLabelCn() + "]值有错");
/* 1787 */               uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1788 */               break;
/*      */             }
/* 1790 */             uploadClassMeta.setExcelAttrValue(sAttrId, "");
/*      */           }
/* 1793 */           else if (bFlag)
/*      */           {
/* 1795 */             if (!sRelatedClassId.equals(sClassId))
/*      */             {
/* 1797 */               uploadClassMeta.setAttrErrorDetail(sAttrId, "转换属性[" + attrMeta.getLabelCn() + "]值有错");
/* 1798 */               uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1799 */               break;
/*      */             }
/* 1801 */             uploadClassMeta.setExcelAttrValue(sAttrId, "");
/*      */           }
/*      */           else {
/* 1804 */             uploadClassMeta.setExcelAttrValue(sAttrId, "");
/*      */           }
/*      */         }
/*      */         else {
/* 1808 */           uploadClassMeta.setExcelAttrValue(sAttrId, sClassCuid);
/*      */         }
/*      */ 
/* 1811 */         if (attrMeta.getUpperAttrIds().size() <= 0)
/*      */           continue;
/* 1813 */         setUnpperAttrValue(sRootAttrId, attrMeta, sClassCuid, uploadClassMeta, relateUniqueList, relatedMap, matOneValueList);
/*      */       }
/*      */ 
/* 1818 */       if (uploadClassMeta.getDataType().equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 1823 */       Iterator iterator = uploadClassMeta.getUniqueAttrIds().keySet().iterator();
/* 1824 */       while (iterator.hasNext()) {
/* 1825 */         String sUniqueName = (String)iterator.next();
/* 1826 */         String sUniqueValue = sUniqueName + "-";
/* 1827 */         String[] uniqueIds = (String[])uploadClassMeta.getUniqueAttrIds().get(sUniqueName);
/* 1828 */         for (int j = 0; j < uniqueIds.length; j++) {
/* 1829 */           String sId = uniqueIds[j];
/* 1830 */           if (sUniqueValue.equals(""))
/* 1831 */             sUniqueValue = uploadClassMeta.getExcelAttrValue(sId);
/*      */           else {
/* 1833 */             sUniqueValue = sUniqueValue + "-" + uploadClassMeta.getExcelAttrValue(sId);
/*      */           }
/*      */         }
/*      */ 
/* 1837 */         Integer iRow = (Integer)uniqueValue.get(sUniqueValue);
/* 1838 */         if (iRow != null) {
/* 1839 */           uploadClassMeta.setRowErrorInfo("与第[" + iRow.intValue() + "]行记录重复");
/* 1840 */           uploadClassMeta.setDataType(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR);
/* 1841 */           break;
/*      */         }
/* 1843 */         uniqueValue.put(sUniqueValue, Integer.valueOf(i + DATA_BEGIN_LINE_IN_EXCEL + 1));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setUnpperAttrValue(String sRootAttrId, BMAttrMeta attrMeta, String sAttrCuid, DrmUploadClassMeta uploadClassMeta, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, String> relatedMap, Map<String, String> matOneValueList)
/*      */   {
/* 1855 */     IDrmQueryResultSet rs = null;
/* 1856 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 1857 */     DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/*      */ 
/* 1859 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), attrMeta.getBmClassId());
/*      */ 
/* 1861 */     String sRClassId = getRelatedClassId(sRootAttrId, attrMeta, relateUniqueList);
/* 1862 */     if (sRClassId.equals("")) return;
/* 1863 */     drmQuery.setBmClassId(sRClassId);
/* 1864 */     BMClassMeta rClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRClassId);
/*      */ 
/* 1867 */     for (int i = 0; i < attrMeta.getUpperAttrIds().size(); i++) {
/* 1868 */       String sUpperAttrId = (String)attrMeta.getUpperAttrIds().get(i);
/* 1869 */       BMAttrMeta upperAttrMeta = classMeta.getAttrMeta(sUpperAttrId);
/* 1870 */       String sUpperRClassId = getRelatedClassId(sRootAttrId, upperAttrMeta, relateUniqueList);
/* 1871 */       if (sUpperRClassId.equals(""))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 1878 */       String sCuid = "";
/* 1879 */       List attrList = rClassMeta.getRelatedClassAttrMeta(sUpperRClassId);
/* 1880 */       if (attrList.size() > 0) {
/* 1881 */         String key = sRootAttrId + KEY_FLAG + sUpperRClassId + KEY_FLAG + sUpperAttrId + KEY_FLAG + sAttrCuid;
/* 1882 */         sCuid = (String)matOneValueList.get(key);
/* 1883 */         if (sCuid != null) {
/* 1884 */           uploadClassMeta.setExcelAttrValue(sUpperAttrId, sCuid);
/*      */         } else {
/* 1886 */           if (rs == null) {
/* 1887 */             drmQuery.addQueryCondExps(new DrmQueryAttrCond(rClassMeta.getCuidAttrId(), "=", sAttrCuid));
/* 1888 */             rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmQuery);
/* 1889 */             if ((rs == null) || (rs.getResultSet().size() == 0))
/* 1890 */               return;
/*      */           }
/* 1892 */           sCuid = (String)((IDrmQueryRow)rs.getResultSet().get(0)).getAttrRow().get(sRClassId + "." + ((BMAttrMeta)attrList.get(0)).getAttrId());
/* 1893 */           uploadClassMeta.setExcelAttrValue(sUpperAttrId, sCuid);
/* 1894 */           matOneValueList.put(key, sCuid);
/*      */         }
/*      */       }
/*      */ 
/* 1898 */       if (upperAttrMeta.getUpperAttrIds().size() > 0)
/* 1899 */         setUnpperAttrValue(sRootAttrId, upperAttrMeta, sCuid, uploadClassMeta, relateUniqueList, relatedMap, matOneValueList);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String getUniqueOneValueKey(String sRootAttrId, String sAttrId, DrmUploadClassMeta uploadClassMeta, String sRelatedClassId, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, String> relatedMap)
/*      */   {
/* 1914 */     String sOneValue = ""; String sLabel = "";
/*      */ 
/* 1917 */     String key = sRootAttrId + KEY_FLAG + sRelatedClassId + KEY_FLAG + sAttrId;
/* 1918 */     String sClassCuid = (String)relatedMap.get(key);
/* 1919 */     if (sClassCuid != null) return sClassCuid;
/*      */ 
/* 1921 */     key = sRootAttrId + KEY_FLAG + sRelatedClassId;
/*      */ 
/* 1923 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRelatedClassId);
/* 1924 */     String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, relatedClassMeta, relateUniqueList);
/* 1925 */     for (int i = 0; i < uniqueIds.length; i++) {
/* 1926 */       BMAttrMeta rMat = relatedClassMeta.getAttrMeta(uniqueIds[i]);
/* 1927 */       if (rMat.getIsRelation().booleanValue())
/*      */       {
/* 1929 */         String sRClassId = getRelatedClassId(sRootAttrId, rMat, relateUniqueList);
/* 1930 */         sLabel = getUniqueOneValueKey(sRootAttrId, uniqueIds[i], uploadClassMeta, sRClassId, relateUniqueList, relatedMap);
/*      */       }
/*      */       else {
/* 1933 */         sLabel = uploadClassMeta.getRelatedNoClassAttrLabel(RELATED_COL_FLAG + key + "-" + uniqueIds[i]);
/* 1934 */         if ((sLabel == null) || (sLabel.equals(""))) return "";
/*      */       }
/* 1936 */       sOneValue = sOneValue + "-" + sLabel;
/*      */     }
/*      */ 
/* 1939 */     return sOneValue;
/*      */   }
/*      */ 
/*      */   private static String checkAndConvRelated(String sRootAttrId, String sAttrId, DrmUploadClassMeta uploadClassMeta, String sRelatedClassId, Map<String, String> matOneValueList, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, String> relatedMap)
/*      */   {
/* 1955 */     IDrmQueryResultSet rs = null;
/* 1956 */     IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 1957 */     DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/*      */ 
/* 1960 */     String key = sRootAttrId + KEY_FLAG + sRelatedClassId + KEY_FLAG + sAttrId;
/* 1961 */     String sClassCuid = (String)relatedMap.get(key);
/* 1962 */     if (sClassCuid != null) return sClassCuid;
/*      */ 
/* 1964 */     key = sRootAttrId + KEY_FLAG + sRelatedClassId;
/*      */ 
/* 1966 */     BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRelatedClassId);
/* 1967 */     Map labelMap = new HashMap();
/* 1968 */     String[] uniqueIds = getRelatedClassUniqueIds(sRootAttrId, relatedClassMeta, relateUniqueList);
/* 1969 */     String oneValueKey = sRelatedClassId;
/*      */ 
/* 1971 */     for (int i = 0; i < uniqueIds.length; i++) {
/* 1972 */       String sLabel = "";
/* 1973 */       BMAttrMeta rMat = relatedClassMeta.getAttrMeta(uniqueIds[i]);
/* 1974 */       if (rMat.getIsRelation().booleanValue())
/*      */       {
/* 1976 */         sLabel = getUniqueOneValueKey(sRootAttrId, uniqueIds[i], uploadClassMeta, sRelatedClassId, relateUniqueList, relatedMap);
/* 1977 */         if (sLabel.equals("")) return ""; 
/*      */       }
/*      */       else
/*      */       {
/* 1980 */         sLabel = uploadClassMeta.getRelatedNoClassAttrLabel(RELATED_COL_FLAG + key + "-" + uniqueIds[i]);
/* 1981 */         if ((sLabel == null) || (sLabel.equals(""))) return "";
/*      */       }
/* 1983 */       oneValueKey = oneValueKey + "-" + sLabel;
/* 1984 */       labelMap.put(uniqueIds[i], sLabel);
/*      */     }
/*      */ 
/* 1988 */     sClassCuid = (String)matOneValueList.get(oneValueKey);
/* 1989 */     if (sClassCuid != null) return sClassCuid;
/*      */ 
/* 1991 */     drmQuery.setBmClassId(sRelatedClassId);
/* 1992 */     for (int i = 0; i < uniqueIds.length; i++) {
/* 1993 */       BMAttrMeta rMat = relatedClassMeta.getAttrMeta(uniqueIds[i]);
/* 1994 */       if (rMat.getIsRelation().booleanValue()) {
/* 1995 */         String sRClassId = getRelatedClassId(sRootAttrId, rMat, relateUniqueList);
/* 1996 */         String sCuid = checkAndConvRelated(sRootAttrId, uniqueIds[i], uploadClassMeta, sRClassId, matOneValueList, relateUniqueList, relatedMap);
/* 1997 */         if (sCuid.equals("")) return "";
/* 1998 */         drmQuery.addQueryCondExps(new DrmQueryAttrCond(uniqueIds[i], "=", sCuid));
/*      */       } else {
/* 2000 */         String sLabel = (String)labelMap.get(uniqueIds[i]);
/* 2001 */         drmQuery.addQueryCondExps(new DrmQueryAttrCond(uniqueIds[i], "=", sLabel));
/*      */       }
/*      */     }
/* 2004 */     rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmQuery);
/* 2005 */     if ((rs == null) || (rs.getResultSet().size() == 0)) {
/* 2006 */       return "";
/*      */     }
/* 2008 */     sClassCuid = (String)((IDrmQueryRow)rs.getResultSet().get(0)).getAttrRow().get(sRelatedClassId + "." + relatedClassMeta.getCuidAttrId());
/* 2009 */     matOneValueList.put(oneValueKey, sClassCuid);
/* 2010 */     return sClassCuid;
/*      */   }
/*      */ 
/*      */   public static String getRelatedClassId(String sRootAttrId, BMAttrMeta attrMeta, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList)
/*      */   {
/* 2022 */     Map relatedAttrIds = attrMeta.getRelatedAttrIds();
/* 2023 */     if (relatedAttrIds == null) {
/* 2024 */       return "";
/*      */     }
/*      */ 
/* 2027 */     String sRelatedClassId = "";
/*      */ 
/* 2029 */     Map classRelatedMeta = null;
/* 2030 */     if (relatedAttrIds.size() > 1)
/*      */     {
/* 2032 */       Iterator iter = relatedAttrIds.keySet().iterator();
/* 2033 */       while (iter.hasNext()) {
/* 2034 */         String sClassId = (String)iter.next();
/* 2035 */         String key = sRootAttrId + KEY_FLAG + sClassId;
/* 2036 */         classRelatedMeta = (Map)relateUniqueList.get(key);
/* 2037 */         if (classRelatedMeta != null) {
/* 2038 */           sRelatedClassId = sClassId;
/* 2039 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2044 */       Iterator iter = relatedAttrIds.keySet().iterator();
/* 2045 */       if (iter.hasNext()) {
/* 2046 */         sRelatedClassId = (String)iter.next();
/*      */       }
/*      */     }
/*      */ 
/* 2050 */     return sRelatedClassId;
/*      */   }
/*      */ 
/*      */   private static String[] getRelatedClassUniqueIds(String sRootAttrId, BMClassMeta classMeta, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList)
/*      */   {
/* 2060 */     String[] uniqueAttrIds = null;
/*      */ 
/* 2062 */     String sClassId = classMeta.getBmClassId();
/* 2063 */     String key = sRootAttrId + KEY_FLAG + sClassId;
/*      */ 
/* 2065 */     if (classMeta.getUniqueAttrIds().size() > 1)
/*      */     {
/* 2067 */       if (relateUniqueList.get(key) != null) {
/* 2068 */         ExcelImpTemplateRelatedMeta excelImpTemplateRelatedMeta = (ExcelImpTemplateRelatedMeta)((Map)relateUniqueList.get(key)).get("unique");
/* 2069 */         if (excelImpTemplateRelatedMeta != null)
/*      */         {
/* 2071 */           uniqueAttrIds = (String[])(String[])classMeta.getUniqueAttrIds().get(excelImpTemplateRelatedMeta.getClassUniqueName());
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2076 */       Iterator uniqueIter = classMeta.getUniqueAttrIds().keySet().iterator();
/* 2077 */       String uniqueId = (String)uniqueIter.next();
/* 2078 */       uniqueAttrIds = (String[])(String[])classMeta.getUniqueAttrIds().get(uniqueId);
/*      */     }
/*      */ 
/* 2081 */     return uniqueAttrIds;
/*      */   }
/*      */ 
/*      */   public static String getRelatedClassValueList(String sRootAttrId, BMAttrMeta attrMeta, String sAttrValue, Map<String, Map<String, ExcelImpTemplateRelatedMeta>> relateUniqueList, Map<String, Map<String, String>> relatedClassValueMap)
/*      */   {
/* 2095 */     String sRelatedClassId = ""; String sReturn = "";
/* 2096 */     String[] uniqueIds = null;
/*      */ 
/* 2098 */     sRelatedClassId = getRelatedClassId(sRootAttrId, attrMeta, relateUniqueList);
/* 2099 */     if (sRelatedClassId.equals("")) {
/* 2100 */       return "属性 [" + attrMeta.getLabelCn() + "] 有多个关联类，未设置，无法确定";
/*      */     }
/* 2102 */     if (relatedClassValueMap.get(sRelatedClassId) != null)
/*      */     {
/* 2104 */       return "";
/*      */     }
/* 2106 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), sRelatedClassId);
/*      */ 
/* 2108 */     uniqueIds = getRelatedClassUniqueIds(sRootAttrId, classMeta, relateUniqueList);
/* 2109 */     if (uniqueIds == null) {
/* 2110 */       return "关联类 '" + classMeta.getLabelCn() + "' 唯一性描述未指定";
/*      */     }
/*      */ 
/* 2113 */     Map relatedValueMap = new LinkedHashMap();
/* 2114 */     String sRelatedCuid = "";
/* 2115 */     IDrmQueryResultSet rs = null;
/* 2116 */     if (!sAttrValue.equals(""))
/*      */     {
/* 2118 */       IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 2119 */       DrmSingleClassQuery drmQuery = new DrmSingleClassQuery();
/* 2120 */       drmQuery.setBmClassId(sRelatedClassId);
/* 2121 */       drmQuery.addQueryCondExps(new DrmQueryAttrCond(classMeta.getCuidAttrId(), "=", sAttrValue));
/* 2122 */       rs = getDynResManageService().getDynObjBySql(ServiceHelper.createSvActCxt(), queryContext, drmQuery);
/* 2123 */       if ((rs == null) || (rs.getResultSet().size() == 0)) {
/* 2124 */         return "业务类[" + classMeta.getLabelCn() + "]中未发现" + classMeta.getCuidAttrId() + "为[" + sAttrValue + "]的记录";
/*      */       }
/*      */ 
/* 2133 */       relatedValueMap.put(classMeta.getCuidAttrId(), sAttrValue);
/*      */ 
/* 2136 */       for (int i = 0; i < uniqueIds.length; i++) {
/* 2137 */         BMAttrMeta mat = classMeta.getAttrMeta(uniqueIds[i]);
/*      */ 
/* 2139 */         if (mat.getIsRelation().booleanValue())
/*      */           continue;
/* 2141 */         String sValue = ((IDrmQueryRow)rs.getResultSet().get(0)).getAttrRow().get(sRelatedClassId + "." + uniqueIds[i]).toString();
/*      */ 
/* 2143 */         relatedValueMap.put(uniqueIds[i], sValue);
/*      */       }
/*      */     }
/*      */ 
/* 2147 */     relatedClassValueMap.put(sRootAttrId + KEY_FLAG + sRelatedClassId, relatedValueMap);
/*      */ 
/* 2149 */     for (int i = 0; i < uniqueIds.length; i++) {
/* 2150 */       String attrId = uniqueIds[i];
/*      */ 
/* 2152 */       BMAttrMeta relatedAttrMeta = (BMAttrMeta)classMeta.getAllAttrMetas().get(attrId);
/* 2153 */       if (!relatedAttrMeta.getIsRelation().booleanValue()) {
/*      */         continue;
/*      */       }
/* 2156 */       if (!sAttrValue.equals("")) {
/* 2157 */         sRelatedCuid = (String)((IDrmQueryRow)rs.getResultSet().get(0)).getAttrRow().get(sRelatedClassId + "." + attrId);
/*      */       }
/* 2159 */       sReturn = getRelatedClassValueList(sRootAttrId, relatedAttrMeta, sRelatedCuid, relateUniqueList, relatedClassValueMap);
/* 2160 */       if (!sReturn.equals("")) {
/* 2161 */         return sReturn;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2166 */     return "";
/*      */   }
/*      */ 
/*      */   public static String createResultFile(String sFileName, String contextPath, String contextUrl, String doType, Workbook rwb, List<DrmUploadClassMeta> allData, List<String> errorList)
/*      */   {
/* 2179 */     WritableWorkbook wwb = null;
/* 2180 */     String destFileName = ""; String fileUrl = "";
/* 2181 */     String outPutPath = contextPath + File.separator + "import";
/* 2182 */     createDir(outPutPath);
/*      */     try
/*      */     {
/* 2185 */       String fileName = "";
/* 2186 */       if (doType.equals("check"))
/* 2187 */         fileName = "CheckResult_" + sFileName;
/*      */       else {
/* 2189 */         fileName = "ImportResult_" + sFileName;
/*      */       }
/* 2191 */       String _fileName = fileName;
/* 2192 */       destFileName = outPutPath + File.separator + fileName;
/* 2193 */       fileUrl = contextUrl + "//import//" + _fileName;
/*      */ 
/* 2195 */       wwb = Workbook.createWorkbook(new File(destFileName), rwb);
/*      */ 
/* 2197 */       WritableSheet ws = wwb.getSheet(0);
/*      */ 
/* 2200 */       addImportResultHeader(ws);
/*      */ 
/* 2202 */       markupErrorExcel(ws, allData, doType, errorList);
/*      */ 
/* 2204 */       wwb.write();
/* 2205 */       wwb.close();
/* 2206 */       rwb.close();
/*      */     } catch (Exception ex) {
/* 2208 */       errorList.add("生成结果文件异常：" + ex.getMessage());
/*      */     }
/*      */ 
/* 2211 */     return fileUrl;
/*      */   }
/*      */ 
/*      */   private static void markupErrorExcel(WritableSheet sheet, List<DrmUploadClassMeta> allData, String doType, List<String> errorList)
/*      */   {
/*      */     try
/*      */     {
/* 2225 */       WritableCellFormat cellFormat = null;
/* 2226 */       WritableFont font = null;
/*      */ 
/* 2229 */       for (int i = 0; i < allData.size(); i++) {
/* 2230 */         DrmUploadClassMeta uploadClassMeta = (DrmUploadClassMeta)allData.get(i);
/* 2231 */         String sDataType = uploadClassMeta.getDataType();
/* 2232 */         String sImportResult = "";
/* 2233 */         String sErrorDetail = "";
/*      */ 
/* 2235 */         if (sDataType.equals(DrmUploadClassMeta.DATA_TYPE.DATA_TYPE_ERROR))
/*      */         {
/* 2237 */           font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
/*      */ 
/* 2243 */           if (doType.equals("check"))
/* 2244 */             sImportResult = "数据有错";
/*      */           else {
/* 2246 */             sImportResult = "未导入";
/*      */           }
/* 2248 */           Collection bufs = uploadClassMeta.getAttrErrorDetailMap().values();
/* 2249 */           Iterator iter = bufs.iterator();
/* 2250 */           while (iter.hasNext()) {
/* 2251 */             String sErrorInfo = (String)iter.next();
/* 2252 */             sErrorDetail = sErrorDetail + sErrorInfo + ";";
/*      */           }
/* 2254 */           if (!uploadClassMeta.getRowErrorInfo().equals(""))
/* 2255 */             sErrorDetail = sErrorDetail + uploadClassMeta.getRowErrorInfo() + ";";
/*      */         }
/* 2257 */         else if (doType.equals("check")) {
/* 2258 */           sImportResult = "数据无错";
/* 2259 */           font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
/*      */         }
/*      */         else
/*      */         {
/* 2266 */           sImportResult = uploadClassMeta.getImportResultInfo();
/* 2267 */           if (sImportResult.equals("失败")) {
/* 2268 */             font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
/*      */ 
/* 2274 */             sErrorDetail = uploadClassMeta.getSaveErrorInfo();
/* 2275 */           } else if (sImportResult.equals("未导入")) {
/* 2276 */             font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLUE);
/*      */ 
/* 2282 */             if (doType.equals("add"))
/* 2283 */               sErrorDetail = "只导入增加数据，所以无需导入";
/* 2284 */             else if (doType.equals("add"))
/* 2285 */               sErrorDetail = "只导入修改数据，所以无需导入";
/*      */           }
/*      */           else {
/* 2288 */             font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2298 */         cellFormat = new WritableCellFormat(font);
/* 2299 */         cellFormat.setWrap(false);
/* 2300 */         cellFormat.setAlignment(Alignment.LEFT);
/* 2301 */         cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/* 2303 */         int rowIndex = uploadClassMeta.getExcelRow() - 1;
/* 2304 */         Cell[] cells = sheet.getRow(LABEL_LINE_IN_EXCEL);
/* 2305 */         int colCount = cells.length;
/* 2306 */         int typeColIndex = colCount - 3;
/* 2307 */         int resultColIndex = colCount - 2;
/* 2308 */         int infoColIndex = colCount - 1;
/*      */ 
/* 2310 */         Label label = new Label(typeColIndex, rowIndex, sDataType, cellFormat);
/* 2311 */         sheet.addCell(label);
/*      */ 
/* 2313 */         label = new Label(resultColIndex, rowIndex, sImportResult, cellFormat);
/* 2314 */         sheet.addCell(label);
/* 2315 */         label = new Label(infoColIndex, rowIndex, sErrorDetail, cellFormat);
/* 2316 */         sheet.addCell(label);
/*      */       }
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/* 2321 */       System.out.println("markupErrorExcel: " + ex.getMessage());
/* 2322 */       errorList.add("标记错误信息出错：" + ex.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void createDir(String path) {
/* 2327 */     File d = new File(path);
/* 2328 */     if (!d.exists())
/* 2329 */       d.mkdir();
/*      */   }
/*      */ 
/*      */   public static void addImportResultHeader(WritableSheet sheet)
/*      */     throws WriteException
/*      */   {
/* 2336 */     WritableCellFormat cellFormat = null;
/* 2337 */     WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
/*      */ 
/* 2343 */     cellFormat = new WritableCellFormat(font);
/* 2344 */     cellFormat.setWrap(true);
/* 2345 */     cellFormat.setBackground(Colour.LIGHT_GREEN);
/* 2346 */     cellFormat.setAlignment(Alignment.CENTRE);
/* 2347 */     cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
/*      */ 
/* 2349 */     Cell[] cells = sheet.getRow(ATTR_LINE_IN_EXCEL);
/* 2350 */     int cellLength = cells.length;
/* 2351 */     Label l = new Label(cellLength, LABEL_LINE_IN_EXCEL, "导入方式", cellFormat);
/* 2352 */     sheet.addCell(l);
/* 2353 */     cellLength++;
/* 2354 */     l = new Label(cellLength, LABEL_LINE_IN_EXCEL, "结果", cellFormat);
/* 2355 */     sheet.addCell(l);
/* 2356 */     cellLength++;
/* 2357 */     l = new Label(cellLength, LABEL_LINE_IN_EXCEL, "详细日志", cellFormat);
/* 2358 */     sheet.addCell(l);
/* 2359 */     sheet.setColumnView(cellLength, 100);
/*      */   }
/*      */ 
/*      */   private static WritableCellFormat getCellFormat() throws Exception {
/* 2363 */     WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
/*      */ 
/* 2369 */     WritableCellFormat cellFormat = new WritableCellFormat(font);
/* 2370 */     cellFormat.setBackground(Colour.RED);
/* 2371 */     return cellFormat;
/*      */   }
/*      */ 
/*      */   public static WritableWorkbook createWorkBookByTemplate(String templateXlsPath, OutputStream out)
/*      */     throws Exception
/*      */   {
/* 2377 */     Workbook templateWB = Workbook.getWorkbook(FileHelper.getFileStream(templateXlsPath));
/* 2378 */     return Workbook.createWorkbook(out, templateWB);
/*      */   }
/*      */ 
/*      */   public static WritableWorkbook createEmptyWorkBook(OutputStream out) throws Exception {
/* 2382 */     return Workbook.createWorkbook(out);
/*      */   }
/*      */ 
/*      */   public static void closeWritableWorkbook(WritableWorkbook wb) throws Exception {
/* 2386 */     if (wb != null) {
/* 2387 */       wb.write();
/* 2388 */       wb.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void writeCell(WritableCell cell, String val) {
/* 2393 */     if (val == null) {
/* 2394 */       return;
/*      */     }
/*      */ 
/* 2397 */     if (cell.getType() == CellType.LABEL) {
/* 2398 */       ((Label)cell).setString(val.toString());
/* 2399 */     } else if (cell.getType() == CellType.NUMBER) {
/* 2400 */       ((Number)cell).setValue(Double.parseDouble(val));
/* 2401 */     } else if (cell.getType() == CellType.BOOLEAN) {
/* 2402 */       ((jxl.write.Boolean)cell).setValue(java.lang.Boolean.getBoolean(val));
/* 2403 */     } else if (cell.getType() == CellType.DATE) {
/* 2404 */       java.util.Date date = convToCellDate(val);
/* 2405 */       ((DateTime)cell).setDate(date);
/*      */     } else {
/* 2407 */       LogHome.getLog().error("Excel未处理的单元类型写入[rowIndex=" + cell.getRow() + ", colIndex=" + cell.getColumn() + ", cellType=" + cell.getType() + "]");
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void writeCell(WritableSheet sheet, int rowIndex, int colIndex, String val) throws Exception
/*      */   {
/* 2413 */     WritableCell cell = sheet.getWritableCell(colIndex, rowIndex);
/* 2414 */     writeCell(cell, val);
/*      */   }
/*      */ 
/*      */   public static void writeTitle(WritableSheet sheet, int rowIndex, int colIndex, String val) throws Exception {
/* 2418 */     WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 11, WritableFont.BOLD, false);
/* 2419 */     font.setColour(Colour.BLUE);
/* 2420 */     WritableCellFormat cellFormat = new WritableCellFormat(font);
/* 2421 */     cellFormat.setAlignment(Alignment.CENTRE);
/* 2422 */     cellFormat.setWrap(false);
/*      */ 
/* 2424 */     cellFormat.setBackground(Colour.GRAY_25);
/* 2425 */     Label label = new Label(colIndex, rowIndex, "", cellFormat);
/* 2426 */     label.getCellFormat();
/* 2427 */     label.setString(val);
/* 2428 */     sheet.addCell(label);
/*      */   }
/*      */ 
/*      */   public static void insertRowsByTemplate(WritableSheet sheet, int startRowIndex, int insertRowNum, List<WritableCell> rowCellTemplateList) throws Exception
/*      */   {
/* 2433 */     CellView rowView = sheet.getRowView(startRowIndex);
/* 2434 */     for (int i = 0; i < insertRowNum; i++) {
/* 2435 */       int rowIndex = startRowIndex + i;
/* 2436 */       sheet.setRowView(rowIndex, rowView.getSize());
/* 2437 */       for (int j = 0; j < rowCellTemplateList.size(); j++) {
/* 2438 */         WritableCell cellTemplate = (WritableCell)rowCellTemplateList.get(j);
/* 2439 */         WritableCell copiedCell = cellTemplate.copyTo(cellTemplate.getColumn(), rowIndex);
/*      */ 
/* 2441 */         sheet.addCell(copiedCell);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String convToCellDateStr(java.util.Date date) {
/* 2447 */     return TimeFormatHelper.getFormatDate(date, "yyyy-MM-dd HH:mm:ss");
/*      */   }
/*      */ 
/*      */   public static java.util.Date convToCellDate(String dateStr) {
/* 2451 */     return TimeFormatHelper.convertDate(dateStr, "yyyy-MM-dd HH:mm:ss");
/*      */   }
/*      */ 
/*      */   public static WritableCell createLabelCell(int colIndex, int rowIndex, boolean isWrapper) {
/* 2455 */     WritableCellFormat cellFormat = new WritableCellFormat();
/*      */     try {
/* 2457 */       cellFormat.setWrap(isWrapper);
/*      */     } catch (Exception ex) {
/* 2459 */       ex.printStackTrace();
/*      */     }
/* 2461 */     Label label = new Label(colIndex, rowIndex, "");
/* 2462 */     label.setCellFormat(cellFormat);
/* 2463 */     return label;
/*      */   }
/*      */ 
/*      */   public static WritableCell createDoubleCell(int colIndex, int rowIndex) {
/* 2467 */     Number cell = new Number(colIndex, rowIndex, 0.0D);
/*      */     try {
/* 2469 */       WritableCellFormat cellFormat = new WritableCellFormat();
/* 2470 */       cellFormat.setAlignment(Alignment.LEFT);
/* 2471 */       cell.setCellFormat(cellFormat); } catch (WriteException ex) {
/*      */     }
/* 2473 */     return cell;
/*      */   }
/*      */ 
/*      */   public static WritableCell createIntegerCell(int colIndex, int rowIndex) {
/* 2477 */     Number cell = new Number(colIndex, rowIndex, 0.0D, new WritableCellFormat(NumberFormats.INTEGER));
/*      */     try {
/* 2479 */       WritableCellFormat cellFormat = new WritableCellFormat();
/* 2480 */       cellFormat.setAlignment(Alignment.LEFT);
/* 2481 */       cell.setCellFormat(cellFormat); } catch (WriteException ex) {
/*      */     }
/* 2483 */     return cell;
/*      */   }
/*      */ 
/*      */   public static WritableCell createDateCell(int colIndex, int rowIndex) {
/* 2487 */     DateTime cell = new DateTime(colIndex, rowIndex, new java.util.Date());
/*      */     try {
/* 2489 */       DateFormat df = new DateFormat("yyyy-MM-dd");
/* 2490 */       WritableCellFormat dateFormat = new WritableCellFormat(df);
/* 2491 */       dateFormat.setAlignment(Alignment.LEFT);
/* 2492 */       cell.setCellFormat(dateFormat); } catch (Exception ex) {
/*      */     }
/* 2494 */     return cell;
/*      */   }
/*      */ 
/*      */   public static WritableCell createBoolCell(int colIndex, int rowIndex) {
/* 2498 */     return new jxl.write.Boolean(colIndex, rowIndex, false);
/*      */   }
/*      */ 
/*      */   private static IBMModelService getBMModelService() {
/* 2502 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*      */   }
/*      */ 
/*      */   private static IVMModelService getVMModelService() {
/* 2506 */     return (IVMModelService)ServiceHomeFactory.getInstance().getService("VMModelService");
/*      */   }
/*      */ 
/*      */   private static IDynResManageService getDynResManageService() {
/* 2510 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*      */   }
/*      */ 
/*      */   public class attrType
/*      */   {
/*      */     public static final String isString = "String";
/*      */     public static final String isLong = "Integer";
/*      */     public static final String isDouble = "Float";
/*      */     public static final String isDate = "Date";
/*      */     public static final String isBoolean = "Boolean";
/*      */ 
/*      */     public attrType()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.excel.ExcelHelper
 * JD-Core Version:    0.6.0
 */