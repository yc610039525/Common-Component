package com.boco.raptor.drm.core.service.security;

import java.io.Serializable;

public abstract interface IFunctionNode extends Serializable
{
  public abstract String getNodeActionNames();

  public abstract String getNodeCode();

  public abstract String getNodeId();

  public abstract String getNodeName();

  public abstract String getParendNode();

  public abstract void setParendNode(String paramString);

  public abstract void setNodeName(String paramString);

  public abstract void setNodeId(String paramString);

  public abstract void setNodeCode(String paramString);

  public abstract void setNodeActionNames(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IFunctionNode
 * JD-Core Version:    0.6.0
 */