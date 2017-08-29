package com.boco.raptor.drm.core.service.security;

import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.IDrmDataObject;

public abstract interface IObjectValidService
{
  public abstract void isObjectValid(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IObjectValidService
 * JD-Core Version:    0.6.0
 */