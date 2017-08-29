/*     */ package com.boco.raptor.drm.core.service.dynres.impl;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.except.UserException;
/*     */ import com.boco.raptor.common.service.IServiceActionContext;
/*     */ import com.boco.raptor.common.service.ServiceHelper;
/*     */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmEntityFactory;
/*     */ import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
/*     */ import com.boco.raptor.drm.core.dto.IDrmDataObject;
/*     */ import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryContext;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
/*     */ import com.boco.raptor.drm.core.dto.IDrmQueryRow;
/*     */ import com.boco.raptor.drm.core.meta.BMClassMeta;
/*     */ import com.boco.raptor.drm.core.meta.RelatedClassTreeNode;
/*     */ import com.boco.raptor.drm.core.meta.RelatedClassTreeNode.RelatedType;
/*     */ import com.boco.raptor.drm.core.meta.RelatedClassTreeNode.SearchType;
/*     */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageService;
/*     */ import com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO;
/*     */ import com.boco.raptor.drm.core.service.dynres.IRelatedObjectHandler;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class RelatedObjectService
/*     */ {
/*  46 */   private Map<String, IRelatedObjectHandler> relatedObjectHandlers = new HashMap();
/*     */   private IDynResManageServiceDAO serviceDAO;
/*     */   private MemoryQueryService memoryQueryService;
/*     */ 
/*     */   public Map<String, Integer> getRelatedDeleteObjectCount(IServiceActionContext actionContext, IDrmDataObject deleteObj, boolean isCascade)
/*     */     throws UserException
/*     */   {
/*  55 */     if ((deleteObj.getCuid() == null) || (deleteObj.getCuid().trim().length() == 0)) {
/*  56 */       throw new UserException("要删除的对象属性不完整，必须有CUID才可以");
/*     */     }
/*  58 */     Map result = new HashMap();
/*  59 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), deleteObj.getBmClassId());
/*  60 */     if (classMeta == null) {
/*  61 */       throw new UserException("业务模型没有配置，bmClassId=" + deleteObj.getBmClassId());
/*     */     }
/*  63 */     RelatedClassTreeNode rootNode = classMeta.getRelatedClassNode();
/*  64 */     if (rootNode == null) return result;
/*     */ 
/*  66 */     List directCascadeNodes = new ArrayList();
/*  67 */     if (isCascade) {
/*  68 */       directCascadeNodes = getDirectCascadeNodes(deleteObj.getBmClassId());
/*     */     }
/*  70 */     List constr2manualNodes = rootNode.getFirstConstr2ManualNodes();
/*  71 */     constr2manualNodes = filterSameClassNode(constr2manualNodes);
/*     */ 
/*  73 */     setDirectRelatedObjectCount(actionContext, deleteObj, directCascadeNodes, result);
/*  74 */     setConstr2ManulObjectCount(actionContext, deleteObj, constr2manualNodes, result);
/*     */ 
/*  77 */     return result;
/*     */   }
/*     */ 
/*     */   private List<RelatedClassTreeNode> filterSameClassNode(List<RelatedClassTreeNode> nodes) {
/*  81 */     Map nodeTable = new HashMap();
/*  82 */     for (RelatedClassTreeNode node : nodes) {
/*  83 */       nodeTable.put(node.getBmClassKey(), node);
/*     */     }
/*  85 */     List filterNodes = new ArrayList();
/*  86 */     filterNodes.addAll(nodeTable.values());
/*  87 */     return filterNodes;
/*     */   }
/*     */ 
/*     */   private void setConstr2ManulObjectCount(IServiceActionContext actionContext, IDrmDataObject deleteObj, List<RelatedClassTreeNode> constr2manualNodes, Map<String, Integer> result)
/*     */   {
/*  92 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), deleteObj.getBmClassId());
/*  93 */     RelatedClassTreeNode rootNode = classMeta.getRelatedClassNode();
/*     */ 
/*  95 */     for (RelatedClassTreeNode constr2manualNode : constr2manualNodes) {
/*  96 */       BMClassMeta constrClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), constr2manualNode.getBmClassId());
/*     */ 
/*  98 */       if (constr2manualNode.isRootDirectNode()) {
/*  99 */         List directChildNodes = new ArrayList();
/* 100 */         directChildNodes.add(constr2manualNode);
/* 101 */         setDirectRelatedObjectCount(actionContext, deleteObj, directChildNodes, result);
/*     */       } else {
/* 103 */         List shortPath = getRawShortTreePath(rootNode, constr2manualNode.getBmClassId(), constr2manualNode.isSelfChildNode());
/*     */ 
/* 105 */         shortPath.remove(0);
/* 106 */         List parentCuids = new ArrayList();
/* 107 */         parentCuids.add(deleteObj.getCuid());
/* 108 */         for (RelatedClassTreeNode node : shortPath) {
/* 109 */           if (parentCuids.size() == 0) {
/*     */             break;
/*     */           }
/* 112 */           parentCuids = getNodeCuidList(actionContext, node, parentCuids, deleteObj);
/* 113 */           setNodeCount(node, parentCuids.size(), result);
/*     */         }
/*     */ 
/* 116 */         int count = 0;
/* 117 */         for (String parentCuid : parentCuids) {
/* 118 */           count += getRelatedCountByCuid(actionContext, constrClassMeta, constr2manualNode.getParentAttrIds(), parentCuid);
/*     */         }
/* 120 */         setNodeCount(constr2manualNode, count, result);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setDirectRelatedObjectCount(IServiceActionContext actionContext, IDrmDataObject deleteObj, List<RelatedClassTreeNode> directChildNodes, Map<String, Integer> result)
/*     */   {
/* 127 */     for (RelatedClassTreeNode childNode : directChildNodes) {
/* 128 */       String childBmClassId = childNode.getBmClassId();
/* 129 */       BMClassMeta classMeta = childNode.getClassMeta();
/*     */       try {
/* 131 */         if (childNode.getSearchType() == RelatedClassTreeNode.SearchType.JAVA) {
/* 132 */           IRelatedObjectHandler handler = (IRelatedObjectHandler)this.relatedObjectHandlers.get(childNode.getBmClassId());
/* 133 */           if (handler != null) {
/* 134 */             int count = handler.getRelatedDeleteObjectCount(childBmClassId, classMeta, deleteObj);
/* 135 */             setNodeCount(childNode, count, result);
/*     */           }
/*     */         } else {
/* 138 */           int count = getRelatedCountByCuid(actionContext, classMeta, childNode.getRootAttrIds(), deleteObj.getCuid());
/*     */ 
/* 140 */           setNodeCount(childNode, count, result);
/*     */         }
/*     */       } catch (Exception ex) {
/* 143 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setNodeCount(RelatedClassTreeNode node, int count, Map<String, Integer> result) {
/* 149 */     result.put(node.getBmClassKey(), Integer.valueOf(count));
/*     */   }
/*     */ 
/*     */   private List<String> getNodeCuidList(IServiceActionContext actionContext, RelatedClassTreeNode node, List<String> parentCuids, IDrmDataObject deleteObj)
/*     */   {
/* 155 */     List cuids = new ArrayList();
/* 156 */     String bmClassId = node.getBmClassId();
/* 157 */     BMClassMeta classMeta = node.getClassMeta();
/* 158 */     for (String parentCuid : parentCuids) {
/*     */       try {
/* 160 */         if (node.getSearchType() == RelatedClassTreeNode.SearchType.JAVA) {
/* 161 */           IRelatedObjectHandler handler = (IRelatedObjectHandler)this.relatedObjectHandlers.get(node.getBmClassId());
/* 162 */           if (handler != null)
/* 163 */             handler.getRelatedDeleteCuids(bmClassId, classMeta, deleteObj);
/*     */         }
/*     */         else {
/* 166 */           List relatedAttrIds = node.getParentAttrIds();
/* 167 */           if ((relatedAttrIds != null) && (relatedAttrIds.size() > 0)) {
/* 168 */             String sqlCond = "";
/* 169 */             for (int i = 0; i < relatedAttrIds.size(); i++) {
/* 170 */               if (i > 0) {
/* 171 */                 sqlCond = sqlCond + " or ";
/*     */               }
/* 173 */               sqlCond = sqlCond + (String)relatedAttrIds.get(i) + "='" + parentCuid + "'";
/*     */             }
/* 175 */             DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 176 */             query.setBmClassId(bmClassId);
/* 177 */             query.setClassMeta(classMeta);
/* 178 */             query.addQueryAttrId("CUID");
/* 179 */             query.setSqlCond(sqlCond);
/* 180 */             IDrmQueryContext queryContext = DrmEntityFactory.getInstance().createQueryContext();
/* 181 */             IDrmQueryResultSet rs = this.serviceDAO.getDynObjBySql(actionContext, queryContext, query);
/* 182 */             for (IDrmQueryRow row : rs.getResultSet()) {
/* 183 */               IDrmDataObject dbo = row.getResultDbo(classMeta.getDbClassId());
/* 184 */               cuids.add(dbo.getCuid());
/*     */             }
/*     */           } else {
/* 187 */             LogHome.getLog().error("无法获取上级节点，nodeBmClassId=" + node.getBmClassId());
/*     */           }
/*     */         }
/*     */       } catch (Exception ex) {
/* 191 */         LogHome.getLog().error("", ex);
/*     */       }
/*     */     }
/* 194 */     return cuids;
/*     */   }
/*     */ 
/*     */   private int getRelatedCountByCuid(IServiceActionContext actionContext, BMClassMeta classMeta, List<String> relatedAttrIds, String cuid)
/*     */   {
/* 199 */     int count = 0;
/*     */     try {
/* 201 */       String dbClassId = classMeta.getDbClassId();
/* 202 */       String sql = "select count(*) from " + dbClassId + " where ";
/* 203 */       for (int i = 0; i < relatedAttrIds.size(); i++) {
/* 204 */         if (i > 0) {
/* 205 */           sql = sql + " or ";
/*     */         }
/* 207 */         String relatedAttrId = (String)relatedAttrIds.get(i);
/* 208 */         sql = sql + relatedAttrId + "='" + cuid + "'";
/* 209 */         if (classMeta.isChildBmClass()) {
/* 210 */           sql = sql + " and " + classMeta.getBmDivideSqlCond();
/*     */         }
/*     */       }
/* 213 */       count = this.serviceDAO.getDynObjCount(actionContext, sql);
/*     */     } catch (Exception ex) {
/* 215 */       LogHome.getLog().error("", ex);
/*     */     }
/* 217 */     return count;
/*     */   }
/*     */ 
/*     */   public void setRelatedObjectHandlers(List<IRelatedObjectHandler> roHandlers) {
/* 221 */     for (Iterator i$ = roHandlers.iterator(); i$.hasNext(); ) { roHandler = (IRelatedObjectHandler)i$.next();
/* 222 */       for (String bmClassId : roHandler.getSupportClassIds())
/* 223 */         this.relatedObjectHandlers.put(bmClassId, roHandler); }
/*     */     IRelatedObjectHandler roHandler;
/*     */   }
/*     */ 
/*     */   private List<RelatedClassTreeNode> getDirectCascadeNodes(String bmClassId) {
/* 229 */     List directNodes = new ArrayList();
/* 230 */     BMClassMeta classMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), bmClassId);
/* 231 */     RelatedClassTreeNode rootNode = classMeta.getRelatedClassNode();
/* 232 */     for (RelatedClassTreeNode childNode : rootNode.getAllChildNodes()) {
/* 233 */       if ((childNode.getRelatedType() != RelatedClassTreeNode.RelatedType.CASCADE) || (childNode.isSelfChildNode())) {
/*     */         continue;
/*     */       }
/* 236 */       List treePath = getRawShortTreePath(rootNode, childNode.getBmClassId(), childNode.isSelfChildNode());
/* 237 */       if (treePath.size() == 2) {
/* 238 */         directNodes.add(childNode);
/*     */       }
/*     */     }
/* 241 */     return directNodes;
/*     */   }
/*     */ 
/*     */   private List<RelatedClassTreeNode> getRealShortTreePath(RelatedClassTreeNode rootNode, List<RelatedClassTreeNode> path, boolean isSelfChildNode)
/*     */   {
/* 247 */     List shortPath = path;
/* 248 */     if (path.size() > 2) {
/* 249 */       List[] shortPaths = new ArrayList[path.size() - 2];
/* 250 */       for (int i = 0; i < path.size() - 2; i++) {
/* 251 */         shortPaths[i] = getRawShortTreePath(rootNode, ((RelatedClassTreeNode)path.get(i + 2)).getBmClassId(), isSelfChildNode);
/* 252 */         for (int k = i + 3; k < path.size(); k++) {
/* 253 */           shortPaths[i].add(path.get(k));
/*     */         }
/*     */       }
/*     */ 
/* 257 */       for (int i = 0; i < shortPaths.length; i++) {
/* 258 */         if (shortPaths[i].size() < shortPath.size()) {
/* 259 */           shortPath = shortPaths[i];
/*     */         }
/*     */       }
/*     */     }
/* 263 */     return shortPath;
/*     */   }
/*     */ 
/*     */   private List<RelatedClassTreeNode> getRawShortTreePath(RelatedClassTreeNode rootNode, String searchBmClassId, boolean isSelfChildNode)
/*     */   {
/* 268 */     List shortPath = new ArrayList();
/* 269 */     List searchNodes = rootNode.searchChildNodes(searchBmClassId, isSelfChildNode);
/*     */ 
/* 271 */     List[] shortPaths = new ArrayList[searchNodes.size()];
/* 272 */     for (int i = 0; i < searchNodes.size(); i++) {
/* 273 */       RelatedClassTreeNode searchNode = (RelatedClassTreeNode)searchNodes.get(i);
/* 274 */       if (searchNode.isRootDirectNode()) {
/* 275 */         shortPath.add(rootNode);
/* 276 */         shortPath.add(searchNode);
/* 277 */         return shortPath;
/*     */       }
/*     */ 
/* 280 */       shortPaths[i] = rootNode.getParentTreePath(searchNode);
/* 281 */       shortPaths[i].add(searchNode);
/*     */     }
/*     */ 
/* 284 */     for (int i = 0; i < shortPaths.length; i++) {
/* 285 */       if (i == 0)
/* 286 */         shortPath = shortPaths[i];
/* 287 */       else if (shortPaths[i].size() < shortPath.size()) {
/* 288 */         shortPath = shortPaths[i];
/*     */       }
/*     */     }
/* 291 */     return shortPath;
/*     */   }
/*     */ 
/*     */   public IDrmQueryResultSet getRelatedDeleteObjects(IServiceActionContext actionContext, IDrmMemQueryContext queryContext, DrmSingleClassQuery query, IDrmDataObject deleteObj, int relatedCount, boolean isSelfChildNode) throws UserException
/*     */   {
/* 296 */     IDrmQueryResultSet result = null;
/* 297 */     BMClassMeta deleteClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), deleteObj.getBmClassId());
/* 298 */     String relatedBmClassId = query.getBmClassId();
/* 299 */     RelatedClassTreeNode rootNode = deleteClassMeta.getRelatedClassNode();
/* 300 */     List shortPath = getRawShortTreePath(rootNode, relatedBmClassId, isSelfChildNode);
/*     */ 
/* 302 */     if (shortPath.size() < 2) {
/* 303 */       LogHome.getLog().error("关联路径搜索错误：relatedBmClassId=" + relatedBmClassId);
/* 304 */       return null;
/*     */     }
/*     */ 
/* 307 */     List relatedNodes = rootNode.searchChildNodes(relatedBmClassId, isSelfChildNode);
/* 308 */     RelatedClassTreeNode relatedNode = (RelatedClassTreeNode)relatedNodes.get(0);
/*     */ 
/* 310 */     if ((shortPath.size() == 2) && (relatedNode.getSearchType() == RelatedClassTreeNode.SearchType.CLASS)) {
/* 311 */       result = getRelatedObjects(actionContext, queryContext, relatedNode, deleteObj.getCuid(), query);
/* 312 */       return result;
/*     */     }
/*     */ 
/* 316 */     if (!queryContext.isRefresh()) {
/* 317 */       result = this.memoryQueryService.getResultSet(queryContext);
/* 318 */       if (result != null) return result;
/*     */ 
/*     */     }
/*     */ 
/* 322 */     int maxFetchSize = queryContext.getMaxBufSize() > 0 ? queryContext.getMaxBufSize() : 0;
/* 323 */     if (relatedCount > 0) {
/* 324 */       maxFetchSize = (maxFetchSize > 0) && (relatedCount < maxFetchSize) ? relatedCount : maxFetchSize;
/*     */     }
/*     */ 
/* 327 */     result = DrmEntityFactory.getInstance().createResultSet();
/*     */     List parentCuids;
/* 328 */     if ((shortPath.size() > 2) && (relatedNode.getSearchType() == RelatedClassTreeNode.SearchType.CLASS)) {
/* 329 */       parentCuids = new ArrayList();
/* 330 */       parentCuids.add(deleteObj.getCuid());
/* 331 */       shortPath.remove(0);
/* 332 */       for (RelatedClassTreeNode node : shortPath) {
/* 333 */         if (parentCuids.size() == 0) {
/*     */           break;
/*     */         }
/* 336 */         parentCuids = getNodeCuidList(actionContext, node, parentCuids, deleteObj);
/* 337 */         for (String parentCuid : parentCuids)
/*     */           try {
/* 339 */             queryContext.setFetchSize(0);
/* 340 */             IDrmQueryResultSet rs = getRelatedObjects(actionContext, queryContext, relatedNode, parentCuid, query);
/* 341 */             for (IDrmQueryRow row : rs.getResultSet())
/* 342 */               if (maxFetchSize == 0)
/* 343 */                 result.getResultSet().add(row);
/* 344 */               else if ((maxFetchSize > 0) && (result.getResultSet().size() < maxFetchSize))
/* 345 */                 result.getResultSet().add(row);
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 349 */             LogHome.getLog().error("", ex);
/*     */           }
/*     */       }
/*     */     }
/* 353 */     else if (relatedNode.getSearchType() == RelatedClassTreeNode.SearchType.JAVA) {
/* 354 */       IRelatedObjectHandler handler = (IRelatedObjectHandler)this.relatedObjectHandlers.get(relatedNode.getBmClassId());
/* 355 */       if (handler != null) {
/* 356 */         BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), relatedBmClassId);
/* 357 */         result = handler.getRelatedDeleteObjects(relatedBmClassId, relatedClassMeta, deleteObj);
/*     */       }
/*     */     }
/*     */ 
/* 361 */     result.setCountValue(relatedCount);
/* 362 */     this.memoryQueryService.setResultSet(queryContext, result);
/* 363 */     result = this.memoryQueryService.getResultSet(queryContext);
/* 364 */     return result;
/*     */   }
/*     */ 
/*     */   private IDrmQueryResultSet getRelatedObjects(IServiceActionContext actionContext, IDrmQueryContext queryContext, RelatedClassTreeNode relatedNode, String parentCuid, DrmSingleClassQuery query)
/*     */   {
/* 369 */     IDrmQueryResultSet rs = null;
/*     */     try {
/* 371 */       String relatedBmClassId = relatedNode.getBmClassId();
/* 372 */       BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), relatedBmClassId);
/* 373 */       query.setClassMeta(relatedClassMeta);
/* 374 */       String sqlCond = "";
/* 375 */       List parentAttrIds = relatedNode.getParentAttrIds();
/* 376 */       for (int i = 0; i < parentAttrIds.size(); i++) {
/* 377 */         if (i > 0) {
/* 378 */           sqlCond = sqlCond + " or ";
/*     */         }
/* 380 */         String parentAttrId = (String)parentAttrIds.get(i);
/* 381 */         sqlCond = sqlCond + parentAttrId + "='" + parentCuid + "'";
/*     */       }
/* 383 */       query.setSqlCond(sqlCond);
/* 384 */       rs = getDynRMService().getDynObjBySql(actionContext, queryContext, query);
/*     */     } catch (Exception ex) {
/* 386 */       LogHome.getLog().error("", ex);
/*     */     }
/* 388 */     return rs;
/*     */   }
/*     */ 
/*     */   public List<IDrmDataObject> getCascadeDeleteObjects(IServiceActionContext actionContext, IDrmDataObject deleteObj) {
/* 392 */     BMClassMeta deleteClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), deleteObj.getBmClassId());
/* 393 */     RelatedClassTreeNode rootNode = deleteClassMeta.getRelatedClassNode();
/* 394 */     List cacadeObjects = new ArrayList();
/* 395 */     if (rootNode != null) {
/* 396 */       List cascadeNodes = rootNode.getFirstCascadeNodes();
/* 397 */       for (RelatedClassTreeNode cascadeNode : cascadeNodes) {
/* 398 */         BMClassMeta cascadeClassMeta = cascadeNode.getClassMeta();
/* 399 */         if (cascadeNode.getSearchType() == RelatedClassTreeNode.SearchType.JAVA) {
/* 400 */           IRelatedObjectHandler handler = (IRelatedObjectHandler)this.relatedObjectHandlers.get(cascadeNode.getBmClassId());
/* 401 */           String relatedBmClassId = cascadeNode.getBmClassId();
/* 402 */           if (handler != null) {
/* 403 */             BMClassMeta relatedClassMeta = getBMModelService().getClassMeta(ServiceHelper.createSvActCxt(), relatedBmClassId);
/* 404 */             IDrmQueryResultSet rs = handler.getRelatedDeleteObjects(relatedBmClassId, relatedClassMeta, deleteObj);
/* 405 */             if ((rs != null) && (rs.getResultSet() != null))
/* 406 */               for (int i = 0; i < rs.getResultSet().size(); i++) {
/* 407 */                 IDrmDataObject dbo = ((IDrmQueryRow)rs.getResultSet().get(i)).getResultDbo(cascadeClassMeta.getDbClassId());
/* 408 */                 cacadeObjects.add(dbo);
/*     */               }
/*     */           }
/*     */         }
/*     */         else {
/* 413 */           DrmSingleClassQuery query = new DrmSingleClassQuery();
/* 414 */           query.setBmClassId(cascadeNode.getBmClassId());
/* 415 */           query.addQueryAttrId(cascadeNode.getClassMeta().getCuidAttrId());
/* 416 */           query.addQueryAttrId(cascadeNode.getClassMeta().getPkAttrId());
/* 417 */           IDrmQueryResultSet rs = getRelatedObjects(actionContext, DrmEntityFactory.getInstance().createQueryContext(), cascadeNode, deleteObj.getCuid(), query);
/* 418 */           if ((rs != null) && (rs.getResultSet() != null)) {
/* 419 */             for (int i = 0; i < rs.getResultSet().size(); i++) {
/* 420 */               IDrmDataObject dbo = ((IDrmQueryRow)rs.getResultSet().get(i)).getResultDbo(cascadeClassMeta.getDbClassId());
/* 421 */               cacadeObjects.add(dbo);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 427 */     return cacadeObjects;
/*     */   }
/*     */ 
/*     */   public MemoryQueryService getMemoryQueryService() {
/* 431 */     return this.memoryQueryService;
/*     */   }
/*     */ 
/*     */   public void setMemoryQueryService(MemoryQueryService memoryQueryService) {
/* 435 */     this.memoryQueryService = memoryQueryService;
/*     */   }
/*     */ 
/*     */   public void setServiceDAO(IDynResManageServiceDAO serviceDAO) {
/* 439 */     this.serviceDAO = serviceDAO;
/*     */   }
/*     */ 
/*     */   private IDynResManageService getDynRMService() {
/* 443 */     return (IDynResManageService)ServiceHomeFactory.getInstance().getService("DynResManageService");
/*     */   }
/*     */ 
/*     */   public static IBMModelService getBMModelService() {
/* 447 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.impl.RelatedObjectService
 * JD-Core Version:    0.6.0
 */