package com.boco.raptor.drm.core.service.security;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IAuthentication extends Serializable
{
  public abstract IUserDetails getUserDetails();

  public abstract void setUserDetails(IUserDetails paramIUserDetails);

  public abstract List getBmClassIds();

  public abstract void setBmClassIds(List paramList);

  public abstract List getActionNames();

  public abstract void setActionNames(List paramList);

  public abstract Map getPositiveObjects();

  public abstract void setPositiveObjects(Map paramMap);

  public abstract Map getReverseObjects();

  public abstract void setReverseObjects(Map paramMap);

  public abstract void setDimensionObject(Map<String, List> paramMap);

  public abstract Map<String, List> getDimensionObject();

  public abstract void setDimensionActionNames(Map<String, Map<String, List>> paramMap);

  public abstract Map<String, Map<String, List>> getDimensionActionNames();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IAuthentication
 * JD-Core Version:    0.6.0
 */