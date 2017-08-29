package com.boco.raptor.drm.core.service.vm;

import com.boco.common.util.except.UserException;
import com.boco.raptor.common.service.IService;
import com.boco.raptor.common.service.IServiceActionContext;
import com.boco.raptor.drm.core.dto.impl.upload.DrmUploadClassMeta;
import com.boco.raptor.drm.core.meta.BMAttrMeta;
import com.boco.raptor.drm.core.meta.ClassAttrGroupMeta;
import com.boco.raptor.drm.core.meta.ExcelImpTemplateMeta;
import com.boco.raptor.drm.core.meta.ExtAttrMetaGroup;
import com.boco.raptor.drm.core.meta.TemplateMeta;
import com.boco.transnms.common.dto.Drm;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jxl.write.WriteException;

public abstract interface IVMModelService extends IService
{
  public static final String SERVICE_ID = "VMModelService";

  public abstract void setServiceDAO(IVMModelServiceDAO paramIVMModelServiceDAO);

  public abstract void setDefaultUserId(IServiceActionContext paramIServiceActionContext, String paramString);

  public abstract ExtAttrMetaGroup getQueryTableMeta(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract ExtAttrMetaGroup getPropertyMeta(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract List<TemplateMeta> getQueryTemplates(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract void setPropertyMeta(IServiceActionContext paramIServiceActionContext, String paramString, ExtAttrMetaGroup paramExtAttrMetaGroup)
    throws UserException;

  public abstract void setQueryTableMeta(IServiceActionContext paramIServiceActionContext, String paramString, ExtAttrMetaGroup paramExtAttrMetaGroup)
    throws UserException;

  public abstract void modifyQueryTableMeta(IServiceActionContext paramIServiceActionContext, String paramString, ExtAttrMetaGroup paramExtAttrMetaGroup)
    throws UserException;

  public abstract void addQueryTemplate(IServiceActionContext paramIServiceActionContext, String paramString, TemplateMeta paramTemplateMeta)
    throws UserException;

  public abstract void modifyQueryTemplate(IServiceActionContext paramIServiceActionContext, String paramString, TemplateMeta paramTemplateMeta)
    throws UserException;

  public abstract void modifyQueryTemplatesSortNo(IServiceActionContext paramIServiceActionContext, String paramString, Map<String, TemplateMeta> paramMap)
    throws UserException;

  public abstract void deleteQueryTemplate(IServiceActionContext paramIServiceActionContext, String paramString, TemplateMeta paramTemplateMeta)
    throws UserException;

  public abstract List<ExcelImpTemplateMeta> getExcelImpTemplates(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract ExcelImpTemplateMeta getExcelImpTemplate(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2)
    throws UserException;

  public abstract ExcelImpTemplateMeta addExcelImpTemplate(IServiceActionContext paramIServiceActionContext, String paramString, ExcelImpTemplateMeta paramExcelImpTemplateMeta)
    throws UserException;

  public abstract void modifyExcelImpTemplate(IServiceActionContext paramIServiceActionContext, String paramString, ExcelImpTemplateMeta paramExcelImpTemplateMeta)
    throws UserException;

  public abstract void deleteExcelImpTemplate(IServiceActionContext paramIServiceActionContext, String paramString, ExcelImpTemplateMeta paramExcelImpTemplateMeta)
    throws UserException;

  public abstract int saveExcelImportData(IServiceActionContext paramIServiceActionContext, String paramString1, List<DrmUploadClassMeta> paramList, String paramString2)
    throws UserException;

  public abstract List<TemplateMeta> getExcelExpTemplates(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract List<BMAttrMeta> getExcelExpAttrList(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2)
    throws UserException;

  public abstract ExcelImpTemplateMeta addExcelExpTemplate(IServiceActionContext paramIServiceActionContext, String paramString, ExcelImpTemplateMeta paramExcelImpTemplateMeta)
    throws UserException;

  public abstract void deleteExcelExpTemplate(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2)
    throws UserException;

  public abstract void synAttrGroup(IServiceActionContext paramIServiceActionContext, List<Drm> paramList, List<String> paramList1)
    throws UserException;

  public abstract List<ClassAttrGroupMeta> getClassAttrGroup(IServiceActionContext paramIServiceActionContext)
    throws UserException;

  public abstract ClassAttrGroupMeta addClassAttrGroup(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract void modifyClassAttrGroup(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2)
    throws UserException;

  public abstract String delClassAttrGroup(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract ClassAttrGroupMeta getClassGroupAttr(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2)
    throws UserException;

  public abstract Map<String, ClassAttrGroupMeta> getClassGroupAttrMap(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;

  public abstract void saveClassGroupAttr(IServiceActionContext paramIServiceActionContext, String paramString, ClassAttrGroupMeta paramClassAttrGroupMeta)
    throws UserException;

  public abstract void saveClassGroupAttrMap(IServiceActionContext paramIServiceActionContext, String paramString, List<ClassAttrGroupMeta> paramList)
    throws UserException;

  public abstract void modifyClassAttrGroupSort(IServiceActionContext paramIServiceActionContext, List<String> paramList)
    throws UserException;

  public abstract Map validateExcelFile(IServiceActionContext paramIServiceActionContext, byte[] paramArrayOfByte, String paramString1, String paramString2)
    throws UserException;

  public abstract Map importExcelData(IServiceActionContext paramIServiceActionContext, String paramString1, List<DrmUploadClassMeta> paramList, String paramString2)
    throws UserException;

  public abstract String createResultFile(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3, String paramString4, byte[] paramArrayOfByte, List<DrmUploadClassMeta> paramList, List paramList1)
    throws UserException;

  public abstract String[] addTemplateHeaderMatrixClass(IServiceActionContext paramIServiceActionContext, String paramString1, String paramString2, String paramString3, ExcelImpTemplateMeta paramExcelImpTemplateMeta)
    throws IOException, WriteException, Exception;

  public abstract boolean isHideResNav(IServiceActionContext paramIServiceActionContext)
    throws UserException;

  public abstract void setResNavVisible(IServiceActionContext paramIServiceActionContext, String paramString)
    throws UserException;
}

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.service.vm.IVMModelService
 * JD-Core Version:    0.6.0
 */