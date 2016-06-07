package com.iflytek.voicecloud.compass.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.aspect.Cache;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.HttpClientUtil;
import com.iflytek.voicecloud.compass.common.LoginUtil;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.service.IAuthorityService;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

/**
 * 
 * @author kwliu
 * @date 下午2:04:41, 2015年9月2日
 */
@Controller
public class LoginController
{
    
    @Autowired
    ITagAdminService tagAdminService;
    
    @Autowired
    IAuthorityService authorityService;
    
    @RequestMapping(value = "/login")
    public ModelAndView login(HttpSession session)
    	    throws AddressException, MessagingException, ClientProtocolException, IOException
    	  {
    	    ModelAndView modelAndView = new ModelAndView();
    	    ClassLoader classLoader = getClass().getClassLoader();
            Properties pro = new Properties();
            FileInputStream in = new FileInputStream(classLoader.getResource("url.properties").getFile());
            pro.load(in);
            Constant.httpUrl = pro.getProperty("httpUrl").trim();
            Constant.basicDataUrl = pro.getProperty("basicDataUrl").trim();
            Constant.esUrl = pro.getProperty("esUrl").trim();
            Constant.redisUrl = pro.getProperty("redisUrl").trim();
            Constant.redisTime = Integer.valueOf(pro.getProperty("redisTime").trim());
            Constant.threadNum = Integer.valueOf(pro.getProperty("threadNum").trim());
            Constant.jedisNum = Integer.valueOf(pro.getProperty("jedisNum").trim());
            in.close();
    	    Constant.username = "";
    	    Constant.password = "";
    	    session.invalidate();
    	    modelAndView.setViewName("login");
    	    return modelAndView;
    	  }

    @RequestMapping({"/index"})
    	  public String index(HttpSession session)
    	    throws IOException
    	  {
    	    return "login";
    	  }
    @RequestMapping(value = "/tologin")
    public ModelAndView tologin(HttpSession session, @RequestParam(value = "username", required = false)
    String username, @RequestParam(value = "password", required = false)
    String password)
        throws AddressException, MessagingException, ClientProtocolException, IOException
    {
      Logger logger = Logger.getLogger("E");  
       Constant.username = username + "@iflytek.com";
       Constant.password = password;
       LoginUtil loginUtil = new LoginUtil();
        ModelAndView modelAndView = new ModelAndView();
        try{
      // if(password.equals("1"))
        if (loginUtil.isLegal())
        {
            session.setAttribute("username", username);
            // 获取用户权限
            session.setAttribute("authority", authorityService.getAuthorityByUserName(username));
            ServletContext application = session.getServletContext();  
            //添加至用户列表
            List<Map<String,String>> userList =(List<Map<String, String>>) application.getAttribute("userList");  
            if(userList == null){  
              userList = new ArrayList<Map<String,String>>();  
              application.setAttribute("userList",userList);  
                                }  
            Map<String,String> map=new HashMap<String,String>();
            map.put("name",username);
            map.put("authority",session.getAttribute("authority")+"");
            map.put("logintime",Long.toString(System.currentTimeMillis()));
            userList.add(map);
            application.setAttribute("userList",userList); 
            
            modelAndView.addObject("basicDataUrl", Constant.basicDataUrl);
            modelAndView.setViewName("welcome");
            //直接获取所有分类的值，填进去
          Cache.populationsMap.put(Constant.ALL_TAG_ID, HttpClientUtil.getAllTags());
          Cache.populationsMap.put(Constant.ALL_TAG_ID, 100L);
            List<Classify> childs = tagAdminService.getAllChildClassifies();
            for(Classify child: childs){
         Cache.populationsMap.put(child.getClassifyId(), HttpClientUtil.getTagsByChildId(child.getClassifyId()));
         //Cache.populationsMap.put(child.getClassifyId(), 10L);
            }
        }
        else
        {
            modelAndView.setViewName("login");
        }
        }
        catch(Exception e){
        e.printStackTrace();
        logger.error(e.getMessage(),e); 
        }
        return modelAndView;
    }
    
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session)
    {
        Constant.username = "";
        Constant.password = "";
        session.invalidate();
        return "login";
    }
    @RequestMapping(value = "/toWelcome")
    public ModelAndView toWelcome(HttpSession session)
    {
        ModelAndView modelAndView = new ModelAndView();
        if (session.getAttribute("username") != null)
        {
            modelAndView.addObject("basicDataUrl", Constant.basicDataUrl);
            modelAndView.setViewName("welcome");
        }
        else
        {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }
    @RequestMapping(value = "/toMain")
    public ModelAndView toMain(HttpSession session)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        return modelAndView;
    }
    @RequestMapping(value = "/toHeader")
    public ModelAndView toHeader(HttpSession session)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("common/header");
        return modelAndView;
    }
    @RequestMapping(value = "/toBakMain")
    public ModelAndView toBakMain(HttpSession session)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bakMain");
        return modelAndView;
    }
    @RequestMapping(value = "/toHeader2")
    public ModelAndView toHeader2(HttpSession session)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("header2");
        return modelAndView;
    }
    @RequestMapping(value = "/toTagAdmin")
    public ModelAndView toTagAdmin(@RequestParam(value = "currentParentId", required = false)
    String currentParentId)
    {
        ModelAndView modelAndView = new ModelAndView();
        List<Classify> parentClassifies = new ArrayList<Classify>();
        parentClassifies = tagAdminService.getParentClassifies();
        
        if (parentClassifies.size() != 0)
        {
            if (currentParentId != null)
            {
                modelAndView.addObject("currentParentId", currentParentId);
            }
            else
            {
                modelAndView.addObject("currentParentId", parentClassifies.get(0).getClassifyId());
            }
        }
        
        modelAndView.addObject("parentClassifies", parentClassifies);
        modelAndView.setViewName("tagAdmin");
        return modelAndView;
    }
    
    @RequestMapping(value = "/toTagView")
    public ModelAndView toTagView()
    {
        ModelAndView modelAndView = new ModelAndView();
        
        List<Classify> parenClassifies = tagAdminService.getParentClassifies();
        modelAndView.addObject("parentClassifies", parenClassifies);
        
        modelAndView.setViewName("tagView");
        return modelAndView;
    }
    
    @RequestMapping(value = "/toTagIntegration")
    public ModelAndView toTagIntegration()
    {
        ModelAndView modelAndView = new ModelAndView();
        
        List<Classify> parenClassifies = tagAdminService.getParentClassifies();
        modelAndView.addObject("parentClassifies", parenClassifies);
        
        modelAndView.setViewName("tagIntegration");
        return modelAndView;
    }
    
    @RequestMapping(value = "/toComplexSearch")
    public ModelAndView toComplexSearch(@RequestParam(value = "currentParentId", required = false)
    String currentParentId)
    {
        ModelAndView modelAndView = new ModelAndView();
        List<Classify> parentClassifies = new ArrayList<Classify>();
        parentClassifies = tagAdminService.getParentClassifies();
        
        if (parentClassifies.size() != 0)
        {
            if (currentParentId != null)
            {
                modelAndView.addObject("currentParentId", currentParentId);
            }
            else
            {
                modelAndView.addObject("currentParentId", parentClassifies.get(0).getClassifyId());
            }
        }
        
        modelAndView.addObject("parentClassifies", parentClassifies);
        modelAndView.setViewName("complexSearch");
        return modelAndView;
    }
    
    @RequestMapping(value = "/toTagInsight")
    public ModelAndView toTagInsight(@RequestParam(value = "tagId", required = false)String tag_id,
    		@RequestParam(value = "tagName", required = false)String tag_name)
    throws ClientProtocolException, IOException
    {
    	 //tag_name=new String(tag_name.getBytes("ISO-8859-1"),"UTF-8");
    	 ModelAndView modelAndView = new ModelAndView();
    	//变量，要映射到result中
   	     Long tagNum;
   	     List<Classify> parents=new ArrayList<Classify>(); 
         List<Long> childids=new ArrayList<Long>(); 
         List<Long> tagids=new ArrayList<Long>(); 
         
         Map<Long,List<Classify>> classify_relation =new HashMap<Long,List<Classify>>();
         Map<Long,List<Tag>> tag_classify =new HashMap<Long,List<Tag>>();
         
         Map<Long,Long> tag_population=new HashMap<Long,Long>();
         Map<Long,Long> child_population=new HashMap<Long,Long>();
         Map<Long,Long> parent_population=new HashMap<Long,Long>();
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
        Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
        //QueryBuilder filterbuilder = QueryBuilders.termQuery("_all",tag_id);
		 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,FilterBuilders.termFilter("taglist",tag_id));
		@SuppressWarnings("deprecation")
		//统计标签及其覆盖度
		SearchResponse response = client.prepareSearch("dmp")
				.setTypes("imei")
				.setQuery(filterbuilder)
				.addAggregation(AggregationBuilders.terms("tagsgroup").field("taglist").size(Integer.MAX_VALUE))
		        .execute()
		        .actionGet();
		Aggregations a=response.getAggregations();
		Terms term = a.get("tagsgroup");
		for(Bucket bc:term.getBuckets()){
			String name=""+bc.getKey();
			String num=""+bc.getDocCount();
			if(name.equals(tag_id)) continue;
			tagids.add(Long.valueOf(name));
			tag_population.put(Long.valueOf(name),Long.valueOf(num));
		//	System.out.println("标签:"+name+"覆盖度:" + num);
		}
		
		response = client.prepareSearch("dmp")
   				.setTypes("imei")
   				.setQuery(filterbuilder)
   		        .execute()
   		        .actionGet();
       Long pul=response.getHits().getTotalHits();
       String tag_pul=pul+"";
       
		//统计二级分类及其覆盖度
		response = client.prepareSearch("dmp")
				.setTypes("imei")
				//.setPostFilter(filterbuilder)
				.setQuery(filterbuilder)
				.addAggregation(AggregationBuilders.terms("classify").field("tag.classify").size(Integer.MAX_VALUE))
				//.addFacet( FacetBuilders.termsFacet("classify").field("classify").size(Integer.MAX_VALUE).facetFilter(filterbuilder))
		        .execute()
		        .actionGet();
	    //f = response.getFacets();
		//facet = (TermsFacet)f.getFacets().get("classify");
		 a=response.getAggregations();
	     term = a.get("classify");
		//for(TermsFacet.Entry tf :facet.getEntries()){
	     for(Bucket bc:term.getBuckets()){
			String name=""+bc.getKey();
			String num=""+bc.getDocCount();
			childids.add(Long.valueOf(name));
			child_population.put(Long.valueOf(name),Long.valueOf(num));
		//	System.out.println("二级分类:"+name+"覆盖度:" + num);
		}
		//统计标签和二级分类的关系
		for(Long tagid:tagids)
		{
			List<Classify> temp=tagAdminService.getChildClassifiesByTag(tagid);
			for(Classify child:temp){
				if(!tag_classify.containsKey(child.getClassifyId())) tag_classify.put(child.getClassifyId(),new ArrayList<Tag>());
			       tag_classify.get(child.getClassifyId()).add(tagAdminService.getTag(tagid));
			}
		/*	for(Long childid:childids) 
			{
				if(tagAdminService.isRelation(childid,tagid))
				{
				   if(!tag_classify.containsKey(childid)) tag_classify.put(childid,new ArrayList<Tag>());
			       tag_classify.get(childid).add(tagAdminService.getTag(tagid));
				}
			}*/
		}
		for(Long childid:childids)
			if(!tag_classify.containsKey(childid)) tag_classify.put(childid,new ArrayList<Tag>());
           //获取一级分类
           for(Long childid:childids)
           {
           	Classify parenttemp=tagAdminService.getParentByChild(childid);
            boolean add=true;
 		    for(Classify parent:parents) 
 		       {
 			   if(parenttemp.getClassifyId().equals(parent.getClassifyId())) {add=false;break;}
 		        }
 		    if(add==true) parents.add(parenttemp);
           //	if(!parents.contains(parent)) parents.add(parent);
           	if(!classify_relation.containsKey(parenttemp.getClassifyId())) classify_relation.put(parenttemp.getClassifyId(),new ArrayList<Classify>());
           	classify_relation.get(parenttemp.getClassifyId()).add(tagAdminService.getChildById(childid));
           }
           //统计一级分类覆盖率
          // for(Classify parent:parents) parent_population.put(parent.getClassifyId(),1000L);
   		response = client.prepareSearch("dmp")
   				.setTypes("imei")
   				.setQuery(filterbuilder)
   				.addAggregation(AggregationBuilders.terms("parents").field("tag.parent").size(Integer.MAX_VALUE))
   		        .execute()
   		        .actionGet();
   		  a=response.getAggregations();
   	     term = a.get("parents");
   	     for(Bucket bc:term.getBuckets()){
   			String name=""+bc.getKey();
   			String num=""+bc.getDocCount();
   			parent_population.put(Long.valueOf(name),Long.valueOf(num));
   			//System.out.println("一级分类:"+name+"覆盖度:" +num);
   		}    
       client.close();
       modelAndView.addObject("parents",parents);
       modelAndView.addObject("classify_relation",classify_relation);
       modelAndView.addObject("tag_classify",tag_classify);
       modelAndView.addObject("tag_population",tag_population);
       modelAndView.addObject("child_population",child_population);
       modelAndView.addObject("parent_population",parent_population);
       modelAndView.addObject("tag_name",tag_name);
       modelAndView.addObject("tag_id",tag_id);
       modelAndView.addObject("tag_pul",tag_pul);
       modelAndView.setViewName("tagInsight");
       return modelAndView;
    	
    }
    @RequestMapping(value = "/adminSearch")
    public ModelAndView adminSearch(@RequestParam(value = "tagName", required = false)
    String tagName)
    {
        ModelAndView m = new ModelAndView();
        List<Tag> tags = new ArrayList<Tag>();
        if (!tagName.equals(""))
        {
            tags = tagAdminService.getTagsLike(tagName);
        }
        List<Classify> keys = new ArrayList<Classify>();
        List<Long> keyIds = new ArrayList<Long>();
        Map<Long, List<Tag>> resultMap = new HashMap<Long, List<Tag>>();
        
        for (Tag tag : tags)
        {
            List<Classify> childClassifies = tagAdminService.getChildClassifiesByTag(tag.getTagId());
            for (Classify childClassify : childClassifies)
            {
                if (!keyIds.contains(childClassify.getClassifyId()))
                {
                    keyIds.add(childClassify.getClassifyId());
                    keys.add(childClassify);
                }
                List<Tag> tmp = resultMap.get(childClassify.getClassifyId());
                if (tmp == null)
                {
                    tmp = new ArrayList<Tag>();
                    tmp.add(tag);
                    resultMap.put(childClassify.getClassifyId(), tmp);
                }
                else if (!tmp.contains(tag))
                {
                    tmp.add(tag);
                    resultMap.put(childClassify.getClassifyId(), tmp);
                }
            }
        }
        // 填充resultMap2
        Map<Classify, List<Tag>> resultMap2 = new HashMap<Classify, List<Tag>>();
        for (Classify childClassify : keys)
        {
            resultMap2.put(childClassify, resultMap.get(childClassify.getClassifyId()));
        }
        
        m.setViewName("tagAdminSearch");
        m.addObject("resultMap", resultMap2);
        m.addObject("allChildClassifies", tagAdminService.getAllChildClassifies());     
        return m;
    }
    @RequestMapping(value = "/adminHint")
    public void adminHint(@RequestParam(value = "inputInfo", required = false)
    String inputInfo, HttpServletResponse response)
    {
        List<Tag> cacheTags = tagAdminService.getTagsLike(inputInfo);
        
        List<String> hints = new ArrayList<String>();
        for (Tag tag : cacheTags)
        {
            hints.add(tag.getTagName());
        }
        String tagsJson = JSON.toJSONString(hints, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagsJson);
    }
    
}
