package com.boco.raptor.drm.core.dto;

import java.io.Serializable;
import java.util.List;

public abstract interface IDrmQueryResultSet extends Serializable
{
  public abstract int getCountValue();

  public abstract void setCountValue(int paramInt);

  public abstract int getOffset();

  public abstract void setOffset(int paramInt);

  public abstract int getPageSize();

  public abstract void setFetchSize(int paramInt);

  public abstract int getFetchSize();

  public abstract boolean isEntity();

  public abstract void setEntity(boolean paramBoolean);

  public abstract boolean isPopulate();

  public abstract void setPopulate(boolean paramBoolean);

  public abstract List<IDrmQueryRow> getResultSet();

  public abstract void addRow(IDrmQueryRow paramIDrmQueryRow);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmQueryResultSet
 * JD-Core Version:    0.6.0
 */