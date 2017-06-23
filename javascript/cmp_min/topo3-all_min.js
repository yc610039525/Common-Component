Ext.ns("Frame.topo3");
Frame.topo3.TopoStore = Ext.extend(Ext.util.Observable, {
	constructor : function(a) {
		this.data = {};
		this.model = new ht.DataModel();
		Ext.apply(this, a);
		Frame.topo3.TopoStore.superclass.constructor.call(this, a);
		this.addEvents("beforeadd", "beforeload", "load")
	},
	getModel : function() {
		return this.model
	},
	loadData : function(b, a) {
		var c = this.getModel();
		if (a != true) {
			c.clear()
		}
		var e = this._readDatas(b);
		for ( var d = 0; d < e.length; d++) {
			var f = e[d];
			if (Ext.isEmpty(c.getDataById(f.getId()))) {
				if (this.fireEvent("beforeadd", this, f)) {
					c.add(f)
				}
			}
		}
	},
	load : function(a) {
		if (!this.baseParams) {
			this.baseParams = {}
		}
		if (a) {
			if (a.baseParams) {
				Ext.apply(this.baseParams, a.baseParams)
			}
		}
		if (this.fireEvent("beforeload", this, a)) {
			Ext.Ajax.request( {
				url : this.url,
				method : "POST",
				timeout : 300000,
				success : this._onLoadSuccess,
				failure : this._onLoadFailure,
				params : this.baseParams,
				scope : this
			})
		}
	},
	addData : function(c, b) {
		var a = this.getModel();
		a.add(c, b)
	},
	_onLoadSuccess : function(c, d) {
		var b;
		if (Ext.isObject(c.responseText)) {
			b = c.responseText
		} else {
			b = Ext.util.JSON.decode(c.responseText)
		}
		if (!Ext.isEmpty(b.error)) {
			Ext.Msg.alert("系统异常", b.error)
		} else {
			var a = false;
			if (!Ext.isEmpty(d.params.append)) {
				a = d.params.append
			}
			this.loadData(b, a)
		}
		this.fireEvent("load", this, b, d)
	},
	_onLoadFailure : function(a, b) {
		var c = Ext.util.JSON.decode(a.responseText);
		Ext.Msg.alert("系统异常", Ext.isEmpty(c) ? "网络连接超时" : c);
		this.fireEvent("load", this, null, b)
	},
	_readDatas : function(datas) {
		var resList = [];
		var resMap = {};
		var objMap = {};
		var dataModel = this.getModel();
		if (Ext.isObject(datas)) {
			var list = datas.d;
			Ext.each(list, function(obj, i) {
				var data;
				if (!Ext.isEmpty(obj.c)) {
					var C;
					try {
						C = eval("window." + obj.c)
					} catch (e) {
					}
					if (Ext.isFunction(C)) {
						if (!Ext.isEmpty(obj.i)) {
							data = dataModel.getDataById(obj.i);
							if (Ext.isEmpty(data)) {
								data = new C();
								data.setId(obj.i)
							}
						} else {
							data = new C()
						}
					}
				}
				if (data) {
					resList.push(data);
					resMap[data.getId()] = data;
					objMap[data.getId()] = obj
				}
			}, this)
		}
		Ext.each(resList, function(res, i) {
			var id = res.getId();
			var obj = objMap[id];
			var data = resMap[id];
			if (obj.a) {
				for ( var k in obj.a) {
					var v = obj.a[k];
					data.setAttr(k, v)
				}
			}
			if (obj.s) {
				for ( var k in obj.s) {
					data.setStyle(k, obj.s[k])
				}
			}
			if (obj.p) {
				for ( var pKey in obj.p) {
					var pVal = obj.p[pKey];
					var v;
					if (Ext.isObject(pVal) && !Ext.isEmpty(pVal.__i)) {
						v = resMap[pVal.__i]
					} else {
						if (pKey == "image") {
							v = pVal;
							if (v.indexOf(ctx) != 0) {
								v = ctx + "/" + v
							}
							v = v.replace(/\\/g, "/");
							v = v.replace(/\/\//g, "/")
						} else {
							v = pVal
						}
					}
					var f = data["set" + pKey.substring(0, 1).toUpperCase()
							+ pKey.substring(1, pKey.length)];
					if (Ext.isFunction(f)) {
						f.call(data, v)
					}
				}
			}
			if (data instanceof ht.Edge) {
				data.setStyle("edge.group", 0)
			}
			data.setStyle("select.color", "red");
			data.setStyle("select.width", "3")
		});
		return resList
	}
});
Ext.ns("Frame.topo3");
Ext.ns("Frame.topo3.plugin.event");
Frame.topo3.BaseTopoPanel = Ext
		.extend(
				Ext.Panel,
				{
					loadData : true,
					hasInnerTbar : true,
					constructor : function(a) {
						Frame.topo3.BaseTopoPanel.superclass.constructor.call(
								this, a)
					},
					initComponent : function() {
						this._initGraph();
						this._initPlugins();
						if (this.hasInnerTbar == true) {
							this._initInnerTbar()
						}
						Frame.topo3.BaseTopoPanel.superclass.initComponent
								.call(this);
						this.on( {
							afterrender : this._onCmpRender,
							resize : this._onCmpResize
						}, this)
					},
					load : function(a) {
						this.store.load( {
							baseParams : a
						})
					},
					getSm : function() {
						return this.graph.getSelectionModel()
					},
					getDm : function() {
						return this.store.getModel()
					},
					tool_zoomIn : function() {
						this.graph.zoomIn(true)
					},
					tool_zoomOut : function() {
						this.graph.zoomOut(true)
					},
					tool_overview : function() {
						this.graph.fitContent(true)
					},
					tool_layout : function(a, c) {
						var b = [ "symmetric", "circular", "hierarchical",
								"towardnorth", "towardsouth", "towardeast",
								"towardwest" ];
						if (b.indexOf(a) != -1) {
							if (!c) {
								c = Ext.emptyFn()
							}
							if (!this.autoLayout) {
								this.autoLayout = new ht.layout.AutoLayout(
										this.graph)
							}
							this.autoLayout.layout(a, c)
						}
					},
					tool_image : function() {
						var a = this.graph
								.toCanvas(document.body.style.background), c = window
								.open(), b = c.document;
						b.title = a.width + "|" + a.height;
						img = b.createElement("img");
						img.src = a.toDataURL();
						b.body.appendChild(img)
					},
					_createStore : function() {
						var a = new Frame.topo3.TopoStore( {
							url : this.dataUrl
						});
						a.on("beforeadd", this._onDataBeforeAdd, this);
						a.on("beforeload", this._onDataBeforeLoad, this);
						a.on("load", this._onDataLoad, this);
						return a
					},
					_initGraph : function() {
						this.store = this._createStore();
						this.graph = new ht.graph.GraphView(this.store
								.getModel());
						this.autoLayout = new ht.layout.AutoLayout(this.graph);
						this.view = this.graph.getView();
						this.innerFrame = new ht.widget.BorderPane();
						this.innerFrame.getView().className = "ht_topo3_view";
						this.innerFrame.setCenterView(this.graph);
						this._initGraphEvent()
					},
					_onCmpRender : function() {
						this.body.appendChild(this.innerFrame.getView());
						Ext.get(this.view).on(
								"contextmenu",
								function(b) {
									var a = this.graph
											.getDataAt(b.browserEvent);
									if (a) {
										this.fireEvent("graph_contextmenu",
												b.browserEvent, a);
										b.stopEvent()
									}
								}, this);
						if (this.loadData == true) {
							this.store.load()
						}
					},
					_initPlugins : function() {
						if (!Ext.isObject(this.pluginKeys)) {
							this.pluginKeys = {}
						}
						this._initPluginEvent()
					},
					_initPluginEvent : function() {
						var a = Frame.topo3.plugin.event;
						if (Ext.isArray(this.pluginKeys.event)) {
							Ext.each(this.pluginKeys.event, function(e) {
								var f = Frame.topo3.plugin.event[e];
								if (Ext.isFunction(f)) {
									var c = new f(this);
									if (Ext.isObject(c)) {
										for ( var d in c) {
											var b = c[d];
											if (d.indexOf("graph_") == 0) {
												this._onGraphEvent(d, b)
											} else {
												this._on(this, d, b)
											}
										}
									}
								}
							}, this)
						}
					},
					_onGraphEvent : function(b, a) {
						if (b == "graph_beforeload" || b == "graph_load") {
							this._on(this.store, b.substring(6, b.length), a)
						} else {
							this._on(this, b, a)
						}
					},
					_on : function(e, b, a, c) {
						if (!c) {
							c = e
						}
						if (Ext.isFunction(a)) {
							e.on(b, a, c)
						} else {
							if (Ext.isObject(a)) {
								var d = {};
								d[b] = a;
								if (!a.scope) {
									a.scope = c
								}
								e.on(d)
							}
						}
					},
					_initGraphEvent : function() {
						var a = this;
						this.graph.addInteractorListener(function(b) {
							a.fireEvent("graph_" + b.kind, a, b)
						});
						this.graph.getLabel = function(b) {
							if (b instanceof ht.Edge) {
								if (b.isEdgeGroupAgent()) {
									return "+" + b.getEdgeGroupSize()
								}
							}
							return b.getName()
						}
					},
					_initInnerTbar : function() {
						var e = this;
						var a = [];
						var h = [ "ZoomIn", "ZoomOut", "Overview", "Layout",
								"ExportImage" ];
						if (Ext.isArray(this.innerTbarKeys)) {
							h = this.innerTbarKeys
						}
						for ( var c = 0; c < h.length; c++) {
							var g = h[c];
							if (g == "ZoomIn") {
								a.push( {
									label : "Zoom In",
									action : this.tool_zoomIn.createDelegate(e)
								})
							} else {
								if (g == "ZoomOut") {
									a.push( {
										label : "Zoom Out",
										action : this.tool_zoomOut
												.createDelegate(e)
									})
								} else {
									if (g == "Overview") {
										a.push( {
											label : "Fit Content",
											action : this.tool_overview
													.createDelegate(e)
										})
									} else {
										if (g == "Layout") {
											this.layoutSelEl = document
													.createElement("select");
											this.layoutSelEl.onchange = function() {
												e.tool_layout.call(e,
														this.value)
											};
											var f = [ "symmetric", "circular",
													"hierarchical",
													"towardnorth",
													"towardsouth",
													"towardeast", "towardwest" ];
											for ( var b = 0; b < f.length; b++) {
												var d = document
														.createElement("option");
												d.value = f[b];
												d.innerHTML = f[b];
												this.layoutSelEl.appendChild(d)
											}
											a.push( {
												label : "Layout",
												element : this.layoutSelEl
											})
										} else {
											if (g = "ExportImage") {
												a.push( {
													label : "Export Image",
													action : this.tool_image
															.createDelegate(e)
												})
											}
										}
									}
								}
							}
						}
						if (a.length > 0) {
							this.innerTbar = new ht.widget.Toolbar(a);
							this.innerFrame.setTopView(this.innerTbar)
						}
					},
					_onDataBeforeAdd : function(a, b) {
						return true
					},
					_onDataBeforeLoad : function(a) {
						MaskHelper.mask(this.getEl(), "数据加载中,请稍候...");
						this.fireEvent("graph_beforeload", a)
					},
					_onDataLoad : function(c) {
						MaskHelper.unmask(this.getEl());
						var b = this.getDm().getDatas();
						var f = this.getSm();
						var a = [];
						var e = 0;
						b.each(function(g) {
							if (g instanceof ht.Node) {
								e++;
								var h = g.getPosition();
								if (!h || h.x == 0 || h.y == 0) {
									a.push(g)
								}
							}
						});
						if (a.length > 0) {
							if (a.length < e / 2) {
								f.setSelection(a)
							}
							var d = "symmetric";
							if (this.layoutSelEl) {
								d = this.layoutSelEl.value
							}
							this.autoLayout.layout(d, function() {
								f.clearSelection()
							})
						}
						this.fireEvent("graph_load", c)
					},
					_onCmpResize : function(a, e, c, b, d) {
						this.innerFrame.invalidate()
					}
				});