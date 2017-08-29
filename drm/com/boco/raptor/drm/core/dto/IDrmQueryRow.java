package com.boco.raptor.drm.core.dto;

import java.io.Serializable;
import java.util.Map;

public abstract interface IDrmQueryRow extends Serializable
{
  public abstract IDrmDataObject getResultDbo(String paramString);

  public abstract <T> T getResAttrValue(String paramString1, String paramString2);

  public abstract Map<String, IDrmDataObject> getDboRow();

  public abstract Map<String, Object> getAttrRow();

  public abstract void setResultRow(Map<String, IDrmDataObject> paramMap);

  public abstract void addResultDbo(IDrmDataObject paramIDrmDataObject);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.dto.IDrmQueryRow
 * JD-Core Version:    0.6.0
 */