package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 应用信息Dao
 */
public interface IAppInfoDao {
    // 获得应用信息
    List<Map<String, Object>> getAPPList();
}
