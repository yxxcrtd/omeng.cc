package com.shanjin.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.GenericCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

/**
 * 与用户相关的缓存服务
 * 
 * @author 俞成
 *
 */
@Service("userRelatedCacheServices")
public class UserRelatedCacheServices extends GenericCacheServiceImpl implements
		GenericCacheService, IUserRelatedCacheServices {
	Log log = LogFactory.getLog(UserRelatedCacheServices.class);

	private static Map<String, Object> type = new HashMap<String, Object>();

	public String cacheVerifyInfo(String key, Map<String, String> info) {
		String result = hmset(CacheConstants.USER_PRE_VERIFY_KEY + key, info);
		setExpire(CacheConstants.USER_PRE_VERIFY_KEY + key,
				CacheConstants.USER_PRE_VERIFY_TIMEOUT);

		return result;
	}

	public Map<String, String> getVerifyInfo(String key) {
		return hmget(CacheConstants.USER_PRE_VERIFY_KEY + key);
	}

	@Override
	public String getUserIdByPhone(String phone) {
		return hget(CacheConstants.PHONE_USER_KEY, phone);
	}

	@Override
	public long cachePhoneUserRelation(String phone, String userID) {
		return hset(CacheConstants.PHONE_USER_KEY, phone, userID);
	}

	@Override
	public void cacheUserInfo(Map<String, String> userInfo) {
		// 清除TOP N 以后用户相关的缓存信息 在定时调度中完成。
		String userId = userInfo.get("id");
		String phone = userInfo.get("phone");
		Jedis jedis = JedisPoolUtil.getJedis();
		Pipeline pipeLine = jedis.pipelined();
		pipeLine.hset(CacheConstants.PHONE_USER_KEY, phone, userId);
		pipeLine.zadd(CacheConstants.TOP_USER_KEY,
				(double) System.currentTimeMillis(), userId);
		pipeLine.hmset(CacheConstants.TOP_USER_INFO_KEY + userId, userInfo);
		pipeLine.sync();
		JedisPoolUtil.returnRes(jedis);
	}

	@Override
	public long updateTopUser(String userId) {
		return zadd(CacheConstants.TOP_USER_KEY,
				(double) System.currentTimeMillis(), userId);
	}

	@Override
	public Map<String, String> getCachedUseInfo(String userId) {
		return hmget(CacheConstants.TOP_USER_INFO_KEY + userId);
	}

	@Override
	public long cacheUserOrderEntry(String userID, String orderID) {
		return zadd(CacheConstants.ORDER_PER_USER_ENTRY_KEY + userID,
				(double) System.currentTimeMillis(), orderID);
	}

	public long cacheUserOrderWithJson(String userID,
			Map<String, Object> userOrder) {
		return hset(CacheConstants.ORDER_USER_KEY + userID,
				(String) userOrder.get("orderId"), JSON.toJSONString(userOrder));
	}

	@Override
	public long getUserOrderEntrySize(String userID) {
		return zcard(CacheConstants.ORDER_PER_USER_ENTRY_KEY + userID);
	}

	@Override
	public Map<String, Object> getCachedUserOrder(String userid, String orderId) {
		String value = hget(CacheConstants.ORDER_USER_KEY + userid, orderId);
		if (value == null || value.equals(""))
			return null;
		else
			return JSON.parseObject(value, type.getClass());
	}

	@Override
	public void cacheUserOrders(List<Map<String, Object>> orders, String userId) {
		if (orders == null)
			return;

		Jedis jedis = JedisPoolUtil.getJedis();
		String lockName = CacheConstants.LOCK_ORDER_USER_PREFIX + userId;
		try {
			JedisLock lock = new JedisLock(jedis, lockName, 10000, 10000);
			if (lock.acquire()) {
				Pipeline pipeLine = jedis.pipelined();
				String userOrderEntryKey = CacheConstants.ORDER_PER_USER_ENTRY_KEY
						+ userId;
				String useOrderKey = CacheConstants.ORDER_USER_KEY + userId;
				for (Map<String, Object> order : orders) {
					long timeStamp = StringUtil.nullToLong(order
							.get("joinTimeStamp"));
					String orderId = order.get("orderId").toString();
					pipeLine.hset(useOrderKey, orderId,
							JSON.toJSONString(order));
					pipeLine.zadd(userOrderEntryKey, (double) timeStamp,
							orderId);
					pipeLine.hset(CacheConstants.ORDER_TO_USER, orderId, userId);
				}
				pipeLine.sync();
			}
			lock.release();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
	}

	@Override
	public List<Map<String, Object>> getCachedUserOrders(String userId) {
		Map<String, String> cachedOrders = hmget(CacheConstants.ORDER_USER_KEY
				+ userId);
		if (cachedOrders == null || cachedOrders.size() < 1)
			return null;
		else {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(
					cachedOrders.size());
			for (Map.Entry<String, String> entry : cachedOrders.entrySet()) {
				result.add(JSON.parseObject(entry.getValue(), type.getClass()));
			}
			return result;
		}
	}

	@Override
	public long cacheOrderToUserRelation(String orderId, String userId) {
		return hset(CacheConstants.ORDER_TO_USER, orderId, userId);
	}

	@Override
	public Map<String, Object> getCachedUserOrder(String orderId) {
		Map<String, Object> result = null;
		Jedis jedis = JedisPoolUtil.getJedis();
		String userId = jedis.hget(CacheConstants.ORDER_TO_USER, orderId);
		if (userId != null && !userId.equals("")) {
			String value = jedis.hget(CacheConstants.ORDER_USER_KEY + userId,
					orderId);
			if (value != null || !value.equals(""))
				result = JSON.parseObject(value, type.getClass());
		}
		JedisPoolUtil.returnRes(jedis);
		return result;
	}

	@Override
	public void rmUserOrderCache(String orderId) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			String userID = jedis.hget(CacheConstants.ORDER_TO_USER, orderId);
			if (userID != null) {
				jedis.hdel(CacheConstants.ORDER_TO_USER, orderId);
				String lockName = CacheConstants.LOCK_ORDER_USER_PREFIX
						+ userID;
				JedisLock lock = new JedisLock(jedis, lockName, 10000, 1000);
				if (lock.acquire()) {
					jedis.zrem(
							CacheConstants.ORDER_PER_USER_ENTRY_KEY + userID,
							orderId);
					jedis.hdel(CacheConstants.ORDER_USER_KEY + userID, orderId);
				}
				lock.release();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
	}

	@Override
	public void addUserOrderCache(Map<String, Object> order, String userID,
			String orderId) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			String lockName = CacheConstants.LOCK_ORDER_USER_PREFIX + userID;
			JedisLock lock = new JedisLock(jedis, lockName, 10000, 1000);
			if (lock.acquire()) {
				// 将新创建的订单加入缓存
				jedis.hset(CacheConstants.ORDER_USER_KEY + userID, orderId,
						JSON.toJSONString(order));
				jedis.zadd(CacheConstants.ORDER_PER_USER_ENTRY_KEY + userID,
						System.currentTimeMillis(), orderId);
				jedis.hset(CacheConstants.ORDER_TO_USER, orderId, userID);

				// 删除TOP n 之后的记录
				Set<String> keys = jedis.zrevrange(
						CacheConstants.ORDER_PER_USER_ENTRY_KEY + userID,
						CacheConstants.ORDER_PER_USER_SIZE, -1);
				if (keys != null && keys.size() > 0) {
					Pipeline pipeLine = jedis.pipelined();
					for (String key : keys) {
						jedis.hdel(CacheConstants.ORDER_TO_USER, key);
						jedis.zrem(CacheConstants.ORDER_PER_USER_ENTRY_KEY
								+ userID, key);
						jedis.hdel(CacheConstants.ORDER_USER_KEY + userID, key);
					}
					pipeLine.sync();
				}
			}
			lock.release();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}

	}

	@Override
	public void cleanUserCache() {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			JedisLock lock = new JedisLock(jedis,
					CacheConstants.LOCK_USER_CACHE, 2 * 60 * 1000,
					2 * 60 * 1000);
			if (lock.acquire()) { // 获得用户缓存锁的线程，才能执行缓存清理工作。

				// 逐一删除排在TOP N 后的用户缓存
				Set<String> userIds = jedis.zrevrange(
						CacheConstants.TOP_USER_KEY,
						CacheConstants.TOP_USER_NUMBER, -1);
				if (userIds != null && userIds.size() > 0) {
					for (String userId : userIds) {
						String orderLockName = CacheConstants.LOCK_ORDER_USER_PREFIX
								+ userId;
						String userOrderEntryKeyName = CacheConstants.ORDER_PER_USER_ENTRY_KEY
								+ userId;
						JedisLock perOrderLock = new JedisLock(jedis,
								orderLockName, 10 * 1000, 10 * 1000);
						if (perOrderLock.acquire()) {
							Set<String> orderIds = jedis.zrange(
									userOrderEntryKeyName, 0, -1);
							String userInfoKey = CacheConstants.TOP_USER_INFO_KEY
									+ userId;
							Map<String, String> userInfo = jedis
									.hgetAll(userInfoKey);
							Pipeline pipeLine = jedis.pipelined();
							pipeLine.zrem(CacheConstants.TOP_USER_KEY, userId);
							pipeLine.hdel(CacheConstants.PHONE_USER_KEY,
									userInfo.get("phone"));
							if (orderIds != null && orderIds.size() > 0) {
								for (String orderId : orderIds) {
									pipeLine.hdel(CacheConstants.ORDER_TO_USER,
											orderId);
								}
							}
							pipeLine.del(CacheConstants.TOP_USER_INFO_KEY
									+ userId);
							pipeLine.del(CacheConstants.ORDER_PER_USER_ENTRY_KEY
									+ userId);
							pipeLine.del(CacheConstants.ORDER_USER_KEY + userId);
							pipeLine.sync();
						}
						perOrderLock.release();
					}
				}
				lock.release();
			}
		} catch (InterruptedException e) {
			log.error("用户缓存清理出错：" + e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
	}

	public static void main(String[] args) {
		UserRelatedCacheServices urSrv = new UserRelatedCacheServices();
		urSrv.cleanUserCache();
	}

	@Override
	public void cancelOrderInCache(List<Map<String, String>> orders) {
		// 传入的orders 里 key 为 orderId, value 为 userId
		Jedis jedis = JedisPoolUtil.getJedis();
		for (Map<String, String> order : orders) {
			String orderId = null, userId = null;
			for (Map.Entry<String, String> entry : order.entrySet()) {
				if (entry.getKey().equals("orderId"))
					orderId = entry.getValue();
				else
					userId = entry.getValue();
			}
			String cacheName = CacheConstants.ORDER_USER_KEY + userId;
			String orderJsonStr = jedis.hget(cacheName, orderId);
			if (orderJsonStr == null)
				continue;
			else {
				Map<String, Object> cachedOrder = JSON.parseObject(
						orderJsonStr, type.getClass());
				cachedOrder.put("orderStatus", Constant.OVERDUE_ORDER);
				jedis.hset(cacheName, orderId, JSON.toJSONString(cachedOrder));
			}

		}
		JedisPoolUtil.returnRes(jedis);
	}

	@Override
	public JedisLock getOrderNoChoosedExireLock() {
		return getLock(CacheConstants.LOCK_ORDER_NO_CHOOSED_CLEAN, 5, 300);
	}

	public JedisLock getEvaluationOrderLock() {
		return getLock(CacheConstants.LOCK_EVALUATION_ORDER, 5, 300000000);
	}

	@Override
	public void ReleaseOrderExireLock(JedisLock lock) {
		releaseLock(lock);
	}

	@Override
	public Boolean removeUserCachedOrders(String userId) {
		String userOrderEntry = CacheConstants.ORDER_PER_USER_ENTRY_KEY
				+ userId; // 用户订单入口key
		String userOrderMapKey = CacheConstants.ORDER_USER_KEY + userId; // 用户订单列表的key

		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			String orderLockName = CacheConstants.LOCK_ORDER_USER_PREFIX
					+ userId;
			JedisLock perOrderLock = new JedisLock(jedis, orderLockName,
					10 * 1000, 10 * 1000);
			if (perOrderLock.acquire()) {
				Pipeline pipeLine = jedis.pipelined();
				Set<String> orderIds = jedis.zrange(userOrderEntry, 0, -1);
				if (orderIds != null && orderIds.size() > 0) {
					pipeLine.del(userOrderEntry);
					for (String orderId : orderIds) {
						pipeLine.hdel(CacheConstants.ORDER_TO_USER, orderId);
					}
					pipeLine.del(userOrderMapKey);
					pipeLine.sync();
				}
			}
			perOrderLock.release();

		} catch (InterruptedException e) {
			log.error("用户缓存清理出错：userId=" + userId + e.getMessage(), e);
			return false;
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
		return true;
		// 1 清理用户的订单列表
		// 2.清理用户的

	}

	@Override
	public Integer getVersionOfOrderCache(String userId) {
		Jedis jedis = JedisPoolUtil.getJedis();
		String version = jedis.hget(CacheConstants.ORDER_VERSION_KEY, userId);
		JedisPoolUtil.returnRes(jedis);

		if (version == null || version.trim().equals(""))
			return -1;
		else
			return Integer.parseInt(version);

	}

	@Override
	public void regOrderCacheVersion(String userId, int version) {
		Jedis jedis = JedisPoolUtil.getJedis();
		jedis.hset(CacheConstants.ORDER_VERSION_KEY, userId, "" + version);
		JedisPoolUtil.returnRes(jedis);

	}

	@Override
	public JedisLock getOrderNoBidExireLock() {
		return getLock(CacheConstants.LOCK_ORDER_NO_BID_CLEAN, 5, 300);
	}

	/**
	 * 从缓存中获取订单状态和商户订单状态
	 *
	 * @param orderId
	 *            订单ID
	 * @param userId
	 *            用户ID
	 * @return
	 */
	@Override
	public Map<String, Object> getOrderStatusAndMerchantOrderStatus(
			String orderId, String userId) {
		Map<String, Object> result = null;
		Jedis jedis = JedisPoolUtil.getJedis();
		String value = jedis.hget(CacheConstants.USER_ORDER_STATUS_KEY
				+ orderId, userId);
		if (null != value || !"".equals(value)) {
			result = JSON.parseObject(value, type.getClass());
		}
		JedisPoolUtil.returnRes(jedis);
		return result;
	}

	/**
	 * 在缓存中添加订单状态和商户订单状态
	 *
	 * @param orderInfo
	 *            存储：订单状态和商户订单状态
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 */
	@Override
	public void addOrderStatusAndMerchantOrderStatus(
			Map<String, Object> orderInfo, String userId, String orderId) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			jedis.hset(CacheConstants.USER_ORDER_STATUS_KEY + orderId, userId,
					JSON.toJSONString(orderInfo));
			jedis.expire(CacheConstants.USER_ORDER_STATUS_KEY + orderId,
					CacheConstants.USER_ORDER_STATUS_KEY_EXPIRE);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JedisPoolUtil.returnRes(jedis);
		}
	}

	@Override
	public JedisLock getPurifyNoBidExireLock() {
		return getLock(CacheConstants.LOCK_PURIFY_NO_BID, 10, 600);
	}

	@Override
	public JedisLock getPurifyNoChoosedExireLock() {
		return getLock(CacheConstants.LOCK_PURIFY_NO_CHOOSED, 10, 600);
	}

	@Override
	public JedisLock getPurifyCancelOrderLock() {
		return getLock(CacheConstants.LOCK_PURIFY_CANCEL_ORDER, 10, 600);
	}

	@Override
	public JedisLock getPurifyInProcessOrderLock() {
		return getLock(CacheConstants.LOCK_PURIFY_INPROCESS_ORDER, 10, 600);
	}

	@Override
	public JedisLock getReturnBidFeeLock() {
		return getLock(CacheConstants.LOCK_RETURN_BIDFEE_ORDER, 10, 600);
	}

	@Override
	public JedisLock getDealUnSentMQExireLock() {
		return getLock(CacheConstants.LOCK_DEAL_UN_SENT_MQ, 1000, 60 * 1000);
	}

	@Override
	public void releaseDealUnSentMQrExireLock(JedisLock lock) {
		releaseLock(lock);
	}

}
