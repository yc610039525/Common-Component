(function() {
	function createXhrObject() {
		var http = null;
		var activeX = ['MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP',
				'Microsoft.XMLHTTP'];
		try {
			http = new XMLHttpRequest();
		}catch (e) {
			for (var i = 0; i < activeX.length; ++i) {
				try {
					http = new ActiveXObject(activeX[i]);
					break;
				} catch (e) {
				}
			}
		}
		return http;
	};
	
	SynDataHelper = {
		load : function(url) {
			var conn = createXhrObject();
			conn.open("GET", url, false);
			conn.send(null);
			if (conn.responseText != '') {
				var obj = null;
				try {
					obj = eval("(" + conn.responseText + ")");
				}catch(e) {
					alert(e);
				}
				return obj;
			}else {
				return null;
			}
		}
	};
})();