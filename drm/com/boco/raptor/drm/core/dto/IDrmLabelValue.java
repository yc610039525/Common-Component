package com.boco.raptor.drm.core.dto;

import java.io.Serializable;
import java.util.Map;

public abstract interface IDrmLabelValue extends Serializable
{
  public abstract Object getDboId();

  public abstract Object getValue();

  public abstract String getLabel();

  public abstract Map getExtMapValue();

  public abstract void setDboId(Object paramObject);

  public abstract void setValue(Object paramObject);

  public abstract void setLabel(String paramString);

  public abstract void setExtMapValue(Map paramMap);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmLabelValue
 * JD-Core Version:    0.6.0
 */