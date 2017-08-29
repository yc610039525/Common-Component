Ext.ns("IRMS.component");

IRMS.component.TraphAutoCheckPanel = Ext.extend(Ext.Panel, {
	frame : true,
	constructor : function(config) {
		if(!config.inputParams) {
			config.inputParams = {};
		}
		IRMS.component.TraphAutoCheckPanel.superclass.constructor.call(this,config);
	},
	initComponent : function() {
		this._initItems();
		IRMS.component.TraphAutoCheckPanel.superclass.initComponent.call(this);
	},
	_initItems : function() {
		
		
		var scope = this;
		var datas = [];
	    for(var i=0;i<24;i++){
	    	var option = [i,i +"点"];
	    	datas.push(option);
	    }
		var synchTimeStore = new Ext.data.SimpleStore({
            data   : datas,
            autoLoad : true,
            fields : ['value','label']
        });
		
		
		var synchDayStore = new Ext.data.JsonStore({
			root   : 'data',
			fields : ['value','label']
		});
		
		
		var _items = [];		
		this.formPanel = new Ext.FormPanel({
			bodyStyle : 'padding:5px 5px 5px 5px',
			border : false,
			autoScroll : false,
			hideBorders : false,
			items : [{
				xtype : "fieldset",
				title : " 任务项设置",
				layout : 'column',
				defaults : {
					border : false,
					columnWidth : .33,
					layout : 'form',
					anchor : '-20',
					border : false
				},
				items :[{
					columnWidth : .33,
					layout : 'form',
					labelWidth : 160,
					items : [{
						fieldLabel : '是否自动进行电路核查',
						hideLabel : false,
						name : 'autoTraphcheck',
						xtype : 'radiogroup',
						columns : 2,
						items: [
						        {boxLabel: '是', name: 'autoTraphcheck_group', inputValue: true, checked: true},
						        {boxLabel: '否', name: 'autoTraphcheck_group', inputValue: false}
						        ]
					}]
				},{
					columnWidth : .33,
					layout : 'form',
					labelWidth : 160,
					items : [{
						fieldLabel : '是否下发采集任务',
						name : 'collectWhenCheckPath',
						xtype : 'radiogroup',
			        	columns : 2,
			        	items: [
			                {boxLabel: '是', name: 'collectWhenCheckPath_group', inputValue: true, checked: true},
			                {boxLabel: '否', name: 'collectWhenCheckPath_group', inputValue: false}
			            ]
					}]
				},{
					columnWidth : .33,
					layout : 'form',
					labelWidth : 160,
					items : [{
						fieldLabel : '是否自动更新配线架、端口信息',
						name : 'autoUpateDDFAndPort',
						xtype : 'radiogroup',
			        	columns : 2,
			        	items: [
			                {boxLabel: '是', name: 'autoUpateDDFAndPort_group', inputValue: true, checked: true},
			                {boxLabel: '否', name: 'autoUpateDDFAndPort_group', inputValue: false}
			            ]
					}]
				}]
			}]
		});
		
		this.formPanel2 = new Ext.FormPanel({
			region: "north",
			bodyStyle : 'padding:5px 5px 5px 5px',
			border : true,
			autoScroll : false,
			hideBorders : false,
			items : [{
				layout : 'column',
				xtype : "fieldset",
				title : " 任务时间设置",
				defaults : {border : true},
				items : [{
					columnWidth : .3,
					labelWidth  : 90,
					layout      : 'form',
					items       : [{
						xtype : 'combo',
						fieldLabel : '同步周期',
						mode          : 'local',
						name		  : 'traphCheckPeriod',
						triggerAction : 'all',
						displayField  : 'label',
						allowBlank	 : false,
						valueField    : 'value',
						store         : new Ext.data.SimpleStore({
			               data   : [['1', '周'],['2', '月'],['3', '日']],
			                fields : ['value','label']
			            }),
			            listeners : {
			            	select : function(combo){
			            		var synchDayFs = scope.formPanel2.getForm().findField("synchDay");
			            		var data1 = [];
				                for(var i=0;i<31;i++){
				                	var rec = {'value': i,'label': (i+1) +"日"};
				                	data1.push(rec);
				                }
			            		var data2 = [{'value' : 0,'label': '星期一'},{'value' : 1,'label': '星期二'},{ 'value' :2,'label': '星期三'},{'value' :3,'label': '星期四'},{'value' :4,'label': '星期五'},{'value' :5,'label': '星期六'},{'value' :6,'label':'星期日'}];
			            		if(combo.value == 1){
			            			synchDayFs.enable();
			            			synchDayFs.store.removeAll();
			            			synchDayFs.store.loadData({data : data2},true);
			            			synchDayFs.setValue(0);
			            		} else if (combo.value == 2){
			            			synchDayFs.enable();
			            			synchDayFs.store.removeAll();
			            			synchDayFs.store.loadData({data : data1},true);
			            			synchDayFs.setValue(0);
			            		} else if(combo.value == 3){
			            			synchDayFs.disable();
			            			synchDayFs.store.removeAll();
			            			synchDayFs.setRawValue("空");
			            		}
			            	}
			            }
					}]
				},{
					columnWidth : .3,
					labelWidth  : 90,
					layout      : 'form',
					items       : [{
						xtype : 'combo',
						fieldLabel : '周期内同步日',
						name		 : 'synchDay',
						mode          : 'local',
						triggerAction : 'all',
						allowBlank	 : false,
						displayField  : 'label',
						valueField    : 'value',
						store         : synchDayStore,
					}]
				},{
					columnWidth : .3,
					labelWidth  : 90,
					layout      : 'form',
					items       : [{
						xtype : 'combo',
						fieldLabel : '同步时间',
						mode          : 'local',
						name		 : 'synchTime',
						triggerAction : 'all',
						allowBlank	 : false,
						displayField  : 'label',
						valueField    : 'value',
						store         : synchTimeStore
					}]
				}]
			}]
		});
		
		
		_items.push(this.formPanel);
		_items.push(this.formPanel2);
		this.items = _items;
		this.buttons=['-',{	text : '确定',iconCls : 'c_find',scope : this,handler:this.save}]
		this.buttonAlign='center';
		this.height= 200;
	},
	getSynchTimeStore : function(){
		var data = [];
	    for(var i=0;i<24;i++){
	    	var option = [i,(i+1) +"点"];
	    	data.push(option);
	    }
		var synchTimeStore = new Ext.data.SimpleStore({
            data   : data,
            fields : ['value','label']
        });
		return synchTimeStore;
	},
	save : function(callback){
		var scope = this;
		var form = scope.formPanel.getForm();
		var form2 = scope.formPanel2.getForm();
		var values = form.getValues();
		if(form.isValid()&&form2.isValid()){
			var newParaMap = new Object();
			if (values.autoTraphcheck_group=='true'){
				newParaMap.isAutoTraph ='true';
			}else {
				newParaMap.isAutoTraph ='false';
			}
			if (values.collectWhenCheckPath_group=='true'){
				newParaMap.collectWhenCheckPath ='true';
			}else {
				newParaMap.collectWhenCheckPath ='false';
			}
			if (values.autoUpateDDFAndPort_group=='true'){
				newParaMap.autoUpateDDFAndPort ='true';
			}else {
				newParaMap.autoUpateDDFAndPort ='false';
			}
			newParaMap.traphCheckPeriod = form2.findField("traphCheckPeriod").value;
			var sel2value = form2.findField("synchDay").value;
			if (sel2value == undefined ){
				newParaMap.traphCheckDay = 1;
			}else {
				newParaMap.traphCheckDay = sel2value;
			}
			newParaMap.traphCheckTime = form2.findField("synchTime").value;
			TraphAction.setTraphParams(newParaMap,function(data){
				if(Ext.isFunction(callback)) {
					callback.call(scope);
				}
				Ext.Msg.alert("温馨提示","保存成功");
			});
			 
		} else {
			Ext.Msg.alert("温馨提示","请填写必填字段");
		}

	}
});