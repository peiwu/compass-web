$(function(){
	
	//$("#top-tag-search span").addClass("top-nav-active");
	
	var resultList=$("#resultList").val();
	if(resultList!=[]){
		$(".dev-search-content").show();
		$(".no-result").hide();
	}else{
		$(".dev-search-content").hide();
		$(".no-result").show();
	}
});
