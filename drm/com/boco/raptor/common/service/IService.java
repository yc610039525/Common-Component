package com.boco.raptor.common.service;

public abstract interface IService
{
  public abstract String getServiceId();

  public abstract void setServiceId(String paramString);

  public abstract void initService()
    throws Exception;

  public abstract boolean isEnable();

  public abstract void setIsEnable(boolean paramBoolean);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.IService
 * JD-Core Version:    0.6.0
 */