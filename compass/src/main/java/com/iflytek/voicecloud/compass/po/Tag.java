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
 * @date 下午5:12:38, 2015年8月25日
 */
@Entity
@Table(name = "tag")
public class Tag
{
    private Long tagId;
    
    private String tagName; // 标签名称
    
    private String tagDescription; // 标签描述
    
    private String updateGranularity; // 更新粒度
    
    private int updateSpan; // 更新跨度
    
    private int isAct; // 是否激活 0-激活 1-未激活
    
    private int regUser; // 录入人
    
    private Long regTime; // 录入时间
    
    private Long updateTime; // 修改时间
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", unique = true, nullable = false)
    public Long getTagId()
    {
        return tagId;
    }
    
    public void setTagId(Long tagId)
    {
        this.tagId = tagId;
    }
    
    @Column(name = "tag_name")
    public String getTagName()
    {
        return tagName;
    }
    
    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }
    
    @Column(name = "tag_description")
    public String getTagDescription()
    {
        return tagDescription;
    }
    
    public void setTagDescription(String tagDescription)
    {
        this.tagDescription = tagDescription;
    }
    
    @Column(name = "update_granularity")
    public String getUpdateGranularity()
    {
        return updateGranularity;
    }
    
    public void setUpdateGranularity(String updateGranularity)
    {
        this.updateGranularity = updateGranularity;
    }
    
    @Column(name = "update_span")
    public int getUpdateSpan()
    {
        return updateSpan;
    }
    
    public void setUpdateSpan(int updateSpan)
    {
        this.updateSpan = updateSpan;
    }
    
    @Column(name = "is_act")
    public int getIsAct()
    {
        return isAct;
    }
    
    public void setIsAct(int isAct)
    {
        this.isAct = isAct;
    }
    
    @Column(name = "reg_user")
    public int getRegUser()
    {
        return regUser;
    }
    
    public void setRegUser(int regUser)
    {
        this.regUser = regUser;
    }
    
    @Column(name = "reg_time")
    public Long getRegTime()
    {
        return regTime;
    }
    
    public void setRegTime(Long regTime)
    {
        this.regTime = regTime;
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
