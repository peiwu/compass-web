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
	var currentParentId=$("#current-parent-class").val();
	clickParentClass(currentParentId);		
	
	//改变标签表格样式
	$(document).on("mouseover",".tag-table-tbody tr",function(){
		$(this).css("background","#f4f2f2");
	});
	$(document).on("mouseout",".tag-table-tbody tr",function(){
		$(this).css("background","#fff");
	});
	
	//$("#top-tag-admin span").addClass("top-nav-active");	
	$(parent.frames["header"].document).find("#top-tag-view span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-search span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-integration span").removeClass("top-nav-active");
	$(parent.frames["header"].document).find("#top-tag-admin span").addClass("top-nav-active");	
});


//鼠标悬停一级分类，显示编辑和删除
$(".parent-class-item").mouseover(function(){
	var parentId=$(this).attr("data-parentid");
	  $(".edit-parent-class[data-parentid='"+parentId+"'],.remove-parent-class[data-parentid='"+parentId+"']").css("display","inline-block");
}).mouseout(function(){
		$(".edit-parent-class,.remove-parent-class").css("display","none");
});
$(".parent-hint-icon").mouseover(function(){
	  $(".parent-hint-div").css("display","inline-block");
}).mouseout(function(){
	$(".parent-hint-div").css("display","none");
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
//新建一级分类
$(document).on("click","#add-parent-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var  str='<div class="alertCon">'
			+'<div class="alert-header"><span class="alert-header-left">新增一级分类</span><span class="alert-header-right" id="close"></span></div>'
			+'<div class="alert-content"><div class="add-class-form">'
				+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-class-name" class="alert-name-label">分类名称：</label><input type="text" class="validate alert-class-name" id="add-parent-name" name="alert-class-name" placeholder="请填写详细的分类名称"/><span class="errMsg input-errMsg"></span></div>'
				+'<div class="alert-desc-div"><span class="desc-star">*</span><label for="alert-class-desc" class="alert-desc-label">分类描述：</label><textarea type="text" class="validate alert-class-desc" id="add-parent-desc" name="alert-class-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg textarea-errMsg"></span></div>'				
				+'<div class="alert-btn-group"><button class="alert-btn-ok" id="add-parent-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
			+'</div></div>'
		+'</div>';			
		$.myAlert.popUp(str);
		$(document).on("click","#add-parent-ok",function(){
			addParentClass();
		});
	}
});

//编辑一级分类
$(document).on("click",".edit-parent-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		var parentName=$(this).attr("data-parentname");
		var parentDesc=$(this).attr("data-parentdesc");
		var  str='<div class="alertCon">'
					+'<div class="alert-header"><span class="alert-header-left">编辑一级分类</span><span class="alert-header-right" id="close"></span></div>'
					+'<div class="alert-content"><div class="add-class-form">'
						+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-class-name" class="alert-name-label">分类名称：</label><input type="text" class="validate alert-class-name" id="edit-parent-name" name="alert-class-name" placeholder="请填写详细的分类名称"/><span class="errMsg input-errMsg"></span></div>'
						+'<div class="alert-desc-div"><span class="desc-star">*</span><label for="alert-class-desc" class="alert-desc-label">分类描述：</label><textarea type="text" class="validate alert-class-desc" id="edit-parent-desc" name="alert-class-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg textarea-errMsg"></span></div>'				
						+'<div class="alert-btn-group"><button class="alert-btn-ok" id="edit-parent-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
					+'</div></div>'
				+'</div>';	

		$.myAlert.popUp(str);
		$("#edit-parent-name").val(parentName);
		$("#edit-parent-desc").val(parentDesc);
		$(document).on("click","#edit-parent-ok",function(){
			editParentClass(parentId,parentName);
		});
	}
});
//删除一级分类
$(document).on("click",".remove-parent-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		removeParentClass(parentId);
	}
});



//显示二级分类下的标签
$(document).on("click",".child-class-item",function(){
	var childId=$(this).data("childid");
	var pageIndex=1;
	var pageSize=10;
	clickChildClass(childId,pageIndex,pageSize);
});
//新建二级分类
$(document).on("click","#add-child-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		var  str='<div class="alertCon">'
					+'<div class="alert-header"><span class="alert-header-left">新增二级分类</span><span class="alert-header-right" id="close"></span></div>'
					+'<div class="alert-content"><div class="add-class-form">'
						+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-class-name" class="alert-name-label">分类名称：</label><input type="text" class="validate alert-class-name" id="add-child-name" name="alert-class-name" placeholder="请填写详细的分类名称" maxlength="12"/><span class="errMsg input-errMsg"></span></div>'
						+'<div class="alert-desc-div"><span class="desc-star">*</span><label for="alert-class-desc" class="alert-desc-label">分类描述：</label><textarea type="text" class="validate alert-class-desc" id="add-child-desc" name="alert-class-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg textarea-errMsg"></span></div>'				
						+'<div class="alert-btn-group"><button class="alert-btn-ok" id="add-child-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
					+'</div></div>'
				+'</div>';			
		$.myAlert.popUp(str);
		$(document).on("click","#add-child-ok",function(){
			addChildClass(parentId);
		});
	}
});


//编辑二级分类
$(document).on("click","#edit-child-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		var childId=$(this).attr("data-childid");
		var childName=$(this).attr("data-childname");
		var childDesc=$(this).attr("data-childdesc");
		var  str='<div class="alertCon">'
					+'<input id="edit-child-info" type="hidden" data-parentid="'+parentId+'" data-childid="'+childId+'" data-oldname="'+childName+'"/>'
					+'<div class="alert-header"><span class="alert-header-left">编辑二级分类</span><span class="alert-header-right" id="close"></span></div>'
					+'<div class="alert-content"><div class="add-class-form">'
						+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-class-name" class="alert-name-label">分类名称：</label><input type="text" class="validate alert-class-name" id="edit-child-name" name="alert-class-name" placeholder="请填写详细的分类名称"/><span class="errMsg input-errMsg"></span></div>'
						+'<div class="alert-desc-div"><span class="desc-star">*</span><label for="alert-class-desc" class="alert-desc-label">分类描述：</label><textarea type="text" class="validate alert-class-desc" id="edit-child-desc" name="alert-class-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg textarea-errMsg"></span></div>'				
						+'<div class="alert-btn-group"><button class="alert-btn-ok" id="edit-child-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
					+'</div></div>'
				+'</div>';			
		$.myAlert.popUp(str);
		$("#edit-child-name").val(childName);
		$("#edit-child-desc").val(childDesc);
	}
});	
$(document).on("click","#edit-child-ok",function(){
	editChildClass();
});
//删除二级分类
$(document).on("click",".remove-child-class",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		var childId=$(this).attr("data-childid");
		removeChildClass(parentId,childId);
	}
});	


//新建标签
$(document).on("click","#add-tag",function(){	
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var  str='<div class="alert-add-tag">'
			+'<div class="alert-header"><span class="alert-header-left">新增标签</span><span class="alert-header-right" id="close"></span></div>'
			+'<div class="content add-tag-content"><div class="add-tag-form">'
				+'<div class="alert-name-div"><span class="name-star">*</span><label for="alert-tag-name" class="alert-name-label">标签名称：</label><input type="text" class=" validate alert-tag-name" id="add-tag-name" name="alert-tag-name" placeholder="请填写详细的标签名称"/><span class="errMsg input-name-errMsg"></span></div>'
				+'<div class="tag-desc-div"><span class="tag-desc-star">*</span><label for="alert-tag-desc" class="tag-desc-label">标签描述：</label><textarea type="text" class=" validate alert-tag-desc" id="add-tag-desc" name="alert-tag-desc" placeholder="请简述分类含义和生成策略"></textarea><span class="errMsg tag-textarea-errMsg"></span></div>'	
				+'<div class="tag-updateGra-div"><span class="tag-updateGra-star">*</span><label for="alert-tag-updateGra" class="tag-updateGra-label">更新粒度：</label>'					
					+'<input type="text" id="add-tag-updateGra" class="validate alert-tag-updateGra" autocomplete="off" placeholder="请选择"/><span class="errMsg input-errMsg"></span><span id="select-icon"></span>'
					+'<ul class="show-select-option" style="display:none"></ul>'					
				+'</div>'
				+'<div class="tag-updateSpan-div"><span class="tag-updateSpan-star">*</span><label for="alert-tag-updateSpan" class="tag-updateSpan-label">更新跨度：</label><input type="text" class=" validate alert-tag-updateSpan" id="add-tag-updateSpan" name="alert-tag-updateSpan" placeholder="数值型（单位：月）"/><span class="errMsg input-updateUpan-errMsg"></span></div>'
				+'<div class="alert-btn-group"><button class="alert-btn-ok" id="add-tag-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
			+'</div></div>'
		+'</div>';			
		$.myAlert.popUp(str);
	}
});
$(document).on("click","#add-tag-ok",function(){
	var childId=$("#add-tag").attr("data-childid");
	addTag(childId);
});



//编辑标签
$(document).on("click",".edit-tag",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var allChildClassStr ="";
		var childId=$(this).attr("data-childid");
		var tagId=$(this).attr("data-tagid");
		var oldTagName=$(this).attr("data-tagname");
		var tagDesc=$(this).attr("data-tagdesc");
		var tagUpdateGra=$(this).attr("data-updategra");
		var tagUpdateSpan=$(this).attr("data-updatespan");
		var belongChilds=[];
		var allChildClassStr="";
		if(tagUpdateGra=="D"){
			tagUpdateGra="日";
		}else if(tagUpdateGra=="W"){
			tagUpdateGra="周";
		}else if(tagUpdateGra=="M"){
			tagUpdateGra="月";
		}
		//获得当前标签所属的二级分类
		$.ajax({
			type:"POST",
			url:"tagAdmin/getOriginalChildClassifies",
			data:{tagId:tagId},
			dataType:"text",
			async: false,
			success:function(data){
				$.each(eval("("+data+")"),function(index,elem){
					belongChilds.push(elem.classifyId);
				});
			}
		});
		//获得所有二级分类
		$.ajax({
			type:"POST",
			url:"tagAdmin/getAllChildClassifies",
			dataType:"text",
			async: false,
			success:function(data){
				$.each(eval("("+data+")"),function(index,elem){
					allChildClassStr+='<option class="all-child-item" value="'+elem.classifyId+'">'+elem.classifyName+'</option>';
				});
			}
		});
		
		var  str='<div class="alert-edit-tag">'
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
						+'<div class="alert-btn-group"><button class="alert-btn-ok" id="edit-tag-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
					+'</div></div>'
				+'</div>';	
		/*显示弹出框并带回原有的值*/
		$.myAlert.popUp(str);
		$("#edit-tag-name").val(oldTagName);
		$("#edit-tag-desc").val(tagDesc);
		$("#edit-tag-updateGra").val(tagUpdateGra);
		$("#edit-tag-updateSpan").val(tagUpdateSpan);
		$(".alert-tag-sort").select2({});
		$(".alert-tag-sort").val(belongChilds).trigger("change"); 


		$(document).on("click","#edit-tag-ok",function(){
			var childIds=$(".alert-tag-sort").select2('val');
			var childIdStr="";
			for(var i=0,len=childIds.length;i<len;i++){
				childIdStr+=childIds[i]+"*";
			}
			editTag(childId,childIdStr,tagId,oldTagName);
		});
	}
});
//删除标签
$(document).on("click",".remove-tag",function(){
	var authority = $("#authority").val();;
	if(authority==2||authority==3){
		alert("您没有权限执行此操作！");
	}
	else{
		var tagId=$(this).attr("data-tagid");
		var childId=$(this).attr("data-childid");
		removeTag(childId,tagId);
	}
	
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
	
//	var contLeftEle=document.getElementById("content-left");
//	contLeftEle.style.height="100px";
	
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
			
			$("#edit-parent-class").attr({"data-parentid":parentId});
			$("#remove-parent-class").attr({"data-parentid":parentId});
			
			//渲染第一个二级分类标签
			$.each(eval("("+tags+")"),function(index,elem){
				if(elem.updateGranularity=="D"){
					elem.updateGranularity="日";
				}else if(elem.updateGranularity=="W"){
					elem.updateGranularity="周";
				}else if(elem.updateGranularity=="M"){
					elem.updateGranularity="月";
				}
				tagstr+='<tr><td class="tag-name">'+elem.tagName+'</td><td class="tag-id">'+elem.tagId+'</td><td class="tag-desc">'+elem.tagDescription+'</td>'
					+'<td class="tag-update-gra">'+elem.updateGranularity+'</td><td class="tag-update-span">'+elem.updateSpan+'</td>'					
					+'<td><li class="operator-item edit-tag" data-tagid="'+elem.tagId+'" data-tagname="'+elem.tagName+'" data-tagdesc="'+elem.tagDescription+'"data-updategra="'+elem.updateGranularity+'"data-updateSpan="'+elem.updateSpan+'" ><a href="javascript:void(null)"></a></li>'
					+'<li class="operator-item remove-tag" data-tagid="'+elem.tagId+'" data-parentid="'+parentId+'" data-parentname="'+name+'"><a href="javascript:void(null)"></a></li></td></tr>';												
			});
			$(".tag-table tbody").empty().append(tagstr);
			
			//渲染二级分类
			if(!eval("("+secondClass+")").length){				
				$(".child-class-name").text("");
				$(".child-class-id").text("");
				$(".child-class-desc").text("");
				$(".render-result").hide();
				$(".no-child-class-hint").show();
			}else{
				$(".render-result").show();
				$(".no-child-class-hint").hide();
				$.each(eval("("+secondClass+")"),function(index,elem){
					if(index=="0"){
						$("#current-child-class").val(elem.classifyId);
						$(".child-class-name").text(elem.classifyName);
						$(".child-class-desc").text(elem.classifyDescription);
						$(".child-class-id").text(elem.classifyId);
						$("#edit-child-class").attr({"data-childid":elem.classifyId,"data-parentid":parentId,"data-childname":elem.classifyName,"data-childdesc":elem.classifyDescription});
						$("#remove-child-class").attr({"data-childid":elem.classifyId,"data-parentid":parentId});
						$("#add-tag").attr({"data-childid":elem.classifyId});
						$(".edit-tag").attr({"data-childid":elem.classifyId});
						$(".remove-tag").attr({"data-childid":elem.classifyId});
						childstr+='<li class="child-class-item" data-parentid="'+parentId+'"  data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'" data-childdesc="'+elem.classifyDescription+'" ><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
					}else{
						childstr+='<li class="child-class-item" data-parentid="'+parentId+'"  data-childid="'+elem.classifyId+'" data-childname="'+elem.classifyName+'" data-childdesc="'+elem.classifyDescription+'" ><a href="javascript:void(null)">&nbsp;'+elem.classifyName+'</a></li>';
					}	
				});
			}			
			childstr+='<li class="add-child-class" id="add-child-class" data-parentid="'+parentId+'"><a  href="javascript:void(null)">二级分类</a></li>';						
			$(".child-classifies ul").empty().append(childstr);
			$(".child-class-item:eq(0)").addClass("child-class-active");
			
			
//			var  bodyHeight=$(document).height();
//			var contLeftHeight=bodyHeight-160;
//			contLeftEle.style.height=contLeftHeight+"px";
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
	var childName,childDesc,parentId;
	$(".child-class-item").each(function(){
		if($(this).attr("data-childid")==childId){
			childName=$(this).attr("data-childname");
			childDesc=$(this).attr("data-childdesc");
			parentId=$(this).attr("data-parentid");
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
			/*$("#pagination-left").text("共"+tagNum+"个标签，每页显示10条信息"); */
			$(".total-page").text(totalPageNum);
			var tagstr="";
			$(".child-class-name").text(childName);
			$(".child-class-id").text(childId);
			$(".child-class-desc").text(childDesc);
			$("#edit-child-class").attr({"data-parentid":parentId,"data-childid":childId,"data-childname":childName,"data-childdesc":childDesc});
			$("#remove-child-class").attr({"data-parentid":parentId,"data-childid":childId});
			$("#add-tag").attr({"data-childid":childId});
			var tagstr="";
			$.each(eval("("+tags+")"),function(index,elem){ 
				if(elem.updateGranularity=="D"){
					elem.updateGranularity="日";
				}else if(elem.updateGranularity=="W"){
					elem.updateGranularity="周";
				}else if(elem.updateGranularity=="M"){
					elem.updateGranularity="月";
				}
				tagstr+='<tr><td class="tag-name">'+elem.tagName+'</td><td class="tag-id">'+elem.tagId+'</td><td class="tag-desc">'+elem.tagDescription+'</td>'
				+'<td class="tag-update-gra">'+elem.updateGranularity+'</td><td class="tag-update-span">'+elem.updateSpan+'</td>'					
				+'<td><li class="operator-item edit-tag" data-tagid="'+elem.tagId+'" data-tagname="'+elem.tagName+'" data-tagdesc="'+elem.tagDescription+'"data-updategra="'+elem.updateGranularity+'"data-updateSpan="'+elem.updateSpan+'" data-childid="'+childId+'"><a href="javascript:void(null)"></a></li>'
				+'<li class="operator-item remove-tag" data-tagid="'+elem.tagId+'" data-parentid="'+parentId+'" data-parentname="'+name+'" data-childid="'+childId+'"><a href="javascript:void(null)"></a></li></td></tr>';												
			});
			$(".tag-table tbody").empty().append(tagstr);
//			var  bodyHeight=$(document).height();
//			var contLeftHeight=bodyHeight-160;
//			contLeftEle.style.height=contLeftHeight+"px";
			resizeLeft();
		}
	});
}

//创建一级分类
function addParentClass(){
	var $parentName=$("#add-parent-name"),$parentDesc=$("#add-parent-desc");
	var parentName= $parentName.val().replace(/\s+/g,""),parentDesc=$parentDesc.val();	
	var nameFlag=1,descFlag=1;
	/*一级分类名称验证*/
		if(isNotEmpty($parentName,parentName)&&wordLength($parentName,parentName,10)&&isUnique($parentName,{parentName:parentName},"tagAdmin/parentNameVarify")){
			nameFlag=1;
		}else{
			nameFlag=0;
		};
		
		if(isNotEmpty($parentDesc,parentDesc)){
			descFlag=1;
		}else{
			descFlag=0;
		};		

if(nameFlag&&descFlag){
	$.ajax({
		type:"POST",
		url:"tagAdmin/addParentClassify",
		data:{parentName:parentName,parentDescription:parentDesc},
		dataType:"text",
		success:function(data){
			
			$("#mask,#alert").remove();
			$("#currentParentId").val(data);
			//重新加载页面
			$("#refresh").submit();
		}
	});

}
	
	
}
//创建二级分类
function addChildClass(parentId){		
		var $childName=$("#add-child-name"),$childDesc=$("#add-child-desc");
		var childName= $childName.val().replace(/\s+/g,""),childDesc=$childDesc.val();
		var nameFlag=1,descFlag=1;
		/*二级分类验证*/
			if(isNotEmpty($childName,childName)&&wordLength($childName,childName,20)&&isUnique($childName,{childName:childName},"tagAdmin/childNameVarify")){
				nameFlag=1;
			}else{
				nameFlag=0;
			};
			
			if(isNotEmpty($childDesc,childDesc)){
				descFlag=1;
			}else{
				descFlag=0;
			};
		if(nameFlag&&descFlag){
			$.ajax({
				type:"POST",
				url:"tagAdmin/addChildClassify",
				data:{childName:childName,childDescription:childDesc,parentId:parentId},
				dataType:"text",
				success:function(data){
					$("#mask,#alert").remove();
					//刷新所属一级分类的页面
					clickParentClass(parentId);
					clickChildClass(data,1,10);
				}
			});
		
		}
}
//添加新标签
function addTag(childId){	
	var $tagName=$("#add-tag-name"),$tagDesc=$("#add-tag-desc"),$tagUpgra=$("#add-tag-updateGra"),$tagUpspan=$("#add-tag-updateSpan");
	var tagName=$tagName.val().replace(/\s+/g,""),tagDesc=$tagDesc.val(),tagUpgra=$tagUpgra.val(),tagUpspan=$tagUpspan.val();
	var nameFlag=1,descFlag=1,totalTagNum=$(".total-tag-num").text(),curPageNum=$(".current-page-num").text();
	if(totalTagNum==0){
		pageIndex=1;
	}else if(totalTagNum%10==0){
		pageIndex=Number(curPageNum)+Number(1);
	}else{
		pageIndex=curPageNum;
	}
	/*标签验证*/
    if(isNotEmpty($tagName,tagName)&&isUnique($tagName,{tagName:tagName},"tagAdmin/tagNameVarify")){
	//if(isNotEmpty($tagName,tagName)){
			nameFlag=1;
		}else{
			nameFlag=0;
		};
		
		if(isNotEmpty($tagDesc,tagDesc)&&isNotEmpty($tagUpgra,tagUpgra)&&isNotEmpty($tagUpspan,tagUpspan)&&isNumber($tagUpspan,tagUpspan)){
			descFlag=1;
		}else{
			descFlag=0;
		};
	if(nameFlag&&descFlag){
		$.ajax({
			type:"POST",
			url:"tagAdmin/addTag",
			data:{tagName:tagName,tagDescription:tagDesc,updateGranularity:tagUpgra,updateSpan:tagUpspan,childId:childId},
			dataType:"text",
			success:function(data){
				$("#mask,#alert").remove();
				//刷新所属二级分类的页面
				clickChildClass(childId,pageIndex,10);
			}
		});
	}

}
//编辑一级分类
function editParentClass(parentId,parentName){

	var $modifyName=$("#edit-parent-name"),$modifyDesc=$("#edit-parent-desc");
	var modifyName= $modifyName.val().replace(/\s+/g,""),modifyDesc=$modifyDesc.val();
	var nameFlag=1,descFlag=1;
	/*一级分类名称验证*/
	if(modifyName==parentName){
		nameFlag=1;
	}else{
		if(isNotEmpty($modifyName,modifyName)&&wordLength($modifyName,modifyName,10)&&isUnique($modifyName,{parentName:modifyName},"tagAdmin/parentNameVarify")){
			nameFlag=1;
		}else{
			nameFlag=0;
		};
	}
	
		if(isNotEmpty($modifyDesc,modifyDesc)){
			descFlag=1;
		}else{
			descFlag=0;
		};
	
		if(nameFlag&&descFlag){
			$.ajax({
				type:"POST",
				url:"tagAdmin/modifyParentClassify",
				data:{parentId:parentId,parentName:modifyName,parentDescription:modifyDesc},
				dataType:"text",
				success:function(data){
					$("#currentParentId").val(parentId);
					$("#mask,#alert").remove();
					$("#refresh").submit();
				}
			});
		}
}
//编辑二级分类
function editChildClass(){
	/*参数childId,childName,childDescription,parentIds*/
	var $modifyName=$("#edit-child-name"),$modifyDesc=$("#edit-child-desc"),$hiddenInput=$("#edit-child-info");
	var modifyName= $modifyName.val().replace(/\s+/g,""),modifyDesc=$modifyDesc.val();
	var oldName=$hiddenInput.attr("data-oldname"),parentId=$hiddenInput.attr("data-parentid"),childId=$hiddenInput.attr("data-childid");
	var nameFlag=1,descFlag=1,pageIndex=$(".current-page-num").text();
	/*二级分类验证*/
	if(modifyName==oldName){
		nameFlag=1;
	}else{
		if(isNotEmpty($modifyName,modifyName)&&wordLength($modifyName,modifyName,20)&&isUnique($modifyName,{childName:modifyName},"tagAdmin/childNameVarify")){
			nameFlag=1;
		}else{
			nameFlag=0;
		};
	}
		
		if(isNotEmpty($modifyDesc,modifyDesc)){
			descFlag=1;
		}else{
			descFlag=0;
		};

		if(nameFlag&&descFlag){
			$.ajax({
				type:"POST",
				url:"tagAdmin/modifyChildClassify",
				data:{childId:childId,childName:modifyName,childDescription:modifyDesc,parentIds:parentId},
				dataType:"text",
				success:function(data){
					$("#mask,#alert").remove();
					   clickParentClass(parentId);
						clickChildClass(childId,pageIndex,10);
					}
				});	
		}	
}
//编辑标签
function editTag(childId,childIdS,tagId,oldTagName){

	var $tagName=$("#edit-tag-name"),$tagDesc=$("#edit-tag-desc"),$tagUpGra=$("#edit-tag-updateGra"),$tagUpSpan=$("#edit-tag-updateSpan"),$childIdStr=$("#alert-tag-sort");
	var tagName=$tagName.val().replace(/\s+/g,""),tagDesc=$tagDesc.val(),tagUpGra=$tagUpGra.val(),tagUpSpan=$tagUpSpan.val(),childIdStr=childIdS;
	var nameFlag=1,descFlag=1;
	/*标签验证*/
	if(tagName==oldTagName){
		nameFlag=1;
	}else{
		if(isNotEmpty($tagName,tagName)&&isUnique($tagName,{tagName:tagName},"tagAdmin/tagNameVarify")){
			nameFlag=1;
		}else{
			nameFlag=0;
		};	
	}
		if(isNotEmpty($tagDesc,tagDesc)&&isNotEmpty($childIdStr,childIdStr)&&isNotEmpty($tagUpGra,tagUpGra)&&isNotEmpty($tagUpSpan,tagUpSpan)){
			descFlag=1;
		}else{
			descFlag=0;
		};

	if(nameFlag&&descFlag){
		$.ajax({
			type:"POST",
			url:"tagAdmin/modifyTag",
			data:{tagId:tagId,tagName:tagName,tagDescription:tagDesc,updateGranularity:tagUpGra,updateSpan:tagUpSpan,childIds:childIdStr},
			dataType:"text",
			success:function(data){
				$("#mask,#alert").remove();
				clickChildClass(childId,1,10);
				}
			});
	}

}
//删除一级分类
function removeParentClass(parentId){	
	$.ajax({
		type:"POST",
		url:"tagAdmin/removeParentClassifyVarify",
		data:{parentId:parentId},
		async: false,
		dataType:"text",
		success:function(data){
			if(data=="false"){	
				var str='<div class="alert-remove not-allow-remove">'
							+'<div class="alert-header"><span class="alert-header-left">删除一级分类</span></div>'
							+'<div class="alert-remove-content"><div class="alert-remove-main"><span class="alert-remove-warn"></span><span class="alert-remove-msg">当前待删除分类已覆盖标签，不能删除！</span></div></div>'
						+'</div>';
				$.myAlert.popUp(str);				
			}else{				
				var str='<div class="alert-remove allow-remove">'
						+'<div class="alert-header"><span class="alert-header-left">删除一级分类</span><span class="alert-header-right" id="close"></span></div>'
						+'<div class="alert-remove-content"><form>'
							+'<div class="allow-remove-msg">是否删除该一级分类？</div>'
							+'<div class="allow-remove-btnGroup"><button class="alert-btn-ok remove-btn-ok" id="remove-parent-ok">确定</button><button class="cancel remove-btn-cancel" id="alert-btn-cancel">取消</button></div>'
							+'</form></div>'
						+'</div>';
				$.myAlert.popUp(str);
				$(document).on("click","#remove-parent-ok",function(){
					$.ajax({
						type:"POST",
						url:"tagAdmin/removeParentClassify",
						data:{parentId:parentId},
						async: false,
						dataType:"text",
						success:function(data){
							$("#mask,#alert").remove();
							/*$("#refresh").submit();*/
							window.location.href = window.location.href;
							}
						});
				});
				
			}
		}					
	});
}
//删除二级分类
function removeChildClass(parentId,childId){
	$.ajax({
		type:"POST",
		url:"tagAdmin/removeChildClassifyVarify",
		data:{childId:childId},
		dataType:"text",
		success:function(data){
			if(data=="false"){	
				var str='<div class="alert-remove not-allow-remove">'
							+'<div class="alert-header"><span class="alert-header-left">删除二级分类</span></div>'
							+'<div class="alert-remove-content"><div class="alert-remove-main"><span class="alert-remove-warn"></span><span class="alert-remove-msg">当前待删除分类已覆盖标签，不能删除！</span></div></div>'
						+'</div>';
				$.myAlert.popUp(str);				
			}else{
				var str='<div class="alert-remove allow-remove">'
						+'<div class="alert-header"><span class="alert-header-left">删除二级分类</span><span class="alert-header-right" id="close"></span></div>'
						+'<div class="alert-remove-content">'
							+'<div class="allow-remove-msg">是否删除该二级分类？</div>'
							+'<div class="allow-remove-btnGroup"><button class="alert-btn-ok remove-btn-ok" id="remove-child-ok">确定</button><button class="cancel remove-btn-cancel" id="alert-btn-cancel">取消</button></div>'
						+'</div>'
					+'</div>';
				$.myAlert.popUp(str);
				$(document).on("click","#remove-child-ok",function(){
					$.ajax({
						type:"POST",
						url:"tagAdmin/removeChildClassify",
						data:{childId:childId},
						dataType:"text",
						success:function(data){
							$("#mask,#alert").remove();
							clickParentClass(parentId);
							}
						});
				});
			}				
			}
		});
}
//删除标签
function removeTag(childId,tagId){	
	var str='<div class="alert-remove allow-remove">'
			+'<div class="alert-header"><span class="alert-header-left">删除标签</span><span class="alert-header-right" id="close"></span></div>'
			+'<div class="alert-remove-content">'
				+'<div class="allow-remove-msg">是否删除该标签？</div>'
				+'<div class="allow-remove-btnGroup"><button class="alert-btn-ok remove-btn-ok" id="remove-tag-ok">确定</button><button class="cancel remove-btn-cancel" id="alert-btn-cancel">取消</button></div>'
			+'</div>'
		+'</div>';
	$.myAlert.popUp(str);
	$(document).on("click","#remove-tag-ok",function(){
		$.ajax({
			type:"POST",
			url:"tagAdmin/removeTag",
			data:{tagId:tagId},
			dataType:"text",
			success:function(data){
				$("#mask,#alert").remove();
				clickChildClass(childId,1,10);
				}
			});
	});
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
	if(!word.length){
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
	/*$.ajax({
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
	});*/
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

/*String.prototype.TrimAll = function()
{
 return this.replace(/( )/g,"")
}*/