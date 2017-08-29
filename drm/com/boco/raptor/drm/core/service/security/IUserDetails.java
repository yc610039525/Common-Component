package com.boco.raptor.drm.core.service.security;

import java.io.Serializable;

public abstract interface IUserDetails extends Serializable
{
  public abstract String getPassword();

  public abstract String getUserId();

  public abstract String getTruename();

  public abstract String getClientIp();

  public abstract String getClientHostName();

  public abstract boolean getIsAdmin();

  public abstract String getLoginName();

  public abstract String getRelatedDistrict();

  public abstract void setClientHostName(String paramString);

  public abstract void setClientIp(String paramString);

  public abstract void setPassword(String paramString);

  public abstract void setTruename(String paramString);

  public abstract void setUserId(String paramString);

  public abstract void setIsAdmin(boolean paramBoolean);

  public abstract void setLoginName(String paramString);

  public abstract void setRelatedDistrict(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IUserDetails
 * JD-Core Version:    0.6.0
 */