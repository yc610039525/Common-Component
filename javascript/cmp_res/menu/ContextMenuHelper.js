$importjs(ctx + '/dwr/interface/MenuAction.js');
$importjs(ctx + '/commons/utils/FrameHelper.js');
$importjs(ctx + '/commons/utils/UrlHelper.js');
$importjs(ctx + "/jslib/zeroclipboard/ZeroClipboard.js");

(function(){
	var cmStack = {};
	var boName = "XmlContextMenuBO";
	var clip = null;
	Ext.onReady(function(){
		clip = new ZeroClipboard.Client();
		clip.setHandCursor( true );
		clip.addEventListener('complete', function (client, text) {
			clip.reposition(Ext.getBody().dom);
		});
	});
	
	function onCopyItemActivate(item) {
		clip.setText(item.str);
		if(clip.div) {
    		clip.reposition(item.el.dom.id);
    	}else {
    		clip.glue(item.el.dom.id);
    	}
	}
	var defaultCmList = [{
		text : '复制文本',
		iconCls : 'c_page_white_copy',
		listeners : {
			activate : onCopyItemActivate
		}
	}];
	function addDefaultCm(cm) {
		Ext.each(defaultCmList, function(dcm, idx){
			var item = new Ext.menu.Item(Ext.applyIf({
				shadow : false,
				str : cm.str
			}, dcm));
			cm.add(item);
		});
		cm.add('-');
	}
	
	ContextMenuHelper = {
		/**
		 * @param {Object} obj 实例对象，至少包括CUID、BM_CLASS_ID两个属性
		 */
		getContextMenuData : function(obj) {
			var param = {
				cuid : obj.cuid,
				bmClassId : obj.bmClassId,
				type : Ext.isEmpty(obj.cuid)?'':'实例'
			};
			var data;
			if(!Ext.isEmpty(obj.bmClassId)) {
				DWREngine.setAsync(false);
				MenuAction.loadData({boName : boName, params : param}, function(m) {
					data = m;
				});
				DWREngine.setAsync(true);
			}
			return data;
		},
		/**
		 * @param {Object} obj 实例对象，至少包括CUID、BM_CLASS_ID两个属性
		 * @param {Array/Object} menus 菜单数据，用来构建菜单
		 */
		build : function(obj, menus, str) {
			var menuId = obj.bmClassId;
			var contextmenu = cmStack[menuId];
			if(!contextmenu) {
				if(!menus) {
					menus = ContextMenuHelper.getContextMenuData(obj);
				}
				contextmenu = new Ext.menu.Menu({
					shadow : false,
					obj : obj,
					str : str
				});
				addDefaultCm(contextmenu);
				for(var i = 0; !Ext.isEmpty(menus) && i < menus.length; i++) {
					if(menus[i].children && menus[i].children.length > 0) {
						//包含二级菜单
						var group = contextmenu.add({
							text : menus[i].labelCn,
							shadow : false,
							menu : new Ext.menu.Menu({shadow : false})
						});
						var childItems = [];
						for(var j = 0; j < menus[i].children.length; j++) {
							var subMenu = menus[i].children[j];
							if(subMenu) {
								subMenu.cm = contextmenu;
								var m = createContextMenuItem(subMenu);
								childItems.push(m);
							}
						}
						group.menu.add(childItems);
					}else {
						if(menus[i]) {
							menus[i].cm = contextmenu;
							contextmenu.add(createContextMenuItem(menus[i]));
						}
					}
				}
			}else {
				//用在右键操作事件时，提供当前实例对象
				contextmenu.obj = obj;
			}
			return contextmenu;
		}
	}
	
	function getContextMenuData(param) {
		var data;
		DWREngine.setAsync(false);
		MenuAction.loadData({boName : boName, params : param}, function(m) {
			data = m;
		});
		DWREngine.setAsync(true);
		return data;
	}
	
	function createContextMenuItem(menu) {
		return new Ext.menu.Item(Ext.applyIf({
			text : menu.labelCn,
			icon : !Ext.isEmpty(menu.iconPath)? ctx + "/" + menu.iconPath : Ext.BLANK_IMAGE_URL,
			shadow : false,
			handler : buildMenuItemHandler(menu)
		}, menu));
	}
	
	function buildMenuItemHandler(menu) {
		if(!Ext.isEmpty(menu.handler)) {
			var handler = menu.handler;
			if(Ext.isString(handler)) {
				try {
					handler = Ext.decode(handler);
				}catch(e){
					return Ext.emptyFn;
				}
			}
			if (handler.url) {
				var title = "";
				if(Ext.isEmpty(handler.title)) {
					title = Ext.isEmpty(menu.labelCn)?"":menu.labelCn;
				}else {
					title = handler.title;
				}
				return function() {
					FrameHelper.openUrl(handler.url, title, menu.cm.obj, handler);
				}
			}else if (handler.fn) {
				var scope = window;
				var fns = handler.fn.split(".");
				var actionHandlerClass = window;
				for(var i = 0; i < fns.length; i++) {
					var f = actionHandlerClass[fns[i]];
					if(!f) {
						break;
					}else {
						actionHandlerClass = actionHandlerClass[fns[i]];
					}
				}
				if(i >= fns.length && Ext.isFunction(actionHandlerClass)) {
					return function() {
						actionHandlerClass.call(this, menu.cm.obj);
					}
				}else {
					return Ext.emptyFn;
				}
			}
		}else {
			return Ext.emptyFn;
		}
	}
})();