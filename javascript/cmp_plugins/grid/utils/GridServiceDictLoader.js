$importjs(ctx + "/dwr/interface/GridViewAction.js");
(function(){
	var pluginsStack = {};
	var param = UrlHelper.getUrlObj(window.location.search.substring(1));
	if(param && !Ext.isEmpty(param.code)) {
		var plugins = [];
		var pluginPath = '/cmp_plugins/grid';
		var dictMeta, dictRoot, code;
		if(param.code.indexOf('.') != -1) {
			var s = param.code.split('.');
			dictRoot = s[0];
			code = s[1];
		}else {
			dictRoot = 'service_dict_maintain';
			code = param.code;
		}
		try{
			var serviceDictRoot = Frame.SERVICE_DICT_URL;
			if(Ext.isEmpty(serviceDictRoot)) {
				serviceDictRoot = ctx + '/service_dict';
			}
			dictMeta = SynDataHelper.load(serviceDictRoot+'/'+dictRoot+'.json');
		}catch(e){
			alert('获取服务目录发生异常：'+e+'，请检查配置是否正确！');
			return;
		}
		if(!Ext.isEmpty(dictMeta)) {
			var dict = dictMeta[code];
			if(Ext.isEmpty(dict)) {
				alert('未找到“code='+param.code+'”对应的服务目录！');
				return;
			}else {
				if(dict.gridwrap === true) {
					$importcss(ctx + '/jsp/component/grid/gridwrap.css');
				}
				if(dict.pluginPath) {
					pluginPath = dict.pluginPath;
				}
				Ext.applyIf(param, dict);
			}
		}else {
			return;
		}
		if(param.queryPlugin) {
			plugins = plugins.concat(param.queryPlugin);
			if(param.queryPlugin.indexOf("/") != -1) {
				param.queryPlugin = param.queryPlugin.substring(param.queryPlugin.lastIndexOf("/")+1);
			}
		}
		if(param.tbarPlugin) {
			plugins = plugins.concat(param.tbarPlugin);
			if(param.tbarPlugin.indexOf("/") != -1) {
				param.tbarPlugin = param.tbarPlugin.substring(param.tbarPlugin.lastIndexOf("/")+1);
			}
		}
		
		if(param.bbarPlugin) {
			plugins = plugins.concat(param.bbarPlugin);
			if(param.bbarPlugin.indexOf("/") != -1) {
				param.bbarPlugin =  param.bbarPlugin.substring(param.bbarPlugin.lastIndexOf("/")+1);
			}
		}
		
		if(param.buttonsPlugin) {
			plugins = plugins.concat(param.buttonsPlugin);
			if(param.buttonsPlugin.indexOf("/") != -1) {
				param.buttonsPlugin =  param.buttonsPlugin.substring(param.buttonsPlugin.lastIndexOf("/")+1);
			}
		}
		
		if(param.eventPlugin) {
			plugins = plugins.concat(param.eventPlugin);
			if(param.eventPlugin.indexOf("/") != -1) {
				param.eventPlugin = param.eventPlugin.substring(param.eventPlugin.lastIndexOf("/")+1);
			}
		}
		
		if(param.rendererPlugin) {
			plugins = plugins.concat(param.rendererPlugin);
			if(param.rendererPlugin.indexOf("/") != -1) {
				param.rendererPlugin = param.rendererPlugin.substring(param.rendererPlugin.lastIndexOf("/")+1);
			}
		}
		
		for(var i = 0; i < plugins.length; i++) {
			var plugin = plugins[i];
			if(pluginsStack[plugin]) {
				$importjs(pluginsStack[plugin]);
			}else {
				if(plugin.indexOf('.js') == -1) {
					plugin += '.js';
				}
				$importjs(ctx + pluginPath +'/'+ plugin);
			}
		}
	}
	getGridCfgByCode = function(){
		//添加查询条件是否显示的配置
		DWREngine.setAsync(false);
		GridViewAction.getQueryPanelState(param.code,function(data){
			param.queryCollapse=data;
		});
		DWREngine.setAsync(true);
		return param;
	};
})();