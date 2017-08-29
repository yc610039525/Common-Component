package com.boco.transnms.server.bo.path;

import com.boco.common.util.db.TransactionFactory;
import com.boco.common.util.db.UserTransaction;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.transnms.common.dto.AttempPtnPath;
import com.boco.transnms.common.dto.AttempTraph;
import com.boco.transnms.common.dto.BackboneCollect;
import com.boco.transnms.common.dto.NeModelCfgType;
import com.boco.transnms.common.dto.PtnAtm;
import com.boco.transnms.common.dto.PtnEth;
import com.boco.transnms.common.dto.PtnIpCrossconnect;
import com.boco.transnms.common.dto.PtnPath;
import com.boco.transnms.common.dto.PtnPortBanding;
import com.boco.transnms.common.dto.PtnTdm;
import com.boco.transnms.common.dto.PtnTpPair;
import com.boco.transnms.common.dto.PtnTurnnel;
import com.boco.transnms.common.dto.PtnVirtualLine;
import com.boco.transnms.common.dto.Ptp;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.TDeviceToPerson;
import com.boco.transnms.common.dto.TransElement;
import com.boco.transnms.common.dto.Traph;
import com.boco.transnms.common.dto.TunnelToVirtualLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.misc.LayerRate;
import com.boco.transnms.common.dto.path.TunnelGraphicSeg;
import com.boco.transnms.common.dto.path.TunnelGraphicSegLine;
import com.boco.transnms.common.dto.path.TunnelGraphicSegNode;
import com.boco.transnms.common.dto.path.TunnelGraphicView;
import com.boco.transnms.common.dto.traph.TunnelInfo;
import com.boco.transnms.server.bo.base.AbstractBO;
import com.boco.transnms.server.bo.ibo.path.IPtnBOX;
import com.boco.transnms.server.dao.base.DaoHelper;
import com.boco.transnms.server.dao.path.PtnDAOX;
import com.boco.transnms.server.dao.traph.TraphDAOX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

public class PtnBOX extends AbstractBO
  implements IPtnBOX
{
  private static final String e = null;
private static final Throwable ex = null;

public DboCollection getPseudoWireByPage(BoQueryContext queryContext, Map paramMap)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      if (paramMap != null)
        dbos = getPtnDAOX().getPseudoWireByPage(queryContext, paramMap);
    }
    catch (Exception e) {
      LogHome.getLog().error("分页查询伪线信息异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getTunnelByEmsAndTunnelName(BoActionContext actionContext, String emsCuid, String tunnelName)
    throws UserException
  {
    DataObjectList tunnelList = null;
    if (StringUtils.isNotEmpty(emsCuid)) {
      try {
        String sql = "RELATED_EMS_CUID='" + emsCuid + "'";
        if (StringUtils.isNotEmpty(tunnelName)) {
          sql = sql + " and NATIVE_EMS_NAME like '%" + tunnelName + "%'";
        }
        tunnelList = getPtnDAOX().getPtnTurnnelsBySql(sql);
      } catch (Exception e) {
        LogHome.getLog().error("根据EMS和隧道名称查询隧道异常", e);
        throw new UserException(e);
      }
    }
    return tunnelList;
  }

  public DataObjectList getTraphByPseudoWireCuid(BoActionContext actionContext, String pseudoWireCuid)
    throws UserException
  {
    DataObjectList traphList = new DataObjectList();
    try
    {
      DboCollection dbos = getPtnDAOX().getTraphByPtnVir(actionContext, pseudoWireCuid);
      if ((dbos != null) && (dbos.size() > 0))
        for (int i = 0; i < dbos.size(); i++) {
          PtnPath dbo = (PtnPath)dbos.getAttrField("PTN_PATH", i);
          if (dbo != null) {
            String traphCuid = dbo.getRelatedRouteCuid();
            if (traphCuid.indexOf("TRAPH") == 0) {
              Traph traph = (Traph)getPtnDAOX().getObjByCuid(traphCuid);
              if (traph != null)
                traphList.add(traph);
            }
          }
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("查询伪线承载电路异常", e);
      throw new UserException(e);
    }
    return traphList;
  }

  public DataObjectList getTraphByPseudoWireFdn(BoActionContext actionContext, String pseudoWireFdn)
    throws UserException
  {
    DataObjectList traphList = new DataObjectList();
    try {
      PtnVirtualLine virLine = getPtnDAOX().getPtnVirByFdn(pseudoWireFdn);
      if (virLine != null) {
        DboCollection dbos = getPtnDAOX().getTraphByPtnVir(actionContext, virLine.getCuid());
        if ((dbos != null) && (dbos.size() > 0)) {
          for (int i = 0; i < dbos.size(); i++) {
            PtnPath dbo = (PtnPath)dbos.getAttrField("PTN_PATH", i);
            if (dbo != null) {
              String traphCuid = dbo.getRelatedRouteCuid();
              if (traphCuid.indexOf("TRAPH") == 0) {
                Traph traph = (Traph)getPtnDAOX().getObjByCuid(traphCuid);
                if (traph != null) {
                  traphList.add(traph);
                }
              }
            }
          }
        }
      }
      long begin = System.currentTimeMillis();
      DataObjectList serviceList = getPtnDAOX().getServiceByPseudoWireFdn(pseudoWireFdn);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        String origPtpCuid = "";
        String destPtpCuid = "";
        String vlanId = "";
        for (int i = 0; i < serviceList.size(); i++) {
          GenericDO dbo = (GenericDO)serviceList.get(i);
          if (dbo.getCuid().startsWith("PTN_ETH")) {
            PtnEth ptnEth = (PtnEth)dbo;
            origPtpCuid = ptnEth.getOrigPtpCuid();
            destPtpCuid = ptnEth.getDestPtpCuid();
            vlanId = ptnEth.getVlanId();
          } else if (dbo.getCuid().startsWith("PTN_TDM")) {
            PtnTdm ptnTdm = (PtnTdm)dbo;
            origPtpCuid = ptnTdm.getOrigPtpCuid();
            destPtpCuid = ptnTdm.getDestPtpCuid();
          } else if (dbo.getCuid().startsWith("PTN_ATM")) {
            PtnAtm ptnAtm = (PtnAtm)dbo;
            origPtpCuid = ptnAtm.getOrigPtpCuid();
            destPtpCuid = ptnAtm.getDestPtpCuid();
          }
          if ((StringUtils.isNotEmpty(origPtpCuid)) && (StringUtils.isNotEmpty(destPtpCuid))) {
            DboCollection dbos = getTraphDAOX().getTraphByPhysicalPtp(actionContext, origPtpCuid, destPtpCuid, vlanId);
            if ((dbos == null) || (dbos.size() == 0)) {
              origPtpCuid = getPtnDAOX().getPtnPortBandingByTpCuids(origPtpCuid);
              destPtpCuid = getPtnDAOX().getPtnPortBandingByTpCuids(destPtpCuid);
              if ((StringUtils.isNotEmpty(origPtpCuid)) && (StringUtils.isNotEmpty(destPtpCuid))) {
                dbos = getTraphDAOX().getTraphByPhysicalPtp(actionContext, origPtpCuid, destPtpCuid, vlanId);
              }
            }
            if ((dbos != null) && (dbos.size() > 0)) {
              for (int j = 0; j < dbos.size(); j++) {
                Traph traph = (Traph)dbos.getAttrField("TRAPH", j);
                traphList.add(traph);
              }
            }
          }
        }
      }
      LogHome.getLog().info("===== 查询伪线承载电路用时 " + (System.currentTimeMillis() - begin) + " ms =====");
    } catch (Exception e) {
      LogHome.getLog().error("查询伪线承载电路异常", e);
      throw new UserException(e);
    }
    return traphList;
  }

  public DataObjectList getPseudoWireByTunnelFdn(BoActionContext actionContext, String tunnelFdn)
    throws UserException
  {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPseudoWireByTunnelFdn(tunnelFdn);
    } catch (Exception e) {
      LogHome.getLog().error("查询隧道承载伪线异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getTraphByTunnelFdn(BoActionContext actionContext, String tunnelFdn)
    throws UserException
  {
    DataObjectList traphList = new DataObjectList();
    try
    {
      DataObjectList pseudoWireList = getPtnDAOX().getPseudoWireByTunnelFdn(tunnelFdn);
      if ((pseudoWireList != null) && (pseudoWireList.size() > 0))
        for (int i = 0; i < pseudoWireList.size(); i++) {
          PtnVirtualLine ptnVirtualLine = (PtnVirtualLine)pseudoWireList.get(i);
          if (ptnVirtualLine != null) {
            DataObjectList tmpTraphList = getTraphByPseudoWireCuid(actionContext, ptnVirtualLine.getCuid());
            if ((tmpTraphList != null) && (tmpTraphList.size() > 0)) {
              traphList.addAll(tmpTraphList);
            }
            DataObjectList tmpTraphList1 = getTraphByPseudoWireFdn(actionContext, ptnVirtualLine.getFdn());
            if ((tmpTraphList1 != null) && (tmpTraphList1.size() > 0))
              traphList.addAll(tmpTraphList1);
          }
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("查询隧道承载电路异常", e);
      throw new UserException(e);
    }
    return traphList;
  }

  public DboCollection getTunnelByPage(BoQueryContext queryContext, Map paramMap)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getTunnelByPage(queryContext, paramMap);
    } catch (Exception e) {
      LogHome.getLog().error("分页查询隧道异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public PtnTurnnel getPtnTurnnelByCuid(BoActionContext actionContext, String tunnelCuid)
    throws UserException
  {
    PtnTurnnel ptnTurnnel = null;
    try {
      ptnTurnnel = getPtnDAOX().getPtnTurnnelByCuid(tunnelCuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据CUID查询隧道异常", e);
      throw new UserException(e);
    }
    return ptnTurnnel;
  }

  public PtnVirtualLine getPseudoWireByCuid(BoActionContext actionContext, String cuid)
  {
    PtnVirtualLine ptnVirtualLine = null;
    try {
      ptnVirtualLine = getPtnDAOX().getPseudoWireByCuid(cuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据CUID查询伪线异常", e);
      throw new UserException(e);
    }
    return ptnVirtualLine;
  }

  public PtnTurnnel getPtnTurnnelByFdn(BoActionContext actionContext, String tunnelFdn)
    throws UserException
  {
    PtnTurnnel ptnTurnnel = null;
    try {
      ptnTurnnel = getPtnDAOX().getPtnTurnnelByFdn(tunnelFdn);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询隧道异常", e);
      throw new UserException(e);
    }
    return ptnTurnnel;
  }

  public DataObjectList getPtnTurnnelByFdnList(BoActionContext actionContext, String tunnelFdn) {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPtnTurnnelByFdnList(tunnelFdn);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询隧道异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getPtnTurnnelListByName(BoActionContext actionContext, String tunnelName)
    throws UserException
  {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPtnTurnnelsByName(tunnelName);
    } catch (Exception e) {
      LogHome.getLog().error("根据隧道名称查询隧道列表异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DboCollection getPtnIpCrossconnectByTurnnelCuid(BoActionContext actionContext, String turnnelCuid)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getPtnIpCrossconnectByTurnnelCuid(actionContext, turnnelCuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据隧道CUID查询隧道标签交换失败", e);
      throw new UserException(e.getMessage());
    }
    return dbos;
  }

  public DboCollection getPtnIpCrossconnectByTurnnelFdn(BoActionContext actionContext, String turnnelFdn)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      PtnTurnnel ptnTurnnel = getPtnDAOX().getPtnTurnnelByFdn(turnnelFdn);
      if (ptnTurnnel != null)
        dbos = getPtnDAOX().getPtnIpCrossconnectByTurnnelCuid(actionContext, ptnTurnnel.getCuid());
    }
    catch (Exception e) {
      LogHome.getLog().error("根据隧道的FDN查询标签交换异常", e);
      throw new UserException(e.getMessage());
    }
    return dbos;
  }

  public PtnVirtualLine getPseudoWireByFdn(BoActionContext actionContext, String pseudoWireFdn)
    throws UserException
  {
    PtnVirtualLine ptnVirtualLine = null;
    try {
      ptnVirtualLine = getPtnDAOX().getPseudoWireByFdn(pseudoWireFdn);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询伪线异常", e);
      throw new UserException(e);
    }
    return ptnVirtualLine;
  }

  public DataObjectList getPseudoWireListByFdn(BoActionContext actionContext, String pseudoWireFdn)
  {
    DataObjectList ptnVirtualLine = null;
    try {
      ptnVirtualLine = getPtnDAOX().getPseudoWireListByFdn(pseudoWireFdn);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询伪线异常", e);
      throw new UserException(e);
    }

    return null;
  }

  public PtnVirtualLine getPtnVirtualLineByTunnelCuid(BoActionContext actionContext, String cuid)
  {
    PtnVirtualLine ptnVirtualLineList = null;
    try {
      ptnVirtualLineList = getPtnDAOX().getPtnVirtualLineByTunnelCuid(cuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询伪线异常", e);
      throw new UserException(e);
    }
    return ptnVirtualLineList;
  }

  public DataObjectList getPtnVirtualVitaeByFdn(BoActionContext actionContext, String fdn)
    throws UserException
  {
    DataObjectList ptnTdmList = null;
    try {
      ptnTdmList = getPtnDAOX().getPtnVirtualVitaeByFdn(fdn);
    } catch (Exception e) {
      LogHome.getLog().error("根据FDN查询伪线异常", e);
      throw new UserException(e);
    }
    return ptnTdmList;
  }

  public Long getPseudoWireServiceType(BoActionContext actionContext, String pseudoWireFdn)
    throws UserException
  {
    long serviceType = 0L;
    try {
      DataObjectList serviceList = getPtnDAOX().getServiceByPseudoWireFdn(pseudoWireFdn);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        GenericDO dbo = (GenericDO)serviceList.get(0);
        if (dbo.getCuid().startsWith("PTN_ETH"))
          serviceType = 1L;
        else if (dbo.getCuid().startsWith("PTN_TDM"))
          serviceType = 2L;
        else if (dbo.getCuid().startsWith("PTN_ATM"))
          serviceType = 3L;
      }
    }
    catch (Exception e) {
      LogHome.getLog().error("根据伪线FDN查询其业务类型异常", e);
      throw new UserException(e);
    }
    return Long.valueOf(serviceType);
  }

  private PtnDAOX getPtnDAOX() {
    return (PtnDAOX)super.getDAO("PtnDAOX");
  }

  private TraphDAOX getTraphDAOX() {
    return (TraphDAOX)super.getDAO("TraphDAOX");
  }

  public PtnEth getPtnEthByCuid(BoActionContext actionContext, String cuid) throws UserException {
    PtnEth dbo = null;
    try {
      dbo = getPtnDAOX().getPtnEthByCuid(actionContext, cuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据以太网业务CUID查询以太网业务失败", e);
      throw new UserException(e.getMessage());
    }
    return dbo;
  }

  public DataObjectList getServiceByPseudoWireFdnList(BoActionContext actionContext, String fdn, String type) {
    DataObjectList serviceList = null;
    try {
      serviceList = getPtnDAOX().getServiceByPseudoWireFdnList(fdn, type);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return serviceList;
  }

  public PtnTdm getPtnTdmByCuid(BoActionContext actionContext, String cuid)
  {
    PtnTdm dbo = null;
    try {
      dbo = getPtnDAOX().getPtnTdmByCuid(actionContext, cuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据TDM业务CUID查询TDM业务失败", e);
      throw new UserException(e.getMessage());
    }
    return dbo;
  }

  public PtnAtm getPtnAtmByCuid(BoActionContext actionContext, String cuid)
  {
    PtnAtm dbo = null;
    try {
      dbo = getPtnDAOX().getPtnAtmByCuid(actionContext, cuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据ATM业务CUID查询ATM业务失败", e);
      throw new UserException(e.getMessage());
    }
    return dbo;
  }

  public DataObjectList getPtnVirtualLineByEMSAndFdn(BoActionContext actionContext, Map param) throws UserException
  {
    try
    {
      DataObjectList dbo = getPtnDAOX().getPtnVirtualLineByEMSAndFdn(actionContext, param);
      return dbo; } catch (Exception e) {
    }
    throw new UserException(e);
  }

  public DboCollection getPtnServiceByPath(BoQueryContext queryContext, String orderString, String emsCuid, String tunnelFdns, String pseudoWireFdn, GenericDO dbo)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getPtnServiceByPath(queryContext, orderString, emsCuid, tunnelFdns, pseudoWireFdn, dbo);
    } catch (Exception e) {
      LogHome.getLog().error("根据路径查询业务异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public String getVLCuidByPath(BoActionContext actionContext, String ptnPathCuid) throws UserException {
    String pwCuid = "";
    String origPtpCuid = "";
    String destPtpCuid = "";
    String origPtpCuid2 = "";
    String destPtpCuid2 = "";
    try {
      DataObjectList serviceList = null;
      if (ptnPathCuid.startsWith("ATTEMP_PTN_PATH")) {
        AttempPtnPath ptnPath = (AttempPtnPath)getPtnDAOX().getObjByCuid(new AttempPtnPath(ptnPathCuid));
        serviceList = getPtnDAOX().getPtnServiceByPath(ptnPath);
        origPtpCuid = ptnPath.getRelatedAPtpCuid();
        origPtpCuid2 = ptnPath.getRelatedAPtpCuid2();
        destPtpCuid = ptnPath.getRelatedZPtpCuid();
        destPtpCuid2 = ptnPath.getRelatedZPtpCuid2();
      } else if (ptnPathCuid.startsWith("PTN_PATH")) {
        PtnPath ptnPath = (PtnPath)getPtnDAOX().getObjByCuid(new PtnPath(ptnPathCuid));
        serviceList = getPtnDAOX().getPtnServiceByPath(ptnPath);
        origPtpCuid = ptnPath.getRelatedAPtpCuid();
        origPtpCuid2 = ptnPath.getRelatedAPtpCuid2();
        destPtpCuid = ptnPath.getRelatedZPtpCuid();
        destPtpCuid2 = ptnPath.getRelatedZPtpCuid2();
      }
      if ((serviceList != null) && (serviceList.size() > 0)) {
        GenericDO dbo = (GenericDO)serviceList.get(0);
        String pwFdn = "";
        if (dbo.getCuid().startsWith("PTN_ETH")) {
          pwFdn = ((PtnEth)dbo).getPtnVlFdn();
          PtnVirtualLine pw = getPtnDAOX().getPseudoWireByFdn(pwFdn);
          if (pw != null) {
            pwCuid = pw.getCuid();
          } else {
            String EthCuid = dbo.getCuid();
            String sql = "select RELATED_VIRTUAL_LINE_CUID from PTN_TP_PAIR where RELATED_SERVICE_CUID ='" + EthCuid + "'";
            DataObjectList pairs = getPtnDAOX().selectDBOs(sql, new Class[] { String.class });
            if ((pairs != null) && (pairs.size() > 0)) {
              GenericDO gdo = (GenericDO)pairs.get(0);
              if (StringUtils.isNotEmpty(gdo.getAttrString("1")))
                pwCuid = gdo.getAttrString("1");
            }
          }
        }
        else if (dbo.getCuid().startsWith("PTN_TDM")) {
          pwFdn = ((PtnTdm)dbo).getPtnVlFdn();
          PtnVirtualLine pw = getPtnDAOX().getPseudoWireByFdn(pwFdn);
          if (pw != null)
            pwCuid = pw.getCuid();
        }
        else if (dbo.getCuid().startsWith("PTN_ATM")) {
          pwFdn = ((PtnAtm)dbo).getPtnVlFdn();
          PtnVirtualLine pw = getPtnDAOX().getPseudoWireByFdn(pwFdn);
          if (pw != null)
            pwCuid = pw.getCuid();
        }
        else if (dbo.getCuid().startsWith("PTN_TP_PAIR")) {
          pwCuid = ((PtnTpPair)dbo).getRelatedVirtualLineCuid();
        }

      }

    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据PTN路由段查询伪线异常：", e);
      throw new UserException("根据PTN路由段查询伪线异常：" + e.getMessage());
    }
    return pwCuid;
  }

  public DboCollection getPtnServiceByEquipCuid(BoQueryContext queryContext, String orderString, String equipCuid, GenericDO dbo)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getPtnServiceByEquipCuid(queryContext, orderString, equipCuid, dbo);
    } catch (Exception e) {
      LogHome.getLog().error("根据网元查询业务异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DboCollection getPtnServiceByOrdePort(BoQueryContext queryContext, String orderString, String emsCuid, String origCuid, String destCuid, GenericDO dbo)
    throws UserException
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getPtnServiceByOrdePort(queryContext, orderString, emsCuid, origCuid, destCuid, dbo);
    } catch (Exception e) {
      LogHome.getLog().error("根据起止端口查询业务异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public Ptp getPtpByCuid(BoActionContext actionContext, String ptpCuid) throws UserException {
    try {
      Ptp dbo = getPtnDAOX().getPtpByCuid(actionContext, ptpCuid);
      return dbo;
    } catch (Exception e) {
      LogHome.getLog().error("getPtpByCuid方法出错", e);
    }throw new UserException(e);
  }

  public String getPortCuids(BoActionContext actionContext, String portCuids, String port) throws UserException
  {
    try {
      Ptp ptp = getPtpByCuid(actionContext, port);
      if ((ptp.getPortSubType() == 7L) || (ptp.getPortSubType() == 8L)) {
        DataObjectList dbo = getPtnDAOX().getPortBandByPtpCuid(actionContext, port);
        if ((dbo != null) && (dbo.size() > 0))
          for (int j = 0; j < dbo.size(); j++) {
            PtnPortBanding band = (PtnPortBanding)dbo.get(j);
            portCuids = portCuids + "," + band.getSvTpCuid();
          }
      }
      else {
        portCuids = portCuids + "," + port;
      }
    } catch (Exception e) {
      LogHome.getLog().error("getPortCuids方法出错", e);
      throw new UserException(e);
    }
    return portCuids;
  }

  public DataObjectList getPtnServiceByTraph(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList serviceList = new DataObjectList();
    try {
      DboCollection ptnPathCollection = getPtnDAOX().getPtnPathByTraph(actionContext, traphCuid);
      if ((ptnPathCollection != null) && (ptnPathCollection.size() > 0))
        for (int i = 0; i < ptnPathCollection.size(); i++) {
          PtnPath ptnPath = (PtnPath)ptnPathCollection.getAttrField("PTN_PATH", i);
          DataObjectList tmpServiceList = getPtnDAOX().getPtnServiceByPath(ptnPath);
          if ((tmpServiceList != null) && (tmpServiceList.size() > 0))
            serviceList.addAll(tmpServiceList);
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据电路查询业务", e);
      throw new UserException(e);
    }
    return serviceList;
  }

  public DataObjectList getPtnServiceByTraph1(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList serviceList = new DataObjectList();
    try {
      DboCollection ptnPathCollection = getPtnDAOX().getPtnPathByTraph(actionContext, traphCuid);
      if ((ptnPathCollection != null) && (ptnPathCollection.size() > 0))
        for (int i = 0; i < ptnPathCollection.size(); i++) {
          PtnPath ptnPath = null;
          if (traphCuid.startsWith("TRAPH"))
            ptnPath = (PtnPath)ptnPathCollection.getAttrField("PTN_PATH", i);
          else {
            ptnPath = (PtnPath)ptnPathCollection.getAttrField("ATTEMP_PTN_PATH", i);
          }
          String virtualLineCuid = ptnPath.getRelatedVirtualLineCuid();
          PtnVirtualLine liPtnVirtualLine = null;
          if ((virtualLineCuid != null) && (virtualLineCuid.length() > 0)) {
            liPtnVirtualLine = (PtnVirtualLine)getPtnDAOX().getObjByCuid(virtualLineCuid);
          }
          if (liPtnVirtualLine != null)
            serviceList.add(liPtnVirtualLine);
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据电路查询业务", e);
      throw new UserException(e);
    }
    return serviceList;
  }

  public DataObjectList getPtnVirtualLineByTraph(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList pwList = new DataObjectList();
    try {
      DataObjectList serviceList = getPtnServiceByTraph(actionContext, traphCuid);
      if ((serviceList != null) && (serviceList.size() > 0))
        for (int i = 0; i < serviceList.size(); i++) {
          GenericDO dbo = (GenericDO)serviceList.get(i);
          String pwFdn = "";
          if (dbo.getCuid().startsWith("PTN_ETH"))
            pwFdn = ((PtnEth)dbo).getPtnVlFdn();
          else if (dbo.getCuid().startsWith("PTN_TDM"))
            pwFdn = ((PtnTdm)dbo).getPtnVlFdn();
          else if (dbo.getCuid().startsWith("PTN_ATM")) {
            pwFdn = ((PtnAtm)dbo).getPtnVlFdn();
          }
          PtnVirtualLine pw = getPtnDAOX().getPseudoWireByFdn(pwFdn);
          if (pw != null)
            pwList.add(pw);
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据电路查询伪线异常", e);
      throw new UserException(e);
    }
    return pwList;
  }

  public DataObjectList getPtnVirtualLineByTraph1(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList pwList = new DataObjectList();
    try {
      DataObjectList serviceList = getPtnServiceByTraph1(actionContext, traphCuid);
      if ((serviceList != null) && (serviceList.size() > 0))
        for (int i = 0; i < serviceList.size(); i++) {
          GenericDO dbo = (GenericDO)serviceList.get(i);
          PtnVirtualLine pw = null;
          if ((dbo instanceof PtnVirtualLine)) {
            pw = (PtnVirtualLine)dbo;
          }

          if (pw != null)
            pwList.add(pw);
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据电路查询伪线异常", e);
      throw new UserException(e);
    }
    return pwList;
  }

  public DataObjectList getPtnTurnnelByTraph(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList tunnelList = new DataObjectList();
    try {
      DataObjectList pwList = getPtnVirtualLineByTraph(actionContext, traphCuid);
      if ((pwList != null) && (pwList.size() > 0))
        for (int i = 0; i < pwList.size(); i++) {
          PtnVirtualLine pvl = (PtnVirtualLine)pwList.get(i);
          PtnTurnnel tunnel = getPtnDAOX().getPtnTurnnelByFdn(pvl.getRelatedLspFdn());
          if (tunnel != null) {
            DataObjectList routeList = getPtnDAOX().getTunnelRouteByTurnnelCuid(actionContext, tunnel.getCuid());
            tunnel.setAttrValue("routeList", routeList);
            tunnelList.add(tunnel);
          }
        }
    }
    catch (Exception e) {
      LogHome.getLog().error("根据电路查询隧道异常", e);
      throw new UserException(e);
    }
    return tunnelList;
  }

  public DataObjectList getPtnTurnnelByTraph1(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    LogHome.getLog().info("PTNBOX初始化成功@getPtnTurnnelByTraph1-start");
    DataObjectList tunnelList = new DataObjectList();
    try {
      DataObjectList pwList = getPtnVirtualLineByTraph1(actionContext, traphCuid);
      if ((pwList != null) && (pwList.size() > 0)) {
        String pwCuids = "";
        for (int i = 0; i < pwList.size(); i++) {
          PtnVirtualLine pvl = (PtnVirtualLine)pwList.get(i);
          if (StringUtils.isEmpty(pwCuids))
            pwCuids = pvl.getCuid();
          else {
            pwCuids = pwCuids + "," + pvl.getCuid();
          }
        }
        DataObjectList dbos = getPtnDAOX().getTurnnelToVirtualLineByPW(pwCuids);
        if ((dbos != null) && (dbos.size() > 0))
          for (int j = 0; j < dbos.size(); j++) {
            TunnelToVirtualLine ttvl = (TunnelToVirtualLine)dbos.get(j);
            PtnTurnnel tunnel = getPtnDAOX().getPtnTurnnelByCuid(ttvl.getRelatedTunnelCuid());
            if (tunnel != null) {
              if (ttvl.getIsBackup())
                tunnel.setAttrValue("isBackup", "true");
              else {
                tunnel.setAttrValue("isBackup", "false");
              }
              DataObjectList routeList = getPtnDAOX().getTunnelRouteByTurnnelCuid(actionContext, tunnel.getCuid());
              tunnel.setAttrValue("routeList", routeList);
              tunnelList.add(tunnel);
            }
          }
      }
    }
    catch (Exception e) {
      LogHome.getLog().error("根据电路查询隧道异常", e);
      throw new UserException(e);
    }
    LogHome.getLog().info("PTNBOX初始化成功@getPtnTurnnelByTraph1-end");
    return tunnelList;
  }

  public DataObjectList getPtnServicePtpByTraph(BoActionContext actionContext, String traphCuid)
    throws UserException
  {
    DataObjectList ptpList = null;
    try {
      DataObjectList ptnServiceList = getPtnServiceByTraph(actionContext, traphCuid);
      if ((ptnServiceList != null) && (ptnServiceList.size() > 0))
        for (int i = 0; i < ptnServiceList.size(); i++) {
          GenericDO dbo = (GenericDO)ptnServiceList.get(i);
          String ptpCuids = "";
          if (dbo.getCuid().startsWith("PTN_ETH")) {
            PtnEth ptnEth = (PtnEth)dbo;
            if ((StringUtils.isNotEmpty(ptnEth.getOrigPtpCuid())) || (StringUtils.isNotEmpty(ptnEth.getDestPtpCuid()))) {
              if ((StringUtils.isNotEmpty(ptnEth.getOrigPtpCuid())) && (ptpCuids.indexOf(ptnEth.getOrigPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnEth.getOrigPtpCuid();
                else {
                  ptpCuids = ptpCuids + "," + ptnEth.getOrigPtpCuid();
                }
              }
              if ((StringUtils.isNotEmpty(ptnEth.getDestPtpCuid())) && (ptpCuids.indexOf(ptnEth.getDestPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnEth.getDestPtpCuid();
                else
                  ptpCuids = ptpCuids + "," + ptnEth.getDestPtpCuid();
              }
            }
          }
          else if (dbo.getCuid().startsWith("PTN_TDM")) {
            PtnTdm ptnTdm = (PtnTdm)dbo;
            if ((StringUtils.isNotEmpty(ptnTdm.getOrigPtpCuid())) || (StringUtils.isNotEmpty(ptnTdm.getDestPtpCuid()))) {
              if ((StringUtils.isNotEmpty(ptnTdm.getOrigPtpCuid())) && (ptpCuids.indexOf(ptnTdm.getOrigPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnTdm.getOrigPtpCuid();
                else {
                  ptpCuids = ptpCuids + "," + ptnTdm.getOrigPtpCuid();
                }
              }
              if ((StringUtils.isNotEmpty(ptnTdm.getDestPtpCuid())) && (ptpCuids.indexOf(ptnTdm.getDestPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnTdm.getDestPtpCuid();
                else
                  ptpCuids = ptpCuids + "," + ptnTdm.getDestPtpCuid();
              }
            }
          }
          else if (dbo.getCuid().startsWith("PTN_ATM")) {
            PtnAtm ptnAtm = (PtnAtm)dbo;
            if ((StringUtils.isNotEmpty(ptnAtm.getOrigPtpCuid())) || (StringUtils.isNotEmpty(ptnAtm.getDestPtpCuid()))) {
              if ((StringUtils.isNotEmpty(ptnAtm.getOrigPtpCuid())) && (ptpCuids.indexOf(ptnAtm.getOrigPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnAtm.getOrigPtpCuid();
                else {
                  ptpCuids = ptpCuids + "," + ptnAtm.getOrigPtpCuid();
                }
              }
              if ((StringUtils.isNotEmpty(ptnAtm.getDestPtpCuid())) && (ptpCuids.indexOf(ptnAtm.getDestPtpCuid()) == -1)) {
                if (StringUtils.isEmpty(ptpCuids))
                  ptpCuids = ptnAtm.getDestPtpCuid();
                else {
                  ptpCuids = ptpCuids + "," + ptnAtm.getDestPtpCuid();
                }
              }
            }
          }
          if (!StringUtils.isNotEmpty(ptpCuids)) continue;
          try {
            ptpList = new DataObjectList();
            DboCollection dbos = getPtnDAOX().getPTPByCuids(new BoQueryContext(), ptpCuids);
            if ((dbos != null) && (dbos.size() > 0)) {
              for (int j = 0; j < dbos.size(); j++) {
                Ptp ptp = (Ptp)dbos.getAttrField("PTP", j);
                ptpList.add(ptp);
              }
            }
            DataObjectList portBandingList = getPtnDAOX().getPtnPortBandingByPtpCuids(ptpCuids);
            if ((portBandingList != null) && (portBandingList.size() > 0))
              for (int j = 0; j < portBandingList.size(); j++) {
                PtnPortBanding ptnPortBanding = (PtnPortBanding)portBandingList.get(j);
                if ((ptpCuids.indexOf(ptnPortBanding.getPhysicalTpCuid()) == -1) && (StringUtils.isNotEmpty(ptnPortBanding.getPhysicalTpCuid()))) {
                  Ptp ptp = (Ptp)getPtnDAOX().getObjByCuid(ptnPortBanding.getPhysicalTpCuid());
                  if (ptp != null) {
                    ptpList.add(ptp);
                  }
                }
                if ((ptpCuids.indexOf(ptnPortBanding.getSvTpCuid()) == -1) && (StringUtils.isNotEmpty(ptnPortBanding.getSvTpCuid()))) {
                  Ptp ptp = (Ptp)getPtnDAOX().getObjByCuid(ptnPortBanding.getSvTpCuid());
                  if (ptp != null)
                    ptpList.add(ptp);
                }
              }
          }
          catch (Exception ex)
          {
            LogHome.getLog().error("根据端口" + ptpCuids + "查询PTN业务的端口绑定异常", ex);
          }
        }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据电路的CUID查询PTN业务经过的端口异常", e);
      throw new UserException(e);
    }
    return ptpList;
  }

  public Map<String, String> getLayerRateMap(BoActionContext actionContext)
    throws UserException
  {
    Map layerRateMap = new HashMap();
    try {
      DboCollection dbos = getPtnDAOX().getLayerRateMap();
      if ((dbos != null) && (dbos.size() > 0))
        for (int i = 0; i < dbos.size(); i++) {
          LayerRate layerRate = (LayerRate)dbos.getAttrField("LAYER_RATE", i);
          layerRateMap.put("" + layerRate.getEnumValue(), layerRate.getEnumName());
        }
    }
    catch (Exception e) {
      LogHome.getLog().error("查询层速率异常", e);
      throw new UserException(e);
    }
    return layerRateMap;
  }

  public void initBO() throws Exception {
    super.initBO();
    LogHome.getLog().info("PTNBOX初始化成功@");
  }

  public DataObjectList getAllBackBone(BoQueryContext queryContext) throws UserException {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getAllBackBone(queryContext);
    } catch (Exception e) {
      LogHome.getLog().error("获得骨干汇聚点失败", e);
    }
    return dbos;
  }

  public BackboneCollect getBackBoneByNetAndNe(BoActionContext actionContext, String subNetCuid, String elementCuid) throws UserException {
    BackboneCollect dbo = null;
    try {
      dbo = getPtnDAOX().getBackBoneByNetAndNe(actionContext, subNetCuid, elementCuid);
    } catch (Exception e) {
      LogHome.getLog().error("通过子网获得骨干汇聚点失败", e);
    }
    return dbo;
  }

  public DataObjectList getBackBoneByPage(BoQueryContext queryContext, String orderString, String relatedEMSCuids, String queryRelatedNetCuid, String labelCn, Long boneProperty) throws UserException {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getBackBoneByPage(queryContext, orderString, relatedEMSCuids, queryRelatedNetCuid, labelCn, boneProperty);
    } catch (Exception e) {
      LogHome.getLog().error("分页查询骨干汇聚点异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getBackBoneBySubNet(BoQueryContext queryContext, String netCuid) throws Exception {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getBackBoneBySubNet(queryContext, netCuid);
    } catch (Exception e) {
      LogHome.getLog().error("通过子网查询骨干汇聚点异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public void delBackBone(BoActionContext actionContext, Long objectId) throws UserException {
    try {
      getPtnDAOX().delBackBone(actionContext, objectId);
    } catch (Exception e) {
      LogHome.getLog().error("删除骨干汇聚点异常", e);
      throw new UserException(e);
    }
  }

  public DboCollection getPtnTunnelByVLCuid(BoActionContext actionContext, String vlCuid) throws UserException {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getPtnTunnelByVLCuid(vlCuid);
    } catch (Exception e) {
      LogHome.getLog().error("删除骨干汇聚点异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getPtnTunnelByVLCuidList(BoActionContext actionContext, String vlCuid) {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPtnTunnelByVLCuidList(vlCuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据伪线CUID查询隧道异常", e);
      throw new UserException(e);
    }
    return dbos;
  }

  public String getLspRouteDesc(BoActionContext actionContext, String lspCuid)
    throws UserException
  {
    String lspRoute = "";
    try {
      Map ptpMap = new HashMap();
      String sql = "select P.CUID, P.LABEL_CN from PTP P, PTN_IP_CROSSCONNECT PIC where PIC.ORIG_PTP_CUID=P.CUID and PIC.RELATED_PATH_CUID='" + lspCuid + "'";

      sql = sql + "UNION select P." + "CUID" + ", P." + "LABEL_CN" + " from " + "PTP" + " P, " + "PTN_IP_CROSSCONNECT" + " PIC where  PIC." + "DEST_PTP_CUID" + "=P." + "CUID" + " and PIC." + "RELATED_PATH_CUID" + "='" + lspCuid + "'";

      DboCollection dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new Ptp() });
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          Ptp ptp = (Ptp)dbos.getAttrField("PTP", i);
          ptpMap.put(ptp.getCuid(), ptp.getLabelCn());
        }
      }
      sql = "select ORIG_PTP_CUID, DEST_PTP_CUID from PTN_IP_CROSSCONNECT where RELATED_PATH_CUID='" + lspCuid + "' order by " + "BELONG_PATH" + "," + "ORDINAL_IDENTIFIER";

      dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new PtnIpCrossconnect() });
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          PtnIpCrossconnect connect = (PtnIpCrossconnect)dbos.getAttrField("PTN_IP_CROSSCONNECT", i);
          if (ptpMap.get(connect.getOrigPtpCuid()) != null) {
            if (StringUtils.isEmpty(lspRoute))
              lspRoute = (String)ptpMap.get(connect.getOrigPtpCuid());
            else {
              lspRoute = lspRoute + "-->" + (String)ptpMap.get(connect.getOrigPtpCuid());
            }
          }
          if (ptpMap.get(connect.getDestPtpCuid()) != null)
            if (StringUtils.isEmpty(lspRoute))
              lspRoute = (String)ptpMap.get(connect.getDestPtpCuid());
            else
              lspRoute = lspRoute + "-->" + (String)ptpMap.get(connect.getDestPtpCuid());
        }
      }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("根据标签交换生成隧道的路由描述异常", e);
      throw new UserException(e);
    }
    return lspRoute;
  }

  public TunnelGraphicView getTunnelGraphSegListByLineList(List<PtnVirtualLine> ptnVirLists)
  {
    TunnelGraphicView graphicView = null;
    if (ptnVirLists != null) {
      graphicView = new TunnelGraphicView();
      HashMap objMap = getNeModelMap();
      for (int i = 0; i < ptnVirLists.size(); i++) {
        PtnVirtualLine virLine = (PtnVirtualLine)ptnVirLists.get(i);
        TunnelGraphicSeg graphicSeg = new TunnelGraphicSeg();

        PtnTurnnel mainTurnnel = (PtnTurnnel)virLine.getAttrValue("MAIN_TRUNNEL");
        PtnTurnnel mainTurnnel2 = (PtnTurnnel)virLine.getAttrValue("MAIN_TRUNNEL2");
        PtnTurnnel backTurnnel = (PtnTurnnel)virLine.getAttrValue("BACK_TRUNNEL");

        ArrayList mainNodeList = getTunnelSegNodeList(mainTurnnel, 1, objMap);
        ArrayList mainNode2List = getTunnelSegNodeList(mainTurnnel2, 2, objMap);
        ArrayList backNodeList = getTunnelSegNodeList(backTurnnel, 3, objMap);
        graphicSeg.addMainListNode(mainNodeList);
        graphicSeg.addMain2ListNode(mainNode2List);
        graphicSeg.addBackListNode(backNodeList);

        ArrayList lineList = getTunnelSegLineList(mainTurnnel, 1);
        graphicSeg.addLines(lineList);
        lineList = getTunnelSegLineList(mainTurnnel2, 2);
        graphicSeg.addLines(lineList);
        lineList = getTunnelSegLineList(backTurnnel, 3);
        graphicSeg.addLines(lineList);

        graphicSeg.setANeKey(virLine.getOrigNeCuid());
        graphicSeg.setAPortName(virLine.getAttrString("ORIG_TPNAME"));
        graphicSeg.setZNeKey(virLine.getDestNeCuid());
        graphicSeg.setZPortName(virLine.getAttrString("DEST_TPNAME"));

        graphicView.addSegList(graphicSeg);
      }
    }
    return graphicView;
  }
  public HashMap getNeModelMap() {
    HashMap rtnMap = new HashMap();
    try {
      String sql = "SELECT PRODUCT_MODEL,CUID FROM NE_MODEL_CFG_TYPE";

      DboCollection dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new NeModelCfgType() });
      if (dbos != null)
        for (int i = 0; i < dbos.size(); i++) {
          NeModelCfgType cfgType = (NeModelCfgType)dbos.getAttrField("NE_MODEL_CFG_TYPE", i);
          rtnMap.put(cfgType.getCuid(), cfgType.getProductModel());
        }
    }
    catch (Exception ex) {
      LogHome.getLog().info("出错", ex);
    }
    return rtnMap;
  }
  private ArrayList getTunnelSegLineList(PtnTurnnel tunnel, int type) {
    ArrayList arrayLineList = new ArrayList();
    if (tunnel != null) {
      ArrayList routeList = (ArrayList)tunnel.getAttrValue("PTN_IP_CROSSCONNECT");
      if (routeList != null) {
        for (int i = 0; i < routeList.size() - 1; i++) {
          PtnIpCrossconnect ptnIpOrig = (PtnIpCrossconnect)routeList.get(i);
          PtnIpCrossconnect ptnIpDest = (PtnIpCrossconnect)routeList.get(i + 1);
          if (!ptnIpOrig.getRelatedNeCuid().equals(ptnIpDest.getRelatedNeCuid())) {
            TunnelGraphicSegLine segLine = new TunnelGraphicSegLine();
            segLine.setAKeyId(ptnIpOrig.getRelatedNeCuid());
            segLine.setZKeyId(ptnIpDest.getRelatedNeCuid());
            String origTpName = (String)ptnIpOrig.getAttrValue("DEST_TPNAME");
            segLine.setAPortName(origTpName);
            String destTpName = (String)ptnIpDest.getAttrValue("ORIG_TPNAME");
            segLine.setZPortName(destTpName);
            segLine.setType(type);
            arrayLineList.add(segLine);
          }
        }
      }
    }
    return arrayLineList;
  }
  private ArrayList getTunnelSegNodeList(PtnTurnnel tunnel, int type, HashMap cfgMap) {
    ArrayList arrayNodeList = new ArrayList();
    if (tunnel != null) {
      ArrayList routeList = (ArrayList)tunnel.getAttrValue("PTN_IP_CROSSCONNECT");
      if (routeList != null) {
        for (int i = 0; i < routeList.size(); i++) {
          PtnIpCrossconnect ipCorssconnect = (PtnIpCrossconnect)routeList.get(i);
          TunnelGraphicSegNode segNode = new TunnelGraphicSegNode();
          segNode.setNodeKey(ipCorssconnect.getRelatedNeCuid());
          segNode.setNodeName(ipCorssconnect.getAttrString("PICNENAME"));
          String neModelCuid = (String)ipCorssconnect.getAttrValue("neModel");
          if (neModelCuid != null) {
            String cfgName = (String)cfgMap.get(neModelCuid);
            if (cfgName != null) {
              segNode.setNeModel(cfgName);
            }
          }
          arrayNodeList.add(segNode);
        }
      }
    }

    return arrayNodeList;
  }

  public TunnelInfo getTunnelInfoByVlCuid(BoActionContext actionContext, TunnelInfo tunnelInfo, String vlCuid)
    throws UserException
  {
    if (tunnelInfo == null) {
      tunnelInfo = new TunnelInfo();
    }
    if (StringUtils.isNotEmpty(vlCuid)) {
      try {
        PtnVirtualLine vl = (PtnVirtualLine)getTraphDAOX().getObjByCuid(new PtnVirtualLine(vlCuid));
        if (vl != null) {
          List pweList = new ArrayList();
          pweList.add(vl);
          tunnelInfo.setPweList(pweList);
          String sql = "select PT.*, TTVL.* from TUNNEL_TO_VIRTUAL_LINE TTVL, PTN_TURNNEL PT where TTVL.RELATED_TUNNEL_CUID=PT.CUID and TTVL.RELATED_VIRTUAL_LINE_CUID='" + vlCuid + "'";

          DboCollection dbos = getTraphDAOX().selectDBOs(sql, new GenericDO[] { new PtnTurnnel(), new TunnelToVirtualLine() });
          if ((dbos != null) && (dbos.size() > 0))
            for (int i = 0; i < dbos.size(); i++) {
              PtnTurnnel tunnel = (PtnTurnnel)dbos.getAttrField("PTN_TURNNEL", i);
              String routeDesc = getLspRouteDesc(actionContext, tunnel.getCuid());
              tunnel.setAttrValue("routeDesc", routeDesc);
              TunnelToVirtualLine ttvl = (TunnelToVirtualLine)dbos.getAttrField("TUNNEL_TO_VIRTUAL_LINE", i);
              if (!ttvl.getIsBackup())
                tunnelInfo.setMainTunnel(tunnel);
              else
                tunnelInfo.setBackupTunnel(tunnel);
            }
        }
      }
      catch (Exception e)
      {
        LogHome.getLog().error("根据伪线CUID获取隧道信息出错", e);
        throw new UserException("根据伪线CUID获取隧道信息出错：" + e.getMessage());
      }
    }
    return tunnelInfo;
  }

  public HashMap getChangePtnVL(BoActionContext actionContext, String traphCuids) throws Exception {
    HashMap rtnMap = new HashMap();
    Map erroMap = new HashMap();
    rtnMap.put("flag", "1");
    ArrayList rtnList = new ArrayList();
    ArrayList errList = new ArrayList();
    if (StringUtils.isNotEmpty(traphCuids)) {
      DboCollection dbos = null;
      try {
        if (traphCuids.startsWith("ATTEMP_TRAPH")) {
          String sql = "select APP.* from ATTEMP_TRAPH_ROUTE ATR, ATTEMP_PTN_PATH APP where ATR.RELATED_SERVICE_CUID=APP.RELATED_ROUTE_CUID and (APP.RELATED_VIRTUAL_LINE_CUID is null or APP.RELATED_VIRTUAL_LINE_CUID='') and ATR.RELATED_SERVICE_CUID in ('" + traphCuids.replace(",", "','") + "')";

          dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new AttempPtnPath() });
          if ((dbos != null) && (dbos.size() > 0)) {
            for (int i = 0; i < dbos.size(); i++) {
              AttempPtnPath ptnPath = (AttempPtnPath)dbos.getAttrField("ATTEMP_PTN_PATH", i);
              String vlCuid = getVLCuidByPath(new BoActionContext(), ptnPath.getCuid());
              if ((StringUtils.isNotEmpty(vlCuid)) && (!vlCuid.equals(ptnPath.getRelatedVirtualLineCuid()))) {
                if (StringUtils.isNotEmpty(ptnPath.getRelatedVirtualLineCuid()))
                {
                  deletePtnVirtualLine(actionContext, ptnPath.getRelatedVirtualLineCuid());
                }
                ptnPath.setRelatedVirtualLineCuid(vlCuid);
                getPtnDAOX().updateObject(new BoActionContext(), ptnPath);
                LogHome.getLog().info("替换【" + ptnPath.getCuid() + "】的相关伪线");
                PtnVirtualLine pvl = (PtnVirtualLine)getPtnDAOX().getObjByCuid(vlCuid);
                if (pvl != null) {
                  if ((ptnPath.getQosBand() != null) && (ptnPath.getQosBand().trim().length() > 0)) {
                    pvl.setQos(ptnPath.getQosBand());
                  }
                  if ((ptnPath.getCirBand() != null) && (ptnPath.getCirBand().trim().length() > 0)) {
                    pvl.setCir(ptnPath.getCirBand());
                  }
                  if ((ptnPath.getPirBand() != null) && (ptnPath.getPirBand().trim().length() > 0)) {
                    pvl.setPir(ptnPath.getPirBand());
                  }
                  getPtnDAOX().updateObject(actionContext, pvl);

                  setPtnServiceStateByVlCuid(ptnPath.getCuid(), pvl.getCuid(), 3L);
                  LogHome.getLog().info("更新伪线【" + pvl.getUserLabel() + "】相关的PTN业务状态为预占");
                }
              } else {
                errList.add(ptnPath.getRelatedRouteCuid());
                LogHome.getLog().info("没有找到【" + ptnPath.getRelatedRouteCuid() + "】的相关伪线");
              }
            }
          } else {
            rtnMap.put("flag", "0");
            rtnList.add("没有找到需要替换隧道的路由段");
          }
        } else if (traphCuids.startsWith("TRAPH")) {
          String sql = "select APP.* from TRAPH_ROUTE ATR, PTN_PATH APP where ATR.RELATED_SERVICE_CUID=APP.RELATED_ROUTE_CUID and (APP.RELATED_VIRTUAL_LINE_CUID is null or APP.RELATED_VIRTUAL_LINE_CUID='') and ATR.RELATED_SERVICE_CUID in ('" + traphCuids.replace(",", "','") + "')";

          dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new PtnPath() });
          long beginTime = System.currentTimeMillis();
          int maxSize = 15;
          if ((null != dbos) && (dbos.size() > maxSize)) {
            LogHome.getLog().info("待关联的PTN电路总数" + dbos.size() + "大于" + maxSize + ",进入批量隧道关联接口");
            UserTransaction trx = TransactionFactory.getInstance().createTransaction();
            try {
              trx.begin();
              int dbosSize = dbos.size();
              Map<String, PtnPath> ptnMap = new HashMap<String, PtnPath>();//key值为ptnpath的cuid,value值为所对应的ptn对象
              DataObjectList updatePtnDbos = new DataObjectList();
              String ptnPathCuids = "''";
              String ptnAndVlCuids = "''";
              for (int i = 0; i < dbosSize; i++) {
                PtnPath ptnPath = (PtnPath)dbos.getAttrField("PTN_PATH", i);
                ptnPathCuids = ptnPathCuids + ",'" + ptnPath.getCuid() + "'";
                ptnMap.put(ptnPath.getCuid(), ptnPath);
              }
              Map ptnVlMap1 = getVLCuidsByPaths(actionContext, ptnPathCuids, 1);
              String ptnPathCuids2 = "''";
              for (Entry<String, PtnPath> entry  : ptnMap.entrySet()) {
                String key = (String)entry.getKey();
                PtnPath ptnPath = (PtnPath)ptnMap.get(key);
                if (ptnVlMap1.containsKey(key)) {
                  ptnPath.setRelatedVirtualLineCuid((String)ptnVlMap1.get(key));
                  LogHome.getLog().info("查询1准备替换【" + ptnPath.getCuid() + "】的相关伪线");
                  updatePtnDbos.add(ptnPath);
                  ptnAndVlCuids = ptnAndVlCuids + ",'" + ptnPath.getCuid() + "'";
                }
                else if ((DaoHelper.isNotEmpty(ptnPath.getRelatedAPtpCuid2())) && (DaoHelper.isNotEmpty(ptnPath.getRelatedZPtpCuid2())))
                {
                  ptnPathCuids2 = ptnPathCuids2 + ",'" + ptnPath.getCuid() + "'";
                }
              }
              Map<String, String>  ptnVlMap2;
              if (!ptnPathCuids2.equals("''")) {
                ptnVlMap2 = getVLCuidsByPaths(actionContext, ptnPathCuids2, 2);
                for (Map.Entry entry : ptnVlMap2.entrySet()) {
                  PtnPath ptnPath2 = (PtnPath)ptnMap.get(entry.getKey());
                  ptnPath2.setRelatedVirtualLineCuid((String)ptnVlMap2.get(ptnPath2.getCuid()));
                  LogHome.getLog().info("查询2准备替换【" + ptnPath2.getCuid() + "】的相关伪线");
                  updatePtnDbos.add(ptnPath2);
                  ptnAndVlCuids = ptnAndVlCuids + ",'" + ptnPath2.getCuid() + "'";
                }
              }
              if (updatePtnDbos.size() > 0) {
                getPtnDAOX().updateObjects(actionContext, updatePtnDbos, null, false);
                LogHome.getLog().info("批量更新PTN段伪线信息入库");
              }

              if (!ptnAndVlCuids.equals("''")) {
                String ttvlSql = "select P.CUID,TTVL.*  from PTN_VIRTUAL_LINE TTVL, PTN_PATH P  where P.RELATED_VIRTUAL_LINE_CUID = TTVL.CUID and P.CUID in (" + ptnAndVlCuids + ")";

                DboCollection ttvlDbos = getPtnDAOX().selectDBOs(ttvlSql, new GenericDO[] { new PtnPath(), new PtnVirtualLine() });
                DataObjectList updateTTVL = new DataObjectList();
                for (int i = 0; i < ttvlDbos.size(); i++) {
                  boolean needUpdateTVL = false;
                  PtnPath path1 = (PtnPath)ttvlDbos.getAttrField("PTN_PATH", i);
                  PtnVirtualLine toLine = (PtnVirtualLine)ttvlDbos.getAttrField("PTN_VIRTUAL_LINE", i);
                  PtnPath ptnPath = (PtnPath)ptnMap.get(path1.getCuid());
                  if ((ptnPath.getQosBand() != null) && (ptnPath.getQosBand().trim().length() > 0)) {
                    toLine.setQos(ptnPath.getQosBand());
                    needUpdateTVL = true;
                  }
                  if ((ptnPath.getCirBand() != null) && (ptnPath.getCirBand().trim().length() > 0)) {
                    toLine.setCir(ptnPath.getCirBand());
                    needUpdateTVL = true;
                  }
                  if ((ptnPath.getPirBand() != null) && (ptnPath.getPirBand().trim().length() > 0)) {
                    toLine.setPir(ptnPath.getPirBand());
                    needUpdateTVL = true;
                  }
                  if (needUpdateTVL) {
                    updateTTVL.add(toLine);
                  }

                  setPtnServiceStateByVlCuid(ptnPath.getCuid(), toLine.getCuid(), 2L);
                }
                if (updateTTVL.size() > 0) {
                  getPtnDAOX().updateObjects(new BoActionContext(), updateTTVL, null, false);
                  LogHome.getLog().info("批量修改隧道伪线的关联关系入库");
                }
              }
              for (Map.Entry entry : ptnMap.entrySet()) {
                PtnPath ptnPath = (PtnPath)entry.getValue();
                if (!DaoHelper.isNotEmpty(ptnPath.getRelatedVirtualLineCuid())) {
                  errList.add(ptnPath.getRelatedRouteCuid());
                  LogHome.getLog().info("没有找到【" + ptnPath.getRelatedRouteCuid() + "】的相关伪线");
                }
              }
              trx.commit();
            } catch (Exception e) {
              trx.rollback();
              rtnMap.put("flag", "0");
              rtnList.add("批量PTN隧道关联失败:" + e.getMessage());
              LogHome.getLog().error("批量PTN隧道关联失败:" + e.getMessage(), e);
              throw new UserException(e);
            }
          }
          if ((dbos != null) && (dbos.size() <= maxSize)) {
            LogHome.getLog().info("待关联的PTN电路总数" + dbos.size() + "小于等于" + maxSize + ",进入单条隧道关联接口");
            for (int i = 0; i < dbos.size(); i++) {
              PtnPath ptnPath = (PtnPath)dbos.getAttrField("PTN_PATH", i);
              String vlCuid = getVLCuidByPath(new BoActionContext(), ptnPath.getCuid());
              if (StringUtils.isNotEmpty(vlCuid)) {
                ptnPath.setRelatedVirtualLineCuid(vlCuid);
                getPtnDAOX().updateObject(new BoActionContext(), ptnPath);
                LogHome.getLog().info("替换【" + ptnPath.getCuid() + "】的相关伪线");
                PtnVirtualLine pvl = (PtnVirtualLine)getPtnDAOX().getObjByCuid(vlCuid);
                if (pvl != null) {
                  if ((ptnPath.getQosBand() != null) && (ptnPath.getQosBand().trim().length() > 0)) {
                    pvl.setQos(ptnPath.getQosBand());
                  }
                  if ((ptnPath.getCirBand() != null) && (ptnPath.getCirBand().trim().length() > 0)) {
                    pvl.setCir(ptnPath.getCirBand());
                  }
                  if ((ptnPath.getPirBand() != null) && (ptnPath.getPirBand().trim().length() > 0)) {
                    pvl.setPir(ptnPath.getPirBand());
                  }
                  getPtnDAOX().updateObject(new BoActionContext(), pvl);
                }

                LogHome.getLog().info("更新伪线【" + pvl.getFdn() + "】相关的PTN业务状态为占用");
                setPtnServiceStateByVlCuid(ptnPath.getCuid(), pvl.getCuid(), 2L);
              } else {
                errList.add(ptnPath.getRelatedRouteCuid());
                LogHome.getLog().info("没有找到【" + ptnPath.getRelatedRouteCuid() + "】的相关伪线");
              }

            }

          }

          long endTime = System.currentTimeMillis();
          int ptnSize = null != dbos ? dbos.size() : 0;
          LogHome.getLog().info(ptnSize + "条PTN电路隧道关联用时:" + (endTime - beginTime) + "毫秒");
        }
      } catch (Exception e) {
        LogHome.getLog().info("替换路由段中空的伪线出错：" + e.getMessage(), e);
      }
      String[] traphCuidArr = traphCuids.split(",");
      if (traphCuidArr.length == 1) {
        if (errList.size() > 0) {
          rtnMap.put("flag", "0");
          String attTraphCuid = (String)errList.get(0);
          if (attTraphCuid.startsWith("ATTEMP_TRAPH")) {
            String sql = "SELECT LABEL_CN FROM ATTEMP_TRAPH WHERE CUID in ('" + attTraphCuid + "')";

            DboCollection dboc = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new AttempTraph() });
            if (dboc != null) {
              AttempTraph attTraph = (AttempTraph)dboc.getAttrField("ATTEMP_TRAPH", 0);
              rtnList.add("隧道关联失败,失败原因：没有找到电路【" + attTraph.getLabelCn() + "】关联的隧道.");
              erroMap.put(attTraphCuid, "没有找到关联隧道");
            }
          } else if (attTraphCuid.startsWith("TRAPH")) {
            String sql = "SELECT LABEL_CN FROM TRAPH WHERE CUID in ('" + attTraphCuid + "')";

            DboCollection dboc = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new Traph() });
            if (dboc != null) {
              Traph traph = (Traph)dboc.getAttrField("TRAPH", 0);
              rtnList.add("隧道关联失败,失败原因：没有找到电路【" + traph.getLabelCn() + "】关联的隧道.");
              erroMap.put(attTraphCuid, "没有找到关联隧道");
            }
          }
        }
        else if ((null == dbos) || (dbos.size() == 0)) {
          rtnMap.put("flag", "0");
          rtnList.add("隧道关联失败,失败原因：没有找到需要替换隧道的路由段");
          erroMap.put(traphCuidArr[0], "没有找到需要替换隧道的路由段");
        } else {
          rtnList.add("隧道关联成功!");
        }
      }
      else if (traphCuidArr.length > 1) {
        if ((errList.size() > 0) && (errList.size() <= traphCuidArr.length)) {
          rtnMap.put("flag", "0");
          for (int i = 0; i < errList.size(); i++) {
            String attTraphCuid = (String)errList.get(i);
            int successNum = traphCuidArr.length - errList.size();
            rtnList.add("所选电路【" + traphCuidArr.length + "】条,关联成功【" + successNum + "】条，" + "关联失败【" + errList.size() + "】条。隧道关联失败原因请点击[查看隧道关联结果]按钮。");

            erroMap.put(attTraphCuid, "没有找到关联的隧道");
          }
        }
        else if ((null == dbos) || (dbos.size() == 0)) {
          rtnMap.put("flag", "0");
          rtnList.add("所选电路【" + traphCuidArr.length + "】条,关联成功【" + 0 + "】条，" + "关联失败【" + traphCuidArr.length + "】条。隧道关联失败原因请点击[查看隧道关联结果]按钮。");

          for (int i = 0; i < traphCuidArr.length; i++) {
            String traphcuid = traphCuidArr[i];
            erroMap.put(traphcuid, "没有找到需要替换隧道的路由段");
          }
        } else {
          rtnList.add("所选电路【" + traphCuidArr.length + "】条,关联成功!");
        }
      }
    }

    rtnMap.put("info", rtnList);
    rtnMap.put("sessionError", erroMap);
    return rtnMap;
  }

  public DataObjectList getTunnelToVirtualLine(String lineCuid) throws UserException
  {
    DataObjectList dbos = null;
    try {
      String sql = "RELATED_VIRTUAL_LINE_CUID='" + lineCuid + "'";
      dbos = getPtnDAOX().getObjectsBySql(sql, new TunnelToVirtualLine(), 0);
    } catch (Exception e) {
      LogHome.getLog().error("获取隧道伪线关系失败:" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dbos;
  }

  public Boolean modifyRelatedSpace(BoActionContext actionContext, Room srcRoom, Room desRoom, DataObjectList dbos) throws UserException
  {
    Boolean ansResult = Boolean.valueOf(false);
    if ((dbos != null) && (dbos.size() > 0)) {
      try {
        for (int i = 0; i < dbos.size(); i++) {
          TransElement me = (TransElement)dbos.get(i);
          String sqls = "SELECT * FROM PTN_PATH WHERE RELATED_A_NE_CUID ='" + me.getCuid() + "' " + " OR " + "RELATED_Z_NE_CUID" + " ='" + me.getCuid() + "'";

          DboCollection ptnPathList = getPtnDAOX().selectDBOs(new BoQueryContext(), sqls, new GenericDO[] { new PtnPath() });
          if ((ptnPathList != null) && (ptnPathList.size() > 0))
            for (int j = 0; j < ptnPathList.size(); j++) {
              PtnPath path = (PtnPath)ptnPathList.getAttrField("PTN_PATH", j);
              String oldRouteDesciption = path.getRouteDesciption();
              if ((DaoHelper.isNotEmpty(path.getRelatedANeCuid())) && (me.getCuid().equals(path.getRelatedANeCuid()))) {
                if (-1 != path.getOrigPointCuid().indexOf("SITE"))
                {
                  Site origOldSite = (Site)getPtnDAOX().getObjByCuid(actionContext, path.getOrigPointCuid());
                  path.setOrigPointCuid(desRoom.getRelatedSiteCuid());
                  Site origNewSite = (Site)getPtnDAOX().getObjByCuid(actionContext, desRoom.getRelatedSiteCuid());
                  if (-1 != oldRouteDesciption.indexOf(origOldSite.getLabelCn()))
                  {
                    oldRouteDesciption = oldRouteDesciption.replaceAll(origOldSite.getLabelCn(), origNewSite.getLabelCn());
                  }
                }
                if (-1 != path.getOrigPointCuid().indexOf("ROOM"))
                {
                  Room origOldRoom = (Room)getPtnDAOX().getObjByCuid(actionContext, path.getOrigPointCuid());
                  path.setOrigPointCuid(desRoom.getCuid());
                  Room origNewRoom = (Room)getPtnDAOX().getObjByCuid(actionContext, desRoom.getCuid());
                  if (-1 != oldRouteDesciption.indexOf(origOldRoom.getLabelCn()))
                  {
                    oldRouteDesciption = oldRouteDesciption.replaceAll(origOldRoom.getLabelCn(), origNewRoom.getLabelCn());
                  }
                }
              }
              if ((DaoHelper.isNotEmpty(path.getRelatedZNeCuid())) && (me.getCuid().equals(path.getRelatedZNeCuid()))) {
                if (-1 != path.getDestPointCuid().indexOf("SITE"))
                {
                  Site destOldSite = (Site)getPtnDAOX().getObjByCuid(actionContext, path.getDestPointCuid());
                  path.setDestPointCuid(desRoom.getRelatedSiteCuid());
                  Site destNewSite = (Site)getPtnDAOX().getObjByCuid(actionContext, desRoom.getRelatedSiteCuid());
                  if (-1 != oldRouteDesciption.indexOf(destOldSite.getLabelCn()))
                  {
                    oldRouteDesciption = oldRouteDesciption.replaceAll(destOldSite.getLabelCn(), destNewSite.getLabelCn());
                  }
                }
                if (-1 != path.getDestPointCuid().indexOf("ROOM"))
                {
                  Room destOldRoom = (Room)getPtnDAOX().getObjByCuid(actionContext, path.getDestPointCuid());
                  path.setDestPointCuid(desRoom.getCuid());
                  Room destNewRoom = (Room)getPtnDAOX().getObjByCuid(actionContext, desRoom.getCuid());
                  if (-1 != oldRouteDesciption.indexOf(destOldRoom.getLabelCn()))
                  {
                    oldRouteDesciption = oldRouteDesciption.replaceAll(destOldRoom.getLabelCn(), destNewRoom.getLabelCn());
                  }
                }
              }
              if (!oldRouteDesciption.equals(path.getRouteDesciption()))
              {
                path.setRouteDesciption(oldRouteDesciption);
              }
              getPtnDAOX().updateObject(actionContext, path);
            }
        }
      }
      catch (Exception e) {
        LogHome.getLog().error("更新PTN段站点机房失败:" + e.getMessage(), e);
      }
    }

    return ansResult;
  }

  public DboCollection getPtnTraphsByTraphCuids(BoActionContext actionContext, String traphCuids)
    throws UserException
  {
    DboCollection dboCollection = null;
    try {
      dboCollection = getPtnDAOX().getPtnTraphsByTraphCuids(actionContext, traphCuids);
    } catch (Exception e) {
      LogHome.getLog().error("获取PTN电路出错:" + e.getMessage(), e);
    }
    return dboCollection;
  }

  public PtnVirtualLine getPtnVirtuallLineByCuid(String lineCuid) throws UserException {
    PtnVirtualLine ptnVirtualLine = null;
    try {
      ptnVirtualLine = getPtnDAOX().getPtnVirtuallLineByCuid(lineCuid);
    } catch (Exception e) {
      LogHome.getLog().error("获取PTN伪线出错:" + e.getMessage(), e);
      throw new UserException(e);
    }
    return ptnVirtualLine;
  }

  public DataObjectList getNoRelationTraphsByCuids(BoActionContext boActionContext, String traphCuids) throws UserException
  {
    try
    {
      return getPtnDAOX().getNoRelationTraphsByCuids(boActionContext, traphCuids);
    } catch (Exception e) {
      LogHome.getLog().error("获取为关联隧道的PTN电路失败:" + e.getMessage(), e);
    }throw new UserException(e);
  }

  public Map<String, String> getVLCuidsByPaths(BoActionContext actionContext, String ptnPathCuids, int port) throws UserException
  {
    Map ptnVlMap = new HashMap();
    try {
      String relatedAptpCuid = "";
      String relatedZptpCuid = "";
      switch (port)
      {
      case 1:
        relatedAptpCuid = "RELATED_A_PTP_CUID";
        relatedZptpCuid = "RELATED_Z_PTP_CUID";
        break;
      case 2:
        relatedAptpCuid = "RELATED_A_PTP_CUID2";
        relatedZptpCuid = "RELATED_Z_PTP_CUID2";
      }

      String queryAptpSql = "(select A.CUID,A." + relatedAptpCuid + " as A1," + "A.A2," + "nvl((select max(PPB." + "SV_TP_CUID" + ") from " + "PTN_PORT_BANDING" + " PPB " + " where PPB." + "PHYSICAL_TP_CUID" + " = A.A2)," + " (select max(PPB." + "PHYSICAL_TP_CUID" + ") " + " from " + "PTN_PORT_BANDING" + " PPB " + " where PPB." + "SV_TP_CUID" + " = A.A2)) AS A3 " + " from (select PP." + relatedAptpCuid + "," + "nvl((select max(PPB." + "SV_TP_CUID" + ")" + " from " + "PTN_PORT_BANDING" + " PPB " + " where PP." + relatedAptpCuid + " = PPB." + "PHYSICAL_TP_CUID" + ")," + " (select max(PPB." + "PHYSICAL_TP_CUID" + " )" + " from " + "PTN_PORT_BANDING" + " PPB" + " where PP." + relatedAptpCuid + " = PPB." + "SV_TP_CUID" + ")) as A2," + " PP." + "CUID" + " " + " from " + "PTN_PATH" + " PP " + " where PP." + "CUID" + " in (" + ptnPathCuids + ")) A) A";

      String queryZptpSql = "(select Z.CUID,Z." + relatedZptpCuid + " as Z1," + "Z.Z2," + "nvl((select max(PPB." + "SV_TP_CUID" + ") from " + "PTN_PORT_BANDING" + " PPB " + " where PPB." + "PHYSICAL_TP_CUID" + " = Z.Z2)," + " (select max(PPB." + "PHYSICAL_TP_CUID" + ") " + " from " + "PTN_PORT_BANDING" + " PPB " + " where PPB." + "SV_TP_CUID" + " = Z.Z2)) AS Z3 " + " from (select PP." + relatedZptpCuid + "," + "nvl((select max(PPB." + "SV_TP_CUID" + ")" + " from " + "PTN_PORT_BANDING" + " PPB " + " where PP." + relatedZptpCuid + " = PPB." + "PHYSICAL_TP_CUID" + ")," + " (select max(PPB." + "PHYSICAL_TP_CUID" + " )" + " from " + "PTN_PORT_BANDING" + " PPB" + " where PP." + relatedZptpCuid + " = PPB." + "SV_TP_CUID" + ")) as Z2," + " PP." + "CUID" + " " + " from " + "PTN_PATH" + " PP " + " where PP." + "CUID" + " in (" + ptnPathCuids + ")) Z) Z";

      String sql = "select PP.CUID,max(PETA.PVL_CUID) as PVL_CUID  from " + queryAptpSql + "," + queryZptpSql + "," + "PTN_PATH" + " PP, ";

      String ptnServiceSql = "( select PVL.CUID as PVL_CUID, PE.ORIG_PTP_CUID,PE.DEST_PTP_CUID  from PTN_ETH PE, PTN_VIRTUAL_LINE PVL  where PE.PTN_VL_FDN = PVL.FDN  union  select PVL.CUID as PVL_CUID, PT.ORIG_PTP_CUID,PT.DEST_PTP_CUID  from PTN_TDM PT, PTN_VIRTUAL_LINE PVL  where PT.PTN_VL_FDN = PVL.FDN  union  select PVL.CUID as PVL_CUID, PT.ORIG_PTP_CUID,PT.DEST_PTP_CUID  from PTN_TP_PAIR PT, PTN_VIRTUAL_LINE PVL  where PT.RELATED_VIRTUAL_LINE_CUID = PVL.CUID  union  select PVL.CUID as PVL_CUID, PA.ORIG_PTP_CUID, PA.DEST_PTP_CUID from PTN_ATM PA, PTN_VIRTUAL_LINE PVL  where PA.PTN_VL_FDN = PVL.FDN) PETA ";

      String whereSql = "  where ((PETA.ORIG_PTP_CUID in (A.A1, A.A2, A.A3) and  PETA.DEST_PTP_CUID in (Z.Z1, Z.Z2, Z.Z3)) or  (PETA.DEST_PTP_CUID in (A.A1, A.A2, A.A3) and  PETA.ORIG_PTP_CUID in (Z.Z1, Z.Z2, Z.Z3)))  and PP.CUID= A.CUID  and PP.CUID= Z.CUID  group by PP.CUID";

      sql = sql + ptnServiceSql + whereSql;

      DataObjectList dbos = getPtnDAOX().selectDBOs(sql, new Class[] { String.class, String.class });
      if (null != dbos)
      {
        for (GenericDO dbo : dbos)
        {
          ptnVlMap.put(dbo.getAttrString("1"), dbo.getAttrString("2"));
        }
      }
    }
    catch (Exception e)
    {
      LogHome.getLog().error("批量隧道关联失败" + e.getMessage(), e);
      throw new UserException(e);
    }
    return ptnVlMap;
  }

  public List<String> getSimplePtnTraphObjs(BoActionContext actionContext, String traphObjectids)
    throws UserException
  {
    String queryPathCuid = "select T.OBJECTID,P.CUID  from TRAPH T,PTN_PATH P where T.OBJECTID in (" + traphObjectids + ") and T." + "CUID" + " = P." + "RELATED_ROUTE_CUID";

    List simplePtnTraphObjs = new ArrayList();
    try {
      DataObjectList dbos = getPtnDAOX().selectDBOs(queryPathCuid, new Class[] { String.class, String.class });
      Map<String, Boolean> traphAndPathMap = new HashMap<String, Boolean>();
      if ((null != dbos) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          String objectId = ((GenericDO)dbos.get(i)).getAttrString("1");
          String pathCuid = ((GenericDO)dbos.get(i)).getAttrString("2");
          if (!traphAndPathMap.containsKey(objectId))
          {
            traphAndPathMap.put(objectId, Boolean.valueOf(false));
            if (pathCuid.startsWith("PTN_PATH"))
            {
              traphAndPathMap.put(objectId, Boolean.valueOf(true));
            }
            LogHome.getLog().info("电路:" + objectId + "对应的第一段路由类型是:" + pathCuid);
          }
          if (!traphAndPathMap.containsKey(objectId))
            continue;
          if (!((Boolean)traphAndPathMap.get(objectId)).booleanValue())
            continue;
          LogHome.getLog().info("电路:" + objectId + "的前一段路由为PTN段");
          if (pathCuid.startsWith("PTN_PATH"))
          {
            traphAndPathMap.put(objectId, Boolean.valueOf(true));
          }
          else {
            traphAndPathMap.put(objectId, Boolean.valueOf(false));
          }
          LogHome.getLog().info("电路:" + objectId + "当前路由段为" + pathCuid);
        }

      }

      for (Map.Entry entry : traphAndPathMap.entrySet())
      {
        if (((Boolean)entry.getValue()).booleanValue())
        {
          simplePtnTraphObjs.add(entry.getKey());
          LogHome.getLog().info("-CD-记录纯PTN电路OBJECTID:" + (String)entry.getKey());
        }
      }
    } catch (Exception e) {
      LogHome.getLog().error("获取纯PTN电路OBJECTID失败:" + e.getMessage(), e);
      throw new UserException(e);
    }
    return simplePtnTraphObjs;
  }

  public DataObjectList getAzPtp(BoActionContext context, String ptnPathCuid)
    throws UserException
  {
    DataObjectList dataObjectList = new DataObjectList();
    try
    {
      LogHome.getLog().info("查询PTN段AZ端口开始");
      DataObjectList dbos = getPtnDAOX().getAzPtp(context, ptnPathCuid);
      if (null != dbos)
      {
        dataObjectList = dbos;
      }
      LogHome.getLog().info("查询PTN段AZ端口结束");
    }
    catch (Exception e) {
      LogHome.getLog().error("查询PTN段AZ端口失败:" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dataObjectList;
  }

  public DataObjectList getPtnLteProByCuid(BoActionContext context, String traphCuids) throws UserException
  {
    DataObjectList dataObjectList = new DataObjectList();
    try {
      DataObjectList dbos = getPtnDAOX().getPtnLteProByCuid(context, traphCuids);
      if ((null != dbos) && (!dbos.isEmpty()))
      {
        dataObjectList = dbos;
      }
    }
    catch (Exception e) {
      LogHome.getLog().error("获取PTN、LTE电路属性失败:" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dataObjectList;
  }

  public DataObjectList getTraphByVLCuid(BoActionContext actionContext, String virCuid) throws UserException
  {
    try {
      return getPtnDAOX().getTraphByVLCuid(actionContext, virCuid);
    } catch (Exception e) {
      LogHome.getLog().error("根据伪线获取电路失败:" + e.getMessage(), e);
    }
    return null;
  }

  public void deletePtnVirtualLine(BoActionContext actionContext, String vlCuid)
    throws Exception
  {
    String sql = "SELECT FDN FROM PTN_VIRTUAL_LINE WHERE CUID ='" + vlCuid + "'";
    DboCollection dbos1 = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
    if ((dbos1 != null) && (dbos1.size() > 0)) {
      PtnVirtualLine dbo = (PtnVirtualLine)dbos1.getAttrField("PTN_VIRTUAL_LINE", 0);
      if (dbo != null) {
        sql = "RELATED_VIRTUALLINE_FDN='" + dbo.getFdn() + "'";
        getPtnDAOX().deleteObjects(new BoActionContext(), "PTN_NE_TO_SERVICE", sql);
      }
    }

    DataObjectList ptnServiceList = setPtnServiceStateByVlCuid("", vlCuid, 1L);
    if ((ptnServiceList == null) || (ptnServiceList.size() <= 0))
    {
      sql = "delete from PTN_VIRTUAL_LINE where CUID='" + vlCuid + "'";
      getPtnDAOX().execSql(sql);
      String sqlQuery = "SELECT RELATED_TUNNEL_CUID FROM TUNNEL_TO_VIRTUAL_LINE WHERE RELATED_VIRTUAL_LINE_CUID='" + vlCuid + "'";

      DboCollection dboc = getPtnDAOX().selectDBOs(sqlQuery, new GenericDO[] { new TunnelToVirtualLine() });
      sql = "delete from TUNNEL_TO_VIRTUAL_LINE where RELATED_VIRTUAL_LINE_CUID='" + vlCuid + "'";

      getPtnDAOX().execSql(sql);
      for (int i = 0; i < dboc.size(); i++) {
        TunnelToVirtualLine toLine = (TunnelToVirtualLine)dboc.getAttrField("TUNNEL_TO_VIRTUAL_LINE", i);
        sqlQuery = "SELECT RELATED_TUNNEL_CUID FROM TUNNEL_TO_VIRTUAL_LINE WHERE RELATED_TUNNEL_CUID='" + toLine.getRelatedTunnelCuid() + "'";

        DboCollection dboc2 = getPtnDAOX().selectDBOs(sqlQuery, new GenericDO[] { new TunnelToVirtualLine() });
        if (dboc2.size() == 0) {
          sql = "SELECT FDN FROM PTN_TURNNEL WHERE CUID ='" + toLine.getRelatedTunnelCuid() + "'";
          DboCollection dbos = getPtnDAOX().selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
          if ((dbos != null) && (dbos.size() > 0)) {
            PtnTurnnel dbo = (PtnTurnnel)dbos.getAttrField("PTN_TURNNEL", 0);
            if (dbo != null) {
              sql = "RELATED_TUNNEL_FDN='" + dbo.getFdn() + "'";
              getPtnDAOX().deleteObjects(new BoActionContext(), "PTN_NE_TO_SERVICE", sql);
            }
          }
          sql = "delete from PTN_TURNNEL where CUID='" + toLine.getRelatedTunnelCuid() + "'";

          getPtnDAOX().execSql(sql);

          sql = "delete from PTN_IP_CROSSCONNECT where RELATED_PATH_CUID='" + toLine.getRelatedTunnelCuid() + "'";

          getPtnDAOX().execSql(sql);
        }
      }
    }
  }

  public DataObjectList setPtnServiceStateByVlCuid(String ptnPathCuid, String vlCuid, long state)
    throws Exception
  {
    String sql = "SELECT P.CUID, 'PTN_ETH' AS TBL_NAME FROM PTN_ETH P, PTN_VIRTUAL_LINE PVL WHERE P.PTN_VL_FDN=PVL.FDN AND PVL.CUID='" + vlCuid + "'" + " UNION SELECT P.CUID, 'PTN_ATM' AS TBL_NAME FROM PTN_ATM P, PTN_VIRTUAL_LINE PVL WHERE P.PTN_VL_FDN=PVL.FDN AND PVL.CUID='" + vlCuid + "'" + " UNION SELECT P.CUID, 'PTN_TDM' AS TBL_NAME FROM PTN_TDM P, PTN_VIRTUAL_LINE PVL WHERE P.PTN_VL_FDN=PVL.FDN AND PVL.CUID='" + vlCuid + "'";

    DataObjectList ptnServiceList = getPtnDAOX().selectDBOs(sql, new Class[] { String.class, String.class });
    if ((ptnServiceList != null) && (ptnServiceList.size() > 0)) {
      for (int i = 0; i < ptnServiceList.size(); i++) {
        GenericDO gdo = (GenericDO)ptnServiceList.get(i);
        String ptnServiceCuid = gdo.getAttrString("1");
        String tabName = gdo.getAttrString("2");
        sql = "update " + tabName + " set STATE=" + state + " where CUID='" + ptnServiceCuid + "'";
        LogHome.getLog().info("更新PTN业务【" + ptnServiceCuid + "】状态为" + state);
        getPtnDAOX().execSql(sql);
      }
      if (state == 1L) {
        sql = "update PTN_VIRTUAL_LINE set RELATED_PATH_CUID='' where CUID='" + vlCuid + "'";

        LogHome.getLog().info("清空伪线【" + vlCuid + "】的RELATED_PATH_CUID");
        getPtnDAOX().execSql(sql);
      } else if (StringUtils.isNotEmpty(ptnPathCuid)) {
        sql = "update PTN_VIRTUAL_LINE set RELATED_PATH_CUID='" + ptnPathCuid + "'" + " where " + "CUID" + "='" + vlCuid + "'";

        LogHome.getLog().info("更新伪线【" + vlCuid + "】的RELATED_PATH_CUID为" + ptnPathCuid);
        getPtnDAOX().execSql(sql);
      }
    }
    return ptnServiceList;
  }

  public DboCollection getLspName(String lspName) throws Exception {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getLspName(lspName);
    } catch (Exception e) {
      LogHome.getLog().error("依据保护组名称查询保护组出错" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DboCollection getTurnnelNativeName(String nativeName) throws Exception
  {
    DboCollection dbos = null;
    try {
      dbos = getPtnDAOX().getTurnnelNativeName(nativeName);
    } catch (Exception e) {
      LogHome.getLog().error("依据隧道本地名称查询隧道出错" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dbos;
  }

  public DataObjectList getOriPtpCuidCount(String EthCuid) throws Exception {
    DataObjectList list = null;
    try {
      list = getPtnDAOX().getOriPtpCuidCount(EthCuid);
    } catch (Exception e) {
      LogHome.getLog().error("依据ptn_ETH源端端口数目出错" + e.getMessage(), e);
      throw new UserException(e);
    }
    return list;
  }

  public DataObjectList getDestPtpCuidCount(String EthCuid) throws Exception {
    DataObjectList list = null;
    try {
      list = getPtnDAOX().getDestPtpCuidCount(EthCuid);
    } catch (Exception e) {
      LogHome.getLog().error("依据ptn_ETH源端端口数目出错" + e.getMessage(), e);
      throw new UserException(e);
    }
    return list;
  }

  public DataObjectList getPtnAndVirtualLine(BoQueryContext context, String EthCuid) throws Exception {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPtnAndVirtualLine(context, EthCuid);
    } catch (Exception e) {
      LogHome.getLog().error("依据ptn_ETH查询伪线出错" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dbos;
  }
  public DataObjectList getPtnProtectGroupInfos(BoActionContext actionContext, String turnnelCuids) throws UserException {
    DataObjectList dbos = null;
    try {
      dbos = getPtnDAOX().getPtnProtectGroupInfos(actionContext, turnnelCuids);
    } catch (Exception e) {
      LogHome.getLog().info("查询隧道保护组出错！" + e.getMessage());
      throw new UserException(e);
    }
    return dbos;
  }

  public void saveDataResponsiblePerson(BoActionContext actionContext, TDeviceToPerson tDeviceToPerson) {
    try {
      getPtnDAOX().saveDataResponsiblePerson(actionContext, tDeviceToPerson);
    } catch (Exception e) {
      LogHome.getLog().error("增加责任人失败" + e.getMessage(), e);
      throw new UserException(e);
    }
  }

  public void removeDataResponsiblePerson(BoActionContext actionContext, String cuid) {
    try {
      getPtnDAOX().removeDataResponsiblePerson(actionContext, cuid);
    } catch (Exception e) {
      LogHome.getLog().error("删除责任人失败" + e.getMessage(), e);
      throw new UserException(e);
    }
  }

  public DataObjectList getDataResponsiblePerson(BoActionContext actionContext, String cuid) {
    DataObjectList dbo;
    try {
      dbo = getPtnDAOX().getDataResponsiblePerson(actionContext, cuid);
    } catch (Exception e) {
      LogHome.getLog().error("获取责任人失败" + e.getMessage(), e);
      throw new UserException(e);
    }
    return dbo;
  }

  public List getTraphAvailability(BoActionContext actionContext, String districtCuid)
  {
    try
    {
      return getPtnDAOX().getTraphAvailability(actionContext, districtCuid);
    } catch (Exception ex) {
      LogHome.getLog().error("根据区域查询端口使用情况失败！", ex);
    }throw new UserException(ex.getMessage());
  }

@Override
public HashMap getChangePtnVLHb(BoActionContext actionContext,
		String ptnTraphCuids) throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Map<String, String> getVLCuidsByPathsHb(BoActionContext actionContext,
		String ptnPathCuids) throws UserException {
	// TODO Auto-generated method stub
	return null;
}
}