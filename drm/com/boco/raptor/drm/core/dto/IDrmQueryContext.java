package com.boco.raptor.drm.core.dto;

import java.io.Serializable;

public abstract interface IDrmQueryContext extends Serializable
{
  public abstract int getOffset();

  public abstract String getOrderField();

  public abstract int getFetchSize();

  public abstract boolean isByPage();

  public abstract boolean isCountBeforQuery();

  public abstract boolean isOrderDesc();

  public abstract void setCountBeforQuery(boolean paramBoolean);

  public abstract void setOffset(int paramInt);

  public abstract void setOrderField(String paramString);

  public abstract void setOrderDesc(boolean paramBoolean);

  public abstract void setFetchSize(int paramInt);

  public abstract boolean isPopulate();

  public abstract void setPopulate(boolean paramBoolean);

  public abstract boolean isEntity();

  public abstract void setEntity(boolean paramBoolean);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmQueryContext
 * JD-Core Version:    0.6.0
 */