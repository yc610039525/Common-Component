package com.boco.raptor.common.service;

import java.io.Serializable;

public abstract interface IServiceActionContext extends Serializable
{
  public abstract void addExtCxtAttr(String paramString, Serializable paramSerializable);

  public abstract <T> T getExtCxtAttr(String paramString);

  public abstract void removeExtCxtAttr(String paramString);

  public abstract String getDataSourceName();

  public abstract String getActionId();

  public abstract String getHostIP();

  public abstract String getServiceId();

  public abstract String getUserId();

  public abstract String getUserName();

  public abstract String getRequestId();

  public abstract String getProxyName();

  public abstract String getBmClassId();

  public abstract boolean getIsLog();

  public abstract void setDataSourceName(String paramString);

  public abstract void setActionId(String paramString);

  public abstract void setHostIP(String paramString);

  public abstract void setServiceId(String paramString);

  public abstract void setUserId(String paramString);

  public abstract void setUserName(String paramString);

  public abstract void setRequestId(String paramString);

  public abstract void setProxyName(String paramString);

  public abstract void setBmClassId(String paramString);

  public abstract void setIsLog(boolean paramBoolean);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.IServiceActionContext
 * JD-Core Version:    0.6.0
 */