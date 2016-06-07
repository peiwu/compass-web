package com.iflytek.voicecloud.compass.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.ClassifyRelation;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagClassify;
import com.iflytek.voicecloud.compass.po.TagIntegration;
import com.iflytek.voicecloud.compass.po.TagStatistics;
import com.iflytek.voicecloud.compass.po.TagStatisticsVo;

/**
 * 标签管理
 * 
 * @author kwliu
 * @date 下午2:40:53, 2015年9月2日
 */
public interface ITagAdminService
{
   public List<Long> getTagIdsFromDevice(String dvcnumber);
   public List<String> getDvcByTags(String[] tagIds) throws ClientProtocolException, IOException;
   public boolean addRedis(Redis redis);
   public Redis getRedis(String username,Long writetime);
   public Redis getRedisById(Long id);
   public boolean updateRedis(Redis redis);
   public boolean deleteRedis(Long id);
   public List<Redis> getNoOkRedis(String username);
   public boolean checkPrefix(String username,String prefix);
   public boolean isRelation(Long childid,Long tagid);
   public Classify getParentByChild(Long childid);
   public Classify getChildById(Long childid);
   public Long getPopulationByTags(List<String> tagIds) throws ClientProtocolException, IOException;
   public List<Tag> getTags(Long childId);
   public Map<String,String> getTagPopulation(Tag tag);
    /**
     * 获取所有的一级分类
     * 
     * @return
     */
    public List<Classify> getParentClassifies();
    
    /**
     * 获取指定一级分类下的二级分类
     * 
     * @param parentId
     * @return
     */
    public List<Classify> getChildClassifies(Long parentId);
    
    /**
     * 根据分类名称获取分类
     * 
     * @param classifyName
     * @return
     */
    public Classify getClassify(String classifyName);
    
    /**
     * 获取指定二级分类下的所有标签
     * 
     * @param childId
     * @return
     */
    public Map<String, Object> getTags(Long childId, int pageIndex, int pageSize);
    /**
     * 根据tag查其所对应的二级分类
     * @param tagId
     * @return
     */
    public List<Classify> getChildClassifiesByTag(Long tagId);
    /**
     * 获取所有的二级分类
     * @return
     */
    public List<Classify> getAllChildClassifies();

    /**
     * 根据标签名称获取标签
     * 
     * @param tagName
     * @return
     */
    public Tag getTag(String tagName,Long regTime);
    /**
     * 根据标签Id获取标签
     * @param tagId
     * @return
     */
    public Tag getTag(Long tagId);
    /**
     * 添加一个分类
     * 
     * @param classify
     * @param flag
     * @return
     */
    public boolean addClassify(Classify classify);
    
    /**
     * 添加一个一级二级分类关系
     * 
     * @param classifyRelation
     * @return
     */
    public boolean addClassifyRelation(ClassifyRelation classifyRelation);
    
    /**
     * 添加一个标签
     * 
     * @param tag
     * @return
     */
    public boolean addTag(Tag tag);
    
    /**
     * 添加一个标签-分类关系
     * 
     * @param tagClassifyRelation
     * @return
     */
    public boolean addTagClassify(TagClassify tagClassify);
    
    /**
     * 删除一个标签，同时删除标签-二级分类关系
     * 
     * @param tagId
     * @return
     */
    public boolean removeTag(Long tagId);
    
    /**
     * 删除一个二级分类，前提是二级分类下已没有标签，否则失败
     * 
     * @param childId
     * @return
     */
    public boolean removeChildClassify(Long childId);
    
    /**
     * 删除一个一级分类，前提是一级分类下已没有二级分类，否则失败
     * 
     * @param parentId
     * @return
     */
    public boolean removeParentClassify(Long parentId);
    
    /**
     * 更新一个一级分类的name和description
     * 
     * @param parentClassify
     * @return
     */
    public boolean updateClassify(Classify classify);
    
    /**
     * 更新一个Tag
     * 
     * @param tag
     * @return
     */
    public boolean updateTag(Tag tag);
    
    /**
     * 将string转成List<Long>
     * 
     * @param idStr
     * @return
     */
    public List<Long> seperateIds(String idStr);

    public List<TagStatisticsVo> getEffectiveTagView(List<Tag> tags);
    public List<TagStatisticsVo> getAllTagView(List<Tag> tags);
    public Map<String, List<Long>> getTagStatistics(Tag tag);
    /**
     * 添加一条TagPopulationStatistics记录
     * 
     * @param tagPopulationStatistics
     * @return
     */
    public boolean addTagStatistics(TagStatistics tagStatistics);

    public boolean removeTagClassify(Long tagId, Long childId);
    public boolean tagNameVarify(String tagName);
    public boolean parentNameVarify(String parentName);
    public boolean childNameVarify(String childName);
    
    public boolean isIntegrationTag(Long tagId);
    public boolean addTagIntegration(TagIntegration tagIntegration);
    public List<Tag> getTagsLike(String tagName);
    
}
