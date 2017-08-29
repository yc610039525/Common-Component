/*     */ package com.boco.raptor.drm.core.meta;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ClassStatTreeNode
/*     */   implements Serializable
/*     */ {
/*     */   private String bmClassId;
/*     */   private String attrId;
/*     */   private Object attrValue;
/*     */   private String attrName;
/*  27 */   private int colspan = 1;
/*     */ 
/*  29 */   private ClassStatTreeNode parentNode = null;
/*     */   private List<ClassStatTreeNode> childNodes;
/*     */   private boolean isLeafNode;
/*  35 */   private boolean notFirstChildNode = false;
/*     */ 
/*  37 */   private boolean isRootNode = false;
/*     */ 
/*     */   public String getBmClassId()
/*     */   {
/*  43 */     return this.bmClassId;
/*     */   }
/*     */ 
/*     */   public String getAttrId() {
/*  47 */     return this.attrId;
/*     */   }
/*     */ 
/*     */   public Object getAttrValue() {
/*  51 */     return this.attrValue;
/*     */   }
/*     */ 
/*     */   public String getAttrName() {
/*  55 */     return this.attrName;
/*     */   }
/*     */ 
/*     */   public int getColspan() {
/*  59 */     return this.colspan;
/*     */   }
/*     */ 
/*     */   public ClassStatTreeNode getParentNode() {
/*  63 */     return this.parentNode;
/*     */   }
/*     */ 
/*     */   public List<ClassStatTreeNode> getChildNodes() {
/*  67 */     return this.childNodes;
/*     */   }
/*     */ 
/*     */   public void setBmClassId(String bmClassId) {
/*  71 */     this.bmClassId = bmClassId;
/*     */   }
/*     */ 
/*     */   public void setAttrId(String attrId) {
/*  75 */     this.attrId = attrId;
/*     */   }
/*     */ 
/*     */   public void setAttrValue(Object attrValue) {
/*  79 */     this.attrValue = attrValue;
/*     */   }
/*     */ 
/*     */   public void setAttrName(String attrName) {
/*  83 */     this.attrName = attrName;
/*     */   }
/*     */ 
/*     */   public void setColspan(int colspan) {
/*  87 */     this.colspan = colspan;
/*     */   }
/*     */ 
/*     */   public void setParentNode(ClassStatTreeNode parentNode) {
/*  91 */     this.parentNode = parentNode;
/*     */   }
/*     */ 
/*     */   public void setChildNodes(List<ClassStatTreeNode> childNodes) {
/*  95 */     this.childNodes = childNodes;
/*     */   }
/*     */ 
/*     */   public void setIsRootNode(boolean isRootNode) {
/*  99 */     this.isRootNode = isRootNode;
/*     */   }
/*     */ 
/*     */   public boolean getIsLeafNode() {
/* 103 */     return this.isLeafNode;
/*     */   }
/*     */ 
/*     */   public boolean getIsRootNode() {
/* 107 */     return this.isRootNode;
/*     */   }
/*     */ 
/*     */   public void addChildNode(ClassStatTreeNode childNode) {
/* 111 */     this.childNodes = (this.childNodes == null ? new ArrayList() : this.childNodes);
/* 112 */     this.childNodes.add(childNode);
/* 113 */     childNode.setParentNode(this);
/*     */   }
/*     */ 
/*     */   public void setIsLeafNode(boolean isLeafNode) {
/* 117 */     this.isLeafNode = isLeafNode;
/*     */   }
/*     */ 
/*     */   public void setNotFirstChildNode(boolean notFirstChildNode) {
/* 121 */     this.notFirstChildNode = notFirstChildNode;
/*     */   }
/*     */ 
/*     */   public boolean getNotFirstChildNode() {
/* 125 */     return this.notFirstChildNode;
/*     */   }
/*     */ 
/*     */   public ClassStatTreeNode getLastChildNode() {
/* 129 */     ClassStatTreeNode lastChildNode = this.childNodes == null ? null : (ClassStatTreeNode)this.childNodes.get(this.childNodes.size() - 1);
/* 130 */     return lastChildNode;
/*     */   }
/*     */ 
/*     */   public void addColspan() {
/* 134 */     this.colspan += 1;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ClassStatTreeNode
 * JD-Core Version:    0.6.0
 */