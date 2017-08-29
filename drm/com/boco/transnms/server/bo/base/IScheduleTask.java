package com.boco.transnms.server.bo.base;

import java.util.Date;
import javax.management.NotificationListener;

public abstract interface IScheduleTask extends NotificationListener
{
  public abstract Integer getTaskId();

  public abstract String getTaskType();

  public abstract Date getScheduleTime();

  public abstract String getTaskMessage();

  public abstract Object getTaskUserData();

  public abstract Date nextScheduleTime();

  public abstract void setTaskId(Integer paramInteger);

  public abstract void setTaskType(String paramString);

  public abstract void setScheduleTime(Date paramDate);

  public abstract void setTaskMessage(String paramString);

  public abstract void setTaskUserData(Object paramObject);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.IScheduleTask
 * JD-Core Version:    0.6.0
 */