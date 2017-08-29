package com.boco.transnms.server.bo.ibo;

import com.boco.common.util.except.UserException;
import com.boco.transnms.server.bo.base.IBusinessObject;

public abstract interface IBasicSecurityBO extends IBusinessObject
{
  public abstract boolean isActionValid(String paramString1, String paramString2)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.ibo.IBasicSecurityBO
 * JD-Core Version:    0.6.0
 */