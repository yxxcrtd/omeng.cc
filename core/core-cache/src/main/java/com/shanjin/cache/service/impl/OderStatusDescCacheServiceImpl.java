package com.shanjin.cache.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.IOderStatusDescCacheService;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.util.StringUtil;


/**
 * 订单状态描述缓存服务
 * 
 * @author 俞成
 *
 */

@Service("orderStatusDescribeCacheServices")
public class OderStatusDescCacheServiceImpl implements
		IOderStatusDescCacheService {
	Log log = LogFactory.getLog(OderStatusDescCacheServiceImpl.class);
	
	@Override
	public void cacheOrderStatusDescribes(List<Map<String, Object>> describeInfo) {
		  if (describeInfo!=null && describeInfo.size()>0){
			  Jedis jedis=null;
			  try{
			  jedis = JedisPoolUtil.getJedis();
			  Pipeline pipeLine = jedis.pipelined();
			  for (Map<String,Object> info: describeInfo){
				   jedis.hset(CacheConstants.ORDER_STATUS_DESCRIBE,
						   info.get("dict_type").toString()+"_"+StringUtil.null2Str(info.get("dict_key")), StringUtil.null2Str(info.get("remark")));
			  }
			  pipeLine.sync();
			  jedis.expire(CacheConstants.ORDER_STATUS_DESCRIBE, 24*3600);
			  }catch(Exception e){
				  log.error(e.getMessage(), e);
			  }finally{
				  JedisPoolUtil.returnRes(jedis);
			  }
		  }

	}

}
