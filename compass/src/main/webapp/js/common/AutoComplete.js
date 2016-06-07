//搜索框keyup，focus显示模糊标签


(function(){
	$.AutoComplete ={
			Auto:function(searchWord,url){
				if(searchWord.trim()!=''){
				    $.ajax({
				    	type:"POST",
						url:url,
						data:{inputInfo:searchWord},
						dataType:"text",
						success: function(data){
					    	var res=eval("("+data+")");
					    	if(res.length){  //判断josn个数		    		
						        $.each(res,function(index,tem){
						        	$("<li></li>").text(tem).appendTo($(".show-blur-tag"))						        	
						        	.mouseover(function(){       		                     
						        			$(this).css("background", "#18b0c3");
						                })
						            .mouseout(function() {
						                   $(this).css("background", "#fff");
						                })
						            .click(function() {
						                    
						                     $(".show-blur-tag").hide();
						                     $("#search").val(tem).focus();
						                 });
						        });
						        $(".show-blur-tag").show(); //按下触发div显示   
					    	}
						}
				    });
				}
				//点击空白处 隐藏下拉列表
				$("body").click(function(evt) {
				    if (!evt.target.id) {
				      $(".show-blur-tag").hide();
				      return;
				    }
				});	
			}

	};
})();

