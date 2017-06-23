Ext.ns('Frame.grid.plugins.buttons');

Frame.grid.plugins.buttons.ResGridButtons = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.buttons.ResGridButtons.superclass.constructor.call(this);
		var btns = [];
		if(self.dialogArguments == 'selectRecord') {
			btns.push({
				text : '确定选择',
				iconCls : 'c_accept',
				scope : this,
				handler : this.confirmSelData
			});
			btns.push({
				text : '取消',
				iconCls : 'c_door_in',
				scope : this,
				handler : this.closeWin
			});
		}
		return btns;
	},
	confirmSelData : function() {
		var sels = this.grid.getSelectionModel().getSelections();
		if(sels.length > 0) {
			window.returnValue = sels;
			window.close();
		}else {
			Ext.Msg.alert('温馨提示', '请先选择数据！');
		}
	},
	closeWin : function() {
		window.returnValue = null;
		window.close();
	}
});