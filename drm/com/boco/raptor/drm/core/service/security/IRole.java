package com.boco.raptor.drm.core.service.security;

import java.util.List;

public abstract interface IRole
{
  public abstract List<IFunctionNode> getFunctionNodes();

  public abstract String getRoleId();

  public abstract String getRoleName();

  public abstract void setRoleName(String paramString);

  public abstract void setRoleId(String paramString);

  public abstract void setFunctionNodes(List<IFunctionNode> paramList);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IRole
 * JD-Core Version:    0.6.0
 */