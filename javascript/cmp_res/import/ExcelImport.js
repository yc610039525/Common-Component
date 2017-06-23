Ext.namespace('Frame.elt');
$importcss(ctx + "/jslib/ext/ux/fileuploadfield/css/fileuploadfield.css");
$importjs(ctx + "/jslib/ext/ux/fileuploadfield/FileUploadField.js");

Frame.elt.ExcelImport = Ext.extend(Ext.Panel,{
	layout : 'border',
	initComponent : function(){
		
		var html='<p style="font-size:12px">Excel文件中定义的列名需要与模型中的字段名称一致</p>';
		html+="<p style='font-size:12px'>可以通过<a href='"+ctx+"/excelmodel/download.do?templateId="+encodeURI(encodeURI(this.templateId))+"' target='_self'>下载</a>左侧模板进行快速导出Excel模板</p>";
		html+='<p style="font-size:12px">如果要修改数据，请先导出数据，系统会自动在导出的文件中记录系统数据的唯一ID，修改后再导入</p>';
		
		this.pgbProgress = new Ext.ProgressBar({
					anchor : "95%"
				});
		
		this.fileUploadField = new Ext.ux.form.FileUploadField({
			emptyText : '选择一个数据文件',
			fieldLabel : '上传数据文件',
			name : 'file',
			buttonText : '',
			buttonCfg : {
				iconCls : 'c_image_add'
			}
		});
		this.uploadForm  = new Ext.FormPanel({
			fileUpload : true,
			frame : true,
			region: 'center',
			width : 350,
	        defaults: {
	            anchor: '95%'
	        },
			items: [{
				xtype : "fieldset",
				title : "导入/导出帮助",
				defaults: {
		            anchor: '100%'
		        },
				items :[{
					html:html
				}]
	        },{
				xtype : "fieldset",
				title : "文件信息",
				defaults: {
		            anchor: '100%'
		        },
				items :[this.fileUploadField]
	        },{
				xtype : "fieldset",
				title : "导入信息",
				defaults: {
		            anchor: '100%'
		        },
				items : [{
					fieldLabel : "进度",
					items : [this.pgbProgress]
				}, {
					fieldLabel : "执行时间",
					items : [{
								xtype : "label",
								id : "importtimefield",
								text : "0秒"
							}]
				}, {
					fieldLabel : "操作结果",
					items : [{
								xtype : "label",
								id : "importresultfield"
							}]
				}]
			}],
			buttonAlign: 'center',
			buttons : [
				{
					text     : '导入',
					scope    : this,
					disabled : false,
					iconCls  : 'c_application_get',
					handler  : this.onBtnImportClick
				}/*, {
					text     : '导出',
					scope    : this,
					disabled : false,
					iconCls  : 'doImport',
					handler  : this.onBtnExportClick
				}*/
			]
		});
		this.items = [this.uploadForm];
		Frame.elt.ExcelImport.superclass.initComponent.call(this);
	},
	onBtnImportClick : function() {
		var scope = this;
		var filePath = this.fileUploadField.getValue();
		if (Ext.isEmpty(filePath)) {
			Ext.Msg.show({
						title : "错误",
						msg : "请选择要导入的excel文件！",
						buttons : Ext.Msg.OK,
						minWidth : 300,
						icon : Ext.Msg.ERROR
					});
		} else if (filePath.length < 4
				|| ( filePath.lastIndexOf(".xls") != filePath.length - 4 && filePath.lastIndexOf(".xlsx") != filePath.length - 5)) {
			Ext.Msg.show({
						title : "错误",
						msg : "上传的文件类型错误，必须为xls/xlsx文件",
						buttons : Ext.Msg.OK,
						minWidth : 300,
						icon : Ext.Msg.ERROR
					});
		} else {
			Ext.Msg.show({
						title : "提示",
						msg : "确认导入数据：<br/>" + filePath,
						buttons : Ext.Msg.YESNO,
						minWidth : 300,
						icon : Ext.Msg.INFO,
						fn : function(btn) {
							if (btn == "yes") {
								scope.importData.call(scope);
							}
						}
					});
		}
	},
	importData : function() {
		MaskHelper.mask(this.getEl(),"数据导入中...");
		var scope = this;
		var url = ctx+'/import/index.do?templateId='+encodeURI(encodeURI(this.templateId))+'&taskId='+encodeURI(encodeURI(this.taskId))+'&sheetType='+encodeURI(encodeURI(this.sheetType));
		var startTime = new Date();
		this.uploadForm.getForm().submit({
            url     : url,
            success : function(form, action) {
                this.pgbProgress.updateProgress(100, "100%");
                MaskHelper.unmask(this.getEl());
                //this.fireEvent("_upload");
                if (action.result.success) {
                	var endTime = new Date();
                	var spendTime = (endTime-startTime)/1000+"秒";
                	var msg='';
                    var batchNo = action.result.batchNo;
                    var total = action.result.total;
                    var templateCuid = action.result.templateCuid;
                    var sucess = action.result.sucess;
                    var error = action.result.error;
                    var errorExportUrl = ctx + '/excelmodel/exporterrors.do?templateId='+templateCuid+'&batchNo='+batchNo+'&boName='+scope.boName;
                   	if(!Ext.isEmpty(batchNo)){
                   		msg+="总处理数据：<B>"+total +"</B> 条<BR> 成功入库：<B>"+sucess+"</B> 条"
                   		if(error > 0){
                   			msg+="<BR>错误数据:<a href='#' onclick='window.location.href=\""+errorExportUrl+"\"' ><B>"+error+"</B></a> 条。";
                   		}
                   	}
                    var msgInfo = action.result.msg;
                    if (!Ext.isEmpty(msgInfo)) {
                    	if(!Ext.isEmpty(msg)){
                    		msg+="<BR>"
                    	}
                    	 msg += msgInfo;
                    }
                    Ext.getCmp("importtimefield").getEl().update(spendTime);
                    Ext.getCmp("importresultfield").getEl().update(msg);
                }
            },
            failure : function(form, action) {
                MaskHelper.unmask(this.getEl());
                this.pgbProgress.updateProgress(100, "100%");
                Ext.getCmp("importresultfield").setText(action.result.msg);
                //this.fireEvent("_upload");
            },
            scope   : this
        });
	}
});