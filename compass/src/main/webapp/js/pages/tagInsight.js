$(function(){
	//$("#top-tag-view span").addClass("top-nav-active");	
	var radio=$("input[name='tag']");
	for(i=0;i<radio.length;i++)
	   {
		if(radio[i].checked) radio[i].checked=false; ;
		}
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
});
//点击一级分类
$(document).on('click', '.push1', function () {  
	 var child=$(this).parent().parent().find(".child-item");
	    if(child.css("display")=="none") {
	    	$(".child-item").css("display","none");
	    	child.css("display","block");
	            }
	    else child.css("display","none");
});  

//点击二级分类
$(document).on('click', '.push2', function () {  
	  var tag=$(this).parent().find(".tag-ul");
  if(tag.css("display")=="none") tag.css("display","block");
  else tag.css("display","none");
});

//点击导出按钮
$(document).on('click', '#export-submit', function () {  
	  //传递当前tag name路径
	  $("#exp-tagpath").val($(".taglist").text());
	  $("#exp-num").val($(".popular").text());
	  //传递当前列的标签名称,用户数以及比例
	 var tags=$("#parent-"+$("#currentstep").val()+" input[name='tag']");
	 var temp="";
	 for(var i=0;i<tags.length;i++) temp=temp+tags[i].value+"&&"; 
	  $("#exp-tags").val(temp);
	  var  str='<div class="alertCon">'
			+'<div class="alert-header"><span class="alert-header-left">导出洞察信息</span><span class="alert-header-right" id="close"></span></div>'
			+'<div class="alert-content">'
				+'<div class="alert-filename"><span class="name-star">*</span><label for="alert-class-name" class="alert-name-label">自定义人群名称：</label><input type="text" class="validate alert-class-name" id="filename" name="filename" value="'+$(".taglist").text()+'" maxlength="12"/></div>'
				+'<div class="alert-msg-div1"><span class="desc-star">*</span><label for="alert-class-desc" class="alert-desc-label">当前筛选结果： 筛选路径  '+$(".taglist").text()+'</label></div>'		
				+'<div class="alert-msg-div2"><span class="alert-num">覆盖用户  '+$(".popular").text()+'</span></div>'
				+'<div class="alert-check"><label><input id="need" type="checkbox" value="" checked="checked"/>导出筛选路径下未筛选标签信息</label></div>'
				+'<div class="alert-btn-group"><button class="alert-btn-ok" id="add-child-ok">确定</button><button class="cancel alert-btn-cancel" id="alert-btn-cancel">取消</button></div>'
			+'</div>'
		+'</div>';			
     $.myAlert.popUp(str);
     $(document).on("click","#add-child-ok",function(){
    if($("#filename").val()=="") alert("请输入导出文件名!");
    else
    {
     if($("#need").is(':checked'))  $("#exp-need").val("yes");
     else $("#exp-need").val("no");
    	   $("#exp-filename").val($("#filename").val());
    	   $("#export").submit();
    	   $("#alert-btn-cancel").click();
    }
                                                      });
});


$(document).on('change', "input[name='tag']", function () {  
	var tag=$("input[name='tag']:checked").val().split("-");
	var tagid=tag[0];
	var tagname=tag[1];
	var tagpopular=tag[2];
	var tagrate=tag[3];
	//设置当前用户数
	$(".popular").text(tagpopular);
	var step=parseInt($("input[name='tag']:checked").attr("step"));
	//设置tag name路径
	var temp=$(".taglist").text();
	var tagname_list=temp.split("->");
	var tagnames="";
	for(var i=0;i<step-1;i++) tagnames=tagnames+tagname_list[i]+"->";
	tagnames+=tagname;
	$(".taglist").text(tagnames);
	 //设置tag id路径
	var step1=step+1;
	var tagid_list=$("#currenttag").val().split("%%");
	var tagpath="";
	for(var i=0;i<step-1;i++) tagpath=tagpath+tagid_list[i]+"%%";
    tagpath=tagpath+tagid;
    $("#currenttag").val(tagpath);                                 
    $.ajax({
		type:"POST",
		url:"tagInsight/Insight",
		async: false,
		data:{tags:tagpath},
		dataType:"text",
		beforeSend:function(){
			$(".load").css("display","block");
		},
		success:function(data){
			$(".load").css("display","none");
			$("#loading").text("");
			$('.datagrid-mask-msg').remove(); 
			var array=data.split("%%");
			var parents=array[0];
			parents=eval("("+parents+")");
			var classify_relation=array[1];
			classify_relation=eval("("+classify_relation+")");
			var tag_classify=array[2];
			tag_classify=eval("("+tag_classify+")");
			var tag_population=array[3];
			tag_population=eval("("+tag_population+")");
			var child_population=array[4];
			child_population=eval("("+child_population+")");
			var parent_population=array[5];
			parent_population=eval("("+parent_population+")");
			var str='<div class="line"  id="line-'+step1+'"></div><div class="parent" id="parent-'+step1+'">';
			for(var parent in parents) 
			{
				parentid=parents[parent].classifyId; 
				str+='<div class="parent-item"><div class="part2"> <div class="parent-name"><span class="p-name">'+parents[parent].classifyName+
				'</span> <span class="t-name"></span><img class="push1" src="css/img/push.png"> </div>';
			    str+='<div class="child-item"><ul class="child-ul">';
				var childList = classify_relation[parentid];
				for(var child in childList) 
				{
					childid=childList[child].classifyId;
					str+='<li class="child-li"> <img class="push2" src="css/img/push1.png">'+childList[child].classifyName+'&nbsp;'+child_population[childid]+'&nbsp;&nbsp;<span class="red">'+(child_population[childid]*100/tagpopular).toFixed(2)+'%</span>';
					str+='<ul class="tag-ul" style="display:none">';
					var tagList=tag_classify[childid];
					  for(var tag in tagList) 
					  {
						  tagid=tagList[tag].tagId;
						  str+='<li class="tag-li" data-tagid="'+tagid+'">&nbsp;&nbsp;&nbsp;<input type="radio" name="tag" value="'+tagid+'-'+tagList[tag].tagName+'-'+tag_population[tagid]+'-'+(tag_population[tagid]*100/tagpopular).toFixed(2)+'%" step="'+step1+'"/>'
						  +tagList[tag].tagName+'&nbsp;'+tag_population[tagid]+'&nbsp;&nbsp;<span class="red">'+(tag_population[tagid]*100/tagpopular).toFixed(2)+'%</span></li>';
						  
					  }
					str+='</ul></li>';
					
				}
				str+='</ul></div><div class="parent-popular"><span class="parent-rate">占比：<br><label class="p-rate">'+(parent_population[parentid]*100/tagpopular).toFixed(2)+'%</label><label class="t-rate"></label></span><span class="parent-num">用户数：<br><label class="p-num">'+parent_population[parentid]+'</label><label class="t-num"></label></span></div></div><div class="arrow2"></div></div>';
			}
			str+='</div>'; 
			//删减列
			for(var i=step+1;i<=parseInt($("#currentstep").val());i++) 
			     {
				   $("#parent-"+i).remove();
				   $("#line-"+i).remove();
			      }
			//恢复列颜色和箭头
			var checkparent= $("#parent-"+step);
			checkparent.find(".parent-name").css("background-color","#36B1ED");
			checkparent.find(".parent-popular").css("background-color","#5BBEF0");
			checkparent.find(".t-name").html("");
			checkparent.find(".t-rate").html("");
			checkparent.find(".t-num").html("");
			checkparent.find(".p-rate").css("display","block");
			checkparent.find(".p-num").css("display","block");
			checkparent.find(".arrow2").css("display","none");
			//改变选择块颜色
			var parentitem=$("input[name='tag']:checked").parent().parent().parent().parent().parent().parent().parent();
			var part2=parentitem.find(".part2");
			var arrow=parentitem.find(".arrow2");
			part2.find(".parent-name").css("background-color","#1BA3AD");
			part2.find(".parent-popular").css("background-color","#1DB5BE");	
			part2.find(".t-name").html('-'+tagname);
			part2.find(".p-rate").css("display","none");
			part2.find(".p-num").css("display","none");
			part2.find(".t-rate").html(tagrate);
			part2.find(".t-num").html(tagpopular);
			arrow.css("display","block");
			//收起所有下拉菜单
			$(".child-item").css("display","none");
			$(".content").append(str);
			$("#currentstep").val(step1);
		                       }       
	        });
});