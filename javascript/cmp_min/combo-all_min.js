Ext.ns("Frame.combo");
if ("function" !== typeof RegExp.escape) {
	RegExp.escape = function(a) {
		if ("string" !== typeof a) {
			return a
		}
		return a.replace(/([.*+?^=!:${}()|[\]\/\\])/g, "\\$1")
	}
}
Frame.combo.AsynCombo = Ext.extend(Ext.form.ComboBox, {
	forceSelection : true,
	loadingText : "数据加载中，请稍候...",
	mode : "remote",
	triggerAction : "query",
	resizable : true,
	minChars : 1,
	queryDelay : 100,
	lazyInit : false,
	trigger1Class : "x-form-clear-trigger",
	trigger2Class : "x-form-trigger",
	hideTrigger1 : false,
	hideTrigger2 : false,
	displayField : "text",
	valueField : "value",
	dataField : "data",
	typeField : "type",
	colorField : "color",
	remarkField : "remark",
	loadData : false,
	queryParam : "LABEL_CN",
	valueParam : "CUID",
	blurMatch : "right",
	triggerAll : false,
	multSel : false,
	checkField : "checked",
	separator : ",",
	constructor : function(a) {
		if (!a.comboxCfg) {
			a.comboxCfg = new Object()
		}
		Ext.applyIf(a, {
					hiddenName : a.name
				});
		Ext.applyIf(a.comboxCfg, {
					cfgParams : new Object(),
					queryParams : new Object(),
					extParams : {
						"undefined" : {
							key : "undefined"
						}
					}
				});
		if (!Ext.isEmpty(a.pageSize)) {
			a.minListWidth = 250
		}
		Frame.combo.AsynCombo.superclass.constructor.call(this, a)
	},
	initComponent : function() {
		this.tpl = '<tpl for="."><div class="x-combo-list-item" style="color:{'
				+ this.colorField + '}" title="{' + this.remarkField + '}">{'
				+ this.displayField + "}</div></tpl>";
		var a = this;
		this.store = new Ext.data.Store({
					proxy : new Ext.ux.data.DwrProxy({
								apiActionToHandlerMap : {
									read : {
										dwrFunction : ComboxAction.getComboxData,
										getDwrArgsFunction : function() {
											return [a.comboxCfg]
										}
									}
								}
							}),
					reader : new Ext.data.JsonReader({
								root : "list",
								totalProperty : "totalCount",
								id : "value",
								fields : [{
											name : this.displayField
										}, {
											name : this.valueField
										}, {
											name : this.typeField
										}, {
											name : this.colorField
										}, {
											name : this.remarkField
										}, {
											name : this.dataField
										}, {
											name : this.checkField
										}]
							}),
					baseParams : {
						totalNum : this.multSel == true ? 1000 : 0
					},
					listeners : {
						scope : this
					}
				});
		if (Ext.isEmpty(this.value)) {
			this.setValue(this.value)
		}
		if (this.multSel == true) {
			this.tpl = '<tpl for="."><div class="x-combo-list-item"><img src="'
					+ Ext.BLANK_IMAGE_URL
					+ '" class="ux-lovcombo-icon ux-lovcombo-icon-{[values.'
					+ this.checkField
					+ '?"checked":"unchecked"]}"><div class="ux-lovcombo-item-text">{'
					+ (this.displayField || "text") + "}</div></div></tpl>";
			this.on({
						scope : this,
						beforequery : this.onBeforeQuery,
						blur : this.onRealBlur
					});
			this.onLoad = this.onLoad.createSequence(function() {
						if (this.el) {
							var b = this.el.dom.value;
							this.el.dom.value = "";
							this.el.dom.value = b
						}
					})
		} else {
			this.on({
						scope : this,
						beforequery : this.onBeforeQuery
					})
		}
		Frame.combo.AsynCombo.superclass.initComponent.call(this);
		this.triggerConfig = {
			tag : "span",
			cls : "x-form-twin-triggers",
			cn : [{
						tag : "img",
						src : Ext.BLANK_IMAGE_URL,
						alt : "",
						cls : "x-form-trigger " + this.trigger1Class
					}, {
						tag : "img",
						src : Ext.BLANK_IMAGE_URL,
						alt : "",
						cls : "x-form-trigger " + this.trigger2Class
					}]
		};
		if (Ext.isEmpty(this.value) && this.loadData === true) {
			this.on("afterrender", function(b) {
						b.getStore().load()
					});
			this.getStore().on("load", function(c, b) {
						if (b.length > 0) {
							var d = undefined;
							var e = b[0].data.value;
							this.setValue(b[0].data.value);
							this.fireEvent("change", this, e, d)
						}
					}, this)
		}
	},
	initList : function() {
		Frame.combo.AsynCombo.superclass.initList.call(this);
		if (this.multSel == true) {
			this.footer = this.list.createChild({
						cls : "x-combo-list-ft"
					});
			this.tb = new Ext.Toolbar({
						renderTo : this.footer,
						items : [{
									text : "确定",
									iconCls : "c_accept",
									scope : this,
									handler : function() {
										this.collapse()
									}
								}, {
									text : "取消",
									iconCls : "c_door_in",
									scope : this,
									handler : function() {
										this.clearValue();
										this.collapse()
									}
								}]
					});
			this.assetHeight += this.footer.getHeight()
		}
	},
	getCheckedDisplay : function() {
		var a = new RegExp(this.separator, "g");
		return this.getCheckedValue(this.displayField).replace(a,
				this.separator + " ")
	},
	getCheckedValue : function(b) {
		b = b || this.valueField;
		var d = [];
		var a = this.store.snapshot || this.store.data;
		a.each(function(c) {
					if (c.get(this.checkField)) {
						d.push(c.get(b))
					}
				}, this);
		return d.join(this.separator)
	},
	selAll : function() {
		var a = this.getStore().getRange();
		for (var b = 0; b < a.length; b++) {
			a[b].set(this.checkField, true)
		}
		this.setValue(this.getCheckedValue())
	},
	setValue : function(a) {
		var d = this;
		if (!Ext.isEmpty(a)) {
			if (this.multSel == true) {
				if (this.mode == "remote") {
					var c = {};
					DWREngine.setAsync(false);
					ComboxAction.getComboxData(c, this.comboxCfg, function(g) {
							if(!Ext.isEmpty(c)){
								if (g) {
									d.getStore().loadData(g)
								}
								d.mode = "local";
							}
							});
					DWREngine.setAsync(true)
				}
				this.store.clearFilter();
				this.store.each(function(h) {
							var g = !(!a.match("(^|" + this.separator + ")"
									+ RegExp.escape(h.get(this.valueField))
									+ "(" + this.separator + "|$)"));
							h.set(this.checkField, g)
						}, this);
				this.value = this.getCheckedValue();
				this.setRawValue(this.getCheckedDisplay());
				if (this.hiddenField) {
					this.hiddenField.value = this.value
				}
			} else {
				if (Ext.isObject(a)) {
					if (!Ext.isEmpty(a[this.valueField])) {
						var e = this.findRecord(this.valueField,
								a[this.valueField]);
						if (!e) {
							this.getStore().loadData({
										list : [a],
										totalCount : 1
									})
						}
					}
					a = a[this.valueField]
				} else {
					if (Ext.isString(a) || Ext.isNumber(a)) {
						a = a + "";
						var e = this.findRecord(this.valueField, a);
						if (!e && this.mode == "remote") {
							var c = {};
							c[this.valueParam] = a;
							DWREngine.setAsync(false);
							ComboxAction.getComboxData(c, this.comboxCfg,
									function(g) {
										if(!Ext.isEmpty(c)){
											if (g) {
												d.getStore().loadData(g)
											}
										}
									});
							DWREngine.setAsync(true)
						}
						var e = this.findRecord(this.valueField, a);
						if (!e && this.forceSelection != false) {
							a = ""
						}
					}
				}
				var f = false;
				var b = this.getValue();
				if (b != a) {
					f = true
				}
				Frame.combo.AsynCombo.superclass.setValue.call(this, a);
				if (f) {
					this.fireEvent("change", this, a, b)
				}
			}
		} else {
			this.clearValue()
		}
	},
	onTriggerClick : function() {
		var a = this.getRawValue();
		if (!this.isExpanded() && this.triggerAll === true) {
			this.setRawValue("")
		}
		Frame.combo.AsynCombo.superclass.onTriggerClick.call(this);
		this.setRawValue(a)
	},
	getTrigger : function(a) {
		return this.triggers[a]
	},
	afterRender : function() {
		Frame.combo.AsynCombo.superclass.afterRender.call(this);
		var c = this.triggers, b = 0, a = c.length;
		for (; b < a; ++b) {
			if (this["hideTrigger" + (b + 1)]) {
				c[b].hide()
			}
		}
	},
	initTrigger : function() {
		var a = this.trigger.select(".x-form-trigger", true), b = this;
		a.each(function(d, f, c) {
					var e = "Trigger" + (c + 1);
					d.hide = function() {
						var g = b.wrap.getWidth();
						this.dom.style.display = "none";
						b.el.setWidth(g - b.trigger.getWidth());
						b["hidden" + e] = true
					};
					d.show = function() {
						var g = b.wrap.getWidth();
						this.dom.style.display = "";
						b.el.setWidth(g - b.trigger.getWidth());
						b["hidden" + e] = false
					};
					this.mon(d, "click", this["on" + e + "Click"], this, {
								preventDefault : true
							});
					d.addClassOnOver("x-form-trigger-over");
					d.addClassOnClick("x-form-trigger-click")
				}, this);
		this.triggers = a.elements
	},
	getTriggerWidth : function() {
		var a = 0;
		Ext.each(this.triggers, function(d, c) {
					var e = "Trigger" + (c + 1), b = d.getWidth();
					if (b === 0 && !this["hidden" + e]) {
						a += this.defaultTriggerWidth
					} else {
						a += b
					}
				}, this);
		return a
	},
	onResize : function(a, b) {
		Frame.combo.AsynCombo.superclass.onResize.apply(this, arguments)
	},
	onDestroy : function() {
		Ext.destroy(this.triggers);
		Frame.combo.AsynCombo.superclass.onDestroy.call(this)
	},
	onTrigger1Click : function() {
		if (this.readOnly || this.disabled) {
			return
		}
		var a = this.getValue();
		this.clearValue();
		this.fireEvent("change", this, "", a)
	},
	onTrigger2Click : function() {
		this.onTriggerClick()
	},
	onSelect : function(a, b) {
		if (this.multSel == true) {
			if (this.fireEvent("beforeselect", this, a, b) !== false) {
				a.set(this.checkField, !a.get(this.checkField));
				if (this.store.isFiltered()) {
					this.doQuery(this.allQuery)
				}
				this.setValue(this.getCheckedValue());
				this.fireEvent("select", this, a, b)
			}
		} else {
			Frame.combo.AsynCombo.superclass.onSelect.call(this, a, b)
		}
	},
	onBeforeQuery : function(a) {
        if(a.query.trim()==''&& a.combo.queryType=='SITE') {
        	return false;
        }
		if (this.multSel == true) {
			a.query = a.query.replace(new RegExp(this.getCheckedDisplay()
							+ "[ " + this.separator + "]*"), "");
			delete this.lastQuery;
			this.minChars = 0
		} else {
			this.minChars = 1;
			var b = a.query;
			if (b) {
				b = b.replace(/(^\s*)|(\s*$)/g, "")
			}
			if (b != "") {
				if (this.blurMatch == "both") {
					b = "%" + b + "%"
				} else {
					if (this.blurMatch == "left") {
						b = "%" + b
					} else {
						b = b + "%"
					}
				}
				a.query = b
			} else {
				a.query = "%%"
			}
		}
		return true
	},
	onRealBlur : function() {
		this.list.hide();
		var d = this.getCheckedDisplay();
		var a = d.split(new RegExp(RegExp.escape(this.separator) + " *"));
		var c = [];
		var b = this.store.snapshot || this.store.data;
		Ext.each(a, function(e) {
					b.each(function(f) {
								if (e === f.get(this.displayField)) {
									c.push(f.get(this.valueField))
								}
							}, this)
				}, this);
		this.setValue(c.join(this.separator));
		this.store.clearFilter()
	},
	clearValue : function() {
		var a = this.getValue();
		if (this.multSel == true) {
			this.value = "";
			this.setRawValue(this.value);
			this.store.clearFilter();
			this.store.each(function(b) {
						b.set(this.checkField, false)
					}, this);
			if (this.hiddenField) {
				this.hiddenField.value = ""
			}
			this.applyEmptyText()
		}
		delete this.lastQuery;
		Frame.combo.AsynCombo.superclass.clearValue.call(this);
		if (!Ext.isEmpty(a)) {
			this.fireEvent("select", this, null, -1)
		}
	},
	initEvents : function() {
		Frame.combo.AsynCombo.superclass.initEvents.apply(this, arguments);
		if (this.multSel == true) {
			this.keyNav.tab = false
		}
	},
	getRecordValue : function() {
		var a = this.getValue();
		var b = null;
		if (!Ext.isEmpty(a)) {
			b = this.findRecord(this.valueField, this.getValue())
		}
		return b
	}
});
Ext.reg("asyncombox", Frame.combo.AsynCombo);
$importjs(ctx + "/commons/utils/WindowHelper.js");
Frame.combo.PopCombo = Ext.extend(Frame.combo.AsynCombo, {
			trigger1Class : "x-form-clear-trigger",
			trigger2Class : "x-form-search-trigger",
			constructor : function(a) {
				if (!a.comboxCfg.cfgParams) {
					a.mode = "local"
				}
				if (!a.wincfg) {
					a.wincfg = {}
				}
				Frame.combo.PopCombo.superclass.constructor.call(this, a)
			},
			initComponent : function() {
				Frame.combo.PopCombo.superclass.initComponent.call(this);
				this.addEvents("beforeopen")
			},
			onTriggerClick : Ext.emptyFn,
			onTrigger2Click : function(i) {
				if (this.disabled == true) {
					return false
				}
				if (this.fireEvent("beforeopen", this) !== false) {
					if (this.comboxCfg) {
						if (this.comboxCfg.url) {
							var c = this.comboxCfg.url;
							if (!this.wincfg.winArgs) {
								var b = Ext.getBody().getWidth() - 50;
								var f = Ext.getBody().getHeight() - 50;
								this.wincfg.winArgs = "dialogWidth=" + b
										+ "px;dialogHeight=" + f + "px;"
							}
							if (this.comboxCfg.urlArgs) {
								c = UrlHelper.replaceUrlArguments(c,
										this.comboxCfg.urlArgs)
							}
							var d = window.showModalDialog(c, "selectRecord",
									this.wincfg.winArgs);
							if (!Ext.isEmpty(d)) {
								this.setValue(d);
								var g = this.getStore().query(this.valueField,
										this.getValue(), false);
								var a = this.getStore().find(this.valueField,
										this.getValue(), 0, false);
								if (g && g.getCount() > 0) {
									this.fireEvent("select", this, g.get(0), a)
								}
							}
						} else {
							if (this.comboxCfg.panel) {
								this.wincfg.winPanel = WindowHelper.openExtWin(
										this.comboxCfg.panel,
										this.wincfg.winArgs)
							}
						}
					} else {
						return false
					}
				}
			},
			setValue : function(a) {
				if (Ext.isArray(a) && a.length > 0) {
					a = a[0]
				}
				if (Ext.isObject(a) && !Ext.isEmpty(a.json)) {
					a = a.json;
					a = {
						value : Ext.isEmpty(a.value) ? a.CUID : a.value,
						text : Ext.isEmpty(a.text) ? a.LABEL_CN : a.text,
						data : a
					}
				}
				Frame.combo.PopCombo.superclass.setValue.call(this, a)
			}
		});
Ext.reg("popcombox", Frame.combo.PopCombo);
Frame.combo.EnumCombo = Ext.extend(Frame.combo.AsynCombo, {
			triggerAll : true,
			hideTrigger1 : true,
			constructor : function(b) {
				var c = {}, a = {};
				if (b.comboxCfg) {
					if (b.comboxCfg.queryParams) {
						c = b.comboxCfg.queryParams
					}
					if (b.comboxCfg.extParams) {
						a = b.comboxCfg.extParams
					}
				}
				b.comboxCfg = {
					boName : "EnumComboxBO",
					cfgParams : {
						code : b.code
					},
					queryParams : c,
					extParams : a
				};
				Frame.combo.EnumCombo.superclass.constructor.call(this, b)
			},
			changeCode : function(a) {
				this.comboxCfg.cfgParams.code = a
			}
		});
Ext.reg("enumcombox", Frame.combo.EnumCombo);
Frame.combo.LocCombo = Ext.extend(Ext.form.ComboBox, {
			width : 100,
			editable : false,
			triggerAction : "all",
			mode : "local",
			displayField : "text",
			valueField : "value",
			dataField : "data",
			typeField : "type",
			colorField : "color",
			remarkField : "remark",
			constructor : function(a) {
				Frame.combo.LocCombo.superclass.constructor.call(this, a)
			},
			initComponent : function() {
				if (!this.datas) {
					this.datas = []
				}
				this.tpl = '<tpl for="."><div class="x-combo-list-item" style="color:{'
						+ this.colorField
						+ '}" title="{'
						+ this.remarkField
						+ '}">{' + this.displayField + "}</div></tpl>";
				if (!this.store) {
					this.store = new Ext.data.JsonStore({
								root : "list",
								id : "value",
								fields : [{
											name : this.displayField
										}, {
											name : this.valueField
										}, {
											name : this.typeField
										}, {
											name : this.colorField
										}, {
											name : this.remarkField
										}, {
											name : this.dataField
										}],
								data : {
									list : this.datas
								}
							})
				}
				Frame.combo.LocCombo.superclass.initComponent.call(this)
			},
			getValueObj : function() {
				var c = null;
				var a = this.getValue();
				if (!Ext.isEmpty(a)) {
					var b = this.findRecord(this.valueField, this.getValue());
					if (b) {
						c = b.json
					}
				}
				return c
			},
			setValue : function(a) {
				if (!Ext.isEmpty(a) && Ext.isObject(a)) {
					if (!Ext.isEmpty(a[this.valueField])) {
						var b = this.findRecord(this.valueField,
								a[this.valueField]);
						if (!b) {
							this.getStore().loadData({
										list : [a],
										totalCount : 1
									})
						}
						a = a[this.valueField]
					} else {
						a = ""
					}
				}
				return Frame.combo.LocCombo.superclass.setValue.call(this, a)
			}
		});
Ext.reg("loccombox", Frame.combo.LocCombo);