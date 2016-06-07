$(function(){
	update();
	//定时刷新
		setInterval(function(){
	    update();
		},20000);
});	

function update(){
	  $.ajax({
			type:"POST",
			url:"userChange",
			async: false,
			data:{},
			dataType:"text",
			success:function(data){
			//userlist展示
			var userlist=eval("("+data+")");
			var str1="";
			for(var index in userlist){
			//时间转化
			var date=new Date(parseInt(userlist[index]["logintime"]));
			var year=date.getFullYear();
			var month=date.getMonth()+1;
			var day=date.getDate();
			var hours=date.getHours(); //获取系统时，
			var minutes=date.getMinutes(); //分
			var seconds=date.getSeconds(); //秒
			var time=year+"/"+month+"/"+day+" "+hours+":"+minutes+":"+seconds;
			userlist[index]["logintime"]=time;
			var authority=userlist[index]["authority"];
			if(authority==0) userlist[index]["authority"]="超级管理员";
			else if(authority==1) userlist[index]["authority"]="普通管理员";
			else if(authority==2) userlist[index]["authority"]="广告用户";
			else userlist[index]["authority"]="普通用户";
			str1+='<tr><td>'+userlist[index]["name"]+'</td><td>'+userlist[index]["authority"]+'</td><td>'+userlist[index]["logintime"]+'</td></tr>';
		                              	}
			$(".user-table tbody").empty().append(str1);
			                    }//success
	       });//ajax
}