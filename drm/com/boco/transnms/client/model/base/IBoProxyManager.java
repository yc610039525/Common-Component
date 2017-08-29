package com.boco.transnms.client.model.base;

public abstract interface IBoProxyManager
{
  public abstract String[] getBoNames();

  public abstract IBoProxy getBoProxy(String paramString);

  public abstract IBoProxy getBoProxy(String paramString1, String paramString2);

  public abstract void prepareBoProxy();

  public abstract String getBoProxyName();

  public abstract boolean isLocalBoProxy();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.IBoProxyManager
 * JD-Core Version:    0.6.0
 */