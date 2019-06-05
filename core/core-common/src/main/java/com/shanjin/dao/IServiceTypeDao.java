package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 服务类型Dao
 */
public interface IServiceTypeDao {
	
    // 获得一级服务类型
    List<Map<String, Object>> getParentServiceType(Map<String, Object> paramMap);
    
    // 获得子服务类型
    List<Map<String, Object>> getChildServiceType(Map<String, Object> paramMap);
    
    List<Map<String, Object>> getAllServiceType();
    
    // 获得易学堂服务类型
    List<Map<String, Object>> getYxtServiceType(Map<String, Object> paramMap);
    
    
    //获取艾秘书服务类型列表
    List<Map<String,Object>> getAmsServiceType(String appType);

	/** 获得订单的服务类型 */
	public Long selectOrderServiceType(Map<String, Object> paramMap);
	
	public List<Map<String,Object>> getServiceShowIconImg();
	
	
	/**
	 * 按 搜索引擎 返回的appType及serviceType　查找对应的服务id
	 * @param paramMap
	 * @return
	 */
	public Long getServiceByIsearch(Map<String,Object> paramMap);

	public Map<String,Object> getCatalogByAlias(String alias);
	
	public List<Map<String,Object>> getCatalogByParentId(Long parentId);
}
