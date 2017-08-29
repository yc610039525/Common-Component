package com.boco.raptor.drm.core.service.security;

import com.boco.raptor.common.service.IService;
import java.util.List;
import java.util.Map;

public abstract interface ISecurityService extends IService
{
  public static final String SERVICE_ID = "SecurityService";

  public abstract void addUser(IUserDetails paramIUserDetails)
    throws Exception;

  public abstract IUserDetails getUser(String paramString)
    throws Exception;

  public abstract void deleteUser(String paramString)
    throws Exception;

  public abstract void deleteAllUser()
    throws Exception;

  public abstract void modifyUser(IUserDetails paramIUserDetails)
    throws Exception;

  public abstract List<IUserDetails> getAllUser()
    throws Exception;

  public abstract List<IRole> getRoles()
    throws Exception;

  public abstract List<IFunctionNode> getFunctionNodes()
    throws Exception;

  public abstract void addFunctionNode(IFunctionNode paramIFunctionNode)
    throws Exception;

  public abstract void addFunctionNodes(List<IFunctionNode> paramList)
    throws Exception;

  public abstract void modifyFunctionNode(IFunctionNode paramIFunctionNode)
    throws Exception;

  public abstract IFunctionNode getFunctionNode(String paramString)
    throws Exception;

  public abstract List<IFunctionNode> getAllFunctionNode()
    throws Exception;

  public abstract void deleteFunctionNode(String paramString)
    throws Exception;

  public abstract void deleteAllFunctionNode()
    throws Exception;

  public abstract void addRole(IRole paramIRole)
    throws Exception;

  public abstract void addRoles(List<IRole> paramList)
    throws Exception;

  public abstract void modifyRole(IRole paramIRole)
    throws Exception;

  public abstract IRole getRole(String paramString)
    throws Exception;

  public abstract List<IRole> getAllRoles()
    throws Exception;

  public abstract void deleteRole(String paramString)
    throws Exception;

  public abstract void deleteAllRole()
    throws Exception;

  public abstract void addRolesToUserDimension(String paramString1, String paramString2, String paramString3, List<String> paramList)
    throws Exception;

  public abstract void addRoleToUserDimension(String paramString1, String paramString2, String paramString3, String paramString4)
    throws Exception;

  public abstract void deleteRoleFromUserDimension(String paramString1, String paramString2, String paramString3, String paramString4)
    throws Exception;

  public abstract void deleteRolesFromUserDimension(String paramString1, String paramString2, String paramString3, List<String> paramList)
    throws Exception;

  public abstract void addObjectsToUserDimension(String paramString1, String paramString2, List<String> paramList)
    throws Exception;

  public abstract void addObjectToUserDimension(String paramString1, String paramString2, String paramString3)
    throws Exception;

  public abstract void deleteObjectFromUserdimension(String paramString1, String paramString2, String paramString3)
    throws Exception;

  public abstract void deleteObjectsFromUserdimension(String paramString1, String paramString2, List<String> paramList)
    throws Exception;

  public abstract Map<String, List<String>> getDimensionActionNameMap(String paramString1, String paramString2)
    throws Exception;

  public abstract Map<String, List<String>> getDimensionObjectMap(String paramString1, String paramString2)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.ISecurityService
 * JD-Core Version:    0.6.0
 */