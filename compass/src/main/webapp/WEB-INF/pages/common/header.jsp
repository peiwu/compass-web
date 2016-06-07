<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<link rel="stylesheet" type="text/css" href="${resourcePath}/css/common/header_footer.css">
<html>
	<head>
	<%@include file="meta.jsp"%>
	<title> header</title>
	</head>
<body >
<div class="header">
<input type="hidden" id="authority" value="${authority}"/> 
	<div class="header-top">
		<div class="logo">
			<a href="toWelcome"> <span><img
					src="${resourcePath}/img/logo.png"></span>
			</a>
		</div>
         <form id="export" action="search/exportFile" method="post">
		  <input type="hidden" id="listname" name="listname">
		  </form>
		<div class="top-nav">
			<ul class="top-nav-main">
				<li><a href="toTagAdmin" target="main"> <span class="top-nav-item"
						id="top-tag-admin">标签管理<span></span></span>
				</a></li>
				<li><a href="toTagView"  target="main"> <span class="top-nav-item"
						id="top-tag-view">标签视图<span></span></span>
				</a></li>
				<li><a href="toTagIntegration"  target="main"> <span class="top-nav-item"
						id="top-tag-integration">标签整合<span></span></span>
				</a></li>
				<li><a href="toComplexSearch"  target="main"> <span class="top-nav-item"
						id="top-tag-search">标签查询<span></span></span>
				</a></li>
			</ul>
		</div>
        
		<div class="login-info">
			<span class="login-info-item  huanying">欢迎</span> <span
				class="login-info-item username"><a id="username-a" href="javascript:void(null)">${username}</a></span>
			<span class="login-info-item split" id="split"></span> <a id="logout" href="javascript:void(null)"><span class="login-info-item tuichu">注销</span></a>
		</div>
	</div>
</div>
<script type="text/javascript" src="${resourcePath}/js/pages/header.js"></script>
<script type="text/javascript" src="${resourcePath}/js/util/select2.min.js"></script>	
 </body>
<style>
#username-a:link,#username-a:visited {
	color: #fff;
	text-decoration: none;
}

#username-a:hover {
	color: yellow;
	text-decoration: none;
}
</style>
</html>