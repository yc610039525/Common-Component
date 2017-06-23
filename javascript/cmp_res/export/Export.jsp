<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<%@ include file="/commons/dwr.jsp"%>
		<%@ include file="/commons/ext.jsp"%>
		<script type="text/javascript" src="${ctx}/dwr/interface/TicketDwrAction.js"></script>
		<script type="text/javascript" src="${ctx}/commons/utils/UrlHelper.js"></script>
		<script type="text/javascript" src="${ctx}/cmp_res/export/Export.js"></script>
		<script type="text/javascript">
			Ext.onReady(function() {
				var param = UrlHelper.getUrlObj(window.location.search.substring(1));
				param.s_PARAM='';
				if(param.ticketId) {
					DWREngine.setAsync(false);
					TicketDwrAction.getValue(param.ticketId, function(value){
						param.s_PARAM = Ext.encode(value);
					});
					DWREngine.setAsync(true);
				}
				var exportCfg = IRMS.download.Export.initParamsByUrl(param);
				var panel = new IRMS.download.Export({
					inputParam : exportCfg
				});
				var view = new Ext.Viewport({
                    layout : 'fit',
                    items : [panel]
                });
			});
		</script>
	</head>
	<body>
	</body>
</html>