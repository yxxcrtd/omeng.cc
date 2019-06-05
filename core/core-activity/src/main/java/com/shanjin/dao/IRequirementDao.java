package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IRequirementDao {

    /**
     * 获取需求活动服务项目列表
     * @param paramMap
     * @return
     */
    List<Map<String,Object>> getRequireServiceList(Map<String, Object> paramMap);
    
    /**
     * 获取需求活动基本信息
     * @param paramMap
     * @return
     */
    Map<String, Object> getRequireActivity(Map<String, Object> paramMap); 
	
}
