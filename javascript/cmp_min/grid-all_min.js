Ext.ns("Frame.grid");
Ext.ns("Frame.grid.plugins.bbar");
Ext.ns("Frame.grid.plugins.tbar");
Ext.ns("Frame.grid.plugins.event");
Ext.ns("Frame.grid.plugins.renderer");
Ext.ns("Frame.grid.plugins.buttons");
Frame.grid.BaseGridView = Ext
		.extend(
				Ext.grid.GridView,
				{
					copyColText : "复制列",
					constructor : function(a) {
						var b = null;
						b = new ZeroClipboard.Client();
						b.setHandCursor(true);
						b.addEventListener("complete", function(c, d) {
							b.reposition(Ext.getBody().dom)
						});
						a.clip = b;
						Frame.grid.BaseGridView.superclass.constructor.call(
								this, a)
					},
					onHeaderClick : Ext.emptyFn,
					afterRenderUI : function() {
						var a = this.grid;
						this.initElements();
						Ext.fly(this.innerHd).on("click", this.handleHdDown,
								this);
						this.mainHd.on( {
							scope : this,
							mouseover : this.handleHdOver,
							mouseout : this.handleHdOut,
							mousemove : this.handleHdMove
						});
						this.scroller.on("scroll", this.syncScroll, this);
						if (a.enableColumnResize !== false) {
							this.splitZone = new Ext.grid.GridView.SplitDragZone(
									a, this.mainHd.dom)
						}
						if (a.enableColumnMove) {
							this.columnDrag = new Ext.grid.GridView.ColumnDragZone(
									a, this.innerHd);
							this.columnDrop = new Ext.grid.HeaderDropZone(a,
									this.mainHd.dom)
						}
						if (a.enableHdMenu !== false) {
							this.hmenu = new Ext.menu.Menu( {
								id : a.id + "-hctx"
							});
							this.hmenu
									.add( {
										itemId : "copyCol",
										text : this.copyColText,
										cls : "xg-hmenu-copy-col",
										expandMenu : this.showMenu
												.createDelegate(this),
										listeners : {
											scope : this,
											activate : this.onCopyBtnActivate
										}
									});
							this.hmenu.add( {
								itemId : "asc",
								text : this.sortAscText,
								cls : "xg-hmenu-sort-asc"
							}, {
								itemId : "desc",
								text : this.sortDescText,
								cls : "xg-hmenu-sort-desc"
							});
							if (a.enableColumnHide !== false) {
								this.colMenu = new Ext.menu.Menu( {
									id : a.id + "-hcols-menu"
								});
								this.colMenu.on( {
									scope : this,
									beforeshow : this.beforeColMenuShow,
									itemclick : this.handleHdMenuClick
								});
								this.hmenu.add("-", {
									itemId : "columns",
									hideOnClick : false,
									text : this.columnsText,
									menu : this.colMenu,
									iconCls : "x-cols-icon"
								})
							}
							this.hmenu.on("itemclick", this.handleHdMenuClick,
									this)
						}
						if (a.trackMouseOver) {
							this.mainBody.on( {
								scope : this,
								mouseover : this.onRowOver,
								mouseout : this.onRowOut
							})
						}
						if (a.enableDragDrop || a.enableDrag) {
							this.dragZone = new Ext.grid.GridDragZone(a, {
								ddGroup : a.ddGroup || "GridDD"
							})
						}
						this.updateHeaderSortState()
					},
					showMenu : function() {
						var b = this.ds, d = this.cm
								.getDataIndex(this.hdCtxIndex), c = this.cm.config[this.hdCtxIndex];
						var f = c.renderer;
						var e = [];
						var a = b.getRange();
						Ext.each(a,
								function(g, h) {
									var i = g.get(d);
									if (Ext.isFunction(f)) {
										i = trimToEmpty(c.renderer.call(c, i,
												null, g));
										i = i.replace(/<[^>]+>/g, "")
									}
									e.push(i)
								}, this);
						this.clip.setText(e.join("\n"))
					},
					onCopyBtnActivate : function(a) {
						if (this.clip.div) {
							this.clip.reposition(a.el.dom.id)
						} else {
							this.clip.glue(a.el.dom.id)
						}
						return true
					}
				});
Frame.grid.BaseGroupingView = Ext.extend(Ext.grid.GroupingView, {
	onHeaderClick : Ext.emptyFn
});
Frame.grid.BasePagingToolbar = Ext
		.extend(
				Ext.PagingToolbar,
				{
					busyIconCls : "x-status-busy",
					busyText : "数据统计中...",
					displayInfo : true,
					initComponent : function() {
						Frame.grid.BasePagingToolbar.superclass.initComponent
								.call(this);
						this.store = this.grid.getStore();
						this.grid.on("beforequery", function() {
							this.doLoad(this.cursor, false)
						}, this)
					},
					changePageSize : function(a) {
						if (Ext.isNumber(a)) {
							this.pageSize = a;
							this.doLoad(0)
						}
					},
					doLoad : function(e, c) {
						this.beforeLoad();
						var d = {}, a = this.getParams();
						d[a.start] = e;
						d[a.limit] = this.pageSize;
						if (this.fireEvent("beforechange", this, d) !== false) {
							this.loadPageInfo(d);
							if (c != false) {
								for ( var b in a) {
									this.store.setBaseParam(b, d[a[b]])
								}
								this.store.load( {
									params : d
								})
							}
						}
					},
					loadPageInfo : function(b) {
						if (this.displayItem) {
							this.displayItem
									.setText('<span class="x-statusbar" style="padding:0;margin:0;"><span class="'
											+ this.busyIconCls
											+ '" style="width:16px;height:16px;padding:0;margin:0;">&nbsp;</span>'
											+ this.busyText + "</span>")
						}
						var a = this;
						GridViewAction.getGridPageInfo(b, this.grid.gridCfg,
								function(c) {
									a.onLoad(c, b)
								})
					},
					onLoad : function(a, b) {
						if (a != null) {
							this.totalCount = a.totalCount
						} else {
							this.totalCount = 0
						}
						Frame.grid.BasePagingToolbar.superclass.onLoad.call(
								this, this.store, null, {
									params : b
								})
					},
					getPageData : function() {
						var a = this.totalCount;
						return {
							total : a,
							activePage : Math
									.ceil((this.cursor + this.pageSize)
											/ this.pageSize),
							pages : a < this.pageSize ? 1 : Math.ceil(a
									/ this.pageSize)
						}
					},
					updateInfo : function() {
						if (this.displayItem) {
							var a = this.pageSize;
							var b = a == 0 ? this.emptyMsg : String.format(
									this.displayMsg, this.cursor + 1,
									this.cursor + a, this.totalCount);
							this.displayItem.setText(b)
						}
					},
					changePage : function(a) {
						this.doLoad(((a - 1) * this.pageSize).constrain(0,
								this.totalCount))
					},
					beforeLoad : function() {
						if (this.rendered) {
							if (this.refresh) {
								this.refresh.disable()
							}
							this.first.setDisabled(true);
							this.prev.setDisabled(true);
							this.next.setDisabled(true);
							this.last.setDisabled(true)
						}
					},
					moveLast : function() {
						var b = this.totalCount, a = b % this.pageSize;
						this.doLoad(a ? (b - a) : b - this.pageSize)
					}
				});
Frame.grid.BaseGridPanel = Ext.extend(Ext.grid.GridPanel, {
	border : false,
	loadMask : true,
	remoteSort : true,
	groupTextTpl : "{text}",
	pageSize : 20,
	totalNum : 0,
	hasPageBar : true,
	ddGroup : "GridDD",
	ddCopy : false,
	constructor : function(a) {
		Frame.grid.BaseGridPanel.superclass.constructor.call(this, a)
	},
	initComponent : function() {
		if (!Ext.isEmpty(this.gridCfg.groupField)) {
			this.groupField = this.gridCfg.groupField
		}
		this.initPlugins();
		if (!this.sm) {
			this.sm = new Ext.grid.CheckboxSelectionModel( {})
		}
		this.store = this.createStore();
		this.columns = [];
		this.colModel = new Ext.grid.ColumnModel( {
			columns : this.columns,
			defaults : {
				width : 150
			}
		});
		if (this.hasPageBar == false) {
			if (Ext.isEmpty(this.totalNum) || this.totalNum == 0) {
				this.pageSize = 100, this.totalNum = this.pageSize
			} else {
				this.pageSize = this.totalNum
			}
		} else {
			if (this.totalNum > 0) {
				this.pagingDefer = true
			} else {
				this.pagingDefer = false
			}
			this.bbar = this.createPageBar()
		}
		if (!Ext.isEmpty(this.groupField)) {
			if (!Ext.isEmpty(this.startCollapsed)) {
				this.startCollapsed = true;
			} else {
				this.startCollapsed = false;
			}
			var a = {
				groupTextTpl : this.groupTextTpl,
				startCollapsed : this.startCollapsed
			};
			if (this.viewConfig && Ext.isObject(this.viewConfig)) {
				Ext.apply(a, this.viewConfig);
				delete this.viewConfig
			}
			this.view = new Frame.grid.BaseGroupingView(a)
		} else {
			var a = {
				enableRowBody : Ext.isEmpty(this.expander) ? false : true
			};
			if (this.viewConfig && Ext.isObject(this.viewConfig)) {
				Ext.apply(a, this.viewConfig);
				delete this.viewConfig
			}
			this.view = new Frame.grid.BaseGridView(a)
		}
		Frame.grid.BaseGridPanel.superclass.initComponent.call(this);
		this.refreshGridMeta();
		this
				.addEvents("beforequery", "query", "configloaded",
						"copy2clipboard");
		this.on( {
			afterrender : {
				scope : this,
				fn : function(b) {
					if (b.loadData == true) {
						b.doQuery()
					}
					if (b.enableDragDrop == true) {
						b.createDD()
					}
				}
			}
		})
	},
	initPlugins : function() {
		if (!this.rendererPluginKeys) {
			this.rendererPluginKeys = []
		}
		if (!this.bbarPluginKeys) {
			this.bbarPluginKeys = []
		}
		if (!this.tbarPluginKeys) {
			this.tbarPluginKeys = []
		}
		if (!this.eventPluginKeys) {
			this.eventPluginKeys = []
		}
		if (!this.buttonsPluginKeys) {
			this.buttonsPluginKeys = []
		}
		if (Frame.grid.plugins.renderer) {
			var b = Frame.grid.plugins.renderer;
			if (this.rendererPlugin) {
				this.rendererPluginKeys.push(this.rendererPlugin)
			}
			Ext.each(this.rendererPluginKeys, function(d, c) {
				var e = b[d];
				if (Ext.isObject(e)) {
					Ext.applyIf(this, e)
				}
			}, this)
		}
		if (Frame.grid.plugins.bbar && this.hasBbar != false) {
			var a = [];
			if (this.hasPageBar != true) {
				this.refresh = new Ext.Button( {
					text : "刷新",
					overflowText : "刷新",
					iconCls : "x-tbar-loading",
					handler : function() {
						this.doQuery()
					},
					scope : this
				});
				a.push(this.refresh)
			}
			var b = Frame.grid.plugins.bbar;
			if (this.bbarPlugin) {
				this.bbarPluginKeys.push(this.bbarPlugin)
			}
			Ext.each(this.bbarPluginKeys, function(d, c) {
				var f = b[d];
				if (Ext.isFunction(f)) {
					var e = new f(this);
					if (Ext.isArray(e)) {
						Ext.each(e, function(g) {
							if (g.divide == true) {
								a.push("-")
							}
							a.push(g)
						})
					} else {
						if (e.divide == true) {
							a.push("-")
						}
						a.push(e)
					}
				}
			}, this);
			if (a.length > 0) {
				if (this.bbar && this.bbar.length > 0) {
					this.bbar = a.concat(this.bbar)
				} else {
					this.bbar = a
				}
			}
		}
		if (Frame.grid.plugins.tbar) {
			var a = [];
			var b = Frame.grid.plugins.tbar;
			if (this.tbarPlugin) {
				this.tbarPluginKeys.push(this.tbarPlugin)
			}
			Ext.each(this.tbarPluginKeys, function(d, c) {
				var f = b[d];
				if (Ext.isFunction(f)) {
					var e = new f(this);
					if (Ext.isArray(e)) {
						Ext.each(e, function(g) {
							if (g.divide == true) {
								a.push("-")
							}
							a.push(g)
						})
					} else {
						if (e.divide == true) {
							a.push("-")
						}
						a.push(e)
					}
				}
			}, this);
			if (a.length > 0) {
				if (this.tbar && this.tbar.length > 0) {
					this.tbar = a.concat(this.tbar)
				} else {
					this.tbar = a
				}
			}
		}
		if (Frame.grid.plugins.buttons) {
			var a = [];
			var b = Frame.grid.plugins.buttons;
			if (this.buttonsPlugin) {
				this.buttonsPluginKeys.push(this.buttonsPlugin)
			}
			Ext.each(this.buttonsPluginKeys, function(d, c) {
				var f = b[d];
				if (Ext.isFunction(f)) {
					var e = new f(this);
					if (Ext.isArray(e)) {
						Ext.each(e, function(g) {
							if (g.divide == true) {
								a.push("-")
							}
							a.push(g)
						})
					} else {
						if (e.divide == true) {
							a.push("-")
						}
						a.push(e)
					}
				}
			}, this);
			if (a.length > 0) {
				if (this.buttons && this.buttons.length > 0) {
					this.buttons = a.concat(this.buttons)
				} else {
					this.buttons = a
				}
			}
		}
		if (Frame.grid.plugins.event) {
			var b = Frame.grid.plugins.event;
			if (this.eventPlugin) {
				this.eventPluginKeys.push(this.eventPlugin)
			}
			Ext.each(this.eventPluginKeys, function(d, c) {
				var f = b[d];
				if (Ext.isFunction(f)) {
					var e = new f(this);
					if (Ext.isObject(e)) {
						if (!this.listeners) {
							this.listeners = {}
						}
						Ext.apply(this.listeners, e)
					}
				}
			}, this)
		}
	},
	createStore : function() {
		var c = this;
		var a;
		var b = {
			autoLoad : false,
			remoteSort : this.remoteSort == false ? false : true,
			proxy : new Ext.ux.data.DwrProxy( {
				apiActionToHandlerMap : {
					read : {
						dwrFunction : GridViewAction.getGridData,
						getDwrArgsFunction : function(d) {
							return [ c.gridCfg ]
						}
					}
				}
			}),
			reader : new Ext.data.JsonReader( {
				root : "list",
				totalProperty : "totalCount",
				id : this.pk,
				fields : []
			}),
			baseParams : {
				start : 0,
				limit : this.pageSize,
				totalNum : this.totalNum
			},
			listeners : {
				scope : this,
				beforeload : function(e, d, f) {
				},
				load : function(d, e) {
					this.fireEvent("query", d, e)
				}
			}
		};
		if (Ext.isEmpty(this.groupField)) {
			a = new Ext.data.Store(b)
		} else {
			a = new Ext.data.GroupingStore(Ext.apply( {
				groupField : this.groupField
			}, b))
		}
		return a
	},
	createPageBar : function() {
		var b, a;
		var c = [ this.pageSizeMenu = this.createPageSizeMenu() ];
		a = c;
		if (this.bbar && this.bbar.length > 0) {
			a = c.concat(this.bbar)
		}
		if (this.pagingDefer == true) {
			b = new Frame.grid.BasePagingToolbar( {
				pageSize : this.pageSize,
				grid : this,
				items : a
			})
		} else {
			b = new Ext.PagingToolbar( {
				pageSize : this.pageSize,
				store : this.store,
				displayInfo : true,
				items : a,
				changePageSize : function(d) {
					if (Ext.isNumber(d)) {
						this.pageSize = d;
						this.doLoad(0)
					}
				}
			})
		}
		return b
	},
	createPageSizeMenu : function() {
		if (!this.pageSizeArray) {
			this.pageSizeArray = [ 20, 30, 50, 100, 200, 500, 1000, 5000 ];
			if (Ext.isNumber(this.pageSize)) {
				if (this.pageSizeArray.indexOf(this.pageSize) == -1) {
					this.pageSizeArray = [ this.pageSize ]
							.concat(this.pageSizeArray)
				}
			}
		}
		var c = this;
		var d = [];
		var b = "每页{0}条";
		var a = null;
		function e() {
			var f = this.text * 1;
			a.setText(String.format(b, f));
			c.changePageSize(f)
		}
		Ext.each(this.pageSizeArray, function(f) {
			if (Ext.isNumber(f)) {
				d.push( {
					text : f,
					iconCls : "c_text_list_numbers",
					handler : e
				})
			}
		});
		a = new Ext.Button( {
			text : String.format(b, this.pageSize),
			menu : d
		});
		return a
	},
	changePageSize : function(a) {
		var b = this.getBottomToolbar();
		if (b && Ext.isFunction(b.changePageSize)) {
			b.changePageSize.call(b, a)
		}
	},
	refreshGridMeta : function() {
		if (this.rendered == true) {
			this.getEl().mask("表格创建中，请稍候...")
		}
		var a = this;
		GridViewAction.getGridMeta(this.gridCfg, function(b) {
			if (Ext.isEmpty(b)) {
				Ext.Msg.alert("系统异常", "无法获取表格定义，请检查表格配置！")
			} else {
				a.gridMeta = b;
				a.reconfigure.call(a)
			}
			if (a.rendered == true) {
				a.getEl().unmask()
			}
		})
	},
	reconfigure : function() {
		this.reconfigured = false;
		this.getStore().reader.onMetaChange( {
			root : "list",
			totalProperty : "totalCount",
			id : this.pk,
			fields : this.gridMeta.fields
		});
		this.colModel.setConfig(this.createColumns());
		Frame.grid.BaseGridPanel.superclass.reconfigure.call(this, this
				.getStore(), this.colModel);
		if (this.hasPageBar == true && this.pagingDefer != true) {
			this.getBottomToolbar().bind(this.getStore())
		}
		if (this.expander) {
			this.plugins = this.expander;
			this.initPlugin(this.expander);
			this.expander.onRender.call(this.expander)
		}
		this.reconfigured = true;
		this.fireEvent("configloaded", this)
	},
	createColumns : function() {
		var b = [];
		if (!Ext.isEmpty(this.gridMeta.plugins)
				&& this.gridMeta.plugins.length > 0) {
			Ext.each(this.gridMeta.plugins, function(d, c) {
				if (d == "number") {
					if (!this.numberCm) {
						this.numberCm = new Ext.grid.RowNumberer()
					}
					b = [ this.numberCm ].concat(b)
				} else {
					if (d == "expander") {
						b.push(this.expander)
					} else {
						if (d == "checkbox") {
							b.push(this.selModel)
						}
					}
				}
			}, this)
		}
		var a = [];
		Ext.each(this.gridMeta.columns, function(c, d) {
			var e = Ext.applyIf( {}, c);
			if (e.cm) {
				if (e.width > 0) {
					e.cm.width = e.width
				}
				Ext.apply(e, e.cm)
			}
			if (e.renderer) {
				if (e.renderer.indexOf("fn:") == 0) {
					e.renderer = e.renderer.substring(3)
				}
				if (Ext.isFunction(this[e.renderer])) {
					e.renderer = this[e.renderer]
				} else {
					if (e.renderer == "date") {
						e.renderer = Ext.util.Format
								.dateRenderer("Y-m-d H:i:s")
					} else {
						delete e.renderer
					}
				}
			}
			a.push(e)
		}, this);
		b = b.concat(a.sort(this.sortColumn));
		return b
	},
	sortColumn : function(d, c) {
		var b = d.colIndex, a = c.colIndex;
		if (!Ext.isDefined(b)) {
			b = 99
		}
		if (!Ext.isDefined(!a)) {
			a = 99
		}
		return b - a
	},
	doQuery : function() {
		if (this.reconfigured != true || this.rendered != true) {
			this.doQuery.defer(200, this);
			return
		}
		this.getStore().setBaseParam("start", 0);
		var a = this.getBottomToolbar();
		if (a) {
			a.cursor = 0
		}
		if (this.fireEvent("beforequery", this.gridCfg) != false) {
			this.getStore().load()
		}
	},
	createDD : function() {
		if (this.rendered == true) {
			var a = this;
			this.ddrow = new Ext.dd.DropTarget(this.container, {
				ddGroup : this.ddGroup,
				copy : this.ddCopy,
				notifyDrop : function(b, d, c) {
					a.notifyDrop.call(a, b, d, c)
				}
			})
		}
	},
	notifyDrop : function(a, h, g) {
		var f = g.selections;
		var b = a.getDragData(h).rowIndex;
		if (typeof (b) == "undefined") {
			return
		}
		for ( var c = 0; c < f.length; c++) {
			var d = f[c];
			if (!this.ddrow.copy) {
				this.getStore().remove(d)
			}
			this.getStore().insert(b, d)
		}
		this.refreshRowNumber()
	},
	refreshRowNumber : function() {
		var a = Ext.select("div.x-grid3-col-numberer", null, this.getEl().dom);
		Ext.each(a.elements, function(c, b) {
			c.innerHTML = b + 1
		})
	}
});
Frame.grid.BaseGridPanel.parseQueryParams = function(a, d) {
	var c = null, b = null;
	d = d.replace(/(^,*)|(,*$)/g, "");
	if (Ext.isEmpty(d) || d.toLowerCase() == "null") {
		return null
	}
	if (d.indexOf("*") != -1) {
		c = "like";
		d = d.replace(/\*/g, "%")
	} else {
		if (d.toLowerCase() == "isnull") {
			c = "is";
			d = "null";
			b = "null"
		} else {
			if (d.toLowerCase() == "isnotnull") {
				c = "is not";
				d = "null";
				b = "null"
			} else {
				if (d.indexOf(">=") != -1) {
					c = ">=";
					d = d.substring(2)
				} else {
					if (d.indexOf("<=") != -1) {
						c = "<=";
						d = d.substring(2)
					} else {
						if (d.indexOf(">") != -1) {
							c = ">";
							d = d.substring(1)
						} else {
							if (d.indexOf("<") != -1) {
								c = "<";
								d = d.substring(1)
							} else {
								if (d.indexOf(",") != -1) {
									if (d.indexOf("!") == 0) {
										c = "not in"
									} else {
										c = "in"
									}
								} else {
									c = "=";
									d = Ext.isEmpty(d) ? undefined : d;
									b = "string"
								}
							}
						}
					}
				}
			}
		}
	}
	return {
		key : a,
		relation : c,
		type : b,
		value : d
	}
};
Frame.grid.BaseGridPanel.initParamsByUrl = function(e) {
	var b = {};
	var a = {};
	for ( var d in e) {
		if (d == "boName" || d == "exportBoName" || d == "viewBoName"
				|| d == "groupField") {
			a[d] = e[d]
		} else {
			if (d == "bmClassId" || d == "templateId") {
				if (!a.cfgParams) {
					a.cfgParams = {}
				}
				a.cfgParams[d] = e[d]
			} else {
				if (d.indexOf("c_") == 0) {
					if (!a.cfgParams) {
						a.cfgParams = {}
					}
					a.cfgParams[d.substring(2)] = e[d]
				} else {
					if (d.indexOf("s_") == 0) {
						d = d.substring(2);
						if (!a.urlParams) {
							a.urlParams = {}
						}
						a.urlParams[d] = Frame.grid.BaseGridPanel
								.parseQueryParams(d, e["s_" + d])
					} else {
						if (d.indexOf("k_") == 0) {
							if (!a.keyParams) {
								a.keyParams = {}
							}
							a.keyParams[d] = {
								key : d.substring(2),
								value : e[d]
							}
						} else {
							var c = e[d];
							if (c == "true") {
								c = true
							} else {
								if (c == "false") {
									c = false
								}
							}
							b[d] = c
						}
					}
				}
			}
		}
	}
	b.gridCfg = a;
	return b
};
Frame.grid.DataGridPanel = Ext.extend(Frame.grid.BaseGridPanel, {
	constructor : function(a) {
		Frame.grid.DataGridPanel.superclass.constructor.call(this, a)
	},
	initComponent : function() {
		Frame.grid.DataGridPanel.superclass.initComponent.call(this)
	},
	initPlugins : function() {
		var b = [ "GridCustLayout", "GridDataExport" ];
		if (this.bbarPluginKeys) {
			this.bbarPluginKeys = b.concat(this.bbarPluginKeys)
		} else {
			this.bbarPluginKeys = b
		}
		var a = [ "GridEvent" ];
		if (this.eventPluginKeys) {
			this.eventPluginKeys = a.concat(this.eventPluginKeys)
		} else {
			this.eventPluginKeys = a
		}
		Frame.grid.DataGridPanel.superclass.initPlugins.call(this)
	},
	doQuery : function(a, b) {
		if (this.reconfigured != true) {
			this.doQuery.defer(200, this, [ a, b ]);
			return
		}
		this.mergeQueryParams(a, b);
		Frame.grid.DataGridPanel.superclass.doQuery.call(this)
	},
	mergeQueryParams : function(b, f) {
		var d = {}, a = {};
		if (Ext.isObject(b)) {
			for ( var c in b) {
				var g = b[c];
				if (!g) {
					continue
				}
				var e = g.value;
				if (Ext.isEmpty(e) || (e + "").toUpperCase() == "NULL"
						|| (e + "").replace(/\*/g, "") == "") {
					continue
				}
				if (g != null) {
					d[c] = g
				}
			}
		}
		for ( var c in this.gridCfg.urlParams) {
			var g = this.gridCfg.urlParams[c];
			if (!g) {
				continue
			}
			var e = g.value;
			if (Ext.isEmpty(e) || (e + "").toUpperCase() == "NULL"
					|| (e + "").replace(/\*/g, "") == "") {
				continue
			}
			if (!d[c]) {
				if (g != null) {
					d[c] = g
				}
			}
		}
		this.gridCfg.queryParams = d;
		if (Ext.isObject(f)) {
			Ext.apply(a, f)
		}
		Ext.applyIf(a, this.gridCfg.keyParams);
		this.gridCfg.extParams = a
	}
});
Frame.grid.QueryGridPanel = Ext
		.extend(
				Ext.Panel,
				{
					border : false,
					enableQueryPanel : true,
					constructor : function(a) {
						if (a.gridConfig && Ext.isObject(a.gridConfig)) {
							Ext.apply(a.gridConfig, {
								loadData : false,
								border : false,
								frame : false
							});
							a.gridConfig = Ext.applyIf(a.gridConfig, a)
						} else {
							a.gridConfig = Ext.applyIf( {
								loadData : false,
								border : false,
								frame : false
							}, a)
						}
						delete a.gridConfig.id;
						delete a.tbar;
						delete a.bbar;
						Frame.grid.QueryGridPanel.superclass.constructor.call(
								this, a)
					},
					initComponent : function() {
						this.layout = "border";
						this.items = [];
						this.initPlugins();
						if (this.enableQueryPanel == true) {
							if (!this.queryPanel) {
								var b = [];
								if (this.queryPlugins.length > 1) {
									var a = new Ext.Slider(
											{
												height : 80,
												vertical : true,
												increment : 1,
												minValue : 0,
												maxValue : this.queryPlugins.length - 1,
												plugins : new Ext.slider.Tip(),
												listeners : {
													change : {
														scope : this,
														fn : this.onQueryPluginChange
													}
												}
											});
									this.queryPluginChangePanel = new Ext.Panel(
											{
												xtype : "panel",
												region : "east",
												border : false,
												padding : 6,
												width : 30,
												items : [ a ]
											});
									b.push(this.queryPluginChangePanel)
								}
								this.queryPluginCard = new Ext.Panel( {
									region : "center",
									layout : "card",
									activeItem : 0,
									layoutConfig : {
										deferredRender : true
									},
									items : this.queryPlugins
								});
								b.push(this.queryPluginCard);
								this.queryPanel = new Ext.Panel(
										{
											region : "north",
											layout : "border",
											border : false,
											height : 100,
											minHeight : 100,
											split : true,
											collapsed : this.queryCollapse == true ? true
													: false,
											collapseMode : "mini",
											animCollapse : false,
											bodyCfg : {
												cls : "x-panel-mc"
											},
											items : b,
											listeners : {
												heightresize : {
													scope : this,
													fn : this.onHeightResize
												},
												afterrender : {
													scope : this,
													fn : this.onHeightResize
												},
												expand : {
													scope : this,
													fn : this.onHeightResize
												}
											}
										})
							}
							this.items.push(this.queryPanel)
						}
						this.contentPanel = this.createContentPanel( {
							region : "south",
							height : 150,
							split : true,
							collapsed : true,
							border : false,
							collapseMode : "mini",
							animCollapse : false
						});
						this.items.push(this.contentPanel);
						this.grid = new Frame.grid.DataGridPanel(Ext.applyIf( {
							region : "center"
						}, this.gridConfig));
						this.items.push(this.grid);
						this.grid.on("rowclick", function(c, f, d) {
							if (this.contentPanel) {
								this.contentPanel.setContent(c, f)
							}
						}, this);
						if (this.loadData == true) {
							this.on( {
								afterrender : function() {
									this.doQuery()
								}
							}, this)
						}
						Frame.grid.QueryGridPanel.superclass.initComponent
								.call(this)
					},
					initPlugins : function() {
						if (this.enableQueryPanel == true) {
							var b = [ "GridGeneralQueryForm" ];
							if (!this.queryPluginKeys) {
								this.queryPluginKeys = b
							} else {
								this.queryPluginKeys = b
										.concat(this.queryPluginKeys)
							}
							if (Frame.grid.plugins.query) {
								var c = [];
								var a = Frame.grid.plugins.query;
								if (this.queryPlugin) {
									this.queryPluginKeys = [ this.queryPlugin ]
											.concat(this.queryPluginKeys)
								} else {
									if (Ext.isArray(this.queryPlugins)) {
										this.queryPluginKeys = this.queryPlugins
												.concat(this.queryPluginKeys)
									}
								}
								Ext
										.each(
												this.queryPluginKeys,
												function(f, d) {
													var g = new a[f](this);
													g = this.buildQueryForm(g);
													if (this.gridConfig.gridCfg.urlParams) {
														var h = {};
														for ( var e in this.gridConfig.gridCfg.urlParams) {
															h[e] = this.gridConfig.gridCfg.urlParams[e].value
														}
														g
																.on(
																		"afterrender",
																		function() {
																			var l = g
																					.getForm();
																			for ( var j in h) {
																				var i = l
																						.findField(j);
																				if (i) {
																					i
																							.setValue(h[j])
																				}
																			}
																		})
													}
													c.push(g)
												}, this);
								this.queryPlugins = c
							}
						}
					},
					doLoad : function(a, b) {
						this.grid.doQuery(a, b)
					},
					doQuery : function() {
						if (this.queryPanel) {
							var a = this.getQueryForm();
							if (a && a.rendered == true) {
								var c = a.getForm();
								if (c.isValid()) {
									var b = this.getWhereQueryItems(c);
									var d = this.getExtQueryItems(c);
									this.doLoad(b, d)
								}
							} else {
								this.doQuery.defer(200, this)
							}
						} else {
							this.doLoad()
						}
					},
					refreshGridData : function() {
						this.grid.getStore().reload()
					},
					onQueryPluginChange : function(a, b) {
						this.queryPluginCard.getLayout().setActiveItem(b);
						this.queryPanel.fireEvent("heightresize");
						this.queryPluginChangePanel.items.get(0).setValue(b)
					},
					onHeightResize : function() {
						var a = this.getQueryForm();
						if (a) {
							var b = a.height;
							if (!Ext.isNumber(b) || b <= 100) {
								b = 100
							}
							this.queryPanel.setHeight(b);
							this.doLayout()
						} else {
							this.onHeightResize.defer(200, this)
						}
					},
					getWhereQueryItems : function(a) {
						var k = {};
						var l = a.getFieldValues();
						for ( var j in l) {
							var i = a.findField(j);
							if (!Ext.isEmpty(i.sql)) {
								continue
							}
							var m = l[j];
							if (Ext.isEmpty(m)) {
								continue
							} else {
								if (Ext.isDate(m)) {
									m = m.format("Y-m-d H:i:s")
								} else {
									if (Ext.isArray(m)) {
										var g = [];
										Ext.each(m, function(n) {
											if (Ext.isDate(n)) {
												n = n.format("Y-m-d H:i:s")
											}
											g.push(n)
										});
										m = g.join(",")
									} else {
										m = m + ""
									}
								}
							}
							var b = null, f = null, c = null, h = null;
							if (i.queryCfg) {
								if (!Ext.isEmpty(m)) {
									if (i.queryCfg.type) {
										f = i.queryCfg.type;
										if (f == "sql" || f == "append") {
											var d = i.queryCfg.sqlTemplate;
											if (!Ext.isEmpty(d)) {
												h = m;
												m = d.replace(/{V}/g, m)
											}
										}
									}
								}
								if (i.queryCfg.relation) {
									b = i.queryCfg.relation.toLowerCase()
								}
								if (i.queryCfg.alias) {
									c = i.queryCfg.alias
								}
								if (b == "like") {
									if (i.queryCfg.blurMatch == "left") {
										m = "%" + m
									} else {
										if (i.queryCfg.blurMatch == "right") {
											m = m + "%"
										} else {
											if (i.queryCfg.blurMatch == "both") {
												m = "%" + m + "%"
											} else {
												m = m + "%"
											}
										}
									}
								}
							}
							var e = {
								key : j,
								relation : Ext.isEmpty(b) ? "like" : b,
								value : m,
								baseValue : h,
								type : Ext.isEmpty(f) ? "string" : f,
								alias : c
							};
							k[j] = e
						}
						return k
					},
					getExtQueryItems : function(g) {
						var e = {};
						var c = g.getFieldValues();
						for ( var d in c) {
							var h = g.findField(d);
							if (!Ext.isEmpty(h.sql)) {
								var b = c[d];
								if (Ext.isDate(b)) {
									b = b.format("Y-m-d H:i:s")
								} else {
									if (Ext.isArray(b)) {
										var a = [];
										Ext.each(b, function(i) {
											if (Ext.isDate(i)) {
												i = i.format("Y-m-d H:i:s")
											}
											a.push(i)
										});
										b = a.join(",")
									} else {
										b = b + ""
									}
								}
								var f = {
									key : d,
									sql : h.sql,
									value : b
								};
								e[d] = f
							}
						}
						return e
					},
					getQueryForm : function() {
						return this.queryPluginCard.getLayout().activeItem
					},
					buildQueryForm : function(c) {
						var b = [ {
							text : "查询",
							iconCls : "c_find",
							scope : this,
							handler : this.doQuery
						}, {
							text : "重置",
							iconCls : "c_arrow_rotate_anticlockwise",
							scope : this,
							handler : function() {
								var e = this.getQueryForm();
								e.getForm().reset()
							}
						} ];
						var a;
						var d = c.getFooterToolbar();
						if (c instanceof Ext.form.FormPanel) {
							a = c
						} else {
							if (d && d.items.getCount() > 0) {
								d.items.each(function(e) {
									b.push(e)
								});
								d.removeAll(true)
							}
							a = new Ext.form.FormPanel( {
								padding : 6,
								layout : "fit",
								border : false,
								height : c.height,
								items : [ c ],
								buttons : b
							})
						}
						return a
					},
					createContentPanel : function(b) {
						var a = new Ext.Panel(
								Ext
										.applyIf(
												{
													autoScroll : true,
													asynDetail : this.asynDetail,
													setContent : function(c, m) {
														var o = this;
														if (this.asynDetail == true) {
															var j = c
																	.getStore()
																	.getAt(m);
															MaskHelper
																	.mask(this
																			.getEl());
															GridViewAction
																	.getGridDetail(
																			c.gridCfg,
																			j.json.CUID,
																			j.json.BM_CLASS_ID,
																			function(
																					i) {
																				var d = [ "<table class='formTable'>" ];
																				Ext
																						.each(
																								i,
																								function(
																										p) {
																									d
																											.push("<tr><td class='formlable' width='20%'>"
																													+ p.label
																													+ "</td><td class='formcontent'>"
																													+ trimToEmpty(p.value)
																													+ "</td></tr>")
																								});
																				d
																						.push("</table>");
																				o
																						.update(d
																								.join(""));
																				MaskHelper
																						.unmask(o
																								.getEl())
																			})
														} else {
															var j = c
																	.getStore()
																	.getAt(m);
															var f = c
																	.getColumnModel().config;
															var h = [ "<table class='formTable'>" ];
															var k = null;
															for ( var g = 0; g < f.length; g++) {
																if (g == 0) {
																	k = "x-grid3-cell-first"
																} else {
																	k = (g == f.length - 1) ? "x-grid3-cell-last "
																			: ""
																}
																var e = f[g];
																var l = false;
																for ( var n in j.data) {
																	if (n == e.dataIndex) {
																		l = j.data[n];
																		if (e.renderer) {
																			l = e.renderer
																					.call(
																							e,
																							l,
																							{
																								id : e.id,
																								style : e.style,
																								css : k,
																								attr : "",
																								cellAttr : ""
																							},
																							j)
																		}
																		break
																	}
																}
																if (l != false) {
																	h
																			.push("<tr><td class='formlable' width='20%'>"
																					+ e.header
																					+ "</td><td class='formcontent'>"
																					+ trimToEmpty(l)
																					+ "</td></tr>")
																}
															}
															h.push("</table>");
															o
																	.update(h
																			.join(""))
														}
													}
												}, b));
						return a
					}
				});