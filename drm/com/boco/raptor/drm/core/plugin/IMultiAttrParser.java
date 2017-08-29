package com.boco.raptor.drm.core.plugin;

import com.boco.raptor.drm.core.meta.BMAttrMeta;

public abstract interface IMultiAttrParser
{
  public abstract String[] parseValues(BMAttrMeta paramBMAttrMeta, String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.plugin.IMultiAttrParser
 * JD-Core Version:    0.6.0
 */