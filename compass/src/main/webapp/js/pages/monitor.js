var type,state,person,start,end;
$(function(){
	type="all";
	state="all";
	person="all";
	start=0;
	end=99999999999999999;
	update(type,state,person,start,end);
	//定时刷新
		setInterval(function(){
	    update(type,state,person,start,end);
		},20000);
});	
$(document).on('change', "#type", function () {  
	type=$("#type").val();
	update(type,state,person,start,end);
});
$(document).on('change', "#state", function () {  
	  state=$("#state").val();
	  update(type,state,person,start,end);
});
$(document).on('change', "#person", function () {  
	  person=$("#person").val();
	  update(type,state,person,start,end);
});
$(document).on('change', "#startdate", function () {  
	  var date=$("#startdate").val();
	  if (date=="") start=0;
	  else{
	  date=date.replace(/-/g,'/');
	  date=new Date(date).getTime();
	  start=date;
           }
	  //alert(start);
	  update(type,state,person,start,end);
});
$(document).on('change', "#enddate", function () {  
	 var date=$("#enddate").val();
	  if (date=="") end=99999999999999999;
	  else{
	  date=date.replace(/-/g,'/');
	  date=new Date(date).getTime();
	  end=date;
          }
	  update(type,state,person,start,end);
});
$(document).on('click', ".stop a", function () {  
	   var id=$(this).attr("data");
	   if(confirm("确定手动终止该任务?")){
	    $.ajax({
			type:"POST",
			url:"search/errorRedis",
			async: false,
			data:{id:id},
			dataType:"text",
			success:function(data){}
	          });
	    update(type,state,person,start,end);
	                                    }
});
function update(Type,State,Person,Start,End){
	  $.ajax({
			type:"POST",
			url:"monitorChange",
			async: false,
			data:{type:Type,state:State,person:Person,start:Start,end:End},
			dataType:"text",
			success:function(data){
			//redislist展示
			var redislist=eval("("+data+")");
			var str="";
			for(var index in redislist){
				var option="/";
				if(redislist[index].type=="0") {redislist[index].type="文件";redislist[index].prefix="/";}
				else redislist[index].type="redis";
				//时间转化
				var date=new Date(redislist[index].startDate);
				var year=date.getFullYear();
				var month=date.getMonth()+1;
				var day=date.getDate();
				var hours=date.getHours(); 
				var minutes=date.getMinutes(); 
				var seconds=date.getSeconds(); 
				var time=year+"/"+month+"/"+day+" "+hours+":"+minutes+":"+seconds;
				redislist[index].startDate=time;
				date=new Date(redislist[index].endDate);
				year=date.getFullYear();
				month=date.getMonth()+1;
				day=date.getDate();
				var hours=date.getHours(); 
				 minutes=date.getMinutes();
				seconds=date.getSeconds(); 
			    time=year+"/"+month+"/"+day+" "+hours+":"+minutes+":"+seconds;
				redislist[index].endDate=time;
				if(redislist[index].state=="0") 
				{
					redislist[index].state="正在执行";
					redislist[index].endDate="/";
					redislist[index].costTime="/";
					option="终止";
				}

				else if(redislist[index].state=="1") 
				{
					redislist[index].state="已完成";
				}
				else  
				{
					redislist[index].state="出错";
					redislist[index].endDate="/";
					redislist[index].costTime="/";
				}
				str+='<tr><td>'+redislist[index].id+'</td><td>'+redislist[index].person+'</td><td>'+redislist[index].type+'</td><td>'+
				redislist[index].path+'</td><td>'+redislist[index].prefix+'</td><td>'+redislist[index].num+'</td><td>'+redislist[index].state+'</td><td>'+redislist[index].startDate+'</td><td>'
				+redislist[index].endDate+'</td><td>'+redislist[index].costTime+'</td><td class="stop"><a href="javascript:void(null)" data="'+redislist[index].id+'">'+option+'</a></td></tr>';
			                            }
			$(".redis-table tbody").empty().append(str);
			                    }//success
	       });//ajax
}