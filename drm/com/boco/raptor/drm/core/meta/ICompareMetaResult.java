package com.boco.raptor.drm.core.meta;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface ICompareMetaResult extends Serializable
{
  public abstract List<BMClassMeta> getAddedClassMetas();

  public abstract List<String> getRemovedClassIds();

  public abstract List<String> getUpdatedClassIds();

  public abstract Map<String, Object> getUpdatedClassEles(String paramString);

  public abstract List<BMAttrMeta> getAddedAttrMetas();

  public abstract List<String> getRemovedAttrIds();

  public abstract List<String> getUpdatedAttrIds();

  public abstract Map<String, Object> getUpdatedAttrEles(String paramString1, String paramString2);

  public abstract List<BMEnumMeta> getAddedEnums();

  public abstract List<BMEnumMeta> getRemovedEnums();

  public abstract List<BMEnumMeta> getUpdatedEnums();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.meta.ICompareMetaResult
 * JD-Core Version:    0.6.0
 */