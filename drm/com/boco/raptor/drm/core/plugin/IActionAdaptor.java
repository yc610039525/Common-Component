package com.boco.raptor.drm.core.plugin;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.IDrmDataObject;
import com.boco.raptor.drm.core.meta.BMClassMeta;
import java.util.List;
import java.util.Map;

public abstract interface IActionAdaptor
{
  public abstract IDrmDataObject addDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract List<IDrmDataObject> addDynObjects(IServiceActionContext paramIServiceActionContext, List<IDrmDataObject> paramList, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract void deleteDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract Map<String, String> deleteDynObjects(IServiceActionContext paramIServiceActionContext, List<IDrmDataObject> paramList, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract void modifyDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;

  public abstract void modifyDynObjects(IServiceActionContext paramIServiceActionContext, List<IDrmDataObject> paramList, Map<String, Object> paramMap, boolean paramBoolean, BMClassMeta paramBMClassMeta)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.plugin.IActionAdaptor
 * JD-Core Version:    0.6.0
 */