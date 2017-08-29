package com.boco.raptor.drm.core.dto;

public abstract interface IDrmMemQueryContext extends IDrmQueryContext
{
  public static final int CLEAR_REQUEST = 1;
  public static final int CLEAR_PAGE = 2;
  public static final int CLEAR_SESSION = 3;

  public abstract String getSessionId();

  public abstract String getPageId();

  public abstract String getRequestId();

  public abstract boolean isRefresh();

  public abstract int getClearType();

  public abstract int getMaxBufSize();

  public abstract void setSessionId(String paramString);

  public abstract void setPageId(String paramString);

  public abstract void setRequestId(String paramString);

  public abstract void setRefresh(boolean paramBoolean);

  public abstract void setClearType(int paramInt);

  public abstract void setMaxBufSize(int paramInt);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmMemQueryContext
 * JD-Core Version:    0.6.0
 */