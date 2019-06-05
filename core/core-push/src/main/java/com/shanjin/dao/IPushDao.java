package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IPushDao {
	// 查询所有发布的客户端启动页列表
	List<Map<String, Object>> getLoadingList();

	/** 根据appType查询推送信息 **/
	public List<Map<String, Object>> getPushRange();

	/** 查找所有商家 */
	public List<Map<String, Object>> getAllMerchant(Map<String, Object> paramMap);
	    
	/** 根据城市得到所有商家 */
	public List<Map<String, Object>> getAllMerchantByCity(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getAllVIPMerchantByCity(Map<String, Object> paramMap);

	/** 根据经纬度得到所有商家 */
	public List<Map<String, Object>> getAllMerchantByRange(Map<String, Object> paramMap);
		
	/** 获得在线的商户*/
	public List<Map<String, Object>> getOnlineMerchant(List<Map<String, Object>>  allMerchantList);

	/** 根据服务标签得到商家ClientId */
	public List<Map<String, Object>> getClientIdsByServiceTag(Map<String, Object> paramMap);
	
	/**插入商户订单表**/
	public int insertPushMerchantOrder(Map<String, Object> paramMap);
	
	/**查询即将推送的商户的clientId**/
	List<Map<String, Object>> getMerchantClientId(Map<String, Object> paramMap);
	/**查询即将推送的商户的clientId**/
	List<Map<String, Object>> getUserClientId(Map<String, Object> paramMap);
	
	/** 获得个性服务的订单标题**/
	public String getOrderTitle(Map<String, Object> paramMap);
	/** 获得订单的定位信息 */
	public Map<String, Object> selectOrderLocation(Long orderId);
	/** 获得订单的定位信息 */
	public Map<String, Object> selectCbtOrderLocation(Map<String, Object> paramMap);
	/**获取订单省市信息**/
	Map<String, Object> getProvinceAndCityByorderId(Long orderId);
	/** 根据用户ID获取用户推送设备列表 Long userId*/
	List<Map<String, Object>> selectUserPushList(Map<String, Object> paramMap);
	/**查询需要推送的服务类型**/
	List<Map<String, Object>> getPushServiceTypeIdList();
	
	String getAppTypeByMerchantId(Long merchantId);
	String getAliasByOrderId(Long orderId);
	/**
	 * 获取配置信息
	 * @return    
	 * @throws
	 */
	public List<Map<String,Object>> getConfigurationInfo();
	
	int getUserPushCountByUserId(Map<String, Object> paramMap);
	/** 删除用户设备的记录  Long userId*/
	int deleteUserPushByUserId(Map<String, Object> paramMap);
	
	int deleteUserPushByUserIdOrClientId(Map<String, Object> paramMap);
	
	int insertUserPush(Map<String, Object> paramMap);
	
	int updateUserPush(Map<String, Object> paramMap);
	
	List<Map<String, Object>> getMerchantLastLoginList(Map<String, Object> paramMap);
	
	List<Map<String, Object>> getMerchantActiveList(Map<String, Object> paramMap);
	
	String getMerchantIdsByPhone(Map<String, Object> paramMap);
	
	List<Map<String, Object>> getCityPrivateAssistants(Map<String, Object> paramMap);
	
	int getMerchantOrderCount(Map<String, Object> paramMap);
	
	public List<Map<String,Object>> getSendList(Long userId);
	
	public int addOrderSendSmsInfo(Map<String, Object> paramMap);
	
	
	/**
	 * 获取私人助理的推送信息
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> getAssistantPushInfo(Map<String,Object> param);
}
