package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IManagerHotRecommendDao {
	
	//获得用户首页行业推荐
    List<Map<String, Object>> getUserHomeCommend(Map<String, Object> paramMap);
    
    
    //获取用户首页个性推荐
    List<Map<String, Object>> getUserHomePersonCommend(Map<String, Object> paramMap);
    
    
    //获取热门搜素
    List<Map<String,Object>> getHotSearch();
    
    
    //获取全部服务信息
    List<Map<String,Object>> getAllServiceInfo();
    
    
    
    //获取第三方应用
    List<Map<String,Object>>    getExternalInfos();
    
    //获取用户热门搜索的图标资源
    List<Map<String,Object>> getAllHotIcons();
    
    
    //获取自定义服务------ 根据分类-服务配置表 判断是否为个性服务。
    List<Map<String,Object>>  getCustomServices();
    
    
    //获取自定义服务中指定范围内的服务信息
    List<Map<String,Object>>  getCustomServicesByRange(Map<String,Object> paramMap);
    
    
    //根据appTypes 获取 行业信息
    List<Map<String,Object>>  getAppInfoByAppType(Map<String,Object> paramMap);
    
    //根据叶子级分类，获取服务id及服务名称
    List<Map<String,Object>>  getServiceInfoByCatalogIds(Map<String,Object> paramMap);

 
    
    
}
