package com.shanjin.cache.service.impl;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.cache.util.JedisPoolUtil;


/**
 * IP 城市 缓存 服务类
 * @author revoke
 *
 */
@Service("ipCityCacheServices")
public class IpCityCacheServiceImpl extends CommonCacheServiceImpl implements IIpCityCacheService {
	Log log = LogFactory.getLog(UserRelatedCacheServices.class);
	
	
	//根据IP 从缓存中读出位置信息
	public JSONObject getCity(String ip){
		String info = hget(CacheConstants.IP_TO_LOCATOIN, ip);
		if (info!=null){
			 //更新时间戳
			 double now = System.currentTimeMillis();
			 zadd(CacheConstants.IP_RECENT_ACCESS,now,ip);
			 return 	JSONObject.parseObject(info);
		}	
		return null;
		
	}
	
	
	//将IP，位置信息放入缓存
	public void cachedCity(String ip,String province,String city) {
		 JSONObject jsonObject =new JSONObject();
		 jsonObject.put("province", province);
		 jsonObject.put("city", city);
		 double now = System.currentTimeMillis();
		 Jedis jedis = JedisPoolUtil.getJedis();
		 Pipeline pipeLine = jedis.pipelined();
		 
		 pipeLine.hset(CacheConstants.IP_TO_LOCATOIN, ip, jsonObject.toJSONString());
		 pipeLine.zadd(CacheConstants.IP_RECENT_ACCESS,now,ip);
		 
		 pipeLine.sync();
		 JedisPoolUtil.returnRes(jedis);
	}
	
	//按LRU 原则 清理超出缓存范围的 IP --CITY
	public void cleanOldIpCache() {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
				JedisLock lock = new JedisLock(jedis, CacheConstants.LOCK_IP_CACHE, 60*1000, 60*1000);
				if (lock.acquire()) {	//获得IP缓存锁的线程，才能执行缓存清理工作。
					
					Set<String> ips=jedis.zrevrange(CacheConstants.IP_RECENT_ACCESS, CacheConstants.IP_MAX_ENTRY, -1);
					if (ips!=null && ips.size()>0) {
							Pipeline pipeLine = jedis.pipelined();
							for(String ip:ips){
									pipeLine.zrem(CacheConstants.IP_RECENT_ACCESS, ip);
									pipeLine.hdel(CacheConstants.IP_TO_LOCATOIN, ip);
							}
							pipeLine.sync();
					}  
				}
				lock.release();
		}catch (InterruptedException e) {
					log.error("ip-城市缓存清理出错："+e.getMessage(), e);
		}finally{
					JedisPoolUtil.returnRes(jedis);
		}
	}
}
