<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/devSearch.css">
<title>devSearch</title>
</head>
  <body>
 <div class="container">
	<!-- 页面头部 -->
	<!--  <%@include file="common/header.jsp" %>-->
	<div class="header-choose-search">
		<div class="search">
			<form action="dvcSearch" method="post">
				<input type="text" id="search" name="dvc" autocomplete="off" placeholder="搜索设备号"/>
				<ul class='show-blur-tag' style="display:none"></ul>
				<input  type="submit" value="" id="submit-search"/>
			</form>
		</div>			
		<div class="clear"></div>
	</div>  
   <!-- 搜索内容 -->
	<div class="dev-search-content">
	  <div id="head-msg">
		   <table class="head-table" >
		      <thead class="head-table-header">
					<tr>
						<td>名称</td>
						<td class="head-table-split">ID</td>
						<td class="head-table-split">描述</td>
						<td class="head-table-split">设备号</td>
					</tr>
				</thead>
	 		 </table>
	 </div>
		  <div id="tag-admin">
			<table class="tag-table" >
			       <tbody class="tag-table-tbody">	
						<c:forEach items ="${resultList}" var ="tag">
						<tr>
							<td class="tag-name"><c:out value="${tag.tagName}"/></td> 
							<td class="tag-id"><c:out value="${tag.tagId}"/></td>
							<td class="tag-desc"><c:out value="${tag.tagDescription}"/></td>
							<td class="tag-davName"><c:out value="${devName}"/></td>
						</tr>
						</c:forEach>
					</tbody>
			</table>
		  </div>
		</div>
	<div id="pagination">
	<div id="pagination-left">共<c:out value="${tagNum}"/>个标签</div>
	</div>
	<!-- 无搜索结果-->
	<div class="no-result">
		<span>没有匹配设备号！</span>
	</div>
    <!-- 页面脚部	 -->
	<%@include file="common/footer.jsp"%>	
</div>	
<script type="text/javascript" src="${resourcePath}/js/pages/devSearch.js"></script>	
  </body>
</html>
