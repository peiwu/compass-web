$(document).on("click","#username-a",function(){
	var authority = $("#authority").val();
	if(authority==0||authority==1){
		//window.top.location.href="toAuthority";
		window.open("toBakMain");
	}
	else alert("您没有权限执行权限管理操作！");
});
$(document).on("click","#logout",function(){
		window.top.location.href="logout";
});