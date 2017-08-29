package com.boco.transnms.server.dao.base;

public abstract interface IDaoHome
{
  public abstract IDataAccessObject getDAO(String paramString);

  public abstract String[] getDaoNames();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.IDaoHome
 * JD-Core Version:    0.6.0
 */