package com.boco.transnms.common.dto.base;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract interface IAttrObject extends Serializable
{
  public abstract String getClassName();

  public abstract Map getAllAttr();

  public abstract String[] getAttrNames();

  public abstract void setClassName(String paramString);

  public abstract boolean containsAttr(String paramString);

  public abstract boolean getAttrBool(String paramString);

  public abstract boolean getAttrBool(String paramString, boolean paramBoolean);

  public abstract byte getAttrByte(String paramString);

  public abstract byte getAttrByte(String paramString, byte paramByte);

  public abstract short getAttrShort(String paramString);

  public abstract short getAttrShort(String paramString, short paramShort);

  public abstract int getAttrInt(String paramString);

  public abstract int getAttrInt(String paramString, int paramInt);

  public abstract long getAttrLong(String paramString);

  public abstract long getAttrLong(String paramString, long paramLong);

  public abstract float getAttrFloat(String paramString);

  public abstract float getAttrFloat(String paramString, float paramFloat);

  public abstract double getAttrDouble(String paramString);

  public abstract double getAttrDouble(String paramString, double paramDouble);

  public abstract String getAttrString(String paramString);

  public abstract Timestamp getAttrDateTime(String paramString);

  public abstract Object getAttrArray(String paramString);

  public abstract List<Serializable> getAttrList(String paramString);

  public abstract AttrObject getAttrObj(String paramString);

  public abstract <T> T getAttrValueT(String paramString);

  public abstract Object getAttrValue(String paramString);

  public abstract void setAttrNull(String paramString);

  public abstract void setAttrValue(String paramString1, String paramString2);

  public abstract void setAttrValue(String paramString, boolean paramBoolean);

  public abstract void setAttrValue(String paramString, byte paramByte);

  public abstract void setAttrValue(String paramString, short paramShort);

  public abstract void setAttrValue(String paramString, int paramInt);

  public abstract void setAttrValue(String paramString, long paramLong);

  public abstract void setAttrValue(String paramString, float paramFloat);

  public abstract void setAttrValue(String paramString, double paramDouble);

  public abstract void setAttrValue(String paramString, Date paramDate);

  public abstract void setAttrValue(String paramString, Object paramObject);

  public abstract void setAttrValue(String paramString, AttrObject paramAttrObject);

  public abstract void setAttrValue(String paramString, ArrayList<Serializable> paramArrayList);

  public abstract void setAttrValues(Map paramMap);

  public abstract void setDefaultAttrValue(String paramString, Object paramObject);

  public abstract void removeDefaultAttrValue(String paramString);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.common.dto.base.IAttrObject
 * JD-Core Version:    0.6.0
 */