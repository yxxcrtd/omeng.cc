package com.shanjin.manager.job;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;


/**
 * 后台业务数据处理job
 * 
 * @author 
 * 
 */
public class SearchStatisticJob {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	protected void work(){
		List<Map<String,Object>>results =  (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SEARCH_STATICTIS_KEY);
      if(results==null||results.size()==0){
        	
        }else{
        	String sql="update search_statistic s set s.search_count =s.search_count+? where s.service_id =?";
        	List<Record> re=null;
        	for(Map<String,Object> ma:results){
        		if(re!=null&&re.size()>0){
        			 Db.update(sql,ma.get("search_count"),ma.get("service_type_id"));	
        		}
        	}
        	commonCacheService.deleteObject(CacheConstants.SEARCH_STATICTIS_KEY);
        }
	}

}

