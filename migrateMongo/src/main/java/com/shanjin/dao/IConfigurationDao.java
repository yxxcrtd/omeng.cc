package com.shanjin.dao;

import java.util.Map;

/**
 * 相关配置信息的DAO 
 * @author Revoke Yu 2016.9.19
 *
 */
public interface IConfigurationDao {

	/**
	 * 获取配置表中特定配置项信息
	 * @param configKey
	 * @return
	 */
	Map<String,Object> getConfigByKey(String configKey);
}
