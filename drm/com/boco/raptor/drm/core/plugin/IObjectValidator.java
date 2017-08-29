package com.boco.raptor.drm.core.plugin;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.IDrmDataObject;
import com.boco.raptor.drm.core.meta.BMClassMeta;
import java.io.Serializable;

public abstract interface IObjectValidator extends Serializable
{
  public abstract void checkAddObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract void checkModifyObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract void checkDeleteObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, BMClassMeta paramBMClassMeta)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.plugin.IObjectValidator
 * JD-Core Version:    0.6.0
 */