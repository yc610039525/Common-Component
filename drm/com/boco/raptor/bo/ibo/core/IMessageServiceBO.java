package com.boco.raptor.bo.ibo.core;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.message.IMessage;
import com.boco.raptor.common.message.IMessageFilter;
import com.boco.raptor.common.message.IMessagePublisher;
import com.boco.raptor.common.message.ISessionListener;
import com.boco.raptor.common.message.MsgServiceTypeEnum;
import com.boco.transnms.common.dto.base.IBoActionContext;
import com.boco.transnms.server.bo.base.IBusinessObject;
import java.util.List;

public abstract interface IMessageServiceBO extends IBusinessObject
{
  public static final String BoName = "MessageServiceBO";

  public abstract String addSession(IBoActionContext paramIBoActionContext, MsgServiceTypeEnum paramMsgServiceTypeEnum, String paramString, IMessageFilter paramIMessageFilter)
    throws UserException;

  public abstract String addSession(IBoActionContext paramIBoActionContext, MsgServiceTypeEnum paramMsgServiceTypeEnum, String paramString1, String paramString2, Object paramObject)
    throws UserException;

  public abstract void delSession(IBoActionContext paramIBoActionContext, String paramString);

  public abstract void modifyFilterPara(IBoActionContext paramIBoActionContext, String paramString, Object paramObject);

  public abstract IMessageFilter getMsgFilter(IBoActionContext paramIBoActionContext, String paramString);

  public abstract void addMessage(IBoActionContext paramIBoActionContext, IMessage paramIMessage);

  public abstract void modifyMsgPublishers(List<IMessagePublisher> paramList);

  public abstract void modifyActive(IBoActionContext paramIBoActionContext, Boolean paramBoolean);

  public abstract boolean isActive(IBoActionContext paramIBoActionContext);

  public abstract void modifyTopicActive(IBoActionContext paramIBoActionContext, String paramString, Boolean paramBoolean);

  public abstract boolean isTopicActive(IBoActionContext paramIBoActionContext, String paramString);

  public abstract void modifySessionActive(IBoActionContext paramIBoActionContext, String paramString, Boolean paramBoolean);

  public abstract void addSessionListener(ISessionListener paramISessionListener);

  public abstract void delSessionListener(ISessionListener paramISessionListener);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.ibo.core.IMessageServiceBO
 * JD-Core Version:    0.6.0
 */