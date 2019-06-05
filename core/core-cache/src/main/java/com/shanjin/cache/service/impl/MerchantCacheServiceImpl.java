package com.shanjin.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.GenericCacheService;
import com.shanjin.cache.service.IMerchantCacheService;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

@Service("merchantCacheService")
public class MerchantCacheServiceImpl extends GenericCacheServiceImpl implements
		GenericCacheService, IMerchantCacheService {
	Log log = LogFactory.getLog(MerchantCacheServiceImpl.class);
	
	private Map<String,Object> type= new HashMap<String,Object>(0);

	private GenericCacheService genericCacheService;

	/**
	 * 手机验证码缓存
	 */
	public String createVerificationCodeCache(Long merchantId, String info) {

		String result = set(CacheConstants.VERIFICATIONCODE_CACHE + "_"
				+ merchantId, info);

		setExpire(CacheConstants.VERIFICATIONCODE_CACHE + "_" + merchantId,
				CacheConstants.VERIFICATIONCODE_CACHE_TIMEOUT);

		return result;
	}

	/**
	 * 获取手机验证码缓存
	 */
	public String getVerificationCodeCache(Long merchantId) {

		Object result = get(CacheConstants.VERIFICATIONCODE_CACHE + "_"
				+ merchantId);
		if (result == null) {
			return null;
		}
		return result.toString();
	}

	
	/**
	 * 获取某商户缓存订单列表中的某条订单
	 * @param merchantID
	 * @param orderID
	 */
	public  Map<String,Object> getCachedMerchantPushOrder(String merchantID,String orderID) {
		Map<String,Object> result=null;
		String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
		String historyCachedKey = CacheConstants.HISTORY_PUSH_ORDER_PREFIX+merchantID+"::"+orderID;
		Jedis jedis = null;
		try{
			jedis=JedisPoolUtil.getJedis();
			if (jedis.exists(key)){
				String cachedOrder=jedis.hget(key, orderID);
				if (cachedOrder!=null){
					 result = JSON.parseObject(cachedOrder, type.getClass());
				}
			}else if (jedis.exists(historyCachedKey)){
					String cachedOrder=jedis.get(historyCachedKey);
					if (cachedOrder!=null){
						result = JSON.parseObject(cachedOrder, type.getClass());
					}
			}else{
				 //不存在对应的缓存
			}
		}catch(Exception e){
			log.error("从缓存获取商户推送订单列表失败：merchantId-->"+merchantID+""+e.getMessage(), e);
		}finally{
			JedisPoolUtil.returnRes(jedis);
		}
		return result;
	}
	
	/**
	 * 更新特定商户中的某条订单缓存到指定状态，如果近期缓存存在，则更新之，否则更新历史缓存。 都没命中，跳过。
	 * @param merchantID
	 * @param status
	 */
	public void updateMerchantOrderCache(String merchantID,String orderID,String status){
		String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
		String lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
		Jedis jedis = null;
		try{
			jedis=JedisPoolUtil.getJedis();
			JedisLock lock = new JedisLock(jedis, lockName, 20000, 5000);
			if (lock.acquire(jedis)){
				 if (jedis.hexists(key, orderID)){
					 //更新商户侧最新订单缓存列表状态
		 			 String cachedInfo=jedis.hget(key, orderID);
		 			 Map<String,Object> cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
		 			 cachedPushOrder.put("orderStatus", status);
		 			 jedis.hset(key, orderID, JSON.toJSONString(cachedPushOrder));
				 }else{
					 String historyCachedKey = CacheConstants.HISTORY_PUSH_ORDER_PREFIX+merchantID+"::"+orderID;
		 			 if (jedis.exists(historyCachedKey)){
		 			 	 //更新商户侧历史订单缓存状态
		 				 String cachedInfo=jedis.get(historyCachedKey);
		 				 Map<String,Object> cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
		 				 cachedPushOrder.put("orderStatus", status);
		 				 jedis.setex(historyCachedKey,CacheConstants.HISTORY_MERCHANT_PUSH_ORDER_EXPIRETIME, JSON.toJSONString(cachedPushOrder));
		 			 }else{
		 				 //未命中任何缓存，跳过。
		 			 }
				 }
				 
			}
			lock.release();
		}catch(Exception e){
			log.error("更新商户缓存失败：merchantId-->"+merchantID+",orderID->"+orderID+" "+e.getMessage(), e);
		}finally{
			JedisPoolUtil.returnRes(jedis);
		}
		
		
	}

	
	/**
	 * 更新特定商户中的某条订单缓存，如果近期缓存存在，则更新之，否则更新历史缓存。 都没命中，跳过。
	 * @param merchantID
	 * @param status
	 */
	public void updateMerchantOrderCache(String merchantID,String orderID,Map<String,Object> upOrderInfo){
		String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
		String lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
		Jedis jedis = null;
		try{
			jedis=JedisPoolUtil.getJedis();
			JedisLock lock = new JedisLock(jedis, lockName, 20000, 5000);
			if (lock.acquire(jedis)){
				 if (jedis.hexists(key, orderID)){
					 //更新商户侧最新订单缓存列表状态
		 			 String cachedInfo=jedis.hget(key, orderID);
		 			 Map<String,Object> cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
		 			 cachedPushOrder.putAll(upOrderInfo);
		 			 jedis.hset(key, orderID, JSON.toJSONString(cachedPushOrder));
				 }else{
					 String historyCachedKey = CacheConstants.HISTORY_PUSH_ORDER_PREFIX+merchantID+"::"+orderID;
		 			 if (jedis.exists(historyCachedKey)){
		 			 	 //更新商户侧历史订单缓存状态
		 				 String cachedInfo=jedis.get(historyCachedKey);
		 				 Map<String,Object> cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
		 				 cachedPushOrder.putAll(upOrderInfo);
		 				 jedis.setex(historyCachedKey,CacheConstants.HISTORY_MERCHANT_PUSH_ORDER_EXPIRETIME, JSON.toJSONString(cachedPushOrder));
		 			 }else{
		 				 //未命中任何缓存，跳过。
		 			 }
				 }
				 
			}
			lock.release();
		}catch(Exception e){
			log.error("更新商户缓存失败：merchantId-->"+merchantID+",orderID->"+orderID+" "+e.getMessage(), e);
		}finally{
			JedisPoolUtil.returnRes(jedis);
		}
		
		
	}
	
	
	/**
	 * 获取某商户的缓存订单列表
	 * @param merchantID
	 */
	public List<Map<String,Object>> getCachedMerchantPushOrders(String merchantID) {
		List<Map<String,Object>> result =null;
		String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
		String lkey = CacheConstants.MERCHANT_PUSH_ORDER_IDS +merchantID;
		Jedis jedis = null;
		try{
			jedis=JedisPoolUtil.getJedis();
			Map<String,String> cached = jedis.hgetAll(key);
			if (cached==null || cached.size()<1)
				return null;
			result = new ArrayList<Map<String,Object>>(cached.size());
			for (Entry<String,String> entry:cached.entrySet()){
					result.add(JSON.parseObject(entry.getValue(),type.getClass()));
			}
			
			//刷新缓存生存期
			jedis.expire(key, CacheConstants.MERCHANT_PUSH_ORDER_EXPIRETIME);
			jedis.expire(lkey, CacheConstants.MERCHANT_PUSH_ORDER_EXPIRETIME);
		}catch(Exception e){
			log.error("从缓存获取商户推送订单列表失败：merchantId-->"+merchantID+""+e.getMessage(), e);
		}finally{
			JedisPoolUtil.returnRes(jedis);
		}
		return result;
	}
	
	/**
	 * 更新商户侧某条订单缓存
	 * @param pushOrderInfo
	 * @param merchantID
	 * @param orderID
	 */
	public void updateMerchantCachedOrder(Map pushOrderInfo,String merchantID,String orderID) {
			String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
			String historyCachedKey = CacheConstants.HISTORY_PUSH_ORDER_PREFIX+merchantID+"::"+orderID;
			String lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
			Jedis jedis = null;
			try{
				jedis=JedisPoolUtil.getJedis();;
				JedisLock lock = new JedisLock(jedis, lockName, 20000, 5000);
				if (lock.acquire(jedis)){
					 if (jedis.exists(key)){
						 	jedis.hset(key, orderID, JSON.toJSONString(pushOrderInfo));
					 }else {
						  //更新商户侧历史订单缓存状态
		 				  jedis.setex(historyCachedKey,CacheConstants.HISTORY_MERCHANT_PUSH_ORDER_EXPIRETIME,JSON.toJSONString(pushOrderInfo));		 
					 }
				}
				lock.release();
			}catch(Exception e){
				log.error("更新商户推送订单列表缓存失败：merchantId-->"+merchantID+" "+",orderId-->"+orderID+e.getMessage(), e);
			}finally{
				JedisPoolUtil.returnRes(jedis);
			}
			
	}
	
	
	/**
	 * 将某商户近期的推送订单加载到缓存中
	 * @param pushOrderInfos
	 * @param merchantID
	 */
	public void cachePushOrderForSpecialMerchant(List<Map<String,Object>> pushOrderInfos,String merchantID) {
		if (pushOrderInfos != null && pushOrderInfos.size() > 0) {
			Jedis jedis = null;
			String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
			String lkey = CacheConstants.MERCHANT_PUSH_ORDER_IDS +merchantID;
			String lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
			String orderId=null;
			try {
				jedis = JedisPoolUtil.getJedis();
				JedisLock lock = new JedisLock(jedis, lockName, 10000, 20000);
				if (lock.acquire()) {
					Pipeline pipeLine = jedis.pipelined();
					for (Map pushOrderInfo:pushOrderInfos){
						orderId = pushOrderInfo.get("orderId").toString();
						jedis.hset(key, orderId, JSON.toJSONString(pushOrderInfo));
						jedis.lpush(lkey, orderId);
					}
					jedis.expire(key, CacheConstants.MERCHANT_PUSH_ORDER_EXPIRETIME);
					jedis.expire(lkey, CacheConstants.MERCHANT_PUSH_ORDER_EXPIRETIME);
					pipeLine.sync();
				}
				lock.release();
			} catch (InterruptedException e) {
				 log.error("加载商户推送订单列表到缓存失败：merchantId-->"+merchantID+" "+e.getMessage(), e);
			}finally{
				JedisPoolUtil.returnRes(jedis);
			}			
		}
	}
	
	/**
	 * 根据某次推送订单列表更新商户侧 订单缓存
	 * @param pushOrderInfos
	 * @param orderId
	 */
	public void cachePushOrderForAllMerchants(List<Map<String,Object>> pushOrderInfos,String orderId) {
		if (pushOrderInfos != null && pushOrderInfos.size() > 0) {
			Jedis jedis = null;
			String lockName = null;
			try {
				jedis = JedisPoolUtil.getJedis();
				for (Map pushOrderInfo : pushOrderInfos) {
					String merchantID=pushOrderInfo.get("merchantId").toString();
					lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
					JedisLock lock = new JedisLock(jedis, lockName, 20000, 5000);
					String key = CacheConstants.MERCHANT_PUSH_ORDER_PREFIX
							+ merchantID;
					String lkey = CacheConstants.MERCHANT_PUSH_ORDER_IDS +merchantID;
					
					if (!jedis.exists(key)){
						//如果推送订单对应的商户 不存在推送订单缓存，则跳过  ---很可能是很少使用系统的商户
						continue;
					} 
					if (lock.acquire()) {
						jedis.hset(key, orderId, JSON.toJSONString(pushOrderInfo));
						jedis.lpush(lkey,orderId);
					}
					lock.release();
				}
			} catch (Exception e) {
				log.error("根据推送订单更新商户侧缓存失败：orderId->"+orderId+" "+e.getMessage(), e);
			} finally {
				JedisPoolUtil.returnRes(jedis);
			}
		}
	}
	
	/**
	 * 批量更新商户端订单缓存状态
	 * 1. 如果近期的订单缓存存在，则更新之。2.如果1不成立，检查历史订单缓存是否存在。
	 * @param orderId
	 * @param merchantIds
	 * @param status
	 */
	public void batchUpdateMerchantOrderStatus(String orderId,List<Map<String,Object>> merchantIds,String status) {
		 Jedis jedis = null;
		 String  merchantID= null;
		 String cachedKey=null;
		 String historyCachedKey=null;
		 jedis = JedisPoolUtil.getJedis();
		 String cachedInfo=null;
		 Map<String,Object> cachedPushOrder=null;
		 try{
			 	for (Map<String,Object> merchant:merchantIds){
			 		merchantID = merchant.get("merchant_id").toString();
			 		cachedKey = CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+merchantID;
			 		if (jedis.hexists(cachedKey, orderId)){
			 			//更新商户侧最新订单缓存列表状态
			 			 cachedInfo=jedis.hget(cachedKey, orderId);
			 			 cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
			 			 cachedPushOrder.put("orderStatus", status);
			 			 jedis.hset(cachedKey, orderId, JSON.toJSONString(cachedPushOrder));
			 		}else {
			 			 historyCachedKey = CacheConstants.HISTORY_PUSH_ORDER_PREFIX+merchantID+"::"+orderId;
			 			 if (jedis.exists(historyCachedKey)){
			 			 	 //更新商户侧历史订单缓存状态
			 				 cachedInfo=jedis.get(historyCachedKey);
			 				 cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
			 				 cachedPushOrder.put("orderStatus", status);
			 				 jedis.setex(historyCachedKey,CacheConstants.HISTORY_MERCHANT_PUSH_ORDER_EXPIRETIME,JSON.toJSONString(cachedPushOrder));
			 			 }else{
			 		 			//未命中任何缓存，忽略	
			 			 }
			 		}
			 	}
		 }catch(Exception e){
			 	log.error("批量更新商户侧推送订单缓存失败：orderId->"+orderId+" "+e.getMessage(), e);
		 }finally{
			 	JedisPoolUtil.returnRes(jedis);
		 }
	}
	
	/**
	 * 定时调度清理商户侧一月前的缓存列表
	 * TODO  改进性能
	 */
	public void cleanCache() {
        long currentTime = System.currentTimeMillis();
		Jedis jedis = null;
		Map<String,Object> type = new  HashMap<String,Object>(0);
		String scheduleLockName = CacheConstants.LOCK_CLEAN_PUSH_MERCHANT_ORDER_CACHE;
		String  today = DateUtil.getCurrentday();
		JedisLock scheduleLock=null;
		try {
			jedis = JedisPoolUtil.getJedis();
			scheduleLock = new JedisLock(jedis, scheduleLockName, 10000, 600000);
			if (scheduleLock.acquire()){
					Set<String> keySet=jedis.keys(CacheConstants.MERCHANT_PUSH_ORDER_PREFIX+"*");
					String  specialMerchantOrderLockName=null;
					String  listKey=null;
					JedisLock  specialMerchantOrderLock=null;
					for(String key:keySet){
						    specialMerchantOrderLockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX+key;
							specialMerchantOrderLock = new JedisLock(jedis,specialMerchantOrderLockName,10000,10000);
							try{
									//清理特定商户的订单缓存列表
									if (specialMerchantOrderLock.acquire()){
												   listKey = CacheConstants.MERCHANT_PUSH_ORDER_IDS+key;
										 		   do{
										 			   String orderId= jedis.lpop(listKey);
										 			   if (orderId==null) { //已全部清空
										 				   break;
										 			   }
										 			   String cachedInfo=jedis.hget(key, orderId);
										 			   Map<String,Object> cachedPushOrder = JSON.parseObject(cachedInfo,type.getClass());
										 			   Timestamp jointime = (Timestamp) cachedPushOrder.get("joinTime");
										 			   if (isNeedRemove(DateUtil.getDate(jointime),today)){
										 				    jedis.hdel(key, orderId);
										 			   }else{
										 				   	//该清理的已清理，压回弹出的订单,跳出本商户的清理
										 				    jedis.lpush(listKey, orderId);
										 				    break;
										 			   }
										 		   }while(true);
										 		   jedis.sync();
									}
							}catch(Exception e){
								log.error("商户订单清理调度失败，商户ID->"+key+" "+e.getMessage(), e);
							}finally{
								specialMerchantOrderLock.release();
							}
						
					}
			}
			long endTime = System.currentTimeMillis();
			log.info(" total cost:" + (endTime-currentTime)/1000);
			log.info(" 商户订单清理成功 ");
		}catch(Exception e){
			log.error("商户订单清理失败  "+e.getMessage(), e);
		}finally{
			scheduleLock.release();
			JedisPoolUtil.returnRes(jedis);
		}
	}
	
	/**
	 * 从商户缓存中删除某个订单
	 * @param merchantID
	 * @param orderId
	 */
	public boolean  delOrderFromMerchantCache(String merchantID,String orderId){
		Jedis jedis = null;
		String key =  CacheConstants.MERCHANT_PUSH_ORDER_PREFIX	+ merchantID;
		String lkey = CacheConstants.MERCHANT_PUSH_ORDER_IDS +merchantID;
		String lockName = CacheConstants.LOCK_PUSH_MERCHANT_ORDER_PREFIX + merchantID;
		try {
			jedis = JedisPoolUtil.getJedis();
			JedisLock lock = new JedisLock(jedis, lockName, 10000, 20000);
			if (lock.acquire()) {
					if (jedis.hexists(key, orderId)){
							jedis.hdel(key, orderId);
							jedis.lrem(lkey, 1, orderId);
					}
			}
			lock.release();
			return true;
		}catch(Exception e){
			log.error("删除商户订单失败： 商户->"+merchantID+",订单号->"+orderId+" "+e.getMessage(), e);
		}finally {
			JedisPoolUtil.returnRes(jedis);
		}
		
		return false;
	}
	
	
	private  boolean isNeedRemove(String startDate,String endDate) {
		 return DateUtil.getDaysBetweenDAY1andDAY2(startDate,endDate)>CacheConstants.KEEP_MERCHANT_PUSH_ORDER_PERIOD;
	}
	
	
	public static void main(String[] args){
			new MerchantCacheServiceImpl().getCachedMerchantPushOrders("146407294733696702");
	}

	/**
	 * 从商户缓存中查询商户认证类型
	 *
	 * @param merchantId 商户ID
	 * @return 0 - 不是认证商户；1 - 企业认证；2 - 个人认证
     */
	public String getAuthTypeFromMerchantCache(Long merchantId) {
		Object stringAuthType = get(CacheConstants.MERCHANT_AUTH_TYPE + merchantId);
		return null == stringAuthType ? "0" : String.valueOf(stringAuthType);
	}

	/**
	 * 在缓存中添加当前商户的的授权类型
	 *
	 * @param merchantId 商户ID
	 * @param authType 1 - 企业认证；2 - 个人认证
     */
	public void setAuthTypeFromMerchantCache(Long merchantId, String authType) {
		set(CacheConstants.MERCHANT_AUTH_TYPE + merchantId, CacheConstants.MERCHANT_AUTH_TYPE_EXPIRE, authType);
	}

}
