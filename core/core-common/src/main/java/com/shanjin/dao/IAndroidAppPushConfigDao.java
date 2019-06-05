package com.shanjin.dao;

import java.util.Map;

/**
 * Android应用推送配置Dao
 */
public interface IAndroidAppPushConfigDao {
	// 获得Android APP的推送设置
	Map<String, Object> getPushConfig(Map<String, Object> paramMap);
}
