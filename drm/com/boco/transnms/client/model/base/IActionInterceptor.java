package com.boco.transnms.client.model.base;

import javax.swing.Action;

public abstract interface IActionInterceptor
{
  public abstract boolean isActionEnable(Action paramAction);

  public abstract boolean isActionEnable(String paramString);

  public abstract boolean isActionVisible(Action paramAction);

  public abstract boolean isActionVisible(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.client.model.base.IActionInterceptor
 * JD-Core Version:    0.6.0
 */