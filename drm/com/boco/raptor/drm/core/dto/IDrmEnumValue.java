package com.boco.raptor.drm.core.dto;

import java.io.Serializable;

public abstract interface IDrmEnumValue<T> extends Serializable
{
  public abstract String getEnumName();

  public abstract T getEnumValue();

  public abstract IDrmDataObject getEnumDrmDataObject();

  public abstract void setEnumName(String paramString);

  public abstract void setEnumValue(T paramT);

  public abstract void setEnumDrmDataObject(IDrmDataObject paramIDrmDataObject);

  public abstract boolean isEnumValueEqual(T paramT);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmEnumValue
 * JD-Core Version:    0.6.0
 */