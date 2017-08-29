package com.boco.transnms.server.bo.ibo;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.GenericDO;

public abstract interface IGenericExtBO extends IGenericBO
{
  public abstract GenericDO getExtObject(BoActionContext paramBoActionContext, long paramLong)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.ibo.IGenericExtBO
 * JD-Core Version:    0.6.0
 */