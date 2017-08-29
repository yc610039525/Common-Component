package com.boco.transnms.server.bo.ibo;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.IBusinessObject;
import java.util.List;
import java.util.Map;

public abstract interface IGenericBO extends IBusinessObject
{
  public abstract GenericDO getObject(BoActionContext paramBoActionContext, long paramLong)
    throws UserException;

  public abstract DataObjectList getObjectsBySql(BoActionContext paramBoActionContext, String paramString, GenericDO paramGenericDO)
    throws UserException;

  public abstract DboCollection getObjectsBySql(BoActionContext paramBoActionContext, String paramString, GenericDO[] paramArrayOfGenericDO)
    throws UserException;

  public abstract void deleteObject(BoActionContext paramBoActionContext, Long paramLong)
    throws UserException;

  public abstract void updateObject(BoActionContext paramBoActionContext, GenericDO paramGenericDO)
    throws UserException;

  public abstract void createObject(BoActionContext paramBoActionContext, GenericDO paramGenericDO)
    throws UserException;

  public abstract Object getData(BoActionContext paramBoActionContext, Map paramMap, List paramList, String paramString, Long paramLong, long paramLong1);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.ibo.IGenericBO
 * JD-Core Version:    0.6.0
 */