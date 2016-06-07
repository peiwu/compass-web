package com.iflytek.voicecloud.compass.service;
import java.util.List;

import com.iflytek.voicecloud.compass.po.Authority;
/**
 * 
 * @author	kwliu
 * @date	上午8:41:40, 2015年11月30日
 */
public interface IAuthorityService
{
    public List<Authority> getAllAuthorities();
    public int getAuthorityByUserName(String username);
    public boolean removeAuthority(String username);
    public boolean addAuthority(String username, int auth);
    
}

