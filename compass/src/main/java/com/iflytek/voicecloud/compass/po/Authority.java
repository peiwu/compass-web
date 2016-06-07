package com.iflytek.voicecloud.compass.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author	kwliu
 * @date	上午8:38:20, 2015年11月30日
 */
@Entity
@Table(name = "authority")
public class Authority
{
    private int id;
    private String username;
    private int authority;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    @Column(name = "username")
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    @Column(name = "authority")
    public int getAuthority()
    {
        return authority;
    }
    public void setAuthority(int authority)
    {
        this.authority = authority;
    }
}

