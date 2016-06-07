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
 * @date 上午10:44:04, 2015年10月8日
 */
@Entity
@Table(name = "tag_integration")
public class TagIntegration
{
    private Long id;
    
    private Long tagIntegrationId;
    
    private Long tagId;
    
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
    
    @Column(name = "tag_integration_id")
    public Long getTagIntegrationId()
    {
        return tagIntegrationId;
    }
    
    public void setTagIntegrationId(Long tagIntegrationId)
    {
        this.tagIntegrationId = tagIntegrationId;
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
    
}
