<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@include file="common/meta.jsp"%>
    <title>Online</title>
  </head>
  <body>
    <div class="maincontainer">
		<br /><br />
		<table class="user-table">
			<thead>
				<tr height="50px" bgcolor=#16C5BC>
					<td width="100">用户名</td>
					<td width="100">权限</td>
					<td width="150">登录时间</td>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
   </div>
  </body>
  <script type="text/javascript" src="${resourcePath}/js/pages/online.js"></script>
  <style>
  .maincontainer {
	position: relative;
	left: 40%;
	/*font-size: 16px;*/
	font-family:微软雅黑;
	color: #000;
}
.user-table tbody tr{
     height:35px;
}
  td
  {
    text-align:center;
  }
  </style>
</html>

