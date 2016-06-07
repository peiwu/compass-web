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
 * @date 下午5:13:24, 2015年8月25日
 */
@Entity
@Table(name = "classify")
public class Classify
{
    private Long classifyId;
    
    private String classifyName;
    
    private String classifyDescription;
    
    private int isParent; // 是否为一级分类：0-父类 1-子类
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classify_id", unique = true, nullable = false)
    public Long getClassifyId()
    {
        return classifyId;
    }
    
    public void setClassifyId(Long classifyId)
    {
        this.classifyId = classifyId;
    }
    
    @Column(name = "classify_name")
    public String getClassifyName()
    {
        return classifyName;
    }
    
    public void setClassifyName(String classifyName)
    {
        this.classifyName = classifyName;
    }
    
    @Column(name = "classify_description")
    public String getClassifyDescription()
    {
        return classifyDescription;
    }
    
    public void setClassifyDescription(String classifyDescription)
    {
        this.classifyDescription = classifyDescription;
    }
    @Column(name = "level")
    public int getIsParent()
    {
        return isParent;
    }

    public void setIsParent(int isParent)
    {
        this.isParent = isParent;
    }
    
    
    
}
