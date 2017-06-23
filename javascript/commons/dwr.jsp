<%@page language="java" pageEncoding="UTF-8"%>
<%
response.setHeader("Expires", "01 Jan 2099 00:00:00 GMT");
response.setHeader("Cache-Control", "public"); // HTTP/1.1
response.setHeader("Pragma", "Pragma"); // HTTP/1.0
request.setAttribute("ctx",request.getContextPath());
%>
<script src="${ctx}/commons/utils/FilterChar.js"></script>
<script src="${ctx}/dwr/util.js"></script>
<script src="${ctx}/dwr/engine.js"></script>
<script src="${ctx}/commons/dwr/sengine.js"></script>
<script type="text/javascript">
	DWREngine.setErrorHandler(function(error){
	    error=FilterChar.filterChinese(error);//去掉错乱英文提示
	    if(error=='')
	       error='发生未知异常';
		if(error && error != '') {
			if(error=='Unauthorized'){
				window.location.href= ctx+'/login.jsp';
			}else {
				if(error!='语法错误'){
					if(error.indexOf('SqlMapClient') != -1){
						if(Ext){
							Ext.Msg.alert("程序运行异常", error);
						}else{
							alert("程序运行异常,请联系管理员:"+error);
						}
					}else{
					  var n = error.lastIndexOf('Exception:');
					  if(n != -1) {
						error = error.substring(n+10);
					  }
					  try {
						 Ext.Msg.alert("业务异常", error);
					  }catch(e) {
						alert(error);
					  }
					}
				}
				
				try {
					MaskHelper.unmaskAll();
				}catch(e){}
			}
		}
	});
</script>