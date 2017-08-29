package com.boco.transnms.common.dto.base;

public abstract interface IQueryCollection
{
  public abstract int size();

  public abstract GenericDO getQueryDbo(int paramInt, String paramString);

  public abstract <T> T getQueryAttrValue(int paramInt, String paramString1, String paramString2);

  public abstract String getQueryAttrString(int paramInt, String paramString1, String paramString2);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.IQueryCollection
 * JD-Core Version:    0.6.0
 */