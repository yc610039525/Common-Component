Ext.ns("Frame.tree");
$importjs(ctx + '/cmp_res/menu/ContextMenuHelper.js');

Frame.tree.ResTreePanel = Ext.extend(Frame.tree.AsynTreePanel, {
	initComponent : function() {
		Frame.tree.ResTreePanel.superclass.initComponent.call(this);
		this.on('contextmenu',this._onContextMenu, this);
		this.on('click',function(node,e){
			var bmClassId = node.attributes.params.bmClassId;
			var cuid = node.attributes.cuid;
			if(bmClassId) {
				FrameHelper.showDetail({
					cuid : cuid,
					bmClassId : bmClassId
				});
			}
		})
		
		//禁止TreeNodeUI的onDblClick方法自动展开关闭节点
		this.on('beforedblclick',function(node, e){
			if(node.getUI().checkbox){
                node.getUI().toggleCheck();
            }
            /* 在原方法中屏蔽该部分内容
            if(!this.animating && this.node.isExpandable()){
                this.node.toggle();
            }*/
            this.fireEvent("dblclick", node, e);
			return false;
		});
	},
	_onContextMenu : function(node, e) {
		e.preventDefault();
		if(this.menuDisabled === true) {
			return false;
		}
		var bmClassId = node.attributes.params.bmClassId;
		if(!bmClassId) {
			//return false;
		}
		var obj = Ext.applyIf({
			bmClassId : bmClassId,
			text : node.text,
			cuid : node.attributes.cuid
		}, node.attributes);
		var str = '';
		if(e.target) {
			try {
				str = e.target.innerHTML;
				str = str.replace(/<[^>]+>/g, '');
			}catch(e) {
			}
		}
		//var str = node.text.replace(/<[^>]+>/g, '');
		var menu = ContextMenuHelper.build(obj, null, str);
		if(menu) {
			var x = e.getPageX(), y = e.getPageY();
			menu.showAt([x, y]);
		}
	}
});