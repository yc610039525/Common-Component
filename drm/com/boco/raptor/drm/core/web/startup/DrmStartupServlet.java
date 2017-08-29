/*    */ package com.boco.raptor.drm.core.web.startup;
/*    */ 
/*    */ import com.boco.common.util.debug.LogHome;
/*    */ import com.boco.common.util.lang.SystemEnv;
/*    */ import com.boco.raptor.cfg.HomeFactoryIoc;
/*    */ import com.boco.raptor.common.service.ServiceHomeFactory;
/*    */ import com.boco.raptor.drm.core.service.bm.IBMModelService;
/*    */ import com.boco.transnms.server.common.cfg.TnmsDrmCfg;
/*    */ import com.boco.transnms.server.dao.base.DaoHomeFactory;
/*    */ import com.boco.transnms.server.dao.base.DynDaoHome;
/*    */ import com.boco.transnms.server.dao.base.GenericDAO;
/*    */ import com.boco.transnms.server.dao.base.GenericObjectDAO;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.springframework.context.support.FileSystemXmlApplicationContext;
/*    */ 
/*    */ public class DrmStartupServlet extends HttpServlet
/*    */ {
/*    */   private static final String CONTENT_TYPE = "text/html; charset=GBK";
/*    */   private static final String TNMS_SERVER_HOME = "TNMS_SERVER_HOME";
/* 41 */   private String cfgPath = SystemEnv.getPathEnv("TNMS_SERVER_HOME") + "/tnms-conf/";
/*    */ 
/*    */   public void init() throws ServletException {
/*    */     try {
/* 45 */       LogHome.getLog().info("----    初始化TransNMS-DRM系统    ---");
/* 46 */       loadDbCfg();
/* 47 */       initDAO();
/* 48 */       initDrm();
/* 49 */       LogHome.getLog().info("----  TransNMS-DRM系统初始化完成  ---");
/*    */     } catch (Exception ex) {
/* 51 */       LogHome.getLog().error("", ex);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void initDrm() throws Exception {
/* 56 */     String filePath = "raptor-conf/raptor-factory.xml";
/* 57 */     HomeFactoryIoc.createInstance(filePath);
/* 58 */     TnmsDrmCfg.getInstance().setDbModelFilePath(this.cfgPath + "goat_model.xml");
/* 59 */     getBMModelService().setModelFilePath(this.cfgPath);
/* 60 */     ServiceHomeFactory.getInstance().initServiceHome();
/*    */   }
/*    */ 
/*    */   protected void loadDbCfg() throws Exception {
/* 64 */     TnmsDrmCfg.getInstance().setDbModelFilePath(this.cfgPath + "goat_model.xml");
/* 65 */     new FileSystemXmlApplicationContext(this.cfgPath + "tnmscfg.xml");
/* 66 */     LogHome.getLog().info("读取配置文件：" + this.cfgPath + "tnmscfg.xml");
/*    */   }
/*    */ 
/*    */   protected void initDAO() throws Exception {
/* 70 */     DynDaoHome daoHome = new DynDaoHome();
/* 71 */     daoHome.addDAO(new GenericDAO());
/* 72 */     daoHome.addDAO(new GenericObjectDAO());
/* 73 */     DaoHomeFactory.getInstance().addDaoHome("default", daoHome);
/*    */   }
/*    */ 
/*    */   public static IBMModelService getBMModelService() {
/* 77 */     return (IBMModelService)ServiceHomeFactory.getInstance().getService("BMModelService");
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.drm.core.web.startup.DrmStartupServlet
 * JD-Core Version:    0.6.0
 */