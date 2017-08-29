package com.boco.transnms.server.bo.base;

public abstract interface IBusinessObject
{
  public abstract String getBoName();

  public abstract void initBO()
    throws Exception;

  public abstract boolean isInitByAllServer();

  public abstract boolean isInitByThisServer();

  public abstract boolean isCallByRemote();

  public abstract String getServerName();

  public abstract boolean isStateLess();

  public abstract void setInitByAllServer(boolean paramBoolean);

  public abstract void setServerName(String paramString);

  public abstract void setStateLess(boolean paramBoolean);

  public abstract boolean isRunOnLocalServer();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.IBusinessObject
 * JD-Core Version:    0.6.0
 */