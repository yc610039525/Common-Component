<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>通用Excel上传下载</title>
<%@ include file="/commons/common.jsp"%>
<%@ include file="/commons/dwr.jsp"%>
<%@ include file="/commons/ext.jsp"%>
<%@ include file="/cmp_include/form.jsp"%>
<%@ include file="/cmp_include/grid.jsp"%>
<script type="text/javascript" src="${ctx}/cmp_res/import/ExcelImport.js"></script>

  <body>
    <script type="text/javascript">
	Ext.onReady(function() {
		var param = UrlHelper.getUrlObj(window.location.search.substring(1));
		var panel = new Frame.elt.ExcelImport({
			templateId  : param.templateId,
			bmClassId	: param.bmClassId,
			boName : param.boName,
			taskId : param.TASK_ID,
			sheetType: param.sheetType
		});
		var view = new Ext.Viewport({
	    	layout	: 'fit',
	    	items	: [panel]
		});
	});
	</script>
  </body>
</html>
