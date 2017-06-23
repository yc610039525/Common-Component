$importjs(ctx + '/cmp_res/grid/renderer/ResGridRenderer.js');
$importjs(ctx + '/cmp_res/grid/event/ResGridEvent.js');
$importjs(ctx + '/cmp_res/grid/buttons/ResGridButtons.js');

Frame.grid.ResGridPanel = Ext.extend(Frame.grid.QueryGridPanel, {
	enableContextMenu : true,
	constructor : function(config) {
		config.gridConfig = {
			rendererPluginKeys : ['ResGridRenderer'],
			eventPluginKeys : ['ResGridEvent'],
			buttonsPluginKeys : ['ResGridButtons'],
			enableContextMenu : Ext.isDefined(config.enableContextMenu)?config.enableContextMenu==true:this.enableContextMenu==true
		};
		Frame.grid.ResGridPanel.superclass.constructor.call(this,config);
	}
});