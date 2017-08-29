package com.boco.raptor.common.message;

import java.util.List;

public abstract interface IMessagePublisher
{
  public abstract MsgServiceTypeEnum getMsgServiceType();

  public abstract String getTopicName();

  public abstract void publishMessage(List<String> paramList, IMessage paramIMessage)
    throws Exception;

  public abstract void initPublisher()
    throws Exception;

  public abstract void setActive(boolean paramBoolean);

  public abstract boolean isActive();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.IMessagePublisher
 * JD-Core Version:    0.6.0
 */