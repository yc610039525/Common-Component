Ext.ns("Frame.grid.plugins.event");

$importjs(ctx + '/dwr/interface/MenuAction.js');
$importjs(ctx + '/commons/utils/FrameHelper.js');
$importjs(ctx + '/commons/utils/UrlHelper.js');
$importjs(ctx + '/cmp_res/menu/ContextMenuHelper.js');

Frame.grid.plugins.event.ResGridEvent = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.event.ResGridEvent.superclass.constructor.call(this);
		var evts = {};
		if(this.grid.enableContextMenu != false) {
			evts['rowcontextmenu'] = {
				scope : this,
				fn : this.onContextmenu
			};
		}
		if(self.dialogArguments == 'selectRecord') {
			evts['rowdblclick'] = {
				scope : this,
				fn : this.onRowdblclick
			};
		}
		evts['rowclick'] = {
			scope : this,
			fn : this.onRowclick
		};
		return evts;
	},
	onContextmenu : function(grid, rowIndex, e) {
		e.preventDefault();
		var record = grid.getStore().getAt(rowIndex);
		grid.getSelectionModel().selectRow(rowIndex);
		var obj = Ext.applyIf({
			cuid : record.json.CUID,
			bmClassId : record.json.BM_CLASS_ID
		}, record.json);
		/*var str = [];
		Ext.each(grid.getColumnModel().config, function(col){
			if(!Ext.isEmpty(col.header)) {
				var value = record.json[col.dataIndex];
				if(Ext.isFunction(col.renderer)) {
	    			value = trimToEmpty(col.renderer.call(col, value, null, record));
	    			value = value.replace(/<[^>]+>/g, '');
	    		}
				str.push(value);
			}
		});*/
		var str = '';
		if(e.target) {
			try {
				str = e.target.innerHTML;
				str = str.replace(/<[^>]+>/g, '');
			}catch(e) {
			}
		}
		var contextmenu = ContextMenuHelper.build(obj, null, str);
		if(contextmenu) {
			var x = e.getPageX(), y = e.getPageY();
			contextmenu.showAt([x, y]);
		}
	},
	onRowdblclick : function(grid, rowIndex, e) {
		var record = grid.getStore().getAt(rowIndex);
		window.returnValue = [record];
		window.close();
	},
	onRowclick : function(grid, rowIndex, e) {
		var r = grid.getStore().getAt(rowIndex);
		FrameHelper.showDetail(r.json.BM_CLASS_ID, r.json.CUID);
	}
});