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
import java.util.Iterator;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.service.ITagAdminService;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
public class MyHandler3 implements WebSocketListener
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
       Long redisId=Long.valueOf(text[3]);//数据库中id
       Long dvcNum=Long.valueOf(text[4]);
       //redis消息
    if(text[0].equals("redis")){
      String prefix=text[5];
       int threadNum,redisNum;
      if(dvcNum>300000){threadNum=200;redisNum=25;}
      else if(dvcNum>100000) {threadNum=100;redisNum=10;}
      else {threadNum=1;redisNum=1;}
     //  threadNum=Constant.threadNum;
      // redisNum=Constant.jedisNum;
     //公共变量
       String listid="";
       for(int i=0;i<tagIds.length;i++) listid=listid+tagIds[i]+",";
       listid=listid.substring(0,listid.length()-1);
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
    /* RedisHandler[] handlers = new RedisHandler[threadnum];
    	{
    		for(int i=0;i<threadnum;i++) handlers[i]=new RedisHandler(countDownLatch,listid);
    	} */
  	WorkerPool<StringEvent> workerPool=new WorkerPool<StringEvent>(ringBuffer,  ringBuffer.newBarrier(), new IgnoreExceptionHandler(), handlers);  
  	ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
     workerPool.start(executor); 
     //记录开始时间
    Long start=System.currentTimeMillis();
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
	    	    if (count % 10000L == 0L)  System.out.println("produce:"+count+"  dvc:"+dvc); 
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
              //记录结束时间
		      System.out.println("costTime:"+ (System.currentTimeMillis()-start));
		      //通知客户端
		         pout.print("redis"+listname+"&&"+redisId);
		         pout.close();
		         reader.close();
       }
         catch(JedisConnectionException|ElasticsearchException|InterruptedException e)
         {
        	e.printStackTrace();
	        pout.print("error"+listname+"&&"+redisId);
            pout.close();
            reader.close();
         }
       }//if
    else {
         try{ 
   	 //disruptor
   	    //int threadnum=(int) (dvcNum/65000+1);
        	 int  threadnum=1;
   	     CountDownLatch countDownLatch=new CountDownLatch(threadnum);
   	      EventFactory<StringEvent> eventFactory = new StringEventFactory();
   	      int ringBufferSize = 1024 * 1024;  
   	      ExecutorService executor = Executors.newFixedThreadPool(threadnum);
   	      RingBuffer<StringEvent> ringBuffer=RingBuffer.createSingleProducer(eventFactory, ringBufferSize, new YieldingWaitStrategy());
   	      FileHandler[] handlers = new FileHandler[threadnum];
   	  	{
   	  		for(int i=0;i<threadnum;i++) handlers[i]=new FileHandler(i,countDownLatch,listname);
   	  	}
   	      
   	  	WorkerPool<StringEvent> workerPool=new WorkerPool<StringEvent>(ringBuffer,  ringBuffer.newBarrier(), new IgnoreExceptionHandler(), handlers);  
   	  	ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
   	     workerPool.start(executor); 
   	     //开始生产
   	   //记录开始时间  
   	   Long start=System.currentTimeMillis();
   	  ExportResult exportResult=new ExportResult(tagIds);
   	  Long count=0L;
      Iterator<Map<String,Object>> it=exportResult.iterator();//获得匿名内部类的对象，即获得遍历器
      while(it.hasNext()){//判断游标当前指向的是不是尾部
                count++;
                Map<String,Object> document=it.next();
                String dvc=(String)document.get("idtype")+"_"+(String)document.get("did");
                long sequence = ringBuffer.next();
 	   	        StringEvent event = ringBuffer.getPreallocated(sequence);//获取该序号对应的事件对象；
 	            event.set(dvc);
 	            ringBuffer.publish(sequence);//发布事件；
 	    	    if (count % 10000L == 0L)  System.out.println("produce:"+count); 
 	                    }
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
       /*  File[] srcfiles = new File[threadnum];
       	{
       		for(int i=0;i<threadnum;i++) srcfiles[i]=new File(listname+"("+i+")"+".xls");
       	}
       	File zipfile=new File(listname+".zip");
       	
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
             //for(int i=0;i<threadnum;i++) srcfiles[i].delete();
            } catch (IOException e) {  
             e.printStackTrace();  
                                  }  */
         //通知客户端
          pout.print("files"+listname+"&&"+redisId);
          pout.close();
          reader.close();
         
		} catch (Exception e) {
			// TODO Auto-generated catch block
		   e.printStackTrace();
           pout.print("errof"+listname+"&&"+redisId);
           pout.close();
           reader.close();
		} 
 }//else    
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
//file消费者
class FileHandler implements WorkHandler<StringEvent>
{
   private WritableWorkbook wb;
   private WritableSheet ws;
   private int row;
   private int sheetnum;
   private Label label;
   private boolean writeable;
   private CountDownLatch countDownLatch;
   FileHandler(int fileNum,CountDownLatch countDownLatch,String listname){
	   try {
		this.wb=Workbook.createWorkbook(new File(listname+"("+fileNum+")"+".xls"));
	   this.ws =this.wb.createSheet(sheetnum+"", sheetnum);
      CellView cellView = new CellView();  
  	   cellView.setAutosize(true); //设置自动大小    
      this.ws.setColumnView(0, cellView);//根据内容自动设置列宽
      WritableCellFormat cellFormat = new WritableCellFormat();  
	  cellFormat.setAlignment(jxl.format.Alignment.CENTRE);     
      Label label= new Label(0, 0, "设备号");
      label.setCellFormat(cellFormat); 
  	  this. ws.addCell(label);
	       }catch (Exception e) {
			e.printStackTrace();
	        } 
	   this.countDownLatch=countDownLatch;
	   this.sheetnum=0;
       this.row=1;
       this.writeable=true;
       
   }
@Override
public void onEvent(StringEvent arg0) throws Exception {
  if(writeable){
     String dvc=arg0.get();
    // System.out.println(threadNum+"consumer:"+dvc+" row"+row);
	 if(!dvc.equals("end"))
	 {
		//写入设备号
	     label = new Label(0, row, dvc);
	     ws.addCell(label);
          row++;  
          //添加新的sheet
           if(row>65000) {
        	   sheetnum++;
        	   row=0;
        	   ws = wb.createSheet(sheetnum+"", sheetnum);
        	   CellView cellView = new CellView();  
          	   cellView.setAutosize(true); //设置自动大小    
               ws.setColumnView(0, cellView);//根据内容自动设置列宽
                        }
	 }
	 else{
	  wb.write();
	  wb.close();
	  //放弃竞争
	  writeable=false;
	  countDownLatch.countDown();
	     }
	                                      }
                                                     }
}
