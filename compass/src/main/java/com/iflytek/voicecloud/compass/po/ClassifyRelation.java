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
 * @date 下午5:13:35, 2015年8月25日
 */
@Entity
@Table(name = "classify_relation")
public class ClassifyRelation
{
    private Long id;
    
    private Long parentId; // 一级分类Id
    
    private Long childId; // 二级分类Id
    
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
    
    @Column(name = "parent_id")
    public Long getParentId()
    {
        return parentId;
    }
    
    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }
    
    @Column(name = "classify_id")
    public Long getChildId()
    {
        return childId;
    }
    
    public void setChildId(Long childId)
    {
        this.childId = childId;
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
