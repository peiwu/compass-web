//弹出框

(function(){
	$.myAlert ={
			popUp:function(str){
				//获取页面的高度和宽度
				var sWidth=document.body.scrollWidth;
				var sHeight=document.body.scrollHeight;
				
				//获取页面的可视区域高度和宽度
				//如果说页面是一个竖向的页面，那么可视区域的宽度和页面宽度是一样的
				var wHeight=document.documentElement.clientHeight;
				
				var oMask=document.createElement("div");
					oMask.id="mask";
					oMask.style.height=sHeight+"px";
					oMask.style.width=sWidth+"px";
					document.body.appendChild(oMask);
				var oAlert=document.createElement("div");
					oAlert.id="alert";
					oAlert.innerHTML=str;
					document.body.appendChild(oAlert);
				
				//获取登陆框的宽和高
				var dHeight=oAlert.offsetHeight;
				var dWidth=oAlert.offsetWidth;
					//设置登陆框的left和top
					oAlert.style.left=sWidth/2-dWidth/2+"px";
					oAlert.style.top=wHeight/2-dHeight/2+"px";
				//点击关闭按钮
				var oClose=document.getElementById("close");
				/*var oBtn=document.getElementsByTagName('button');*/
				var oCancel=document.getElementById("alert-btn-cancel");
				var oSelect=document.getElementById("select-icon");
					//点击登陆框以外的区域也可以关闭登陆框
				oCancel.onclick=oClose.onclick=oMask.onclick=function(){
								document.body.removeChild(oAlert);
								document.body.removeChild(oMask);
								};
								
				//input模拟单选下拉框				
				$("#select-icon,.alert-tag-updateGra").on('click',function(){
					$(".show-select-option").empty();
					$(".alert-tag-updateGra").focus().css("border-bottom","1px solid #bbbbbb");
					$("<li>日</li><li>周</li><li>月</li>").appendTo($(".show-select-option"))						        	
						 .mouseover(function(){       		                     
						       $(this).css("background", "#18b0c3");
						 })
						 .mouseout(function() {
						       $(this).css("background", "#fff");
						 })
						 .click(function() {
						       $(".alert-tag-updateGra").val($(this).text());
						       $(".show-select-option").hide();
						 });
						       $(".show-select-option").show(); //按下触发div显示   
						       
						$(".content").click(function(evt) {
							if (evt.target.id!="select-icon"&&evt.target.id!="add-tag-updateGra"&&evt.target.id!="edit-tag-updateGra") {
								 $(".show-select-option").hide();
								      return;
								 }
							});        
					});		
					
					
				/*focus事件去除错误提示&&改变边框样式发光*/
				$(document).on("focus",".validate",function(){
						$(this).next().text("");
						$(".alert-btn-ok").attr("disabled",false);
						$(this).css({"border":"1px solid #38b8ca","box-shadow":"0 0 8px rgba(56, 184, 202, 1)"});
				}).on("blur",".validate",function(){
					$(this).css({"border":"1px solid #bbbbbb","box-shadow":"none"});
				});
				$(document).on("focus",".alert-tag-sort",function(){
					$(".tag-sort-errMsg").text("");
					$(".alert-btn-ok").attr("disabled",false);
				});
	
			}			
	};
})();

