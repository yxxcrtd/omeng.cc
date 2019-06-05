package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 增值服务收益计算工具DAO
 * 
 * @author 李焕民
 * @version 2015年11月13日
 *
 */
public interface IValueAddedIncomeDao {

	/** 商户信息 */
	Map<String, Object> selectMerchantInfo(Map<String, Object> paramMap);

	/** 查询项目代理用户 */
	List<Map<String, Object>> getSystemUserInfo(Map<String, Object> paramMap);

	/** 查询市代用户 */
	List<Map<String, Object>> getCityUserInfo(Map<String, Object> paramMap);

	/** 查询省代用户 */
	List<Map<String, Object>> getProvinceUser(Map<String, Object> paramMap);

	/** 查询公司指定账号 */
	List<Map<String, Object>> getOmengUser(Map<String, Object> paramMap);

	/** 更新项目代理 */
	int updateSystemUserInfo(Map<String, Object> paramMap);

	/** 保存代理收益详细 */
	int saveAgent(Map<String, Object> paramMap);

}