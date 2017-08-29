package com.boco.transnms.server.bo.base;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StateLessBO
{
  public abstract String serverName();

  public abstract boolean initByAllServer();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.base.StateLessBO
 * JD-Core Version:    0.6.0
 */