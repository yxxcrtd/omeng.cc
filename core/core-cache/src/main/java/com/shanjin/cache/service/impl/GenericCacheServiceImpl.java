package com.shanjin.cache.service.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.shanjin.cache.service.GenericCacheService;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.cache.util.SerializeUtil;
import com.shanjin.common.util.StringUtil;

public abstract class GenericCacheServiceImpl implements GenericCacheService{

	/** set Object */
	public String set(String key,Object object) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			if(jedis.exists(key.getBytes())){
				//判断缓存中key是否存在，若存在，del
				jedis.del(key.getBytes());
			}
			return jedis.set(key.getBytes(),  SerializeUtil.serialize(object));
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return null;
	}
	
	/** set Object */
	public String set(String key,int seconds,Object object) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			if(jedis.exists(key.getBytes())){
				//判断缓存中key是否存在，若存在，del
				jedis.del(key.getBytes());
			}
			return jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(object));
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return null;
	}

	/** get Object */
	public Object get(String key) {
		byte[] value = null;
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			value = jedis.get(key.getBytes());
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return SerializeUtil.deserialize(value);
	}

	/** delete a key **/
	public boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			jedis.del(key.getBytes());
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);;
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return true;
	}
	
	/**
	 * del contains key when flag is true;otherwise del a key.
	 */
	public boolean delContainsKey(String key,boolean flag) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			if(flag){
				//删除包含key的缓存
			     Set keys = jedis.keys(key+"*");//列出所有的key
			     Iterator it=keys.iterator() ;  
			     String keyItem = null;
			     while(it.hasNext()){   
			         keyItem=StringUtil.null2Str(it.next());   
			         jedis.del(keyItem.getBytes()); // 删除包含key的缓存数据
			     }  
			}else{
				jedis.del(key.getBytes());
			}
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);;
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return true;
	}
	public boolean delContainsKey(String key) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtil.getJedis();
			//删除包含key的缓存
			     Set keys = jedis.keys(key+"*");//列出所有的key
			     Iterator it=keys.iterator() ;  
			     String keyItem = null;
			     while(it.hasNext()){
			         keyItem=StringUtil.null2Str(it.next());   
			         jedis.del(keyItem.getBytes()); // 删除包含key的缓存数据
			         Thread.sleep(1000);
			     }  
			
		} catch (Exception e) {
			// 释放redis对象
			JedisPoolUtil.returnBrokenRes(jedis);;
			e.printStackTrace();
		} finally {
			// 返还到连接池
			JedisPoolUtil.returnRes(jedis);
		}
		return true;
	}


	/**
	 * set  map
	 * @param key
	 * @param info
	 * @return
	 */
	public  String  hmset(String key,Map<String,String> info) {
		 Jedis jedis = JedisPoolUtil.getJedis();
		 String result = jedis.hmset(key, info);
		 JedisPoolUtil.returnRes(jedis);
		 return result;
	}
	
	
	
	
	public String hget(String key,String field) {
		 Jedis jedis = JedisPoolUtil.getJedis();
		 String result = jedis.hget(key, field);
		 JedisPoolUtil.returnRes(jedis);
		 return result;
	}
	
	public long hset(String key,String field,String value) {
		   Jedis jedis = JedisPoolUtil.getJedis();
		   long result = jedis.hset(key, field, value);
		   JedisPoolUtil.returnRes(jedis);
		   return result;
		
	}
	
	public  Map<String, String> hmget(String key){
		 Jedis jedis = JedisPoolUtil.getJedis();
		  Map<String,String>  result= jedis.hgetAll(key);
		  JedisPoolUtil.returnRes(jedis);
		  return result;
	}
	
	
	
	public long  setExpire(String key,int seconds){
		     Jedis jedis = JedisPoolUtil.getJedis();
			 long result = jedis.expire(key, seconds);
			  JedisPoolUtil.returnRes(jedis);
			  return result;
	}
	
	
	public  long zadd(String key,Double score,String memeber){
		  Jedis jedis = JedisPoolUtil.getJedis();
		  long result = jedis.zadd(key, score, memeber);
		  JedisPoolUtil.returnRes(jedis);
		  return result;
	}
	
	
	public long zcard(String key) {
		   Jedis jedis = JedisPoolUtil.getJedis();
		   
		   long  result = jedis.zcard(key);
		   JedisPoolUtil.returnRes(jedis);
		   return result;
	}
	
	
	/**
	 * 获取一个锁，             redis 资源不释放， 由配对的releaseLock 来释放redis 资源
	 * @param lockName
	 * @param waitSec    获取锁等待的时间
	 * @param expireSec  锁定时间
	 * @return
	 */
	public JedisLock getLock(String lockName,int waitSec,int expireSec){
		   Jedis jedis = JedisPoolUtil.getJedis();
		   JedisLock result = new JedisLock(jedis,lockName,waitSec,expireSec);
		   return result;
	}

	/**
	 * 释放一个锁，同时释放锁关联的redis资源
	 * @param lock
	 */
	public  void releaseLock(JedisLock lock){
		 //   Jedis jedis = JedisPoolUtil.getJedis();
			lock.release();
		//	JedisPoolUtil.returnRes(jedis);
			JedisPoolUtil.returnRes(lock.getJedis());
	}
}
