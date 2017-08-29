package com.boco.transnms.server.dao.base;

import com.boco.common.util.db.UserTransaction;
import com.cmcc.tm.middleware.manager.IObjectManager;

public abstract interface IObjectDataSource
{
  public abstract IObjectManager getObjectManager();

  public abstract void close();

  public abstract boolean isClosed();

  public abstract UserTransaction getTransaction();

  public abstract void setTransaction(UserTransaction paramUserTransaction);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.IObjectDataSource
 * JD-Core Version:    0.6.0
 */