package com.boco.raptor.drm.core.service.vm;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
import com.boco.raptor.drm.core.meta.TemplateMeta;
import com.boco.transnms.common.dto.base.DataObjectList;
import java.util.List;

public abstract interface IVMModelServiceDAO
{
  public abstract List<ExtAttrMetaGroup> getAllAttrGroup(IServiceActionContext paramIServiceActionContext)
    throws UserException;

  public abstract List<TemplateMeta> getAllQueryTemplate(IServiceActionContext paramIServiceActionContext)
    throws UserException;

  public abstract void createAttrGroup(IServiceActionContext paramIServiceActionContext, ExtAttrMetaGroup paramExtAttrMetaGroup)
    throws UserException;

  public abstract void deleteAttrGroup(IServiceActionContext paramIServiceActionContext, ExtAttrMetaGroup paramExtAttrMetaGroup)
    throws UserException;

  public abstract void createQueryTemplate(IServiceActionContext paramIServiceActionContext, TemplateMeta paramTemplateMeta);

  public abstract void modifyQueryTemplate(IServiceActionContext paramIServiceActionContext, TemplateMeta paramTemplateMeta);

  public abstract void deleteQueryTemplate(IServiceActionContext paramIServiceActionContext, TemplateMeta paramTemplateMeta)
    throws UserException;

  public abstract DataObjectList getObjectBySql(IServiceActionContext paramIServiceActionContext, String paramString, Class[] paramArrayOfClass)
    throws Exception;

  public abstract void delOjbectBySql(IServiceActionContext paramIServiceActionContext, String paramString)
    throws Exception;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.vm.IVMModelServiceDAO
 * JD-Core Version:    0.6.0
 */