package com.iflytek.voicecloud.compass.service;

import java.util.List;

import com.iflytek.voicecloud.compass.po.Redis;

public interface IMonitorService {
	  List<Redis> getSelectRedis(String type,String state,String person,long start,long end);
	  public List<String> getAllPerson();
}
