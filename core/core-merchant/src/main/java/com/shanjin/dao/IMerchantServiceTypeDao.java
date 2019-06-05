package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户关联服务类型表Dao
 */
public interface IMerchantServiceTypeDao {

	/** 查询商户服务项目名 */
	List<String> selectMerchantServiceTypeName(Map<String, Object> paramMap);
	
	String getMerchantServiceTypeNames(Map<String, Object> paramMap);

	/** 查询商户在当前应用程序中所能提供的服务项目 */
	List<Map<String, Object>> selectMerchantService(Map<String, Object> paramMap);

	/** 查询编辑商户已提供和未提供的服务项目 */
	List<Map<String, Object>> selectMerchantServiceForChoose(Map<String, Object> paramMap);

	/** 查询编辑商户已提供和未提供的服务项目 */
	List<Map<String, Object>> selectCategoryMerchantService(Map<String, Object> paramMap);

	/** 查询编辑商户已提供和未提供的服务项目 
	List<Map<String, Object>> selectPersonalMerchantService(Map<String, Object> paramMap);*/

	/** 查询编辑商户已提供和未提供的服务项目 */
	List<Map<String, Object>> selectMerchantServiceForChooseMultilevel(Map<String, Object> paramMap);

	/** 商户服务项目的保存 */
	int insertMerchantServiceType(List<Map<String, Object>> merchantServiceTypeList);

	/** 查询单个商户服务数量 */
	int selectMerchantServiceNum(Map<String, Object> paramMap);

	/** 删除商户服务项目 */
	int deleteMerchantServiceType(Map<String, Object> paramMap);

	/** 删除商户服务项目 */
	int deleteByServiceTypeId(Map<String, Object> paramMap);

	/** 将原来的serviceType转换成自增Id */
	String serviceTypeConvertToId(Map<String, Object> paramMap);

	/** 查询单个商户的服务id */
	List<String> selectServiceTypeId(Map<String, Object> paramMap);

	/** 商户添加的服务个数 */
	int personApplyServiceNum(Map<String, Object> paramMap);

	/** 验证商户添加的服务是否已经在service_type表里存在 */
	int checkApplyServiceInServiceType(Map<String, Object> paramMap);

	/** 查询商户申请的服务的信息 */
	Map<String, Object> selectApplyServiceInfo(Map<String, Object> paramMap);

	/** 个人申请的服务的保存 */
	int personApplyServiceSave(Map<String, Object> paramMap);

	/** 个人申请的服务的查询 */
	List<Map<String, Object>> personApplyServiceQuery(Map<String, Object> paramMap);

	/** 个人申请的服务的删除*/
	int personApplyServiceDelete(Map<String, Object> paramMap);
}
