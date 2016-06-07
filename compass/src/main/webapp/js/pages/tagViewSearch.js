
$(function(){
	/*
	var currentPage=$("#currentPage").val();
	if(currentPage=="view"){
		$("#top-tag-view span").addClass("top-nav-active");
	}else if(currentPage=="integration"){
		$("#top-tag-integration span").addClass("top-nav-active");
	}
	*/
	var resultMap=$("#resultMap").val();
	if(resultMap=="{}"){
		$(".admin-search-content").hide();
		$(".no-result").show();
	}else{
		$(".admin-search-content").show();
		$(".no-result").hide();
	}
	
	var chooseStat=$("#chooseStat").val();
	$(".choose[value='"+chooseStat+"']").attr("checked",true);
	
	
	//显示更新粒度
	$(".upGra-val").each(function(){
		var $thisText=$(this).text();
		if($thisText=="D"){
			$thisText="日";
		}else if($thisText=="W"){
			$thisText="周";
		}else if($thisText=="M"){
			$thisText="月";
		}
		$(this).text($thisText);
	});

	//显示时间
	$(".time").each(function(){
		var initialTime=Number($(this).text());
		var myDate=new Date(initialTime);
		var year=myDate.getFullYear();
		var month =myDate.getMonth()+1;
		var date = myDate.getDate();
		var time = year+"-"+month+"-"+date;
		$(this).text(time);
	});
	
	//页面加载显示标签折线图
	$(".show-chart").each(function(){
		var tagId=$(this).attr("data-tagid");
		var dataId=$(this).attr("data-id");
		$(".tag-linechart[data-id='"+dataId+"']").show();
		$.ajax({
			type:"POST",
			url:"getTagStatistics",
			data:{tagId:tagId},
			dataType:"text",
			 async: false,
			success:function(data){		
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
				var chartEle=document.getElementById(dataId);
				chartEle.style.height="200px";
				chartEle.style.width="890px";
				var eachPop=tagStat.eachPopulation;			
				 var tagChart = echarts.init(chartEle); 
				    
				 option = {
				
						    tooltip : {
						        trigger: 'axis'
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
						            symbol:"none", 
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
			}
		});	
	});
	splitHeight();

});



//鼠标悬停二级名称，出现问号，悬停问号，出现二级信息提示
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


//选择有效或者所有标签时      搜索框 搜索调用不同的接口
$("#submit-search").on("click",function(){
	var chooseStat=$("#chooseStat").val();
	var searchWord=$('#search').val().trim();
	var re=/^[A-Za-z0-9-]{10,100}$/.test(searchWord);
	if(chooseStat=="0"&&(!re)){
		$(this).parents("form").attr("action","viewEffectiveSearch");
	}else if(chooseStat=="1"&&(!re)){
		$(this).parents("form").attr("action","viewAllSearch");
	}else if(chooseStat=="0"&&(re)){
		$(this).parents("form").attr("action","viewDeviceEffectiveSearch");
	}
	else if(chooseStat=="1"&&(re)){
		$(this).parents("form").attr("action","viewDeviceAllSearch");
	}
	
	$(".no-result").hide();
	$(".view-search-content").html('<div class="loding-div"><span class="loading"></span><div>');
	$(".loading").css("margin-top","10%");
	
});

//在已搜索结果页面   切换显示有效或者所有标签
$(".choose").change(function(){
	var chooseStat=$("input[name='viewChoose']:checked").val();
	$("#chooseStat").val(chooseStat);
	var searchWord=$("#searchWord").val().trim();
	$("#search").val(searchWord);
	var $searchForm=$("#search-form");
	/*if(chooseStat=="0"){
		$("#search")
		$("#tagName1").val(searchWord);
		$("#effectiveTagsForm").submit();
	}else{
		$("#tagName2").val(searchWord);
		$("#allTagsForm").submit();
		
	};	*/
	var re=/^[A-Za-z0-9-]{10,100}$/.test(searchWord);
	if(chooseStat=="0"&&(!re)){
		$searchForm.attr("action","viewEffectiveSearch");
	}else if(chooseStat=="1"&&(!re)){
		$searchForm.attr("action","viewAllSearch");
	}else if(chooseStat=="0"&&(re)){
		$searchForm.attr("action","viewDeviceEffectiveSearch");
	}
	else if(chooseStat=="1"&&(re)){
		$searchForm.attr("action","viewDeviceAllSearch");
	}
	$searchForm.submit();
	$(".no-result").hide();
	$(".view-search-content").html('<div class="loding-div"><span class="loading"></span><div>');
	$(".loading").css("margin-top","10%");
});

$(document).on("click",".hide-chart",function(){
	var tagId=$(this).attr("data-tagid");
	$(".tag-linechart[id='"+tagId+"']").hide();
	$(this).removeClass("hide-chart").addClass("show-chart");
})  ;  





//搜索框keyup，focus显示模糊标签
$('#search').on("keyup focus",function(){
	var  searchWord=$(this).val().trim();
	$(".show-blur-tag").hide().empty();
	$.AutoComplete.Auto(searchWord,"viewHint");
});

//调节二级分类和标签之间分割线的长度
function splitHeight(){
	$(".each-result-wrapper").each(function(){	
		var leftHeight=$(this).find(".child-class-view").height();
		var rightHeight=$(this).find(".tag-view").height();
		$(this).find(".child-tag-split").height((leftHeight>rightHeight)?leftHeight:rightHeight);
	});
};

