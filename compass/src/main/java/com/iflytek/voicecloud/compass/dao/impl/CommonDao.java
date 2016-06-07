package com.iflytek.voicecloud.compass.dao.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.iflytek.voicecloud.common.dbutils.DbAccessor;
import com.iflytek.voicecloud.common.dbutils.QueryInfo;
import com.iflytek.voicecloud.common.dbutils.UpdateInfo;
import com.iflytek.voicecloud.common.dbutils.WhereList;
import com.iflytek.voicecloud.compass.dao.ICommonDao;

/**
 * 
 * @author kwliu
 * @date 下午2:28:55, 2015年8月25日
 */
@Repository
public class CommonDao implements ICommonDao
{
    @Autowired
    private DbAccessor dbAccessor;
    
    @Override
	public void addObject(Object object)
    {
        dbAccessor.insert(object);
    }
    
    @Override
	public <T> T getObject(Class<T> clazz, int id)
    {
        return dbAccessor.getObject(clazz, id);
    }
    
    @Override
	public void updateObject(Object object)
    {
        dbAccessor.update(object);
    }
    
    @Override
	public int updateObject(Class<?> clazz, UpdateInfo updateInfo)
    {
        return dbAccessor.update(clazz, updateInfo);
    }
    
    @Override
	public void delete(Object object)
    {
        dbAccessor.delete(object);
    }
    
    @Override
	public int delete(Class<?> clazz, WhereList whereList)
    {
        return dbAccessor.delete(clazz, whereList);
    }
    
    @Override
	public <T> T getObject(Class<T> clazz, WhereList whereList)
    {
        return dbAccessor.getObject(clazz, whereList);
    }
    
    @Override
	public <T> List<T> getList(Class<T> clazz, WhereList whereList)
    {
        return dbAccessor.getList(clazz, whereList);
    }
    
    @Override
	public <T> List<T> getList(Class<T> clazz, QueryInfo queryInfo)
    {
        return dbAccessor.getList(clazz, queryInfo);
    }
}
