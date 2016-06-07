package com.iflytek.voicecloud.compass.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
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
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.caucho.websocket.*;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.service.ITagAdminService;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
public class MyHandler2 implements WebSocketListener
{
	 public ITagAdminService tagAdminService ;
@Override
public void onClose(WebSocketContext arg0) throws IOException {
	// 页面关闭时socket关闭
	System.out.println("close");
}

@Override
public void onDisconnect(WebSocketContext arg0) throws IOException {
	// 长时间无动作关闭
	System.out.println("disconnect");
}

@Override
public void onReadBinary(WebSocketContext arg0, InputStream arg1)
		throws IOException {
	// TODO Auto-generated method stub
	
}
@Override
public void onStart(WebSocketContext arg0) throws IOException {
	// TODO Auto-generated method stub
	System.out.println("connect");
}

@Override
public void onTimeout(WebSocketContext arg0) throws IOException {
	// TODO Auto-generated method stub
	System.out.println("timeout");
}
@Override
public void onReadText(WebSocketContext context, Reader reader) throws IOException {
	// TODO Auto-generated method stub
	   PrintWriter pout=context.startTextMessage();
	   int ch;
	   String msg="";
	   while((ch=reader.read())>=0) msg+=(char)ch;
	   System.out.println("receivemsg:"+msg);
	   String []text=msg.split("&&");
       String tagIds[]=text[1].split("%%");
       String tagNames[]=text[2].split("%%");
       String listname="";
       for(int i=0;i<tagNames.length;i++) listname=listname+tagNames[i]+"-";
       listname=listname.substring(0,listname.length()-1);
       
       //redis消息
    if(text[0].equals("redis")){
       Long dvcNum=Long.valueOf(text[4]);
       Long redisId=Long.valueOf(text[3]);//数据库中id
     //公共变量
       String listid="";
       for(int i=0;i<tagIds.length;i++) listid=listid+tagIds[i]+",";
       listid=listid.substring(0,listid.length()-1);
       //disruptor
       int threadnum=Constant.threadNum;
       CountDownLatch countDownLatch=new CountDownLatch(1);
       EventFactory<StringEvent> eventFactory = new StringEventFactory();
       int ringBufferSize = 1024 * 2;  
       ExecutorService executor = Executors.newFixedThreadPool(threadnum);
       RingBuffer<StringEvent> ringBuffer=RingBuffer.createSingleProducer(eventFactory, ringBufferSize, new YieldingWaitStrategy());
       
       try{  
    Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
   	Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
   	BoolFilterBuilder bool=FilterBuilders.boolFilter().cache(true);
   	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("taglist",tagIds[i]));
   	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
   	
      //记录开始时间
   	Long start= System.currentTimeMillis();
      //创建多个jedis
      int jedisNum=Constant.jedisNum;
      Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
      jedisClusterNodes.add(new HostAndPort(Constant.redisUrl,6000));
      JedisCluster[] jedis=new JedisCluster[jedisNum];
      {
    	  for(int i=0;i<jedisNum;i++) jedis[i]= new JedisCluster(jedisClusterNodes);
      }
      //JedisCluster jedis = new JedisCluster(jedisClusterNodes);
     //测试redis连通性
      try{
      jedis[0].setex("test",10,"test");
      }catch(JedisConnectionException e){
   	      e.printStackTrace();
   	      pout.print("error"+listname+"&&"+redisId);
          pout.close();
          reader.close();
                                        }
      
     RedisHandler[] handlers = new RedisHandler[threadnum];
  	{
  		for(int i=0;i<threadnum;i++) handlers[i]=new RedisHandler(countDownLatch,listid,jedis[i%jedisNum]);
  		//for(int i=0;i<threadnum;i++) handlers[i]=new RedisHandler(countDownLatch,listid);
  	}
    
  	WorkerPool<StringEvent> workerPool=new WorkerPool<StringEvent>(ringBuffer,  ringBuffer.newBarrier(), new IgnoreExceptionHandler(), handlers);  
  	ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
     workerPool.start(executor); 
      //开始生产
      SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
    		  .setSearchType(SearchType.SCAN)
    		  .setFetchSource(new String[]{"idtype","did","taglist"},null)
		      .setScroll(TimeValue.timeValueMinutes(30))
		      .setQuery(filterbuilder)
		      .setSize(50)
		      .execute()
		      .actionGet();
      long count=0L;
	 while(true){
		  searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
		   	      .setScroll(TimeValue.timeValueMinutes(30))
		   	      .execute()
		   	      .actionGet();
	      SearchHits hits = searchResponse.getHits();
	      SearchHit[] searchHists = hits.getHits(); 
	      if(searchHists.length==0) {client.close();break;}
	      for(SearchHit hint : searchHists ){
	   	     String dvc="*"+hint.getSource().get("idtype")+"_"+(String) hint.getSource().get("did");
	   	       long sequence = ringBuffer.next();
	   	       StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
	            event.set(dvc);
	    	    ringBuffer.publish(sequence);//发布事件；
	    	    count++;
	    	    if (count % 10000L == 0L)  System.out.println("produce:"+count); 
	                                         }
	         
	           }
          // test
           //写入结束标志
           long sequence = ringBuffer.next();
   	       StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
            event.set("end");
    	    ringBuffer.publish(sequence);//发布事件
		      //等待消费者处理结束
				countDownLatch.await();
				Thread.sleep(5000);
	    	     for(int i=0;i<jedisNum;i++)  jedis[i].close();
    	      workerPool.halt();
				Thread.sleep(2);
              executor.shutdown();
              //记录结束时间
              System.out.println("costTime:"+ (System.currentTimeMillis()-start));
		      //通知客户端
		         pout.print("redis"+listname+"&&"+redisId);
		         pout.close();
		         reader.close();
       }
         catch(Exception e)
         {
	        e.printStackTrace();
	        pout.print("error"+listname+"&&"+redisId);
            pout.close();
            reader.close();
         }
       }
    
    //file消息
    else {
    	 Long dvcNum=Long.valueOf(text[3]);
    	 //Long dvcNum=100000L;
          try{
	   //es初始配置
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
    	Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
    	BoolFilterBuilder bool=FilterBuilders.boolFilter().cache(true);
    	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("taglist",tagIds[i]));
    	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
    	 
    	 //disruptor
    	    int threadnum=(int) (dvcNum/65000+1);
    	     CountDownLatch countDownLatch=new CountDownLatch(threadnum);
    	      EventFactory<StringEvent> eventFactory = new StringEventFactory();
    	      int ringBufferSize = 1024 * 1024;  
    	      ExecutorService executor = Executors.newFixedThreadPool(threadnum);
    	      RingBuffer<StringEvent> ringBuffer=RingBuffer.createSingleProducer(eventFactory, ringBufferSize, new YieldingWaitStrategy());
    	      //记录开始时间
    	     Long start=System.currentTimeMillis();
    	      FileHandler[] handlers = new FileHandler[threadnum];
    	  	{
    	  		for(int i=0;i<threadnum;i++) handlers[i]=new FileHandler(i,countDownLatch,listname);
    	  	}
    	      
    	  	WorkerPool<StringEvent> workerPool=new WorkerPool<StringEvent>(ringBuffer,  ringBuffer.newBarrier(), new IgnoreExceptionHandler(), handlers);  
    	  	ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    	     workerPool.start(executor); 
    	      //开始生产
           SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
           .setFetchSource(new String[]{"idtype","did"}, null)
           .setSearchType(SearchType.SCAN)
           .setScroll(TimeValue.timeValueMinutes(30))
           .setQuery(filterbuilder)
           .setSize(50)
           .execute()
           .actionGet();
       while(true){
    	 searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
    	    	      .setScroll(TimeValue.timeValueMinutes(30))
    	    	      .execute()
    	    	      .actionGet();
       SearchHits hits = searchResponse.getHits();
       SearchHit[] searchHists = hits.getHits(); 
           if(searchHists.length==0) 
                   {
    	             client.close();
    	             break;
    	            }
       for(SearchHit hint : searchHists ){
    	       String dvc=hint.getSource().get("idtype")+"_"+(String) hint.getSource().get("did");
    	       long sequence = ringBuffer.next();
	   	       StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
	            event.set(dvc);
	    	    ringBuffer.publish(sequence);//发布事件；
                                          }
                  }
    	 //test
    	      /*for(int i=0;i<dvcNum;i++)
              {
       	       long sequence = ringBuffer.next();
	   	        StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
	            event.set("imei_35386307"+i);
	    	    ringBuffer.publish(sequence);//发布事件；
		      //  System.out.println("producer:"+i);
		      }*/
       //写入结束标志,保证通知到每个消费者
    	for(int i=0;i<threadnum;i++)
              {
         long sequence = ringBuffer.next();
	     StringEvent event = ringBuffer.getPreallocated(sequence);
        event.set("end");
	    ringBuffer.publish(sequence);//发布事件；
              }
	      //等待消费者结束
          countDownLatch.await();
	      workerPool.halt();
	      Thread.sleep(2);
          executor.shutdown();	
          //记录结束时间
          System.out.println("costTime:"+ (System.currentTimeMillis()-start));
          //压缩文件
          File[] srcfiles = new File[threadnum];
        	{
        		for(int i=0;i<threadnum;i++) srcfiles[i]=new File(listname+"("+i+")"+".xls");
        	}
        	File zipfile=new File(listname+".rar");
        	
          byte[] buf = new byte[1024];  
          try {  
              ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));  
              for (int i = 0; i < threadnum; i++) {  
                  FileInputStream in = new FileInputStream(srcfiles[i]);  
                  out.putNextEntry(new ZipEntry(srcfiles[i].getName()));  
                  int len;  
                  while ((len = in.read(buf)) > 0) {  
                      out.write(buf, 0, len);  
                                                    }  
                  out.closeEntry();  
                  in.close();  
                                                         }  
              
              out.close();  
              //删除源文件
              for(int i=0;i<threadnum;i++) srcfiles[i].delete();
          } catch (IOException e) {  
              e.printStackTrace();  
                                   }  
          //通知客户端
           pout.print("files"+listname);
           pout.close();
           reader.close();
          
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 			 //通知客户端
            pout.print("errof"+listname);
            pout.close();
            reader.close();
 		} 
  }//else    
}
}