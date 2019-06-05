package com.shanjin.cache.service;

import java.util.List;
import java.util.Map;

import com.shanjin.cache.service.impl.JedisLock;



public interface IUserRelatedCacheServices {
	/**
	 * 缓存用户预验证信息
	 * @param key
	 * @param info
	 * @return
	 */
	public String cacheVerifyInfo(String key,Map<String,String> info);
	
	
	/**
	 * 获取用于预验证信息
	 * @param key
	 * @return
	 */
	public Map<String,String> getVerifyInfo(String key);
	
	
	
	/**
	 * 根据电话读取用户ID
	 * @param key
	 * @return
	 */
	public  String   getUserIdByPhone(String key);
	
	
	/**
	 * 缓存电话--用户对应关系。
	 * @param phone
	 * @param userID
	 * @return
	 */
	public  long   cachePhoneUserRelation(String phone,String userID);
	
	
	
	/**
	 * 缓存用户基本信息
	 * @param userInfo
	 * @return
	 */
	public  void   cacheUserInfo(Map<String,String> userInfo);
	
	
	/**
	 * 获取缓存的用户信息。
	 * @param userId
	 * @return
	 */
	public Map<String,String> getCachedUseInfo(String userId);
	
	
	/**
	 * 更新TOP 用户入口
	 * @param userId
	 * @return
	 */
	public  long	 updateTopUser(String userId);
	
	
	/**
	 * 缓存用户订单入口
	 * @param orderID
	 * @return
	 */
	public  long 	 cacheUserOrderEntry(String userID,String orderID);
	
	
	
	/**
	 * 
	 * @param userID
	 * @return
	 */
	public  long 	 getUserOrderEntrySize(String userID);
	
	
	
	/**
	 * 以JSON串形式缓存用户订单
	 * @param userOrder
	 * @return
	 */
	public  long 	 cacheUserOrderWithJson(String userId,Map<String,Object> userOrder);
	
	
	/**
	 * 返回缓存的用户订单
	 * @param userid
	 * @param field
	 * @return
	 */
	public Map<String,Object> getCachedUserOrder(String userid,String field);


	/**
	 * 以JSON串形式缓存用户订单列表，同时写订单缓存入口
	 * @param cachableResult
	 */
	public void cacheUserOrders(List<Map<String, Object>> orders,String userId);


	/**
	 * 返回换从的用户订单列表
	 * @param string
	 * @return
	 */
	public List<Map<String, Object>> getCachedUserOrders(String userId);
	
	
	
	/**
	 * 根据订单ID,查找所属用户
	 * @param orderId
	 * @param userId
	 * @return
	 */
	public long  cacheOrderToUserRelation(String orderId,String userId);
	
	
	/**
	 * 从缓存中获取用户订单
	 * @param orderId
	 * @return
	 */
	public Map<String,Object>  getCachedUserOrder(String orderId);
	
	
	/**
	 * 从缓存中删除用户订单
	 * @param orderId
	 */
	public  void  rmUserOrderCache(String orderId);

	/**
	 * 在缓存中增加用户订单
	 * @param orderId
	 */
	public void   addUserOrderCache(Map<String,Object> order,String userID,String orderId);
	
	
	/**
	 * 清理TOP N 外用户的缓存
	 */
	public void   cleanUserCache();
	
	
	/**
	 * 如果缓存中存在列表里的订单，则更新其状态为取消
	 * @param orderIds
	 */
	public void cancelOrderInCache(List<Map<String,String>> orderIds);
	
	/**
	 * 获取订单过期---未选择报价方案处理定时调度锁
	 * @return
	 */
	public JedisLock getOrderNoChoosedExireLock();
	
	/**
	 * 获取订单过期---无报价方案处理定时调度锁
	 * @return
	 */
	public JedisLock getOrderNoBidExireLock();
	
	/**
	 * 好评奖励
	 * @return
	 */
	public JedisLock getEvaluationOrderLock();
	
	
	/**
	 * 获取无报价方案的过期订单--推送记录及商户侧缓存清理锁
	 * @return
	 */
	public JedisLock getPurifyNoBidExireLock();
	
	/**
	 * 获取未选定报价方案的过期订单--推送记录及商户侧缓存清理锁
	 * @return
	 */
	public JedisLock getPurifyNoChoosedExireLock();
	
	
	/**
	 * 获取用户取消的订单--推送记录及商户侧缓存清理锁
	 * @return
	 */
	public JedisLock getPurifyCancelOrderLock();
	
	
	
	/**
	 * 获取进行中的订单--推送记录及商户侧缓存清理锁
	 * @return
	 */
	public JedisLock getPurifyInProcessOrderLock();
	
	
	
	/**
	 * 获取过期报价方案--返回抢单金业务处理锁
	 * @return
	 */
	public JedisLock getReturnBidFeeLock();
	
	
	/**
	 * 释放获取订单定时调度锁
	 */
	public void ReleaseOrderExireLock(JedisLock lock);
	
	
	/**
	 * 清理指定用户的订单缓存。
	 * @param userId
	 * @return 
	 */
	public Boolean removeUserCachedOrders(String userId);
	
	
	/**
	 * 获取指定用户的订单缓存对应的版本，如果不存在则返回-1
	 * @param userId
	 * @return
	 */
	public Integer  getVersionOfOrderCache(String userId);
	
	
	
	/**
	 * 登记用户订单列表对应的图片版本
	 * @param userId
	 * @param version
	 */
	public void   regOrderCacheVersion(String userId,int version);



    /**
     * 从缓存中获取订单状态和商户订单状态
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return
     */
    Map<String, Object> getOrderStatusAndMerchantOrderStatus(String orderId, String userId);

    /**
     * 在缓存中添加订单状态和商户订单状态
     *
     * @param orderInfo 存储：订单状态和商户订单状态
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void addOrderStatusAndMerchantOrderStatus(Map<String, Object> orderInfo, String userId, String orderId);


    /** 添加处理未发送的MQ锁 */
    JedisLock getDealUnSentMQExireLock();

    /** 释放处理未发送的MQ锁 */
    void releaseDealUnSentMQrExireLock(JedisLock lock);

}
