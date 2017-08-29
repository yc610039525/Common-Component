/*    */ package com.boco.raptor.drm.core.service.security.impl;
/*    */ 
/*    */ import com.boco.raptor.drm.core.service.security.IFunctionNode;
/*    */ import com.boco.raptor.drm.core.service.security.IRole;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Role
/*    */   implements IRole
/*    */ {
/*    */   private String roleId;
/*    */   private String roleName;
/*    */   private List<IFunctionNode> functionNodes;
/*    */ 
/*    */   public Role(String roleId, String roleName, List<IFunctionNode> functionNodes)
/*    */   {
/* 25 */     this.roleId = roleId;
/* 26 */     this.roleName = roleName;
/* 27 */     this.functionNodes = functionNodes;
/*    */   }
/*    */ 
/*    */   public List<IFunctionNode> getFunctionNodes() {
/* 31 */     return this.functionNodes;
/*    */   }
/*    */ 
/*    */   public String getRoleId() {
/* 35 */     return this.roleId;
/*    */   }
/*    */ 
/*    */   public String getRoleName() {
/* 39 */     return this.roleName;
/*    */   }
/*    */ 
/*    */   public void setRoleName(String roleName) {
/* 43 */     this.roleName = roleName;
/*    */   }
/*    */ 
/*    */   public void setRoleId(String roleId) {
/* 47 */     this.roleId = roleId;
/*    */   }
/*    */ 
/*    */   public void setFunctionNodes(List<IFunctionNode> functionNodes) {
/* 51 */     this.functionNodes = functionNodes;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.impl.Role
 * JD-Core Version:    0.6.0
 */