<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>空间查询</title>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<%@ include file="/cmp_include/grid.jsp"%>
		<%@ include file="/cmp_include/tree.jsp"%>
		<script type="text/javascript" src="${ctx}/cmp_res/combo/query/SpaceQueryPanel.js"></script>
		<script type="text/javascript">
			Ext.onReady(function(){
				var param = UrlHelper.getUrlObj(window.location.search.substring(1));
				var panel = new Frame.combo.SpaceQueryPanel({
					inputParam : param
				});
				new Ext.Viewport({
                    layout : 'fit',
                    items : [panel]
                });
			});
		</script>
	</head>
	<body>
	</body>
</html>