<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<script type="text/javascript" src="${ctx}/commons/utils/UrlHelper.js"></script>
		<script type="text/javascript" src="${ctx}/jsp/component_ui/menu/ContextMenuHelper.js"></script>
		<script type="text/javascript" src="${ctx}/jsp/component/grid/PropertyGridPanel.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = UrlHelper.getUrlObj(window.location.search.substring(1));
				var gridCfg = IRMS.grid.PropertyGridPanel.initParamsByUrl(param);
				
				var grid = new IRMS.grid.PropertyGridPanel({
					gridCfg : gridCfg,
					editable : param.editable,
					loadData : param.loadData=='false'?false:true
				});
				var view = new Ext.Viewport({
                    layout : 'fit',
                    items : [grid]
                });
			});
		</script>
	</head>
	<body>
	</body>
</html>