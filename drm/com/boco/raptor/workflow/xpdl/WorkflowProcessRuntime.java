package com.boco.raptor.workflow.xpdl;

import java.util.Map;

public abstract interface WorkflowProcessRuntime
{
  public abstract String runActivity(String paramString, Map paramMap)
    throws Exception;

  public abstract String createInstance(Map paramMap)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.workflow.xpdl.WorkflowProcessRuntime
 * JD-Core Version:    0.6.0
 */