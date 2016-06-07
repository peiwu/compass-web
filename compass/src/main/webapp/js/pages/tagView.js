$(function(){
	/*setInterval(function(){
	    $.ajax({
				type:"POST",
				url:"search/checkRedis",
				success:function(data){
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
	$(parent.frames["header"].document).find("#top-tag-view span").addClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-integration span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-search span").removeClass("top-nav-active");
	//$("#top-tag-view span").addClass("top-nav-active");
	$(".header-choose-search").show();
	var parentId=$(".parent-class-item:eq(0)").attr("data-parentid");
	var target=$(".parent-class-item:eq(0)");
	clickParentClass(parentId,target);
});

//鼠标悬停一级提示，显示id和描述
$(".parent-hint-icon").mouseover(function(){
	  $(".parent-hint-div").css("display","inline-block");
}).mouseout(function(){
	 $(".parent-hint-div").css("display","none");
});
//鼠标悬停二级名称，出现问号，悬停问号，出现二级信息提示
$(document).on("mouseover",".name",function(){
	$(this).find(".tag-msg-icon").css("display","inline-block");
}).on("mouseout",".name",function(){
	$(this).find(".tag-msg-icon").css("display","none");
});

$(document).on("mouseover",".tag-msg-icon",function(){
	$(this).parent().find(".descid-hint-msg").css("display","inline-block");
}).on("mouseout",".tag-msg-icon",function(){
	$(this).parent().find(".descid-hint-msg").css("display","none");
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
	var childId=$("#current-child-class").val();
	var target=$(".child-class-item[data-childid='"+childId+"']");
	var chooseStat=$("#chooseStat").val();
	if(chooseStat=="0"){
		clickChildClass(childId,target,"tagView/getEffectiveTagsFromChildClassify",pageIndex,pageSize);
	}else{
		clickChildClass(childId,target,"tagView/getAllTagsFromChildClassify",pageIndex,pageSize);
	}		
	/*参数childId,target,pageIndex,pageSize*/	
});

//搜索框keyup，focus显示模糊标签
$('#search').on("keyup focus",function(){
	var  searchWord=$(this).val().trim();
	$(".show-blur-tag").hide().empty();
	$.AutoComplete.Auto(searchWord,"search/viewHint");
});

//标签查询
//选择有效或者所有标签时      搜索框 搜索调用不同的接口
$("#submit-search").on("click",function(){
	var chooseStat=$("#chooseStat").val();
	var searchWord=$('#search').val().trim();
	if(chooseStat=="0"){
		$(this).parents("form").attr("action","search/viewEffectiveSearch");
	}else if(chooseStat=="1"){
		$(this).parents("form").attr("action","search/viewAllSearch");
	}
	/*else if(chooseStat=="0"&&(re)){
		$(this).parents("form").attr("action","viewDeviceEffectiveSearch");
	}
	else if(chooseStat=="1"&&(re)){
		$(this).parents("form").attr("action","viewDeviceAllSearch");
	}*/
	
	
	$(".content-wrapper").html('<div class="loding-div"><span class="loading"></span><div>');
	$(".loading").css("margin-top","10%");
});

//点击一级分类查看其相关标签视图
$(document).on("click",".parent-class-item",function(){
	var parentId=$(this).attr("data-parentid");
	var target=$(this);
	$(".choose").each(function(){
		if($(this).val()=="0"){
			this.checked=true;
		}else{
			this.checked=false;
		}
	});
	$("#chooseStat").val("0");	
	//if(chooseStat=="0"){
	clickParentClass(parentId,target,"tagView/getEffectiveTagsFromParentClassify");
	//}else{
		//clickParentClass(parentId,target,"tagView/getAllTagsFromParentClassify");
	//}	
});

//查看二级分类及其标签视图
$(document).on("click",".child-class-item",function(){
	var childId=$(this).attr("data-childid");
	var target=$(this);
	var chooseStat=$("#chooseStat").val();
	if(chooseStat=="0"){
		clickChildClass(childId,target,"tagView/getEffectiveTagsFromChildClassify",1,10);
	}else{
		clickChildClass(childId,target,"tagView/getAllTagsFromChildClassify",1,10);
	}	
});

//显示有效或者所有标签，0表示仅显有效，1表示显示所有标签
$(".choose").change(function(){
	var chooseStat=$("input[name='viewChoose']:checked").val();
	$("#chooseStat").val(chooseStat);
	childId=$("#current-child-class").val();
	target=$(".child-class-item[data-childid="+childId+"]");
	var pageIndex=1;
	var pageSize=10;
	if(chooseStat=="0"){
		clickChildClass(childId,target,"tagView/getEffectiveTagsFromChildClassify",pageIndex,pageSize);
	}else{
		clickChildClass(childId,target,"tagView/getAllTagsFromChildClassify",pageIndex,pageSize);
	};	
});


//点击加号显示标签折线图
$(document).on("click",".show-chart",function(){
	var tagId=$(this).attr("data-tagid");
	$(".tag-linechart[id='"+tagId+"']").show();
	$.ajax({
		type:"POST",
		url:"tagView/getTagStatistics",
		data:{tagId:tagId},
		dataType:"text",
		success:function(data){
			//<div id="main" style="height:400px"></div>
			var tagStat=eval("("+data+")");
			var eachTime=tagStat.eachTime;
			var eachTimeStr = [];
			for(var i=0,len=eachTime.length;i<len;i++){
				var myDate=new Date(eachTime[i]);
				var year=myDate.getFullYear();
				var month =myDate.getMonth()+1;
				var date = myDate.getDate();
				eachTimeStr[i] = year+"."+month+"."+date;
			}
			
		
			var chartEle=document.getElementById(tagId);

			
			chartEle.style.height="200px";
			var eachPop=tagStat.eachPopulation;			
			 var tagChart = echarts.init(chartEle); 
			    
			 option = {
			
					    tooltip : {
					        trigger: 'axis',
					    },
				
					    calculable : true,
					    xAxis : [
					        {
					            type : 'category',
					            boundaryGap : false,
					            data : eachTimeStr
					        }
					    ],
					    yAxis : [
					        {
					            type : 'value',
	
					        }
					    ],
					    series : [ 
					        {
					            name:'总人数',
					            type:'line',
					            data:eachPop,
					            symbol:"auto",
					            symbolSize:"auto",
					            color:'#18b0c3',
					            itemStyle:{
					            	normal:{
					            		lineStyle:{color:'#18b0c3'}
					            	}
					            }
					        }
					    ]
					};
		 
		        tagChart.setOption(option); 
		        resizeLeft();
		        resizeSplit();
		}
	});	
	$(this).removeClass("show-chart").addClass("hide-chart");	
});

$(document).on("click",".hide-chart",function(){
	var tagId=$(this).attr("data-tagid");
	$(".tag-linechart[id='"+tagId+"']").hide();
	$(this).removeClass("hide-chart").addClass("show-chart");
	resizeLeft();
	 resizeSplit();
})  ;  



//点击一级分类
function clickParentClass(parentId,target){
	$(".current-page-num").text(1);
	target.addClass("parent-class-active");
	$(".parent-class-item[data-parentid='"+parentId+"']").find(".parent-class-icon").css("background-image","url('./css/img/parent-class-active-icon.png')");
	$(".parent-class-item[data-parentid!='"+parentId+"']").removeClass("parent-class-active");
	$(".parent-class-item[data-parentid!='"+parentId+"']").find(".parent-class-icon").css("background-image","url('./css/img/parent-class-icon.png')");
	var parentName=target.attr("data-parentname");
	var parentDesc=target.attr("data-parentDesc");
	$(".parent-classifies-name").text(">"+parentName);
	/*$(".current-parent-class").val(parentId);*/
	var template = $('#show-tag-template').html();
	Mustache.parse(template);

	$("#parent-classifies-id").text(parentId);
	$("#parent-classifies-desc").text(parentDesc);
	
	
	$(".tag-view").html('<div class="loding-div"><span class="loading"></span><div>');
	$.ajax({
		type:"POST",
		url:"tagView/getEffectiveTagsFromParentClassify",
		data:{parentId:parentId,pageIndex:1,pageSize:10},
		dataType:"text",
		success:function(data){
			var array=data.split("%%");
			var secondClass=array[0];
			var tagStat=array[1];
			var childStat=array[2];
			var tagNum=array[3];
			var totalPageNum;
			if(tagNum==0){
				totalPageNum=1;
			}else{
				totalPageNum=Math.ceil(tagNum/10);
			}			
			$(".total-tag-num").text(tagNum);
			$(".total-page").text(totalPageNum);
			
			var childStr="";
			var tagStr="";
			
			/*$(".child-class-stat").text(childStat+"%");*/
			//渲染二级分类
			if(!eval("("+secondClass+")").length){				
				$(".child-classifies ul").empty();	
				$(".tag-view").empty();	
				$(".tag-view-wrapper").hide();
				$(".no-child-class-hint").show();
			}else{
				$(".tag-view-wrapper").show();
				$(".no-child-class-hint").hide();
				$.each(eval("("+secondClass+")"),function(index,elem){
					if(index=="0"){
						$(".child-class-name").text(elem.classifyName);
						$(".child-class-desc").text(elem.classifyDescription);	
						$(".child-class-stat").text(childStat+"%");
						$("#current-child-class").val(elem.classifyId);
					}
					childStr+='<li class="child-class-item" data-parentid="'+parentId+'" data-parentname="'+parentName+'" data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'" data-childdesc="'+elem.classifyDescription+'"><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
				});
				$(".child-classifies ul").empty().append(childStr);
				$(".child-class-item:eq(0)").addClass("child-class-active");

				//渲染第一个二级分类的标签视图
			/*	$(".tag-view").html('<span class="loading"></span>');	*/
				if(!eval("("+tagStat+")").length){				
					$(".tag-view").empty();	
					$(".no-tag-hint").show();
				}else{
					$(".child-class-view").show();
					$(".no-tag-hint").hide();
					tagStat=eval("("+tagStat+")");
					for(var i=0,len=tagStat.length; i<len; i++){
						/*时间和日期转换*/
						var date=new Date(tagStat[i].latestUpdateTime);
						var year=date.getFullYear();
						var month=date.getMonth()+1;
						var day=date.getDate();
						var time=year+"-"+month+"-"+day;
						tagStat[i].time=time;
							
						if(tagStat[i].updateGranularity=="D"){
							tagStat[i].updateGranularity="天";
						}else if(tagStat[i].updateGranularity=="W"){
							tagStat[i].updateGranularity="周";
						}else if(tagStat[i].updateGranularity=="M"){
							tagStat[i].updateGranularity="月";
						}
						tagStr+=Mustache.render(template,tagStat[i]);
		            };
					$(".tag-view").empty().append(tagStr);
				}
			}
			resizeLeft();
			resizeSplit();
		}
	});
}

//点击二级分类
function clickChildClass(childId,target,url,pageIndex,pageSize){
	$(".current-page-num").text(pageIndex);
	var childName=target.attr("data-childname");
	var childDesc=target.attr("data-childdesc");
	var template = $('#show-tag-template').html();
	Mustache.parse(template);

	target.addClass("child-class-active");
	$(".child-class-item[data-childid!='"+childId+"']").removeClass("child-class-active");
	var tagStr="";
	$(".child-class-name").text(childName);
	$(".child-class-desc").text(childDesc);	
	$("#current-child-class").val(childId);
	
	$(".tag-view").html('<div class="loding-div"><span class="loading"></span><div>');	
	$("#pagination").hide();
	$.ajax({
		type:"POST",
		url:url,
		data:{childId:childId,pageIndex:pageIndex,pageSize:pageSize},
		dataType:"text",
		success:function(data){
			var array=data.split("%%");
			var tagStat=array[0];
			var childStat=array[1];
			
			var tagNum=array[2];
			var totalPageNum;
			if(tagNum==0){
				totalPageNum=1;
			}else{
				totalPageNum=Math.ceil(tagNum/10);
			}			
			$(".total-tag-num").text(tagNum);
			$(".total-page").text(totalPageNum);
			
			
			$(".child-class-stat").text(childStat+"%");
			if(!eval("("+tagStat+")").length){				
				$(".tag-view").empty().append(tagStr);	
				$(".no-tag-hint").show();
				$("#pagination").show();
			}else{
				$(".no-tag-hint").hide();
				tagStat=eval("("+tagStat+")");
					for(var i=0,len=tagStat.length; i<len; i++){	
						var date=new Date(tagStat[i].latestUpdateTime);
						var year=date.getFullYear();
						var month=date.getMonth()+1;
						var day=date.getDate();
						var time=year+"-"+month+"-"+day;
						tagStat[i].time=time;
						
						if(tagStat[i].updateGranularity=="D"){
							tagStat[i].updateGranularity="天";
						}else if(tagStat[i].updateGranularity=="W"){
							tagStat[i].updateGranularity="周";
						}else if(tagStat[i].updateGranularity=="M"){
							tagStat[i].updateGranularity="月";
						}
						tagStr+=Mustache.render(template,tagStat[i]);
		            };
				$(".tag-view").empty().append(tagStr);
				$("#pagination").show();
			}	
			resizeLeft();
			resizeSplit();
			
		}
		
	});
	
}

//左侧导航全屏高度显示
function resizeLeft(){
	var contRightEle=$(".content-right");
	var winHeight = $(window).height();
	var rightHeight = contRightEle.height();
	var contLeftHeight;
	var diff = 160;diffHeight=winHeight-diff;
	
	contLeftHeight = diffHeight>rightHeight?diffHeight:rightHeight;
	
/*	if(winHeight-diff < rightHeight){
		contLeftHeight = rightHeight;
	}
	*/
	var contLeftEle=document.getElementById("content-left");
	contLeftEle.style.height=contLeftHeight+"px";
}

//调节二级分类和标签之间分割线的长度
function resizeSplit(){
	var leftHeight=$(".child-class-view").height();
	var rightHeight=$(".tag-view").height();
	$(".child-tag-split").height((leftHeight>rightHeight)?leftHeight:rightHeight);
};