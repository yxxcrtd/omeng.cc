package com.shanjin.dao;

import java.util.Map;

/**
 * 设备信息Dao
 */
public interface IDeviceDao {

	/** 验证这个设备是否记录 */
	public Integer checkDeviceInfo(Map<String, Object> paramMap);

	/** 记录设备信息 */
	public Integer recordDeviceInfo(Map<String, Object> paramMap);
}
