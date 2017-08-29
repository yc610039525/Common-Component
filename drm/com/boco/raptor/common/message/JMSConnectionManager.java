/*     */ package com.boco.raptor.common.message;
/*     */ 
/*     */ import com.boco.common.util.debug.LogHome;
/*     */ import com.boco.common.util.lang.StringHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JMSConnectionManager
/*     */ {
/*  20 */   private final Map<String, JMSConnectionFactory> jmsConnFactoryTable = new HashMap();
/*  21 */   private final Map<String, JMSConnectionFactory> jmsBackConnFactoryTable = new HashMap();
/*  22 */   private final Map<String, String> jmsDestinationTable = new HashMap();
/*  23 */   private final Map<String, String> jmsBackDestinationTable = new HashMap();
/*  24 */   private Map<String, String> alarmChannels = new HashMap();
/*  25 */   private static JMSConnectionManager instance = new JMSConnectionManager();
/*     */   private String masterMqHost;
/*  27 */   private boolean useMasterBackupMode = false;
/*     */ 
/*  29 */   private final Map<String, Boolean> usedMQConnMode = new HashMap();
/*  30 */   private final Map<String, JMSConnectionFactory> destinations = new HashMap();
/*  31 */   private final Map<String, JMSConnectionFactory> backDestinations = new HashMap();
/*     */ 
/*     */   public static JMSConnectionManager getInstance()
/*     */   {
/*  37 */     return instance;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getDestinationTable() {
/*  41 */     return this.jmsDestinationTable;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getBackupDestinationTable() {
/*  45 */     return this.jmsBackDestinationTable;
/*     */   }
/*     */ 
/*     */   public JMSConnectionFactory getJMSConnectionFactory(String destinationName) {
/*  49 */     JMSConnectionFactory factory = (JMSConnectionFactory)this.destinations.get(destinationName);
/*  50 */     if ((factory != null) && (factory.isMainMQ() == ((Boolean)this.usedMQConnMode.get(factory.getJmsContext().getContextName())).booleanValue())) {
/*  51 */       return factory;
/*     */     }
/*  53 */     return (JMSConnectionFactory)this.backDestinations.get(destinationName);
/*     */   }
/*     */ 
/*     */   public List<JMSConnectionFactory> getJMSConnectionFactorys(String destinationName) {
/*  57 */     List factoryList = new ArrayList();
/*  58 */     JMSConnectionFactory factory = (JMSConnectionFactory)this.destinations.get(destinationName);
/*  59 */     JMSConnectionFactory backFactory = (JMSConnectionFactory)this.backDestinations.get(destinationName);
/*  60 */     if (factory != null) {
/*  61 */       factoryList.add(factory);
/*     */     }
/*  63 */     if ((isUseMasterBackupMode()) && (backFactory != null)) {
/*  64 */       factoryList.add(backFactory);
/*     */     }
/*  66 */     return factoryList;
/*     */   }
/*     */ 
/*     */   public Boolean getUsedMQConnMode(String contextName) {
/*  70 */     return (Boolean)this.usedMQConnMode.get(contextName);
/*     */   }
/*     */ 
/*     */   public void setUsedMQConnMode(String contextName, boolean isMainMode) {
/*  74 */     this.usedMQConnMode.put(contextName, Boolean.valueOf(isMainMode));
/*     */   }
/*     */ 
/*     */   public Map<String, JMSConnectionFactory> getAllMainConnectionFactorys() {
/*  78 */     return this.jmsConnFactoryTable;
/*     */   }
/*     */ 
/*     */   public Map<String, JMSConnectionFactory> getAllBackConnectionFactorys() {
/*  82 */     return this.jmsBackConnFactoryTable;
/*     */   }
/*     */ 
/*     */   public String getJMSContextName(String destinationName) {
/*  86 */     return (String)this.jmsDestinationTable.get(destinationName);
/*     */   }
/*     */ 
/*     */   public String getMasterMqHost() {
/*  90 */     return this.masterMqHost;
/*     */   }
/*     */ 
/*     */   public void setMasterMqHost(String mqHost) {
/*  94 */     this.masterMqHost = mqHost;
/*     */   }
/*     */ 
/*     */   public boolean isUseMasterBackupMode() {
/*  98 */     return this.useMasterBackupMode;
/*     */   }
/*     */ 
/*     */   public void setUseMasterBackupMode(boolean useMasterBackupMode) {
/* 102 */     this.useMasterBackupMode = useMasterBackupMode;
/*     */   }
/*     */ 
/*     */   public void setJmsConnectionPools(Map<String, JMSContext> jmsContexts) throws Exception {
/* 106 */     if (jmsContexts != null) {
/* 107 */       Iterator iterator = jmsContexts.keySet().iterator();
/* 108 */       while (iterator.hasNext()) {
/* 109 */         String destinationName = iterator.next().toString();
/* 110 */         JMSContext jmsContext = (JMSContext)jmsContexts.get(destinationName);
/* 111 */         if ((isUseMasterBackupMode()) || (jmsContext.getUrl().equals(this.masterMqHost)) || (StringHelper.isEmpty(this.masterMqHost))) {
/* 112 */           this.jmsDestinationTable.put(destinationName, jmsContext.getContextName());
/* 113 */           setDestinationConnection(jmsContext, destinationName);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setJmsBackConnectionPools(Map<String, JMSContext> jmsContexts) throws Exception {
/* 120 */     if (jmsContexts != null) {
/* 121 */       Iterator iterator = jmsContexts.keySet().iterator();
/* 122 */       while (iterator.hasNext()) {
/* 123 */         String destinationName = iterator.next().toString();
/* 124 */         JMSContext jmsContext = (JMSContext)jmsContexts.get(destinationName);
/* 125 */         if ((isUseMasterBackupMode()) || (jmsContext.getUrl().equals(this.masterMqHost))) {
/* 126 */           this.jmsBackDestinationTable.put(destinationName, jmsContext.getContextName());
/* 127 */           setDestinationConnection(jmsContext, destinationName);
/*     */         }
/*     */       }
/* 130 */       LogHome.getLog().info("主:" + this.destinations + ",备:" + this.backDestinations);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setDestinationConnection(JMSContext jmsContext, String destinationName) throws Exception {
/* 135 */     boolean isMasterMq = (StringHelper.isEmpty(this.masterMqHost)) || (jmsContext.getUrl().equals(this.masterMqHost));
/* 136 */     this.usedMQConnMode.put(jmsContext.getContextName(), Boolean.valueOf(true));
/* 137 */     if (isMasterMq) {
/* 138 */       setJmsConnectionPool(jmsContext);
/* 139 */       JMSConnectionFactory factory = (JMSConnectionFactory)this.jmsConnFactoryTable.get(jmsContext.getContextName());
/* 140 */       this.destinations.put(destinationName, factory);
/*     */     } else {
/* 142 */       setJmsBackConnectionPool(jmsContext);
/* 143 */       JMSConnectionFactory factory = (JMSConnectionFactory)this.jmsBackConnFactoryTable.get(jmsContext.getContextName());
/* 144 */       this.backDestinations.put(destinationName, factory);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setJmsConnectionPool(JMSContext jmsContext) throws Exception {
/* 149 */     if (!this.jmsConnFactoryTable.containsKey(jmsContext.getContextName())) {
/* 150 */       JMSConnectionFactory factory = new JMSConnectionFactory(jmsContext, true);
/* 151 */       this.jmsConnFactoryTable.put(jmsContext.getContextName(), factory);
/* 152 */       this.usedMQConnMode.put(jmsContext.getContextName(), Boolean.valueOf(true));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setJmsBackConnectionPool(JMSContext jmsContext) throws Exception {
/* 157 */     if (!this.jmsBackConnFactoryTable.containsKey(jmsContext.getContextName())) {
/* 158 */       JMSConnectionFactory factory = new JMSConnectionFactory(jmsContext, false);
/* 159 */       this.jmsBackConnFactoryTable.put(jmsContext.getContextName(), factory);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAlarmChannels(Map<String, String> _alarmChannels) throws Exception {
/* 164 */     this.alarmChannels = _alarmChannels;
/*     */   }
/*     */ 
/*     */   public String getAlarmMsgDestination(String amrtuName) {
/* 168 */     String destName = (String)this.alarmChannels.get(amrtuName);
/* 169 */     if ((destName == null) || (destName.trim().length() == 0)) {
/* 170 */       destName = "Q_AM_AMRTU2AM";
/*     */     }
/* 172 */     return destName;
/*     */   }
/*     */ 
/*     */   public ArrayList getAllAlarmChannels() {
/* 176 */     ArrayList channels = new ArrayList();
/* 177 */     if (this.alarmChannels.size() == 0) {
/* 178 */       channels.add("Q_AM_AMRTU2AM");
/*     */     } else {
/* 180 */       HashMap map = new HashMap();
/* 181 */       Iterator iterator = this.alarmChannels.values().iterator();
/* 182 */       while (iterator.hasNext()) {
/* 183 */         String str = (String)iterator.next();
/* 184 */         map.put(str, str);
/*     */       }
/* 186 */       channels.addAll(map.keySet());
/*     */     }
/* 188 */     return channels;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\drm.jar
 * Qualified Name:     com.boco.raptor.common.message.JMSConnectionManager
 * JD-Core Version:    0.6.0
 */