package com.shanjin.cache.service;

import java.util.Map;

import com.shanjin.cache.service.impl.JedisLock;


/**
 * 缓存通用接口描述
 * @author Huang yulai
 *
 */
public interface ICommonCacheService {


	/**
	 * 读取缓存数据通用接口 （缓存中的K-V,K为主关键字和附属关键字的拼接，之间用“_”连接）
	 * @param key （主关键字，一般是定义的特定常量）
	 * @param attachedKey （附属关键字，一般是接口的参数,可以为null）
	 * @return
	 */
	public Object getObject(String key,String... attachedKey);
	
	/**
	 * 更新缓存数据通用接口（缓存中的K-V,K为主关键字和附属关键字的拼接，之间用“_”连接）
	 * @param obj（需要刷新缓存的V值）
	 * @param key（主关键字，一般是定义的特定常量）
	 * @param attachedKey （附属关键字，一般是接口的参数,可以为null）
	 * @return
	 */
	public Object setObject(Object obj,String key,String... attachedKey);
	
	/**
	 * 更新缓存数据通用接口（缓存中的K-V,K为主关键字和附属关键字的拼接，之间用“_”连接）
	 * @param obj（需要刷新缓存的V值）
	 * @param seconds（缓存失效时间，单位秒，多少秒未访问该缓存即失效）
	 * @param key（主关键字，一般是定义的特定常量）
	 * @param attachedKey （附属关键字，一般是接口的参数,可以为null）
	 * @return
	 */
	public Object setObject(Object obj,int seconds,String key,String... attachedKey);
	
	/**
	 * 删除缓存数据通用接口 （缓存中的K-V,K为主关键字和附属关键字的拼接，之间用“_”连接）
	 * @param key （主关键字，一般是定义的特定常量）
	 * @param attachedKey （附属关键字，一般是接口的参数,可以为null）
	 * @return
	 */
	public boolean deleteObject(String key,String... attachedKey);
	
	/**
	 * 删除缓存K.contains(key)的对象
	 * @param key 缓存key
	 * @param flag （true：删除K.contains(key)的对象 false:删除K==key对象）
	 * @return
	 */
	public boolean delObjectContainsKey(String key,boolean flag);
	
	
	/**
	 * 循环按时逐一删除 缓存K.contains(key)的对象
	 * @param key 缓存key
	 * @return
	 */
	public boolean delObjectContainsKey(String key);
	
	/**
	 * 更新缓存数据通用接口（缓存中的K-V,K为主关键字和附属关键字的拼接，之间用“_”连接）
	 * @param obj（需要刷新缓存的V值）
	 * @param seconds（缓存失效时间，单位秒，数据缓存后倒计时）
	 * @param key（主关键字，一般是定义的特定常量）
	 * @param attachedKey （附属关键字，一般是接口的参数,可以为null）
	 * @return
	 */
	public Object setExpireForObject(Object obj,int seconds,String key,String... attachedKey);

	/**
	 * 通用的Redis中获取hget的方法
     *
     * @param folderName 文件夹名称
     * @param tagName 包名
     * @param tagKey 包的key
     * @param returnType 0 单值类型；1 Map类型（以后根据需要扩充）
     * @param <T> 泛型对象
     * @return
     */
    <T> T getCommonCacheWithHGet(String folderName, String tagName, String tagKey, int returnType);

    /**
     * 通用的Redis中获取hset的方法
     *
     * @param folderName 文件夹名称
     * @param tagKey 包的Key
     * @param tagValue 包的Value
     * @param expire 过期时间（单位：秒）
     */
	void setCommonCacheWithHGet(String folderName, String tagKey, String tagValue, int expire);

    /**
     * 检查Redis中的Key是否存在
     *
     * @param key Redis的 Key
     * @return true 存在; false 不存在
     */
    boolean isExistKey(String key);

    public String hget(String key,String field);

    public long hset(String key,String field,String value);
    
    public  Map<String, String> hmget(String key);
    
    public  String  hmset(String key,Map<String,String> info);   
	
	public JedisLock getLock(String lockName,int waitSec,int expireSec);
	
	public  void releaseLock(JedisLock lock);
    
}
