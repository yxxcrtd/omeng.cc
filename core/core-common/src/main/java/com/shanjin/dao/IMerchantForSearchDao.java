package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IMerchantForSearchDao {

	/**
	 * 获取所有有效的商店数量
	 * @return
	 */
	public  int getTotalMerchant();
	
	
	/**
	 * 返回一页的商户信息
	 * @param pageNo
	 * @return
	 */
	public List<Map<String, Object>> getMerchantInPage(Map<String,Object> param);
	
	
	/**
	 * 按id 来获取商户的概要信息
	 * @param param
	 * @return
	 */
	public Map<String,Object> getMerchantOutLineById(Map<String,Object> param);
	
	
	/**
	 * 查询某商户的的所有服务id
	 * @param merchantId
	 * @return
	 */
	public String getMerchantServiceTypeIds(String merchantId);
	
	/**
	 * 根据商户的IDS 查询要推动的到的商户雇员
	 * @param merchantIds
	 * @return
	 */
	public List<Map<String,Object>> getUserIdsByMerchantIds(String merchantIds);
	
	
	/**
	 * 根据城市查询该地区的商户数量
	 * @param city
	 * @return
	 */
	public Integer getMerchantQualityByCity(Map<String,String> param);

}
