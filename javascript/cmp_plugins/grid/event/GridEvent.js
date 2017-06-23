Ext.ns("Frame.grid.plugins.event");

$importjs(ctx + '/commons/utils/FrameHelper.js');

Frame.grid.plugins.event.GridEvent = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.event.GridEvent.superclass.constructor.call(this);
		return {
			copy2clipboard : {
				scope : this.grid,
				fn : this.onCopy2Clipboard
			}
		};
	},
	onCopy2Clipboard : function(text) {
		FrameHelper.copy2Clipboard(text);
	}
});