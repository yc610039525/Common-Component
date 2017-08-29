/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.io.BufInputStream;
/*     */ import com.boco.common.util.io.BufOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class RelatedClassTreeNode
/*     */   implements Serializable
/*     */ {
/*     */   private String nodeId;
/*     */   private String bmClassId;
/*     */   private String bmClassLabelCn;
/*     */   private List<String> parentAttrIds;
/*  56 */   private Boolean isSlave = Boolean.valueOf(false);
/*     */ 
/*  63 */   private int relatedType = 0;
/*     */ 
/*  75 */   private int searchType = 0;
/*     */   private List<RelatedClassTreeNode> childNodes;
/*  90 */   private RelatedClassTreeNode parentNode = null;
/*     */   private List<String> rootAttrIds;
/*     */   private String selfRelAttrId;
/* 109 */   private int selfRelType = 0;
/*     */   private BMClassMeta classMeta;
/*     */ 
/*     */   public void initRootNode()
/*     */   {
/* 122 */     setNodeId(getBmClassId() + "-" + 0);
/* 123 */     if (isHaveSelfChildNode()) {
/* 124 */       createSelfChildNode();
/*     */     }
/*     */ 
/* 127 */     createAllChildSelfChild();
/* 128 */     setAllChildNodeId();
/* 129 */     setAllChildRoot();
/*     */   }
/*     */ 
/*     */   private void setAllChildRoot() {
/* 133 */     List childNodes = getAllChildNodes();
/* 134 */     for (RelatedClassTreeNode childNode : childNodes) {
/* 135 */       if (childNode.getClassMeta() == null)
/*     */         continue;
/* 137 */       List attrMetas = childNode.getClassMeta().getRelatedClassAttrMeta(getBmClassId());
/* 138 */       if ((attrMetas != null) && (attrMetas.size() > 0)) {
/* 139 */         List rootAttrIds = new ArrayList();
/* 140 */         for (BMAttrMeta attrMeta : attrMetas) {
/* 141 */           rootAttrIds.add(attrMeta.getAttrId());
/*     */         }
/* 143 */         childNode.setRootAttrIds(rootAttrIds);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createAllChildSelfChild() {
/* 149 */     List childNodes = getAllChildNodes();
/* 150 */     for (RelatedClassTreeNode childNode : childNodes)
/* 151 */       if (childNode.isHaveSelfChildNode())
/* 152 */         childNode.createSelfChildNode();
/*     */   }
/*     */ 
/*     */   private void setAllChildNodeId()
/*     */   {
/* 158 */     List _childNodes = getAllChildNodes();
/*     */ 
/* 160 */     for (int i = 0; i < _childNodes.size(); i++) {
/* 161 */       RelatedClassTreeNode childNode = (RelatedClassTreeNode)_childNodes.get(i);
/* 162 */       childNode.setNodeId(childNode.getBmClassId() + "-" + (i + 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<String> getParentAttrIds() {
/* 167 */     return this.parentAttrIds;
/*     */   }
/*     */ 
/*     */   public String getBmClassId() {
/* 171 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public String getBmClassLabelCn() {
/* 175 */     return this.bmClassLabelCn;
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> getChildNodes() {
/* 179 */     return this.childNodes;
/*     */   }
/*     */ 
/*     */   public int getRelatedType() {
/* 183 */     return this.relatedType;
/*     */   }
/*     */ 
/*     */   public Boolean getIsSlave() {
/* 187 */     return this.isSlave;
/*     */   }
/*     */ 
/*     */   public int getSearchType() {
/* 191 */     return this.searchType;
/*     */   }
/*     */ 
/*     */   public RelatedClassTreeNode getParentNode() {
/* 195 */     return this.parentNode;
/*     */   }
/*     */ 
/*     */   public List<String> getRootAttrIds()
/*     */   {
/* 200 */     return this.rootAttrIds;
/*     */   }
/*     */ 
/*     */   public String getSelfRelAttrId() {
/* 204 */     return this.selfRelAttrId;
/*     */   }
/*     */ 
/*     */   public int getSelfRelType() {
/* 208 */     return this.selfRelType;
/*     */   }
/*     */ 
/*     */   public String getNodeId() {
/* 212 */     return this.nodeId;
/*     */   }
/*     */ 
/*     */   public BMClassMeta getClassMeta() {
/* 216 */     return this.classMeta;
/*     */   }
/*     */ 
/*     */   public void setParentAttrIds(List<String> parentAttrIds) {
/* 220 */     this.parentAttrIds = parentAttrIds;
/*     */   }
/*     */ 
/*     */   public void setParentAttrIds(String[] parentAttrIds) {
/* 224 */     if (this.parentAttrIds == null) {
/* 225 */       this.parentAttrIds = new ArrayList();
/*     */     }
/* 227 */     for (String parentAttrId : parentAttrIds)
/* 228 */       this.parentAttrIds.add(parentAttrId);
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId)
/*     */   {
/* 233 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setBmClassLabelCn(String bmClassLabelCn) {
/* 237 */     this.bmClassLabelCn = bmClassLabelCn;
/*     */   }
/*     */ 
/*     */   public void setIsSlave(Boolean isSlave) {
/* 241 */     this.isSlave = isSlave;
/*     */   }
/*     */ 
/*     */   public void setChildNodes(List<RelatedClassTreeNode> childNodes) {
/* 245 */     this.childNodes = childNodes;
/* 246 */     for (RelatedClassTreeNode childNode : childNodes)
/* 247 */       childNode.setParentNode(this);
/*     */   }
/*     */ 
/*     */   public void setRelatedType(int relatedType)
/*     */   {
/* 252 */     this.relatedType = relatedType;
/*     */   }
/*     */ 
/*     */   public void setSearchType(int searchType) {
/* 256 */     this.searchType = searchType;
/*     */   }
/*     */ 
/*     */   public void setParentNode(RelatedClassTreeNode parentNode) {
/* 260 */     this.parentNode = parentNode;
/*     */   }
/*     */ 
/*     */   public void setRootAttrIds(List<String> rootAttrIds) {
/* 264 */     this.rootAttrIds = rootAttrIds;
/*     */   }
/*     */ 
/*     */   public void setSelfRelAttrId(String selfRelAttrId) {
/* 268 */     this.selfRelAttrId = selfRelAttrId;
/*     */   }
/*     */ 
/*     */   public void setSelfRelType(int selfRelType) {
/* 272 */     this.selfRelType = selfRelType;
/*     */   }
/*     */ 
/*     */   public void setNodeId(String nodeId) {
/* 276 */     this.nodeId = nodeId;
/*     */   }
/*     */ 
/*     */   public void setClassMeta(BMClassMeta classMeta) {
/* 280 */     this.classMeta = classMeta;
/* 281 */     if ((classMeta == null) || (!classMeta.isDynClass()))
/* 282 */       setSearchType(SearchType.JAVA);
/*     */   }
/*     */ 
/*     */   public boolean isHaveSelfChildNode()
/*     */   {
/* 287 */     return this.selfRelAttrId != null;
/*     */   }
/*     */ 
/*     */   public boolean isSelfChildNode() {
/* 291 */     boolean isSelfChildNode = false;
/* 292 */     if ((this.parentNode != null) && (getBmClassId().equals(this.parentNode.getBmClassId()))) {
/* 293 */       isSelfChildNode = true;
/*     */     }
/* 295 */     return isSelfChildNode;
/*     */   }
/*     */ 
/*     */   public boolean isRootDirectNode() {
/* 299 */     return ((this.rootAttrIds != null) && (this.rootAttrIds.size() > 0)) || (getSearchType() == SearchType.JAVA);
/*     */   }
/*     */ 
/*     */   public void addChildNode(RelatedClassTreeNode childNode)
/*     */   {
/* 305 */     this.childNodes = (this.childNodes == null ? new ArrayList() : this.childNodes);
/* 306 */     this.childNodes.add(childNode);
/* 307 */     childNode.setParentNode(this);
/*     */   }
/*     */ 
/*     */   public void addChildNode(int index, RelatedClassTreeNode childNode) {
/* 311 */     this.childNodes = (this.childNodes == null ? new ArrayList() : this.childNodes);
/* 312 */     this.childNodes.add(index, childNode);
/* 313 */     childNode.setParentNode(this);
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> searchChildNodes(String searchBmClassId, boolean isSelfChildNode) {
/* 317 */     List result = new ArrayList();
/* 318 */     for (RelatedClassTreeNode child : getAllChildNodes()) {
/* 319 */       if ((searchBmClassId.equals(child.getBmClassId())) && (child.isSelfChildNode() == isSelfChildNode)) {
/* 320 */         result.add(child);
/*     */       }
/*     */     }
/* 323 */     return result;
/*     */   }
/*     */ 
/*     */   public RelatedClassTreeNode searchChildNode(String nodeId) {
/* 327 */     RelatedClassTreeNode result = null;
/* 328 */     for (RelatedClassTreeNode child : getAllChildNodes()) {
/* 329 */       if (nodeId.equals(child.getNodeId())) {
/* 330 */         result = child;
/* 331 */         break;
/*     */       }
/*     */     }
/* 334 */     return result;
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> getParentTreePath(RelatedClassTreeNode node) {
/* 338 */     List treePath = new ArrayList();
/* 339 */     getParentTreePath(node, treePath);
/* 340 */     return treePath;
/*     */   }
/*     */ 
/*     */   private void getParentTreePath(RelatedClassTreeNode node, List<RelatedClassTreeNode> parentNodes) {
/* 344 */     if (node.getParentNode() != null) {
/* 345 */       parentNodes.add(0, node.getParentNode());
/* 346 */       getParentTreePath(node.getParentNode(), parentNodes);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> getAllChildNodes() {
/* 351 */     List childNodes = new ArrayList();
/* 352 */     getAllChildNodes(this, childNodes);
/* 353 */     return childNodes;
/*     */   }
/*     */ 
/*     */   private void getAllChildNodes(RelatedClassTreeNode startNode, List<RelatedClassTreeNode> childNodes) {
/* 357 */     if (startNode.childNodes != null)
/* 358 */       for (RelatedClassTreeNode childNode : startNode.childNodes) {
/* 359 */         childNodes.add(childNode);
/* 360 */         getAllChildNodes(childNode, childNodes);
/*     */       }
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> getFirstCascadeNodes()
/*     */   {
/* 366 */     List cascadeNodes = new ArrayList();
/* 367 */     List childNodes = getAllChildNodes();
/* 368 */     for (RelatedClassTreeNode childNode : childNodes) {
/* 369 */       if (childNode.getRelatedType() == RelatedType.CASCADE) {
/* 370 */         cascadeNodes.add(childNode);
/*     */       }
/*     */     }
/* 373 */     return childNodes;
/*     */   }
/*     */ 
/*     */   public List<RelatedClassTreeNode> getFirstConstr2ManualNodes() {
/* 377 */     List childNodes = new ArrayList();
/* 378 */     getFirstConstr2ManualNodes(this, childNodes);
/* 379 */     return childNodes;
/*     */   }
/*     */ 
/*     */   private void getFirstConstr2ManualNodes(RelatedClassTreeNode startNode, List<RelatedClassTreeNode> childNodes)
/*     */   {
/* 384 */     if (startNode.childNodes != null)
/* 385 */       for (RelatedClassTreeNode childNode : startNode.childNodes)
/* 386 */         if ((childNode.getRelatedType() == RelatedType.CONSTRAINT) || (childNode.getRelatedType() == RelatedType.MANUAL))
/*     */         {
/* 388 */           if (!childNodes.contains(childNode))
/* 389 */             childNodes.add(childNode);
/*     */         }
/*     */         else
/* 392 */           getFirstConstr2ManualNodes(childNode, childNodes);
/*     */   }
/*     */ 
/*     */   public RelatedClassTreeNode deepClone()
/*     */   {
/* 399 */     RelatedClassTreeNode cloned = null;
/*     */     try {
/* 401 */       BufOutputStream bufOut = new BufOutputStream();
/* 402 */       ObjectOutputStream out = new ObjectOutputStream(bufOut);
/* 403 */       out.writeObject(this);
/* 404 */       ObjectInputStream in = new ObjectInputStream(new BufInputStream(bufOut.getBuf()));
/* 405 */       cloned = (RelatedClassTreeNode)in.readObject();
/* 406 */       in.close();
/* 407 */       out.close();
/*     */     } catch (Exception ex) {
/* 409 */       LogHome.getLog().error("", ex);
/*     */     }
/* 411 */     return cloned;
/*     */   }
/*     */ 
/*     */   public String getBmClassKey() {
/* 415 */     String bmClassKey = this.bmClassId;
/* 416 */     if (isSelfChildNode()) {
/* 417 */       bmClassKey = bmClassKey + "-CHILD";
/*     */     }
/* 419 */     return bmClassKey;
/*     */   }
/*     */ 
/*     */   public static String getBmClassId(String bmClassKey) {
/* 423 */     String bmClassId = bmClassKey;
/* 424 */     int index = bmClassKey.indexOf("-CHILD");
/* 425 */     if (index > 0) {
/* 426 */       bmClassId = bmClassKey.substring(0, index);
/*     */     }
/* 428 */     return bmClassId;
/*     */   }
/*     */ 
/*     */   private void createSelfChildNode() {
/* 432 */     if (isHaveSelfChildNode()) {
/* 433 */       RelatedClassTreeNode selfChildNode = new RelatedClassTreeNode();
/* 434 */       selfChildNode.setBmClassId(getBmClassId());
/* 435 */       if (getClassMeta() != null) {
/* 436 */         selfChildNode.setClassMeta(getClassMeta());
/* 437 */         selfChildNode.setBmClassLabelCn(getClassMeta().getLabelCn());
/*     */       }
/* 439 */       selfChildNode.setRelatedType(this.selfRelType);
/* 440 */       selfChildNode.setSearchType(SearchType.CLASS);
/* 441 */       selfChildNode.setParentAttrIds(new String[] { this.selfRelAttrId });
/* 442 */       addChildNode(0, selfChildNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 448 */     return "TreeNode[bmClassId=" + this.bmClassId + ", labelCn=" + this.bmClassLabelCn + "]";
/*     */   }
/*     */ 
/*     */   public static class SearchType
/*     */   {
/*  77 */     public static int CLASS = 0;
/*  78 */     public static int JAVA = 1;
/*     */   }
/*     */ 
/*     */   public static class RelatedType
/*     */   {
/*  65 */     public static int CASCADE = 0;
/*  66 */     public static int MANUAL = 1;
/*  67 */     public static int CONSTRAINT = 2;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.RelatedClassTreeNode
 * JD-Core Version:    0.6.0
 */