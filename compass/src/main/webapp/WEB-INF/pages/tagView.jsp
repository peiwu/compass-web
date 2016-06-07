<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
<%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/tagView.css">
<title>tagView</title>
</head>
<body>
<div class="container">
	<!-- <%@include file="common/header.jsp" %>-->
	<div class="header-choose-search">
		<div class="choose-tag-view">						
			<form>
				<input type="radio" class="choose" id="effective" checked="checked" name="viewChoose" value="0" /><label for="effective" class="radio-label">仅显有效</label>
				<input type="radio" class="choose" id="all" name="viewChoose" value="1" /><label for="all" class="radio-label">我的标签</label>
			</form>
		</div>
	
		<div class="search">
			<form action="" method="post">
				<input type="text" id="search" name="tagName" autocomplete="off" placeholder="搜索标签 "/>
				<ul class='show-blur-tag' style="display:none"></ul>
				<input type="hidden" id="chooseStat" name="chooseStat" value="0" />
				<input type="hidden" id="currentPage" name="currentPage" value="view" /><!-- 记录是标签视图页面还是标签整合页面 -->
				<input  type="submit"  value="" id="submit-search"/>
			</form>		
		</div>
		
		<div class="clear"></div>
	</div>
	
	<div class="content-wrapper">
		<div class="content-left" id="content-left">
			<div class="parent-classifies">
				<ul class="parent-classifies-ul">
					<c:forEach items="${parentClassifies}" var="parentClassify" step="1">						
						<li class="parent-class-item" data-parentid="${parentClassify.classifyId}" data-parentname="${parentClassify.classifyName}" data-parentdesc="${parentClassify.classifyDescription}">
							<span class="parent-class-icon"></span>
							<a href="javascript:void(null)">${parentClassify.classifyName}</a>
						</li>	
						<hr class="parent-class-split" />						
					</c:forEach> 
				</ul>
				<!-- <input type="hidden" id="current-parent-class" />		 -->	
			</div>						
		</div>
	
		<div class="content-right">
			<div class="content-right-header">
				<span class="exist">标签视图</span>
				<span class="parent-classifies-name"></span>
				<span class="parent-hint-icon">
					<div class="parent-hint-div">
						<p>ID:<span id="parent-classifies-id"></span></p>
						<p>描述:<span id="parent-classifies-desc"></span></p>
					</div>
				</span>
				<div class="export-button"> 
				<form id="export" action="tagView/Export" method="post">
			    <input type="submit" id="export-submit" value="导出" style="height:30px;width:50px;"/>
				</form>
	            </div>
			</div>
			<hr class="main-top" />
			<div class="content-right-main">
				<!-- 显示所有二级分类 -->
				<div class="child-classifies">
					<ul>	
					</ul>
					<input type="hidden" id="current-child-class" />
					<!-- <input type="hidden" id="current-choose" value="0"/> -->
				</div>
				
			
				<!-- 显示一个二级分类及其所有标签 -->
				<div class="tag-view-wrapper">
					<!-- 显示一个二级分类 -->
					<div class="child-class-view">
						<span class="child-icon-name"><span class="child-icon"></span><p class="child-class-name"></p></span>
						<p class="child-class-stat"></p>
						<span class="child-hint-msg">用户拥有该标签</span>
						<p class="child-class-desc"></p>
					</div>

					<div class="child-tag-split"></div>
					<!-- 显示所有标签 -->
					<div class="tag-view">	
					</div>

					<div class="no-tag-hint"></div>
					<div class="clear"></div>
					<div id="pagination">
								<div id="pagination-left">共<span class="total-tag-num"></span>个标签，每页显示10条信息</div>
								<div id="pagination-right">
									<span class="page-operator page-up">上一页</span>
									<span class="current-page-num">1</span>
									<span class="page-operator page-down">下一页</span>
									<span class="total-page-num">共<span class="total-page"></span>页</span>
									<span>到</span>
									<span class="page-choose-div">
										<input class="page-choose" type="text" value="1"/>
										<span class="page-up-icon"></span>
										<span class="page-down-icon"></span>
									</span>
									<span class="page-operator page-choose-ok">确定</span>								
								</div>
								<div class="clear"></div>
					</div>
				</div>
				<div class="no-child-class-hint"></div>
			</div>
		</div>
		<div class="clear"></div>
	</div>
	<%@include file="common/footer.jsp"%>			
</div>
<script  type="x-tmpl-mustache" id="show-tag-template">
	<div class="each">
		<div class="each-tag-view">
			<span class="name">
				<span class="icon tag-icon"></span>
				<span class="tag-name" style="display: inline;" data-tagid="{{tagId}}">{{tagName}}
					<span class="icon tag-msg-icon" style="display:none"></span>
					<span class="descid-hint-msg" style="display:none" >
						<p>ID：<span class="tag-id" >{{tagId}}</span></p>
						<p>描述：<span class="tag-desc" >{{tagDescritpion}}</span></p>		
					</span>
				</span>
			</span>
			<span class="totlePop">
				<span class="tag-totalPop">{{totalPopulation}}</span>
				<span class="tag-hint-msg light">个用户</span>
			</span>
			<span class="updateGra">
				<span class="icon updateGra-icon"></span>
				<span class="tag-updateGra light">每<span class="upGra-val">{{updateGranularity}}</span>更新</span>
			</span>
			<span class="updateSpan">
				<span class="icon updateSpan-icon"></span>
				<span class="tag-updateSpan light">更新跨度：<span class="upSpan-val">{{updateSpan}}</span>个月</span>
			</span>
			<span class="updateTime">
				<span class="icon updateTime-icon"></span>
				<span class="tag-latestUpdateTime light">更新时间：<span class="upTime-val">{{time}}</span></span>
			</span>					
		</div>
        <a href="toTagInsight?tagId={{tagId}}&tagName={{tagName}}">
            <span class="insight">开始洞察 </span>
        </a>
		<span class="show-chart" data-tagid="{{tagId}}"></span>
		<div class="tag-linechart" id="{{tagId}}"></div>
		<hr class="tag-split" />
	</div>
</script>
<script type="text/javascript" src="${resourcePath}/js/pages/tagView.js"></script>	
<script type="text/javascript" src="${resourcePath}/js/util/echarts.js"></script>
</body>
</html>