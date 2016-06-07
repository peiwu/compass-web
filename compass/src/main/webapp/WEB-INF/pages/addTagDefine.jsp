<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<form action="addTagDefine" method="POST">
tag_name:<input type="text" name="tag_name" id="tag_name"/><br/>
tag_description:<input type="text" name="tag_description" id="tag_description"/><br/>
update_granularity:<input type="text" name="update_granularity" id="update_granularity"/><br/>
update_span:<input type="text" name="update_span" id="update_span"/><br/>
<input type = "submit" value="add" /><br/>
</form>
</body>
</html>