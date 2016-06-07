<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    <link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/header2.css"> 
<html>
	<head>
	<title> header2</title>
	</head>
<body >
<div class="header">
<input type="hidden" id="authority" value="${authority}"/> 
	<div class="header-top">
		<div class="top-log">
		     <img src="${resourcePath}/img/logo2.png">
		</div>
		<div class="top-manage">
		 后  台  管  理
		</div>
		<div class="top-nav">
			<ul class="top-nav-main">
				<li><a id="monitor" href="javascript:void(null)" target="main"> <span class="top-nav-item"
						id="top-monitor">任务监控<span></span></span>
				</a></li>
				<li><a id="online" href="javascript:void(null)"  target="main"> <span class="top-nav-item"
						id="top-online">在线用户<span></span></span>
				</a></li>
				<li><a id="authority" href="javascript:void(null)"  target="main"> <span class="top-nav-item"
						id="top-authority">权限管理<span></span></span>
				</a></li>
			</ul>
		</div>
        
	</div>
</div>
  <script type="text/javascript" src="${resourcePath}/js/libs/jquery-2.1.4.js"></script>
  <script type="text/javascript" src="${resourcePath}/js/pages/header2.js"></script>
 </body>
</html>