package com.shanjin.cache.service.impl;


import com.alibaba.fastjson.JSON;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service("commonCacheService")
public class CommonCacheServiceImpl extends GenericCacheServiceImpl implements ICommonCacheService{
	private static Map<String, Object> type = new HashMap<String, Object>();
    Log log = LogFactory.getLog(CommonCacheServiceImpl.class);

	public Object getObject(String key,String... attachedKey){
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		return get(okey);
	}
	
	public Object setObject(Object obj,String key,String... attachedKey){
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		set(okey,obj);
		return getObject(okey);
	}
	
	public Object setObject(Object obj,int seconds,String key,String... attachedKey){
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		set(okey,seconds,obj);
		return getObject(okey);
	}

	@Override
	public boolean deleteObject(String key, String... attachedKey) {
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		return del(okey);
	}

	@Override
	public boolean delObjectContainsKey(String key, boolean flag) {
		return delContainsKey(key,flag);
	}

	@Override
	public boolean delObjectContainsKey(String key) {
		
		return delContainsKey(key);
	}

	@Override
	public Object setExpireForObject(Object obj, int seconds, String key,
			String... attachedKey) {
		String okey = key;
		if(attachedKey!=null){
			for(String ak : attachedKey){
				if(!StringUtil.isNullStr(ak)){
					okey = okey + CacheConstants.JOIN + ak;
				}
			}
		}
		set(okey,obj);
		setExpire(okey,seconds);
		return getObject(okey);
	}

    /**
     * 通用的Redis中获取hget的方法
     *
     * @param folderName 文件夹名称
     * @param tagName 包名
     * @param tagKey 包的key
     * @return Object
     */
	@Override
	public <T> T getCommonCacheWithHGet(String folderName, String tagName, String tagKey, int returnType) {
        T t = null;
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			// 从Redis中获取存储的值
            String tagValue = jedis.hget(folderName + tagName, tagKey);
            if (isNotEmpty(tagValue) && 0 == returnType) {
                t = (T) tagValue;
            }
            if (isNotEmpty(tagValue) && 1 == returnType) {
                t = (T) JSON.parseObject(tagValue, type.getClass());
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
        return t;
	}

    /**
     * 通用的Redis中获取hset的方法
     *
     * @param folderName 文件夹名称
     * @param tagKey 包的Key
     * @param tagValue 包的Value
     * @param expire 过期时间（单位：秒）
     */
	@Override
	public void setCommonCacheWithHGet(String folderName, String tagKey, String tagValue, int expire) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			jedis.hset(folderName, tagKey, tagValue);
            if (expire > 0) {
                jedis.expire(folderName, expire);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
	}

    /**
     * 检查Redis中的Key是否存在
     *
     * @param key Redis的 Key
     * @return true 存在; false 不存在
     */
    public boolean isExistKey(String key) {
        boolean returnValue = false;
        Jedis jedis = JedisPoolUtil.getJedis();
        try {
            returnValue = jedis.exists(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            JedisPoolUtil.returnRes(jedis);
        }
        return returnValue;
    }
    /**
	 * 获取一个锁，             redis 资源不释放， 由配对的releaseLock 来释放redis 资源
	 * @param lockName
	 * @param waitSec    获取锁等待的时间
	 * @param expireSec  锁定时间
	 * @return
	 */
    @Override
	public JedisLock getLock(String lockName,int waitSec,int expireSec){
		 return  super.getLock(lockName, waitSec, expireSec);
	}

	/**
	 * 释放一个锁，同时释放锁关联的redis资源
	 * @param lock
	 */
    @Override
	public  void releaseLock(JedisLock lock){
		super.releaseLock(lock);
	} 
	public static void main(String[] args) throws InterruptedException {
		CommonCacheServiceImpl cacheImpl = new CommonCacheServiceImpl();

        System.out.println(cacheImpl.isExistKey("user_blacklist"));

//		cacheImpl.set("testExpire", 5, "hello");
//		 while(true){
//			 System.out.println(cacheImpl.get("testExpire"));
//			 Thread.sleep(500);
//		 }

	}
	
}

