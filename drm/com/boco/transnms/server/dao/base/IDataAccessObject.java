package com.boco.transnms.server.dao.base;

import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.base.IBoActionContext;
import com.boco.transnms.common.dto.base.IBoQueryContext;
import java.util.List;
import java.util.Map;

public abstract interface IDataAccessObject
{
  public abstract String getDaoName();

  public abstract boolean isClassCached(String paramString);

  public abstract void createObject(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void createObject(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO, boolean paramBoolean)
    throws Exception;

  public abstract void createObjects(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList)
    throws Exception;

  public abstract void updateObject(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void updateObjects(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList, Map paramMap, boolean paramBoolean)
    throws Exception;

  public abstract void updateObjects(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList, Map paramMap)
    throws Exception;

  public abstract void updateObjects(IBoActionContext paramIBoActionContext, String paramString1, String paramString2, Map paramMap)
    throws Exception;

  public abstract void deleteObject(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void deleteObjects(IBoActionContext paramIBoActionContext, List paramList)
    throws Exception;

  public abstract void deleteObjects(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList)
    throws Exception;

  public abstract void deleteObjects(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList, boolean paramBoolean)
    throws Exception;

  public abstract void deleteObjects(IBoActionContext paramIBoActionContext, String paramString1, String paramString2)
    throws Exception;

  public abstract void deleteAll(IBoActionContext paramIBoActionContext, String paramString)
    throws Exception;

  public abstract GenericDO getAttrObj(GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getAttrObjs(long[] paramArrayOfLong, GenericDO paramGenericDO)
    throws Exception;

  public abstract GenericDO getObjByCuid(GenericDO paramGenericDO)
    throws Exception;

  public abstract GenericDO getObjByCuid(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjsByCuids(List<String> paramList, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjsByCuids(IBoActionContext paramIBoActionContext, List<String> paramList, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjByAttrs(IBoQueryContext paramIBoQueryContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjByAttrValues(IBoQueryContext paramIBoQueryContext, GenericDO paramGenericDO, String paramString, List<String> paramList)
    throws Exception;

  public abstract GenericDO getSimpleObject(GenericDO paramGenericDO)
    throws Exception;

  public abstract GenericDO getObject(GenericDO paramGenericDO)
    throws Exception;

  public abstract GenericDO getObject(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjects(long[] paramArrayOfLong, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getObjects(IBoActionContext paramIBoActionContext, long[] paramArrayOfLong, GenericDO paramGenericDO)
    throws Exception;

  public abstract DataObjectList getAllObjByClass(GenericDO paramGenericDO, int paramInt)
    throws Exception;

  public abstract DataObjectList getObjectsBySql(String paramString, GenericDO paramGenericDO, int paramInt)
    throws Exception;

  public abstract DataObjectList getObjectsBySql(IBoActionContext paramIBoActionContext, String paramString, GenericDO paramGenericDO, int paramInt)
    throws Exception;

  public abstract int getCountOfClass(String paramString)
    throws Exception;

  public abstract DboCollection selectDBOs(String paramString, GenericDO[] paramArrayOfGenericDO)
    throws Exception;

  public abstract DboCollection selectDBOs(IBoQueryContext paramIBoQueryContext, String paramString, GenericDO[] paramArrayOfGenericDO)
    throws Exception;

  public abstract DataObjectList selectDBOs(String paramString, Class[] paramArrayOfClass)
    throws Exception;

  public abstract DataObjectList selectDBOs(IBoQueryContext paramIBoQueryContext, String paramString, Class[] paramArrayOfClass)
    throws Exception;

  public abstract int getCalculateValue(String paramString)
    throws Exception;

  public abstract int getCalculateValue(IBoActionContext paramIBoActionContext, String paramString)
    throws Exception;

  public abstract int[] getCalculateValues(String paramString)
    throws Exception;

  public abstract int[] getCalculateValues(IBoActionContext paramIBoActionContext, String paramString)
    throws Exception;

  public abstract boolean hasObjects(String paramString)
    throws Exception;

  public abstract boolean hasObjects(IBoActionContext paramIBoActionContext, String paramString)
    throws Exception;

  public abstract int execSql(String paramString)
    throws Exception;

  public abstract int execSql(IBoActionContext paramIBoActionContext, String paramString)
    throws Exception;

  public abstract void insertDbo(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void insertDbos(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList)
    throws Exception;

  public abstract void updateDbo(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO, String paramString)
    throws Exception;

  public abstract void updateDbo(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void updateBlob(IBoActionContext paramIBoActionContext, String paramString1, String paramString2, DboBlob paramDboBlob, String paramString3)
    throws Exception;

  public abstract void reInitCache(GenericDO paramGenericDO)
    throws Exception;

  public abstract void clearCache(GenericDO paramGenericDO)
    throws Exception;

  public abstract String getLabelCnByCuid(String paramString)
    throws Exception;

  public abstract Map getLabelCnsByCuids(String[] paramArrayOfString)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.dao.base.IDataAccessObject
 * JD-Core Version:    0.6.0
 */