package com.boco.raptor.drm.core.service.dynres;

import com.boco.raptor.drm.core.dto.IDrmDataObject;
import com.boco.raptor.drm.core.dto.IDrmQueryResultSet;
import com.boco.raptor.drm.core.meta.BMClassMeta;
import java.util.List;

public abstract interface IRelatedObjectHandler
{
  public abstract List<String> getSupportClassIds();

  public abstract int getRelatedDeleteObjectCount(String paramString, BMClassMeta paramBMClassMeta, IDrmDataObject paramIDrmDataObject);

  public abstract boolean isHaveRelatedDeleteObject(String paramString, BMClassMeta paramBMClassMeta, IDrmDataObject paramIDrmDataObject);

  public abstract IDrmQueryResultSet getRelatedDeleteObjects(String paramString, BMClassMeta paramBMClassMeta, IDrmDataObject paramIDrmDataObject);

  public abstract List<String> getRelatedDeleteCuids(String paramString, BMClassMeta paramBMClassMeta, IDrmDataObject paramIDrmDataObject);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.dynres.IRelatedObjectHandler
 * JD-Core Version:    0.6.0
 */