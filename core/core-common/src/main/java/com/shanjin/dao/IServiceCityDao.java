package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IServiceCityDao {

	/** 检查服务城市是否开通 */
	int checkServiceCity(Map<String, Object> paramMap);
	
	List<Map<String, Object>> getAllServiceCity();
}
