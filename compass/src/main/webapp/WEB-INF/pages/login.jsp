<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html >
<head>

<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/login.css">
<script type="text/javascript" src="${resourcePath}/js/pages/login.js"></script>

</head>
<body>
	<div class="container">
	<div class="alertMessageBg" id="alertMessageBg" style="display:none"><p class="loading">正在进行身份验证与数据初始化，请稍后 </p><div id="loading-img"></div>
	</div>
		<div class="header" id="login-header">			
		</div>
		
		<div class="content" id="login-content">
			<div id="login-form">
				<form action="tologin" method="post" name="form">
					<div class="form-group username-div">
						<label id="username-label" for="username">用户名：</label>
						<span id="username-bg">									
							<input type="text" class="input-username" name="username" id="username"  disableautocomplete autocomplete="off" style="background-color:transparent"/>
						</span>	
					</div>
					
					<div class="form-group password-div">
						<label id="password-label" for="password">密<span id="space"></span>码：</label>
						<span id="password-bg">
							<input type="password" class="input-password" name="password" id="password"  disableautocomplete autocomplete="off" style="background-color:transparent"/>
						</span>
					</div>
					
					<div class="form-group btn-div">
						<!-- <button class="btn-ok" id="login-ok" type="submit" style= "background-color:transparent"></button> -->
						<input type="button" id="login-ok" class="btn-ok" style="background-color:transparent" onClick="check();">
					</div>
				</form>
		
			</div>
		</div>
	</div>
</body>
</html>