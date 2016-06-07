package com.iflytek.voicecloud.compass.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iflytek.voicecloud.common.dbutils.DbAccessor;
import com.iflytek.voicecloud.common.dbutils.QueryInfo;
import com.iflytek.voicecloud.common.dbutils.WhereList;
import com.iflytek.voicecloud.compass.common.Constant;
import com.iflytek.voicecloud.compass.common.HttpClientUtil;
import com.iflytek.voicecloud.compass.dao.IClassifyDao;
import com.iflytek.voicecloud.compass.dao.IClassifyRelationDao;
import com.iflytek.voicecloud.compass.dao.IRedisDao;
import com.iflytek.voicecloud.compass.dao.ITagClassifyDao;
import com.iflytek.voicecloud.compass.dao.ITagDao;
import com.iflytek.voicecloud.compass.dao.ITagIntegrationDao;
import com.iflytek.voicecloud.compass.dao.ITagStatisticsDao;
import com.iflytek.voicecloud.compass.po.Classify;
import com.iflytek.voicecloud.compass.po.ClassifyRelation;
import com.iflytek.voicecloud.compass.po.Redis;
import com.iflytek.voicecloud.compass.po.Tag;
import com.iflytek.voicecloud.compass.po.TagClassify;
import com.iflytek.voicecloud.compass.po.TagIntegration;
import com.iflytek.voicecloud.compass.po.TagStatistics;
import com.iflytek.voicecloud.compass.po.TagStatisticsVo;
import com.iflytek.voicecloud.compass.service.ITagAdminService;


/**
 * 
 * @author kwliu
 * @date 下午2:41:04, 2015年9月2日
 */
@Service
public class TagAdminService implements ITagAdminService
{
    @Autowired
    private IClassifyDao classifyDao;
    
    @Autowired
    private IClassifyRelationDao classifyRelationDao;
    
    @Autowired
    private ITagClassifyDao tagClassifyDao;
    
    @Autowired
    private ITagDao tagDao;
    
    @Autowired
    private ITagStatisticsDao tagStatisticsDao;
    
    @Autowired
    private ITagIntegrationDao tagIntegrationDao;
    
   @Autowired
   private IRedisDao redisDao;
    
    @Autowired
    DbAccessor dbAccessor;
    
    private Client client;
    //查询设备号
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
	public List<Long> getTagIdsFromDevice(String dvcnumber)
	    {
	         List<Long> tagids=new ArrayList<Long>(); 
	         Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
	        Client client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
	    	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,FilterBuilders.termFilter("did",dvcnumber));
			SearchResponse sresponse = client.prepareSearch("dmp")
					.setTypes("imei")
					.setQuery(filterbuilder)
			     	.addAggregation(AggregationBuilders.terms("tagsgroup").field("taglist").size(Integer.MAX_VALUE))
			        .execute()
			        .actionGet();
			Aggregations a=sresponse.getAggregations();
			Terms term = a.get("tagsgroup");
			for(Bucket bc:term.getBuckets())
			{
				String name=""+bc.getKey();
				tagids.add(Long.valueOf(name));
			}
			return tagids;
	    }
    
    //查询多个标签的设备号
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<String> getDvcByTags(String[] tagIds) throws ClientProtocolException, IOException
    {   
       List<String> dvcs=new ArrayList<String>();
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
    	client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
    	BoolFilterBuilder bool=FilterBuilders.boolFilter();
    	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("taglist",tagIds[i]));
    	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
        SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
       .setScroll(TimeValue.timeValueMinutes(60000))
       .setQuery(filterbuilder)
       .setSize(500)
       .execute()
       .actionGet();
       String dvc="";
       long num=0L;
       SearchHits hits ;
       SearchHit[] searchHists ;
       while(true){
       hits = searchResponse.getHits();
       searchHists = hits.getHits(); 
       if(searchHists.length==0||num==50000L) {client.close();break;}
       num+=searchHists.length;
       System.out.println(num);
       for(SearchHit hint : searchHists ){
    	       dvc=(String) hint.getSource().get("dvc");
    	       dvcs.add(dvc);
                                          }
               searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
    	      .setScroll(TimeValue.timeValueMinutes(60000))
    	      .execute()
    	      .actionGet();
                  }
        return dvcs;
    }
    @Override
 	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkPrefix(String username, String prefix) {
    	 WhereList whereList = new WhereList();
         whereList.format("person!=? and prefix =?",username,prefix);
  	  // whereList.format("exp_date =?",writetime);
         if(redisDao.getList(Redis.class,whereList).size()==0) return true;
         else return false;
	}
    
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
       public boolean addRedis(Redis redis)
       {
           redisDao.addObject(redis);
           return true;
       }
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
    public Redis getRedis(String username,Long writetime)
       {
    	   WhereList whereList = new WhereList();
           whereList.format("person=? and startDate =?",username,writetime);
    	  // whereList.format("exp_date =?",writetime);
           return redisDao.getObject(Redis.class,whereList);
       }
    
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
    public Redis getRedisById(Long id)
       {
    	   WhereList whereList = new WhereList();
    	   whereList.format("id =?",id);
           return redisDao.getObject(Redis.class,whereList);
       }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean updateRedis(Redis redis)
    {
        redisDao.updateObject(redis);
        return true;
    }
    
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteRedis(Long id)
       {
    	   WhereList whereList = new WhereList();
    	   whereList.format("id =?",id);
           redisDao.delete(redisDao.getObject(Redis.class,whereList));
           return true;
       }
    @Override
   	@Transactional(propagation = Propagation.REQUIRED)
    public List<Redis> getNoOkRedis(String username)
       {
    	   WhereList whereList = new WhereList();
    	   whereList.format("person=? and state =? and type=?",username,0,1);
         //  whereList.format("state =?",0);
           return redisDao.getList(Redis.class, whereList);
       }
    
  @Override
@Transactional(propagation = Propagation.REQUIRED)
  public boolean isRelation(Long childid,Long tagid){
	  WhereList whereList = new WhereList();
      whereList.format("tag_id=? and classify_id =?",tagid,childid);
      TagClassify temp=tagClassifyDao.getObject(TagClassify.class, whereList);
      if(temp==null) return false;
      else return true; 
  }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Classify getParentByChild(Long childid){
    	 WhereList whereList = new WhereList();
         whereList.format("classify_id=?",childid);
         ClassifyRelation temp=classifyRelationDao.getObject(ClassifyRelation.class, whereList);
         WhereList whereList1 = new WhereList();
         whereList1.format("classify_id=?",temp.getParentId());
         return classifyDao.getObject(Classify.class, whereList1);
    }
   
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Classify getChildById(Long childid){
    	 WhereList whereList = new WhereList();
         whereList.format("classify_id=?",childid);
         return classifyDao.getObject(Classify.class, whereList);
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Long getPopulationByTags(List<String> tagIds) throws ClientProtocolException, IOException
    {   
       List<String> dvcs=new ArrayList<String>();
      // Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
    	client= new TransportClient().addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9300));
    	BoolFilterBuilder bool=FilterBuilders.boolFilter();
    	for(int i=0;i<tagIds.size();i++) bool.must(FilterBuilders.termFilter("_all",tagIds.get(i)));
    	FilterBuilder filterbuilder=bool;
       SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
       .setPostFilter(filterbuilder)
       .execute()
       .actionGet();
       Long num= searchResponse.getHits().getTotalHits();
        return num;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Classify> getParentClassifies()
    {
        WhereList whereList = new WhereList();
        whereList.format("level = ? ", Constant.PARENT_CLASSIFY_TYPE);
        List<Classify> result=new ArrayList<Classify>();
        Logger logger = Logger.getLogger("E"); 
        try{
        result=classifyDao.getList(Classify.class, whereList);
        }
        catch(Exception e){
        logger.error(e.getMessage(),e);
        }
        return result;
        //return classifyDao.getList(Classify.class, whereList);
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Classify> getChildClassifies(Long parentId)
    {
        WhereList whereList = new WhereList();
        whereList.format("parent_id = ?", parentId);
        List<ClassifyRelation> classifyRelations = classifyRelationDao.getList(ClassifyRelation.class, whereList);
        if (classifyRelations.isEmpty())
        {
            return new ArrayList<Classify>();
        }
        else{
         // 获取所有childId;
            WhereList whereList2 = new WhereList();
            String sql = "classify_id in (";
            for (ClassifyRelation classifyRelation : classifyRelations)
            {
                sql += classifyRelation.getChildId() + ",";
            }
            // 去掉最后一个
            sql = sql.substring(0, sql.length() - 1) + ")";
            whereList2.format(sql);
            return classifyDao.getList(Classify.class, whereList2); 
        }
        
    }
    
    /**
     * 要加上分页查询
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> getTags(Long childId, int pageIndex, int pageSize)
    {
        WhereList whereList = new WhereList();
        whereList.format("classify_id = ?", childId);
        List<TagClassify> tagClassifys =
            tagClassifyDao.getList(TagClassify.class, whereList);
      
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(tagClassifys.isEmpty()){
            resultMap.put("tags", new ArrayList<Tag>());
            resultMap.put("totalCount", 0L);
            return resultMap;
        }
        String sql = "tag_id in (";
        for (TagClassify tagClassify : tagClassifys)
        {
            sql += tagClassify.getTagId() + ",";
        }
        // 去掉最后一个
        sql = sql.substring(0, sql.length() - 1) + ") and is_act = "+Constant.ACT;
        
        // 进行分页查询
        QueryInfo queryInfo = new QueryInfo();
   
        queryInfo.getWhereList().format(sql);
        
        queryInfo.setPageIndex(pageIndex);
        queryInfo.setPageSize(pageSize);
        resultMap.put("tags", tagDao.getList(Tag.class, queryInfo));
        resultMap.put("totalCount", queryInfo.getTotalCount());
        return resultMap;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Tag> getTags(Long childId)
    {
        WhereList whereList = new WhereList();
        whereList.format("classify_id = ?", childId);
        List<TagClassify> tagClassifys =
            tagClassifyDao.getList(TagClassify.class, whereList);
        String sql = "tag_id in (";
        for (TagClassify tagClassify : tagClassifys)
        {
            sql += tagClassify.getTagId() + ",";
        }
        // 去掉最后一个
        sql = sql.substring(0, sql.length() - 1) + ") and is_act = "+Constant.ACT; 
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.getWhereList().format(sql);
        List<Tag> result=tagDao.getList(Tag.class, queryInfo);
        return result;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Classify> getChildClassifiesByTag(Long tagId){
        WhereList whereList = new WhereList();
        whereList.format("tag_id = ?", tagId);
        List<TagClassify> tagClassifies = tagClassifyDao.getList(TagClassify.class, whereList);
        if (tagClassifies.isEmpty())
        {
            return new ArrayList<Classify>();
        }
        else{
            // 获取所有childId;
            WhereList whereList2 = new WhereList();
            String sql = "classify_id in (";
            for (TagClassify tagClassify : tagClassifies)
            {
                sql += tagClassify.getClassifyId() + ",";
            }
            // 去掉最后一个
            sql = sql.substring(0, sql.length() - 1) + ")";
            whereList2.format(sql);
            return classifyDao.getList(Classify.class, whereList2); 
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Classify> getAllChildClassifies(){
        WhereList whereList = new WhereList();
        whereList.format("level = ?", Constant.CHILD_CLASSIFY_TYPE);
        return classifyDao.getList(Classify.class, whereList);
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addClassify(Classify classify)
    {
        //查classify是否有同名的，返回false
        WhereList whereList = new WhereList();
        whereList.format("classify_name = ?", classify.getClassifyName());
        if(classifyDao.getObject(Classify.class, whereList)!=null){
            return false;
        }
        classifyDao.addObject(classify);
        return true;
        
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addClassifyRelation(ClassifyRelation classifyRelation)
    {
        // 先判断这个关系是否已经存在，已经存在则不添加，不存在，则添加
        WhereList whereList = new WhereList();
        whereList.format("parent_id = ? and classify_id = ?",
            classifyRelation.getParentId(),
            classifyRelation.getChildId());
        if (classifyRelationDao.getObject(ClassifyRelation.class, whereList) == null)
        {
            classifyRelationDao.addObject(classifyRelation);
        }
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Classify getClassify(String classifyName)
    {
        WhereList whereList = new WhereList();
        whereList.format("classify_name = ?", classifyName);
        return classifyDao.getObject(Classify.class, whereList);
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addTag(Tag tag)
    {
        //查tag是否有同名的，返回false
       // WhereList whereList = new WhereList();
        //whereList.format("tag_name = ?", tag.getTagName());
        //Tag tag2 = tagDao.getObject(Tag.class, whereList);
       // if(tag2==null){
            tagDao.addObject(tag);
            return true;
       // }
        /*else{
            if(tag2.getIsAct()==Constant.NOT_ACT){
                tag2.setIsAct(Constant.ACT);
                tag2.setRegTime(tag.getRegTime());
                tag2.setRegUser(tag.getRegUser());
                tag2.setTagDescription(tag.getTagDescription());
                tag2.setUpdateGranularity(tag.getUpdateGranularity());
                tag2.setUpdateSpan(tag.getUpdateSpan());
                tag2.setUpdateTime(tag.getUpdateTime());
                tagDao.updateObject(tag2);
                return true;
            }
            else{
                return false;
            }
        }*/
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addTagClassify(TagClassify tagClassify)
    {
        // 先判断这个关系是否已经存在，已经存在则不添加，不存在，则添加
       // WhereList whereList = new WhereList();
        //whereList.format("tag_id = ? and classify_id = ?", tagClassify.getTagId(), tagClassify.getClassifyId());
        //TagClassify tagClassify2 = tagClassifyDao.getObject(TagClassify.class, whereList);
        //if (tagClassify2 == null)
        //{
            tagClassifyDao.addObject(tagClassify);
        //}
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Tag getTag(String tagName,Long regTime)
    {
        WhereList whereList = new WhereList();
        whereList.format("tag_name = ? and is_act = ? and reg_time= ?", tagName, Constant.ACT,regTime);
        return tagDao.getObject(Tag.class, whereList);
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean removeTag(Long tagId)
    {
        // 删除tag记录
        WhereList whereList = new WhereList();
        whereList.format("tag_id = ? and is_act = ?", tagId, Constant.ACT);
        // 将isAct改成not_act即表明删除了
        Tag tag = tagDao.getObject(Tag.class, whereList);
        tag.setIsAct(Constant.NOT_ACT);
        tagDao.updateObject(tag);
       
        // 删除tag_classify_relation记录
        WhereList whereList2 = new WhereList();
        whereList2.format("tag_id = ?", tagId);
        tagClassifyDao.delete(TagClassify.class, whereList2);
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean removeChildClassify(Long childId)
    {
        // 首先判断二级分类下是否还有标签，查tag_classify_relation表
        WhereList whereList = new WhereList();
        whereList.format("classify_id = ?", childId);
        List<TagClassify> tagClassifys =
            tagClassifyDao.getList(TagClassify.class, whereList);
        if (!tagClassifys.isEmpty())
        {
            return false;
        }
        WhereList whereList2 = new WhereList();
        whereList2.format("classify_id = ? and level = ?", childId, Constant.CHILD_CLASSIFY_TYPE);
        classifyDao.delete(Classify.class, whereList2);
        //还要删除其与一级分类的关系
        WhereList whereList3 = new WhereList();
        whereList3.format("classify_id = ?", childId);
        classifyRelationDao.delete(ClassifyRelation.class,whereList3);
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean removeParentClassify(Long parentId)
    {
        // 首先判断一级分类下是否还有二级分类，查classify_relation表
        WhereList whereList = new WhereList();
        whereList.format("parent_id = ?", parentId);
        List<ClassifyRelation> classifyRelations = classifyRelationDao.getList(ClassifyRelation.class, whereList);
        if (!classifyRelations.isEmpty())
        {
            return false;
        }
        WhereList whereList2 = new WhereList();
        whereList2.format("classify_id = ? and level = ?", parentId, Constant.PARENT_CLASSIFY_TYPE);
        classifyDao.delete(Classify.class, whereList2);
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean updateClassify(Classify classify)
    {
        classifyDao.updateObject(classify);
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean updateTag(Tag tag)
    {
        tagDao.updateObject(tag);
        return true;
    }
    
    @Override
	public List<Long> seperateIds(String idStr)
    {
        String[] arr = idStr.split("\\*");
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < arr.length; i++)
        {
            ids.add(Long.parseLong(arr[i]));
        }
        return ids;
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Tag getTag(Long tagId){
        WhereList whereList = new WhereList();
        whereList.format("tag_id = ? and is_act = ?", tagId, Constant.ACT);
        return tagDao.getObject(Tag.class, whereList);
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<TagStatisticsVo> getEffectiveTagView(List<Tag> tags){
        
        if(tags.isEmpty()){
            return new ArrayList<TagStatisticsVo>();
        }
        String tagIds = "(";
        for(Tag tag: tags){
            tagIds+=tag.getTagId()+",";
        }
        tagIds = tagIds.substring(0, tagIds.length()-1)+")";
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        //从统计表中查出对应的最近更新数据
        String sql = "select * from (select * from tag_statistics where tag_id in "+tagIds+" order by update_time desc) as b group by tag_id;";
        
        List list = dbAccessor.sqlQuery(sql);
        if (list.size() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                Object[] objects = (Object[])(list.get(i));
                TagStatisticsVo tagStatisticsVo = new TagStatisticsVo();
                tagStatisticsVo.setLatestUpdateTime(Long.parseLong(objects[3].toString()));
                tagStatisticsVo.setTotalPopulation(Long.parseLong(objects[2].toString()));
                //根据tagId查tag表获取name等信息
                WhereList whereList2 = new WhereList();
                whereList2.format("tag_id = ?", Long.parseLong(objects[1].toString()));
                Tag tag = tagDao.getObject(Tag.class, whereList2);
                tagStatisticsVo.setTagId(tag.getTagId());
                tagStatisticsVo.setTagName(tag.getTagName());
                tagStatisticsVo.setTagDescritpion(tag.getTagDescription());
                tagStatisticsVo.setUpdateGranularity(tag.getUpdateGranularity());
                tagStatisticsVo.setUpdateSpan(tag.getUpdateSpan());
                tagStatisticsVos.add(tagStatisticsVo);
            }
        }
        return tagStatisticsVos;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<TagStatisticsVo> getAllTagView(List<Tag> tags){
        
        List<TagStatisticsVo> tagStatisticsVos = new ArrayList<TagStatisticsVo>();
        
        if(tags.isEmpty()){
            return new ArrayList<TagStatisticsVo>();
        }

        String tagIds = "(";
   
        for(Tag tag: tags){
            TagStatisticsVo tagStatisticsVo = new TagStatisticsVo();
            tagStatisticsVo.setLatestUpdateTime(tag.getUpdateTime());
            tagStatisticsVo.setTagId(tag.getTagId());
            tagStatisticsVo.setTagName(tag.getTagName());
            tagStatisticsVo.setTotalPopulation(0L);
            tagStatisticsVo.setUpdateGranularity(tag.getUpdateGranularity());
            tagStatisticsVo.setUpdateSpan(tag.getUpdateSpan());
            tagStatisticsVos.add(tagStatisticsVo);
            tagIds+=tag.getTagId()+",";
        }
        tagIds = tagIds.substring(0, tagIds.length()-1)+")";
        
        //从统计表中查出对应的最近更新数据
        String sql = "select * from (select * from tag_statistics where tag_id in "+tagIds+" order by update_time desc) as b group by tag_id;";
        
        List list = dbAccessor.sqlQuery(sql);
        if (list.size() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                Object[] objects = (Object[])(list.get(i));
                Long tagId = Long.parseLong(objects[1].toString());
                for(TagStatisticsVo tagStatisticsVo: tagStatisticsVos){
                    if(tagStatisticsVo.getTagId().equals(tagId)){
                        tagStatisticsVo.setLatestUpdateTime(Long.parseLong(objects[3].toString()));
                        tagStatisticsVo.setTotalPopulation(Long.parseLong(objects[2].toString()));
                        break;
                    }
                }
            }
        }
        return tagStatisticsVos;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Map<String, List<Long>> getTagStatistics(Tag tag){
        Map<String, List<Long>> result = new HashMap<String, List<Long>>();
        List<Long> eachTime = new ArrayList<Long>();
        List<Long> eachPopulation = new ArrayList<Long>();
        String sql = "select * from tag_statistics where tag_id = "+tag.getTagId()+" order by update_time;";
        List list = dbAccessor.sqlQuery(sql);
        if(list.size()>0){
            Object[] objects1 = (Object[])(list.get(0));
            Long currentTime = Long.parseLong(objects1[3].toString());
            //根据粒度与跨度，获取统计的起始时间
            Long startTime = getStartTime(currentTime, tag.getUpdateSpan(), tag.getUpdateGranularity());
            for(int i=0; i< list.size(); i++){
                Object[] objects = (Object[])(list.get(i));
                Long population = Long.parseLong(objects[2].toString());
                Long updateTime = Long.parseLong(objects[3].toString());
                //只统计一个更新跨度内的数据
                if(updateTime.compareTo(startTime) >= 0){
                    eachPopulation.add(population);
                    eachTime.add(updateTime);
                }
            }
        }
        result.put("eachPopulation", eachPopulation);
        result.put("eachTime", eachTime);
        return result;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public Map<String,String> getTagPopulation(Tag tag){
    	Map<String,String> result=new HashMap<String,String>();
    	String population="";
    	String date="";
        String sql = "select * from tag_statistics where tag_id = "+tag.getTagId()+" order by update_time desc;";
        List list = dbAccessor.sqlQuery(sql);
        if(list.size()>0)
          {
            Object[] objects = (Object[])(list.get(0));
            population = objects[2].toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            date = format.format(objects[3]);
            result.put("population",population);
            result.put("date",date);
          }
        return result;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addTagStatistics(TagStatistics tagStatistics)
    {
        tagStatisticsDao.addObject(tagStatistics);
        return true;
    }
  
    
    /**
     * 根据粒度，跨度，当前时间推测初始时间
     * 
     * @param currentTime
     * @param updateSpan
     * @param updateGranularity
     * @return
     */
    @SuppressWarnings("static-access")
    private Long getStartTime(Long currentTime, int updateSpan, String updateGranularity)
    {
        // 将currentTime转calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.add(calendar.MONTH, -1 * updateSpan);
        System.out.println(calendar.getTime());
        return calendar.getTimeInMillis();
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean removeTagClassify(Long tagId, Long childId){
        WhereList whereList = new WhereList();
        whereList.format("tag_id = ? and classify_id = ?", tagId, childId);
        tagClassifyDao.delete(TagClassify.class,whereList);
        return true;
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean tagNameVarify(String tagName){
        WhereList whereList = new WhereList();
        whereList.format("tag_name = ? and is_act = ?", tagName, Constant.ACT);
        Tag tag = tagDao.getObject(Tag.class, whereList);
        if(tag==null){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean childNameVarify(String childName){
        WhereList whereList = new WhereList();
        whereList.format("classify_name=? and level = ?", childName, Constant.CHILD_CLASSIFY_TYPE);
        Classify child = classifyDao.getObject(Classify.class, whereList);
        if(child==null){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean parentNameVarify(String parentName){
        WhereList whereList = new WhereList();
        whereList.format("classify_name=? and level = ?", parentName, Constant.PARENT_CLASSIFY_TYPE);
        Classify parent = classifyDao.getObject(Classify.class, whereList);
        if(parent==null){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean isIntegrationTag(Long tagId){
        WhereList whereList = new WhereList();
        whereList.format("tag_integration_id = ?", tagId);
        TagIntegration tagIntegration = tagIntegrationDao.getObject(TagIntegration.class, whereList);
        if(tagIntegration==null){
            return false;
        }
        else{
            return true;
        }
    }
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public boolean addTagIntegration(TagIntegration tagIntegration){
        tagIntegrationDao.addObject(tagIntegration);
        return true;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public List<Tag> getTagsLike(String tagName){
        WhereList whereList = new WhereList();
        String sql = "tagName like '%"+tagName+"%'";
        whereList.format(sql);
        return tagDao.getList(Tag.class, whereList); 
        
    }

}
