package com.boco.raptor.drm.core.service.bm;

import com.boco.raptor.drm.core.meta.BMClassMeta;
import com.boco.raptor.drm.core.meta.ICompareMetaResult;
import java.util.Map;

public abstract interface ILoadModelListener
{
  public abstract void syncCompareModel(Map<String, BMClassMeta> paramMap);

  public abstract void notifyBModelChanged(ICompareMetaResult paramICompareMetaResult);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.bm.ILoadModelListener
 * JD-Core Version:    0.6.0
 */