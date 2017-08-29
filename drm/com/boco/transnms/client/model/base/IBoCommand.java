package com.boco.transnms.client.model.base;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.base.BoCmdContext;
import java.io.IOException;
import java.io.Serializable;

public abstract interface IBoCommand extends Serializable
{
  public abstract BoCmdContext getCmdContext();

  public abstract Object[] getParas();

  public abstract Object exec()
    throws UserException;

  public abstract Object getResult();

  public abstract byte[] getZipBytes()
    throws IOException;

  public abstract byte[] getBytes()
    throws IOException;

  public abstract UserException getException();

  public abstract void setResult(Object paramObject);

  public abstract void setException(UserException paramUserException);

  public abstract void setCmdTarget(String paramString);

  public abstract String getCmdTarget();

  public abstract void setHostIP(String paramString);

  public abstract void setHostName(String paramString);

  public abstract String getHostIP();

  public abstract String getHostName();

  public abstract void setCmdSize(int paramInt);

  public abstract int getCmdSize();

  public abstract boolean isCompressed();

  public abstract void setCompressed(boolean paramBoolean);

  public abstract long getZipTime();

  public abstract void setZipTime(long paramLong);

  public abstract long getUnZipTime();

  public abstract void setUnZipTime(long paramLong);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.IBoCommand
 * JD-Core Version:    0.6.0
 */