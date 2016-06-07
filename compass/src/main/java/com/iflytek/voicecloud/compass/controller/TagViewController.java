package com.iflytek.voicecloud.compass.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.aspect.Cache;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.dao.IClassifyDao;
import com.iflytek.voicecloud.compass.dao.impl.RedisDao;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagStatisticsVo;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

/**
 * 
 * @author kwliu
 * @date 上午10:23:33, 2015年9月6日
 */
@Controller
@RequestMapping(value = "/tagView")
public class TagViewController
{
    @Autowired
    ITagAdminService tagAdminService;
    //二级分类及标签覆盖度导出
    @RequestMapping(value = "/Export")
    public void Export(HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
    	int k=1;
    	List<Classify> childs = tagAdminService.getAllChildClassifies();
       //写入excel
   	   WritableWorkbook wb;
		try {
			wb = Workbook.createWorkbook(new File("temp3.xls"));
			WritableSheet ws = wb.createSheet("sheet1", 0);
			CellView cellView = new CellView();  cellView.setAutosize(true); //设置自动大小    
		    //ws.setColumnView(0, cellView);//根据内容自动设置列宽
			ws.setColumnView(0, 20);
			ws.setColumnView(1, 20);
			ws.setColumnView(2, 20);
		    WritableCellFormat cellFormat = new WritableCellFormat();  
		    cellFormat.setAlignment(jxl.format.Alignment.CENTRE);   
			Label label0= new Label(0, 0, "名称");
		    label0.setCellFormat(cellFormat); 
			ws.addCell(label0);
			label0=new Label(1,0,"覆盖度");
			label0.setCellFormat(cellFormat); 
			ws.addCell(label0);
			label0=new Label(2,0,"更新时间");
			label0.setCellFormat(cellFormat); 
			ws.addCell(label0);
			for(Classify child: childs)
			{
				//int per=(int)((float)Cache.populationsMap.get(child.getClassifyId())/(float)Cache.populationsMap.get(Constant.ALL_TAG_ID)*100);
				//if(per==0) continue;
				//String rate =per+"%";
				String rate = (Cache.populationsMap.get(child.getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"%";
				if(rate.equals("0%")) continue;
				Label label1 = new Label(0, k, child.getClassifyName());
				label1.setCellFormat(cellFormat); 
				ws.addCell(label1);
				Label label2 = new Label(1, k,rate);
				label2.setCellFormat(cellFormat); 
				ws.addCell(label2);
				k++;
				List<Tag> tags=tagAdminService.getTags(child.getClassifyId());
				for(Tag tag:tags)
				 {
					Map<String,String> result=tagAdminService.getTagPopulation(tag);
					if(result.get("population")==null) continue;
					Label label3 = new Label(0, k, tag.getTagName());
					label3.setCellFormat(cellFormat); 
					ws.addCell(label3);
					Label label4 = new Label(1, k,result.get("population"));
					label4.setCellFormat(cellFormat); 
					ws.addCell(label4);
					Label label5 = new Label(2, k,result.get("date"));
					label5.setCellFormat(cellFormat); 
					ws.addCell(label5);
					k++;
				}
				k++;
				
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
		File file=new File("temp3.xls");
		response.reset();
		//response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-disposition", "attachment;filename=population.xls");  
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
    
   
    /**
     * 点击一级分类，显示有效标签
     * 
     * @param printWriter
     * @throws IOException
     * @throws ClientProtocolException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getEffectiveTagsFromParentClassify")
    public void getEffectiveTagsFromParentClassify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long parentId = Long.parseLong(parentIdStr);
        // 根据parentId找
        List<Classify> childClassifies = new ArrayList<Classify>();
        String rate = "0";
        List<TagStatisticsVo> tagStatisticsVosPagination = new ArrayList<TagStatisticsVo>();
        int totalCount = 0;
        childClassifies = tagAdminService.getChildClassifies(parentId);
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        if(!childClassifies.isEmpty()){
            //rate = HttpClientUtil.getTagsRate(childClassifies.get(0).getClassifyId()) + "";
        	//int per=(int)((float)Cache.populationsMap.get(childClassifies.get(0).getClassifyId())/(float)Cache.populationsMap.get(Constant.ALL_TAG_ID)*100);
           // rate =per+"";
            rate = (Cache.populationsMap.get(childClassifies.get(0).getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
            //根据第一个二级分类获取所有统计标签
            Long childId = childClassifies.get(0).getClassifyId();
            List<Tag> tags =  (List<Tag>)tagAdminService.getTags(childId, Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
            tagStatisticsVos = tagAdminService.getEffectiveTagView(tags);
            totalCount = tagStatisticsVos.size();
            
            // 根据index和size收到分页
            for (int i = (pageIndex - 1) * pageSize; (i < totalCount) && (i < pageIndex * pageSize); i++)
            {
                tagStatisticsVosPagination.add(tagStatisticsVos.get(i));
            }
        } 
        
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        String tagStatisticsDtosJson = JSON.toJSONString(tagStatisticsVosPagination, SerializerFeature.PrettyFormat);
        String result = childClassifiesJson + "%%" + tagStatisticsDtosJson + "%%" + rate + "%%" + totalCount;
        PrintWriterUtil.returnPrintWriterJson(response, result);
    }
    
    /**
     * 点击一级分类，显示所有标签
     * 
     * @param printWriter
     * @throws IOException
     * @throws ClientProtocolException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAllTagsFromParentClassify")
    public void getAllTagsFromParentClassify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long parentId = Long.parseLong(parentIdStr);
        // 根据parentId找
        List<Classify> childClassifies = new ArrayList<Classify>();
        List<Tag> tags = new ArrayList<Tag>();
        Long totalCount = 0L;
        childClassifies = tagAdminService.getChildClassifies(parentId);
        String rate = "0";
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        if (!childClassifies.isEmpty())
        {
            //rate = HttpClientUtil.getTagsRate(childClassifies.get(0).getClassifyId()) + "";
            rate = (Cache.populationsMap.get(childClassifies.get(0).getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
            tags =
                (List<Tag>)tagAdminService.getTags(childClassifies.get(0).getClassifyId(), pageIndex, pageSize)
                    .get("tags");
            totalCount =
                (Long)tagAdminService.getTags(childClassifies.get(0).getClassifyId(), pageIndex, pageSize)
                    .get("totalCount");
        }
        
        // 对tags还要进行深度处理，查询最近的更新跨度内的population，直接返回List吧
        tagStatisticsVos = tagAdminService.getAllTagView(tags);
        
        
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        
        String result = childClassifiesJson + "%%" + tagStatisticsVosJson + "%%" + rate + "%%" + totalCount;
        PrintWriterUtil.returnPrintWriterJson(response, result);
        
    }
    
    /**
     * 点击“+”，显示统计图
     * 
     * @param tagIdStr
     * @param printWriter
     * @throws IOException
     * @throws ClientProtocolException
     */
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
     * 点击二级分类，获取有效标签
     * 
     * @param childIdStr
     * @param printWriter
     * @throws IOException
     * @throws ClientProtocolException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getEffectiveTagsFromChildClassify")
    public void getEffectiveTagsFromChildClassify(@RequestParam(value = "childId")
    String childIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long childId = Long.parseLong(childIdStr);
       // int per=(int)((float)Cache.populationsMap.get(childId)/(float)Cache.populationsMap.get(Constant.ALL_TAG_ID)*100);
        //String rate =per+"";
        String rate = (Cache.populationsMap.get(childId)/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
        List<TagStatisticsVo> tagStatisticsVosPagination = new ArrayList<TagStatisticsVo>();
        int totalCount = 0;
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        List<Tag> tags =  (List<Tag>)tagAdminService.getTags(childId, Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
        tagStatisticsVos = tagAdminService.getEffectiveTagView(tags);
       
        totalCount = tagStatisticsVos.size();
        
        // 根据index和size收到分页
        for (int i = (pageIndex - 1) * pageSize; (i < totalCount) && (i < pageIndex * pageSize); i++)
        {
            tagStatisticsVosPagination.add(tagStatisticsVos.get(i));
        }
        String tagStatisticsDtosJson = JSON.toJSONString(tagStatisticsVosPagination, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagStatisticsDtosJson + "%%" + rate + "%%" + totalCount);
    }
    
    /**
     * 点击二级分类，获取所有标签
     * 
     * @param childIdStr
     * @param printWriter
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/getAllTagsFromChildClassify")
    public void getAllTagsFromChildClassify(@RequestParam(value = "childId")
    String childIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long childId = Long.parseLong(childIdStr);
        String rate = (Cache.populationsMap.get(childId)/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
      
        @SuppressWarnings("unchecked")
        List<Tag> tags = (List<Tag>)tagAdminService.getTags(childId, pageIndex, pageSize).get("tags");
        Long totalCount = (Long)tagAdminService.getTags(childId, pageIndex, pageSize).get("totalCount");
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        tagStatisticsVos = tagAdminService.getAllTagView(tags);
        
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagStatisticsVosJson + "%%" + rate + "%%" + totalCount);
    }
    
    /**
     * 搜索框提示
     * 
     * @param inputInfo
     */
   /* @RequestMapping(value = "/viewHint")
    public void viewHint(@RequestParam(value = "inputInfo", required = false)
    String inputInfo, HttpServletResponse response)
    {
        List<Tag> tags = tagAdminService.getTagsLike(inputInfo);
        List<String> hints = new ArrayList<String>();
        for (Tag tag : tags)
        {
            hints.add(tag.getTagName());
        }
        String tagsJson = JSON.toJSONString(hints, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagsJson);
    }*/
}
