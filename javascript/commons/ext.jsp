<link type="text/css" rel="stylesheet" href="${ctx}/jslib/ext/resources/css/ext-all.css">
<!--  
<link type="text/css" rel="stylesheet" href="${ctx}/jslib/ext/resources/css/xtheme-gray.css">
-->
<link type="text/css" rel="stylesheet" href="${ctx}/resources/icons/$icon.css">
<link type="text/css" rel="stylesheet" href="${ctx}/resources/icons/$treeicon.css">
<script type="text/javascript" src="${ctx}/jslib/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="${ctx}/jslib/ext/ext-all.js"></script>
<script type="text/javascript" src="${ctx}/jslib/ext/ext-customer.js"></script>
<script type="text/javascript" src="${ctx}/jslib/ext/local/ext-lang-zh_CN.js"></script>

<script type="text/javascript" src="${ctx}/commons/utils/MaskHelper.js"></script>
<script type="text/javascript" src="${ctx}/commons/dwr/DwrProxy.js"></script>

<script type="text/javascript">
	Ext.BLANK_IMAGE_URL = '${ctx}/jslib/ext/resources/images/default/s.gif';
	Ext.chart.Chart.CHART_URL = '${ctx}/jslib/ext/resources/charts.swf';
	
	Ext.reg('twintrigger', Ext.form.TwinTriggerField);
	
	Ext.util.JSON.encodeDate = function(d) {
	    return d.format('"Y-m-d H:i:s"');
	};
</script>