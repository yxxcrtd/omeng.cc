package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface ILoadingDao {
	// 查询所有发布的客户端启动页列表
	List<Map<String, Object>> getLoadingList();

	/** 根据appType查询推送信息 **/
	public Map<String, Object> getPushInfo(String appType);

	/** 查找所有商家 */
	public List<Map<String, Object>> getAllMerchant(Map<String, Object> paramMap);
    
	/** 根据城市得到所有商家 */
	public List<Map<String, Object>> getAllMerchantByCity(Map<String, Object> paramMap);

	/** 根据经纬度得到所有商家 */
	public List<Map<String, Object>> getAllMerchantByRange(Map<String, Object> paramMap);
	
	/** 获得在线的商户*/
	public List<Map<String, Object>> getOnlineMerchant(List<Map<String, Object>>  allMerchantList);

	/** 根据服务标签得到商家ClientId */
	public List<Map<String, Object>> getClientIdsByServiceTag(Map<String, Object> paramMap);
}
