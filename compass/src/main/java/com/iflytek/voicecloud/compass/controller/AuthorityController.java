package com.iflytek.voicecloud.compass.controller;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iflytek.voicecloud.compass.service.IAuthorityService;
import com.iflytek.voicecloud.compass.service.IMonitorService;

/**
 * 域账号权限管理，超级管理员账号登录后，点击用户名可以访问
 * @author kwliu
 * @date 上午10:05:33, 2015年11月30日
 */
@Controller
public class AuthorityController
{
    @Autowired
    IAuthorityService authorityService;
    /**
     * 超级管理员才可以进行权限管理
     * 
     * @param sesssion
     * @return
     */
    @RequestMapping(value = "/toAuthority")
    public ModelAndView toAuthority(HttpSession sesssion)
    {
        ModelAndView modelAndView = new ModelAndView();
        if (sesssion.getAttribute("authority") != null && sesssion.getAttribute("authority").toString().equals("0"))
        {
            modelAndView.addObject("authorities", authorityService.getAllAuthorities());
            modelAndView.setViewName("authority");
        }
        else
        {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }
    
    /**
     * 添加一个域账号权限
     * 
     * @param request
     * @param sesssion 
     * @return
     */
    
    @RequestMapping(value = "/addAuthority")
    public ModelAndView addAuthority(HttpServletRequest request, ServletRequest sesssion)
    {
    	 ModelAndView modelAndView = new ModelAndView();
    	 if (sesssion.getAttribute("authority") != null && sesssion.getAttribute("authority").toString().equals("0"))
         {
        String username = request.getParameter("username");
        int auth = Integer.parseInt(request.getParameter("authority"));
        authorityService.addAuthority(username, auth);
        modelAndView.addObject("authorities", authorityService.getAllAuthorities());
        modelAndView.setViewName("authority");
         }
    	 else modelAndView.setViewName("login");
        return modelAndView;
    }
    
    /**
     * 删除一个域账号权限
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/removeAuthority")
    public ModelAndView removeAuthority(HttpServletRequest request,ServletRequest sesssion)
    {
    	
    	ModelAndView modelAndView = new ModelAndView();
    	 if (sesssion.getAttribute("authority") != null && sesssion.getAttribute("authority").toString().equals("0"))
         {
        String username = request.getParameter("username");
        authorityService.removeAuthority(username);
        modelAndView.addObject("authorities", authorityService.getAllAuthorities());
        modelAndView.setViewName("authority");
         }
    	 else modelAndView.setViewName("login");
        return modelAndView;
    }
}
