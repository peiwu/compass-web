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
@Table(name = "redis")
public class Redis
{
    private Long id;
    private String path; 
    private String person; 
    private int state; 
    private int type;
    private Long num;
    private Long startDate; 
    private Long endDate; 
    private Long costTime; 
    private String prefix;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long Id)
    {
        this.id = Id;
    }
    
    
    @Column(name = "path")
    public String getPath()
    {
        return path;
    }
    public void setPath(String Path)
    {
        this.path = Path;
    }
    
    @Column(name = "person")
    public String getPerson()
    {
        return person;
    }
    public void setPerson(String Person)
    {
        this.person = Person;
    }
    
    @Column(name = "type")
    public int getType()
    {
        return type;
    }
    
    public void setType(int Type)
    {
        this.type = Type;
    }
    
    @Column(name = "state")
    public int getState()
    {
        return state;
    }
    
    public void setState(int State)
    {
        this.state = State;
    }
    
    @Column(name = "num")
    public Long getNum()
    {
        return num;
    }
    
    public void setNum(Long Num)
    {
        this.num = Num;
    }
    
    @Column(name = "start_date")
    public Long getStartDate()
    {
        return startDate;
    }
    
    public void setStartDate(Long datetime)
    {
        this.startDate = datetime;
    }
    
    @Column(name = "end_date")
    public Long getEndDate()
    {
        return endDate;
    }
    
    public void setEndDate(Long datetime)
    {
        this.endDate = datetime;
    }
    
    @Column(name = "cost_time")
    public Long getCostTime()
    {
        return costTime;
    }
    
    public void setCostTime(Long datetime)
    {
        this.costTime = datetime;
    }
    
    @Column(name = "prefix")
    public String getPrefix()
    {
        return prefix;
    }
    public void setPrefix(String Prefix)
    {
        this.prefix = Prefix;
    }
}

