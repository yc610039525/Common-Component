package com.boco.raptor.common.service;

import java.lang.reflect.InvocationHandler;
import java.util.List;

public abstract interface IServiceInterceptor extends InvocationHandler
{
  public abstract List<String> getInterceptServiceIds();

  public abstract void setSerivce(IService paramIService);

  public abstract IServiceInterceptor clone();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.service.IServiceInterceptor
 * JD-Core Version:    0.6.0
 */