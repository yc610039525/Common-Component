package com.boco.raptor.common.message;

import javax.jms.Session;

public abstract interface IMsgDestination
{
  public abstract Session getSession();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.IMsgDestination
 * JD-Core Version:    0.6.0
 */