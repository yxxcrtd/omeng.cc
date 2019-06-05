package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 黑名单
 */
public interface IBlackListDao {

	// 验证是否为黑名单
	public int checkBlackList(Map<String, Object> paramMap);

    List<Map<String, Object>> findAllBlackList();

}
