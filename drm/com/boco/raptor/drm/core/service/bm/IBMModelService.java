package com.boco.raptor.drm.core.service.bm;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IService;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.IDrmLabelValue;
import com.boco.raptor.drm.core.meta.BMAttrMeta;
import com.boco.raptor.drm.core.meta.BMClassMeta;
import com.boco.raptor.drm.core.meta.BMEnumMeta;
import com.boco.raptor.drm.core.meta.ICompareMetaResult;
import com.boco.transnms.common.dto.DrmModelLogIndex;
import java.util.List;
import java.util.Map;

public abstract interface IBMModelService extends IService
{
  public static final String SERVICE_ID = "BMModelService";

  public abstract void setModelFilePath(String paramString);

  public abstract ICompareMetaResult loadModelFile(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3)
    throws UserException;

  public abstract BMClassMeta[] getAllClassMeta(IServiceActionContext paramIServiceActionContext);

  public abstract IDrmLabelValue[] getAllClassMetaLabelValue(IServiceActionContext paramIServiceActionContext);

  public abstract List<BMClassMeta> getStatClassMeta(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract BMClassMeta getClassMeta(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract List<BMClassMeta> getClassMetas(IServiceActionContext paramIServiceActionContext, List<String> paramList);

  public abstract BMAttrMeta getAttrMeta(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2);

  public abstract BMEnumMeta[] getAllEnumMetas(IServiceActionContext paramIServiceActionContext);

  public abstract BMEnumMeta getEnumMeta(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract BMEnumMeta getRelationEnumMeta(IServiceActionContext paramIServiceActionContext, Map paramMap);

  public abstract DrmModelLogIndex getModelCompareLog(IServiceActionContext paramIServiceActionContext, byte[] paramArrayOfByte)
    throws UserException;

  public abstract DrmModelLogIndex getModelCompareLog(IServiceActionContext paramIServiceActionContext, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws UserException;

  public abstract void modifyModelMeta(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws UserException;

  public abstract void modifyModelMeta(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws UserException;

  public abstract void printBmModel(IServiceActionContext paramIServiceActionContext);

  public abstract void setLoadModelListener(ILoadModelListener paramILoadModelListener);

  public abstract ILoadModelListener getLoadModelListener();
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.bm.IBMModelService
 * JD-Core Version:    0.6.0
 */