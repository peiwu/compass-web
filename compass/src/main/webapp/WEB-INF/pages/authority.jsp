<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.iflytek.voicecloud.compass.po.Authority"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>权限控制</title>
</head>
<body>
	<div class="maincontainer">
		<h2>已有权限列表：</h2>
		<table class="authority-table" border="1" cellspacing="0">
			<thead>
				<tr height="50px">
					<td>ID</td>
					<td>域账号</td>
					<td>权限(0-超级管理员，1-普通管理员，2-广告用户)</td>
					<td>操作</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${authorities}" var="authority" step="1">
					<tr height="50px">
						<td>${authority.id}</td>
						<td>${authority.username}</td>
						<td>${authority.authority}</td>
						<td>
							<a href="removeAuthority?username=${authority.username}">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<br />
		<h2>添加权限：</h2>
		<form action="addAuthority" method="post">
			<table class="add-authority">
				<tr>
					<td>域账号:</td>
					<td><input type="text" id="username" name="username" /></td>
				</tr>
				<tr>
					<td>权限:</td>
					<td><select id="authority" name="authority">
							<option style="width: 200px">0</option>
							<option style="width: 200px">1</option>
							<option style="width: 200px">2</option>
					</select></td>
				</tr>
				<tr>
					<td><input type="submit" id="submit" value="添加" /></td>
					<td><input type="reset" id="reset" name="重置" /></td>
				</tr>	 
			</table>
			
		</form>
	</div>
</body>
<style>
.maincontainer {
	position: relative;
	left: 35%;
	font-size: 16px;
	font-family:微软雅黑;
	color: #000;
}

.authority-table {
	width: 600px;
}
.add-authority{
	font-size:16px;
}
</style>
</html>