package com.iflytek.voicecloud.compass.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener{     
	
    @Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}   
    public void sessionDestroyed(HttpSessionEvent event) { 
    	HttpSession ses = event.getSession();  
		ServletContext application = ses.getServletContext();    
		synchronized (this) {     
			List<Map<String,String>> userList =(List<Map<String, String>>) application.getAttribute("userList");
				if(userList == null){  
		              userList = new ArrayList<Map<String,String>>();  
		              application.setAttribute("userList",userList);  
		                            }  
				
		        for(int i=0;i<userList.size();i++){
		        	if(userList.get(i).containsValue(ses.getAttribute("username"))) {userList.remove(i);break;}
		                                           }
		        application.setAttribute("userList",userList);  
			}     
		}
	}    
