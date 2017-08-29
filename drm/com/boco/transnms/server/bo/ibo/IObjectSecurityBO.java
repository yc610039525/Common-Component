package com.boco.transnms.server.bo.ibo;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.base.IBoActionContext;
import com.boco.transnms.server.bo.base.IBusinessObject;
import java.util.Map;

public abstract interface IObjectSecurityBO extends IBusinessObject
{
  public abstract String getQueryFilterSql(BoActionContext paramBoActionContext, String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5)
    throws UserException;

  public abstract void isObjectPermitEdit(IBoActionContext paramIBoActionContext, GenericDO paramGenericDO)
    throws Exception;

  public abstract void isObjectsPermitEdit(IBoActionContext paramIBoActionContext, DataObjectList paramDataObjectList)
    throws Exception;

  public abstract String getQueryFilterSqlToEMS(IBoActionContext paramIBoActionContext, Map paramMap)
    throws Exception;

  public abstract String getQueryFilterSqlToSubNet(IBoActionContext paramIBoActionContext, Map paramMap)
    throws Exception;

  public abstract String getQueryFilterSqlAndCardKind(BoActionContext paramBoActionContext, String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.transnms.server.bo.ibo.IObjectSecurityBO
 * JD-Core Version:    0.6.0
 */