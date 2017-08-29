package com.boco.raptor.drm.core.service.dynres;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IService;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.DrmSingleClassQuery;
import com.boco.raptor.drm.core.dto.IDrmDataObject;
import com.boco.raptor.drm.core.dto.IDrmLabelValue;
import com.boco.raptor.drm.core.dto.IDrmMemQueryContext;
import com.boco.raptor.drm.core.dto.IDrmQueryContext;
import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
import com.boco.raptor.drm.core.dto.impl.check.DrmAttrCheckCount;
import com.boco.raptor.drm.core.dto.impl.check.DrmCheckAttrId;
import com.boco.transnms.common.dto.base.GenericDO;
import java.util.List;
import java.util.Map;

public abstract interface IDynResManageService<T extends IDrmDataObject> extends IService
{
  public static final String SERVICE_ID = "DynResManageService";

  public abstract void setServiceDAO(IDynResManageServiceDAO paramIDynResManageServiceDAO);

  public abstract boolean getIsLog(IServiceActionContext paramIServiceActionContext);

  public abstract IDrmDataObject addDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean)
    throws UserException;

  public abstract List<IDrmDataObject> addDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList, boolean paramBoolean)
    throws UserException;

  public abstract void deleteDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean)
    throws UserException;

  public abstract Map<String, String> deleteDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList, boolean paramBoolean)
    throws UserException;

  public abstract Map<String, String> deleteDynObjectsByCond(IServiceActionContext paramIServiceActionContext, String paramString, DrmSingleClassQuery paramDrmSingleClassQuery, boolean paramBoolean)
    throws UserException;

  public abstract void modifyDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean)
    throws UserException;

  public abstract void modifyDynObject(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, List<T> paramList, boolean paramBoolean)
    throws UserException;

  public abstract void modifyDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList, Map<String, Object> paramMap, boolean paramBoolean)
    throws UserException;

  public abstract void modifyDynObjects(IServiceActionContext paramIServiceActionContext, List<T> paramList1, Map<String, Object> paramMap, List<T> paramList2, boolean paramBoolean)
    throws UserException;

  public abstract IDrmQueryResultSet getObjectsBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, String paramString, GenericDO[] paramArrayOfGenericDO)
    throws UserException;

  public abstract IDrmQueryResultSet getDynObjBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery)
    throws UserException;

  public abstract IDrmQueryResultSet getDynObjBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery, IDrmDataObject paramIDrmDataObject)
    throws UserException;

  public abstract T getDynObject(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, T paramT)
    throws UserException;

  public abstract IDrmQueryResultSet getRelatedAttrValues(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery)
    throws UserException;

  public abstract IDrmQueryResultSet getLabelValuesBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, String paramString1, GenericDO[] paramArrayOfGenericDO, String paramString2, String paramString3, String paramString4)
    throws UserException;

  public abstract IDrmQueryResultSet getRelatedDeleteObjects(IServiceActionContext paramIServiceActionContext, IDrmMemQueryContext paramIDrmMemQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery, IDrmDataObject paramIDrmDataObject, int paramInt, boolean paramBoolean)
    throws UserException;

  public abstract Map<String, Integer> getRelatedDeleteObjectCount(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject, boolean paramBoolean)
    throws UserException;

  public abstract void clearQueryResult(IServiceActionContext paramIServiceActionContext, IDrmMemQueryContext paramIDrmMemQueryContext);

  public abstract int getDynObjCountBySql(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, DrmSingleClassQuery paramDrmSingleClassQuery)
    throws UserException;

  public abstract List<DrmAttrCheckCount> getCheckCounts(IServiceActionContext paramIServiceActionContext, IDrmMemQueryContext paramIDrmMemQueryContext, String paramString, List<DrmCheckAttrId> paramList)
    throws UserException;

  public abstract IDrmQueryResultSet getCheckResult(IServiceActionContext paramIServiceActionContext, IDrmMemQueryContext paramIDrmMemQueryContext, DrmAttrCheckCount paramDrmAttrCheckCount)
    throws UserException;

  public abstract Map<String, Map<Integer, Integer>> getEnumCount(IServiceActionContext paramIServiceActionContext, String paramString, List<String> paramList)
    throws UserException;

  public abstract IDrmQueryResultSet getClassStatResultSet(IServiceActionContext paramIServiceActionContext, IDrmQueryContext paramIDrmQueryContext, String paramString, List<String> paramList)
    throws UserException;

  public abstract IDrmLabelValue getRelatedIdLabelByAttrValue(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws UserException;

  public abstract IDrmDataObject getRelatedObjectByAttrValue(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws UserException;

  public abstract IDrmLabelValue getRelatedIdLabelByClassValue(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws UserException;

  public abstract IDrmDataObject getRelatedObjectByClassValue(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws UserException;

  public abstract List<IDrmLabelValue> getDbEnums(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract IDynResManageServiceDAO getServiceDAO();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.IDynResManageService
 * JD-Core Version:    0.6.0
 */