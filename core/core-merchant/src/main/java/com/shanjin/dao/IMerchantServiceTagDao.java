package com.shanjin.dao;

import java.util.List;
import java.util.Map;

/**
 * 商户关联服务类型表Dao
 */
public interface IMerchantServiceTagDao {

	/** 商户服务标签的保存 */
	int insertMerchantServiceTag(Map<String, Object> paramMap);

	/** 选择推荐的服务标签保存 */
	int chooseServiceTagSave(Map<String, Object> paramMap);

	/** 商户服务标签的删除 */
	int deleteMerchantServiceTag(Map<String, Object> paramMap);

	/** 验证商户服务标签是否存在 */
	int checkMerchantServiceTag(Map<String, Object> paramMap);

	/** 验证商户添加的服务标签个数 */
	int checkAddMerchantServiceTag(Map<String, Object> paramMap);

	/** 验证商户服务标签是否在审核 */
	int checkAuditMerchantServiceTag(Map<String, Object> paramMap);

	/** 查询商户的服务标签 */
	List<Map<String, Object>> selectMerchantServiceTag(Map<String, Object> paramMap);

	/** 查询商户的服务标签ID */
	List<Map<String, Object>> selectMerchantServiceTagId(Map<String, Object> paramMap);

	/** 检查商户服务标签是否已经添加为待审核关键词 2016年1月9日 */
	int checkServiceTagForKeyword(Map<String, Object> paramMap);

	/** 添加商户服务标签为待审核关键词 2016年1月9日 */
	int insertServiceTagForKeyword(Map<String, Object> paramMap);
}
