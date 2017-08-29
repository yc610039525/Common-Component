package com.boco.transnms.server.bo.base;

import com.boco.common.util.except.UserException;

public abstract interface IBoHome
{
  public abstract String getBoClassName(String paramString)
    throws UserException;

  public abstract IBusinessObject getBO(String paramString);

  public abstract String[] getBoNames();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.IBoHome
 * JD-Core Version:    0.6.0
 */