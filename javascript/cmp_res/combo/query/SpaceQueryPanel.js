$importjs(ctx + "/dwr/interface/SpaceQueryAction.js");

Ext.namespace('Frame.combo');
Frame.combo.SpaceQueryPanel = Ext.extend(Ext.Panel, {
	border : false,
	queryType : 'ROOM',
	typeArray : ['DISTRICT', 'SITE','ACCESSPOINT','SITE_ACCESSPOINT', 'ROOM', 'SWITCHELEMENT','USER_SITE','SITE_CORE'],
	typeArrayLabel : ['区域', '站点','资源点','站点/资源点','机房','业务网元'],
	frame : true,
	singleSelect : true,
	constructor : function(config) {
		Frame.combo.SpaceQueryPanel.superclass.constructor.call(this, config);
	},
	initComponent : function() {
		this.layout = 'border';
		this.queryForm = this.createQueryForm({
			region : 'north',
			height : 60,
			border : false
		});
		this.gridCard = this.createGridCard({
			region : 'center',
			border : true
		});
		this.items = [this.queryForm, this.gridCard];
		if(self.dialogArguments == 'selectRecord') {
			this.buttons = [{
				text : '确定选择',
				iconCls : 'c_accept',
				scope : this,
				handler : function() {
					var curGrid = this.gridCard.getLayout().activeItem;
					if(this.returnTypes.indexOf(curGrid.type) == -1) {
						var returnTypeLabels = [];
						Ext.each(this.returnTypes, function(type){
							returnTypeLabels.push('<font color="red">'+this.typeArrayLabel[this.typeArray.indexOf(type)]+'</font>');
						}, this);
						Ext.Msg.alert('温馨提示', '要求选择【'+returnTypeLabels.join('/')+'】！');
						return;
					}
					var result = this.getResult();
					if(result.length > 0) {
						window.returnValue = this.singleSelect==false?result:result[0];
						window.close();
					}else {
						Ext.Msg.alert('温馨提示', '未选择任何数据！');
					}
				}
			}, {
				text : '取消',
				iconCls : 'c_door_in',
				scope : this,
				handler : function() {
					window.returnValue = null;
					window.close();
				}
			}];
		}
		Frame.combo.SpaceQueryPanel.superclass.initComponent.call(this);
		this.on({
			afterrender : function(cmp){
				cmp.refreshData(cmp.inputParam);
			}
		});
	},
	createQueryForm : function(cfg) {
		var items = [];
		var fsMap = {};
		this.districtFs = this.createQueryField('DISTRICT', {
			fieldLabel : '区域'
		});
		items.push(this.districtFs);
		fsMap['DISTRICT'] = this.districtFs;
		this.siteFs = this.createQueryField('SITE', {
			fieldLabel : '站点',
			hidden : false
		});
		items.push(this.siteFs);
		fsMap['SITE'] = this.siteFs;
		this.accesspointFs = this.createQueryField('ACCESSPOINT', {
			fieldLabel : '资源点',
			hidden : false
		});
		items.push(this.accesspointFs);
		fsMap['ACCESSPOINT'] = this.accesspointFs;
		this.roomFs = this.createQueryField('ROOM', {
			fieldLabel : '机房',
			hidden : true
		});
		items.push(this.roomFs);
		fsMap['ROOM'] = this.roomFs;
		this.switchElementFs = this.createQueryField('SWITCHELEMENT', {
			fieldLabel : '业务网元'
		});
		items.push(this.switchElementFs);
		fsMap['SWITCHELEMENT'] = this.switchElementFs;
		var panel = new Ext.form.FormPanel(Ext.apply({
			labelWidth : 50,
			fsMap : fsMap,
			items : items
		}, cfg));
		return panel;
	},
	createGridCard : function(cfg) {
		var items = [];
		var gridMap = {};
		this.districtGrid = this.createQueryGrid('DISTRICT', {
		});
		items.push(this.districtGrid);
		gridMap['DISTRICT'] = this.districtGrid;
		this.siteGrid = this.createQueryGrid('SITE', {
		});
		items.push(this.siteGrid);
		gridMap['SITE'] = this.siteGrid;
		this.accesspointGrid = this.createQueryGrid('ACCESSPOINT', {
		});
		items.push(this.accesspointGrid);
		gridMap['ACCESSPOINT'] = this.accesspointGrid;
		this.roomGrid = this.createQueryGrid('ROOM', {
		});
		items.push(this.roomGrid);
		gridMap['ROOM'] = this.roomGrid;
		this.switchElementGrid = this.createQueryGrid('SWITCHELEMENT', {
		});
		items.push(this.switchElementGrid);
		gridMap['SWITCHELEMENT'] = this.switchElementGrid;
		var panel = new Ext.Panel(Ext.apply({
			activeItem : 0,
			layout : 'card',
			gridMap : gridMap,
			items : items
		}, cfg));
		return panel;
	},
	createQueryGrid : function(type, cfg) {
		var scope = this;
		var sm = null;
		if(this.singleSelect == false) {
			sm = new Ext.grid.CheckboxSelectionModel({});
		}else {
			sm = new Ext.grid.RowSelectionModel({
				singleSelect : true
			});
		}
		var grid = new Frame.grid.DataGridPanel({
			type : type,
			gridCfg : {
				cfgParams : {
					templateId : 'RESCMP_QUERY_'+type,
					type : type
				}
			},
			hasPageBar : true,
			totalNum : 200,
			pageSize : 200,
			loadData : false,
			sm : sm,
			countRenderer : function(value, metaData, record, rowIndex, colIndex, store) {
				var mappingCol = metaData.id;
				var countType = mappingCol.substring(0, mappingCol.indexOf('_'));
				if(!Ext.isEmpty(value)) {
					if((scope.typeArray.indexOf(scope.queryType) >= scope.typeArray.indexOf(countType))) {
						var countRendererTpl = "<span style='color:blue;cursor:pointer;text-decoration:underline' onclick=\"javascript:Frame.combo.SpaceQueryPanel.onHyperClick('{0}', '{1}', '{2}', '{3}', '{4}')\">{5}</span>";
						return value == '0' ? '0' : String.format(countRendererTpl, scope.id, countType, record.json.CUID, record.json.LABEL_CN, record.json.BM_CLASS_ID, value);
					}else {
						return value;
					}
				}
			},
			listeners : {
				scope : this,
				rowdblclick : function(grid, rowIndex, e) {
					if(self.dialogArguments == 'selectRecord') {
						if(this.returnTypes.indexOf(grid.type) == -1) {
							var returnTypeLabels = [];
							Ext.each(this.returnTypes, function(type){
								returnTypeLabels.push('<font color="red">'+this.typeArrayLabel[this.typeArray.indexOf(type)]+'</font>');
							}, this);
							Ext.Msg.alert('温馨提示', '要求选择【'+returnTypeLabels.join('/')+'】！');
							return;
						}
						var result = this.getResult();
						if(result.length > 0) {
							window.returnValue = result[0];
							window.close();
						}
					}
				}
			}
		});
		return grid;
	},
	createQueryField : function(type, cfg) {
		var scope = this;
		var f = new Ext.form.TwinTriggerField(Ext.apply({
			name : type+'_NAME',
			type : type,
			anchor : '100%',
			trigger1Class : 'x-form-clear-trigger',
			trigger2Class : 'x-form-search-trigger',
			onTrigger1Click : function(event, trigger, preventDefault) {
				//this.clearValue();
			   if(this.hiddenField){
	               this.hiddenField.value = '';
	           }
	           this.setRawValue('');
	           this.lastSelectionText = '';
	           this.applyEmptyText();
	           this.value = '';
			},
			onTrigger2Click : function(event, trigger, preventDefault) {
				scope.doQuery(type);
			},
			listeners : {
				specialkey : this.doEnterKey,
				scope : this
			}
		}, cfg));
		return f;
	},
	doEnterKey : function(field, event, trigger) {
		if (event.getKey() == event.ENTER) {
			this.doQuery(field.type);
		}
	},
	refreshData : function(inputParam) {
		//this.queryType = 'ROOM';
		this.queryType = 'SWITCHELEMENT';
		this.districtGrid.gridCfg.urlParams = {};
		var form = this.queryForm.getForm();
		var managerValue = ac.managerDistrictCuid;
		managerValue = managerValue.replace("[",'').replace("]", '').replace(/\ /g,'');
		Ext.iterate(this.inputParam, function(key, value, obj){
			if(key == 'singleSelect'){
				this.singleSelect = (value+''=='false');
			}else if(key == 'queryType') {
				if(this.typeArray.indexOf(value) != -1) { 
					this.queryType = value;
				}
			}else if(key == 'returnType') {
				var types = value.split(',');
				if(!Ext.isDefined(this.returnTypes)) {
					this.returnTypes = [];
				}
				Ext.each(types, function(t){
					if(this.typeArray.indexOf(t) != -1) {
						this.returnTypes.push(t);
					}
				}, this);
			}else if(key.indexOf('s_') == 0) {
				key = key.substring(2);
				key = "subStr("+key+",0,26)";
				var relation = '=';
				
				if(!Ext.isEmpty(value)){
				    if(value.indexOf('*')) {
					    //value = value.replace(/\*/g, '%');
				    	value = managerValue;
					    relation = 'in';
				    }
				    this.districtGrid.gridCfg.urlParams[key] = {
							key : key,
							value : value,
							relation : relation,
							type : 'string'
					};
				}
			}else if(key.indexOf('f_') == 0) {
				key = key.substring(2);
				var f = form.findField(key);
				if(f) {
					f.setValue(value);
				}
			}
		}, this);
		if(Ext.isEmpty(this.returnTypes)) {
			if(this.queryType == 'SITE_ACCESSPOINT'){
				this.returnTypes = ['SITE','ACCESSPOINT'];
			}else if(this.queryType == 'SITE_CORE'){
				var f = form.findField('SITE_NAME');
				if(f) {
					f.setValue('');
				}
				this.returnTypes = ['SITE'];
			}else{
				this.returnTypes = [this.queryType];
			}
		}
		var northHeight = 80;
		if(this.typeArray.indexOf(this.queryType) == 7) {
			northHeight = 80;
			this.districtFs.show();
			this.siteFs.show();
			this.accesspointFs.hide();
			this.roomFs.hide();
			this.switchElementFs.hide();
		}else if(this.typeArray.indexOf(this.queryType) > 4) {
			northHeight = 160;
			this.districtFs.show();
			this.siteFs.show();
			this.accesspointFs.show();
			this.roomFs.show();
			this.switchElementFs.show();
		}else if(this.typeArray.indexOf(this.queryType) > 3) {
			northHeight = 120;
			this.districtFs.show();
			this.siteFs.show();
			this.accesspointFs.hide();
			this.roomFs.show();
			this.switchElementFs.hide();
		}else if(this.typeArray.indexOf(this.queryType) > 2) {
			northHeight = 120;
			this.districtFs.show();
			this.siteFs.show();
			this.accesspointFs.show();
			this.roomFs.hide();
			this.switchElementFs.hide();
		}else if(this.typeArray.indexOf(this.queryType) > 1) {
			northHeight = 80;
			this.districtFs.show();
			this.siteFs.hide();
			this.accesspointFs.show();
			this.roomFs.hide();
			this.switchElementFs.hide();
		}else if(this.typeArray.indexOf(this.queryType) > 0) {
			northHeight = 80;
			this.districtFs.show();
			this.siteFs.show();
			this.roomFs.hide();
			this.accesspointFs.hide();
			this.switchElementFs.hide();
		}else {
			northHeight = 40;
			this.districtFs.show();
			this.siteFs.hide();
			this.accesspointFs.hide();
			this.roomFs.hide();
			this.switchElementFs.hide();
		}
		this.queryForm.setHeight(northHeight);
		var loadType = 'DISTRICT';
		if(!Ext.isEmpty(this.switchElementFs.getValue()) && this.switchElementFs.hidden != true) {
			loadType = 'SWITCHELEMENT';
		}else if(!Ext.isEmpty(this.accesspointFs.getValue()) && this.accesspointFs.hidden != true) {
			loadType = 'ACCESSPOINT';
		}else if(!Ext.isEmpty(this.roomFs.getValue()) && this.roomFs.hidden != true) {
			loadType = 'ROOM';
		}else if(this.siteFs.hidden != true) {
			if(this.queryType == 'SITE_CORE'){
				loadType = 'SITE_CORE';
			}else{
				loadType = 'USER_SITE';
			}
		}
		this.doQuery(loadType);
	},
	doQuery : function(type,hideQuerys) {
		if(this.queryForm.rendered != true) {
			this.doQuery.defer(200, this, [type, hideQuerys]);
			return;
		}
		var form = this.queryForm.getForm();
		var vals = form.getValues();
		if((vals.ACCESSPOINT_NAME != null && vals.ACCESSPOINT_NAME != '') && (vals.SITE_NAME != null && vals.SITE_NAME != '')){
			Ext.Msg.alert('温馨提示', '站点和资源点不能同时有值,请清空其中一个再进行查询！');
			return;
		}
		if ((vals.ACCESSPOINT_NAME != null && vals.ACCESSPOINT_NAME != '') && type == 'SITE'){
			Ext.Msg.alert('温馨提示', '请确认是否是查询资源点');
			return;
		}
		if ((vals.SITE_NAME != null && vals.SITE_NAME != '') && type == 'ACCESSPOINT'){
			Ext.Msg.alert('温馨提示', '请确认是否是查询站点');
			return;
		}
		var whereParams = {};
		Ext.iterate(vals, function(key, value){
			if(!Ext.isEmpty(value)) {
				whereParams[key] = {
					key : key,
					value : '%'+value+'%',
					relation : 'like'
				}
			}
		});
		if(Ext.isDefined(hideQuerys) && Ext.isObject(hideQuerys)) {
			Ext.apply(whereParams, hideQuerys);
		}
		var flag=0;
		if(type=='USER_SITE'){
			type='SITE'
			flag=1;
		}
		if(type == 'SITE_CORE'){
			type='SITE'
			flag=2;
		}
		var grid = this.getGridByType(type);
		if(flag==1){
			grid.gridCfg.cfgParams.templateId='RESCMP_QUERY_USER_SITE';
			whereParams = {};
		}
		this.gridCard.getLayout().setActiveItem(grid.id);
		grid.doQuery(whereParams);
		var id = '';
		if (type=='SITE'){
			id = 'RESCMP_QUERY_SITE';
			if(flag==2){
				id = 'RESCMP_QUERY_SITE_CORE';
			}
		}else if (type=='ACCESSPOINT'){
			id = 'RESCMP_QUERY_ACCESSPOINT';
		}else if (type=='DISTRICT'){
			id = 'RESCMP_QUERY_DISTRICT';
		}else if (type=='ROOM'){
			id = 'RESCMP_QUERY_ROOM';
		}else if (type=='SWITCHELEMENT'){
			id = 'RESCMP_QUERY_SWITCHELEMENT';
		}
		this.setType.defer(1000, this, [ grid,id ]);
//        DWREngine.setAsync(true);
	},
	setType:function(grid,id){
		grid.gridCfg.cfgParams.templateId=id;
	},
	getResult : function() {
		var grid = this.gridCard.getLayout().activeItem;
		var result = [];
		var sels = grid.getSelectionModel().getSelections();
		if(sels.length > 0) {
			sels.sort(function(a, b){
				return store.indexOf(a) - store.indexOf(b);
			});
			Ext.each(sels, function(sel) {
				result.push(sel.json);
			});
		}
		return result;
	},
	getQueryFieldByType : function(type) {
		return this.queryForm.fsMap[type];
	},
	getGridByType : function(type) {
		return this.gridCard.gridMap[type];
	}
});
Frame.combo.SpaceQueryPanel.onHyperClick = function(cmpId, type, cuid, labelCn, bmClassId) {
	var scope = Ext.getCmp(cmpId);
	var hideQuerys = {};
	var key = bmClassId+'_CUID';
	hideQuerys[key] = {
		key : key,
		value : cuid+'%',
		relation : 'like',
		type : 'string'
	};
	scope.doQuery(type, hideQuerys);
	var form = scope.queryForm.getForm();
	var f = form.findField(bmClassId+'_NAME');
	if(f) {
		f.setValue(labelCn);
	}
}