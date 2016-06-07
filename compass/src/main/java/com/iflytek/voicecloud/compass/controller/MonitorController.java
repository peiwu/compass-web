package com.iflytek.voicecloud.compass.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.service.IMonitorService;

@Controller
public class MonitorController {
	    @Autowired
	    IMonitorService monitorService;
	    /**
	     * 管理员才可以进行权限管理
	     * 
	     * @param sesssion
	     * @return
	     */
	    @RequestMapping(value = "/toMonitor")
	    public ModelAndView toMonitor(HttpSession sesssion)
	    {
	        ModelAndView modelAndView = new ModelAndView();
	        if (sesssion.getAttribute("authority") != null && (sesssion.getAttribute("authority").toString().equals("0")||(sesssion.getAttribute("authority").toString().equals("1"))))
	        {
	        	List<String> personlist=new ArrayList<String>();
	        	personlist=monitorService.getAllPerson();
	            modelAndView.addObject("personlist",personlist);
	            modelAndView.setViewName("monitor");
	        }
	        else
	        {
	            modelAndView.setViewName("login");
	        }
	        return modelAndView;
	    }
	    
	    @RequestMapping(value = "/toOnline")
	    public ModelAndView toOnline(HttpSession sesssion)
	    {
	        ModelAndView modelAndView = new ModelAndView();
	        if (sesssion.getAttribute("authority") != null && (sesssion.getAttribute("authority").toString().equals("0")||(sesssion.getAttribute("authority").toString().equals("1"))))
	        {
	            modelAndView.setViewName("online");
	        }
	        else
	        {
	            modelAndView.setViewName("login");
	        }
	        return modelAndView;
	    }
	    @RequestMapping(value = "/monitorChange")
	    public void toMonitorChange(HttpServletRequest req,HttpServletResponse response,@RequestParam(value = "type")String type,
	    		@RequestParam(value = "state")String state,@RequestParam(value = "person")String person,
	    		@RequestParam(value = "start")long start,
	    		@RequestParam(value = "end")long end)
	    {
	        	List<Redis> redislist=new ArrayList<Redis>();
	        	redislist=monitorService.getSelectRedis(type,state,person,start,end);
	        	String redislistJson = JSON.toJSONString(redislist, SerializerFeature.PrettyFormat);
		        PrintWriterUtil.returnPrintWriterJson(response, redislistJson);
		        
	    }
	    @RequestMapping(value = "/userChange")
	    public void toUserChange(HttpServletRequest req,HttpServletResponse response)
	    {
	        	 ServletContext application = req.getServletContext();  
	             List<Map<String,String>> userList =(List<Map<String, String>>) application.getAttribute("userList");  
	             String userlistJson=JSON.toJSONString(userList, SerializerFeature.PrettyFormat);
		        PrintWriterUtil.returnPrintWriterJson(response,userlistJson);
		        
	    }
}
