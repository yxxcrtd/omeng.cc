package com.shanjin.service.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderApiService {
	/**
	 * 保存订单信息
	 * 
	 * @param orderInfo
	 * @return
	 */
	public int saveAppOrder(Map<String, Object> orderInfo);

	/** 用户端订单详情业务信息 */
	public Map<String, Object> selectAppOrderText(Map<String, Object> paramMap)throws Exception;

	/** 商户端订单详情业务信息 */
	public Map<String, Object> selectOrderText(Map<String, Object> paramMap)throws Exception;

	/** 获取所需要的商户clentId */
	List<Map<String, Object>> getMerchantClientIds(Map<String, Object> paramMap);
	
	/** 获取经纬度*/
	public Map<String, Object> selectOrderLocation(Map<String, Object> paramMap);	
	
	/**根据订单ID查找订单地址信息 */
	Map<String, Object> getProvinceAndCityByorderId(Long orderId);
	
	

	/**
	 * 从请求串中服务时间模板方法----子类根据需要改写
	 * @return
	 */
	public String  extractServiceTime( Map<String, Object> requestInfo);
	
	
	/**
	 * 格式化显示时间---这里显示默认的格式 -----子类可根据需要改写
	 * @param serviceTime
	 * @param appType
	 * @return
	 */
	public String  formatServiceTime(Date  serviceTime,String appType,String serviceType);
	
}
