package com.boco.raptor.drm.core.dto;

public abstract interface IDrmRelatedIdValue extends IDrmLabelValue
{
  public abstract String getDbClassId();

  public abstract String getBmClassId();

  public abstract void setDbClassId(String paramString);

  public abstract void setBmClassId(String paramString);

  public abstract String getAttrId();

  public abstract void setAttrId(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmRelatedIdValue
 * JD-Core Version:    0.6.0
 */