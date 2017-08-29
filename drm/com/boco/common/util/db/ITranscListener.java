package com.boco.common.util.db;

public abstract interface ITranscListener
{
  public abstract void doTranscBegin(UserTransaction paramUserTransaction);

  public abstract void doTranscCommit(UserTransaction paramUserTransaction);

  public abstract void doTranscRollback(UserTransaction paramUserTransaction);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.common.util.db.ITranscListener
 * JD-Core Version:    0.6.0
 */