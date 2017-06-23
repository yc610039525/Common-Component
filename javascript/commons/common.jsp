<%@page import="com.boco.core.spring.SysProperty"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@include file="/commons/taglibs.jsp" %>

<%
response.addHeader("pragma", "no-cache");
response.addHeader("Cache-Control", "no-store, must-revalidate"); 
response.addHeader("expires", "Thu, 01 Jan 1970 00:00:01 GMT");
response.addHeader("expires", "0");
response.addHeader("X-UA-Compatible", "chrome=1");
response.addHeader("X-UA-Compatible", "IE=EmulateIE7");
%>
<META HTTP-EQUIV="pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-store, must-revalidate"> 
<META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT"> 
<META HTTP-EQUIV="expires" CONTENT="0">

<META http-equiv="X-UA-Compatible" CONTENT="chrome=1">
<META http-equiv="X-UA-Compatible" CONTENT="IE=EmulateIE7">
 
<script type="text/javascript">
	var ac = {
		userId : '<c:if test="${sessionScope.ac.isAdmin == true}">doraemon</c:if><c:if test="${sessionScope.ac.isAdmin == false}">${sessionScope.ac.userId}</c:if>',
		userName : '${sessionScope.ac.userName}',
		password:'${sessionScope.ac.password}',
		relatedDepartmentCuid : '${sessionScope.ac.relatedDepartmentCuid}',
		relatedDistrictCuid : '${sessionScope.ac.relatedDistrictCuid}',
		managerDistrictCuid : '${sessionScope.ac.managerDistrictCuid}',
		linkPhone : '${sessionScope.ac.linkPhone}',
		flowUser : '${sessionScope.ac.flowUser}'
	};
	var sysProp = {serverPath : {},networkPath:{}};
</script>
<c:forEach items="${clientSrvMap}" var="obj">
	<script type="text/javascript">
	sysProp.serverPath.${obj.key}='${obj.value}';
	</script>
</c:forEach>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	var ctx = "${ctx}";
	var baseUrl = 'http://${pageContext.request.serverName}:${pageContext.request.serverPort}';
	var jsContainer = [];
	function $importjs(_path){
		var flag = true;
		for(var i = 0; i < jsContainer.length; i++) {
			if(jsContainer[i] == _path) {
				flag = false;
			}
		}
		if(flag) {
			document.write("<script type='text/javascript' src='" + _path + "'></scr" + "ipt>");
			jsContainer.push(_path);
		}
	}
	
	function $importcss(_path){
		document.write("<link rel='STYLESHEET' type='text/css' href='" + _path + "'>");
	}
	
	function trimToEmpty(s) {
		if(s == undefined || s == null) {
			return '';
		}else {
			return s+'';
		}
	}
	
	function trimToUndefined(s) {
		if(s == null || s == '') {
			return undefined;
		}else {
			return s+'';
		}
	}
	function stopBubble(e) {
        if ( e && e.stopPropagation )
            e.stopPropagation();
        else
            window.event.cancelBubble = true;
    }
    function stopDefault( e ) {
        if ( e && e.preventDefault )
            e.preventDefault();
        else
            window.event.returnValue = false;
        return false;
    }
    String.prototype.format = function(args) {
        var result = this;
        if (arguments.length > 0) {    
            if (arguments.length == 1 && typeof (args) == "object") {
                for (var key in args) {
                    if(args[key]!=undefined){
                        var reg = new RegExp("({" + key + "})", "g");
                        result = result.replace(reg, args[key]);
                    }
                }
            }
            else {
                for (var i = 0; i < arguments.length; i++) {
                    if (arguments[i] != undefined) {
                        var reg = new RegExp("({[" + i + "]})", "g");
                        result = result.replace(reg, arguments[i]);
                    }
                }
            }
        }
        return result;
    }
</script>
<link rel="Shortcut Icon" href="favicon.ico">
<script type="text/javascript" src="${ctx}/commons/utils/UrlHelper.js"></script>