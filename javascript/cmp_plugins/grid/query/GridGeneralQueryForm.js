Ext.ns("Frame.grid.plugins.query");

$importjs(ctx + '/commons/utils/WindowHelper.js');

$importjs(ctx + '/jslib/ext/ux/CheckColumn.js');

Frame.grid.plugins.query.GridGeneralQueryForm = Ext.extend(Object, {
	columnNum : 2,
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.query.GridGeneralQueryForm.superclass.constructor.call(this);
		this.queryForm = new Ext.form.FormPanel({
			padding : 6,
			border : false,
			layout : 'fit',
			listeners : {
				afterrender : {
					scope : this,
					fn : this.createFormItems
				}
			},
			emptyTpl : '请先配置查询条件！',
			busyTpl : '配置加载中，请稍候...',
				buttons : [{
				text : '查询',
				iconCls : 'c_find',
				scope : this,
				handler : this.doQuery
			},{
				text : '重置',
				iconCls : 'c_arrow_rotate_anticlockwise',
				scope : this.grid,
				handler : function(){
					var formPanel = this.getQueryForm();;
					formPanel.getForm().reset();
				}
			},{
				text : '配置查询条件',
				iconCls : 'c_cog',
				scope : this,
				handler : this.generalQueryPanelCfg
			}]
		});
		return this.queryForm;
	},
	doQuery : function() {
		if(this.configed != true && this.queryForm.rendered != true) {
			this.doQuery.defer(200, this);
			return;
		}
		var form = this.queryForm.getForm();
		var whereParams = this.grid.getWhereQueryItems(form);
		var extParams = this.grid.getExtQueryItems(form);
		this.grid.doQuery(whereParams, extParams);
	},
	createFormItems : function() {
		this.queryForm.update(this.queryForm.busyTpl);
		this.getGridMeta.call(this, function(data){
			this.getQueryCust(this.refreshQueryItems);
		});
	},
	refreshQueryItems : function() {
		var items = [];
		if(!Ext.isEmpty(this.gridMeta)){
			for(var i = 0 ; i < this.gridMeta.columns.length; i++) {
				var column = this.gridMeta.columns[i];
				var cust = null;
				for(var j = 0; j < this.queryCust.length; j++) {
					if(this.queryCust[j].queryKey == column.dataIndex) {
						cust = this.queryCust[j];
						break;
					}
				}
				if(!cust) {
					continue;
				}
				var item = {
					xtype : 'textfield',
					fieldLabel : column.header,
					name : column.dataIndex,
					anchor : '-20',
					value : cust.defaultValue,
					queryCfg : {
						relation : cust.condition,
						blurMatch : cust.condition.toLowerCase() == 'like'?'both':undefined
					}
				};
				if(column.type == 'enum') {
					Ext.apply(item, {
						xtype : 'asyncombox',
						comboxCfg : {
							boName : 'EnumComboxBO',
							cfgParams : {
		       					code : column.params.enumId
		       				}
						}
					});
				}else if(column.type == 'relation') {
					Ext.apply(item, {
						xtype : 'popcombox',
						hiddenName : column.dataIndex,
						comboxCfg : {
							url : ctx + '/jsp/component_ui/grid/ux/ReportGridPanel.jsp?boName=MetaGridBO&loadData=={loadData}&bmClassId={bmClassId}&queryPlugin={queryPlugin}',
		       				winArgs : "dialogWidth=800px;dialogHeight=600px",
		       				urlArgs : {
		       					bmClassId : column.params.relatedMetaId,
		       					loadData : true,
		       					queryPlugin : 'query/CommonQuery'
		       				}
		   				}
					});
				}else if(column.type == 'int') {
					Ext.apply(item, {
						xtype : 'spinnerfield'
					});
				}else if(column.type == 'float') {
					Ext.apply(item, {
						xtype : 'numberfield'
					});
				}else if(column.type == 'date') {
					Ext.apply(item, {
						xtype : "datefield",
		            	format : "Y-m-d H:i:s"
					});
				}
				items.push(item);
			}
		}
		var formItems = {
			autoScroll : true,
			layout : 'column',
			border : false,
			items : []
		};
		for(var i = 0; i < items.length; i++) {
			if(!formItems.items[i % this.columnNum]) {
				formItems.items[i % this.columnNum] = {
					columnWidth : "."+(100/this.columnNum),
					layout : 'form',
					items : []
				};
			}
			formItems.items[i % this.columnNum].items.push(items[i]);
		}
		this.queryForm.removeAll(true);
		var h = 72;
		if(items.length == 0) {
			this.queryForm.update(this.queryForm.emptyTpl);
		}else {
			this.queryForm.update('');
			this.queryForm.add(formItems);
			var row = ((items.length-(items.length%this.columnNum))/this.columnNum) + ((items.length%this.columnNum)>0?1:0);
			h += row*24;
		}
		this.queryForm.height = h;
		this.onHeightChange();
		this.configed = true;
	},
	getGridMeta : function(callback) {
		var scope = this;
		//获取列表初始化参数（columns fields）
		if(this.grid.gridCfg!=null){
			GridViewAction.getGridMeta(this.grid.gridCfg, function(data) {
			scope.gridMeta = data;
				if(Ext.isFunction(callback)) {
					callback.call(scope);
				}
			});
		}
	},
	getQueryCust : function(callback) {
		var scope = this;
		if(this.grid.gridCfg!=null){
			GridViewAction.getGridQueryCust(this.grid.gridCfg, function(data){
				scope.queryCust = data;
				if(Ext.isFunction(callback)) {
					callback.call(scope);
				}
			});
		}
	},
	generalQueryPanelCfg : function() {
		if(!this.generalQueryCfgGrid) {
			this.generalQueryCfgGrid = new Ext.grid.EditorGridPanel({
				store : new Ext.data.JsonStore({
					root : 'list',
					idProperty : 'queryKey',
					fields : ['header', 'queryKey', 'condition', 'defaultValue', {name: 'enabled', type: 'bool'}]
				}),
				cm : new Ext.grid.ColumnModel({
					defaults: {
		                width: 150,
		                menuDisabled : true,
		                sortable: false
		            },
					columns : [{
						xtype: 'checkcolumn',
						header : '状态',
						width : 60,
						dataIndex : 'enabled',
						trueText : '启用',
						falseText : '禁用'
						
					},{
						header : '查询字段',
						dataIndex : 'header',
						sortable: true
					},{
						header : '查询策略',
						dataIndex : 'condition',
						width : 80,
						editor : new Ext.form.ComboBox({
							triggerAction: 'all',
							mode: 'local',
							valueField: 'value',
							displayField: 'text',
							store : new Ext.data.ArrayStore({
								fields : ['text','value'],
								data : [
									['模糊','LIKE'],
									['精确','=']
								],
								listeners : {
								}
							})
						})
					},{
						header : '默认值',
						dataIndex: 'defaultValue',
						editor : new Ext.form.TextField({})
					}]
				})
			});
		}
		if(Ext.isArray(this.queryCust)) {
			var data = new Array();
			for(var i = 0; i < this.gridMeta.columns.length; i++) {
				var column = this.gridMeta.columns[i];
				var condition = 'LIKE';
				var tmp = null;
				for(var j = 0 ; j < this.queryCust.length; j++) {
					var cust = this.queryCust[j];
					if(cust.queryKey == column.dataIndex) {
						tmp = cust;
						tmp.enabled = true;
						tmp.header = column.header;
						break;
					}
				}
				if(tmp == null && column.header !="") {
					tmp = {
						header : column.header,
						queryKey : column.dataIndex,
						condition : condition,
						defaultValue : '',
						enabled : false,
						sortNo : 999
					};
	//				data.push(tmp);
				}
				data.push(tmp);
			}
			data.sort(function(d1, d2){
				return d1.sortNo - d2.sortNo;
			});
			this.generalQueryCfgGrid.getStore().loadData({
				list : data
			});
		}
		this.queryCfgWin = WindowHelper.openExtWin(this.generalQueryCfgGrid, {
			title : '查询条件配置',
			width : 500,
			buttons : [{
				text: '保存',
				iconCls : 'c_disk',
				scope : this,
				handler : function() {
					var scope = this;
					var range = this.generalQueryCfgGrid.getStore().getRange();
					var list = new Array();
					for(var i = 0; i < range.length; i++) {
						if(range[i].get('enabled') === true) {
							range[i].data.sortNo = i;
							list.push(range[i].data);
						}
					}
					GridViewAction.saveGridQueryCust(this.grid.gridCfg, list, function(){
						scope.getQueryCust(scope.refreshQueryItems);
						scope.queryCfgWin.hide();
					});
				}
			},{
				text: '取消',
				iconCls : 'c_door_open',
				scope : this,
				handler : function() {
					this.queryCfgWin.hide();
				}
			}]
		});
	},
	onHeightChange : function() {
		this.queryForm.doLayout();
		this.queryForm.fireEvent('heightresize');
	}
});