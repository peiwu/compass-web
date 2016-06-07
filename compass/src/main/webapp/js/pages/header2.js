$(function(){
	$("#top-monitor span").addClass("top-nav-active");
	$("#top-online span").removeClass("top-nav-active");
	$("#top-authority span").removeClass("top-nav-active");
});

$(document).on("click","#monitor",function(){
	window.parent.frames["main"].location.href="toMonitor";
		$("#top-monitor span").addClass("top-nav-active");
		$("#top-online span").removeClass("top-nav-active");
		$("#top-authority span").removeClass("top-nav-active");
});
$(document).on("click","#online",function(){
	    window.parent.frames["main"].location.href="toOnline";
		//window.location.href="toOnline";
		$("#top-monitor span").removeClass("top-nav-active");
		$("#top-online span").addClass("top-nav-active");
		$("#top-authority span").removeClass("top-nav-active");
});
$(document).on("click","#authority",function(){
	$("#top-monitor span").removeClass("top-nav-active");
	$("#top-online span").removeClass("top-nav-active");
	$("#top-authority span").addClass("top-nav-active");
	var authority = $("#authority").val();
	if(authority==0){
		window.parent.frames["main"].location.href="toAuthority";
	}
	else alert("您没有权限执行此操作！");
});