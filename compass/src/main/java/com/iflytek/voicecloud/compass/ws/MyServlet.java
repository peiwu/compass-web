package com.iflytek.voicecloud.compass.ws;
import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.caucho.websocket.*;
public class MyServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	public void service(ServletRequest req, ServletResponse res)
	    throws IOException, ServletException
	  {
	    WebSocketServletRequest wsReq = (WebSocketServletRequest) req;

	    WebSocketListener handler = new MyHandler3();

	    wsReq.startWebSocket(handler);
	  }
	}
