package com.iflytek.voicecloud.compass.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author kwliu
 * @date 下午5:12:53, 2015年8月25日
 */
@Entity
@Table(name = "tag_statistics")
public class TagStatistics
{
    private Long id; 
    
    private Long tagId; // 标签ID
    
    private Long population; // 覆盖人群
    
    private Long updateTime; // 更新时间
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    @Column(name = "tag_id")
    public Long getTagId()
    {
        return tagId;
    }
    
    public void setTagId(Long tagId)
    {
        this.tagId = tagId;
    }
    
    @Column(name = "population")
    public Long getPopulation()
    {
        return population;
    }
    
    public void setPopulation(Long population)
    {
        this.population = population;
    }
    
    @Column(name = "update_time")
    public Long getUpdateTime()
    {
        return updateTime;
    }
    
    public void setUpdateTime(Long updateTime)
    {
        this.updateTime = updateTime;
    }
    
}
