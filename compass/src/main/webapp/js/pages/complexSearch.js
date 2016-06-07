$(function(){
	//轮询方式
	/*setInterval(function(){
    $.ajax({
			type:"POST",
			url:"search/checkRedis",
			success:function(data)
			{
			alert(data);
		    if(!data.equals(""))
		      {
	        var result=data.split("%%");
	        for(var i=0;i<result.length;i++) alert(result[i]+"  导入redis完成");
		      }
			}
			});
	},30000);*/
	resizeLeft();
	$(window).resize(function() {
		//左侧导航全屏高度显示
		resizeLeft();
	});	
	$(parent.frames["header"].document).find("#top-tag-admin span").removeClass("top-nav-active");	
	$(parent.frames["header"].document).find("#top-tag-view span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-integration span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-search span").addClass("top-nav-active");
	//$("#top-tag-search span").addClass("top-nav-active");	
	//首次打开窗口相当于点击第一个父分类
	var currentParentId=$("#current-parent-class").val();
	clickParentClass(currentParentId);		
	
	//改变标签表格样式
	$(document).on("mouseover",".tag-table-tbody tr",function(){
		$(this).css("background","#f4f2f2");
	});
	$(document).on("mouseout",".tag-table-tbody tr",function(){
		$(this).css("background","#fff");
	});
});
//全局变量
var tagIds=new Array();
var tagNames=new Array();
var dvcNum;
//查询设备号
$("#submit-search").on("click",function(){
	var chooseStat=$("#chooseStat").val();
	var searchWord=$('#search').val().trim();
	//$(this).parents("form").attr("action","search/dvcSearch");
	var re=/^[0-9]*$/.test(searchWord);
	if(re){
		$(this).parents("form").attr("action","search/dvcSearch");
		$(".content-wrapper").html('<div class="loding-div"><span class="loading"></span><div>');
		$(".loading").css("margin-top","10%");
	                          }
	else {
		alert("请输入合法的设备标识符！");
		 }
});
//添加查询标签
$(document).on("click",".add-tag",function(){
		var tagId=$(this).attr("data-tagid");
		var tagName=$(this).attr("data-tagname");
		tagIds.push(tagId);
		tagNames.push(tagName);
		var childstr='<li class="search-tag-item"><span>'+tagName+'</span></li>';
		$(".search-tags ul").append(childstr);
		$("#search-button").attr("disabled",false);
		$("#clear-button").attr("disabled",false);
});

//导入redis
$("#redis-button").on("click",function(){
	var authority = $("#authority").val();;
	if(authority==3){
		alert("您没有权限执行此操作！");
	}
else{
	var prefix;
	var ok=false;
	while(!ok){
	prefix=prompt("请输入前缀:（最大8位,不能含'#','&'）");
	if (prefix.length>8) {alert("前缀长度不能超过8位！请重新输入");ok=false;}
	else if(prefix=="") {alert("前缀不能为空！请重新输入");ok=false;}
	else if(prefix.indexOf('#')>=0){alert("前缀不能包含'#'，请重新输入");}
	else if(prefix.indexOf('&')>=0){alert("前缀不能包含'&'，请重新输入");}
	else{
		var id;
		$.ajax({
			type:"POST",
			url:"search/checkPrefix",
			async:false,
			data:{prefix:prefix},
			dataType:"text",
			success:function(data){
			    if(data=="ok") ok=true;
			    else {alert("前缀与其他用户重名！请重新输入");ok=false;}
			                       }
			  });
     	}
	           }//while
    var temp1="";
	for(i=0;i<tagIds.length;i++) temp1=temp1+tagIds[i]+"%%";
	var temp2="";
	for(i=0;i<tagNames.length;i++) temp2=temp2+tagNames[i]+"%%";
	//数组清空，标签清空，查询导出不可用
	tagIds=[];
	tagNames=[];
	$("#clear-button").attr("disabled",true);
	$("#search-button").attr("disabled",true);
	$("#redis-button").attr("disabled",true);
	$("#export-submit").attr("disabled",true);
	$(".search-tags ul").empty();
	$(".dvc-num").text("");
	alert("已送入后台处理,完成后将通知您，请勿关闭页面！");
	var msg0=temp1+"&&"+dvcNum+"&&"+prefix;
	var msg=temp1+"&&"+temp2;
	var id;
	$.ajax({
		type:"POST",
		url:"search/insertRedis",
		async:false,
		data:{msg:msg0},
		dataType:"text",
		success:function(data){
		    id=data;
		                       }
		  });
	   if (parent.socket.readyState !== 1)
        {
	     parent.socket.close();
         parent.initWebSocket();
         setTimeout(function(){parent.socket.send("redis&&"+msg+"&&"+id+"&&"+dvcNum+"&&"+prefix);},1500);
        }
       else parent.socket.send("redis&&"+msg+"&&"+id+"&&"+dvcNum+"&&"+prefix);
	}
});

//清空
$("#clear-button").on("click",function(){
	//数组清空，标签清空，查询导出不可用
	tagIds=[];
	tagNames=[];
	dvcNum=0;
	$("#clear-button").attr("disabled",true);
	$("#search-button").attr("disabled",true);
	$("#export-submit").attr("disabled",true);
	$("#redis-button").attr("disabled",true);
	$(".search-tags ul").empty();
	$(".dvc-num").text("");
});

//查询
$("#search-button").on("click",function(){
    var temp="";
	for(i=0;i<tagIds.length;i++) temp=temp+tagIds[i]+"%%";
	$.ajax({
		type:"POST",
		url:"search/getDvcByTags",
		async: false,
		data:{tagIds:temp},
		dataType:"text",
		success:function(data){
		    dvcNum=data;
		    if(dvcNum=="error") alert("查询失败，请稍候重试！");
		    else{
			if(dvcNum!=0){
				$("#export-submit").attr("disabled",false);
				$("#redis-button").attr("disabled",false);
			             }
			$(".dvc-num").text("共"+dvcNum+"个用户");
	            }
		                        }
		});
});

//导出文件
$("#export-submit").on("click",function(){
	var authority = $("#authority").val();;
	if(authority==3){
		alert("您没有权限执行此操作！");
	}
	else {
	var temp1="";
	for(i=0;i<tagIds.length;i++) temp1=temp1+tagIds[i]+"%%";
	var temp2="";
	for(i=0;i<tagNames.length;i++) temp2=temp2+tagNames[i]+"%%";
	//数组清空，标签清空，查询导出不可用
	tagIds=[];
	tagNames=[];
	$("#clear-button").attr("disabled",true);
	$("#search-button").attr("disabled",true);
	$("#redis-button").attr("disabled",true);
	$("#export-submit").attr("disabled",true);
	$(".search-tags ul").empty();
	$(".dvc-num").text("");
	var msg0=temp1+"&&"+dvcNum;
	var msg=temp1+"&&"+temp2;
	var id;
	$.ajax({
		type:"POST",
		url:"search/insertFile",
		async:false,
		data:{msg:msg0},
		dataType:"text",
		success:function(data){
		    id=data;
		                       }
		  });
	alert("后台正在处理，完成后将自动下载");
	if (parent.socket.readyState !== 1)
	 {
		    parent.socket.close();
	        parent.initWebSocket();
	        setTimeout(function(){parent.socket.send("files&&"+msg+"&&"+id+"&&"+dvcNum);},1500);
	 }
	else parent.socket.send("files&&"+msg+"&&"+id+"&&"+dvcNum);
	}
});
//分页
$(".page-up-icon").on("click",function(){
	var pageChooseVal=Number($(".page-choose").val());
	var totalPage=Number($(".total-page").text());
	if(pageChooseVal<totalPage){
		pageChooseVal+=1;
	}else{
		pageChooseVal=totalPage;
	}	
	$(".page-choose").val(pageChooseVal);
});

$(".page-down-icon").on("click",function(){
	var pageChooseVal=Number($(".page-choose").val());
	if(pageChooseVal>1){
		pageChooseVal-=1;
	}else{
		pageChooseVal=1;
	}
	$(".page-choose").val(pageChooseVal);
});

$(".page-operator").on("click",function(){
	var $this=$(this);
	var pageIndex=1;
	var pageSize=10;
	var childId=$("#current-child-class").val();
	if($this.hasClass("page-up")){
		if($(".current-page-num").text()!="1"){
			pageIndex=Number($(".current-page-num").text())-1;
			$(".current-page-num").text(pageIndex);
			$(".page-choose").val(pageIndex);
		}else{
			pageIndex=1;
			$(".current-page-num").text(pageIndex);
			$(".page-choose").val(pageIndex);
		}
	}else if($this.hasClass("page-down")){
		if($(".current-page-num").text()!=$(".total-page").text()){
			pageIndex=Number($(".current-page-num").text())+1;
			$(".current-page-num").text(pageIndex);
			$(".page-choose").val(pageIndex);
		}else{
			pageIndex=$(".total-page").text();
			$(".current-page-num").text(pageIndex);
			$(".page-choose").val(pageIndex);
		}	
	}else{
		pageIndex=$(".page-choose").val();
		$(".current-page-num").text(pageIndex);
		$(".page-choose").val(pageIndex);
	}
	clickChildClass(childId,pageIndex,pageSize);
	
});



//显示一级分类相关内容
$(document).on("click",".parent-class-item a",function(){
	var parentId=$(this).attr("data-parentid");
	clickParentClass(parentId);
});


//显示二级分类下的标签
$(document).on("click",".child-class-item",function(){
	var childId=$(this).attr("data-childid");
	var pageIndex=1;
	var pageSize=10;
	clickChildClass(childId,pageIndex,pageSize);
});




//搜索框keyup，focus显示模糊标签
$('#search').on("keyup focus",function(){
	var  searchWord=$(this).val().trim();
	console.log(searchWord.length);
	$(".show-blur-tag").hide().empty();
	$.AutoComplete.Auto(searchWord,"adminHint");
});

//左侧导航全屏高度显示
function resizeLeft(){
	var contRightEle=$(".content-right");
	var winHeight = $(window).height();
	var rightHeight = contRightEle.height();
	var contLeftHeight;
	var diff = 160;
	
	contLeftHeight =  winHeight-diff;
	
	if(winHeight-diff < rightHeight){
		contLeftHeight = rightHeight;
	}
	
	var contLeftEle=document.getElementById("content-left");
	contLeftEle.style.height=contLeftHeight+"px";
}

//点击一级分类，显示相应二级分类及第一个二级分类的标签
function clickParentClass(parentId){	
	$("#current-parent-class").val(parentId);
	$(".current-page-num").text(1);
	var parentName,parentDesc;
	$(".parent-class-item").each(function(){
		if($(this).attr("data-parentid")==parentId){
			parentName=$(this).attr("data-parentname");
			parentDesc=$(this).attr("data-parentdesc");
			$(this).addClass("parent-class-active");
			$(this).find(".parent-class-icon").css("background-image","url('./css/img/parent-class-active-icon.png')");
		}else{
			$(this).removeClass("parent-class-active");
			$(this).find(".parent-class-icon").css("background-image","url('./css/img/parent-class-icon.png')");
		}
	});

	$.ajax({
		type:"POST",
		url:"tagAdmin/getTagsFromParentClassify",
		async: false,
		data:{parentId:parentId,pageIndex:1,pageSize:10},
		dataType:"text",
		success:function(data){
			var array=data.split("%%");
			var secondClass=array[0];
			var tags=array[1];
			var tagNum=array[2];
			var totalPageNum;
			if(tagNum==0){
				totalPageNum=1;
			}else{
				totalPageNum=Math.ceil(tagNum/10);
			}
			var childstr="";
			var tagstr="";
			
			$(".total-tag-num").text(tagNum);
			/*$("#pagination-left").text("共"+tagNum+"个标签，每页显示10条信息"); */
			$(".total-page").text(totalPageNum);
			$("#parent-classifies-name").text(">"+parentName);
			$("#parent-classifies-desc").text(parentDesc);
			$("#parent-classifies-id").text(parentId);
	
			//渲染第一个二级分类的标签
			$.each(eval("("+tags+")"),function(index,elem){
				tagstr+='<tr><td class="tag-name">'+elem.tagName+'</td><td class="tag-id">'+elem.tagId+'</td><td class="tag-desc">'+elem.tagDescription+'</td>'				
					+'<td><li class="operator-item add-tag" data-tagname="'+elem.tagName+'" data-tagid="'+elem.tagId+'"><a href="javascript:void(null)"></a></li></td></tr>';												
			});
			$(".tag-table tbody").empty().append(tagstr);
			
			//渲染二级分类
			if(!eval("("+secondClass+")").length){				
				$(".child-class-name").text("");
				$(".render-result").hide();
				$(".no-child-class-hint").show();
			}else{
				$(".render-result").show();
				$(".no-child-class-hint").hide();
				$.each(eval("("+secondClass+")"),function(index,elem){
					if(index=="0"){
						$("#current-child-class").val(elem.classifyId);
						childstr+='<li class="child-class-item" data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'"><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
					}else{
						childstr+='<li class="child-class-item"  data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'"><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
					}	
				});
			}						
			$(".child-classifies ul").empty().append(childstr);
			$(".child-class-item:eq(0)").addClass("child-class-active");
			
			resizeLeft();
			
		}
	});
}

//点击二级分类，显示其相关信息及其标签
function clickChildClass(childId,pageIndex,pageSize){
	
//	var contLeftEle=document.getElementById("content-left");
//	contLeftEle.style.height="100px";
	
	$("#current-child-class").val(childId);
	$(".current-page-num").text(pageIndex);
	$(".child-class-item").each(function(){
		if($(this).attr("data-childid")==childId){
			$(this).addClass("child-class-active");
		}
	});
	$(".child-class-item[data-childid!='"+childId+"']").removeClass("child-class-active");
	
	$.ajax({
		type:"POST",
		url:"tagAdmin/getTagsFromChildClassify",
		async:false,
		data:{childId:childId,pageIndex:pageIndex,pageSize:pageSize},
		dataType:"text",
		success:function(data){
			var array=data.split("%%");
			var tags=array[0];
			var tagNum=array[1];
			var totalPageNum;
			if(tagNum==0){
				totalPageNum=1;
			}else{
				totalPageNum=Math.ceil(tagNum/10);
			}
			
			$(".total-tag-num").text(tagNum);
			$(".total-page").text(totalPageNum);
			var tagstr="";
			var tagstr="";
			$.each(eval("("+tags+")"),function(index,elem){ 
				tagstr+='<tr><td class="tag-name">'+elem.tagName+'</td><td class="tag-id">'+elem.tagId+'</td><td class="tag-desc">'+elem.tagDescription+'</td>'				
				+'<td><li class="operator-item add-tag" data-tagname="'+elem.tagName+'" data-tagid="'+elem.tagId+'"><a href="javascript:void(null)"></a></li></td></tr>';												
		});
		$(".tag-table tbody").empty().append(tagstr);
			resizeLeft();
		}
	});
}
