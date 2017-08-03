Ext.ns("IRMS.component");

IRMS.component.EmsSelectPanel = Ext.extend(Ext.Panel, {
	frame : true,
	constructor : function(config) {
		if(!config.inputParams) {
			config.inputParams = {};
		}
		IRMS.component.EmsSelectPanel.superclass.constructor.call(this,config);
	},
	initComponent : function() {
		this._initItems();
		IRMS.component.EmsSelectPanel.superclass.initComponent.call(this);
	},
	_initItems : function() {
		var _items = [];
		this.formPanel = new Ext.FormPanel({
				layout : 'column',
				region : 'north',
				frame : true,
				bodyPadding: 20,
				defaults : {
					anchor : '-10'
					
				},
				items : [{
					columnWidth : 1,
					layout : 'form',
					defaults : {
						anchor : '-20'
					},
					items : [{
						xtype : 'radiogroup',
						fieldLabel : 'EMS选择',
						name : 'EMS_TYPE',
						columns : 2,
						items: [
						        {boxLabel: 'EMS名称', name: 'EMS_TYPE_GROUP', inputValue: 'EMS_LABEL_CN', checked: true},
						        {boxLabel: '厂家', name: 'EMS_TYPE_GROUP', inputValue: 'FACTORY'},
						        {boxLabel: '空间', name: 'EMS_TYPE_GROUP', inputValue: 'SPACE'}
						        ]
						
					}]
				},{
					columnWidth : 1,
					layout : 'form',
					defaultType : 'textfield',
					defaults : {
						anchor : '-20'
					},
					items : [{
						xtype : 'asyncombox',
						fieldLabel : 'EMS名称',
						name : 'LABEL_CN',
						comboxCfg : {
							cfgParams : {
								code : "TNMS_NMS_SYSTEM"
							}
						}
						
					}]
				}, {
					columnWidth : .5,
					layout : 'form',
					defaultType : 'textfield',
					defaults : {
						anchor : '-20'
					},
					items : [{
						xtype : 'asyncombox',
						fieldLabel : '厂家',
						name : 'RELATED_VENDOR_CUID',
						comboxCfg : {
							cfgParams : {
								code : "CM.DEVICE_VENDOR"
							}
						}
					},{
						xtype : 'asyncombox',
						fieldLabel : '空间',
						name : 'RELATED_SPACE_CUID',
						comboxCfg : {
							cfgParams : {
								code : "TNMS_DISTRICT"
							}
						}
							
					},{
						xtype : 'textfield',
						fieldLabel : '核查任务名称',
						name : 'taskName'
					}]
				}, {
					columnWidth : .5,
					layout : 'form',
					defaultType : 'textfield',
					defaults : {
						anchor : '-20'
					},
					items : [{
						xtype : 'asyncombox',
						fieldLabel : 'EMS名称',
						name : 'LABEL_CN_VENDOR',
						comboxCfg : {
							cfgParams : {
								code : "TNMS_NMS_SYSTEM"
							}
						},
						listeners : {
							scope : this,
							beforequery : function(queryEvent ){
								var form = this.formPanel.getForm();
								var vendorCuid = form.findField('RELATED_VENDOR_CUID').getValue();
								if(vendorCuid.length >0){
									queryEvent.combo.comboxCfg.queryParams= {
											RELATED_VENDOR_CUID : {
											key : 'RELATED_VENDOR_CUID',
											value : vendorCuid
										}
									}
								}
								
							}
						}
						
					},{
						xtype : 'asyncombox',
						fieldLabel : 'EMS名称',
						name : 'LABEL_CN_SPACE',
						comboxCfg : {
							cfgParams : {
								code : "TNMS_NMS_SYSTEM"
							}
						},
						listeners : {
							scope : this,
							beforequery : function(queryEvent ){
								var form = this.formPanel.getForm();
								var spaceCuid = form.findField('RELATED_SPACE_CUID').getValue();
								if(spaceCuid.length >0){
									queryEvent.combo.comboxCfg.queryParams= {
											RELATED_SPACE_CUID : {
											key : 'RELATED_SPACE_CUID',
											value : spaceCuid
										}
									};
								}
							}
						}
					},{
						xtype : 'checkbox',
						fieldLabel : '是否下发采集任务',
						name : 'isSend'
					}]
				}]
		});
		_items.push(this.formPanel);
		this.items = _items;
	},
	save : function(callBack) {
		var scope = this;
		var data = scope.formPanel.getForm().getValues();
		var taskName = data.taskName;
		var cuids = [];
		if(!Ext.isEmpty(data.LABEL_CN)){
			cuids.push(data.LABEL_CN);
		}
		if(!Ext.isEmpty(data.LABEL_CN_VENDOR)){
			cuids.push(data.LABEL_CN_VENDOR);
		}
		if(!Ext.isEmpty(data.LABEL_CN_SPACE)){
			cuids.push(data.LABEL_CN_SPACE);
		}
		if(cuids.length == 0){
			Ext.Msg.alert("温馨提示","请选择一个EMS");
			return false;
		}
		if(Ext.isEmpty(taskName)){
			Ext.Msg.alert("温馨提示","请填写任务名称");
			return false;
		}
		MaskHelper.mask(scope.getEl(), '核查任务创建中，请稍候...');
		TransPathAction.setTranspathCheckTaskByEmsCuids(cuids,taskName,function(data){
			if(Ext.isFunction(callBack)){
				callBack.call();
			}
			Ext.Msg.alert("温馨提示",data);
			MaskHelper.unmask(scope.getEl());
		});
	}
});