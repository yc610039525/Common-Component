<script type="text/javascript" src="${ctx}/jslib/jquery/jquery-1.7.1.min.js"></script>

<link type="text/css" rel="stylesheet" href="${ctx}/jslib/jquery/ui/css/cupertino/jquery-ui-1.8.18.custom.css">
<script type="text/javascript" src="${ctx}/jslib/jquery/ui/js/jquery-ui-1.8.18.custom.min.js"></script>

<script type="text/javascript">
(function($){
	$.getUrlParam = function(name) {
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r!=null) return unescape(r[2]); return null;
	}
})(jQuery);
</script>