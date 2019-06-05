package com.shanjin.dao;

import java.util.List;
import java.util.Map;


/**
 * 增值服务Dao
 * @author Huang yulai
 *
 */
public interface IValueAddedDao {
	
	/**
	 * 根据服务大类ID获取包列表
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getPackageListByServiceId(Map<String, Object> paramMap);

	/**
	 * 根据服务大类ID和商户ID查询服务包详细信息
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getPackageDetail(Map<String, Object> paramMap);
	
	/**
	 * 根据服务大类ID 获取 服务Key
	 * @return
	 */
	public String getServiceKeyById(Map<String, Object> paramMap);
	
	/**
	 * 获取所有增值服务列表
	 * @return
	 */
	List<Map<String, Object>> getValueAddServiceList();

	/**
	 * 获取商户购买的增值服务包
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> getMerchantServiceList(Map<String, Object> paramMap);

	/**
	 * 获取商户的员工数和
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> getMerchantInfo(Map<String, Object> paramMap);

	int getMerchantBuyDetail(Map<String, Object> paramMap);

	public Map<String, Object> getMerchantDetail(Map<String, Object> paramMap);

}
