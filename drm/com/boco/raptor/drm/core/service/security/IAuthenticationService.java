package com.boco.raptor.drm.core.service.security;

import com.boco.raptor.common.service.IService;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.IDrmDataObject;

public abstract interface IAuthenticationService extends IService
{
  public static final String SESSION_USER_ID = "SESSION_USER_ID";
  public static final String SERVICE_ID = "AuthenticationService";

  public abstract void addAuthentication(IServiceActionContext paramIServiceActionContext, IAuthentication paramIAuthentication);

  public abstract void removeAuthentication(IServiceActionContext paramIServiceActionContext, IAuthentication paramIAuthentication);

  public abstract void removeAuthentication(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract IAuthentication getAuthentication(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract void isActionValid(String paramString1, String paramString2);

  public abstract void isObjectValid(IServiceActionContext paramIServiceActionContext, IDrmDataObject paramIDrmDataObject)
    throws Exception;

  public abstract String getBmClassMetaAuthenticationSql(IServiceActionContext paramIServiceActionContext);

  public abstract IUserDetails getAdmin(IServiceActionContext paramIServiceActionContext);

  public abstract void setAdmin(IUserDetails paramIUserDetails);

  public abstract void setObjectValidService(IObjectValidService paramIObjectValidService);

  public abstract void setSecurityService(ISecurityService paramISecurityService);

  public abstract ISecurityService getSecurityService();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.security.IAuthenticationService
 * JD-Core Version:    0.6.0
 */