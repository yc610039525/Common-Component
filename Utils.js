/**
 * 批量修改工具类
 * @author liuyazhou
 * @date 2015-12-30
 */		
(function(){
	
	Frame.grid.batchmodify.Utils = {
			
		ownProp : Object.hasOwnProperty,
		
		each : 	function(o,callback,scope){
			
			if(!o)  return;
			
			if(typeof callback !== 'function') return;
			
			for(var k in o){
				
				if(Frame.grid.batchmodify.Utils.ownProp.call(o,k)){
					
					callback.call(scope,k,o[k]);
				}
			}
		},
		
		decode : function(source){
			
			var result = {};
			
			try{
				result = Ext.decode(source);
			}catch(e){}
			
			return result;
		},
		getValidLabelCn : function(labelCn, labelLength){
			/*var trueLabelCn=labelCn;
			if(!Ext.isEmpty(labelCn)){
				if(getStrLength(labelCn)>labelLength){
					labelCn = labelCn.abbreviate(labelLength);
					return "<span ext:qtip='"+trueLabelCn+"'>"+labelCn+"</span>";
				}
			}*/
			return labelCn;
		}
	};
})();

function getStrLength(str){
	
	var realLength = 0, len = str.length, charCode = -1;
    for (var i = 0; i < len; i++) {
        charCode = str.charCodeAt(i);
        if (charCode >= 0 && charCode <= 128) realLength += 1;
        else realLength += 2;
    }
    return realLength;
}


function JHashTable(_hashObj){
    this.hashObj = _hashObj != null ? _hashObj : {};
}

JHashTable.prototype.getKeys = function(){
//    return Object.keys(this.hashObj);
	var keys = [];
	for(var k in this.hashObj){
		keys.push(k);
	}
	return keys;
};

JHashTable.prototype.getValues = function(){
	
	var values = new Array();
	for(var k in this.hashObj){
		values.push(this.hashObj[k]);
	}
    return values;
};

JHashTable.prototype.put = function(_key, _value){
    this.hashObj[_key] = _value;
};

JHashTable.prototype.get = function(_key){
    return  this.hashObj[_key];
};

JHashTable.prototype.containsKey = function(_key){
    for (var key in this.hashObj){
        if(key == _key) return true;
    }
	return false;
};

JHashTable.prototype.remove = function(_key){
	delete this.hashObj[_key];
};

JHashTable.prototype.clear = function(){
	this.hashObj = null;
 	this.hashObj = {};
};

