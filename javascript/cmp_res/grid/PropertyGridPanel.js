Ext.ns("Frame.grid");
$importjs(ctx + "/dwr/interface/PropertyGridViewAction.js");
$importjs(ctx + "/cmp_res/grid/renderer/ResGridRenderer.js");
if(!Ext.grid.GridView.prototype.templates) {   
	    Ext.grid.GridView.prototype.templates = {};   
	}
	Ext.grid.GridView.prototype.templates.cell = new Ext.Template(   
	     '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} x-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>' ,   
	     '<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>' ,   
	     '</td>'
	);
	
Frame.grid.PropertyRecord = Ext.data.Record.create([
    'name', 'value', 'group', 'groupSort', 'columnSort'
]);

Frame.grid.PropertyStore = Ext.extend(Ext.util.Observable, {
    constructor : function(grid, source){
        this.grid = grid;
        this.store = new Ext.data.GroupingStore({
            recordType : Frame.grid.PropertyRecord,
            groupField : 'group'
        });
        this.store.multiSortInfo.sorters = [{
        	field: 'groupSort',
            direction: 'ASC'
        },{
        	field: 'columnSort',
            direction: 'ASC'
        }];
        if(source){
            this.setSource(source);
        }
        Frame.grid.PropertyStore.superclass.constructor.call(this);    
    },
    
    // protected - should only be called by the grid.  Use grid.setSource instead.
    setSource : function(o){
        this.source = o;
        this.store.removeAll();
        var data = [];
        for(var k in o) {
        	var type = o[k].type;
        	if(type == 'system') {
        		continue;
        	}
        	var name = o[k].name;
        	var value = o[k].value;
        	var group = o[k].group;
        	var groupSort = Ext.isEmpty(o[k].groupSort)? 999:o[k].groupSort*1;
        	var columnSort = Ext.isEmpty(o[k].columnSort)? 999:o[k].columnSort*1;
        	if(!group) {
        		group = "默认分组";
        	}
        	data.push(new Frame.grid.PropertyRecord({name: name, value: value, group: group, type: type, groupSort:groupSort, columnSort: columnSort}, k));
        }
        this.store.loadRecords({records: data}, {}, true);
    },

    // private
    getProperty : function(row){
       return this.store.getAt(row);
    },

    // private
    setValue : function(prop, value, group, create){
        var r = this.getRec(prop);
        if(r){
            r.set('value', value);
            this.source[prop] = {name: prop, value: value, group: r.get('group')};
        }else if(create){
        	var v = {name: prop, value: value, group:group};
            this.source[prop] = v;
            r = new Frame.grid.PropertyRecord(v, prop);
            this.store.add(r);

        }
    },
    
    // private
    remove : function(prop){
        var r = this.getRec(prop);
        if(r){
            this.store.remove(r);
            delete this.source[prop];
        }
    },
    
    // private
    getRec : function(prop){
        return this.store.getById(prop);
    },

    // protected - should only be called by the grid.  Use grid.getSource instead.
    getSource : function(){
        return this.source;
    }
});

Frame.grid.PropertyColumnModel = Ext.extend(Ext.grid.ColumnModel, {
	nameText : '属性名',
    valueText : '属性值',
    groupText : '属性组',
    dateFormat : 'Y-m-d h:i:s',
    trueText: '是',
    falseText: '否',
    constructor : function(grid, store){
	    this.grid = grid;
	    Frame.grid.PropertyColumnModel.superclass.constructor.call(this, [
	        {header: this.nameText, width:100, sortable: true, dataIndex:'name', id: 'name', menuDisabled:true},
	        {header: this.valueText, width:200, sortable: true, resizable:false, dataIndex: 'value', id: 'value', menuDisabled:true},
	        {header: this.groupText,dataIndex: 'group', id: 'group', hidden:true}
	    ]);
	    this.store = store;
	    this.renderCellDelegate = this.renderCell.createDelegate(this);
	    this.renderPropDelegate = this.renderProp.createDelegate(this);
    },

    // private
    renderDate : function(dateVal){
        return dateVal.dateFormat(this.dateFormat);
    },

    // private
    renderBool : function(bVal){
        return this[bVal ? 'trueText' : 'falseText'];
    },

    // private
    getRenderer : function(col){
        return col == 1 ?
            this.renderCellDelegate : this.renderPropDelegate;
    },

    // private
    renderProp : function(v, metadata, record){
    	var prop = this.getPropertyName(v);
    	if(record.data.type == 'relation') {
    		prop = '<font color="blue">'+prop+'</font>';
    	}else if(record.data.type == 'enum') {
    		prop = '<font color="green">'+prop+'</font>';
    	}
        return prop;
    },

    // private
    renderCell : function(val, meta, rec){
        var renderer
        if(this.grid.customRenderers){
        	renderer = this.grid.customRenderers[rec.get('name')];
        }
        if(renderer){
        	if(Ext.isString(renderer)) {
        		eval("renderer = "+renderer);
        	}
            return renderer.apply(this, arguments);
        }
        var rv = val;
        if(Ext.isDate(val)){
            rv = this.renderDate(val);
        }else if(typeof val == 'boolean'){
            rv = this.renderBool(val);
        }
        return Ext.util.Format.htmlEncode(rv);
    },

    // private
    getPropertyName : function(name){
        var pn = this.grid.propertyNames;
        return pn && pn[name] ? pn[name] : name;
    }
});

Frame.grid.PropertyGridPanel = Ext.extend(Ext.grid.GridPanel, {
	enableColumnMove:false,
    stripeRows:false,
    trackMouseOver: false,
    enableHdMenu : false,
    loadData : false,
    view: new Ext.grid.GroupingView({
        forceFit:true,
        groupTextTpl: '{text} ({[values.rs.length]} 项属性)'
    }),
    constructor : function(config) {
    	//自动加载数据
		if(config.gridCfg && config.loadData != false) {
			DWREngine.setAsync(false);
			PropertyGridViewAction.loadData(config.gridCfg, function(gridMeta){
				if(gridMeta) {
					config.initData = gridMeta;
					scope.customRenderers = gridMeta.customRenderers;
					config.propertyNames = gridMeta.propertyNames;
					config.source = gridMeta.data;
				}
			});
			DWREngine.setAsync(true);
		}
		Frame.grid.PropertyGridPanel.superclass.constructor.call(this,config);
		this.addEvents('dataChange');
    },
    initComponent : function(){
        this.customRenderers = this.customRenderers || {};
        this.customEditors = this.customEditors || {};
        this.lastEditRow = null;
        var store = new Frame.grid.PropertyStore(this);
        this.propStore = store;
        var cm = new Frame.grid.PropertyColumnModel(this, store);
        this.addEvents(
            'beforepropertychange',
            'propertychange'
        );
        this.cm = cm;
        this.ds = store.store;
		Frame.grid.PropertyGridPanel.superclass.initComponent.call(this);
    },

    // private
    onRender : function(){
        Frame.grid.PropertyGridPanel.superclass.onRender.apply(this, arguments);

        this.getGridEl().addClass('x-props-grid');
    },

    // private
    afterRender: function(){
        Frame.grid.PropertyGridPanel.superclass.afterRender.apply(this, arguments);
        if(this.source){
            this.setSource(this.source);
        }
    },

    setSource : function(source){
        this.propStore.setSource(source);
    },

    getSource : function(){
        return this.propStore.getSource();
    },
    
    setProperty : function(prop, value, create){
        this.propStore.setValue(prop, value, create);    
    },
    
    removeProperty : function(prop){
        this.propStore.remove(prop);
    },
    
    setValue : function(config) {
    	if(!config) {
    		this.initData = {};
    		this.propertyNames = {};
    		this.setSource({});
    	}else {
			var scope = this;
			var boName = undefined;
			if(this.gridCfg) {
				boName = this.gridCfg.boName;
			}
			var param = {
				boName : boName,
				cfgParams : config
			};
			MaskHelper.mask(this.getEl(),"数据加载中,请稍候...");
			DWREngine.setAsync(false);
			var gridMeta;
			PropertyGridViewAction.loadData(param, function(gridMeta){
				if(gridMeta) {
					scope.initData = gridMeta;
					scope.customRenderers = gridMeta.customRenderers;
					scope.propertyNames = gridMeta.propertyNames;
					scope.setSource(gridMeta.data);
				}
				scope.gridMeta = gridMeta;
				scope.fireEvent("dataChange", config);
				MaskHelper.unmask(scope.getEl());
			});
			DWREngine.setAsync(true);
    	}
	}
});

Frame.grid.PropertyGridPanel.initParamsByUrl =  function(param){   
	var gridCfg = {
		cfgParams : {}
	};
	for(var key in param) {
		if(key == "boName"){
			gridCfg.boName = param[key];
		}else {
			gridCfg.cfgParams[key] = param[key];
		}
	}
	return gridCfg;
};