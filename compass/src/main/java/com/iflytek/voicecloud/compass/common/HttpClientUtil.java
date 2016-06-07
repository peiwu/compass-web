package com.iflytek.voicecloud.compass.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author kwliu
 * @date 下午3:02:37, 2015年10月9日
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil
{
    @SuppressWarnings({"resource", "unchecked"})
    public static List<Long> getTagIdsFromDevice(String deviceNumber)
        throws ClientProtocolException, IOException
    {
        List<Long> tagIds = new ArrayList<Long>();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpgets = new HttpGet(Constant.httpUrl+"/dmp/imei/_search?q=did:" + deviceNumber);
        HttpResponse response = httpclient.execute(httpgets);
        HttpEntity entity = response.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        if (entity != null)
        {
            InputStream instreams = entity.getContent();
            String str = HttpClientUtil.convertStreamToString(instreams);
            // 下面是对str进行解析，获取所有的tagId，然后就调用tagIdsearch就可以了
            Map<String, Object> jsonMap = objectMapper.readValue(str, Map.class);
            Map<String, Object> jsonMap1 = (Map<String, Object>)(jsonMap.get("hits"));
            List<Map<String,Object>> listmap=(List<Map<String,Object>>)jsonMap1.get("hits");
           // Map<String, Object> jsonMap2 = (Map<String, Object>)(jsonMap1.get("hits"));
            Map<String, Object> jsonMap3 = (Map<String, Object>)(listmap.get(0).get("_source"));
            if(jsonMap3!=null){
                for (String key : jsonMap3.keySet())
                {
                    if (!key.equals("dvc") && !key.equals("classify")&&!key.equals("idtype")&&!key.equals("did"))
                    {
                        tagIds.add(Long.parseLong(key));
                    }
                } 
            }
            httpgets.abort();
        }
        return tagIds;
    }
    
    @SuppressWarnings({"resource", "unchecked"})
    public static Long getTagPopulation(String tagId)
        throws ClientProtocolException, IOException
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpgets = new HttpGet(Constant.httpUrl+"/dmp/imei/_count?q=_all:"+tagId);
        HttpResponse response = httpclient.execute(httpgets);
        HttpEntity entity = response.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        Long population = 0L;
        if (entity != null)
        {
            InputStream instreams = entity.getContent();
            String str = HttpClientUtil.convertStreamToString(instreams);
            
            Map<String, Object> jsonMap = objectMapper.readValue(str, Map.class);
            
            population = Long.valueOf(jsonMap.get("count").toString());
            
            httpgets.abort();
        }
        return population;
    }
    
    @SuppressWarnings({"resource", "unchecked"})
    public static Long getSubTagPopulation(String url)
        throws ClientProtocolException, IOException
    {
        
        HttpClient httpclient = new DefaultHttpClient();
        
        HttpGet httpgets = new HttpGet(Constant.httpUrl+"/dmp/_count?q=" + url);
        
        HttpResponse response = httpclient.execute(httpgets);
        HttpEntity entity = response.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        Long population = 0L;
        if (entity != null)
        {
            InputStream instreams = entity.getContent();
            String str = HttpClientUtil.convertStreamToString(instreams);
            
            Map<String, Object> jsonMap = objectMapper.readValue(str, Map.class);
            
            population = Long.valueOf(jsonMap.get("count").toString());
            
            httpgets.abort();
        }
        return population;
    }
    
    @SuppressWarnings("resource")
    public static Long getTagsByChildId(Long childId) throws ClientProtocolException, IOException{
     // 创建HttpClient实例
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpgets1 = new HttpGet(Constant.httpUrl+"/dmp/_count?q=classify:" + childId);
        HttpResponse response1 = httpclient.execute(httpgets1);
        HttpEntity entity1 = response1.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        Long childTagsCount = 0L;
        if (entity1 != null)
        {
            InputStream instreams = entity1.getContent();
            Map<String, Object> jsonMap = objectMapper.readValue(convertStreamToString(instreams), Map.class);
            childTagsCount = Long.valueOf(jsonMap.get("count").toString());
            httpgets1.abort();
        }
        return childTagsCount*100;
    }
    @SuppressWarnings("resource")
    public static Long getAllTags() throws ClientProtocolException, IOException{
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpgets2 = new HttpGet(Constant.httpUrl+"/dmp/_count?q=classify:*");
        HttpResponse response2 = httpclient.execute(httpgets2);
        HttpEntity entity2 = response2.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        Long totalCount = 1L;
        if (entity2 != null)
        {
            InputStream instreams = entity2.getContent();
            Map<String, Object> jsonMap = objectMapper.readValue(convertStreamToString(instreams), Map.class);
            totalCount = Long.valueOf(jsonMap.get("count").toString());
            httpgets2.abort();
        }
        return totalCount;
    }
    
    public static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
