Ext.ns("Frame.grid.plugins.bbar");

Frame.grid.plugins.bbar.GridCustLayout = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.bbar.GridCustLayout.superclass.constructor.call(this);
		return {
			tooltip : '保存布局',
			text : '保存布局',
			iconCls : 'c_table_save',
			scope : this,
			handler : this.saveGridLayout
		};
	},
	getGridlayout : function() {
		var hiddenColumn = [];
		var showColumn = [];
		var columns = this.grid.getColumnModel().config;
		for(var i = 0; i < columns.length; i++) {
			var column = columns[i];
			if(column.hidden == true) {
				hiddenColumn.push({
					id : column.id,
					width : column.width,
					colIndex : i
				});
			}else {
				showColumn.push({
					id : column.id,
					width : column.width,
					colIndex : i
				});
			}
		}
		var gridCust = {
			showColumn : showColumn,
			hiddenColumn : hiddenColumn
		};
		return gridCust;
	},
	saveGridLayout : function() {
		var gridCust = this.getGridlayout();
		this.grid.ownerCt.queryCollapse=false;
		var code = this.grid.ownerCt.code;
		var state = 'false';
		if (this.grid.ownerCt.queryPanel != undefined){
			state = this.grid.ownerCt.queryPanel.collapsed;
		}
		GridViewAction.saveGridColumnCust(this.grid.gridCfg, gridCust,code,state, function() {
			Ext.Msg.alert("操作结果","保存成功!");
		});
	}
});