package com.iflytek.voicecloud.compass.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.aspect.Cache;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.HttpClientUtil;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagClassify;
import com.iflytek.voicecloud.compass.po.TagIntegration;
import com.iflytek.voicecloud.compass.po.TagStatistics;
import com.iflytek.voicecloud.compass.po.TagStatisticsVo;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

/**
 * 
 * @author kwliu
 * @date 上午10:24:45, 2015年10月8日
 */
@Controller
@RequestMapping(value = "/tagIntegration")
public class TagIntegrationController
{
    @Autowired
    ITagAdminService tagAdminService;
    
    /**
     * 获取下拉框内容
     * 
     * @param parentIdStr
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getChildAndTag")
    public void getChildAndTag(@RequestParam(value = "parentId", required = false)
    String parentIdStr, HttpServletResponse response)
    {
        // 根据parentId获取所有的二级分类
        Long parentId = Long.parseLong(parentIdStr);
        List<Classify> childClassifies = new ArrayList<Classify>();
        List<Tag> tags = new ArrayList<Tag>();
        childClassifies = tagAdminService.getChildClassifies(parentId);
        
        // 根据所有的二级分类，获取对应的所有非整合的标签，放到map中
        Map<String, List<Tag>> resultMap = new HashMap<String, List<Tag>>();
        for (Classify child : childClassifies)
        {
            tags = (List<Tag>)tagAdminService.getTags(child.getClassifyId(), Constant.defaultPageIndex, Constant.defaultPageSize).get("tags"); // 要改写该方法，从中去掉整合的标签
            /*
             * List<Tag> newTags = new ArrayList<Tag>(); //从tags中去掉整合的标签 for(Tag tag: tags){
             * if(!tagAdminService.isIntegrationTag(tag.getTagId())){ newTags.add(tag); } }
             */
            resultMap.put(child.getClassifyName(), tags); // 现在是整合之后可以再整合
        }
        
        String resultMapJson = JSON.toJSONString(resultMap, SerializerFeature.PrettyFormat);
        
        PrintWriterUtil.returnPrintWriterJson(response, resultMapJson);
        
    }
    
    /**
     * 添加一个整合标签
     * 
     * @param request
     * @param printWriter
     */
    @RequestMapping("/addTagIntegration")
    public void addTagIntegration(HttpServletRequest request, PrintWriter printWriter)
        throws ClientProtocolException, IOException
    {
        // 传过来的参数，包括：tagName,tagDesc,updateSpan,updateGranularity,childs,tagIds等
        String tagName = request.getParameter("tagName");
        String tagDescription = request.getParameter("tagDescription");
        int updateSpan = Integer.parseInt(request.getParameter("updateSpan"));
        String updateGranularity = request.getParameter("updateGranularity");
        List<Long> childIds = tagAdminService.seperateIds(request.getParameter("childIds"));// 用*隔开
        List<Long> tagIds = tagAdminService.seperateIds(request.getParameter("tagIds"));// 用*隔开
        // 第一步，生成该整合标签
        Tag tag = new Tag();
        tag.setIsAct(Constant.ACT);
        tag.setTagName(tagName);
        Long regtime=System.currentTimeMillis();
        tag.setRegTime(regtime);
        tag.setRegUser(111);
        tag.setTagDescription(tagDescription);
        tag.setUpdateGranularity(updateGranularity);
        tag.setUpdateSpan(updateSpan);
        tag.setUpdateTime(regtime);
        // 将tag写进数据库
        tagAdminService.addTag(tag);
        // 获取该tag的tagId
        Long tagIntegrationId = tagAdminService.getTag(tagName,regtime).getTagId();
        // 生成整合标签与子标签关系记录,同时拼接一个url记录，为http访问准备
        String url = "";
        for (Long tagId : tagIds)
        {
            url += "_all:*" + tagId + "%20";
            TagIntegration tagIntegration = new TagIntegration();
            tagIntegration.setTagIntegrationId(tagIntegrationId);
            tagIntegration.setTagId(tagId);
            tagAdminService.addTagIntegration(tagIntegration);
        }
        // 生成该整合标签与二级分类关系记录
        
        // 添加一个标签分类关系
        for (Long childId : childIds)
        {
            TagClassify tagClassify = new TagClassify();
            tagClassify.setClassifyId(childId);
            tagClassify.setTagId(tagIntegrationId);
            tagAdminService.addTagClassify(tagClassify);
        }
        // 添加一条统计记录 100100010001 100100010001
        
        // 首先从所有的子记录中获取统计数据
        Long population = HttpClientUtil.getSubTagPopulation(url);
        
        // 把这个population计入统计数据
        TagStatistics tagStatistics = new TagStatistics();
        tagStatistics.setPopulation(population);
        tagStatistics.setUpdateTime(System.currentTimeMillis());
        tagStatistics.setTagId(tagIntegrationId);
        tagAdminService.addTagStatistics(tagStatistics);
        
        printWriter.write("true");
        printWriter.flush();
        printWriter.close();
    }
    
    @RequestMapping("/getAllTagIntegrationsFromParentClassify")
    public void getAllTagIntegrationsFromParentClassify(HttpServletRequest request, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long parentId = Long.parseLong(request.getParameter("parentId"));
       
        // 根据parentId找
        List<Classify> childClassifies = new ArrayList<Classify>();
        
        List<Classify> childs = tagAdminService.getChildClassifies(parentId);
        
        List<Tag> tagIntegrations = new ArrayList<Tag>();
        // 遍历所有的二级分类下的标签，判断是否为整合标签
        
        int index = 0;
        for (Classify child : childs)
        {
            @SuppressWarnings("unchecked")
            List<Tag> tags = (List<Tag>)tagAdminService.getTags(child.getClassifyId(), Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
            boolean hasTagIntegration = false;
            
            for (Tag tag : tags)
            {
                if (tagAdminService.isIntegrationTag(tag.getTagId()))
                {
                    if (index == 0)
                    {
                        tagIntegrations.add(tag);
                    }
                    hasTagIntegration = true;
                }
            }
            if (hasTagIntegration == true)
            {
                index++;
                childClassifies.add(child);
            }
            
        }
        
        // 对tagIntegrations还要进行深度处理，查询最近的更新跨度内的population，直接返回List吧
        
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getAllTagView(tagIntegrations);
        
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        String rate = "0";
        if (childClassifies.size() != 0)
        {
            //rate = HttpClientUtil.getTagsRate(childClassifies.get(0).getClassifyId()) + "";
            rate = (Cache.populationsMap.get(childClassifies.get(0).getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
        }
        String result = childClassifiesJson + "%%" + tagStatisticsVosJson + "%%" + rate + "%";
        
        PrintWriterUtil.returnPrintWriterJson(response, result);
        
    }
    
    /**
     * 返回结果,包括：当前parentId下的二级分类中有整合标签的二级分类和该二级分类下的整合标签
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ClientProtocolException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getAllTagIntegrationsFromChildClassify")
    public void getAllTagIntegrationsFromChildClassify(HttpServletRequest request, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long childId = Long.parseLong(request.getParameter("childId"));
        //String rate = HttpClientUtil.getTagsRate(childId) + "";
        String rate = (Cache.populationsMap.get(childId)/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
        List<Tag> tagIntegrations = new ArrayList<Tag>();
        List<Tag> tags = (List<Tag>)tagAdminService.getTags(childId, Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
        
        for (Tag tag : tags)
        {
            if (tagAdminService.isIntegrationTag(tag.getTagId()))
            {
                tagIntegrations.add(tag);
            }
        }
        
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getAllTagView(tagIntegrations);
       
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagStatisticsVosJson + "%%" + rate + "%");
    }
    
    @RequestMapping("/getEffectiveTagIntegrationsFromParentClassify")
    public void getEffectiveTagIntegrationsFromParentClassify(HttpServletRequest request, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long parentId = Long.parseLong(request.getParameter("parentId"));
        // 根据parentId找
        List<Classify> childClassifies = new ArrayList<Classify>();
        
        List<Classify> childs = tagAdminService.getChildClassifies(parentId);
        
        List<Tag> tagIntegrations = new ArrayList<Tag>();
        // 遍历所有的二级分类下的标签，判断是否为整合标签
        
        int index = 0;
        for (Classify child : childs)
        {
            @SuppressWarnings("unchecked")
            List<Tag> tags = (List<Tag>)tagAdminService.getTags(child.getClassifyId(), Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
            boolean hasTagIntegration = false;
            
            for (Tag tag : tags)
            {
                if (tagAdminService.isIntegrationTag(tag.getTagId()))
                {
                    
                    if (index == 0)
                    {
                        tagIntegrations.add(tag);
                    }
                    hasTagIntegration = true;
                    
                }
            }
            if (hasTagIntegration == true)
            {
                index++;
                childClassifies.add(child);
                
            }
            
        }
        
        // 对tagIntegrations还要进行深度处理，查询最近的更新跨度内的population，直接返回List吧
        
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getEffectiveTagView(tagIntegrations);
  
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        String rate = "0";
        if (childClassifies.size() != 0)
        {
            //rate = HttpClientUtil.getTagsRate(childClassifies.get(0).getClassifyId()) + "";
            rate = (Cache.populationsMap.get(childClassifies.get(0).getClassifyId())/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
        }
        
        String result = childClassifiesJson + "%%" + tagStatisticsVosJson + "%%" + rate + "%";
        
        PrintWriterUtil.returnPrintWriterJson(response, result);
        
    }
    
    /**
     * 返回结果,包括：当前parentId下的二级分类中有整合标签的二级分类和该二级分类下的整合标签
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ClientProtocolException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getEffectiveTagIntegrationsFromChildClassify")
    public void getEffectiveTagIntegrationsFromChildClassify(HttpServletRequest request, HttpServletResponse response)
        throws ClientProtocolException, IOException
    {
        Long childId = Long.parseLong(request.getParameter("childId"));
        //String rate = HttpClientUtil.getTagsRate(childId) + "";
        String rate = (Cache.populationsMap.get(childId)/Cache.populationsMap.get(Constant.ALL_TAG_ID))+"";
        List<Tag> tagIntegrations = new ArrayList<Tag>();
        List<Tag> tags = (List<Tag>)tagAdminService.getTags(childId, Constant.defaultPageIndex, Constant.defaultPageSize).get("tags");
        for (Tag tag : tags)
        {
            if (tagAdminService.isIntegrationTag(tag.getTagId()))
            {
                tagIntegrations.add(tag);
            }
        }
        List<TagStatisticsVo> tagStatisticsVos = tagAdminService.getEffectiveTagView(tagIntegrations);
        String tagStatisticsVosJson = JSON.toJSONString(tagStatisticsVos, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, tagStatisticsVosJson + "%%" + rate + "%");
    }
    
    /**
     * 新增标签时获取所有的二级分类
     * 
     * @param response
     */
    @RequestMapping(value = "/getAllChildClassifies")
    public void getAllChildClassifies(HttpServletResponse response)
    {
        List<Classify> childClassifies = tagAdminService.getAllChildClassifies();
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, childClassifiesJson);
    }
}
