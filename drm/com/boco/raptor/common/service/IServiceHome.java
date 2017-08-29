package com.boco.raptor.common.service;

import com.boco.common.util.except.UserException;
import java.util.List;

public abstract interface IServiceHome
{
  public abstract String getServiceHomeId();

  public abstract void setServiceHomeId(String paramString);

  public abstract boolean isLocalServiceHome();

  public abstract void initServices()
    throws Exception;

  public abstract IService getService(String paramString)
    throws UserException;

  public abstract void addService(IService paramIService)
    throws Exception;

  public abstract void deleteService(String paramString);

  public abstract String[] getServiceIds();

  public abstract void setServiceInterceptor(IServiceInterceptor paramIServiceInterceptor)
    throws UserException;

  public abstract void setServiceInterceptors(List<IServiceInterceptor> paramList)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.IServiceHome
 * JD-Core Version:    0.6.0
 */