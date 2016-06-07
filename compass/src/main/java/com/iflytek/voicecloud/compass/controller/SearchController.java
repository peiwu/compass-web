package com.iflytek.voicecloud.compass.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.aspect.Cache;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.HttpClientUtil;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.ClassifyDto;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagStatisticsVo;
import com.iflytek.voicecloud.compass.service.ITagAdminService;
import com.iflytek.voicecloud.compass.ws.ExportResult;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;

@Controller
@RequestMapping(value = "/search")
public class SearchController
{
    @Autowired
    ITagAdminService tagAdminService;
    
    /**
     * 搜索框提示
     * 
     * @param inputInfo
     */
    //由设备号获取tags
    @RequestMapping({"/dvcSearch"})
    public ModelAndView dvcSearch(HttpServletRequest request)
      throws ClientProtocolException, IOException
    {
      ModelAndView m = new ModelAndView();
      //获取设备号
      Long tagnum;
      String deviceNumber = request.getParameter("dvc").trim();
      //List<Long> tagIds = HttpClientUtil.getTagIdsFromDevice(deviceNumber);
      List<Long> tagIds = tagAdminService.getTagIdsFromDevice(deviceNumber);
      tagnum=Long.valueOf(tagIds.size());
      List<Tag> tags = new ArrayList<Tag>();
      if (!tagIds.isEmpty())
      {
        for (Long tagId : tagIds)
        {
          Tag tag = this.tagAdminService.getTag(tagId);
          tags.add(tag);
        }
      }
      m.addObject("resultList", tags);
      m.addObject("tagNum", tagnum);
      m.addObject("devName", deviceNumber);
      m.setViewName("devSearch");
      return m;
    }
    
    //查询多个标签的用户数
    @RequestMapping(value = "/getDvcByTags")
    public void getDvcByTags(@RequestParam(value = "tagIds") String ids,HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
    	Long dvcnum;
    	String result;
    	try{
    	String[] tagIds=ids.split("%%");
    	Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
    	Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
    	BoolFilterBuilder bool=FilterBuilders.boolFilter();
    	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("taglist",tagIds[i]));
    	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
       SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
 		      .setQuery(filterbuilder)
 		      .setSize(6)
 		      .execute()
 		      .actionGet();
       dvcnum=searchResponse.getHits().getTotalHits();
      result=dvcnum+"";
       client.close();
    	}catch(Exception e){
    		e.printStackTrace();
    		result="error";
    	}
      
        PrintWriterUtil.returnPrintWriterJson(response, result);
    }
    
     //文件导出
    @RequestMapping(value = "/exportFile")
    public void exportFile(@RequestParam(value = "listname")String filename,
        	HttpServletResponse response) throws IOException{
 		//下载excel
 		File file=new File(filename+"(0).xls");
 		response.reset();
 		//response.setCharacterEncoding("UTF-8");
 		response.setContentType("application/x-msdownload");
 		response.setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes("UTF-8"),"iso-8859-1")+".xls");  
 		response.setHeader("Content_Length", String.valueOf(file.length()));
 		FileInputStream in=new FileInputStream(file);
         OutputStream out = response.getOutputStream();
         int b = 0;  
         byte[] buffer = new byte[1024];  
         while ((b=in.read(buffer))!=-1){   
             out.write(buffer,0,b);  
                                         }  
         in.close();  
         //file.delete();
         out.flush();
         out.close();  	
    }
    
    @RequestMapping(value = "/checkPrefix")
    public void checkPrefix(@RequestParam(value = "prefix")String prefix,
    	HttpServletResponse response,HttpSession session) throws IOException{
    	String username=(String)session.getAttribute("username");
    	String result;
    	if(tagAdminService.checkPrefix(username,prefix)) result="ok";
    	else result="exist";
    	PrintWriterUtil.returnPrintWriterJson(response,result);
    }
    
    //向数据库中插入redis任务
    @RequestMapping(value = "/insertRedis")
    public void insertRedis(@RequestParam(value = "msg")String msg,
    	HttpServletResponse response,HttpSession session) throws IOException{
    	 //写入数据库
    	String username=(String)session.getAttribute("username");
    	String temp[]=msg.split("&&");
    	String tagIds[]=temp[0].split("%%");
    	String path="";
	       for(int i=0;i<tagIds.length;i++) path=path+tagIds[i]+"-";
	       path=path.substring(0,path.length()-1);
 	    Redis redis=new Redis();
 	    redis.setType(1);
        redis.setPath(path);
        redis.setPerson(username);
        redis.setState(0);
        redis.setNum(Long.valueOf(temp[1]));
        redis.setPrefix(temp[2]);
        Long writetime=System.currentTimeMillis();
        redis.setStartDate(writetime);
        try{
        tagAdminService.addRedis(redis);
        //返回id
        Long id=tagAdminService.getRedis(username,writetime).getId();
        PrintWriterUtil.returnPrintWriterJson(response,id.toString());
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
   
    //向数据库中插入file导出任务
    @RequestMapping(value = "/insertFile")
    public void insertFile(@RequestParam(value = "msg")String msg,
    	HttpServletResponse response,HttpSession session) throws IOException{
    	 //写入数据库
    	String username=(String)session.getAttribute("username");
    	String temp[]=msg.split("&&");
    	String tagIds[]=temp[0].split("%%");
    	String path="";
	       for(int i=0;i<tagIds.length;i++) path=path+tagIds[i]+"-";
	       path=path.substring(0,path.length()-1);
 	    Redis redis=new Redis();
 	    redis.setType(0);
        redis.setPath(path);
        redis.setPerson(username);
        redis.setState(0);
        redis.setNum(Long.valueOf(temp[1]));
        Long writetime=System.currentTimeMillis();
        redis.setStartDate(writetime);
        try{
        tagAdminService.addRedis(redis);
        //返回id
        Long id=tagAdminService.getRedis(username,writetime).getId();
        PrintWriterUtil.returnPrintWriterJson(response,id.toString());
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
  
    //修改任务完成状态
    @RequestMapping(value = "/changeRedis")
    public void changeRedis(@RequestParam(value = "id")String redisId,
    	HttpServletResponse response,HttpSession session) throws IOException{
    	  //修改数据库状态
    	long endtime=System.currentTimeMillis();
    	try{
        Redis oldredis= tagAdminService.getRedisById(Long.valueOf(redisId));
        long starttime=oldredis.getStartDate();
        oldredis.setState(1);
        oldredis.setEndDate(endtime);
        oldredis.setCostTime(endtime-starttime);
        tagAdminService.updateRedis(oldredis);
        PrintWriterUtil.returnPrintWriterJson(response,"");
    	}
    	catch(Exception e){
        	e.printStackTrace();
        }
    }

    //修改任务出错状态
    @RequestMapping(value = "/errorRedis")
    public void errorRedis(@RequestParam(value = "id")String redisId,
    	HttpServletResponse response,HttpSession session) throws IOException{
    	  //修改数据库状态
    	try{
    		  Redis oldredis= tagAdminService.getRedisById(Long.valueOf(redisId));
    	        oldredis.setState(2);
    	        tagAdminService.updateRedis(oldredis);
              PrintWriterUtil.returnPrintWriterJson(response,"");
    	}
    	catch(Exception e){
        	e.printStackTrace();
        }
    }
    //页面关闭后继续处理
    @RequestMapping(value = "/completeRedis")
    public void completeRedis(HttpServletResponse response,HttpSession session) throws IOException{
    	 //完成未完成的redis任务 
    	List<Redis> redislist=tagAdminService.getNoOkRedis((String)session.getAttribute("username"));
    	session.invalidate();
    	if(!redislist.isEmpty()){
    	for(Redis redis:redislist) 
    	{
    	    //写入redis
    		String path=redis.getPath();
    		String prefix=redis.getPrefix();
    		System.out.println("未完成："+path);
    		 String tagIds[]=path.split("-");
    		 String listid="";
    	       for(int i=0;i<tagIds.length;i++) listid=listid+tagIds[i]+",";
    	       listid=listid.substring(0,listid.length()-1);
    		 Long dvcNum=redis.getNum();
    	     int threadNum,redisNum;
    	     if(dvcNum>300000){threadNum=200;redisNum=25;}
    	      else if(dvcNum>100000) {threadNum=100;redisNum=10;}
    	      else {threadNum=1;redisNum=1;}
    	       try{
    	    	     Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
    	    	      jedisClusterNodes.add(new HostAndPort(Constant.redisUrl,6000));
    	    	      JedisCluster[] jedis=new JedisCluster[redisNum];
    	    	      {
    	    	    	  for(int i=0;i<redisNum;i++) jedis[i]= new JedisCluster(jedisClusterNodes);
    	    	      }
    	    	      //JedisCluster jedis = new JedisCluster(jedisClusterNodes);
    	    	     //测试redis连通性
    	    	      jedis[0].setex("test",10,"test");
    	    	      //disruptor
    	    	      CountDownLatch countDownLatch=new CountDownLatch(1);
    	    	      EventFactory<StringEvent> eventFactory = new StringEventFactory();
    	    	      int ringBufferSize = 1024 * 1024;  
    	    	      ExecutorService executor = Executors.newFixedThreadPool(threadNum);
    	    	      RingBuffer<StringEvent> ringBuffer=RingBuffer.createSingleProducer(eventFactory, ringBufferSize, new YieldingWaitStrategy());
    	    	     RedisHandler[] handlers = new RedisHandler[threadNum];
    	    	  	{
    	    	  		for(int i=0;i<threadNum;i++) handlers[i]=new RedisHandler(countDownLatch,listid,jedis[i%redisNum]);
    	    	  	}
    	    	  	WorkerPool<StringEvent> workerPool=new WorkerPool<StringEvent>(ringBuffer,  ringBuffer.newBarrier(), new IgnoreExceptionHandler(), handlers);  
    	    	  	ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    	    	     workerPool.start(executor); 
    	    	     ExportResult exportResult=new ExportResult(tagIds);
    	    	     Long count=0L;
    	    	     Iterator<Map<String,Object>> it=exportResult.iterator();//获得匿名内部类的对象，即获得遍历器
    	    	      //开始生产
    	    	     try{
    	    	     while(it.hasNext()){//判断游标当前指向的是不是尾部
    	    	               count++;
    	    	               Map<String,Object> document=it.next();
    	    	               String dvc=prefix+"#"+(String)document.get("idtype")+"_"+(String)document.get("did");
    	    	               long sequence = ringBuffer.next();
    	    		   	       StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
    	    		            event.set(dvc);
    	    		            ringBuffer.publish(sequence);//发布事件；
    	    		                    }
    	    	        }catch(ElasticsearchException e){
    	    	    	 //异常发生，做好关闭工作
    	    	    	 for(int i=0;i<redisNum;i++)  jedis[i].close();
    	    		      workerPool.halt();
    	    		      Thread.sleep(2);
    	    	         executor.shutdown();
    	    	    	 throw e;
    	    	     }
    	    	           //写入结束标志
    	    	           long sequence = ringBuffer.next();
    	    	   	       StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
    	    	            event.set("end");
    	    	    	    ringBuffer.publish(sequence);//发布事件
    	    			      //等待消费者处理结束
    	    				 countDownLatch.await();
    	    			     Thread.sleep(5000);
    	    	    	     for(int i=0;i<redisNum;i++)  jedis[i].close();
    	    	    	      workerPool.halt();
    	    			      Thread.sleep(2);
    	    	              executor.shutdown();
    	    			  
    		          //更新数据库
    	    	      long endtime=System.currentTimeMillis();
    	    	      long starttime=redis.getStartDate();
    	    	       redis.setEndDate(endtime);
    	    	       redis.setCostTime(endtime-starttime);
    		          redis.setState(1);
    		          tagAdminService.updateRedis(redis);
    	            } catch(Exception e){
    	            	e.printStackTrace();
    	            	redis.setState(2);
      		            tagAdminService.updateRedis(redis);
    	            	                }
    	}//for
    	                          }//if
    } //void
    
    /**
     * 搜索框提示
     * 
     * @param inputInfo
     */
    @RequestMapping(value = "/viewHint")
    public void viewHint(@RequestParam(value = "inputInfo", required = false)
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
    
    @RequestMapping(value = "/getTagStatistics")
    public void getTagStatistics(@RequestParam(value = "tagId")
    String tagIdStr, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long tagId = Long.parseLong(tagIdStr);
        Tag tag = tagAdminService.getTag(tagId);
        Map<String, List<Long>> result = tagAdminService.getTagStatistics(tag);
        String tagStatisticsDtoJson = JSON.toJSONString(result, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagStatisticsDtoJson);
    }
    
    /**
     * 显示所有标签
     * 
     * @param tagName
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/viewAllSearch")
    public ModelAndView viewAllSearch(@RequestParam(value = "tagName", required = false)
    String tagName, @RequestParam(value = "chooseStat", required = false)
    String chooseStat, @RequestParam(value = "currentPage", required = false)
    String currentPage)
        throws ClientProtocolException, IOException
    {
        ModelAndView m = new ModelAndView();
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        if (!tagName.equals(""))
        {
            List<Tag> tags = tagAdminService.getTagsLike(tagName);
            tagStatisticsVos = tagAdminService.getAllTagView(tags);
        }
        
        // 填充resultMap2
        Map<ClassifyDto, List<TagStatisticsVo>> resultMap2 = setResultMap(tagStatisticsVos);
        
        m.setViewName("tagViewSearch");
        m.addObject("resultMap", resultMap2);
        m.addObject("tagName", tagName);
        m.addObject("chooseStat", chooseStat);
        m.addObject("currentPage", currentPage);
        
        return m;
    }
    
    /**
     * 显示有效
     * 
     * @param tagName
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/viewEffectiveSearch")
    public ModelAndView viewEffectiveSearch(@RequestParam(value = "tagName", required = false)
    String tagName, @RequestParam(value = "chooseStat", required = false)
    String chooseStat, @RequestParam(value = "currentPage", required = false)
    String currentPage)
        throws ClientProtocolException, IOException
    {
        ModelAndView m = new ModelAndView();
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        if (!tagName.equals(""))
        {
            List<Tag> tags = tagAdminService.getTagsLike(tagName);
            tagStatisticsVos = tagAdminService.getEffectiveTagView(tags);
        }
        
        // 填充resultMap2
        Map<ClassifyDto, List<TagStatisticsVo>> resultMap2 = setResultMap(tagStatisticsVos);
        
        m.setViewName("tagViewSearch");
        m.addObject("resultMap", resultMap2);
        m.addObject("tagName", tagName);
        m.addObject("chooseStat", chooseStat);
        m.addObject("currentPage", currentPage);
        
        return m;
    }
    
    @RequestMapping("/viewDeviceAllSearch")
    public ModelAndView viewDeviceAllSearch(HttpServletRequest request)
        throws ClientProtocolException, IOException
    {
        ModelAndView m = new ModelAndView();
        
        String deviceNumber = request.getParameter("tagName").trim();
        String chooseStat = request.getParameter("chooseStat");
        String currentPage = request.getParameter("currentPage");
        // 对deviceNumber进行base64encoding
        
        String deviceNumberBase64 = new String(Base64.encodeBase64(deviceNumber.getBytes()), "UTF-8");
        
        List<Long> tagIds = HttpClientUtil.getTagIdsFromDevice(deviceNumberBase64);
        List<Tag> tags = new ArrayList<Tag>();
        if (!tagIds.isEmpty())
        {
            for (Long tagId : tagIds)
            {
                Tag tag = tagAdminService.getTag(tagId);
                tags.add(tag);
            }
        }
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getAllTagView(tags);

        Map<ClassifyDto, List<TagStatisticsVo>> resultMap2 = setResultMap(tagStatisticsVos);
        
        m.addObject("resultMap", resultMap2);
        m.addObject("tagName", deviceNumber);
        m.addObject("chooseStat", chooseStat);
        m.addObject("currentPage", currentPage);
        m.setViewName("tagViewSearch");
        return m;
    }
    
    @RequestMapping("/viewDeviceEffectiveSearch")
    public ModelAndView viewDeviceEffectiveSearch(HttpServletRequest request)
        throws ClientProtocolException, IOException
    {
        ModelAndView m = new ModelAndView();
        
        String deviceNumber = request.getParameter("tagName").trim();
        String chooseStat = request.getParameter("chooseStat");
        String currentPage = request.getParameter("currentPage");
        // 对deviceNumber进行base64encoding
        String deviceNumberBase64 = new String(Base64.encodeBase64(deviceNumber.getBytes()), "UTF-8");
        List<Long> tagIds = HttpClientUtil.getTagIdsFromDevice(deviceNumberBase64);
        List<Tag> tags = new ArrayList<Tag>();
        if (!tagIds.isEmpty())
        {
            for (Long tagId : tagIds)
            {
                Tag tag = tagAdminService.getTag(tagId);
                tags.add(tag);
            }
        }
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getEffectiveTagView(tags);
        
        Map<ClassifyDto, List<TagStatisticsVo>> resultMap2 = setResultMap(tagStatisticsVos);
        
        m.addObject("resultMap", resultMap2);
        m.addObject("tagName", deviceNumber);
        m.addObject("chooseStat", chooseStat);
        m.addObject("currentPage", currentPage);
        m.setViewName("tagViewSearch");
        return m;
    }
    /**
     * 填充resultMap
     * @param tagStatisticsVos
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private Map<ClassifyDto, List<TagStatisticsVo>> setResultMap(List<TagStatisticsVo> tagStatisticsVos)
        throws ClientProtocolException, IOException
    {
        List<Classify> keys = new ArrayList<Classify>();
        List<Long> keyIds = new ArrayList<Long>();
        Map<Long, List<TagStatisticsVo>> resultMap = new HashMap<Long, List<TagStatisticsVo>>();
        for (TagStatisticsVo tagStatisticsVo : tagStatisticsVos)
        {
            
            List<Classify> childClassifies = tagAdminService.getChildClassifiesByTag(tagStatisticsVo.getTagId());
            for (Classify childClassify : childClassifies)
            {
                if (!keyIds.contains(childClassify.getClassifyId()))
                {
                    keyIds.add(childClassify.getClassifyId());
                    keys.add(childClassify);
                }
                List<TagStatisticsVo> tmp = resultMap.get(childClassify.getClassifyId());
                if (tmp == null)
                {
                    tmp = new ArrayList<TagStatisticsVo>();
                    tmp.add(tagStatisticsVo);
                    resultMap.put(childClassify.getClassifyId(), tmp);
                }
                else if (!tmp.contains(tagStatisticsVo))
                {
                    tmp.add(tagStatisticsVo);
                    resultMap.put(childClassify.getClassifyId(), tmp);
                }
            }
        }
        Map<ClassifyDto, List<TagStatisticsVo>> resultMap2 = new HashMap<ClassifyDto, List<TagStatisticsVo>>();
        for (Classify childClassify : keys)
        {
            ClassifyDto childDto = new ClassifyDto();
            childDto.setClassifyDescription(childClassify.getClassifyDescription());
            childDto.setClassifyId(childClassify.getClassifyId());
            childDto.setClassifyName(childClassify.getClassifyName());
            childDto.setIsParent(childClassify.getIsParent());
            int rate = (int)(Cache.populationsMap.get(childClassify.getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID));
            childDto.setRate(rate);
            resultMap2.put(childDto, resultMap.get(childClassify.getClassifyId()));
        }
        return resultMap2;
    }
}

class StringEvent
{
    private String dvc;

    public void set(String dvc)
    {
        this.dvc = dvc;
    }
    public String get()
    {
        return dvc ;
    }
}
class StringEventFactory implements EventFactory<StringEvent>
{
    public StringEvent newInstance()
    {
        return new StringEvent();
    }
}
//redis消费者
class RedisHandler implements WorkHandler<StringEvent>
{
 private String listid;
 private JedisCluster jedis;
 private CountDownLatch countDownLatch;
 //private Jedis jedis;//test
 RedisHandler(CountDownLatch countDownLatch,String listid,JedisCluster jedis){
	   this.countDownLatch=countDownLatch;    
     this.listid=listid;
     this.jedis=jedis;
     // this.jedis = new Jedis("172.16.154.54");//test                                                        
                                                                             }
@Override
public void onEvent(StringEvent arg0) throws Exception {
	// TODO Auto-generated method stub
	 String dvc=arg0.get();
	   if(!dvc.equals("end")){
			try{
	 	jedis.setex(dvc,Constant.redisTime,listid);
	         }catch(JedisConnectionException e)
	              {
	        System.out.println("redis提前关闭");
		    //e.printStackTrace();
	        try{
		    Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		    jedisClusterNodes.add(new HostAndPort(Constant.redisUrl,6000));
		    jedis = new JedisCluster(jedisClusterNodes);
		 	jedis.setex(dvc,Constant.redisTime,listid);
		 	System.out.println("uncomplete:"+dvc);
		 	jedis.close();
	        }catch(JedisConnectionException e1){
	        	throw e1;
	        }
	              }
                            }
	 else  countDownLatch.countDown();
                                                       }                                                   
}