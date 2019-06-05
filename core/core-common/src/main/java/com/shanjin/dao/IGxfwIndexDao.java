package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 个性服务商户标签DAO
 */
public interface IGxfwIndexDao {

	// 全部商家的服务标签
	public List<Map<String, Object>> getAllMerchantTags();

	// 获得个性服务的订单标题
	public String getOrderTitle(Map<String, Object> paramMap);

	/** 查询推荐的的服务标签 */
	List<Map<String, Object>> selectRecommendServiceTag();

	// 根据ID获得服务标签
	public Map<String, Object> getAllMerchantTagsById(Map<String, Object> paramMap);
}
