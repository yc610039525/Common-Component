Ext.ns("Frame.grid.plugins.renderer");
Frame.grid.plugins.renderer.ResGridRenderer = {
	rendererRel : function(value) {
		if(!Ext.isEmpty(value)) {
			//value格式：$bmClassId.$key=$cuid[$label]
			var s1 = value.split('=');
			if(s1.length == 2) {
				var $bmClassId = s1[0].split('.')[0];
				var $cuid = s1[1].split('[')[0];
				if(!Ext.isEmpty($bmClassId) && !Ext.isEmpty($cuid)) {
					var $label = s1[1].substring(s1[1].indexOf('[')+1,s1[1].indexOf(']'));
					var clickFn = "FrameHelper.showDetail('"+$bmClassId+"','"+$cuid+"');stopBubble(window.event||event)";
					var style = 'cursor:pointer;color:blue;text-decoration:underline';
					value = '<span style="'+style+'" onclick="'+clickFn+'">'+$label+'</span>';
				}else {
					value = '';
				}
			}else {
				value = '配置有误';
			}
		}
		return value;
	},
	rendererCount : function(value, metadata, record, rowIndex, colIndex, store) {
		if(!Ext.isEmpty(value)) {
			//value格式：$code.$key=$cuid[$label]$title
			var s1 = [value.substring(0,value.indexOf("=")), value.substring(value.indexOf("=")+1)];
			if(s1.length == 2) {
				var dotIndex = s1[0].lastIndexOf('.');
				if(dotIndex<=-1){
					return '配置有误';
				}
				var $code = s1[0].substring(0,dotIndex);
				var $key = s1[0].substring(dotIndex+1);
				var $cuid = s1[1].substring(0, s1[1].indexOf('['));
				var $title = s1[1].substring(s1[1].indexOf(']')+1);
				if(!Ext.isEmpty($code) && !Ext.isEmpty($cuid)) {
					var $label = s1[1].substring(s1[1].indexOf('[')+1,s1[1].indexOf(']'));
					if($label != '0') {
						var url = "/cmp_res/grid/ResGridPanel.jsp?code="+$code+"&header=false&s_"+$key+"="+$cuid;
						var tabName = "统计";
						if($title) {
							tabName = $title;
						}else {
							var colName = this.header;
							if(!Ext.isEmpty(this.header)) {
								colName += '['+$label+']';
							}else {
								colName = this.header;
							}
							if(!Ext.isEmpty(colName)) {
								tabName = colName;
							}
						}
						var clickFn = "FrameHelper.openUrl('"+url+"','"+tabName+"');stopBubble(window.event);";
						var style = 'cursor:pointer;color:blue;text-decoration:underline';
						value = '<span style="'+style+'" onclick="'+clickFn+'">'+$label+'</span>';
					}else {
						value = '0';
					}
				}else {
					value = '';
				}
			}else {
				value = '配置有误';
			}
		}
		return value;
	},
	
	rendererAttrType: function(value, metadata, record, rowIndex, colIndex, store) {
		if(!Ext.isEmpty(value)) {
			if(value=='string'){
				value = '字符';
			}else if(value=='int'){
				value = '数字';
			}else if(value=='float'){
				value = '小数';
			}else if(value=='enum'){
				value = '枚举';
			}else if(value=='relation'){
				value = '关联';
			}else if(value=='date'){
				value = '日期';
			}else if(value=='state'){
				value = '状态';
			}
		}
		return value;
	}
};