$importjs(ctx + "/commons/utils/UrlHelper.js");
(function(){
	var winStack = {};
	WindowHelper = {
		/**
		 * 带回调的窗口
		 * fnName 子panel的方法名
		 * callback  回调函数
		 * 说明：子Panel的 id 等于通过地址传递过去的参数panelId(方法内部)；
		 */
		openIframeWindow:function(url,title,cfg,fnName,callback){
			var panelId = Ext.id();
			url = url+"&panelId="+panelId;
			url = UrlHelper.replaceUrlArguments(url,null);
			if(url.indexOf('?')!=-1) {
				url += '&timestamp='+Date.parse(new Date());
			}else {
				url += '?timestamp='+Date.parse(new Date());
			}
			var win = new Ext.Window(Ext.apply({
				title : Ext.isEmpty(title)?'':title,
				width : 600,
				height : 600,
				closeAction: 'close',
				layout : 'fit',
				border : false,
				modal : true,
				html : '<iframe src="'+url+'" width="100%" height="100%" frameborder="0"></iframe>',
				buttons : [{
					text : '保存',
					iconCls : 'c_disk',
					handler : function(){
						var iframe = win.el.child("iframe").dom;
						var childWin = iframe.contentWindow.Ext.getCmp(panelId);
						if(!Ext.isEmpty(fnName)&&!Ext.isEmpty(callback)){
							childWin[fnName].call(childWin,callback);
						}
					}
				},{
					text : '取消',
					iconCls : 'c_door_in',
					handler : function(){
						win.hide();
					}
				}]
			},Ext.isEmpty(cfg.openCfg)?{}:cfg.openCfg));
			return win;
		},
		openExtWin : function(panel, winCfg, winParams) {
			var win = winStack[panel.id];
			var w = Ext.getBody().getWidth()-50;
			var h = Ext.getBody().getHeight()-50;
			if(Ext.isObject(winCfg)) {
				if(Ext.isEmpty(winCfg.width)) {
					winCfg.width = w;
				}
				if(Ext.isEmpty(winCfg.height)) {
					winCfg.height = h;
				}
			}else {
				winCfg = {
					width : w,
					height : h
				}
			}
			if(!win) {
				winStack[panel.id] = win = new Ext.Window(Ext.applyIf({
					closeAction: 'hide',
					layout : 'fit',
					border : false,
					modal : winCfg.modal == false?false:true,
					items : [panel]
				}, winCfg));
			}else {
				win.setWidth(winCfg.width);
				win.setHeight(winCfg.height);
			}
			if(!Ext.isEmpty(winParams) && Ext.isObject(winParams)) {
				Ext.apply(win, winParams);
			}
			win.show();
			return win;
		}
	}
})();