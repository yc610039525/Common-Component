package com.boco.raptor.bo.ibo.core;

import com.boco.raptor.common.message.IMessage;
import com.boco.raptor.common.message.ISimpleMsgListener;
import com.boco.transnms.common.dto.base.IBoActionContext;
import com.boco.transnms.server.bo.base.IBusinessObject;

public abstract interface IMsgBusServiceBO extends IBusinessObject
{
  public static final String BoName = "IMsgBusServiceBO";

  public abstract void sendMessage(IBoActionContext paramIBoActionContext, IMessage paramIMessage);

  public abstract void addMsgListener(IBoActionContext paramIBoActionContext, String paramString1, String paramString2, ISimpleMsgListener paramISimpleMsgListener)
    throws Exception;

  public abstract void removeMsgListener(IBoActionContext paramIBoActionContext, ISimpleMsgListener paramISimpleMsgListener)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.ibo.core.IMsgBusServiceBO
 * JD-Core Version:    0.6.0
 */