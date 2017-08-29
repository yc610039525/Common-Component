package com.boco.transnms.client.model.base;

import com.boco.common.util.except.UserException;

public abstract interface ICmdInterceptor
{
  public abstract void doCommand(IBoCommand paramIBoCommand)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.ICmdInterceptor
 * JD-Core Version:    0.6.0
 */