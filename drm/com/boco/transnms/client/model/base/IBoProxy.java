package com.boco.transnms.client.model.base;

public abstract interface IBoProxy
{
  public abstract String getBoName();

  public abstract void setBoName(String paramString);

  public abstract Object exec(IBoCommand paramIBoCommand)
    throws Exception;

  public abstract BoProxyType getBoProxyType();

  public abstract String getIdentifier();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.IBoProxy
 * JD-Core Version:    0.6.0
 */