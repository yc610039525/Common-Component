package com.boco.transnms.common.dto.base;

public abstract interface IBoQueryContext extends IBoActionContext
{
  public abstract int getFetchSize();

  public abstract int getOffset();

  public abstract String getOrderField();

  public abstract boolean isCountBeforQuery();

  public abstract boolean isOrderDesc();

  public abstract boolean isQueryDeleted();

  public abstract void setCountBeforQuery(boolean paramBoolean);

  public abstract void setFetchSize(int paramInt);

  public abstract void setOffset(int paramInt);

  public abstract void setOrderDesc(boolean paramBoolean);

  public abstract void setOrderField(String paramString);

  public abstract void setQueryDeleted(boolean paramBoolean);

  public abstract void setQueryCountOnly(boolean paramBoolean);

  public abstract boolean isQueryCountOnly();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.IBoQueryContext
 * JD-Core Version:    0.6.0
 */