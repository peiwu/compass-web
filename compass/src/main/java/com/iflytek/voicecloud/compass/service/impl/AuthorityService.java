package com.iflytek.voicecloud.compass.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iflytek.voicecloud.common.dbutils.WhereList;
import com.iflytek.voicecloud.compass.service.IAuthorityService;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.dao.impl.AuthorityDao;
import com.iflytek.voicecloud.compass.po.Authority;

/**
 * 
 * @author kwliu
 * @date 上午8:41:51, 2015年11月30日
 */
@Service
public class AuthorityService implements IAuthorityService
{
    @Autowired
    private AuthorityDao authorityDao;
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Authority> getAllAuthorities()
    {
        WhereList whereList = new WhereList();
        whereList.format("id is not null");
        List<Authority> authorities = authorityDao.getList(Authority.class, whereList);
        if (authorities.isEmpty())
        {
            return new ArrayList<Authority>();
        }
        else
        {
            return authorities;
        }
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public int getAuthorityByUserName(String username)
    {
        WhereList whereList = new WhereList();
        whereList.format("username = ?", username);
        Authority authority = authorityDao.getObject(Authority.class, whereList);
        if (authority != null)
        {
            return authority.getAuthority();
        }
        else
        {
            return Constant.NORMAL_USER;
        }
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean removeAuthority(String username){
        WhereList whereList = new WhereList();
        whereList.format("username = ?", username);
        Authority authority = authorityDao.getObject(Authority.class, whereList);
        if(authority == null){
            return false;
        }
        else{
            authorityDao.delete(authority);
            return true;
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addAuthority(String username, int auth){
        if(getAuthorityByUserName(username)==Constant.NORMAL_USER){
            Authority authority = new Authority();
            authority.setAuthority(auth);
            authority.setUsername(username);
            authorityDao.addObject(authority);
            return true;
        }
        else{
            return false;
        }
        
    }
   
}
