<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.iflytek.voicecloud.compass.po.Redis"%>
<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> -->
<!DOCTYPE HTML>
<html>
  <head>
   <%@include file="common/meta.jsp"%>
    <title>Monitor</title>
  </head>
  <body>
    <div class="choose">
					  任务类型：<select id="type" name="type">
						<option value="all">全部</option>
						<option value="0">文件</option>
						<option value="1">Redis</option>
						</select>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				          任务状态：<select id="state" name="state">
						<option value="all">全部</option>
						<option value="0">正在执行</option>
						<option value="1">已完成</option>
						<option value="2">出错</option>
						</select>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					用户：
					<select id="person" name="person">
					<option value="all">全部</option>
					<c:forEach items="${personlist}" var="person">
					<option value="${person}">${person}</option> 
					</c:forEach>
				    </select>
				        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    起始日期: &nbsp;&nbsp;<input id="startdate" type="date" value=""/>
				        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    结束日期: &nbsp;&nbsp;<input id="enddate" type="date" value=""/>
	</div>
	<div class="maincontainer">
		<table  class="redis-table">
			<thead>
				<tr height="50px" bgcolor=#16C5BC>
					<td width="100">ID</td>
					<td width="100">用户名</td>
					<td width="50">任务类型</td>
					<td width="200">导出路径</td>
					<td width="80">前缀</td>
					<td width="100">导出数量</td>
					<td width="50">任务状态</td>
					<td width="150">开始时间</td>
					<td width="150">结束时间</td>
					<td width="100">花费时间(毫秒)</td>
					<td width=50>操作</td>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
   </div>
  </body>
  <script type="text/javascript" src="${resourcePath}/js/pages/monitor.js"></script>
  <style>
  .choose{
    position: relative;
    top:2%;
	left: 10%;
	font-family:微软雅黑;
	font-size: 14px;
	color:#000;
  }
  .maincontainer {
    top:4%;
  	position: relative;
	left: 10%;
	font-family:微软雅黑;
	font-size: 16px;
	color: #000;
}

.redis-table tbody tr{
     height:35px;
}
.stop a{
color:	#DC143C;
}
td
  {
    text-align:center;
  }

  </style>
</html>
