package com.iflytek.voicecloud.compass.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.http.client.ClientProtocolException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolFilterBuilder;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

@Controller
@RequestMapping(value = "/tagInsight")
public class TagInsightController {
	 @Autowired
	    ITagAdminService tagAdminService;
	  @SuppressWarnings("unchecked")
	  @RequestMapping(value = "/Export")
		 public void Export(@RequestParam(value = "exp-tagpath", required = false) String tagpath,
		 @RequestParam(value = "exp-tags", required = false) String tags,
		 @RequestParam(value = "exp-num", required = false) String tagnum,
		 @RequestParam(value = "exp-filename", required = false) String filename,
		 @RequestParam(value = "exp-need", required = false) String need,
		// @RequestParam(value = "exp-result", required = false) String result,
		 HttpServletResponse response)
				 throws ClientProtocolException, IOException
	    {
	         
		   //  tagpath=new String(tagpath.getBytes("iso-8859-1"),"UTF-8");
			 String[] taglist=tags.split("&&");
			 String[] namelist=new String[taglist.length];
			 String[] popularlist=new String[taglist.length];
			 String[] ratelist=new String[taglist.length];
			 for(int i=0;i<taglist.length;i++)
			 {
				 String[] temp=taglist[i].split("-");
				 namelist[i]=temp[1];
			     popularlist[i]=temp[2];
			     ratelist[i]=temp[3];
			 }
			//写入excel
	    	 WritableWorkbook wb;
	 		try {
	 			wb = Workbook.createWorkbook(new File("temp2.xls"));
	 			
	 			WritableSheet ws = wb.createSheet("已筛选", 0);
	 			WritableCellFormat cellFormat = new WritableCellFormat();  
	 		    cellFormat.setAlignment(jxl.format.Alignment.CENTRE);  
	 		    System.out.println(tagpath.length());
	 		    ws.setColumnView(0, tagpath.length()*2);
	 		    ws.setColumnView(1, 20);
	 		    Label label= new Label(0, 0, "标签路径");
	 		    label.setCellFormat(cellFormat); 
	 			ws.addCell(label);
	 			label=new Label(1,0,"用户数");
	 			label.setCellFormat(cellFormat); 
	 			ws.addCell(label);
	 			label=new Label(0,1,tagpath); 
	 			label.setCellFormat(cellFormat); 
	 			ws.addCell(label);
	 			label=new Label(1,1,tagnum);
	 			label.setCellFormat(cellFormat); 
	 			ws.addCell(label);
	 	if(need.equals("yes"))
	 	{
	 			ws = wb.createSheet("未筛选", 1);
			    ws.setColumnView(0, 20);
			    ws.setColumnView(1, 20);
			    ws.setColumnView(2, 15); 
			  //设置表头
		 		 ws.mergeCells(0, 0, 2, 0);//添加合并单元格，第一个参数是起始列，第二个参数是起始行，第三个参数是终止列，第四个参数是终止行
		         WritableFont bold = new WritableFont(WritableFont.ARIAL,8,WritableFont.BOLD);//设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
		         WritableCellFormat titleFormate = new WritableCellFormat(bold);//生成一个单元格样式控制对象
		         titleFormate.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
		         titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
		         Label title = new Label(0,0,tagpath,titleFormate);
		         ws.setRowView(0, 400, false);//设置第一行的高度
		         ws.addCell(title);
		       //写入数据
	 			Label label0= new Label(0, 1, "标签");
	 		    label0.setCellFormat(cellFormat); 
	 			ws.addCell(label0);
	 			label0=new Label(1,1,"用户数");
	 			label0.setCellFormat(cellFormat); 
	 			ws.addCell(label0);
	 			label0=new Label(2,1,"比例");
	 			label0.setCellFormat(cellFormat); 
	 			ws.addCell(label0);
	 			for(int i=0;i<taglist.length;i++)
	 			{
	 				Label label1 = new Label(0, i+2, namelist[i]);
	 				label1.setCellFormat(cellFormat);
	 				ws.addCell(label1);
	 				Label label2 = new Label(1, i+2, popularlist[i]);
	 				label2.setCellFormat(cellFormat);
	 				ws.addCell(label2);
	 				Label label3 = new Label(2, i+2, ratelist[i]);
	 				label3.setCellFormat(cellFormat);
	 				ws.addCell(label3);
	 			}
	 	  }
	 			wb.write();
	 			wb.close();

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
	 		//下载excel
	 		File file=new File("temp2.xls");
	 		response.reset();
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
	         out.flush();
	         out.close();  		 
			 
		 }
	  
	  
	 @SuppressWarnings("unchecked")
	@RequestMapping(value = "/Insight")
	 public void Insight(@RequestParam(value = "tags", required = false)
	    String tagpath, HttpServletResponse response)
	    {
		    System.out.println("洞察路径："+tagpath);
		    String[] searchTags=tagpath.split("%%");
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
	        BoolFilterBuilder bool=FilterBuilders.boolFilter();
	    	for(int i=0;i<searchTags.length;i++) bool.must(FilterBuilders.termFilter("taglist",searchTags[i]));
	    	//FilterBuilder filterbuilder=bool;
	    	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
			//统计标签及其覆盖度
			SearchResponse sresponse = client.prepareSearch("dmp")
					.setTypes("imei")
					.setQuery(filterbuilder)
			     	.addAggregation(AggregationBuilders.terms("tagsgroup").field("taglist").size(Integer.MAX_VALUE))
			        .execute()
			        .actionGet();
			Aggregations a=sresponse.getAggregations();
			Terms term = a.get("tagsgroup");
			for(Bucket bc:term.getBuckets()){
				String name=""+bc.getKey();
				String num=""+bc.getDocCount();
				boolean add=true;
				for(int i=0;i<searchTags.length;i++){
					if(searchTags[i].equals(name)) {add=false;break;}
				}
				if(add==false) continue;
				tagids.add(Long.valueOf(name));
				tag_population.put(Long.valueOf(name),Long.valueOf(num));
				//System.out.println("标签:"+name+"覆盖度:" + num);
			}
			//统计二级分类及其覆盖度
			sresponse = client.prepareSearch("dmp")
					.setTypes("imei")
					.setQuery(filterbuilder)
				    .addAggregation(AggregationBuilders.terms("classify").field("tag.classify").size(Integer.MAX_VALUE))
			        .execute()
			        .actionGet();
			 a=sresponse.getAggregations();
		     term = a.get("classify");
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
	           	if(!classify_relation.containsKey(parenttemp.getClassifyId())) classify_relation.put(parenttemp.getClassifyId(),new ArrayList<Classify>());
	           	classify_relation.get(parenttemp.getClassifyId()).add(tagAdminService.getChildById(childid));
	           }
	           //统计一级分类覆盖率
	        //for(Classify parent:parents) parent_population.put(parent.getClassifyId(),1000L);
	   		sresponse = client.prepareSearch("dmp")
	   				.setTypes("imei")
	   				.setQuery(filterbuilder)
				    .addAggregation(AggregationBuilders.terms("parents").field("tag.parent").size(Integer.MAX_VALUE))
	   		        .execute()
	   		        .actionGet();
	   	    a=sresponse.getAggregations();
	        term = a.get("parents");
	        for(Bucket bc:term.getBuckets()){
	   			String name=""+bc.getKey();
	   			String num=""+bc.getDocCount();
	   			parent_population.put(Long.valueOf(name),Long.valueOf(num));
	   		//	System.out.println("一级分类:"+name+"覆盖度:" + num);
	   		}
	        client.close();    
	        String parentsJson = JSON.toJSONString(parents, SerializerFeature.PrettyFormat);
	        String classify_relationJson = JSON.toJSONString(classify_relation, SerializerFeature.PrettyFormat);
	        String tag_classifyJson = JSON.toJSONString(tag_classify, SerializerFeature.PrettyFormat);
	        String tag_populationJson = JSON.toJSONString(tag_population, SerializerFeature.PrettyFormat);
	        String child_populationJson = JSON.toJSONString(child_population, SerializerFeature.PrettyFormat);
	        String parent_populationJson = JSON.toJSONString(parent_population, SerializerFeature.PrettyFormat);
	        
	        String result = parentsJson + "%%" + classify_relationJson + "%%" + tag_classifyJson + "%%" + tag_populationJson+"%%"
	        		+child_populationJson+"%%"+parent_populationJson;
	        PrintWriterUtil.returnPrintWriterJson(response, result);
	       
	    }
}
