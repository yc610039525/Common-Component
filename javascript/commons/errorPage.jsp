<%@ page language="java" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@ include file="/commons/common.jsp"%>
		<!-- deivce form plugin-->
	</head>
	<body>
	</body>
	<script type="text/javascript">
		var msg = "${msg}";
		document.body.innerHTML = msg;
	</script>
</html>