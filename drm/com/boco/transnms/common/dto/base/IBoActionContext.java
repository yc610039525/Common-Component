package com.boco.transnms.common.dto.base;

import java.io.Serializable;

public abstract interface IBoActionContext extends Serializable
{
  public abstract String getDsName();

  public abstract String getUserId();

  public abstract String getHostIP();

  public abstract String getHostName();

  public abstract String getActionName();

  public abstract String getUserName();

  public abstract String getClientId();

  public abstract boolean isActionChecked();

  public abstract void setDsName(String paramString);

  public abstract void setUserId(String paramString);

  public abstract void setHostIP(String paramString);

  public abstract void setHostName(String paramString);

  public abstract void setActionName(String paramString);

  public abstract void setUserName(String paramString);

  public abstract void setClientId(String paramString);

  public abstract void setActionChecked(boolean paramBoolean);

  public abstract boolean isCompressed();

  public abstract void setCompressed(boolean paramBoolean);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.IBoActionContext
 * JD-Core Version:    0.6.0
 */