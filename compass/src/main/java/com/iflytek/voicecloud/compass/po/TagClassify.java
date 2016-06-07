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
 * @date 下午5:13:12, 2015年8月25日
 */
@Entity
@Table(name = "tag_classify")
public class TagClassify
{
    private Long id;  
    
    private Long tagId;  // 标签Id
    
    private Long classifyId; // 二级分类Id
    
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
    
    @Column(name = "classify_id")
    public Long getClassifyId()
    {
        return classifyId;
    }
    
    public void setClassifyId(Long classifyId)
    {
        this.classifyId = classifyId;
    }
    
}
