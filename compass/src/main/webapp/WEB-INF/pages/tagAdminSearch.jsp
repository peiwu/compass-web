<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
    <%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
    <%-- <%@page import="java.util.Map"%>
    <%@page import="java.util.HashMap"%> --%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/tagAdminSearch.css">
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/util/select2.css">

<title>tagAdmin</title>
</head>
<body>
<input type="hidden" id="authority" value="${authority}"/> 
<div class="container">
	<!-- 获取所有的二级分类标签，编辑标签时选择所属分类使用 -->
	<div id="all-child-class" style="display:none;">
		<c:forEach items="${allChildClassifies}" var="childClassify" step="1">
			<option value="${childClassify.classifyId}">${childClassify.classifyName}</option>
		</c:forEach>
	</div>

	<input value="${resultMap}" type="hidden" id="resultMap"/>


	<!-- 页面头部 -->
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
		
	<div class="admin-search-content">	
		<!-- 页面content部分头部 ，显示所有列表的列属性 -->
		<div id="child-classifies-msg">
			<table class="child-classiffies-table" >
				<thead class="child-table-header">
					<tr>
						<td>名称</td>
						<td class="child-table-split">ID</td>
						<td class="child-table-split">描述</td>
						<td class="child-table-split">更新粒度</td>
						<td class="child-table-split">更新跨度</td>
						<td class="child-table-split">操作</td>
					</tr>
				</thead>	
			</table>
		</div>
		<!-- 搜索结果，显示多个二级分类及相应标签列表 -->
		<div id="tag-admin">
			<c:forEach items="${resultMap}" var="item">
				<table class="tag-table">
					<thead class="child-table-tbody">			
						<tr>
							<td class="child-class-name"><c:out value="${item.key.classifyName}"/></td>
							<td class="child-class-id"><c:out value="${item.key.classifyId}"/></td>
							<td class="child-class-desc"><c:out value="${item.key.classifyDescription}"/></td>
							<td>--</td>
							<td>--</td>
							<td>
								<li class="operator-item edit-child-class" id="edit-child-class" data-childid="${item.key.classifyId}" data-childname="${item.key.classifyName}" data-childdesc="${item.key.classifyDescription}"><a href="javascript:void(null)"></a></li>
								<li class="operator-item remove-child-class" id="remove-child-class" data-childid="${item.key.classifyId}" ><a href="javascript:void(null)"></a></li>
							</td>
						</tr>
					</thead>								
					<tbody class="tag-table-tbody">	
						<c:forEach items = "${item.value}" var ="tag">
						<tr>
							<td class="tag-name"><c:out value="${tag.tagName}"/></td> 
							<td class="tag-id"><c:out value="${tag.tagId}"/></td>
							<td class="tag-desc"><c:out value="${tag.tagDescription}"/></td>
							<td class="tag-update-gra"><c:out value="${tag.updateGranularity}"/></td>
							<td class="tag-update-span"><c:out value="${tag.updateSpan}"/></td>
							<td class="operator">
								<ul>
									<li class="operator-item edit-tag" data-tagid="${tag.tagId}" data-tagname="${tag.tagName}" data-tagdesc="${tag.tagDescription}" data-updategra="${tag.updateGranularity}" data-updateSpan="${tag.updateSpan}"><a></a></li>
									<li class="operator-item remove-tag"  data-tagid="${tag.tagId}"><a></a></li>
								</ul>
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:forEach> 
		</div>
		<!-- 分页 -->
<!-- 		<div id="pagination">
			<div id="pagination-left"></div>
			<div id="pagination-right">
				<span class="page-operator page-up">上一页</span>
				<span class="current-page-num">1</span>
				<span class="page-operator page-down">下一页</span>
				<span class="total-page-num">共<span class="total-page"></span>页</span>
				<span>到</span>
				<span class="page-choose-div">
					<input class="page-choose" type="text" />
					<span class="page-up"></span>
					<span class="page-down"></span>
				</span>
				<span class="page-operator page-choose-ok">确定</span>								
			</div>
			<div class="clear"></div>
		</div> -->
	</div>

	<div class="no-result">
		<span>没有匹配标签！</span>
	</div>

	<%@include file="common/footer.jsp"%>			
		
</div>
<script type="text/javascript" src="${resourcePath}/js/pages/tagAdminSearch.js"></script>	
<script type="text/javascript" src="${resourcePath}/js/util/select2.min.js"></script>	

</body>
</html>