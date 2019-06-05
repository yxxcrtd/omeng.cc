package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface ICataLogDao {
		
		 /**
		  * 获取分类表中所有已审核未删除记录
		  * @return
		  */
		 List<Map<String,Object>> getCataLogs(int parentId);
		 List<Map<String,Object>> getCataLogsForOrder(int parentId);
		 
		 
		 /**
		  * 根据叶子级分类获取对应的服务列表
		  * @param catalogIds
		  * @return
		  */
		 List<Map<String,Object>> getServiceTypeByCatalogs(Map<String,Object> catalogIds);
		 List<Map<String,Object>> getServiceTypeByCatalogsForOrder(Map<String,Object> catalogIds);
		 
		 
		 
		 
		  //获取某个分类的上级节点。
		  List<Map<String,Object>>  getCatalogById(Integer catalogId);
		  
		  
		  
		  //获取某个服务对应的分类信息
		  List<Map<String,Object>>   getCatalogByServiceId(Integer serviceId);
		    
		  
		  //根据自定义服务的id 查找自定意义服务项信息
		  List<Map<String,Object>>   getServicesInfoByServiceIds(Map<String,Object> param);
		  
		  
		  //根据appType 获取顶级分类
		  List<Map<String,Object>> getCataLogsByAppType(Map<String,Object> param);
			 

		  /**
		   * 获取用户自定义顶级分类信息
		   * @return
		   */
		  Map<String,Object>  getPersonalCatalog();
		  
		  
		  /**
		   * 获取商户入驻行业的一级分类
		   * @return
		   */
		  List<Map<String,Object>>  getMerchantatalog();
		  
		  
		  /**
		   * 获取指定页的用户顶级分类及其服务
		   * @return
		   */
		  List<Map<String,Object>>  getPersonalCatalogByPage(Map<String,Object> queryParam);
		  
		  
		  /**
		   * 按关键词搜索自定义分类及其下属服务
		   * @param keyword
		   * @return
		   */
		  List<Map<String,Object>>  getPersonalCatalogByKeywords(Map<String,Object> queryParam);
		  
		
		  /**
		   * 按关键字搜索自定义服务及其分类
		   * @param queryParam
		   * @return
		   */
		  List<Map<String,Object>>  getPersonalServiceAndCatalogByKeywords(Map<String,Object> queryParam);

		  
		  /**
		   * 获取个性服务二级分类的数量
		   * @return
		   */
		  Long  getTopCatalogNumber();
		  
		  /**
		   * 获取分类
		   * @return
		   */
		  List<Map<String,Object>>  getCatalogList(Map<String,Object> param);
		  
		  /**
		   * 获取服务
		   * @return
		   */
		  List<Map<String,Object>>  getServiceList(Map<String,Object> param);
}
