package com.iflytek.voicecloud.compass.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.PrintWriterUtil;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.ClassifyRelation;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagClassify;
import com.iflytek.voicecloud.compass.service.ITagAdminService;

/**
 * 
 * @author kwliu
 * @date 上午11:05:47, 2015年8月26日
 */
@Controller
@RequestMapping(value = "/tagAdmin")
public class TagAdminController
{
    @Autowired
    ITagAdminService tagAdminService;
    
    /**
     * 根据一级分类获取二级分类列表和第一个二级分类列表下的标签列表
     * 
     * @param parentIdStr 一级分类ID
     * @param printWriter
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getTagsFromParentClassify")
    public void getTagsFromParentClassify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
    {
        Long parentId = Long.parseLong(parentIdStr);
        List<Classify> childClassifies = new ArrayList<Classify>();
        List<Tag> tags = new ArrayList<Tag>();
        childClassifies = tagAdminService.getChildClassifies(parentId);
        Long totalCount = 0L;
        if (!childClassifies.isEmpty())
        {
            tags =
                (List<Tag>)tagAdminService.getTags(childClassifies.get(0).getClassifyId(), pageIndex, pageSize)
                    .get("tags");
            totalCount =
                (Long)tagAdminService.getTags(childClassifies.get(0).getClassifyId(), pageIndex, pageSize)
                    .get("totalCount");
        }
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        String tagsJson = JSON.toJSONString(tags, SerializerFeature.PrettyFormat);
        
        String result = childClassifiesJson + "%%" + tagsJson + "%%" + totalCount;
        
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter;
        try
        {
            printWriter = response.getWriter();
            printWriter.write(result);
            printWriter.flush();
            printWriter.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 根据二级分类获取对应标签列表
     * 
     * @param childIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/getTagsFromChildClassify")
    public void getTagsFromChildClassify(@RequestParam(value = "childId", required = false)
    String childIdStr, @RequestParam(value = "pageIndex")
    int pageIndex, @RequestParam(value = "pageSize")
    int pageSize, HttpServletResponse response)
    {
        Long childId = Long.parseLong(childIdStr);
        @SuppressWarnings("unchecked")
        List<Tag> tags = (List<Tag>)tagAdminService.getTags(childId, pageIndex, pageSize).get("tags");
        Long totalCount = (Long)tagAdminService.getTags(childId, pageIndex, pageSize).get("totalCount");
        String tagsJson = JSON.toJSONString(tags, SerializerFeature.PrettyFormat) + "%%" + totalCount;
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter;
        try
        {
            printWriter = response.getWriter();
            printWriter.write(tagsJson);
            printWriter.flush();
            printWriter.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 添加一级分类
     * 
     * @param parentName
     * @param parentDescription
     * @param printWriter
     */
    @RequestMapping(value = "/addParentClassify")
    public void addParentClassify(@RequestParam(value = "parentName", required = false)
    String parentName, @RequestParam(value = "parentDescription", required = false)
    String parentDescription, PrintWriter printWriter)
    {
        Classify parentClassify = new Classify();
        parentClassify.setClassifyName(parentName);
        parentClassify.setClassifyDescription(parentDescription);
        parentClassify.setIsParent(Constant.PARENT_CLASSIFY_TYPE);
        tagAdminService.addClassify(parentClassify);
        
        // 查询一下，获取其parentID
        Classify parentClassify2 = tagAdminService.getClassify(parentName);
        
        printWriter.write(parentClassify2.getClassifyId().toString());
        printWriter.flush();
        printWriter.close();
    }
    
    /**
     * 添加二级分类并关联到指定一级分类下
     * 
     * @param childName
     * @param childDescription
     * @param parentIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/addChildClassify")
    public void addChildClassify(@RequestParam(value = "childName", required = false)
    String childName, @RequestParam(value = "childDescription", required = false)
    String childDescription, @RequestParam(value = "parentId", required = false)
    String parentIdStr, PrintWriter printWriter)
    {
        Classify childClassify = new Classify();
        childClassify.setClassifyName(childName);
        childClassify.setClassifyDescription(childDescription);
        childClassify.setIsParent(Constant.CHILD_CLASSIFY_TYPE);
        tagAdminService.addClassify(childClassify);
        
        // 查询一下，获取其childID
        Classify childClassify2 = tagAdminService.getClassify(childName);
        
        // 添加一个父类子类关系
        ClassifyRelation classifyRelation = new ClassifyRelation();
        classifyRelation.setChildId(childClassify2.getClassifyId());
        Long parentId = Long.parseLong(parentIdStr);
        classifyRelation.setParentId(parentId);
        tagAdminService.addClassifyRelation(classifyRelation);
        
        printWriter.write(childClassify2.getClassifyId().toString());
        printWriter.flush();
        printWriter.close();
    }
    
    /**
     * 添加标签并关联到指定二级分类下
     * 
     * @param tagName
     * @param tagDescription
     * @param updateGranularity
     * @param updateSpanStr
     * @param childIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/addTag")
    public void addTag(@RequestParam(value = "tagName", required = false)
    String tagName, @RequestParam(value = "tagDescription", required = false)
    String tagDescription, @RequestParam(value = "updateGranularity", required = false)
    String updateGranularity, @RequestParam(value = "updateSpan", required = false)
    String updateSpanStr, @RequestParam(value = "childId", required = false)
    String childIdStr, PrintWriter printWriter)
    {
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setTagDescription(tagDescription);
        if (updateGranularity.equals("日"))
        {
            updateGranularity = "D";
        }
        else if (updateGranularity.equals("周"))
        {
            updateGranularity = "W";
        }
        else
        {
            updateGranularity = "M";
        }
        tag.setUpdateGranularity(updateGranularity);
        tag.setUpdateSpan(Integer.parseInt(updateSpanStr));
        tag.setIsAct(Constant.ACT);
        Long regTime=System.currentTimeMillis();
        tag.setRegTime(regTime);
        tag.setRegUser(111); // 将来是要查account表的，塞到session中去
        tag.setUpdateTime(regTime);
        
        tagAdminService.addTag(tag);
 
        // 查询一下，获取其tagId
        Tag tag2 = tagAdminService.getTag(tagName,regTime);
        
        // 添加一个标签分类关系
        TagClassify tagClassify = new TagClassify();
        tagClassify.setClassifyId(Long.parseLong(childIdStr));
        tagClassify.setTagId(tag2.getTagId());
        tagAdminService.addTagClassify(tagClassify);
        
        printWriter.write("true");
        printWriter.flush();
        printWriter.close();
    }
    
    /**
     * 修改一级分类，只能修改描述
     * 
     * @param parentIdStr
     * @param parentName
     * @param parentDescription
     * @param printWriter
     */
    @RequestMapping(value = "/modifyParentClassify")
    public void modifyParentClassify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, @RequestParam(value = "parentName", required = false)
    String parentName, @RequestParam(value = "parentDescription", required = false)
    String parentDescription, PrintWriter printWriter)
    {
        Long parentId = Long.parseLong(parentIdStr);
        Classify parentClassify = new Classify();
        parentClassify.setClassifyId(parentId);
        parentClassify.setClassifyName(parentName);
        parentClassify.setClassifyDescription(parentDescription);
        parentClassify.setIsParent(Constant.PARENT_CLASSIFY_TYPE);
        tagAdminService.updateClassify(parentClassify);
        
        printWriter.write("true");
        printWriter.flush();
        printWriter.close();
        
    }
    
    /**
     * 修改二级分类，可以修改其所对应的一级分类，也可以增加其所对应的一级分类
     * 
     * @param childIdStr
     * @param childName
     * @param childDescription
     * @param parentIdsStr
     * @param printWriter
     */
    @RequestMapping(value = "/modifyChildClassify")
    public void modifyChildClassify(@RequestParam(value = "childId", required = false)
    String childIdStr, @RequestParam(value = "childName", required = false)
    String childName, @RequestParam(value = "childDescription", required = false)
    String childDescription, @RequestParam(value = "parentIds", required = false)
    String parentIdsStr, PrintWriter printWriter)
    {
        // 更新二级分类
        Long childId = Long.parseLong(childIdStr);
        Classify childClassify = new Classify();
        childClassify.setClassifyId(childId);
        childClassify.setClassifyName(childName);
        childClassify.setClassifyDescription(childDescription);
        childClassify.setIsParent(Constant.CHILD_CLASSIFY_TYPE);
        tagAdminService.updateClassify(childClassify);
        if (!parentIdsStr.equals(""))
        {
            // 更新分类关系,先判断原有关系是否存在，存在在不变，否则增加
            List<Long> parentIds = tagAdminService.seperateIds(parentIdsStr);
            for (Long parentId : parentIds)
            {
                ClassifyRelation classifyRelation = new ClassifyRelation();
                classifyRelation.setParentId(parentId);
                classifyRelation.setChildId(childId);
                tagAdminService.addClassifyRelation(classifyRelation);
            }
        }
        
        printWriter.write("true");
        printWriter.flush();
        printWriter.close();
    }
    
    /**
     * 修改标签，修改标签对应的二级分类
     * 
     * @param tagIdStr
     * @param tagName
     * @param tagDescription
     * @param updateGranularity
     * @param updateSpanStr
     * @param childIdsStr
     * @param printWriter
     */
    @RequestMapping(value = "/modifyTag")
    public void modifyTag(@RequestParam(value = "tagId", required = false)
    String tagIdStr, @RequestParam(value = "tagName", required = false)
    String tagName, @RequestParam(value = "tagDescription", required = false)
    String tagDescription, @RequestParam(value = "updateGranularity", required = false)
    String updateGranularity, @RequestParam(value = "updateSpan", required = false)
    String updateSpanStr, @RequestParam(value = "childIds", required = false)
    String childIdsStr, PrintWriter printWriter)
    {
        // 更新tag表记录
        Long tagId = Long.parseLong(tagIdStr);
        Tag oldTag = tagAdminService.getTag(tagId);
        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setTagName(tagName);
        tag.setTagDescription(tagDescription);
        if (updateGranularity.equals("日"))
        {
            updateGranularity = "D";
        }
        else if (updateGranularity.equals("周"))
        {
            updateGranularity = "W";
        }
        else
        {
            updateGranularity = "M";
        }
        tag.setUpdateGranularity(updateGranularity);
        tag.setUpdateSpan(Integer.parseInt(updateSpanStr));
        tag.setUpdateTime(System.currentTimeMillis());
        tag.setIsAct(Constant.ACT);
        tag.setRegUser(oldTag.getRegUser());
        tag.setRegTime(oldTag.getRegTime());
        
        tagAdminService.updateTag(tag);
        List<Long> childIds = tagAdminService.seperateIds(childIdsStr);
        // 获取原有的旧关系
        List<Classify> oldClassifies = tagAdminService.getChildClassifiesByTag(tagId);
        List<Long> oldChildIds = new ArrayList<Long>();
        for (Classify classify : oldClassifies)
        {
            oldChildIds.add(classify.getClassifyId());
        }
        for (Long oldChildId : oldChildIds)
        {
            if (!childIds.contains(oldChildId))
            {
                tagAdminService.removeTagClassify(tagId, oldChildId);
            }
        }
        for (Long childId : childIds)
        {
            if (!oldChildIds.contains(childId))
            {
                TagClassify tagClassify = new TagClassify();
                tagClassify.setTagId(tagId);
                tagClassify.setClassifyId(childId);
                tagAdminService.addTagClassify(tagClassify);
            }
        }
        
        printWriter.write("true");
        printWriter.flush();
        printWriter.close();
    }
    
    /**
     * 删除一级分类，前提是该一级分类下没有二级分类
     * 
     * @param parentIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/removeParentClassify")
    public void removeParentClassify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, PrintWriter printWriter)
    {
        Long parentId = Long.parseLong(parentIdStr);
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.removeParentClassify(parentId));
    }
    
    /**
     * 删除二级分类，前提是该二级分类下没有标签
     * 
     * @param childIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/removeChildClassify")
    public void removeChildClassify(@RequestParam(value = "childId", required = false)
    String childIdStr, PrintWriter printWriter)
    {
        Long childId = Long.parseLong(childIdStr);
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.removeChildClassify(childId));
    }
    
    /**
     * 删除标签，同时删除标签与二级分类的对应关系
     * 
     * @param tagIdStr
     * @param printWriter
     */
    @RequestMapping(value = "/removeTag")
    public void removeTag(@RequestParam(value = "tagId", required = false)
    String tagIdStr, PrintWriter printWriter)
    {
        Long tagId = Long.parseLong(tagIdStr);
        boolean condition = false;
        if(tagAdminService.removeTag(tagId)){
            condition = true;
        }
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, condition);
    }
    
    @RequestMapping(value = "/removeParentClassifyVarify")
    public void removeParentClassifyVarify(@RequestParam(value = "parentId", required = false)
    String parentIdStr, PrintWriter printWriter)
    {
        // 判断一级分类下是否有二级分类
        Long parentId = Long.parseLong(parentIdStr);
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.getChildClassifies(parentId).isEmpty());
    }
    
    @RequestMapping(value = "/removeChildClassifyVarify")
    public void removeChildClassifyVarify(@RequestParam(value = "childId", required = false)
    String childIdStr, PrintWriter printWriter)
    {
        // 判断二级分类下是否有标签
        Long childId = Long.parseLong(childIdStr);
        PrintWriterUtil.returnPrintWriterBoolean(printWriter,
            ((Long)tagAdminService.getTags(childId, 1, 1).get("totalCount")).equals(0L));
        
    }
    
    @RequestMapping(value = "/getOriginalChildClassifies")
    public void getOriginalChildClassifies(@RequestParam(value = "tagId", required = false)
    String tagIdStr, HttpServletResponse response)
    {
        Long tagId = Long.parseLong(tagIdStr);
        // 查tag-classify表，获取所有的childClassifiesId
        List<Classify> childClassifies = tagAdminService.getChildClassifiesByTag(tagId);
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, childClassifiesJson);
    }
    
    @RequestMapping(value = "/getAllChildClassifies")
    public void getAllChildClassifies(HttpServletResponse response)
    {
        List<Classify> childClassifies = tagAdminService.getAllChildClassifies();
        String childClassifiesJson = JSON.toJSONString(childClassifies, SerializerFeature.PrettyFormat);
        PrintWriterUtil.returnPrintWriterJson(response, childClassifiesJson);
    }
    
    @RequestMapping(value = "tagNameVarify")
    public void tagNameVarify(@RequestParam(value = "tagName")
    String tagName, PrintWriter printWriter)
    {
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.tagNameVarify(tagName));
    }
    
    @RequestMapping(value = "childNameVarify")
    public void childNameVarify(@RequestParam(value = "childName")
    String childName, PrintWriter printWriter)
    {
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.childNameVarify(childName));
    }
    
    @RequestMapping(value = "parentNameVarify")
    public void parentNameVarify(@RequestParam(value = "parentName")
    String parentName, PrintWriter printWriter)
    {
        PrintWriterUtil.returnPrintWriterBoolean(printWriter, tagAdminService.parentNameVarify(parentName));
    }
}
