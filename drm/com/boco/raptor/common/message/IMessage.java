package com.boco.raptor.common.message;

import java.io.Serializable;

public abstract interface IMessage extends Serializable
{
  public abstract String getTopicName();

  public abstract long getTimestamp();

  public abstract void setTimestamp(long paramLong);

  public abstract <T extends Serializable> T getDataObject();

  public abstract void setDataObject(Serializable paramSerializable);

  public abstract String getSourceName();

  public abstract void setSourceName(String paramString);

  public abstract void setTargetId(String paramString);

  public abstract String getTargetId();

  public abstract <T extends Serializable> T getAttachData();

  public abstract void setAttachData(Serializable paramSerializable);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.IMessage
 * JD-Core Version:    0.6.0
 */