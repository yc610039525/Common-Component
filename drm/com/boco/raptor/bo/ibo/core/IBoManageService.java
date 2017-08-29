package com.boco.raptor.bo.ibo.core;

import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.server.bo.base.IBusinessObject;
import java.util.Map;

public abstract interface IBoManageService extends IBusinessObject
{
  public static final String BoName = "IBoManageService";

  public abstract String getBoClassName(BoActionContext paramBoActionContext, String paramString)
    throws UserException;

  public abstract Map<String, String> getBoProxyUrls(Map paramMap);

  public abstract Map<String, String> getBoRemoteProxyUrls(Map paramMap);
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.bo.ibo.core.IBoManageService
 * JD-Core Version:    0.6.0
 */