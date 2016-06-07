package com.iflytek.voicecloud.compass.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iflytek.voicecloud.common.dbutils.WhereList;
import com.iflytek.voicecloud.common.dbutils.DbAccessor;
import com.iflytek.voicecloud.compass.dao.impl.RedisDao;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.service.IMonitorService;

@Service
public class MonitorService implements IMonitorService {
	@Autowired
	private RedisDao redisDao;
	
	 @Autowired
	 DbAccessor dbAccessor;
	 
	   @Override
		@Transactional(propagation = Propagation.REQUIRED)
	    public List<String> getAllPerson()
	    {
	   String sql = "select username from authority";
	   List<String> result=new ArrayList<String>();
       List list = dbAccessor.sqlQuery(sql);
        for(int i=0;i<list.size();i++) result.add((String)list.get(i));
        return list;
	    }
	    @Override
		@Transactional(propagation = Propagation.REQUIRED)
	    public List<Redis> getSelectRedis(String type,String state,String person,long start,long end)
	    {
	    	int Type,State;
	    	if(type.equals("0")) Type=0;
	    	else if(type.equals("1")) Type=1;
	    	else Type=99;
	    	if(state.equals("0")) State=0;
	    	else if(state.equals("1")) State=1;
	    	else if(state.equals("2")) State=2;		
	    	else State=99;
	    	WhereList whereList = new WhereList();
	    	if(person.equals("all")){
	    	if(Type!=99&&State!=99)
	        whereList.format("type = ? and state=? and startDate>=? and startDate<=?" , Type,State,start,end);
	    	else if(Type==99&&State!=99)  whereList.format("state=? and startDate>=? and startDate<=?",State,start,end);
	    	else if(Type!=99&&State==99)  whereList.format("type=? and startDate>=? and startDate<=?",Type,start,end);
	    	else  whereList.format("startDate>=? and startDate<=?",start,end);
	    	}
	        else{
	        	if(Type!=99&&State!=99)
	    	        whereList.format("type = ? and state=? and person=? and startDate>=? and startDate<=?", Type,State,person,start,end);
	    	    	else if(Type==99&&State!=99)  whereList.format("state=? and person=? and startDate>=? and startDate<=?",State,person,start,end);
	    	    	else if(Type!=99&&State==99)  whereList.format("type=? and person=? and startDate>=? and startDate<=?",Type,person,start,end);
	    	    	else  whereList.format("person=? and startDate>=? and startDate<=?",person,start,end);
	        }
	    	 List<Redis> redislist = redisDao.getList(Redis.class, whereList);
	        if (redislist.isEmpty())
	        {
	            return new ArrayList<Redis>();
	        }
	        else
	        {
	            return redislist;
	        }
	    }
}
