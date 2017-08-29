package com.boco.raptor.drm.core.dto;

import java.util.Map;

public abstract interface IDrmDataObject
{
  public abstract String getBmClassId();

  public abstract void setBmClassId(String paramString);

  public abstract String getDbClassId();

  public abstract void setDbClassId(String paramString);

  public abstract Long getDboId();

  public abstract void setDboId(Long paramLong);

  public abstract String getCuid();

  public abstract void setCuid(String paramString);

  public abstract Map<String, Object> getAllAttr();

  public abstract String[] getAllAttrId();

  public abstract void setAttrValues(Map<String, Object> paramMap);

  public abstract <T> T getAttrValueT(String paramString);

  public abstract Object getAttrValue(String paramString);

  public abstract void setAttrValue(String paramString, Object paramObject);

  public abstract boolean containsAttr(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmDataObject
 * JD-Core Version:    0.6.0
 */