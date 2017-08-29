package com.boco.raptor.bo.ibo.core;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.message.IMessage;
import com.boco.transnms.common.dto.base.IBoActionContext;
import com.boco.transnms.server.bo.base.IBusinessObject;
import java.util.List;

public abstract interface IXrpcMsgServiceBO extends IBusinessObject
{
  public static final String BoName = "XrpcMsgServiceBO";

  public abstract void addSession(IBoActionContext paramIBoActionContext, String paramString);

  public abstract void addMessage(IBoActionContext paramIBoActionContext, List<String> paramList, IMessage paramIMessage);

  public abstract List<IMessage> getMessages(IBoActionContext paramIBoActionContext, String paramString)
    throws UserException;

  public abstract void delSession(IBoActionContext paramIBoActionContext, String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.ibo.core.IXrpcMsgServiceBO
 * JD-Core Version:    0.6.0
 */