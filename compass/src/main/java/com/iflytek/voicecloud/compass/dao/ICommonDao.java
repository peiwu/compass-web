package com.iflytek.voicecloud.compass.dao;

import java.util.List;

import com.iflytek.voicecloud.common.dbutils.QueryInfo;
import com.iflytek.voicecloud.common.dbutils.UpdateInfo;
import com.iflytek.voicecloud.common.dbutils.WhereList;

/**
 * 
 * @author kwliu
 * @date 下午2:24:16, 2015年8月25日
 */
public interface ICommonDao
{
    public void addObject(Object object);
    
    public <T> T getObject(Class<T> clazz, int id);
    
    public <T> T getObject(Class<T> clazz, WhereList whereList);
    
    public void updateObject(Object object);
    
    public int updateObject(Class<?> clazz, UpdateInfo updateInfo);
    
    public void delete(Object object);
    
    public int delete(Class<?> clazz, WhereList whereList);
    
    public <T> List<T> getList(Class<T> clazz, WhereList whereList);
    
    public <T> List<T> getList(Class<T> clazz, QueryInfo queryInfo);
}
