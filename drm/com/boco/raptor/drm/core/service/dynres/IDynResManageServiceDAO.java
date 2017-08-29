package com.boco.raptor.drm.core.service.dynres;

import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
import com.boco.raptor.drm.core.dto.IDrmDataObject;
import com.boco.raptor.drm.core.dto.IDrmQueryContext;
import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
import com.boco.raptor.drm.core.meta.BMClassMeta;
import com.boco.transnms.server.dao.base.IDataAccessObject;
import java.util.List;
import java.util.Map;

public abstract interface IDynResManageServiceDAO<T extends IDrmDataObject> extends IDataAccessObject
{
  public abstract IDrmDataObject addDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract void deleteDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract void deleteDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList)
    throws Exception;

  public abstract void modifyDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract void modifyDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList, Map paramMap)
    throws Exception;

  public abstract IDrmQueryResultSet getDynObjBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery)
    throws Exception;

  public abstract IDrmQueryResultSet getDynObjBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract IDrmDataObject getDynObject(IServiceActionContext paramIServiceActionContext, BMClassMeta paramBMClassMeta, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract int getDynObjCount(IServiceActionContext paramIServiceActionContext, String paramString)
    throws Exception;

  public abstract int getDynObjCount(IServiceActionContext paramIServiceActionContext, DrmSingleClassQuery paramDrmSingleClassQuery)
    throws Exception;

  public abstract Map<Integer, Integer> getEnumCount(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws Exception;

  public abstract List<Integer> getClassStatCount(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, String paramString1, List<String> paramList, String paramString2)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.IDynResManageServiceDAO
 * JD-Core Version:    0.6.0
 */