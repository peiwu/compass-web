<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
    <%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/tagViewSearch.css">
<title>tagAdmin</title>
</head>
<body>
<div class="container">
	<input value="${resultMap}" type="hidden" id="resultMap"/>
	<!-- 页面头部 -->
	<!--  <%@include file="common/header.jsp" %>-->
	<div class="header-choose-search">
		<div class="choose-tag-view">						
			 <form>
				<input type="radio" class="choose"  id="effective" name="viewChoose" value="0" /><label for="effective" class="radio-label">仅显有效</label>
				<input type="radio" class="choose" id="all" name="viewChoose" value="1" /><label for="all" class="radio-label">我的标签</label>
			</form> 
			
			<input type="hidden" id="searchWord" value="${tagName}"/>	
		</div>
		
		<div class="search">
			<form id="search-form" action="" method="post">
				<input type="text" id="search" name="tagName" autocomplete="off" placeholder="搜索标签 "/>
				<ul class='show-blur-tag' style="display:none"></ul>
				<input type="hidden" id="chooseStat" name="chooseStat" value="${chooseStat}" />
				<input type="hidden" id="currentPage" name="currentPage" value="${currentPage}" /><!-- 记录是标签视图页面还是标签整合页面 -->
				<input  type="submit"  value="" id="submit-search"/>
			</form>
		</div>
		<div class="clear"></div>
	</div>
	
	<!-- 搜索结果内容 -->
	<div class="view-search-content">		
		<c:forEach items="${resultMap}" var="item">
			<div class="each-result-wrapper">
				<!-- 显示结果中的二级分类 -->
				<div class="child-class-view">			
					<span class="child-icon-name"><span class="child-icon"></span><p class="child-class-name"><c:out value="${item.key.classifyName}"/></p></span>
					<p class="child-class-stat"><c:out value="${item.key.rate}"/>%</p>
					<span class="child-hint-msg">用户拥有该标签</span>
					<p class="child-class-desc"><c:out value="${item.key.classifyDescription}"/></p>		
				</div>
				<div class="child-tag-split"></div>
					
				<!-- 显示相应二级分类下的标签 -->
				<div class="tag-view">
					<c:forEach items="${item.value}" var="tagStat">
						<div class="each-tag-view">
							<span class="name">
								<span class="icon tag-icon"></span>								
								<span class="tag-name" style="display: inline;"><c:out value="${tagStat.tagName}"/></span>
									<span class="icon tag-msg-icon" style="display:none"></span>
									<span class="descid-hint-msg" style="display:none" >
										<p class="tag-id" >ID：<span class="tag-id" ><c:out value="${tagStat.tagId}"/></span></p>	
										<p class="tag-desc" >描述：<span class="tag-desc" ><c:out value="${tagStat.tagDescritpion}"/></span></p>
									</span>
								</span>								
							</span>
							<span class="totlePop">
								<span class="tag-totalPop"><c:out value="${tagStat.totalPopulation}"/></span>
								<span class="tag-hint-msg light">用户拥有该标签</span>
							</span>
							<span class="updateGra">
								<span class="icon updateGra-icon"></span>
								<span class="tag-updateGra light">每<span class="upGra-val"><c:out value="${tagStat.updateGranularity}"/></span>更新</span>
							</span>
							<span class="updateSpan">
								<span class="icon updateSpan-icon"></span>
								<span class="tag-updateSpan light">更新跨度：<span class="upSpan-val"><c:out value="${tagStat.updateSpan}"/></span>个月</span>
							</span>
							<span class="updateTime">
								<span class="icon updateTime-icon"></span>
								<span class="tag-latestUpdateTime light">更新时间：<span class="time"><c:out value="${tagStat.latestUpdateTime}"/></span></span>
							</span>												
						</div>
						<!-- 显示折线图 -->
						<hr class="tag-split" />
						<span class="show-chart" data-tagid="${tagStat.tagId}" data-id="${item.key.classifyId}-${tagStat.tagId}"></span>
						<div class="tag-linechart" id="${item.key.classifyId}-${tagStat.tagId}"></div>						
					</c:forEach>
				</div>
				<div class="clear"></div>
			</div>
		</c:forEach>	
			
	</div>	
	
	<div class="no-result">
		<span>没有匹配标签！</span>
	</div>
	
	<!-- 页面脚部	 -->	
	 <%@include file="common/footer.jsp"%>		
</div>
<script type="text/javascript" src="${resourcePath}/js/util/echarts.js"></script>
<script type="text/javascript" src="${resourcePath}/js/pages/tagViewSearch.js"></script>	
</body>
</html>