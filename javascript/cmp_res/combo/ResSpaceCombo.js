Ext.ns('Frame.combo');

Frame.combo.ResSpaceCombo = Ext.extend(Frame.combo.PopCombo, {
	pkg : 'RESCMP.QUERY',
	blurMatch : 'both',
	boName : 'XmlTemplateComboxBO',
	popUrl : ctx + '/cmp_res/combo/query/SpaceQueryPanel.jsp',
	queryType : 'SITE',
	popParams : {
		queryType : '{queryType}',
		s_PARENT_DISTRICT_CUID : '{s_PARENT_DISTRICT_CUID}',
		s_DISTRICT_CUID : '{s_DISTRICT_CUID}',
		f_DISTRICT_NAME : '{f_DISTRICT_NAME}',
		f_SITE_NAME : '{f_SITE_NAME}',
		f_ACCESSPOINT_NAME : '{f_ACCESSPOINT_NAME}',
		f_ROOM_NAME : '{f_ROOM_NAME}',
		f_SWITCHELEMENT_NAME : '{f_SWITCHELEMENT_NAME}'
	},
	constructor : function(config) {
		config.comboxCfg = {
			boName : this.boName,
			cfgParams : {
				code : this.pkg+'.'+(Ext.isEmpty(config.queryType)?this.queryType:config.queryType)
			},
			queryParams:{
				RELATED_SPACE_CUID : {
					key : 'DISTRICT_CUID',
					type : 'string',
					relation : 'like',
					value : ac.relatedDistrictCuid+'%'
				}
			},
			url : this.popUrl + Ext.urlEncode(this.popParams, '?')
		};
		Frame.combo.ResSpaceCombo.superclass.constructor.call(this, config);
	},
	initComponent : function() {
		Frame.combo.ResSpaceCombo.superclass.initComponent.call(this);
		this.on({
			scope : this,
			beforeopen : {
			    fn : this.onBeforeOpen
		    },
			afterrender : {
		    	fn : this.onAfterRender
		    }
		});
	},
	setValue : function(v) {
		if(!Ext.isEmpty(v) && Ext.isObject(v)) {
			if(!Ext.isEmpty(v.value)) {
				Frame.combo.PopCombo.superclass.setValue.call(this, v.value);
			}else {
				var obj = {};
				obj[this.valueField] = v.CUID;
				obj[this.displayField] = v.LABEL_CN;
				obj[this.dataField] = v;
				Frame.combo.PopCombo.superclass.setValue.call(this, obj);
			}
		}else {
			Frame.combo.PopCombo.superclass.setValue.call(this, v);
		}
	},
	changeType : function(type) {
		this.queryType = type;
		this.comboxCfg.cfgParams.code = this.pkg+'.'+type;
		delete this.comboxCfg.queryParams.RELATED_SPACE_CUID;
		if(this.queryType == 'DISTRICT') {
			if(!Ext.isEmpty(this.s_PARENT_DISTRICT_CUID)) {
				this.comboxCfg.queryParams.RELATED_SPACE_CUID = {
					key : 'PARENT_DISTRICT_CUID',
					type : 'string',
					relation : 'like',
					value : this.s_PARENT_DISTRICT_CUID+'%'
				};
			}else if(!Ext.isEmpty(this.s_DISTRICT_CUID)){
				this.comboxCfg.queryParams.RELATED_SPACE_CUID = {
					key : 'DISTRICT_CUID',
					type : 'string',
					relation : 'like',
					value : this.s_DISTRICT_CUID+'%'
				};
			}
		}else if(this.queryType == 'SITE' || this.queryType == 'SITE_CORE' || this.queryType == 'ACCESSPOINT' || this.queryType == 'ROOM' || this.queryType == 'SWITCHELEMENT' || this.queryType == 'SITE_ACCESSPOINT') {
			if(!Ext.isEmpty(this.s_PARENT_DISTRICT_CUID)) {
				this.comboxCfg.queryParams.RELATED_SPACE_CUID = {
					key : 'DISTRICT_CUID',
					type : 'string',
					relation : 'like',
					value : this.s_PARENT_DISTRICT_CUID+'%'
				};
			}else if(!Ext.isEmpty(this.s_DISTRICT_CUID)){
				this.comboxCfg.queryParams.RELATED_SPACE_CUID = {
					key : 'DISTRICT_CUID',
					type : 'string',
					relation : 'like',
					value : this.s_DISTRICT_CUID+'%'
				};
			}
		}
	},
	onAfterRender : function(combo) {
		this.changeType(this.queryType);
	},
	onBeforeQuery : function(qe) {
		this.changeType(this.queryType);
		Frame.combo.PopCombo.superclass.onBeforeQuery.call(this, qe);
	},
	onBeforeOpen : function() {
		if(this.queryType == 'DISTRICT') {
			this.f_DISTRICT_NAME = this.getRawValue();
			this.f_SITE_NAME = '';
			this.f_ACCESSPOINT_NAME = '';
			this.f_ROOM_NAME = '';
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_PARENT_DISTRICT_CUID : Ext.isEmpty(this.s_PARENT_DISTRICT_CUID)?'':this.s_PARENT_DISTRICT_CUID+'*',
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME
			}
		}else if(this.queryType == 'SITE') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = this.getRawValue();
			this.f_ACCESSPOINT_NAME = '';
			this.f_ROOM_NAME = '';
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_SITE_NAME : this.f_SITE_NAME
			}
		}else if(this.queryType == 'ACCESSPOINT') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = '';
			this.f_ACCESSPOINT_NAME = this.getRawValue();
			this.f_ROOM_NAME = '';
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_ACCESSPOINT_NAME : this.f_ACCESSPOINT_NAME
			}
		}else if(this.queryType == 'ROOM') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = Ext.isEmpty(this.f_SITE_NAME)?'':this.f_SITE_NAME;
			this.f_ACCESSPOINT_NAME = '';
			this.f_ROOM_NAME = this.getRawValue();
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_SITE_NAME : this.f_SITE_NAME,
				f_ROOM_NAME : this.f_ROOM_NAME
			}
		}else if(this.queryType == 'SWITCHELEMENT') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = Ext.isEmpty(this.f_SITE_NAME)?'':this.f_SITE_NAME;
			this.f_ACCESSPOINT_NAME = Ext.isEmpty(this.f_ACCESSPOINT_NAME)?'':this.f_ACCESSPOINT_NAME;
			this.f_ROOM_NAME = Ext.isEmpty(this.f_ROOM_NAME)?'':this.f_ROOM_NAME;
			this.f_SWITCHELEMENT_NAME = this.getRawValue();
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_SITE_NAME : this.f_SITE_NAME,
				f_ACCESSPOINT_NAME : this.f_ACCESSPOINT_NAME,
				f_ROOM_NAME : this.f_ROOM_NAME
			}
		}else if(this.queryType == 'SITE_ACCESSPOINT') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = this.getRawValue();
			this.f_ACCESSPOINT_NAME = this.getRawValue();
			this.f_ROOM_NAME = '';
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_SITE_NAME : this.f_SITE_NAME,
				f_ACCESSPOINT_NAME : this.f_ACCESSPOINT_NAME
			}
		}else if(this.queryType == 'SITE_CORE') {
			this.f_DISTRICT_NAME = Ext.isEmpty(this.f_DISTRICT_NAME)?'':this.f_DISTRICT_NAME;
			this.f_SITE_NAME = this.getRawValue();
			this.f_ACCESSPOINT_NAME = '';
			this.f_ROOM_NAME = '';
			this.f_SWITCHELEMENT_NAME = '';
			this.comboxCfg.urlArgs = {
				queryType : this.queryType,
				s_DISTRICT_CUID : Ext.isEmpty(this.s_DISTRICT_CUID)?'':this.s_DISTRICT_CUID+'*',
				f_DISTRICT_NAME : this.f_DISTRICT_NAME,
				f_SITE_NAME : this.f_SITE_NAME
			}
		}
		return true;
	}
});

Ext.reg("spacecombox", Frame.combo.ResSpaceCombo);