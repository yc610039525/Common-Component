package com.boco.raptor.workflow.xpdl;

import java.util.Map;

public abstract interface Hook
{
  public abstract void execute(ActivityXPDL paramActivityXPDL, Map paramMap)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.Hook
 * JD-Core Version:    0.6.0
 */