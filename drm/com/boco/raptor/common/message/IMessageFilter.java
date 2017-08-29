package com.boco.raptor.common.message;

import java.io.Serializable;

public abstract interface IMessageFilter extends Serializable, Cloneable
{
  public abstract boolean isMsgPublish(IMessage paramIMessage);

  public abstract void setFilterPara(Object paramObject);

  public abstract String getFilterName();

  public abstract <T extends IMessageFilter> T cloneFilter();

  public abstract void setSessionId(String paramString);

  public abstract String getSessionId();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.IMessageFilter
 * JD-Core Version:    0.6.0
 */