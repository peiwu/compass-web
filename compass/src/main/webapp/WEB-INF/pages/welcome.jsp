<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/welcome.css">
<title>tagAdmin</title>
<style type="text/css">

</style>
</head>
<body>
<div id="welcome-container">
		<%@include file="common/header.jsp" %>
		<div id="welcome-content">
			<img id="welcome-img" src="${resourcePath}/img/welcome(3).png">
			<div id="word-btn-div">
				<div id="welcome-word">
					<p class="line1"><span id="welcome-in">欢迎进入</span><span id="welcome-in-tag">数据管理平台！</span></p>
					<p class="line2"><span id="guanli">可视化管理</span><span id="dongcha">多维度洞察</span></p>
				</div>
				<div id="welcome-btn-group">
					<!-- <a href="toTagAdmin"><button class="welcomt-btn welcome-admin-btn"></button></a>-->
					<a href="toMain"><button class="welcomt-btn welcome-admin-btn"></button></a>
					<a href="${basicDataUrl}" target="basicDate"><button class="welcomt-btn welcome-view-btn"></button></a>
				</div>
			</div>
		</div>
		<%@include file="common/footer.jsp"%>
 <script type="text/javascript">
	$(".top-nav,.login-info").hide();
 </script>		
</div>
</body>
</html>