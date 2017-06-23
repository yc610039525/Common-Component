Ext.ns("IRMS.download");
$importjs(ctx + "/dwr/interface/ExportDwrAction.js");
IRMS.download.Export = Ext.extend(Ext.Panel, {
	frame : true,
	layout : 'border',
	constructor : function(config) {
		config.initParam = Ext.apply({},config);
		IRMS.download.Export.superclass.constructor.call(this, config);
	},
	initComponent : function() {
		this.loadPanel = new Ext.Panel({
			region : 'north',
			height : 22,
			html : "正在导出中"
		});
		
		var scope = this;
		
		this.grid = new Ext.grid.GridPanel({
			region : 'center',
			title : '文件下载列表',
			store : new Ext.data.JsonStore({
				root : 'list',
				fields : ['fileName','filePath','fileShowName','msg']
			}),
			columns : [
				new Ext.grid.RowNumberer(),
				{
					header : "文件",
					width : 500,
					dataIndex : 'fileName',
					renderer : scope.exportRenderer
				}]
		});
				
		this.items = [this.loadPanel, this.grid];

		this.on("afterrender", function() {
				MaskHelper.mask(Ext.getBody(), '正在导出,请稍候......');
				var scope = this;
				ExportDwrAction.exportFile(this.inputParam, function(results) {
						var time = results.seconds;
						var files = results.files;
						scope.grid.getStore().loadData({list:files});
						scope.loadPanel.update("导出完成,总计导出文件"+files.length+"个，耗时："+time+"毫秒,");
						MaskHelper.unmask(Ext.getBody());
					});
			});
		IRMS.download.Export.superclass.initComponent.call(this);
	},
	
	exportRenderer : function(value,metadata,record) {
		//encodeURI 两次将文本字符串编码为一个有效的统一资源标识符 (URI)。 sov
		var str= String.format("<span style='color:blue;cursor:pointer;text-decoration:underline'><a href='" + ctx + "/download.do?file={0}&fileName={1}'>"+value+"</span>", record.data.filePath, encodeURI(encodeURI(record.data.fileName)));
		return str;
	}
	
});

IRMS.download.Export.initParamsByUrl =  function(param){
	var exportCfg = {
		extParams : new Object(),
		queryParams : new Object()
	};
	for(var key in param) {
		if(key == "boName" || key == "code" || key == "taskId"){
			exportCfg[key] = param[key];
		}else if(key.indexOf("s_") == 0) {
			var key = key.substring(2);
			exportCfg.queryParams[key] = {
				key :  key,
				value : param["s_"+key]
			};
		}else {
			exportCfg.extParams[key] = {
				key:key,
				value:param[key]
			};
		}
	}
	return exportCfg;
};