package com.boco.common.util.lang;

import java.util.List;

public abstract interface IQueueThreadHandler<E>
{
  public abstract void handle(String paramString, E paramE);

  public abstract void handle(String paramString, List<E> paramList);

  public abstract boolean isSyncWait(E paramE1, E paramE2);

  public abstract boolean isSyncWait(E paramE, List<E> paramList);

  public abstract Object getElementKey(E paramE);

  public abstract void notifyQueueLock();

  public abstract void notifyQueueUnlock();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.lang.IQueueThreadHandler
 * JD-Core Version:    0.6.0
 */