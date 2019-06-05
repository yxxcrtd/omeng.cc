package com.shanjin.cache.service;

import java.util.List;
import java.util.Map;

public interface IMerchantCacheService {
	/**
	 * 绑定银行卡时手机验证码缓存
	 */
	 public String createVerificationCodeCache(Long merchantId,String info);

	 /**
	 * 获取绑定银行卡时手机验证码缓存
	 */
	 public String getVerificationCodeCache(Long merchantId);
	 
	 /**
	  * 将某商户近期的推送订单加载到缓存中
	  * @param pushOrderInfos
	  * @param merchantID
	  */
	 public void cachePushOrderForSpecialMerchant(List<Map<String,Object>> pushOrderInfos,String merchantID);
	 
	 
	 
	 /**
	  * 根据某次推送订单列表更新商户侧 订单缓存
	  * @param pushOrderInfos
	  * @param orderId
	  */
	 public void cachePushOrderForAllMerchants(List<Map<String,Object>> pushOrderInfos,String orderId);
	 
	 
	 
	 /**
	  * 更新某商户某条订单缓存
	  * @param pushOrderInfo
	  * @param merchantID
	  * @param orderID
	  */
	 public void updateMerchantCachedOrder(Map<String,Object> pushOrderInfo,String merchantID,String orderID);


	 /**
	  * 更新特定商户中的某条订单缓存到指定状态，如果近期缓存存在，则更新之，否则更新历史缓存。 都没命中，跳过。
	  * @param merchantID
	  * @param status
	 */
	 public void updateMerchantOrderCache(String merchantID,String orderID,String status);
	 

	 /**
	  * 更新特定商户中的某条订单缓存，如果近期缓存存在，则更新之，否则更新历史缓存。 都没命中，跳过。
	  * @param merchantID
	  * @param status
	 */
	public void updateMerchantOrderCache(String merchantID,String orderID,Map<String,Object> upOrderInfo);
	
	
	 /**
	  * 获取某商户的缓存订单列表
	  * @param merchantID
	  */
	 public List<Map<String,Object>> getCachedMerchantPushOrders(String merchantID);
	 
	 
	 
	 /**
	  * 获取某商户缓存订单列表中的某条订单
	  * @param merchantID
	  * @param orderID
	  */
	public  Map<String,Object> getCachedMerchantPushOrder(String merchantID,String orderID);
	
	
	/**
	 * 批更新某订单对应的商户侧订单状态
	 * @param orderId
	 * @param merchantIds
	 * @param status
	 */
	public void batchUpdateMerchantOrderStatus(String orderId,List<Map<String,Object>> merchantIds,String status);

	
	/**
	 * 从商户缓存中删除某个订单
	 * @param merchantID
	 * @param orderId
	 */
	public boolean  delOrderFromMerchantCache(String merchantID,String orderId);
	
	/**
	 * 清理商户订单推送缓存
	 */
	public void cleanCache();

	/**
	 * 从商户缓存中查询商户认证类型
	 *
	 * @param merchantId
	 * @return
     */
	String getAuthTypeFromMerchantCache(Long merchantId);

    /**
     * 在缓存中添加当前商户的的授权类型
     *
     * @param merchantId 商户ID
     * @param authType 1 - 企业认证；2 - 个人认证
     */
	void setAuthTypeFromMerchantCache(Long merchantId, String authType);

}
