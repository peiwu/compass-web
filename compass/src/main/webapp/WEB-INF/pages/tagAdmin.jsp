<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
<%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/tagAdmin.css">
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/util/select2.css">

<title>tagAdmin</title>
</head>
<body>		 
<div class="container">
<input type="hidden" id="authority" value="${authority}"/> 
	<form action="toTagAdmin" id="refresh" method="post">
		<input type="hidden" id="currentParentId" name="currentParentId"/>
	</form> 
	<!-- <%@include file="common/header.jsp" %>-->
		<div class="header-choose-search">
			<div class="search">
				<form action="adminSearch" method="post">
					<input type="text" id="search" name="tagName" autocomplete="off" placeholder="搜索标签"/>
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
									<span class="edit-parent-class" data-parentid="${parentClassify.classifyId}" data-parentname="${parentClassify.classifyName}" data-parentdesc="${parentClassify.classifyDescription}"></span>
									<span class="remove-parent-class" data-parentid="${parentClassify.classifyId}"></span>
								</li>	
								<hr class="parent-class-split" />							
							</c:forEach> 
							<li class="add-parent-classifies">
								<a href="javascript:void(null)"  id="add-parent-class" >
									<i class="add-parent-icon"></i>
									<span>一级分类</span>
								</a>
							</li>
						</ul>	
						<input type="hidden" id="current-parent-class" value="${currentParentId}"/> 
					</div>						
				</div>
	
				<div class="content-right">								
					<div class="content-right-header">
						<span class="exist">标签管理</span>
						<span id="parent-classifies-name"></span>
						<span class="parent-hint-icon">
							<div class="parent-hint-div">
								<p>ID:<span id="parent-classifies-id"></span></p>
								<p>描述:<span id="parent-classifies-desc"></span></p>
							</div>
						</span>
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
											<td class="child-table-split column-updateGra">更新粒度</td>
											<td class="child-table-split column-updateSpan">更新跨度</td>
											<td class="child-table-split column-operator">操作</td>
										</tr>
									</thead>	
									<tbody class="child-table-tbody">
										<tr>
											<td class="column-name  child-class-name"></td>
											<td class="column-id child-class-id"></td>
											<td class="column-desc  child-class-desc"></td>
											<td class="column-updateGra">--</td>
											<td class="column-updateSpan">--</td>
											<td class="column-operator">
												<li class="operator-item edit-child-class" id="edit-child-class"><a href="javascript:void(null)"></a></li>
												<li class="operator-item remove-child-class" id="remove-child-class"><a href="javascript:void(null)"></a></li>
											</td>
										</tr>
									</tbody>							
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
							
							<div class="add-tag-div">
								<span id="add-tag"><a href="javascript:void(null)">新标签</a></span>
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
<script type="text/javascript" src="${resourcePath}/js/pages/tagAdmin.js"></script>
<script type="text/javascript" src="${resourcePath}/js/util/select2.min.js"></script>	
	
</body>
</html>