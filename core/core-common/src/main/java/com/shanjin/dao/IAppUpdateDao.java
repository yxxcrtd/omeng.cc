package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 操纵应用更新配置Dao
 */
public interface IAppUpdateDao {
    //判断是否有更新
    Map<String, Object> checkUpdate(Map<String, Object> paramMap);
    
    // 查询所有发布的客户端版本列表
    List<Map<String,Object>> getPubClientVersionList();
}
