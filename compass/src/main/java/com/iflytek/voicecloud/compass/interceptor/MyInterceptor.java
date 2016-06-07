package com.iflytek.voicecloud.compass.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author kwliu
 * @date 下午2:19:53, 2015年8月25日
 */
public class MyInterceptor implements HandlerInterceptor
{
    
    private static final String INDEX_URL = "/index";
    private static final String TOLOGIN_URL ="/tologin";
    private static final String LOGIN_URL ="/login";
    
    @Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
        throws Exception
    {
       HttpSession session = req.getSession(true);
        //从session 里面获取用户名的信息
       Object obj = session.getAttribute("username");
       if ((obj == null || "".equals(obj.toString())) && (!req.getRequestURL().toString().contains(TOLOGIN_URL)) && (!req.getRequestURL().toString().contains(INDEX_URL))&& (!req.getRequestURL().toString().contains(LOGIN_URL)))
        {
            res.sendRedirect("login");
        }
        return true;
    }
    
    @Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object arg2, ModelAndView arg3)
        throws Exception
    {
    }
    
    @Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object arg2, Exception arg3)
        throws Exception
    {
    }
    
}
