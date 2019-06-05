package com.shanjin.dao;

import java.util.Map;

/**
 * 操纵应用配置Dao
 */
public interface IAppConfigDao {
    //获得秘钥
    Map<String, Object> getAuthKey(Map<String, Object> paramMap);
}
