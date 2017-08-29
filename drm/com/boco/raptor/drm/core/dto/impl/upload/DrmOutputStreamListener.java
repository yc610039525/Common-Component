package com.boco.raptor.drm.core.dto.impl.upload;

public abstract interface DrmOutputStreamListener
{
  public abstract void start();

  public abstract void bytesRead(int paramInt);

  public abstract void error(String paramString);

  public abstract void done();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.impl.upload.DrmOutputStreamListener
 * JD-Core Version:    0.6.0
 */