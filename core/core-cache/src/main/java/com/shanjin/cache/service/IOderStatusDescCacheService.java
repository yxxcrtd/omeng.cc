package com.shanjin.cache.service;

import java.util.List;
import java.util.Map;

public interface IOderStatusDescCacheService {
	
	/**
	 * 缓存订单状态描述列表
	 * @param describeInfo
	 */
	public void cacheOrderStatusDescribes(List<Map<String,Object>> describeInfo);
}
