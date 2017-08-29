/*    */ package com.boco.raptor.drm.core.service.security.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.service.security.IFunctionNode;
/*    */ 
/*    */ public class FunctionNode
/*    */   implements IFunctionNode
/*    */ {
/*    */   private String nodeName;
/*    */   private String nodeActionNames;
/*    */   private String nodeCode;
/*    */   private String parendNode;
/*    */   private String nodeId;
/*    */ 
/*    */   public FunctionNode(String _nodeId, String _nodeName, String _nodeActionNames, String _parentNode, String _nodeCode)
/*    */   {
/* 24 */     this.nodeId = _nodeId;
/* 25 */     this.nodeName = _nodeName;
/* 26 */     this.nodeActionNames = _nodeActionNames;
/* 27 */     this.parendNode = _parentNode;
/* 28 */     this.nodeCode = _nodeCode;
/*    */   }
/*    */ 
/*    */   public FunctionNode(String nodeId) {
/* 32 */     this.nodeId = nodeId;
/*    */   }
/*    */ 
/*    */   public String getNodeActionNames() {
/* 36 */     return this.nodeActionNames;
/*    */   }
/*    */ 
/*    */   public String getNodeCode() {
/* 40 */     return this.nodeCode;
/*    */   }
/*    */ 
/*    */   public String getNodeId() {
/* 44 */     return this.nodeId;
/*    */   }
/*    */ 
/*    */   public String getNodeName() {
/* 48 */     return this.nodeName;
/*    */   }
/*    */ 
/*    */   public String getParendNode() {
/* 52 */     return this.parendNode;
/*    */   }
/*    */ 
/*    */   public void setParendNode(String ParendNode) {
/* 56 */     this.parendNode = ParendNode;
/*    */   }
/*    */ 
/*    */   public void setNodeName(String nodeName) {
/* 60 */     this.nodeName = nodeName;
/*    */   }
/*    */ 
/*    */   public void setNodeId(String nodeId) {
/* 64 */     this.nodeId = nodeId;
/*    */   }
/*    */ 
/*    */   public void setNodeCode(String nodeCode) {
/* 68 */     this.nodeCode = nodeCode;
/*    */   }
/*    */ 
/*    */   public void setNodeActionNames(String nodeActionNames) {
/* 72 */     this.nodeActionNames = nodeActionNames;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.impl.FunctionNode
 * JD-Core Version:    0.6.0
 */