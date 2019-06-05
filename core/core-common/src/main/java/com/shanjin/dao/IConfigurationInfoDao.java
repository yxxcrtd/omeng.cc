package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IConfigurationInfoDao {
	/**
	 * 获取配置信息
	 * @return    
	 * @throws
	 */
	public List<Map<String,Object>> getConfigurationInfo();
}
