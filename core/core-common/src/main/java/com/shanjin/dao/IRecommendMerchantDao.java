/**	
 * <br>
 * Copyright 2014 om.All rights reserved.<br>
 * <br>			 
 * Package: com.shanjin.dao <br>
 * FileName: IRecommendMerchant.java <br>
 * <br>
 * @version
 * @author Liuxingwen
 * @created 2016年10月29日
 * @last Modified 
 * @history
 */
package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 *  {首页推荐服务商}
 *  @author Liuxingwen
 *  @created 2016年10月29日 上午10:34:20
 *  @lastModified       
 *  @history           
 */
public interface IRecommendMerchantDao {

	/**
	 * 
	 *  ｛首页推荐服务商查询，根据省份和城市｝
	 *  @param parMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月29日 上午10:36:10
	 *  @lastModified       
	 *  @history
	 */
	public List<Map<String, Object>> getRecommendMerchantListByPC(Map<String, Object> parMap) throws Exception;
	
	public int getRecommendMerchantListByPCount(Map<String, Object> parMap) throws Exception;
	
	public List<Map<String, Object>> recommendMerchantList(Map<String, Object> parMap) throws Exception;
	
	public List<Map<String, Object>> getRecommendAppList(Map<String, Object> parMap) throws Exception;
	
}
