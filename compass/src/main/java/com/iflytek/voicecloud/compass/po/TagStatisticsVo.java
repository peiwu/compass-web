package com.iflytek.voicecloud.compass.po;

/**
 * 
 * @author	kwliu
 * @date	下午1:43:31, 2015年12月9日
 */
public class TagStatisticsVo
{
    private Long tagId;
    private String tagName;
    private String tagDescritpion;
    private Long totalPopulation;//更新跨度内的总用户数  
    private Long latestUpdateTime; //最新更新时间
    private String updateGranularity;
    private int updateSpan;
    public Long getTagId()
    {
        return tagId;
    }
    public void setTagId(Long tagId)
    {
        this.tagId = tagId;
    }
    public String getTagName()
    {
        return tagName;
    }
    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }
    public String getTagDescritpion()
    {
        return tagDescritpion;
    }
    public void setTagDescritpion(String tagDescritpion)
    {
        this.tagDescritpion = tagDescritpion;
    }

    public Long getTotalPopulation()
    {
        return totalPopulation;
    }
    public void setTotalPopulation(Long totalPopulation)
    {
        this.totalPopulation = totalPopulation;
    }
    public Long getLatestUpdateTime()
    {
        return latestUpdateTime;
    }
    public void setLatestUpdateTime(Long latestUpdateTime)
    {
        this.latestUpdateTime = latestUpdateTime;
    }
    public String getUpdateGranularity()
    {
        return updateGranularity;
    }
    public void setUpdateGranularity(String updateGranularity)
    {
        this.updateGranularity = updateGranularity;
    }
    public int getUpdateSpan()
    {
        return updateSpan;
    }
    public void setUpdateSpan(int updateSpan)
    {
        this.updateSpan = updateSpan;
    }
    
}

