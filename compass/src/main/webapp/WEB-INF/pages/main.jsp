<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <script>
    function load()
          {      
          initWebSocket();
          }
    var socket;
     //websocket方式
     function initWebSocket(){
	//socket= new WebSocket('ws://192.168.72.124:8000/compass/websocket');
	//socket= new WebSocket('ws://192.168.52.25:8000/compass/websocket');
	//socket= new WebSocket('ws://127.0.0.1:8080/compass/websocket');
    socket= new WebSocket('ws://taghf.openspeech.cn/compass/websocket');
     //socket= new WebSocket('ws://localhost/compass/websocket');
	socket.onopen = function(){
       // alert('Connection open!');
		                        };
	socket.onclose = function()
	    {
		//alert('Disconnecting');
		  };
	socket.onmessage = function (evt) 
	   { 
	      var msg = evt.data;
	      var head=msg.substring(0,5);
	      var body=msg.substring(5);
	      var array=body.split("&&");
	      var req=null;
	       if (window.XMLHttpRequest) req = new XMLHttpRequest();
            else if (window.ActiveXObject) 
                {
                   try {
                     req = new ActiveXObject("Msxml2.XMLHTTP");
                       } catch (e1) {
                          try {
                               req = new ActiveXObject("Microsoft.XMLHTTP");
                               } catch (e2) {
                                        req = null;
                                             }
                                    }
                 }
    if(req==null) alert("更新任务数据库失败！");
    else{
	      if(head=="redis") 
	      {
          var url="search/changeRedis?id="+array[1];
          url=encodeURI(encodeURI(url));
          req.open("GET",url,true);
          req.onreadystatechange=function(){};
          req.send(null);
          alert(array[0]+"  导入Redis完成");
	      }
	      //导入redis出错，删除数据库记录
	      else if(head=="error")
	       {
          var url="search/errorRedis?id="+array[1];
          url=encodeURI(encodeURI(url));
          req.open("GET",url,true);
          req.onreadystatechange=function(){};
          req.send(null);
              
            alert(array[0]+"  导入Redis出错，请重试！");
	      }
	      //导出文件出错
	     else if(head=="errof") 
	        {
	      var url="search/errorRedis?id="+array[1];
          url=encodeURI(encodeURI(url));
          req.open("GET",url,true);
          req.onreadystatechange=function(){};
          req.send(null);
	       alert(array[0]+" 导出文件出错，请重试！");
	         }
		  //导入文件成功
	      else 
            {
          //修改任务状态
          var url="search/changeRedis?id="+array[1];
          url=encodeURI(encodeURI(url));
          req.open("GET",url,true);
          req.onreadystatechange=function(){};
          req.send(null);
          window.frames["header"].document.getElementById("listname").value=array[0];
	      window.frames["header"].document.getElementById("export").submit();
	         }
	     }
	   };
	 socket.onerror = function(event) {
	  	   alert(event.data);
	  	  };
	  	  }
	 window.onbeforeunload=function(event){
	 var event = event || window.event;
	 var req=null;
        //ajax更新数据库状态
	     if(window.XMLHttpRequest) req = new XMLHttpRequest();
            else if (window.ActiveXObject) 
                {
                   try {
                     req = new ActiveXObject("Msxml2.XMLHTTP");
                       } catch (e1) {
                          try {
                               req = new ActiveXObject("Microsoft.XMLHTTP");
                               } catch (e2) {
                                        req = null;
                                             }
                                    }
                 }
          if(req==null) alert("err Happend!");
          else{
          var url="search/completeRedis";
          url=encodeURI(encodeURI(url));
          req.open("GET",url,true);
          req.send(null);
              }
              //兼容firefox4之前的写法
       if(event) event.returnValue = "页面关闭后，后台redis任务继续，但完成后不通知！";
       
         return "页面关闭后，后台redis任务继续，但完成后不通知！";
	  	  };
       </script>
    <title>main</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <frameset border="0" rows="60,*" onload="load()"> 
        <frame name="header" src="toHeader"> 
        <frame name="main" src="toTagAdmin"> 
    </frameset> 
</html>
