$(function(){
	//$("#top-tag-admin span").addClass("top-nav-active");
	
	var resultMap=$("#resultMap").val();
	if(resultMap=="{}"){
		$(".admin-search-content").hide();
		$(".no-result").show();
	}else{
		$(".admin-search-content").show();
		$(".no-result").hide();
	}
	
	
	/*修改显示时间样式*/
	$(".tag-update-gra").each(function(index,elem){
		var $this=$(this);
		var $thisVal=$this.text();
		if($thisVal=="D"){
			$this.text("天");
		}else if($thisVal=="W"){
			$this.text("周");
		}else if($thisVal=="M"){
			$this.text("月");
		}
	});
});

//搜索框keyup，focus显示模糊标签
$('#search').on("keyup focus",function(){
	var  searchWord=$(this).val().trim();
	$(".show-blur-tag").hide().empty();
	$.AutoComplete.Auto(searchWord,"adminHint");
});

//编辑二级分类
$(document).on("click","#edit-child-class",function(){
	var authority = $("#authority").val();;
	if(authority==2){
		alert("没有权限，请联系刘可为(kwliu@iflytek.com)");
	}
	else{
		var parentId=$(this).attr("data-parentid");
		var childId=$(this).attr("data-childid");
		var childName=$(this).attr("data-childname");
		var childDesc=$(this).attr("data-childdesc");
		var  str='<div class="alertCon">'
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
		$(document).on("click","#edit-child-ok",function(){
			var $modifyName=$("#edit-child-name"),$modifyDesc=$("#edit-child-desc");
			var modifyName= $modifyName.val().replace(/\s+/g,""),modifyDesc=$modifyDesc.val();
			var nameFlag=1,descFlag=1;
			/*二级分类验证*/
			if(modifyName==childName){
				nameFlag=1;
			}else{
				if(isNotEmpty($modifyName,modifyName)&&wordLength($modifyName,modifyName,6)&&isUnique($modifyName,{childName:modifyName},"tagAdmin/childNameVarify")){
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
						data:{childId:childId,childName:modifyName,childDescription:modifyDesc,parentIds:""},
						dataType:"text",
						success:function(data){
							$("#mask,#alert").remove();
							window.location.reload();
							}
						});	
				}	
		});
	}
	
});	

//删除二级分类
$(document).on("click",".remove-child-class",function(){
	var authority = $("#authority").val();;
	if(authority==2){
		alert("没有权限，请联系刘可为(kwliu@iflytek.com)");
	}
	else{
		var childId=$(this).attr("data-childid");
		removeChildClass(childId);
	}
});	


//编辑标签
$(document).on("click",".edit-tag",function(){
	var authority = $("#authority").val();;
	if(authority==2){
		alert("没有权限，请联系刘可为(kwliu@iflytek.com)");
	}
	else{
		var tagId=$(this).attr("data-tagid");
		var tagName=$(this).attr("data-tagname");
		var tagDesc=$(this).attr("data-tagdesc");
		var tagUpdateGra=$(this).attr("data-updategra");
		var tagUpdateSpan=$(this).attr("data-updatespan");
		var belongChilds=[];
		var allChildClassStr="";
		if(tagUpdateGra=="D"){
			tagUpdateGra="天";
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
					+'<select class="alert-tag-sort" multiple="multiple">'+allChildClassStr+'</select><span class="errMsg tag-sort-errMsg"></span>'
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
		$("#edit-tag-name").val(tagName);
		$("#edit-tag-desc").val(tagDesc);
		$("#edit-tag-updateGra").val(tagUpdateGra);
		$("#edit-tag-updateSpan").val(tagUpdateSpan);
		$(".alert-tag-sort").select2({
		});
		$(".alert-tag-sort").val(belongChilds).trigger("change"); 


		$(document).on("click","#edit-tag-ok",function(){
			var childIds=$(".alert-tag-sort").select2('val');
			var childIdStr="";
			for(var i=0,len=childIds.length;i<len;i++){
				childIdStr+=childIds[i]+"*";
			}
			/*editTag(childId,childIdStr,tagId);*/
			var tagName=$("#edit-tag-name").val().replace(/\s+/g,"");
			var tagDesc=$("#edit-tag-desc").val();
			var tagUpGra=$("#edit-tag-updateGra").val();
			var tagUpSpan=$("#edit-tag-updateSpan").val();
			$.ajax({
				type:"POST",
				url:"tagAdmin/modifyTag",
				data:{tagId:tagId,tagName:tagName,tagDescription:tagDesc,updateGranularity:tagUpGra,updateSpan:tagUpSpan,childIds:childIdStr},
				dataType:"text",
				success:function(data){
					$("#mask,#alert").remove();
					window.location.reload();
					}
				});
		});
	}
});



//删除标签
$(document).on("click",".remove-tag",function(){
	var authority = $("#authority").val();;
	if(authority==2){
		alert("没有权限，请联系刘可为(kwliu@iflytek.com)");
	}
	else{
		var tagId=$(this).attr("data-tagid");
		var childId=$(this).attr("data-childid");
		removeTag(childId,tagId);
	}
	
});






//删除二级分类
function removeChildClass(childId){
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
							window.location.reload();
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
					window.location.reload();
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