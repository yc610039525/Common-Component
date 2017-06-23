
(function(){
	function logger(msg) {
		if(console && console.log) {
			console.log(msg);
		}
	}
	function findNodeChildren(node) {
		var edges = node.getEdges();
		var nodes=[];
		edges.each(function(edge){
			var source = edge.getSource();
			var target = edge.getTarget();
			if(source != node && nodes.indexOf(source) == -1){
				nodes.push(source);
			}
			if(target!=node && nodes.indexOf(target) == -1){
				nodes.push(target);
			}
		})
		return nodes;
	}
	function indexOfWay(ways, compareWay) {
		var idx = -1;
		for(var i = 0; i < ways.length; i++) {
			var way = ways[i];
			if(way.length == compareWay.length) {
				var isSame = true;
				for(var j = 0; j < way.length; j++) {
					if(way[j] != compareWay[j]) {
						isSame = false;
						break;
					}
				}
				if(isSame) {
					idx = i;
				}
			}
		}
		return idx;
	}
	function hasSeg(way, start, end) {
		var flag = false;
		for(var i = 0; i < way.length - 1 && way.length > 1; i++) {
			var source = way[i];
			var target = way[i+1];
			if((source == start && target == end) || (source == end && target == start)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	function containNodes(way, nodes) {
		var flag = true;
		for(var i = 0; i < nodes.length; i++) {
			if(way.indexOf(nodes[i]) == -1) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	function deepSearch(max, tmpWay, end, ways, mustList, skipList, skipWays) {
		if(!skipWays) {
			skipWays = [];
		}
		if(ways.length >= max) {
			return;
		}
		var node = tmpWay[tmpWay.length - 1];
		var children = findNodeChildren(node);
		for(var i = 0; i < children.length; i++) {
			var child = children[i];
			if(skipList && skipList.indexOf(child) != -1) {
				continue;
			}
			if(hasSeg(tmpWay, node, child)) {
				continue;
			}
			if(tmpWay.indexOf(child) != -1) {
				continue;
			}
			if(child == end) {
				if(indexOfWay(ways, tmpWay) == -1 && indexOfWay(ways, skipWays) == -1) {
					var findWay = [].concat(tmpWay);
					findWay.push(end);
					if(containNodes(tmpWay, mustList)) {
						var msg = [];
						Ext.each(tmpWay, function(tmp){
							msg.push(tmp.getName());
						});
						logger(msg.join('->'));
						ways.push(findWay);
					} else {
						skipWays.push(findWay);
					}
				}
			} else {
				tmpWay.push(child);
				deepSearch(max, tmpWay, end, ways, mustList, skipList, skipWays);
			}
		}
		tmpWay.pop();
	}
	TopoHelper = {
		getNearNodes : function(node) {
			return findNodeChildren(node);
		},
		getEdges : function(n1, n2) {
			var edges = n1.getEdges();
			var list = [];
			edges.each(function(edge) {
				var source = edge.getSource();
				var target = edge.getTarget();
				if((source == n1 && target == n2) || (source == n2 && target == n1)) {
					list.push(edge);
				}
			});
			return list;
		},
		search : function(datas, start, end, mustList, skipList, pageSize) {
			var ways = [];
			if(!mustList) {
				mustList = [];
			}
			if(!skipList) {
				skipList = [];
			}
			if(!Ext.isNumber(pageSize)) {
				pageSize = 20;
			}
			deepSearch(1000, [start], end, ways, mustList, skipList);
			ways.sort(function(way1, way2) {
				return way1.length - way2.length;
			});
			return ways.slice(0, pageSize);
		}
	}
})();