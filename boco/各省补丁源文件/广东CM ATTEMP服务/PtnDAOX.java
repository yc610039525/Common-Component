package com.boco.transnms.server.dao.path;

import com.boco.common.util.debug.LogHome;
import com.boco.transnms.common.dto.AttempPtnPath;
import com.boco.transnms.common.dto.BackboneCollect;
import com.boco.transnms.common.dto.PtnAtm;
import com.boco.transnms.common.dto.PtnEth;
import com.boco.transnms.common.dto.PtnIpCrossconnect;
import com.boco.transnms.common.dto.PtnPath;
import com.boco.transnms.common.dto.PtnPortBanding;
import com.boco.transnms.common.dto.PtnProtectGroup;
import com.boco.transnms.common.dto.PtnProtectGroupUnit;
import com.boco.transnms.common.dto.PtnTdm;
import com.boco.transnms.common.dto.PtnTpPair;
import com.boco.transnms.common.dto.PtnTurnnel;
import com.boco.transnms.common.dto.PtnVirtualLine;
import com.boco.transnms.common.dto.Ptp;
import com.boco.transnms.common.dto.PtpNa;
import com.boco.transnms.common.dto.TDeviceToPerson;
import com.boco.transnms.common.dto.Traph;
import com.boco.transnms.common.dto.TunnelToVirtualLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DataObjectMap;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.misc.LayerRate;
import com.boco.transnms.server.dao.base.DaoHelper;
import com.boco.transnms.server.dao.base.GenericDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

public class PtnDAOX extends GenericDAO
{
  public PtnDAOX()
  {
    super("PtnDAOX");
  }

  public DataObjectList getPtnLteProByCuid(BoActionContext context, String traphCuids)
    throws Exception
  {
    Map ipMap = new HashMap();
    DataObjectList list = new DataObjectList();
    if (StringUtils.isNotEmpty(traphCuids)) {
      if (traphCuids.endsWith(",")) {
        traphCuids = traphCuids.substring(0, traphCuids.length() - 1);
      }
      traphCuids = traphCuids.replaceAll(",", "','");
      String ipSql = "SELECT IP.ADDR,P.RELATED_ROUTE_CUID,TPI.TYPE FROM T_PTN_PATH_TO_IP TPI, T_LOGIC_NUMBER_IP IP,T_LOGIC_NUMBER_RANGE R,PTN_PATH P WHERE TPI.RELATED_PTN_PATH_CUID = P.CUID AND TPI.RELATED_NUMBER_IP_CUID = IP.CUID AND R.CUID=IP.RELATED_NUMBER_RANGE_CUID AND P.RELATED_ROUTE_CUID IN ('" + traphCuids + "')";

      DataObjectList ipList = super.selectDBOs(ipSql, new Class[] { String.class, String.class, String.class });
      if ((ipList != null) && (ipList.size() > 0)) {
        for (GenericDO gdo : ipList) {
          if (gdo != null) {
            String traphCuid = gdo.getAttrString("2");
            String ipType = gdo.getAttrString("3");
            String addr = gdo.getAttrString("1");
            if ((ipType != null) && (!ipType.equals(""))) {
              if (ipType.equals("BUSINESS")) {
                if (ipMap.get(traphCuid) != null) {
                  GenericDO gend = (GenericDO)ipMap.get(traphCuid);
                  gend.setAttrValue("ipAddr", addr);
                  ipMap.put(traphCuid, gend);
                } else {
                  GenericDO gend = new GenericDO();
                  gend.setAttrValue("ipAddr", addr);
                  ipMap.put(traphCuid, gend);
                }
              }
              else if (ipType.equals("NETCONFIG")) {
                if (ipMap.get(traphCuid) != null) {
                  GenericDO gend = (GenericDO)ipMap.get(traphCuid);
                  gend.setAttrValue("netAddr", addr);
                  ipMap.put(traphCuid, gend);
                } else {
                  GenericDO gend = new GenericDO();
                  gend.setAttrValue("netAddr", addr);
                  ipMap.put(traphCuid, gend);
                }
              }
            }
          }
        }
      }

      String sql = " SELECT P.CUID, P.RELATED_ROUTE_CUID, P.CIR_BAND, P.PIR_BAND, P.QOS_BAND, P.VLANID,(SELECT S.SITECODING FROM SITE S, TRANS_ELEMENT E  WHERE E.RELATED_SITE_CUID = S.CUID  AND E.CUID = P.RELATED_Z_NE_CUID) AS LTE_SITECODING, (SELECT LABEL_CN FROM PTP PT WHERE PT.CUID = P.RELATED_A_PTP_CUID)  AS V_PORT, (SELECT LABEL_CN FROM PTP PT WHERE PT.CUID = P.RELATED_A_PTP_CUID2) AS V_PORT2, P.BSVLANID  FROM PTN_PATH P  WHERE P.RELATED_ROUTE_CUID IN ('" + traphCuids + "')";

      list = super.selectDBOs(sql, new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class });

      if ((list != null) && (list.size() > 0)) {
        for (GenericDO gdo : list) {
          if (gdo != null) {
            String traphCuid = gdo.getAttrString("2");
            GenericDO endss = (GenericDO)ipMap.get(traphCuid);
            if (endss != null) {
              String ipAddrStr = (String)endss.getAttrValue("ipAddr");
              String gateWayStr = (String)endss.getAttrValue("netAddr");
              gdo.setAttrValue("11", ipAddrStr);
              gdo.setAttrValue("12", gateWayStr);
            } else {
              gdo.setAttrValue("11", "");
              gdo.setAttrValue("12", "");
            }
          }
        }
      }
    }

    return list;
  }

  public DataObjectList getAzPtp(BoActionContext context, String ptnPathCuid)
    throws Exception
  {
    String sql = "SELECT (SELECT LABEL_CN FROM PTP WHERE CUID = RELATED_A_PTP_CUID) AS RELATED_A_PTP, (SELECT LABEL_CN FROM PTP WHERE CUID = RELATED_Z_PTP_CUID) AS RELATED_Z_PTP, (SELECT LABEL_CN FROM PTP WHERE CUID = RELATED_A_PTP_CUID2) AS RELATED_A_PTP2,(SELECT LABEL_CN FROM PTP WHERE CUID = RELATED_Z_PTP_CUID2) AS RELATED_Z_PTP2  FROM PTN_PATH WHERE CUID = '" + ptnPathCuid + "'";

    return super.selectDBOs(sql, new Class[] { String.class, String.class, String.class, String.class });
  }

  public DboCollection getTunnelByPage(BoQueryContext queryContext, Map paramMap)
    throws Exception
  {
    DboCollection dbos = null;
    if (paramMap != null) {
      long startTime = System.currentTimeMillis();
      String isExcel = (String)paramMap.get("isExcel");
      String sql = getTunnelQuerySql(paramMap);
      if (StringUtils.isNotEmpty(sql)) {
        if ((StringUtils.isNotEmpty(isExcel)) && (isExcel.equals("1")))
        {
          DataObjectList dbs = super.selectDBOs(sql, new Class[] { String.class, String.class, String.class, String.class, String.class, Long.TYPE, Long.TYPE, Long.TYPE, Boolean.TYPE, Timestamp.class, String.class, String.class, Long.TYPE, String.class, String.class, String.class, String.class });
          if ((dbs != null) && (dbs.size() > 0)) {
            DboCollection dblis = new DboCollection();
            for (int i = 0; i < dbs.size(); i++) {
              PtnTurnnel turnnel = new PtnTurnnel();
              PtnProtectGroup group = new PtnProtectGroup();
              PtnProtectGroupUnit unit = new PtnProtectGroupUnit();
              GenericDO gdo = (GenericDO)dbs.get(i);
              String fdn = gdo.getAttrString("1");
              String orig = gdo.getAttrString("2");
              String dest = gdo.getAttrString("3");
              String turUserlabel = gdo.getAttrString("4");
              String nativeEmsName = gdo.getAttrString("5");
              long channelType = gdo.getAttrLong("6");
              long lspDirection = gdo.getAttrLong("7");
              long activeFlag = gdo.getAttrLong("8");
              boolean networkRouted = gdo.getAttrBool("9");
              Timestamp channelCreateTime = gdo.getAttrDateTime("10");
              String remark = gdo.getAttrString("11");
              String gUserLabel = gdo.getAttrString("12");
              long twType = gdo.getAttrLong("13");
              String origPtpCuid = gdo.getAttrString("14");
              String origTpinLabel = gdo.getAttrString("15");
              String destPtpCuid = gdo.getAttrString("16");
              String destTpinLabel = gdo.getAttrString("17");
              turnnel.setFdn(fdn);
              turnnel.setOrigTpOutlabel(orig);
              turnnel.setDestTpOutlabel(dest);
              turnnel.setUserLabel(turUserlabel);
              turnnel.setNativeEmsName(nativeEmsName);
              turnnel.setChannelType(channelType);
              turnnel.setLspDirection(lspDirection);
              turnnel.setActiveFlag(activeFlag);
              turnnel.setNetworkRouted(networkRouted);
              turnnel.setChannelCreateTime(channelCreateTime);
              turnnel.setRemark(remark);
              group.setUserLabel(gUserLabel);
              unit.setTwType(twType);
              turnnel.setOrigPtpCuid(origPtpCuid);
              turnnel.setOrigTpInlabel(origTpinLabel);
              turnnel.setDestPtpCuid(destPtpCuid);
              turnnel.setDestTpInlabel(destTpinLabel);
              DataObjectMap objectMap = new DataObjectMap();
              objectMap.put("PTN_TURNNEL", turnnel);
              objectMap.put("PTN_PROTECT_GROUP", group);
              objectMap.put("PTN_PROTECT_GROUP_UNIT", unit);
              dblis.addRow(objectMap);
            }
            dbos = dblis;
          }
        }
        else {
          LogHome.getLog().info("------------------------------sqlgetturnnel:" + sql);

          DataObjectList dbs = super.selectDBOs(queryContext, sql, new Class[] { String.class, String.class, String.class, String.class, String.class, Long.TYPE, Long.TYPE, Long.TYPE, Boolean.TYPE, Timestamp.class, String.class, String.class, Long.TYPE, String.class, String.class, String.class, String.class });
          if ((dbs != null) && (dbs.size() > 0)) {
            DboCollection dblis = new DboCollection();
            dblis.setCountValue(dbs.getCountValue());
            dblis.setFetchSize(dbs.getFetchSize());
            dblis.setOffset(dbs.getOffset());
            for (int i = 0; i < dbs.size(); i++) {
              PtnTurnnel turnnel = new PtnTurnnel();
              PtnProtectGroup group = new PtnProtectGroup();
              PtnProtectGroupUnit unit = new PtnProtectGroupUnit();
              GenericDO gdo = (GenericDO)dbs.get(i);
              String fdn = gdo.getAttrString("1");
              String orig = gdo.getAttrString("2");
              String dest = gdo.getAttrString("3");
              String turUserlabel = gdo.getAttrString("4");
              String nativeEmsName = gdo.getAttrString("5");
              long channelType = gdo.getAttrLong("6");
              long lspDirection = gdo.getAttrLong("7");
              long activeFlag = gdo.getAttrLong("8");
              boolean networkRouted = gdo.getAttrBool("9");
              Timestamp channelCreateTime = gdo.getAttrDateTime("10");
              String remark = gdo.getAttrString("11");
              String gUserLabel = gdo.getAttrString("12");
              long twType = gdo.getAttrLong("13");
              String origPtpCuid = gdo.getAttrString("14");
              String origTpinLabel = gdo.getAttrString("15");
              String destPtpCuid = gdo.getAttrString("16");
              String destTpinLabel = gdo.getAttrString("17");
              turnnel.setFdn(fdn);
              turnnel.setOrigTpOutlabel(orig);
              turnnel.setDestTpOutlabel(dest);
              turnnel.setUserLabel(turUserlabel);
              turnnel.setNativeEmsName(nativeEmsName);
              turnnel.setChannelType(channelType);
              turnnel.setLspDirection(lspDirection);
              turnnel.setActiveFlag(activeFlag);
              turnnel.setNetworkRouted(networkRouted);
              turnnel.setChannelCreateTime(channelCreateTime);
              turnnel.setRemark(remark);
              group.setUserLabel(gUserLabel);
              unit.setTwType(twType);
              turnnel.setOrigPtpCuid(origPtpCuid);
              turnnel.setOrigTpInlabel(origTpinLabel);
              turnnel.setDestPtpCuid(destPtpCuid);
              turnnel.setDestTpInlabel(destTpinLabel);
              DataObjectMap objectMap = new DataObjectMap();
              objectMap.put("PTN_TURNNEL", turnnel);
              objectMap.put("PTN_PROTECT_GROUP", group);
              objectMap.put("PTN_PROTECT_GROUP_UNIT", unit);
              dblis.addRow(objectMap);
            }
            dbos = dblis;
          }
          LogHome.getLog().info("---------------------------------ok");
        }
      }
      LogHome.getLog().info("===== 查询隧道用时 " + (System.currentTimeMillis() - startTime) + " ms=====");
    }
    return dbos;
  }

  private String getTunnelQuerySql(Map paramMap)
  {
    String sql = "";
    if (paramMap != null) {
      String queryType = "";
      if (paramMap.get("queryType") != null) {
        queryType = (String)paramMap.get("queryType");
      }
      if (StringUtils.isNotEmpty(queryType)) {
        if (queryType.equals("ems")) {
          String emsCuid = (String)paramMap.get("emsCuid");
          sql = "SELECT DISTINCT PT.FDN,PT.ORIG_TP_OUTLABEL,PT.DEST_TP_OUTLABEL,PT.USER_LABEL,PT.NATIVE_EMS_NAME,PT.CHANNEL_TYPE,PT.LSP_DIRECTION,PT.ACTIVE_FLAG,PT.NETWORK_ROUTED,PT.CHANNEL_CREATE_TIME,PT.REMARK,g.USER_LABEL,u.TW_TYPE,PT.ORIG_PTP_CUID,PT.ORIG_TP_INLABEL,PT.DEST_PTP_CUID,PT.DEST_TP_INLABEL FROM (PTN_PROTECT_GROUP g INNER JOIN PTN_PROTECT_GROUP_UNIT u ON g.CUID = u.PG_CUID) RIGHT OUTER JOIN PTN_TURNNEL PT ON PT.CUID = u.TW_CUID WHERE PT.RELATED_EMS_CUID='" + emsCuid + "'";
          String tunnelName = "";
          if (paramMap.get("tunnelName") != null) {
            tunnelName = (String)paramMap.get("tunnelName");
            if (StringUtils.isNotEmpty(tunnelName)) {
              sql = sql + " AND PT.NATIVE_EMS_NAME LIKE '%" + tunnelName + "%'";
            }
          }
          String lspName = "";
          if (paramMap.get("lspName") != null) {
            lspName = (String)paramMap.get("lspName");
            if (StringUtils.isNotEmpty(lspName))
              sql = sql + " AND g.USER_LABEL LIKE '%" + lspName + "%'";
          }
        }
        else if (queryType.equals("element")) {
          String elementType = (String)paramMap.get("elementType");
          String elementCuids = (String)paramMap.get("elementCuids");
          if ((StringUtils.isNotEmpty(elementType)) && (StringUtils.isNotEmpty(elementCuids))) {
            elementCuids = "'" + elementCuids.replace(",", "','") + "'";
            if (elementType.equals("ne"))
            {
              sql = "select distinct PT.FDN,PT.ORIG_TP_OUTLABEL,PT.DEST_TP_OUTLABEL,PT.USER_LABEL,PT.NATIVE_EMS_NAME,PT.CHANNEL_TYPE,PT.LSP_DIRECTION,PT.ACTIVE_FLAG,PT.NETWORK_ROUTED,PT.CHANNEL_CREATE_TIME,PT.REMARK,g.USER_LABEL,u.TW_TYPE,PT.ORIG_PTP_CUID,PT.ORIG_TP_INLABEL,PT.DEST_PTP_CUID,PT.DEST_TP_INLABEL from (PTN_PROTECT_GROUP g inner join PTN_PROTECT_GROUP_UNIT u on g.CUID = u.PG_CUID) right outer join (PTN_TURNNEL PT inner join PTN_IP_CROSSCONNECT PIC on PT.CUID = PIC.RELATED_PATH_CUID) on PT.CUID = u.TW_CUID where PIC.RELATED_NE_CUID in (" + elementCuids + ")";
            }
            else if (elementType.equals("card"))
            {
              sql = "select distinct PT.FDN,PT.ORIG_TP_OUTLABEL,PT.DEST_TP_OUTLABEL,PT.USER_LABEL,PT.NATIVE_EMS_NAME,PT.CHANNEL_TYPE,PT.LSP_DIRECTION,PT.ACTIVE_FLAG,PT.NETWORK_ROUTED,PT.CHANNEL_CREATE_TIME,PT.REMARK,g.USER_LABEL,u.TW_TYPE,PT.ORIG_PTP_CUID,PT.ORIG_TP_INLABEL,PT.DEST_PTP_CUID,PT.DEST_TP_INLABEL from (PTN_PROTECT_GROUP g inner join PTN_PROTECT_GROUP_UNIT u on g.CUID = u.PG_CUID) right outer join (PTN_TURNNEL PT inner join PTN_IP_CROSSCONNECT PIC on PT.CUID = PIC.RELATED_PATH_CUID) on PT.CUID = u.TW_CUID where (PIC.ORIG_CARD_CUID in (" + elementCuids + ") or PIC." + "DEST_CARD_CUID" + " in (" + elementCuids + "))";
            }
            else if (elementType.equals("ptp"))
            {
              sql = "select distinct PT.FDN,PT.ORIG_TP_OUTLABEL,PT.DEST_TP_OUTLABEL,PT.USER_LABEL,PT.NATIVE_EMS_NAME,PT.CHANNEL_TYPE,PT.LSP_DIRECTION,PT.ACTIVE_FLAG,PT.NETWORK_ROUTED,PT.CHANNEL_CREATE_TIME,PT.REMARK,g.USER_LABEL,u.TW_TYPE,PT.ORIG_PTP_CUID,PT.ORIG_TP_INLABEL,PT.DEST_PTP_CUID,PT.DEST_TP_INLABEL from (PTN_PROTECT_GROUP g inner join PTN_PROTECT_GROUP_UNIT u on g.CUID = u.PG_CUID) right outer join (PTN_TURNNEL PT inner join PTN_IP_CROSSCONNECT PIC on PT.CUID = PIC.RELATED_PATH_CUID) on PT.CUID = u.TW_CUID where (PIC.ORIG_PTP_CUID in (" + elementCuids + ") or PIC." + "DEST_PTP_CUID" + " in (" + elementCuids + "))";
            }
          }

        }
        else if (queryType.equals("port")) {
          String startPortCuids = "";
          String startNeCuids = "";
          String startCardCuids = "";
          String startPtpCuids = "";
          String endPortCuids = "";
          String endNeCuids = "";
          String endCardCuids = "";
          String endPtpCuids = "";

          if (paramMap.get("startPortCuids") != null) {
            startPortCuids = (String)paramMap.get("startPortCuids");
            Map startPointMap = getPortCuids(startPortCuids);
            if (startPointMap != null) {
              startNeCuids = (String)startPointMap.get("neCuids");
              startCardCuids = (String)startPointMap.get("cardCuids");
              startPtpCuids = (String)startPointMap.get("ptpCuids");
              if (StringUtils.isEmpty(startNeCuids)) {
                startNeCuids = "'null'";
              }
              if (StringUtils.isEmpty(startCardCuids)) {
                startCardCuids = "'null'";
              }
              if (StringUtils.isEmpty(startPtpCuids)) {
                startPtpCuids = "'null'";
              }
            }
          }
          if (paramMap.get("endPortCuids") != null) {
            endPortCuids = (String)paramMap.get("endPortCuids");
            Map endPointMap = getPortCuids(endPortCuids);
            if (endPointMap != null) {
              endNeCuids = (String)endPointMap.get("neCuids");
              endCardCuids = (String)endPointMap.get("cardCuids");
              endPtpCuids = (String)endPointMap.get("ptpCuids");
              if (StringUtils.isEmpty(endNeCuids)) {
                endNeCuids = "'null'";
              }
              if (StringUtils.isEmpty(endCardCuids)) {
                endCardCuids = "'null'";
              }
              if (StringUtils.isEmpty(endPtpCuids)) {
                endPtpCuids = "'null'";
              }
            }
          }

          sql = "select distinct PT.FDN,PT.ORIG_TP_OUTLABEL,PT.DEST_TP_OUTLABEL,PT.USER_LABEL,PT.NATIVE_EMS_NAME,PT.CHANNEL_TYPE,PT.LSP_DIRECTION,PT.ACTIVE_FLAG,PT.NETWORK_ROUTED,PT.CHANNEL_CREATE_TIME,PT.REMARK,g.USER_LABEL,u.TW_TYPE,PT.ORIG_PTP_CUID,PT.ORIG_TP_INLABEL,PT.DEST_PTP_CUID,PT.DEST_TP_INLABEL from (PTN_PROTECT_GROUP g inner join PTN_PROTECT_GROUP_UNIT u on g.CUID = u.PG_CUID) right outer join PTN_TURNNEL PT on PT.CUID = u.TW_CUID";
          if ((StringUtils.isEmpty(startPortCuids)) && (StringUtils.isNotEmpty(endPortCuids))) {
            sql = sql + " where (PT.ORIG_NE_CUID in (" + endNeCuids + ") or PT." + "ORIG_CARD_CUID" + " in (" + endCardCuids + ") or PT." + "ORIG_PTP_CUID" + " in (" + endPtpCuids + "))" + " or (PT." + "DEST_PTP_CUID" + " in (" + endNeCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + endCardCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + endPtpCuids + "))";
          }
          else if ((StringUtils.isNotEmpty(startPortCuids)) && (StringUtils.isEmpty(endPortCuids))) {
            sql = sql + " where (PT.ORIG_NE_CUID in (" + startNeCuids + ") or PT." + "ORIG_CARD_CUID" + " in (" + startCardCuids + ") or PT." + "ORIG_PTP_CUID" + " in (" + startPtpCuids + "))" + " or (PT." + "DEST_PTP_CUID" + " in (" + startNeCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + startCardCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + startPtpCuids + "))";
          }
          else if ((StringUtils.isNotEmpty(startPortCuids)) && (StringUtils.isNotEmpty(endPortCuids))) {
            sql = sql + " where (((PT.ORIG_NE_CUID in (" + startNeCuids + ") or PT." + "ORIG_CARD_CUID" + " in (" + startCardCuids + ") or PT." + "ORIG_PTP_CUID" + " in (" + startPtpCuids + "))" + " and (PT." + "DEST_PTP_CUID" + " in (" + endNeCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + endCardCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + endPtpCuids + ")))" + " or ((PT." + "ORIG_NE_CUID" + " in (" + endNeCuids + ") or PT." + "ORIG_CARD_CUID" + " in (" + endCardCuids + ") or PT." + "ORIG_PTP_CUID" + " in (" + endPtpCuids + "))" + " and (PT." + "DEST_PTP_CUID" + " in (" + startNeCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + startCardCuids + ") or PT." + "DEST_PTP_CUID" + " in (" + startPtpCuids + "))))";
          }
          else
          {
            return "";
          }
        }
        String orderFieldString = "";
        if (paramMap.get("orderFieldString") != null) {
          orderFieldString = (String)paramMap.get("orderFieldString");
        }
        if (StringUtils.isNotEmpty(orderFieldString))
          sql = sql + " ORDER BY PT." + orderFieldString;
        else {
          sql = sql + " ORDER BY PT.NATIVE_EMS_NAME";
        }
      }
    }
    return sql;
  }

  public PtnTurnnel getPtnTurnnelByFdn(String fdn)
    throws Exception
  {
    PtnTurnnel ptnTurnnel = null;
    if (StringUtils.isNotEmpty(fdn)) {
      String sql = "select * from PTN_TURNNEL where FDN='" + fdn + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
      if ((dbos != null) && (dbos.size() > 0)) {
        ptnTurnnel = (PtnTurnnel)dbos.getAttrField("PTN_TURNNEL", 0);
      }
    }
    return ptnTurnnel;
  }

  public DataObjectList getPtnTurnnelByFdnList(String fdn) throws Exception
  {
    DataObjectList list = new DataObjectList();
    if (StringUtils.isNotEmpty(fdn)) {
      String sql = "select * from PTN_TURNNEL where FDN IN (" + fdn + ")";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          PtnTurnnel ptnTurnnel = (PtnTurnnel)dbos.getAttrField("PTN_VIRTUAL_LINE", i);
          list.add(ptnTurnnel);
        }
      }
    }
    return list;
  }

  public PtnVirtualLine getPtnVirByFdn(String fdn)
    throws Exception
  {
    PtnVirtualLine virtualLine = null;
    if (StringUtils.isNotEmpty(fdn)) {
      String sql = "select * from PTN_VIRTUAL_LINE where FDN='" + fdn + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0)) {
        virtualLine = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", 0);
      }
    }
    return virtualLine;
  }

  public PtnTurnnel getPtnTurnnelByCuid(String cuid)
    throws Exception
  {
    PtnTurnnel ptnTurnnel = null;
    if (StringUtils.isNotEmpty(cuid)) {
      String sql = "select * from PTN_TURNNEL where CUID='" + cuid + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
      if ((dbos != null) && (dbos.size() > 0)) {
        ptnTurnnel = (PtnTurnnel)dbos.getAttrField("PTN_TURNNEL", 0);
      }
    }
    return ptnTurnnel;
  }

  public PtnVirtualLine getPseudoWireByCuid(String cuid)
    throws Exception
  {
    PtnVirtualLine ptnTurnnel = null;
    if (StringUtils.isNotEmpty(cuid)) {
      String sql = "select * from PTN_VIRTUAL_LINE where CUID='" + cuid + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0)) {
        ptnTurnnel = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", 0);
      }
    }
    return ptnTurnnel;
  }

  public DataObjectList getPtnTurnnelsByName(String tunnelName)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(tunnelName)) {
      long startTime = System.currentTimeMillis();
      String sql = "NATIVE_EMS_NAME like '%" + tunnelName + "%'";
      dbos = super.getObjectsBySql(sql, new PtnTurnnel(), 0);
      LogHome.getLog().info("===== 根据隧道名称查询隧道列表用时 " + (System.currentTimeMillis() - startTime) + " ms =====");
    }
    return dbos;
  }

  public DataObjectList getPtnTurnnelsBySql(String sql)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(sql)) {
      long startTime = System.currentTimeMillis();
      dbos = super.getObjectsBySql(sql, new PtnTurnnel(), 0);
      LogHome.getLog().info("===== 根据隧道名称查询隧道列表用时 " + (System.currentTimeMillis() - startTime) + " ms =====");
    }
    return dbos;
  }

  public DboCollection getPtnIpCrossconnectByTurnnelCuid(BoActionContext actionContext, String turnnelCuid)
    throws Exception
  {
    DboCollection dbos = null;
    if (StringUtils.isNotEmpty(turnnelCuid)) {
      String sql = "select * from PTN_IP_CROSSCONNECT where RELATED_PATH_CUID='" + turnnelCuid + "' order by " + "BELONG_PATH" + ", " + "ORDINAL_IDENTIFIER";
      dbos = super.selectDBOs(sql, new GenericDO[] { new PtnIpCrossconnect() });
    }
    return dbos;
  }

  public DboCollection getPseudoWireByPage(BoQueryContext queryContext, Map paramMap)
    throws Exception
  {
    DboCollection dbos = null;
    if (paramMap != null) {
      long begin = System.currentTimeMillis();
      String isExcel = (String)paramMap.get("isExcel");
      String sql = getPseudoWireQuerySql(paramMap);
      if (StringUtils.isNotEmpty(sql)) {
        LogHome.getLog().info("查询伪线sql=" + sql);
        if ((StringUtils.isNotEmpty(isExcel)) && (isExcel.equals("1")))
          dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
        else {
          dbos = super.selectDBOs(queryContext, sql, new GenericDO[] { new PtnVirtualLine() });
        }
      }
      LogHome.getLog().info("===== 查询伪线用时 " + (System.currentTimeMillis() - begin) + " ms =====");
    }
    return dbos;
  }

  private String getPseudoWireQuerySql(Map paramMap)
  {
    String sql = "";
    if (paramMap != null) {
      String queryType = "";
      if (paramMap.get("queryType") != null) {
        queryType = (String)paramMap.get("queryType");
      }
      if (StringUtils.isNotEmpty(queryType)) {
        if (queryType.equals("ems")) {
          String emsCuid = (String)paramMap.get("emsCuid");
          sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL where PVL.RELATED_EMS_CUID='" + emsCuid + "'";
          String tunnelFdns = "";
          if (paramMap.get("tunnelFdns") != null) {
            tunnelFdns = (String)paramMap.get("tunnelFdns");
            if (StringUtils.isNotEmpty(tunnelFdns)) {
              sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL,TUNNEL_TO_VIRTUAL_LINE T where PVL.RELATED_EMS_CUID='" + emsCuid + "'";
              tunnelFdns = "'" + tunnelFdns.replace(",", "','") + "'";
              sql = sql + " and T.RELATED_TUNNEL_CUID in (" + tunnelFdns + ")";
              sql = sql + " and T.RELATED_VIRTUAL_LINE_CUID = PVL.CUID";
            }
          }
        } else if (queryType.equals("element")) {
          String elementType = (String)paramMap.get("elementType");
          String elementCuids = (String)paramMap.get("elementCuids");
          if ((StringUtils.isNotEmpty(elementType)) && (StringUtils.isNotEmpty(elementCuids))) {
            elementCuids = "'" + elementCuids.replace(",", "','") + "'";
            if (elementType.equals("ne")) {
              sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_NE_TO_SERVICE PNTS where PVL.FDN=PNTS.RELATED_VIRTUALLINE_FDN and PNTS.RELATED_NE_CUID in (" + elementCuids + ")";
            }
            else if (elementType.equals("card")) {
              sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_NE_TO_SERVICE PNTS where PVL.FDN=PNTS.RELATED_VIRTUALLINE_FDN and PNTS.RELATED_CARD_CUID in (" + elementCuids + ")";
            }
            else if (elementType.equals("ptp")) {
              sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_NE_TO_SERVICE PNTS where PVL.FDN=PNTS.RELATED_VIRTUALLINE_FDN and PNTS.RELATED_PTP_CUID in (" + elementCuids + ")";
            }
          }
        }
        else if (queryType.equals("port")) {
          String startPortCuids = "";
          String startNeCuids = "";
          String startCardCuids = "";
          String startPtpCuids = "";
          String endPortCuids = "";
          String endNeCuids = "";
          String endCardCuids = "";
          String endPtpCuids = "";
          if (paramMap.get("startPortCuids") != null) {
            startPortCuids = (String)paramMap.get("startPortCuids");
            Map startPointMap = getPortCuids(startPortCuids);
            if (startPointMap != null) {
              startNeCuids = (String)startPointMap.get("neCuids");
              startCardCuids = (String)startPointMap.get("cardCuids");
              startPtpCuids = (String)startPointMap.get("ptpCuids");
            }
          }
          if (paramMap.get("endPortCuids") != null) {
            endPortCuids = (String)paramMap.get("endPortCuids");
            Map endPointMap = getPortCuids(endPortCuids);
            if (endPointMap != null) {
              endNeCuids = (String)endPointMap.get("neCuids");
              endCardCuids = (String)endPointMap.get("cardCuids");
              endPtpCuids = (String)endPointMap.get("ptpCuids");
            }
          }
          sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL";
          if ((StringUtils.isEmpty(startPortCuids)) && (StringUtils.isNotEmpty(endPortCuids))) {
            sql = sql + " where (PVL.ORIG_NE_CUID in (" + endNeCuids + ") or PVL." + "ORIG_CARD_CUID" + " in (" + endCardCuids + ") or PVL." + "ORIG_PTP_CUID" + " in (" + endPtpCuids + "))" + " or (PVL." + "DEST_PTP_CUID" + " in (" + endNeCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + endCardCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + endPtpCuids + "))";
          }
          else if ((StringUtils.isNotEmpty(startPortCuids)) && (StringUtils.isEmpty(endPortCuids))) {
            sql = sql + " where (PVL.ORIG_NE_CUID in (" + startNeCuids + ") or PVL." + "ORIG_CARD_CUID" + " in (" + startCardCuids + ") or PVL." + "ORIG_PTP_CUID" + " in (" + startPtpCuids + "))" + " or (PVL." + "DEST_PTP_CUID" + " in (" + startNeCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + startCardCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + startPtpCuids + "))";
          }
          else if ((StringUtils.isNotEmpty(startPortCuids)) && (StringUtils.isNotEmpty(endPortCuids))) {
            sql = sql + " where (((PVL.ORIG_NE_CUID in (" + startNeCuids + ") or PVL." + "ORIG_CARD_CUID" + " in (" + startCardCuids + ") or PVL." + "ORIG_PTP_CUID" + " in (" + startPtpCuids + "))" + " and (PVL." + "DEST_PTP_CUID" + " in (" + endNeCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + endCardCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + endPtpCuids + ")))" + " or ((PVL." + "ORIG_NE_CUID" + " in (" + endNeCuids + ") or PVL." + "ORIG_CARD_CUID" + " in (" + endCardCuids + ") or PVL." + "ORIG_PTP_CUID" + " in (" + endPtpCuids + "))" + " and (PVL." + "DEST_PTP_CUID" + " in (" + startNeCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + startCardCuids + ") or PVL." + "DEST_PTP_CUID" + " in (" + startPtpCuids + "))))";
          }
          else
          {
            return "";
          }
        } else if (queryType.equals("tunnel")) {
          String tunnelFdn = (String)paramMap.get("tunnelFdn");
          sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, TUNNEL_TO_VIRTUAL_LINE TTVL, PTN_TURNNEL PT where TTVL.RELATED_VIRTUAL_LINE_CUID=PVL.CUID and TTVL.RELATED_TUNNEL_CUID=PT.CUID and PT.FDN='" + tunnelFdn + "'";
        }
        else if (queryType.equals("service")) {
          String serviceType = (String)paramMap.get("serviceType");
          String serviceFdn = (String)paramMap.get("serviceFdn");
          if ("ethernet".equals(serviceType)) {
            sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_TP_PAIR PP,PTN_ETH PE where PE.CUID=PP.RELATED_SERVICE_CUID AND PVL.CUID = PP.RELATED_VIRTUAL_LINE_CUID and PE.FDN='" + serviceFdn + "'";
          }
          else if ("tdm".equals(serviceType)) {
            sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_TDM PT where PT.PTN_VL_FDN=PVL.FDN and PT.FDN='" + serviceFdn + "'";
          }
          else if ("atm".equals(serviceType)) {
            sql = "select distinct PVL.* from PTN_VIRTUAL_LINE PVL, PTN_ATM PA where PA.PTN_VL_FDN=PVL.FDN and PA.FDN='" + serviceFdn + "'";
          }
          else {
            return "";
          }
        }
        String orderFieldString = "";
        if (paramMap.get("orderFieldString") != null) {
          orderFieldString = (String)paramMap.get("orderFieldString");
        }
        if (StringUtils.isNotEmpty(orderFieldString))
          sql = sql + " order by PVL." + orderFieldString;
        else {
          sql = sql + " order by PVL.NATINE_EMS_NAME";
        }
      }
    }
    return sql;
  }

  private Map<String, String> getPortCuids(String portCuids)
  {
    Map map = new HashMap();
    String neCuids = "";
    String cardCuids = "";
    String ptpCuids = "";
    if (StringUtils.isNotEmpty(portCuids)) {
      String[] portCuidArr = portCuids.split(",");
      if (portCuidArr != null) {
        for (int i = 0; i < portCuidArr.length; i++) {
          if (StringUtils.isNotEmpty(portCuidArr[i])) {
            if (portCuidArr[i].startsWith("TRANS_ELEMENT")) {
              if (StringUtils.isEmpty(neCuids))
                neCuids = "'" + portCuidArr[i] + "'";
              else
                neCuids = neCuids + ",'" + portCuidArr[i] + "'";
            }
            else if (portCuidArr[i].startsWith("CARD")) {
              if (StringUtils.isEmpty(cardCuids))
                cardCuids = "'" + portCuidArr[i] + "'";
              else
                cardCuids = cardCuids + ",'" + portCuidArr[i] + "'";
            }
            else if (portCuidArr[i].startsWith("PTP")) {
              if (StringUtils.isEmpty(ptpCuids))
                ptpCuids = "'" + portCuidArr[i] + "'";
              else {
                ptpCuids = ptpCuids + ",'" + portCuidArr[i] + "'";
              }
            }
          }
        }
      }
      if (StringUtils.isEmpty(neCuids)) {
        neCuids = "'null'";
      }
      if (StringUtils.isEmpty(cardCuids)) {
        cardCuids = "'null'";
      }
      if (StringUtils.isEmpty(ptpCuids)) {
        ptpCuids = "'null'";
      }
      map.put("neCuids", neCuids);
      map.put("cardCuids", cardCuids);
      map.put("ptpCuids", ptpCuids);
    }
    return map;
  }

  public PtnVirtualLine getPseudoWireByFdn(String pseudoWireFdn)
    throws Exception
  {
    PtnVirtualLine ptnVirtualLine = null;
    if (StringUtils.isNotEmpty(pseudoWireFdn)) {
      String sql = "select * from PTN_VIRTUAL_LINE where FDN='" + pseudoWireFdn + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0)) {
        ptnVirtualLine = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", 0);
      }
    }
    return ptnVirtualLine;
  }

  public DataObjectList getPseudoWireListByFdn(String fdn) throws Exception {
    DataObjectList list = new DataObjectList();
    if (StringUtils.isNotEmpty(fdn)) {
      String sql = "SELECT * FROM PTN_VIRTUAL_LINE WHERE FDN ='" + fdn + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          PtnVirtualLine ptnVirtualLine = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", i);
          list.add(ptnVirtualLine);
        }
      }
    }
    return list;
  }

  public DataObjectList getPtnVirtualVitaeByFdn(String fdn)
    throws Exception
  {
    DataObjectList list = new DataObjectList();
    if (StringUtils.isNotEmpty(fdn)) {
      String sql = "SELECT * FROM PTN_VIRTUAL_LINE WHERE FDN ='" + fdn + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          PtnVirtualLine ptnVirtualLine = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", i);
          list.add(ptnVirtualLine);
        }
      }
    }
    return list;
  }

  public DataObjectList getPseudoWireByTunnelFdn(String tunnelFdn)
    throws Exception
  {
    DataObjectList dbos = null;

    PtnTurnnel tunnel = getPtnTurnnelByFdn(tunnelFdn);
    if (tunnel != null) {
      DboCollection pwDbos = getPseudoWireByTunnelCuid(tunnel.getCuid());
      if ((pwDbos != null) && (pwDbos.size() > 0)) {
        dbos = new DataObjectList();
        for (int i = 0; i < pwDbos.size(); i++) {
          PtnVirtualLine ptnVirtualLine = (PtnVirtualLine)pwDbos.getAttrField("PTN_VIRTUAL_LINE", i);
          dbos.add(ptnVirtualLine);
        }
      }
    }
    return dbos;
  }

  public PtnVirtualLine getPtnVirtualLineByTunnelFdn(String tunnelFdn) throws Exception {
    PtnVirtualLine ptnVirtualLine = null;

    PtnTurnnel tunnel = getPtnTurnnelByFdn(tunnelFdn);
    if (tunnel != null) {
      DboCollection pwDbos = getPseudoWireByTunnelCuid(tunnel.getCuid());
      if ((pwDbos != null) && (pwDbos.size() > 0)) {
        for (int i = 0; i < pwDbos.size(); i++) {
          ptnVirtualLine = (PtnVirtualLine)pwDbos.getAttrField("PTN_VIRTUAL_LINE", i);
        }
      }
    }

    return ptnVirtualLine;
  }

  public DboCollection getPseudoWireByTunnelCuid(String tunnelCuid) throws Exception {
    DboCollection dbos = null;
    if (StringUtils.isNotEmpty(tunnelCuid)) {
      String sql = "select PVL.* from PTN_VIRTUAL_LINE PVL, TUNNEL_TO_VIRTUAL_LINE TTVL where TTVL.RELATED_VIRTUAL_LINE_CUID=PVL.CUID and TTVL.RELATED_TUNNEL_CUID='" + tunnelCuid + "'";
      dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
    }
    return dbos;
  }

  public PtnVirtualLine getPtnVirtualLineByTunnelCuid(String cuid)
    throws Exception
  {
    PtnVirtualLine ptnVirtualLine = null;
    if (StringUtils.isNotEmpty(cuid)) {
      String sql = "select PVL.* from PTN_VIRTUAL_LINE PVL, TUNNEL_TO_VIRTUAL_LINE TTVL where TTVL.RELATED_VIRTUAL_LINE_CUID=PVL.CUID and TTVL.RELATED_TUNNEL_CUID='" + cuid + "'";
      DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnVirtualLine() });
      if ((dbos != null) && (dbos.size() > 0))
      {
        ptnVirtualLine = (PtnVirtualLine)dbos.getAttrField("PTN_VIRTUAL_LINE", 0);
      }
    }

    return ptnVirtualLine;
  }

  public DataObjectList getServiceByPseudoWireFdn(String pseudoWireFdn)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(pseudoWireFdn)) {
      String sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
      dbos = super.getObjectsBySql(sql, new PtnEth(), 0);
      if ((dbos == null) || (dbos.size() == 0)) {
        sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
        dbos = super.getObjectsBySql(sql, new PtnTdm(), 0);
        if ((dbos == null) || (dbos.size() == 0)) {
          sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
          dbos = super.getObjectsBySql(sql, new PtnAtm(), 0);
        }
      }
    }
    return dbos;
  }

  public DataObjectList getServiceByPseudoWireFdnList(String pseudoWireFdn, String type) throws Exception
  {
    DataObjectList dbos = null;
    if ((StringUtils.isNotEmpty(pseudoWireFdn)) && (type.equals("ptnEth"))) {
      String sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
      dbos = super.getObjectsBySql(sql, new PtnEth(), 0);
      if (type.equals("ptnTdm")) {
        sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
        dbos = super.getObjectsBySql(sql, new PtnTdm(), 0);
        if ((dbos == null) || (dbos.size() == 0)) {
          sql = "PTN_VL_FDN='" + pseudoWireFdn + "'";
          dbos = super.getObjectsBySql(sql, new PtnAtm(), 0);
        }
      }
    }
    return dbos;
  }

  public PtnEth getPtnEthByCuid(BoActionContext actionContext, String cuid)
    throws Exception
  {
    PtnEth dbo = new PtnEth();
    dbo.setCuid(cuid);
    dbo = (PtnEth)super.getObjByCuid(dbo);
    return dbo;
  }

  public PtnTdm getPtnTdmByCuid(BoActionContext actionContext, String cuid)
    throws Exception
  {
    PtnTdm dbo = new PtnTdm();
    dbo.setCuid(cuid);
    dbo = (PtnTdm)super.getObjByCuid(dbo);
    return dbo;
  }

  public PtnAtm getPtnAtmByCuid(BoActionContext actionContext, String cuid)
    throws Exception
  {
    PtnAtm dbo = new PtnAtm();
    dbo.setCuid(cuid);
    dbo = (PtnAtm)super.getObjByCuid(dbo);
    return dbo;
  }

  public String getPtnPortBandingByTpCuids(String tpCuids)
    throws Exception
  {
    String physicalPtpCuids = "";
    if (StringUtils.isNotEmpty(tpCuids)) {
      tpCuids = "'" + tpCuids.replace(",", "','") + "'";
      String sql = "SV_TP_CUID in (" + tpCuids + ")";
      DataObjectList dbos = super.getObjectsBySql(sql, new PtnPortBanding(), 0);
      if ((dbos != null) && (dbos.size() > 0)) {
        for (int i = 0; i < dbos.size(); i++) {
          PtnPortBanding ptnPortBanding = (PtnPortBanding)dbos.get(i);
          if (StringUtils.isEmpty(physicalPtpCuids))
            physicalPtpCuids = ptnPortBanding.getPhysicalTpCuid();
          else {
            physicalPtpCuids = physicalPtpCuids + "," + ptnPortBanding.getPhysicalTpCuid();
          }
        }
      }
    }
    return physicalPtpCuids;
  }

  public DataObjectList getTunnelByEmsAndTunnelName(BoActionContext actionContext, String emsCuid, String tunnelName)
    throws Exception
  {
    DataObjectList dbos = null;
    String sql = "";
    if (StringUtils.isNotEmpty(emsCuid)) {
      sql = " AND RELATED_EMS_CUID='" + emsCuid + "'";
    }
    if (StringUtils.isNotEmpty(tunnelName)) {
      sql = " AND NATIVE_EMS_NAME like '%" + tunnelName + "%'";
    }
    if (!sql.equals("")) {
      sql = sql.substring(4, sql.length());
    }
    dbos = super.getObjectsBySql(sql, new PtnTurnnel(), 0);
    return dbos;
  }

  public DataObjectList getPtnVirtualLineByEMSAndFdn(BoActionContext actionContext, Map param)
    throws Exception
  {
    DataObjectList dbos = null;
    String sql = "";
    String emsCuids = (String)param.get("emsCuid");
    String tunnelFnds = (String)param.get("tunnelFnds");
    String pseudoWireName = (String)param.get("pseudoWireName");
    if (StringUtils.isNotEmpty(emsCuids)) {
      sql = sql + " AND " + "RELATED_EMS_CUID" + "='" + emsCuids + "'";
    }
    if (StringUtils.isNotEmpty(tunnelFnds)) {
      tunnelFnds = tunnelFnds.replaceAll(",", "','");
      sql = sql + " AND " + "RELATED_LSP_FDN" + " IN ('" + tunnelFnds + "')";
    }
    if (StringUtils.isNotEmpty(pseudoWireName)) {
      sql = sql + " AND " + "NATINE_EMS_NAME" + " LIKE '%" + pseudoWireName + "%'";
    }
    if (!sql.equals("")) {
      sql = sql.substring(4, sql.length());
    }
    dbos = super.getObjectsBySql(sql, new PtnVirtualLine(), 0);
    return dbos;
  }

  private void setTraphInfByPtnServiceFdn(DboCollection PtnS, GenericDO dbo)
    throws Exception
  {
    DataObjectList dbos = null;
    String fdns = "";
    if ((PtnS != null) && (PtnS.size() > 0)) {
      for (int i = 0; i < PtnS.size(); i++) {
        GenericDO gd = (GenericDO)PtnS.getAttrField(dbo.getClassName(), i);
        fdns = fdns + ",'" + gd.getAttrString("PTN_VL_FDN") + "'";
      }
      if (StringUtils.isNotEmpty(fdns)) {
        fdns = fdns.substring(1);
        String sql = "SELECT T.DISPATCH_NAME,T.LABEL_CN ,L.FDN FROM PTN_PATH P, TRAPH T,PTN_VIRTUAL_LINE L   WHERE P.RELATED_ROUTE_CUID = T.CUID  AND L.CUID = P.RELATED_VIRTUAL_LINE_CUID AND L.FDN IN(" + fdns + ")";

        dbos = super.selectDBOs(sql, new Class[] { String.class, String.class, String.class });
        for (int i = 0; i < PtnS.size(); i++) {
          GenericDO gd = (GenericDO)PtnS.getAttrField(dbo.getClassName(), i);
          String sfdn = gd.getAttrString("PTN_VL_FDN");
          gd.setAttrValue("dispatchName", "");
          gd.setAttrValue("traphName", "");
          for (int j = 0; j < dbos.size(); j++) {
            GenericDO vldbo = (GenericDO)dbos.get(j);
            String lfdn = vldbo.getAttrString("3");
            if ((StringUtils.isNotEmpty(sfdn)) && (sfdn.equals(lfdn))) {
              gd.setAttrValue("dispatchName", vldbo.getAttrString("1"));
              gd.setAttrValue("traphName", vldbo.getAttrString("2"));
            }
          }
        }
      }
    }
  }

  public DboCollection getPtnServiceByPath(BoQueryContext queryContext, String orderString, String emsCuid, String tunnelFdns, String pseudoWireFdn, GenericDO dbo) throws Exception
  {
    DboCollection dbos = null;
    String sql = "SELECT S.* FROM " + dbo.getClassName() + " S " + " WHERE 1=1 ";
    if (StringUtils.isNotEmpty(emsCuid)) {
      sql = sql + " AND  S.RELATED_EMS_CUID='" + emsCuid + "'";
    }
    String link = " AND ";
    if (StringUtils.isNotEmpty(tunnelFdns)) {
      tunnelFdns = tunnelFdns.replaceAll(",", "','");
      sql = sql + " AND  S.PTN_VL_FDN IN (SELECT " + "FDN" + " FROM " + "PTN_VIRTUAL_LINE" + " WHERE " + "RELATED_LSP_FDN" + " IN ('" + tunnelFdns + "'))";
      link = " OR ";
    }
    if (StringUtils.isNotEmpty(pseudoWireFdn)) {
      pseudoWireFdn = pseudoWireFdn.replaceAll(",", "','");
      sql = sql + link + "  S.PTN_VL_FDN IN('" + pseudoWireFdn + "')";
    }
    if (StringUtils.isNotEmpty(orderString))
      sql = sql + " ORDER BY  S." + orderString + "";
    else {
      sql = sql + " ORDER BY  S.NATIVE_EMS_NAME";
    }
    DboCollection rt = super.selectDBOs(queryContext, sql, new GenericDO[] { dbo });
    setTraphInfByPtnServiceFdn(rt, dbo);
    return rt;
  }

  public DboCollection getPtnServiceByEquipCuid(BoQueryContext queryContext, String orderString, String equipCuid, GenericDO dbo)
    throws Exception
  {
    DboCollection dbos = null;
    String sql = "SELECT distinct S.* FROM " + dbo.getClassName() + " S," + "PTN_NE_TO_SERVICE" + " P WHERE P." + "RELATED_TUNNEL_FDN" + " = S.FDN ";
    if (StringUtils.isNotEmpty(equipCuid)) {
      equipCuid = equipCuid.replaceAll(",", "','");
      if (equipCuid.indexOf("TRANS_ELEMENT") != -1)
        sql = sql + " AND P." + "RELATED_NE_CUID" + " IN('" + equipCuid + "')";
      else if (equipCuid.indexOf("CARD") != -1)
        sql = sql + " AND P." + "RELATED_CARD_CUID" + " IN('" + equipCuid + "')";
      else if (equipCuid.indexOf("PTP") != -1) {
        sql = sql + " AND P." + "RELATED_PTP_CUID" + " IN('" + equipCuid + "')";
      }
    }
    if (StringUtils.isNotEmpty(orderString))
      sql = sql + " ORDER BY S." + orderString + "";
    else {
      sql = sql + " ORDER BY S.NATIVE_EMS_NAME";
    }
    DboCollection rt = super.selectDBOs(queryContext, sql, new GenericDO[] { dbo });
    setTraphInfByPtnServiceFdn(rt, dbo);
    return rt;
  }

  public DboCollection getPtnServiceByOrdePort(BoQueryContext queryContext, String orderString, String emsCuid, String origCuid, String destCuid, GenericDO dbo)
    throws Exception
  {
    String sql = "SELECT distinct * FROM " + dbo.getClassName() + " S WHERE 1=1 ";
    String startNeCuids = "";
    String startCardCuids = "";
    String startPtpCuids = "";

    String endNeCuids = "";
    String endCardCuids = "";
    String endPtpCuids = "";

    if (StringUtils.isNotEmpty(origCuid)) {
      Map startPointMap = getPortCuids(origCuid);
      if (startPointMap != null) {
        startNeCuids = (String)startPointMap.get("neCuids");
        startCardCuids = (String)startPointMap.get("cardCuids");
        startPtpCuids = (String)startPointMap.get("ptpCuids");
      }
    }
    if (StringUtils.isNotEmpty(destCuid)) {
      Map endPointMap = getPortCuids(destCuid);
      if (endPointMap != null) {
        endNeCuids = (String)endPointMap.get("neCuids");
        endCardCuids = (String)endPointMap.get("cardCuids");
        endPtpCuids = (String)endPointMap.get("ptpCuids");
      }
    }
    if ((StringUtils.isEmpty(origCuid)) && (StringUtils.isNotEmpty(destCuid))) {
      sql = sql + " and (S.ORIG_NE_CUID in (" + endNeCuids + ") or S.ORIG_CARD_CUID in (" + endCardCuids + ") or S.ORIG_PTP_CUID in (" + endPtpCuids + "))" + " or (S.DEST_NE_CUID in (" + endNeCuids + ") or S.DEST_CARD_CUID in (" + endCardCuids + ") or S.DEST_PTP_CUID in (" + endPtpCuids + "))";
    }
    else if ((StringUtils.isNotEmpty(origCuid)) && (StringUtils.isEmpty(destCuid))) {
      sql = sql + " and (S.ORIG_NE_CUID in (" + startNeCuids + ") or S.ORIG_CARD_CUID in (" + startCardCuids + ") or S.ORIG_PTP_CUID in (" + startPtpCuids + "))" + " or (S.DEST_NE_CUID in (" + startNeCuids + ") or S.DEST_CARD_CUID in (" + startCardCuids + ") or S.DEST_PTP_CUID in (" + startPtpCuids + "))";
    }
    else if ((StringUtils.isNotEmpty(origCuid)) && (StringUtils.isNotEmpty(destCuid))) {
      sql = sql + " and ((S.ORIG_NE_CUID in (" + startNeCuids + ") or S.ORIG_CARD_CUID in (" + startCardCuids + ") or S.ORIG_PTP_CUID in (" + startPtpCuids + "))" + " and (S.DEST_NE_CUID in (" + endNeCuids + ") or S.DEST_CARD_CUID in (" + endCardCuids + ") or S.DEST_PTP_CUID in (" + endPtpCuids + ")))" + " or ((S.ORIG_NE_CUID in (" + endNeCuids + ") or S.ORIG_CARD_CUID in (" + endCardCuids + ") or S.ORIG_PTP_CUID in (" + endPtpCuids + "))" + " and (S.DEST_NE_CUID in (" + startNeCuids + ") or S.DEST_CARD_CUID in (" + startCardCuids + ") or S.DEST_PTP_CUID in (" + startPtpCuids + ")))";
    }

    if (StringUtils.isNotEmpty(orderString))
      sql = sql + " ORDER BY S." + orderString + "";
    else {
      sql = sql + " ORDER BY S.NATIVE_EMS_NAME";
    }
    DboCollection rt = super.selectDBOs(queryContext, sql, new GenericDO[] { dbo });
    setTraphInfByPtnServiceFdn(rt, dbo);
    return rt;
  }

  public DboCollection getPtnPathByTraph(BoActionContext actionContext, String traphCuid)
    throws Exception
  {
    DboCollection dbos = new DboCollection();
    if (StringUtils.isNotEmpty(traphCuid)) {
      if (traphCuid.startsWith("TRAPH")) {
        String sql = "SELECT pp.* FROM TRAPH_ROUTE_TO_PATH trtp,TRAPH_ROUTE tr,PTN_PATH pp WHERE trtp.TRAPH_ROUTE_CUID=tr.CUID AND trtp.PATH_CUID=pp.CUID AND tr.RELATED_SERVICE_CUID='" + traphCuid + "'";

        dbos = super.selectDBOs(sql, new GenericDO[] { new PtnPath() });
      } else if (traphCuid.startsWith("ATTEMP_TRAPH")) {
        String sql = "SELECT app.* FROM ATTTRAPH_ROUTE_TO_PATH artp,ATTEMP_TRAPH_ROUTE atr,ATTEMP_PTN_PATH app WHERE artp.TRAPH_ROUTE_CUID=atr.CUID AND artp.PATH_CUID=app.CUID AND atr.RELATED_SERVICE_CUID='" + traphCuid + "'";

        dbos = super.selectDBOs(sql, new GenericDO[] { new AttempPtnPath() });
      }
    }
    return dbos;
  }

  public DataObjectList getTraphByVLCuid(BoActionContext actionContext, String virCuid)
    throws Exception
  {
    DataObjectList dbos = new DataObjectList();
    String sql = "SELECT T.CUID, T.LABEL_CN  FROM TRAPH T, PTN_PATH P  WHERE P.RELATED_VIRTUAL_LINE_CUID = '" + virCuid + "' " + " AND P." + "RELATED_ROUTE_CUID" + " = T." + "CUID";

    DataObjectList list = super.selectDBOs(sql, new Class[] { String.class, String.class });
    if ((null != list) && (!list.isEmpty()))
    {
      dbos = list;
    }
    return dbos;
  }

  public DboCollection getTraphByPtnVir(BoActionContext actionContext, String virCuid)
    throws Exception
  {
    DboCollection dbos = new DboCollection();
    if (StringUtils.isNotEmpty(virCuid)) {
      String sql = "SELECT RELATED_ROUTE_CUID FROM PTN_PATH WHERE RELATED_VIRTUAL_LINE_CUID='" + virCuid + "'";
      dbos = super.selectDBOs(sql, new GenericDO[] { new PtnPath() });
    }
    return dbos;
  }

  public DataObjectList getPortBandByPtpCuid(BoActionContext actionContext, String ptpCuid) throws Exception
  {
    String sql = "PHYSICAL_TP_CUID = '" + ptpCuid + "'";
    DataObjectList dbo = super.getObjectsBySql(sql, new PtnPortBanding(), 0);
    return dbo;
  }

  public Ptp getPtpByCuid(BoActionContext actionContext, String ptpCuid) throws Exception {
    GenericDO ptp = new Ptp();
    if ((DaoHelper.isNotEmpty(ptpCuid)) && (ptpCuid.indexOf("PTP_NA") != -1)) {
      ptp = new PtpNa();
    }
    ptp.setCuid(ptpCuid);
    return (Ptp)super.getObjByCuid(ptp);
  }

  public DataObjectList getPtnPortBandingByPtpCuids(String ptpCuids)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(ptpCuids)) {
      ptpCuids = "'" + ptpCuids.replace(",", "','") + "'";
      String sql = "SV_TP_CUID in (" + ptpCuids + ") or " + "PHYSICAL_TP_CUID" + " in (" + ptpCuids + ")";
      dbos = super.getObjectsBySql(sql, new PtnPortBanding(), 0);
    }
    return dbos;
  }

  public DboCollection getLayerRateMap()
    throws Exception
  {
    DboCollection dbos = null;
    String sql = "select * from LAYER_RATE";
    dbos = super.selectDBOs(sql, new GenericDO[] { new LayerRate() });
    return dbos;
  }

  public DataObjectList getTurnnelToVirtualLineByPW(String pwCuid)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(pwCuid)) {
      pwCuid = "'" + pwCuid.replace(",", "','") + "'";
      String sql = "RELATED_VIRTUAL_LINE_CUID in (" + pwCuid + ")";
      dbos = super.getObjectsBySql(sql, new TunnelToVirtualLine(), 0);
    }
    return dbos;
  }

  public DataObjectList getPtnServiceByPath(PtnPath ptnPath)
    throws Exception
  {
    DataObjectList serviceList = new DataObjectList();
    if (ptnPath != null) {
      DataObjectList tmpServiceList = getPtnServiceByPhyPort(ptnPath.getRelatedAPtpCuid(), ptnPath.getRelatedZPtpCuid());
      if ((tmpServiceList != null) && (tmpServiceList.size() > 0)) {
        serviceList.addAll(tmpServiceList);
      }
      tmpServiceList = getPtnServiceByPhyPort(ptnPath.getRelatedAPtpCuid2(), ptnPath.getRelatedZPtpCuid2());
      if ((tmpServiceList != null) && (tmpServiceList.size() > 0)) {
        serviceList.addAll(tmpServiceList);
      }
    }
    return serviceList;
  }

  public DataObjectList getPtnServiceByPhyPort(String origPtpCuid, String destPtpCuid)
    throws Exception
  {
    DataObjectList serviceList = new DataObjectList();
    String orig = "";
    String dest = "";
    if ((StringUtils.isNotEmpty(origPtpCuid)) && (StringUtils.isNotEmpty(destPtpCuid))) {
      serviceList = getPtnServiceByPtpCuid(origPtpCuid, destPtpCuid);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        return serviceList;
      }
      DataObjectList portBandList = getPtnPortBandingByPtpCuids(origPtpCuid + "," + destPtpCuid);
      HashMap map = new HashMap();
      if (portBandList != null) {
        for (int i = 0; i < portBandList.size(); i++) {
          PtnPortBanding portBand = (PtnPortBanding)portBandList.get(i);
          map.put(portBand.getCuid(), portBand.getCuid());
          if (origPtpCuid.equals(portBand.getSvTpCuid())) {
            origPtpCuid = portBand.getPhysicalTpCuid() + "," + portBand.getSvTpCuid();
            orig = portBand.getPhysicalTpCuid();
          } else if (origPtpCuid.equals(portBand.getPhysicalTpCuid())) {
            origPtpCuid = portBand.getPhysicalTpCuid() + "," + portBand.getSvTpCuid();
            orig = portBand.getSvTpCuid();
          } else if (destPtpCuid.equals(portBand.getSvTpCuid())) {
            destPtpCuid = portBand.getPhysicalTpCuid() + "," + portBand.getSvTpCuid();
            dest = portBand.getPhysicalTpCuid();
          } else if (destPtpCuid.equals(portBand.getPhysicalTpCuid())) {
            destPtpCuid = portBand.getPhysicalTpCuid() + "," + portBand.getSvTpCuid();
            dest = portBand.getSvTpCuid();
          }
        }
        serviceList = getPtnServiceByPtpCuid(origPtpCuid, destPtpCuid);
        if ((serviceList != null) && (serviceList.size() > 0)) {
          return serviceList;
        }
        portBandList = getPtnPortBandingByPtpCuids(orig + "," + dest);
        if (portBandList != null) {
          for (int i = 0; i < portBandList.size(); i++) {
            PtnPortBanding portBand = (PtnPortBanding)portBandList.get(i);
            if (map.containsKey(portBand.getCuid()))
            {
              continue;
            }

            if (origPtpCuid.indexOf(portBand.getSvTpCuid()) >= 0)
              origPtpCuid = origPtpCuid + "," + portBand.getPhysicalTpCuid();
            else if (origPtpCuid.indexOf(portBand.getPhysicalTpCuid()) >= 0)
              origPtpCuid = origPtpCuid + "," + portBand.getSvTpCuid();
            else if (destPtpCuid.indexOf(portBand.getSvTpCuid()) >= 0)
              destPtpCuid = destPtpCuid + "," + portBand.getPhysicalTpCuid();
            else if (destPtpCuid.indexOf(portBand.getPhysicalTpCuid()) >= 0) {
              destPtpCuid = destPtpCuid + "," + portBand.getSvTpCuid();
            }
          }
        }

        serviceList = getPtnServiceByPtpCuid(origPtpCuid, destPtpCuid);
        return serviceList;
      }
    }

    return serviceList;
  }

  private DataObjectList getPtnServiceByPtpCuid(String origPtpCuids, String destPtpCuids) throws Exception
  {
    DataObjectList serviceList = new DataObjectList();
    if ((StringUtils.isNotEmpty(origPtpCuids)) && (StringUtils.isNotEmpty(destPtpCuids))) {
      origPtpCuids = "'" + origPtpCuids.replace(",", "','") + "'";
      destPtpCuids = "'" + destPtpCuids.replace(",", "','") + "'";
      String sql = "(ORIG_PTP_CUID in (" + origPtpCuids + ") and " + "DEST_PTP_CUID" + " in (" + destPtpCuids + ")) or (" + "DEST_PTP_CUID" + " in (" + origPtpCuids + ") and " + "ORIG_PTP_CUID" + " in (" + destPtpCuids + "))";

      serviceList = super.getObjectsBySql(sql, new PtnEth(), 0);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        return serviceList;
      }
      sql = "(ORIG_PTP_CUID in (" + origPtpCuids + ") and " + "DEST_PTP_CUID" + " in (" + destPtpCuids + ")) or (" + "DEST_PTP_CUID" + " in (" + origPtpCuids + ") and " + "ORIG_PTP_CUID" + " in (" + destPtpCuids + "))";

      serviceList = super.getObjectsBySql(sql, new PtnTdm(), 0);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        return serviceList;
      }
      sql = "(ORIG_PTP_CUID in (" + origPtpCuids + ") and " + "DEST_PTP_CUID" + " in (" + destPtpCuids + ")) or (" + "DEST_PTP_CUID" + " in (" + origPtpCuids + ") and " + "ORIG_PTP_CUID" + " in (" + destPtpCuids + "))";

      serviceList = super.getObjectsBySql(sql, new PtnAtm(), 0);
      if ((serviceList != null) && (serviceList.size() > 0)) {
        return serviceList;
      }
      sql = "(ORIG_PTP_CUID in (" + origPtpCuids + ") and " + "DEST_PTP_CUID" + " in (" + destPtpCuids + ")) or (" + "DEST_PTP_CUID" + " in (" + origPtpCuids + ") and " + "ORIG_PTP_CUID" + " in (" + destPtpCuids + "))";

      serviceList = super.getObjectsBySql(sql, new PtnTpPair(), 0);
    }
    return serviceList;
  }

  public DataObjectList getTunnelRouteByTurnnelCuid(BoActionContext actionContext, String turnnelCuid)
    throws Exception
  {
    DataObjectList dbos = null;
    if (StringUtils.isNotEmpty(turnnelCuid)) {
      String sql = "RELATED_PATH_CUID='" + turnnelCuid + "' order by " + "BELONG_PATH" + ", " + "ORDINAL_IDENTIFIER";
      dbos = super.getObjectsBySql(sql, new PtnIpCrossconnect(), 0);
    }
    return dbos;
  }

  public DboCollection getPTPByCuids(BoQueryContext queryContext, String cuids) throws Exception
  {
    String[] cuid = cuids.split(",");
    String cuidss = new String();
    for (int i = 0; i < cuid.length; i++) {
      cuidss = cuidss + ",'" + cuid[i] + "'";
    }
    String sql = "select * from PTP where CUID in (" + cuidss.substring(1, cuidss.length()) + ") and " + "PORT_TYPE" + " !=4";
    sql = sql + " order by " + "LABEL_CN" + "";
    return super.selectDBOs(queryContext, sql, new GenericDO[] { new Ptp() });
  }

  public DataObjectList getAllBackBone(BoQueryContext queryContext) throws Exception {
    DataObjectList dbos = null;
    String sql = "select T.LABEL_CN,B.RELATED_NET_CUID,B.RELATED_NE_CUID FROM BACKBONE_COLLECT B,TRANS_ELEMENT T WHERE  B.RELATED_NE_CUID =T.CUID AND B.PROPERTY=1";

    Class[] type = { String.class, String.class, String.class };
    return super.selectDBOs(queryContext, sql, type);
  }

  public DataObjectList getBackBoneBySubNet(BoQueryContext queryContext, String netCuid) throws Exception {
    DataObjectList dbos = null;
    String sql = "select T.LABEL_CN,B.RELATED_NE_CUID FROM BACKBONE_COLLECT B,TRANS_ELEMENT T WHERE  B.RELATED_NE_CUID =T.CUID AND B.PROPERTY=1 AND B.RELATED_NET_CUID ='" + netCuid + "'";

    Class[] type = { String.class, String.class };
    return super.selectDBOs(queryContext, sql, type);
  }

  public BackboneCollect getBackBoneByNetAndNe(BoActionContext actionContext, String subNetCuid, String elementCuid) throws Exception {
    String sql = "RELATED_NET_CUID = '" + subNetCuid + "'" + " and " + "RELATED_NE_CUID" + " = '" + elementCuid + "'";

    DataObjectList dbo = super.getObjectsBySql(sql, new BackboneCollect(), 0);
    BackboneCollect bone = null;
    if ((dbo != null) && (dbo.size() > 0)) {
      bone = (BackboneCollect)dbo.get(0);
    }
    return bone;
  }

  public DataObjectList getBackBoneByPage(BoQueryContext queryContext, String orderString, String relatedEMSCuids, String queryRelatedNetCuid, String labelCn, Long boneProperty) throws Exception {
    String sql = "select B.OBJECTID,T.LABEL_CN,B.PROPERTY,B.RELATED_GROUP,TS.LABEL_CN FROM BACKBONE_COLLECT B,TRANS_ELEMENT T,TRANS_SUB_NETWORK TS WHERE  B.RELATED_NE_CUID =T.CUID AND B.RELATED_NET_CUID =TS.CUID";

    if (DaoHelper.isNotEmpty(relatedEMSCuids)) {
      relatedEMSCuids = relatedEMSCuids.replace(",", "','");
      sql = sql + " AND T." + "RELATED_EMS_CUID" + " IN ('" + relatedEMSCuids + "')";
    }

    if (DaoHelper.isNotEmpty(queryRelatedNetCuid)) {
      queryRelatedNetCuid = queryRelatedNetCuid.replace(",", "','");
      sql = sql + " AND B." + "RELATED_NET_CUID" + " IN ('" + queryRelatedNetCuid + "')";
    }

    if (DaoHelper.isNotEmpty(labelCn)) {
      sql = sql + " AND T." + "LABEL_CN" + " LIKE '%" + labelCn + "%'";
    }

    if (boneProperty.longValue() != -1L) {
      sql = sql + " AND B." + "PROPERTY" + " = " + boneProperty;
    }

    if (DaoHelper.isNotEmpty(orderString)) {
      sql = sql + " ORDER BY B." + orderString;
    }

    Class[] type = { String.class, String.class, Long.TYPE, Long.TYPE, String.class };
    return super.selectDBOs(queryContext, sql, type);
  }

  public void delBackBone(BoActionContext actionContext, Long objectId) throws Exception {
    BackboneCollect dbo = new BackboneCollect();
    dbo.setObjectNum(objectId.longValue());
    super.deleteObject(actionContext, dbo);
  }

  public DboCollection getPtnTunnelByVLCuid(String vlCuid) throws Exception {
    String sql = "select PT.* from PTN_TURNNEL PT, TUNNEL_TO_VIRTUAL_LINE TTVL where TTVL.RELATED_TUNNEL_CUID=PT.CUID and TTVL.RELATED_VIRTUAL_LINE_CUID='" + vlCuid + "'";

    return super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
  }

  public DataObjectList getPtnTunnelByVLCuidList(String vlCuid) throws Exception {
    DataObjectList dbos = null;
    String sql = "SELECT DISTINCT PT.CUID, PT.LABEL_CN  FROM PTN_TURNNEL PT, TUNNEL_TO_VIRTUAL_LINE TTVL where TTVL.RELATED_TUNNEL_CUID=PT.CUID and TTVL.RELATED_VIRTUAL_LINE_CUID IN (" + 
      vlCuid + ")";
    DboCollection list = super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
    if ((list != null) && (list.size() > 0)) {
      dbos = new DataObjectList();
      for (int i = 0; i < list.size(); i++) {
        PtnTurnnel ptnTurnnel = (PtnTurnnel)list.getAttrField("PTN_TURNNEL", i);
        dbos.add(ptnTurnnel);
      }
    }
    return dbos;
  }

  public DataObjectList isTunnelHasTraph(BoActionContext actionContext, ArrayList<String> tunnelList)
    throws Exception
  {
    String sql = "";
    if (tunnelList != null) {
      for (int i = 0; i < tunnelList.size(); i++) {
        String cuid = (String)tunnelList.get(i);
        if (cuid != null) {
          if ((sql != null) && (sql.trim().length() > 0))
            sql = sql + ",'" + cuid + "'";
          else {
            sql = sql + "'" + cuid + "'";
          }
        }
      }
      DataObjectList dbos = null;
      if ((sql != null) && (sql.trim().length() > 0)) {
        sql = "SELECT AP.CUID,TV.RELATED_TUNNEL_CUID FROM TUNNEL_TO_VIRTUAL_LINE TV,ATTEMP_PTN_PATH AP WHERE TV.RELATED_TUNNEL_CUID IN (" + sql + ")" + " AND TV." + "RELATED_VIRTUAL_LINE_CUID" + "=AP." + "RELATED_VIRTUAL_LINE_CUID";
      }

      dbos = super.selectDBOs(sql, new Class[] { String.class, String.class });

      return dbos;
    }

    return null;
  }

  public DataObjectList getMaxHeadingNum(BoActionContext actionContext, String year, String month)
    throws Exception
  {
    String sql = "SELECT MAX(HEADING_NUM_CODE) FROM ATTEMP_SHEET WHERE HEADING_NUM LIKE '" + year + month + "%";

    return super.selectDBOs(sql, new Class[] { String.class });
  }

  public DboCollection getPtnTraphsByTraphCuids(BoActionContext boActionContext, String traphCuids)
    throws Exception
  {
    String sql = "";
    DboCollection dboCollection = null;
    if (DaoHelper.isNotEmpty(traphCuids))
    {
      if (traphCuids.startsWith("TRAPH"))
      {
        sql = "select distinct T.CUID from PTN_PATH P, TRAPH T  where P.RELATED_ROUTE_CUID = T.CUID and T.CUID in ('" + traphCuids.replaceAll(",", "','") + "')";

        dboCollection = super.selectDBOs(sql, new GenericDO[] { new Traph() });
      }
    }
    return dboCollection;
  }

  public PtnVirtualLine getPtnVirtuallLineByCuid(String lineCuid)
    throws Exception
  {
    String sql = "CUID='" + lineCuid + "'";
    DataObjectList dbos = getObjectsBySql(sql, new PtnVirtualLine(), 0);

    if ((dbos != null) && (dbos.size() > 0)) {
      return (PtnVirtualLine)dbos.get(0);
    }
    return null;
  }

  public DataObjectList getNoRelationTraphsByCuids(BoActionContext boActionContext, String traphCuids)
    throws Exception
  {
    String sql = "";
    DataObjectList dbos = null;
    if (DaoHelper.isNotEmpty(traphCuids))
    {
      sql = "select distinct T.CUID from PTN_PATH P, TRAPH T  where P.RELATED_ROUTE_CUID = T.CUID and T.CUID in ('" + traphCuids.replaceAll(",", "','") + "')" + " AND (SELECT COUNT(" + "CUID" + ") FROM " + "PTN_VIRTUAL_LINE" + " WHERE " + "CUID" + " = P." + "RELATED_VIRTUAL_LINE_CUID" + ")=0";

      dbos = super.selectDBOs(sql, new Class[] { String.class });
    }
    return dbos;
  }

  public PtnEth getPtnEthByVLFdn(String vlFdn) throws Exception {
    PtnEth ptnEth = null;
    String sql = "select * from PTN_ETH where PTN_VL_FDN='" + vlFdn + "'";
    DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnEth() });
    if ((dbos != null) && (dbos.size() > 0)) {
      ptnEth = (PtnEth)dbos.getAttrField("PTN_ETH", 0);
    }
    return ptnEth;
  }
  public PtnAtm getPtnAtmByVLFdn(String vlFdn) throws Exception {
    PtnAtm ptnAtm = null;
    String sql = "select * from PTN_ATM where PTN_VL_FDN='" + vlFdn + "'";
    DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnAtm() });
    if ((dbos != null) && (dbos.size() > 0)) {
      ptnAtm = (PtnAtm)dbos.getAttrField("PTN_ATM", 0);
    }
    return ptnAtm;
  }
  public PtnTdm getPtnTdmByVLFdn(String vlFdn) throws Exception {
    PtnTdm ptnTdm = null;
    String sql = "select * from PTN_TDM where PTN_VL_FDN='" + vlFdn + "'";
    DboCollection dbos = super.selectDBOs(sql, new GenericDO[] { new PtnTdm() });
    if ((dbos != null) && (dbos.size() > 0)) {
      ptnTdm = (PtnTdm)dbos.getAttrField("PTN_TDM", 0);
    }
    return ptnTdm;
  }
  public DboCollection getLspName(String lspName) throws Exception {
    String sql = "SELECT USER_LABEL FROM PTN_PROTECT_GROUP WHERE USER_LABEL LIKE '%" + lspName + "%'";
    return super.selectDBOs(sql, new GenericDO[] { new PtnProtectGroup() });
  }

  public DboCollection getTurnnelNativeName(String nativeName) throws Exception {
    String sql = "SELECT NATIVE_EMS_NAME FROM PTN_TURNNEL WHERE NATIVE_EMS_NAME LIKE '%" + nativeName + "%'";
    return super.selectDBOs(sql, new GenericDO[] { new PtnTurnnel() });
  }

  public DataObjectList getOriPtpCuidCount(String EthCuid) throws Exception {
    String sql = "SELECT DISTINCT ORIG_PTP_CUID FROM PTN_TP_PAIR WHERE RELATED_SERVICE_CUID = '" + EthCuid + "'";
    DataObjectList dbos = super.selectDBOs(sql, new Class[] { String.class });
    return dbos;
  }
  public DataObjectList getDestPtpCuidCount(String EthCuid) throws Exception {
    String sql = "SELECT DISTINCT DEST_PTP_CUID FROM PTN_TP_PAIR WHERE RELATED_SERVICE_CUID = '" + EthCuid + "'";
    DataObjectList dbos = super.selectDBOs(sql, new Class[] { String.class });
    return dbos;
  }
  public DataObjectList getPtnAndVirtualLine(BoQueryContext context, String EthCuid) throws Exception {
    String sql = "SELECT p.ORIG_PTP_CUID,p.DEST_PTP_CUID,v.LABEL_CN,v.USER_LABEL,v.NATINE_EMS_NAME,v.FDN,v.DIRECTIONALITY,v.ACTIVE_FLAG,v.CUID,v.REMARK FROM PTN_TP_PAIR p LEFT JOIN PTN_VIRTUAL_LINE v ON p.RELATED_VIRTUAL_LINE_CUID=v.CUID WHERE p.RELATED_SERVICE_CUID = '" + EthCuid + "'";
    return super.selectDBOs(context, sql, new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, Long.TYPE, Long.TYPE, String.class, String.class });
  }

  public DataObjectList getPtnProtectGroupInfos(BoActionContext actionContext, String turnnelCuids) throws Exception {
    String sql = "";
    DataObjectList dbos = null;
    if (DaoHelper.isNotEmpty(turnnelCuids)) {
      sql = "SELECT T.PG_CUID,P.USER_LABEL,P.NATIVE_EMS_NAME,P.REVERSION_MODE,P.PG_TYPE,P.OWNER,T.TW_CUID  FROM PTN_PROTECT_GROUP_UNIT T,PTN_PROTECT_GROUP P  WHERE T.PG_CUID = P.CUID AND T.TW_CUID IN ('" + turnnelCuids + "') AND 1=1";

      dbos = super.selectDBOs(sql, new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class });
    }
    return dbos;
  }

  public List getTraphAvailability(BoActionContext actionContext, String districtCuid)
    throws Exception
  {
    List list = new ArrayList();
    Map map = new HashMap();
    int aSize = 0;
    int zSize = 0;
    String sdhASql = "select count(*) from(  select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '1'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='1' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    DataObjectList dbos = super.selectDBOs(sdhASql, new Class[] { String.class });
    if (dbos != null) {
      String sdhASize2M = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(sdhASize2M);
    }

    String sdhZSql = "select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '1'  and tp.related_z_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='1' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhZSql, new Class[] { String.class });
    if (dbos != null) {
      String sdhZSize2M = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(sdhZSize2M);
    }

    String sdhSize2M = Integer.toString(aSize + zSize);
    map.put("sdhSize2M", sdhSize2M);

    sdhASql = "select count(*) from(  select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '7'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='7' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhASql, new Class[] { String.class });
    if (dbos != null) {
      String sdhASize100M = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(sdhASize100M);
    }
    else {
      aSize = 0;
    }

    sdhZSql = "select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '7'  and tp.related_z_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='7' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhZSql, new Class[] { String.class });
    if (dbos != null) {
      String sdhZSize100M = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(sdhZSize100M);
    } else {
      zSize = 0;
    }

    String sdhSize100M = Integer.toString(aSize + zSize);
    map.put("sdhSize100M", sdhSize100M);

    sdhASql = "select count(*) from(  select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '9'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='9' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhASql, new Class[] { String.class });
    if (dbos != null) {
      String sdhASize155M = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(sdhASize155M);
    } else {
      aSize = 0;
    }

    sdhZSql = "select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '9'  and tp.related_z_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='9' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhZSql, new Class[] { String.class });
    if (dbos != null) {
      String sdhZSize155M = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(sdhZSize155M);
    } else {
      zSize = 0;
    }

    String sdhSize155M = Integer.toString(aSize + zSize);
    map.put("sdhSize155M", sdhSize155M);

    sdhASql = "select count(*) from(  select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '13'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='13' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhASql, new Class[] { String.class });
    if (dbos != null) {
      String sdhASize622M = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(sdhASize622M);
    } else {
      aSize = 0;
    }

    sdhZSql = "select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='1'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '13'  and tp.related_z_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='1' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='13' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(sdhZSql, new Class[] { String.class });
    if (dbos != null) {
      String sdhZSize622M = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(sdhZSize622M);
    } else {
      zSize = 0;
    }

    String sdhSize622M = Integer.toString(aSize + zSize);
    map.put("sdhSize622M", sdhSize622M);

    String ptnASql = " select count(*) from(  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '7'  union all  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '7')";

    dbos = super.selectDBOs(ptnASql, new Class[] { String.class });
    if (dbos != null) {
      String ptnASize100M = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(ptnASize100M);
    } else {
      aSize = 0;
    }
    String ptnZSql = " select count(*) from(  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '7'  union all  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_z_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '7')";

    dbos = super.selectDBOs(ptnZSql, new Class[] { String.class });
    if (dbos != null) {
      String ptnZSize100M = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(ptnZSize100M);
    } else {
      zSize = 0;
    }

    String ptnSize100M = Integer.toString(aSize + zSize);
    map.put("ptnSize100M", ptnSize100M);

    ptnASql = " select count(*) from(  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '35'  union all  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '35')";

    dbos = super.selectDBOs(ptnASql, new Class[] { String.class });
    if (dbos != null) {
      String ptnASizeGE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(ptnASizeGE);
    } else {
      aSize = 0;
    }

    ptnZSql = " select count(*) from(  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '35'  union all  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_z_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '35')";

    dbos = super.selectDBOs(ptnZSql, new Class[] { String.class });
    if (dbos != null) {
      String ptnZSizeGE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(ptnZSizeGE);
    } else {
      zSize = 0;
    }

    String ptnSizeGE = Integer.toString(aSize + zSize);
    map.put("ptnSizeGE", ptnSizeGE);

    ptnASql = " select count(*) from(  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '36'  union all  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '36')";

    dbos = super.selectDBOs(ptnASql, new Class[] { String.class });
    if (dbos != null) {
      String ptnASize10GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(ptnASize10GE);
    } else {
      aSize = 0;
    }

    ptnZSql = " select count(*) from(  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '36'  union all  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_z_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '36')";

    dbos = super.selectDBOs(ptnZSql, new Class[] { String.class });
    if (dbos != null) {
      String ptnZSize10GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(ptnZSize10GE);
    } else {
      zSize = 0;
    }

    String ptnSize10GE = Integer.toString(aSize + zSize);
    map.put("ptnSize10GE", ptnSize10GE);

    ptnASql = " select count(*) from(  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '41'  union all  select distinct tp.related_a_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_a_ptp_cuid  and p.port_rate = '41')";

    dbos = super.selectDBOs(ptnASql, new Class[] { String.class });
    if (dbos != null) {
      String ptnASize40GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(ptnASize40GE);
    } else {
      aSize = 0;
    }

    ptnZSql = " select count(*) from(  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='PTN_PATH'  and trp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_a_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '41'  union all  select distinct tp.related_z_ptp_cuid  from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,ptn_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type is null  and trp.path_cuid is null  and trp.cuid = mp.related_route_path_cuid  and mp.path_type = 'PTN_PATH'  and mp.path_cuid = tp.cuid  and tp.path_type='1'  and tp.related_z_ptp_cuid is not null  and p.cuid = tp.related_z_ptp_cuid  and p.port_rate = '41')";

    dbos = super.selectDBOs(ptnZSql, new Class[] { String.class });
    if (dbos != null) {
      String ptnZSize40GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(ptnZSize40GE);
    } else {
      zSize = 0;
    }

    String ptnSize40GE = Integer.toString(aSize + zSize);
    map.put("ptnSize40GE", ptnSize40GE);

    String otnASql = " select count(*) from( select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '35'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='35' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnASql, new Class[] { String.class });
    if (dbos != null) {
      String otnASizeGE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(otnASizeGE);
    } else {
      aSize = 0;
    }
    String otnZSql = " select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '35'  and tp.related_z_district_cuid like '" + districtCuid + "%'" + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='35' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnZSql, new Class[] { String.class });
    if (dbos != null) {
      String otnZSizeGE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(otnZSizeGE);
    } else {
      zSize = 0;
    }

    String otnSizeGE = Integer.toString(aSize + zSize);
    map.put("otnSizeGE", otnSizeGE);

    otnASql = " select count(*) from( select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '16'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='16' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnASql, new Class[] { String.class });
    if (dbos != null) {
      String otnASize25GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(otnASize25GE);
    } else {
      aSize = 0;
    }

    otnZSql = " select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '16'  and tp.related_z_district_cuid like '" + districtCuid + "%'" + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='16' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnZSql, new Class[] { String.class });
    if (dbos != null) {
      String otnZSize25GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(otnZSize25GE);
    } else {
      zSize = 0;
    }

    String otnSize25GE = Integer.toString(aSize + zSize);
    map.put("otnSize25GE", otnSize25GE);

    otnASql = " select count(*) from( select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '36'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='36' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnASql, new Class[] { String.class });
    if (dbos != null) {
      String otnASize10GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(otnASize10GE);
    } else {
      aSize = 0;
    }

    otnZSql = " select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '36'  and tp.related_z_district_cuid like '" + districtCuid + "%'" + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='36' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnZSql, new Class[] { String.class });
    if (dbos != null) {
      String otnZSize10GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(otnZSize10GE);
    } else {
      zSize = 0;
    }

    String otnSize10GE = Integer.toString(aSize + zSize);
    map.put("otnSize10GE", otnSize10GE);

    otnASql = " select count(*) from( select distinct tp.related_a_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_a_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_a_end_ptp  and p.port_rate = '44'  and tp.related_a_district_cuid like '" + districtCuid + "%' " + " union all " + " select distinct tp.related_a_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_a_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_a_end_ptp " + " and p.port_rate='44' " + " and tp.related_a_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnASql, new Class[] { String.class });
    if (dbos != null) {
      String otnASize100GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      aSize = Integer.parseInt(otnASize100GE);
    } else {
      aSize = 0;
    }

    otnZSql = " select count(*) from(  select distinct tp.related_z_end_ptp  from traph t,traph_route tr,traph_route_to_path trp,trans_path tp,ptp p  where t.cuid = tr.related_service_cuid  and trp.traph_route_cuid = tr.cuid  and trp.path_type='TRANS_PATH'  and trp.path_cuid = tp.cuid  and tp.related_z_end_ptp is not null  and tp.path_type='2'  and p.cuid = tp.related_z_end_ptp  and p.port_rate = '44'  and tp.related_z_district_cuid like '" + districtCuid + "%'" + " union all " + " select distinct tp.related_z_end_ptp " + " from traph t,traph_route tr,traph_route_to_path trp,multi_path mp,trans_path tp,ptp p " + " where t.cuid = tr.related_service_cuid " + " and trp.traph_route_cuid = tr.cuid " + " and trp.path_type is null " + " and trp.path_cuid is null " + " and trp.cuid = mp.related_route_path_cuid " + " and mp.path_type = 'TRANS_PATH' " + " and mp.path_cuid = tp.cuid " + " and tp.related_z_end_ptp is not null " + " and tp.path_type='2' " + " and p.cuid = tp.related_z_end_ptp " + " and p.port_rate='44' " + " and tp.related_z_district_cuid like '" + districtCuid + "%')";

    dbos = super.selectDBOs(otnZSql, new Class[] { String.class });
    if (dbos != null) {
      String otnZSize100GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      zSize = Integer.parseInt(otnZSize100GE);
    } else {
      zSize = 0;
    }

    String otnSize100GE = Integer.toString(aSize + zSize);
    map.put("otnSize100GE", otnSize100GE);

    String sql2M = "select count(*) from ptp where port_rate = '1' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql2M, new Class[] { String.class });
    if (dbos != null) {
      String size2M = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size2M", size2M);
    } else {
      map.put("size2M", "0");
    }

    String sql100M = "select count(*) from ptp where port_rate = '7' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql100M, new Class[] { String.class });
    if (dbos != null) {
      String size100M = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size100M", size100M);
    } else {
      map.put("size100M", "0");
    }

    String sql155M = "select count(*) from ptp where port_rate = '9' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql155M, new Class[] { String.class });
    if (dbos != null) {
      String size155M = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size155M", size155M);
    } else {
      map.put("size155M", "0");
    }

    String sql622M = "select count(*) from ptp where port_rate = '13' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql622M, new Class[] { String.class });
    if (dbos != null) {
      String size622M = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size622M", size622M);
    } else {
      map.put("size622M", "0");
    }

    String sqlGE = "select count(*) from ptp where port_rate = '35' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sqlGE, new Class[] { String.class });
    if (dbos != null) {
      String sizeGE = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("sizeGE", sizeGE);
    } else {
      map.put("sizeGE", "0");
    }

    String sql10GE = "select count(*) from ptp where port_rate = '36' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql10GE, new Class[] { String.class });
    if (dbos != null) {
      String size10GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size10GE", size10GE);
    } else {
      map.put("size10GE", "0");
    }

    String sql40GE = "select count(*) from ptp where port_rate = '41' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql40GE, new Class[] { String.class });
    if (dbos != null) {
      String size40GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size40GE", size40GE);
    } else {
      map.put("size40GE", "0");
    }

    String sql25GE = "select count(*) from ptp where port_rate = '16' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql25GE, new Class[] { String.class });
    if (dbos != null) {
      String size25GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size25GE", size25GE);
    } else {
      map.put("size25GE", "0");
    }

    String sql100GE = "select count(*) from ptp where port_rate = '44' and related_district_cuid like '" + districtCuid + "%'";
    dbos = super.selectDBOs(sql100GE, new Class[] { String.class });
    if (dbos != null) {
      String size100GE = ((GenericDO)dbos.get(0)).getAttrString("1");
      map.put("size100GE", size100GE);
    } else {
      map.put("size100GE", "0");
    }
    list.add(map);
    return list;
  }

  public DataObjectList getDataResponsiblePerson(BoActionContext actionContext, String cuid) throws Exception {
    String sql = "SELECT * FROM T_DEVICE_TO_PERSON WHERE CUID='" + cuid + "'";
    return super.selectDBOs(sql, new Class[] { String.class, String.class, String.class, String.class });
  }

  public void removeDataResponsiblePerson(BoActionContext actionContext, String cuid) throws Exception
  {
    String sql = "delete   from t_device_to_person where cuid='" + cuid + "'";
    super.execSql(sql);
  }

  public void saveDataResponsiblePerson(BoActionContext actionContext, TDeviceToPerson tDeviceToPerson) throws Exception
  {
    String sql = "INSERT INTO  t_device_to_person(CUID,BMCLASSID,DATA_QUALITY_PERSON) VALUES ('" + tDeviceToPerson.getCuid() + "','" + tDeviceToPerson.getCuid().split("-")[0] + "','" + tDeviceToPerson.getDataQualityPerson() + "') ";
    tDeviceToPerson.setBmclassid(tDeviceToPerson.getCuid().split("-")[0]);
    LogHome.getLog().info("==============================" + tDeviceToPerson.getCuid().split("-")[0]);
    super.execSql(sql);
  }
}