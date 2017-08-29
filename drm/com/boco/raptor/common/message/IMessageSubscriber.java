package com.boco.raptor.common.message;

import java.io.Serializable;

public abstract interface IMessageSubscriber extends Serializable
{
  public abstract String getSessionId();

  public abstract void addMsgListener(IMessageListener paramIMessageListener);

  public abstract void removeMsgListener(IMessageListener paramIMessageListener);

  public abstract void createSession(String paramString, Serializable paramSerializable);

  public abstract void createSession(IMessageFilter paramIMessageFilter);

  public abstract void closeSession();

  public abstract void resetFilterPara(Object paramObject);

  public abstract void setTopicName(String paramString);

  public abstract MsgServiceTypeEnum getMsgServieType();

  public abstract void setSessionActive(boolean paramBoolean);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.IMessageSubscriber
 * JD-Core Version:    0.6.0
 */