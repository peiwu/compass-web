package com.iflytek.voicecloud.compass.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.iflytek.voicecloud.compass.common.Constant;

public class ExportResult
implements Iterable<Map<String,Object>>
{
	 private String scrollId;
	 private Client client;
	 private String []tagIds;
	 public ExportResult(String[]tagIds){
		 this.tagIds=tagIds;
		 try{
		 this.scrollId = prepareScroll(this.tagIds);
		 }
		 catch(Exception e){
			 //往上层抛异常
			 e.printStackTrace();
			 throw e;
		 }
	 }
	 public String prepareScroll(String []tagIds){
		  Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "dmp").build(); 
		   	client= new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(Constant.esUrl, 9400));
		   	BoolFilterBuilder bool=FilterBuilders.boolFilter().cache(true);
		   	for(int i=0;i<tagIds.length;i++) bool.must(FilterBuilders.termFilter("taglist",tagIds[i]));
		   	 QueryBuilder filterbuilder = QueryBuilders.filteredQuery(null,bool);
		   	 
		   	SearchResponse searchResponse = client.prepareSearch("dmp").setTypes("imei")
		   			  .setSearchType(SearchType.SCAN)
		   		      .addFields(new String[]{"idtype","did","taglist"})
				      .setScroll(TimeValue.timeValueMinutes(30))
				      .setQuery(filterbuilder)
				      .setSize(50)
				      .execute()
				      .actionGet();
		   	return searchResponse .getScrollId();
	 }
    public SearchResponse scrollResult(String scrollId) {
			    return 
			      (SearchResponse)this.client.prepareSearchScroll(scrollId)
			      .setScroll(TimeValue.timeValueSeconds(30))
			      .execute().actionGet();
			  }

	  public List<Map<String,Object>> getResult() {
	    SearchResponse response =scrollResult(this.scrollId);
	    if(response.getHits().getHits().length==0) return null;
	    else{
	    this.scrollId = response.getScrollId();
	    return parseResponse(response.getHits().getHits());
	        }
	  }

	  public List<Map<String,Object>> parseResponse(SearchHit[] response)
	  {
	    List <Map<String,Object>>result = new ArrayList<Map<String,Object>>();
	    for (SearchHit hit : response)
	    {
	    	Map<String,Object> v=new HashMap<String,Object>();
	    	 for (Map.Entry<String,SearchHitField> entry : hit.fields().entrySet()) {
	    		 v.put((String)entry.getKey(),((SearchHitField)entry.getValue()).getValue());
	    	      }
	      //String dvc="*"+hit.getSource().get("idtype")+"_"+(String) hit.getSource().get("did");
	      result.add(v);
	    }
	    return result;
	  }
	@Override
	public Iterator<Map<String,Object>> iterator() {
		// TODO Auto-generated method stub
		 return new Iterator<Map<String,Object>>()
		 {
			  private long total=0;
		      private List<Map<String,Object>> resultMap;
		      private Iterator<Map<String,Object>> iterator;

		      public boolean hasNext()
		      {
		    	if(this.total==0){
		    		this.resultMap=getResult();
		    		if(resultMap==null) return false;
		    		else {
		    			this.iterator = this.resultMap.iterator();	
		    			this.total+=resultMap.size();
		    			return true;
		    		    }
		    		         }
		    	else return true;
		      }
		      public Map<String,Object> next() {
		    	  this.total--;
		        return (Map<String, Object>)this.iterator.next();
		      }
		      public void remove() {
		        throw new UnsupportedOperationException("don't support remove operation");
		      }
		    };
	}
	





}
