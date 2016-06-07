<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
<%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/complexSearch.css">
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/util/select2.css">
<title>complexSearch</title>
</head>
<body>		 
<div class="container">
<input type="hidden" id="authority" value="${authority}"/> 
	<form action="toTagAdmin" id="refresh" method="post">
		<input type="hidden" id="currentParentId" name="currentParentId"/>
	</form> 
		<!--  <%@include file="common/header.jsp" %>-->
		<div class="header-choose-search">
		    <div class="dvcsearch">
		    <label class="dvcsearch-label">设备号查询：</label>
		    </div>
			<div class="search">
				<form action="" method="post">
					<input type="text" id="search" name="dvc" autocomplete="off" placeholder="输入设备号"/>
					<ul class='show-blur-tag' style="display:none"></ul>
					<input  type="submit" value="" id="submit-search"/>
				</form>
			</div>
						
			<div class="clear"></div>
		</div>
		<div id="content">
			<div class="content-wrapper">
				<div class="content-left" id="content-left">
					<div class="parent-classifies">
						<ul class="parent-classifies-ul">
							<c:forEach items="${parentClassifies}" var="parentClassify" step="1">
								<li class="parent-class-item" data-parentid="${parentClassify.classifyId}" data-parentname="${parentClassify.classifyName}" data-parentdesc="${parentClassify.classifyDescription}">
									<span class="parent-class-icon"></span>
									<a href="javascript:void(null)" data-parentid="${parentClassify.classifyId}">${parentClassify.classifyName}</a>
								</li>	
								<hr class="parent-class-split" />							
							</c:forEach> 
						</ul>	
						<input type="hidden" id="current-parent-class" value="${currentParentId}"/> 
					</div>						
				</div>
	
				<div class="content-right">			
									
				    <div class="content-right-header">
				       <div class="search-hint">
						  <span >标签组合查询:</span>
				       </div>
				       
					   <div class="search-tags">
					       <ul>							 	
					      </ul>
					  </div>
					   
					   <div class="search-button">
					     <input type="button" id="search-button" value="查询" style="height:30px;width:50px;" disabled="disabled"/>
					   </div>
					    
					    <div class="clear-button">
					     <input type="button" id="clear-button" value="清空" style="height:30px;width:50px;" disabled="disabled"/>
					   </div>
					   
					    <div class="num-show">
					        <label class="dvc-num"></label>
					    </div>
					  
					   <div class="export-button"> 
					       <input type="button" id="export-submit" value="导出文件" style="height:30px;width:70px;" disabled="disabled"/>
					   </div>
					   
					   <div class="redis-button">
					     <input type="button" id="redis-button" value="导入redis" style="height:30px;width:70px;" disabled="disabled"/>
					   </div>
				    </div>
				    
					<hr class="main-top" />
					
					<div id="content-right-main">		
									
						<div class="child-classifies">
							<ul>							 	
							</ul>
							<input type="hidden" id="current-child-class" />
						</div>
						
						<div class="render-result">
							<div id="child-classifies-msg">
								<table class="child-classiffies-table" border="0">
									<thead class="child-table-header">
										<tr>
											<td class="column-name">名称</td>
											<td class="child-table-split column-id ">ID</td>
											<td class="child-table-split column-desc">描述</td>
											<td class="child-table-split column-operator">添加</td>
										</tr>
									</thead>							
								</table>
							</div>
						
							<div id="tag-admin">
								<table class="tag-table">
									<thead></thead>
									<tbody class="tag-table-tbody">									
									</tbody>
								</table>
							</div>
						
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
		
		</div>
		<%@include file="common/footer.jsp"%>			
</div>
<script type="text/javascript" src="${resourcePath}/js/pages/complexSearch.js"></script>
<script type="text/javascript" src="${resourcePath}/js/util/select2.min.js"></script>		
</body>
</html>