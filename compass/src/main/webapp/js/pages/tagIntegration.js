$(function(){
	resizeLeft();
	$(window).resize(function() {
		//左侧导航全屏高度显示
		resizeLeft();
	});
	$(parent.frames["header"].document).find("#top-tag-admin span").removeClass("top-nav-active");	
	$(parent.frames["header"].document).find("#top-tag-view span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-integration span").addClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-search span").removeClass("top-nav-active");
	//$("#top-tag-integration span").addClass("top-nav-active");
	
	$(".header-choose-search").show();
	var parentId=$(".parent-class-item:eq(0)").attr("data-parentid");
	$("#current-parent-class").val(parentId);
	var target=$(".parent-class-item:eq(0)");
	clickParentClass(parentId,target);
});


//点击下拉框显示可选的一级分类列表
$("#add-original-icon").on("click",function(){
	$(".parent-class-list").show();
});
$("body").click(function(evt) {
	if (evt.target.id!="add-original-icon") {
		 	$(".parent-class-list").hide();
	 }
});        



$(".parent-class-list-item").mouseover(function(){       		                     
	$(this).css("background", "#18b0c3");
}).mouseout(function() {
   $(this).css("background", "#fff");
}).click(function() {
	var parentId=$(this).attr("data-parentid");
	var parentName=$(this).attr("data-parentname");
	
	if($(".integration-operator-div:has(div)").length==0){
		$('<div class="integration-operator-item"><span class="add-integration-icon-first"></span><div class="integration-operator-parent" data-parentid="'+parentId+'"><span class="parent-name">'+parentName+'</span><span class="show-child-icon" data-parentid="'+parentId+'"></span></div><div class="integration-operator-child"></div><span class="remove-integration-item-icon"></span></div>').appendTo($(".integration-operator-div"));
	}else{
		$('<div class="integration-operator-item"><span class="add-integration-icon"></span><div class="integration-operator-parent" data-parentid="'+parentId+'"><span class="parent-name">'+parentName+'</span><span class="show-child-icon" data-parentid="'+parentId+'"></span></div><div class="integration-operator-child"></div><span class="remove-integration-item-icon"></span></div>').appendTo($(".integration-operator-div"));
	}

	$(".parent-class-list").hide();
 });


//删除integration-operator-item项
$(document).on("click",".remove-integration-item-icon",function(){
	$(this).parents(".integration-operator-item").remove();
	$(".integration-operator-item:eq(0)").find(".add-integration-icon").css({"opacity":"0","filter":"alpha(opacity=0)"});
});
//获得所属一级分类的所有二级分类及标签
$(document).on("click",".integration-operator-parent",function(){
	var $this=$(this);
	var parentId=$(this).attr("data-parentid");
	$this.parents(".integration-operator-item").find(".integration-operator-child").empty();
	$.ajax({
	type:"POST",
	url:"tagIntegration/getChildAndTag",
	data:{parentId:parentId},
	dataType:"json",
	success:function(data){
		var allChildClassStr='';
		for(var i in data){
			allChildClassStr+='<div class="origin-class-div"><div class="origin-child-class-div"><span class="show-tag-icon"></span><span class="origin-child-class-item">'+i+'</span></div><div class="origin-tag-div">';
			for(var j in data[i] ){
				var item=data[i][j];
				allChildClassStr+='<li class="origin-tag-item" data-tagid="'+item['tagId']+'">'+item['tagName']+'</li>';
			}
			allChildClassStr+='</div></div>';
		}
		$this.parents(".integration-operator-item").find(".integration-operator-child").toggle();
		$this.parents(".integration-operator-item").find(".integration-operator-child").append('<ul>'+allChildClassStr+'</ul>');
		$(".origin-tag-div").hide();
		
	}
	});	
});
/*点击二级分类，显示或收起其下面的标签*/
$(document).on("click",".show-tag-icon",function(){
	$(this).parents(".origin-child-class-div").next(".origin-tag-div").toggle();
	$(this).removeClass("show-tag-icon").addClass("hide-tag-icon");
});
$(document).on("click",".hide-tag-icon",function(){
	$(this).parents(".origin-child-class-div").next(".origin-tag-div").toggle();
	$(this).removeClass("hide-tag-icon").addClass("show-tag-icon");
});



/*选中标签或者点击空白处收起组合项下的显示块*/
$(document).on("click",".origin-tag-item",function(){
	var $thisText=$(this).text();
	var tagid=$(this).attr("data-tagid");
	$(this).parents(".integration-operator-item").find(".integration-operator-parent").attr("data-tagid",tagid);
	$(this).parents(".integration-operator-item").find(".parent-name").text($thisText);
	$(this).parents(".integration-operator-child").hide();
});
$("body").click(function(evt) {
	if (evt.target.class="") {
		 	$(".integration-operator-child").hide();
	 }
}); 

//点击组合，显示新增组合标签弹窗
$(document).on("click","#submit-integration-btn",function(){
	var authority = $("#authority").val();;
	if(authority==2){
		alert("没有权限，请联系刘可为(kwliu@iflytek.com)");
	}
	else{
		//获得所有二级分类
		var tagIds="";
		$(".integration-operator-parent").each(function(){
			tagIds+=$(this).attr("data-tagid")+"*";
		});
		var allChildClassStr="";
		$.ajax({
			type:"POST",
			url:"tagIntegration/getAllChildClassifies",
			dataType:"text",
			success:function(data){
				$.each(eval("("+data+")"),function(index,elem){
					allChildClassStr+='<option class="all-child-item" value="'+elem.classifyId+'">'+elem.classifyName+'</option>';			
				});

				var  str='<div class="alert-edit-tag">'
					+'<input type="hidden" id="tag-ids" value="'+tagIds+'"/>'
					+'<div class="alert-header"><span class="alert-header-left">编辑标签</span><span class="alert-header-right" id="close"></span></div>'
					+'<div class="content edit-tag-content"><div class="add-tag-form">'
						+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-tag-name" class="alert-name-label">标签名称：</label><input type="text" class="validate alert-tag-name" id="edit-tag-name" name="alert-tag-name" placeholder="请填写详细的标签名称"/><span class="errMsg input-errMsg"></span></div>'
						+'<div class="tag-desc-div"><span class="tag-desc-star">*</span><label for="alert-tag-desc" class="tag-desc-label">标签描述：</label><textarea type="text" class="validate alert-tag-desc" id="edit-tag-desc" name="alert-tag-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg input-errMsg"></span></div>'	
						+'<div class="tag-sort-div"><span class="tag-sort-star">*</span><label for="alert-tag-sort" class="tag-sort-label">标签分类：</label>'
							+'<select class="alert-tag-sort" id="alert-tag-sort" multiple="multiple">'+allChildClassStr+'</select><span class="errMsg tag-sort-errMsg"></span>'
						+'</div>'
						+'<div class="tag-updateGra-div"><span class="tag-updateGra-star">*</span><label for="alert-tag-updateGra" class="tag-updateGra-label">更新粒度：</label>'
							+'<input type="text" id="edit-tag-updateGra" class="validate alert-tag-updateGra" autocomplete="off"><span class="errMsg input-errMsg"></span><span id="select-icon"></span>'
							+'<ul class="show-select-option" style="display:none"></ul>'					
						+'</div>'
						+'<div class="tag-updateSpan-div"><span class="tag-updateSpan-star">*</span><label for="alert-tag-updateSpan" class="tag-updateSpan-label">更新跨度：</label><input type="text" class="validate alert-tag-updateSpan" id="edit-tag-updateSpan" name="alert-tag-updateSpan" autocomplete="off" placeholder="数值型（单位：月）"/><span class="errMsg input-errMsg"></span></div>'
						+'<div class="alert-btn-group"><button class="alert-btn-ok" id="add-integration-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
					+'</div></div>'
				+'</div>';	
				/*显示弹出框并带回原有的值*/
				$.myAlert.popUp(str);
				$(".alert-tag-sort").select2({});
			}
		});
		/*$(".alert-tag-sort").val(belongChilds).trigger("change"); */
	}

});
$(document).on("click","#add-integration-ok",function(){
	/*参数，包括：tagName,tagDesc,updateSpan,updateGranularity,childs,tagIds等*/
	addIntegrationTag();
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



//搜索框keyup，focus显示模糊标签
$('#search').on("keyup focus",function(){
	var  searchWord=$(this).val().trim();
	$(".show-blur-tag").hide().empty();
	$.AutoComplete.Auto(searchWord,"search/viewHint");
});

//选择有效或者所有标签时      搜索框 搜索调用不同的接口
$("#submit-search").on("click",function(){
	var chooseStat=$("#chooseStat").val();
	var searchWord=$('#search').val().trim();
	var re=/^[A-Za-z0-9-]{10,100}$/.test(searchWord);
	if(chooseStat=="0"&&(!re)){
		$(this).parents("form").attr("action","search/viewEffectiveSearch");
	}else if(chooseStat=="1"&&(!re)){
		$(this).parents("form").attr("action","search/viewAllSearch");
	}else if(chooseStat=="0"&&(re)){
		$(this).parents("form").attr("action","search/viewDeviceEffectiveSearch");
	}
	else if(chooseStat=="1"&&(re)){
		$(this).parents("form").attr("action","search/viewDeviceAllSearch");
	}
	
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
	clickParentClass(parentId,target,"tagIntegration/getEffectiveTagIntegrationsFromParentClassify");
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
		clickChildClass(childId,target,"tagIntegration/getEffectiveTagIntegrationsFromChildClassify");
	}else{
		clickChildClass(childId,target,"tagIntegration/getAllTagIntegrationsFromChildClassify");
	}	
});

//显示有效或者所有标签，0表示仅显有效，1表示显示所有标签
$(".choose").change(function(){
	var chooseStat=$("input[name='viewChoose']:checked").val();
	$("#chooseStat").val(chooseStat);
	childId=$("#current-child-class").val();
	target=$(".child-class-item[data-childid="+childId+"]");
	if(chooseStat=="0"){
		clickChildClass(childId,target,"tagIntegration/getEffectiveTagIntegrationsFromChildClassify");
	}else{
		clickChildClass(childId,target,"tagIntegration/getAllTagIntegrationsFromChildClassify");
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
	target.addClass("parent-class-active");
	$(".parent-class-item[data-parentid='"+parentId+"']").find(".parent-class-icon").css("background-image","url('./css/img/parent-class-active-icon.png')");
	$(".parent-class-item[data-parentid!='"+parentId+"']").removeClass("parent-class-active");
	$(".parent-class-item[data-parentid!='"+parentId+"']").find(".parent-class-icon").css("background-image","url('./css/img/parent-class-icon.png')");
	var parentName=target.attr("data-parentname");
	var parentDesc=target.attr("data-parentDesc");
	$(".parent-classifies-name").text(">"+parentName);
	$("#current-parent-class").val(parentId);
	/*$(".current-parent-class").val(parentId);*/
	var template = $('#show-tag-template').html();
	Mustache.parse(template);

	$("#parent-classifies-id").text(parentId);
	$("#parent-classifies-desc").text(parentDesc);
	
	
	$(".tag-view-wrapper").show();
	$(".child-class-view").hide();
	$(".tag-view").html('<div class="loding-div"><span class="loading"></span><div>');
	$.ajax({
		type:"POST",
		url:"tagIntegration/getEffectiveTagIntegrationsFromParentClassify",
		data:{parentId:parentId},
		dataType:"text",
		success:function(data){			
			var array=data.split("%%");
			var secondClass=array[0];
			var tagStat=array[1];
			var childStat=array[2];
			var childStr="";
			var tagStr="";
			
			$(".child-class-stat").text(childStat);
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
						$(".child-class-stat").text(childStat);
						$("#current-child-class").val(elem.classifyId);
					}
					childStr+='<li class="child-class-item" data-parentid="'+parentId+'" data-parentname="'+parentName+'" data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'" data-childdesc="'+elem.classifyDescription+'"><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
				});
				$(".child-classifies ul").empty().append(childStr);
				$(".child-class-item:eq(0)").addClass("child-class-active");
				
				$(".child-class-view").show();
				//渲染第一个二级分类的标签视图
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
							tagStat[i].updateGranularity="日";
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
function clickChildClass(childId,target,url){
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
	$.ajax({
		type:"POST",
		url:url,
		data:{childId:childId},
		dataType:"text",
		success:function(data){
			var array=data.split("%%");
			var tagStat=array[0];
			var childStat=array[1];
			$(".child-class-stat").text(childStat);
			if(!eval("("+tagStat+")").length){				
				$(".tag-view").empty().append(tagStr);	
				$(".no-tag-hint").show();
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
							tagStat[i].updateGranularity="日";
						}else if(tagStat[i].updateGranularity=="W"){
							tagStat[i].updateGranularity="周";
						}else if(tagStat[i].updateGranularity=="M"){
							tagStat[i].updateGranularity="月";
						}
						tagStr+=Mustache.render(template,tagStat[i]);
		            };
				$(".tag-view").empty().append(tagStr);
			}	
			resizeLeft();
			resizeSplit();
		}						
	});
	
}

function addIntegrationTag(){
	var $tagName=$("#edit-tag-name"),$tagDesc=$("#edit-tag-desc"),$tagUpGra=$("#edit-tag-updateGra"),$tagUpSpan=$("#edit-tag-updateSpan"),$childIdStr=$("#alert-tag-sort");
	var tagName=$tagName.val(),tagDesc=$tagDesc.val(),tagUpGra=$tagUpGra.val(),tagUpSpan=$tagUpSpan.val(),tagIds=$("#tag-ids").val();
	var childIds=$(".alert-tag-sort").select2('val');
	var childIdStr="";
	for(var i=0,len=childIds.length;i<len;i++){
		childIdStr+=childIds[i]+"*";
	}
	if(tagUpGra=="日"){
		tagUpGra="D";
	}else if(tagUpGra=="周"){
		tagUpGra="W";
	}else if(tagUpGra=="月"){
		tagUpGra="M";
	}
	var nameFlag=1,descFlag=1;
	/*标签验证*/
	
		if(isNotEmpty($tagName,tagName)&&isUnique($tagName,{tagName:tagName},"tagAdmin/tagNameVarify")){
			nameFlag=1;
		}else{
			nameFlag=0;
		};	
	
		if(isNotEmpty($tagDesc,tagDesc)&&isNotEmpty($childIdStr,childIdStr)&&isNotEmpty($tagUpGra,tagUpGra)&&isNotEmpty($tagUpSpan,tagUpSpan)){
			descFlag=1;
		}else{
			descFlag=0;
		};
		/*参数，包括：tagName,tagDescription,updateSpan,updateGranularity,childIds,tagIds等*/
	if(nameFlag&&descFlag){
		$.ajax({
			type:"POST",
			url:"tagIntegration/addTagIntegration",
			data:{tagName:tagName,tagDescription:tagDesc,updateGranularity:tagUpGra,updateSpan:tagUpSpan,childIds:childIdStr,tagIds:tagIds},
			dataType:"text",
			success:function(data){
				$("#mask,#alert").remove();
				window.location.reload();
				}
			});
	}
}

//验证名称长度
function wordLength(elem,word,len){
	if(word.trim().length>len){
		elem.next().text("最大长度为"+len+"！");
		$(".alert-btn-ok").attr("disabled",true);
		return false;
	}else{
		elem.next().text("");
		$(".alert-btn-ok").attr("disabled",false);
		return true;
	}
}
function isNotEmpty(elem,word){
	if(!word.trim().length){
		elem.next().text("不能为空!");
		$(".alert-btn-ok").attr("disabled",true);
		return false;
	}else{
		elem.next().text("");
		$(".alert-btn-ok").attr("disabled",false);
		return true;
	}
}

function isUnique(elem,data,url){
	flag=true;
	$.ajax({
		type:"POST",
		url:url,
		data:data,
		dataType:"text",
		async: false,
		success:function(returnData){
			if(returnData=="false"){
				elem.next().text("名称已存在！");
				$(".alert-btn-ok").attr("disabled",true);
				flag=false;
			}
		}
	});
	return flag;
}
function isNumber(elem,data){
	var result=/^[1-9]+/.test(data);
	if(!result){
		elem.next().text("请输入正整数!");
		$(".alert-btn-ok").attr("disabled",true);
		return false;
	}else{
		elem.next().text("");
		$(".alert-btn-ok").attr("disabled",false);
		return true;
	}
}

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
//调节二级分类和标签之间分割线的长度
function resizeSplit(){
	var leftHeight=$(".child-class-view").height();
	var rightHeight=$(".tag-view").height();
	$(".child-tag-split").height((leftHeight>rightHeight)?leftHeight:rightHeight);
};


