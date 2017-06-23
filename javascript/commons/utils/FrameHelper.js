$importjs(ctx + "/commons/utils/UrlHelper.js");
$importjs(ctx + '/dwr/interface/MenuAction.js');
(function(){
	/**
	 * 获取框架panel，向上找5层，如果任然没有找到，则返回undefined
	 */
	function getExplorer() {
		var scope = window;
		var explorer = undefined;
		try {
			for(var i = 0; i < 5; i++) {
				if(scope.Ext) {
					explorer = scope.Ext.getCmp("explorer_frame_panel");
					if(explorer) {
						break;
					}
				}
				scope = scope.parent;
			}
		}catch(e){}
		return explorer;
	};
	/**
	 * 右键菜单缓存
	 */
	var cmStack = {};
	/**
	 * Ext window缓存
	 */
	var winStack = {};
	
	FrameHelper = {
		/**
		 * 显示右侧详细信息栏
		 */
		showDetail : function() {
			if(!Ext.isEmpty(arguments[0])) {
				var config;
				if(!Ext.isEmpty(arguments[0]) && Ext.isObject(arguments[0])) {
					config = arguments[0];
				}else if(Ext.isString(arguments[0]) && Ext.isString(arguments[1])) {
					config = {
						bmClassId : arguments[0],
						cuid : arguments[1]
					};
				}
				if(config) {
					var explorer = getExplorer();
					if(explorer) {
						explorer.setDetail(config);
					}
				}
			}else {
				var explorer = getExplorer();
				if(explorer) {
					explorer.setDetail();
				}
			}
		},
		closeTab : function(iframe) {
			var explorer = getExplorer();
			if(explorer) {
				explorer.closeTabByIframe(iframe);
			}else {
				window.location.reload();
			}
		},
		closeTabById : function(id) {
			var explorer = getExplorer();
			if(explorer) {
				explorer.closeTabById(id);
			}else {
				window.location.reload();
			}
		},
		// 针对待办调整流程，关闭tab
		closeTabByFlow : function(url,taskId,taskName) {
			var id = url+taskId;
			id = encodeURI(id);
			id = id.replace(/[^\w]/g,'')+taskName;
			this.closeTabById(id);
		},
		/**
		 * 从框架打开url
		 * 
		 * @param url
		 *            要打开的链接
		 * @param title
		 *            框架Tab页的显示名称
		 * @param urlObj
		 *            用来替换url的参数对象
		 * @param cfg
		 *            打开窗口的样式，采用win打开还是添加到tab中
		 */
		openUrl : function(url, title, urlObj, cfg) {
			if(!Ext.isEmpty(url)) {
				//替换参数，并格式化url
				url = UrlHelper.replaceUrlArguments(url, urlObj);
				//url解码
				url = encodeURI(url);
				//将url转成对象
				var urlObj = UrlHelper.getUrlObj(url.substring(url.indexOf("?")+1));
				if(!Ext.isDefined(cfg)) {
					cfg = {};
				}
				if(!cfg.openCfg) {
					cfg.openCfg = {};
				}
				if(!cfg.openCfg.opener) {
					cfg.openCfg.opener = window;
				}
				if(cfg && cfg.openType && cfg.openType.toLowerCase() == 'extwin') {
					if(url.indexOf('?')!=-1) {
						url += '&timestamp='+Date.parse(new Date());
					}else {
						url += '?timestamp='+Date.parse(new Date());
					}
					//通过Ext.Window对象打开
					var callbackFn = cfg.callback;
					var win = new Ext.Window(Ext.apply({
						title : Ext.isEmpty(title)?'':title,
						width : Ext.isEmpty(cfg.width)?'600':cfg.width,
						height : Ext.isEmpty(cfg.height)?'550':cfg.height,
						closeAction: 'close',
						layout : 'fit',
						border : false,
						modal : true,
						html : '<iframe src="'+url+'" width="100%" height="100%" frameborder="0"></iframe>',
						listeners : {
							close : function() {
								if(!Ext.isEmpty(callbackFn)){
									var iframe = win.el.child("iframe").dom;
									var returnValue = iframe.contentWindow.returnValue;
									if(window[callbackFn] !== undefined && typeof window[callbackFn] == 'function') {
										window[callbackFn].call(win, returnValue, urlObj, url);
									}
								}
							},
							afterlayout : function() {
								if(this.inited !== true) {
									var iframe = win.el.child("iframe").dom;
									var iframeWin = iframe.contentWindow;
									iframeWin.name = win.id;
									this.inited = true;
								}
							}
						}
					}, cfg.openCfg));
					win.show();
				}else {
					var explorer = getExplorer();
					if(explorer) {
						//生成tab页id
						var id = cfg.id;
						if(Ext.isEmpty(id)) {
							id = url.replace(/[^\w]/g,'')+title;
						}
						explorer.fireEvent("addtabtores",{
							id : id,
							title : title,
							src : url,
							opener : cfg.openCfg.opener,
							forcusRefresh : cfg.refresh == true?true:false
						});
					}else {
						window.open(url);
					}
				}
			}
		},
		buildHandler : function (obj, node) {
			var bmClassId = node.bmClassId;
			var handler = obj.handler;
			if (!Ext.isEmpty(handler)) {
				if (typeof(handler) != "object") {
					try {
						handler = Ext.decode(handler);
					}catch(e){
						return;
					}
				}
				if (handler.url) {
					var title = "";
					if(Ext.isEmpty(handler.title)) {
						title = Ext.isEmpty(obj.labelCn)?"":obj.labelCn;
					}else {
						title = handler.title;
					}
					if(!Ext.isEmpty(node.text)) {
						title += node.text;
					}
					return function() {
						FrameHelper.openUrl(handler.url, title, node, handler);
					}
				} else if (handler.func) {
					var scope = window;
					var actionHandlerClass = undefined;
					for(var i = 0; i < 5; i++) {
						if(scope.IRMS && scope.IRMS.ActionHelp) {
							actionHandlerClass = scope.IRMS.ActionHelp;
							break;
						}
						scope = scope.parent;
					}
					if(actionHandlerClass && typeof actionHandlerClass[handler.func] == "function") {
						return function() {
							actionHandlerClass[handler.func].call(this,node);
						}
					}else {
						return function() {};
					}
				} else {
					return function() {};
				}
			}
		},
		fullScreen : function() {
			var explorer = getExplorer();
			if(explorer) {
				if(explorer.fullScreend == true) {
					explorer.fullScreen(true);
				}else {
					explorer.fullScreen(false);
				}
			}
		},
		copy2Clipboard : function(text) {
			var explorer = getExplorer();
			if(explorer && explorer.copy2Clipboard) {
				explorer.copy2Clipboard(text);
			}else {
				try {
					window.clipboardData.setData("Text", text);
				}catch(e){}
			}
		},
		refreshParentTab : function(url,taskId,taskName) {
			var id = url+taskId;
			id = encodeURI(id);
			id = id.replace(/[^\w]/g,'')+taskName;
			var explorer = getExplorer();
			if(explorer) {
				explorer.refreshParentTab(id);
			}
		}
	}
})();