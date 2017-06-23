(function() {
	function getSysProp() {
		//sysProp 来自/commons/common.jsp
		return sysProp;
	};
	
	function formatUrl(url) {
		if(url.indexOf('http://') != 0) {
			if (url.indexOf('www.') == 0) {
				url = "http://" + url;
			} else if(url.indexOf(ctx) != 0){
				url = baseUrl + ctx + "/" + url;
			}
		}
		url = url.replace(/http:\/\//g,'-_-!');
		url = url.replace(/\/+/g, '/');
		url = url.replace(/-_-!/g,'http://');
		//添加用户信息
		/*
		var sysP = getSysProp();
		if(sysP) {
			if(url.indexOf('?')!=-1){
				url+='&';
			}else{
				url+='?';
			}
			url+='userId='+sysP.userId+'&userName='+sysP.userName;
		}
		*/
		return url;
	};
	
	UrlHelper = {
		getUrlObj : function (param){
	    	param = decodeURI(param);
	    	var obj={}
			var params = param.split("&");
			var tempObj = {};
			for(var i=0;i<params.length;i++){
				var values = [];
				if(params[i].indexOf(">=") != -1) {
					values = params[i].split(">=");
					values[1] = ">="+values[1];
				}else if(params[i].indexOf("<=") != -1) {
					values = params[i].split("<=");
					values[1] = "<="+values[1];
				}else if(params[i].indexOf("<") != -1) {
					values = params[i].split("<");
					values[1] = "<"+values[1];
				}else if(params[i].indexOf(">") != -1) {
					values = params[i].split(">");
					values[1] = ">"+values[1];
				}else {
					values = params[i].split("=");
				}
				var attrId = values[0];
				var newAttrId=attrId;
				var value = values[1];
				if(tempObj[attrId]){
					newAttrId+='$'+i;
				}
				tempObj[attrId] = value;
				obj[newAttrId] = value;
			}
			return obj;
	    },
	    /**
	     * 替换url参数
	     * 参数格式（key为变量）：
	     * (1)$("key")
	     * (2)$(\"key\")
	     * (3)$(key)
	     * (4){key}
	     */
	    replaceUrlArguments : function(url, replaceObj) {
	    	if(url) {
				//替换服务器参数
	    		var serverObj = sysProp.serverPath;
	    		if(!replaceObj) {
	    			replaceObj = {};
	    		}
	    		for(var key in serverObj) {
	    			replaceObj[key] = serverObj[key];
	    		}
	    		replaceObj['userName'] = ac.userName;
	    		replaceObj['userId'] = ac.userId;
	    		replaceObj['password'] = ac.password;
	    		url = url.replace(/\%7B/g, '{');
	    		url = url.replace(/\%7D/g, '}');
				for(var key in replaceObj) {
					var pnmReg = /\$\(\\?"?\w+\\?"?\)|{\w+}/g;
					var pnvReg = /[\$ \( \) { } \\ "]/g;
					var pnms = url.match(pnmReg);
					for(var i = 0 ; pnms && i < pnms.length; i++) {
						var pnm = pnms[i];
						var pnv = pnm.replace(pnvReg, '');
						var pv = replaceObj[pnv];
						if(Ext.isDefined(pv)) {
							url = url.replace(pnm, pv);
						}else {
							url = url.replace(pnm, '');
						}
					}
				}
	    	}
			//格式化url,去除多余的"/",添加用户信息
	    	url = formatUrl(url);
			return url;
		}
	}
})();