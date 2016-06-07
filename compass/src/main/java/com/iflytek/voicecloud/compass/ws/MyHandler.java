package com.iflytek.voicecloud.compass.ws;

import java.io.File;
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
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
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

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;

import com.caucho.websocket.*;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

public class MyHandler implements WebSocketListener
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
    	//公共变量
       Long redisId=Long.valueOf(text[3]);//数据库中id
       String listid="";
       for(int i=0;i<tagIds.length;i++) listid=listid+tagIds[i]+",";
       listid=listid.substring(0,listid.length()-1);
       BlockingQueue<String> dvcQueue = new LinkedBlockingQueue<String>(10000); //生产池
       try{
     Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
   	Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
   	BoolFilterBuilder bool=FilterBuilders.boolFilter();
   	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("_all",tagIds[i]));
   	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
   	
      //记录开始时间
      Date day=new Date();
      SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("starttime:"+df.format(day));
      
      Thread p=new Thread(new Producer(dvcQueue,client,filterbuilder),"producer");
      Consumer c=new Consumer(dvcQueue,pout,reader,listname,listid,redisId);
      Thread c1=new Thread(c,"consumer1");
      Thread c2=new Thread(c,"consumer2");
      Thread c3=new Thread(c,"consumer3");
      Thread c4=new Thread(c,"consumer4");
      Thread c5=new Thread(c,"consumer5");
      p.start();
      c1.start();
      c2.start();
      c3.start();
      c4.start();
      c5.start();
      //countDownLatch.await () ;//等待所有子进程结束
        //记录结束时间
     /* day=new Date();
      df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("endtime:"+df.format(day));
       //通知客户端
         pout.print("redis"+listname+"&&"+redisId);
         pout.close();
         reader.close();*/
       }
       catch(Exception e)
          {
    	   e.printStackTrace();
    	   pout.print("error"+listname);
           pout.close();
           reader.close();
          }
       }
       
       //file消息
       else {
    	   Date day=new Date();
           SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           System.out.println("starttime:"+df.format(day));
           
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
       	Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
       	BoolFilterBuilder bool=FilterBuilders.boolFilter();
       	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("_all",tagIds[i]));
       	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
           SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
          .setScroll(TimeValue.timeValueMinutes(2))
          .setQuery(filterbuilder)
          .setSize(500)
          .execute()
          .actionGet();
          String dvc="";
          long num=0L;//当前已写入用户数
          int max=0;//当前sheet累计值
          int sheetnum=0;
          SearchHits hits ;
          SearchHit[] searchHists ;
          try{
       	   //初始化表格
       	    WritableWorkbook wb;
       		wb = Workbook.createWorkbook(new File(listname+".xls"));
    			WritableSheet ws = wb.createSheet(sheetnum+"", sheetnum);
    			CellView cellView = new CellView();  cellView.setAutosize(true); //设置自动大小    
    		    ws.setColumnView(0, cellView);//根据内容自动设置列宽
    		    WritableCellFormat cellFormat = new WritableCellFormat();  
    		    cellFormat.setAlignment(jxl.format.Alignment.CENTRE);   
    			Label label= new Label(0, 0, "设备号");
    		    label.setCellFormat(cellFormat); 
    			ws.addCell(label);
    			int row=1;
          while(true){
          hits = searchResponse.getHits();
          searchHists = hits.getHits(); 
          if(searchHists.length==0) 
             {
       	   client.close();
       	   wb.write();
   		   wb.close();
       	   break;
       	     }
          //溢出换sheet
          if(max>=65000) {
        	  max=0;
        	  sheetnum++;
        	 ws = wb.createSheet(sheetnum+"", sheetnum);
  		    ws.setColumnView(0, cellView);//根据内容自动设置列宽
          }
          num+=searchHists.length;
          max+=searchHists.length;
          System.out.println(num);
          for(SearchHit hint : searchHists ){
       	       dvc=hint.getSource().get("idtype")+"_"+(String) hint.getSource().get("did");
       	       //写入设备号
       	       label = new Label(0, row%65001, dvc);
   				ws.addCell(label);
   				row++;
                                             }
               searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
       	      .setScroll(TimeValue.timeValueMinutes(2))
       	      .execute()
       	      .actionGet();
                     }
          day=new Date();
          df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          System.out.println("endtime:"+df.format(day));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		catch (RowsExceededException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (WriteException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
          //通知客户端
          pout.print("files"+listname);
          pout.close();
          reader.close();
     }
}
}
class Producer implements Runnable{

	 private BlockingQueue<String> dvcQueue;
	 private QueryBuilder filterbuilder;
	 private Client client;
	 Producer(BlockingQueue<String> dvcQueue,Client client,QueryBuilder filterbuilder) 
	   {
	        this.dvcQueue = dvcQueue;
            this.filterbuilder=filterbuilder;
            this.client=client;
	    }
	
	@Override
	public void run() 
	{
		SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
				      .setScroll(TimeValue.timeValueMinutes(2))
				      .setQuery(filterbuilder)
				      .setSize(200)
				      .execute()
				      .actionGet();
			 while(true){
			      SearchHits hits = searchResponse.getHits();
			      SearchHit[] searchHists = hits.getHits(); 
			      if(searchHists.length==0) {client.close();break;}
			      for(SearchHit hint : searchHists ){
			   	     String dvc=hint.getSource().get("idtype")+"_"+(String) hint.getSource().get("did");
			   	       try 
			   	       {
						dvcQueue.put(dvc);
					    } catch (InterruptedException e) 
					    {
						e.printStackTrace();
					    }
			                                         }
			           searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
			   	      .setScroll(TimeValue.timeValueMinutes(2))
			   	      .execute()
			   	      .actionGet();
			                 }
		//test
	/*	for(int i=0;i<1000;i++)
		{
			try {
				dvcQueue.put("imei_3538630706"+i);
				System.out.println("producer:"+i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
			 try {
				dvcQueue.put("end");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
class Consumer implements Runnable{

	 private BlockingQueue<String> dvcQueue;
	 private PrintWriter pout;
	 private Reader reader;
	 private String listname;
	 private Long redisId;
	 private String listid;
     private JedisCluster jedis;
	 Consumer(BlockingQueue<String> dvcQueue,PrintWriter pout,Reader reader,String listname,String listid,Long redisId) 
	   {
	        this.dvcQueue = dvcQueue;
            this.pout=pout;
            this.reader=reader;
            this.listname=listname;
            this.listid=listid;	 
            this.redisId=redisId;
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            jedisClusterNodes.add(new HostAndPort(Constant.redisUrl,6000));
            this.jedis = new JedisCluster(jedisClusterNodes);
       }
	
	@Override
	public void run() 
	{
		while(true)
		{
			String dvc="";
			try {
		    dvc = dvcQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(dvc.equals("end")) break;
             //Jedis jedis = new Jedis("172.16.154.54");
				dvc="*"+dvc;
				if (jedis.exists(dvc)) jedis.del(dvc);
			 	jedis.setex(dvc,Constant.redisTime,listid);
			 	System.out.println(Thread.currentThread().getName()+dvc);
		}
		 Date day=new Date();
	      SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      System.out.println("endtime:"+df.format(day));
	       //通知客户端
	         pout.print("redis"+listname+"&&"+redisId);
	         pout.close();
	         try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   //countDownLatch.countDown();
	   //System.out.println(Thread.currentThread().getName()+"end");
	}
	
	
}